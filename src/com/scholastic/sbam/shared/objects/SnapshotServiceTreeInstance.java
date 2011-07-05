package com.scholastic.sbam.shared.objects;

import com.extjs.gxt.ui.client.data.BeanModelTag;
import com.google.gwt.user.client.rpc.IsSerializable;

public class SnapshotServiceTreeInstance extends TreeInstance<SnapshotServiceTreeInstance> implements BeanModelTag, IsSerializable {
	
	private int		snapshotId;
	private String	serviceCode;
	
	public int getSnapshotId() {
		return snapshotId;
	}
	public void setSnapshotId(int snapshotId) {
		this.snapshotId = snapshotId;
	}
	public String getServiceCode() {
		return serviceCode;
	}
	public void setServiceCode(String serviceCode) {
		this.serviceCode = serviceCode;
	}
	public String getUniqueKey() {
		return snapshotId + " / " + serviceCode;
	}
}
