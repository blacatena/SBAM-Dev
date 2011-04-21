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
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.scholastic.sbam.client.services.SiteLocationSearchService;
import com.scholastic.sbam.client.services.SiteLocationSearchServiceAsync;
import com.scholastic.sbam.shared.exceptions.ServiceNotReadyException;
import com.scholastic.sbam.shared.objects.AgreementSiteInstance;
import com.scholastic.sbam.shared.objects.AuthMethodInstance;
import com.scholastic.sbam.shared.objects.SiteInstance;
import com.scholastic.sbam.shared.objects.SimpleKeyProvider;
import com.scholastic.sbam.shared.objects.SynchronizedPagingLoadResult;
import com.scholastic.sbam.shared.util.AppConstants;

public class SiteLocationSearchField extends ComboBox<BeanModel> {
	
	protected final SiteLocationSearchServiceAsync siteLocationSearchService = GWT.create(SiteLocationSearchService.class);
	
	private long					searchSyncId		=	0;
	
	private	int						ucn;
	private	int						ucnSuffix;
	
	private boolean					includeAddOption	=	true;
	private boolean					includeAllOption	=	true;
	private boolean					includeMainOption	=	false;
	private SiteInstance			addInstance			=	null;
	private SiteInstance			allInstance			=	null;
	private SiteInstance			mainInstance		=	null;

	private String					sortField			=	"descriptionAndCode";
	private SortDir					sortDir				=	SortDir.ASC;
	
	public SiteLocationSearchField() {
		
		PagingLoader<PagingLoadResult<SiteInstance>> loader = getSiteLoader(); 
		
		ListStore<BeanModel> siteLocationStore = new ListStore<BeanModel>(loader);
		siteLocationStore.setKeyProvider(new SimpleKeyProvider("uniqueKey"));
		
		this.setWidth(300);
		this.setValueField("uniqueKey");
		this.setDisplayField("descriptionAndCode");
		this.setEmptyText("Enter site location search criteria here...");
		this.setStore(siteLocationStore);
		this.setMinChars(0);
		this.setHideTrigger(false); 
		this.setTriggerStyle("trigger-square");
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
	 * Construct and return a loader to handle returning a list of siteLocations.
	 * @return
	 */
	protected PagingLoader<PagingLoadResult<SiteInstance>> getSiteLoader() {
		// proxy and reader  
		RpcProxy<PagingLoadResult<SiteInstance>> proxy = new RpcProxy<PagingLoadResult<SiteInstance>>() {  
			@Override  
			public void load(Object loadConfig, final AsyncCallback<PagingLoadResult<SiteInstance>> callback) {
		    	
				// This could be as simple as calling userListService.getUsers and passing the callback
				// Instead, here the callback is overridden so that it can catch errors and alert the users.  Then the original callback is told of the failure.
				// On success, the original callback is just passed the onSuccess message, and the response (the list).
				
				AsyncCallback<SynchronizedPagingLoadResult<SiteInstance>> myCallback = new AsyncCallback<SynchronizedPagingLoadResult<SiteInstance>>() {
					public void onFailure(Throwable caught) {
						// Show the RPC error message to the user
						if (caught instanceof IllegalArgumentException)
							MessageBox.alert("Alert", caught.getMessage(), null);
						else if (caught instanceof ServiceNotReadyException)
								MessageBox.alert("Alert", "The " + caught.getMessage() + " is not available at this time.  Please try again in a few minutes.", null);
						else {
							MessageBox.alert("Alert", "Site Location load failed unexpectedly.", null);
							System.out.println(caught.getClass().getName());
							System.out.println(caught.getMessage());
						}
						callback.onFailure(caught);
					}

					public void onSuccess(SynchronizedPagingLoadResult<SiteInstance> syncResult) {
						if(syncResult.getSyncId() != searchSyncId)
							return;
						
						PagingLoadResult<SiteInstance> result = syncResult.getResult();
						int resultCount = result != null && result.getData() != null ? result.getData().size() : 0;
						if (includeAddOption) {
							if (addInstance == null) {
								addInstance= new SiteInstance();
								addInstance.setStatus(AppConstants.STATUS_NEW);
								result.setTotalLength(result.getTotalLength() + 1);
							}
							result.getData().add(0, addInstance);
						}
						if (includeAllOption) {
							if (allInstance == null) {
								allInstance= new SiteInstance();
								allInstance.setStatus(AppConstants.STATUS_ALL);
							}
							result.getData().add(0, allInstance);
							result.setTotalLength(result.getTotalLength() + 1);
						}
						if (includeMainOption && resultCount == 0) {
							mainInstance= SiteInstance.getMainInstance(ucn, ucnSuffix);
							result.getData().add(0, mainInstance);
							result.setTotalLength(result.getTotalLength() + 1);
						}

						callback.onSuccess(result);
					}
				};
				
				( (PagingLoadConfig) loadConfig).set("sortField",	sortField);
				( (PagingLoadConfig) loadConfig).set("sortDir",		sortDir);
				
				searchSyncId = System.currentTimeMillis();
				siteLocationSearchService.searchSiteLocations((PagingLoadConfig) loadConfig, ucn, ucnSuffix, getRawValue(), searchSyncId, myCallback);
				
		    }  
		};
		BeanModelReader reader = new BeanModelReader();
		
		// loader and store  
		PagingLoader<PagingLoadResult<SiteInstance>> loader = new BasePagingLoader<PagingLoadResult<SiteInstance>>(proxy, reader);
		return loader;
	}

	public boolean isIncludeAddOption() {
		return includeAddOption;
	}

	public void setIncludeAddOption(boolean includeAddOption) {
		this.includeAddOption = includeAddOption;
	}

	public boolean isIncludeAllOption() {
		return includeAllOption;
	}

	public void setIncludeAllOption(boolean includeAllOption) {
		this.includeAllOption = includeAllOption;
	}

	public boolean isIncludeMainOption() {
		return includeMainOption;
	}

	public void setIncludeMainOption(boolean includeMainOption) {
		this.includeMainOption = includeMainOption;
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

	public int getUcn() {
		return ucn;
	}

	public void setUcn(int ucn) {
		this.ucn = ucn;
	}

	public int getUcnSuffix() {
		return ucnSuffix;
	}

	public void setUcnSuffix(int ucnSuffix) {
		this.ucnSuffix = ucnSuffix;
	}
	
	public void setFor(int ucn, int ucnSuffix) {
		this.ucn = ucn;
		this.ucnSuffix = ucnSuffix;
	}
	
	public void setFor(AuthMethodInstance method) {
		ucn = method.getUcn();
		ucnSuffix = method.getUcnSuffix();
	}
	
	public void setFor(AgreementSiteInstance site) {
		ucn = site.getSiteUcn();
		ucnSuffix = site.getSiteUcnSuffix();
	}
	
	public void setFor(SiteInstance site) {
		ucn = site.getUcn();
		ucnSuffix = site.getUcnSuffix();
	}
}
