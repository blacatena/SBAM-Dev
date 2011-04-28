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
import com.google.gwt.user.client.ui.Widget;

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
	
		/** TODO
		 * 	Figure out how to scroll portlets into view when they're inserted.
		 */
/*	
 * 		NONE OF THIS WORKS PROPERLY... it won't scroll all the way over, even though it seems to...
 * 	
 * 		It seems the right object to scroll is this.getParent() (i.e. the container holding the Portal),
 * 		since everything else is zero (see dump(Widget)), but even that doesn't work
 * 
 * 		This is the code we could/should use (using the local scrollIntoView method hack, to make it scroll horizontally)...
 * 
		//	If the portal is in a scrollable container, scroll to this portal
		if (portlet.getParent() != null) {
			// Scroll portlet into view in portal
			if (this.getParent() != null)
				scrollIntoView(this.getParent(), portlet);
			// Scroll column into view
			if (portlet.getParent().getParent() != null)
				scrollIntoView(portlet.getParent().getParent(), portlet.getParent());
			// Scroll portlet into view in column
			scrollIntoView(portlet.getParent(), portlet);
		}

*		This is the more direct code that should at least scroll to the top left of the parent container
*
		if (this.getParent() != null) scrollTo(this.getParent(), 0, 0);
		
*		This is the code that should work, using the more proper container.scrollIntoView(target) method, except for the horizontal scroll problem
*
//		if (portlet.getParent() != null && portlet.getParent() instanceof LayoutContainer) {
//			// Scroll column into view
//			if (portlet.getParent().getParent() != null && portlet.getParent() instanceof LayoutContainer)
//				( (LayoutContainer) portlet.getParent()).scrollIntoView(portlet.getParent());
//			// Scroll portlet into view in column
//			( (LayoutContainer) getParent()).scrollIntoView(portlet);
//			// In case this is necessary, scroll portlet into view in portal
//			if (portlet.getParent().getParent() != null && portlet.getParent() instanceof LayoutContainer)
//				( (LayoutContainer) portlet.getParent()).scrollIntoView(portlet);
//		}
 * 
 */
		nextPortalId++;
	}
	
	public void dump(Widget thing) {
		if (thing != null && thing instanceof Component) {
			System.out.println(thing);
			Component container = (Component) thing;
			if (container.isRendered()) {
				System.out.println(container.getClass().getName());
				System.out.println("H: " + container.getOffsetHeight());
				System.out.println("W: " + container.getOffsetWidth());
				System.out.println("T: " + container.getAbsoluteTop());
				System.out.println("L: " + container.getAbsoluteLeft());
				System.out.println("SH:" + container.getElement().getScrollHeight());
				System.out.println("SW:" + container.getElement().getScrollWidth());
				System.out.println("ST:" + container.getElement().getScrollTop());
				System.out.println("SL:" + container.getElement().getScrollLeft());
			}
		}
	}
	
	public void scrollTo(Widget thing, int top, int left) {
		if (thing != null && thing instanceof Component) {
			Component container = (Component) thing;
			if (container.isRendered()) {
				container.el().setScrollTop(top);
				container.el().setScrollLeft(left);
			}
		}
	}
	
	public void scrollIntoView(Widget containerWidget, Widget componentWidget) {
		if (containerWidget instanceof Component && componentWidget instanceof Component) {
			Component container = (Component) containerWidget;
			Component component = (Component) componentWidget;
		    if (container.isRendered() && component.isRendered()) {
		    	component.el().scrollIntoView(container.el().dom, true);
		    }
		}
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
