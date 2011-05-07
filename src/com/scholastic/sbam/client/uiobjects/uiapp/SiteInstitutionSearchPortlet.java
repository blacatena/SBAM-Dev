package com.scholastic.sbam.client.uiobjects.uiapp;

import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.scholastic.sbam.client.services.SiteInstitutionSearchService;
import com.scholastic.sbam.client.services.SiteInstitutionSearchServiceAsync;
import com.scholastic.sbam.client.services.SiteInstitutionWordService;
import com.scholastic.sbam.client.services.SiteInstitutionWordServiceAsync;
import com.scholastic.sbam.client.util.UiConstants;
import com.scholastic.sbam.shared.objects.FilterWordInstance;
import com.scholastic.sbam.shared.objects.InstitutionInstance;
import com.scholastic.sbam.shared.objects.SynchronizedPagingLoadResult;

public class SiteInstitutionSearchPortlet extends InstitutionSearchPortletBase {
	protected final SiteInstitutionSearchServiceAsync customerSearchService = GWT.create(SiteInstitutionSearchService.class);
	protected final SiteInstitutionWordServiceAsync   customerWordService   = GWT.create(SiteInstitutionWordService.class);

	
	public SiteInstitutionSearchPortlet() {
		super(AppPortletIds.SITE_INSTITUTION_SEARCH.getHelpTextId());
	}
	
	public void setPortletRenderValues() {
		setHeading("Site Institution Search");
		setToolTip(UiConstants.getQuickTip("Use this tool to find institutions that are recipients of product services."));
	}
	
	public void invokeSearchService(PagingLoadConfig loadConfig, String filter, boolean includeAgreementSummaries, long searchSyncId, AsyncCallback<SynchronizedPagingLoadResult<InstitutionInstance>> myCallback) {
		customerSearchService.getSiteInstitutions((PagingLoadConfig) loadConfig, filter, true, searchSyncId, myCallback);
	}
	
	protected void invokeWordService(PagingLoadConfig loadConfig, AsyncCallback<PagingLoadResult<FilterWordInstance>>myCallback) {
		customerWordService.getSiteInstitutionWords((PagingLoadConfig) loadConfig, myCallback);
	}
}
