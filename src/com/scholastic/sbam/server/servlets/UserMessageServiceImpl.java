package com.scholastic.sbam.server.servlets;

import java.util.List;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.scholastic.sbam.client.services.UserMessageService;
import com.scholastic.sbam.server.database.codegen.UserMessage;
import com.scholastic.sbam.server.database.objects.DbUserMessage;
import com.scholastic.sbam.server.database.util.HibernateUtil;
import com.scholastic.sbam.shared.objects.UserMessageCollection;
import com.scholastic.sbam.shared.objects.UserMessageInstance;
import com.scholastic.sbam.shared.util.WebUtilities;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class UserMessageServiceImpl extends RemoteServiceServlet implements UserMessageService {

	@Override
	public UserMessageCollection getUserMessages(String userName, String locationTag) throws IllegalArgumentException {
		
		HibernateUtil.openSession();
		HibernateUtil.startTransaction();

		UserMessageCollection coll = new UserMessageCollection();
		try {
			List<UserMessage> msgs = DbUserMessage.findToShow(userName, locationTag);

			for (UserMessage msg : msgs) {
				UserMessageInstance instance = new UserMessageInstance();
				instance.setId(msg.getId());
				instance.setUserName(msg.getUserName());
				instance.setLocationTag(msg.getLocationTag());
				instance.setText(msg.getText());
				instance.setX(msg.getWindowPosX());
				instance.setY(msg.getWindowPosY());
				instance.setZ(msg.getWindowPosZ());
				coll.add(instance);
			}
			
			if (msgs.size() == 0) {
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
