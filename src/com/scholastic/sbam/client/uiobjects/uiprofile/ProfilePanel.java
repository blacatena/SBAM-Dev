package com.scholastic.sbam.client.uiobjects.uiprofile;

import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.scholastic.sbam.client.services.UpdateUserPasswordService;
import com.scholastic.sbam.client.services.UpdateUserPasswordServiceAsync;
import com.scholastic.sbam.client.uiobjects.foundation.AppSleeper;
import com.scholastic.sbam.client.util.IconSupplier;

public class ProfilePanel extends LayoutContainer implements AppSleeper {
	
	private final UpdateUserPasswordServiceAsync	updateUserPasswordService = GWT.create(UpdateUserPasswordService.class);
	
	protected Button				changeButton;
	protected TextField<String>		oldPasswordField;
	protected TextField<String>		passwordField;
	protected TextField<String>		passwordConfirm;
	
	Timer		dirtyListenTimer;
	
	@Override
	public void onRender(Element element, int index) {
		super.onRender(element, index);
		
		TableLayout layout = new TableLayout(2);
		layout.setCellPadding(10);
		layout.setWidth("500px");
		setLayout(layout);

		addStyleName("subtle-form");
		
		changeButton = new Button("Change Password") {
			@Override
			public void onClick(ComponentEvent ce) {
				changePassword();
			}
		};
		changeButton.disable();
		IconSupplier.forceIcon(changeButton, IconSupplier.getCheckedIconName());
		

		LabelField oldPasswordLabel = new LabelField("Old Password:");
		oldPasswordLabel.setWidth(150);
		oldPasswordLabel.setStyleAttribute("padding-left", "10px");
		oldPasswordLabel.setStyleAttribute("padding-right", "10px");
		
		oldPasswordField = new TextField<String>();
		oldPasswordField.setPassword(true);
		

		LabelField passwordLabel = new LabelField("New Password:");
		passwordLabel.setWidth(150);
		passwordLabel.setStyleAttribute("padding-left", "10px");
		passwordLabel.setStyleAttribute("padding-right", "10px");
		
		passwordField = new TextField<String>();
		passwordField.setPassword(true);
		

		LabelField confirmLabel = new LabelField("Confirm:");
		confirmLabel.setWidth(150);
		confirmLabel.setStyleAttribute("padding-left", "10px");
		confirmLabel.setStyleAttribute("padding-right", "10px");
		
		passwordConfirm = new TextField<String>();
		passwordConfirm.setPassword(true);
		
		createDirtyListenTimer();
		
		add(oldPasswordLabel);
		add(oldPasswordField);
		
		add(passwordLabel);
		add(passwordField);
		
		add(confirmLabel);
		add(passwordConfirm);
		
		add(new Html(""));
		add(changeButton);
	}
	
	protected void createDirtyListenTimer() {

		dirtyListenTimer = new Timer() {
			  @Override
			  public void run() {
			    	validatePasswordFields();
			  }
			};

		dirtyListenTimer.scheduleRepeating(200);
	}
	
	protected void changePassword() {
		mask("Changing password...");

		updateUserPasswordService.updateUserPassword(
					"",
					oldPasswordField.getValue(),
					passwordField.getValue(),
					new AsyncCallback<String>() {
					
						public void onFailure(Throwable caught) {
							unmask();
							// Show the RPC error message to the user
							if (caught instanceof IllegalArgumentException)
								MessageBox.alert("Alert", caught.getMessage(), null);
							else {
								MessageBox.alert("Alert", "Export initiation failed unexpectedly.", null);
								System.out.println(caught.getClass().getName());
								System.out.println(caught.getMessage());
							}
					}

					public void onSuccess(String message) {
						oldPasswordField.clear();
						passwordField.clear();
						passwordConfirm.clear();
						changeButton.disable();
						unmask();
						MessageBox.alert("Attention", message, null);
					}
			});
	}
	
	protected void validatePasswordFields() {
		boolean valid = false;
		
		if (oldPasswordField.getValue() == null
		&&  passwordField.getValue() == null
		&&	passwordConfirm.getValue() == null) {
			oldPasswordField.clearInvalid();
			passwordField.clearInvalid();
			passwordConfirm.clearInvalid();
		} else {
			valid = true;
			if (passwordField.getValue() == null || passwordField.getValue().length() < 6) {
				passwordField.forceInvalid("A password must be at least 6 characters long.");
				valid=false;
			} else {
				passwordField.clearInvalid();
			}
			if (passwordConfirm.getValue() == null) {
				passwordConfirm.forceInvalid("Re-enter your new password.");
				valid=false;
			} else if (!passwordConfirm.getValue().equals(passwordField.getValue())) {
				passwordField.forceInvalid("The password and confirmation value do not match.");
				passwordConfirm.forceInvalid("The password and confirmation value do not match.");
				valid=false;
			} else {
				passwordConfirm.clearInvalid();
			}
			if (oldPasswordField.getValue() == null || oldPasswordField.getValue().length() < 6) {
				oldPasswordField.forceInvalid("Enter your old password.");
				valid=false;
			} else {
				oldPasswordField.clearInvalid();
			}
		}
		
		changeButton.setEnabled(valid);
	}
	
	@Override
	public void awaken() {
		if (dirtyListenTimer != null)
			dirtyListenTimer.scheduleRepeating(250);
	}

	@Override
	public void sleep() {
		if (dirtyListenTimer != null) {
			dirtyListenTimer.cancel();
		}
	}
}
