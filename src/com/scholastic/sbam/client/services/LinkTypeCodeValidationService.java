package com.scholastic.sbam.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.scholastic.sbam.shared.objects.BetterRowEditInstance;
import com.scholastic.sbam.shared.validation.AsyncValidationResponse;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("validateLinkTypeCode")
public interface LinkTypeCodeValidationService extends RemoteService {
	AsyncValidationResponse validate(String value, BetterRowEditInstance dataInstance, int validationCount) throws IllegalArgumentException, Exception;
}
