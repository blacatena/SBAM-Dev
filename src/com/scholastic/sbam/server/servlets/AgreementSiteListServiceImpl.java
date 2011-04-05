package com.scholastic.sbam.server.servlets;

import java.util.ArrayList;
import java.util.List;

import com.scholastic.sbam.client.services.AgreementSiteListService;
import com.scholastic.sbam.server.database.codegen.AgreementSite;
import com.scholastic.sbam.server.database.objects.DbAgreementSite;
import com.scholastic.sbam.server.database.util.HibernateUtil;
import com.scholastic.sbam.server.fastSearch.InstitutionCache;
import com.scholastic.sbam.server.fastSearch.InstitutionCache.InstitutionCacheConflict;
import com.scholastic.sbam.shared.objects.AgreementSiteInstance;
import com.scholastic.sbam.shared.security.SecurityManager;
import com.scholastic.sbam.shared.util.AppConstants;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class AgreementSiteListServiceImpl extends AuthenticatedServiceServlet implements AgreementSiteListService {

	@Override
	public List<AgreementSiteInstance> getAgreementSites(int agreementId, char neStatus) throws IllegalArgumentException {
		
		authenticate("get agreement sites", SecurityManager.ROLE_QUERY);
		
		HibernateUtil.openSession();
		HibernateUtil.startTransaction();

		List<AgreementSiteInstance> list = new ArrayList<AgreementSiteInstance>();
		try {
			//	Find only undeleted site types
			List<AgreementSite> siteInstances = DbAgreementSite.findByAgreementId(agreementId, AppConstants.STATUS_ANY_NONE, neStatus);
			
			for (AgreementSite siteInstance : siteInstances) {
				list.add(DbAgreementSite.getInstance(siteInstance));
			}
			
			for (AgreementSiteInstance site : list) {
				setDescriptions(site);
			}

		} catch (Exception exc) {
			exc.printStackTrace();
		}
		
		HibernateUtil.endTransaction();
		HibernateUtil.closeSession();
		
		return list;
	}
	

	
	private void setDescriptions(AgreementSiteInstance agreementSite) throws InstitutionCacheConflict {
		if (agreementSite == null)
			return;
		
		DbAgreementSite.setDescriptions(agreementSite);
		if (agreementSite.getSite() != null)
			InstitutionCache.getSingleton().setDescriptions( agreementSite.getSite().getInstitution() );
	}
}
