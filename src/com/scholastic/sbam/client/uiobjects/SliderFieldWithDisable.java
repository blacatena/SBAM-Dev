package com.scholastic.sbam.client.uiobjects;

import com.extjs.gxt.ui.client.widget.Slider;
import com.extjs.gxt.ui.client.widget.form.SliderField;

/**
 * This class is necessary because GXT's disable doesn't seem to work.
 * 
 * It would be best to turn off the draggable object, but since that's (foolishly) hidden by GXT (i.e. private), use a clear mask instead.  Making the mask clear
 * requires a bit of CSS... otherwise the mask has the usual semi-opaque cast to it.
 *  
 * @author Bob Lacatena
 *
 */
public class SliderFieldWithDisable extends SliderField {
	
	public SliderFieldWithDisable(Slider slider) {
		super(slider);
	}
	
	@Override
	public void disable() {
		super.disable();
		getSlider().mask(null, "slider-mask");
	}
	
	@Override
	public void enable() {
		super.enable();
		getSlider().unmask();
	}
	
	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		if (enabled)
			getSlider().unmask();
		else
			getSlider().mask(null, "slider-mask");
	}
}
