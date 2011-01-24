package com.scholastic.sbam.client.uiobjects;


import java.util.HashSet;
import java.util.Set;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
//import com.extjs.gxt.ui.client.fx.FxConfig;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.scholastic.sbam.client.services.AuthenticateService;
import com.scholastic.sbam.client.services.AuthenticateServiceAsync;
import com.scholastic.sbam.client.services.DeauthenticateService;
import com.scholastic.sbam.client.services.DeauthenticateServiceAsync;
import com.scholastic.sbam.client.services.UserMessageService;
import com.scholastic.sbam.client.services.UserMessageServiceAsync;
import com.scholastic.sbam.client.util.UiConstants;
import com.scholastic.sbam.shared.objects.Authentication;
import com.scholastic.sbam.shared.objects.UserMessageCollection;
import com.scholastic.sbam.shared.objects.UserMessageInstance;
import com.scholastic.sbam.shared.security.SecurityManager;

public class LoginUiManager {
	
	private static final String	DISPLAY_NOT_LOGGED_IN = "<B><I>Please log in.</I></B>";

	private boolean			loggedIn = false;
	private String			loggedInUserName;
	
	//	The Application (to apply security changes on login)
	private AppNav			theApp;
	
	//	Log In Dialog form/field elements
	private LoginDialog 	loginBox;
	
	private final AuthenticateServiceAsync authenticateService = GWT.create(AuthenticateService.class);

	//	Logged in form/field elements
	private LoggedInPanel	loggedInPanel	= new LoggedInPanel();
	private Html			displayName		= loggedInPanel.getDisplayName();
	private Button			logoutButton	= loggedInPanel.getButtonLogOut();
	
	//	Registry of sticky note windows
	Set<StickyNoteWindow> stickyNotes = new HashSet<StickyNoteWindow>();
	
	private final DeauthenticateServiceAsync deauthenticateService = GWT.create(DeauthenticateService.class);
	
	private final UserMessageServiceAsync userMessageService = GWT.create(UserMessageService.class);
	
	public LoginUiManager() {
		init();
	}
	
	public LoginUiManager(AppNav theApp) {
		this.theApp = theApp;
		init();
	}
	
	private void init() {
		//	Prepare the logout button
		logoutButton.setVisible(false);
		logoutButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
		    public void componentSelected(ButtonEvent ce) {
		    	deauthenticate();
			}
		});
		
	    // Create the dialog box
	    
		loginBox = createLoginBox();
	}
	
	public void show() {
	//	loginBox.center();
	//	loginBox.el().fadeToggle(FxConfig.NONE);
		loginBox.show();
	}
	
	public LoginDialog getLoginDialog() {
		return loginBox;
	}
	
	public boolean isLoggedIn() {
		return loggedIn;
	}

	private LoginDialog createLoginBox() {
		//	Set the user to not logged in
		setLoggedOut();
		
		//	Create the box.
		LoginDialog box = new LoginDialog();
		box.setVisible(false);
		box.ensureDebugId("loginBox");
		
		box.getLogin().addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
		    public void componentSelected(ButtonEvent ce) {
		    	authenticate();
			}
		});

	    return box;
	}
	
	private void authenticate() {
		String userName = loginBox.getUserName().getValue();
		String password = loginBox.getPassword().getValue();
		
		if (userName == null || userName.isEmpty()) {
			loginBox.getUserName().forceInvalid("Please enter a user name and password.");
			return;
		}
		
		authenticateService.authenticate(userName, password,
			new AsyncCallback<Authentication>() {
				public void onFailure(Throwable caught) {
					// Show the RPC error message to the user
					loginBox.getUserName().forceInvalid("An unexpected error occurred in processing.");
				}

				public void onSuccess(Authentication auth) {
					loggedIn = auth.isAuthenticated();
					if (loggedIn) {
						loggedInUserName = auth.getUserName();
						//	Blank out the previous password, so it can't be stolen
						loginBox.getPassword().setValue("");
						loginBox.status.clearStatus("");
						loginBox.status.hide();
						//  Give them what they deserve
						applyRoles(auth);
						//	Show who's logged on, and show the logout button
						setLoggedIn(auth.getDisplayName());
						//	Hide the login dialog
					//	loginBox.el().fadeOut(FxConfig.NONE);
						loginBox.hide();
						loadUserMessages();
					} else {
						loggedInUserName = null;
						noRoles();
						loginBox.getUserName().forceInvalid(auth.getMessage());
						loginBox.status.setStatus("Failed.", "x-status-fail");
						loginBox.status.show();
						setLoggedOut();
					//	loginBox.show();
					}
				}
			});
	}
	
	private void deauthenticate() {
		deauthenticateService.deauthenticate(new AsyncCallback<String>() {
				public void onFailure(Throwable caught) {
					// Show the RPC error message to the user
					System.out.println("An unexpected error occurred in processing.");
				}

				public void onSuccess(String result) {
					setLoggedOut();
					if (theApp != null)
						theApp.setLoggedOut();
					loginBox.status.clearStatus("");
					loginBox.userName.clearInvalid();
					loginBox.password.clearInvalid();
				//	loginBox.el().fadeIn(FxConfig.NONE);
					loginBox.show();
				}
			});
	}

	private void loadUserMessages() {
		userMessageService.getUserMessages("", 
				new AsyncCallback<UserMessageCollection>() {
					public void onFailure(Throwable caught) {
						// Show the RPC error message to the user
						MessageBox.alert("Alert", "Notes load failed unexpectedly.", null);
					}
		
					public void onSuccess(UserMessageCollection messages) {
						for (UserMessageInstance message : messages.getUserMessages()) {
							createStickyNote(message);
						}
					}
		});
	}
	
	public void noRoles() {
		if (theApp != null)
			theApp.applyRoles(SecurityManager.NO_ROLES);
	}
	
	public void applyRoles(Authentication auth) {
		if (theApp != null)
			theApp.applyRoles(auth.getRoleNames());
	}
	
	public void setLoggedOut() {
		//	Cancel periodic events
		UiConstants.cancel();
		
		//	Clear away any sticky notes
		destroyStickyNotes();
		
		//	Clear the logged in panel values
		loggedInUserName = null;
		if (displayName != null)
			displayName.setHtml(DISPLAY_NOT_LOGGED_IN);
		if (logoutButton != null)
			logoutButton.setVisible(false);
	}
	
	public void setLoggedIn(String loggedInName) {		
		//	Initialize application UI constants
		UiConstants.init();
		
		//	Set logged in UI controls and values
		if (displayName != null)
			displayName.setHtml(loggedInName);
		if (logoutButton != null)
			logoutButton.setVisible(true);
	}
	
	public void createStickyNote(UserMessageInstance message) {
		StickyNoteWindow window = new StickyNoteWindow(message);
		stickyNotes.add(window);
//		window.layout(true);
//		window.el().fadeIn(FxConfig.NONE);
		window.show();
	}
	
	public void createStickyNote(String locationTag) {
		StickyNoteWindow window = new StickyNoteWindow(loggedInUserName, locationTag);
		stickyNotes.add(window);
//		window.layout(true);
//		window.el().fadeIn(FxConfig.NONE);
		window.show();
	}
	
	public void destroyStickyNotes() {
		for (StickyNoteWindow window : stickyNotes) {
		//	window.el().fadeOut(FxConfig.NONE);
			window.removeFromParent();
		}
	}

	public String getLoggedInUserName() {
		return loggedInUserName;
	}

	public void setLoggedInUserName(String loggedInUserName) {
		this.loggedInUserName = loggedInUserName;
	}

	public LoggedInPanel getLoggedInPanel() {
		return loggedInPanel;
	}

	public void setPanel(LoggedInPanel loggedInPanel) {
		this.loggedInPanel = loggedInPanel;
	}

	public Html getDisplayName() {
		return displayName;
	}

	public void setDisplayName(Html displayName) {
		this.displayName = displayName;
	}

	public Button getLogoutButton() {
		return logoutButton;
	}

	public void setLogoutButton(Button logoutButton) {
		this.logoutButton = logoutButton;
	}

	public LoginDialog getLoginBox() {
		return loginBox;
	}

	public void setLoginBox(LoginDialog loginBox) {
		this.loginBox = loginBox;
	}

	public TextField<String> getUserNameField() {
		return loginBox.getUserName();
	}

	public TextField<String> getPasswordField() {
		return loginBox.getPassword();
	}

	public void setLoggedIn(boolean loggedIn) {
		this.loggedIn = loggedIn;
	}

	public AppNav getTheApp() {
		return theApp;
	}

	public void setTheApp(AppNav theApp) {
		this.theApp = theApp;
	}

}
