package com.scholastic.sbam.server.servlets;

import java.util.Date;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.scholastic.sbam.client.services.DeauthenticateService;
import com.scholastic.sbam.shared.objects.Authentication;
import com.scholastic.sbam.shared.security.SecurityManager;

/**
 * Log a user out.
 * 
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class DeauthenticateServiceImpl extends RemoteServiceServlet implements DeauthenticateService {

	@Override
	public String deauthenticate() throws IllegalArgumentException {
		if (this.getServletContext().getAttribute(SecurityManager.AUTHENTICATION_ATTRIBUTE) != null) {
			Authentication auth = (Authentication) this.getServletContext().getAttribute(SecurityManager.AUTHENTICATION_ATTRIBUTE);
			System.out.println("Logging out " + auth.getUserName() + " at " + new Date());
			//	This method sets the log off time, so that a grace period will be allowed for late requests
			auth.setLoggedOff();
			
			//	This is the old method, that simply logged them off immediately (and so slow/late requests caused errors).
		//	this.getServletContext().setAttribute(SecurityManager.AUTHENTICATION_ATTRIBUTE, null);
		}
		return "";
	}
}
