/* 
* This example is from javareference.com 
* for more information visit, 
* http://www.javareference.com 
*/

package com.badiyan.uk.online.servlets;
 
//import jr.beans.common.AuthorsBean;

import java.util.*;
import java.io.*; 
import javax.servlet.http.*;

/**
 * This servlet handles the get author profile
 * ajax request. It gets the author name as parameter
 * queries the AuthorBean for the author profile and
 * returns the profile back.
 * 
 * @author Rahul Sapkal(rahul@javareference.com)
 */
public class GetAuthorsProfile extends HttpServlet 
{    
    
    private Random randy = new Random();
    private int i = 1;
    
    /**
     * This method is overriden from the base class to handle the
     * get request. 
     */
    protected void doGet(HttpServletRequest requestObj, HttpServletResponse responseObj)
                   throws IOException
    {
        //set the content type
        responseObj.setContentType("text/xml");
        
        responseObj.setHeader("Cache-Control", "no-cache");
        
        //get the PrintWriter object to write the html page
        PrintWriter writer = responseObj.getWriter();
        
        //get parameters store into the hashmap
        HashMap paramsMap = new HashMap();
        Enumeration paramEnum = requestObj.getParameterNames();
        while(paramEnum.hasMoreElements())
        {
            String paramName = (String)(paramEnum.nextElement());
            paramsMap.put(paramName, requestObj.getParameter(paramName));
        }
        //get the author name passed
        String authorName= (String)paramsMap.get("author");
        
        //creating the author bean
        //AuthorsBean authBean = new AuthorsBean();
	
	
        
        //get the author profile by quering the AuthorsBean by passing author name
         writer.println("<Profile><![CDATA[" + i++ + "]]></Profile>");
	 
        
        //close the write
        writer.close();                    
    }        
}