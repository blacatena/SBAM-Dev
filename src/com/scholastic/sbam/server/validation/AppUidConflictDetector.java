package com.scholastic.sbam.server.validation;

import java.util.List;

import com.scholastic.sbam.server.database.codegen.AuthMethod;
import com.scholastic.sbam.server.database.objects.DbAgreementTerm;
import com.scholastic.sbam.server.database.objects.DbAuthMethod;
import com.scholastic.sbam.shared.objects.AuthMethodInstance;
import com.scholastic.sbam.shared.objects.MethodIdInstance;
import com.scholastic.sbam.shared.util.AppConstants;
import com.scholastic.sbam.shared.validation.AsyncValidationResponse;

/**
 * This class performs a validation of a user id/password/proxy combination for a method instance (and agreement method or site method) to determine if there
 * are any conflicts, i.e. situations where this address conflicts with another address for a different use, site, or range.
 * 
 * In particular,
 * 
 * Passwords should match.
 * 
 * Proxy IDs should match.
 * 
 * Note that there are circumstances that are undesirable but allowed.  In particular, the same user ID may have existed in the past for another, 
 * expired agreement.  THIS IS NOT DESIREABLE, but it is allowed, primarily to provide backward compatibility to 
 * legacy data.
 * 
 * But these situations should be resolved manually by the users, and so alert messages are generated for the user through this service.
 */
public class AppUidConflictDetector {
	
	public static final int	SAME_SOURCE			=	0;		//	Both ranges belong to the same source (agreement, location, proxy)
	public static final int	SAME_SOURCE_TYPE	=	1;		//	Both ranges belong to different sources of the same type (agreement, location, proxy)
	public static final int	DIFF_SOURCE_TYPE	=	2;		//	Both ranges belong to different sources of different types (agreement, location, proxy)
	
	public static final int SAME_LOCATION		=	10;		//	Both ranges belong to the same location (i.e. site and location code)
	public static final int DIFF_LOCATION_CODE	=	20;		//	Both ranges belong to the same location (i.e. site and location code)
	public static final int DIFF_UCN_SUFFIX		=	30;		//	Both ranges belong to the same UCN, but a different location AND source
	public static final int DIFF_UCN			=	40;		//	Both ranges belong to entirely different UCNs.

	protected String					uid;
	protected String					password;
	protected char						userType;
	protected int						proxyId;
	protected MethodIdInstance			methodId;
	protected AsyncValidationResponse	response;
	
	public AppUidConflictDetector(String uid, String password, char userType, int proxyId, MethodIdInstance methodId, AsyncValidationResponse response) {
		this.uid = uid;
		this.password = password;
		this.userType = userType;
		this.proxyId  = proxyId;
		this.methodId = methodId;
		this.response = response;
	}
	
	public AppUidConflictDetector(String uid, String password, char userType, int proxyId, MethodIdInstance methodId) {
		this(uid, password, userType, proxyId, methodId, new AsyncValidationResponse(0));
	}
	
	public AppUidConflictDetector(String uid, String password, char userType, int proxyId, MethodIdInstance methodId, int validationCounter) {
		this(uid, password, userType, proxyId, methodId, new AsyncValidationResponse(validationCounter));
	}

	public void doValidation() {
		
		List<AuthMethod> authMethods  = DbAuthMethod.findByUid(uid);
		
		int otherAgreements = 0;
		if (authMethods != null) {
			for (AuthMethod authMethod : authMethods) {
				AuthMethodInstance amInstance = DbAuthMethod.getInstance(authMethod);
				//	Don't check a method against itself
				if (methodId != null && methodId.sourceEquals(amInstance.obtainMethodId())) {
					continue;
				}
				
				otherAgreements += compare(amInstance.getUserId(), amInstance.getPassword(), amInstance.getUserType(), amInstance.getProxyId(), amInstance.obtainMethodId(), response);
			}
		}
		
		//	One message counting valid assignments within other agreements
		if (otherAgreements > 0)
			response.getInfoMessages().add("This user ID exists on " + otherAgreements + " other agreement" +
						(otherAgreements > 1?"s":"") + " consistent with this assignment.");
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
	
	protected int compare(String compareUid, String comparePassword, char compareUserType, int compareProxyId, MethodIdInstance compareMethodId, AsyncValidationResponse response) {
		
		int siteComparison		= -1;
		int sourceComparison	= -1;
		String sourceType		= "";
//		String compareType		= "";
		
		boolean isError = false;
		boolean isInfo = false;
		StringBuffer msg = new StringBuffer();
		
		
		if (methodId.getAgreementId() > 0)
			sourceType = "agreement";
		else if (methodId.getUcn() > 0)
			sourceType = "site location";
		else
			sourceType = "proxy";	//	Can never happen... can't have UIDs on proxies

//		if (compareMethodId.getAgreementId() > 0)
//			compareType = "agreement";
//		else if (compareMethodId.getUcn() > 0)
//			compareType = "site location";
//		else
//			compareType = "proxy";
		
		if (methodId.sourceOwnerEquals(compareMethodId) )
			sourceComparison = SAME_SOURCE;
		else {
			if (
					 (methodId.getAgreementId() != 0 && compareMethodId.getAgreementId() != 0)
				||   (methodId.getUcn()         != 0 && compareMethodId.getUcn()         != 0)
				||   (methodId.getProxyId()     != 0 && compareMethodId.getProxyId()     != 0) 
			)
				sourceComparison = SAME_SOURCE_TYPE;
			else
				sourceComparison = DIFF_SOURCE_TYPE;
		}
		
		if (methodId.getForUcn() != compareMethodId.getForUcn())
			siteComparison = DIFF_UCN;
		else if (methodId.getForUcnSuffix() != compareMethodId.getForUcnSuffix())
			siteComparison = DIFF_UCN_SUFFIX;
		else if (stringEquals(methodId.getForSiteLocCode(), methodId.getForSiteLocCode() ) )
			siteComparison = SAME_LOCATION;
		else
			siteComparison = DIFF_LOCATION_CODE;
		
		if (proxyId < 0)
			proxyId = 0;	// This was a deleted value if negative, saved for reference, so just zero it for comparison
		if (compareProxyId < 0)
			compareProxyId = 0;	// This was a deleted value if negative, saved for reference, so just zero it for comparison
		
		boolean samePassword = (password == null)? false : password.equals(comparePassword);
		boolean sameUserType = userType == compareUserType;
		boolean sameProxyId  = proxyId == compareProxyId;
		boolean sameEverything = samePassword && sameUserType && sameProxyId;
		
		if (sameEverything
		&&	siteComparison  == SAME_LOCATION
		&&  sourceComparison== SAME_SOURCE_TYPE
		&&  methodId.getAgreementId() > 0)
			return 1;
			
		msg.append("This user ID ");
		
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
		
		if (!samePassword) {
			msg.append(" with the password '");
			msg.append(comparePassword);
			msg.append("'");
		}
		if (!sameProxyId) {
			if (compareProxyId > 0) {
				if (samePassword)
					msg.append(" with");
				else
					msg.append(" and");
				msg.append(" proxy ");
				msg.append(AppConstants.appendCheckDigit(compareProxyId));
			} else
				msg.append(" without a proxy");
		}
		if (!sameUserType) {
			if (!samePassword || !sameProxyId)
				msg.append(" and");
			msg.append(" is ");
			msg.append(AuthMethodInstance.getUserTypeNoun(compareUserType));
		}
		
		if (siteComparison == SAME_LOCATION) {
			if (methodId.getForUcn() == 0)
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
			} else if (methodId.getForUcn() > 0 && compareMethodId.getAgreementId() > 0 && compareMethodId.getForUcn() == 0) {
				msg.append(" with no UCN.");
			} else {
				msg.append(".");
			}
		}
		
		if (isError)
			response.getErrorMessages().add(msg.toString());
		else if (isInfo)
			response.getInfoMessages().add(msg.toString());
		else
			response.getAlertMessages().add(msg.toString());
		
		return 0;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public char getUserType() {
		return userType;
	}

	public void setUserType(char userType) {
		this.userType = userType;
	}

	public int getProxyId() {
		return proxyId;
	}

	public void setProxyId(int proxyId) {
		this.proxyId = proxyId;
	}

	public MethodIdInstance getMethodId() {
		return methodId;
	}

	public void setMethodId(MethodIdInstance methodId) {
		this.methodId = methodId;
	}

	public AsyncValidationResponse getResponse() {
		return response;
	}

	public void setResponse(AsyncValidationResponse response) {
		this.response = response;
	}

}
