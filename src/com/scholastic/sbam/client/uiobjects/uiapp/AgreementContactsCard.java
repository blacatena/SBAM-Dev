package com.scholastic.sbam.client.uiobjects.uiapp;

import java.util.List;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.CheckBoxGroup;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.MultiField;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.RowExpander;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.scholastic.sbam.client.services.AgreementContactListService;
import com.scholastic.sbam.client.services.AgreementContactListServiceAsync;
import com.scholastic.sbam.client.services.UpdateAgreementContactService;
import com.scholastic.sbam.client.services.UpdateAgreementContactServiceAsync;
import com.scholastic.sbam.client.services.UpdateContactNoteService;
import com.scholastic.sbam.client.services.UpdateContactNoteServiceAsync;
import com.scholastic.sbam.client.uiobjects.fields.ContactSearchField;
import com.scholastic.sbam.client.uiobjects.fields.EnhancedComboBox;
import com.scholastic.sbam.client.uiobjects.fields.InstitutionSearchField;
import com.scholastic.sbam.client.uiobjects.fields.NotesIconButtonField;
import com.scholastic.sbam.client.uiobjects.foundation.FieldFactory;
import com.scholastic.sbam.client.uiobjects.foundation.FormAndGridPanel;
import com.scholastic.sbam.client.uiobjects.foundation.FormInnerPanel;
import com.scholastic.sbam.client.util.UiConstants;
import com.scholastic.sbam.shared.objects.AgreementContactInstance;
import com.scholastic.sbam.shared.objects.AgreementInstance;
import com.scholastic.sbam.shared.objects.ContactInstance;
import com.scholastic.sbam.shared.objects.ContactSearchResultInstance;
import com.scholastic.sbam.shared.objects.ContactTypeInstance;
import com.scholastic.sbam.shared.objects.InstitutionInstance;
import com.scholastic.sbam.shared.objects.SimpleKeyProvider;
import com.scholastic.sbam.shared.objects.UpdateResponse;
import com.scholastic.sbam.shared.util.AppConstants;

public class AgreementContactsCard extends FormAndGridPanel<AgreementContactInstance> {
	
	protected final AgreementContactListServiceAsync 	agreementContactListService 	= GWT.create(AgreementContactListService.class);
	protected final UpdateAgreementContactServiceAsync	updateAgreementContactService	= GWT.create(UpdateAgreementContactService.class);
	protected final UpdateContactNoteServiceAsync		updateContactNoteService		= GWT.create(UpdateContactNoteService.class);
	
	protected FormInnerPanel				formColumn1;
	protected FormInnerPanel				formColumn2;
	protected FormInnerPanel				formRow2;
	
	protected RowExpander			noteExpander;

	protected MultiField<String>			idNotesCombo		= new MultiField<String>("Agreement #");
	protected LabelField					agreementIdField	= getLabelField();
	protected NotesIconButtonField<String>	notesField			= getNotesButtonField();
	protected InstitutionSearchField		institutionField	= getInstitutionField("ucn", "Institution", 250, "The parent institution for this contact.");
	protected ContactSearchField			contactField		= getContactField("contact", "Contact", 250, "Find or create a contact to attach to this agreement");
	protected EnhancedComboBox<BeanModel>	contactTypeField	= getComboField("contactType", 		"Contact Type",	250,		
																		"The type for this contact.",	
																		UiConstants.getContactTypes(), "contactTypeCode", "description");
	protected TextField<String>				fullNameDisplay		= getTextField("Name");
	protected CheckBoxGroup					renewalContactGroup = new CheckBoxGroup();
	protected CheckBox						renewalContactCheck	= getCheckBoxField("Renewal Contact");
	protected TextArea						addressDisplay		= getMultiLineField("Address", 3);
	protected TextField<String>				cityDisplay			= getTextField("City");
	protected MultiField<String>			stateZipCountryCombo= new MultiField<String>("State/Zip/Country");
	protected TextField<String>				stateDisplay		= getTextField("State");
	protected TextField<String>				zipDisplay			= getTextField("Zip");
	protected TextField<String>				countryDisplay		= getTextField("Country");
	protected TextField<String>				titleDisplay		= getTextField("Title");
	protected TextField<String>				phoneDisplay		= getTextField("Phone");
	protected TextField<String>				phone2Display		= getTextField("Phone (alt)");
	protected TextField<String>				faxDisplay			= getTextField("Fax");
	protected TextField<String>				emailDisplay		= getTextField("E-Mail");
	protected TextField<String>				email2Display		= getTextField("E-Mail (alt)");
	
	protected ContactInstance				selectedContact;

	protected AgreementInstance				agreement;
	protected InstitutionInstance			billToInstitution;
	
	public int getAgreementId() {
		return getFocusId();
	}
	
	public void setAgreementId(int agreementId) {
		setFocusId(agreementId);
	}
	
	public void setAgreementContact(AgreementContactInstance instance) {
		setFocusInstance(instance);
	}

	public void setAgreement(AgreementInstance agreement) {
		this.agreement = agreement;
	}

	public void setBillToInstitution(InstitutionInstance billToInstitution) {
		this.billToInstitution = billToInstitution;
	}
	
	@Override
	protected String getButtonString() {
		return NEW_BUTTON + EDIT_BUTTON + CANCEL_BUTTON + SAVE_BUTTON + DELETE_BUTTON;
	}
	
	@Override
	public String getDeleteMessage() {
		return "Do you wish to remove this contact from this agreement?  The contact will not be deleted from its parent institution.";
	}

	@Override
	public void awaken() {
	}

	@Override
	public void sleep() {
	}
	
	public String getFormHeading() {
		return "Agreement Contacts";
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
		grid.setAutoExpandColumn("contact.fullName");  	
		gridStore.setKeyProvider(new SimpleKeyProvider("uniqueKey")); 
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
		selectedContact = instance == null ? null : instance.getContact();
		
		if (instance != null)
			agreementIdField.setValue(AppConstants.appendCheckDigit(getAgreementId()) + " &nbsp;&nbsp;&nbsp;<i>Contact " + AppConstants.getStatusDescription(instance.getStatus()) + "</i>");
		else
			agreementIdField.setValue(AppConstants.appendCheckDigit(getAgreementId()));
		
		contactField.setValue(selectedContact);	//	ContactSearchResultInstance.obtainModel(new ContactSearchResultInstance(instance.getContact())));
		
		//	This is a kludge bug fix, because contactField above is going to fire a selectionChanged event that's going to change it to the old one
		selectedContact = instance == null ? null : instance.getContact();
		
		if (instance != null)
			renewalContactCheck.setValue(instance.isRenewalContact());
		else
			renewalContactCheck.setValue(false);
			
		if (selectedContact != null) {
			institutionField.setValue(InstitutionInstance.obtainModel(selectedContact.getInstitution()));
			contactTypeField.setValue(ContactTypeInstance.obtainModel(selectedContact.getContactType()));
			fullNameDisplay.setValue(selectedContact.getFullName());
			titleDisplay.setValue(selectedContact.getTitle());
			phoneDisplay.setValue(selectedContact.getPhone());
			phone2Display.setValue(selectedContact.getPhone2());
			faxDisplay.setValue(selectedContact.getFax());
			emailDisplay.setValue(selectedContact.geteMail());
			email2Display.setValue(selectedContact.geteMail2());
			addressDisplay.setValue(getAddressLines(selectedContact));
			cityDisplay.setValue(selectedContact.getCity());
			stateDisplay.setValue(selectedContact.getState());
			zipDisplay.setValue(selectedContact.getZip());
			countryDisplay.setValue(selectedContact.getCountry());

			setNotesField(selectedContact.getNote());
			
		} else {
			if (billToInstitution != null)
				institutionField.setValue(InstitutionInstance.obtainModel(billToInstitution));
			else if (agreement != null && agreement.getInstitution() != null)
				institutionField.setValue(InstitutionInstance.obtainModel(agreement.getInstitution()));
			else
				institutionField.clear();
			contactTypeField.clear();
			fullNameDisplay.clear();
			titleDisplay.clear();
			phoneDisplay.clear();
			phone2Display.clear();
			faxDisplay.clear();
			emailDisplay.clear();
			email2Display.clear();
			addressDisplay.clear();
			cityDisplay.clear();
			stateDisplay.clear();
			zipDisplay.clear();
			countryDisplay.clear();
			
			setNotesField("");
		}
	}
	
	public String getAddressLines(ContactInstance contact) {
		return getAddressLines(contact.getAddress1(), contact.getAddress2(), contact.getAddress3());
	}
	
	public String getAddressLines(String address1, String address2, String address3) {
		String value = address1;
		if (address2 != null && address2.length() > 0)
			value += '\n' + address2;
		if (address3 != null && address3.length() > 0)
			value += '\n' + address3;
		return value;
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
			AsyncCallback<List<AgreementContactInstance>> callback) {
		agreementContactListService.getAgreementContacts(id, AppConstants.STATUS_DELETED, callback);
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
		formColumn2 = getNewFormInnerPanel(75);
		
//		contactIdDisplay.setToolTip(UiConstants.getQuickTip("The unique ID for this contact."));
//		contactTypeField.setToolTip(UiConstants.getQuickTip("The type of contact."));
		fullNameDisplay.setToolTip(UiConstants.getQuickTip("The full name for the contact."));
		titleDisplay.setToolTip(UiConstants.getQuickTip("The title of the contact at his or her position."));
		
		fullNameDisplay.setMinLength(1);

		agreementIdField.setReadOnly(true);
		agreementIdField.setWidth(200);
		idNotesCombo.setSpacing(20);
		
		stateZipCountryCombo.setSpacing(20);
		stateDisplay.setWidth(30);
		zipDisplay.setWidth(60);
		countryDisplay.setWidth(80);
		
//		addressDisplay.setHeight(72);
		
		renewalContactGroup.setLabelSeparator("");
		
//		contactTypeField.setWidth(200);
//		fullNameDisplay.setWidth(250);
//		titleDisplay.setWidth(250);
//		phoneDisplay.setWidth(200);
//		emailDisplay.setWidth(200);
//		addressDisplay.setWidth(200);

		idNotesCombo.add(agreementIdField);	
		idNotesCombo.add(notesField);
		
		stateZipCountryCombo.add(stateDisplay);
		stateZipCountryCombo.add(zipDisplay);
		stateZipCountryCombo.add(countryDisplay);
		
		renewalContactGroup.add(renewalContactCheck);
		
		formColumn1.add(idNotesCombo,    formData);
		formColumn1.add(institutionField, formData);
		formColumn1.add(contactField, formData);
		formColumn1.add(contactTypeField, formData);
		formColumn1.add(fullNameDisplay, formData);
		formColumn1.add(titleDisplay, formData);
		formColumn1.add(emailDisplay, formData);
		formColumn1.add(email2Display, formData);
		formColumn2.add(renewalContactGroup, formData);
		formColumn2.add(addressDisplay, formData);
		formColumn2.add(cityDisplay, formData);
		formColumn2.add(stateZipCountryCombo, formData);
		formColumn2.add(phoneDisplay, formData);
		formColumn2.add(phone2Display, formData);
		formColumn2.add(faxDisplay, formData);

		panel.add(formColumn1,	tData1);
		panel.add(formColumn2,	tData1);
	}
	
	protected NotesIconButtonField<String> getNotesButtonField() {
		NotesIconButtonField<String> nibf = new NotesIconButtonField<String>(this) {
			@Override
			public void updateNote(String note) {
				asyncUpdateNote(note);
			}
		};
		nibf.setLabelSeparator("");
		nibf.setEmptyNoteText("Click the note icon to add notes for this agreement site.");
		return nibf;
	}

	
	protected ContactSearchField getContactField(String name, String label, int width, String toolTip) {
        ContactSearchField contactCombo = new ContactSearchField();
		FieldFactory.setStandard(contactCombo, label);
		
		if (toolTip != null)
			contactCombo.setToolTip(toolTip);
		if (width > 0)
			contactCombo.setWidth(width);
//		contactCombo.setDisplayField("fullName");
		
		contactCombo.addSelectionChangedListener(new SelectionChangedListener<BeanModel>() {

			@Override
			public void selectionChanged(SelectionChangedEvent<BeanModel> se) {
				selectContact(se.getSelectedItem());
			}
			
		});
		
		return contactCombo;
	}
	
	protected void selectContact(BeanModel model) {
		if (model != null) {
			ContactSearchResultInstance instance = (ContactSearchResultInstance) model.getBean();
			if (instance.getType() == ContactSearchResultInstance.CONTACT) {
				matchToContact( instance.getContact() );
				return;
			} else
				return;
		}
		
		if (contactField.getSelectedValue() != null) {
			ContactSearchResultInstance instance = (ContactSearchResultInstance) contactField.getSelectedValue().getBean();
			matchToContact( instance.getContact() );
		}else
			if (contactField.getOriginalValue() != null) {
				ContactSearchResultInstance instance = (ContactSearchResultInstance) contactField.getOriginalValue().getBean();
				matchToContact( instance.getContact() );
			} else
				matchToContact( selectedContact );
	}
	
	protected void matchToContact(ContactInstance instance) {
//		contactBinding.bind(ContactInstance.obtainModel(billToContact));
		selectedContact = instance;
		
		if (instance == null) {
			if (billToInstitution != null)
				institutionField.setValue(InstitutionInstance.obtainModel(billToInstitution));
			else
				institutionField.setValue(InstitutionInstance.obtainModel(InstitutionInstance.getEmptyInstance()));
			contactTypeField.setValue(ContactTypeInstance.obtainModel(ContactTypeInstance.getEmptyInstance()));
			fullNameDisplay.setValue("");
			phoneDisplay.setValue("");
			phone2Display.setValue("");
			faxDisplay.setValue("");
			emailDisplay.setValue("");
			email2Display.setValue("");
			titleDisplay.setValue("");
			addressDisplay.setValue("");
			cityDisplay.setValue("");
			stateDisplay.setValue("");
			zipDisplay.setValue("");
			countryDisplay.setValue("");
			
			setNotesField("");
			return;
		}

		if (instance.getInstitution() != null)
			institutionField.setValue(InstitutionInstance.obtainModel(instance.getInstitution()));
		else
			institutionField.setValue(InstitutionInstance.obtainModel(InstitutionInstance.getEmptyInstance()));
		contactTypeField.setValue(ContactTypeInstance.obtainModel(instance.getContactType()));
		fullNameDisplay.setValue(instance.getFullName());
		phoneDisplay.setValue(instance.getPhone());
		phone2Display.setValue(instance.getPhone2());
		faxDisplay.setValue(instance.getFax());
		emailDisplay.setValue(instance.geteMail());
		email2Display.setValue(instance.geteMail2());
		titleDisplay.setValue(instance.getTitle());

		addressDisplay.setValue(getAddressLines(instance));
		cityDisplay.setValue(instance.getCity());
		stateDisplay.setValue(instance.getState());
		zipDisplay.setValue(instance.getZip());
		countryDisplay.setValue(instance.getCountry());
		
		setNotesField(instance.getNote());
	}

	
	protected InstitutionSearchField getInstitutionField(String name, String label, int width, String toolTip) {
        InstitutionSearchField instCombo = new InstitutionSearchField();
		FieldFactory.setStandard(instCombo, label);
		
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
				else
					matchToInstitution( null );
		} else {
			InstitutionInstance instance = (InstitutionInstance) model.getBean();
			matchToInstitution( instance );
		}
	}
	
	protected void matchToInstitution( InstitutionInstance instance) {
		if (instance == null)
			contactField.setUcn(0);
		else
			contactField.setUcn(instance.getUcn());
	}
	
	protected String [] getAddressLines(String addressLines) {
		if (addressLines == null)
			return new String [0];
		String [] address = addressLines.split("\\n");
		return address;
	}
	
	@Override
	public void handleNew() {
		super.handleNew();
		agreementIdField.setValue(AppConstants.appendCheckDigit(getAgreementId()) + "&nbsp;&nbsp;&nbsp;New" );
	}

	@Override
	protected void asyncUpdate() {
	
		// Set field values from form fields
		ContactInstance contactInstance;
		
		if (contactField.getSelectedValue() != null) {
			ContactSearchResultInstance searchInstance = contactField.getSelectedValue().getBean();
			contactInstance = searchInstance.getContact();
			if (searchInstance.getType() == ContactSearchResultInstance.ADD_NEW || searchInstance.getContact() == null) {
				contactInstance = new ContactInstance();
				contactInstance.setNewRecord(true);
			}
		} else {
			contactInstance = new ContactInstance();
			contactInstance.setNewRecord(true);
		}
		
		contactInstance.setFullName(fullNameDisplay.getValue());
		contactInstance.setContactType( (ContactTypeInstance) contactTypeField.getSelectedValue().getBean());
		contactInstance.setTitle( titleDisplay.getValue());
		contactInstance.setPhone(phoneDisplay.getValue());
		contactInstance.setPhone2(phone2Display.getValue());
		contactInstance.setFax(faxDisplay.getValue());
		contactInstance.seteMail(emailDisplay.getValue());
		contactInstance.seteMail2(email2Display.getValue());
		
		if (institutionField.getSelectedValue() != null) {
			contactInstance.setInstitution( (InstitutionInstance) institutionField.getSelectedValue().getBean());
		} else
			contactInstance.setInstitution( null );
		
		String [] addressLines = getAddressLines(addressDisplay.getValue());
		if (addressLines.length > 0)
			contactInstance.setAddress1(addressLines [0]);
		else
			contactInstance.setAddress1("");
		if (addressLines.length > 1)
			contactInstance.setAddress2(addressLines [1]);
		else
			contactInstance.setAddress2("");
		if (addressLines.length > 2)
			contactInstance.setAddress3(addressLines [2]);
		else
			contactInstance.setAddress3("");
		
		contactInstance.setCity(cityDisplay.getValue());
		contactInstance.setState(stateDisplay.getValue());
		contactInstance.setZip(zipDisplay.getValue());
		contactInstance.setCountry(countryDisplay.getValue());
		
		if (focusInstance == null) {
			focusInstance = new AgreementContactInstance();
			focusInstance.setNewRecord(true);
			focusInstance.setAgreementId(getAgreementId());
			
//			if (contactInstance == null) {
//				MessageBox.alert("Unexpected Error", "No contact is selected.", null);
//				return;
//			}
			
			focusInstance.setContactId(contactInstance.getContactId());
		}
		
		focusInstance.setContact(contactInstance);	
		focusInstance.setRenewalContact(renewalContactCheck.getValue() ? 'y' : 'n');
		focusInstance.setStatus(AppConstants.STATUS_ACTIVE);
		
		if (contactInstance.isNewRecord())
			contactInstance.setNote(notesField.getNote());
		else
			contactInstance.setNote(null);	//	This will keep the note from being updated by this call
	
		//	Issue the asynchronous update request and plan on handling the response
		updateAgreementContactService.updateAgreementContact(focusInstance,
				new AsyncCallback<UpdateResponse<AgreementContactInstance>>() {
					public void onFailure(Throwable caught) {
						// Show the RPC error message to the user
						if (caught instanceof IllegalArgumentException)
							MessageBox.alert("Alert", caught.getMessage(), null);
						else {
							MessageBox.alert("Alert", "Agreement contact  update failed unexpectedly.", null);
							System.out.println(caught.getClass().getName());
							System.out.println(caught.getMessage());
						}
						deleteButton.setEnabled(focusInstance != null && !focusInstance.isNewRecord());
						editButton.enable();
						newButton.enable();
					}

					public void onSuccess(UpdateResponse<AgreementContactInstance> updateResponse) {
						AgreementContactInstance updatedAgreementContact = (AgreementContactInstance) updateResponse.getInstance();
						boolean isNew = updatedAgreementContact.isNewRecord();
						
						updatedAgreementContact.setNewRecord(false);
						focusInstance.setNewRecord(false);
						focusInstance.setValuesFrom(updatedAgreementContact);
						focusInstance.getContact().setValuesFrom(updatedAgreementContact.getContact());
						setFormFromInstance(focusInstance);	//	setFormFieldValues(updatedAgreementContact);
						
						if (isNew) {
							grid.getStore().insert(AgreementContactInstance.obtainModel(focusInstance), 0);
						} else {
							//	This puts the grid in synch
							BeanModel gridModel = grid.getStore().findModel(focusInstance.getUniqueKey());
							if (gridModel != null) {
								AgreementContactInstance matchInstance = gridModel.getBean();
								matchInstance.setValuesFrom(focusInstance);
	//							matchInstance.getContact().setValuesFrom(focusInstance.getContact());
								grid.getStore().update(gridModel);
							}
						}
						
						deleteButton.enable();
						editButton.enable();
						newButton.enable();
				}
			});
	}

	@Override
	protected void asyncDelete() {
		if (focusInstance == null)
			return;
		
		focusInstance.setStatus(AppConstants.STATUS_DELETED);
	
		//	Issue the asynchronous update request and plan on handling the response
		updateAgreementContactService.updateAgreementContact(focusInstance,
				new AsyncCallback<UpdateResponse<AgreementContactInstance>>() {
					public void onFailure(Throwable caught) {
						// Show the RPC error message to the user
						if (caught instanceof IllegalArgumentException)
							MessageBox.alert("Alert", caught.getMessage(), null);
						else {
							MessageBox.alert("Alert", "Agreement contact delete failed unexpectedly.", null);
							System.out.println(caught.getClass().getName());
							System.out.println(caught.getMessage());
						}
						deleteButton.enable();
						editButton.enable();
						newButton.enable();
					}

					public void onSuccess(UpdateResponse<AgreementContactInstance> updateResponse) {
						AgreementContactInstance updatedAgreementContact = (AgreementContactInstance) updateResponse.getInstance();
						//	This puts the grid in synch
						BeanModel gridModel = grid.getStore().findModel(updatedAgreementContact.getUniqueKey());
						if (gridModel != null) {
							grid.getStore().remove(gridModel);
						}
						
						focusInstance = null;
						selectedContact = null;
						
						//	Clear contact form values
						setFormFieldValues(focusInstance);
						
						deleteButton.disable();
						editButton.disable();
						newButton.enable();
				}
			});
	}
	
	protected void asyncUpdateNote(String note) {
	
		// Set field values from form fields
		
		if (focusInstance == null || focusInstance.isNewRecord()) {
			return;
		}
		
		focusInstance.getContact().setNote(note);
	
		//	Issue the asynchronous update request and plan on handling the response
		updateContactNoteService.updateContactNote(focusInstance.getContact(),
				new AsyncCallback<UpdateResponse<ContactInstance>>() {
					public void onFailure(Throwable caught) {
						// Show the RPC error message to the user
						if (caught instanceof IllegalArgumentException)
							MessageBox.alert("Alert", caught.getMessage(), null);
						else {
							MessageBox.alert("Alert", "Contact note update failed unexpectedly.", null);
							System.out.println(caught.getClass().getName());
							System.out.println(caught.getMessage());
						}
						notesField.unlockNote();
					}

					public void onSuccess(UpdateResponse<ContactInstance> updateResponse) {
						ContactInstance updatedContact = (ContactInstance) updateResponse.getInstance();
						//	This makes sure the field and instance are in synch
						if (!notesField.getNote().equals(updatedContact.getNote())) {
							focusInstance.getContact().setNote(updatedContact.getNote());
							setNotesField(updatedContact.getNote());
						}
						//	This puts the grid in synch
						BeanModel gridModel = grid.getStore().findModel(focusInstance.getUniqueKey());
						if (gridModel != null) {
							AgreementContactInstance matchInstance = gridModel.getBean();
							matchInstance.getContact().setNote(focusInstance.getContact().getNote());
							grid.getStore().update(gridModel);
						}
						notesField.unlockNote();
				}
			});
	}
	
	
}
