package com.scholastic.sbam.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.scholastic.sbam.shared.objects.UpdateResponse;
import com.scholastic.sbam.shared.objects.CancelReasonInstance;

public interface UpdateCancelReasonServiceAsync {

	void updateCancelReason(CancelReasonInstance beanModel, AsyncCallback<UpdateResponse<CancelReasonInstance>> callback);

}
