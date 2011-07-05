package com.scholastic.sbam.shared.objects;

import com.extjs.gxt.ui.client.data.BeanModelTag;
import com.google.gwt.user.client.rpc.IsSerializable;
import com.scholastic.sbam.shared.util.AppConstants;

public class SnapshotTreeInstance extends TreeInstance<SnapshotTreeInstance> implements BeanModelTag, IsSerializable {
	public static String		SNAPSHOT	= "snapshot";

	protected SnapshotInstance	snapshot;
	protected char				status;
	
	public SnapshotTreeInstance() {
		super();
	}
	
	public SnapshotTreeInstance(SnapshotInstance snapshot) {
		super();
		setSnapshot(snapshot);
	}
	
	public SnapshotInstance getSnapshot() {
		return snapshot;
	}

	public void setSnapshot(SnapshotInstance snapshot) {
		this.snapshot = snapshot;
		if (snapshot != null) {
			setDescription(snapshot.getSnapshotName());
			setStatus(snapshot.getStatus());
		}
	}
	
	public int getSnapshotId() {
		if (snapshot == null)
			return 0;
		return snapshot.getSnapshotId();
	}

	public char getStatus() {
		return status;
	}

	public void setStatus(char status) {
		this.status = status;
	}

	public String getStatusDescription() {
		return getStatusDescription(status);
	}
	
	public String getNote() {
		if (snapshot == null)
			return "";
		return snapshot.getNote();
	}
	
	@Override
	public String getDescription() {
		return snapshot.getSnapshotName();
	}
	
	@Override
	public void setDescription(String description) {
		snapshot.setSnapshotName(description);
	}

	public String getUniqueKey() {
		return snapshot.getSnapshotId() + "";
	}

	public static String getStatusDescription(char status) {
		if (status == 0)
			return "None";
		if (status == AppConstants.STATUS_NEW)
			return "New";
		if (status == AppConstants.STATUS_DELETED)
			return "Deleted";
		if (status == AppConstants.STATUS_ACTIVE)
			return "Active";
		if (status == AppConstants.STATUS_INACTIVE)
			return "Inactive";
		return "Unknown";
	}
}