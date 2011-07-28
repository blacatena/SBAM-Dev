package com.scholastic.sbam.client.uiobjects.uireports;

import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.scholastic.sbam.client.util.IconSupplier;
import com.scholastic.sbam.shared.objects.SnapshotInstance;

public class CustomerSelectionCard extends SnapshotCardBase {
	
	protected ContentPanel	contentPanel				=	 getNewContentPanel();
	
	private TabPanel advanced;
	
	protected InstitutionStateSelectionCard		stateCard	= new InstitutionStateSelectionCard();
	protected InstitutionCountrySelectionCard	countryCard = new InstitutionCountrySelectionCard();
	
	public CustomerSelectionCard() {
		super();
		this.headingToolTip = "Use this panel to select customers for the snapshot.";
	}

	@Override
	public void addPanelContent() {
//		contentPanel.add(getPanelsContainer());
		
		contentPanel.add(getPanelsContainer());
		add(contentPanel);
	}
	
	public ContentPanel getNewContentPanel() {
		ContentPanel contentPanel = new ContentPanel();
		contentPanel.setHeading(getPanelTitle());
		IconSupplier.setIcon(contentPanel, IconSupplier.getCustomerIconName());
		contentPanel.setLayout(new FitLayout());
		return contentPanel;
	}
	
	public TabPanel getPanelsContainer() {

		advanced = new TabPanel();  
		advanced.setMinTabWidth(115);  
		advanced.setResizeTabs(true);
		advanced.setAnimScroll(true);
		advanced.setTabScroll(true);
		advanced.setCloseContextMenu(true);
		
		TabItem statesTab		=	addTab("States",		IconSupplier.getUsaIconName(), "Specify U.S. states or Canadian provinces to be selected.");
		TabItem countriesTab	=	addTab("Countries",		IconSupplier.getCountriesIconName(), "Specify countries to be selected.");
		
		stateCard.getContentPanel().setHeaderVisible(false);	
		statesTab.add(stateCard);
		
		
		countryCard.getContentPanel().setHeaderVisible(false);
		countriesTab.add(countryCard);
		
		return advanced;
	}

	@Override
	public ContentPanel getContentPanel() {
		return contentPanel;
	}

	@Override
	public String getPanelTitle() {
		return "Snapshot Customer Selector";
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
		item.addStyleName("pad-text");  
		advanced.add(item);
		return item;
	}
	
	public void setSnapshot(SnapshotInstance snapshot) {
		super.setSnapshot(snapshot);
		stateCard.setSnapshot(snapshot);
	}
	
}
