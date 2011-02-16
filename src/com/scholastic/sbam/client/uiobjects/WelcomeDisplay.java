package com.scholastic.sbam.client.uiobjects;

import java.util.List;

import com.extjs.gxt.ui.client.widget.Composite;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.scholastic.sbam.client.services.WelcomeMessageService;
import com.scholastic.sbam.client.services.WelcomeMessageServiceAsync;

public class WelcomeDisplay extends Composite implements AppSecurityManager, AppSleeper {
	
	private LayoutContainer container;
	private Html 			htmlWelcome;
	private Html 			htmlMessages;
	private Html 			htmlLoading;

	private final WelcomeMessageServiceAsync welcomeMessageService = GWT.create(WelcomeMessageService.class);

	public WelcomeDisplay() {
		container = new LayoutContainer();
		
		htmlWelcome = new Html("<h2><i><b>Welcome</b></i> to the Scholastic Site Based Authentication Management System (SBAM).</h2><hr/>");
		container.add(htmlWelcome);
		
		htmlMessages = new Html("");
		container.add(htmlMessages);
		
		htmlLoading = new Html("<i>Loading welcome messages</i>");
		htmlLoading.addStyleName("loading-indicator");
		container.add(htmlLoading);

		initComponent(container);
		loadWelcomeMessages();
	}
	
	/**
	 * Load any current welcome messages.
	 */
	public void loadWelcomeMessages() {
		if (htmlLoading != null)
			htmlLoading.setVisible(true);

		welcomeMessageService.getWelcomeMessages(
			new AsyncCallback<String>() {
				public void onFailure(Throwable caught) {
					// Show the RPC error message to the user
					if (htmlMessages != null) 
						htmlMessages.setHtml("Message load failed.");
					if (htmlLoading != null)
						htmlLoading.setVisible(false);
				}

				public void onSuccess(String result) {
					if (htmlMessages != null) 
						htmlMessages.setHtml(result);
					if (htmlLoading != null)
						htmlLoading.setVisible(false);
				}
			});
		
	}

	@Override
	public void awaken() {
		loadWelcomeMessages();
	}

	@Override
	public void sleep() {	
	}

	@Override
	public void applyRoles(List<String> roleNames) {
	}

}
