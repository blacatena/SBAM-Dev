package com.scholastic.sbam.server.servlets;

import com.scholastic.sbam.client.services.InstitutionGetService;
import com.scholastic.sbam.server.database.codegen.Institution;
import com.scholastic.sbam.server.database.objects.DbInstitution;
import com.scholastic.sbam.server.database.util.HibernateUtil;
import com.scholastic.sbam.server.fastSearch.InstitutionCache;
import com.scholastic.sbam.shared.objects.InstitutionInstance;
import com.scholastic.sbam.shared.security.SecurityManager;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class InstitutionGetServiceImpl extends AuthenticatedServiceServlet implements InstitutionGetService {

	@Override
	public InstitutionInstance getInstitution(int ucn) throws IllegalArgumentException {
		authenticate("get institution", SecurityManager.ROLE_QUERY);

		if (ucn <= 0)
			throw new IllegalArgumentException("A UCN is required.");
		
		InstitutionInstance result = null;
		
		HibernateUtil.openSession();
		HibernateUtil.startTransaction();
		
		try {
			Institution dbInstance = DbInstitution.getByCode(ucn);
			
			if (dbInstance != null) {
				result = DbInstitution.getInstance(dbInstance);

				result.setTypeDescription(InstitutionCache.getSingleton().getInstitutionType(result.getTypeCode()).getDescription());
				result.setGroupDescription(InstitutionCache.getSingleton().getInstitutionGroup(result.getGroupCode()).getDescription());
				result.setPublicPrivateDescription(InstitutionCache.getSingleton().getInstitutionPubPriv(result.getPublicPrivateCode()).getDescription());
//					result.setAgreementSummaryList(DbAgreement.findAgreementSummaries(instance.getUcn(), false, (char) 0, AppConstants.STATUS_DELETED));
					
			}
			
		} catch (Exception exc) {
			exc.printStackTrace();
		} finally {		
			HibernateUtil.endTransaction();
			HibernateUtil.closeSession();
		}
		
		return result;
	}
}
