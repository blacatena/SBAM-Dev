package com.scholastic.sbam.server.util;

import com.scholastic.sbam.server.authentication.AuthenticationConflict;
import com.scholastic.sbam.server.database.codegen.AeControl;

public interface ExportController extends ConsoleOutputter {
	
	public static final	long				REMOTE_AU_ADD	= 1000000;
	public static final char				LEGACY_CUSTOMER_CODES = 'l';
	public static final char				EXPORT_UCNS = 'u';
	public static final String				VALID_UCN_MODES = "" + LEGACY_CUSTOMER_CODES + EXPORT_UCNS;
	
	public AeControl getLastCompleteAeControl();
	
	public AeControl getAeControl();
	
	public void addConflict(AuthenticationConflict conflict);
	
	public String getExportDirectory();
}
