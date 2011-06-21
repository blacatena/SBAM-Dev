package com.scholastic.sbam.server.authentication;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.scholastic.sbam.server.database.codegen.AeAuthUnit;
import com.scholastic.sbam.server.database.codegen.AeIp;
import com.scholastic.sbam.server.database.codegen.AePref;
import com.scholastic.sbam.server.database.codegen.AePrefId;
import com.scholastic.sbam.server.database.codegen.AePuid;
import com.scholastic.sbam.server.database.codegen.AeRsurl;
import com.scholastic.sbam.server.database.codegen.AeUid;
import com.scholastic.sbam.server.database.codegen.AeUrl;
import com.scholastic.sbam.server.database.objects.DbAeAuthUnit;
import com.scholastic.sbam.server.database.objects.DbAeIp;
import com.scholastic.sbam.server.database.objects.DbAePref;
import com.scholastic.sbam.server.database.objects.DbAePuid;
import com.scholastic.sbam.server.database.objects.DbAeRsurl;
import com.scholastic.sbam.server.database.objects.DbAeUid;
import com.scholastic.sbam.server.database.objects.DbAeUrl;
import com.scholastic.sbam.server.database.util.HibernateUtil;
import com.scholastic.sbam.server.database.util.SqlConstructor;
import com.scholastic.sbam.server.database.util.SqlExecution;
import com.scholastic.sbam.server.util.ExportController;
import com.scholastic.sbam.shared.objects.ExportProcessReport;

public class AuthenticationMerger {
	protected	ExportController	controller;
	protected	ExportProcessReport	exportReport;
	
	protected	int					customerCombos	=	0;
	protected	int					authUnitCount	=	0;
	protected	int					authUnitCount2	=	0;
	
	public AuthenticationMerger(ExportController controller, ExportProcessReport exportReport) {
		this.controller		= controller;
		this.exportReport	= exportReport;
	}
	
	public void mergeAuthUnits() throws SQLException {
		controller.forceConsoleOutput("Merge equivalent authentication units...");
			
		//	Cycle through all referenced customers
		
		//	This SQL finds all active agreements
		SqlConstructor mainSql = new SqlConstructor();
		mainSql.append("select site_ucn, site_ucn_suffix, site_loc_code, site_parent_ucn, site_parent_ucn_suffix, bill_ucn, bill_ucn_suffix, count(*) as au_count ");
		mainSql.append("from (");
		
		mainSql.append("select distinct site_ucn, site_ucn_suffix, site_loc_code, site_parent_ucn, site_parent_ucn_suffix, bill_ucn, bill_ucn_suffix, ae_auth_unit.au_id ");
		mainSql.append("from ae_auth_unit, ae_pref ");
		mainSql.addCondition("ae_auth_unit.au_id = ae_pref.au_id");
		mainSql.addCondition("ae_pref.ae_id = ", controller.getAeControl().getAeId());
		mainSql.addCondition("ae_auth_unit.au_id < ", ExportController.REMOTE_AU_ADD);		// These dont' get put in ae_auth_unit, but check anyway
		mainSql.addCondition("ae_pref.pref_value = 'y'");
		
		mainSql.append(") auth_counts ");
		mainSql.append("group by site_ucn, site_ucn_suffix, site_loc_code, site_parent_ucn, site_parent_ucn_suffix, bill_ucn, bill_ucn_suffix ");
		mainSql.append("having count(*) > 1 ");	//	Optimize to skip combinations with nothing to merge
		mainSql.append("order by site_ucn, site_ucn_suffix, bill_ucn, bill_ucn_suffix");
		
		SqlExecution mainLoopExec = new SqlExecution(mainSql.getSql());
		
		while (mainLoopExec.getResults().next()) {
			
			processCustomerCombo(mainLoopExec.getResults());
			customerCombos++;
		}
		
		controller.forceConsoleOutput(customerCombos + " customer combinations considered for merging.");
		controller.forceConsoleOutput(authUnitCount2 + " auth units read.");
		controller.forceConsoleOutput(authUnitCount + " auth units considered for merging.");
		controller.forceConsoleOutput(exportReport.getAeAuMerged() + " auth units merged.");
		
		mainLoopExec.close();
	}
	
	public void processCustomerCombo(ResultSet resultSet) throws SQLException {
		//	Extract the columns
		
		int		siteUcn				= resultSet.getBigDecimal("site_ucn").intValue();
		int		siteUcnSuffix		= resultSet.getBigDecimal("site_ucn_suffix").intValue();
		String	siteLocCode			= resultSet.getString("site_loc_code");
		

		int		siteParentUcn		= resultSet.getBigDecimal("site_parent_ucn").intValue();
		int		siteParentUcnSuffix	= resultSet.getBigDecimal("site_parent_ucn_suffix").intValue();

		int		billUcn				= resultSet.getBigDecimal("bill_ucn").intValue();
		int		billUcnSuffix		= resultSet.getBigDecimal("bill_ucn_suffix").intValue();
		
		HibernateUtil.openSession();
		HibernateUtil.startTransaction();
		
		//	For each auth unit for this combination, merge any auth units with the exact same authentication methods by removing them from the second, and adding the additional products to the first
		
		List<AeAuthUnit> authUnits = DbAeAuthUnit.findBySite(siteUcn, siteUcnSuffix, siteLocCode, billUcn, billUcnSuffix, siteParentUcn, siteParentUcnSuffix);
		
		authUnitCount2 += authUnits.size();
		
		if (controller.getCountIncrement() > 0 && customerCombos % controller.getCountIncrement() == 0) 
			controller.forceConsoleOutput(authUnitCount2 + " AUs considered for " + customerCombos + " customer combinations considered for merging.");
		
		if (authUnits.size() > 1) {	// Optimization to reduce overhead, although the SQL should prevent this from happening
			//	Make a separate map to use, to optimize (i.e only consider what's not already done)
			
			HashMap<Integer, AeAuthUnit> auMap = new HashMap<Integer, AeAuthUnit>();
			
			for (AeAuthUnit authUnit : authUnits) {
				auMap.put(authUnit.getAuId(), authUnit);
			}
			
			for (AeAuthUnit authUnit : authUnits) {
				mergeIntoAuthUnit(authUnit, auMap, 0);
			}
			
			//	Repeat for remote AUs
			
			auMap.clear();
			for (AeAuthUnit authUnit : authUnits) {
				auMap.put(authUnit.getAuId(), authUnit);
			}
			
			for (AeAuthUnit authUnit : authUnits) {
				mergeIntoAuthUnit(authUnit, auMap, ExportController.REMOTE_AU_ADD);
			}
			
			HibernateUtil.endTransaction();
			HibernateUtil.closeSession();
		}
	}
	
	public void mergeIntoAuthUnit(AeAuthUnit authUnit, HashMap<Integer, AeAuthUnit> auMap, int offset) {
//		System.out.println("mergeIntoAuthUnit " + authUnitCount);
	
		//	If this AU isn't in the to-do map, no need to do it
		if (!auMap.containsKey(authUnit.getAuId()))
			return;
		
		authUnitCount++;
		
		List<AeAuthUnit> copyList = new ArrayList<AeAuthUnit>();
		copyList.addAll(auMap.values());
		
		//	Find any au with the exact same methods
		for (AeAuthUnit otherAu : copyList) {
			//	Dont' match something to itself
			if (otherAu.getAuId() == authUnit.getAuId())
				continue;
			
			if (matchMethods(authUnit, otherAu, offset)) {
				mergeAuthUnits(authUnit, otherAu, offset);
				auMap.remove(otherAu.getAuId());
			}
		}
		
		//	Remove the just processed auth unit from the list
		auMap.remove(authUnit.getAuId());

//		System.out.println("<<<<mergeIntoAuthUnit");
	}
	
	public boolean matchMethods(AeAuthUnit authUnit, AeAuthUnit otherAu, int offset) {
		if (matchIps(authUnit, otherAu, offset)
		&&	matchUids(authUnit, otherAu, offset)
		&&	matchPuids(authUnit, otherAu, offset)
		&&	matchUrls(authUnit, otherAu, offset)
		&&	matchRsurls(authUnit, otherAu, offset)) {
			return true;
		}
		return false;
	}

	private boolean matchIps(AeAuthUnit authUnit, AeAuthUnit otherAu, int offset) {
		List<AeIp> ips = DbAeIp.findByAuId(controller.getAeControl().getAeId(), authUnit.getAuId() + offset);
		List<AeIp> otherIps = DbAeIp.findByAuId(controller.getAeControl().getAeId(), otherAu.getAuId() + offset);
		
		if (ips.size() != otherIps.size())
			return false;
		
		for (int i = 0; i < ips.size(); i++) {
			if (ips.get(i).getIpLo() != otherIps.get(i).getIpLo())
				return false;
			if (ips.get(i).getIpHi() != otherIps.get(i).getIpHi())
				return false;
		}

		return true;
	}

	private boolean matchUids(AeAuthUnit authUnit, AeAuthUnit otherAu, int offset) {
		List<AeUid> uids = DbAeUid.findByAuId(controller.getAeControl().getAeId(), authUnit.getAuId() + offset);
		List<AeUid> otherUids = DbAeUid.findByAuId(controller.getAeControl().getAeId(), otherAu.getAuId() + offset);
		
		if (uids.size() != otherUids.size())
			return false;
		
		for (int i = 0; i < uids.size(); i++) {
			if (uids.get(i).getId().getUserId().equals(otherUids.get(i).getId().getUserId()))
				return false;
			if (uids.get(i).getPassword() != otherUids.get(i).getPassword())
				return false;
		}

		return true;
	}

	private boolean matchPuids(AeAuthUnit authUnit, AeAuthUnit otherAu, int offset) {
		List<AePuid> puids = DbAePuid.findByAuId(controller.getAeControl().getAeId(), authUnit.getAuId() + offset);
		List<AePuid> otherPuids = DbAePuid.findByAuId(controller.getAeControl().getAeId(), otherAu.getAuId() + offset);
		
		if (puids.size() != otherPuids.size())
			return false;
		
		for (int i = 0; i < puids.size(); i++) {
			if (puids.get(i).getId().getUserId().equals(otherPuids.get(i).getId().getUserId()))
				return false;
			if (puids.get(i).getIpLo() != otherPuids.get(i).getIpLo())
				return false;
			if (puids.get(i).getIpHi() != otherPuids.get(i).getIpHi())
				return false;
			if (puids.get(i).getPassword() != otherPuids.get(i).getPassword())
				return false;
		}

		return true;
	}

	private boolean matchUrls(AeAuthUnit authUnit, AeAuthUnit otherAu, int offset) {
		List<AeUrl> urls = DbAeUrl.findByAuId(controller.getAeControl().getAeId(), authUnit.getAuId() + offset);
		List<AeUrl> otherUrls = DbAeUrl.findByAuId(controller.getAeControl().getAeId(), otherAu.getAuId() + offset);
		
		if (urls.size() != otherUrls.size())
			return false;
		
		for (int i = 0; i < urls.size(); i++) {
			if (urls.get(i).getId().getUrl().equals(otherUrls.get(i).getId().getUrl()))
				return false;
		}

		return true;
	}
	
	private boolean matchRsurls(AeAuthUnit authUnit, AeAuthUnit otherAu, int offset) {
		List<AeRsurl> rsurls = DbAeRsurl.findByAuId(controller.getAeControl().getAeId(), authUnit.getAuId() + offset);
		List<AeRsurl> otherRsurls = DbAeRsurl.findByAuId(controller.getAeControl().getAeId(), otherAu.getAuId() + offset);
		
		if (rsurls.size() != otherRsurls.size())
			return false;
		
		for (int i = 0; i < rsurls.size(); i++) {
			if (rsurls.get(i).getId().getUrl().equals(otherRsurls.get(i).getId().getUrl()))
				return false;
		}

		return true;
	}

	public void mergeAuthUnits(AeAuthUnit authUnit, AeAuthUnit otherAu, int offset) {
		// Since all auth methods match, just need to find the different preferences, then remove everything from the unused AU
		
		List<AePref> otherPrefs = DbAePref.findByAuId(controller.getAeControl().getAeId(), otherAu.getAuId() + offset);
		
		for (AePref otherPref : otherPrefs) {
			if (otherPref.getPrefValue().equals("y")) {
				AePref pref = DbAePref.getById(controller.getAeControl().getAeId(), authUnit.getAuId() + offset, otherPref.getId().getPrefCode());
				if (pref == null) {
					pref = new AePref();
					
					AePrefId prefId = new AePrefId();
					prefId.setAeId(controller.getAeControl().getAeId());
					prefId.setAuId(authUnit.getAuId() + offset);
					prefId.setPrefCode(otherPref.getId().getPrefCode());
					
					pref.setId(prefId);
					
					pref.setPrefValue(otherPref.getPrefValue());
					
					DbAePref.persist(pref);
				}
			}
			
			//	Remove the preference, whether it was copied or not
			DbAePref.delete(otherPref);
		}
		
		//	Delete all methods
		List<AeIp> otherIps = DbAeIp.findByAuId(controller.getAeControl().getAeId(), otherAu.getAuId() + offset);
		for (AeIp otherIp : otherIps)
			DbAeIp.delete(otherIp);
		
		List<AeUid> otherUids = DbAeUid.findByAuId(controller.getAeControl().getAeId(), otherAu.getAuId() + offset);
		for (AeUid otherUid : otherUids)
			DbAeUid.delete(otherUid);
		
		List<AePuid> otherPuids = DbAePuid.findByAuId(controller.getAeControl().getAeId(), otherAu.getAuId() + offset);
		for (AePuid otherPuid : otherPuids)
			DbAePuid.delete(otherPuid);
		
		List<AeUrl> otherUrls = DbAeUrl.findByAuId(controller.getAeControl().getAeId(), otherAu.getAuId() + offset);
		for (AeUrl otherUrl : otherUrls)
			DbAeUrl.delete(otherUrl);
		
		List<AeRsurl> otherRsurls = DbAeRsurl.findByAuId(controller.getAeControl().getAeId(), otherAu.getAuId() + offset);
		for (AeRsurl otherRsurl : otherRsurls)
			DbAeRsurl.delete(otherRsurl);
				
		exportReport.countAeAuMerged();
	}
}
