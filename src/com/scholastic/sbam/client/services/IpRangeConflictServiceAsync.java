package com.scholastic.sbam.client.services;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.scholastic.sbam.shared.objects.MethodConflictInstance;

public interface IpRangeConflictServiceAsync {
	void getIpRangeConflicts(long ipLo, long ipHi, AsyncCallback<List<MethodConflictInstance>> callback);
}
