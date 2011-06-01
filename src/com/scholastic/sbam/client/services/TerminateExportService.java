package com.scholastic.sbam.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.scholastic.sbam.shared.objects.ExportProcessReport;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("terminateExport")
public interface TerminateExportService extends RemoteService {
	ExportProcessReport terminateExport(String terminationReason) throws IllegalArgumentException;
}
