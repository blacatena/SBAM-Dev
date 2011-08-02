package com.scholastic.sbam.shared.objects;

import java.util.Date;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.BeanModelFactory;
import com.extjs.gxt.ui.client.data.BeanModelLookup;
import com.extjs.gxt.ui.client.data.BeanModelTag;
import com.google.gwt.user.client.rpc.IsSerializable;

public class ProductInstance extends BetterRowEditInstance implements BeanModelTag, IsSerializable  {

	private static BeanModelFactory beanModelfactory;

	private String productCode;
	private String description;
	private String shortName;
	private String defaultTermType;
	private String defaultCommissionCode;
	private String orgPath;
	private int    seq;
	private char   status;
	private boolean active;
	private Date   createdDatetime;
	
	private TermTypeInstance		defaultTermTypeInstance;
	private CommissionTypeInstance	defaultCommTypeInstance;
	
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

	public String getDefaultCommissionCode() {
		return defaultCommissionCode;
	}

	public void setDefaultCommissionCode(String defaultCommissionCode) {
		this.defaultCommissionCode = defaultCommissionCode;
	}

	public String getOrgPath() {
		return orgPath;
	}

	public void setOrgPath(String orgPath) {
		this.orgPath = orgPath;
	}

	public int getSeq() {
		return seq;
	}

	public void setSeq(int seq) {
		this.seq = seq;
	}

	public TermTypeInstance getDefaultTermTypeInstance() {
		return defaultTermTypeInstance;
	}

	public void setDefaultTermTypeInstance(TermTypeInstance defaultTermTypeInstance) {
		this.defaultTermTypeInstance = defaultTermTypeInstance;
	}

	public CommissionTypeInstance getDefaultCommTypeInstance() {
		return defaultCommTypeInstance;
	}

	public void setDefaultCommTypeInstance(
			CommissionTypeInstance defaultCommTypeInstance) {
		this.defaultCommTypeInstance = defaultCommTypeInstance;
	}
	
	public String getDescriptionAndCode() {
		if (productCode == null || productCode.length() == 0)
			return description;
		return description + " [ " + productCode + " ]";
	}
	
	public static ProductInstance getEmptyInstance() {
		ProductInstance instance = new ProductInstance();
		instance.productCode = "";
		instance.description = "";
		instance.shortName   = "";
		return instance;
	}
	
	public static ProductInstance getUnknownInstance(String code) {
		ProductInstance instance = new ProductInstance();
		instance.productCode = code;
		instance.description = "Unknown product " + code;
		return instance;
	}

	public static BeanModel obtainModel(ProductInstance instance) {
		if (beanModelfactory == null)
			beanModelfactory  = BeanModelLookup.get().getFactory(ProductInstance.class);
		BeanModel model = beanModelfactory.createModel(instance);
		return model;
	}
	
	public String toString() {
		return productCode + " / " + description + " / " + shortName + " / " + defaultTermType + " / " + status + " / " + createdDatetime + "/ [" + defaultTermTypeInstance + "]";
	}

}
