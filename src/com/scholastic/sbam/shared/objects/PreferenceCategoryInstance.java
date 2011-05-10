package com.scholastic.sbam.shared.objects;

import java.util.Date;
import java.util.List;

import com.extjs.gxt.ui.client.data.BeanModelTag;
import com.google.gwt.user.client.rpc.IsSerializable;

public class PreferenceCategoryInstance extends BetterRowEditInstance implements BeanModelTag, IsSerializable {

	private String prefCatCode;
	private String description;
	private int	   seq;
	private char   status;
	private boolean active;
	private Date   createdDatetime;
	
	private List<PreferenceCodeInstance> preferenceCodes;
	
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

	public String getPrefCatCode() {
		return prefCatCode;
	}

	public void setPrefCatCode(String prefCatCode) {
		this.prefCatCode = prefCatCode;
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

	public List<PreferenceCodeInstance> getPreferenceCodes() {
		return preferenceCodes;
	}

	public void setPreferenceCodes(List<PreferenceCodeInstance> preferenceCodes) {
		this.preferenceCodes = preferenceCodes;
	}

	@Override
	public String toString() {
		return prefCatCode + " (" + seq + ") " + description;
	}
}
