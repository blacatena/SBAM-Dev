package com.scholastic.sbam.client.uiobjects;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.FormPanel.LabelAlign;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.RowExpander;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.scholastic.sbam.client.services.AgreementTermListService;
import com.scholastic.sbam.client.services.AgreementTermListServiceAsync;
import com.scholastic.sbam.client.util.UiConstants;
import com.scholastic.sbam.shared.objects.AgreementTermInstance;
import com.scholastic.sbam.shared.util.AppConstants;

public class AgreementTermsCard extends FormAndGridPanel<AgreementTermInstance> {
	
	protected final AgreementTermListServiceAsync agrementTermListService = GWT.create(AgreementTermListService.class);
	
//	protected AgreementTermsCard	mainContainer	= this;
//	
//	protected FormPanel				termFormPanel;
//	protected ContentPanel			termsGridPanel;
//	protected ListStore<ModelData>	termsStore;
//	protected Grid<ModelData>		termsGrid;
	
	protected RowExpander			noteExpander;
	
	protected LabelField			agreementIdDisplay	= new LabelField();;
	protected LabelField			productDisplay		= new LabelField();;
	
//	protected int					agreementId;
//	protected AgreementTermInstance	agreementTerm;

//	@Override
//	protected void onRender(Element parent, int index) {
//		super.onRender(parent, index);
//		
//		FormData formData = new FormData("100%");
//	
//		setLayout(new FlowLayout());
//		
//		termFormPanel = new FormPanel() {
//			/*
//			 * This panel has to take care of telling the grid panel to grow or shrink as it renders/resizes/expands/collapses
//			 * (non-Javadoc)
//			 */
//			
//			@Override
//			protected void onRender(Element parent, int index) {
//				super.onRender(parent, index);
//				if (termsGridPanel != null && termsGridPanel.isRendered()) {
//					termsGridPanel.setHeight(mainContainer.getHeight(true) - getHeight());
//				}
//			}
//			
//			@Override
//			public void onResize(int width, int height) {
//				super.onResize(width, height);
//				if (termsGridPanel != null && termsGridPanel.isRendered()) {
//					termsGridPanel.setHeight(mainContainer.getHeight(true) - getHeight());
//				}
//			}
//			
//			@Override
//			public void onCollapse() {
//				super.onCollapse();
//				// Resize in anticipation of what it will be after collapse
//				if (termsGridPanel != null && termsGridPanel.isRendered()) {
//					if (isHeaderVisible())
//						termsGridPanel.setHeight(mainContainer.getHeight(true) - getHeader().getOffsetHeight());
//					else
//						termsGridPanel.setHeight(mainContainer.getHeight(true));
//				}
//			}
//			
//			@Override
//			public void afterCollapse() {
//				super.afterCollapse();
//				if (termsGridPanel != null && termsGridPanel.isRendered()) {
//					termsGridPanel.setHeight(mainContainer.getHeight(true) - getHeight());
//				}
//			}
//			
//			@Override
//			public void afterExpand() {
//				super.afterExpand();
//				if (termsGridPanel != null && termsGridPanel.isRendered()) {
//					termsGridPanel.setHeight(mainContainer.getHeight(true) - getHeight());
//				}
//			}
//			
//		};
//		termFormPanel.setPadding(20);
//		termFormPanel.setFrame(true);
//		termFormPanel.setHeaderVisible(true);
//		termFormPanel.setHeading("Product Terms");
//		termFormPanel.setBodyBorder(true);
//		termFormPanel.setBorders(false);
//		termFormPanel.setBodyStyleName("subtle-form");
//		termFormPanel.setButtonAlign(HorizontalAlignment.CENTER);
//		termFormPanel.setLabelAlign(LabelAlign.RIGHT);
//		termFormPanel.setLabelWidth(100);
//		termFormPanel.setCollapsible(true);
////		termFormPanel.setHeight(300);
//		if (agreementTerm == null)
//			termFormPanel.collapse();
//		
////		agreementIdDisplay = new LabelField();  
//		agreementIdDisplay.setFieldLabel("Agreement # :");
//		termFormPanel.add(agreementIdDisplay, formData);
//		
////		productDisplay = new LabelField();  
//		productDisplay.setFieldLabel("Product :");
//		termFormPanel.add(productDisplay, formData);
//		
////		Button doneButton = new Button("Done");
////		doneButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
////			@Override
////			public void componentSelected(ButtonEvent ce) {
////				termFormPanel.collapse();
////			}
////			
////		});
////		
////		termFormPanel.addButton(doneButton);
//		
//		termsGridPanel = new ContentPanel() {
//			@Override
//			protected void onRender(Element parent, int index) {
//				super.onRender(parent, index);
//				// This is key: it fits the grid, initially, to the height of the parent container
//				if (termFormPanel != null && termFormPanel.isRendered() && termFormPanel.isHeaderVisible())
//					termsGridPanel.setHeight(mainContainer.getHeight(true) - termFormPanel.getHeader().getOffsetHeight());
//				else
//					termsGridPanel.setHeight(mainContainer.getHeight(true));
////				setHeight(parent.getClientHeight());
//			}
//		};
//		termsGridPanel.setLayout(new FitLayout());
//		termsGridPanel.setFrame(true);
//		termsGridPanel.setHeaderVisible(false);
//		termsGridPanel.setBodyBorder(true);
//		termsGridPanel.setBorders(false);
//		termsGridPanel.setBodyStyleName("subtle-form");
//		termsGridPanel.setButtonAlign(HorizontalAlignment.CENTER);
//		
//		termsGridPanel.add(getAgreementTermsGrid(formData));
//		
//		add(termFormPanel);
//		add(termsGridPanel);
//		
//		if (agreementId != 0) {
//			loadAgreementTerms(agreementId);
//		}
//	}
	
//	@Override
//	public void onResize(int width, int height) {
//		super.onResize(width, height);
//		if (termsGridPanel != null && termsGrid != null && termsGridPanel.isRendered() && termsGrid.isRendered() && termsGrid.getHeight() == 0) {
//			System.out.println("Container : set height of grid panel");
//			if (termFormPanel != null && termFormPanel.isRendered() && termFormPanel.isHeaderVisible())
//				termsGridPanel.setHeight(mainContainer.getHeight(true) - termFormPanel.getHeader().getOffsetHeight());
//			else
//				termsGridPanel.setHeight(mainContainer.getHeight(true));
////			termsGridPanel.setHeight(height);
//		}
//	}

//	public void setAgreementTerm(AgreementTermInstance agreementTerm) {
//		this.agreementTerm = agreementTerm;
//		// No agreement term means clear the form
//		if (agreementTerm == null) {
//			clearAgreementTerm();
//			return;
//		}
//		
//		// Set the form fields
//		agreementIdDisplay.setValue(agreementTerm.getAgreementId());
//		productDisplay.setValue(agreementTerm.getProductDescription());
//		
//		if (termFormPanel != null)
//			termFormPanel.expand();
//	}
	
//	public void clearAgreementTerm() {
//		agreementIdDisplay.clear();
//		productDisplay.clear();
//		termFormPanel.collapse();
//	}
	
//	protected Grid<ModelData> getAgreementTermsGrid(FormData formData) {
//		List<ColumnConfig> columns = new ArrayList<ColumnConfig>();
//		
////		columns.add(getDisplayColumn("productDescription",		"Product",					200,
////					"This is the product ordered."));
////		columns.add(getDisplayColumn("startDate",				"Start",					80,		true, UiConstants.APP_DATE_TIME_FORMAT,
////					"This is the service start date for a product term."));
////		columns.add(getDisplayColumn("endDate",					"End",						80,		true, UiConstants.APP_DATE_TIME_FORMAT,
////					"This is the service end date for a product term."));
////		columns.add(getDisplayColumn("terminateDate",			"Terminate",				80,		true, UiConstants.APP_DATE_TIME_FORMAT,
////					"This is the actual service termination date for a product term."));
////		columns.add(getDisplayColumn("dollarValue",				"Value",					80,		true, NumberFormat.getCurrencyFormat(UiConstants.US_DOLLARS),
////					"This is the value of the service."));
////		columns.add(getDisplayColumn("termTypeDescription",		"Type",						80,
////					"This is the type of service."));
////
////		RowExpander expander = getNoteExpander();
////		columns.add(expander);
//		
//		ColumnModel cm = new ColumnModel(columns);  
//
//		termsStore = new ListStore<ModelData>();
//		
//		termsGrid = new Grid<ModelData>(termsStore, cm); 
////		termsGrid.addPlugin(noteExpander);
//		termsGrid.setBorders(true);  
////		termsGrid.setAutoExpandColumn("productDescription");  
//		termsGrid.setLoadMask(true);
////		termsGrid.setHeight(this.getHeight());
//		termsGrid.setStripeRows(true);
//		termsGrid.setColumnLines(false);
//		termsGrid.setHideHeaders(false);
////		termsGrid.setWidth(cm.getTotalWidth() + 20);
//		
//		addRowListener(termsGrid);
//		
//		//	Switch to the display card when a row is selected
//		termsGrid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
//
//		return termsGrid;
//	}
	
//	@Override
//	public void onRowSelected(BeanModel instance) {
//		setAgreementTerm((AgreementTermInstance) instance.getBean());
////		termFormPanel.expand();
////		termsGrid.getSelectionModel().deselectAll();
//	}

//	public void loadAgreementTerms(final int id) {
//		if (termsStore == null)
//			return;
//		if (id == 0) {
//			termsStore.removeAll();
//			return;
//		}
//		agrementTermListService.getAgreementTerms(id, AppConstants.STATUS_DELETED,
//				new AsyncCallback<List<AgreementTermInstance>>() {
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
//					public void onSuccess(List<AgreementTermInstance> terms) {
//						termsStore.removeAll();
//						for (AgreementTermInstance agreementTerm : terms) {
//							termsStore.add(getModel(agreementTerm));
//						}
//					}
//			});
//	}


//	public int getAgreementId() {
//		return agreementId;
//	}
//
//
//	public void setAgreementId(int agreementId) {
//		if (agreementId != this.agreementId) {
//			this.agreementId = agreementId;
//			loadAgreementTerms(agreementId);
//		}
//	}
	
	public int getAgreementId() {
		return getFocusId();
	}
	
	public void setAgreementId(int agreementId) {
		setFocusId(agreementId);
	}
	
	public void setAgreementTerm(AgreementTermInstance instance) {
		setFocusInstance(instance);
	}

	@Override
	public void awaken() {
	}

	@Override
	public void sleep() {
	}
	
	@Override
	public void addGridPlugins(Grid<ModelData> grid) {
		grid.addPlugin(noteExpander);
	}
	
	/**
	 * Override to set any further grid atrributes, such as the autoExpandColumn.
	 * @param grid
	 */
	@Override
	public void setGridAttributes(Grid<ModelData> grid) {
		grid.setAutoExpandColumn("productDescription");  	
	}

	@Override
	public void addGridColumns(List<ColumnConfig> columns) {
		columns.add(getDisplayColumn("productDescription",		"Product",					200,
					"This is the product ordered."));
		columns.add(getDisplayColumn("startDate",				"Start",					80,		true, UiConstants.APP_DATE_TIME_FORMAT,
					"This is the service start date for a product term."));
		columns.add(getDisplayColumn("endDate",					"End",						80,		true, UiConstants.APP_DATE_TIME_FORMAT,
					"This is the service end date for a product term."));
		columns.add(getDisplayColumn("terminateDate",			"Terminate",				80,		true, UiConstants.APP_DATE_TIME_FORMAT,
					"This is the actual service termination date for a product term."));
		columns.add(getDisplayColumn("dollarValue",				"Value",					80,		true, NumberFormat.getCurrencyFormat(UiConstants.US_DOLLARS),
					"This is the value of the service."));
		columns.add(getDisplayColumn("termTypeDescription",		"Type",						80,
					"This is the type of service."));
	
		noteExpander = getNoteExpander();
		columns.add(noteExpander);
	}

	@Override
	public void setFormFieldValues(AgreementTermInstance instance) {
		agreementIdDisplay.setValue(instance.getAgreementId());
		productDisplay.setValue(instance.getProductDescription());
	}

	@Override
	public void clearFormFieldValues() {
		agreementIdDisplay.clear();
		productDisplay.clear();
	}

	@Override
	protected void executeLoader(int id,
			AsyncCallback<List<AgreementTermInstance>> callback) {
		agrementTermListService.getAgreementTerms(id, AppConstants.STATUS_DELETED,callback);
	}

	@Override
	protected void addFormFields(FormPanel panel, FormData formData) {
//		agreementIdDisplay = new LabelField();  
		agreementIdDisplay.setFieldLabel("Agreement # :");
		panel.add(agreementIdDisplay, formData);
		
//		productDisplay = new LabelField();  
		productDisplay.setFieldLabel("Product :");
		panel.add(productDisplay, formData);
	}
	
	
}
