package com.scholastic.sbam.server.authentication;

import java.util.List;

import com.scholastic.sbam.server.database.codegen.AeAuthUnit;
import com.scholastic.sbam.server.database.codegen.AePref;
import com.scholastic.sbam.server.database.codegen.AePrefId;
import com.scholastic.sbam.server.database.codegen.Agreement;
import com.scholastic.sbam.server.database.codegen.AuthMethod;
import com.scholastic.sbam.server.database.codegen.Institution;
import com.scholastic.sbam.server.database.codegen.RemoteSetupUrl;
import com.scholastic.sbam.server.database.codegen.Site;
import com.scholastic.sbam.server.database.codegen.SitePreference;
import com.scholastic.sbam.server.database.objects.DbAePref;
import com.scholastic.sbam.server.database.objects.DbAuthMethod;
import com.scholastic.sbam.server.database.objects.DbRemoteSetupUrl;
import com.scholastic.sbam.server.database.objects.DbSitePreference;
import com.scholastic.sbam.server.util.ExportController;
import com.scholastic.sbam.shared.exceptions.AuthenticationExportException;
import com.scholastic.sbam.shared.objects.ExportProcessReport;
import com.scholastic.sbam.shared.util.AppConstants;

/**
 * This class represents a site in the Authentication Export process.
 * @author Bob Lacatena
 *
 */
public class AuthenticationExportSite {
	protected ExportProcessReport				exportProcessReport;
	protected ExportController					controller;
	
	protected	Agreement						agreement;
	protected	Institution						institution;
	protected	Site							site;
	protected	AeAuthUnit						authUnit;
	
	public AuthenticationExportSite(Agreement agreement, AeAuthUnit authUnit, Site site, Institution institution, ExportController controller, ExportProcessReport exportProcessReport) {
		this.exportProcessReport	=	exportProcessReport;
		this.controller				=	controller;
		this.agreement				=	agreement;
		this.authUnit				=	authUnit;
		this.site					=	site;
		this.institution			=	institution;
		
		controller.consoleOutput("Agreement Site : " + agreement.getId() + " ... " + 
													site.getId().getUcn() + "-" + 
													site.getId().getUcnSuffix() + ":" + 
													site.getId().getSiteLocCode() + 
													" (" +site.getDescription() + ").");
	}
	
	public void exportSite() throws AuthenticationExportException {
		exportProcessReport.countSite();
		
		//	Site authentication methods
		
		List<AuthMethod> siteAuthMethods = DbAuthMethod.findBySite(	
																	site.getId().getUcn(), 
																	site.getId().getUcnSuffix(), 
																	site.getId().getSiteLocCode(), 
																	null, AppConstants.STATUS_ACTIVE, AppConstants.STATUS_DELETED
																);
		
		for (AuthMethod authMethod : siteAuthMethods) {
			new AuthenticationExportMethod(agreement, authUnit, site, institution, authMethod, controller, exportProcessReport).exportMethod();
		}
		
		//	Agreement authentication methods for this site for this agreement
		
		List<AuthMethod> agreementAuthMethods = DbAuthMethod.findBySite(
																agreement.getId(), 
																site.getId().getUcn(), 
																site.getId().getUcnSuffix(), 
																site.getId().getSiteLocCode(), 
																null, AppConstants.STATUS_ACTIVE, AppConstants.STATUS_DELETED
															);
		
		for (AuthMethod authMethod : agreementAuthMethods) {
			new AuthenticationExportMethod(agreement, authUnit, site, institution, authMethod, controller, exportProcessReport).exportMethod();
		}
		
		//	Remote setup URLs for this site
		
		List<RemoteSetupUrl> siteRemoteSetupUrls = DbRemoteSetupUrl.findBySite(	
																		site.getId().getUcn(), 
																		site.getId().getUcnSuffix(), 
																		site.getId().getSiteLocCode(), 
																		null, AppConstants.STATUS_ACTIVE, AppConstants.STATUS_DELETED
																	);
		
		for (RemoteSetupUrl remoteSetupUrl : siteRemoteSetupUrls) {
			new AuthenticationExportRemoteSetupUrl(agreement, authUnit, site, institution, remoteSetupUrl, controller, exportProcessReport).exportRemoteSetupUrl();
		}
		
		//	Remote setup URLs for this site from this agreement
		
		List<RemoteSetupUrl> agreementRemoteSetupUrls = DbRemoteSetupUrl.findBySite(
																		agreement.getId(), 
																		site.getId().getUcn(), 
																		site.getId().getUcnSuffix(), 
																		site.getId().getSiteLocCode(), 
																		null, AppConstants.STATUS_ACTIVE, AppConstants.STATUS_DELETED
																	);
		
		for (RemoteSetupUrl remoteSetupUrl : agreementRemoteSetupUrls) {
			new AuthenticationExportRemoteSetupUrl(agreement, authUnit, site, institution, remoteSetupUrl, controller, exportProcessReport).exportRemoteSetupUrl();
		}
		
		//	Preferences for this site
		
		List<SitePreference> sitePrefs = DbSitePreference.findBySite(site.getId().getUcn(), 
																	site.getId().getUcnSuffix(), 
																	site.getId().getSiteLocCode(), 
																	AppConstants.STATUS_ACTIVE, 
																	AppConstants.STATUS_DELETED);
		for (SitePreference sitePref : sitePrefs) {
			AePrefId aePrefId = new AePrefId();
			aePrefId.setAeId(controller.getAeControl().getAeId());
			aePrefId.setAuId(authUnit.getAuId());
			aePrefId.setPrefCode(sitePref.getId().getPrefCatCode());
			
			AePref aePref = DbAePref.getById(aePrefId);
			
			if (aePref == null) {
				aePref = new AePref();
				
				aePref.setId(aePrefId);
				aePref.setPrefValue(sitePref.getPrefSelCode());
				
				DbAePref.persist(aePref);
				
				exportProcessReport.countSitePref();
			} else {
				//	We should check for differences, to look for and record inconsistencies, but really, why bother?  How would it happen?
			}
		}
	}
}
