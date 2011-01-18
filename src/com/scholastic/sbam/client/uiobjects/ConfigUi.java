package com.scholastic.sbam.client.uiobjects;

import java.util.List;

import com.extjs.gxt.ui.client.widget.Composite;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.scholastic.sbam.client.uiobjects.AppSecurityManager;
import com.scholastic.sbam.shared.security.SecurityManager;

public class ConfigUi extends Composite implements AppSecurityManager, AppSleeper {

	private TabPanel advanced;
	
	private TabItem  tbtmProducts;
	private TabItem  tbtmServices;
	private TabItem  tbtmTermTypes;
	private TabItem  tbtmPreferences;
	private TabItem  tbtmDeleteReasons;
	private TabItem  tbtmCancelReasons;
	
	private DeleteReasonEditGrid		deleteReasonEditGrid;
	private CancelReasonEditGrid		cancelReasonEditGrid;
	private ServiceEditGrid 			serviceEditGrid;
	private TermTypeEditGrid 			termTypeEditGrid;
	private PreferenceCategoryEditGrid 	preferenceCategoryEditGrid;
	 
	public ConfigUi() {
//		VerticalPanel vp = new VerticalPanel();  
//		vp.setSpacing(10);  
//   
//		HorizontalPanel hp = new HorizontalPanel();  
//		hp.setSpacing(5);
		
		advanced = new TabPanel();  
		advanced.setSize(600, 250);  
		advanced.setMinTabWidth(115);  
		advanced.setResizeTabs(true);  
		advanced.setAnimScroll(true);  
		advanced.setTabScroll(true);  
		advanced.setCloseContextMenu(true); 

		tbtmProducts		=	addTab("Products");
		tbtmServices		=	addTab("Services");
		tbtmTermTypes		=	addTab("Term Types");
		tbtmPreferences		=	addTab("Preferences");
		tbtmDeleteReasons	=	addTab("Delete Reasons");
		tbtmCancelReasons	=	addTab("Cancel Reasons");
		
		deleteReasonEditGrid = new DeleteReasonEditGrid();
		tbtmDeleteReasons.add(deleteReasonEditGrid);
		
		cancelReasonEditGrid = new CancelReasonEditGrid();
		tbtmCancelReasons.add(cancelReasonEditGrid);
		
		serviceEditGrid = new ServiceEditGrid();
		tbtmServices.add(serviceEditGrid);
		
		termTypeEditGrid = new TermTypeEditGrid();
		tbtmTermTypes.add(termTypeEditGrid);
		
		preferenceCategoryEditGrid = new PreferenceCategoryEditGrid();
		tbtmPreferences.add(preferenceCategoryEditGrid);
		
		initComponent(advanced);
	}
	
	public TabItem addTab(String tabTitle) {
		TabItem item = new TabItem(); 
		item.setLayout(new FitLayout());
		item.setText(tabTitle);  
	//	item.setClosable(false);
		item.addStyleName("pad-text");  
		advanced.add(item);
		return item;
	}

	public void applyRoles(List<String> roleNames) {
		if (roleNames.contains(SecurityManager.ROLE_CONFIG)) {
			tbtmDeleteReasons.enable();
			tbtmCancelReasons.enable();
			tbtmServices.enable();
			tbtmTermTypes.enable();
		} else {
			tbtmDeleteReasons.disable();
			tbtmCancelReasons.disable();
			tbtmServices.disable();
			tbtmTermTypes.disable();
		}
	}
	
	public void sleep() {
		deleteReasonEditGrid.sleep();
		cancelReasonEditGrid.sleep();
		serviceEditGrid.sleep();
		termTypeEditGrid.sleep();
	}
	
	public void awaken() {
		if (advanced.getSelectedItem() == tbtmDeleteReasons) {
			deleteReasonEditGrid.awaken();
		} else if (advanced.getSelectedItem() == tbtmCancelReasons) {
			cancelReasonEditGrid.awaken();
		} else if (advanced.getSelectedItem() == tbtmServices) {
			serviceEditGrid.awaken();
		} else if (advanced.getSelectedItem() == tbtmTermTypes) {
			termTypeEditGrid.awaken();
		}
	}

	public TabPanel getAdvanced() {
		return advanced;
	}

	public void setAdvanced(TabPanel advanced) {
		this.advanced = advanced;
	}

	public TabItem getTbtmProducts() {
		return tbtmProducts;
	}

	public void setTbtmProducts(TabItem tbtmProducts) {
		this.tbtmProducts = tbtmProducts;
	}

	public TabItem getTbtmServices() {
		return tbtmServices;
	}

	public void setTbtmServices(TabItem tbtmServices) {
		this.tbtmServices = tbtmServices;
	}

	public TabItem getTbtmTermTypes() {
		return tbtmTermTypes;
	}

	public void setTbtmTermTypes(TabItem tbtmTermTypes) {
		this.tbtmTermTypes = tbtmTermTypes;
	}

	public TabItem getTbtmPreferences() {
		return tbtmPreferences;
	}

	public void setTbtmPreferences(TabItem tbtmPreferences) {
		this.tbtmPreferences = tbtmPreferences;
	}

	public TabItem getTbtmDeleteReasons() {
		return tbtmDeleteReasons;
	}

	public void setTbtmDeleteReasons(TabItem tbtmDeleteReasons) {
		this.tbtmDeleteReasons = tbtmDeleteReasons;
	}

	public TabItem getTbtmCancelReasons() {
		return tbtmCancelReasons;
	}

	public void setTbtmCancelReasons(TabItem tbtmCancelReasons) {
		this.tbtmCancelReasons = tbtmCancelReasons;
	}
	
}
