package com.scholastic.sbam.client.services;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.scholastic.sbam.shared.objects.UserInstance;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("getUsers")
public interface UserListService extends RemoteService {
	List<UserInstance> getUsers(String userName, String firstName, String lastName, String email) throws IllegalArgumentException;
}
