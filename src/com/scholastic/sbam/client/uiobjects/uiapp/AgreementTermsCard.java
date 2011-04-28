package com.scholastic.sbam.client.uiobjects.uiapp;

import java.util.List;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.MultiField;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.RowExpander;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.scholastic.sbam.client.services.AgreementTermListService;
import com.scholastic.sbam.client.services.AgreementTermListServiceAsync;
import com.scholastic.sbam.client.services.UpdateAgreementTermNoteService;
import com.scholastic.sbam.client.services.UpdateAgreementTermNoteServiceAsync;
import com.scholastic.sbam.client.services.UpdateAgreementTermService;
import com.scholastic.sbam.client.services.UpdateAgreementTermServiceAsync;
import com.scholastic.sbam.client.uiobjects.fields.BoundDateField;
import com.scholastic.sbam.client.uiobjects.fields.BoundSliderField;
import com.scholastic.sbam.client.uiobjects.fields.DateDefaultBinder;
import com.scholastic.sbam.client.uiobjects.fields.DateRangeBinder;
import com.scholastic.sbam.client.uiobjects.fields.EnhancedComboBox;
import com.scholastic.sbam.client.uiobjects.fields.NotesIconButtonField;
import com.scholastic.sbam.client.uiobjects.foundation.FormAndGridPanel;
import com.scholastic.sbam.client.uiobjects.foundation.FormInnerPanel;
import com.scholastic.sbam.client.util.UiConstants;
import com.scholastic.sbam.shared.objects.AgreementTermInstance;
import com.scholastic.sbam.shared.objects.CancelReasonInstance;
import com.scholastic.sbam.shared.objects.CommissionTypeInstance;
import com.scholastic.sbam.shared.objects.ProductInstance;
import com.scholastic.sbam.shared.objects.SimpleKeyProvider;
import com.scholastic.sbam.shared.objects.TermTypeInstance;
import com.scholastic.sbam.shared.objects.UpdateResponse;
import com.scholastic.sbam.shared.util.AppConstants;
import com.scholastic.sbam.shared.validation.DatesSliderBinder;

public class AgreementTermsCard extends FormAndGridPanel<AgreementTermInstance> {
	
	protected final AgreementTermListServiceAsync		agreementTermListService		= GWT.create(AgreementTermListService.class);
	protected final UpdateAgreementTermServiceAsync		updateAgreementTermService		= GWT.create(UpdateAgreementTermService.class);
	protected final UpdateAgreementTermNoteServiceAsync	updateAgreementTermNoteService	= GWT.create(UpdateAgreementTermNoteService.class);
	
	protected FormInnerPanel				formColumn1;
	protected FormInnerPanel				formColumn2;
	protected FormInnerPanel				formRow2;
	
	protected ListStore<BeanModel>	agreementGridStore;
	
	protected RowExpander					noteExpander;

	protected MultiField<String>			idNotesCombo		= new MultiField<String>("Agreement #");
	protected LabelField					agreementIdField	= new LabelField();
	protected NotesIconButtonField<String>	notesField			= getNotesButtonField();
	protected EnhancedComboBox<BeanModel>	productField		= getComboField("product", 	"Product",	280,		
																	"The product to deliver for this term.",	
																	UiConstants.getProducts(), "productCode", "descriptionAndCode");
	protected NumberField					dollarValueField	= getDollarField("Value");
	protected BoundDateField				startDateField		= getBoundDateField("Start");
	protected BoundDateField				endDateField		= getBoundDateField("End");
	protected BoundDateField				terminateDateField	= getBoundDateField("Terminate");
	protected BoundSliderField				startEndSliderField = getBoundSliderField("Days");
	protected BoundSliderField				endTerminateSliderField = getBoundSliderField("Days");
	protected EnhancedComboBox<BeanModel>	termTypeField		= getComboField("termType", 		"Term Type",	0,		
																		"The term type for this product term.",	
																		UiConstants.getTermTypes(), "termTypeCode", "description");
	protected EnhancedComboBox<BeanModel>	commissionTypeField	= getComboField("commissionType", 	"Commission",	290,		
																		"The commission code assigned to this product term for reporting purposes.",	
																		UiConstants.getCommissionTypes(UiConstants.CommissionTypeTargets.AGREEMENT_TERM), "commissionCode", "descriptionAndCode");
	protected EnhancedComboBox<BeanModel>	cancelReasonField	= getComboField("cancelReason", 	"Cancel",	290,		
																		"The reason for canceling for this product term.",	
																		UiConstants.getCancelReasons(), "cancelReasonCode", "descriptionAndCode");
//	protected LabelField					statusField			= new LabelField();
	
	protected NumberField					referenceSaIdField	= getIntegerField("Reference",		50);
	protected TextField<String>				poNumberField		= getTextField("PO #");
	protected TextField<String>				groupField			= getTextField("Group");

//	protected NumberField					buildingsField		= getIntegerField("Buildings",		50);
//	protected NumberField					populationField		= getIntegerField("Population",		50);
//	protected NumberField					enrollmentField		= getIntegerField("Enrollment",		50);
//	protected NumberField					workstationsField	= getIntegerField("Workstations",	50);
	
	protected DateRangeBinder				startEndRangeBinder = new DateRangeBinder();
	protected DatesSliderBinder				startEndSliderBinder = new DatesSliderBinder(365 * 2);
	protected DatesSliderBinder				endTerminateSliderBinder = new DatesSliderBinder(150);
	protected DateDefaultBinder				terminateDefaultBinder	= new DateDefaultBinder(60);
	
	public int getAgreementId() {
		return getFocusId();
	}
	
	public void setAgreementId(int agreementId) {
		setFocusId(agreementId);
	}
	
	public void setAgreementTerm(AgreementTermInstance instance) {
		setFocusInstance(instance);
	}

	@Override
	public void awaken() {
	}

	@Override
	public void sleep() {
	}
	
	public String getFormHeading() {
		return "Product Terms";
	}
	
	@Override
	public void addGridPlugins(Grid<BeanModel> grid) {
		grid.addPlugin(noteExpander);
	}
	
	/**
	 * Override to set any further grid attributes, such as the autoExpandColumn.
	 * @param grid
	 */
	@Override
	public void setGridAttributes(Grid<BeanModel> grid) {
		grid.setAutoExpandColumn("product.description");
		gridStore.setKeyProvider(new SimpleKeyProvider("uniqueKey"));
	}

	@Override
	public void addGridColumns(List<ColumnConfig> columns) {
		columns.add(getDisplayColumn("product.description",		"Product",					200,
					"This is the product ordered."));
		columns.add(getDisplayColumn("startDate",				"Start",					80,		true, UiConstants.APP_DATE_TIME_FORMAT,
					"This is the service start date for a product term."));
		columns.add(getDisplayColumn("endDate",					"End",						80,		true, UiConstants.APP_DATE_TIME_FORMAT,
					"This is the service end date for a product term."));
		columns.add(getDisplayColumn("terminateDate",			"Terminate",				80,		true, UiConstants.APP_DATE_TIME_FORMAT,
					"This is the actual service termination date for a product term."));
		columns.add(getDisplayColumn("dollarValue",				"Value",					80,		true, UiConstants.DOLLARS_FORMAT,
					"This is the value of the product term."));
		columns.add(getDisplayColumn("termType.description",	"Type",						50,
					"This is the type of product term."));
		columns.add(getDisplayColumn("statusDescription",		"Status",					50,
					"This is the status of product term."));
	
		noteExpander = getNoteExpander();
		columns.add(noteExpander);
	}

	@Override
	public void setFormFieldValues(AgreementTermInstance instance) {
		String displayStatus = "Term " + AppConstants.getStatusDescription(instance.getStatus());
		if (instance.getStatus() == AppConstants.STATUS_INACTIVE && instance.getCancelReasonCode() != null && instance.getCancelReasonCode().length() > 0 && instance.getCancelDate() != null)
			displayStatus = "Canceled " + UiConstants.formatDate(instance.getCancelDate());
		agreementIdField.setValue(AppConstants.appendCheckDigit(instance.getAgreementId()) + " &nbsp;&nbsp;&nbsp;<i>" + displayStatus + "</i>");
		productField.setValue(ProductInstance.obtainModel(instance.getProduct()));
		dollarValueField.setValue(instance.getDollarValue());
		
		startDateField.setUnbound();
		startDateField.setValue(instance.getStartDate());
		endDateField.setValue(instance.getEndDate());
		startEndRangeBinder.setDependentFields();
		startEndSliderBinder.setDependentFields();
		endTerminateSliderBinder.setDependentFields();
		startDateField.setBound();
		
		terminateDateField.setValue(instance.getTerminateDate());
		termTypeField.setValue(TermTypeInstance.obtainModel(instance.getTermType()));
		cancelReasonField.setValue(CancelReasonInstance.obtainModel(instance.getCancelReason()));
		commissionTypeField.setValue(CommissionTypeInstance.obtainModel(instance.getCommissionType()));
		
//		if (instance.getStatus() == AppConstants.STATUS_INACTIVE && instance.getCancelReasonCode() != null && instance.getCancelReasonCode().length() > 0 && instance.getCancelDate() != null)
//			statusField.setValue("Canceled " + instance.getCancelDate());
//		statusField.setValue(AppConstants.getStatusDescription(instance.getStatus()));
		
		referenceSaIdField.setValue(instance.getReferenceSaId());
		poNumberField.setValue(instance.getPoNumber());
		groupField.setValue(instance.getOrgPath());
		
//		buildingsField.setValue(instance.getBuildings());
//		populationField.setValue(instance.getPopulation());
//		enrollmentField.setValue(instance.getEnrollment());
//		workstationsField.setValue(instance.getWorkstations());

		setNotesField(instance.getNote());
		
//		setOriginalValues();
	}
	
	public void setNotesField(String note) {
		if (note != null && note.length() > 0) {
			notesField.setEditMode();
			notesField.setNote(note);
		} else {
			notesField.setAddMode();
			notesField.setNote("");			
		}
	}

	@Override
	protected void executeLoader(int id,
			AsyncCallback<List<AgreementTermInstance>> callback) {
		agreementTermListService.getAgreementTerms(id, AppConstants.STATUS_DELETED,callback);
	}

	@Override
	protected void addFormFields(FormPanel panel, FormData formData) {
		formData = new FormData("-24");
		
		TableLayout tableLayout = new TableLayout(2);
		tableLayout.setWidth("100%");
		panel.setLayout(tableLayout);
		
		TableData tData1 = new TableData();
		tData1.setWidth("50%");
		
		formColumn1 = getNewFormInnerPanel(75);
		formColumn2 = getNewFormInnerPanel(85);
		
		agreementIdField.setReadOnly(true);
		agreementIdField.setWidth(150);
		idNotesCombo.setSpacing(20);
		
		dollarValueField.setToolTip(UiConstants.getQuickTip("The total dollar value of this product for this term."));
		startDateField.setToolTip(UiConstants.getQuickTip("The date on which this product term will take effect."));
		endDateField.setToolTip(UiConstants.getQuickTip("The date on which this product term is scheduled to end."));
		terminateDateField.setToolTip(UiConstants.getQuickTip("The date on which this product term will no longer be delivered."));
		
		startEndSliderField.setToolTip(UiConstants.getQuickTip("Use this slider to adjust the end date based on the number of days from the start date"));
		endTerminateSliderField.setToolTip(UiConstants.getQuickTip("Use this slider to adjust the terminate date based on the number of days from the end date"));

		//  Start / end / terminate range binding
		
		startDateField.bindLow(startEndRangeBinder);
		endDateField.bindHigh(startEndRangeBinder);
		terminateDateField.bindHigh(startEndRangeBinder);
		
		//	Start / end slider binding
		
		startEndSliderField.getSlider().setWidth(180);
		startEndSliderField.getSlider().setMinValue(1);
		startEndSliderField.getSlider().setMaxValue(365 * 2);
		startEndSliderField.getSlider().setValue(365);
		startEndSliderField.getSlider().setIncrement(1);
		startEndSliderField.getSlider().setMessage("{0} days");
	    
	    startDateField.bindLow(startEndSliderBinder);
	    endDateField.bindHigh(startEndSliderBinder);
	    startEndSliderField.bind(startEndSliderBinder);
		
	    //	End / Terminate slider binding
	    
		endTerminateSliderField.getSlider().setWidth(180);
		endTerminateSliderField.getSlider().setMinValue(0);
		endTerminateSliderField.getSlider().setMaxValue(150);
		endTerminateSliderField.getSlider().setValue(60);
		endTerminateSliderField.getSlider().setIncrement(1);
		endTerminateSliderField.getSlider().setMessage("{0} days");
	    
	    startDateField.bindMin(endTerminateSliderBinder);
	    endDateField.bindLow(endTerminateSliderBinder);
	    terminateDateField.bindHigh(endTerminateSliderBinder);
	    endTerminateSliderField.bind(endTerminateSliderBinder);
	    
	    terminateDateField.bindTarget(terminateDefaultBinder);
	    endDateField.bindControl(terminateDefaultBinder);

	    startDateField.setWidth(0);
	    endDateField.setWidth(0);
	    terminateDateField.setWidth(0);
	    startEndSliderField.setWidth(0);
	    endTerminateSliderField.setWidth(0);
	    termTypeField.setWidth(0);
	    
		productField.setAllowBlank(false);
		termTypeField.setAllowBlank(false);
		cancelReasonField.setAllowBlank(true);
		commissionTypeField.setAllowBlank(true);
		
		//	Form column 1
		
		idNotesCombo.add(agreementIdField);	
		idNotesCombo.add(notesField);
		formColumn1.add(idNotesCombo,    formData);
		
		formColumn1.add(productField, formData);
		formColumn1.add(dollarValueField, formData);
		
		formColumn1.add(commissionTypeField, formData);
		
		formColumn1.add(referenceSaIdField, formData);
		formColumn1.add(poNumberField, formData);
		formColumn1.add(groupField, formData);
		
//		formColumn1.add(statusField, formData);
		formColumn1.add(cancelReasonField, formData);
		
		//	Form column 2
		
		FieldSet datesFieldSet = new FieldSet();
		FormLayout layout = new FormLayout();
		layout.setLabelAlign(panel.getLabelAlign());
		layout.setLabelWidth(panel.getLabelWidth() - 10);
		datesFieldSet.setLayout(layout);
		datesFieldSet.setBorders(true);
		datesFieldSet.setHeading("Term Dates");
		
		datesFieldSet.add(startDateField, formData);
		datesFieldSet.add(endDateField, formData);
		datesFieldSet.add(startEndSliderField, formData);
		datesFieldSet.add(terminateDateField, formData);
		datesFieldSet.add(endTerminateSliderField, formData);
		datesFieldSet.add(termTypeField, formData);
		
		formColumn2.add(datesFieldSet);

//		FieldSet profileFieldSet = new FieldSet();
//		FormLayout profileLayout = new FormLayout();
//		profileLayout.setLabelAlign(panel.getLabelAlign());
//		profileLayout.setLabelWidth(panel.getLabelWidth() - 10);
//		profileFieldSet.setLayout(profileLayout);
//		profileFieldSet.setBorders(true);
//		profileFieldSet.setHeading("Profile");
//		profileFieldSet.setWidth(buildingsField.getWidth() + 50);
//		
//		profileFieldSet.add(buildingsField);
//		profileFieldSet.add(populationField);
//		profileFieldSet.add(enrollmentField);
//		profileFieldSet.add(workstationsField);
//		formColumn2.add(profileFieldSet, formData);

		panel.add(formColumn1, tData1);
		panel.add(formColumn2, tData1);
	}
	
	protected NotesIconButtonField<String> getNotesButtonField() {
		NotesIconButtonField<String> nibf = new NotesIconButtonField<String>(this) {
			@Override
			public void updateNote(String note) {
				asyncUpdateNote(note);
			}
		};
		nibf.setLabelSeparator("");
		nibf.setEmptyNoteText("Click the note icon to add notes for this agreement term.");
		return nibf;
	}

//	protected boolean isDirtyForm() {
//		return focusInstance == null || formColumn1.isDirty() || formColumn2.isDirty();
//	}
	
	@Override
	protected boolean isFormValidAndReady() {
		boolean ready = formPanel.isValid();
		
		//	Check for required fields
		if (productField.getSelectedValue() == null) { 
			productField.markInvalid("Select a product.");
			ready = false;
		} else
			productField.clearInvalid();
		if (termTypeField.getSelectedValue() == null) {
			termTypeField.markInvalid("Select a term type.");
			ready = false;
		} else
			termTypeField.clearInvalid();
//		if (commissionTypeField.getSelectedValue() == null) {
//			commissionTypeField.markInvalid("Select a commission code.");
//			ready = false;
//		} else
//			commissionTypeField.clearInvalid();
		
		return ready;
	}

	@Override
	protected void asyncUpdate() {
	
		// Set field values from form fields
		
		if (focusInstance == null) {
			focusInstance = new AgreementTermInstance();
			focusInstance.setNewRecord(true);
			focusInstance.setAgreementId(getAgreementId());
		}
		
		focusInstance.setTermType( (TermTypeInstance) termTypeField.getSelectedValue().getBean()  );
		if (commissionTypeField.getSelectedValue() == null)
			focusInstance.setCommissionType(null);
		else
			focusInstance.setCommissionType( (CommissionTypeInstance) commissionTypeField.getSelectedValue().getBean() );
		
		focusInstance.setProduct( ((ProductInstance) productField.getSelectedValue().getBean()) );
		
		if (cancelReasonField.getSelectedValue() == null) {
			focusInstance.setCancelReason(CancelReasonInstance.getEmptyInstance());
			focusInstance.setCancelReasonCode("");
		} else
			focusInstance.setCancelReason( (CancelReasonInstance) cancelReasonField.getSelectedValue().getBean() );
		
		if (dollarValueField.getValue() == null)
			focusInstance.setDollarValue(0);
		else
			focusInstance.setDollarValue(dollarValueField.getValue().doubleValue());
		
		focusInstance.setStartDate(startDateField.getValue());
		focusInstance.setEndDate(endDateField.getValue());
		focusInstance.setTerminateDate(terminateDateField.getValue());
		
		if (referenceSaIdField.getValue() == null)
			focusInstance.setReferenceSaId(0);
		else
			focusInstance.setReferenceSaId(referenceSaIdField.getValue().intValue());
		focusInstance.setPoNumber(poNumberField.getValue());
		focusInstance.setOrgPath(groupField.getValue());
		
//		focusInstance.setBuildings(buildingsField.getValue().intValue());
//		focusInstance.setPopulation(populationField.getValue().intValue());
//		focusInstance.setEnrollment(enrollmentField.getValue().intValue());
//		focusInstance.setWorkstations(workstationsField.getValue().intValue());
		
		if (focusInstance.isNewRecord())
			focusInstance.setNote(notesField.getNote());
		else
			focusInstance.setNote(null);	//	This will keep the note from being updated by this call
	
		//	Issue the asynchronous update request and plan on handling the response
		updateAgreementTermService.updateAgreementTerm(focusInstance,
				new AsyncCallback<UpdateResponse<AgreementTermInstance>>() {
					public void onFailure(Throwable caught) {
						// Show the RPC error message to the user
						if (caught instanceof IllegalArgumentException)
							MessageBox.alert("Alert", caught.getMessage(), null);
						else {
							MessageBox.alert("Alert", "Agreement term  update failed unexpectedly.", null);
							System.out.println(caught.getClass().getName());
							System.out.println(caught.getMessage());
						}
						editButton.enable();
						newButton.enable();
					}

					public void onSuccess(UpdateResponse<AgreementTermInstance> updateResponse) {
						AgreementTermInstance updatedAgreementTerm = (AgreementTermInstance) updateResponse.getInstance();
						if (updatedAgreementTerm.isNewRecord()) {
							updatedAgreementTerm.setNewRecord(false);
							grid.getStore().insert(AgreementTermInstance.obtainModel(updatedAgreementTerm), 0);
							agreementGridStore.insert(AgreementTermInstance.obtainModel(updatedAgreementTerm), 0);
						}
						
						focusInstance.setNewRecord(false);
						focusInstance.setValuesFrom(updatedAgreementTerm);
						setFormFromInstance(updatedAgreementTerm);	//	setFormFieldValues(updatedAgreementTerm);
						
						//	This puts the grid in synch
						BeanModel gridModel = grid.getStore().findModel(focusInstance.getUniqueKey());
						if (gridModel != null) {
							AgreementTermInstance matchInstance = gridModel.getBean();
							matchInstance.setValuesFrom(focusInstance);
							grid.getStore().update(gridModel);
						}
						//	Do the same for the terms grid on the main agreement panel
						if (agreementGridStore != null) {
							BeanModel mainGridModel = agreementGridStore.findModel(focusInstance.getUniqueKey());
							if (mainGridModel != null) {
								AgreementTermInstance mainMatchInstance = mainGridModel.getBean();
								mainMatchInstance.setValuesFrom(focusInstance);
								agreementGridStore.update(mainGridModel);
							}
						}
						
						editButton.enable();
						newButton.enable();
				}
			});
	}

	protected void asyncUpdateNote(String note) {
	
		// Set field values from form fields
		
		if (focusInstance == null || focusInstance.isNewRecord()) {
			return;
		}
		
		focusInstance.setNote(note);
	
		//	Issue the asynchronous update request and plan on handling the response
		updateAgreementTermNoteService.updateAgreementTermNote(focusInstance,
				new AsyncCallback<UpdateResponse<AgreementTermInstance>>() {
					public void onFailure(Throwable caught) {
						// Show the RPC error message to the user
						if (caught instanceof IllegalArgumentException)
							MessageBox.alert("Alert", caught.getMessage(), null);
						else {
							MessageBox.alert("Alert", "Agreement term note update failed unexpectedly.", null);
							System.out.println(caught.getClass().getName());
							System.out.println(caught.getMessage());
						}
						notesField.unlockNote();
					}

					public void onSuccess(UpdateResponse<AgreementTermInstance> updateResponse) {
						AgreementTermInstance updatedAgreementTerm = (AgreementTermInstance) updateResponse.getInstance();
						//	This makes sure the field and instance are in synch
						if (!notesField.getNote().equals(updatedAgreementTerm.getNote())) {
							focusInstance.setNote(updatedAgreementTerm.getNote());
							setNotesField(updatedAgreementTerm.getNote());
						}
						//	This puts the grid in synch
						BeanModel gridModel = grid.getStore().findModel(focusInstance.getUniqueKey());
						if (gridModel != null) {
							AgreementTermInstance matchInstance = gridModel.getBean();
							matchInstance.setNote(focusInstance.getNote());
							grid.getStore().update(gridModel);
						}
						//	Do the same for the terms grid on the main agreement panel
						if (agreementGridStore != null) {
							BeanModel mainGridModel = agreementGridStore.findModel(focusInstance.getUniqueKey());
							if (mainGridModel != null) {
								AgreementTermInstance mainMatchInstance = mainGridModel.getBean();
								mainMatchInstance.setNote(focusInstance.getNote());
								agreementGridStore.update(mainGridModel);
							}
						}
						notesField.unlockNote();
				}
			});
	}

	public ListStore<BeanModel> getAgreementGridStore() {
		return agreementGridStore;
	}

	public void setAgreementGridStore(ListStore<BeanModel> agreementGridStore) {
		this.agreementGridStore = agreementGridStore;
	}
	
	
}
