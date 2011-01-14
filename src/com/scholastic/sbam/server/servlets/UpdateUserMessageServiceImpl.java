package com.scholastic.sbam.server.servlets;

import java.util.Date;

import com.scholastic.sbam.client.services.UpdateUserMessageService;
import com.scholastic.sbam.server.database.codegen.UserMessage;
import com.scholastic.sbam.server.database.objects.DbUserMessage;
import com.scholastic.sbam.server.database.util.HibernateUtil;
import com.scholastic.sbam.shared.objects.Authentication;
import com.scholastic.sbam.shared.objects.UserMessageInstance;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class UpdateUserMessageServiceImpl extends AuthenticatedServiceServlet implements UpdateUserMessageService {

	@Override
	public Integer updateUserMessage(UserMessageInstance instance) throws IllegalArgumentException {

		Authentication auth = authenticate("update user message");
		
		HibernateUtil.openSession();
		HibernateUtil.startTransaction();
		
		int returnId = -1;

		try {
			
			String userName = auth.getUserName();
			UserMessage dbInstance = null;
			
			returnId = instance.getId();
			
			if (instance.getId() >= 0) {
				dbInstance = DbUserMessage.getById(instance.getId());
				if (userName == null || !userName.equals(dbInstance.getUserName())) {
					throw new Exception ("User name mismatch.");
				}
			} else {
				dbInstance = new UserMessage();
				dbInstance.setCreateDate(new Date());
				dbInstance.setStatus('A');
				dbInstance.setUserName(userName);
			}

			dbInstance.setLocationTag(instance.getLocationTag());
			dbInstance.setText(instance.getText());
			dbInstance.setStatus(instance.getStatus());
			dbInstance.setWindowPosX(instance.getX());
			dbInstance.setWindowPosY(instance.getY());
			dbInstance.setWindowPosZ(instance.getZ());
			dbInstance.setWindowWidth(instance.getWidth());
			dbInstance.setWindowHeight(instance.getHeight());
			dbInstance.setRestorePosX(instance.getRestoreX());
			dbInstance.setRestorePosY(instance.getRestoreY());
			dbInstance.setRestoreWidth(instance.getRestoreWidth());
			dbInstance.setRestoreHeight(instance.getRestoreHeight());
			dbInstance.setMinimized(instance.isMinimized() ? 'Y' : 'N');
			dbInstance.setMaximized(instance.isMaximized() ? 'Y' : 'N');
			dbInstance.setCollapsed(instance.isCollapsed() ? 'Y' : 'N');

			DbUserMessage.persist(dbInstance);
			if (dbInstance.getId() == null)
				DbUserMessage.refresh(dbInstance);
			returnId = dbInstance.getId();
		} catch (Exception exc) {
			exc.printStackTrace();
		}
		
		HibernateUtil.endTransaction();
		HibernateUtil.closeSession();
		
		return returnId;
	}
}
