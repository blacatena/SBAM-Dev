package com.scholastic.sbam.client.uiobjects.uiapp;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.scholastic.sbam.client.services.UpdateSiteLocationService;
import com.scholastic.sbam.client.services.UpdateSiteLocationServiceAsync;
import com.scholastic.sbam.client.uiobjects.fields.EnhancedComboBox;
import com.scholastic.sbam.client.uiobjects.fields.SiteLocCodeField;
import com.scholastic.sbam.client.uiobjects.foundation.FieldFactory;
import com.scholastic.sbam.client.uiobjects.foundation.PortletMaskDialog;
import com.scholastic.sbam.client.util.UiConstants;
import com.scholastic.sbam.shared.objects.SiteInstance;
import com.scholastic.sbam.shared.objects.CommissionTypeInstance;
import com.scholastic.sbam.shared.objects.UpdateResponse;
import com.scholastic.sbam.shared.util.AppConstants;

public class CreateSiteDialog extends PortletMaskDialog {
	
	protected final UpdateSiteLocationServiceAsync		updateSiteLocationService		= GWT.create(UpdateSiteLocationService.class);
	
	/**
	 * Implement this interface to respond to the dialog's request to create a site -- and apply it to the target
	 * @author Bob Lacatena
	 *
	 */
	public interface CreateSiteDialogSaver {
		public void onCreateSiteSave(SiteInstance instance, boolean openAfterSave);
		
		public void lockTrigger();
		
		public void unlockTrigger();
	}
	
	protected CreateSiteDialogSaver			saver;
	
	protected int							ucn;
	protected int							ucnSuffix;
	protected String						institutionName;
	
	protected LabelField					institutionNameField;
	protected SiteLocCodeField				codeField;
	protected TextField<String>				descriptionField;
	protected EnhancedComboBox<BeanModel>	commissionTypeField;
	
	public CreateSiteDialog(LayoutContainer container, CreateSiteDialogSaver saver, int ucn, int ucnSuffix, String institutionName) {
		super(container, true);
		this.ucn = ucn;
		this.ucnSuffix = ucnSuffix;
		this.institutionName = institutionName;
		this.saver = saver;
	}
	
	@Override
	public void init() {
		super.init();
		setHeading("Create Site Location");
	}

	@Override
	public void addFields(FormPanel formPanel) {
		
		if (institutionNameField == null)
			institutionNameField = FieldFactory.getLabelField("Institution", 0, institutionName, "The institution for which a new site location will be created.");
		
		if (codeField == null) {
			codeField = new SiteLocCodeField(ucn, ucnSuffix);
			FieldFactory.setStandard(codeField, "Code");
			codeField.setWidth(30);
			codeField.setToolTip("A unique code for this site location for this UCN.");
		}
		
		if (descriptionField == null) 
			descriptionField = FieldFactory.getStringTextField("Description", "A clear description of the site location.");
		
		if (commissionTypeField == null)	
			commissionTypeField = FieldFactory.getComboField("commissionType", 	"Commission",	0,		
				"The commission code assigned to this site for reporting purposes.",	
				UiConstants.getCommissionTypes(UiConstants.CommissionTypeTargets.SITE), "commissionCode", "descriptionAndCode");	
		
		codeField.setAllowBlank(false);
		codeField.setMinLength(4);
		codeField.setMaxLength(32);
		
		descriptionField.setAllowBlank(false);
		descriptionField.setMinLength(4);
		descriptionField.setMaxLength(255);
		
		FormData fd = new FormData("-24");
		
		formPanel.add(institutionNameField,	fd);
		formPanel.add(codeField);
		formPanel.add(descriptionField,		fd);
		formPanel.add(commissionTypeField,	fd);
		
		formPanel.enable();
	}

	@Override
	public void destroyFields() {
		if (institutionNameField != null) {
			institutionNameField.removeFromParent();
			institutionNameField = null;
		}
		
		if (codeField != null) {
			codeField.removeFromParent();
			codeField = null;
		}

		if (descriptionField != null) {
			descriptionField.removeFromParent();
			descriptionField = null;
		}
		
		if (commissionTypeField != null) {
			commissionTypeField.removeFromParent();
			commissionTypeField = null;
		}
	}

	@Override
	protected void onSave(final boolean openAfterSave) {
	
		// Set field values from form fields
		SiteInstance siteInstance = new SiteInstance();
		siteInstance.setNewRecord(true);
		siteInstance.setUcn(ucn);
		siteInstance.setUcnSuffix(ucnSuffix);
		siteInstance.setSiteLocCode(codeField.getValue());
		siteInstance.setDescription(descriptionField.getValue());
		if (commissionTypeField.getValue() != null)
			siteInstance.setCommissionType((CommissionTypeInstance) commissionTypeField.getValue().getBean());
		siteInstance.setStatus(AppConstants.STATUS_ACTIVE);
	
		//	Issue the asynchronous update request and plan on handling the response
		updateSiteLocationService.updateSiteLocation(siteInstance,
				new AsyncCallback<UpdateResponse<SiteInstance>>() {
					public void onFailure(Throwable caught) {
						// Show the RPC error message to the user
						if (caught instanceof IllegalArgumentException)
							MessageBox.alert("Alert", caught.getMessage(), null);
						else {
							MessageBox.alert("Alert", "Site location update failed unexpectedly.", null);
							System.out.println(caught.getClass().getName());
							System.out.println(caught.getMessage());
						}
					}

					public void onSuccess(UpdateResponse<SiteInstance> updateResponse) {
						if (saver != null)
							saver.onCreateSiteSave(updateResponse.getInstance(), openAfterSave);
				}
			});
	}

	@Override
	public void lockTrigger() {
		saver.lockTrigger();
	}

	@Override
	public void unlockTrigger() {
		saver.unlockTrigger();
	}

	public int getUcn() {
		return ucn;
	}

	public void setUcn(int ucn) {
		this.ucn = ucn;
	}

	public String getInstitutionName() {
		return institutionName;
	}

	public void setInstitutionName(String institutionName) {
		this.institutionName = institutionName;
	}

}
