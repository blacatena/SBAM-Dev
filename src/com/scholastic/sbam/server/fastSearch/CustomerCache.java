package com.scholastic.sbam.server.fastSearch;

import com.scholastic.sbam.shared.objects.CacheStatusInstance;

/**
 * This version of the institution cache is restricted to customers with agreements (active or not).
 * 
 * It is updateable, so that new agreements (or customer changes) can be reflected in the cache.
 * 
 * Note that customers are not interactively removed from the cache, only added.  The search servlet can take care of filtering out customers without current, active agreements.
 * 
 * @author Bob Lacatena
 *
 */
public class CustomerCache extends InstitutionCache {
	
	protected static CustomerCache singleton = null;
	
	protected static String CUSTOMER_SQL_FROM = "institution, agreement WHERE institution.ucn = agreement.bill_ucn and agreement.status <> 'X' ";
	
	/**
	 * SQL statement with a join to agreement
	 */
	protected static final String CUSTOMER_SQL = "SELECT DISTINCT ucn, parent_ucn, institution_name, address1, address2, address3, city, state, zip, country, phone, fax, alternate_ids FROM " + CUSTOMER_SQL_FROM;
	
//	public CustomerCache() {
//		config = new InstitutionCacheConfig();
//		config.setUpdateable(true);
//		init();
//	}
	
	public CustomerCache(InstitutionCacheConfig config) {
		if (config == null)
			config = new InstitutionCacheConfig();
		config.setUpdateable(true);
		this.config = config;
		init();
	}
	
	@Override
	public String getCacheName() {
		return "Customer Cache";
	}
	
	@Override
	protected String getBaseSqlStatement() {
		return CUSTOMER_SQL;
	}
	
	public static synchronized CustomerCache getSingleton(InstitutionCacheConfig config) throws InstitutionCacheConflict {
		if (singleton == null) {
			singleton = new CustomerCache(config);
		} else {
			if (config != null && singleton.config != config && !configsAreEqual(singleton.config, config)) {
			//	Config has changed, so we need to re-initialize the singleton	
				if (!singleton.init(config)) {
					//	If it wouldn't reinitialize because another init was running, then this particular request failed
					System.out.println("Running   config -- " + singleton.config);
					System.out.println("Requested config -- " + config);
					throw new InstitutionCacheConflict();
				}
			}
		}
		//	In any event, return the singleton we have here
		return singleton;
	}

	
	public static synchronized CustomerCache getSingleton() throws InstitutionCacheConflict {
		return getSingleton(null);
	}

	
	@Override
	public String getTableName() {
		return CUSTOMER_SQL_FROM;
	}

	@Override
	public String getName() {
		return "Customer Cache";
	}

	@Override
	public int getSeq() {
		return 1;
	}

	@Override
	public String getKey() {
		return CacheStatusInstance.CUSTOMER_CACHE_KEY;
	}
}
