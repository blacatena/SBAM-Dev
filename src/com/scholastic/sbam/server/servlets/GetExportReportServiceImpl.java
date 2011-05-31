package com.scholastic.sbam.server.servlets;

import com.scholastic.sbam.client.services.GetExportReportService;
import com.scholastic.sbam.server.authentication.AuthenticationGenerator;
import com.scholastic.sbam.shared.objects.ExportProcessReport;
import com.scholastic.sbam.shared.security.SecurityManager;

/**
 * Base class providing methods for any class that must load and attach some or all agreement terms to an agreement instance.
 */
@SuppressWarnings("serial")
public class GetExportReportServiceImpl extends AuthenticatedServiceServlet implements GetExportReportService {
	
	@Override
	public ExportProcessReport getExportReport() {
		authenticate("get export report", SecurityManager.ROLE_ADMIN);
		
		AuthenticationGenerator auGen = AuthenticationGenerator.getInstance();
		
		return auGen.getCurrentExportReport();
	}
}
