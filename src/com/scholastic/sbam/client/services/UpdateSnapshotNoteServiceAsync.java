package com.scholastic.sbam.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.scholastic.sbam.shared.objects.UpdateResponse;
import com.scholastic.sbam.shared.objects.SnapshotInstance;

public interface UpdateSnapshotNoteServiceAsync {

	void updateSnapshotNote(SnapshotInstance beanModel, AsyncCallback<UpdateResponse<SnapshotInstance>> callback);

}
