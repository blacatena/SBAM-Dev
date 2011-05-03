package com.scholastic.sbam.client.uiobjects.uitop;

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.widget.Composite;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.layout.CenterLayout;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.google.gwt.user.client.ui.Widget;
import com.scholastic.sbam.client.util.IconSupplier;
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
		anchorContainer.setLayout(new BorderLayout());
		setBorders(false);
		
		//	Create a container for the header elements
		
		headerContainer = new LayoutContainer();
		headerContainer.setLayout(new BorderLayout());
		headerContainer.setHeight(30);
		headerContainer.setStyleAttribute("background-color", "white");
		
		//	Create a separate container for the logout widget
		
		LayoutContainer logoutContainer = new LayoutContainer();
		logoutContainer.setWidth(180);
		logoutContainer.setLayout(new FlowLayout(5));
		if (headerWidget != null)
			logoutContainer.add(headerWidget);
		
		//	Create a container for the site page heading
		
		titleContainer = new LayoutContainer();
		titleContainer.setHeight(30);
		titleContainer.setLayout(new CenterLayout());
//		titleContainer.setStyleAttribute("background-color", "white");
		
		htmlHeader = new Html("<h1 style=\"padding-top: 10px;\"><img src=\"resources/images/logo/logo.png\" align=\"absmiddle\" width=\"24\" height=\"24\" style=\"margin-top: -4px;\" />&nbsp;Scholastic Site Based Authentication Management System</h1>");
		titleContainer.add(htmlHeader);
		
		//	Create a container for the right side toolbar (currently there's only a "new note" button
		
		toolbarContainer = new LayoutContainer();
		
		buttonBar = new ButtonBar();
		buttonBar.setWidth(100);
		
		Button btnNewNote = new Button("New Note");
		IconSupplier.forceIcon(btnNewNote, IconSupplier.getNoteAddIconName());
		
		ToolTipConfig config = new ToolTipConfig();
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
		toolbarContainer.setWidth(100);
		
		//	Add the three containers (logout, header/title, right toolbar) to the header area
		
		headerContainer.add(logoutContainer, new BorderLayoutData(LayoutRegion.WEST, 180f, 180, 180));
		headerContainer.add(titleContainer, new BorderLayoutData(LayoutRegion.CENTER));
		headerContainer.add(toolbarContainer, new BorderLayoutData(LayoutRegion.EAST, 100f, 100, 100));
		
		//	Create the center container for the application
		
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
		contentContainer.setBorders(false);
		contentContainer.setStyleAttribute("padding", "4px");
		centerContainer.setSize("", "");
		centerContainer.addStyleName("x-panel");
		
		//	Add the header and center containers to the page
		
//		AnchorData ad_headerContainer = new AnchorData("100% ");
//		ad_headerContainer.setMargins(new Margins(3, 3, 3, 3));
//		anchorContainer.add(headerContainer, ad_headerContainer);
//		
//		AnchorData ad_centerContainer = new AnchorData("-0 -0");
//		ad_centerContainer.setMargins(new Margins(5, 5, 5, 5));
//		anchorContainer.add(centerContainer, ad_centerContainer);
		
		anchorContainer.add(headerContainer, new BorderLayoutData(LayoutRegion.NORTH, 40f, 40, 40));
		anchorContainer.add(centerContainer, new BorderLayoutData(LayoutRegion.CENTER));
		
		//	If there is a footer widget, add a footer container with that
		
		if (footerWidget != null) {
			footerContainer = new LayoutContainer();
			footerContainer.setLayout(new CenterLayout());
			footerContainer.setHeight(40);
		
			footerContainer.add(footerWidget);
		
//			AnchorData ad_footerContainer = new AnchorData("100%");
//			ad_footerContainer.setMargins(new Margins(3, 3, 3, 3));
//			anchorContainer.add(footerContainer, ad_footerContainer);
			anchorContainer.add(footerContainer, new BorderLayoutData(LayoutRegion.SOUTH, 40f, 40, 40));
		}
		
		//	Finish up
		
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
