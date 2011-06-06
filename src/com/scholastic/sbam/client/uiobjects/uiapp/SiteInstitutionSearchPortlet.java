package com.scholastic.sbam.client.uiobjects.uiapp;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.data.BasePagingLoader;
import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.BeanModelReader;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoader;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.scholastic.sbam.client.services.SiteInstitutionSearchService;
import com.scholastic.sbam.client.services.SiteInstitutionSearchServiceAsync;
import com.scholastic.sbam.client.services.SiteInstitutionWordService;
import com.scholastic.sbam.client.services.SiteInstitutionWordServiceAsync;
import com.scholastic.sbam.client.services.SiteLocationSearchService;
import com.scholastic.sbam.client.services.SiteLocationSearchServiceAsync;
import com.scholastic.sbam.client.util.IconSupplier;
import com.scholastic.sbam.client.util.UiConstants;
import com.scholastic.sbam.shared.exceptions.ServiceNotReadyException;
import com.scholastic.sbam.shared.objects.FilterWordInstance;
import com.scholastic.sbam.shared.objects.InstitutionInstance;
import com.scholastic.sbam.shared.objects.SiteInstance;
import com.scholastic.sbam.shared.objects.SynchronizedPagingLoadResult;

public class SiteInstitutionSearchPortlet extends InstitutionSearchPortletBase {
	protected final SiteInstitutionSearchServiceAsync	siteInstitutionSearchService = GWT.create(SiteInstitutionSearchService.class);
	protected final SiteInstitutionWordServiceAsync  	siteInstitutionWordService   = GWT.create(SiteInstitutionWordService.class);
	protected final SiteLocationSearchServiceAsync 		siteLocationSearchService	 = GWT.create(SiteLocationSearchService.class);

	protected long									siteSearchSyncId;
	PagingLoader<PagingLoadResult<SiteInstance>>	siteLocationsLoader;
	protected ListStore<ModelData>					siteLocationsStore;
	protected Grid<ModelData>						siteLocationsGrid;
	protected FieldSet 								siteLocationsFieldSet;
	
	public SiteInstitutionSearchPortlet() {
		super(AppPortletIds.SITE_INSTITUTION_SEARCH.getHelpTextId());
	}
	
	protected void setPortletIcon() {
		IconSupplier.setIcon(this, IconSupplier.getSiteIconName());
	}
	
	public void setPortletRenderValues() {
		setHeading("Site Institution Search");
		setToolTip(UiConstants.getQuickTip("Use this tool to find institutions that are recipients of product services."));
	}
	
	public void invokeSearchService(PagingLoadConfig loadConfig, String filter, boolean includeAgreementSummaries, long searchSyncId, AsyncCallback<SynchronizedPagingLoadResult<InstitutionInstance>> myCallback) {
		siteInstitutionSearchService.getSiteInstitutions((PagingLoadConfig) loadConfig, filter, true, searchSyncId, myCallback);
	}
	
	protected void invokeWordService(PagingLoadConfig loadConfig, AsyncCallback<PagingLoadResult<FilterWordInstance>>myCallback) {
		siteInstitutionWordService.getSiteInstitutionWords((PagingLoadConfig) loadConfig, myCallback);
	}
	
	/**
	 * Overridden to also add a grid for the site locations.
	 */
	@Override
	protected void addAgreementsGrid(FormData formData) {
		super.addAgreementsGrid(formData);
		addSiteLocationsGrid(formData);
		addFieldSetListeners();
	}
	
	@Override
	protected void showInstitution(InstitutionInstance institution) {
		super.showInstitution(institution);
		siteLocationsLoader.load();
	}
	
	protected void addSiteLocationsGrid(FormData formData) {
		List<ColumnConfig> columns = new ArrayList<ColumnConfig>();
		
		columns.add(getDisplayColumn("ucnSuffix",				"Suffix",					50,		true, NumberFormat.getFormat("#"),
					"This is the suffix for the site."));
		columns.add(getDisplayColumn("siteLocCode",				"Code",						80,
					"This is the code identifying the site location."));
		columns.add(getDisplayColumn("description",				"Decription",				180,
					"This is the description of the site location."));
		
		ColumnModel cm = new ColumnModel(columns);  

		siteLocationsLoader = getSiteLoader();
		siteLocationsStore = new ListStore<ModelData>(siteLocationsLoader);
		
		siteLocationsGrid = new Grid<ModelData>(siteLocationsStore, cm);  
		siteLocationsGrid.setBorders(true);
		siteLocationsGrid.setHeight(200);
		siteLocationsGrid.setStripeRows(true);
		siteLocationsGrid.setColumnLines(true);
		siteLocationsGrid.setHideHeaders(false);
		siteLocationsGrid.setWidth(cm.getTotalWidth() + 5);
		
		//	Open a new portlet to display an agreement when a row is selected
		siteLocationsGrid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE); 
		final AppPortlet thisPortlet = this; 
		siteLocationsGrid.getSelectionModel().addListener(Events.SelectionChange,  
				new Listener<SelectionChangedEvent<ModelData>>() {  
					public void handleEvent(SelectionChangedEvent<ModelData> be) {  
						if (be.getSelection().size() > 0) {
							SiteInstance site = (SiteInstance) ((BeanModel) be.getSelectedItem()).getBean();
							SiteLocationPortlet portlet = (SiteLocationPortlet) portletProvider.getPortlet(AppPortletIds.SITE_LOCATION_DISPLAY);
							portlet.setSiteUcn(site.getUcn());
							portlet.setSiteUcnSuffix(site.getUcnSuffix());
							portlet.setSiteLocCode(site.getSiteLocCode());
							if (focusInstitution != null) {
								String foundFor = focusInstitution.getInstitutionName() != null && focusInstitution.getInstitutionName().length() > 0 ? 
													focusInstitution.getInstitutionName() : 
													"UCN " + focusInstitution.getUcn();
								portlet.setIdentificationTip("Found for " + foundFor + "");
							}
							portletProvider.insertPortlet(portlet, portalRow, thisPortlet.getInsertColumn());
							siteLocationsGrid.getSelectionModel().deselectAll();
						} 
					}
			});
	
		siteLocationsFieldSet = new FieldSet();
		siteLocationsFieldSet.setBorders(true);
		siteLocationsFieldSet.setHeading("Site Locations");// 		displayCard.add(new LabelField("<br/><i>Existing Agreements</i>"));
		siteLocationsFieldSet.setCollapsible(true);
		siteLocationsFieldSet.setToolTip(UiConstants.getQuickTip("These are the site locations for this institution.  Click a site location to review or edit."));
		siteLocationsFieldSet.add(siteLocationsGrid, new FormData(cm.getTotalWidth() + 10, 200));
		siteLocationsFieldSet.collapse();
		
//		displayCard.add(new LabelField(""));	// Used as a spacer
		displayCard.add(siteLocationsFieldSet, formData);	// new FormData("95%")); // new FormData(cm.getTotalWidth() + 20, 200));	
	}
	
	/**
	 * Add listeners to automatically close other field sets when one is expanded.
	 */
	protected void addFieldSetListeners() {
		
		siteLocationsFieldSet.addListener(Events.BeforeExpand, new Listener<BaseEvent>() {
				@Override
				public void handleEvent(BaseEvent be) {
					if (be.getType().getEventCode() == Events.BeforeExpand.getEventCode()) {
						agreementsFieldSet.collapse();
					}
				}		
			});
		
		agreementsFieldSet.addListener(Events.BeforeExpand, new Listener<BaseEvent>() {
				@Override
				public void handleEvent(BaseEvent be) {
					if (be.getType().getEventCode() == Events.BeforeExpand.getEventCode()) {
						siteLocationsFieldSet.collapse();
					}
				}		
			});
	}
	
	/**
	 * Construct and return a loader to handle returning a list of siteLocations.
	 * @return
	 */
	protected PagingLoader<PagingLoadResult<SiteInstance>> getSiteLoader() {
		// proxy and reader  
		RpcProxy<PagingLoadResult<SiteInstance>> proxy = new RpcProxy<PagingLoadResult<SiteInstance>>() {  
			@Override  
			public void load(Object loadConfig, final AsyncCallback<PagingLoadResult<SiteInstance>> callback) {
		    	
				// This could be as simple as calling userListService.getUsers and passing the callback
				// Instead, here the callback is overridden so that it can catch errors and alert the users.  Then the original callback is told of the failure.
				// On success, the original callback is just passed the onSuccess message, and the response (the list).
				
				AsyncCallback<SynchronizedPagingLoadResult<SiteInstance>> myCallback = new AsyncCallback<SynchronizedPagingLoadResult<SiteInstance>>() {
					public void onFailure(Throwable caught) {
						// Show the RPC error message to the user
						if (caught instanceof IllegalArgumentException)
							MessageBox.alert("Alert", caught.getMessage(), null);
						else if (caught instanceof ServiceNotReadyException)
								MessageBox.alert("Alert", "The " + caught.getMessage() + " is not available at this time.  Please try again in a few minutes.", null);
						else {
							MessageBox.alert("Alert", "Site Location load failed unexpectedly.", null);
							System.out.println(caught.getClass().getName());
							System.out.println(caught.getMessage());
						}
						callback.onFailure(caught);
					}

					public void onSuccess(SynchronizedPagingLoadResult<SiteInstance> syncResult) {
						if(syncResult.getSyncId() != siteSearchSyncId)
							return;
						
						PagingLoadResult<SiteInstance> result = syncResult.getResult();
						if (result.getData() != null && result.getData().size() > 0) {
							if (siteLocationsFieldSet != null) siteLocationsFieldSet.expand();
						} else {
							if (agreementsFieldSet != null) agreementsFieldSet.expand();
						}

						callback.onSuccess(result);
					}
				};
				
				siteSearchSyncId = System.currentTimeMillis();
				siteLocationSearchService.searchSiteLocations((PagingLoadConfig) loadConfig, -1, focusUcn, -1, null, siteSearchSyncId, myCallback);
				
		    }  
		};
		BeanModelReader reader = new BeanModelReader();
		
		// loader and store  
		PagingLoader<PagingLoadResult<SiteInstance>> loader = new BasePagingLoader<PagingLoadResult<SiteInstance>>(proxy, reader);
		return loader;
	}
}
