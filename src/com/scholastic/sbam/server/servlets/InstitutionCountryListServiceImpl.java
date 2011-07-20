package com.scholastic.sbam.server.servlets;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.data.LoadConfig;
import com.scholastic.sbam.client.services.InstitutionCountryListService;
import com.scholastic.sbam.server.database.codegen.InstitutionCountry;
import com.scholastic.sbam.server.database.objects.DbInstitutionCountry;
import com.scholastic.sbam.server.database.util.HibernateUtil;
import com.scholastic.sbam.shared.objects.InstitutionCountryInstance;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class InstitutionCountryListServiceImpl extends AuthenticatedServiceServlet implements InstitutionCountryListService {

	@Override
	public List<InstitutionCountryInstance> getInstitutionCountries(LoadConfig loadConfig) throws IllegalArgumentException {
		
		authenticate("list link types");	//	SecurityManager.ROLE_CONFIG);
		
		HibernateUtil.openSession();
		HibernateUtil.startTransaction();

		List<InstitutionCountryInstance> list = new ArrayList<InstitutionCountryInstance>();
		try {
			
			//	Find only undeleted term types
			List<InstitutionCountry> dbInstances = DbInstitutionCountry.findAll();

			for (InstitutionCountry dbInstance : dbInstances) {
				list.add(DbInstitutionCountry.getInstance(dbInstance));
			}

		} catch (Exception exc) {
			exc.printStackTrace();
		}
		
		HibernateUtil.endTransaction();
		HibernateUtil.closeSession();
		
		return list;
	}
}
