package com.scholastic.sbam.server.authentication;

import java.util.Date;

import com.scholastic.sbam.server.database.codegen.AeControl;
import com.scholastic.sbam.server.database.objects.DbAeControl;
import com.scholastic.sbam.server.database.util.HibernateUtil;
import com.scholastic.sbam.server.util.ExportController;

public class AuthenticationRerunController implements ExportController {
	
	public AeControl aeControl;
	public AeControl lastCompleteAeControl;
	
	public AuthenticationRerunController(int aeId, int lastCompleteAeId) throws Exception {
		loadAeControls(aeId, lastCompleteAeId);
	}
	
	protected void loadAeControls(int aeId, int lastCompleteAeId) throws Exception {
		HibernateUtil.openSession();
		HibernateUtil.startTransaction();
		
		aeControl			  = DbAeControl.getById(aeId);
		lastCompleteAeControl = DbAeControl.getById(lastCompleteAeId);

		
		HibernateUtil.endTransaction();
		HibernateUtil.closeSession();
	}

	@Override
	public void forceConsoleOutput(String message) {
		System.out.println(new Date() + " : " + message);
	}

	@Override
	public void consoleOutput(String message) {
		System.out.println(new Date() + " : " + message);
	}

	@Override
	public AeControl getLastCompleteAeControl() {
		return lastCompleteAeControl;
	}

	@Override
	public AeControl getAeControl() {
		return aeControl;
	}

	@Override
	public void addConflict(AuthenticationConflict conflict) {
		System.out.println(conflict.message);
	}

	@Override
	public String getExportDirectory() {
		return null;
	}

	@Override
	public int getCountIncrement() {
		return 0;
	}

}
