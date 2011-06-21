package com.scholastic.sbam.server.authentication;

import com.scholastic.sbam.server.database.codegen.AeUrl;
import com.scholastic.sbam.server.util.ExportController;
import com.scholastic.sbam.shared.objects.ExportProcessReport;

public class AuthenticationExportFileAuUrl extends AuthenticationExportFile {

	public AuthenticationExportFileAuUrl(ExportController controller, ExportProcessReport exportReport) {
		super("aeauurl.dat", controller, exportReport);
	}

	/**
	 * Write one record for the URL.
	 * @param auUrl
	 */
	public void write(AeUrl auUrl) {
		write(
				getAuId(	auUrl.getId().getAuId(),	auUrl.getRemote()	), 
				auUrl.getId().getUrl(),
				auUrl.getRemote()
			);
		exportReport.countUrlWrite();
	}
}
