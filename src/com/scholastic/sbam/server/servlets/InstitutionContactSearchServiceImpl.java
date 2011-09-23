package com.scholastic.sbam.server.servlets;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.Style.SortDir;
import com.extjs.gxt.ui.client.data.LoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.scholastic.sbam.client.services.InstitutionContactSearchService;
import com.scholastic.sbam.server.database.codegen.Institution;
import com.scholastic.sbam.server.database.codegen.InstitutionContact;
import com.scholastic.sbam.server.database.codegen.Contact;
import com.scholastic.sbam.server.database.objects.DbInstitution;
import com.scholastic.sbam.server.database.objects.DbInstitutionContact;
import com.scholastic.sbam.server.database.objects.DbContact;
import com.scholastic.sbam.server.database.util.HibernateUtil;
import com.scholastic.sbam.server.fastSearch.InstitutionCache;
import com.scholastic.sbam.server.fastSearch.InstitutionCache.InstitutionCacheConflict;
import com.scholastic.sbam.shared.objects.InstitutionInstance;
import com.scholastic.sbam.shared.objects.InstitutionContactInstance;
import com.scholastic.sbam.shared.objects.InstitutionContactTuple;
import com.scholastic.sbam.shared.objects.ContactInstance;
import com.scholastic.sbam.shared.objects.SynchronizedPagingLoadResult;
import com.scholastic.sbam.shared.security.SecurityManager;
import com.scholastic.sbam.shared.util.AppConstants;
import com.scholastic.sbam.shared.util.SearchUtilities;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class InstitutionContactSearchServiceImpl extends AuthenticatedServiceServlet implements InstitutionContactSearchService {

	@Override
	public SynchronizedPagingLoadResult<InstitutionContactTuple> searchInstitutionContacts(LoadConfig loadConfig, long syncId) throws IllegalArgumentException {
	
		authenticate("search institution contacts", SecurityManager.ROLE_QUERY);
		
		List<InstitutionContactTuple> list = new ArrayList<InstitutionContactTuple>();

		String filter	= loadConfig.get("filter")   != null ? loadConfig.get("filter").toString() : null;

		if (filter == null || filter.trim().length() == 0)
			return new SynchronizedPagingLoadResult<InstitutionContactTuple>(list, 0, 0, syncId); 
		
		SearchUtilities.SearchSettings searchSettings = new SearchUtilities.SearchSettings(filter);
		
		HibernateUtil.openSession();
		HibernateUtil.startTransaction();
		
		try {
			
			String sortField	= null;
			SortDir sortDir		= null;
			if (loadConfig instanceof PagingLoadConfig && ("true".equals(loadConfig.get("remoteSort")))) {
				sortField = ((PagingLoadConfig) loadConfig).getSortField();
				sortDir   = ((PagingLoadConfig) loadConfig).getSortDir();
			}
			
			List<Object []> tuples = DbInstitutionContact.findFiltered(searchSettings.getFilter(), searchSettings.isDoBoolean(), (char) 0, AppConstants.STATUS_DELETED, sortField, sortDir);
			
			int loadLimit = AppConstants.STANDARD_LOAD_LIMIT;
			if (loadConfig.get("limit") != null && loadConfig.get("limit") instanceof Integer)
				loadLimit = ( (Integer) loadConfig.get("limit"));
			if (tuples.size() > loadLimit) {
				//	This is an error return with no data, but a count (too many results)
				return new SynchronizedPagingLoadResult<InstitutionContactTuple>(list, 0, tuples.size(), syncId);
			}
			
			for (Object [] objects : tuples) {

				InstitutionInstance 			institution			= DbInstitution       .getInstance((Institution) 		objects [0]);
				InstitutionContactInstance	institutionContact	= DbInstitutionContact.getInstance((InstitutionContact) objects [1]);
				ContactInstance				contact				= DbContact			.getInstance((Contact) 			objects [2]);
				
				setDescriptions(contact);
				setDescriptions(institutionContact, contact);
				setDescriptions(institution);
					
				InstitutionContactTuple tuple = new InstitutionContactTuple(institution, institutionContact);
				list.add(tuple);
			}

		} catch (Exception exc) {
			exc.printStackTrace();
		}
		
		HibernateUtil.endTransaction();
		HibernateUtil.closeSession();
		
		return new SynchronizedPagingLoadResult<InstitutionContactTuple>(list, 0, list.size(), syncId);
	}
	
	public void setDescriptions(InstitutionInstance institution) throws InstitutionCacheConflict {
		institution.setTypeDescription(InstitutionCache.getSingleton().getInstitutionType(institution.getTypeCode()).getDescription());
		institution.setGroupDescription(InstitutionCache.getSingleton().getInstitutionGroup(institution.getGroupCode()).getDescription());
		institution.setPublicPrivateDescription(InstitutionCache.getSingleton().getInstitutionPubPriv(institution.getPublicPrivateCode()).getDescription());
	}

	private void setDescriptions(ContactInstance contact) {
		DbContact.setDescriptions(contact);
	}

	private void setDescriptions(InstitutionContactInstance institutionContact, ContactInstance contact) {
		institutionContact.setContact(contact);
		DbInstitutionContact.setDescriptions(institutionContact);
	}
}
