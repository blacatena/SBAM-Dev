package com.scholastic.sbam.client.services;

import com.extjs.gxt.ui.client.data.LoadConfig;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.scholastic.sbam.shared.objects.AgreementNotesTuple;
import com.scholastic.sbam.shared.objects.SynchronizedPagingLoadResult;

public interface AgreementNotesSearchServiceAsync {

	void searchAgreementNotes(LoadConfig config, long syncId, AsyncCallback<SynchronizedPagingLoadResult<AgreementNotesTuple>> callback);

}
