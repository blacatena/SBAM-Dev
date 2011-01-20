package com.scholastic.sbam.server.database.objects;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.scholastic.sbam.server.database.codegen.PreferenceCode;
import com.scholastic.sbam.server.database.util.HibernateAccessor;

/**
 * Sample database table accessor class, extending HibernateAccessor, and implementing custom get/find methods.
 * 
 * @author Bob Lacatena
 *
 */
public class DbPreferenceCode extends HibernateAccessor {
	
	static String objectName = PreferenceCode.class.getSimpleName();
	
	public static PreferenceCode getByCode(String catCode, String selCode) {
		List<PreferenceCode> list = findFiltered(catCode, selCode, null, null, (char) 0, (char) 0);
		if (list == null || list.size() == 0)
			return null;
		return list.get(0);
	}
	
	public static List<PreferenceCode> findAll() {
		List<Object> results = findAll(objectName);
		List<PreferenceCode> reasons = new ArrayList<PreferenceCode>();
		for (int i = 0; i < results.size(); i++)
			reasons.add((PreferenceCode) results.get(i));
		return reasons;
	}
	
	/**
	 * Find all preference codes for a preference category.
	 * @param catCode
	 * @param excludeStatus
	 *  Use 'X' to exclude all delete codes.
	 * @return
	 */
	public static List<PreferenceCode> findByCategory(String catCode, char excludeStatus) {
		return findFiltered(catCode, null, null, null, (char) 0, excludeStatus);
	}
	
	public static List<PreferenceCode> findFiltered(String catCode, String selCode, String description, String exportValue, char status, char neStatus) {
        try
        {
            Criteria crit = sessionFactory.getCurrentSession().createCriteria(getObjectReference(objectName));
            if (catCode != null && catCode.length() > 0)
            	crit.add(Restrictions.like("id.prefCatCode", catCode));
            if (selCode != null && selCode.length() > 0)
            	crit.add(Restrictions.like("id.prefSelCode", selCode));
            if (description != null && description.length() > 0)
            	crit.add(Restrictions.like("description", description));
            if (exportValue != null && exportValue.length() > 0)
            	crit.add(Restrictions.like("exportValue", exportValue));
            if (status != 0)
            	crit.add(Restrictions.like("status", status));
            if (neStatus != 0)
            	crit.add(Restrictions.ne("status", neStatus));
            crit.addOrder(Order.asc("seq"));
            @SuppressWarnings("unchecked")
			List<PreferenceCode> objects = crit.list();
            return objects;
        }
        catch(Exception e)
        {
        	e.printStackTrace();
            System.out.println(e.getMessage());
        }
        return new ArrayList<PreferenceCode>();
	}
}
