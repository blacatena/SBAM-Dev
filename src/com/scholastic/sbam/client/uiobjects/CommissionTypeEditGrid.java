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
import com.scholastic.sbam.client.services.CommissionCodeValidationService;
import com.scholastic.sbam.client.services.CommissionCodeValidationServiceAsync;
import com.scholastic.sbam.client.services.CommissionTypeListService;
import com.scholastic.sbam.client.services.CommissionTypeListServiceAsync;
import com.scholastic.sbam.client.services.UpdateCommissionTypeService;
import com.scholastic.sbam.client.services.UpdateCommissionTypeServiceAsync;
import com.scholastic.sbam.client.util.UiConstants;
import com.scholastic.sbam.shared.objects.CommissionTypeInstance;
import com.scholastic.sbam.shared.objects.UpdateResponse;
import com.scholastic.sbam.shared.validation.CodeValidator;
import com.scholastic.sbam.shared.validation.NameValidator;

/**
 * @author Bob Lacatena
 *
 */
public class CommissionTypeEditGrid extends BetterFilterEditGrid<CommissionTypeInstance> {
	
	private final CommissionTypeListServiceAsync commissionTypeListService = GWT.create(CommissionTypeListService.class);
	private final UpdateCommissionTypeServiceAsync updateCommissionTypeService = GWT.create(UpdateCommissionTypeService.class);
	private final CommissionCodeValidationServiceAsync commissionCodeValidationService = GWT.create(CommissionCodeValidationService.class);
	
	@Override
	public void onRender(Element parent, int index) {
	//	setForceHeight(600);
		setAdditionalWidthPadding(0);
	//	setForceWidth(600);
	//	setAutoExpandColumn("spacer");
		setLayout(new CenterLayout());
	//	setLayout(new FillLayout(Orientation.VERTICAL));
		setPanelHeading("Term Types");
		super.onRender(parent, index);
	}

	@Override
	protected void asyncLoad(Object loadConfig, AsyncCallback<List<CommissionTypeInstance>> callback) {
		LoadConfig myLoadConfig = null;
		if (loadConfig instanceof LoadConfig)
			myLoadConfig = (LoadConfig) loadConfig;
		commissionTypeListService.getCommissionTypes(myLoadConfig, callback);
	}

	@Override
	protected void addColumns(List<ColumnConfig> columns) {
		columns.add(getEditColumn(			"commissionCode", 	"Code", 			40,		"A unique code to identify the commission type.",		new CodeValidator(2), 		commissionCodeValidationService));
		columns.add(getEditColumn(			"description", 		"Description", 		200,	"A clear description of the commission type.",			new NameValidator(),		null));
		columns.add(getEditColumn(			"shortName", 		"Short Name", 		200,	"An abbreviated name for the commission type.",			new NameValidator(31),		null));
		columns.add(getEditCheckColumn(		"products",			"Product", 			50,		"Check if this type can be applied to products."));
		columns.add(getEditCheckColumn(		"sites",			"Sites", 			50,		"Check if this type can be applied to sites."));
		columns.add(getEditCheckColumn(		"agreements",		"Agreements", 		80,		"Check if this type can be applied to agreements."));
		columns.add(getEditCheckColumn(		"agreementTerms",	"Agreement<br/>Terms", 	80,		"Check if this type can be applied to agreement terms."));
		columns.add(getEditCheckColumn(		"active",			"Active", 			50,		"Uncheck to deactivate a code value."));
		columns.add(getDateColumn(			"createdDatetime",	"Created", 			75,		"The date that this row was created."));
	}

	@Override
	protected CommissionTypeInstance getNewInstance() {
		CommissionTypeInstance commissionType = new CommissionTypeInstance();
		commissionType.setCommissionCode(null);
		commissionType.setDescription("");
		commissionType.setShortName("");
		commissionType.setProducts(true);
		commissionType.setSites(true);
		commissionType.setAgreements(true);
		commissionType.setAgreementTerms(true);
		commissionType.setStatus('A');
		commissionType.setActive(true);
		return commissionType;
	}

	@Override
	protected void asyncUpdate(BeanModel beanModel) {
		final BeanModel targetBeanModel = beanModel;
	//	System.out.println("Before update: " + targetBeanModel.getProperties());
		updateCommissionTypeService.updateCommissionType((CommissionTypeInstance) beanModel.getBean(),
				new AsyncCallback<UpdateResponse<CommissionTypeInstance>>() {
					public void onFailure(Throwable caught) {
						// Show the RPC error message to the user
						if (caught instanceof IllegalArgumentException)
							MessageBox.alert("Alert", caught.getMessage(), null);
						else {
							MessageBox.alert("Alert", "Commission type update failed unexpectedly.", null);
							System.out.println(caught.getClass().getName());
							System.out.println(caught.getMessage());
						}
					}

					public void onSuccess(UpdateResponse<CommissionTypeInstance> updateResponse) {
						CommissionTypeInstance updatedCommissionType = (CommissionTypeInstance) updateResponse.getInstance();
						CommissionTypeInstance storeInstance = targetBeanModel.getBean();
						storeInstance.setNewRecord(false);
						// If this user is newly created, back-populate the id
						if (targetBeanModel.get("createdDatetime") == null) {
							storeInstance.setCreatedDatetime(updatedCommissionType.getCreatedDatetime());
//							targetBeanModel.set("createdDatetime", updatedCommissionType.getCreatedDatetime());
						}
						//	Refresh this user's commission type cache
						UiConstants.loadCommissionTypes();
				}
			});
	}

}
