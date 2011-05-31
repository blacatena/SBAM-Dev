package com.scholastic.sbam.shared.objects;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.extjs.gxt.ui.client.data.BeanModelTag;
import com.google.gwt.user.client.rpc.IsSerializable;

public class ExportProcessReport implements BeanModelTag, IsSerializable {
	
	public static class ProcessMessage {
		protected Date		date;
		protected String	message;
		
		public ProcessMessage(String message) {
			date = new Date();
			this.message = message;
		}

		public Date getDate() {
			return date;
		}

		public String getMessage() {
			return message;
		}
	}
	
	List<ProcessMessage> messages = new ArrayList<ProcessMessage>();
	
	protected		boolean	validExport		=	false;
	protected		boolean running			=	false;
	protected		String	status			=	 "Unstarted";
	
	protected		Date	timeStarted;
	protected		Date	timeCompleted;
	
	protected		int		errors;
	protected		int		agreements;
	protected		int		authUnits;
	protected		int		ips;
	protected		int		uids;
	protected		int		urls;
	
	public ExportProcessReport() {
		
	}
	
	public void addError(String error) {
		addMessage(error);
		errors++;
	}
	
	public void addAgreement() {
		agreements++;
	}

	public List<ProcessMessage> getMessages() {
		return messages;
	}

	public void setMessages(List<ProcessMessage> messages) {
		this.messages = messages;
	}
	
	public void addMessage(String message) {
		messages.add(new ProcessMessage(message));
	}
	
	public void setStarted() {
		if (running) {
			addMessage("Start status requested when already running.");
			return;
		}
		running = true;
		setTimeStarted(new Date());
		setStatus("initiated");
		addMessage("Process initiated.");
	}
	
	public void setCompleted() {
		setCompleted(null);
	}
	
	public void setCompleted(String disposition) {
		if (!running) {
			addMessage("Completion update requested when not running.");
			return;
		}
		running = false;
		setTimeCompleted(new Date());
		if (disposition != null && disposition.length() > 0) {
			setStatus(disposition);
			addMessage("Process completed " + disposition + ".");
		} else {
			validExport = true;
			setStatus("succesfully");
			addMessage("Process completed successfully.");
		}
	}
	
	public ProcessMessage getLastMessage() {
		if (messages.size() == 0)
			return null;
		return messages.get(messages.size() - 1);
	}

	public Date getTimeStarted() {
		return timeStarted;
	}

	public void setTimeStarted(Date timeStarted) {
		this.timeStarted = timeStarted;
	}

	public Date getTimeCompleted() {
		return timeCompleted;
	}

	public void setTimeCompleted(Date timeCompleted) {
		this.timeCompleted = timeCompleted;
	}

	public int getErrors() {
		return errors;
	}

	public void setErrors(int errors) {
		this.errors = errors;
	}

	public int getAuthUnits() {
		return authUnits;
	}

	public int getAgreements() {
		return agreements;
	}

	public void setAgreements(int agreements) {
		this.agreements = agreements;
	}

	public void setAuthUnits(int authUnits) {
		this.authUnits = authUnits;
	}

	public int getIps() {
		return ips;
	}

	public void setIps(int ips) {
		this.ips = ips;
	}

	public int getUids() {
		return uids;
	}

	public void setUids(int uids) {
		this.uids = uids;
	}

	public int getUrls() {
		return urls;
	}

	public void setUrls(int urls) {
		this.urls = urls;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public boolean isValidExport() {
		return validExport;
	}

	public boolean isRunning() {
		return running;
	}
	
}
