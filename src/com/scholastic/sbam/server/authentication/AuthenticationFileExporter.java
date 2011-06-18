package com.scholastic.sbam.server.authentication;

import java.io.IOException;
import java.util.List;

import com.scholastic.sbam.server.database.codegen.AeAu;
import com.scholastic.sbam.server.database.codegen.AeIp;
import com.scholastic.sbam.server.database.codegen.AePref;
import com.scholastic.sbam.server.database.codegen.AePrefCode;
import com.scholastic.sbam.server.database.codegen.AePuid;
import com.scholastic.sbam.server.database.codegen.AeRsurl;
import com.scholastic.sbam.server.database.codegen.AeUid;
import com.scholastic.sbam.server.database.codegen.AeUrl;
import com.scholastic.sbam.server.database.objects.DbAeAu;
import com.scholastic.sbam.server.database.objects.DbAeIp;
import com.scholastic.sbam.server.database.objects.DbAePref;
import com.scholastic.sbam.server.database.objects.DbAePrefCode;
import com.scholastic.sbam.server.database.objects.DbAePuid;
import com.scholastic.sbam.server.database.objects.DbAeRsurl;
import com.scholastic.sbam.server.database.objects.DbAeUid;
import com.scholastic.sbam.server.database.objects.DbAeUrl;
import com.scholastic.sbam.server.database.util.HibernateUtil;
import com.scholastic.sbam.server.util.ExportController;
import com.scholastic.sbam.shared.objects.ExportProcessReport;

public class AuthenticationFileExporter {
	protected	ExportController	controller;
	protected	String				exportDirectory = null;
	protected	ExportProcessReport	exportReport;
	
	public AuthenticationFileExporter(ExportController controller, ExportProcessReport exportReport) {
		this.controller		= controller;
		this.exportReport	= exportReport;
	}
	
	public void exportAuthenticationFiles() throws Exception {
		if (exportDirectory == null)
			exportDirectory = controller.getExportDirectory();
		
		controller.forceConsoleOutput("Exporting output files to " + exportDirectory + ".");
		
		AuthenticationExportFileStatus statusFile = new AuthenticationExportFileStatus(controller, exportReport);
		
		statusFile.open();
		statusFile.write("Started","Export");
		
		try {
			exportAus();
			statusFile.write("Part OK", "AUs written");
			
			exportAuPrefs();
			statusFile.write("Part OK", "AU Preferences written");
			
			exportAuIps();
			statusFile.write("Part OK", "AU IPs written");
			
			exportAuUids();
			statusFile.write("Part OK", "AU UIDs written");
			
			exportAuPuids();
			statusFile.write("Part OK", "AU Proxy UIDs written");
			
			exportAuUrls();
			statusFile.write("Part OK", "AU URLs written");
			
			exportAuRsUrls();
			statusFile.write("Part OK", "AU Remote Setup URLs written");
			
			exportAuPrefCodes();
			statusFile.write("Part OK", "AU Preference Codes written");
			
			new AuthenticationExportCustomers(controller, exportReport).exportCustomers();
			statusFile.write("Part OK", "Customers written");
			
			statusFile.write("OK", "Export completed successfully");
		} catch (Exception e) {
			statusFile.write(e.getMessage(), "Previous step");
			throw e;
		}
		
		statusFile.close();
	}
	
	public void exportAus() throws IOException {
		controller.forceConsoleOutput("Exporting AUs...");
		
		HibernateUtil.openSession();
		HibernateUtil.startTransaction();
		
		List<AeAu> aus = DbAeAu.findByAeId(controller.getAeControl().getAeId());
		
		AuthenticationExportFileAu file = new AuthenticationExportFileAu(controller, exportReport);
		file.open();
		
		for (AeAu au : aus) {
			file.write(au);
		}
		
		file.close();
		
		HibernateUtil.endTransaction();
		HibernateUtil.closeSession();
	}
	
	public void exportAuPrefs() throws IOException {
		controller.forceConsoleOutput("Exporting AU Preferences...");
		
		HibernateUtil.openSession();
		HibernateUtil.startTransaction();
		
		List<AePref> auPrefs = DbAePref.findByAeId(controller.getAeControl().getAeId());
		
		AuthenticationExportFileAuPref file = new AuthenticationExportFileAuPref(controller, exportReport);
		file.open();
		
		for (AePref auPref : auPrefs) {
			file.write(auPref);
		}
		
		file.close();
		
		HibernateUtil.endTransaction();
		HibernateUtil.closeSession();
	}
	
	public void exportAuIps() throws IOException {
		controller.forceConsoleOutput("Exporting Ips...");
		
		HibernateUtil.openSession();
		HibernateUtil.startTransaction();
		
		List<AeIp> ips = DbAeIp.findByAeId(controller.getAeControl().getAeId());
		
		AuthenticationExportFileAuIp file = new AuthenticationExportFileAuIp(controller, exportReport);
		file.open();
		
		for (AeIp ip : ips) {
			file.write(ip);
		}
		
		file.close();
		
		HibernateUtil.endTransaction();
		HibernateUtil.closeSession();
	}
	
	public void exportAuUrls() throws IOException {
		controller.forceConsoleOutput("Exporting Urls...");
		
		HibernateUtil.openSession();
		HibernateUtil.startTransaction();
		
		List<AeUrl> urls = DbAeUrl.findByAeId(controller.getAeControl().getAeId());
		
		AuthenticationExportFileAuUrl file = new AuthenticationExportFileAuUrl(controller, exportReport);
		file.open();
		
		for (AeUrl url : urls) {
			file.write(url);
		}
		
		file.close();
		
		HibernateUtil.endTransaction();
		HibernateUtil.closeSession();
	}
	
	public void exportAuUids() throws IOException {
		controller.forceConsoleOutput("Exporting Uids...");
		
		HibernateUtil.openSession();
		HibernateUtil.startTransaction();
		
		List<AeUid> uids = DbAeUid.findByAeId(controller.getAeControl().getAeId());
		
		AuthenticationExportFileAuUid file = new AuthenticationExportFileAuUid(controller, exportReport);
		file.open();
		
		for (AeUid uid : uids) {
			file.write(uid);
		}
		
		file.close();
		
		HibernateUtil.endTransaction();
		HibernateUtil.closeSession();
	}
	
	public void exportAuPuids() throws IOException {
		controller.forceConsoleOutput("Exporting Proxy Uids...");
		
		HibernateUtil.openSession();
		HibernateUtil.startTransaction();
		
		List<AePuid> puids = DbAePuid.findByAeId(controller.getAeControl().getAeId());
		
		AuthenticationExportFileAuPuid file = new AuthenticationExportFileAuPuid(controller, exportReport);
		file.open();
		
		for (AePuid puid : puids) {
			file.write(puid);
		}
		
		file.close();
		
		HibernateUtil.endTransaction();
		HibernateUtil.closeSession();
	}
	
	public void exportAuRsUrls() throws IOException {
		controller.forceConsoleOutput("Exporting Remote Setup Urls...");
		
		HibernateUtil.openSession();
		HibernateUtil.startTransaction();
		
		List<AeRsurl> rsurls = DbAeRsurl.findByAeId(controller.getAeControl().getAeId());
		
		AuthenticationExportFileAuRsurl file = new AuthenticationExportFileAuRsurl(controller, exportReport);
		file.open();
		
		for (AeRsurl rsurl : rsurls) {
			file.write(rsurl);
		}
		
		file.close();
		
		HibernateUtil.endTransaction();
		HibernateUtil.closeSession();
	}
	
	public void exportAuPrefCodes() throws IOException {
		controller.forceConsoleOutput("Exporting AU Preference Codes...");
		
		HibernateUtil.openSession();
		HibernateUtil.startTransaction();
		
		List<AePrefCode> auPrefCodes = DbAePrefCode.findByAeId(controller.getAeControl().getAeId());
		
		AuthenticationExportFilePrefCode file = new AuthenticationExportFilePrefCode(controller, exportReport);
		file.open();
		
		for (AePrefCode auPrefCode : auPrefCodes) {
			file.write(auPrefCode);
		}
		
		file.close();
		
		HibernateUtil.endTransaction();
		HibernateUtil.closeSession();
	}
}
