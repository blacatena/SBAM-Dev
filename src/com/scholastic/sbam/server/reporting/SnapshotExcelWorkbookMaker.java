package com.scholastic.sbam.server.reporting;

import java.util.Date;
import java.util.TreeMap;
import java.util.List;

import javax.servlet.ServletException;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Font;

import com.scholastic.sbam.server.database.codegen.CancelReason;
import com.scholastic.sbam.server.database.codegen.Institution;
import com.scholastic.sbam.server.database.codegen.Product;
import com.scholastic.sbam.server.database.codegen.Service;
import com.scholastic.sbam.server.database.codegen.SnapshotParameter;
import com.scholastic.sbam.server.database.codegen.SnapshotTermData;
import com.scholastic.sbam.server.database.codegen.TermType;
import com.scholastic.sbam.server.database.objects.DbCancelReason;
import com.scholastic.sbam.server.database.objects.DbInstitution;
import com.scholastic.sbam.server.database.objects.DbProduct;
import com.scholastic.sbam.server.database.objects.DbService;
import com.scholastic.sbam.server.database.objects.DbSnapshotParameter;
import com.scholastic.sbam.server.database.objects.DbSnapshotTermData;
import com.scholastic.sbam.server.database.objects.DbTermType;
import com.scholastic.sbam.server.database.util.HibernateUtil;
import com.scholastic.sbam.shared.objects.CancelReasonInstance;
import com.scholastic.sbam.shared.objects.InstitutionInstance;
import com.scholastic.sbam.shared.objects.ProductInstance;
import com.scholastic.sbam.shared.objects.ServiceInstance;
import com.scholastic.sbam.shared.objects.SnapshotParameterValueObject;
import com.scholastic.sbam.shared.objects.SnapshotTermDataInstance;
import com.scholastic.sbam.shared.objects.TermTypeInstance;
import com.scholastic.sbam.shared.reporting.SnapshotParameterNames;

public class SnapshotExcelWorkbookMaker {
	
	//	Excel lookup =VLOOKUP(B2,Sheet1!$A$1:$B$22,2)
	
	protected static final int		DATE_WIDTH				= 12;
	protected static final int		DOLLAR_WIDTH			= 12;
	protected static final int		CODE_WIDTH				= 12;
	protected static final int		DESCRIPTION_WIDTH		= 30;
	protected static final int		CITY_WIDTH				= 20;
	protected static final int		STATE_WIDTH				= 6;
	protected static final int		COUNTRY_WIDTH			= 12;
	
	protected static final String	EXCEL_TEXT_FORMAT		=	"text";
	protected static final String	EXCEL_CURRENCY_FORMAT	=	"$#,##0.00;[Red]($#,##0.00)";	//	or "#,##0.00"
	protected static final String	EXCEL_NUMBER_FORMAT		=	"#,##0";
	protected static final String	EXCEL_INTEGER_FORMAT	=	"0";
	protected static final String	EXCEL_LONG_DATE_FORMAT	=	"d-mmm-yy";
	
	protected static final short	TEXT_FORMAT				=	HSSFDataFormat.getBuiltinFormat(EXCEL_TEXT_FORMAT);
	protected static final short	CURRENCY_FORMAT			=	HSSFDataFormat.getBuiltinFormat(EXCEL_CURRENCY_FORMAT);
	protected static final short	NUMBER_FORMAT			=	HSSFDataFormat.getBuiltinFormat(EXCEL_NUMBER_FORMAT);
	protected static final short	INTEGER_FORMAT			=	HSSFDataFormat.getBuiltinFormat(EXCEL_INTEGER_FORMAT);
	protected static final short	DATE_FORMAT				=	HSSFDataFormat.getBuiltinFormat(EXCEL_LONG_DATE_FORMAT);
	
	protected int					snapshotId;
	
	protected boolean				useLookups				=	true;
	
	protected HSSFWorkbook			wb						=	new HSSFWorkbook();
	
	protected HSSFSheet				dataSheet				=	wb.createSheet("Snapshot Term Data");
	protected HSSFSheet				coverSheet				=	wb.createSheet("Snapshot Parameters");
	protected HSSFSheet				institutionSheet		=	wb.createSheet("Institutions");
	protected HSSFSheet				productSheet			=	wb.createSheet("Products");
	protected HSSFSheet				serviceSheet			=	wb.createSheet("Services");
	
	protected final HSSFFont		BOLD_FONT				=	createBoldFont();
	
	protected final HSSFCellStyle	TEXT_STYLE				=	createTextStyle();
	protected final HSSFCellStyle	BOLD_STYLE				=	createBoldStyle();
	protected final HSSFCellStyle	DATE_STYLE				=	createDateStyle();
	protected final HSSFCellStyle	NUMBER_STYLE			=	createNumberStyle();
	protected final HSSFCellStyle	INTEGER_STYLE			=	createIntegerStyle();
	protected final HSSFCellStyle	CURRENCY_STYLE			=	createCurrencyStyle();
	
	protected int					rowNum					=	0;

	//	These maps are used so that we'll reuse all instances, to be sure to conserve space in serialization
	
	protected TreeMap<Integer, InstitutionInstance>	institutionMap	= new TreeMap<Integer, InstitutionInstance>();
	protected TreeMap<String, ProductInstance>		productMap		= new TreeMap<String, ProductInstance>();
	protected TreeMap<String, ServiceInstance>		serviceMap		= new TreeMap<String, ServiceInstance>();
	protected TreeMap<String, CancelReasonInstance>	cancelReasonMap = new TreeMap<String, CancelReasonInstance>();
	protected TreeMap<String, TermTypeInstance> 	termTypeMap		= new TreeMap<String, TermTypeInstance>();
	
	public SnapshotExcelWorkbookMaker(int snapshotId) {
		this.snapshotId = snapshotId;
	}
	
	public HSSFWorkbook getWorkbook() throws ServletException {
		if (wb.getSummaryInformation() == null 
		||  wb.getSummaryInformation().getAuthor() == null 
		||  wb.getSummaryInformation().getAuthor().length() == 0)
			populateWorkbook();
		
		return wb;
	}
	
	public void populateWorkbook() throws ServletException {
		setInfo();
		
		createCustomPalette();
		
		populateCoverSheet();
		
		startDataSheet();
		
		processTermData();
		
		addLookupSheets();
	}
	
	protected void setInfo() {
		wb.createInformationProperties();
		wb.getSummaryInformation().setTitle("Snapshot Name");
		wb.getSummaryInformation().setAuthor("SBAM Application Generated Spreadsheet");
	}
	
	protected void createCustomPalette() {
//		HSSFPalette palette = wb.getCustomPalette();
//		
//		palette.setColorAtIndex(RED_TEXT_COLOR, (byte) 229,	(byte) 233,	(byte) 252);
	}
	
	protected HSSFFont createBoldFont() {
		HSSFFont thisFont = wb.createFont();
		
		thisFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
		
		return thisFont;
	}
	
	protected HSSFCellStyle createTextStyle() {
		HSSFCellStyle thisStyle = wb.createCellStyle();
		
		thisStyle.setDataFormat(TEXT_FORMAT);
		thisStyle.setWrapText(true);
		
		return thisStyle;
	}
	
	protected HSSFCellStyle createDateStyle() {
		HSSFCellStyle thisStyle = wb.createCellStyle();
		
		thisStyle.setDataFormat(DATE_FORMAT);
		thisStyle.setWrapText(true);
		
		return thisStyle;
	}
	
	protected HSSFCellStyle createNumberStyle() {
		HSSFCellStyle thisStyle = wb.createCellStyle();
		
		thisStyle.setDataFormat(NUMBER_FORMAT);
		thisStyle.setWrapText(true);
		
		return thisStyle;
	}
	
	protected HSSFCellStyle createIntegerStyle() {
		HSSFCellStyle thisStyle = wb.createCellStyle();
		
		thisStyle.setDataFormat(INTEGER_FORMAT);
		thisStyle.setWrapText(true);
		
		return thisStyle;
	}
	
	protected HSSFCellStyle createCurrencyStyle() {
		HSSFCellStyle thisStyle = wb.createCellStyle();
		
		thisStyle.setDataFormat(CURRENCY_FORMAT);
		thisStyle.setWrapText(true);
		
		return thisStyle;
	}
	
	protected HSSFCellStyle createBoldStyle() {
		HSSFCellStyle thisStyle = createTextStyle();

		thisStyle.setFont(BOLD_FONT);
		
		return thisStyle;
	}
	
	protected void populateCoverSheet() throws ServletException {
		int coverRow = 0;
		
		coverRow = addCoverCell(coverRow, "Snapshot ID", snapshotId + "");
		
		coverRow = addParameters(coverRow);
	}
	
	protected int addCoverCell(int coverRow, String... cellContent) {
		HSSFRow row = coverSheet.createRow(coverRow);
		
		for (int cellNum = 0; cellNum < cellContent.length; cellNum++) {
			HSSFCell cell = row.createCell(cellNum);
			cell.setCellValue(cellContent [cellNum]);
			if (cellNum == 0)
				cell.setCellStyle(BOLD_STYLE);
			else
				cell.setCellStyle(TEXT_STYLE);
		}
		
		return ++coverRow;
	}
	
	public int addParameters(int coverRow) throws ServletException {
		
		HSSFRow row = null;
		
		int cellNum = 0;

		HibernateUtil.openSession();
		HibernateUtil.startTransaction();
		
		try {
			String lastParameterName = null;
			
			List<SnapshotParameter> parameterValues = DbSnapshotParameter.findBySource(snapshotId, null);
			for (SnapshotParameter parameterValue : parameterValues) {
				if (lastParameterName == null || !lastParameterName.equalsIgnoreCase(parameterValue.getId().getParameterName())) {
					row = coverSheet.createRow(coverRow++);
					
					cellNum = 0; 
					
					HSSFCell cell = row.createCell(cellNum++);
					cell.setCellStyle(BOLD_STYLE);
					cell.setCellValue(SnapshotParameterNames.getLabel(parameterValue.getId().getParameterName()));
					
					lastParameterName = parameterValue.getId().getParameterName();
				}

				if (parameterValue.getParameterType() == SnapshotParameterValueObject.INTEGER) {
					if (parameterValue.getIntFromValue() == null)
						throw new ServletException("Null value found for " + parameterValue.getId().getParameterName() + " for snapshot " + snapshotId + ".");
					if (parameterValue.getIntToValue() != null && parameterValue.getIntToValue().intValue() > parameterValue.getIntFromValue().intValue())
						addCell(row, cellNum++, parameterValue.getIntFromValue() + "<==>" + parameterValue.getIntToValue());
					else
						addCell(row, cellNum++, parameterValue.getIntFromValue());
					
				} else if (parameterValue.getParameterType() == SnapshotParameterValueObject.DOUBLE) {
					if (parameterValue.getDblFromValue() == null)
						throw new ServletException("Null value found for " + parameterValue.getId().getParameterName() + " for snapshot " + snapshotId + ".");
					if (parameterValue.getDblToValue().doubleValue() > parameterValue.getDblFromValue().doubleValue())
						addCell(row, cellNum++, parameterValue.getDblFromValue() + "<==>" + parameterValue.getDblToValue());
					else
						addCell(row, cellNum++, parameterValue.getDblFromValue());
					
				} else if (parameterValue.getParameterType() == SnapshotParameterValueObject.DATE) {
					if (parameterValue.getDateFromValue() == null)
						throw new ServletException("Null value found for " + parameterValue.getId().getParameterName() + " for snapshot " + snapshotId + ".");
					if (parameterValue.getDateToValue() != null && parameterValue.getDateToValue().after(parameterValue.getDateFromValue()))
						addCell(row, cellNum++, parameterValue.getDateFromValue() + "<==>" + parameterValue.getDateToValue());
					else
						addCell(row, cellNum++, parameterValue.getDateFromValue());
					
				} else if (parameterValue.getParameterType() == SnapshotParameterValueObject.STRING) {
					if (parameterValue.getStrFromValue() == null)
						throw new ServletException("Null value found for " + parameterValue.getId().getParameterName() + " for snapshot " + snapshotId + ".");
					if (parameterValue.getStrToValue() != null && parameterValue.getStrToValue().compareTo(parameterValue.getStrFromValue()) > 0)
						addCell(row, cellNum++, parameterValue.getStrFromValue() + "<==>" + parameterValue.getStrToValue());
					else
						addCell(row, cellNum++, parameterValue.getStrFromValue());
					
				} else if (parameterValue.getParameterType() == SnapshotParameterValueObject.BOOLEAN) {
					if (parameterValue.getIntFromValue() == null)
						throw new IllegalArgumentException("Null value found for " + parameterValue.getId().getParameterName() + " for snapshot " + snapshotId + ".");
					addCell(row, cellNum++, (parameterValue.getIntFromValue() > 0) ? "TRUE" : "FALSE");
					
				} else 
					throw new ServletException("Unrecognized Parameter Type " + parameterValue.getParameterType() + " for " + parameterValue.getId().getParameterName() + " for snapshot " + snapshotId + ".");

			}
		} finally {		
			HibernateUtil.endTransaction();
			HibernateUtil.closeSession();
		}
		
		return coverRow;
	}
	
	protected void startDataSheet() {
//		dataSheet.setDefaultRowHeight((short) 50);
		
		int cellNum = 0;
		
		dataSheet.setColumnWidth(cellNum++, getCharacterWidth(12));
		dataSheet.setColumnWidth(cellNum++, getCharacterWidth(12));
		dataSheet.setColumnWidth(cellNum++, getCharacterWidth(16));
		dataSheet.setColumnWidth(cellNum++, getCharacterWidth(16));
		dataSheet.setColumnWidth(cellNum++, getDollarWidth());
		dataSheet.setColumnWidth(cellNum++, getDateWidth());
		dataSheet.setColumnWidth(cellNum++, getDateWidth());
		dataSheet.setColumnWidth(cellNum++, getDateWidth());
		dataSheet.setColumnWidth(cellNum++, getDollarWidth());
		dataSheet.setColumnWidth(cellNum++, getDollarWidth());
		dataSheet.setColumnWidth(cellNum++, getDollarWidth());
		dataSheet.setColumnWidth(cellNum++, getDollarWidth());
		dataSheet.setColumnWidth(cellNum++, getDollarWidth());
		cellNum = addProductWidths(dataSheet, cellNum);
		cellNum = addServiceWidths(dataSheet, cellNum);
		cellNum = addInstitutionWidths(dataSheet, cellNum);
		
		createHeaders();
	}
	
	protected int getDateWidth() {
		return getCharacterWidth(DATE_WIDTH);
	}
	
	protected int getDollarWidth() {
		return getCharacterWidth(DOLLAR_WIDTH);
	}
	
	protected int getCharacterWidth(int characters) {
		return characters * 256;
	}
	
	protected void createHeaders() {
		HSSFRow row = dataSheet.createRow(rowNum);
		
		int cellNum = 0;
		
		addCell(row, cellNum++, "Agreement #");
		addCell(row, cellNum++, "UCN");
		addCell(row, cellNum++, "Product Code");
		addCell(row, cellNum++, "Service Code");
		addCell(row, cellNum++, "Total Term Dollar Value");
		addCell(row, cellNum++, "Start Date");
		addCell(row, cellNum++, "End Date");
		addCell(row, cellNum++, "Terminate Date");
		addCell(row, cellNum++, "Dollar Fraction");
		addCell(row, cellNum++, "Service Fraction");
		addCell(row, cellNum++, "Dollar Service Fraction");
		addCell(row, cellNum++, "UCN Fraction");
		addCell(row, cellNum++, "Dollar UCN Fraction");
		cellNum = addProductHeadings(row, cellNum);
		cellNum = addServiceHeadings(row, cellNum);
		cellNum = addInstitutionHeadings(row, cellNum);
		
		rowNum++;
	}
	
	protected void processTermData() {
		HibernateUtil.openSession();
		HibernateUtil.startTransaction();

		try {
			
			//	Find only undeleted cancel reasons
			List<SnapshotTermData> snapshotTermDatas = DbSnapshotTermData.findFiltered(snapshotId, -1, -1, null, null, -1, -1, null);

			//	First pass, just load the ancillary tables if it's needed for the main data sheet
			
			if (useLookups) {
				for (SnapshotTermData snapshotTermData : snapshotTermDatas) {
					getInstitution(snapshotTermData.getId().getUcn(), institutionMap);			
					getProduct(snapshotTermData.getId().getProductCode(), productMap);
					getService(snapshotTermData.getId().getServiceCode(), serviceMap);
					getCancelReason(snapshotTermData.getCancelReasonCode(), cancelReasonMap);
					getTermType(snapshotTermData.getTermType(), termTypeMap);
				}
			}
			
			//	Second pass, create the actual data sheet
			
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
		HSSFRow row = dataSheet.createRow(rowNum);
		
		int cellNum = 0;
		
		addCell(row, cellNum++, instance.getAgreementId(), INTEGER_STYLE);
		addCell(row, cellNum++, instance.getUcn(), INTEGER_STYLE);
		addCell(row, cellNum++, instance.getProductCode());
		addCell(row, cellNum++, instance.getServiceCode());
		addCell(row, cellNum++, instance.getDollarValue());
		addCell(row, cellNum++, instance.getStartDate());
		addCell(row, cellNum++, instance.getEndDate());
		addCell(row, cellNum++, instance.getTerminateDate());
		addCell(row, cellNum++, instance.getDollarFraction());
		addCell(row, cellNum++, instance.getServiceFraction());
		addCell(row, cellNum++, instance.getDollarServiceFraction());
		addCell(row, cellNum++, instance.getUcnFraction());
		addCell(row, cellNum++, instance.getDollarUcnFraction());
		if (useLookups) {
			for (int col = 2; col < 5; col++)
				addFormulaCell(row, cellNum++, getLookupFunction(productSheet, "C", row.getRowNum(), productMap.size(), col), TEXT_STYLE);
			for (int col = 2; col < 4; col++)
				addFormulaCell(row, cellNum++, getLookupFunction(serviceSheet, "D", row.getRowNum(), serviceMap.size(), col), TEXT_STYLE);
			for (int col = 2; col < 8; col++)
				addFormulaCell(row, cellNum++, getLookupFunction(institutionSheet, "B", row.getRowNum(), institutionMap.size(), col), TEXT_STYLE);
		} else {
			cellNum = addProductCells(row, cellNum, instance.getProduct());
			cellNum = addServiceCells(row, cellNum, instance.getService());
			cellNum = addInstitutionCells(row, cellNum, instance.getInstitution());
		}
		
		rowNum++;
	}
	
	protected String getLookupFunction(HSSFSheet sheet, String codeCol, int row, int size, int col) {
		return "VLOOKUP(" + codeCol + (row + 1) + "," + sheet.getSheetName() + "!$A$2:$Z$" + (size + 1) + "," + col +")";
	}
	
	protected void addLookupSheets() {
		addProductSheet();
		addServiceSheet();
		addInstitutionSheet();
	}
	
	protected void addInstitutionSheet() {
		int rowNum = 0;
		int cellNum = 0;
		
		institutionSheet.setColumnWidth(cellNum++, getCharacterWidth(CODE_WIDTH));
		cellNum = addInstitutionWidths(institutionSheet, cellNum);
		
		HSSFRow row = institutionSheet.createRow(rowNum++);
		
		cellNum = 0;
		
		addCell(row, cellNum++, "UCN");
		cellNum = addInstitutionHeadings(row, cellNum);
		
		for (Integer ucn : institutionMap.keySet()) {
			
			row = institutionSheet.createRow(rowNum++);
			
			cellNum = 0;
			
			addCell(row, cellNum++, ucn, INTEGER_STYLE);
			addInstitutionCells(row, cellNum, institutionMap.get(ucn));
		}
	}
	
	protected int addInstitutionWidths(HSSFSheet sheet, int cellNum) {
		sheet.setColumnWidth(cellNum++, getCharacterWidth(DESCRIPTION_WIDTH));
		sheet.setColumnWidth(cellNum++, getCharacterWidth(DESCRIPTION_WIDTH));
		sheet.setColumnWidth(cellNum++, getCharacterWidth(DESCRIPTION_WIDTH));
		sheet.setColumnWidth(cellNum++, getCharacterWidth(CITY_WIDTH));
		sheet.setColumnWidth(cellNum++, getCharacterWidth(STATE_WIDTH));
		sheet.setColumnWidth(cellNum++, getCharacterWidth(COUNTRY_WIDTH));
		return cellNum;
	}
	
	protected int addInstitutionHeadings(HSSFRow row, int cellNum) {
		addCell(row, cellNum++, "Name");
		addCell(row, cellNum++, "Address 1");
		addCell(row, cellNum++, "Address 2");
		addCell(row, cellNum++, "City");
		addCell(row, cellNum++, "State");
		addCell(row, cellNum++, "Country");
		return cellNum;
	}
	
	protected int addInstitutionCells(HSSFRow row, int cellNum, InstitutionInstance institution) {
		addCell(row, cellNum++, institution.getInstitutionName());
		addCell(row, cellNum++, institution.getAddress1());
		addCell(row, cellNum++, institution.getAddress2());
		addCell(row, cellNum++, institution.getCity());
		addCell(row, cellNum++, institution.getState());
		addCell(row, cellNum++, institution.getCountry());
		return cellNum;
	}
	
	protected void addProductSheet() {
		int rowNum = 0;
		int cellNum = 0;
		
		productSheet.setColumnWidth(cellNum++, getCharacterWidth(CODE_WIDTH));
		cellNum = addProductWidths(productSheet, cellNum);
		
		HSSFRow row = productSheet.createRow(rowNum++);
		
		cellNum = 0;
		
		addCell(row, cellNum++, "Product Code");
		cellNum = addProductHeadings(row, cellNum);
		
		for (String productCode : productMap.keySet()) {
			
			row = productSheet.createRow(rowNum++);
			
			cellNum = 0;
			
			addCell(row, cellNum++, productCode);
			addProductCells(row, cellNum, productMap.get(productCode));
		}
	}
	
	protected int addProductWidths(HSSFSheet sheet, int cellNum) {
		sheet.setColumnWidth(cellNum++, getCharacterWidth(DESCRIPTION_WIDTH));
		sheet.setColumnWidth(cellNum++, getCharacterWidth(CODE_WIDTH));
//		sheet.setColumnWidth(cellNum++, getCharacterWidth(DESCRIPTION_WIDTH));
		sheet.setColumnWidth(cellNum++, getCharacterWidth(CODE_WIDTH));
//		sheet.setColumnWidth(cellNum++, getCharacterWidth(DESCRIPTION_WIDTH));
		return cellNum;
	}
	
	protected int addProductHeadings(HSSFRow row, int cellNum) {
		addCell(row, cellNum++, "Product");
		addCell(row, cellNum++, "Default Term Type Code");
//		addCell(row, cellNum++, "Default Term Type");
		addCell(row, cellNum++, "Default Commission Type Code");
//		addCell(row, cellNum++, "Default Commission Type");
		return cellNum;
	}
	
	protected int addProductCells(HSSFRow row, int cellNum, ProductInstance product) {
		addCell(row, cellNum++, product.getDescription());
		addCell(row, cellNum++, product.getDefaultTermType());
//		addCell(row, cellNum++, product.getDefaultTermTypeInstance().getDescription());
		addCell(row, cellNum++, product.getDefaultCommissionCode());
//		addCell(row, cellNum++, product.getDefaultCommTypeInstance().getDescription());
		return cellNum;
	}
	
	protected void addServiceSheet() {
		int rowNum = 0;
		int cellNum = 0;
		
		serviceSheet.setColumnWidth(cellNum++, getCharacterWidth(CODE_WIDTH));
		cellNum = addServiceWidths(serviceSheet, cellNum);
		
		HSSFRow row = serviceSheet.createRow(rowNum++);
		
		cellNum = 0;
		
		addCell(row, cellNum++, "Service Code");
		cellNum = addServiceHeadings(row, cellNum);
		
		for (String serviceCode : serviceMap.keySet()) {
			
			row = serviceSheet.createRow(rowNum++);
			
			cellNum = 0;
			
			addCell(row, cellNum++, serviceCode);
			addServiceCells(row, cellNum, serviceMap.get(serviceCode));
		}
	}
	
	protected int addServiceWidths(HSSFSheet sheet, int cellNum) {
		sheet.setColumnWidth(cellNum++, getCharacterWidth(DESCRIPTION_WIDTH));
		sheet.setColumnWidth(cellNum++, getCharacterWidth(CODE_WIDTH));
//		sheet.setColumnWidth(cellNum++, getCharacterWidth(DESCRIPTION_WIDTH));
		return cellNum;
	}
	
	protected int addServiceHeadings(HSSFRow row, int cellNum) {
		addCell(row, cellNum++, "Service");
		addCell(row, cellNum++, "Service Type Code");
//		addCell(row, cellNum++, "Service Type");
		return cellNum;
	}
	
	protected int addServiceCells(HSSFRow row, int cellNum, ServiceInstance service) {
		addCell(row, cellNum++, service.getDescription());
		addCell(row, cellNum++, service.getServiceType() + "");
//		addCell(row, cellNum++, service.getServiceTypeName());
		return cellNum;
	}
	
	protected int addFormulaCell(HSSFRow row, int cellNum, String formula, HSSFCellStyle style) {
		HSSFCell cell = getCell(row, cellNum);
		cell.setCellStyle(style);
		cell.setCellFormula(formula);
		return ++cellNum;
	}
	
	protected int addCell(HSSFRow row, int cellNum, int data) {
		return addCell(row, cellNum, data, NUMBER_STYLE);
	}
	
	protected int addCell(HSSFRow row, int cellNum, int data, HSSFCellStyle style) {
		HSSFCell cell = getCell(row, cellNum);
		cell.setCellStyle(style);
		cell.setCellValue(data);
		return ++cellNum;
	}
	
	protected int addCell(HSSFRow row, int cellNum, double data) {
		return addCell(row, cellNum, data, CURRENCY_STYLE);
	}

	protected int addCell(HSSFRow row, int cellNum, double data, HSSFCellStyle style) {
		HSSFCell cell = getCell(row, cellNum, style);
		cell.setCellValue(data);
		return ++cellNum;
	}
	
	protected int addCell(HSSFRow row, int cellNum, String data) {
		getCell(row, cellNum).setCellValue(data);
		return ++cellNum;
	}
	
	protected int addCell(HSSFRow row, int cellNum, Date data) {
		HSSFCell cell = getCell(row, cellNum, DATE_STYLE);
		cell.setCellValue(data);	
		return ++cellNum;
	}
	
	protected HSSFCell getCell(HSSFRow row, int cellNum) {
		if (cellNum == 0 || row.getRowNum() == 0)
			return getCell(row, cellNum, BOLD_STYLE);
		else
			return getCell(row, cellNum, TEXT_STYLE);
	}
	
	protected HSSFCell getCell(HSSFRow row, int cellNum, HSSFCellStyle style) {
		HSSFCell cell = row.createCell(cellNum);
		cell.setCellStyle(style);
		return cell;
	}
	
	protected ServiceInstance getService(String serviceCode, TreeMap<String, ServiceInstance> serviceMap) {
		if (serviceCode == null || serviceCode.length() == 0)
			return null;
		
		if (serviceMap.containsKey(serviceCode))
			return serviceMap.get(serviceCode);
		
		Service service = DbService.getByCode(serviceCode);
		if (service != null) {
			serviceMap.put(serviceCode, DbService.getInstance(service));
			return serviceMap.get(serviceCode);
		}
		
		return null;
	}
	
	protected ProductInstance getProduct(String productCode, TreeMap<String, ProductInstance> productMap) {
		if (productCode == null || productCode.length() == 0)
			return null;
		
		if (productMap.containsKey(productCode))
			return productMap.get(productCode);
		
		Product product = DbProduct.getByCode(productCode);
		if (product != null) {
			productMap.put(productCode, DbProduct.getInstance(product));
			return productMap.get(productCode);
		}
		
		return null;
	}
	
	protected CancelReasonInstance getCancelReason(String cancelReasonCode, TreeMap<String, CancelReasonInstance> cancelReasonMap) {
		if (cancelReasonCode == null || cancelReasonCode.length() == 0)
			return null;
		
		if (cancelReasonMap.containsKey(cancelReasonCode))
			return cancelReasonMap.get(cancelReasonCode);
		
		CancelReason cancelReason = DbCancelReason.getByCode(cancelReasonCode);
		if (cancelReason != null) {
			cancelReasonMap.put(cancelReasonCode, DbCancelReason.getInstance(cancelReason));
			return cancelReasonMap.get(cancelReasonCode);
		}
		
		return null;
	}
	
	protected InstitutionInstance getInstitution(int ucn, TreeMap<Integer, InstitutionInstance> institutionMap) {
		if (ucn <= 0)
			return null;
		
		if (institutionMap.containsKey(ucn))
			return institutionMap.get(ucn);
		
		Institution institution = DbInstitution.getByCode(ucn);
		if (institution != null) {
			institutionMap.put(ucn, DbInstitution.getInstance(institution));
			return institutionMap.get(ucn);
		}
		
		return null;
	}
	
	protected TermTypeInstance getTermType(String termTypeCode, TreeMap<String, TermTypeInstance> termTypeMap) {
		if (termTypeCode == null || termTypeCode.length() == 0)
			return null;
		
		if (termTypeMap.containsKey(termTypeCode))
			return termTypeMap.get(termTypeCode);
		
		TermType termType = DbTermType.getByCode(termTypeCode);
		if (termType != null) {
			termTypeMap.put(termTypeCode, DbTermType.getInstance(termType));
			termTypeMap.get(termTypeCode);
		}
		
		return null;
	}

	public boolean usesLookups() {
		return useLookups;
	}

	public void setUseLookups(boolean useLookups) {
		this.useLookups = useLookups;
	}
	
	
}
