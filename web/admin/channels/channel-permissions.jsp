<%
try
{
    if (!(loginBean.getPerson().hasRole(RoleBean.SYSTEM_ADMINISTRATOR_ROLE_NAME) ||
        loginBean.getPerson().hasRole(RoleBean.TRAINING_ADMINISTRATOR_ROLE_NAME) ||
        loginBean.getPerson().hasRole(RoleBean.COMPANY_ADMINISTRATOR_ROLE_NAME) ||
        loginBean.getPerson().hasRole(UKOnlineRoleBean.DEPARTMENT_ADMINISTRATOR_ROLE_NAME)))
    {
        RequestDispatcher rd = pageContext.getServletContext().getRequestDispatcher("/admin/index.jsp");
        rd.forward(request, response);
        return;
    }
}
catch (Exception x)
{
    System.out.println(x.getMessage());
    x.printStackTrace();
    RequestDispatcher rd = pageContext.getServletContext().getRequestDispatcher("/admin/index.jsp");
    rd.forward(request, response);
    return;
}
%>