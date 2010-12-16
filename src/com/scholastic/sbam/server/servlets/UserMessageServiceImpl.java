package com.scholastic.sbam.server.servlets;

import java.util.List;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.scholastic.sbam.client.services.UserMessageService;
import com.scholastic.sbam.server.database.codegen.UserMessage;
import com.scholastic.sbam.server.database.objects.DbUserMessage;
import com.scholastic.sbam.server.database.util.HibernateUtil;
import com.scholastic.sbam.shared.objects.Authentication;
import com.scholastic.sbam.shared.objects.UserMessageCollection;
import com.scholastic.sbam.shared.objects.UserMessageInstance;
import com.scholastic.sbam.shared.security.SecurityManager;
import com.scholastic.sbam.shared.util.WebUtilities;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class UserMessageServiceImpl extends RemoteServiceServlet implements UserMessageService {

	@Override
	public UserMessageCollection getUserMessages(String locationTag) throws IllegalArgumentException {
		
		HibernateUtil.openSession();
		HibernateUtil.startTransaction();

		UserMessageCollection coll = new UserMessageCollection();
		try {
			String userName = ((Authentication) getServletContext().getAttribute(SecurityManager.AUTHENTICATION_ATTRIBUTE)).getUserName();
			if (userName == null || userName.length() == 0)
				throw new Exception("No logged in user for whom to retrieve notes.");
			
			List<UserMessage> msgs = DbUserMessage.findToShow(userName, locationTag);

			for (UserMessage msg : msgs) {
				UserMessageInstance instance = new UserMessageInstance();
				instance.setId(msg.getId());
				instance.setUserName(msg.getUserName());
				instance.setLocationTag(msg.getLocationTag());
				instance.setCreated(msg.getCreateDate() + "");
				instance.setStatus(msg.getStatus());
				instance.setText(msg.getText());
				instance.setX(msg.getWindowPosX());
				instance.setY(msg.getWindowPosY());
				instance.setZ(msg.getWindowPosZ());
				instance.setWidth(msg.getWindowWidth());
				instance.setHeight(msg.getWindowHeight());
				instance.setRestoreX(msg.getRestorePosX());
				instance.setRestoreY(msg.getRestorePosY());
				instance.setRestoreWidth(msg.getRestoreWidth());
				instance.setRestoreHeight(msg.getRestoreHeight());
				instance.setMinimized(msg.getMinimized() == 'Y');
				instance.setMaximized(msg.getMaximized() == 'Y');
				instance.setCollapsed(msg.getCollapsed() == 'Y');
				coll.add(instance);
			}

		} catch (Exception exc) {
			exc.printStackTrace();
			UserMessageInstance errorMessage = new UserMessageInstance();
			errorMessage.setText("Internal error getting User Messages:<br/><br/>" + WebUtilities.getAsHtml(exc.toString()));
			coll.add(errorMessage);
		}
		
		HibernateUtil.endTransaction();
		HibernateUtil.closeSession();
		
		return coll;
	}
}
