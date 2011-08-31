package com.scholastic.sbam.shared.objects;

import java.util.Date;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.BeanModelFactory;
import com.extjs.gxt.ui.client.data.BeanModelLookup;
import com.extjs.gxt.ui.client.data.BeanModelTag;
import com.google.gwt.user.client.rpc.IsSerializable;
import com.scholastic.sbam.shared.util.AppConstants;

public class ProxyInstance extends BetterRowEditInstance implements BeanModelTag, IsSerializable, UserCacheTarget {

	public static final int PROXY_KEY_SET	= 0;
	
	private static BeanModelFactory beanModelfactory;

	private int			proxyId;
	private String		description;
	private String		searchKeys;
	private String		note;
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

	public int getProxyId() {
		return proxyId;
	}

	public void setProxyId(int proxyId) {
		this.proxyId = proxyId;
	}
	
	public int getProxyIdCheckDigit() {
		if (proxyId <= 0)
			return 0;
		return AppConstants.appendCheckDigit(proxyId);
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getSearchKeys() {
		return searchKeys;
	}

	public void setSearchKeys(String searchKeys) {
		this.searchKeys = searchKeys;
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
	
	public boolean isAddNew() {
		return (this.status == AppConstants.STATUS_NEW);
	}
	
	public boolean isNone() {
		return (this.status == AppConstants.STATUS_NULL);
	}

	public String toString() {
		return description;
	}
	
	public String getListStyle() {
		if (isAddNew())
			return "list-new";
		if (isNone())
			return "list-null";
		return "list-normal";
	}
	
	public String getUniqueKey() {
		return proxyId + "";
	}
	
	public static ProxyInstance getEmptyInstance() {
		ProxyInstance instance = new ProxyInstance();
		instance.proxyId = 0;
		instance.description = "";
		return instance;
	}
	
	public static ProxyInstance getUnknownInstance(int proxyId) {
		ProxyInstance instance = new ProxyInstance();
		instance.proxyId = proxyId;
		instance.description = "Unknown proxy " + proxyId;
		return instance;
	}
	
	public static ProxyInstance getAddNewInstance() {
		ProxyInstance instance = new ProxyInstance();
		instance.proxyId = 0;
		instance.description = "Create a new proxy.";
		instance.status = AppConstants.STATUS_NEW;
		return instance;
	}
	
	public static ProxyInstance getNoneInstance() {
		ProxyInstance instance = new ProxyInstance();
		instance.proxyId = 0;
		instance.description = "No proxy.";
		instance.status = AppConstants.STATUS_NULL;
		return instance;
	}

	public static BeanModel obtainModel(ProxyInstance instance) {
		if (beanModelfactory == null)
			beanModelfactory  = BeanModelLookup.get().getFactory(ProxyInstance.class);
		BeanModel model = beanModelfactory.createModel(instance);
		return model;
	}
	
	public String getDescriptionAndId() {
		if (proxyId == 0)
			return description;
		return description + " [ " + getProxyIdCheckDigit() + " ]";
	}
	
	public static String getUserCacheCategory() {
		return "Proxy";
	}

	@Override
	public String userCacheCategory(int keySet) {
		return getUserCacheCategory();
	}

	@Override
	public String userCacheStringKey(int keySet) {
		return "";
	}

	@Override
	public int userCacheIntegerKey(int keySet) {
		return proxyId;
	}
	
	@Override
	public int userCacheKeyCount() {
		return 1;
	}
}
