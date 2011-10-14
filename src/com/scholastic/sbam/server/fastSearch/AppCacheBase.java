package com.scholastic.sbam.server.fastSearch;

import com.scholastic.sbam.server.database.util.HibernateAccessor;
import com.scholastic.sbam.shared.objects.CacheStatusInstance;

public abstract class AppCacheBase implements AppCache {

	public abstract String getTableName();

	public int getExpectedCount() throws Exception {
//		long start = System.currentTimeMillis();
		int count = HibernateAccessor.count(getTableName());
//		System.out.println((System.currentTimeMillis() - start) + " : " + getTableName());
		return count;
	}

	@Override
	public CacheStatusInstance getCacheStatus() throws Exception {
		CacheStatusInstance cacheStatus = new CacheStatusInstance();
		
		cacheStatus.setKey(getKey());
		cacheStatus.setSeq(getSeq());
		cacheStatus.setName(getName());
		cacheStatus.setReady(getReady());
		cacheStatus.setLoading(getLoading());
		cacheStatus.setCountHeadings(getCountHeadings());
		cacheStatus.setExpectedCounts(getExpectedCounts());
		cacheStatus.setCounts(getProcessed());
		
		return cacheStatus;
	}
	
}
