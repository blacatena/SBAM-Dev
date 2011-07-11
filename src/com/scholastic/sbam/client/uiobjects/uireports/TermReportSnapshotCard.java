package com.scholastic.sbam.client.uiobjects.uireports;

import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.scholastic.sbam.client.util.IconSupplier;

public class TermReportSnapshotCard extends SnapshotCardBase {

	protected ContentPanel	contentPanel;
	
	public TermReportSnapshotCard() {
		super();
		this.headingToolTip = "Use this panel to view a terms based report.";
	}

	@Override
	public void addPanelContent() {
		contentPanel = new ContentPanel();
		contentPanel.setHeading("Snapshot Terms Data View");
		IconSupplier.setIcon(contentPanel, IconSupplier.getReportIconName());
		
		add(contentPanel);
	}

	@Override
	public ContentPanel getContentPanel() {
		return contentPanel;
	}
	
}
