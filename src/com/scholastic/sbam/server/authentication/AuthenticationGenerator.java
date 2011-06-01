package com.scholastic.sbam.server.authentication;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
	
	protected static AuthenticationGenerator singleton;
	
	protected boolean						consoleOutputOn = true;
	protected boolean 						running;
	protected String						terminationReason;
	protected String						terminationUserId;
	protected ExportProcessReport			currentExportReport;
	protected List<ExportProcessReport>		exportReportLog = new ArrayList<ExportProcessReport>();
	
	protected AuthenticationGenerator() {
		
	}
	
	public static void main(String [] args) {
		//	Generate Export
		new AuthenticationGenerator().generateExport(new Date());
	}
	
	protected synchronized String generateExport(Date execDate) {
		
		if (running)
			return "The export is already running.";
		
		running = true;
		currentExportReport = new ExportProcessReport();
		exportReportLog.add(currentExportReport);
		currentExportReport.setStarted();
		
		try {
			
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
			return "Export failed.";
		} finally {
			running = false;
		}
		
		currentExportReport.setCompleted();
		running = false;
		
		return "Export complete.";
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
	
	public void consoleOutput(String message) {
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
	
}
