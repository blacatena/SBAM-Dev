package com.scholastic.sbam.server.servlets;

import java.util.ArrayList;
import java.util.List;

import com.scholastic.sbam.client.services.UidConflictService;
import com.scholastic.sbam.server.database.codegen.AuthMethod;
import com.scholastic.sbam.server.database.objects.DbAuthMethod;
import com.scholastic.sbam.server.database.util.HibernateUtil;
import com.scholastic.sbam.shared.objects.AuthMethodInstance;
import com.scholastic.sbam.shared.objects.MethodConflictInstance;
import com.scholastic.sbam.shared.security.SecurityManager;

/**
 * This class gets any objects for UID conflicts.
 * 
 */
@SuppressWarnings("serial")
public class UidConflictServiceImpl extends ConflictServiceServletBase implements UidConflictService {

	/**
	 * Perform backend validation on a field value, given a previous data instance.
	 * 
	 * The 
	 * @param uid
	 * The user ID.
	 * @return
	 * A list of UID methods.
	 * @throws Exception
	 */
	public List<MethodConflictInstance> getUidConflicts(String url) throws Exception {

		authenticate("get UID conflicts", SecurityManager.ROLE_QUERY);
		
		HibernateUtil.openSession();
		HibernateUtil.startTransaction();

		List<MethodConflictInstance> response = new ArrayList<MethodConflictInstance>();
		try {
			
			authenticate();
			
			List<AuthMethod> authMethods  = DbAuthMethod.findByUid(url);
			if (authMethods != null) {
				for (AuthMethod authMethod : authMethods) {
					AuthMethodInstance amInstance = DbAuthMethod.getInstance(authMethod);
						response.add(getMethodConflictInstance(amInstance));
					}
			}

		} catch (Exception exc) {
			exc.printStackTrace();
			throw exc;
		}
		
		HibernateUtil.endTransaction();
		HibernateUtil.closeSession();
		
		return response;
	}
}
