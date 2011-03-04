package com.scholastic.sbam.client.uiobjects;

import java.util.List;

import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.Composite;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.layout.AccordionLayout;
import com.extjs.gxt.ui.client.widget.layout.CenterLayout;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.scholastic.sbam.client.uiobjects.AppSecurityManager;
import com.scholastic.sbam.client.util.IconSupplier;
import com.scholastic.sbam.shared.security.SecurityManager;

public class AdminUi extends Composite implements AppSecurityManager, AppSleeper {
	private ContentPanel cntntpnlUsers;
	private ContentPanel cntntpnlMessages;
	private ContentPanel cntntpnlVersion;
	private ContentPanel cntntpnlProgramming;
	private ContentPanel cntntpnlCacheControl;
	private UserEditGrid userEditGrid;
	private DocumentationLinksDisplay docLinksDisplay;
	private WelcomeMessageEditGrid welcomeMessageEditGrid;
	private CacheControlPanel cacheControlPanel;

	public AdminUi() {
		
		LayoutContainer layoutContainer = new LayoutContainer();
		layoutContainer.setLayout(new AccordionLayout());
		
		/*
		 * Welcome Messages edit
		 */
		
		cntntpnlMessages = new ContentPanel(new CenterLayout());
		cntntpnlMessages.setHeading("Messages");
		cntntpnlMessages.setCollapsible(true);
		IconSupplier.setIcon(cntntpnlMessages, IconSupplier.getMessagesIconName());
		
		cntntpnlMessages.addListener(Events.Collapse, new Listener<ComponentEvent>() {  
			public void handleEvent(ComponentEvent be) {
				if (welcomeMessageEditGrid != null) welcomeMessageEditGrid.sleep();
			}  
		});  
		cntntpnlMessages.addListener(Events.Expand, new Listener<ComponentEvent>() {  
			public void handleEvent(ComponentEvent be) {
				if (welcomeMessageEditGrid != null) welcomeMessageEditGrid.awaken();
			}  
		});
		
		welcomeMessageEditGrid = new WelcomeMessageEditGrid();
		cntntpnlMessages.add(welcomeMessageEditGrid);
		layoutContainer.add(cntntpnlMessages);
		
		/*
		 * Users edit
		 */
		
		cntntpnlUsers = new ContentPanel(new CenterLayout()) {
			@Override
			public void onExpand() {
				super.onExpand();
				layout(true);
			}
		};
		cntntpnlUsers.setHeading("Users");
		cntntpnlUsers.setCollapsible(true);
		IconSupplier.setIcon(cntntpnlUsers, IconSupplier.getUsersIconName());
		
		cntntpnlUsers.addListener(Events.Collapse, new Listener<ComponentEvent>() {  
			public void handleEvent(ComponentEvent be) {
				if (userEditGrid != null) userEditGrid.sleep();
			}  
		});  
		cntntpnlUsers.addListener(Events.Expand, new Listener<ComponentEvent>() {  
			public void handleEvent(ComponentEvent be) {
				if (userEditGrid != null) userEditGrid.awaken();
			}  
		});
		
		userEditGrid = new UserEditGrid();
		cntntpnlUsers.add(userEditGrid);
		layoutContainer.add(cntntpnlUsers);
		
		/*
		 * Version display
		 */
		
		cntntpnlVersion = new ContentPanel();
		cntntpnlVersion.setHeading("Version");
		cntntpnlVersion.setCollapsible(true);
		IconSupplier.setIcon(cntntpnlVersion, IconSupplier.getVersionIconName());
		cntntpnlVersion.add(new VersionDisplay());
		layoutContainer.add(cntntpnlVersion);
		
		/*
		 * Programming documentation links
		 */
		
		cntntpnlProgramming = new ContentPanel();
		cntntpnlProgramming.setHeading("Programming");
		cntntpnlProgramming.setCollapsible(true);
		IconSupplier.setIcon(cntntpnlProgramming, IconSupplier.getProgrammingIconName());
		
		docLinksDisplay = new DocumentationLinksDisplay();
		cntntpnlProgramming.add(docLinksDisplay);
		layoutContainer.add(cntntpnlProgramming);
		
		cntntpnlProgramming.addListener(Events.Expand, new Listener<ComponentEvent>() {
            public void handleEvent(ComponentEvent be) {
            	if(docLinksDisplay != null)
            		docLinksDisplay.startReload();
            }
        });
		
		/*
		 * Cache Control
		 */

		cntntpnlCacheControl = new ContentPanel();
		cntntpnlCacheControl.setHeading("Cache Control");
		cntntpnlCacheControl.setCollapsible(true);
		IconSupplier.setIcon(cntntpnlCacheControl, IconSupplier.getCacheIconName());
		cacheControlPanel = new CacheControlPanel();
		cntntpnlCacheControl.add(cacheControlPanel);
		layoutContainer.add(cntntpnlCacheControl);

		/*
		 * Finish up
		 */
		
		initComponent(layoutContainer);
		layoutContainer.setBorders(true);
	}

	public void applyRoles(List<String> roleNames) {
		if (roleNames.contains(SecurityManager.ROLE_ADMIN)) {
			cntntpnlUsers.enable();
			cntntpnlMessages.enable();
			cntntpnlVersion.enable();
			cntntpnlProgramming.enable();
			cntntpnlCacheControl.enable();
		} else {
			cntntpnlUsers.disable();
			cntntpnlMessages.disable();
			cntntpnlVersion.disable();
			cntntpnlProgramming.disable();
			cntntpnlCacheControl.disable();
		}
	}
	
	public void sleep() {
		if (userEditGrid != null) 			userEditGrid.sleep();
		if (docLinksDisplay != null) 		docLinksDisplay.sleep();
		if (welcomeMessageEditGrid != null) welcomeMessageEditGrid.sleep();
//		if (cntntpnlProgramming != null) 	cntntpnlProgramming.sleep();
		if (cntntpnlCacheControl != null) 	cacheControlPanel.sleep();
	}
	
	public void awaken() {
		if (!cntntpnlUsers.isCollapsed() && userEditGrid != null) {
			userEditGrid.awaken();
		}
		if (!cntntpnlProgramming.isCollapsed() && docLinksDisplay != null) {
			docLinksDisplay.awaken();
		}
		if (!cntntpnlMessages.isCollapsed() && welcomeMessageEditGrid != null) {
			welcomeMessageEditGrid.awaken();
		}
//		if (!cntntpnlProgramming.isCollapsed() && cntntpnlProgramming != null) {
//			cntntpnlProgramming.awaken();
//		}
		if (!cntntpnlCacheControl.isCollapsed() && cacheControlPanel != null) {
			cacheControlPanel.awaken();
		}
	}
	
}
