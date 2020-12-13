<%
if (session.isNew())
{
    RequestDispatcher rd = pageContext.getServletContext().getRequestDispatcher("/admin/memory.jsp");
    request.setAttribute("session-expired", "true");
    rd.forward(request, response);
    return;
}
%>