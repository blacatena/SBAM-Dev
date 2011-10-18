package com.scholastic.sbam.shared.objects;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.BeanModelFactory;
import com.extjs.gxt.ui.client.data.BeanModelLookup;
import com.extjs.gxt.ui.client.data.BeanModelTag;
import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Class to carry the key information for an IpAddressRange instance... proxy, agreement auth method, or site location auth method.
 * 
 * @author Bob Lacatena
 *
 */
public class MethodIdInstance implements BeanModelTag, IsSerializable {
	
	private static BeanModelFactory beanModelfactory;
	
	public static final String AM_IP 	= "ip";
	public static final String AM_URL	= "url";
	public static final String AM_UID	= "uid";
	public static final String AM_RSURL	= "rsurl";
	public static final String PXY_IP 	= "pip";

	private int		agreementId;
	private int		ucn;
	private int		ucnSuffix;
	private String	siteLocCode;
	
	private String	methodType;
	private int		methodKey;

	private int		proxyId;
	private int		ipId;
	
	private int		forUcn;
	private int		forUcnSuffix;
	private String	forSiteLocCode;

	public int getAgreementId() {
		return agreementId;
	}

	public void setAgreementId(int agreementId) {
		this.agreementId = agreementId;
	}

	public int getUcn() {
		return ucn;
	}

	public void setUcn(int ucn) {
		this.ucn = ucn;
	}

	public int getUcnSuffix() {
		return ucnSuffix;
	}

	public void setUcnSuffix(int ucnSuffix) {
		this.ucnSuffix = ucnSuffix;
	}

	public String getSiteLocCode() {
		return siteLocCode;
	}

	public void setSiteLocCode(String siteLocCode) {
		this.siteLocCode = siteLocCode;
	}

	public String getMethodType() {
		return methodType;
	}

	public void setMethodType(String methodType) {
		this.methodType = methodType;
	}

	public int getMethodKey() {
		return methodKey;
	}

	public void setMethodKey(int methodKey) {
		this.methodKey = methodKey;
	}

	public int getForUcn() {
		return forUcn;
	}

	public void setForUcn(int forUcn) {
		this.forUcn = forUcn;
	}

	public int getForUcnSuffix() {
		return forUcnSuffix;
	}

	public void setForUcnSuffix(int forUcnSuffix) {
		this.forUcnSuffix = forUcnSuffix;
	}

	public String getForSiteLocCode() {
		return forSiteLocCode;
	}

	public void setForSiteLocCode(String forSiteLocCode) {
		this.forSiteLocCode = forSiteLocCode;
	}

	public int getProxyId() {
		return proxyId;
	}

	public void setProxyId(int proxyId) {
		this.proxyId = proxyId;
	}

	public int getIpId() {
		return ipId;
	}

	public void setIpId(int ipId) {
		this.ipId = ipId;
	}
	
	public boolean isAgreementAuthMethod() {
		return agreementId > 0;
	}
	
	public boolean isSiteAuthMethod() {
		return ucn > 0;
	}
	
	public boolean isAgreementIp() {
		return agreementId > 0 && AM_IP.equals(methodType);
	}
	
	public boolean isSiteIp() {
		return ucn > 0 && AM_IP.equals(methodType);
	}
	
	public boolean isProxyIp() {
		return proxyId > 0;
	}
	
	public boolean isRemoteSetupUrl() {
		return AM_RSURL.equals(methodType);
	}
	
	public boolean isAgreementUserId() {
		return agreementId > 0 && AM_UID.equals(methodType);
	}
	
	public boolean isSiteUserId() {
		return ucn > 0 && AM_UID.equals(methodType);
	}
	
	public boolean isAgreementUrl() {
		return agreementId > 0 && AM_URL.equals(methodType);
	}
	
	public boolean isSiteUrl() {
		return ucn > 0 && AM_URL.equals(methodType);
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

	public boolean sourceOwnerEquals(MethodIdInstance other) {
		return (
					proxyId		== other.proxyId
			&&		agreementId == other.agreementId
			&&		ucn			== other.ucn
			&&		ucnSuffix	== other.ucnSuffix
			&& 		stringEquals(siteLocCode, other.siteLocCode)
			);
	}

	public boolean sourceEquals(MethodIdInstance other) {
		return (
					proxyId		== other.proxyId
			&&		ipId		== other.ipId
			&&		agreementId == other.agreementId
			&&		ucn			== other.ucn
			&&		ucnSuffix	== other.ucnSuffix
			&&		methodKey	== other.methodKey
			&&		stringEquals(methodType,  other.methodType)
			&& 		stringEquals(siteLocCode, other.siteLocCode)
			);
	}

	public boolean siteEquals(MethodIdInstance other) {
		return (
					forUcn		== other.forUcn
			&&		forUcnSuffix== other.forUcnSuffix
			&& 		stringEquals(forSiteLocCode, other.forSiteLocCode)
			
			);
	}

	public boolean equals(MethodIdInstance other) {
		return (
					proxyId		== other.proxyId
			&&		ipId		== other.ipId
			&&		agreementId == other.agreementId
			&&		ucn			== other.ucn
			&&		ucnSuffix	== other.ucnSuffix
			&&		methodKey	== other.methodKey
			&&		forUcn		== other.forUcn
			&&		forUcnSuffix== other.forUcnSuffix
			&& 		stringEquals(siteLocCode, other.siteLocCode)
			&&		stringEquals(methodType,  other.methodType)
			&& 		stringEquals(forSiteLocCode, other.forSiteLocCode)
			);
	}

	public static MethodIdInstance getEmptyInstance() {
		MethodIdInstance instance = new MethodIdInstance();
		instance.proxyId		=	0;
		instance.ipId			=	0;
		instance.agreementId	=	0;
		instance.ucn			=	0;
		instance.ucnSuffix		=	0;
		instance.methodKey		=	0;
		instance.forUcn			=	0;
		instance.forUcnSuffix	=	0;
		instance.siteLocCode	=	"";
		instance.methodType		=	"";
		instance.forSiteLocCode	=	"";
		return instance;
	}

	public void setFrom(MethodIdInstance other) {
		this.proxyId		= other.proxyId;
		this.ipId			= other.ipId;
		this.agreementId 	= other.agreementId;
		this.ucn			= other.ucn;
		this.ucnSuffix		= other.ucnSuffix;
		this.siteLocCode	= other.siteLocCode;
		this.methodType		= other.methodType;
		this.methodKey		= other.methodKey;
		this.forUcn			= other.forUcn;
		this.forUcnSuffix	= other.forUcnSuffix;
		this.forSiteLocCode	= other.forSiteLocCode;
	}
	
	public static BeanModel obtainModel(MethodIdInstance instance) {
		if (beanModelfactory == null)
			beanModelfactory  = BeanModelLookup.get().getFactory(MethodIdInstance.class);
		BeanModel model = beanModelfactory.createModel(instance);
		return model;
	}
	
	public String toString() {
		if (proxyId > 0)
			return "Proxy " + proxyId + "-" + ipId;
		if (agreementId > 0)
			return "Agreement " + agreementId + "-" + methodType + "-" + methodKey + " for " + forUcn + "-" + forUcnSuffix + " " + forSiteLocCode;
		return "Site " + ucn + "-" + ucnSuffix + " " + siteLocCode + "-" + methodType + "-" + methodKey + " for " + forUcn + "-" + forUcnSuffix + " " + forSiteLocCode;
	}
}
