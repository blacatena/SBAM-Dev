package com.scholastic.sbam.server.authentication;

import com.google.gwt.user.client.rpc.IsSerializable;
import com.scholastic.sbam.server.database.codegen.AeIp;
import com.scholastic.sbam.server.database.codegen.AePuid;
import com.scholastic.sbam.server.database.codegen.AeUid;
import com.scholastic.sbam.server.database.codegen.AeUrl;
import com.scholastic.sbam.server.database.codegen.AuthMethod;
import com.scholastic.sbam.shared.objects.ExportProcessMessage;

public class AuthenticationConflict implements IsSerializable {
	public static int	PASSWORD_MISMATCH	=	1;
	public static int	REMOTE_MISMATCH		=	2;
	public static int	USER_TYPE_MISMATCH	=	3;
	
	public ExportProcessMessage	message;
	public int					errorType;
	public AuthMethod			authMethod;
	public AeIp					aeIp;
	public AeUid				aeUid;
	public AePuid				aePuid;
	public AeUrl				aeUrl;
	
	public AuthenticationConflict() {
	}

	public AuthenticationConflict(AuthMethod authMethod, AeIp aeIp, int errorType) {
		this.authMethod		= authMethod;
		this.aeIp			= aeIp;
		this.errorType		= errorType;
	}

	public AuthenticationConflict(AuthMethod authMethod, AeUid aeUid, int errorType) {
		this.authMethod		= authMethod;
		this.aeUid			= aeUid;
		this.errorType		= errorType;
	}

	public AuthenticationConflict(AuthMethod authMethod, AePuid aePuid, int errorType) {
		this.authMethod		= authMethod;
		this.aePuid			= aePuid;
		this.errorType		= errorType;
	}

	public AuthenticationConflict(AuthMethod authMethod, AeUrl aeUrl, int errorType) {
		this.authMethod		= authMethod;
		this.aeUrl			= aeUrl;
		this.errorType		= errorType;
	}
}
