<%@ page language="java" buffer="12kb" contentType="text/html" import="com.badiyan.uk.beans.*, java.util.*, org.apache.commons.beanutils.*, com.badiyan.uk.online.beans.*" %>

<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="struts-bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="struts-html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="struts-logic" %>

<jsp:useBean id="adminCompany" class="com.badiyan.uk.online.beans.UKOnlineCompanyBean" scope="session" />
<jsp:useBean id="adminResource" class="com.badiyan.uk.beans.ResourceBean" scope="session" />
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
        adminResource = new ResourceBean();
}
else
{
    if (request.getParameter("id") != null)
    {
        try
        {
            adminResource = ResourceBean.getResource(Integer.parseInt(request.getParameter("id")));
        }
        catch (NumberFormatException x)
        {
        }
    }
}

session.setAttribute("adminResource", adminResource);

String menuString = "1";

listHelper.setCompany(adminCompany);
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
		
		<struts-html:javascript formName="resourceNewForm" dynamicJavascript="true" staticJavascript="false"/>
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

						<p class="headlineA">Add New Resource</p>
						<p>Use this screen to add a new resource. Enter the resource name, select the type, enter a
description, and then select a contact. When you have entered all the information, click
Add New Resource to go to the Resource Details screen. You will then be able to add other
information about this resource..</p>
						<div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>

						<struts-html:form action="/admin/resourceNew" focus="nameInput" onsubmit="return validateResourceNewForm(this);">

							<input id="dummy" name="dummy" type="hidden" />

							<div class="adminItem">
								<div class="leftTM">RESOURCE NAME</div>
								<div class="right">
									<input name="nameInput" onfocus="select();" value="<%= adminResource.getLabel() %>" size="31" maxlength="100" class="inputbox" style="width: 206px;" />
								</div>
								<div class="end"></div>
							</div>
							<div class="adminItem">
								<div class="leftTM">TYPE</div>
								<div class="right">
									<label for="radio1"><img src="../images/icnResourceDOC.gif" width="17" height="17" title="Microsoft Word (DOC)" alt="Microsoft Word (DOC)" /></label>
									<input name="typeSelect" id="radio1" type="radio" value="<%= ResourceBean.WORD_RESOURCE_TYPE %>" class="crirHiddenJS" onclick="" <%= adminResource.getTypeString().equals(ResourceBean.WORD_RESOURCE_TYPE) ? "checked=\"checked\"" : "" %> />

									<label for="radio2"><img src="../images/icnResourcePPT.gif" width="17" height="17" title="Microsoft PowerPoint (PPT)" alt="Microsoft PowerPoint (PPT)" /></label>
									<input name="typeSelect" id="radio2" type="radio" value="<%= ResourceBean.POWERPOINT_RESOURCE_TYPE %>" class="crirHiddenJS" onclick="" <%= adminResource.getTypeString().equals(ResourceBean.POWERPOINT_RESOURCE_TYPE) ? "checked=\"checked\"" : "" %> />

									<label for="radio3"><img src="../images/icnResourceXLS.gif" width="17" height="17" title="Microsoft Excel (XLS)" alt="Microsoft Excel (XLS)" /></label>
									<input name="typeSelect" id="radio3" type="radio" value="<%= ResourceBean.XLS_RESOURCE_TYPE %>" class="crirHiddenJS" onclick="" <%= adminResource.getTypeString().equals(ResourceBean.XLS_RESOURCE_TYPE) ? "checked=\"checked\"" : "" %> />

									<label for="radio4"><img src="../images/icnResourcePDF.gif" width="17" height="17" title="Adobe Acrobat (PDF)" alt="Adobe Acrobat (PDF)" /></label>
									<input name="typeSelect" id="radio4" type="radio" value="<%= ResourceBean.ACROBAT_RESOURCE_TYPE %>" class="crirHiddenJS" onclick="" <%= adminResource.getTypeString().equals(ResourceBean.ACROBAT_RESOURCE_TYPE) ? "checked=\"checked\"" : "" %> />

									<br />

									<label for="radio5"><img src="../images/icnResourceSWF.gif" width="17" height="17" title="Macromedia Flash (SWF)" alt="Macromedia Flash (SWF)" /></label>
									<input name="typeSelect" id="radio5" type="radio" value="<%= ResourceBean.FLASH_RESOURCE_TYPE %>" class="crirHiddenJS" onclick="" <%= adminResource.getTypeString().equals(ResourceBean.FLASH_RESOURCE_TYPE) ? "checked=\"checked\"" : "" %> />

									<label for="radio6"><img src="../images/icnResourceWEB.gif" width="17" height="17" title="Internet" alt="Internet" /></label>
									<input name="typeSelect" id="radio6" type="radio" value="<%= ResourceBean.INTERNET_RESOURCE_TYPE %>" class="crirHiddenJS" onclick="" <%= adminResource.getTypeString().equals(ResourceBean.INTERNET_RESOURCE_TYPE) ? "checked=\"checked\"" : "" %> />

									<label for="radio7"><img src="../images/icnResourceVID.gif" width="17" height="17" title="Video" alt="Video" /></label>
									<input name="typeSelect" id="radio7" type="radio" value="<%= ResourceBean.VIDEO_RESOURCE_TYPE %>" class="crirHiddenJS" onclick="" <%= adminResource.getTypeString().equals(ResourceBean.VIDEO_RESOURCE_TYPE) ? "checked=\"checked\"" : "" %> />

									<label for="radio8"><img src="../images/icnResourceCD.gif" width="17" height="17" title="CD" alt="CD" /></label>
									<input name="typeSelect" id="radio8" type="radio" value="<%= ResourceBean.CD_RESOURCE_TYPE %>" class="crirHiddenJS" onclick="" <%= adminResource.getTypeString().equals(ResourceBean.CD_RESOURCE_TYPE) ? "checked=\"checked\"" : "" %> />
								</div>
								<div class="end"></div>
							</div>
							<div class="adminItem">
								<div class="leftTM">DESCRIPTION</div>
								<div class="right">
									<textarea name="descriptionInput" rows="5" cols="35" class="textarea" style="width: 304px;"><%= adminResource.getDescription() %></textarea>
								</div>
								<div class="end"></div>
							</div>
							<div class="adminItem">
								<div class="leftTM">CONTACT</div>
								<div class="right">
									<select name="ownerSelect" class="select" style="width: 309px;">
										<option value="0">-- SELECT A CONTACT --</option>
<%
Iterator contacts = listHelper.getPossibleResourceOwners().iterator();
while (contacts.hasNext())
{
    PersonBean possible_contact = (PersonBean)contacts.next();
%>
										<option value="<%= possible_contact.getId() %>"><%= possible_contact.getLabel() %></option>
<%
}
%>
									</select>
								</div>
								<div class="end"></div>
							</div>

							<p>&nbsp;</p>

							<p><input class="formbutton" type="submit" value="Add New Resource" alt="Add New Resource" /><input type="hidden" name="releasedDateInput" value="none"><input type="hidden" name="expiresDateInput" value="none"></p>

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