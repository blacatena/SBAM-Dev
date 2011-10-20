package com.scholastic.sbam.client.uiobjects.fields;

import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableRowLayout;
import com.google.gwt.user.client.Element;
import com.scholastic.sbam.client.uiobjects.foundation.FieldFactory;

public class FieldAndButtonCombo<D> extends LayoutContainer {
	protected	LabelField			labelField;
	protected	Field<D>			dataField;
	protected	Button				button;
	protected	String				fieldLabel;
	
	protected	int					fieldLabelWidth	=	40;
	
	public FieldAndButtonCombo(Field<D> dataField, String fieldLabel) {
		this.fieldLabel = fieldLabel;
		this.dataField	= dataField;
	}
	
	public FieldAndButtonCombo(Field<D> dataField, String fieldLabel, Button button) {
		this.fieldLabel = fieldLabel;
		this.dataField	= dataField;
		this.button		= button;
	}
	
	public FieldAndButtonCombo(Field<D> dataField, LabelField labelField) {
		this.dataField	= dataField;
		this.labelField = labelField;
		if (labelField != null)
			this.fieldLabel = labelField.getFieldLabel();
	}
	
	public FieldAndButtonCombo(Field<D> dataField, LabelField labelField, Button button) {
		this.dataField	= dataField;
		this.labelField = labelField;
		this.button		= button;
		if (labelField != null)
			this.fieldLabel = labelField.getFieldLabel();
	}
	
	@Override
	public void onRender(Element parent, int index) {
		super.onRender(parent, index);
		
		if (labelField == null)
			labelField = createLabelField(fieldLabel);
		
		if (button == null)
			button = createButton();

		setLayout(new TableRowLayout());
		
		TableData td1 = new TableData();
		td1.setPadding(5);
		add(this.labelField, td1);
		
		TableData td2 = new TableData();
		dataField.setWidth(450);
		add(this.dataField, td2);
		
		TableData td3 = new TableData();
		td3.setPadding(5);
		
		add(this.button, td3);
	}
	
	@Override
	public void onResize(int width, int height) {
		super.onResize(width, height);
		if (dataField != null && isRendered() && getItemCount() > 2 && getItem(0).isRendered() && getItem(1).isRendered()) {
			int proxyWidth = width - (20 + getItem(0).getOffsetWidth() + getItem(2).getOffsetWidth());
			dataField.setWidth(proxyWidth);
		}
	}
	
	public LabelField createLabelField(String label) {
		if (dataField != null && (label == null || label.length() == 0) )
			label = dataField.getFieldLabel();
		
		if (label == null)
			label = "";
		if (label.length() > 0 && !label.endsWith(":"))
			label += ":";
		LabelField labelField = FieldFactory.getLabelField(fieldLabelWidth);
		labelField.setValue(label);
		labelField.addStyleName("x-form-item-label");
		labelField.removeStyleName("field-or-label");
		return labelField;
	}
	
	public Button createButton() {
		Button button = new Button("Open");
//		button.addSelectionListener(new SelectionListener<ButtonEvent>() {
//
//			@Override
//			public void componentSelected(ButtonEvent ce) {
//				System.out.println("open proxy pressed");
//			}
//			
//		});
		button.setWidth(40);
		button.setHeight(20);
		button.setPixelSize(40, 20);
		
		return button;
	}
	
	public LabelField getLabelField() {
		return labelField;
	}

	public void setLabelField(LabelField labelField) {
		this.labelField = labelField;
	}

	public Field<D> getDataField() {
		return dataField;
	}

	public void setDataField(Field<D> dataField) {
		this.dataField = dataField;
	}

	public Button getButton() {
		return button;
	}

	public void setButton(Button button) {
		this.button = button;
	}

	public String getFieldLabel() {
		return fieldLabel;
	}

	public void setFieldLabel(String fieldLabel) {
		this.fieldLabel = fieldLabel;
	}

	public int getFieldLabelWidth() {
		return fieldLabelWidth;
	}

	public void setFieldLabelWidth(int fieldLabelWidth) {
		this.fieldLabelWidth = fieldLabelWidth;
	}

	@Override
	public void disable() {
		setEnabled(false);
	}
	
	@Override
	public void enable() {
		setEnabled(true);
	}
	
	@Override
	public void setEnabled(boolean enabled) {
		dataField.setEnabled(enabled);
		button.setEnabled(enabled);
	}
}
