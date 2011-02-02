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
import com.scholastic.sbam.client.services.TermTypeCodeValidationService;
import com.scholastic.sbam.client.services.TermTypeCodeValidationServiceAsync;
import com.scholastic.sbam.client.services.TermTypeListService;
import com.scholastic.sbam.client.services.TermTypeListServiceAsync;
import com.scholastic.sbam.client.services.UpdateTermTypeService;
import com.scholastic.sbam.client.services.UpdateTermTypeServiceAsync;
import com.scholastic.sbam.shared.objects.TermTypeInstance;
import com.scholastic.sbam.shared.objects.UpdateResponse;
import com.scholastic.sbam.shared.validation.CodeValidator;
import com.scholastic.sbam.shared.validation.NameValidator;

/**
 * @author Bob Lacatena
 *
 */
public class TermTypeEditGrid extends BetterFilterEditGrid<TermTypeInstance> {
	
	private final TermTypeListServiceAsync termTypeListService = GWT.create(TermTypeListService.class);
	private final UpdateTermTypeServiceAsync updateTermTypeService = GWT.create(UpdateTermTypeService.class);
	private final TermTypeCodeValidationServiceAsync termTypeCodeValidationService = GWT.create(TermTypeCodeValidationService.class);
	
	@Override
	public void onRender(Element parent, int index) {
		setForceHeight(600);
		setAdditionalWidthPadding(0);
	//	setForceWidth(600);
	//	setAutoExpandColumn("spacer");
		setLayout(new CenterLayout());
	//	setLayout(new FillLayout(Orientation.VERTICAL));
		setPanelHeading("Term Types");
		super.onRender(parent, index);
	}

	@Override
	protected void asyncLoad(Object loadConfig, AsyncCallback<List<TermTypeInstance>> callback) {
		LoadConfig myLoadConfig = null;
		if (loadConfig instanceof LoadConfig)
			myLoadConfig = (LoadConfig) loadConfig;
		termTypeListService.getTermTypes(myLoadConfig, callback);
	}

	@Override
	protected void addColumns(List<ColumnConfig> columns) {
		columns.add(getEditColumn(			"termTypeCode", 	"Code", 			40,		"A unique code to identify the term type.",		new CodeValidator(1), 		termTypeCodeValidationService));
		columns.add(getEditColumn(			"description", 		"Description", 		200,	"A clear description of the term.",				new NameValidator(),		null));
		columns.add(getEditCheckColumn(		"activate",			"Activate", 		50,		"Check if use of this term can actually activate services."));
		columns.add(getEditCheckColumn(		"active",			"Active", 			50,		"Uncheck to deactivate a code value."));
		columns.add(getDateColumn(			"createdDatetime",	"Created", 			75,		"The date that this row was created."));
	}

	@Override
	protected TermTypeInstance getNewInstance() {
		TermTypeInstance termType = new TermTypeInstance();
		termType.setTermTypeCode(null);
		termType.setDescription("");
		termType.setActivate('n');
		termType.setStatus('A');
		termType.setActive(true);
		return termType;
	}

	@Override
	protected void asyncUpdate(BeanModel beanModel) {
		final BeanModel targetBeanModel = beanModel;
	//	System.out.println("Before update: " + targetBeanModel.getProperties());
		updateTermTypeService.updateTermType((TermTypeInstance) beanModel.getBean(),
				new AsyncCallback<UpdateResponse<TermTypeInstance>>() {
					public void onFailure(Throwable caught) {
						// Show the RPC error message to the user
						if (caught instanceof IllegalArgumentException)
							MessageBox.alert("Alert", caught.getMessage(), null);
						else {
							MessageBox.alert("Alert", "Term type update failed unexpectedly.", null);
							System.out.println(caught.getClass().getName());
							System.out.println(caught.getMessage());
						}
					}

					public void onSuccess(UpdateResponse<TermTypeInstance> updateResponse) {
						TermTypeInstance updatedTermType = (TermTypeInstance) updateResponse.getInstance();
						TermTypeInstance  storeInstance = targetBeanModel.getBean();
						storeInstance.setNewRecord(false);
						// If this user is newly created, back-populate the id
						if (storeInstance.getCreatedDatetime() == null) {
							storeInstance.setCreatedDatetime(updatedTermType.getCreatedDatetime());
						}
				}
			});
	}

}
