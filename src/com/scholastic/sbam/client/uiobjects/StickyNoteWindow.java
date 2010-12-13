package com.scholastic.sbam.client.uiobjects;

import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.layout.CardLayout;
import com.extjs.gxt.ui.client.widget.CardPanel;

public class StickyNoteWindow extends Window {
	private CardPanel noteViewPanel;
	private CardPanel noteEditPanel;
	private CardLayout noteLayout;

	public StickyNoteWindow() {
		setSize("550", "160");
		setCollapsible(true);
		setHeading("Note");
		noteLayout = new CardLayout();
		setLayout(noteLayout);
		
		noteViewPanel = new CardPanel();
		
		StickyNoteDisplay stickyNoteDisplay = new StickyNoteDisplay();
		noteViewPanel.add(stickyNoteDisplay);
		add(noteViewPanel);
		
		noteEditPanel = new CardPanel();
		
		StickyNoteEditor stickyNoteEditor = new StickyNoteEditor();
		noteEditPanel.add(stickyNoteEditor);
		
		stickyNoteEditor.setNoteLayout(noteLayout);
		stickyNoteEditor.setDisplayPanel(noteViewPanel);
		stickyNoteEditor.setStickyNoteDisplay(stickyNoteDisplay);
		stickyNoteDisplay.setNoteLayout(noteLayout);
		stickyNoteDisplay.setEditPanel(noteEditPanel);
		stickyNoteDisplay.setStickyNoteEditor(stickyNoteEditor);
		add(noteEditPanel);
	}

}
