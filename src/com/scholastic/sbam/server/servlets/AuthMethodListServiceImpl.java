package com.scholastic.sbam.server.servlets;

import java.util.ArrayList;
import java.util.List;

import com.scholastic.sbam.client.services.AuthMethodListService;
import com.scholastic.sbam.server.database.codegen.AuthMethod;
import com.scholastic.sbam.server.database.objects.DbAuthMethod;
import com.scholastic.sbam.server.database.util.HibernateUtil;
import com.scholastic.sbam.server.fastSearch.InstitutionCache;
import com.scholastic.sbam.server.fastSearch.InstitutionCache.InstitutionCacheConflict;
import com.scholastic.sbam.shared.objects.AuthMethodInstance;
import com.scholastic.sbam.shared.security.SecurityManager;
import com.scholastic.sbam.shared.util.AppConstants;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class AuthMethodListServiceImpl extends AuthenticatedServiceServlet implements AuthMethodListService {

	@Override
	public List<AuthMethodInstance> getAuthMethods(int agreementId, int ucn, int ucnSuffix, String siteLocCode, String methodType, char neStatus) throws IllegalArgumentException {
		
		authenticate("get auth methods", SecurityManager.ROLE_QUERY);
		
		HibernateUtil.openSession();
		HibernateUtil.startTransaction();

		List<AuthMethodInstance> list = new ArrayList<AuthMethodInstance>();
		try {
			//	Find only undeleted method types
			List<AuthMethod> methodInstances;
			if (agreementId > 0) {
				methodInstances = DbAuthMethod.findByAgreementId(agreementId, methodType, AppConstants.STATUS_ANY_NONE, neStatus);
			} else {
				methodInstances = DbAuthMethod.findBySite(ucn, ucnSuffix, siteLocCode, methodType, AppConstants.STATUS_ANY_NONE, neStatus);
			}
			
			for (AuthMethod methodInstance : methodInstances) {
				list.add(DbAuthMethod.getInstance(methodInstance));
			}
			
			for (AuthMethodInstance method : list) {
				setDescriptions(method);
			}

		} catch (Exception exc) {
			exc.printStackTrace();
		}
		
		HibernateUtil.endTransaction();
		HibernateUtil.closeSession();
		
		return list;
	}
	

	
	private void setDescriptions(AuthMethodInstance authMethod) throws InstitutionCacheConflict {
		if (authMethod == null)
			return;
		
		DbAuthMethod.setDescriptions(authMethod);
		if (authMethod.getSite() != null)
			InstitutionCache.getSingleton().setDescriptions( authMethod.getSite().getInstitution() );
	}
}
