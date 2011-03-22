package com.scholastic.sbam.client.uiobjects;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoader;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.ToggleButton;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.LiveGridView;
import com.extjs.gxt.ui.client.widget.grid.RowExpander;
import com.extjs.gxt.ui.client.widget.layout.CardLayout;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Timer;
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
import com.scholastic.sbam.shared.objects.AgreementTypeInstance;
import com.scholastic.sbam.shared.objects.CommissionTypeInstance;
import com.scholastic.sbam.shared.objects.DeleteReasonInstance;
import com.scholastic.sbam.shared.objects.InstitutionInstance;
import com.scholastic.sbam.shared.util.AppConstants;

public class AgreementPortlet extends GridSupportPortlet<AgreementTermInstance> implements AppSleeper {
	
	protected final int DIRTY_FORM_LISTEN_TIME = 3000;
	
	protected final AgreementGetServiceAsync agrementGetService = GWT.create(AgreementGetService.class);
	protected final InstitutionGetServiceAsync institutionGetService = GWT.create(InstitutionGetService.class);
	
	protected int					agreementId;
	protected AgreementInstance		agreement;
	protected InstitutionInstance	billToInstitution;
	protected String				identificationTip	=	"";
	
	protected ContentPanel			outerContainer;
	protected CardLayout			cards;
	protected LayoutContainer		displayCard;
	protected FormPanel				formColumn1;
	protected FormPanel				formColumn2;
	protected FormPanel				formRow2;
	protected AgreementTermsCard	termsCard;
	protected AgreementSitesCard	sitesCard;
	protected AgreementContactsCard	contactsCard;
	protected Grid<ModelData>		grid;
	protected LiveGridView			liveView;
//	protected FormBinding			institutionBinding;
	
	protected ListStore<ModelData>	store;
	
	protected PagingLoader<PagingLoadResult<InstitutionInstance>> institutionLoader;
	
	protected Timer					dirtyFormListener;
	
	protected Button				editButton;
	protected Button				cancelButton;
	protected Button				saveButton;
	
	protected ToggleButton			agreementButton;
	protected ToggleButton			termsButton;
	protected ToggleButton			sitesButton;
	protected ToggleButton			methodsButton;
	protected ToggleButton			remoteButton;
	protected ToggleButton			contactsButton;

	protected NumberField					agreementIdField	= getIntegerField("Agreement #");
	protected NumberField					currentValueField	= getDollarField("Current Value");
	protected LabelField					idTipField			= new LabelField();
	protected InstitutionSearchField		institutionField	= getInstitutionField("billUcn", "Bill To", 300, "The institution that will pay for the products delivered.");
	protected LabelField					addressDisplay		= new LabelField();
	protected NumberField					ucnDisplay			= getIntegerField("UCN");
	protected LabelField					customerTypeDisplay	= new LabelField();
	protected EnhancedComboBox<BeanModel>	agreementTypeField	= getComboField("agreementType", 	"Type",	150,		
								"The agreement type assigned to this agreement for reporting purposes.",	
								UiConstants.getAgreementTypes(), "agreementTypeCode", "descriptionAndCode");
	protected LabelField					statusDisplay		= new LabelField();
	protected EnhancedComboBox<BeanModel>	commissionTypeField	= getComboField("commissionType", 	"Commission",	150,		
								"The commission code assigned to this agreement for reporting purposes.",	
								UiConstants.getCommissionTypes(UiConstants.CommissionTypeTargets.AGREEMENT), "commissionCode", "descriptionAndCode");
	protected EnhancedComboBox<BeanModel>	deleteReasonField	= getComboField("deleteReason", 	"Delete",	150,		
								"The reason for deleting this agreement.",	
								UiConstants.getDeleteReasons(), "deleteReasonCode", "descriptionAndCode");

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
		if (identificationTip == null)
			identificationTip = "";
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
		
//		setLayout(new FitLayout());
		
		setThis();
		
		outerContainer = new ContentPanel();
		outerContainer.setBorders(false);
		outerContainer.setHeaderVisible(false);
		addPanelSwitchTools();
		
		cards = new CardLayout();
		outerContainer.setLayout(cards);
		
		createDisplayCard();
		outerContainer.add(displayCard);
		
		termsCard = new AgreementTermsCard();
		outerContainer.add(termsCard);
		
		sitesCard = new AgreementSitesCard();
		outerContainer.add(sitesCard);
		
		contactsCard = new AgreementContactsCard();
		outerContainer.add(contactsCard);
		
		add(outerContainer);
		
		if (agreementId > 0)
			loadAgreement(agreementId);
	}
	
	private void createDisplayCard() {
		FormData formData = new FormData("100%");
		displayCard = new LayoutContainer(new RowLayout(Orientation.VERTICAL));
		
//		displayCard.addListener(Events.OnClick, new Listener<BaseEvent>() {
//			@Override
//			public void handleEvent(BaseEvent be) {
//				beginEdit();
//			}	
//		});
		
		LayoutContainer sideBySide = new LayoutContainer(new RowLayout(Orientation.HORIZONTAL));
		
		formColumn1 = getNewFormPanel();
		formColumn2 = getNewFormPanel();
		formRow2	= getNewFormPanel();
		
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

		agreementIdField.setReadOnly(true);
		currentValueField.setReadOnly(true);
		ucnDisplay.setReadOnly(true);

		formColumn1.add(agreementIdField, formData);
		formColumn1.add(idTipField, formData);
		formColumn2.add(currentValueField, formData);

		formColumn1.add(institutionField, formData);
		
//		ucnDisplay = new LabelField();
//		ucnDisplay.setFieldLabel("Bill Institution :");
//		displayCard.add(ucnDisplay, formData);
		
		formColumn1.add(addressDisplay, formData); 
		formColumn1.add(ucnDisplay, formData);
		formColumn1.add(customerTypeDisplay, formData); 
		
		formColumn2.add(agreementTypeField, formData); 
			
		formColumn2.add(commissionTypeField, formData); 
			
		statusDisplay.setFieldLabel("Status :");
		formColumn2.add(statusDisplay, formData);
		formColumn2.add(deleteReasonField, formData);
		
		addEditSaveButtons(formColumn2);
		
		addAgreementTermsGrid(formData);
		
		sideBySide.add(formColumn1, new RowData(	0.55,   170, new Margins(0)));
		sideBySide.add(formColumn2, new RowData(	0.45,   170, new Margins(0)));
		displayCard.add(sideBySide, new RowData(	1,	    170, new Margins(0)));
		displayCard.add(formRow2,   new RowData(	1,		  1, new Margins(0)));
	}
	
	protected InstitutionSearchField getInstitutionField(String name, String label, int width, String toolTip) {
        InstitutionSearchField instCombo = new InstitutionSearchField()
//        {
//			@Override
//			public void onSelect(BeanModel model, int index) {
//				super.onSelect(model, index);
//				selectInstitution(model);
//			};
//		}
        ;
		FieldFactory.setStandard(instCombo, label);
		
		if (toolTip != null)
			instCombo.setToolTip(toolTip);
		if (width > 0)
			instCombo.setWidth(width);
		instCombo.setDisplayField("institutionName");
		
		instCombo.addSelectionChangedListener(new SelectionChangedListener<BeanModel>() {

			@Override
			public void selectionChanged(SelectionChangedEvent<BeanModel> se) {
				selectInstitution(se.getSelectedItem());
			}
			
		});
		
		return instCombo;
	}
	
	protected void selectInstitution(BeanModel model) {
		if (model == null)
			matchToInstitution(null);
		else
			matchToInstitution( (InstitutionInstance) model.getBean() );
	}
	
	protected void addAgreementTermsGrid(FormData formData) {
		List<ColumnConfig> columns = new ArrayList<ColumnConfig>();
		
		columns.add(getDisplayColumn("product.description",		"Product",					200,
					"This is the product ordered."));
		columns.add(getDisplayColumn("startDate",				"Start",					80,		true, UiConstants.APP_DATE_TIME_FORMAT,
					"This is the service start date for a product term."));
		columns.add(getDisplayColumn("endDate",					"End",						80,		true, UiConstants.APP_DATE_TIME_FORMAT,
					"This is the service end date for a product term."));
		columns.add(getHiddenColumn("terminateDate",			"Terminate",				80,		true, UiConstants.APP_DATE_TIME_FORMAT));
		columns.add(getDisplayColumn("dollarValue",				"Value",					80,		true, NumberFormat.getCurrencyFormat(UiConstants.US_DOLLARS),
					"This is the value of the service."));
		columns.add(getDisplayColumn("termType.description",	"Type",						80,
					"This is the type of service."));

		RowExpander expander = getNoteExpander();
		columns.add(expander);
		
		ColumnModel cm = new ColumnModel(columns);  

		termsStore = new ListStore<ModelData>();
		
		termsGrid = new Grid<ModelData>(termsStore, cm); 
		termsGrid.addPlugin(expander);
		termsGrid.setBorders(true);  
		termsGrid.setAutoExpandColumn("product.description");  
//		termsGrid.setLoadMask(true);
		termsGrid.setHeight(250);
		termsGrid.setStripeRows(true);
		termsGrid.setColumnLines(false);
		termsGrid.setHideHeaders(false);
//		termsGrid.setWidth(cm.getTotalWidth() + 20);
//		termsGrid.setDeferHeight(true);
		
		addRowListener(termsGrid);
		
		//	Switch to the display card when a row is selected
		termsGrid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
	
		FieldSet fieldSet = new FieldSet();
		fieldSet.setBorders(true);
		fieldSet.setHeading("Product Terms");// 		displayCard.add(new LabelField("<br/><i>Existing Agreements</i>"));
		fieldSet.setCollapsible(true);
		fieldSet.setDeferHeight(true);
		fieldSet.setToolTip(UiConstants.getQuickTip("These are most recent terms for this agreement.  Click the grid to edit or review all terms."));
		fieldSet.add(termsGrid, new FormData("95%")); // new FormData(cm.getTotalWidth() + 25, 200));
		
//		formRow2.add(new LabelField(""));	// Used as a spacer
		formRow2.add(fieldSet, new FormData("100%")); // new FormData(cm.getTotalWidth() + 20, 200));
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
	

	protected void addEditSaveButtons(FormPanel targetPanel) {
		
		ToolBar toolBar = new ToolBar();
		toolBar.setAlignment(HorizontalAlignment.CENTER);
		toolBar.setBorders(false);
		toolBar.setSpacing(20);
		toolBar.setMinButtonWidth(60);
//		toolBar.addStyleName("clear-toolbar");
		
		editButton = new Button("Edit");
		IconSupplier.forceIcon(editButton, IconSupplier.getEditIconName());
		editButton.addSelectionListener(new SelectionListener<ButtonEvent>() {  
			@Override
			public void componentSelected(ButtonEvent ce) {
				beginEdit();
			}  
		});
		toolBar.add(editButton);
		
		cancelButton = new Button("Cancel");
		IconSupplier.forceIcon(cancelButton, IconSupplier.getCancelIconName());
		cancelButton.disable();
		cancelButton.addSelectionListener(new SelectionListener<ButtonEvent>() {  
			@Override
			public void componentSelected(ButtonEvent ce) {
				endEdit(false);
			}  
		});
		toolBar.add(cancelButton);
		
		saveButton = new Button("Save");
		IconSupplier.forceIcon(saveButton, IconSupplier.getSaveIconName());
		saveButton.disable();
		saveButton.addSelectionListener(new SelectionListener<ButtonEvent>() {  
			@Override
			public void componentSelected(ButtonEvent ce) {
				endEdit(true);
			}  
		});
		toolBar.add(saveButton);
		
		targetPanel.setBottomComponent(toolBar);
		
		addDirtyFormListener();
	}
	
	protected void addDirtyFormListener() {
		if (dirtyFormListener == null) {
			dirtyFormListener = new Timer() {

				@Override
				public void run() {
					if (isDirtyForm())
						handleDirtyForm();
					else
						handleCleanForm();
				}
				
			};
		}
		
		dirtyFormListener.scheduleRepeating(DIRTY_FORM_LISTEN_TIME);
	}

	protected boolean isDirtyForm() {

//		System.out.println();
//		System.out.println(System.currentTimeMillis());
//		System.out.println("Original " + institutionField.getOriginalValue());
//		System.out.println("New      " + institutionField.getValue());
//		System.out.println("Raw      " + institutionField.getRawValue());
//		System.out.println("Dirty    " + institutionField.isDirty());
		
		return formColumn1.isDirty() || formColumn2.isDirty();
	}
	
	protected void handleDirtyForm() {
		saveButton.enable();
	}
	
	protected void handleCleanForm() {
		saveButton.disable();
	}
	
	/**
	 * Add the toolbar buttons
	 */
	protected void addPanelSwitchTools() {
		
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
			clearFormValues();
		} else {
			agreementIdField.setValue(agreement.getIdCheckDigit());
			idTipField.setValue(identificationTip);
			
			currentValueField.setValue(agreement.getCurrentValue());
			
//			ucnDisplay.setValue(agreement.getBillUcn());
//			institutionField.setValue(InstitutionInstance.obtainModel(agreement.getInstitution()));
			
			ListStore<ModelData> store = termsStore;
			store.removeAll();
			for (AgreementTermInstance agreementTerm : agreement.getAgreementTerms()) {
				store.add(getModel(agreementTerm));
			}
			
			addressDisplay.setValue("<i>Loading...</i>");
//			customerTypeDisplay.setValue("");
			
			statusDisplay.setValue(AppConstants.getStatusDescription(agreement.getStatus()));
			agreementTypeField.setValue(AgreementTypeInstance.obtainModel(agreement.getAgreementType()));
			commissionTypeField.setValue(CommissionTypeInstance.obtainModel(agreement.getCommissionType()));
			deleteReasonField.setValue(DeleteReasonInstance.obtainModel(agreement.getDeleteReason()));
			
			cards.setActiveItem(displayCard);
			
			if (agreement.getInstitution() != null) {
				set(agreement.getInstitution());
			} else {
				set(InstitutionInstance.getEmptyInstance());
//				loadInstitution(agreement.getBillUcn());
			}
		}

		setOriginalValues();
	}
	
	/**
	 * Set an institution on the form
	 * @param instance
	 */
	protected void set(InstitutionInstance instance) {
		if (billToInstitution == instance)
			return;
		
		billToInstitution = instance;
		
		if (billToInstitution == null) {
			MessageBox.alert("Institution Not Found", "The Institution for the agreement was not found.", null);
			billToInstitution = InstitutionInstance.getEmptyInstance(); 
		}

		if (institutionField.getValue() == null || !billToInstitution.equals(institutionField.getValue().getBean())) {
			institutionField.setValue(InstitutionInstance.obtainModel(billToInstitution));
		}

		setPortletHeading();
		matchToInstitution(billToInstitution);
	}
	
	protected void matchToInstitution(InstitutionInstance instance) {
//		institutionBinding.bind(InstitutionInstance.obtainModel(billToInstitution));
		
		if (instance == null) {
			ucnDisplay.setValue(0);
			addressDisplay.setValue("");
			customerTypeDisplay.setValue("");
			return;
		}
		
		ucnDisplay.setValue(instance.getUcn());
		addressDisplay.setValue(instance.getHtmlAddress());
		customerTypeDisplay.setValue(instance.getPublicPrivateDescription() + " / " + instance.getGroupDescription() + " &rArr; " + instance.getTypeDescription());
	}
	
	public void beginEdit() {
		editButton.disable();
		cancelButton.enable();
		enableFields();
	}
	
	public void endEdit(boolean save) {
		if (save) {
			setOriginalValues();
		} else
			resetFormValues();
		editButton.enable();
		cancelButton.disable();
		saveButton.disable();
		disableFields();
	}
	
	public void clearFormValues() {
		formColumn1.clear();
		formColumn2.clear();
	}
	
	public void resetFormValues() {
		formColumn1.reset();
		formColumn2.reset();
	}
	
	public void setOriginalValues() {
		setOriginalValues(formColumn1);
		setOriginalValues(formColumn2);
	}
	
	@SuppressWarnings("unchecked")
	public void setOriginalValues(FormPanel formPanel) {
		for (Field<?> field : formPanel.getFields()) {
			((Field<Object>) field).setOriginalValue(field.getValue());
		}
	}
	
	public void enableFields() {
		for (Field<?> field : formColumn1.getFields()) {
			field.enable();
		}
		for (Field<?> field : formColumn2.getFields()) {
			field.enable();
		}
	}
	
	public void disableFields() {
		for (Field<?> field : formColumn1.getFields()) {
			field.disable();
		}
		for (Field<?> field : formColumn2.getFields()) {
			field.disable();
		}
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
		if (this.isExpanded()) {
			if (dirtyFormListener != null)
				dirtyFormListener.scheduleRepeating(DIRTY_FORM_LISTEN_TIME);
		}
	}

	/**
	 * Turn off the listener timer when going to sleep.
	 */
	@Override
	public void sleep() {
		if (dirtyFormListener != null)
			dirtyFormListener.cancel();
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