package com.scholastic.sbam.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.scholastic.sbam.shared.objects.UpdateResponse;
import com.scholastic.sbam.shared.objects.AgreementSiteInstance;

public interface UpdateAgreementSiteServiceAsync {

	void updateAgreementSite(AgreementSiteInstance beanModel, AsyncCallback<UpdateResponse<AgreementSiteInstance>> callback);

}
