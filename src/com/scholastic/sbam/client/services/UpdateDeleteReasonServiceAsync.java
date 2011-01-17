package com.scholastic.sbam.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.scholastic.sbam.shared.objects.UpdateResponse;
import com.scholastic.sbam.shared.objects.DeleteReasonInstance;

public interface UpdateDeleteReasonServiceAsync {

	void updateDeleteReason(DeleteReasonInstance beanModel, AsyncCallback<UpdateResponse<DeleteReasonInstance>> callback);

}
