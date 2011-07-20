package com.scholastic.sbam.client.uiobjects.uireports;

import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.ToolButton;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.user.client.Element;
import com.scholastic.sbam.shared.objects.SnapshotInstance;

public abstract class SnapshotCardBase extends LayoutContainer {
	
	protected	SnapshotInstance				snapshot;
	
	protected	SnapshotParentCardPanel			parentCardPanel;
	
	protected	String	headingToolTip			=	"Use this panel to select snapshot details. ";

	@Override
	public void onRender(Element element, int index) {
		super.onRender(element, index);
		
		setLayout(new FitLayout());
		addStyleName("sbam-report-body");
		
		addPanelContent();
		
		addReturnTool();
	}
	
	public abstract String getPanelTitle();
	
	public abstract void addPanelContent();
	
	public abstract ContentPanel	getContentPanel();

	public int getSnapshotId() {
		if (snapshot == null)
			return -1;
		return snapshot.getSnapshotId();
	}
	
	public SnapshotInstance getSnapshot() {
		return snapshot;
	}

	public void setSnapshot(SnapshotInstance snapshot) {
		this.snapshot = snapshot;
		if (getContentPanel() != null)
			if (snapshot == null)
				getContentPanel().setHeading(getPanelTitle());
			else
				getContentPanel().setHeading(getPanelTitle() + " for <i>" + snapshot.getSnapshotId() + " &mdash; " + snapshot.getSnapshotName() + "</i>");
	}

	public SnapshotParentCardPanel getParentCardPanel() {
		return parentCardPanel;
	}

	public void setParentCardPanel(SnapshotParentCardPanel parentCardPanel) {
		this.parentCardPanel = parentCardPanel;
	}

	public void addReturnTool() {
	
		ToolButton returnTool = new ToolButton("x-tool-left") {
				@Override
				protected void onClick(ComponentEvent ce) {
					parentCardPanel.switchLayout(SnapshotParentCardPanel.SNAPSHOT_SELECTOR_PANEL);
				}
			};
		returnTool.enable();
		
		getContentPanel().getHeader().addTool(returnTool);
		getContentPanel().getHeader().setToolTip(headingToolTip + "  Use the return arrow tool at the top right to return to the snapshot selector.");
	}
	
}
