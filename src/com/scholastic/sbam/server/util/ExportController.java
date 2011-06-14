package com.scholastic.sbam.server.util;

import com.scholastic.sbam.server.authentication.AuthenticationConflict;
import com.scholastic.sbam.server.database.codegen.AeControl;

public interface ExportController extends ConsoleOutputter {
	public AeControl getLastCompleteAeControl();
	
	public AeControl getAeControl();
	
	public void addConflict(AuthenticationConflict conflict);
}
