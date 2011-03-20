package com.scholastic.sbam.client.uiobjects;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.FormPanel.LabelAlign;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.filters.GridFilters;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;

public abstract class FormAndGridPanel<ModelInstance> extends GridSupportContainer<ModelInstance> {
	
	protected FormAndGridPanel<ModelInstance>	mainContainer	= this;
	
	protected FormPanel				formPanel;
	protected ContentPanel			gridPanel;
	protected ListStore<ModelData>	gridStore;
	protected Grid<ModelData>		grid;
	
	protected int					focusId;
	protected ModelInstance			focusInstance;

	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);
		
		FormData formData = new FormData("100%");
	
		setLayout(new FlowLayout());
		
		formPanel = new FormPanel() {
			/*
			 * This panel has to take care of telling the grid panel to grow or shrink as it renders/resizes/expands/collapses
			 * (non-Javadoc)
			 */
			
			@Override
			protected void afterRender() {
				super.afterRender();
				adjustGridPanelHeight();
			}
			
			@Override
			public void onResize(int width, int height) {
				super.onResize(width, height);
				adjustGridPanelHeight();
			}
			
			@Override
			public void onCollapse() {
				super.onCollapse();
				// Resize in anticipation of what it WILL be after collapse
				if (gridPanel != null && gridPanel.isRendered()) {
					if (isHeaderVisible())
						gridPanel.setHeight(mainContainer.getHeight(true) - getHeader().getOffsetHeight());
					else
						gridPanel.setHeight(mainContainer.getHeight(true));
				}
			}
			
			@Override
			public void afterCollapse() {
				super.afterCollapse();
				adjustGridPanelHeight();
			}
			
			@Override
			public void afterExpand() {
				super.afterExpand();
				adjustGridPanelHeight();
			}
			
			@Override
			public void onAfterLayout() { // This is critical... this is what makes sure that the grid panel gets resized after the formPanel actually has a size
				super.onAfterLayout();
				adjustGridPanelHeight();
			}
			
		};
		formPanel.setPadding(5);
		formPanel.setFrame(true);
		formPanel.addStyleName("inner-panel");
		if (getFormHeading() == null)
			formPanel.setHeaderVisible(false);
		else {
			formPanel.setHeaderVisible(true);
			formPanel.setHeading(getFormHeading());
			formPanel.getHeader().addStyleName("inner-panel");
		}
		formPanel.setBodyBorder(true);
		formPanel.setBorders(false);
		formPanel.setBodyStyleName("subtle-form");
		formPanel.setButtonAlign(HorizontalAlignment.CENTER);
		formPanel.setLabelAlign(LabelAlign.RIGHT);
		formPanel.setLabelWidth(getLabelWidth());
		formPanel.setCollapsible(true);
//		formPanel.setHeight(300);
		if (focusInstance == null) {
			formPanel.collapse();
		}
		
		addFormFields(formPanel, formData);
		
		formPanel.addListener(Events.OnClick, new Listener<BaseEvent>() {
			@Override
			public void handleEvent(BaseEvent be) {
				enableFields();
			}	
		});
		
//		Button doneButton = new Button("Done");
//		doneButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
//			@Override
//			public void componentSelected(ButtonEvent ce) {
//				formPanel.collapse();
//			}
//			
//		});
//		
//		formPanel.addButton(doneButton);
		
		gridPanel = new ContentPanel() {
			
			@Override
			protected void afterRender() {
				super.afterRender();
				adjustGridPanelHeight();
			}
		};
		gridPanel.setLayout(new FitLayout());
		gridPanel.setFrame(true);
		gridPanel.setHeaderVisible(false);
		gridPanel.setBodyBorder(true);
		gridPanel.setBorders(false);
		gridPanel.setBodyStyleName("subtle-form");
		gridPanel.setButtonAlign(HorizontalAlignment.CENTER);
		
		gridPanel.add(getAgreementTermsGrid(formData));
		
		add(formPanel);
		add(gridPanel);
		
		if (focusId != 0) {
			loadGrid(focusId);
		}
	}
	
	public void adjustGridPanelHeight() {
		if (gridPanel == null || !gridPanel.isRendered())
			return;
		
		if (formPanel == null || !formPanel.isRendered())
			gridPanel.setHeight(mainContainer.getHeight(true));
		else
			if (formPanel.isExpanded())
				gridPanel.setHeight(mainContainer.getHeight(true) - formPanel.getHeight());
			else
				if (formPanel.isHeaderVisible())
					gridPanel.setHeight(mainContainer.getHeight(true) - formPanel.getHeader().getOffsetHeight());
				else
					gridPanel.setHeight(mainContainer.getHeight(true));
	}
	
	public int getLabelWidth() {
		return 100;
	}

	@Override
	protected void afterRender() {
		super.afterRender();
		adjustGridPanelHeight();
	}
			
	
	@Override
	public void onResize(int width, int height) {
		super.onResize(width, height);
		adjustGridPanelHeight();
	}

	public void setFocusInstance(ModelInstance modelInstance) {
		focusInstance = modelInstance;
		// No agreement term means clear the form
		if (focusInstance == null) {
			clearFocusInstance();
			return;
		}
		
		// Set the form fields
		setFormFieldValues(focusInstance);
		
		if (formPanel != null) {
			formPanel.expand();
		}
	}
	
	public void clearFocusInstance() {
		clearFormFieldValues();
		formPanel.collapse();
	}
	
	protected Grid<ModelData> getAgreementTermsGrid(FormData formData) {

		gridFilters = new GridFilters(); // Have to create this here, before adding the grid columns, so the filters get created with the columns
		
		List<ColumnConfig> columns = new ArrayList<ColumnConfig>();
		
		addGridColumns(columns);
		
		ColumnModel cm = new ColumnModel(columns);  

		gridStore = new ListStore<ModelData>();
		
		grid = new Grid<ModelData>(gridStore, cm); 
//		grid.addPlugin(expander);
		grid.setBorders(true);  
//		grid.setAutoExpandColumn(autoExpandColumn);  
		grid.setLoadMask(true);
		grid.setStripeRows(true);
		grid.setColumnLines(false);
		grid.setHideHeaders(false);
		
		gridFilters.setLocal(true); 
		grid.addPlugin(gridFilters);

		setGridAttributes(grid);
		addGridPlugins(grid);
		
		addRowListener(grid);
		
		//	Switch to the display card when a row is selected
		grid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

		return grid;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void onRowSelected(BeanModel instance) {
		setFocusInstance((ModelInstance) instance.getBean());
//		formPanel.expand();
//		grid.getSelectionModel().deselectAll();
	}
	
	public AsyncCallback<List<ModelInstance>> getLoaderCallback() {
		return new AsyncCallback<List<ModelInstance>>() {
					public void onFailure(Throwable caught) {
						// Show the RPC error message to the user
						if (caught instanceof IllegalArgumentException)
							MessageBox.alert("Alert", caught.getMessage(), null);
						else {
							MessageBox.alert("Alert", "Grid data access failed unexpectedly.", null);
							System.out.println(caught.getClass().getName());
							System.out.println(caught.getMessage());
						}
						grid.unmask();
					}

					public void onSuccess(List<ModelInstance> instances) {
						gridStore.removeAll();
						for (ModelInstance instance : instances) {
							gridStore.add(getModel(instance));
						}
						grid.unmask();
					}
			};
	}

	public void loadGrid(final int id) {
		if (gridStore == null)
			return;
		if (id == 0) {
			gridStore.removeAll();
			return;
		}
		grid.mask("Loading...");
		executeLoader(id, getLoaderCallback());
	}


	public int getFocusId() {
		return focusId;
	}


	public void setFocusId(int focusId) {
		if (focusId != this.focusId) {
			this.focusId = focusId;
			loadGrid(focusId);
		}
	}

	public ModelInstance getFocusInstance() {
		return focusInstance;
	}

	public FormAndGridPanel<ModelInstance> getMainContainer() {
		return mainContainer;
	}

	public void setMainContainer(FormAndGridPanel<ModelInstance> mainContainer) {
		this.mainContainer = mainContainer;
	}

	public ContentPanel getGridPanel() {
		return gridPanel;
	}

	public void setGridPanel(ContentPanel gridPanel) {
		this.gridPanel = gridPanel;
	}

	public ListStore<ModelData> getGridStore() {
		return gridStore;
	}

	public void setGridStore(ListStore<ModelData> gridStore) {
		this.gridStore = gridStore;
	}

	public Grid<ModelData> getGrid() {
		return grid;
	}

	public void setGrid(Grid<ModelData> grid) {
		this.grid = grid;
	}
	
	public abstract String getFormHeading();

	/**
	 * Implement to add all columns to the column configuration list.
	 * @param columns
	 */
	public abstract void addGridColumns(List<ColumnConfig> columns);
	
	/**
	 * Override to add any plug-ins to the grid.
	 * @param grid
	 */
	public void addGridPlugins(Grid<ModelData> grid) {
//		For example:
//		grid.addPlugin(expander);
	}
	
	/**
	 * Override to set any further grid atrributes, such as the autoExpandColumn.
	 * @param grid
	 */
	public void setGridAttributes(Grid<ModelData> grid) {
//		For example:
//		grid.setAutoExpandColumn(autoExpandColumn);  	
	}
	
	public void enableFields() {
		for (Field<?> field : formPanel.getFields()) {
			field.enable();
		}
	}
	
	/**
	 * Implement to set the form field values based on the focus instance. 
	 * @param instance
	 */
	public abstract void setFormFieldValues(ModelInstance instance);
	
	/**
	 * Implement to clear the form field values based on the focus instance. 
	 * @param instance
	 */
	public void clearFormFieldValues() {
		formPanel.clear();
	}
	
	/**
	 * Implement to call the backend service to retrieve the list of instances for the grid.
	 * @param id
	 * @param callback
	 */
	protected abstract void executeLoader(final int id, AsyncCallback<List<ModelInstance>> callback);
	
	/**
	 * Implement to add all necessary fields (and other components) to the form.
	 * @param panel
	 */
	protected abstract void addFormFields(FormPanel panel, FormData formData);
	
}
