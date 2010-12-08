package com.scholastic.sbam.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("getWelcomeMessages")
public interface WelcomeMessageService extends RemoteService {
	String getWelcomeMessages() throws IllegalArgumentException;
}
