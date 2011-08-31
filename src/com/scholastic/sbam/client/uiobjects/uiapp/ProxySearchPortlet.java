package com.scholastic.sbam.client.uiobjects.uiapp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.Style.SortDir;
import com.extjs.gxt.ui.client.data.BasePagingLoader;
import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.BeanModelReader;
import com.extjs.gxt.ui.client.data.LoadConfig;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoader;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.KeyNav;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.ToolButton;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.FormPanel.LabelAlign;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridView;
import com.extjs.gxt.ui.client.widget.grid.RowExpander;
import com.extjs.gxt.ui.client.widget.grid.filters.GridFilters;
import com.extjs.gxt.ui.client.widget.grid.filters.NumericFilter;
import com.extjs.gxt.ui.client.widget.grid.filters.StringFilter;
import com.extjs.gxt.ui.client.widget.layout.CardLayout;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.toolbar.LabelToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.scholastic.sbam.client.services.ProxyGetService;
import com.scholastic.sbam.client.services.ProxyGetServiceAsync;
import com.scholastic.sbam.client.services.ProxySearchService;
import com.scholastic.sbam.client.services.ProxySearchServiceAsync;
import com.scholastic.sbam.client.uiobjects.foundation.AppSleeper;
import com.scholastic.sbam.client.uiobjects.foundation.GridSupportPortlet;
import com.scholastic.sbam.client.util.IconSupplier;
import com.scholastic.sbam.client.util.UiConstants;
import com.scholastic.sbam.shared.exceptions.ServiceNotReadyException;
import com.scholastic.sbam.shared.objects.ProxyIpInstance;
import com.scholastic.sbam.shared.objects.ProxyInstance;
import com.scholastic.sbam.shared.objects.ProxyTuple;
import com.scholastic.sbam.shared.objects.SynchronizedPagingLoadResult;
import com.scholastic.sbam.shared.util.AppConstants;

public class ProxySearchPortlet extends GridSupportPortlet<ProxyInstance> implements AppSleeper, AppPortletRequester {
	
	protected static final int LOAD_LIMIT			= AppConstants.STANDARD_LOAD_LIMIT;
	protected static final int MIN_FILTER_LENGTH	= 0;
	protected static final int FILTER_LISTEN_PERIOD = 500;	//	This is a little higher, because we're doing a database scan on the back end, so it's not very fast
	
	protected final ProxySearchServiceAsync	proxySearchService  = GWT.create(ProxySearchService.class);
	protected final ProxyGetServiceAsync    proxyGetService		= GWT.create(ProxyGetService.class);
//	protected final AgreementGetServiceAsync    	agreementGetService			= GWT.create(AgreementGetService.class);
	
	protected CardLayout						cards;
	protected ContentPanel						searchPanel;
	protected FormPanel							displayCard;
	protected Grid<ModelData>					grid;
//	protected LiveGridView						liveView;		LiveView removed because local filters don't work with it
	protected GridView							gridView;
	
	protected RowExpander						noteExpander;
	
	protected ListStore<ModelData>				store;
	protected TextField<String>					filterField;
	protected Timer								filterListenTimer;
	protected String							lastTestFilter;
	protected String							filter = "";
	protected GridFilters						columnFilters;
	
	protected PagingLoader<PagingLoadResult<ProxyInstance>> proxyLoader;

	protected LabelField						proxyIdField;
	protected LabelField						descriptionField;
	protected LabelField						searchKeysField;

	protected ListStore<ModelData>				proxyIpsStore;
	protected Grid<ModelData>					proxyIpsGrid;
	protected FieldSet							proxyIpsFieldSet;
	
	protected AppPortletProvider				portletProvider;
	
	protected int								focusAgreementId;
	protected int								focusProxyId;
	protected ProxyInstance						focusProxy;
	
	protected long								searchSyncId = 0;
	
	public ProxySearchPortlet() {
		super();
	}
	
	public ProxySearchPortlet(String helpTextId) {
		super(helpTextId);
	}

	@Override  
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);
		
		setPortletRenderValues();
		
		setLayout(new FitLayout());
		LayoutContainer outerContainer = new LayoutContainer();
		add(outerContainer);
		
		cards = new CardLayout();
		outerContainer.setLayout(cards);
		
		//	We need this to be able to put the tool bar into the top
		searchPanel = new ContentPanel(new FitLayout());
		searchPanel.setHeaderVisible(false);
		searchPanel.setBorders(false);
//		searchPanel.setHeight(540);
		
		setThis();
		addGrid();
		setFilter();
		initializeFilter();
		
		outerContainer.add(searchPanel);
		
		createDisplayCard();
		outerContainer.add(displayCard);
		
		if (focusAgreementId > 0)
			loadProxy(focusAgreementId);
		if (filter != null && filter.length() > 0)
			loadFiltered(filter);
	}
	
	public void setPortletRenderValues() {
		setHeading("Proxy Search");
		setToolTip(UiConstants.getQuickTip("Use this portlet to search for proxies by date and product."));
	}
	
	@Override
	public String getPresenterToolTip() {
		if (focusProxy != null) {
			return "ID " + AppConstants.appendCheckDigit(focusProxy.getProxyId());
		}
		if (focusAgreementId != 0 && focusProxyId != 0)
			if (filter != null && filter.length() > 0)
				return "ID " + focusAgreementId + "-" + focusProxyId + " found for '" + filter + "'";
			else
				return "ID " + focusAgreementId + "-" + focusProxyId;
		if (filter != null && filter.length() > 0)
			return "Agreement Search for '" + filter + "'";
		return "Search for proxies.";
	}
	
	public void initializeFilter() {
		filterField.setValue(filter);
	}
	
	private void createDisplayCard() {
		FormData formData = new FormData("100%");
		displayCard = new FormPanel();

		displayCard.setPadding(10);  
		displayCard.setFrame(true); 
		displayCard.setHeaderVisible(false);  
		displayCard.setBodyBorder(true);
		displayCard.setBodyStyleName("subtle-form");
		displayCard.setButtonAlign(HorizontalAlignment.CENTER);
		displayCard.setLabelAlign(LabelAlign.RIGHT);
		displayCard.setLabelWidth(100); 
		
		ToolButton returnTool = new ToolButton("x-tool-left") {
				@Override
				protected void onClick(ComponentEvent ce) {
					cards.setActiveItem(searchPanel);
					focusProxy = null;
					focusAgreementId = 0;
					focusProxyId = 0;
					updateUserPortlet();
					updatePresenterLabel();
				}
			};
		returnTool.enable();
		
		ToolBar displayBar = new ToolBar();
		displayBar.add(returnTool);
		displayBar.add(new SeparatorToolItem());
		displayBar.add(new Html("<b>Selected Proxy</b>"));
		displayCard.setTopComponent(displayBar);

		proxyIdField = new LabelField();
		proxyIdField.setFieldLabel("ID");
		displayCard.add(proxyIdField, formData);

		descriptionField = new LabelField();
		descriptionField.setFieldLabel("Description");
		displayCard.add(descriptionField, formData);
		
		searchKeysField = new LabelField();  
		searchKeysField.setFieldLabel("Search Keys :");
		displayCard.add(searchKeysField, formData);
		
		addAgreementsGrid(formData);
		addButtons(formData);
		
//		Button returnButton = new Button("Return");
//		returnButton.addSelectionListener(new SelectionListener<ButtonEvent>() {  
//				@Override  
//					public void componentSelected(ButtonEvent ce) {
//						cards.setActiveItem(searchPanel);
//					}  
//			});
//		displayCard.addButton(returnButton);
	}
	

	
	protected void addAgreementsGrid(FormData formData) {
		List<ColumnConfig> columns = new ArrayList<ColumnConfig>();
		
		columns.add(getDisplayColumn("ipRangeDisplay",			"IP Address",		250, "This is an IP address associated with the proxy."));
		
		ColumnModel cm = new ColumnModel(columns);  

		proxyIpsStore = new ListStore<ModelData>();
		
		proxyIpsGrid = new Grid<ModelData>(proxyIpsStore, cm);  
		proxyIpsGrid.setBorders(true);
		proxyIpsGrid.setHeight(180);
		proxyIpsGrid.setStripeRows(true);
		proxyIpsGrid.setColumnLines(true);
		proxyIpsGrid.setHideHeaders(false);
		proxyIpsGrid.setWidth(cm.getTotalWidth() + 5);
		proxyIpsGrid.setAutoExpandColumn("ipRangeDisplay");
		
		//	Open a new portlet to display an agreement when a row is selected
	
		proxyIpsFieldSet = new FieldSet();
		proxyIpsFieldSet.setBorders(true);
		proxyIpsFieldSet.setHeading("IP Addresses");
		proxyIpsFieldSet.setCollapsible(true);
		proxyIpsFieldSet.setToolTip(UiConstants.getQuickTip("These are the IP addresses for this proxy."));
		proxyIpsFieldSet.add(proxyIpsGrid);	//, new FormData(cm.getTotalWidth() + 10, 200));
		
	//	displayCard.add(new LabelField(""));	// Used as a spacer
		displayCard.add(proxyIpsFieldSet, formData);	//	new FormData("95%")); // new FormData(cm.getTotalWidth() + 20, 200));
		
	}
	
	public String constructFilterDescription() {
		return "Links for " + filter;
	}
	
	protected void addButtons(FormData formData) {
		
		ToolBar toolBar = new ToolBar();
		toolBar.setAlignment(HorizontalAlignment.CENTER);
		toolBar.setToolTip(UiConstants.getQuickTip("Use these buttons to edit this proxy."));
		
		Button newAgreementButton = new Button("Edit Proxy");
		newAgreementButton.setToolTip(UiConstants.getQuickTip("Use this button to edit this proxy."));
		IconSupplier.forceIcon(newAgreementButton, IconSupplier.getNewIconName());
		newAgreementButton.addSelectionListener(new SelectionListener<ButtonEvent>() {  
				@Override
				public void componentSelected(ButtonEvent ce) {
					ProxyPortlet portlet = (ProxyPortlet) portletProvider.getPortlet(AppPortletIds.PROXY_DISPLAY);
					portlet.setProxyId(focusProxyId);
					int insertCol = (portalColumn == 0) ? 1 : 0;
					portletProvider.insertPortlet(portlet, portalRow, insertCol);
				}  
			});
		toolBar.add(newAgreementButton);
		
		displayCard.add(toolBar);
	}
	
	protected void showProxy(ProxyInstance instance) {
		loadProxy(instance.getProxyId());
	}
	
	protected void showProxy(ProxyTuple instance) {
		focusProxy = instance.getProxy();
		
		if (focusProxy == null)
			return;
		
		focusProxyId = focusProxy.getProxyId();
		
//		registerUserCache(focusProxy, "Found for " + filter);
		updateUserPortlet();

		proxyIdField.setValue(focusProxy.getProxyIdCheckDigit());
		descriptionField.setValue(focusProxy.getDescription());
		searchKeysField.setValue(focusProxy.getSearchKeys());
		
		proxyIpsStore.removeAll();
		if (instance.getProxyIps() != null)
			for (ProxyIpInstance proxyIp : instance.getProxyIps())
				proxyIpsStore.add(ProxyIpInstance.obtainModel(proxyIp));
		
		cards.setActiveItem(displayCard);

		if (presenter != null)
			presenter.updateLabel(this);
		
	}

//	@Override
//	protected BeanModel getModel(ProxyInstance instance) {
//		BeanModel model = super.getModel(instance);
//		String display = "<b>" + instance.getId() + "</b> : " + instance.getLastStartDate() + " &ndash; " + instance.getEndDate();
//		if (!instance.getFirstStartDate().equals(instance.getLastStartDate()))
//			display += " (since " + instance.getFirstStartDate() + ")";
//		model.set("display", display);
//		return model;
//	}
	
	protected void setFilter() {
		
		ToolBar toolBar = new ToolBar();
		toolBar.setAlignment(HorizontalAlignment.LEFT);
		toolBar.getAriaSupport().setLabel("Filters");

//		LiveToolItem removed because it requires a LiveGridView, which doesn't allow for local filtering
//		LiveToolItem item = new LiveToolItem();  
//		item.bindGrid(grid); 
//		toolBar.add(item); 
		
//		toolBar.add(new FillToolItem());  
		
		toolBar.add(new LabelToolItem("Filter by: "));
		
		filterField = getFilterBox();
		filterField.getAriaSupport().setLabelledBy(toolBar.getItem(0).getId());
//		filter.setRawValue(filter);
		toolBar.add(filterField);
		
		searchPanel.setTopComponent(toolBar);
	}
	
	public Date getTodayDate() {
		Date today = new Date();
		
		DateTimeFormat format = DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_MEDIUM);	//field.getPropertyEditor().getFormat();

	    try {
		    String value = format.format(today);
	    	return format.parse(value);	//	field.getPropertyEditor().convertStringValue(value);
	    } catch (Exception e) {
	    	return new Date();
	    }
	}
	
	protected TextField<String> getFilterBox() {

		TextField<String> filterField = new TextField<String>();  
		filterField.setWidth(150); 
		filterField.setEmptyText("Enter search criteria here...");
		
		setFilterListenTimer(filterField);	// This method sends messages using a timer... it is less responsive, but so bothers the server less, and is a little more reliable
		
		return filterField;
	}
	
	protected void setFilterListenTimer(final TextField<String> filterField) {
		filterListenTimer = new Timer() {
			  @Override
			  public void run() {
			//	  System.out.println("Filter: " + filter);
			//	  System.out.println(combo.getRawValue() +  " / " + combo.getValue() + " / " + combo.getOriginalValue());
				  String value = (filterField.getRawValue() == null)?"":filterField.getRawValue().trim();
				  if (!value.equals(filter.trim())) {
			 		  if (value.trim().length() < MIN_FILTER_LENGTH) {
			 			  //	Feedback here...
			 			  grid.getStore().removeAll();
			 			  gridView.setEmptyText("Enter at least " + MIN_FILTER_LENGTH + " characters with which to search.");
			 		  } else {
						  // This optimization makes sure the user has stopped (or at least paused) in typing... if the value is changing, wait until it doesn't
						  if (value.equals(lastTestFilter))
							  loadFiltered(filterField.getRawValue());
						  else
							  lastTestFilter = value;
					  }	  
				  }
			  }
			};

			filterListenTimer.scheduleRepeating(FILTER_LISTEN_PERIOD);
	}
	
	protected void addGrid() {
		proxyLoader = getAgreementLoader(); 

		proxyLoader.setSortDir(SortDir.ASC);  
		proxyLoader.setSortField("id");  

		proxyLoader.setRemoteSort(false);

		store = new ListStore<ModelData>(proxyLoader); 
 
		List<ColumnConfig> columns = new ArrayList<ColumnConfig>();  
		     
//		ColumnConfig name = new ColumnConfig("id", "Agreement #", 80);  
//		name.setRenderer(new GridCellRenderer<ModelData>() {  
//
//		  public Object render(ModelData model, String property, ColumnData config, int rowIndex, int colIndex,  
//		      ListStore<ModelData> store, Grid<ModelData> grid) {  
//		    return "<b>"  
//		        + model.get("institutionName")  
//		        + "</b>";  
//		  }  
//
//		});
		
		columns.add(getDisplayColumn("proxyIdCheckDigit",				"Proxy #",			80));  
		columns.add(getDisplayColumn("description",						"Description",		150));   
		columns.add(getDisplayColumn("searchKeys",						"Search Keys",		80));
		
		noteExpander = getNoteExpander("note");
		columns.add(noteExpander);
		
		ColumnModel cm = new ColumnModel(columns);

		grid = new Grid<ModelData>(store, cm);  
		grid.setBorders(true);  
		grid.setAutoExpandColumn("description");  
		grid.setLoadMask(true);
		grid.setStripeRows(true);
		grid.setColumnLines(true);
		
		//	Switch to the display card when a row is selected
		grid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE); 
		addRowListener(grid);
		
		addLocalGridFilters();

//		LiveGridView replaced with GridView because local filters don't work with a LiveGridView
//		liveView = new LiveGridView();  
//		liveView.setEmptyText("Enter filter criteria to search for IP addresses.");
//		liveView.setCacheSize(100);
//		grid.setView(liveView);
		
		gridView = new GridView();
		gridView.setEmptyText("Enter filter criteria to search for proxies.");
		grid.setView(gridView);
		grid.getAriaSupport().setLabelledBy(this.getHeader().getId() + "-label"); 
		
		addGridPlugins(grid);
		
		searchPanel.add(grid);
		  
	}
	
	protected void addLocalGridFilters() {
		columnFilters = new GridFilters();  
		columnFilters.setLocal(true);
		columnFilters.setAutoReload(false);
		
		columnFilters.addFilter(new NumericFilter("proxyIdCheckDigit"));
		columnFilters.addFilter(new NumericFilter("description"));
		columnFilters.addFilter(new StringFilter("searchKeys"));
		
		grid.addPlugin(columnFilters);
	}
	
	public void addGridPlugins(Grid<ModelData> grid) {
		grid.addPlugin(noteExpander);
	}
	
	protected void addRowListener(Grid<?> grid) {
		grid.addListener(Events.RowClick, new Listener<GridEvent<?>>() {
		      public void handleEvent(GridEvent<?> be) {
		    	  onRowSelected(be);
		      }
		    });
		grid.addListener(Events.SelectionChange, new Listener<GridEvent<?>>() {
		      public void handleEvent(GridEvent<?> be) {
		    	  onRowSelected(be);
		      }
		    });
		
		new KeyNav<GridEvent<?>>(grid) {
		      @Override
		      public void onUp(GridEvent<?> ce) {
		        onRowSelected((BeanModel) ce.getGrid().getSelectionModel().getSelectedItem());
		      }
		      
		      @Override
		      public void onDown(GridEvent<?> ce) {
	        	onRowSelected((BeanModel) ce.getGrid().getSelectionModel().getSelectedItem());
		      }
		    };
	}
	
	@Override
	public void onRowSelected(BeanModel beanModel) {
		if (beanModel != null)
			showProxy((ProxyInstance) beanModel.getBean());
	}
	
	protected void setThis() {
		this.setLayout(new FitLayout());
		this.setHeight(forceHeight);
		IconSupplier.setIcon(this, IconSupplier.getProxyIconName());
	}
	
	/**
	 * Instigate an asynchronous load with a filter value.
	 * @param filter
	 */
	protected void loadFiltered(String filter) {
		if (filter == null || filter.length() == 0 || filter.length() < MIN_FILTER_LENGTH)
			return;
		
		this.filter = filter;
		
		updateUserPortlet();
		
		proxyLoader.load();
	}
	
	/**
	 * Construct and return a loader to handle returning a list of institutions.
	 * @return
	 */
	protected PagingLoader<PagingLoadResult<ProxyInstance>> getAgreementLoader() {
		// proxy and reader  
		RpcProxy<PagingLoadResult<ProxyInstance>> proxy = new RpcProxy<PagingLoadResult<ProxyInstance>>() {  
			@Override  
			public void load(Object loadConfig, final AsyncCallback<PagingLoadResult<ProxyInstance>> callback) {
		    	
				// This could be as simple as calling userListService.getUsers and passing the callback
				// Instead, here the callback is overridden so that it can catch errors and alert the users.  Then the original callback is told of the failure.
				// On success, the original callback is just passed the onSuccess message, and the response (the list).
				
				AsyncCallback<SynchronizedPagingLoadResult<ProxyInstance>> myCallback = new AsyncCallback<SynchronizedPagingLoadResult<ProxyInstance>>() {
					public void onFailure(Throwable caught) {
						// Show the RPC error message to the user
						if (caught instanceof IllegalArgumentException)
							MessageBox.alert("Alert", caught.getMessage(), null);
						else if (caught instanceof ServiceNotReadyException)
								MessageBox.alert("Alert", "The " + caught.getMessage() + " is not available at this time.  Please try again in a few minutes.", null);
						else {
							MessageBox.alert("Alert", "Proxies search failed unexpectedly.", null);
							System.out.println(caught.getClass().getName());
							System.out.println(caught.getMessage());
						}
						callback.onFailure(caught);
					}

					public void onSuccess(SynchronizedPagingLoadResult<ProxyInstance> syncResult) {
						if(syncResult.getSyncId() != searchSyncId)
							return;
						
						PagingLoadResult<ProxyInstance> result = syncResult.getResult();
						if ( result.getData() == null || result.getData().size() == 0 ) {
							if (result.getTotalLength() > 0)
								gridView.setEmptyText(result.getTotalLength() + " proxies qualify (too many to display).<br/>Please enter filter criteria to narrow your search.");
							else if (filter.length() == 0)
								gridView.setEmptyText("Enter filter criteria to search for proxies.");
							else
								gridView.setEmptyText("Please enter filter criteria to narrow your search.");
						}
						callback.onSuccess(result);
					}
				};

//				final List<Filter> saveFilters = columnFilters.getFilterData();
//				columnFilters.clearFilters();
				
				searchSyncId = System.currentTimeMillis();
				if (setFilterParameters( (LoadConfig) loadConfig ))
					invokeSearchService((PagingLoadConfig) loadConfig, searchSyncId, myCallback);
		    }  
		};
		BeanModelReader reader = new BeanModelReader();
		
		// loader and store  
		PagingLoader<PagingLoadResult<ProxyInstance>> loader = new BasePagingLoader<PagingLoadResult<ProxyInstance>>(proxy, reader)
//			Use the below code if filtering will be done on the server site
//			{  
//		      @Override  
//		      protected Object newLoadConfig() {  
//		        BasePagingLoadConfig config = new BaseFilterPagingLoadConfig();  
//		        return config;  
//		      }  
//		    }
		;
		return loader;
	}
	
	/**
	 * Set any parameters to be passed to the backend service (through loadConfig properties) from any fields.
	 * 
	 * @param loadConfig
	 */
	public boolean setFilterParameters(LoadConfig loadConfig) {
//		if (fromDateField.getValue() == null || toDateField.getValue() == null || dateTypeField.getValue() == null)
//			return false;
		
		loadConfig.set("filter", filter);
		loadConfig.set("limit", LOAD_LIMIT);
//		loadConfig.set("id", filter);
		
		return true;
	}
 	
	public void invokeSearchService(PagingLoadConfig loadConfig, long searchSyncId, AsyncCallback<SynchronizedPagingLoadResult<ProxyInstance>> myCallback) {
		grid.mask("Searching for proxies...");
		proxySearchService.searchProxies((PagingLoadConfig) loadConfig, filter, searchSyncId, myCallback);
	}

	protected void loadProxy(final int proxyId) {
		proxyGetService.getProxy(proxyId, true,
				new AsyncCallback<ProxyTuple>() {
					public void onFailure(Throwable caught) {
						// Show the RPC error message to the user
						if (caught instanceof IllegalArgumentException)
							MessageBox.alert("Alert", caught.getMessage(), null);
						else {
							MessageBox.alert("Alert", "Proxy access failed unexpectedly.", null);
							System.out.println(caught.getClass().getName());
							System.out.println(caught.getMessage());
						}
					}

					public void onSuccess(ProxyTuple proxyTuple) {
						showProxy(proxyTuple);
					}
			});
	}
	
	@Override
	public void onExpand() {
		super.onExpand();
		awaken();
	}
	
	@Override
	public void onCollapse() {
		super.onCollapse();
		sleep();
	}
	
	/**
	 * Turn on the listener timer when waking up.
	 */
	@Override
	public void awaken() {
		if (this.isExpanded())
			if (filterListenTimer != null)
				filterListenTimer.scheduleRepeating(FILTER_LISTEN_PERIOD);
	}

	/**
	 * Turn off the listener timer when going to sleep.
	 */
	@Override
	public void sleep() {
		if (filterListenTimer != null) {
			filterListenTimer.cancel();
		}
	}

	@Override
	public void setAppPortletProvider(AppPortletProvider portletProvider) {
		this.portletProvider = portletProvider;
	}
	
	@Override
	public String getShortPortletName() {
		if (focusAgreementId > 0)
			return "Agreement " + focusAgreementId;
		if (filter == null || filter.length() == 0)
			return "Product Terms Search";
		if (filter.length() > 10)
			return "Agreements for " + filter.substring(0, 10);
		return "Agreements for " + filter;
	}
	
	@Override
	public boolean allowDuplicatePortlets() {
		//	Not allowed for a particular institution
		if (focusAgreementId > 0)
			return false;
		//	Allowed for any general search
		return true;
	}
	
	@Override
	public String getPortletIdentity() {
		if (focusAgreementId <= 0)
			return super.getPortletIdentity();
		return this.getClass().getName() + ":" + focusAgreementId;
	}

	public int getFocusAgreementId() {
		return focusAgreementId;
	}

	public void setFocusAgreementId(int focusAgreementId) {
		this.focusAgreementId = focusAgreementId;
	}

	public int getFocusProxyId() {
		return focusProxyId;
	}

	public void setFocusProxyId(int focusProxyId) {
		this.focusProxyId = focusProxyId;
	}

	public String getFilter() {
		return filter;
	}

	public void setFilter(String filter) {
		this.filter = filter;
	}

	@Override
	public void setFromKeyData(String keyData) {
		if (keyData == null)
			return;
		
		String oldFilter		= null;
		String oldAgreementId   = null;
		String oldTermId		= null;
		
		keyData = keyData.replace("\\:", "''''");	//	Remove any escaped :
		String [] parts = keyData.split(":");
		
		if (parts.length > 0)
			oldAgreementId = parts [0].trim();
		if (parts.length > 1)
			oldFilter = parts [1].trim().replace("''''", ":"); //	Restore any escaped :
		
		if (oldFilter != null && oldFilter.length() > 0) {
			filter = oldFilter;
		}
		
		if (oldAgreementId != null && oldAgreementId.length() > 0) {
			try {
				focusAgreementId = Integer.parseInt(oldAgreementId);
				focusProxyId = Integer.parseInt(oldTermId);
			} catch (NumberFormatException e) {
			}
		}
	}

	@Override
	public String getKeyData() {
		if (focusProxy == null)
			return "::" + filter;
		else	//	Note that we escape any ":" value in the filter with \\:
			return focusProxy.getProxyId() + ":" + filter.replace(":", "\\:");
	}

}