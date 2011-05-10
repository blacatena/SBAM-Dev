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
import com.scholastic.sbam.client.services.PrefCodeSelCodeValidationService;
import com.scholastic.sbam.client.services.PrefCodeSelCodeValidationServiceAsync;
import com.scholastic.sbam.client.services.PreferenceCodeListService;
import com.scholastic.sbam.client.services.PreferenceCodeListServiceAsync;
import com.scholastic.sbam.client.services.UpdatePreferenceCodeSeqService;
import com.scholastic.sbam.client.services.UpdatePreferenceCodeSeqServiceAsync;
import com.scholastic.sbam.client.services.UpdatePreferenceCodeService;
import com.scholastic.sbam.client.services.UpdatePreferenceCodeServiceAsync;
import com.scholastic.sbam.client.uiobjects.foundation.BetterFilterEditGrid;
import com.scholastic.sbam.client.uiobjects.foundation.DualEditGridLink;
import com.scholastic.sbam.client.uiobjects.foundation.DualEditGridLinker;
import com.scholastic.sbam.client.util.UiConstants;
import com.scholastic.sbam.shared.objects.PreferenceCodeInstance;
import com.scholastic.sbam.shared.objects.UpdateResponse;
import com.scholastic.sbam.shared.validation.CodeValidator;
import com.scholastic.sbam.shared.validation.NameValidator;

/**
 * @author Bob Lacatena
 *
 */
public class PreferenceCodeEditGrid extends BetterFilterEditGrid<PreferenceCodeInstance> implements DualEditGridLink {
	
	private String prefCatCode;
	private DualEditGridLinker gridLinker;
	
	private final PreferenceCodeListServiceAsync preferenceCodeListService = GWT.create(PreferenceCodeListService.class);
	private final UpdatePreferenceCodeServiceAsync updatePreferenceCodeService = GWT.create(UpdatePreferenceCodeService.class);
	private final PrefCodeSelCodeValidationServiceAsync prefCodeSelCodeValidationService = GWT.create(PrefCodeSelCodeValidationService.class);
	private final UpdatePreferenceCodeSeqServiceAsync updatePreferenceCodeSeqService = GWT.create(UpdatePreferenceCodeSeqService.class);
	
	public PreferenceCodeEditGrid() {
	//	setForceHeight(600);
		setAdditionalWidthPadding(0);
	//	setForceWidth(600);
	//	setAutoExpandColumn("spacer");
		setPanelHeading("Preference Codes");
		setToolTip(UiConstants.getQuickTip("Drag rows to resequence codes. <i>Note that the first code will be considered the default for this category.</i>"));
		setLayout(new CenterLayout());
	}
	
//	@Override
//	public void onRender(Element parent, int index) {
//		setLayout(new CenterLayout());
//	//	setLayout(new FillLayout(Orientation.VERTICAL));
//		super.onRender(parent, index);
//	}

	@Override
	protected void asyncLoad(Object loadConfig, AsyncCallback<List<PreferenceCodeInstance>> callback) {
		LoadConfig myLoadConfig = null;
		if (loadConfig instanceof LoadConfig)
			myLoadConfig = (LoadConfig) loadConfig;
		preferenceCodeListService.getPreferenceCodes(prefCatCode, myLoadConfig, callback);
	}

	@Override
	protected void addColumns(List<ColumnConfig> columns) {
		columns.add(getEditColumn(			"prefSelCode", 			"Code", 			40,		"A unique code to identify the preference selection.",		new CodeValidator(2), 		prefCodeSelCodeValidationService));
		columns.add(getEditColumn(			"description", 			"Description", 		200,	"A clear description of the preference selection.",			new NameValidator(),		null));
		columns.add(getEditColumn(			"exportValue", 			"Export Value", 	80,		"The value to export for this preference selection.",		new NameValidator(),		null));
		columns.add(getEditCheckColumn(		"active",				"Active", 			50,		"Uncheck to deactivate a code value."));
		columns.add(getDateColumn(			"createdDatetime",		"Created", 			75,		"The date that this row was created."));
		columns.add(this.getColumn(			"seq", 					"Seq", 				50,		"To resequence the codes, sort by this column, then drag and drop."));
	}

	@Override
	protected PreferenceCodeInstance getNewInstance() {
		PreferenceCodeInstance preferenceCode = new PreferenceCodeInstance();
		preferenceCode.setPrefCatCode(null);
		preferenceCode.setDescription("");
		preferenceCode.setSeq(0);
		preferenceCode.setStatus('A');
		preferenceCode.setActive(true);
		return preferenceCode;
	}

	@Override
	protected void asyncUpdate(BeanModel beanModel) {
		final BeanModel targetBeanModel = beanModel;
	//	System.out.println("Before update: " + targetBeanModel.getProperties());
		updatePreferenceCodeService.updatePreferenceCode((PreferenceCodeInstance) beanModel.getBean(),
				new AsyncCallback<UpdateResponse<PreferenceCodeInstance>>() {
					public void onFailure(Throwable caught) {
						// Show the RPC error message to the user
						if (caught instanceof IllegalArgumentException)
							MessageBox.alert("Alert", caught.getMessage(), null);
						else {
							MessageBox.alert("Alert", "Cancel reason update failed unexpectedly.", null);
							System.out.println(caught.getClass().getName());
							System.out.println(caught.getMessage());
						}
					}

					public void onSuccess(UpdateResponse<PreferenceCodeInstance> updateResponse) {
						PreferenceCodeInstance updatedPreferenceCode = (PreferenceCodeInstance) updateResponse.getInstance();
						PreferenceCodeInstance  storeInstance = targetBeanModel.getBean();
						storeInstance.setNewRecord(false);
						// If this user is newly created, back-populate the id
						if (storeInstance.getCreatedDatetime() == null) {
							storeInstance.setCreatedDatetime(updatedPreferenceCode.getCreatedDatetime());
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
				PreferenceCodeInstance prefCode = store.getAt(i).getBean();
				prefCode.setSeq(i);
				sequence.add(prefCode.getPrefSelCode());
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
		updatePreferenceCodeSeqService.updatePreferenceCodeSeq( prefCatCode, sequence,
				new AsyncCallback<String>() {
					public void onFailure(Throwable caught) {
						// Show the RPC error message to the user
						if (caught instanceof IllegalArgumentException)
							MessageBox.alert("Alert", caught.getMessage(), null);
						else {
							MessageBox.alert("Alert", "Preference Code update failed unexpectedly.", null);
							System.out.println(caught.getClass().getName());
							System.out.println(caught.getMessage());
						}
					}

					public void onSuccess(String updateResponse) {
						// Nothing to do
					}
			});
	}
	
	protected void makeRowEditor() {
		super.makeRowEditor();
		
		Button doneButton = new Button("Done");
		doneButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			   
			@Override
			public void componentSelected(ButtonEvent ce) {
				gridLinker.showParent();
			}  
		 
		});
		
		panel.addButton(doneButton);
	}

	public String getPrefCatCode() {
		return prefCatCode;
	}

	public void setPrefCatCode(String prefCatCode) {
		this.prefCatCode = prefCatCode;
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
			setPrefCatCode(args [0].toString());
			if (args.length > 1 && args [1] != null)
				setPanelHeading("Preference Codes: " + args [1]);
		}
		
		refreshGridData();
	}

}
