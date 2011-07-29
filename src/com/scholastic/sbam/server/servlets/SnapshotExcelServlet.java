package com.scholastic.sbam.server.servlets;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.scholastic.sbam.server.reporting.SnapshotExcelWorkbookMaker;
import com.scholastic.sbam.server.util.SecurityEnforcer;
import com.scholastic.sbam.shared.objects.Authentication;
import com.scholastic.sbam.shared.security.SecurityManager;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class SnapshotExcelServlet extends HttpServlet {

	public void genearteExcel() throws IllegalArgumentException {
		
	}
	
	public void init(ServletConfig config) throws ServletException {
        super.init(config);  
    }

    public void destroy() {
    }

    /** Processes requests for both HTTP GET and POST methods.
     * @param request servlet request
     * @param response servlet response
     */

    protected void processRequest(HttpServletRequest request,
        HttpServletResponse response) throws ServletException, IOException {
	
    	try {
    		authenticate("get snapshot excel file", SecurityManager.ROLE_QUERY);
    	} catch (IllegalArgumentException exc) {
    		throw new ServletException(exc);
    	}
    	
		int snapshotId = getSnapshotId(request);
		
        setResponseHeaders(response, snapshotId);

        SnapshotExcelWorkbookMaker wbMaker = new SnapshotExcelWorkbookMaker(snapshotId);
              
        HSSFWorkbook wb = wbMaker.getWorkbook();
        
        // Write the output 
        OutputStream out = response.getOutputStream();
        wb.write(out);
        out.flush();
        out.close();
    }
    
    protected void setResponseHeaders(HttpServletResponse response, int snapshotId) {
		
		String filename = "SBAMSnapshot" + snapshotId;
		
    	response.setContentType("application/vnd.ms-excel");
        //	Copied from ListBuilder
        response.setHeader("Content-disposition", "attachment; filename=\"" + filename +".xls");
        
        //	For IE 6, which messes up downloads unless cache control is overridden
        response.setHeader("Expires", "Sat, 6 May 2105 12:00:00 GMT");
        response.setHeader("Cache-Control", "private");
    }
    
    protected int getSnapshotId(HttpServletRequest request) throws ServletException {
    	 String snapshotIdStr = request.getParameter("snapshotId");
         if (snapshotIdStr == null)
         	throw new ServletException("Snapshot ID is a required parameter.");
         try {
         	return Integer.parseInt(snapshotIdStr);
         } catch (NumberFormatException nfExc) {
         	throw new ServletException("Invalid snapshot ID " + snapshotIdStr);
         }
    }

    /** Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     */

    protected void doGet(HttpServletRequest request,
        HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    /** Handles the HTTP POST method.
     * @param request servlet request
     * @param response servlet response
     */

    protected void doPost(HttpServletRequest request,
        HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    /** Returns a short description of the servlet.
     */

    public String getServletInfo() {
       return "Example to create a workbook in a servlet using HSSF";
    }
	/**
	 * Verify that the user is authenticated for the requested task.
	 * 
	 * The Authentication object is returned in case it is needed by the servlet, but can be ignored.
	 * 
	 * @param taskDesc
	 *  The description of the task 
	 * @param roleName
	 * 	The role name required for the user to perform the task.
	 * @return
	 *  The Authentication currently active for the session.
	 * @throws IllegalArgumentException
	 */
	protected Authentication authenticate(String taskDesc, String roleName) throws IllegalArgumentException {
		return SecurityEnforcer.authenticate(this, taskDesc, roleName);
	}

	protected Authentication authenticate(String taskDesc) throws ServletException {
		return authenticate(taskDesc, null);
	}

	protected Authentication authenticate() throws ServletException {
		return authenticate(getServletName(), null);
	}
}
