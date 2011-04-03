package com.scholastic.sbam.client.uiobjects;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.extjs.gxt.ui.client.widget.form.DateField;
import com.scholastic.sbam.shared.validation.DatesSliderBinder;

public class BoundDateField extends DateField {
	List<DateFieldsBinder>	binders = new ArrayList<DateFieldsBinder>();
	protected boolean 		locked	= false;
	
	public void bindMin(DatesSliderBinder binder) {
		binders.add(binder);
		binder.setMinDate(this);
	}
	
	public void bindMax(DatesSliderBinder binder) {
		binders.add(binder);
		binder.setMaxDate(this);
	}
	
	public void bindLow(DatesSliderBinder binder) {
		binders.add(binder);
		binder.setLoDate(this);
	}
	
	public void bindHigh(DatesSliderBinder binder) {
		binders.add(binder);
		binder.setHiDate(this);
	}
	
	public void bindLow(DateRangeBinder binder) {
		binders.add(binder);
		binder.addLoDate(this);
	}
	
	public void bindHigh(DateRangeBinder binder) {
		binders.add(binder);
		binder.addHiDate(this);
	}
	
	public void bindControl(DateDefaultBinder binder) {
		binders.add(binder);
		binder.setControlDate(this);
	}
	
	public void bindTarget(DateDefaultBinder binder) {
		binders.add(binder);
		binder.setTargetDate(this);
	}
	
	public void setValue(Date value) {
		if (locked) {
			return;
		}
		
		locked = true;
		super.setValue(value);
		for (DateFieldsBinder binder : binders) {
			binder.fieldChanged(this);
		}
		locked = false;
	}
	
	public void setUnbound() {
		for (DateFieldsBinder binder : binders)
			binder.setUnbound();
	}
	
	public void setBound() {
		for (DateFieldsBinder binder : binders)
			binder.setBound();
	}
	
	public void setDependencies() {
		for (DateFieldsBinder binder : binders)
			binder.setDependentFields();
	}
}
