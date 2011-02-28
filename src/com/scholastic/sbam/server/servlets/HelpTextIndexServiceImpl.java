package com.scholastic.sbam.server.servlets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.scholastic.sbam.client.services.HelpTextIndexService;
import com.scholastic.sbam.server.database.codegen.HelpText;
import com.scholastic.sbam.server.database.objects.DbHelpText;
import com.scholastic.sbam.server.database.util.HibernateUtil;
import com.scholastic.sbam.shared.objects.HelpTextIndexInstance;

/**
 * The server side implementation of the RPC service to generate and return an index tree for the Help Text.
 */
@SuppressWarnings("serial")
public class HelpTextIndexServiceImpl extends AuthenticatedServiceServlet implements HelpTextIndexService {

	@Override
	public List<HelpTextIndexInstance> getHelpTextIndex() throws IllegalArgumentException {
		
		authenticate("get help text index");	//, SecurityManager.ROLE_CONFIG);
		
		HibernateUtil.openSession();
		HibernateUtil.startTransaction();

		//	This could be cached once it is built, but this shouldn't be done very often, and anyway... it would then require a restart or other coding to rebuild the cache
		List<HelpTextIndexInstance> result = new ArrayList<HelpTextIndexInstance>();
		try {
			
			List<HelpText> dbInstances = DbHelpText.findAll();
			if (dbInstances != null) {
				String rootStart = null;
				HashMap<String, HelpText> map = new HashMap<String, HelpText>();
				//	First, load the relevant info into a hash map for access, and save the starting point (i.e. the entry with no parent or previous sibling)
				for (HelpText dbInstance : dbInstances) {
				//	dbInstance.setText("");	// We don't need this, so lose it (i.e. use something that needs less space)
					map.put(dbInstance.getId(), dbInstance);
					if (dbInstance.getParentId() == null || dbInstance.getParentId().length() == 0)
						if (dbInstance.getPrevSiblingId() == null || dbInstance.getPrevSiblingId().length() == 0)
							rootStart = dbInstance.getId();
				}
				//	If no root, we have a problem
				if (rootStart == null) {
					throw new IllegalArgumentException("Help text has no valid root element.");
				} else {
					buildFromMap(rootStart, map, result);
				}
			} else {
				throw new IllegalArgumentException("No help text found.");
			}
		} catch (IllegalArgumentException exc) {
			throw exc;
		} catch (Exception exc) {
			exc.printStackTrace();
		}
		
		HibernateUtil.endTransaction();
		HibernateUtil.closeSession();
		
		return result;
	}
	
	/**
	 * Build help text index instances, adding siblings to the root list
	 * @param id
	 * @param map
	 * @param list
	 * @throws IllegalArgumentException
	 */
	public void buildFromMap(String id, HashMap<String, HelpText> map, List<HelpTextIndexInstance> list) throws IllegalArgumentException {
		
		//	Process all siblings, and add them in order to the list
		while (id != null && id.length() > 0) {
			if (!map.containsKey(id))
				throw new IllegalArgumentException("Referenced Help Text ID '" + id + "' not found.");
			
			HelpText dbInstance = map.get(id);
			HelpTextIndexInstance instance = getIndexInstance(dbInstance);
			buildFromMap(dbInstance.getFirstChildId(), map, instance);
			
			list.add(instance);
			id = dbInstance.getNextSiblingId();
		}
		
	}
	
	/**
	 * Build help text index instances, adding siblings to the declared parent
	 * @param id
	 * @param map
	 * @param parent
	 * @throws IllegalArgumentException
	 */
	public void buildFromMap(String id, HashMap<String, HelpText> map, HelpTextIndexInstance parent) throws IllegalArgumentException {
		
		//	Process all siblings, and add them in order to the list
		while (id != null && id.length() > 0) {
			if (!map.containsKey(id))
				throw new IllegalArgumentException("Referenced Help Text ID '" + id + "' not found.");
			
			HelpText dbInstance = map.get(id);
			HelpTextIndexInstance instance = getIndexInstance(dbInstance);
			buildFromMap(dbInstance.getFirstChildId(), map, instance);
			
			parent.add(instance);
			id = dbInstance.getNextSiblingId();
		}
		
	}
	
	public HelpTextIndexInstance getIndexInstance(HelpText dbInstance) {
		HelpTextIndexInstance instance = new HelpTextIndexInstance();
		instance.setId(dbInstance.getId());
		instance.setTitle(dbInstance.getTitle());
		instance.setIconName(dbInstance.getIconName());
		
		return instance;
	}
}
