package com.scholastic.sbam.server.servlets;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.scholastic.sbam.client.services.ContactSearchService;
import com.scholastic.sbam.server.database.codegen.Contact;
import com.scholastic.sbam.server.database.codegen.Institution;
import com.scholastic.sbam.server.database.objects.DbContact;
import com.scholastic.sbam.server.database.objects.DbInstitution;
import com.scholastic.sbam.server.database.util.HibernateUtil;
import com.scholastic.sbam.server.fastSearch.InstitutionCache;
import com.scholastic.sbam.shared.exceptions.ServiceNotReadyException;
import com.scholastic.sbam.shared.objects.ContactInstance;
import com.scholastic.sbam.shared.objects.ContactSearchResultInstance;
import com.scholastic.sbam.shared.objects.InstitutionInstance;
import com.scholastic.sbam.shared.objects.SynchronizedPagingLoadResult;
import com.scholastic.sbam.shared.security.SecurityManager;
import com.scholastic.sbam.shared.util.AppConstants;
import com.scholastic.sbam.shared.util.SearchUtilities;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class ContactSearchServiceImpl extends AuthenticatedServiceServlet implements ContactSearchService {

	@Override
	public SynchronizedPagingLoadResult<ContactSearchResultInstance> searchContacts(PagingLoadConfig loadConfig, int ucn, boolean searchInstitutions, String filter, long syncId) throws IllegalArgumentException, ServiceNotReadyException {
		
		authenticate("search contacts", SecurityManager.ROLE_QUERY);
		
		if (ucn != 0)
			return searchWithinInstitution(loadConfig, ucn, searchInstitutions, filter, syncId);
		else if (searchInstitutions)
			return searchContactsAndInstitutions(loadConfig, ucn, searchInstitutions, filter, syncId);
		else
			return searchContactsOnly(loadConfig, ucn, searchInstitutions, filter, syncId);
	}

	/**
	 * Search for contacts using the filters, with no UCN given
	 * @param loadConfig
	 * @param ucn
	 * @param searchInstitutions
	 * @param filter
	 * @param syncId
	 * @return
	 */
	public SynchronizedPagingLoadResult<ContactSearchResultInstance> searchContactsOnly(PagingLoadConfig loadConfig, int ucn, boolean searchInstitutions, String filter, long syncId) {

		//	Determine search type -- loose boolean, boolean, or natural language
		boolean doLoose		= SearchUtilities.DEFAULT_LOOSE_SEARCH;
		boolean doBoolean	= SearchUtilities.DEFAULT_BOOLEAN_SEARCH; 
		if (filter.length() > 0 && filter.charAt(0) == AppConstants.LOOSE_BOOLEAN_SEARCH_FLAG) {
			filter = filter.substring(1);
			doLoose = true;
			doBoolean = true;
		} else if (filter.length() > 0 && filter.charAt(0) == AppConstants.STRICT_BOOLEAN_SEARCH_FLAG) {
			filter = filter.substring(1);
			doLoose   = false;
			doBoolean = true;
		} else if (filter.length() > 0 && filter.charAt(0) == AppConstants.QUERY_EXPANSION_SEARCH_FLAG) {
			filter = filter.substring(1);
			doLoose   = false;
			doBoolean = false;
		}
		
		//	Adjust terms if loose boolean
		if (doBoolean && doLoose)
			filter = SearchUtilities.getLooseBoolean(filter);
		
		SynchronizedPagingLoadResult<ContactSearchResultInstance> result = null;
		List<ContactSearchResultInstance> list = new ArrayList<ContactSearchResultInstance>();
		
		if (filter == null || filter.length() == 0)
			return new SynchronizedPagingLoadResult<ContactSearchResultInstance>(list, loadConfig.getOffset(), 0, syncId);

		try {
			
			HibernateUtil.openSession();
			HibernateUtil.startTransaction();
			
			List<Contact> dbInstances = DbContact.findFiltered(filter, doBoolean, (char) 0, AppConstants.STATUS_DELETED, loadConfig.getSortField(), loadConfig.getSortDir());
			
			int i = 0;
			int totSize = 0;

			for (Contact dbInstance : dbInstances) {
				if (qualifyContact(dbInstance, AppConstants.parseFilterTerms(filter))) {
					totSize++;
					//	Paging... start from where asked, and don't return more than requested
					if (i >= loadConfig.getOffset() && list.size() < loadConfig.getLimit()) {
						ContactInstance instance = DbContact.getInstance(dbInstance);
						DbContact.setDescriptions(instance);
						list.add(new ContactSearchResultInstance(instance));
					}
					i++;
				}
			}
			result = new SynchronizedPagingLoadResult<ContactSearchResultInstance>(list, loadConfig.getOffset(), totSize, syncId);
		
		} catch (Exception exc) {
			exc.printStackTrace();
		} finally {
			HibernateUtil.endTransaction();
			HibernateUtil.closeSession();	
		}
		
		if (result == null)	//	This is an "error" result with no data, but the count of how many might have qualified
			result = new SynchronizedPagingLoadResult<ContactSearchResultInstance>(list, loadConfig.getOffset(), 0, syncId);
		return result;
	}
	
	/**
	 * Search for contacts within a particular institution, with or without filters
	 * @param loadConfig
	 * @param ucn
	 * @param searchInstitutions
	 * @param filter
	 * @param syncId
	 * @return
	 */
	public 	SynchronizedPagingLoadResult<ContactSearchResultInstance> searchWithinInstitution(PagingLoadConfig loadConfig, int ucn, boolean searchInstitutions, String filter, long syncId) {
		
		SynchronizedPagingLoadResult<ContactSearchResultInstance> result = null;
		List<ContactSearchResultInstance> list = new ArrayList<ContactSearchResultInstance>();

		try {
			
			HibernateUtil.openSession();
			HibernateUtil.startTransaction();
			
			List<Contact> dbInstances = DbContact.findByUcn(ucn, (char) 0, AppConstants.STATUS_DELETED);
			
			int i = 0;
			int totSize = 0;

			for (Contact dbInstance : dbInstances) {
				if (qualifyContact(dbInstance, AppConstants.parseFilterTerms(filter))) {
					totSize++;
					//	Paging... start from where asked, and don't return more than requested
					if (i >= loadConfig.getOffset() && list.size() < loadConfig.getLimit()) {
						ContactInstance instance = DbContact.getInstance(dbInstance);
						DbContact.setDescriptions(instance);
						list.add(new ContactSearchResultInstance(instance));
					}
					i++;
				}
			}
			result = new SynchronizedPagingLoadResult<ContactSearchResultInstance>(list, loadConfig.getOffset(), totSize, syncId);
		
		} catch (Exception exc) {
			exc.printStackTrace();
		} finally {
			HibernateUtil.endTransaction();
			HibernateUtil.closeSession();	
		}
		
		if (result == null)	//	This is an "error" result with no data, but the count of how many might have qualified
			result = new SynchronizedPagingLoadResult<ContactSearchResultInstance>(list, loadConfig.getOffset(), 0, syncId);
		return result;
	}
	
	/**
	 * Search for institutions
	 * @param loadConfig
	 * @param ucn
	 * @param searchInstitutions
	 * @param filter
	 * @param syncId
	 * @return
	 */
	public SynchronizedPagingLoadResult<ContactSearchResultInstance> searchContactsAndInstitutions(PagingLoadConfig loadConfig, int ucn, boolean searchInstitutions, String filter, long syncId) {
		
		// This is just a BasePagingLoadResult,but it has a synchronization tag to make sure old, slow, late search results don't overwrite newer, better results
		SynchronizedPagingLoadResult<ContactSearchResultInstance> result = searchContactsOnly(loadConfig, ucn,searchInstitutions, filter, syncId);
		
		if (result.getResult().getData() != null && result.getResult().getData().size() > 0) {
			return result;
		}
		
		try {
			List<ContactSearchResultInstance> list = new ArrayList<ContactSearchResultInstance>();
			
			//	If we're looking for institutions, we need some criteria of some sort, or else return nothing
			if (filter == null || filter.length() == 0)
				return new SynchronizedPagingLoadResult<ContactSearchResultInstance>(list, loadConfig.getOffset(), 0, syncId);
			
			if (InstitutionCache.getSingleton() == null || !InstitutionCache.getSingleton().isMapsReady())
				throw new ServiceNotReadyException("contact search function institution search");
			
			//	Filtering... only get the UCNs for all of the terms listed, using the cache
			String filters [] = InstitutionCache.getSingleton().parseFilter(filter);
			List<Integer> ucns = InstitutionCache.getSingleton().getFilteredUcns(filter);
			
			if (ucns == null || ucns.size() == 0) {
				// Nothing qualified, so look in the counts to see if there were too many
				int bestCount = InstitutionCache.getSingleton().getFilteredUcnCount(filters);
				
				//	This is an "error" result with no data, but the count of how many might have qualified
				result = new SynchronizedPagingLoadResult<ContactSearchResultInstance>(list, loadConfig.getOffset(), bestCount, syncId);
			} else {
				
				HibernateUtil.openSession();
				HibernateUtil.startTransaction();
				
				//	Convert contact fields into institution fields where necessary
				String sortCol = loadConfig.getSortField();
				if (sortCol != null && "fullName".equals(sortCol))
					sortCol = "institutionName";
				
				List<Institution> dbInstances = DbInstitution.findFiltered(ucns, (char) 0, AppConstants.STATUS_DELETED, sortCol, loadConfig.getSortDir());
				
				int i = 0;
				int totSize = 0;

				for (Institution dbInstance : dbInstances) {
					if (qualifyInstitution(dbInstance, filters)) {
						totSize++;
						//	Paging... start from where asked, and don't return more than requested
						if (i >= loadConfig.getOffset() && list.size() < loadConfig.getLimit()) {
							InstitutionInstance instance = DbInstitution.getInstance(dbInstance);
//							instance.setTypeDescription(InstitutionCache.getSingleton().getInstitutionType(instance.getTypeCode()).getDescription());
//							instance.setGroupDescription(InstitutionCache.getSingleton().getInstitutionGroup(instance.getGroupCode()).getDescription());
//							instance.setPublicPrivateDescription(InstitutionCache.getSingleton().getInstitutionPubPriv(instance.getPublicPrivateCode()).getDescription());
							list.add(new ContactSearchResultInstance(instance));
							
						}
						i++;
					}
				}
				result = new SynchronizedPagingLoadResult<ContactSearchResultInstance>(list, loadConfig.getOffset(), totSize, syncId);
				
				HibernateUtil.endTransaction();
				HibernateUtil.closeSession();
			}
			

		} catch (InstitutionCache.InstitutionCacheNotReady exc) {
			throw new ServiceNotReadyException("Contact search function");
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
			&&  (dbInstance.getPhone() == null || dbInstance.getPhone().toUpperCase().indexOf(filter) < 0)
			&&  (dbInstance.getFax() == null || dbInstance.getFax().toUpperCase().indexOf(filter) < 0)
			&&  (dbInstance.getUcn() + "").indexOf(filter) < 0
			&&  dbInstance.getAlternateIds().toUpperCase().indexOf(filter) < 0)
				return false;
		}
		return true;
	}
	
	/**
	 * Apply all filters to be sure this contact qualifies.
	 * @param dbInstance
	 * @param filters
	 * @return
	 */
	private boolean qualifyContact(Contact dbInstance, String [] filters) {
		for (int i = 0; i < filters.length; i++) {
			String filter = filters [i].trim().toUpperCase();
			if (filter.length() == 0)
				continue;
			if (dbInstance.getFullName().toUpperCase().indexOf(filter) < 0
			&&  dbInstance.getAddress1().toUpperCase().indexOf(filter) < 0
			&&  dbInstance.getCity().toUpperCase().indexOf(filter) < 0
			&&  dbInstance.getZip().toUpperCase().indexOf(filter) < 0)
				return false;
		}
		return true;
	}
}
