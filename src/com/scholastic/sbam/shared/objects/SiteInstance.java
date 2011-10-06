package com.scholastic.sbam.shared.objects;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.BeanModelFactory;
import com.extjs.gxt.ui.client.data.BeanModelLookup;
import com.extjs.gxt.ui.client.data.BeanModelTag;
import com.google.gwt.user.client.rpc.IsSerializable;
import com.scholastic.sbam.shared.util.AppConstants;

public class SiteInstance extends BetterRowEditInstance implements BeanModelTag, IsSerializable, UserCacheTarget {
	
	public static final int SITE_LOCATION_KEY_SET	= 0;
	public static final int INSTITUTION_KEY_SET    = 1;

	private static BeanModelFactory beanModelfactory;

	private int		ucn;
	private int		ucnSuffix;
	
	private String	siteLocCode;
	private String	description;
	
	private String	commissionCode;
	
	private char	pseudoSite;
	
	private String	note;
	
	private char	status;
	private boolean	active;
	private Date	createdDatetime;
	
	private InstitutionInstance		institution;
	private CommissionTypeInstance	commissionType;
	
	private HashMap<String, String> selectedPreferences;
	private List<PreferenceCategoryInstance> allPreferenceCategories;
	
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCommissionCode() {
		return commissionCode;
	}

	public void setCommissionCode(String commissionCode) {
		this.commissionCode = commissionCode;
	}

	public char getPseudoSite() {
		return pseudoSite;
	}

	public void setPseudoSite(char pseudoSite) {
		this.pseudoSite = pseudoSite;
	}

	public boolean isAPseudoSite() {
		return pseudoSite == 'y';
	}

	public void setAPseudoSite(boolean pseudoSite) {
		this.pseudoSite = pseudoSite ? 'y' : 'n';
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

	public InstitutionInstance getInstitution() {
		return institution;
	}

	public void setInstitution(InstitutionInstance institution) {
		this.institution = institution;
	}

	public CommissionTypeInstance getCommissionType() {
		return commissionType;
	}

	public void setCommissionType(CommissionTypeInstance commissionType) {
		this.commissionType = commissionType;
		if (this.commissionType == null)
			this.commissionCode = "";
		else
			this.commissionCode = commissionType.getCommissionCode();
	}
	
	public HashMap<String, String> getSelectedPreferences() {
		return selectedPreferences;
	}

	public void setSelectedPreferences(HashMap<String, String> selectedPreferences) {
		this.selectedPreferences = selectedPreferences;
	}

	public List<PreferenceCategoryInstance> getAllPreferenceCategories() {
		return allPreferenceCategories;
	}

	public void setAllPreferenceCategories(
			List<PreferenceCategoryInstance> allPreferenceCategories) {
		this.allPreferenceCategories = allPreferenceCategories;
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
	
	public String getDescriptionAndCode() {
		if (status == AppConstants.STATUS_NEW)
			return "Create a new location at this site.";
		if (status == AppConstants.STATUS_ALL)
			return "All locations at this institution.";
		
		if (siteLocCode == null || siteLocCode.length() == 0)
			return description;
		return description + " [ " + siteLocCode + " ]";
	}
	
	public static SiteInstance getEmptyInstance() {
		SiteInstance instance = new SiteInstance();
		instance.ucn = 0;
		instance.ucnSuffix = 0;
		instance.siteLocCode = "";
		instance.description = "";
		instance.institution = InstitutionInstance.getEmptyInstance();
		return instance;
	}
	
	public static SiteInstance getAllInstance(int ucn, int ucnSuffix) {
		SiteInstance instance = new SiteInstance();
		instance.ucn = ucn;
		instance.ucnSuffix = ucnSuffix;
		instance.siteLocCode = "";
		instance.description = "All Locations";
		instance.status = AppConstants.STATUS_ALL;
		instance.institution = InstitutionInstance.getEmptyInstance();
		return instance;
	}
	
	public static SiteInstance getMainInstance(int ucn, int ucnSuffix) {
		SiteInstance instance = new SiteInstance();
		instance.ucn = ucn;
		instance.ucnSuffix = ucnSuffix;
		instance.siteLocCode = "main";
		instance.description = "Main Location";
		instance.status = AppConstants.STATUS_ACTIVE;
		instance.institution = InstitutionInstance.getEmptyInstance();
		return instance;
	}
	
	public static SiteInstance getUnknownInstance(int ucn, int ucnSuffix, String siteLocCode) {
		SiteInstance instance = new SiteInstance();
		instance.ucn = ucn;
		instance.ucnSuffix = ucnSuffix;
		instance.siteLocCode = siteLocCode;
		instance.description = "Unknown site " + ucn + " - " + ucnSuffix + " - " + siteLocCode;
		return instance;
	}
	
	public static SiteInstance getDefaultNewInstance(int ucn, int ucnSuffix, String siteLocCode) {
		SiteInstance instance = new SiteInstance();
		instance.ucn = ucn;
		instance.ucnSuffix = ucnSuffix;
		instance.siteLocCode = siteLocCode;
		if ("main".equals(siteLocCode))
			instance.description = "Main Location";
		else
			instance.description = ucn + " - " + ucnSuffix + " - " + siteLocCode;
		instance.status = AppConstants.STATUS_ACTIVE;
		instance.setNewRecord(true);
		return instance;
	}

	public static BeanModel obtainModel(SiteInstance instance) {
		if (beanModelfactory == null)
			beanModelfactory  = BeanModelLookup.get().getFactory(SiteInstance.class);
		BeanModel model = beanModelfactory.createModel(instance);
		return model;
	}

	public String toString() {
		return "Site " + ucn + "-" + ucnSuffix + "-" + siteLocCode;
	}

	public static String getUserCacheCategory(int keySet) {
		if (keySet == INSTITUTION_KEY_SET)
			return "Institution";
		return "SiteLocation";
	}

	@Override
	public String userCacheCategory(int keySet) {
		return getUserCacheCategory(keySet);
	}

	@Override
	public String userCacheStringKey(int keySet) {
		if (keySet == INSTITUTION_KEY_SET)
			return null;
		return ucn + ":" + ucnSuffix + ":" + siteLocCode;
	}

	@Override
	public int userCacheIntegerKey(int keySet) {
		if (keySet == INSTITUTION_KEY_SET)
			return ucn;
		return 0;
	}
	
	@Override
	public int userCacheKeyCount() {
		return 2;
	}
}
