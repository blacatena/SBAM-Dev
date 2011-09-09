package com.scholastic.sbam.server.servlets;

import com.scholastic.sbam.server.database.codegen.Agreement;
import com.scholastic.sbam.server.database.codegen.Institution;
import com.scholastic.sbam.server.database.codegen.Proxy;
import com.scholastic.sbam.server.database.codegen.Site;
import com.scholastic.sbam.server.database.objects.DbAgreement;
import com.scholastic.sbam.server.database.objects.DbAuthMethod;
import com.scholastic.sbam.server.database.objects.DbInstitution;
import com.scholastic.sbam.server.database.objects.DbProxy;
import com.scholastic.sbam.server.database.objects.DbSite;
import com.scholastic.sbam.server.fastSearch.InstitutionCache;
import com.scholastic.sbam.server.fastSearch.InstitutionCache.InstitutionCacheConflict;
import com.scholastic.sbam.shared.objects.AuthMethodInstance;
import com.scholastic.sbam.shared.objects.InstitutionInstance;
import com.scholastic.sbam.shared.objects.MethodConflictInstance;
import com.scholastic.sbam.shared.objects.ProxyInstance;
import com.scholastic.sbam.shared.objects.ProxyIpInstance;
import com.scholastic.sbam.shared.objects.RemoteSetupUrlInstance;
import com.scholastic.sbam.shared.objects.SiteInstance;


/**
 * A base class to provide core functionality needed for all method conflict services.
 * 
 */
@SuppressWarnings("serial")
public class ConflictServiceServletBase extends AuthenticatedServiceServlet {
	
	public MethodConflictInstance getMethodConflictInstance(AuthMethodInstance amInstance) throws InstitutionCacheConflict {
		MethodConflictInstance mcInstance = new MethodConflictInstance(amInstance);
		load(mcInstance);
		return mcInstance;
	}
	
	public MethodConflictInstance getMethodConflictInstance(ProxyIpInstance ipInstance) throws InstitutionCacheConflict {
		MethodConflictInstance mcInstance = new MethodConflictInstance(ipInstance);
		load(mcInstance);
		return mcInstance;
	}
	
	public MethodConflictInstance getMethodConflictInstance(RemoteSetupUrlInstance rsUrlInstance) throws InstitutionCacheConflict {
		MethodConflictInstance mcInstance = new MethodConflictInstance(rsUrlInstance);
		load(mcInstance);
		return mcInstance;
	}
	
	public void load(MethodConflictInstance methodConflict) throws InstitutionCacheConflict {
		if (methodConflict.getAuthMethod() != null) {
			DbAuthMethod.setDescriptions(methodConflict.getAuthMethod());
			methodConflict.setForSite(methodConflict.getAuthMethod().getSite());
			loadAgreement(methodConflict, methodConflict.getAuthMethod().getAgreementId());
			loadOwningSite(methodConflict, methodConflict.getAuthMethod().getUcn(), methodConflict.getAuthMethod().getUcnSuffix(), methodConflict.getAuthMethod().getSiteLocCode());
			if (methodConflict.getAuthMethod().getForUcn() > 0)
				loadForSite(methodConflict, methodConflict.getAuthMethod().getForUcn(), methodConflict.getAuthMethod().getForUcnSuffix(), methodConflict.getAuthMethod().getForSiteLocCode());
			else
				methodConflict.setForSite(methodConflict.getOwningSite());
		}
		if (methodConflict.getProxyIp() != null) {
			loadProxy(methodConflict, methodConflict.getProxyIp().getProxyId());
		}
		if (methodConflict.getRemoteSetupUrl() != null) {
			loadOwningSite(methodConflict, methodConflict.getAuthMethod().getUcn(), methodConflict.getAuthMethod().getUcnSuffix(), methodConflict.getAuthMethod().getSiteLocCode());
			methodConflict.setForSite(methodConflict.getOwningSite());
		}
	}
	
	protected void loadProxy(MethodConflictInstance methodConflict, int proxyId) {
		if (proxyId > 0) {
			Proxy proxy = DbProxy.getById(proxyId);
			if (proxy != null)
				methodConflict.setProxy(DbProxy.getInstance(proxy));
			else
				methodConflict.setProxy(ProxyInstance.getUnknownInstance(proxyId));
		} else
			methodConflict.setProxy(ProxyInstance.getEmptyInstance());
	}

	protected void loadOwningSite(MethodConflictInstance methodConflict, int ucn, int ucnSuffix, String siteLocCode) {
		if (ucnSuffix <= 0)
			ucnSuffix = 1;
		
		if (ucn > 0) {
			if (siteLocCode != null && siteLocCode.length() > 0) {
				Site dbSite = DbSite.getById(ucn, ucnSuffix, siteLocCode);
				if (dbSite != null)
					methodConflict.setOwningSite( DbSite.getInstance(dbSite) );
				else
					methodConflict.setOwningSite( SiteInstance.getUnknownInstance( ucn, ucnSuffix, siteLocCode) );
			} else {
				methodConflict.setOwningSite( SiteInstance.getAllInstance(ucn, ucnSuffix) );
			}
			Institution dbInstitution = DbInstitution.getByCode(ucn);
			if (dbInstitution != null)
				methodConflict.getOwningSite().setInstitution( DbInstitution.getInstance( dbInstitution));
			else
				methodConflict.getOwningSite().setInstitution( InstitutionInstance.getEmptyInstance() );
		} else {
			methodConflict.setOwningSite( SiteInstance.getEmptyInstance());
		}
	}

	protected void loadForSite(MethodConflictInstance methodConflict, int ucn, int ucnSuffix, String siteLocCode) {
		if (ucnSuffix <= 0)
			ucnSuffix = 1;
		
		if (ucn > 0) {
			if (siteLocCode != null && siteLocCode.length() > 0) {
				Site dbSite = DbSite.getById(ucn, ucnSuffix, siteLocCode);
				if (dbSite != null)
					methodConflict.setForSite( DbSite.getInstance(dbSite) );
				else
					methodConflict.setForSite( SiteInstance.getUnknownInstance( ucn, ucnSuffix, siteLocCode) );
			} else {
				methodConflict.setForSite( SiteInstance.getAllInstance(ucn, ucnSuffix) );
			}
			Institution dbInstitution = DbInstitution.getByCode(ucn);
			if (dbInstitution != null)
				methodConflict.getForSite().setInstitution( DbInstitution.getInstance( dbInstitution));
			else
				methodConflict.getForSite().setInstitution( InstitutionInstance.getEmptyInstance() );
		} else {
			methodConflict.setForSite( SiteInstance.getEmptyInstance());
		}
	}

	protected void loadAgreement(MethodConflictInstance methodConflict, int agreementId) throws InstitutionCacheConflict {
		if (agreementId <= 0)
			return;
		Agreement agreement = DbAgreement.getById(agreementId);
		methodConflict.setAgreement(DbAgreement.getInstance(agreement));
		DbAgreement.setDescriptions(methodConflict.getAgreement());

		
		// Get the institution
		if (agreement.getBillUcn() > 0) {
			Institution dbInstitution = DbInstitution.getByCode(agreement.getBillUcn());
			methodConflict.getAgreement().setInstitution(DbInstitution.getInstance(dbInstitution));

			if (methodConflict.getAgreement().getInstitution() != null) {
				InstitutionCache.getSingleton().setDescriptions( methodConflict.getAgreement().getInstitution() );
			}
		}
	}
}
