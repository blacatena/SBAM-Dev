package com.scholastic.sbam.client.uiobjects;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.extjs.gxt.ui.client.widget.form.DateField;
import com.scholastic.sbam.shared.validation.DatesSliderBinder;

public class BoundDateField extends DateField {
	List<DatesSliderBinder>	binders = new ArrayList<DatesSliderBinder>();
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
	
	public void setValue(Date value) {
		if (locked) {
			return;
		}
		
		locked = true;
		super.setValue(value);
		for (DatesSliderBinder binder : binders) {
			binder.setFromField(this);
		}
		locked = false;
	}
	
	public void setUnbound() {
		for (DatesSliderBinder binder : binders)
			binder.setUnbound();
	}
	
	public void setBound() {
		for (DatesSliderBinder binder : binders)
			binder.setBound();
	}
	
	public void setSliders() {
		for (DatesSliderBinder binder : binders)
			binder.setSlider();
	}
}
