<%@ page language="java" buffer="12kb" contentType="text/html" import="com.badiyan.uk.beans.*,java.util.*,org.apache.commons.beanutils.*,com.badiyan.uk.online.beans.*,com.badiyan.torque.*" %>
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

boolean can_modify = loginBean.getPerson().hasRole(RoleBean.SYSTEM_ADMINISTRATOR_ROLE_NAME) || loginBean.getPerson().hasRole(RoleBean.COMPANY_ADMINISTRATOR_ROLE_NAME) || loginBean.getPerson().hasRole(RoleBean.TRAINING_ADMINISTRATOR_ROLE_NAME);

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

						<p class="headlineA">User Profile&nbsp;&#47;&nbsp;Certifications</p>
						<p class="currentObjectName"><%= adminPerson.getLabel() %></p>
						<p>Use this screen to update user certification information. Entering dates next to a particular
certification results in that certification appearing on this user's home page.</p>

						<struts-html:form action="/admin/userCertificationAssign">

							<input id="dummy" name="dummy" type="hidden" />

							<div class="content_AdministrationTable">

								<div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>
								<div class="heading">
									<table cellspacing="0" cellpadding="0" border="0" summary="">
										<tr style="display: block;">
											<td style="width:  20px; text-align: left;  "></td>
											<td style="width: 292px; text-align: left;  ">Title</td>
											<td style="width:  90px; text-align: center;">Received</td>
											<td style="width:  90px; text-align: center;">Expires</td>
										</tr>
									</table>
								</div>
								<div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>
<%
   
HashMap start_hash = new HashMap(11);
HashMap end_hash = new HashMap(11);
Iterator cert_itr = EcolabCertificationBean.getActivePersonCertifications(adminPerson).iterator();
if (cert_itr.hasNext())
{
    while (cert_itr.hasNext())
    {
	PersonCertification per_cert_obj = (PersonCertification)cert_itr.next();
	EcolabCertificationBean cert_obj = EcolabCertificationBean.getCertification(per_cert_obj.getCertificationId());
	String start_date_string = "";
	try
	{
	    start_date_string = CUBean.getUserDateString(per_cert_obj.getStartdate());
	}
	catch (Exception x)
	{
	}
	String end_date_string = "";
	try
	{
	    end_date_string = CUBean.getUserDateString(per_cert_obj.getEnddate());
	}
	catch (Exception x)
	{
	}
	
	start_hash.put(cert_obj, start_date_string);
	end_hash.put(cert_obj, end_date_string);
    }
}
   
   
   
   
   
Vector active_certifications = EcolabCertificationBean.getActiveCertifications(adminPerson);


cert_itr = EcolabCertificationBean.getCertifications().iterator();
while (cert_itr.hasNext())
{
    EcolabCertificationBean certification = (EcolabCertificationBean)cert_itr.next();
    boolean active = active_certifications.contains(certification);
    String start_date_string = "";
    if (start_hash.get(certification) != null)
	start_date_string = (String)start_hash.get(certification);
    String end_date_string = "";
    if (end_hash.get(certification) != null)
	end_date_string = (String)end_hash.get(certification);
%>
								<!-- BEGIN Certification -->
								<div class="certification">
									<table cellspacing="0" cellpadding="0" border="0" summary="">
										<tr>
											<td style="width:  20px; text-align: left;  "><input type="hidden" name="cert_id" value="<%= certification.getId() %>"></td>
<%
    if (loginBean.getPerson().hasRole(RoleBean.SYSTEM_ADMINISTRATOR_ROLE_NAME) || loginBean.getPerson().hasRole(RoleBean.COMPANY_ADMINISTRATOR_ROLE_NAME))
    {
%>
											<td style="width: 292px; text-align: left;  "><a href="admin-certification-main.jsp?id=<%= certification.getValue() %>"><%= certification.getLabel() %></a><%= active ? " *" : "" %></td>
<%
    }
    else
    {
%>
											<td style="width: 292px; text-align: left;  "><%= certification.getLabel() %><%= active ? " *" : "" %></td>
<%
    }
%>
											<td style="width:  90px; text-align: center;"><input name="received_<%= certification.getId() %>" value="<%= start_date_string %>" size="7" maxlength="100" class="inputbox" style="width: 64px;" onfocus="getDate('userCertificationAssignForm','received_<%= certification.getId() %>');select();"<%= can_modify ? "" : " disabled=\"true\"" %> /></td>
											<td style="width:  90px; text-align: center;"><input name="expires_<%= certification.getId() %>" value="<%= end_date_string %>" size="7" maxlength="100" class="inputbox" style="width: 64px;" onfocus="getDate('userCertificationAssignForm','expires_<%= certification.getId() %>');select();"<%= can_modify ? "" : " disabled=\"true\"" %> /></td>
										</tr>
									</table>
								</div>
								<!-- END Certification -->
<%
}
%>

								<div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>
							</div>

<%
if (can_modify)
{
%>
							<p style="margin-top: 20px; ">
								<input class="formbutton" type="submit" value="Update Certifications" alt="Update Certifications" />
							</p>
<%
}
else
{
%>
							<p style="margin-top: 20px; ">
								You don't have permission to modify certification assignments.
							</p>
<%
}
%>

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