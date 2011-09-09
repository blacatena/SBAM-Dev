package com.scholastic.sbam.client.services;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.scholastic.sbam.shared.objects.MethodConflictInstance;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("getUidConflicts")
public interface UidConflictService extends RemoteService {
	List<MethodConflictInstance> getUidConflicts(String url) throws IllegalArgumentException, Exception;
}
