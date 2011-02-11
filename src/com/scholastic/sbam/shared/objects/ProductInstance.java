package com.scholastic.sbam.shared.objects;

import java.util.Date;

import com.extjs.gxt.ui.client.data.BeanModelTag;
import com.google.gwt.user.client.rpc.IsSerializable;

public class ProductInstance extends BetterRowEditInstance implements BeanModelTag, IsSerializable  {

	private String productCode;
	private String description;
	private String shortName;
	private String defaultTermType;
	private String defaultCommissionCode;
	private char   status;
	private boolean active;
	private Date   createdDatetime;
	
	private TermTypeInstance defaultTermTypeInstance;
	
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

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public String getDefaultTermType() {
		return defaultTermType;
	}

	public void setDefaultTermType(String defaultTermType) {
		this.defaultTermType = defaultTermType;
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
	
	public String toString() {
		return productCode + " / " + description + " / " + shortName + " / " + defaultTermType + " / " + status + " / " + createdDatetime + "/ [" + defaultTermTypeInstance + "]";
	}

	public TermTypeInstance getDefaultTermTypeInstance() {
		return defaultTermTypeInstance;
	}

	public void setDefaultTermTypeInstance(TermTypeInstance defaultTermTypeInstance) {
		this.defaultTermTypeInstance = defaultTermTypeInstance;
	}

	public String getDefaultCommissionCode() {
		return defaultCommissionCode;
	}

	public void setDefaultCommissionCode(String defaultCommissionCode) {
		this.defaultCommissionCode = defaultCommissionCode;
	}

}
