package com.scholastic.sbam.shared.objects;

public abstract class BetterRowEditInstance {
	
	public abstract void markForDeletion();
	
	public abstract boolean thisIsDeleted();
	
	public abstract boolean thisIsNewRecord();
	
	public abstract boolean thisIsValid();
	
	public abstract String	returnTriggerProperty();
	
	public abstract String	returnTriggerValue();
	
}
