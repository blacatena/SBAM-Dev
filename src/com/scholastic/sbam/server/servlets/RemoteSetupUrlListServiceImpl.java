package com.scholastic.sbam.server.servlets;

import java.util.ArrayList;
import java.util.List;

import com.scholastic.sbam.client.services.RemoteSetupUrlListService;
import com.scholastic.sbam.server.database.codegen.RemoteSetupUrl;
import com.scholastic.sbam.server.database.objects.DbRemoteSetupUrl;
import com.scholastic.sbam.server.database.util.HibernateUtil;
import com.scholastic.sbam.server.fastSearch.InstitutionCache;
import com.scholastic.sbam.server.fastSearch.InstitutionCache.InstitutionCacheConflict;
import com.scholastic.sbam.shared.objects.RemoteSetupUrlInstance;
import com.scholastic.sbam.shared.security.SecurityManager;
import com.scholastic.sbam.shared.util.AppConstants;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class RemoteSetupUrlListServiceImpl extends AuthenticatedServiceServlet implements RemoteSetupUrlListService {

	@Override
	public List<RemoteSetupUrlInstance> getRemoteSetupUrls(int agreementId, int ucn, int ucnSuffix, String siteLocCode, char neStatus) throws IllegalArgumentException {
		
		authenticate("get remote setup urls", SecurityManager.ROLE_QUERY);
		
		HibernateUtil.openSession();
		HibernateUtil.startTransaction();

		List<RemoteSetupUrlInstance> list = new ArrayList<RemoteSetupUrlInstance>();
		try {
			//	Find only undeleted method types
			List<RemoteSetupUrl> methodInstances;
			if (agreementId > 0) {
				methodInstances = DbRemoteSetupUrl.findByAgreementId(agreementId, AppConstants.STATUS_ANY_NONE, neStatus);
			} else {
				methodInstances = DbRemoteSetupUrl.findBySite(ucn, ucnSuffix, siteLocCode, AppConstants.STATUS_ANY_NONE, neStatus);
			}
			
			for (RemoteSetupUrl methodInstance : methodInstances) {
				list.add(DbRemoteSetupUrl.getInstance(methodInstance));
			}
			
			for (RemoteSetupUrlInstance method : list) {
				setDescriptions(method);
			}

		} catch (Exception exc) {
			exc.printStackTrace();
		}
		
		HibernateUtil.endTransaction();
		HibernateUtil.closeSession();
		
		return list;
	}
	

	
	private void setDescriptions(RemoteSetupUrlInstance remoteSetupUrl) throws InstitutionCacheConflict {
		if (remoteSetupUrl == null)
			return;
		
		DbRemoteSetupUrl.setDescriptions(remoteSetupUrl);
		if (remoteSetupUrl.getSite() != null)
			InstitutionCache.getSingleton().setDescriptions( remoteSetupUrl.getSite().getInstitution() );
	}
}
