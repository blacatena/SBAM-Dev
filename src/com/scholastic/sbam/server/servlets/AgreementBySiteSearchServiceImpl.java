package com.scholastic.sbam.server.servlets;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.data.LoadConfig;
import com.scholastic.sbam.client.services.AgreementBySiteSearchService;
import com.scholastic.sbam.server.database.codegen.Agreement;
import com.scholastic.sbam.server.database.codegen.AgreementSite;
import com.scholastic.sbam.server.database.codegen.Institution;
import com.scholastic.sbam.server.database.objects.DbAgreement;
import com.scholastic.sbam.server.database.objects.DbAgreementSite;
import com.scholastic.sbam.server.database.objects.DbInstitution;
import com.scholastic.sbam.server.database.util.HibernateUtil;
import com.scholastic.sbam.server.fastSearch.InstitutionCache;
import com.scholastic.sbam.server.fastSearch.InstitutionCache.InstitutionCacheConflict;
import com.scholastic.sbam.server.fastSearch.SiteInstitutionCache;
import com.scholastic.sbam.shared.exceptions.ServiceNotReadyException;
import com.scholastic.sbam.shared.objects.AgreementInstance;
import com.scholastic.sbam.shared.objects.AgreementSiteInstance;
import com.scholastic.sbam.shared.objects.AgreementSiteTuple;
import com.scholastic.sbam.shared.objects.InstitutionInstance;
import com.scholastic.sbam.shared.objects.SynchronizedPagingLoadResult;
import com.scholastic.sbam.shared.security.SecurityManager;
import com.scholastic.sbam.shared.util.AppConstants;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class AgreementBySiteSearchServiceImpl extends AuthenticatedServiceServlet implements AgreementBySiteSearchService {

	@Override
	public SynchronizedPagingLoadResult<AgreementSiteTuple> searchAgreementsBySite(LoadConfig config, long syncId) throws IllegalArgumentException {
	
		authenticate("search agreementSites", SecurityManager.ROLE_QUERY);
		
		List<AgreementSiteTuple> list = new ArrayList<AgreementSiteTuple>();

		String filter	= config.get("filter")   != null ? config.get("filter").toString() : null;

		if (filter				== null)
			return new SynchronizedPagingLoadResult<AgreementSiteTuple>(list, 0, 0, syncId); 
		
		
		HibernateUtil.openSession();
		HibernateUtil.startTransaction();
		
		try {

			if (SiteInstitutionCache.getSingleton() == null || !SiteInstitutionCache.getSingleton().isMapsReady())
				throw new ServiceNotReadyException("institution search function");
			SiteInstitutionCache searchCache =  SiteInstitutionCache.getSingleton();
		
			//	Filtering... only get the UCNs for all of the terms listed, using the cache
			String filters [] = searchCache.parseFilter(filter);
			List<Integer> ucns = searchCache.getFilteredUcns(filter);
			
			if (ucns == null || ucns.size() == 0) {
				// Nothing qualified, so look in the counts to see if there were too many
				int bestCount = searchCache.getFilteredUcnCount(filters);
				
				//	This is an "error" result with no data, but the count of how many sites might have qualified
				return new SynchronizedPagingLoadResult<AgreementSiteTuple>(list, 0, bestCount, syncId);
			}
			
			List<Object []> tuples = DbAgreementSite.findByUcn(ucns, AppConstants.STATUS_DELETED);
			
			int loadLimit = AppConstants.STANDARD_LOAD_LIMIT;
			if (config.get("limit") != null && config.get("limit") instanceof Integer)
				loadLimit = ( (Integer) config.get("limit"));
			if (tuples.size() > loadLimit) {
				//	This is an error return with no data, but a count (too many results)
				return new SynchronizedPagingLoadResult<AgreementSiteTuple>(list, 0, tuples.size(), syncId);
			}
			
			for (Object [] objects : tuples) {

				AgreementInstance 		agreement			= DbAgreement    .getInstance((Agreement) objects [0]);
				AgreementSiteInstance	agreementSite		= DbAgreementSite.getInstance((AgreementSite) objects [1]);
				InstitutionInstance		siteInstitution		= DbInstitution  .getInstance((Institution) objects [2]);
				
				setDescriptions(agreementSite, siteInstitution);
				setDescriptions(agreement);
				setInstitution(agreement);
					
				AgreementSiteTuple tuple = new AgreementSiteTuple(agreement, agreementSite);
				list.add(tuple);
			}

		} catch (InstitutionCache.InstitutionCacheConflict exc) {
			throw new IllegalArgumentException("Site Institution Cache not yet ready.");
		} catch (Exception exc) {
			exc.printStackTrace();
		}
		
		HibernateUtil.endTransaction();
		HibernateUtil.closeSession();
		
		return new SynchronizedPagingLoadResult<AgreementSiteTuple>(list, 0, list.size(), syncId);
	}

	private void setDescriptions(AgreementInstance agreement) {
		DbAgreement.setDescriptions(agreement);
	}
	
	private void setInstitution(AgreementInstance agreement) throws InstitutionCacheConflict {
		// Get the institution
		if (agreement.getBillUcn() > 0) {
			Institution dbInstitution = DbInstitution.getByCode(agreement.getBillUcn());
			agreement.setInstitution(DbInstitution.getInstance(dbInstitution));

			if (agreement.getInstitution() != null) {
				SiteInstitutionCache.getSingleton().setDescriptions( agreement.getInstitution() );
			}
		}
	}

	private void setDescriptions(AgreementSiteInstance agreementSite, InstitutionInstance siteInstitution) {
		DbAgreementSite.setDescriptions(agreementSite, siteInstitution);
	}
}
