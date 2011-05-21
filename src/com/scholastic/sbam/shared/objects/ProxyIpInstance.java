package com.scholastic.sbam.shared.objects;

import java.util.Date;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.BeanModelFactory;
import com.extjs.gxt.ui.client.data.BeanModelLookup;
import com.extjs.gxt.ui.client.data.BeanModelTag;
import com.google.gwt.user.client.rpc.IsSerializable;
import com.scholastic.sbam.shared.util.AppConstants;

public class ProxyIpInstance extends IpAddressInstance implements BeanModelTag, IsSerializable {

	private static BeanModelFactory beanModelfactory;

	private int			proxyId;
	private int			ipId;
	private long		ipLo;
	private long		ipHi;
	private String		ipRangeCode;
	private String		note;
	private char		approved;
	private char		status;
	private boolean		active;
	private Date		createdDatetime;
	
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

	public int getProxyId() {
		return proxyId;
	}

	public void setProxyId(int proxyId) {
		this.proxyId = proxyId;
	}
	
	public int getProxyIdCheckDigit() {
		return AppConstants.appendCheckDigit(proxyId);
	}

	public int getIpId() {
		return ipId;
	}

	public void setIpId(int ipId) {
		this.ipId = ipId;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public long getIpLo() {
		return ipLo;
	}

	public void setIpLo(long ipLo) {
		this.ipLo = ipLo;
		//	This is optimized to not bother computing the range if it has already been set, or both IPs are not yet set
		if (this.ipLo != 0 && this.ipHi != 0 && (this.ipRangeCode == null || this.ipRangeCode.length() == 0) )
			setIpRangeCode();
//		syncMethodKey();
	}

	public long getIpHi() {
		return ipHi;
	}

	public void setIpHi(long ipHi) {
		if (ipHi == 0)
			ipHi = ipLo;
		this.ipHi = ipHi;
		//	This is optimized to not bother computing the range if it has already been set, or both IPs are not yet set
		if (this.ipLo != 0 && this.ipHi != 0 && (this.ipRangeCode == null || this.ipRangeCode.length() == 0) )
			setIpRangeCode();
//		syncMethodKey();
	}

	public String getIpRangeCode() {
		return ipRangeCode;
	}
	
	private void setIpRangeCode() {
		ipRangeCode = getCommonIpRangeCode(ipLo, ipHi);
	}

	public void setIpRangeCode(String ipRangeCode) {
		this.ipRangeCode = ipRangeCode;
	}

	public char getApproved() {
		return approved;
	}

	public void setApproved(char approved) {
		this.approved = approved;
	}

	public boolean isApproved() {
		return approved == 'y' || approved == 'Y';
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
		return getProxyIdCheckDigit() + " : " + ipLo + " --> " + ipHi;
	}

	public static BeanModel obtainModel(ProxyInstance instance) {
		if (beanModelfactory == null)
			beanModelfactory  = BeanModelLookup.get().getFactory(ProxyInstance.class);
		BeanModel model = beanModelfactory.createModel(instance);
		return model;
	}
	
	public MethodIdInstance obtainMethodId() {
		MethodIdInstance mid = new MethodIdInstance();
		mid.setAgreementId(0);
		mid.setUcn(0);
		mid.setUcnSuffix(0);
		mid.setSiteLocCode(null);
		mid.setMethodType(null);
		mid.setMethodKey(0);
		mid.setProxyId(proxyId);
		mid.setIpId(ipId);
		return mid;
	}
}
