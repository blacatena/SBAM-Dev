package com.scholastic.sbam.client.uiobjects.uiconfig;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.data.BaseTreeModel;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.dnd.DND.Feedback;
import com.extjs.gxt.ui.client.dnd.TreePanelDragSource;
import com.extjs.gxt.ui.client.dnd.TreePanelDropTarget;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.DNDEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.Store;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.ButtonBar;
import com.extjs.gxt.ui.client.widget.button.ToolButton;
import com.extjs.gxt.ui.client.widget.form.StoreFilterField;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel.CheckCascade;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel.CheckNodes;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel.TreeNode;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.IsSerializable;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.scholastic.sbam.client.services.ProductServiceListService;
import com.scholastic.sbam.client.services.ProductServiceListServiceAsync;
import com.scholastic.sbam.client.services.UpdateProductServiceListService;
import com.scholastic.sbam.client.services.UpdateProductServiceListServiceAsync;
import com.scholastic.sbam.client.uiobjects.foundation.AppSleeper;
import com.scholastic.sbam.client.uiobjects.foundation.DualEditGridLink;
import com.scholastic.sbam.client.uiobjects.foundation.DualEditGridLinker;
import com.scholastic.sbam.client.uiobjects.uitop.HelpTextDialog;
import com.scholastic.sbam.client.util.IconSupplier;
import com.scholastic.sbam.shared.objects.ProductServiceTreeInstance;

public class ProductServiceSelectTree extends LayoutContainer implements DualEditGridLink, AppSleeper {
	
	/**
	 * This is a simple utility class to simplify the creation of folder items.
	 * 
	 * @author Bob Lacatena
	 *
	 */
	public class Folder extends BaseTreeModel implements IsSerializable, ModelData {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public Folder() {
			set("type", "folder");
		}

		public Folder(String description) {
			this();
			set("description", description);
		}

		public Folder(String name, BaseTreeModel[] children) {
			this(name);
			for (int i = 0; i < children.length; i++) {
				add(children[i]);
			}
		}

		public void setDescription(String description) {
			set("description", description);
		}

		public String getDescription() {
			return (String) get("description");
		}

		public String toString() {
			return getDescription();
		}
	};
	
	/**
	 * Customized version to show folder icons for folders which are leafs (could have used ModelIconProvider, but didn't).
	 * 
	 * @author Bob Lacatena
	 *
	 * @param <M>
	 */
	public class MyTreePanel<M extends ModelData> extends TreePanel<M> {

		public MyTreePanel(TreeStore<M> store) {
			super(store);
		}
		
		/**
		 * Overridden to return a folder icon for a leaf which is defined as a folder.
		 */
		@Override
		protected AbstractImagePrototype calculateIconStyle(M model) {
			if (isLeaf(model) && isFolder(model)) {
				return getStyle().getNodeOpenIcon();
			}
			return super.calculateIconStyle(model);
		}
		
		/**
		 * Overridden to automatically check anything which has the "checked" property set to "checked" when the model is added to the tree.
		 */
		@Override
		protected String register(M m) {
			String id = super.register(m);
			setChecked(m, (m.get("checked") != null && m.get("checked").equals("checked")));
			return id;
		}
		
		/**
		 * Overridden to turn off cascade behavior when rendering children, which happens on a load, refresh, expand, etc.
		 * 
		 * Basically, only do the cascade when the user checks the box.
		 * 
		 */
		@Override
		protected void renderChildren(M parent) {
			CheckCascade save = getCheckStyle();
			setCheckStyle(CheckCascade.NONE);
			super.renderChildren(parent);
			setCheckStyle(save);
		}
		
		/**
		 * Overridden to adjust model in store to match tree, both for use with the filter and for passing back to the server side
		 */
		@Override
		public void setChecked(M m, boolean checked) {
			super.setChecked(m, checked);
			if (m.getPropertyNames().contains("checked"))
				m.set("checked", checked ? "checked" : "");
			if (store.contains(m)) {
				if (store.getRecord(m).getModel().getPropertyNames().contains("checked")) {
					store.getRecord(m).getModel().set("checked", checked ? "checked" : "");
				}
			}
		}
		
		private boolean isFolder(ModelData item) {
			return item.get("type") != null && item.get("type").equals("folder");
		}
		
	}
	
	/**
	 * Customized version to reject drops on non-Folder items
	 * @author Bob Lacatena
	 *
	 */
	public class MyTreePanelDropTarget extends TreePanelDropTarget {

		public MyTreePanelDropTarget(TreePanel<ModelData> tree) {
			super(tree);
		}


		/**
		 * Overridden to disallow drops onto non-folder items, whether they are leafs or not.
		 */
		@SuppressWarnings("rawtypes")
		@Override
		protected void handleAppend(DNDEvent event, final TreeNode item) {
			final TreeNode overItem = tree.findNode(event.getTarget());
		    if (overItem != null) {
		    	if (overItem instanceof TreeNode) {
		    		if (!isFolder((TreeNode) overItem)) {
		    			clearStyles(event);
		    			return;
		    		}
		    	}
		    }
		    super.handleAppend(event, overItem);
		}
		
		@SuppressWarnings("rawtypes")
		public boolean isFolder(TreeNode item) {
			return isFolder(item.getModel());
		}
		
		public boolean isFolder(ModelData item) {
			return item.get("type") != null && item.get("type").equals("folder");
		}
	}

	
	protected String				helpTextId;
	
	private String productCode;
	private String panelHeading;
	private DualEditGridLinker gridLinker;

	private ContentPanel panel;
	private TreePanel<ModelData> tree;
	private TreeStore<ModelData> store;
	
	private final ProductServiceListServiceAsync productServiceListService = GWT.create(ProductServiceListService.class);
	private final UpdateProductServiceListServiceAsync updateProductServiceListService = GWT.create(UpdateProductServiceListService.class);

	public ProductServiceSelectTree() {
		super();
		this.helpTextId = this.getClass().getName();
		if (helpTextId.lastIndexOf('.') >= 0)
			helpTextId = helpTextId.substring(helpTextId.lastIndexOf('.') + 1);
	}

	@Override  
	protected void onRender(Element parent, int index) {  
		super.onRender(parent, index);

		setLayout(new FitLayout());
		setBorders(false);
		
		panel = new ContentPanel(new FitLayout()) {
			@Override
			protected void initTools() {
				addHelp(this);
				super.initTools();
			}		
		};
		panel.setBorders(false);
		IconSupplier.setIcon(panel, IconSupplier.getServiceIconName());
		panel.setHeading(panelHeading);
		panel.setHeaderVisible(true);
//		panel.setDeferHeight(true);
		
		createTreeStore();
		StoreFilterField<ModelData> filter = getFilter();
		ToolBar toolbar = getExpandCollapseBar();
		createTreePanel();
		
		final LayoutContainer tools = new LayoutContainer(new RowLayout(Orientation.HORIZONTAL));
		tools.setHeight(30);
		tools.add(filter,  new RowData(0.6, -1, new Margins(2)));
		tools.add(toolbar, new RowData(200, 30, new Margins(2)));
		
		LayoutContainer inset = new LayoutContainer(new FlowLayout(10)) {
			@Override
			protected void onResize(int width, int height) {
				if (tools.isRendered()) {
					tree.setHeight(height - tools.getHeight());
				} else {
					tree.setHeight(height - 40);
				}
				tree.setWidth(width - 10);
				
				super.onResize(width, height);	
			}
		};
//		inset.setDeferHeight(true);
//		inset.setStyleAttribute("padding", "10px");
		
//		LayoutContainer container = new LayoutContainer();
//		container.setSize(400, 300);
//		container.setBorders(false);  
//		container.setLayout(new FitLayout());

//		inset.add(filter);
//		inset.add(toolbar);
		inset.add(tools);
		inset.add(tree);
//		container.add(getTreePanel());
//		inset.add(container);
		createButtons();
		
		panel.add(inset);
		
		add(panel);
		
		addReorderCapability();
	}
	
	/**
	 * Create the TreeStore from the data.
	 */
	public void createTreeStore() {
		store = new TreeStore<ModelData>();
		refreshTreeData();
	}
	
	/**
	 * Set up and get the filter to use with the store.
	 * @return
	 */
	public StoreFilterField<ModelData> getFilter() {
		StoreFilterField<ModelData> filter = new StoreFilterField<ModelData>() {  
		
		  @Override  
		  protected boolean doSelect(Store<ModelData> store, ModelData parent,  
		      ModelData record, String property, String filter) {  
		    // only match leaf nodes  
		    if (record instanceof Folder) {  
		      return false;
		    }  
		    String description = record.get("description");
		    description = description.toLowerCase();
		    if (description.contains(filter.toLowerCase())) {  
		      return true;
		    }  
		    return false;
		  }  
		 
		};
		filter.bind(store);
		filter.setWidth("40%");
		
		return filter;
	}
	
	/**
	 * Set up and get the TreePanel to use.
	 * @return
	 */
	public void createTreePanel() {
//		EditorTreeGrid<ModelData> tree = new EditorTreeGrid<ModelData>(store, cm); 
		tree = new MyTreePanel<ModelData>(store);
		tree.setDisplayProperty("description");
		tree.setCheckable(true);
		tree.setCheckNodes(CheckNodes.BOTH);
		tree.setCheckStyle(CheckCascade.CHILDREN);
		tree.setBorders(false);
//		int height = 600;
//		if (isRendered()) { System.out.println("parent isRendered() " + getHeight(true)); height = getHeight(true); };
//		tree.setHeight(height - 200);
//		tree.setAutoWidth(false);
//		tree.setAutoHeight(false);
//		tree.setDeferHeight(true);
		tree.setStateful(true);
		tree.setAutoExpand(true);
		tree.setAutoLoad(true);
//		tree.setStyleAttribute("padding", "20px");	// Just to get some padding between the tree and other elements
//		tree.setIconProvider(iconProvider);
		tree.getStyle().setLeafIcon(IconSupplier.getColorfulIcon(IconSupplier.getServiceIconName()));
//		tree.getStyle().setLeafIcon(IconHelper.create("resources/images/icons/menus/service.png"));
//		tree.expandAll();
		
//		tree.setShadow(true);
//		tree.setToolTip("Check any services to be activated with this product and click the \"Save\" button.");
		tree.setTrackMouseOver(true);

//		tree.getStyle().setLeafIcon(IconHelper.createStyle("icon-music"));
		
		addContextMenu();
		
	}
	
	/**
	 * Set up and get a button bar with buttons to expand or collapse the tree.
	 * @return
	 */
	public ToolBar getExpandCollapseBar() {
		ButtonBar toolbar = new ButtonBar();
		toolbar.setAlignment(HorizontalAlignment.RIGHT);
		
		Button expandButton = new Button("Expand");
		expandButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			   
			@Override
			public void componentSelected(ButtonEvent ce) {
				tree.expandAll();
			}  
		 
		});
		
		Button collapseButton = new Button("Collapse");
		collapseButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			   
			@Override
			public void componentSelected(ButtonEvent ce) {
				tree.collapseAll();
			}  
		 
		});
		
		toolbar.add(expandButton);
		toolbar.add(collapseButton);
		
		return toolbar;
	}
	
	/**
	 * Create and add (to the panel) the buttons to Cancel, Save or Reset the changes to the tree.
	 */
	public void createButtons() {
		
		panel.setButtonAlign(HorizontalAlignment.CENTER);
		
		Button cancelButton = new Button("Cancel");
		IconSupplier.setIcon(cancelButton, IconSupplier.getCancelIconName());
		cancelButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			   
			@Override
			public void componentSelected(ButtonEvent ce) {
				gridLinker.showParent();
			}  
		 
		});
		
		Button saveButton = new Button("Save");
		IconSupplier.setIcon(saveButton, IconSupplier.getSaveIconName());
		saveButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			   
			@Override
			public void componentSelected(ButtonEvent ce) {
				doUpdate();
				gridLinker.showParent();
			}  
		 
		});
		
		Button resetButton = new Button("Reset");
		IconSupplier.setIcon(resetButton, IconSupplier.getResetIconName());
		resetButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			   
			@Override
			public void componentSelected(ButtonEvent ce) {
				refreshTreeData();
			}  
		 
		});
		
		panel.getButtonBar().getParent().addStyleName("x-panel-bc");	//	To get a nice, darker background behind the buttons
		panel.addButton(cancelButton);	
		panel.addButton(saveButton);
		panel.addButton(resetButton);
		
	}
	
	/**
	 * Add drag/drop reorder capability to the tree.
	 */
	public void addReorderCapability() {
		
		new TreePanelDragSource(tree);
//		TreePanelDragSource source = new TreePanelDragSource(tree);
//		Original code rejected drag/drop with first item... why?  Did it mean if there's only one item?  Does it cause problems? 
//		source.addDNDListener(new DNDListener() { 
//		  @Override  
//		  public void dragStart(DNDEvent e) {  
//		    ModelData sel = tree.getSelectionModel().getSelectedItem();
//		    if (sel != null && sel == tree.getStore().getRootItems().get(0)) {  
//		      e.setCancelled(true);
//		      e.getStatus().setStatus(false);
//		      return;
//		    }  
//		    super.dragStart(e);
//		  }  
//		});
		  
		TreePanelDropTarget target = new MyTreePanelDropTarget(tree);
		target.setAllowDropOnLeaf(true);
		target.setAllowSelfAsSource(true);
		target.setFeedback(Feedback.BOTH);
		target.setScrollElementId(panel.getId());
		target.setAutoScroll(true);
		target.setAutoExpand(true);
		
	}
	
	/**
	 * Determine if a particular model represents a folder (versus a service). 
	 * @param item
	 * @return
	 */
	public boolean isFolder(ModelData item) {
		return item.get("type") != null && item.get("type").equals("folder");
	}
	
//	public void addHelpButton() {
//		IconButton helpButton = new IconButton("ExtGWT/images/default/button/arrow.gif");
//		ToolTipConfig config = new ToolTipConfig();
//		config.setTitle("What To Do:");
//		config.setShowDelay(1);
//		config.setText("Check the services which apply to this product and hit save.");
//		panel.getHeader().addTool(helpButton);
//	}
	
	/**
	 * Add a contextual menu to all the user to insert, remove or rename folders.
	 */
	public void addContextMenu() {
		Menu contextMenu = new Menu();
 
		MenuItem insert = new MenuItem();
		insert.setText("Insert Folder");
		insert.setIcon(IconSupplier.getMenuIcon(IconSupplier.getInsertIconName()));
//		insert.setIconStyle("resources/images/icons/menus/insert.png");
		insert.addSelectionListener(new SelectionListener<MenuEvent>() {  
		  public void componentSelected(MenuEvent ce) {  
		    ModelData parent = tree.getSelectionModel().getSelectedItem();
		    if (parent != null) {  
		      Folder child = new Folder("New Folder " + ((int) (Math.random() * 100)));
		      folderRename(child, "Enter a name for the new folder:");
		      if (isFolder(parent)) {
			      store.add(parent, child, false);
			      tree.setExpanded(parent, true);
		      } else
		    	  if (store.getParent(parent) != null)
		    		  store.add(store.getParent(parent), child, false);
		    	  else
		    		  store.add(child, false);
		    }
		  }  
		});
		contextMenu.add(insert);

		MenuItem remove = new MenuItem();
		remove.setText("Remove");
		remove.setIcon(IconSupplier.getMenuIcon(IconSupplier.getRemoveIconName()));
//		remove.setIconStyle("resources/images/icons/menus/remove.png");
		remove.addSelectionListener(new SelectionListener<MenuEvent>() {  
		  public void componentSelected(MenuEvent ce) {  
		    List<ModelData> selected = tree.getSelectionModel().getSelectedItems();
		    for (ModelData sel : selected) { 
		    	if (isFolder(sel)) {
	    			//  Save the children, to restore them after removing the folder
	    			List<ModelData> children = store.getChildren(sel, true);
	    			ModelData parent = store.getParent(sel);
		    		store.remove(sel);
		    		//	Put the children back
		    		for (ModelData child : children) {
		    			if (parent != null)
		    				store.add(parent, child, true);
		    			else
		    				store.add(child, true);
		    		}
		    	} else
		    		MessageBox.alert("Alert", "You cannot remove a service, only a folder.", null);
		    }  
		  }  
		});
		contextMenu.add(remove);

		MenuItem rename = new MenuItem();
		rename.setText("Rename");
		rename.setIcon(IconSupplier.getMenuIcon(IconSupplier.getRenameIconName()));
//		rename.setIconStyle("resources/images/icons/menus/rename.png");
		rename.addSelectionListener(new SelectionListener<MenuEvent>() {
		  public void componentSelected(MenuEvent ce) {
		    List<ModelData> selected = tree.getSelectionModel().getSelectedItems();
		    for (ModelData sel : selected) { 
		    	if (isFolder(sel)) {
		    		folderRename(sel, "Enter a new name for " + sel.get("description") +":");
		    	} else
		    		MessageBox.alert("Alert", "You cannot rename a service, only a folder.", null);
		    }  
		  }  
		});
		contextMenu.add(rename);

		tree.setContextMenu(contextMenu);
	}
	
	/**
	 * Rename a folder using a dialog box to get the new name.
	 * 
	 * @param toRename
	 * @param prompt
	 */
	private void folderRename(final ModelData toRename, String prompt) {
		final MessageBox box = MessageBox.prompt("Folder Name", prompt);
			box.addCallback(new Listener<MessageBoxEvent>() {  
				public void handleEvent(MessageBoxEvent be) {
					if (!be.isCancelled() && be.getValue() != null && be.getValue().length() > 0)
						store.getRecord(toRename).set("description", be.getValue());
				}  
			});
	}
	
	/**
	 * Reload the tree from the database.
	 */
	private void refreshTreeData() {
		if (store != null) {
			//	Note... the store will be *cleared* and replaced when the data is successfully retrieved... don't removeAll() now
			asyncLoad();
		}
	}
	
	/**
	 * Update the database with the current tree settings.
	 */
	private void doUpdate() {		
		updateProductServiceListService.updateProductServiceList(productCode, getOrderedUpdateList(),
				new AsyncCallback<String>() {
					public void onFailure(Throwable caught) {
						// Show the RPC error message to the user
						if (caught instanceof IllegalArgumentException)
							MessageBox.alert("Alert", caught.getMessage(), null);
						else {
							MessageBox.alert("Alert", "Update of product services for " + productCode + " failed unexpectedly.", null);
							System.out.println(caught.getClass().getName());
							System.out.println(caught.getMessage());
						}
					}

					public void onSuccess(String result) {
					}
				});
	}
	
	public List<ProductServiceTreeInstance> getOrderedUpdateList() {
		List<ProductServiceTreeInstance> updateList = new ArrayList<ProductServiceTreeInstance>();
		
		List<ModelData> rootItems = store.getRootItems();
		
		for (ModelData rootItem : rootItems) {
			addAllToList(updateList, rootItem);
		}
		
		return updateList;
	}
	
	public void addAllToList(List<ProductServiceTreeInstance> list, ModelData model) {
		ProductServiceTreeInstance instance = getPstInstance(model );
		list.add( instance );
		addChildrenToParent(instance, model);
//		for (ModelData child : store.getChildren(item))
//			addAllToList(list, child);
	}
	
	public void addChildrenToParent(ProductServiceTreeInstance parentInstance, ModelData parentModel) {
		
		for (ModelData childModel : store.getChildren(parentModel)) {
			ProductServiceTreeInstance child = getPstInstance(childModel);
			parentInstance.addChildInstance(child);
			addChildrenToParent(child, childModel);
		}
	}
	
	public ProductServiceTreeInstance getPstInstance(ModelData item) {
		ProductServiceTreeInstance instance = new ProductServiceTreeInstance();
		instance.setProductCode(productCode);
		instance.setServiceCode(getAsString(item.get("serviceCode")));
		instance.setDescription(getAsString(item.get("description")));
		instance.setSelected(item.get("checked") != null && item.get("checked").equals("checked"));
		instance.setType(getAsString(item.get("type")));
		
		return instance;
	}
	
	public String getAsString(Object value) {
		if (value == null)
			return "";
		else
			return value.toString();
	}
	
	public void awaken() {
	}
	
	public void sleep() {
	}

	protected void asyncLoad() {
		productServiceListService.getProductServices(productCode, null,
				new AsyncCallback<List<ProductServiceTreeInstance>>() {
					public void onFailure(Throwable caught) {
						// Show the RPC error message to the user
						if (caught instanceof IllegalArgumentException)
							MessageBox.alert("Alert", caught.getMessage(), null);
						else {
							MessageBox.alert("Alert", "Get product services for " + productCode + " failed unexpectedly.", null);
							System.out.println(caught.getClass().getName());
							System.out.println(caught.getMessage());
						}
					}

					public void onSuccess(List<ProductServiceTreeInstance> productServices) {
						if (store == null)
							return;
						store.removeAll();
						for (ProductServiceTreeInstance instance : productServices) {
							addInstanceToStore(null, instance);
						}
					//	tree.expandAll();
					}
				});
	}
	
	private void addInstanceToStore(ModelData parent, ProductServiceTreeInstance instance) {
		ModelData item = new BaseModelData();
		item.set("description", instance.getDescription());
		item.set("type", instance.getType());
		item.set("serviceCode", instance.getServiceCode());
		item.set("checked", instance.isSelected() ? "checked" : "");
		if (parent == null)
			store.add(item, false);
		else
			store.add(parent, item, false);
		
		if (instance.getChildInstances() != null)
			for (ProductServiceTreeInstance child : instance.getChildInstances()) {
				addInstanceToStore(item, child);
			}
	}
	
	protected void addHelp(ContentPanel panel) {
		if (helpTextId == null)
			return;
		
		ToolButton helpBtn = new ToolButton("x-tool-help");
//		if (GXT.isAriaEnabled()) {
//			helpBtn.setTitle(GXT.MESSAGES.pagingToolBar_beforePageText());
//		}
		helpBtn.addListener(Events.Select, new Listener<ComponentEvent>() {
			public void handleEvent(ComponentEvent ce) {
				HelpTextDialog htd = new HelpTextDialog(helpTextId);
				htd.show();
			}
		});
		panel.getHeader().addTool(helpBtn);
	}
	
	public void setPanelHeading(String panelHeading) {
		this.panelHeading = panelHeading;
		if (panel != null)
			panel.setHeading(panelHeading);
	}

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	@Override
	public DualEditGridLinker getGridLinker() {
		return gridLinker;
	}

	@Override
	public void setGridLinker(DualEditGridLinker gridLinker) {
		this.gridLinker = gridLinker;
	}

	@Override
	public void prepareForActivation(Object... args) {
		if (args != null && args.length > 0) {
			setProductCode(args [0].toString());
			if (args.length > 1 && args [1] != null)
				setPanelHeading("Product Services: " + args [1]);
		}
		
		refreshTreeData();
	}

}
