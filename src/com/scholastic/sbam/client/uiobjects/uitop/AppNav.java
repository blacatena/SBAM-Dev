package com.scholastic.sbam.client.uiobjects.uitop;

import java.util.List;

import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.Composite;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.scholastic.sbam.client.uiobjects.foundation.AppSecurityManager;
import com.scholastic.sbam.client.uiobjects.foundation.AppSleeper;
import com.scholastic.sbam.client.uiobjects.uiapp.AppWorkSpace;
import com.scholastic.sbam.client.util.IconSupplier;
import com.scholastic.sbam.client.util.UiConstants;
import com.scholastic.sbam.shared.objects.Authentication;
import com.scholastic.sbam.shared.security.SecurityManager;

public class AppNav extends Composite implements AppSecurityManager {
	protected TabPanel			tabPanel;
	protected TabItem			tbtmAdministration;
	protected TabItem			tbtmWelcome;
	protected TabItem			tbtmConfiguration;
	protected TabItem			tbtmReports;
	protected TabItem			tbtmAgreements;
	protected TabItem			tbtmProfile;
	protected AdminUi			adminUi;
	protected ReportsUi			reportsUi;
	protected ConfigUi			configUi;
	protected ProfileUi			profileUi;
	protected WelcomeDisplay	welcomeDisplay;
	protected AppWorkSpace		appPortal;

	public AppNav() {
		
		tabPanel = new TabPanel();
		
		
		
		tbtmWelcome = new TabItem("Welcome");
		IconSupplier.setIcon(tbtmWelcome, IconSupplier.getWelcomeIconName());
		tbtmWelcome.getHeader().setToolTip(UiConstants.getQuickTip("Use the Welcome tab to see important user and system messages."));
		
		welcomeDisplay = new WelcomeDisplay();
		tbtmWelcome.add(welcomeDisplay);
		
		tabPanel.add(tbtmWelcome);
	
		
		
		tbtmAgreements = new TabItem("Agreements");
		tbtmAgreements.setLayout(new FitLayout());
		IconSupplier.setIcon(tbtmAgreements, IconSupplier.getAgreementIconName());
		tbtmAgreements.getHeader().setToolTip(UiConstants.getQuickTip("Use the Agreements tab to review and maintain agreements."));
		
		appPortal = new AppWorkSpace();
		tbtmAgreements.add(appPortal);
		
		tabPanel.add(tbtmAgreements);
		
		
		
		tbtmConfiguration = new TabItem("Configuration");
		tbtmConfiguration.setLayout(new FitLayout());
		IconSupplier.setIcon(tbtmConfiguration, IconSupplier.getConfigurationIconName());
		tbtmConfiguration.getHeader().setToolTip(UiConstants.getQuickTip("Use the Configuration tab to maintain system wide configuration data and tables."));

		configUi = new ConfigUi();
		tbtmConfiguration.add(configUi);
		
		tabPanel.add(tbtmConfiguration);
		
		
		
		tbtmReports = new TabItem("Reports");
		IconSupplier.setIcon(tbtmReports, IconSupplier.getReportIconName());
		tbtmReports.getHeader().setToolTip(UiConstants.getQuickTip("Use the Reports tab to query, summarize, aggregate, graph and report on data."));
		tbtmReports.setLayout(new FitLayout());

		reportsUi = new ReportsUi();
		tbtmReports.add(reportsUi);
		
		tabPanel.add(tbtmReports);
		
		
		
		tbtmAdministration = new TabItem("Administration");
		IconSupplier.setIcon(tbtmAdministration, IconSupplier.getAdministrationIconName());
		tbtmAdministration.getHeader().setToolTip(UiConstants.getQuickTip("Use the Administration tab to perform system management tasks."));
		tbtmAdministration.setLayout(new FitLayout());
		
		adminUi = new AdminUi();
		tbtmAdministration.add(adminUi);
		
		tabPanel.add(tbtmAdministration);

		
		
		tbtmProfile = new TabItem("Profile");
		IconSupplier.setIcon(tbtmProfile, IconSupplier.getProfileIconName());
		tbtmProfile.getHeader().setToolTip(UiConstants.getQuickTip("Use the Profile tab to modify your personal profile."));
		tbtmProfile.setLayout(new FitLayout());

		profileUi = new ProfileUi();
		tbtmProfile.add(profileUi);
		
		tabPanel.add(tbtmProfile);
		
		
		
		addSleepListeners();
		
		initComponent(tabPanel);
		
		applyRoles(SecurityManager.NO_ROLES);
	//	loadWelcomeMessages();
	}
	
	/**
	 * Create listeners for tabs that must be notified when put to sleep.
	 */
	private void addSleepListeners() {
	
		tbtmWelcome.addListener(Events.Select, new Listener<ComponentEvent>() {  
			public void handleEvent(ComponentEvent be) {
				sleepOthers(tbtmWelcome);
				awaken(welcomeDisplay);
			}  
		}); 
	
		tbtmAdministration.addListener(Events.Select, new Listener<ComponentEvent>() {  
			public void handleEvent(ComponentEvent be) {
				sleepOthers(tbtmAdministration);
				awaken(adminUi);
			}  
		}); 
	
		tbtmConfiguration.addListener(Events.Select, new Listener<ComponentEvent>() {  
			public void handleEvent(ComponentEvent be) {
				sleepOthers(tbtmConfiguration);
				awaken(configUi);
			}  
		}); 
	
		tbtmAgreements.addListener(Events.Select, new Listener<ComponentEvent>() {  
			public void handleEvent(ComponentEvent be) {
				sleepOthers(tbtmAgreements);
				awaken(appPortal);
			}  
		}); 
	
		tbtmReports.addListener(Events.Select, new Listener<ComponentEvent>() {  
			public void handleEvent(ComponentEvent be) {
				sleepOthers(tbtmReports);
			}  
		}); 
	}
	
	protected void awaken(AppSleeper target) {
		target.awaken();
	}
	
	/**
	 * Put other tabs and tab components to sleep.
	 * @param exception
	 *  The tab which will stay awake.
	 */
	protected void sleepOthers(TabItem exception) {
		if (exception != tbtmAdministration)
			adminUi.sleep();
		if (exception != tbtmConfiguration)
			configUi.sleep();
		if (exception != tbtmWelcome)
			welcomeDisplay.sleep();
	}
	
	/**
	 * On log in, reset things for the user's roles, and load his cached portlets
	 * @param roleNames
	 */
	public void setLoggedIn(Authentication auth) {
		applyRoles(auth.getRoleNames());
		appPortal.setLoggedIn(auth);
	}
	
	/**
	 * On log out, remove all portlets, and reset security to "NONE"
	 */
	public void setLoggedOut() {
		tabPanel.setSelection(tbtmWelcome);
		appPortal.setLoggedOut();
		applyRoles(SecurityManager.NO_ROLES);
	}

	public void applyRoles(List<String> roleNames) {
		if (roleNames.contains(SecurityManager.ROLE_ADMIN))
			tbtmAdministration.enable();
		else
			tbtmAdministration.disable();

		if (roleNames.contains(SecurityManager.ROLE_MAINT))
			tbtmAgreements.enable();
		else
			tbtmAgreements.disable();

		if (roleNames.contains(SecurityManager.ROLE_CONFIG))
			tbtmConfiguration.enable();
		else
			tbtmConfiguration.disable();
		
		if (roleNames.contains(SecurityManager.ROLE_QUERY))
			tbtmReports.enable();
		else
			tbtmReports.disable();
	}

	public TabItem getTbtmAdministration() {
		return tbtmAdministration;
	}

	public void setTbtmAdministration(TabItem tbtmAdministration) {
		this.tbtmAdministration = tbtmAdministration;
	}

	public TabItem getTbtmWelcome() {
		return tbtmWelcome;
	}

	public void setTbtmWelcome(TabItem tbtmWelcome) {
		this.tbtmWelcome = tbtmWelcome;
	}

	public TabItem getTbtmConfiguration() {
		return tbtmConfiguration;
	}

	public void setTbtmConfiguration(TabItem tbtmConfiguration) {
		this.tbtmConfiguration = tbtmConfiguration;
	}

	public TabItem getTbtmReports() {
		return tbtmReports;
	}

	public void setTbtmReports(TabItem tbtmReports) {
		this.tbtmReports = tbtmReports;
	}

	public TabItem getTbtmAgreements() {
		return tbtmAgreements;
	}

	public void setTbtmAgreements(TabItem tbtmAgreements) {
		this.tbtmAgreements = tbtmAgreements;
	}

}
