package com.scholastic.sbam.client.uiobjects;

import java.util.List;

import com.extjs.gxt.ui.client.widget.Composite;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.layout.AccordionLayout;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.scholastic.sbam.client.uiobjects.AppSecurityManager;
import com.scholastic.sbam.shared.security.SecurityManager;

public class AdminUi extends Composite implements AppSecurityManager {
	private ContentPanel cntntpnlUsers;
	private ContentPanel cntntpnlMessages;
	private UserEditGrid userEditGrid;

	public AdminUi() {
		
		LayoutContainer layoutContainer = new LayoutContainer();
		layoutContainer.setLayout(new AccordionLayout());
		
		cntntpnlUsers = new ContentPanel();
		cntntpnlUsers.setHeading("Users");
		cntntpnlUsers.setCollapsible(true);
		
		userEditGrid = new UserEditGrid();
		cntntpnlUsers.add(userEditGrid);
		layoutContainer.add(cntntpnlUsers);
		
		cntntpnlMessages = new ContentPanel();
		cntntpnlMessages.setHeading("Messages");
		cntntpnlMessages.setCollapsible(true);
		layoutContainer.add(cntntpnlMessages);
		initComponent(layoutContainer);
		layoutContainer.setBorders(true);
	}

	public void applyRoles(List<String> roleNames) {
		if (roleNames.contains(SecurityManager.ROLE_ADMIN)) {
			cntntpnlUsers.enable();
			cntntpnlMessages.enable();
		} else {
			cntntpnlUsers.disable();
			cntntpnlMessages.disable();
		}
	}
	
}
