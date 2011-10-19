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
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.scholastic.sbam.client.services.AgreementLinkSearchService;
import com.scholastic.sbam.client.services.AgreementLinkSearchServiceAsync;
import com.scholastic.sbam.client.uiobjects.uiapp.AgreementLinkPortlet;
import com.scholastic.sbam.client.uiobjects.uiapp.AppPortletIds;
import com.scholastic.sbam.client.uiobjects.uiapp.AppPortletProvider;
import com.scholastic.sbam.client.uiobjects.uiapp.CreateAgreementLinkDialog;
import com.scholastic.sbam.client.uiobjects.uiapp.CreateAgreementLinkDialog.CreateAgreementLinkDialogSaver;
import com.scholastic.sbam.client.util.IconSupplier;
import com.scholastic.sbam.shared.exceptions.ServiceNotReadyException;
import com.scholastic.sbam.shared.objects.AgreementLinkInstance;
import com.scholastic.sbam.shared.objects.SimpleKeyProvider;
import com.scholastic.sbam.shared.objects.SynchronizedPagingLoadResult;
import com.scholastic.sbam.shared.util.AppConstants;

public class AgreementLinkSearchField extends ComboBox<BeanModel> implements CreateAgreementLinkDialogSaver {
	
	protected final AgreementLinkSearchServiceAsync agreementLinkSearchService = GWT.create(AgreementLinkSearchService.class);
	
	protected AppPortletProvider	appPortletProvider	=	null;
	
	private long					searchSyncId		=	0;
	
	private boolean					includeAddOption	=	true;
	private boolean					includeNoneOption	=	true;
	private AgreementLinkInstance	addInstance			=	null;
	private AgreementLinkInstance	noneInstance		=	null;

	private String					sortField			=	"institution.institutionName";
	private SortDir					sortDir				=	SortDir.ASC;
	
	protected LayoutContainer		createDialogContainer	= null;
	
	protected int					agreementId			=	0;
	
	protected Button				openButton			=	null;
	
	public AgreementLinkSearchField() {
		super();
		
		PagingLoader<PagingLoadResult<AgreementLinkInstance>> loader = getAgreementLinkLoader(); 
		
		ListStore<BeanModel> agreementLinkStore = new ListStore<BeanModel>(loader);
		agreementLinkStore.setKeyProvider(new SimpleKeyProvider("linkId"));
		
		this.setWidth(300);
		this.setValueField("linkId");
		this.setDisplayField("descriptionAndCode");
		this.setEmptyText("Enter agreement link search criteria here...");
		this.setStore(agreementLinkStore);
		this.setMinChars(2);
		this.setHideTrigger(false); 
		this.setTriggerAction(TriggerAction.QUERY);
		this.setTriggerStyle("trigger-square");
		this.setPageSize(200);
		this.setAllowBlank(true);
		this.setEditable(true);
		this.setSimpleTemplate(getMultiLineAddressTemplate());
		
		this.addSelectionChangedListener(new SelectionChangedListener<BeanModel>() {

			@Override
			public void selectionChanged(SelectionChangedEvent<BeanModel> se) {
				if (se.getSelectedItem() == null) {
					if (openButton != null)
						openButton.disable();
				} else {
					AgreementLinkInstance selected = (AgreementLinkInstance) se.getSelectedItem().getBean();
					if (openButton != null)
						openButton.setEnabled(selected.getLinkId() > 0);
				}
			}
			
		});
	}
	
	public AgreementLinkSearchField(LayoutContainer createDialogContainer) {
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
	
	public AgreementLinkInstance getSelectedAgreementLink() {
		if (value != null)
			return (AgreementLinkInstance) value.getBean();
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
		return "<div class=\"{listStyle}\">{descriptionAndCode}</div>";
		//	This isn't working... it's dying on institution.institutionName and institution.htmlAddress (for today, it is okay to say GXT sucks).
		//	return "<b>{linkIdCheckDigit} {institution.institutionName}</b><br/>  <span style=\"color:gray\">{institution.htmlAddress}</span>"; // {address1}<br/>{city}, {state} &nbsp;&nbsp;&nbsp; {zip}";
	}
	
	@Override
	public void onSelect(BeanModel model, int index) {
		if (model.getBean() != null) {
			AgreementLinkInstance link = (AgreementLinkInstance) model.getBean();
			if (link.isAddNew()) {
				openCreateAgreementLinkDialog();
				lastQuery = null;
			}
		}
		
		super.onSelect(model, index);
	}
	
	protected void openCreateAgreementLinkDialog() {
		new CreateAgreementLinkDialog(createDialogContainer, this).show();	
	}

	@Override
	public void onCreateAgreementLinkSave(AgreementLinkInstance instance) {
		//	Add the model to the field store and select it
		BeanModel model = AgreementLinkInstance.obtainModel(instance);
		this.getStore().add(model);
//		this.select(model);
		this.setValue(model);
//		this.setRawValue(instance.getDescriptionAndCode());
		lastQuery = null;		//	So next trigger click reloads/sorts from database
	}
	
	public void openAgreementLinkPortlet(AgreementLinkInstance instance) {
		if (instance == null)
			return;
		openAgreementLinkPortlet(instance.getLinkId());
	}
	
	public void openAgreementLinkPortlet(int linkId) {
		if (appPortletProvider == null)
			return;
		AgreementLinkPortlet portlet = (AgreementLinkPortlet) appPortletProvider.getPortlet(AppPortletIds.AGREEMENT_LINK_DISPLAY);
		portlet.setAgreementLinkId(linkId);
		appPortletProvider.addPortlet(portlet);
	}

	
	public Button createOpenButton() {
		if (openButton != null)
			return openButton;
		
		final AgreementLinkSearchField agrementLinkField = this;
		openButton = new Button();
		IconSupplier.forceIcon(openButton, IconSupplier.getGoOpenIconName());
		openButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				if (agrementLinkField.getSelectedAgreementLink() != null && agrementLinkField.getSelectedAgreementLink().getLinkId() > 0) {
					openAgreementLinkPortlet(agrementLinkField.getSelectedAgreementLink());
				}
			}
			
		});
		
		final int WIDTH  = 24;
		final int HEIGHT = 22;
		openButton.setWidth(WIDTH);
		openButton.setHeight(HEIGHT);
		openButton.setPixelSize(WIDTH, HEIGHT);
		
		return openButton;
	}
	
	@Override
	public void lockTrigger() {
		this.disable();
	}
	
	public void unlockTrigger() {
		this.enable();
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
							result.setTotalLength(result.getTotalLength() + 1);
						}
						if (includeNoneOption) {
							if (noneInstance == null) {
								noneInstance= new AgreementLinkInstance();
								noneInstance.setStatus(AppConstants.STATUS_NULL);
							}
							result.getData().add(0, noneInstance);
							result.setTotalLength(result.getTotalLength() + 1);
						}

						callback.onSuccess(result);
					}
				};
				
				( (PagingLoadConfig) loadConfig).set("sortField",	sortField);
				( (PagingLoadConfig) loadConfig).set("sortDir",		sortDir);
				
				searchSyncId = System.currentTimeMillis();
				agreementLinkSearchService.searchAgreementLinks((PagingLoadConfig) loadConfig, getQueryValue(loadConfig), searchSyncId, myCallback);
				
		    }  
		};
		BeanModelReader reader = new BeanModelReader();
		
		// loader and store  
		PagingLoader<PagingLoadResult<AgreementLinkInstance>> loader = new BasePagingLoader<PagingLoadResult<AgreementLinkInstance>>(proxy, reader);
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

	public Button getOpenButton() {
		return createOpenButton();
	}

	public void setOpenButton(Button openButton) {
		this.openButton = openButton;
	}

	public int getAgreementId() {
		return agreementId;
	}

	public void setAgreementId(int agreementId) {
		this.agreementId = agreementId;
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
