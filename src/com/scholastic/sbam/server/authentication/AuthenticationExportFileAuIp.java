package com.scholastic.sbam.server.authentication;

import com.scholastic.sbam.server.database.codegen.AeIp;
import com.scholastic.sbam.server.util.ExportController;
import com.scholastic.sbam.shared.objects.ExportProcessReport;

public class AuthenticationExportFileAuIp extends AuthenticationExportFile {

	public AuthenticationExportFileAuIp(ExportController controller, ExportProcessReport exportReport) {
		super("aeauip.dat", controller, exportReport);
	}

	/**
	 * Write one record for the IP.
	 * @param auIp
	 */
	public void write(AeIp auIp) {
		write(
				getAuId(	auIp.getId().getAuId(),	auIp.getRemote()	), 
				auIp.getId().getIp(), 
				auIp.getRemote()
			);
	}
}
