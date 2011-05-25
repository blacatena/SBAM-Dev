package com.scholastic.sbam.client.uiobjects.fields;

import java.util.List;

import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.Style.HideMode;
import com.extjs.gxt.ui.client.core.XDOM;
import com.extjs.gxt.ui.client.widget.ComponentHelper;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.WidgetComponent;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.MultiField;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Image;
import com.scholastic.sbam.client.services.IpRangeValidationService;
import com.scholastic.sbam.client.services.IpRangeValidationServiceAsync;
import com.scholastic.sbam.client.util.IconSupplier;
import com.scholastic.sbam.shared.objects.IpAddressInstance;
import com.scholastic.sbam.shared.objects.MethodIdInstance;
import com.scholastic.sbam.shared.validation.AsyncValidationResponse;

@SuppressWarnings("deprecation")
public class IpAddressRangeField extends MultiField<Long []> {
	public final String	LOW_IP_LABEL	= "";	//"From:&nbsp;";
	public final String HIGH_IP_LABEL	= "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&hArr;&nbsp;&nbsp;&nbsp;&nbsp;";	//"&nbsp;&nbsp;To:&nbsp;";
	
	protected LabelField		loIpLabelField;
	protected IpAddressField	loIpField;
	protected LabelField		hiIpLabelField;
	protected IpAddressField	hiIpField;
	
	protected int				validationCounter	= 0;
	protected long				lastLoIpValidated	= 0L;
	protected long				lastHiIpValidated	= 0L;
	protected MethodIdInstance	lastMethodId		= MethodIdInstance.getEmptyInstance();
	protected List<String>		asyncMessages		= null;
	protected List<String>		asyncAlertMessages	= null;
	protected List<String>		asyncInfoMessages	= null;
	protected MethodIdInstance	methodId			= MethodIdInstance.getEmptyInstance();
	

	protected WidgetComponent	infoIcon;
	protected String			infoStyle	= "x-form-info";
	protected String			alertStyle	= "x-form-info-alert";
	protected String			activeInfoMessage;
	
	protected IpRangeValidationServiceAsync validationService = GWT.create(IpRangeValidationService.class);
	
	public IpAddressRangeField() {
		this("");
	}
	
	public IpAddressRangeField(String label) {
		super(label);
		createFields();
	}
	
	protected void createFields() {
		loIpField = new IpAddressField();
		hiIpField = new IpAddressField();
		loIpLabelField = new ConstantLabelField();
		hiIpLabelField = new ConstantLabelField();
		loIpLabelField.setValue(LOW_IP_LABEL);
		hiIpLabelField.setValue(HIGH_IP_LABEL);
		
		hiIpField.setHighIp(true);
		hiIpField.setAllowWildcards(false);
		hiIpField.setTiedIpField(loIpField);
		
		add(loIpLabelField);
		add(loIpField);
		add(hiIpLabelField);
		add(hiIpField);
	}
	
	protected void setLowIpLabel(String label) {
		loIpLabelField.setValue(label);
	}
	
	protected void setHighIpLabel(String label) {
		hiIpLabelField.setValue(label);
	}
	
	protected void setOctetWidths(int width) {
		loIpField.setOctetWidths(width);
		hiIpField.setOctetWidths(width);
	}
	
	protected void setSeparatorWidths(int width) {
		loIpField.setSeparatorWidths(width);
		hiIpField.setSeparatorWidths(width);
	}
	
	protected void setSeparatorValues(String value) {
		loIpField.setSeparatorValues(value);
		hiIpField.setSeparatorValues(value);
	}
	
	public void addStyleName(String styleName) {
		loIpField.addStyleName(styleName);
		hiIpField.addStyleName(styleName);
	}
	
	@Override
	public Long [] getValue() {
		long hiIpValue = hiIpField.getValue();
		if (hiIpValue == 0)
			hiIpValue = loIpField.getValue();
		return new Long [] {loIpField.getValue(), hiIpValue};
	}
	
	@Override
	public void setValue(Long [] values) {
		resetAsynchValidation();
		super.setValue(values);
		if (values [0] == 0 && values [1] == 0) {
			loIpField.setValue(values [0], new String [] {"", "", "", ""});
			hiIpField.setValue(values [1], new String [] {"", "", "", ""});			
		} else {
			String [] [] octets = getIpOctetStrings(values [0], values [1]);
			loIpField.setValue(values [0], octets [0]);
			hiIpField.setValue(values [1], octets [1]);
		}
	}
	
	public void setValue(Long loValue, Long hiValue) {
		setValue(new Long [] {loValue, hiValue});
	}
	
	@Override
	public void setOriginalValue(Long [] values) {
		super.setOriginalValue(values);
		if (values [0] == 0 && values [1] == 0) {
			loIpField.setOriginalValue(values [0], new String [] {"", "", "", ""});
			hiIpField.setOriginalValue(values [1], new String [] {"", "", "", ""});			
		} else {
			String [] [] octets = getIpOctetStrings(values [0], values [1]);
			loIpField.setOriginalValue(values [0], octets [0]);
			hiIpField.setOriginalValue(values [1], octets [1]);
		}
	}
	
	public void setOriginalValue(Long loValue, Long hiValue) {
		setOriginalValue(new Long [] {loValue, hiValue});
	}

	public long getLowValue() {
		// return loIpField.getValue();
		return IpAddressInstance.getIpRange(getLowValues(), getHighValues()) [0];	// We have to do this to translate the octets and account for wildcards, blanks, etc.
	}

	public long getHighValue() {
		//	return hiIpField.getValue();
		return IpAddressInstance.getIpRange(getLowValues(), getHighValues()) [1];	// We have to do this to translate the octets and account for wildcards, blanks, etc.
	}

	public String [] getLowValues() {
		return loIpField.getValues();
	}

	public String [] getHighValues() {
		return hiIpField.getValues();
	}
	
	@Override
	public void clear() {
		loIpField.clear();
		hiIpField.clear();
	}
	
	@Override
	public void setReadOnly(boolean readOnly) {
		super.setReadOnly(readOnly);
	}
	
	@Override
	public void disable() {
		super.disable();
		loIpField.disable();
		hiIpField.disable();
	}
	
	@Override
	public void enable() {
		super.enable();
		loIpField.enable();
		hiIpField.enable();
	}
	
	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		loIpField.setEnabled(enabled);
		hiIpField.setEnabled(enabled);
	}
	
	@Override
	protected boolean validateValue(String value) {
//		boolean isValid = super.validateValue(value);
//		if (!isValid)
//			return false;
		
//		if (loIpField.isBlank()) {
//			markInvalid("An IP address is required.");
//			return false;
//		}
		
		if (loIpField.isWildcarded() && !hiIpField.isBlank()) {
			markInvalid("First IP wildcarded.  Do not specify a range.");
			return false;
		}
		
		if (!loIpField.isBlank() && !hiIpField.isBlank()) {
			if (loIpField.getValue().compareTo(hiIpField.getValue()) > 0) {
				markInvalid("First IP cannot be greater than second IP in a range.");
				return false;
			}
		}
		
		if (getLowValue() == lastLoIpValidated && getHighValue() == lastHiIpValidated && methodId.equals(lastMethodId)) {
			if (asyncMessages != null && asyncMessages.size() > 0) {
				markInvalid(asyncMessages);
				return false;
			}
		}
		
		clearInvalid();
		
		asynchValidation();
		
		return true;
	}
	
	@Override
	public void clearInvalid() {
		super.clearInvalid();
		asyncMessages = null;
	}
	
	public void asynchValidation() {
		long loIp = getLowValue();
		long hiIp = getHighValue();
		if (hiIp == 0)
			hiIp = loIp;
		if (loIp == 0)
			return;
		
//		markInvalid("<em>Validating...</em>");
		asynchValidation(loIp, hiIp);
	}
	
	public void asynchValidation(long loIp, long hiIp) {
		if (loIp == lastLoIpValidated && hiIp == lastHiIpValidated && methodId.equals(lastMethodId))
			return;
		
		lastLoIpValidated = loIp;
		lastHiIpValidated = hiIp;
		lastMethodId.setFrom(methodId);

		validationService.validateIpRange(loIp, hiIp, methodId, ++validationCounter,
				new AsyncCallback<AsyncValidationResponse>() {
					public void onFailure(Throwable caught) {
						// Show the RPC error message to the user
						if (caught instanceof IllegalArgumentException)
							MessageBox.alert("Alert", caught.getMessage(), null);
						else {
							MessageBox.alert("Alert", "IP range validation failed unexpectedly.", null);
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
		lastLoIpValidated = 0;
		lastHiIpValidated = 0;
		clearInfo();
	}
	
//	@Override
//	public boolean isValid(boolean preventMark) {
//		boolean loIsValid = loIpField.isValid(preventMark);
//		if (loIsValid && loIpField.isWildcarded())
//			return hiIpField.invalidateNonBlank(preventMark, "Must be blank if low IP is wildcarded.");
//		return loIsValid && hiIpField.isValid(preventMark);
//	}
	
	public static String [] [] getIpOctetStrings(long ipLo, long ipHi) {
		return IpAddressInstance.getIpOctetStrings(ipLo, ipHi);
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
			loIpField.removeInputStyleName(infoStyle);
			loIpField.addInputStyleName(alertStyle);
			hiIpField.removeInputStyleName(infoStyle);
			loIpField.addInputStyleName(alertStyle);
		} else {
			loIpField.removeInputStyleName(alertStyle);
			loIpField.addInputStyleName(infoStyle);
			hiIpField.removeInputStyleName(alertStyle);
			hiIpField.addInputStyleName(infoStyle);
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
			//	FOR DEBUGGING TOOLTIP CSS -- makes the tooltip hang around long enough to look at
//			infoIcon.getToolTip().getToolTipConfig().setDismissDelay(0); System.out.println("set delay 0");
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
		
		loIpField.removeInputStyleName(infoStyle);
		loIpField.removeInputStyleName(alertStyle);
		hiIpField.removeInputStyleName(infoStyle);
		hiIpField.removeInputStyleName(alertStyle);

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
