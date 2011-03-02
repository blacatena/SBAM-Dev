package com.scholastic.sbam.client.uiobjects;

import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.fx.Resizable;
import com.extjs.gxt.ui.client.widget.button.ToolButton;
import com.extjs.gxt.ui.client.widget.custom.Portlet;
import com.google.gwt.user.client.Element;
import com.scholastic.sbam.client.uiobjects.HelpTextDialog;

public class AppPortlet extends Portlet {
	
	protected String	helpTextId;
	protected boolean	resizeable = false;	//	This doesn't quite work... Enclosing portal has to be adjusted somehow, to reposition the other portlets after resizing
	protected Resizable	resizer;
	
	public AppPortlet(String helpTextId) {
		super();
		this.helpTextId = helpTextId;
	}
	
	@Override
	protected void onRender(Element el, int index) {
		super.onRender(el, index);
		if (resizeable) {
			resizer = new Resizable(this);
			resizer.setDynamic(true);
			resizer.setMaxWidth(this.getWidth());
			resizer.setMinWidth(this.getWidth());
		}
	}

	@Override
	protected void initTools() {
		addHelp();
		super.initTools();
		addClosable();
	}
	
	@Override
	protected void onResize(int width, int height) {
		super.onResize(width, height);
		if (resizer != null) {
			resizer.setMaxWidth(this.getWidth());
			resizer.setMaxHeight(this.getHeight());
		}
	}
	
	protected void addHelp() {
		if (helpTextId == null)
			return;
		
		ToolButton helpBtn = new ToolButton("x-tool-help");
//		if (GXT.isAriaEnabled()) {
//			helpBtn.setTitle(GXT.MESSAGES.pagingToolBar_beforePageText());
//		}
		helpBtn.addListener(Events.Select, new Listener<ComponentEvent>() {
			public void handleEvent(ComponentEvent ce) {
				HelpTextDialog htd = new HelpTextDialog(helpTextId);
				htd.show();
			}
		});
		head.addTool(helpBtn);
	}
	
	protected void addClosable() {
		
		ToolButton closeBtn = new ToolButton("x-tool-close");
		if (GXT.isAriaEnabled()) {
			closeBtn.setTitle(GXT.MESSAGES.messageBox_close());
		}
		closeBtn.addListener(Events.Select, new Listener<ComponentEvent>() {
			public void handleEvent(ComponentEvent ce) {
				removeFromParent();
			}
		});
		head.addTool(closeBtn);
	}

	public String getHelpTextId() {
		return helpTextId;
	}

	public void setHelpTextId(String helpTextId) {
		this.helpTextId = helpTextId;
	}

	public boolean isResizeable() {
		return resizeable;
	}

	public void setResizeable(boolean resizeable) {
		this.resizeable = resizeable;
		if (resizeable)
			System.out.println("WARNING: Attempt made to make a portlet resizeable, when there are bugs in it.");
	}
	
}
