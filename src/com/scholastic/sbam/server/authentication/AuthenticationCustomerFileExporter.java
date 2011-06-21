package com.scholastic.sbam.server.authentication;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;

import com.scholastic.sbam.server.database.codegen.Agreement;
import com.scholastic.sbam.server.database.codegen.Institution;
import com.scholastic.sbam.server.database.codegen.ProductService;
import com.scholastic.sbam.server.database.codegen.StatsAdmin;
import com.scholastic.sbam.server.database.objects.DbInstitution;
import com.scholastic.sbam.server.database.objects.DbStatsAdmin;
import com.scholastic.sbam.server.database.util.HibernateUtil;
import com.scholastic.sbam.server.database.util.SqlConstructor;
import com.scholastic.sbam.server.database.util.SqlExecution;
import com.scholastic.sbam.server.util.ExportController;
import com.scholastic.sbam.shared.objects.ExportProcessReport;
import com.scholastic.sbam.shared.util.AppConstants;

public class AuthenticationCustomerFileExporter {
		
		protected ExportProcessReport				exportReport;
		protected ExportController					controller;
		
		protected Agreement							agreement;
		protected HashMap<String, ProductService>	productServices = new HashMap<String, ProductService>();
		
		public AuthenticationCustomerFileExporter(ExportController controller, ExportProcessReport exportProcessReport) {
			this.exportReport	= exportProcessReport;
			this.controller		=	controller;
		}
		
		public void exportCustomers() throws IOException, SQLException {
			controller.consoleOutput("Exporting customers... ");
			
			AuthenticationExportFileCst file = new AuthenticationExportFileCst(controller, exportReport);
			file.open();
				
			//	Cycle through all referenced customers
			
			//	This SQL finds all active agreements
			SqlConstructor mainSql = new SqlConstructor();
			mainSql.append("select distinct ucn, ucn_suffix, customer_code ");
			mainSql.append("from ( ");
			mainSql.append("select site_ucn as ucn, site_ucn_suffix as ucn_suffix, ae_au.site_code as customer_code from ae_auth_unit, ae_au ");
			mainSql.append("where ae_auth_unit.au_id = ae_au.au_id ");
			mainSql.append("and ae_au.ae_id = ");
			mainSql.append(controller.getAeControl().getAeId() + "");
			mainSql.append("union");
			mainSql.append("select bill_ucn as ucn, bill_ucn_suffix as ucn_suffix, ae_au.bill_code as customer_code from ae_auth_unit, ae_au ");
			mainSql.append("where ae_auth_unit.au_id = ae_au.au_id ");
			mainSql.append("and ae_au.ae_id = ");
			mainSql.append(controller.getAeControl().getAeId() + "");
			mainSql.append("union");
			mainSql.append("select site_parent_ucn as ucn, site_parent_ucn_suffix, ae_au.site_parent_code as customer_code from ae_auth_unit, ae_au ");
			mainSql.append("where ae_auth_unit.au_id = ae_au.au_id ");
			mainSql.append("and ae_au.ae_id = ");
			mainSql.append(controller.getAeControl().getAeId() + "");
			mainSql.append(") joined ");
			mainSql.append("order by customer_code, ucn, ucn_suffix");
			
			SqlExecution mainLoopExec = new SqlExecution(mainSql.getSql());
			
			String lastCustomerCode = null;
			while (mainLoopExec.getResults().next()) {
				
				int ucn = mainLoopExec.getResults().getBigDecimal("ucn").intValue();
				int ucnSuffix = mainLoopExec.getResults().getBigDecimal("ucn_suffix").intValue();
				String customerCode = mainLoopExec.getResults().getString("customer_code");
				
				if (lastCustomerCode == null || !lastCustomerCode.equals(customerCode))
					processUcn(file, ucn, ucnSuffix, customerCode);
				
			}
			
			controller.forceConsoleOutput(exportReport.getCustomers() + " customers processed");
			
			mainLoopExec.close();
			
			file.close();
		}
		
		public void processUcn(AuthenticationExportFileCst file, int ucn, int ucnSuffix, String customerCode) {
			if (ucn <= 0)
				return;
			
			HibernateUtil.openSession();
			HibernateUtil.startTransaction();
			
			Institution institution = DbInstitution.getByCode(ucn);
			if (institution == null) {
				exportReport.addAlert("Missing institution for code " + customerCode + ", UCN " + ucn + " - " + ucnSuffix);
			} else {
				StatsAdmin statsAdmin = DbStatsAdmin.getById(ucn);
				
				if (statsAdmin != null && statsAdmin.getStatus() != AppConstants.STATUS_ACTIVE) {
					statsAdmin = null;	// If inactive, forget it
				}
				
				exportReport.countCustomers();
				file.write(customerCode, institution, statsAdmin);
			}
			
			HibernateUtil.endTransaction();
			HibernateUtil.closeSession();
		}
}
