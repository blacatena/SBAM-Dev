package com.scholastic.sbam.server.servlets;

import javax.servlet.*;

import javax.servlet.http.HttpServlet;

import com.scholastic.sbam.server.fastSearch.HelpTextCache;
import com.scholastic.sbam.server.fastSearch.InstitutionCache;
import com.scholastic.sbam.server.util.AppServerConstants;

public class Initializer extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
	//	PrintWriter out = response.getWriter();
	
		try {
			AppServerConstants.init(config.getServletContext().getRealPath("/"));	//, ">>>  ");
			System.out.println("Institution cache object is " + InstitutionCache.getSingleton(AppServerConstants.getInstCacheConfig()));
			System.out.println("Help Text cache object is " + HelpTextCache.getSingleton());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	//	out.close();
	}
}
