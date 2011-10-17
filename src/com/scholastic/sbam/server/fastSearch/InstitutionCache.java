package com.scholastic.sbam.server.fastSearch;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import com.scholastic.sbam.server.database.codegen.InstitutionGroup;
import com.scholastic.sbam.server.database.codegen.InstitutionPubPriv;
import com.scholastic.sbam.server.database.codegen.InstitutionType;
import com.scholastic.sbam.server.database.objects.DbInstitutionGroup;
import com.scholastic.sbam.server.database.objects.DbInstitutionPubPriv;
import com.scholastic.sbam.server.database.objects.DbInstitutionType;
import com.scholastic.sbam.server.database.util.HibernateUtil;
import com.scholastic.sbam.shared.objects.CacheStatusInstance;
import com.scholastic.sbam.shared.objects.InstitutionGroupInstance;
import com.scholastic.sbam.shared.objects.InstitutionInstance;
import com.scholastic.sbam.shared.objects.InstitutionPubPrivInstance;
import com.scholastic.sbam.shared.objects.InstitutionTypeInstance;
import com.scholastic.sbam.shared.util.AppConstants;

/**
 * This class acts as a cache, to speed institution searches and sorting.
 * 
 * @author Bob Lacatena
 *
 */
public class InstitutionCache extends AppCacheBase implements Runnable {
	protected static final boolean FIRST_PASS_READY	= false;	//	Are maps ready for use after the first pass?
	protected static final boolean SECOND_PASS_READY	=	true;	//	Are maps ready for use after the second pass?
	
	protected static final String INSTITUTION_SQL = "SELECT ucn, parent_ucn, institution_name, address1, address2, address3, city, state, zip, country, phone, fax, alternate_ids FROM institution WHERE ucn > 0 ";
	
	public static class InstitutionCacheNotReady extends Exception {
		protected static final long serialVersionUID = -8657762616708856381L;
	}
	
	public static class InstitutionCacheConflict extends Exception {
		protected static final long serialVersionUID = -5649410161902019748L;
	}
	
	public static class InstitutionCacheConfig {
		protected long		id = System.currentTimeMillis();
		/**
		 * Load garbage collection point
		 */
		protected int		loadGcPoint = 100000;
		/**
		 * Load garbage collection point
		 */
		protected int		loadWatchPoint = 5000;
		/**
		 * Load limit (max customers)
		 */
		protected int		loadLimit	= -1;
		/**
		 * Whether or not inner strings should be used, or only the strings that begin words.
		 */
		protected boolean	useInnerStrings		= false;
		/**
		 * Whether or not pairs of strings should be used, or only the strings alone.
		 */
		protected boolean	useStringPairs		= false;
		/**
		 * The minimum length to bother even considering for a search string.
		 */
		protected int	 	minStringLength	= 2;
		/**
		 * The minimum length to bother with in creating inner strings.
		 */
		protected int	 	minInnerStringLength	= 4;
		/**
		 * The maximum length to bother with in creating string pairs.
		 */
		protected int	 	maxStringPairLength	= 5;
		/**
		 * The maximum useful length of a list.  Anything over this is stored only as a count in the countMap.
		 */
		protected int		maxListLength = 500;
		/**
		 * The maximum number of words to list for a prefix.  Anything over this is removed from the list.
		 */
		protected int		maxWordListLength	= 500;
		protected String	loadStatusList;
		/**
		 * Can this cache be updated (by adding more institutions) after it is generated?  If true, more memory is required.
		 */
		protected boolean	updateable			= false;
		
		public String toString() {
			return "Institution Cache Config : [ID = " + id + ", Inner = " + useInnerStrings + ", Pairs = " + useStringPairs + ", min len = " + minStringLength + ", min inner = " + minInnerStringLength + 
					", max pair = " + maxStringPairLength + ", max ucn list = " + maxListLength + ", max word list = " + maxWordListLength + ", load statuses " + loadStatusList + "]";
		}
		
		public boolean isUseInnerStrings() {
			return useInnerStrings;
		}
		public void setUseInnerStrings(boolean useInnerStrings) {
			this.useInnerStrings = useInnerStrings;
		}
		public boolean isUseStringPairs() {
			return useStringPairs;
		}
		public void setUseStringPairs(boolean useStringPairs) {
			this.useStringPairs = useStringPairs;
		}
		public int getMinStringLength() {
			return minStringLength;
		}
		public void setMinStringLength(int minStringLength) {
			this.minStringLength = minStringLength;
		}
		public int getMinInnerStringLength() {
			return minInnerStringLength;
		}
		public void setMinInnerStringLength(int minInnerStringLength) {
			this.minInnerStringLength = minInnerStringLength;
		}
		public int getMaxStringPairLength() {
			return maxStringPairLength;
		}
		public void setMaxStringPairLength(int maxStringPairLength) {
			this.maxStringPairLength = maxStringPairLength;
		}
		public int getMaxListLength() {
			return maxListLength;
		}
		public void setMaxListLength(int maxListLength) {
			this.maxListLength = maxListLength;
		}
		public int getMaxWordListLength() {
			return maxWordListLength;
		}
		public void setMaxWordListLength(int maxWordListLength) {
			this.maxWordListLength = maxWordListLength;
		}
		public int getLoadGcPoint() {
			return loadGcPoint;
		}
		public void setLoadGcPoint(int loadGcPoint) {
			this.loadGcPoint = loadGcPoint;
		}
		public int getLoadWatchPoint() {
			return loadWatchPoint;
		}
		public void setLoadWatchPoint(int loadWatchPoint) {
			this.loadWatchPoint = loadWatchPoint;
		}
		public int getLoadLimit() {
			return loadLimit;
		}
		public void setLoadLimit(int loadLimit) {
			this.loadLimit = loadLimit;
		}
		public String getLoadStatusList() {
			return loadStatusList;
		}
		public void setLoadStatusList(String loadStatusList) {
			this.loadStatusList = loadStatusList;
		}
		public boolean isUpdateable() {
			return updateable;
		}
		public void setUpdateable(boolean updateable) {
			this.updateable = updateable;
		}

		public InstitutionCacheConfig clone() {
			InstitutionCacheConfig clone = new InstitutionCacheConfig();
			
			clone.loadGcPoint			=	this.loadGcPoint;
			clone.loadWatchPoint		=	this.loadWatchPoint;
			clone.loadLimit				=	this.loadLimit;
			clone.useInnerStrings		=	this.useInnerStrings;
			clone.useStringPairs		=	this.useStringPairs;
			clone.minStringLength		=	this.minStringLength;
			clone.minInnerStringLength	=	this.minInnerStringLength;
			clone.maxStringPairLength	=	this.maxStringPairLength;
			clone.maxListLength			=	this.maxListLength;
			clone.maxWordListLength		=	this.maxWordListLength;
			clone.loadStatusList		=	this.loadStatusList;
			clone.updateable			=	this.updateable;
			
			return clone;
		}
	}
	
	protected static InstitutionCache singleton;	//	 = new InstitutionCache();
	
	/**
	 * The cache configuration
	 */
	protected InstitutionCacheConfig config;
	
	/**
	 * Whether or not the init thread is running.
	 */
	protected boolean initRunning		= false;
	/**
	 * Whether or not a reload is in progress and data is ready
	 */
	protected boolean mapsReady			= false;
	
	protected int ucns = 0;
	
	protected HashMap<String, SortedSet<String>>	wordMap	  = new HashMap<String, SortedSet<String>>();
	protected HashMap<String, List<Integer>>		searchMap = new HashMap<String, List<Integer>>();
	protected HashMap<String, Integer>				countMap  = new HashMap<String, Integer>();
	protected HashSet<Integer>						ucnSet	  = new HashSet<Integer>();
	
	// These can just be server side, because they only change when the institutions change (i.e. its externally defined data)
	protected HashMap<String, InstitutionGroupInstance>	groups		= new HashMap<String, InstitutionGroupInstance>();
	protected HashMap<String, InstitutionTypeInstance>	types		= new HashMap<String, InstitutionTypeInstance>();
	protected HashMap<String, InstitutionPubPrivInstance>	pubPrivs= new HashMap<String, InstitutionPubPrivInstance>();
	
	
	protected InstitutionCache() {
//		config = new InstitutionCacheConfig();
//		init(config);
	}
	
	protected InstitutionCache(InstitutionCacheConfig config) {
		if (config != null)
			this.config = config;
		init(this.config);
	}
	
	/**
	 * This class returns the singleton with new, specific run parameters.
	 * 
	 * This method can fail!  If a previous call to initialize the singleton is running, then a new one cannot be created (unless there were unlimited memory,
	 * or we took the trouble to kill the old thread and clean it up first).  Instead, this method returns null, signaling the calling process that it
	 * needs to worry about canceling its own request, or waiting and requesting the singleton again.
	 * 
	 * This is no different from the fact that any request to use the singleton must also check that it is ready.
	 * 
	 * @param useInnerStrings
	 * @param minStringLength
	 * @param maxListLength
	 * @param maxWordListLength
	 * @return
	 */
	public static synchronized InstitutionCache getSingleton(InstitutionCacheConfig config) throws InstitutionCacheConflict {
		if (singleton == null) {
			singleton = new InstitutionCache(config);
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
	
	protected boolean init() {
		return init(config);
	}
	
	protected static boolean configsAreEqual(InstitutionCacheConfig config1, InstitutionCacheConfig config2) {
		if (config1.useInnerStrings != config2.useInnerStrings)
			return false;
		if (config1.useStringPairs != config2.useStringPairs)
			return false;
		if (config1.minStringLength != config2.minStringLength)
			return false;
		if (config1.minInnerStringLength != config2.minInnerStringLength)
			return false;
		if (config1.maxStringPairLength != config2.maxStringPairLength)
			return false;
		if (config1.maxListLength != config2.maxListLength)
			return false;
		if (config1.maxWordListLength != config2.maxWordListLength)
			return false;
		return true;
	}
	
	public String getCacheName() {
		return "Institution Cache";
	}
	
	/**
	 * Initialize the map from the institutions in the database.
	 */
	protected synchronized boolean init(InstitutionCacheConfig config) {
		if (initRunning)
			return false;
		
		initRunning = true;
		mapsReady   = false;
		ucns        = 0;
		
		this.config = config;
		
		System.out.println(getCacheName() + " init with " + this.config);
		System.out.println(getCacheName() + " cache thread starting...");
		Thread initThread = new Thread(this);
		initThread.setDaemon(true);
		initThread.start();
		System.out.println(getCacheName() + " cache thread running.");
		return true;
	}

	
	/**
	 * Threaded code to initialize the map from the institutions in the database.
	 */
	public synchronized void run() {
		initRunning = true;
		mapsReady = false;
		
		ucns = 0;
		searchMap = new HashMap<String, List<Integer>>();
		countMap  = new HashMap<String, Integer>();
		wordMap	  = new HashMap<String, SortedSet<String>>();
		
		System.gc();
		
		HibernateUtil.openSession();
		HibernateUtil.startTransaction();

		try {
			
			loadCodes();

			int count1 = loadPass(1);
			
			int wordCount1 = cleanUp();

			System.out.println(new Date());
			System.out.println(getCacheName() + " : " + count1 + " institutions parsed and loaded into a map of size " + searchMap.size() + ".");
			System.out.println(getCacheName() + " : " + wordMap.size() + " word prefixes with " + wordCount1 + " words stored.");
			System.out.println(getCacheName() + " : " + countMap.size() + " words with counts only, " + searchMap.size() + " words with UCNs.");
		
			mapsReady = FIRST_PASS_READY;
			
			if (config.useStringPairs) {
				
				int count2 = loadPass(2);
				
			//	int wordCount2 = cleanUp();	Don't need this, because the pairs didn't change the word map
				
				System.out.println(new Date());
				System.out.println(getCacheName() + " : " + count2 + " institutions parsed and loaded into a map of size " + searchMap.size() + ".");
			//	System.out.println(getCacheName() + " : " + wordMap.size() + " word prefixes with " + wordCount2 + " words stored.");
				System.out.println(getCacheName() + " : " + countMap.size() + " words with counts only, " + searchMap.size() + " words with UCNs.");
				
			}
			
			mapsReady = SECOND_PASS_READY;
			
		} catch (Exception exc) {
			exc.printStackTrace();
		}
		
		HibernateUtil.endTransaction();
		HibernateUtil.closeSession();

		Runtime.getRuntime().gc();
		System.out.println(getCacheName() + " : " + "Total memory: " + Runtime.getRuntime().totalMemory());
		System.out.println(getCacheName() + " : " + "Free  memory: " + Runtime.getRuntime().freeMemory());
		System.out.println(getCacheName() + " : " + "Max   memory: " + Runtime.getRuntime().maxMemory());
		
		initRunning = false;
		
//		dumpWordStats();
//		for (String mapped : searchMap.keySet())
//			if (mapped.indexOf(' ') >= 0) System.out.println(mapped);
	}
	
	protected String getBaseSqlStatement() {
		return INSTITUTION_SQL;
	}
	
	protected String getSqlStatement() {
		StringBuffer sb = new StringBuffer(getBaseSqlStatement());
		
		appendStatusClause(sb);
		
		return sb.toString();
	}
	
	protected void appendStatusClause(StringBuffer sb) {
		if (config.loadStatusList != null && config.loadStatusList.length() > 0) {
			for (int i = 0; i < config.loadStatusList.length(); i++) {
				char status = config.loadStatusList.charAt(i);
				if (i == 0) {
					if (status == '~') {
						sb.append(" AND `institution`.`status` not in (");
					} else {
						sb.append(" AND `institution`.`status` in (");
					}
				}
				if (status != '~' && status != '=') {
					if (i > 1 || (i > 0 && config.loadStatusList.charAt(0) != '~' && config.loadStatusList.charAt(0) != '='))
						sb.append(",");
					sb.append("'");
					sb.append(status);
					sb.append("'");
				}
			}
			sb.append(")");
		}
		
	}
	
	protected void loadCodes() {
		types.clear();
		groups.clear();
		pubPrivs.clear();
		
		List<InstitutionType> typeList = DbInstitutionType.findAll();
		for (InstitutionType type : typeList)
			types.put(type.getTypeCode(), DbInstitutionType.getInstance(type));
		
		List<InstitutionGroup> groupList = DbInstitutionGroup.findAll();
		for (InstitutionGroup group : groupList)
			groups.put(group.getGroupCode(), DbInstitutionGroup.getInstance(group));
		
		List<InstitutionPubPriv> pubPrivList = DbInstitutionPubPriv.findAll();
		for (InstitutionPubPriv pubPriv : pubPrivList)
			pubPrivs.put(pubPriv.getPubPrivCode(), DbInstitutionPubPriv.getInstance(pubPriv));
	}
	
	protected int loadPass(int pass) throws SQLException {
		System.out.println(new Date());
		System.out.println(getCacheName() + " : " + "Pass: "+ pass + " Loading institutions (statuses " + config.loadStatusList + ")...");
		
		// 	We must use SQL, because normal Hibernate access wants to load the full dataset into memory, which uses just too much space.
		Connection conn   = HibernateUtil.getConnection();
		Statement sqlStmt = conn.createStatement();
		ResultSet results = sqlStmt.executeQuery(getSqlStatement());
		
		int count = 0;
		System.out.println(new Date());
		System.out.println(getCacheName() + " : " + "Parsing institutions...");
		while (results.next()) {
			
			if (config.loadLimit > -1 && count >= config.loadLimit) {
				break;
			}

			count++;
			parse(getInstance(results), pass);
			
			if (config.loadWatchPoint > 0 && count % config.loadWatchPoint == 0) {
				System.out.println(getCacheName() + " : " + pass + " :: " + count + " | " +
						new Date() + "   |   " + 
						searchMap.size() + " tags  |   "  + 
						countMap.size() + " counts  |   " + 
						(Math.round(Runtime.getRuntime().freeMemory() / 1000000d) / 1000d) + "Gb Free  |  " + 
						(Math.round(Runtime.getRuntime().totalMemory() / 1000000d) / 1000d) + "Gb Total  |   " + 
						(Math.round(Runtime.getRuntime().maxMemory() / 1000000d) / 1000d) + "Gb Max   ");
			}
			if (config.loadGcPoint > 0 && count % config.loadGcPoint == 0)
				Runtime.getRuntime().gc();
			
			Thread.yield();
		}
		System.out.println(getCacheName() + " : " + pass + " :: " + count + " | " +
				new Date() + "   |   " + 
				searchMap.size() + " tags  |   "  + 
				countMap.size() + " counts  |   " + 
				(Math.round(Runtime.getRuntime().freeMemory() / 1000000d) / 1000d) + "Gb Free  |  " + 
				(Math.round(Runtime.getRuntime().totalMemory() / 1000000d) / 1000d) + "Gb Total  |   " + 
				(Math.round(Runtime.getRuntime().maxMemory() / 1000000d) / 1000d) + "Gb Max   ");
		System.out.println(getCacheName() + " : " + "Pass: "+ pass + " complete.");
		System.out.println();
		
		results.close();
		sqlStmt.close();
		HibernateUtil.freeConnection(conn);	//	conn.close();
		
		return count;
	}
	
	protected int cleanUp() {
//		Clean up the map words (remove anything that had too many entries to be used in a search)
		int wordCount = 0;
		HashSet<String> nullWords = new HashSet<String>();
		for (String string : wordMap.keySet()) {
			if (wordMap.get(string) == null) {
				nullWords.add(string);
			} else {
				wordCount += wordMap.get(string).size();
//				System.out.println(string + " : " + wordMap.get(string));
			}
		}
		System.out.println(getCacheName() + " : " + "Null words count is " + nullWords.size());
		for (String string : nullWords)
			wordMap.remove(string);

		System.out.println(getCacheName() + " : " + "Loaded.");
		Runtime.getRuntime().gc();
		System.out.println(getCacheName() + " : " + "Cleaned.");
		
		return wordCount;
	}
	
	/**
	 * Output the word map statistics for debugging or performance analysis
	 */
	public void dumpWordStats() {
		
		HashMap<Integer, Integer> wordCountMap = new HashMap<Integer, Integer>();
		HashMap<Integer, Integer> prefixCountMap = new HashMap<Integer, Integer>();
		for (String string : wordMap.keySet()) {
			if (wordCountMap.containsKey(string.length())) {
				wordCountMap.put(string.length(), wordCountMap.get(string.length()) + wordMap.get(string).size());
				prefixCountMap.put(string.length(), prefixCountMap.get(string.length()) + 1);
			} else {
				wordCountMap.put(string.length(), wordMap.get(string).size());
				prefixCountMap.put(string.length(), 1);
			}
		}
		for (Integer length : wordCountMap.keySet()) {
			System.out.println(getCacheName() + " : " + "length " + length + "  : " + prefixCountMap.get(length) + " prefixes, " + wordCountMap.get(length) + " words");
		}
		
	}
	
	protected InstitutionInstance getInstance(ResultSet results) throws SQLException {
		InstitutionInstance instance = new InstitutionInstance();
		
		instance.setUcn(results.getInt("ucn"));
		instance.setParentUcn(results.getInt("parent_ucn"));
		instance.setAlternateIds(results.getString("alternate_ids"));
		instance.setInstitutionName(results.getString("institution_name"));
		instance.setAddress1(results.getString("address1"));
		instance.setAddress2(results.getString("address2"));
		instance.setAddress3(results.getString("address3"));
		instance.setCity(results.getString("city"));
		instance.setState(results.getString("state"));
		instance.setZip(results.getString("zip"));
		instance.setCountry(results.getString("country"));
		instance.setPhone(results.getString("phone"));
		instance.setFax(results.getString("fax"));
		
		return instance;
	}
	
	public boolean addInstitution(InstitutionInstance institution) throws Exception {
		if (!config.updateable)
			throw new Exception("Attempted to update a non-updateable " + getCacheName() + ".");

		if (ucnSet.contains(institution.getUcn()))
			return false;
		
		parse(institution, 1);
		parse(institution, 2);
		
		return true;
	}
	
	/**
	 * Parse one institution and add it to the supplied, temporary (list based) map of strings.
	 * 
	 * @param institution
	 * @param tempMap
	 */
	protected void parse(InstitutionInstance institution, int pass) {
		if (pass == 1)	
			ucns++;
		
		if (config.updateable && pass == 1)
			ucnSet.add(institution.getUcn());
		
		HashSet<String> strings = new HashSet<String>();
		
		//  Parse all components of the institution address
		parseAdd(institution, strings);
		
		//	Add more substrings for inner strings, if the option is activated
		if (config.useInnerStrings) {
			HashSet<String> newStrings = new HashSet<String>();
			for (String string : strings) {
				newStrings.add(string);
				for (int i = 1; i < string.length(); i++) {
					for (int j = i + config.minInnerStringLength; j <= string.length(); j++) {
						if (!newStrings.contains(string.substring(i, j)))
							newStrings.add(string.substring(i,j));
					}
				}
			}
			strings = newStrings;
		}
		
		//	Add pairs of substrings, if the option is activated
		if (config.useStringPairs && pass == 2) {
			HashSet<String> stringPairs = new HashSet<String>();
			//	Make a list of strings that are okay for pairs
			List<String> pairCandidates = new ArrayList<String>();
			for (String string : strings) {
				//	Optimization : skip short strings  THIS WOULD WORK FINE IF WE HAD THE MEMORY, BUT IT EATS UP TONS!!!
				if (string.length() < config.minStringLength)
					continue;
				//	Optimization : skip strings that are too long and already in the list
				if (string.length() > config.maxStringPairLength)
					if (strings.contains(string.substring(0, config.maxStringPairLength)))
						continue;
				//	Optimization : skip strings that don't need pairs (i.e. they are already in the "singles" map)
				if (!searchMap.containsKey(string))
					pairCandidates.add(string);
			}
			//	Make every pair
			for (int i = 0; i < pairCandidates.size() - 1; i++) {
				String first = pairCandidates.get(i);
				for (int j = i + 1; j < pairCandidates.size(); j++) {
					String second = pairCandidates.get(j);
					//	Optimization -- don't pair a string with variations of itself
					if (first.length() <= second.length() && second.startsWith(first))
						continue;
					if (second.length() <= first.length() && first.startsWith(second))
						continue;
					stringPairs.add(getFilterPair(first, second));
				}
			}
			//	strings.addAll(stringPairs);	// One pass method, add the string pairs to the single strings
			strings = stringPairs;			// Two pass method, replace the single strings with the string pairs
			
			
//			for (String first : strings) {
//				if (searchMap.containsKey(first)) continue;	//	Optimization: if the string is good enough to do a search alone, then don't need the pair
//				for (String second : strings) {
//					if (searchMap.containsKey(second)) continue;	// Same here
//					if (!first.equals(second)) {
//						stringPairs.add(getFilterPair(first, second));
//					}
//				}
//			}
//		//	strings.addAll(stringPairs);	// One pass method, add the string pairs to the single strings
//			strings = stringPairs;			// Two pass method, replace the single strings with the string pairs
		}
		
		//  Add them all to the search maps
		Integer key = new Integer(institution.getUcn());
		for (String string : strings) {
			if (string.length() >= config.minStringLength) {
				if (countMap.containsKey(string)) {
					countMap.put(string, countMap.get(string) + 1);
				} else if (searchMap.containsKey(string)) {
					if (searchMap.get(string).size() + 1 > config.maxListLength) {
						countMap.put(string, searchMap.get(string).size() + 1);
						searchMap.remove(string);
					//	Runtime.getRuntime().gc();
					} else {
						searchMap.get(string).add(key);
					}
				} else {
					List<Integer> list = new ArrayList<Integer>();
					list.add(key);
					searchMap.put(string, list);
				}
			} else {
				if (countMap.containsKey(string)) {
					countMap.put(string, countMap.get(string) + 1);
				} else {
					countMap.put(string, 1);
				}
			}
		}
		
//		System.out.println(institution.getInstitutionName());
//		System.out.println(institution.getAddress1());
//		System.out.println(institution.getAddress2());
//		System.out.println(institution.getAddress3());
//		System.out.println(institution.getCity());
//		System.out.println(institution.getState());
//		System.out.println(institution.getZip());
//		System.out.println(institution.getCountry());
//		System.out.println(institution.getPhone());
//		System.out.println(institution.getFax());
//		System.out.println();
//		System.out.println(strings);
//		System.out.println("---------------------------------------------------------------------------------------------------");
	}
	
	protected void parseAdd(InstitutionInstance institution, HashSet<String> strings) {
		parseAdd(institution.getUcn() + "", strings);
		parseAdd(institution.getParentUcn() + "", strings);
		parseAdd(institution.getAlternateIds(), strings);
		parseAdd(institution.getInstitutionName(), strings);
		parseAdd(institution.getAddress1(), strings);
		parseAdd(institution.getAddress2(), strings);
		parseAdd(institution.getAddress3(), strings);
		parseAdd(institution.getCity(), strings);
		parseAdd(institution.getState(), strings);
		parseAdd(institution.getZip(), strings);
		parseAdd(institution.getCountry(), strings);
		parseAddContinuous(institution.getPhone(), strings);
		parseAddContinuous(institution.getFax(), strings);	
	}
	
	/**
	 * Parse a string into word strings, broken (and ignoring) by any non-alphanumeric characters into a hash set of such strings.
	 * 
	 * Using a hash set guarantees that any string will exist at most one time in the complete list, even if duplicated in the address.
	 * @param string
	 * @param strings
	 */
	protected void parseAdd(String string, HashSet<String> strings) {
		string = string.toUpperCase();
		StringBuffer word = new StringBuffer();
		for (int i = 0; i < string.length(); i++) {
			if ( (string.charAt(i) >= 'a' && string.charAt(i) <= 'z')
			|| 	 (string.charAt(i) >= 'A' && string.charAt(i) <= 'Z')
			||	 (string.charAt(i) >= '0' && string.charAt(i) <= '9')) {
				word.append(string.charAt(i));
				if (!strings.contains(word.toString())) {
					strings.add(word.toString());
				}
			} else if (word.length() > 0) {
				mapWord(word);
				word = new StringBuffer();
			}
		}
		if (word.length() > 0)
			mapWord(word);
	}
	
	/**
	 * Add a word to the word map
	 * @param word
	 */
	protected void mapWord(StringBuffer word) {
		if (word == null || word.length() == 0)
			return;
		String mapWord;
//		if (word.length() == 1)
			mapWord = word.toString().toUpperCase();
//		else 
//			mapWord = word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase();
		for (int i = 1; i <= word.length(); i++) {
			String string = word.substring(0, i).toUpperCase();
			if (wordMap.containsKey(string)) {
				SortedSet<String> words = wordMap.get(string);
				if (words != null) {
					if (words.size() < config.maxWordListLength)
						words.add(mapWord);
					else
						wordMap.put(string, null);
				}
			} else {
				SortedSet<String> words = new TreeSet<String>();
				words.add(mapWord);
				wordMap.put(string, words);
			}
		}
	}
	
	/**
	 * Parse a string into character sequences without word breaks into a hashset of such strings.
	 * 
	 * Using a hash set guarantees that any string will exist at most one time in the complete list, even if duplicated in the address.
	 * @param string
	 * @param strings
	 */
	protected void parseAddContinuous(String string, HashSet<String> strings) {
		if (string == null || string.length() == 0)
			return;
		string = string.toUpperCase();
		StringBuffer word = new StringBuffer();
		for (int i = 0; i < string.length(); i++) {
			if ( (string.charAt(i) >= 'a' && string.charAt(i) <= 'z')
			|| 	 (string.charAt(i) >= 'A' && string.charAt(i) <= 'Z')
			||	 (string.charAt(i) >= '0' && string.charAt(i) <= '9')) {
				word.append(string.charAt(i));
				if (!strings.contains(word.toString()))
					strings.add(word.toString());
			}
		}
		
	}
	
	public String [] parseFilter(String filter) {
		return AppConstants.parseFilterTerms(filter);
	}
	
	public String getFilterPair(String first, String second) {
		if (first.length() > config.maxStringPairLength)
			first = first.substring(0, config.maxStringPairLength);
		if (second.length() > config.maxStringPairLength)
			second = second.substring(0, config.maxStringPairLength);
		
		if (first.compareToIgnoreCase(second) <= 0)
			return (first + " " + second);
		else
			return (second + " " + first);
	}
	
	public List<Integer> getFilteredUcns(String filter) throws InstitutionCacheNotReady {
		String filters [] = parseFilter(filter);
		return getFilteredUcns(filters);
	}
	
	public List<Integer> getFilteredUcns(String [] filters) throws InstitutionCacheNotReady {
		
		List<Integer> ucns = null;

		if (config.useStringPairs && filters.length > 1) {
			for (int i = 0; i < filters.length - 1; i++)
				for (int j = i + 1; j < filters.length; j++)
					if (!filters.equals(filters[j])) {
						String filterPair = getFilterPair(filters [i], filters [j]);
						if (filterPair != null) {
							List<Integer> unionUcns = getInstitutionUcns(filterPair);
							if (unionUcns != null) {
								if (ucns == null) {
									ucns = new ArrayList<Integer>();
									ucns.addAll(unionUcns);
								} else {
									ucns.retainAll(unionUcns);
								}
							}
						}
					}
		}
		
		for (int i = 0; i < filters.length; i++) {
			List<Integer> unionUcns = getInstitutionUcns(filters [i]);
			if (unionUcns != null) {
				if (ucns == null) {
					ucns = new ArrayList<Integer>();
					ucns.addAll(unionUcns);
				} else {
					ucns.retainAll(unionUcns);
				}
			}
		}

		return ucns;
	}
	
	public int getFilteredUcnCount(String filter) throws InstitutionCacheNotReady {
		String filters [] = parseFilter(filter);
		return getFilteredUcnCount(filters);
	}

	
	public int getFilteredUcnCount(String [] filters) throws InstitutionCacheNotReady {
		int bestCount = -1;
		
		if (config.useStringPairs && filters.length > 1) {
			for (int i = 0; i < filters.length - 1; i++) {
				for (int j = i; j < filters.length; j++) {
					String filterPair = getFilterPair(filters [i], filters [j]);
					if (filterPair != null) {
						int thisCount = getInstitutionCount(filterPair);
						if(thisCount > 0 && (bestCount < 0 || thisCount < bestCount))
							bestCount = thisCount;
					}
				}
			}
		}
		
		for (int i = 0; i < filters.length; i++) {
			int thisCount = getInstitutionCount(filters [i]);
			if(thisCount > 0 && (bestCount < 0 || thisCount < bestCount))
				bestCount = thisCount;
		}
		
		if (bestCount < 0)
			bestCount = 0;
		
		return bestCount;
	}
	
	public void refresh() {
		init();
	}

	public boolean isMapsReady() {
		return mapsReady;
	}

	public HashMap<String, SortedSet<String>> getWordMap() throws InstitutionCacheNotReady {
		if (!mapsReady)
			throw new InstitutionCacheNotReady();
		return wordMap;
	}

	public InstitutionCacheConfig getConfig() {
		return config;
	}

	public static InstitutionCache getSingleton() throws InstitutionCacheConflict {
		return getSingleton(null);
	}

	public HashMap<String, List<Integer>> getSearchMap() throws InstitutionCacheNotReady {
		if (!mapsReady)
			throw new InstitutionCacheNotReady();
		return searchMap;
	}

	public HashMap<String, Integer> getCountMap() throws InstitutionCacheNotReady {
		if (!mapsReady)
			throw new InstitutionCacheNotReady();
		return countMap;
	}
	
	public List<Integer> getInstitutionUcns(String string) throws InstitutionCacheNotReady {
		if (!mapsReady)
			throw new InstitutionCacheNotReady();
		return searchMap.get(string);
	}
	
	public int getInstitutionCount(String string) throws InstitutionCacheNotReady {
		if (!mapsReady)
			throw new InstitutionCacheNotReady();
		if (countMap.containsKey(string))
			return countMap.get(string);
		else
			return -1;
	}
	
	public String [] getWords(String prefix) throws InstitutionCacheNotReady {
		if (!mapsReady)
			throw new InstitutionCacheNotReady();
		if (prefix == null)
			return new String [] {};
		prefix = prefix.toUpperCase();
		if (wordMap.containsKey(prefix) && wordMap.get(prefix) != null)
			return wordMap.get(prefix).toArray(new String [] {});
		else
			return new String [] {};
	}

	public HashMap<String, InstitutionGroupInstance> getGroups() {
		return groups;
	}

	public void setGroups(HashMap<String, InstitutionGroupInstance> groups) {
		this.groups = groups;
	}

	public HashMap<String, InstitutionTypeInstance> getTypes() {
		return types;
	}

	public void setTypes(HashMap<String, InstitutionTypeInstance> types) {
		this.types = types;
	}
	
	public HashMap<String, InstitutionPubPrivInstance> getPubPrivs() {
		return pubPrivs;
	}

	public void setPubPrivs(HashMap<String, InstitutionPubPrivInstance> pubPrivs) {
		this.pubPrivs = pubPrivs;
	}
	
	public void setDescriptions(InstitutionInstance institution) {
		if (institution == null) {
			return;
		}
		institution.setTypeDescription(getInstitutionType(institution.getTypeCode()).getDescription());
		institution.setGroupDescription(getInstitutionGroup(institution.getGroupCode()).getDescription());
		institution.setPublicPrivateDescription(getInstitutionPubPriv(institution.getPublicPrivateCode()).getDescription());
	}

	public InstitutionTypeInstance getInstitutionType(String typeCode) {
		if (types.containsKey(typeCode))
			return types.get(typeCode);
		
		InstitutionTypeInstance unknown = new InstitutionTypeInstance();
		unknown.setTypeCode(typeCode);
		unknown.setDescription("Unknown Type " + typeCode);
		
		//	Just so we don't have to keep making this every time
		types.put(typeCode, unknown);
		
		return unknown;
	}
	
	public InstitutionGroupInstance getInstitutionGroup(String groupCode) {
		if (groups.containsKey(groupCode))
			return groups.get(groupCode);
		
		InstitutionGroupInstance unknown = new InstitutionGroupInstance();
		unknown.setGroupCode(groupCode);
		unknown.setDescription("Unknown Group" + groupCode);
		
		//	Just so we don't have to keep making this every time
		groups.put(groupCode, unknown);
		
		return unknown;
	}
	
	public InstitutionPubPrivInstance getInstitutionPubPriv(String pubPrivCode) {
		if (pubPrivs.containsKey(pubPrivCode))
			return pubPrivs.get(pubPrivCode);
		
		InstitutionPubPrivInstance unknown = new InstitutionPubPrivInstance();
		unknown.setPublicPrivateCode(pubPrivCode);
		unknown.setDescription("Unknown Public Private code " + pubPrivCode);
		
		//	Just so we don't have to keep making this every time
		pubPrivs.put(pubPrivCode, unknown);
		
		return unknown;
	}
	
	@Override
	public String getTableName() {
		StringBuffer sb = new StringBuffer();
		sb.append("institution where ucn > 0 ");
		appendStatusClause(sb);
		return sb.toString();
	}
	
	@Override
	public String [] getCountHeadings() {
		return new String [] {"UCNs", "Searchable Terms", "Countable Terms", "Words"};
	}

	@Override
	public int [] getProcessed() {
		return new int [] {ucns, searchMap.size(), countMap.size(), wordMap.size()};
	}
	
	@Override
	public int [] getExpectedCounts() throws Exception {
		return new int [] {getExpectedCount()};
	}

	@Override
	public boolean getReady() {
		return mapsReady;
	}

	@Override
	public boolean getLoading() {
		return initRunning;
	}

	@Override
	public String getName() {
		return "Institution Cache";
	}

	@Override
	public int getSeq() {
		return 0;
	}

	@Override
	public String getKey() {
		return CacheStatusInstance.INSTITUTION_CACHE_KEY;
	}
}
