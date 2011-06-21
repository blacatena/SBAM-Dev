package com.scholastic.sbam.server.authentication;

import com.scholastic.sbam.server.database.codegen.Institution;
import com.scholastic.sbam.server.database.codegen.StatsAdmin;
import com.scholastic.sbam.server.util.ExportController;
import com.scholastic.sbam.shared.objects.ExportProcessReport;

public class AuthenticationExportFileCst extends AuthenticationExportFile {

	public AuthenticationExportFileCst(ExportController controller, ExportProcessReport exportReport) {
		super("aecst.dat", controller, exportReport);
	}

	/**
	 * Write one record for each valid customer institution.
	 * @param code
	 * @param institution
	 * @param statsAdmin
	 */
	public void write(String code, Institution institution, StatsAdmin statsAdmin) {
		write(
				code,
				institution.getInstitutionName(),
				institution.getAddress1(),
				institution.getAddress2(),
				institution.getCity(),
				institution.getState(),
				institution.getZip(),
				statsAdmin == null ? "" : statsAdmin.getAdminUid(),
				statsAdmin == null ? "" : statsAdmin.getAdminPassword(),
				statsAdmin == null ? "" : statsAdmin.getStatsGroup()
			);
		exportReport.countCstWrite();
	}
}
