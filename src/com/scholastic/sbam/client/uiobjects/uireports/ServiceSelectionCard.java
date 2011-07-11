package com.scholastic.sbam.client.uiobjects.uireports;

import com.extjs.gxt.ui.client.widget.ContentPanel;

public class ServiceSelectionCard extends SnapshotCardBase {
	
	protected	SnapshotServiceSelectTree		serviceSelectTree;

	public ServiceSelectionCard() {
		this.headingToolTip = "Use this panel to select services.";
	}
	
	@Override
	public void addPanelContent() {
		serviceSelectTree = new SnapshotServiceSelectTree();
		serviceSelectTree.setSnapshotId(snapshotId);
		serviceSelectTree.setPanelHeading("Snapshot Services Selector");
		
		add(serviceSelectTree);
	}

	@Override
	public ContentPanel getContentPanel() {
		return serviceSelectTree.getPanel();
	}
	
}
