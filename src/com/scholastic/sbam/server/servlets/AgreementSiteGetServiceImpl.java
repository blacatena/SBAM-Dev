package com.scholastic.sbam.server.servlets;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.scholastic.sbam.client.services.AgreementSiteGetService;
import com.scholastic.sbam.server.database.codegen.Agreement;
import com.scholastic.sbam.server.database.codegen.AgreementSite;
import com.scholastic.sbam.server.database.codegen.AgreementTerm;
import com.scholastic.sbam.server.database.codegen.Institution;
import com.scholastic.sbam.server.database.objects.DbAgreement;
import com.scholastic.sbam.server.database.objects.DbAgreementSite;
import com.scholastic.sbam.server.database.objects.DbAgreementTerm;
import com.scholastic.sbam.server.database.objects.DbInstitution;
import com.scholastic.sbam.server.database.util.HibernateUtil;
import com.scholastic.sbam.server.fastSearch.InstitutionCache;
import com.scholastic.sbam.shared.objects.AgreementInstance;
import com.scholastic.sbam.shared.objects.AgreementSiteInstance;
import com.scholastic.sbam.shared.objects.AgreementSiteTuple;
import com.scholastic.sbam.shared.objects.AgreementTermInstance;
import com.scholastic.sbam.shared.security.SecurityManager;
import com.scholastic.sbam.shared.util.AppConstants;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class AgreementSiteGetServiceImpl extends AuthenticatedServiceServlet implements AgreementSiteGetService {

	@Override
	public AgreementSiteTuple getAgreementSite(int agreementId, int ucn, int ucnSuffix, String siteLocCode, boolean loadTerms, boolean allTerms) throws IllegalArgumentException {
		
		authenticate("get agreement term", SecurityManager.ROLE_QUERY);
		
		HibernateUtil.openSession();
		HibernateUtil.startTransaction();

		AgreementInstance		agreement = null;
		AgreementSiteInstance	agreementSite = null;
		AgreementSiteTuple		agreementSiteTuple = null;
		try {
			Agreement		dbInstance = DbAgreement.getById(agreementId);
			AgreementSite	dbSiteInstance = DbAgreementSite.getById(agreementId, ucn, ucnSuffix, siteLocCode);
			if (dbInstance != null && dbSiteInstance != null) {
				agreement = DbAgreement.getInstance(dbInstance);
				setDescriptions(agreement);
				agreementSite = DbAgreementSite.getInstance(dbSiteInstance);
				setDescriptions(agreementSite);
				
				// Get the institution
				if (agreement.getBillUcn() > 0) {
					Institution dbInstitution = DbInstitution.getByCode(agreement.getBillUcn());
					agreement.setInstitution(DbInstitution.getInstance(dbInstitution));

					if (agreement.getInstitution() != null) {
						InstitutionCache.getSingleton().setDescriptions( agreement.getInstitution() );
					}
				}
				
				agreementSiteTuple = new AgreementSiteTuple(agreement, agreementSite);
				
				if (loadTerms)
					loadAgreementTerms(agreement, allTerms);
			}

		} catch (Exception exc) {
			exc.printStackTrace();
		}
		
		HibernateUtil.endTransaction();
		HibernateUtil.closeSession();
		
		return agreementSiteTuple;
	}
	
	private void setDescriptions(AgreementInstance agreement) {
		DbAgreement.setDescriptions(agreement);
	}
	
	private void setDescriptions(AgreementSiteInstance agreementSite) {
		DbAgreementSite.setDescriptions(agreementSite);
	}
	
	private void setDescriptions(AgreementTermInstance agreementTerm) {
		DbAgreementTerm.setDescriptions(agreementTerm);
	}
	
	private void loadAgreementTerms(AgreementInstance agreement, boolean allTerms) {
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
		List<AgreementTermInstance> list = new ArrayList<AgreementTermInstance>();
		for (AgreementTerm dbAgreementTerm : dbAgreementTerms) {
			AgreementTermInstance termInstance = DbAgreementTerm.getInstance(dbAgreementTerm);
			//	Separate current value computation
			if (termInstance.deliverService())
				agreement.setCurrentValue(agreement.getCurrentValue() + dbAgreementTerm.getDollarValue().doubleValue());
			
			//	Figure out which terms to keep in the list, too
			if (allTerms) {
				list.add(termInstance);
			} else if (chosenPath == null && chosenDate == null) {
				list.add(termInstance);
			} else if (chosenPath != null && chosenPath.equals(dbAgreementTerm.getPrimaryOrgPath())) {
				list.add(termInstance);
			} else if (dbAgreementTerm.getEndDate() == null || (chosenDate != null && chosenDate.getTime().before(dbAgreementTerm.getEndDate()))) {
				list.add(termInstance);
			} // else don't add it, we don't need it
		}
		
		agreement.setAgreementTerms(list);
		for (AgreementTermInstance term : list) {
			setDescriptions(term);
		}
		
	}
}
