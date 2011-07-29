package com.scholastic.sbam.client.uiobjects.uireports;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.data.BaseTreeModel;
import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.dnd.DND.Feedback;
import com.extjs.gxt.ui.client.dnd.TreeGridDragSource;
import com.extjs.gxt.ui.client.dnd.TreeGridDropTarget;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.DNDEvent;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.Record;
import com.extjs.gxt.ui.client.store.Store;
import com.extjs.gxt.ui.client.store.StoreEvent;
import com.extjs.gxt.ui.client.store.StoreListener;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.ButtonBar;
import com.extjs.gxt.ui.client.widget.button.IconButton;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.StoreFilterField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.grid.CellEditor;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.EditorGrid.ClicksToEdit;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.grid.GridSelectionModel;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.tips.ToolTipConfig;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.extjs.gxt.ui.client.widget.treegrid.EditorTreeGrid;
import com.extjs.gxt.ui.client.widget.treegrid.TreeGrid;
import com.extjs.gxt.ui.client.widget.treegrid.TreeGridCellRenderer;
import com.extjs.gxt.ui.client.widget.treegrid.TreeGrid.TreeNode;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.IsSerializable;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.datepicker.client.CalendarUtil;
import com.scholastic.sbam.client.services.DuplicateSnapshotService;
import com.scholastic.sbam.client.services.DuplicateSnapshotServiceAsync;
import com.scholastic.sbam.client.services.SnapshotListService;
import com.scholastic.sbam.client.services.SnapshotListServiceAsync;
import com.scholastic.sbam.client.services.UpdateSnapshotBasicsService;
import com.scholastic.sbam.client.services.UpdateSnapshotBasicsServiceAsync;
import com.scholastic.sbam.client.services.UpdateSnapshotNoteService;
import com.scholastic.sbam.client.services.UpdateSnapshotNoteServiceAsync;
import com.scholastic.sbam.client.services.UpdateSnapshotService;
import com.scholastic.sbam.client.services.UpdateSnapshotServiceAsync;
import com.scholastic.sbam.client.services.UpdateSnapshotTreeService;
import com.scholastic.sbam.client.services.UpdateSnapshotTreeServiceAsync;
import com.scholastic.sbam.client.uiobjects.fields.NotesIconButtonField;
import com.scholastic.sbam.client.uiobjects.foundation.AppSleeper;
import com.scholastic.sbam.client.util.IconSupplier;
import com.scholastic.sbam.client.util.UiConstants;
import com.scholastic.sbam.shared.objects.SimpleModelDataKeyProvider;
import com.scholastic.sbam.shared.objects.SnapshotInstance;
import com.scholastic.sbam.shared.objects.SnapshotTreeInstance;
import com.scholastic.sbam.shared.objects.UpdateResponse;
import com.scholastic.sbam.shared.util.AppConstants;

public class SnapshotSelectorCard extends LayoutContainer implements AppSleeper, SnapshotReflection {

	protected int							folderId			=	0;
	protected boolean						allowReorganize		=	true;
	protected String						snapshotTypeFilter	=	"";
	
	protected String						panelHeading		=	"Snapshot Selector";

	protected ContentPanel 					panel;
	protected EditorTreeGrid<ModelData> 	treeGrid;
	protected TreeStore<ModelData> 			store;
	
	protected SnapshotParentCardPanel		parentCardPanel;
	
	protected final SnapshotListServiceAsync 			snapshotListService = GWT.create(SnapshotListService.class);
	protected final UpdateSnapshotBasicsServiceAsync 	updateSnapshotBasicsService = GWT.create(UpdateSnapshotBasicsService.class);
	protected final UpdateSnapshotServiceAsync 			updateSnapshotService = GWT.create(UpdateSnapshotService.class);
	protected final UpdateSnapshotNoteServiceAsync 		updateSnapshotNoteService = GWT.create(UpdateSnapshotNoteService.class);
	protected final UpdateSnapshotTreeServiceAsync		updateSnapshotTreeService = GWT.create(UpdateSnapshotTreeService.class);
	protected final DuplicateSnapshotServiceAsync 		duplicateSnapshotService = GWT.create(DuplicateSnapshotService.class);
	
	protected final ToolTipConfig servicesTip	=	getIconButtonToolTip("Use this button to restrict the services selected for this snapshot.");
	protected final ToolTipConfig customersTip	=	getIconButtonToolTip("Use this button to restrict the customers selected for this snapshot.");
	protected final ToolTipConfig termsTip		=	getIconButtonToolTip("Use this button to restrict the terms selected for this snapshot.");
	protected final ToolTipConfig viewDataTip	=	getIconButtonToolTip("Use this button to view data for this snapshot.");
	protected final ToolTipConfig excelTip		=	getIconButtonToolTip("Use this button to download the data for this snapshot as an excel spreadsheet.");
	
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
		protected static final long serialVersionUID = 1L;

		public Folder() {
			set("type", "folder");
		}

		public Folder(String description) {
			this();
			folderId--;
			set("snapshotId", folderId);
			set("description", description);
			set("status", AppConstants.STATUS_NEW);
			set("statusDescription", SnapshotTreeInstance.getStatusDescription(AppConstants.STATUS_NEW));
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
			return "Folder: " + getDescription();
		}
	};
	
	/**
	 * Customized version to show folder icons for folders which are leafs (could have used ModelIconProvider, but didn't).
	 * 
	 * @author Bob Lacatena
	 *
	 * @param <M>
	 */
	public class MyTreeGrid<M extends ModelData> extends TreeGrid<M> {

		public MyTreeGrid(TreeStore<M> store, ColumnModel cm) {
			super(store, cm);
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
		
		protected boolean isFolder(ModelData item) {
			return item.get("type") != null && item.get("type").equals("folder");
		}
		
	}
	
	/**
	 * Customized version to reject drops on non-Folder items
	 * @author Bob Lacatena
	 *
	 */
	public class MyTreeGridDropTarget extends TreeGridDropTarget {

		public MyTreeGridDropTarget(TreeGrid<ModelData> tree) {
			super(tree);
		}


		/**
		 * Overridden to disallow drops onto non-folder items, whether they are leafs or not.
		 */
		@SuppressWarnings("rawtypes")
		@Override
		protected void handleAppend(DNDEvent event, final TreeNode item) {
			final TreeNode overItem = treeGrid.findNode(event.getTarget());
		    if (overItem != null) {
		    	if (overItem instanceof TreeNode) {
		    		if (!isFolder((TreeNode) overItem)) {
		    			clearStyle(overItem);
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


	public SnapshotSelectorCard() {
	}
	
	public SnapshotSelectorCard(boolean allowReorganize) {
		this();
		this.allowReorganize = allowReorganize;
	}
	
	public SnapshotSelectorCard(String snapshotTypeFilter) {
		this();
		this.snapshotTypeFilter = snapshotTypeFilter;
	}
	
	public SnapshotSelectorCard(String snapshotTypeFilter, boolean allowReorganize) {
		this();
		this.snapshotTypeFilter = snapshotTypeFilter;
		this.allowReorganize = allowReorganize;
	}

	@Override  
	protected void onRender(Element parent, int index) {  
		super.onRender(parent, index);

		setLayout(new FitLayout());
		setBorders(false);
		
		panel = new ContentPanel(new FitLayout());
		panel.setBorders(false);
		IconSupplier.setIcon(panel, IconSupplier.getSnapshotIconName());
		panel.setHeading(panelHeading);
		panel.setHeaderVisible(true);
		panel.addStyleName("sbam-report-body");
		
		createTreeStore();
		StoreFilterField<ModelData> filter = getFilter();
		ToolBar toolbar = getButtonsBar();
		createTreeGrid();
		
		final LayoutContainer tools = new LayoutContainer(new RowLayout(Orientation.HORIZONTAL));
		tools.setHeight(30);
		tools.add(filter,  new RowData(200, -1, new Margins(2)));
		tools.add(toolbar, new RowData(550, 30, new Margins(2)));
		
		LayoutContainer inset = new LayoutContainer() {
//			@Override
//			protected void onResize(int width, int height) {
//				if (treeGrid != null) {
//					if (tools.isRendered()) {
//						treeGrid.setHeight(height - tools.getHeight());
//					} else {
//						treeGrid.setHeight(height - 40);
//					}
//					treeGrid.setWidth(width - 10);
//				}
//				super.onResize(width, height);	
//			}
		};

//		inset.add(tools);
		
		inset.setLayout(new FitLayout());
		inset.add(treeGrid);		//	inset.add(tree);
		
		inset.addStyleName("sbam-report-body");
		
		panel.setTopComponent(tools);
		panel.add(inset);
		
		add(panel);
	}
	
	/**
	 * Create the TreeStore from the data.
	 */
	public void createTreeStore() {
		store = new TreeStore<ModelData>();
		
		store.setKeyProvider(new SimpleModelDataKeyProvider("snapshotId"));
		
		store.addStoreListener(new StoreListener<ModelData>() {
			@Override
			public void storeUpdate(StoreEvent<ModelData> se) {
				doUpdateSnapshots();	//	Apply name or status changes to snapshots
			}
			
			//	Other events available
//			@Override
//			public void storeDataChanged(StoreEvent<ModelData> se) {
//				System.out.println("storeDataChanged");
//			}
//			@Override
//			public void storeAdd(StoreEvent<ModelData> se) {
//				System.out.println("storeAdd");
//			}
//			@Override
//			public void storeRemove(StoreEvent<ModelData> se) {
//				System.out.println("storeRemove");
//			}
//			@Override
//			public void storeSort(StoreEvent<ModelData> se) {
//				System.out.println("storeSort");
//			}
//			@Override
//			public void storeFilter(StoreEvent<ModelData> se) {
//				System.out.println("storeFilter");
//			}
		});
		
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
		filter.setWidth(200);
		
		return filter;
	}
	
	public void createTreeGrid() {
		treeGrid = new EditorTreeGrid<ModelData>(store, getColumnModel()) {  	
				/**
				 * Overridden to return a folder icon for a leaf which is defined as a folder.
				 */
				@Override
				protected AbstractImagePrototype calculateIconStyle(ModelData model) {
					if (isLeaf(model) && isFolder(model)) {
						return getStyle().getNodeOpenIcon();
					}
					return super.calculateIconStyle(model);
				}
			
		      @Override  
		      protected boolean hasChildren(ModelData m) {  
		        if (m instanceof Folder) {  
		          return true;  
		        }  
		        return super.hasChildren(m);  
		      }  
		    };  
	    treeGrid.setBorders(true);  
	    treeGrid.getStyle().setLeafIcon(IconSupplier.getColorfulIcon(IconSupplier.getSnapshotIconName()));  
		treeGrid.setAutoExpandColumn("description");  
	    treeGrid.setTrackMouseOver(true);
//	    treeGrid.setHeight(500);
	    treeGrid.setAutoHeight(false);
	    treeGrid.setClicksToEdit(ClicksToEdit.TWO);
	    treeGrid.setSelectionModel(new GridSelectionModel<ModelData>());
	    
	    new TreeGridDragSource(treeGrid);  
	  
	    MyTreeGridDropTarget target = new MyTreeGridDropTarget(treeGrid) {
	    	@Override
	    	public void onDragDrop(DNDEvent event) {
	    		super.onDragDrop(event);
	    		doUpdateTreeStructure();
	    	}
	    };  
	    target.setAllowSelfAsSource(true); 
	    target.setFeedback(Feedback.BOTH);
		target.setAllowDropOnLeaf(true);
		target.setAutoScroll(true);
		target.setAutoExpand(true);
		target.setAutoExpandDelay(100);
		target.setAddChildren(true);
	    
	    addContextMenu();
	    
//	    RowEditor<ModelData> editor = new RowEditor<ModelData>(); 
//	    editor.setClicksToEdit(ClicksToEdit.TWO);
//	    treeGrid.addPlugin(editor);  
	}
	
	public ColumnModel getColumnModel() {
		ColumnConfig id = new ColumnConfig("snapshotId",	"Id",  120);	//	Not snapshot.snapshotId, because folders don't have a snapshot
	    id.setRenderer(new TreeGridCellRenderer<ModelData>() {
	    	@Override
	    	protected String getText(TreeGrid<ModelData> grid, ModelData model, String property, int rowIndex, int colIndex) {
	    	    if (model.get(property) instanceof Integer && ((Integer) model.get(property)) <= 0)
	    	    	return "";
	    		return String.valueOf(model.get(property));
	    	}
	    });
	    
	    ColumnConfig name = new ColumnConfig("description",	"Name", 150);		//	Not snapshot.snapshotName, because folders don't have a snapshot
	    name.setEditor(new CellEditor(new TextField<String>()) );
//	    {
//	    	@Override
//	    	protected void completeEdit(boolean remainVisible) {
//	    		super.completeEdit(remainVisible);
//	    		System.out.println("Edit completed");	// Might use this to trigger name change saves, but for now, the store is detecting them more efficiently
//	    	}
//	    });
	    
	    ColumnConfig date = new ColumnConfig("snapshot.snapshotTaken",		"Date Snapshot Taken",		120);
	    date.setDateTimeFormat(UiConstants.APP_DATE_PLUS_TIME_FORMAT);

	    ColumnConfig rows = new ColumnConfig("snapshot.snapshotRows",		"Entries",					80);
	    rows.setNumberFormat(UiConstants.INTEGER_FORMAT);
	    rows.setRenderer(new GridCellRenderer<ModelData>() {  
				  public Object render(ModelData model, String property, ColumnData config, int rowIndex, int colIndex,  
				      ListStore<ModelData> store, Grid<ModelData> grid) {
					  if (model.get(property) == null || model.get(property).toString().equals("0"))
						  return "";
					  return model.get(property);
				  }  
				});
	    
	    ColumnConfig expi = new ColumnConfig("snapshot.expireDatetime",		"Expiration Date",			120);
	    expi.setDateTimeFormat(UiConstants.APP_DATE_LONG_FORMAT);
	    expi.setEditor(new CellEditor(new DateField()));

	    ColumnConfig user = new ColumnConfig("snapshot.createDisplayName",	"Creator",					100);
	    ColumnConfig stat = new ColumnConfig("statusDescription",			"Status",					60);
	    
	    ColumnConfig term = new ColumnConfig("termButton",					"",							30);
	    term.setRenderer(getTermButtonRenderer());
	    
	    ColumnConfig srvc = new ColumnConfig("serviceButton",				"",							30);
	    srvc.setRenderer(getServiceButtonRenderer());
	    
	    ColumnConfig cust = new ColumnConfig("customerButton",				"",							30);
	    cust.setRenderer(getCustomerButtonRenderer());
	    
	    ColumnConfig data = new ColumnConfig("viewDataButton",				"",							30);
	    data.setRenderer(getViewDataButtonRenderer());
	    
	    ColumnConfig excl = new ColumnConfig("excelButton",					"",							30);
	    excl.setRenderer(getExcelButtonRenderer());
	    
	    ColumnConfig note = new ColumnConfig("notesButton",					"",							30);
	    note.setRenderer(getNoteButtonRenderer());
	    
	    ColumnModel cm = new ColumnModel(Arrays.asList(id, name, date, rows, expi, user, stat, term, srvc, cust, data, excl, note));
	    
	    return cm;
	}
	
	/**
	 * Set up and get a button bar with buttons to expand or collapse the tree.
	 * @return
	 */
	public ToolBar getButtonsBar() {
		ButtonBar toolbar = new ButtonBar();
		toolbar.setAlignment(HorizontalAlignment.RIGHT);
		
		Button newSnapshotButton = new Button("New Snapshot");
		IconSupplier.forceIcon(newSnapshotButton, IconSupplier.getSnapshotAddIconName());
		newSnapshotButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			   
			@Override
			public void componentSelected(ButtonEvent ce) {
				createNewSnapshot();
			}  
		 
		});
		
		Button newFolderButton = new Button("New Folder");
		IconSupplier.forceIcon(newFolderButton, IconSupplier.getFolderAddIconName());
		newFolderButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			   
			@Override
			public void componentSelected(ButtonEvent ce) {
				createNewFolder();
			}  
		 
		});
		
		Button expandButton = new Button("Expand");
		IconSupplier.forceIcon(expandButton, IconSupplier.getExpandIconName());
		expandButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			   
			@Override
			public void componentSelected(ButtonEvent ce) {
				treeGrid.expandAll();
			}  
		 
		});
		
		Button collapseButton = new Button("Collapse");
		IconSupplier.forceIcon(collapseButton, IconSupplier.getCollapseIconName());
		collapseButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			   
			@Override
			public void componentSelected(ButtonEvent ce) {
				treeGrid.collapseAll();
			}  
		 
		});
		
		//	This button allowed the user to explicitly save a modified tree structure... now obsolete, because update messages are sent automatically
//		Button saveButton = new Button("Save");
//		IconSupplier.forceIcon(saveButton, IconSupplier.getSaveIconName());
//		saveButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
//			   
//			@Override
//			public void componentSelected(ButtonEvent ce) {
//				doUpdateTreeStructure();
//			}  
//		 
//		});
		
		Button resetButton = new Button("Refresh");
		IconSupplier.forceIcon(resetButton, IconSupplier.getResetIconName());
		resetButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			   
			@Override
			public void componentSelected(ButtonEvent ce) {
				refreshTreeData();
			}  
		 
		});

		toolbar.add(newSnapshotButton);
		toolbar.add(newFolderButton);
		toolbar.add(new SeparatorToolItem());
		toolbar.add(expandButton);
		toolbar.add(collapseButton);
		toolbar.add(new SeparatorToolItem());
//		toolbar.add(saveButton);
		toolbar.add(resetButton);
		
		return toolbar;
	}
	
	/**
	 * Determine if a particular model represents a folder (versus a service). 
	 * @param item
	 * @return
	 */
	public boolean isFolder(ModelData item) {
		return item != null && item.get("type") != null && item.get("type").equals("folder");
	}
	
	/**
	 * Add a contextual menu to all the user to insert, remove or rename folders.
	 */
	public void addContextMenu() {
		if (!allowReorganize)
			return;
		
		Menu contextMenu = new Menu();

		MenuItem create = new MenuItem();
		create.setText("Create Snapshot");
		create.setIcon(IconSupplier.getMenuIcon(IconSupplier.getSnapshotAddIconName()));
		create.addSelectionListener(new SelectionListener<MenuEvent>() {  
		  public void componentSelected(MenuEvent ce) {
			  createNewSnapshot();
		  }  
		});
		contextMenu.add(create);
 
		MenuItem insert = new MenuItem();
		insert.setText("Create Folder");
		insert.setIcon(IconSupplier.getMenuIcon(IconSupplier.getFolderAddIconName()));
		insert.addSelectionListener(new SelectionListener<MenuEvent>() {  
			  public void componentSelected(MenuEvent ce) { 
				  createNewFolder();
			  }
		});
		contextMenu.add(insert);

		MenuItem remove = new MenuItem();
		remove.setText("Remove");
		remove.setIcon(IconSupplier.getMenuIcon(IconSupplier.getRemoveIconName()));
		remove.addSelectionListener(new SelectionListener<MenuEvent>() {  
		  public void componentSelected(MenuEvent ce) {
			  removeSelectedItems();
		  }  
		});
		contextMenu.add(remove);

		MenuItem restore = new MenuItem();
		restore.setText("Restore Snapshot");
		restore.setIcon(IconSupplier.getMenuIcon(IconSupplier.getCancelIconName()));
		restore.addSelectionListener(new SelectionListener<MenuEvent>() {  
		  public void componentSelected(MenuEvent ce) {
			  restoreSnapshot();
		  }  
		});
		contextMenu.add(restore);

		MenuItem duplicate = new MenuItem();
		duplicate.setText("Duplicate Snapshot");
		duplicate.setIcon(IconSupplier.getMenuIcon(IconSupplier.getSnapshotCopyIconName()));
		duplicate.addSelectionListener(new SelectionListener<MenuEvent>() {  
		  public void componentSelected(MenuEvent ce) {
			  doDuplicateSnapshots();
		  }  
		});
		contextMenu.add(duplicate);

		if (treeGrid != null)
			treeGrid.setContextMenu(contextMenu);
	}
	
	/**
	 * Reload the tree from the database.
	 */
	protected void refreshTreeData() {
		if (store != null) {
			//	Note... the store will be *cleared* and replaced when the data is successfully retrieved... don't removeAll() now
			asyncLoad();
		}
	}
	
	protected void createNewFolder() { 
		  ModelData parent = treeGrid.getSelectionModel().getSelectedItem();
		     
	      Folder child = new Folder("New Folder " + UiConstants.formatDate(new Date())); //((int) (Math.random() * 100)));
	      // folderRename(child, "Enter a name for the new folder:");
	      if (parent != null && isFolder(parent)) {
		      store.insert(parent, child, 0, false);
		      treeGrid.setExpanded(parent, true);
	      } else 
	    	  if (parent != null && store.getParent(parent) != null)
	    		  store.insert(store.getParent(parent), child, store.indexOf(parent), false);
	    	  else if (parent != null)
	    		  store.insert(child, store.indexOf(parent), false);
	    	  else
	    		  store.insert(child, 0, false);
	}
	
	protected void removeSelectedItems() {
	    List<ModelData> selected = treeGrid.getSelectionModel().getSelectedItems();
	    if (selected == null || selected.size() == 0) {
	    	MessageBox.alert("Alert", "Select a row or rows to be deleted.", null);
	    	return;
	    }
	    
	    int foldersDeleted = 0;
	    int snapshotsDeleted = 0;
	    for (ModelData sel : selected) { 
	    	if (isFolder(sel)) {
	    		foldersDeleted++;
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
	    	} else {
	    		snapshotsDeleted++;
	    		Record record = store.getRecord(sel);
	    		record.beginEdit();
	    		record.set("status", AppConstants.STATUS_DELETED);
	    		record.set("statusDescription", SnapshotTreeInstance.getStatusDescription(AppConstants.STATUS_DELETED));
	    		record.endEdit();
	    	}
	    }
	    if (snapshotsDeleted > 0)
	    	doUpdateSnapshots();
	    if (foldersDeleted > 0)
	    	doUpdateTreeStructure();
	}
	
	protected void restoreSnapshot() {
	    List<ModelData> selected = treeGrid.getSelectionModel().getSelectedItems();
	    
	    int count = 0;
	    for (ModelData sel : selected) { 
	    	if (!isFolder(sel) && AppConstants.STATUS_DELETED == sel.get("status").toString().charAt(0)) {
	    		count++;
	    		Record record = store.getRecord(sel);
	    		record.beginEdit();
	    		record.set("status", AppConstants.STATUS_ACTIVE);
	    		record.set("statusDescription", SnapshotTreeInstance.getStatusDescription(AppConstants.STATUS_ACTIVE));
	    		record.endEdit();
	    	}
	    }  
	    if (count == 0) {
	    	MessageBox.alert("Alert", "Select a deleted snapshot or snapsnots to be restored.", null);
	    	return;
	    }
	    doUpdateSnapshots();
	}
	
	/**
	 * Update a snapshot name or status... note that this is triggered automatically by an update to the store
	 */
	protected void doDuplicateSnapshots() {
		int snapshotsSelected = 0;
	    List<ModelData> selected = treeGrid.getSelectionModel().getSelectedItems();
	    
	    if (selected == null || selected.size() == 0) {
	    	MessageBox.alert("Alert", "Select a snapshot or snapsnots to be duplicated.", null);
	    	return;
	    }
	    
		for (ModelData snapshot : selected) {
			if (!isFolder(snapshot)) {
				snapshotsSelected++;
				doDuplicateSnapshot(snapshot);
			}
		}
		if (snapshotsSelected == 0) {
	    	MessageBox.alert("Alert", "Folders cannot be duplicated.", null);
	    	return;
		}
	}
	
	/**
	 * Update a snapshot name or status... note that this is triggered automatically by an update to the store
	 */
	protected void doUpdateSnapshots() {
		int foldersRenamed = 0;
		for (Record record : store.getModifiedRecords()) {
			if (record.get("snapshot") != null) {
				SnapshotInstance snapshot = (SnapshotInstance) ((BeanModel) record.get("snapshot")).getBean() ;
				snapshot.setSnapshotName(record.get("description").toString());
			}
			if (record.get("snapshotId") != null) {
				int snapshotId = (Integer) record.get("snapshotId");
				if (snapshotId > 0) {
					doUpdateSnapshot(record);
				} else {
					foldersRenamed++;
					record.commit(true);
				}
			}
		}
		if (foldersRenamed > 0)
			doUpdateTreeStructure();
	}
	
//	public void createNewSnapshot() {
//	    ModelData parent = treeGrid.getSelectionModel().getSelectedItem();
//    	SnapshotTreeInstance newTreeInstance = new SnapshotTreeInstance(SnapshotInstance.getNewInstance());
//	    if (isFolder(parent)) {
//	    	addInstanceToStore(parent, newTreeInstance, true);
//	    	treeGrid.setExpanded(parent, true);
//	    } else
//	    	addInstanceToStore(null, newTreeInstance, true);
//	}
	
	/**
	 * Update the database with the current tree settings.
	 */
	protected void createNewSnapshot() {
	    final ModelData parent = treeGrid.getSelectionModel().getSelectedItem();
		
	    Date expireDate = new Date();
	    CalendarUtil.addDaysToDate(expireDate, 60);
	    
		SnapshotInstance instance = new SnapshotInstance();
		instance.setSnapshotId(0);
		instance.setSnapshotName("Snapshot " + UiConstants.formatDate(new Date()));
		instance.setSnapshotType(snapshotTypeFilter);
		instance.setExpireDatetime(expireDate);
		instance.setNote("");
		instance.setProductServiceType(SnapshotInstance.SERVICE_TYPE);
		instance.setUcnType(SnapshotInstance.BILL_UCN_TYPE);
		instance.setStatus(AppConstants.STATUS_ACTIVE);
		instance.setNewRecord(true);
		
		updateSnapshotService.updateSnapshot(instance,
				new AsyncCallback<UpdateResponse<SnapshotInstance>>() {
					public void onFailure(Throwable caught) {
						// Show the RPC error message to the user
						if (caught instanceof IllegalArgumentException)
							MessageBox.alert("Alert", caught.getMessage(), null);
						else {
							MessageBox.alert("Alert", "Create snapshot failed unexpectedly.", null);
							System.out.println(caught.getClass().getName());
							System.out.println(caught.getMessage());
						}
					}

					public void onSuccess(UpdateResponse<SnapshotInstance> result) {
				    	SnapshotTreeInstance newTreeInstance = new SnapshotTreeInstance(result.getInstance());
					    if (isFolder(parent)) {
					    	addInstanceToStore(parent, newTreeInstance, true);
					    	treeGrid.setExpanded(parent, true);
					    } else
					    	addInstanceToStore(null, newTreeInstance, true);
					}
				});
	}
	
	/**
	 * Update the database with the current tree settings.
	 */
	protected void doUpdateSnapshot(final Record record) {
		char	snapshotStatus	= (Character) record.get("status");
		String	snapshotName	= (String) record.get("description");
		Date	expireDatetime	= (Date) record.get("snapshot.expireDatetime");
		if (record.get("snapshotId") != null) {
			final int snapshotId = (Integer) record.getModel().get("snapshotId");
			if (snapshotId > 0) {
				updateSnapshotBasicsService.updateSnapshotBasics(snapshotId, snapshotName, snapshotStatus, expireDatetime, null,
						new AsyncCallback<UpdateResponse<SnapshotInstance>>() {
							public void onFailure(Throwable caught) {
								// Show the RPC error message to the user
								if (caught instanceof IllegalArgumentException)
									MessageBox.alert("Alert", caught.getMessage(), null);
								else {
									MessageBox.alert("Alert", "Update snapshot " + snapshotId + " failed unexpectedly.", null);
									System.out.println(caught.getClass().getName());
									System.out.println(caught.getMessage());
								}
							}
		
							public void onSuccess(UpdateResponse<SnapshotInstance> result) {
								record.commit(false);	// commit the change
							}
						});
			} else {
				System.out.println("Tried to update snapshot with zero ID in " + getClass().getName());
			}
		} else {
			System.out.println("Tried to update non-snapshot record in " + getClass().getName());
		}
	}
	
	/**
	 * Update the database with the current tree settings.
	 */
	protected void doDuplicateSnapshot(final ModelData model) {
		if (model.get("snapshotId") != null) {
			final int snapshotId = (Integer) model.get("snapshotId");
			if (snapshotId > 0) {
				duplicateSnapshotService.duplicateSnapshot(snapshotId, null,
						new AsyncCallback<UpdateResponse<SnapshotInstance>>() {
							public void onFailure(Throwable caught) {
								// Show the RPC error message to the user
								if (caught instanceof IllegalArgumentException)
									MessageBox.alert("Alert", caught.getMessage(), null);
								else {
									MessageBox.alert("Alert", "Duplicate snapshot " + snapshotId + " failed unexpectedly.", null);
									System.out.println(caught.getClass().getName());
									System.out.println(caught.getMessage());
								}
							}
		
							public void onSuccess(UpdateResponse<SnapshotInstance> result) {
								SnapshotTreeInstance treeInstance = new SnapshotTreeInstance(result.getInstance());
								addInstanceToStore(store.getParent(model), treeInstance, true);
//								if (store.getParent(model) != null)
//									store.insert(store.getParent(model), item, 0, false);
//								else
//									store.insert(item, store.indexOf(model), false);
							}
						});
			} else {
				System.out.println("Tried to duplicate snapshot with zero ID in " + getClass().getName());
			}
		} else {
			System.out.println("Tried to duplicate non-snapshot record in " + getClass().getName());
		}
	}
	
	/**
	 * Update the database with the current tree settings.
	 */
	protected void doUpdateTreeStructure() {		
		updateSnapshotTreeService.updateSnapshotTree(snapshotTypeFilter, getOrderedUpdateList(),
				new AsyncCallback<String>() {
					public void onFailure(Throwable caught) {
						// Show the RPC error message to the user
						if (caught instanceof IllegalArgumentException)
							MessageBox.alert("Alert", caught.getMessage(), null);
						else {
							MessageBox.alert("Alert", "Update of snapshot tree for " + snapshotTypeFilter + " failed unexpectedly.", null);
							System.out.println(caught.getClass().getName());
							System.out.println(caught.getMessage());
						}
					}

					public void onSuccess(String result) {
					}
				});
	}
	
	public List<SnapshotTreeInstance> getOrderedUpdateList() {
		List<SnapshotTreeInstance> updateList = new ArrayList<SnapshotTreeInstance>();
		
		List<ModelData> rootItems = store.getRootItems();
		
		for (ModelData rootItem : rootItems) {
			addAllToList(updateList, rootItem);
		}
		
		return updateList;
	}
	
	public void addAllToList(List<SnapshotTreeInstance> list, ModelData model) {
		SnapshotTreeInstance instance = getSnapshotTreeInstance(model );
		list.add( instance );
		addChildrenToParent(instance, model);
//		for (ModelData child : store.getChildren(item))
//			addAllToList(list, child);
	}
	
	public void addChildrenToParent(SnapshotTreeInstance parentInstance, ModelData parentModel) {
		for (ModelData childModel : store.getChildren(parentModel)) {
			SnapshotTreeInstance child = getSnapshotTreeInstance(childModel);
			parentInstance.addChildInstance(child);
			addChildrenToParent(child, childModel);
		}
	}
	
	public SnapshotTreeInstance getSnapshotTreeInstance(ModelData item) {
		SnapshotTreeInstance instance = new SnapshotTreeInstance();
		if (item.get("snapshot") != null)
			instance.setSnapshot( (SnapshotInstance) ((BeanModel) item.get("snapshot")).getBean() );
		instance.setDescription(getAsString(item.get("description")));
		instance.setStatus(getAsChar(item.get("status")));
		instance.setType(getAsString(item.get("type")));
		
		return instance;
	}
	
	public String getAsString(Object value) {
		if (value == null)
			return "";
		else
			return value.toString();
	}
	
	public char getAsChar(Object value) {
		if (value == null)
			return 0;
		else if (value instanceof Character) {
			return ((Character) value).charValue();
		} else {
			String strVal = value.toString();
			if (strVal.length() > 0)
				return strVal.charAt(0);
			return 0;
		}
	}
	
	public void awaken() {
	}
	
	public void sleep() {
	}

	protected void asyncLoad() {
		snapshotListService.getSnapshots(snapshotTypeFilter, null,
				new AsyncCallback<List<SnapshotTreeInstance>>() {
					public void onFailure(Throwable caught) {
						// Show the RPC error message to the user
						if (caught instanceof IllegalArgumentException)
							MessageBox.alert("Alert", caught.getMessage(), null);
						else {
							MessageBox.alert("Alert", "Get snapshot services for " + snapshotTypeFilter + " failed unexpectedly.", null);
							System.out.println(caught.getClass().getName());
							System.out.println(caught.getMessage());
						}
					}

					public void onSuccess(List<SnapshotTreeInstance> productServices) {
						if (store == null)
							return;
						store.removeAll();
						for (SnapshotTreeInstance instance : productServices) {
							addInstanceToStore(null, instance);
						}
					//	tree.expandAll();
					}
				});
	}
	
	protected void addInstanceToStore(ModelData parent, SnapshotTreeInstance instance) {
		addInstanceToStore(parent, instance, false);
	}
	
	protected void addInstanceToStore(ModelData parent, SnapshotTreeInstance instance, boolean insertFirst) {
		ModelData item = new BaseModelData();
		item.set("description",			instance.getDescription());
		item.set("type",				instance.getType());
		if (isFolder(item)) {
			folderId--;
			item.set("snapshotId",		folderId);
		} else {
			item.set("snapshotId",		instance.getSnapshot().getSnapshotId());
		}
		item.set("expireDatetime",		instance.getSnapshot().getExpireDatetime());
		item.set("status",				instance.getStatus());
		item.set("statusDescription",	instance.getStatusDescription());
		item.set("note",				instance.getNote());
		item.set("snapshot",			SnapshotInstance.obtainModel(instance.getSnapshot()));
		item.set("checked",				instance.isSelected() ? "checked" : "");
		if (instance.getStatus() == AppConstants.STATUS_NEW)
			item.set("referenceId", System.currentTimeMillis() % 100000);
		if (parent == null)
			if (insertFirst)
				store.insert(item, 0, false);
			else
				store.add(item, false);
		else
			if (insertFirst)
				store.insert(parent, item, 0, false);
			else
				store.add(parent, item, false);
		
		if (instance.getChildInstances() != null)
			for (SnapshotTreeInstance child : instance.getChildInstances()) {
				addInstanceToStore(item, child);
			}
	}
	
	protected GridCellRenderer<ModelData> getNoteButtonRenderer() {
		GridCellRenderer<ModelData> buttonRenderer = new GridCellRenderer<ModelData>() {   
		  
		      public Object render(final ModelData model, String property, ColumnData config, final int rowIndex,  
		          final int colIndex, ListStore<ModelData> store, Grid<ModelData> grid) {  
		  
		    	if (model.get("note") == null || (model.get("type") != null && SnapshotTreeInstance.FOLDER.equals(model.get("type").toString())))	// For folders
		    		return new Html("");
		    	
		        NotesIconButtonField<String> b = new NotesIconButtonField<String>(panel) {
		        	@Override
		        	public void updateNote(String note) {
		        		asyncUpdateNote(model, note, this);
		        	}
		        };
		        b.setCloseable(false);
		        b.setSize(24, 20);
//		        b.setStyleAttribute("padding-left", "5px");
		        b.addStyleName("tree-grid-notes-button");
		        b.setNote(model.get("note").toString(), true);
		       
		        return b;  
		      }  
		    };  
		    
		return buttonRenderer;
	}
	
	protected ToolTipConfig getIconButtonToolTip(String tip) {
		return new ToolTipConfig(tip);
	}
	
	protected GridCellRenderer<ModelData> getServiceButtonRenderer() {
		GridCellRenderer<ModelData> buttonRenderer = new GridCellRenderer<ModelData>() {   
	
		      public Object render(final ModelData model, String property, ColumnData config, final int rowIndex, final int colIndex, ListStore<ModelData> store, Grid<ModelData> grid) {  
		    	  return getLayoutSwitchButton("services-selector-button", SnapshotParentCardPanel.SERVICE_SELECTOR_PANEL, model, servicesTip) ;  
		      }  
	
		};  
		    
		return buttonRenderer;
	}
	
	protected GridCellRenderer<ModelData> getCustomerButtonRenderer() {
		GridCellRenderer<ModelData> buttonRenderer = new GridCellRenderer<ModelData>() {   
	
		      public Object render(final ModelData model, String property, ColumnData config, final int rowIndex, final int colIndex, ListStore<ModelData> store, Grid<ModelData> grid) {  
		    	  return getLayoutSwitchButton("customers-selector-button", SnapshotParentCardPanel.CUSTOMER_SELECTOR_PANEL, model, customersTip) ;  
		      }  
	
		};  
		    
		return buttonRenderer;
	}
	
	protected GridCellRenderer<ModelData> getTermButtonRenderer() {
		GridCellRenderer<ModelData> buttonRenderer = new GridCellRenderer<ModelData>() {   
	
		      public Object render(final ModelData model, String property, ColumnData config, final int rowIndex, final int colIndex, ListStore<ModelData> store, Grid<ModelData> grid) {  
		    	  return getLayoutSwitchButton("terms-selector-button", SnapshotParentCardPanel.CRITERIA_PANEL, model, termsTip) ;  
		      }  
	
		};  
		    
		return buttonRenderer;
	}
	
	protected GridCellRenderer<ModelData> getViewDataButtonRenderer() {
		GridCellRenderer<ModelData> buttonRenderer = new GridCellRenderer<ModelData>() {   
	
		      public Object render(final ModelData model, String property, ColumnData config, final int rowIndex, final int colIndex, ListStore<ModelData> store, Grid<ModelData> grid) {  
		    	  return getLayoutSwitchButton("view-data-button", SnapshotParentCardPanel.VIEW_DATA_PANEL, model, viewDataTip) ;  
		      }  
	
		};  
		    
		return buttonRenderer;
	}
	
	protected GridCellRenderer<ModelData> getExcelButtonRenderer() {
		GridCellRenderer<ModelData> buttonRenderer = new GridCellRenderer<ModelData>() {   
	
		      public Object render(final ModelData model, String property, ColumnData config, final int rowIndex, final int colIndex, ListStore<ModelData> store, Grid<ModelData> grid) {  
		    	  return getExcelDownloadButton(model) ;  
		      }  
	
		};  
		    
		return buttonRenderer;
	}
	
	protected Object getLayoutSwitchButton(String iconStyle, final int selectorId, final ModelData model, final ToolTipConfig toolTipConfig) { 
		  
    	if (model.get("type") != null && SnapshotTreeInstance.FOLDER.equals(model.get("type").toString()))	// For folders
    		return new Html("");
    	
		IconButton b = new IconButton("three-state-button") {
        	@Override
        	public void onClick(ComponentEvent ce) {
        		SnapshotInstance snapshot = ((BeanModel) model.get("snapshot")).getBean();
        		if (snapshot.getStatus() == AppConstants.STATUS_COMPILING) {
        			MessageBox.alert("Alert", "Snapshot is currently compiling.  Please wait until compilation is complete.", null);
        		} else {
	        		parentCardPanel.setTargetSnapshot(snapshot);
	        		parentCardPanel.switchLayout(selectorId);
        		}
        	}
        };
        b.setSize(16, 16);
        b.setToolTip(toolTipConfig);
        b.addStyleName(iconStyle);
        
        if (model.get("snapshot.status").toString().equals(AppConstants.STATUS_COMPILING)) {
        	b.disable();
        }
       
        return b; 
	}
	
	protected Object getExcelDownloadButton(final ModelData model) { 
		  
    	if (model.get("type") != null && SnapshotTreeInstance.FOLDER.equals(model.get("type").toString()))	// For folders
    		return new Html("");
    	
		IconButton b = new IconButton("three-state-button") {
        	@Override
        	public void onClick(ComponentEvent ce) {
        		SnapshotInstance snapshot = ((BeanModel) model.get("snapshot")).getBean();
        		if (snapshot.getStatus() == AppConstants.STATUS_COMPILING) {
        			MessageBox.alert("Alert", "Snapshot is currently compiling.  Please wait until compilation is complete.", null);
        		} else {
        			Window.open("sbam/getSnapshot.xls", "new", "");
        		}
        	}
        };
        b.setSize(16, 16);
        b.setToolTip(excelTip);
        b.addStyleName("excel-button");
        
        if (model.get("snapshot.status").toString().equals(AppConstants.STATUS_COMPILING)) {
        	b.disable();
        }
       
        return b; 
	}
	

	protected void asyncUpdateNote(final ModelData model, String note, final NotesIconButtonField<String> notesField) {
		
		int snapshotId = (Integer) model.get("snapshotId");
		
		if (snapshotId < 0) {
			System.out.println("Attempt to update a non-snapshot note ignored.");
			notesField.unlockNote();
			return;
		}

		SnapshotInstance snapshotInstance = new SnapshotInstance();
		snapshotInstance.setSnapshotId(snapshotId);
		snapshotInstance.setNote(note);
	
		//	Issue the asynchronous update request and plan on handling the response
		updateSnapshotNoteService.updateSnapshotNote(snapshotInstance,
				new AsyncCallback<UpdateResponse<SnapshotInstance>>() {
					public void onFailure(Throwable caught) {
						// Show the RPC error message to the user
						if (caught instanceof IllegalArgumentException)
							MessageBox.alert("Alert", caught.getMessage(), null);
						else {
							MessageBox.alert("Alert", "Snapshot note update failed unexpectedly.", null);
							System.out.println(caught.getClass().getName());
							System.out.println(caught.getMessage());
						}
						notesField.unlockNote();
					}

					public void onSuccess(UpdateResponse<SnapshotInstance> updateResponse) {
						SnapshotInstance updatedSnapshot = (SnapshotInstance) updateResponse.getInstance();
						//	This makes sure the field and instance are in synch
						model.set("note", updatedSnapshot.getNote());
						setNotesField(notesField, updatedSnapshot.getNote());
						notesField.unlockNote();
				}
			});
	}
	
	public void setNotesField(NotesIconButtonField<String> notesField, String note) {
		if (note != null && note.length() > 0) {
			notesField.setEditMode();
			notesField.setNote(note);
		} else {
			notesField.setAddMode();
			notesField.setNote("");		
		}
	}
	
	public void setPanelHeading(String panelHeading) {
		this.panelHeading = panelHeading;
		if (panel != null)
			panel.setHeading(panelHeading);
	}

	public boolean isAllowReorganize() {
		return allowReorganize;
	}

	public void setAllowReorganize(boolean allowReorganize) {
		this.allowReorganize = allowReorganize;
	}

	public SnapshotParentCardPanel getParentCardPanel() {
		return parentCardPanel;
	}

	public void setParentCardPanel(SnapshotParentCardPanel parentCardPanel) {
		this.parentCardPanel = parentCardPanel;
	}

	@Override
	public void reflectSnapshotChanges(SnapshotInstance instance) {
		if (instance == null)
			return;
		ModelData model = store.findModel(instance.getSnapshotId() + "");
		if (model == null) {
			System.out.println("Model not found");
			return;
		}
		Record record = store.getRecord(model);
		if (record == null) {
			System.out.println("Record not found");
			return;
		}

		record.beginEdit();
		record.getModel().set("snapshot", SnapshotInstance.obtainModel(instance));
		record.endEdit();
		record.commit(false);

	}

//	@Override
//	public void prepareForActivation(Object... args) {
//		if (args != null && args.length > 0) {
//			setProductCode(args [0].toString());
//			if (args.length > 1 && args [1] != null)
//				setPanelHeading("Selected Services: " + args [1]);
//		}
//		
//		refreshTreeData();
//	}

}
