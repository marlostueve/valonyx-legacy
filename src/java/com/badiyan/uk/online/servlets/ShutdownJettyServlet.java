/*
 * ShutdownJettyServlet.java
 *
 * Created on March 12, 2007, 11:39 AM
 */

package com.badiyan.uk.online.servlets;

import java.io.*;
import java.net.*;

import javax.servlet.*;
import javax.servlet.http.*;

/**
 *
 * @author marlo
 * @version
 */
public class ShutdownJettyServlet extends HttpServlet
{
    
    /** Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException
    {
	response.setContentType("text/html;charset=UTF-8");
	PrintWriter out = response.getWriter();
	
	out.println("<html>");
	out.println("<head>");
	out.println("<title>Servlet ShutdownJettyServlet</title>");
	out.println("</head>");
	out.println("<body>");
	out.println("<h1>Servlet ShutdownJettyServlet at " + request.getContextPath () + "</h1>");
	out.println("</body>");
	out.println("</html>");
	
	try
	{
	    Socket s=new Socket(InetAddress.getByName("127.0.0.1"), 8079);
	    OutputStream outStream=s.getOutputStream();
	    outStream.write(("stopJetty\r\nstop\r\n").getBytes());
	    outStream.flush();
	    s.close();
	}
	catch (Exception x)
	{
	    x.printStackTrace();
	}
	
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
