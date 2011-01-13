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
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.scholastic.sbam.client.services.FieldValidationServiceAsync;
import com.scholastic.sbam.client.validation.AsyncTextField;
import com.scholastic.sbam.shared.objects.BetterRowEditInstance;
import com.scholastic.sbam.shared.util.AppConstants;
import com.scholastic.sbam.shared.validation.AppRoleGroupValidator;

public abstract class BetterEditGrid<I extends BetterRowEditInstance> extends LayoutContainer implements AppSleeper {
	
	private ListStore<BeanModel>	store;
	private Grid<BeanModel>			grid;
	private ContentPanel 			panel;
	
	private List<BeanModel> 		selection;
	
	/**
	 * The ID of the column which will expand to absorb unused width.
	 */
	private String					autoExpandColumn	= null;
	/**
	 * The width to force the grid into.  If set to negative or zero, this will be computed from the width of the columns added.
	 */
	private int						forceWidth			= -1;
	/**
	 * The label for the button to be used to create new grid rows.
	 */
	private String					newButtonLabel		= "New";
	
	public BetterEditGrid(final String autoExpandColumn) {
		super();
		this.autoExpandColumn = autoExpandColumn;
	}
	
	public BetterEditGrid(final int forceWidth) {
		super();
		this.forceWidth = forceWidth;
	}
	
	public BetterEditGrid(final int forceWidth, final String autoExpandColumn) {
		super();
		this.forceWidth = forceWidth;
		this.autoExpandColumn = autoExpandColumn;
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
		setGridAttributes();

		makeFilters();
		
		makeRowEditor();
		
		int width = 0;
		for (int i = 0; i < cm.getColumnCount(); i++)
			width += cm.getColumnWidth(i);
		if (forceWidth >= 0)
			width = forceWidth;
		
		panel.setHeading("Users");
		panel.setFrame(true);
		panel.setSize(width, 450);
		//panel.setIcon(Resources.ICONS.table());
		panel.setLayout(new FitLayout());
		
		add(panel);
	}
	
	protected void setGridAttributes() {
		grid.setBorders(true);
		grid.setStripeRows(true);
		grid.setColumnLines(true);
		grid.setColumnReordering(true);
		if (autoExpandColumn != null)
			grid.setAutoExpandColumn(autoExpandColumn);
		grid.getAriaSupport().setLabelledBy(panel.getHeader().getId() + "-label"); // access for people with disabilities -- ARIA	
	}
	
	protected ListLoader<ListLoadResult<ModelData>> getLoader() {
		// proxy and reader  
		RpcProxy<List<I>> proxy = new RpcProxy<List<I>>() {  
			@Override  
			public void load(Object loadConfig, AsyncCallback<List<I>> callback) {
		    	asyncLoad(loadConfig, callback);
		    }  
		};
		BeanModelReader reader = new BeanModelReader();
		
		// loader and store  
		ListLoader<ListLoadResult<ModelData>> loader = new BaseListLoader<ListLoadResult<ModelData>>(proxy, reader);
		return loader;
	}
	
	/**
	 * Implement this method to execute the actual asynchronous call to the loader service.
	 * @param loadConfig
	 * @param callback
	 */
	protected abstract void asyncLoad(Object loadConfig, AsyncCallback<List<I>> callback);
	
	protected void addStoreListeners() {
		store.addListener(Store.Update, new Listener<StoreEvent<BeanModel>>() {
            public void handleEvent(final StoreEvent<BeanModel> se) {
                if (se.getOperation() == Record.RecordUpdate.COMMIT && se.getModel() != null) {
                	asyncUpdate(se.getModel());
                }
                
            }
        });	
	}
	
	protected ColumnModel getColumnModel() {  
		List<ColumnConfig> columns = new ArrayList<ColumnConfig>();
		addColumns(columns);
		return new ColumnModel(columns);
	}
	
	/**
	 * Implement this method to add the grid columns.
	 * 
	 * This method will basically be a series of columns.add(column) statements.
	 * @param columns
	 * The List of column configurations to which columns will be added.
	 */
	protected abstract void addColumns(List<ColumnConfig> columns);
	
	/**
	 * Override this method to add any desired fitlers.
	 */
	protected void makeFilters() {
		
	}
	
	protected void makeRowEditor() {
		
		Field<?> userNameField = grid.getColumnModel().getColumns().get(0).getEditor().getField();
		//	BetterRowEditor created with delete button, not additional buttons, and the userName field as unchangeable
		final RowEditor<ModelData> re = new BetterRowEditor<ModelData>(store, true, null, userNameField);

		grid.addPlugin(re);
		
		Button newButton = new Button(newButtonLabel);
		newButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			   
			@Override
			public void componentSelected(ButtonEvent ce) {
				I instance = getNewInstance();
				 
				re.stopEditing(false);
				BeanModel userModel = getModel(instance);
				store.insert(userModel, 0);
				re.startEditing(store.indexOf(userModel), true);
			 
			}  
		 
		});
		
		panel.add(grid);
		panel.setButtonAlign(HorizontalAlignment.CENTER);
		
		panel.addButton(newButton);
	}
	
	/**
	 * Implement this method to return a newly initialized instance to use as the basis for creating a new persistent object.
	 * @return
	 */
	protected abstract I getNewInstance();
	
	private BeanModel getModel(I instance) {
		BeanModelFactory factory = BeanModelLookup.get().getFactory(instance.getClass());
		BeanModel model = factory.createModel(instance);
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
	
	/**
	 * Utility method to create a basic display only text field.
	 * @param name
	 * 	The name of the field.
	 * @param header
	 * 	The header for the field.
	 * @param width
	 * 	The width of the field.
	 * @return
	 */
	protected ColumnConfig getColumn(String name, String header, int width) {
		return getColumn(name, header, width, null);
	}
	
	/**
	 * Utility method to create a basic date field.
	 * @param name
	 * 	The name of the field.
	 * @param header
	 * 	The header for the field.
	 * @param width
	 * 	The width of the field.
	 * @return
	 */
	protected ColumnConfig getDateColumn(String name, String header, int width) {
		return getDateColumn(name, header, width, AppConstants.APP_DATE_TIME_FORMAT);
	}


	
	/**
	 * Utility method to create a basic date field.
	 * @param name
	 * 	The name of the field.
	 * @param header
	 * 	The header for the field.
	 * @param width
	 * 	The width of the field.
	 * @param format
	 *  The DateTimeFormat to use for the field.
	 * @return
	 */
	protected ColumnConfig getDateColumn(String name, String header, int width, DateTimeFormat format) {
		DateField dateField = new DateField();  
		dateField.getPropertyEditor().setFormat(AppConstants.APP_DATE_TIME_FORMAT);
		dateField.setReadOnly(true);
		
		ColumnConfig column = getColumn(name, header, width, null);
		column.setDateTimeFormat(format);
		column.setEditor(new CellEditor(dateField));
		return column;
	}
	
	/**
	 * Utility method to create a basic date field.
	 * @param name
	 * 	The name of the field.
	 * @param header
	 * 	The header for the field.
	 * @param width
	 * 	The width of the field.
	 * @param toolTip
	 *  The value of the tool tip to use for the field.
	 * @return
	 */
	protected ColumnConfig getColumn(String name, String header, int width, String toolTip) {
		ColumnConfig column = new ColumnConfig();
		column.setId(name);
		column.setHeader(header);
		column.setWidth(width);
		if (toolTip != null && toolTip.length() > 0)
			column.setToolTip(toolTip);
		return column;
	}
	
	/**
	 * Utility method to create a basic editable text field.
	 * @param name
	 * 	The name of the field.
	 * @param header
	 * 	The header for the field.
	 * @param width
	 * 	The width of the field.
	 * @return
	 */
	protected ColumnConfig getEditColumn(String name, String header, int width) {
		return getEditColumn(name, header, width, null);
	}
	
	/**
	 * Utility method to create a basic editable text field.
	 * @param name
	 * 	The name of the field.
	 * @param header
	 * 	The header for the field.
	 * @param width
	 * 	The width of the field.
	 * @param toolTip
	 *  The value of the tool tip to use for the field.
	 * @return
	 */
	protected ColumnConfig getEditColumn(String name, String header, int width, String toolTip) {
		ColumnConfig column = getColumn(name, header, width, toolTip);
		TextField<String> text = new TextField<String>();
		text.setName(name);
		text.setAllowBlank(false);
		column.setEditor(new CellEditor(text));
		return column;
	}

	
	/**
	 * Utility method to create a basic editable asynchronous text field with custom synchronous and asynchronous validation.
	 * @param name
	 * 	The name of the field.
	 * @param header
	 * 	The header for the field.
	 * @param width
	 * 	The width of the field.
	 * @param toolTip
	 *  The value of the tool tip to use for the field.
	 * @param validator
	 *  The validator to use for the field for local editing.
	 * @param validationService
	 *  The asynchronous validation service to use with the field.
	 * @return
	 */
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
	
	/**
	 * Utility method to create a basic checkbox field.
	 * @param name
	 * 	The name of the field.
	 * @param header
	 * 	The header for the field.
	 * @param width
	 * 	The width of the field.
	 * @param toolTip
	 *  The value of the tool tip to use for the field.
	 * @return
	 */
	protected ColumnConfig getEditCheckColumn(String name, String header, int width, String toolTip) {
		CheckColumnConfig checkColumn = new CheckColumnConfig(name, header, width); 
		CellEditor checkBoxEditor = new CellEditor(new CheckBox());
		checkColumn.setEditor(checkBoxEditor);
		return checkColumn;
	}
	
	/**
	 * Utility method to create a combo box column.
	 * @param name
	 *  The name of the column.
	 * @param header
	 *  The header for the column.
	 * @param width
	 *  The width of the column.
	 * @param values
	 *  The list of valid values for the column.
	 * @return
	 */
	protected ColumnConfig getComboColumn(String name, String header, int width, String [] values) {
		
		final SimpleComboBox<String> combo = new SimpleComboBox<String>();
		combo.setForceSelection(true);
		combo.setTriggerAction(TriggerAction.ALL);
		combo.setValidator(new AppRoleGroupValidator());
		combo.setEditable(false);
		
		for (int i = 0; i < values.length; i++) {
			combo.add(values [i]);
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
		column.setId(name);
		column.setHeader(header);
		column.setWidth(width);
		column.setEditor(editor);
	
		return column; 
	}
	
	/**
	 * Implement this method to make the asynchronous call to perform an actual update.
	 * 
	 * @param beanModel
	 * Save/use the bean model, as needed, to reflect changes enforced by response from the service call.
	 */
	protected abstract void asyncUpdate(BeanModel beanModel);
	
	public String getFailureMessage() {
		return "Update failed unexpectedly.";
	}

	public ListStore<BeanModel> getStore() {
		return store;
	}

	public void setStore(ListStore<BeanModel> store) {
		this.store = store;
	}

	public Grid<BeanModel> getGrid() {
		return grid;
	}

	public void setGrid(Grid<BeanModel> grid) {
		this.grid = grid;
	}

	public ContentPanel getPanel() {
		return panel;
	}

	public void setPanel(ContentPanel panel) {
		this.panel = panel;
	}

	public String getAutoExpandColumn() {
		return autoExpandColumn;
	}

	public void setAutoExpandColumn(String autoExpandColumn) {
		this.autoExpandColumn = autoExpandColumn;
	}

	public int getForceWidth() {
		return forceWidth;
	}

	public void setForceWidth(int forceWidth) {
		this.forceWidth = forceWidth;
	}

	public String getNewButtonLabel() {
		return newButtonLabel;
	}

	public void setNewButtonLabel(String newButtonLabel) {
		this.newButtonLabel = newButtonLabel;
	}

}
