package com.scholastic.sbam.shared.objects;

import com.extjs.gxt.ui.client.data.BeanModelTag;
import com.google.gwt.user.client.rpc.IsSerializable;

public class SnapshotTreeInstance extends TreeInstance<SnapshotTreeInstance> implements BeanModelTag, IsSerializable {
	public static String		SNAPSHOT	= "snapshot";

	protected SnapshotInstance	snapshot;
	
	public SnapshotInstance getSnapshot() {
		return snapshot;
	}

	public void setSnapshot(SnapshotInstance snapshot) {
		this.snapshot = snapshot;
		if (snapshot != null) {
			setDescription(snapshot.getSnapshotName());
		}
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
		return snapshot.getSnapshotCode();
	}
}