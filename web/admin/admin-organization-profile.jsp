<%@ page language="java" buffer="12kb" contentType="text/html" import="com.badiyan.uk.beans.*, java.util.*, org.apache.commons.beanutils.*, com.badiyan.uk.online.beans.*" %>

<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="struts-bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="struts-html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="struts-logic" %>

<jsp:useBean id="adminCompany" class="com.badiyan.uk.online.beans.UKOnlineCompanyBean" scope="session" />
<jsp:useBean id="listHelper" class="com.badiyan.uk.online.beans.EcolabListHelperBean" scope="session" />
<jsp:useBean id="loginBean" class="com.badiyan.uk.online.beans.UKOnlineLoginBean" scope="session" />
<jsp:useBean id="adminCourse" class="com.badiyan.uk.beans.CourseBean" scope="session" />
<jsp:useBean id="courseSearch" class="com.badiyan.uk.online.beans.UKOnlineCourseSearchBean" scope="session"/>
<jsp:useBean id="resourceSearch" class="com.badiyan.uk.online.beans.UKOnlineResourceSearchBean" scope="session" />

<%@ include file="channels/channel-session.jsp" %>
<%@ include file="channels/channel-permissions.jsp" %>

<%

if (request.getParameter("id") != null)
{
    try
    {
	int companyId = Integer.parseInt(request.getParameter("id"));
	CompanyBean company_obj = CompanyBean.getCompany(companyId);

	// ensure that this person is an admin for this company
	if (company_obj.isAdministrator(loginBean.getPerson()))
	{
	    adminCompany = company_obj;
	    session.setAttribute("adminCompany", adminCompany);
	    loginBean.getPerson().selectCompany(adminCompany);
	}
    }
    catch (Exception x)
    {
	x.printStackTrace();
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

		<script type="text/javascript" src="scripts/crir.js"></script>
		<!--[if lte IE 7]><script type="text/javascript" src="scripts/optionDisabledSupport.js"></script><![endif]-->
		
<%@ include file="..\channels\channel-head-javascript.jsp" %>

	</head>

	<body onload="javascript:initErrors();">

		<div id="main">
		    
<%@ include file="..\channels\channel-menu.jsp" %>

			<div class="horizontalRule"><img src="images/trspacer.gif" width="1" height="1" alt="" /></div>

			<!-- *** BEGIN Content *** -->
			<div id="content">

				<div class="content_TextBlock">
					<p class="headline"><%= adminCompany.getLabel() %> Administration</p>
					<div class="horizontalRule"><img src="images/trspacer.gif" width="1" height="1" alt="" /></div>
				</div>

				<div class="content_Administration">

<%@ include file="channels\channel-admin-menu.jsp" %>

					<div class="main">

						<p class="headlineA">Organization Profile</p>
						<p>Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet dolore magna aliquam erat volutpat.</p>
						<div class="horizontalRule"><img src="images/trspacer.gif" width="1" height="1" alt="" /></div>

						<struts-html:form action="/admin/organizationMain" focus="nameInput">

							<input id="dummy" name="dummy" type="hidden" />

							<div class="adminItem">
								<div class="leftTM">ORGANIZATION NAME</div>
								<div class="right">
									<input name="nameInput" onfocus="select();" value="<%= adminCompany.getLabel() %>" size="31" maxlength="100" class="inputbox" style="width: 206px;" />
								</div>
								<div class="end"></div>
							</div>

							<p>&nbsp;</p>

							<p>
								<input class="formbutton" type="submit" name="submit_button" value="Update Organization" alt="Update Organization" style="margin-right: 10px; "/>
								<input class="formbutton" type="submit" name="submit_button" value="Delete Organization" alt="Delete Organization" onclick="javascript:return confirm('Are you sure?');" />
								
							</p>

						</struts-html:form>

					</div>

					<div class="end"></div>
				</div>

				<div class="content_TextBlock">
					<div class="horizontalRule"><img src="images/trspacer.gif" width="1" height="1" alt="" /></div>
				</div>

			</div>
			<!-- *** END Content *** -->

			<div class="horizontalRule"><img src="images/trspacer.gif" width="1" height="1" alt="" /></div>

			<!-- *** BEGIN Footer *** -->
			<div id="footer">
				<div id="footer_Copyright"><p>&copy;2007 Ecolab Inc. All rights reserved.</p></div>
			</div>
			<!-- *** END Footer *** -->

		</div>

	</body>

</html>