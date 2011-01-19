package com.scholastic.sbam.server.servlets;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.data.LoadConfig;
import com.scholastic.sbam.client.services.PreferenceCodeListService;
import com.scholastic.sbam.server.database.codegen.PreferenceCode;
import com.scholastic.sbam.server.database.objects.DbPreferenceCode;
import com.scholastic.sbam.server.database.util.HibernateUtil;
import com.scholastic.sbam.shared.objects.PreferenceCodeInstance;
import com.scholastic.sbam.shared.security.SecurityManager;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class PreferenceCodeListServiceImpl extends AuthenticatedServiceServlet implements PreferenceCodeListService {

	@Override
	public List<PreferenceCodeInstance> getPreferenceCodes(String prefCatCode, LoadConfig loadConfig) throws IllegalArgumentException {
		
		authenticate("list preference codes", SecurityManager.ROLE_CONFIG);
		
		HibernateUtil.openSession();
		HibernateUtil.startTransaction();

		List<PreferenceCodeInstance> list = new ArrayList<PreferenceCodeInstance>();
		try {
			
			//	Find only undeleted cancel reasons
			List<PreferenceCode> preferenceCodes = DbPreferenceCode.findByCategory(prefCatCode, 'X');

			for (PreferenceCode preferenceCode : preferenceCodes) {
				PreferenceCodeInstance instance = new PreferenceCodeInstance();
				instance.setPrefCatCode(preferenceCode.getId().getPrefCatCode());
				instance.setPrefSelCode(preferenceCode.getId().getPrefSelCode());
				instance.setDescription(preferenceCode.getDescription());
				instance.setExportValue(preferenceCode.getExportValue());
				instance.setSeq(preferenceCode.getSeq());
				instance.setStatus(preferenceCode.getStatus());
				instance.setCreatedDatetime(preferenceCode.getCreatedDatetime());
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
