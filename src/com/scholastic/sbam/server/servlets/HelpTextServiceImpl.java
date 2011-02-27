package com.scholastic.sbam.server.servlets;

import com.scholastic.sbam.client.services.HelpTextService;
import com.scholastic.sbam.server.database.codegen.HelpText;
import com.scholastic.sbam.server.database.objects.DbHelpText;
import com.scholastic.sbam.server.database.util.HibernateUtil;
import com.scholastic.sbam.shared.objects.HelpTextInstance;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class HelpTextServiceImpl extends AuthenticatedServiceServlet implements HelpTextService {

	@Override
	public HelpTextInstance getHelpText(String id) throws IllegalArgumentException {
		
		authenticate("get help text");	//, SecurityManager.ROLE_CONFIG);
		
		HibernateUtil.openSession();
		HibernateUtil.startTransaction();

		HelpTextInstance result = null;
		try {
			
			HelpText dbInstance = DbHelpText.getByCode(id);
			if (dbInstance != null) {
				result = DbHelpText.getInstance(dbInstance);
			} else {
				result = new HelpTextInstance();
				result.setId(id);
				result.setTitle("Unknown Page " + id);
				result.setText("The page for '<b>" + id + "</b>' could not be found.  Please contact a system administrator, and give them the exact ID reference so that the problem can be corrected.");
			}
		} catch (IllegalArgumentException exc) {
			throw exc;
		} catch (Exception exc) {
			exc.printStackTrace();
		}
		
		HibernateUtil.endTransaction();
		HibernateUtil.closeSession();
		
		return result;
	}
}
