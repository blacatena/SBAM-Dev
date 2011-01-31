package com.scholastic.sbam.client.services;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.scholastic.sbam.shared.objects.ProductServiceTreeInstance;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("updateProductServiceList")
public interface UpdateProductServiceListService extends RemoteService {
	String updateProductServiceList(String productCode, List<ProductServiceTreeInstance> list) throws IllegalArgumentException;
}
