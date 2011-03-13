package com.scholastic.sbam.client.uiobjects;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.Style.SortDir;
import com.extjs.gxt.ui.client.data.BasePagingLoader;
import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.BeanModelReader;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoader;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.grid.filters.DateFilter;
import com.extjs.gxt.ui.client.widget.grid.filters.GridFilters;
import com.extjs.gxt.ui.client.widget.grid.filters.NumericFilter;
import com.extjs.gxt.ui.client.widget.grid.filters.StringFilter;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.scholastic.sbam.client.services.UserCacheListService;
import com.scholastic.sbam.client.services.UserCacheListServiceAsync;
import com.scholastic.sbam.client.util.IconSupplier;
import com.scholastic.sbam.client.util.UiConstants;
import com.scholastic.sbam.shared.exceptions.ServiceNotReadyException;
import com.scholastic.sbam.shared.objects.InstitutionInstance;
import com.scholastic.sbam.shared.objects.SynchronizedPagingLoadResult;
import com.scholastic.sbam.shared.objects.UserCacheInstance;

public class RecentInstitutionsPortlet extends GridSupportPortlet<InstitutionInstance> implements AppSleeper, AppPortletRequester {

	protected final UserCacheListServiceAsync userCacheListService = GWT.create(UserCacheListService.class);
	
	protected ListStore<ModelData>	institutionsStore;
	protected Grid<ModelData>		institutionsGrid;
//	protected LiveGridView			liveView;
	
	protected PagingLoader<PagingLoadResult<UserCacheInstance>> userCacheLoader;
	
	protected AppPortletProvider	portletProvider;
	
	protected long					searchSyncId = 0;
	
	protected String				filter;
	
	public RecentInstitutionsPortlet() {
		super(AppPortletIds.RECENT_AGREEMENTS_DISPLAY.getHelpTextId());
	}
	
	public RecentInstitutionsPortlet(String helpTextId) {
		super(helpTextId);
	}
	
	@Override  
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);
		
		setHeading("Recent Institutions");
		setLayout(new FitLayout());
		setHeight(250);
		IconSupplier.setIcon(this, IconSupplier.getInstitutionIconName());
		
		addRecentInstitutionsGrid(new FormData("100%"));
		addFilters();
		
		loadFiltered(null);
	}
	

	
	protected void addRecentInstitutionsGrid(FormData formData) {
		userCacheLoader = getUserCacheLoader(); 

		userCacheLoader.setSortDir(SortDir.DESC);  
		userCacheLoader.setSortField("accessDatetime");  
		userCacheLoader.setRemoteSort(false);
		
		List<ColumnConfig> columns = new ArrayList<ColumnConfig>();
		
		ColumnConfig idCol = getDisplayColumn("intKey",					"ID #",						80,		true,	UiConstants.INTEGER_FORMAT);
		idCol.setRenderer(new GridCellRenderer<ModelData>() {

			  public Object render(ModelData model, String property, ColumnData config, int rowIndex, int colIndex,  
			      ListStore<ModelData> store, Grid<ModelData> grid) {  
			    return "<b>"  
			        + model.get("intKey") 
			        + "</b>";  
			  }  

			});
		idCol.setAlignment(HorizontalAlignment.RIGHT);
		
		columns.add(idCol);
		
		columns.add(getDisplayColumn("accessDatetime",			"Accessed",					150,			true, UiConstants.APP_DATE_PLUS_TIME_FORMAT,
		"This is the date this institution was last viewed."));
//		columns.add(getDisplayColumn("",						"Customer",					150));
		columns.add(getDisplayColumn("hint",					"Hint",						250));
		
		ColumnModel cm = new ColumnModel(columns);  

		institutionsStore = new ListStore<ModelData>(userCacheLoader);
		
		institutionsGrid = new Grid<ModelData>(institutionsStore, cm);  
		institutionsGrid.setBorders(false);  
		institutionsGrid.setAutoExpandColumn("hint");  
//		institutionsGrid.setLoadMask(true);
//		institutionsGrid.setHeight(200);
		institutionsGrid.setStripeRows(true);
		institutionsGrid.setColumnLines(true);
		institutionsGrid.setHideHeaders(false);
		institutionsGrid.setWidth(cm.getTotalWidth() + 20);
		
		//	Switch to the display card when a row is selected
		institutionsGrid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);  
		institutionsGrid.getSelectionModel().addListener(Events.SelectionChange,  
				new Listener<SelectionChangedEvent<ModelData>>() {  
					public void handleEvent(SelectionChangedEvent<ModelData> be) {  
						if (be.getSelection().size() > 0) {
							UserCacheInstance cacheInstance = (UserCacheInstance) ((BeanModel) be.getSelectedItem()).getBean();
							InstitutionSearchPortlet portlet = (InstitutionSearchPortlet) portletProvider.getPortlet(AppPortletIds.FULL_INSTITUTION_SEARCH);
							portlet.setFocusUcn(cacheInstance.getIntKey());
						//	portlet.setFilter(cacheInstance.getHint());
							portletProvider.addPortlet(portlet, 1);
							institutionsGrid.getSelectionModel().deselectAll();
						} 
					}  
			});

//		liveView = new LiveGridView();  
//		liveView.setEmptyText("There are no recent institutions for your user name.");
//		liveView.setCacheSize(100);
//		liveView.setRowHeight(32);
//		institutionsGrid.setView(liveView);
//		grid.setHeight(550);
		institutionsGrid.getAriaSupport().setLabelledBy(this.getHeader().getId() + "-label"); 
		
		add(institutionsGrid);
	}
	
	protected void addFilters() {
		GridFilters filters = new GridFilters();  
		filters.setLocal(true);
		
		filters.addFilter(new NumericFilter("intKey"));
		filters.addFilter(new DateFilter("accessDatetime"));
		filters.addFilter(new StringFilter("hint"));
		
		institutionsGrid.addPlugin(filters);
	}
	
	/**
	 * Instigate an asynchronous load with a filter value (currently ignored).
	 * @param filter
	 */
	protected void loadFiltered(String filter) {
	//	updateUserPortlet();
		userCacheLoader.load();
	}
	
	/**
	 * Construct and return a loader to handle returning a list of institutions.
	 * @return
	 */
	protected PagingLoader<PagingLoadResult<UserCacheInstance>> getUserCacheLoader() {
		// proxy and reader  
		RpcProxy<PagingLoadResult<UserCacheInstance>> proxy = new RpcProxy<PagingLoadResult<UserCacheInstance>>() {  
			@Override  
			public void load(Object loadConfig, final AsyncCallback<PagingLoadResult<UserCacheInstance>> callback) {
		    	
				// This could be as simple as calling userListService.getUsers and passing the callback
				// Instead, here the callback is overridden so that it can catch errors and alert the users.  Then the original callback is told of the failure.
				// On success, the original callback is just passed the onSuccess message, and the response (the list).
				
				AsyncCallback<SynchronizedPagingLoadResult<UserCacheInstance>> myCallback = new AsyncCallback<SynchronizedPagingLoadResult<UserCacheInstance>>() {
					public void onFailure(Throwable caught) {
						// Show the RPC error message to the user
						if (caught instanceof IllegalArgumentException)
							MessageBox.alert("Alert", caught.getMessage(), null);
						else if (caught instanceof ServiceNotReadyException)
								MessageBox.alert("Alert", "The " + caught.getMessage() + " is not available at this time.  Please try again in a few minutes.", null);
						else {
							MessageBox.alert("Alert", "User Cache load failed unexpectedly.", null);
							System.out.println(caught.getClass().getName());
							System.out.println(caught.getMessage());
						}
						callback.onFailure(caught);
					}

					public void onSuccess(SynchronizedPagingLoadResult<UserCacheInstance> syncResult) {
						if(syncResult.getSyncId() != searchSyncId)
							return;
						
						PagingLoadResult<UserCacheInstance> result = syncResult.getResult();
//						if ( result.getData() == null || result.getData().size() == 0 ) {
//							if (filter.length() == 0)
//								liveView.setEmptyText("Enter filter criteria to search for recent institutions.");
//							else
//								liveView.setEmptyText("Please enter filter criteria to narrow your search.");
//						}
						callback.onSuccess(result);
					}
				};

				searchSyncId = System.currentTimeMillis();
				userCacheListService.getUserCacheTargets((PagingLoadConfig) loadConfig, null, InstitutionInstance.getUserCacheCategory(), null, 0, searchSyncId, myCallback);
				
		    }  
		};
		BeanModelReader reader = new BeanModelReader();
		
		// loader and store  
		PagingLoader<PagingLoadResult<UserCacheInstance>> loader = new BasePagingLoader<PagingLoadResult<UserCacheInstance>>(proxy, reader);
		return loader;
	}

	@Override
	public void awaken() {
		if (this.isExpanded()) {
			
		}
	}

	@Override
	public void sleep() {
	}

	@Override
	public void setFromKeyData(String keyData) {
		//	Nothing to do... this grid is unfocused / always populates fresh
	}

	@Override
	public String getKeyData() {
		//	Nothing to do... this grid is unfocused / always populates fresh
		return "";
	}

	@Override
	public void setAppPortletProvider(AppPortletProvider provider) {
		this.portletProvider = provider;
	}

}
