<%

if (CUBean.isMasterServer())
{
%>
<jsp:forward page="logout.jsp?logout=true" />
<%
}
%>