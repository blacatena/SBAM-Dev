package com.scholastic.sbam.client.uiobjects;

import java.util.List;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.RowExpander;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.scholastic.sbam.client.services.AgreementContactListService;
import com.scholastic.sbam.client.services.AgreementContactListServiceAsync;
import com.scholastic.sbam.client.util.UiConstants;
import com.scholastic.sbam.shared.objects.AgreementContactInstance;
import com.scholastic.sbam.shared.objects.ContactInstance;
import com.scholastic.sbam.shared.util.AppConstants;

public class AgreementContactsCard extends FormAndGridPanel<AgreementContactInstance> {
	
	protected final AgreementContactListServiceAsync agreementContactListService = GWT.create(AgreementContactListService.class);
	
	protected RowExpander			noteExpander;
	
	protected NumberField			agreementIdDisplay	= getIntegerField("Agreement #");
	protected NumberField			contactIdDisplay	= getIntegerField("Contact ID");
	protected TextField<String>		contactTypeDisplay	= getTextField("Contact Type");
	protected TextField<String>		fullNameDisplay		= getTextField("Name");
	protected TextField<String>		titleDisplay		= getTextField("Title");
	protected TextField<String>		phoneDisplay		= getTextField("Phone");
	protected TextField<String>		emailDisplay		= getTextField("E-Mail");
	
	public int getAgreementId() {
		return getFocusId();
	}
	
	public void setAgreementId(int agreementId) {
		setFocusId(agreementId);
	}
	
	public void setAgreementContact(AgreementContactInstance instance) {
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
	 * Override to set any further grid atrributes, such as the autoExpandColumn.
	 * @param grid
	 */
	@Override
	public void setGridAttributes(Grid<ModelData> grid) {
		grid.setAutoExpandColumn("contact.fullName");  	
	}

	@Override
	public void addGridColumns(List<ColumnConfig> columns) {
		columns.add(getDisplayColumn("contact.fullName",				"Name",						120,
					"This is the name of the contact."));
		columns.add(getDisplayColumn("contact.contactTypeDescription",	"Type",						130,
					"This is the type for the contact."));
		columns.add(getDisplayColumn("contact.title",					"Title",					130,
					"This is the contact's official title."));
		columns.add(getDisplayColumn("contact.phone",					"Phone",					90,
					"This is the contact's primary phone number."));
		columns.add(getHiddenColumn("contact.phone2",					"Phone",					90,		true,
					"This is the contact's secondary phone number."));
		columns.add(getDisplayColumn("contact.eMail",					"E-Mail",					100,
					"This is the contact's primary e-mail."));
		columns.add(getHiddenColumn("contact.eMail2",					"E-Mail",					100,	true,
					"This is the contact's secondary e-mail."));
	
		noteExpander = getNoteExpander();
		columns.add(noteExpander);
	}

	@Override
	public void setFormFieldValues(AgreementContactInstance instance) {
		ContactInstance contact = instance.getContact();
		
		agreementIdDisplay.setValue(AppConstants.appendCheckDigit(instance.getAgreementId()));
		
		contactIdDisplay.setValue(instance.getContactId());
		
		if (contact != null) {
			contactTypeDisplay.setValue(contact.getContactTypeDescription());
			fullNameDisplay.setValue(contact.getFullName());
			titleDisplay.setValue(contact.getTitle());
			phoneDisplay.setValue(contact.getPhone());
			emailDisplay.setValue(contact.geteMail());
		} else {
			contactTypeDisplay.clear();
			fullNameDisplay.clear();
			titleDisplay.clear();
			phoneDisplay.clear();
			emailDisplay.clear();
		}
	}

	@Override
	protected void executeLoader(int id,
			AsyncCallback<List<AgreementContactInstance>> callback) {
		agreementContactListService.getAgreementContacts(id, AppConstants.STATUS_DELETED,callback);
	}

	@Override
	protected void addFormFields(FormPanel panel, FormData formData) {
		
		contactIdDisplay.setToolTip(UiConstants.getQuickTip("The unique ID for this contact."));
		contactTypeDisplay.setToolTip(UiConstants.getQuickTip("The type of contact."));
		fullNameDisplay.setToolTip(UiConstants.getQuickTip("The full name for the contact."));
		titleDisplay.setToolTip(UiConstants.getQuickTip("The title of the contact at his or her position."));
		
		contactTypeDisplay.setWidth(300);
		fullNameDisplay.setWidth(300);
		titleDisplay.setWidth(300);
		
		panel.add(agreementIdDisplay, formData);
		panel.add(contactIdDisplay, formData);	
		panel.add(contactTypeDisplay, formData);
		panel.add(fullNameDisplay, formData);
		panel.add(phoneDisplay, formData);
		panel.add(emailDisplay, formData);
		
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
