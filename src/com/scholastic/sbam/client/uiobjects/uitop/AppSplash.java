package com.scholastic.sbam.client.uiobjects.uitop;

import java.util.List;

import com.extjs.gxt.ui.client.widget.Composite;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.layout.CenterLayout;
import com.scholastic.sbam.client.uiobjects.foundation.AppSecurityManager;

public class AppSplash extends Composite implements AppSecurityManager {

	public AppSplash() {
		
		LayoutContainer layoutContainer = new LayoutContainer();
		layoutContainer.setLayout(new CenterLayout());
		
		Html htmlThesbamapplication = new Html("The<br/> <b>SBAM</b><br/><i>Application</i>");
		htmlThesbamapplication.setBorders(true);
		htmlThesbamapplication.addStyleName("x-panel");
		layoutContainer.add(htmlThesbamapplication);
		initComponent(layoutContainer);
	}

	public void applyRoles(List<String> roleNames) {
		return;
	}
}
