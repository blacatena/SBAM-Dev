package com.scholastic.sbam.client.services;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.scholastic.sbam.shared.objects.HelpTextIndexInstance;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("getHelpTextIndex")
public interface HelpTextIndexService extends RemoteService {
	List<HelpTextIndexInstance> getHelpTextIndex() throws IllegalArgumentException;
}
