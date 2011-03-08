package com.scholastic.sbam.client.uiobjects;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoader;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.FormPanel.LabelAlign;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.LiveGridView;
import com.extjs.gxt.ui.client.widget.layout.CardLayout;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.scholastic.sbam.client.services.AgreementGetService;
import com.scholastic.sbam.client.services.AgreementGetServiceAsync;
import com.scholastic.sbam.client.uiobjects.AppPortletIds;
import com.scholastic.sbam.client.uiobjects.AppSleeper;
import com.scholastic.sbam.client.util.IconSupplier;
import com.scholastic.sbam.client.util.UiConstants;
import com.scholastic.sbam.shared.objects.AgreementInstance;
import com.scholastic.sbam.shared.objects.AgreementTermInstance;
import com.scholastic.sbam.shared.objects.InstitutionInstance;

public class AgreementPortlet extends GridSupportPortlet<AgreementTermInstance> implements AppSleeper {
	
	protected final AgreementGetServiceAsync agrementGetService = GWT.create(AgreementGetService.class);
	
	protected int					agreementId;
	protected AgreementInstance		agreement;
	
	protected CardLayout			cards;
	protected FormPanel				displayCard;
	protected Grid<ModelData>		grid;
	protected LiveGridView			liveView;
	
	protected ListStore<ModelData>	store;
	
	protected PagingLoader<PagingLoadResult<InstitutionInstance>> institutionLoader;

	protected LabelField			agreementIdField;
	protected LabelField			ucnField;
	protected LabelField			addressField;
	protected ListStore<ModelData>	termsStore;
	protected Grid<ModelData>		termsGrid;
	
	public AgreementPortlet() {
		super(AppPortletIds.AGREEMENT_DISPLAY.getHelpTextId());
	}
	
	public int getAgreementId() {
		return agreementId;
	}

	public void setAgreementId(int agreementId) {
		this.agreementId = agreementId;
	}

	@Override  
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);
		
//		setTitle("Full Institution Search");
		setHeading("Full Institution Search");
		setToolTip(UiConstants.getQuickTip("Use the Full Institution Search to find any institution available in the customer database, regardless of activity."));
		
		setLayout(new FitLayout());
		LayoutContainer outerContainer = new LayoutContainer();
		add(outerContainer);
		
		cards = new CardLayout();
		outerContainer.setLayout(cards);
		
		setThis();
		
		createDisplayCard();
		outerContainer.add(displayCard);
		
		if (agreementId > 0)
			loadAgreement(agreementId);
	}
	
	private void createDisplayCard() {
		FormData formData = new FormData("100%");
		displayCard = new FormPanel();

		displayCard.setPadding(40);  
		displayCard.setFrame(true); 
		displayCard.setHeaderVisible(false);  
		displayCard.setBodyBorder(true);
		displayCard.setBodyStyleName("subtle-form");
		displayCard.setButtonAlign(HorizontalAlignment.CENTER);
		displayCard.setLabelAlign(LabelAlign.RIGHT);
		displayCard.setLabelWidth(100); 
		
//		ToolButton returnTool = new ToolButton("x-tool-left") {
//				@Override
//				protected void onClick(ComponentEvent ce) {
//					cards.setActiveItem(searchPanel);
//				}
//			};
//		returnTool.enable();
//		
//		ToolBar displayBar = new ToolBar();
//		displayBar.add(returnTool);
//		displayBar.add(new SeparatorToolItem());
//		displayBar.add(new Html("<b>Selected Institution</b>"));
//		displayCard.setTopComponent(displayBar);

		ucnField = new LabelField();  
		ucnField.setFieldLabel("UCN :");
		displayCard.add(ucnField, formData);
		
		addressField = new LabelField(); 
		displayCard.add(addressField, formData); 
		
		addAgreementsGrid(formData);
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

		termsStore = new ListStore<ModelData>();
		
		termsGrid = new Grid<ModelData>(termsStore, cm);  
		termsGrid.setBorders(true);  
//		termsGrid.setAutoExpandColumn("firstStartDate");  
//		termsGrid.setLoadMask(true);
		termsGrid.setHeight(200);
		termsGrid.setStripeRows(true);
		termsGrid.setColumnLines(true);
		termsGrid.setHideHeaders(false);
		termsGrid.setWidth(cm.getTotalWidth() + 5);
		
		//	Switch to the display card when a row is selected
		termsGrid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);  
		termsGrid.getSelectionModel().addListener(Events.SelectionChange,  
				new Listener<SelectionChangedEvent<ModelData>>() {  
					public void handleEvent(SelectionChangedEvent<ModelData> be) {  
						if (be.getSelection().size() > 0) {
							System.out.println("Agreement " + ((BeanModel) be.getSelectedItem()).get("idCheckDigit"));
						} 
					}  
			});
	
		FieldSet fieldSet = new FieldSet();
		fieldSet.setBorders(true);
		fieldSet.setHeading("Existing Agreements");// 		displayCard.add(new LabelField("<br/><i>Existing Agreements</i>"));
		fieldSet.setCollapsible(true);
		fieldSet.setToolTip(UiConstants.getQuickTip("These are the existing terms for which this institution is either the buyer or a listed site.  Click an agreement to review or edit."));
		fieldSet.add(termsGrid, new FormData(cm.getTotalWidth() + 10, 200));
		
		displayCard.add(new LabelField(""));	// Used as a spacer
		displayCard.add(fieldSet, new FormData("95%")); // new FormData(cm.getTotalWidth() + 20, 200));
	}
	
	protected void showAgreement(BeanModel model) {
		AgreementInstance instance = model.getBean();

		ucnField.setValue(instance.getBillUcn());
//		addressField.setValue("<b>" + instance.getInstitutionName() + "</b><br/>" +
//				instance.getAddress1() + brIfNotEmpty(instance.getAddress2()) + brIfNotEmpty(instance.getAddress3()) + "<br/>" +
//				instance.getCity() + commaIfNotEmpty(instance.getState()) + spaceIfNotEmpty(instance.getZip()) + 
//				brIfNotUsa(instance.getCountry()));
		
		ListStore<ModelData> store = termsStore;
		store.removeAll();
		for (AgreementTermInstance agreementTerm : instance.getAgreementTerms()) {
			store.add(getModel(agreementTerm));
		}
		
		cards.setActiveItem(displayCard);
	}
	
	protected void setThis() {
//		this.setFrame(true);  
//		this.setCollapsible(true);  
//		this.setAnimCollapse(false);  
//		this.setIcon(Resources.ICONS.table()); 
		this.setLayout(new FitLayout());
		this.setHeight(550);
		IconSupplier.setIcon(this, IconSupplier.getAgreementIconName());
//		this.setSize(grid.getWidth() + 50, 400);  
	}
	
	protected void set(AgreementInstance agreement) {
		this.agreement = agreement;
	}

	protected void loadAgreement(final int id) {
	//	System.out.println("Before update: " + targetBeanModel.getProperties());
		agrementGetService.getAgreement(id, false,
				new AsyncCallback<AgreementInstance>() {
					public void onFailure(Throwable caught) {
						// Show the RPC error message to the user
						if (caught instanceof IllegalArgumentException)
							MessageBox.alert("Alert", caught.getMessage(), null);
						else {
							MessageBox.alert("Alert", "Agreement access failed unexpectedly.", null);
							System.out.println(caught.getClass().getName());
							System.out.println(caught.getMessage());
						}
					}

					public void onSuccess(AgreementInstance agreement) {
						set(agreement);
					}
			});
	}
	
	/**
	 * Turn on the listener timer when waking up.
	 */
	@Override
	public void awaken() {
		if (this.isExpanded()) {
			
		}
	}

	/**
	 * Turn off the listener timer when going to sleep.
	 */
	@Override
	public void sleep() {
	}

}