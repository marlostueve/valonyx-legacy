<%@ page language="java" buffer="12kb" contentType="text/html" import="com.badiyan.uk.beans.*, java.util.*, org.apache.commons.beanutils.*, com.badiyan.uk.online.beans.*" %>

<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="struts-bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="struts-html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="struts-logic" %>

<jsp:useBean id="adminCompany" class="com.badiyan.uk.online.beans.UKOnlineCompanyBean" scope="session" />
<jsp:useBean id="resourceSearch" class="com.badiyan.uk.online.beans.UKOnlineResourceSearchBean" scope="session" />
<jsp:useBean id="listHelper" class="com.badiyan.uk.online.beans.EcolabListHelperBean" scope="session" />
<jsp:useBean id="loginBean" class="com.badiyan.uk.online.beans.UKOnlineLoginBean" scope="session" />

<jsp:useBean id="courseSearch" class="com.badiyan.uk.online.beans.UKOnlineCourseSearchBean" scope="session"/>
<jsp:useBean id="adminCourse" class="com.badiyan.uk.beans.CourseBean" scope="session" />

<%@ include file="channels/channel-session.jsp" %>
<%@ include file="channels/channel-permissions.jsp" %>

<%
if (request.getParameter("page") != null)
{
    if (request.getParameter("page").equals("next"))
        resourceSearch.nextPage();
    else if (request.getParameter("page").equals("previous"))
        resourceSearch.previousPage();
}
String deleteIdString = request.getParameter("deleteId");
if (deleteIdString != null)
{
    ResourceBean.delete(deleteIdString);
    resourceSearch.invalidateSearchResults();
}

listHelper.setCompany(adminCompany);
resourceSearch.setCompany(adminCompany);
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

						<p class="headlineA">Resource Manager</p>
						<p>Use this screen to select a resource to update or delete. You may find a resource by using
the Search check boxes and field below the menu. When you click a resource in the list,
you will go to the Resource Details screen for that resource.</p>

						<div class="content_AdministrationTable">

							<div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>
							<div class="heading">
								<table cellspacing="0" cellpadding="0" border="0" summary="">
									<tr style="display: block;">
										<td style="width:  20px; text-align: left;  "></td>
										<td style="width: 332px; text-align: left;  ">Resource</td>
										<td style="width:  50px; text-align: center;">Type</td>
										<td style="width:  90px; text-align: center;">Released</td>
									</tr>
								</table>
							</div>
							<div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>
<%
boolean resultsFound = false;
Enumeration resources = resourceSearch.getList();
ResourceBean resource = null;
if (resources.hasMoreElements())
{
    resultsFound = true;
    while (resources.hasMoreElements())
    {
        resource = (ResourceBean)resources.nextElement();
	String resource_type = resource.getType();
	String icon_string = "";
	if (resource_type.equals(ResourceBean.WORD_RESOURCE_TYPE))
	    icon_string = "<img src=\"../images/icnResourceDOC.gif\" width=\"17\" height=\"17\" title=\"Microsoft Word (DOC)\" alt=\"Microsoft Word (DOC)\" />";
	else if (resource_type.equals(ResourceBean.ACROBAT_RESOURCE_TYPE))
	    icon_string = "<img src=\"../images/icnResourcePDF.gif\" width=\"17\" height=\"17\" title=\"Portable Document Format (PDF)\" alt=\"Portable Document Format (PDF)\" />";
	else if (resource_type.equals(ResourceBean.FLASH_RESOURCE_TYPE))
	    icon_string = "<img src=\"../images/icnResourceSWF.gif\" width=\"17\" height=\"17\" title=\"Flash (SWF)\" alt=\"Flash (SWF)\" />";
	else if (resource_type.equals(ResourceBean.INTERNET_RESOURCE_TYPE))
	    icon_string = "<img src=\"../images/icnResourceWEB.gif\" width=\"17\" height=\"17\" title=\"World Wide Web\" alt=\"World Wide Web\" />";
	else if (resource_type.equals(ResourceBean.POWERPOINT_RESOURCE_TYPE))
	    icon_string = "<img src=\"../images/icnResourcePPT.gif\" width=\"17\" height=\"17\" title=\"Microsoft Powerpoint (PPT)\" alt=\"Microsoft Powerpoint (PPT)\" />";
	else if (resource_type.equals(ResourceBean.VIDEO_RESOURCE_TYPE))
	    icon_string = "<img src=\"../images/icnResourceVID.gif\" width=\"17\" height=\"17\" title=\"Video\" alt=\"Video\" />";
	else if (resource_type.equals(ResourceBean.XLS_RESOURCE_TYPE))
	    icon_string = "<img src=\"../images/icnResourceXLS.gif\" width=\"17\" height=\"17\" title=\"Microsoft Excel (XLS)\" alt=\"Microsoft Excel (XLS)\" />";
	else if (resource_type.equals(ResourceBean.CD_RESOURCE_TYPE))
	    icon_string = "<img src=\"../images/icnResourceCD.gif\" width=\"17\" height=\"17\" title=\"Compact Disc (CD)\" alt=\"Compact Disc (CD)\" />";
%>
							<!-- BEGIN Resource -->
							<div class="resource">
								<table cellspacing="0" cellpadding="0" border="0" summary="">
									<tr>
										<td style="width:  20px; text-align: left;  "></td>
										<td style="width: 332px; text-align: left;  "><a href="admin-resource-basic.jsp?id=<%= resource.getId() %>" title=""><%= resource.getLabel() %></a></td>
										<td style="width:  50px; text-align: center;  "><%= icon_string %></td>
										<td style="width:  90px; text-align: center;"><%= resource.getReleasedDateString() %></td>
									</tr>
								</table>
							</div>
							<!-- END Resource -->
<%
    }
}
else
{
%>
							<!-- BEGIN Resource -->
							<div class="resource">
								<table cellspacing="0" cellpadding="0" border="0" summary="">
									<tr>
										<td style="width: 492px; text-align: left;  " colspan="4">No Resources Found</td>
									</tr>
								</table>
							</div>
							<!-- END Resource -->
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