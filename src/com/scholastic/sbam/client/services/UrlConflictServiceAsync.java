package com.scholastic.sbam.client.services;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.scholastic.sbam.shared.objects.MethodConflictInstance;

public interface UrlConflictServiceAsync {
	void getUrlConflicts(String url, AsyncCallback<List<MethodConflictInstance>> callback);
}
