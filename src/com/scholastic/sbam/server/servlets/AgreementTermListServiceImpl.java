package com.scholastic.sbam.server.servlets;

import java.util.ArrayList;
import java.util.List;

import com.scholastic.sbam.client.services.AgreementTermListService;
import com.scholastic.sbam.server.database.codegen.AgreementTerm;
import com.scholastic.sbam.server.database.codegen.CancelReason;
import com.scholastic.sbam.server.database.codegen.CommissionType;
import com.scholastic.sbam.server.database.codegen.Product;
import com.scholastic.sbam.server.database.codegen.TermType;
import com.scholastic.sbam.server.database.objects.DbAgreementTerm;
import com.scholastic.sbam.server.database.objects.DbCancelReason;
import com.scholastic.sbam.server.database.objects.DbCommissionType;
import com.scholastic.sbam.server.database.objects.DbProduct;
import com.scholastic.sbam.server.database.objects.DbTermType;
import com.scholastic.sbam.server.database.util.HibernateUtil;
import com.scholastic.sbam.shared.objects.AgreementTermInstance;
import com.scholastic.sbam.shared.objects.CancelReasonInstance;
import com.scholastic.sbam.shared.objects.TermTypeInstance;
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
		if (agreementTerm == null)
			return;
		
		if (agreementTerm.getTermTypeCode() != null) {
			TermType tType = DbTermType.getByCode(agreementTerm.getTermTypeCode());
			if (tType != null) {
				agreementTerm.setTermType(DbTermType.getInstance(tType));
			} else {
				agreementTerm.setTermType(TermTypeInstance.getUnknownInstance(agreementTerm.getTermTypeCode()));
			}
		} else
			agreementTerm.setTermType(TermTypeInstance.getUnknownInstance("none"));
		
	
		if (agreementTerm.getCommissionCode() != null) {
			CommissionType cType = DbCommissionType.getByCode(agreementTerm.getCommissionCode());
			if (cType != null)
				agreementTerm.setCommissionCodeDescription(cType.getDescription());
		}
		
		
		if (agreementTerm.getCancelReasonCode() != null && agreementTerm.getCancelReasonCode().length() > 0) {
			CancelReason cancelReason = DbCancelReason.getByCode(agreementTerm.getCancelReasonCode());
			if (cancelReason != null) {
				agreementTerm.setCancelReason(DbCancelReason.getInstance(cancelReason));
			} else {
				agreementTerm.setCancelReason(CancelReasonInstance.getUnknownInstance(agreementTerm.getCancelReasonCode()));
			}
		} else
			agreementTerm.setCancelReason(CancelReasonInstance.getEmptyInstance());
			
		
		if (agreementTerm.getProductCode() != null) {
			Product product = DbProduct.getByCode(agreementTerm.getProductCode());
			if (product != null) {
				agreementTerm.setProductDescription(product.getDescription());
				agreementTerm.setProductShortName(product.getShortName());
			}
		}
		
	}
}
