package com.scholastic.sbam.client.uiobjects.uireports;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.Container;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.ButtonBar;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.scholastic.sbam.client.services.SnapshotParameterSetGetService;
import com.scholastic.sbam.client.services.SnapshotParameterSetGetServiceAsync;
import com.scholastic.sbam.client.services.UpdateSnapshotParameterSetService;
import com.scholastic.sbam.client.services.UpdateSnapshotParameterSetServiceAsync;
import com.scholastic.sbam.client.uiobjects.fields.BoundDateField;
import com.scholastic.sbam.client.uiobjects.fields.DateDefaultBinder;
import com.scholastic.sbam.client.uiobjects.fields.DateRangeBinder;
import com.scholastic.sbam.client.uiobjects.foundation.AppSleeper;
import com.scholastic.sbam.client.uiobjects.foundation.FieldFactory;
import com.scholastic.sbam.client.util.IconSupplier;
import com.scholastic.sbam.shared.objects.SnapshotInstance;
import com.scholastic.sbam.shared.objects.SnapshotParameterSetInstance;
import com.scholastic.sbam.shared.objects.SnapshotParameterValueObject;
import com.scholastic.sbam.shared.util.AppConstants;

public class TermCriteriaCard extends SnapshotCardBase implements AppSleeper {
	
	protected final int				DIRTY_FIELDS_LISTEN_TIME	=	250;
	protected final String			SOURCE						=	AppConstants.getSimpleName(this);
	protected final String			DATES_GROUP					=	"Dates";
	protected final String			START_DATE					=	"startDate";
	protected final String			END_DATE					=	"endDate";
	protected final String			TERMINATE_DATE				=	"terminateDate";
	
	protected ContentPanel			contentPanel;
	
	protected Timer					dirtyFieldsListener;
	
	protected TableData				tableDataLabel1;
	protected TableData				tableDataLabel2;
	protected TableData				tableDataField;
	protected TableData				tableDividerRow;
	
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
	
	protected Button				saveButton;
	protected Button				resetButton;
	
	private final UpdateSnapshotParameterSetServiceAsync	updateSnapshotParameterSetService	= GWT.create(UpdateSnapshotParameterSetService.class);
	private final SnapshotParameterSetGetServiceAsync		snapshotParameterSetGetService		= GWT.create(SnapshotParameterSetGetService.class);
	
	public TermCriteriaCard() {
		super();
		this.headingToolTip = "Use this panel to specify term criteria for the snapshot.";
		
		startFromDate		=	getDateField();
		startToDate			=	getDateField();

		endFromDate			=	getDateField();
		endToDate			=	getDateField();

		terminateFromDate	=	getDateField();
		terminateToDate		=	getDateField();
	}

	@Override
	public void addPanelContent() {
		contentPanel = new ContentPanel();
		contentPanel.setHeading("Snapshot Terms Selector");
		IconSupplier.setIcon(contentPanel, IconSupplier.getTermTypeIconName());
		
		createTableDataSpecifications();
		
		addButtonRow();
		
		addTermCriteriaFields();
		
		addDirtyFieldsListener();
		
		add(contentPanel);
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
		
	}
	
	protected void addTermCriteriaFields() {

		//  Start / end / terminate range binding
		
		startFromDate.bindLow(startRangeBinder);
		startToDate.bindHigh(startRangeBinder);
		
		endFromDate.bindLow(endRangeBinder);
		endToDate.bindHigh(endRangeBinder);
		
		terminateFromDate.bindLow(terminateRangeBinder);
		terminateToDate.bindHigh(terminateRangeBinder);
		
		addDividerRow();
		addDateRange("Start Date:", 	startFromDate,		startToDate);
		addDateRange("End Date:",		endFromDate,		endToDate);
		addDateRange("Terminate Date:",	terminateFromDate,	terminateToDate);
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
		
		saveButton = new Button("Save");
		IconSupplier.forceIcon(saveButton, IconSupplier.getSaveIconName());
		saveButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			   
			@Override
			public void componentSelected(ButtonEvent ce) {
				doUpdate();
			}  
		 
		});
		
		resetButton = new Button("Reset");
		IconSupplier.forceIcon(resetButton, IconSupplier.getRefreshIconName());
		resetButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			   
			@Override
			public void componentSelected(ButtonEvent ce) {
				doReset();
			}  
		 
		});
		
		toolbar.add(saveButton);
		toolbar.add(resetButton);
		
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
							MessageBox.alert("Alert", "Agreement access failed unexpectedly.", null);
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
		
		setDateFieldRange(snapshotParameterSet, START_DATE,		startFromDate,		startToDate);
		setDateFieldRange(snapshotParameterSet, END_DATE,		endFromDate,		endToDate);
		setDateFieldRange(snapshotParameterSet, TERMINATE_DATE, terminateFromDate,	terminateToDate);
		
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

	@SuppressWarnings({"unchecked", "rawtypes"})
	protected void clearValues() {
		for (Field field : getFields() ) {
			field.setValue(null);
			field.setOriginalValue(null);
		}
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	protected void setOriginalValues() {
		for (Field field : getFields() ) {
			field.setOriginalValue(field.getValue());
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
		if (saveButton != null) saveButton.enable();
		if (resetButton != null) resetButton.enable();
	}
	
	public void handleCleanForm() {
		if (saveButton != null) saveButton.disable();
		if (resetButton != null) resetButton.disable();
	}

	@Override
	public ContentPanel getContentPanel() {
		return contentPanel;
	}
	
	@Override
	public void setSnapshot(SnapshotInstance snapshot) {
		super.setSnapshot(snapshot);
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
