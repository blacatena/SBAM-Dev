package com.scholastic.sbam.server.servlets;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.extjs.gxt.ui.client.data.LoadConfig;
import com.scholastic.sbam.client.services.AgreementSearchService;
import com.scholastic.sbam.server.database.codegen.Agreement;
import com.scholastic.sbam.server.database.codegen.AgreementTerm;
import com.scholastic.sbam.server.database.codegen.Institution;
import com.scholastic.sbam.server.database.objects.DbAgreement;
import com.scholastic.sbam.server.database.objects.DbAgreementTerm;
import com.scholastic.sbam.server.database.objects.DbInstitution;
import com.scholastic.sbam.server.database.util.HibernateUtil;
import com.scholastic.sbam.server.fastSearch.InstitutionCache;
import com.scholastic.sbam.shared.objects.AgreementInstance;
import com.scholastic.sbam.shared.objects.AgreementTermInstance;
import com.scholastic.sbam.shared.objects.SynchronizedPagingLoadResult;
import com.scholastic.sbam.shared.security.SecurityManager;
import com.scholastic.sbam.shared.util.AppConstants;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class AgreementSearchServiceImpl extends AuthenticatedServiceServlet implements AgreementSearchService {

	@Override
	public SynchronizedPagingLoadResult<AgreementInstance> searchAgreements(LoadConfig config, AgreementInstance sampleInstance, boolean currentTerms, long syncId) throws IllegalArgumentException {
	
		authenticate("search agreements", SecurityManager.ROLE_QUERY);
		
		HibernateUtil.openSession();
		HibernateUtil.startTransaction();
		
		List<AgreementInstance> list = new ArrayList<AgreementInstance>();
		
		try {
			String agreementId			= getAgreementId(config, sampleInstance);
			String billUcn				= getBillUcn(config, sampleInstance);
			String linkId				= getLinkId(config, sampleInstance);
			String agreementTypeCode	= getAgreementTypeCode(config, sampleInstance);
			String note					= getNote(config, sampleInstance);
			
			List<Agreement> agreements = DbAgreement.findFiltered(agreementId, billUcn, linkId, agreementTypeCode, note);
			
			int loadLimit = AppConstants.STANDARD_LOAD_LIMIT;
			if (config.get("limit") != null && config.get("limit") instanceof Integer)
				loadLimit = ( (Integer) config.get("limit"));
			if (agreements.size() > loadLimit) {
				//	This is an error return with no data, but a count (too many results)
				return new SynchronizedPagingLoadResult<AgreementInstance>(list, 0, agreements.size(), syncId);
			}
			
			for (Agreement dbInstance : agreements) {
				if (dbInstance != null) {
					AgreementInstance agreement = DbAgreement.getInstance(dbInstance);
					setDescriptions(agreement);
					
					// Get the institution
					if (agreement.getBillUcn() > 0) {
						Institution dbInstitution = DbInstitution.getByCode(agreement.getBillUcn());
						agreement.setInstitution(DbInstitution.getInstance(dbInstitution));
	
						if (agreement.getInstitution() != null) {
							InstitutionCache.getSingleton().setDescriptions( agreement.getInstitution() );
						}
					}
					
					loadTerms(agreement);
				}
			}

		} catch (Exception exc) {
			exc.printStackTrace();
		}
		
		HibernateUtil.endTransaction();
		HibernateUtil.closeSession();
		
		return new SynchronizedPagingLoadResult<AgreementInstance>(list, 0, list.size(), syncId);
	}
	
	private void loadTerms(AgreementInstance agreement) {
		//	Find only undeleted term types
		List<AgreementTerm> dbAgreementTerms = DbAgreementTerm.findByAgreementId(agreement.getId(), AppConstants.STATUS_ANY_NONE, AppConstants.STATUS_DELETED);

		//	First scan through the instances, to see what date or path to use
		String		chosenPath = null;
		Calendar	chosenDate = null;
		for (AgreementTerm dbAgreementTerm : dbAgreementTerms) {

			//	 Determine the most recent path, or the most recent date
				if (chosenPath == null && chosenDate == null && dbAgreementTerm.getEndDate() != null) {
					chosenDate = Calendar.getInstance();
					chosenDate.setTime(dbAgreementTerm.getEndDate());
					chosenPath = dbAgreementTerm.getPrimaryOrgPath();
				} else if (dbAgreementTerm.getEndDate() != null && dbAgreementTerm.getEndDate().after(chosenDate.getTime())) {
					//	If we find something that ends more than a year later than this, throw out anything too old
					chosenDate.setTime(dbAgreementTerm.getEndDate());
					chosenPath = dbAgreementTerm.getPrimaryOrgPath();
				}
				

		}
		
		//	Keep either the chosen org path, or the oldest end date less a year
		if (chosenPath != null && chosenPath.length() > 0) {
			chosenDate = null;	// Ignore the date, and pick anything in the same path as the latest end date
		} else if (chosenDate != null) {
			chosenPath = null;
			chosenDate.add(Calendar.YEAR, -1);	//	Set the last date back one year, and choose within that
		}

		//  Second, scan again to pick which terms to keep  -- at the same time, compute the current value
		List<AgreementTermInstance> terms = new ArrayList<AgreementTermInstance>();
		for (AgreementTerm dbAgreementTerm : dbAgreementTerms) {
			AgreementTermInstance termInstance = DbAgreementTerm.getInstance(dbAgreementTerm);
			//	Separate current value computation
			if (termInstance.deliverService())
				agreement.setCurrentValue(agreement.getCurrentValue() + dbAgreementTerm.getDollarValue().doubleValue());
			
			//	Figure out which terms to keep in the list, too
			if (chosenPath == null && chosenDate == null) {
				terms.add(termInstance);
			} else if (chosenPath != null && chosenPath.equals(dbAgreementTerm.getPrimaryOrgPath())) {
				terms.add(termInstance);
			} else if (dbAgreementTerm.getEndDate() == null || (chosenDate != null && chosenDate.getTime().before(dbAgreementTerm.getEndDate()))) {
				terms.add(termInstance);
			} // else don't add it, we don't need it
		}
		
		agreement.setAgreementTerms(terms);
		for (AgreementTermInstance term : terms) {
			setDescriptions(term);
		}
	}
	
	private String getNote(LoadConfig config, AgreementInstance sampleInstance) {
		if (sampleInstance != null)
			if (sampleInstance.getNote() != null && sampleInstance.getNote().length() > 0)
				return sampleInstance.getNote();
		if (config.get("note") != null)
			return config.get("note").toString();
		return null;
	}

	private String getAgreementTypeCode(LoadConfig config, AgreementInstance sampleInstance) {
		if (sampleInstance != null)
			if (sampleInstance.getAgreementTypeCode() != null && sampleInstance.getAgreementTypeCode().length() > 0)
				return sampleInstance.getAgreementTypeCode();
		if (config.get("agreementTypeCode") != null)
			return config.get("agreementTypeCode").toString();
		return null;
	}

	private String getLinkId(LoadConfig config, AgreementInstance sampleInstance) {
		if (sampleInstance != null)
			if (sampleInstance.getAgreementLinkId() > 0)
				return sampleInstance.getAgreementLinkId() + "";
		if (config.get("agreementLinkId") != null)
			return config.get("agreementLinkId").toString();
		return null;
	}

	private String getBillUcn(LoadConfig config, AgreementInstance sampleInstance) {
		if (sampleInstance != null)
			if (sampleInstance.getBillUcn() > 0)
				return sampleInstance.getBillUcn() + "";
		if (config.get("billUcn") != null)
			return config.get("billUcn").toString();
		return null;
	}

	private String getAgreementId(LoadConfig config, AgreementInstance sampleInstance) {
		if (sampleInstance != null)
			if (sampleInstance.getId() > 0)
				return sampleInstance.getId() + "";
		if (config.get("id") != null)
			return config.get("id").toString();
		return null;
	}

	private void setDescriptions(AgreementInstance agreement) {
		DbAgreement.setDescriptions(agreement);
	}
	
	private void setDescriptions(AgreementTermInstance agreementTerm) {
		DbAgreementTerm.setDescriptions(agreementTerm);
	}
}
