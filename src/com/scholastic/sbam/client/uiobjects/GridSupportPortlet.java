package com.scholastic.sbam.client.uiobjects;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.BeanModelFactory;
import com.extjs.gxt.ui.client.data.BeanModelLookup;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;

public abstract class GridSupportPortlet<I> extends AppPortlet {
	
	public GridSupportPortlet(String helpTextId) {
		super(helpTextId);
	}

	protected String plusIfNotEmpty(String value, String prefix) {
		if (value == null || value.length() == 0)
			return "";
		return prefix + value;
	}
	
	protected String brIfNotEmpty(String value) {
		return plusIfNotEmpty(value, "<br/>");
	}
	
	protected String commaIfNotEmpty(String value) {
		return plusIfNotEmpty(value, ", ");
	}
	
	protected String spaceIfNotEmpty(String value) {
		return plusIfNotEmpty(value, "&nbsp;&nbsp;&nbsp;");
	}
	
	protected String brIfNotUsa(String value) {
		if (value == null || value.length() == 0)
			return "";
		if (value.equalsIgnoreCase("USA"))
			return "";
		return "<br/>" + value;
	}

	
	protected BeanModel getModel(I instance) {
		BeanModelFactory factory = BeanModelLookup.get().getFactory(instance.getClass());
		BeanModel model = factory.createModel(instance);
		return model;
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
	
	protected ColumnConfig getHiddenColumn(String column, String heading, int size, boolean sortable) {
		return getGridColumn(column, heading, size, true, sortable, null, null);
	}
	
	protected ColumnConfig getHiddenColumn(String column, String heading, int size, boolean sortable, DateTimeFormat dateFormat) {
		return getGridColumn(column, heading, size, true, sortable, dateFormat, null);
	}
	
	protected ColumnConfig getHiddenColumn(String column, String heading, int size, boolean sortable, NumberFormat numberFormat) {
		return getGridColumn(column, heading, size, true, sortable, null, numberFormat);
	}
	
	protected ColumnConfig getGridColumn(String column, String heading, int size, boolean hidden, boolean sortable, DateTimeFormat dateFormat, NumberFormat numberFormat) {
		return getGridColumn(column, heading, size, hidden, sortable, dateFormat, numberFormat, null);
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

	/**
	 * Go to sleep when collapsed.
	 */
	@Override
	public void onCollapse() {
		super.onCollapse();
		sleep();
	}
	
	/**
	 * Wake up when expanded.
	 */
	@Override
	public void onExpand() {
		super.onExpand();
		awaken();
	}
	
	public abstract void awaken();
	
	public abstract void sleep();

}