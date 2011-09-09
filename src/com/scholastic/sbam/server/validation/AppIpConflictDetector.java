package com.scholastic.sbam.server.validation;

import java.util.List;

import com.scholastic.sbam.server.database.codegen.AuthMethod;
import com.scholastic.sbam.server.database.codegen.ProxyIp;
import com.scholastic.sbam.server.database.objects.DbAgreementTerm;
import com.scholastic.sbam.server.database.objects.DbAuthMethod;
import com.scholastic.sbam.server.database.objects.DbProxyIp;
import com.scholastic.sbam.server.database.util.HibernateUtil;
import com.scholastic.sbam.shared.objects.AuthMethodInstance;
import com.scholastic.sbam.shared.objects.IpAddressInstance;
import com.scholastic.sbam.shared.objects.MethodIdInstance;
import com.scholastic.sbam.shared.objects.ProxyIpInstance;
import com.scholastic.sbam.shared.util.AppConstants;
import com.scholastic.sbam.shared.validation.AsyncValidationResponse;

/**
* This class performs a validation of an ip address range for a method instance (and agreement method, site method, or proxy) to determine if there
* are any conflicts, i.e. situations where this address conflicts with another address for a different use, site, or range.
* 
* In particular,
* 
* First, IP addresses may overlap.  2-3 is within 1-4, 1-3 overlaps with 2-4, etc.
* 
* Second, if an IP address is used for one site (UCN, location), it should not be used for a different site.
* 
* Third, if an IP address is used on an agreement or site, it should not be used for a proxy as well.
* 
* Note that there are circumstances that are undesirable but allowed.  In particular, the same IP may exist on several agreements because they have not been migrated
* to the site (UCN/location).  They may also have been set up with conflicts that can be resolved in authentication (such as a case where
* the IP addresses are delivering different products).  THIS IS NOT DESIREABLE, but it is allowed, primarily to provide backward compatibility to 
* legacy data.
* 
* But these situations should be resolved, and so alert messages are generated for the user through this service.
*/
public class AppIpConflictDetector {
	
	public static final int	SAME_SOURCE			=	0;		//	Both ranges belong to the same source (agreement, location, proxy)
	public static final int	SAME_SOURCE_TYPE	=	1;		//	Both ranges belong to different sources of the same type (agreement, location, proxy)
	public static final int	DIFF_SOURCE_TYPE	=	2;		//	Both ranges belong to different sources of different types (agreement, location, proxy)
	
	public static final int SAME_LOCATION		=	10;		//	Both ranges belong to the same location (i.e. site and location code)
	public static final int DIFF_LOCATION_CODE	=	20;		//	Both ranges belong to the same location (i.e. site and location code)
	public static final int DIFF_UCN_SUFFIX		=	30;		//	Both ranges belong to the same UCN, but a different location AND source
	public static final int DIFF_UCN			=	40;		//	Both ranges belong to entirely different UCNs.
	
	public static final int SAME_RANGE			=	100;	//	Exact same low and high IPs
	public static final int	ENCOMPASSED			=	200;	//	The other range is entirely within the first
	public static final int ENCOMPASSING		=	300;	//	This range is entirely within the other range
	public static final	int PARTIAL_OVERLAP		=	400;	// Some IPs overlap, but each has values outside of the other

	protected long ipLo;
	protected long ipHi;
	protected MethodIdInstance methodId;
	protected AsyncValidationResponse response;
	
	public AppIpConflictDetector(long ipLo, long ipHi, MethodIdInstance methodId, AsyncValidationResponse response) {
		this.ipLo = ipLo;
		this.ipHi = ipHi;
		this.methodId = methodId;
		this.response = response;
	}
	
	public AppIpConflictDetector(long ipLo, long ipHi, MethodIdInstance methodId) {
		this(ipLo, ipHi, methodId, new AsyncValidationResponse(0));
	}
	
	public AppIpConflictDetector(long ipLo, long ipHi, MethodIdInstance methodId, int validationCounter) {
		this(ipLo, ipHi, methodId, new AsyncValidationResponse(validationCounter));
	}
	
	public void doValidation() {
		
		boolean endTransaction = false;
		
		if (!HibernateUtil.isTransactionInProgress()) {
			endTransaction = true;
			HibernateUtil.openSession();
			HibernateUtil.startTransaction();
		}
		
		List<AuthMethod> authMethods  = DbAuthMethod.findOverlapIps(ipLo, ipHi);
		
		int otherAgreements = 0;
		if (authMethods != null) {
			for (AuthMethod authMethod : authMethods) {
				AuthMethodInstance amInstance = DbAuthMethod.getInstance(authMethod);
				//	Don't check a method against itself
				if (methodId != null && methodId.sourceEquals(amInstance.obtainMethodId())) {
					continue;
				}
				
				otherAgreements += compare(amInstance.getIpLo(), amInstance.getIpHi(), amInstance.obtainMethodId());
			}
		}
		
		//	One message counting valid assignments within other agreements
		if (otherAgreements > 0)
			response.getInfoMessages().add("This IP " + (ipLo == ipHi?"address":"range") + " exists on " + otherAgreements + " other agreement" +
						(otherAgreements > 1?"s":"") + " consistent with this assignment.");
			
		List<ProxyIp> proxyIps  = DbProxyIp.findOverlapIps(ipLo, ipHi);
		
		if (proxyIps != null) {
			for (ProxyIp proxyIp : proxyIps) {
				ProxyIpInstance pipInstance = DbProxyIp.getInstance(proxyIp);
				//	Don't check a proxy IP against itself
				if (methodId != null && methodId.sourceEquals(pipInstance.obtainMethodId())) {
					continue;
				}
				
				compare(pipInstance.getIpLo(), pipInstance.getIpHi(), pipInstance.obtainMethodId());
			}
		}
		
		if (endTransaction) {
			HibernateUtil.endTransaction();
			HibernateUtil.closeSession();
		}
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
	
	protected int compare(long compareIpLo, long compareIpHi, MethodIdInstance compareMethodId) {
		
		int rangeComparison;
		int siteComparison;
		int sourceComparison;
		String sourceType;
//		String compareType;
		
		boolean isInfo  = false;
		boolean isError = false;
		StringBuffer msg = new StringBuffer();
		
		if (methodId.getAgreementId() > 0)
			sourceType = "agreement";
		else if (methodId.getUcn() > 0)
			sourceType = "site location";
		else
			sourceType = "proxy";

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
		
		if (ipLo == compareIpLo && ipHi == compareIpHi)
			rangeComparison = SAME_RANGE;
		else if (ipLo < compareIpLo && ipHi > compareIpHi)
			rangeComparison = ENCOMPASSED;
		else if (ipLo > compareIpLo && ipHi < compareIpHi)
			rangeComparison = ENCOMPASSING;
		else
			rangeComparison = PARTIAL_OVERLAP;
		
		// Exact same IP for the same site location on multiple agreements generates just one message with a count...
		if (rangeComparison == SAME_RANGE
		&&	siteComparison  == SAME_LOCATION
		&&  sourceComparison== SAME_SOURCE_TYPE
		&&  methodId.getAgreementId() > 0)
			return 1;
			
		
		if (rangeComparison == SAME_RANGE)
			if (ipLo == ipHi)
				msg.append("This IP address ");
			else
				msg.append("This IP range ");
		else
			msg.append("The IP " + IpAddressInstance.getBriefIpDisplay(compareIpLo, compareIpHi));
		
		if (sourceComparison == SAME_SOURCE) {
			if (rangeComparison == SAME_RANGE) {
				isError = true;
				msg.append(" already exists on this ");
				msg.append(sourceType);
			} else if (rangeComparison == ENCOMPASSED) {
				msg.append(" encompasses this IP on this ");
				msg.append(sourceType);
			} else {
				msg.append(" overlaps with this IP on this ");
				msg.append(sourceType);
			}
		} else {
			if (compareMethodId.getAgreementId() > 0) {
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
			} else if (compareMethodId.getProxyId() > 0) {
				msg.append(" exists on proxy ");
				msg.append(AppConstants.appendCheckDigit(compareMethodId.getProxyId()));
			}
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

	public long getIpLo() {
		return ipLo;
	}

	public void setIpLo(long ipLo) {
		this.ipLo = ipLo;
	}

	public long getIpHi() {
		return ipHi;
	}

	public void setIpHi(long ipHi) {
		this.ipHi = ipHi;
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

	public void newResponse(int validationCounter) {
		response = new AsyncValidationResponse(validationCounter);
	}
	
	public void newResponse() {
		newResponse(0);
	}
}
