package com.scholastic.sbam.shared.objects;

import java.util.Date;
import java.util.List;

import com.extjs.gxt.ui.client.data.BeanModelTag;
import com.google.gwt.user.client.rpc.IsSerializable;
import com.scholastic.sbam.shared.util.AppConstants;

public class AgreementInstance extends BetterRowEditInstance implements BeanModelTag, IsSerializable {

	private int		id;
	private int		idCheckDigit;
	private int		billUcn;
	private int		billUcnSuffix;
	private String	agreementTypeCode;
	private String	agreementTypeDescription;
	private String	commissionCode;
	private String	commissionCodeDescription;
	private String	deleteReasonCode;
	private String	deleteReasonDescription;
	private String	orgPath;
	private String	note;
	private char	status;
	private boolean	active;
	private Date	createdDatetime;
	
	private List<AgreementTermInstance> agreementTerms;
	
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

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
		idCheckDigit = AppConstants.appendCheckDigit(id);
	}

	public int getIdCheckDigit() {
		return idCheckDigit;
	}

	public void setIdCheckDigit(int idCheckDigit) {
		this.idCheckDigit = idCheckDigit;
		this.id = idCheckDigit / 10;
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

	public String getAgreementTypeCode() {
		return agreementTypeCode;
	}

	public void setAgreementTypeCode(String agreementTypeCode) {
		this.agreementTypeCode = agreementTypeCode;
	}

	public String getAgreementTypeDescription() {
		return agreementTypeDescription;
	}

	public void setAgreementTypeDescription(String agreementTypeDescription) {
		this.agreementTypeDescription = agreementTypeDescription;
	}

	public String getCommissionCode() {
		return commissionCode;
	}

	public void setCommissionCode(String commissionCode) {
		this.commissionCode = commissionCode;
	}

	public String getCommissionCodeDescription() {
		return commissionCodeDescription;
	}

	public void setCommissionCodeDescription(String commissionCodeDescription) {
		this.commissionCodeDescription = commissionCodeDescription;
	}

	public String getDeleteReasonCode() {
		return deleteReasonCode;
	}

	public void setDeleteReasonCode(String deleteReasonCode) {
		this.deleteReasonCode = deleteReasonCode;
	}

	public String getDeleteReasonDescription() {
		return deleteReasonDescription;
	}

	public void setDeleteReasonDescription(String deleteReasonDescription) {
		this.deleteReasonDescription = deleteReasonDescription;
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

	public List<AgreementTermInstance> getAgreementTerms() {
		return agreementTerms;
	}

	public void setAgreementTerms(List<AgreementTermInstance> agreementTerms) {
		this.agreementTerms = agreementTerms;
	}

	public String toString() {
		return "Agreement " + idCheckDigit;
	}
}
