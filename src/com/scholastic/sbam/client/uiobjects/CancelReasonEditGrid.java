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
import com.scholastic.sbam.client.services.CancelReasonCodeValidationService;
import com.scholastic.sbam.client.services.CancelReasonCodeValidationServiceAsync;
import com.scholastic.sbam.client.services.CancelReasonListService;
import com.scholastic.sbam.client.services.CancelReasonListServiceAsync;
import com.scholastic.sbam.client.services.UpdateCancelReasonService;
import com.scholastic.sbam.client.services.UpdateCancelReasonServiceAsync;
import com.scholastic.sbam.shared.objects.CancelReasonInstance;
import com.scholastic.sbam.shared.objects.UpdateResponse;
import com.scholastic.sbam.shared.validation.CodeValidator;
import com.scholastic.sbam.shared.validation.NameValidator;

/**
 * @author Bob Lacatena
 *
 */
public class CancelReasonEditGrid extends BetterFilterEditGrid<CancelReasonInstance> {
	
	private final CancelReasonListServiceAsync cancelReasonListService = GWT.create(CancelReasonListService.class);
	private final UpdateCancelReasonServiceAsync updateCancelReasonService = GWT.create(UpdateCancelReasonService.class);
	private final CancelReasonCodeValidationServiceAsync cancelReasonCodeValidationService = GWT.create(CancelReasonCodeValidationService.class);
	
	@Override
	public void onRender(Element parent, int index) {
		setForceHeight(600);
		setAdditionalWidthPadding(14);
	//	setForceWidth(600);
	//	setAutoExpandColumn("spacer");
		setLayout(new CenterLayout());
	//	setLayout(new FillLayout(Orientation.VERTICAL));
		setPanelHeading("Cancel Reasons");
		super.onRender(parent, index);
	}

	@Override
	protected void asyncLoad(Object loadConfig, AsyncCallback<List<CancelReasonInstance>> callback) {
		LoadConfig myLoadConfig = null;
		if (loadConfig instanceof LoadConfig)
			myLoadConfig = (LoadConfig) loadConfig;
		cancelReasonListService.getCancelReasons(myLoadConfig, callback);
	}

	@Override
	protected void addColumns(List<ColumnConfig> columns) {
		columns.add(getEditColumn(			"cancelReasonCode", 	"Code", 			40,		"A unique code to identify the reason for a cancellation.",		new CodeValidator(2), 		cancelReasonCodeValidationService));
		columns.add(getEditColumn(			"description", 			"Description", 		200,	"A clear description of the reason for the cancellation.",		new NameValidator(),		null));
		columns.add(getEditCheckColumn(		"changeNotCancel",		"Change", 			50,		"Check if this represents a change (move) of service rather than a cancellation."));
		columns.add(getEditCheckColumn(		"active",				"Active", 			50,		"Uncheck to deactivate a code value."));
		columns.add(getDateColumn(			"createdDatetime",		"Created", 			75,		"The date that this row was created."));
	}

	@Override
	protected CancelReasonInstance getNewInstance() {
		CancelReasonInstance cancelReason = new CancelReasonInstance();
		cancelReason.setCancelReasonCode(null);
		cancelReason.setDescription("");
		cancelReason.setChangeNotCancel('n');
		cancelReason.setStatus('A');
		cancelReason.setActive(true);
		return cancelReason;
	}

	@Override
	protected void asyncUpdate(BeanModel beanModel) {
		final BeanModel targetBeanModel = beanModel;
	//	System.out.println("Before update: " + targetBeanModel.getProperties());
		updateCancelReasonService.updateCancelReason((CancelReasonInstance) beanModel.getBean(),
				new AsyncCallback<UpdateResponse<CancelReasonInstance>>() {
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

					public void onSuccess(UpdateResponse<CancelReasonInstance> updateResponse) {
						CancelReasonInstance updatedCancelReason = (CancelReasonInstance) updateResponse.getInstance();
						CancelReasonInstance  storeInstance = targetBeanModel.getBean();
						storeInstance.setNewRecord(false);
						// If this user is newly created, back-populate the id
						if (storeInstance.getCreatedDatetime() == null) {
							storeInstance.setCreatedDatetime(updatedCancelReason.getCreatedDatetime());
						}
				}
			});
	}

}
