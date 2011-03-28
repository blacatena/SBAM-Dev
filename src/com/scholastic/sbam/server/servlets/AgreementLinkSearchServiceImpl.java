package com.scholastic.sbam.server.servlets;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.scholastic.sbam.client.services.AgreementLinkSearchService;
import com.scholastic.sbam.server.database.codegen.AgreementLink;
import com.scholastic.sbam.server.database.codegen.Institution;
import com.scholastic.sbam.server.database.objects.DbAgreementLink;
import com.scholastic.sbam.server.database.objects.DbInstitution;
import com.scholastic.sbam.server.database.util.HibernateUtil;
import com.scholastic.sbam.shared.exceptions.ServiceNotReadyException;
import com.scholastic.sbam.shared.objects.AgreementLinkInstance;
import com.scholastic.sbam.shared.objects.InstitutionInstance;
import com.scholastic.sbam.shared.objects.SynchronizedPagingLoadResult;
import com.scholastic.sbam.shared.security.SecurityManager;
import com.scholastic.sbam.shared.util.AppConstants;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class AgreementLinkSearchServiceImpl extends AuthenticatedServiceServlet implements AgreementLinkSearchService {

	@Override
	public SynchronizedPagingLoadResult<AgreementLinkInstance> searchAgreementLinks(PagingLoadConfig loadConfig, String filter, long syncId) throws IllegalArgumentException, ServiceNotReadyException {
		
		authenticate("search agreement links", SecurityManager.ROLE_QUERY);

		// This is just a BasePagingLoadResult,but it has a synchronization tag to make sure old, slow, late search results don't overwrite newer, better results
		SynchronizedPagingLoadResult<AgreementLinkInstance> result = null;
	
		try {
			List<AgreementLinkInstance> list = new ArrayList<AgreementLinkInstance>();
				
			HibernateUtil.openSession();
			HibernateUtil.startTransaction();
			
			String [] filters = AppConstants.parseFilterTerms(filter);

			List<Object []> dbInstances = DbAgreementLink.findFiltered(filter, AppConstants.STATUS_ACTIVE, AppConstants.STATUS_ANY_NONE);	//, loadConfig.getSortField(), loadConfig.getSortDir());
			
			int i = 0;
			int totSize = 0;

			for (Object [] dbInstanceParts : dbInstances) {
				AgreementLink agreementLink = (AgreementLink) dbInstanceParts [0];
				Institution   institution    = (Institution) dbInstanceParts [1];
				if (qualifyAgreementLink(agreementLink, institution, filters)) {	// This test is redundant, and could cause problems if it is out of sync with the SQL
																					// But it also gives the future option of a performance boost by simply doing a findAll
																					// with the database, and simple tests here, since there are so few links in the system
					totSize++;
					//	Paging... start from where asked, and don't return more than requested
					if (i >= loadConfig.getOffset() && (list.size() < loadConfig.getLimit() || loadConfig.getLimit() <= 0)) {
						AgreementLinkInstance alInstance	= DbAgreementLink.getInstance(agreementLink);
						InstitutionInstance   instInstance	= DbInstitution.getInstance(institution);
						alInstance.setInstitution(instInstance);
						DbAgreementLink.setDescriptions(alInstance);
						list.add(alInstance);
					}
					i++;
				}
			}
			
			result = new SynchronizedPagingLoadResult<AgreementLinkInstance>(list, loadConfig.getOffset(), totSize, syncId);
			
			HibernateUtil.endTransaction();
			HibernateUtil.closeSession();
			

		} catch (Exception exc) {
			exc.printStackTrace();
			throw new IllegalArgumentException("Unexpected failure on agreement link search: " + exc.getMessage());
		} finally {
			
		}
		
		return result;
	}
	
	/**
	 * Apply all filters to be sure this address qualifies.
	 * @param dbInstance
	 * @param filters
	 * @return
	 */
	private boolean qualifyAgreementLink(AgreementLink agreementLink, Institution institution, String [] filters) {
		for (int i = 0; i < filters.length; i++) {
			String filter = filters [i].trim().toUpperCase();
			if (filter.length() == 0)
				continue;
			
			
			if ( (agreementLink.getLinkIdCheckDigit() + "").indexOf(filter) < 0
			&&  institution.getInstitutionName().toUpperCase().indexOf(filter) < 0
			&&  institution.getAddress1().toUpperCase().indexOf(filter) < 0
			&&  institution.getCity().toUpperCase().indexOf(filter) < 0
			&&  institution.getState().toUpperCase().indexOf(filter) < 0
			&&  institution.getZip().toUpperCase().indexOf(filter) < 0
			&&  institution.getAddress2().toUpperCase().indexOf(filter) < 0
			&&  institution.getAddress3().toUpperCase().indexOf(filter) < 0
			&&  institution.getCountry().toUpperCase().indexOf(filter) < 0
			&&  (institution.getPhone() == null || institution.getPhone().toUpperCase().indexOf(filter) < 0)
			&&  (institution.getFax() == null || institution.getFax().toUpperCase().indexOf(filter) < 0)
			&&  (institution.getUcn() + "").indexOf(filter) < 0
			&&  institution.getAlternateIds().toUpperCase().indexOf(filter) < 0)
				return false;
		}
		return true;
	}
}
