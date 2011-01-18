package com.scholastic.sbam.server.servlets;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.data.LoadConfig;
import com.scholastic.sbam.client.services.PreferenceCategoryListService;
import com.scholastic.sbam.server.database.codegen.PreferenceCategory;
import com.scholastic.sbam.server.database.objects.DbPreferenceCategory;
import com.scholastic.sbam.server.database.util.HibernateUtil;
import com.scholastic.sbam.shared.objects.PreferenceCategoryInstance;
import com.scholastic.sbam.shared.security.SecurityManager;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class PreferenceCategoryListServiceImpl extends AuthenticatedServiceServlet implements PreferenceCategoryListService {

	@Override
	public List<PreferenceCategoryInstance> getPreferenceCategories(LoadConfig loadConfig) throws IllegalArgumentException {
		
		authenticate("list preference categories", SecurityManager.ROLE_CONFIG);
		
		HibernateUtil.openSession();
		HibernateUtil.startTransaction();

		List<PreferenceCategoryInstance> list = new ArrayList<PreferenceCategoryInstance>();
		try {
			
			//	Find only undeleted cancel reasons
			List<PreferenceCategory> preferenceCategorys = DbPreferenceCategory.findFiltered(null, null, (char) 0, 'X');

			for (PreferenceCategory preferenceCategory : preferenceCategorys) {
				PreferenceCategoryInstance instance = new PreferenceCategoryInstance();
				instance.setPrefCatCode(preferenceCategory.getPrefCatCode());
				instance.setDescription(preferenceCategory.getDescription());
				instance.setSeq(preferenceCategory.getSeq());
				instance.setStatus(preferenceCategory.getStatus());
				instance.setCreatedDatetime(preferenceCategory.getCreatedDatetime());
				list.add(instance);
			}

		} catch (Exception exc) {
			exc.printStackTrace();
		}
		
		HibernateUtil.endTransaction();
		HibernateUtil.closeSession();
		
		return list;
	}
}
