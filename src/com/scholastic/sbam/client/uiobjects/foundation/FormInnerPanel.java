package com.scholastic.sbam.client.uiobjects.foundation;


import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.util.Size;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.Container;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.FormPanel.LabelAlign;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;

/**
 * A panel for displaying form widgets
 */
public class FormInnerPanel extends ContentPanel {

  /**
   * Form method enumeration.
   */
  public enum Method {
    GET, POST;
  }

  private boolean	 outerForm	= false;
  private LabelAlign labelAlign = LabelAlign.LEFT;
  private int labelWidth = 75;
  private int fieldWidth = 210;
  private String labelSeparator = ":";
  private boolean hideLabels;
  private int padding = 10;
  private El div;

  /**
   * Creates a new form panel.
   */
  public FormInnerPanel() {
  }

  /**
   * Creates a new form panel.
   */
  public FormInnerPanel(boolean outerForm) {
	  this();
	  this.outerForm = outerForm;
  }

  /**
   * Clears all values from all fields.
   */
  public void clear() {
    for (Field<?> f : getFields()) {
      f.clear();
    }
  }

  /**
   * Resets the dirty state for all fields by setting the original value to be
   * equal to the current value.
   */
  @SuppressWarnings({"unchecked", "rawtypes"})
  public void clearDirtyFields() {
    for (Field f : getFields()) {
      if (f.isDirty()) {
        f.setOriginalValue(f.getValue());
      }
    }
  }

  /**
   * Resets all field values.
   */
  public void reset() {
    for (Field<?> f : getFields()) {
      f.reset();
    }
  }

  /**
   * Returns all of the panel's child fields. Fields in nested containers are
   * included in the returned list.
   * 
   * @return the fields
   */
  public List<Field<?>> getFields() {
    List<Field<?>> fields = new ArrayList<Field<?>>();
    getChildFields(this, fields);
    return fields;
  }

  /**
   * Returns the field width.
   * 
   * @return the field width
   */
  public int getFieldWidth() {
    return fieldWidth;
  }

  /**
   * Returns true if labels are being hidden.
   * 
   * @return the hide label state
   */
  public boolean getHideLabels() {
    return hideLabels;
  }

  /**
   * Returns the label alignment.
   * 
   * @return the label alignment
   */
  public LabelAlign getLabelAlign() {
    return labelAlign;
  }

  /**
   * Returns the label separator.
   * 
   * @return the label separator
   */
  public String getLabelSeparator() {
    return labelSeparator;
  }

  /**
   * Returns the default width.
   * 
   * @return the label width
   */
  public int getLabelWidth() {
    return labelWidth;
  }

  @Override
  public El getLayoutTarget() {
    return div;
  }

  /**
   * Returns the panel's padding.
   * 
   * @return the padding
   */
  public int getPadding() {
    return padding;
  }

  /**
   * Returns true if any of the form's fields are dirty.
   * 
   * @return true for dirty
   */
  public boolean isDirty() {
    for (Field<?> f : getFields()) {
      if (f.isDirty()) {
//    	System.out.println("ifp dirty " + f.getId() + " : " + f.getFieldLabel());
        return true;
      }
    }
//	System.out.println("ifp not dirty");
    return false;
  }

  /**
   * Returns true if the form is invalid.
   * 
   * @return true if all fields are valid
   */
  public boolean isValid() {
    return isValid(false);
  }

  /**
   * Returns the form's valid state by querying all child fields.
   * 
   * @param preventMark true for silent validation (no invalid event and field
   *          is not marked invalid)
   * 
   * @return true if all fields are valid
   */
  public boolean isValid(boolean preventMark) {
    boolean valid = true;
    for (Field<?> f : getFields()) {
      if (!f.isValid(preventMark)) {
        valid = false;
      }
    }
    return valid;
  }

  /**
   * Sets the default field width (defaults to 210).
   * 
   * @param fieldWidth the field width
   */
  public void setFieldWidth(int fieldWidth) {
    this.fieldWidth = fieldWidth;
  }

  /**
   * True to hide field labels by default (defaults to false).
   * 
   * @param hideLabels true to hide labels
   */
  public void setHideLabels(boolean hideLabels) {
    this.hideLabels = hideLabels;
  }

  /**
   * Sets the label alignment.
   * 
   * @param align the alignment
   */
  public void setLabelAlign(LabelAlign align) {
    this.labelAlign = align;
  }

  /**
   * Sets the label separator (defaults to ':').
   * 
   * @param labelSeparator the label separator
   */
  public void setLabelSeparator(String labelSeparator) {
    this.labelSeparator = labelSeparator;
  }

  /**
   * Sets the default label width.
   * 
   * @param labelWidth the label width
   */
  public void setLabelWidth(int labelWidth) {
    this.labelWidth = labelWidth;
  }

  /**
   * Sets the padding to be applied to the FormPanel body (defaults to 10).
   * 
   * @param padding the padding
   */
  public void setPadding(int padding) {
    this.padding = padding;
  }

  /**
   * Sets all of the panel's fields read only state.
   * 
   * @param readOnly true for read only
   */
  public void setReadOnly(boolean readOnly) {
    for (Field<?> f : getFields()) {
      f.setReadOnly(readOnly);
    }
  }

  @Override
  protected Size adjustBodySize() {
    return body.getFrameSize();
  }

  @Override
  protected void onRender(Element target, int index) {
    super.onRender(target, index);
    body.setStyleAttribute("background", "none");

    if (outerForm) {
    	div = new El(DOM.createForm());
    } else {
    	div = new El(DOM.createDiv());
    	div.addStyleName("sbam-form-container");
    }
    div.setStyleAttribute("overflow", "hidden");
    body.appendChild(div.dom);

    getLayoutTarget().setStyleAttribute("padding", padding + "px");

    if (getLayout() == null) {
      FormLayout layout = new FormLayout();
      layout.setDefaultWidth(fieldWidth);
      layout.setLabelWidth(labelWidth);
      layout.setLabelAlign(labelAlign);
      layout.setLabelSeparator(labelSeparator);
      layout.setHideLabels(hideLabels);
      setLayout(layout);
    }

// div.addEventsSunk(Event.ONLOAD);
    
    setAriaRole("region");
  }

  @SuppressWarnings("unchecked")
  private void getChildFields(Container<Component> c, List<Field<?>> fields) {
    for (Component comp : c.getItems()) {
      if (comp instanceof Field) {
        fields.add((Field<?>) comp);
      } else if (comp instanceof Container) {
        getChildFields((Container<Component>) comp, fields);
      }
    }
  }

}
