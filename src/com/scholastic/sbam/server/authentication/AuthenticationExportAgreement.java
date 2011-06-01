package com.scholastic.sbam.server.authentication;

import com.scholastic.sbam.server.database.codegen.Agreement;
import com.scholastic.sbam.server.database.codegen.AgreementSite;
import com.scholastic.sbam.server.database.codegen.AuthMethod;
import com.scholastic.sbam.server.database.codegen.Site;
import com.scholastic.sbam.server.database.objects.DbAgreement;
import com.scholastic.sbam.server.database.objects.DbAgreementSite;
import com.scholastic.sbam.server.database.objects.DbAuthMethod;
import com.scholastic.sbam.server.database.objects.DbSite;
import com.scholastic.sbam.server.util.ConsoleOutputter;
import com.scholastic.sbam.shared.exceptions.AuthenticationExportException;
import com.scholastic.sbam.shared.objects.ExportProcessReport;
import com.scholastic.sbam.shared.util.AppConstants;

public class AuthenticationExportAgreement {
	protected ExportProcessReport	exportReport;
	protected ConsoleOutputter		output;
	
	protected Agreement				agreement;
	
	public AuthenticationExportAgreement(int agreementId, ConsoleOutputter output, ExportProcessReport exportProcessReport) {
		this.exportReport	= exportProcessReport;
		this.output					=	output;
		output.consoleOutput("Agreement " + agreementId);
		loadAgreement(agreementId);
	}
	
	public void loadAgreement(int agreementId) {
		agreement = DbAgreement.getById(agreementId);
		if (agreement == null)
			throw new AuthenticationExportException("Agreement not found for ID " + agreementId);
	}
	
	public void exportSites() {
		if (agreement == null)
			throw new AuthenticationExportException("No agreement loaded");
		
		exportReport.countAgreement();
		
		//	Authenticate site related methods
		
		for (AgreementSite agreementSite : DbAgreementSite.findByAgreementId(agreement.getId(), AppConstants.STATUS_ACTIVE, AppConstants.STATUS_DELETED)) {
			if (agreementSite.getId().getSiteLocCode().length() == 0) {
				//	All sites
				for (Site site : DbSite.findByUcn(agreementSite.getId().getSiteUcn(), agreementSite.getId().getSiteUcnSuffix(), AppConstants.STATUS_ACTIVE, AppConstants.STATUS_DELETED)) {
					new AuthenticationExportSite(agreement, site, output, exportReport).exportSite();
				}
			} else {
				Site site = DbSite.getById(agreementSite.getId().getSiteUcn(), agreementSite.getId().getSiteUcnSuffix(), agreementSite.getId().getSiteLocCode());
				if (site == null) {
					throw new AuthenticationExportException("Site " +
															agreementSite.getId().getSiteUcn() + "-" +
															agreementSite.getId().getSiteUcnSuffix() + ":" +
															agreementSite.getId().getSiteLocCode() + 
															" not found for agreement " + agreementSite.getId().getAgreementId() + ".");
				}
			}
		}
		
		//	Authenticate agreement related methods
		
		for (AuthMethod authMethod : DbAuthMethod.findByAgreementId(agreement.getId(), null, AppConstants.STATUS_ACTIVE, AppConstants.STATUS_DELETED)) {
			new AuthenticationExportMethod(agreement, null, authMethod, output, exportReport).exportMethod();
		}
	}
}
