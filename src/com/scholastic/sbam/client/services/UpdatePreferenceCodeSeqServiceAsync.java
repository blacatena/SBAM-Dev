package com.scholastic.sbam.client.services;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface UpdatePreferenceCodeSeqServiceAsync {

	void updatePreferenceCodeSeq(String catCode, List<String> sequence, AsyncCallback<String> callback);

}
