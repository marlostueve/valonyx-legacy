<%@ page language="java" buffer="12kb" contentType="text/html" import="com.badiyan.uk.beans.*, java.util.*, org.apache.commons.beanutils.*, com.badiyan.uk.online.beans.*" %>

<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="struts-bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="struts-html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="struts-logic" %>

<jsp:useBean id="adminCompany" class="com.badiyan.uk.online.beans.UKOnlineCompanyBean" scope="session" />
<jsp:useBean id="adminCourseAudience" class="com.badiyan.uk.beans.CourseAudienceBean" scope="session" />
<jsp:useBean id="adminCourse" class="com.badiyan.uk.beans.CourseBean" scope="session" />
<jsp:useBean id="listHelper" class="com.badiyan.uk.online.beans.EcolabListHelperBean" scope="session" />
<jsp:useBean id="loginBean" class="com.badiyan.uk.online.beans.UKOnlineLoginBean" scope="session" />
<jsp:useBean id="courseSearch" class="com.badiyan.uk.online.beans.UKOnlineCourseSearchBean" scope="session"/>
<jsp:useBean id="resourceSearch" class="com.badiyan.uk.online.beans.UKOnlineResourceSearchBean" scope="session" />

<%@ include file="channels/channel-session.jsp" %>
<%@ include file="channels/channel-permissions.jsp" %>

<%
String deleteIdString = request.getParameter("deleteId");
if (deleteIdString != null)
{
    try
    {
	CourseAudienceBean.deleteCourseAudience(Integer.parseInt(deleteIdString), adminCourse.getId());
    }
    catch (Exception x)
    {
	x.printStackTrace();
    }
}

int selected_audience_id = 0;
String requirement_type_string = "Elective";
if (request.getParameter("audience_id") != null)
{
    try
    {
        selected_audience_id = Integer.parseInt(request.getParameter("audience_id"));
        AudienceBean audience_obj = AudienceBean.getAudience(selected_audience_id);
        CompanyBean company_obj = audience_obj.getCompany();

        // ensure that this person is an admin for this company

        if (company_obj.isAdministrator(loginBean.getPerson()))
        {
            adminCompany = company_obj;
            session.setAttribute("adminCompany", adminCompany);

            adminCourseAudience = CourseAudienceBean.getCourseAudience(audience_obj.getId(), adminCourse.getId());
            session.setAttribute("adminCourseAudience", adminCourseAudience);
        }
    }
    catch (Exception x)
    {
    }
}
else
{
    adminCourseAudience = new CourseAudienceBean();
    session.setAttribute("adminCourseAudience", adminCourseAudience);
}
if (!adminCourseAudience.getRequirementTypeString().equals(""))
    requirement_type_string = adminCourseAudience.getRequirementTypeString();
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

		<script TYPE="text/javascript">
		<!--

		function
		initForm()
		{
		    document.forms[0].audienceSelect.value = <%= selected_audience_id %>;
		    document.forms[0].courseIsSelect.value = '<%= requirement_type_string %>';
		    document.forms[0].completeByDateInput.value = '<%= adminCourseAudience.getCompleteByDateString() %>';
		}

		// -->
		</script>
		
<%@ include file="..\channels\channel-head-javascript.jsp" %>

	</head>

	<body onload="initForm();initErrors();">

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

						<p class="headlineA">Activity Details&nbsp;&#47;&nbsp;Audiences</p>
						<p class="currentObjectName"><%= adminCourse.getLabel() %></p>
						<p>Use this screen to add or update audience-specific requirements for an activity. Select an
audience and then click Elective, Recommended, or Required. Required activities appear
on users' home pages. Elective and recommended activities appear in users' Activity
Catalog. Users who are not members of this audience do not have access to this activity.</p>
						<div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>

						<struts-html:form action="/admin/courseAudience">

							<input id="dummy" name="dummy" type="hidden" />

							<div class="adminItem">
								<div class="leftTM">AUDIENCE</div>
								<div class="right">
                                                                        <struts-html:select property="audienceSelect" style="width: 309px;" >
                                                                            <struts-html:option value="0">-- SELECT AN AUDIENCE --</struts-html:option>
                                                                            <struts-html:optionsCollection name="listHelper" property="audiences" />
                                                                        </struts-html:select>
								</div>
								<div class="end"></div>
							</div>
							<div class="adminItem">
								<div class="leftTM">ENROLLMENT</div>
								<div class="right">
                                                                        <struts-html:select property="courseIsSelect" style="width: 309px;" >
                                                                            <struts-html:option value="Elective">Elective</struts-html:option>
                                                                            <struts-html:option value="Recommended">Recommended</struts-html:option>
                                                                            <struts-html:option value="Required">Required</struts-html:option>
                                                                        </struts-html:select>
								</div>
								<div class="end"></div>
							</div>
							<div class="adminItem">
								<div class="leftTM">START IN</div>
								<div class="right">
									<input name="completeByDateInput" onfocus="getDate('courseAudienceForm','completeByDateInput');select();" value="" size="7" maxlength="100" class="inputbox" style="width: 64px;" />
								</div>
								<div class="end"></div>
							</div>
							<div class="adminItem">
								<div class="leftTM"></div>
								<div class="right">
									<input class="formbutton" type="submit" value="<%= (adminCourseAudience.isNew()) ? "Add" : "Save" %> Audience" alt="<%= (adminCourseAudience.isNew()) ? "Add" : "Save" %> Audience" />
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
    // get the audiences assigned to this course

    //Iterator audiences = AudienceBean.getAudiences(adminCompany).iterator();
    Iterator audiences = CourseAudienceBean.getCourseAudiences(adminCourse).iterator();
    if (audiences.hasNext())
    {
	while (audiences.hasNext())
	{
	    CourseAudienceBean audience_obj = (CourseAudienceBean)audiences.next();
	    
%>
							<!-- BEGIN Audience -->
							<div class="audience">
								<table cellspacing="0" cellpadding="0" border="0" summary="">
									<tr>
										<td style="width:  20px; text-align: left;  "></td>
										<td style="width: 402px; text-align: left;  "><a href="admin-course-audience.jsp?audience_id=<%= audience_obj.getValue() %>" title=""><%= audience_obj.getLabel() %></a></td>
										<td style="width:  70px; text-align: center;  "><a href="javascript:if (confirm('Are you sure?')) location = 'admin-course-audience.jsp?id=<%= adminCourse.getId() %>&deleteId=<%= audience_obj.getAudience().getId() %>'" title="Delete">Delete</a></td>
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
										<td style="width:  492px; text-align: left;  ">No Audiences Assigned</td>
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