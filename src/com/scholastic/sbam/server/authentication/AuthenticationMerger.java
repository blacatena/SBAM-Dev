package com.scholastic.sbam.server.authentication;

import com.scholastic.sbam.server.util.ExportController;
import com.scholastic.sbam.shared.objects.ExportProcessReport;

public class AuthenticationMerger {
	protected	ExportController	controller;
	protected	ExportProcessReport	exportReport;
	
	public AuthenticationMerger(ExportController controller, ExportProcessReport exportReport) {
		this.controller		= controller;
		this.exportReport	= exportReport;
	}
	
	public void mergeAuthUnits() {
		controller.forceConsoleOutput("Merge equivalent authentication units...");
		/**
		 * TODO
		 * Fill in code.
		 */
	}
}
