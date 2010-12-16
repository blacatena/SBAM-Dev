package com.scholastic.sbam.client.uiobjects;

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

//		title = msg.getText();
//		if (title.length() > 20) 
//			title = title.substring(1,20) + "É";

		init();
		System.out.println("Now set size to " + msg.getWidth() + " / " + msg.getHeight());
		setSize(msg.getWidth(), msg.getHeight());
	}
	
	private void setDefaultNewNote(String userName, String location) {
		this.msg = new UserMessageInstance();
		msg.setId(-1);
		msg.setLocationTag(location);
		msg.setUserName(userName);
		msg.setStatus('A');
		msg.setCreated("");
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
//		title = "Note";
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
				System.out.println("Window activated.");
				if (isRendered()) {
					UserMessageInstance clone = msg.clone();
					msg.setZ(getPositionEl().getZIndex());
					updateUserMessage(clone);
				}
			}
			public void windowDeactivate(WindowEvent we) {
				System.out.println("Window deactivated.");
				if (isRendered()) {
					UserMessageInstance clone = msg.clone();
					msg.setZ(getPositionEl().getZIndex());
					updateUserMessage(clone);
				}
			}
			public void windowMinimize(WindowEvent we) {
				System.out.println("Window minimized");
				UserMessageInstance clone = msg.clone();
				if (isCollapsed() && getPosition(false).y == 0 && msg.getRestoreY() > 0 && msg.isMinimized()) {
					System.out.println("Minimized: expand");
					setPosition(msg.getRestoreX(), msg.getRestoreY());
					expand();
					msg.setMinimized(false);
					msg.setCollapsed(false);
					msg.setRestoreX(0);
					msg.setRestoreY(0);
				} else {
					System.out.println("Expanded: minimize");
					msg.setMinimized(true);
					msg.setCollapsed(true);
					msg.setRestoreX(getPosition(false).x);
					msg.setRestoreY(getPosition(false).y);
					setPosition(getPosition(false).x, 0);
					collapse();
				}
				updateUserMessage(clone);
			}
			public void windowMaximize(WindowEvent we) {
				System.out.println("Window maximized");
				UserMessageInstance clone = msg.clone();
				msg.setMaximized(true);
				updateUserMessage(clone);
			}
			public void windowHide(WindowEvent we) {
				System.out.println("Window hide");
				UserMessageInstance clone = msg.clone();
				msg.setStatus('X');
				updateUserMessage(clone);
			}
			public void windowRestore(WindowEvent we) {
				System.out.println("Window restore");
				UserMessageInstance clone = msg.clone();
				msg.setMaximized(false);
				updateUserMessage(clone);
			}
		});
	}

	protected void onRender(Element parent, int pos) {
		super.onRender(parent, pos);
		//	On render, reposition the message if necessary
		if (msg.getX() >= 0 && msg.getY() >= 0)
			setPosition(msg.getX(), msg.getY());
		if (msg.getZ() >= 0)
			setZIndex(msg.getZ());
		
		if (msg.isMinimized())
			minimize();
		else if (msg.isCollapsed())
			collapse();
		else if (msg.isMaximized())
			maximize();
	}
	
	protected void onPosition(int x, int y) {
		super.onPosition(x, y);
		UserMessageInstance clone = msg.clone();
		msg.setX(x);
		msg.setY(y);
		System.out.println("Update position to " + x + " , " + y);
		updateUserMessage(clone);
	}
	
//	protected void onResize(int width, int height) {
//		
//	}
	
	protected void onEndResize(ResizeEvent re) {
		super.onEndResize(re);
		UserMessageInstance clone = msg.clone();
		msg.setWidth(getWidth());
		msg.setHeight(getHeight());
		System.out.println("Update width " + getWidth() + ", height" + getHeight());
		updateUserMessage(clone);
	}
	
	protected void onCollapse() {
		super.onCollapse();
		System.out.println("Collapse");
		UserMessageInstance clone = msg.clone();
		msg.setCollapsed(true);
		updateUserMessage(clone);
	}
	
	protected void onExpand() {
		super.onExpand();
		System.out.println("Expand");
		UserMessageInstance clone = msg.clone();
		msg.setCollapsed(false);
		updateUserMessage(clone);
	}

	private void updateUserMessage(UserMessageInstance prevMsg) {
		if (!msg.windowEqual(prevMsg)) {
			System.out.println("Update");
			updateUserMessage();
		} else
			System.out.println("No update");
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
							System.out.println("Added as id " + id + " [" + msg.getId() + "]");
							msg.setId(id);
						}
//						String title = msg.getText();
//						if (title.length() > 20) 
//							title = title.substring(1,20) + "É";
//						setTitle(title);
					}
			});
	}
}
