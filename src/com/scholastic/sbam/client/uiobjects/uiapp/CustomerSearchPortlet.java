package com.scholastic.sbam.client.uiobjects.uiapp;

import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.scholastic.sbam.client.services.CustomerSearchService;
import com.scholastic.sbam.client.services.CustomerSearchServiceAsync;
import com.scholastic.sbam.client.services.CustomerWordService;
import com.scholastic.sbam.client.services.CustomerWordServiceAsync;
import com.scholastic.sbam.client.util.IconSupplier;
import com.scholastic.sbam.client.util.UiConstants;
import com.scholastic.sbam.shared.objects.FilterWordInstance;
import com.scholastic.sbam.shared.objects.InstitutionInstance;
import com.scholastic.sbam.shared.objects.SynchronizedPagingLoadResult;

public class CustomerSearchPortlet extends InstitutionSearchPortletBase {
	protected final CustomerSearchServiceAsync customerSearchService = GWT.create(CustomerSearchService.class);
	protected final CustomerWordServiceAsync   customerWordService   = GWT.create(CustomerWordService.class);

	
	public CustomerSearchPortlet() {
		super(AppPortletIds.CUSTOMER_SEARCH.getHelpTextId());
	}
	
	protected void setPortletIcon() {
		IconSupplier.setIcon(this, IconSupplier.getCustomerIconName());
	}
	
	public void setPortletRenderValues() {
		setHeading("Customer Search");
		setToolTip(UiConstants.getQuickTip("Use this tool to find institutions that are paying customers for agreements."));
	}
	
	public void invokeSearchService(PagingLoadConfig loadConfig, String filter, boolean includeAgreementSummaries, long searchSyncId, AsyncCallback<SynchronizedPagingLoadResult<InstitutionInstance>> myCallback) {
		customerSearchService.getCustomers((PagingLoadConfig) loadConfig, filter, true, searchSyncId, myCallback);
	}
	
	protected void invokeWordService(PagingLoadConfig loadConfig, AsyncCallback<PagingLoadResult<FilterWordInstance>>myCallback) {
		customerWordService.getCustomerWords((PagingLoadConfig) loadConfig, myCallback);
	}
}
