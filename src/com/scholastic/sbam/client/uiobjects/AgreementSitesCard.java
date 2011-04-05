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
import com.scholastic.sbam.client.services.AgreementSiteListService;
import com.scholastic.sbam.client.services.AgreementSiteListServiceAsync;
import com.scholastic.sbam.client.services.UpdateAgreementSiteNoteService;
import com.scholastic.sbam.client.services.UpdateAgreementSiteNoteServiceAsync;
import com.scholastic.sbam.client.util.UiConstants;
import com.scholastic.sbam.shared.objects.AgreementSiteInstance;
import com.scholastic.sbam.shared.objects.CancelReasonInstance;
import com.scholastic.sbam.shared.objects.CommissionTypeInstance;
import com.scholastic.sbam.shared.objects.InstitutionInstance;
import com.scholastic.sbam.shared.objects.SimpleKeyProvider;
import com.scholastic.sbam.shared.objects.SiteInstance;
import com.scholastic.sbam.shared.objects.UpdateResponse;
import com.scholastic.sbam.shared.util.AppConstants;

public class AgreementSitesCard extends FormAndGridPanel<AgreementSiteInstance> {
	
	protected final AgreementSiteListServiceAsync agreementSiteListService = GWT.create(AgreementSiteListService.class);
	protected final UpdateAgreementSiteNoteServiceAsync	updateAgreementSiteNoteService	= GWT.create(UpdateAgreementSiteNoteService.class);
	
	protected FormPanel				formColumn1;
	protected FormPanel				formColumn2;
	protected FormPanel				formRow2;
	
	protected InstitutionInstance	siteInstitution;
	
	protected RowExpander			noteExpander;

	protected MultiField<String>			idNotesCombo		= new MultiField<String>("Agreement #");
	protected LabelField					agreementIdField	= new LabelField();
	protected NotesIconButtonField<String>	notesField			= getNotesButtonField();
	protected InstitutionSearchField		institutionField	= getInstitutionField("ucn", "Site", 290, "The institution that will receive the product services.");
	protected LabelField					addressDisplay		= new LabelField();
	protected TextField<String>				ucnDisplay			= getTextField("ICN+");
	protected LabelField					customerTypeDisplay	= new LabelField();
	protected SiteLocationSearchField		siteLocationField	= getSiteLocationField("uniqueKey", "Site Location", 290, "The specific location at the customer site.");
	
	protected EnhancedComboBox<BeanModel>	commissionTypeField	= getComboField("commissionType", 	"Commission",	290,		
			"The commission code assigned to this site for reporting purposes.",	
			UiConstants.getCommissionTypes(UiConstants.CommissionTypeTargets.SITE), "commissionCode", "descriptionAndCode");
	protected EnhancedComboBox<BeanModel>	cancelReasonField	= getComboField("cancelReason", 	"Cancel",	290,		
			"The reason for canceling (deactivating) for this site.",
			UiConstants.getCancelReasons(), "cancelReasonCode", "descriptionAndCode");
	
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
		return "Sites";
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
		grid.setAutoExpandColumn("site.institution.institutionName"); 
		gridStore.setKeyProvider(new SimpleKeyProvider("uniqueKey")); 	
	}

	@Override
	public void addGridColumns(List<ColumnConfig> columns) {

		columns.add(getDisplayColumn("displayUcn",							"UCN+",						100,		false,
					"This is the UCN+ for the site."));
		columns.add(getDisplayColumn("site.institution.institutionName",	"Institution",				180,
					"This is the institution name."));
		columns.add(getDisplayColumn("site.description",					"Location",					100,
					"This is the description of the location at the site."));
		columns.add(getDisplayColumn("site.institution.htmlAddress",		"Address",					180));
		
//		These hidden columns were dropped because they mess up the columns widths and the notes row expander cell... a bug in GXT, it seems.
//		columns.add(getHiddenColumn("siteUcn",								"UCN",						 40,		true, UiConstants.INTEGER_FORMAT,
//				"This is the UCN for the site."));
//		columns.add(getHiddenColumn("siteUcnSuffix",						"Suffix",					 40,		true, UiConstants.INTEGER_FORMAT,
//				"This is the suffix for this pseudo site."));
		columns.add(getHiddenColumn("siteLocCode",							"Code",						 40,
				"This is the code for the location at the site."));
	
		noteExpander = getNoteExpander();
		columns.add(noteExpander);
		
	}

	@Override
	public void setFormFieldValues(AgreementSiteInstance instance) {
		String displayStatus = "Site " + AppConstants.getStatusDescription(instance.getStatus());
		if (instance.getStatus() == AppConstants.STATUS_INACTIVE && instance.getCancelReasonCode() != null && instance.getCancelReasonCode().length() > 0 && instance.getInactiveDate() != null)
			displayStatus = "Deactivated " + instance.getInactiveDate();
		agreementIdField.setValue(AppConstants.appendCheckDigit(instance.getAgreementId()) + " &nbsp;&nbsp;&nbsp;<i>" + displayStatus + "</i>");
		
		if (instance.getSiteUcnSuffix() <= 1)
			ucnDisplay.setValue(instance.getSiteUcn() + "");
		else
			ucnDisplay.setValue(instance.getSiteUcn() + " - " + instance.getSiteUcnSuffix());
		
		set(instance.getSite().getInstitution());
		institutionField.setReadOnly(!instance.isNewRecord());
		
		siteLocationField.setFor(instance);
		siteLocationField.setValue(SiteInstance.obtainModel(instance.getSite()));
		siteLocationField.setReadOnly(!instance.isNewRecord());
		
		cancelReasonField.setValue(CancelReasonInstance.obtainModel(instance.getCancelReason()));
		commissionTypeField.setValue(CommissionTypeInstance.obtainModel(instance.getCommissionType()));

		setNotesField(instance.getNote());
		
//		setOriginalValues();
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
	public void handleNew() {
		super.handleNew();
		institutionField.setReadOnly(false);
		siteLocationField.setReadOnly(false);
	}

	
	@Override
	protected boolean isFormValidAndReady() {
		boolean ready = formPanel.isValid();
		
		//	Check for required fields
		if (institutionField.getSelectedValue() == null) { 
			institutionField.markInvalid("Select an institution.");
			ready = false;
		} else
			institutionField.clearInvalid();
		if (siteLocationField.getSelectedValue() == null) {
			siteLocationField.markInvalid("Select a site location.");
			ready = false;
		} else
			siteLocationField.clearInvalid();
//		if (commissionTypeField.getSelectedValue() == null) {
//			commissionTypeField.markInvalid("Select a commission code.");
//			ready = false;
//		} else
//			commissionTypeField.clearInvalid();
		
		return ready;
	}

	@Override
	protected void executeLoader(int id,
			AsyncCallback<List<AgreementSiteInstance>> callback) {
		agreementSiteListService.getAgreementSites(id, AppConstants.STATUS_DELETED,callback);
	}

	@Override
	protected void addFormFields(FormPanel panel, FormData formData) {
		formData = new FormData("-24");
		
		panel.setLayout(new TableLayout(2));
		
		formColumn1 = getNewFormPanel(75);
		formColumn2 = getNewFormPanel(75);
		
		ucnDisplay.setToolTip(UiConstants.getQuickTip("The ucn for the site."));
		addressDisplay.setToolTip(UiConstants.getQuickTip("The address of the institution."));

		agreementIdField.setReadOnly(true);
		agreementIdField.setWidth(150);
		idNotesCombo.setSpacing(20);
		ucnDisplay.setReadOnly(true);
		
		addressDisplay.setWidth(300);

		
		idNotesCombo.add(agreementIdField);	
		idNotesCombo.add(notesField);
		formColumn1.add(idNotesCombo,    formData);
		formColumn1.add(institutionField, formData);
		formColumn1.add(ucnDisplay, formData);	
		formColumn1.add(addressDisplay, formData);
		formColumn1.add(customerTypeDisplay, formData);
		formColumn2.add(siteLocationField, formData);
		formColumn2.add(commissionTypeField,	formData);
		formColumn2.add(cancelReasonField,	formData);
		
		panel.add(formColumn1);
		panel.add(formColumn2);
	}
	
	protected SiteLocationSearchField getSiteLocationField(String name, String label, int width, String toolTip) {
		SiteLocationSearchField siteLocCombo = new SiteLocationSearchField();
		FieldFactory.setStandard(siteLocCombo, label);
		
		if (toolTip != null)
			siteLocCombo.setToolTip(toolTip);
		if (width > 0)
			siteLocCombo.setWidth(width);
		siteLocCombo.setDisplayField("descriptionAndCode");
		
//		Since site location can't be changed once set, this listener isn't needed
//		siteLocCombo.addSelectionChangedListener(new SelectionChangedListener<BeanModel>() {
//
//			@Override
//			public void selectionChanged(SelectionChangedEvent<BeanModel> se) {
//				selectSiteLocation(se.getSelectedItem());
//			}
//			
//		});
		
		return siteLocCombo;
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
					matchToInstitution( siteInstitution );
		} else {
			InstitutionInstance instance = (InstitutionInstance) model.getBean();
			matchToInstitution( instance );
		}
	}
	
	/**
	 * Set an institution on the form
	 * @param instance
	 */
	protected void set(InstitutionInstance instance) {
		if (siteInstitution == instance)
			return;
		
		siteInstitution = instance;
		
		if (siteInstitution == null) {
			MessageBox.alert("Institution Not Found", "The Institution for the site was not found.", null);
			siteInstitution = InstitutionInstance.getEmptyInstance(); 
		}

		if (institutionField.getSelectedValue() == null || !siteInstitution.equals(institutionField.getSelectedValue().getBean())) {
			institutionField.setValue(InstitutionInstance.obtainModel(siteInstitution));
		}

		matchToInstitution(siteInstitution);
	}
	
	protected void matchToInstitution(InstitutionInstance instance) {
//		institutionBinding.bind(InstitutionInstance.obtainModel(billToInstitution));
		
		if (instance == null) {
			ucnDisplay.setValue("");
			addressDisplay.setValue("");
			customerTypeDisplay.setValue("");
			return;
		}
		
		if (focusInstance != null && !focusInstance.isNewRecord()) {
			siteLocationField.setFor(instance.getUcn(), 1);
			siteLocationField.setValue(SiteInstance.obtainModel(SiteInstance.getAllInstance(instance.getUcn(), 1)));
//			siteLocationField.setReadOnly(!agreementSite.isNewRecord());
		}
		
		ucnDisplay.setValue(instance.getUcn() + "");
		addressDisplay.setValue(instance.getHtmlAddress());
		customerTypeDisplay.setValue(instance.getPublicPrivateDescription() + " / " + instance.getGroupDescription() + " &rArr; " + instance.getTypeDescription());
		
	}

	protected void asyncUpdateNote(String note) {
	
		// Set field values from form fields
		
		if (focusInstance == null || focusInstance.isNewRecord()) {
			return;
		}
		
		focusInstance.setNote(note);
	
		//	Issue the asynchronous update request and plan on handling the response
		updateAgreementSiteNoteService.updateAgreementSiteNote(focusInstance,
				new AsyncCallback<UpdateResponse<AgreementSiteInstance>>() {
					public void onFailure(Throwable caught) {
						// Show the RPC error message to the user
						if (caught instanceof IllegalArgumentException)
							MessageBox.alert("Alert", caught.getMessage(), null);
						else {
							MessageBox.alert("Alert", "Agreement site note update failed unexpectedly.", null);
							System.out.println(caught.getClass().getName());
							System.out.println(caught.getMessage());
						}
						notesField.unlockNote();
					}

					public void onSuccess(UpdateResponse<AgreementSiteInstance> updateResponse) {
						AgreementSiteInstance updatedAgreementSite = (AgreementSiteInstance) updateResponse.getInstance();
						//	This makes sure the field and instance are in synch
						if (!notesField.getNote().equals(updatedAgreementSite.getNote())) {
							focusInstance.setNote(updatedAgreementSite.getNote());
							setNotesField(updatedAgreementSite.getNote());
						}
						//	This puts the grid in synch
						BeanModel gridModel = grid.getStore().findModel(focusInstance.getUniqueKey());
						if (gridModel != null) {
							AgreementSiteInstance matchInstance = gridModel.getBean();
							matchInstance.setNote(focusInstance.getNote());
							grid.getStore().update(gridModel);
						}
						notesField.unlockNote();
				}
			});
	}
}
