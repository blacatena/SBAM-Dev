package com.scholastic.sbam.server.servlets;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.scholastic.sbam.server.database.codegen.Snapshot;
import com.scholastic.sbam.server.database.objects.DbSnapshot;
import com.scholastic.sbam.server.database.util.HibernateUtil;
import com.scholastic.sbam.server.reporting.SnapshotExcelWorkbookMaker;
import com.scholastic.sbam.server.reporting.SnapshotMaker;
import com.scholastic.sbam.server.util.SecurityEnforcer;
import com.scholastic.sbam.shared.objects.Authentication;
import com.scholastic.sbam.shared.security.SecurityManager;
import com.scholastic.sbam.shared.util.AppConstants;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class SnapshotExcelServlet extends HttpServlet {
	
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
		
		testSnapshot(snapshotId);
		
        setResponseHeaders(response, snapshotId);

        // This does the actual work of creating the spreadsheet
        SnapshotExcelWorkbookMaker wbMaker = new SnapshotExcelWorkbookMaker(snapshotId);
        HSSFWorkbook wb = wbMaker.getWorkbook();
        
        // Write the output 
        OutputStream out = response.getOutputStream();
        wb.write(out);
        out.flush();
        out.close();
    }
	
    /**
     * Verify that the snapshot exists, is ready to be accessed, and if necessary compile the snapshot.
     * @param snapshotId
     * @throws ServletException
     */
	protected void testSnapshot(int snapshotId) throws ServletException {
		Snapshot dbSnapshot = null;
		
		HibernateUtil.openSession();
		HibernateUtil.startTransaction();

		try {
			
			//	Get existing
			dbSnapshot = DbSnapshot.getById(snapshotId);
			if (dbSnapshot == null)
				throw new ServletException("Snapshot " + snapshotId + " not found.");
			
			if (dbSnapshot.getStatus() != AppConstants.STATUS_ACTIVE)
				if (dbSnapshot.getStatus() == AppConstants.STATUS_COMPILING)	
					throw new ServletException("INTERNAL SAFETY CHECK FAILED: Snapshot is currently already compiling.");
				else	
					throw new ServletException("INTERNAL SAFETY CHECK FAILED: Invalid snapshot status " + dbSnapshot.getStatus() + ".");
			
		} finally {
			if (HibernateUtil.isTransactionInProgress())
				HibernateUtil.endTransaction();
			HibernateUtil.closeSession();
		}
		
		if (dbSnapshot != null && dbSnapshot.getSnapshotTaken() == null)
			compileSnapshot(snapshotId);
	}
    
	/**
	 * Compile the snapshot.
	 * @param snapshotId
	 */
    protected void compileSnapshot(int snapshotId) {
    	new SnapshotMaker().makeSnapshot(snapshotId);
    }
    
    /**
     * Set the response headers for an Excel spreadsheet.
     * @param response
     * @param snapshotId
     */
    protected void setResponseHeaders(HttpServletResponse response, int snapshotId) {
		
		String filename = "SBAMSnapshot" + snapshotId;
		
    	response.setContentType("application/vnd.ms-excel");
        //	Copied from ListBuilder
        response.setHeader("Content-disposition", "attachment; filename=\"" + filename +".xls");
        
        //	For IE 6, which messes up downloads unless cache control is overridden
        response.setHeader("Expires", "Sat, 6 May 2105 12:00:00 GMT");
        response.setHeader("Cache-Control", "private");
    }
    
    /**
     * Extract the requested snapshot ID from the request parameters.
     * @param request
     * @return
     * @throws ServletException
     */
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
}
