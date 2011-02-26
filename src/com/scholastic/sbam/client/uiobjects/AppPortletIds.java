package com.scholastic.sbam.client.uiobjects;

import com.scholastic.sbam.client.util.IconSupplier;

public enum AppPortletIds {
 
	FULL_INSTITUTION_SEARCH	("Full Institution Search", 	IconSupplier.getInstitutionIconName(),		"InstitutionSearch"),
	UNKNOWN_PORTLET			("The Unknown Portlet", 		null,										"UnknownPortlet");
	
	String name;
	String iconName;
	String helpTextId;
	
	
	AppPortletIds(String name, String iconName, String helpTextId) {
		this.name			= name;
		this.iconName		= iconName;
		this.helpTextId		= helpTextId;
	}

	public String getName() {
		return name;
	}

	public String getIconName() {
		return iconName;
	}

	public String getHelpTextId() {
		return helpTextId;
	};

}
