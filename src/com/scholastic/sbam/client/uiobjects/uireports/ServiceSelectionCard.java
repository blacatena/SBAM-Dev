package com.scholastic.sbam.client.uiobjects.uireports;

import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.user.client.Element;

public class ServiceSelectionCard extends LayoutContainer {
	
	protected	SnapshotServiceSelectTree		serviceSelectTree;

	@Override
	public void onRender(Element element, int index) {
		super.onRender(element, index);
		
		setLayout(new FitLayout());
		addStyleName("sbam-report-body");
		
		serviceSelectTree = new SnapshotServiceSelectTree();
		serviceSelectTree.setSnapshotId(1);
		
		add(serviceSelectTree);
	}
}
