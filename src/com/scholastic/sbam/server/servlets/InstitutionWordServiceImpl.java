package com.scholastic.sbam.server.servlets;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.scholastic.sbam.client.services.InstitutionWordService;
import com.scholastic.sbam.server.fastSearch.InstitutionCache;
import com.scholastic.sbam.server.fastSearch.InstitutionCache.InstitutionCacheConflict;
import com.scholastic.sbam.shared.exceptions.ServiceNotReadyException;
import com.scholastic.sbam.shared.objects.FilterWordInstance;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class InstitutionWordServiceImpl extends AuthenticatedServiceServlet implements InstitutionWordService {

	@Override
	public PagingLoadResult<FilterWordInstance> getInstitutionWords(PagingLoadConfig loadConfig) throws IllegalArgumentException, ServiceNotReadyException {		
		authenticate(getSubjectName() + " word list");	//	SecurityManager.ROLE_CONFIG);
		return doWordSearch(loadConfig);
	}
	
	public PagingLoadResult<FilterWordInstance> doWordSearch(PagingLoadConfig loadConfig) throws IllegalArgumentException, ServiceNotReadyException {
		BasePagingLoadResult<FilterWordInstance> result = null;
		try {
			
			InstitutionCache searchCache = getSearchCache();
			if (searchCache == null || !searchCache.isMapsReady())
				throw new ServiceNotReadyException(getSubjectName() + " word filter function");

			List<FilterWordInstance> list = new ArrayList<FilterWordInstance>();
			
			//	Filtering... only get the words for the last term listed, using the cache
			String query = loadConfig.get("query").toString();
			String search = getLastWord(query.toUpperCase());
			int wordCount = 0;
			// The point of this test is to make sure the user has started typing a new word, i.e. isn't at something like "Space " or "Dash-"
			if (search.length() > 0) {
				String prefix = query.substring(0, query.length() - search.length());
				String [] words = searchCache.getWords(search);
				wordCount = words.length;
				for (int i = loadConfig.getOffset(); i < words.length; i++) {
					FilterWordInstance word = new FilterWordInstance();
					word.setWord(prefix + toTitleCase(words [i]));
					list.add(word);
					if (list.size() >= loadConfig.getLimit())
						break;
				}		
			}
			
			result = new BasePagingLoadResult<FilterWordInstance>(list, loadConfig.getOffset(), wordCount);
			

		} catch (InstitutionCache.InstitutionCacheNotReady exc) {
			throw new ServiceNotReadyException(getSubjectName() + " word filter function");
		} catch (ServiceNotReadyException exc) {
			throw exc;
		} catch (Exception exc) {
			exc.printStackTrace();
		}
		
		if (result == null)
			result = new BasePagingLoadResult<FilterWordInstance>(new ArrayList<FilterWordInstance>(), loadConfig.getOffset(), 0);
		
		return result;
	}
	
	public String getSubjectName() {
		return "institution";
	}
	
	public InstitutionCache getSearchCache() throws InstitutionCacheConflict {
		return InstitutionCache.getSingleton();
	}
	
	/**
	 * Extract the last word from the search phrase.
	 * @param phrase
	 * @return
	 */
	protected String getLastWord(String phrase) {
		for (int i = phrase.length() - 1; i >= 0; i--) {
			if ( (phrase.charAt(i) >= '0' && phrase.charAt(i) <= '9')
			||	 (phrase.charAt(i) >= 'A' && phrase.charAt(i) <= 'Z')
			||	 (phrase.charAt(i) >= 'a' && phrase.charAt(i) <= 'z') )
				continue;
			else if (i < phrase.length() - 1) {
				return phrase.substring(i + 1);
			} else
				return "";
		}
		// Never found a word break
		return phrase;
	}
	
	/**
	 * Convert a word to title case
	 * @param word
	 * @return
	 */
	protected String toTitleCase(String word) {
		if (word.length() <= 1)
			return word.toUpperCase();
		return word.substring(0,1).toUpperCase() + word.substring(1).toLowerCase();
	}
}
