package com.scholastic.sbam.client.uiobjects.fields;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.scholastic.sbam.client.util.UiConstants;

public class StateComboBox extends EnhancedComboBox<BeanModel> {
	
	public StateComboBox() {
		super();
		this.setWidth(75);
		this.setValueField("stateCode");
		this.setDisplayField("description");  
		this.setEmptyText("Select a state...");
		this.setStore(UiConstants.getInstitutionStates());
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
		return "<div class=\"list-normal\"><span class=\"show-plain\">{comboDescription}</span><span class=\"show-code\"> {stateCode}</span></div>"; // {address1}<br/>{city}, {state} &nbsp;&nbsp;&nbsp; {zip}";
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
			return this.getSelectedValue().get("stateCode");
		} else {
			if (getRawValue() == null) {
				return "";
			} else {
				return getRawValue().toString();
			}
		}
	}
}
