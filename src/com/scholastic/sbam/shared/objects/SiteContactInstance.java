package com.scholastic.sbam.shared.objects;

import java.util.Date;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.BeanModelFactory;
import com.extjs.gxt.ui.client.data.BeanModelLookup;
import com.extjs.gxt.ui.client.data.BeanModelTag;
import com.google.gwt.user.client.rpc.IsSerializable;
import com.scholastic.sbam.shared.util.AppConstants;

public class SiteContactInstance extends BetterRowEditInstance implements BeanModelTag, IsSerializable {

	private static BeanModelFactory beanModelfactory;

	private int		ucn;
	
	private int		contactId;
	
	private char	status;
	private boolean	active;
	private Date	createdDatetime;
	
	@Override
	public void markForDeletion() {
		setStatus('X');
	}

	@Override
	public boolean thisIsDeleted() {
		return status == AppConstants.STATUS_DELETED;
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

	public int getUcn() {
		return ucn;
	}

	public void setUcn(int ucn) {
		this.ucn = ucn;
	}

	public int getContactId() {
		return contactId;
	}

	public void setContactId(int contactId) {
		this.contactId = contactId;
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
	
	public static SiteContactInstance getEmptyInstance() {
		SiteContactInstance instance = new SiteContactInstance();
		instance.ucn = 0;
		instance.contactId = 0;
		return instance;
	}
	
	public static SiteContactInstance getUnknownInstance(int ucn, int contactId) {
		SiteContactInstance instance = new SiteContactInstance();
		instance.ucn = ucn;
		instance.contactId = contactId;
		return instance;
	}

	public static BeanModel obtainModel(SiteContactInstance instance) {
		if (beanModelfactory == null)
			beanModelfactory  = BeanModelLookup.get().getFactory(SiteContactInstance.class);
		BeanModel model = beanModelfactory.createModel(instance);
		return model;
	}

	public String toString() {
		return "SiteContact " + ucn + "-" + contactId;
	}
}
