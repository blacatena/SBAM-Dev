package com.scholastic.sbam.shared.objects;

import java.util.Date;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.BeanModelFactory;
import com.extjs.gxt.ui.client.data.BeanModelLookup;
import com.extjs.gxt.ui.client.data.BeanModelTag;
import com.google.gwt.user.client.rpc.IsSerializable;
import com.scholastic.sbam.shared.util.AppConstants;

public class AgreementLinkInstance extends BetterRowEditInstance implements BeanModelTag, IsSerializable {

	private static BeanModelFactory beanModelfactory;

	private int		linkId;
	private int		linkIdCheckDigit;
	private int		ucn;
	private String	linkTypeCode;
	private String	note;
	private Date	createdDatetime;
	private boolean	active;
	private char	status;
	
	private LinkTypeInstance	linkType;
	private InstitutionInstance institution;
	
	@Override
	public void markForDeletion() {
		setStatus(AppConstants.STATUS_DELETED);
	}

	@Override
	public boolean thisIsDeleted() {
		return status == AppConstants.STATUS_DELETED;
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

	public int getLinkId() {
		return linkId;
	}

	public void setLinkId(int linkId) {
		this.linkId = linkId;
	}

	public int getLinkIdCheckDigit() {
		return linkIdCheckDigit;
	}

	public void setLinkIdCheckDigit(int linkIdCheckDigit) {
		this.linkIdCheckDigit = linkIdCheckDigit;
	}

	public int getUcn() {
		return ucn;
	}

	public void setUcn(int ucn) {
		this.ucn = ucn;
	}

	public String getLinkTypeCode() {
		return linkTypeCode;
	}

	public void setLinkTypeCode(String linkTypeCode) {
		this.linkTypeCode = linkTypeCode;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public InstitutionInstance getInstitution() {
		return institution;
	}

	public void setInstitution(InstitutionInstance institution) {
		this.institution = institution;
	}

	public LinkTypeInstance getLinkType() {
		return linkType;
	}

	public void setLinkType(LinkTypeInstance linkType) {
		this.linkType = linkType;
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
	
	public String getListStyle() {
		if (status == AppConstants.STATUS_NEW)
			return "list-new";
		if (status == AppConstants.STATUS_NULL)
			return "list-null";
		return "list-normal";
	}

	public String toString() {
		return "" + linkIdCheckDigit;
	}
	
	public static AgreementLinkInstance getEmptyInstance() {
		AgreementLinkInstance instance = new AgreementLinkInstance();
		instance.linkIdCheckDigit = 0;
		return instance;
	}
	
	public static AgreementLinkInstance getUnknownInstance(int code) {
		AgreementLinkInstance instance = new AgreementLinkInstance();
		instance.linkIdCheckDigit = 0;
		return instance;
	}
	
	public static AgreementLinkInstance getUnknownInstance(String code) {
		AgreementLinkInstance instance = new AgreementLinkInstance();
		instance.linkIdCheckDigit = 0;
		return instance;
	}

	public static BeanModel obtainModel(AgreementLinkInstance instance) {
		if (beanModelfactory == null)
			beanModelfactory  = BeanModelLookup.get().getFactory(AgreementLinkInstance.class);
		BeanModel model = beanModelfactory.createModel(instance);
		return model;
	}
	
	public String getDescriptionAndCode() {
		if (status == AppConstants.STATUS_NEW)
			return "Create a new agreement link.";
		if (status == AppConstants.STATUS_NULL)
			return "No link.";
		
		if (linkId <= 0)
			return "None";
		
		if (institution == null)
			return "" + linkIdCheckDigit;
		
		return linkIdCheckDigit + " [ " + institution.getInstitutionName() + " ]";
	}
}
