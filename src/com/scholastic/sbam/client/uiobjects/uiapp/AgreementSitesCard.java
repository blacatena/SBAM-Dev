package com.scholastic.sbam.client.uiobjects.uiapp;

import java.util.List;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.data.BaseFilterPagingLoadConfig;
import com.extjs.gxt.ui.client.data.BasePagingLoader;
import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.BeanModelReader;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoader;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.MultiField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.LiveGridView;
import com.extjs.gxt.ui.client.widget.grid.RowExpander;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.extjs.gxt.ui.client.widget.toolbar.LiveToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.scholastic.sbam.client.services.AgreementSiteListService;
import com.scholastic.sbam.client.services.AgreementSiteListServiceAsync;
import com.scholastic.sbam.client.services.UpdateAgreementSiteNoteService;
import com.scholastic.sbam.client.services.UpdateAgreementSiteNoteServiceAsync;
import com.scholastic.sbam.client.services.UpdateAgreementSiteService;
import com.scholastic.sbam.client.services.UpdateAgreementSiteServiceAsync;
import com.scholastic.sbam.client.services.UpdateUserCacheService;
import com.scholastic.sbam.client.services.UpdateUserCacheServiceAsync;
import com.scholastic.sbam.client.uiobjects.events.AppEvent;
import com.scholastic.sbam.client.uiobjects.events.AppEventBus;
import com.scholastic.sbam.client.uiobjects.events.AppEvents;
import com.scholastic.sbam.client.uiobjects.fields.EnhancedComboBox;
import com.scholastic.sbam.client.uiobjects.fields.InstitutionSearchField;
import com.scholastic.sbam.client.uiobjects.fields.NotesIconButtonField;
import com.scholastic.sbam.client.uiobjects.fields.SiteLocationSearchField;
import com.scholastic.sbam.client.uiobjects.foundation.FieldFactory;
import com.scholastic.sbam.client.uiobjects.foundation.FormAndGridPanel;
import com.scholastic.sbam.client.uiobjects.foundation.FormInnerPanel;
import com.scholastic.sbam.client.util.UiConstants;
import com.scholastic.sbam.shared.exceptions.ServiceNotReadyException;
import com.scholastic.sbam.shared.objects.AgreementSiteInstance;
import com.scholastic.sbam.shared.objects.CancelReasonInstance;
import com.scholastic.sbam.shared.objects.CommissionTypeInstance;
import com.scholastic.sbam.shared.objects.InstitutionInstance;
import com.scholastic.sbam.shared.objects.SimpleKeyProvider;
import com.scholastic.sbam.shared.objects.SiteInstance;
import com.scholastic.sbam.shared.objects.SynchronizedPagingLoadResult;
import com.scholastic.sbam.shared.objects.UpdateResponse;
import com.scholastic.sbam.shared.objects.UserCacheTarget;
import com.scholastic.sbam.shared.util.AppConstants;

public class AgreementSitesCard extends FormAndGridPanel<AgreementSiteInstance> {
	
	private static final int DEFAULT_FIELD_WIDTH	=	0;	//250;
	private static final int SITE_LOAD_LIMIT		=	50;
	
	protected final AgreementSiteListServiceAsync 		agreementSiteListService 		= GWT.create(AgreementSiteListService.class);
	protected final UpdateAgreementSiteServiceAsync		updateAgreementSiteService		= GWT.create(UpdateAgreementSiteService.class);
	protected final UpdateAgreementSiteNoteServiceAsync	updateAgreementSiteNoteService	= GWT.create(UpdateAgreementSiteNoteService.class);
	protected final UpdateUserCacheServiceAsync 		userCacheUpdateService			= GWT.create(UpdateUserCacheService.class);
	
	protected long							searchSyncId;
	protected LiveGridView					liveView;
	protected PagingLoader<PagingLoadResult<AgreementSiteInstance>> siteLoader;
	
	protected FormInnerPanel				formColumn1;
	protected FormInnerPanel				formColumn2;
	protected FormInnerPanel				formRow2;
	
	protected AppPortletProvider	appPortletProvider;
	
	protected InstitutionInstance	siteInstitution;
	
	protected RowExpander			noteExpander;

	protected MultiField<String>			idNotesCombo		= new MultiField<String>("Agreement #");
	protected LabelField					agreementIdField	= getLabelField();
	protected NotesIconButtonField<String>	notesField			= getNotesButtonField();
	protected InstitutionSearchField		institutionField	= getInstitutionField("ucn", "Institution", DEFAULT_FIELD_WIDTH, "The institution that will receive the product services.");
	protected LabelField					addressDisplay		= getLabelField();
	protected TextField<String>				ucnDisplay			= getTextField("UCN+");
	protected LabelField					customerTypeDisplay	= getLabelField();
	protected SiteLocationSearchField		siteLocationField	= getSiteLocationField("uniqueKey", "Site Location", DEFAULT_FIELD_WIDTH, "The specific location at the customer site.");
	
	protected EnhancedComboBox<BeanModel>	commissionTypeField	= getComboField("commissionType", 	"Commission",	DEFAULT_FIELD_WIDTH,		
			"The commission code assigned to this site for reporting purposes.",	
			UiConstants.getCommissionTypes(UiConstants.CommissionTypeTargets.SITE), "commissionCode", "descriptionAndCode");
	protected EnhancedComboBox<BeanModel>	cancelReasonField	= getComboField("cancelReason", 	"Cancel",	DEFAULT_FIELD_WIDTH,		
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

	public AppPortletProvider getAppPortletProvider() {
		return appPortletProvider;
	}

	public void setAppPortletProvider(AppPortletProvider appPortletProvider) {
		this.appPortletProvider = appPortletProvider;
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
	public boolean areGridFiltersLocal() {
		return false;
	}
	
	/**
	 * This method is overridden to set a loader for the store, to use live grid view
	 */
	@Override
	public ListStore<BeanModel> getNewGridStore() {
		
		siteLoader = getSiteLoader(); 

		siteLoader.setLimit(SITE_LOAD_LIMIT);
//		siteLoader.setSortDir(SortDir.ASC);  
//		siteLoader.setSortField("site.institution.institutionName");  

		siteLoader.setRemoteSort(true);
		
		return new ListStore<BeanModel>(siteLoader);
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
		
		//	Basic stuff
		
		grid.setAutoExpandColumn("site.institution.institutionName"); 
		gridStore.setKeyProvider(new SimpleKeyProvider("uniqueKey"));
		gridStore.setStoreSorter(null);
		
		//	Live View
		
		liveView = new LiveGridView();  
		liveView.setEmptyText("No sites were found.  Expand the panel to add sites.");
		liveView.setCacheSize(200);
		grid.setView(liveView);
		
	    ToolBar toolBar = new ToolBar();
	    toolBar.setAlignment(HorizontalAlignment.RIGHT);
	    LiveToolItem tool = new LiveToolItem();
	    tool.bindGrid(grid);
	    toolBar.add(tool);
	    gridPanel.setBottomComponent(toolBar);
	    
	    //	Buffer view (i.e. paging controls) instead of live view
	   
//	    BufferView bufferView = new BufferView();
//	    bufferView.setEmptyText("No sites were found.  Expand the panel to add sites.");
//	    bufferView.setCacheSize(SITE_LOAD_LIMIT * 2);
//	    GridViewConfig viewConfig = new GridViewConfig();
//	    bufferView.setViewConfig(viewConfig);
//		final PagingToolBar toolBar = new PagingToolBar(SITE_LOAD_LIMIT);  
//	    toolBar.bind(siteLoader);
	    
	}

	@Override
	public void addGridColumns(List<ColumnConfig> columns) {

		columns.add(getDisplayColumn("displayUcn",							"UCN+",						100,		false,
					"This is the UCN+ for the site."));
		columns.add(getDisplayColumn("site.institution.institutionName",	"Institution",				140,
					"This is the institution name."));
		columns.add(getDisplayColumn("site.description",					"Location",					100,
					"This is the description of the location at the site."));
		columns.add(getDisplayColumn("site.institution.htmlAddress",		"Address",					140));
		columns.add(getDisplayColumn("statusDescription",					"Status",					80));
		
//		These hidden columns were dropped because they mess up the columns widths and the notes row expander cell... a bug in GXT, it seems.
		columns.add(getHiddenColumn("siteUcn",								"UCN",						 80,		true, UiConstants.INTEGER_FORMAT,
				"This is the UCN for the site."));
		columns.add(getHiddenColumn("siteUcnSuffix",						"Suffix",					 40,		true, UiConstants.INTEGER_FORMAT,
				"This is the suffix for this pseudo site."));
		columns.add(getHiddenColumn("siteLocCode",							"Code",						 40,
				"This is the code for the location at the site."));
	
		noteExpander = getNoteExpander();
		columns.add(noteExpander);
		
	}

	@Override
	public void setFormFieldValues(AgreementSiteInstance instance) {
		String displayStatus = "Site " + AppConstants.getStatusDescription(instance.getStatus());
		if (instance.getStatus() == AppConstants.STATUS_INACTIVE && instance.getCancelReasonCode() != null && instance.getCancelReasonCode().length() > 0 && instance.getInactiveDate() != null)
			displayStatus = "Deactivated " + UiConstants.formatDate(instance.getInactiveDate());
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
		
		if (instance != null)
			registerUserCache(instance, "Site for Agreement " + AppConstants.appendCheckDigit(instance.getAgreementId()));
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
	protected void executeLoader(int id, AsyncCallback<List<AgreementSiteInstance>> callback) {
		// BufferView requires this, LiveView does not (it starts loading automatically)
//		grid.getStore().getLoader().load();
	}
	
	@Override
	public void adjustFormPanelSize(int width, int height) {
		super.adjustFormPanelSize(width, height);
		
		if (formColumn1.isRendered())
			agreementIdField.setWidth( (formColumn1.getWidth(true) - formColumn1.getLabelWidth()) - 64);
	}

	@Override
	protected void addFormFields(FormPanel panel, FormData formData) {
		formData = new FormData("-24");
		
		TableLayout tableLayout = new TableLayout(2);
		tableLayout.setWidth("100%");
		panel.setLayout(tableLayout);
		
		TableData tData1 = new TableData();
		tData1.setWidth("50%");
		
		formColumn1 = getNewFormInnerPanel(75); formColumn1.setId("formColumn1"); //formColumn1.setLayoutData(0.5); //formColumn1.setWidth("50%");
		formColumn2 = getNewFormInnerPanel(75); formColumn2.setId("formColumn2"); //formColumn2.setLayoutData(0.5); //formColumn2.setWidth("50%");
		
		ucnDisplay.setToolTip(UiConstants.getQuickTip("The ucn for the site."));
		addressDisplay.setToolTip(UiConstants.getQuickTip("The address of the institution."));

		agreementIdField.setReadOnly(true);
		agreementIdField.setWidth(200);
		idNotesCombo.setSpacing(20);
		ucnDisplay.setReadOnly(true);
		
		addressDisplay.setWidth(200);
		
		//	Force all field widths to zero, so that they'll be computed based on the width of the enclosing form
		idNotesCombo.setWidth(0);
//		agreementIdField.setWidth(0);
		
		notesField.setWidth(0);
		institutionField.setWidth(0);
		addressDisplay.setWidth(0);
		ucnDisplay.setWidth(0);
		customerTypeDisplay.setWidth(0);
		siteLocationField.setWidth(0);
		commissionTypeField.setWidth(0);
		cancelReasonField.setWidth(0);

		siteLocationField.setAppPortletProvider(appPortletProvider);
		
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
		
		panel.add(formColumn1,	tData1);
		panel.add(formColumn2,	tData1);
	}
	
	protected SiteLocationSearchField getSiteLocationField(String name, String label, int width, String toolTip) {
		SiteLocationSearchField siteLocCombo = new SiteLocationSearchField(this);
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
			siteLocationField.setFor(0, 0, null);
			return;
		}
		
//		if (focusInstance != null && !focusInstance.isNewRecord()) {
//			siteLocationField.setFor(instance.getUcn(), 1);
//			siteLocationField.setValue(SiteInstance.obtainModel(SiteInstance.getAllInstance(instance.getUcn(), 1)));
////			siteLocationField.setReadOnly(!agreementSite.isNewRecord());
//		}
		
		if (focusInstance != null && focusInstance.getSiteUcn() == instance.getUcn()) {
			// Same institution as instance, so use the instance UCN
			siteLocationField.setFor(focusInstance);
			siteLocationField.setValue(SiteInstance.obtainModel(focusInstance.getSite()));
		} else {
			// Different UCN, default to suffix 1
			siteLocationField.setFor(instance);
			siteLocationField.setValue(SiteInstance.obtainModel(SiteInstance.getAllInstance(instance.getUcn(), 1)));
		}
		
		ucnDisplay.setValue(instance.getUcn() + "");
		addressDisplay.setValue(instance.getHtmlAddress());
		customerTypeDisplay.setValue(instance.getPublicPrivateDescription() + " / " + instance.getGroupDescription() + " &rArr; " + instance.getTypeDescription());
		
	}

	
	public void registerUserCache(final UserCacheTarget target, String hint) {
		
		if (!AppConstants.USER_ACCESS_CACHE_ACTIVE)
			return;
		
		if (target != null) {
			userCacheUpdateService.updateUserCache(target, hint,
					new AsyncCallback<String>() {
						public void onFailure(Throwable caught) {
							// In production, this might all be removed, and treated as something users don't care about
							// Show the RPC error message to the user
							if (caught instanceof IllegalArgumentException)
								MessageBox.alert("Alert", caught.getMessage(), null);
							else {
								MessageBox.alert("Alert", "User cache update failed unexpectedly.", null);
								System.out.println(caught.getClass().getName());
								System.out.println(caught.getMessage());
							}
						}

						public void onSuccess(String result) {
							fireUserCacheUpdateEvents(target);
						}
				});
		}
	}
	
	public void fireUserCacheUpdateEvents(UserCacheTarget target) {
		//	Fire an event so any listening portlets can update themselves
		AppEvent appEvent = new AppEvent(AppEvents.SiteAccess);
		if (target instanceof AgreementSiteInstance)
			appEvent.set( (AgreementSiteInstance) target);
		AppEventBus.getSingleton().fireEvent(AppEvents.SiteAccess, appEvent);
	}

	@Override
	protected void asyncUpdate() {
	
		// Set field values from form fields
		
		if (focusInstance == null) {
			focusInstance = new AgreementSiteInstance();
			focusInstance.setNewRecord(true);
			focusInstance.setAgreementId(getAgreementId());
			InstitutionInstance institution = institutionField.getSelectedValue().getBean();
			if (institution == null) {
				MessageBox.alert("Unexpted Error", "No institution is selected for the site.", null);
				return;
			}
			focusInstance.setSiteUcn(institution.getUcn());
			focusInstance.setSiteUcnSuffix(1);
			SiteInstance site = siteLocationField.getSelectedValue().getBean();
			if (site == null) {
				MessageBox.alert("Unexpted Error", "No site location is selected for this agreement site.", null);
				return;
			}
			focusInstance.setSiteLocCode(site.getSiteLocCode());
		}
		
		if (commissionTypeField.getSelectedValue() == null)
			focusInstance.setCommissionType(null);
		else
			focusInstance.setCommissionType( (CommissionTypeInstance) commissionTypeField.getSelectedValue().getBean() );
		
		if (cancelReasonField.getSelectedValue() == null) {
			focusInstance.setCancelReason(CancelReasonInstance.getEmptyInstance());
			focusInstance.setCancelReasonCode("");
		} else
			focusInstance.setCancelReason( (CancelReasonInstance) cancelReasonField.getSelectedValue().getBean() );
		
		if (focusInstance.isNewRecord())
			focusInstance.setNote(notesField.getNote());
		else
			focusInstance.setNote(null);	//	This will keep the note from being updated by this call
	
		//	Issue the asynchronous update request and plan on handling the response
		updateAgreementSiteService.updateAgreementSite(focusInstance,
				new AsyncCallback<UpdateResponse<AgreementSiteInstance>>() {
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

					public void onSuccess(UpdateResponse<AgreementSiteInstance> updateResponse) {
						AgreementSiteInstance updatedAgreementSite = (AgreementSiteInstance) updateResponse.getInstance();
						if (updatedAgreementSite.isNewRecord()) {
							updatedAgreementSite.setNewRecord(false);
							grid.getStore().insert(AgreementSiteInstance.obtainModel(updatedAgreementSite), 0);
							//	Fire an event so any listening portlets can update themselves
							AppEvent appEvent = new AppEvent(AppEvents.SiteAccess);
							appEvent.set(updatedAgreementSite);
							AppEventBus.getSingleton().fireEvent(AppEvents.SiteAccess, appEvent);
						}
						
						focusInstance.setNewRecord(false);
						focusInstance.setValuesFrom(updatedAgreementSite);
						setFormFromInstance(updatedAgreementSite);	//	setFormFieldValues(updatedAgreementSite);
						
						//	This puts the grid in synch
						BeanModel gridModel = grid.getStore().findModel(focusInstance.getUniqueKey());
						if (gridModel != null) {
							AgreementSiteInstance matchInstance = gridModel.getBean();
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
	
	/**
	 * Construct and return a loader to handle returning a list of institutions.
	 * @return
	 */
	protected PagingLoader<PagingLoadResult<AgreementSiteInstance>> getSiteLoader() {
		// proxy and reader  
		RpcProxy<PagingLoadResult<AgreementSiteInstance>> proxy = new RpcProxy<PagingLoadResult<AgreementSiteInstance>>() {  
			@Override  
			public void load(Object loadConfig, final AsyncCallback<PagingLoadResult<AgreementSiteInstance>> callback) {
		    	
				// This could be as simple as calling userListService.getUsers and passing the callback
				// Instead, here the callback is overridden so that it can catch errors and alert the users.  Then the original callback is told of the failure.
				// On success, the original callback is just passed the onSuccess message, and the response (the list).
				
				AsyncCallback<SynchronizedPagingLoadResult<AgreementSiteInstance>> myCallback = new AsyncCallback<SynchronizedPagingLoadResult<AgreementSiteInstance>>() {
					public void onFailure(Throwable caught) {
						// Show the RPC error message to the user
						if (caught instanceof IllegalArgumentException)
							MessageBox.alert("Alert", caught.getMessage(), null);
						else if (caught instanceof ServiceNotReadyException)
								MessageBox.alert("Alert", "The " + caught.getMessage() + " is not available at this time.  Please try again in a few minutes.", null);
						else {
							System.out.println(caught.getClass().getName());
							System.out.println(caught.getMessage());
							MessageBox.alert("Alert", "Agreement site load failed unexpectedly.", null);
						}
						callback.onFailure(caught);
					}

					public void onSuccess(SynchronizedPagingLoadResult<AgreementSiteInstance> syncResult) {
						if(syncResult.getSyncId() != searchSyncId)
							return;
						
						PagingLoadResult<AgreementSiteInstance> result = syncResult.getResult();

						callback.onSuccess(result);

						grid.unmask();
					}
				};
				
				if (!grid.isMasked()) grid.mask("Loading...");		// Required because GXT forgets to do this when a remote sort is initiated through the columns
				searchSyncId = System.currentTimeMillis();
				invokeSearchService((PagingLoadConfig) loadConfig, focusId, AppConstants.STATUS_DELETED, searchSyncId, myCallback);
		    }  
		};
		BeanModelReader reader = new BeanModelReader();
		
		// loader and store  
		BasePagingLoader<PagingLoadResult<AgreementSiteInstance>> loader = new BasePagingLoader<PagingLoadResult<AgreementSiteInstance>>(proxy, reader) {
			@Override
			  protected Object newLoadConfig() {
				return new BaseFilterPagingLoadConfig();
			}
		};
		loader.setReuseLoadConfig(false);
		return loader;
	}
	
	public void invokeSearchService(PagingLoadConfig loadConfig, int id, char neStatus, long searchSyncId, AsyncCallback<SynchronizedPagingLoadResult<AgreementSiteInstance>> myCallback) {
		agreementSiteListService.getAgreementSites((PagingLoadConfig) loadConfig, focusId, neStatus, searchSyncId, myCallback);
	}
}
