<%@ page language="java" buffer="12kb" contentType="text/html" import="com.badiyan.uk.beans.*, java.util.*, com.badiyan.uk.online.beans.*" %>

<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="struts-bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="struts-html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="struts-logic" %>

<jsp:useBean id="adminCompany" class="com.badiyan.uk.online.beans.UKOnlineCompanyBean" scope="session" />
<jsp:useBean id="loginBean" class="com.badiyan.uk.online.beans.UKOnlineLoginBean" scope="session" />
<jsp:useBean id="personSearch" class="com.badiyan.uk.online.beans.UKOnlinePersonSearchBean" scope="session"/>
<jsp:useBean id="listHelper" class="com.badiyan.uk.online.beans.EcolabListHelperBean" scope="session" />
<jsp:useBean id="courseSearch" class="com.badiyan.uk.online.beans.UKOnlineCourseSearchBean" scope="session"/>
<jsp:useBean id="resourceSearch" class="com.badiyan.uk.online.beans.UKOnlineResourceSearchBean" scope="session" />
<jsp:useBean id="adminCourse" class="com.badiyan.uk.beans.CourseBean" scope="session" />

<%@ include file="channels/channel-session.jsp" %>
<%@ include file="channels/channel-permissions.jsp" %>

<%
personSearch.setCompany(adminCompany);
if (loginBean.getPerson().hasRole(UKOnlineRoleBean.DEPARTMENT_ADMINISTRATOR_ROLE_NAME))
    personSearch.setDepartment(loginBean.getPerson().getDepartment());
if (request.getParameter("search") != null)
{
    personSearch.clearSearchCriteria();
    personSearch.setKeyword(request.getParameter("search"));
    if (loginBean.getPerson().hasRole(UKOnlineRoleBean.DEPARTMENT_ADMINISTRATOR_ROLE_NAME))
	personSearch.setDepartment(loginBean.getPerson().getDepartment());
    personSearch.search();
    personSearch.setNumToDisplayPerPage(999);
}
else
{
    if (loginBean.getPerson().hasRole(UKOnlineRoleBean.DEPARTMENT_ADMINISTRATOR_ROLE_NAME))
	personSearch.setDepartment(loginBean.getPerson().getDepartment());
}

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
		<struts-html:javascript formName="personSearchForm" dynamicJavascript="true" staticJavascript="false"/>
		<script language="Javascript1.1" src="../staticJavascript.jsp"></script>
		
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

						<p class="headlineA">User Manager</p>
						<p>This screen allows you to select users to manage their information. Click a menu item on
the left to add, update, or delete information on Roles, Activities, or Certifications, or to
Assign Activities to a user.</p>
<p>Click the name of a user to go to the User Profile screen where you can view or update the
information associated with that user, or delete the user.</p>

						<div class="content_AdministrationTable">

							<div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>
							<div class="heading">
								<table cellspacing="0" cellpadding="0" border="0" summary="">
									<tr style="display: block;">
										<td style="width:  20px; text-align: left;  "></td>
										<td style="width: 382px; text-align: left;  ">User</td>
										<td style="width:  90px; text-align: center;">Employee #</td>
									</tr>
								</table>
							</div>
							<div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>
<%
Enumeration persons = personSearch.getList();
UKOnlinePersonBean person = null;
if (persons.hasMoreElements())
{
    while (persons.hasMoreElements())
    {
        person = (UKOnlinePersonBean)persons.nextElement();
%>
							<!-- BEGIN User -->
							<div class="user">
								<table cellspacing="0" cellpadding="0" border="0" summary="">
									<tr>
										<td style="width:  20px; text-align: left;  "></td>
										<td style="width: 382px; text-align: left;  "><a href="admin-user-profile.jsp?id=<%= person.getId() %>" title=""><%= person.getFullName() %></a></td>
										<td style="width:  90px; text-align: center;"><%= person.getEmployeeNumberString() %></td>
									</tr>
								</table>
							</div>
							<!-- END User -->
<%
    }
}
else
{
%>
							<!-- BEGIN User -->
							<div class="user">
								<table cellspacing="0" cellpadding="0" border="0" summary="">
									<tr>
										<td style="width: 492px; text-align: left;  " colspan="3">No Users Found</td>
									</tr>
								</table>
							</div>
							<!-- END User -->
<%
}
%>

							<div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>

						</div>

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