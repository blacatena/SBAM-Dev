package com.scholastic.sbam.client.uiobjects;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.KeyListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
//import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.Status;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.scholastic.sbam.client.util.IconSupplier;

/**
 * A log in dialog to accept a user name and password, and shield the application from use until validated.
 * 
 * This class has been trivially modified to extend EffectsDialog, rather than the usual GXT Dialog, so that animate can more easily be applied.
 * 
 * To revert to the simpler, more stable, un-animated version, simple change the class inheritance to extend com.extjs.gxt.ui.client.widget.Dialog.
 * 
 * @author Bob Lacatena
 *
 */
public class LoginDialog extends EffectsDialog {

	  protected TextField<String> userName;
	  protected TextField<String> password;
	  protected Button reset;
	  protected Button login;
	  protected Status status;

	  public LoginDialog() {
	    FormLayout layout = new FormLayout();
	    layout.setLabelWidth(90);
	    layout.setDefaultWidth(155);
	    setLayout(layout);
	    
//	    setAnimCollapse(true);
	    setButtonAlign(HorizontalAlignment.LEFT);
	    setButtons("");
	    setIcon(IconSupplier.getHeaderIcon(IconSupplier.getLoginIconName()));
	    setHeading("SBAM Login");
	    setModal(true);
	    setBodyBorder(true);
	    setBodyStyle("padding: 8px;background: none");
	    setWidth(300);
	    setResizable(false);
	    setClosable(false);
	    setShadow(true);
	    setShadowOffset(getShadowOffset() + (getShadowOffset() / 2));

	    KeyListener keyListener = new KeyListener() {
	      public void componentKeyUp(ComponentEvent event) {
	        validate();
	      }

	    };

	    userName = new TextField<String>();
	    userName.setMinLength(4);
	    userName.setFieldLabel("Username");
	    userName.addKeyListener(keyListener);
	    add(userName);

	    password = new TextField<String>();
	    password.setMinLength(4);
	    password.setPassword(true);
	    password.setFieldLabel("Password");
	    password.addKeyListener(keyListener);
	    add(password);

	    setFocusWidget(userName);

	  }

	  @Override
	  protected void createButtons() {
	    super.createButtons();
	    status = new Status();
	    status.setBusy("validating...");
	    status.hide();
	    status.setAutoWidth(true);
	    getButtonBar().add(status);
	    
	    getButtonBar().add(new FillToolItem());
	    
	    reset = new Button("Reset");
	    reset.addSelectionListener(new SelectionListener<ButtonEvent>() {
	      public void componentSelected(ButtonEvent ce) {
	        userName.reset();
	        password.reset();
	        status.clearStatus("");
	        validate();
	        userName.focus();
	      }

	    });

	    login = new Button("Login");
	    login.disable();
	    //	Selection listener must be added by the LoginUIManager

	    addButton(reset);
	    addButton(login);

	    
	  }
	  
	  public void show(boolean animate) {
		  super.show(animate);
	  }

	  protected boolean hasValue(TextField<String> field) {
	    return field.getValue() != null && field.getValue().length() > 0;
	  }

	  protected void validate() {
	    login.setEnabled(hasValue(userName) && hasValue(password)
	        && password.getValue().length() > 3);
	  }

		public TextField<String> getUserName() {
			return userName;
		}
	
		public TextField<String> getPassword() {
			return password;
		}
	
		public Button getLogin() {
			return login;
		}
	  
	}
