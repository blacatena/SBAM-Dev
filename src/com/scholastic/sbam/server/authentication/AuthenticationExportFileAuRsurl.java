package com.scholastic.sbam.server.authentication;

import com.scholastic.sbam.server.database.codegen.AeRsurl;
import com.scholastic.sbam.server.util.ExportController;
import com.scholastic.sbam.shared.objects.ExportProcessReport;

public class AuthenticationExportFileAuRsurl extends AuthenticationExportFile {

	public AuthenticationExportFileAuRsurl(ExportController controller, ExportProcessReport exportReport) {
		super("aeaursurl.dat", controller, exportReport);
	}

	/**
	 * Write one record for Remote Setup URL.
	 * @param auRsurl
	 */
	public void write(AeRsurl auRsurl) {
		write(
				auRsurl.getId().getAuId(),	//	For remote setup, this can only be the main, not remote, URL
				auRsurl.getId().getUrl()
			);
	}
}
