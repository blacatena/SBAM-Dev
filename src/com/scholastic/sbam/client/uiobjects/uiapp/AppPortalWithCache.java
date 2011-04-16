package com.scholastic.sbam.client.uiobjects.uiapp;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.PortalEvent;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.custom.Portal;
import com.extjs.gxt.ui.client.widget.custom.Portlet;

public class AppPortalWithCache extends Portal {
	
	protected int nextPortalId = 0;

	public AppPortalWithCache(int numColumns) {
		super(numColumns);
		addDragListener();
	}

	/**
	 * Add a portlet to the portal and register it in the portlet cache.
	 * @param portlet
	 * @param index
	 * @param column
	 */
	@Override
	public void insert(Portlet portlet, int index, int column) {
		super.insert(portlet, index, column);
		if (portlet instanceof AppPortlet) {
			AppPortlet appPortlet = (AppPortlet) portlet;
			appPortlet.registerUserPortlet(nextPortalId, index, column);
		}
		nextPortalId++;
	}
	
	/**
	 * Add a portlet to the portal without registering it (because it came from the portlet cache to begin with).
	 * @param portlet
	 * @param index
	 * @param column
	 */
	public void reinsert(Portlet portlet, int index, int column) {
		//	Use this to fix problems where cached portlet locations are out of bounds
		if (column >= this.getItemCount())
			column = getItemCount() - 1;
		if (index > this.getItem(column).getItemCount())
			index = this.getItem(column).getItemCount();
		
		super.insert(portlet, index, column);
		if (portlet instanceof AppPortlet) {
			AppPortlet appPortlet = (AppPortlet) portlet;
		//	appPortlet.registerUserPortlet(appPortlet.getPortletId(), index, column);
			appPortlet.setPortalColumn(column);
			appPortlet.setPortalRow(index);
			if (appPortlet.getPortletId() >= nextPortalId)
				nextPortalId = appPortlet.getPortletId() + 1;
		}
	}
	
	public void addDragListener() {
		this.addListener(Events.Drop, new Listener<PortalEvent>() {
			@Override
			public void handleEvent(PortalEvent pe) {
				if (pe.getEventTypeInt() == Events.Drop.getEventCode()) {
					// It's necessary to refresh everything, to account not only for this move, but how it affected all other portlets in the from and to columns
					// This could be optimized to only update those two columns (startColumn, column), but why bother?
					refreshAllPortletStates();
				}
			}
		});
	}
	
	/**
	 * Update the position and state of all porlets.
	 */
	public void refreshAllPortletStates() {
		
		for (int col = 0; col < this.getItemCount(); col++) {
		    LayoutContainer con = getItem(col);
		    List<Component> list = new ArrayList<Component>(con.getItems());
		    for (int row = 0; row < list.size(); row++) {
		    	if (list.get(row) instanceof AppPortlet) {
					AppPortlet appPortlet = (AppPortlet) list.get(row);
					appPortlet.updateUserPortlet(row, col);
		    	}
		    }
		}
	}
}
