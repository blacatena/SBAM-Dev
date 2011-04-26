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
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.scholastic.sbam.client.services.AgreementSiteSearchService;
import com.scholastic.sbam.client.services.AgreementSiteSearchServiceAsync;
import com.scholastic.sbam.client.uiobjects.uiapp.CreateAgreementSiteDialog;
import com.scholastic.sbam.client.uiobjects.uiapp.CreateAgreementSiteDialog.CreateAgreementSiteDialogSaver;
import com.scholastic.sbam.shared.exceptions.ServiceNotReadyException;
import com.scholastic.sbam.shared.objects.AgreementSiteInstance;
import com.scholastic.sbam.shared.objects.SiteInstance;
import com.scholastic.sbam.shared.objects.SimpleKeyProvider;
import com.scholastic.sbam.shared.objects.SynchronizedPagingLoadResult;
import com.scholastic.sbam.shared.util.AppConstants;

/**
 * INCOMPLETE -- TEST AND FINISH BEFORE USING
 * 
 * This class presents a field for finding the sites (ucn+suffix+location) for an agreement.
 * 
 * It was abandoned in favor of a two field approach (institution, location).
 * 
 * To be used this must be expanded to show the institution and location name and codes in the results, and to select
 * and provide all three values through a SiteInstance.
 * 
 * @author Bob Lacatena
 *
 */
public class AgreementSiteSearchField extends ComboBox<BeanModel> implements CreateAgreementSiteDialogSaver {
	
	protected final AgreementSiteSearchServiceAsync agreementSiteSearchService = GWT.create(AgreementSiteSearchService.class);
	
	protected long						searchSyncId		=	0;
	
	protected int						agreementId;
	
	protected boolean					includeAddOption	=	true;
	protected AgreementSiteInstance		addInstance			=	null;

	protected String					sortField			=	"descriptionAndCode";
	protected SortDir					sortDir				=	SortDir.ASC;
	
	protected LayoutContainer			createDialogContainer	= null;
	protected String					institutionName			= null;
	
	public AgreementSiteSearchField() {
		super();
		
		PagingLoader<PagingLoadResult<AgreementSiteInstance>> loader = getSiteLoader(); 
		
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
		this.setTriggerAction(TriggerAction.ALL);
		this.setPageSize(200);
		this.setAllowBlank(true);
		this.setEditable(true);
		this.setSimpleTemplate(getMultiLineAddressTemplate());
	}
	

	public AgreementSiteSearchField(LayoutContainer createDialogContainer) {
		this();
		this.createDialogContainer = createDialogContainer;
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
	
	@Override
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
	
	@Override
	public void onSelect(BeanModel model, int index) {
		if (model.getBean() != null) {
			SiteInstance site = (SiteInstance) model.getBean();
			if (site.isAddNew()) {
				openCreateSiteDialog();
				lastQuery = null;
			}
		}
		
		super.onSelect(model, index);
	}
	
	protected void openCreateSiteDialog() {
		if (agreementId == 0) {
			MessageBox.alert("Programming Error", "No institution has been selected for which to create a site location.", null);
			return;
		}
		
		new CreateAgreementSiteDialog(createDialogContainer, this, agreementId).show();	
	}

	@Override
	public void onCreateAgreementSiteSave(AgreementSiteInstance instance) {
		//	Add the model to the field store and select it
		BeanModel model = AgreementSiteInstance.obtainModel(instance);
		this.getStore().add(model);
//		this.select(model);
		this.setValue(model);
//		this.setRawValue(instance.getDescriptionAndCode());
		lastQuery = null;		//	So next trigger click reloads/sorts from database
	}
	
	@Override
	public void lockTrigger() {
		this.disable();
	}
	
	public void unlockTrigger() {
		this.enable();
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
	protected PagingLoader<PagingLoadResult<AgreementSiteInstance>> getSiteLoader() {
		// proxy and reader  
		RpcProxy<PagingLoadResult<AgreementSiteInstance>> proxy = new RpcProxy<PagingLoadResult<AgreementSiteInstance>>() {  
			@Override  
			public void load(Object loadConfig, final AsyncCallback<PagingLoadResult<AgreementSiteInstance>> callback) {
		    	
				// This could be as simple as calling userListService.getUsers and passing the callback
				// Instead, here the callback is overridden so that it can catch errors and alert the users.  Then the original callback is told of the failure.
				// On success, the original callback is just passed the onSuccess message, and the response (the list).
				
				AsyncCallback<SynchronizedPagingLoadResult<AgreementSiteInstance>> myCallback = new AsyncCallback<SynchronizedPagingLoadResult<AgreementSiteInstance>>() {
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

					public void onSuccess(SynchronizedPagingLoadResult<AgreementSiteInstance> syncResult) {
						if(syncResult.getSyncId() != searchSyncId)
							return;
						
						PagingLoadResult<AgreementSiteInstance> result = syncResult.getResult();
						if (includeAddOption) {
							if (addInstance == null) {
								addInstance= new AgreementSiteInstance();
								addInstance.setStatus(AppConstants.STATUS_NEW);
							}
							result.getData().add(0, addInstance);
							result.setTotalLength(result.getTotalLength() + 1);
						}

						callback.onSuccess(result);
						setRawValue("");
					}
				};
				
				( (PagingLoadConfig) loadConfig).set("sortField",	sortField);
				( (PagingLoadConfig) loadConfig).set("sortDir",		sortDir);
				
				searchSyncId = System.currentTimeMillis();
				agreementSiteSearchService.searchAgreementSites((PagingLoadConfig) loadConfig, agreementId, getQueryValue(loadConfig), AppConstants.STATUS_DELETED, searchSyncId, myCallback);
				
		    }  
		};
		BeanModelReader reader = new BeanModelReader();
		
		// loader and store  
		PagingLoader<PagingLoadResult<AgreementSiteInstance>> loader = new BasePagingLoader<PagingLoadResult<AgreementSiteInstance>>(proxy, reader);
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

	public LayoutContainer getCreateDialogContainer() {
		return createDialogContainer;
	}

	public void setCreateDialogContainer(LayoutContainer createDialogContainer) {
		this.createDialogContainer = createDialogContainer;
	}

	public String getInstitutionName() {
		return institutionName;
	}

	public void setInstitutionName(String institutionName) {
		this.institutionName = institutionName;
	}

	public int getAgreementId() {
		return agreementId;
	}

	public void setAgreementId(int agreementId) {
		this.agreementId = agreementId;
		lastQuery = null;	// Necessary so that a trigger click for the new UCN reloads for that UCN
	}
	
	public void setFor(int agreementId, String institutionName) {
		this.agreementId = agreementId;
		if (institutionName != null && institutionName.length() > 0)
			this.institutionName = institutionName;
		else if (agreementId > 0)
			institutionName = "Agreement Id " + agreementId;
		else
			institutionName = "";
		lastQuery = null;	// Necessary so that a trigger click for the new UCN reloads for that UCN
	}
	
	public void setFor(AgreementSiteInstance site) {
		if (site.getSite() != null && site.getSite().getInstitution() != null)
			setFor(site.getAgreementId(), site.getSite().getInstitution().getInstitutionName());
		else
			setFor(site.getAgreementId(), null);
	}
}
