package com.scholastic.sbam.client.uiobjects.uiapp;

import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.scholastic.sbam.client.services.InstitutionSearchService;
import com.scholastic.sbam.client.services.InstitutionSearchServiceAsync;
import com.scholastic.sbam.client.services.InstitutionWordService;
import com.scholastic.sbam.client.services.InstitutionWordServiceAsync;
import com.scholastic.sbam.client.util.UiConstants;
import com.scholastic.sbam.shared.objects.FilterWordInstance;
import com.scholastic.sbam.shared.objects.InstitutionInstance;
import com.scholastic.sbam.shared.objects.SynchronizedPagingLoadResult;

public class InstitutionSearchPortlet extends InstitutionSearchPortletBase {
	
	protected final InstitutionSearchServiceAsync institutionSearchService = GWT.create(InstitutionSearchService.class);
	protected final InstitutionWordServiceAsync   institutionWordService   = GWT.create(InstitutionWordService.class);
	
	public InstitutionSearchPortlet() {
		super(AppPortletIds.FULL_INSTITUTION_SEARCH.getHelpTextId());
	}
	
	public void setPortletRenderValues() {
		setHeading("Institution Search");
		setToolTip(UiConstants.getQuickTip("Use this tool to find institutions whether or not they are already customers."));
	}
	
	public void invokeSearchService(PagingLoadConfig loadConfig, String filter, boolean includeAgreementSummaries, long searchSyncId, AsyncCallback<SynchronizedPagingLoadResult<InstitutionInstance>> myCallback) {
		institutionSearchService.getInstitutions((PagingLoadConfig) loadConfig, filter, true, searchSyncId, myCallback);
	}
	
	protected void invokeWordService(PagingLoadConfig loadConfig, AsyncCallback<PagingLoadResult<FilterWordInstance>>myCallback) {
		institutionWordService.getInstitutionWords((PagingLoadConfig) loadConfig, myCallback);
	}

}