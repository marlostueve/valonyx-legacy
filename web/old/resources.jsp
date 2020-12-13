<%@ page contentType="text/html" import="java.util.*, java.io.*, com.badiyan.uk.beans.*, com.badiyan.torque.*, com.badiyan.uk.exceptions.*, com.badiyan.uk.online.beans.*" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="struts-bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="struts-html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="struts-logic" %>

<jsp:useBean id="userCompany" class="com.badiyan.uk.online.beans.UKOnlineCompanyBean" scope="session" />
<jsp:useBean id="selectedDepartment" class="com.badiyan.uk.beans.DepartmentBean" scope="session" />
<jsp:useBean id="selectedGroup" class="com.badiyan.uk.beans.PersonGroupBean" scope="session" />
<jsp:useBean id="selectedTitle" class="com.badiyan.uk.beans.PersonTitleBean" scope="session" />

<jsp:useBean id="resourceSearch" class="com.badiyan.uk.online.beans.UKOnlineResourceSearchBean" scope="session" />
<jsp:useBean id="listHelper" class="com.badiyan.uk.online.beans.EcolabListHelperBean" scope="session"/>
<jsp:useBean id="loginBean" class="com.badiyan.uk.online.beans.UKOnlineLoginBean" scope="session" />

<%@ include file="channels/channel-session.jsp" %>
<%@ include file="channels/channel-permissions.jsp" %>

<%
Calendar now = Calendar.getInstance();
UKOnlinePersonBean person_obj = (UKOnlinePersonBean)loginBean.getPerson();
listHelper.setCompany(userCompany);
resourceSearch.setCompany(userCompany);

resourceSearch.clearSearchCriteria();

if (!resourceSearch.searchPerformed())
{
    try
    {
	resourceSearch.setDisplayOnlyActive(true);
	
	try
	{
	    resourceSearch.setUserGroup(person_obj.getGroup());
	}
	catch (Exception x)
	{
	    //x.printStackTrace();
	}
	try
	{
	    resourceSearch.setJobTitle(person_obj.getJobTitle());
	}
	catch (Exception x)
	{
	    //x.printStackTrace();
	}
	try
	{
	    resourceSearch.setDepartment(person_obj.getDepartment());
	}
	catch (Exception x)
	{
	}
	resourceSearch.search();
    }
    catch (Exception x)
    {
	x.printStackTrace();
    }
}

Vector resource_list = null;
//Vector course_list = null;
if (!CUBean.isMasterServer())
{
    resource_list = new Vector();
    String resources_path = System.getProperty("cu.tabletContentPath") + "\\resources";
    File resources_path_file = new File(resources_path);
    if (resources_path_file.exists() && resources_path_file.isDirectory())
    {
	String[] file_list = resources_path_file.list();
	for (int i = 0; i < file_list.length; i++)
	    resource_list.addElement(file_list[i]);
    }

    /*
    course_list = new Vector();
    String courses_path = System.getProperty("cu.tabletContentPath") + "\\courses";
    File courses_path_file = new File(courses_path);
    if (courses_path_file.exists() && courses_path_file.isDirectory())
    {
	String[] file_list = courses_path_file.list();
	for (int i = 0; i < file_list.length; i++)
	    course_list.addElement(file_list[i]);
    }
    */
 }
	
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">

	<head>

		<title>Ecolab</title>

		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
		<meta name="generator" content="TextPad 4.6.2" />
		<meta name="author" content="" />
		<meta name="copyright" content="&copy; 2007" />
		<meta name="keywords" content="" />
		<meta name="description" content="" />
		
		<link rel="stylesheet" type="text/css" href="css/web.css" />

	</head>

	<body>

		<div id="main">

<%@ include file="channels\channel-menu.jsp" %>

			<div class="horizontalRule"><img src="images/trspacer.gif" width="1" height="1" alt="" /></div>

			<!-- *** BEGIN Content *** -->
			<div id="content">

				<div class="content_TextBlock">
					<p class="headline">Resources</p>
					<p>This screen lists all resources. To view a resource, click the name of the resource; this will
take you to the Details screen for that resource.
If you will frequently refer to the same resource, save it as a Favorite from the Resource
Details screen.</p>
				</div>


				<div class="content_Table">

					<div class="horizontalRule"><img src="images/trspacer.gif" width="1" height="1" alt="" /></div>
					<div class="heading">
						<table cellspacing="0" cellpadding="0" border="0" summary="">
							<tr style="display: block;">
								<td style="width:  20px; text-align: left;  "></td>
								<td style="width:  28px; text-align: left;  ">Fav</td>
								<td style="width: 520px; text-align: left;  ">Title</td>
								<td style="width:  90px; text-align: center;">Type</td>
								<td style="width:  90px; text-align: center;">Released</td>
							</tr>
						</table>
					</div>
					<div class="horizontalRule"><img src="images/trspacer.gif" width="1" height="1" alt="" /></div>
<%
boolean resource_found = false;
Iterator itr = person_obj.getResources().iterator();
if (itr.hasNext())
{
    while (itr.hasNext())
    {
	resource_found = true;
	ResourceBean resourceObj = (ResourceBean)itr.next();
	boolean isNew = false;
	Calendar broadcastDate = Calendar.getInstance();
	broadcastDate.setTime(resourceObj.getEffectiveDate());
	broadcastDate.add(Calendar.DATE, resourceObj.getDisplayAsNewDays());
	if (broadcastDate.after(now))
	    isNew = true;
	String resource_type = resourceObj.getType();
	String icon_string = "";
	String urlString = "resources/" + resourceObj.getURL();
	if (resource_type.equals(ResourceBean.WORD_RESOURCE_TYPE))
	    icon_string = "<img src=\"images/icnResourceDOC.gif\" width=\"17\" height=\"17\" title=\"Microsoft Word (DOC)\" alt=\"Microsoft Word (DOC)\" />";
	else if (resource_type.equals(ResourceBean.ACROBAT_RESOURCE_TYPE))
	    icon_string = "<img src=\"images/icnResourcePDF.gif\" width=\"17\" height=\"17\" title=\"Portable Document Format (PDF)\" alt=\"Portable Document Format (PDF)\" />";
	else if (resource_type.equals(ResourceBean.FLASH_RESOURCE_TYPE))
	    icon_string = "<img src=\"images/icnResourceSWF.gif\" width=\"17\" height=\"17\" title=\"Flash (SWF)\" alt=\"Flash (SWF)\" />";
	else if (resource_type.equals(ResourceBean.INTERNET_RESOURCE_TYPE))
	    icon_string = "<img src=\"images/icnResourceWEB.gif\" width=\"17\" height=\"17\" title=\"World Wide Web\" alt=\"World Wide Web\" />";
	else if (resource_type.equals(ResourceBean.POWERPOINT_RESOURCE_TYPE))
	    icon_string = "<img src=\"images/icnResourcePPT.gif\" width=\"17\" height=\"17\" title=\"Microsoft Powerpoint (PPT)\" alt=\"Microsoft Powerpoint (PPT)\" />";
	else if (resource_type.equals(ResourceBean.VIDEO_RESOURCE_TYPE))
	    icon_string = "<img src=\"images/icnResourceVID.gif\" width=\"17\" height=\"17\" title=\"Video\" alt=\"Video\" />";
	else if (resource_type.equals(ResourceBean.XLS_RESOURCE_TYPE))
	    icon_string = "<img src=\"images/icnResourceXLS.gif\" width=\"17\" height=\"17\" title=\"Microsoft Excel (XLS)\" alt=\"Microsoft Excel (XLS)\" />";
	else if (resource_type.equals(ResourceBean.CD_RESOURCE_TYPE))
	{
	    urlString = "";
	    icon_string = "<img src=\"images/icnResourceCD.gif\" width=\"17\" height=\"17\" title=\"Compact Disc (CD)\" alt=\"Compact Disc (CD)\" />";
	}
%>
					<!-- BEGIN Resource -->
					<div class="resource">
						<table cellspacing="0" cellpadding="0" border="0" summary="">
							<tr>
								<td style="width:  20px; text-align: left;  "></td>
								<td style="width:  28px; text-align: left;  "><img src="images/icnFav.gif" width="17" height="17" title="Favorite" alt="Favorite" /></td>
								<td style="width: 520px; text-align: left;  "><a href="resource-detail.jsp?id=<%= resourceObj.getId() %>" title=""><%= resourceObj.getNameString() %><%= isNew ? "&nbsp;<img src=\"images/PttrsnUK_ICON_NEW.gif\" border=0>" : "" %></a></td>
								<td style="width:  90px; text-align: center;"><%= icon_string %></td>
								<td style="width:  90px; text-align: center;"><%= resourceObj.getReleasedDateString() %></td>
							</tr>
						</table>
					</div>
					<!-- END Resource -->
<%
    }
}

boolean only_show_resources_that_exist_locally = false;
if (!CUBean.isMasterServer())
{
    UKOnlinePersonBean tablet_user = UKOnlinePersonBean.getTabletUser();
    if (!tablet_user.isServerApproved())
    {
	// I'm assuming that this is a good check to determine if a sync has been completed...
	// only allow viewing resources that actually exist
	only_show_resources_that_exist_locally = true;
    }
}
Enumeration resources = resourceSearch.getList();
if (resources.hasMoreElements())
{
    while (resources.hasMoreElements())
    {
	
	ResourceBean resourceObj = (ResourceBean)resources.nextElement();
	
	boolean show_resource = true;
	if (only_show_resources_that_exist_locally)
	    show_resource = resource_list.contains(resourceObj.getURL());
	
	if (!person_obj.isFavoriteResource(resourceObj) && show_resource)
	{
	    resource_found = true;
	    boolean isNew = false;
	    Calendar broadcastDate = Calendar.getInstance();
	    broadcastDate.setTime(resourceObj.getEffectiveDate());
	    broadcastDate.add(Calendar.DATE, resourceObj.getDisplayAsNewDays());
	    if (broadcastDate.after(now))
		isNew = true;
	    String resource_type = resourceObj.getType();
	    String icon_string = "";
	    String urlString = "resources/" + resourceObj.getURL();
	    if (resource_type.equals(ResourceBean.WORD_RESOURCE_TYPE))
		icon_string = "<img src=\"images/icnResourceDOC.gif\" width=\"17\" height=\"17\" title=\"Microsoft Word (DOC)\" alt=\"Microsoft Word (DOC)\" />";
	    else if (resource_type.equals(ResourceBean.ACROBAT_RESOURCE_TYPE))
		icon_string = "<img src=\"images/icnResourcePDF.gif\" width=\"17\" height=\"17\" title=\"Portable Document Format (PDF)\" alt=\"Portable Document Format (PDF)\" />";
	    else if (resource_type.equals(ResourceBean.FLASH_RESOURCE_TYPE))
		icon_string = "<img src=\"images/icnResourceSWF.gif\" width=\"17\" height=\"17\" title=\"Flash (SWF)\" alt=\"Flash (SWF)\" />";
	    else if (resource_type.equals(ResourceBean.INTERNET_RESOURCE_TYPE))
		icon_string = "<img src=\"images/icnResourceWEB.gif\" width=\"17\" height=\"17\" title=\"World Wide Web\" alt=\"World Wide Web\" />";
	    else if (resource_type.equals(ResourceBean.POWERPOINT_RESOURCE_TYPE))
		icon_string = "<img src=\"images/icnResourcePPT.gif\" width=\"17\" height=\"17\" title=\"Microsoft Powerpoint (PPT)\" alt=\"Microsoft Powerpoint (PPT)\" />";
	    else if (resource_type.equals(ResourceBean.VIDEO_RESOURCE_TYPE))
		icon_string = "<img src=\"images/icnResourceVID.gif\" width=\"17\" height=\"17\" title=\"Video\" alt=\"Video\" />";
	    else if (resource_type.equals(ResourceBean.XLS_RESOURCE_TYPE))
		icon_string = "<img src=\"images/icnResourceXLS.gif\" width=\"17\" height=\"17\" title=\"Microsoft Excel (XLS)\" alt=\"Microsoft Excel (XLS)\" />";
	    else if (resource_type.equals(ResourceBean.CD_RESOURCE_TYPE))
	    {
		urlString = "";
		icon_string = "<img src=\"images/icnResourceCD.gif\" width=\"17\" height=\"17\" title=\"Compact Disc (CD)\" alt=\"Compact Disc (CD)\" />";
	    }
%>
					<!-- BEGIN Resource -->
					<div class="resource">
						<table cellspacing="0" cellpadding="0" border="0" summary="">
							<tr>
								<td style="width:  20px; text-align: left;  "></td>
								<td style="width:  28px; text-align: left;  "><img src="images/icnFav-grayed.gif" width="17" height="17" title="Not a Favorite" alt="Not a Favorite" /></td>
								<td style="width: 520px; text-align: left;  "><a href="resource-detail.jsp?id=<%= resourceObj.getId() %>" title=""><%= resourceObj.getNameString() %><%= isNew ? "&nbsp;<img src=\"images/PttrsnUK_ICON_NEW.gif\" border=0>" : "" %></a></td>
								<td style="width:  90px; text-align: center;"><%= icon_string %></td>
								<td style="width:  90px; text-align: center;"><%= resourceObj.getReleasedDateString() %></td>
							</tr>
						</table>
					</div>
					<!-- END Resource -->
<%
	}
    }
}
if (!resource_found)
{
%>
					<!-- BEGIN Resource -->
					<div class="resource">
						<table cellspacing="0" cellpadding="0" border="0" summary="">
							<tr>
								<td style="width:  748px; text-align: left;" colspan="5">No Resources Found</td>
							</tr>
						</table>
					</div>
					<!-- END Resource -->
<%
}
%>
					<div class="horizontalRule"><img src="images/trspacer.gif" width="1" height="1" alt="" /></div>
				</div>
				
				

				<div id="content_SimpleSearch">
					<struts-html:form action="/indexResourceSearch" focus="keyword" ><input name="keyword" onfocus="select();" value="Search" size="37" maxlength="50" class="inputbox" style="width: 206px; margin-right: 4px;" /><input type="image" src="images/btnSearch.gif" value="Search" alt="Search" style="_margin-top: 1px;" /></struts-html:form>
				</div>

			</div>
			<!-- *** END Content *** -->

			<div class="horizontalRule"><img src="images/trspacer.gif" width="1" height="1" alt="" /></div>

<%@ include file="channels\channel-footer.jsp" %>

		</div>

	</body>

</html>