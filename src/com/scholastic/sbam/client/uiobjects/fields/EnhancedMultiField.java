package com.scholastic.sbam.client.uiobjects.fields;

import java.util.List;

import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.Style.HideMode;
import com.extjs.gxt.ui.client.core.XDOM;
import com.extjs.gxt.ui.client.widget.ComponentHelper;
import com.extjs.gxt.ui.client.widget.WidgetComponent;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.MultiField;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Image;
import com.scholastic.sbam.client.util.IconSupplier;

/**
 * Extends MultiField to add the capabilities to automatically set original values, and to disable the component fields rather than the multifield as a whole.
 * 
 * Both characteristics can be turned on or off individually.
 * 
 * @author Bob Lacatena
 *
 * @param <D>
 */
@SuppressWarnings("deprecation")
public class EnhancedMultiField<D> extends MultiField<D> {
	/**	
	 * True if component fields should be disabled individually, rather than together
	 */
	protected boolean	disableFieldsIndividually	= true;
	/**
	 * 	True if the original value should be applied to the first field as well
	 */
	protected boolean	setFirstFieldValue			= true;
	/* TODO Expand this to work with an Object [] to set values, so that a call like setOriginalValue(getValue()) will work seamlessly. */

	/**
	 * Fields added to provide info/alert support
	 */
	protected WidgetComponent	infoIcon;
	protected String			infoStyle	= "x-form-info";
	protected String			alertStyle	= "x-form-info-alert";
	protected String			activeInfoMessage;
	
	public EnhancedMultiField() {
		super();
	}
	
	public EnhancedMultiField(String label) {
		super(label);
	}
	
	public void setOriginalValues() {
		for (Field<?> field : fields) {
			@SuppressWarnings("unchecked")
			Field<Object> dField = (Field<Object>) field;
			dField.setOriginalValue(dField.getValue());
		}
	}
	
	@Override
	public void setOriginalValue(D value) {
		super.setOriginalValue(value);
		if (fields != null && fields.size() > 0) {
			@SuppressWarnings("unchecked")
			Field<Object> field = (Field<Object>) fields.get(0);
			field.setOriginalValue(value);
		}
	}
	
	@Override
	public void onDisable() {
		if (disableFieldsIndividually) {
			for (Field<?> field : fields) {
				field.disable();
			}
		} else
			super.onDisable();
	}

	public boolean isDisableFieldsIndividually() {
		return disableFieldsIndividually;
	}

	public void setDisableFieldsIndividually(boolean disableFieldsIndividually) {
		this.disableFieldsIndividually = disableFieldsIndividually;
	}

	public boolean isSetFirstFieldValue() {
		return setFirstFieldValue;
	}

	public void setSetFirstFieldValue(boolean setFirstFieldValue) {
		this.setFirstFieldValue = setFirstFieldValue;
	}

	@Override
	public boolean isDirty() {
		if (disabled || !rendered)
			return false;
		
		for (Field<?> field : fields) {
			if (field.isDirty())
				return true;
		}
		return false;
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
	
	public void addInputStyleName(String style) {
		for (Field<?> f : fields) {
			f.addInputStyleName(style);
		}
	}
	
	public void removeInputStyleName(String style) {
		for (Field<?> f : fields) {
			f.removeInputStyleName(style);
		}
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
