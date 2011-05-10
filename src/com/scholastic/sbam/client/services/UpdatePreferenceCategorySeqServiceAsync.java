package com.scholastic.sbam.client.services;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface UpdatePreferenceCategorySeqServiceAsync {

	void updatePreferenceCategorySeq(List<String> sequence, AsyncCallback<String> callback);

}
