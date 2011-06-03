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
import com.scholastic.sbam.client.services.SiteLocationSearchService;
import com.scholastic.sbam.client.services.SiteLocationSearchServiceAsync;
import com.scholastic.sbam.client.uiobjects.uiapp.AppPortletIds;
import com.scholastic.sbam.client.uiobjects.uiapp.AppPortletProvider;
import com.scholastic.sbam.client.uiobjects.uiapp.CreateSiteDialog;
import com.scholastic.sbam.client.uiobjects.uiapp.CreateSiteDialog.CreateSiteDialogSaver;
import com.scholastic.sbam.client.uiobjects.uiapp.SiteLocationPortlet;
import com.scholastic.sbam.shared.exceptions.ServiceNotReadyException;
import com.scholastic.sbam.shared.objects.AgreementSiteInstance;
import com.scholastic.sbam.shared.objects.AuthMethodInstance;
import com.scholastic.sbam.shared.objects.InstitutionInstance;
import com.scholastic.sbam.shared.objects.RemoteSetupUrlInstance;
import com.scholastic.sbam.shared.objects.SiteInstance;
import com.scholastic.sbam.shared.objects.SimpleKeyProvider;
import com.scholastic.sbam.shared.objects.SynchronizedPagingLoadResult;
import com.scholastic.sbam.shared.util.AppConstants;

public class SiteLocationSearchField extends ComboBox<BeanModel> implements CreateSiteDialogSaver {
	
	protected final SiteLocationSearchServiceAsync siteLocationSearchService = GWT.create(SiteLocationSearchService.class);
	
	protected AppPortletProvider		appPortletProvider;
	
	protected long						searchSyncId		=	0;
	
	protected int						agreementId;	// 	This is optional, and will limit the sites found to those available on a particular agreement
	protected int						ucn;
	protected int						ucnSuffix;
	
	protected boolean					includeAddOption	=	true;
	protected boolean					includeAllOption	=	true;
	protected boolean					includeMainOption	=	false;
	protected SiteInstance				addInstance			=	null;
	protected SiteInstance				allInstance			=	null;
	protected SiteInstance				mainInstance		=	null;

	protected String					sortField			=	"descriptionAndCode";
	protected SortDir					sortDir				=	SortDir.ASC;
	
	protected LayoutContainer			createDialogContainer	= null;
	protected String					institutionName			= null;
	
	public SiteLocationSearchField() {
		super();
		
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
		this.setTriggerAction(TriggerAction.ALL);
		this.setPageSize(200);
		this.setAllowBlank(true);
		this.setEditable(true);
		this.setSimpleTemplate(getMultiLineAddressTemplate());
	}
	

	public SiteLocationSearchField(LayoutContainer createDialogContainer) {
		this();
		this.createDialogContainer = createDialogContainer;
	}
	

	public SiteLocationSearchField(LayoutContainer createDialogContainer, AppPortletProvider appPortletProvider) {
		this(createDialogContainer);
		this.appPortletProvider = appPortletProvider;
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
		if (ucn == 0) {
			MessageBox.alert("Programming Error", "No institution has been selected for which to create a site location.", null);
			return;
		}
		
		new CreateSiteDialog(createDialogContainer, this, ucn, ucnSuffix, institutionName).show();	
	}

	@Override
	public void onCreateSiteSave(SiteInstance instance, boolean openAfterSave) {
		//	Add the model to the field store and select it
		BeanModel model = SiteInstance.obtainModel(instance);
		this.getStore().add(model);
//		this.select(model);
		this.setValue(model);
//		this.setRawValue(instance.getDescriptionAndCode());
		lastQuery = null;		//	So next trigger click reloads/sorts from database
		
		if (openAfterSave && appPortletProvider != null) {
			SiteLocationPortlet portlet = (SiteLocationPortlet) appPortletProvider.getPortlet(AppPortletIds.SITE_LOCATION_DISPLAY);
			portlet.setSiteUcn(instance.getUcn());
			portlet.setSiteUcnSuffix(instance.getUcnSuffix());
			portlet.setSiteLocCode(instance.getSiteLocCode());
			if (agreementId > 0)
				portlet.setIdentificationTip("Created for agreement " + agreementId);
			appPortletProvider.addPortlet(portlet);
		}
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
							}
							result.getData().add(0, addInstance);
							result.setTotalLength(result.getTotalLength() + 1);
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
						setRawValue("");
					}
				};
				
				( (PagingLoadConfig) loadConfig).set("sortField",	sortField);
				( (PagingLoadConfig) loadConfig).set("sortDir",		sortDir);
				
				searchSyncId = System.currentTimeMillis();
				siteLocationSearchService.searchSiteLocations((PagingLoadConfig) loadConfig, agreementId, ucn, ucnSuffix, getQueryValue(loadConfig), searchSyncId, myCallback);
				
		    }  
		};
		BeanModelReader reader = new BeanModelReader();
		
		// loader and store  
		PagingLoader<PagingLoadResult<SiteInstance>> loader = new BasePagingLoader<PagingLoadResult<SiteInstance>>(proxy, reader);
		return loader;
	}
	
	public String getQueryValue(Object loadConfig) {
		String query = (loadConfig != null && loadConfig instanceof PagingLoadConfig) ? ((PagingLoadConfig) loadConfig).get("query").toString() : null;
		if (query == null)
			query = getRawValue();
		return query;
	}

	public AppPortletProvider getAppPortletProvider() {
		return appPortletProvider;
	}

	public void setAppPortletProvider(AppPortletProvider appPortletProvider) {
		this.appPortletProvider = appPortletProvider;
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


	public int getUcn() {
		return ucn;
	}

	public void setUcn(int ucn) {
		this.ucn = ucn;
		lastQuery = null;	// Necessary so that a trigger click for the new UCN reloads for that UCN
	}

	public int getUcnSuffix() {
		return ucnSuffix;
	}

	public void setUcnSuffix(int ucnSuffix) {
		this.ucnSuffix = ucnSuffix;
		lastQuery = null;	// Necessary so that a trigger click for the new UCN suffix reloads for that UCN
	}
	
	public void setFor(int ucn, int ucnSuffix, String institutionName) {
		this.ucn = ucn;
		this.ucnSuffix = ucnSuffix;
		if (institutionName != null && institutionName.length() > 0)
			this.institutionName = institutionName;
		else if (ucn > 0)
			institutionName = "UCN " + ucn;
		else
			institutionName = "";
		lastQuery = null;	// Necessary so that a trigger click for the new UCN reloads for that UCN
	}
	
	public void setFor(AuthMethodInstance method) {
		if (method.getSite() != null && method.getSite().getInstitution() != null)
			setFor(method, method.getSite().getInstitution().getInstitutionName());
		else
			setFor(method, null);
	}
	
	public void setFor(AuthMethodInstance method, String institutionName) {
		setFor(method.getForUcn(), method.getForUcnSuffix(), institutionName);
		agreementId = method.getAgreementId();
	}
	
	public void setFor(RemoteSetupUrlInstance remoteSetupUrl) {
		if (remoteSetupUrl.getSite() != null && remoteSetupUrl.getSite().getInstitution() != null)
			setFor(remoteSetupUrl, remoteSetupUrl.getSite().getInstitution().getInstitutionName());
		else
			setFor(remoteSetupUrl, null);
	}
	
	public void setFor(RemoteSetupUrlInstance remoteSetupUrl, String institutionName) {
		setFor(remoteSetupUrl.getForUcn(), remoteSetupUrl.getForUcnSuffix(), institutionName);
		agreementId = remoteSetupUrl.getAgreementId();
	}
	
	public void setFor(AgreementSiteInstance site) {
		if (site.getSite() != null && site.getSite().getInstitution() != null)
			setFor(site.getSiteUcn(), site.getSiteUcnSuffix(), site.getSite().getInstitution().getInstitutionName());
		else
			setFor(site.getSiteUcn(), site.getSiteUcnSuffix(), null);
		agreementId = site.getAgreementId();
	}
	
	public void setFor(SiteInstance site) {
		ucn = site.getUcn();
		ucnSuffix = site.getUcnSuffix();
		if (site.getInstitution() != null)
			setFor(site.getUcn(), site.getUcnSuffix(), site.getInstitution().getInstitutionName());
		else
			setFor(site.getUcn(), site.getUcnSuffix(), null);
	}
	
	public void setFor(InstitutionInstance institution) {
		setFor(institution.getUcn(), 1, institution.getInstitutionName());
	}
}
