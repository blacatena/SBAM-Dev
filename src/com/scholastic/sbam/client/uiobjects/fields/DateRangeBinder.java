package com.scholastic.sbam.client.uiobjects.fields;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.Field;

public class DateRangeBinder implements DateFieldsBinder {
	
	/**
	 * This can be used to unbind things temporarily, while setting values, to prevent unexpected interactions
	 */
	private boolean				unbound;

	private List<BoundDateField>	loDates = new ArrayList<BoundDateField>();
	private List<BoundDateField>	hiDates = new ArrayList<BoundDateField>();
	
	@Override
	public void fieldChanged(Field<?> field) {
		if (unbound)
			return;
		
		for (BoundDateField loDate : loDates) {
			if (field == loDate) {
				setMaxLoDates();
			}
		}
		for (BoundDateField hiDate : hiDates) {
			if (field == hiDate) {
				setMinHiDates();
			}
		}
	}

	public boolean isUnbound() {
		return unbound;
	}
	
	@Override
	public void setBound() {
		setUnbound(false);
	}

	@Override
	public void setUnbound() {
		setUnbound(true);
	}

	public void setUnbound(boolean unbound) {
		this.unbound = unbound;
	}

	@Override
	public void setDependentFields() {
		setMaxLoDates();
		setMinHiDates();
	}
	
	public void setMinHiDates() {
		if (loDates.size() == 0 || hiDates.size() == 0)
			return;
		
		Date minDate = null;
		for (DateField loDate : loDates) {
			if (loDate.getValue() != null) {
				if (minDate == null) {
					minDate = new Date();
					minDate.setTime(loDate.getValue().getTime());
				} else {
					if (loDate.getValue().after(minDate))
						minDate.setTime(loDate.getValue().getTime());
				}
			}
		}
		
		if (minDate != null) {
			for (DateField hiDate : hiDates) {
				hiDate.setMinValue(minDate);
			}
		}
	}
	
	public void setMaxLoDates() {
		if (loDates.size() == 0 || hiDates.size() == 0)
			return;
		
		Date maxDate = null;
		for (DateField hiDate : hiDates) {
			if (hiDate.getValue() != null) {
				if (maxDate == null) {
					maxDate = new Date();
					maxDate.setTime(hiDate.getValue().getTime());
				} else {
					if (hiDate.getValue().before(maxDate))
						maxDate.setTime(hiDate.getValue().getTime());
				}
			}
		}
		
		if (maxDate != null) {
			for (DateField loDate : loDates) {
				loDate.setMaxValue(maxDate);
			}
		}
	}

	public List<BoundDateField> getLoDates() {
		return loDates;
	}

	public void addLoDate(BoundDateField loDate) {
		loDates.add(loDate);
	}

	public List<BoundDateField> getHiDates() {
		return hiDates;
	}

	public void addHiDate(BoundDateField hiDate) {
		hiDates.add(hiDate);
	}

}
