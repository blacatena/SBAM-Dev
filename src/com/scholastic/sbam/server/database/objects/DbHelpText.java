package com.scholastic.sbam.server.database.objects;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import com.scholastic.sbam.server.database.codegen.HelpText;
import com.scholastic.sbam.server.database.util.HibernateAccessor;
import com.scholastic.sbam.server.database.util.HibernateUtil;
import com.scholastic.sbam.shared.objects.HelpTextInstance;

/**
 * Sample Help Text database table accessor class, extending HibernateAccessor, and implementing custom get/find methods.
 * 
 * @author Bob Lacatena
 *
 */
public class DbHelpText extends HibernateAccessor {
	
	static String objectName = HelpText.class.getSimpleName();
	
	public static HelpTextInstance getInstance(HelpText dbInstance) {
		HelpTextInstance instance = new HelpTextInstance();
		
		instance.setId(dbInstance.getId());
		instance.setIconName(dbInstance.getIconName());
		instance.setTitle(dbInstance.getTitle());
		instance.setText(dbInstance.getText());
		instance.setParentId(dbInstance.getParentId());
		instance.setFirstChildId(dbInstance.getFirstChildId());
		instance.setNextSiblingId(dbInstance.getNextSiblingId());
		instance.setPrevSiblingId(dbInstance.getPrevSiblingId());
		instance.setRelatedIdsList(dbInstance.getRelatedIds());
		
		// If there is a current transaction, then populate the info for related entries, the children, siblings and parent
		if (HibernateUtil.isTransactionInProgress()) {
			setRelatives(instance);
		}
		
		return instance;
	}
	
	/**
	 * Set the parent, sibling and related instance information in this instance
	 * @param instance
	 */
	public static void setRelatives(HelpTextInstance instance) {
		// Do parent
		if (instance.getParentId() != null && instance.getParentId().length() > 0) {
			HelpText parent = getByCode(instance.getParentId());
			if (parent != null) {
				instance.setParentTitle(parent.getTitle());
				instance.setParentIconName(parent.getIconName());
			}
		}
		// Do next sibling
		if (instance.getNextSiblingId() != null && instance.getNextSiblingId().length() > 0) {
			HelpText nextSibling = getByCode(instance.getNextSiblingId());
			if (nextSibling != null) {
				instance.setNextSiblingTitle(nextSibling.getTitle());
				instance.setNextSiblingIconName(nextSibling.getIconName());
			}
		}
		// Do previous sibling
		if (instance.getPrevSiblingId() != null && instance.getPrevSiblingId().length() > 0) {
			HelpText prevSibling = getByCode(instance.getPrevSiblingId());
			if (prevSibling != null) {
				instance.setPrevSiblingTitle(prevSibling.getTitle());
				instance.setPrevSiblingIconName(prevSibling.getIconName());
			}
		}
		// Do related IDs
		List<String> relatedIds = instance.parseIdList(instance.getRelatedIdsList());
		for (String relatedId : relatedIds) {
			HelpText relatedInstance = getByCode(relatedId);
			if (relatedInstance != null)
				instance.addRelated(relatedInstance.getId(), relatedInstance.getTitle(), relatedInstance.getIconName());
		}
		//	Set this to null so it doesn't get done again by accident
		instance.setRelatedIdsList(null);
		// Do child IDs
		List<HelpText> children = DbHelpText.findChildren(instance.getFirstChildId());
		for (HelpText child : children) {
			instance.addChild(child.getId(), child.getTitle(), child.getIconName());
		}
		
	}
	
	public static HelpText getByCode(String code) {
		return (HelpText) getByField(objectName, "id", code, "title");
	}
	
	public static List<HelpText> findAll() {
		List<Object> results = findAll(objectName);
		List<HelpText> reasons = new ArrayList<HelpText>();
		if (results != null)
			for (int i = 0; i < results.size(); i++)
				reasons.add((HelpText) results.get(i));
		return reasons;
	}
	

	
	public static List<HelpText> findChildren(HelpText parent) {
		return findChildren(parent.getFirstChildId());
	}
	
	public static List<HelpText> findChildren(String firstChildId) {
        try
        {
			List<HelpText> objects = new ArrayList<HelpText>();
        	String nextSiblingId = firstChildId;
        	while (nextSiblingId != null && nextSiblingId.length() > 0) {
                HelpText child = getByCode(nextSiblingId);
                if (child != null) {
                	objects.add(child);
                	nextSiblingId = child.getNextSiblingId();
                } else
                	break;
        	}
        	return objects;
        }
        catch(Exception e)
        {
        	e.printStackTrace();
            System.out.println(e.getMessage());
        }
        return new ArrayList<HelpText>();
	}
	
	public static List<HelpText> find(String [] ids) {
        try
        {
            Criteria crit = sessionFactory.getCurrentSession().createCriteria(getObjectReference(objectName));
            if (ids != null && ids.length > 0)
            	crit.add(Restrictions.in("id", ids));
            @SuppressWarnings("unchecked")
			List<HelpText> objects = crit.list();
            return objects;
        }
        catch(Exception e)
        {
        	e.printStackTrace();
            System.out.println(e.getMessage());
        }
        return new ArrayList<HelpText>();
	}
	
	public static List<HelpText> find(List<String> ids) {
        try
        {
            Criteria crit = sessionFactory.getCurrentSession().createCriteria(getObjectReference(objectName));
            if (ids != null && ids.size() > 0)
            	crit.add(Restrictions.in("id", ids));
            @SuppressWarnings("unchecked")
			List<HelpText> objects = crit.list();
            return objects;
        }
        catch(Exception e)
        {
        	e.printStackTrace();
            System.out.println(e.getMessage());
        }
        return new ArrayList<HelpText>();
	}
}
