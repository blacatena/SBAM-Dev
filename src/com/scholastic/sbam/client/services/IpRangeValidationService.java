package com.scholastic.sbam.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.scholastic.sbam.shared.objects.MethodIdInstance;
import com.scholastic.sbam.shared.validation.AsyncValidationResponse;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("validateIpRange")
public interface IpRangeValidationService extends RemoteService {
	AsyncValidationResponse validateIpRange(long ipLo, long ipHi, MethodIdInstance methodId, int validationCount) throws IllegalArgumentException, Exception;
}
