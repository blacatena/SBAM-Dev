package com.scholastic.sbam.client.uiobjects;

import java.util.List;

//import com.extjs.gxt.ui.client.event.ComponentEvent;
//import com.extjs.gxt.ui.client.event.Events;
//import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.Composite;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.scholastic.sbam.client.services.WelcomeMessageService;
import com.scholastic.sbam.client.services.WelcomeMessageServiceAsync;
import com.scholastic.sbam.shared.security.SecurityManager;

public class AppNav extends Composite implements AppSecurityManager {
	private TabPanel tabPanel;
	private TabItem tbtmAdministration;
	private TabItem tbtmWelcome;
	private Html htmlWelcome;
	private Html htmlMessages;
	private Html htmlLoading;
	private TabItem tbtmReports;
	private TabItem tbtmAgreements;
	private AdminUi adminUi;

	public AppNav() {
		
		tabPanel = new TabPanel();
		
		tbtmWelcome = new TabItem("Welcome");
		
		htmlWelcome = new Html("<h2><i><b>Welcome</b></i> to the Scholastic Site Based Authentication Management System (SBAM).</h2><hr/>");
		tbtmWelcome.add(htmlWelcome);
		
//		tbtmWelcome.addListener(Events.Select, new Listener<ComponentEvent>() {  
//			public void handleEvent(ComponentEvent be) {
//				System.out.println("Welcome Tab Selected");  
//			}  
//		}); 
		
		htmlMessages = new Html("");
		tbtmWelcome.add(htmlMessages);
		
		htmlLoading = new Html("<i>Loading welcome messages</i>");
		htmlLoading.addStyleName("loading-indicator");
		tbtmWelcome.add(htmlLoading);
		
		tabPanel.add(tbtmWelcome);
		
		tbtmAgreements = new TabItem("Agreements");
		tabPanel.add(tbtmAgreements);
		
		tbtmReports = new TabItem("Reports");
		tabPanel.add(tbtmReports);
		
		tbtmAdministration = new TabItem("Administration");
		tbtmAdministration.setLayout(new FitLayout());
		
		adminUi = new AdminUi();
		tbtmAdministration.add(adminUi);
		tabPanel.add(tbtmAdministration);
		
		initComponent(tabPanel);
		
		applyRoles(SecurityManager.NO_ROLES);
		loadWelcomeMessages();
	}
	
	public void loadWelcomeMessages() {

		final WelcomeMessageServiceAsync welcomeMessageService = GWT.create(WelcomeMessageService.class);

		
		welcomeMessageService.getWelcomeMessages(
			new AsyncCallback<String>() {
				public void onFailure(Throwable caught) {
					// Show the RPC error message to the user
					htmlMessages.setHtml("Message load failed.");
					htmlLoading.setVisible(false);
				}

				public void onSuccess(String result) {
					htmlMessages.setHtml(result);
					htmlLoading.setVisible(false);
				}
			});
		
	}
	
	public void setLoggedOut() {
		tabPanel.setSelection(tbtmWelcome);
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

	public Html getHtmlwelcomeToThe() {
		return htmlWelcome;
	}

	public void setHtmlwelcomeToThe(Html htmlwelcomeToThe) {
		this.htmlWelcome = htmlwelcomeToThe;
	}

}
