package com.scholastic.sbam.server.servlets;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.scholastic.sbam.client.services.DeauthenticateService;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class DeauthenticateServiceImpl extends RemoteServiceServlet implements DeauthenticateService {

	@Override
	public String deauthenticate() throws IllegalArgumentException {
		this.getServletContext().setAttribute("Auth", null);
		return "";
	}
}
