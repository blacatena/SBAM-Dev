package com.scholastic.sbam.server.authentication;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import com.scholastic.sbam.server.util.ExportController;
import com.scholastic.sbam.shared.objects.ExportProcessReport;

public class AuthenticationExportFile {
	protected	ExportController	controller;
	protected	String				exportDirectory = null;
	protected	ExportProcessReport	exportReport;
	
	protected	PrintWriter			out;
	protected	String				fileName;
	
	public AuthenticationExportFile(String fileName, ExportController controller, ExportProcessReport exportReport) {
		this.controller		= controller;
		this.exportReport	= exportReport;
		this.fileName		= fileName;
		if (exportDirectory == null)
			exportDirectory = controller.getExportDirectory();
	}
	
	public void open() throws IOException {
		String fullName;
		if (exportDirectory != null && exportDirectory.length() > 0) {
			if (exportDirectory.charAt(exportDirectory.length() - 1) == '/')
				fullName = exportDirectory + fileName;
			else
				fullName = exportDirectory + "/" + fileName;
		} else
			fullName = fileName;
		out = new PrintWriter(new FileWriter(fullName));
	}
	
	public void write(Object... args) {
		
		StringBuffer sb = new StringBuffer();
		
		if (args.length > 0) {
			sb.append(args [0].toString());
			for (int i = 1; i < args.length; i++) {
				sb.append("|");
				sb.append(args [i].toString());
			}
		}
		
		out.println(sb);
	}
	
	public long getAuId(long auId, char remote) {
		if (remote == 'y' || remote == 'Y')
			return auId + ExportController.REMOTE_AU_ADD;
		else
			return auId;
	}
	
	public void close() {
		out.close();
	}

	public ExportController getController() {
		return controller;
	}

	public void setController(ExportController controller) {
		this.controller = controller;
	}

	public String getExportDirectory() {
		return exportDirectory;
	}

	public void setExportDirectory(String exportDirectory) {
		this.exportDirectory = exportDirectory;
	}

	public ExportProcessReport getExportReport() {
		return exportReport;
	}

	public void setExportReport(ExportProcessReport exportReport) {
		this.exportReport = exportReport;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
}
