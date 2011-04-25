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
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.scholastic.sbam.client.services.ContactSearchService;
import com.scholastic.sbam.client.services.ContactSearchServiceAsync;
import com.scholastic.sbam.shared.exceptions.ServiceNotReadyException;
import com.scholastic.sbam.shared.objects.ContactInstance;
import com.scholastic.sbam.shared.objects.ContactSearchResultInstance;
import com.scholastic.sbam.shared.objects.InstitutionInstance;
import com.scholastic.sbam.shared.objects.SimpleKeyProvider;
import com.scholastic.sbam.shared.objects.SynchronizedPagingLoadResult;

public class ContactSearchField extends ComboBox<BeanModel> {
	
	protected final ContactSearchServiceAsync contactSearchService = GWT.create(ContactSearchService.class);
	
	private long						searchSyncId		=	0;
	
	private	int							ucn;
	/**
	 * Whether or not this contact search should search both institutions and contacts, or contacts only
	 */
	private boolean						searchInstitutions	=	false;
	
	private boolean						includeAddOption	=	true;
	private ContactSearchResultInstance	addInstance			=	null;
	private ContactSearchResultInstance	newSearchInstance	=	null;

	private String						sortField			=	"fullName";
	private SortDir						sortDir				=	SortDir.ASC;
	
	public ContactSearchField() {
		
		PagingLoader<PagingLoadResult<ContactSearchResultInstance>> loader = getContactLoader(); 
		
		ListStore<BeanModel> contactStore = new ListStore<BeanModel>(loader);
		contactStore.setKeyProvider(new SimpleKeyProvider("uniqueKey"));
		
		this.setWidth(300);
		this.setValueField("uniqueKey");
		this.setDisplayField("fullName");
		this.setEmptyText("Enter contact search criteria here...");
		this.setStore(contactStore);
		this.setMinChars(0);
		this.setHideTrigger(false); 
		this.setTriggerAction(TriggerAction.ALL);
		this.setTriggerStyle("trigger-square");
		this.setPageSize(200);
		this.setAllowBlank(true);
		this.setEditable(true);
		this.setSimpleTemplate(getMultiLineAddressTemplate());
		this.setSelectOnFocus(true);
		
		this.addSelectionChangedListener(new SelectionChangedListener<BeanModel>() {

			@Override
			public void selectionChanged(SelectionChangedEvent<BeanModel> se) {
				if (se.getSelectedItem() == null) {
					onSelectionChange(null);
				} else
					onSelectionChange((ContactSearchResultInstance) se.getSelectedItem().getBean());
			}
			
		});
	}
	
	public void onSelectionChange(ContactSearchResultInstance selected) {
		if (selected == null) {
			if (searchInstitutions)
				ucn = 0;
			lastQuery = null;
			this.setRawValue("")	;	//	this.setValue(ContactSearchResultInstance.obtainModel(ContactSearchResultInstance.getEmptyInstance()));
			return;
		}
		if (selected.getType() == ContactSearchResultInstance.INSTITUTION) {
			ucn = selected.getId();
			this.setRawValue("");
			lastQuery = null;
			this.getStore().getLoader().load();
			return;
		}
		if (selected.getType() == ContactSearchResultInstance.NEW_SEARCH) {
			if (searchInstitutions)
				ucn = 0;
			lastQuery = null;
			this.setRawValue("")	;	//	this.setValue(ContactSearchResultInstance.obtainModel(ContactSearchResultInstance.getEmptyInstance()));
			return;
		}
		//	For add let the object using the field handle it
		//	For an actual contact, let the object using the field handle it
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
	
	public ContactInstance getSelectedContact() {
		if (value != null)
			return ((ContactSearchResultInstance) value.getBean()).getContact();
		return null;
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
		return "<div class=\"{listStyle}\"><b>{nameInfoHtml}</b><br/>{multiLineAddress}</div>";
		//	This isn't working... it's dying on institution.institutionName and institution.htmlAddress (for today, it is okay to say GXT sucks).
		//	return "<b>{linkIdCheckDigit} {institution.institutionName}</b><br/>  <span style=\"color:gray\">{institution.htmlAddress}</span>"; // {address1}<br/>{city}, {state} &nbsp;&nbsp;&nbsp; {zip}";
	}
	
	/**
	 * Construct and return a loader to handle returning a list of contacts.
	 * @return
	 */
	protected PagingLoader<PagingLoadResult<ContactSearchResultInstance>> getContactLoader() {
		// proxy and reader  
		RpcProxy<PagingLoadResult<ContactSearchResultInstance>> proxy = new RpcProxy<PagingLoadResult<ContactSearchResultInstance>>() {  
			@Override  
			public void load(Object loadConfig, final AsyncCallback<PagingLoadResult<ContactSearchResultInstance>> callback) {
		    	
				// This could be as simple as calling userListService.getUsers and passing the callback
				// Instead, here the callback is overridden so that it can catch errors and alert the users.  Then the original callback is told of the failure.
				// On success, the original callback is just passed the onSuccess message, and the response (the list).
				
				AsyncCallback<SynchronizedPagingLoadResult<ContactSearchResultInstance>> myCallback = new AsyncCallback<SynchronizedPagingLoadResult<ContactSearchResultInstance>>() {
					public void onFailure(Throwable caught) {
						// Show the RPC error message to the user
						if (caught instanceof IllegalArgumentException)
							MessageBox.alert("Alert", caught.getMessage(), null);
						else if (caught instanceof ServiceNotReadyException)
								MessageBox.alert("Alert", "The " + caught.getMessage() + " is not available at this time.  Please try again in a few minutes.", null);
						else {
							MessageBox.alert("Alert", "Contact search failed unexpectedly.", null);
							System.out.println(caught.getClass().getName());
							System.out.println(caught.getMessage());
						}
						callback.onFailure(caught);
					}

					public void onSuccess(SynchronizedPagingLoadResult<ContactSearchResultInstance> syncResult) {
						if(syncResult.getSyncId() != searchSyncId)
							return;
						
						PagingLoadResult<ContactSearchResultInstance> result = syncResult.getResult();
						if (includeAddOption) {
							if (addInstance == null) {
								addInstance= ContactSearchResultInstance.getAddNewInstance();
							}
							result.getData().add(0, addInstance);
							result.setTotalLength(result.getTotalLength() + 1);
						}
						
						if (searchInstitutions) {
							if (newSearchInstance == null) {
								newSearchInstance= ContactSearchResultInstance.getNewSearchInstance();
							}
							result.getData().add(0, newSearchInstance);
							result.setTotalLength(result.getTotalLength() + 1);
						}

						callback.onSuccess(result);
					}
				};
				
				( (PagingLoadConfig) loadConfig).set("sortField",	sortField);
				( (PagingLoadConfig) loadConfig).set("sortDir",		sortDir);
				
				searchSyncId = System.currentTimeMillis();
				contactSearchService.searchContacts((PagingLoadConfig) loadConfig, ucn, searchInstitutions, getQueryValue(loadConfig), searchSyncId, myCallback);
				
		    }  
		};
		BeanModelReader reader = new BeanModelReader();
		
		// loader and store  
		PagingLoader<PagingLoadResult<ContactSearchResultInstance>> loader = new BasePagingLoader<PagingLoadResult<ContactSearchResultInstance>>(proxy, reader);
		return loader;
	}
	
	public String getQueryValue(Object loadConfig) {
		String query = (loadConfig != null && loadConfig instanceof PagingLoadConfig) ? ((PagingLoadConfig) loadConfig).get("query").toString() : null;
		if (query == null)
			query = getRawValue();
		return query;
	}

	public boolean isIncludeAddOption() {
		return includeAddOption;
	}

	public void setIncludeAddOption(boolean includeAddOption) {
		this.includeAddOption = includeAddOption;
	}

	public boolean isSearchInsitutions() {
		return searchInstitutions;
	}

	public void setSearchInsitutions(boolean searchInsitutions) {
		this.searchInstitutions = searchInsitutions;
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
		lastQuery = null;	// Necessary so that a trigger click for the new UCN reloads for that UCN
	}

	public void setFor(int ucn) {
		this.ucn = ucn;
		lastQuery = null;	// Necessary so that a trigger click for the new UCN reloads for that UCN
	}
	
	public void setFor(InstitutionInstance institution) {
		ucn = institution.getUcn();
		lastQuery = null;	// Necessary so that a trigger click for the new UCN reloads for that UCN
	}
}
