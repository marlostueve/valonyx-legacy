/*
 * ReceiveSyncObjectsFromServerServlet.java
 *
 * Created on March 8, 2007, 11:22 AM
 */

package com.badiyan.uk.online.servlets;

import java.io.*;
import java.net.*;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;

import com.badiyan.uk.beans.*;

/**
 *
 * @author marlo
 * @version
 */
public class
ReceiveSyncObjectsFromServerServlet
    extends HttpServlet
{
    
    /** Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     */
    protected void
    processRequest(HttpServletRequest _request, HttpServletResponse _response)
	throws ServletException, IOException
    {
	System.out.println("processRequest() invoked in ReceiveSyncObjectsFromServerServlet");
	
	
	//System.out.println("command >" + _request.getParameter("command"));
	
	HashMap paramsMap = new HashMap();
        Enumeration paramEnum = _request.getParameterNames();
	System.out.println("paramEnum.hasMoreElements() >" + paramEnum.hasMoreElements());
        while(paramEnum.hasMoreElements())
        {
            String paramName = (String)(paramEnum.nextElement());
	    System.out.println("paramName >" + paramName + ", " + _request.getParameter(paramName));
            paramsMap.put(paramName, _request.getParameter(paramName));
        }
        //get the author name passed
        //String authorName= (String)paramsMap.get("author");
	
	
	//set the content type
	_response.setContentType("text/xml");
	_response.setHeader("Cache-Control", "no-cache");
	
	//get the PrintWriter object to write the html page
	PrintWriter writer = _response.getWriter();
	
	try
	{
	    Vector sync_objects = CUBean.receiveSyncObjectsFromServer();
	    writer.println("<status><![CDATA[" + sync_objects.size() + "]]></status>");
	}
	catch (Exception x)
	{
	    writer.println("<error><![CDATA[" + x.getMessage() + "]]></error>");
	}
	
	//close the write
	writer.close();
    }
    
    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /** Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     */
    protected void
    doGet(HttpServletRequest _request, HttpServletResponse _response)
	throws ServletException, IOException
    {
	System.out.println("doGet() invoked - " + _request.getParameter("command"));
	processRequest(_request, _response);
    }
    
    /** Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     */
    protected void
    doPost(HttpServletRequest _request, HttpServletResponse _response)
	throws ServletException, IOException
    {
	System.out.println("doPost() invoked - " + _request.getParameter("command"));
	processRequest(_request, _response);
    }
    
    /** Returns a short description of the servlet.
     */
    public String getServletInfo()
    {
	return "Short description";
    }
    // </editor-fold>
}
