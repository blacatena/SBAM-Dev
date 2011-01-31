package com.scholastic.sbam.client.uiobjects;

import java.util.List;

import com.extjs.gxt.ui.client.widget.Composite;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.scholastic.sbam.client.uiobjects.AppSecurityManager;
import com.scholastic.sbam.client.util.IconSupplier;
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
//	private PreferenceCategoryEditGrid 	preferenceCategoryEditGrid;
	private DualEditGridContainer		preferenceEditGridContainer;
	private DualEditGridContainer		productEditGridContainer;
	 
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

		tbtmProducts		=	addTab("Products",		IconSupplier.getProductIconName());
		tbtmServices		=	addTab("Services",		IconSupplier.getServiceIconName());
		tbtmTermTypes		=	addTab("Term Types",	IconSupplier.getTermTypeIconName());
		tbtmPreferences		=	addTab("Preferences",	IconSupplier.getPreferenceIconName());
		tbtmDeleteReasons	=	addTab("Delete Reasons",IconSupplier.getDeleteReasonIconName());
		tbtmCancelReasons	=	addTab("Cancel Reasons",IconSupplier.getCancelReasonIconName());
		
		deleteReasonEditGrid = new DeleteReasonEditGrid();
		tbtmDeleteReasons.add(deleteReasonEditGrid);
		
		cancelReasonEditGrid = new CancelReasonEditGrid();
		tbtmCancelReasons.add(cancelReasonEditGrid);
		
		serviceEditGrid = new ServiceEditGrid();
		tbtmServices.add(serviceEditGrid);
		
		termTypeEditGrid = new TermTypeEditGrid();
		tbtmTermTypes.add(termTypeEditGrid);
		
		PreferenceCategoryEditGrid preferenceCategoryEditGrid = new PreferenceCategoryEditGrid();
		preferenceCategoryEditGrid.setAutoExpandColumn("description");
		preferenceCategoryEditGrid.setForceWidth(450);
		PreferenceCodeEditGrid preferenceCodeEditGrid = new PreferenceCodeEditGrid();
		preferenceCodeEditGrid.setAutoExpandColumn("description");
		preferenceCodeEditGrid.setForceWidth(450);
		preferenceEditGridContainer = new DualEditGridContainer(preferenceCategoryEditGrid, preferenceCodeEditGrid);
		preferenceEditGridContainer.setForceWidth(preferenceCategoryEditGrid.getForceWidth() + 12);
		preferenceEditGridContainer.setForceHeight(preferenceCategoryEditGrid.getForceHeight() + 73);
		tbtmPreferences.add(preferenceEditGridContainer);
		
		ProductEditGrid productEditGrid = new ProductEditGrid();
		productEditGrid.setAutoExpandColumn("description");
		productEditGrid.setForceWidth(650);
		productEditGrid.setForceHeight(600);
		ProductServiceSelectTree productServiceSelectTree = new ProductServiceSelectTree();
		productServiceSelectTree.setWidth(500);
		productServiceSelectTree.setHeight(600);
		productEditGridContainer = new DualEditGridContainer(productEditGrid, productServiceSelectTree);
		productEditGridContainer.setForceWidth(662);	//	productEditGrid.getForceWidth() + 12);
		productEditGridContainer.setForceHeight(673);	//	productEditGrid.getForceHeight() + 73);
		tbtmProducts.add(productEditGridContainer);
		
	//	productEditGrid = new ProductEditGrid();
	//	tbtmProducts.add(productEditGrid);
		
		initComponent(advanced);
	}
	
	public TabItem addTab(String tabTitle) {
		return addTab(tabTitle, null);
	}
	
	public TabItem addTab(String tabTitle, String iconName) {
		TabItem item = new TabItem(); 
		item.setLayout(new FitLayout());
		item.setText(tabTitle);
		if (iconName != null && iconName.length() > 0)
			IconSupplier.setIcon(item, iconName);
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
			tbtmPreferences.enable();
			tbtmProducts.enable();
		} else {
			tbtmDeleteReasons.disable();
			tbtmCancelReasons.disable();
			tbtmServices.disable();
			tbtmTermTypes.disable();
			tbtmPreferences.disable();
			tbtmProducts.disable();
		}
	}
	
	public void sleep() {
		deleteReasonEditGrid.sleep();
		cancelReasonEditGrid.sleep();
		serviceEditGrid.sleep();
		termTypeEditGrid.sleep();
	//	preferenceCategoryEditGrid.sleep();
		preferenceEditGridContainer.sleep();
		productEditGridContainer.sleep();
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
		} else if (advanced.getSelectedItem() == tbtmPreferences) {
		//	preferenceCategoryEditGrid.awaken();
			preferenceEditGridContainer.awaken();
		} else if (advanced.getSelectedItem() == tbtmProducts) {
			productEditGridContainer.awaken();
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
