<%@ page language="java" buffer="12kb" contentType="text/html" import="com.badiyan.uk.beans.*, java.util.*, org.apache.commons.beanutils.*, com.badiyan.uk.online.beans.*" %>

<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="struts-bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="struts-html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="struts-logic" %>

<jsp:useBean id="adminCompany" class="com.badiyan.uk.online.beans.UKOnlineCompanyBean" scope="session" />
<jsp:useBean id="adminAudience" class="com.badiyan.uk.beans.AudienceBean" scope="session" />
<jsp:useBean id="listHelper" class="com.badiyan.uk.online.beans.EcolabListHelperBean" scope="session" />
<jsp:useBean id="loginBean" class="com.badiyan.uk.online.beans.UKOnlineLoginBean" scope="session" />
<jsp:useBean id="adminCourse" class="com.badiyan.uk.beans.CourseBean" scope="session" />
<jsp:useBean id="courseSearch" class="com.badiyan.uk.online.beans.UKOnlineCourseSearchBean" scope="session"/>
<jsp:useBean id="resourceSearch" class="com.badiyan.uk.online.beans.UKOnlineResourceSearchBean" scope="session" />

<%@ include file="channels/channel-session.jsp" %>
<%@ include file="channels/channel-permissions.jsp" %>

<%
String stepString = "Step 7: Audiences";

if (request.getParameter("id") != null)
{
    try
    {
        AudienceBean audience_obj = AudienceBean.getAudience(Integer.parseInt(request.getParameter("id")));
        CompanyBean company_obj = audience_obj.getCompany();

        // ensure that this person is an admin for this company
        
        if (company_obj.isAdministrator(loginBean.getPerson()))
        {
            adminCompany = company_obj;
            session.setAttribute("adminCompany", adminCompany);
            adminAudience = audience_obj;
            session.setAttribute("adminAudience", adminAudience);
        }
    }
    catch (Exception x)
    {
    }
}
else
{
    if ((request.getParameter("new") != null) && (request.getParameter("new").equals("true")))
    {
	adminAudience = new AudienceBean();
	session.setAttribute("adminAudience", adminAudience);
    }
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

						<p class="headlineA">Organization Profile&nbsp;&#47;&nbsp;Audiences</p>
						<p class="currentObjectName"><%= adminCompany.getLabel() %></p>
						<p>Use this screen to add a new audience, delete an audience, or update the departments,
job titles, or user groups associated with it.</p>
<p>Examples of audiences: Dept 1 and Job Title 3, Depts 3 & 4 and Job Title 1</p>
						<div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>

						<struts-html:form action="/admin/organizationAudiences">

							<input id="dummy" name="dummy" type="hidden" />

							<div class="adminItem">
								<div class="leftTM">AUDIENCE</div>
								<div class="right">
									<input name="nameInput" onfocus="select();" value="<%= adminAudience.getLabel() %>" size="31" maxlength="100" class="inputbox" style="width: 206px; margin-right: 4px;" />
								</div>
								<div class="end"></div>
							</div>
							<div class="adminItem">
								<div class="leftTM">DEPARTMENTS</div>
								<div class="right">
									<select name="departmentSelect" class="multipleSelect" size="5" multiple="multiple" style="width: 309px;">
										<option value="0"<%= adminAudience.isAssociatedWithAllDepartments() ? " selected" : "" %>>-- ALL DEPARTMENTS --</option>
<%
try
{
    Iterator departments = DepartmentBean.getDepartments(adminCompany).iterator();
    while (departments.hasNext())
    {
        DepartmentBean obj = (DepartmentBean)departments.next();
%>
										<option value="<%= obj.getValue() %>"<%= (!adminAudience.isAssociatedWithAllDepartments() && adminAudience.contains(obj)) ? " selected" : "" %>><%= obj.getLabel() %></option>
<%
    }
}
catch (Exception x)
{
    x.printStackTrace();
}
%>
									</select>
								</div>
								<div class="end"></div>
							</div>
							<div class="adminItem">
								<div class="leftTM">JOB TITLES</div>
								<div class="right">
									<select name="jobTitleSelect" class="multipleSelect" size="5" multiple="multiple" style="width: 309px;">
										<option value="0"<%= adminAudience.isAssociatedWithAllPersonTitles() ? " selected" : "" %>>-- ALL TITLES --</option>
<%
try
{
    Iterator titles = PersonTitleBean.getPersonTitles(adminCompany).iterator();
    while (titles.hasNext())
    {
        PersonTitleBean obj = (PersonTitleBean)titles.next();
%>
										<option value="<%= obj.getValue() %>"<%= (!adminAudience.isAssociatedWithAllPersonTitles() && adminAudience.contains(obj)) ? " SELECTED" : "" %>><%= obj.getLabel() %></option>
<%
    }
}
catch (Exception x)
{
    x.printStackTrace();
}
%>
									</select>
								</div>
								<div class="end"></div>
							</div>
							<div class="adminItem">
								<div class="leftTM">USER GROUPS</div>
								<div class="right">
									<select name="userGroupSelect" class="multipleSelect" size="5" multiple="multiple" style="width: 309px;">
										<option value="0"<%= adminAudience.isAssociatedWithAllPersonGroups() ? " selected" : "" %>>-- ALL GROUPS --</option>
<%
try
{
    Iterator groups = PersonGroupBean.getPersonGroups(adminCompany).iterator();
    while (groups.hasNext())
    {
        PersonGroupBean obj = (PersonGroupBean)groups.next();
%>
										<option value="<%= obj.getValue() %>"<%= (!adminAudience.isAssociatedWithAllPersonGroups() && adminAudience.contains(obj)) ? " SELECTED" : "" %>><%= obj.getLabel() %></option>
<%
    }
}
catch (Exception x)
{
    x.printStackTrace();
}
%>
									</select>
								</div>
								<div class="end"></div>
							</div>
							<div class="adminItem">
								<div class="leftTM"></div>
								<div class="right">
									<input class="formbutton" type="submit" value="Add&nbsp;&#47;&nbsp;Update" alt="Add&nbsp;&#47;&nbsp;Update" style="margin-right: 4px;"/><input type="hidden" name="delete_id" value="0" />
								</div>
								<div class="end"></div>
							</div>

						</struts-html:form>

						<div class="content_AdministrationTable">

							<div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>
							<div class="heading">
								<table cellspacing="0" cellpadding="0" border="0" summary="">
									<tr style="display: block;">
										<td style="width:  20px; text-align: left;  "></td>
										<td style="width: 402px; text-align: left;  ">Audience</td>
										<td style="width:  70px; text-align: center;  "></td>
									</tr>
								</table>
							</div>
							<div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>
<%
try
{
    Iterator audiences = AudienceBean.getAudiences(adminCompany).iterator();
    if (audiences.hasNext())
    {
	while (audiences.hasNext())
	{
	    AudienceBean audience_obj = (AudienceBean)audiences.next();
%>
							<!-- BEGIN Audience -->
							<div class="audience">
								<table cellspacing="0" cellpadding="0" border="0" summary="">
									<tr>
										<td style="width:  20px; text-align: left;  "></td>
										<td style="width: 402px; text-align: left;  "><a href="admin-organization-audiences.jsp?id=<%= audience_obj.getId() %>" title=""><%= audience_obj.getLabel() %></a></td>
										<td style="width:  70px; text-align: center;  "><a href="javascript:if (confirm('Are you sure?')) {document.forms[0].delete_id.value=<%= audience_obj.getId() %>;document.forms[0].submit();}" title="Delete">Delete</a></td>
									</tr>
								</table>
							</div>
							<!-- END Audience -->
<%
	}
    }
    else
    {
%>
							<!-- BEGIN Audience -->
							<div class="audience">
								<table cellspacing="0" cellpadding="0" border="0" summary="">
									<tr>
										<td style="width: 492px; text-align: left;  " colspan="3">No Audiences Found</td>
									</tr>
								</table>
							</div>
							<!-- END Audience -->
<%
    }
}
catch (Exception x)
{
    x.printStackTrace();
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