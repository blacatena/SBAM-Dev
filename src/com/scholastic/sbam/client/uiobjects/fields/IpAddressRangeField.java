package com.scholastic.sbam.client.uiobjects.fields;

import java.util.List;

import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.MultiField;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.scholastic.sbam.client.services.IpRangeValidationService;
import com.scholastic.sbam.client.services.IpRangeValidationServiceAsync;
import com.scholastic.sbam.shared.objects.IpAddressInstance;
import com.scholastic.sbam.shared.objects.MethodIdInstance;
import com.scholastic.sbam.shared.validation.AsyncValidationResponse;

public class IpAddressRangeField extends MultiField<Long []> {
	public final String	LOW_IP_LABEL	= "";	//"From:&nbsp;";
	public final String HIGH_IP_LABEL	= "&nbsp;&nbsp;&nbsp;&hArr;&nbsp;";	//"&nbsp;&nbsp;To:&nbsp;";
	
	protected LabelField		loIpLabelField;
	protected IpAddressField	loIpField;
	protected LabelField		hiIpLabelField;
	protected IpAddressField	hiIpField;
	
	protected int				validationCounter	= 0;
	protected long				lastLoIpValidated	= 0L;
	protected long				lastHiIpValidated	= 0L;
	protected List<String>		asyncMessages		= null;
	protected MethodIdInstance	methodId			= null;
	
	protected IpRangeValidationServiceAsync validationService = GWT.create(IpRangeValidationService.class);
	
	public IpAddressRangeField() {
		this("");
	}
	
	public IpAddressRangeField(String label) {
		super(label);
		createFields();
	}
	
	protected void createFields() {
		loIpField = new IpAddressField();
		hiIpField = new IpAddressField();
		loIpLabelField = new ConstantLabelField();
		hiIpLabelField = new ConstantLabelField();
		loIpLabelField.setValue(LOW_IP_LABEL);
		hiIpLabelField.setValue(HIGH_IP_LABEL);
		
		hiIpField.setHighIp(true);
		hiIpField.setAllowWildcards(false);
		hiIpField.setTiedIpField(loIpField);
		
		add(loIpLabelField);
		add(loIpField);
		add(hiIpLabelField);
		add(hiIpField);
	}
	
	protected void setLowIpLabel(String label) {
		loIpLabelField.setValue(label);
	}
	
	protected void setHighIpLabel(String label) {
		hiIpLabelField.setValue(label);
	}
	
	protected void setOctetWidths(int width) {
		loIpField.setOctetWidths(width);
		hiIpField.setOctetWidths(width);
	}
	
	protected void setSeparatorWidths(int width) {
		loIpField.setSeparatorWidths(width);
		hiIpField.setSeparatorWidths(width);
	}
	
	protected void setSeparatorValues(String value) {
		loIpField.setSeparatorValues(value);
		hiIpField.setSeparatorValues(value);
	}
	
	public void addStyleName(String styleName) {
		loIpField.addStyleName(styleName);
		hiIpField.addStyleName(styleName);
	}
	
	@Override
	public Long [] getValue() {
		long hiIpValue = hiIpField.getValue();
		if (hiIpValue == 0)
			hiIpValue = loIpField.getValue();
		return new Long [] {loIpField.getValue(), hiIpValue};
	}
	
	@Override
	public void setValue(Long [] values) {
		super.setValue(values);
		if (values [0] == 0 && values [1] == 0) {
			loIpField.setValue(values [0], new String [] {"", "", "", ""});
			hiIpField.setValue(values [1], new String [] {"", "", "", ""});			
		} else {
			String [] [] octets = getIpOctetStrings(values [0], values [1]);
			loIpField.setValue(values [0], octets [0]);
			hiIpField.setValue(values [1], octets [1]);
		}
	}
	
	public void setValue(Long loValue, Long hiValue) {
		setValue(new Long [] {loValue, hiValue});
	}
	
	@Override
	public void setOriginalValue(Long [] values) {
		super.setOriginalValue(values);
		if (values [0] == 0 && values [1] == 0) {
			loIpField.setOriginalValue(values [0], new String [] {"", "", "", ""});
			hiIpField.setOriginalValue(values [1], new String [] {"", "", "", ""});			
		} else {
			String [] [] octets = getIpOctetStrings(values [0], values [1]);
			loIpField.setOriginalValue(values [0], octets [0]);
			hiIpField.setOriginalValue(values [1], octets [1]);
		}
	}
	
	public void setOriginalValue(Long loValue, Long hiValue) {
		setOriginalValue(new Long [] {loValue, hiValue});
	}

	public long getLowValue() {
		return loIpField.getValue();
	}

	public long getHighValue() {
		return hiIpField.getValue();
	}

	public String [] getLowValues() {
		return loIpField.getValues();
	}

	public String [] getHighValues() {
		return hiIpField.getValues();
	}
	
	@Override
	public void clear() {
		loIpField.clear();
		hiIpField.clear();
	}
	
	@Override
	public void setReadOnly(boolean readOnly) {
		super.setReadOnly(readOnly);
	}
	
	@Override
	public void disable() {
		super.disable();
		loIpField.disable();
		hiIpField.disable();
	}
	
	@Override
	public void enable() {
		super.enable();
		loIpField.enable();
		hiIpField.enable();
	}
	
	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		loIpField.setEnabled(enabled);
		hiIpField.setEnabled(enabled);
	}
	
	@Override
	protected boolean validateValue(String value) {
//		boolean isValid = super.validateValue(value);
//		if (!isValid)
//			return false;
		
//		if (loIpField.isBlank()) {
//			markInvalid("An IP address is required.");
//			return false;
//		}
		
		if (loIpField.isWildcarded() && !hiIpField.isBlank()) {
			markInvalid("First IP wildcarded.  Do not specify a range.");
			return false;
		}
		
		if (!loIpField.isBlank() && !hiIpField.isBlank()) {
			if (loIpField.getValue().compareTo(hiIpField.getValue()) > 0) {
				markInvalid("First IP cannot be greater than second IP in a range.");
				return false;
			}
		}
		
		clearInvalid();
		
		asynchValidation();
		
		return true;
	}
	
	public void asynchValidation() {
		long loIp = loIpField.getValue();
		long hiIp = hiIpField.getValue();
		if (hiIp == 0)
			hiIp = loIp;
		if (loIp == 0)
			return;
		
//		markInvalid("<em>Validating...</em>");
		asynchValidation(loIp, hiIp);
	}
	
	public void asynchValidation(long loIp, long hiIp) {
		if (loIp == lastLoIpValidated && hiIp == lastHiIpValidated)
			return;
		
		lastLoIpValidated = loIp;
		lastHiIpValidated = hiIp;

		validationService.validateIpRange(loIp, hiIp, methodId, ++validationCounter,
				new AsyncCallback<AsyncValidationResponse>() {
					public void onFailure(Throwable caught) {
						// Show the RPC error message to the user
						if (caught instanceof IllegalArgumentException)
							MessageBox.alert("Alert", caught.getMessage(), null);
						else {
							MessageBox.alert("Alert", "IP range validation failed unexpectedly.", null);
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
								asyncMessages = null;
								clearInvalid();
							}
						}
					}
			});
	}
	
	public void markInvalid(List<String> messages) {
		for (String message: messages)
			markInvalid(message);
	}
	
//	@Override
//	public boolean isValid(boolean preventMark) {
//		boolean loIsValid = loIpField.isValid(preventMark);
//		if (loIsValid && loIpField.isWildcarded())
//			return hiIpField.invalidateNonBlank(preventMark, "Must be blank if low IP is wildcarded.");
//		return loIsValid && hiIpField.isValid(preventMark);
//	}
	
	public static String [] [] getIpOctetStrings(long ipLo, long ipHi) {
		return IpAddressInstance.getIpOctetStrings(ipLo, ipHi);
	}
}
