package com.scholastic.sbam.client.services;

import java.util.List;

import com.extjs.gxt.ui.client.data.LoadConfig;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.scholastic.sbam.shared.objects.WelcomeMessageInstance;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("getWelcomeMessageList")
public interface WelcomeMessageListService extends RemoteService {
	List<WelcomeMessageInstance> getWelcomeMessageList(LoadConfig loadConfig) throws IllegalArgumentException;
}
