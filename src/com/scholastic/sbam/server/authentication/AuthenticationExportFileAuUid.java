package com.scholastic.sbam.server.authentication;

import com.scholastic.sbam.server.database.codegen.AeUid;
import com.scholastic.sbam.server.util.ExportController;
import com.scholastic.sbam.shared.objects.ExportProcessReport;

public class AuthenticationExportFileAuUid extends AuthenticationExportFile {

	public AuthenticationExportFileAuUid(ExportController controller, ExportProcessReport exportReport) {
		super("aeauuid.dat", controller, exportReport);
	}

	/**
	 * Write one record for the user ID.
	 * @param auUid
	 */
	public void write(AeUid auUid) {
		write(
				getAuId(	auUid.getId().getAuId(),	auUid.getRemote()	), 
				auUid.getId().getUserId(), 
				auUid.getPassword(), 
				auUid.getRemote(), 
				auUid.getUserType()
			);
		exportReport.countUidWrite();
	}
}
