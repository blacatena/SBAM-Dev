package com.scholastic.sbam.client.uiobjects.foundation;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.layout.CenterLayout;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
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
	/**
	 * The amount of space to leave at the top and bottom combined between the embedded panel and the container.
	 */
	protected int					verticalMargins = 40;

	public DualEditGridContainer(Component mainGrid, Component childGrid) {
		this.mainGrid = mainGrid;
		this.childGrid = childGrid;
		
		((DualEditGridLink) mainGrid).setGridLinker(this);
		((DualEditGridLink) childGrid).setGridLinker(this);
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
		cntntpnlDualGrid.setBodyStyleName("dual-grid-bg");	// Get rid of the ugly white space, and make the container transparent instead
		
		cardLayout = new CardLayout();
		cntntpnlDualGrid.setLayout(cardLayout);
		
		mainPanel = new CardPanel();
		mainPanel.setBorders(false);
		mainPanel.setLayout(new FitLayout());
		cntntpnlDualGrid.add(mainPanel);
		
		childPanel = new CardPanel();
		childPanel.setBorders(false);
		childPanel.setLayout(new FitLayout());
		cntntpnlDualGrid.add(childPanel);
		
		if (mainGrid instanceof BetterEditGrid) {
			((BetterEditGrid<?>) mainGrid).setVerticalMargins(0);
		}
		if (childGrid instanceof BetterEditGrid) {
			((BetterEditGrid<?>) childGrid).setVerticalMargins(0);
		}
		
		mainPanel.add(mainGrid);
		childPanel.add(childGrid);
		
		addResizeListener();
		
		add(cntntpnlDualGrid);
	
		resizePanelHeight();
	}
	
	/**
	 * Add a listener to detect a change in the parent container size, and resize the grid panel
	 */
	public void addResizeListener() {
		if (getParent() != null && getParent() instanceof LayoutContainer) {
			LayoutContainer c = (LayoutContainer) getParent();
			c.addListener(Events.Resize, new Listener<BaseEvent>() {

				@Override
				public void handleEvent(BaseEvent be) {
					if (be.getType().getEventCode() == Events.ResizeEnd.getEventCode()) {
						resizePanelHeight();
					}
				}
				
			});
		}
	}
	
	/**
	 * Resize the panel height based on the parent container height
	 */
	public void resizePanelHeight() {
		if (forceHeight <= 0 && getParent() != null && isRendered()) {
			int newHeight = getParent().getOffsetHeight();
			if (newHeight > verticalMargins)
				newHeight -= verticalMargins;
			if (!cntntpnlDualGrid.isRendered() || cntntpnlDualGrid.getHeight() != newHeight) {
				cntntpnlDualGrid.setHeight(newHeight);
				if (cntntpnlDualGrid.isRendered())
					layout(true);
			}
		}
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

	public int getVerticalMargins() {
		return verticalMargins;
	}

	public void setVerticalMargins(int verticalMargins) {
		this.verticalMargins = verticalMargins;
	}

}
