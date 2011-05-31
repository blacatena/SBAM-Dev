package com.scholastic.sbam.server.servlets;

import com.scholastic.sbam.client.services.InitiateExportService;
import com.scholastic.sbam.server.authentication.AuthenticationGenerator;
import com.scholastic.sbam.shared.objects.ExportProcessReport;
import com.scholastic.sbam.shared.security.SecurityManager;

/**
 * Base class providing methods for any class that must load and attach some or all agreement terms to an agreement instance.
 */
@SuppressWarnings("serial")
public class InitiateExportServiceImpl extends AuthenticatedServiceServlet implements InitiateExportService {
	
	@Override
	public ExportProcessReport initiateExport() {
		authenticate("initiate export", SecurityManager.ROLE_ADMIN);
		
		AuthenticationGenerator auGen = AuthenticationGenerator.getInstance();
		
		if (auGen.beginThreadedExport())
			return auGen.getCurrentExportReport();
		else {
			auGen.getCurrentExportReport().addMessage("Request to begin a new export ignored.");
			throw new IllegalArgumentException("An export is already currently executing.");
		}
	}
}
