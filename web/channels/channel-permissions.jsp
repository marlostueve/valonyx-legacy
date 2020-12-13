<%
if (!CUBean.isMasterServer())
{
    if (!request.getRemoteAddr().equals("127.0.0.1"))
    {
	RequestDispatcher rd = pageContext.getServletContext().getRequestDispatcher("/error");
	request.setAttribute("session-expired", "true");
	rd.forward(request, response);
	return;
    }
}
try
{
    loginBean.getPerson();
}
catch (Exception x)
{
    System.out.println("LOGIN ERROR >>" + x.getMessage());
    x.printStackTrace();
%>
<jsp:forward page="logout.jsp?logout=true" />
<%
}
%>