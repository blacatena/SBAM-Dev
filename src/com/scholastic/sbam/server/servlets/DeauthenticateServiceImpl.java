package com.scholastic.sbam.server.servlets;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.scholastic.sbam.client.services.DeauthenticateService;
import com.scholastic.sbam.shared.security.SecurityManager;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class DeauthenticateServiceImpl extends RemoteServiceServlet implements DeauthenticateService {

	@Override
	public String deauthenticate() throws IllegalArgumentException {
		this.getServletContext().setAttribute(SecurityManager.AUTHENTICATION_ATTRIBUTE, null);
		return "";
	}
}
