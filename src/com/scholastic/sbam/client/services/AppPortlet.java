package com.scholastic.sbam.client.services;

import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.button.ToolButton;
import com.extjs.gxt.ui.client.widget.custom.Portlet;
import com.scholastic.sbam.client.uiobjects.HelpTextDialog;

public class AppPortlet extends Portlet {
	
	protected String helpTextId;
	
	public AppPortlet(String helpTextId) {
		super();
		this.helpTextId = helpTextId;
	}

	@Override
	protected void initTools() {
		addHelp();
		super.initTools();
		addClosable();
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
}
