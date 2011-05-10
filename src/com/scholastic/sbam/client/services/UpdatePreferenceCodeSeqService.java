package com.scholastic.sbam.client.services;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("updatePreferenceCodeSeq")
public interface UpdatePreferenceCodeSeqService extends RemoteService {
	public String updatePreferenceCodeSeq(String catCode, List<String> sequence) throws IllegalArgumentException;
}
