package com.scholastic.sbam.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.scholastic.sbam.shared.objects.UpdateResponse;
import com.scholastic.sbam.shared.objects.ProductInstance;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("updateProduct")
public interface UpdateProductService extends RemoteService {
	UpdateResponse<ProductInstance> updateProduct(ProductInstance instance) throws IllegalArgumentException;
}
