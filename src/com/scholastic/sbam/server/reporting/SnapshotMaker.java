package com.scholastic.sbam.server.reporting;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.scholastic.sbam.server.database.codegen.Agreement;
import com.scholastic.sbam.server.database.codegen.AgreementSite;
import com.scholastic.sbam.server.database.codegen.Institution;
import com.scholastic.sbam.server.database.codegen.Snapshot;
import com.scholastic.sbam.server.database.codegen.SnapshotProductService;
import com.scholastic.sbam.server.database.codegen.SnapshotTermData;
import com.scholastic.sbam.server.database.codegen.SnapshotTermDataId;
import com.scholastic.sbam.server.database.objects.DbAgreement;
import com.scholastic.sbam.server.database.objects.DbAgreementSite;
import com.scholastic.sbam.server.database.objects.DbInstitution;
import com.scholastic.sbam.server.database.objects.DbSnapshot;
import com.scholastic.sbam.server.database.objects.DbSnapshotParameter;
import com.scholastic.sbam.server.database.objects.DbSnapshotProductService;
import com.scholastic.sbam.server.database.objects.DbSnapshotTermData;
import com.scholastic.sbam.server.database.util.HibernateUtil;
import com.scholastic.sbam.shared.objects.SnapshotInstance;
import com.scholastic.sbam.shared.objects.SnapshotParameterSetInstance;
import com.scholastic.sbam.shared.objects.SnapshotParameterValueObject;
import com.scholastic.sbam.shared.objects.SnapshotTermDataInstance;
import com.scholastic.sbam.shared.reporting.SnapshotParameterNames;
import com.scholastic.sbam.shared.util.AppConstants;

public class SnapshotMaker {

	protected Snapshot										dbSnapshot;
	
	protected SnapshotParameterSetInstance					parameters;
	
	protected int											nextRowId = 1;
	
	protected HashMap<String, SnapshotProductServiceEntry>	productServiceMap;
	
	protected char											savedStatus;
	
	public SnapshotMaker() {
	}
	
	public SnapshotInstance makeSnapshot(int snapshotId) {
		nextRowId = 1;
		
		Date snapshotTaken = new Date();
		
		//	Round the last 3 decimals off of the time, to get just seconds. because GWT or GXT is doing this some where on the UI side
		long roundedTime = 1000 * ( (long) snapshotTaken.getTime() / 1000);
		snapshotTaken.setTime(roundedTime);
		
		dbSnapshot = getSnapshot(snapshotId);
		if (dbSnapshot != null) {
			try {
				removeOldSnapshotData();
				compileSnapshot();
				updateSnapshot(snapshotTaken);
			} catch (Exception exc) {
				resetSnapshot();
				throw new IllegalArgumentException(exc.getMessage());
			}
		}
		
		return DbSnapshot.getInstance(dbSnapshot);
	}
	
	protected Snapshot getSnapshot(int snapshotId) {
		Snapshot dbSnapshot = null;
		
		HibernateUtil.openSession();
		HibernateUtil.startTransaction();

		try {
			
			//	Get existing
			dbSnapshot = DbSnapshot.getById(snapshotId);
			if (dbSnapshot == null)
				throw new IllegalArgumentException("Snapshot " + snapshotId + " not found.");
			
			if (dbSnapshot.getSnapshotTaken() != null)
				throw new IllegalArgumentException("INTERNAL SAFETY CHECK FAILED: Snapshot alrady taken.");
			
			if (dbSnapshot.getStatus() != AppConstants.STATUS_ACTIVE)
				if (dbSnapshot.getStatus() == AppConstants.STATUS_COMPILING)	
					throw new IllegalArgumentException("INTERNAL SAFETY CHECK FAILED: Snapshot is currently already compiling.");
				else	
					throw new IllegalArgumentException("INTERNAL SAFETY CHECK FAILED: Invalid snapshot status " + dbSnapshot.getStatus() + ".");
			
			savedStatus = dbSnapshot.getStatus();
			
			dbSnapshot.setStatus(AppConstants.STATUS_COMPILING);
			DbSnapshot.persist(dbSnapshot);
			
		} catch (IllegalArgumentException exc) {
			silentRollback();
			throw exc;
		} catch (Exception exc) {
			silentRollback();
			exc.printStackTrace();
			throw new IllegalArgumentException("The snapshot access failed unexpectedly.");
		} finally {
			if (HibernateUtil.isTransactionInProgress())
				HibernateUtil.endTransaction();
			HibernateUtil.closeSession();
		}
		
		return dbSnapshot;
	}
	
	protected Snapshot resetSnapshot() {
		HibernateUtil.openSession();
		HibernateUtil.startTransaction();

		try {
			
			//	Get existing
			dbSnapshot = DbSnapshot.getById(dbSnapshot.getSnapshotId());
			if (dbSnapshot != null) {
				dbSnapshot.setStatus(savedStatus);
				dbSnapshot.setSnapshotTaken(null);
				dbSnapshot.setExcelFilename(null);
				DbSnapshot.persist(dbSnapshot);
			}
			
		} catch (IllegalArgumentException exc) {
			silentRollback();
			throw exc;
		} catch (Exception exc) {
			silentRollback();
			exc.printStackTrace();
			throw new IllegalArgumentException("The snapshot reset failed unexpectedly.");
		} finally {
			if (HibernateUtil.isTransactionInProgress())
				HibernateUtil.endTransaction();
			HibernateUtil.closeSession();
		}
		
		return dbSnapshot;
	}
	
	protected void removeOldSnapshotData() {
	
		HibernateUtil.openSession();
		HibernateUtil.startTransaction();

		try {
			
			int rowsDeleted = DbSnapshotTermData.deleteAll(dbSnapshot.getSnapshotId());
			
			if (rowsDeleted > 0)
				System.out.println(new Date() + " : " + rowsDeleted + " rows deleted from retaken snapshot " + dbSnapshot.getSnapshotId() + ".");
			
		} catch (IllegalArgumentException exc) {
			silentRollback();
			throw exc;
		} catch (Exception exc) {
			silentRollback();
			exc.printStackTrace();
			throw new IllegalArgumentException("The old snapshot data clear failed unexpectedly.");
		} finally {
			if (HibernateUtil.isTransactionInProgress())
				HibernateUtil.endTransaction();
			HibernateUtil.closeSession();
		}
	}
	
	protected void compileSnapshot() {
	
		HibernateUtil.openSession();
		HibernateUtil.startTransaction();

		try {
			
			//	Load snapshot parameters and services
			
			parameters = DbSnapshotParameter.getParameters( DbSnapshotParameter.findBySnapshot(dbSnapshot.getSnapshotId() ));
			
			List<SnapshotProductService> snapshotProducts = DbSnapshotProductService.findProductBySnapshot(dbSnapshot.getSnapshotId(), AppConstants.STATUS_DELETED);
			List<SnapshotProductService> snapshotServices = DbSnapshotProductService.findServiceBySnapshot(dbSnapshot.getSnapshotId(), AppConstants.STATUS_DELETED); 
			
//			if (dbSnapshot.getProductServiceType() != SnapshotInstance.SERVICE_TYPE
//			&&  snapshotServices != null
//			&&  snapshotServices.size() > 0)
//				throw new IllegalArgumentException("Cannot create a snapshot by product with selection by service.");
			
			//	Load all products and services
			
			productServiceMap = SnapshotProductServiceEntry.getProductServicesMap();
			
			//	This does not do the customer piece...
			
			String agreementTermSql = new SnapshotAgreementTermSql(dbSnapshot, parameters, snapshotProducts, snapshotServices, productServiceMap).getSnapshotSql(); // getSnapshotSql(dbSnapshot, parameters, snapshotProductServices);
			
			// 	We use straight SQL, because normal Hibernate access wants to load the full dataset into memory, which uses just too much space.
			Connection conn   = HibernateUtil.getConnection();
			Statement sqlStmt = conn.createStatement();
			ResultSet results = sqlStmt.executeQuery(agreementTermSql);
			
			while (results.next()) {
				SnapshotTermDataInstance termData = DbSnapshotTermData.getInstance(results);
				
				processTermDataInstance(termData);
				
				Thread.yield();
			}
			
			results.close();
			sqlStmt.close();
			HibernateUtil.freeConnection(conn);	//	conn.close();
			
			//	Customer permutations done here
			
		} catch (IllegalArgumentException exc) {
			silentRollback();
			exc.printStackTrace();
			throw exc;
		} catch (Exception exc) {
			silentRollback();
			exc.printStackTrace();
			throw new IllegalArgumentException("The snapshot take failed unexpectedly.");
		} finally {
			if (HibernateUtil.isTransactionInProgress())
				HibernateUtil.endTransaction();
			HibernateUtil.closeSession();
		}
	}
	
	protected void updateSnapshot(Date snapshotTaken) {
	
		HibernateUtil.openSession();
		HibernateUtil.startTransaction();

		try {
			
			dbSnapshot = DbSnapshot.getById(dbSnapshot.getSnapshotId());
			if (dbSnapshot == null)
				throw new Exception("Snapshot disappered during compilation.");
			
			dbSnapshot.setSnapshotTaken(snapshotTaken);
			dbSnapshot.setSnapshotRows(nextRowId);
			dbSnapshot.setExcelFilename(null);
			dbSnapshot.setStatus(AppConstants.STATUS_ACTIVE);
			
			DbSnapshot.persist(dbSnapshot);
		
		} catch (IllegalArgumentException exc) {
			silentRollback();
			throw exc;
		} catch (Exception exc) {
			silentRollback();
			exc.printStackTrace();
			throw new IllegalArgumentException("The snapshot take failed unexpectedly.");
		} finally {
			if (HibernateUtil.isTransactionInProgress())
				HibernateUtil.endTransaction();
			HibernateUtil.closeSession();
		}
	}
	
	protected void processTermDataInstance(SnapshotTermDataInstance termData) {
		Agreement dbAgreement = DbAgreement.getById(termData.getAgreementId());
		
		if (dbAgreement == null)
			throw new IllegalArgumentException("Missing agreement " + termData.getAgreementId() + " when compiling snapshot " + dbSnapshot.getSnapshotId() + ".");
		
		if (dbSnapshot.getUcnType() == SnapshotInstance.BILL_UCN_TYPE) {
			processBillUcn(termData, dbAgreement);
		} else if (dbSnapshot.getUcnType() == SnapshotInstance.SITE_UCN_TYPE) {
			processSiteUcns(termData, dbAgreement);
		} else {
			throw new IllegalArgumentException("Invalid snapshot UCN type " + dbSnapshot.getUcnType() + " for snapshot ID " + dbSnapshot.getSnapshotId() + ".");
		}
		
	}
	
	
	protected void processBillUcn(SnapshotTermDataInstance termData, Agreement dbAgreement) {
		createTermDataRow(termData, dbAgreement.getBillUcn(), dbAgreement.getBillUcnSuffix(), 1.0d, dbAgreement);
	}
	
	protected void processSiteUcns(SnapshotTermDataInstance termData, Agreement dbAgreement) {
		List<AgreementSite> siteList = DbAgreementSite.findByAgreementId(termData.getAgreementId(), AppConstants.STATUS_ACTIVE, AppConstants.STATUS_DELETED);
		if (siteList == null || siteList.size() == 0) {
			processBillUcn(termData, dbAgreement);
		} else {
			List<AgreementSite> filteredList = new ArrayList<AgreementSite>();
			
			int lastSiteUcn = -1;
			int lastSiteUcnSuffix = -1;
			for (AgreementSite site : siteList) {
				//	This may be by location... only do one entry per site, even for multiple locations
				if (site.getId().getSiteUcn() != lastSiteUcn || site.getId().getSiteUcnSuffix() != lastSiteUcnSuffix)
					filteredList.add(site);
				
				lastSiteUcn = site.getId().getSiteUcn();
				lastSiteUcnSuffix = site.getId().getSiteUcnSuffix();
			}

			double ucnFraction = 1.0d / filteredList.size();
			for (AgreementSite site : filteredList) {
				createTermDataRow(termData, site.getId().getSiteUcn(), site.getId().getSiteUcnSuffix(), ucnFraction, dbAgreement);
			}	
		}
	}
	
	protected void createTermDataRow(SnapshotTermDataInstance termData, int ucn, int ucnSuffix, double ucnFraction, Agreement dbAgreement) {
		Institution institution = null;
		
		//	Apply any country filter
		if (parameters.getValues().containsKey(SnapshotParameterNames.INSTITUTION_COUNTRY)
		&&  parameters.getValues().get(SnapshotParameterNames.INSTITUTION_COUNTRY) != null
		&&  parameters.getValues().get(SnapshotParameterNames.INSTITUTION_COUNTRY).size() > 0) {
			if (institution == null) {
				institution = DbInstitution.getByCode(ucn);
			}
			if (!isSelectedCountry(institution))
				return;
		}
		
		//	Apply any state filter
		if (parameters.getValues().containsKey(SnapshotParameterNames.INSTITUTION_STATE)
				&&  parameters.getValues().get(SnapshotParameterNames.INSTITUTION_STATE) != null
				&&  parameters.getValues().get(SnapshotParameterNames.INSTITUTION_STATE).size() > 0) {
			if (institution == null) {
				institution = DbInstitution.getByCode(ucn);
			}
			if (!isSelectedState(institution))
				return;
		}
		
		SnapshotTermData dbTermData		= new SnapshotTermData();
		
		SnapshotTermDataId dbTermDataId = new SnapshotTermDataId();
		
		dbTermDataId.setSnapshotId(dbSnapshot.getSnapshotId());
		dbTermDataId.setAgreementId(termData.getAgreementId());
		dbTermDataId.setTermId(termData.getTermId());
		dbTermDataId.setUcn(ucn);
		dbTermDataId.setUcnSuffix(ucnSuffix);
		dbTermDataId.setProductCode(termData.getProductCode());
		dbTermDataId.setServiceCode(termData.getServiceCode());
		dbTermDataId.setRowId(nextRowId);
		
		dbTermData.setId(dbTermDataId);
		
		dbTermData.setStartDate(termData.getStartDate());
		dbTermData.setEndDate(termData.getEndDate());
		dbTermData.setTerminateDate(termData.getTerminateDate());
		dbTermData.setDollarValue(new BigDecimal(termData.getDollarValue()));
		dbTermData.setCommissionCode(termData.getCommissionCode());
		dbTermData.setCancelReasonCode(termData.getCancelReasonCode());
		dbTermData.setCancelDate(termData.getCancelDate());
		dbTermData.setPrimaryTerm(termData.getPrimaryTerm());
		dbTermData.setTermType(termData.getTermTypeCode());

		dbTermData.setWorkstations(dbAgreement.getWorkstations());
		dbTermData.setBuildings(dbAgreement.getBuildings());
		dbTermData.setPopulation(dbAgreement.getPopulation());
		dbTermData.setEnrollment(dbAgreement.getEnrollment());
		
		double serviceFraction = productServiceMap.get(termData.getProductCode()).getFraction(termData.getServiceCode());
		
		dbTermData.setCustomerFraction(new BigDecimal(ucnFraction));
		dbTermData.setServiceFraction(new BigDecimal(serviceFraction));
		
		DbSnapshotTermData.persist(dbTermData);
		
		nextRowId++;
		
	}
	
	protected boolean isSelectedCountry(Institution institution) {
		if (institution == null)
			return true;

		for (SnapshotParameterValueObject value : parameters.getValues().get(SnapshotParameterNames.INSTITUTION_COUNTRY)) {
			if (institution.getCountry() == null)
				return true;
			if (institution.getCountry().equalsIgnoreCase(value.getStringValue()))
				return true;
		}
		
		return false;
	}
	
	protected boolean isSelectedState(Institution institution) {
		if (institution == null)
			return true;
		
		//	If we already selected by country, don't apply this except to USA or CAN
		if (parameters.getValues().containsKey(SnapshotParameterNames.INSTITUTION_COUNTRY)
		&&  parameters.getValues().get(SnapshotParameterNames.INSTITUTION_COUNTRY) != null
		&&  parameters.getValues().get(SnapshotParameterNames.INSTITUTION_COUNTRY).size() > 0) {
			if (institution.getCountry() != null
			&&  !institution.getCountry().equalsIgnoreCase("USA")
			&&  !institution.getCountry().equalsIgnoreCase("CAN")
			&&  !institution.getCountry().equalsIgnoreCase("US")
			&&  !institution.getCountry().equalsIgnoreCase("CA"))
				return true;
		}

		for (SnapshotParameterValueObject value : parameters.getValues().get(SnapshotParameterNames.INSTITUTION_STATE)) {
			if (institution.getState() == null)
				return true;
			if (institution.getState().equalsIgnoreCase(value.getStringValue()))
				return true;
		}
		
		return false;
	}

	private void silentRollback() {
		try {
			if (HibernateUtil.isTransactionInProgress())
				HibernateUtil.getSession().getTransaction().rollback();	
		} catch (Exception exc) { }
	}
}
