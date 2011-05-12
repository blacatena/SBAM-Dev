package com.scholastic.sbam.client.uiobjects.foundation;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.FormPanel.LabelAlign;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Timer;
import com.scholastic.sbam.client.util.IconSupplier;

/**
 * A foundation class to implement dialogs that mask a portlet or other container without masking the entire application.
 * @author Bob Lacatena
 *
 */
public abstract class PortletMaskDialog extends Dialog {
	
	public final static int		DIRTY_FORM_LISTEN_TIME	=	250;
	
	protected LayoutContainer	constrainContainer;
	protected int				marginWidth			=	100;
	protected int				minWidth			=	200;
	protected boolean			allowSaveAndOpen	=	false;
	protected boolean			alwaysSaveAndOpen	=	false;
	
	protected Listener<BaseEvent>	moveListener;
	protected Listener<BaseEvent>	resizeListener;
	
	protected FormPanel				formPanel;
	protected Button 				cancelButton;
	protected Button 				saveButton;
	protected Button 				saveAndOpenButton;
	
	protected Timer					dirtyFormListener;
	protected int					dirtyFormListenTime = DIRTY_FORM_LISTEN_TIME;
	
	
	public PortletMaskDialog() {
		super();
		preRenderInit();
	}
	
	public PortletMaskDialog(LayoutContainer constrainContainer) {
		this();
		this.constrainContainer = constrainContainer;
	}
	
	public PortletMaskDialog(LayoutContainer constrainContainer, boolean allowSaveAndOpen) {
		this(constrainContainer);
		this.allowSaveAndOpen = allowSaveAndOpen;
	}
	
	public void preRenderInit() {
	    setConstrain(true);
	    setClosable(false);
	}
	
	@Override
	public void onRender(Element parent, int index) { 
		super.onRender(parent, index);
		init();
	}	
		
	public void init() {
		setParentListener();

		setButtons("");
	    setBodyStyleName("pad-text");
	    setLayout(new FitLayout());
	    
	    formPanel = getFormPanel();
	    
	    addFields(formPanel);
	    
	    addButtons();
	    
	    add(formPanel);
	    
	    getItem(0).getFocusSupport().setIgnore(true);  
	    setScrollMode(Scroll.NONE);
	    
		addDirtyFormListener();
	}
	
	public void setParentListener() {
		if (constrainContainer == null)
			return;
		
		if (moveListener == null) {
			moveListener = new Listener<BaseEvent>() {

					@Override
					public void handleEvent(BaseEvent be) {
						if (be.getType().getEventCode() == Events.Move.getEventCode())
							repositionDialog();
					}
		
				};
		}
		
		if (resizeListener == null) {
			resizeListener = new Listener<BaseEvent>() {

					@Override
					public void handleEvent(BaseEvent be) {
						if (be.getType().getEventCode() == Events.Resize.getEventCode())
							repositionDialog();
					}
		
				};
		}
		
		
		constrainContainer.addListener(Events.Move, moveListener);
		constrainContainer.addListener(Events.Resize, resizeListener);
	}
	
	public void removeListeners() {
		if (moveListener != null) {
			constrainContainer.removeListener(Events.Move, moveListener);
			moveListener = null;
		}
		if (resizeListener != null) {
			constrainContainer.removeListener(Events.Resize, resizeListener);
			resizeListener = null;
		}
		if (dirtyFormListener != null) {
			dirtyFormListener.cancel();
			dirtyFormListener = null;
		}
	}
	
	public void repositionDialog() {
	    if (constrainContainer != null) {
	    	constrainContainer.mask();
	    	setContainer(constrainContainer.getElement());
	    	if ( constrainContainer.getWidth() - (2 * marginWidth) < minWidth) {
	    		int newMargin = (constrainContainer.getWidth() - minWidth) / 2;
    			if (newMargin < 0)
    				newMargin = 0;
		    	setSize(constrainContainer.getWidth() - (2 * newMargin), constrainContainer.getHeight() - (2 * newMargin));
		    	setPagePosition(constrainContainer.getAbsoluteLeft() + newMargin, constrainContainer.getAbsoluteTop() + newMargin);
	    	} else {
		    	setSize(constrainContainer.getWidth() - (2 * marginWidth), constrainContainer.getHeight() - (2 * marginWidth));
		    	setPagePosition(constrainContainer.getAbsoluteLeft() + marginWidth, constrainContainer.getAbsoluteTop() + marginWidth);
	    	}
	    }
	}
	
	@Override
	public void onShow() {
	    
	    repositionDialog();
		super.onShow();
	}
	
	/**
	 * Helper method to easily get a properly formatted form panel in which to put the fields (and any buttons).
	 * @return
	 */
	protected FormPanel getFormPanel() {
		FormPanel formPanel = new FormPanel();
//		formPanel.setId("formPanelMain");
		formPanel.setPadding(5);
		formPanel.setFrame(true);
		formPanel.addStyleName("inner-panel");
		formPanel.setHeaderVisible(false);
		formPanel.setBodyBorder(false);
		formPanel.setBorders(false);
		formPanel.setBodyStyleName("subtle-form");
		formPanel.setButtonAlign(HorizontalAlignment.CENTER);
		formPanel.setLabelAlign(LabelAlign.RIGHT);
		formPanel.setLabelWidth(75);
		formPanel.setCollapsible(false);
		
		return formPanel;
	}
	
	protected void addButtons() {
		cancelButton = new Button("Cancel");
		IconSupplier.forceIcon(cancelButton, IconSupplier.getCancelIconName());
		cancelButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
				@Override
				public void componentSelected(ButtonEvent ce) {
					closeDialog();
				}
			
			});
		
		saveButton = new Button("Save");
		IconSupplier.forceIcon(saveButton, IconSupplier.getSaveIconName());
		saveButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
				@Override
				public void componentSelected(ButtonEvent ce) {
					doSave(alwaysSaveAndOpen);
				}
			
			});
		saveButton.disable();
		
		if (allowSaveAndOpen && !alwaysSaveAndOpen) {
			saveAndOpenButton = new Button("Save and Open");
			IconSupplier.forceIcon(saveAndOpenButton, IconSupplier.getSaveIconName());
			saveAndOpenButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
					@Override
					public void componentSelected(ButtonEvent ce) {
						doSave(true);
					}
				
				});
			saveAndOpenButton.disable();
		}
		
		ToolBar toolBar = new ToolBar();
		toolBar.setAlignment(HorizontalAlignment.CENTER);
		toolBar.setSpacing(20);
		
		toolBar.add(cancelButton);
		toolBar.add(saveButton);
		if (allowSaveAndOpen && !alwaysSaveAndOpen)
			toolBar.add(saveAndOpenButton);
		
		formPanel.setBottomComponent(toolBar);
	}

	protected void closeDialog() {
		if (constrainContainer != null)
			constrainContainer.unmask();
		this.hide();
		removeListeners();
		destroyFields();
	}
	
	protected void doSave(boolean openAfterSave) {
		lockTrigger();
		onSave(openAfterSave);
		closeDialog();
	}

	protected void addDirtyFormListener() {
		if (dirtyFormListener == null) {
			dirtyFormListener = new Timer() {
	
				@Override
				public void run() {
					if (formPanel.isDirty() && formPanel.isValid(true)) {
						enableSave(true);
					} else {
						enableSave(false);
					}
				}
			};
		}
		
		dirtyFormListener.scheduleRepeating(dirtyFormListenTime);
	}
	
	public void enableSave(boolean enabled) {
		saveButton.setEnabled(enabled);
		if (saveAndOpenButton != null)
			saveAndOpenButton.setEnabled(enabled);
	}
	
	@Override
	public void onUnload() {
		removeListeners();
		destroyFields();
	}

	public LayoutContainer getConstrainContainer() {
		return constrainContainer;
	}

	public void setConstrainContainer(LayoutContainer constrainContainer) {
		this.constrainContainer = constrainContainer;
	}

	public int getMarginWidth() {
		return marginWidth;
	}

	public void setMarginWidth(int marginWidth) {
		this.marginWidth = marginWidth;
	}

	public int getMinWidth() {
		return minWidth;
	}

	public void setMinWidth(int minWidth) {
		this.minWidth = minWidth;
	}

	public boolean isAllowSaveAndOpen() {
		return allowSaveAndOpen;
	}

	public void setAllowSaveAndOpen(boolean allowSaveAndOpen) {
		this.allowSaveAndOpen = allowSaveAndOpen;
	}

	public boolean isAlwaysSaveAndOpen() {
		return alwaysSaveAndOpen;
	}

	public void setAlwaysSaveAndOpen(boolean alwaysSaveAndOpen) {
		this.alwaysSaveAndOpen = alwaysSaveAndOpen;
	}

	/**
	 * Add any fields to the dialog.
	 */
	public abstract void addFields(FormPanel formPanel);

	/**
	 * If this dialog will not be reused, explicitly destroy the fields (remove from parent and set them to null).
	 */
	public abstract void destroyFields();
	
	/**
	 * The SAVE button was pressed.  Do whatever needs to be done.  Upon completion, call saveFinished();
	 * @param openAfterSave
	 * Set to true if the handler is tasked with opening the newly created entity in a window/portlet/tab after the save is complete.
	 */
	protected abstract void onSave(boolean openAfterSave);
	
	/**
	 * The save has finished, so the trigger can be unlocked.
	 */
	protected void saveFinished() {
		unlockTrigger();
	}
	
	/**
	 * Lock any triggers that can activate this dialog, so it cannot be re-triggered until the last save completes.
	 * 
	 * This will only be done when the SAVE button is pressed (the mask keeps it from happening before the dialog is dismissed).
	 * 
	 */
	public abstract void lockTrigger();
	
	/**
	 * Unlock any triggers locked by lockTrigger().  Note that this will primarily be done after a save either completes or fails.
	 * It is intended to prevent a new save from starting before any previous asynchronous save has completed.
	 */
	public abstract void unlockTrigger();
}
