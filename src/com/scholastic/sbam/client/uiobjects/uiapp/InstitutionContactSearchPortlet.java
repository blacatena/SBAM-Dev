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
import com.extjs.gxt.ui.client.widget.button.ToolButton;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.FormPanel.LabelAlign;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridView;
import com.extjs.gxt.ui.client.widget.grid.RowExpander;
import com.extjs.gxt.ui.client.widget.grid.filters.GridFilters;
import com.extjs.gxt.ui.client.widget.grid.filters.NumericFilter;
import com.extjs.gxt.ui.client.widget.grid.filters.StringFilter;
import com.extjs.gxt.ui.client.widget.layout.CardLayout;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.toolbar.LabelToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.scholastic.sbam.client.services.InstitutionContactGetService;
import com.scholastic.sbam.client.services.InstitutionContactGetServiceAsync;
import com.scholastic.sbam.client.services.InstitutionContactSearchService;
import com.scholastic.sbam.client.services.InstitutionContactSearchServiceAsync;
import com.scholastic.sbam.client.uiobjects.foundation.AppSleeper;
import com.scholastic.sbam.client.uiobjects.foundation.GridSupportPortlet;
import com.scholastic.sbam.client.util.IconSupplier;
import com.scholastic.sbam.client.util.UiConstants;
import com.scholastic.sbam.shared.exceptions.ServiceNotReadyException;
import com.scholastic.sbam.shared.objects.AgreementSummaryInstance;
import com.scholastic.sbam.shared.objects.ContactInstance;
import com.scholastic.sbam.shared.objects.InstitutionContactTuple;
import com.scholastic.sbam.shared.objects.InstitutionInstance;
import com.scholastic.sbam.shared.objects.SynchronizedPagingLoadResult;
import com.scholastic.sbam.shared.util.AppConstants;

public class InstitutionContactSearchPortlet extends GridSupportPortlet<InstitutionContactTuple> implements AppSleeper, AppPortletRequester {
	
	protected static final int LOAD_LIMIT			= AppConstants.STANDARD_LOAD_LIMIT;
	protected static final int MIN_FILTER_LENGTH	= 5;
	protected static final int FILTER_LISTEN_PERIOD = 500;	//	This is a little higher, because we want to give the user time to type in a meaningful amount of a name
	
	protected final InstitutionContactSearchServiceAsync	institutionContactSearchService	= GWT.create(InstitutionContactSearchService.class);
	protected final InstitutionContactGetServiceAsync    	institutionContactGetService			= GWT.create(InstitutionContactGetService.class);
//	protected final InstitutionGetServiceAsync    		agreementGetService				= GWT.create(InstitutionGetService.class);
//	protected final SiteInstitutionWordServiceAsync  	siteInstitutionWordService   = GWT.create(SiteInstitutionWordService.class);
	
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
	protected String							lastTestFilter;
	protected String							filter = "";
	protected GridFilters						columnFilters;
	
	protected PagingLoader<PagingLoadResult<InstitutionContactTuple>> institutionContactLoader;


	protected LabelField						fullName;
	protected LabelField						phoneNumber;
	protected LabelField						email;
	protected LabelField						ucn;
	protected LabelField						address;
	protected LabelField						type;
	protected LabelField						altIds;
	protected ListStore<ModelData>				agreementsStore;
	protected Grid<ModelData>					agreementsGrid;
	protected FieldSet							agreementsFieldSet;
	
	protected AppPortletProvider				portletProvider;
	
	protected int								focusUcn;
	protected int								focusInstitutionContactId;
	protected InstitutionContactTuple			focusInstitutionContact;
	
	protected long								searchSyncId = 0;
	
	public InstitutionContactSearchPortlet() {
		super();
	}
	
	public InstitutionContactSearchPortlet(String helpTextId) {
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
		
		if (focusUcn > 0)
			loadInstitutionContact(focusUcn, focusInstitutionContactId);
		if (filter != null && filter.length() > 0)
			loadFiltered(filter);
	}
	
	public void setPortletRenderValues() {
		setHeading("Institution Contact Search");
		setToolTip(UiConstants.getQuickTip("Use this portlet to search for institutions by contact."));
	}
	
	@Override
	public String getPresenterToolTip() {
		if (focusInstitutionContact != null) {
			return "ID " + focusInstitutionContact.getInstitution().getUcn() + "-" + focusInstitutionContact.getInstitutionContact().getContactId();
		}
		if (focusUcn != 0 && focusInstitutionContactId != 0)
			if (filter != null && filter.length() > 0)
				return "ID " + focusUcn + "-" +  focusInstitutionContactId + " found for '" + filter + "'";
			else
				return "ID " + focusUcn + "-" +  focusInstitutionContactId ;
		if (filter != null && filter.length() > 0)
			return "Institution Contact Search for '" + filter + "'";
		return "Search for institution contacts.";
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
					updatePresenterLabel();
				}
			};
		returnTool.enable();
		
		ToolBar displayBar = new ToolBar();
		displayBar.add(returnTool);
		displayBar.add(new SeparatorToolItem());
		displayBar.add(new Html("<b>Selected Institution Contact</b>"));
		displayCard.setTopComponent(displayBar);
		
		fullName = new LabelField();
		fullName.setFieldLabel("Contact :");
		displayCard.add(fullName, formData); 
		
		phoneNumber = new LabelField(); 
		displayCard.add(phoneNumber, formData); 
		
		email = new LabelField(); 
		displayCard.add(email, formData); 

		ucn = new LabelField();  
		ucn.setFieldLabel("UCN :");
		displayCard.add(ucn, formData);
		
		address = new LabelField(); 
		displayCard.add(address, formData); 
		
		altIds = new LabelField(); 
		altIds.setFieldLabel("Alternate IDs :");
		displayCard.add(altIds, formData); 
		
		type = new LabelField(); 
		type.setFieldLabel("Type :");
		displayCard.add(type, formData);
		
		addAgreementsGrid(formData);
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
	

	
	protected void addAgreementsGrid(FormData formData) {
		List<ColumnConfig> columns = new ArrayList<ColumnConfig>();
		
		columns.add(getDisplayColumn("idCheckDigit",		"Agreement ID",				100,	true, NumberFormat.getFormat("#"),
					"This is the ID number for the agreement."));
		columns.add(getDisplayColumn("lastStartDate",		"Start",					80,		true, UiConstants.APP_DATE_TIME_FORMAT,
					"This is the most recent service start date for a product term under this agreement."));
		columns.add(getDisplayColumn("endDate",				"End",						80,		true, UiConstants.APP_DATE_TIME_FORMAT,
					"This is the latest service end date for a product term under this agreement."));
		columns.add(getDisplayColumn("firstStartDate",		"Original Start",			80,		true, UiConstants.APP_DATE_TIME_FORMAT,
					"This is the earliest service start date for a product term under this agreement."));
		columns.add(getDisplayColumn("billUcn",				"Bill UCN",					100,	true, NumberFormat.getFormat("#"),
					"This is the UCN which is being billed for this agreement."));
		
		ColumnModel cm = new ColumnModel(columns);  

		agreementsStore = new ListStore<ModelData>();
		
		agreementsGrid = new Grid<ModelData>(agreementsStore, cm);  
		agreementsGrid.setBorders(true);
		agreementsGrid.setHeight(180);
		agreementsGrid.setStripeRows(true);
		agreementsGrid.setColumnLines(true);
		agreementsGrid.setHideHeaders(false);
		agreementsGrid.setWidth(cm.getTotalWidth() + 5);
		
		//	Open a new portlet to display an agreement when a row is selected
		agreementsGrid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE); 
		final AppPortlet thisPortlet = this; 
		agreementsGrid.getSelectionModel().addListener(Events.SelectionChange,  
				new Listener<SelectionChangedEvent<ModelData>>() {  
					public void handleEvent(SelectionChangedEvent<ModelData> be) {  
						if (be.getSelection().size() > 0) {
						//	System.out.println("Agreement " + ((BeanModel) be.getSelectedItem()).get("idCheckDigit"));
							AgreementSummaryInstance agreement = (AgreementSummaryInstance) ((BeanModel) be.getSelectedItem()).getBean();
							AgreementPortlet portlet = (AgreementPortlet) portletProvider.getPortlet(AppPortletIds.AGREEMENT_DISPLAY);
							portlet.setAgreementId(agreement.getId());
							if (focusInstitutionContact != null && focusInstitutionContact.getInstitution() != null) {
								InstitutionInstance institution = focusInstitutionContact.getInstitution();
								String foundFor = institution.getInstitutionName() != null && institution.getInstitutionName().length() > 0 ? 
										institution.getInstitutionName() : 
													"UCN " + institution.getUcn();
								portlet.setIdentificationTip("Found for " + foundFor + "");
							}
//							Old, simple way
//							int insertCol = (portalColumn == 0) ? 1 : 0;
//							portletProvider.insertPortlet(portlet, portalRow, insertCol);
//							New, more thorough way
							portletProvider.insertPortlet(portlet, portalRow, thisPortlet.getInsertColumn());
							agreementsGrid.getSelectionModel().deselectAll();
						} 
					}
			});
	
		agreementsFieldSet = new FieldSet();
		agreementsFieldSet.setBorders(true);
		agreementsFieldSet.setHeading("Existing Agreements");// 		displayCard.add(new LabelField("<br/><i>Existing Agreements</i>"));
		agreementsFieldSet.setCollapsible(true);
		agreementsFieldSet.setToolTip(UiConstants.getQuickTip("These are the existing agreements for which this institution is either the buyer or a listed site.  Click an agreement to review or edit."));
		agreementsFieldSet.add(agreementsGrid, new FormData(cm.getTotalWidth() + 10, 200));
		
	//	displayCard.add(new LabelField(""));	// Used as a spacer
		displayCard.add(agreementsFieldSet, formData);	//	new FormData("95%")); // new FormData(cm.getTotalWidth() + 20, 200));
		
	}
	
	protected void addButtons(FormData formData) {
		
		ToolBar toolBar = new ToolBar();
		toolBar.setAlignment(HorizontalAlignment.CENTER);
		toolBar.setToolTip(UiConstants.getQuickTip("Use these buttons to access detailed information for this institution."));
		
//		FieldSet buttonSet = new FieldSet();
//		buttonSet.setBorders(true);
//		buttonSet.setHeading(null);// 		displayCard.add(new LabelField("<br/><i>Existing Agreements</i>"));
//		buttonSet.setCollapsible(false);
		
		Button newAgreementButton = new Button("New Agreement");
		newAgreementButton.setToolTip(UiConstants.getQuickTip("Use this button to create a new agreement billed to this institution."));
		IconSupplier.forceIcon(newAgreementButton, IconSupplier.getNewIconName());
		newAgreementButton.addSelectionListener(new SelectionListener<ButtonEvent>() {  
				@Override
				public void componentSelected(ButtonEvent ce) {
					AgreementPortlet portlet = (AgreementPortlet) portletProvider.getPortlet(AppPortletIds.AGREEMENT_DISPLAY);
					portlet.setAgreementId(0);
					portlet.setCreateForInstitution(focusInstitutionContact.getInstitution());
					int insertCol = (portalColumn == 0) ? 1 : 0;
					portletProvider.insertPortlet(portlet, portalRow, insertCol);
				}  
			});
		toolBar.add(newAgreementButton);
		
		Button contactsButton = new Button("Contacts");
		contactsButton.setToolTip(UiConstants.getQuickTip("Use this button to view and edit contacts associated with this institution."));
		IconSupplier.forceIcon(contactsButton, IconSupplier.getContactsIconName());
		contactsButton.addSelectionListener(new SelectionListener<ButtonEvent>() {  
				@Override
				public void componentSelected(ButtonEvent ce) {
				}  
			});
		toolBar.add(contactsButton);
		
		displayCard.add(toolBar);
	}
	
	protected void showInstitutionContact(BeanModel model) {
		showInstitutionContact((InstitutionContactTuple) model.getBean());
	}
	
	protected void showInstitutionContact(InstitutionContactTuple instance) {
		focusInstitutionContact = instance;
		
		if (instance == null)
			return;
		
		ContactInstance		focusContact	 = focusInstitutionContact.getContact();
		InstitutionInstance focusInstitution = focusInstitutionContact.getInstitution();
		
		focusUcn = focusInstitution.getUcn();
		
		registerUserCache(focusInstitution, focusInstitution.getInstitutionName());
		updateUserPortlet();
		
		if (focusContact != null) {
			if (focusContact.getTitle() != null && focusContact.getTitle().length()> 0)
				fullName.setValue(focusContact.getFullName() + " [ " + focusContact.getTitle() + " ]");
			else
				fullName.setValue(focusContact.getFullName());
			phoneNumber.setValue(focusContact.getPhone());
			email.setValue(focusContact.geteMail());
		} else {
			fullName.setValue("");
			phoneNumber.setValue("");
			email.setValue("");
		}
		
		ucn.setValue(focusInstitution.getUcn());
		address.setValue("<b>" + focusInstitution.getInstitutionName() + "</b><br/>" +
				focusInstitution.getAddress1() + brIfNotEmpty(focusInstitution.getAddress2()) + brIfNotEmpty(focusInstitution.getAddress3()) + "<br/>" +
				focusInstitution.getCity() + commaIfNotEmpty(focusInstitution.getState()) + spaceIfNotEmpty(focusInstitution.getZip()) + 
				brIfNotUsa(focusInstitution.getCountry()));
		
		if (focusInstitution.getAlternateIds() == null || focusInstitution.getAlternateIds().length() == 0)
			altIds.setValue("None");
		else
			altIds.setValue(focusInstitution.getAlternateIds().replace(",", ", "));
		type.setValue(focusInstitution.getPublicPrivateDescription() + " / " + focusInstitution.getGroupDescription() + " &rArr; " + focusInstitution.getTypeDescription());
//		group.setValue(instance.getGroupDescription());
		
		ListStore<ModelData> store = agreementsStore;
		store.removeAll();
		if (focusInstitution != null && focusInstitution.getAgreementSummaryList() != null)
			for (AgreementSummaryInstance agreement : focusInstitution.getAgreementSummaryList().values()) {
				store.add(getModel(agreement));
			}
		
		cards.setActiveItem(displayCard);
		if (presenter != null)
			presenter.updateLabel(this);
	}

	protected BeanModel getModel(AgreementSummaryInstance instance) {
		BeanModel model = AgreementSummaryInstance.obtainModel(instance);
		String display = "<b>" + instance.getId() + "</b> : " + instance.getLastStartDate() + " &ndash; " + instance.getEndDate();
		if (!instance.getFirstStartDate().equals(instance.getLastStartDate()))
			display += " (since " + instance.getFirstStartDate() + ")";
		model.set("display", display);
		return model;
	}

//	@Override
//	protected BeanModel getModel(InstitutionContactInstance instance) {
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
		
		toolBar.add(new LabelToolItem(" "));
		
		searchPanel.setTopComponent(toolBar);
	}
	
	protected TextField<String> getFilterBox() {

		TextField<String> filterField = new TextField<String>();  
		filterField.setWidth(250); 
		filterField.setEmptyText("Enter search criteria here...");
		
		setFilterListenTimer(filterField);	// This method sends messages using a timer... it is less responsive, but so bothers the server less, and is a little more reliable
		
		return filterField;
	}
	
	protected void setFilterListenTimer(final TextField<String> filterField) {
		filterListenTimer = new Timer() {
			  @Override
			  public void run() {
				  String value = (filterField.getRawValue() == null)?"":filterField.getRawValue().trim();
			 	  if (!value.equals(filter.trim())) {
			 		  if (value.trim().length() < MIN_FILTER_LENGTH) {
			 			  //	Feedback here...
			 			  grid.getStore().removeAll();
			 			  gridView.setEmptyText("Enter at least " + MIN_FILTER_LENGTH + " characters of a name with which to search.");
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
		institutionContactLoader = getInstitutionContactLoader(); 

		institutionContactLoader.setSortDir(SortDir.ASC);  
		institutionContactLoader.setSortField("contact.fullName");  

		institutionContactLoader.setRemoteSort(false);

		store = new ListStore<ModelData>(institutionContactLoader); 
 
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
		
		columns.add(getDisplayColumn("institution.ucn",									"UCN",			80));  
		columns.add(getDisplayColumn("institutionContact.contact.fullName",				"Contact Name",			150));   
		columns.add(getDisplayColumn("institutionContact.contact.title",				"Title",				100));
//		columns.add(getDisplayColumn("agreement.agreementTypeCode",						"Type",					80));
//		columns.add(getDisplayColumn("agreement.currentValue",							"Value",				50,		true, UiConstants.DOLLARS_FORMAT));
//		columns.add(getDisplayColumn("agreement.expireDate",							"Expires",		 		70,		true, UiConstants.APP_DATE_TIME_FORMAT)); 
		columns.add(getDisplayColumn("institution.institutionName",						"Institution",			150));
		columns.add(getDisplayColumn("institutionContact.contact.eMail",				"E-mail",				100));   
		columns.add(getDisplayColumn("institutionContact.contact.phone",				"Phone",				100));
		
		//	Hidden institution columns
		columns.add(getHiddenColumn("institution.state",								"State",			50));
		columns.add(getHiddenColumn("institution.country",								"Country",			50));    
		columns.add(getHiddenColumn("institution.typeCode",								"Cust Type Code", 	50));
		columns.add(getHiddenColumn("institution.typeDescription",						"Cust Type", 		100,			false));    
		columns.add(getHiddenColumn("institution.groupCode",							"Type Group Code", 	50));  
		columns.add(getHiddenColumn("institution.groupDescription",						"Type Group", 		100,			false));    
		columns.add(getHiddenColumn("institution.publicPrivateCode",					"Public/Private", 	50));  
		columns.add(getHiddenColumn("institution.publicPrivateDescription",				"Public/Private Desc", 	100,	false));
		
		noteExpander = getNoteExpander("contactNote");
		columns.add(noteExpander);
		
		ColumnModel cm = new ColumnModel(columns);  

		grid = new Grid<ModelData>(store, cm);  
		grid.setBorders(true);  
		grid.setAutoExpandColumn("institutionContact.contact.fullName");  
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
		gridView.setEmptyText("Enter filter criteria to search for agreement contacts.");
		grid.setView(gridView);
		grid.getAriaSupport().setLabelledBy(this.getHeader().getId() + "-label"); 
		
		addGridPlugins(grid);
		
		searchPanel.add(grid);
		  
	}
	
	protected void addLocalGridFilters() {
		columnFilters = new GridFilters();  
		columnFilters.setLocal(true);
		columnFilters.setAutoReload(false);
		
		columnFilters.addFilter(new NumericFilter("institution.ucn"));
		columnFilters.addFilter(new StringFilter("institution.institutionName"));
		columnFilters.addFilter(new StringFilter("institution.contact.fullName"));
		columnFilters.addFilter(new StringFilter("institution.contact.title"));
		columnFilters.addFilter(new StringFilter("institution.contact.institution.institutionName"));
		columnFilters.addFilter(new StringFilter("institution.contact.eMail"));
		columnFilters.addFilter(new StringFilter("institution.contact.phone"));
		columnFilters.addFilter(new StringFilter("institution.typeCode"));
		
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
			showInstitutionContact(beanModel);
	}
	
	protected void setThis() {
		this.setLayout(new FitLayout());
		this.setHeight(forceHeight);
		IconSupplier.setIcon(this, IconSupplier.getContactsIconName());
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
		
		institutionContactLoader.load();
	}
	
	/**
	 * Construct and return a loader to handle returning a list of institutions.
	 * @return
	 */
	protected PagingLoader<PagingLoadResult<InstitutionContactTuple>> getInstitutionContactLoader() {
		// proxy and reader  
		RpcProxy<PagingLoadResult<InstitutionContactTuple>> proxy = new RpcProxy<PagingLoadResult<InstitutionContactTuple>>() {  
			@Override  
			public void load(Object loadConfig, final AsyncCallback<PagingLoadResult<InstitutionContactTuple>> callback) {
		    	
				// This could be as simple as calling userListService.getUsers and passing the callback
				// Instead, here the callback is overridden so that it can catch errors and alert the users.  Then the original callback is told of the failure.
				// On success, the original callback is just passed the onSuccess message, and the response (the list).
				
				AsyncCallback<SynchronizedPagingLoadResult<InstitutionContactTuple>> myCallback = new AsyncCallback<SynchronizedPagingLoadResult<InstitutionContactTuple>>() {
					public void onFailure(Throwable caught) {
						// Show the RPC error message to the user
						if (caught instanceof IllegalArgumentException)
							MessageBox.alert("Alert", caught.getMessage(), null);
						else if (caught instanceof ServiceNotReadyException)
								MessageBox.alert("Alert", "The " + caught.getMessage() + " is not available at this time.  Please try again in a few minutes.", null);
						else {
							MessageBox.alert("Alert", "Institution contact search failed unexpectedly.", null);
							System.out.println(caught.getClass().getName());
							System.out.println(caught.getMessage());
						}
						callback.onFailure(caught);
					}

					public void onSuccess(SynchronizedPagingLoadResult<InstitutionContactTuple> syncResult) {
						if(syncResult.getSyncId() != searchSyncId)
							return;
						
						PagingLoadResult<InstitutionContactTuple> result = syncResult.getResult();
						if ( result.getData() == null || result.getData().size() == 0 ) {
							if (result.getTotalLength() > 0)	// Note that this may be sites, not agreements -- we may not know how many agreements yet
								gridView.setEmptyText(result.getTotalLength() + " contacts qualify (too many to display).<br/>Please enter filter criteria to narrow your search.");
							else if (filter.length() == 0)
								gridView.setEmptyText("Enter filter criteria to search for agreements.");
							else
								gridView.setEmptyText("Please enter filter criteria to narrow your search.");
						}
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
		PagingLoader<PagingLoadResult<InstitutionContactTuple>> loader = new BasePagingLoader<PagingLoadResult<InstitutionContactTuple>>(proxy, reader)
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
	
//	/**
//	 * Construct and return a loader to return a list of words.
//	 * 
//	 * @return
//	 */
//	protected PagingLoader<PagingLoadResult<FilterWordInstance>> getWordLoader() {
//		// proxy and reader  
//		RpcProxy<PagingLoadResult<FilterWordInstance>> proxy = new RpcProxy<PagingLoadResult<FilterWordInstance>>() {  
//			@Override  
//			public void load(Object loadConfig, final AsyncCallback<PagingLoadResult<FilterWordInstance>> callback) {
//		    	
//				// This could be as simple as calling userListService.getUsers and passing the callback
//				// Instead, here the callback is overridden so that it can catch errors and alert the users.  Then the original callback is told of the failure.
//				// On success, the original callback is just passed the onSuccess message, and the response (the list).
//				
//				AsyncCallback<PagingLoadResult<FilterWordInstance>> myCallback = new AsyncCallback<PagingLoadResult<FilterWordInstance>>() {
//					public void onFailure(Throwable caught) {
//						// Show the RPC error message to the user
//						if (caught instanceof IllegalArgumentException)
//							MessageBox.alert("Alert", caught.getMessage(), null);
//						else if (caught instanceof ServiceNotReadyException)
//								MessageBox.alert("Alert", "The " + caught.getMessage() + " is not available at this time.  Please try again in a few minutes.", null);
//						else {
//							MessageBox.alert("Alert", "Word load failed unexpectedly.", null);
//							System.out.println(caught.getClass().getName());
//							System.out.println(caught.getMessage());
//						}
//						callback.onFailure(caught);
//					}
//
//					public void onSuccess(PagingLoadResult<FilterWordInstance> result) {
//						callback.onSuccess(result);
//					}
//				};
//
//				invokeWordService((PagingLoadConfig) loadConfig, myCallback);
////				institutionWordService.getInstitutionWords((PagingLoadConfig) loadConfig, myCallback);
//				
//		    }  
//		};
//		BeanModelReader reader = new BeanModelReader();
//		
//		// loader and store  
//		PagingLoader<PagingLoadResult<FilterWordInstance>> loader = new BasePagingLoader<PagingLoadResult<FilterWordInstance>>(proxy, reader);
//		return loader;
//	}
//	
//	protected void invokeWordService(PagingLoadConfig loadConfig, AsyncCallback<PagingLoadResult<FilterWordInstance>>myCallback) {
//		siteInstitutionWordService.getSiteInstitutionWords((PagingLoadConfig) loadConfig, myCallback);
//	}
	
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
 	
	public void invokeSearchService(PagingLoadConfig loadConfig, long searchSyncId, AsyncCallback<SynchronizedPagingLoadResult<InstitutionContactTuple>> myCallback) {
		grid.mask("Searching for institution contacts...");
		institutionContactSearchService.searchInstitutionContacts((PagingLoadConfig) loadConfig, true, searchSyncId, myCallback);
	}

	protected void loadInstitutionContact(final int agreementId, final int contactId) {
		institutionContactGetService.getInstitutionContact(agreementId, contactId,	true,
				new AsyncCallback<InstitutionContactTuple>() {
					public void onFailure(Throwable caught) {
						// Show the RPC error message to the user
						if (caught instanceof IllegalArgumentException)
							MessageBox.alert("Alert", caught.getMessage(), null);
						else {
							MessageBox.alert("Alert", "Institution Contact access failed unexpectedly.", null);
							System.out.println(caught.getClass().getName());
							System.out.println(caught.getMessage());
						}
					}

					public void onSuccess(InstitutionContactTuple institutionContact) {
						showInstitutionContact(institutionContact);
					}
			});
	}

//	protected void loadInstitution(final int agreementId) {
//		agreementGetService.getInstitution(agreementId, true,
//				new AsyncCallback<InstitutionInstance>() {
//					public void onFailure(Throwable caught) {
//						// Show the RPC error message to the user
//						if (caught instanceof IllegalArgumentException)
//							MessageBox.alert("Alert", caught.getMessage(), null);
//						else {
//							MessageBox.alert("Alert", "Institution access failed unexpectedly.", null);
//							System.out.println(caught.getClass().getName());
//							System.out.println(caught.getMessage());
//						}
//					}
//
//					public void onSuccess(InstitutionInstance agreement) {
//						showInstitution(agreement);
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
		if (focusUcn > 0)
			return "Institution " + focusUcn;
		if (filter == null || filter.length() == 0)
			return "Institution Contact Search";
		if (filter.length() > 10)
			return "Institutions for " + filter.substring(0, 10);
		return "Institutions for " + filter;
	}
	
	@Override
	public boolean allowDuplicatePortlets() {
		//	Not allowed for a particular institution
		if (focusUcn > 0)
			return false;
		//	Allowed for any general search
		return true;
	}
	
	@Override
	public String getPortletIdentity() {
		if (focusUcn <= 0)
			return super.getPortletIdentity();
		return this.getClass().getName() + ":" + focusUcn;
	}

	public int getFocusUcn() {
		return focusUcn;
	}

	public void setFocusUcn(int focusUcn) {
		this.focusUcn = focusUcn;
	}

	public int getFocusInstitutionContactId() {
		return focusInstitutionContactId;
	}

	public void setFocusInstitutionContactId(int focusInstitutionContactId) {
		this.focusInstitutionContactId = focusInstitutionContactId;
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
		String oldUcn   = null;
		String oldContactId		= null;
		
		keyData = keyData.replace("\\:", "''''");	//	Remove any escaped :
		String [] parts = keyData.split(":");
		
		if (parts.length > 0)
			oldUcn = parts [0].trim();
		if (parts.length > 1)
			oldContactId = parts [1].trim();
		if (parts.length > 2)
			oldFilter = parts [2].trim().replace("''''", ":"); //	Restore any escaped :
		
		if (oldFilter != null && oldFilter.length() > 0) {
			filter = oldFilter;
		}
		
		if (oldUcn != null && oldUcn.length() > 0) {
			try {
				focusUcn = Integer.parseInt(oldUcn);
				focusInstitutionContactId = Integer.parseInt(oldContactId);
			} catch (NumberFormatException e) {
			}
		}
	}

	@Override
	public String getKeyData() {
		if (focusInstitutionContact == null)
			return "::" + filter;
		else	//	Note that we escape any ":" value in the filter with \\:
			return focusInstitutionContact.getInstitutionContact().getUcn() + ":" +
				   focusInstitutionContact.getInstitutionContact().getContactId() + ":" + 
				   filter.replace(":", "\\:");
	}

}