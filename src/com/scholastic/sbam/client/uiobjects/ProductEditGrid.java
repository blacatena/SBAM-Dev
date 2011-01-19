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
import com.scholastic.sbam.client.services.ProductCodeValidationService;
import com.scholastic.sbam.client.services.ProductCodeValidationServiceAsync;
import com.scholastic.sbam.client.services.ProductListService;
import com.scholastic.sbam.client.services.ProductListServiceAsync;
import com.scholastic.sbam.client.services.TermTypeCodeValidationService;
import com.scholastic.sbam.client.services.TermTypeCodeValidationServiceAsync;
import com.scholastic.sbam.client.services.UpdateProductService;
import com.scholastic.sbam.client.services.UpdateProductServiceAsync;
import com.scholastic.sbam.shared.objects.ProductInstance;
import com.scholastic.sbam.shared.objects.UpdateResponse;
import com.scholastic.sbam.shared.validation.CodeValidator;
import com.scholastic.sbam.shared.validation.NameValidator;

/**
 * @author Bob Lacatena
 *
 */
public class ProductEditGrid extends BetterFilterEditGrid<ProductInstance> {
	
	private final ProductListServiceAsync productListService = GWT.create(ProductListService.class);
	private final UpdateProductServiceAsync updateProductService = GWT.create(UpdateProductService.class);
	private final ProductCodeValidationServiceAsync productCodeValidationService = GWT.create(ProductCodeValidationService.class);
	private final TermTypeCodeValidationServiceAsync termTypeCodeValidationService = GWT.create(TermTypeCodeValidationService.class);
	
	@Override
	public void onRender(Element parent, int index) {
		setForceHeight(600);
		setAdditionalWidthPadding(14);
	//	setForceWidth(600);
	//	setAutoExpandColumn("spacer");
		setLayout(new CenterLayout());
	//	setLayout(new FillLayout(Orientation.VERTICAL));
		setPanelHeading("Products");
		super.onRender(parent, index);
	}

	@Override
	protected void asyncLoad(Object loadConfig, AsyncCallback<List<ProductInstance>> callback) {
		LoadConfig myLoadConfig = null;
		if (loadConfig instanceof LoadConfig)
			myLoadConfig = (LoadConfig) loadConfig;
		productListService.getProducts(myLoadConfig, callback);
	}

	@Override
	protected void addColumns(List<ColumnConfig> columns) {
		columns.add(getEditColumn(			"productCode", 			"Code", 			100,	"A unique code to identify the reason for a cancellation.",				new CodeValidator(6), 		productCodeValidationService));
		columns.add(getEditColumn(			"description", 			"Description", 		200,	"A clear description of the reason for the cancellation.",				new NameValidator(),		null));
		columns.add(getEditColumn(			"shortName", 			"Short Name", 		100,	"A short name to use for the product.",									new NameValidator(),		null));
		columns.add(getEditColumn(			"defaultTermType", 		"Default<BR/>Term<BR/>Type",50,		"The term type to initially use for new terms for this product.",	new CodeValidator(1), 	null)); //	termTypeCodeValidationService));
		columns.add(getEditCheckColumn(		"active",				"Active", 			50,		"Uncheck to deactivate a code value."));
		columns.add(getDateColumn(			"createdDatetime",		"Created", 			75,		"The date that this row was created."));
	}

	@Override
	protected ProductInstance getNewInstance() {
		ProductInstance product = new ProductInstance();
		product.setProductCode(null);
		product.setDescription("");
		product.setShortName("");
	//	product.setDefaultTermType('I');
		product.setStatus('A');
		product.setActive(true);
		return product;
	}

	@Override
	protected void asyncUpdate(BeanModel beanModel) {
		final BeanModel targetBeanModel = beanModel;
	//	System.out.println("Before update: " + targetBeanModel.getProperties());
		updateProductService.updateProduct((ProductInstance) beanModel.getBean(),
				new AsyncCallback<UpdateResponse<ProductInstance>>() {
					public void onFailure(Throwable caught) {
						// Show the RPC error message to the user
						if (caught instanceof IllegalArgumentException)
							MessageBox.alert("Alert", caught.getMessage(), null);
						else {
							MessageBox.alert("Alert", "Product update failed unexpectedly.", null);
							System.out.println(caught.getClass().getName());
							System.out.println(caught.getMessage());
						}
					}

					public void onSuccess(UpdateResponse<ProductInstance> updateResponse) {
						ProductInstance updatedProduct = (ProductInstance) updateResponse.getInstance();
						// If this user is newly created, back-populate the id
						if (targetBeanModel.get("createdDatetime") == null) {
							targetBeanModel.set("createdDatetime", updatedProduct.getCreatedDatetime());
						}
				}
			});
	}

}
