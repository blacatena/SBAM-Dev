package com.scholastic.sbam.server.servlets;

import java.util.List;

import com.scholastic.sbam.client.services.UpdateSnapshotTreeService;
import com.scholastic.sbam.server.database.codegen.Snapshot;
import com.scholastic.sbam.server.database.objects.DbSnapshot;
import com.scholastic.sbam.server.database.objects.DbService;
import com.scholastic.sbam.server.database.util.HibernateUtil;
import com.scholastic.sbam.shared.objects.Authentication;
import com.scholastic.sbam.shared.objects.SnapshotTreeInstance;
import com.scholastic.sbam.shared.security.SecurityManager;
import com.scholastic.sbam.shared.util.AppConstants;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class UpdateSnapshotTreeServiceImpl extends AuthenticatedServiceServlet implements UpdateSnapshotTreeService {
	
	private static final String ESCAPE_REG_EXP		= constructRegExp(AppConstants.PATH_ESCAPE);
	private static final String DELIMITER_REG_EXP	= constructRegExp(AppConstants.PATH_DELIMITER);
	private static final String ESCAPE_REPLACEMENT	= constructReplacement(AppConstants.PATH_ESCAPE);
	private static final String PATH_PREFIX			= "" + AppConstants.PATH_DELIMITER + AppConstants.PATH_ESCAPE;

	@Override
	public String updateSnapshotTree(String snapshotType, List<SnapshotTreeInstance> list) throws IllegalArgumentException {
		
		@SuppressWarnings("unused")
		Authentication auth = authenticate("update preference categories", SecurityManager.ROLE_CONFIG);	// May later be used for logging activity
		
		if (snapshotType == null || snapshotType.trim().length() == 0)
			throw new IllegalArgumentException("A product code is required.");
		
		HibernateUtil.openSession();
		HibernateUtil.startTransaction();

		try {
			
			//	Cycle through the list, creating a folder tree, and updating snapshots as needed
			
			int seq = 0;
			
			System.out.println("___________________________________");
			for (SnapshotTreeInstance instance : list) {
				instance.getSnapshot().setSnapshotType(snapshotType);	//	In case it's wrong or missing, set the snapshotType from the service call parameter
				if (instance.getStatus() != AppConstants.STATUS_DELETED) {
					seq = processTreeInstance(instance, seq, "" + AppConstants.PATH_DELIMITER + AppConstants.PATH_ESCAPE, snapshotType);
				} else
					System.out.println("Skipping deleted instance " + instance.getSnapshotId());
			}
			
		} catch (IllegalArgumentException exc) {
			silentRollback();
			throw exc;
		} catch (Exception exc) {
			silentRollback();
			exc.printStackTrace();
			throw new IllegalArgumentException("The snapshot tree update failed unexpectedly.");
		} finally {
			if (HibernateUtil.isTransactionInProgress())
				HibernateUtil.endTransaction();
			HibernateUtil.closeSession();
		}
		
		return "";
	}
	
	private int processTreeInstance(SnapshotTreeInstance instance, int seq, String path, String snapshotType) {
		
		if (instance == null)
			return seq;
		
		seq++;
		
		if (instance.getType().equals(SnapshotTreeInstance.FOLDER)) {
			//	Add folder name to the path, with the delimiter, after first escaping any instances of the escape character and delimiter character
			String fixedFolderName = instance.getDescription().replaceAll(ESCAPE_REG_EXP, ESCAPE_REPLACEMENT);
			fixedFolderName = fixedFolderName.replaceAll(DELIMITER_REG_EXP, "" + AppConstants.PATH_ESCAPE + AppConstants.PATH_DELIMITER);
			path += fixedFolderName + AppConstants.PATH_DELIMITER;
			//  Recursively process all children
			if (instance.getChildInstances() != null) {
				for (SnapshotTreeInstance child : instance.getChildInstances()) {
					seq = processTreeInstance(child, seq, path, snapshotType);
				}
			}
		} else {
			updateSnapshot(instance.getSnapshotId(), seq, path);
		}
		
		//	Process all of the children
		if (instance.getChildInstances() != null) {
			for (SnapshotTreeInstance child : instance.getChildInstances()) {
				if (child != null) {
					child.getSnapshot().setSnapshotType(snapshotType);	//	In case it's wrong or missing, propagate the product code from the parent
					seq = processTreeInstance(child, seq, path, snapshotType);
				}
			}
		}
		
		return seq;
	}
	
	/**
	 * For a regular expression, precede any specially interpreted characters with a back slash.
	 * 
	 * @param value
	 * @return
	 */
	private static String constructRegExp(char value) {
		return "\\" + value;
//		if (value == '\\' || value == '.' || value == '*'|| value == '^' || value == '$')
//			return "\\" + value;
//		return value + "";
	}
	
	private static String constructReplacement(char value) {
		if (value == '\\')
			return "\\\\\\\\";
		return value + value + "";
	}
	
	private void updateSnapshot(int snapshotId, int seq, String path) {
		Snapshot service = DbSnapshot.getById(snapshotId);
		
		if (seq == service.getSeq() && isUnchanged(path, service.getOrgPath()))
			return;
		
		service.setSeq(seq);
		if (path != null && !path.equals(PATH_PREFIX))
			service.setOrgPath(path);
		else
			service.setOrgPath("");
		
		DbService.persist(service);
	}
	
	private boolean isUnchanged(String first, String second) {
		if (first == null && second == null)
			return false;
		if (first == null || second == null)
			return true;
		return first.equals(second);
	}
	
	protected void testMessages(List<String> messages) throws IllegalArgumentException {
		if (messages != null)
			for (String message: messages)
				testMessage(message);
	}
	

	protected void testMessage(String message) throws IllegalArgumentException {
		if (message != null && message.length() > 0)
			throw new IllegalArgumentException(message);
	}
	

	protected void silentRollback() {
		try {
			if (HibernateUtil.isTransactionInProgress())
				HibernateUtil.getSession().getTransaction().rollback();	
		} catch (Exception exc) { }
	}
}
