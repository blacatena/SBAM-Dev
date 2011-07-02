package com.scholastic.sbam.shared.objects;

import com.extjs.gxt.ui.client.data.BeanModelTag;
import com.google.gwt.user.client.rpc.IsSerializable;

public class SnapshotServiceTreeInstance extends TreeInstance<SnapshotServiceTreeInstance> implements BeanModelTag, IsSerializable {
	
	private String snapshotCode;
	private String serviceCode;
	
	public String getSnapshotCode() {
		return snapshotCode;
	}
	public void setSnapshotCode(String snapshotCode) {
		this.snapshotCode = snapshotCode;
	}
	public String getServiceCode() {
		return serviceCode;
	}
	public void setServiceCode(String serviceCode) {
		this.serviceCode = serviceCode;
	}
	public String getUniqueKey() {
		return snapshotCode + " / " + serviceCode;
	}
}
