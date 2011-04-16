package com.scholastic.sbam.client.uiobjects.fields;

import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.google.gwt.user.client.Element;

public class SizedTextArea extends TextArea {
	protected int rows = 0;
	protected int cols = 0;

	@Override
	  protected void onRender(Element target, int index) {
		super.onRender(target, index);
	    if (el() != null && getInputEl() != null) {
	    	if (rows > 0) {
	    		setHeight(0);
	    		getInputEl().dom.setPropertyInt("rows", rows);
	    	}
	    	if (cols > 0)
	    		getInputEl().dom.setPropertyInt("cols", cols);
	    }
	}

	public int getRows() {
		return rows;
	}

	public void setRows(int rows) {
		this.rows = rows;
	}

	public int getCols() {
		return cols;
	}

	public void setCols(int cols) {
		this.cols = cols;
	}
	
}
