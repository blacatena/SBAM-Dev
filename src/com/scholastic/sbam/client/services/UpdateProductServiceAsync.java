package com.scholastic.sbam.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.scholastic.sbam.shared.objects.UpdateResponse;
import com.scholastic.sbam.shared.objects.ProductInstance;

public interface UpdateProductServiceAsync {

	void updateProduct(ProductInstance beanModel, AsyncCallback<UpdateResponse<ProductInstance>> callback);

}
