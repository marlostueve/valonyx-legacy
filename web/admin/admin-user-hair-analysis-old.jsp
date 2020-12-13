<%@ page language="java" buffer="12kb" contentType="text/html" import="com.badiyan.uk.beans.*, java.util.*, org.apache.commons.beanutils.*, com.badiyan.uk.online.beans.*, com.badiyan.uk.online.PDF.*" %>

<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="struts-bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="struts-html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="struts-logic" %>

<jsp:useBean id="adminCompany" class="com.badiyan.uk.online.beans.UKOnlineCompanyBean" scope="session" />
<jsp:useBean id="adminPerson" class="com.badiyan.uk.online.beans.UKOnlinePersonBean" scope="session"/>
<jsp:useBean id="adminAudience" class="com.badiyan.uk.beans.AudienceBean" scope="session" />
<jsp:useBean id="listHelper" class="com.badiyan.uk.online.beans.EcolabListHelperBean" scope="session" />
<jsp:useBean id="loginBean" class="com.badiyan.uk.online.beans.UKOnlineLoginBean" scope="session" />
<jsp:useBean id="adminCourse" class="com.badiyan.uk.beans.CourseBean" scope="session" />
<jsp:useBean id="courseSearch" class="com.badiyan.uk.online.beans.UKOnlineCourseSearchBean" scope="session"/>
<jsp:useBean id="resourceSearch" class="com.badiyan.uk.online.beans.UKOnlineResourceSearchBean" scope="session" />

<jsp:useBean id="mineralRatio" class="com.badiyan.uk.online.beans.MineralRatioBean" scope="session" />

<%@ include file="channels/channel-session.jsp" %>
<%@ include file="channels/channel-permissions.jsp" %>

<%

if (request.getParameter("id") != null)
{
    try
    {
        mineralRatio = MineralRatioBean.getMineralRatio(Integer.parseInt(request.getParameter("id")));
    }
    catch (Exception x)
    {
	x.printStackTrace();
    }
}
else
    mineralRatio = new MineralRatioBean();

session.setAttribute("mineralRatio", mineralRatio);
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

						<p class="headlineA">User Profile&nbsp;&#47;&nbsp;Hair Analysis</p>
						<p class="currentObjectName"><%= adminPerson.getLabel() %></p>
						<p>Use this screen to manage hair analysis data.</p>
						<div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>

						<struts-html:form action="/admin/userHairAnalysis" focus="dateInput">

							<input id="dummy" name="dummy" type="hidden" />

							<div class="adminItem">
								<div class="leftTM">ANALYSIS DATE</div>
								<div class="right">
									<input name="dateInput" onfocus="getDate('userHairAnalysisForm','dateInput');select();" value="<%= mineralRatio.getAnalysisDateString() %>" size="7" maxlength="100" class="inputbox" style="width: 64px; margin-right: 4px;" />
								</div>
								<div class="end"></div>
							</div>
							<div class="adminItem">
								<div class="leftTM">CA</div>
								<div class="right">
									<input name="CA" onfocus="select();" value="<%= mineralRatio.getCaString() %>" size="7" maxlength="100" class="inputbox" style="width: 64px; margin-right: 4px;" />
								</div>
								<div class="end"></div>
							</div>
							<div class="adminItem">
								<div class="leftTM">K</div>
								<div class="right">
									<input name="K" onfocus="select();" value="<%= mineralRatio.getKString() %>" size="7" maxlength="100" class="inputbox" style="width: 64px; margin-right: 4px;" />
								</div>
								<div class="end"></div>
							</div>
							<div class="adminItem">
								<div class="leftTM">CU</div>
								<div class="right">
									<input name="CU" onfocus="select();" value="<%= mineralRatio.getCuString() %>" size="7" maxlength="100" class="inputbox" style="width: 64px; margin-right: 4px;" />
								</div>
								<div class="end"></div>
							</div>
							<div class="adminItem">
								<div class="leftTM">HG</div>
								<div class="right">
									<input name="HG" onfocus="select();" value="<%= mineralRatio.getHgString() %>" size="7" maxlength="100" class="inputbox" style="width: 64px; margin-right: 4px;" />
								</div>
								<div class="end"></div>
							</div>
							<div class="adminItem">
								<div class="leftTM">CA&nbsp;&#47;&nbsp;MG</div>
								<div class="right">
									<input name="CA_MG" onfocus="select();" value="<%= mineralRatio.getCaMgString() %>" size="7" maxlength="100" class="inputbox" style="width: 64px; margin-right: 4px;" />
								</div>
								<div class="end"></div>
							</div>
							<div class="adminItem">
								<div class="leftTM">CA&nbsp;&#47;&nbsp;K</div>
								<div class="right">
									<input name="CA_K" onfocus="select();" value="<%= mineralRatio.getCaKString() %>" size="7" maxlength="100" class="inputbox" style="width: 64px; margin-right: 4px;" />
								</div>
								<div class="end"></div>
							</div>
							<div class="adminItem">
								<div class="leftTM">NA&nbsp;&#47;&nbsp;MG</div>
								<div class="right">
									<input name="NA_MG" onfocus="select();" value="<%= mineralRatio.getNaMgString() %>" size="7" maxlength="100" class="inputbox" style="width: 64px; margin-right: 4px;" />
								</div>
								<div class="end"></div>
							</div>
							<div class="adminItem">
								<div class="leftTM">NA&nbsp;&#47;&nbsp;K</div>
								<div class="right">
									<input name="NA_K" onfocus="select();" value="<%= mineralRatio.getNaKString() %>" size="7" maxlength="100" class="inputbox" style="width: 64px; margin-right: 4px;" />
								</div>
								<div class="end"></div>
							</div>
							<div class="adminItem">
								<div class="leftTM">ZN&nbsp;&#47;&nbsp;CU</div>
								<div class="right">
									<input name="ZN_CU" onfocus="select();" value="<%= mineralRatio.getZnCuString() %>" size="7" maxlength="100" class="inputbox" style="width: 64px; margin-right: 4px;" />
								</div>
								<div class="end"></div>
							</div>
							<div class="adminItem">
								<div class="leftTM">CA&nbsp;&#47;&nbsp;P</div>
								<div class="right">
									<input name="CA_P" onfocus="select();" value="<%= mineralRatio.getCaPString() %>" size="7" maxlength="100" class="inputbox" style="width: 64px; margin-right: 4px;" />
								</div>
								<div class="end"></div>
							</div>
<%
String oxidationTypeString = mineralRatio.getOxidationType();
if (oxidationTypeString == null)
    oxidationTypeString = "";
%>
							<div class="adminItem">
								<div class="leftTM">OXIDATION TYPE</div>
								<div class="right">
									<select name="oxidationType" class="select" style="width: 309px;">
										<option value="0">-- SELECT AN OXIDATION TYPE --</option>
										<option value="1"<%= oxidationTypeString.equals("MIXED") ? " selected" : "" %>>MIXED</option>
										<option value="2"<%= oxidationTypeString.equals("FAST") ? " selected" : "" %>>FAST</option>
										<option value="3"<%= oxidationTypeString.equals("SLOW") ? " selected" : "" %>>SLOW</option>
									</select>
								</div>
								<div class="end"></div>
							</div>
							<div class="adminItem">
								<div class="leftTM">NOTES</div>
								<div class="right">
									<textarea name="notes" class="textarea"  style="width: 309px;" rows="5" cols="44"><%= mineralRatio.getNotesString() %></textarea>
								</div>
								<div class="end"></div>
							</div>
							<div class="adminItem">
								<div class="leftTM"></div>
								<div class="right">
									<input class="formbutton" type="submit" value="Add&nbsp;&#47;&nbsp;Update" alt="Add&nbsp;&#47;&nbsp;Update" style="margin-right: 4px;"/><input type="hidden" name="delete_id" value="0" />
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
										<td style="width: 402px; text-align: left;  ">Hair Analyses</td>
										<td style="width:  70px; text-align: center;  "></td>
									</tr>
								</table>
							</div>
							<div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>
<%
try
{
    Iterator analyses_itr = MineralRatioBean.getMineralRatios(adminPerson).iterator();
    if (analyses_itr.hasNext())
    {
	while (analyses_itr.hasNext())
	{
	    MineralRatioBean mineral_ratio_obj = (MineralRatioBean)analyses_itr.next();
%>
							<!-- BEGIN Audience -->
							<div class="audience">
								<table cellspacing="0" cellpadding="0" border="0" summary="">
									<tr>
										<td style="width:  20px; text-align: left;  "></td>
										<td style="width: 402px; text-align: left;  "><a href="admin-user-hair-analysis.jsp?id=<%= mineral_ratio_obj.getId() %>" title=""><%= mineral_ratio_obj.getLabel() %></a></td>
										<td style="width:  70px; text-align: center;  "><a href="admin-user-hair-analysis-report.jsp?id=<%= mineral_ratio_obj.getId() %>">Reports</a>&nbsp;<a href="javascript:if (confirm('Are you sure?')) {document.forms[1].delete_id.value=<%= mineral_ratio_obj.getId() %>;document.forms[1].submit();}" title="Delete">Delete</a></td>
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
										<td style="width: 492px; text-align: left;  " colspan="3">No Hair Analyses Found</td>
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