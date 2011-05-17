package com.scholastic.sbam.server.servlets;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.extjs.gxt.ui.client.data.LoadConfig;
import com.scholastic.sbam.client.services.AgreementTermSearchService;
import com.scholastic.sbam.server.database.codegen.Agreement;
import com.scholastic.sbam.server.database.codegen.AgreementTerm;
import com.scholastic.sbam.server.database.codegen.Institution;
import com.scholastic.sbam.server.database.codegen.Product;
import com.scholastic.sbam.server.database.objects.DbAgreement;
import com.scholastic.sbam.server.database.objects.DbAgreementTerm;
import com.scholastic.sbam.server.database.objects.DbInstitution;
import com.scholastic.sbam.server.database.objects.DbProduct;
import com.scholastic.sbam.server.database.util.HibernateUtil;
import com.scholastic.sbam.server.fastSearch.InstitutionCache;
import com.scholastic.sbam.server.fastSearch.InstitutionCache.InstitutionCacheConflict;
import com.scholastic.sbam.shared.objects.AgreementInstance;
import com.scholastic.sbam.shared.objects.AgreementTermInstance;
import com.scholastic.sbam.shared.objects.AgreementTermTuple;
import com.scholastic.sbam.shared.objects.ProductInstance;
import com.scholastic.sbam.shared.objects.SynchronizedPagingLoadResult;
import com.scholastic.sbam.shared.security.SecurityManager;
import com.scholastic.sbam.shared.util.AppConstants;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class AgreementTermSearchServiceImpl extends AuthenticatedServiceServlet implements AgreementTermSearchService {

	@Override
	public SynchronizedPagingLoadResult<AgreementTermTuple> searchAgreementTerms(LoadConfig config, long syncId) throws IllegalArgumentException {
	
		authenticate("search agreementTerms", SecurityManager.ROLE_QUERY);
		
		List<AgreementTermTuple> list = new ArrayList<AgreementTermTuple>();

		String filter	= config.get("filter")   != null ? config.get("filter").toString() : null;
		String dateType	= config.get("dateType") != null ? config.get("dateType").toString() : null;
		Date   fromDate = config.get("fromDate") != null ? (Date) config.get("fromDate") : null;
		Date   toDate	= config.get("toDate")   != null ? (Date) config.get("toDate") : null;

		if (filter				== null)
			return new SynchronizedPagingLoadResult<AgreementTermTuple>(list, 0, 0, syncId); 
		
		HibernateUtil.openSession();
		HibernateUtil.startTransaction();
		
		try {
			
			List<Object []> tuples = DbAgreementTerm.findFiltered(filter, dateType, fromDate, toDate, AppConstants.STATUS_DELETED);
			
			int loadLimit = AppConstants.STANDARD_LOAD_LIMIT;
			if (config.get("limit") != null && config.get("limit") instanceof Integer)
				loadLimit = ( (Integer) config.get("limit"));
			if (tuples.size() > loadLimit) {
				//	This is an error return with no data, but a count (too many results)
				return new SynchronizedPagingLoadResult<AgreementTermTuple>(list, 0, tuples.size(), syncId);
			}
			
			for (Object [] objects : tuples) {

				AgreementInstance 		agreement		= DbAgreement    .getInstance((Agreement) objects [0]);
				AgreementTermInstance	agreementTerm	= DbAgreementTerm.getInstance((AgreementTerm) objects [1]);
				ProductInstance			product			= DbProduct.getInstance((Product) objects [2]);
				
				setDescriptions(agreementTerm, product);
				setDescriptions(agreement);
				setInstitution(agreement);
					
				AgreementTermTuple tuple = new AgreementTermTuple(agreement, agreementTerm);
				list.add(tuple);
			}

		} catch (Exception exc) {
			exc.printStackTrace();
		}
		
		HibernateUtil.endTransaction();
		HibernateUtil.closeSession();
		
		return new SynchronizedPagingLoadResult<AgreementTermTuple>(list, 0, list.size(), syncId);
	}

	private void setDescriptions(AgreementInstance agreement) {
		DbAgreement.setDescriptions(agreement);
	}
	
	private void setInstitution(AgreementInstance agreement) throws InstitutionCacheConflict {
		// Get the institution
		if (agreement.getBillUcn() > 0) {
			Institution dbInstitution = DbInstitution.getByCode(agreement.getBillUcn());
			agreement.setInstitution(DbInstitution.getInstance(dbInstitution));

			if (agreement.getInstitution() != null) {
				InstitutionCache.getSingleton().setDescriptions( agreement.getInstitution() );
			}
		}
	}

	private void setDescriptions(AgreementTermInstance agreementTerm, ProductInstance product) {
		agreementTerm.setProduct(product);
		DbAgreementTerm.setDescriptions(agreementTerm);
	}
}
