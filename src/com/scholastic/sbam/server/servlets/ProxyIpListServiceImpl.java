package com.scholastic.sbam.server.servlets;

import java.util.ArrayList;
import java.util.List;

import com.scholastic.sbam.client.services.ProxyIpListService;
import com.scholastic.sbam.server.database.codegen.ProxyIp;
import com.scholastic.sbam.server.database.objects.DbProxyIp;
import com.scholastic.sbam.server.database.util.HibernateUtil;
import com.scholastic.sbam.shared.objects.ProxyIpInstance;
import com.scholastic.sbam.shared.security.SecurityManager;
import com.scholastic.sbam.shared.util.AppConstants;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class ProxyIpListServiceImpl extends AuthenticatedServiceServlet implements ProxyIpListService {

	@Override
	public List<ProxyIpInstance> getProxyIps(int proxyIp, long loIp, long hiIp, char neStatus) throws IllegalArgumentException {
		
		authenticate("get proxy IPs", SecurityManager.ROLE_QUERY);
		
		HibernateUtil.openSession();
		HibernateUtil.startTransaction();
		
		if (proxyIp == 0 && loIp == 0 && hiIp == 0) {
			throw new IllegalArgumentException("No key values specified to get proxy IP list.");
		}

		List<ProxyIpInstance> list = new ArrayList<ProxyIpInstance>();
		try {
			//	Find only undeleted proxy IPs
			List<ProxyIp> ipInstances = DbProxyIp.findInRange(proxyIp, loIp, hiIp, AppConstants.STATUS_ANY_NONE, neStatus, null, null);	//	loadConfig.getSortField(), loadConfig.getSortDir());
			
			for (ProxyIp termInstance : ipInstances) {
				list.add(DbProxyIp.getInstance(termInstance));
			}

		} catch (Exception exc) {
			exc.printStackTrace();
		}
		
		HibernateUtil.endTransaction();
		HibernateUtil.closeSession();
		
		return list;
	}
}
