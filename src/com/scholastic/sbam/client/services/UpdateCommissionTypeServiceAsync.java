package com.scholastic.sbam.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.scholastic.sbam.shared.objects.UpdateResponse;
import com.scholastic.sbam.shared.objects.CommissionTypeInstance;

public interface UpdateCommissionTypeServiceAsync {

	void updateCommissionType(CommissionTypeInstance beanModel, AsyncCallback<UpdateResponse<CommissionTypeInstance>> callback);

}
