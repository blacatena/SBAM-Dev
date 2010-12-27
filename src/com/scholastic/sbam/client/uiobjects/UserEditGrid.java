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
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.grid.CellEditor;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.RowEditor;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.scholastic.sbam.client.services.UpdateUserService;
import com.scholastic.sbam.client.services.UpdateUserServiceAsync;
import com.scholastic.sbam.client.services.UserListService;
import com.scholastic.sbam.client.services.UserListServiceAsync;
import com.scholastic.sbam.shared.objects.UserInstance;

public class UserEditGrid extends LayoutContainer {
	
	private ListStore<BeanModel>	store;
	private Grid<BeanModel>			grid;
	private ContentPanel 			panel;
	
	private final UserListServiceAsync userListService = GWT.create(UserListService.class);
	private final UpdateUserServiceAsync updateUserService = GWT.create(UpdateUserService.class);

	public UserEditGrid() {
	}
	
	@Override  
	protected void onRender(Element parent, int index) {  
		super.onRender(parent, index);  
		setStyleAttribute("padding", "20px");
		
		panel = new ContentPanel(); 
		
		// proxy and reader  
		RpcProxy<List<UserInstance>> proxy = new RpcProxy<List<UserInstance>>() {  
			@Override  
			public void load(Object loadConfig, AsyncCallback<List<UserInstance>> callback) {
		    	userListService.getUsers(null, null, null, null, callback);
		    }  
		};  
		BeanModelReader reader = new BeanModelReader();  
		
		// loader and store  
		ListLoader<ListLoadResult<ModelData>> loader = new BaseListLoader<ListLoadResult<ModelData>>(proxy, reader);  
		store = new ListStore<BeanModel>(loader);  
		
		store.addListener(Store.Update, new Listener<StoreEvent<BeanModel>>() {
            public void handleEvent(final StoreEvent<BeanModel> se) {
//                if (se.getRecord() != null && se.getRecord().getModel() != null) {
//                	System.out.println(se.getRecord().getModel().getProperties());
//            	} else System.out.println("No model data");
                
                if (se.getOperation() == Record.RecordUpdate.COMMIT && se.getModel() != null) {
                	updateUser(se.getModel());
                }
                
            }
        });
		
		loader.load();  
		
		// column model  
		List<ColumnConfig> columns = new ArrayList<ColumnConfig>();  
		columns.add(getEditColumn(			"userName", 	"User", 		60));
		columns.add(getEditPasswordColumn(	"password", 	"Password", 	60, 	null));
		columns.add(getEditColumn(			"firstName", 	"First Name", 	100));
		columns.add(getEditColumn(			"lastName", 	"Last Name", 	100));
		columns.add(getEditColumn(			"email", 		"Email", 		200));
		ColumnModel cm = new ColumnModel(columns);  
		
		grid = new Grid<BeanModel>(store, cm);
		grid.setBorders(true);  
		grid.setStripeRows(true);  
		grid.setColumnLines(true);  
		grid.setColumnReordering(true);
		grid.setAutoExpandColumn("userName");
		grid.getAriaSupport().setLabelledBy(panel.getHeader().getId() + "-label"); // access for people with disabilities -- ARIA

		makeRowEditor();
		
		panel.setHeading("Users");
		panel.setFrame(true);
		panel.setSize(600, 600);  
		//panel.setIcon(Resources.ICONS.table());  
		panel.setLayout(new FitLayout()); 
		
		add(panel);  
	}
	
	protected void makeRowEditor() {

		final RowEditor<ModelData> re = new BetterRowEditor<ModelData>(store);

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
	
	protected ColumnConfig getColumn(String name, String header, int width) {
		return getColumn(name, header, width, null);
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
		text.setAllowBlank(false);
		if (password) {
			text.setPassword(password);
		}
		column.setEditor(new CellEditor(text));
		return column;
	}
	
	private void updateUser(BeanModel beanModel) {
		final BeanModel targetBeanModel = beanModel;
	//	System.out.println("Before update: " + targetBeanModel.getProperties());
		updateUserService.updateUser((UserInstance) beanModel.getBean(),
				new AsyncCallback<UserInstance>() {
					public void onFailure(Throwable caught) {
						// Show the RPC error message to the user
						System.out.println(caught);
						MessageBox.alert("Alert", "User update failed unexpectedly.", null);
					}

					public void onSuccess(UserInstance updatedUser) {
					//	System.out.println("UPDATE SUCCESSFUL");
						// If this user is newly created, back-populate the id
						if (targetBeanModel.get("id") == null) {
							targetBeanModel.set("id",updatedUser.getId());
					//	System.out.println("After update " + targetBeanModel.getProperties());
					}
				}
			});
	}

}
