package com.scholastic.sbam.client.uiobjects.fields;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.MultiField;
import com.extjs.gxt.ui.client.widget.form.TextField;

public class IpAddressField extends MultiField<Long> {
	public static final int		DEFAULT_OCTET_WIDTH = 30;
	public static final int		DEFAULT_SEPARATOR_WIDTH = 5;
	public static final String	DEFAULT_OCTET_SEPARATOR = "&nbsp;.&nbsp;";
	
	protected boolean allowWildcards;
	protected boolean highIp;
	
	protected List<TextField<String>>	octetFields		= new ArrayList<TextField<String>>();
	protected List<LabelField>			separatorFields	= new ArrayList<LabelField>();
	
	public IpAddressField() {
		this("");
	}
	
	public IpAddressField(String label) {
		super(label);
		createFields();
	}
	
	protected void createFields() {
		for (int i = 0; i < 4; i++) {
			octetFields.add(new TextField<String>());
			add(octetFields.get(i));
			if (i < 3) {
				separatorFields.add(new ConstantLabelField());
				add(separatorFields.get(i));
			}
		}
		
		setOctetWidths(DEFAULT_OCTET_WIDTH);
		setSeparatorWidths(DEFAULT_SEPARATOR_WIDTH);
		setSeparatorValues(DEFAULT_OCTET_SEPARATOR);
	}
	
	protected void setOctetWidths(int width) {
		for (TextField<String> octetField : octetFields)
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
		for (TextField<String> octetField : octetFields)
			octetField.addStyleName(styleName);
		for (LabelField separatorField : separatorFields)
			separatorField.addStyleName(styleName);
	}
	
	public void setValue(String [] octets) {
		for (int i = 0; i < octetFields.size(); i++) {
			octetFields.get(i).setValue(octets [i]);
		}
	}
	
	public void setOriginalValue(String [] octets) {
		for (int i = 0; i < octetFields.size(); i++) {
			octetFields.get(i).setOriginalValue(octets [i]);
		}
	}
	
	@Override
	public Long getValue() {
		long value = 0;
		long factor = 1;
		for (int i = octetFields.size() - 1; i >= 0; i--) {
			System.out.println(i + " : " + octetFields.get(i).getValue());
			value += getOctetValue(i) * factor;
			System.out.println(i + " : octet value " + getOctetValue(i) + " * " + factor);
			factor *= 256l;
		}
		System.out.println("Get ip value " + value);
		return value;
	}
	
	public long getOctetValue(int octet) {
		TextField<String> field = octetFields.get(octet);
		if (field.getValue() == null)
			return 0l;
		if ("*".equals(field.getValue()))
			if (highIp)
				return 255l;
			else
				return 0l;
		try { return Long.parseLong(field.getValue()); } catch (Exception e) { return 0l; }
	}
	
	@Override
	public void setValue(Long value) {
		System.out.println("Set ip to " + value);
		super.setValue(value);
		for (int i = octetFields.size() - 1; i >= 0; i--) {
			long octet = value % 256;
			octetFields.get(i).setValue("" + octet);
			System.out.println(i + " : " + octetFields.get(i).getValue());
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
		for (TextField<String> octetField : octetFields)
			octetField.clear();
	}
	
//	@Override
//	public void setEnabled(boolean enabled) {
//		for (TextField<String> octetField : octetFields)
//			octetField.setEnabled(enabled);
//	}
//	
//	@Override
//	public void setReadOnly(boolean readonly) {
//		for (TextField<String> octetField : octetFields)
//			octetField.setReadOnly(readonly);
//	}
//	
//	@Override
//	public boolean isValid(boolean preventMark) {
//		return super.isValid(preventMark);
//	}
}
