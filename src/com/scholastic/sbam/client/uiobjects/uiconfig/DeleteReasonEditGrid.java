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
import com.scholastic.sbam.client.services.DeleteReasonCodeValidationService;
import com.scholastic.sbam.client.services.DeleteReasonCodeValidationServiceAsync;
import com.scholastic.sbam.client.services.DeleteReasonListService;
import com.scholastic.sbam.client.services.DeleteReasonListServiceAsync;
import com.scholastic.sbam.client.services.UpdateDeleteReasonService;
import com.scholastic.sbam.client.services.UpdateDeleteReasonServiceAsync;
import com.scholastic.sbam.client.uiobjects.foundation.BetterFilterEditGrid;
import com.scholastic.sbam.shared.objects.DeleteReasonInstance;
import com.scholastic.sbam.shared.objects.UpdateResponse;
import com.scholastic.sbam.shared.validation.CodeValidator;
import com.scholastic.sbam.shared.validation.NameValidator;

/**
 * @author Bob Lacatena
 *
 */
public class DeleteReasonEditGrid extends BetterFilterEditGrid<DeleteReasonInstance> {
	
	private final DeleteReasonListServiceAsync deleteReasonListService = GWT.create(DeleteReasonListService.class);
	private final UpdateDeleteReasonServiceAsync updateDeleteReasonService = GWT.create(UpdateDeleteReasonService.class);
	private final DeleteReasonCodeValidationServiceAsync deleteReasonCodeValidationService = GWT.create(DeleteReasonCodeValidationService.class);
	
	@Override
	public void onRender(Element parent, int index) {
	//	setForceHeight(600);
		setAdditionalWidthPadding(0);
	//	setForceWidth(600);
	//	setAutoExpandColumn("spacer");
		setLayout(new CenterLayout());
	//	setLayout(new FillLayout(Orientation.VERTICAL));
		setPanelHeading("Delete Reasons");
		super.onRender(parent, index);
	}

	@Override
	protected void asyncLoad(Object loadConfig, AsyncCallback<List<DeleteReasonInstance>> callback) {
		LoadConfig myLoadConfig = null;
		if (loadConfig instanceof LoadConfig)
			myLoadConfig = (LoadConfig) loadConfig;
		deleteReasonListService.getDeleteReasons(myLoadConfig, callback);
	}

	@Override
	protected void addColumns(List<ColumnConfig> columns) {
		columns.add(getEditColumn(			"deleteReasonCode", 	"Code", 			40,		"A unique code to identify the reason for a deletion.",		new CodeValidator(2), 		deleteReasonCodeValidationService));
		columns.add(getEditColumn(			"description", 			"Description", 		200,	"A clear description of the reason for the deletion.",		new NameValidator(),		null));
		columns.add(getEditCheckColumn(		"active",				"Active", 			50,		"Uncheck to deactivate a code value."));
		columns.add(getDateColumn(			"createdDatetime",		"Created", 			75,		"The date that this row was created."));
	}

	@Override
	protected DeleteReasonInstance getNewInstance() {
		DeleteReasonInstance deleteReason = new DeleteReasonInstance();
		deleteReason.setDeleteReasonCode(null);
		deleteReason.setDescription("");
		deleteReason.setStatus('A');
		deleteReason.setActive(true);
		return deleteReason;
	}

	@Override
	protected void asyncUpdate(BeanModel beanModel) {
		final BeanModel targetBeanModel = beanModel;
	//	System.out.println("Before update: " + targetBeanModel.getProperties());
		updateDeleteReasonService.updateDeleteReason((DeleteReasonInstance) beanModel.getBean(),
				new AsyncCallback<UpdateResponse<DeleteReasonInstance>>() {
					public void onFailure(Throwable caught) {
						// Show the RPC error message to the user
						if (caught instanceof IllegalArgumentException)
							MessageBox.alert("Alert", caught.getMessage(), null);
						else {
							MessageBox.alert("Alert", "Delete reason update failed unexpectedly.", null);
							System.out.println(caught.getClass().getName());
							System.out.println(caught.getMessage());
						}
					}

					public void onSuccess(UpdateResponse<DeleteReasonInstance> updateResponse) {
						DeleteReasonInstance updatedDeleteReason = (DeleteReasonInstance) updateResponse.getInstance();
						DeleteReasonInstance   storeInstance = targetBeanModel.getBean();
						storeInstance.setNewRecord(false);
						// If this user is newly created, back-populate the id
						if (storeInstance.getCreatedDatetime() == null) {
							storeInstance.setCreatedDatetime(updatedDeleteReason.getCreatedDatetime());
						}
				}
			});
	}

}
