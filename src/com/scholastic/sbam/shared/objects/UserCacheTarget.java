package com.scholastic.sbam.shared.objects;

public interface UserCacheTarget {
	public String userCacheCategory();
	
	public String userCacheStringKey();
	
	public int userCacheIntegerKey();
	
	public int userCacheColumn();
	
	public int userCacheRow();
	
	public char userCacheState();
	
	public void storeCacheState(int row, int column, char state);
}
