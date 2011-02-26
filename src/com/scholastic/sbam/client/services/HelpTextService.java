package com.scholastic.sbam.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.scholastic.sbam.shared.objects.HelpTextInstance;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("getHelpText")
public interface HelpTextService extends RemoteService {
	HelpTextInstance getHelpText(String id) throws IllegalArgumentException;
}
