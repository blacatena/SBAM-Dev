package com.scholastic.sbam.client.uiobjects.fields;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.scholastic.sbam.client.util.UiConstants;

public class CountryComboBox extends EnhancedComboBox<BeanModel> {
	
	public CountryComboBox() {
		super();
		this.setWidth(100);
		this.setValueField("countryCode");
		this.setDisplayField("description");  
		this.setEmptyText("Select country...");
		this.setStore(UiConstants.getInstitutionCountries());
		this.setMinChars(1);
		this.setHideTrigger(false); 
		this.setTriggerStyle("trigger-square"); 
		this.setTypeAhead(true);
		this.setTriggerAction(TriggerAction.ALL);
		this.setPageSize(200);
		this.setAllowBlank(true);
		this.setEditable(false);
		this.setSimpleTemplate(getMultiLineAddressTemplate());
		this.setMinListWidth(200);
	}

	protected String getMultiLineAddressTemplate() {
		return "<div class=\"list-normal\"><span class=\"show-plain\">{comboDescription}</span><span class=\"show-code\"> {countryCode}</span></div>"; // {address1}<br/>{city}, {state} &nbsp;&nbsp;&nbsp; {zip}";
	}
	
	public void setCodeValue(String value) {
		if (this.getStore().findModel(value) != null) {
			setRawValue(value);
			setValue(this.getStore().findModel(value));
		} else {
			setValue(null);
			setRawValue(value);
		}
	}
	
	public String getCodeValue() {
		if (this.getSelectedValue() != null) {
			return this.getSelectedValue().get("countryCode");
		} else {
			if (getRawValue() == null) {
				return "";
			} else {
				return getRawValue().toString();
			}
		}
	}
}
