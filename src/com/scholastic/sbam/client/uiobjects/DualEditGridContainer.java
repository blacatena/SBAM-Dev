package com.scholastic.sbam.client.uiobjects;

import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.layout.CenterLayout;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.layout.CardLayout;
import com.extjs.gxt.ui.client.widget.CardPanel;
import com.google.gwt.user.client.Element;

public class DualEditGridContainer extends LayoutContainer implements AppSleeper, DualEditGridLinker {
	
	private CardPanel childPanel;
	private CardPanel mainPanel;
	private ContentPanel cntntpnlDualGrid;
	private CardLayout cardLayout;
	
	private Component mainGrid;
	private Component childGrid;
	
	private int forceWidth	=	0;
	private int forceHeight	=	0;

	public DualEditGridContainer(Component mainGrid, Component childGrid) {	
		this.mainGrid = mainGrid;
		this.childGrid = childGrid;
	}
	
	@Override
	public void onRender(Element parent, int index) {
		super.onRender(parent, index);
		
		setLayout(new CenterLayout());
		setBorders(false);
		
		cntntpnlDualGrid = new ContentPanel();
	//	cntntpnlDualGrid.setHeading("Dual Grid");
		cntntpnlDualGrid.setHeaderVisible(false);
		cntntpnlDualGrid.setCollapsible(false);
		cntntpnlDualGrid.setWidth(forceWidth);
		cntntpnlDualGrid.setHeight(forceHeight);
		cntntpnlDualGrid.setBorders(false);
		cntntpnlDualGrid.setBodyBorder(false);
		
		cardLayout = new CardLayout();
		cntntpnlDualGrid.setLayout(cardLayout);
		
		mainPanel = new CardPanel();
		mainPanel.setBorders(false);
		cntntpnlDualGrid.add(mainPanel);
		
		childPanel = new CardPanel();
		childPanel.setBorders(false);
		cntntpnlDualGrid.add(childPanel);
		
		((DualEditGridLink) mainGrid).setGridLinker(this);
		((DualEditGridLink) childGrid).setGridLinker(this);
		
		mainPanel.add(mainGrid);
		childPanel.add(childGrid);
		
		add(cntntpnlDualGrid);
	}

	@Override
	public void awaken() {
		((AppSleeper) mainGrid).awaken();
		((AppSleeper) childGrid).awaken();
	}

	@Override
	public void sleep() {
		((AppSleeper) mainGrid).sleep();
		((AppSleeper) childGrid).sleep();
	}

	@Override
	public void showParent(Object... args) {
		((DualEditGridLink) mainGrid).prepareForActivation(args);
		cardLayout.setActiveItem(mainPanel);
	}

	@Override
	public void showChild(Object... args) {		
		((DualEditGridLink) childGrid).prepareForActivation(args);
		cardLayout.setActiveItem(childPanel);
	}

	public int getForceWidth() {
		return forceWidth;
	}

	public void setForceWidth(int forceWidth) {
		this.forceWidth = forceWidth;
	}

	public int getForceHeight() {
		return forceHeight;
	}

	public void setForceHeight(int forceHeight) {
		this.forceHeight = forceHeight;
	}

}
