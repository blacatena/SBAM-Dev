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
import com.scholastic.sbam.client.services.ServiceCodeValidationService;
import com.scholastic.sbam.client.services.ServiceCodeValidationServiceAsync;
import com.scholastic.sbam.client.services.ServiceListService;
import com.scholastic.sbam.client.services.ServiceListServiceAsync;
import com.scholastic.sbam.client.services.UpdateServiceService;
import com.scholastic.sbam.client.services.UpdateServiceServiceAsync;
import com.scholastic.sbam.shared.objects.ServiceInstance;
import com.scholastic.sbam.shared.objects.UpdateResponse;
import com.scholastic.sbam.shared.validation.CodeValidator;
import com.scholastic.sbam.shared.validation.NameValidator;

/**
 * @author Bob Lacatena
 *
 */
public class ServiceEditGrid extends BetterFilterEditGrid<ServiceInstance> {
	
	private final ServiceListServiceAsync serviceListService = GWT.create(ServiceListService.class);
	private final UpdateServiceServiceAsync updateServiceService = GWT.create(UpdateServiceService.class);
	private final ServiceCodeValidationServiceAsync serviceCodeValidationService = GWT.create(ServiceCodeValidationService.class);
	
	@Override
	public void onRender(Element parent, int index) {
		setForceHeight(600);
		setAdditionalWidthPadding(14);
	//	setForceWidth(600);
	//	setAutoExpandColumn("spacer");
		setLayout(new CenterLayout());
	//	setLayout(new FillLayout(Orientation.VERTICAL));
		setPanelHeading("Services");
		super.onRender(parent, index);
	}

	@Override
	protected void asyncLoad(Object loadConfig, AsyncCallback<List<ServiceInstance>> callback) {
		LoadConfig myLoadConfig = null;
		if (loadConfig instanceof LoadConfig)
			myLoadConfig = (LoadConfig) loadConfig;
		serviceListService.getServices(myLoadConfig, callback);
	}

	@Override
	protected void addColumns(List<ColumnConfig> columns) {
		columns.add(getEditColumn(			"serviceCode", 			"Code", 			40,		"A unique code to identify the service.",					new CodeValidator(2), 		serviceCodeValidationService));
		columns.add(getEditColumn(			"description", 			"Description", 		200,	"A clear description of the service.",						new NameValidator(),		null));
		columns.add(getEditColumn(			"exportValue",			"Export Value", 	100,	"The value to export for this service.",					new NameValidator("exort value"),		null));
		columns.add(getEditColumn(			"exportFile",			"Export File", 		100,	"The file to use for export of this service.",				new NameValidator("export file"),		null));
		columns.add(getComboColumn(			"serviceTypeName",		"Service Type", 	100,	"The type of this service.",								ServiceInstance.SERVICE_TYPE_NAMES ));
		columns.add(getEditCheckColumn(		"active",				"Active", 			50,		"Uncheck to deactivate a code value."));
		columns.add(getDateColumn(			"createdDatetime",		"Created", 			75,		"The date that this row was created."));
	}

	@Override
	protected ServiceInstance getNewInstance() {
		ServiceInstance service = new ServiceInstance();
		service.setServiceCode(null);
		service.setDescription("");
		service.setExportValue("");
		service.setExportFile("");
		service.setServiceType('I');
		service.setStatus('A');
		service.setActive(true);
		return service;
	}

	@Override
	protected void asyncUpdate(BeanModel beanModel) {
		final BeanModel targetBeanModel = beanModel;
	//	System.out.println("Before update: " + targetBeanModel.getProperties());
		updateServiceService.updateService((ServiceInstance) beanModel.getBean(),
				new AsyncCallback<UpdateResponse<ServiceInstance>>() {
					public void onFailure(Throwable caught) {
						// Show the RPC error message to the user
						if (caught instanceof IllegalArgumentException)
							MessageBox.alert("Alert", caught.getMessage(), null);
						else {
							MessageBox.alert("Alert", "Service update failed unexpectedly.", null);
							System.out.println(caught.getClass().getName());
							System.out.println(caught.getMessage());
						}
					}

					public void onSuccess(UpdateResponse<ServiceInstance> updateResponse) {
						ServiceInstance updatedService = (ServiceInstance) updateResponse.getInstance();
						ServiceInstance  storeInstance = targetBeanModel.getBean();
						storeInstance.setNewRecord(false);
						// If this user is newly created, back-populate the id
						if (storeInstance.getCreatedDatetime() == null) {
							storeInstance.setCreatedDatetime(updatedService.getCreatedDatetime());
						}
				}
			});
	}

}
