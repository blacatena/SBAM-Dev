package com.scholastic.sbam.server.authentication;

import java.sql.SQLException;
import java.util.List;

import com.scholastic.sbam.server.database.codegen.AeAuthUnit;
import com.scholastic.sbam.server.database.codegen.AeIp;
import com.scholastic.sbam.server.database.codegen.AePuid;
import com.scholastic.sbam.server.database.codegen.AeRsurl;
import com.scholastic.sbam.server.database.codegen.AeUid;
import com.scholastic.sbam.server.database.codegen.AeUrl;
import com.scholastic.sbam.server.database.objects.DbAeAuthUnit;
import com.scholastic.sbam.server.database.objects.DbAeIp;
import com.scholastic.sbam.server.database.objects.DbAePuid;
import com.scholastic.sbam.server.database.objects.DbAeRsurl;
import com.scholastic.sbam.server.database.objects.DbAeUid;
import com.scholastic.sbam.server.database.objects.DbAeUrl;
import com.scholastic.sbam.server.database.util.HibernateUtil;
import com.scholastic.sbam.server.database.util.SqlConstructor;
import com.scholastic.sbam.server.database.util.SqlExecution;
import com.scholastic.sbam.server.util.ExportController;
import com.scholastic.sbam.shared.objects.ExportProcessMessage;
import com.scholastic.sbam.shared.objects.ExportProcessReport;

public class AuthenticationConflictResolver {
	
	protected ExportController		controller;
	protected ExportProcessReport	exportReport;

	protected int					ipConflictsConsidered		=	0;
	protected int					uidConflictsConsidered		=	0;
	protected int					puidConflictsConsidered		=	0;
	protected int					urlConflictsConsidered		=	0;
	protected int					rsurlConflictsConsidered	=	0;

	protected int					ipConflictInstances			=	0;
	protected int					uidConflictInstances		=	0;
	protected int					puidConflictInstances		=	0;
	protected int					urlConflictInstances		=	0;
	protected int					rsurlConflictInstances		=	0;

	protected int					ipConflictsDiscarded		=	0;
	protected int					uidConflictsDiscarded		=	0;
	protected int					puidConflictsDiscarded		=	0;
	protected int					urlConflictsDiscarded		=	0;
	protected int					rsurlConflictsDiscarded		=	0;
	
	public AuthenticationConflictResolver(ExportController controller, ExportProcessReport exportReport) {
		this.controller		= controller;
		this.exportReport	= exportReport;
	}
	
	public static void main(String [] args) {
		
		try {
			AuthenticationRerunController controller = new AuthenticationRerunController(126, 0);
			AuthenticationConflictResolver resolver = new AuthenticationConflictResolver(controller, null);
			resolver.resolveConflicts();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}

	public void resolveConflicts() throws SQLException {
		controller.forceConsoleOutput("Resolving conflicts...");
		resolveAllConflicts();
		reportCounts();
		controller.forceConsoleOutput("Conflicts resolved.");
	}
	
	public void resolveAllConflicts() throws SQLException {
		resolveIpConflicts();
		resolveUidConflicts();
		resolvePuidConflicts();
		resolveUrlConflicts();
		resolveRsUrlConflicts();
	}
	
	public void reportCounts() {
		controller.forceConsoleOutput(ipConflictsConsidered + " IP conflicts considered.");
		controller.forceConsoleOutput(ipConflictInstances + " Conflicted IPs.");
		controller.forceConsoleOutput(ipConflictsDiscarded + " Conflicted IPs discarded.");
		controller.forceConsoleOutput(puidConflictsConsidered + " Proxy UID conflicts considered.");
		controller.forceConsoleOutput(puidConflictInstances + " Conflicted Proxy UIDs.");
		controller.forceConsoleOutput(puidConflictsDiscarded + " Conflicted Proxy UIDs discarded.");
		controller.forceConsoleOutput(uidConflictsConsidered + " UID conflicts considered.");
		controller.forceConsoleOutput(uidConflictInstances + " Conflicted UIDs.");
		controller.forceConsoleOutput(uidConflictsDiscarded + " Conflicted UIDs discarded.");
		controller.forceConsoleOutput(urlConflictsConsidered + " URL conflicts considered.");
		controller.forceConsoleOutput(urlConflictInstances + " Conflicted URLs.");
		controller.forceConsoleOutput(urlConflictsDiscarded + " Conflicted URLs discarded.");
		controller.forceConsoleOutput(rsurlConflictsConsidered + " Remote Setup URL conflicts considered.");
		controller.forceConsoleOutput(rsurlConflictInstances + " Conflicted Remote Setup URLs.");
		controller.forceConsoleOutput(rsurlConflictsDiscarded + " Conflicted Remote Setup URLs discarded.");
	}

	private void resolveUrlConflicts() throws SQLException {
		
		controller.forceConsoleOutput("Resolving URL conflicts");
	
		SqlConstructor mainSql = new SqlConstructor();
		mainSql.append("select url,count(*) ");
		mainSql.append("from (select distinct url, au_id from ae_url where ae_id =");
		mainSql.append(controller.getAeControl().getAeId() + "");
		mainSql.append(") urls ");
		mainSql.append("group by url having count(*) > 1");

		SqlExecution mainLoopExec = new SqlExecution(mainSql.getSql());
		
		while (mainLoopExec.getResults().next()) {
			
			String url = mainLoopExec.getResults().getString("url");
		
			processUrl(url);
		}
		
		mainLoopExec.close();
	}
	
	protected void processUrl(String url) {
		urlConflictsConsidered++;
		
		HibernateUtil.openSession();
		HibernateUtil.startTransaction();
		
		AeAuthUnit selectedAu = null;
		
		List<AeUrl> prevAeUrls = DbAeUrl.findByUrl(controller.getLastCompleteAeControl().getAeId(), url);
		if (prevAeUrls.size() > 0) {
			selectedAu = DbAeAuthUnit.getByCode(prevAeUrls.get(0).getId().getAuId());
		}
		
		List<AeUrl> aeUrls = DbAeUrl.findByUrl(controller.getAeControl().getAeId(), url);
		
		if (aeUrls.size() == 0)
			return;
		
		if (selectedAu == null)
			selectedAu = DbAeAuthUnit.getByCode(aeUrls.get(0).getId().getAuId());
		
		for (AeUrl aeUrl : aeUrls) {
			urlConflictInstances++;
			
			if (!isSameCustomer(selectedAu, aeUrl.getId().getAuId())) {
				
				AuthenticationConflict conflict = new AuthenticationConflict(selectedAu, aeUrl, AuthenticationConflict.CUSTOMER_MISMATCH);
				conflict.message = new ExportProcessMessage("Customer Mismatch, URL deleted from AU " + aeUrl.getId().getAuId() + " ... " + aeUrl.getId().getUrl(), ExportProcessMessage.ALERT);
				controller.addConflict(conflict);
				
				DbAeUrl.delete(aeUrl);
				urlConflictsDiscarded++;
			}
		}
		
		HibernateUtil.endTransaction();
		HibernateUtil.closeSession();
	}

	protected void resolvePuidConflicts() throws SQLException {
		
//		select user_id,ip_range_code,count(*)
//		from (select distinct user_id, ip_range_code, au_id from ae_puid where ae_id = 0) proxy_users
//		group by user_id, ip_range_code
//		having count(*) > 1 or length(ip_range_code) < 8
//		
//		or
//		
//		select user_id,ip_range_code,count(*)
//		from (
//			select distinct puid1.user_id, puid1.ip_range_code, puid1.au_id from ae_puid puid1, ae_puid puid2 
//				where puid1.ae_id = 0
//				and   puid2.ae_id = puid1.ae_id
//				and	  puid2.user_id = puid1.user_id
//				and   puid2.ip_range_code like concat(puid1.ip_range_code, '%')
//				and	  (puid1.au_id <> puid2.au_id || puid1.ip <> puid2.ip)
//		) proxy_users
//		group by user_id, ip_range_code
//		order by user_id, ip_range_code
//		
//		or
//
//		select distinct user_id, ip_range_code
//		from (
//		select distinct puid1.user_id, puid1.ip_range_code, puid1.au_id, puid2.ip_range_code as ip_range_code_2, puid2.au_id as au_id_2 
//		from ae_puid puid1, ae_puid puid2 
//				where puid1.ae_id = 0
//				and   puid2.ae_id = puid1.ae_id
//				and	  puid2.user_id = puid1.user_id
//				and   puid2.ip_range_code like concat(puid1.ip_range_code, '%')
//		 		and	  (puid1.au_id <> puid2.au_id || puid1.ip <> puid2.ip)
//				and   length(puid1.ip_range_code) <= length(puid2.ip_range_code)
//		) proxy_users
//		order by user_id, ip_range_code
		
		controller.forceConsoleOutput("Resolving Proxy UID conflicts");
		
		SqlConstructor mainSql = new SqlConstructor();
		mainSql.append("select distinct user_id, ip_range_code ");
		mainSql.append("from ( ");
		mainSql.append("select distinct puid1.user_id, puid1.ip_range_code, puid1.au_id, puid2.ip_range_code as ip_range_code_2, puid2.au_id as au_id_2  ");
		mainSql.append("from ae_puid puid1, ae_puid puid2  ");
		mainSql.append("where puid1.ae_id = ");
		mainSql.append(controller.getAeControl().getAeId() + "");
		mainSql.append("and   puid2.ae_id = puid1.ae_id ");
		mainSql.append("and	  puid2.user_id = puid1.user_id ");
		mainSql.append("and   puid2.ip_range_code like concat(puid1.ip_range_code, '%') ");
		mainSql.append("and	  (puid1.au_id <> puid2.au_id || puid1.ip <> puid2.ip) ");
		mainSql.append("and   length(puid1.ip_range_code) <= length(puid2.ip_range_code) ");
		mainSql.append(") proxy_users ");
		mainSql.append("order by user_id, ip_range_code ");

		SqlExecution mainLoopExec = new SqlExecution(mainSql.getSql());
		
		while (mainLoopExec.getResults().next()) {
			
			String userId		= mainLoopExec.getResults().getString("user_id");
			String ipRangeCode	= mainLoopExec.getResults().getString("ip_range_code");
		
			processPuid(userId, ipRangeCode);
		}
	}
	
	protected void processPuid(String userId, String ipRangeCode) {
		puidConflictsConsidered++;
		
		HibernateUtil.openSession();
		HibernateUtil.startTransaction();
		
		List<AePuid> aePuids = DbAePuid.findByUid(controller.getAeControl().getAeId(), userId, ipRangeCode);
		
		for (AePuid aePuid : aePuids) {
			puidConflictInstances++;
		}
		
		HibernateUtil.endTransaction();
		HibernateUtil.closeSession();
	}

	protected void resolveUidConflicts() throws SQLException {
		
		SqlConstructor mainSql = new SqlConstructor();
		mainSql.append("select user_id,count(*) ");
		mainSql.append("from (select distinct user_id, au_id from ae_uid where ae_id = ");
		mainSql.append(controller.getAeControl().getAeId() + "");
		mainSql.append(" union select distinct user_id, au_id from ae_puid where ae_id = ");
		mainSql.append(controller.getAeControl().getAeId() + "");
		mainSql.append(") users ");
		mainSql.append("group by user_id ");
		mainSql.append("having count(*) > 1 ");

		SqlExecution mainLoopExec = new SqlExecution(mainSql.getSql());
		
		while (mainLoopExec.getResults().next()) {
			
			String userId = mainLoopExec.getResults().getString("user_id");
		
			processUid(userId);
		}
		
		mainLoopExec.close();
	}
	
	protected void processUid(String userId) {
		uidConflictsConsidered++;
		
		HibernateUtil.openSession();
		HibernateUtil.startTransaction();
		
		AeAuthUnit selectedAu = null;
		
		List<AeUid> prevAeUids = DbAeUid.findByUserId(controller.getLastCompleteAeControl().getAeId(), userId);
		if (prevAeUids.size() > 0) {
			selectedAu = DbAeAuthUnit.getByCode(prevAeUids.get(0).getId().getAuId());
		}
		
		List<AeUid> aeUids = DbAeUid.findByUserId(controller.getAeControl().getAeId(), userId);
		
		if (aeUids.size() == 0)
			return;
		
		if (selectedAu == null)
			selectedAu = DbAeAuthUnit.getByCode(aeUids.get(0).getId().getAuId());
		
		for (AeUid aeUid : aeUids) {
			uidConflictInstances++;
			
			if (!isSameCustomer(selectedAu, aeUid.getId().getAuId())) {
				
				AuthenticationConflict conflict = new AuthenticationConflict(selectedAu, aeUid, AuthenticationConflict.CUSTOMER_MISMATCH);
				conflict.message = new ExportProcessMessage("Customer Mismatch, User ID deleted from AU " + aeUid.getId().getAuId() + " ... " + aeUid.getId().getUserId(), ExportProcessMessage.ALERT);
				controller.addConflict(conflict);
				
				DbAeUid.delete(aeUid);
				uidConflictsDiscarded++;
			}
		}
		
		HibernateUtil.endTransaction();
		HibernateUtil.closeSession();
	}

	protected void resolveIpConflicts() throws SQLException {
		
		SqlConstructor mainSql = new SqlConstructor();
		mainSql.append("select ip_range_code,count(*) ");
		mainSql.append("from (select distinct ip_range_code, au_id from ae_ip where ae_id = ");
		mainSql.append(controller.getAeControl().getAeId() + "");
		mainSql.append(") ips ");
		mainSql.append("group by ip_range_code ");
		mainSql.append("having count(*) > 1 or length(ip_range_code) < 8 ");

		SqlExecution mainLoopExec = new SqlExecution(mainSql.getSql());
		
		while (mainLoopExec.getResults().next()) {
			
			String ipRangeCode = mainLoopExec.getResults().getString("ip_range_code");
		
			processIp(ipRangeCode);
		}
		
		mainLoopExec.close();
	}
	
	protected void processIp(String ipRangeCode) {
		ipConflictsConsidered++;
		
		HibernateUtil.openSession();
		HibernateUtil.startTransaction();
		
		List<AeIp> aeIps = DbAeIp.findByIpRangeCode(controller.getAeControl().getAeId(), ipRangeCode);
		
		for (AeIp aeIp : aeIps) {
			ipConflictInstances++;
		}
		
		HibernateUtil.endTransaction();
		HibernateUtil.closeSession();
	}

	private void resolveRsUrlConflicts() throws SQLException {
		
		controller.forceConsoleOutput("Resolving Remote Setup URL conflicts");
	
		SqlConstructor mainSql = new SqlConstructor();
		mainSql.append("select url,count(*) ");
		mainSql.append("from (select distinct url, au_id from ae_rsurl where ae_id =");
		mainSql.append(controller.getAeControl().getAeId() + "");
		mainSql.append(") rsurls ");
		mainSql.append("group by url having count(*) > 1");

		SqlExecution mainLoopExec = new SqlExecution(mainSql.getSql());
		
		while (mainLoopExec.getResults().next()) {
			
			String url = mainLoopExec.getResults().getString("url");
		
			processRsUrl(url);
		}
		
		mainLoopExec.close();
	}
	
	protected void processRsUrl(String rsurl) {
		rsurlConflictsConsidered++;
		
		HibernateUtil.openSession();
		HibernateUtil.startTransaction();
		
		AeAuthUnit selectedAu = null;
		
		List<AeRsurl> prevAeRsUrls = DbAeRsurl.findByUrl(controller.getLastCompleteAeControl().getAeId(), rsurl);
		if (prevAeRsUrls.size() > 0) {
			selectedAu = DbAeAuthUnit.getByCode(prevAeRsUrls.get(0).getId().getAuId());
		}
		
		List<AeRsurl> aeRsUrls = DbAeRsurl.findByUrl(controller.getAeControl().getAeId(), rsurl);
		
		if (aeRsUrls.size() == 0)
			return;
		
		if (selectedAu == null)
			selectedAu = DbAeAuthUnit.getByCode(aeRsUrls.get(0).getId().getAuId());
		
		for (AeRsurl aeRsUrl : aeRsUrls) {
			rsurlConflictInstances++;
			
			if (!isSameCustomer(selectedAu, aeRsUrl.getId().getAuId())) {
				
				AuthenticationConflict conflict = new AuthenticationConflict(selectedAu, aeRsUrl, AuthenticationConflict.CUSTOMER_MISMATCH);
				conflict.message = new ExportProcessMessage("Customer Mismatch, Remote Setup URL deleted from AU " + aeRsUrl.getId().getAuId() + " ... " + aeRsUrl.getId().getUrl(), ExportProcessMessage.ALERT);
				controller.addConflict(conflict);
				
				DbAeUrl.delete(aeRsUrl);
				rsurlConflictsDiscarded++;
			}
		}
		
		HibernateUtil.endTransaction();
		HibernateUtil.closeSession();
	}
	
	protected boolean isSameCustomer(AeAuthUnit selectedAu, int auId) {
		AeAuthUnit thisAu = DbAeAuthUnit.getByCode(auId);
		if (thisAu != null && selectedAu != null) {
			if (thisAu.getSiteUcn() != selectedAu.getSiteUcn())	return false;
			if (thisAu.getSiteUcnSuffix() != selectedAu.getSiteUcnSuffix())	return false;
			if (thisAu.getSiteLocCode() != selectedAu.getSiteLocCode())	return false;
			if (thisAu.getSiteParentUcn() != selectedAu.getSiteParentUcn())	return false;
			if (thisAu.getSiteParentUcnSuffix() != selectedAu.getSiteParentUcnSuffix())	return false;
			if (thisAu.getBillUcn() != selectedAu.getBillUcn())	return false;
			if (thisAu.getBillUcnSuffix() != selectedAu.getBillUcnSuffix())	return false;
		}
		return true;
	}
}
