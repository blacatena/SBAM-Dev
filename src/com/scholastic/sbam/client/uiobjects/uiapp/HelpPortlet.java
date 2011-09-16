package com.scholastic.sbam.client.uiobjects.uiapp;

import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.user.client.Element;
import com.scholastic.sbam.client.uiobjects.uitop.HelpTextDialog;

public class HelpPortlet extends AppPortlet {
	
	protected String			currentPage	= "intro";
	protected HelpTextDialog	helpDialog;

	public HelpPortlet() {
		super(AppPortletIds.HELP_VIEWER.getHelpTextId());
	}
	
	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);
		
		setHeight(550);
		setLayout(new FitLayout());
		
		helpDialog = new HelpTextDialog(currentPage);
		helpDialog.setFrame(false);
		helpDialog.setHeaderVisible(false);
		helpDialog.addStyleName("help-portlet");
		
		add(helpDialog);
	}

	@Override
	public void setFromKeyData(String keyData) {
		currentPage = keyData;
		if (helpDialog != null)
			helpDialog.setHelpTextId(keyData);
	}

	@Override
	public String getKeyData() {
		return currentPage;
	}

}
