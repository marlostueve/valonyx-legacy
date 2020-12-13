<%
   /*
response.setHeader("Cache-Control", "no-cache");
response.setHeader("Cache-Control", "no-store");
response.setHeader("Pragma", "no-cache");
response.setDateHeader("Expires", 0);
 */
   
if (session.isNew())
{
    String forward_to_page = "/login.jsp";
    if (!CUBean.isMasterServer())
	forward_to_page = "/index.jsp";
    RequestDispatcher rd = pageContext.getServletContext().getRequestDispatcher(forward_to_page);
    request.setAttribute("session-expired", "true");
    rd.forward(request, response);
    return;
}

/*
response.setHeader("Cache-Control","no-cache"); //Forces caches to obtain a new copy of the page from the origin server
response.setHeader("Cache-Control","no-store"); //Directs caches not to store the page under any circumstance
response.setDateHeader("Expires", 0); //Causes the proxy cache to see the page as "stale"
response.setHeader("Pragma","no-cache"); //HTTP 1.0 backward compatibility
UKOnlineLoginBean loginBean_x = (UKOnlineLoginBean)session.getAttribute("loginBean");
if (null == loginBean_x) {
   request.setAttribute("Error", "Session has ended.  Please login.");
   RequestDispatcher rd = request.getRequestDispatcher("login.jsp");
   rd.forward(request, response);
}
 */
%>