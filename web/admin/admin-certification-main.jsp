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

						<p class="headlineA">Certification Details</p>
						<p>Use this screen to add or update details for a certification.</p>
<p>If a certification is not appearing on the Certification Manager screen, check the Status,
Released, and Expires fields. If you make changes, click <strong>Update Certification.</strong></p>
						<div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>

						<struts-html:form action="/admin/certificationMain" focus="nameInput">

							<input id="dummy" name="dummy" type="hidden" />

							<div class="adminItem">
								<div class="leftTM">CERTIFICATION NAME</div>
								<div class="right">
									<input name="nameInput" onfocus="select();" value="<%= adminCertification.getLabel() %>" size="31" maxlength="100" class="inputbox" style="width: 206px;" />
								</div>
								<div class="end"></div>
							</div>
							<div class="adminItem">
								<div class="leftTM">DESCRIPTION</div>
								<div class="right">
									<textarea name="description" rows="5" cols="35" class="textarea" style="width: 304px;"><%= adminCertification.getDescriptionString() %></textarea>
								</div>
								<div class="end"></div>
							</div>
							<div class="adminItem">
								<div class="leftTM">CONTACT</div>
								<div class="right">
									<select name="contact" class="select" style="width: 309px;">
										<option value="0">-- SELECT A CONTACT --</option>
<%
PersonBean contact_person = null;
try
{
    contact_person = adminCertification.getContactPerson();
}
catch (Exception x)
{
}
try
{
    RoleBean role = RoleBean.getRole(RoleBean.CERTIFICATION_OWNER_ROLE_NAME);
    Iterator itr = PersonBean.getPersons(adminCompany, role).iterator();
    while (itr.hasNext())
    {
	PersonBean person = (PersonBean)itr.next();
%>
										<option value="<%= person.getValue() %>"<%= person.equals(contact_person) ? " selected" : "" %>><%= person.getLabel() %></option>
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

							<div class="adminItem">
								<div class="leftTM">STATUS</div>
								<div class="right">
									<label for="statusRadio2">Inactive</label>
									<input name="active" id="statusRadio2" type="radio" value="0" class="crirHiddenJS" onclick="" <%= adminCertification.isActive() ? "" : "checked=\"checked\"" %> />

									<label for="statusRadio3">Active</label>
									<input name="active" id="statusRadio3" type="radio" value="1" class="crirHiddenJS" onclick="" <%= adminCertification.isActive() ? "checked=\"checked\"" : "" %> />
								</div>
								<div class="end"></div>
							</div>
							<!--
							<div class="adminItem">
								<div class="leftTM">RELEASED</div>
								<div class="right">
									<input name="released" onfocus="select();" value="" size="7" maxlength="100" class="inputbox" style="width: 64px;" />
								</div>
								<div class="end"></div>
							</div>
							<div class="adminItem">
								<div class="leftTM"></div>
								<div class="right">
									<label for="makeActiveOn" style="margin-left: -3px;">Make Active On:&nbsp;</label>
									<input name="makeActiveOn" id="makeActiveOn" type="checkbox" value="" class="crirHiddenJS" checked="checked" />
									<input name="makeActiveOnDate" onfocus="select();" value="" size="7" maxlength="100" class="inputbox" style="width: 64px; margin-top: -1px;" />
								</div>
								<div class="end"></div>
							</div>
							<div class="adminItem">
								<div class="leftTM"></div>
								<div class="right">
									<label for="displayAsNewFor" style="margin-left: -3px;">Display as new for&nbsp;</label>
									<input name="displayAsNewFor" id="displayAsNewFor" type="checkbox" value="" class="crirHiddenJS" checked="checked" />
									<input name="displayAsNewForDays" onfocus="select();" value="" size="2" maxlength="4" class="inputbox" style="width: 34px; margin-top: -1px;" />
									&nbsp;days
								</div>
								<div class="end"></div>
							</div>
							<div class="adminItem">
								<div class="leftTM">EXPIRES</div>
								<div class="right">
									<input name="expires" onfocus="select();" value="" size="7" maxlength="100" class="inputbox" style="width: 64px;" />
								</div>
								<div class="end"></div>
							</div>

							<p>&nbsp;</p>

							<div class="adminItem">
								<div class="leftTM">UPDATE NOTES</div>
								<div class="right">
									<textarea name="updateNotes" rows="5" cols="35" class="textarea" style="width: 304px;"></textarea>
								</div>
								<div class="end"></div>
							</div>
							-->

							<p>&nbsp;</p>

							<p>
								<!-- <input class="formbutton" type="submit" value="Delete Certification" alt="Delete Certification" style="margin-right: 10px; "/> -->
								<input class="formbutton" type="submit" value="Update Certification" alt="Update Certification" />
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