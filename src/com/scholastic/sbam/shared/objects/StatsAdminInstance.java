package com.scholastic.sbam.shared.objects;

import java.util.Date;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.BeanModelFactory;
import com.extjs.gxt.ui.client.data.BeanModelLookup;
import com.extjs.gxt.ui.client.data.BeanModelTag;
import com.google.gwt.user.client.rpc.IsSerializable;
import com.scholastic.sbam.shared.util.AppConstants;

public class StatsAdminInstance extends BetterRowEditInstance implements BeanModelTag, IsSerializable {

	private static BeanModelFactory beanModelfactory;

	protected int		ucn;
	
	protected String	adminUid;
	protected String	adminPassword;
	
	protected String	statsGroup;
	
	protected String	note;
	
	protected char		status;
	protected boolean	active;
	protected Date		createdDatetime;
	
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

	public int getUcn() {
		return ucn;
	}

	public void setUcn(int ucn) {
		this.ucn = ucn;
	}

	public String getAdminUid() {
		return adminUid;
	}

	public void setAdminUid(String adminUid) {
		this.adminUid = adminUid;
	}

	public String getAdminPassword() {
		return adminPassword;
	}

	public void setAdminPassword(String adminPassword) {
		this.adminPassword = adminPassword;
	}

	public String getStatsGroup() {
		return statsGroup;
	}

	public void setStatsGroup(String statsGroup) {
		this.statsGroup = statsGroup;
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

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getListStyle() {
		if (status == AppConstants.STATUS_NEW)
			return "list-new";
		if (status == AppConstants.STATUS_ALL)
			return "list-all";
		return "list-normal";
	}
	
	public boolean isAddNew() {
		return (status == AppConstants.STATUS_NEW);
	}
	
	public static StatsAdminInstance getEmptyInstance() {
		StatsAdminInstance instance = new StatsAdminInstance();
		instance.ucn = 0;
		return instance;
	}
	
	public static StatsAdminInstance getDefaultNewInstance(int ucn) {
		StatsAdminInstance instance = new StatsAdminInstance();
		instance.ucn = ucn;
		instance.adminUid = "";
		instance.adminPassword = "";
		instance.statsGroup = "";
		instance.status = AppConstants.STATUS_ACTIVE;
		instance.setNewRecord(true);
		return instance;
	}

	public static BeanModel obtainModel(StatsAdminInstance instance) {
		if (beanModelfactory == null)
			beanModelfactory  = BeanModelLookup.get().getFactory(StatsAdminInstance.class);
		BeanModel model = beanModelfactory.createModel(instance);
		return model;
	}

	public String toString() {
		return "Site " + ucn + "-" + adminUid + "-" + statsGroup;
	}
}
