package com.scholastic.sbam.server.authentication;

import com.google.gwt.user.client.rpc.IsSerializable;
import com.scholastic.sbam.server.database.codegen.AeAuthUnit;
import com.scholastic.sbam.server.database.codegen.AeConflict;
import com.scholastic.sbam.server.database.codegen.AeConflictId;
import com.scholastic.sbam.server.database.codegen.AeIp;
import com.scholastic.sbam.server.database.codegen.AePuid;
import com.scholastic.sbam.server.database.codegen.AeRsurl;
import com.scholastic.sbam.server.database.codegen.AeUid;
import com.scholastic.sbam.server.database.codegen.AeUrl;
import com.scholastic.sbam.server.database.codegen.AuthMethod;
import com.scholastic.sbam.server.database.objects.DbAeConflict;
import com.scholastic.sbam.server.database.util.HibernateUtil;
import com.scholastic.sbam.shared.objects.ExportProcessMessage;

public class AuthenticationConflict implements IsSerializable {
	public static int	PASSWORD_MISMATCH	=	1;
	public static int	REMOTE_MISMATCH		=	2;
	public static int	USER_TYPE_MISMATCH	=	3;
	public static int	CUSTOMER_MISMATCH	=	4;
	
	public ExportProcessMessage	message;
	public int					errorType;
	public int					auId;
	public AuthMethod			authMethod;
	public AeIp					aeIp;
	public AeUid				aeUid;
	public AePuid				aePuid;
	public AeUrl				aeUrl;
	public AeRsurl				aeRsUrl;
	
	public int					referenceAuId;
	
	public AuthenticationConflict() {
	}

	public AuthenticationConflict(AuthMethod authMethod, AeIp aeIp, int errorType) {
		this.authMethod		= authMethod;
		this.aeIp			= aeIp;
		this.errorType		= errorType;
		
		this.auId			= (aeIp != null) ? aeIp.getId().getAuId() : 0;
	}

	public AuthenticationConflict(AuthMethod authMethod, AeUid aeUid, int errorType) {
		this.authMethod		= authMethod;
		this.aeUid			= aeUid;
		this.errorType		= errorType;
		
		this.auId			= (aeUid != null) ? aeUid.getId().getAuId() : 0;
	}

	public AuthenticationConflict(AuthMethod authMethod, AePuid aePuid, int errorType) {
		this.authMethod		= authMethod;
		this.aePuid			= aePuid;
		this.errorType		= errorType;
		
		this.auId			= (aePuid != null) ? aePuid.getId().getAuId() : 0;
	}

	public AuthenticationConflict(AuthMethod authMethod, AeUrl aeUrl, int errorType) {
		this.authMethod		= authMethod;
		this.aeUrl			= aeUrl;
		this.errorType		= errorType;
		
		this.auId			= (aeUrl != null) ? aeUrl.getId().getAuId() : 0;
	}

	public AuthenticationConflict(AuthMethod authMethod, AeRsurl aeRsUrl, int errorType) {
		this.authMethod		= authMethod;
		this.aeRsUrl		= aeRsUrl;
		this.errorType		= errorType;
		
		this.auId			= (aeRsUrl != null) ? aeRsUrl.getId().getAuId() : 0;
	}

	public AuthenticationConflict(AeAuthUnit authUnit, AeIp aeIp, int errorType) {
		this.authMethod		= null;
		this.aeIp			= aeIp;
		this.errorType		= errorType;
		
		this.auId			= (aeIp != null) ? aeIp.getId().getAuId() : 0;
		this.referenceAuId	= authUnit.getAuId();
	}

	public AuthenticationConflict(AeAuthUnit authUnit, AeUid aeUid, int errorType) {
		this.authMethod		= null;
		this.aeUid			= aeUid;
		this.errorType		= errorType;
		
		this.auId			= (aeUid != null) ? aeUid.getId().getAuId() : 0;
		this.referenceAuId	= authUnit.getAuId();
	}

	public AuthenticationConflict(AeAuthUnit authUnit, AePuid aePuid, int errorType) {
		this.authMethod		= null;
		this.aePuid			= aePuid;
		this.errorType		= errorType;
		
		this.auId			= (aePuid != null) ? aePuid.getId().getAuId() : 0;
		this.referenceAuId	= authUnit.getAuId();
	}

	public AuthenticationConflict(AeAuthUnit authUnit, AeUrl aeUrl, int errorType) {
		this.authMethod		= null;
		this.aeUrl			= aeUrl;
		this.errorType		= errorType;
		
		this.auId			= (aeUrl != null) ? aeUrl.getId().getAuId() : 0;
		this.referenceAuId	= authUnit.getAuId();
	}

	public AuthenticationConflict(AeAuthUnit authUnit, AeRsurl aeRsUrl, int errorType) {
		this.authMethod		= null;
		this.aeRsUrl		= aeRsUrl;
		this.errorType		= errorType;
		
		this.auId			= (aeRsUrl != null) ? aeRsUrl.getId().getAuId() : 0;
		this.referenceAuId	= authUnit.getAuId();
	}
	
	public void persist(int aeId) {
		boolean handleTransaction = !HibernateUtil.isTransactionInProgress();
		
		if (handleTransaction) {
			HibernateUtil.openSession();
			HibernateUtil.startTransaction();
		}
		
		AeConflict aeConflict = new AeConflict();
		
		AeConflictId aeConflictId = new AeConflictId();
		aeConflictId.setAeId(aeId);
		aeConflictId.setConflictId(DbAeConflict.getNextConflictId(aeId));
		
		aeConflict.setId(aeConflictId);
		aeConflict.setAuId(auId);
		
		aeConflict.setConflictMsg(toString());
		aeConflict.setConflictKey(getKey());
		aeConflict.setConflictType(errorType);
		aeConflict.setReferenceAuId(referenceAuId);
		
		if (authMethod != null) {
			aeConflict.setMethodType(authMethod.getId().getMethodType());
			aeConflict.setIpLo(authMethod.getIpLo());
			aeConflict.setIpHi(authMethod.getIpHi());
			aeConflict.setUserId(authMethod.getUserId());
			aeConflict.setPassword(authMethod.getPassword());
			aeConflict.setUrl(authMethod.getUrl());
		} else if (aeIp != null) {
			aeConflict.setMethodType("ip");
			aeConflict.setIpLo(aeIp.getIpLo());
			aeConflict.setIpHi(aeIp.getIpHi());
			aeConflict.setUserId("");
			aeConflict.setPassword("");
			aeConflict.setUrl("");
		} else if (aeUid != null) {
			aeConflict.setMethodType("uid");
			aeConflict.setIpLo(0);
			aeConflict.setIpHi(0);
			aeConflict.setUserId(aeUid.getId().getUserId());
			aeConflict.setPassword(aeUid.getPassword());
			aeConflict.setUrl("");
		} else if (aeUrl != null) {
			aeConflict.setMethodType("url");
			aeConflict.setIpLo(0);
			aeConflict.setIpHi(0);
			aeConflict.setUserId("");
			aeConflict.setPassword("");
			aeConflict.setUrl(aeUrl.getId().getUrl());
		} else if (aePuid != null) {
			aeConflict.setMethodType("puid");
			aeConflict.setIpLo(aePuid.getIpLo());
			aeConflict.setIpHi(aePuid.getIpHi());
			aeConflict.setUserId(aePuid.getId().getUserId());
			aeConflict.setPassword(aePuid.getPassword());
			aeConflict.setUrl("");
		} else if (aeRsUrl != null) {
			aeConflict.setMethodType("rsurl");
			aeConflict.setIpLo(0);
			aeConflict.setIpHi(0);
			aeConflict.setUserId("");
			aeConflict.setPassword("");
			aeConflict.setUrl(aeRsUrl.getId().getUrl());
		} else {
			aeConflict.setMethodType("???");
			aeConflict.setIpLo(0);
			aeConflict.setIpHi(0);
			aeConflict.setUserId("");
			aeConflict.setPassword("");
			aeConflict.setUrl("");
		}
		
		DbAeConflict.persist(aeConflict);
		
		if (handleTransaction) {
			HibernateUtil.endTransaction();
			HibernateUtil.closeSession();
		}
	}
	
	public String toString() {
		if (message != null && message.getMessage() != null)
			return message.getMessage();
		return getKey() + " : " + getTypeMessage() + ".";
	}
	
	public String getTypeMessage() {
		if (errorType == PASSWORD_MISMATCH)
			return "Password mismatch";
		if (errorType == REMOTE_MISMATCH)
			return "Remote mismatch";
		if (errorType == USER_TYPE_MISMATCH)
			return "User type mismatch";
		if (errorType == CUSTOMER_MISMATCH)
			return "Customer mismatch";
		return "Unknown conflict type " + errorType;
	}
		
	public String getKey() {
		if (authMethod != null)
			return
				authMethod.getId().getAgreementId() + ":" +
				authMethod.getId().getUcn() + ":" +
				authMethod.getId().getUcnSuffix() + ":" +
				authMethod.getId().getSiteLocCode() + ":" +
				authMethod.getId().getMethodType() + ":" + 
				authMethod.getId().getMethodKey();
		if (aeIp != null)
			return
				aeIp.getId().getAeId() + ":" + aeIp.getId().getAuId() + ":" + aeIp.getId().getIp();
		if (aeUid != null)
			return
				aeUid.getId().getAeId() + ":" + aeUid.getId().getAuId() + ":" + aeUid.getId().getUserId();
		if (aeUrl != null)
			return
			aeUrl.getId().getAeId() + ":" + aeUrl.getId().getAuId() + ":" + aeUrl.getId().getUrl();
		if (aePuid != null)
			return
			aePuid.getId().getAeId() + ":" + aePuid.getId().getAuId() + ":" + aePuid.getId().getUserId() + ":" + aePuid.getId().getIp();
		if (aeRsUrl != null)
			return
			aeRsUrl.getId().getAeId() + ":" + aeRsUrl.getId().getAuId() + ":" + aeRsUrl.getId().getUrl();
		return "???";
	}

	public ExportProcessMessage getMessage() {
		return message;
	}

	public void setMessage(ExportProcessMessage message) {
		this.message = message;
	}

	public int getErrorType() {
		return errorType;
	}

	public void setErrorType(int errorType) {
		this.errorType = errorType;
	}

	public int getAuId() {
		return auId;
	}

	public void setAuId(int auId) {
		this.auId = auId;
	}

	public AuthMethod getAuthMethod() {
		return authMethod;
	}

	public void setAuthMethod(AuthMethod authMethod) {
		this.authMethod = authMethod;
	}

	public AeIp getAeIp() {
		return aeIp;
	}

	public void setAeIp(AeIp aeIp) {
		this.aeIp = aeIp;
	}

	public AeUid getAeUid() {
		return aeUid;
	}

	public void setAeUid(AeUid aeUid) {
		this.aeUid = aeUid;
	}

	public AePuid getAePuid() {
		return aePuid;
	}

	public void setAePuid(AePuid aePuid) {
		this.aePuid = aePuid;
	}

	public AeUrl getAeUrl() {
		return aeUrl;
	}

	public void setAeUrl(AeUrl aeUrl) {
		this.aeUrl = aeUrl;
	}

	public AeRsurl getAeRsUrl() {
		return aeRsUrl;
	}

	public void setAeRsUrl(AeRsurl aeRsUrl) {
		this.aeRsUrl = aeRsUrl;
	}

	public int getReferenceAuId() {
		return referenceAuId;
	}

	public void setReferenceAuId(int referenceAuId) {
		this.referenceAuId = referenceAuId;
	}
	
}
