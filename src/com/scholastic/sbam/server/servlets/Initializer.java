package com.scholastic.sbam.server.servlets;

import javax.servlet.*;

import javax.servlet.http.HttpServlet;

import com.scholastic.sbam.server.fastSearch.CustomerCache;
import com.scholastic.sbam.server.fastSearch.HelpTextCache;
import com.scholastic.sbam.server.fastSearch.InstitutionCache;
import com.scholastic.sbam.server.fastSearch.SiteInstitutionCache;
import com.scholastic.sbam.server.util.AppServerConstants;

public class Initializer extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
	//	PrintWriter out = response.getWriter();
	
		try {
			AppServerConstants.init(config.getServletContext().getRealPath("/"));	//, ">>>  ");
			//	Institution cache configuration comes from the sys_config table
			System.out.println("Institution cache object is " + InstitutionCache.getSingleton(AppServerConstants.getInstCacheConfig()));
			//	Customer cache configuration duplicates that from the sys_config table (but will change updateable = true)
			System.out.println("Customer cache object is " + CustomerCache.getSingleton(AppServerConstants.getInstCacheConfig().clone()));
			//	Site institution cache configuration duplicates that from the sys_config table (but will change updateable = true)
			System.out.println("Site institution cache object is " + SiteInstitutionCache.getSingleton(AppServerConstants.getInstCacheConfig().clone()));
			System.out.println("Help Text cache object is " + HelpTextCache.getSingleton());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	//	out.close();
	}
}
