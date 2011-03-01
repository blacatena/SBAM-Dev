package com.scholastic.sbam.client.services;

import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.scholastic.sbam.shared.objects.SearchResultInstance;

public interface HelpTextSearchServiceAsync {

	void searchHelpText(PagingLoadConfig loadConfig, String filter, AsyncCallback<PagingLoadResult<SearchResultInstance>> callback);

}
