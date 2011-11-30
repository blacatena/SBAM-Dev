package com.scholastic.sbam.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.scholastic.sbam.shared.objects.StatsAdminInstance;

public interface StatsAdminGetServiceAsync {

	void getStatsAdmin(int ucn, AsyncCallback<StatsAdminInstance> callback);

}
