<%@ page language="java" buffer="12kb" contentType="text/html" import="com.badiyan.uk.beans.*,java.util.*,org.apache.commons.beanutils.*,com.badiyan.uk.online.beans.*" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="struts-bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="struts-html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="struts-logic" %>

<jsp:useBean id="adminCompany" class="com.badiyan.uk.online.beans.UKOnlineCompanyBean" scope="session" />
<jsp:useBean id="adminPerson" class="com.badiyan.uk.online.beans.UKOnlinePersonBean" scope="session"/>
<jsp:useBean id="listHelper" class="com.badiyan.uk.online.beans.EcolabListHelperBean" scope="session" />
<jsp:useBean id="loginBean" class="com.badiyan.uk.online.beans.UKOnlineLoginBean" scope="session" />
<jsp:useBean id="courseSearch" class="com.badiyan.uk.online.beans.UKOnlineCourseSearchBean" scope="session"/>
<jsp:useBean id="resourceSearch" class="com.badiyan.uk.online.beans.UKOnlineResourceSearchBean" scope="session" />
<jsp:useBean id="adminCourse" class="com.badiyan.uk.beans.CourseBean" scope="session" />

<%@ include file="channels/channel-session.jsp" %>
<%@ include file="channels/channel-permissions.jsp" %>

<%

String errorMessage = "";
if (request.getParameter("id") != null)
{
    try
    {
        adminPerson = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(Integer.parseInt(request.getParameter("id")));
        session.setAttribute("adminPerson", adminPerson);
    }
    catch (NumberFormatException x)
    {
        errorMessage = x.getMessage();
    }
}
else
{
    //adminPerson = new PersonBean();
}

if (request.getParameter("deleteId") != null)
{
    try
    {
        EnrollmentBean.deleteWithErrorMessage(request.getParameter("deleteId"));
        adminPerson.invalidateEnrollments();
    }
    catch (Exception x)
    {
        errorMessage = x.getMessage();
    }
}

String stepString = "Step 2: Roles";

listHelper.setCompany(adminCompany);
listHelper.setPerson(loginBean.getPerson());
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">

	<head>

		<title>Universal Knowledge</title>

		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
		<meta name="generator" content="TextPad 4.6.2" />
		<meta name="author" content="" />
		<meta name="copyright" content="&copy; 2007" />
		<meta name="keywords" content="" />
		<meta name="description" content="" />

		<link rel="stylesheet" type="text/css" href="../css/web.css" />

		<script type="text/javascript" src="../scripts/crir.js"></script>
		<!--[if lte IE 7]><script type="text/javascript" src="scripts/optionDisabledSupport.js"></script><![endif]-->
		
<%@ include file="..\channels\channel-head-javascript.jsp" %>

	</head>

	<body onload="javascript:initErrors();">

		<div id="main">

<%@ include file="..\channels\channel-menu.jsp" %>

			<div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>

			<!-- *** BEGIN Content *** -->
			<div id="content">

				<div class="content_TextBlock">
					<p class="headline"><%= adminCompany.getLabel() %> Administration</p>
					<div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>
				</div>

				<div class="content_Administration">

<%@ include file="channels\channel-admin-menu.jsp" %>

					<div class="main">

						<p class="headlineA">User Profile&nbsp;&#47;&nbsp;Roles</p>
						<p class="currentObjectName"><%= adminPerson.getLabel() %></p>
						<p>Use this screen to update user role information. You may select multiple roles by holding
the Ctrl or Shift keys while clicking.</p>

						<struts-html:form action="/admin/userRollAssign">

							<input id="dummy" name="dummy" type="hidden" />

							<p>
								<select name="userRoleAssignSelect" class="multipleSelect" size="12" multiple="multiple" style="width: 492px;">
<%
        Iterator role_itr = listHelper.getAssignableRoles().iterator();
        for (int i = 0; role_itr.hasNext(); i++)
        {
            RoleBean role_obj = (RoleBean)role_itr.next();
%>
									<option value="<%= role_obj.getValue() %>"<%= adminPerson.hasRole(role_obj) ? " selected=\"selected\"" : "" %>><%= role_obj.getLabel() %></option>
<%
        }
%>
								</select>
							</p>

							<p style="margin-top: 20px; ">
								<input class="formbutton" type="submit" value="Update Roles" alt="Update Roles" />
							</p>

						</struts-html:form>


					</div>

					<div class="end"></div>
				</div>

				<div class="content_TextBlock">
					<div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>
				</div>

			</div>
			<!-- *** END Content *** -->

			<div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>

<%@ include file="..\channels\channel-footer.jsp" %>

		</div>

	</body>

</html>