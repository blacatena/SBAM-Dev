package com.scholastic.sbam.client.uiobjects.uiapp;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.scholastic.sbam.client.services.UpdateAgreementSiteService;
import com.scholastic.sbam.client.services.UpdateAgreementSiteServiceAsync;
import com.scholastic.sbam.client.uiobjects.fields.EnhancedComboBox;
import com.scholastic.sbam.client.uiobjects.fields.InstitutionSearchField;
import com.scholastic.sbam.client.uiobjects.fields.SiteLocationSearchField;
import com.scholastic.sbam.client.uiobjects.foundation.FieldFactory;
import com.scholastic.sbam.client.uiobjects.foundation.PortletMaskDialog;
import com.scholastic.sbam.client.util.UiConstants;
import com.scholastic.sbam.shared.objects.AgreementSiteInstance;
import com.scholastic.sbam.shared.objects.CommissionTypeInstance;
import com.scholastic.sbam.shared.objects.InstitutionInstance;
import com.scholastic.sbam.shared.objects.SiteInstance;
import com.scholastic.sbam.shared.objects.UpdateResponse;
import com.scholastic.sbam.shared.util.AppConstants;

/**
 *  INCOMPLETE -- TEST AND FINISH BEFORE USING
 * 
 * This class was created to use in conjunction with the AgreementSiteSearchField for finding the sites (ucn+suffix+location) for an agreement.
 * 
 * It was abandoned in favor of a two field approach (institution, location).
 * 
 * To be used this must be expanded and properly tested.
 * 
 * @author Bob Lacatena
 *
 */
public class CreateAgreementSiteDialog extends PortletMaskDialog {
	
	protected final UpdateAgreementSiteServiceAsync		updateAgreementSiteService		= GWT.create(UpdateAgreementSiteService.class);
	
	/**
	 * Implement this interface to respond to the dialog's request to create a site -- and apply it to the target
	 * @author Bob Lacatena
	 *
	 */
	public interface CreateAgreementSiteDialogSaver {
		public void onCreateAgreementSiteSave(AgreementSiteInstance instance);
		
		public void lockTrigger();
		
		public void unlockTrigger();
	}
	
	protected CreateAgreementSiteDialogSaver			saver;
	
	protected int							agreementId;
	
	protected LabelField					agreementIdField;
	protected InstitutionSearchField		institutionField;
	protected SiteLocationSearchField		siteLocationField;
	protected EnhancedComboBox<BeanModel>	commissionTypeField;
	
	public CreateAgreementSiteDialog(LayoutContainer container, CreateAgreementSiteDialogSaver saver, int agreementId) {
		super(container);
		this.agreementId = agreementId;
		this.saver = saver;
	}
	
	@Override
	public void init() {
		super.init();
		setHeading("Create Site Location");
	}

	@Override
	public void addFields(FormPanel formPanel) {
		
		if (agreementIdField == null)
			agreementIdField = FieldFactory.getLabelField("Agreement", 0, agreementId + "", "The agreement for which a new site location will be created.");
		
		if (institutionField == null) {
			institutionField = new InstitutionSearchField();
			FieldFactory.setStandard(institutionField, "Institution");
			institutionField.setToolTip("The institution for this site.");
		}
		
		if (siteLocationField == null) {
			siteLocationField = getSiteLocationField("siteLocCode", "Location", 0, "The location for this site.");
		}
		
		if (commissionTypeField == null)	
			commissionTypeField = FieldFactory.getComboField("commissionType", 	"Commission",	0,		
				"The commission code assigned to this site for reporting purposes.",	
				UiConstants.getCommissionTypes(UiConstants.CommissionTypeTargets.SITE), "commissionCode", "descriptionAndCode");	
		
		FormData fd = new FormData("-24");
		
		formPanel.add(agreementIdField,	fd);
		formPanel.add(institutionField,	fd);
		formPanel.add(siteLocationField, fd);
		formPanel.add(commissionTypeField,	fd);
		
		formPanel.enable();
	}
	
	protected SiteLocationSearchField getSiteLocationField(String name, String label, int width, String toolTip) {
		final SiteLocationSearchField siteLocCombo = new SiteLocationSearchField(this);
		siteLocCombo.setIncludeAllOption(false);
		siteLocCombo.setIncludeMainOption(true);
		siteLocCombo.setIncludeAddOption(false);
		FieldFactory.setStandard(siteLocCombo, label);
		
		if (toolTip != null)
			siteLocCombo.setToolTip(toolTip);
		if (width >= 0)
			siteLocCombo.setWidth(width);
		siteLocCombo.setDisplayField("descriptionAndCode");
		
		return siteLocCombo;
	}

	
	protected InstitutionSearchField getInstitutionField(String name, String label, int width, String toolTip) {
        InstitutionSearchField instCombo = new InstitutionSearchField();
		FieldFactory.setStandard(instCombo, label);
		instCombo.setAllowBlank(true);
		
		if (toolTip != null)
			instCombo.setToolTip(toolTip);
		if (width > 0)
			instCombo.setWidth(width);
		instCombo.setDisplayField("institutionName");
		
		instCombo.addSelectionChangedListener(new SelectionChangedListener<BeanModel>() {

			@Override
			public void selectionChanged(SelectionChangedEvent<BeanModel> se) {
				selectInstitution(se.getSelectedItem());
			}
			
		});
		
		return instCombo;
	}
	
	protected void selectInstitution(BeanModel model) {
		if (model == null) {	// No value selected means leave it as is
			if (institutionField.getSelectedValue() != null)
				matchToInstitution( (InstitutionInstance) institutionField.getSelectedValue().getBean() );
			else
				if (institutionField.getOriginalValue() != null)
					matchToInstitution( (InstitutionInstance) institutionField.getOriginalValue().getBean());
		} else {
			InstitutionInstance instance = (InstitutionInstance) model.getBean();
			matchToInstitution( instance );
		}
	}
	
	protected void matchToInstitution(InstitutionInstance instance) {
		
		if (instance == null || instance.getUcn() == 0) {
			siteLocationField.setFor(0, 0, "");
			return;
		}
		
		siteLocationField.setFor(instance.getUcn(), 1, instance.getInstitutionName());
		siteLocationField.setValue(SiteInstance.obtainModel(SiteInstance.getAllInstance(instance.getUcn(), 1)));		
	}

	@Override
	public void destroyFields() {
		if (agreementIdField != null) {
			agreementIdField.removeFromParent();
			agreementIdField = null;
		}
		
		if (institutionField != null) {
			institutionField.removeFromParent();
			institutionField = null;
		}

		if (siteLocationField != null) {
			siteLocationField.removeFromParent();
			siteLocationField = null;
		}
		
		if (commissionTypeField != null) {
			commissionTypeField.removeFromParent();
			commissionTypeField = null;
		}
	}

	@Override
	protected void onSave() {
	
		// Set field values from form fields
		AgreementSiteInstance agreementSiteInstance = new AgreementSiteInstance();
		agreementSiteInstance.setNewRecord(true);
		
		InstitutionInstance institution = institutionField.getValue().getBean();
		
		if (institution == null) {
			MessageBox.alert("Internal Error", "No institution is selected.", null);
			return;
		}
		
		agreementSiteInstance.setSiteUcn(institution.getUcn());
		agreementSiteInstance.setSiteUcnSuffix(1);
		
		SiteInstance siteInstance = siteLocationField.getValue().getBean();
		
		if (siteInstance == null) {
			MessageBox.alert("Internal Error", "No site location is selected.", null);
			return;
		}
		
		agreementSiteInstance.setSiteLocCode(siteInstance.getSiteLocCode());
		agreementSiteInstance.setCommissionType((CommissionTypeInstance) commissionTypeField.getValue().getBean());
		agreementSiteInstance.setStatus(AppConstants.STATUS_ACTIVE);
	
		//	Issue the asynchronous update request and plan on handling the response
		updateAgreementSiteService.updateAgreementSite(agreementSiteInstance,
				new AsyncCallback<UpdateResponse<AgreementSiteInstance>>() {
					public void onFailure(Throwable caught) {
						// Show the RPC error message to the user
						if (caught instanceof IllegalArgumentException)
							MessageBox.alert("Alert", caught.getMessage(), null);
						else {
							MessageBox.alert("Alert", "Agrement site location update failed unexpectedly.", null);
							System.out.println(caught.getClass().getName());
							System.out.println(caught.getMessage());
						}
					}

					public void onSuccess(UpdateResponse<AgreementSiteInstance> updateResponse) {
						if (saver != null)
							saver.onCreateAgreementSiteSave(updateResponse.getInstance());
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

	public int getAgreementId() {
		return agreementId;
	}

	public void setAgreementId(int agreementId) {
		this.agreementId = agreementId;
	}

}
