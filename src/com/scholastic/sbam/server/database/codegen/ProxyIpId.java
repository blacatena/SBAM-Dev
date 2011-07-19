package com.scholastic.sbam.server.database.codegen;

// Generated Jul 19, 2011 10:00:16 AM by Hibernate Tools 3.2.4.GA

/**
 * ProxyIpId generated by hbm2java
 */
public class ProxyIpId implements java.io.Serializable {

	private int proxyId;
	private int ipId;

	public ProxyIpId() {
	}

	public ProxyIpId(int proxyId, int ipId) {
		this.proxyId = proxyId;
		this.ipId = ipId;
	}

	public int getProxyId() {
		return this.proxyId;
	}

	public void setProxyId(int proxyId) {
		this.proxyId = proxyId;
	}

	public int getIpId() {
		return this.ipId;
	}

	public void setIpId(int ipId) {
		this.ipId = ipId;
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof ProxyIpId))
			return false;
		ProxyIpId castOther = (ProxyIpId) other;

		return (this.getProxyId() == castOther.getProxyId())
				&& (this.getIpId() == castOther.getIpId());
	}

	public int hashCode() {
		int result = 17;

		result = 37 * result + this.getProxyId();
		result = 37 * result + this.getIpId();
		return result;
	}

}
