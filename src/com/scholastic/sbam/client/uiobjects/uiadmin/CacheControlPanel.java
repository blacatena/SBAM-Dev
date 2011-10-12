package com.scholastic.sbam.client.uiobjects.uiadmin;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.SortDir;
import com.extjs.gxt.ui.client.data.BaseListLoader;
import com.extjs.gxt.ui.client.data.BeanModelReader;
import com.extjs.gxt.ui.client.data.ListLoadResult;
import com.extjs.gxt.ui.client.data.ListLoader;
import com.extjs.gxt.ui.client.data.LoadConfig;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.BoxComponent;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.grid.CheckColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.grid.GridView;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.scholastic.sbam.client.services.CacheRefreshService;
import com.scholastic.sbam.client.services.CacheRefreshServiceAsync;
import com.scholastic.sbam.client.services.CacheStatusListService;
import com.scholastic.sbam.client.services.CacheStatusListServiceAsync;
import com.scholastic.sbam.client.uiobjects.foundation.AppSleeper;
import com.scholastic.sbam.client.util.IconSupplier;
import com.scholastic.sbam.client.util.UiConstants;
import com.scholastic.sbam.shared.objects.CacheStatusInstance;

public class CacheControlPanel extends LayoutContainer implements AppSleeper {
	
	private final CacheStatusListServiceAsync	cacheStatusListService	= GWT.create(CacheStatusListService.class);
	private final CacheRefreshServiceAsync		cacheRefreshService		= GWT.create(CacheRefreshService.class);

	protected	Grid<ModelData>			grid;
	protected	ListStore<ModelData>	store;
	
	protected ListLoader<ListLoadResult<ModelData>> cacheStatusLoader;
	
	Timer		refreshTimer;
	
	@Override
	public void onRender(Element element, int index) {
		super.onRender(element, index);
		
		addGrid();	
		
		addButtons();
		
		grid.getStore().getLoader().load();
	}
	
	public void addGrid() {

		cacheStatusLoader = getCacheStatusLoader(); 

		cacheStatusLoader.setSortDir(SortDir.ASC);  
		cacheStatusLoader.setSortField("name");  

		cacheStatusLoader.setRemoteSort(false);  

		store = new ListStore<ModelData>(cacheStatusLoader);  
 
		List<ColumnConfig> columns = new ArrayList<ColumnConfig>();  
		
		columns.add(getDisplayColumn("name",					"Name",				150,	null));
		columns.add(getDisplayColumn("countHeading",			"Type",				90,		null)); 
		columns.add(getGridCheckColumn("loading",				"Loading",			50,		null)); 
		columns.add(getGridCheckColumn("ready",					"Ready",			50,		null)); 
		columns.add(getDisplayColumn("expectedCount",			"Expected",			50,		true, 	NumberFormat.getFormat("BWZ"),	null));
		columns.add(getDisplayColumn("count",					"Count",			50,		true, 	NumberFormat.getFormat("BWZ"),	null));
		columns.add(getDisplayColumn("percentCompleted",		"Percent Complete",	50,		true, 	NumberFormat.getPercentFormat(),	null)); 
		columns.add(getDisplayColumn("countHeading2",			"Type",				90,		null)); 
		columns.add(getDisplayColumn("count2",					"Count",			50,		true, 	NumberFormat.getFormat("BWZ"),	null));
		columns.add(getDisplayColumn("countHeading3",			"Type",				90,		null)); 
		columns.add(getDisplayColumn("count3",					"Count",			50,		true, 	NumberFormat.getFormat("BWZ"),	null));
		columns.add(getDisplayColumn("countHeading4",			"Type",				90,		null)); 
		columns.add(getDisplayColumn("count4",					"Count",			50,		true, 	NumberFormat.getFormat("BWZ"),	null));
		columns.add(getRefreshButtonColumn(140)); 
		
		ColumnModel cm = new ColumnModel(columns);  

		grid = new Grid<ModelData>(store, cm);  
		grid.setBorders(true);  
		grid.setAutoExpandColumn("name");  
		grid.setLoadMask(true);
		grid.setStripeRows(true);
		grid.setColumnLines(true);
//		grid.setHeight(550);
		grid.setAutoHeight(true);
		
		GridView gridView= new GridView();
		gridView.setEmptyText("There are no caches to display.");
		grid.setView(gridView);

		add(grid);
	}
	
	protected void addButtons() {
		
		ToolBar toolBar = new ToolBar();
		toolBar.setAlignment(HorizontalAlignment.CENTER);
		
		Button refresh = new Button("Refresh List");
		refresh.setToolTip(UiConstants.getQuickTip("Use this button to refresh the display."));
		IconSupplier.forceIcon(refresh, IconSupplier.getRefreshIconName());
		refresh.addSelectionListener(new SelectionListener<ButtonEvent>() {  
			@Override
			public void componentSelected(ButtonEvent ce) {
				reloadGrid();
			}  
		});
		toolBar.add(refresh);
		
		add(toolBar);
	}
	
	protected void reloadGrid() {
		grid.mask();
		grid.getStore().getLoader().load();
	}
	
	protected ListLoader<ListLoadResult<ModelData>> getCacheStatusLoader() {
		// proxy and reader  
		RpcProxy<List<CacheStatusInstance>> proxy = new RpcProxy<List<CacheStatusInstance>>() {  
			@Override  
			public void load(Object loadConfig, final AsyncCallback<List<CacheStatusInstance>> callback) {
		    	
				// This could be as simple as calling userListService.getUsers and passing the callback
				// Instead, here the callback is overridden so that it can catch errors and alert the users.  Then the original callback is told of the failure.
				// On success, the original callback is just passed the onSuccess message, and the response (the list).
				
				AsyncCallback<List<CacheStatusInstance>> myCallback = new AsyncCallback<List<CacheStatusInstance>>() {
					public void onFailure(Throwable caught) {
						// Show the RPC error message to the user
						if (caught instanceof IllegalArgumentException)
							MessageBox.alert("Alert", caught.getMessage(), null);
						else {
							MessageBox.alert("Alert", "Data load failed unexpectedly.", null);
							System.out.println(caught.getClass().getName());
							System.out.println(caught.getMessage());
						}
						callback.onFailure(caught);
						grid.unmask();
					}

					public void onSuccess(List<CacheStatusInstance> list) {
						callback.onSuccess(list);
						grid.unmask();
						layout(true);
						scheduleTimer();
					}
				};
				
				asyncLoad(loadConfig, myCallback);
				
		    }  
		};
		BeanModelReader reader = new BeanModelReader();
		
		// loader and store  
		ListLoader<ListLoadResult<ModelData>> loader = new BaseListLoader<ListLoadResult<ModelData>>(proxy, reader);
		return loader;
	}

	protected void asyncLoad(Object loadConfig, AsyncCallback<List<CacheStatusInstance>> callback) {
		LoadConfig myLoadConfig = null;
		if (loadConfig instanceof LoadConfig)
			myLoadConfig = (LoadConfig) loadConfig;
		cacheStatusListService.listCacheStatus(myLoadConfig, callback);
	}
	
	protected ColumnConfig getDisplayColumn(String column, String heading, int size, String toolTip) {
		return getGridColumn(column, heading, size, false, true, null, null, toolTip);
	}
	
	protected ColumnConfig getDisplayColumn(String column, String heading, int size, boolean sortable, NumberFormat numberFormat, String toolTip) {
		return getGridColumn(column, heading, size, false, sortable, null, numberFormat, toolTip);
	}
	
	protected ColumnConfig getGridColumn(String column, String heading, int size, boolean hidden, boolean sortable, DateTimeFormat dateFormat, NumberFormat numberFormat, String toolTip) {
		ColumnConfig cc = new ColumnConfig(column,		heading, 		size);
		cc.setHidden(hidden);
		cc.setSortable(sortable);
		if (toolTip != null)
			cc.setToolTip(toolTip);
		if (dateFormat != null)
			cc.setDateTimeFormat(dateFormat);
		if (numberFormat != null) {
			cc.setAlignment(HorizontalAlignment.RIGHT);
			if (numberFormat.getPattern().equals("BWZ")) {
				// Special implementation for blank when zero
				cc.setRenderer(new GridCellRenderer<ModelData>() {  
				  public Object render(ModelData model, String property, ColumnData config, int rowIndex, int colIndex,  
				      ListStore<ModelData> store, Grid<ModelData> grid) {
					  if (model.get(property).toString().equals("0"))
						  return "";
					  return model.get(property);
				  }  
				});
			} else {
				cc.setNumberFormat(numberFormat);
			}
		}
		return cc;
	}
	
	protected ColumnConfig getGridCheckColumn(String name, String header, int width, String toolTip) {
		CheckColumnConfig checkColumn = new CheckColumnConfig(name, header, width); 
		if (toolTip != null && toolTip.length() > 0)
			checkColumn.setToolTip(toolTip);
//		CellEditor checkBoxEditor = new CellEditor(new CheckBox());
//		checkColumn.setEditor(checkBoxEditor);
		return checkColumn;
	}
	
	public GridCellRenderer<ModelData> getRefreshButtonRenderer() {
		GridCellRenderer<ModelData> buttonRenderer = new GridCellRenderer<ModelData>() {  

			private boolean init;  

			public Object render(final ModelData model, String property, ColumnData config, final int rowIndex,  
					final int colIndex, ListStore<ModelData> store, Grid<ModelData> grid) {  
				if (!init) {  
					init = true;  
					grid.addListener(Events.ColumnResize, new Listener<GridEvent<ModelData>>() {  

						public void handleEvent(GridEvent<ModelData> be) {  
							for (int i = 0; i < be.getGrid().getStore().getCount(); i++) {  
								if (be.getGrid().getView().getWidget(i, be.getColIndex()) != null  
										&& be.getGrid().getView().getWidget(i, be.getColIndex()) instanceof BoxComponent) {  
									((BoxComponent) be.getGrid().getView().getWidget(i, be.getColIndex())).setWidth(be.getWidth() - 10);  
								}  
							}  
						}  
					});  
				}  

				Button b = new Button("Refresh this Cache", new SelectionListener<ButtonEvent>() {  
					@Override  
					public void componentSelected(ButtonEvent ce) {  
						asyncRefreshCache(model.get("key").toString());  
					}  
				});  
				b.setWidth(grid.getColumnModel().getColumnWidth(colIndex) - 10);  
				b.setToolTip("Click to refresh this cache.");
				IconSupplier.forceIcon(b, IconSupplier.getCacheIconName());

				return b;  
			}  
		};

		return buttonRenderer;
	}
	
	public ColumnConfig getRefreshButtonColumn(int width) {
		ColumnConfig column = new ColumnConfig();  
	    column.setId("refresh");  
	    column.setHeader("Refresh");  
	    column.setWidth(width);  
	    column.setRenderer(getRefreshButtonRenderer()); 

	    return column;
	}

	protected void asyncRefreshCache(String cacheKey) {
	
		//	Issue the asynchronous update request and plan on handling the response
		cacheRefreshService.refreshCache(cacheKey,
				new AsyncCallback<CacheStatusInstance>() {
					public void onFailure(Throwable caught) {
						// Show the RPC error message to the user
						if (caught instanceof IllegalArgumentException)
							MessageBox.alert("Alert", caught.getMessage(), null);
						else {
							MessageBox.alert("Alert", "Cache refresh failed unexpectedly.", null);
							System.out.println(caught.getClass().getName());
							System.out.println(caught.getMessage());
						}
					}

					public void onSuccess(CacheStatusInstance refreshedCache) {
						ModelData model = CacheStatusInstance.obtainModel(refreshedCache);
						if (store.contains(model))
							store.update(model);
						scheduleTimer();
					}
				});
	}
	
	public void scheduleTimer() {
		if (store != null) {
			for (ModelData model : store.getModels()) {
				if (!"true".equals(model.get("ready").toString())) {
					if (refreshTimer == null) {
						refreshTimer = new Timer() {
							  @Override
							  public void run() {
								  reloadGrid();
							  }
						};
					}
					refreshTimer.schedule(30000);
					System.out.println("Rescheduled");
					return;
				}
			}
		}
	}
	
	@Override
	public void awaken() {
		scheduleTimer();
	}

	@Override
	public void sleep() {
		if (refreshTimer != null) {
			refreshTimer.cancel();
		}
	}
}
