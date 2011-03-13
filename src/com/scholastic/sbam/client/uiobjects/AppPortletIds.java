package com.scholastic.sbam.client.uiobjects;

import com.scholastic.sbam.client.util.IconSupplier;

public enum AppPortletIds {
 
	FULL_INSTITUTION_SEARCH	("Full Institution Search", 	IconSupplier.getInstitutionIconName(),		"InstitutionSearch", InstitutionSearchPortlet.class.getName()),
	AGREEMENT_DISPLAY		("Agreement Display", 			IconSupplier.getAgreementIconName(),		"AgreementDisplay" , AgreementPortlet.class.getName()),
	UNKNOWN_PORTLET			("The Unknown Portlet", 		null,										"UnknownPortlet"   , UnknownPortlet.class.getName());
	
	String name;
	String iconName;
	String helpTextId;
	String className;
	
	AppPortletIds(String name, String iconName, String helpTextId, String className) {
		this.name			= name;
		this.iconName		= iconName;
		this.helpTextId		= helpTextId;
		this.className		= className;
	}

	public String getName() {
		return name;
	}

	public String getIconName() {
		return iconName;
	}

	public String getHelpTextId() {
		return helpTextId;
	}

	public String getClassName() {
		return className;
	};

}
