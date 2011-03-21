package com.scholastic.sbam.client.uiobjects;

import java.util.List;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.RowExpander;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.scholastic.sbam.client.services.AgreementTermListService;
import com.scholastic.sbam.client.services.AgreementTermListServiceAsync;
import com.scholastic.sbam.client.util.UiConstants;
import com.scholastic.sbam.shared.objects.AgreementTermInstance;
import com.scholastic.sbam.shared.objects.CancelReasonInstance;
import com.scholastic.sbam.shared.objects.CommissionTypeInstance;
import com.scholastic.sbam.shared.objects.ProductInstance;
import com.scholastic.sbam.shared.objects.TermTypeInstance;
import com.scholastic.sbam.shared.util.AppConstants;

public class AgreementTermsCard extends FormAndGridPanel<AgreementTermInstance> {
	
	protected final AgreementTermListServiceAsync agreementTermListService = GWT.create(AgreementTermListService.class);
	
	protected RowExpander					noteExpander;
	
	protected NumberField					agreementIdField	= getIntegerField("Agreement #");
	protected EnhancedComboBox<BeanModel>	productField		= getComboField("product", 	"Product",	150,		
																	"The product to deliver for this term.",	
																	UiConstants.getProducts(), "productCode", "descriptionAndCode");
	protected NumberField					dollarValueField	= getDollarField("Value");
	protected DateField						startDateField		= getDateField("Start");
	protected DateField						endDateField		= getDateField("End");
	protected DateField						terminateDateField	= getDateField("Terminate");
	protected EnhancedComboBox<BeanModel>	termTypeField		= getComboField("termType", 		"Term Type",	150,		
																		"The term type for this product term.",	
																		UiConstants.getTermTypes(), "termTypeCode", "description");
	protected EnhancedComboBox<BeanModel>	commissionTypeField	= getComboField("commissionType", 	"Commission Code",	150,		
																		"The commission code assigned to this product term for reporting purposes.",	
																		UiConstants.getCommissionTypes(UiConstants.CommissionTypeTargets.AGREEMENT_TERM), "commissionCode", "descriptionAndCode");
	protected EnhancedComboBox<BeanModel>	cancelReasonField	= getComboField("cancelReason", 	"Cancel Reason",	150,		
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
	public void addGridPlugins(Grid<ModelData> grid) {
		grid.addPlugin(noteExpander);
	}
	
	/**
	 * Override to set any further grid attributes, such as the autoExpandColumn.
	 * @param grid
	 */
	@Override
	public void setGridAttributes(Grid<ModelData> grid) {
		grid.setAutoExpandColumn("product.description");  	
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
	}

	@Override
	protected void executeLoader(int id,
			AsyncCallback<List<AgreementTermInstance>> callback) {
		agreementTermListService.getAgreementTerms(id, AppConstants.STATUS_DELETED,callback);
	}

	@Override
	protected void addFormFields(FormPanel panel, FormData formData) {
		agreementIdField.setReadOnly(true);
		
		dollarValueField.setToolTip(UiConstants.getQuickTip("The total dollar value of this product for this term."));
		startDateField.setToolTip(UiConstants.getQuickTip("The date on which this product term will take effect."));
		endDateField.setToolTip(UiConstants.getQuickTip("The date on which this product term is scheduled to end."));
		terminateDateField.setToolTip(UiConstants.getQuickTip("The date on which this product term will no longer be delivered."));

		productField.setAllowBlank(false);
		termTypeField.setAllowBlank(false);
		cancelReasonField.setAllowBlank(true);
		commissionTypeField.setAllowBlank(true);
		
		panel.add(agreementIdField, formData);
		panel.add(productField, formData);
		panel.add(dollarValueField, formData);
		
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
		
		panel.add(fieldSet);
		
		panel.add(cancelReasonField, formData);
		panel.add(commissionTypeField, formData);
	}
	
	
}
