package com.scholastic.sbam.client.uiobjects.fields;

import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.MultiField;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.scholastic.sbam.shared.objects.AuthMethodInstance;

public class UserIdPasswordField extends MultiField<String []> {
	public final String	USER_ID_LABEL			= "User&nbsp;ID:&nbsp;";
	public final String PASSWORD_LABEL			= "&nbsp;&nbsp;Password:&nbsp;";
	public final String USER_TYPE_LABEL			= "Type:&nbsp;";
	public final int	DEFAULT_LABEL_WIDTH		=	50;
	public final int	DEFAULT_FIELD_WIDTH		=	100;
	
	protected LabelField		userIdLabelField;
	protected TextField<String>	userIdField;
	protected LabelField		passwordLabelField;
	protected TextField<String>	passwordField;
	
	protected LabelField		userTypeSpacerField;
	protected LabelField		userTypeLabelField;
	protected RadioGroup		userTypeGroup;
	protected Radio				cookieCheckBox;
	protected Radio				permanentCheckBox;
	
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
		userIdField = new TextField<String>();
		passwordField = new TextField<String>();
		userIdLabelField = new ConstantLabelField();
		passwordLabelField = new ConstantLabelField();
		userIdLabelField.setValue(USER_ID_LABEL);
		passwordLabelField.setValue(PASSWORD_LABEL);
		
		setUserIdWidth(DEFAULT_FIELD_WIDTH);
		setPasswordWidth(DEFAULT_FIELD_WIDTH);
		
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
		return new String [] {getUserId(), getPassword(), getUserType()};
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
	
	public String getUserType() {
		if (cookieCheckBox == null)
			return null;
		return cookieCheckBox.getValue() ? AuthMethodInstance.UserTypes.COOKIE.getCode() : AuthMethodInstance.UserTypes.PUP.getCode();
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
}
