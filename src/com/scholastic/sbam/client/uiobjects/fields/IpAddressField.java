package com.scholastic.sbam.client.uiobjects.fields;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.MultiField;

public class IpAddressField extends MultiField<Long> {
	public static final int		DEFAULT_OCTET_WIDTH = 30;
	public static final int		DEFAULT_SEPARATOR_WIDTH = 20;
	public static final String	DEFAULT_OCTET_SEPARATOR = "&nbsp;&nbsp;&loz;&nbsp;&nbsp;";
	
	/**
	 * This determines whether or not wildcards are allowed for this IP address.
	 */
	protected boolean 			allowWildcards = true;
	/**
	 * highIp affects how a wildcard character is interpreted when getting the value... 255 for high, 0 for low.
	 */
	protected boolean 			highIp;
	/**
	 * The tiedIpAddressField can be used to auto-fill missing octets, rather than generate an error 
	 */
	protected IpAddressField	tiedIpField;
	
	protected List<OctetField>			octetFields		= new ArrayList<OctetField>();
	protected List<LabelField>			separatorFields	= new ArrayList<LabelField>();
	
	public IpAddressField() {
		this("");
	}
	
	public IpAddressField(String label) {
		super(label);
		createFields();
	}
	
	protected void createFields() {
		OctetField prevField = null;
		for (int i = 0; i < 4; i++) {
			OctetField octetField = new OctetField();
		//	octetField.setId("octet" + i);
			octetFields.add(octetField);
			octetField.setPrevField(prevField);
			if (prevField != null)
				prevField.setNextField(octetField);
			
			add(octetField);
			if (i < 3) {
				separatorFields.add(new ConstantLabelField());
				add(separatorFields.get(i));
			}
			
			prevField = octetField;
		}
		
		setOctetWidths(DEFAULT_OCTET_WIDTH);
		setSeparatorWidths(DEFAULT_SEPARATOR_WIDTH);
		setSeparatorValues(DEFAULT_OCTET_SEPARATOR);
	}
	
	protected void setOctetWidths(int width) {
		for (OctetField octetField : octetFields)
			octetField.setWidth(width);
	}
	
	protected void setSeparatorWidths(int width) {
		for (LabelField separatorField : separatorFields)
			separatorField.setWidth(width);
	}
	
	protected void setSeparatorValues(String value) {
		for (LabelField separatorField : separatorFields)
			separatorField.setValue(value);
	}
	
	public void addStyleName(String styleName) {
		for (OctetField octetField : octetFields)
			octetField.addStyleName(styleName);
		for (LabelField separatorField : separatorFields)
			separatorField.addStyleName(styleName);
	}
	
	public void setValue(Long value, String [] octets) {
		super.setValue(value);
		for (int i = 0; i < octetFields.size(); i++) {
			octetFields.get(i).setValue(octets [i]);
		}
	}
	
	public void setOriginalValue(Long value, String [] octets) {
		super.setOriginalValue(value);
		for (int i = 0; i < octetFields.size(); i++) {
			octetFields.get(i).setOriginalValue(octets [i]);
		}
	}
	
	@Override
	public Long getValue() {
		long value = 0;
		long factor = 1;
		for (int i = octetFields.size() - 1; i >= 0; i--) {
			value += getOctetValue(i) * factor;
			factor *= 256l;
		}
		return value;
	}
	
	public long getOctetValue(int octet) {
		OctetField field = octetFields.get(octet);
		if (field.getValue() == null)
			return 0l;
		if (field.isWildcard())
			if (highIp)
				return 255l;
			else
				return 0l;
		try { return Long.parseLong(field.getValue()); } catch (Exception e) { return 0l; }
	}
	
	@Override
	public void setValue(Long value) {
		super.setValue(value);
		for (int i = octetFields.size() - 1; i >= 0; i--) {
			long octet = value % 256;
			octetFields.get(i).setValue("" + octet);
			value /= 256l;
		}
	}
	
	@Override
	public void setOriginalValue(Long value) {
		super.setOriginalValue(value);
		for (int i = octetFields.size() - 1; i >= 0; i--) {
			int octet = (int) (value % 256);
			octetFields.get(i).setOriginalValue("" + octet);
			value /= 256;
		}
	}

	public String [] getValues() {
		String [] values = new String [octetFields.size()];
		for (int i = 0; i < values.length; i++)
			values [i] = octetFields.get(i).getValue();
		return values;
	}
	
	@Override
	public void clear() {
		for (OctetField octetField : octetFields)
			octetField.clear();
	}
	
	@Override
	public void disable() {
		super.disable();
		for (OctetField octetField : octetFields)
			octetField.disable();
	}
	
	@Override
	public void enable() {
		super.enable();
		for (OctetField octetField : octetFields)
			octetField.enable();
	}
	
	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		for (OctetField octetField : octetFields)
			octetField.setEnabled(enabled);
	}
	
//	@Override
//	public boolean validateValue(String value) {
//		boolean isValid = super.validateValue(value);
//		if (!isValid)
//			return isValid;
//		
//		for (OctetField octetField : octetFields)
//			isValid = isValid && octetField.validateValue(octetField.getValue());
//		return isValid;
//	}
	
	
	public boolean isWildcarded() {
		for (OctetField octetField : octetFields) {
			if (octetField.isWildcard())
				return true;
		}
		return false;
	}
	
	public boolean isBlank() {
		for (OctetField octetField : octetFields) {
			if (!octetField.isBlank())
				return false;
		}
		return true;
	}

	public boolean isAllowWildcards() {
		return allowWildcards;
	}

	public void setAllowWildcards(boolean allowWildcards) {
		this.allowWildcards = allowWildcards;
		for (OctetField octetField : octetFields) {
			octetField.setWildcardAllowed(allowWildcards);
		}
	}

	public boolean isHighIp() {
		return highIp;
	}

	public void setHighIp(boolean highIp) {
		this.highIp = highIp;
		for (OctetField octetField : octetFields) {
			octetField.setAllBlankAllowed(highIp);
		}
	}

	public IpAddressField getTiedIpField() {
		return tiedIpField;
	}

	public void setTiedIpField(IpAddressField tiedIpField) {
		this.tiedIpField = tiedIpField;
		if (tiedIpField != null) {
			for (int i = 0; i < octetFields.size(); i++) {
				octetFields.get(i).setTiedOctetField(tiedIpField.getOctetField(i));
			}
		} else {
			for (OctetField octetField : octetFields)
				octetField.setTiedOctetField(null);
		}
	}
	
	public String getOctetRawValue(int index) {
		if (index < 0 || index >= octetFields.size())
			return "";
		return octetFields.get(index).getValue();
	}
	
	public OctetField getOctetField(int index) {
		if (index < 0 || index >= octetFields.size())
			return null;
		return octetFields.get(index);
	}
}
