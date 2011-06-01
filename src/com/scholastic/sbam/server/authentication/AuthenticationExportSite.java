package com.scholastic.sbam.server.authentication;

import java.util.List;

import com.scholastic.sbam.server.database.codegen.Agreement;
import com.scholastic.sbam.server.database.codegen.AuthMethod;
import com.scholastic.sbam.server.database.codegen.Site;
import com.scholastic.sbam.server.database.objects.DbAuthMethod;
import com.scholastic.sbam.server.util.ConsoleOutputter;
import com.scholastic.sbam.shared.objects.ExportProcessReport;
import com.scholastic.sbam.shared.util.AppConstants;

/**
 * This class represents a site in the Authentication Export process.
 * @author Bob Lacatena
 *
 */
public class AuthenticationExportSite {
	protected ExportProcessReport	exportReport;
	protected ConsoleOutputter		output;
	
	protected	Agreement	agreement;
	protected	Site		site;
	
	public AuthenticationExportSite(Agreement agreement, Site site, ConsoleOutputter output, ExportProcessReport exportProcessReport) {
		this.exportReport	=	exportProcessReport;
		this.output			=	output;
		this.agreement		=	agreement;
		this.site			=	site;
		
		output.consoleOutput("Agreement Site : " + agreement.getId() + " ... " + 
													site.getId().getUcn() + "-" + 
													site.getId().getUcnSuffix() + ":" + 
													site.getId().getSiteLocCode() + 
													" (" +site.getDescription() + ").");
	}
	
	public void exportSite() {
		exportReport.countSite();
		
		//	Site authentication methods
		
		List<AuthMethod> siteAuthMethods = DbAuthMethod.findBySite(	
																	site.getId().getUcn(), 
																	site.getId().getUcnSuffix(), 
																	site.getId().getSiteLocCode(), 
																	null, AppConstants.STATUS_ACTIVE, AppConstants.STATUS_DELETED
																);
		
		for (AuthMethod authMethod : siteAuthMethods) {
			new AuthenticationExportMethod(agreement, site, authMethod, output, exportReport).exportMethod();
		}
		
		//	Agreement authentication methods
		List<AuthMethod> agreementAuthMethods = DbAuthMethod.findByOwner(
																agreement.getId(), 
																site.getId().getUcn(), 
																site.getId().getUcnSuffix(), 
																site.getId().getSiteLocCode(), 
																null, AppConstants.STATUS_ACTIVE, AppConstants.STATUS_DELETED
															);
		
		for (AuthMethod authMethod : agreementAuthMethods) {
			new AuthenticationExportMethod(agreement, site, authMethod, output, exportReport).exportMethod();
		}
	}
}
