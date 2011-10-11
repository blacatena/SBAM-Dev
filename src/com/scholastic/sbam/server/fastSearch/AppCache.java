package com.scholastic.sbam.server.fastSearch;

import com.scholastic.sbam.shared.objects.CacheStatusInstance;

public interface AppCache {
	public int	[]	getExpectedCounts() throws Exception;
	
	public int	[]	getProcessed();
	
	public String [] getCountHeadings();
	
	public boolean	getReady();
	
	public boolean	getLoading();
	
	public String	getName();
	
	public int		getSeq();
	
	public String	getKey();
	
	public CacheStatusInstance getCacheStatus() throws Exception;
}
