package com.scholastic.sbam.client.uiobjects.uiapp;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.HtmlEditor;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.scholastic.sbam.client.services.UpdateAgreementLinkService;
import com.scholastic.sbam.client.services.UpdateAgreementLinkServiceAsync;
import com.scholastic.sbam.client.uiobjects.fields.EnhancedComboBox;
import com.scholastic.sbam.client.uiobjects.fields.InstitutionSearchField;
import com.scholastic.sbam.client.uiobjects.foundation.FieldFactory;
import com.scholastic.sbam.client.uiobjects.foundation.PortletMaskDialog;
import com.scholastic.sbam.client.util.UiConstants;
import com.scholastic.sbam.shared.objects.AgreementLinkInstance;
import com.scholastic.sbam.shared.objects.InstitutionInstance;
import com.scholastic.sbam.shared.objects.LinkTypeInstance;
import com.scholastic.sbam.shared.objects.UpdateResponse;
import com.scholastic.sbam.shared.util.AppConstants;

public class CreateAgreementLinkDialog extends PortletMaskDialog {
	
	protected final UpdateAgreementLinkServiceAsync		updateAgreementLinkService		= GWT.create(UpdateAgreementLinkService.class);
	
	/**
	 * Implement this interface to respond to the dialog's request to create a site -- and apply it to the target
	 * @author Bob Lacatena
	 *
	 */
	public interface CreateAgreementLinkDialogSaver {
		public void onCreateAgreementLinkSave(AgreementLinkInstance instance);
		
		public void lockTrigger();
		
		public void unlockTrigger();
	}
	
	protected CreateAgreementLinkDialogSaver			saver;
	
	protected InstitutionSearchField		institutionField;
	protected EnhancedComboBox<BeanModel>	linkTypeField;
	protected HtmlEditor					noteField;
	
	public CreateAgreementLinkDialog(LayoutContainer container, CreateAgreementLinkDialogSaver saver) {
		super(container);
		this.saver = saver;
		setMarginWidth(50);
	}
	
	@Override
	public void init() {
		super.init();
		setHeading("Create AgreementLink");
	}

	@Override
	public void addFields(FormPanel formPanel) {
		
		if (institutionField == null) 
			institutionField = getInstitutionField("ucn", "Description", 0, "A clear description of the agreementLink.");
		
		if (linkTypeField == null) {
			linkTypeField 	 	= FieldFactory.getComboField("linkType", 		"Link Type",	0,		
																"The type for this link.",	
																UiConstants.getLinkTypes(), "linkTypeCode", "description");
		}
		
		if (noteField == null) {
			noteField = new HtmlEditor();
			FieldFactory.setStandard(noteField, "Notes");
			noteField.setHeight(200);
		}
		
		institutionField.setAllowBlank(false);
		linkTypeField.setAllowBlank(false);
		
		
		FormData fd = new FormData("-24");
		
		formPanel.add(institutionField,		fd);
		formPanel.add(linkTypeField,		fd);
		formPanel.add(noteField,			fd);
		
		formPanel.enable();
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
		
		return instCombo;
	}

	@Override
	public void destroyFields() {

		if (institutionField != null) {
			institutionField.removeFromParent();
			institutionField = null;
		}

		if (linkTypeField != null) {
			linkTypeField.removeFromParent();
			linkTypeField = null;
		}

		if (noteField != null) {
			noteField.removeFromParent();
			noteField = null;
		}
	}

	@Override
	protected void onSave(boolean openAfterSave) {
	
		// Set field values from form fields
		AgreementLinkInstance agreementLinkInstance = new AgreementLinkInstance();
		agreementLinkInstance.setNewRecord(true);

		if (institutionField.getValue() == null) {
			MessageBox.alert("Error", "An institution must be selected.", null);
			return;
		}
		
		InstitutionInstance institution = institutionField.getValue().getBean();
		agreementLinkInstance.setUcn(institution.getUcn());
		
		if (linkTypeField.getValue() == null) {
			MessageBox.alert("Error", "A link type must be selected.", null);
			return;
		}
		
		LinkTypeInstance linkType = linkTypeField.getValue().getBean();
		agreementLinkInstance.setLinkType(linkType);
		
		agreementLinkInstance.setNote(noteField.getValue());
		agreementLinkInstance.setStatus(AppConstants.STATUS_ACTIVE);
	
		//	Issue the asynchronous update request and plan on handling the response
		updateAgreementLinkService.updateAgreementLink(agreementLinkInstance,
				new AsyncCallback<UpdateResponse<AgreementLinkInstance>>() {
					public void onFailure(Throwable caught) {
						// Show the RPC error message to the user
						if (caught instanceof IllegalArgumentException)
							MessageBox.alert("Alert", caught.getMessage(), null);
						else {
							MessageBox.alert("Alert", "Agreement Link update failed unexpectedly.", null);
							System.out.println(caught.getClass().getName());
							System.out.println(caught.getMessage());
						}
					}

					public void onSuccess(UpdateResponse<AgreementLinkInstance> updateResponse) {
						if (saver != null)
							saver.onCreateAgreementLinkSave(updateResponse.getInstance());
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

}
