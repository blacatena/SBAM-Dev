package com.scholastic.sbam.client.uiobjects;

import com.extjs.gxt.ui.client.widget.Composite;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.layout.CenterLayout;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.layout.AnchorLayout;
import com.extjs.gxt.ui.client.widget.layout.AnchorData;
import com.google.gwt.user.client.ui.Widget;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.extjs.gxt.ui.client.widget.tips.ToolTipConfig;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.widget.button.ButtonBar;

public class AppHolder extends Composite {
	private LayoutContainer headerContainer;
	private LayoutContainer centerContainer;
	private LayoutContainer footerContainer;
	private Html 			htmlHeader;
	private LayoutContainer contentContainer;
	private Html 			htmlsbamrocks;
	private LayoutContainer titleContainer;
	private LayoutContainer toolbarContainer;
	private ButtonBar 		buttonBar;

	public AppHolder() {
		initAppHolder(null, null, null, null);
	}
	/**
	 * @wbp.parser.constructor
	 */
	public AppHolder(Widget headerWidget, Widget contentWidget, Widget footerWidget, LoginUiManager	loginUiManager) {
		initAppHolder(headerWidget, contentWidget, footerWidget, loginUiManager);
	}
	
	public void initAppHolder(Widget headerWidget, Widget contentWidget, Widget footerWidget, LoginUiManager	loginUiManager) {
		final LoginUiManager thisLoginUiManager = loginUiManager;
		
		LayoutContainer anchorContainer = new LayoutContainer();
		anchorContainer.setLayout(new AnchorLayout());
		
		headerContainer = new LayoutContainer();
		AnchorData ad_headerContainer = new AnchorData("100% ");
		ad_headerContainer.setMargins(new Margins(3, 3, 3, 3));
		headerContainer.setLayout(new AnchorLayout());
		
		LayoutContainer logoutContainer = new LayoutContainer();
		logoutContainer.setWidth(200);
		logoutContainer.setLayout(new FlowLayout(5));
		headerContainer.add(logoutContainer, new AnchorData(""));
		if (headerWidget != null)
			logoutContainer.add(headerWidget);
		
		titleContainer = new LayoutContainer();
		headerContainer.add(titleContainer, new AnchorData("-100"));
		titleContainer.setLayout(new CenterLayout());
		
		htmlHeader = new Html("<h1>Scholastic Site Based Authentication Management System</h1>");
		titleContainer.add(htmlHeader);
		titleContainer.setWidth("");
		anchorContainer.add(headerContainer, ad_headerContainer);
		headerContainer.setSize("", "");
		headerContainer.setHeight(30);
		
		centerContainer = new LayoutContainer();
		
		contentContainer = new LayoutContainer();
		contentContainer.setLayout(new FitLayout());
		
		if (contentWidget == null) {
			htmlsbamrocks = new Html("<b>SBAM</b><br/>Rocks");
			contentContainer.add(htmlsbamrocks);
		} else {
			contentContainer.add(contentWidget);
		}
		centerContainer.setLayout(new FitLayout());
		centerContainer.add(contentContainer);
		contentContainer.setBorders(true);
		AnchorData ad_centerContainer = new AnchorData("-0 -70");
		ad_centerContainer.setMargins(new Margins(10, 10, 10, 10));
		anchorContainer.add(centerContainer, ad_centerContainer);
		centerContainer.setSize("", "");
		centerContainer.addStyleName("x-panel");
		
		footerContainer = new LayoutContainer();
		AnchorData ad_footerContainer = new AnchorData("100%");
		ad_footerContainer.setMargins(new Margins(3, 3, 3, 3));
		footerContainer.setLayout(new CenterLayout());
		
		toolbarContainer = new LayoutContainer();
		footerContainer.add(toolbarContainer);
		
		buttonBar = new ButtonBar();
		buttonBar.setWidth(100);
		
		Button btnNewNote = new Button("Note");
		ToolTipConfig config = new ToolTipConfig();
//		config.setTitle("What's This?");  
		config.setText("Use this to create an editable sticky note (private to your user name) that will be retained from one session to the next.");  
		config.setMouseOffset(new int[] {0, -10});
		config.setTrackMouse(true);
		config.setAnchor("left");  
		btnNewNote.setToolTip(config);
		buttonBar.add(btnNewNote);
		btnNewNote.addSelectionListener(new SelectionListener<ButtonEvent>() {
			public void componentSelected(ButtonEvent ce) {
				thisLoginUiManager.createStickyNote("");
			}
		});
		toolbarContainer.add(buttonBar);
		toolbarContainer.setLayout(new FlowLayout(1));
		anchorContainer.add(footerContainer, ad_footerContainer);
		footerContainer.setSize("", "");
		
		if (footerWidget != null) {
			footerContainer.add(footerWidget);
		}
		footerContainer.setHeight(40);
		initComponent(anchorContainer);
		anchorContainer.setBorders(true);
	}

	public LayoutContainer getHeaderContainer() {
		return headerContainer;
	}

	public void setHeaderContainer(LayoutContainer headerContainer) {
		this.headerContainer = headerContainer;
	}

	public LayoutContainer getContentContainer() {
		return centerContainer;
	}

	public void setContentContainer(LayoutContainer contentContainer) {
		this.centerContainer = contentContainer;
	}

	public LayoutContainer getFooterContainer() {
		return footerContainer;
	}

	public void setFooterContainer(LayoutContainer footerContainer) {
		this.footerContainer = footerContainer;
	}
	

}
