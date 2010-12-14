package com.scholastic.sbam.client.uiobjects;

import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.layout.CardLayout;
import com.extjs.gxt.ui.client.widget.CardPanel;
import com.scholastic.sbam.shared.objects.UserMessageInstance;

public class StickyNoteWindow extends Window {
	private CardPanel noteViewPanel;
	private CardPanel noteEditPanel;
	private CardLayout noteLayout;
	private UserMessageInstance msg;

	public StickyNoteWindow(String userName, String location) {
		this.msg = new UserMessageInstance();
		msg.setId(-1);
		msg.setText("Please place <b>your</b> thoughts <i>here</i>, " + userName + ".");
		msg.setX(-1);
		msg.setY(-1);
		msg.setZ(-1);
		msg.setWidth(550);
		msg.setHeight(160);
		msg.setLocationTag(location);
		msg.setUserName(userName);
		init();
	}
	/**
	 * @wbp.parser.constructor
	 */
	public StickyNoteWindow(UserMessageInstance msg) {
		this.msg = msg;
		init();
		setPosition(msg.getX(), msg.getY());
		setZIndex(msg.getZ());
	}
	
	private void init() {
		setSize(msg.getWidth(), msg.getHeight());
		setCollapsible(true);
		setHeading("Note");
		noteLayout = new CardLayout();
		setLayout(noteLayout);
		
		noteViewPanel = new CardPanel();
		
		StickyNoteDisplay stickyNoteDisplay = new StickyNoteDisplay(msg.getText());
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
