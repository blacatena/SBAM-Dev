package com.scholastic.sbam.server.servlets;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.data.LoadConfig;
import com.scholastic.sbam.client.services.SnapshotListService;
import com.scholastic.sbam.server.database.codegen.Snapshot;
import com.scholastic.sbam.server.database.objects.DbSnapshot;
import com.scholastic.sbam.server.database.util.HibernateUtil;
import com.scholastic.sbam.shared.objects.SnapshotTreeInstance;
import com.scholastic.sbam.shared.security.SecurityManager;
import com.scholastic.sbam.shared.util.AppConstants;

/**
 * The server side implementation of the RPC service to list product service assignments.
 * 
 * Note that this class can be generalized for other implementations by making SnapshotTreeInstance an implementation that requires the
 * getters and setters for Description, Type, and Children.
 */
@SuppressWarnings("serial")
public class SnapshotListServiceImpl extends TreeListServiceBase<SnapshotTreeInstance> implements SnapshotListService {
	
	@Override
	public List<SnapshotTreeInstance> getSnapshots(String snapshotType, LoadConfig loadConfig) throws IllegalArgumentException {
		
		authenticate("list snapshots", SecurityManager.ROLE_QUERY);
		
		HibernateUtil.openSession();
		HibernateUtil.startTransaction();
		
		SnapshotTreeInstance root = null;

		List<SnapshotTreeInstance> list = new ArrayList<SnapshotTreeInstance>();
		try {
			
			//	Find only undeleted product services
			List<Snapshot> snapshots = DbSnapshot.findForPresentation(snapshotType, AppConstants.STATUS_ANY_NONE, AppConstants.STATUS_DELETED);

			for (Snapshot snapshot : snapshots) {
				SnapshotTreeInstance instance = new SnapshotTreeInstance();
				
				instance.setSnapshot(DbSnapshot.getInstance(snapshot));
				instance.setType(SnapshotTreeInstance.SNAPSHOT);
				
				root = addFolderTree(list, root, instance, snapshot.getOrgPath());
			}

		} catch (Exception exc) {
			exc.printStackTrace();
		}
		
		HibernateUtil.endTransaction();
		HibernateUtil.closeSession();
		
		return list;
	}

	@Override
	protected SnapshotTreeInstance getTreeInstance() {
		return new SnapshotTreeInstance();
	}
}
