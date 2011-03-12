package com.scholastic.sbam.client.uiobjects;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.custom.Portal;
import com.extjs.gxt.ui.client.widget.custom.Portlet;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanelSelectionModel;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.google.gwt.user.client.Element;
import com.scholastic.sbam.shared.util.AppConstants;

public class AppPortal extends LayoutContainer implements AppSleeper {
	
	public static class AppTreeSelectionModel extends TreePanelSelectionModel<ModelData> {
		AppPortletProvider provider;
		
		AppTreeSelectionModel(AppPortletProvider provider) {
			super();
			this.provider = provider;
		}
		
		@Override
		public void onSelectChange(ModelData model, boolean select) {
			super.onSelectChange(model, select);
			if (select) {
				provider.addPortlet(model);
			}
			this.deselectAll();
		}
	}
	
	private Portal thePortal;
	private AppPortletProvider portletProvider;

	@Override  
	protected void onRender(Element parent, int index) {  

		super.onRender(parent, index);
		setLayout(new BorderLayout());
		
		thePortal = new AppPortalWithCache(2);
		thePortal.setColumnWidth(0, .5);
		thePortal.setColumnWidth(1, .5);
		BorderLayoutData centerData = new BorderLayoutData(LayoutRegion.CENTER);
		centerData.setSplit(true);
		
		ContentPanel contentPanel = new ContentPanel();
		contentPanel.setHeading("Navigation");
		contentPanel.setCollapsible(false);
		contentPanel.setBorders(true);
		
		TreePanel<ModelData> appNavTree = AppNavTree.getTreePanel();
		portletProvider = new AppPortletProvider(thePortal);
		appNavTree.setSelectionModel(new AppTreeSelectionModel(portletProvider));
		contentPanel.add(appNavTree);
		
		BorderLayoutData westData = new BorderLayoutData(LayoutRegion.WEST, .1f, 100, 200);
		westData.setCollapsible(true);
		westData.setFloatable(true);
		westData.setSplit(true);
		westData.setMargins(new Margins(5));

		add(contentPanel, 	westData);
		add(thePortal, 		centerData);
	}
	
	public void setLoggedIn() {
		if (AppConstants.USER_PORTLET_CACHE_ACTIVE)
			System.out.println("Restore portlets here.");
	}
	
	public void setLoggedOut() {
//		On logout, remove all portlets
		removeAllPortlets();
	}

	/**
	 * Remove all portlets (without recording them as closed in the user portlet cache)
	 */
	public void removeAllPortlets() {
		for (int col = 0; col < this.getItemCount(); col++) {
		    LayoutContainer con = thePortal.getItem(col);
		    List<Component> list = new ArrayList<Component>(con.getItems());
		    for (int row = 0; row < list.size(); row++) {
		    	if (list.get(row) instanceof Portlet) {
		    		((Portlet) list.get(row)).removeFromParent();
		    	}
		    }
		}
	}

	@Override
	public void awaken() {
		for (LayoutContainer portlet : thePortal.getItems())
			if (portlet instanceof AppSleeper)
				((AppSleeper) portlet).awaken();
	}

	@Override
	public void sleep() {
		for (LayoutContainer portlet : thePortal.getItems())
			if (portlet instanceof AppSleeper)
				((AppSleeper) portlet).sleep();
	}

}

