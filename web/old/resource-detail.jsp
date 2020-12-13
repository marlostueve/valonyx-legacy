<%@ page contentType="text/html" import="java.util.*, java.io.*, com.badiyan.uk.beans.*, com.badiyan.torque.*, com.badiyan.uk.exceptions.*, com.badiyan.uk.online.beans.*" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="struts-bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="struts-html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="struts-logic" %>

<jsp:useBean id="userCompany" class="com.badiyan.uk.online.beans.UKOnlineCompanyBean" scope="session" />
<jsp:useBean id="loginBean" class="com.badiyan.uk.online.beans.UKOnlineLoginBean" scope="session" />
<jsp:useBean id="userResource" class="com.badiyan.uk.beans.ResourceBean" scope="session" />

<%@ include file="channels/channel-session.jsp" %>
<%@ include file="channels/channel-permissions.jsp" %>

<%
   
UKOnlinePersonBean person_obj = (UKOnlinePersonBean)loginBean.getPerson();

boolean is_connected = false;
if (!CUBean.isMasterServer())
    is_connected = CUBean.isConnected();
if (request.getParameter("id") != null)
{
    try
    {
        userResource = ResourceBean.getResource(Integer.parseInt(request.getParameter("id")));
        session.setAttribute("userResource", userResource);
    }
    catch (NumberFormatException x)
    {
        //errorMessage = x.getMessage();
    }
}

Calendar now = Calendar.getInstance();
boolean isNew = false;
Calendar broadcastDate = Calendar.getInstance();
broadcastDate.setTime(userResource.getEffectiveDate());
broadcastDate.add(Calendar.DATE, userResource.getDisplayAsNewDays());
if (broadcastDate.after(now))
    isNew = true;
String resource_type = userResource.getType();
String icon_string = "";
String urlString = "resources/" + userResource.getURL();
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


boolean viewable = userResource.isViewableBy(loginBean.getPerson());
if (!viewable)
{
    // person not part of audience.  allow access if this resource is a resource for a course that I'm enrolled in.
    
    boolean is_resource_for_enrolled_course = false;
    Iterator enrollments_itr = loginBean.getPerson().getEnrollments().iterator();
    while (enrollments_itr.hasNext())
    {
	EnrollmentBean enrollment_obj = (EnrollmentBean)enrollments_itr.next();
	CourseBean enrolled_course = enrollment_obj.getCourse();
	is_resource_for_enrolled_course = enrolled_course.isResource(userResource);
	if (is_resource_for_enrolled_course)
	    break;
    }
    
    viewable = is_resource_for_enrolled_course;
}

boolean has_missing_files = userResource.hasMissingFiles();

if (has_missing_files && !CUBean.isMasterServer())
{
    // if the resource is missing and this is a tablet then look in the content folder for the resource...
    
    String resource_file_url = userResource.getURL();
    if ((resource_file_url == null) || (resource_file_url.equals("")))
    {
    }
    else
    {
	String file_path = System.getProperty("cu.tabletContentPath") + "\\resources\\" + resource_file_url;
	File test_file = new File(file_path);
	has_missing_files = !test_file.exists();
    }
}

if (request.getParameter("save") != null)
{
    try
    {
	ResourceBean fav_res = ResourceBean.getResource(Integer.parseInt(request.getParameter("save")));
	if (viewable)
	    person_obj.addResource(fav_res);
    }
    catch (Exception x)
    {
    }
}
if (request.getParameter("remove") != null)
{
    try
    {
        person_obj.removeResource(ResourceBean.getResource(Integer.parseInt(request.getParameter("remove"))));
    }
    catch (Exception x)
    {
    }
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
		<struts-html:javascript formName="indexResourceSearchForm" dynamicJavascript="true" staticJavascript="false"/>
		<link rel="stylesheet" type="text/css" href="css/web.css" />
	</head>

	<body>

		<div id="main">

<%@ include file="channels\channel-menu.jsp" %>

			<div class="horizontalRule"><img src="images/trspacer.gif" width="1" height="1" alt="" /></div>

			<!-- *** BEGIN Content *** -->
			<div id="content">

				<div class="content_TextBlock">
					<p class="headline">Resource Details</p>
					<p>This screen lists the details for the resource you selected.</p>
<p>Click the appropriate link on the right to open the resource. The link will be blue (active) if
you currently <strong>can</strong> complete that action, or gray (inactive) if you <strong>cannot</strong>. For example, if
you want to download a resource from the Internet, the link will be blue if you are on-line,
and gray if you are not.</p>
<p>If you will frequently refer to this resource, save it as a Favorite by clicking <strong>Create
Favorite</strong>. When you will no longer need to refer to the resource regularly, click <strong>Remove
Favorite</strong>.  This will not remove the resource from the list, just remove its Favorite status.</p>
					<div class="horizontalRule"><img src="images/trspacer.gif" width="1" height="1" alt="" /></div>
				</div>

				<div class="content_Details">
					<div class="subnav">
<%

if (!CUBean.isMasterServer())
{
    if (is_connected && has_missing_files)
    {
%>
						<a href="synchronize-resource.jsp?id=<%= userResource.getId() %>" title="Download Resource">Download Resource</a>
<%
    }
    else
    {
%>
						<span>Download Resource</span>
<%
    }
%>
						<!-- <a href="#" title="Update Resource">Update Resource</a> -->
						<span>Update Resource</span>
<%
}
if (viewable && !has_missing_files)
{
%>
						<a href="#" onClick="window.open('resource.jsp?id=<%= userResource.getId() %>','ResourceWin','scrollbars=yes,resizable=yes,toolbar=no,location=no,directories=no,status=no,menubar=yes,width=800,height=600');" title="Open Resource">Open Resource</a>
<%
}
else
{
%>
						<span>Open Resource</span>
<%
}
if (person_obj.isFavoriteResource(userResource))
{
%>
						<span>Create Favorite</span>
						<a href="resource-detail.jsp?remove=<%= userResource.getId() %>" title="Remove Favorite">Remove Favorite</a>
<%
}
else
{
    if (viewable)
    {
%>
						<a href="resource-detail.jsp?save=<%= userResource.getId() %>" title="Create Favorite">Create Favorite</a>
<%
    }
    else
    {
%>
						<span>Create Favorite</span>
<%
    }
%>
						<span>Remove Favorite</span>
<%
}
%>
					</div>


					<div class="detailItem">
						<div class="left">TITLE</div>
						<div class="right"><%= userResource.getLabel() %></div>
						<div class="end"></div>
					</div>

					<div class="detailItem">
						<div class="left">DESCRIPTION</div>
						<div class="right"><%= userResource.getDescription() %></div>
						<div class="end"></div>
					</div>

					<div class="detailItem">
						<div class="left">TYPE</div>
						<div class="right"><%= icon_string %></div>
						<div class="end"></div>
					</div>

					<div class="detailItem">
						<div class="left">RELEASED</div>
						<div class="right"><%= userResource.getReleasedDateString() %></div>
						<div class="end"></div>
					</div>

					<div class="detailItem">
						<div class="left">LAST UPDATED</div>
						<div class="right"><%= userResource.getModificationDateString() %></div>
						<div class="end"></div>
					</div>

					<div class="detailItem">
						<div class="left">UPDATE NOTES</div>
						<div class="right"><%= userResource.getUpdateNotes() %></div>
						<div class="end"></div>
					</div>
<%
String contact_string = "";
try
{
    PersonBean contact = userResource.getOwner();
    contact_string = contact.getFullName();
}
catch (Exception x)
{
}
%>
					<div class="detailItem">
						<div class="left">CONTACT</div>
						<div class="right"><%= contact_string %></div>
						<div class="end"></div>
					</div>


				</div>


				<div class="content_TextBlock">
					<div class="horizontalRule"><img src="images/trspacer.gif" width="1" height="1" alt="" /></div>
				</div>

				<div id="content_SimpleSearch">
					<struts-html:form action="/indexResourceSearch" focus="keyword" onsubmit="return validateIndexResourceSearchForm(this);"><input name="keyword" onfocus="select();" value="Search" size="37" maxlength="50" class="inputbox" style="width: 206px; margin-right: 4px;" /><input type="image" src="images/btnSearch.gif" value="Search" alt="Search" style="_margin-top: 1px;" /></struts-html:form>
				</div>

			</div>
			<!-- *** END Content *** -->

			<div class="horizontalRule"><img src="images/trspacer.gif" width="1" height="1" alt="" /></div>

<%@ include file="channels\channel-footer.jsp" %>

		</div>

	</body>

</html>