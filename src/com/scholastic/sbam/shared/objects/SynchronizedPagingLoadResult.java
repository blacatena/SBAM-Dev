package com.scholastic.sbam.shared.objects;

import java.util.List;

import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * This variation on BasePagingLoadResult includes a synchronization ID so that the recipient can be sure that old, slow, late search results don't overwrite newer, faster, more appropriate
 * results which arrived sooner.
 * 
 * It is up to the calling process to use this information to determine whether these results should be displayed, or discarded.
 * 
 * Sadly, this could not simply extend BasePagingLoadResult, because Sencha foolishly made the default, and there's no way around it.
 * 
 * To use this class, send the syncId in the call to the service, which should then return it through this class, along with the actual results.  The
 * callback method should test this syncId against the most recent used, and discard results that don't match.
 * 
 * Note that to combine this with the GXT code to make use of PagingLoadResult responses, the asynch call must be wrapped in another call (although this is commonly done for other reasons
 * as well.
 * 
 * Example:
 * 
 * 	protected PagingLoader<PagingLoadResult<InstitutionInstance>> getInstitutionLoader() {
 * 		// proxy and reader  
 * 		RpcProxy<PagingLoadResult<InstitutionInstance>> proxy = new RpcProxy<PagingLoadResult<InstitutionInstance>>() {  
 * 			@Override  
 * 			public void load(Object loadConfig, final AsyncCallback<PagingLoadResult<InstitutionInstance>> callback) {
 * 		    	
 * 				// Here the callback is overridden so that it can catch errors and alert the users.  Then the original callback is told of the failure.
 * 				// On success, the original callback is just passed the onSuccess message, and the response (the list).
 * 				
 * 				AsyncCallback<SynchronizedPagingLoadResult<InstitutionInstance>> myCallback = new AsyncCallback<SynchronizedPagingLoadResult<InstitutionInstance>>() {
 * 					public void onFailure(Throwable caught) {
 * 						// Show the RPC error message to the user
 * 						MessageBox.alert("Alert", "Institution load failed unexpectedly.", null);
 *						// Let the usual callback method do its thing
 * 						callback.onFailure(caught);
 * 					}
 * 
 * 					public void onSuccess(SynchronizedPagingLoadResult<InstitutionInstance> syncResult) {
 *						// Ignore late responses
 * 						if(syncResult.getSyncId() != searchSyncId)
 * 							return;
 * 						// Get the actual PagingLoadResult object and pass it on to let the usual callback do its thing
 * 						PagingLoadResult<InstitutionInstance> result = syncResult.getResult();
 * 						callback.onSuccess(result);
 * 					}
 * 				};
 * 
 * 				//	Set up the call with a unique synch ID
 * 				searchSyncId = System.currentTimeMillis();
 * 				institutionSearchService.getInstitutions((PagingLoadConfig) loadConfig, filter, searchSyncId, myCallback);
 * 				
 * 		    }  
 * 		};
 * 		BeanModelReader reader = new BeanModelReader();
 * 		
 * 		// Note that this is defined with a PagingLoadResult... the SynchronizedPagingLoadResult only exists within the wrapped callback.
 * 		PagingLoader<PagingLoadResult<InstitutionInstance>> loader = new BasePagingLoader<PagingLoadResult<InstitutionInstance>>(proxy, reader);
 * 		return loader;
 * 	}
 * 
 * @author Bob Lacatena
 *
 * @param <Data>
 */
public class SynchronizedPagingLoadResult<Data> implements IsSerializable {
	
	/**
	 * A synchronization ID used to match requests with results.
	 */
	private long syncId;
	private BasePagingLoadResult<Data> result;
	
	public SynchronizedPagingLoadResult(List<Data> data, long syncId) {
		this.result = new BasePagingLoadResult<Data>(data);
		this.syncId = syncId;
	}
	
	public SynchronizedPagingLoadResult(List<Data> data, int offset, int totalLength, long syncId) {
		this.result = new BasePagingLoadResult<Data>(data, offset, totalLength);
		this.syncId = syncId;
	}
	
	public SynchronizedPagingLoadResult() {
		
	}

	public long getSyncId() {
		return syncId;
	}

	public void setSyncId(long syncId) {
		this.syncId = syncId;
	}

	public BasePagingLoadResult<Data> getResult() {
		return result;
	}

	public void setResult(BasePagingLoadResult<Data> result) {
		this.result = result;
	}
	
}
