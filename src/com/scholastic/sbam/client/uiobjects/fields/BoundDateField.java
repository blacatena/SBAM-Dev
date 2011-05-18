package com.scholastic.sbam.client.uiobjects.fields;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.util.DateWrapper;
import com.extjs.gxt.ui.client.util.Format;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.google.gwt.i18n.client.DateTimeFormat;
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

	/**
	 * This method is overridden because of a bug in the original, where DateWrapper resetTime is applied to the min/max, but not the date.
	 * 
	 * To overcome difficulties in overriding the values, however, tricks have to be played.  Specifically, when the super.validateValue call
	 * is made, minValue and MaxValue must be forced to null, so those failed checks don't occur in the super method.
	 */
	@Override
	protected boolean validateValue(String value) {
		Date minValue = getMinValue();
		Date maxValue = getMaxValue();
		
		//	This has to null out the min/max values, so the super call only does the super.super.validate with no min/max checks
		super.setMinValue(null);
		super.setMaxValue(null);
		boolean superValid = super.validateValue(value);
		super.setMinValue(minValue);
		super.setMaxValue(maxValue);
		if (!superValid)
			return false;
		
//		if (!super.validateValue(value)) {
//			return false;
//		}
		
		if (value.length() < 1) { // if it's blank and textfield didn't flag it then
			// it's valid
			return true;
		}

		DateTimeFormat format = getPropertyEditor().getFormat();

		Date date = null;

		try {
			date = getPropertyEditor().convertStringValue(value);
			//	Bug fix to put date in same time zone as min/max value will be
			date = new DateWrapper(date).resetTime().asDate();
		} catch (Exception e) {

		}

		if (date == null) {
			String error = null;
			if (getMessages().getInvalidText() != null) {
				error = Format.substitute(getMessages().getInvalidText(), value, format.getPattern().toUpperCase());
			} else {
				error = GXT.MESSAGES.dateField_invalidText(value, format.getPattern().toUpperCase());
			}
			markInvalid(error);
			return false;
		}

		if (minValue != null && date.before(minValue)) {
			String error = null;
			if (getMessages().getMinText() != null) {
				error = Format.substitute(getMessages().getMinText(), format.format(minValue));
			} else {
				error = GXT.MESSAGES.dateField_minText(format.format(minValue));
			}
			markInvalid(error);
			return false;
		}
		if (maxValue != null && date.after(maxValue)) {
			String error = null;
			if (getMessages().getMaxText() != null) {
				error = Format.substitute(getMessages().getMaxText(), format.format(maxValue));
			} else {
				error = GXT.MESSAGES.dateField_maxText(format.format(maxValue));
			}
			markInvalid(error);
			return false;
		}

		if (isFormatValue() && getPropertyEditor().getFormat() != null) {
			setRawValue(getPropertyEditor().getStringValue(date));
		}

		return true;
	}
}
