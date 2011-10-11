package com.scholastic.sbam.shared.objects;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.BeanModelFactory;
import com.extjs.gxt.ui.client.data.BeanModelLookup;
import com.extjs.gxt.ui.client.data.BeanModelTag;
import com.google.gwt.user.client.rpc.IsSerializable;

public class CacheStatusInstance extends BetterRowEditInstance implements BeanModelTag, IsSerializable, UserCacheTarget {

	public static final int CACHE_KEY_SET	= 0;
	
	public static final String	INSTITUTION_CACHE_KEY	=	"Inst";
	public static final String	SITE_CACHE_KEY			=	"Site";
	public static final String	CUSTOMER_CACHE_KEY		=	"Cust";
	public static final String	HELP_TEXT_CACHE_KEY		=	"Help";
	
	private static BeanModelFactory beanModelfactory;

	protected	String		key;
	protected	int			seq;
	protected	String		name;
	protected	boolean		ready;
	protected	boolean		loading;
	protected	String []	countHeadings;
	protected	int []		counts;
	protected	int	[]		expectedCounts;
	
	@Override
	public void markForDeletion() {
	}

	@Override
	public boolean thisIsDeleted() {
		return false;
	}

	@Override
	public boolean thisIsValid() {
		return true;
	}

	@Override
	public String returnTriggerProperty() {
		return "junk";
	}

	@Override
	public String returnTriggerValue() {
		return "junk";
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public int getSeq() {
		return seq;
	}

	public void setSeq(int seq) {
		this.seq = seq;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isReady() {
		return ready;
	}

	public void setReady(boolean ready) {
		this.ready = ready;
	}

	public boolean isLoading() {
		return loading;
	}

	public void setLoading(boolean loading) {
		this.loading = loading;
	}

	public int [] getCounts() {
		return counts;
	}

	public void setCounts(int [] counts) {
		this.counts = counts;
	}

	public int [] getExpectedCounts() {
		return expectedCounts;
	}

	public void setExpectedCounts(int [] expectedCounts) {
		this.expectedCounts = expectedCounts;
	}
	
	public String[] getCountHeadings() {
		return countHeadings;
	}

	public void setCountHeadings(String[] countHeadings) {
		this.countHeadings = countHeadings;
	}
	
	public String getCountHeading(int idx) {
		if (countHeadings == null || countHeadings.length <= idx)
			return "";
		return countHeadings [idx];
	}
	
	public int getExpectedCount(int idx) {
		if (expectedCounts == null || expectedCounts.length <= idx)
			return 0;
		return expectedCounts [idx];
	}
	
	public int getCount(int idx) {
		if (counts == null || counts.length <= idx)
			return 0;
		return counts [idx];
	}

	public double getPercentCompleted(int idx) {
		if (expectedCounts == null || expectedCounts.length <= idx)
			return 0d;
		return 1d * getCount(idx) / getExpectedCount(idx);
	}
	
	public String getCountHeading() {
		return getCountHeading(0);
	}
	
	public int getExpectedCount() {
		return getExpectedCount(0);
	}
	
	public int getCount() {
		return getCount(0);
	}

	public double getPercentCompleted() {
		return getPercentCompleted(0);
	}
	
	public String getCountHeading2() {
		return getCountHeading(1);
	}
	
	public int getExpectedCount2() {
		return getExpectedCount(1);
	}
	
	public int getCount2() {
		return getCount(1);
	}
	
	public String getCountHeading3() {
		return getCountHeading(2);
	}
	
	public int getExpectedCount3() {
		return getExpectedCount(2);
	}
	
	public int getCount3() {
		return getCount(2);
	}
	
	public String getCountHeading4() {
		return getCountHeading(3);
	}
	
	public int getExpectedCount4() {
		return getExpectedCount(3);
	}
	
	public int getCount4() {
		return getCount(3);
	}

	public double getPercentCompleted2() {
		return getPercentCompleted(1);
	}
	
	public static String [] getAllCacheKeys() {
		return new String [] {INSTITUTION_CACHE_KEY, SITE_CACHE_KEY, CUSTOMER_CACHE_KEY, HELP_TEXT_CACHE_KEY};
	}

	public static BeanModel obtainModel(CacheStatusInstance instance) {
		if (beanModelfactory == null)
			beanModelfactory  = BeanModelLookup.get().getFactory(CacheStatusInstance.class);
		BeanModel model = beanModelfactory.createModel(instance);
		return model;
	}
	
	public static String getUserCacheCategory() {
		return "CacheStatus";
	}

	@Override
	public String userCacheCategory(int keySet) {
		return getUserCacheCategory();
	}

	@Override
	public String userCacheStringKey(int keySet) {
		return key;
	}

	@Override
	public int userCacheIntegerKey(int keySet) {
		return 0;
	}
	
	@Override
	public int userCacheKeyCount() {
		return 1;
	}
}
