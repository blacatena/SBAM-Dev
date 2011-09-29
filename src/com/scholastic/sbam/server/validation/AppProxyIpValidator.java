package com.scholastic.sbam.server.validation;

import java.util.ArrayList;
import java.util.List;

import com.scholastic.sbam.server.database.codegen.Proxy;
import com.scholastic.sbam.server.database.codegen.ProxyIp;
import com.scholastic.sbam.server.database.objects.DbProxy;
import com.scholastic.sbam.server.database.objects.DbProxyIp;
import com.scholastic.sbam.shared.objects.ProxyIpInstance;
import com.scholastic.sbam.shared.util.AppConstants;

public class AppProxyIpValidator {
	
	public static int MIN_CODE_LEN = 2;
	
	private List<String> messages = new ArrayList<String>();
	
	private	ProxyIpInstance original;
	private ProxyIp			 proxyIp;

	public List<String> validateProxyIp(ProxyIpInstance instance) {
		if (instance.getStatus() == AppConstants.STATUS_DELETED)
			return null;

		patchInstance(instance);

		validateProxyId(instance);
		validateProxyIpId(instance, instance.isNewRecord());
		validateStatus(instance.getStatus());
		validateIpRange(instance);
		return messages;
	}
	
	public void patchInstance(ProxyIpInstance instance) {
		if (instance.isNewRecord() && instance.getStatus() == AppConstants.STATUS_ANY_NONE)
			instance.setStatus(AppConstants.STATUS_ACTIVE);
		
		if (instance.getIpLo() > 0 && instance.getIpHi() == 0)
			instance.setIpHi(instance.getIpLo());
	}
	
	public List<String> validateProxyId(ProxyIpInstance instance) {
		if (instance.getProxyId() < 0) {
			addMessage("An proxy ID is required.");
		} else {
			Proxy proxy = DbProxy.getById(instance.getProxyId());
			if (proxy == null) {
				addMessage("Proxy " + instance.getProxyId() + " does not exist.");
			}
		}
		return messages;
	}
	
	public List<String> validateProxyIpId(ProxyIpInstance instance, boolean isNew) {
		if (isNew) {
			validateNewProxyIpId(instance.getProxyId(), instance.getIpId(), instance.getIpLo(), instance.getIpHi());
		} else {
			validateOldProxyIpId(instance.getProxyId(), instance.getIpId());
		}
		return messages;
	}
	
	public List<String> validateOldProxyIpId(int proxyId, int ipId) {
		
		if (!loadProxyIp())
			return messages;
		
		if (proxyId <= 0 && ipId <= 0) {
			addMessage("An proxy ID or IP ID is required.");
			return messages;
		}
		
		if (proxyIp.getId().getProxyId() != proxyId)
			addMessage("Proxy ID cannot be changed.");
		
		if (proxyIp.getId().getIpId() != ipId)
			addMessage("IP ID cannot be changed.");
		
		return messages;
	}
	
	public List<String> validateNewProxyIpId(int proxyId, int ipId, long ipLo, long ipHi) {
		if (proxyId > 0 && ipId > 0 ) {
			ProxyIp conflict = DbProxyIp.getById(proxyId, ipId);
			if (conflict != null && conflict.getStatus() != AppConstants.STATUS_DELETED) {
				addMessage("Proxy IP already exists.");
			}
		} else if (proxyId > 0) {
			ProxyIp conflict = DbProxyIp.findInRange(proxyId, ipLo, ipHi);
			if (conflict != null && conflict.getStatus() != AppConstants.STATUS_DELETED) {
				addMessage("Proxy IP already exists.");
			}
		}
		return messages;
	}
	
	public List<String> validateStatus(char status) {
		if (status != AppConstants.STATUS_ACTIVE && status != AppConstants.STATUS_INACTIVE && status != AppConstants.STATUS_DELETED)
			addMessage("Invalid status " + status);
		return messages;
	}
	
	public void validateIpRange(ProxyIpInstance instance) {
		if (instance.getIpLo() == 0 && instance.getIpHi() > 0)
			instance.setIpLo(instance.getIpHi());
		if (instance.getIpLo() != 0) {
			if (instance.getIpHi() == 0)
				instance.setIpHi(instance.getIpHi());
			if (instance.getIpLo() > instance.getIpHi())
				addMessage("Low IP cannot be greater than high IP.");
			instance.assignIpRangeCode();
			if (instance.getIpRangeCode() == null || instance.getIpRangeCode().length() == 0) {
				addMessage("IP range too broad.");
			}
		}
	}
	
	private boolean loadProxyIp() {
		if (proxyIp == null) {
			proxyIp = DbProxyIp.getById(original.getProxyId(), original.getIpId());
			if (proxyIp == null) {
				addMessage("Unexpected Error: Original proxy IP not found in the database.");
				return false;
			}
		}
		return true;
	}
	
	private void addMessage(String message) {
		if (message != null && message.length() > 0)
			messages.add(message);
	}

	public ProxyIpInstance getOriginal() {
		return original;
	}

	public void setOriginal(ProxyIpInstance original) {
		this.original = original;
	}

	public List<String> getMessages() {
		return messages;
	}

	public void setMessages(List<String> messages) {
		this.messages = messages;
	}
}
