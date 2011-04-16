package com.scholastic.sbam.client.uiobjects.uitop;

import java.util.List;

import com.extjs.gxt.ui.client.widget.Composite;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.scholastic.sbam.client.uiobjects.foundation.AppSecurityManager;
import com.scholastic.sbam.client.uiobjects.foundation.AppSleeper;
import com.scholastic.sbam.client.uiobjects.foundation.DualEditGridContainer;
import com.scholastic.sbam.client.uiobjects.uiconfig.AgreementTypeEditGrid;
import com.scholastic.sbam.client.uiobjects.uiconfig.CancelReasonEditGrid;
import com.scholastic.sbam.client.uiobjects.uiconfig.CommissionTypeEditGrid;
import com.scholastic.sbam.client.uiobjects.uiconfig.ContactTypeEditGrid;
import com.scholastic.sbam.client.uiobjects.uiconfig.DeleteReasonEditGrid;
import com.scholastic.sbam.client.uiobjects.uiconfig.PreferenceCategoryEditGrid;
import com.scholastic.sbam.client.uiobjects.uiconfig.PreferenceCodeEditGrid;
import com.scholastic.sbam.client.uiobjects.uiconfig.ProductEditGrid;
import com.scholastic.sbam.client.uiobjects.uiconfig.ProductServiceSelectTree;
import com.scholastic.sbam.client.uiobjects.uiconfig.ServiceEditGrid;
import com.scholastic.sbam.client.uiobjects.uiconfig.TermTypeEditGrid;
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
	private TabItem  tbtmCommissionTypes;
	private TabItem  tbtmAgreementTypes;
	private TabItem  tbtmContactTypes;
	
	private DeleteReasonEditGrid		deleteReasonEditGrid;
	private CancelReasonEditGrid		cancelReasonEditGrid;
	private ServiceEditGrid 			serviceEditGrid;
	private TermTypeEditGrid 			termTypeEditGrid;
	private CommissionTypeEditGrid		commissionTypeEditGrid;
	private AgreementTypeEditGrid		agreementTypeEditGrid;
	private ContactTypeEditGrid			contactTypeEditGrid;
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

		tbtmPreferences		=	addTab("Preferences",	IconSupplier.getPreferenceIconName());
		tbtmServices		=	addTab("Services",		IconSupplier.getServiceIconName());
		tbtmProducts		=	addTab("Products",		IconSupplier.getProductIconName(), "Maintain the products which may be assigned to agreements.");
		tbtmTermTypes		=	addTab("Term Types",	IconSupplier.getTermTypeIconName());
		tbtmDeleteReasons	=	addTab("Delete Reasons",IconSupplier.getDeleteReasonIconName());
		tbtmCancelReasons	=	addTab("Cancel Reasons",IconSupplier.getCancelReasonIconName());
		tbtmCommissionTypes	=	addTab("Commission Types",IconSupplier.getCommissionTypeIconName());
		tbtmAgreementTypes	=	addTab("Agreement Types",IconSupplier.getAgreementTypeIconName());
		tbtmContactTypes	=	addTab("Contact Types",IconSupplier.getContactTypeIconName());
		
		deleteReasonEditGrid = new DeleteReasonEditGrid();
		tbtmDeleteReasons.add(deleteReasonEditGrid);
		
		cancelReasonEditGrid = new CancelReasonEditGrid();
		tbtmCancelReasons.add(cancelReasonEditGrid);
		
		serviceEditGrid = new ServiceEditGrid();
		tbtmServices.add(serviceEditGrid);
		
		termTypeEditGrid = new TermTypeEditGrid();
		tbtmTermTypes.add(termTypeEditGrid);
		
		commissionTypeEditGrid = new CommissionTypeEditGrid();
		tbtmCommissionTypes.add(commissionTypeEditGrid);
		
		agreementTypeEditGrid = new AgreementTypeEditGrid();
		tbtmAgreementTypes.add(agreementTypeEditGrid);
		
		contactTypeEditGrid = new ContactTypeEditGrid();
		tbtmContactTypes.add(contactTypeEditGrid);
		
		PreferenceCategoryEditGrid preferenceCategoryEditGrid = new PreferenceCategoryEditGrid();
		preferenceCategoryEditGrid.setAutoExpandColumn("description");
		preferenceCategoryEditGrid.setForceWidth(450);
		PreferenceCodeEditGrid preferenceCodeEditGrid = new PreferenceCodeEditGrid();
		preferenceCodeEditGrid.setAutoExpandColumn("description");
		preferenceCodeEditGrid.setForceWidth(450);
		preferenceEditGridContainer = new DualEditGridContainer(preferenceCategoryEditGrid, preferenceCodeEditGrid);
		preferenceEditGridContainer.setForceWidth(preferenceCategoryEditGrid.getForceWidth() + 12);
//		preferenceEditGridContainer.setForceHeight(preferenceCategoryEditGrid.getForceHeight() + 73);
		tbtmPreferences.add(preferenceEditGridContainer);
		
		ProductEditGrid productEditGrid = new ProductEditGrid();
		productEditGrid.setAutoExpandColumn("description");
		productEditGrid.setForceWidth(650);
//		productEditGrid.setForceHeight(600);
		ProductServiceSelectTree productServiceSelectTree = new ProductServiceSelectTree();
		productServiceSelectTree.setWidth(500);
//		productServiceSelectTree.setHeight(600);
		productEditGridContainer = new DualEditGridContainer(productEditGrid, productServiceSelectTree);
		productEditGridContainer.setForceWidth(662);	//	productEditGrid.getForceWidth() + 12);
//		productEditGridContainer.setForceHeight(673);	//	productEditGrid.getForceHeight() + 73);
		tbtmProducts.add(productEditGridContainer);
		
	//	productEditGrid = new ProductEditGrid();
	//	tbtmProducts.add(productEditGrid);
		
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
		if (roleNames.contains(SecurityManager.ROLE_CONFIG)) {
			tbtmDeleteReasons.enable();
			tbtmCancelReasons.enable();
			tbtmContactTypes.enable();
			tbtmServices.enable();
			tbtmTermTypes.enable();
			tbtmCommissionTypes.enable();
			tbtmAgreementTypes.enable();
			tbtmPreferences.enable();
			tbtmProducts.enable();
		} else {
			tbtmDeleteReasons.disable();
			tbtmCancelReasons.disable();
			tbtmContactTypes.disable();
			tbtmServices.disable();
			tbtmTermTypes.disable();
			tbtmCommissionTypes.disable();
			tbtmAgreementTypes.disable();
			tbtmPreferences.disable();
			tbtmProducts.disable();
		}
	}
	
	public void sleep() {
		deleteReasonEditGrid.sleep();
		cancelReasonEditGrid.sleep();
		serviceEditGrid.sleep();
		contactTypeEditGrid.sleep();
		termTypeEditGrid.sleep();
		commissionTypeEditGrid.sleep();
		agreementTypeEditGrid.sleep();
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
		} else if (advanced.getSelectedItem() == tbtmCommissionTypes) {
			commissionTypeEditGrid.awaken();
		} else if (advanced.getSelectedItem() == tbtmAgreementTypes) {
			agreementTypeEditGrid.awaken();
		} else if (advanced.getSelectedItem() == tbtmContactTypes) {
			contactTypeEditGrid.awaken();
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

	public TabItem getTbtmCommissionTypes() {
		return tbtmCommissionTypes;
	}

	public void setTbtmCommissionTypes(TabItem tbtmCommissionTypes) {
		this.tbtmCommissionTypes = tbtmCommissionTypes;
	}

	public TabItem getTbtmAgreementTypes() {
		return tbtmAgreementTypes;
	}

	public void setTbtmAgreementTypes(TabItem tbtmAgreementTypes) {
		this.tbtmAgreementTypes = tbtmAgreementTypes;
	}

	public TabItem getTbtmContactTypes() {
		return tbtmContactTypes;
	}

	public void setTbtmContactTypes(TabItem tbtmContactTypes) {
		this.tbtmContactTypes = tbtmContactTypes;
	}
	
}
