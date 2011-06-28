package com.scholastic.sbam.client.uiobjects.uireports;

import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.layout.CenterLayout;
import com.google.gwt.user.client.Element;

public class CustomerSelectionCard extends LayoutContainer {

	@Override
	public void onRender(Element element, int index) {
		super.onRender(element, index);
		
		setLayout(new CenterLayout());
		addStyleName("sbam-report-body");
		
		add(new Html("Howdy hi-dee hofdfsdsdfsd."));
	}
	
}
