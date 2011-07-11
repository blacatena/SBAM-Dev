package com.scholastic.sbam.client.uiobjects.uireports;

import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Html;
import com.scholastic.sbam.client.util.IconSupplier;

public class CustomerSelectionCard extends SnapshotCardBase {
	
	protected ContentPanel	contentPanel;
	
	public CustomerSelectionCard() {
		super();
		this.headingToolTip = "Use this panel to select customers for the snapshot.";
	}

	@Override
	public void addPanelContent() {
		contentPanel = new ContentPanel();
		contentPanel.setHeading("Snapshot Customer Selector");
		IconSupplier.setIcon(contentPanel, IconSupplier.getCustomerIconName());
		contentPanel.add(new Html("Customer Selector Card"));
		
		add(contentPanel);
	}

	@Override
	public ContentPanel getContentPanel() {
		return contentPanel;
	}
	
}
