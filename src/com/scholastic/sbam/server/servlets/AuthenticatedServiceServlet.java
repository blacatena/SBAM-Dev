package com.scholastic.sbam.server.servlets;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.scholastic.sbam.shared.objects.Authentication;
import com.scholastic.sbam.shared.security.SecurityManager;

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
		String authUserName = null;
		Authentication auth = ((Authentication) getServletContext().getAttribute(SecurityManager.AUTHENTICATION_ATTRIBUTE));
		if (auth != null)
			authUserName = auth.getUserName();
		if (auth == null || authUserName == null || authUserName.length() == 0 || !auth.isAuthenticated())
			throw new IllegalArgumentException("No logged in user for whom to " + taskDesc + ".");
		if (roleName != null && !auth.hasRoleName(roleName))
			throw new IllegalArgumentException("User is not privileged to " + taskDesc + ".");
		return auth;
	}

	protected Authentication authenticate(String taskDesc) throws IllegalArgumentException {
		return authenticate(taskDesc, null);
	}

	protected Authentication authenticate() throws IllegalArgumentException {
		return authenticate(getServletName(), null);
	}
}
