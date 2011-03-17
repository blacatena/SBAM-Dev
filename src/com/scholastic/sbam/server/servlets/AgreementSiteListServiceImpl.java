package com.scholastic.sbam.server.servlets;

import java.util.ArrayList;
import java.util.List;

import com.scholastic.sbam.client.services.AgreementSiteListService;
import com.scholastic.sbam.server.database.codegen.AgreementSite;
import com.scholastic.sbam.server.database.codegen.CommissionType;
import com.scholastic.sbam.server.database.codegen.Institution;
import com.scholastic.sbam.server.database.codegen.Site;
import com.scholastic.sbam.server.database.objects.DbAgreementSite;
import com.scholastic.sbam.server.database.objects.DbCommissionType;
import com.scholastic.sbam.server.database.objects.DbInstitution;
import com.scholastic.sbam.server.database.objects.DbSite;
import com.scholastic.sbam.server.database.util.HibernateUtil;
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
	

	
	private void setDescriptions(AgreementSiteInstance agreementSite) {
		if (agreementSite == null)
			return;
		
		if (agreementSite.getSiteUcn() > 0) {
			Institution dbInstitution = DbInstitution.getByCode(agreementSite.getSiteUcn());
			if (dbInstitution != null)
				agreementSite.setInstitution( DbInstitution.getInstance(dbInstitution) );
			Site dbSite = DbSite.getById(agreementSite.getSiteUcn(), agreementSite.getSiteUcnSuffix(), agreementSite.getSiteLocCode());
			if (dbSite != null)
				agreementSite.setSite( DbSite.getInstance(dbSite) );
		}
	
		if (agreementSite.getCommissionCode() != null) {
			CommissionType cType = DbCommissionType.getByCode(agreementSite.getCommissionCode());
			if (cType != null)
				agreementSite.setCommissionCodeDescription(cType.getDescription());
		}
	}
}
