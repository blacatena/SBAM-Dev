/**
 * 
 */
package com.scholastic.sbam.client.uiobjects;

import java.util.List;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.LoadConfig;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.layout.CenterLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.scholastic.sbam.client.services.PrefCodeSelCodeValidationService;
import com.scholastic.sbam.client.services.PrefCodeSelCodeValidationServiceAsync;
import com.scholastic.sbam.client.services.PreferenceCodeListService;
import com.scholastic.sbam.client.services.PreferenceCodeListServiceAsync;
import com.scholastic.sbam.client.services.UpdatePreferenceCodeService;
import com.scholastic.sbam.client.services.UpdatePreferenceCodeServiceAsync;
import com.scholastic.sbam.shared.objects.PreferenceCodeInstance;
import com.scholastic.sbam.shared.objects.UpdateResponse;
import com.scholastic.sbam.shared.validation.CodeValidator;
import com.scholastic.sbam.shared.validation.NameValidator;

/**
 * @author Bob Lacatena
 *
 */
public class PreferenceCodeEditGrid extends BetterFilterEditGrid<PreferenceCodeInstance> {
	
	private String prefCatCode;
	
	private final PreferenceCodeListServiceAsync preferenceCodeListService = GWT.create(PreferenceCodeListService.class);
	private final UpdatePreferenceCodeServiceAsync updatePreferenceCodeService = GWT.create(UpdatePreferenceCodeService.class);
	private final PrefCodeSelCodeValidationServiceAsync prefCodeSelCodeValidationService = GWT.create(PrefCodeSelCodeValidationService.class);
	
	@Override
	public void onRender(Element parent, int index) {
		setForceHeight(600);
		setAdditionalWidthPadding(0);
	//	setForceWidth(600);
	//	setAutoExpandColumn("spacer");
		setLayout(new CenterLayout());
	//	setLayout(new FillLayout(Orientation.VERTICAL));
		setPanelHeading("Preference Codes");
		super.onRender(parent, index);
	}

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
						// If this user is newly created, back-populate the id
						if (targetBeanModel.get("createdDatetime") == null) {
							targetBeanModel.set("createdDatetime", updatedPreferenceCode.getCreatedDatetime());
						}
				}
			});
	}

	public String getPrefCatCode() {
		return prefCatCode;
	}

	public void setPrefCatCode(String prefCatCode) {
		this.prefCatCode = prefCatCode;
	}

}
