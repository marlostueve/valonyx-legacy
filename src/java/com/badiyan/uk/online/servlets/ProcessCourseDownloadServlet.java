/*
 * ProcessCourseDownloadServlet.java
 *
 * Created on March 9, 2007, 11:24 AM
 */

package com.badiyan.uk.online.servlets;

import java.io.*;
import java.net.*;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;

import com.badiyan.uk.beans.*;
import com.badiyan.uk.online.beans.*;
import com.badiyan.uk.online.tasks.*;

/**
 *
 * @author marlo
 * @version
 */
public class
ProcessCourseDownloadServlet
    extends HttpServlet
{
    private UKOnlineLoginBean loginBean = null;
    private CourseBean courseBean;
    private ProcessCourseDownloadTask sync_task = null;
    
    private Vector messages = new Vector();
    
    private boolean running_sync = false;
    
    /** Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     */
    protected synchronized void
    processRequest(HttpServletRequest _request, HttpServletResponse _response)
	throws ServletException, IOException
    {
	//System.out.println("processRequest() invoked in ProcessSyncObjectsServlet");
	
	HttpSession session = _request.getSession(false);
	loginBean = (UKOnlineLoginBean)session.getAttribute("loginBean");
	courseBean = (CourseBean)session.getAttribute("courseBean");
		
	System.out.println("command >" + _request.getParameter("command"));
	
	String command = _request.getParameter("command");
	
	//set the content type
	_response.setContentType("text/xml");
	_response.setHeader("Cache-Control", "no-cache");

	//get the PrintWriter object to write the html page
	PrintWriter writer = _response.getWriter();
	
	try
	{
	    System.out.println("running >" + running_sync);
	    if (!running_sync && (messages.size() == 0))
	    {
		running_sync = true;
		//System.out.println("start task...");
		sync_task = new ProcessCourseDownloadTask(this, messages, command.equals("update"), loginBean, courseBean);
		new Thread(sync_task).start();
	    }

	    synchronized (messages)
	    {
		String message = null;
		if (messages.size() > 0)
		{
		    message = (String)messages.remove(0);
		}
		
		if (message != null)
		    writer.println(message);
	    }
	}
	catch (Exception x)
	{
	    messages.addElement("<error><![CDATA[" + x.getMessage() + "]]></error>");
	    x.printStackTrace();
	}
	
	writer.close();
    }
    
    public void
    notifyComplete()
    {
	running_sync = false;
    }
    
    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /** Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException
    {
	processRequest(request, response);
    }
    
    /** Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException
    {
	processRequest(request, response);
    }
    
    /** Returns a short description of the servlet.
     */
    public String getServletInfo()
    {
	return "Short description";
    }
    // </editor-fold>
}

