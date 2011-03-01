package com.scholastic.sbam.server.servlets;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.scholastic.sbam.client.services.HelpTextWordService;
import com.scholastic.sbam.server.fastSearch.HelpTextCache;
import com.scholastic.sbam.shared.exceptions.ServiceNotReadyException;
import com.scholastic.sbam.shared.objects.FilterWordInstance;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class HelpTextWordServiceImpl extends AuthenticatedServiceServlet implements HelpTextWordService {

	@Override
	public PagingLoadResult<FilterWordInstance> getHelpTextWords(PagingLoadConfig loadConfig) throws IllegalArgumentException, ServiceNotReadyException {
		
		authenticate("get help text words");	//	SecurityManager.ROLE_CONFIG);

		BasePagingLoadResult<FilterWordInstance> result = null;
		try {
			
			if (HelpTextCache.getSingleton() == null || !HelpTextCache.getSingleton().isMapsReady())
				throw new ServiceNotReadyException("help text word filter function");

			List<FilterWordInstance> list = new ArrayList<FilterWordInstance>();
			
			//	Filtering... only get the words for the first term listed, using the cache
			String query = loadConfig.get("query").toString();
			String filters [] = HelpTextCache.getSingleton().parseFilter(query);
			int wordCount = 0;
			if (filters.length > 0  && query.endsWith(filters [filters.length - 1])) {
				String search = filters [filters.length - 1];
				String prefix = query.substring(0, query.length() - search.length());
				String [] words = HelpTextCache.getSingleton().getWords(search);
				wordCount = words.length;
				for (int i = loadConfig.getOffset(); i < words.length; i++) {
					FilterWordInstance word = new FilterWordInstance();
					word.setWord(prefix + words [i]);
					list.add(word);
					if (list.size() >= loadConfig.getLimit())
						break;
				}		
			}
			
			result = new BasePagingLoadResult<FilterWordInstance>(list, loadConfig.getOffset(), wordCount);
			

		} catch (HelpTextCache.HelpTextCacheNotReady exc) {
			throw new ServiceNotReadyException("help text word filter function");
		} catch (ServiceNotReadyException exc) {
			throw exc;
		} catch (Exception exc) {
			exc.printStackTrace();
		}
		
		return result;
	}
}
