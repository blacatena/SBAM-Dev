package com.scholastic.sbam.client.uiobjects;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.binding.FormBinding;
import com.extjs.gxt.ui.client.data.BaseListLoader;
import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.BeanModelFactory;
import com.extjs.gxt.ui.client.data.BeanModelLookup;
import com.extjs.gxt.ui.client.data.BeanModelReader;
import com.extjs.gxt.ui.client.data.ListLoadResult;
import com.extjs.gxt.ui.client.data.ListLoader;
import com.extjs.gxt.ui.client.data.LoadConfig;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.event.BindingEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.FormButtonBinding;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.HtmlEditor;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.grid.CellEditor;
import com.extjs.gxt.ui.client.widget.grid.CheckColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.filters.DateFilter;
import com.extjs.gxt.ui.client.widget.grid.filters.GridFilters;
import com.extjs.gxt.ui.client.widget.grid.filters.NumericFilter;
import com.extjs.gxt.ui.client.widget.grid.filters.StringFilter;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.scholastic.sbam.client.services.UpdateWelcomeMessageService;
import com.scholastic.sbam.client.services.UpdateWelcomeMessageServiceAsync;
import com.scholastic.sbam.client.services.WelcomeMessageListService;
import com.scholastic.sbam.client.services.WelcomeMessageListServiceAsync;
import com.scholastic.sbam.client.util.IconSupplier;
import com.scholastic.sbam.shared.objects.UpdateResponse;
import com.scholastic.sbam.shared.objects.WelcomeMessageInstance;
import com.scholastic.sbam.shared.util.AppConstants;
import com.scholastic.sbam.shared.validation.NameValidator;

public class WelcomeMessageEditGrid extends LayoutContainer {

	private final WelcomeMessageListServiceAsync welcomeMessageListService = GWT.create(WelcomeMessageListService.class);
	private final UpdateWelcomeMessageServiceAsync updateWelcomeMessageService = GWT.create(UpdateWelcomeMessageService.class);

	protected	FormBinding								formBindings;
	protected	ListLoader<ListLoadResult<ModelData>>	loader;
	protected	ListStore<BeanModel>					store;
	protected	Grid<BeanModel>							grid;
	protected	ContentPanel 							cp;
	protected	FormPanel 								panel;
	
	protected	TextField<String>						id;
	protected	TextField<String>						title;
	protected	TextField<String>						posted;
	protected	DateField								expires;
	protected	CheckBox								active;
	protected	HtmlEditor								content;
	
	protected	Button									saveButton;
	protected	Button									startNewButton;
	protected	Button									refreshThisButton;
	protected	Button									refreshAllButton;
	protected	Button									deleteButton;
	protected	Button									cancelButton;
	
	public WelcomeMessageEditGrid() {
	}
	
	@Override    
	protected void onRender(Element parent, int index) {    
	    super.onRender(parent, index);
	    setStyleAttribute("margin", "10px");
	
	    cp = new ContentPanel();
	
	    cp.setHeading("Welcome Messages");
	    cp.setFrame(true);
	    cp.setSize(1150, 600);
	    cp.setLayout(new RowLayout(Orientation.HORIZONTAL));
	    IconSupplier.setIcon(cp, IconSupplier.getMessagesIconName());
		
		// loader and store  
		loader = getLoader();
		store = new ListStore<BeanModel>(loader);
	    store.setMonitorChanges(true);   
		
		loader.load();
	
	    Grid<BeanModel> grid = createGrid();
	    grid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
	    grid.getSelectionModel().addListener(Events.SelectionChange,    
	            new Listener<SelectionChangedEvent<BeanModel>>() { 
	    			@Override
	                public void handleEvent(SelectionChangedEvent<BeanModel> be) {    
	                    if (be.getSelection().size() > 0) {    
	                        formBindings.bind((ModelData) be.getSelection().get(0));
	                    } else {    
	                        formBindings.unbind();
	                    }
	                }
	            });
	    cp.add(grid, new RowData(550, 1));
	
	    panel = createForm();
	    
	    formBindings = new FormBinding(panel, true);
	    formBindings.addListener(Events.Bind, new Listener<BindingEvent>() {

				@Override
				public void handleEvent(BindingEvent be) {
					enableBindingButtons();
				}
	    	
	    	});
	    formBindings.addListener(Events.UnBind, new Listener<BindingEvent>() {
	
				@Override
				public void handleEvent(BindingEvent be) {
					disableNoBindingButtons();
				}
	    	
	    	});
	
	    cp.add(panel, new RowData(595, 1));
	
	    add(cp);
	}
	
	/**
	 * Create a form panel to edit welcome messages.
	 * @return
	 */
	private FormPanel createForm() {    
		FormPanel panel = new FormPanel();
	    panel.setHeaderVisible(false);
	
	    id = new TextField<String>();
	    id.setName("id");
	    id.setFieldLabel("ID");
	    id.setReadOnly(true);
	    id.setWidth(50);
	    panel.add(id);
	
	    title = new TextField<String>();
	    title.setName("title");
	    title.setFieldLabel("Title");
	    title.setValidator(new NameValidator(10, 0));
	    title.setMessageTarget("tooltip");
	    panel.add(title, new FormData("100%"));
	
	    posted = new TextField<String>();
	    posted.setName("postDate");
	    posted.setFieldLabel("Posted");
	    posted.setReadOnly(true);
	    panel.add(posted);   
	
	    expires = new DateField();
	    expires.setName("expireDate");
	    expires.setFieldLabel("Expires");
	    expires.setMinValue(new Date());
	    expires.setMinLength(0);
	    expires.setMessageTarget("tooltip");
	    panel.add(expires);   
	
	    active = new CheckBox();
	    active.setName("active");
	    active.setFieldLabel("Active?");
	    panel.add(active);
	    
	    content = new HtmlEditor();
	    content.setName("content");
	    content.setFieldLabel("Message");
	    content.setHeight(380);
	    panel.add(content, new FormData("100%"));
	    
	    addFormButtons(panel);
	
	    return panel;
	}
	
	/**
	 * Add Save, New and Refresh buttons to the form panel.
	 * @param panel
	 */
	private void addFormButtons(FormPanel panel) { 
		
		saveButton = new Button("Save");
		
		saveButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			   
			@Override
			public void componentSelected(ButtonEvent ce) {
				if (formBindings.getModel() == null  && title.getValue() != null && !title.getValue().equals("New"))
					refuseAction(ce, "Nothing is selected to be saved.");
				else
					asyncUpdate(false);
			}
		});
		
		startNewButton = new Button("Start New");
		
		startNewButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			   
			@SuppressWarnings("deprecation")
			@Override
			public void componentSelected(ButtonEvent ce) {
				Date defaultExpires = new Date();
				defaultExpires.setDate(defaultExpires.getDate() + 1);
				
				formBindings.unbind();
				id.setValue("New");
				title.setValue("");
				posted.setValue("");
				expires.setValue(defaultExpires);
				content.setValue("");
				title.focus();
			}  
		 
		}); 
		
		deleteButton = new Button("Delete");
		
		deleteButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			   
			@Override
			public void componentSelected(ButtonEvent ce) {

				// This listener does the delete if confirmed by the user
				final Listener<MessageBoxEvent> confirmDelete = new Listener<MessageBoxEvent>() {  
					public void handleEvent(MessageBoxEvent ce) {
						Button btn = ce.getButtonClicked();
						if ("Yes".equals(btn.getText()))
							asyncUpdate(true);
					}  
				};

				//	Refuse to delete nothing, or else ask for confirmation and delete if confirmed
				if (formBindings.getModel() == null)
					refuseAction(ce, "No message is selected to be deleted.");
				else
					MessageBox.confirm("Confirm Delete", "Are you sure you want to delete this welcome message?", confirmDelete);
			}
		});
		
		cancelButton = new Button("Delete");
		
		cancelButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			   
			@Override
			public void componentSelected(ButtonEvent ce) {
				cancelChanges();
			}
		});
		
		refreshThisButton = new Button("Refresh This");
		
		refreshThisButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			   
			@Override
			public void componentSelected(ButtonEvent ce) {
				getFormPanel().reset();
			}
		});
		
		refreshAllButton = new Button("Refresh All");
		
		refreshAllButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			   
			@Override
			public void componentSelected(ButtonEvent ce) {
				if (loader != null)
					loader.load();
			}
		});
 
	    panel.setButtonAlign(HorizontalAlignment.CENTER);
	    panel.addButton(saveButton); 
	    panel.addButton(startNewButton);
	    panel.addButton(deleteButton); 
	    panel.addButton(refreshThisButton); 
	    panel.addButton(refreshAllButton);
		
	    // Set initial states
	    saveButton.disable();
	    deleteButton.disable();
	    refreshThisButton.disable();	    
	    
	    //	Add bindings for button behaviors
	    
	    FormButtonBinding binding = new FormButtonBinding(panel);  
	    binding.addButton(saveButton);
	}
	
	private void disableNoBindingButtons() {
	//	if (saveButton != null) 
	//		saveButton.disable();
		if (deleteButton != null) 
			deleteButton.disable();
		if (refreshThisButton != null) 
			refreshThisButton.disable();
	}
	
	private void enableBindingButtons() {
	//	if (saveButton != null) 
	//		saveButton.enable();
		if (deleteButton != null) 
			deleteButton.enable();
		if (refreshThisButton != null) 
			refreshThisButton.enable();
	}
	
	private void refuseAction(final ButtonEvent be, final String message) {
//		be.getButton().setToolTip(message);
//		be.getButton().getToolTip().disable();
//		be.getButton().getToolTip().show();
		MessageBox.alert("Sorry", message, null);
	}
	
	/**
	 * Create the grid to display welcome messages.
	 * @return
	 */
	private Grid<BeanModel> createGrid() {  
	
//	    GridCellRenderer<BeanModel> change = new GridCellRenderer<BeanModel>() {    
//	
//	        public String render(BeanModel model, String property, ColumnData config, int rowIndex,    
//	                int colIndex, ListStore<BeanModel> store, Grid<BeanModel> grid) {    
//	            double val = (Double) model.get(property);
//	            String style = val < 0 ? "red" : "green";
//	            return "<span style='color:" + style + "'>" + number.format(val) + "</span>";
//	        }
//	    };
//	
//	    GridCellRenderer<BeanModel> gridNumber = new GridCellRenderer<BeanModel>() {    
//	        public String render(BeanModel model, String property, ColumnData config, int rowIndex,    
//	                int colIndex, ListStore<BeanModel> store, Grid<BeanModel> grid) {    
//	            return numberRenderer.render(null, property, model.get(property));
//	        }
//	    };
	
	    List<ColumnConfig> configs = new ArrayList<ColumnConfig>();
	
	    ColumnConfig column = new ColumnConfig();
	    column.setId("id");
	    column.setHeader("ID");
	    column.setWidth(50);
	    configs.add(column);
	
	    column = new ColumnConfig();
	    column.setId("title");
	    column.setHeader("Title");
	    column.setWidth(200);
	    configs.add(column);
	
//	    column = new ColumnConfig();
//	    column.setId("content");
//	    column.setHeader("Message");
//	    column.setWidth(300);
//	    column.setHidden(true);
//	    configs.add(column);
	
	    column = new ColumnConfig("postDate", "Posted", 120);
	    column.setHeader("Posted");
	    column.setAlignment(HorizontalAlignment.RIGHT);
	    column.setDateTimeFormat(DateTimeFormat.getFormat("E, MMM dd, yyyy"));
	    configs.add(column);
	
	    column = new ColumnConfig("expireDate", "Expires", 120);
	    column.setHeader("Expires");
	    column.setAlignment(HorizontalAlignment.RIGHT);
	    column.setDateTimeFormat(DateTimeFormat.getFormat("E, MMM dd, yyyy"));
	    configs.add(column);
	    
		CheckColumnConfig checkColumn = new CheckColumnConfig("active", "Active?", 55);
		CellEditor checkBoxEditor = new CellEditor(new CheckBox());
		checkColumn.setEditor(checkBoxEditor);
		configs.add(checkColumn);
	
	    ColumnModel cm = new ColumnModel(configs);
	
	    Grid<BeanModel> grid = new Grid<BeanModel>(store, cm);
	    grid.getView().setEmptyText("");
	    grid.setBorders(false);
//	    grid.setAutoExpandColumn("title");
	    grid.setBorders(true);
	    grid.setColumnReordering(true);
	    grid.setStripeRows(true);
	    
	    addFilters(grid);
	
	    return grid;
	}
	
	private void addFilters(Grid<BeanModel> grid) {
		GridFilters filters = new GridFilters();  
		filters.setLocal(true);

		filters.addFilter(new NumericFilter("ID"));
		filters.addFilter(new StringFilter("title"));
		filters.addFilter(new StringFilter("content"));
		filters.addFilter(new DateFilter("postDate"));
		filters.addFilter(new DateFilter("expireDate"));
		
		grid.addPlugin(filters);
	}
	
	protected ListLoader<ListLoadResult<ModelData>> getLoader() {
		// proxy and reader  
		RpcProxy<List<WelcomeMessageInstance>> proxy = new RpcProxy<List<WelcomeMessageInstance>>() {  
			@Override  
			public void load(Object loadConfig, final AsyncCallback<List<WelcomeMessageInstance>> callback) {
		    	
				// This could be as simple as calling welcomeMessageListService.getUsers and passing the callback
				// Instead, here the callback is overridden so that it can catch errors and alert the users.  Then the original callback is told of the failure.
				// On success, the original callback is just passed the onSuccess message, and the response (the list).
				
				AsyncCallback<List<WelcomeMessageInstance>> myCallback = new AsyncCallback<List<WelcomeMessageInstance>>() {
					public void onFailure(Throwable caught) {
						// Show the RPC error message to the user
						if (caught instanceof IllegalArgumentException)
							MessageBox.alert("Alert", caught.getMessage(), null);
						else {
							MessageBox.alert("Alert", "Welcome message load failed unexpectedly.", null);
							System.out.println(caught.getClass().getName());
							System.out.println(caught.getMessage());
						}
						callback.onFailure(caught);
					}

					public void onSuccess(List<WelcomeMessageInstance> list) {
						callback.onSuccess(list);
					}
				};
				
				asyncLoad(loadConfig, myCallback);
				
		    }  
		};
		BeanModelReader reader = new BeanModelReader();
		
		// loader and store  
		ListLoader<ListLoadResult<ModelData>> loader = new BaseListLoader<ListLoadResult<ModelData>>(proxy, reader);
		return loader;
	}
	
	protected void cancelChanges() {
		formBindings.clear();
		formBindings.unbind();
	}
	
	protected void asyncLoad(Object loadConfig, AsyncCallback<List<WelcomeMessageInstance>> callback) {
		LoadConfig myLoadConfig = null;
		if (loadConfig instanceof LoadConfig)
			myLoadConfig = (LoadConfig) loadConfig;
		welcomeMessageListService.getWelcomeMessageList(myLoadConfig, callback);
	}
	
	protected void asyncUpdate(boolean delete) {
	//	System.out.println("Before update: " + targetBeanModel.getProperties());
		WelcomeMessageInstance updateInstance = new WelcomeMessageInstance();
		updateInstance.setId(-1);
		if (id.getValue() != null && id.getValue().length() > 0 && !id.getValue().equals("New"))
			updateInstance.setId(Integer.parseInt(id.getValue()));
		else
			updateInstance.setNewRecord(true);
		if (delete) {
			updateInstance.setStatus(AppConstants.STATUS_DELETED);
		} else {
			updateInstance.setTitle(title.getValue());
			updateInstance.setContent(content.getValue());
			updateInstance.setExpireDate(expires.getValue());
			updateInstance.setActive(active.getValue());
		}
		
		updateWelcomeMessageService.updateWelcomeMessage(updateInstance,
				new AsyncCallback<UpdateResponse<WelcomeMessageInstance>>() {
					public void onFailure(Throwable caught) {
						// Show the RPC error message to the user
						if (caught instanceof IllegalArgumentException)
							MessageBox.alert("Alert", caught.getMessage(), null);
						else {
							MessageBox.alert("Alert", "Welcome Message update failed unexpectedly.", null);
							System.out.println(caught.getClass().getName());
							System.out.println(caught.getMessage());
						}
					}

					public void onSuccess(UpdateResponse<WelcomeMessageInstance> updateResponse) {
						WelcomeMessageInstance updatedWelcomeMessage = (WelcomeMessageInstance) updateResponse.getInstance();
						BeanModelFactory factory = BeanModelLookup.get().getFactory(updatedWelcomeMessage.getClass());
						BeanModel model = factory.createModel(updatedWelcomeMessage);
						// If this welcome message is newly created, back-populate the grid
						if (updateResponse.isNewCreated()) {
							store.add(model);
						} else if (updatedWelcomeMessage.getStatus() == AppConstants.STATUS_DELETED) {
							store.remove(model);
						}
						formBindings.unbind();
						panel.clear();
				}
			});
	}

	public WelcomeMessageListServiceAsync getWelcomeMessageListService() {
		return welcomeMessageListService;
	}

	public UpdateWelcomeMessageServiceAsync getUpdateWelcomeMessageService() {
		return updateWelcomeMessageService;
	}

	public FormBinding getFormBindings() {
		return formBindings;
	}

	public ListStore<BeanModel> getStore() {
		return store;
	}

	public Grid<BeanModel> getGrid() {
		return grid;
	}

	public ContentPanel getContentPanel() {
		return cp;
	}

	public FormPanel getFormPanel() {
		return panel;
	}

}
