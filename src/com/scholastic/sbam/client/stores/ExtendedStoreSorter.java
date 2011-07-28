package com.scholastic.sbam.client.stores;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.store.Store;
import com.extjs.gxt.ui.client.store.StoreSorter;

public class ExtendedStoreSorter extends StoreSorter<BeanModel> {
	
	public String [] otherSorts;
	
	public ExtendedStoreSorter(String [] otherSorts) {
		this.otherSorts = otherSorts;
	}
	
	@Override
	public int compare(Store<BeanModel> store, BeanModel m1, BeanModel m2, String property) {
		
		int baseCompare = super.compare(store, m1, m2, property);
		if (baseCompare != 0)
			return baseCompare;
		
		for (String otherSort : otherSorts) {
			baseCompare = super.compare(store, m1, m2, otherSort);
			if (baseCompare != 0)
				return baseCompare;
		}
		
		return 0;
	};
}