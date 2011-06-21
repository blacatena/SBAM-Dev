package com.scholastic.sbam.server.authentication;

import com.scholastic.sbam.server.database.codegen.AePrefCode;
import com.scholastic.sbam.server.util.ExportController;
import com.scholastic.sbam.shared.objects.ExportProcessReport;

public class AuthenticationExportFilePrefCode extends AuthenticationExportFile {

	public AuthenticationExportFilePrefCode(ExportController controller, ExportProcessReport exportReport) {
		super("aeprefcode.dat", controller, exportReport);
	}

	/**
	 * Write one record for each valid pref code.
	 * @param prefCode
	 */
	public void write(AePrefCode prefCode) {
		write(prefCode.getId().getPrefCode(), prefCode.getDescription(), prefCode.getDefaultValue());
		exportReport.countPrefCodeWrite();
	}
}
