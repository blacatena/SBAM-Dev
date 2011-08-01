package com.scholastic.sbam.server.servlets;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.scholastic.sbam.server.util.SecurityEnforcer;
import com.scholastic.sbam.shared.objects.Authentication;

public abstract class AuthenticatedServiceServlet extends RemoteServiceServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Verify that the user is authenticated for the requested task.
	 * 
	 * The Authentication object is returned in case it is needed by the servlet, but can be ignored.
	 * 
	 * @param taskDesc
	 *  The description of the task 
	 * @param roleName
	 * 	The role name required for the user to perform the task.
	 * @return
	 *  The Authentication currently active for the session.
	 * @throws IllegalArgumentException
	 */
	protected Authentication authenticate(String taskDesc, String roleName) throws IllegalArgumentException {
		return SecurityEnforcer.authenticate(this, taskDesc, roleName);
	}

	protected Authentication authenticate(String taskDesc) throws IllegalArgumentException {
		return authenticate(taskDesc, null);
	}

	protected Authentication authenticate() throws IllegalArgumentException {
		return authenticate(getServletName(), null);
	}
}
