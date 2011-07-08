package com.scholastic.sbam.server.database.objects;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.scholastic.sbam.server.database.codegen.Snapshot;
import com.scholastic.sbam.server.database.util.HibernateAccessor;
import com.scholastic.sbam.shared.objects.SnapshotInstance;

/**
 * Sample database table accessor class, extending HibernateAccessor, and implementing custom get/find methods.
 * 
 * @author Bob Lacatena
 *
 */
public class DbSnapshot extends HibernateAccessor {
	
	static String objectName = Snapshot.class.getSimpleName();
	
	public static SnapshotInstance getInstance(Snapshot dbInstance) {
		SnapshotInstance instance = new SnapshotInstance();

		instance.setSnapshotId(dbInstance.getSnapshotId());
		
		instance.setSnapshotName(dbInstance.getSnapshotName());
		instance.setSnapshotType(dbInstance.getSnapshotType());
		instance.setProductServiceType(dbInstance.getProductServiceType());
		instance.setSnapshotTaken(dbInstance.getSnapshotTaken());
		
		instance.setExpireDatetime(dbInstance.getExpireDatetime());
		instance.setCreateUserId(dbInstance.getCreateUserId());
		
		instance.setNote(dbInstance.getNote());
		instance.setOrgPath(dbInstance.getOrgPath());
		
		instance.setStatus(dbInstance.getStatus());
		instance.setCreatedDatetime(dbInstance.getCreatedDatetime());
		
		return instance;
	}
	
	public static Snapshot getById(int snapshotId) {
		try {
			Snapshot instance = (Snapshot) sessionFactory.getCurrentSession().get(getObjectReference(objectName), snapshotId);
			return instance;
		} catch (RuntimeException re) {
        	re.printStackTrace();
            System.out.println(re.getMessage());
			throw re;
		}
	}
	
	public static List<Snapshot> findAll() {
		List<Object> results = findAll(objectName);
		List<Snapshot> reasons = new ArrayList<Snapshot>();
		if (results != null)
			for (int i = 0; i < results.size(); i++)
				reasons.add((Snapshot) results.get(i));
		return reasons;
	}
	
	public static List<Snapshot> findFiltered(String snapshotType, char status, char neStatus) {
        try
        {
            Criteria crit = sessionFactory.getCurrentSession().createCriteria(getObjectReference(objectName));
            if (snapshotType != null)
            	crit.add(Restrictions.eq("snapshotType", snapshotType));
            if (status != 0)
            	crit.add(Restrictions.like("status", status));
            if (neStatus != 0)
            	crit.add(Restrictions.ne("status", neStatus));
            crit.addOrder(Order.asc("snapshotId"));
            @SuppressWarnings("unchecked")
			List<Snapshot> objects = crit.list();
            return objects;
        }
        catch(Exception e)
        {
        	e.printStackTrace();
            System.out.println(e.getMessage());
        }
        return new ArrayList<Snapshot>();
	}
	
	public static List<Snapshot> findForPresentation(String snapshotType, char status, char neStatus) {
        try
        {
            Criteria crit = sessionFactory.getCurrentSession().createCriteria(getObjectReference(objectName));
            if (snapshotType != null)
            	crit.add(Restrictions.eq("snapshotType", snapshotType));
            if (status != 0)
            	crit.add(Restrictions.like("status", status));
            if (neStatus != 0)
            	crit.add(Restrictions.ne("status", neStatus));
            crit.addOrder(Order.asc("seq"));
            crit.addOrder(Order.asc("orgPath"));
            crit.addOrder(Order.asc("snapshotId"));
            @SuppressWarnings("unchecked")
			List<Snapshot> objects = crit.list();
            return objects;
        }
        catch(Exception e)
        {
        	e.printStackTrace();
            System.out.println(e.getMessage());
        }
        return new ArrayList<Snapshot>();
	}
}
