package com.scholastic.sbam.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.scholastic.sbam.shared.objects.InstitutionInstance;

public interface InstitutionGetServiceAsync {

	void getInstitution(int ucn, AsyncCallback<InstitutionInstance> callback);

}
