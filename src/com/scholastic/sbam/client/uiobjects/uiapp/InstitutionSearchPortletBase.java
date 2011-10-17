package com.scholastic.sbam.client.uiobjects.uiapp;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.Style.SortDir;
import com.extjs.gxt.ui.client.data.BaseModelData;
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
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.ToolButton;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.FormPanel.LabelAlign;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.grid.LiveGridView;
import com.extjs.gxt.ui.client.widget.layout.CardLayout;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.LabelToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.LiveToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.scholastic.sbam.client.services.InstitutionGetService;
import com.scholastic.sbam.client.services.InstitutionGetServiceAsync;
import com.scholastic.sbam.client.uiobjects.events.AppEvent;
import com.scholastic.sbam.client.uiobjects.events.AppEventBus;
import com.scholastic.sbam.client.uiobjects.events.AppEvents;
import com.scholastic.sbam.client.uiobjects.foundation.AppSleeper;
import com.scholastic.sbam.client.uiobjects.foundation.GridSupportPortlet;
import com.scholastic.sbam.client.util.IconSupplier;
import com.scholastic.sbam.client.util.UiConstants;
import com.scholastic.sbam.shared.exceptions.ServiceNotReadyException;
import com.scholastic.sbam.shared.objects.AgreementSummaryInstance;
import com.scholastic.sbam.shared.objects.FilterWordInstance;
import com.scholastic.sbam.shared.objects.InstitutionInstance;
import com.scholastic.sbam.shared.objects.SynchronizedPagingLoadResult;

public class InstitutionSearchPortletBase extends GridSupportPortlet<AgreementSummaryInstance> implements AppSleeper, AppPortletRequester {
	
	protected static final int FILTER_LISTEN_PERIOD = 250;
	
	protected final InstitutionGetServiceAsync    institutionGetService    = GWT.create(InstitutionGetService.class);
	
	protected CardLayout				cards;
	protected ContentPanel				searchPanel;
	protected FormPanel					displayCard;
	protected InstitutionContactsCard	contactsCard;
	protected Grid<ModelData>			grid;
	protected LiveGridView				liveView;
	
	protected ListStore<ModelData>		store;
	protected ComboBox<ModelData>		filterCombo;
	protected Timer						filterListenTimer;
	protected String					filter = "";
	
	protected PagingLoader<PagingLoadResult<InstitutionInstance>> institutionLoader;

	protected LabelField				ucn;
	protected LabelField				address;
	protected LabelField				type;
	protected LabelField				altIds;
	protected ListStore<ModelData>		agreementsStore;
	protected Grid<ModelData>			agreementsGrid;
	protected FieldSet					agreementsFieldSet;
	protected CheckBox					termsTypeCheckBox;
	
	protected AppPortletProvider		portletProvider;
	
	protected int						focusUcn;
	protected InstitutionInstance		focusInstitution;
	
	protected long						searchSyncId = 0;
	
	public InstitutionSearchPortletBase() {
		super();
	}
	
	public InstitutionSearchPortletBase(String helpTextId) {
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
		
		createContactsCard();
		outerContainer.add(contactsCard);
		
		addAppListeners();
		
		if (focusUcn > 0)
			loadInstitution(focusUcn);
	}
	
	public void setPortletRenderValues() {
		setHeading("Override This to Set the Portlet Heading");
		setToolTip(UiConstants.getQuickTip("Override this to set the portlet tooltip."));
	}
	
	@Override
	public String getPresenterToolTip() {
		if (focusInstitution != null) {
			return "UCN " + focusInstitution.getUcn() + " : " + focusInstitution.getInstitutionName();
		}
		if (focusUcn != 0)
			if (filter != null && filter.length() > 0)
				return "UCN " + focusUcn + " found for '" + filter + "'";
			else
				return "UCN " + focusUcn;
		if (filter != null && filter.length() > 0)
			return "Institution Search for '" + filter + "'";
		return "Search for institutions.";
	}
	
	private void initializeFilter() {
		if (filter.length() > 0) {
			ModelData model = new BaseModelData();
			model.set("word", filter);
			filterCombo.setValue(model);
		}
	}
	
	public void addAppListeners() {
		AppEventBus.getSingleton().addListener(AppEvents.NewAgreement, new Listener<AppEvent>() {
			public void handleEvent(AppEvent e) {
				if (focusUcn > 0 && e.getUcn() == focusUcn)
					loadInstitution(focusUcn);
			}
		});
		AppEventBus.getSingleton().addListener(AppEvents.NewSite, new Listener<AppEvent>() {
			public void handleEvent(AppEvent e) {
				if (focusUcn > 0 && e.getUcn() == focusUcn)
					loadInstitution(focusUcn);
			}
		});
		AppEventBus.getSingleton().addListener(AppEvents.ChangeAgreementTerms, new Listener<AppEvent>() {
			public void handleEvent(AppEvent e) {
				if (focusUcn > 0 && e.getUcn() == focusUcn)
					loadInstitution(focusUcn);
			}
		});
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
					focusUcn = 0;
					cards.setActiveItem(searchPanel);
					updatePresenterLabel();
				}
			};
		returnTool.enable();
		
		ToolBar displayBar = new ToolBar();
		displayBar.add(returnTool);
		displayBar.add(new SeparatorToolItem());
		displayBar.add(new Html("<b>Selected Institution</b>"));
		displayCard.setTopComponent(displayBar);

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
		
		termsTypeCheckBox = new CheckBox() {
			@Override
			public void onClick(ComponentEvent ce) {
				super.onClick(ce);
				reloadAgreements();
			}
		};
		termsTypeCheckBox.setFieldLabel("");
		termsTypeCheckBox.setBoxLabel("Show active agreements only");
		termsTypeCheckBox.setValue(true);
		displayCard.add(termsTypeCheckBox);
		
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

	
	protected void createContactsCard() {
		
		contactsCard = new InstitutionContactsCard();
		
		ToolButton returnTool = new ToolButton("x-tool-left") {
				@Override
				protected void onClick(ComponentEvent ce) {
					cards.setActiveItem(displayCard);
					updatePresenterLabel();
				}
			};
		returnTool.enable();
		
		contactsCard.addToolItem(returnTool);
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
							if (focusInstitution != null) {
								String foundFor = focusInstitution.getInstitutionName() != null && focusInstitution.getInstitutionName().length() > 0 ? 
													focusInstitution.getInstitutionName() : 
													"UCN " + focusInstitution.getUcn();
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
					portlet.setCreateForInstitution(focusInstitution);
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
					contactsCard.setInstitution(focusInstitution);
					cards.setActiveItem(contactsCard);
				}  
			});
		toolBar.add(contactsButton);
		
		displayCard.add(toolBar);
	}
	
	protected void showInstitution(BeanModel model) {
		showInstitution((InstitutionInstance) model.getBean());
	}
	
	protected void showInstitution(InstitutionInstance instance) {
		focusInstitution = instance;
		
		if (instance == null)
			return;
		
		focusUcn = focusInstitution.getUcn();
		
		registerUserCache(focusInstitution, focusInstitution.getInstitutionName());
		updateUserPortlet();

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
		
		reloadAgreements();
		
		cards.setActiveItem(displayCard);
		if (presenter != null)
			presenter.updateLabel(this);
	}
	
	protected void reloadAgreements() {
		ListStore<ModelData> store = agreementsStore;
		store.removeAll();
		if (focusInstitution != null && focusInstitution.getAgreementSummaryList() != null) {
			SortedMap<Integer, AgreementSummaryInstance> list;
			if (termsTypeCheckBox == null)
				list = focusInstitution.getAgreementSummaryList(true);
			else
				list = focusInstitution.getAgreementSummaryList(termsTypeCheckBox.getValue());
			for (AgreementSummaryInstance agreement : list.values()) {
				if (agreement != null)
					store.add(getModel(agreement));
			}
		}
	}

	@Override
	protected BeanModel getModel(AgreementSummaryInstance instance) {
		if (instance == null)
			return null;
		BeanModel model = super.getModel(instance);
		String display = "<b>" + instance.getId() + "</b>";
		if (instance.getFirstStartDate() != null) {
			if (instance.getEndDate() != null)
				display += " : " + instance.getLastStartDate() + " &rarr; " + instance.getEndDate();
			else
				display += " : " + instance.getLastStartDate() + " &rarr; ";
			if (instance.getFirstStartDate() != null && !instance.getFirstStartDate().equals(instance.getLastStartDate()))
				display += " (since " + instance.getFirstStartDate() + ")";
		} else
			display += " : No term dates yet.";
		model.set("display", display);
		return model;
	}
	
	protected void setFilter() {
		
		ToolBar toolBar = new ToolBar();
		toolBar.setAlignment(HorizontalAlignment.LEFT);
		toolBar.getAriaSupport().setLabel("Filters");

		LiveToolItem item = new LiveToolItem();  
		item.bindGrid(grid); 
		toolBar.add(item);  
		
		toolBar.add(new FillToolItem());  
		
		toolBar.add(new LabelToolItem("Filter by: "));
		
		ComboBox<ModelData> filter = getFilterBox();
		filter.getAriaSupport().setLabelledBy(toolBar.getItem(0).getId());
//		filter.setRawValue(filter);
		toolBar.add(filter);
		
		searchPanel.setTopComponent(toolBar);
	}
	
	protected ComboBox<ModelData> getFilterBox() {

		PagingLoader<PagingLoadResult<FilterWordInstance>> loader = getWordLoader(); 
		
		ListStore<ModelData> wordStore = new ListStore<ModelData>(loader);  
		
		filterCombo = new ComboBox<ModelData>();  
		filterCombo.setWidth(250); 
		filterCombo.setDisplayField("word");  
		filterCombo.setEmptyText("Enter search criteria here...");
		filterCombo.setStore(wordStore);
		filterCombo.setMinChars(1);
		filterCombo.setHideTrigger(true);  
		filterCombo.setPageSize(10);
		filterCombo.setAllowBlank(true);
		filterCombo.setEditable(true);
//		combo.setTypeAhead(true);
		
//		addComboListeners();			// This method sends messages by listening for keypresses
		
		setFilterListenTimer(filterCombo);	// This method sends messages using a timer... it is less responsive, but so bothers the server less, and is a little more reliable
		
		return filterCombo;
	}

//	This method was abandoned, because the key press fires before the combo field value is changed, and change fires only after the user hits tab or return
//	Applying the raw key code in the key press event (with tab, backspace, and such) is too complex to be worth the time.
//	protected void addComboListeners() {
//		combo.addListener(Events.Change, new Listener<FieldEvent>() {
//			public void handleEvent(FieldEvent be) {
//				System.out.println("change to " + be.getValue().toString());
//				loadFiltered(be.getValue().toString());
//			}
//		});
//		
//		combo.addListener(Events.KeyPress, new Listener<FieldEvent>() {
//			public void handleEvent(FieldEvent be) {
//				String value = (be.getField().getRawValue() == null)?"":be.getField().getRawValue().trim();
//				System.out.println("raw value " + value);
//				System.out.println("plus key press " + value);
//				if (be.getField().getRawValue() == null || be.getField().getRawValue().trim().length() == 0)
//					clearInstitutions("Enter filter criteria to search for institutions.");
//				else
//					loadFiltered(be.getField().getRawValue());
//			}
//		});
//	}
	
	protected void setFilterListenTimer(final ComboBox<ModelData> combo) {
		filterListenTimer = new Timer() {
			  @Override
			  public void run() {
			//	  System.out.println("Filter: " + filter);
			//	  System.out.println(combo.getRawValue() +  " / " + combo.getValue() + " / " + combo.getOriginalValue());
				  String value = (combo.getRawValue() == null)?"":combo.getRawValue().trim();
				  if (!value.equals(filter.trim()))
					  loadFiltered(combo.getRawValue());
			  }
			};

			filterListenTimer.scheduleRepeating(FILTER_LISTEN_PERIOD);
	}
	
	protected void addGrid() {
		institutionLoader = getInstitutionLoader(); 

		institutionLoader.setSortDir(SortDir.ASC);  
		institutionLoader.setSortField("institutionName");  

		institutionLoader.setRemoteSort(true);  

		store = new ListStore<ModelData>(institutionLoader);  
 
		List<ColumnConfig> columns = new ArrayList<ColumnConfig>();  
		   
		columns.add(getHiddenColumn("ucn",		"UCN", 		80));  
		ColumnConfig name = new ColumnConfig("institutionName", "Name", 200);  
		name.setRenderer(new GridCellRenderer<ModelData>() {  

		  public Object render(ModelData model, String property, ColumnData config, int rowIndex, int colIndex,  
		      ListStore<ModelData> store, Grid<ModelData> grid) {  
		    return "<b>"  
		        + model.get("institutionName")  
		        + "</b>";  
		  }  

		});
		
		//	Address columns
		columns.add(name);  
		columns.add(getDisplayColumn("address1",			"Street",			150));  
		columns.add(getDisplayColumn("city",				"City",				100));  
		columns.add(getDisplayColumn("state",				"State",			30));   
		columns.add(getDisplayColumn("zip",					"Zip",				50));   
		
		//	Hidden institution columns
		columns.add(getHiddenColumn("country",				"Country",			50));    
		columns.add(getHiddenColumn("typeCode",				"Type Code", 		50));
		columns.add(getHiddenColumn("typeDescription",		"Type", 			100,			false));    
		columns.add(getHiddenColumn("groupCode",			"Type Group Code", 	50));  
		columns.add(getHiddenColumn("groupDescription",		"Type Group", 		100,			false));    
		columns.add(getHiddenColumn("publicPrivateCode",	"Public/Private", 	50));  
		columns.add(getHiddenColumn("publicPrivateDescription",	"Public/Private Desc", 	100,	false)); 
		columns.add(getHiddenColumn("createdDate",			"Created",		 	70,		true, UiConstants.APP_DATE_TIME_FORMAT)); 
		columns.add(getHiddenColumn("closedDate",			"Closed",		 	70,		true, UiConstants.APP_DATE_TIME_FORMAT)); 
		columns.add(getHiddenColumn("alternateIds",			"Alternate IDs", 	100,	true));
		
		//	Agreement Summary columns
		columns.add(getDisplayColumn("agreementCountCombo",	"Agreements",		70,		false, "The number of active and (total) agreements for this institution."));
		columns.add(getHiddenColumn("agreements",			"Agreements",		70,		true, 	NumberFormat.getFormat("BWZ")));
		columns.add(getHiddenColumn("activeAgreements",		"Active Agreements",70,		true, 	NumberFormat.getFormat("BWZ")));
		columns.add(getDisplayColumn("lastServiceDate",		"Expires",			70,		true, 	UiConstants.APP_DATE_TIME_FORMAT));

		ColumnModel cm = new ColumnModel(columns);  

		grid = new Grid<ModelData>(store, cm);  
		grid.setBorders(true);  
		grid.setAutoExpandColumn("institutionName");  
		grid.setLoadMask(true);
		grid.setStripeRows(true);
		grid.setColumnLines(true);
		
		//	Switch to the display card when a row is selected
		grid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);  
		grid.getSelectionModel().addListener(Events.SelectionChange,  
				new Listener<SelectionChangedEvent<ModelData>>() {  
					public void handleEvent(SelectionChangedEvent<ModelData> be) {  
						if (be.getSelection().size() > 0) {
							showInstitution((BeanModel) be.getSelectedItem());
						} 
					}  
			});  

		liveView = new LiveGridView();  
		liveView.setEmptyText("Enter filter criteria to search for institutions.");
		liveView.setCacheSize(100);
//		liveView.setRowHeight(32);
		grid.setView(liveView);
//		grid.setHeight(550);
		grid.getAriaSupport().setLabelledBy(this.getHeader().getId() + "-label"); 
		searchPanel.add(grid);   
		  
	}
	
	protected void setThis() {
//		this.setFrame(true);  
//		this.setCollapsible(true);  
//		this.setAnimCollapse(false);  
//		this.setIcon(Resources.ICONS.table()); 
		this.setLayout(new FitLayout());
		this.setHeight(forceHeight);
		setPortletIcon();
//		this.setSize(grid.getWidth() + 50, 400);  
	}
	
	protected void setPortletIcon() {
		IconSupplier.setIcon(this, IconSupplier.getInstitutionIconName());
	}
	
//	/**
//	 * Clear the contents of the grid, and display the live text message.  NOT WORKING
//	 * @param message
//	 */
//	protected void clearInstitutions(String message) {
//		liveView.setEmptyText(message);
//		filter=(combo.getRawValue() == null)?"":combo.getRawValue();
//	//	liveView.refresh();
//		store.removeAll();
//		store.commitChanges();
//		liveView.refresh(false);	// THIS WON'T CLEAR THE GRID!!!  WHY NOT?
//	}
	
	/**
	 * Instigate an asynchronous load with a filter value.
	 * @param filter
	 */
	protected void loadFiltered(String filter) {
		this.filter = filter;
		updateUserPortlet();
		institutionLoader.load();
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
						if ( result.getData() == null || result.getData().size() == 0 ) {
							if (result.getTotalLength() > 0)
								liveView.setEmptyText(result.getTotalLength() + " institutions qualify (too many to display).<br/>Please enter filter criteria to narrow your search.");
							else if (filter.length() == 0)
								liveView.setEmptyText("Enter filter criteria to search for institutions.");
							else
								liveView.setEmptyText("Please enter filter criteria to narrow your search.");
						}
						callback.onSuccess(result);
					}
				};

				searchSyncId = System.currentTimeMillis();
				invokeSearchService((PagingLoadConfig) loadConfig, filter, true, searchSyncId, myCallback);
//				institutionSearchService.getInstitutions((PagingLoadConfig) loadConfig, filter, true, searchSyncId, myCallback);
				
		    }  
		};
		BeanModelReader reader = new BeanModelReader();
		
		// loader and store  
		PagingLoader<PagingLoadResult<InstitutionInstance>> loader = new BasePagingLoader<PagingLoadResult<InstitutionInstance>>(proxy, reader);
		return loader;
	}
	
	public void invokeSearchService(PagingLoadConfig loadConfig, String filter, boolean includeAgreementSummaries, long searchSyncId, AsyncCallback<SynchronizedPagingLoadResult<InstitutionInstance>> myCallback) {
		System.out.println("Override this method to call the asyncronous institition search service.");
		MessageBox.alert("Unimplemented Method", "Override this method to call the asyncronous institition search service.", null);
	}
	
	/**
	 * Construct and return a loader to return a list of words.
	 * 
	 * @return
	 */
	protected PagingLoader<PagingLoadResult<FilterWordInstance>> getWordLoader() {
		// proxy and reader  
		RpcProxy<PagingLoadResult<FilterWordInstance>> proxy = new RpcProxy<PagingLoadResult<FilterWordInstance>>() {  
			@Override  
			public void load(Object loadConfig, final AsyncCallback<PagingLoadResult<FilterWordInstance>> callback) {
		    	
				// This could be as simple as calling userListService.getUsers and passing the callback
				// Instead, here the callback is overridden so that it can catch errors and alert the users.  Then the original callback is told of the failure.
				// On success, the original callback is just passed the onSuccess message, and the response (the list).
				
				AsyncCallback<PagingLoadResult<FilterWordInstance>> myCallback = new AsyncCallback<PagingLoadResult<FilterWordInstance>>() {
					public void onFailure(Throwable caught) {
						// Show the RPC error message to the user
						if (caught instanceof IllegalArgumentException)
							MessageBox.alert("Alert", caught.getMessage(), null);
						else if (caught instanceof ServiceNotReadyException)
								MessageBox.alert("Alert", "The " + caught.getMessage() + " is not available at this time.  Please try again in a few minutes.", null);
						else {
							MessageBox.alert("Alert", "Word load failed unexpectedly.", null);
							System.out.println(caught.getClass().getName());
							System.out.println(caught.getMessage());
						}
						callback.onFailure(caught);
					}

					public void onSuccess(PagingLoadResult<FilterWordInstance> result) {
						callback.onSuccess(result);
					}
				};

				invokeWordService((PagingLoadConfig) loadConfig, myCallback);
//				institutionWordService.getInstitutionWords((PagingLoadConfig) loadConfig, myCallback);
				
		    }  
		};
		BeanModelReader reader = new BeanModelReader();
		
		// loader and store  
		PagingLoader<PagingLoadResult<FilterWordInstance>> loader = new BasePagingLoader<PagingLoadResult<FilterWordInstance>>(proxy, reader);
		return loader;
	}
	
	protected void invokeWordService(PagingLoadConfig loadConfig, AsyncCallback<PagingLoadResult<FilterWordInstance>>myCallback) {
		System.out.println("Override this method to call the asyncronous word search service.");
		MessageBox.alert("Unimplemented Method", "Override this method to call the asyncronous word search service.", null);
	}

	protected void loadInstitution(final int ucn) {
		institutionGetService.getInstitution(ucn, true,
				new AsyncCallback<InstitutionInstance>() {
					public void onFailure(Throwable caught) {
						// Show the RPC error message to the user
						if (caught instanceof IllegalArgumentException)
							MessageBox.alert("Alert", caught.getMessage(), null);
						else {
							MessageBox.alert("Alert", "Institution access failed unexpectedly.", null);
							System.out.println(caught.getClass().getName());
							System.out.println(caught.getMessage());
						}
					}

					public void onSuccess(InstitutionInstance institution) {
						showInstitution(institution);
					}
			});
	}
	
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
			return "UCN " + focusUcn;
		if (filter == null || filter.length() == 0)
			return "UCN Search";
		if (filter.length() > 10)
			return "UCNs for " + filter.substring(0, 10);
		return "UCNs for " + filter;
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
		
		String oldFilter = null;
		String oldUcn    = null;
		
		if (keyData.indexOf(':') >= 0) {
			oldUcn = keyData.substring(0, keyData.indexOf(':'));
			oldFilter = keyData.substring(keyData.indexOf(':') + 1);
		}
		if (oldFilter != null && oldFilter.trim().length() > 0) {
			filter = oldFilter.trim();
//			loadFiltered(parts [1]);
		}
		
		if (oldUcn != null && oldUcn.length() > 0) {
			try {
				focusUcn = Integer.parseInt(oldUcn);
			} catch (NumberFormatException e) {
				return;
			}
		}
	}

	@Override
	public String getKeyData() {
		if (focusInstitution == null)
			return ":" + filter;
		else
			return focusInstitution.getUcn() + ":" + filter;
	}

}