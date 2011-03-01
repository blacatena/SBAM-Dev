package com.scholastic.sbam.server.servlets;

import java.util.ArrayList;
import java.util.List;

import com.scholastic.sbam.client.services.HelpTextIndexService;
import com.scholastic.sbam.server.fastSearch.HelpTextCache;
import com.scholastic.sbam.shared.exceptions.ServiceNotReadyException;
import com.scholastic.sbam.shared.objects.HelpTextIndexInstance;

/**
 * The server side implementation of the RPC service to generate and return an index tree for the Help Text.
 */
@SuppressWarnings("serial")
public class HelpTextIndexServiceImpl extends AuthenticatedServiceServlet implements HelpTextIndexService {

	@Override
	public List<HelpTextIndexInstance> getHelpTextIndex() throws IllegalArgumentException, ServiceNotReadyException {
		
		authenticate("get help text index");	//, SecurityManager.ROLE_CONFIG);

		//	This could be cached once it is built, but this shouldn't be done very often, and anyway... it would then require a restart or other coding to rebuild the cache
		List<HelpTextIndexInstance> result = new ArrayList<HelpTextIndexInstance>();
		try {
			if (!HelpTextCache.getSingleton().isMapsReady())
				throw new ServiceNotReadyException();
			result = HelpTextCache.getSingleton().getIndex();
			if (result == null) {
				throw new ServiceNotReadyException(HelpTextCache.getSingleton().getIndexError());
			}
		} catch (ServiceNotReadyException exc) {
			throw exc;
		} catch (IllegalArgumentException exc) {
			throw exc;
		} catch (Exception exc) {
			exc.printStackTrace();
		}
		
		return result;
	}
}
