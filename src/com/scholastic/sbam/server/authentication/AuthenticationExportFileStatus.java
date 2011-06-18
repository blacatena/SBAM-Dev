package com.scholastic.sbam.server.authentication;

import java.util.Date;

import com.scholastic.sbam.server.util.ExportController;
import com.scholastic.sbam.shared.objects.ExportProcessReport;

public class AuthenticationExportFileStatus extends AuthenticationExportFile {

	public AuthenticationExportFileStatus(ExportController controller, ExportProcessReport exportReport) {
		super("aestatus.dat", controller, exportReport);
	}

	/**
	 * Write a record the status of the entire export.
	 * @param au
	 */
	public void write(String status, String segment) {
		write(
				status,
				segment,
				new Date()
			);
	}
}
