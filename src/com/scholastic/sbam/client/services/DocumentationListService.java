package com.scholastic.sbam.client.services;

import java.util.List;

import com.extjs.gxt.ui.client.data.LoadConfig;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.scholastic.sbam.shared.objects.DocumentationInstance;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("getDocumentationLinks")
public interface DocumentationListService extends RemoteService {
	List<DocumentationInstance> getDocumentationLinks(LoadConfig loadConfig) throws IllegalArgumentException;
}
