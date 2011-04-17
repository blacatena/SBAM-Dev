package com.scholastic.sbam.client.uiobjects.uiapp;

import java.util.List;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.CheckBoxGroup;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.MultiField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.RowExpander;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.scholastic.sbam.client.services.AuthMethodListService;
import com.scholastic.sbam.client.services.AuthMethodListServiceAsync;
import com.scholastic.sbam.client.services.UpdateAuthMethodNoteService;
import com.scholastic.sbam.client.services.UpdateAuthMethodNoteServiceAsync;
import com.scholastic.sbam.client.services.UpdateAuthMethodService;
import com.scholastic.sbam.client.services.UpdateAuthMethodServiceAsync;
import com.scholastic.sbam.client.uiobjects.fields.InstitutionSearchField;
import com.scholastic.sbam.client.uiobjects.fields.IpAddressField;
import com.scholastic.sbam.client.uiobjects.fields.NotesIconButtonField;
import com.scholastic.sbam.client.uiobjects.fields.SiteLocationSearchField;
import com.scholastic.sbam.client.uiobjects.foundation.FieldFactory;
import com.scholastic.sbam.client.uiobjects.foundation.FormAndGridPanel;
import com.scholastic.sbam.client.util.UiConstants;
import com.scholastic.sbam.shared.objects.AgreementInstance;
import com.scholastic.sbam.shared.objects.AuthMethodInstance;
import com.scholastic.sbam.shared.objects.InstitutionInstance;
import com.scholastic.sbam.shared.objects.SimpleKeyProvider;
import com.scholastic.sbam.shared.objects.SiteInstance;
import com.scholastic.sbam.shared.objects.UpdateResponse;
import com.scholastic.sbam.shared.util.AppConstants;

public class AgreementMethodsCard extends FormAndGridPanel<AuthMethodInstance> {
	
	private static final int DEFAULT_FIELD_WIDTH	=	0;	//250;
//	private static final String OCTET_REGEX = "[0-9]|[0-9][0-9]|[0-1][0-9][0-9]|2[0-4][0-9]|25[0-5]|\\*";
//	private static final String OCTET_REGEX = "([0-9])|([0-9][0-9])|([0-1][0-9][0-9])|(2[0-4][0-9])|(25[0-5])|(\\*)";
//	private static final String OCTET_ERROR = "An octet may be any value from 0 to 255, or an *.";
	
//	private static final String MSG_SITE_REQUIRED = "Select a site location.";
	
	protected final AuthMethodListServiceAsync 			authMethodListService 		= GWT.create(AuthMethodListService.class);
	protected final UpdateAuthMethodServiceAsync		updateAuthMethodService		= GWT.create(UpdateAuthMethodService.class);
	protected final UpdateAuthMethodNoteServiceAsync	updateAuthMethodNoteService	= GWT.create(UpdateAuthMethodNoteService.class);

	protected FormPanel				formRow1;
	protected FormPanel				formColumn1;
	protected FormPanel				formColumn2;
	protected FormPanel				formRow2;
	
	protected AgreementInstance		agreement;
	protected InstitutionInstance	siteInstitution;
	
	protected RowExpander			noteExpander;

	protected MultiField<String>			idNotesCombo		= new MultiField<String>("Agreement #");
	protected LabelField					agreementIdField	= getLabelField();
	protected NotesIconButtonField<String>	notesField			= getNotesButtonField();
	protected InstitutionSearchField		institutionField	= getInstitutionField("ucn", "Site", DEFAULT_FIELD_WIDTH, "The institution that will receive the product services through this authentication method.");
	protected SiteLocationSearchField		siteLocationField	= getSiteLocationField("uniqueKey", "Site Location", DEFAULT_FIELD_WIDTH, "The specific location at the customer site targeted by this authentication method.");
//	protected LabelField					addressDisplay		= getLabelField();
	protected TextField<String>				ucnDisplay			= getTextField("UCN+");
//	protected LabelField					customerTypeDisplay	= getLabelField();
	protected CheckBox						approvedCheck		= getCheckBoxField("Approved");
	protected CheckBox						validatedCheck		= getCheckBoxField("Validated");
	protected CheckBox						remoteCheck			= getCheckBoxField("Remote");
	protected CheckBoxGroup					statusGroup			= getCheckBoxGroup(null, approvedCheck, validatedCheck, remoteCheck);
	
	protected IpAddressField				ipLoField			= new IpAddressField("From");
//	protected TextField<String>				ipLoOctet1Field		= getTextField("");
//	protected TextField<String>				ipLoOctet2Field		= getTextField("");
//	protected TextField<String>				ipLoOctet3Field		= getTextField("");
//	protected TextField<String>				ipLoOctet4Field		= getTextField("");
	protected IpAddressField				ipHiField			= new IpAddressField("To");
//	protected TextField<String>				ipHiOctet1Field		= getTextField("");
//	protected TextField<String>				ipHiOctet2Field		= getTextField("");
//	protected TextField<String>				ipHiOctet3Field		= getTextField("");
//	protected TextField<String>				ipHiOctet4Field		= getTextField("");

	protected TextField<String>				userIdField			= getTextField("User ID");
	protected TextField<String>				passwordField		= getTextField("Password");
	protected CheckBox						cookieUidCheck		= getCheckBoxField("Cookie");
	protected CheckBox						permanentUidCheck	= getCheckBoxField("Permanent");
	protected CheckBoxGroup					uidTypeGroup		= getCheckBoxGroup("UID Type", permanentUidCheck, cookieUidCheck);
//	protected EnhancedComboBox<BeanModel>	uidTypeField	= getComboField("uidType", 	"UID Type",	DEFAULT_FIELD_WIDTH,		
//			"The type of user ID to deploy.",	
//			UiConstants.getUidTypes(), "code", "name");

	protected TextField<String>				urlField			= getTextField("URL");
	

	protected FieldSet						ipFieldSet		= new FieldSet() 
//																{
//																	@Override
//																	public void onExpand() {
//																		super.onExpand();
//																		uidFieldSet.collapse();
//																		urlFieldSet.collapse();
//																	}
//																}
																;
	protected FieldSet						uidFieldSet		= new FieldSet() 
//																{
//																	@Override
//																	public void onExpand() {
//																		super.onExpand();
//																		ipFieldSet.collapse();
//																		urlFieldSet.collapse();
//																	}
//																}
																;
	protected FieldSet						urlFieldSet		= new FieldSet() 
//																{
//																	@Override
//																	public void onExpand() {
//																		super.onExpand();
//																		uidFieldSet.collapse();
//																		ipFieldSet.collapse();
//																	}
//																}
																;
	
	
//	protected EnhancedComboBox<BeanModel>	cancelReasonField	= getComboField("cancelReason", 	"Cancel",	DEFAULT_FIELD_WIDTH,		
//			"The reason for canceling (deactivating) for this site.",
//			UiConstants.getCancelReasons(), "cancelReasonCode", "descriptionAndCode");
	
	public int getAgreementId() {
		return getFocusId();
	}
	
	public void setAgreementId(int agreementId) {
		setFocusId(agreementId);
	}
	
	public AgreementInstance getAgreement() {
		return agreement;
	}

	public void setAgreement(AgreementInstance agreement) {
		this.agreement = agreement;
		setAgreementId(agreement.getId());
	}

	public void setAuthMethod(AuthMethodInstance instance) {
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
		grid.setAutoExpandColumn("methodDisplay"); 
		gridStore.setKeyProvider(new SimpleKeyProvider("uniqueKey")); 	
	}

	@Override
	public void addGridColumns(List<ColumnConfig> columns) {

//		columns.add(getDisplayColumn("displayUcn",							"UCN+",						100,		false,
//					"This is the UCN+ for the site."));
		columns.add(getDisplayColumn("methodDisplay",						"Method",					180));
		columns.add(getDisplayColumn("site.institution.institutionName",	"Institution",				180,
					"This is the institution name."));
		columns.add(getDisplayColumn("site.description",					"Location",					100,
					"This is the description of the location at the site."));
		columns.add(getHiddenColumn("siteLocCode",							"Code",						 40,
				"This is the code for the location at the site."));
	
		noteExpander = getNoteExpander();
		columns.add(noteExpander);
		
	}

	@Override
	public void setFormFieldValues(AuthMethodInstance instance) {
		String displayStatus = "Method " + AppConstants.getStatusDescription(instance.getStatus());
		if (instance.isActivated())
			if (instance.getReactivatedDatetime() != null)
				displayStatus += ", Rectivated " + UiConstants.formatDate(instance.getReactivatedDatetime());
			else
				displayStatus += ", Activated " + UiConstants.formatDate(instance.getActivatedDatetime());
		else
			if (instance.getDeactivatedDatetime() != null)
				displayStatus += ", Deactivated " + UiConstants.formatDate(instance.getDeactivatedDatetime());
		agreementIdField.setValue(AppConstants.appendCheckDigit(instance.getAgreementId()) + " &nbsp;&nbsp;&nbsp;<i>" + displayStatus + "</i>");
		
		if (instance.getUcnSuffix() <= 1)
			ucnDisplay.setValue(instance.getUcn() + "");
		else
			ucnDisplay.setValue(instance.getUcn() + " - " + instance.getUcnSuffix());
		
		set(instance.getSite().getInstitution());
		institutionField.setReadOnly(!instance.isNewRecord());
		
		siteLocationField.setFor(instance);
		siteLocationField.setValue(SiteInstance.obtainModel(instance.getSite()));
		siteLocationField.setReadOnly(!instance.isNewRecord());
		
		validatedCheck.setValue(instance.isValidated());
		approvedCheck.setValue(instance.isApproved());
		remoteCheck.setValue(instance.isRemote());
		
		if (AuthMethodInstance.AM_IP.equals(instance.getMethodType())) {
//			String [] [] octets = AuthMethodInstance.getIpOctetStrings(instance.getIpLo(), instance.getIpHi());
			ipLoField.setValue(instance.getIpLo()); //	ipLoField.setValue(octets [0]);
			ipHiField.setValue(instance.getIpHi()); //	ipHiField.setValue(octets [1]);
//			ipLoOctet1Field.setValue(octets [0] [0]);
//			ipLoOctet2Field.setValue(octets [0] [1]);
//			ipLoOctet3Field.setValue(octets [0] [2]);
//			ipLoOctet4Field.setValue(octets [0] [3]);
//			ipHiOctet1Field.setValue(octets [1] [0]);
//			ipHiOctet2Field.setValue(octets [1] [1]);
//			ipHiOctet3Field.setValue(octets [1] [2]);
//			ipHiOctet4Field.setValue(octets [1] [3]);
			openUrlFields(false);
			clearUrlFields();
			openUidFields(false);
			clearUidFields();
			openIpFields(true);
		} else if (AuthMethodInstance.AM_UID.equals(instance.getMethodType())) {
			userIdField.setValue(instance.getUserId());
			passwordField.setValue(instance.getPassword());
			cookieUidCheck.setValue(instance.isUserType(AuthMethodInstance.UserTypes.COOKIE));
			permanentUidCheck.setValue(instance.isUserType(AuthMethodInstance.UserTypes.PUP));
			openIpFields(false);
			clearIpFields();
			openUrlFields(false);
			clearUrlFields();
			openUidFields(true);
		} else if (AuthMethodInstance.AM_URL.equals(instance.getMethodType())) {
			urlField.setValue(instance.getUrl());
			openIpFields(false);
			clearIpFields();
			openUidFields(false);
			clearUidFields();
			openUrlFields(true);
		}

		setNotesField(instance.getNote());
		
//		setOriginalValues();
	}
	
	public void openIpFields(boolean open) {
		ipLoField.setReadOnly(!open);
		ipHiField.setReadOnly(!open);
//		ipLoOctet1Field.setReadOnly(!open);
//		ipLoOctet2Field.setReadOnly(!open);
//		ipLoOctet3Field.setReadOnly(!open);
//		ipLoOctet4Field.setReadOnly(!open);
//		ipHiOctet1Field.setReadOnly(!open);
//		ipHiOctet2Field.setReadOnly(!open);
//		ipHiOctet3Field.setReadOnly(!open);
//		ipHiOctet4Field.setReadOnly(!open);
		ipFieldSet.setEnabled(open);
		ipFieldSet.setExpanded(open);
	}
	
	public void clearIpFields() {
		ipLoField.clear();
		ipHiField.clear();
//		ipLoOctet1Field.clear();
//		ipLoOctet2Field.clear();
//		ipLoOctet3Field.clear();
//		ipLoOctet4Field.clear();
//		ipHiOctet1Field.clear();
//		ipHiOctet2Field.clear();
//		ipHiOctet3Field.clear();
//		ipHiOctet4Field.clear();
	}
	
	public void openUrlFields(boolean open) {
		urlField.setReadOnly(!open);
		urlFieldSet.setEnabled(open);
		urlFieldSet.setExpanded(open);
	}
	
	public void clearUrlFields() {
		urlField.clear();
	}
	
	public void openUidFields(boolean open) {
		userIdField.setReadOnly(!open);
		passwordField.setReadOnly(!open);
		uidTypeGroup.setReadOnly(!open);
		uidFieldSet.setEnabled(open);
		uidFieldSet.setExpanded(open);
	}
	
	public void clearUidFields() {
		userIdField.clear();
		passwordField.clear();
		cookieUidCheck.clear();
		permanentUidCheck.clear();
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
		openIpFields(true);
		openUidFields(true);
		openUrlFields(true);
	}

	
	@Override
	protected boolean isFormValidAndReady() {
		boolean ready = formPanel.isValid();
		
//		//	Check for required fields
//		if (institutionField.getSelectedValue() == null) { 
//			institutionField.markInvalid("Select an institution.");
//			ready = false;
//		} else
//			institutionField.clearInvalid();
//		
//		if (siteLocationField.getSelectedValue() == null) {
//			System.out.println(MSG_SITE_REQUIRED + " vs " + siteLocationField.getErrorMessage());
//			if (!MSG_SITE_REQUIRED.equals(siteLocationField.getErrorMessage()))
//				siteLocationField.markInvalid(MSG_SITE_REQUIRED);
//			ready = false;
//		} else
//			siteLocationField.clearInvalid();
		
		return ready;
	}

	@Override
	protected void executeLoader(int id,
			AsyncCallback<List<AuthMethodInstance>> callback) {
		authMethodListService.getAuthMethods(id, 0, 0, null, null, AppConstants.STATUS_DELETED,callback);
	}
	
	@Override
	public void adjustFormPanelSize(int width, int height) {
	//	super.adjustFormPanelSize(width, height);
		
		if (formRow1.isRendered()) {
			agreementIdField.setWidth( (formRow1.getWidth(true) - formRow1.getLabelWidth()) - 64);
		}
		
		if (formPanel != null && formPanel.isRendered())
			formPanel.layout(true);
	}

	@Override
	protected void addFormFields(FormPanel panel, FormData formData) {
		formData = new FormData("-24");
		
		TableLayout tableLayout = new TableLayout(2);
		tableLayout.setWidth("100%");
		panel.setLayout(tableLayout);

		formRow1	= getNewFormPanel(75); formRow1.setId("formRow1");
		formColumn1 = getNewFormPanel(75); formColumn1.setId("formColumn1"); //formColumn1.setLayoutData(0.5); //formColumn1.setWidth("50%");
		formColumn2 = getNewFormPanel(75); formColumn2.setId("formColumn2"); //formColumn2.setLayoutData(0.5); //formColumn2.setWidth("50%");
		formRow2	= getNewFormPanel(75); formRow2.setId("formRow2");
		
		TableData tData1 = new TableData();
		tData1.setWidth("50%");
		TableData tData2 = new TableData();
		tData2.setColspan(2);
		tData2.setWidth("100%");
		
		formRow1.setLayoutData(tData2);
		formRow2.setLayoutData(tData2);
		
		formRow1.setPadding(0);
		formRow2.setPadding(0);
		formColumn1.setPadding(0);
		formColumn2.setPadding(0);
		
		ucnDisplay.setToolTip(UiConstants.getQuickTip("The ucn for the site."));

		agreementIdField.setReadOnly(true);
		agreementIdField.setWidth(200);
		idNotesCombo.setSpacing(20);
		ucnDisplay.setReadOnly(true);
		
		FieldFactory.setStandard(ipLoField, "From");
		FieldFactory.setStandard(ipHiField, "To");
		
		//	Force all field widths to zero, so that they'll be computed based on the width of the enclosing form
		idNotesCombo.setWidth(0);
		
		notesField.setWidth(0);
		institutionField.setWidth(0);
		ucnDisplay.setWidth(0);
		siteLocationField.setWidth(0);

		
		idNotesCombo.add(agreementIdField);	
		idNotesCombo.add(notesField);
		
//		ipLoField.setSpacing(10);
//		ipLoOctet1Field.setWidth(30);
//		ipLoOctet2Field.setWidth(30);
//		ipLoOctet3Field.setWidth(30);
//		ipLoOctet4Field.setWidth(30);
//		
//		ipLoOctet1Field.setRegex(OCTET_REGEX);
//		ipLoOctet1Field.getMessages().setRegexText(OCTET_ERROR);
//		ipLoOctet2Field.setRegex(OCTET_REGEX);
//		ipLoOctet2Field.getMessages().setRegexText(OCTET_ERROR);
//		ipLoOctet3Field.setRegex(OCTET_REGEX);
//		ipLoOctet3Field.getMessages().setRegexText(OCTET_ERROR);
//		ipLoOctet4Field.setRegex(OCTET_REGEX);
//		ipLoOctet4Field.getMessages().setRegexText(OCTET_ERROR);
//		
//		ipLoField.add(ipLoOctet1Field);
//		ipLoField.add(ipLoOctet2Field);
//		ipLoField.add(ipLoOctet3Field);
//		ipLoField.add(ipLoOctet4Field);
//		
//		ipHiField.setSpacing(10);
//		ipHiOctet1Field.setWidth(30);
//		ipHiOctet2Field.setWidth(30);
//		ipHiOctet3Field.setWidth(30);
//		ipHiOctet4Field.setWidth(30);
//		
//		ipHiOctet1Field.setRegex(OCTET_REGEX);
//		ipHiOctet1Field.getMessages().setRegexText(OCTET_ERROR);
//		ipHiOctet2Field.setRegex(OCTET_REGEX);
//		ipHiOctet2Field.getMessages().setRegexText(OCTET_ERROR);
//		ipHiOctet3Field.setRegex(OCTET_REGEX);
//		ipHiOctet3Field.getMessages().setRegexText(OCTET_ERROR);
//		ipHiOctet4Field.setRegex(OCTET_REGEX);
//		ipHiOctet4Field.getMessages().setRegexText(OCTET_ERROR);
//		
//		ipHiField.add(ipHiOctet1Field);
//		ipHiField.add(ipHiOctet2Field);
//		ipHiField.add(ipHiOctet3Field);
//		ipHiField.add(ipHiOctet4Field);
		

		ipFieldSet.setBorders(true);
		ipFieldSet.setHeading("IP Address");
		ipFieldSet.setCollapsible(true);
//		linkFieldSet.setCheckboxToggle(true);
		FormLayout fLayout = new FormLayout();
		fLayout.setLabelWidth(60);
		ipFieldSet.setLayout(fLayout);
		ipFieldSet.setToolTip(UiConstants.getQuickTip("Define authentication by IP address."));
		
		ipFieldSet.add(ipLoField);
		ipFieldSet.add(ipHiField);

		uidFieldSet.setBorders(true);
		uidFieldSet.setHeading("User ID and Password");
		uidFieldSet.setCollapsible(true);
//		linkFieldSet.setCheckboxToggle(true);
		FormLayout fLayout2 = new FormLayout();
		fLayout2.setLabelWidth(60);
		uidFieldSet.setLayout(fLayout2);
		uidFieldSet.setToolTip(UiConstants.getQuickTip("Define authentication by User ID and password."));
		
		userIdField.setWidth(0);
		passwordField.setWidth(0);
		
		uidFieldSet.add(userIdField);
		uidFieldSet.add(passwordField);
		uidFieldSet.add(uidTypeGroup);
		

		urlFieldSet.setBorders(true);
		urlFieldSet.setHeading("ReferrerURL");
		urlFieldSet.setCollapsible(true);
//		linkFieldSet.setCheckboxToggle(true);
		FormLayout fLayout3 = new FormLayout();
		fLayout3.setLabelAlign(panel.getLabelAlign());
		fLayout3.setLabelWidth(60);
		urlFieldSet.setLayout(fLayout3);
		urlFieldSet.setToolTip(UiConstants.getQuickTip("Define authentication by a referrer URL."));

		urlField.setWidth(0);
		
		urlFieldSet.add(urlField, formData);
		urlFieldSet.setAutoWidth(true);
		
		formRow1.add(idNotesCombo);
		
		formColumn1.add(institutionField, formData);
		formColumn1.add(ucnDisplay, formData);
		
		formColumn2.add(siteLocationField, formData);
		formColumn2.add(statusGroup, formData);
		
		formRow2.add(ipFieldSet);
		formRow2.add(uidFieldSet);
		formRow2.add(urlFieldSet);

		panel.add(formRow1,		tData2);
		panel.add(formColumn1,	tData1);
		panel.add(formColumn2,	tData1);
		panel.add(formRow2,		tData2);
		
		panel.layout(true);
		
		addFieldSetListeners();
	}
	
	/**
	 * Add listeners to automatically close other field sets when one is expanded.
	 */
	protected void addFieldSetListeners() {
		
		//	Add listeners to collapse field sets in synch
		
		urlFieldSet.addListener(Events.BeforeExpand, new Listener<BaseEvent>() {
				@Override
				public void handleEvent(BaseEvent be) {
					if (be.getType().getEventCode() == Events.BeforeExpand.getEventCode()) {
						ipFieldSet.collapse();
						uidFieldSet.collapse();
					}
				}		
			});
		
		uidFieldSet.addListener(Events.BeforeExpand, new Listener<BaseEvent>() {
				@Override
				public void handleEvent(BaseEvent be) {
					if (be.getType().getEventCode() == Events.BeforeExpand.getEventCode()) {
						ipFieldSet.collapse();
						urlFieldSet.collapse();
					}
				}		
			});
		
		ipFieldSet.addListener(Events.BeforeExpand, new Listener<BaseEvent>() {
				@Override
				public void handleEvent(BaseEvent be) {
					if (be.getType().getEventCode() == Events.BeforeExpand.getEventCode()) {
						urlFieldSet.collapse();
						uidFieldSet.collapse();
					}
				}		
			});
	}
	
	protected SiteLocationSearchField getSiteLocationField(String name, String label, int width, String toolTip) {
		SiteLocationSearchField siteLocCombo = new SiteLocationSearchField();
		siteLocCombo.setIncludeAllOption(false);
		siteLocCombo.setIncludeMainOption(true);
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
//			addressDisplay.setValue("");
//			customerTypeDisplay.setValue("");
			siteLocationField.setFor(0, 0);
			return;
		}
		
//		if (focusInstance != null && !focusInstance.isNewRecord()) {
//			siteLocationField.setFor(instance.getUcn(), 1);
//			siteLocationField.setValue(SiteInstance.obtainModel(SiteInstance.getAllInstance(instance.getUcn(), 1)));
////			siteLocationField.setReadOnly(!authMethod.isNewRecord());
//		}
		
		if (focusInstance != null && focusInstance.getUcn() == instance.getUcn()) {
			// Same institution as instance, so use the instance UCN
			siteLocationField.setFor(focusInstance);
			siteLocationField.setValue(SiteInstance.obtainModel(focusInstance.getSite()));
		} else {
			// Different UCN, default to suffix 1
			siteLocationField.setFor(instance.getUcn(), 1);
			siteLocationField.setValue(SiteInstance.obtainModel(SiteInstance.getMainInstance(instance.getUcn(), 1)));
		}
		
		ucnDisplay.setValue(instance.getUcn() + "");
//		addressDisplay.setValue(instance.getHtmlAddress());
//		customerTypeDisplay.setValue(instance.getPublicPrivateDescription() + " / " + instance.getGroupDescription() + " &rArr; " + instance.getTypeDescription());
		
	}

	@Override
	protected void asyncUpdate() {
	
		// Set field values from form fields
		
		if (focusInstance == null) {
			focusInstance = new AuthMethodInstance();
			focusInstance.setNewRecord(true);
			focusInstance.setAgreementId(getAgreementId());
			InstitutionInstance institution = institutionField.getSelectedValue().getBean();
			if (institution == null) {
				MessageBox.alert("Unexpted Error", "No institution is selected for the site.", null);
				return;
			}
			focusInstance.setUcn(institution.getUcn());
			focusInstance.setUcnSuffix(1);
			SiteInstance site = siteLocationField.getSelectedValue().getBean();
			if (site == null) {
				MessageBox.alert("Unexpted Error", "No site location is selected for this agreement site.", null);
				return;
			}
			focusInstance.setSiteLocCode(site.getSiteLocCode());
		}
		
		if (focusInstance.isNewRecord())
			focusInstance.setNote(notesField.getNote());
		else
			focusInstance.setNote(null);	//	This will keep the note from being updated by this call
	
		//	Issue the asynchronous update request and plan on handling the response
		updateAuthMethodService.updateAuthMethod(focusInstance,
				new AsyncCallback<UpdateResponse<AuthMethodInstance>>() {
					public void onFailure(Throwable caught) {
						// Show the RPC error message to the user
						if (caught instanceof IllegalArgumentException)
							MessageBox.alert("Alert", caught.getMessage(), null);
						else {
							MessageBox.alert("Alert", "Agreement site  update failed unexpectedly.", null);
							System.out.println(caught.getClass().getName());
							System.out.println(caught.getMessage());
						}
						editButton.enable();
						newButton.enable();
					}

					public void onSuccess(UpdateResponse<AuthMethodInstance> updateResponse) {
						AuthMethodInstance updatedAuthMethod = (AuthMethodInstance) updateResponse.getInstance();
						if (updatedAuthMethod.isNewRecord()) {
							updatedAuthMethod.setNewRecord(false);
							grid.getStore().insert(AuthMethodInstance.obtainModel(updatedAuthMethod), 0);
						}
						
						focusInstance.setNewRecord(false);
						focusInstance.setValuesFrom(updatedAuthMethod);
						setFormFromInstance(updatedAuthMethod);	//	setFormFieldValues(updatedAuthMethod);
						
						//	This puts the grid in synch
						BeanModel gridModel = grid.getStore().findModel(focusInstance.getUniqueKey());
						if (gridModel != null) {
							AuthMethodInstance matchInstance = gridModel.getBean();
							matchInstance.setValuesFrom(focusInstance);
							grid.getStore().update(gridModel);
						}
						
						editButton.enable();
						newButton.enable();
				}
			});
	}

	protected void asyncUpdateNote(String note) {
	
		// Set field values from form fields
		
		if (focusInstance == null || focusInstance.isNewRecord()) {
			return;
		}
		
		focusInstance.setNote(note);
	
		//	Issue the asynchronous update request and plan on handling the response
		updateAuthMethodNoteService.updateAuthMethodNote(focusInstance,
				new AsyncCallback<UpdateResponse<AuthMethodInstance>>() {
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

					public void onSuccess(UpdateResponse<AuthMethodInstance> updateResponse) {
						AuthMethodInstance updatedAuthMethod = (AuthMethodInstance) updateResponse.getInstance();
						//	This makes sure the field and instance are in synch
						if (!notesField.getNote().equals(updatedAuthMethod.getNote())) {
							focusInstance.setNote(updatedAuthMethod.getNote());
							setNotesField(updatedAuthMethod.getNote());
						}
						//	This puts the grid in synch
						BeanModel gridModel = grid.getStore().findModel(focusInstance.getUniqueKey());
						if (gridModel != null) {
							AuthMethodInstance matchInstance = gridModel.getBean();
							matchInstance.setNote(focusInstance.getNote());
							grid.getStore().update(gridModel);
						}
						notesField.unlockNote();
				}
			});
	}
}
