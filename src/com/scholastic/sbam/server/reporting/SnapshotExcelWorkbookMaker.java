package com.scholastic.sbam.server.reporting;

import java.util.HashMap;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFPalette;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.scholastic.sbam.server.database.codegen.CancelReason;
import com.scholastic.sbam.server.database.codegen.Institution;
import com.scholastic.sbam.server.database.codegen.Product;
import com.scholastic.sbam.server.database.codegen.Service;
import com.scholastic.sbam.server.database.codegen.SnapshotTermData;
import com.scholastic.sbam.server.database.codegen.TermType;
import com.scholastic.sbam.server.database.objects.DbCancelReason;
import com.scholastic.sbam.server.database.objects.DbInstitution;
import com.scholastic.sbam.server.database.objects.DbProduct;
import com.scholastic.sbam.server.database.objects.DbService;
import com.scholastic.sbam.server.database.objects.DbSnapshotTermData;
import com.scholastic.sbam.server.database.objects.DbTermType;
import com.scholastic.sbam.server.database.util.HibernateUtil;
import com.scholastic.sbam.shared.objects.CancelReasonInstance;
import com.scholastic.sbam.shared.objects.InstitutionInstance;
import com.scholastic.sbam.shared.objects.ProductInstance;
import com.scholastic.sbam.shared.objects.ServiceInstance;
import com.scholastic.sbam.shared.objects.SnapshotTermDataInstance;
import com.scholastic.sbam.shared.objects.TermTypeInstance;

public class SnapshotExcelWorkbookMaker {
	
	protected static final String	EXCEL_TEXT_FORMAT		=	"@";
	protected static final String	EXCEL_CURRENCY_FORMAT	=	"($#,##0.00_);[Red]($#,##0.00)";	//	or "#,##0.00"
	protected static final String	EXCEL_NUMBER_FORMAT		=	"#,##0";
	
	protected static final short	TEXT_FORMAT				= HSSFDataFormat.getBuiltinFormat(EXCEL_TEXT_FORMAT);
	protected static final short	CURRENCY_FORMAT			= HSSFDataFormat.getBuiltinFormat(EXCEL_CURRENCY_FORMAT);
	protected static final short	NUMBER_FORMAT			= HSSFDataFormat.getBuiltinFormat(EXCEL_NUMBER_FORMAT);
	
	protected int					snapshotId;
	
	protected HSSFWorkbook			wb						= new HSSFWorkbook();
	
	protected final HSSFCellStyle	TEXT_STYLE				= createTextStyle();

	//	These maps are used so that we'll reuse all instances, to be sure to conserve space in serialization
	
	protected HashMap<Integer, InstitutionInstance>	institutionMap	= new HashMap<Integer, InstitutionInstance>();
	protected HashMap<String, ProductInstance>		productMap		= new HashMap<String, ProductInstance>();
	protected HashMap<String, ServiceInstance>		serviceMap		= new HashMap<String, ServiceInstance>();
	protected HashMap<String, CancelReasonInstance>	cancelReasonMap = new HashMap<String, CancelReasonInstance>();
	protected HashMap<String, TermTypeInstance> 	termTypeMap		= new HashMap<String, TermTypeInstance>();
	
	public SnapshotExcelWorkbookMaker(int snapshotId) {
		this.snapshotId = snapshotId;
	}
	
	public HSSFWorkbook getWorkbook() {
		if (wb.getSummaryInformation().getAuthor() == null || wb.getSummaryInformation().getAuthor().length() == 0)
			populateWorkbook();
		
		return wb;
	}
	
	public void populateWorkbook() {
		 
		HSSFSheet sheet = wb.createSheet("new sheet");
		
		setInfo();
		
		createCustomPalette();
		createFonts();
		
		processTermData();
		
		// Create a row and put some cells in it. Rows are 0 based.
		HSSFRow row     = sheet.createRow((short)0);
		
		// Create a cell and put a value in it.
		HSSFCell cell   = row.createCell((short)0);
		
		cell.setCellValue(1);
		
		// Or do it on one line.
		row.createCell((short)1).setCellValue(1.2);
		row.createCell((short)2).setCellValue("This is a string");
		row.createCell((short)3).setCellValue(true);
	}
	
	protected void setInfo() {
		wb.getSummaryInformation().setAuthor("SBAM Application Generated Spreadsheet");
	}
	
	protected void createCustomPalette() {
//		HSSFPalette palette = wb.getCustomPalette();
//		
//		palette.setColorAtIndex(RED_TEXT_COLOR, (byte) 229,	(byte) 233,	(byte) 252);
	}
	
	protected void createFonts() {
//			
	}
	
	protected HSSFCellStyle createTextStyle() {
		HSSFCellStyle thisStyle = wb.createCellStyle();
		
		thisStyle.setDataFormat(TEXT_FORMAT);
		thisStyle.setWrapText(true);
		
		return thisStyle;
	}
	
	protected void processTermData() {
		HibernateUtil.openSession();
		HibernateUtil.startTransaction();

		try {
			
			//	Find only undeleted cancel reasons
			List<SnapshotTermData> snapshotTermDatas = DbSnapshotTermData.findFiltered(snapshotId, -1, -1, null, null, -1, -1, null);

			for (SnapshotTermData snapshotTermData : snapshotTermDatas) {
				SnapshotTermDataInstance instance = DbSnapshotTermData.getInstance(snapshotTermData);

				instance.setInstitution(getInstitution(instance.getUcn(), institutionMap));
				
				instance.setProduct(getProduct(instance.getProductCode(), productMap));
				
				instance.setService(getService(instance.getServiceCode(), serviceMap));
				
				instance.setCancelReason(getCancelReason(instance.getCancelReasonCode(), cancelReasonMap));
				
				instance.setTermType(getTermType(instance.getTermTypeCode(), termTypeMap));
				
				processTermData(instance);
			}

		} catch (Exception exc) {
			exc.printStackTrace();
		}
		
		HibernateUtil.endTransaction();
		HibernateUtil.closeSession();
	}
	
	protected void processTermData(SnapshotTermDataInstance instance) {
		
	}
	
	protected ServiceInstance getService(String serviceCode, HashMap<String, ServiceInstance> serviceMap) {
		if (serviceCode == null || serviceCode.length() == 0)
			return null;
		
		if (serviceMap.containsKey(serviceCode))
			return serviceMap.get(serviceCode);
		
		Service service = DbService.getByCode(serviceCode);
		if (service != null) {
			return DbService.getInstance(service);
		}
		
		return null;
	}
	
	protected ProductInstance getProduct(String productCode, HashMap<String, ProductInstance> productMap) {
		if (productCode == null || productCode.length() == 0)
			return null;
		
		if (productMap.containsKey(productCode))
			return productMap.get(productCode);
		
		Product product = DbProduct.getByCode(productCode);
		if (product != null) {
			return DbProduct.getInstance(product);
		}
		
		return null;
	}
	
	protected CancelReasonInstance getCancelReason(String cancelReasonCode, HashMap<String, CancelReasonInstance> cancelReasonMap) {
		if (cancelReasonCode == null || cancelReasonCode.length() == 0)
			return null;
		
		if (cancelReasonMap.containsKey(cancelReasonCode))
			return cancelReasonMap.get(cancelReasonCode);
		
		CancelReason cancelReason = DbCancelReason.getByCode(cancelReasonCode);
		if (cancelReason != null) {
			return DbCancelReason.getInstance(cancelReason);
		}
		
		return null;
	}
	
	protected InstitutionInstance getInstitution(int ucn, HashMap<Integer, InstitutionInstance> institutionMap) {
		if (ucn <= 0)
			return null;
		
		if (institutionMap.containsKey(ucn))
			return institutionMap.get(ucn);
		
		Institution institution = DbInstitution.getByCode(ucn);
		if (institution != null) {
			return DbInstitution.getInstance(institution);
		}
		
		return null;
	}
	
	protected TermTypeInstance getTermType(String termTypeCode, HashMap<String, TermTypeInstance> termTypeMap) {
		if (termTypeCode == null || termTypeCode.length() == 0)
			return null;
		
		if (termTypeMap.containsKey(termTypeCode))
			return termTypeMap.get(termTypeCode);
		
		TermType termType = DbTermType.getByCode(termTypeCode);
		if (termType != null) {
			return DbTermType.getInstance(termType);
		}
		
		return null;
	}
}
