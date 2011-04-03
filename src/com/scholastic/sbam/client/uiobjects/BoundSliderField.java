package com.scholastic.sbam.client.uiobjects;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.widget.Slider;
import com.extjs.gxt.ui.client.widget.form.PropertyEditor;
import com.scholastic.sbam.shared.validation.DatesSliderBinder;

public class BoundSliderField extends SliderFieldWithDisable {
	List<DatesSliderBinder>	binders = new ArrayList<DatesSliderBinder>();
	protected boolean 		locked	= false;
	
	public class IntegerPropertyEditor implements PropertyEditor<Integer> {

		@Override
		public String getStringValue(Integer value) {
			return value.toString();
		}

		@Override
		public Integer convertStringValue(String value) {
			if (value == null || value.length() == 0)
				return 0;
			return Integer.parseInt(value);
		}
		
	}

	public BoundSliderField(Slider slider) {
		super(slider);
		//	This is necessary because GXT didn't bother to do it, or anything with the values of the actual Field object... it only bothers to work with the 
		//  web page <input> element (as if all you might ever want to do is to submit the form).
		setPropertyEditor(new IntegerPropertyEditor());
	}
	
	public void bind(DatesSliderBinder binder) {
		binders.add(binder);
		binder.setSlider(this);
	}
	
	/**
	 * This is necessary because the GXT SliderField implementation only sets the hidden form element in the DOM, not the value of the
	 * Field object.
	 * 
	 * One could just get the value from the slider itself, but the setValue() is needed to trigger the listener to activate the DateSliderBinder activity.
	 */
	@Override
	public void updateHiddenField() {
		super.updateHiddenField();
		setValue(getSlider().getValue());
	}
	
	@Override
	public void setValue(Integer value) {
		if (locked) {
			return;
		}
		
		locked = true;
		super.setValue(value);
		for (DatesSliderBinder binder : binders) {
			binder.fieldChanged(this);
		}
		locked = false;
	}

}
