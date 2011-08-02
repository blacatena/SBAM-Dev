package com.scholastic.sbam.client.uiobjects.uireports;

import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.scholastic.sbam.shared.objects.SnapshotInstance;

public class ProductSelectionCard extends SnapshotCardBase {
	
	protected	SnapshotProductSelectTree		productSelectTree = new SnapshotProductSelectTree();

	public ProductSelectionCard() {
		this.headingToolTip = "Use this panel to select products.";
	}
	
	@Override
	public void addPanelContent() {
//		productSelectTree = new SnapshotProductSelectTree();
		productSelectTree.setSnapshot(snapshot);
		productSelectTree.setPanelHeading(getPanelTitle());
		
		add(productSelectTree);
	}
	
	@Override
	public void setSnapshot(SnapshotInstance snapshot) {
		super.setSnapshot(snapshot);
		if (productSelectTree != null)
			productSelectTree.setSnapshot(snapshot);
	}

	@Override
	public ContentPanel getContentPanel() {
		return productSelectTree.getPanel();
	}

	@Override
	public String getPanelTitle() {
		return "Snapshot Products Selector";
	}
}
