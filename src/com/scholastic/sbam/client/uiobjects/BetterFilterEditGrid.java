package com.scholastic.sbam.client.uiobjects;

import java.util.SortedMap;
import java.util.TreeMap;

import com.extjs.gxt.ui.client.widget.form.Validator;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.filters.BooleanFilter;
import com.extjs.gxt.ui.client.widget.grid.filters.DateFilter;
import com.extjs.gxt.ui.client.widget.grid.filters.GridFilters;
import com.extjs.gxt.ui.client.widget.grid.filters.NumericFilter;
import com.extjs.gxt.ui.client.widget.grid.filters.StringFilter;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Element;
import com.scholastic.sbam.client.services.FieldValidationServiceAsync;
import com.scholastic.sbam.shared.objects.BetterRowEditInstance;

/**
 * This class will create a BetterEditGrid with automatic filtering on all fields, based on the field type.
 * 
 * This behavior can be modified by overriding the adjustFilters class to add, remove or modify the field names, filter types, and filter values (for "choice" filters)
 * to be included.
 * 
 * @author Bob Lacatena
 *
 * @param <I>
 * 	The BetterRowEditInstance instance class that is the basis of the grid.
 */
public abstract class BetterFilterEditGrid<I extends BetterRowEditInstance> extends BetterEditGrid<I> {
	
	public enum FilterType {
		STRING, NUMBER, DATE, BOOLEAN, CHOICE;
	}
	
	private SortedMap<String, FilterType>	filterTypes = new TreeMap<String, FilterType>();
	private SortedMap<String, String []>	filterValues = new TreeMap<String, String []>();
	
	public void onRender(Element parent, int index) {
		super.onRender(parent, index);
	}
	
	/**
	 * Automatically load filters for all fields based on the field types.
	 */
	@Override
	protected void makeFilters() {
		if (filterTypes == null)
			return;
		
		adjustFilters();
		
		GridFilters filters = new GridFilters();  
		filters.setLocal(true);  
		    
		for (String name: filterTypes.keySet()) {
			switch (filterTypes.get(name)) {
				case STRING:	filters.addFilter(new StringFilter(name));
								break;
				case NUMBER:	filters.addFilter(new NumericFilter(name));
								break;
				case DATE:		filters.addFilter(new DateFilter(name));
								break;
				case BOOLEAN:	filters.addFilter(new BooleanFilter(name));
								break;
				case CHOICE:	if (filterValues != null && filterValues.containsKey(name)) {
//									String [] values = filterValues.get(name);
//									ListStore<ModelData> choiceStore = new ListStore<ModelData>();
//									for (int i = 0; i < values.length; i++)
//										typeStore.add(new StringInstance(whatever [i]));
//									ListFilter listFilter = new ListFilter(name, choiceStore);  
//									listFilter.setDisplayProperty("stringValue");
								}
								break;
			}
		}
		  
		
		grid.addPlugin(filters);
	
		//	Clean up; we don't need these anymore
		filterTypes = null;
		filterValues = null;
	}
	
	/**
	 * Override this method as desired to add or remove entries from the filterTypes and filterValues maps to alter how filters are created.
	 * 
	 * By default, this method does nothing, so all default filters will be added.
	 */
	protected void adjustFilters() {
		
	}
	
	protected ColumnConfig getColumn(String name, String header, int width) {
		return getColumn(name, header, width, null);
	}
	
	protected ColumnConfig getDateColumn(String name, String header, int width, String toolTip) {
		addFilter(name, FilterType.DATE);
		return super.getDateColumn(name, header, width, toolTip);
	}

	protected ColumnConfig getDateColumn(String name, String header, int width, String toolTip, DateTimeFormat format) {
		addFilter(name, FilterType.DATE);
		return super.getDateColumn(name, header, width, toolTip, format);
	}
	
	protected ColumnConfig getColumn(String name, String header, int width, String toolTip) {
		addFilter(name, FilterType.STRING);
		return super.getColumn(name, header, width, toolTip);
	}
	
	protected ColumnConfig getEditColumn(String name, String header, int width) {
		addFilter(name, FilterType.STRING);
		return super.getEditColumn(name, header, width);
	}

	protected ColumnConfig getEditColumn(String name, String header, int width, String toolTip) {
		addFilter(name, FilterType.STRING);
		return super.getEditColumn(name, header, width, toolTip);
	}
	
	protected ColumnConfig getEditColumn(String name, String header, int width, String toolTip, Validator validator, FieldValidationServiceAsync validationService) {
		addFilter(name, FilterType.STRING);
		return super.getEditColumn(name, header, width, toolTip, validator, validationService);
	}

	protected ColumnConfig getEditDateColumn(String name, String header, int width, String toolTip, Validator validator) {
		addFilter(name, FilterType.DATE);
		return super.getEditDateColumn(name, header, width, toolTip, validator);
	}
	
	protected ColumnConfig getEditCheckColumn(String name, String header, int width, String toolTip) {
		addFilter(name, FilterType.BOOLEAN);
		return super.getEditCheckColumn(name, header, width, toolTip);
	}
	
	protected ColumnConfig getComboColumn(String name, String header, int width, String [] values, String toolTip) {
		addFilter(name, FilterType.CHOICE, values);
		return super.getComboColumn(name, header, width, values, toolTip);
	}
	
	protected void addFilter(String name, FilterType type) {
		addFilter(name, type, null);
	}

	protected void addFilter(String name, FilterType type, String [] values) {
		if (filterTypes != null) {
			filterTypes.put(name, type);
			if (filterValues != null)
				filterValues.put(name, values);
		}
	}
}
