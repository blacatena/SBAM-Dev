package com.scholastic.sbam.client.uiobjects;

import java.util.List;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.MultiField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.RowExpander;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.scholastic.sbam.client.services.AgreementContactListService;
import com.scholastic.sbam.client.services.AgreementContactListServiceAsync;
import com.scholastic.sbam.client.services.UpdateContactNoteService;
import com.scholastic.sbam.client.services.UpdateContactNoteServiceAsync;
import com.scholastic.sbam.client.util.UiConstants;
import com.scholastic.sbam.shared.objects.AgreementContactInstance;
import com.scholastic.sbam.shared.objects.ContactInstance;
import com.scholastic.sbam.shared.objects.ContactSearchResultInstance;
import com.scholastic.sbam.shared.objects.ContactTypeInstance;
import com.scholastic.sbam.shared.objects.UpdateResponse;
import com.scholastic.sbam.shared.util.AppConstants;

public class AgreementContactsCard extends FormAndGridPanel<AgreementContactInstance> {
	
	protected final AgreementContactListServiceAsync agreementContactListService = GWT.create(AgreementContactListService.class);
	protected final UpdateContactNoteServiceAsync	updateContactNoteService	= GWT.create(UpdateContactNoteService.class);
	
	protected FormPanel				formColumn1;
	protected FormPanel				formColumn2;
	protected FormPanel				formRow2;
	
	protected RowExpander			noteExpander;

	protected MultiField<String>			idNotesCombo		= new MultiField<String>("Agreement #");
	protected LabelField					agreementIdField	= getLabelField();
	protected NotesIconButtonField<String>	notesField			= getNotesButtonField();
	protected ContactSearchField			contactField		= getContactField("contact", "Contact", 250, "Find or create a contact to attach to this agreement");
	protected EnhancedComboBox<BeanModel>	contactTypeField	= getComboField("contactType", 		"Contact Type",	250,		
																		"The type for this contact.",	
																		UiConstants.getContactTypes(), "contactTypeCode", "description");
	protected TextField<String>				fullNameDisplay		= getTextField("Name");
	protected LabelField					addressDisplay		= getLabelField(250);
	protected TextField<String>				titleDisplay		= getTextField("Title");
	protected TextField<String>				phoneDisplay		= getTextField("Phone");
	protected TextField<String>				emailDisplay		= getTextField("E-Mail");
	
	protected ContactInstance				contact;
	
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
		
		agreementIdField.setValue(AppConstants.appendCheckDigit(instance.getAgreementId()) + " &nbsp;&nbsp;&nbsp;<i>Contact " + AppConstants.getStatusDescription(instance.getStatus()) + "</i>");
		
		contactField.setValue(ContactSearchResultInstance.obtainModel(new ContactSearchResultInstance(instance.getContact())));
		
		if (contact != null) {
			contactTypeField.setValue(ContactTypeInstance.obtainModel(contact.getContactType()));
			fullNameDisplay.setValue(contact.getFullName());
			titleDisplay.setValue(contact.getTitle());
			phoneDisplay.setValue(contact.getPhone());
			emailDisplay.setValue(contact.geteMail());
			addressDisplay.setValue(contact.getHtmlAddress());

			setNotesField(contact.getNote());
		} else {
			contactTypeField.clear();
			fullNameDisplay.clear();
			titleDisplay.clear();
			phoneDisplay.clear();
			emailDisplay.clear();
			addressDisplay.clear();
			
			setNotesField("");
		}
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
		agreementContactListService.getAgreementContacts(id, AppConstants.STATUS_DELETED,callback);
	}

	@Override
	protected void addFormFields(FormPanel panel, FormData formData) {
		formData = new FormData("-24");
		
		panel.setLayout(new TableLayout(2));
		
		formColumn1 = getNewFormPanel(75);
		formColumn2 = getNewFormPanel(75);
		
//		contactIdDisplay.setToolTip(UiConstants.getQuickTip("The unique ID for this contact."));
//		contactTypeField.setToolTip(UiConstants.getQuickTip("The type of contact."));
		fullNameDisplay.setToolTip(UiConstants.getQuickTip("The full name for the contact."));
		titleDisplay.setToolTip(UiConstants.getQuickTip("The title of the contact at his or her position."));

		agreementIdField.setReadOnly(true);
		agreementIdField.setWidth(200);
		idNotesCombo.setSpacing(20);
		
//		contactTypeField.setWidth(200);
		fullNameDisplay.setWidth(250);
		titleDisplay.setWidth(250);
		phoneDisplay.setWidth(200);
		emailDisplay.setWidth(200);
		addressDisplay.setWidth(200);

		idNotesCombo.add(agreementIdField);	
		idNotesCombo.add(notesField);
		formColumn1.add(idNotesCombo,    formData);
		formColumn1.add(contactField, formData);	
		formColumn1.add(contactTypeField, formData);
		formColumn1.add(fullNameDisplay, formData);
		formColumn2.add(addressDisplay, formData);
		formColumn2.add(phoneDisplay, formData);
		formColumn2.add(emailDisplay, formData);

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
				matchToContact( contact );
	}
	
	protected void matchToContact(ContactInstance instance) {
//		contactBinding.bind(ContactInstance.obtainModel(billToContact));
		
		if (instance == null) {
			contactTypeField.setValue(ContactTypeInstance.obtainModel(ContactTypeInstance.getEmptyInstance()));
			fullNameDisplay.setValue("");
			phoneDisplay.setValue("");
			emailDisplay.setValue("");
			titleDisplay.setValue("");
			return;
		}
		
		contactTypeField.setValue(ContactTypeInstance.obtainModel(instance.getContactType()));
		fullNameDisplay.setValue(instance.getFullName());
		phoneDisplay.setValue(instance.getPhone());
		emailDisplay.setValue(instance.geteMail());
		titleDisplay.setValue(instance.getTitle());
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
