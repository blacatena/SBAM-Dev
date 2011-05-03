package com.scholastic.sbam.client.uiobjects.uiapp;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.Composite;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.custom.Portlet;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.scholastic.sbam.shared.objects.UserPortletCacheInstance;

/**
 * A "portal" with a tab layout.
 * 
 * To maintain a similarity to a real Portal, with columns, this tab layout also lays it's tabs out, at least organizationally, in columns
 * 
 * @author Bob Lacatena
 *
 */
public class AppTabPortal  extends Composite implements AppPortletPresenter {
	
	protected int 					nextPortalId = 0;
	protected TabPanel 				tabPanel;
	protected List<List<TabItem>>	portletTabs	=	new ArrayList<List<TabItem>>();
	
	public AppTabPortal() {
		tabPanel = new TabPanel();
		tabPanel.setTabScroll(true);
		
		initComponent(tabPanel);
	}

	@Override
	public void add(Portlet portlet, int column) {
		insert(portlet, 0, column);
	}

	@Override
	public void insert(Portlet portlet, int index, int column) {
		int portletId = nextPortalId++;
		reinsert(portlet, index, column, portletId);
		if (portlet instanceof AppPortlet) {
			AppPortlet appPortlet = (AppPortlet) portlet;
			appPortlet.registerUserPortlet(portletId, index, column);
		}
		scrollToPortlet(portlet);
	}

	@Override
	public void scrollToPortlet(Portlet portlet) {
		TabItem tab = getTab(portlet);
		if (tab != null)
			tabPanel.setSelection(tab);
	}

	@Override
	public void reinsert(Portlet portlet, int index, int column, int portletId) {
		if (portlet instanceof AppPortlet) {
			( (AppPortlet) portlet).setPortletId(portletId);
			( (AppPortlet) portlet).setPresenter(this);
		}
		
		//	Get the tab we'll add (with the portlet in it)
		TabItem tab = getPortletTab(portlet, index, column);
		
		//	Create all the columns we need
		while (portletTabs.size() <= column) {
			portletTabs.add(new ArrayList<TabItem>());
		}
		
		//	Get the column we'll add to
		List<TabItem> tabColumn = portletTabs.get(column);
		
		//	Now we'll add the tab to the proper column, and to the display
		
		//	If this is at the end of the column, the tab has to go before the first item in the next column
		if (index >= tabColumn.size()) {
			//	Append to end of the column
			tabColumn.add(tab);
			
			//	Find the next actual tab item
			TabItem nextTab = null;
			int nextColumn = column + 1;

			while (nextTab == null && nextColumn < portletTabs.size()) {
				//	Insert before first tab in next column
				List<TabItem> nextTabColumn = portletTabs.get(nextColumn);
				nextColumn++;
				if (nextTabColumn.size() > 0) {
					nextTab = nextTabColumn.get(0);
					break;
				}
			}
			
			//	Add the tab
			if (nextTab == null) {
				//	No next column, just add to end
				tabPanel.add(tab);
			} else {
				tabPanel.insert(tab, tabPanel.indexOf(nextTab));
			}
		} else {
			//	Insert before the specified tab in this column
			TabItem beforeTab = tabColumn.get(index);
			tabColumn.add(index, tab);
			tabPanel.insert(tab, tabPanel.indexOf(beforeTab));
		}
		
		//	Lastly, set the row and column for the portlet, and fix the local next portlet ID
		if (portlet instanceof AppPortlet) {
			AppPortlet appPortlet = (AppPortlet) portlet;

			// Set the tooltip for the tab
			tab.getHeader().setToolTip( appPortlet.getPresenterToolTip() );
		//	appPortlet.registerUserPortlet(appPortlet.getPortletId(), index, column);
			appPortlet.setPortalColumn(column);
			appPortlet.setPortalRow(index);
			if (appPortlet.getPortletId() >= nextPortalId)
				nextPortalId = appPortlet.getPortletId() + 1;
		}
		
	}
	
	public TabItem getPortletTab(Portlet portlet, int index, int column) {
		TabItem tab = new TabItem(getTabLabel(portlet, index, column));
		tab.setLayout(new FitLayout());
		tab.setClosable(true);
		tab.setScrollMode(Scroll.NONE);
		tab.add(portlet);
		
		if (portlet instanceof AppPortlet) {
			final AppPortlet appPortlet = (AppPortlet) portlet;
			tab.addListener(Events.Close, new Listener<BaseEvent>() {
	            public void handleEvent(BaseEvent be)
	            {
	            	if (be.getType().getEventCode() == Events.Close.getEventCode()) {
	            		appPortlet.closePortlet();
	            	}
	            };
	        });
		}
		
		return tab;
	}
	
	public String getTabLabel(Portlet portlet, int index, int column) {
		String label = null;
		if (portlet instanceof AppPortlet)
			label = ( (AppPortlet) portlet).getShortPortletName();
		else
			label = portlet.getHeading();
		if (label != null && label.length() > 0)
			return label;
		return index + "-" + column + " : ";
	}
	
	@Override
	public void close(Portlet portlet) {
		TabItem tab = getTab(portlet);
		if (tab != null && tab.getTabPanel() != null) {
			tab.close();
		}
		
		//	Take the tab out of the list of tabs
		for (List<TabItem> column : portletTabs) {
			if (column.contains(tab))
				column.remove(tab);
		}
		
		//	Make sure the portlet is out of the tab itself
		if (portlet.getParent() != null)
			portlet.removeFromParent();
		
		//	Update the portlet as "closed" in the user cache
		if (portlet instanceof AppPortlet)
			((AppPortlet) portlet).updateUserPortlet();
	}
	
	@Override
	public void updateLabel(Portlet portlet) {
		TabItem tab = getTab(portlet);
		if (tab != null) {
			tab.setText(getTabLabel(portlet, 0, 0));
			if (portlet instanceof AppPortlet)
				tab.getHeader().setToolTip( ( (AppPortlet) portlet).getPresenterToolTip() );
		}
	}

	@Override
	public void refreshAllPortletStates() {
		for (int col = 0; col < portletTabs.size(); col++) {
		    List<TabItem> list = new ArrayList<TabItem>(portletTabs.get(col));
		    for (int row = 0; row < list.size(); row++) {
		    	TabItem tab = list.get(row);
		    	if (tab.getItemCount() > 0 && tab.getItem(0) instanceof AppPortlet) {
					AppPortlet appPortlet = (AppPortlet) tab.getItem(0);
					appPortlet.updateUserPortlet(row, col);
		    	}
		    }
		}
	}
	
	/**
	 * Remove all portlets (without recording them as closed in the user portlet cache)
	 */
	public void removeAllPortlets() {
		for (int col = 0; col < portletTabs.size(); col++) {
		    List<TabItem> list = new ArrayList<TabItem>(portletTabs.get(col));
		    for (int row = 0; row < list.size(); row++) {
		    	TabItem tab = list.get(row);
		    	if (tab.getItemCount() > 0 && tab.getItem(0) instanceof AppPortlet) {
					AppPortlet appPortlet = (AppPortlet) tab.getItem(0);
					tab.remove(appPortlet);
		    	}
				tabPanel.remove(tab);
		    }
		}
		portletTabs = new ArrayList<List<TabItem>>();
	}
	
	public TabItem getTab(Portlet portlet) {
		for (TabItem tab : tabPanel.getItems()) {
			if (tab.getItems().contains(portlet))
				return tab;
		}
//		for (List<TabItem> tabs : portletTabs) {
//			for (TabItem tab : tabs) {
//				if (tab.getItems().contains(portlet))
//					return tab;
//			}
//		}
		return null;
	}

	@Override
	public void restorePresentationState(List<UserPortletCacheInstance> list) {
		// Nothing to do
	}

}
