package com.scholastic.sbam.shared.objects;

import java.util.List;

import com.extjs.gxt.ui.client.data.BeanModelTag;
import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * A tuple of an agreement link with its related agreements.
 * 
 * @author Bob Lacatena
 *
 */
public class ProxyTuple implements BeanModelTag, IsSerializable, UserCacheTarget {
	
	public static final int PROXY_KEY_SET		=	0;
	
	ProxyInstance			proxy;
	List<ProxyIpInstance>	proxyIps;

	public ProxyTuple() {
	}
	public ProxyTuple(ProxyInstance proxy, List<ProxyIpInstance> proxyIps) {
		this.proxyIps		= proxyIps;
		this.proxy	= proxy;
	}
	public ProxyInstance getProxy() {
		return proxy;
	}
	public void setProxy(ProxyInstance proxy) {
		this.proxy = proxy;
	}
	public List<ProxyIpInstance> getProxyIps() {
		return proxyIps;
	}
	public void setProxyIps(List<ProxyIpInstance> proxyIps) {
		this.proxyIps = proxyIps;
	}
	public String getProxyNote() {
		return proxy.getNote();
	}
	
	public static String getUserCacheCategory() {
		return getUserCacheCategory(0);
	}
	
	public static String getUserCacheCategory(int keySet) {
		return "Proxy";
	}

	@Override
	public String userCacheCategory(int keySet) {
		return getUserCacheCategory(keySet);
	}

	@Override
	public String userCacheStringKey(int keySet) {
		return null;
	}

	@Override
	public int userCacheIntegerKey(int keySet) {
		return proxy.getProxyId();
	}
	
	@Override
	public int userCacheKeyCount() {
		return 0;	// All access recording is turned off!!!!
	}
	
}
