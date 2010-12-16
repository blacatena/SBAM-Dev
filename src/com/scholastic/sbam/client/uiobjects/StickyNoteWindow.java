package com.scholastic.sbam.client.uiobjects;

import java.util.Date;

import com.extjs.gxt.ui.client.event.ResizeEvent;
import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.event.WindowListener;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.layout.CardLayout;
import com.extjs.gxt.ui.client.widget.CardPanel;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.scholastic.sbam.client.services.UpdateUserMessageService;
import com.scholastic.sbam.client.services.UpdateUserMessageServiceAsync;
import com.scholastic.sbam.shared.objects.UserMessageInstance;

public class StickyNoteWindow extends Window {
	private CardPanel noteViewPanel;
	private CardPanel noteEditPanel;
	private CardLayout noteLayout;
	private UserMessageInstance msg;
	
//	private String title;
	
	private final int DEFAULT_WIDTH	=	550;
	private final int DEFAULT_HEIGHT =	160;
	private final String DEFAULT_TEXT = "Please place <b>your</b> thoughts <i>here</i>.";
	
	private final UpdateUserMessageServiceAsync updateUserMessageService = GWT.create(UpdateUserMessageService.class);

	public StickyNoteWindow(String userName, String location) {
		setDefaultNewNote(userName, location);
		init();
	}
	/**
	 * @wbp.parser.constructor
	 */
	public StickyNoteWindow(UserMessageInstance msg) {
		this.msg = msg;

		init();
		setTitle();
		setSize(msg.getWidth(), msg.getHeight());
	}
	
	private void setDefaultNewNote(String userName, String location) {
		this.msg = new UserMessageInstance();
		msg.setId(-1);
		msg.setLocationTag(location);
		msg.setUserName(userName);
		msg.setStatus('A');
		msg.setCreated(new Date() + "");
		msg.setText(DEFAULT_TEXT);
		msg.setX(-1);
		msg.setY(-1);
		msg.setZ(-1);
		msg.setWidth(DEFAULT_WIDTH);
		msg.setHeight(DEFAULT_HEIGHT);
		msg.setRestoreX(-1);
		msg.setRestoreY(-1);
		msg.setRestoreWidth(DEFAULT_WIDTH);
		msg.setRestoreHeight(DEFAULT_HEIGHT);
		msg.setMinimized(false);
		msg.setMaximized(false);
		msg.setCollapsed(false);
	}
	
	private void init() {
		setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
		setCollapsible(true);
		setMinimizable(true);
		setMaximizable(true);
		
		addWindowListeners();
		
		setHeading("Note");
		noteLayout = new CardLayout();
		setLayout(noteLayout);
		
		noteViewPanel = new CardPanel();
		
		StickyNoteDisplay stickyNoteDisplay = new StickyNoteDisplay(msg.getText());
		noteViewPanel.add(stickyNoteDisplay);
		add(noteViewPanel);
		
		noteEditPanel = new CardPanel();
		
		StickyNoteEditor stickyNoteEditor = new StickyNoteEditor(msg);
		noteEditPanel.add(stickyNoteEditor);
		
		stickyNoteEditor.setNoteLayout(noteLayout);
		stickyNoteEditor.setDisplayPanel(noteViewPanel);
		stickyNoteEditor.setStickyNoteDisplay(stickyNoteDisplay);
		stickyNoteDisplay.setNoteLayout(noteLayout);
		stickyNoteDisplay.setEditPanel(noteEditPanel);
		stickyNoteDisplay.setStickyNoteEditor(stickyNoteEditor);
		add(noteEditPanel);
	}
	
	private void addWindowListeners() {

		addWindowListener(new WindowListener() {
			public void windowActivate(WindowEvent we) {
				if (isRendered()) {
					UserMessageInstance clone = msg.clone();
					msg.setZ(getPositionEl().getZIndex());
					updateUserMessage(clone);
				}
			}
			public void windowDeactivate(WindowEvent we) {
				if (isRendered()) {
					UserMessageInstance clone = msg.clone();
					msg.setZ(getPositionEl().getZIndex());
					updateUserMessage(clone);
				}
			}
			public void windowMinimize(WindowEvent we) {
				UserMessageInstance clone = msg.clone();
				if (isCollapsed() && getPosition(false).y == 0 && msg.isMinimized()) {
					setPosition(msg.getRestoreX(), msg.getRestoreY());
					expand();
					msg.setMinimized(false);
					msg.setCollapsed(false);
//					msg.setRestoreX(0);
					msg.setRestoreY(0);
				} else {
					msg.setMinimized(true);
					msg.setCollapsed(true);
					msg.setRestoreX(msg.getX());	//	should be getPosition(false).x, but it isn't properly set yet in onRender when loading windows from the database
					msg.setRestoreY(msg.getY());	//	should be getPosition(false).y, but it isn't properly set yet in onRender when loading windows from the database
					setPosition(msg.getX(), 0);
					collapse();
				}
				updateUserMessage(clone);
			}
			public void windowMaximize(WindowEvent we) {
				System.out.println("Window maximized");
				UserMessageInstance clone = msg.clone();
				msg.setMaximized(true);
				msg.setCollapsed(false);
				msg.setMinimized(false);
				msg.setRestoreX(msg.getX());
				msg.setRestoreY(msg.getY());
				updateUserMessage(clone);
			}
			public void windowHide(WindowEvent we) {
				UserMessageInstance clone = msg.clone();
				msg.setStatus('X');
				updateUserMessage(clone);
			}
			public void windowRestore(WindowEvent we) {
				System.out.println("Window restore");
				UserMessageInstance clone = msg.clone();
				msg.setMaximized(false);
				msg.setCollapsed(false);
				msg.setMinimized(false);
				updateUserMessage(clone);
			}
		});
	}

	protected void onRender(Element parent, int pos) {
		super.onRender(parent, pos);
		//	On render, reposition the message if necessary
		if (msg.getZ() >= 0)
			setZIndex(msg.getZ());
		
		if (msg.isMinimized()) {
			msg.setY(msg.getRestoreY());	//	For a moment, make the window think it's at the old Y, so it will restore back there
			minimize();
		} else if (msg.isCollapsed())
			collapse();
		else if (msg.isMaximized())
			maximize();
		else if (msg.getX() >= 0 && msg.getY() >= 0)
			setPosition(msg.getX(), msg.getY());
	}
	 
	protected void onPosition(int x, int y) {
		super.onPosition(x, y);
		UserMessageInstance clone = msg.clone();
		msg.setX(x);
		msg.setY(y);
		updateUserMessage(clone);
	}
	
	protected void onEndResize(ResizeEvent re) {
		super.onEndResize(re);
		UserMessageInstance clone = msg.clone();
		msg.setWidth(getWidth());
		msg.setHeight(getHeight());
		updateUserMessage(clone);
	}
	
	protected void onCollapse() {
		super.onCollapse();
		UserMessageInstance clone = msg.clone();
		msg.setCollapsed(true);
		updateUserMessage(clone);
	}
	
	protected void onExpand() {
		super.onExpand();
		UserMessageInstance clone = msg.clone();
		msg.setCollapsed(false);
		msg.setMinimized(false);
		updateUserMessage(clone);
	}

	private void updateUserMessage(UserMessageInstance prevMsg) {
		if (!msg.windowEqual(prevMsg)) {
			updateUserMessage();
		}
	}
	
	public void setTitle() {
		String date = msg.getCreated();
		
		//	Strip the seconds... it's too busy that way
		if (date.matches("^.*[0-9]+:[0-9]+:[0-9]+\\.[0-9]+$")) {
			date = date.replaceFirst(":[0-9]+\\.[0-9]+$", "");
		}
		
		setTitle("Note: " + date);
//		setTitle(msg.getText());
	}
	
	public void setTitle(String title) {
		if (title == null)
			setHeading("New Note");
		if (title.length() > 30) // Don't let note titles be too long
			title = title.substring(0,29) + "...";
		setHeading(title);
	}
	
	private void updateUserMessage() {
		updateUserMessageService.updateUserMessage(msg,
				new AsyncCallback<Integer>() {
					public void onFailure(Throwable caught) {
						// Show the RPC error message to the user
						MessageBox.alert("Alert", "Note update failed unexpectedly.", null);
					}

					public void onSuccess(Integer id) {
						if (msg.getId() != id.intValue()) {
							msg.setId(id);
						}
						setTitle();
					}
			});
	}
}
