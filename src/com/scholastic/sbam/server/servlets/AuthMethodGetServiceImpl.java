package com.scholastic.sbam.server.servlets;

import com.scholastic.sbam.client.services.AuthMethodGetService;
import com.scholastic.sbam.server.database.codegen.Agreement;
import com.scholastic.sbam.server.database.codegen.AuthMethod;
import com.scholastic.sbam.server.database.codegen.Institution;
import com.scholastic.sbam.server.database.codegen.Site;
import com.scholastic.sbam.server.database.objects.DbAgreement;
import com.scholastic.sbam.server.database.objects.DbAuthMethod;
import com.scholastic.sbam.server.database.objects.DbInstitution;
import com.scholastic.sbam.server.database.objects.DbSite;
import com.scholastic.sbam.server.database.util.HibernateUtil;
import com.scholastic.sbam.server.fastSearch.InstitutionCache;
import com.scholastic.sbam.server.validation.AppIpConflictDetector;
import com.scholastic.sbam.server.validation.AppUidConflictDetector;
import com.scholastic.sbam.server.validation.AppUrlConflictDetector;
import com.scholastic.sbam.shared.objects.AgreementInstance;
import com.scholastic.sbam.shared.objects.AuthMethodInstance;
import com.scholastic.sbam.shared.objects.AuthMethodTuple;
import com.scholastic.sbam.shared.objects.SiteInstance;
import com.scholastic.sbam.shared.security.SecurityManager;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class AuthMethodGetServiceImpl extends AgreementGetServiceBase implements AuthMethodGetService {

	@Override
	public AuthMethodTuple getAuthMethod(int agreementId, int ucn, int ucnSuffix, String siteLocCode, String methodType, int methodKey, boolean loadTerms, boolean allTerms, boolean conflicts) throws IllegalArgumentException {
		
		authenticate("get authentication method", SecurityManager.ROLE_QUERY);
		
		HibernateUtil.openSession();
		HibernateUtil.startTransaction();

		SiteInstance		site			=	null;
		AgreementInstance	agreement		= null;
		AuthMethodInstance	authMethod		= null;
		AuthMethodTuple		authMethodTuple	= null;
		try {
			
			AuthMethod	dbAuthMethod = DbAuthMethod.getById(agreementId, ucn, ucnSuffix, siteLocCode, methodType, methodKey);
		
			if (dbAuthMethod != null) {
				
				authMethod = DbAuthMethod.getInstance(dbAuthMethod);
				setDescriptions(authMethod);
				
				if (agreementId > 0) {
					Agreement	dbAgreement = DbAgreement.getById(agreementId);
					if (dbAgreement != null) {
						agreement = DbAgreement.getInstance(dbAgreement);
						setDescriptions(agreement);
						
						// Get the institution
						if (agreement.getBillUcn() > 0) {
							Institution dbInstitution = DbInstitution.getByCode(agreement.getBillUcn());
							agreement.setInstitution(DbInstitution.getInstance(dbInstitution));
		
							if (agreement.getInstitution() != null) {
								InstitutionCache.getSingleton().setDescriptions( agreement.getInstitution() );
							}
						}
					}
						
					authMethodTuple = new AuthMethodTuple(agreement, authMethod);
					
					if (loadTerms)
						loadAgreementTerms(agreement, allTerms);
					if (conflicts)
						includeConflicts(authMethodTuple);
				} else if (ucn > 0) {
					Site	dbSite = DbSite.getById(ucn, ucnSuffix, siteLocCode);
					if (dbSite != null) {
						site = DbSite.getInstance(dbSite);
						setDescriptions(site);
					}
						
					authMethodTuple = new AuthMethodTuple(agreement, site);
				}
			}

		} catch (Exception exc) {
			exc.printStackTrace();
		}
		
		HibernateUtil.endTransaction();
		HibernateUtil.closeSession();
		
		return authMethodTuple;
	}
	
	public void setDescriptions(AuthMethodInstance authMethod) {
		DbAuthMethod.setDescriptions(authMethod);
	}
	
	public void setDescriptions(SiteInstance site) {
		DbSite.setDescriptions(site);
	}
	
	protected void includeConflicts(AuthMethodTuple tuple) {
		AuthMethodInstance method = tuple.getAuthMethod();
		
		if (method.methodIsIpAddress()) {
			AppIpConflictDetector detector = new AppIpConflictDetector(method.getIpLo(), method.getIpHi(), method.obtainMethodId());
			detector.doValidation();
			tuple.setConflicts(detector.getResponse());
			return;
		}
		
		if (method.methodIsUserId()) {
			AppUidConflictDetector detector = new AppUidConflictDetector(method.getUserId(), method.getPassword(), method.getUserType(), method.getProxyId(), method.obtainMethodId());
			detector.doValidation();
			tuple.setConflicts(detector.getResponse());
			return;
		}
		
		if (method.methodIsUrl()) {
			AppUrlConflictDetector detector = new AppUrlConflictDetector(method.getUrl(), method.obtainMethodId());
			detector.doValidation();
			tuple.setConflicts(detector.getResponse());
			return;
		}
		
		throw new IllegalArgumentException("Invalid Authentication Method type " + method.getMethodType() + " for " + method.getUniqueKey() + ".");
	}
}
