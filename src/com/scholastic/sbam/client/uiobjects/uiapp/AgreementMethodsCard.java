package com.scholastic.sbam.client.uiobjects.uiapp;

import java.util.List;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.ToggleButton;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.CheckBoxGroup;
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
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.scholastic.sbam.client.services.AuthMethodListService;
import com.scholastic.sbam.client.services.AuthMethodListServiceAsync;
import com.scholastic.sbam.client.services.UpdateAuthMethodNoteService;
import com.scholastic.sbam.client.services.UpdateAuthMethodNoteServiceAsync;
import com.scholastic.sbam.client.services.UpdateAuthMethodService;
import com.scholastic.sbam.client.services.UpdateAuthMethodServiceAsync;
import com.scholastic.sbam.client.uiobjects.fields.AgreementSiteInstitutionSearchField;
import com.scholastic.sbam.client.uiobjects.fields.IpAddressRangeField;
import com.scholastic.sbam.client.uiobjects.fields.LockableFieldSet;
import com.scholastic.sbam.client.uiobjects.fields.NotesIconButtonField;
import com.scholastic.sbam.client.uiobjects.fields.ProxySearchField;
import com.scholastic.sbam.client.uiobjects.fields.SiteLocationSearchField;
import com.scholastic.sbam.client.uiobjects.fields.UrlField;
import com.scholastic.sbam.client.uiobjects.fields.UserIdPasswordField;
import com.scholastic.sbam.client.uiobjects.foundation.FieldFactory;
import com.scholastic.sbam.client.uiobjects.foundation.FormAndGridPanel;
import com.scholastic.sbam.client.uiobjects.foundation.FormInnerPanel;
import com.scholastic.sbam.client.util.IconSupplier;
import com.scholastic.sbam.client.util.UiConstants;
import com.scholastic.sbam.shared.objects.AgreementInstance;
import com.scholastic.sbam.shared.objects.AuthMethodInstance;
import com.scholastic.sbam.shared.objects.InstitutionInstance;
import com.scholastic.sbam.shared.objects.MethodIdInstance;
import com.scholastic.sbam.shared.objects.ProxyInstance;
import com.scholastic.sbam.shared.objects.SimpleKeyProvider;
import com.scholastic.sbam.shared.objects.SiteInstance;
import com.scholastic.sbam.shared.objects.UpdateResponse;
import com.scholastic.sbam.shared.util.AppConstants;

public class AgreementMethodsCard extends FormAndGridPanel<AuthMethodInstance> {
	
	private static final int DEFAULT_FIELD_WIDTH	=	0;	//250;
	
//	private static final String MSG_SITE_REQUIRED = "Select a site location.";
	
	protected final AuthMethodListServiceAsync 			authMethodListService 		= GWT.create(AuthMethodListService.class);
	protected final UpdateAuthMethodServiceAsync		updateAuthMethodService		= GWT.create(UpdateAuthMethodService.class);
	protected final UpdateAuthMethodNoteServiceAsync	updateAuthMethodNoteService	= GWT.create(UpdateAuthMethodNoteService.class);

	protected FormInnerPanel				formRow1;
	protected FormInnerPanel				formColumn1;
	protected FormInnerPanel				formColumn2;
	protected FormInnerPanel				formRow2;
	
	ToggleButton allButton;
	ToggleButton ipButton;
	ToggleButton uidButton;
	ToggleButton urlButton;
	
	protected AgreementInstance		agreement;
	protected InstitutionInstance	siteInstitution;
	protected SiteInstance			saveSiteLocation;
	protected MethodIdInstance		methodId			=	MethodIdInstance.getEmptyInstance();
	
	protected RowExpander			noteExpander;

	protected MultiField<String>			idNotesCombo		= new MultiField<String>("Agreement #");
	protected LabelField					agreementIdField	= getLabelField();
	protected NotesIconButtonField<String>	notesField			= getNotesButtonField();
	protected AgreementSiteInstitutionSearchField		institutionField	= getInstitutionField("ucn", "Site", DEFAULT_FIELD_WIDTH, "The institution that will receive the product services through this authentication method.");
	protected SiteLocationSearchField		siteLocationField	= getSiteLocationField("uniqueKey", "Site Location", DEFAULT_FIELD_WIDTH, "The specific location at the customer site targeted by this authentication method.");
	protected TextField<String>				ucnDisplay			= getTextField("UCN+");
	protected CheckBox						approvedCheck		= getCheckBoxField("Approved");
	protected CheckBox						validatedCheck		= getCheckBoxField("Validated");
	protected CheckBox						remoteCheck			= getCheckBoxField("Remote");
	protected CheckBoxGroup					statusGroup			= getCheckBoxGroup(null, approvedCheck, validatedCheck, remoteCheck);
	
	protected IpAddressRangeField			ipRangeField		= new IpAddressRangeField();
	protected UserIdPasswordField			uidPasswordField	= new UserIdPasswordField();
	protected UrlField						urlField			= new UrlField();
	
	protected ProxySearchField				proxyField			= getProxySearchField("proxyId", "Proxy", 350, "Any proxy attached to this user ID.");

	protected LockableFieldSet				ipFieldSet		= new LockableFieldSet() 
//																{
//																	@Override
//																	public void onExpand() {
//																		super.onExpand();
//																		uidFieldSet.collapse();
//																		urlFieldSet.collapse();
//																	}
//																}
																;
	protected LockableFieldSet				uidFieldSet		= new LockableFieldSet() 
//																{
//																	@Override
//																	public void onExpand() {
//																		super.onExpand();
//																		ipFieldSet.collapse();
//																		urlFieldSet.collapse();
//																	}
//																}
																;
	protected LockableFieldSet				urlFieldSet		= new LockableFieldSet() 
																{
																	@Override
																	public void onExpand() {
																		super.onExpand();
																		formPanel.layout(true);	// Needed because url doesn't behave properly
																	}
																}
																;
	
	
//	protected EnhancedComboBox<BeanModel>	cancelReasonField	= getComboField("cancelReason", 	"Cancel",	DEFAULT_FIELD_WIDTH,		
//			"The reason for canceling (deactivating) for this site.",
//			UiConstants.getCancelReasons(), "cancelReasonCode", "descriptionAndCode");
	
	public int getAgreementId() {
		return getFocusId();
	}
	
	public void setAgreementId(int agreementId) {
		setFocusId(agreementId);
		if (siteLocationField != null)
			siteLocationField.setAgreementId(agreementId);
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
	
	@Override
	public String getFormHeading() {
		return "Methods";
	}
	
	@Override
	public void moreRendering() {
		addGridSelectButtons(gridPanel);
	}
	
	@Override
	public void addGridPlugins(Grid<BeanModel> grid) {
		grid.addPlugin(noteExpander);
	}
	
	/**
	 * Override to set any further grid attributes, such as the autoExpandColumn.
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
		columns.add(getHiddenColumn("ipLoDisplay",							"Low IP",					90));
		columns.add(getHiddenColumn("ipHiDisplay",							"High IP",					90));
		columns.add(getHiddenColumn("userId",								"UID",						75));
		columns.add(getHiddenColumn("password",								"Password",					75));
		columns.add(getHiddenColumn("userType",								"",							30));
		columns.add(getHiddenColumn("url",									"URL",						180));
		columns.add(getDisplayColumn("site.institution.institutionName",	"Institution",				180,
					"This is the institution name."));
		columns.add(getDisplayColumn("site.description",					"Location",					100,
					"This is the description of the location at the site."));
		columns.add(getHiddenColumn("site.siteLocCode",						"Code",						40,
					"This is the code for the location at the site."));
//		columns.add(getDisplayColumn("methodType",							"Type",						40,
//				"The type of authentication."));
		columns.add(getDisplayColumn("methodTypeInstance",					"Type",						40,
					"The type of authentication.", UiConstants.getAuthMethodTypes()));
	
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
		
		if (instance.getForUcnSuffix() <= 1)
			ucnDisplay.setValue(instance.getForUcn() + "");
		else
			ucnDisplay.setValue(instance.getForUcn() + " - " + instance.getForUcnSuffix());
		
		institutionField.setAgreementId(getAgreementId());
		
		set(instance.getSite().getInstitution());
//		institutionField.setReadOnly(!instance.isNewRecord());
		
		siteLocationField.setFor(instance);
		siteLocationField.setValue(SiteInstance.obtainModel(instance.getSite()));
//		siteLocationField.setReadOnly(!instance.isNewRecord());
		
		validatedCheck.setValue(instance.isValidated());
		approvedCheck.setValue(instance.isApproved());
		remoteCheck.setValue(instance.isRemote());
		
		methodId.setFrom(instance.obtainMethodId());
		
		if (AuthMethodInstance.AM_IP.equals(instance.getMethodType())) {
			ipRangeField.setValue(instance.getIpLo(), instance.getIpHi());
			proxyField.setValue(ProxyInstance.obtainModel(ProxyInstance.getEmptyInstance()));
			openUrlFields(false);
			clearUrlFields();
			openUidFields(false);
			clearUidFields();
			openIpFields(true);
		} else if (AuthMethodInstance.AM_UID.equals(instance.getMethodType())) {
			uidPasswordField.setValue(instance.getUserId(), instance.getPassword(), instance.getUserType());
			proxyField.setValue(ProxyInstance.obtainModel(instance.getProxy()));
			openIpFields(false);
			clearIpFields();
			openUrlFields(false);
			clearUrlFields();
			openUidFields(true);
		} else if (AuthMethodInstance.AM_URL.equals(instance.getMethodType())) {
			urlField.setValue(instance.getUrl());
			proxyField.setValue(ProxyInstance.obtainModel(ProxyInstance.getEmptyInstance()));
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
		ipRangeField.setReadOnly(!open);
	//	ipFieldSet.markLocked(!open);
		ipFieldSet.setExpanded(open);
		ipFieldSet.enableFields(false);
		ipFieldSet.setEnabled(open);
	}
	
	public void clearIpFields() {
		ipRangeField.clear();
	}
	
	public void openUrlFields(boolean open) {
		urlField.setReadOnly(!open);
	//	urlFieldSet.markLocked(!open);
		urlFieldSet.setExpanded(open);
		urlFieldSet.enableFields(false);
		urlFieldSet.setEnabled(open);
	}
	
	public void clearUrlFields() {
		urlField.clear();
	}
	
	public void openUidFields(boolean open) {
		uidPasswordField.setReadOnly(!open);
		proxyField.setReadOnly(!open);
	//	uidFieldSet.markLocked(!open);
		uidFieldSet.setExpanded(open);
		uidFieldSet.enableFields(false);
		uidFieldSet.setEnabled(open);
	}
	
	public void clearUidFields() {
		uidPasswordField.clear();
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
		openIpFields(true);
		openUidFields(true);
		openUrlFields(true);
		super.handleNew();
		
		//	This is for/shared by the ip, uid and url fields that do async validation based on the method ID
		methodId.setFrom(MethodIdInstance.getEmptyInstance());
		methodId.setAgreementId(getAgreementId());
		methodId.setMethodKey(-1);
		
		institutionField.setAgreementId(getAgreementId());
		agreementIdField.setValue(AppConstants.appendCheckDigit(agreement.getId()) + " &nbsp;&nbsp;&nbsp;<i>New Method</i>");
		//	Originally, these couldn't be changed on a method because they were part of the key, but now they can
//		institutionField.setReadOnly(false);
//		siteLocationField.setReadOnly(false);
		if (uidButton != null && uidButton.isPressed())
			uidFieldSet.expand();
		else if (urlButton != null && urlButton.isPressed())
			urlFieldSet.expand();
		else
			ipFieldSet.expand();
	}

	
	@Override
	protected boolean isFormValidAndReady() {
		boolean ready = formPanel.isValid();
		
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

		formRow1	= getNewFormInnerPanel(75); formRow1.setId("formRow1");
		formColumn1 = getNewFormInnerPanel(75); formColumn1.setId("formColumn1"); //formColumn1.setLayoutData(0.5); //formColumn1.setWidth("50%");
		formColumn2 = getNewFormInnerPanel(75); formColumn2.setId("formColumn2"); //formColumn2.setLayoutData(0.5); //formColumn2.setWidth("50%");
		formRow2	= getNewFormInnerPanel(75); formRow2.setId("formRow2");
		
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
		urlField.setAllowBlank(false);
		
		if (agreement != null)
			siteLocationField.setAgreementId(agreement.getId());
		
		FieldFactory.setStandard(ipRangeField, "");
		FieldFactory.setStandard(uidPasswordField, "");
		FieldFactory.setStandard(urlField, "URL");
		
		//	Force all field widths to zero, so that they'll be computed based on the width of the enclosing form
		idNotesCombo.setWidth(0);
		
		notesField.setWidth(0);
		institutionField.setWidth(0);
		ucnDisplay.setWidth(0);
		siteLocationField.setWidth(0);
		proxyField.setWidth(0);
		
		idNotesCombo.add(agreementIdField);	
		idNotesCombo.add(notesField);
		
		ipFieldSet.setId("IPfs");
		urlFieldSet.setId("URLfs");
		uidFieldSet.setId("UIDfs");
		
		if (methodId == null) methodId = MethodIdInstance.getEmptyInstance();
		methodId.setAgreementId(getAgreementId());
		
		ipRangeField.setMethodId(methodId);
		urlField.setMethodId(methodId);
		uidPasswordField.setMethodId(methodId);
		uidPasswordField.setProxyField(proxyField);

		ipFieldSet.setBorders(true);
		ipFieldSet.setHeading("IP Address");
		ipFieldSet.setCollapsible(true);
		FormLayout fLayout = new FormLayout();
		fLayout.setLabelWidth(0);
		ipFieldSet.setLayout(fLayout);
		ipFieldSet.setToolTip(UiConstants.getQuickTip("Define authentication by IP address."));
		
		ipFieldSet.add(ipRangeField);
		ipFieldSet.collapse();

		uidFieldSet.setBorders(true);
		uidFieldSet.setHeading("User ID and Password");
		uidFieldSet.setCollapsible(true);
		FormLayout fLayout2 = new FormLayout();
		fLayout2.setLabelWidth(60);
		uidFieldSet.setLayout(fLayout2);
		uidFieldSet.setToolTip(UiConstants.getQuickTip("Define authentication by User ID and password."));
		
		uidFieldSet.add(uidPasswordField);
		uidFieldSet.add(proxyField, formData);
		uidFieldSet.collapse();
		

		urlFieldSet.setBorders(true);
		urlFieldSet.setHeading("ReferrerURL");
		urlFieldSet.setCollapsible(true);
		FormLayout fLayout3 = new FormLayout();
		fLayout3.setLabelAlign(panel.getLabelAlign());
		fLayout3.setLabelWidth(60);
		urlFieldSet.setLayout(fLayout3);
		urlFieldSet.setToolTip(UiConstants.getQuickTip("Define authentication by a referrer URL."));

		urlField.setWidth(0);
		
		urlFieldSet.add(urlField, formData);
		urlFieldSet.setAutoWidth(true);
		urlFieldSet.collapse();
		
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
						urlFieldSet.enable();
					}
				}		
			});
		
		uidFieldSet.addListener(Events.BeforeExpand, new Listener<BaseEvent>() {
				@Override
				public void handleEvent(BaseEvent be) {
					if (be.getType().getEventCode() == Events.BeforeExpand.getEventCode()) {
						ipFieldSet.collapse();
						urlFieldSet.collapse();
						uidFieldSet.enable();
					}
				}		
			});
		
		ipFieldSet.addListener(Events.BeforeExpand, new Listener<BaseEvent>() {
				@Override
				public void handleEvent(BaseEvent be) {
					if (be.getType().getEventCode() == Events.BeforeExpand.getEventCode()) {
						urlFieldSet.collapse();
						uidFieldSet.collapse();
						ipFieldSet.enable();
					}
				}		
			});
	}
	
	protected SiteLocationSearchField getSiteLocationField(String name, String label, int width, String toolTip) {
		final SiteLocationSearchField siteLocCombo = new SiteLocationSearchField(this);
		siteLocCombo.setIncludeAllOption(false);
		siteLocCombo.setIncludeMainOption(true);
		FieldFactory.setStandard(siteLocCombo, label);
		
		if (toolTip != null)
			siteLocCombo.setToolTip(toolTip);
		if (width > 0)
			siteLocCombo.setWidth(width);
		siteLocCombo.setDisplayField("descriptionAndCode");
		
		//	Add a listener to tell the asynchronous validate fields which site location to use.
		siteLocCombo.addSelectionChangedListener(new SelectionChangedListener<BeanModel>() {

			@Override
			public void selectionChanged(SelectionChangedEvent<BeanModel> se) {
				//	Since all IP/uid/url fields share the same method ID instance, this only has to be done once
				if (se.getSelectedItem() == null) {
					methodId.setForSiteLocCode(null);
				} else
					methodId.setForSiteLocCode(se.getSelectedItem().get("siteLocCode").toString());
			}
			
		});
		
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
		nibf.setEmptyNoteText("Click the note icon to add notes for this method.");
		return nibf;
	}

	
	protected ProxySearchField getProxySearchField(String name, String label, int width, String toolTip) {
		ProxySearchField proxyCombo = new ProxySearchField(this);
		FieldFactory.setStandard(proxyCombo, label);
		proxyCombo.setAllowBlank(true);
		
		if (toolTip != null)
			proxyCombo.setToolTip(toolTip);
		if (width >= 0)
			proxyCombo.setWidth(width);
		proxyCombo.setDisplayField("descriptionAndId");
		
//		proxyCombo.addSelectionChangedListener(new SelectionChangedListener<BeanModel>() {
//
//			@Override
//			public void selectionChanged(SelectionChangedEvent<BeanModel> se) {
//				selectProxy(se.getSelectedItem());
//			}
//			
//		});
		
		return proxyCombo;
	}

	
	protected AgreementSiteInstitutionSearchField getInstitutionField(String name, String label, int width, String toolTip) {
		AgreementSiteInstitutionSearchField instCombo = new AgreementSiteInstitutionSearchField();
		FieldFactory.setStandard(instCombo, label);
		instCombo.setAllowBlank(true);
		instCombo.setAgreementId(getAgreementId());
		
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
	
	/**
	 * Match the form field values to reflect a selected institution.
	 * @param institution
	 */
	protected void matchToInstitution(InstitutionInstance institution) {
		
		if (institution == null || institution.getUcn() == 0) {
			ucnDisplay.setValue("");
			siteLocationField.setFor(0, 0, "");
			setMethodIdFor(0, 0, "");
			return;
		}
		
		if (focusInstance != null && focusInstance.getForUcn() == institution.getUcn()) {
			// Same institution as instance, so use the instance UCN
			siteLocationField.setFor(focusInstance, institution.getInstitutionName());
			siteLocationField.setValue(SiteInstance.obtainModel(focusInstance.getSite()));
			setMethodIdFor(focusInstance.getSite().getUcn(), focusInstance.getSite().getUcnSuffix(), focusInstance.getSite().getSiteLocCode());
		} else {
			// Different UCN, default to suffix 1
			siteLocationField.setFor(institution.getUcn(), 1, institution.getInstitutionName());
			SiteInstance newMain = SiteInstance.getMainInstance(institution.getUcn(), 1);
			siteLocationField.setValue(SiteInstance.obtainModel(newMain));
			setMethodIdFor(newMain.getUcn(), newMain.getUcnSuffix(), newMain.getSiteLocCode());
		}
		
		ucnDisplay.setValue(institution.getUcn() + "");
		
	}
	
	protected void setMethodIdFor(int forUcn, int forUcnSuffix, String forSiteLocCode) {
		// Since the IP/uid/url fields all share the same method ID instance, this only has to be done for one of them
		methodId.setForUcn(forUcn);
		methodId.setForUcnSuffix(forUcnSuffix);
		methodId.setForSiteLocCode(forSiteLocCode);
	}

	/**
	 * Perform an update to the database using the current form field values.
	 */
	@Override
	protected void asyncUpdate() {
	
		// Set field values from form fields
		
		if (focusInstance == null) {
			focusInstance = new AuthMethodInstance();
			focusInstance.setNewRecord(true);
			focusInstance.setAgreementId(getAgreementId());
			focusInstance.setUcn(0);
			focusInstance.setUcnSuffix(0);
			focusInstance.setSiteLocCode("");
		}
		
		InstitutionInstance institution = (institutionField.getSelectedValue() == null) ? null : (InstitutionInstance) institutionField.getSelectedValue().getBean();
		if (institution == null) {
			focusInstance.setForUcn(0);
			focusInstance.setForUcnSuffix(0);
			focusInstance.setForSiteLocCode("");
		} else {
			if (focusInstance.getForUcn() != institution.getUcn()) {
				focusInstance.setForUcn(institution.getUcn());
				focusInstance.setForUcnSuffix(1);
			}
			SiteInstance site = (siteLocationField.getSelectedValue() == null) ? null : (SiteInstance) siteLocationField.getSelectedValue().getBean();
			if (site == null) {
				MessageBox.alert("Unexpected Error", "No site location is selected for this authentication method.", null);
				return;
			}
			focusInstance.setForSiteLocCode(site.getSiteLocCode());
		}
		
		if (focusInstance.isNewRecord()) {
			if (ipFieldSet.isExpanded()) {
				focusInstance.setIpLo(ipRangeField.getLowValue());
				focusInstance.setIpHi(ipRangeField.getHighValue());
			} else if (uidFieldSet.isExpanded()) {
				focusInstance.setUserId(uidPasswordField.getUserId());
				focusInstance.setPassword(uidPasswordField.getPassword());
				focusInstance.setUserType(uidPasswordField.getUserType());
			} else if (urlFieldSet.isExpanded()) {
				focusInstance.setUrl(urlField.getValue());
			} else {
				MessageBox.alert("Unexpected Error", "No authentication data has been selected.", null);
				return;
			}
		} else {

			if (AuthMethodInstance.AM_IP.equals(focusInstance.getMethodType())) {
				focusInstance.setIpLo(ipRangeField.getLowValue());
				focusInstance.setIpHi(ipRangeField.getHighValue());
			} else if (AuthMethodInstance.AM_UID.equals(focusInstance.getMethodType())) {
				focusInstance.setUserId(uidPasswordField.getUserId());
				focusInstance.setPassword(uidPasswordField.getPassword());
				focusInstance.setUserType(uidPasswordField.getUserType());
				focusInstance.setProxy(proxyField.getSelectedProxy());
			} else if (AuthMethodInstance.AM_URL.equals(focusInstance.getMethodType())) {
				focusInstance.setUrl(urlField.getValue());
			} else {
				MessageBox.alert("Unexpected Error", "Invalid authentication method type " + focusInstance.getMethodType() + ".", null);
				return;
			}
		}
		
		focusInstance.setRemote(remoteCheck.getValue()?'y':'n');
		focusInstance.setApproved(approvedCheck.getValue()?'y':'n');
		focusInstance.setValidated(validatedCheck.getValue()?'y':'n');
		
		if (focusInstance.isNewRecord()) {
			focusInstance.setNote(notesField.getNote());
			focusInstance.syncMethodType();
		} else
			focusInstance.setNote(null);	//	This will keep the note from being updated by this call
	
		//	Issue the asynchronous update request and plan on handling the response
		updateAuthMethodService.updateAuthMethod(focusInstance,
				new AsyncCallback<UpdateResponse<AuthMethodInstance>>() {
					public void onFailure(Throwable caught) {
						// Show the RPC error message to the user
						if (caught instanceof IllegalArgumentException)
							MessageBox.alert("Alert", caught.getMessage(), null);
						else {
							MessageBox.alert("Alert", "Agreement authentication method update failed unexpectedly.", null);
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
							MessageBox.alert("Alert", "Agreement athentication method note update failed unexpectedly.", null);
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
	
	protected void setAllView() {
		gridStore.filter("methodType", "");
		setHideGeneralColumns(false);
		setHideIpColumns(true);
		setHideUidColumns(true);
		setHideUrlColumns(true);
		grid.setAutoExpandColumn("methodDisplay");
		grid.getView().refresh(true);
		layout(true);
	}
	
	protected void setIpView() {
		gridStore.filter("methodType", AuthMethodInstance.AM_IP);
		setHideGeneralColumns(true);
		setHideIpColumns(false);
		setHideUidColumns(true);
		setHideUrlColumns(true);
		grid.setAutoExpandColumn("site.institution.institutionName");
		grid.getView().refresh(true);
		layout(true);
	}
	
	protected void setUrlView() {
		gridStore.filter("methodType", AuthMethodInstance.AM_URL);
		setHideGeneralColumns(true);
		setHideIpColumns(true);
		setHideUidColumns(true);
		setHideUrlColumns(false);
		grid.setAutoExpandColumn("site.institution.institutionName");
		grid.getView().refresh(true);
		layout(true);
	}
	
	protected void setUidView() {
		gridStore.filter("methodType", AuthMethodInstance.AM_UID);
		setHideGeneralColumns(true);
		setHideIpColumns(true);
		setHideUidColumns(false);
		setHideUrlColumns(true);
		grid.setAutoExpandColumn("site.institution.institutionName");
		grid.getView().refresh(true);
		layout(true);
	}
	
	protected void setHideGeneralColumns(boolean hide) {
		grid.getColumnModel().getColumnById("methodDisplay").setHidden(hide);
	}
	
	protected void setHideIpColumns(boolean hide) {
		grid.getColumnModel().getColumnById("ipLoDisplay").setHidden(hide);
		grid.getColumnModel().getColumnById("ipHiDisplay").setHidden(hide);
	}
	
	protected void setHideUidColumns(boolean hide) {
		grid.getColumnModel().getColumnById("userId").setHidden(hide);
		grid.getColumnModel().getColumnById("password").setHidden(hide);
		grid.getColumnModel().getColumnById("userType").setHidden(hide);
	}
	
	protected void setHideUrlColumns(boolean hide) {
		grid.getColumnModel().getColumnById("url").setHidden(hide);
	}
	
	protected void addGridSelectButtons(ContentPanel targetPanel) {
		
		String toggleGroup = "av" + System.currentTimeMillis();
		
		ToolBar viewToolBar = new ToolBar();
		viewToolBar.setAlignment(HorizontalAlignment.CENTER);
		viewToolBar.setBorders(false);
		viewToolBar.setSpacing(20);
		viewToolBar.setMinButtonWidth(60);
//		toolBar.addStyleName("clear-toolbar");

		allButton = new ToggleButton("All");
		IconSupplier.forceIcon(allButton, IconSupplier.getViewAccessIconName());
		allButton.enable();
		allButton.toggle(true);
		allButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				allButton.toggle(true);
				setAllView();
			}  
		});
		allButton.setToggleGroup(toggleGroup);
		viewToolBar.add(allButton);

		ipButton = new ToggleButton("IP Address");
		IconSupplier.forceIcon(ipButton, IconSupplier.getViewAccessIconName());
		ipButton.enable();
		ipButton.toggle(false);
		ipButton.addSelectionListener(new SelectionListener<ButtonEvent>() {  
			@Override
			public void componentSelected(ButtonEvent ce) {
				ipButton.toggle(true);
				setIpView();
			}  
		});
		ipButton.setToggleGroup(toggleGroup);
		viewToolBar.add(ipButton);

		uidButton = new ToggleButton("User ID");
		IconSupplier.forceIcon(uidButton, IconSupplier.getViewAccessIconName());
		uidButton.enable();
		uidButton.toggle(false);
		uidButton.addSelectionListener(new SelectionListener<ButtonEvent>() {  
			@Override
			public void componentSelected(ButtonEvent ce) {
				uidButton.toggle(true);
				setUidView();
			}  
		});
		uidButton.setToggleGroup(toggleGroup);
		viewToolBar.add(uidButton);

		urlButton = new ToggleButton("URL");
		IconSupplier.forceIcon(urlButton, IconSupplier.getViewAccessIconName());
		urlButton.enable();
		uidButton.toggle(false);
		urlButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				urlButton.toggle(true);
				setUrlView();
			}  
		});
		urlButton.setToggleGroup(toggleGroup);
		viewToolBar.add(urlButton);

		
		targetPanel.setTopComponent(viewToolBar);
	}
	
	@Override
	public void setFocusInstance(AuthMethodInstance instance) {
		super.setFocusInstance(instance);
		openFocusFieldSet();
	}
	
	@Override
	public void afterRender() {
		super.afterRender();
		//	We need to do this for a focus instance, because the portlet may not have been rendered before
		openFocusFieldSet();
	}
	
	public void openFocusFieldSet() {
		if (focusInstance == null)
			return;

		if (focusInstance.methodIsIpAddress() && ipFieldSet != null) {
			ipFieldSet.expand();
		} else if (focusInstance.methodIsUserId() && uidFieldSet != null) {
			uidFieldSet.expand();
		} else if (focusInstance.methodIsUrl() && urlFieldSet != null) {
			urlFieldSet.expand();
		}
	}
}
