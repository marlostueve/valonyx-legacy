

<%
/*
if (request.getParameter("logout") != null)
{
    if (request.getParameter("logout").equals("true"))
    {
        session.invalidate();
        RequestDispatcher rd = pageContext.getServletContext().getRequestDispatcher("/login.jsp");
        request.removeAttribute("logout");
        rd.forward(request, response);
        return;
    }
}
*/

session.invalidate();
RequestDispatcher rd = pageContext.getServletContext().getRequestDispatcher("/admin/schedule-chrome.jsp");
request.removeAttribute("logout");
rd.forward(request, response);
return;
%>