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
import com.scholastic.sbam.shared.objects.CancelReasonInstance;
import com.scholastic.sbam.shared.objects.CommissionTypeInstance;
import com.scholastic.sbam.shared.objects.ProductInstance;
import com.scholastic.sbam.shared.objects.TermTypeInstance;
import com.scholastic.sbam.shared.security.SecurityManager;
import com.scholastic.sbam.shared.util.AppConstants;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class AgreementGetServiceImpl extends AuthenticatedServiceServlet implements AgreementGetService {

	@Override
	public AgreementInstance getAgreement(int agreementId, boolean allTerms) throws IllegalArgumentException {
		
		authenticate("get agreement", SecurityManager.ROLE_QUERY);
		
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
				
				//	Keep either the chosen org path, or the oldest end date less a year
				if (chosenPath != null && chosenPath.length() > 0) {
					chosenDate = null;	// Ignore the date, and pick anything in the same path as the latest end date
				} else if (chosenDate != null) {
					chosenPath = null;
					chosenDate.add(Calendar.YEAR, -1);	//	Set the last date back one year, and choose within that
				}

				List<AgreementTermInstance> list = new ArrayList<AgreementTermInstance>();
				for (AgreementTerm termInstance : termInstances) {
					if (allTerms) {
						list.add(DbAgreementTerm.getInstance(termInstance));
					} else if (chosenPath == null && chosenDate == null) {
						list.add(DbAgreementTerm.getInstance(termInstance));
					} else if (chosenPath != null && chosenPath.equals(termInstance.getPrimaryOrgPath())) {
						list.add(DbAgreementTerm.getInstance(termInstance));
					} else if (chosenDate != null && chosenDate.getTime().before(termInstance.getEndDate())) {
						list.add(DbAgreementTerm.getInstance(termInstance));
					}
				}
				
				agreement.setAgreementTerms(list);
				for (AgreementTermInstance term : list) {
					setDescriptions(term);
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
		
		if (agreement.getCommissionCode() != null) {
			CommissionType cType = DbCommissionType.getByCode(agreement.getCommissionCode());
			if (cType != null)
				agreement.setCommissionCodeDescription(cType.getDescription());
		}
		
		if (agreement.getDeleteReasonCode() != null) {
			DeleteReason dType = DbDeleteReason.getByCode(agreement.getDeleteReasonCode());
			if (dType != null)
				agreement.setDeleteReasonDescription(dType.getDescription());
		}
	}
	

	
	private void setDescriptions(AgreementTermInstance agreementTerm) {
		if (agreementTerm == null)
			return;
		
		if (agreementTerm.getTermTypeCode() != null) {
			TermType tType = DbTermType.getByCode(agreementTerm.getTermTypeCode());
			if (tType != null) {
				agreementTerm.setTermType(DbTermType.getInstance(tType));
			} else
				agreementTerm.setTermType(TermTypeInstance.getUnknownInstance(agreementTerm.getTermTypeCode()));
		} else
			agreementTerm.setTermType(TermTypeInstance.getUnknownInstance("none"));
		
		
		if (agreementTerm.getCommissionCode() != null && agreementTerm.getCommissionCode().length() > 0) {
			CommissionType commissionType = DbCommissionType.getByCode(agreementTerm.getCommissionCode());
			if (commissionType != null) {
				agreementTerm.setCommissionType(DbCommissionType.getInstance(commissionType));
			} else {
				agreementTerm.setCommissionType(CommissionTypeInstance.getUnknownInstance(agreementTerm.getCommissionCode()));
			}
		} else
			agreementTerm.setCommissionType(CommissionTypeInstance.getEmptyInstance());
		
		
		if (agreementTerm.getCancelReasonCode() != null && agreementTerm.getCancelReasonCode().length() > 0) {
			CancelReason cancelReason = DbCancelReason.getByCode(agreementTerm.getCancelReasonCode());
			if (cancelReason != null) {
				agreementTerm.setCancelReason(DbCancelReason.getInstance(cancelReason));
			} else {
				agreementTerm.setCancelReason(CancelReasonInstance.getUnknownInstance(agreementTerm.getCancelReasonCode()));
			}
		} else
			agreementTerm.setCancelReason(CancelReasonInstance.getEmptyInstance());
			

		
		if (agreementTerm.getProductCode() != null && agreementTerm.getProductCode().length() > 0) {
			Product product = DbProduct.getByCode(agreementTerm.getProductCode());
			if (product != null) {
				agreementTerm.setProduct(DbProduct.getInstance(product));
			} else {
				agreementTerm.setProduct(ProductInstance.getUnknownInstance(agreementTerm.getProductCode()));
			}
		} else
			agreementTerm.setProduct(ProductInstance.getEmptyInstance());
	}
}
