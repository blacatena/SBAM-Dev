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
import com.scholastic.sbam.client.services.AgreementTypeCodeValidationService;
import com.scholastic.sbam.client.services.AgreementTypeCodeValidationServiceAsync;
import com.scholastic.sbam.client.services.AgreementTypeListService;
import com.scholastic.sbam.client.services.AgreementTypeListServiceAsync;
import com.scholastic.sbam.client.services.UpdateAgreementTypeService;
import com.scholastic.sbam.client.services.UpdateAgreementTypeServiceAsync;
import com.scholastic.sbam.shared.objects.AgreementTypeInstance;
import com.scholastic.sbam.shared.objects.UpdateResponse;
import com.scholastic.sbam.shared.validation.CodeValidator;
import com.scholastic.sbam.shared.validation.NameValidator;

/**
 * @author Bob Lacatena
 *
 */
public class AgreementTypeEditGrid extends BetterFilterEditGrid<AgreementTypeInstance> {
	
	private final AgreementTypeListServiceAsync agreementTypeListService = GWT.create(AgreementTypeListService.class);
	private final UpdateAgreementTypeServiceAsync updateAgreementTypeService = GWT.create(UpdateAgreementTypeService.class);
	private final AgreementTypeCodeValidationServiceAsync agreementTypeCodeValidationService = GWT.create(AgreementTypeCodeValidationService.class);
	
	@Override
	public void onRender(Element parent, int index) {
	//	setForceHeight(600);
		setAdditionalWidthPadding(0);
	//	setForceWidth(600);
	//	setAutoExpandColumn("spacer");
		setLayout(new CenterLayout());
	//	setLayout(new FillLayout(Orientation.VERTICAL));
		setPanelHeading("Agreement Types");
		super.onRender(parent, index);
	}

	@Override
	protected void asyncLoad(Object loadConfig, AsyncCallback<List<AgreementTypeInstance>> callback) {
		LoadConfig myLoadConfig = null;
		if (loadConfig instanceof LoadConfig)
			myLoadConfig = (LoadConfig) loadConfig;
		agreementTypeListService.getAgreementTypes(myLoadConfig, callback);
	}

	@Override
	protected void addColumns(List<ColumnConfig> columns) {
		columns.add(getEditColumn(			"agreementTypeCode", 	"Code", 			40,		"A unique code to identify the agreement type.",		new CodeValidator(1), 		agreementTypeCodeValidationService));
		columns.add(getEditColumn(			"description", 		"Description", 		200,	"A clear description of the agreement.",					new NameValidator(),		null));
		columns.add(getEditColumn(			"shortName", 		"Short Name", 		100,	"A clear description of the agreement.",					new NameValidator(),		null));
		columns.add(getEditCheckColumn(		"active",			"Active", 			50,		"Uncheck to deactivate a code value."));
		columns.add(getDateColumn(			"createdDatetime",	"Created", 			75,		"The date that this row was created."));
	}

	@Override
	protected AgreementTypeInstance getNewInstance() {
		AgreementTypeInstance agreementType = new AgreementTypeInstance();
		agreementType.setAgreementTypeCode(null);
		agreementType.setDescription("");
		agreementType.setShortName("");
		agreementType.setStatus('A');
		agreementType.setActive(true);
		return agreementType;
	}

	@Override
	protected void asyncUpdate(BeanModel beanModel) {
		final BeanModel targetBeanModel = beanModel;
	//	System.out.println("Before update: " + targetBeanModel.getProperties());
		updateAgreementTypeService.updateAgreementType((AgreementTypeInstance) beanModel.getBean(),
				new AsyncCallback<UpdateResponse<AgreementTypeInstance>>() {
					public void onFailure(Throwable caught) {
						// Show the RPC error message to the user
						if (caught instanceof IllegalArgumentException)
							MessageBox.alert("Alert", caught.getMessage(), null);
						else {
							MessageBox.alert("Alert", "Agreement type update failed unexpectedly.", null);
							System.out.println(caught.getClass().getName());
							System.out.println(caught.getMessage());
						}
					}

					public void onSuccess(UpdateResponse<AgreementTypeInstance> updateResponse) {
						AgreementTypeInstance updatedAgreementType = (AgreementTypeInstance) updateResponse.getInstance();
						AgreementTypeInstance  storeInstance = targetBeanModel.getBean();
						storeInstance.setNewRecord(false);
						// If this user is newly created, back-populate the id
						if (storeInstance.getCreatedDatetime() == null) {
							storeInstance.setCreatedDatetime(updatedAgreementType.getCreatedDatetime());
						}
				}
			});
	}

}
