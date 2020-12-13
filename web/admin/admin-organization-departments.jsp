<%@ page language="java" buffer="12kb" contentType="text/html" import="com.badiyan.uk.beans.*, java.util.*, org.apache.commons.beanutils.*, com.badiyan.uk.online.beans.*" %>

<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="struts-bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="struts-html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="struts-logic" %>

<jsp:useBean id="adminCompany" class="com.badiyan.uk.online.beans.UKOnlineCompanyBean" scope="session" />
<jsp:useBean id="adminDepartment" class="com.badiyan.uk.beans.DepartmentBean" scope="session" />
<jsp:useBean id="adminDepartmentType" class="com.badiyan.uk.beans.DepartmentTypeBean" scope="session" />
<jsp:useBean id="listHelper" class="com.badiyan.uk.online.beans.EcolabListHelperBean" scope="session" />
<jsp:useBean id="loginBean" class="com.badiyan.uk.online.beans.UKOnlineLoginBean" scope="session" />
<jsp:useBean id="adminCourse" class="com.badiyan.uk.beans.CourseBean" scope="session" />
<jsp:useBean id="courseSearch" class="com.badiyan.uk.online.beans.UKOnlineCourseSearchBean" scope="session"/>
<jsp:useBean id="resourceSearch" class="com.badiyan.uk.online.beans.UKOnlineResourceSearchBean" scope="session" />

<%@ include file="channels/channel-session.jsp" %>
<%@ include file="channels/channel-permissions.jsp" %>

<%
boolean new_department = false;
if (request.getParameter("id") != null)
{
    try
    {
        String dept_id_string = request.getParameter("id");
	adminDepartment = DepartmentBean.getDepartment(Integer.parseInt(dept_id_string));
	session.setAttribute("adminDepartment", adminDepartment);
    }
    catch (Exception x)
    {
	x.printStackTrace();
	new_department = true;
    }
}
else
    new_department = true;

if (new_department)
{
    if (!adminDepartment.isNew())
    {
	adminDepartment = new DepartmentBean();
	session.setAttribute("adminDepartment", adminDepartment);
    }
}

int parent_id = 0;
try
{
    if (!adminDepartment.isNew())
	parent_id = adminDepartment.getParentId();
}
catch (Exception x)
{
}

String department_type_string = "";
try
{
    if (!adminDepartment.isNew())
	department_type_string = adminDepartment.getTypeString();
}
catch (Exception x)
{
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

						<p class="headlineA">Organization Profile&nbsp;&#47;&nbsp;Departments</p>
						<p class="currentObjectName"><%= adminCompany.getLabel() %></p>
						<p>Use this screen to add a new department or update the name, parent, or department type
of an existing one.</p>
						<div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>

						<struts-html:form action="/admin/organizationDepartments">

							<input id="dummy" name="dummy" type="hidden" />

							<div class="adminItem">
								<div class="leftTM">DEPARTMENT</div>
								<div class="right">
									<input name="nameInput" onfocus="select();" value="<%= adminDepartment.getLabel() %>" size="31" maxlength="100" class="inputbox" style="width: 206px; margin-right: 4px;" />
								</div>
								<div class="end"></div>
							</div>
							<div class="adminItem">
								<div class="leftTM">PARENT</div>
								<div class="right">
									<select name="parent" class="select" style="width: 309px;">
										<option value="0">NO PARENT (ROOT)</option>
<%
try
{
    Iterator departments = DepartmentBean.getDepartments(adminCompany).iterator();
    while (departments.hasNext())
    {
        DepartmentBean department_obj = (DepartmentBean)departments.next();
%>
										<option value="<%= department_obj.getValue() %>"<%= (department_obj.getId() == parent_id) ? " selected" : "" %>><%= department_obj.getLabel() %></option>
<%
    }
}
catch (Exception x)
{
}
%>
									</select>
								</div>
								<div class="end"></div>
							</div>
							<div class="adminItem">
								<div class="leftTM">DEPARTMENT TYPE</div>
								<div class="right">
									<select name="type" class="select" style="width: 309px; margin-right: 4px;">
<%
try
{
    Iterator types = DepartmentTypeBean.getDepartmentTypes(adminCompany).iterator();
    while (types.hasNext())
    {
        DepartmentTypeBean obj = (DepartmentTypeBean)types.next();
%>
										<option value="<%= obj.getValue() %>"<%= obj.getLabel().equals(department_type_string) ? " selected" : "" %>><%= obj.getLabel() %></option>
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
										<td style="width: 402px; text-align: left;  ">Department&nbsp;:&nbsp;Department Type</td>
										<td style="width:  70px; text-align: center;  "></td>
									</tr>
								</table>
							</div>
							<div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>
<%
try
{
    Iterator departments = DepartmentBean.getDepartments(adminCompany).iterator();
    if (departments.hasNext())
    {
	while (departments.hasNext())
	{
	    DepartmentBean department_obj = (DepartmentBean)departments.next();
%>
							<!-- BEGIN Department -->
							<div class="department">
								<table cellspacing="0" cellpadding="0" border="0" summary="">
									<tr>
										<td style="width:  20px; text-align: left;  "></td>
										<td style="width: 402px; text-align: left;  "><a href="admin-organization-departments.jsp?id=<%= department_obj.getId() %>" title=""><%= department_obj.getLabel() %>&nbsp;:&nbsp;<%= department_obj.getTypeString() %></a></td>
										<td style="width:  70px; text-align: center;  "><a href="javascript:if (confirm('Are you sure?')) {document.forms[0].delete_id.value=<%= department_obj.getId() %>;document.forms[0].submit();}" title="Delete">Delete</a></td>
									</tr>
								</table>
							</div>
							<!-- END Department -->
<%
	}
    }
    else
    {
%>
							<!-- BEGIN Department -->
							<div class="department">
								<table cellspacing="0" cellpadding="0" border="0" summary="">
									<tr>
										<td style="width: 492px; text-align: left;  " colspan="3">No Departments Found</td>
									</tr>
								</table>
							</div>
							<!-- END Department -->
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