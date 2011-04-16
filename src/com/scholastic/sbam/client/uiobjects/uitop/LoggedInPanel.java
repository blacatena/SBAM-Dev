package com.scholastic.sbam.client.uiobjects.uitop;

import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.layout.ColumnLayout;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.layout.ColumnData;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.extjs.gxt.ui.client.widget.layout.FlowData;

public class LoggedInPanel extends LayoutContainer {
	private Button buttonLogOut;
	private Html displayName;

	public LoggedInPanel() {
		setLayout(new ColumnLayout());
		
		buttonLogOut = new Button("Log Out");
		add(buttonLogOut, new ColumnData(60.0));
		
		add(new Html("&nbsp;"), new ColumnData(10.0));
		
		LayoutContainer nameContainer = new LayoutContainer();
		nameContainer.setLayout(new FlowLayout(3));
		
		displayName = new Html("Please log in.");
		nameContainer.add(displayName, new FlowData(0, 0, 0, 0));
		add(nameContainer);
	}

	public Button getButtonLogOut() {
		return buttonLogOut;
	}

	public Html getDisplayName() {
		return displayName;
	}

}
