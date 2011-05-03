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
import com.scholastic.sbam.shared.objects.UserPortletCacheInstance;

public class AppPortalWithCache extends Portal implements AppPortletPresenter {
	
	protected int nextPortalId = 0;

	public AppPortalWithCache(int numColumns) {
		super(numColumns);
		addDragListener();
	}

	@Override
	public void add(Portlet portlet, int column) {
		super.add(portlet, column);
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
		scrollToPortlet(portlet);
		nextPortalId++;
	}
	
	public void scrollToPortlet(Portlet portlet) {
/*	
 * 		NONE OF THIS WORKS PROPERLY... it won't scroll all the way over, even though it seems to...
 * 	
 * 		It seems the right object to scroll is this.getParent() (i.e. the container holding the Portal),
 * 		since everything else is zero (see dump(Widget)), but even that doesn't work
 * 
 */
		
		if (this.getParent() != null && this.getParent() instanceof LayoutContainer) {
			LayoutContainer parent = (LayoutContainer) this.getParent();
			parent.setHScrollPosition(portlet.getPosition(true).x);
			parent.setVScrollPosition(portlet.getPosition(true).y);
		}
		
/* 		This is the code we could/should use (using the local scrollIntoView method hack, to make it scroll horizontally)...
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
/**/
		
/*		This is the more direct code that should at least scroll to the top left of the parent container
*
		if (this.getParent() != null) scrollTo(this.getParent(), -1000, -1000);
/**/		
		
/*		This is the code that should work, using the more proper container.scrollIntoView(target) method, except for the horizontal scroll problem
*
		if (portlet.getParent() != null && portlet.getParent() instanceof LayoutContainer) {
			// Scroll column into view
			if (portlet.getParent().getParent() != null && portlet.getParent() instanceof LayoutContainer)
				( (LayoutContainer) portlet.getParent()).scrollIntoView(portlet.getParent());
			// Scroll portlet into view in column
			( (LayoutContainer) getParent()).scrollIntoView(portlet);
			// In case this is necessary, scroll portlet into view in portal
			if (portlet.getParent().getParent() != null && portlet.getParent() instanceof LayoutContainer)
				( (LayoutContainer) portlet.getParent()).scrollIntoView(portlet);
		}
 * 
/**/
		
	}
	
//	public void dump(Widget thing) {
//		if (thing != null && thing instanceof Component) {
//			System.out.println(thing);
//			Component container = (Component) thing;
//			if (container.isRendered()) {
//				System.out.println(container.getClass().getName());
//				System.out.println("H: " + container.getOffsetHeight());
//				System.out.println("W: " + container.getOffsetWidth());
//				System.out.println("T: " + container.getAbsoluteTop());
//				System.out.println("L: " + container.getAbsoluteLeft());
//				System.out.println("SH:" + container.getElement().getScrollHeight());
//				System.out.println("SW:" + container.getElement().getScrollWidth());
//				System.out.println("ST:" + container.getElement().getScrollTop());
//				System.out.println("SL:" + container.getElement().getScrollLeft());
//			}
//		}
//	}
//	
//	public void scrollTo(Widget thing, int top, int left) {
//		if (thing != null && thing instanceof LayoutContainer) {
//			LayoutContainer container = (LayoutContainer) thing;
//			System.out.println(thing.getClass().getName());
//			container.setHScrollPosition(left);
//			container.setVScrollPosition(top);
//			return;
//		} else if (thing != null && thing instanceof Component) {
//			Component container = (Component) thing;
//			if (container.isRendered()) {
//				container.el().setScrollTop(top);
//				container.el().setScrollLeft(left);
//			}
//		}
//	}
//	
//	public void scrollIntoView(Widget containerWidget, Widget componentWidget) {
//		if (containerWidget instanceof Component && componentWidget instanceof Component) {
//			Component container = (Component) containerWidget;
//			Component component = (Component) componentWidget;
//		    if (container.isRendered() && component.isRendered()) {
//		    	component.el().scrollIntoView(container.el().dom, true);
//		    }
//		}
//	}
	
	/**
	 * Add a portlet to the portal without registering it (because it came from the portlet cache to begin with).
	 * @param portlet
	 * @param index
	 * @param column
	 */
	public void reinsert(Portlet portlet, int index, int column, int portletId) {
		if (portlet instanceof AppPortlet) {
			( (AppPortlet) portlet).setPortletId(portletId);
			( (AppPortlet) portlet).setPresenter(this);
		}
		
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
	
	@Override
	public void updateLabel(Portlet portlet) {
		//	Nothing to do, because a portal doesn't use labels
	}
	
	@Override
	public void close(Portlet portlet) {
		
		remove(portlet, this.getPortletColumn(portlet));
		
		if (portlet instanceof AppPortlet)
			((AppPortlet) portlet).updateUserPortlet();
	}
	
	/**
	 * Update the position and state of all porlets.
	 */
	@Override
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


	/**
	 * Remove all portlets (without recording them as closed in the user portlet cache)
	 */
	@Override
	public void removeAllPortlets() {
		for (int col = 0; col < this.getItemCount(); col++) {
		    LayoutContainer con = getItem(col);
		    List<Component> list = new ArrayList<Component>(con.getItems());
		    for (int row = 0; row < list.size(); row++) {
		    	if (list.get(row) instanceof Portlet) {
		    		if (list.get(row) instanceof AppPortlet)
		    			((AppPortlet) list.get(row)).setPortletId(-1);
//		    		((Portlet) list.get(row)).removeFromParent();
		    		remove((Portlet) list.get(row), col);
		    	}
		    }
		}
	}

	@Override
	public void restorePresentationState(List<UserPortletCacheInstance> list) {
		boolean [] columnWidthSet = new boolean [getItemCount()];	// This only needs as many entries as columns, but why bother... the highest it would be is the number of portlets
		for (UserPortletCacheInstance instance : list) {
			if (instance.getRestoreColumn() >= 0 && instance.getRestoreColumn() < columnWidthSet.length) {
				if (! columnWidthSet [instance.getRestoreColumn()]) {
					setColumnWidth(instance.getRestoreColumn(), instance.getRestoreWidth());
					getItem(instance.getRestoreColumn()).setWidth(instance.getRestoreWidth() + getSpacing());
					columnWidthSet [instance.getRestoreColumn()] = true; // This just makes sure we only do each column once, so first portlet in line decides the width
				}
			}
		}
	}
}
