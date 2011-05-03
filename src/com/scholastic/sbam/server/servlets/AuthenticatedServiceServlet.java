package com.scholastic.sbam.server.servlets;

import java.util.Enumeration;

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
		if (auth != null) {
			//	Test if the grace period has expired, and the user needs to be finally, really logged off.
			if (auth.getLoggedOff() > 0 && auth.getLoggedOff() + Authentication.LOG_OFF_GRACE_PERIOD > System.currentTimeMillis()) {
				// User log off is now official
				auth = null;
				getServletContext().removeAttribute(SecurityManager.AUTHENTICATION_ATTRIBUTE);
			} else if (auth.getLoggedOff() > 0)
				System.out.println("Grace period processing allowed for " + taskDesc);
		}
		if (auth != null)
			authUserName = auth.getUserName();
		if (auth == null || authUserName == null || authUserName.length() == 0 || !auth.isAuthenticated()) {
			
			// DEBUG OUTPUT ONLY... remove when this bug is found or verified to be normal system behavior
			System.out.println("");
			System.out.println("No logged in user error on " + taskDesc + " for " + roleName);
			System.out.println("auth " + auth);
			System.out.println("authUserName " + authUserName);
			if (auth != null)
				System.out.println("Authenticated " + auth.isAuthenticated());
			
			@SuppressWarnings({ "rawtypes" })
			Enumeration enu = this.getServletContext().getAttributeNames();
			while (enu.hasMoreElements()) {
				String attrName = enu.nextElement().toString();
				System.out.println(attrName + " : " + this.getServletContext().getAttribute(attrName));
			}
			
			throw new IllegalArgumentException("No logged in user for whom to " + taskDesc + ".");
		}
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
