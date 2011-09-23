package com.scholastic.sbam.shared.objects;

import com.extjs.gxt.ui.client.data.BeanModelTag;
import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * A tuple of an site term with its parent site.
 * 
 * @author Bob Lacatena
 *
 */
public class SiteContactTuple implements BeanModelTag, IsSerializable, UserCacheTarget {
	
	SiteInstance			site;
	SiteContactInstance		siteContact;

	public SiteContactTuple() {
	}
	public SiteContactTuple(SiteInstance site, SiteContactInstance siteContact) {
		this.site			= site;
		this.siteContact	= siteContact;
	}
	public SiteInstance getSite() {
		return site;
	}
	public void setSite(SiteInstance site) {
		this.site = site;
	}
	public SiteContactInstance getSiteContact() {
		return siteContact;
	}
	public void setSiteContact(SiteContactInstance siteContact) {
		this.siteContact = siteContact;
	}
	public ContactInstance getContact() {
		return siteContact.getContact();
	}
	public String getContactNote() {
		return siteContact.getContact().getNote();
	}
	public String getSiteNote() {
		return site.getNote();
	}
	
	public static String getUserCacheCategory() {
		return getUserCacheCategory(0);
	}
	
	public static String getUserCacheCategory(int keySet) {
		return "Site";
	}

	@Override
	public String userCacheCategory(int keySet) {
		return getUserCacheCategory(keySet);
	}

	@Override
	public String userCacheStringKey(int keySet) {
		return site.getUcn() + ":" + site.getUcnSuffix() + site.getSiteLocCode() + ":" + siteContact.getContactId();
	}

	@Override
	public int userCacheIntegerKey(int keySet) {
		return 0;
	}
	
	@Override
	public int userCacheKeyCount() {
		return 0;	// All access recording is turned off!!!!
	}
	
}
