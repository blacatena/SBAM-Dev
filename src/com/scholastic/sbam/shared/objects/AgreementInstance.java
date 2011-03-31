package com.scholastic.sbam.shared.objects;

import java.util.Date;
import java.util.List;

import com.extjs.gxt.ui.client.data.BeanModelTag;
import com.google.gwt.user.client.rpc.IsSerializable;
import com.scholastic.sbam.shared.util.AppConstants;

public class AgreementInstance extends BetterRowEditInstance implements BeanModelTag, IsSerializable, UserCacheTarget {

	private int		id;
	private int		idCheckDigit;
	private int		billUcn;
	private int		billUcnSuffix;
	private int		agreementLinkId;
	private String	agreementTypeCode;
	private String	commissionCode;
	private String	deleteReasonCode;
	private int		workstations;
	private int		buildings;
	private int		population;
	private int		enrollment;
	private String	orgPath;
	private String	note;
	private char	status;
	private boolean	active;
	private Date	createdDatetime;
	
	private double	currentValue;

	private AgreementTypeInstance	agreementType;
	private CommissionTypeInstance	commissionType;
	private DeleteReasonInstance	deleteReason;
	private InstitutionInstance		institution;
	private AgreementLinkInstance	agreementLink;
	
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

	public int getAgreementLinkId() {
		return agreementLinkId;
	}

	public void setAgreementLinkId(int agreementLinkId) {
		this.agreementLinkId = agreementLinkId;
	}

	public String getAgreementTypeCode() {
		return agreementTypeCode;
	}

	public void setAgreementTypeCode(String agreementTypeCode) {
		this.agreementTypeCode = agreementTypeCode;
	}

	public String getCommissionCode() {
		return commissionCode;
	}

	public void setCommissionCode(String commissionCode) {
		this.commissionCode = commissionCode;
	}

	public String getDeleteReasonCode() {
		return deleteReasonCode;
	}

	public void setDeleteReasonCode(String deleteReasonCode) {
		this.deleteReasonCode = deleteReasonCode;
	}

	public int getWorkstations() {
		return workstations;
	}

	public void setWorkstations(int workstations) {
		this.workstations = workstations;
	}

	public int getBuildings() {
		return buildings;
	}

	public void setBuildings(int buildings) {
		this.buildings = buildings;
	}

	public int getPopulation() {
		return population;
	}

	public void setPopulation(int population) {
		this.population = population;
	}

	public int getEnrollment() {
		return enrollment;
	}

	public void setEnrollment(int enrollment) {
		this.enrollment = enrollment;
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

	public double getCurrentValue() {
		return currentValue;
	}

	public void setCurrentValue(double currentValue) {
		this.currentValue = currentValue;
	}

	public AgreementTypeInstance getAgreementType() {
		return agreementType;
	}

	public void setAgreementType(AgreementTypeInstance agreementType) {
		this.agreementType = agreementType;
		if (agreementType == null)
			this.agreementTypeCode = "";
		else
			this.agreementTypeCode = agreementType.getAgreementTypeCode();
	}

	public CommissionTypeInstance getCommissionType() {
		return commissionType;
	}

	public void setCommissionType(CommissionTypeInstance commissionType) {
		this.commissionType = commissionType;
		if (commissionType == null)
			this.commissionCode = "";
		else
			this.commissionCode = commissionType.getCommissionCode();
	}

	public DeleteReasonInstance getDeleteReason() {
		return deleteReason;
	}

	public void setDeleteReason(DeleteReasonInstance deleteReason) {
		this.deleteReason = deleteReason;
		if (deleteReason == null)
			this.deleteReasonCode = "";
		else
			this.deleteReasonCode = deleteReason.getDeleteReasonCode();
	}

	public List<AgreementTermInstance> getAgreementTerms() {
		return agreementTerms;
	}

	public InstitutionInstance getInstitution() {
		return institution;
	}

	public void setInstitution(InstitutionInstance institution) {
		this.institution = institution;
		if (institution == null)
			this.billUcn = 0;
		else
			this.billUcn = institution.getUcn();
	}

	public AgreementLinkInstance getAgreementLink() {
		return agreementLink;
	}

	public void setAgreementLink(AgreementLinkInstance agreementLink) {
		this.agreementLink = agreementLink;
		if (agreementLink == null)
			this.agreementLinkId = 0;
		else
			this.agreementLinkId = agreementLink.getLinkId();
	}

	public void setAgreementTerms(List<AgreementTermInstance> agreementTerms) {
		this.agreementTerms = agreementTerms;
	}

	public String toString() {
		return "Agreement " + idCheckDigit;
	}
	
	public static String getUserCacheCategory() {
		return "Agreement";
	}

	@Override
	public String userCacheCategory() {
		return getUserCacheCategory();
	}

	@Override
	public String userCacheStringKey() {
		return null;
	}

	@Override
	public int userCacheIntegerKey() {
		return id;
	}
	
	
}
