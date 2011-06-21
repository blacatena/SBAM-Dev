package com.scholastic.sbam.server.authentication;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import com.scholastic.sbam.server.database.codegen.AeControl;
import com.scholastic.sbam.server.database.codegen.AePrefCode;
import com.scholastic.sbam.server.database.codegen.AePrefCodeId;
import com.scholastic.sbam.server.database.codegen.PreferenceCategory;
import com.scholastic.sbam.server.database.codegen.PreferenceCode;
import com.scholastic.sbam.server.database.codegen.Service;
import com.scholastic.sbam.server.database.codegen.SysConfig;
import com.scholastic.sbam.server.database.objects.DbAeControl;
import com.scholastic.sbam.server.database.objects.DbAePrefCode;
import com.scholastic.sbam.server.database.objects.DbPreferenceCategory;
import com.scholastic.sbam.server.database.objects.DbPreferenceCode;
import com.scholastic.sbam.server.database.objects.DbService;
import com.scholastic.sbam.server.database.objects.DbSysConfig;
import com.scholastic.sbam.server.database.util.HibernateAccessor;
import com.scholastic.sbam.server.database.util.HibernateUtil;
import com.scholastic.sbam.server.database.util.SqlConstructor;
import com.scholastic.sbam.server.database.util.SqlExecution;
import com.scholastic.sbam.server.util.AppServerConstants;
import com.scholastic.sbam.server.util.ExportController;
import com.scholastic.sbam.shared.exceptions.AuthenticationExportException;
import com.scholastic.sbam.shared.objects.ExportProcessMessage;
import com.scholastic.sbam.shared.objects.ExportProcessReport;
import com.scholastic.sbam.shared.util.AppConstants;

/**
 * This class initiates the creation of all authentication export data.
 * 
 * Note that a public static main(String []) method is implemented so that the task can be run as a stand alone java APP (not yet complete).
 * 
 * Otherwise, the export must be initiated as a thread, using beginThreadedExport.
 * 
 * @author Bob Lacatena
 *
 */
public class AuthenticationGenerator implements Runnable, ExportController {
	
	public static final int					DEBUG_COUNT = 500;
	public static final	int					GARBAGE_COLLECTION_POINT = 500;
	
	public static final char				RUNNING	= 'r';
	public static final char				ABORTED = 'a';
	public static final char				ERROR	= 'e';
	public static final char				COMPLETE= 'c';
	public static final char				DEBUG	= 'd';
	
	protected static AuthenticationGenerator singleton;
	
	protected boolean						consoleOutputOn = true;
	protected int							countIncrement = 0;
	protected boolean 						running;
	protected String						terminationReason;
	protected String						terminationUserId;
	protected ExportProcessReport			currentExportReport;
	protected List<ExportProcessReport>		exportReportLog = new ArrayList<ExportProcessReport>();
	
	protected char							ucnMode	= 0;
	protected Date							asOfDate = new Date();
	protected boolean						debugMode = false;
	
	protected String						exportDirectory = "/tmp";
	
	protected AeControl						aeControl;
	protected AeControl						lastCompleteAeControl;
	
	protected List<AuthenticationConflict>	conflicts = new ArrayList<AuthenticationConflict>();
	
	protected AuthenticationGenerator() {
		
	}
	
	public static void main(String [] args) {

		if (args != null)
			for (String arg : args) {
				if ("-help".equals(arg)) {
					showHelp();
					System.exit(0);
				}
				if (! ( "-silent".equals(arg) || "-verbose".equals(arg) || "-ucn".equals(arg) || "-legacy".equals(arg) || "-debug".equals(arg) || AppConstants.isNumeric(arg) || AppConstants.isDate(arg) || isPath(arg)) ) {
					System.out.println("Invalid parameter " + arg + " (use -help to list valid parameters).");
					System.exit(0);
				}
			}
		//	Generate Export
		
		AuthenticationGenerator auGen = new AuthenticationGenerator();
		
		auGen.forceConsoleOutput("Authentication Generator running...");
		
		auGen.setConsoleOutputOn(false);
		
		if (args != null)
			for (String arg : args) {
				if ("-silent".equals(arg))
					auGen.setConsoleOutputOn(false);
				else if ("-verbose".equals(arg))
					auGen.setConsoleOutputOn(true);
				else if ("-ucn".equals(arg))
					auGen.setUcnMode(EXPORT_UCNS);
				else if ("-legacy".equals(arg))
					auGen.setUcnMode(LEGACY_CUSTOMER_CODES);
				else if (AppConstants.isNumeric(arg))
					auGen.setCountIncrement(Integer.parseInt(arg));
				else if ("-debug".equals(arg)) {
					auGen.setDebugMode(true);
					auGen.forceConsoleOutput("DEBUG MODE: Processing limited to " + DEBUG_COUNT + " agreements.");
				} else if (AppConstants.isDate(arg)) {
						try {
							auGen.setAsOfDate(arg);
							auGen.forceConsoleOutput("Running for  AS OF DATE " + formatDate(auGen.getAsOfDate()) + ".");
						} catch (ParseException e) {
							System.out.println("Invalid date as of date " + arg );
							System.exit(1);
						}
				}
			}
		
		auGen.generateExport();
		
		auGen.forceConsoleOutput("Authentication Generator execution completed.");
	}
	
	protected static boolean isPath(String arg) {
		if (arg == null)
			return false;
		return (arg.length() > 0 && arg.charAt(0) == '/') 
			|| (arg.length() > 1 && arg.startsWith("./")) 
			|| (arg.length() > 2 && arg.startsWith("../")) ;
	}
	
	protected static void showHelp() {
		System.out.println("-silent    To run without output (default).");
		System.out.println("-verbose   To run with specific progress messages.");
		System.out.println("-ucn       To generate the export with UCNs.");
		System.out.println("-legacy    To generate the export with legacy (Global) customer codes.");
		System.out.println("n          Where n is any positive, non-zero number, to output agreement, site and method counts after every n processed agreements.");
		System.out.println("-debug     Speeds processing by restricting the run to " + DEBUG_COUNT + " agreements.");
		System.out.println("yyyy-mm-dd Date as of which to execute this generation.  USE WITH CAUTION AND FULL KNOWLEDGE OF WHAT YOU'RE DOING!!!");
		System.out.println("-help      To show this help list.");
	}
	
	protected String generateExport() {
		
		if (running)
			return "The export is already running.";
		
		running = true;
		currentExportReport = new ExportProcessReport();
		exportReportLog.add(currentExportReport);
		currentExportReport.setStarted();
		
		try {
			loadSysConfigParms();
			
			createAeControl();
			
			loadLastCompleteAeControl();
			
			if (lastCompleteAeControl != null && lastCompleteAeControl.getAsOfDate().after(asOfDate))
				forceConsoleOutput("WARNING: Running for a previous date is NOT recommented (last complete run was for " + lastCompleteAeControl.getAsOfDate() + ").");

			//	Cycle through all open contracts
			
			//	This SQL finds all active agreements
			SqlConstructor mainSql = new SqlConstructor();
			mainSql.append("select distinct agreement.id ");
			mainSql.append("from agreement, agreement_term, product, product_service, service");
			
			mainSql.addCondition("agreement.status <> ", AppConstants.STATUS_DELETED);
			mainSql.addCondition("agreement.status <> ", AppConstants.STATUS_INACTIVE);
			mainSql.addCondition("agreement.id = agreement_term.agreement_id");
			mainSql.addCondition("agreement_term.status =", AppConstants.STATUS_ACTIVE);
			mainSql.addCondition("agreement_term.start_date is not null");
			mainSql.addCondition("agreement_term.start_date <=", asOfDate);
			mainSql.addCondition("agreement_term.terminate_date is not null");
			mainSql.addCondition("agreement_term.terminate_date >", asOfDate);
			mainSql.addCondition("agreement_term.product_code = product.product_code");
			mainSql.addCondition("product.status =", AppConstants.STATUS_ACTIVE);
			mainSql.addCondition("product.product_code = product_service.product_code");
			mainSql.addCondition("product_service.status =", AppConstants.STATUS_ACTIVE);
			mainSql.addCondition("product_service.service_code = service.service_code");
			mainSql.addCondition("service.status =", AppConstants.STATUS_ACTIVE);
			mainSql.append("order by agreement.id");
			
			SqlExecution mainLoopExec = new SqlExecution(mainSql.getSql());
			
			while (mainLoopExec.getResults().next()) {
				
				if (terminationReason != null) {
					consoleOutput("Termination requested : " + terminationReason);
					currentExportReport.addError("Process terminating by user request : " + terminationUserId + " (" + terminationReason + ")");
					currentExportReport.setCompleted("terminated abnormally");
					closeAeControlAbort();
					return "Export terminated by user request.";
				}
				
				processAgreement(mainLoopExec.getResults().getBigDecimal("id").intValue());
				
				if (debugMode && currentExportReport.getAgreements() > DEBUG_COUNT) {
					forceConsoleOutput("Debug mode on and debug limit reached.");
					break;
				}
				
				if (currentExportReport.getAgreements() % GARBAGE_COLLECTION_POINT == 0) {
					Runtime.getRuntime().gc();
					forceConsoleOutput("Total memory: " + Runtime.getRuntime().totalMemory() + 
									" : Free  memory: " + Runtime.getRuntime().freeMemory() + 
									" : Max   memory: " + Runtime.getRuntime().maxMemory());
				}
				
			}
			
			mainLoopExec.close();
			
			forceConsoleOutput(currentExportReport.getAgreements() + " agreements processed");
			currentExportReport.addMessage(currentExportReport.getAgreements() + " agreements processed");
			
			resolveConflicts();
			
			mergeAuthUnits();
			
			translateAuthUnits();
			
			copyPrefCodes();
			
			generateExportFiles();
			
		} catch (SQLException e) {
			e.printStackTrace();
			currentExportReport.addError(e.getMessage());
			currentExportReport.setCompleted("terminated with a SQL error");
			running = false;
			closeAeControlError();
			return "Export failed.";
		} catch (Exception e) {
			e.printStackTrace();
			currentExportReport.addError(e.getMessage());
			currentExportReport.setCompleted("terminated with an error");
			running = false;
			closeAeControlError();
			return "Export failed.";
		} finally {
			running = false;
		}
		
		currentExportReport.setCompleted();
		closeAeControlComplete();
		running = false;
		
//		generateReportErrors();
		
		generateReportCounts();
		
		return "Export complete.";
	}
	
	protected void loadSysConfigParms() throws Exception {
		//	If the mode is already set, just use what's there
		if (ucnMode != 0)
			return;

		// Load if available interactively from database
		
		HibernateUtil.openSession();
		HibernateUtil.startTransaction();

		try {
			SysConfig sysConfig = DbSysConfig.getActive();
			if (sysConfig != null) {
				ucnMode = sysConfig.getAeUcnMode();
			} else {
				ucnMode = AppServerConstants.getDefaultAeUcnMode();
			}
		} catch (Exception exc) {
			exc.printStackTrace();
			throw exc;
		}
		
		if (ucnMode == 0)
			throw new Exception("No UCN export mode found.");
		
		if (VALID_UCN_MODES.indexOf(ucnMode) < 0)
			throw new Exception("Invalid UCN export mode '" + ucnMode + "'.");
		
		HibernateUtil.endTransaction();
		HibernateUtil.closeSession();
		
		forceConsoleOutput("Export being generated with " + (ucnMode == 'u' ? "UCNs" : "old (Global) codes") + " as customer codes.");
	}
	
	protected void createAeControl() {
		HibernateUtil.openSession();
		HibernateUtil.startTransaction();
		
		aeControl = new AeControl();
		
		aeControl.setCreatedDatetime(new Date());
		aeControl.setInitiatedDatetime(new Date());
		aeControl.setStatus(RUNNING);
		
		aeControl.setUcnMode(ucnMode);
		aeControl.setAsOfDate(asOfDate);
		
		HibernateAccessor.persist(aeControl);
		
		HibernateUtil.endTransaction();
		HibernateUtil.closeSession();
		
		forceConsoleOutput("Authentication Export ID " + aeControl.getAeId() + " assigned.");
	}
	
	protected void closeAeControlAbort() {
		closeAeControl(true, false);
	}
	
	protected void closeAeControlError() {
		closeAeControl(false, true);
	}
	
	protected void closeAeControlComplete() {
		closeAeControl(false, false);
	}
	
	protected void closeAeControl(boolean aborted, boolean error) {
		HibernateUtil.openSession();
		HibernateUtil.startTransaction();
		
		HibernateAccessor.refresh(aeControl);
		
		if (error) {
			aeControl.setStatus(ERROR);
		} else if (aborted) {
			aeControl.setStatus(ABORTED);
		} else {
			if (debugMode)
				aeControl.setStatus(DEBUG);
			else
				aeControl.setStatus(COMPLETE);
			aeControl.setCompletedDatetime(new Date());
		}
		
		aeControl.setTerminatedDatetime(new Date());
		aeControl.setElapsedSeconds((int) (aeControl.getTerminatedDatetime().getTime() - aeControl.getInitiatedDatetime().getTime()));
		
		aeControl.setCountAgreements(currentExportReport.getAgreements());
		aeControl.setCountSites(currentExportReport.getSites());
		aeControl.setCountIps(currentExportReport.getIps());
		aeControl.setCountUids(currentExportReport.getUids());
		aeControl.setCountUrls(currentExportReport.getUrls());
		
		aeControl.setCountErrors(currentExportReport.getErrors());
		
		HibernateAccessor.persist(aeControl);
		
		HibernateUtil.endTransaction();
		HibernateUtil.closeSession();
	}
	
	protected void loadLastCompleteAeControl() throws Exception {
		HibernateUtil.openSession();
		HibernateUtil.startTransaction();
		
		lastCompleteAeControl = DbAeControl.getLastComplete();
		
		HibernateUtil.endTransaction();
		HibernateUtil.closeSession();
	}
	
	protected void processAgreement(int agreementId) {
		
		HibernateUtil.openSession();
		HibernateUtil.startTransaction();

		consoleOutput("Process Agreement " + agreementId);
		
		try {
			new AuthenticationExportAgreement(agreementId, this, currentExportReport).exportSites();
		} catch (AuthenticationExportException exc) {
			currentExportReport.addError(exc.getMessage());
		}
		
		if (HibernateUtil.isTransactionInProgress()) {
			HibernateUtil.endTransaction();
		}
		HibernateUtil.closeSession();
		
		if (countIncrement > 0 && currentExportReport.getAgreements() % countIncrement == 0)
			forceConsoleOutput("...... " + currentExportReport.getAgreements() + " agreements, " + currentExportReport.getSites() + " sites, " + 
					(currentExportReport.getIps() + currentExportReport.getUids() + currentExportReport.getUrls()) + " methods.");
	}

	@Override
	public void run() {
		System.out.println(new Date() + " : Export thread running.");
		String result = generateExport();
		consoleOutput(result);
		System.out.println(new Date() + " : Export thread ended.");
	}
	
	/**
	 * Start a new export running in a background (daemon) thread.
	 * @return
	 * True if a new thread was initiated.  False if another thread is already running.
	 */
	public synchronized boolean beginThreadedExport(boolean consoleOutputOn) {
		if (running)
			return false;
		
		this.consoleOutputOn = consoleOutputOn;
		
		System.out.println(new Date() + " : Kicking off authentication export thread...");	// Not "console output" -- can't be turned off
		
		Thread initThread = new Thread(this);
		initThread.setDaemon(false);		//	We use false, to that an export process cannot be interrupted.  This may be changed to true if it becomes an issue.
		initThread.start();
		
		System.out.println(new Date() + " : Export thread started.");	// Not "console output" -- can't be turned off
		
		return true;
	}
	
	@Override
	public void addConflict(AuthenticationConflict conflict) {
		conflicts.add(conflict);
	}
	
	/**
	 * Copy all current preference code settings for the export
	 */
	public void copyPrefCodes() {
		forceConsoleOutput("Copy service and preference code definitions.");
		
		HibernateUtil.openSession();
		HibernateUtil.startTransaction();
		
		//	Process all active services (termed "products" in the old export and existing auth processes)
		
		List<Service> services = DbService.findAll();
		
		for (Service service : services) {
			if (service.getStatus() == AppConstants.STATUS_ACTIVE) {		
				AePrefCode aeCode = new AePrefCode();
				
				AePrefCodeId aeCodeId = new AePrefCodeId();
				aeCodeId.setAeId(aeControl.getAeId());
				aeCodeId.setPrefCode(service.getServiceCode());
				
				aeCode.setId(aeCodeId);
				aeCode.setDescription(service.getDescription());
				aeCode.setDefaultValue("y");
				
				DbAePrefCode.persist(aeCode);
				
				currentExportReport.countServices();
			}
		}

		//	Process all active preference categories (NOTE: Old import terminology calls them "codes", but new distinction calls them "categories")
		
		List<PreferenceCategory> cats = DbPreferenceCategory.findAll();
		
		for (PreferenceCategory cat : cats) {
			if (cat.getStatus() == AppConstants.STATUS_ACTIVE) {
				String defaultValue = "";
				List<PreferenceCode> values = DbPreferenceCode.findByCategory(cat.getPrefCatCode(), AppConstants.STATUS_DELETED);
				for (PreferenceCode value : values) {
					if (value.getStatus() == AppConstants.STATUS_ACTIVE) {
						//	First active product, sorted by sequence number, is the default
						defaultValue = value.getId().getPrefSelCode();
						break;
					}
				}
				
				
				AePrefCode aeCode = new AePrefCode();
				
				AePrefCodeId aeCodeId = new AePrefCodeId();
				aeCodeId.setAeId(aeControl.getAeId());
				aeCodeId.setPrefCode(cat.getPrefCatCode());
				
				aeCode.setId(aeCodeId);
				aeCode.setDescription(cat.getDescription());
				aeCode.setDefaultValue(defaultValue);
				
				DbAePrefCode.persist(aeCode);
				
				currentExportReport.countPreferences();
			}
		}
		
		HibernateUtil.endTransaction();
		HibernateUtil.closeSession();
	}
	
	/**
	 * Resolve all collisions and conflicts in the generated data.
	 */
	public void resolveConflicts() {
		AuthenticationConflictResolver resolver = new AuthenticationConflictResolver(this, currentExportReport);
		
		resolver.resolveConflicts();
	}
	
	/**
	 * Resolve all collisions and conflicts in the generated data.
	 * @throws SQLException 
	 */
	public void mergeAuthUnits() throws SQLException {
		AuthenticationMerger merger = new AuthenticationMerger(this, currentExportReport);

		merger.mergeAuthUnits();
	}
	
	public void translateAuthUnits() {
		AuthenticationAuthUnitTranslator translator = new AuthenticationAuthUnitTranslator(this, currentExportReport);

		translator.translateAuthUnits();
	}
	
	/**
	 * Copy all data to flat files, for transfer to an authentication system.
	 * @throws IOException 
	 */
	public void generateExportFiles() throws Exception {
		AuthenticationFileExporter exporter = new AuthenticationFileExporter(this, currentExportReport);
		
		exporter.exportAuthenticationFiles();
	}
	
	public void generateReportCounts() {
		forceConsoleOutput("========================== Export Counts ===========================");
		forceConsoleOutput("              Agreements: " + currentExportReport.getAgreements());
		forceConsoleOutput("  Single Site Agreements: " + currentExportReport.getSingleSiteAgreementCount());
		forceConsoleOutput("   No Default Agreements: " + currentExportReport.getNoDefaultSiteAgreementCount());
		forceConsoleOutput();
		forceConsoleOutput("               Total AUs: " + currentExportReport.getAuthUnits());
		forceConsoleOutput("     AU...    reuse same: " + currentExportReport.getAuCountReuseSameProduct());
		forceConsoleOutput("     AU...  use existing: " + currentExportReport.getAuCountExistingAgreement());
		forceConsoleOutput("     AU...       created: " + currentExportReport.getAuCountCreatedThisExport());
		forceConsoleOutput("     AU...       similar: " + currentExportReport.getAuCountReuseSimilarProducts());
		forceConsoleOutput("     AU...   prev unused: " + currentExportReport.getAuCountReusePrevUnusedAuCount());
		forceConsoleOutput("     AU... random resuse: " + currentExportReport.getAuCountReusePrevRandom());
		forceConsoleOutput();
		forceConsoleOutput("                   Sites: " + currentExportReport.getSites());
		forceConsoleOutput("        Site Preferences: " + currentExportReport.getSitePrefs());
		forceConsoleOutput("                     IPs: " + currentExportReport.getIps());
		forceConsoleOutput("                    UIDs: " + currentExportReport.getUids());
		forceConsoleOutput("            (Proxy UIDs): " + currentExportReport.getPuids());
		forceConsoleOutput("                    URLs: " + currentExportReport.getUrls());
		forceConsoleOutput("       Remote Setup URLs: " + currentExportReport.getRsUrls());
		forceConsoleOutput("              IP Entries: " + currentExportReport.getIpEntries());
		forceConsoleOutput("            PUID Entries: " + currentExportReport.getPuidEntries());
		forceConsoleOutput();
		forceConsoleOutput("            IP Conflicts: " + currentExportReport.getIpConflicts());
		forceConsoleOutput("           UID Conflicts: " + currentExportReport.getUidConflicts());
		forceConsoleOutput("     Proxy UID Conflicts: " + currentExportReport.getPuidConflicts());
		forceConsoleOutput("           URL Conflicts: " + currentExportReport.getUrlConflicts());
		forceConsoleOutput();
		forceConsoleOutput("           IP Duplicates: " + currentExportReport.getIpDuplicates());
		forceConsoleOutput("          UID Duplicates: " + currentExportReport.getUidDuplicates());
		forceConsoleOutput("    Proxy UID Duplicates: " + currentExportReport.getPuidDuplicates());
		forceConsoleOutput("          URL Duplicates: " + currentExportReport.getUrlDuplicates());
		forceConsoleOutput("    RemoteSetup URL Dups: " + currentExportReport.getRsUrlDuplicates());
		forceConsoleOutput();
		forceConsoleOutput("      Bad Customer Codes: " + currentExportReport.getBadCustomerCodes());
		forceConsoleOutput("    AUs w/ bad customers: " + currentExportReport.getBadCustomerCodeAus());
		forceConsoleOutput("          AUs translated: " + currentExportReport.getAeAuCopied());
		forceConsoleOutput();
		forceConsoleOutput("     Services (Products): " + currentExportReport.getServices());
		forceConsoleOutput("             Preferences: " + currentExportReport.getPreferences());
		forceConsoleOutput();
		forceConsoleOutput("          AUs translated: " + currentExportReport.getAeAuCopied());
		forceConsoleOutput("              AUs merged: " + currentExportReport.getAeAuMerged());
		forceConsoleOutput();
		forceConsoleOutput("              AU exports: " + currentExportReport.getAuWrites());
		forceConsoleOutput("   AU Preference exports: " + currentExportReport.getAuPrefWrites());
		forceConsoleOutput(" Preference Code exports: " + currentExportReport.getPrefCodeWrites());
		forceConsoleOutput("        Customer exports: " + currentExportReport.getCstWrites());
		forceConsoleOutput("              IP exports: " + currentExportReport.getIpWrites());
		forceConsoleOutput("             UID exports: " + currentExportReport.getUidWrites());
		forceConsoleOutput("       Proxy UID exports: " + currentExportReport.getPuidWrites());
		forceConsoleOutput("             URL exports: " + currentExportReport.getUrlWrites());
		forceConsoleOutput("Remote Setup URL exports: " + currentExportReport.getRsUrlWrites());
		forceConsoleOutput();
		forceConsoleOutput("                 Errors: " + currentExportReport.getErrors());
		forceConsoleOutput("              Conflicts: " + conflicts.size());
		forceConsoleOutput("        Elapsed seconds: " + currentExportReport.getElapsedSeconds());
		forceConsoleOutput("        Elapsed minutes: " + currentExportReport.getElapsedMinutes());
		forceConsoleOutput("======================================================================");
	}
	
	public void generateReportErrors() {
		forceConsoleOutput("=================================");
		for (ExportProcessMessage message : currentExportReport.getMessages()) {
			forceConsoleOutput(message.getMessage());
		}
		forceConsoleOutput("=================================");
	}
	
	public void forceConsoleOutput() {
		forceConsoleOutput("");
	}
	
	@Override
	public void forceConsoleOutput(String message) {
		consoleOutput(message, true);
		if (currentExportReport != null)
			currentExportReport.addMessage(message);
	}
	
	@Override
	public void consoleOutput(String message) {
		consoleOutput(message, consoleOutputOn);
	}
	
	public void consoleOutput(String message, boolean consoleOutputOn) {
		if (!consoleOutputOn)
			return;
		System.out.println(new Date() + " : " + (aeControl != null?"" + aeControl.getAeId():"?") + " : Authentication Export : " + message);
	}
	
	public static synchronized AuthenticationGenerator getInstance() {
		if (singleton == null)
			singleton = new AuthenticationGenerator();
		return singleton;
	}
	
	public String getExportStatus() {
		if (!running) {
			if (currentExportReport != null)
				return "The most recent export " + currentExportReport.getStatus() + " " + currentExportReport.getTimeCompleted();
			else
				return "No export has been initiated.";
		} else
			return "There is an export currently executing.";
	}

	public boolean isRunning() {
		return running;
	}

	public ExportProcessReport getCurrentExportReport() {
		return currentExportReport;
	}
	
	public void clearExportReportLog() {
		exportReportLog.clear();
		//	Always add back the last report
		if (currentExportReport != null)
			exportReportLog.add(currentExportReport);
	}

	public List<ExportProcessReport> getExportReportLog() {
		return exportReportLog;
	}

	public void setExportReportLog(List<ExportProcessReport> exportReportLog) {
		this.exportReportLog = exportReportLog;
	}

	public String getTerminationReason() {
		return terminationReason;
	}

	public void setTerminationRequest(String terminationUserId, String terminationReason) {
		this.terminationUserId = terminationUserId;
		this.terminationReason = terminationReason;
		if (currentExportReport != null)
			currentExportReport.addError("Termination requested [" + terminationReason + "]");
	}

	public String getTerminationUserId() {
		return terminationUserId;
	}

	public boolean isConsoleOutputOn() {
		return consoleOutputOn;
	}

	public void setConsoleOutputOn(boolean consoleOutputOn) {
		this.consoleOutputOn = consoleOutputOn;
	}

	public char getUcnMode() {
		return ucnMode;
	}

	public void setUcnMode(char ucnMode) {
		this.ucnMode = ucnMode;
	}

	public int getCountIncrement() {
		return countIncrement;
	}

	public void setCountIncrement(int countIncrement) {
		this.countIncrement = countIncrement;
	}

	@Override
	public AeControl getLastCompleteAeControl() {
		return lastCompleteAeControl;
	}

	@Override
	public AeControl getAeControl() {
		return aeControl;
	}
	
	public static String formatDate(Date date) {
		return new SimpleDateFormat("yyyy-MM-dd").format(date);
	}

	public Date getAsOfDate() {
		return asOfDate;
	}

	public void setAsOfDate(Date asOfDate) {
		this.asOfDate = asOfDate;
	}
	
	public void setAsOfDate(String asOfDate) throws ParseException {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    	this.asOfDate = format.parse(asOfDate);
	}

	public boolean isDebugMode() {
		return debugMode;
	}

	public void setDebugMode(boolean debugMode) {
		this.debugMode = debugMode;
	}

	public String getExportDirectory() {
		return exportDirectory;
	}

	public void setExportDirectory(String exportDirectory) {
		this.exportDirectory = exportDirectory;
	}
}
