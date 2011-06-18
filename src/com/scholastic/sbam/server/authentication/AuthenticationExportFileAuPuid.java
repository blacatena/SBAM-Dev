package com.scholastic.sbam.server.authentication;

import com.scholastic.sbam.server.database.codegen.AePuid;
import com.scholastic.sbam.server.util.ExportController;
import com.scholastic.sbam.shared.objects.ExportProcessReport;

public class AuthenticationExportFileAuPuid extends AuthenticationExportFile {

	public AuthenticationExportFileAuPuid(ExportController controller, ExportProcessReport exportReport) {
		super("aeaupuid.dat", controller, exportReport);
	}

	/**
	 * Write one record for the Proxy UID.
	 * @param auPuid
	 */
	public void write(AePuid auPuid) {
		write(
				getAuId(	auPuid.getId().getAuId(),	auPuid.getRemote()), 
				auPuid.getId().getUserId(), 
				auPuid.getPassword(), 
				auPuid.getRemote(), 
				auPuid.getUserType(), 
				auPuid.getId().getIp()
			);
	}
}
