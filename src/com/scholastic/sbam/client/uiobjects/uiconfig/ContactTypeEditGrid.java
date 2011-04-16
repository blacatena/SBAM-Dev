/**
 * 
 */
package com.scholastic.sbam.client.uiobjects.uiconfig;

import java.util.List;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.LoadConfig;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.layout.CenterLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.scholastic.sbam.client.services.ContactTypeCodeValidationService;
import com.scholastic.sbam.client.services.ContactTypeCodeValidationServiceAsync;
import com.scholastic.sbam.client.services.ContactTypeListService;
import com.scholastic.sbam.client.services.ContactTypeListServiceAsync;
import com.scholastic.sbam.client.services.UpdateContactTypeService;
import com.scholastic.sbam.client.services.UpdateContactTypeServiceAsync;
import com.scholastic.sbam.client.uiobjects.foundation.BetterFilterEditGrid;
import com.scholastic.sbam.shared.objects.ContactTypeInstance;
import com.scholastic.sbam.shared.objects.UpdateResponse;
import com.scholastic.sbam.shared.validation.CodeValidator;
import com.scholastic.sbam.shared.validation.NameValidator;

/**
 * @author Bob Lacatena
 *
 */
public class ContactTypeEditGrid extends BetterFilterEditGrid<ContactTypeInstance> {
	
	private final ContactTypeListServiceAsync contactTypeListService = GWT.create(ContactTypeListService.class);
	private final UpdateContactTypeServiceAsync updateContactTypeService = GWT.create(UpdateContactTypeService.class);
	private final ContactTypeCodeValidationServiceAsync contactTypeCodeValidationService = GWT.create(ContactTypeCodeValidationService.class);
	
	@Override
	public void onRender(Element parent, int index) {
	//	setForceHeight(600);
		setAdditionalWidthPadding(12);
	//	setForceWidth(600);
	//	setAutoExpandColumn("spacer");
		setLayout(new CenterLayout());
	//	setLayout(new FillLayout(Orientation.VERTICAL));
		setPanelHeading("Contact Types");
		super.onRender(parent, index);
	}

	@Override
	protected void asyncLoad(Object loadConfig, AsyncCallback<List<ContactTypeInstance>> callback) {
		LoadConfig myLoadConfig = null;
		if (loadConfig instanceof LoadConfig)
			myLoadConfig = (LoadConfig) loadConfig;
		contactTypeListService.getContactTypes(myLoadConfig, callback);
	}

	@Override
	protected void addColumns(List<ColumnConfig> columns) {
		columns.add(getEditColumn(			"contactTypeCode", 	"Code", 			60,		"A unique code to identify the contact type.",		new CodeValidator(1), 		contactTypeCodeValidationService));
		columns.add(getEditColumn(			"description", 		"Description", 		200,	"A clear description of the contact type.",			new NameValidator(),		null));
		columns.add(getEditCheckColumn(		"active",			"Active", 			50,		"Uncheck to deactivate a code value."));
		columns.add(getDateColumn(			"createdDatetime",	"Created", 			75,		"The date that this type was created."));
	}

	@Override
	protected ContactTypeInstance getNewInstance() {
		ContactTypeInstance contactType = new ContactTypeInstance();
		contactType.setContactTypeCode(null);
		contactType.setDescription("");
		contactType.setStatus('A');
		contactType.setActive(true);
		return contactType;
	}

	@Override
	protected void asyncUpdate(BeanModel beanModel) {
		final BeanModel targetBeanModel = beanModel;
	//	System.out.println("Before update: " + targetBeanModel.getProperties());
		updateContactTypeService.updateContactType((ContactTypeInstance) beanModel.getBean(),
				new AsyncCallback<UpdateResponse<ContactTypeInstance>>() {
					public void onFailure(Throwable caught) {
						// Show the RPC error message to the user
						if (caught instanceof IllegalArgumentException)
							MessageBox.alert("Alert", caught.getMessage(), null);
						else {
							MessageBox.alert("Alert", "Contact type update failed unexpectedly.", null);
							System.out.println(caught.getClass().getName());
							System.out.println(caught.getMessage());
						}
					}

					public void onSuccess(UpdateResponse<ContactTypeInstance> updateResponse) {
						ContactTypeInstance updatedContactType = (ContactTypeInstance) updateResponse.getInstance();
						ContactTypeInstance  storeInstance = targetBeanModel.getBean();
						storeInstance.setNewRecord(false);
						// If this user is newly created, back-populate the id
						if (storeInstance.getCreatedDatetime() == null) {
							storeInstance.setCreatedDatetime(updatedContactType.getCreatedDatetime());
						}
				}
			});
	}

}
