package com.scholastic.sbam.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.scholastic.sbam.shared.objects.ExportProcessReport;

public interface InitiateExportServiceAsync {

	void initiateExport(AsyncCallback<ExportProcessReport> callback);

}
