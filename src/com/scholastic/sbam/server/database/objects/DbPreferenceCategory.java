package com.scholastic.sbam.server.database.objects;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.scholastic.sbam.server.database.codegen.PreferenceCategory;
import com.scholastic.sbam.server.database.codegen.PreferenceCode;
import com.scholastic.sbam.server.database.util.HibernateAccessor;
import com.scholastic.sbam.shared.objects.PreferenceCategoryInstance;
import com.scholastic.sbam.shared.objects.PreferenceCodeInstance;

/**
 * Sample database table accessor class, extending HibernateAccessor, and implementing custom get/find methods.
 * 
 * @author Bob Lacatena
 *
 */
public class DbPreferenceCategory extends HibernateAccessor {
	
	static String objectName = PreferenceCategory.class.getSimpleName();
	
	public static PreferenceCategory getByCode(String code) {
		return (PreferenceCategory) getByField(objectName, "prefCatCode", code, "description");
	}
	
	public static PreferenceCategoryInstance getInstance(PreferenceCategory dbInstance) {
		PreferenceCategoryInstance instance = new PreferenceCategoryInstance();
		
		instance.setPrefCatCode(dbInstance.getPrefCatCode());
		instance.setDescription(dbInstance.getDescription());
		instance.setSeq(dbInstance.getSeq());
		instance.setStatus(dbInstance.getStatus());
		instance.setCreatedDatetime(dbInstance.getCreatedDatetime());
		
		return instance;
	}
	
	public static List<PreferenceCategory> findAll() {
		List<Object> results = findAll(objectName);
		List<PreferenceCategory> reasons = new ArrayList<PreferenceCategory>();
		for (int i = 0; i < results.size(); i++)
			reasons.add((PreferenceCategory) results.get(i));
		return reasons;
	}
	
	public static List<PreferenceCategory> findFiltered(String code, String description, char status, char neStatus) {
        try
        {
            Criteria crit = sessionFactory.getCurrentSession().createCriteria(getObjectReference(objectName));
            if (code != null && code.length() > 0)
            	crit.add(Restrictions.like("prefCatCode", code));
            if (description != null && description.length() > 0)
            	crit.add(Restrictions.like("description", description));
            if (status != 0)
            	crit.add(Restrictions.like("status", status));
            if (neStatus != 0)
            	crit.add(Restrictions.ne("status", neStatus));
            crit.addOrder(Order.asc("seq"));
            @SuppressWarnings("unchecked")
			List<PreferenceCategory> objects = crit.list();
            return objects;
        }
        catch(Exception e)
        {
        	e.printStackTrace();
            System.out.println(e.getMessage());
        }
        return new ArrayList<PreferenceCategory>();
	}
	
	public static List<PreferenceCategoryInstance> findAllCatsAndCodes() {

		String sqlQuery = "SELECT {preference_category.*}, {preference_code.*} FROM preference_category LEFT JOIN preference_code ON preference_category.pref_cat_code = preference_code.pref_cat_code AND preference_code.status <> 'X' WHERE preference_category.status <> 'X' order by preference_category.seq, preference_category.pref_cat_code, preference_code.seq, preference_code.pref_sel_code";

        SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(sqlQuery);            
        query.addEntity("preference_category", PreferenceCategory.class);
        query.addEntity("preference_code", PreferenceCode.class);
            
        List<PreferenceCategoryInstance> result = new ArrayList<PreferenceCategoryInstance>();
        
        PreferenceCategoryInstance prevPrefCat = null;
        
        @SuppressWarnings("unchecked")
		List<Object []> objects = query.list();
		for (Object [] pair : objects) {
			PreferenceCategory	prefCat  = (PreferenceCategory) pair [0];
			PreferenceCode		prefCode = (PreferenceCode)		pair [1];
			
			if (prevPrefCat == null || !prevPrefCat.getPrefCatCode().equals(prefCat.getPrefCatCode())) {
				prevPrefCat = getInstance(prefCat);
				result.add(prevPrefCat);
				prevPrefCat.setPreferenceCodes(new ArrayList<PreferenceCodeInstance>());
			}
			
			if (prefCode != null)
				prevPrefCat.getPreferenceCodes().add(DbPreferenceCode.getInstance(prefCode));
		}
        
		return result;
	}
}
