package com.scholastic.sbam.client.uiobjects.foundation;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.ModelKeyProvider;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
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
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.scholastic.sbam.client.stores.KeyModelComparer;
import com.scholastic.sbam.client.uiobjects.fields.LockableFieldSet;
import com.scholastic.sbam.client.util.IconSupplier;
import com.scholastic.sbam.shared.objects.SimpleKeyProvider;

public abstract class FormAndGridPanel<ModelInstance> extends GridSupportContainer<ModelInstance> {
	
	public final static int		DIRTY_FORM_LISTEN_TIME	=	250;
	
	public final static String EDIT_BUTTON		= "E";
	public final static String CANCEL_BUTTON	= "C";
	public final static String NEW_BUTTON		= "N";
	public final static String SAVE_BUTTON		= "S";
	public final static String DELETE_BUTTON	= "D";
	
	protected boolean				doNotExpandFields = false;
	
	protected FormAndGridPanel<ModelInstance>	mainContainer	= this;
	
	protected Timer					dirtyFormListener;
	protected int					dirtyFormListenTime = DIRTY_FORM_LISTEN_TIME;
	
	protected ToolBar				editToolBar;
	protected Button				editButton;
	protected Button				deleteButton;
	protected Button				cancelButton;
	protected Button				saveButton;
	protected Button				newButton;
	protected String				useButtons = getButtonString();
	
	protected FormPanel				formPanel;
	protected ContentPanel			gridPanel;
	protected ListStore<BeanModel>	gridStore;
	protected Grid<BeanModel>		grid;
	
	protected int					focusId;
	protected ModelInstance			focusInstance;
	
	protected boolean				fieldsOpen;
	
	protected String getButtonString() {
		return NEW_BUTTON + EDIT_BUTTON + CANCEL_BUTTON + SAVE_BUTTON;
	}

	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);
		
		FormData formData = new FormData("100%");
	
		setLayout(new FlowLayout());
		
		formPanel = new FormPanel() {
			private boolean firstExpand = true;
			/*
			 * This panel has to take care of telling the grid panel to grow or shrink as it renders/resizes/expands/collapses
			 * (non-Javadoc)
			 */
			
			@Override
			protected void afterRender() {
				super.afterRender();
				adjustGridPanelHeight();
				adjustFormPanelSize(-1, -1);
			//	dumpSizes("FormPanel afterRender");
			}
			
			@Override
			public void onResize(int width, int height) {
				super.onResize(width, height);
				adjustGridPanelHeight();
				adjustFormPanelSize(width, height);
			//	dumpSizes("FormPanel onResize " + width);
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
				if (firstExpand) {
					adjustFormPanelSize(-1, -1);
					firstExpand = false;
				}
			}
			
			@Override
			public void onAfterLayout() { // This is critical... this is what makes sure that the grid panel gets resized after the formPanel actually has a size
				super.onAfterLayout();
				adjustGridPanelHeight();
			}
			
//			@Override
//			public boolean isDirty() {
//				if (super.isDirty()) {
//					for (Field<?> f : getFields()) {
//						if (!(f instanceof CheckBoxGroup) && f.isDirty()) {
//							f.isDirty();
//							System.out.println("dirty " + f.getId() + " : " + f.getFieldLabel() +  " / " + f.getValue() + " vs " + f.getOriginalValue() + " ... " + f.getRawValue() + " [ " + f.getClass().getName());
//							return true;
//						}
//					}
//					return true;
//				} else {
//					System.out.println("form panel not dirty");
//					return false;
//				}
//			}
			
		};
		
		formPanel.setId("formPanelMain");
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
		if (focusInstance == null) {
			formPanel.collapse();
		}
		
		addFormFields(formPanel, formData);
		
		addEditSaveButtons();
		
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
		
		gridPanel.add(getGrid(formData));
		
		add(formPanel);
		add(gridPanel);
		
		if (focusId != 0) {
			loadGrid(focusId);
		}
		
		moreRendering();
	}
	
	/**
	 * Override this method to perform any additional rendering steps.
	 */
	public void moreRendering() {
	}
	
	/**
	 * This method automatically adjusts the widths of the components of the FormPanel if it is using a TableLayout.
	 * 
	 * Percent widths are set in the components LayoutData if they are Doubles.
	 * 
	 * Components without Double layoutData are automatically sized to use a portion of any remaning width.
	 * 
	 * @param width
	 * @param height
	 */
	public void adjustFormPanelSize(int width, int height) {
		if (doNotExpandFields)
			return;
		
		if (!formPanel.isRendered())
			return;
		
		if (formPanel.getLayout() != null && formPanel.getLayout() instanceof TableLayout) {
			formPanel.layout(true);
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
//		adjustFormPanelSize(-1, -1);
	//	dumpSizes("afterRender");
	}
			
	
	@Override
	public void onResize(int width, int height) {
		super.onResize(width, height);
		adjustGridPanelHeight();
		adjustFormPanelSize(width, height);
	//	dumpSizes("onResize " + width);
	}
	
	public void dumpSizes(String title) {
		System.out.println();
		System.out.println("___________________" + title);
	//	dumpSizes(this);		
	}
	
	public void dumpSizes(LayoutContainer container) {
		if (!container.isRendered()) {
			System.out.println(container.getId() + " " + container.getClass().getName() + " not rendered");
		} else {
			System.out.println(container.getId() + " " + container.getClass().getName() + "   /   " + container.getWidth() + " / " + container.getWidth(true));
		}
		for (Component item : container.getItems()) {
			if (item instanceof LayoutContainer)
				dumpSizes( (LayoutContainer) item);
			else if (item instanceof Field) {
				System.out.println( "Field " + item.getClass().getName() + " / " + ((Field<?>) item).getFieldLabel() + " : " + ((Field<?>) item).getWidth());
			}
		}
	}

	public void setFocusInstance(ModelInstance modelInstance) {
		focusInstance = modelInstance;
		
		//	Disable these buttons, in case the user was in the midst of editing something
		if (cancelButton != null) cancelButton.disable();
		if (saveButton != null) saveButton.disable();
		
		// No agreement term means clear the form
		if (focusInstance == null) {
			if (editButton != null) editButton.disable();
			if (deleteButton != null) deleteButton.disable();
			clearFocusInstance();
			return;
		}
		
		//	Enable edit
		if (editButton != null) editButton.enable();
		if (deleteButton != null) deleteButton.enable();
		
		// Set the form fields
		setFormFromInstance(focusInstance);
		
		if (formPanel != null) {
			formPanel.expand();
		}
		
		focusSelection();
	}
	
	public void clearFocusInstance() {
		clearFormFieldValues();
		formPanel.collapse();
	}
	
//	protected FormInnerPanel getNewInnerFormPanel() {
//		return getNewFormPanel(75);
//	}
//	
//	protected FormInnerPanel getNewFormPanel(int labelWidth) {
//		return getNewFormPanel(false, labelWidth);
//	}
//	
//	protected FormInnerPanel getNewFormPanel(boolean outerForm) {
//		return getNewFormPanel(outerForm, 75);
//	}
//	
//	protected FormInnerPanel getNewFormPanel(boolean outerForm, int labelWidth) {
//		FormInnerPanel newFormPanel = new FormInnerPanel(outerForm);
//		
//		newFormPanel.setFrame(false); // true
//		newFormPanel.setHeaderVisible(false);  
//		newFormPanel.setBodyBorder(false);	// true
//		newFormPanel.setBorders(false);
//		newFormPanel.setBodyStyleName("subtle-form");
//		newFormPanel.setButtonAlign(HorizontalAlignment.CENTER);
//		newFormPanel.setLabelAlign(LabelAlign.RIGHT);
//		newFormPanel.setLabelWidth(labelWidth);
//		
//		return newFormPanel;
//	}
	
	protected void addEditSaveButtons() {
		addEditSaveButtons(formPanel, useButtons);
	}
	
	protected void addEditSaveButtons(String buttons) {
		addEditSaveButtons(formPanel, buttons);
	}
	
	protected void addEditSaveButtons(FormPanel targetPanel) {
		addEditSaveButtons(targetPanel, useButtons);
	}
	
	/**
	 * Use this to add the standard New/Edit/Cancel/Save buttons to the top panel.
	 * 
	 * The buttons parameter can be set with a combination of button ids using the constants, such as NEW_BUTTON + SAVE_BUTTON
	 * 
	 * @param targetPanel
	 * @param buttons
	 */
	protected void addEditSaveButtons(FormPanel targetPanel, String buttons) {
		if (useButtons == null)
			return;
		
		editToolBar = new ToolBar();
		editToolBar.setAlignment(HorizontalAlignment.CENTER);
		editToolBar.setBorders(false);
		editToolBar.setSpacing(20);
		editToolBar.setMinButtonWidth(60);
//		toolBar.addStyleName("clear-toolbar");

		if (buttons.length() == 0 || buttons.contains(NEW_BUTTON)) {
			newButton = new Button("New");
			IconSupplier.forceIcon(newButton, IconSupplier.getNewIconName());
			newButton.enable();
			newButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
				@Override
				public void componentSelected(ButtonEvent ce) {
					handleNew();
				}  
			});
			editToolBar.add(newButton);
		}
		
		if (buttons.length() == 0 || buttons.contains(EDIT_BUTTON)) {
			editButton = new Button("Edit");
			IconSupplier.forceIcon(editButton, IconSupplier.getEditIconName());
			if (focusInstance != null)
				editButton.enable();
			else
				editButton.disable();
			editButton.addSelectionListener(new SelectionListener<ButtonEvent>() {  
				@Override
				public void componentSelected(ButtonEvent ce) {
					beginEdit();
				}  
			});
			editToolBar.add(editButton);
		}
		
		if (buttons.length() == 0 || buttons.contains(DELETE_BUTTON)) {
			deleteButton = new Button("Delete");
			IconSupplier.forceIcon(deleteButton, IconSupplier.getDeleteIconName());
			if (focusInstance != null)
				deleteButton.enable();
			else
				deleteButton.disable();
			deleteButton.addSelectionListener(new SelectionListener<ButtonEvent>() {  
				@Override
				public void componentSelected(ButtonEvent ce) {
					confirmDelete();
				}  
			});
			editToolBar.add(deleteButton);
		}

		if (buttons.length() == 0 || buttons.contains(CANCEL_BUTTON)) {
			cancelButton = new Button("Cancel");
			IconSupplier.forceIcon(cancelButton, IconSupplier.getCancelIconName());
			cancelButton.disable();
			cancelButton.addSelectionListener(new SelectionListener<ButtonEvent>() {  
				@Override
				public void componentSelected(ButtonEvent ce) {
					endEdit(false);
				}  
			});
			editToolBar.add(cancelButton);
		}

		if (buttons.length() == 0 || buttons.contains(SAVE_BUTTON)) {
			saveButton = new Button("Save");
			IconSupplier.forceIcon(saveButton, IconSupplier.getSaveIconName());
			saveButton.disable();
			saveButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
				@Override
				public void componentSelected(ButtonEvent ce) {
					handleSave();
				}  
			});
			editToolBar.add(saveButton);
		}
		
		targetPanel.setBottomComponent(editToolBar);
		
		addDirtyFormListener();
	}
	
	protected void addDirtyFormListener() {
		if (dirtyFormListener == null) {
			dirtyFormListener = new Timer() {

				@Override
				public void run() {
					if (isDirtyForm()) {
						handleDirtyForm();
					} else {
						handleCleanForm();
					}
				}
				
			};
		}
		
		dirtyFormListener.scheduleRepeating(dirtyFormListenTime);
	}
	
	/**
	 * Override this to determine if the current form is considered dirty.
	 * @return
	 */
	protected boolean isDirtyForm() {
		return focusInstance == null || formPanel.isDirty();
	}
	
	public String getDeleteMessage() {
		return "Do you wish to delete this entry?";
	}
	
	public void confirmDelete() {
		final Listener<MessageBoxEvent> confirmDelete = new Listener<MessageBoxEvent>() {  
			public void handleEvent(MessageBoxEvent ce) {  
				Button btn = ce.getButtonClicked();
				if ("Yes".equals(btn.getText()))
					asyncDelete();
			}  
		};
		MessageBox.confirm("Delete?", getDeleteMessage(), confirmDelete);
	}
	
	/**
	 * Override this to take action when the user wishes to create a new entry
	 */
	public void handleNew() {
		focusInstance = null;
		clearFormFieldValues();
		beginEdit();
	}
	
	/**
	 * Override this to perform any special checks or warnings/confirmations before doing a save.
	 * The default behavior is to simple do an endEdit(true).
	 */
	protected void handleSave() {
		endEdit(true);
	}
	
	protected void handleDirtyForm() {
		if (fieldsOpen && isFormValidAndReady() && isDirtyForm()) {
			saveButton.enable();
		} else {
			saveButton.disable();
		}
	}
	
	protected boolean isFormValidAndReady() {
		return formPanel != null && formPanel.isValid();
	}
	
	protected void handleCleanForm() {
		if (saveButton != null) saveButton.disable();
	}
	
	public void beginEdit() {
		if (newButton != null) newButton.disable();
		if (editButton != null) editButton.disable();
		if (deleteButton != null) deleteButton.disable();
		if (cancelButton != null) cancelButton.enable();
		enableFields();
	}

	public void endEdit(boolean save) {
		if (cancelButton != null) cancelButton.disable();
		if (saveButton != null) saveButton.disable();
		disableFields();
		if (save) {
			if (editButton != null) editButton.disable();	//	Disable this ...let the update enable it when the response arrives
			if (editButton != null) newButton.disable();	//	Disable this ...let the update enable it when the response arrives
			if (deleteButton != null) deleteButton.disable();
			asyncUpdate();
		} else {
			resetFormValues();
			if (newButton != null)	newButton.enable();
			if (editButton != null && focusInstance != null) editButton.enable();
			if (deleteButton != null) deleteButton.setEnabled(focusInstance != null);
		}
	}
	
	protected void asyncUpdate() {
		System.out.println("Update not implemented in " + getClass().getName());
	}
	
	protected void asyncDelete() {
		System.out.println("Delete not implemented in " + getClass().getName());
	}
	
	protected void resetFormValues() {
		formPanel.reset();
	}
	
	public void setFormFromInstance(ModelInstance instance) {
		setFormFieldValues(instance);
		setOriginalValues();
	}
	
	public void setOriginalValues() {
		setOriginalValues(formPanel);
	}
	
	public void setOriginalValues(FormPanel formPanel) {
		FieldFactory.setOriginalValues(formPanel);
	}
	
//	public void setOriginalValues(FormPanel formPanel) {
//		if (formPanel != null) {
//			for (Field<?> field : formPanel.getFields()) {
//				setOriginalValues(field);
//			}
//			
//		}
//	}
//	
//	@SuppressWarnings("unchecked")
//	public void setOriginalValues(Field<?> field) {
//		if (field instanceof EnhancedComboBox) {
//			EnhancedComboBox<ModelData>  ecb = (EnhancedComboBox<ModelData>) field;
//			ecb.setOriginalValue(ecb.getSelectedValue());
//		} else if (field instanceof InstitutionSearchField) {
//			InstitutionSearchField  isf = (InstitutionSearchField) field;
//			isf.setOriginalValue(isf.getSelectedValue());
//		} else if (field instanceof SliderField) {
//			((Field<Object>) field).setOriginalValue(field.getValue());
//		} else if (field instanceof CheckBoxGroup) {
//			CheckBoxGroup cbg = (CheckBoxGroup) field;
//			for (Field<?> cbf : cbg.getAll()) {
//				CheckBox cb = (CheckBox) cbf;
//				cb.setOriginalValue(cb.getValue());
//			}
//		} else if (field instanceof RadioGroup) {
//			RadioGroup cbg = (RadioGroup) field;
//			for (Field<?> cbf : cbg.getAll()) {
//				CheckBox cb = (CheckBox) cbf;
//				cb.setOriginalValue(cb.getValue());
//			}
//		} else if (field instanceof CheckBox) {
//			CheckBox cb = (CheckBox) field;
//			cb.setOriginalValue(cb.getOriginalValue());
//		} else if (field instanceof IpAddressRangeField) {
//			IpAddressRangeField iprf = (IpAddressRangeField) field;
//			iprf.setOriginalValue(iprf.getValue());
//		} else if (field instanceof IpAddressField) {
//			IpAddressField ipf = (IpAddressField) field;
//			ipf.setOriginalValue(ipf.getValue());
//		} else if (field instanceof UserIdPasswordField) {
//			UserIdPasswordField ipf = (UserIdPasswordField) field;
//			ipf.setOriginalValue(ipf.getValue());
//		} else if (field instanceof MultiField) {
//			MultiField<Object> mf = (MultiField<Object>) field; 
//			for (Object o : mf.getAll()) {
//				Field<?> f = (Field<?>) o;
//				setOriginalValues(f);
//			}
//		} else {
//			((Field<Object>) field).setOriginalValue(field.getValue());
//		}
//		
//	}
	
	protected Grid<BeanModel> getGrid(FormData formData) {

		gridFilters = new GridFilters(); // Have to create this here, before adding the grid columns, so the filters get created with the columns
		
		List<ColumnConfig> columns = new ArrayList<ColumnConfig>();
		
		addGridColumns(columns);
		
		ColumnModel cm = new ColumnModel(columns);  

		gridStore = getNewGridStore();
		gridStore.setKeyProvider(getGridStoreKeyProvider());
		gridStore.setModelComparer(new KeyModelComparer<BeanModel>(gridStore));
		
		grid = new Grid<BeanModel>(gridStore, cm); 
//		grid.addPlugin(expander);
		grid.setBorders(true);  
//		grid.setAutoExpandColumn(autoExpandColumn);  
		grid.setLoadMask(true);
		grid.setStripeRows(true);
		grid.setColumnLines(false);
		grid.setHideHeaders(false);
		
		gridFilters.setLocal(areGridFiltersLocal()); 
		grid.addPlugin(gridFilters);

		setGridAttributes(grid);
		addGridPlugins(grid);
		
		addRowListener(grid);
		
		grid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

		return grid;
	}
	
	public boolean areGridFiltersLocal() {
		return true;
	}
	
	public ListStore<BeanModel> getNewGridStore() {
		return new ListStore<BeanModel>();
	}
	
	public ModelKeyProvider<BeanModel> getGridStoreKeyProvider() {
		return new SimpleKeyProvider("uniqueKey");
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void onRowSelected(BeanModel instance) {
		if (cancelButton != null && cancelButton.isEnabled() && isDirtyForm()) {
			grid.mask("Save or cancel your changes in progress before selecting another row.");
			Timer unmaskTimer = new Timer() {
				@Override
				public void run() {
					grid.unmask();
				}
			};
			unmaskTimer.schedule(2000);
		} else
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
						//	Go into add mode immediately if the grid is empty.
						if (gridStore.getCount() == 0  && focusInstance == null) {
							formPanel.expand();
							beginEdit();
						} else if (focusInstance != null) {
							focusSelection();
						}
 					}
			};
	}
	
	public void focusSelection() {
		if (focusInstance == null || grid == null || gridStore == null)
			return;
		int rowIndex = gridStore.indexOf(gridStore.findModel(getModel(focusInstance)));
		grid.getView().focusRow(rowIndex);
		grid.getSelectionModel().select(rowIndex, false);
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

	public ListStore<BeanModel> getGridStore() {
		return gridStore;
	}

	public void setGridStore(ListStore<BeanModel> gridStore) {
		this.gridStore = gridStore;
	}

	public Grid<BeanModel> getGrid() {
		return grid;
	}

	public void setGrid(Grid<BeanModel> grid) {
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
	public void addGridPlugins(Grid<BeanModel> grid) {
//		For example:
//		grid.addPlugin(expander);
	}
	
	/**
	 * Override to set any further grid atrributes, such as the autoExpandColumn.
	 * @param grid
	 */
	public void setGridAttributes(Grid<BeanModel> grid) {
//		For example:
//		grid.setAutoExpandColumn(autoExpandColumn);  	
	}
	
	public void disableFields() {
		fieldsOpen = false;
		for (Field<?> field : formPanel.getFields()) {
			field.disable();
		}
	}
	
	public void enableFields() {
		fieldsOpen = true;
		for (Field<?> field : formPanel.getFields()) {
			if (field.getParent() != null && field.getParent() instanceof LockableFieldSet) {
				LockableFieldSet lfs = (LockableFieldSet) field.getParent();
				lfs.enableFields(true);
			} else 
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
		setOriginalValues();
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

	public boolean isDoNotExpandFields() {
		return doNotExpandFields;
	}

	public void setDoNotExpandFields(boolean doNotExpandFields) {
		this.doNotExpandFields = doNotExpandFields;
	}
	
}
