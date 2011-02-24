package com.scholastic.sbam.client.uiobjects;

import com.extjs.gxt.ui.client.widget.custom.Portlet;
import com.scholastic.sbam.client.InstitutionSearchPortlet;

public class AppPortletProvider {
	public enum AppPortletIds { 
		FULL_INSTITUTION_SEARCH("Full Institution Search"),
		UNKNOWN_PORTLET("The Unknown Portlet");
		
		String name;
		
		AppPortletIds(String name) {
			this.name = name;
		};
	}
	
	public static Portlet getPortlet(AppPortletIds id) {
		if (id == AppPortletIds.FULL_INSTITUTION_SEARCH)
			return new InstitutionSearchPortlet();
		
		
		Portlet junk = new Portlet();
		junk.setHeading("Unknown Portlet Request");
		junk.addText("This portlet was created in response to a request for unmapped ID '" + id.name + "'.");
		return junk;
	}
}
