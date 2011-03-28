package com.scholastic.sbam.client.uiobjects;

import com.extjs.gxt.ui.client.Style.SortDir;
import com.extjs.gxt.ui.client.data.BasePagingLoader;
import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.BeanModelReader;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoader;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.scholastic.sbam.client.services.AgreementLinkSearchService;
import com.scholastic.sbam.client.services.AgreementLinkSearchServiceAsync;
import com.scholastic.sbam.shared.exceptions.ServiceNotReadyException;
import com.scholastic.sbam.shared.objects.AgreementLinkInstance;
import com.scholastic.sbam.shared.objects.SimpleKeyProvider;
import com.scholastic.sbam.shared.objects.SynchronizedPagingLoadResult;
import com.scholastic.sbam.shared.util.AppConstants;

public class AgreementLinkSearchField extends ComboBox<BeanModel> {
	
	protected final AgreementLinkSearchServiceAsync agreementLinkSearchService = GWT.create(AgreementLinkSearchService.class);
	
	private long					searchSyncId		=	0;
	
	private boolean					includeAddOption	=	true;
	private boolean					includeNoneOption	=	true;
	private AgreementLinkInstance	addInstance			=	null;
	private AgreementLinkInstance	noneInstance		=	null;

	private String					sortField			=	"institution.institutionName";
	private SortDir					sortDir				=	SortDir.ASC;
	
	public AgreementLinkSearchField() {
		
		PagingLoader<PagingLoadResult<AgreementLinkInstance>> loader = getAgreementLinkLoader(); 
		
		ListStore<BeanModel> agreementLinkStore = new ListStore<BeanModel>(loader);
		agreementLinkStore.setKeyProvider(new SimpleKeyProvider("linkId"));
		
		this.setWidth(300);
		this.setValueField("linkId");
		this.setDisplayField("descriptionAndCode");
		this.setEmptyText("Enter agreement link search criteria here...");
		this.setStore(agreementLinkStore);
		this.setMinChars(2);
		this.setHideTrigger(true);  
		this.setPageSize(200);
		this.setAllowBlank(true);
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
	
//	public void dumpValues(String tag) {
//		if (originalValue == null) System.out.println(tag + ": Original null"); else System.out.println(tag + ": Original " + originalValue.getProperties());
//		if (value == null) System.out.println(tag + ": Value null"); else System.out.println(tag + ": Value " + value.getProperties());
//	}
	
	public BeanModel getSelectedValue() {
		return value;
	}
	
	public void onBlur(ComponentEvent ce) {
		super.onBlur(ce);
		if (this.value == null) {
			this.value = this.originalValue;
			if (this.value == null)
				setRawValue("");
			else
				setRawValue(this.value.get(this.getDisplayField()).toString());
		}
	}
	
	protected String getMultiLineAddressTemplate() {
		return "<div class=\"{listStyle}\">{descriptionAndCode}</div>";
		//	This isn't working... it's dying on institution.institutionName and institution.htmlAddress (for today, it is okay to say GXT sucks).
		//	return "<b>{linkIdCheckDigit} {institution.institutionName}</b><br/>  <span style=\"color:gray\">{institution.htmlAddress}</span>"; // {address1}<br/>{city}, {state} &nbsp;&nbsp;&nbsp; {zip}";
	}
	
	/**
	 * Construct and return a loader to handle returning a list of agreementLinks.
	 * @return
	 */
	protected PagingLoader<PagingLoadResult<AgreementLinkInstance>> getAgreementLinkLoader() {
		// proxy and reader  
		RpcProxy<PagingLoadResult<AgreementLinkInstance>> proxy = new RpcProxy<PagingLoadResult<AgreementLinkInstance>>() {  
			@Override  
			public void load(Object loadConfig, final AsyncCallback<PagingLoadResult<AgreementLinkInstance>> callback) {
		    	
				// This could be as simple as calling userListService.getUsers and passing the callback
				// Instead, here the callback is overridden so that it can catch errors and alert the users.  Then the original callback is told of the failure.
				// On success, the original callback is just passed the onSuccess message, and the response (the list).
				
				AsyncCallback<SynchronizedPagingLoadResult<AgreementLinkInstance>> myCallback = new AsyncCallback<SynchronizedPagingLoadResult<AgreementLinkInstance>>() {
					public void onFailure(Throwable caught) {
						// Show the RPC error message to the user
						if (caught instanceof IllegalArgumentException)
							MessageBox.alert("Alert", caught.getMessage(), null);
						else if (caught instanceof ServiceNotReadyException)
								MessageBox.alert("Alert", "The " + caught.getMessage() + " is not available at this time.  Please try again in a few minutes.", null);
						else {
							MessageBox.alert("Alert", "Agreement Link load failed unexpectedly.", null);
							System.out.println(caught.getClass().getName());
							System.out.println(caught.getMessage());
						}
						callback.onFailure(caught);
					}

					public void onSuccess(SynchronizedPagingLoadResult<AgreementLinkInstance> syncResult) {
						if(syncResult.getSyncId() != searchSyncId)
							return;
						
						PagingLoadResult<AgreementLinkInstance> result = syncResult.getResult();
						if (includeAddOption) {
							if (addInstance == null) {
								addInstance= new AgreementLinkInstance();
								addInstance.setStatus(AppConstants.STATUS_NEW);
							}
							result.getData().add(0, addInstance);
						}
						if (includeNoneOption) {
							if (noneInstance == null) {
								noneInstance= new AgreementLinkInstance();
								noneInstance.setStatus(AppConstants.STATUS_NULL);
							}
							result.getData().add(0, noneInstance);
						}

						callback.onSuccess(result);
					}
				};
				
				( (PagingLoadConfig) loadConfig).set("sortField",	sortField);
				( (PagingLoadConfig) loadConfig).set("sortDir",		sortDir);
				
				searchSyncId = System.currentTimeMillis();
				agreementLinkSearchService.searchAgreementLinks((PagingLoadConfig) loadConfig, getRawValue(), searchSyncId, myCallback);
				
		    }  
		};
		BeanModelReader reader = new BeanModelReader();
		
		// loader and store  
		PagingLoader<PagingLoadResult<AgreementLinkInstance>> loader = new BasePagingLoader<PagingLoadResult<AgreementLinkInstance>>(proxy, reader);
		return loader;
	}

	public boolean isIncludeAddOption() {
		return includeAddOption;
	}

	public void setIncludeAddOption(boolean includeAddOption) {
		this.includeAddOption = includeAddOption;
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
	
}
