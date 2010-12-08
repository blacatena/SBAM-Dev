package com.scholastic.sbam.client.uiobjects;

import com.extjs.gxt.ui.client.widget.Composite;
import com.extjs.gxt.ui.client.widget.button.ButtonBar;
import com.extjs.gxt.ui.client.widget.button.Button;

public class AppTools extends Composite {
	private Button btnNewNote;

	public AppTools() {
		
		ButtonBar buttonBar = new ButtonBar();
		buttonBar.setSize("42px", "28px");
		
		btnNewNote = new Button("Note");
		buttonBar.add(btnNewNote);
		initComponent(buttonBar);
	}

}
