package com.scholastic.sbam.client.stores;

import com.extjs.gxt.ui.client.data.ModelComparer;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.store.Store;

public class KeyModelComparer<M extends ModelData> implements ModelComparer<M> {

	Store<M>	store;
	
	public KeyModelComparer(Store<M> store) {
		super();
		this.store = store;
	}
	
	@Override
	public boolean equals(M m1, M m2) {
		if (store.getKeyProvider() == null)
		    return (m1 == m2 || (m1 != null && m1.equals(m2)));
		
		String key1 = store.getKeyProvider().getKey(m1);
		String key2 = store.getKeyProvider().getKey(m2);
		
		return (key1 == key2 || (key1 != null && key1.equals(key2)));
	}

}
