package com.scholastic.sbam.client.uiobjects.uitop;

import java.util.List;

import com.extjs.gxt.ui.client.widget.Container;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.user.client.Element;
import com.scholastic.sbam.client.uiobjects.foundation.AppSecurityManager;
import com.scholastic.sbam.client.uiobjects.foundation.AppSleeper;
import com.scholastic.sbam.client.uiobjects.uireports.ServiceTermReportPanel;
import com.scholastic.sbam.client.util.IconSupplier;
import com.scholastic.sbam.shared.security.SecurityManager;

public class ReportsUi extends LayoutContainer implements AppSecurityManager, AppSleeper {

	private TabPanel advanced;
	
	private TabItem  serviceTermsTab;
	private TabItem  productTermsTab;
	
	private ServiceTermReportPanel 		serviceReportPanel;
	private ServiceTermReportPanel		productReportPanel;
	 
	public ReportsUi() {
		oneOptionReportsUi();
	}
	@Override
	public void onRender(Element element, int index) {
		super.onRender(element, index);
		oneOptionReportsUi();
		setLayout(new FitLayout());
	}
	
	/*
	 * New method just has one selector
	 */
	public void oneOptionReportsUi() {
		setLayout(new FitLayout());
		serviceReportPanel = new ServiceTermReportPanel();
		initComponent(serviceReportPanel);
	}
 	 
	/*
	 * Old method used tabs
	 */
	public void tabbedReportsUi() {
		
		advanced = new TabPanel();  
//		advanced.setSize(600, 250);  
		advanced.setMinTabWidth(115);  
		advanced.setResizeTabs(true);
		advanced.setAnimScroll(true);
		advanced.setTabScroll(true);
		advanced.setCloseContextMenu(true); 
//		advanced.setLayout(new FitLayout());

		serviceTermsTab		=	addTab("Services",		IconSupplier.getServiceIconName(), "Generate reports based on services and agreement terms.");
		productTermsTab		=	addTab("Products",		IconSupplier.getProductIconName(), "Generate reports based on products and agreement terms.");
		
		serviceReportPanel = new ServiceTermReportPanel();
		serviceTermsTab.add(serviceReportPanel);
		
		serviceReportPanel = new ServiceTermReportPanel();
		productTermsTab.add(serviceReportPanel);
		
		initComponent(advanced);
	}
	
	@SuppressWarnings("rawtypes")
	public void initComponent(Container component) {
		add(component);
	}
	
	public TabItem addTab(String tabTitle) {
		return addTab(tabTitle, null);
	}
	
	public TabItem addTab(String tabTitle, String iconName) {
		return addTab(tabTitle, iconName, null);
	}
		
	public TabItem addTab(String tabTitle, String iconName, String toolTip) {
		TabItem item = new TabItem(); 
		item.setLayout(new FitLayout());
		item.setText(tabTitle);
		if (iconName != null && iconName.length() > 0)
			IconSupplier.setIcon(item, iconName);
		item.getHeader().setToolTip(toolTip);
	//	item.setClosable(false);
//		This doesn't work -- GXT puts the tooltip on the content area, instead of the tab itself
//		if (toolTip != null) {
//			item.setToolTip(toolTip);
//		}
		item.addStyleName("pad-text");  
		advanced.add(item);
		return item;
	}

	public void applyRoles(List<String> roleNames) {
		if (roleNames.contains(SecurityManager.ROLE_QUERY)) {
			if (serviceTermsTab != null) serviceTermsTab.enable();
			if (productTermsTab != null) productTermsTab.enable();
		} else {
			if (serviceTermsTab != null) serviceTermsTab.disable();
			if (productTermsTab != null) productTermsTab.disable();
		}
	}
	
	public void sleep() {
		serviceReportPanel.sleep();
	}
	
	public void awaken() {
		if (serviceTermsTab != null && advanced.getSelectedItem() == productTermsTab) {
			serviceReportPanel.awaken();
		} else if (productTermsTab != null && advanced.getSelectedItem() == serviceTermsTab) {
			productReportPanel.awaken();
		}
	}
	
}
