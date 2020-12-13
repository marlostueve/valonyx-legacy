<%@ page language="java" buffer="12kb" contentType="text/html" import="com.badiyan.uk.beans.*, java.util.*, java.text.*, org.apache.commons.beanutils.*, com.badiyan.uk.online.beans.*" %>


<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>Valeo Scheduler</title>
<meta name="viewport" content="width=320; initial-scale=1.0; maximum-scale=1.0; user-scalable=0;"/>
<style type="text/css" media="screen">
@import "iui/iui.css";
</style>
<script type="application/x-javascript" src="iui/iui.js"></script>

<%



UKOnlineCompanyBean company = (UKOnlineCompanyBean)UKOnlineCompanyBean.getCompany(5);
Vector practice_areas = PracticeAreaBean.getPracticeAreas(company);

//Vector practice_areas = PracticeAreaBean.getPracticeAreas(adminCompany);

Vector practitioners = null;

if (practitioners == null)
{
    //practitioners = UKOnlinePersonBean.getPractitioners(adminCompany);
    practitioners = new Vector();
    Iterator itr = practice_areas.iterator();
    while (itr.hasNext())
    {
		PracticeAreaBean practice_area = (PracticeAreaBean)itr.next();
		Iterator pa_prac_itr = practice_area.getPractitioners().iterator();
		while (pa_prac_itr.hasNext())
		{
			UKOnlinePersonBean practitioner = (UKOnlinePersonBean)pa_prac_itr.next();
			if (!practitioners.contains(practitioner) && practitioner.hasRole(UKOnlineRoleBean.PRACTITIONER_ROLE_NAME))
				practitioners.addElement(practitioner);
		}
    }
}
%>

</head>

<body>

<div class="toolbar">
  <h1 id="pageTitle"></h1>
        <a id="backButton" class="button" href="#"></a>
        <!-- <a class="button" href="#searchForm">Search</a> -->
        </div>

	<ul id="home" title="Valeo Schedule" selected="true">
<%
Iterator practitioner_itr = practitioners.iterator();
while (practitioner_itr.hasNext())
{
	UKOnlinePersonBean practitioner = (UKOnlinePersonBean)practitioner_itr.next();
%>
        <li><a href="schedule-mob-practitioner.jsp?id=<%= practitioner.getId() %>"><%= practitioner.getLabel() %></a></li>
<%
}
%>
        <li><a href="about.html" class="no_bold">About</a></li>
        <li><a href="donate.html" class="no_bold">Donate</a></li>
</ul>

<div id="preloader"></div></body></html>