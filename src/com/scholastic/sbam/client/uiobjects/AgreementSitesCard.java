package com.scholastic.sbam.client.uiobjects;

import java.util.List;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.RowExpander;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.scholastic.sbam.client.services.AgreementSiteListService;
import com.scholastic.sbam.client.services.AgreementSiteListServiceAsync;
import com.scholastic.sbam.client.util.UiConstants;
import com.scholastic.sbam.shared.objects.AgreementSiteInstance;
import com.scholastic.sbam.shared.util.AppConstants;

public class AgreementSitesCard extends FormAndGridPanel<AgreementSiteInstance> {
	
	protected final AgreementSiteListServiceAsync agreementSiteListService = GWT.create(AgreementSiteListService.class);
	
	protected RowExpander			noteExpander;
	
	protected NumberField			agreementIdDisplay	= getIntegerField("Agreement #");
	protected TextField<String>		ucnDisplay			= getTextField("UCN");
	protected TextField<String>		locationDisplay		= getTextField("Location");
	protected TextField<String>		institutionDisplay	= getTextField("");
	
	public int getAgreementId() {
		return getFocusId();
	}
	
	public void setAgreementId(int agreementId) {
		setFocusId(agreementId);
	}
	
	public void setAgreementSite(AgreementSiteInstance instance) {
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
	 * Override to set any further grid atrributes, such as the autoExpandColumn.
	 * @param grid
	 */
	@Override
	public void setGridAttributes(Grid<BeanModel> grid) {
		grid.setAutoExpandColumn("institution.institutionName");  	
	}

	@Override
	public void addGridColumns(List<ColumnConfig> columns) {
		columns.add(getDisplayColumn("displayUcn",				"UCN+",						80,		false,
					"This is the UCN+ for the site."));
		columns.add(getHiddenColumn("siteUcn",					"UCN",						40,		true, UiConstants.INTEGER_FORMAT,
					"This is the UCN for the site."));
		columns.add(getHiddenColumn("siteUcnSuffix",			"Suffix",					40,		true, UiConstants.INTEGER_FORMAT,
					"This is the suffix for this pseudo site."));
		columns.add(getDisplayColumn("institution.institutionName",		"Institution",		150,
					"This is the institution name."));
		columns.add(getDisplayColumn("siteLocCode",				"Code",						40,
					"This is the code for the location at the site."));
		columns.add(getDisplayColumn("site.description",		"Description",				150,
					"This is the description of the location at the site."));
	
		noteExpander = getNoteExpander();
		columns.add(noteExpander);
	}

	@Override
	public void setFormFieldValues(AgreementSiteInstance instance) {
		agreementIdDisplay.setValue(AppConstants.appendCheckDigit(instance.getAgreementId()));
		
		if (instance.getSiteUcnSuffix() <= 1)
			ucnDisplay.setValue(instance.getSiteUcn() + "");
		else
			ucnDisplay.setValue(instance.getSiteUcn() + " - " + instance.getSiteUcnSuffix());
		
		institutionDisplay.setValue(instance.getInstitution().getInstitutionName());
		
		if (instance.getSiteLocCode() == null || instance.getSiteLocCode().length() == 0) {
			locationDisplay.setVisible(false);
			locationDisplay.setValue("Main location");
		} else {
			locationDisplay.setVisible(true);
			if (instance.getSite() == null)
				locationDisplay.setValue(instance.getSiteLocCode() + " - [site not found]");
			else
				locationDisplay.setValue(instance.getSiteLocCode() + " - " + instance.getSite().getDescription());
		}
	}

	@Override
	protected void executeLoader(int id,
			AsyncCallback<List<AgreementSiteInstance>> callback) {
		agreementSiteListService.getAgreementSites(id, AppConstants.STATUS_DELETED,callback);
	}

	@Override
	protected void addFormFields(FormPanel panel, FormData formData) {
		
		ucnDisplay.setToolTip(UiConstants.getQuickTip("The ucn for the site."));
		institutionDisplay.setToolTip(UiConstants.getQuickTip("The name of the institution."));
		locationDisplay.setToolTip(UiConstants.getQuickTip("The code and name for a specific location within the institution."));
		
		institutionDisplay.setLabelSeparator("");
		
		institutionDisplay.setWidth(300);
		locationDisplay.setWidth(300);
		
		panel.add(agreementIdDisplay, formData);
		panel.add(ucnDisplay, formData);	
		panel.add(institutionDisplay, formData);
		panel.add(locationDisplay, formData);
		
//		FieldSet fieldSet = new FieldSet();
//		FormLayout layout = new FormLayout();
//		layout.setLabelAlign(panel.getLabelAlign());
//		layout.setLabelWidth(panel.getLabelWidth() - 10);
//		fieldSet.setLayout(layout);
//		fieldSet.setBorders(true);
//		fieldSet.setHeading("Term Dates");
//		
//		fieldSet.add(startDate);
//		fieldSet.add(endDate);
//		fieldSet.add(terminateDate);
//		
//		panel.add(fieldSet);
	}
	
	
}
