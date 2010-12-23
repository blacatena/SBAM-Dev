package com.scholastic.sbam.client.services;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.scholastic.sbam.shared.objects.UserInstance;

public interface UserListServiceAsync {

	void getUsers(String userName, String firstName, String lastName, String email, AsyncCallback<List<UserInstance>> callback);

}
