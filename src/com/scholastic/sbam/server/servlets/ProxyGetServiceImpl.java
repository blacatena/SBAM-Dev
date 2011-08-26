package com.scholastic.sbam.server.servlets;

import java.util.ArrayList;
import java.util.List;

import com.scholastic.sbam.client.services.ProxyGetService;
import com.scholastic.sbam.server.database.codegen.ProxyIp;
import com.scholastic.sbam.server.database.codegen.Proxy;
import com.scholastic.sbam.server.database.objects.DbProxyIp;
import com.scholastic.sbam.server.database.objects.DbProxy;
import com.scholastic.sbam.server.database.util.HibernateUtil;
import com.scholastic.sbam.server.fastSearch.InstitutionCache.InstitutionCacheConflict;
import com.scholastic.sbam.shared.objects.ProxyIpInstance;
import com.scholastic.sbam.shared.objects.ProxyInstance;
import com.scholastic.sbam.shared.objects.ProxyTuple;
import com.scholastic.sbam.shared.security.SecurityManager;
import com.scholastic.sbam.shared.util.AppConstants;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class ProxyGetServiceImpl extends AuthenticatedServiceServlet implements ProxyGetService {

	@Override
	public ProxyTuple getProxy(int linkId, boolean loadProxyIps) throws IllegalArgumentException {
		
		authenticate("get proxy", SecurityManager.ROLE_QUERY);
		
		HibernateUtil.openSession();
		HibernateUtil.startTransaction();

		ProxyTuple	proxyTuple = null;
		try {
			Proxy	dbInstance = DbProxy.getById(linkId);
			if (dbInstance != null) {
				ProxyInstance proxyInstance = DbProxy.getInstance(dbInstance);
				
				
				if (loadProxyIps)
					proxyTuple = new ProxyTuple(proxyInstance, getProxyIps(proxyInstance));
				else
					proxyTuple = new ProxyTuple(proxyInstance, null);
			}

		} catch (Exception exc) {
			exc.printStackTrace();
		}
		
		HibernateUtil.endTransaction();
		HibernateUtil.closeSession();
		
		return proxyTuple;
	}
	
	public List<ProxyIpInstance> getProxyIps(ProxyInstance proxyInstance) throws InstitutionCacheConflict {

		List<ProxyIpInstance> list = new ArrayList<ProxyIpInstance>();
		
		List<ProxyIp> proxyIps = DbProxyIp.findByProxyId(proxyInstance.getProxyId(), AppConstants.STATUS_ANY_NONE);
		
		for (ProxyIp dbInstance : proxyIps) {
			if (dbInstance != null) {
				ProxyIpInstance proxyIp = DbProxyIp.getInstance(dbInstance);
				list.add(proxyIp);
			}
		}
		
		return list;
	}
}
