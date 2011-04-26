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
import com.scholastic.sbam.client.services.LinkTypeCodeValidationService;
import com.scholastic.sbam.client.services.LinkTypeCodeValidationServiceAsync;
import com.scholastic.sbam.client.services.LinkTypeListService;
import com.scholastic.sbam.client.services.LinkTypeListServiceAsync;
import com.scholastic.sbam.client.services.UpdateLinkTypeService;
import com.scholastic.sbam.client.services.UpdateLinkTypeServiceAsync;
import com.scholastic.sbam.client.uiobjects.foundation.BetterFilterEditGrid;
import com.scholastic.sbam.shared.objects.LinkTypeInstance;
import com.scholastic.sbam.shared.objects.UpdateResponse;
import com.scholastic.sbam.shared.validation.CodeValidator;
import com.scholastic.sbam.shared.validation.NameValidator;

/**
 * @author Bob Lacatena
 *
 */
public class LinkTypeEditGrid extends BetterFilterEditGrid<LinkTypeInstance> {
	
	private final LinkTypeListServiceAsync linkTypeListService = GWT.create(LinkTypeListService.class);
	private final UpdateLinkTypeServiceAsync updateLinkTypeService = GWT.create(UpdateLinkTypeService.class);
	private final LinkTypeCodeValidationServiceAsync linkTypeCodeValidationService = GWT.create(LinkTypeCodeValidationService.class);
	
	@Override
	public void onRender(Element parent, int index) {
	//	setForceHeight(600);
		setAdditionalWidthPadding(12);
	//	setForceWidth(600);
	//	setAutoExpandColumn("spacer");
		setLayout(new CenterLayout());
	//	setLayout(new FillLayout(Orientation.VERTICAL));
		setPanelHeading("Link Types");
		super.onRender(parent, index);
	}

	@Override
	protected void asyncLoad(Object loadConfig, AsyncCallback<List<LinkTypeInstance>> callback) {
		LoadConfig myLoadConfig = null;
		if (loadConfig instanceof LoadConfig)
			myLoadConfig = (LoadConfig) loadConfig;
		linkTypeListService.getLinkTypes(myLoadConfig, callback);
	}

	@Override
	protected void addColumns(List<ColumnConfig> columns) {
		columns.add(getEditColumn(			"linkTypeCode", 	"Code", 			60,		"A unique code to identify the link type.",		new CodeValidator(1), 		linkTypeCodeValidationService));
		columns.add(getEditColumn(			"description", 		"Description", 		200,	"A clear description of the link type.",			new NameValidator(),		null));
		columns.add(getEditCheckColumn(		"active",			"Active", 			50,		"Uncheck to deactivate a code value."));
		columns.add(getDateColumn(			"createdDatetime",	"Created", 			75,		"The date that this type was created."));
	}

	@Override
	protected LinkTypeInstance getNewInstance() {
		LinkTypeInstance linkType = new LinkTypeInstance();
		linkType.setLinkTypeCode(null);
		linkType.setDescription("");
		linkType.setStatus('A');
		linkType.setActive(true);
		return linkType;
	}

	@Override
	protected void asyncUpdate(BeanModel beanModel) {
		final BeanModel targetBeanModel = beanModel;
	//	System.out.println("Before update: " + targetBeanModel.getProperties());
		updateLinkTypeService.updateLinkType((LinkTypeInstance) beanModel.getBean(),
				new AsyncCallback<UpdateResponse<LinkTypeInstance>>() {
					public void onFailure(Throwable caught) {
						// Show the RPC error message to the user
						if (caught instanceof IllegalArgumentException)
							MessageBox.alert("Alert", caught.getMessage(), null);
						else {
							MessageBox.alert("Alert", "Link type update failed unexpectedly.", null);
							System.out.println(caught.getClass().getName());
							System.out.println(caught.getMessage());
						}
					}

					public void onSuccess(UpdateResponse<LinkTypeInstance> updateResponse) {
						LinkTypeInstance updatedLinkType = (LinkTypeInstance) updateResponse.getInstance();
						LinkTypeInstance  storeInstance = targetBeanModel.getBean();
						storeInstance.setNewRecord(false);
						// If this user is newly created, back-populate the id
						if (storeInstance.getCreatedDatetime() == null) {
							storeInstance.setCreatedDatetime(updatedLinkType.getCreatedDatetime());
						}
				}
			});
	}

}
