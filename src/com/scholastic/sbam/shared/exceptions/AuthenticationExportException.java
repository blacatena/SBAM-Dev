package com.scholastic.sbam.shared.exceptions;

import com.google.gwt.user.client.rpc.IsSerializable;

public class AuthenticationExportException extends RuntimeException implements IsSerializable {

	private static final long serialVersionUID = -4720486977795927518L;

	public AuthenticationExportException() {
	  }

	  public AuthenticationExportException(String message) {
	    super(message);
	  }

	  public AuthenticationExportException(String message, Throwable cause) {
	    super(message, cause);
	  }

	  public AuthenticationExportException(Throwable cause) {
	    super(cause);
	  }

}
