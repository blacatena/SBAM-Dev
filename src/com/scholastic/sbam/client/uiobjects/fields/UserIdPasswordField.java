package com.scholastic.sbam.client.uiobjects.fields;

import java.util.List;

import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.Style.HideMode;
import com.extjs.gxt.ui.client.core.XDOM;
import com.extjs.gxt.ui.client.widget.BoxComponent;
import com.extjs.gxt.ui.client.widget.ComponentHelper;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.WidgetComponent;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.MultiField;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Image;
import com.scholastic.sbam.client.services.UidValidationService;
import com.scholastic.sbam.client.services.UidValidationServiceAsync;
import com.scholastic.sbam.client.util.IconSupplier;
import com.scholastic.sbam.shared.objects.AuthMethodInstance;
import com.scholastic.sbam.shared.objects.MethodIdInstance;
import com.scholastic.sbam.shared.validation.AsyncValidationResponse;

@SuppressWarnings("deprecation")
public class UserIdPasswordField extends MultiField<String []> {
	public final String	USER_ID_LABEL			= "User&nbsp;ID:&nbsp;";
	public final String PASSWORD_LABEL			= "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Password:&nbsp;";
	public final String USER_TYPE_LABEL			= "Type:&nbsp;";
	public final int	DEFAULT_LABEL_WIDTH		=	50;
	public final int	DEFAULT_FIELD_WIDTH		=	100;
	public final int	DEFAULT_MIN_VAL_LEN		=	2;

	protected LabelField		iconPositionField;
	protected LabelField		userIdLabelField;
	protected TextField<String>	userIdField;
	protected LabelField		passwordLabelField;
	protected TextField<String>	passwordField;
	
	protected LabelField		userTypeSpacerField;
	protected LabelField		userTypeLabelField;
	protected RadioGroup		userTypeGroup;
	protected Radio				cookieCheckBox;
	protected Radio				permanentCheckBox;

	protected ProxySearchField	proxyField;
	
	protected int				validationCounter		= 0;
	protected String			lastUidValidated		= "";
	protected String			lastPasswordValidated	= "";
	protected char				lastUserTypeValidated	= 0;
	protected int				lastProxyIdValidated	= 0;
	protected MethodIdInstance	lastMethodId			= MethodIdInstance.getEmptyInstance();
	protected List<String>		asyncMessages			= null;
	protected List<String>		asyncAlertMessages		= null;
	protected List<String>		asyncInfoMessages		= null;
	protected MethodIdInstance	methodId				= MethodIdInstance.getEmptyInstance();
	

	protected WidgetComponent	infoIcon;
	protected String			infoStyle	= "x-form-info";
	protected String			alertStyle	= "x-form-info-alert";
	protected String			activeInfoMessage;
	
	protected UidValidationServiceAsync validationService = GWT.create(UidValidationService.class);
	
	public UserIdPasswordField() {
		this("", true);
	}
	
	public UserIdPasswordField(String label) {
		this(label, true);
	}
	
	public UserIdPasswordField(String label, boolean includeUserType) {
		super(label);
		createFields();
		if (includeUserType) {
			createUserTypeGroup();
		}
	}
	
	protected void createFields() {
		iconPositionField = new ConstantLabelField();
		iconPositionField.setValue("");
		iconPositionField.setWidth(20);
		
		userIdField = new TextField<String>();
		passwordField = new TextField<String>();
		userIdLabelField = new ConstantLabelField();
		passwordLabelField = new ConstantLabelField();
		userIdLabelField.setValue(USER_ID_LABEL);
		passwordLabelField.setValue(PASSWORD_LABEL);
		
		userIdField.setAllowBlank(false);
		passwordField.setAllowBlank(false);
		userIdField.setMinLength(DEFAULT_MIN_VAL_LEN);
		passwordField.setMinLength(DEFAULT_MIN_VAL_LEN);
		
		setUserIdWidth(DEFAULT_FIELD_WIDTH);
		setPasswordWidth(DEFAULT_FIELD_WIDTH);

		userIdLabelField.setStyleName("x-form-item-label");
		userIdLabelField.setWidth(60);
		passwordLabelField.setStyleName("x-form-item-label");
		passwordLabelField.setWidth(75);
		
		add(iconPositionField);
		add(userIdLabelField);
		add(userIdField);
		add(passwordLabelField);
		add(passwordField);
	}
	
	protected void createUserTypeGroup() {
		userTypeGroup = new RadioGroup();
		cookieCheckBox = new Radio();
		permanentCheckBox = new Radio();
		
		cookieCheckBox.setBoxLabel("Cookie");
		permanentCheckBox.setBoxLabel("Permanent");
		
		userTypeGroup.add(cookieCheckBox);
		userTypeGroup.add(permanentCheckBox);
		
		userTypeSpacerField = new ConstantLabelField();
		userTypeSpacerField.setValue("&nbsp;");
		userTypeSpacerField.setWidth(30);
		
		userTypeLabelField = new ConstantLabelField();
		userTypeLabelField.setValue(USER_TYPE_LABEL);
		
		add(userTypeSpacerField);
		add(userTypeLabelField);
		add(userTypeGroup);
	}
	
	protected void setUserIdLabel(String label) {
		userIdLabelField.setValue(label);
	}
	
	protected void setPasswordLabel(String label) {
		passwordLabelField.setValue(label);
	}
	
	protected void setUserTypeLabel(String label) {
		userTypeLabelField.setValue(label);
	}
	
	protected void setUserIdWidth(int width) {
		userIdField.setWidth(width);
	}
	
	protected void setPasswordWidth(int width) {
		passwordField.setWidth(width);
	}
	
	protected void setUserIdLabelWidth(int width) {
		userIdLabelField.setWidth(width);
	}
	
	protected void setPasswordLabelWidth(int width) {
		passwordLabelField.setWidth(width);
	}
	
	protected void setUserTypeSpacerWidth(int width) {
		userTypeSpacerField.setWidth(width);
	}
	
	protected void setUserTypeLabelWidth(int width) {
		userTypeLabelField.setWidth(width);
	}
	
	public void addStyleName(String styleName) {
		userIdField.addStyleName(styleName);
		passwordField.addStyleName(styleName);
		if (userTypeGroup != null) {
			userTypeGroup.addStyleName(styleName);
			cookieCheckBox.addStyleName(styleName);
			permanentCheckBox.addStyleName(styleName);
		}
	}
	
	@Override
	public String [] getValue() {
		return new String [] {getUserId(), getPassword(), getUserTypeString()};
	}
	
	@Override
	public void setValue(String [] values) {
		super.setValue(values);
		userIdField.setValue(values [0]);
		passwordField.setValue(values [1]);
		if (userTypeGroup != null && values.length > 2) {
			if (AuthMethodInstance.UserTypes.COOKIE.getCode().equals(values [2]))
				cookieCheckBox.setValue(true);
			else
				permanentCheckBox.setValue(true);
		}
	}
	
	public void setValue(String userId, String password) {
		setValue(new String [] {userId, password});
	}
	
	public void setValue(String userId, String password, String userType) {
		setValue(new String [] {userId, password, userType});
	}
	
	public void setValue(String userId, String password, char userType) {
		setValue(new String [] {userId, password, userType + ""});
	}
	
	public String getUserId() {
		return userIdField.getValue();
	}
	
	public String getPassword() {
		return passwordField.getValue();
	}
	
	public String getUserTypeString() {
		if (cookieCheckBox == null)
			return null;
		return cookieCheckBox.getValue() ? AuthMethodInstance.UserTypes.COOKIE.getCode() : AuthMethodInstance.UserTypes.PUP.getCode();
	}
	
	public char getUserType() {
		String userTypeStr = getUserTypeString();
		if (userTypeStr == null || userTypeStr.length() == 0)
			return (char) 0;
		else
			return userTypeStr.charAt(0);
	}
	
	@Override
	public void setOriginalValue(String [] values) {
		super.setOriginalValue(values);
		userIdField.setOriginalValue(values [0]);
		passwordField.setOriginalValue(values [1]);
		if (userTypeGroup != null && values.length > 2) {
			if (AuthMethodInstance.UserTypes.COOKIE.getCode().equals(values [2]))
				cookieCheckBox.setOriginalValue(true);
			else
				permanentCheckBox.setOriginalValue(true);
		}
	}
	
	public void setOriginalValue(String userId, String password) {
		setOriginalValue(new String [] {userId, password});
	}
	
	public void setOriginalValue(String userId, String password, String userType) {
		setOriginalValue(new String [] {userId, password, userType});
	}
	
	public void setOriginalValue(String userId, String password, char userType) {
		setOriginalValue(new String [] {userId, password, userType + ""});
	}
	
	@Override
	public void clear() {
		userIdField.clear();
		passwordField.clear();
		if (userTypeGroup != null)
			cookieCheckBox.setValue(true);
	}
	
	@Override
	public void disable() {
		super.disable();
		userIdField.disable();
		passwordField.disable();
		if (userTypeGroup != null)
			userTypeGroup.disable();
	}
	
	@Override
	public void enable() {
		super.enable();
		userIdField.enable();
		passwordField.enable();
		if (userTypeGroup != null)
			userTypeGroup.enable();
	}
	
	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		userIdField.setEnabled(enabled);
		passwordField.setEnabled(enabled);
		if (userTypeGroup != null)
			userTypeGroup.setEnabled(enabled);
	}
	
	/*
	 * The following methods were added to handle aysncronous validation
	 * @see com.extjs.gxt.ui.client.widget.form.Field#clearInvalid()
	 */
	
	@Override
	public void clearInvalid() {
		super.clearInvalid();
		asyncMessages = null;
	}
	
	
	@Override
	protected boolean validateValue(String value) {
		 // validate multi field
	    if (validator != null) {
	      String msg = validator.validate(this, value);
	      if (msg != null) {
	        markInvalid(msg);
	        return false;
	      }
	    }
		
		if (getUserId() != null		&& getUserId().equals(lastUidValidated)
		&&	getPassword() != null	&& getPassword().equals(lastPasswordValidated)
		&&	getUserType() == lastUserTypeValidated
		&&  getProxyId() == lastProxyIdValidated
		&&	methodId.equals(lastMethodId)) {
			if (asyncMessages != null && asyncMessages.size() > 0) {
				markInvalid(asyncMessages);
				return false;
			}
		}

		clearInvalid();
		
		if (isEnabled())
			asynchValidation();
		
		return true;
	}
	
	public void asynchValidation() {
		String userId	= getUserId();
		String password	= getPassword();
		char   userType	= getUserType();
		int    proxyId  = getProxyId();
		
//		markInvalid("<em>Validating...</em>");
		asynchValidation(userId, password, userType, proxyId);
	}
	
	public void asynchValidation(String uid, String password, char userType, int proxyId) {
		if (uid == null || password == null || userType == 0)
			return;
		if (!isEnabled() || !userIdField.isEnabled())	// Don't bother with async validation when disabled.
			return;
		if (uid.equals(lastUidValidated) 
		&& 	password.equals(lastPasswordValidated) 
		&& 	userType == lastUserTypeValidated
		&&	proxyId == lastProxyIdValidated
		&& 	methodId.equals(lastMethodId))
			return;
		
		lastUidValidated = uid;
		lastPasswordValidated = password;
		lastUserTypeValidated = userType;
		lastProxyIdValidated = proxyId;
		lastMethodId.setFrom(methodId);

		validationService.validateUid(uid, password, userType, proxyId, methodId, ++validationCounter,
				new AsyncCallback<AsyncValidationResponse>() {
					public void onFailure(Throwable caught) {
						// Show the RPC error message to the user
						if (caught instanceof IllegalArgumentException)
							MessageBox.alert("Alert", caught.getMessage(), null);
						else {
							MessageBox.alert("Alert", "User ID validation failed unexpectedly.", null);
							System.out.println(caught.getClass().getName());
							System.out.println(caught.getMessage());
						}
					}

					public void onSuccess(AsyncValidationResponse response) {
						//	Mark invalid if an error occurred, and if the response matches the current field validation count setting
						if (response.getValidationCounter() == validationCounter) {
							if (response.getMessages() != null && response.getMessages().size() > 0) {						
								asyncMessages = response.getMessages();
								markInvalid(asyncMessages);
							} else {
								clearInvalid();
								asyncInfoMessages = response.getInfoMessages();
								asyncAlertMessages = response.getAlertMessages();
								markInfo();
							}
						}
					}
			});
	}
	
	public int getProxyId() {
		if (proxyField == null)
			return 0;
		if (proxyField.getSelectedProxy() == null)
			return 0;
		else
			return proxyField.getSelectedProxy().getProxyId();
	}
	
	public ProxySearchField getProxyField() {
		return proxyField;
	}

	public void setProxyField(ProxySearchField proxyField) {
		this.proxyField = proxyField;
	}

	public MethodIdInstance getMethodId() {
		return methodId;
	}

	public void setMethodId(MethodIdInstance methodId) {
		this.methodId = methodId;
	}

	public void markInvalid(List<String> messages) {
		for (String message: messages)
			markInvalid(message);
	}
	
	public void resetAsynchValidation() {
		lastUidValidated		= "";
		lastPasswordValidated	= "";
		lastUserTypeValidated	= 0;
		lastProxyIdValidated	= 0;
		clearInfo();
	}
	
	
	/**
	 * 
	 * The below methods have all been added to incorporate the capability to have "info" and "alert" messages display if no error messages are found.
	 * 
	 * The code is basically a duplication of GXT's error icon code, but using different style attributes.
	 * 
	 * This code should in theory be incorporated in a higher level Field class, but of course there's no way to "insert" that into the inheritance hierarchy,
	 * which in turn would require building all new higher level classes (TextFieldWithInfo, MultiFieldWithInfo, etc.).
	 * 
	 */

	
	@Override
	public void reset() {
		super.reset();
		resetAsynchValidation();
		clearInfo();
	}
	
	public void markInfo() {
		if (asyncInfoMessages == null || asyncInfoMessages.size() == 0)
			if (asyncAlertMessages == null || asyncAlertMessages.size() == 0) {
				clearInfo();
				return;
			}
		markInfo(asyncInfoMessages, asyncAlertMessages);
	}
	
	public void markInfo(List<String> infoMsgs, List<String> alertMsgs) {
		if (infoMsgs == null || infoMsgs.size() == 0)
			if (alertMsgs == null || alertMsgs.size() == 0) {
				clearInfo();
				return;
			}
		
		boolean alert = alertMsgs != null && alertMsgs.size() > 0;
		StringBuffer msgs = new StringBuffer();
		if (alertMsgs != null) {
			boolean first = true;
			for (String msg : alertMsgs) {
				if (msgs.length() > 0) msgs.append("<br/>");
				msgs.append("<span class=\"field-alert-msg" + (first?" first-msg":"") + "\">");
				msgs.append(msg);
				msgs.append("</span>");
				first=false;
			}
		}
		if (infoMsgs != null) {
			boolean first = true;
			for (String msg : infoMsgs) {
				if (msgs.length() > 0) msgs.append("<br/>");
				msgs.append("<span class=\"field-info-msg" + (first?" first-msg":"") + "\">");
				msgs.append(msg);
				msgs.append("</span>");
				first=false;
			}
		}
		markInfo(msgs.toString(), alert);
	}
	
	@Override
	public void markInvalid(String msg) {
		super.markInvalid(msg);
		clearInfo();
//		FOR DEBUGGING TOOLTIP CSS -- makes the tooltip hang around long enough to look at
//		if (errorIcon != null) errorIcon.getToolTip().getToolTipConfig().setDismissDelay(0);
//		if (errorIcon != null) errorIcon.getToolTip().getToolTipConfig().setHideDelay(500000);
	}
	
	/**
	 * Marks this field as having info (warnings or messages). Validation will still run if called again, and
	 * the error message will be changed or cleared based on validation. To set a
	 * error message that will not be cleared until manually cleared see
	 * {@link #forceInvalid(String)}
	 * 
	 * @param msg the validation message
	 */
	public void markInfo(String msg, boolean alert) {
		if (msg == null) {
			clearInfo();
			return;
		}
//		msg = Format.htmlEncode(msg == null ? getMessages().getInvalidText() : msg);
		activeInfoMessage = msg;
		if (!rendered || preventMark) {
			return;
		}
		if (alert) {
			userIdField.removeInputStyleName(infoStyle);
			userIdField.addInputStyleName(alertStyle);
			passwordField.removeInputStyleName(infoStyle);
			passwordField.addInputStyleName(alertStyle);
		} else {
			userIdField.removeInputStyleName(alertStyle);
			userIdField.addInputStyleName(infoStyle);
			passwordField.removeInputStyleName(alertStyle);
			passwordField.addInputStyleName(infoStyle);
		}

		if ("side".equals(getMessageTarget())) {
			if (infoIcon == null) {
				if (alert)
					infoIcon = new WidgetComponent(IconSupplier.getColorfulIcon(IconSupplier.getAlertIconName()).createImage());	//	getImages().getInvalid().createImage());
				else
					infoIcon = new WidgetComponent(IconSupplier.getColorfulIcon(IconSupplier.getInfoIconName()).createImage());	//	getImages().getInvalid().createImage());
				Element p = passwordField.el().getParent().dom;
				infoIcon.render(p);
				infoIcon.setHideMode(HideMode.VISIBILITY);
				infoIcon.hide();
				infoIcon.setStyleAttribute("display", "block");
				infoIcon.el().makePositionable(true);
				infoIcon.getAriaSupport().setRole("alert");
				if (GXT.isAriaEnabled()) {
					setAriaState("aria-describedby", infoIcon.getId());
					infoIcon.setTitle(getErrorMessage());
				}

			} else if (!infoIcon.el().isConnected()) {
				Element p = passwordField.el().getParent().dom;
				p.appendChild(infoIcon.getElement());
			}
			if (!infoIcon.isAttached()) {
				ComponentHelper.doAttach(infoIcon);
			}

			alignInfoIcon();
			if (GXT.isIE || GXT.isOpera) {
				alignInfoIcon();
			}
			// needed to prevent flickering
			DeferredCommand.addCommand(new Command() {
				public void execute() {
					if (infoIcon.isAttached()) {
						infoIcon.show();
					}
				}
			});
			infoIcon.setToolTip(msg);
			infoIcon.getToolTip().getToolTipConfig().setDismissDelay(0); 	// So the user can hang out over the icon and keep the messages up
//			infoIcon.getToolTip().getToolTipConfig().setHideDelay(500000);
			if (alert) {
				infoIcon.getToolTip().removeStyleName("x-form-info-tip");
				infoIcon.getToolTip().addStyleName("x-form-info-alert-tip");
				Image image = (Image) infoIcon.getWidget();
				IconSupplier.getColorfulIcon(IconSupplier.getAlertIconName()).applyTo(image);
			} else {
				infoIcon.getToolTip().removeStyleName("x-form-alert-tip");
				infoIcon.getToolTip().addStyleName("x-form-info-tip");
				Image image = (Image) infoIcon.getWidget();
				IconSupplier.getColorfulIcon(IconSupplier.getInfoIconName()).applyTo(image);
			}
//			infoIcon.getToolTip().getToolTipConfig().setAutoHide(false);
//			infoIcon.getToolTip().getToolTipConfig().setCloseable(true);
			el().repaint();
		} else if ("title".equals(getMessageTarget())) {
			setTitle(msg);
		} else if ("tooltip".equals(getMessageTarget())) {
			setToolTip(msg);
			if (alert)
				getToolTip().addStyleName("x-form-info-alert-tip");
			else
				getToolTip().addStyleName("x-form-info-tip");
			getToolTip().enable();
		} else if ("none".equals(getMessageTarget())) {
			// do nothing
		} else {
			Element elem = XDOM.getElementById(getMessageTarget());
			if (elem != null) {
				elem.setInnerHTML(msg);
			}
		}

//		if (GXT.isAriaEnabled()) {
//			setAriaState("aria-invalid", "true");
//		}
//
//		FieldEvent fe = new FieldEvent(this);
//		fe.setMessage(msg);
//		fireEvent(Events.Invalid, fe);
	}

	/**
	 * Clear any info styles / messages for this field.
	 */
	public void clearInfo() {
		if (!rendered) {
			return;
		}
		
		userIdField.removeInputStyleName(infoStyle);
		userIdField.removeInputStyleName(alertStyle);
		passwordField.removeInputStyleName(infoStyle);
		passwordField.removeInputStyleName(alertStyle);

		//	    if (forceInfoText != null) {
		//	      forceInfoText = null;
		//	    }

		if ("side".equals(getMessageTarget())) {
			if (infoIcon != null && infoIcon.isAttached()) {
				ComponentHelper.doDetach(infoIcon);
				infoIcon.setVisible(false);
				setAriaState("aria-describedby", "");
			}
		} else if ("title".equals(getMessageTarget())) {
			setTitle("");
		} else if ("tooltip".equals(getMessageTarget())) {
			hideToolTip();
			if (toolTip != null) {
				toolTip.disable();
			}
		} else {
			Element elem = XDOM.getElementById(getMessageTarget());
			if (elem != null) {
				elem.setInnerHTML("");
			}
		}
//		if (GXT.isAriaEnabled()) {
//			getAriaSupport().setState("aria-invalid", "false");
//		}
//		fireEvent(Events.Valid, new FieldEvent(this));
	}



	protected void iconAlign(BoxComponent b) {
		b.el().alignTo(iconPositionField.getElement(), "c-c", new int[] {0, 0});	//	(iconPositionField.getOffsetHeight() - getHeight()) / 2});
	}

	/**
	 * Like the info icon, the error icon for the entire group is aligned to the center of the label field between the ip addresses
	 */
	@Override
	protected void alignErrorIcon() {
		DeferredCommand.addCommand(new Command() {
			public void execute() {
				iconAlign(errorIcon);
//				errorIcon().alignTo(getElement(), "tl-tr", new int[] {2, 3});	// Original
			}
		});
	}

	/**
	 * The info icon is aligned to the center of the label field between the IP addresses!
	 */
	protected void alignInfoIcon() {
		DeferredCommand.addCommand(new Command() {
			public void execute() {
				iconAlign(infoIcon);
//				infoIcon.el().alignTo(getElement(), "tl-tr", new int[] {2, 3});	// Original
			}
		});
	}

	@Override
	protected void doDetachChildren() {
		super.doDetachChildren();
		if (infoIcon != null && infoIcon.isAttached()) {
			infoIcon.setVisible(false);
			ComponentHelper.doDetach(infoIcon);
		}
	}

	@Override
	protected void onHide() {
		super.onHide();
		if (infoIcon != null && infoIcon.isAttached()) {
			infoIcon.hide();
		}
	}

	@Override
	protected void onResize(int width, int height) {
		super.onResize(width, height);
		if (infoIcon != null && infoIcon.isAttached()) {
			alignInfoIcon();
		}
	}

	@Override
	protected void onShow() {
		super.onShow();
		if (infoIcon != null && infoIcon.isAttached()) {
			infoIcon.show();
		}
	}
}
