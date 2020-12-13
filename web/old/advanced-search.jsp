<%@ page contentType="text/html" import="java.util.*, java.io.*, com.badiyan.uk.beans.*, com.badiyan.torque.*, com.badiyan.uk.exceptions.*, com.badiyan.uk.online.beans.*" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="struts-bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="struts-html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="struts-logic" %>

<jsp:useBean id="adminCompany" class="com.badiyan.uk.online.beans.UKOnlineCompanyBean" scope="session" />

<jsp:useBean id="courseSearch" class="com.badiyan.uk.online.beans.UKOnlineCourseSearchBean" scope="session"/>
<jsp:useBean id="resourceSearch" class="com.badiyan.uk.online.beans.UKOnlineResourceSearchBean" scope="session" />

<jsp:useBean id="loginBean" class="com.badiyan.uk.online.beans.UKOnlineLoginBean" scope="session" />
<jsp:useBean id="listHelper" class="com.badiyan.uk.online.beans.EcolabListHelperBean" scope="session"/>

<%@ include file="channels/channel-session.jsp" %>
<%@ include file="channels/channel-permissions.jsp" %>

<%
Calendar now = Calendar.getInstance();

Vector resource_list = null;
Vector course_list = null;
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

    course_list = new Vector();
    String courses_path = System.getProperty("cu.tabletContentPath") + "\\courses";
    File courses_path_file = new File(courses_path);
    if (courses_path_file.exists() && courses_path_file.isDirectory())
    {
	String[] file_list = courses_path_file.list();
	for (int i = 0; i < file_list.length; i++)
	    course_list.addElement(file_list[i]);
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

		<link rel="stylesheet" type="text/css" href="css/web.css" />

		<script type="text/javascript" src="scripts/crir.js"></script>
		<%@ include file="channels\channel-head-javascript.jsp" %>
	</head>

	<body>

		<div id="main">

<%@ include file="channels\channel-menu.jsp" %>

			<div class="horizontalRule"><img src="images/trspacer.gif" width="1" height="1" alt="" /></div>

			<!-- *** BEGIN Content *** -->
			<div id="content">

				<div class="content_TextBlock">
					<p class="headline">Search</p>
					<p>This screen allows you to search for activities or resources. You can refine your
search by selecting types of activities or resources, defining release parameters, or
specifying keywords. When you have defined the parameters of your search, click the magnifying glass button. The results of your search will appear below in the Search Results area.</p>
					<div class="horizontalRule"><img src="images/trspacer.gif" width="1" height="1" alt="" /></div>
				</div>

				<div class="content_AdvancedSearch">

					<struts-html:form action="/advancedSearch" focus="keyword">

						<input id="dummy" name="dummy" type="hidden" />

						<div class="searchItem">

							<label for="radio1">Activities</label>
							<input name="group1" id="radio1" type="radio" value="1" class="crirHiddenJS" onclick="document.getElementById('activityTypes').style.display = 'block'; document.getElementById('resourceTypes').style.display = 'none';" <%= (resourceSearch.getResultsType() == 1) ? "checked=\"checked\" " : "" %>/>

							<label for="radio2">Resources</label>
							<input name="group1" id="radio2" type="radio" value="2" class="crirHiddenJS" onclick="document.getElementById('activityTypes').style.display = 'none'; document.getElementById('resourceTypes').style.display = 'block';" <%= (resourceSearch.getResultsType() == 2) ? "checked=\"checked\" " : "" %>/>
<!--
							<label for="radio3">All</label>
							<input name="group1" id="radio3" type="radio" value="3" class="crirHiddenJS" onclick="" <%= (resourceSearch.getResultsType() == 3) ? "checked=\"checked\" " : "" %>/>
-->
						</div>

						<div class="searchItem" id="activityTypes" style="display: <%= (resourceSearch.getResultsType() == 1) ? "block" : "none" %>;">

							<div class="left">ACTIVITY TYPE</div>
							<div class="right">
<%
boolean activity_selected = (courseSearch.containsType(CourseReportLister.E_LEARNING_ACTIVITY_TYPE_NAME) || courseSearch.containsType(CourseReportLister.MENTOR_ACTIVITY_TYPE_NAME) || courseSearch.containsType(CourseReportLister.INSTRUCTOR_LED_ACTIVITY_TYPE_NAME) || courseSearch.containsType(CourseReportLister.ASSESSMENT_ACTIVITY_TYPE_NAME));
%>
								<label for="typeActivityELRN"><img src="images/icnActivityELRN.gif" width="17" height="17" title="E-Learning" alt="E-Learning" /></label>
								<input name="typeActivityELRN" id="typeActivityELRN" type="checkbox" onclick="activityClick();" class="crirHiddenJS" <%= courseSearch.containsType(CourseReportLister.E_LEARNING_ACTIVITY_TYPE_NAME) ? "checked=\"checked\" " : "" %>/>

								<label for="typeActivityMNTR"><img src="images/icnActivityMNTR.gif" width="17" height="17" title="Mentor" alt="Mentor" /></label>
								<input name="typeActivityMNTR" id="typeActivityMNTR" type="checkbox" onclick="activityClick();" class="crirHiddenJS" <%= courseSearch.containsType(CourseReportLister.MENTOR_ACTIVITY_TYPE_NAME) ? "checked=\"checked\" " : "" %>/>

								<label for="typeActivityILED"><img src="images/icnActivityILED.gif" width="17" height="17" title="Instructor-Led" alt="Instructor-Led" /></label>
								<input name="typeActivityILED" id="typeActivityILED" type="checkbox" onclick="activityClick();" class="crirHiddenJS" <%= courseSearch.containsType(CourseReportLister.INSTRUCTOR_LED_ACTIVITY_TYPE_NAME) ? "checked=\"checked\" " : "" %>/>

								<label for="typeActivityASMT"><img src="images/icnActivityASMT.gif" width="17" height="17" title="Assessment" alt="Assessment" /></label>
								<input name="typeActivityASMT" id="typeActivityASMT" type="checkbox" onclick="activityClick();" class="crirHiddenJS" <%= courseSearch.containsType(CourseReportLister.ASSESSMENT_ACTIVITY_TYPE_NAME) ? "checked=\"checked\" " : "" %>/>
								
								<label for="typeActivityALL">All Activities<img src="images/trspacer.gif" width="17" height="17" title="Assessment" alt="Assessment" /></label>
								<input name="typeActivityALL" id="typeActivityALL" type="checkbox" onclick="activityAllClick();" class="crirHiddenJS" <%= activity_selected ? "" : "checked=\"checked\" " %>/>

							</div>
							<div class="end"></div>

						</div>

						<div class="searchItem" id="resourceTypes" style="display: <%= (resourceSearch.getResultsType() == 2) ? "block" : "none" %>;">

							<div class="left">RESOURCE TYPE</div>
							<div class="right">
<%
boolean resource_selected = (resourceSearch.containsType(ResourceBean.WORD_RESOURCE_TYPE) || resourceSearch.containsType(ResourceBean.POWERPOINT_RESOURCE_TYPE) || resourceSearch.containsType(ResourceBean.XLS_RESOURCE_TYPE) || resourceSearch.containsType(ResourceBean.ACROBAT_RESOURCE_TYPE) || resourceSearch.containsType(ResourceBean.FLASH_RESOURCE_TYPE) || resourceSearch.containsType(ResourceBean.INTERNET_RESOURCE_TYPE) || resourceSearch.containsType(ResourceBean.VIDEO_RESOURCE_TYPE) || resourceSearch.containsType(ResourceBean.CD_RESOURCE_TYPE));
%>
								<label for="typeResourceDOC"><img src="images/icnResourceDOC.gif" width="17" height="17" title="Microsoft Word (DOC)" alt="Microsoft Word (DOC)" /></label>
								<input name="typeResourceDOC" id="typeResourceDOC" type="checkbox" onclick="resourceClick();" class="crirHiddenJS" <%= resourceSearch.containsType(ResourceBean.WORD_RESOURCE_TYPE) ? "checked=\"checked\" " : "" %>/>

								<label for="typeResourcePPT"><img src="images/icnResourcePPT.gif" width="17" height="17" title="Microsoft PowerPoint (PPT)" alt="Microsoft PowerPoint (PPT)" /></label>
								<input name="typeResourcePPT" id="typeResourcePPT" type="checkbox" onclick="resourceClick();" class="crirHiddenJS" <%= resourceSearch.containsType(ResourceBean.POWERPOINT_RESOURCE_TYPE) ? "checked=\"checked\" " : "" %>/>

								<label for="typeResourceXLS"><img src="images/icnResourceXLS.gif" width="17" height="17" title="Microsoft Excel (XLS)" alt="Microsoft Excel (XLS)" /></label>
								<input name="typeResourceXLS" id="typeResourceXLS" type="checkbox" onclick="resourceClick();" class="crirHiddenJS" <%= resourceSearch.containsType(ResourceBean.XLS_RESOURCE_TYPE) ? "checked=\"checked\" " : "" %>/>

								<label for="typeResourcePDF"><img src="images/icnResourcePDF.gif" width="17" height="17" title="Adobe Acrobat (PDF)" alt="Adobe Acrobat (PDF)" /></label>
								<input name="typeResourcePDF" id="typeResourcePDF" type="checkbox" onclick="resourceClick();" class="crirHiddenJS" <%= resourceSearch.containsType(ResourceBean.ACROBAT_RESOURCE_TYPE) ? "checked=\"checked\" " : "" %>/>

								<label for="typeResourceSWF"><img src="images/icnResourceSWF.gif" width="17" height="17" title="Macromedia Flash (SWF)" alt="Macromedia Flash (SWF)" /></label>
								<input name="typeResourceSWF" id="typeResourceSWF" type="checkbox" onclick="resourceClick();" class="crirHiddenJS" <%= resourceSearch.containsType(ResourceBean.FLASH_RESOURCE_TYPE) ? "checked=\"checked\" " : "" %>/>

								<label for="typeResourceWEB"><img src="images/icnResourceWEB.gif" width="17" height="17" title="Internet" alt="Internet" /></label>
								<input name="typeResourceWEB" id="typeResourceWEB" type="checkbox" onclick="resourceClick();" class="crirHiddenJS" <%= resourceSearch.containsType(ResourceBean.INTERNET_RESOURCE_TYPE) ? "checked=\"checked\" " : "" %>/>

								<label for="typeResourceVID"><img src="images/icnResourceVID.gif" width="17" height="17" title="Video" alt="Video" /></label>
								<input name="typeResourceVID" id="typeResourceVID" type="checkbox" onclick="resourceClick();" class="crirHiddenJS" <%= resourceSearch.containsType(ResourceBean.VIDEO_RESOURCE_TYPE) ? "checked=\"checked\" " : "" %>/>

								<label for="typeResourceCD"><img src="images/icnResourceCD.gif" width="17" height="17" title="CD" alt="CD" /></label>
								<input name="typeResourceCD" id="typeResourceCD" type="checkbox" onclick="resourceClick();" class="crirHiddenJS" <%= resourceSearch.containsType(ResourceBean.CD_RESOURCE_TYPE) ? "checked=\"checked\" " : "" %>/>
								
								<label for="typeResourceALL">All Resources<img src="images/trspacer.gif" width="17" height="17" title="Assessment" alt="Assessment" /></label>
								<input name="typeResourceALL" id="typeResourceALL" type="checkbox" onclick="resourceAllClick();" class="crirHiddenJS" <%= resource_selected ? "" : "checked=\"checked\" " %>/>

							</div>
							<div class="end"></div>

						</div>
						
						<script type="text/javascript">
						//<![CDATA[

							function activityClick() {
								
								if ((document.getElementById('typeActivityELRN').checked == false) &&
									(document.getElementById('typeActivityMNTR').checked == false) &&
									(document.getElementById('typeActivityILED').checked == false) &&
									(document.getElementById('typeActivityASMT').checked == false))
								{
								    document.getElementById('typeActivityALL').checked = "checked";
								    crir.findLabel('typeActivityALL').className = "checkbox_checked";
								}
								else
								{
								    document.getElementById('typeActivityALL').checked = "";
								    crir.findLabel('typeActivityALL').className = "checkbox_unchecked";
								}
							}
							
							function activityAllClick() {
								
								document.getElementById('typeActivityELRN').checked = "";
								document.getElementById('typeActivityMNTR').checked = "";
								document.getElementById('typeActivityILED').checked = "";
								document.getElementById('typeActivityASMT').checked = "";
								document.getElementById('typeActivityALL').checked = "checked";

								crir.findLabel('typeActivityELRN').className = "checkbox_unchecked";
								crir.findLabel('typeActivityMNTR').className = "checkbox_unchecked";
								crir.findLabel('typeActivityILED').className = "checkbox_unchecked";
								crir.findLabel('typeActivityASMT').className = "checkbox_unchecked";
								crir.findLabel('typeActivityALL').className = "checkbox_checked";
							}
							
							function resourceClick() {
								
								if ((document.getElementById('typeResourceDOC').checked == false) &&
									(document.getElementById('typeResourcePPT').checked == false) &&
									(document.getElementById('typeResourceXLS').checked == false) &&
									(document.getElementById('typeResourcePDF').checked == false) &&
									(document.getElementById('typeResourceSWF').checked == false) &&
									(document.getElementById('typeResourceWEB').checked == false) &&
									(document.getElementById('typeResourceVID').checked == false) &&
									(document.getElementById('typeResourceCD').checked == false))
								{
								    document.getElementById('typeResourceALL').checked = "checked";
								    crir.findLabel('typeResourceALL').className = "checkbox_checked";
								}
								else
								{
								    document.getElementById('typeResourceALL').checked = "";
								    crir.findLabel('typeResourceALL').className = "checkbox_unchecked";
								}
							}
							
							function resourceAllClick() {
								
								document.getElementById('typeResourceDOC').checked = "";
								document.getElementById('typeResourcePPT').checked = "";
								document.getElementById('typeResourceXLS').checked = "";
								document.getElementById('typeResourcePDF').checked = "";
								document.getElementById('typeResourceSWF').checked = "";
								document.getElementById('typeResourceWEB').checked = "";
								document.getElementById('typeResourceVID').checked = "";
								document.getElementById('typeResourceCD').checked = "";
								document.getElementById('typeResourceALL').checked = "checked";

								crir.findLabel('typeResourceDOC').className = "checkbox_unchecked";
								crir.findLabel('typeResourcePPT').className = "checkbox_unchecked";
								crir.findLabel('typeResourceXLS').className = "checkbox_unchecked";
								crir.findLabel('typeResourcePDF').className = "checkbox_unchecked";
								crir.findLabel('typeResourceSWF').className = "checkbox_unchecked";
								crir.findLabel('typeResourceWEB').className = "checkbox_unchecked";
								crir.findLabel('typeResourceVID').className = "checkbox_unchecked";
								crir.findLabel('typeResourceCD').className = "checkbox_unchecked";
								crir.findLabel('typeResourceALL').className = "checkbox_checked";
							}

						//]]>
						</script>

						<div class="searchItem">

							<div class="left">RELEASED</div>
							<div class="right">

								<input name="releaseDate" onfocus="getDate('advancedSearchForm','releaseDate');select();" value="<%= resourceSearch.getReleasedDateString() %>" size="37" maxlength="50" class="inputbox" style="width: 206px; margin-right: 4px;" />

								<label for="radio4">Before</label>
								<input name="group2" id="radio4" type="radio" value="1" class="crirHiddenJS" <%= (resourceSearch.getReleasedDateCompareType() == 1) ? "checked=\"checked\" " : "" %>/>

								<label for="radio5">After</label>
								<input name="group2" id="radio5" type="radio" value="2" class="crirHiddenJS" <%= (resourceSearch.getReleasedDateCompareType() == 2) ? "checked=\"checked\" " : "" %>/>

								<label for="radio6">All</label>
								<input name="group2" id="radio6" type="radio" value="3" class="crirHiddenJS" <%= (resourceSearch.getReleasedDateCompareType() == 3) ? "checked=\"checked\" " : "" %>/>

							</div>
							<div class="end"></div>

						</div>

						<div class="searchItem">

							<div class="left">KEYWORD</div>
							<div class="right">

								<input name="keyword" onfocus="select();" value="<%= courseSearch.getKeywordString() %>" size="37" maxlength="50" class="inputbox" style="width: 206px; margin-right: 4px;" /><input type="image" src="images/btnSearch.gif" value="Search" alt="Search" style="_margin-top: 1px;" />

							</div>
							<div class="end"></div>

						</div>

					</struts-html:form>

				</div>

				<div class="content_TextBlock">
					<div class="horizontalRule"><img src="images/trspacer.gif" width="1" height="1" alt="" /></div>
				</div>

				<div class="content_TextBlock">
					<p class="headline">Search Results</p>
				</div>


				<div class="content_Table">

					<div class="horizontalRule"><img src="images/trspacer.gif" width="1" height="1" alt="" /></div>
					<div class="heading">
						<table cellspacing="0" cellpadding="0" border="0" summary="">
							<tr style="display: block;">
								<td style="width:  20px; text-align: left;  "></td>
								<td style="width:  45px; text-align: left;  ">Rel</td>
								<td style="width: 483px; text-align: left;  ">Title</td>
								<td style="width:  90px; text-align: center;">Type</td>
								<td style="width:  90px; text-align: center;">Released</td>
							</tr>
						</table>
					</div>
					<div class="horizontalRule"><img src="images/trspacer.gif" width="1" height="1" alt="" /></div>
<%
Enumeration results = null;
if (resourceSearch.getResultsType() == 1)
{
    // activity only
    
    results = courseSearch.getList();
}
else if (resourceSearch.getResultsType() == 2)
{
    // resource only
    
    results = resourceSearch.getList();
}
else
{
    // both
    
    //resourceSearch.addSearchResults();
}

boolean results_found = false;
if ((results != null) && results.hasMoreElements())
{
    Vector score_vec = null;
    if (resourceSearch.getResultsType() == 1)
	score_vec = courseSearch.getRelScores();
    else if (resourceSearch.getResultsType() == 2)
	score_vec = resourceSearch.getRelScores();
    for (int i = 0; results.hasMoreElements(); i++)
    {
	Object result = results.nextElement();
	if (result instanceof ResourceBean)
	{
	    ResourceBean resource = (ResourceBean)result;
	    
	    boolean display_resource = true;
	    if (resource_list != null)
		display_resource = resource_list.contains(resource.getURLString());
	    if (display_resource)
	    {
		results_found = true;
		boolean isNew = false;
		try
		{
		    Calendar broadcastDate = Calendar.getInstance();
		    broadcastDate.setTime(resource.getEffectiveDate());
		    broadcastDate.add(Calendar.DATE, resource.getDisplayAsNewDays());
		    if (broadcastDate.after(now))
			isNew = true;
		}
		catch (Exception xx)
		{
		}
		String resource_type = resource.getType();
		String icon_string = "";
		String urlString = "resources/" + resource.getURL();
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
					<!-- BEGIN Search Result -->
					<div class="searchResult">
						<table cellspacing="0" cellpadding="0" border="0" summary="">
							<tr>
								<td style="width:  20px; text-align: left;  "></td>
								<td style="width:  45px; text-align: left;  "><%= resourceSearch.useKeywordSearch() ? (String)score_vec.elementAt(i) : "100" %></td>
								<td style="width: 483px; text-align: left;  "><a href="resource-detail.jsp?id=<%= resource.getValue() %>" title=""><%= resource.getLabel() %><%= isNew ? "&nbsp;<img src=\"images/PttrsnUK_ICON_NEW.gif\" border=0>" : "" %></a></td>
								<td style="width:  90px; text-align: center;"><%= icon_string %></td>
								<td style="width:  90px; text-align: center;"><%= resource.getReleasedDateString() %></td>
							</tr>
						</table>
					</div>
					<!-- END Search Result -->
<%
	    }
	}
	else
	{
	    CourseBean course = (CourseBean)result;
	    
	    boolean display_course = true;
	    if (course_list != null)
		display_course = course_list.contains(course.getIdString());
	    if (display_course)
	    {
		results_found = true;
		String activity_image = "";
		if (course.getType().equals(CourseReportLister.MENTOR_ACTIVITY_TYPE_NAME))
		    activity_image = "<img src=\"images/icnActivityMNTR.gif\" width=\"17\" height=\"17\" title=\"Mentor\" alt=\"Mentor\" />";
		else if (course.getType().equals(CourseReportLister.E_LEARNING_ACTIVITY_TYPE_NAME))
		    activity_image = "<img src=\"images/icnActivityELRN.gif\" width=\"17\" height=\"17\" title=\"E-Learning\" alt=\"E-Learning\" />";
		else if (course.getType().equals(CourseReportLister.INSTRUCTOR_LED_ACTIVITY_TYPE_NAME))
		    activity_image = "<img src=\"images/icnActivityILED.gif\" width=\"17\" height=\"17\" title=\"Instructor-Led\" alt=\"Instructor-Led\" />";
		else if (course.getType().equals(CourseReportLister.ASSESSMENT_ACTIVITY_TYPE_NAME))
		    activity_image = "<img src=\"images/icnActivityASMT.gif\" width=\"17\" height=\"17\" title=\"Assessment\" alt=\"Assessment\" />";
%>
					<!-- BEGIN Search Result -->
					<div class="searchResult">
						<table cellspacing="0" cellpadding="0" border="0" summary="">
							<tr>
								<td style="width:  20px; text-align: left;  "></td>
								<td style="width:  45px; text-align: left;  "><%= resourceSearch.useKeywordSearch() ? (String)score_vec.elementAt(i) : "100" %></td>
								<td style="width: 483px; text-align: left;  "><a href="course-detail.jsp?id=<%= course.getId() %>" title=""><%= course.getLabel() %></a></a></td>
								<td style="width:  90px; text-align: center;"><%= activity_image %></td>
								<td style="width:  90px; text-align: center;"><%= course.getReleasedDateString() %></td>
							</tr>
						</table>
					</div>
					<!-- END Search Result -->
<%
	    }
	}
    }
}
if (!results_found)
{
%>
					<!-- BEGIN Search Result -->
					<div class="searchResult">
						<table cellspacing="0" cellpadding="0" border="0" summary="">
							<tr>
								<td style="text-align: left;" colspan="5">No Results Found</td>
							</tr>
						</table>
					</div>
					<!-- END Search Result -->
<%
}
%>

					<div class="horizontalRule"><img src="images/trspacer.gif" width="1" height="1" alt="" /></div>
				</div>




			</div>
			<!-- *** END Content *** -->

			<div class="horizontalRule"><img src="images/trspacer.gif" width="1" height="1" alt="" /></div>

<%@ include file="channels\channel-footer.jsp" %>

		</div>

	</body>

</html>