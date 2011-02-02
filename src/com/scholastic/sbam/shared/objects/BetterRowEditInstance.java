package com.scholastic.sbam.shared.objects;

import com.google.gwt.user.client.rpc.IsSerializable;

public abstract class BetterRowEditInstance implements IsSerializable {
	private boolean newRecord = false;
	
	public boolean isNewRecord() {
		return newRecord;
	}
	
	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}
	
	/**
	 * Implement this method to set any property values that show that this instance is to be deleted from the database (or marked as such).
	 */
	public abstract void markForDeletion();
	
	/**
	 * Implement this method to determine if a record exists but is marked as deleted.
	 * @return
	 */
	public abstract boolean thisIsDeleted();
	
	/**
	 * Implement this method, if desired, to determine if the values in a row taken together are valid (e.g. if two distinct properties have compatible values).
	 * @return
	 */
	public abstract boolean thisIsValid();
	
	/**
	 * Implement this method to return the name of a property that will be changed to trigger deletion.  It need not be a property that actually exists in the instance.
	 * 
	 * This property would usually, but need not be, the property changed by the markForDeletion() method.
	 * 
	 * Some property is needed here, however, to for the row editor to actually send an async update message to the server.
	 * @return
	 *  The name of the property that will be set to trigger an async update message to the server.
	 */
	public abstract String	returnTriggerProperty();

	
	/**
	 * Implement this method to return the value that will be set in the trigger property to trigger deletion.
	 * 
	 * @return
	 *  The value that will be set to trigger an async update message to the server.
	 */
	public abstract String	returnTriggerValue();
	
}
