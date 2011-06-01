package com.scholastic.sbam.server.authentication;

import com.scholastic.sbam.server.database.codegen.Agreement;
import com.scholastic.sbam.server.database.codegen.AuthMethod;
import com.scholastic.sbam.server.database.codegen.Site;
import com.scholastic.sbam.server.util.ConsoleOutputter;
import com.scholastic.sbam.shared.objects.AuthMethodInstance;
import com.scholastic.sbam.shared.objects.ExportProcessReport;

/**
 * This class represents a site in the Authentication Export process.
 * @author Bob Lacatena
 *
 */
public class AuthenticationExportMethod {
	protected ExportProcessReport	exportReport;
	protected ConsoleOutputter		output;
	
	protected	Agreement	agreement;
	protected	Site		site;
	protected	AuthMethod	authMethod;
	
	public AuthenticationExportMethod(Agreement agreement, Site site, AuthMethod authMethod, ConsoleOutputter output, ExportProcessReport exportProcessReport) {
		this.exportReport	=	exportProcessReport;
		this.output			=	output;
		this.agreement		=	agreement;
		this.site			=	site;
		this.authMethod		=	authMethod;
	}
	
	public void exportMethod() {
		if (AuthMethodInstance.AM_IP.equals(authMethod.getId().getMethodType()))
			exportReport.countIp();
		else if (AuthMethodInstance.AM_UID.equals(authMethod.getId().getMethodType()))
			exportReport.countUid();
		else if (AuthMethodInstance.AM_URL.equals(authMethod.getId().getMethodType()))
			exportReport.countUrl();
	}
}
