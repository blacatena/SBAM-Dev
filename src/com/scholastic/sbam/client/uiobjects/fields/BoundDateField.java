package com.scholastic.sbam.client.uiobjects.fields;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.google.gwt.user.client.Element;
import com.scholastic.sbam.shared.validation.DatesSliderBinder;

public class BoundDateField extends DateField {
	List<DateFieldsBinder>	binders = new ArrayList<DateFieldsBinder>();
	protected boolean 		locked	= false;
	
	public void onRender(Element parent, int index) {
		super.onRender(parent, index);
		
		this.addListener(Events.Change, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
				if (be.getType().getEventCode() == Events.Change.getEventCode()) {
					updateBinders(getValue());
				}
			}
			
		});
	}
	
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
	
	@Override
	public void setValue(Date value) {
		if (locked) {
			return;
		}
		
		locked = true;
		super.setValue(value);
		updateBinders(value);
		locked = false;
	}
	
	public void updateBinders(Date value) {
		locked = true;
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
