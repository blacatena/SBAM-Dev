package com.scholastic.sbam.client.uiobjects;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.core.XTemplate;
import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.BeanModelFactory;
import com.extjs.gxt.ui.client.data.BeanModelLookup;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.grid.RowExpander;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.scholastic.sbam.client.util.IconSupplier;

/**
 * A base class which provides useful helper methods for any portlet that will be using grids.
 * 
 * Classes include methods for creating grid columns and for constructing addresses in HTML.
 * 
 * @author Bob Lacatena
 *
 * @param <I>
 */
public abstract class GridSupportPortlet<I> extends FieldSupportPortlet {
	
	public GridSupportPortlet(String helpTextId) {
		super(helpTextId);
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
	
	protected RowExpander getNoteExpander() {
		XTemplate tpl = XTemplate.create("<p style=\"padding: 10px\"><b>Note:</b> {note}</p>");  

		RowExpander noteExpander = new RowExpander() {
			@Override
			protected void onMouseDown(GridEvent<?> e) {
				if (e.getModel() != null && e.getModel().get("note") != null && e.getModel().get("note").toString().length() > 0) {
					super.onMouseDown(e);
				}
			}
		};
		noteExpander.setTemplate(tpl);
		
		/**
		 * This renderer must replace the one set up by RowExpander, so it must do the same thing, as well as add the Note icon
		 */
		final GridCellRenderer<BeanModel> noteRenderer = new GridCellRenderer<BeanModel>() {
			@Override
			public Object render(final BeanModel model, final String property, final ColumnData config, final int rowIndex,
					final int colIndex, final ListStore<BeanModel> store, final Grid<BeanModel> grid) {
				boolean hasNote = (model.get("note") != null && model.get("note").toString().length() > 0);
				config.style = "";
				if (hasNote) {
					config.cellAttr = "rowspan='2'";
					config.style = "background: url(" + IconSupplier.getColorfulIconPath(IconSupplier.getNoteIconName()) +
									") no-repeat 10px 2px !important;";
			        return "<div class='x-grid3-row-expander'>&#160;</div>";
				}
				return "";
			}
		};
		noteExpander.setWidth(32);
		noteExpander.setRenderer(noteRenderer);
		noteExpander.setToolTip("Click the notes icon to expand or collapse the row to display any notes.");
//		noteExpander.setId("showNotes");

		return noteExpander;
	}
	
	protected void addRowListener(Grid<?> grid) {
		grid.addListener(Events.RowClick, new Listener<GridEvent<?>>() {
		      public void handleEvent(GridEvent<?> be) {
		        onRowSelected(be);
		      }
		    });
	}
	
	protected boolean hasNote(ModelData model) {
		return model != null && model.get("note") != null && model.get("note").toString().length() > 0;
	}
	
	protected void onRowSelected(GridEvent<?> be) {
		if (!be.getTarget().getClassName().equals("x-grid3-row-expander") || !hasNote(be.getModel()))
			onRowSelected(be.getModel());
		// else do nothing... handled later by RowExpander
	}
	
	/**
	 * Override this method to provide functionality when a row is selected but not expanded/collapsed.
	 * @param model
	 */
	protected void onRowSelected(ModelData model) {
	}
}