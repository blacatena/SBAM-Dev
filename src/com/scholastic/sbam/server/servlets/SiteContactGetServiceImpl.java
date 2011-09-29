package com.scholastic.sbam.server.servlets;

import com.scholastic.sbam.client.services.SiteContactGetService;
import com.scholastic.sbam.server.database.codegen.Site;
import com.scholastic.sbam.server.database.codegen.SiteContact;
import com.scholastic.sbam.server.database.codegen.Institution;
import com.scholastic.sbam.server.database.objects.DbSite;
import com.scholastic.sbam.server.database.objects.DbSiteContact;
import com.scholastic.sbam.server.database.objects.DbInstitution;
import com.scholastic.sbam.server.database.util.HibernateUtil;
import com.scholastic.sbam.server.fastSearch.InstitutionCache;
import com.scholastic.sbam.shared.objects.SiteInstance;
import com.scholastic.sbam.shared.objects.SiteContactInstance;
import com.scholastic.sbam.shared.objects.SiteContactTuple;
import com.scholastic.sbam.shared.security.SecurityManager;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class SiteContactGetServiceImpl extends AuthenticatedServiceServlet implements SiteContactGetService {

	@Override
	public SiteContactTuple getSiteContact(int ucn, int ucnSuffix, String siteLocCode, int contactId) throws IllegalArgumentException {
		
		boolean disabled = true;
		if (disabled)
			throw new IllegalArgumentException("Do not use Site Contacts, use Institution Contacts");
		
		authenticate("get site contact", SecurityManager.ROLE_QUERY);
		
		HibernateUtil.openSession();
		HibernateUtil.startTransaction();

		SiteInstance		site = null;
		SiteContactInstance	siteContact = null;
		
		SiteContactTuple	siteContactTuple = null;
		try {
			Site		dbInstance = DbSite.getById(ucn, ucnSuffix, siteLocCode);
			SiteContact	dbSiteContactInstance = DbSiteContact.getById(ucn, contactId);
			if (dbInstance != null && dbSiteContactInstance != null && dbSiteContactInstance != null) {
				site = DbSite.getInstance(dbInstance);
				setDescriptions(site);
				siteContact = DbSiteContact.getInstance(dbSiteContactInstance);
				setDescriptions(siteContact);			
				
				// Get the institution
				if (site.getUcn() > 0) {
					Institution dbInstitution = DbInstitution.getByCode(site.getUcn());
					site.setInstitution(DbInstitution.getInstance(dbInstitution));

					if (site.getInstitution() != null) {
						InstitutionCache.getSingleton().setDescriptions( site.getInstitution() );
					}
				}
				
				siteContactTuple = new SiteContactTuple(site, siteContact);
			}

		} catch (Exception exc) {
			exc.printStackTrace();
		}
		
		HibernateUtil.endTransaction();
		HibernateUtil.closeSession();
		
		return siteContactTuple;
	}
	
	public void setDescriptions(SiteInstance site) {
		DbSite.setDescriptions(site);
	}
	
	public void setDescriptions(SiteContactInstance contact) {
//		DbSiteContact.setDescriptions(contact);
	}
}
