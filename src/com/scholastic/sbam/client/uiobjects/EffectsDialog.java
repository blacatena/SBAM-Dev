package com.scholastic.sbam.client.uiobjects;

import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FxEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.fx.FxConfig;
import com.extjs.gxt.ui.client.widget.ModalPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * This customized version of GXT Dialog re-implements the original show and hide methods in a way that
 * permits animation, and continues the after show/hide logic only after the animation completes.
 * 
 * @author Bob Lacatena
 *
 */
public class EffectsDialog extends ExtendableDialog {
	
	public int getHideAnimateTime() {
		return 250;
	}
	
	public int getShowAnimateTime() {
		return 250;
	}
	
	@Override
	public void show() {
		show(true);
	}
	
	public void show(boolean animate) {
	    if (!hidden || !fireEvent(Events.BeforeShow, new WindowEvent(this))) {
	      return;
	    }
	    // remove hide style, else layout fails
	    removeStyleName(getHideMode().value());
	    if (!isAttached()) {
	      RootPanel.get().add(this);
	    }
	    el().setVisibility(false);
	    el().makePositionable(true);
	    if (animate) {
	    	animateShow();
	    } else {
	    	onShow();
	    	afterAnimateShow();
	    }
	}

	public void afterAnimateShow() {
	    manager.register(this);

	    afterShow();
	    notifyShow();
	}
	
	protected void animateShow() {
		final EffectsDialog thisDialog = this;
		
		if (isRendered()) {
			  el().fadeIn(new FxConfig(getShowAnimateTime(), new Listener<FxEvent>() {
					@Override
					public void handleEvent(FxEvent be) {
						thisDialog.afterAnimateShow();
					}
				  }));
		} else
			onShow();
	}
	
	@Override
	public void hide(Button buttonPressed) {
	    if (hidden || !fireEvent(Events.BeforeHide, new WindowEvent(this, buttonPressed))) {
	        return;
	      }

	      if (dragger != null) {
	        dragger.cancelDrag();
	      }

	      hidden = true;

	      if (!maximized) {
	        restoreSize = getSize();
	        restorePos = getPosition(true);
	      }

	      if (modalPreview != null) {
	        modalPreview.removeHandler();
	        modalPreview = null;
	      }

	      animateHide(buttonPressed);
	}

	public void afterAnimateHide(Button buttonPressed) {
	      manager.unregister(this);
	      if (removeFromParentOnHide) {
	        removeFromParent();
	      }

	      if (modalPanel != null) {
	        ModalPanel.push(modalPanel);
	        modalPanel = null;
	      }

	      eventPreview.remove();
	      notifyHide();

	      if (restoreWindowScrolling != null) {
	        com.google.gwt.dom.client.Document.get().enableScrolling(restoreWindowScrolling.booleanValue());
	      }

	      fireEvent(Events.Hide, new WindowEvent(this, buttonPressed));	
	}
	
	protected void animateHide(final Button buttonPressed) {
		final EffectsDialog thisDialog = this;
		
		if (isRendered()) {
			  el().fadeOut(new FxConfig(getHideAnimateTime(), new Listener<FxEvent>() {
					@Override
					public void handleEvent(FxEvent be) {
						thisDialog.afterAnimateHide(buttonPressed);
					}
				  }));
		} else
			onHide();
	}
}
