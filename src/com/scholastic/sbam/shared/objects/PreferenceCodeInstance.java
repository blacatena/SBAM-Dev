package com.scholastic.sbam.shared.objects;

import java.util.Date;

import com.extjs.gxt.ui.client.data.BeanModelTag;
import com.google.gwt.user.client.rpc.IsSerializable;

public class PreferenceCodeInstance extends BetterRowEditInstance implements BeanModelTag, IsSerializable {

	private String prefCatCode;
	private String prefSelCode;
	private String description;
	private int	   seq;
	private String exportValue;
	private char   status;
	private boolean active;
	private Date   createdDatetime;
	
	@Override
	public void markForDeletion() {
		setStatus('X');
	}

	@Override
	public boolean thisIsDeleted() {
		return status == 'X';
	}

	@Override
	public boolean thisIsNewRecord() {
		return prefCatCode == null || prefCatCode.length() == 0;
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

	public String getPrefCatCode() {
		return prefCatCode;
	}

	public void setPrefCatCode(String prefCatCode) {
		this.prefCatCode = prefCatCode;
	}

	public String getPrefSelCode() {
		return prefSelCode;
	}

	public void setPrefSelCode(String prefSelCode) {
		this.prefSelCode = prefSelCode;
	}

	public int getSeq() {
		return seq;
	}

	public void setSeq(int seq) {
		this.seq = seq;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getExportValue() {
		return exportValue;
	}

	public void setExportValue(String exportValue) {
		this.exportValue = exportValue;
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

}
