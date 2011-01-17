package com.scholastic.sbam.client.uiobjects;

import com.extjs.gxt.ui.client.widget.Composite;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.extjs.gxt.ui.client.widget.grid.EditorGrid;
import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.event.ColumnModelEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import java.util.List;
import java.util.ArrayList;
import com.extjs.gxt.ui.client.widget.layout.CenterLayout;
import com.extjs.gxt.ui.client.widget.layout.AnchorLayout;
import com.extjs.gxt.ui.client.widget.layout.FillLayout;
import com.extjs.gxt.ui.client.Style.Orientation;

public class CancelReasonEditGrid extends Composite {
	
	private EditorGrid<BeanModel> grid;
	ContentPanel layoutContainer;

	public CancelReasonEditGrid() {
		
		LayoutContainer layoutContainer = new LayoutContainer();
		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();
		layoutContainer.setLayout(new FillLayout(Orientation.VERTICAL));
		
		ColumnConfig clmncnfgstColumn = new ColumnConfig("id", "1st Column", 60);
		configs.add(clmncnfgstColumn);
		
		ColumnConfig clmncnfgndColumn = new ColumnConfig("id", "2nd Column", 200);
		configs.add(clmncnfgndColumn);
		
		ColumnConfig clmncnfgrdColumn = new ColumnConfig("id", "3rd Column", 100);
		configs.add(clmncnfgrdColumn);
		
		ColumnConfig clmncnfgthColumn = new ColumnConfig("id", "4th Column", 150);
		configs.add(clmncnfgthColumn);
		
		grid = new EditorGrid<BeanModel>(new ListStore<BeanModel>(), new ColumnModel(configs));
		layoutContainer.add(grid);
//		grid.setWidth("81px");
		grid.setWidth(grid.getColumnModel().getTotalWidth() + 20);
		grid.setBorders(false);
		
//		grid.addListener(Events.ViewReady, new Listener<ComponentEvent>() {
//		      public void handleEvent(ComponentEvent be) {
////		        grid.getStore().addListener(Store.Add, new Listener<StoreEvent<Plant>>() {
////		          public void handleEvent(StoreEvent<Plant> be) {
////		            doAutoHeight();
////		          }
////		        });
//		        doAutoHeight();
//		      }
//		    });
//		grid.addListener(Events.ColumnResize, new Listener<ComponentEvent>() {
//		      public void handleEvent(ComponentEvent be) {
//		        doAutoHeight();
//		      }
//		    });
//		grid.getColumnModel().addListener(Events.HiddenChange, new Listener<ColumnModelEvent>() {
//		      public void handleEvent(ColumnModelEvent be) {
//		        doAutoHeight();
//		      }
//		    });
		
		initComponent(layoutContainer);
		layoutContainer.setBorders(false);
	}



	  protected void doAutoHeight() {
	    if (grid.isViewReady()) {
	    	if (grid.getParent() != null) {
	    		int rowHeight = grid.getView().getBody().firstChild() != null ? grid.getView().getBody().firstChild().getHeight() : 0;
	    		grid.getParent().setHeight(((grid.getView().getBody().isScrollableX() ? 19 : 0) + grid.el().getFrameWidth("tb")
		          + rowHeight + grid.getView().getBody().firstChild().getHeight()) + "px");
	    	}
	    }
	  }
}
