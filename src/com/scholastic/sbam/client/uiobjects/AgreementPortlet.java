package com.scholastic.sbam.client.uiobjects;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoader;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.ToggleButton;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.FormPanel.LabelAlign;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.LiveGridView;
import com.extjs.gxt.ui.client.widget.grid.RowExpander;
import com.extjs.gxt.ui.client.widget.layout.CardLayout;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.scholastic.sbam.client.services.AgreementGetService;
import com.scholastic.sbam.client.services.AgreementGetServiceAsync;
import com.scholastic.sbam.client.services.InstitutionGetService;
import com.scholastic.sbam.client.services.InstitutionGetServiceAsync;
import com.scholastic.sbam.client.uiobjects.AppPortletIds;
import com.scholastic.sbam.client.uiobjects.AppSleeper;
import com.scholastic.sbam.client.util.IconSupplier;
import com.scholastic.sbam.client.util.UiConstants;
import com.scholastic.sbam.shared.objects.AgreementInstance;
import com.scholastic.sbam.shared.objects.AgreementTermInstance;
import com.scholastic.sbam.shared.objects.InstitutionInstance;
import com.scholastic.sbam.shared.util.AppConstants;

public class AgreementPortlet extends GridSupportPortlet<AgreementTermInstance> implements AppSleeper {
	
	protected final AgreementGetServiceAsync agrementGetService = GWT.create(AgreementGetService.class);
	protected final InstitutionGetServiceAsync institutionGetService = GWT.create(InstitutionGetService.class);
	
	protected int					agreementId;
	protected AgreementInstance		agreement;
	protected InstitutionInstance	billToInstitution;
	protected String				identificationTip;
	
	protected ContentPanel			outerContainer;
	protected CardLayout			cards;
	protected FormPanel				displayCard;
	protected AgreementTermsCard	termsCard;
	protected AgreementSitesCard	sitesCard;
	protected AgreementContactsCard	contactsCard;
	protected Grid<ModelData>		grid;
	protected LiveGridView			liveView;
	
	protected ListStore<ModelData>	store;
	
	protected PagingLoader<PagingLoadResult<InstitutionInstance>> institutionLoader;
	
	protected ToggleButton			agreementButton;
	protected ToggleButton			termsButton;
	protected ToggleButton			sitesButton;
	protected ToggleButton			methodsButton;
	protected ToggleButton			remoteButton;
	protected ToggleButton			contactsButton;

	protected LabelField			agreementIdDisplay;
	protected NumberField			ucnDisplay;
	protected LabelField			addressDisplay;
	protected LabelField			typeDisplay;
	protected LabelField			statusDisplay;
	protected LabelField			commDisplay;
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
	
	public String getIdentificationTip() {
		return identificationTip;
	}

	public void setIdentificationTip(String identificationTip) {
		this.identificationTip = identificationTip;
	}

	protected void setPortletHeading() {
		String heading = "";
		if (agreementId <= 0) {
			heading = "Create New Agreement";
		} else {
			heading = "Agreement #" + AppConstants.appendCheckDigit(agreementId);
		}
		if (billToInstitution != null) {
			heading += " &nbsp;&nbsp;&nbsp; &mdash; <i>" + billToInstitution.getInstitutionName() + "</i>";
		}
		setHeading(heading);
	}
	
	@Override  
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);
		
		if (agreementId == 0) {
			setToolTip(UiConstants.getQuickTip("Use this panel to create a new agreement."));
		}

		setPortletHeading();
		
		setLayout(new FitLayout());
		outerContainer = new ContentPanel();
		outerContainer.setBorders(false);
		outerContainer.setHeaderVisible(false);
		addButtons();
		add(outerContainer);
		
		cards = new CardLayout();
		outerContainer.setLayout(cards);
		
		setThis();
		
		createDisplayCard();
		outerContainer.add(displayCard);
		
		termsCard = new AgreementTermsCard();
		outerContainer.add(termsCard);
		
		sitesCard = new AgreementSitesCard();
		outerContainer.add(sitesCard);
		
		contactsCard = new AgreementContactsCard();
		outerContainer.add(contactsCard);
		
		if (agreementId > 0)
			loadAgreement(agreementId);
	}
	
	private void createDisplayCard() {
		FormData formData = new FormData("100%");
		displayCard = new FormPanel();

//		displayCard.setPadding(40);  
		displayCard.setFrame(true); 
		displayCard.setHeaderVisible(false);  
		displayCard.setBodyBorder(true);
		displayCard.setBorders(false);
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

		agreementIdDisplay = new LabelField();  
		agreementIdDisplay.setFieldLabel("Agreement # :");
		displayCard.add(agreementIdDisplay, formData);

		// ucnDisplay as NumberField
		ucnDisplay = getIntegerField("Bill Institution");
//		ucnDisplay = new NumberField();
////		ucnDisplay.setReadOnly(true);
//		ucnDisplay.setFormat(NumberFormat.getFormat("#"));
//		ucnDisplay.setAllowDecimals(false);
//		ucnDisplay.setAllowNegative(false);
//		ucnDisplay.setEnabled(false);
//		ucnDisplay.addStyleName("field-or-label");
////		ucnDisplay.setMessageTarget(messageTarget)
////		ucnDisplay.setMessages(messages);
////		ucnDisplay.setImages(images);
//		ucnDisplay.setFieldLabel("Bill Institution ");
		displayCard.add(ucnDisplay, formData);
		
//		ucnDisplay = new LabelField();
//		ucnDisplay.setFieldLabel("Bill Institution :");
//		displayCard.add(ucnDisplay, formData);
		
		addressDisplay = new LabelField();
		displayCard.add(addressDisplay, formData); 
		
		typeDisplay = new LabelField();
		typeDisplay.setFieldLabel("Customer Type :");
		displayCard.add(typeDisplay, formData);
			
		statusDisplay = new LabelField();
		statusDisplay.setFieldLabel("Status :");
		displayCard.add(statusDisplay, formData); 
			
		commDisplay = new LabelField();
		commDisplay.setFieldLabel("Commission Code :");
		displayCard.add(statusDisplay, formData); 
		
		addAgreementTermsGrid(formData);
	}
	
	protected void addAgreementTermsGrid(FormData formData) {
		List<ColumnConfig> columns = new ArrayList<ColumnConfig>();
		
		columns.add(getDisplayColumn("productDescription",		"Product",					200,
					"This is the product ordered."));
		columns.add(getDisplayColumn("startDate",				"Start",					80,		true, UiConstants.APP_DATE_TIME_FORMAT,
					"This is the service start date for a product term."));
		columns.add(getDisplayColumn("endDate",					"End",						80,		true, UiConstants.APP_DATE_TIME_FORMAT,
					"This is the service end date for a product term."));
		columns.add(getDisplayColumn("dollarValue",				"Value",					80,		true, NumberFormat.getCurrencyFormat(UiConstants.US_DOLLARS),
					"This is the value of the service."));
		columns.add(getDisplayColumn("termTypeDescription",		"Type",						80,
					"This is the type of service."));

		RowExpander expander = getNoteExpander();
		columns.add(expander);
		
		ColumnModel cm = new ColumnModel(columns);  

		termsStore = new ListStore<ModelData>();
		
		termsGrid = new Grid<ModelData>(termsStore, cm); 
		termsGrid.addPlugin(expander);
		termsGrid.setBorders(true);  
//		termsGrid.setAutoExpandColumn("firstStartDate");  
//		termsGrid.setLoadMask(true);
		termsGrid.setHeight(200);
		termsGrid.setStripeRows(true);
		termsGrid.setColumnLines(false);
		termsGrid.setHideHeaders(false);
		termsGrid.setWidth(cm.getTotalWidth() + 20);
		
		addRowListener(termsGrid);
		
		//	Switch to the display card when a row is selected
		termsGrid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
//		termsGrid.getSelectionModel().addListener(Events.SelectionChange,  
//				new Listener<SelectionChangedEvent<ModelData>>() {
//					public void handleEvent(SelectionChangedEvent<ModelData> be) {  
//						if (be.getSelection().size() > 0) {
//							System.out.println("Agreement Term" + ((BeanModel) be.getSelectedItem()).get("id"));
//							System.out.println(be.getSource().toString() + " / " + be.getSource().getClass().getName());
//						} 
//					}  
//			});
	
		FieldSet fieldSet = new FieldSet();
		fieldSet.setBorders(true);
		fieldSet.setHeading("Product Terms");// 		displayCard.add(new LabelField("<br/><i>Existing Agreements</i>"));
		fieldSet.setCollapsible(true);
		fieldSet.setToolTip(UiConstants.getQuickTip("These are most recent terms for this agreement.  Click the grid to edit or review all terms."));
		fieldSet.add(termsGrid, new FormData(cm.getTotalWidth() + 25, 200));
		
		displayCard.add(new LabelField(""));	// Used as a spacer
		displayCard.add(fieldSet, new FormData("95%")); // new FormData(cm.getTotalWidth() + 20, 200));
	}
	
	/**
	 * What to do when a row is selected.
	 */
	@Override
	protected void onRowSelected(BeanModel data) {
		termsCard.setAgreementId(agreementId);
		termsCard.setAgreementTerm((AgreementTermInstance) data.getBean());
		cards.setActiveItem(termsCard);
		// Set the button states to match the automatic switch
		agreementButton.toggle(false);
		termsButton.toggle(true);
	}
	
	/**
	 * Add the toolbar buttons
	 */
	protected void addButtons() {
		
		ToolBar toolBar = new ToolBar();
		toolBar.setAlignment(HorizontalAlignment.CENTER);
		toolBar.setBorders(false);
		toolBar.setSpacing(20);
		toolBar.setToolTip(UiConstants.getQuickTip("Use these buttons to access detailed information for this agreement."));
		
		agreementButton = new ToggleButton("Agreement");
		agreementButton.setToolTip(UiConstants.getQuickTip("Define and edit the main agreement."));
		IconSupplier.forceIcon(agreementButton, IconSupplier.getAgreementIconName());
		agreementButton.addSelectionListener(new SelectionListener<ButtonEvent>() {  
				@Override
				public void componentSelected(ButtonEvent ce) {
					cards.setActiveItem(displayCard);
					agreementButton.toggle(true);
				}  
			});
		agreementButton.setToggleGroup("agreementGroup");
		toolBar.add(agreementButton);
		
		termsButton = new ToggleButton("Terms");
		termsButton.setToolTip(UiConstants.getQuickTip("Define and edit product terms for this agreement."));
		IconSupplier.forceIcon(termsButton, IconSupplier.getAgreementTermIconName());
		termsButton.addSelectionListener(new SelectionListener<ButtonEvent>() {  
				@Override
				public void componentSelected(ButtonEvent ce) {
					termsCard.setAgreementId(agreementId);
					cards.setActiveItem(termsCard);
					termsButton.toggle(true);
				}  
			});
		termsButton.setToggleGroup("agreementGroup");
		toolBar.add(termsButton);
		
		sitesButton = new ToggleButton("Sites");
		sitesButton.setToolTip(UiConstants.getQuickTip("Define and edit the list of sites for this agreement."));
		IconSupplier.forceIcon(sitesButton, IconSupplier.getSiteIconName());
		sitesButton.addSelectionListener(new SelectionListener<ButtonEvent>() {  
				@Override
				public void componentSelected(ButtonEvent ce) {
					sitesCard.setAgreementId(agreementId);
					cards.setActiveItem(sitesCard);
					sitesButton.toggle(true);
				}  
			});
		sitesButton.setToggleGroup("agreementGroup");
		toolBar.add(sitesButton);
		
		methodsButton = new ToggleButton("Access");
		methodsButton.setToolTip(UiConstants.getQuickTip("Define and edit access methods for this agreement."));
		IconSupplier.forceIcon(methodsButton, IconSupplier.getAccessMethodIconName());
		methodsButton.addSelectionListener(new SelectionListener<ButtonEvent>() {  
				@Override
				public void componentSelected(ButtonEvent ce) {
					methodsButton.toggle(true);
				}
			});
		methodsButton.setToggleGroup("agreementGroup");
		toolBar.add(methodsButton);
		
		remoteButton = new ToggleButton("Remote Setup");
		remoteButton.setToolTip(UiConstants.getQuickTip("Define and edit any remote setup for this agreement."));
		IconSupplier.forceIcon(remoteButton, IconSupplier.getRemoteIconName());
		remoteButton.addSelectionListener(new SelectionListener<ButtonEvent>() {  
				@Override
				public void componentSelected(ButtonEvent ce) {
					remoteButton.toggle(true);
				}
			});
		remoteButton.setToggleGroup("agreementGroup");
		toolBar.add(remoteButton);
		
		contactsButton = new ToggleButton("Contacts");
		contactsButton.setToolTip(UiConstants.getQuickTip("View, define and edit the contacts for this agreement."));
		IconSupplier.forceIcon(contactsButton, IconSupplier.getContactsIconName());
		contactsButton.addSelectionListener(new SelectionListener<ButtonEvent>() {  
				@Override
				public void componentSelected(ButtonEvent ce) {
					contactsCard.setAgreementId(agreementId);
					cards.setActiveItem(contactsCard);
					contactsButton.toggle(true);
				}
			});
		contactsButton.setToggleGroup("agreementGroup");
		toolBar.add(contactsButton);

		agreementButton.toggle(true);
		
		outerContainer.setBottomComponent(toolBar);
	}
	
	/**
	 * Set attributes for the main container
	 */
	protected void setThis() {
//		this.setFrame(true);  
//		this.setCollapsible(true);  
//		this.setAnimCollapse(false);  
//		this.setIcon(Resources.ICONS.table()); 
		this.setLayout(new FitLayout());
		this.setHeight(forceHeight);
		IconSupplier.setIcon(this, IconSupplier.getAgreementIconName());
//		this.setSize(grid.getWidth() + 50, 400);  
	}
	
	/**
	 * Set an agreement on the form, and load its institution
	 * @param agreement
	 */
	protected void set(AgreementInstance agreement) {
		this.agreement = agreement;
		if (agreement == null)
			this.agreementId = -1;
		else
			this.agreementId = agreement.getId();
		registerUserCache(agreement, identificationTip);
		setPortletHeading();

		if (agreement == null) {
			MessageBox.alert("Agreement not found.", "The requested agreement was not found.", null);
		} else {
			if (identificationTip == null)
				agreementIdDisplay.setValue(agreement.getIdCheckDigit());
			else
				agreementIdDisplay.setValue(agreement.getIdCheckDigit() +  "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&mdash;&nbsp;&nbsp;<i>" + identificationTip + "</i>");
			
			ucnDisplay.setValue(agreement.getBillUcn());
			
			ListStore<ModelData> store = termsStore;
			store.removeAll();
			for (AgreementTermInstance agreementTerm : agreement.getAgreementTerms()) {
				store.add(getModel(agreementTerm));
			}
			
			addressDisplay.setValue("<i>Loading...</i>");
			
			if (agreement.getStatus() == AppConstants.STATUS_DELETED)
				statusDisplay.setValue(AppConstants.getStatusDescription(agreement.getStatus()) + " &ndash; " + agreement.getDeleteReasonDescription());
			else
				statusDisplay.setValue(AppConstants.getStatusDescription(agreement.getStatus()));
			typeDisplay.setValue(agreement.getAgreementTypeDescription());
			commDisplay.setValue(agreement.getCommissionCodeDescription());
			
			cards.setActiveItem(displayCard);
			
			loadInstitution(agreement.getBillUcn());
		}
	}
	
	/**
	 * Set an institution on the form
	 * @param instance
	 */
	protected void set(InstitutionInstance instance) {
		billToInstitution = instance;
		setPortletHeading();
		
		if (instance == null) {
			MessageBox.alert("Institution Not Found", "The Institution for the agreement was not found.", null);
			addressDisplay.setValue("");
			typeDisplay.setValue("");
			return;
		}
		
		addressDisplay.setValue("<b>" + instance.getInstitutionName() + "</b><br/>" +
				instance.getAddress1() + brIfNotEmpty(instance.getAddress2()) + brIfNotEmpty(instance.getAddress3()) + "<br/>" +
				instance.getCity() + commaIfNotEmpty(instance.getState()) + spaceIfNotEmpty(instance.getZip()) + 
				brIfNotUsa(instance.getCountry()));

		typeDisplay.setValue(instance.getPublicPrivateDescription() + " / " + instance.getGroupDescription() + " &rArr; " + instance.getTypeDescription());
	}

	/**
	 * Load the agreement for an ID
	 * @param id
	 */
	protected void loadAgreement(final int id) {
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
	 * Load the institution for the agreement
	 * @param ucn
	 */
	protected void loadInstitution(final int ucn) {
		institutionGetService.getInstitution(ucn, false,
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
						set(institution);
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

	/**
	 * For the user cache, set this instance from a string of data stored offline
	 */
	@Override
	public void setFromKeyData(String keyData) {
		if (keyData == null)
			return;
		
		String oldTip = null;
		String oldAid = null;
		
		if (keyData.indexOf(':') >= 0) {
			oldAid = keyData.substring(0, keyData.indexOf(':'));
			oldTip = keyData.substring(keyData.indexOf(':') + 1);
		}
		if (oldTip != null)
			identificationTip = oldTip;
		if (oldAid != null && oldAid.length() > 0) {
			try {
				agreementId = Integer.parseInt(oldAid);
			} catch (NumberFormatException e) {
				return;
			}
		}
	}

	/**
	 * For the user cache, return the "set" data as a string for offline storage
	 */
	@Override
	public String getKeyData() {
		if (identificationTip == null)
			return agreementId + ":";
		else
			return agreementId + ":" + identificationTip;
	}

}