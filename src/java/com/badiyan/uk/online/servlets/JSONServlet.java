/*
 * JSONServlet.java
 *
 * Created on November 3, 2007, 3:04 PM
 */

package com.badiyan.uk.online.servlets;

import java.util.*;
import java.io.*;
import java.net.*;

import javax.servlet.*;
import javax.servlet.http.*;

/**
 *
 * @author marlo
 * @version
 */
public class JSONServlet extends HttpServlet
{
    
    /** Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException
    {
	System.out.println("processRequest() invoked in JSONServlet");
	
	/*
	response.setContentType("text/plain;charset=UTF-8");
	response.setHeader("Cache-Control", "no-cache");
	PrintWriter out = response.getWriter();
	
	out.println("<html>");
	out.println("<head>");
	out.println("<title>Servlet JSONServlet</title>");
	out.println("</head>");
	out.println("<body>");
	out.println("<h1>Servlet JSONServlet at " + request.getContextPath () + "</h1>");
	out.println("</body>");
	out.println("</html>");
	 
	out.close();
	 */
	
	Map valMap = request.getParameterMap();
	StringBuffer body = new StringBuffer("{\n");
	
	if(valMap != null)
	{
	    String val=null;
	    String key = null;
	    Map.Entry me = null;
	    Set entries =  valMap.entrySet(  );
	    
	    int size = entries.size(  );
	    int counter=0;
	    for(Iterator iter= entries.iterator(  );iter.hasNext(  );)
	    {
		counter++;
		me=(Map.Entry) iter.next(  );
		val= ((String[])me.getValue(  ))[0];
		key = (String) me.getKey(  );
		if(counter < size)
		{
		    body.append(key).append(":\"").append(val).append("\",\n");
		}
		else
		{
		    //remove comma for last entry
		    body.append(key).append(":\"").append(val).append("\"\n");
		}
	    }
	    
	}
	body.append("}");
	//AjaxUtil.sendText(httpServletResponse,body.toString(  ));
	
	response.setContentType("text/plain;charset=UTF-8");
	response.setHeader("Cache-Control", "no-cache");
	PrintWriter out = response.getWriter();
	
	/*
	out.println("<html>");
	out.println("<head>");
	out.println("<title>Servlet JSONServlet</title>");
	out.println("</head>");
	out.println("<body>");
	out.println("<h1>Servlet JSONServlet at " + request.getContextPath () + "</h1>");
	out.println("</body>");
	out.println("</html>");
	 */
	
	out.print(body.toString());
	 
	out.close();
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
