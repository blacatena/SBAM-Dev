package com.scholastic.sbam.server.database.codegen;

// Generated Jul 1, 2011 1:29:56 PM by Hibernate Tools 3.2.4.GA

/**
 * UserPortletCacheId generated by hbm2java
 */
public class UserPortletCacheId implements java.io.Serializable {

	private String userName;
	private int portletId;

	public UserPortletCacheId() {
	}

	public UserPortletCacheId(String userName, int portletId) {
		this.userName = userName;
		this.portletId = portletId;
	}

	public String getUserName() {
		return this.userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public int getPortletId() {
		return this.portletId;
	}

	public void setPortletId(int portletId) {
		this.portletId = portletId;
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof UserPortletCacheId))
			return false;
		UserPortletCacheId castOther = (UserPortletCacheId) other;

		return ((this.getUserName() == castOther.getUserName()) || (this
				.getUserName() != null && castOther.getUserName() != null && this
				.getUserName().equals(castOther.getUserName())))
				&& (this.getPortletId() == castOther.getPortletId());
	}

	public int hashCode() {
		int result = 17;

		result = 37 * result
				+ (getUserName() == null ? 0 : this.getUserName().hashCode());
		result = 37 * result + this.getPortletId();
		return result;
	}

}
