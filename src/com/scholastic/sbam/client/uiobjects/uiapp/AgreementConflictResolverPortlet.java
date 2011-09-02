package com.scholastic.sbam.client.uiobjects.uiapp;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.Style.SortDir;
import com.extjs.gxt.ui.client.data.BasePagingLoader;
import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.BeanModelReader;
import com.extjs.gxt.ui.client.data.LoadConfig;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoader;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.KeyNav;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.IconButton;
import com.extjs.gxt.ui.client.widget.button.ToolButton;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.FormPanel.LabelAlign;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.grid.GridView;
import com.extjs.gxt.ui.client.widget.grid.RowExpander;
import com.extjs.gxt.ui.client.widget.grid.filters.GridFilters;
import com.extjs.gxt.ui.client.widget.grid.filters.NumericFilter;
import com.extjs.gxt.ui.client.widget.grid.filters.StringFilter;
import com.extjs.gxt.ui.client.widget.layout.CardLayout;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.tips.ToolTipConfig;
import com.extjs.gxt.ui.client.widget.toolbar.LabelToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.scholastic.sbam.client.services.AuthMethodGetService;
import com.scholastic.sbam.client.services.AuthMethodGetServiceAsync;
import com.scholastic.sbam.client.services.AuthMethodSearchService;
import com.scholastic.sbam.client.services.AuthMethodSearchServiceAsync;
import com.scholastic.sbam.client.services.SiteInstitutionWordService;
import com.scholastic.sbam.client.services.SiteInstitutionWordServiceAsync;
import com.scholastic.sbam.client.uiobjects.foundation.AppSleeper;
import com.scholastic.sbam.client.uiobjects.foundation.GridSupportPortlet;
import com.scholastic.sbam.client.util.IconSupplier;
import com.scholastic.sbam.client.util.UiConstants;
import com.scholastic.sbam.shared.exceptions.ServiceNotReadyException;
import com.scholastic.sbam.shared.objects.AuthMethodTuple;
import com.scholastic.sbam.shared.objects.AgreementTermInstance;
import com.scholastic.sbam.shared.objects.SnapshotTreeInstance;
import com.scholastic.sbam.shared.objects.SynchronizedPagingLoadResult;
import com.scholastic.sbam.shared.util.AppConstants;
import com.scholastic.sbam.shared.validation.AsyncValidationResponse;

public class AgreementConflictResolverPortlet extends GridSupportPortlet<AuthMethodTuple> implements AppSleeper, AppPortletRequester {

	protected static final int MIN_FILTER_LENGTH	= 4;
	protected static final int LOAD_LIMIT			= AppConstants.STANDARD_LOAD_LIMIT;
	protected static final int FILTER_LISTEN_PERIOD = 500;	//	This is a little higher, because we're doing a database scan on the back end, so it's not very fast
	
	protected final AuthMethodSearchServiceAsync		authMethodSearchService	= GWT.create(AuthMethodSearchService.class);
	protected final AuthMethodGetServiceAsync    		authMethodGetService			= GWT.create(AuthMethodGetService.class);
//	protected final AgreementGetServiceAsync    		agreementGetService				= GWT.create(AgreementGetService.class);
	protected final SiteInstitutionWordServiceAsync  	siteInstitutionWordService   = GWT.create(SiteInstitutionWordService.class);
	
	protected CardLayout						cards;
	protected ContentPanel						searchPanel;
	protected FormPanel							displayCard;
	protected Grid<ModelData>					grid;
//	protected LiveGridView						liveView;		LiveView removed because local filters don't work with it
	protected GridView							gridView;
	
	protected RowExpander						noteExpander;
	
	protected ListStore<ModelData>				store;
	protected TextField<String>					filterField;
	protected Timer								filterListenTimer;
	protected String							lastTestFilter = "";
	protected String							filter = "";
	protected GridFilters						columnFilters;
	
	protected PagingLoader<PagingLoadResult<AuthMethodTuple>> agreementLoader;

	protected LabelField						agreementIdField;
	protected LabelField						agreementTypeField;
	protected LabelField						linkIdField;
	protected LabelField						ucnField;
	protected LabelField						addressField;
	protected LabelField						customerTypeField;
	protected LabelField						altIds;
	protected ListStore<ModelData>				agreementTermsStore;
	protected Grid<ModelData>					agreementTermsGrid;
	protected FieldSet							agreementsFieldSet;
	
	protected AppPortletProvider				portletProvider;
	
	protected int								focusAgreementId;
	protected String							focusMethodType;
	protected int								focusMethodKey;
	protected String							focusMethodDisplay;
	protected AuthMethodTuple					focusAuthMethod;
	
	protected long								searchSyncId = 0;
	
	public AgreementConflictResolverPortlet() {
		super();
	}
	
	public AgreementConflictResolverPortlet(String helpTextId) {
		super(helpTextId);
	}

	@Override  
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);
		
		setPortletRenderValues();
		
		setLayout(new FitLayout());
		LayoutContainer outerContainer = new LayoutContainer();
		add(outerContainer);
		
		cards = new CardLayout();
		outerContainer.setLayout(cards);
		
		//	We need this to be able to put the tool bar into the top
		searchPanel = new ContentPanel(new FitLayout());
		searchPanel.setHeaderVisible(false);
		searchPanel.setBorders(false);
//		searchPanel.setHeight(540);
		
		setThis();
		addGrid();
		setFilter();
		initializeFilter();
		
		outerContainer.add(searchPanel);
		
		createDisplayCard();
		outerContainer.add(displayCard);
		
		if (focusAgreementId > 0)
			loadAuthMethod(focusAgreementId, focusMethodType, focusMethodKey);
		if (filter != null && filter.length() > 0)
			loadFiltered(filter);
	}
	
	public void setPortletRenderValues() {
		setHeading("Agreement Method Conflict Resolver");
		setToolTip(UiConstants.getQuickTip("Use this portlet to search for agreements by authentication method."));
	}
	
	@Override
	public String getPresenterToolTip() {
		if (focusAuthMethod != null) {
			return "ID " + AppConstants.appendCheckDigit(focusAuthMethod.getAgreement().getId()) + "-" + focusAuthMethod.getAuthMethod().getMethodDisplay();
		}
		if (focusAgreementId != 0 && focusMethodKey != 0)
			if (filter != null && filter.length() > 0)
				return "ID " + focusAgreementId + "-" + focusMethodType + "-" + focusMethodKey + " (" + focusMethodDisplay + ") found for '" + filter + "'";
			else
				return "ID " + focusAgreementId + "-" + focusMethodType + "-" + focusMethodKey + " (" + focusMethodDisplay + ")";
		if (filter != null && filter.length() > 0)
			return "Agreement Authentication Method confict resolution for '" + filter + "'";
		return "Search for agreement authentication method conflicts.";
	}
	
	public void initializeFilter() {
		filterField.setValue(filter);
	}
	
	private void createDisplayCard() {
		FormData formData = new FormData("100%");
		displayCard = new FormPanel();

		displayCard.setPadding(10);  
		displayCard.setFrame(true); 
		displayCard.setHeaderVisible(false);  
		displayCard.setBodyBorder(true);
		displayCard.setBodyStyleName("subtle-form");
		displayCard.setButtonAlign(HorizontalAlignment.CENTER);
		displayCard.setLabelAlign(LabelAlign.RIGHT);
		displayCard.setLabelWidth(100); 
		
		ToolButton returnTool = new ToolButton("x-tool-left") {
				@Override
				protected void onClick(ComponentEvent ce) {
					cards.setActiveItem(searchPanel);
					focusAuthMethod = null;
					focusAgreementId = 0;
					focusMethodKey = 0;
					updateUserPortlet();
					updatePresenterLabel();
				}
			};
		returnTool.enable();
		
		ToolBar displayBar = new ToolBar();
		displayBar.add(returnTool);
		displayBar.add(new SeparatorToolItem());
		displayBar.add(new Html("<b>Selected Agreement</b>"));
		displayCard.setTopComponent(displayBar);

		agreementIdField = new LabelField();
		agreementIdField.setFieldLabel("ID");
		displayCard.add(agreementIdField, formData);

		agreementTypeField = new LabelField();
		agreementTypeField.setFieldLabel("Type");
		displayCard.add(agreementTypeField, formData);
		
		ucnField = new LabelField();  
		ucnField.setFieldLabel("UCN :");
		displayCard.add(ucnField, formData);
		
		addressField = new LabelField(); 
		displayCard.add(addressField, formData); 
		
		altIds = new LabelField(); 
		altIds.setFieldLabel("Alternate IDs :");
		displayCard.add(altIds, formData); 
		
		customerTypeField = new LabelField(); 
		customerTypeField.setFieldLabel("Customer Type :");
		displayCard.add(customerTypeField, formData);
		
		addAuthMethodsGrid(formData);
		addButtons(formData);
		
//		Button returnButton = new Button("Return");
//		returnButton.addSelectionListener(new SelectionListener<ButtonEvent>() {  
//				@Override  
//					public void componentSelected(ButtonEvent ce) {
//						cards.setActiveItem(searchPanel);
//					}  
//			});
//		displayCard.addButton(returnButton);
	}
	

	
	protected void addAuthMethodsGrid(FormData formData) {
		List<ColumnConfig> columns = new ArrayList<ColumnConfig>();
		
		columns.add(getDisplayColumn("productCode",			"Product Code",				100,
					"This is the product code for this term."));
		columns.add(getDisplayColumn("product.description",	"Product",					250,
					"This is the product for this term."));
		columns.add(getDisplayColumn("startDate",			"Start",					80,		true, UiConstants.APP_DATE_TIME_FORMAT,
					"This is the start date for this term."));
		columns.add(getDisplayColumn("endDate",				"End",						80,		true, UiConstants.APP_DATE_TIME_FORMAT,
					"This is the end date for this term."));
		columns.add(getDisplayColumn("terminateDate",		"Siteinate",				80,		true, UiConstants.APP_DATE_TIME_FORMAT,
					"This is the terminate date for this term."));
		columns.add(getDisplayColumn("dollarValue",			"Value",					80,		true, UiConstants.DOLLARS_FORMAT,
					"This is the dollar value for this term."));
		
		ColumnModel cm = new ColumnModel(columns);  

		agreementTermsStore = new ListStore<ModelData>();
		
		agreementTermsGrid = new Grid<ModelData>(agreementTermsStore, cm);  
		agreementTermsGrid.setBorders(true);
		agreementTermsGrid.setHeight(180);
		agreementTermsGrid.setStripeRows(true);
		agreementTermsGrid.setColumnLines(true);
		agreementTermsGrid.setHideHeaders(false);
		agreementTermsGrid.setWidth(cm.getTotalWidth() + 5);
		
		//	Open a new portlet to display an agreement when a row is selected
		agreementTermsGrid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE); 
		final AppPortlet thisPortlet = this; 
		agreementTermsGrid.getSelectionModel().addListener(Events.SelectionChange,  
				new Listener<SelectionChangedEvent<ModelData>>() {  
					public void handleEvent(SelectionChangedEvent<ModelData> be) {  
						if (be.getSelection().size() > 0) {
						//	System.out.println("Agreement " + ((BeanModel) be.getSelectedItem()).get("idCheckDigit"));
							AgreementTermInstance AgreementTerm = (AgreementTermInstance) ((BeanModel) be.getSelectedItem()).getBean();
							AgreementPortlet portlet = (AgreementPortlet) portletProvider.getPortlet(AppPortletIds.AGREEMENT_DISPLAY);
							portlet.setAgreementId(AgreementTerm.getAgreementId());
							if (focusAuthMethod != null) {
								String foundFor = constructFilterDescription();
								portlet.setIdentificationTip("Found for " + foundFor + "");
							}
//							Old, simple way
//							int insertCol = (portalColumn == 0) ? 1 : 0;
//							portletProvider.insertPortlet(portlet, portalRow, insertCol);
//							New, more thorough way
							portletProvider.insertPortlet(portlet, portalRow, thisPortlet.getInsertColumn());
							agreementTermsGrid.getSelectionModel().deselectAll();
						} 
					}
			});
	
		agreementsFieldSet = new FieldSet();
		agreementsFieldSet.setBorders(true);
		agreementsFieldSet.setHeading("Current and Pending Sites");// 		displayCard.add(new LabelField("<br/><i>Existing Agreements</i>"));
		agreementsFieldSet.setCollapsible(true);
		agreementsFieldSet.setToolTip(UiConstants.getQuickTip("These are the current or pending agreement terms for this agreement."));
		agreementsFieldSet.add(agreementTermsGrid);//, new FormData("-24"));	//new FormData(cm.getTotalWidth() + 10, 200));
		
	//	displayCard.add(new LabelField(""));	// Used as a spacer
		displayCard.add(agreementsFieldSet, formData);	//	new FormData("95%")); // new FormData(cm.getTotalWidth() + 20, 200));
		
	}
	
	public String constructFilterDescription() {
		return filter;
	}
	
	protected void addButtons(FormData formData) {
		
		ToolBar toolBar = new ToolBar();
		toolBar.setAlignment(HorizontalAlignment.CENTER);
		toolBar.setToolTip(UiConstants.getQuickTip("Use these buttons to edit this agreement."));
		
		Button newAgreementButton = new Button("Edit Agreement");
		newAgreementButton.setToolTip(UiConstants.getQuickTip("Use this button to edit this agreement."));
		IconSupplier.forceIcon(newAgreementButton, IconSupplier.getNewIconName());
		newAgreementButton.addSelectionListener(new SelectionListener<ButtonEvent>() {  
				@Override
				public void componentSelected(ButtonEvent ce) {
					AgreementPortlet portlet = (AgreementPortlet) portletProvider.getPortlet(AppPortletIds.AGREEMENT_DISPLAY);
					portlet.setAgreementId(focusAgreementId);
					int insertCol = (portalColumn == 0) ? 1 : 0;
					portletProvider.insertPortlet(portlet, portalRow, insertCol);
				}  
			});
		toolBar.add(newAgreementButton);
		
		displayCard.add(toolBar);
	}
	
	protected void showAgreement(BeanModel model) {
		showAuthMethod((AuthMethodTuple) model.getBean());
		// If the agreement term came without a set of other terms, load it again (with the terms)...
		if (focusAuthMethod.getAgreement().getAgreementTerms() == null) {
			loadAuthMethod(focusAgreementId, focusMethodType, focusMethodKey);
		}
	}
	
	protected void showAuthMethod(AuthMethodTuple instance) {
		focusAuthMethod = instance;
		
		if (instance == null)
			return;
		
		focusAgreementId			= focusAuthMethod.getAuthMethod().getAgreementId();
		focusMethodKey		= focusAuthMethod.getAuthMethod().getMethodKey();
		focusMethodDisplay	= focusAuthMethod.getAuthMethod().getMethodDisplay();
		
		registerUserCache(focusAuthMethod, "Found for " + filter);
		updateUserPortlet();

		agreementIdField.setValue(focusAuthMethod.getAgreement().getIdCheckDigit());
		agreementTypeField.setValue(focusAuthMethod.getAgreement().getAgreementType().getDescriptionAndCode());
		ucnField.setValue(focusAuthMethod.getAgreement().getBillUcn());
		
		if (focusAuthMethod.getAgreement().getInstitution() == null) {
			addressField.setValue("");
			altIds.setValue("");
			customerTypeField.setValue("");
		} else {
			addressField.setValue(focusAuthMethod.getAgreement().getInstitution().getHtmlAddress());
			if (focusAuthMethod.getAgreement().getInstitution().getAlternateIds() == null 
			||  focusAuthMethod.getAgreement().getInstitution().getAlternateIds().length() == 0)
				altIds.setValue("None");
			else
				altIds.setValue(focusAuthMethod.getAgreement().getInstitution().getAlternateIds().replace(",", ", "));
			customerTypeField.setValue(focusAuthMethod.getAgreement().getInstitution().getPublicPrivateDescription() + " / " + 
					focusAuthMethod.getAgreement().getInstitution().getGroupDescription() + " &rArr; " + 
					focusAuthMethod.getAgreement().getInstitution().getTypeDescription());
		}
		
		agreementTermsStore.removeAll();
		if (focusAuthMethod.getAgreement().getAgreementTerms() != null)
			for (AgreementTermInstance term : focusAuthMethod.getAgreement().getAgreementTerms())
				agreementTermsStore.add(AgreementTermInstance.obtainModel(term));
		
		cards.setActiveItem(displayCard);

		if (presenter != null)
			presenter.updateLabel(this);
		
	}

//	@Override
//	protected BeanModel getModel(AuthMethodInstance instance) {
//		BeanModel model = super.getModel(instance);
//		String display = "<b>" + instance.getId() + "</b> : " + instance.getLastStartDate() + " &ndash; " + instance.getEndDate();
//		if (!instance.getFirstStartDate().equals(instance.getLastStartDate()))
//			display += " (since " + instance.getFirstStartDate() + ")";
//		model.set("display", display);
//		return model;
//	}
	
	protected void setFilter() {
		
		ToolBar toolBar = new ToolBar();
		toolBar.setAlignment(HorizontalAlignment.RIGHT);
		toolBar.getAriaSupport().setLabel("Filters");

//		LiveToolItem removed because it requires a LiveGridView, which doesn't allow for local filtering
//		LiveToolItem item = new LiveToolItem();  
//		item.bindGrid(grid); 
//		toolBar.add(item); 
		
//		toolBar.add(new FillToolItem());
		
		toolBar.add(new LabelToolItem("Filter by: "));
		
		filterField = getFilterBox();
		filterField.getAriaSupport().setLabelledBy(toolBar.getItem(0).getId());
//		filter.setRawValue(filter);
		toolBar.add(filterField);
		
		toolBar.add(new LabelToolItem("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;")); // Provides space on the right for the error icon
		
		searchPanel.setTopComponent(toolBar);
	}
	
	protected TextField<String> getFilterBox() {

		TextField<String> filterField = new TextField<String>();  
		filterField.setWidth(350); 
		filterField.setEmptyText("Enter search criteria here...");
		
		setFilterListenTimer(filterField);	// This method sends messages using a timer... it is less responsive, but so bothers the server less, and is a little more reliable
		
		return filterField;
	}
	
	protected void setFilterListenTimer(final TextField<String> filterField) {
		filterListenTimer = new Timer() {
			  @Override
			  public void run() {
			//	  System.out.println("Filter: " + filter);
			//	  System.out.println(combo.getRawValue() +  " / " + combo.getValue() + " / " + combo.getOriginalValue());
				  String value = (filterField.getRawValue() == null)?"":filterField.getRawValue().trim();
				  if (!value.equals(filter.trim())) {
			 		  if (value.trim().length() < MIN_FILTER_LENGTH) {
			 			  //	Feedback here...
			 			  grid.getStore().removeAll();
			 			  gridView.setEmptyText("Enter at least " + MIN_FILTER_LENGTH + " characters with which to search.");
			 		  } else {
						  // This optimization makes sure the user has stopped (or at least paused) in typing... if the value is changing, wait until it doesn't
						  if (value.equals(lastTestFilter))
							  loadFiltered(filterField.getRawValue());
						  else
							  lastTestFilter = value;
			 		  }
				  }
			  }
			};

			filterListenTimer.scheduleRepeating(FILTER_LISTEN_PERIOD);
	}
	
	protected void addGrid() {
		agreementLoader = getAgreementLoader(); 

		agreementLoader.setSortDir(SortDir.ASC);  
		agreementLoader.setSortField("id");  

		agreementLoader.setRemoteSort(false);

		store = new ListStore<ModelData>(agreementLoader); 
 
		List<ColumnConfig> columns = new ArrayList<ColumnConfig>();  
		     
//		ColumnConfig name = new ColumnConfig("id", "Agreement #", 80);  
//		name.setRenderer(new GridCellRenderer<ModelData>() {  
//
//		  public Object render(ModelData model, String property, ColumnData config, int rowIndex, int colIndex,  
//		      ListStore<ModelData> store, Grid<ModelData> grid) {  
//		    return "<b>"  
//		        + model.get("institutionName")  
//		        + "</b>";  
//		  }  
//
//		});
		
		columns.add(getDisplayColumn("agreement.idCheckDigit",							"Agreement #",		80));  
		columns.add(getDisplayColumn("authMethod.methodDisplay",						"Method",			160));
		columns.add(getDisplayColumn("authMethod.site.institution.institutionName",		"Site Institution",	150));
		columns.add(getDisplayColumn("authMethod.site.description",						"Site Location",	100));
		columns.add(getDisplayColumn("agreement.agreementTypeCode",						"Type",				30));
//		columns.add(getDisplayColumn("agreement.currentValue",							"Value",			50,		true, UiConstants.DOLLARS_FORMAT));
//		columns.add(getDisplayColumn("agreement.expireDate",							"Expires",		 	70,		true, UiConstants.APP_DATE_TIME_FORMAT)); 
		columns.add(getDisplayColumn("agreement.institution.institutionName",			"Bill Institution",	150));

//		columns.add(getDisplayColumn("conflicts.messages",								"Conflicts",		100));
		
		ColumnConfig conflicts = new ColumnConfig("conflictButton",					"",							30);
		conflicts.setRenderer(getConflictButtonRenderer());
	    columns.add(conflicts);
		
		//	Hidden institution columns
		columns.add(getHiddenColumn("authMethod.ipRangeDisplay",						"IP",				100));
		columns.add(getHiddenColumn("authMethod.userId",								"User ID",			80));
		columns.add(getHiddenColumn("authMethod.password",								"password",			80));
		columns.add(getHiddenColumn("authMethod.url",									"url",				200));
		columns.add(getHiddenColumn("agreement.institution.state",						"State",			50));
		columns.add(getHiddenColumn("agreement.institution.country",					"Country",			50));    
		columns.add(getHiddenColumn("agreement.institution.typeCode",					"Cust Type Code", 	50));
		columns.add(getHiddenColumn("agreement.institution.typeDescription",			"Cust Type", 		100,			false));    
		columns.add(getHiddenColumn("agreement.institution.groupCode",					"Type Group Code", 	50));  
		columns.add(getHiddenColumn("agreement.institution.groupDescription",			"Type Group", 		100,			false));    
		columns.add(getHiddenColumn("agreement.institution.publicPrivateCode",			"Public/Private", 	50));  
		columns.add(getHiddenColumn("agreement.institution.publicPrivateDescription",	"Public/Private Desc", 	100,	false));
		
		noteExpander = getNoteExpander("authMethodNote");
		columns.add(noteExpander);
		
		ColumnModel cm = new ColumnModel(columns);  

		grid = new Grid<ModelData>(store, cm);  
		grid.setBorders(true);  
		grid.setAutoExpandColumn("authMethod.site.institution.institutionName");  
		grid.setLoadMask(true);
		grid.setStripeRows(true);
		grid.setColumnLines(true);
		
		//	Switch to the display card when a row is selected
		grid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE); 
		addRowListener(grid);
		
		addLocalGridFilters();

//		LiveGridView replaced with GridView because local filters don't work with a LiveGridView
//		liveView = new LiveGridView();  
//		liveView.setEmptyText("Enter filter criteria to search for agreements.");
//		liveView.setCacheSize(100);
//		grid.setView(liveView);
		
		gridView = new GridView();
		gridView.setEmptyText("Enter filter criteria to search for agreements.");
		grid.setView(gridView);
		grid.getAriaSupport().setLabelledBy(this.getHeader().getId() + "-label"); 
		
		addGridPlugins(grid);
		
		searchPanel.add(grid);
		  
	}
	
	protected GridCellRenderer<ModelData> getConflictButtonRenderer() {
		GridCellRenderer<ModelData> buttonRenderer = new GridCellRenderer<ModelData>() {   
	
		      public Object render(final ModelData model, String property, ColumnData config, final int rowIndex, final int colIndex, ListStore<ModelData> store, Grid<ModelData> grid) {  
		    	  return getConflictButton(model) ;  
		      }  
	
		};  
		    
		return buttonRenderer;
	}
	
	protected Object getConflictButton(final ModelData model) { 
		  
    	if (model.get("type") != null && SnapshotTreeInstance.FOLDER.equals(model.get("type").toString()))	// For folders
    		return new Html("");
    	
        AuthMethodTuple tuple = ((BeanModel) model).getBean();
		IconButton b = new IconButton("three-state-button") {
        	@Override
        	public void onClick(ComponentEvent ce) {
        		
        	}
        };
        b.setSize(16, 16);
        if (tuple.getConflicts() != null)
        	setConflictTip(b, tuple.getConflicts());
        b.addStyleName("conflict-button");
       
        return b; 
	}
	
	protected void setConflictTip(IconButton infoIcon, AsyncValidationResponse conflicts) {

		infoIcon.setToolTip(getConflictMessageHtml(conflicts));
		infoIcon.getToolTip().getToolTipConfig().setDismissDelay(0);	// So the user can hang out over the icon and keep the messages up
//		infoIcon.getToolTip().getToolTipConfig().setHideDelay(500000);
		if (noConflicts(conflicts)) { 
			infoIcon.getToolTip().removeStyleName("x-form-alert-tip");
			infoIcon.getToolTip().removeStyleName("x-form-info-tip");
			infoIcon.getToolTip().addStyleName("x-form-okay-tip");
			infoIcon.removeStyleName("conflict-alert-button");
			infoIcon.removeStyleName("conflict-info-button");
			infoIcon.addStyleName("conflict-none-button");
		} else if (conflicts.getAlertMessages() != null && conflicts.getAlertMessages().size() > 0) {
			infoIcon.getToolTip().removeStyleName("x-form-okay-tip");
			infoIcon.getToolTip().removeStyleName("x-form-info-tip");
			infoIcon.getToolTip().addStyleName("x-form-alert-tip");
			infoIcon.removeStyleName("conflict-none-button");
			infoIcon.removeStyleName("conflict-info-button");
			infoIcon.addStyleName("conflict-alert-button");
		} else {
			infoIcon.getToolTip().removeStyleName("x-form-okay-tip");
			infoIcon.getToolTip().removeStyleName("x-form-alert-tip");
			infoIcon.getToolTip().addStyleName("x-form-info-tip");
			infoIcon.removeStyleName("conflict-alert-button");
			infoIcon.removeStyleName("conflict-none-button");
			infoIcon.addStyleName("conflict-info-button");
		} 
	}
	
	protected boolean noConflicts(AsyncValidationResponse conflicts) {
		if (conflicts == null)
			return true;
		if (conflicts.getInfoMessages() == null || conflicts.getInfoMessages().size() == 0)
			if (conflicts.getAlertMessages() == null || conflicts.getAlertMessages().size() == 0)
				return true;
		return false;
	}
	
	protected ToolTipConfig getIconButtonToolTip(String tip) {
		return new ToolTipConfig(tip);
	}
	
	protected String getConflictMessageHtml(AsyncValidationResponse conflicts) {
		List<String> infoMsgs = conflicts.getInfoMessages();
		List<String> alertMsgs = conflicts.getAlertMessages();
		
		if (infoMsgs == null || infoMsgs.size() == 0)
			if (alertMsgs == null || alertMsgs.size() == 0) {
				return "No conflicts";
			}
		
		StringBuffer msgs = new StringBuffer();
		if (alertMsgs != null) {
			boolean first = true;
			for (String msg : alertMsgs) {
				if (msgs.length() > 0) msgs.append("<br/>");
				msgs.append("<span class=\"field-alert-msg" + (first?" first-msg":"") + "\">");
				msgs.append(msg);
				msgs.append("</span>");
				first=false;
			}
		}
		if (infoMsgs != null) {
			boolean first = true;
			for (String msg : infoMsgs) {
				if (msgs.length() > 0) msgs.append("<br/>");
				msgs.append("<span class=\"field-info-msg" + (first?" first-msg":"") + "\">");
				msgs.append(msg);
				msgs.append("</span>");
				first=false;
			}
		}
		
		return msgs.toString();
	}
	
	protected void addLocalGridFilters() {
		columnFilters = new GridFilters();  
		columnFilters.setLocal(true);
		columnFilters.setAutoReload(false);
		
		columnFilters.addFilter(new NumericFilter("agreement.idCheckDigit"));
		columnFilters.addFilter(new StringFilter("authMethod.methodDisplay"));
		columnFilters.addFilter(new StringFilter("authMethod.ipRangeDisplay"));
		columnFilters.addFilter(new StringFilter("authMethod.userId"));
		columnFilters.addFilter(new StringFilter("authMethod.password"));
		columnFilters.addFilter(new StringFilter("authMethod.url"));
		columnFilters.addFilter(new StringFilter("agreement.institution.institutionName"));
		columnFilters.addFilter(new StringFilter("authMethod.site.institution.institutionName"));
		columnFilters.addFilter(new StringFilter("authMethod.site.description"));
//		columnFilters.addFilter(new NumericFilter("agreement.currentValue"));
//		columnFilters.addFilter(new DateFilter("agreement.expireDate"));
		columnFilters.addFilter(new StringFilter("agreement.agreementTypeCode"));
		
		grid.addPlugin(columnFilters);
	}
	
	public void addGridPlugins(Grid<ModelData> grid) {
		grid.addPlugin(noteExpander);
	}
	
	protected void addRowListener(Grid<?> grid) {
		grid.addListener(Events.RowClick, new Listener<GridEvent<?>>() {
		      public void handleEvent(GridEvent<?> be) {
		    	  onRowSelected(be);
		      }
		    });
		grid.addListener(Events.SelectionChange, new Listener<GridEvent<?>>() {
		      public void handleEvent(GridEvent<?> be) {
		    	  onRowSelected(be);
		      }
		    });
		
		new KeyNav<GridEvent<?>>(grid) {
		      @Override
		      public void onUp(GridEvent<?> ce) {
		        onRowSelected((BeanModel) ce.getGrid().getSelectionModel().getSelectedItem());
		      }
		      
		      @Override
		      public void onDown(GridEvent<?> ce) {
	        	onRowSelected((BeanModel) ce.getGrid().getSelectionModel().getSelectedItem());
		      }
		    };
	}
	
	@Override
	public void onRowSelected(BeanModel beanModel) {
		if (beanModel != null)
			showAgreement(beanModel);
	}
	
	protected void setThis() {
		this.setLayout(new FitLayout());
		this.setHeight(forceHeight);
		IconSupplier.setIcon(this, IconSupplier.getMethodConflictIconName());
	}
	
	/**
	 * Instigate an asynchronous load with a filter value.
	 * @param filter
	 */
	protected void loadFiltered(String filter) {
		if (filter == null || filter.length() == 0 || filter.length() < MIN_FILTER_LENGTH)
			return;
		
		this.filter = filter;
		
		updateUserPortlet();
		
		agreementLoader.load();
	}
	
	/**
	 * Construct and return a loader to handle returning a list of institutions.
	 * @return
	 */
	protected PagingLoader<PagingLoadResult<AuthMethodTuple>> getAgreementLoader() {
		// proxy and reader  
		RpcProxy<PagingLoadResult<AuthMethodTuple>> proxy = new RpcProxy<PagingLoadResult<AuthMethodTuple>>() {  
			@Override  
			public void load(Object loadConfig, final AsyncCallback<PagingLoadResult<AuthMethodTuple>> callback) {
		    	
				// This could be as simple as calling userListService.getUsers and passing the callback
				// Instead, here the callback is overridden so that it can catch errors and alert the users.  Then the original callback is told of the failure.
				// On success, the original callback is just passed the onSuccess message, and the response (the list).
				
				AsyncCallback<SynchronizedPagingLoadResult<AuthMethodTuple>> myCallback = new AsyncCallback<SynchronizedPagingLoadResult<AuthMethodTuple>>() {
					public void onFailure(Throwable caught) {
						// Show the RPC error message to the user
						if (caught instanceof IllegalArgumentException)
							MessageBox.alert("Alert", caught.getMessage(), null);
						else if (caught instanceof ServiceNotReadyException)
								MessageBox.alert("Alert", "The " + caught.getMessage() + " is not available at this time.  Please try again in a few minutes.", null);
						else {
							MessageBox.alert("Alert", "Agreement load failed unexpectedly.", null);
							System.out.println(caught.getClass().getName());
							System.out.println(caught.getMessage());
						}
						callback.onFailure(caught);
					}

					public void onSuccess(SynchronizedPagingLoadResult<AuthMethodTuple> syncResult) {
						if(syncResult.getSyncId() != searchSyncId)
							return;
						
						PagingLoadResult<AuthMethodTuple> result = syncResult.getResult();
						if ( result.getData() == null || result.getData().size() == 0 ) {
							if (result.getTotalLength() > 0)	// Note that this may be sites, not agreements -- we may not know how many agreements yet
								gridView.setEmptyText(result.getTotalLength() + " authentication methods qualify (too many to display).<br/>Please enter filter criteria to narrow your search.");
							else if (filter.length() == 0)
								gridView.setEmptyText("Enter filter criteria to search for agreements.");
							else
								gridView.setEmptyText("Please enter filter criteria to narrow your search.");
						}
						setSearchMessages(syncResult.getMessages());
						callback.onSuccess(result);
					}
				};

//				final List<Filter> saveFilters = columnFilters.getFilterData();
//				columnFilters.clearFilters();
				
				searchSyncId = System.currentTimeMillis();
				if (setFilterParameters( (LoadConfig) loadConfig ))
					invokeSearchService((PagingLoadConfig) loadConfig, searchSyncId, myCallback);
		    }  
		};
		BeanModelReader reader = new BeanModelReader();
		
		// loader and store  
		PagingLoader<PagingLoadResult<AuthMethodTuple>> loader = new BasePagingLoader<PagingLoadResult<AuthMethodTuple>>(proxy, reader)
//			Use the below code if filtering will be done on the server site
//			{  
//		      @Override  
//		      protected Object newLoadConfig() {  
//		        BasePagingLoadConfig config = new BaseFilterPagingLoadConfig();  
//		        return config;  
//		      }  
//		    }
		;
		return loader;
	}
	
	public void setSearchMessages(List<String> messages) {
		StringBuffer msgHtml = new StringBuffer();
		if (messages != null)
			for (String msg : messages) {
				if (msg != null && msg.trim().length() > 0) {
					if (msgHtml.length() > 0)
						msgHtml.append("<br/>");
					msgHtml.append(msg);
				}
			}
		if (msgHtml.length() > 0)
			filterField.forceInvalid(msgHtml.toString());
		else
			filterField.clearInvalid();
	}
	
	/**
	 * Set any parameters to be passed to the backend service (through loadConfig properties) from any fields.
	 * 
	 * @param loadConfig
	 */
	public boolean setFilterParameters(LoadConfig loadConfig) {
		
		loadConfig.set("filter", filter);
		loadConfig.set("limit", LOAD_LIMIT);
//		loadConfig.set("id", filter);
		
		return true;
	}
 	
	public void invokeSearchService(PagingLoadConfig loadConfig, long searchSyncId, AsyncCallback<SynchronizedPagingLoadResult<AuthMethodTuple>> myCallback) {
		grid.mask("Searching for authentication methods...");
		loadConfig.set("conflicts", "y");
		authMethodSearchService.searchAuthMethods((PagingLoadConfig) loadConfig, searchSyncId, myCallback);
	}

	protected void loadAuthMethod(final int agreementId, final String methodType, final int methodKey) {
		authMethodGetService.getAuthMethod(agreementId, 0, 0, "", methodType, methodKey, true, false,
				new AsyncCallback<AuthMethodTuple>() {
					public void onFailure(Throwable caught) {
						// Show the RPC error message to the user
						if (caught instanceof IllegalArgumentException)
							MessageBox.alert("Alert", caught.getMessage(), null);
						else {
							MessageBox.alert("Alert", "Authentication Method access failed unexpectedly.", null);
							System.out.println(caught.getClass().getName());
							System.out.println(caught.getMessage());
						}
					}

					public void onSuccess(AuthMethodTuple agreement) {
						showAuthMethod(agreement);
					}
			});
	}

//	protected void loadAgreement(final int agreementId) {
//		agreementGetService.getAgreement(agreementId, true,
//				new AsyncCallback<AgreementInstance>() {
//					public void onFailure(Throwable caught) {
//						// Show the RPC error message to the user
//						if (caught instanceof IllegalArgumentException)
//							MessageBox.alert("Alert", caught.getMessage(), null);
//						else {
//							MessageBox.alert("Alert", "Agreement access failed unexpectedly.", null);
//							System.out.println(caught.getClass().getName());
//							System.out.println(caught.getMessage());
//						}
//					}
//
//					public void onSuccess(AgreementInstance agreement) {
//						showAgreement(agreement);
//					}
//			});
//	}
	
	@Override
	public void onExpand() {
		super.onExpand();
		awaken();
	}
	
	@Override
	public void onCollapse() {
		super.onCollapse();
		sleep();
	}
	
	/**
	 * Turn on the listener timer when waking up.
	 */
	@Override
	public void awaken() {
		if (this.isExpanded())
			if (filterListenTimer != null)
				filterListenTimer.scheduleRepeating(FILTER_LISTEN_PERIOD);
	}

	/**
	 * Turn off the listener timer when going to sleep.
	 */
	@Override
	public void sleep() {
		if (filterListenTimer != null) {
			filterListenTimer.cancel();
		}
	}

	@Override
	public void setAppPortletProvider(AppPortletProvider portletProvider) {
		this.portletProvider = portletProvider;
	}
	
	@Override
	public String getShortPortletName() {
		if (focusAgreementId > 0)
			return "Conflict Resolver for " + focusAgreementId;
		if (filter == null || filter.length() == 0)
			return "Method Conflict Resolver";
		if (filter.length() > 10)
			return "Method Conflicts for " + filter.substring(0, 10);
		return "Method Conflicts for " + filter;
	}
	
	@Override
	public boolean allowDuplicatePortlets() {
		//	Not allowed for a particular institution
		if (focusAgreementId > 0)
			return false;
		//	Allowed for any general search
		return true;
	}
	
	@Override
	public String getPortletIdentity() {
		if (focusAgreementId <= 0)
			return super.getPortletIdentity();
		return this.getClass().getName() + ":" + focusAgreementId;
	}

	public int getFocusAgreementId() {
		return focusAgreementId;
	}

	public void setFocusAgreementId(int focusAgreementId) {
		this.focusAgreementId = focusAgreementId;
	}

	public int getFocusAuthMethodId() {
		return focusMethodKey;
	}

	public void setFocusAuthMethodId(int focusAuthMethodId) {
		this.focusMethodKey = focusAuthMethodId;
	}

	public String getFilter() {
		return filter;
	}

	public void setFilter(String filter) {
		this.filter = filter;
	}

	@Override
	public void setFromKeyData(String keyData) {
		if (keyData == null)
			return;
		
		String oldFilter		= null;
		String oldAgreementId   = null;
		String oldMethodType	= null;
		String oldMethodKey		= null;
		String oldMethodDisplay	= null;
		
		keyData = keyData.replace("\\:", "''''");	//	Remove any escaped :
		String [] parts = keyData.split(":");
		
		if (parts.length > 0)
			oldAgreementId = parts [0].trim();
		if (parts.length > 1)
			oldMethodType = parts [1].trim();
		if (parts.length > 2)
			oldMethodKey = parts [2].trim();
		if (parts.length > 3)
			oldMethodDisplay = parts [3].trim().replace("''''", ":");
		if (parts.length > 4)
			oldFilter = parts [4].trim().replace("''''", ":"); //	Restore any escaped :
		
		if (oldMethodType != null) {
			focusMethodType = oldMethodType;
		}
		
		if (oldFilter != null && oldFilter.length() > 0) {
			filter = oldFilter;
		}
		
		if (oldAgreementId != null && oldAgreementId.length() > 0) {
			try {
				focusAgreementId = Integer.parseInt(oldAgreementId);
				focusMethodKey = Integer.parseInt(oldMethodKey);
				focusMethodDisplay = oldMethodDisplay;
			} catch (NumberFormatException e) {
			}
		}
	}

	@Override
	public String getKeyData() {
		if (focusAuthMethod == null)
			return "::::" + filter;
		else	//	Note that we escape any ":" value in the method display and filter with \\:
			return focusAuthMethod.getAuthMethod().getAgreementId() + 
					":" + focusAuthMethod.getAuthMethod().getMethodType() + 
					":" + focusAuthMethod.getAuthMethod().getMethodKey() + 
					":" + focusAuthMethod.getAuthMethod().getMethodDisplay().replace(":", "\\:") + 
					":" + filter.replace(":", "\\:");
	}

}