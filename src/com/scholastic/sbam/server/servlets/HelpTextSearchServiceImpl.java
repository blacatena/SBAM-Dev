package com.scholastic.sbam.server.servlets;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.scholastic.sbam.client.services.HelpTextSearchService;
import com.scholastic.sbam.server.database.util.HibernateUtil;
import com.scholastic.sbam.server.fastSearch.HelpTextCache;
import com.scholastic.sbam.shared.exceptions.ServiceNotReadyException;
import com.scholastic.sbam.shared.objects.SearchResultInstance;
import com.scholastic.sbam.shared.util.AppConstants;
import com.scholastic.sbam.shared.util.SearchUtilities;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class HelpTextSearchServiceImpl extends AuthenticatedServiceServlet implements HelpTextSearchService {

	@Override
	public PagingLoadResult<SearchResultInstance> searchHelpText(PagingLoadConfig loadConfig, String filter) throws IllegalArgumentException, ServiceNotReadyException {
		
		authenticate("search help text");	//	SecurityManager.ROLE_CONFIG);

		BasePagingLoadResult<SearchResultInstance> result = null;
		try {
			List<SearchResultInstance> list = new ArrayList<SearchResultInstance>();

			boolean doLoose		= SearchUtilities.DEFAULT_LOOSE_SEARCH;
			boolean doBoolean	= SearchUtilities.DEFAULT_BOOLEAN_SEARCH; 
			if (filter.length() > 0 && filter.charAt(0) == AppConstants.LOOSE_BOOLEAN_SEARCH_FLAG) {
				filter = filter.substring(1);
				doLoose = true;
				doBoolean = true;
			} else if (filter.length() > 0 && filter.charAt(0) == AppConstants.STRICT_BOOLEAN_SEARCH_FLAG) {
				filter = filter.substring(1);
				doLoose   = false;
				doBoolean = true;
			} else if (filter.length() > 0 && filter.charAt(0) == AppConstants.QUERY_EXPANSION_SEARCH_FLAG) {
				filter = filter.substring(1);
				doLoose   = false;
				doBoolean = false;
			}
			
			if (filter == null || filter.trim().length() == 0) {
				throw new IllegalArgumentException("A filter value is required.");
			} else if (HelpTextCache.getSingleton() == null || !HelpTextCache.getSingleton().isMapsReady()) {
				throw new ServiceNotReadyException("help text search function");
			} else {
				if (doBoolean && doLoose)
					filter = SearchUtilities.getLooseBoolean(filter);
				filter = filter.replaceAll("'", "''");
				String sql;
				if (doBoolean) {
					sql = "SELECT id, title, text, MATCH (title,text) AGAINST ('" + filter + "' IN BOOLEAN MODE) AS score FROM help_text WHERE MATCH (title,text) AGAINST ('" + filter + "' IN BOOLEAN MODE) order by score desc;";
				} else {
					sql = "SELECT id, title, text, MATCH (title,text) AGAINST ('" + filter + "' WITH QUERY EXPANSION) AS score FROM help_text WHERE MATCH (title,text) AGAINST ('" + filter + "' WITH QUERY EXPANSION) order by score desc;";
				}
				
				HibernateUtil.openSession();
	//			HibernateUtil.startTransaction();
				
				int i = 0;
				int totSize = 0;
				Connection conn   = HibernateUtil.getConnection();
				Statement sqlStmt = conn.createStatement();
				ResultSet results = sqlStmt.executeQuery(sql);
				
				while (results.next()) {
					i++;
					if (i >= loadConfig.getOffset() && list.size() < loadConfig.getLimit()) {
						SearchResultInstance instance = new SearchResultInstance();
						instance.setId(results.getString("id"));
						instance.setTitle(results.getString("title"));
						instance.setText(SearchUtilities.markTerms(results.getString("text"), filter, doLoose, doBoolean));
						if (doBoolean)
							instance.setScore(0);
						else
							instance.setScore(results.getDouble("score"));
						list.add(instance);
					}
					totSize++;
				}
				
				results.close();
				sqlStmt.close();
				conn.close();
				
				result = new BasePagingLoadResult<SearchResultInstance>(list, loadConfig.getOffset(), totSize);
				
//				HibernateUtil.endTransaction();
				HibernateUtil.closeSession();
			}
			

//		} catch (HelpTextCache.HelpTextCacheNotReady exc) {
//			throw new ServiceNotReadyException("help text search function");
		} catch (ServiceNotReadyException exc) {
			throw exc;
		} catch (Exception exc) {
			exc.printStackTrace();
			throw new IllegalArgumentException(exc.getMessage());
		}
		
		return result;
	}
}
