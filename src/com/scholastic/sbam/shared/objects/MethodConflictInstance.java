package com.scholastic.sbam.shared.objects;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.BeanModelFactory;
import com.extjs.gxt.ui.client.data.BeanModelLookup;
import com.extjs.gxt.ui.client.data.BeanModelTag;
import com.google.gwt.user.client.rpc.IsSerializable;
import com.scholastic.sbam.shared.util.AppConstants;

public class MethodConflictInstance implements BeanModelTag, IsSerializable {
	
	private static BeanModelFactory beanModelfactory;
	
	protected MethodIdInstance			methodId;
	protected AuthMethodInstance		authMethod;
	protected ProxyIpInstance			proxyIp;
	protected RemoteSetupUrlInstance	remoteSetupUrl;
	protected AgreementInstance			agreement;
	protected SiteInstance				owningSite;
	protected SiteInstance				forSite;
	protected ProxyInstance				proxy;
	
	public MethodConflictInstance() {
	}
	public MethodConflictInstance(AuthMethodInstance authMethod) {
		this.authMethod = authMethod;
		this.methodId   = authMethod.obtainMethodId();
	}
	public MethodConflictInstance(ProxyIpInstance proxyIp) {
		this.proxyIp 	= proxyIp;
		this.methodId   = proxyIp.obtainMethodId();
	}
	public MethodConflictInstance(RemoteSetupUrlInstance remoteSetupUrl) {
		this.remoteSetupUrl = remoteSetupUrl;
		this.methodId   	= remoteSetupUrl.obtainMethodId();
	}
	public MethodIdInstance getMethodId() {
		return methodId;
	}
	public void setMethodId(MethodIdInstance methodId) {
		this.methodId = methodId;
	}
	public AuthMethodInstance getAuthMethod() {
		return authMethod;
	}
	public void setAuthMethod(AuthMethodInstance authMethod) {
		this.authMethod = authMethod;
	}
	public ProxyIpInstance getProxyIp() {
		return proxyIp;
	}
	public void setProxyIp(ProxyIpInstance proxyIp) {
		this.proxyIp = proxyIp;
	}
	public AgreementInstance getAgreement() {
		return agreement;
	}
	public void setAgreement(AgreementInstance agreement) {
		this.agreement = agreement;
	}
	public SiteInstance getOwningSite() {
		return owningSite;
	}
	public void setOwningSite(SiteInstance owningSite) {
		this.owningSite = owningSite;
	}
	public ProxyInstance getProxy() {
		return proxy;
	}
	public void setProxy(ProxyInstance proxy) {
		this.proxy = proxy;
	}
	public RemoteSetupUrlInstance getRemoteSetupUrl() {
		return remoteSetupUrl;
	}
	public void setRemoteSetupUrl(RemoteSetupUrlInstance remoteSetupUrl) {
		this.remoteSetupUrl = remoteSetupUrl;
	}
	public SiteInstance getForSite() {
		return forSite;
	}
	public void setForSite(SiteInstance forSite) {
		this.forSite = forSite;
	}
	
	public String getCombinedValue() {
		return getOwnerValue() + " : " + getMethodValue();
	}
	
	public boolean isApproved() {
		if (authMethod != null)
			return authMethod.isApproved();
		else if (proxyIp != null)
			return proxyIp.isApproved();
		else if (remoteSetupUrl != null)
			return remoteSetupUrl.isApproved();
		return false;
	}
	
	public char getStatus() {
		if (authMethod != null)
			return authMethod.getStatus();
		else if (proxyIp != null)
			return proxyIp.getStatus();
		else if (remoteSetupUrl != null)
			return remoteSetupUrl.getStatus();
		return AppConstants.STATUS_ANY_NONE;
	}
	
	public String getStatusDescription() {
		return AppConstants.getStatusDescription(getStatus());
	}
	
	public String getApprovedDescription() {
		return (isApproved()) ? "Yes" : "No";
	}
	
	public String getMethodValue() {
		
		if (methodId.isAgreementAuthMethod()) {
			return authMethod.getMethodDisplay();
		} else if (methodId.isSiteAuthMethod()) {
			return authMethod.getMethodDisplay();
		} else if (methodId.isProxyIp()) {
			return proxyIp.getIpRangeDisplay();
		} else if (methodId.isRemoteSetupUrl()) {
			return remoteSetupUrl.getUrl();
		} else {
			StringBuffer sb = new StringBuffer();
			sb.append("Unknown method type ");
			if (authMethod != null) {
				sb.append(authMethod.getUniqueKey());
			} else if (proxyIp != null) {
				sb.append(proxyIp.getUniqueKey());
			} else if (remoteSetupUrl != null)
				sb.append(remoteSetupUrl.getUniqueKey());
			return sb.toString();
		}	
	}
	
	public String getMethodTypeValue() {
		if (methodId.isAgreementAuthMethod() || methodId.isSiteAuthMethod()) {
			if (authMethod.methodIsIpAddress())
				return "Access IP";
			if (authMethod.methodIsUrl())
				return "Referrer URL";
			if (authMethod.methodIsUserId())
				return "User ID";
			return "Unknown " + methodId.getMethodType();
		} else if (methodId.isProxyIp()) {
			return "Proxy IP";
		} else if (methodId.isRemoteSetupUrl()) {
			return "Remote Setup URL";
		} else {
			return "Unknown " + methodId.getMethodType();
		}
	}
	
	public String getOwnerValue() {
		StringBuffer sb = new StringBuffer();
		
		if (methodId.isAgreementAuthMethod()) {
			sb.append("Agreement ");
			sb.append(AppConstants.appendCheckDigit(methodId.getAgreementId()));
		} else if (methodId.isSiteAuthMethod()) {
			sb.append("UCN ");
			sb.append(methodId.getUcn());
			if (methodId.getSiteLocCode() != null && methodId.getSiteLocCode().length() > 0) {
				sb.append(" ");
				sb.append(methodId.getSiteLocCode());
			}
		} else if (methodId.isProxyIp()) {
			sb.append("Proxy ");
			sb.append(AppConstants.appendCheckDigit(methodId.getProxyId()));
		} else if (methodId.isRemoteSetupUrl()) {
			sb.append("UCN ");
			sb.append(methodId.getUcn());
			if (methodId.getSiteLocCode() != null && methodId.getSiteLocCode().length() > 0) {
				sb.append(" ");
				sb.append(methodId.getSiteLocCode());
			}
		} else {
			sb.append("Unknown owner");
		}
		
		return sb.toString();
	}

	public static BeanModel obtainModel(MethodConflictInstance instance) {
		if (beanModelfactory == null)
			beanModelfactory  = BeanModelLookup.get().getFactory(MethodConflictInstance.class);
		BeanModel model = beanModelfactory.createModel(instance);
		return model;
	}
}
