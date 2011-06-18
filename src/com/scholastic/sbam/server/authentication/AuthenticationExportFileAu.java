package com.scholastic.sbam.server.authentication;

import com.scholastic.sbam.server.database.codegen.AeAu;
import com.scholastic.sbam.server.util.ExportController;
import com.scholastic.sbam.shared.objects.ExportProcessReport;

public class AuthenticationExportFileAu extends AuthenticationExportFile {

	public AuthenticationExportFileAu(ExportController controller, ExportProcessReport exportReport) {
		super("aeau.dat", controller, exportReport);
	}

	/**
	 * Write one record for the AU, and a second for any remote access.
	 * @param au
	 */
	public void write(AeAu au) {
		write(au.getId().getAuId(), au.getSiteParentCode(), au.getBillCode(), au.getSiteCode(), au.getSiteLocCode());
		//	Always write a remote record
		write(getAuId(au.getId().getAuId(), 'y'), au.getSiteParentCode(), au.getBillCode(), au.getSiteCode(), au.getSiteLocCode());
	}
}
