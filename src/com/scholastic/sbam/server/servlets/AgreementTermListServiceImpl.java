package com.scholastic.sbam.server.servlets;

import java.util.ArrayList;
import java.util.List;

import com.scholastic.sbam.client.services.AgreementTermListService;
import com.scholastic.sbam.server.database.codegen.AgreementTerm;
import com.scholastic.sbam.server.database.objects.DbAgreementTerm;
import com.scholastic.sbam.server.database.util.HibernateUtil;
import com.scholastic.sbam.shared.objects.AgreementTermInstance;
import com.scholastic.sbam.shared.security.SecurityManager;
import com.scholastic.sbam.shared.util.AppConstants;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class AgreementTermListServiceImpl extends AuthenticatedServiceServlet implements AgreementTermListService {

	@Override
	public List<AgreementTermInstance> getAgreementTerms(int agreementId, char neStatus) throws IllegalArgumentException {
		
		authenticate("get agreement terms", SecurityManager.ROLE_QUERY);
		
		HibernateUtil.openSession();
		HibernateUtil.startTransaction();

		List<AgreementTermInstance> list = new ArrayList<AgreementTermInstance>();
		try {
			//	Find only undeleted term types
			List<AgreementTerm> termInstances = DbAgreementTerm.findByAgreementId(agreementId, AppConstants.STATUS_ANY_NONE, neStatus);
			
			for (AgreementTerm termInstance : termInstances) {
				list.add(DbAgreementTerm.getInstance(termInstance));
			}
			
			for (AgreementTermInstance term : list) {
				setDescriptions(term);
			}

		} catch (Exception exc) {
			exc.printStackTrace();
		}
		
		HibernateUtil.endTransaction();
		HibernateUtil.closeSession();
		
		return list;
	}
	
	
	private void setDescriptions(AgreementTermInstance agreementTerm) {
		DbAgreementTerm.setDescriptions(agreementTerm);
	}
}
