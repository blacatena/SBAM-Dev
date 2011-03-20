package com.scholastic.sbam.shared.objects;

import java.util.Date;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.BeanModelTag;
import com.extjs.gxt.ui.client.data.ModelKeyProvider;
import com.google.gwt.user.client.rpc.IsSerializable;

public class CommissionTypeInstance extends BetterRowEditInstance implements BeanModelTag, IsSerializable, InherentModelKeyProvider {

	private static ModelKeyProvider<BeanModel> singletonKeyProvider = new SimpleKeyProvider("commissionCode");
	
	private String	commissionCode;
	private String	description;
	private String	shortName;
	private boolean	products;
	private boolean	sites;
	private boolean	agreements;
	private boolean	agreementTerms;
	private char	status;
	private boolean	active;
	private Date	createdDatetime;
	
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

	public String getCommissionCode() {
		return commissionCode;
	}

	public void setCommissionCode(String commissionCode) {
		this.commissionCode = commissionCode;
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

	public boolean isProducts() {
		return products;
	}

	public void setProducts(boolean products) {
		this.products = products;
	}

	public void setProducts(char products) {
		this.products = products == 'y';
	}
	
	public char getProductsChar() {
		return products?'y':'n';
	}

	public boolean isSites() {
		return sites;
	}

	public void setSites(boolean sites) {
		this.sites = sites;
	}

	public void setSites(char sites) {
		this.sites = sites == 'y';
	}
	
	public char getSitesChar() {
		return sites?'y':'n';
	}

	public boolean isAgreements() {
		return agreements;
	}

	public void setAgreements(boolean agreements) {
		this.agreements = agreements;
	}

	public void setAgreements(char agreements) {
		this.agreements = agreements == 'y';
	}
	
	public char getAgreementsChar() {
		return agreements?'y':'n';
	}

	public boolean isAgreementTerms() {
		return agreementTerms;
	}

	public void setAgreementTerms(boolean agreementTerms) {
		this.agreementTerms = agreementTerms;
	}

	public void setAgreementTerms(char agreementTerms) {
		this.agreementTerms = agreementTerms == 'y';
	}
	
	public char getAgreementTermsChar() {
		return agreementTerms?'y':'n';
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
		return shortName;
	}
	
	public static ModelKeyProvider<BeanModel> obtainStaticModelKeyProvider() {
		return singletonKeyProvider;
	}

	@Override
	public ModelKeyProvider<BeanModel> obtainModelKeyProvider() {
		return CommissionTypeInstance.singletonKeyProvider;
	}
	
	public String getDescriptionAndCode() {
		return description + "[" + commissionCode + "]";
	}
}
