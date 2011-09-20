package com.scholastic.sbam.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("updateUserPassword")
public interface UpdateUserPasswordService extends RemoteService {
	String updateUserPassword(String user, String oldPassword, String newPassword) throws IllegalArgumentException;
}
