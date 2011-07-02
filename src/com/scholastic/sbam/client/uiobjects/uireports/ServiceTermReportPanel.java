package com.scholastic.sbam.client.uiobjects.uireports;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.button.ToggleButton;
import com.extjs.gxt.ui.client.widget.layout.CardLayout;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.Element;
import com.scholastic.sbam.client.uiobjects.foundation.AppSleeper;
import com.scholastic.sbam.client.uiobjects.foundation.FitCardLayout;
import com.scholastic.sbam.client.uiobjects.foundation.GridSupportContainer;
import com.scholastic.sbam.client.util.IconSupplier;
import com.scholastic.sbam.client.util.UiConstants;
import com.scholastic.sbam.shared.objects.AgreementTermInstance;

public class ServiceTermReportPanel extends GridSupportContainer<AgreementTermInstance> implements AppSleeper {
	
	protected ContentPanel					outerContainer;
	protected CardLayout					cards;

	protected SnapshotSelectorCard			snapshotSelectorCard;
	protected CustomerSelectionCard			customersCard;
	protected ServiceSelectionCard			servicesCard;
	protected TermCriteriaCard				criteriaCard;
	protected TermReportSnapshotCard		viewDataCard;

	protected ToggleButton					selectorButton;
	protected ToggleButton					customersButton;
	protected ToggleButton					servicesButton;
	protected ToggleButton					criteriaButton;
	protected ToggleButton					viewDataButton;

	@Override
	public void onRender(Element element, int index) {
		super.onRender(element, index);
		
		setLayout(new FitLayout());
		
		setToolTip(UiConstants.getLazyTip("Use this panel to generate and access data snapshots by final service."));
		
		outerContainer = new ContentPanel();
		outerContainer.setBorders(false);
		outerContainer.setHeaderVisible(false);
		addPanelSwitchTools();
		
		cards = new FitCardLayout();
		outerContainer.setLayout(cards);
		
		snapshotSelectorCard = new SnapshotSelectorCard("byService");
		outerContainer.add(snapshotSelectorCard);
		
		customersCard = new CustomerSelectionCard();
		outerContainer.add(customersCard);
		
		servicesCard = new ServiceSelectionCard();
		outerContainer.add(servicesCard);
		
		criteriaCard = new TermCriteriaCard();
		outerContainer.add(criteriaCard);
		
		viewDataCard = new TermReportSnapshotCard();
		outerContainer.add(viewDataCard);
		
		add(outerContainer);
	}
	
	/**
	 * Add the toolbar buttons
	 */
	protected void addPanelSwitchTools() {
		
		final int MIN_BUTTON_WIDTH = 80;
		String toggleGroup = "sr" + System.currentTimeMillis();
		
		ToolBar toolBar = new ToolBar();
		toolBar.setAlignment(HorizontalAlignment.CENTER);
		toolBar.setBorders(false);
		toolBar.setSpacing(20);
		toolBar.setToolTip(UiConstants.getLazyTip("Use these buttons to choose or define a data selector."));
		toolBar.getToolTip().getToolTipConfig().setAnchorToTarget(true);
		toolBar.getToolTip().getToolTipConfig().setTrackMouse(true);
		toolBar.getToolTip().getToolTipConfig().setShowDelay(3000);
		
		selectorButton = new ToggleButton("Snapshot Selector");
		selectorButton.setMinWidth(MIN_BUTTON_WIDTH);
		selectorButton.setToolTip(UiConstants.getQuickTip("Select an old or define a new snapshot."));
		IconSupplier.forceIcon(selectorButton, IconSupplier.getSnapshotIconName());
		selectorButton.addSelectionListener(new SelectionListener<ButtonEvent>() {  
				@Override
				public void componentSelected(ButtonEvent ce) {
					cards.setActiveItem(snapshotSelectorCard);
					selectorButton.toggle(true);
				}  
			});
		selectorButton.setToggleGroup(toggleGroup);
		toolBar.add(selectorButton);
		
		customersButton = new ToggleButton("Customers");
		customersButton.setMinWidth(MIN_BUTTON_WIDTH);
		customersButton.setToolTip(UiConstants.getQuickTip("Define the customers included in this selection."));
		IconSupplier.forceIcon(customersButton, IconSupplier.getCustomerIconName());
		customersButton.addSelectionListener(new SelectionListener<ButtonEvent>() {  
				@Override
				public void componentSelected(ButtonEvent ce) {
					cards.setActiveItem(customersCard);
					customersButton.toggle(true);
				}  
			});
		customersButton.setToggleGroup(toggleGroup);
		toolBar.add(customersButton);
		
		servicesButton = new ToggleButton("Services");
		servicesButton.setMinWidth(MIN_BUTTON_WIDTH);
		servicesButton.setToolTip(UiConstants.getQuickTip("Define the services included in this selection."));
		IconSupplier.forceIcon(servicesButton, IconSupplier.getServiceIconName());
		servicesButton.addSelectionListener(new SelectionListener<ButtonEvent>() {  
				@Override
				public void componentSelected(ButtonEvent ce) {
					cards.setActiveItem(servicesCard);
					servicesButton.toggle(true);
				}  
			});
		servicesButton.setToggleGroup(toggleGroup);
		toolBar.add(servicesButton);
		
		criteriaButton = new ToggleButton("Terms");
		criteriaButton.setMinWidth(MIN_BUTTON_WIDTH);
		criteriaButton.setToolTip(UiConstants.getQuickTip("Define general criteria for this selection."));
		IconSupplier.forceIcon(criteriaButton, IconSupplier.getAgreementTermIconName());
		criteriaButton.addSelectionListener(new SelectionListener<ButtonEvent>() {  
				@Override
				public void componentSelected(ButtonEvent ce) {
					cards.setActiveItem(criteriaCard);
					criteriaButton.toggle(true);
				}  
			});
		criteriaButton.setToggleGroup(toggleGroup);
		toolBar.add(criteriaButton);
		
		viewDataButton = new ToggleButton("View Data");
		viewDataButton.setMinWidth(MIN_BUTTON_WIDTH);
		viewDataButton.setToolTip(UiConstants.getQuickTip("View the data for this selection."));
		IconSupplier.forceIcon(viewDataButton, IconSupplier.getReportIconName());
		viewDataButton.addSelectionListener(new SelectionListener<ButtonEvent>() {  
				@Override
				public void componentSelected(ButtonEvent ce) {
					cards.setActiveItem(viewDataCard);
					viewDataButton.toggle(true);
				}
			});
		viewDataButton.setToggleGroup(toggleGroup);
		toolBar.add(viewDataButton);

		selectorButton.toggle(true);
		
		outerContainer.setTopComponent(toolBar);
	}
	
	@Override
	public void awaken() {
	}

	@Override
	public void sleep() {
	}

}
