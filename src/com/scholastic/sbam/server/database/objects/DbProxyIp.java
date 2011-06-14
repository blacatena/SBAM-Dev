package com.scholastic.sbam.server.database.objects;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.extjs.gxt.ui.client.Style.SortDir;
import com.scholastic.sbam.server.database.codegen.ProxyIp;
import com.scholastic.sbam.server.database.codegen.ProxyIpId;
import com.scholastic.sbam.server.database.util.HibernateAccessor;
import com.scholastic.sbam.shared.objects.ProxyIpInstance;
import com.scholastic.sbam.shared.util.AppConstants;

/**
 * Sample database table accessor class, extending HibernateAccessor, and implementing custom get/find methods.
 * 
 * @author Bob Lacatena
 *
 */
public class DbProxyIp extends HibernateAccessor {
	
	static String objectName = ProxyIp.class.getSimpleName();
	
	public static ProxyIpInstance getInstance(ProxyIp dbInstance) {
		ProxyIpInstance instance = new ProxyIpInstance();
		instance.setProxyId(dbInstance.getId().getProxyId());
		instance.setIpId(dbInstance.getId().getIpId());
		instance.setIpRangeCode(dbInstance.getIpRangeCode());
		instance.setIpLo(dbInstance.getIpLo());
		instance.setIpHi(dbInstance.getIpHi());
		instance.setApproved(dbInstance.getApproved());
		instance.setNote(dbInstance.getNote());
		instance.setStatus(dbInstance.getStatus());
		instance.setCreatedDatetime(dbInstance.getCreatedDatetime());
		
		return instance;
	}
	
	public static ProxyIp getById(int proxyId, int ipId) {
		ProxyIpId aid = new ProxyIpId();
		aid.setProxyId(proxyId);
		aid.setIpId(ipId);
		try {
			ProxyIp instance = (ProxyIp) sessionFactory.getCurrentSession().get(getObjectReference(objectName), aid);
//			if (instance == null) {
//				log.debug("get successful, no instance found");
//			} else {
//				log.debug("get successful, instance found");
//			}
			return instance;
		} catch (RuntimeException re) {
        	re.printStackTrace();
            System.out.println(re.getMessage());
			throw re;
		}
	}
	
	public static List<ProxyIp> findAll() {
		List<Object> results = findAll(objectName);
		List<ProxyIp> reasons = new ArrayList<ProxyIp>();
		if (results != null)
			for (int i = 0; i < results.size(); i++)
				reasons.add((ProxyIp) results.get(i));
		return reasons;
	}
	
	public static List<ProxyIp> findByProxyId(int proxyId, char status) {
		return findInRange(proxyId, 0, 0, status, AppConstants.STATUS_ANY_NONE, null, null);
	}
	
	public static List<ProxyIp> findInRange(int proxyId, long loIp, long hiIp, char status, char neStatus, String sortCol, SortDir sortDirection) {
        try
        {
            Criteria crit = sessionFactory.getCurrentSession().createCriteria(getObjectReference(objectName));
      
            if (proxyId > 0)
            	crit.add(Restrictions.eq("id.proxyId", proxyId));
            
            if (loIp > 0)
            	crit.add(Restrictions.ge("ipHi", loIp));
            if (hiIp > 0)
            	crit.add(Restrictions.le("ipLo", hiIp));
            
            if (status != 0)
            	crit.add(Restrictions.like("status", status));
            if (neStatus != 0)
            	crit.add(Restrictions.ne("status", neStatus));
            
            if (sortCol != null && sortCol.length() > 0) {
            	if (sortDirection == SortDir.ASC)
            		crit.addOrder(Order.asc(sortCol));
            	else
            		crit.addOrder(Order.desc(sortCol));
            } else {
            	crit.addOrder(Order.asc("id.proxyId"));
            	crit.addOrder(Order.asc("ipLo"));
            }
            
            @SuppressWarnings("unchecked")
			List<ProxyIp> objects = crit.list();
            return objects;
        }
        catch(Exception e)
        {
        	e.printStackTrace();
            System.out.println(e.getMessage());
        }
        return new ArrayList<ProxyIp>();
	}
	
	public static List<ProxyIp> findOverlapIps(long ipLo, long ipHi) {
		String ipRangeCode = ProxyIpInstance.getCommonIpRangeCode(ipLo, ipHi);
		if (ipRangeCode == null || ipRangeCode.length() == 0)
			return new ArrayList<ProxyIp>();
		
		//	By restricting the search by range codes, the database is able to effectively find potential overlaps
		List<String> rangeCodes = new ArrayList<String>();
		for (int i = 1; i < ipRangeCode.length(); i++) {
			rangeCodes.add(ipRangeCode.substring(0,i));
		}
		
        Criteria crit = sessionFactory.getCurrentSession().createCriteria(getObjectReference(objectName));

        //	This criterion is for database performance (i.e. prevents a full database scan)
        crit.add(Restrictions.or(Restrictions.in("ipRangeCode", rangeCodes), Restrictions.like("ipRangeCode", ipRangeCode + "%")));
        //	These criteria actually perform the real check
        crit.add(Restrictions.le("ipLo", ipHi));
        crit.add(Restrictions.ge("ipHi", ipLo));
        
        @SuppressWarnings("unchecked")
		List<ProxyIp> objects = crit.list();
        return objects;
	}
}
