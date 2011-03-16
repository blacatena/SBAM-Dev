package com.scholastic.sbam.client.uiobjects;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.FormPanel.LabelAlign;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
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
			protected void onRender(Element parent, int index) {
				super.onRender(parent, index);
				if (gridPanel != null && gridPanel.isRendered()) {
					gridPanel.setHeight(mainContainer.getHeight(true) - getHeight());
				}
			}
			
			@Override
			public void onResize(int width, int height) {
				super.onResize(width, height);
				if (gridPanel != null && gridPanel.isRendered()) {
					gridPanel.setHeight(mainContainer.getHeight(true) - getHeight());
				}
			}
			
			@Override
			public void onCollapse() {
				super.onCollapse();
				// Resize in anticipation of what it will be after collapse
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
				if (gridPanel != null && gridPanel.isRendered()) {
					gridPanel.setHeight(mainContainer.getHeight(true) - getHeight());
				}
			}
			
			@Override
			public void afterExpand() {
				super.afterExpand();
				if (gridPanel != null && gridPanel.isRendered()) {
					gridPanel.setHeight(mainContainer.getHeight(true) - getHeight());
				}
			}
			
		};
		formPanel.setPadding(20);
		formPanel.setFrame(true);
		formPanel.setHeaderVisible(true);
		formPanel.setHeading("Product Terms");
		formPanel.setBodyBorder(true);
		formPanel.setBorders(false);
		formPanel.setBodyStyleName("subtle-form");
		formPanel.setButtonAlign(HorizontalAlignment.CENTER);
		formPanel.setLabelAlign(LabelAlign.RIGHT);
		formPanel.setLabelWidth(100);
		formPanel.setCollapsible(true);
//		formPanel.setHeight(300);
		if (focusInstance == null)
			formPanel.collapse();
		
		addFormFields(formPanel, formData);
		
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
			protected void onRender(Element parent, int index) {
				super.onRender(parent, index);
				// This is key: it fits the grid, initially, to the height of the parent container
				if (formPanel != null && formPanel.isRendered() && formPanel.isHeaderVisible())
					gridPanel.setHeight(mainContainer.getHeight(true) - formPanel.getHeader().getOffsetHeight());
				else
					gridPanel.setHeight(mainContainer.getHeight(true));
//				setHeight(parent.getClientHeight());
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
	
	@Override
	public void onResize(int width, int height) {
		super.onResize(width, height);
		if (gridPanel != null && grid != null && gridPanel.isRendered() && grid.isRendered() && grid.getHeight() == 0) {
			if (formPanel != null && formPanel.isRendered() && formPanel.isHeaderVisible())
				gridPanel.setHeight(mainContainer.getHeight(true) - formPanel.getHeader().getOffsetHeight());
			else
				gridPanel.setHeight(mainContainer.getHeight(true));
//			gridPanel.setHeight(height);
		}
	}

	public void setModelInstance(ModelInstance modelInstance) {
		focusInstance = modelInstance;
		// No agreement term means clear the form
		if (focusInstance == null) {
			clearModelInstance();
			return;
		}
		
		// Set the form fields
		setFormFieldValues(focusInstance);
		
		if (formPanel != null)
			formPanel.expand();
	}
	
	public void clearModelInstance() {
		clearFormFieldValues();
		formPanel.collapse();
	}
	
	protected Grid<ModelData> getAgreementTermsGrid(FormData formData) {
		List<ColumnConfig> columns = new ArrayList<ColumnConfig>();
		
		addGridColumns(columns);
		
		ColumnModel cm = new ColumnModel(columns);  

		gridStore = new ListStore<ModelData>();
		
		grid = new Grid<ModelData>(gridStore, cm); 
//		grid.addPlugin(expander);
		grid.setBorders(true);  
//		grid.setAutoExpandColumn(autoExpandColumn);  
		grid.setLoadMask(true);
//		grid.setHeight(this.getHeight());
		grid.setStripeRows(true);
		grid.setColumnLines(false);
		grid.setHideHeaders(false);
//		grid.setWidth(cm.getTotalWidth() + 20);
		
		addGridPlugins(grid);
		setGridAttributes(grid);
		
		addRowListener(grid);
		
		//	Switch to the display card when a row is selected
		grid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

		return grid;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void onRowSelected(BeanModel instance) {
		setModelInstance((ModelInstance) instance.getBean());
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
							MessageBox.alert("Alert", "Agreement access failed unexpectedly.", null);
							System.out.println(caught.getClass().getName());
							System.out.println(caught.getMessage());
						}
					}

					public void onSuccess(List<ModelInstance> instances) {
						gridStore.removeAll();
						for (ModelInstance instance : instances) {
							gridStore.add(getModel(instance));
						}
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

	public void setFocusInstance(ModelInstance focusInstance) {
		this.focusInstance = focusInstance;
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
//		grid.addPlugin(expander);
	}
	
	/**
	 * Override to set any further grid atrributes, such as the autoExpandColumn.
	 * @param grid
	 */
	public void setGridAttributes(Grid<ModelData> grid) {
//		grid.setAutoExpandColumn(autoExpandColumn);  	
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
	public abstract void clearFormFieldValues();
	
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
