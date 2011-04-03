package com.scholastic.sbam.client.uiobjects;

import java.util.Date;

import com.extjs.gxt.ui.client.widget.form.Field;
import com.google.gwt.user.datepicker.client.CalendarUtil;

public class DateDefaultBinder implements DateFieldsBinder {
	
	protected boolean			unbound;
	
	protected BoundDateField	controlDate;
	protected BoundDateField	targetDate;
	protected int				daysDiff;
	
	public DateDefaultBinder(int daysDiff) {
		this.daysDiff = daysDiff;
	}

	@Override
	public void fieldChanged(Field<?> field) {
		if (unbound)
			return;
		
		if (field == controlDate)
			setTargetDateValue();
	}
	
	public void setTargetDateValue() {
		if (controlDate != null && targetDate != null) {
			if (controlDate.getValue() != null && targetDate.getValue() == null) {
				targetDate.setValue(getDatePlusDays(controlDate.getValue(), daysDiff));
			}
		}
	}
	
	/**
	 * Return a date given a start date and a number of days.
	 * @param date
	 * @param days
	 * @return
	 */
	public Date getDatePlusDays(Date date, int days) {
//		long newTime = date.getTime() + (days * (24 * 60 * 60 * 1000));
		Date newDate = new Date();
		newDate.setTime(date.getTime());
		CalendarUtil.addDaysToDate(newDate, days);
		return newDate;
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
		setTargetDateValue();
	}

	public BoundDateField getControlDate() {
		return controlDate;
	}

	public void setControlDate(BoundDateField controlDate) {
		this.controlDate = controlDate;
	}

	public BoundDateField getTargetDate() {
		return targetDate;
	}

	public void setTargetDate(BoundDateField targetDate) {
		this.targetDate = targetDate;
	}

	public int getDaysDiff() {
		return daysDiff;
	}

	public void setDaysDiff(int daysDiff) {
		this.daysDiff = daysDiff;
	}

}
