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
	
	protected NumberField					agreementIdDisplay	= getIntegerField("Agreement #");
	protected EnhancedComboBox<BeanModel>	productDisplay		= getComboField("product", 	"Product",	150,		
																	"The product to deliver for this term.",	
																	UiConstants.getProducts(), "productCode", "descriptionAndCode");
	protected NumberField					dollarValue			= getDollarField("Value");
	protected DateField						startDate			= getDateField("Start");
	protected DateField						endDate				= getDateField("End");
	protected DateField						terminateDate		= getDateField("Terminate");
	protected EnhancedComboBox<BeanModel>	termType			= getComboField("termType", 		"Term Type",	150,		
																		"The term type for this product term.",	
																		UiConstants.getTermTypes(), "termTypeCode", "description");
	protected EnhancedComboBox<BeanModel>	commissionType		= getComboField("commissionType", 	"Commission Code",	150,		
																		"The commission code assigned to this product term for reporting purposes.",	
																		UiConstants.getCommissionTypes(UiConstants.CommissionTypeTargets.AGREEMENT_TERM), "commissionCode", "descriptionAndCode");
	protected EnhancedComboBox<BeanModel>	cancelReason		= getComboField("cancelReason", 	"Cancel Reason",	150,		
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
		agreementIdDisplay.setValue(AppConstants.appendCheckDigit(instance.getAgreementId()));
		productDisplay.setValue(ProductInstance.obtainModel(instance.getProduct()));
		dollarValue.setValue(instance.getDollarValue());
		startDate.setValue(instance.getStartDate());
		endDate.setValue(instance.getEndDate());
		terminateDate.setValue(instance.getTerminateDate());
		termType.setValue(TermTypeInstance.obtainModel(instance.getTermType()));
		cancelReason.setValue(CancelReasonInstance.obtainModel(instance.getCancelReason()));
		commissionType.setValue(CommissionTypeInstance.obtainModel(instance.getCommissionType()));
	}

	@Override
	protected void executeLoader(int id,
			AsyncCallback<List<AgreementTermInstance>> callback) {
		agreementTermListService.getAgreementTerms(id, AppConstants.STATUS_DELETED,callback);
	}

	@Override
	protected void addFormFields(FormPanel panel, FormData formData) {
		agreementIdDisplay.setReadOnly(true);
		
		dollarValue.setToolTip(UiConstants.getQuickTip("The total dollar value of this product for this term."));
		startDate.setToolTip(UiConstants.getQuickTip("The date on which this product term will take effect."));
		endDate.setToolTip(UiConstants.getQuickTip("The date on which this product term is scheduled to end."));
		terminateDate.setToolTip(UiConstants.getQuickTip("The date on which this product term will no longer be delivered."));

		productDisplay.setAllowBlank(false);
		termType.setAllowBlank(false);
		cancelReason.setAllowBlank(true);
		commissionType.setAllowBlank(true);
		
		panel.add(agreementIdDisplay, formData);
		panel.add(productDisplay, formData);
		panel.add(dollarValue, formData);
		
		FieldSet fieldSet = new FieldSet();
		FormLayout layout = new FormLayout();
		layout.setLabelAlign(panel.getLabelAlign());
		layout.setLabelWidth(panel.getLabelWidth() - 10);
		fieldSet.setLayout(layout);
		fieldSet.setBorders(true);
		fieldSet.setHeading("Term Dates");
		
		fieldSet.add(startDate);
		fieldSet.add(endDate);
		fieldSet.add(terminateDate);
		fieldSet.add(termType);
		
		panel.add(fieldSet);
		
		panel.add(cancelReason, formData);
		panel.add(commissionType, formData);
	}
	
	
}
