<%@ page language="java" buffer="12kb" contentType="text/html" import="com.badiyan.uk.beans.*,java.util.*,org.apache.commons.beanutils.*,com.badiyan.uk.online.beans.*,com.badiyan.uk.online.util.*" %>
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
else if (request.getParameter("new") != null)
{
    if (request.getParameter("new").equals("true"))
    {
        adminPerson = new UKOnlinePersonBean();
        session.setAttribute("adminPerson", adminPerson);
    }
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
		<struts-html:javascript formName="userProfileEditForm" dynamicJavascript="true" staticJavascript="false"/>
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

						<p class="headlineA">Add New User</p>
						<p>Use this screen to add information for a user who is new to the LMS.</p>
<p>Before you can assign activities or certifications to this user, you must click <strong>Add New User</strong>.</p>
<p>If the User Group, Department, or Job Title which you want to assign to this user does not
yet exist, you must go to Organization Manager to create the appropriate category. It will
then be available on the drop-down list on this screen.</p>
						<div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>

						<struts-html:form action="/admin/userProfileNew" focus="first_name" onsubmit="return validateUserProfileEditForm(this);">

							<input id="dummy" name="dummy" type="hidden" />

							<div class="adminItem">
								<div class="leftTM">FIRST NAME</div>
								<div class="right">
									<input name="first_name" onfocus="select();" value="<%= adminPerson.getFirstNameString() %>" size="31" maxlength="100" class="inputbox" style="width: 206px;" />
								</div>
								<div class="end"></div>
							</div>
							<div class="adminItem">
								<div class="leftTM">LAST NAME</div>
								<div class="right">
									<input name="last_name" onfocus="select();" value="<%= adminPerson.getLastNameString() %>" size="31" maxlength="100" class="inputbox" style="width: 206px;" />
								</div>
								<div class="end"></div>
							</div>
							<div class="adminItem">
								<div class="leftTM">CUSTOMER #</div>
								<div class="right">
									<input name="employee_number" onfocus="select();" value="<%= adminPerson.getEmployeeNumberString() %>" size="31" maxlength="100" class="inputbox" style="width: 206px;" />
								</div>
								<div class="end"></div>
							</div>
							<div class="adminItem">
								<div class="leftTM">EMAIL/USERNAME</div>
								<div class="right">
									<input name="email" onfocus="select();" value="<%= adminPerson.getEmailString() %>" size="31" maxlength="100" class="inputbox" style="width: 206px; margin-right: 4px;" />
									<img src="../images/trspacer.gif" width="21" height="21" alt="" style="_margin-top: 1px;" />
								</div>
								<div class="end"></div>
							</div>
							<div class="adminItem">
								<div class="leftTM">PASSWORD</div>
								<div class="right">
									<input name="password" onfocus="select();" value="<%= adminPerson.isNew() ? (PasswordGenerator.getPassword(3) + "-" + PasswordGenerator.getPassword(3)) : adminPerson.getPassword() %>" size="31" maxlength="100" class="inputbox" style="width: 206px;" />
								</div>
								<div class="end"></div>
							</div>

							<p><input class="formbutton" type="submit" value="Add New User" alt="Add New User" /></p>

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