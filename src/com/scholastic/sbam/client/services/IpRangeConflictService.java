package com.scholastic.sbam.client.services;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.scholastic.sbam.shared.objects.MethodConflictInstance;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("getIpRangeConflicts")
public interface IpRangeConflictService extends RemoteService {
	List<MethodConflictInstance> getIpRangeConflicts(long ipLo, long ipHi) throws IllegalArgumentException, Exception;
}
