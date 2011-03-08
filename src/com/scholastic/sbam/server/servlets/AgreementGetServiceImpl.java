package com.scholastic.sbam.server.servlets;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.scholastic.sbam.client.services.AgreementGetService;
import com.scholastic.sbam.server.database.codegen.Agreement;
import com.scholastic.sbam.server.database.codegen.AgreementTerm;
import com.scholastic.sbam.server.database.codegen.AgreementType;
import com.scholastic.sbam.server.database.codegen.CancelReason;
import com.scholastic.sbam.server.database.codegen.CommissionType;
import com.scholastic.sbam.server.database.codegen.DeleteReason;
import com.scholastic.sbam.server.database.codegen.Product;
import com.scholastic.sbam.server.database.codegen.TermType;
import com.scholastic.sbam.server.database.objects.DbAgreement;
import com.scholastic.sbam.server.database.objects.DbAgreementTerm;
import com.scholastic.sbam.server.database.objects.DbAgreementType;
import com.scholastic.sbam.server.database.objects.DbCancelReason;
import com.scholastic.sbam.server.database.objects.DbCommissionType;
import com.scholastic.sbam.server.database.objects.DbDeleteReason;
import com.scholastic.sbam.server.database.objects.DbProduct;
import com.scholastic.sbam.server.database.objects.DbTermType;
import com.scholastic.sbam.server.database.util.HibernateUtil;
import com.scholastic.sbam.shared.objects.AgreementInstance;
import com.scholastic.sbam.shared.objects.AgreementTermInstance;
import com.scholastic.sbam.shared.util.AppConstants;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class AgreementGetServiceImpl extends AuthenticatedServiceServlet implements AgreementGetService {

	@Override
	public AgreementInstance getAgreement(int agreementId, boolean allTerms) throws IllegalArgumentException {
		
		authenticate("list term types");	//	SecurityManager.ROLE_CONFIG);
		
		HibernateUtil.openSession();
		HibernateUtil.startTransaction();

		AgreementInstance agreement = null;
		try {
			Agreement dbInstance = DbAgreement.getById(agreementId);
			if (dbInstance != null) {
				agreement = DbAgreement.getInstance(dbInstance);
				setDescriptions(agreement);
				
				//	Find only undeleted term types
				List<AgreementTerm> termInstances = DbAgreementTerm.findByAgreementId(agreement.getId(), AppConstants.STATUS_ANY_NONE, AppConstants.STATUS_DELETED);
	
				//	First scan through the instances, to see what date or path to use
				String		chosenPath = null;
				Calendar	chosenDate = null;
				for (AgreementTerm termInstance : termInstances) {

					//	 Determine the most recent path, or the most recent date
						if (chosenPath == null && chosenDate == null && termInstance.getEndDate() != null) {
							chosenDate = Calendar.getInstance();
							chosenDate.setTime(termInstance.getEndDate());
							chosenPath = termInstance.getPrimaryOrgPath();
						} else if (termInstance.getEndDate().after(chosenDate.getTime())) {
							//	If we find something that ends more than a year later than this, throw out anything too old
							chosenDate.setTime(termInstance.getEndDate());
							chosenPath = termInstance.getPrimaryOrgPath();
						}

				}
				
				if (!allTerms) {
					//	Keep either the chosen org path, or the oldest end date less a year
					if (chosenPath != null) {
						chosenDate = null;
					} else if (chosenDate != null) {
						chosenDate.add(Calendar.YEAR, -1);
					}
					

					List<AgreementTermInstance> list = new ArrayList<AgreementTermInstance>();
					for (AgreementTerm termInstance : termInstances) {
						if (allTerms) {
							list.add(DbAgreementTerm.getInstance(termInstance));
						} else if (chosenPath == null && chosenDate == null) {
							list.add(DbAgreementTerm.getInstance(termInstance));
						} else if (chosenPath != null && chosenPath.equals(termInstance.getPrimaryOrgPath())) {
							list.add(DbAgreementTerm.getInstance(termInstance));
						} else if (chosenDate != null && chosenDate.before(termInstance.getEndDate())) {
							list.add(DbAgreementTerm.getInstance(termInstance));
						}
					}
					
					agreement.setAgreementTerms(list);
					for (AgreementTermInstance term : list) {
						setDescriptions(term);
					}
				}
			}

		} catch (Exception exc) {
			exc.printStackTrace();
		}
		
		HibernateUtil.endTransaction();
		HibernateUtil.closeSession();
		
		return agreement;
	}
	
	private void setDescriptions(AgreementInstance agreement) {
		if (agreement == null)
			return;
		
		if (agreement.getAgreementTypeCode() != null) {
			AgreementType aType = DbAgreementType.getByCode(agreement.getAgreementTypeCode());
			if (aType != null)
				agreement.setAgreementTypeDescription(aType.getDescription());
		}
		
		if (agreement.getAgreementTypeCode() != null) {
			CommissionType cType = DbCommissionType.getByCode(agreement.getCommissionCode());
			if (cType != null)
				agreement.setCommissionCodeDescription(cType.getDescription());
		}
		
		if (agreement.getAgreementTypeCode() != null) {
			DeleteReason dType = DbDeleteReason.getByCode(agreement.getDeleteReasonCode());
			if (dType != null)
				agreement.setDeleteReasonDescription(dType.getDescription());
		}
	}
	

	
	private void setDescriptions(AgreementTermInstance agreementTerm) {
		if (agreementTerm == null)
			return;
		
		if (agreementTerm.getTermType() != null) {
			TermType tType = DbTermType.getByCode(agreementTerm.getTermType());
			if (tType != null)
				agreementTerm.setTermTypeDescription(tType.getDescription());
		}
	
		if (agreementTerm.getCommissionCode() != null) {
			CommissionType cType = DbCommissionType.getByCode(agreementTerm.getCommissionCode());
			if (cType != null)
				agreementTerm.setCommissionCodeDescription(cType.getDescription());
		}
		
		if (agreementTerm.getCancelReasonCode() != null) {
			CancelReason cancelReason = DbCancelReason.getByCode(agreementTerm.getCancelReasonCode());
			if (cancelReason != null)
				agreementTerm.setCancelReasonDescription(cancelReason.getDescription());
		}
		if (agreementTerm.getProductCode() != null) {
			Product product = DbProduct.getByCode(agreementTerm.getProductCode());
			if (product != null) {
				agreementTerm.setProductDescription(product.getDescription());
				agreementTerm.setProductShortName(product.getShortName());
			}
		}
	}
}
