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
import com.extjs.gxt.ui.client.store.Store;
import com.extjs.gxt.ui.client.store.StoreEvent;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
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
import com.scholastic.sbam.client.services.UserListService;
import com.scholastic.sbam.client.services.UserListServiceAsync;
import com.scholastic.sbam.shared.objects.UserInstance;

public class UserEditGrid extends LayoutContainer {
	
	private ListStore<BeanModel>	store;
	private Grid<BeanModel>			grid;
	private ContentPanel 			panel;
	
	private final UserListServiceAsync userListService = GWT.create(UserListService.class);

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
                System.out.println("StoreEvent - After store Update " + se);
                if (se.getRecord() != null && se.getRecord().getModel() != null) {
                	System.out.println(se.getRecord().getModel().getProperties());
                	System.out.println(se.getModel().getProperties());
            	} else System.out.println("No model data");
                if (se.getOperation() != null) System.out.println(se.getOperation().name());
                else System.out.println("No operation");
                if (se.getModels() != null)
                	for (BeanModel model : se.getModels())
                		System.out.println(model.getProperties());
                if (se.getStore().getModels() != null)
                	for (BeanModel model : se.getStore().getModels())
                		System.out.println("store " + model.getProperties());
                System.out.println("Do updates if COMMIT");
            }
        });  
		
//		store.addListener(Store.DataChanged, new Listener<StoreEvent<BeanModel>>() {
//            public void handleEvent(final StoreEvent<BeanModel> se) {
//                System.out.println("StoreEvent - After store data DataChanged " + se);
//                if (se.getRecord() != null && se.getRecord().getModel() != null) {
//                	System.out.println(se.getRecord().getModel().getProperties());
//                	System.out.println(se.getModel().getProperties());
//            	} else System.out.println("No model data");
//                if (se.getOperation() != null) System.out.println(se.getOperation().name());
//                else System.out.println("No operation");
//            }
//        });
		
		store.addListener(Store.BeforeRemove, new Listener<StoreEvent<BeanModel>>() {
            public void handleEvent(final StoreEvent<BeanModel> se) {
                System.out.println("StoreEvent - Before store Remove " + se);
                if (se.getRecord() != null && se.getRecord().getModel() != null) {
                	System.out.println(se.getRecord().getModel().getProperties());
            	} else System.out.println("No record data");
                    if (se.getRecord() != null && se.getModel() != null) {
                	System.out.println(se.getModel().getProperties());
            	} else System.out.println("No model data");
                if (se.getOperation() != null) System.out.println(se.getOperation().name());
                else System.out.println("No operation");
                if (se.getModels() != null)
                	for (BeanModel model : se.getModels())
                		System.out.println(model.getProperties());
                if (se.getStore().getModels() != null)
                	for (BeanModel model : se.getStore().getModels())
                		System.out.println("store " + model.getProperties());
                System.out.println("Do deletes");
            }
        });
		
//		store.addListener(Store.Remove, new Listener<StoreEvent<BeanModel>>() {
//            public void handleEvent(final StoreEvent<BeanModel> se) {
//                System.out.println("StoreEvent - After store Remove " + se);
//                if (se.getRecord() != null && se.getRecord().getModel() != null) {
//                	System.out.println(se.getRecord().getModel().getProperties());
//                	System.out.println(se.getModel().getProperties());
//            	} else System.out.println("No model data");
//                if (se.getOperation() != null) System.out.println(se.getOperation().name());
//                else System.out.println("No operation");
//                if (se.getModels() != null)
//                	for (BeanModel model : se.getModels())
//                		System.out.println(model.getProperties());
//                if (se.getStore().getModels() != null)
//                	for (BeanModel model : se.getStore().getModels())
//                		System.out.println("store " + model.getProperties());
//                System.out.println("Do deletes");
//            }
//        });
		
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

		final RowEditor<ModelData> re = new BetterRowEditor<ModelData>(store, new String [] {"id", "userName"});
//		final RowEditor<ModelData> re = new RowEditor<ModelData>() {
//			
//			private static final String DELETE_PROPERTY = "__deleted";
//			private Button delButton;
//			
//			@Override
//			protected void onRender(Element target, int index) {
//					super.onRender(target, index);
//					//	Add a Delete button if the id is not null, i.e. a record exists
//					if (btns != null) {
//						btns.setLayout(new TableLayout(3));
//						delButton = new Button("Delete");      
//						delButton.addListener(Events.Select, new Listener<ButtonEvent>() {
//							public void handleEvent(ButtonEvent be) {
//								final Listener<MessageBoxEvent> confirmDelete = new Listener<MessageBoxEvent>() {  
//									public void handleEvent(MessageBoxEvent ce) {  
//										Button btn = ce.getButtonClicked();
//										if ("Yes".equals(btn.getText()))
//											doDelete();
//									}  
//								};
//								MessageBox.confirm("Confirm Delete", "Are you sure you want to delete this entry?", confirmDelete);
//							}
//						});
//						delButton.setMinWidth(getMinButtonWidth());
//						btns.add(delButton);
//						btns.layout(true);
//					}
//			}
//			@Override
//			protected void afterRender() {
//				super.afterRender();
//				if (renderButtons) {
//					btns.setWidth((getMinButtonWidth() * 3) + (5 * 3) + (3 * 4));
//				}
//			} 
//			
//			protected void doDelete() {
//				store.getAt(rowIndex).set(DELETE_PROPERTY, DELETE_PROPERTY);
//				stopEditing(false);
//			}
//			
//			@Override
//			public void startEditing(int rowIndex, boolean doFocus) {
//				super.startEditing(rowIndex, doFocus);
//				if (store.getAt(rowIndex).getProperties().get("id") != null && !store.getAt(rowIndex).getProperties().containsKey(DELETE_PROPERTY))
//					delButton.enable();
//				else
//					delButton.disable();
//			}
//			
//			@Override
//			public void stopEditing(boolean saveChanges) {
//				boolean validChanges = isValid();
//				super.stopEditing(saveChanges);
//				if (!saveChanges) {
//					store.rejectChanges();
//					removeEmptyRows();
//				} else if (validChanges) {
//					System.out.println("Before store commit");
//					store.commitChanges();
//					System.out.println("After store commit");
//				}
//			}
//			
//			@Override
//			public boolean isValid() {
//				boolean fieldsAreValid = super.isValid();
//				//	Now do a higher level of validation, e.g. multiple field relationships -- warning, THIS GETS CALLED REPEATEDLY during editing!!!!
//				return fieldsAreValid;
//			}
//			
//			private void removeEmptyRows() {
//				for (BeanModel data : store.getModels()) {
//					System.out.println(data.getProperties());
//					//	Rows with no id and no user name are considered "empty"
//					if (data.get("id") == null && (data.get("userName") == null || data.get("userName").toString().length() == 0)) {
//						store.remove(data);
//					} else if (data.get(DELETE_PROPERTY) != null) {
//						store.remove(data);
//					}
//				}
//			}
//			
//		};

		grid.addPlugin(re);
		
//		re.addListener(Events.AfterEdit, new Listener<RowEditorEvent>() {
//            public void handleEvent(final RowEditorEvent be) {
//                System.out.println("RowEditorEvent - After edit " + be);
//                System.out.println(be.getRowIndex() + " / " + be.getChanges());
//                System.out.println(be.getRecord().getPropertyNames());
//                System.out.println(be.getRecord().getModel().getProperties());
//                System.out.println(be.getRecord().getChanges());
//            }
//        });
//		
//		re.addListener(Events.BeforeEdit, new Listener<RowEditorEvent>() {
//            public void handleEvent(final RowEditorEvent be) {
//                System.out.println("RowEditorEvent - Before edit " + be);
//                System.out.println(be.getRowIndex() + " / " + be.getChanges());
//                System.out.println(be.getRecord().getPropertyNames());
//                System.out.println(be.getRecord().getModel().getProperties());
//                System.out.println(be.getRecord().getChanges());
//            }
//        });
//		
//		re.addListener(Events.ValidateEdit, new Listener<RowEditorEvent>() {
//            public void handleEvent(final RowEditorEvent be) {
//                System.out.println("RowEditorEvent - Validate edit " + be);
//                System.out.println(be.getRowIndex() + " / " + be.getChanges());
//                System.out.println(be.getRecord().getPropertyNames());
//                System.out.println(be.getRecord().getModel().getProperties());
//                System.out.println(be.getRecord().getChanges());
//            }
//        });
		
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

//		ToolBar toolBar = new ToolBar();
//		toolBar.add(newUser);  
//		panel.add(toolBar);
//		panel.setTopComponent(toolBar);  
		 
//		panel.addButton(new Button("Reset", new SelectionListener<ButtonEvent>() {  
//		 
//			@Override  
//			public void componentSelected(ButtonEvent ce) {  
//				store.rejectChanges();  
//			}
//			
//		}));  
//
//		panel.addButton(new Button("Save", new SelectionListener<ButtonEvent>() {  
//	 
//			@Override  
//			public void componentSelected(ButtonEvent ce) {  
//				grid.getStore().commitChanges();  
//			}
//			
//		}));  
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

}
