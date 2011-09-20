package com.scholastic.sbam.client.uiobjects.uitop;

import java.util.List;

import com.extjs.gxt.ui.client.widget.Composite;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.scholastic.sbam.client.uiobjects.foundation.AppSecurityManager;
import com.scholastic.sbam.client.uiobjects.foundation.AppSleeper;
import com.scholastic.sbam.client.uiobjects.uiprofile.ProfilePanel;
import com.scholastic.sbam.client.util.IconSupplier;

/**
 * An outer layer that can be easily expanded to contain multiple elements using an Accordion or Tab layout.
 * 
 * For now, there's just one panel.
 * 
 * @author Bob Lacatena
 *
 */
public class ProfileUi extends Composite implements AppSecurityManager, AppSleeper {
	private ContentPanel cntntpnlProfile;
	
	private ProfilePanel				profilePanel;

	public ProfileUi() {
		
		LayoutContainer layoutContainer = new LayoutContainer();
		layoutContainer.setLayout(new FitLayout());
//		layoutContainer.setLayout(new AccordionLayout());
		
		/*
		 * Basic Profile edit
		 */
		
		cntntpnlProfile = new ContentPanel(new FitLayout());
		cntntpnlProfile.setHeading("Profile");
//		cntntpnlProfile.setCollapsible(true);
		IconSupplier.setIcon(cntntpnlProfile, IconSupplier.getProfileIconName());
		
		profilePanel = new ProfilePanel();
		cntntpnlProfile.add(profilePanel);

		layoutContainer.add(cntntpnlProfile);

		/*
		 * Finish up
		 */
		
		initComponent(layoutContainer);
		layoutContainer.setBorders(true);
	}

	public void applyRoles(List<String> roleNames) {
	}
	
	public void sleep() {
		if (profilePanel != null) 			profilePanel.sleep();
	}
	
	public void awaken() {
		if (!cntntpnlProfile.isCollapsed() && profilePanel != null) {
			profilePanel.awaken();
		}
	}
	
}
