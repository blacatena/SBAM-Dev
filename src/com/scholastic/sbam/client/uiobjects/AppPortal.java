package com.scholastic.sbam.client.uiobjects;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.custom.Portal;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanelSelectionModel;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.google.gwt.user.client.Element;

public class AppPortal extends LayoutContainer implements AppSleeper {
	
	public static class AppTreeSelectionModel extends TreePanelSelectionModel<ModelData> {
		AppPortletProvider provider;
		
		AppTreeSelectionModel(AppPortletProvider provider) {
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
	
	int counter = 0;
	
//	private Portal portale;
	private Portal portalc;

	@Override  
	protected void onRender(Element parent, int index) {  

		super.onRender(parent, index);
		setLayout(new BorderLayout());
		
//		LayoutContainer north = new LayoutContainer();
//		BorderLayoutData northData = new BorderLayoutData(LayoutRegion.NORTH, 30);
		
//		ContentPanel contentPanel_1 = new ContentPanel();
//		contentPanel_1.setHeading("New ContentPanel South");
//		contentPanel_1.setCollapsible(false);
//		BorderLayoutData southData = new BorderLayoutData(LayoutRegion.SOUTH, 100);
//		southData.setCollapsible(true);
//		southData.setFloatable(true);
//		southData.setSplit(true);
//		southData.setMargins(new Margins(5));
//		
//		Button newButtonE = new Button("New Porlet East");
//		newButtonE.addSelectionListener(new SelectionListener<ButtonEvent>() {
//			   
//			@Override
//			public void componentSelected(ButtonEvent ce) {
//				addPortlet(portale);
//			}
//		 
//		});
//		
//		Button newButtonC = new Button("New Porlet Center");
//		newButtonC.addSelectionListener(new SelectionListener<ButtonEvent>() {
//			   
//			@Override
//			public void componentSelected(ButtonEvent ce) {
//				addPortlet(portalc);
//			}
//		 
//		});
//		
//		contentPanel_1.addButton(newButtonC);
//		contentPanel_1.addButton(newButtonE);
//		
//		portale = new Portal(2);
//		portale.setColumnWidth(0, .5);
//		portale.setColumnWidth(1, .5);
//		BorderLayoutData eastData = new BorderLayoutData(LayoutRegion.EAST, 300);
//		eastData.setCollapsible(true);
//		eastData.setFloatable(true);
//		eastData.setSplit(true);
//		eastData.setMargins(new Margins(5));
		
		portalc = new Portal(2);
		portalc.setColumnWidth(0, .5);
		portalc.setColumnWidth(1, .5);
		BorderLayoutData centerData = new BorderLayoutData(LayoutRegion.CENTER);
		centerData.setSplit(true);
		
		ContentPanel contentPanel = new ContentPanel();
		contentPanel.setHeading("Navigation");
		contentPanel.setCollapsible(false);
		contentPanel.setBorders(true);
		
		TreePanel<ModelData> appNavTree = AppNavTree.getTreePanel();
		appNavTree.setSelectionModel(new AppTreeSelectionModel(new AppPortletProvider(portalc)));
		contentPanel.add(appNavTree);
		
//		contentPanel.setStyleAttribute("backgroundColor", "cornsilk");
		BorderLayoutData westData = new BorderLayoutData(LayoutRegion.WEST, .1f, 100, 200);
		westData.setCollapsible(true);
		westData.setFloatable(true);
		westData.setSplit(true);
		westData.setMargins(new Margins(5));

//		add(north,			northData);
		add(contentPanel, 	westData);
//		add(contentPanel_1, southData);
//		add(portale,		eastData);
		add(portalc, 		centerData);
	}

	@Override
	public void awaken() {
		for (LayoutContainer portlet : portalc.getItems())
			if (portlet instanceof AppSleeper)
				((AppSleeper) portlet).awaken();
	}

	@Override
	public void sleep() {
		for (LayoutContainer portlet : portalc.getItems())
			if (portlet instanceof AppSleeper)
				((AppSleeper) portlet).sleep();
	}

}

