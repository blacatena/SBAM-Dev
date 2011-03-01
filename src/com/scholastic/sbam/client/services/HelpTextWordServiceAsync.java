package com.scholastic.sbam.client.services;

import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.scholastic.sbam.shared.objects.FilterWordInstance;

public interface HelpTextWordServiceAsync {

	void getHelpTextWords(PagingLoadConfig loadConfig, AsyncCallback<PagingLoadResult<FilterWordInstance>> callback);

}
