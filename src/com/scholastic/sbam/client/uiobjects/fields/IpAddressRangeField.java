package com.scholastic.sbam.client.uiobjects.fields;

import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.MultiField;

public class IpAddressRangeField extends MultiField<Long []> {
	public final String	LOW_IP_LABEL	= "";	//"From:&nbsp;";
	public final String HIGH_IP_LABEL	= "&nbsp;&nbsp;&nbsp;&hArr;&nbsp;";	//"&nbsp;&nbsp;To:&nbsp;";
	
	protected LabelField		loIpLabelField;
	protected IpAddressField	loIpField;
	protected LabelField		hiIpLabelField;
	protected IpAddressField	hiIpField;
	
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
		return true;
	}
	
//	@Override
//	public boolean isValid(boolean preventMark) {
//		boolean loIsValid = loIpField.isValid(preventMark);
//		if (loIsValid && loIpField.isWildcarded())
//			return hiIpField.invalidateNonBlank(preventMark, "Must be blank if low IP is wildcarded.");
//		return loIsValid && hiIpField.isValid(preventMark);
//	}
	
	public static String [] [] getIpOctetStrings(long ipLo, long ipHi) {
		if (ipHi == 0)
			ipHi = ipLo;
		int [] lo = getIpOctets(ipLo);
		int [] hi = getIpOctets(ipHi);
		String [] strLo = new String [4];
		String [] strHi = new String [4];
		for (int i = 0; i < 4; i++) {
			strLo [i] = lo [i] + "";
			strHi [i] = hi [i] + "";
		}
		if (lo [0] == hi [0]) {
			if (lo [1] == hi [1]) {
				if (lo [2] == hi [2]) {
					if (lo [3] == hi [3]) {
						blankOctets(strHi);
					} else if (lo [3] == 0 && hi [3] == 255) {
						blankOctets(strHi);
						strLo [3] = "*";
					}
				} else if (lo [2] == 0 && lo [3] == 0 && hi [2] == 255 && hi [3] == 255) {
					blankOctets(strHi);
					strLo [2] = "*";
					strLo [3] = "";
				}
			} else if (lo [1] == 0 && lo [2] == 0 && lo [3] == 0 && hi [1] == 255 && hi [2] == 255 && hi [3] == 255) {
				blankOctets(strHi);
				strLo [1] = "*";
				strLo [2] = "";
				strLo [3] = "";
			}
		}
		
		return new String [] [] {strLo, strHi};
	}
	
	public static void blankOctets(String [] octets) {
		for (int i = 0; i < octets.length; i++)
			octets [i] = "";
	}
	
	public static int [] getIpOctets(long ip) {
		int o1 = (int) (ip % 256);
		ip = ip / 256;
		int o2 = (int) (ip % 256);
		ip = ip / 256;
		int o3 = (int) (ip % 256);
		int o4 = (int) (ip % 256);
		return new int [] {o4, o3, o2, o1};
	}
}
