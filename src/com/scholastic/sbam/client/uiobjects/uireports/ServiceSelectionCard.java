package com.scholastic.sbam.client.uiobjects.uireports;

import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.scholastic.sbam.shared.objects.SnapshotInstance;

public class ServiceSelectionCard extends SnapshotCardBase {
	
	protected	SnapshotServiceSelectTree		serviceSelectTree = new SnapshotServiceSelectTree();

	public ServiceSelectionCard() {
		this.headingToolTip = "Use this panel to select services.";
	}
	
	@Override
	public void addPanelContent() {
//		serviceSelectTree = new SnapshotServiceSelectTree();
		serviceSelectTree.setSnapshot(snapshot);
		serviceSelectTree.setPanelHeading(getPanelTitle());
		
		add(serviceSelectTree);
	}
	
	@Override
	public void setSnapshot(SnapshotInstance snapshot) {
		super.setSnapshot(snapshot);
		if (serviceSelectTree != null)
			serviceSelectTree.setSnapshot(snapshot);
	}

	@Override
	public ContentPanel getContentPanel() {
		return serviceSelectTree.getPanel();
	}

	@Override
	public String getPanelTitle() {
		return "Snapshot Services Selector";
	}
}
