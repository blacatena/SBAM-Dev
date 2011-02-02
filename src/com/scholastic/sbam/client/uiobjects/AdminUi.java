package com.scholastic.sbam.client.uiobjects;

import java.util.List;

import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.Composite;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.layout.AccordionLayout;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.scholastic.sbam.client.uiobjects.AppSecurityManager;
import com.scholastic.sbam.client.util.IconSupplier;
import com.scholastic.sbam.shared.security.SecurityManager;

public class AdminUi extends Composite implements AppSecurityManager, AppSleeper {
	private ContentPanel cntntpnlUsers;
	private ContentPanel cntntpnlMessages;
	private ContentPanel cntntpnlVersion;
	private ContentPanel cntntpnlProgramming;
	private UserEditGrid userEditGrid;

	public AdminUi() {
		
		LayoutContainer layoutContainer = new LayoutContainer();
		layoutContainer.setLayout(new AccordionLayout());
		
		cntntpnlUsers = new ContentPanel();
		cntntpnlUsers.setHeading("Users");
		cntntpnlUsers.setCollapsible(true);
		IconSupplier.setIcon(cntntpnlUsers, IconSupplier.getUsersIconName());
		
		cntntpnlUsers.addListener(Events.Collapse, new Listener<ComponentEvent>() {  
			public void handleEvent(ComponentEvent be) {
				userEditGrid.sleep();
			}  
		});  
		cntntpnlUsers.addListener(Events.Expand, new Listener<ComponentEvent>() {  
			public void handleEvent(ComponentEvent be) {
				userEditGrid.awaken();
			}  
		});
		
		userEditGrid = new UserEditGrid();
		cntntpnlUsers.add(userEditGrid);
		layoutContainer.add(cntntpnlUsers);
		
		cntntpnlMessages = new ContentPanel();
		cntntpnlMessages.setHeading("Messages");
		cntntpnlMessages.setCollapsible(true);
		IconSupplier.setIcon(cntntpnlMessages, IconSupplier.getMessagesIconName());
		layoutContainer.add(cntntpnlMessages);
		
		cntntpnlVersion = new ContentPanel();
		cntntpnlVersion.setHeading("Version");
		cntntpnlVersion.setCollapsible(true);
		IconSupplier.setIcon(cntntpnlVersion, IconSupplier.getVersionIconName());
		layoutContainer.add(cntntpnlVersion);
		
		cntntpnlProgramming = new ContentPanel();
		cntntpnlProgramming.setHeading("Programming");
		cntntpnlProgramming.setCollapsible(true);
		IconSupplier.setIcon(cntntpnlProgramming, IconSupplier.getProgrammingIconName());
		layoutContainer.add(cntntpnlProgramming);
		
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
	
	public void sleep() {
		userEditGrid.sleep();
	}
	
	public void awaken() {
		if (!cntntpnlUsers.isCollapsed()) {
			userEditGrid.awaken();
		}
	}
	
}
