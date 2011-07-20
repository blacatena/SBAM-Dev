package com.scholastic.sbam.client.uiobjects.uireports;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.Container;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.ButtonBar;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.CheckBoxGroup;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.scholastic.sbam.client.services.SnapshotClearService;
import com.scholastic.sbam.client.services.SnapshotClearServiceAsync;
import com.scholastic.sbam.client.services.SnapshotParameterSetGetService;
import com.scholastic.sbam.client.services.SnapshotParameterSetGetServiceAsync;
import com.scholastic.sbam.client.services.UpdateSnapshotParameterSetService;
import com.scholastic.sbam.client.services.UpdateSnapshotParameterSetServiceAsync;
import com.scholastic.sbam.client.uiobjects.fields.BoundDateField;
import com.scholastic.sbam.client.uiobjects.foundation.AppSleeper;
import com.scholastic.sbam.client.uiobjects.foundation.FieldFactory;
import com.scholastic.sbam.client.util.IconSupplier;
import com.scholastic.sbam.client.util.UiConstants;
import com.scholastic.sbam.shared.objects.SnapshotInstance;
import com.scholastic.sbam.shared.objects.SnapshotParameterSetInstance;
import com.scholastic.sbam.shared.objects.SnapshotParameterValueObject;
import com.scholastic.sbam.shared.util.AppConstants;

public abstract class SnapshotCriteriaCardBase extends SnapshotCardBase implements AppSleeper {
	
	protected final int				DIRTY_FIELDS_LISTEN_TIME	=	250;
	protected final String			SOURCE						=	AppConstants.getSimpleName(this);
	
	protected ContentPanel			contentPanel				=	 getNewContentPanel();
	
	protected Timer					dirtyFieldsListener;
	
	protected TableData				tableDataLabel1;
	protected TableData				tableDataLabel2;
	protected TableData				tableDataField;
	protected TableData				tableDividerRow;
	protected TableData				table3ColumnField;
	
	protected Button				saveButton				= new Button("Save Changes");
	protected Button				cancelButton			= new Button("Cancel Changes");
	protected Button				clearButton				= new Button("Clear Snapshot Data");
	
	private final UpdateSnapshotParameterSetServiceAsync	updateSnapshotParameterSetService	= GWT.create(UpdateSnapshotParameterSetService.class);
	private final SnapshotParameterSetGetServiceAsync		snapshotParameterSetGetService		= GWT.create(SnapshotParameterSetGetService.class);
	private final SnapshotClearServiceAsync					snapshotClearService				= GWT.create(SnapshotClearService.class);
	
	public SnapshotCriteriaCardBase() {
		super();
		
		this.headingToolTip = "Use this panel to specify term criteria for the snapshot.";
		
		populateFields();
	}

	/**
	 * Perform any required initial field population here.
	 */
	public abstract void populateFields();
	
	public void popeulatCheckGroup(String name, CheckBoxGroup checkBoxGroup, List<BeanModel> models, String value, String description) {
		checkBoxGroup.setName(name);
		for (BeanModel model : UiConstants.getTermTypes().getModels()) {
				CheckBox checkBox = new CheckBox();
				checkBox.setName(model.get(value).toString());
				checkBox.setBoxLabel(model.get(description).toString());
				checkBox.setValueAttribute(model.get(value).toString());
				checkBoxGroup.add(checkBox);
		}
	}
	
	@Override
	public void addPanelContent() {
		
		createTableDataSpecifications();
		
		addButtonRow();
		
		addCriteriaFields();
		
		addDirtyFieldsListener();
		
		add(contentPanel);
	}
	
	public abstract String getPanelIconName();
	
	public ContentPanel getNewContentPanel() {
		ContentPanel contentPanel = new ContentPanel();
		contentPanel.setHeading(getPanelTitle());
		IconSupplier.setIcon(contentPanel, getPanelIconName());
		return contentPanel;
	}
	
	protected void createTableDataSpecifications() {
		
		contentPanel.setLayout(new TableLayout(4));
		
		tableDividerRow = new TableData();
		tableDividerRow.setColspan(4);
		
		tableDataLabel1 = new TableData();
		tableDataLabel1.setHorizontalAlign(HorizontalAlignment.RIGHT);
		tableDataLabel1.setWidth("150");
		tableDataLabel1.setPadding(2);

		tableDataLabel2 = new TableData();
		tableDataLabel2.setHorizontalAlign(HorizontalAlignment.RIGHT);
		tableDataLabel2.setWidth("15");
		tableDataLabel2.setPadding(2);
		
		tableDataField = new TableData();
		tableDataField.setHorizontalAlign(HorizontalAlignment.LEFT);
		tableDataField.setWidth("150");
		tableDataField.setPadding(2);
		
		table3ColumnField = new TableData();
		table3ColumnField.setColspan(3);
		table3ColumnField.setPadding(2);
		
	}
	
	/**
	 * Add all criteria fields to the content panel here.
	 */
	protected abstract void addCriteriaFields();
	
	/**
	 * Utility method to add a single field to the content panel.
	 * @param label
	 * @param field
	 */
	public void addSingleField(String label, Component field) {
		Html labelHtml = new Html(label);
		labelHtml.setStyleName("report-form-label");
		contentPanel.add(labelHtml,	tableDataLabel1);
		contentPanel.add(field,		table3ColumnField);
	}
	
	/**
	 * Utility method to add the button row to the content panel.
	 */
	public void addButtonRow() {
		contentPanel.add(getButtonsBar(), tableDividerRow);
	}
	
	/**
	 * Utility method to add a blank (divider) row to the content panel.
	 */
	public void addDividerRow() {
		contentPanel.add(new Html("&nbsp;"), tableDividerRow);
	}
	
	/**
	 * Utility method to add a date range to the content panel.
	 * @param rangeLabel
	 * @param fromDateField
	 * @param toDateField
	 */
	public void addDateRange(String rangeLabel, DateField fromDateField, DateField toDateField) {
		Html rangeLabelHtml = new Html(rangeLabel);
		rangeLabelHtml.setStyleName("report-form-label");
		
		Html dividerHtml = new Html("&nbsp;&harr;&nbsp;");
		dividerHtml.setStyleName("report-form-label");

		contentPanel.add(rangeLabelHtml,	tableDataLabel1);		
		contentPanel.add(fromDateField,		tableDataField);
		contentPanel.add(dividerHtml,		tableDataLabel2);		
		contentPanel.add(toDateField,		tableDataField);
	}
	
	/**
	 * Utility method to get a date field.
	 * @return
	 */
	public BoundDateField getDateField() {
		BoundDateField dateField = FieldFactory.getBoundDateField("");
		dateField.enable();
//		dateField.setWidth(200);
		
		return dateField;
	}
	
	/**
	 * Set up and get a button bar with buttons to expand or collapse the tree.
	 * @return
	 */
	public ToolBar getButtonsBar() {
		ButtonBar toolbar = new ButtonBar();
		toolbar.setAlignment(HorizontalAlignment.CENTER);
		
//		saveButton = new Button("Save Changes");
		if (saveButton != null) {
			IconSupplier.forceIcon(saveButton, IconSupplier.getSaveIconName());
			saveButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
				   
				@Override
				public void componentSelected(ButtonEvent ce) {
					doUpdate();
				}  
			 
			});
		}
		
//		cancelButton = new Button("Cancel Changes");
		if (cancelButton != null) {
			IconSupplier.forceIcon(cancelButton, IconSupplier.getCancelIconName());
			cancelButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
				   
				@Override
				public void componentSelected(ButtonEvent ce) {
					doReset();
				}  
			 
			});
		}
		
//		clearButton = new Button("Clear Snapshot Data");
		if (clearButton != null) {
			IconSupplier.forceIcon(clearButton, IconSupplier.getSnapshotClearIconName());
			clearButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
				   
				@Override
				public void componentSelected(ButtonEvent ce) {
					doSnapshotClear();
				}  
			 
			});
		}
		
		if (saveButton != null)		toolbar.add(saveButton);
		if (cancelButton != null)	toolbar.add(cancelButton);
		if (clearButton != null) 	toolbar.add(clearButton);
		
		return toolbar;
	}

	protected void loadSnapshot() {
		snapshotParameterSetGetService.getSnapshotParameterSet(snapshot.getSnapshotId(), getSource(),
				new AsyncCallback<SnapshotParameterSetInstance>() {
					public void onFailure(Throwable caught) {
						// Show the RPC error message to the user
						if (caught instanceof IllegalArgumentException)
							MessageBox.alert("Alert", caught.getMessage(), null);
						else {
							MessageBox.alert("Alert", "Snapshot parameter load failed unexpectedly.", null);
							System.out.println(caught.getClass().getName());
							System.out.println(caught.getMessage());
						}
					}

					public void onSuccess(SnapshotParameterSetInstance snapshotParameterSet) {
						showParameterSet(snapshotParameterSet);
					}
			});
	}
	
	protected void showParameterSet(SnapshotParameterSetInstance snapshotParameterSet) {
		
		clearValues();
		setFieldStates();
		
		setFields(snapshotParameterSet);
		
		setOriginalValues();
		
	}
	
	/**
	 * Implement this method to populate the form fields from an existing snapshot parameter set.
	 * @param snapshotParameterSet
	 */
	protected abstract void setFields(SnapshotParameterSetInstance snapshotParameterSet);
	
	/**
	 * Utility method to set a date range parameter from a pair of date fields
	 * @param snapshotParameterSet
	 * @param name
	 * @param fromField
	 * @param toField
	 */
	protected void setDateFieldRange(SnapshotParameterSetInstance snapshotParameterSet, String name, DateField fromField, DateField toField) {
		SnapshotParameterValueObject value = snapshotParameterSet.getValue(name);
		if (value != null) {
			fromField.setValue(value.getDateValue());
			if (value.isRange())
				toField.setValue(value.getToDateValue());
			else
				toField.setValue(null);
		} else {
			fromField.setValue(null);
			toField.setValue(null);
		}
	}
	
	/**
	 * Utility method to set a list of values from a check box group.
	 * @param snapshotParameterSet
	 * @param name
	 * @param checkGroup
	 */
	protected void setCheckBoxes(SnapshotParameterSetInstance snapshotParameterSet, String name, CheckBoxGroup checkGroup) {
		List<SnapshotParameterValueObject> values = snapshotParameterSet.getValues(name);
		if (values != null) {
			for (SnapshotParameterValueObject value : values) {
				int count = checkGroup.getAll().size();
				for (int i = 0; i < count; i++) {
					CheckBox checkBox = (CheckBox) checkGroup.get(i);
					if (checkBox.getValueAttribute().equals(value.getStringValue()))
						checkBox.setValue(true);
				}
			}
		}
	}

	protected void setFieldStates() {
		for (Field<?> field : getFields() ) {
			field.setEnabled(snapshot.getSnapshotTaken() == null);
		}
	}

	protected void clearValues() {
		for (Field<?> field : getFields() ) {
			if (field instanceof CheckBoxGroup) {
				CheckBoxGroup cbg = (CheckBoxGroup) field;
				for (Field<?> cbf : cbg.getAll()) {
					if (cbf instanceof CheckBox) {
						CheckBox cb = (CheckBox) cbf;
						cb.setValue(false);
						cb.setOriginalValue(false);
					} else {
						cbf.setValue(null);
						cbf.setOriginalValue(null);
					}
				}
			} else {
				field.setValue(null);
				field.setOriginalValue(null);
			}
		}
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	protected void setOriginalValues() {
		for (Field field : getFields() ) {
			if (field instanceof CheckBoxGroup) {
				CheckBoxGroup cbg = (CheckBoxGroup) field;
				for (Field cbf : cbg.getAll()) {
					if (cbf instanceof CheckBox) {
						CheckBox cb = (CheckBox) cbf;
						cb.setOriginalValue(cb.getValue());
					} else {
						cbf.setOriginalValue(cbf.getValue());
					}
				}
			} else {
				field.setOriginalValue(field.getValue());
			}
		}
	}
	
	/**
	 * The source value for this parameter set.  May be overridden.  Defaults to the simple name of this class.
	 * @return
	 */
	protected String getSource() {
		return SOURCE;
	}
	
	/**
	 * Update the database with the current tree settings.
	 */
	protected void doUpdate() {
		handleCleanForm();
		
		SnapshotParameterSetInstance snapshotParameterSet = new SnapshotParameterSetInstance();
		
		snapshotParameterSet.setSnapshotId(snapshot.getSnapshotId());
		snapshotParameterSet.setSource(getSource());
		
		addParametersFromFields(snapshotParameterSet);
		
		updateSnapshotParameterSetService.updateSnapshotParameterSet(snapshotParameterSet,
				new AsyncCallback<String>() {
					public void onFailure(Throwable caught) {
						// Show the RPC error message to the user
						if (caught instanceof IllegalArgumentException)
							MessageBox.alert("Alert", caught.getMessage(), null);
						else {
							MessageBox.alert("Alert", "Update of parameter set for snapshot " + snapshot.getSnapshotId() + " failed unexpectedly.", null);
							System.out.println(caught.getClass().getName());
							System.out.println(caught.getMessage());
						}
					}

					public void onSuccess(String result) {
						setOriginalValues();
						handleCleanForm();
					}
				});
	}
	
	/**
	 * Implement this to populate the snapshot parameter set from all fields on the page
	 * @param snapshotParameterSet
	 */
	public abstract void addParametersFromFields(SnapshotParameterSetInstance snapshotParameterSet);
	
	/**
	 * Add a checkbox group as a list of values to the snapshot parameter set.
	 * @param checkBoxGroup
	 * The check box group to be added.
	 * @param snapshotParameterSet
	 * The parameter set.
	 * @param name
	 * The name of the parameter.
	 * @param groupName
	 * The group name for the parameter.
	 */
	public void addParametersFrom(CheckBoxGroup checkBoxGroup, SnapshotParameterSetInstance snapshotParameterSet, String name, String groupName) {
		for (CheckBox checkBox : checkBoxGroup.getValues()) {
			if (checkBox.getValue())
				snapshotParameterSet.addValue(name, groupName, checkBox.getValueAttribute());
		}
	}
	
	protected void doSnapshotClear() {
		// This listener does the update if confirmed by the user
		final Listener<MessageBoxEvent> confirmClear = new Listener<MessageBoxEvent>() {  
			public void handleEvent(MessageBoxEvent ce) {
				Button btn = ce.getButtonClicked();
				if ("Yes".equals(btn.getText()))
					userConfirmClear();
			}  
		};
		MessageBox.confirm("Are You Sure?", "This action will clear the data currently gathered for this snapshot.  Proceed?", confirmClear);
	}
	
	@SuppressWarnings("unused")
	protected void userConfirmClear() {
		if (true) {
			clearSnapshot();
		} else {
			// This listener does the update if confirmed by the user
			final Listener<MessageBoxEvent> confirmClear = new Listener<MessageBoxEvent>() {  
				public void handleEvent(MessageBoxEvent ce) {
					Button btn = ce.getButtonClicked();
					if ("Yes".equals(btn.getText()))
						clearSnapshot();
				}  
			};
			MessageBox.confirm("Are You Sure?", "You are not the creator of this snapshot.  Are you sure you wish to proceed?", confirmClear);
		}
	}
	
	

	protected void clearSnapshot() {
		clearButton.disable();
		if (snapshot.getSnapshotTaken() == null) {
			return;
		}
		snapshotClearService.clearSnapshot(snapshot.getSnapshotId(), snapshot.getSnapshotTaken(),
				new AsyncCallback<String>() {
					public void onFailure(Throwable caught) {
						// Show the RPC error message to the user
						if (caught instanceof IllegalArgumentException)
							MessageBox.alert("Alert", caught.getMessage(), null);
						else {
							MessageBox.alert("Alert", "Snapshot clear failed unexpectedly.", null);
							System.out.println(caught.getClass().getName());
							System.out.println(caught.getMessage());
						}
					}

					public void onSuccess(String result) {
						clearButton.disable();
						snapshot.setSnapshotTaken(null);
						setFieldStates();	// To enable them now that the snapshot is cleared
						if (parentCardPanel != null) {
							parentCardPanel.reflectSnapshotChanges(snapshot);
						}
					}
			});
	}
	
	protected void addDirtyFieldsListener() {
		if (dirtyFieldsListener == null) {
			dirtyFieldsListener = new Timer() {

				@Override
				public void run() {
					if (fieldsAreDirty())
						handleDirtyForm();
					else
						handleCleanForm();
				}
				
			};
		}
		
		dirtyFieldsListener.scheduleRepeating(DIRTY_FIELDS_LISTEN_TIME);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void doReset() {
		for (Field field : getFields()) {
			field.setValue(field.getOriginalValue());
		}
		
		handleCleanForm();
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

	@SuppressWarnings("unchecked")
	protected void getChildFields(Container<Component> c, List<Field<?>> fields) {
		 for (Component comp : c.getItems()) {
			 if (comp instanceof Field) {
				 fields.add((Field<?>) comp);
			 } else if (comp instanceof Container) {
				 getChildFields((Container<Component>) comp, fields);
			 }
		 }
	}

	  /**
	   * Returns true if any of the form's fields are dirty.
	   * 
	   * @return true for dirty
	   */
	  public boolean isDirty() {
	    for (Field<?> f : getFields()) {
	      if (f.isDirty()) {
	        return true;
	      }
	    }
	    return false;
	  }
	
	public boolean fieldsAreDirty() {
		return isDirty();
	}
	
	public void handleDirtyForm() {
		if (saveButton != null && snapshot.getSnapshotTaken() == null) saveButton.enable();
		if (cancelButton != null) cancelButton.enable();
	}
	
	public void handleCleanForm() {
		if (saveButton != null) saveButton.disable();
		if (cancelButton != null) cancelButton.disable();
	}

	@Override
	public ContentPanel getContentPanel() {
		return contentPanel;
	}
	
	@Override
	public void setSnapshot(SnapshotInstance snapshot) {
		super.setSnapshot(snapshot);
		clearButton.setEnabled(snapshot.getSnapshotTaken() != null);
		loadSnapshot();
	}
	
	/**
	 * Turn on the listener timer when waking up.
	 */
	@Override
	public void awaken() {
		if (dirtyFieldsListener != null)
			dirtyFieldsListener.scheduleRepeating(DIRTY_FIELDS_LISTEN_TIME);
	}

	/**
	 * Turn off the listener timer when going to sleep.
	 */
	@Override
	public void sleep() {
		if (dirtyFieldsListener != null)
			dirtyFieldsListener.cancel();
	}
	
}
