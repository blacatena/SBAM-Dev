package com.scholastic.sbam.server.authentication;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.scholastic.sbam.server.database.codegen.AeControl;
import com.scholastic.sbam.server.database.util.HibernateAccessor;
import com.scholastic.sbam.server.database.util.HibernateUtil;
import com.scholastic.sbam.server.database.util.SqlConstructor;
import com.scholastic.sbam.server.database.util.SqlExecution;
import com.scholastic.sbam.server.util.ConsoleOutputter;
import com.scholastic.sbam.shared.exceptions.AuthenticationExportException;
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
public class AuthenticationGenerator implements Runnable, ConsoleOutputter {
	
	public static final char				RUNNING	= 'r';
	public static final char				ABORTED = 'a';
	public static final char				ERROR	= 'e';
	public static final char				COMPLETE= 'c';
	
	protected static AuthenticationGenerator singleton;
	
	protected boolean						consoleOutputOn = true;
	protected int							countIncrement = 0;
	protected boolean 						running;
	protected String						terminationReason;
	protected String						terminationUserId;
	protected ExportProcessReport			currentExportReport;
	protected List<ExportProcessReport>		exportReportLog = new ArrayList<ExportProcessReport>();
	
	protected AeControl						aeControl;
	
	protected AuthenticationGenerator() {
		
	}
	
	public static void main(String [] args) {

		if (args != null)
			for (String arg : args) {
				if ("-help".equals(arg)) {
					showHelp();
					System.exit(0);
				}
				if (! ( "-silent".equals(arg) || "-verbose".equals(arg) || AppConstants.isNumeric(arg) ) ) {
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
				else if (AppConstants.isNumeric(arg))
					auGen.setCountIncrement(Integer.parseInt(arg));
			}
		
		auGen.generateExport(new Date());
		
		auGen.forceConsoleOutput("Authentication Generator execution completed.");
	}
	
	protected static void showHelp() {
		System.out.println("-silent    To run without output (default).");
		System.out.println("-verbose   To run with specific progress messages.");
		System.out.println("n          Where n is any positive, non-zero number, to output agreement, site and method counts after every n processed agreements.");
		System.out.println("-help      To show this help list.");
	}
	
	protected synchronized String generateExport(Date execDate) {
		
		if (running)
			return "The export is already running.";
		
		running = true;
		currentExportReport = new ExportProcessReport();
		exportReportLog.add(currentExportReport);
		currentExportReport.setStarted();
		
		try {
			createAeControl();
			
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
			mainSql.addCondition("agreement_term.start_date <=", execDate);
			mainSql.addCondition("agreement_term.terminate_date is not null");
			mainSql.addCondition("agreement_term.terminate_date >", execDate);
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
				
			}
			
			consoleOutput(currentExportReport.getAgreements() + " agreements processed");
			currentExportReport.addMessage(currentExportReport.getAgreements() + " agreements processed");
			
		} catch (SQLException e) {
			e.printStackTrace();
			currentExportReport.setCompleted("terminated with a SQL error");
			running = false;
			closeAeControlError();
			return "Export failed.";
		} finally {
			running = false;
		}
		
		currentExportReport.setCompleted();
		closeAeControlComplete();
		running = false;
		
		return "Export complete.";
	}
	
	protected void createAeControl() {
		HibernateUtil.openSession();
		HibernateUtil.startTransaction();
		
		aeControl = new AeControl();
		
		aeControl.setCreatedDatetime(new Date());
		aeControl.setInitiatedDatetime(new Date());
		aeControl.setStatus(RUNNING);
		
		HibernateAccessor.persist(aeControl);
		
		HibernateUtil.endTransaction();
		HibernateUtil.closeSession();
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
		String result = generateExport(new Date());
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
	
	public void forceConsoleOutput(String message) {
		consoleOutput(message, true);
	}
	
	public void consoleOutput(String message) {
		consoleOutput(message, consoleOutputOn);
	}
	
	public void consoleOutput(String message, boolean consoleOutputOn) {
		if (!consoleOutputOn)
			return;
		System.out.println(new Date() + " : Authentication Export : " + message);
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

	public int getCountIncrement() {
		return countIncrement;
	}

	public void setCountIncrement(int countIncrement) {
		this.countIncrement = countIncrement;
	}
	
}
