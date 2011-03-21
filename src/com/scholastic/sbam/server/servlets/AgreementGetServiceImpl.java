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
import com.scholastic.sbam.server.database.codegen.Institution;
import com.scholastic.sbam.server.database.codegen.Product;
import com.scholastic.sbam.server.database.codegen.TermType;
import com.scholastic.sbam.server.database.objects.DbAgreement;
import com.scholastic.sbam.server.database.objects.DbAgreementTerm;
import com.scholastic.sbam.server.database.objects.DbAgreementType;
import com.scholastic.sbam.server.database.objects.DbCancelReason;
import com.scholastic.sbam.server.database.objects.DbCommissionType;
import com.scholastic.sbam.server.database.objects.DbDeleteReason;
import com.scholastic.sbam.server.database.objects.DbInstitution;
import com.scholastic.sbam.server.database.objects.DbProduct;
import com.scholastic.sbam.server.database.objects.DbTermType;
import com.scholastic.sbam.server.database.util.HibernateUtil;
import com.scholastic.sbam.server.fastSearch.InstitutionCache;
import com.scholastic.sbam.shared.objects.AgreementInstance;
import com.scholastic.sbam.shared.objects.AgreementTermInstance;
import com.scholastic.sbam.shared.objects.AgreementTypeInstance;
import com.scholastic.sbam.shared.objects.CancelReasonInstance;
import com.scholastic.sbam.shared.objects.CommissionTypeInstance;
import com.scholastic.sbam.shared.objects.DeleteReasonInstance;
import com.scholastic.sbam.shared.objects.InstitutionInstance;
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
				
				// Get the institution
				if (agreement.getBillUcn() > 0) {
					Institution dbInstitution = DbInstitution.getByCode(agreement.getBillUcn());
					agreement.setInstitution(DbInstitution.getInstance(dbInstitution));

					if (agreement.getInstitution() != null) {
						InstitutionInstance institution = agreement.getInstitution();
						institution.setTypeDescription(InstitutionCache.getSingleton().getInstitutionType(institution.getTypeCode()).getDescription());
						institution.setGroupDescription(InstitutionCache.getSingleton().getInstitutionGroup(institution.getGroupCode()).getDescription());
						institution.setPublicPrivateDescription(InstitutionCache.getSingleton().getInstitutionPubPriv(institution.getPublicPrivateCode()).getDescription());
					}
				}
				
				//	Find only undeleted term types
				List<AgreementTerm> dbAgreementTerms = DbAgreementTerm.findByAgreementId(agreement.getId(), AppConstants.STATUS_ANY_NONE, AppConstants.STATUS_DELETED);
	
				//	First scan through the instances, to see what date or path to use
				String		chosenPath = null;
				Calendar	chosenDate = null;
				for (AgreementTerm dbAgreementTerm : dbAgreementTerms) {

					//	 Determine the most recent path, or the most recent date
						if (chosenPath == null && chosenDate == null && dbAgreementTerm.getEndDate() != null) {
							chosenDate = Calendar.getInstance();
							chosenDate.setTime(dbAgreementTerm.getEndDate());
							chosenPath = dbAgreementTerm.getPrimaryOrgPath();
						} else if (dbAgreementTerm.getEndDate().after(chosenDate.getTime())) {
							//	If we find something that ends more than a year later than this, throw out anything too old
							chosenDate.setTime(dbAgreementTerm.getEndDate());
							chosenPath = dbAgreementTerm.getPrimaryOrgPath();
						}
						

				}
				
				//	Keep either the chosen org path, or the oldest end date less a year
				if (chosenPath != null && chosenPath.length() > 0) {
					chosenDate = null;	// Ignore the date, and pick anything in the same path as the latest end date
				} else if (chosenDate != null) {
					chosenPath = null;
					chosenDate.add(Calendar.YEAR, -1);	//	Set the last date back one year, and choose within that
				}

				//  Second, scan again to pick which terms to keep  -- at the same time, compute the current value
				List<AgreementTermInstance> list = new ArrayList<AgreementTermInstance>();
				for (AgreementTerm dbAgreementTerm : dbAgreementTerms) {
					AgreementTermInstance termInstance = DbAgreementTerm.getInstance(dbAgreementTerm);
					//	Separate current value computation
					if (termInstance.deliverService())
						agreement.setCurrentValue(agreement.getCurrentValue() + dbAgreementTerm.getDollarValue().doubleValue());
					
					//	Figure out which terms to keep in the list, too
					if (allTerms) {
						list.add(termInstance);
					} else if (chosenPath == null && chosenDate == null) {
						list.add(termInstance);
					} else if (chosenPath != null && chosenPath.equals(dbAgreementTerm.getPrimaryOrgPath())) {
						list.add(termInstance);
					} else if (chosenDate != null && chosenDate.getTime().before(dbAgreementTerm.getEndDate())) {
						list.add(termInstance);
					} // else don't add it, we don't need it
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
		
		
		if (agreement.getAgreementTypeCode() != null && agreement.getAgreementTypeCode().length() > 0) {
			AgreementType agreementType = DbAgreementType.getByCode(agreement.getAgreementTypeCode());
			if (agreementType != null) {
				agreement.setAgreementType(DbAgreementType.getInstance(agreementType));
			} else {
				agreement.setAgreementType(AgreementTypeInstance.getUnknownInstance(agreement.getAgreementTypeCode()));
			}
		} else
			agreement.setAgreementType(AgreementTypeInstance.getEmptyInstance());
		
		
		if (agreement.getCommissionCode() != null && agreement.getCommissionCode().length() > 0) {
			CommissionType commissionType = DbCommissionType.getByCode(agreement.getCommissionCode());
			if (commissionType != null) {
				agreement.setCommissionType(DbCommissionType.getInstance(commissionType));
			} else {
				agreement.setCommissionType(CommissionTypeInstance.getUnknownInstance(agreement.getCommissionCode()));
			}
		} else
			agreement.setCommissionType(CommissionTypeInstance.getEmptyInstance());
		
		
		if (agreement.getDeleteReasonCode() != null && agreement.getDeleteReasonCode().length() > 0) {
			DeleteReason deleteReason = DbDeleteReason.getByCode(agreement.getDeleteReasonCode());
			if (deleteReason != null) {
				agreement.setDeleteReason(DbDeleteReason.getInstance(deleteReason));
			} else {
				agreement.setDeleteReason(DeleteReasonInstance.getUnknownInstance(agreement.getDeleteReasonCode()));
			}
		} else
			agreement.setDeleteReason(DeleteReasonInstance.getEmptyInstance());
		
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
