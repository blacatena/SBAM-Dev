package com.scholastic.sbam.client.uiobjects;

import java.util.List;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.MultiField;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.RowExpander;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.scholastic.sbam.client.services.AgreementTermListService;
import com.scholastic.sbam.client.services.AgreementTermListServiceAsync;
import com.scholastic.sbam.client.services.UpdateAgreementTermNoteService;
import com.scholastic.sbam.client.services.UpdateAgreementTermNoteServiceAsync;
import com.scholastic.sbam.client.util.UiConstants;
import com.scholastic.sbam.shared.objects.AgreementTermInstance;
import com.scholastic.sbam.shared.objects.CancelReasonInstance;
import com.scholastic.sbam.shared.objects.CommissionTypeInstance;
import com.scholastic.sbam.shared.objects.ProductInstance;
import com.scholastic.sbam.shared.objects.SimpleKeyProvider;
import com.scholastic.sbam.shared.objects.TermTypeInstance;
import com.scholastic.sbam.shared.objects.UpdateResponse;
import com.scholastic.sbam.shared.util.AppConstants;

public class AgreementTermsCard extends FormAndGridPanel<AgreementTermInstance> {
	
	protected final AgreementTermListServiceAsync		agreementTermListService		= GWT.create(AgreementTermListService.class);
	protected final UpdateAgreementTermNoteServiceAsync	updateAgreementTermNoteService	= GWT.create(UpdateAgreementTermNoteService.class);
	
	protected FormPanel				formColumn1;
	protected FormPanel				formColumn2;
	protected FormPanel				formRow2;
	
	protected ListStore<BeanModel>	agreementGridStore;
	
	protected RowExpander					noteExpander;

	protected MultiField<String>			idNotesCombo		= new MultiField<String>("Agreement #");
	protected NumberField					agreementIdField	= getIntegerField("Agreement #");
	protected NotesIconButtonField<String>	notesField			= getNotesButtonField();
	protected EnhancedComboBox<BeanModel>	productField		= getComboField("product", 	"Product",	240,		
																	"The product to deliver for this term.",	
																	UiConstants.getProducts(), "productCode", "descriptionAndCode");
	protected NumberField					dollarValueField	= getDollarField("Value");
	protected DateField						startDateField		= getDateField("Start");
	protected DateField						endDateField		= getDateField("End");
	protected DateField						terminateDateField	= getDateField("Terminate");
	protected EnhancedComboBox<BeanModel>	termTypeField		= getComboField("termType", 		"Term Type",	0,		
																		"The term type for this product term.",	
																		UiConstants.getTermTypes(), "termTypeCode", "description");
	protected EnhancedComboBox<BeanModel>	commissionTypeField	= getComboField("commissionType", 	"Commission Code",	0,		
																		"The commission code assigned to this product term for reporting purposes.",	
																		UiConstants.getCommissionTypes(UiConstants.CommissionTypeTargets.AGREEMENT_TERM), "commissionCode", "descriptionAndCode");
	protected EnhancedComboBox<BeanModel>	cancelReasonField	= getComboField("cancelReason", 	"Cancel Reason",	0,		
																		"The reason for canceling for this product term.",	
																		UiConstants.getCancelReasons(), "cancelReasonCode", "descriptionAndCode");
	
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
					"This is the value of the service."));
		columns.add(getDisplayColumn("termType.description",	"Type",						80,
					"This is the type of service."));
	
		noteExpander = getNoteExpander();
		columns.add(noteExpander);
	}

	@Override
	public void setFormFieldValues(AgreementTermInstance instance) {
		agreementIdField.setValue(AppConstants.appendCheckDigit(instance.getAgreementId()));
		productField.setValue(ProductInstance.obtainModel(instance.getProduct()));
		dollarValueField.setValue(instance.getDollarValue());
		startDateField.setValue(instance.getStartDate());
		endDateField.setValue(instance.getEndDate());
		terminateDateField.setValue(instance.getTerminateDate());
		termTypeField.setValue(TermTypeInstance.obtainModel(instance.getTermType()));
		cancelReasonField.setValue(CancelReasonInstance.obtainModel(instance.getCancelReason()));
		commissionTypeField.setValue(CommissionTypeInstance.obtainModel(instance.getCommissionType()));

		if (instance.getNote() != null && instance.getNote().length() > 0) {
			notesField.setEditMode();
			notesField.setNote(instance.getNote());
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
		
		panel.setLayout(new TableLayout(2));
		
		formColumn1 = getNewFormPanel(110);
		formColumn2 = getNewFormPanel(5);
		
		agreementIdField.setReadOnly(true);
		agreementIdField.setWidth(150);
		idNotesCombo.setSpacing(20);
		
		dollarValueField.setToolTip(UiConstants.getQuickTip("The total dollar value of this product for this term."));
		startDateField.setToolTip(UiConstants.getQuickTip("The date on which this product term will take effect."));
		endDateField.setToolTip(UiConstants.getQuickTip("The date on which this product term is scheduled to end."));
		terminateDateField.setToolTip(UiConstants.getQuickTip("The date on which this product term will no longer be delivered."));

		productField.setAllowBlank(false);
		termTypeField.setAllowBlank(false);
		cancelReasonField.setAllowBlank(true);
		commissionTypeField.setAllowBlank(true);
		
		idNotesCombo.add(agreementIdField);	
		idNotesCombo.add(notesField);
		formColumn1.add(idNotesCombo,    formData);
		
//		formColumn1.add(agreementIdField, formData);
		formColumn1.add(productField, formData);
		formColumn1.add(dollarValueField, formData);
		
		FieldSet fieldSet = new FieldSet();
		FormLayout layout = new FormLayout();
		layout.setLabelAlign(panel.getLabelAlign());
		layout.setLabelWidth(panel.getLabelWidth() - 10);
		fieldSet.setLayout(layout);
		fieldSet.setBorders(true);
		fieldSet.setHeading("Term Dates");
		
		fieldSet.add(startDateField);
		fieldSet.add(endDateField);
		fieldSet.add(terminateDateField);
		fieldSet.add(termTypeField);
		
//		formColumn2.add(notesField);
		formColumn2.add(fieldSet);
		
		formColumn1.add(commissionTypeField, formData);
		formColumn1.add(cancelReasonField, formData);

		panel.add(formColumn1);
		panel.add(formColumn2);
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
		System.out.println(nibf.getStyleName());
		return nibf;
	}

//	protected boolean isDirtyForm() {
//		return focusInstance == null || formColumn1.isDirty() || formColumn2.isDirty();
//	}
	
	@Override
	protected boolean isFormValidAndReady() {
		boolean ready = true;
		
		//	Check for required fields
		if (productField.getSelectedValue() == null) { 
			productField.markInvalid("Select a product.");
			ready = false;
		} else
			productField.clearInvalid();
		if (termTypeField.getSelectedValue() == null) {
			termTypeField.markInvalid("Select an term type.");
			ready = false;
		} else
			termTypeField.clearInvalid();
		if (commissionTypeField.getSelectedValue() == null) {
			commissionTypeField.markInvalid("Select a commission code.");
			ready = false;
		} else
			commissionTypeField.clearInvalid();
		
		return ready;
	}

//	public void clearFormValues() {
//		formColumn1.clear();
//		formColumn2.clear();
//	}
//
//	@Override
//	public void resetFormValues() {
//		formColumn1.reset();
//		formColumn2.reset();
//	}
//	
//	public void setOriginalValues() {
//		setOriginalValues(formColumn1);
//		setOriginalValues(formColumn2);
//	}

	protected void asyncUpdate() {
		System.out.println("Do update term");
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
							MessageBox.alert("Alert", "Agreement note update failed unexpectedly.", null);
							System.out.println(caught.getClass().getName());
							System.out.println(caught.getMessage());
						}
						notesField.unlockNote();
					}

					public void onSuccess(UpdateResponse<AgreementTermInstance> updateResponse) {
						AgreementTermInstance updatedAgreementTerm = (AgreementTermInstance) updateResponse.getInstance();
						//	This makes sure the field and instance are in synch
						if (!notesField.getNote().equals(updatedAgreementTerm.getNote())) {
							notesField.setNote(updatedAgreementTerm.getNote());
							focusInstance.setNote(updatedAgreementTerm.getNote());
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
