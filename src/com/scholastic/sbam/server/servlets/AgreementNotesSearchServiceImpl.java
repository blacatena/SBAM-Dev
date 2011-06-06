package com.scholastic.sbam.server.servlets;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.Style.SortDir;
import com.extjs.gxt.ui.client.data.LoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.scholastic.sbam.client.services.AgreementNotesSearchService;
import com.scholastic.sbam.server.database.codegen.Agreement;
import com.scholastic.sbam.server.database.codegen.AgreementContact;
import com.scholastic.sbam.server.database.codegen.AgreementLink;
import com.scholastic.sbam.server.database.codegen.AgreementSite;
import com.scholastic.sbam.server.database.codegen.AgreementTerm;
import com.scholastic.sbam.server.database.codegen.AuthMethod;
import com.scholastic.sbam.server.database.codegen.Contact;
import com.scholastic.sbam.server.database.codegen.Institution;
import com.scholastic.sbam.server.database.codegen.Product;
import com.scholastic.sbam.server.database.codegen.RemoteSetupUrl;
import com.scholastic.sbam.server.database.codegen.Site;
import com.scholastic.sbam.server.database.objects.DbAgreement;
import com.scholastic.sbam.server.database.objects.DbAgreementContact;
import com.scholastic.sbam.server.database.objects.DbAgreementLink;
import com.scholastic.sbam.server.database.objects.DbAgreementSite;
import com.scholastic.sbam.server.database.objects.DbAgreementTerm;
import com.scholastic.sbam.server.database.objects.DbAuthMethod;
import com.scholastic.sbam.server.database.objects.DbContact;
import com.scholastic.sbam.server.database.objects.DbInstitution;
import com.scholastic.sbam.server.database.objects.DbProduct;
import com.scholastic.sbam.server.database.objects.DbRemoteSetupUrl;
import com.scholastic.sbam.server.database.objects.DbSite;
import com.scholastic.sbam.server.database.util.HibernateUtil;
import com.scholastic.sbam.server.fastSearch.InstitutionCache;
import com.scholastic.sbam.server.fastSearch.InstitutionCache.InstitutionCacheConflict;
import com.scholastic.sbam.shared.objects.AgreementContactInstance;
import com.scholastic.sbam.shared.objects.AgreementInstance;
import com.scholastic.sbam.shared.objects.AgreementLinkInstance;
import com.scholastic.sbam.shared.objects.AgreementNotesTuple;
import com.scholastic.sbam.shared.objects.AgreementSiteInstance;
import com.scholastic.sbam.shared.objects.AgreementTermInstance;
import com.scholastic.sbam.shared.objects.AuthMethodInstance;
import com.scholastic.sbam.shared.objects.ContactInstance;
import com.scholastic.sbam.shared.objects.InstitutionInstance;
import com.scholastic.sbam.shared.objects.ProductInstance;
import com.scholastic.sbam.shared.objects.RemoteSetupUrlInstance;
import com.scholastic.sbam.shared.objects.SiteInstance;
import com.scholastic.sbam.shared.objects.SynchronizedPagingLoadResult;
import com.scholastic.sbam.shared.security.SecurityManager;
import com.scholastic.sbam.shared.util.AppConstants;
import com.scholastic.sbam.shared.util.SearchUtilities;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class AgreementNotesSearchServiceImpl extends AuthenticatedServiceServlet implements AgreementNotesSearchService {

	@Override
	public SynchronizedPagingLoadResult<AgreementNotesTuple> searchAgreementNotes(LoadConfig loadConfig, long syncId) throws IllegalArgumentException {
	
		authenticate("search agreement contacts", SecurityManager.ROLE_QUERY);
		
		List<AgreementNotesTuple> list = new ArrayList<AgreementNotesTuple>();

		String filter	= loadConfig.get("filter")   != null ? loadConfig.get("filter").toString() : null;

		if (filter == null || filter.trim().length() == 0)
			return new SynchronizedPagingLoadResult<AgreementNotesTuple>(list, 0, 0, syncId); 
		
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
			
			/* Agreements */
			
			List<Agreement> agreements = DbAgreement.findByNote(searchSettings.getFilter(), searchSettings.isDoBoolean(), AppConstants.STATUS_ANY_NONE, AppConstants.STATUS_DELETED, sortField, sortDir);
			for (Agreement agreement : agreements) {
				AgreementInstance instance = DbAgreement.getInstance(agreement);
				setDescriptions(instance);
				setInstitution(instance);		
				AgreementNotesTuple tuple = new AgreementNotesTuple(instance);
				list.add(tuple);
			}
			
			/* Terms */
			
			List<Object []> terms = DbAgreementTerm.findByNote(searchSettings.getFilter(), searchSettings.isDoBoolean(), AppConstants.STATUS_ANY_NONE, AppConstants.STATUS_DELETED, sortField, sortDir);
			for (Object [] result : terms) {
				Agreement 			dbAgreement = (Agreement) result [0];
				AgreementTerm		dbAgreementTerm = (AgreementTerm) result [1];
				Product				dbProduct = (Product) result [2];
				
				AgreementInstance		agreement		= DbAgreement.getInstance(dbAgreement);
				AgreementTermInstance	agreementTerm	= DbAgreementTerm.getInstance(dbAgreementTerm);
				ProductInstance			product			= DbProduct.getInstance(dbProduct);
				
				agreementTerm.setProduct(product);
				
				setDescriptions(agreement);
				setInstitution(agreement);
				DbAgreementTerm.setDescriptions(agreementTerm);
			//	DbProduct.setDescriptions(product);
				
				AgreementNotesTuple tuple = new AgreementNotesTuple(agreement, agreementTerm);
				list.add(tuple);
			}
			
			/* Sites */
			
			List<Object []> sites = DbAgreementSite.findByNote(searchSettings.getFilter(), searchSettings.isDoBoolean(), AppConstants.STATUS_ANY_NONE, AppConstants.STATUS_DELETED, sortField, sortDir);
			for (Object [] result : sites) {
				Agreement 			dbAgreement = (Agreement) result [0];
				AgreementSite		dbAgreementSite = (AgreementSite) result [1];
				Site				dbSite = (Site) result [2];
				Institution			dbInstitution = (Institution) result [3];
				
				AgreementInstance		agreement = DbAgreement.getInstance(dbAgreement);
				AgreementSiteInstance	agreementSite = DbAgreementSite.getInstance(dbAgreementSite);
				SiteInstance			site = DbSite.getInstance(dbSite);
				InstitutionInstance		institution = DbInstitution.getInstance(dbInstitution);
				
				agreementSite.setSite(site);
				
				setDescriptions(agreement);
				setInstitution(agreement);
				DbSite.setDescriptions(site, institution);
				DbAgreementSite.setDescriptions(agreementSite, site, institution);
				
				AgreementNotesTuple tuple = new AgreementNotesTuple(agreement, agreementSite);
				list.add(tuple);
			}
			
			/* Contacts */
			
			List<Object []> contacts = DbAgreementContact.findByNote(searchSettings.getFilter(), searchSettings.isDoBoolean(), AppConstants.STATUS_ANY_NONE, AppConstants.STATUS_DELETED, sortField, sortDir);
			for (Object [] result : contacts) {
				Agreement 			dbAgreement = (Agreement) result [0];
				AgreementContact	dbAgreementContact = (AgreementContact) result [1];
				Contact				dbContact = (Contact) result [2];
				
				AgreementInstance			agreement = DbAgreement.getInstance(dbAgreement);
				AgreementContactInstance	agreementContact = DbAgreementContact.getInstance(dbAgreementContact);
				ContactInstance				contact = DbContact.getInstance(dbContact);
				
				agreementContact.setContact(contact);
				
				setDescriptions(agreement);
				setInstitution(agreement);
				DbAgreementContact.setDescriptions(agreementContact);
				DbContact.setDescriptions(contact);
				
				AgreementNotesTuple tuple = new AgreementNotesTuple(agreement, agreementContact);
				list.add(tuple);
			}
			
			/* Methods */
			
			List<Object []> methods = DbAuthMethod.findByNote(searchSettings.getFilter(), searchSettings.isDoBoolean(), AppConstants.STATUS_ANY_NONE, AppConstants.STATUS_DELETED, sortField, sortDir);
			for (Object [] result : methods) {
				Agreement 		dbAgreement = (Agreement) result [0];
				AuthMethod		dbAuthMethod = (AuthMethod) result [1];
				
				AgreementInstance	agreement	= DbAgreement.getInstance(dbAgreement);
				AuthMethodInstance	authMethod	= DbAuthMethod.getInstance(dbAuthMethod);
				
				setDescriptions(agreement);
				setInstitution(agreement);
				DbAuthMethod.setDescriptions(authMethod);
			//	DbProduct.setDescriptions(product);
				
				AgreementNotesTuple tuple = new AgreementNotesTuple(agreement, authMethod);
				list.add(tuple);
			}
			
			/* Methods */
			
			List<Object []> remoteSetupUrls = DbRemoteSetupUrl.findByNote(searchSettings.getFilter(), searchSettings.isDoBoolean(), AppConstants.STATUS_ANY_NONE, AppConstants.STATUS_DELETED, sortField, sortDir);
			for (Object [] result : remoteSetupUrls) {
				Agreement 		dbAgreement = (Agreement) result [0];
				RemoteSetupUrl		dbRemoteSetupUrl = (RemoteSetupUrl) result [1];
				
				AgreementInstance	agreement	= DbAgreement.getInstance(dbAgreement);
				RemoteSetupUrlInstance	remoteSetupUrl	= DbRemoteSetupUrl.getInstance(dbRemoteSetupUrl);
				
				setDescriptions(agreement);
				setInstitution(agreement);
				DbRemoteSetupUrl.setDescriptions(remoteSetupUrl);
			//	DbProduct.setDescriptions(product);
				
				AgreementNotesTuple tuple = new AgreementNotesTuple(agreement, remoteSetupUrl);
				list.add(tuple);
			}
			
			/* Links */
			
			List<Object []> links = DbAgreementLink.findByNote(searchSettings.getFilter(), searchSettings.isDoBoolean(), AppConstants.STATUS_ANY_NONE, AppConstants.STATUS_DELETED, sortField, sortDir);
			for (Object [] result : links) {
				Agreement 			dbAgreement = (Agreement) result [0];
				AgreementLink	dbAgreementLink = (AgreementLink) result [1];
				Institution			dbInstitution = (Institution) result [2];
				
				AgreementInstance		agreement = DbAgreement.getInstance(dbAgreement);
				AgreementLinkInstance	agreementLink = DbAgreementLink.getInstance(dbAgreementLink);
				InstitutionInstance		institution = DbInstitution.getInstance(dbInstitution);
				
				setDescriptions(agreement);
				setInstitution(agreement);
				InstitutionCache.getSingleton().setDescriptions(institution);
				agreementLink.setInstitution(institution);
				DbAgreementLink.setDescriptions(agreementLink);
				
				AgreementNotesTuple tuple = new AgreementNotesTuple(agreement, agreementLink);
				list.add(tuple);
			}
			
			/* Limits */
			
			int loadLimit = AppConstants.STANDARD_LOAD_LIMIT;
			if (loadConfig.get("limit") != null && loadConfig.get("limit") instanceof Integer)
				loadLimit = ( (Integer) loadConfig.get("limit"));
			if (list.size() > loadLimit) {
				//	This is an error return with no data, but a count (too many results)
				return new SynchronizedPagingLoadResult<AgreementNotesTuple>(list, 0, list.size(), syncId);
			}

		} catch (Exception exc) {
			exc.printStackTrace();
		}
		
		HibernateUtil.endTransaction();
		HibernateUtil.closeSession();
		
		return new SynchronizedPagingLoadResult<AgreementNotesTuple>(list, 0, list.size(), syncId);
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
}
