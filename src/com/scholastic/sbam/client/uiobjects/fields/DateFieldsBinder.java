package com.scholastic.sbam.client.uiobjects.fields;

import com.extjs.gxt.ui.client.widget.form.Field;

public interface DateFieldsBinder {
	public void fieldChanged(Field<?> field);
	
	public void setBound();
	
	public void setUnbound();
	
	public void setDependentFields();
	
	public void setUnbound(boolean unbound);

	public boolean isUnbound();
}
