package com.scholastic.sbam.server.authentication;

import java.util.HashMap;

import com.scholastic.sbam.server.database.codegen.AeAuthUnit;
import com.scholastic.sbam.server.database.codegen.AeRsurl;
import com.scholastic.sbam.server.database.codegen.AeRsurlId;
import com.scholastic.sbam.server.database.codegen.Agreement;
import com.scholastic.sbam.server.database.codegen.RemoteSetupUrl;
import com.scholastic.sbam.server.database.codegen.Institution;
import com.scholastic.sbam.server.database.codegen.ProductService;
import com.scholastic.sbam.server.database.codegen.Site;
import com.scholastic.sbam.server.database.objects.DbAeRsurl;
import com.scholastic.sbam.server.util.ExportController;
import com.scholastic.sbam.shared.objects.ExportProcessReport;

/**
 * This class represents a site in the Authentication Export process.
 * @author Bob Lacatena
 *
 */
public class AuthenticationExportRemoteSetupUrl {
	protected ExportProcessReport				exportReport;
	protected ExportController					controller;
	
	protected	Agreement						agreement;
	protected	HashMap<String, ProductService>	productServices;
	protected	Site							site;
	protected	Institution						institution;
	protected	RemoteSetupUrl					remoteSetupUrl;
	
	protected	AeAuthUnit						authUnit;
	
	public AuthenticationExportRemoteSetupUrl(Agreement agreement, AeAuthUnit authUnit, Site site, Institution	institution, RemoteSetupUrl remoteSetupUrl, ExportController controller, ExportProcessReport exportProcessReport) {
		this.exportReport		=	exportProcessReport;
		this.controller			=	controller;
		this.agreement			=	agreement;
		this.authUnit			=	authUnit;
		this.site				=	site;
		this.institution		=	institution;
		this.remoteSetupUrl		=	remoteSetupUrl;
	}
	
	public void exportRemoteSetupUrl() {
		if (remoteSetupUrl.getApproved() != 'y' && remoteSetupUrl.getApproved() != 'Y')
			return;
		
		exportReport.countRsUrl();
		
		AeRsurlId urlId = new AeRsurlId();	
		
		urlId.setAeId(controller.getAeControl().getAeId());
		urlId.setAuId(authUnit.getAuId());
		urlId.setUrl(remoteSetupUrl.getUrl());
		
		AeRsurl url = DbAeRsurl.getById(urlId);
		
		if (url == null) {
			url = new AeRsurl();
		
			url.setId(urlId);
			
			DbAeRsurl.persist(url);
		} else {
			exportReport.countRsUrlDuplicate();
		}
	}
}
