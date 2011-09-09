package com.scholastic.sbam.server.servlets;

import java.util.ArrayList;
import java.util.List;

import com.scholastic.sbam.client.services.IpRangeConflictService;
import com.scholastic.sbam.server.database.codegen.AuthMethod;
import com.scholastic.sbam.server.database.codegen.ProxyIp;
import com.scholastic.sbam.server.database.objects.DbAuthMethod;
import com.scholastic.sbam.server.database.objects.DbProxyIp;
import com.scholastic.sbam.server.database.util.HibernateUtil;
import com.scholastic.sbam.shared.objects.AuthMethodInstance;
import com.scholastic.sbam.shared.objects.MethodConflictInstance;
import com.scholastic.sbam.shared.objects.ProxyIpInstance;
import com.scholastic.sbam.shared.security.SecurityManager;

/**
 * This class gets any objects for IP range conflicts (i.e. overlapping IP ranges).
 * 
 */
@SuppressWarnings("serial")
public class IpRangeConflictServiceImpl extends ConflictServiceServletBase implements IpRangeConflictService {

	/**
	 * Perform backend validation on a field value, given a previous data instance.
	 * 
	 * The 
	 * @param ipLo
	 * The low IP value.
	 * @param ipHi
	 * The high IP value.
	 * @return
	 * A list of IP methods.
	 * @throws Exception
	 */
	public List<MethodConflictInstance> getIpRangeConflicts(long ipLo, long ipHi) throws Exception {

		authenticate("get IP conflicts", SecurityManager.ROLE_QUERY);
		
		HibernateUtil.openSession();
		HibernateUtil.startTransaction();

		List<MethodConflictInstance> response = new ArrayList<MethodConflictInstance>();
		try {
			
			authenticate();
			
			List<AuthMethod> authMethods  = DbAuthMethod.findOverlapIps(ipLo, ipHi);
			if (authMethods != null) {
				for (AuthMethod authMethod : authMethods) {
						AuthMethodInstance amInstance = DbAuthMethod.getInstance(authMethod);
						response.add(getMethodConflictInstance(amInstance));
					}
			}
			
			List<ProxyIp> proxyIps  = DbProxyIp.findOverlapIps(ipLo, ipHi);
			
			if (proxyIps != null) {
				for (ProxyIp proxyIp : proxyIps) {
					ProxyIpInstance rsInstance = DbProxyIp.getInstance(proxyIp);
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
