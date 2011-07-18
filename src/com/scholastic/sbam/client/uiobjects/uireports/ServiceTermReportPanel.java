package com.scholastic.sbam.client.uiobjects.uireports;

import java.util.List;

import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.ToggleButton;
import com.extjs.gxt.ui.client.widget.layout.CardLayout;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.user.client.Element;
import com.scholastic.sbam.client.uiobjects.foundation.AppSleeper;
import com.scholastic.sbam.client.uiobjects.foundation.FitCardLayout;
import com.scholastic.sbam.client.uiobjects.foundation.GridSupportContainer;
import com.scholastic.sbam.shared.objects.AgreementTermInstance;
import com.scholastic.sbam.shared.objects.SnapshotInstance;

public class ServiceTermReportPanel extends GridSupportContainer<AgreementTermInstance> implements SnapshotParentCardPanel, AppSleeper {
	
	protected ContentPanel					outerContainer;
	protected CardLayout					cards;

	protected SnapshotSelectorCard			snapshotSelectorCard;
	protected CustomerSelectionCard			customersCard;
	protected ServiceSelectionCard			servicesCard;
	protected TermCriteriaCard				criteriaCard;
	protected TermReportViewDataCard		viewDataCard;

	protected ToggleButton					selectorButton;
	protected ToggleButton					customersButton;
	protected ToggleButton					servicesButton;
	protected ToggleButton					criteriaButton;
	protected ToggleButton					viewDataButton;
	
	protected SnapshotInstance				targetSnapshot;

	@Override
	public void onRender(Element element, int index) {
		super.onRender(element, index);
		
		setLayout(new FitLayout());
		
//		setToolTip(UiConstants.getLazyTip("Use this panel to generate and access data snapshots by final service."));
		
		outerContainer = new ContentPanel();
		outerContainer.setBorders(false);
		outerContainer.setHeaderVisible(false);
		addPanelSwitchTools();
		
		cards = new FitCardLayout();
		outerContainer.setLayout(cards);
		
		snapshotSelectorCard = new SnapshotSelectorCard(SnapshotInstance.TERMS_BY_SERVICE);
		snapshotSelectorCard.setParentCardPanel(this);
		outerContainer.add(snapshotSelectorCard);
		
		customersCard = new CustomerSelectionCard();
		customersCard.setParentCardPanel(this);
		outerContainer.add(customersCard);
		
		servicesCard = new ServiceSelectionCard();
		servicesCard.setParentCardPanel(this);
		outerContainer.add(servicesCard);
		
		criteriaCard = new TermCriteriaCard();
		criteriaCard.setParentCardPanel(this);
		outerContainer.add(criteriaCard);
		
		viewDataCard = new TermReportViewDataCard();
		viewDataCard.setParentCardPanel(this);
		outerContainer.add(viewDataCard);
		
		add(outerContainer);
	}
	
	/**
	 * Add the toolbar buttons
	 */
	protected void addPanelSwitchTools() {
		
//		final int MIN_BUTTON_WIDTH = 80;
//		String toggleGroup = "sr" + System.currentTimeMillis();
//		
//		ToolBar toolBar = new ToolBar();
//		toolBar.setAlignment(HorizontalAlignment.CENTER);
//		toolBar.setBorders(false);
//		toolBar.setSpacing(20);
//		toolBar.setToolTip(UiConstants.getLazyTip("Use these buttons to choose or define a data selector."));
//		toolBar.getToolTip().getToolTipConfig().setAnchorToTarget(true);
//		toolBar.getToolTip().getToolTipConfig().setTrackMouse(true);
//		toolBar.getToolTip().getToolTipConfig().setShowDelay(3000);
//		
//		selectorButton = new ToggleButton("Snapshot Selector");
//		selectorButton.setMinWidth(MIN_BUTTON_WIDTH);
//		selectorButton.setToolTip(UiConstants.getQuickTip("Select an old or define a new snapshot."));
//		IconSupplier.forceIcon(selectorButton, IconSupplier.getSnapshotIconName());
//		selectorButton.addSelectionListener(new SelectionListener<ButtonEvent>() {  
//				@Override
//				public void componentSelected(ButtonEvent ce) {
//					cards.setActiveItem(snapshotSelectorCard);
//					selectorButton.toggle(true);
//				}  
//			});
//		selectorButton.setToggleGroup(toggleGroup);
//		toolBar.add(selectorButton);
//		
//		customersButton = new ToggleButton("Customers");
//		customersButton.setMinWidth(MIN_BUTTON_WIDTH);
//		customersButton.setToolTip(UiConstants.getQuickTip("Define the customers included in this selection."));
//		IconSupplier.forceIcon(customersButton, IconSupplier.getCustomerIconName());
//		customersButton.addSelectionListener(new SelectionListener<ButtonEvent>() {  
//				@Override
//				public void componentSelected(ButtonEvent ce) {
//					cards.setActiveItem(customersCard);
//					customersButton.toggle(true);
//				}  
//			});
//		customersButton.setToggleGroup(toggleGroup);
//		toolBar.add(customersButton);
//		
//		servicesButton = new ToggleButton("Services");
//		servicesButton.setMinWidth(MIN_BUTTON_WIDTH);
//		servicesButton.setToolTip(UiConstants.getQuickTip("Define the services included in this selection."));
//		IconSupplier.forceIcon(servicesButton, IconSupplier.getServiceIconName());
//		servicesButton.addSelectionListener(new SelectionListener<ButtonEvent>() {  
//				@Override
//				public void componentSelected(ButtonEvent ce) {
//					cards.setActiveItem(servicesCard);
//					servicesButton.toggle(true);
//				}  
//			});
//		servicesButton.setToggleGroup(toggleGroup);
//		toolBar.add(servicesButton);
//		
//		criteriaButton = new ToggleButton("Terms");
//		criteriaButton.setMinWidth(MIN_BUTTON_WIDTH);
//		criteriaButton.setToolTip(UiConstants.getQuickTip("Define general criteria for this selection."));
//		IconSupplier.forceIcon(criteriaButton, IconSupplier.getAgreementTermIconName());
//		criteriaButton.addSelectionListener(new SelectionListener<ButtonEvent>() {  
//				@Override
//				public void componentSelected(ButtonEvent ce) {
//					cards.setActiveItem(criteriaCard);
//					criteriaButton.toggle(true);
//				}  
//			});
//		criteriaButton.setToggleGroup(toggleGroup);
//		toolBar.add(criteriaButton);
//		
//		viewDataButton = new ToggleButton("View Data");
//		viewDataButton.setMinWidth(MIN_BUTTON_WIDTH);
//		viewDataButton.setToolTip(UiConstants.getQuickTip("View the data for this selection."));
//		IconSupplier.forceIcon(viewDataButton, IconSupplier.getReportIconName());
//		viewDataButton.addSelectionListener(new SelectionListener<ButtonEvent>() {  
//				@Override
//				public void componentSelected(ButtonEvent ce) {
//					cards.setActiveItem(viewDataCard);
//					viewDataButton.toggle(true);
//				}
//			});
//		viewDataButton.setToggleGroup(toggleGroup);
//		toolBar.add(viewDataButton);
//
//		selectorButton.toggle(true);
//		
//		outerContainer.setTopComponent(toolBar);
	}
	
	@Override
	public void awaken() {
	}

	@Override
	public void sleep() {
	}
	
	@Override
	public void setTargetSnapshot(SnapshotInstance snapshot) {
		this.targetSnapshot = snapshot;
	}

	@Override
	public void switchLayout(int id) {
		
		if (id == SnapshotParentCardPanel.SNAPSHOT_SELECTOR_PANEL) {
			cards.setActiveItem(snapshotSelectorCard);
			return;
		}

		//	All remaining cards require a snapshot ID
		if (!haveSnapshot())
			return;
		
		if (id == SnapshotParentCardPanel.SERVICE_SELECTOR_PANEL) {
			servicesCard.setSnapshot(targetSnapshot);
			cards.setActiveItem(servicesCard);
			return;
		}
		
		if (id == SnapshotParentCardPanel.CUSTOMER_SELECTOR_PANEL) {
			customersCard.setSnapshot(targetSnapshot);
			cards.setActiveItem(customersCard);
			return;
		}
		
		if (id == SnapshotParentCardPanel.CRITERIA_PANEL) {
			criteriaCard.setSnapshot(targetSnapshot);
			cards.setActiveItem(criteriaCard);
			return;
		}
		
		if (id == SnapshotParentCardPanel.VIEW_DATA_PANEL) {
			viewDataCard.setSnapshot(targetSnapshot);
			cards.setActiveItem(viewDataCard);
			return;
		}
		
		MessageBox.alert("Internal Error", "Attempted to switch to unknown layout ID " + id, null);
	}
	
	public boolean haveSnapshot() {
		if (targetSnapshot != null)
			return true;

		MessageBox.alert("Internal Error", "Attempted to switch panels without snapshot ID.", null);
		return false;
	}

	@Override
	public void switchLayout(LayoutContainer container) {
		if (container == snapshotSelectorCard)
			switchLayout(SnapshotParentCardPanel.SNAPSHOT_SELECTOR_PANEL);
		else if (container == servicesCard)
			switchLayout(SnapshotParentCardPanel.SERVICE_SELECTOR_PANEL);
//		else if (container == productsCard)
//			switchLayout(SnapshotParentCardPanel.PRODUCT_SELECTOR_PANEL);
		else if (container == customersCard)
			switchLayout(SnapshotParentCardPanel.CUSTOMER_SELECTOR_PANEL);
		else if (container == criteriaCard)
			switchLayout(SnapshotParentCardPanel.CRITERIA_PANEL);
		else if (container == viewDataCard)
			switchLayout(SnapshotParentCardPanel.VIEW_DATA_PANEL);
	}

	@Override
	public List<LayoutContainer> getCards() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void reflectSnapshotChanges(SnapshotInstance snapshot) {
		System.out.println("Refelct change from snapshot " + snapshot.getSnapshotId());
		snapshotSelectorCard.reflectSnapshotChanges(snapshot);
	}

}
