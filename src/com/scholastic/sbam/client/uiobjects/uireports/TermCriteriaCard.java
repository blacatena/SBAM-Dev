package com.scholastic.sbam.client.uiobjects.uireports;

import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Html;
import com.scholastic.sbam.client.util.IconSupplier;

public class TermCriteriaCard extends SnapshotCardBase {
	
	protected ContentPanel	contentPanel;
	
	public TermCriteriaCard() {
		super();
		this.headingToolTip = "Use this panel to specify term criteria for the snapshot.";
	}

	@Override
	public void addPanelContent() {
		contentPanel = new ContentPanel();
		contentPanel.setHeading("Snapshot Terms Selector");
		IconSupplier.setIcon(contentPanel, IconSupplier.getTermTypeIconName());
		contentPanel.add(new Html("Term Criteria Card"));
		
		add(contentPanel);
	}

	@Override
	public ContentPanel getContentPanel() {
		return contentPanel;
	}
	
	
}
