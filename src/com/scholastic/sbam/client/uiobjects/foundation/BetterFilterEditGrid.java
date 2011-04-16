package com.scholastic.sbam.client.uiobjects.foundation;

import java.util.SortedMap;
import java.util.TreeMap;

import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.form.Validator;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.filters.BooleanFilter;
import com.extjs.gxt.ui.client.widget.grid.filters.DateFilter;
import com.extjs.gxt.ui.client.widget.grid.filters.GridFilters;
import com.extjs.gxt.ui.client.widget.grid.filters.ListFilter;
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
		STRING, NUMBER, DATE, BOOLEAN, STRING_CHOICE, MODEL_CHOICE;
	}
	
	public class FilterValues {
		private String [] values				=	null;
		private ListStore<BeanModel> listStore	=	null;
		private String valueField				=	null;
		private String displayField				=	null;
		
		public FilterValues(ListStore<BeanModel> listStore, String valueField, String displayField) {
			this.listStore = listStore;
			this.valueField = valueField;
			this.displayField = displayField;
		}
		
		public FilterValues(String [] values) {
			this.values = values;
		}
		
		public String[] getValues() {
			return values;
		}
		public void setValues(String[] values) {
			this.values = values;
		}
		public ListStore<BeanModel> getListStore() {
			return listStore;
		}
		public void setListStore(ListStore<BeanModel> listStore) {
			this.listStore = listStore;
		}
		public String getValueField() {
			return valueField;
		}
		public void setValueField(String valueField) {
			this.valueField = valueField;
		}
		public String getDisplayField() {
			return displayField;
		}
		public void setDisplayField(String displayField) {
			this.displayField = displayField;
		}
		
	}
	
	/**
	 * The BetterListFilter improves on the ListFilter by properly distinguishing between a display value and a key value.
	 * 
	 * The valueProperty may be set to the name of whatever property uniquely identifies an instance in a list of instances.
	 * 
	 * The displayProperty, as before, can be set to the name of whatever property should be displayed to the user to identify an instance.
	 * 
	 * @author Bob Llacatena
	 *
	 * @param <M>
	 */
	@SuppressWarnings({"unchecked", "rawtypes"})
	public class BetterListFilter<M> extends ListFilter {
		
		private String valueProperty;

		public BetterListFilter(String dataIndex, ListStore store) {
			super(dataIndex, store);
		}

		@Override
		protected <X> X getModelValue(ModelData model) {
			return (X) ((ModelData) model.get(dataIndex)).get(valueProperty);
		}

		public String getValueProperty() {
			return valueProperty;
		}

		public void setValueProperty(String valueProperty) {
			this.valueProperty = valueProperty;
		}
	}
	
	private SortedMap<String, FilterType>			filterTypes	 = new TreeMap<String, FilterType>();
	private SortedMap<String, FilterValues>			filterValues = new TreeMap<String, FilterValues>();

	@Override
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
				case STRING_CHOICE:	if (filterValues != null && filterValues.containsKey(name)) {
										String [] values = filterValues.get(name).getValues();
										ListStore<ModelData> choiceStore = new ListStore<ModelData>();
										for (int i = 0; i < values.length; i++) {
											ModelData model = new BaseModelData();  
											model.set(name, values [i]); 
											choiceStore.add(model);
										}
										ListFilter listFilter = new ListFilter(name, choiceStore);  
										listFilter.setDisplayProperty(name);
										filters.addFilter(listFilter);
									}
									break;
				case MODEL_CHOICE:	if (filterValues != null && filterValues.containsKey(name)) {
									//	ListFilter listFilter = new ListFilter(name, filterValues.get(name).getListStore());  
									//	ListFilter listFilter = new ListFilter(filterValues.get(name).getValueField(), filterValues.get(name).getListStore());   
									//	BetterListFilter listFilter = new BetterListFilter(filterValues.get(name).getValueField(), filterValues.get(name).getListStore());   
										BetterListFilter<BeanModel> listFilter = new BetterListFilter<BeanModel>(name, filterValues.get(name).getListStore());   
										listFilter.setDisplayProperty(filterValues.get(name).getDisplayField());  
									//	listFilter.setValueProperty(filterValues.get(name).getValueField());  
										listFilter.setValueProperty(filterValues.get(name).getDisplayField());
										filters.addFilter(listFilter);
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

	@Override
	protected ColumnConfig getColumn(String name, String header, int width) {
		addFilter(name, FilterType.STRING);
		return super.getColumn(name, header, width, null);
	}

	@Override
	protected ColumnConfig getDateColumn(String name, String header, int width, String toolTip) {
		addFilter(name, FilterType.DATE);
		return super.getDateColumn(name, header, width, toolTip);
	}

	@Override
	protected ColumnConfig getDateColumn(String name, String header, int width, String toolTip, DateTimeFormat format) {
		addFilter(name, FilterType.DATE);
		return super.getDateColumn(name, header, width, toolTip, format);
	}

	@Override
	protected ColumnConfig getColumn(String name, String header, int width, String toolTip) {
		addFilter(name, FilterType.STRING);
		return super.getColumn(name, header, width, toolTip);
	}

	@Override
	protected ColumnConfig getEditColumn(String name, String header, int width) {
		addFilter(name, FilterType.STRING);
		return super.getEditColumn(name, header, width);
	}

	@Override
	protected ColumnConfig getEditColumn(String name, String header, int width, String toolTip) {
		addFilter(name, FilterType.STRING);
		return super.getEditColumn(name, header, width, toolTip);
	}

	@Override
	protected ColumnConfig getEditColumn(String name, String header, int width, String toolTip, Validator validator, FieldValidationServiceAsync validationService) {
		addFilter(name, FilterType.STRING);
		return super.getEditColumn(name, header, width, toolTip, validator, validationService);
	}

	@Override
	protected ColumnConfig getEditDateColumn(String name, String header, int width, String toolTip, Validator validator) {
		addFilter(name, FilterType.DATE);
		return super.getEditDateColumn(name, header, width, toolTip, validator);
	}

	@Override
	protected ColumnConfig getEditCheckColumn(String name, String header, int width, String toolTip) {
		addFilter(name, FilterType.BOOLEAN);
		return super.getEditCheckColumn(name, header, width, toolTip);
	}
	
	@Override
	protected ColumnConfig getComboColumn(String name, String header, int width, String toolTip, String [] values) {
		addFilter(name, FilterType.STRING_CHOICE, values);
		return super.getComboColumn(name, header, width, toolTip, values);
	}
	
	@Override
	protected ColumnConfig getComboColumn(String name, String header, int width, String toolTip, String [] values, Validator validator) {
		addFilter(name, FilterType.STRING_CHOICE, values);
		return super.getComboColumn(name, header, width, toolTip, values, validator);
	}

	@Override
	protected ColumnConfig getComboColumn(String name, String header, int width, String toolTip, BeanModel [] values, String valueField, String displayField, Validator validator) {
		return super.getComboColumn(name, header, width, toolTip, values, valueField, displayField, validator);
	}

	@Override
	protected ColumnConfig getComboColumn(String name, String header, int width, String toolTip, ListStore<BeanModel> listStore, String valueField, String displayField, Validator validator) {
		addFilter(name, FilterType.MODEL_CHOICE, listStore, valueField, displayField);
		return super.getComboColumn(name, header, width, toolTip, listStore, valueField, displayField, validator);
	}
	
	protected void addFilter(String name, FilterType type) {
		if (filterTypes != null && !filterTypes.containsKey(name)) {
			filterTypes.put(name, type);
		}
	}

	protected void addFilter(String name, FilterType type, String [] values) {
		addFilter(name, type);
		if (filterValues != null)
			filterValues.put(name, new FilterValues(values));
	}

	protected void addFilter(String name, FilterType type, ListStore<BeanModel> listStore, String valueField, String displayField) {
		addFilter(name, type);
		if (filterValues != null)
			filterValues.put(name, new FilterValues(listStore, valueField, displayField));
	}
}
