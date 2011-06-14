package com.scholastic.sbam.shared.objects;

import java.util.Date;

import com.google.gwt.user.client.rpc.IsSerializable;

public class ExportProcessMessage implements IsSerializable {
		
	public static final int ALERT		= 1;
	public static final int HIGH_ALERT	= 2;
	
	protected int		priority;
	protected Date		date;
	protected String	message;
	
	public ExportProcessMessage() {
		date = new Date();
		message = "No message.";
	}
	
	public ExportProcessMessage(String message) {
		date = new Date();
		this.message = message;
	}
	
	public ExportProcessMessage(String message, int priority) {
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
