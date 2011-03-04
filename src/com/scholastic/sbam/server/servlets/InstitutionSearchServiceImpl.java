package com.scholastic.sbam.server.servlets;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.scholastic.sbam.client.services.InstitutionSearchService;
import com.scholastic.sbam.server.database.codegen.Institution;
import com.scholastic.sbam.server.database.objects.DbInstitution;
import com.scholastic.sbam.server.database.util.HibernateUtil;
import com.scholastic.sbam.server.fastSearch.InstitutionCache;
import com.scholastic.sbam.shared.exceptions.ServiceNotReadyException;
import com.scholastic.sbam.shared.objects.InstitutionInstance;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class InstitutionSearchServiceImpl extends AuthenticatedServiceServlet implements InstitutionSearchService {

	@Override
	public PagingLoadResult<InstitutionInstance> getInstitutions(PagingLoadConfig loadConfig, String filter) throws IllegalArgumentException, ServiceNotReadyException {
		
		authenticate("search institutions");	//	SecurityManager.ROLE_CONFIG);

		BasePagingLoadResult<InstitutionInstance> result = null;
		try {
			List<InstitutionInstance> list = new ArrayList<InstitutionInstance>();
			
			if (InstitutionCache.getSingleton() == null || !InstitutionCache.getSingleton().isMapsReady())
				throw new ServiceNotReadyException("institution search function");
			
			//	Filtering... only get the UCNs for all of the terms listed, using the cache
			String filters [] = InstitutionCache.getSingleton().parseFilter(filter);
			List<Integer> ucns = InstitutionCache.getSingleton().getFilteredUcns(filter);
			
			if (ucns == null || ucns.size() == 0) {
				// Nothing qualified, so look in the counts to see if there were too many
				int bestCount = InstitutionCache.getSingleton().getFilteredUcnCount(filters);
				
				//	This is an "error" result with no data, but the count of how many might have qualified
				result = new BasePagingLoadResult<InstitutionInstance>(list, loadConfig.getOffset(), bestCount);
			} else {
				
				HibernateUtil.openSession();
				HibernateUtil.startTransaction();
				
				List<Institution> dbInstances = DbInstitution.findFiltered(ucns, (char) 0, 'X', loadConfig.getSortField(), loadConfig.getSortDir());
				
				int i = 0;
				int totSize = 0;

				for (Institution dbInstance : dbInstances) {
					if (qualifyInstitution(dbInstance, filters)) {
						totSize++;
						//	Paging... start from where asked, and don't return more than requested
						if (i >= loadConfig.getOffset() && list.size() < loadConfig.getLimit()) {
							InstitutionInstance instance = DbInstitution.getInstance(dbInstance);
							instance.setTypeDescription(InstitutionCache.getSingleton().getInstitutionType(instance.getTypeCode()).getDescription());
							instance.setGroupDescription(InstitutionCache.getSingleton().getInstitutionGroup(instance.getGroupCode()).getDescription());
							list.add(instance);
						}
						i++;
					}
				}
				result = new BasePagingLoadResult<InstitutionInstance>(list, loadConfig.getOffset(), totSize);
				
				HibernateUtil.endTransaction();
				HibernateUtil.closeSession();
			}
			

		} catch (InstitutionCache.InstitutionCacheNotReady exc) {
			throw new ServiceNotReadyException("institution search function");
		} catch (ServiceNotReadyException exc) {
			throw exc;
		} catch (Exception exc) {
			exc.printStackTrace();
		}
		
		return result;
	}
	
	/**
	 * Apply all filters to be sure this address qualifies.
	 * @param dbInstance
	 * @param filters
	 * @return
	 */
	private boolean qualifyInstitution(Institution dbInstance, String [] filters) {
		for (int i = 0; i < filters.length; i++) {
			String filter = filters [i].trim().toUpperCase();
			if (filter.length() == 0)
				continue;
			if (dbInstance.getInstitutionName().toUpperCase().indexOf(filter) < 0
			&&  dbInstance.getAddress1().toUpperCase().indexOf(filter) < 0
			&&  dbInstance.getCity().toUpperCase().indexOf(filter) < 0
			&&  dbInstance.getState().toUpperCase().indexOf(filter) < 0
			&&  dbInstance.getZip().toUpperCase().indexOf(filter) < 0
			&&  dbInstance.getAddress2().toUpperCase().indexOf(filter) < 0
			&&  dbInstance.getAddress3().toUpperCase().indexOf(filter) < 0
			&&  dbInstance.getCountry().toUpperCase().indexOf(filter) < 0
			&&  dbInstance.getPhone().toUpperCase().indexOf(filter) < 0
			&&  dbInstance.getFax().toUpperCase().indexOf(filter) < 0)
				return false;
		}
		return true;
	}
}
