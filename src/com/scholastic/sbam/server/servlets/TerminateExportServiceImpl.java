package com.scholastic.sbam.server.servlets;

import com.scholastic.sbam.client.services.TerminateExportService;
import com.scholastic.sbam.server.authentication.AuthenticationGenerator;
import com.scholastic.sbam.shared.exceptions.AuthenticationExportException;
import com.scholastic.sbam.shared.objects.Authentication;
import com.scholastic.sbam.shared.objects.ExportProcessReport;
import com.scholastic.sbam.shared.security.SecurityManager;

/**
 * Base class providing methods for any class that must load and attach some or all agreement terms to an agreement instance.
 */
@SuppressWarnings("serial")
public class TerminateExportServiceImpl extends AuthenticatedServiceServlet implements TerminateExportService {
	
	@Override
	public ExportProcessReport terminateExport(String terminationReason) {
		Authentication auth = authenticate("terminate export", SecurityManager.ROLE_ADMIN);
		
		AuthenticationGenerator auGen = AuthenticationGenerator.getInstance();
		if (!auGen.isRunning())
			throw new AuthenticationExportException("No export is currently running.  Cannot terminate.");
		
		auGen.setTerminationRequest(auth.getUserName(), terminationReason);
		return auGen.getCurrentExportReport();
	}
}
