package com.scholastic.sbam.client.uiobjects;

import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.event.WindowListener;
import com.extjs.gxt.ui.client.event.WindowEvent;

public class StickyNote extends Window {

	public StickyNote() {
		addWindowListener(new WindowListener() {
			public void windowMinimize(WindowEvent we) {
				collapse();
				int top = we.getWindow().getParent().getAbsoluteTop() + we.getWindow().getParent().getOffsetHeight() - we.getWindow().getHeight();
				we.getWindow().setPosition(we.getWindow().getAbsoluteLeft(), top);
			}
		});
		setMinimizable(true);
		setMaximizable(true);
		setTitleCollapse(true);
		setCollapsible(true);
		setHeading("Note");
		setLayout(new RowLayout(Orientation.VERTICAL));
		this.addText("Put your thoughts here for safe keeping.");
	}

}
