package com.scholastic.sbam.client.uiobjects;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.data.BaseListLoader;
import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.BeanModelFactory;
import com.extjs.gxt.ui.client.data.BeanModelLookup;
import com.extjs.gxt.ui.client.data.BeanModelReader;
import com.extjs.gxt.ui.client.data.ListLoadResult;
import com.extjs.gxt.ui.client.data.ListLoader;
import com.extjs.gxt.ui.client.data.ModelData; 
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.Record;
import com.extjs.gxt.ui.client.store.Store;
import com.extjs.gxt.ui.client.store.StoreEvent;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.Validator;
import com.extjs.gxt.ui.client.widget.grid.CellEditor;
import com.extjs.gxt.ui.client.widget.grid.CheckColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.RowEditor;
import com.extjs.gxt.ui.client.widget.grid.filters.DateFilter;
import com.extjs.gxt.ui.client.widget.grid.filters.GridFilters;
import com.extjs.gxt.ui.client.widget.grid.filters.ListFilter;
import com.extjs.gxt.ui.client.widget.grid.filters.StringFilter;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.scholastic.sbam.client.services.FieldValidationServiceAsync;
import com.scholastic.sbam.client.services.UpdateUserService;
import com.scholastic.sbam.client.services.UpdateUserServiceAsync;
import com.scholastic.sbam.client.services.UserListService;
import com.scholastic.sbam.client.services.UserListServiceAsync;
import com.scholastic.sbam.client.services.UserNameValidationService;
import com.scholastic.sbam.client.services.UserNameValidationServiceAsync;
import com.scholastic.sbam.client.validation.AsyncTextField;
import com.scholastic.sbam.shared.objects.UpdateResponse;
import com.scholastic.sbam.shared.objects.UserInstance;
import com.scholastic.sbam.shared.security.SecurityManager;
import com.scholastic.sbam.shared.util.AppConstants;
import com.scholastic.sbam.shared.validation.AppRoleGroupValidator;
import com.scholastic.sbam.shared.validation.AppUserNameValidator;
import com.scholastic.sbam.shared.validation.EmailValidator;

public class UserEditGrid extends LayoutContainer implements AppSleeper {
	
	private ListStore<BeanModel>	store;
	private Grid<BeanModel>			grid;
	private ContentPanel 			panel;
	
	private List<BeanModel> selection;
	
	private final UserListServiceAsync userListService = GWT.create(UserListService.class);
	private final UpdateUserServiceAsync updateUserService = GWT.create(UpdateUserService.class);
	private final UserNameValidationServiceAsync userNameValidationService = GWT.create(UserNameValidationService.class);

	public UserEditGrid() {
	}
	
	@Override  
	protected void onRender(Element parent, int index) {  
		super.onRender(parent, index);
		setStyleAttribute("padding", "20px");
		
		panel = new ContentPanel(); 
		
		// loader and store  
		ListLoader<ListLoadResult<ModelData>> loader = getLoader();
		store = new ListStore<BeanModel>(loader);
		
		addStoreListeners();
		
			loader.load();
		
		// column model
		ColumnModel cm = getColumnModel();
		
		grid = new Grid<BeanModel>(store, cm);
		grid.setBorders(true);
		grid.setStripeRows(true);
		grid.setColumnLines(true);
		grid.setColumnReordering(true);
		grid.setAutoExpandColumn("userName");
		grid.getAriaSupport().setLabelledBy(panel.getHeader().getId() + "-label"); // access for people with disabilities -- ARIA

		makeFilters();
		
		makeRowEditor();
		
		int width = 0;
		for (int i = 0; i < cm.getColumnCount(); i++)
			width += cm.getColumnWidth(i);
		
		panel.setHeading("Users");
		panel.setFrame(true);
		panel.setSize(width, 450);
		//panel.setIcon(Resources.ICONS.table());
		panel.setLayout(new FitLayout());
		
		add(panel);
	}
	
	protected ListLoader<ListLoadResult<ModelData>> getLoader() {
		// proxy and reader  
		RpcProxy<List<UserInstance>> proxy = new RpcProxy<List<UserInstance>>() {  
			@Override  
			public void load(Object loadConfig, final AsyncCallback<List<UserInstance>> callback) {
				
				// This could be as simple as calling userListService.getUsers and passing the callback
				// Instead, here the callback is overridden so that it can catch errors and alert the users.  Then the original callback is told of the failure.
				// On success, the original callback is just passed the onSuccess message, and the response (the list).
				
				AsyncCallback<List<UserInstance>> myCallback = new AsyncCallback<List<UserInstance>>() {
					public void onFailure(Throwable caught) {
						// Show the RPC error message to the user
						if (caught instanceof IllegalArgumentException)
							MessageBox.alert("Alert", caught.getMessage(), null);
						else {
							MessageBox.alert("Alert", "User list load failed unexpectedly.", null);
							System.out.println(caught.getClass().getName());
							System.out.println(caught.getMessage());
						}
						callback.onFailure(caught);
					}

					public void onSuccess(List<UserInstance> list) {
						callback.onSuccess(list);
					}
				};
				
				userListService.getUsers(null, null, null, null, myCallback);
							
		    }  
		};
		BeanModelReader reader = new BeanModelReader();
		
		// loader and store  
		ListLoader<ListLoadResult<ModelData>> loader = new BaseListLoader<ListLoadResult<ModelData>>(proxy, reader);
		return loader;
	}
	
	protected void addStoreListeners() {
		store.addListener(Store.Update, new Listener<StoreEvent<BeanModel>>() {
            public void handleEvent(final StoreEvent<BeanModel> se) {
                if (se.getOperation() == Record.RecordUpdate.COMMIT && se.getModel() != null) {
                	updateUser(se.getModel());
                }
                
            }
        });	
	}
	
	protected ColumnModel getColumnModel() {  
		List<ColumnConfig> columns = new ArrayList<ColumnConfig>();
		addColumns(columns);
		return new ColumnModel(columns);
	}
	
	protected void addColumns(List<ColumnConfig> columns) {
		columns.add(getEditColumn(			"userName", 		"User", 			80,		"",		new AppUserNameValidator(), userNameValidationService));
	//	columns.add(getEditPasswordColumn(	"password", 		"Password", 		60, 	null));
		columns.add(getEditColumn(			"firstName", 		"First Name", 		100));
		columns.add(getEditColumn(			"lastName", 		"Last Name", 		100));
		columns.add(getEditColumn(			"email", 			"Email", 			200,	"",		new EmailValidator(),		null));
		columns.add(getRoleGroupColumn(							"Role",				130));	
		columns.add(getEditCheckColumn(		"resetPassword",	"Reset Password", 	100,		"Check to reset user's password"));
		columns.add(getDateColumn(			"createdDatetime",	"Created", 			75));
	}
	
	protected void makeFilters() {
		GridFilters filters = new GridFilters();  
		filters.setLocal(true);  
		    
//		NumericFilter filter = new NumericFilter("change");  
		StringFilter userNameFilter = new StringFilter("userName");  
		StringFilter firstNameFilter = new StringFilter("firstName");  
		StringFilter lastNameFilter	= new StringFilter("lastName");  
		StringFilter emailFilter	= new StringFilter("email");  
		DateFilter   dateFilter		= new DateFilter("createdDatetime");  
//		BooleanFilter booleanFilter = new BooleanFilter("split");  
		  
//		ListStore<ModelData> typeStore = new ListStore<ModelData>();
//		for (int i = 0; i < SecurityManager.ROLE_GROUPS.length; i++)
//			typeStore.add(SecurityManager.ROLE_GROUPS [i].getGroupTitle());
		ListFilter listFilter = new ListFilter("roleGroupTitle", SecurityManager.getRoleGroupListStore());  
		listFilter.setDisplayProperty("groupTitle");  
		
		filters.addFilter(userNameFilter);  
		filters.addFilter(firstNameFilter);  
		filters.addFilter(lastNameFilter);  
		filters.addFilter(emailFilter);  
		filters.addFilter(dateFilter);  
		filters.addFilter(listFilter);
		
		grid.addPlugin(filters);
	}
	
	protected void makeRowEditor() {
		
		Field<?> userNameField = grid.getColumnModel().getColumns().get(0).getEditor().getField();
		//	BetterRowEditor created with delete button, not additional buttons, and the userName field as unchangeable
		final RowEditor<ModelData> re = new BetterRowEditor<ModelData>(store, true, null, userNameField);

		grid.addPlugin(re);
		
		Button newUser = new Button("New User");
		newUser.addSelectionListener(new SelectionListener<ButtonEvent>() {
			   
			@Override
			public void componentSelected(ButtonEvent ce) {
				UserInstance user = new UserInstance();
				user.setUserName("");
				user.setFirstName("");
				user.setLastName("");
				user.setEmail("");
				user.setRoleGroupTitle("None");
				 
				re.stopEditing(false);
				BeanModel userModel = getModel(user);
				store.insert(userModel, 0);
				re.startEditing(store.indexOf(userModel), true);
			 
			}  
		 
		});
		
		panel.add(grid);
		panel.setButtonAlign(HorizontalAlignment.CENTER);
		
		panel.addButton(newUser);
	}
	
	private BeanModel getModel(UserInstance user) {
		BeanModelFactory factory = BeanModelLookup.get().getFactory(UserInstance.class);
		BeanModel model = factory.createModel(user);
		return model;
	}
	
	public void awaken() {
		reload();
	}
	
	public void sleep() {
		clear();
	}
	
	protected void reload() {
		if (store != null)
			store.getLoader().load();
		if (grid != null && grid.getSelectionModel() != null&& selection != null)
			grid.getSelectionModel().setSelection(selection);
	}
	
	protected void clear() {
		if (grid != null && grid.getSelectionModel() != null)
			selection = grid.getSelectionModel().getSelection();
		if (store != null)
			store.removeAll();
	}
	
	protected ColumnConfig getColumn(String name, String header, int width) {
		return getColumn(name, header, width, null);
	}
	
	protected ColumnConfig getDateColumn(String name, String header, int width) {
		return getDateColumn(name, header, width, AppConstants.APP_DATE_TIME_FORMAT);
	}
	
	protected ColumnConfig getDateColumn(String name, String header, int width, DateTimeFormat format) {
		DateField dateField = new DateField();  
		dateField.getPropertyEditor().setFormat(AppConstants.APP_DATE_TIME_FORMAT);
		dateField.setReadOnly(true);
		
		ColumnConfig column = getColumn(name, header, width, null);
		column.setDateTimeFormat(format);
		column.setEditor(new CellEditor(dateField));
		return column;
	}
	
	protected ColumnConfig getColumn(String name, String header, int width, String toolTip) {
		ColumnConfig column = new ColumnConfig();
		column.setId(name);
		column.setHeader(header);
		column.setWidth(width);
		if (toolTip != null && toolTip.length() > 0)
			column.setToolTip(toolTip);
		return column;
	}
	
	protected ColumnConfig getEditColumn(String name, String header, int width) {
		return getEditColumn(name, header, width, null, false);
	}
	
	protected ColumnConfig getEditColumn(String name, String header, int width, String toolTip) {
		return getEditColumn(name, header, width, toolTip, false);
	}
	
	protected ColumnConfig getEditPasswordColumn(String name, String header, int width, String toolTip) {
		return getEditColumn(name, header, width, toolTip, true);
	}
	
	protected ColumnConfig getEditReadOnlyColumn(String name, String header, int width, String toolTip) {
		ColumnConfig column = getColumn(name, header, width, toolTip);
		return column;
	}
	
	protected ColumnConfig getEditColumn(String name, String header, int width, String toolTip, boolean password) {
		ColumnConfig column = getColumn(name, header, width, toolTip);
		TextField<String> text = new TextField<String>();
		text.setName(name);
		text.setAllowBlank(false);
		if (password) {
			text.setPassword(password);
		}
		column.setEditor(new CellEditor(text));
		return column;
	}
	
	protected ColumnConfig getEditColumn(String name, String header, int width, String toolTip, Validator validator, FieldValidationServiceAsync validationService) {
		ColumnConfig column = getColumn(name, header, width, toolTip);
		AsyncTextField<String> text = new AsyncTextField<String>();
		text.setAllowBlank(false);
		if (validator != null) {
			text.setValidator(validator);
		}
		if (validationService != null) {
			text.setValidationService(validationService);
		}
		column.setEditor(new CellEditor(text));
		return column;
	}
	
	protected ColumnConfig getEditDateColumn(String name, String header, int width, String toolTip, Validator validator) {
		DateField dateField = new DateField();  
		dateField.getPropertyEditor().setFormat(AppConstants.APP_DATE_TIME_FORMAT);  
		
		ColumnConfig column = new ColumnConfig();  
		column.setId(name);  
		column.setHeader(header);  
		column.setWidth(width);  
		column.setEditor(new CellEditor(dateField));  
		column.setDateTimeFormat(AppConstants.APP_DATE_TIME_FORMAT);
		return column;
	}
	
	protected ColumnConfig getEditCheckColumn(String name, String header, int width, String toolTip) {
		CheckColumnConfig checkColumn = new CheckColumnConfig(name, header, width); 
		CellEditor checkBoxEditor = new CellEditor(new CheckBox());
		checkColumn.setEditor(checkBoxEditor);
		return checkColumn;
	}
	
	protected ColumnConfig getRoleGroupColumn(String header, int width) {
		
		final SimpleComboBox<String> combo = new SimpleComboBox<String>();
		combo.setForceSelection(true);
		combo.setTriggerAction(TriggerAction.ALL);
		combo.setValidator(new AppRoleGroupValidator());
		combo.setEditable(false);
		
		for (int i = 0; i < SecurityManager.ROLE_GROUPS.length; i++) {
			combo.add(SecurityManager.ROLE_GROUPS [i].getGroupTitle());
		}
		
		CellEditor editor = new CellEditor(combo) {  
		  @Override  
		  public Object preProcessValue(Object value) {  
		    if (value == null) {  
		      return value;
		    }  
		    return combo.findModel(value.toString());
		  }  
 
		  @Override  
		  public Object postProcessValue(Object value) {  
		    if (value == null) {  
		      return value;
		    }  
		    return ((ModelData) value).get("value");
		  }  
		};

		ColumnConfig column = new ColumnConfig();
		column.setId("roleGroupTitle");
		column.setHeader(header);
		column.setWidth(width);
		column.setEditor(editor);
	
		return column; 
	}
	
	private void updateUser(BeanModel beanModel) {
		final BeanModel targetBeanModel = beanModel;
	//	System.out.println("Before update: " + targetBeanModel.getProperties());
		updateUserService.updateUser((UserInstance) beanModel.getBean(),
				new AsyncCallback<UpdateResponse<UserInstance>>() {
					public void onFailure(Throwable caught) {
						// Show the RPC error message to the user
						if (caught instanceof IllegalArgumentException)
							MessageBox.alert("Alert", caught.getMessage(), null);
						else {
							MessageBox.alert("Alert", "User update failed unexpectedly.", null);
							System.out.println(caught.getClass().getName());
							System.out.println(caught.getMessage());
						}
					}

					public void onSuccess(UpdateResponse<UserInstance> updateResponse) {
						UserInstance updatedUser = (UserInstance) updateResponse.getInstance();
						// If this user is newly created, back-populate the id
						if (targetBeanModel.get("id") == null) {
							targetBeanModel.set("id",updatedUser.getId());
							targetBeanModel.set("createdDatetime", updatedUser.getCreatedDatetime());
						}
						targetBeanModel.set("resetPassword", false);
						if (updateResponse.getMessage() != null && updateResponse.getMessage().length() > 0)
							MessageBox.info("Please Note...", updateResponse.getMessage(), null);
				}
			});
		targetBeanModel.set("resetPassword", false);
	}

}
