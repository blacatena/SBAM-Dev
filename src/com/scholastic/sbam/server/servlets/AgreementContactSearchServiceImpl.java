package com.scholastic.sbam.server.servlets;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.Style.SortDir;
import com.extjs.gxt.ui.client.data.LoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.scholastic.sbam.client.services.AgreementContactSearchService;
import com.scholastic.sbam.server.database.codegen.Agreement;
import com.scholastic.sbam.server.database.codegen.AgreementContact;
import com.scholastic.sbam.server.database.codegen.Contact;
import com.scholastic.sbam.server.database.codegen.Institution;
import com.scholastic.sbam.server.database.objects.DbAgreement;
import com.scholastic.sbam.server.database.objects.DbAgreementContact;
import com.scholastic.sbam.server.database.objects.DbContact;
import com.scholastic.sbam.server.database.objects.DbInstitution;
import com.scholastic.sbam.server.database.util.HibernateUtil;
import com.scholastic.sbam.server.fastSearch.InstitutionCache;
import com.scholastic.sbam.server.fastSearch.InstitutionCache.InstitutionCacheConflict;
import com.scholastic.sbam.shared.objects.AgreementInstance;
import com.scholastic.sbam.shared.objects.AgreementContactInstance;
import com.scholastic.sbam.shared.objects.AgreementContactTuple;
import com.scholastic.sbam.shared.objects.ContactInstance;
import com.scholastic.sbam.shared.objects.SynchronizedPagingLoadResult;
import com.scholastic.sbam.shared.security.SecurityManager;
import com.scholastic.sbam.shared.util.AppConstants;
import com.scholastic.sbam.shared.util.SearchUtilities;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class AgreementContactSearchServiceImpl extends AuthenticatedServiceServlet implements AgreementContactSearchService {

	@Override
	public SynchronizedPagingLoadResult<AgreementContactTuple> searchAgreementContacts(LoadConfig loadConfig, long syncId) throws IllegalArgumentException {
	
		authenticate("search agreement contacts", SecurityManager.ROLE_QUERY);
		
		List<AgreementContactTuple> list = new ArrayList<AgreementContactTuple>();

		String filter	= loadConfig.get("filter")   != null ? loadConfig.get("filter").toString() : null;

		if (filter == null || filter.trim().length() == 0)
			return new SynchronizedPagingLoadResult<AgreementContactTuple>(list, 0, 0, syncId); 
		
		SearchUtilities.SearchSettings searchSettings = new SearchUtilities.SearchSettings(filter);
		
		HibernateUtil.openSession();
		HibernateUtil.startTransaction();
		
		try {
			
			String sortField	= null;
			SortDir sortDir		= null;
			if (loadConfig instanceof PagingLoadConfig) {
				sortField = ((PagingLoadConfig) loadConfig).getSortField();
				sortDir   = ((PagingLoadConfig) loadConfig).getSortDir();
			}
			
			List<Object []> tuples = DbAgreementContact.findFiltered(searchSettings.getFilter(), searchSettings.isDoBoolean(), (char) 0, AppConstants.STATUS_DELETED, sortField, sortDir);
			
			int loadLimit = AppConstants.STANDARD_LOAD_LIMIT;
			if (loadConfig.get("limit") != null && loadConfig.get("limit") instanceof Integer)
				loadLimit = ( (Integer) loadConfig.get("limit"));
			if (tuples.size() > loadLimit) {
				//	This is an error return with no data, but a count (too many results)
				return new SynchronizedPagingLoadResult<AgreementContactTuple>(list, 0, tuples.size(), syncId);
			}
			
			for (Object [] objects : tuples) {

				AgreementInstance 			agreement			= DbAgreement       .getInstance((Agreement) 		objects [0]);
				AgreementContactInstance	agreementContact	= DbAgreementContact.getInstance((AgreementContact) objects [1]);
				ContactInstance				contact				= DbContact			.getInstance((Contact) 			objects [2]);
				
				setDescriptions(contact);
				setDescriptions(agreementContact, contact);
				setDescriptions(agreement);
				setInstitution(agreement);
					
				AgreementContactTuple tuple = new AgreementContactTuple(agreement, agreementContact);
				list.add(tuple);
			}

		} catch (Exception exc) {
			exc.printStackTrace();
		}
		
		HibernateUtil.endTransaction();
		HibernateUtil.closeSession();
		
		return new SynchronizedPagingLoadResult<AgreementContactTuple>(list, 0, list.size(), syncId);
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
				InstitutionCache.getSingleton().setDescriptions( agreement.getInstitution() );
			}
		}
	}

	private void setDescriptions(ContactInstance contact) {
		DbContact.setDescriptions(contact);
	}

	private void setDescriptions(AgreementContactInstance agreementContact, ContactInstance contact) {
		agreementContact.setContact(contact);
		DbAgreementContact.setDescriptions(agreementContact);
	}
}
