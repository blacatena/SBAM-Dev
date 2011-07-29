package com.scholastic.sbam.server.util;

import java.util.Date;
import java.util.Enumeration;

import javax.servlet.http.HttpServlet;

import com.scholastic.sbam.shared.objects.Authentication;
import com.scholastic.sbam.shared.security.SecurityManager;

public class SecurityEnforcer {
	public static Authentication authenticate(HttpServlet servlet, String taskDesc, String roleName) throws IllegalArgumentException {
		String authUserName = null;
		Authentication auth = ((Authentication) servlet.getServletContext().getAttribute(SecurityManager.AUTHENTICATION_ATTRIBUTE));
		if (auth != null) {
			//	Test if the grace period has expired, and the user needs to be finally, really logged off.
			if (auth.getLoggedOff() > 0 && (auth.getLoggedOff() + Authentication.LOG_OFF_GRACE_PERIOD) < System.currentTimeMillis()) {
				// User log off is now official
				System.out.println(new Date() + ": Grace period now expired for " + auth.getUserName() + " (" + auth.getLoggedOff() + " vs " + System.currentTimeMillis() + ")");
				auth = null;
				servlet.getServletContext().removeAttribute(SecurityManager.AUTHENTICATION_ATTRIBUTE);
			} else if (auth.getLoggedOff() > 0)
				System.out.println(new Date() + ": Grace period processing allowed for " + taskDesc);
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
			Enumeration enu = servlet.getServletContext().getAttributeNames();
			while (enu.hasMoreElements()) {
				String attrName = enu.nextElement().toString();
				System.out.println(attrName + " : " + servlet.getServletContext().getAttribute(attrName));
			}
			
			throw new IllegalArgumentException("No logged in user for whom to " + taskDesc + ".");
		}
		if (roleName != null && !auth.hasRoleName(roleName))
			throw new IllegalArgumentException("User is not privileged to " + taskDesc + ".");
		return auth;
	}
}
