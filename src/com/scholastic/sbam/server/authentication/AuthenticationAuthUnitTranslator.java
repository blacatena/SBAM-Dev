package com.scholastic.sbam.server.authentication;

import java.util.List;

import com.scholastic.sbam.server.database.codegen.AeAu;
import com.scholastic.sbam.server.database.codegen.AeAuId;
import com.scholastic.sbam.server.database.codegen.AeAuthUnit;
import com.scholastic.sbam.server.database.codegen.UcnConversion;
import com.scholastic.sbam.server.database.objects.DbAeAu;
import com.scholastic.sbam.server.database.objects.DbAeAuthUnit;
import com.scholastic.sbam.server.database.objects.DbUcnConversion;
import com.scholastic.sbam.server.database.util.HibernateUtil;
import com.scholastic.sbam.server.util.ExportController;
import com.scholastic.sbam.shared.exceptions.AuthenticationExportException;
import com.scholastic.sbam.shared.objects.ExportProcessReport;

/**
 * This method translates UCNs to old customer codes, where needed.
 * @author Bob Lacatena
 *
 */
public class AuthenticationAuthUnitTranslator {
	public static final String	BAD_CUSTOMER_CODE	= "MISSING";
	protected	ExportController	controller;
	protected	ExportProcessReport	exportReport;
	
	public AuthenticationAuthUnitTranslator(ExportController controller, ExportProcessReport exportReport) {
		this.controller		= controller;
		this.exportReport	= exportReport;
	}
	
	public void translateAuthUnits() {
		controller.forceConsoleOutput("Translating AUs for export...");

		HibernateUtil.openSession();
		HibernateUtil.startTransaction();
		
		List<AeAuthUnit> aus = DbAeAuthUnit.findForExport();
		
		for (AeAuthUnit au : aus) {
			AeAu aeAu = new AeAu();
			
			AeAuId aeAuId = new AeAuId();
			
			aeAuId.setAeId(controller.getAeControl().getAeId());
			aeAuId.setAuId(au.getAuId());
			
			aeAu.setId(aeAuId);
			
			try {
				aeAu.setSiteParentCode(getCustomerCode(au.getSiteParentUcn(), au.getSiteParentUcnSuffix()));
				aeAu.setBillCode(getCustomerCode(au.getBillUcn(), au.getBillUcnSuffix()));
				aeAu.setSiteCode(getCustomerCode(au.getSiteUcn(), au.getSiteUcnSuffix()));
				aeAu.setSiteLocCode(au.getSiteLocCode());
			} catch (AuthenticationExportException auExExc) {
				controller.forceConsoleOutput(auExExc.getMessage() + " for AU " + aeAu.getId().getAuId() + ".");
				throw auExExc;
			}
			
			if (BAD_CUSTOMER_CODE.equals(aeAu.getBillCode())
			||	BAD_CUSTOMER_CODE.equals(aeAu.getSiteCode())
			||	BAD_CUSTOMER_CODE.equals(aeAu.getSiteParentCode()))
				exportReport.countBadCustomerCodeAus();
			
			DbAeAu.persist(aeAu);
			
			exportReport.countAeAuCopied();
		}
		
		HibernateUtil.endTransaction();
		HibernateUtil.closeSession();
	}
	
	public String getCustomerCode(int ucn, int ucnSuffix) {
		if (ucn <= 0) {
			exportReport.countBadCustomerCodes();
			return BAD_CUSTOMER_CODE;
//			throw new AuthenticationExportException("UCN missing");
		}
		
		if (controller.getAeControl().getUcnMode() == ExportController.EXPORT_UCNS) {
			return ucn + "";
		}
		
		UcnConversion ucnConversion = DbUcnConversion.getById(ucn, ucnSuffix);
		
		if (ucnConversion == null) {
			return ucn + "";
		}
		
		return ucnConversion.getOldCustomerCode();
	}
}
