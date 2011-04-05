package com.scholastic.sbam.shared.objects;

import java.util.Date;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.BeanModelFactory;
import com.extjs.gxt.ui.client.data.BeanModelLookup;
import com.extjs.gxt.ui.client.data.BeanModelTag;
import com.google.gwt.user.client.rpc.IsSerializable;

public class CancelReasonInstance extends BetterRowEditInstance implements BeanModelTag, IsSerializable {

	private static BeanModelFactory beanModelfactory;

	private String		cancelReasonCode;
	private String		description;
	private boolean		changeNotCancel;
	private char		status;
	private boolean		active;
	private Date		createdDatetime;
	
	@Override
	public void markForDeletion() {
		setStatus('X');
	}

	@Override
	public boolean thisIsDeleted() {
		return status == 'X';
	}

	@Override
	public boolean thisIsValid() {
		return true;
	}

	@Override
	public String returnTriggerProperty() {
		return "junk";
	}

	@Override
	public String returnTriggerValue() {
		return "junk";
	}

	public String getCancelReasonCode() {
		return cancelReasonCode;
	}

	public void setCancelReasonCode(String cancelReasonCode) {
		this.cancelReasonCode = cancelReasonCode;
	}

	public boolean isChangeNotCancel() {
		return changeNotCancel;
	}

	public void setChangeNotCancel(boolean changeNotCancel) {
		this.changeNotCancel = changeNotCancel;
	}
	
	public void setChangeNotCancel(char changeNotCancel) {
		this.changeNotCancel = changeNotCancel == 'y';
	}
	
	public char getChangeNotCancelChar() {
		return this.changeNotCancel ? 'y' : 'n';
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public char getStatus() {
		return status;
	}

	public void setStatus(char status) {
		this.status = status;
		this.active = (this.status == 'A');
	}

	public Date getCreatedDatetime() {
		return createdDatetime;
	}

	public void setCreatedDatetime(Date createdDatetime) {
		this.createdDatetime = createdDatetime;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		if (this.status == 'X')
			return;
		setStatus(active?'A':'I');
	}
	
	public static CancelReasonInstance getUnknownInstance(String code) {
		CancelReasonInstance instance = new CancelReasonInstance();
		instance.cancelReasonCode = code;
		instance.description = "Unknown cancel reason " + code;
		return instance;
	}
	
	public static CancelReasonInstance getEmptyInstance() {
		CancelReasonInstance instance = new CancelReasonInstance();
		instance.cancelReasonCode = "";
		instance.description = "";
		return instance;
	}
	
	public static CancelReasonInstance getNoneActiveInstance() {
		CancelReasonInstance instance = new CancelReasonInstance();
		instance.cancelReasonCode = "";
		instance.description = "None (Active)";
		return instance;
	}

	public static BeanModel obtainModel(CancelReasonInstance instance) {
		if (beanModelfactory == null)
			beanModelfactory  = BeanModelLookup.get().getFactory(CancelReasonInstance.class);
		BeanModel model = beanModelfactory.createModel(instance);
		return model;
	}
	
	public String getDescriptionAndCode() {
		if (cancelReasonCode == null || cancelReasonCode.length() == 0)
			return description;
		return description + " [ " + cancelReasonCode + " ]";
	}

}
