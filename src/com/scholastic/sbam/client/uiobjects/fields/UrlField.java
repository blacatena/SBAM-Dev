package com.scholastic.sbam.client.uiobjects.fields;

import java.util.List;

import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.Style.HideMode;
import com.extjs.gxt.ui.client.core.XDOM;
import com.extjs.gxt.ui.client.widget.ComponentHelper;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.WidgetComponent;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Image;
import com.scholastic.sbam.client.services.UrlValidationService;
import com.scholastic.sbam.client.services.UrlValidationServiceAsync;
import com.scholastic.sbam.client.util.IconSupplier;
import com.scholastic.sbam.shared.objects.MethodIdInstance;
import com.scholastic.sbam.shared.validation.AsyncValidationResponse;

@SuppressWarnings("deprecation")
public class UrlField extends TextField<String> {
	
	protected int				validationCounter	= 0;
	protected String			lastUrlValidated	= "";
	protected MethodIdInstance	lastMethodId		= MethodIdInstance.getEmptyInstance();
	protected List<String>		asyncMessages		= null;
	protected List<String>		asyncAlertMessages	= null;
	protected List<String>		asyncInfoMessages	= null;
	protected MethodIdInstance	methodId			= MethodIdInstance.getEmptyInstance();
	

	protected WidgetComponent	infoIcon;
	protected String			infoStyle	= "x-form-info";
	protected String			alertStyle	= "x-form-info-alert";
	protected String			activeInfoMessage;
	
	protected UrlValidationServiceAsync validationService = GWT.create(UrlValidationService.class);

	@Override
	protected boolean validateValue(String value) {
		if (!getAllowBlank())
			if (value == null || value.trim().length() == 0) {
				markInvalid("A URL is required.");
				return false;
			}
		
		if (!validateFormat(value)) {
			markInvalid("A URL must begin with http://, https:// or www.");
			return false;
		}
		
		if (value != null && value.equals(lastUrlValidated) && methodId.equals(lastMethodId)) {
			if (asyncMessages != null && asyncMessages.size() > 0) {
				markInvalid(asyncMessages);
				return false;
			}
		}
		
		clearInvalid();
		
		asynchValidation();
		
		return true;
	}
	
	public boolean validateFormat(String value) {
		
		if (value == null || value.trim().length() == 0) {
			return true;
		}
		
		if (value.startsWith("http://")) {
			return true;
		}
		
		if (value.startsWith("https://")) {
			return true;
		}
		
		if (value.startsWith("www.")) {
			return true;
		}
		
		return false;
	}
	
	@Override
	public void clearInvalid() {
		super.clearInvalid();
		asyncMessages = null;
	}
	
	public void asynchValidation() {
		
//		markInvalid("<em>Validating...</em>");
		asynchValidation(getValue());
	}
	
	public void asynchValidation(String url) {
		if (url == null)
			return;
		if (url.equals(lastUrlValidated) && methodId.equals(lastMethodId))
			return;
		
		lastUrlValidated = url;
		lastMethodId.setFrom(methodId);

		validationService.validateUrl(url, methodId, ++validationCounter,
				new AsyncCallback<AsyncValidationResponse>() {
					public void onFailure(Throwable caught) {
						// Show the RPC error message to the user
						if (caught instanceof IllegalArgumentException)
							MessageBox.alert("Alert", caught.getMessage(), null);
						else {
							MessageBox.alert("Alert", "URL validation failed unexpectedly.", null);
							System.out.println(caught.getClass().getName());
							System.out.println(caught.getMessage());
						}
					}

					public void onSuccess(AsyncValidationResponse response) {
						//	Mark invalid if an error occurred, and if the response matches the current field validation count setting
						if (response.getValidationCounter() == validationCounter) {
							if (response.getMessages() != null && response.getMessages().size() > 0) {						
								asyncMessages = response.getMessages();
								markInvalid(asyncMessages);
							} else {
								clearInvalid();
								asyncInfoMessages = response.getInfoMessages();
								asyncAlertMessages = response.getAlertMessages();
								markInfo();
							}
						}
					}
			});
	}
	
	public MethodIdInstance getMethodId() {
		return methodId;
	}

	public void setMethodId(MethodIdInstance methodId) {
		this.methodId = methodId;
	}

	public void markInvalid(List<String> messages) {
		for (String message: messages)
			markInvalid(message);
	}
	
	public void resetAsynchValidation() {
		lastUrlValidated = "";
		clearInfo();
	}
	
	
	/**
	 * 
	 * The below methods have all been added to incorporate the capability to have "info" and "alert" messages display if no error messages are found.
	 * 
	 * The code is basically a duplication of GXT's error icon code, but using different style attributes.
	 * 
	 * This code should in theory be incorporated in a higher level Field class, but of course there's no way to "insert" that into the inheritance hierarchy,
	 * which in turn would require building all new higher level classes (TextFieldWithInfo, MultiFieldWithInfo, etc.).
	 * 
	 */

	
	@Override
	public void reset() {
		super.reset();
		resetAsynchValidation();
		clearInfo();
	}
	
	public void markInfo() {
		if (asyncInfoMessages == null || asyncInfoMessages.size() == 0)
			if (asyncAlertMessages == null || asyncAlertMessages.size() == 0) {
				clearInfo();
				return;
			}
		markInfo(asyncInfoMessages, asyncAlertMessages);
	}
	
	public void markInfo(List<String> infoMsgs, List<String> alertMsgs) {
		if (infoMsgs == null || infoMsgs.size() == 0)
			if (alertMsgs == null || alertMsgs.size() == 0) {
				clearInfo();
				return;
			}
		
		boolean alert = alertMsgs != null && alertMsgs.size() > 0;
		StringBuffer msgs = new StringBuffer();
		if (alertMsgs != null) {
			for (String msg : alertMsgs) {
				if (msgs.length() > 0) msgs.append("<br/>");
				msgs.append("<em>");
				msgs.append(msg);
				msgs.append("</em>");
			}
		}
		if (infoMsgs != null) {
			for (String msg : infoMsgs) {
				if (msgs.length() > 0) msgs.append("<br/>");
				msgs.append(msg);
			}
		}
		markInfo(msgs.toString(), alert);
	}
	
	@Override
	public void markInvalid(String msg) {
		super.markInvalid(msg);
		clearInfo();
//		FOR DEBUGGING TOOLTIP CSS -- makes the tooltip hang around long enough to look at
//		if (errorIcon != null) errorIcon.getToolTip().getToolTipConfig().setDismissDelay(0);
//		if (errorIcon != null) errorIcon.getToolTip().getToolTipConfig().setHideDelay(500000);
	}
	
	/**
	 * Marks this field as having info (warnings or messages). Validation will still run if called again, and
	 * the error message will be changed or cleared based on validation. To set a
	 * error message that will not be cleared until manually cleared see
	 * {@link #forceInvalid(String)}
	 * 
	 * @param msg the validation message
	 */
	public void markInfo(String msg, boolean alert) {
		if (msg == null) {
			clearInfo();
			return;
		}
//		msg = Format.htmlEncode(msg == null ? getMessages().getInvalidText() : msg);
		activeInfoMessage = msg;
		if (!rendered || preventMark) {
			return;
		}
		if (alert) {
			removeInputStyleName(infoStyle);
			addInputStyleName(alertStyle);
		} else {
			removeInputStyleName(alertStyle);
			addInputStyleName(infoStyle);
		}

		if ("side".equals(getMessageTarget())) {
			if (infoIcon == null) {
				if (alert)
					infoIcon = new WidgetComponent(IconSupplier.getColorfulIcon(IconSupplier.getAlertIconName()).createImage());	//	getImages().getInvalid().createImage());
				else
					infoIcon = new WidgetComponent(IconSupplier.getColorfulIcon(IconSupplier.getInfoIconName()).createImage());	//	getImages().getInvalid().createImage());
				Element p = el().getParent().dom;
				infoIcon.render(p);
				infoIcon.setHideMode(HideMode.VISIBILITY);
				infoIcon.hide();
				infoIcon.setStyleAttribute("display", "block");
				infoIcon.el().makePositionable(true);
				infoIcon.getAriaSupport().setRole("alert");
				if (GXT.isAriaEnabled()) {
					setAriaState("aria-describedby", infoIcon.getId());
					infoIcon.setTitle(getErrorMessage());
				}

			} else if (!infoIcon.el().isConnected()) {
				Element p = el().getParent().dom;
				p.appendChild(infoIcon.getElement());
			}
			if (!infoIcon.isAttached()) {
				ComponentHelper.doAttach(infoIcon);
			}

			alignInfoIcon();
			if (GXT.isIE || GXT.isOpera) {
				alignInfoIcon();
			}
			// needed to prevent flickering
			DeferredCommand.addCommand(new Command() {
				public void execute() {
					if (infoIcon.isAttached()) {
						infoIcon.show();
					}
				}
			});
			infoIcon.setToolTip(msg);
			infoIcon.getToolTip().getToolTipConfig().setDismissDelay(0); 	// So the user can hang out over the icon and keep the messages up
//			infoIcon.getToolTip().getToolTipConfig().setHideDelay(500000);
			if (alert) {
				infoIcon.getToolTip().removeStyleName("x-form-info-tip");
				infoIcon.getToolTip().addStyleName("x-form-info-alert-tip");
				Image image = (Image) infoIcon.getWidget();
				IconSupplier.getColorfulIcon(IconSupplier.getAlertIconName()).applyTo(image);
			} else {
				infoIcon.getToolTip().removeStyleName("x-form-alert-tip");
				infoIcon.getToolTip().addStyleName("x-form-info-tip");
				Image image = (Image) infoIcon.getWidget();
				IconSupplier.getColorfulIcon(IconSupplier.getInfoIconName()).applyTo(image);
			}
//			infoIcon.getToolTip().getToolTipConfig().setAutoHide(false);
//			infoIcon.getToolTip().getToolTipConfig().setCloseable(true);
			el().repaint();
		} else if ("title".equals(getMessageTarget())) {
			setTitle(msg);
		} else if ("tooltip".equals(getMessageTarget())) {
			setToolTip(msg);
			if (alert)
				getToolTip().addStyleName("x-form-info-alert-tip");
			else
				getToolTip().addStyleName("x-form-info-tip");
			getToolTip().enable();
		} else if ("none".equals(getMessageTarget())) {
			// do nothing
		} else {
			Element elem = XDOM.getElementById(getMessageTarget());
			if (elem != null) {
				elem.setInnerHTML(msg);
			}
		}

//		if (GXT.isAriaEnabled()) {
//			setAriaState("aria-invalid", "true");
//		}
//
//		FieldEvent fe = new FieldEvent(this);
//		fe.setMessage(msg);
//		fireEvent(Events.Invalid, fe);
	}

	/**
	 * Clear any info styles / messages for this field.
	 */
	public void clearInfo() {
		if (!rendered) {
			return;
		}
		
		removeInputStyleName(infoStyle);
		removeInputStyleName(alertStyle);

		//	    if (forceInfoText != null) {
		//	      forceInfoText = null;
		//	    }

		if ("side".equals(getMessageTarget())) {
			if (infoIcon != null && infoIcon.isAttached()) {
				ComponentHelper.doDetach(infoIcon);
				infoIcon.setVisible(false);
				setAriaState("aria-describedby", "");
			}
		} else if ("title".equals(getMessageTarget())) {
			setTitle("");
		} else if ("tooltip".equals(getMessageTarget())) {
			hideToolTip();
			if (toolTip != null) {
				toolTip.disable();
			}
		} else {
			Element elem = XDOM.getElementById(getMessageTarget());
			if (elem != null) {
				elem.setInnerHTML("");
			}
		}
//		if (GXT.isAriaEnabled()) {
//			getAriaSupport().setState("aria-invalid", "false");
//		}
//		fireEvent(Events.Valid, new FieldEvent(this));
	}

	protected void alignInfoIcon() {
		DeferredCommand.addCommand(new Command() {
			public void execute() {
				infoIcon.el().alignTo(getElement(), "tl-tr", new int[] {2, 3});
			}
		});
	}

	@Override
	protected void doDetachChildren() {
		super.doDetachChildren();
		if (infoIcon != null && infoIcon.isAttached()) {
			infoIcon.setVisible(false);
			ComponentHelper.doDetach(infoIcon);
		}
	}

	@Override
	protected void onHide() {
		super.onHide();
		if (infoIcon != null && infoIcon.isAttached()) {
			infoIcon.hide();
		}
	}

	@Override
	protected void onResize(int width, int height) {
		super.onResize(width, height);
		if (infoIcon != null && infoIcon.isAttached()) {
			alignInfoIcon();
		}
	}

	@Override
	protected void onShow() {
		super.onShow();
		if (infoIcon != null && infoIcon.isAttached()) {
			infoIcon.show();
		}
	}
}
