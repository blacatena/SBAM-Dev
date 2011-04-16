package com.scholastic.sbam.client.uiobjects.uiadmin;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.Style.SelectionMode;
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
import com.extjs.gxt.ui.client.event.BaseEvent;
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
import com.extjs.gxt.ui.client.widget.form.CheckBoxGroup;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.HtmlEditor;
import com.extjs.gxt.ui.client.widget.form.NumberField;
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
import com.extjs.gxt.ui.client.widget.layout.CenterLayout;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.scholastic.sbam.client.services.UpdateWelcomeMessageService;
import com.scholastic.sbam.client.services.UpdateWelcomeMessageServiceAsync;
import com.scholastic.sbam.client.services.WelcomeMessageListService;
import com.scholastic.sbam.client.services.WelcomeMessageListServiceAsync;
import com.scholastic.sbam.client.uiobjects.foundation.AppSleeper;
import com.scholastic.sbam.client.util.IconSupplier;
import com.scholastic.sbam.shared.objects.UpdateResponse;
import com.scholastic.sbam.shared.objects.WelcomeMessageInstance;
import com.scholastic.sbam.shared.util.AppConstants;

public class WelcomeMessageEditGrid extends LayoutContainer implements AppSleeper {

	private final WelcomeMessageListServiceAsync welcomeMessageListService = GWT.create(WelcomeMessageListService.class);
	private final UpdateWelcomeMessageServiceAsync updateWelcomeMessageService = GWT.create(UpdateWelcomeMessageService.class);

	protected	ListLoader<ListLoadResult<ModelData>>	loader;
	protected	ListStore<BeanModel>					store;
	protected	Grid<BeanModel>							grid;
	protected	ContentPanel 							cp;
	protected	FormPanel 								panel;
	
	protected	NumberField								id;
	protected	TextField<String>						title;
	protected	DateField								posted;
	protected	DateField								expires;
	protected	CheckBox								active;
	protected	CheckBox								priority;
	protected	CheckBoxGroup							checkGroup;
	protected	HtmlEditor								content;
	
	protected	Button									saveButton;
	protected	Button									startNewButton;
	protected	Button									refreshThisButton;
	protected	Button									refreshAllButton;
	protected	Button									deleteButton;
	protected	Button									cancelButton;
	
	protected	BeanModel								targetModel;
	protected	WelcomeMessageInstance					targetInstance;
	
	protected	Timer									dirtyListenTimer;
	/**
	 * The amount of space to leave at the top and bottom combined between the embedded panel and the container.
	 */
	protected int					verticalMargins = 60;
	
	public WelcomeMessageEditGrid() {
	}
	
	@Override    
	protected void onRender(Element parent, int index) {    
	    super.onRender(parent, index);
	    setLayout(new CenterLayout());
	    setStyleAttribute("margin", "10px");
	    setStyleAttribute("background", "transparent !important");
	
	    cp = new ContentPanel();
	
	    cp.setHeading("Welcome Messages");
	    cp.setFrame(true);
	    cp.setSize(1185, -1);
	    cp.setLayout(new RowLayout(Orientation.HORIZONTAL));
	    IconSupplier.setIcon(cp, IconSupplier.getMessagesIconName());
		
		// loader and store  
		loader = getLoader();
		store = new ListStore<BeanModel>(loader);
	    store.setMonitorChanges(true); 
	
	    grid = createGrid();
	    grid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
	    grid.getSelectionModel().addListener(Events.SelectionChange,    
	            new Listener<SelectionChangedEvent<BeanModel>>() { 
	    			@Override
	                public void handleEvent(SelectionChangedEvent<BeanModel> be) { 
	    				bind(be);
	                }
	            });
	    cp.add(grid, new RowData(535, 1));
	
	    panel = createForm();
	
	    cp.add(panel, new RowData(640, 1));
		
		loader.load();
	    formClear();
	    
	    add(cp);
	    
	    addResizeListener();
	    
	    resizePanelHeight();
	}
	
	/**
	 * Add a listener to detect a change in the parent container size, and resize the grid panel
	 */
	public void addResizeListener() {
		if (getParent() != null && getParent() instanceof LayoutContainer) {
			LayoutContainer c = (LayoutContainer) getParent();
			c.addListener(Events.Resize, new Listener<BaseEvent>() {

				@Override
				public void handleEvent(BaseEvent be) {
					if (be.getType().getEventCode() == Events.ResizeEnd.getEventCode()) {
						resizePanelHeight();
					}
				}
				
			});
		}
	}
	
	/**
	 * Resize the panel height based on the parent container height
	 */
	public void resizePanelHeight() {
		if (getParent() != null && isRendered()) {
			int newHeight = getParent().getOffsetHeight();
			if (newHeight > verticalMargins)
				newHeight -= verticalMargins;
			if (!cp.isRendered() || cp.getHeight() != newHeight) {
				cp.setHeight(newHeight);
				if (isRendered())
					layout(true);
			}
		}	
	}
	
	/**
	 * Create a form panel to edit welcome messages.
	 * @return
	 */
	protected FormPanel createForm() {    
		FormPanel panel = new FormPanel();
	    panel.setHeaderVisible(false);
	
	    id = new NumberField();
	    id.setName("id");
	    id.setFieldLabel("ID");
	    id.setReadOnly(true);
	    id.setWidth(50);
	    id.setVisible(false);
	    id.setToolTip("This is the ID that uniquely identifies the message in the database.");
	    panel.add(id);
	
	    title = new TextField<String>();
	    title.setName("title");
	    title.setFieldLabel("Title");
	    title.setMinLength(10);
	    title.setMessageTarget("tooltip");
	    title.setToolTip("This is the title of the message.");
	    panel.add(title, new FormData("100%"));
	
	    posted = new DateField();
	    posted.setName("postDate");
	    posted.setFieldLabel("Posted");
	    posted.setReadOnly(true);
	    posted.setToolTip("This is the date on which this message was created.");
	    panel.add(posted);   
	
	    expires = new DateField();
	    expires.setName("expireDate");
	    expires.setFieldLabel("Expires");
	    expires.setMinValue(new Date());
	    expires.setMinLength(0);
	    expires.setMessageTarget("tooltip");
	    expires.setToolTip("Select a date after which this message will no longer display on the Welcome tab.");
	    panel.add(expires);   
	
	    active = new CheckBox();
	    active.setName("active");
	    active.setBoxLabel("Active");
	    active.setToolTip("Uncheck this message to prevent it from displaying on the Welcome tab.");
	
	    priority = new CheckBox();
	    priority.setName("priority");
	    priority.setBoxLabel("Priority");
	    priority.setToolTip("Check this message as priority to get it to list before other messages on the Welcome tab.");
	    
	    checkGroup = new CheckBoxGroup();  
	    checkGroup.setFieldLabel("");
	    checkGroup.setLabelSeparator("");
	    checkGroup.add(active);
	    checkGroup.add(priority);
	    panel.add(checkGroup);
	    
	    content = new HtmlEditor();
	    content.setName("content");
	    content.setFieldLabel("Message");
	    content.setHeight(380);
	    panel.add(content, new FormData("100%"));
	    
	    setButtonStatusListener(panel);
	    
	    addFormButtons(panel);
	
	    return panel;
	}
	
	protected void disableButton(Button button) {
		if (button != null)
			if (button.isEnabled())
				button.setEnabled(false);
	}
	
	protected void enableButton(Button button) {
		if (button != null)
			if (!button.isEnabled())
				button.setEnabled(true);
	}
	
	protected void setButtonEnable(Button button, boolean enable) {
		if (button != null)
			if (button.isEnabled() != enable)
				button.setEnabled(enable);
	}
	
	protected boolean mainFieldsAreEmpty() {
		return title.getValue() == null || title.getValue().length() == 0 || expires.getValue() == null;
	}
	
	protected boolean allFieldsAreEmpty() {
		return mainFieldsAreEmpty() || content.getValue() == null || content.getValue().length() == 0;
	}
	
	protected boolean fieldsAreComplete() {
		return title.isValid() && expires.isValid() && !mainFieldsAreEmpty();
	}
	
	protected void debugDirty() {
		if (panel.isDirty()) {

		    for (Field<?> f : panel.getFields()) {
		      if (f.isDirty()) {
		        System.out.println(f.getName() + " is dirty with <" + f.getOriginalValue() + "> versus <" + f.getValue() + ">");
		      }
		    }
		}
	}
	
	protected void setButtonStatusListener(final FormPanel panel) {
		dirtyListenTimer = new Timer() {
			  @Override
			  public void run() {
			    	boolean panelDirty = panel != null && panel.isDirty();
			    	boolean panelValid = panel != null && panel.isValid();
			    	setButtonEnable(refreshThisButton, 	panelDirty && targetModel != null);
			    	setButtonEnable(refreshAllButton, 	!panelDirty || targetModel == null);
			    	setButtonEnable(saveButton, 		panelDirty && panelValid && fieldsAreComplete());
			    	setButtonEnable(deleteButton, 		!panelDirty && targetModel != null);
			    	setButtonEnable(startNewButton, 	!panelDirty);
			    	setButtonEnable(cancelButton, 		panelDirty || targetModel != null);	// || (targetModel == null && !allFieldsAreEmpty()));
			  }
			};

		dirtyListenTimer.scheduleRepeating(200);
	}
	
	protected void setReadOnly(boolean readOnly) {
		title.setReadOnly(readOnly);
		expires.setReadOnly(readOnly);
		active.setReadOnly(readOnly);
		priority.setReadOnly(readOnly);
		content.setReadOnly(readOnly);
	}
	
	protected void protectFields() {
		setReadOnly(true);
	}
	
	protected void openFields() {
		setReadOnly(false);
	}
	
	protected void bind(SelectionChangedEvent<BeanModel> be) {
		if (be.getSelection().size() > 0) {
			bind((BeanModel) be.getSelection().get(0));
		} else {
			unbind();
		}
	}
	
	protected void bind(BeanModel model) {
		targetModel = model;
		targetInstance = model.getBean();
		formSet();
		openFields();
	}
	
	protected void unbind() {
		targetModel = null;
		targetInstance = null;
		
		formClear();
		grid.getSelectionModel().deselectAll();
	}
	
	protected boolean modelBound() {
		return targetModel != null;
	}
	
	protected boolean noModelBound() {
		return targetModel == null;
	}
	
	protected boolean newModel() {
		return id.getValue() != null && id.getValue().intValue() < 0;
	}
	
	protected void formClear() {
		panel.clear();
		panel.clearDirtyFields();
		protectFields();
	}
	
	protected void formSet() {
		id.setOriginalValue(targetInstance.getId());
		title.setOriginalValue(targetInstance.getTitle());
		posted.setOriginalValue(targetInstance.getPostDate());
		expires.setOriginalValue(targetInstance.getExpireDate());
		active.setOriginalValue(targetInstance.isActive());
		priority.setOriginalValue(targetInstance.isPriority());
		content.setOriginalValue(targetInstance.getContent());
		
		formReset();
	}
	
	protected void formReset() {
		
		id.setValue(targetInstance.getId());
		title.setValue(targetInstance.getTitle());
		posted.setValue(targetInstance.getPostDate());
		expires.setValue(targetInstance.getExpireDate());
		active.setValue(targetInstance.isActive());
		priority.setValue(targetInstance.isPriority());
		content.setValue(targetInstance.getContent());
	}
	
	protected void reflectChanges(BeanModel updatedModel) {
		
//		This should have worked, but doesn't
//		targetInstance = targetModel.getBean();
////	targetInstance.setId(id.getValue().intValue());
//		targetInstance.setTitle(title.getValue());
////	targetInstance.setPosted(posted.getValue());
//		targetInstance.setExpireDate(expires.getValue());
//		targetInstance.setActive(active.getValue());
//		targetInstance.setPriority(priority.getValue());
//		targetInstance.setContent(content.getValue());
		
		//	Update anything that's not null directly in the bean model
		for (String name : updatedModel.getPropertyNames()) {
			if (updatedModel.get(name) != null) {
				targetModel.set(name, updatedModel.get(name));
			}
		}
		
		store.commitChanges();
		
		grid.getSelectionModel().deselectAll();
	}

	private void createSaveButton() { 
		
		saveButton = new Button("Save");
		
		saveButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			   
			@Override
			public void componentSelected(ButtonEvent ce) {
				if (noModelBound() && !newModel())
					refuseAction(ce, "Nothing is selected to be saved.");
				else
					asyncUpdate(false);
			}
		});
	}

	private void createStartNewButton() { 
		
		startNewButton = new Button("Start New");
		
		startNewButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			   
			@SuppressWarnings("deprecation")
			@Override
			public void componentSelected(ButtonEvent ce) {
				Date defaultExpires = new Date();
				defaultExpires.setDate(defaultExpires.getDate() + 1);
				
				unbind();
				
				id.setOriginalValue(-1);
				title.setOriginalValue("");
				posted.setOriginalValue(new Date());
				expires.setOriginalValue(defaultExpires);
				content.setOriginalValue("");
				active.setOriginalValue(true);
				priority.setOriginalValue(false);
				
				id.setValue(-1);
				title.setValue("");
				posted.setValue(new Date());
				expires.setValue(defaultExpires);
				content.setValue("");
				active.setValue(true);
				priority.setValue(false);
				
				openFields();
				title.focus();
			}  
		 
		}); 
	}

	private void createDeleteButton() { 
		
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
				if (noModelBound())
					refuseAction(ce, "No message is selected to be deleted.");
				else
					MessageBox.confirm("Confirm Delete", "Are you sure you want to delete this welcome message?", confirmDelete);
			}
		});
	}

	private void createRefreshThisButton() { 
		
		refreshThisButton = new Button("Refresh This");
		
		refreshThisButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			   
			@Override
			public void componentSelected(ButtonEvent ce) {
				getFormPanel().reset();
			}
		});
	}

	private void createRefreshAllButton() { 
		
		refreshAllButton = new Button("Refresh List");
		
		refreshAllButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			   
			@Override
			public void componentSelected(ButtonEvent ce) {
				if (loader != null)
					loader.load();
			}
		}); 
	}

	private void createCancelButton() { 
		
		cancelButton = new Button("Cancel");
		
		cancelButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			   
			@Override
			public void componentSelected(ButtonEvent ce) {
				cancelChanges();
			}
		});
	}
		
	/**
	 * Add Save, New and Refresh buttons to the form panel.
	 * @param panel
	 */
	private void addFormButtons(FormPanel panel) { 

		createSaveButton();
		createStartNewButton();
		createDeleteButton();
		createRefreshThisButton();
		createCancelButton();
		createRefreshAllButton();
 
	    panel.setButtonAlign(HorizontalAlignment.CENTER);
	    panel.addButton(saveButton); 
	    panel.addButton(startNewButton);
	    panel.addButton(deleteButton); 
	    panel.addButton(refreshThisButton); 
	    panel.addButton(cancelButton); 
	    panel.addButton(refreshAllButton);
	}
	
	private void refuseAction(final ButtonEvent be, final String message) {
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
	    column.setHidden(true);
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
	
	    column = new ColumnConfig("postDate", "Posted", 110);
	    column.setHeader("Posted");
	    column.setAlignment(HorizontalAlignment.RIGHT);
	    column.setDateTimeFormat(DateTimeFormat.getFormat("E, MMM dd, yyyy"));
	    configs.add(column);
	
	    column = new ColumnConfig("expireDate", "Expires", 110);
	    column.setHeader("Expires");
	    column.setAlignment(HorizontalAlignment.RIGHT);
	    column.setDateTimeFormat(DateTimeFormat.getFormat("E, MMM dd, yyyy"));
	    configs.add(column);
	    
		CheckColumnConfig checkColumn = new CheckColumnConfig("active", "Active", 55);
		CellEditor checkBoxEditor = new CellEditor(new CheckBox());
		checkColumn.setEditor(checkBoxEditor);
		configs.add(checkColumn);
	    
		checkColumn = new CheckColumnConfig("priority", "Priority", 55);
		checkBoxEditor = new CellEditor(new CheckBox());
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
		formClear();
		unbind();
	}
	
	protected void asyncLoad(Object loadConfig, AsyncCallback<List<WelcomeMessageInstance>> callback) {
		LoadConfig myLoadConfig = null;
		if (loadConfig instanceof LoadConfig)
			myLoadConfig = (LoadConfig) loadConfig;
		welcomeMessageListService.getWelcomeMessageList(myLoadConfig, callback);
	}
	
	protected void asyncUpdate(boolean delete) {
		WelcomeMessageInstance updateInstance = new WelcomeMessageInstance();
		updateInstance.setId(-1);
		if (id.getValue() != null && id.getValue().intValue() >= 0 )
			updateInstance.setId(id.getValue().intValue());
		else
			updateInstance.setNewRecord(true);
		if (delete) {
			updateInstance.setStatus(AppConstants.STATUS_DELETED);
		} else {
			updateInstance.setTitle(title.getValue());
			updateInstance.setContent(content.getValue());
			updateInstance.setExpireDate(expires.getValue());
			updateInstance.setActive(active.getValue());
			updateInstance.setPriority(priority.getValue());
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
							store.commitChanges();
						} else if (updatedWelcomeMessage.getStatus() == AppConstants.STATUS_DELETED) {
							store.remove(model);
							store.commitChanges();
						} else {
							reflectChanges(model);
						}
						unbind();
						formClear();
				}
			});
	}

	public WelcomeMessageListServiceAsync getWelcomeMessageListService() {
		return welcomeMessageListService;
	}

	public UpdateWelcomeMessageServiceAsync getUpdateWelcomeMessageService() {
		return updateWelcomeMessageService;
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

	public int getVerticalMargins() {
		return verticalMargins;
	}

	public void setVerticalMargins(int verticalMargins) {
		this.verticalMargins = verticalMargins;
	}

	@Override
	public void awaken() {
		if (dirtyListenTimer != null)
			dirtyListenTimer.scheduleRepeating(250);
	}

	@Override
	public void sleep() {
		if (dirtyListenTimer != null) {
			dirtyListenTimer.cancel();
		}
	}

}
