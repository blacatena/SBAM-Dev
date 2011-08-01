package com.scholastic.sbam.shared.objects;

import java.util.Date;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.BeanModelFactory;
import com.extjs.gxt.ui.client.data.BeanModelLookup;
import com.extjs.gxt.ui.client.data.BeanModelTag;
import com.google.gwt.user.client.rpc.IsSerializable;

public class TermTypeInstance extends BetterRowEditInstance implements BeanModelTag, IsSerializable {

	private static BeanModelFactory beanModelfactory;
	
	private String termTypeCode;
	private String description;
	private boolean   activate;
	private char   status;
	private boolean active;
	private Date   createdDatetime;
	
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

	public String getTermTypeCode() {
		return termTypeCode;
	}

	public void setTermTypeCode(String termTypeCode) {
		this.termTypeCode = termTypeCode;
	}

	public boolean isActivate() {
		return activate;
	}

	public void setActivate(boolean activate) {
		this.activate = activate;
	}
	
	public void setActivate(char activate) {
		this.activate = activate == 'y';
	}
	
	public char getActivateChar() {
		return this.activate ? 'y' : 'n';
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
	
	public String getDescriptionAndCode() {
		if (termTypeCode == null || termTypeCode.length() == 0)
			return description;
		return description + " [ " + termTypeCode + " ]";
	}
	
	public static TermTypeInstance getEmptyInstance() {
		TermTypeInstance instance = new TermTypeInstance();
		instance.termTypeCode = "";
		instance.description = "";
		return instance;
	}
	
	public static TermTypeInstance getUnknownInstance(String code) {
		TermTypeInstance instance = new TermTypeInstance();
		instance.termTypeCode = code;
		instance.description = "Unknown term type " + code;
		return instance;
	}

	public static BeanModel obtainModel(TermTypeInstance instance) {
		if (beanModelfactory == null)
			beanModelfactory  = BeanModelLookup.get().getFactory(TermTypeInstance.class);
		BeanModel model = beanModelfactory.createModel(instance);
		return model;
	}

	public String toString() {
		return description;
	}
}
