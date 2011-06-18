package com.scholastic.sbam.server.authentication;

import com.scholastic.sbam.server.database.codegen.AePref;
import com.scholastic.sbam.server.util.ExportController;
import com.scholastic.sbam.shared.objects.ExportProcessReport;

public class AuthenticationExportFileAuPref extends AuthenticationExportFile {

	public AuthenticationExportFileAuPref(ExportController controller, ExportProcessReport exportReport) {
		super("aeaupref.dat", controller, exportReport);
	}

	/**
	 * Write one record for the AU pref code and value, and a second for any remote access au.
	 * @param pref
	 */
	public void write(AePref pref) {
		write(pref.getId().getAuId(), pref.getId().getPrefCode(), pref.getPrefValue());
//		Always write an equivalent remote record
		write(getAuId(pref.getId().getAuId(), 'y'), pref.getId().getPrefCode(), pref.getPrefValue());
	}
}
