package com.scholastic.sbam.client.uiobjects.uitop;

import java.util.List;

import com.extjs.gxt.ui.client.widget.Composite;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.scholastic.sbam.client.uiobjects.foundation.AppSecurityManager;
import com.scholastic.sbam.client.uiobjects.foundation.AppSleeper;
import com.scholastic.sbam.client.uiobjects.uireports.ServiceTermReportPanel;
import com.scholastic.sbam.client.util.IconSupplier;
import com.scholastic.sbam.shared.security.SecurityManager;

public class ReportsUi extends Composite implements AppSecurityManager, AppSleeper {

	private TabPanel advanced;
	
	private TabItem  serviceTermsTab;
	private TabItem  productTermsTab;
	
	private ServiceTermReportPanel 		serviceReportPanel;
	private ServiceTermReportPanel		productReportPanel;
	 
	public ReportsUi() {
		
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
			serviceTermsTab.enable();
			productTermsTab.enable();
		} else {
			serviceTermsTab.disable();
			productTermsTab.disable();
		}
	}
	
	public void sleep() {
		serviceReportPanel.sleep();
	}
	
	public void awaken() {
		if (advanced.getSelectedItem() == productTermsTab) {
			serviceReportPanel.awaken();
		} else if (advanced.getSelectedItem() == serviceTermsTab) {
			productReportPanel.awaken();
		}
	}
	
}
