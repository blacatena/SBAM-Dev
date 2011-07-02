package com.scholastic.sbam.shared.objects;

import java.util.Date;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.BeanModelFactory;
import com.extjs.gxt.ui.client.data.BeanModelLookup;
import com.extjs.gxt.ui.client.data.BeanModelTag;
import com.google.gwt.user.client.rpc.IsSerializable;

public class SnapshotInstance extends BetterRowEditInstance implements BeanModelTag, IsSerializable {
	public static char	PRODUCT_TYPE = 'p';
	public static char	SERVICE_TYPE = 's';
	public static char  NO_TERM_TYPE = 'n';

	protected static BeanModelFactory beanModelfactory;

	protected String		snapshotCode;
	protected String		snapshotName;
	protected String		snapshotType;
	protected char			productServiceType;
	protected String		note;
	protected String		orgPath;
	protected int			seq;
	protected Date			snapshotTaken;
	protected char			status;
	protected boolean		active;
	protected Date			createdDatetime;
	
	@Override
	public void markForDeletion() {
		setStatus('X');
	}

	@Override
	public boolean thisIsDeleted() {
		return status == 'X';
	}

	@Override
	public boolean thisIsValid() {
		return true;
	}

	@Override
	public String returnTriggerProperty() {
		return "junk";
	}

	@Override
	public String returnTriggerValue() {
		return "junk";
	}

	public String getSnapshotCode() {
		return snapshotCode;
	}

	public void setSnapshotCode(String cancelReasonCode) {
		this.snapshotCode = cancelReasonCode;
	}

	public String getSnapshotName() {
		return snapshotName;
	}

	public void setSnapshotName(String snapshotName) {
		this.snapshotName = snapshotName;
	}

	public String getSnapshotType() {
		return snapshotType;
	}

	public void setSnapshotType(String snapshotType) {
		this.snapshotType = snapshotType;
	}

	public char getProductServiceType() {
		return productServiceType;
	}

	public void setProductServiceType(char productServiceType) {
		this.productServiceType = productServiceType;
	}

	public int getSeq() {
		return seq;
	}

	public void setSeq(int seq) {
		this.seq = seq;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getOrgPath() {
		return orgPath;
	}

	public void setOrgPath(String orgPath) {
		this.orgPath = orgPath;
	}

	public Date getSnapshotTaken() {
		return snapshotTaken;
	}

	public void setSnapshotTaken(Date snapshotTaken) {
		this.snapshotTaken = snapshotTaken;
	}

	public char getStatus() {
		return status;
	}

	public void setStatus(char status) {
		this.status = status;
		this.active = (this.status == 'A');
	}

	public Date getCreatedDatetime() {
		return createdDatetime;
	}

	public void setCreatedDatetime(Date createdDatetime) {
		this.createdDatetime = createdDatetime;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		if (this.status == 'X')
			return;
		setStatus(active?'A':'I');
	}
	
	public static SnapshotInstance getUnknownInstance(String code) {
		SnapshotInstance instance = new SnapshotInstance();
		instance.snapshotCode = code;
		instance.snapshotName = "Unknown snapshot " + code;
		return instance;
	}
	
	public static SnapshotInstance getEmptyInstance() {
		SnapshotInstance instance = new SnapshotInstance();
		instance.snapshotCode = "";
		instance.snapshotName = "";
		return instance;
	}
	
	public static SnapshotInstance getNoneActiveInstance() {
		SnapshotInstance instance = new SnapshotInstance();
		instance.snapshotCode = "";
		instance.snapshotName = "None (Active)";
		return instance;
	}

	public static BeanModel obtainModel(SnapshotInstance instance) {
		if (beanModelfactory == null)
			beanModelfactory  = BeanModelLookup.get().getFactory(SnapshotInstance.class);
		BeanModel model = beanModelfactory.createModel(instance);
		return model;
	}
	
	public String getDescriptionAndCode() {
		if (snapshotCode == null || snapshotCode.length() == 0)
			return snapshotName;
		return snapshotName + " [ " + snapshotCode + " ]";
	}

}
