package com.scholastic.sbam.client.uiobjects.fields;

import com.extjs.gxt.ui.client.Style.SortDir;
import com.extjs.gxt.ui.client.data.BasePagingLoader;
import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.BeanModelReader;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoader;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.scholastic.sbam.client.services.AgreementSiteInstitutionSearchService;
import com.scholastic.sbam.client.services.AgreementSiteInstitutionSearchServiceAsync;
import com.scholastic.sbam.shared.exceptions.ServiceNotReadyException;
import com.scholastic.sbam.shared.objects.InstitutionInstance;
import com.scholastic.sbam.shared.objects.SimpleKeyProvider;
import com.scholastic.sbam.shared.objects.SynchronizedPagingLoadResult;
import com.scholastic.sbam.shared.util.AppConstants;

public class AgreementSiteInstitutionSearchField extends ComboBox<BeanModel> {
	
	protected final AgreementSiteInstitutionSearchServiceAsync institutionSearchService = GWT.create(AgreementSiteInstitutionSearchService.class);
	
	protected long					searchSyncId	=	0;
	
	protected int						agreementId;

	protected String					sortField		=	"institutionName";
	protected SortDir					sortDir			=	SortDir.ASC;
	
	public AgreementSiteInstitutionSearchField() {
		super();
		
		PagingLoader<PagingLoadResult<InstitutionInstance>> loader = getInstitutionLoader(); 
		
		ListStore<BeanModel> institutionStore = new ListStore<BeanModel>(loader);
		institutionStore.setKeyProvider(new SimpleKeyProvider("ucn"));
		
//		this.setWidth(300);
		this.setValueField("ucn");
		this.setDisplayField("nameAndUcn");  
		this.setEmptyText("Enter institution search criteria here...");
		this.setStore(institutionStore);
		this.setMinChars(0);
		this.setHideTrigger(false);
		this.setTriggerAction(TriggerAction.ALL);
		this.setTriggerStyle("trigger-square"); 
		this.setPageSize(200);
		this.setAllowBlank(false);
		this.setEditable(true);
		this.setSimpleTemplate(getMultiLineAddressTemplate());
	}
	
	@Override
	public boolean isDirty() {
		if (disabled || !rendered) {
	    	return false;
	    }
	    return !equalWithNull();
	}

	/**
	 * Like Util.equalWithNull, but if the values are ModelData with a Store and a KeyProvider, use the key values provided.
	 * @param obj1
	 * @param obj2
	 * @return
	 */
	public boolean equalWithNull() {
		if (getSelectedValue() == originalValue) {
			return true;
		} else if (getSelectedValue() == null) {
			return false;
		} else if (getSelectedValue() instanceof ModelData && originalValue instanceof ModelData && this.getStore() != null && this.getStore().getKeyProvider() != null) {
	    	if (originalValue == null)
	    		return true;
	    	String key1 = this.getStore().getKeyProvider().getKey(getSelectedValue());
	    	String key2 = this.getStore().getKeyProvider().getKey(originalValue);
	    	return (key1.equals(key2));
	    } else {
	    	return getSelectedValue().equals(originalValue);
	    }
	}
	
	public BeanModel getSelectedValue() {
		return value;
	}
	
//	public void onBlur(ComponentEvent ce) {
//		super.onBlur(ce);
//		if (this.value == null) {
//			this.value = this.originalValue;
//			if (this.value == null)
//				setRawValue("");
//			else
//				setRawValue(this.value.get(this.getDisplayField()).toString());
//		}
//	}
	
	protected String getMultiLineAddressTemplate() {
		return "<b>{institutionName}</b>  <span style=\"color:gray; font-style: italic\">{ucn}</span><br/>{htmlAddress}"; // {address1}<br/>{city}, {state} &nbsp;&nbsp;&nbsp; {zip}";
	}
	
	/**
	 * Construct and return a loader to handle returning a list of institutions.
	 * @return
	 */
	protected PagingLoader<PagingLoadResult<InstitutionInstance>> getInstitutionLoader() {
		// proxy and reader  
		RpcProxy<PagingLoadResult<InstitutionInstance>> proxy = new RpcProxy<PagingLoadResult<InstitutionInstance>>() {  
			@Override  
			public void load(Object loadConfig, final AsyncCallback<PagingLoadResult<InstitutionInstance>> callback) {
		    	
				// This could be as simple as calling userListService.getUsers and passing the callback
				// Instead, here the callback is overridden so that it can catch errors and alert the users.  Then the original callback is told of the failure.
				// On success, the original callback is just passed the onSuccess message, and the response (the list).
				
				AsyncCallback<SynchronizedPagingLoadResult<InstitutionInstance>> myCallback = new AsyncCallback<SynchronizedPagingLoadResult<InstitutionInstance>>() {
					public void onFailure(Throwable caught) {
						// Show the RPC error message to the user
						if (caught instanceof IllegalArgumentException)
							MessageBox.alert("Alert", caught.getMessage(), null);
						else if (caught instanceof ServiceNotReadyException)
								MessageBox.alert("Alert", "The " + caught.getMessage() + " is not available at this time.  Please try again in a few minutes.", null);
						else {
							MessageBox.alert("Alert", "Institution load failed unexpectedly.", null);
							System.out.println(caught.getClass().getName());
							System.out.println(caught.getMessage());
						}
						callback.onFailure(caught);
					}

					public void onSuccess(SynchronizedPagingLoadResult<InstitutionInstance> syncResult) {
						if(syncResult.getSyncId() != searchSyncId)
							return;
						
						PagingLoadResult<InstitutionInstance> result = syncResult.getResult();
						
						//	This code was left, in case some way is determined to display this information when a search returns too many or no results
//						if ( result.getData() == null || result.getData().size() == 0 ) {
//							if (result.getTotalLength() > 0)
//								liveView.setEmptyText(result.getTotalLength() + " institutions qualify (too many to display).<br/>Please enter filter criteria to narrow your search.");
//							else if (filter.length() == 0)
//								liveView.setEmptyText("Enter filter criteria to search for institutions.");
//							else
//								liveView.setEmptyText("Please enter filter criteria to narrow your search.");
//						}
						callback.onSuccess(result);
					}
				};
				
				( (PagingLoadConfig) loadConfig).set("sortField",	sortField);
				( (PagingLoadConfig) loadConfig).set("sortDir",		sortDir);
				
				searchSyncId = System.currentTimeMillis();
				institutionSearchService.searchAgreementSiteInstitutions((PagingLoadConfig) loadConfig, agreementId, getQueryValue(loadConfig), AppConstants.STATUS_DELETED, searchSyncId, myCallback);
				
		    }  
		};
		BeanModelReader reader = new BeanModelReader();
		
		// loader and store  
		PagingLoader<PagingLoadResult<InstitutionInstance>> loader = new BasePagingLoader<PagingLoadResult<InstitutionInstance>>(proxy, reader);
		return loader;
	}
	
	public String getQueryValue(Object loadConfig) {
		String query = (loadConfig != null && loadConfig instanceof PagingLoadConfig) ? ((PagingLoadConfig) loadConfig).get("query").toString() : null;
		if (query == null)
			query = getRawValue();
		return query;
	}

	public String getSortField() {
		return sortField;
	}

	public void setSortField(String sortField) {
		this.sortField = sortField;
	}

	public SortDir getSortDir() {
		return sortDir;
	}

	public void setSortDir(SortDir sortDir) {
		this.sortDir = sortDir;
	}

	public int getAgreementId() {
		return agreementId;
	}

	public void setAgreementId(int agreementId) {
		this.agreementId = agreementId;
	}
	
}
