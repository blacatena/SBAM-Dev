package com.scholastic.sbam.client.services;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.scholastic.sbam.shared.objects.InstitutionContactInstance;

public interface InstitutionContactListServiceAsync {

	void getInstitutionContacts(int ucn, char neStatus, AsyncCallback<List<InstitutionContactInstance>> callback);

}
