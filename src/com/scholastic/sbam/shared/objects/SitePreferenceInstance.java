package com.scholastic.sbam.shared.objects;

import java.util.Date;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.BeanModelFactory;
import com.extjs.gxt.ui.client.data.BeanModelLookup;
import com.extjs.gxt.ui.client.data.BeanModelTag;
import com.google.gwt.user.client.rpc.IsSerializable;
import com.scholastic.sbam.shared.util.AppConstants;

public class SitePreferenceInstance extends BetterRowEditInstance implements BeanModelTag, IsSerializable {

	private static BeanModelFactory beanModelfactory;

	private int		ucn;
	private int		ucnSuffix;
	private String	siteLocCode;
	
	private String	prefCatCode;
	private String	prefSelCode;
	
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

	public int getUcnSuffix() {
		return ucnSuffix;
	}

	public void setUcnSuffix(int ucnSuffix) {
		this.ucnSuffix = ucnSuffix;
	}

	public String getSiteLocCode() {
		return siteLocCode;
	}

	public void setSiteLocCode(String siteLocCode) {
		this.siteLocCode = siteLocCode;
	}

	public String getPrefCatCode() {
		return prefCatCode;
	}

	public void setPrefCatCode(String prefCatCode) {
		this.prefCatCode = prefCatCode;
	}

	public String getPrefSelCode() {
		return prefSelCode;
	}

	public void setPrefSelCode(String prefSelCode) {
		this.prefSelCode = prefSelCode;
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
	
	public static SitePreferenceInstance getEmptyInstance() {
		SitePreferenceInstance instance = new SitePreferenceInstance();
		instance.ucn = 0;
		instance.ucnSuffix = 0;
		instance.siteLocCode = "";
		instance.prefCatCode = "";
		instance.prefSelCode = "";
		return instance;
	}
	
	public static SitePreferenceInstance getUnknownInstance(int ucn, int ucnSuffix, String siteLocCode, String prefCatCode, String prefSelCode) {
		SitePreferenceInstance instance = new SitePreferenceInstance();
		instance.ucn = ucn;
		instance.ucnSuffix = ucnSuffix;
		instance.siteLocCode = siteLocCode;
		instance.prefCatCode = prefCatCode;
		instance.prefSelCode = prefSelCode;
		return instance;
	}

	public static BeanModel obtainModel(SitePreferenceInstance instance) {
		if (beanModelfactory == null)
			beanModelfactory  = BeanModelLookup.get().getFactory(SitePreferenceInstance.class);
		BeanModel model = beanModelfactory.createModel(instance);
		return model;
	}

	public String toString() {
		return "SitePreference " + ucn + "-" + ucnSuffix + " : " + siteLocCode + "-" + prefCatCode + "-" + prefSelCode;
	}
}
