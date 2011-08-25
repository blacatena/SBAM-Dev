package com.scholastic.sbam.client.uiobjects.fields;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.widget.form.ComboBox;

/**
 * An enhanced combo box which adds two new features, the ability to apply type ahead when exactly one selection is available, and the ability to force the field's value back
 * to its original value if the user exists without having made a valid selection.  Also apply the key provider automatically for comparing models.
 * 
 * @author Bob Lacatena
 *
 * @param <M>
 */
public class EnhancedComboBox<M extends ModelData> extends ComboBox<M> {
	/**
	 * True to apply type ahead if there is only one qualifying value in the list.
	 */
	private boolean typeAheadOne = true;
	/**
	 * True to force a legal value to be selected on blur (meaning if the user doesn't, the previous value is restored.
	 */
	private boolean forceSelectionOnBlur = true;
	
	public M getSelectedValue() {
		return value;
	}
	
	@Override
	protected void onTypeAhead() {
		if (store.getCount() == 1)
			super.onTypeAhead();
	}

	public boolean isTypeAheadOne() {
		return typeAheadOne;
	}
	
	public void selectByKey(String key) {
		if (key == null) {
			select(null);
			return;
		}
		if (getStore().getKeyProvider() != null) {
			for (int i = 0; i < getStore().getCount(); i++) {
				if (key.equals(getStore().getKeyProvider().getKey(getStore().getAt(i)))) {
					select(getStore().getAt(i));
					return;
				}
			}
		}
		select(null);
		return;
	}

	/**
	 * True to apply type ahead if there is only one qualifying value in the list.
	 */
	public void setTypeAheadOne(boolean typeAheadOne) {
		this.typeAheadOne = typeAheadOne;
	}
	
	public void onBlur(ComponentEvent ce) {
		super.onBlur(ce);
		if (this.value == null) {
			this.value = this.originalValue;
			if (this.value == null)
				setRawValue("");
			else
				setRawValue(this.value.get(this.getDisplayField()).toString());
		}
	}

	public boolean isForceSelectionOnBlur() {
		return forceSelectionOnBlur;
	}

	/**
	 * True to force a legal value to be selected on blur (meaning if the user doesn't, the previous value is restored.
	 */
	public void setForceSelectionOnBlur(boolean forceSelectionOnBlur) {
		this.forceSelectionOnBlur = forceSelectionOnBlur;
	}
	
	@Override
	public boolean isDirty() {
	    if (disabled || !rendered) {
	        return false;
	      }
	    
	    return !equalWithNull();
	}

	/**
	 * Like Util.equalWithNull, but if the values are ModelData with a Store and a KeyProvider, use the key values provided.
	 * @param obj1
	 * @param obj2
	 * @return
	 */
	public boolean equalWithNull() {
		if (getSelectedValue() == originalValue) {
			return true;
		} else if (getSelectedValue() == null) {
			return false;
		} else if (getSelectedValue() instanceof ModelData && originalValue instanceof ModelData && this.getStore() != null && this.getStore().getKeyProvider() != null) {
	    	if (originalValue == null)
	    		return true;
	    	String key1 = this.getStore().getKeyProvider().getKey(getSelectedValue());
	    	String key2 = this.getStore().getKeyProvider().getKey(originalValue);
	    	return (key1.equals(key2));
	    } else {
	    	return getSelectedValue().equals(originalValue);
	    }
	}

}
