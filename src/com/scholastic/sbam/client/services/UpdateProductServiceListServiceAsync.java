package com.scholastic.sbam.client.services;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.scholastic.sbam.shared.objects.ProductServiceTreeInstance;

public interface UpdateProductServiceListServiceAsync {

	void updateProductServiceList(String productCode, List<ProductServiceTreeInstance> list, AsyncCallback<String> callback);

}
