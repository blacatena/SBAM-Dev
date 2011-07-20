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
import com.scholastic.sbam.client.uiobjects.fields.DateDefaultBinder;
import com.scholastic.sbam.client.uiobjects.fields.DateRangeBinder;
import com.scholastic.sbam.client.uiobjects.fields.EnhancedCheckBoxGroup;
import com.scholastic.sbam.client.uiobjects.foundation.AppSleeper;
import com.scholastic.sbam.client.uiobjects.foundation.FieldFactory;
import com.scholastic.sbam.client.util.IconSupplier;
import com.scholastic.sbam.client.util.UiConstants;
import com.scholastic.sbam.shared.objects.CommissionTypeInstance;
import com.scholastic.sbam.shared.objects.SnapshotInstance;
import com.scholastic.sbam.shared.objects.SnapshotParameterSetInstance;
import com.scholastic.sbam.shared.objects.SnapshotParameterValueObject;
import com.scholastic.sbam.shared.objects.TermTypeInstance;
import com.scholastic.sbam.shared.util.AppConstants;

public class TermCriteriaCard extends SnapshotCardBase implements AppSleeper {
	
	protected final int				DIRTY_FIELDS_LISTEN_TIME	=	250;
	protected final String			SOURCE						=	AppConstants.getSimpleName(this);
	protected final String			DATES_GROUP					=	"Dates";
	protected final String			START_DATE					=	"startDate";
	protected final String			END_DATE					=	"endDate";
	protected final String			TERMINATE_DATE				=	"terminateDate";
	protected final String			TERM_TYPES					=	"termTypes";
	protected final String			PROD_COMM_CODES				=	"productCommCodes";
	protected final String			AGREEMENT_COMM_CODES		=	"agreementCommCodes";
	protected final String			TERM_COMM_CODES				=	"termCommCodes";
	
	protected ContentPanel			contentPanel				=	 getNewContentPanel();
	
	protected Timer					dirtyFieldsListener;
	
	protected TableData				tableDataLabel1;
	protected TableData				tableDataLabel2;
	protected TableData				tableDataField;
	protected TableData				tableDividerRow;
	protected TableData				table3ColumnField;
	
	protected CheckBoxGroup			termTypeCheckGroup		=	new EnhancedCheckBoxGroup();
	protected CheckBoxGroup			prodCommCheckGroup		=	new EnhancedCheckBoxGroup();
	protected CheckBoxGroup			agreementCommCheckGroup	=	new EnhancedCheckBoxGroup();
	protected CheckBoxGroup			termCommCheckGroup		=	new EnhancedCheckBoxGroup();
	
	protected BoundDateField		startFromDate			=	getDateField();
	protected BoundDateField		startToDate				=	getDateField();

	protected BoundDateField		endFromDate				=	getDateField();
	protected BoundDateField		endToDate				=	getDateField();

	protected BoundDateField		terminateFromDate		=	getDateField();
	protected BoundDateField		terminateToDate			=	getDateField();

	protected DateRangeBinder		startRangeBinder		= new DateRangeBinder();
	protected DateRangeBinder		endRangeBinder			= new DateRangeBinder();
	protected DateRangeBinder		terminateRangeBinder	= new DateRangeBinder();
	protected DateDefaultBinder		terminateDefaultBinder	= new DateDefaultBinder(60);
	
	protected Button				saveButton				= new Button("Save Changes");
	protected Button				cancelButton			= new Button("Cancel Changes");
	protected Button				clearButton				= new Button("Clear Snapshot Data");
	
	private final UpdateSnapshotParameterSetServiceAsync	updateSnapshotParameterSetService	= GWT.create(UpdateSnapshotParameterSetService.class);
	private final SnapshotParameterSetGetServiceAsync		snapshotParameterSetGetService		= GWT.create(SnapshotParameterSetGetService.class);
	private final SnapshotClearServiceAsync					snapshotClearService				= GWT.create(SnapshotClearService.class);
	
	public TermCriteriaCard() {
		super();
		
		this.headingToolTip = "Use this panel to specify term criteria for the snapshot.";
		
		termTypeCheckGroup.setName(TERM_TYPES);
		for (BeanModel termTypeBean : UiConstants.getTermTypes().getModels()) {
			TermTypeInstance termType = termTypeBean.getBean();
			if (termType.getStatus() == AppConstants.STATUS_ACTIVE) {
				CheckBox checkBox = new CheckBox();
				checkBox.setName(termType.getTermTypeCode());
				checkBox.setBoxLabel(termType.getDescription());
				checkBox.setValueAttribute(termType.getTermTypeCode());
				termTypeCheckGroup.add(checkBox);
			}
		}
		
		prodCommCheckGroup.setName(PROD_COMM_CODES);
		for (BeanModel commTypeBean : UiConstants.getCommissionTypes(UiConstants.CommissionTypeTargets.PRODUCT).getModels()) {
			CommissionTypeInstance commType = commTypeBean.getBean();
			if (commType.getStatus() == AppConstants.STATUS_ACTIVE) {
				CheckBox checkBox = new CheckBox();
				checkBox.setName(commType.getCommissionCode());
				checkBox.setBoxLabel(commType.getDescription());
				checkBox.setValueAttribute(commType.getCommissionCode());
				prodCommCheckGroup.add(checkBox);
			}
		}
		
		agreementCommCheckGroup.setName(AGREEMENT_COMM_CODES);
		for (BeanModel commTypeBean : UiConstants.getCommissionTypes(UiConstants.CommissionTypeTargets.AGREEMENT).getModels()) {
			CommissionTypeInstance commType = commTypeBean.getBean();
			if (commType.getStatus() == AppConstants.STATUS_ACTIVE) {
				CheckBox checkBox = new CheckBox();
				checkBox.setName(commType.getCommissionCode());
				checkBox.setBoxLabel(commType.getDescription());
				checkBox.setValueAttribute(commType.getCommissionCode());
				agreementCommCheckGroup.add(checkBox);
			}
		}
		
		termCommCheckGroup.setName(PROD_COMM_CODES);
		for (BeanModel commTypeBean : UiConstants.getCommissionTypes(UiConstants.CommissionTypeTargets.AGREEMENT_TERM).getModels()) {
			CommissionTypeInstance commType = commTypeBean.getBean();
			if (commType.getStatus() == AppConstants.STATUS_ACTIVE) {
				CheckBox checkBox = new CheckBox();
				checkBox.setName(commType.getCommissionCode());
				checkBox.setBoxLabel(commType.getDescription());
				checkBox.setValueAttribute(commType.getCommissionCode());
				termCommCheckGroup.add(checkBox);
			}
		}
		
//		
//		startFromDate		=	getDateField();
//		startToDate			=	getDateField();
//
//		endFromDate			=	getDateField();
//		endToDate			=	getDateField();
//
//		terminateFromDate	=	getDateField();
//		terminateToDate		=	getDateField();
	}

	@Override
	public void addPanelContent() {
//		contentPanel = new ContentPanel();
//		contentPanel.setHeading(getPanelTitle());
//		IconSupplier.setIcon(contentPanel, IconSupplier.getTermTypeIconName());
		
		createTableDataSpecifications();
		
		addButtonRow();
		
		addTermCriteriaFields();
		
		addDirtyFieldsListener();
		
		add(contentPanel);
	}
	
	public ContentPanel getNewContentPanel() {
		ContentPanel contentPanel = new ContentPanel();
		contentPanel.setHeading(getPanelTitle());
		IconSupplier.setIcon(contentPanel, IconSupplier.getTermTypeIconName());
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
	
	protected void addTermCriteriaFields() {

		//  Start / end / terminate range binding
		
		startFromDate.bindLow(startRangeBinder);
		startToDate.bindHigh(startRangeBinder);
		
		endFromDate.bindLow(endRangeBinder);
		endToDate.bindHigh(endRangeBinder);
		
		terminateFromDate.bindLow(terminateRangeBinder);
		terminateToDate.bindHigh(terminateRangeBinder);
		
		/* Dates */
		
		addDividerRow();
		addDateRange("Start Date:", 	startFromDate,		startToDate);
		addDateRange("End Date:",		endFromDate,		endToDate);
		addDateRange("Terminate Date:",	terminateFromDate,	terminateToDate);

		/* Term Types */

		addDividerRow();
		Html termTypeLabelHtml = new Html("Term Types:");
		termTypeLabelHtml.setStyleName("report-form-label");
		contentPanel.add(termTypeLabelHtml,		tableDataLabel1);
		contentPanel.add(termTypeCheckGroup,	table3ColumnField);

		/* Product Commission Types */

		addDividerRow();
		Html prodCommTypeLabelHtml = new Html("Product Commission Codes:");
		prodCommTypeLabelHtml.setStyleName("report-form-label");
		contentPanel.add(prodCommTypeLabelHtml,		tableDataLabel1);
		contentPanel.add(prodCommCheckGroup,		table3ColumnField);

		/* Agreeement Commission Types */

		addDividerRow();
		Html agreementCommTypeLabelHtml = new Html("Agreement Commission Codes:");
		agreementCommTypeLabelHtml.setStyleName("report-form-label");
		contentPanel.add(agreementCommTypeLabelHtml,tableDataLabel1);
		contentPanel.add(agreementCommCheckGroup,	table3ColumnField);

		/* Agreement Term Commission Types */

		addDividerRow();
		Html termCommTypeLabelHtml = new Html("Term Commission Codes:");
		termCommTypeLabelHtml.setStyleName("report-form-label");
		contentPanel.add(termCommTypeLabelHtml,		tableDataLabel1);
		contentPanel.add(termCommCheckGroup,		table3ColumnField);
	}
	
	public void addButtonRow() {
		contentPanel.add(getButtonsBar(), tableDividerRow);
	}
	
	public void addDividerRow() {
		contentPanel.add(new Html("&nbsp;"), tableDividerRow);
	}
	
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
		IconSupplier.forceIcon(saveButton, IconSupplier.getSaveIconName());
		saveButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			   
			@Override
			public void componentSelected(ButtonEvent ce) {
				doUpdate();
			}  
		 
		});
		
//		cancelButton = new Button("Cancel Changes");
		IconSupplier.forceIcon(cancelButton, IconSupplier.getCancelIconName());
		cancelButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			   
			@Override
			public void componentSelected(ButtonEvent ce) {
				doReset();
			}  
		 
		});
		
//		clearButton = new Button("Clear Snapshot Data");
		IconSupplier.forceIcon(clearButton, IconSupplier.getSnapshotClearIconName());
		clearButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			   
			@Override
			public void componentSelected(ButtonEvent ce) {
				doSnapshotClear();
			}  
		 
		});
		
		toolbar.add(saveButton);
		toolbar.add(cancelButton);
		toolbar.add(clearButton);
		
		return toolbar;
	}

	protected void loadSnapshot() {
		snapshotParameterSetGetService.getSnapshotParameterSet(snapshot.getSnapshotId(), SOURCE,
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
		
		setDateFieldRange(snapshotParameterSet, START_DATE,		startFromDate,		startToDate);
		setDateFieldRange(snapshotParameterSet, END_DATE,		endFromDate,		endToDate);
		setDateFieldRange(snapshotParameterSet, TERMINATE_DATE, terminateFromDate,	terminateToDate);
		
		setCheckBoxes(snapshotParameterSet, TERM_TYPES,		 		termTypeCheckGroup);
		setCheckBoxes(snapshotParameterSet, PROD_COMM_CODES, 		prodCommCheckGroup);
		setCheckBoxes(snapshotParameterSet, AGREEMENT_COMM_CODES,	agreementCommCheckGroup);
		setCheckBoxes(snapshotParameterSet, TERM_COMM_CODES, 		termCommCheckGroup);
		
		setOriginalValues();
		
	}
	
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
	 * Update the database with the current tree settings.
	 */
	protected void doUpdate() {
		handleCleanForm();
		
		SnapshotParameterSetInstance snapshotParameterSet = new SnapshotParameterSetInstance();
		
		snapshotParameterSet.setSnapshotId(snapshot.getSnapshotId());
		snapshotParameterSet.setSource(SOURCE);
		
		snapshotParameterSet.addValue(START_DATE,		DATES_GROUP, startFromDate.getValue(),		startToDate.getValue());
		snapshotParameterSet.addValue(END_DATE,			DATES_GROUP, endFromDate.getValue(),		endToDate.getValue());
		snapshotParameterSet.addValue(TERMINATE_DATE,	DATES_GROUP, terminateFromDate.getValue(),	terminateToDate.getValue());
		
		for (CheckBox checkBox : termTypeCheckGroup.getValues()) {
			if (checkBox.getValue())
				snapshotParameterSet.addValue(TERM_TYPES, TERM_TYPES, checkBox.getValueAttribute());
		}
		
		for (CheckBox checkBox : prodCommCheckGroup.getValues()) {
			if (checkBox.getValue())
				snapshotParameterSet.addValue(PROD_COMM_CODES, PROD_COMM_CODES, checkBox.getValueAttribute());
		}
		
		for (CheckBox checkBox : agreementCommCheckGroup.getValues()) {
			if (checkBox.getValue())
				snapshotParameterSet.addValue(AGREEMENT_COMM_CODES, AGREEMENT_COMM_CODES, checkBox.getValueAttribute());
		}
		
		for (CheckBox checkBox : termCommCheckGroup.getValues()) {
			if (checkBox.getValue())
				snapshotParameterSet.addValue(TERM_COMM_CODES, TERM_COMM_CODES, checkBox.getValueAttribute());
		}
		
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

	@Override
	public String getPanelTitle() {
		return "Snapshot Terms Selector";
	}
	
	
}
