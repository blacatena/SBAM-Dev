package com.scholastic.sbam.server.servlets;

import java.util.List;

import com.scholastic.sbam.client.services.UrlValidationService;
import com.scholastic.sbam.server.database.codegen.AuthMethod;
import com.scholastic.sbam.server.database.objects.DbAgreementTerm;
import com.scholastic.sbam.server.database.objects.DbAuthMethod;
import com.scholastic.sbam.server.database.util.HibernateUtil;
import com.scholastic.sbam.shared.objects.AuthMethodInstance;
import com.scholastic.sbam.shared.objects.MethodIdInstance;
import com.scholastic.sbam.shared.security.SecurityManager;
import com.scholastic.sbam.shared.util.AppConstants;
import com.scholastic.sbam.shared.validation.AsyncValidationResponse;

/**
 * This class performs a validation of a referrer URL for a method instance (and agreement method or site method) to determine if there
 * are any conflicts, i.e. situations where this URL conflicts with the same URL for a different use, site, or range.
 * 
 */
@SuppressWarnings("serial")
public class UrlValidationServiceImpl extends AuthenticatedServiceServlet implements UrlValidationService {
	
	public static final int	SAME_SOURCE			=	0;		//	Both ranges belong to the same source (agreement, location, proxy)
	public static final int	SAME_SOURCE_TYPE	=	1;		//	Both ranges belong to different sources of the same type (agreement, location, proxy)
	public static final int	DIFF_SOURCE_TYPE	=	2;		//	Both ranges belong to different sources of different types (agreement, location, proxy)
	
	public static final int SAME_LOCATION		=	10;		//	Both ranges belong to the same location (i.e. site and location code)
	public static final int DIFF_LOCATION_CODE	=	20;		//	Both ranges belong to the same location (i.e. site and location code)
	public static final int DIFF_UCN_SUFFIX		=	30;		//	Both ranges belong to the same UCN, but a different location AND source
	public static final int DIFF_UCN			=	40;		//	Both ranges belong to entirely different UCNs.

	/**
	 * Perform backend validation on a field value, given a previous data instance.
	 * 
	 * The 
	 * @param ipLo
	 * The low IP address.
	 * @param ipHi
	 * The high IP address
	 * @param validationCounter
	 * A counter used to synchronize a response with the most recent validation call (all responses previous to the current client value will be ignored).
	 * @return
	 * An AsyncValidationResponse method identifying the response through the validation counter, and including any error messages.
	 * @throws Exception
	 */
	public AsyncValidationResponse validateUrl(String url, MethodIdInstance methodId, final int validationCounter) throws Exception {

		authenticate("validate referrer URL", SecurityManager.ROLE_QUERY);
		
		HibernateUtil.openSession();
		HibernateUtil.startTransaction();

		AsyncValidationResponse response = new AsyncValidationResponse(validationCounter);
		try {
			
			authenticate();

			doValidation(url, methodId, response);

		} catch (Exception exc) {
			exc.printStackTrace();
			throw exc;
		}
		
		HibernateUtil.endTransaction();
		HibernateUtil.closeSession();
		
		return response;
	}

	protected void doValidation(String url, MethodIdInstance methodId, AsyncValidationResponse response) {
		
		List<AuthMethod> authMethods  = DbAuthMethod.findByUrl(url);
		
		int otherAgreements = 0;
		if (authMethods != null) {
			for (AuthMethod authMethod : authMethods) {
				AuthMethodInstance amInstance = DbAuthMethod.getInstance(authMethod);
				//	Don't check a method against itself
				if (methodId != null && methodId.sourceEquals(amInstance.obtainMethodId())) {
					continue;
				}
				
				otherAgreements += compare(url, methodId, amInstance.getUrl(), amInstance.obtainMethodId(), response);
			}
		}
		
		//	One message counting valid assignments within other agreements
		if (otherAgreements > 0)
			response.getInfoMessages().add("This URL exists on " + otherAgreements + " other agreement" +
						(otherAgreements > 1?"s":"") + ".");
	
	}
	
	public boolean stringEquals(String first, String second) {
		if (first == null && second == null)
			return true;
		if (first == null)
			return second.length() == 0;
		if (second == null)
			return first.length() == 0;
		return first.equals(second);
	}
	
	protected int compare(String url, MethodIdInstance validatedMethodId, String compareUrl, MethodIdInstance compareMethodId, AsyncValidationResponse response) {
		
		int siteComparison		= -1;
		int sourceComparison	= -1;
		String sourceType		= "";
//		String compareType		= "";
		
		boolean isError = false;
		boolean isInfo = false;
		StringBuffer msg = new StringBuffer();
		
		if (validatedMethodId.getAgreementId() > 0)
			sourceType = "agreement";
		else if (validatedMethodId.getUcn() > 0)
			sourceType = "site location";
		else
			sourceType = "proxy";	//	Can never happen... can't have UIDs on proxies

//		if (compareMethodId.getAgreementId() > 0)
//			compareType = "agreement";
//		else if (compareMethodId.getUcn() > 0)
//			compareType = "site location";
//		else
//			compareType = "proxy";
		
		if (validatedMethodId.sourceOwnerEquals(compareMethodId) )
			sourceComparison = SAME_SOURCE;
		else {
			if (
					 (validatedMethodId.getAgreementId() != 0 && compareMethodId.getAgreementId() != 0)
				||   (validatedMethodId.getUcn()         != 0 && compareMethodId.getUcn()         != 0)
				||   (validatedMethodId.getProxyId()     != 0 && compareMethodId.getProxyId()     != 0) 
			)
				sourceComparison = SAME_SOURCE_TYPE;
			else
				sourceComparison = DIFF_SOURCE_TYPE;
		}
		
		if (validatedMethodId.getForUcn() != compareMethodId.getForUcn())
			siteComparison = DIFF_UCN;
		else if (validatedMethodId.getForUcnSuffix() != compareMethodId.getForUcnSuffix())
			siteComparison = DIFF_UCN_SUFFIX;
		else if (stringEquals(validatedMethodId.getForSiteLocCode(), validatedMethodId.getForSiteLocCode() ) )
			siteComparison = SAME_LOCATION;
		else
			siteComparison = DIFF_LOCATION_CODE;
		
		// Exact same IP for the same site location on multiple agreements generates just one message with a count...
		if (siteComparison  == SAME_LOCATION
		&&  sourceComparison== SAME_SOURCE_TYPE
		&&  validatedMethodId.getAgreementId() > 0)
			return 1;

		
		msg.append("This URL ");
		
		if (sourceComparison == SAME_SOURCE) {
				isError = true;
				msg.append(" already exists on this ");
				msg.append(sourceType);
		} else if (compareMethodId.getAgreementId() > 0) {
			List<Object []> activeTerms = DbAgreementTerm.findActive(compareMethodId.getAgreementId());
			if (activeTerms.size() == 0 ) {
				isInfo = true;
				msg.append(" exists on inactive agreement ");
			} else
				msg.append(" exists on ACTIVE agreement ");
			msg.append(AppConstants.appendCheckDigit(compareMethodId.getAgreementId()));
		} else if (compareMethodId.getUcn() > 0) {
			msg.append(" exists on site location ");
			msg.append(compareMethodId.getUcn());
			msg.append("-");
			msg.append(compareMethodId.getUcnSuffix());
			msg.append(" ");
			msg.append(compareMethodId.getSiteLocCode());
		} else {
			msg.append(" exists elsewhere");
		}
		
		if (siteComparison == SAME_LOCATION) {
			if (validatedMethodId.getForUcn() == 0)
				msg.append(".");
			else
				msg.append(" for this same location.");
		} else if (siteComparison == DIFF_LOCATION_CODE) {
			msg.append(" for location code ");
			msg.append(compareMethodId.getForSiteLocCode());
			msg.append(".");
		} else if (siteComparison == DIFF_UCN_SUFFIX) {
			msg.append(" for a different Global ID.");
		} else {
			if (compareMethodId.getForUcn() > 0) {
				msg.append(" for UCN ");
				msg.append(compareMethodId.getForUcn());
				msg.append(".");
			} else if (validatedMethodId.getForUcn() > 0 && compareMethodId.getAgreementId() > 0 && compareMethodId.getForUcn() == 0) {
				msg.append(" with no UCN.");
			} else {
				msg.append(".");
			}
		}
		
		if (isError)
			response.getMessages().add(msg.toString());
		else if (isInfo)
			response.getInfoMessages().add(msg.toString());
		else
			response.getAlertMessages().add(msg.toString());
		
		return 0;
	}
}
