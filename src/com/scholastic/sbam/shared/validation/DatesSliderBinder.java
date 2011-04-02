package com.scholastic.sbam.shared.validation;

import java.util.Date;

import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.SliderField;
import com.google.gwt.user.datepicker.client.CalendarUtil;

public class DatesSliderBinder {
	
	/**
	 * This can be used to unbind things temporarily, while setting values, to prevent unexpected interactions
	 */
	private boolean				unbound;
	/**
	 * The minimum allowed date, used in setting the minimum value for the slider.
	 */
	private DateField			minDate;
	/**
	 * The maximum allowed date, used in setting the maximum value for the slider.
	 * 
	 * If null, bestMaxDays is used instead.
	 */
	private DateField			maxDate;
	/**
	 * The low date in the range.
	 */
	private DateField			loDate;
	/**
	 * The high date in the range.
	 */
	private DateField			hiDate;
	/**
	 * The slider connecting the two dates.
	 */
	private SliderField			slider;
	/**
	 * This is the preferred maximum days value for the slider, but it may be overridden if it falls below the range needed for explicit setting of dates.
	 */
	private int					bestMaxDays;
	/**
	 * This is the preferred maximum days value for the slider, but it may be overridden if it falls below the range needed for explicit setting of dates.
	 */
	private int					bestMinDays;
	
	public DatesSliderBinder(int bestMaxDays) {
		this.bestMaxDays = bestMaxDays;
	}

	/**
	 * Based on a setting of a field, adjust the other fields.
	 * @param field
	 */
	public void setFromField(Field<?> field) {
		if (unbound)
			return;
		
		if (!field.isRendered())
			return;
		if (field == slider) {
			setFromSlider();
		} else if (field == minDate) {
			setSliderMinDays();
		} else {
			setFromDates();
		}
	}
	
	/**
	 * Set the slider value based on the dates.
	 * 
	 * This method can be used to initialize the slider.
	 */
	public void setSlider() {
		boolean saveUnbound = unbound;
		unbound = true;
		
		setFromDates();
		
		unbound = saveUnbound;
	}
	
	/**
	 * Set the dates based on the slider.
	 */
	public void setFromSlider() {
		if (loDate.getValue() == null && hiDate.getValue() == null)
			return;
		
		if (loDate.getValue() == null) {
			Date newLoDate = getDatePlusDays(hiDate.getValue(), -slider.getSlider().getValue());
			loDate.setValue(newLoDate);
		} else {
			Date newHiDate = getDatePlusDays(loDate.getValue(), slider.getSlider().getValue());
			if (hiDate.getValue() == null || !hiDate.getValue().equals(newHiDate))
				hiDate.setValue(newHiDate);
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

	/**
	 * Set the slider from the dates.
	 */
	public void setFromDates() {
		if (loDate.getValue() == null || hiDate.getValue() == null)
			return;

		setSliderMaxDays();
		setSliderMinDays();
		
		int days = CalendarUtil.getDaysBetween(loDate.getValue(), hiDate.getValue());
		if (days < slider.getSlider().getMinValue())
			days = slider.getSlider().getMinValue();
		slider.setValue(days);
	}
	
	/**
	 * Reset the minimum days for the slider.
	 */
	public void setSliderMinDays() {
		if (slider == null)
			return;
		if (minDate == null || minDate.getValue() == null || loDate == null || loDate.getValue() == null) {
			if (slider.getSlider().getMinValue() != bestMinDays)
				slider.getSlider().setMinValue(bestMinDays);
			return;
		}
		
		int minDays = -CalendarUtil.getDaysBetween(minDate.getValue(), loDate.getValue());
		slider.getSlider().setMinValue(minDays);
	}
	
	/**
	 * Reset the maximum days for the slider, in case a new date range exceeds the current setting
	 */
	public void setSliderMaxDays() {
		if (slider == null)
			return;
		if (loDate == null || hiDate == null || loDate.getValue() == null || hiDate.getValue() == null) {
			slider.getSlider().setMaxValue(bestMaxDays);
			return;
		}
		
		///	If there is a max date, use that to determine the maximum number of days
		if (maxDate != null && maxDate.getValue() != null) {
			int maxDays = CalendarUtil.getDaysBetween(loDate.getValue(), maxDate.getValue());
			slider.getSlider().setMaxValue(maxDays);
			return;
		}
		
		//	Determine the maximum number of days using the best Max days, but let it grow if setting the dates exceeds it
		
		int days = CalendarUtil.getDaysBetween(loDate.getValue(), hiDate.getValue());
		
		if (days > slider.getSlider().getMaxValue()) {
			slider.getSlider().setMaxValue(days);
		} else if (days < bestMaxDays && slider.getSlider().getMaxValue() > bestMaxDays) {
			slider.getSlider().setMaxValue(bestMaxDays);
		}
	}

	public DateField getMinDate() {
		return minDate;
	}

	public void setMinDate(DateField minDate) {
		this.minDate = minDate;
	}

	public DateField getMaxDate() {
		return maxDate;
	}

	public void setMaxDate(DateField maxDate) {
		this.maxDate = maxDate;
	}

	public DateField getLoDate() {
		return loDate;
	}


	public void setLoDate(DateField loDate) {
		this.loDate = loDate;
	}


	public DateField getHiDate() {
		return hiDate;
	}


	public void setHiDate(DateField hiDate) {
		this.hiDate = hiDate;
	}


	public SliderField getSlider() {
		return slider;
	}


	public void setSlider(SliderField slider) {
		this.slider = slider;
		bestMinDays = slider.getSlider().getMinValue();
		bestMaxDays = slider.getSlider().getMaxValue();
	}

	public boolean isUnbound() {
		return unbound;
	}
	
	public void setBound() {
		setUnbound(false);
	}

	public void setUnbound() {
		setUnbound(true);
	}

	public void setUnbound(boolean unbound) {
		this.unbound = unbound;
	}

	public int getBestMaxDays() {
		return bestMaxDays;
	}

	public void setBestMaxDays(int bestMaxDays) {
		this.bestMaxDays = bestMaxDays;
	}

}
