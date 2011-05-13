package com.scholastic.sbam.shared.objects;

/**
 * This interface allows a data instance to interact as a cached data instance.
 * 
 * For each key set, a target and keys (integer, string or both) will be used to update the database with
 * the activity.
 * 
 * Override these methods to define the number of key sets, and the category and keys for each key set.
 * @author Bob Lacatena
 *
 */
public interface UserCacheTarget {
	/**
	 * Return any category that must be updated for this cache target.
	 * @param keySet
	 * @return
	 */
	public String userCacheCategory(int keySet);
	
	/**
	 * Return the string key for a target.
	 * @param keySet
	 * @return
	 */
	public String userCacheStringKey(int keySet);
	
	/**
	 * Return the integer key for a target.
	 * @param keySet
	 * @return
	 */
	public int userCacheIntegerKey(int keySet);
	
	/**
	 * Return the number of targets (key set definitions).
	 * @return
	 */
	public int userCacheKeyCount();
}
