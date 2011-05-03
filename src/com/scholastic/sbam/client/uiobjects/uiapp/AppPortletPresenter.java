package com.scholastic.sbam.client.uiobjects.uiapp;

import java.util.List;

import com.extjs.gxt.ui.client.widget.custom.Portlet;
import com.scholastic.sbam.shared.objects.UserPortletCacheInstance;

/**
 * This interface represents any object that may embed portlets.
 * 
 * Normally, this would be a GXT Portal, but it could also be a tab layout container or any other navigation entity.
 * 
 * @author Bob Lacatena
 *
 */
public interface AppPortletPresenter {
	
	/**
	 * Add a portlet at the end of a specific column
	 * @param portlet
	 * @param column
	 */
	public void add(Portlet portlet, int column);
	
	/**
	 * Add a portlet at a specific point in a column
	 * @param portlet
	 * @param index
	 * @param column
	 */
	public void insert(Portlet portlet, int index, int column);
	
	/**
	 * Move a specific portlet into view
	 * @param portlet
	 */
	public void scrollToPortlet(Portlet portlet);
	
	/**
	 * Update the label (and optionally the tooltip) used to represent the portlet in the presenter.
	 * 
	 * @param portlet
	 */
	public void updateLabel(Portlet portlet);
	
	/**
	 * Insert a portlet at a specific location, but without registering the portlet (used when portlet is read from the registry)
	 * @param portlet
	 * @param index
	 * @param column
	 */
	public void reinsert(Portlet portlet, int index, int column, int portletId);
	
	/**
	 * Close a particular portlet.
	 * @param portlet
	 */
	public void close(Portlet portlet);
	
	/**
	 * Update all portlet cache entries in the database to represent their current state.
	 */
	public void refreshAllPortletStates();
	
	/**
	 * Remove all portlets from this container.
	 */
	public void removeAllPortlets();
	
	/**
	 * Do anything required to restore the presentation state (such as setting column widths in a Portal).
	 * 
	 * @param list
	 */
	public void restorePresentationState(List<UserPortletCacheInstance> list);
}
