package com.scholastic.sbam.server.authentication;

import java.util.HashMap;
import java.util.List;

import com.scholastic.sbam.server.database.codegen.AeAuthUnit;
import com.scholastic.sbam.server.database.codegen.AeIp;
import com.scholastic.sbam.server.database.codegen.AeIpId;
import com.scholastic.sbam.server.database.codegen.AePuid;
import com.scholastic.sbam.server.database.codegen.AePuidId;
import com.scholastic.sbam.server.database.codegen.AeUid;
import com.scholastic.sbam.server.database.codegen.AeUidId;
import com.scholastic.sbam.server.database.codegen.AeUrl;
import com.scholastic.sbam.server.database.codegen.AeUrlId;
import com.scholastic.sbam.server.database.codegen.Agreement;
import com.scholastic.sbam.server.database.codegen.AuthMethod;
import com.scholastic.sbam.server.database.codegen.Institution;
import com.scholastic.sbam.server.database.codegen.ProductService;
import com.scholastic.sbam.server.database.codegen.ProxyIp;
import com.scholastic.sbam.server.database.codegen.Site;
import com.scholastic.sbam.server.database.objects.DbAeIp;
import com.scholastic.sbam.server.database.objects.DbAePuid;
import com.scholastic.sbam.server.database.objects.DbAeUid;
import com.scholastic.sbam.server.database.objects.DbAeUrl;
import com.scholastic.sbam.server.database.objects.DbProxyIp;
import com.scholastic.sbam.server.util.ExportController;
import com.scholastic.sbam.shared.objects.AuthMethodInstance;
import com.scholastic.sbam.shared.objects.ExportProcessReport;
import com.scholastic.sbam.shared.objects.IpAddressInstance;
import com.scholastic.sbam.shared.util.AppConstants;

/**
 * This class represents a site in the Authentication Export process.
 * @author Bob Lacatena
 *
 */
public class AuthenticationExportMethod {
	protected ExportProcessReport				exportReport;
	protected ExportController					controller;
	
	protected	Agreement						agreement;
	protected	HashMap<String, ProductService>	productServices;
	protected	Site							site;
	protected	Institution						institution;
	protected	AuthMethod						authMethod;
	
	protected	AeAuthUnit						authUnit;
	
	public AuthenticationExportMethod(Agreement agreement, AeAuthUnit authUnit, Site site, Institution	institution, AuthMethod authMethod, ExportController controller, ExportProcessReport exportProcessReport) {
		this.exportReport		=	exportProcessReport;
		this.controller			=	controller;
		this.agreement			=	agreement;
		this.authUnit			=	authUnit;
		this.site				=	site;
		this.institution		=	institution;
		this.authMethod			=	authMethod;
	}
	
	public void exportMethod() {
		if (authMethod.getApproved() != 'y')
			return;
		if (authMethod.getValidated() != 'y')
			return;
		
		if (AuthMethodInstance.AM_IP.equals(authMethod.getId().getMethodType()))
			exportIp();
		else if (AuthMethodInstance.AM_UID.equals(authMethod.getId().getMethodType()))
			exportUid();
		else if (AuthMethodInstance.AM_URL.equals(authMethod.getId().getMethodType()))
			exportUrl();
	}
	
	protected void exportIp() {
		
		exportReport.countIp();
		
		List<Long []> ipSegements = AuthenticationIpTool.getSegmented(authMethod.getIpLo(), authMethod.getIpHi());
		
		AeIp	ip;
		AeIpId	ipId;
		
		for (Long [] ips : ipSegements) {
			
			ipId = new AeIpId();	
			
			ipId.setAeId(controller.getAeControl().getAeId());
			ipId.setAuId(authUnit.getAuId());
			ipId.setIp(IpAddressInstance.getBriefIpDisplay(ips));
			
			ip = DbAeIp.getById(ipId);
			
			if (ip != null) {
				if (ip.getRemote() != authMethod.getRemote()) {
					AuthenticationConflict conflict = new AuthenticationConflict(authMethod, ip, AuthenticationConflict.REMOTE_MISMATCH);
					controller.addConflict(conflict);
					exportReport.countUidConflict();
				}
				exportReport.countUidDuplicate();
			} else {
				ip = new AeIp();
				
				ip.setId(ipId);
				ip.setIpLo(ips [0]);
				ip.setIpHi(ips [1]);
				ip.setIpRangeCode(IpAddressInstance.getCommonIpRangeCode(ips));
				ip.setRemote(authMethod.getRemote());
				
				DbAeIp.persist(ip);
				
				exportReport.countIpEntry();
			}
		}
	}
	
	protected void exportUid() {
		if (authMethod.getProxyId() > 0)
			exportPuid();
		else
			exportPlainUid();
	}
	
	protected void exportPuid() {

		exportReport.countPuid();
		
		List<ProxyIp> proxyIps = DbProxyIp.findByProxyId(authMethod.getProxyId(), AppConstants.STATUS_ACTIVE);
		
		for (ProxyIp proxyIp : proxyIps) {
			List<Long []> ipSegments = AuthenticationIpTool.getSegmented(proxyIp.getIpLo(), proxyIp.getIpHi());
			
			AePuid	puid;
			AePuidId	puidId;
			
			for (Long [] ips : ipSegments) {
				
				puidId = new AePuidId();	
				
				puidId.setAeId(controller.getAeControl().getAeId());
				puidId.setAuId(authUnit.getAuId());
				puidId.setUserId(authMethod.getUserId());
				puidId.setIp(IpAddressInstance.getBriefIpDisplay(ips));
				
				puid = DbAePuid.getById(puidId);
				if (puid != null) {
					AuthenticationConflict conflict = null;
					if (!puid.getPassword().equals(authMethod.getPassword())) {
						conflict = new AuthenticationConflict(authMethod, puid, AuthenticationConflict.PASSWORD_MISMATCH);
						controller.addConflict(conflict);
					}
					if (puid.getRemote() != authMethod.getRemote()) {
						conflict = new AuthenticationConflict(authMethod, puid, AuthenticationConflict.REMOTE_MISMATCH);
						controller.addConflict(conflict);
					}
					if (puid.getUserType() != authMethod.getUserType()) {
						conflict = new AuthenticationConflict(authMethod, puid, AuthenticationConflict.USER_TYPE_MISMATCH);
						controller.addConflict(conflict);
					}
					if (conflict != null)
						exportReport.countUidConflict();
					
					exportReport.countUidDuplicate();
				} else {
					puid = new AePuid();
					
					puid.setId(puidId);
					puid.setPassword(authMethod.getPassword());
					puid.setIpLo(ips [0]);
					puid.setIpHi(ips [1]);
					puid.setIpRangeCode(IpAddressInstance.getCommonIpRangeCode(ips));
					puid.setRemote(authMethod.getRemote());
					puid.setUserType(authMethod.getUserType());
					
					DbAePuid.persist(puid);
					
					exportReport.countPuidEntry();
				}
			}
		}
	}
	
	protected void exportPlainUid() {
		exportReport.countUid();
		
		AeUidId uidId = new AeUidId();	
		
		uidId.setAeId(controller.getAeControl().getAeId());
		uidId.setAuId(authUnit.getAuId());
		uidId.setUserId(authMethod.getUserId());
		
		AeUid uid = DbAeUid.getById(uidId);
		
		if (uid == null) {
			uid = new AeUid();
			
			uid.setId(uidId);
			uid.setPassword(authMethod.getPassword());
			uid.setUserType(authMethod.getUserType());
			uid.setRemote(authMethod.getRemote());
			
			DbAeUid.persist(uid);
		} else {
			AuthenticationConflict conflict = null;
			if (!uid.getPassword().equals(authMethod.getPassword())) {
				conflict = new AuthenticationConflict(authMethod, uid, AuthenticationConflict.PASSWORD_MISMATCH);
				controller.addConflict(conflict);
			}
			if (uid.getRemote() != authMethod.getRemote()) {
				conflict = new AuthenticationConflict(authMethod, uid, AuthenticationConflict.REMOTE_MISMATCH);
				controller.addConflict(conflict);
			}
			if (uid.getUserType() != authMethod.getUserType()) {
				conflict = new AuthenticationConflict(authMethod, uid, AuthenticationConflict.USER_TYPE_MISMATCH);
				controller.addConflict(conflict);
			}
			if (conflict != null)
				exportReport.countUidConflict();
			
			exportReport.countUidDuplicate();
		}
	}
	
	protected void exportUrl() {
		exportReport.countUrl();
		
		AeUrlId urlId = new AeUrlId();	
		
		urlId.setAeId(controller.getAeControl().getAeId());
		urlId.setAuId(authUnit.getAuId());
		urlId.setUrl(authMethod.getUrl());
		
		AeUrl url = DbAeUrl.getById(urlId);
		
		if (url == null) {
			url = new AeUrl();
		
			url.setId(urlId);
			url.setRemote(authMethod.getRemote());
			
			DbAeUrl.persist(url);
		} else {
			if (url.getRemote() != authMethod.getRemote()) {
				AuthenticationConflict conflict = new AuthenticationConflict(authMethod, url, AuthenticationConflict.REMOTE_MISMATCH);
				controller.addConflict(conflict);
				exportReport.countUrlConflict();
			}
			exportReport.countUrlDuplicate();
		}
	}
}
