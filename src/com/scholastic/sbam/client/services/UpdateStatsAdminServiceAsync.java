package com.scholastic.sbam.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.scholastic.sbam.shared.objects.UpdateResponse;
import com.scholastic.sbam.shared.objects.StatsAdminInstance;

public interface UpdateStatsAdminServiceAsync {

	void updateStatsAdmin(StatsAdminInstance beanModel, AsyncCallback<UpdateResponse<StatsAdminInstance>> callback);

}
