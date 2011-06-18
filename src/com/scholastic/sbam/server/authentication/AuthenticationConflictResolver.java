package com.scholastic.sbam.server.authentication;

import com.scholastic.sbam.server.util.ExportController;
import com.scholastic.sbam.shared.objects.ExportProcessReport;

public class AuthenticationConflictResolver {
	
	protected ExportController		controller;
	protected ExportProcessReport	exportReport;
	
	public AuthenticationConflictResolver(ExportController controller, ExportProcessReport exportReport) {
		this.controller		= controller;
		this.exportReport	= exportReport;
	}

	public void resolveConflicts() {
		controller.forceConsoleOutput("Resolving conflicts...");
		resolveIpConflicts();
		resolveUidConflicts();
		resolvePuidConflicts();
		resolveUrlConflict();
	}

	private void resolveUrlConflict() {
		// TODO Auto-generated method stub
		
	}

	private void resolvePuidConflicts() {
		// TODO Auto-generated method stub
		
	}

	private void resolveUidConflicts() {
		// TODO Auto-generated method stub
		
	}

	private void resolveIpConflicts() {
		// TODO Auto-generated method stub
		
	}
}
