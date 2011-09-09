package com.scholastic.sbam.server.servlets;

import java.util.ArrayList;
import java.util.List;

import com.scholastic.sbam.client.services.UrlConflictService;
import com.scholastic.sbam.server.database.codegen.AuthMethod;
import com.scholastic.sbam.server.database.codegen.RemoteSetupUrl;
import com.scholastic.sbam.server.database.objects.DbAuthMethod;
import com.scholastic.sbam.server.database.objects.DbRemoteSetupUrl;
import com.scholastic.sbam.server.database.util.HibernateUtil;
import com.scholastic.sbam.shared.objects.AuthMethodInstance;
import com.scholastic.sbam.shared.objects.MethodConflictInstance;
import com.scholastic.sbam.shared.objects.RemoteSetupUrlInstance;
import com.scholastic.sbam.shared.security.SecurityManager;

/**
 * This class get any objects for URL conflicts.
 * 
 */
@SuppressWarnings("serial")
public class UrlConflictServiceImpl extends ConflictServiceServletBase implements UrlConflictService {

	/**
	 * Perform backend validation on a field value, given a previous data instance.
	 * 
	 * The 
	 * @param url
	 * The URL.
	 * @return
	 * A list of URL methods.
	 * @throws Exception
	 */
	public List<MethodConflictInstance> getUrlConflicts(String url) throws Exception {

		authenticate("get URL conflicts", SecurityManager.ROLE_QUERY);
		
		HibernateUtil.openSession();
		HibernateUtil.startTransaction();

		List<MethodConflictInstance> response = new ArrayList<MethodConflictInstance>();
		try {
			
			authenticate();
			
			List<AuthMethod> authMethods  = DbAuthMethod.findByUrl(url);
			if (authMethods != null) {
				for (AuthMethod authMethod : authMethods) {
					AuthMethodInstance amInstance = DbAuthMethod.getInstance(authMethod);
						response.add(getMethodConflictInstance(amInstance));
					}
			}
			
			List<RemoteSetupUrl> remoteSetupUrls  = DbRemoteSetupUrl.findByUrl(url);
			
			if (remoteSetupUrls != null) {
				for (RemoteSetupUrl remoteSetupUrl : remoteSetupUrls) {
					RemoteSetupUrlInstance rsInstance = DbRemoteSetupUrl.getInstance(remoteSetupUrl);
					response.add(getMethodConflictInstance(rsInstance));
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
