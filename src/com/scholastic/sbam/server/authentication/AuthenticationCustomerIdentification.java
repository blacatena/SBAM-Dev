package com.scholastic.sbam.server.authentication;

import com.scholastic.sbam.server.database.codegen.AeAuthUnit;

public class AuthenticationCustomerIdentification {
	protected int		siteUcn;
	protected int		siteUcnSuffix;
	protected String	siteLocCode;
	protected int		siteParentUcn;
	protected int		siteParentUcnSuffix;
	protected int		billUcn;
	protected int		billUcnSuffix;
	
	public void set(AeAuthUnit aeAuthUnit) {
		siteUcn				= aeAuthUnit.getSiteUcn();
		siteUcnSuffix		= aeAuthUnit.getSiteUcnSuffix();
		siteLocCode			= aeAuthUnit.getSiteLocCode();
		siteParentUcn		= aeAuthUnit.getSiteUcn();
		siteParentUcnSuffix	= aeAuthUnit.getSiteUcnSuffix();
		billUcn				= aeAuthUnit.getSiteUcn();
		billUcnSuffix		= aeAuthUnit.getSiteUcnSuffix();
	}
	
	public int getSiteUcn() {
		return siteUcn;
	}
	public void setSiteUcn(int siteUcn) {
		this.siteUcn = siteUcn;
	}
	public int getSiteUcnSuffix() {
		return siteUcnSuffix;
	}
	public void setSiteUcnSuffix(int siteUcnSuffix) {
		this.siteUcnSuffix = siteUcnSuffix;
	}
	public String getSiteLocCode() {
		return siteLocCode;
	}
	public void setSiteLocCode(String siteLocCode) {
		this.siteLocCode = siteLocCode;
	}
	public int getSiteParentUcn() {
		return siteParentUcn;
	}
	public void setSiteParentUcn(int siteParentUcn) {
		this.siteParentUcn = siteParentUcn;
	}
	public int getSiteParentUcnSuffix() {
		return siteParentUcnSuffix;
	}
	public void setSiteParentUcnSuffix(int siteParentUcnSuffix) {
		this.siteParentUcnSuffix = siteParentUcnSuffix;
	}
	public int getBillUcn() {
		return billUcn;
	}
	public void setBillUcn(int billUcn) {
		this.billUcn = billUcn;
	}
	public int getBillUcnSuffix() {
		return billUcnSuffix;
	}
	public void setBillUcnSuffix(int billUcnSuffix) {
		this.billUcnSuffix = billUcnSuffix;
	}
	
}
