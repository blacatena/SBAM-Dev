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
import com.scholastic.sbam.shared.objects.AuthMethodInstance;
import com.scholastic.sbam.shared.objects.GenericCodeInstance;
import com.scholastic.sbam.shared.objects.InstitutionInstance;
import com.scholastic.sbam.shared.objects.MethodIdInstance;
import com.scholastic.sbam.shared.objects.ProxyInstance;
import com.scholastic.sbam.shared.objects.SiteInstance;
import com.scholastic.sbam.shared.objects.UpdateResponse;
import com.scholastic.sbam.shared.util.AppConstants;

public class SiteLocationMethodsCard extends FormAndGridPanel<AuthMethodInstance> {
	
	public static final int DEFAULT_FIELD_WIDTH	=	0;	//250;
	
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
	
	protected SiteInstance			site;
	protected InstitutionInstance	siteInstitution;
	protected SiteInstance			saveSiteLocation;
	protected MethodIdInstance		methodId			=	MethodIdInstance.getEmptyInstance();
	
	protected RowExpander			noteExpander;

	protected MultiField<String>			idNotesCombo		= new MultiField<String>("Site");
	protected LabelField					siteDisplayField	= getLabelField();
	protected NotesIconButtonField<String>	notesField			= getNotesButtonField();
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
	
	public int getUcn() {
		return getFocusId();
	}
	
	public void setUcn(int ucn) {
		setFocusId(ucn);
	}
	
	public SiteInstance getSiteLocation() {
		return site;
	}

	public void setSiteLocation(SiteInstance site) {
		this.site = site;
		setUcn(site.getUcn());
	}

	public void setAuthMethod(AuthMethodInstance instance) {
		setFocusInstance(instance);
		if (instance.getSite() != null)
			setSiteLocation(instance.getSite());
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
//		gridStore.setKeyProvider(new SimpleKeyProvider("uniqueKey")); 	
	}

	@Override
	public void addGridColumns(List<ColumnConfig> columns) {

		columns.add(getDisplayColumn("methodDisplay",						"Method",					180));
		columns.add(getHiddenColumn("ipLoDisplay",							"Low IP",					90));
		columns.add(getHiddenColumn("ipHiDisplay",							"High IP",					90));
		columns.add(getHiddenColumn("userId",								"UID",						75));
		columns.add(getHiddenColumn("password",								"Password",					75));
		columns.add(getHiddenColumn("userType",								"",							30));
		columns.add(getHiddenColumn("url",									"URL",						180));
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
		if (instance.getUcnSuffix() <= 1)
			siteDisplayField.setValue(instance.getUcn() + " " + instance.getSiteLocCode() + " &nbsp;&nbsp;&nbsp;<i>" + displayStatus + "</i>");
		else
			siteDisplayField.setValue(instance.getUcn() + " - " + instance.getUcnSuffix() + " " + instance.getSiteLocCode() + " &nbsp;&nbsp;&nbsp;<i>" + displayStatus + "</i>");
		
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
		
		methodId.setUcn(site.getUcn());
		methodId.setUcnSuffix(site.getUcnSuffix());
		methodId.setSiteLocCode(site.getSiteLocCode());
		methodId.setForUcn(site.getUcn());
		methodId.setUcnSuffix(site.getUcnSuffix());
		methodId.setForSiteLocCode(site.getSiteLocCode());
		
		methodId.setMethodKey(-1);
		if (site.getUcnSuffix() <= 1)
			siteDisplayField.setValue(site.getUcn() + " " + site.getSiteLocCode() + " &nbsp;&nbsp;&nbsp;<i>New Method</i>");
		else
			siteDisplayField.setValue(site.getUcn() + " - " + site.getUcnSuffix() + " " + site.getSiteLocCode() + " &nbsp;&nbsp;&nbsp;<i>New Method</i>");
		

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
		authMethodListService.getAuthMethods(0, site.getUcn(), site.getUcnSuffix(), site.getSiteLocCode(), null, AppConstants.STATUS_DELETED,callback);
	}
	
	@Override
	public void adjustFormPanelSize(int width, int height) {
	//	super.adjustFormPanelSize(width, height);
		
		if (formRow1.isRendered()) {
			siteDisplayField.setWidth( (formRow1.getWidth(true) - formRow1.getLabelWidth()) - 64);
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

		siteDisplayField.setReadOnly(true);
		siteDisplayField.setWidth(200);
		idNotesCombo.setSpacing(20);
		urlField.setAllowBlank(false);
		
		FieldFactory.setStandard(ipRangeField, "");
		FieldFactory.setStandard(uidPasswordField, "");
		FieldFactory.setStandard(urlField, "URL");
		
		//	Force all field widths to zero, so that they'll be computed based on the width of the enclosing form
		idNotesCombo.setWidth(0);
		
		notesField.setWidth(0);
		proxyField.setWidth(0);
		
		idNotesCombo.add(siteDisplayField);	
		idNotesCombo.add(notesField);
		
		ipFieldSet.setId("IPfs");
		urlFieldSet.setId("URLfs");
		uidFieldSet.setId("UIDfs");
		
		if (methodId == null) methodId = MethodIdInstance.getEmptyInstance();
		if (site != null) {
			methodId.setUcn(site.getUcn());
			methodId.setUcnSuffix(site.getUcnSuffix());
			methodId.setSiteLocCode(site.getSiteLocCode());
		}
		
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
//		ipFieldSet.collapse();

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
			focusInstance.setUcn(site.getUcn());
			focusInstance.setUcnSuffix(site.getUcnSuffix());
			focusInstance.setSiteLocCode(site.getSiteLocCode());
		}

		focusInstance.setForUcn(focusInstance.getUcn());
		focusInstance.setForUcnSuffix(focusInstance.getUcnSuffix());
		focusInstance.setForSiteLocCode(focusInstance.getSiteLocCode());
		
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
							MessageBox.alert("Alert", "Site authentication method update failed unexpectedly.", null);
							System.out.println(caught.getClass().getName());
							System.out.println(caught.getMessage());
						}
						editButton.enable();
						newButton.enable();
					}

					public void onSuccess(UpdateResponse<AuthMethodInstance> updateResponse) {
						
						AuthMethodInstance updatedAuthMethod = (AuthMethodInstance) updateResponse.getInstance();
						if (updatedAuthMethod.getMethodTypeInstance() == null) {
							updatedAuthMethod.setMethodTypeInstance((GenericCodeInstance) UiConstants.getAuthMethodTypes().findModel(updatedAuthMethod.getMethodType()).getBean());
						}
						focusInstance.setValuesFrom(updatedAuthMethod);
						setFormFromInstance(updatedAuthMethod);	//	setFormFieldValues(updatedAuthMethod);
						
						if (updatedAuthMethod.isNewRecord()) {
							updatedAuthMethod.setNewRecord(false);
							grid.getStore().insert(AuthMethodInstance.obtainModel(updatedAuthMethod), 0);
						} else {
							//	This puts the grid in synch
							BeanModel gridModel = grid.getStore().findModel(focusInstance.getUniqueKey());
							if (gridModel != null) {
								AuthMethodInstance matchInstance = gridModel.getBean();
								matchInstance.setValuesFrom(focusInstance);
								grid.getStore().update(gridModel);
							}			
						}
						
						focusInstance.setNewRecord(false);	
						
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
							MessageBox.alert("Alert", "Site athentication method note update failed unexpectedly.", null);
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
		} else if (ipFieldSet != null){
			ipFieldSet.expand();
		}
	}
}
