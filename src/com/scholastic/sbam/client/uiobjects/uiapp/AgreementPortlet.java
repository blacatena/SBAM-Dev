package com.scholastic.sbam.client.uiobjects.uiapp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoader;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.ToggleButton;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.MultiField;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.LiveGridView;
import com.extjs.gxt.ui.client.widget.grid.RowExpander;
import com.extjs.gxt.ui.client.widget.layout.CardLayout;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.tips.ToolTipConfig;
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
import com.scholastic.sbam.client.services.UpdateAgreementNoteService;
import com.scholastic.sbam.client.services.UpdateAgreementNoteServiceAsync;
import com.scholastic.sbam.client.services.UpdateAgreementService;
import com.scholastic.sbam.client.services.UpdateAgreementServiceAsync;
import com.scholastic.sbam.client.uiobjects.events.AppEvent;
import com.scholastic.sbam.client.uiobjects.events.AppEventBus;
import com.scholastic.sbam.client.uiobjects.events.AppEvents;
import com.scholastic.sbam.client.uiobjects.fields.AgreementLinkSearchField;
import com.scholastic.sbam.client.uiobjects.fields.EnhancedComboBox;
import com.scholastic.sbam.client.uiobjects.fields.EnhancedMultiField;
import com.scholastic.sbam.client.uiobjects.fields.InstitutionSearchField;
import com.scholastic.sbam.client.uiobjects.fields.NotesIconButtonField;
import com.scholastic.sbam.client.uiobjects.foundation.AppSleeper;
import com.scholastic.sbam.client.uiobjects.foundation.FieldFactory;
import com.scholastic.sbam.client.uiobjects.foundation.GridSupportPortlet;
import com.scholastic.sbam.client.util.IconSupplier;
import com.scholastic.sbam.client.util.UiConstants;
import com.scholastic.sbam.shared.objects.AgreementInstance;
import com.scholastic.sbam.shared.objects.AgreementLinkInstance;
import com.scholastic.sbam.shared.objects.AgreementTermInstance;
import com.scholastic.sbam.shared.objects.AgreementTypeInstance;
import com.scholastic.sbam.shared.objects.AuthMethodInstance;
import com.scholastic.sbam.shared.objects.CommissionTypeInstance;
import com.scholastic.sbam.shared.objects.DeleteReasonInstance;
import com.scholastic.sbam.shared.objects.InstitutionInstance;
import com.scholastic.sbam.shared.objects.RemoteSetupUrlInstance;
import com.scholastic.sbam.shared.objects.SimpleKeyProvider;
import com.scholastic.sbam.shared.objects.UpdateResponse;
import com.scholastic.sbam.shared.objects.UserCacheTarget;
import com.scholastic.sbam.shared.util.AppConstants;

public class AgreementPortlet extends GridSupportPortlet<AgreementTermInstance> implements AppSleeper, AppPortletRequester {
	
	protected final boolean ALL_TERMS			=	true;
	protected final boolean RECENT_TERMS		=	false;
	
	protected final int DIRTY_FORM_LISTEN_TIME	=	250;
	protected final int PRESUMED_FORM_HEIGHT	=	270;
	
	protected final AgreementGetServiceAsync		agrementGetService			= GWT.create(AgreementGetService.class);
	protected final UpdateAgreementServiceAsync		updateAgreementService		= GWT.create(UpdateAgreementService.class);
	protected final UpdateAgreementNoteServiceAsync	updateAgreementNoteService	= GWT.create(UpdateAgreementNoteService.class);
	protected final InstitutionGetServiceAsync		institutionGetService		= GWT.create(InstitutionGetService.class);
	
	protected AppPortletProvider	portletProvider;
	
	protected int					agreementId;
	protected AgreementInstance		agreement;
	protected InstitutionInstance	billToInstitution;
	protected AgreementLinkInstance	agreementLink;
	protected InstitutionInstance	createForInstitution;
	protected String				identificationTip	=	"";
	
	protected AuthMethodInstance		jumpToMethod;
	protected RemoteSetupUrlInstance	jumpToRemoteSetupUrl;
	
	protected ContentPanel			outerContainer;
	protected CardLayout			cards;
	protected LayoutContainer		agreementCard;
	protected FormPanel				formColumn1;
	protected FormPanel				formColumn2;
	protected FormPanel				formRow2;
	protected AgreementTermsCard	termsCard;
	protected AgreementSitesCard	sitesCard;
	protected AgreementContactsCard	contactsCard;
	protected AgreementMethodsCard	methodsCard;
	protected AgreementRemoteSetupCard	remoteSetupCard;
	protected Grid<BeanModel>		grid;
	protected LiveGridView			liveView;
//	protected FormBinding			institutionBinding;
	
	protected ListStore<BeanModel>	store;
	
	protected PagingLoader<PagingLoadResult<InstitutionInstance>> institutionLoader;
	
	protected Timer					dirtyFormListener;
	
	protected Button				editButton;
	protected Button				cancelButton;
	protected Button				saveButton;
	
	protected ToggleButton			agreementButton;
	protected ToggleButton			termsButton;
	protected ToggleButton			sitesButton;
	protected ToggleButton			methodsButton;
	protected ToggleButton			remoteSetupButton;
//	protected ToggleButton			remoteButton;		Remote setup is no longer used
	protected ToggleButton			contactsButton;
	
	protected ToolTipConfig					notesToolTip		= new ToolTipConfig();

	protected FieldSet						termsFieldSet;
	
	protected MultiField<String>			valueNotesCombo		= new EnhancedMultiField<String>("Curr Value");
	protected NumberField					agreementIdField	= getIntegerField("Agreement #");
	protected NotesIconButtonField<String>	notesField			= getNotesButtonField();
	protected NumberField					currentValueField	= getDollarField("Current Value");
	protected LabelField					idTipField			= new LabelField();
	protected InstitutionSearchField		institutionField	= getInstitutionField("billUcn", "Bill To", 260, "The institution that will pay for the products delivered.");
	protected LabelField					addressDisplay		= new LabelField();
	protected NumberField					ucnDisplay			= getIntegerField("UCN");
	protected LabelField					customerTypeDisplay	= new LabelField();
	protected AgreementLinkSearchField		agreementLinkField	= getAgreementLinkField("linkId", "Link", 0, "A link used to relate associated agreements.");
	protected LabelField					linkTypeDisplay		= new LabelField();
	protected FieldSet						linkFieldSet		= new FieldSet() {
																	@Override
																	public void onExpand() {
																		super.onExpand();
																		profileFieldSet.collapse();
																	}
																};
//	protected NumberField					linkIdField			= FieldFactory.getIntegerField("ID");
//	protected TextField<String>				linkTypeField		= FieldFactory.getTextField("Type");
//	protected MultiField<?>					linkField			= new MultiField<String>("Link", linkIdField, linkTypeField);
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

	protected FieldSet						profileFieldSet		= new FieldSet() {
																	@Override
																	public void onExpand() {
																		super.onExpand();
																		linkFieldSet.collapse();
																	}
																};
	protected NumberField					buildingsField		= getIntegerField("Buildings",		0);
	protected NumberField					populationField		= getIntegerField("Population",		0);
	protected NumberField					enrollmentField		= getIntegerField("Enrollment",		0);
	protected NumberField					workstationsField	= getIntegerField("Workstations",	0);

	protected ListStore<BeanModel>	termsStore;
	protected Grid<BeanModel>		termsGrid;
	
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

	public InstitutionInstance getCreateForInstitution() {
		return createForInstitution;
	}

	public void setCreateForInstitution(InstitutionInstance createForInstitution) {
		this.createForInstitution = createForInstitution;
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
	public String getPresenterToolTip() {
		String tooltip = "";
		if (agreementId <= 0) {
			tooltip = "Create a New Agreement";
		} else {
			tooltip = "Agreement #" + AppConstants.appendCheckDigit(agreementId);
		}
		if (billToInstitution != null) {
			tooltip += " &ndash; <i>" + billToInstitution.getInstitutionName() + "</i>";
		}
		if (identificationTip != null && identificationTip.length() > 0) {
			tooltip += "<br/><i>" + identificationTip + "</i>";
		}
		return tooltip;
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
		outerContainer.add(agreementCard);
		
		termsCard = new AgreementTermsCard();
		termsCard.setAgreementGridStore(termsStore);
		outerContainer.add(termsCard);
		
		sitesCard = new AgreementSitesCard();
		sitesCard.setAppPortletProvider(portletProvider);
		outerContainer.add(sitesCard);
		
		contactsCard = new AgreementContactsCard();
		outerContainer.add(contactsCard);
		
		methodsCard = new AgreementMethodsCard();
		methodsCard.setAppPortletProvider(portletProvider);
		outerContainer.add(methodsCard);
		
		remoteSetupCard = new AgreementRemoteSetupCard();
		outerContainer.add(remoteSetupCard);
		
		add(outerContainer);
		
		if (agreementId > 0)
			loadAgreement(agreementId);
	}
	
	@Override
	protected void afterRender() {
		super.afterRender();
		if (agreementId == 0) {
			if (createForInstitution != null)
				set(createForInstitution);
			linkFieldSet.collapse();
			beginEdit();
		}
	}
	
	private void createDisplayCard() {
		FormData formData90 = new FormData("-24"); 	//	new FormData("90%");
//		FormData formData	= new FormData("100%");
		agreementCard = new LayoutContainer(new RowLayout(Orientation.VERTICAL));
		
		LayoutContainer sideBySide = new LayoutContainer(new RowLayout(Orientation.HORIZONTAL));
		
		formColumn1 = getNewOuterFormPanel();
		formColumn2 = getNewOuterFormPanel();
		formRow2	= getNewOuterFormPanel();
		formRow2.setLayout(new FitLayout());
		
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
//		agreementCard.setTopComponent(displayBar);

		agreementIdField.setReadOnly(true);
		currentValueField.setReadOnly(true);
		ucnDisplay.setReadOnly(true);
		
		currentValueField.setWidth(120);
		valueNotesCombo.setSpacing(20);
		
		formColumn1.add(agreementIdField, formData90);
		formColumn1.add(idTipField, formData90);
		
		valueNotesCombo.add(currentValueField);	
		valueNotesCombo.add(notesField);
		formColumn2.add(valueNotesCombo,    formData90);

		formColumn1.add(institutionField, formData90);
		
//		ucnDisplay = new LabelField();
//		ucnDisplay.setFieldLabel("Bill Institution :");
//		agreementCard.add(ucnDisplay, formData90);
		
		formColumn1.add(addressDisplay, formData90); 
		formColumn1.add(ucnDisplay, formData90);
		formColumn1.add(customerTypeDisplay, formData90); 
		
		formColumn2.add(agreementTypeField, formData90); 
			
		formColumn2.add(commissionTypeField, formData90); 
			
		statusDisplay.setFieldLabel("Status:");
		formColumn1.add(statusDisplay, formData90);
		formColumn1.add(deleteReasonField, formData90);
		

		linkFieldSet.setBorders(true);
		linkFieldSet.setHeading("Agreement Link");// 		agreementCard.add(new LabelField("<br/><i>Existing Agreements</i>"));
		linkFieldSet.setCollapsible(true);
//		linkFieldSet.setCheckboxToggle(true);
//		linkFieldSet.collapse();
		FormLayout fLayout = new FormLayout();
		fLayout.setLabelWidth(60);
		linkFieldSet.setLayout(fLayout);
		linkFieldSet.setToolTip(UiConstants.getQuickTip("Use these fields to associated the agreement with a link."));
//		linkFieldSet.setLayout(new FitLayout());
//		linkFieldSet.setHeight(300);

		linkFieldSet.add(agreementLinkField, formData90);
		linkFieldSet.add(linkTypeDisplay, formData90);
		formColumn2.add(linkFieldSet);

		FormLayout profileLayout = new FormLayout();
		profileLayout.setLabelAlign(formColumn2.getLabelAlign());
		profileLayout.setLabelWidth(formColumn2.getLabelWidth() - 10);
		profileFieldSet.setLayout(profileLayout);
		profileFieldSet.setBorders(true);
		profileFieldSet.setHeading("Profile");
		profileFieldSet.setWidth(0);//buildingsField.getWidth() + 50);
		profileFieldSet.setCollapsible(true);
		
		buildingsField.setMinValue(0);
		buildingsField.setMaxValue(999999999);
		populationField.setMinValue(0);
		populationField.setMaxValue(999999999);
		enrollmentField.setMinValue(0);
		enrollmentField.setMaxValue(999999999);
		workstationsField.setMinValue(0);
		workstationsField.setMaxValue(999999999);
		
		profileFieldSet.add(buildingsField, formData90);
		profileFieldSet.add(populationField, formData90);
		profileFieldSet.add(enrollmentField, formData90);
		profileFieldSet.add(workstationsField, formData90);
		formColumn2.add(profileFieldSet, formData90);
		
		
//		linkField.setOrientation(Orientation.HORIZONTAL);
//		linkField.setSpacing(20);
//		formColumn1.add(linkField, formData90);
		
		addEditSaveButtons(formColumn1);
		
		addAgreementTermsGrid();
		
		sideBySide.add(formColumn1, new RowData(	0.52,	PRESUMED_FORM_HEIGHT));
		sideBySide.add(formColumn2, new RowData(	0.48,	PRESUMED_FORM_HEIGHT));
		agreementCard.add(sideBySide, new RowData(	1,		PRESUMED_FORM_HEIGHT));
		agreementCard.add(formRow2,   new RowData(	1,		1));
	}
	
	protected InstitutionSearchField getInstitutionField(String name, String label, int width, String toolTip) {
        InstitutionSearchField instCombo = new InstitutionSearchField();
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
		if (model == null) {	// No value selected means leave it as is
			if (institutionField.getSelectedValue() != null)
				matchToInstitution( (InstitutionInstance) institutionField.getSelectedValue().getBean() );
			else
				if (institutionField.getOriginalValue() != null)
					matchToInstitution( (InstitutionInstance) institutionField.getOriginalValue().getBean());
				else
					matchToInstitution( billToInstitution );
		} else
			matchToInstitution( (InstitutionInstance) model.getBean() );
	}
	
	protected AgreementLinkSearchField getAgreementLinkField(String name, String label, int width, String toolTip) {
        AgreementLinkSearchField linkCombo = new AgreementLinkSearchField(this);
		FieldFactory.setStandard(linkCombo, label);
		
		if (toolTip != null)
			linkCombo.setToolTip(toolTip);
		if (width >= 0)
			linkCombo.setWidth(width);
		linkCombo.setDisplayField("descriptionAndCode");
		
		linkCombo.addSelectionChangedListener(new SelectionChangedListener<BeanModel>() {

			@Override
			public void selectionChanged(SelectionChangedEvent<BeanModel> se) {
				selectAgreementLink(se.getSelectedItem());
			}
			
		});
		
		return linkCombo;
	}
	
	protected void selectAgreementLink(BeanModel model) {
		if (model == null) {	// No value selected means leave it as is
			if (agreementLinkField.getSelectedValue() != null)
				matchToAgreementLink( (AgreementLinkInstance) agreementLinkField.getSelectedValue().getBean() );
			else
				if (agreementLinkField.getOriginalValue() != null)
					matchToAgreementLink( (AgreementLinkInstance) agreementLinkField.getOriginalValue().getBean());
				else
					matchToAgreementLink( agreementLink );
		} else
			matchToAgreementLink( (AgreementLinkInstance) model.getBean() );
	}
	
	protected void addAgreementTermsGrid() {
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

		termsStore = new ListStore<BeanModel>();
		termsStore.setKeyProvider(new SimpleKeyProvider("uniqueKey"));
		
		termsGrid = new Grid<BeanModel>(termsStore, cm); 
		termsGrid.addPlugin(expander);
		termsGrid.setBorders(true);  
		termsGrid.setAutoExpandColumn("product.description"); 
		termsGrid.setStripeRows(true);
		termsGrid.setColumnLines(false);
		termsGrid.setHideHeaders(false);
		
		addRowListener(termsGrid);
		
		//	Switch to the display card when a row is selected
		termsGrid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
	
		termsFieldSet = new FieldSet();
		termsFieldSet.setBorders(true);
		termsFieldSet.setHeading("Product Terms");// 		agreementCard.add(new LabelField("<br/><i>Existing Agreements</i>"));
		termsFieldSet.setCollapsible(true);
		termsFieldSet.setDeferHeight(true);
		termsFieldSet.setToolTip(UiConstants.getQuickTip("These are most recent terms for this agreement.  Click the grid to edit or review all terms."));
		termsFieldSet.setLayout(new FitLayout());
		termsFieldSet.setHeight(300);
		termsFieldSet.add(termsGrid, new FormData("95%")); // new FormData(cm.getTotalWidth() + 25, 200));
		
		formRow2.add(termsFieldSet, new FormData("100%")); // new FormData(cm.getTotalWidth() + 20, 200));
	}
	
	/**
	 * What to do when a row is selected.
	 */
	@Override
	protected void onRowSelected(BeanModel data) {
		termsCard.setAgreementId(agreementId);
		termsCard.setAgreement(agreement);
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
				handleSave();
			}  
		});
		toolBar.add(saveButton);
		
		targetPanel.setBottomComponent(toolBar);
		
		addDirtyFormListener();
	}
	
	protected void handleSave() {

		if (institutionField.isDirty() && institutionField.getOriginalValue() != null) {
			
			// The user changed the institution, so we better warn them about what this means

			final Listener<MessageBoxEvent> confirmListener = new Listener<MessageBoxEvent>() {  
				public void handleEvent(MessageBoxEvent ce) {  
					Button btn = ce.getButtonClicked();
					if ("Yes".equals(btn.getText()))
						endEdit(true);
				}
			};
			
			MessageBox.confirm("Institution Change", 
								"A change to the billing institution will affect the gathering of usage statistics.  Are you sure you want to do this?", 
								confirmListener);
		} else
			endEdit(true);	// No change to institution so just go ahead
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
		return agreement == null || formColumn1.isDirty() || formColumn2.isDirty();
	}
	
	protected void handleDirtyForm() {
		boolean ready = true;
		
		if (institutionField.getSelectedValue() == null) { 
			institutionField.markInvalid("Select an instituion.");
			ready = false;
		} else
			institutionField.clearInvalid();
		if (agreementTypeField.getSelectedValue() == null) {
			agreementTypeField.markInvalid("Select an agreement type.");
			ready = false;
		} else
			agreementTypeField.clearInvalid();
		if (commissionTypeField.getSelectedValue() == null) {
			commissionTypeField.markInvalid("Select a commission code.");
			ready = false;
		} else
			commissionTypeField.clearInvalid();
		
		if (ready)
			saveButton.enable();
		else
			saveButton.disable();
	}
	
	protected void handleCleanForm() {
		saveButton.disable();
	}
	
	/**
	 * Add the toolbar buttons
	 */
	protected void addPanelSwitchTools() {
		
		final int MIN_BUTTON_WIDTH = 80;
		String toggleGroup = "ag" + System.currentTimeMillis();
		
		ToolBar toolBar = new ToolBar();
		toolBar.setAlignment(HorizontalAlignment.CENTER);
		toolBar.setBorders(false);
		toolBar.setSpacing(20);
		toolBar.setToolTip(UiConstants.getQuickTip("Use these buttons to access detailed information for this agreement."));
		
		agreementButton = new ToggleButton("Agreement");
		agreementButton.setMinWidth(MIN_BUTTON_WIDTH);
		agreementButton.setToolTip(UiConstants.getQuickTip("Define and edit the main agreement."));
		IconSupplier.forceIcon(agreementButton, IconSupplier.getAgreementIconName());
		agreementButton.addSelectionListener(new SelectionListener<ButtonEvent>() {  
				@Override
				public void componentSelected(ButtonEvent ce) {
					cards.setActiveItem(agreementCard);
					agreementButton.toggle(true);
				}  
			});
		agreementButton.setToggleGroup(toggleGroup);
		toolBar.add(agreementButton);
		
		termsButton = new ToggleButton("Terms");
		termsButton.setMinWidth(MIN_BUTTON_WIDTH);
		termsButton.setToolTip(UiConstants.getQuickTip("Define and edit product terms for this agreement."));
		IconSupplier.forceIcon(termsButton, IconSupplier.getAgreementTermIconName());
		termsButton.addSelectionListener(new SelectionListener<ButtonEvent>() {  
				@Override
				public void componentSelected(ButtonEvent ce) {
					termsCard.setAgreementId(agreementId);
					termsCard.setAgreement(agreement);
					cards.setActiveItem(termsCard);
					termsButton.toggle(true);
				}  
			});
		termsButton.setToggleGroup(toggleGroup);
		toolBar.add(termsButton);
		
		sitesButton = new ToggleButton("Sites");
		sitesButton.setMinWidth(MIN_BUTTON_WIDTH);
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
		sitesButton.setToggleGroup(toggleGroup);
		toolBar.add(sitesButton);
		
		methodsButton = new ToggleButton("Methods");
		methodsButton.setMinWidth(MIN_BUTTON_WIDTH);
		methodsButton.setToolTip(UiConstants.getQuickTip("Define and edit access methods for this agreement."));
		IconSupplier.forceIcon(methodsButton, IconSupplier.getAccessMethodIconName());
		methodsButton.addSelectionListener(new SelectionListener<ButtonEvent>() {  
				@Override
				public void componentSelected(ButtonEvent ce) {
				//	methodsCard.setAgreementId(agreementId);
					methodsCard.setAgreement(agreement);
					cards.setActiveItem(methodsCard);
					methodsButton.toggle(true);
				}
			});
		methodsButton.setToggleGroup(toggleGroup);
		toolBar.add(methodsButton);
		
		remoteSetupButton = new ToggleButton("Remote Setup");
		remoteSetupButton.setMinWidth(MIN_BUTTON_WIDTH);
		remoteSetupButton.setToolTip(UiConstants.getQuickTip("Define and edit remote setup URLs for this agreement."));
		IconSupplier.forceIcon(remoteSetupButton, IconSupplier.getRemoteIconName());
		remoteSetupButton.addSelectionListener(new SelectionListener<ButtonEvent>() {  
				@Override
				public void componentSelected(ButtonEvent ce) {
				//	remoteSetupCard.setAgreementId(agreementId);
					remoteSetupCard.setAgreement(agreement);
					cards.setActiveItem(remoteSetupCard);
					remoteSetupButton.toggle(true);
				}
			});
		remoteSetupButton.setToggleGroup(toggleGroup);
		toolBar.add(remoteSetupButton);
		
//		remoteButton = new ToggleButton("Remote Setup");
//		remoteButton.setMinWidth(MIN_BUTTON_WIDTH);
//		remoteButton.setToolTip(UiConstants.getQuickTip("Define and edit any remote setup for this agreement."));
//		IconSupplier.forceIcon(remoteButton, IconSupplier.getRemoteIconName());
//		remoteButton.addSelectionListener(new SelectionListener<ButtonEvent>() {  
//				@Override
//				public void componentSelected(ButtonEvent ce) {
//					remoteButton.toggle(true);
//				}
//			});
//		remoteButton.setToggleGroup(toggleGroup);
//		toolBar.add(remoteButton);
		
		contactsButton = new ToggleButton("Contacts");
		contactsButton.setMinWidth(MIN_BUTTON_WIDTH);
		contactsButton.setToolTip(UiConstants.getQuickTip("View, define and edit the contacts for this agreement."));
		IconSupplier.forceIcon(contactsButton, IconSupplier.getContactsIconName());
		contactsButton.addSelectionListener(new SelectionListener<ButtonEvent>() {  
				@Override
				public void componentSelected(ButtonEvent ce) {
					contactsCard.setAgreementId(agreementId);
					contactsCard.setAgreement(agreement);
					contactsCard.setBillToInstitution(billToInstitution);
					cards.setActiveItem(contactsCard);
					contactsButton.toggle(true);
				}
			});
		contactsButton.setToggleGroup(toggleGroup);
		toolBar.add(contactsButton);

		agreementButton.toggle(true);
		enableAgreementButtons(agreementId != -1 && agreement != null);
		
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
	
	protected NotesIconButtonField<String> getNotesButtonField() {
		NotesIconButtonField<String> nibf = new NotesIconButtonField<String>(this) {
			@Override
			public void updateNote(String note) {
				asyncUpdateNote(note);
			}
		};
		nibf.setEmptyNoteText("Click the note icon to add notes for this agreement.");
		return nibf;
	}
	
	protected void enableAgreementButtons(boolean enabled) {
		if (editButton != null) editButton.setEnabled(enabled);
		if (termsButton != null) termsButton.setEnabled(enabled);
		if (sitesButton != null) sitesButton.setEnabled(enabled);
		if (methodsButton != null) methodsButton.setEnabled(enabled);
		if (remoteSetupButton != null) remoteSetupButton.setEnabled(enabled);
		if (contactsButton != null) contactsButton.setEnabled(enabled);
	}
	
	/**
	 * Set an agreement on the form, and load its institution
	 * @param agreement
	 */
	protected void set(AgreementInstance agreement) {
		this.agreement = agreement;
		if (agreement == null) {
			this.agreementId = -1;
			enableAgreementButtons(false);
		} else {
			this.agreementId = agreement.getId();
			enableAgreementButtons(true);
		}
		
//		System.out.println("Set agreement ID to " + agreementId);
//		System.out.println("UCN " + (agreement != null ? agreement.getBillUcn() : " null agreement") );
		
		registerUserCache(agreement, identificationTip);
		setPortletHeading();

		if (agreement == null) {
			MessageBox.alert("Agreement not found.", "The requested agreement was not found.", null);
			clearFormValues();
		} else {
			agreementIdField.setValue(agreement.getIdCheckDigit());
			idTipField.setValue(identificationTip);	

			if (agreement.getNote() != null && agreement.getNote().length() > 0) {
				notesField.setEditMode();
				notesField.setNote(agreement.getNote());
			} else {
				notesField.setAddMode();
				notesField.setNote("");			
			}
			
			if (agreement.getAgreementLinkId() > 0) {
				agreementLinkField.setValue(AgreementLinkInstance.obtainModel(agreement.getAgreementLink()));
				linkTypeDisplay.setValue(agreement.getAgreementLink().getLinkType().getDescription());
				profileFieldSet.collapse();
				linkFieldSet.expand();
			} else {
				agreementLinkField.setValue(AgreementLinkInstance.obtainModel(agreement.getAgreementLink()));
				linkTypeDisplay.setValue("");
				linkFieldSet.collapse();
				profileFieldSet.expand();
			}
			
			currentValueField.setValue(agreement.getCurrentValue());
			
//			ucnDisplay.setValue(agreement.getBillUcn());
//			institutionField.setValue(InstitutionInstance.obtainModel(agreement.getInstitution()));
			
			ListStore<BeanModel> store = termsStore;
			store.removeAll();
			if (agreement.getAgreementTerms() != null)
				for (AgreementTermInstance agreementTerm : agreement.getAgreementTerms()) {
					store.add(getModel(agreementTerm));
				}
			
			addressDisplay.setValue("<i>Loading...</i>");
//			customerTypeDisplay.setValue("");
			
			
			
			statusDisplay.setValue(AppConstants.getStatusDescription(agreement.getStatus()));
			agreementTypeField.setValue(AgreementTypeInstance.obtainModel(agreement.getAgreementType()));
			commissionTypeField.setValue(CommissionTypeInstance.obtainModel(agreement.getCommissionType()));
			deleteReasonField.setValue(DeleteReasonInstance.obtainModel(agreement.getDeleteReason()));
			
			buildingsField.setValue(agreement.getBuildings());
			populationField.setValue(agreement.getPopulation());
			enrollmentField.setValue(agreement.getEnrollment());
			workstationsField.setValue(agreement.getWorkstations());
			
			cards.setActiveItem(agreementCard);
			
			if (agreement.getInstitution() != null) {
				set(agreement.getInstitution());
			} else {
				set(InstitutionInstance.getEmptyInstance());
//				loadInstitution(agreement.getBillUcn());
			}
		}

		updatePresenterLabel();
		updateUserPortlet();	// This is mostly for a "create" so the portlet knows the agreement ID has been set
		setOriginalValues();
//		endEdit(false);
		
		jumpTo();
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

		if (institutionField.getSelectedValue() == null || !billToInstitution.equals(institutionField.getSelectedValue().getBean())) {
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
	
	protected void matchToAgreementLink(AgreementLinkInstance instance) {
//		agreementLinkBinding.bind(AgreementLinkInstance.obtainModel(billToAgreementLink));
		
		if (instance == null || instance.getLinkType() == null) {
			linkTypeDisplay.setValue("");
			return;
		}
		
		linkTypeDisplay.setValue(instance.getLinkType().getDescription());
	}
	
	public void beginEdit() {
		editButton.disable();
		cancelButton.enable();
		enableFields();
	}
	
	public void endEdit(boolean save) {
		cancelButton.disable();
		saveButton.disable();
		disableFields();
		if (save) {
			editButton.disable();	//	Disable this ...let the update enable it when the response arrives
			asyncUpdate();
		} else {
			resetFormValues();
			editButton.enable();
		}
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

	public void setOriginalValues(FormPanel formPanel) {
		FieldFactory.setOriginalValues(formPanel);
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
			if (field != valueNotesCombo)
				field.disable();
		}
		for (Field<?> field : formColumn2.getFields()) {
			if (field != valueNotesCombo)
				field.disable();
		}
	}

	/**
	 * Load the agreement for an ID
	 * @param id
	 */
	protected void loadAgreement(final int id) {
		agrementGetService.getAgreement(id, ALL_TERMS,
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
	
	private int getFieldIntValue(NumberField field) {
		if (field.getValue() == null)
			return 0;
		else
			return field.getValue().intValue();
	}
	
	@Override
	public void fireUserCacheUpdateEvents(UserCacheTarget target) {
		//	Fire an event so any listening portlets can update themselves
		AppEvent appEvent = new AppEvent(AppEvents.AgreementAccess);
		if (target instanceof AgreementInstance)
			appEvent.set( (AgreementInstance) target);
		AppEventBus.getSingleton().fireEvent(AppEvents.AgreementAccess, appEvent);
	}

	public void fireNewAgreementEvent(UserCacheTarget target) {
		//	Fire an event so any listening portlets can update themselves
		AppEvent appEvent = new AppEvent(AppEvents.NewAgreement);
		if (target instanceof AgreementInstance)
			appEvent.set( (AgreementInstance) target);
		AppEventBus.getSingleton().fireEvent(AppEvents.NewAgreement, appEvent);
	}


	protected void asyncUpdate() {
	
		// Set field values from form fields
		
		if (agreement == null) {
			agreement = new AgreementInstance();
			agreement.setNewRecord(true);
		}
		
		agreement.setInstitution( ((InstitutionInstance) institutionField.getSelectedValue().getBean()) );
//		System.out.println("Update agreeement " + agreement.getId() + " for institution " + agreement.getInstitution().getUcn() +  "    / bill UCN " + agreement.getBillUcn());
		agreement.setAgreementType( (AgreementTypeInstance) agreementTypeField.getSelectedValue().getBean()  );
		agreement.setCommissionType( (CommissionTypeInstance) commissionTypeField.getSelectedValue().getBean() );
		if (deleteReasonField.getSelectedValue() == null) {
			agreement.setDeleteReason(DeleteReasonInstance.getEmptyInstance());
			agreement.setDeleteReasonCode("");
		} else
			agreement.setDeleteReason( (DeleteReasonInstance) deleteReasonField.getSelectedValue().getBean() );
		
		if (agreementLinkField.getValue() != null)	//	&& linkFieldSet.isExpanded()  <-- this was for a checkbox to delete the link, taken out
			agreement.setAgreementLink( (AgreementLinkInstance) agreementLinkField.getValue().getBean());
		else
			if (agreement.getAgreementLinkId() > 0)
				agreement.setAgreementLinkId( - agreement.getAgreementLinkId() );	//	"Delete" by negating
		
		agreement.setBuildings(getFieldIntValue(buildingsField));
		agreement.setPopulation(getFieldIntValue(populationField));
		agreement.setEnrollment(getFieldIntValue(enrollmentField));
		agreement.setWorkstations(getFieldIntValue(workstationsField));
		
		if (agreement.isNewRecord())
			agreement.setNote(notesField.getNote());
		else
			agreement.setNote(null);	//	This will keep the note from being updated by this call
	
		//	Issue the asynchronous update request and plan on handling the response
		updateAgreementService.updateAgreement(agreement,
				new AsyncCallback<UpdateResponse<AgreementInstance>>() {
					public void onFailure(Throwable caught) {
						// Show the RPC error message to the user
						if (caught instanceof IllegalArgumentException)
							MessageBox.alert("Alert", caught.getMessage(), null);
						else {
							MessageBox.alert("Alert", "Agreement update failed unexpectedly.", null);
							System.out.println(caught.getClass().getName());
							System.out.println(caught.getMessage());
						}
						editButton.enable();
					}

					public void onSuccess(UpdateResponse<AgreementInstance> updateResponse) {
						AgreementInstance updatedAgreement = (AgreementInstance) updateResponse.getInstance();
						if (updatedAgreement.isNewRecord()) {
							updatedAgreement.setNewRecord(false);
							fireNewAgreementEvent(updatedAgreement);
							if (updatedAgreement.getInstitution() == null)
								identificationTip = "Agreement created " + new Date();
							else
								identificationTip = "Agreement created for " + updatedAgreement.getInstitution().getInstitutionName();
						}
						agreement.setNewRecord(false);
						set(updatedAgreement);
						fireUserCacheUpdateEvents(updatedAgreement);
				//		enableAgreementButtons(true);
				}
			});
	}

	protected void asyncUpdateNote(String note) {
	
		// Set field values from form fields
		
		if (agreement == null || agreement.isNewRecord()) {
			return;
		}
		
		agreement.setNote(note);
	
		//	Issue the asynchronous update request and plan on handling the response
		updateAgreementNoteService.updateAgreementNote(agreement,
				new AsyncCallback<UpdateResponse<AgreementInstance>>() {
					public void onFailure(Throwable caught) {
						// Show the RPC error message to the user
						if (caught instanceof IllegalArgumentException)
							MessageBox.alert("Alert", caught.getMessage(), null);
						else {
							MessageBox.alert("Alert", "Agreement note update failed unexpectedly.", null);
							System.out.println(caught.getClass().getName());
							System.out.println(caught.getMessage());
						}
						notesField.unlockNote();
					}

					public void onSuccess(UpdateResponse<AgreementInstance> updateResponse) {
						AgreementInstance updatedAgreement = (AgreementInstance) updateResponse.getInstance();
						if (!notesField.getNote().equals(updatedAgreement.getNote())) {
							notesField.setNote(updatedAgreement.getNote());
							agreement.setNote(updatedAgreement.getNote());
						}
						notesField.unlockNote();
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

	@Override
	public void setAppPortletProvider(AppPortletProvider portletProvider) {
		this.portletProvider = portletProvider;
	}
	
	@Override
	public String getShortPortletName() {
		if (agreementId > 0)
			return "#" + AppConstants.appendCheckDigit(agreementId);
		return "Create Agreement";
	}
	
	@Override
	public boolean allowDuplicatePortlets() {
		//	Not allowed for a particular agreement
		if (agreementId > 0)
			return false;
		//	Allowed for "create new"
		return true;
	}
	
	@Override
	public String getPortletIdentity() {
		return getPortletIdentity(agreementId);
	}
	
	public static String getPortletIdentity(int agreementId) {
		if (agreementId <= 0)
			return AgreementPortlet.class.getName();
		return AgreementPortlet.class.getName() + ":" + agreementId;
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

	public AuthMethodInstance getJumpToMethod() {
		return jumpToMethod;
	}

	public void setJumpToMethod(AuthMethodInstance jumpToMethod) {
		this.jumpToMethod = jumpToMethod;
		jumpTo();	//	This only does something if the agreement is already loaded
	}

	public RemoteSetupUrlInstance getJumpToRemoteSetupUrl() {
		return jumpToRemoteSetupUrl;
	}

	public void setJumpToRemoteSetupUrl(RemoteSetupUrlInstance jumpToRemoteSetupUrl) {
		this.jumpToRemoteSetupUrl = jumpToRemoteSetupUrl;
		jumpTo();
	}

	public void jumpTo() {
		if (jumpToMethod != null && agreement != null && agreement.getId() == jumpToMethod.getAgreementId()) {
			methodsCard.setAgreement(agreement);
			methodsCard.setFocusInstance(jumpToMethod);
			cards.setActiveItem(methodsCard);
			methodsButton.toggle(true);
			
			jumpToMethod = null;	/// Once we've done this, don't do it any more
		} else if (jumpToRemoteSetupUrl != null && agreement != null && agreement.getId() == jumpToRemoteSetupUrl.getAgreementId()) {
			remoteSetupCard.setAgreement(agreement);
			remoteSetupCard.setFocusInstance(jumpToRemoteSetupUrl);
			cards.setActiveItem(remoteSetupCard);
			remoteSetupButton.toggle(true);
			
			jumpToMethod = null;	/// Once we've done this, don't do it any more
		}
	}
}