package com.scholastic.sbam.shared.objects;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Authentication implements IsSerializable {
	public static long LOG_OFF_GRACE_PERIOD	=	1000 * 120;	// Two minute grace period to handle requests after a user has logged off
	
	private int				userId;
	private String			userName;
	private String			firstName;
	private String			lastName;
	private String			message;
	private boolean			authenticated;
	private int				cachedPortlets;
	private long			loggedOff;
	private List<String>	roleNames = new ArrayList<String>();
	
	private UserProfileInstance	profile;
	
	public Authentication() {
		
	}
	
	public Authentication(int userId, String userName, String firstName, String lastName) {
		this.userId		= userId;
		this.userName	= userName;
		this.firstName	= firstName;
		this.lastName	= lastName;
		this.message	= "";
	}
	
	public void addRoleName(String roleName) {
		if (!roleNames.contains(roleName))
			roleNames.add(roleName);
	}
	
	public void removeRoleName(String roleName) {
		if (roleNames.contains(roleName))
			roleNames.remove(roleName);
	}
	
	public void clearRoleNames() {
		roleNames.clear();
	}
	
	public boolean hasRoleName(String roleName) {
		return roleNames.contains(roleName);
	}
	
	public String getDisplayName() {
		return getDisplayName(firstName, lastName);
	}
	
	public static String getDisplayName(String firstName, String lastName) {
		if (firstName != null)
			if (lastName != null)
				return firstName + " " + lastName;
			else
				return firstName;
		else if (lastName != null)
			return lastName;
		else
			return "Unknown.";
	}
	
	public String toString() {
		return userName + "/" + firstName + "/" + lastName + "/" + authenticated + "/" + roleNames;
	}
	
	public boolean isAuthenticated() {
		return authenticated;
	}

	public void setAuthenticated(boolean authenticated) {
		this.authenticated = authenticated;
	}

	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public int getCachedPortlets() {
		return cachedPortlets;
	}

	public void setCachedPortlets(int cachedPortlets) {
		this.cachedPortlets = cachedPortlets;
	}

	public List<String> getRoleNames() {
		return roleNames;
	}
	public void setRoleNames(List<String> roleNames) {
		this.roleNames = roleNames;
	}

	public long getLoggedOff() {
		return loggedOff;
	}

	public void setLoggedOff(long loggedOff) {
		this.loggedOff = loggedOff;
	}
	
	public void setLoggedOff() {
		setLoggedOff(System.currentTimeMillis());
	}

	public UserProfileInstance getProfile() {
		return profile;
	}

	public void setProfile(UserProfileInstance profile) {
		this.profile = profile;
	}
	
}
