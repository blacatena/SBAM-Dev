package com.scholastic.sbam.server.servlets;

import java.util.List;

import com.scholastic.sbam.client.services.UpdateSnapshotServiceListService;
import com.scholastic.sbam.server.database.codegen.Snapshot;
import com.scholastic.sbam.server.database.codegen.SnapshotProductService;
import com.scholastic.sbam.server.database.codegen.SnapshotProductServiceId;
import com.scholastic.sbam.server.database.codegen.Service;
import com.scholastic.sbam.server.database.objects.DbSnapshot;
import com.scholastic.sbam.server.database.objects.DbSnapshotProductService;
import com.scholastic.sbam.server.database.objects.DbService;
import com.scholastic.sbam.server.database.util.HibernateUtil;
import com.scholastic.sbam.shared.objects.Authentication;
import com.scholastic.sbam.shared.objects.SnapshotInstance;
import com.scholastic.sbam.shared.objects.SnapshotServiceTreeInstance;
import com.scholastic.sbam.shared.security.SecurityManager;
import com.scholastic.sbam.shared.util.AppConstants;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class UpdateSnapshotServiceListServiceImpl extends AuthenticatedServiceServlet implements UpdateSnapshotServiceListService {
	
	private static final String ESCAPE_REG_EXP		= constructRegExp(AppConstants.PATH_ESCAPE);
	private static final String DELIMITER_REG_EXP	= constructRegExp(AppConstants.PATH_DELIMITER);
	private static final String ESCAPE_REPLACEMENT	= constructReplacement(AppConstants.PATH_ESCAPE);
	private static final String PATH_PREFIX			= "" + AppConstants.PATH_DELIMITER + AppConstants.PATH_ESCAPE;

	@Override
	public String updateSnapshotServiceList(int snapshotId, boolean updateOrg, List<SnapshotServiceTreeInstance> list) throws IllegalArgumentException {
		
		@SuppressWarnings("unused")
		Authentication auth = authenticate("update snapshot service list", SecurityManager.ROLE_CONFIG);	// May later be used for logging activity
		
		if (snapshotId <= 0)
			throw new IllegalArgumentException("An existing snapshot id is required.");
		
		HibernateUtil.openSession();
		HibernateUtil.startTransaction();

		try {
			
			Snapshot snapshot = DbSnapshot.getById(snapshotId);
			if (snapshot == null)
				throw new IllegalArgumentException("Sanpshot " + snapshotId + " does not exist in the database.");
			
			//	First, remove all product services from this snapshot
			List<SnapshotProductService> SnapshotProductServices = DbSnapshotProductService.findServiceBySnapshot(snapshotId, AppConstants.STATUS_DELETED);
			if (SnapshotProductServices != null) {
				for (SnapshotProductService SnapshotProductService : SnapshotProductServices) {
					DbSnapshotProductService.delete(SnapshotProductService);
				}
			}
			
			//	Now add in the necessary product services for this snapshot

			addFromTree(snapshotId, list);
			
			//	If enabled, update the organization of services (sequence, folder hierarchy)
			
			if (updateOrg) {
				//	Cycle through the list, creating a folder tree, and updating services and product services as needed
				
				int seq = 0;
				
				for (SnapshotServiceTreeInstance instance : list) {
					instance.setSnapshotId(snapshotId);	//	In case it's wrong or missing, set the product code from the service call parameter
					seq = processTreeInstance(instance, seq, "" + AppConstants.PATH_DELIMITER + AppConstants.PATH_ESCAPE, snapshotId);
				}
			}
			
		} catch (IllegalArgumentException exc) {
			silentRollback();
			throw exc;
		} catch (Exception exc) {
			silentRollback();
			exc.printStackTrace();
			throw new IllegalArgumentException("The update failed unexpectedly.");
		} finally {
			if (HibernateUtil.isTransactionInProgress())
				HibernateUtil.endTransaction();
			HibernateUtil.closeSession();
		}
		
		return "";
	}
	
	private void addFromTree(int snapshotId, List<SnapshotServiceTreeInstance> list) {
		if (list == null)
			return;
		
		for (SnapshotServiceTreeInstance instance : list) {
			if (!instance.getType().equals(SnapshotServiceTreeInstance.FOLDER) && instance.isSelected()) {
				SnapshotProductService dbInstance = new SnapshotProductService();
				SnapshotProductServiceId dbId = new SnapshotProductServiceId();
				dbId.setSnapshotId(snapshotId);
				dbId.setProductServiceType(SnapshotInstance.SERVICE_TYPE);
				dbId.setProductServiceCode(instance.getServiceCode());
				dbInstance.setId(dbId);
				DbSnapshotProductService.persist(dbInstance);
			}
			addFromTree(snapshotId, instance.getChildInstances());
		}
	}
	
	private int processTreeInstance(SnapshotServiceTreeInstance instance, int seq, String path, int snapshotId) {
		
		if (instance == null)
			return seq;
		
		seq++;
		
		if (instance.getType().equals(SnapshotServiceTreeInstance.FOLDER)) {
			//	Add folder name to the path, with the delimiter, after first escaping any instances of the escape character and delimiter character
			String fixedFolderName = instance.getDescription().replaceAll(ESCAPE_REG_EXP, ESCAPE_REPLACEMENT);
			fixedFolderName = fixedFolderName.replaceAll(DELIMITER_REG_EXP, "" + AppConstants.PATH_ESCAPE + AppConstants.PATH_DELIMITER);
			path += fixedFolderName + AppConstants.PATH_DELIMITER;
			//  Recursively process all children
			if (instance.getChildInstances() != null) {
				for (SnapshotServiceTreeInstance child : instance.getChildInstances()) {
					seq = processTreeInstance(child, seq, path, snapshotId);
				}
			}
		} else {
			updateService(instance.getServiceCode(), seq, path);
		}
		
		/* THIS SHOULDN'T EVER BE NECESSARY... ALREADY DONE FOR A FOLDER, AND NOT POSSIBLE FOR A SERVICE!!!
		//	Process all of the children
		if (instance.getChildInstances() != null) {
			for (SnapshotServiceTreeInstance child : instance.getChildInstances()) {
				if (child != null) {
					child.setSnapshotId(snapshotId);	//	In case it's wrong or missing, propagate the product code from the parent
					seq = processTreeInstance(child, seq, path, snapshotId);
				}
			}
		}
		*/
		
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
	
	private void updateService(String serviceCode, int seq, String path) {
		Service service = DbService.getByCode(serviceCode);
		
		if (seq == service.getSeq() && isUnchanged(path, service.getPresentationPath()))
			return;
		
		service.setSeq(seq);
		if (path != null && !path.equals(PATH_PREFIX))
			service.setPresentationPath(path);
		else
			service.setPresentationPath("");
		
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
