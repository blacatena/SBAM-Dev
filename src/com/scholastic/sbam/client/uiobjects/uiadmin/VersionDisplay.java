package com.scholastic.sbam.client.uiobjects.uiadmin;

import com.extjs.gxt.ui.client.widget.Composite;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.scholastic.sbam.shared.util.AppConstants;

public class VersionDisplay extends Composite {

	public VersionDisplay() {
		
		LayoutContainer layoutContainer = new LayoutContainer();
		layoutContainer.addStyleName("version");
		
		Html version = new Html("<DIV>Version: <B>" + AppConstants.VERSION + "</B></DIV>");
		Html description = new Html("<DIV>" + AppConstants.VERSION_DESCRIPTION + "</DIV>");
		
		layoutContainer.add(version);
		layoutContainer.add(description);
		
		initComponent(layoutContainer);
	}

}
