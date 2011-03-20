package com.scholastic.sbam.client.stores;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.StoreFilter;

public class BetterFilterListStore<M extends ModelData> extends ListStore<M> {
	protected boolean filterContainsMode	=	true;
	@Override
	@SuppressWarnings({"unchecked", "rawtypes"})
	protected boolean isFiltered(ModelData record, String property) {
		if (filterBeginsWith != null && property != null) {
			Object o = record.get(property);
			if (o != null) {
				if (filterContainsMode) {
					if (o.toString().toLowerCase().indexOf(filterBeginsWith.toLowerCase()) < 0) {
						return true;
					}
				} else {
					if (!o.toString().toLowerCase().startsWith(filterBeginsWith.toLowerCase())) {
						return true;
					}
				}
			}
		}
		if (filters != null) {
			for (StoreFilter filter : filters) {
				boolean result = filter.select(this, record, record, property);
				if (!result) {
					return true;
				}
			}
		}
		return false;
	}
	public boolean isFilterContainsMode() {
		return filterContainsMode;
	}
	public void setFilterContainsMode(boolean filterContainsMode) {
		this.filterContainsMode = filterContainsMode;
	}
	
}
