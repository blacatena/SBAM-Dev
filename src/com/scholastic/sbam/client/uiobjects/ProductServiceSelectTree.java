package com.scholastic.sbam.client.uiobjects;

import java.util.List;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.data.BaseTreeModel;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.dnd.DND.Feedback;
import com.extjs.gxt.ui.client.dnd.TreePanelDragSource;
import com.extjs.gxt.ui.client.dnd.TreePanelDropTarget;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.DNDEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.Store;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Layout;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.ButtonBar;
import com.extjs.gxt.ui.client.widget.form.StoreFilterField;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel.CheckNodes;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel.TreeNode;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.IsSerializable;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

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

		@SuppressWarnings("rawtypes")
		@Override
		protected void showFeedback(DNDEvent event) {
			final TreeNode overItem = tree.findNode(event.getTarget());
		    if (overItem != null) {
		    	if (overItem instanceof TreeNode) {
		    		if (!isFolder((TreeNode) overItem)) {
		    			clearStyles(event);
		    			return;
		    		}
		    	}
		    }
		    super.showFeedback(event);
		  }
		
		@SuppressWarnings("rawtypes")
		public boolean isFolder(TreeNode item) {
			return isFolder(item.getModel());
		}
		
		public boolean isFolder(ModelData item) {
			return item.get("type") != null && item.get("type").equals("folder");
		}
	}

	
	private String productCode;
	private String panelHeading;
	private DualEditGridLinker gridLinker;

	ContentPanel panel;
	TreePanel<ModelData> tree;
	TreeStore<ModelData> store;

	public ProductServiceSelectTree() {
	}

	@Override  
	protected void onRender(Element parent, int index) {  
		super.onRender(parent, index);  

		setLayout(new FitLayout());
		setBorders(false);
		
		panel = new ContentPanel();
		
		Layout panelLayout = new FitLayout();
		panel.setLayout(panelLayout);
		panel.setBorders(false);
		panel.setHeading(panelHeading);
		panel.setHeaderVisible(true);
		
		LayoutContainer inset = new LayoutContainer(new FlowLayout());
		inset.setStyleAttribute("padding", "10px");

		createTreeStore();
		inset.add(getExpandCollapseBar());
		inset.add(getFilter());
		inset.add(getTreePanel());
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

		ModelData item1 = new BaseModelData();
		item1.set("description", "Child Item 1");
		store.add(item1, false);
		store.add(new Folder("My First Folder"), true);
		store.add(new Folder("My Second Folder"), true);
		Folder folder3 = new Folder("My Third Folder");
		ModelData item = new BaseModelData();
		item.set("description", "Child Item 4");
		folder3.add(item);
		item = new BaseModelData();
		item.set("description", "Child Item 5 asdfsddsfsdfdsfssfdsfdf");
		folder3.add(item);
		store.add(folder3, true);
		
//		store.add(folder, true);
		
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
	public TreePanel<ModelData> getTreePanel() {
//		EditorTreeGrid<ModelData> tree = new EditorTreeGrid<ModelData>(store, cm); 
		tree = new MyTreePanel<ModelData>(store);
		tree.setDisplayProperty("description");
		tree.setCheckable(true);
		tree.setCheckNodes(CheckNodes.LEAF);
		tree.setBorders(false);
		tree.setAutoWidth(true);
		tree.setAutoHeight(true);
		tree.setDeferHeight(true);
		tree.setStateful(true);
		tree.setAutoExpand(true);
		tree.setAutoLoad(true);
		tree.setStyleAttribute("padding", "10px");
//		tree.expandAll();
		
//		tree.setShadow(true);
//		tree.setToolTip("Check any services to be activated with this product and click the \"Save\" button.");
		tree.setTrackMouseOver(true);

//		tree.getStyle().setLeafIcon(IconHelper.createStyle("icon-music"));
		
		addContextMenu();
		
		return tree;
		
	}
	
	/**
	 * Set up and get a button bar with buttons to expand or collapse the tree.
	 * @return
	 */
	public ToolBar getExpandCollapseBar() {
		ButtonBar toolbar = new ButtonBar();
		toolbar.setAlignment(HorizontalAlignment.CENTER);
		
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
		cancelButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			   
			@Override
			public void componentSelected(ButtonEvent ce) {
				gridLinker.showParent();
			}  
		 
		});
		
		Button saveButton = new Button("Save");
		saveButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			   
			@Override
			public void componentSelected(ButtonEvent ce) {
				doUpdate();
				gridLinker.showParent();
			}  
		 
		});
		
		Button resetButton = new Button("Reset");
		saveButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			   
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
//		insert.setIcon(Resources.ICONS.add());  
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
//		remove.setIcon(Resources.ICONS.delete());  
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
//		remove.setIcon(Resources.ICONS.rename());  
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
		
	}
	
	/**
	 * Update the database with the current tree settings.
	 */
	private void doUpdate() {
		
	}
	
	public void awaken() {
	}
	
	public void sleep() {
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
