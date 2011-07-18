package com.scholastic.sbam.client.uiobjects.uireports;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.grid.filters.DateFilter;
import com.extjs.gxt.ui.client.widget.grid.filters.GridFilters;
import com.extjs.gxt.ui.client.widget.grid.filters.ListFilter;
import com.extjs.gxt.ui.client.widget.grid.filters.NumericFilter;
import com.extjs.gxt.ui.client.widget.grid.filters.StringFilter;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.scholastic.sbam.client.services.SnapshotTakeService;
import com.scholastic.sbam.client.services.SnapshotTakeServiceAsync;
import com.scholastic.sbam.client.util.IconSupplier;
import com.scholastic.sbam.client.util.UiConstants;
import com.scholastic.sbam.shared.objects.SnapshotInstance;

public class TermReportViewDataCard extends SnapshotCardBase {
	
	/**
	 * The BetterListFilter improves on the ListFilter by properly distinguishing between a display value and a key value.
	 * 
	 * The valueProperty may be set to the name of whatever property uniquely identifies an instance in a list of instances.
	 * 
	 * The displayProperty, as before, can be set to the name of whatever property should be displayed to the user to identify an instance.
	 * 
	 * @author Bob Llacatena
	 *
	 * @param <M>
	 */
	@SuppressWarnings({"unchecked", "rawtypes"})
	public class BetterListFilter<M> extends ListFilter {
		
		private String valueProperty;

		public BetterListFilter(String dataIndex, ListStore store) {
			super(dataIndex, store);
		}

		@Override
		protected <X> X getModelValue(ModelData model) {
			return (X) ((ModelData) model.get(dataIndex)).get(valueProperty);
		}

		public String getValueProperty() {
			return valueProperty;
		}

		public void setValueProperty(String valueProperty) {
			this.valueProperty = valueProperty;
		}
	}

	protected ContentPanel						contentPanel = new ContentPanel();
	
	protected ListStore<BeanModel>				gridStore;
	protected Grid<BeanModel>					grid;
	protected GridFilters 						gridFilters;
	
	protected final SnapshotTakeServiceAsync 	takeSnapshotService = GWT.create(SnapshotTakeService.class);
	
	public TermReportViewDataCard() {
		super();
		this.headingToolTip = "Use this panel to view a terms based report.";
	}

	@Override
	public void addPanelContent() {
//		contentPanel = new ContentPanel();
		contentPanel.setHeading("Snapshot Terms Data View");
		IconSupplier.setIcon(contentPanel, IconSupplier.getReportIconName());
		
		grid = getGrid(); 
		contentPanel.add(grid);
		
		add(contentPanel);
	}

	public void addGridColumns(List<ColumnConfig> columns) {
		columns.add(getDisplayColumn("rowId",					"Row",						30,
					"A unique number for the data row."));
		columns.add(getDisplayColumn("agreementIdCheckDigit",	"Agreement #",				80,
					"This is the agreement number for this service."));
		columns.add(getDisplayColumn("ucn",						"UCN",						80,
					"This is the recipient UCN for this service."));
		columns.add(getDisplayColumn("institution.institutionName",	"Institution",			150,
					"This is the recipient institution for this service."));
		columns.add(getDisplayColumn("product.description",		"Product",					150,
					"This is the product for this service."));
		columns.add(getDisplayColumn("service.description",		"Service",					150,
					"This is the service."));
		columns.add(getDisplayColumn("startDate",				"Start",					80,		true, UiConstants.APP_DATE_TIME_FORMAT,
					"This is the service start date for a product term."));
		columns.add(getDisplayColumn("endDate",					"End",						80,		true, UiConstants.APP_DATE_TIME_FORMAT,
					"This is the service end date for a product term."));
		columns.add(getDisplayColumn("terminateDate",			"Terminate",				80,		true, UiConstants.APP_DATE_TIME_FORMAT,
					"This is the actual service termination date for a product term."));
		columns.add(getDisplayColumn("dollarValue",				"Full Value",				80,		true, UiConstants.DOLLARS_FORMAT,
					"This is the full value of the product term."));
		columns.add(getDisplayColumn("dollarFraction",			"Row Value",				80,		true, UiConstants.DOLLARS_FORMAT,
					"This is the fraction of the total term value for this particular service for this UCN."));
		columns.add(getHiddenColumn("dollarServiceFraction",	"Service Value",			80,		true, UiConstants.DOLLARS_FORMAT,
					"This is the fraction of the total term value for this particular service."));
		columns.add(getHiddenColumn("dollarUcnFraction",		"UCN Value",				80,		true, UiConstants.DOLLARS_FORMAT,
					"This is the fraction of the total term value for this particular UCN."));
		columns.add(getDisplayColumn("termType.description",	"Type",						50,
					"This is the type of product term."));
	}
	
	protected Grid<BeanModel> getGrid() {

		gridFilters = new GridFilters(); // Have to create this here, before adding the grid columns, so the filters get created with the columns
		
		List<ColumnConfig> columns = new ArrayList<ColumnConfig>();
		
		addGridColumns(columns);
		
		ColumnModel cm = new ColumnModel(columns);  

		gridStore = getNewGridStore();
		
		grid = new Grid<BeanModel>(gridStore, cm); 
//		grid.addPlugin(expander);
		grid.setBorders(true);  
//		grid.setAutoExpandColumn(autoExpandColumn);  
		grid.setLoadMask(true);
		grid.setStripeRows(true);
		grid.setColumnLines(false);
		grid.setHideHeaders(false);
		
		gridFilters.setLocal(areGridFiltersLocal()); 
		grid.addPlugin(gridFilters);

		grid.setAutoExpandColumn("");
//		addGridPlugins(grid);	
//		addRowListener(grid);
		
		grid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

		return grid;
	}
	
	protected ColumnConfig getDisplayColumn(String column, String heading, int size) {
		return getGridColumn(column, heading, size, false, true, null, null);
	}
	
	protected ColumnConfig getDisplayColumn(String column, String heading, int size, String toolTip) {
		return getGridColumn(column, heading, size, false, true, null, null, toolTip);
	}
	
	protected ColumnConfig getDisplayColumn(String column, String heading, int size, boolean sortable) {
		return getGridColumn(column, heading, size, false, sortable, null, null);
	}
	
	protected ColumnConfig getDisplayColumn(String column, String heading, int size, boolean sortable, String toolTip) {
		return getGridColumn(column, heading, size, false, sortable, null, null, toolTip);
	}
	
	protected ColumnConfig getDisplayColumn(String column, String heading, int size, boolean sortable, DateTimeFormat dateFormat) {
		return getGridColumn(column, heading, size, false, sortable, dateFormat, null);
	}
	
	protected ColumnConfig getDisplayColumn(String column, String heading, int size, boolean sortable, NumberFormat numberFormat) {
		return getGridColumn(column, heading, size, false, sortable, null, numberFormat);
	}
	
	protected ColumnConfig getDisplayColumn(String column, String heading, int size, boolean sortable, DateTimeFormat dateFormat, String toolTip) {
		return getGridColumn(column, heading, size, false, sortable, dateFormat, null, toolTip);
	}
	
	protected ColumnConfig getDisplayColumn(String column, String heading, int size, boolean sortable, NumberFormat numberFormat, String toolTip) {
		return getGridColumn(column, heading, size, false, sortable, null, numberFormat, toolTip);
	}
	
	protected ColumnConfig getHiddenColumn(String column, String heading, int size) {
		return getGridColumn(column, heading, size, true, true, null, null);
	}
	
	protected ColumnConfig getHiddenColumn(String column, String heading, int size, String toolTip) {
		return getGridColumn(column, heading, size, true, true, null, null, toolTip);
	}
	
	protected ColumnConfig getHiddenColumn(String column, String heading, int size, boolean sortable) {
		return getGridColumn(column, heading, size, true, sortable, null, null);
	}
	
	protected ColumnConfig getHiddenColumn(String column, String heading, int size, boolean sortable, String toolTip) {
		return getGridColumn(column, heading, size, true, sortable, null, null, toolTip);
	}
	
	protected ColumnConfig getHiddenColumn(String column, String heading, int size, boolean sortable, DateTimeFormat dateFormat) {
		return getGridColumn(column, heading, size, true, sortable, dateFormat, null);
	}
	
	protected ColumnConfig getHiddenColumn(String column, String heading, int size, boolean sortable, NumberFormat numberFormat) {
		return getGridColumn(column, heading, size, true, sortable, null, numberFormat);
	}
	
	protected ColumnConfig getHiddenColumn(String column, String heading, int size, boolean sortable, DateTimeFormat dateFormat, String toolTip) {
		return getGridColumn(column, heading, size, true, sortable, dateFormat, null, toolTip);
	}
	
	protected ColumnConfig getHiddenColumn(String column, String heading, int size, boolean sortable, NumberFormat numberFormat, String toolTip) {
		return getGridColumn(column, heading, size, true, sortable, null, numberFormat, toolTip);
	}
	
	protected ColumnConfig getGridColumn(String column, String heading, int size, boolean hidden, boolean sortable, DateTimeFormat dateFormat, NumberFormat numberFormat) {
		return getGridColumn(column, heading, size, hidden, sortable, dateFormat, numberFormat, null);
	}
	
	protected ColumnConfig getGridColumn(String column, String heading, int size, boolean hidden, boolean sortable, DateTimeFormat dateFormat, NumberFormat numberFormat, String toolTip) {
		ColumnConfig cc = new ColumnConfig(column,		heading, 		size);
		cc.setId(column);
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
		
		if (gridFilters != null) {
			if (dateFormat != null)
				gridFilters.addFilter(new DateFilter(column));
			else if (numberFormat != null)
				gridFilters.addFilter(new NumericFilter(column));
			else
				gridFilters.addFilter(new StringFilter(column));
		}
		
		return cc;
	}
	
	protected ColumnConfig getDisplayColumn(String name, String header, int width, String toolTip, ListStore<BeanModel> listStore) {
		return getDisplayColumn(name, header, width, toolTip, listStore, "code", "name");
	}
	
	protected ColumnConfig getDisplayColumn(String name, String header, int width, String toolTip, ListStore<BeanModel> listStore, String valueField, String displayField) {
		
		ColumnConfig column = getDisplayColumn(name, header, width, toolTip);
		
		if (gridFilters != null) {
			BetterListFilter<BeanModel> listFilter = new BetterListFilter<BeanModel>(name, listStore);   
			listFilter.setDisplayProperty(displayField); 
			listFilter.setValueProperty(displayField);
			gridFilters.addFilter(listFilter);
		}
	
		return column; 
	}
	
	public boolean areGridFiltersLocal() {
		return true;
	}
	
	public ListStore<BeanModel> getNewGridStore() {
		return new ListStore<BeanModel>();
	}
	
	@Override
	public ContentPanel getContentPanel() {
		return contentPanel;
	}
	
	@Override
	public void setSnapshot(SnapshotInstance snapshot) {
		super.setSnapshot(snapshot);
		if (snapshot.getSnapshotTaken() == null) {
			takeSnapshot();
		}
	}
	
	protected void takeSnapshot() {
		if (snapshot.getSnapshotTaken() != null)
			return;
		
		contentPanel.mask("Please wait while the snapshot is compiled.");
		
		takeSnapshotService.takeSnapshot(snapshot.getSnapshotId(),
				new AsyncCallback<Date>() {
					public void onFailure(Throwable caught) {
						// Show the RPC error message to the user
						if (caught instanceof IllegalArgumentException)
							MessageBox.alert("Alert", caught.getMessage(), null);
						else {
							MessageBox.alert("Alert", "Take snapshot failed unexpectedly.", null);
							System.out.println(caught.getClass().getName());
							System.out.println(caught.getMessage());
						}
						contentPanel.unmask();
					}

					public void onSuccess(Date result) {
						snapshot.setSnapshotTaken(result);
						if (parentCardPanel != null) {
							parentCardPanel.reflectSnapshotChanges(snapshot);
						}
						contentPanel.unmask();
					}
				});
	}
	
//	protected ContentPanel getNewContentPanel() {
//		return new ContentPanel() {
//			private boolean firstExpand = true;
//			/*
//			 * This panel has to take care of telling the grid panel to grow or shrink as it renders/resizes/expands/collapses
//			 * (non-Javadoc)
//			 */
//			
//			@Override
//			protected void afterRender() {
//				super.afterRender();
//				adjustGridPanelHeight();
//			//	dumpSizes("FormPanel afterRender");
//			}
//			
//			@Override
//			public void onResize(int width, int height) {
//				super.onResize(width, height);
//				adjustGridPanelHeight();
//			//	dumpSizes("FormPanel onResize " + width);
//			}
//			
//			@Override
//			public void onCollapse() {
//				super.onCollapse();
//				// Resize in anticipation of what it WILL be after collapse
//				if (gridPanel != null && gridPanel.isRendered()) {
//					if (isHeaderVisible())
//						gridPanel.setHeight(mainContainer.getHeight(true) - getHeader().getOffsetHeight());
//					else
//						gridPanel.setHeight(mainContainer.getHeight(true));
//				}
//			}
//			
//			@Override
//			public void afterCollapse() {
//				super.afterCollapse();
//				adjustGridPanelHeight();
//			}
//			
//			@Override
//			public void afterExpand() {
//				super.afterExpand();
//				adjustGridPanelHeight();
//				if (firstExpand) {
//					adjustFormPanelSize(-1, -1);
//					firstExpand = false;
//				}
//			}
//			
//			@Override
//			public void onAfterLayout() { // This is critical... this is what makes sure that the grid panel gets resized after the formPanel actually has a size
//				super.onAfterLayout();
//				adjustGridPanelHeight();
//			}
//			
//		};
//	}
//	
//	public void adjustGridPanelHeight() {
//		if (gridPanel == null || !gridPanel.isRendered())
//			return;
//		
//		if (formPanel == null || !formPanel.isRendered())
//			gridPanel.setHeight(mainContainer.getHeight(true));
//		else
//			if (formPanel.isExpanded())
//				gridPanel.setHeight(mainContainer.getHeight(true) - formPanel.getHeight());
//			else
//				if (formPanel.isHeaderVisible())
//					gridPanel.setHeight(mainContainer.getHeight(true) - formPanel.getHeader().getOffsetHeight());
//				else
//					gridPanel.setHeight(mainContainer.getHeight(true));
//	}
	
}
