package com.scholastic.sbam.shared.objects;

import com.extjs.gxt.ui.client.data.BeanModelTag;
import com.google.gwt.user.client.rpc.IsSerializable;

public class SnapshotProductTreeInstance extends TreeInstance<SnapshotProductTreeInstance> implements BeanModelTag, IsSerializable {
	
	private int		snapshotId;
	private String	productCode;
	
	public int getSnapshotId() {
		return snapshotId;
	}
	public void setSnapshotId(int snapshotId) {
		this.snapshotId = snapshotId;
	}
	public String getProductCode() {
		return productCode;
	}
	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}
	public String getUniqueKey() {
		return snapshotId + " / " + productCode;
	}
}
