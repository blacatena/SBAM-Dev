package com.scholastic.sbam.client.uiobjects.uireports;

import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.user.client.Element;

public class TermReportSnapshotCard extends LayoutContainer {

	@Override
	public void onRender(Element element, int index) {
		super.onRender(element, index);
		
		setLayout(new FitLayout());
		addStyleName("sbam-report-body");
		
		add(new Button("Generate"));
	}
}
