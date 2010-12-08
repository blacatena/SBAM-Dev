package com.scholastic.sbam.server.database.util;

import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.hibernate.*;
import org.hibernate.cfg.*;

import com.mchange.v2.c3p0.PooledDataSource;

public class HibernateUtil {

    private static final SessionFactory sessionFactory;

    static {
        try {
            // Create the SessionFactory from hibernate.cfg.xml
            sessionFactory = new Configuration().configure().buildSessionFactory();
        } catch (Throwable ex) {
            // Make sure you log the exception, as it might be swallowed
            System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
    
    public static Session openSession() {
    	return sessionFactory.openSession();
    }
    
    public static Session getSession() {
    	return sessionFactory.getCurrentSession();
    }
    
    public static void closeSession() {
    	sessionFactory.getCurrentSession().close();
    }
    
    public static void startTransaction() {
    	/*
    	 * 	This clumsy little bit of retry logic is necessary because for some reason MySQL connections go stale in Hibernate and C3P0.
    	 * 	It's all of the Internet with a variety of workarounds and solutions, none of which are perfect (nor is this, but it mitigates the problem).
    	 */
    	int retries = 5;
    	while (retries > 0) {
    		try {
	    		sessionFactory.getCurrentSession().beginTransaction();
	    		return;
    		} catch (Exception e){
    			closeSession();
    			openSession();
    		}
    	}
		sessionFactory.getCurrentSession().beginTransaction();
    }
    
    public static void endTransaction() {
    	sessionFactory.getCurrentSession().flush();
    }
    
    public static boolean isTransactionInProgress() {
    	Transaction t = sessionFactory.getCurrentSession().getTransaction();
    	return (t != null && t.isActive());
    }
    
    public static Connection getConnection() throws SQLException {
    	return new Configuration().configure().buildSettings().getConnectionProvider().getConnection(); 
    }
    
    public static void poolConsoleQuery(String dsname) {
		try {
			// fetch a JNDI-bound DataSource 
			InitialContext ictx = new InitialContext();
			DataSource ds = (DataSource) ictx.lookup( "java:comp/env/jdbc/" + dsname );
			// make sure it's a c3p0 PooledDataSource 
			if ( ds == null ) {
				System.err.println("DataSource " + dsname + " not found.");
			} else if ( ds instanceof PooledDataSource) { 
	    		PooledDataSource pds = (PooledDataSource) ds;
	    		System.err.println("num_connections: " + pds.getNumConnectionsDefaultUser()); 
	    		System.err.println("num_busy_connections: " + pds.getNumBusyConnectionsDefaultUser()); 
	    		System.err.println("num_idle_connections: " + pds.getNumIdleConnectionsDefaultUser()); 
	    		System.err.println(pds.getAllUsers());
	    		System.err.println(); 
	    	} else
	    		System.err.println("Not a c3p0 PooledDataSource! "+ ds.getClass().getName()); 
			System.out.println("Prepare Statement Count " + sessionFactory.getStatistics().getPrepareStatementCount());
			System.out.println("Session Open Count " + sessionFactory.getStatistics().getSessionOpenCount());
			System.out.println("Session Close Count " + sessionFactory.getStatistics().getSessionCloseCount());
			System.out.println("Transaction Count " + sessionFactory.getStatistics().getTransactionCount());
			System.out.println("Query Execution Count " + sessionFactory.getStatistics().getQueryExecutionCount());
		} catch (NamingException e) {
			System.out.println(e);
			e.printStackTrace();
		} catch (SQLException e) {
			System.out.println(e);
			e.printStackTrace();
		}
    	
    }

}
