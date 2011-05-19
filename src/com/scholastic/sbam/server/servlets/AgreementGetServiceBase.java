package com.scholastic.sbam.server.servlets;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.scholastic.sbam.server.database.codegen.AgreementTerm;
import com.scholastic.sbam.server.database.objects.DbAgreement;
import com.scholastic.sbam.server.database.objects.DbAgreementTerm;
import com.scholastic.sbam.shared.objects.AgreementInstance;
import com.scholastic.sbam.shared.objects.AgreementTermInstance;
import com.scholastic.sbam.shared.util.AppConstants;

/**
 * Base class providing methods for any class that must load and attach some or all agreement terms to an agreement instance.
 */
@SuppressWarnings("serial")
public class AgreementGetServiceBase extends AuthenticatedServiceServlet {
	
	public void setDescriptions(AgreementInstance agreement) {
		DbAgreement.setDescriptions(agreement);
	}
	
	public void setDescriptions(AgreementTermInstance agreementTerm) {
		DbAgreementTerm.setDescriptions(agreementTerm);
	}
	
	public void loadAgreementTerms(AgreementInstance agreement, boolean allTerms) {
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
