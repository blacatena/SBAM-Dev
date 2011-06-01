package com.scholastic.sbam.shared.objects;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.extjs.gxt.ui.client.data.BeanModelTag;
import com.google.gwt.user.client.rpc.IsSerializable;

public class ExportProcessReport implements BeanModelTag, IsSerializable {
	
	public static class ProcessMessage implements IsSerializable {
		
		public static final int ALERT		= 1;
		public static final int HIGH_ALERT	= 2;
		
		protected int		priority;
		protected Date		date;
		protected String	message;
		
		public ProcessMessage() {
			date = new Date();
			message = "No message.";
		}
		
		public ProcessMessage(String message) {
			date = new Date();
			this.message = message;
		}
		
		public ProcessMessage(String message, int priority) {
			this(message);
			this.priority = priority;
		}

		public Date getDate() {
			return date;
		}

		public String getMessage() {
			return message;
		}
		
		public boolean isAlert() {
			return priority >= ALERT;
		}
		
		public boolean isHighAlert() {
			return priority >= HIGH_ALERT;
		}
		
		public String getStyleName() {
			if (isHighAlert())
				return "exportHighAlert";
			if (isAlert())
				return "exportAlert";
			return "exportInfo";
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
	protected		int		sites;
	protected		int		authUnits;
	protected		int		ips;
	protected		int		uids;
	protected		int		urls;
	
	public ExportProcessReport() {
	}
	
	public void addError(String error) {
		addMessage(error, ProcessMessage.HIGH_ALERT);
		errors++;
	}
	
	public void addAlert(String alert) {
		addMessage(alert, ProcessMessage.ALERT);
	}
	
	public void countAgreement() {
		agreements++;
	}
	
	public void countSite() {
		sites++;
	}
	
	public void countAuthUnit() {
		authUnits++;
	}
	
	public void countIp() {
		ips++;
	}
	
	public void countUid() {
		uids++;
	}
	
	public void countUrl() {
		urls++;
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
	
	public void addMessage(String message, int priority) {
		messages.add(new ProcessMessage(message, priority));
	}
	
	public void setStarted() {
		if (running) {
			addMessage("Start status requested when already running.");
			return;
		}
		running = true;
		setTimeStarted(new Date());
		setStatus("initiated");
		addMessage("Export process initiated.");
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
			addAlert("Process completed successfully.");
		}
	}
	
	public ProcessMessage getLastMessage() {
		if (messages.size() == 0)
			return null;
		return messages.get(messages.size() - 1);
	}
	
	public List<ProcessMessage> getLastMessages() {
		return getLastMessages(10);
	}
	
	public List<ProcessMessage> getLastMessages(int numMessages) {
		if (messages.size() == 0)
			return null;
		int from = messages.size() < numMessages ? 0 : messages.size() - numMessages;
		return messages.subList(from, messages.size());
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

	public int getSites() {
		return sites;
	}

	public void setSites(int sites) {
		this.sites = sites;
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
