package com.scholastic.sbam.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.scholastic.sbam.shared.objects.UpdateResponse;
import com.scholastic.sbam.shared.objects.ContactInstance;

public interface UpdateContactNoteServiceAsync {

	void updateContactNote(ContactInstance beanModel, AsyncCallback<UpdateResponse<ContactInstance>> callback);

}
