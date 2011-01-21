/**
 * 
 */
package com.scholastic.sbam.client.uiobjects;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.LoadConfig;
import com.extjs.gxt.ui.client.event.ButtonEvent;
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
import com.scholastic.sbam.client.services.UpdatePreferenceCategoryService;
import com.scholastic.sbam.client.services.UpdatePreferenceCategoryServiceAsync;
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
	
	public PreferenceCategoryEditGrid() {
		super();
		setForceHeight(600);
		setAdditionalWidthPadding(0);
		//	setForceWidth(600);
		//	setAutoExpandColumn("spacer");
		setPanelHeading("Preference Categories");
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
		preferenceCategoryListService.getPreferenceCategories(myLoadConfig, callback);
	}

	@Override
	protected void addColumns(List<ColumnConfig> columns) {
		columns.add(getEditColumn(			"prefCatCode", 			"Code", 			40,		"A unique code to identify the preference category.",		new CodeValidator(2), 		prefCatCodeValidationService));
		columns.add(getEditColumn(			"description", 			"Description", 		200,	"A clear description of the preference category.",			new NameValidator(),		null));
		columns.add(getEditCheckColumn(		"active",				"Active", 			50,		"Uncheck to deactivate a code value."));
		columns.add(getDateColumn(			"createdDatetime",		"Created", 			75,		"The date that this row was created."));
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
							MessageBox.alert("Alert", "Cancel reason update failed unexpectedly.", null);
							System.out.println(caught.getClass().getName());
							System.out.println(caught.getMessage());
						}
					}

					public void onSuccess(UpdateResponse<PreferenceCategoryInstance> updateResponse) {
						PreferenceCategoryInstance updatedPreferenceCategory = (PreferenceCategoryInstance) updateResponse.getInstance();
						// If this user is newly created, back-populate the id
						if (targetBeanModel.get("createdDatetime") == null) {
							targetBeanModel.set("createdDatetime", updatedPreferenceCategory.getCreatedDatetime());
						}
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
