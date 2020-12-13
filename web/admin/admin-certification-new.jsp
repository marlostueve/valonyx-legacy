<%@ page language="java" buffer="12kb" contentType="text/html" import="com.badiyan.uk.beans.*, java.util.*, org.apache.commons.beanutils.*, com.badiyan.uk.online.beans.*" %>

<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="struts-bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="struts-html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="struts-logic" %>

<jsp:useBean id="adminCompany" class="com.badiyan.uk.online.beans.UKOnlineCompanyBean" scope="session" />
<jsp:useBean id="adminCertification" class="com.badiyan.uk.online.beans.EcolabCertificationBean" scope="session" />
<jsp:useBean id="listHelper" class="com.badiyan.uk.online.beans.EcolabListHelperBean" scope="session" />
<jsp:useBean id="loginBean" class="com.badiyan.uk.online.beans.UKOnlineLoginBean" scope="session" />

<jsp:useBean id="courseSearch" class="com.badiyan.uk.online.beans.UKOnlineCourseSearchBean" scope="session"/>
<jsp:useBean id="resourceSearch" class="com.badiyan.uk.online.beans.UKOnlineResourceSearchBean" scope="session" />
<jsp:useBean id="adminCourse" class="com.badiyan.uk.beans.CourseBean" scope="session" />

<%@ include file="channels/channel-session.jsp" %>
<%@ include file="channels/channel-permissions.jsp" %>

<%
if (request.getParameter("new") != null)
{
    if (request.getParameter("new").equals("true"))
    {
        adminCertification = new EcolabCertificationBean();
    }
}
else
{
    if (request.getParameter("id") != null)
    {
        try
        {
            int certificationId = Integer.parseInt(request.getParameter("id"));
            adminCertification = EcolabCertificationBean.getCertification(certificationId);
        }
        catch (Exception x)
        {
            x.printStackTrace();
        }
    }
}

session.setAttribute("adminCertification", adminCertification);


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

						<p class="headlineA">Add New Certification</p>
						<p>Use this screen to add a new certification. Enter the certification name, enter a
description and requirements, and then select a contact. When you have entered all the
information, click <strong>Add New Certification</strong> to go to the Certification Details screen. You will
then be able to add other information about this certification.</p>
<p><strong>Note:</strong> In the Description field, you should describe <strong>exactly</strong> what is required to achieve this
certification. This will ensure that all employees holding this certification have completed
the same requirements.</p>
						<div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>

						<struts-html:form action="/admin/certificationNew" focus="nameInput">

							<input id="dummy" name="dummy" type="hidden" />

							<div class="adminItem">
								<div class="leftTM">CERTIFICATION NAME</div>
								<div class="right">
									<input name="nameInput" onfocus="select();" value="" size="31" maxlength="100" class="inputbox" style="width: 206px;" />
								</div>
								<div class="end"></div>
							</div>
							<div class="adminItem">
								<div class="leftTM">DESCRIPTION</div>
								<div class="right">
									<textarea name="description" rows="5" cols="35" class="textarea" style="width: 304px;"></textarea>
								</div>
								<div class="end"></div>
							</div>
							<div class="adminItem">
								<div class="leftTM">CONTACT</div>
								<div class="right">
									<select name="contact" class="select" style="width: 309px;">
										<option value="0">-- SELECT A CONTACT --</option>
<%
try
{
    RoleBean role = RoleBean.getRole(RoleBean.CERTIFICATION_OWNER_ROLE_NAME);
    Iterator itr = PersonBean.getPersons(adminCompany, role).iterator();
    while (itr.hasNext())
    {
	PersonBean person = (PersonBean)itr.next();
%>
										<option value="<%= person.getValue() %>"><%= person.getLabel() %></option>
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

							<p>&nbsp;</p>

							<p><input class="formbutton" type="submit" value="Add New Certification" alt="Add New Certification" /></p>

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