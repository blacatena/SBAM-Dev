/**
 * 
 */
package com.scholastic.sbam.client.uiobjects.uiconfig;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.Style.SortDir;
import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.LoadConfig;
import com.extjs.gxt.ui.client.dnd.DND.Feedback;
import com.extjs.gxt.ui.client.dnd.GridDragSource;
import com.extjs.gxt.ui.client.dnd.GridDropTarget;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.DNDEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.layout.CenterLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.scholastic.sbam.client.services.PrefCatCodeValidationService;
import com.scholastic.sbam.client.services.PrefCatCodeValidationServiceAsync;
import com.scholastic.sbam.client.services.PreferenceCategoryListService;
import com.scholastic.sbam.client.services.PreferenceCategoryListServiceAsync;
import com.scholastic.sbam.client.services.UpdatePreferenceCategorySeqService;
import com.scholastic.sbam.client.services.UpdatePreferenceCategorySeqServiceAsync;
import com.scholastic.sbam.client.services.UpdatePreferenceCategoryService;
import com.scholastic.sbam.client.services.UpdatePreferenceCategoryServiceAsync;
import com.scholastic.sbam.client.uiobjects.foundation.BetterFilterEditGrid;
import com.scholastic.sbam.client.uiobjects.foundation.DualEditGridLink;
import com.scholastic.sbam.client.uiobjects.foundation.DualEditGridLinker;
import com.scholastic.sbam.client.util.UiConstants;
import com.scholastic.sbam.shared.objects.PreferenceCategoryInstance;
import com.scholastic.sbam.shared.objects.UpdateResponse;
import com.scholastic.sbam.shared.validation.CodeValidator;
import com.scholastic.sbam.shared.validation.NameValidator;

/**
 * @author Bob Lacatena
 *
 */
public class PreferenceCategoryEditGrid extends BetterFilterEditGrid<PreferenceCategoryInstance> implements DualEditGridLink {

	private DualEditGridLinker gridLinker;
	
	private final PreferenceCategoryListServiceAsync preferenceCategoryListService = GWT.create(PreferenceCategoryListService.class);
	private final UpdatePreferenceCategoryServiceAsync updatePreferenceCategoryService = GWT.create(UpdatePreferenceCategoryService.class);
	private final PrefCatCodeValidationServiceAsync prefCatCodeValidationService = GWT.create(PrefCatCodeValidationService.class);
	private final UpdatePreferenceCategorySeqServiceAsync updatePreferenceCategorySeqService = GWT.create(UpdatePreferenceCategorySeqService.class);
	
	public PreferenceCategoryEditGrid() {
		super();
	//	setForceHeight(600);
		setAdditionalWidthPadding(0);
		//	setForceWidth(600);
		//	setAutoExpandColumn("spacer");
		setPanelHeading("Preference Categories");
		setToolTip(UiConstants.getQuickTip("Drag rows to resequence preferences in all displays."));
		setLayout(new CenterLayout());
	}
	
//	@Override
//	public void onRender(Element parent, int index) {
//		setLayout(new CenterLayout());
//	//	setLayout(new FillLayout(Orientation.VERTICAL));
//		super.onRender(parent, index);
//	}

	@Override
	protected void asyncLoad(Object loadConfig, AsyncCallback<List<PreferenceCategoryInstance>> callback) {
		LoadConfig myLoadConfig = null;
		if (loadConfig instanceof LoadConfig)
			myLoadConfig = (LoadConfig) loadConfig;
		preferenceCategoryListService.getPreferenceCategories(myLoadConfig, false, callback);	// false = Don't bother getting codes
	}

	@Override
	protected void addColumns(List<ColumnConfig> columns) {
		columns.add(getEditColumn(			"prefCatCode", 			"Code", 			40,		"A unique code to identify the preference category.",		new CodeValidator(2), 		prefCatCodeValidationService));
		columns.add(getEditColumn(			"description", 			"Description", 		200,	"A clear description of the preference category.",			new NameValidator(),		null));
		columns.add(getEditCheckColumn(		"active",				"Active", 			50,		"Uncheck to deactivate a code value."));
		columns.add(getDateColumn(			"createdDatetime",		"Created", 			75,		"The date that this row was created."));
		columns.add(this.getColumn(			"seq", 					"Seq", 				50,		"To resequence the categories, sort by this column, then drag and drop."));
	}

	@Override
	protected PreferenceCategoryInstance getNewInstance() {
		PreferenceCategoryInstance preferenceCategory = new PreferenceCategoryInstance();
		preferenceCategory.setPrefCatCode(null);
		preferenceCategory.setDescription("");
		preferenceCategory.setSeq(0);
		preferenceCategory.setStatus('A');
		preferenceCategory.setActive(true);
		return preferenceCategory;
	}

	@Override
	protected void asyncUpdate(BeanModel beanModel) {
		final BeanModel targetBeanModel = beanModel;
	//	System.out.println("Before update: " + targetBeanModel.getProperties());
		updatePreferenceCategoryService.updatePreferenceCategory((PreferenceCategoryInstance) beanModel.getBean(),
				new AsyncCallback<UpdateResponse<PreferenceCategoryInstance>>() {
					public void onFailure(Throwable caught) {
						// Show the RPC error message to the user
						if (caught instanceof IllegalArgumentException)
							MessageBox.alert("Alert", caught.getMessage(), null);
						else {
							MessageBox.alert("Alert", "Preference Category update failed unexpectedly.", null);
							System.out.println(caught.getClass().getName());
							System.out.println(caught.getMessage());
						}
					}

					public void onSuccess(UpdateResponse<PreferenceCategoryInstance> updateResponse) {
						PreferenceCategoryInstance updatedPreferenceCategory = (PreferenceCategoryInstance) updateResponse.getInstance();
						PreferenceCategoryInstance  storeInstance = targetBeanModel.getBean();
						storeInstance.setNewRecord(false);
						// If this user is newly created, back-populate the id
						if (storeInstance.getCreatedDatetime() == null) {
							storeInstance.setCreatedDatetime(updatedPreferenceCategory.getCreatedDatetime());
						}
				}
			});
	}
	
	/**
	 * Ammend setGridAttributes to add drag and drop capability for re-sequencing the rows.
	 */
	@Override
	public void setGridAttributes() {
		super.setGridAttributes();
		
		store.setDefaultSort("seq", SortDir.ASC);
		
		new GridDragSource(grid) {
			@Override
			public void onDragStart(DNDEvent e) {
				//	"Remove" the sorter, so the grid won't re-sequence it all back with another sort
				if (store.getSortField() == null || ("seq".equals(store.getSortField()) && store.getSortDir().equals(SortDir.ASC))) {
					store.setStoreSorter(null);
				}
				super.onDragStart(e);
			}
		};  
	      
	    GridDropTarget target = new GridDropTarget(grid) {
	    	@Override
	    	public void onDragDrop(DNDEvent e) {
	    		super.onDragDrop(e);
	    		//	Do the actual resequencing on the backend
	    		resequenceCategories();
	    	}
	    };  
	    target.setAllowSelfAsSource(true);  
	    target.setFeedback(Feedback.INSERT);
	}
	
	/**
	 * Issue an asynch update call to re-sequence the rows on the backend, and change the local sequence numbers to reflect the new order.
	 * 
	 * This is done by making sure the sort is correct (reject it if not), building a list of codes in the right order, then updating the grid and sending the update to the backend.
	 */
	public void resequenceCategories() {
		//	Test that the sort will allow this
		if (store.getSortField() == null || ("seq".equals(store.getSortField()) && store.getSortDir().equals(SortDir.ASC))) {
			//	"Remove" the sort, so the grid won't re-sequence it all back with another sort
			store.setStoreSorter(null);
			store.setSortField(null);
			store.setSortDir(SortDir.NONE);
			//	Build a list of the category codes, in the new order
			List<String> sequence = new ArrayList<String>();
			for (int i = 0; i < grid.getStore().getCount(); i++) {
				PreferenceCategoryInstance cat = store.getAt(i).getBean();
				cat.setSeq(i);
				sequence.add(cat.getPrefCatCode());
			}
			//	Reflect the change in the grid
			store.commitChanges();
			grid.getView().refresh(false);
			//	Do the actual update
			asyncResequence(sequence);
		} else {
			//	Don't let a user try to re-sequence things using the wrong sort order to begin with
			MessageBox.alert("Operation Refused", "To reorder the categories, first sort the grid by the Seq column.", null);
		}
	}

	/**
	 * Issue the actual asynchronous call to update the database with the new sequence.
	 * @param sequence
	 */
	protected void asyncResequence(List<String> sequence) {
	//	System.out.println("Before update: " + targetBeanModel.getProperties());
		updatePreferenceCategorySeqService.updatePreferenceCategorySeq( sequence,
				new AsyncCallback<String>() {
					public void onFailure(Throwable caught) {
						// Show the RPC error message to the user
						if (caught instanceof IllegalArgumentException)
							MessageBox.alert("Alert", caught.getMessage(), null);
						else {
							MessageBox.alert("Alert", "Preference Category update failed unexpectedly.", null);
							System.out.println(caught.getClass().getName());
							System.out.println(caught.getMessage());
						}
					}

					public void onSuccess(String updateResponse) {
						// Nothing to do
					}
			});
	}
	
	@Override
	public List<Button> getCustomRowButtons() {
		List<Button> customList = new ArrayList<Button>();
		
		Button editCodes = new Button("Values");
		editCodes.addSelectionListener(new SelectionListener<ButtonEvent>() {
			   
			@Override
			public void componentSelected(ButtonEvent ce) {
				gridLinker.showChild(grid.getSelectionModel().getSelectedItem().get("prefCatCode"), grid.getSelectionModel().getSelectedItem().get("description"));
			}  
		 
		});
		
		customList.add(editCodes);
		
		return customList;
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
		// Nothing to do
	}

}
