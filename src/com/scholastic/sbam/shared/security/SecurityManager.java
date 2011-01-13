package com.scholastic.sbam.shared.security;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.BeanModelFactory;
import com.extjs.gxt.ui.client.data.BeanModelLookup;
import com.extjs.gxt.ui.client.data.BeanModelTag;
import com.extjs.gxt.ui.client.store.ListStore;
import com.google.gwt.user.client.rpc.IsSerializable;


public class SecurityManager {
	public static final String AUTHENTICATION_ATTRIBUTE = "Auth";
	
	public static final String ROLE_ADMIN = "admin";
	public static final String ROLE_QUERY = "query";
	public static final String ROLE_MAINT = "maint";
	
	public static final List<String> NO_ROLES = new ArrayList<String>();
	
	public static class RoleGroup implements BeanModelTag, IsSerializable {
		private String groupName;
		private String groupTitle;
		private String [] roles;
		public RoleGroup(String groupName, String groupTitle, String [] roles) {
			this.groupName = groupName;
			this.groupTitle = groupTitle;
			this.roles = roles;
		}
		public String getGroupName() {
			return groupName;
		}
		public String getGroupTitle() {
			return groupTitle;
		}
		public String [] getRoles() {
			return roles;
		}
		public boolean allowsRole(String roleName) {
			for (int i = 0; i < roles.length; i++)
				if (roles [i].equals(roleName))
					return true;
			return false;
		}
	}
	
	public static final RoleGroup [] ROLE_GROUPS = new RoleGroup [] {
					new RoleGroup("ADMIN",     "Administrator", new String [] {ROLE_ADMIN, ROLE_MAINT, ROLE_QUERY} ),
					new RoleGroup("STANDARD",  "Standard", 	    new String [] {ROLE_MAINT, ROLE_QUERY} ),
					new RoleGroup("REPORT",    "Analyst",       new String [] {ROLE_QUERY} ),
					new RoleGroup("NONE",      "None",          new String [] {} )
																	};
	
	public static String getRoleGroupTitle(String [] roleNames) {
		int [] qualified = new int [ROLE_GROUPS.length];
		for (String roleName : roleNames) {
			for (int i = 0; i < ROLE_GROUPS.length; i++)
				if (ROLE_GROUPS [i].allowsRole(roleName))
					qualified [i]++;
		}
		for (int i = 0; i < qualified.length; i++)
			if (qualified [i] == ROLE_GROUPS [i].roles.length)
				return ROLE_GROUPS [i].getGroupTitle();
		return "None";
	}
	
	public static String [] getRoleNames(String roleGroupTitle) throws Exception {
		for (int i = 0; i < ROLE_GROUPS.length; i++)
			if (ROLE_GROUPS [i].getGroupTitle().equals(roleGroupTitle))
				return ROLE_GROUPS [i].getRoles();
		throw new Exception("Unknown role group title " + roleGroupTitle);
	}
	
	public static ListStore<BeanModel> getRoleGroupListStore() {
		BeanModelFactory factory = BeanModelLookup.get().getFactory(RoleGroup.class);
		ListStore<BeanModel> listStore = new ListStore<BeanModel>();
		for (int i = 0; i < ROLE_GROUPS.length; i++) {
			listStore.add(factory.createModel(ROLE_GROUPS [i]));
		}
		return listStore;
	}
}
