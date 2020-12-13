<%@ page language="java" buffer="12kb" contentType="text/html" import="com.badiyan.uk.beans.*, java.util.*, java.text.*, org.apache.commons.beanutils.*, com.badiyan.uk.online.beans.*, com.valeo.qbpos.*, com.valeo.qb.*, com.valeo.qbpos.data.*, com.badiyan.uk.online.webservices.*, com.badiyan.torque.*, com.badiyan.uk.exceptions.*" %>

<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="struts-bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="struts-html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="struts-logic" %>

<jsp:useBean id="adminCompany" class="com.badiyan.uk.online.beans.UKOnlineCompanyBean" scope="session" />

<jsp:useBean id="listHelper" class="com.badiyan.uk.online.beans.EcolabListHelperBean" scope="session" />
<jsp:useBean id="loginBean" class="com.badiyan.uk.online.beans.UKOnlineLoginBean" scope="session" />
<jsp:useBean id="adminCourse" class="com.badiyan.uk.beans.CourseBean" scope="session" />
<jsp:useBean id="courseSearch" class="com.badiyan.uk.online.beans.UKOnlineCourseSearchBean" scope="session"/>
<jsp:useBean id="resourceSearch" class="com.badiyan.uk.online.beans.UKOnlineResourceSearchBean" scope="session" />

<jsp:useBean id="adminAppointmentType" class="com.badiyan.uk.online.beans.AppointmentTypeBean" scope="session" />

<jsp:useBean id="adminContentPage" class="com.badiyan.uk.beans.NewsItemBean" scope="session" />
<jsp:useBean id="contentKey" class="java.lang.String" scope="session" />

<%@ include file="channels/channel-session.jsp" %>
<%@ include file="channels/channel-permissions.jsp" %>


<%

if (request.getParameter("id") != null)
{
	try
	{
		adminContentPage = NewsItemBean.getNewsItemBean(Integer.parseInt(request.getParameter("id")));
		session.setAttribute("adminContentPage", adminContentPage);
	}
	catch (ObjectNotFoundException x)
	{
		//adminContentPage = new NewsItemBean();

	}
}

if (request.getParameter("property") != null)
{
	contentKey = request.getParameter("property");
	session.setAttribute("contentKey", contentKey);
}

NewsItemBean content_index_page = null;
try
{
	content_index_page = NewsItemBean.getNewsItemBean(1);
}
catch (ObjectNotFoundException x)
{
	content_index_page = new NewsItemBean();
	content_index_page.setName("index");
	content_index_page.setOwner(loginBean.getPerson());
	content_index_page.save();
}

NewsItemBean content_practitioners_page = null;
try
{
	content_practitioners_page = NewsItemBean.getNewsItemBean(2);
}
catch (ObjectNotFoundException x)
{
	content_practitioners_page = new NewsItemBean();
	content_practitioners_page.setName("practitioners");
	content_practitioners_page.setOwner(loginBean.getPerson());
	content_practitioners_page.save();
}

NewsItemBean content_why_page = null;
try
{
	content_why_page = NewsItemBean.getNewsItemBean(3);
}
catch (ObjectNotFoundException x)
{
	content_why_page = new NewsItemBean();
	content_why_page.setName("why");
	content_why_page.setOwner(loginBean.getPerson());
	content_why_page.save();
}

NewsItemBean content_testimonials_page = null;
try
{
	content_testimonials_page = NewsItemBean.getNewsItemBean(4);
}
catch (ObjectNotFoundException x)
{
	content_testimonials_page = new NewsItemBean();
	content_testimonials_page.setName("testimonials");
	content_testimonials_page.setOwner(loginBean.getPerson());
	content_testimonials_page.save();
}

NewsItemBean content_about_us_page = null;
try
{
	content_about_us_page = NewsItemBean.getNewsItemBean(5);
}
catch (ObjectNotFoundException x)
{
	content_about_us_page = new NewsItemBean();
	content_about_us_page.setName("about_us");
	content_about_us_page.setOwner(loginBean.getPerson());
	content_about_us_page.save();
}

NewsItemBean content_dummy_page = null;
try
{
	content_dummy_page = NewsItemBean.getNewsItemBean(6);
}
catch (ObjectNotFoundException x)
{
	content_dummy_page = new NewsItemBean();
	content_dummy_page.setName("dummy");
	content_dummy_page.setOwner(loginBean.getPerson());
	content_dummy_page.save();
}

NewsItemBean content_staff_page = null;
try
{
	content_staff_page = NewsItemBean.getNewsItemBean(7);
}
catch (ObjectNotFoundException x)
{
	content_staff_page = new NewsItemBean();
	content_staff_page.setName("staff");
	content_staff_page.setOwner(loginBean.getPerson());
	content_staff_page.save();
}

NewsItemBean content_chiro_page = null;
try
{
	content_chiro_page = NewsItemBean.getNewsItemBean(8);
}
catch (ObjectNotFoundException x)
{
	content_chiro_page = new NewsItemBean();
	content_chiro_page.setName("chiropractic");
	content_chiro_page.setOwner(loginBean.getPerson());
	content_chiro_page.save();
}

NewsItemBean content_naturopathy_page = null;
try
{
	content_naturopathy_page = NewsItemBean.getNewsItemBean(9);
}
catch (ObjectNotFoundException x)
{
	content_naturopathy_page = new NewsItemBean();
	content_naturopathy_page.setName("naturopathy");
	content_naturopathy_page.setOwner(loginBean.getPerson());
	content_naturopathy_page.save();
}

NewsItemBean content_EDS_Scanning_page = null;
try
{
	content_EDS_Scanning_page = NewsItemBean.getNewsItemBean(10);
}
catch (ObjectNotFoundException x)
{
	content_EDS_Scanning_page = new NewsItemBean();
	content_EDS_Scanning_page.setName("EDS-scanning");
	content_EDS_Scanning_page.setOwner(loginBean.getPerson());
	content_EDS_Scanning_page.save();
}

NewsItemBean content_nutrition_page = null;
try
{
	content_nutrition_page = NewsItemBean.getNewsItemBean(11);
}
catch (ObjectNotFoundException x)
{
	content_nutrition_page = new NewsItemBean();
	content_nutrition_page.setName("nutrition");
	content_nutrition_page.setOwner(loginBean.getPerson());
	content_nutrition_page.save();
}

NewsItemBean content_detoxification_page = null;
try
{
	content_detoxification_page = NewsItemBean.getNewsItemBean(12);
}
catch (ObjectNotFoundException x)
{
	content_detoxification_page = new NewsItemBean();
	content_detoxification_page.setName("detoxification");
	content_detoxification_page.setOwner(loginBean.getPerson());
	content_detoxification_page.save();
}

NewsItemBean content_hormone_testing_page = null;
try
{
	content_hormone_testing_page = NewsItemBean.getNewsItemBean(13);
}
catch (ObjectNotFoundException x)
{
	content_hormone_testing_page = new NewsItemBean();
	content_hormone_testing_page.setName("hormone_testing");
	content_hormone_testing_page.setOwner(loginBean.getPerson());
	content_hormone_testing_page.save();
}

NewsItemBean content_hair_analysis_page = null;
try
{
	content_hair_analysis_page = NewsItemBean.getNewsItemBean(14);
}
catch (ObjectNotFoundException x)
{
	content_hair_analysis_page = new NewsItemBean();
	content_hair_analysis_page.setName("hair_analysis");
	content_hair_analysis_page.setOwner(loginBean.getPerson());
	content_hair_analysis_page.save();
}

NewsItemBean content_muscle_testing_page = null;
try
{
	content_muscle_testing_page = NewsItemBean.getNewsItemBean(15);
}
catch (ObjectNotFoundException x)
{
	content_muscle_testing_page = new NewsItemBean();
	content_muscle_testing_page.setName("muscle_testing");
	content_muscle_testing_page.setOwner(loginBean.getPerson());
	content_muscle_testing_page.save();
}

NewsItemBean content_essential_oils_page = null;
try
{
	content_essential_oils_page = NewsItemBean.getNewsItemBean(16);
}
catch (ObjectNotFoundException x)
{
	content_essential_oils_page = new NewsItemBean();
	content_essential_oils_page.setName("essential_oils");
	content_essential_oils_page.setOwner(loginBean.getPerson());
	content_essential_oils_page.save();
}

NewsItemBean content_homeopathy_page = null;
try
{
	content_homeopathy_page = NewsItemBean.getNewsItemBean(17);
}
catch (ObjectNotFoundException x)
{
	content_homeopathy_page = new NewsItemBean();
	content_homeopathy_page.setName("homeopathy");
	content_homeopathy_page.setOwner(loginBean.getPerson());
	content_homeopathy_page.save();
}

NewsItemBean content_herbology_page = null;
try
{
	content_herbology_page = NewsItemBean.getNewsItemBean(18);
}
catch (ObjectNotFoundException x)
{
	content_herbology_page = new NewsItemBean();
	content_herbology_page.setName("herbology");
	content_herbology_page.setOwner(loginBean.getPerson());
	content_herbology_page.save();
}

NewsItemBean content_q2_page = null;
try
{
	content_q2_page = NewsItemBean.getNewsItemBean(19);
}
catch (ObjectNotFoundException x)
{
	content_q2_page = new NewsItemBean();
	content_q2_page.setName("q2");
	content_q2_page.setOwner(loginBean.getPerson());
	content_q2_page.save();
}

NewsItemBean content_massage_page = null;
try
{
	content_massage_page = NewsItemBean.getNewsItemBean(20);
}
catch (ObjectNotFoundException x)
{
	content_massage_page = new NewsItemBean();
	content_massage_page.setName("massage");
	content_massage_page.setOwner(loginBean.getPerson());
	content_massage_page.save();
}

NewsItemBean content_cranio_page = null;
try
{
	content_cranio_page = NewsItemBean.getNewsItemBean(21);
}
catch (ObjectNotFoundException x)
{
	content_cranio_page = new NewsItemBean();
	content_cranio_page.setName("cranio");
	content_cranio_page.setOwner(loginBean.getPerson());
	content_cranio_page.save();
}

NewsItemBean content_acupuncture_page = null;
try
{
	content_acupuncture_page = NewsItemBean.getNewsItemBean(22);
}
catch (ObjectNotFoundException x)
{
	content_acupuncture_page = new NewsItemBean();
	content_acupuncture_page.setName("acupuncture");
	content_acupuncture_page.setOwner(loginBean.getPerson());
	content_acupuncture_page.save();
}

NewsItemBean content_electro_acuscope_page = null;
try
{
	content_electro_acuscope_page = NewsItemBean.getNewsItemBean(23);
}
catch (ObjectNotFoundException x)
{
	content_electro_acuscope_page = new NewsItemBean();
	content_electro_acuscope_page.setName("electro_acuscope");
	content_electro_acuscope_page.setOwner(loginBean.getPerson());
	content_electro_acuscope_page.save();
}

NewsItemBean content_lifestyle_page = null;
try
{
	content_lifestyle_page = NewsItemBean.getNewsItemBean(24);
}
catch (ObjectNotFoundException x)
{
	content_lifestyle_page = new NewsItemBean();
	content_lifestyle_page.setName("lifestyle_coach");
	content_lifestyle_page.setOwner(loginBean.getPerson());
	content_lifestyle_page.save();
}

NewsItemBean content_self_defense_page = null;
try
{
	content_self_defense_page = NewsItemBean.getNewsItemBean(25);
}
catch (ObjectNotFoundException x)
{
	content_self_defense_page = new NewsItemBean();
	content_self_defense_page.setName("self_defense");
	content_self_defense_page.setOwner(loginBean.getPerson());
	content_self_defense_page.save();
}

NewsItemBean content_seminars_events_page = null;
try
{
	content_seminars_events_page = NewsItemBean.getNewsItemBean(26);
}
catch (ObjectNotFoundException x)
{
	content_seminars_events_page = new NewsItemBean();
	content_seminars_events_page.setName("seminars_events");
	content_seminars_events_page.setOwner(loginBean.getPerson());
	content_seminars_events_page.save();
}

NewsItemBean content_contact_us_page = null;
try
{
	content_contact_us_page = NewsItemBean.getNewsItemBean(27);
}
catch (ObjectNotFoundException x)
{
	content_contact_us_page = new NewsItemBean();
	content_contact_us_page.setName("contact_us");
	content_contact_us_page.setOwner(loginBean.getPerson());
	content_contact_us_page.save();
}

NewsItemBean content_links_page = null;
try
{
	content_links_page = NewsItemBean.getNewsItemBean(28);
}
catch (ObjectNotFoundException x)
{
	content_links_page = new NewsItemBean();
	content_links_page.setName("links");
	content_links_page.setOwner(loginBean.getPerson());
	content_links_page.save();
}

NewsItemBean content_newsletters_page = null;
try
{
	content_newsletters_page = NewsItemBean.getNewsItemBean(29);
}
catch (ObjectNotFoundException x)
{
	content_newsletters_page = new NewsItemBean();
	content_newsletters_page.setName("newsletters");
	content_newsletters_page.setOwner(loginBean.getPerson());
	content_newsletters_page.save();
}

NewsItemBean content_forms_page = null;
try
{
	content_forms_page = NewsItemBean.getNewsItemBean(30);
}
catch (ObjectNotFoundException x)
{
	content_forms_page = new NewsItemBean();
	content_forms_page.setName("forms");
	content_forms_page.setOwner(loginBean.getPerson());
	content_forms_page.save();
}

%>

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
	<link rel="stylesheet" type="text/css" href="../css/schedule-stylesheet.css" />

	<style type="text/css">
	/*margin and padding on body element
	  can introduce errors in determining
	  element position and are not recommended;
	  we turn them off as a foundation for YUI
	  CSS treatments. */
	body {
		margin:0;
		padding:0;
	}
	</style>


<link rel="stylesheet" type="text/css" href="https://www.valonyx.com/yui/build/menu/assets/skins/sam/menu.css" />
<link rel="stylesheet" type="text/css" href="https://www.valonyx.com/yui/build/button/assets/skins/sam/button.css" />
<link rel="stylesheet" type="text/css" href="https://www.valonyx.com/yui/build/fonts/fonts-min.css" />
<link rel="stylesheet" type="text/css" href="https://www.valonyx.com/yui/build/container/assets/skins/sam/container.css" />
<link rel="stylesheet" type="text/css" href="https://www.valonyx.com/yui/build/editor/assets/skins/sam/editor.css" />
	<link rel="stylesheet" type="text/css" href="https://www.valonyx.com/yui/build/assets/skins/sam/layout.css">

<script type="text/javascript" src="https://www.valonyx.com/yui/build/yahoo-dom-event/yahoo-dom-event.js"></script>
<script type="text/javascript" src="https://www.valonyx.com/yui/build/animation/animation-min.js"></script>
<script type="text/javascript" src="https://www.valonyx.com/yui/build/element/element-min.js"></script>
<script type="text/javascript" src="https://www.valonyx.com/yui/build/container/container-min.js"></script>
<script type="text/javascript" src="https://www.valonyx.com/yui/build/menu/menu-min.js"></script>
<script type="text/javascript" src="https://www.valonyx.com/yui/build/button/button-min.js"></script>
<script type="text/javascript" src="https://www.valonyx.com/yui/build/editor/editor-min.js"></script>
	<script src="https://www.valonyx.com/yui/build/layout/layout-min.js"></script>



	<script type="text/javascript">

    var scheduleArray = new Array();
    var idArray = new Array();
    var scheduleIndex = 0;
    var typeArray = new Array();
    var httpRequest;
    var request_NextDay;

    var edit = 0;

    function processCommand(command, parameter)
    {
	if (window.ActiveXObject)
	    httpRequest = new ActiveXObject("Microsoft.XMLHTTP");
	else if (window.XMLHttpRequest)
	    httpRequest = new XMLHttpRequest();

	httpRequest.open("POST", '<%= CUBean.getProperty("cu.webDomain")%>/ClientServlet.html', true);
	httpRequest.setRequestHeader('Content-Type','application/x-www-form-urlencoded');
	httpRequest.onreadystatechange = function() {processCommandEvent(); } ;
	eval("httpRequest.send('command=" + command + "&parameter=" + parameter + "')");

	return true;
    }

    function processCommandEvent()
    {
	if (httpRequest.readyState == 4)
	{
	    if (httpRequest.status == 200)
	    {
		if (httpRequest.responseXML.getElementsByTagName("status").length > 0)
		{
		    var xml_response = httpRequest.responseXML.getElementsByTagName("status")[0];

		    document.getElementById('today_date').firstChild.nodeValue = xml_response.getElementsByTagName("date")[0].childNodes[0].nodeValue;
		    document.getElementById('new_appt_date').firstChild.nodeValue = xml_response.getElementsByTagName("date")[0].childNodes[0].nodeValue;
		    //document.getElementById('newAppointmentDateInput').value = xml_response.getElementsByTagName("date")[0].childNodes[0].nodeValue;
		    document.forms[0].newAppointmentDateInput.value = xml_response.getElementsByTagName("date")[0].childNodes[0].nodeValue;

		    buildScheduleArray(xml_response);
		}
		else if (httpRequest.responseXML.getElementsByTagName("clients").length > 0)
		{
		    //alert(httpRequest.responseXML.getElementsByTagName("clients").length);
		    var xml_response = httpRequest.responseXML.getElementsByTagName("clients")[0];
		    showPeople(xml_response);
		}
		else if (httpRequest.responseXML.getElementsByTagName("person").length > 0)
		{
		    //alert(httpRequest.responseXML.getElementsByTagName("clients").length);
		    var xml_response = httpRequest.responseXML.getElementsByTagName("person")[0];
		    //showPeople(xml_response);

		    showPerson(xml_response);
		}
		else if (httpRequest.responseXML.getElementsByTagName("error").length > 0)
		{
		    var xml_response = httpRequest.responseXML.getElementsByTagName("error")[0];
		    alert("Error : " + xml_response.childNodes[0].nodeValue);
		}
	    }
	    else
	    {
		alert("Error : " + httpRequest.status + " : " + httpRequest.statusText);
	    }
	}
    }

	</script>




<script>

(function() {
    var Dom = YAHOO.util.Dom,
        Event = YAHOO.util.Event;

    Event.onDOMReady(function() {
        var layout = new YAHOO.widget.Layout({
                minWidth: 750,
                units: [
		{ position: 'top', height: 21, resize: false, body: 'top1' },
                { position: 'center', body: 'center1', scroll: true, minWidth: 400 }
            ]
        });
        layout.render();

    });

})();
</script>

<%@ include file="..\channels\channel-head-javascript.jsp" %>

	<title>Valonyx</title>

</head>

<body class=" yui-skin-sam" onload="javascript:initErrors();" >

<style>
    .yui-skin-sam .yui-toolbar-container .yui-toolbar-editcode span.yui-toolbar-icon {
        background-image: url( http://developer.yahoo.com/yui/examples/editor/assets/html_editor.gif );
        background-position: 0 1px;
        left: 5px;
    }
    .yui-skin-sam .yui-toolbar-container .yui-button-editcode-selected span.yui-toolbar-icon {
        background-image: url( http://developer.yahoo.com/yui/examples/editor/assets/html_editor.gif );
        background-position: 0 1px;
        left: 5px;
    }
    .editor-hidden {
        visibility: hidden;
        top: -9999px;
        left: -9999px;
        position: absolute;
    }
    textarea {
        border: 0;
        margin: 0;
        padding: 0;
    }
</style>
<style>
.yui-toolbar-group-insertitem {
    *width: auto;
}
</style>

<div id="top1">
    <%@ include file="channels/channel-schedule-menu.jsp" %>
</div>
<div id="center1" style="background-color: white; height: 100%;">
    <div id="content">

	<div class="content_TextBlock">
		<p class="headline"><%= adminCompany.getLabel() %> Administration - Site Content</p>
		<div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>
	</div>

				<div class="content_Administration">

<%@ include file="channels\channel-setup-menu.jsp" %>

				<div class="main">

						<p>Use this screen to manage site content.</p>
						<div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>

						<struts-html:form action="/admin/siteContent" >

							<input id="dummy" name="dummy" type="hidden" />

							<div class="adminItem">
								<div class="leftTM">PAGE</div>
								<div class="right">
									<select name="page_select" class="select" style="width: 309px;" onchange="document.location.href='site-content.jsp?id=' + this.value;">
										<option value="0">-- SELECT A PAGE --</option>
										<option value="<%= content_index_page.getValue() %>"<%= (adminContentPage.equals(content_index_page)) ? " selected" : "" %>><%= content_index_page.getLabel() %></option>
										<option value="<%= content_practitioners_page.getValue() %>"<%= (adminContentPage.equals(content_practitioners_page)) ? " selected" : "" %>><%= content_practitioners_page.getLabel() %></option>
										<option value="<%= content_why_page.getValue() %>"<%= (adminContentPage.equals(content_why_page)) ? " selected" : "" %>><%= content_why_page.getLabel() %></option>
										<option value="<%= content_testimonials_page.getValue() %>"<%= (adminContentPage.equals(content_testimonials_page)) ? " selected" : "" %>><%= content_testimonials_page.getLabel() %></option>
										<option value="<%= content_about_us_page.getValue() %>"<%= (adminContentPage.equals(content_about_us_page)) ? " selected" : "" %>><%= content_about_us_page.getLabel() %></option>
										<option value="<%= content_staff_page.getValue() %>"<%= (adminContentPage.equals(content_staff_page)) ? " selected" : "" %>><%= content_staff_page.getLabel() %></option>
										<option value="<%= content_chiro_page.getValue() %>"<%= (adminContentPage.equals(content_chiro_page)) ? " selected" : "" %>><%= content_chiro_page.getLabel() %></option>
										<option value="<%= content_naturopathy_page.getValue() %>"<%= (adminContentPage.equals(content_naturopathy_page)) ? " selected" : "" %>><%= content_naturopathy_page.getLabel() %></option>
										<option value="<%= content_EDS_Scanning_page.getValue() %>"<%= (adminContentPage.equals(content_EDS_Scanning_page)) ? " selected" : "" %>><%= content_EDS_Scanning_page.getLabel() %></option>
										<option value="<%= content_nutrition_page.getValue() %>"<%= (adminContentPage.equals(content_nutrition_page)) ? " selected" : "" %>><%= content_nutrition_page.getLabel() %></option>
										<option value="<%= content_detoxification_page.getValue() %>"<%= (adminContentPage.equals(content_detoxification_page)) ? " selected" : "" %>><%= content_detoxification_page.getLabel() %></option>
										<option value="<%= content_hormone_testing_page.getValue() %>"<%= (adminContentPage.equals(content_hormone_testing_page)) ? " selected" : "" %>><%= content_hormone_testing_page.getLabel() %></option>
										<option value="<%= content_hair_analysis_page.getValue() %>"<%= (adminContentPage.equals(content_hair_analysis_page)) ? " selected" : "" %>><%= content_hair_analysis_page.getLabel() %></option>
										<option value="<%= content_muscle_testing_page.getValue() %>"<%= (adminContentPage.equals(content_muscle_testing_page)) ? " selected" : "" %>><%= content_muscle_testing_page.getLabel() %></option>
										<option value="<%= content_essential_oils_page.getValue() %>"<%= (adminContentPage.equals(content_essential_oils_page)) ? " selected" : "" %>><%= content_essential_oils_page.getLabel() %></option>
										<option value="<%= content_homeopathy_page.getValue() %>"<%= (adminContentPage.equals(content_homeopathy_page)) ? " selected" : "" %>><%= content_homeopathy_page.getLabel() %></option>
										<option value="<%= content_herbology_page.getValue() %>"<%= (adminContentPage.equals(content_herbology_page)) ? " selected" : "" %>><%= content_herbology_page.getLabel() %></option>
										<option value="<%= content_q2_page.getValue() %>"<%= (adminContentPage.equals(content_q2_page)) ? " selected" : "" %>><%= content_q2_page.getLabel() %></option>
										<option value="<%= content_massage_page.getValue() %>"<%= (adminContentPage.equals(content_massage_page)) ? " selected" : "" %>><%= content_massage_page.getLabel() %></option>
										<option value="<%= content_cranio_page.getValue() %>"<%= (adminContentPage.equals(content_cranio_page)) ? " selected" : "" %>><%= content_cranio_page.getLabel() %></option>
										<option value="<%= content_acupuncture_page.getValue() %>"<%= (adminContentPage.equals(content_acupuncture_page)) ? " selected" : "" %>><%= content_acupuncture_page.getLabel() %></option>
										<option value="<%= content_electro_acuscope_page.getValue() %>"<%= (adminContentPage.equals(content_electro_acuscope_page)) ? " selected" : "" %>><%= content_electro_acuscope_page.getLabel() %></option>
										<option value="<%= content_lifestyle_page.getValue() %>"<%= (adminContentPage.equals(content_lifestyle_page)) ? " selected" : "" %>><%= content_lifestyle_page.getLabel() %></option>
										<option value="<%= content_self_defense_page.getValue() %>"<%= (adminContentPage.equals(content_self_defense_page)) ? " selected" : "" %>><%= content_self_defense_page.getLabel() %></option>
										<option value="<%= content_seminars_events_page.getValue() %>"<%= (adminContentPage.equals(content_seminars_events_page)) ? " selected" : "" %>><%= content_seminars_events_page.getLabel() %></option>
										<option value="<%= content_contact_us_page.getValue() %>"<%= (adminContentPage.equals(content_contact_us_page)) ? " selected" : "" %>><%= content_contact_us_page.getLabel() %></option>
										<option value="<%= content_links_page.getValue() %>"<%= (adminContentPage.equals(content_links_page)) ? " selected" : "" %>><%= content_links_page.getLabel() %></option>
										<option value="<%= content_newsletters_page.getValue() %>"<%= (adminContentPage.equals(content_newsletters_page)) ? " selected" : "" %>><%= content_newsletters_page.getLabel() %></option>
										<option value="<%= content_forms_page.getValue() %>"<%= (adminContentPage.equals(content_forms_page)) ? " selected" : "" %>><%= content_forms_page.getLabel() %></option>
									</select>
								</div>
								<div class="end"></div>
							</div>

							<div class="adminItem">
								<div class="leftTM">PROPERTY</div>
								<div class="right">
									<select name="propertySelect" class="select" style="width: 309px;" onchange="document.location.href='site-content.jsp?property=' + this.value;">
										<option value="0">-- SELECT A PROPERTY --</option>
<%
if (adminContentPage.equals(content_index_page))
{
%>
										<option value="welcome"<%= (contentKey.equals("welcome")) ? " selected" : "" %>>welcome</option>
										<option value="channel_url"<%= (contentKey.equals("channel_url")) ? " selected" : "" %>>channel_url</option>
										<option value="news_article_title"<%= (contentKey.equals("news_article_title")) ? " selected" : "" %>>news_article_title</option>
										<option value="news_article"<%= (contentKey.equals("news_article")) ? " selected" : "" %>>news_article</option>
										<option value="small_box_header_1"<%= (contentKey.equals("small_box_header_1")) ? " selected" : "" %>>small_box_header_1</option>
										<option value="small_box_header_1_item_1"<%= (contentKey.equals("small_box_header_1_item_1")) ? " selected" : "" %>>small_box_header_1_item_1</option>
										<option value="small_box_header_1_item_2"<%= (contentKey.equals("small_box_header_1_item_2")) ? " selected" : "" %>>small_box_header_1_item_2</option>
										<option value="small_box_header_1_item_3"<%= (contentKey.equals("small_box_header_1_item_3")) ? " selected" : "" %>>small_box_header_1_item_3</option>
										<option value="small_box_header_1_item_4"<%= (contentKey.equals("small_box_header_1_item_4")) ? " selected" : "" %>>small_box_header_1_item_4</option>
										<option value="small_box_header_1_item_5"<%= (contentKey.equals("small_box_header_1_item_5")) ? " selected" : "" %>>small_box_header_1_item_5</option>
										<option value="small_box_header_1_item_6"<%= (contentKey.equals("small_box_header_1_item_6")) ? " selected" : "" %>>small_box_header_1_item_6</option>
										<option value="small_box_header_2"<%= (contentKey.equals("small_box_header_2")) ? " selected" : "" %>>small_box_header_2</option>
										<option value="small_box_header_2_item_1"<%= (contentKey.equals("small_box_header_2_item_1")) ? " selected" : "" %>>small_box_header_2_item_1</option>
										<option value="small_box_header_2_item_2"<%= (contentKey.equals("small_box_header_2_item_2")) ? " selected" : "" %>>small_box_header_2_item_2</option>
										<option value="small_box_header_2_item_3"<%= (contentKey.equals("small_box_header_2_item_3")) ? " selected" : "" %>>small_box_header_2_item_3</option>
										<option value="small_box_header_2_item_4"<%= (contentKey.equals("small_box_header_2_item_4")) ? " selected" : "" %>>small_box_header_2_item_4</option>
										<option value="small_box_header_2_item_5"<%= (contentKey.equals("small_box_header_2_item_5")) ? " selected" : "" %>>small_box_header_2_item_5</option>
										<option value="small_box_header_2_item_6"<%= (contentKey.equals("small_box_header_2_item_6")) ? " selected" : "" %>>small_box_header_2_item_6</option>
										<option value="small_box_header_3"<%= (contentKey.equals("small_box_header_3")) ? " selected" : "" %>>small_box_header_3</option>
										<option value="small_box_header_3_item_1"<%= (contentKey.equals("small_box_header_3_item_1")) ? " selected" : "" %>>small_box_header_3_item_1</option>
										<option value="small_box_header_3_item_2"<%= (contentKey.equals("small_box_header_3_item_2")) ? " selected" : "" %>>small_box_header_3_item_2</option>
										<option value="small_box_header_3_item_3"<%= (contentKey.equals("small_box_header_3_item_3")) ? " selected" : "" %>>small_box_header_3_item_3</option>
										<option value="small_box_header_3_item_4"<%= (contentKey.equals("small_box_header_3_item_4")) ? " selected" : "" %>>small_box_header_3_item_4</option>
										<option value="small_box_header_3_item_5"<%= (contentKey.equals("small_box_header_3_item_5")) ? " selected" : "" %>>small_box_header_3_item_5</option>
										<option value="small_box_header_3_item_6"<%= (contentKey.equals("small_box_header_3_item_6")) ? " selected" : "" %>>small_box_header_3_item_6</option>
<%
}
else if (adminContentPage.equals(content_practitioners_page))
{
%>
										<option value="practitioner_name_1"<%= (contentKey.equals("practitioner_name_1")) ? " selected" : "" %>>practitioner_name_1</option>
										<option value="practitioner_content_1"<%= (contentKey.equals("practitioner_content_1")) ? " selected" : "" %>>practitioner_content_1</option>
										<option value="practitioner_name_2"<%= (contentKey.equals("practitioner_name_2")) ? " selected" : "" %>>practitioner_name_2</option>
										<option value="practitioner_content_2"<%= (contentKey.equals("practitioner_content_2")) ? " selected" : "" %>>practitioner_content_2</option>
										<option value="practitioner_name_3"<%= (contentKey.equals("practitioner_name_3")) ? " selected" : "" %>>practitioner_name_3</option>
										<option value="practitioner_content_3"<%= (contentKey.equals("practitioner_content_3")) ? " selected" : "" %>>practitioner_content_3</option>
										<option value="practitioner_name_4"<%= (contentKey.equals("practitioner_name_4")) ? " selected" : "" %>>practitioner_name_4</option>
										<option value="practitioner_content_4"<%= (contentKey.equals("practitioner_content_4")) ? " selected" : "" %>>practitioner_content_4</option>
										<option value="practitioner_name_5"<%= (contentKey.equals("practitioner_name_5")) ? " selected" : "" %>>practitioner_name_5</option>
										<option value="practitioner_content_5"<%= (contentKey.equals("practitioner_content_5")) ? " selected" : "" %>>practitioner_content_5</option>
										<option value="practitioner_name_6"<%= (contentKey.equals("practitioner_name_6")) ? " selected" : "" %>>practitioner_name_6</option>
										<option value="practitioner_content_6"<%= (contentKey.equals("practitioner_content_6")) ? " selected" : "" %>>practitioner_content_6</option>
										<option value="practitioner_name_7"<%= (contentKey.equals("practitioner_name_7")) ? " selected" : "" %>>practitioner_name_7</option>
										<option value="practitioner_content_7"<%= (contentKey.equals("practitioner_content_7")) ? " selected" : "" %>>practitioner_content_7</option>
										<option value="practitioner_name_8"<%= (contentKey.equals("practitioner_name_8")) ? " selected" : "" %>>practitioner_name_8</option>
										<option value="practitioner_content_8"<%= (contentKey.equals("practitioner_content_8")) ? " selected" : "" %>>practitioner_content_8</option>
										<option value="practitioner_name_9"<%= (contentKey.equals("practitioner_name_9")) ? " selected" : "" %>>practitioner_name_9</option>
										<option value="practitioner_content_9"<%= (contentKey.equals("practitioner_content_9")) ? " selected" : "" %>>practitioner_content_9</option>
<%
}
else if (adminContentPage.equals(content_why_page))
{
%>
										<option value="label_1"<%= (contentKey.equals("label_1")) ? " selected" : "" %>>label_1</option>
										<option value="content_1"<%= (contentKey.equals("content_1")) ? " selected" : "" %>>content_1</option>
<%
}
else if (adminContentPage.equals(content_testimonials_page))
{
%>
										<option value="testimonial_label_1"<%= (contentKey.equals("testimonial_label_1")) ? " selected" : "" %>>testimonial_label_1</option>
										<option value="testimonial_content_1"<%= (contentKey.equals("testimonial_content_1")) ? " selected" : "" %>>testimonial_content_1</option>
										<option value="testimonial_label_2"<%= (contentKey.equals("testimonial_label_2")) ? " selected" : "" %>>testimonial_label_2</option>
										<option value="testimonial_content_2"<%= (contentKey.equals("testimonial_content_2")) ? " selected" : "" %>>testimonial_content_2</option>
										<option value="testimonial_label_3"<%= (contentKey.equals("testimonial_label_3")) ? " selected" : "" %>>testimonial_label_3</option>
										<option value="testimonial_content_3"<%= (contentKey.equals("testimonial_content_3")) ? " selected" : "" %>>testimonial_content_3</option>
										<option value="testimonial_label_4"<%= (contentKey.equals("testimonial_label_4")) ? " selected" : "" %>>testimonial_label_4</option>
										<option value="testimonial_content_4"<%= (contentKey.equals("testimonial_content_4")) ? " selected" : "" %>>testimonial_content_4</option>
										<option value="testimonial_label_5"<%= (contentKey.equals("testimonial_label_5")) ? " selected" : "" %>>testimonial_label_5</option>
										<option value="testimonial_content_5"<%= (contentKey.equals("testimonial_content_5")) ? " selected" : "" %>>testimonial_content_5</option>
										<option value="testimonial_label_6"<%= (contentKey.equals("testimonial_label_6")) ? " selected" : "" %>>testimonial_label_6</option>
										<option value="testimonial_content_6"<%= (contentKey.equals("testimonial_content_6")) ? " selected" : "" %>>testimonial_content_6</option>
										<option value="testimonial_label_7"<%= (contentKey.equals("testimonial_label_7")) ? " selected" : "" %>>testimonial_label_7</option>
										<option value="testimonial_content_7"<%= (contentKey.equals("testimonial_content_7")) ? " selected" : "" %>>testimonial_content_7</option>
										<option value="testimonial_label_8"<%= (contentKey.equals("testimonial_label_8")) ? " selected" : "" %>>testimonial_label_8</option>
										<option value="testimonial_content_8"<%= (contentKey.equals("testimonial_content_8")) ? " selected" : "" %>>testimonial_content_8</option>
										<option value="testimonial_label_9"<%= (contentKey.equals("testimonial_label_9")) ? " selected" : "" %>>testimonial_label_9</option>
										<option value="testimonial_content_9"<%= (contentKey.equals("testimonial_content_9")) ? " selected" : "" %>>testimonial_content_9</option>
										<option value="testimonial_label_10"<%= (contentKey.equals("testimonial_label_10")) ? " selected" : "" %>>testimonial_label_10</option>
										<option value="testimonial_content_10"<%= (contentKey.equals("testimonial_content_10")) ? " selected" : "" %>>testimonial_content_10</option>
										<option value="testimonial_label_11"<%= (contentKey.equals("testimonial_label_11")) ? " selected" : "" %>>testimonial_label_11</option>
										<option value="testimonial_content_11"<%= (contentKey.equals("testimonial_content_11")) ? " selected" : "" %>>testimonial_content_11</option>
										<option value="testimonial_label_12"<%= (contentKey.equals("testimonial_label_12")) ? " selected" : "" %>>testimonial_label_12</option>
										<option value="testimonial_content_12"<%= (contentKey.equals("testimonial_content_12")) ? " selected" : "" %>>testimonial_content_12</option>
										<option value="testimonial_label_13"<%= (contentKey.equals("testimonial_label_13")) ? " selected" : "" %>>testimonial_label_13</option>
										<option value="testimonial_content_13"<%= (contentKey.equals("testimonial_content_13")) ? " selected" : "" %>>testimonial_content_13</option>
										<option value="testimonial_label_14"<%= (contentKey.equals("testimonial_label_14")) ? " selected" : "" %>>testimonial_label_14</option>
										<option value="testimonial_content_14"<%= (contentKey.equals("testimonial_content_14")) ? " selected" : "" %>>testimonial_content_14</option>
										<option value="testimonial_label_15"<%= (contentKey.equals("testimonial_label_15")) ? " selected" : "" %>>testimonial_label_15</option>
										<option value="testimonial_content_15"<%= (contentKey.equals("testimonial_content_15")) ? " selected" : "" %>>testimonial_content_15</option>
										<option value="testimonial_label_16"<%= (contentKey.equals("testimonial_label_16")) ? " selected" : "" %>>testimonial_label_16</option>
										<option value="testimonial_content_16"<%= (contentKey.equals("testimonial_content_16")) ? " selected" : "" %>>testimonial_content_16</option>
<%
}
else if (adminContentPage.equals(content_about_us_page))
{
%>
										<option value="label_1"<%= (contentKey.equals("label_1")) ? " selected" : "" %>>label_1</option>
										<option value="content_1"<%= (contentKey.equals("content_1")) ? " selected" : "" %>>content_1</option>
<%
}
else if (adminContentPage.equals(content_staff_page))
{
%>
										<option value="staff_name_1"<%= (contentKey.equals("staff_name_1")) ? " selected" : "" %>>staff_name_1</option>
										<option value="staff_content_1"<%= (contentKey.equals("staff_content_1")) ? " selected" : "" %>>staff_content_1</option>
										<option value="staff_name_2"<%= (contentKey.equals("staff_name_2")) ? " selected" : "" %>>staff_name_2</option>
										<option value="staff_content_2"<%= (contentKey.equals("staff_content_2")) ? " selected" : "" %>>staff_content_2</option>
										<option value="staff_name_3"<%= (contentKey.equals("staff_name_3")) ? " selected" : "" %>>staff_name_3</option>
										<option value="staff_content_3"<%= (contentKey.equals("staff_content_3")) ? " selected" : "" %>>staff_content_3</option>
										<option value="staff_name_4"<%= (contentKey.equals("staff_name_4")) ? " selected" : "" %>>staff_name_4</option>
										<option value="staff_content_4"<%= (contentKey.equals("staff_content_4")) ? " selected" : "" %>>staff_content_4</option>
										<option value="staff_name_5"<%= (contentKey.equals("staff_name_5")) ? " selected" : "" %>>staff_name_5</option>
										<option value="staff_content_5"<%= (contentKey.equals("staff_content_5")) ? " selected" : "" %>>staff_content_5</option>
										<option value="staff_name_6"<%= (contentKey.equals("staff_name_6")) ? " selected" : "" %>>staff_name_6</option>
										<option value="staff_content_6"<%= (contentKey.equals("staff_content_6")) ? " selected" : "" %>>staff_content_6</option>
										<option value="staff_name_7"<%= (contentKey.equals("staff_name_7")) ? " selected" : "" %>>staff_name_7</option>
										<option value="staff_content_7"<%= (contentKey.equals("staff_content_7")) ? " selected" : "" %>>staff_content_7</option>
										<option value="staff_name_8"<%= (contentKey.equals("staff_name_8")) ? " selected" : "" %>>staff_name_8</option>
										<option value="staff_content_8"<%= (contentKey.equals("staff_content_8")) ? " selected" : "" %>>staff_content_8</option>
										<option value="staff_name_9"<%= (contentKey.equals("staff_name_9")) ? " selected" : "" %>>staff_name_9</option>
										<option value="staff_content_9"<%= (contentKey.equals("staff_content_9")) ? " selected" : "" %>>staff_content_9</option>
<%
}
else if (adminContentPage.equals(content_chiro_page))
{
%>
										<option value="label_1"<%= (contentKey.equals("label_1")) ? " selected" : "" %>>label_1</option>
										<option value="content_1"<%= (contentKey.equals("content_1")) ? " selected" : "" %>>content_1</option>
										<option value="label_2"<%= (contentKey.equals("label_2")) ? " selected" : "" %>>label_2</option>
										<option value="content_2"<%= (contentKey.equals("content_2")) ? " selected" : "" %>>content_2</option>
										<option value="label_3"<%= (contentKey.equals("label_3")) ? " selected" : "" %>>label_3</option>
										<option value="content_3"<%= (contentKey.equals("content_3")) ? " selected" : "" %>>content_3</option>
										<option value="label_4"<%= (contentKey.equals("label_4")) ? " selected" : "" %>>label_4</option>
										<option value="content_4"<%= (contentKey.equals("content_4")) ? " selected" : "" %>>content_4</option>
										<option value="label_5"<%= (contentKey.equals("label_5")) ? " selected" : "" %>>label_5</option>
										<option value="content_5"<%= (contentKey.equals("content_5")) ? " selected" : "" %>>content_5</option>
										<option value="label_6"<%= (contentKey.equals("label_6")) ? " selected" : "" %>>label_6</option>
										<option value="content_6"<%= (contentKey.equals("content_6")) ? " selected" : "" %>>content_6</option>
										<option value="label_7"<%= (contentKey.equals("label_7")) ? " selected" : "" %>>label_7</option>
										<option value="content_7"<%= (contentKey.equals("content_7")) ? " selected" : "" %>>content_7</option>
										<option value="label_8"<%= (contentKey.equals("label_8")) ? " selected" : "" %>>label_8</option>
										<option value="content_8"<%= (contentKey.equals("content_8")) ? " selected" : "" %>>content_8</option>
										<option value="label_9"<%= (contentKey.equals("label_9")) ? " selected" : "" %>>label_9</option>
										<option value="content_9"<%= (contentKey.equals("content_9")) ? " selected" : "" %>>content_9</option>
<%
}
else if (adminContentPage.equals(content_naturopathy_page))
{
%>
										<option value="label_1"<%= (contentKey.equals("label_1")) ? " selected" : "" %>>label_1</option>
										<option value="content_1"<%= (contentKey.equals("content_1")) ? " selected" : "" %>>content_1</option>
										<option value="label_2"<%= (contentKey.equals("label_2")) ? " selected" : "" %>>label_2</option>
										<option value="content_2"<%= (contentKey.equals("content_2")) ? " selected" : "" %>>content_2</option>
										<option value="label_3"<%= (contentKey.equals("label_3")) ? " selected" : "" %>>label_3</option>
										<option value="content_3"<%= (contentKey.equals("content_3")) ? " selected" : "" %>>content_3</option>
										<option value="label_4"<%= (contentKey.equals("label_4")) ? " selected" : "" %>>label_4</option>
										<option value="content_4"<%= (contentKey.equals("content_4")) ? " selected" : "" %>>content_4</option>
										<option value="label_5"<%= (contentKey.equals("label_5")) ? " selected" : "" %>>label_5</option>
										<option value="content_5"<%= (contentKey.equals("content_5")) ? " selected" : "" %>>content_5</option>
										<option value="label_6"<%= (contentKey.equals("label_6")) ? " selected" : "" %>>label_6</option>
										<option value="content_6"<%= (contentKey.equals("content_6")) ? " selected" : "" %>>content_6</option>
										<option value="label_7"<%= (contentKey.equals("label_7")) ? " selected" : "" %>>label_7</option>
										<option value="content_7"<%= (contentKey.equals("content_7")) ? " selected" : "" %>>content_7</option>
										<option value="label_8"<%= (contentKey.equals("label_8")) ? " selected" : "" %>>label_8</option>
										<option value="content_8"<%= (contentKey.equals("content_8")) ? " selected" : "" %>>content_8</option>
										<option value="label_9"<%= (contentKey.equals("label_9")) ? " selected" : "" %>>label_9</option>
										<option value="content_9"<%= (contentKey.equals("content_9")) ? " selected" : "" %>>content_9</option>
<%
}
else if (adminContentPage.equals(content_EDS_Scanning_page))
{
%>
										<option value="label_1"<%= (contentKey.equals("label_1")) ? " selected" : "" %>>label_1</option>
										<option value="content_1"<%= (contentKey.equals("content_1")) ? " selected" : "" %>>content_1</option>
										<option value="label_2"<%= (contentKey.equals("label_2")) ? " selected" : "" %>>label_2</option>
										<option value="content_2"<%= (contentKey.equals("content_2")) ? " selected" : "" %>>content_2</option>
										<option value="label_3"<%= (contentKey.equals("label_3")) ? " selected" : "" %>>label_3</option>
										<option value="content_3"<%= (contentKey.equals("content_3")) ? " selected" : "" %>>content_3</option>
										<option value="label_4"<%= (contentKey.equals("label_4")) ? " selected" : "" %>>label_4</option>
										<option value="content_4"<%= (contentKey.equals("content_4")) ? " selected" : "" %>>content_4</option>
										<option value="label_5"<%= (contentKey.equals("label_5")) ? " selected" : "" %>>label_5</option>
										<option value="content_5"<%= (contentKey.equals("content_5")) ? " selected" : "" %>>content_5</option>
										<option value="label_6"<%= (contentKey.equals("label_6")) ? " selected" : "" %>>label_6</option>
										<option value="content_6"<%= (contentKey.equals("content_6")) ? " selected" : "" %>>content_6</option>
										<option value="label_7"<%= (contentKey.equals("label_7")) ? " selected" : "" %>>label_7</option>
										<option value="content_7"<%= (contentKey.equals("content_7")) ? " selected" : "" %>>content_7</option>
										<option value="label_8"<%= (contentKey.equals("label_8")) ? " selected" : "" %>>label_8</option>
										<option value="content_8"<%= (contentKey.equals("content_8")) ? " selected" : "" %>>content_8</option>
										<option value="label_9"<%= (contentKey.equals("label_9")) ? " selected" : "" %>>label_9</option>
										<option value="content_9"<%= (contentKey.equals("content_9")) ? " selected" : "" %>>content_9</option>
<%
}
else if (adminContentPage.equals(content_nutrition_page))
{
%>
										<option value="label_1"<%= (contentKey.equals("label_1")) ? " selected" : "" %>>label_1</option>
										<option value="content_1"<%= (contentKey.equals("content_1")) ? " selected" : "" %>>content_1</option>
										<option value="label_2"<%= (contentKey.equals("label_2")) ? " selected" : "" %>>label_2</option>
										<option value="content_2"<%= (contentKey.equals("content_2")) ? " selected" : "" %>>content_2</option>
										<option value="label_3"<%= (contentKey.equals("label_3")) ? " selected" : "" %>>label_3</option>
										<option value="content_3"<%= (contentKey.equals("content_3")) ? " selected" : "" %>>content_3</option>
										<option value="label_4"<%= (contentKey.equals("label_4")) ? " selected" : "" %>>label_4</option>
										<option value="content_4"<%= (contentKey.equals("content_4")) ? " selected" : "" %>>content_4</option>
										<option value="label_5"<%= (contentKey.equals("label_5")) ? " selected" : "" %>>label_5</option>
										<option value="content_5"<%= (contentKey.equals("content_5")) ? " selected" : "" %>>content_5</option>
										<option value="label_6"<%= (contentKey.equals("label_6")) ? " selected" : "" %>>label_6</option>
										<option value="content_6"<%= (contentKey.equals("content_6")) ? " selected" : "" %>>content_6</option>
										<option value="label_7"<%= (contentKey.equals("label_7")) ? " selected" : "" %>>label_7</option>
										<option value="content_7"<%= (contentKey.equals("content_7")) ? " selected" : "" %>>content_7</option>
										<option value="label_8"<%= (contentKey.equals("label_8")) ? " selected" : "" %>>label_8</option>
										<option value="content_8"<%= (contentKey.equals("content_8")) ? " selected" : "" %>>content_8</option>
										<option value="label_9"<%= (contentKey.equals("label_9")) ? " selected" : "" %>>label_9</option>
										<option value="content_9"<%= (contentKey.equals("content_9")) ? " selected" : "" %>>content_9</option>
<%
}
else if (adminContentPage.equals(content_detoxification_page))
{
%>
										<option value="label_1"<%= (contentKey.equals("label_1")) ? " selected" : "" %>>label_1</option>
										<option value="content_1"<%= (contentKey.equals("content_1")) ? " selected" : "" %>>content_1</option>
										<option value="label_2"<%= (contentKey.equals("label_2")) ? " selected" : "" %>>label_2</option>
										<option value="content_2"<%= (contentKey.equals("content_2")) ? " selected" : "" %>>content_2</option>
										<option value="label_3"<%= (contentKey.equals("label_3")) ? " selected" : "" %>>label_3</option>
										<option value="content_3"<%= (contentKey.equals("content_3")) ? " selected" : "" %>>content_3</option>
										<option value="label_4"<%= (contentKey.equals("label_4")) ? " selected" : "" %>>label_4</option>
										<option value="content_4"<%= (contentKey.equals("content_4")) ? " selected" : "" %>>content_4</option>
										<option value="label_5"<%= (contentKey.equals("label_5")) ? " selected" : "" %>>label_5</option>
										<option value="content_5"<%= (contentKey.equals("content_5")) ? " selected" : "" %>>content_5</option>
										<option value="label_6"<%= (contentKey.equals("label_6")) ? " selected" : "" %>>label_6</option>
										<option value="content_6"<%= (contentKey.equals("content_6")) ? " selected" : "" %>>content_6</option>
										<option value="label_7"<%= (contentKey.equals("label_7")) ? " selected" : "" %>>label_7</option>
										<option value="content_7"<%= (contentKey.equals("content_7")) ? " selected" : "" %>>content_7</option>
										<option value="label_8"<%= (contentKey.equals("label_8")) ? " selected" : "" %>>label_8</option>
										<option value="content_8"<%= (contentKey.equals("content_8")) ? " selected" : "" %>>content_8</option>
										<option value="label_9"<%= (contentKey.equals("label_9")) ? " selected" : "" %>>label_9</option>
										<option value="content_9"<%= (contentKey.equals("content_9")) ? " selected" : "" %>>content_9</option>
<%
}
else if (adminContentPage.equals(content_hormone_testing_page))
{
%>
										<option value="label_1"<%= (contentKey.equals("label_1")) ? " selected" : "" %>>label_1</option>
										<option value="content_1"<%= (contentKey.equals("content_1")) ? " selected" : "" %>>content_1</option>
										<option value="label_2"<%= (contentKey.equals("label_2")) ? " selected" : "" %>>label_2</option>
										<option value="content_2"<%= (contentKey.equals("content_2")) ? " selected" : "" %>>content_2</option>
										<option value="label_3"<%= (contentKey.equals("label_3")) ? " selected" : "" %>>label_3</option>
										<option value="content_3"<%= (contentKey.equals("content_3")) ? " selected" : "" %>>content_3</option>
										<option value="label_4"<%= (contentKey.equals("label_4")) ? " selected" : "" %>>label_4</option>
										<option value="content_4"<%= (contentKey.equals("content_4")) ? " selected" : "" %>>content_4</option>
										<option value="label_5"<%= (contentKey.equals("label_5")) ? " selected" : "" %>>label_5</option>
										<option value="content_5"<%= (contentKey.equals("content_5")) ? " selected" : "" %>>content_5</option>
										<option value="label_6"<%= (contentKey.equals("label_6")) ? " selected" : "" %>>label_6</option>
										<option value="content_6"<%= (contentKey.equals("content_6")) ? " selected" : "" %>>content_6</option>
										<option value="label_7"<%= (contentKey.equals("label_7")) ? " selected" : "" %>>label_7</option>
										<option value="content_7"<%= (contentKey.equals("content_7")) ? " selected" : "" %>>content_7</option>
										<option value="label_8"<%= (contentKey.equals("label_8")) ? " selected" : "" %>>label_8</option>
										<option value="content_8"<%= (contentKey.equals("content_8")) ? " selected" : "" %>>content_8</option>
										<option value="label_9"<%= (contentKey.equals("label_9")) ? " selected" : "" %>>label_9</option>
										<option value="content_9"<%= (contentKey.equals("content_9")) ? " selected" : "" %>>content_9</option>
<%
}
else if (adminContentPage.equals(content_hair_analysis_page))
{
%>
										<option value="label_1"<%= (contentKey.equals("label_1")) ? " selected" : "" %>>label_1</option>
										<option value="content_1"<%= (contentKey.equals("content_1")) ? " selected" : "" %>>content_1</option>
										<option value="label_2"<%= (contentKey.equals("label_2")) ? " selected" : "" %>>label_2</option>
										<option value="content_2"<%= (contentKey.equals("content_2")) ? " selected" : "" %>>content_2</option>
										<option value="label_3"<%= (contentKey.equals("label_3")) ? " selected" : "" %>>label_3</option>
										<option value="content_3"<%= (contentKey.equals("content_3")) ? " selected" : "" %>>content_3</option>
										<option value="label_4"<%= (contentKey.equals("label_4")) ? " selected" : "" %>>label_4</option>
										<option value="content_4"<%= (contentKey.equals("content_4")) ? " selected" : "" %>>content_4</option>
										<option value="label_5"<%= (contentKey.equals("label_5")) ? " selected" : "" %>>label_5</option>
										<option value="content_5"<%= (contentKey.equals("content_5")) ? " selected" : "" %>>content_5</option>
										<option value="label_6"<%= (contentKey.equals("label_6")) ? " selected" : "" %>>label_6</option>
										<option value="content_6"<%= (contentKey.equals("content_6")) ? " selected" : "" %>>content_6</option>
										<option value="label_7"<%= (contentKey.equals("label_7")) ? " selected" : "" %>>label_7</option>
										<option value="content_7"<%= (contentKey.equals("content_7")) ? " selected" : "" %>>content_7</option>
										<option value="label_8"<%= (contentKey.equals("label_8")) ? " selected" : "" %>>label_8</option>
										<option value="content_8"<%= (contentKey.equals("content_8")) ? " selected" : "" %>>content_8</option>
										<option value="label_9"<%= (contentKey.equals("label_9")) ? " selected" : "" %>>label_9</option>
										<option value="content_9"<%= (contentKey.equals("content_9")) ? " selected" : "" %>>content_9</option>
<%
}
else if (adminContentPage.equals(content_muscle_testing_page))
{
%>
										<option value="label_1"<%= (contentKey.equals("label_1")) ? " selected" : "" %>>label_1</option>
										<option value="content_1"<%= (contentKey.equals("content_1")) ? " selected" : "" %>>content_1</option>
										<option value="label_2"<%= (contentKey.equals("label_2")) ? " selected" : "" %>>label_2</option>
										<option value="content_2"<%= (contentKey.equals("content_2")) ? " selected" : "" %>>content_2</option>
										<option value="label_3"<%= (contentKey.equals("label_3")) ? " selected" : "" %>>label_3</option>
										<option value="content_3"<%= (contentKey.equals("content_3")) ? " selected" : "" %>>content_3</option>
										<option value="label_4"<%= (contentKey.equals("label_4")) ? " selected" : "" %>>label_4</option>
										<option value="content_4"<%= (contentKey.equals("content_4")) ? " selected" : "" %>>content_4</option>
										<option value="label_5"<%= (contentKey.equals("label_5")) ? " selected" : "" %>>label_5</option>
										<option value="content_5"<%= (contentKey.equals("content_5")) ? " selected" : "" %>>content_5</option>
										<option value="label_6"<%= (contentKey.equals("label_6")) ? " selected" : "" %>>label_6</option>
										<option value="content_6"<%= (contentKey.equals("content_6")) ? " selected" : "" %>>content_6</option>
										<option value="label_7"<%= (contentKey.equals("label_7")) ? " selected" : "" %>>label_7</option>
										<option value="content_7"<%= (contentKey.equals("content_7")) ? " selected" : "" %>>content_7</option>
										<option value="label_8"<%= (contentKey.equals("label_8")) ? " selected" : "" %>>label_8</option>
										<option value="content_8"<%= (contentKey.equals("content_8")) ? " selected" : "" %>>content_8</option>
										<option value="label_9"<%= (contentKey.equals("label_9")) ? " selected" : "" %>>label_9</option>
										<option value="content_9"<%= (contentKey.equals("content_9")) ? " selected" : "" %>>content_9</option>
<%
}
else if (adminContentPage.equals(content_essential_oils_page))
{
%>
										<option value="label_1"<%= (contentKey.equals("label_1")) ? " selected" : "" %>>label_1</option>
										<option value="content_1"<%= (contentKey.equals("content_1")) ? " selected" : "" %>>content_1</option>
										<option value="label_2"<%= (contentKey.equals("label_2")) ? " selected" : "" %>>label_2</option>
										<option value="content_2"<%= (contentKey.equals("content_2")) ? " selected" : "" %>>content_2</option>
										<option value="label_3"<%= (contentKey.equals("label_3")) ? " selected" : "" %>>label_3</option>
										<option value="content_3"<%= (contentKey.equals("content_3")) ? " selected" : "" %>>content_3</option>
										<option value="label_4"<%= (contentKey.equals("label_4")) ? " selected" : "" %>>label_4</option>
										<option value="content_4"<%= (contentKey.equals("content_4")) ? " selected" : "" %>>content_4</option>
										<option value="label_5"<%= (contentKey.equals("label_5")) ? " selected" : "" %>>label_5</option>
										<option value="content_5"<%= (contentKey.equals("content_5")) ? " selected" : "" %>>content_5</option>
										<option value="label_6"<%= (contentKey.equals("label_6")) ? " selected" : "" %>>label_6</option>
										<option value="content_6"<%= (contentKey.equals("content_6")) ? " selected" : "" %>>content_6</option>
										<option value="label_7"<%= (contentKey.equals("label_7")) ? " selected" : "" %>>label_7</option>
										<option value="content_7"<%= (contentKey.equals("content_7")) ? " selected" : "" %>>content_7</option>
										<option value="label_8"<%= (contentKey.equals("label_8")) ? " selected" : "" %>>label_8</option>
										<option value="content_8"<%= (contentKey.equals("content_8")) ? " selected" : "" %>>content_8</option>
										<option value="label_9"<%= (contentKey.equals("label_9")) ? " selected" : "" %>>label_9</option>
										<option value="content_9"<%= (contentKey.equals("content_9")) ? " selected" : "" %>>content_9</option>
<%
}
else if (adminContentPage.equals(content_homeopathy_page))
{
%>
										<option value="label_1"<%= (contentKey.equals("label_1")) ? " selected" : "" %>>label_1</option>
										<option value="content_1"<%= (contentKey.equals("content_1")) ? " selected" : "" %>>content_1</option>
										<option value="label_2"<%= (contentKey.equals("label_2")) ? " selected" : "" %>>label_2</option>
										<option value="content_2"<%= (contentKey.equals("content_2")) ? " selected" : "" %>>content_2</option>
										<option value="label_3"<%= (contentKey.equals("label_3")) ? " selected" : "" %>>label_3</option>
										<option value="content_3"<%= (contentKey.equals("content_3")) ? " selected" : "" %>>content_3</option>
										<option value="label_4"<%= (contentKey.equals("label_4")) ? " selected" : "" %>>label_4</option>
										<option value="content_4"<%= (contentKey.equals("content_4")) ? " selected" : "" %>>content_4</option>
										<option value="label_5"<%= (contentKey.equals("label_5")) ? " selected" : "" %>>label_5</option>
										<option value="content_5"<%= (contentKey.equals("content_5")) ? " selected" : "" %>>content_5</option>
										<option value="label_6"<%= (contentKey.equals("label_6")) ? " selected" : "" %>>label_6</option>
										<option value="content_6"<%= (contentKey.equals("content_6")) ? " selected" : "" %>>content_6</option>
										<option value="label_7"<%= (contentKey.equals("label_7")) ? " selected" : "" %>>label_7</option>
										<option value="content_7"<%= (contentKey.equals("content_7")) ? " selected" : "" %>>content_7</option>
										<option value="label_8"<%= (contentKey.equals("label_8")) ? " selected" : "" %>>label_8</option>
										<option value="content_8"<%= (contentKey.equals("content_8")) ? " selected" : "" %>>content_8</option>
										<option value="label_9"<%= (contentKey.equals("label_9")) ? " selected" : "" %>>label_9</option>
										<option value="content_9"<%= (contentKey.equals("content_9")) ? " selected" : "" %>>content_9</option>
<%
}
else if (adminContentPage.equals(content_herbology_page))
{
%>
										<option value="label_1"<%= (contentKey.equals("label_1")) ? " selected" : "" %>>label_1</option>
										<option value="content_1"<%= (contentKey.equals("content_1")) ? " selected" : "" %>>content_1</option>
										<option value="label_2"<%= (contentKey.equals("label_2")) ? " selected" : "" %>>label_2</option>
										<option value="content_2"<%= (contentKey.equals("content_2")) ? " selected" : "" %>>content_2</option>
										<option value="label_3"<%= (contentKey.equals("label_3")) ? " selected" : "" %>>label_3</option>
										<option value="content_3"<%= (contentKey.equals("content_3")) ? " selected" : "" %>>content_3</option>
										<option value="label_4"<%= (contentKey.equals("label_4")) ? " selected" : "" %>>label_4</option>
										<option value="content_4"<%= (contentKey.equals("content_4")) ? " selected" : "" %>>content_4</option>
										<option value="label_5"<%= (contentKey.equals("label_5")) ? " selected" : "" %>>label_5</option>
										<option value="content_5"<%= (contentKey.equals("content_5")) ? " selected" : "" %>>content_5</option>
										<option value="label_6"<%= (contentKey.equals("label_6")) ? " selected" : "" %>>label_6</option>
										<option value="content_6"<%= (contentKey.equals("content_6")) ? " selected" : "" %>>content_6</option>
										<option value="label_7"<%= (contentKey.equals("label_7")) ? " selected" : "" %>>label_7</option>
										<option value="content_7"<%= (contentKey.equals("content_7")) ? " selected" : "" %>>content_7</option>
										<option value="label_8"<%= (contentKey.equals("label_8")) ? " selected" : "" %>>label_8</option>
										<option value="content_8"<%= (contentKey.equals("content_8")) ? " selected" : "" %>>content_8</option>
										<option value="label_9"<%= (contentKey.equals("label_9")) ? " selected" : "" %>>label_9</option>
										<option value="content_9"<%= (contentKey.equals("content_9")) ? " selected" : "" %>>content_9</option>
<%
}
else if (adminContentPage.equals(content_q2_page))
{
%>
										<option value="label_1"<%= (contentKey.equals("label_1")) ? " selected" : "" %>>label_1</option>
										<option value="content_1"<%= (contentKey.equals("content_1")) ? " selected" : "" %>>content_1</option>
										<option value="label_2"<%= (contentKey.equals("label_2")) ? " selected" : "" %>>label_2</option>
										<option value="content_2"<%= (contentKey.equals("content_2")) ? " selected" : "" %>>content_2</option>
										<option value="label_3"<%= (contentKey.equals("label_3")) ? " selected" : "" %>>label_3</option>
										<option value="content_3"<%= (contentKey.equals("content_3")) ? " selected" : "" %>>content_3</option>
										<option value="label_4"<%= (contentKey.equals("label_4")) ? " selected" : "" %>>label_4</option>
										<option value="content_4"<%= (contentKey.equals("content_4")) ? " selected" : "" %>>content_4</option>
										<option value="label_5"<%= (contentKey.equals("label_5")) ? " selected" : "" %>>label_5</option>
										<option value="content_5"<%= (contentKey.equals("content_5")) ? " selected" : "" %>>content_5</option>
										<option value="label_6"<%= (contentKey.equals("label_6")) ? " selected" : "" %>>label_6</option>
										<option value="content_6"<%= (contentKey.equals("content_6")) ? " selected" : "" %>>content_6</option>
										<option value="label_7"<%= (contentKey.equals("label_7")) ? " selected" : "" %>>label_7</option>
										<option value="content_7"<%= (contentKey.equals("content_7")) ? " selected" : "" %>>content_7</option>
										<option value="label_8"<%= (contentKey.equals("label_8")) ? " selected" : "" %>>label_8</option>
										<option value="content_8"<%= (contentKey.equals("content_8")) ? " selected" : "" %>>content_8</option>
										<option value="label_9"<%= (contentKey.equals("label_9")) ? " selected" : "" %>>label_9</option>
										<option value="content_9"<%= (contentKey.equals("content_9")) ? " selected" : "" %>>content_9</option>
<%
}
else if (adminContentPage.equals(content_massage_page))
{
%>
										<option value="label_1"<%= (contentKey.equals("label_1")) ? " selected" : "" %>>label_1</option>
										<option value="content_1"<%= (contentKey.equals("content_1")) ? " selected" : "" %>>content_1</option>
										<option value="label_2"<%= (contentKey.equals("label_2")) ? " selected" : "" %>>label_2</option>
										<option value="content_2"<%= (contentKey.equals("content_2")) ? " selected" : "" %>>content_2</option>
										<option value="label_3"<%= (contentKey.equals("label_3")) ? " selected" : "" %>>label_3</option>
										<option value="content_3"<%= (contentKey.equals("content_3")) ? " selected" : "" %>>content_3</option>
										<option value="label_4"<%= (contentKey.equals("label_4")) ? " selected" : "" %>>label_4</option>
										<option value="content_4"<%= (contentKey.equals("content_4")) ? " selected" : "" %>>content_4</option>
										<option value="label_5"<%= (contentKey.equals("label_5")) ? " selected" : "" %>>label_5</option>
										<option value="content_5"<%= (contentKey.equals("content_5")) ? " selected" : "" %>>content_5</option>
										<option value="label_6"<%= (contentKey.equals("label_6")) ? " selected" : "" %>>label_6</option>
										<option value="content_6"<%= (contentKey.equals("content_6")) ? " selected" : "" %>>content_6</option>
										<option value="label_7"<%= (contentKey.equals("label_7")) ? " selected" : "" %>>label_7</option>
										<option value="content_7"<%= (contentKey.equals("content_7")) ? " selected" : "" %>>content_7</option>
										<option value="label_8"<%= (contentKey.equals("label_8")) ? " selected" : "" %>>label_8</option>
										<option value="content_8"<%= (contentKey.equals("content_8")) ? " selected" : "" %>>content_8</option>
										<option value="label_9"<%= (contentKey.equals("label_9")) ? " selected" : "" %>>label_9</option>
										<option value="content_9"<%= (contentKey.equals("content_9")) ? " selected" : "" %>>content_9</option>
<%
}
else if (adminContentPage.equals(content_cranio_page))
{
%>
										<option value="label_1"<%= (contentKey.equals("label_1")) ? " selected" : "" %>>label_1</option>
										<option value="content_1"<%= (contentKey.equals("content_1")) ? " selected" : "" %>>content_1</option>
										<option value="label_2"<%= (contentKey.equals("label_2")) ? " selected" : "" %>>label_2</option>
										<option value="content_2"<%= (contentKey.equals("content_2")) ? " selected" : "" %>>content_2</option>
										<option value="label_3"<%= (contentKey.equals("label_3")) ? " selected" : "" %>>label_3</option>
										<option value="content_3"<%= (contentKey.equals("content_3")) ? " selected" : "" %>>content_3</option>
										<option value="label_4"<%= (contentKey.equals("label_4")) ? " selected" : "" %>>label_4</option>
										<option value="content_4"<%= (contentKey.equals("content_4")) ? " selected" : "" %>>content_4</option>
										<option value="label_5"<%= (contentKey.equals("label_5")) ? " selected" : "" %>>label_5</option>
										<option value="content_5"<%= (contentKey.equals("content_5")) ? " selected" : "" %>>content_5</option>
										<option value="label_6"<%= (contentKey.equals("label_6")) ? " selected" : "" %>>label_6</option>
										<option value="content_6"<%= (contentKey.equals("content_6")) ? " selected" : "" %>>content_6</option>
										<option value="label_7"<%= (contentKey.equals("label_7")) ? " selected" : "" %>>label_7</option>
										<option value="content_7"<%= (contentKey.equals("content_7")) ? " selected" : "" %>>content_7</option>
										<option value="label_8"<%= (contentKey.equals("label_8")) ? " selected" : "" %>>label_8</option>
										<option value="content_8"<%= (contentKey.equals("content_8")) ? " selected" : "" %>>content_8</option>
										<option value="label_9"<%= (contentKey.equals("label_9")) ? " selected" : "" %>>label_9</option>
										<option value="content_9"<%= (contentKey.equals("content_9")) ? " selected" : "" %>>content_9</option>
<%
}
else if (adminContentPage.equals(content_acupuncture_page))
{
%>
										<option value="label_1"<%= (contentKey.equals("label_1")) ? " selected" : "" %>>label_1</option>
										<option value="content_1"<%= (contentKey.equals("content_1")) ? " selected" : "" %>>content_1</option>
										<option value="label_2"<%= (contentKey.equals("label_2")) ? " selected" : "" %>>label_2</option>
										<option value="content_2"<%= (contentKey.equals("content_2")) ? " selected" : "" %>>content_2</option>
										<option value="label_3"<%= (contentKey.equals("label_3")) ? " selected" : "" %>>label_3</option>
										<option value="content_3"<%= (contentKey.equals("content_3")) ? " selected" : "" %>>content_3</option>
										<option value="label_4"<%= (contentKey.equals("label_4")) ? " selected" : "" %>>label_4</option>
										<option value="content_4"<%= (contentKey.equals("content_4")) ? " selected" : "" %>>content_4</option>
										<option value="label_5"<%= (contentKey.equals("label_5")) ? " selected" : "" %>>label_5</option>
										<option value="content_5"<%= (contentKey.equals("content_5")) ? " selected" : "" %>>content_5</option>
										<option value="label_6"<%= (contentKey.equals("label_6")) ? " selected" : "" %>>label_6</option>
										<option value="content_6"<%= (contentKey.equals("content_6")) ? " selected" : "" %>>content_6</option>
										<option value="label_7"<%= (contentKey.equals("label_7")) ? " selected" : "" %>>label_7</option>
										<option value="content_7"<%= (contentKey.equals("content_7")) ? " selected" : "" %>>content_7</option>
										<option value="label_8"<%= (contentKey.equals("label_8")) ? " selected" : "" %>>label_8</option>
										<option value="content_8"<%= (contentKey.equals("content_8")) ? " selected" : "" %>>content_8</option>
										<option value="label_9"<%= (contentKey.equals("label_9")) ? " selected" : "" %>>label_9</option>
										<option value="content_9"<%= (contentKey.equals("content_9")) ? " selected" : "" %>>content_9</option>
<%
}
else if (adminContentPage.equals(content_electro_acuscope_page))
{
%>
										<option value="label_1"<%= (contentKey.equals("label_1")) ? " selected" : "" %>>label_1</option>
										<option value="content_1"<%= (contentKey.equals("content_1")) ? " selected" : "" %>>content_1</option>
										<option value="label_2"<%= (contentKey.equals("label_2")) ? " selected" : "" %>>label_2</option>
										<option value="content_2"<%= (contentKey.equals("content_2")) ? " selected" : "" %>>content_2</option>
										<option value="label_3"<%= (contentKey.equals("label_3")) ? " selected" : "" %>>label_3</option>
										<option value="content_3"<%= (contentKey.equals("content_3")) ? " selected" : "" %>>content_3</option>
										<option value="label_4"<%= (contentKey.equals("label_4")) ? " selected" : "" %>>label_4</option>
										<option value="content_4"<%= (contentKey.equals("content_4")) ? " selected" : "" %>>content_4</option>
										<option value="label_5"<%= (contentKey.equals("label_5")) ? " selected" : "" %>>label_5</option>
										<option value="content_5"<%= (contentKey.equals("content_5")) ? " selected" : "" %>>content_5</option>
										<option value="label_6"<%= (contentKey.equals("label_6")) ? " selected" : "" %>>label_6</option>
										<option value="content_6"<%= (contentKey.equals("content_6")) ? " selected" : "" %>>content_6</option>
										<option value="label_7"<%= (contentKey.equals("label_7")) ? " selected" : "" %>>label_7</option>
										<option value="content_7"<%= (contentKey.equals("content_7")) ? " selected" : "" %>>content_7</option>
										<option value="label_8"<%= (contentKey.equals("label_8")) ? " selected" : "" %>>label_8</option>
										<option value="content_8"<%= (contentKey.equals("content_8")) ? " selected" : "" %>>content_8</option>
										<option value="label_9"<%= (contentKey.equals("label_9")) ? " selected" : "" %>>label_9</option>
										<option value="content_9"<%= (contentKey.equals("content_9")) ? " selected" : "" %>>content_9</option>
<%
}
else if (adminContentPage.equals(content_lifestyle_page))
{
%>
										<option value="label_1"<%= (contentKey.equals("label_1")) ? " selected" : "" %>>label_1</option>
										<option value="content_1"<%= (contentKey.equals("content_1")) ? " selected" : "" %>>content_1</option>
										<option value="label_2"<%= (contentKey.equals("label_2")) ? " selected" : "" %>>label_2</option>
										<option value="content_2"<%= (contentKey.equals("content_2")) ? " selected" : "" %>>content_2</option>
										<option value="label_3"<%= (contentKey.equals("label_3")) ? " selected" : "" %>>label_3</option>
										<option value="content_3"<%= (contentKey.equals("content_3")) ? " selected" : "" %>>content_3</option>
										<option value="label_4"<%= (contentKey.equals("label_4")) ? " selected" : "" %>>label_4</option>
										<option value="content_4"<%= (contentKey.equals("content_4")) ? " selected" : "" %>>content_4</option>
										<option value="label_5"<%= (contentKey.equals("label_5")) ? " selected" : "" %>>label_5</option>
										<option value="content_5"<%= (contentKey.equals("content_5")) ? " selected" : "" %>>content_5</option>
										<option value="label_6"<%= (contentKey.equals("label_6")) ? " selected" : "" %>>label_6</option>
										<option value="content_6"<%= (contentKey.equals("content_6")) ? " selected" : "" %>>content_6</option>
										<option value="label_7"<%= (contentKey.equals("label_7")) ? " selected" : "" %>>label_7</option>
										<option value="content_7"<%= (contentKey.equals("content_7")) ? " selected" : "" %>>content_7</option>
										<option value="label_8"<%= (contentKey.equals("label_8")) ? " selected" : "" %>>label_8</option>
										<option value="content_8"<%= (contentKey.equals("content_8")) ? " selected" : "" %>>content_8</option>
										<option value="label_9"<%= (contentKey.equals("label_9")) ? " selected" : "" %>>label_9</option>
										<option value="content_9"<%= (contentKey.equals("content_9")) ? " selected" : "" %>>content_9</option>
<%
}
else if (adminContentPage.equals(content_self_defense_page))
{
%>
										<option value="label_1"<%= (contentKey.equals("label_1")) ? " selected" : "" %>>label_1</option>
										<option value="content_1"<%= (contentKey.equals("content_1")) ? " selected" : "" %>>content_1</option>
										<option value="label_2"<%= (contentKey.equals("label_2")) ? " selected" : "" %>>label_2</option>
										<option value="content_2"<%= (contentKey.equals("content_2")) ? " selected" : "" %>>content_2</option>
										<option value="label_3"<%= (contentKey.equals("label_3")) ? " selected" : "" %>>label_3</option>
										<option value="content_3"<%= (contentKey.equals("content_3")) ? " selected" : "" %>>content_3</option>
										<option value="label_4"<%= (contentKey.equals("label_4")) ? " selected" : "" %>>label_4</option>
										<option value="content_4"<%= (contentKey.equals("content_4")) ? " selected" : "" %>>content_4</option>
										<option value="label_5"<%= (contentKey.equals("label_5")) ? " selected" : "" %>>label_5</option>
										<option value="content_5"<%= (contentKey.equals("content_5")) ? " selected" : "" %>>content_5</option>
										<option value="label_6"<%= (contentKey.equals("label_6")) ? " selected" : "" %>>label_6</option>
										<option value="content_6"<%= (contentKey.equals("content_6")) ? " selected" : "" %>>content_6</option>
										<option value="label_7"<%= (contentKey.equals("label_7")) ? " selected" : "" %>>label_7</option>
										<option value="content_7"<%= (contentKey.equals("content_7")) ? " selected" : "" %>>content_7</option>
										<option value="label_8"<%= (contentKey.equals("label_8")) ? " selected" : "" %>>label_8</option>
										<option value="content_8"<%= (contentKey.equals("content_8")) ? " selected" : "" %>>content_8</option>
										<option value="label_9"<%= (contentKey.equals("label_9")) ? " selected" : "" %>>label_9</option>
										<option value="content_9"<%= (contentKey.equals("content_9")) ? " selected" : "" %>>content_9</option>
<%
}
else if (adminContentPage.equals(content_seminars_events_page))
{
%>
										<option value="label_1"<%= (contentKey.equals("label_1")) ? " selected" : "" %>>label_1</option>
										<option value="content_1"<%= (contentKey.equals("content_1")) ? " selected" : "" %>>content_1</option>
										<option value="label_2"<%= (contentKey.equals("label_2")) ? " selected" : "" %>>label_2</option>
										<option value="content_2"<%= (contentKey.equals("content_2")) ? " selected" : "" %>>content_2</option>
										<option value="label_3"<%= (contentKey.equals("label_3")) ? " selected" : "" %>>label_3</option>
										<option value="content_3"<%= (contentKey.equals("content_3")) ? " selected" : "" %>>content_3</option>
										<option value="label_4"<%= (contentKey.equals("label_4")) ? " selected" : "" %>>label_4</option>
										<option value="content_4"<%= (contentKey.equals("content_4")) ? " selected" : "" %>>content_4</option>
										<option value="label_5"<%= (contentKey.equals("label_5")) ? " selected" : "" %>>label_5</option>
										<option value="content_5"<%= (contentKey.equals("content_5")) ? " selected" : "" %>>content_5</option>
										<option value="label_6"<%= (contentKey.equals("label_6")) ? " selected" : "" %>>label_6</option>
										<option value="content_6"<%= (contentKey.equals("content_6")) ? " selected" : "" %>>content_6</option>
										<option value="label_7"<%= (contentKey.equals("label_7")) ? " selected" : "" %>>label_7</option>
										<option value="content_7"<%= (contentKey.equals("content_7")) ? " selected" : "" %>>content_7</option>
										<option value="label_8"<%= (contentKey.equals("label_8")) ? " selected" : "" %>>label_8</option>
										<option value="content_8"<%= (contentKey.equals("content_8")) ? " selected" : "" %>>content_8</option>
										<option value="label_9"<%= (contentKey.equals("label_9")) ? " selected" : "" %>>label_9</option>
										<option value="content_9"<%= (contentKey.equals("content_9")) ? " selected" : "" %>>content_9</option>
<%
}
else if (adminContentPage.equals(content_contact_us_page))
{
%>
										<option value="label_1"<%= (contentKey.equals("label_1")) ? " selected" : "" %>>label_1</option>
										<option value="content_1"<%= (contentKey.equals("content_1")) ? " selected" : "" %>>content_1</option>
										<option value="label_2"<%= (contentKey.equals("label_2")) ? " selected" : "" %>>label_2</option>
										<option value="content_2"<%= (contentKey.equals("content_2")) ? " selected" : "" %>>content_2</option>
										<option value="label_3"<%= (contentKey.equals("label_3")) ? " selected" : "" %>>label_3</option>
										<option value="content_3"<%= (contentKey.equals("content_3")) ? " selected" : "" %>>content_3</option>
										<option value="label_4"<%= (contentKey.equals("label_4")) ? " selected" : "" %>>label_4</option>
										<option value="content_4"<%= (contentKey.equals("content_4")) ? " selected" : "" %>>content_4</option>
										<option value="label_5"<%= (contentKey.equals("label_5")) ? " selected" : "" %>>label_5</option>
										<option value="content_5"<%= (contentKey.equals("content_5")) ? " selected" : "" %>>content_5</option>
										<option value="label_6"<%= (contentKey.equals("label_6")) ? " selected" : "" %>>label_6</option>
										<option value="content_6"<%= (contentKey.equals("content_6")) ? " selected" : "" %>>content_6</option>
										<option value="label_7"<%= (contentKey.equals("label_7")) ? " selected" : "" %>>label_7</option>
										<option value="content_7"<%= (contentKey.equals("content_7")) ? " selected" : "" %>>content_7</option>
										<option value="label_8"<%= (contentKey.equals("label_8")) ? " selected" : "" %>>label_8</option>
										<option value="content_8"<%= (contentKey.equals("content_8")) ? " selected" : "" %>>content_8</option>
										<option value="label_9"<%= (contentKey.equals("label_9")) ? " selected" : "" %>>label_9</option>
										<option value="content_9"<%= (contentKey.equals("content_9")) ? " selected" : "" %>>content_9</option>
<%
}
else if (adminContentPage.equals(content_links_page))
{
%>
										<option value="label_1"<%= (contentKey.equals("label_1")) ? " selected" : "" %>>label_1</option>
										<option value="content_1"<%= (contentKey.equals("content_1")) ? " selected" : "" %>>content_1</option>
										<option value="label_2"<%= (contentKey.equals("label_2")) ? " selected" : "" %>>label_2</option>
										<option value="content_2"<%= (contentKey.equals("content_2")) ? " selected" : "" %>>content_2</option>
										<option value="label_3"<%= (contentKey.equals("label_3")) ? " selected" : "" %>>label_3</option>
										<option value="content_3"<%= (contentKey.equals("content_3")) ? " selected" : "" %>>content_3</option>
										<option value="label_4"<%= (contentKey.equals("label_4")) ? " selected" : "" %>>label_4</option>
										<option value="content_4"<%= (contentKey.equals("content_4")) ? " selected" : "" %>>content_4</option>
										<option value="label_5"<%= (contentKey.equals("label_5")) ? " selected" : "" %>>label_5</option>
										<option value="content_5"<%= (contentKey.equals("content_5")) ? " selected" : "" %>>content_5</option>
										<option value="label_6"<%= (contentKey.equals("label_6")) ? " selected" : "" %>>label_6</option>
										<option value="content_6"<%= (contentKey.equals("content_6")) ? " selected" : "" %>>content_6</option>
										<option value="label_7"<%= (contentKey.equals("label_7")) ? " selected" : "" %>>label_7</option>
										<option value="content_7"<%= (contentKey.equals("content_7")) ? " selected" : "" %>>content_7</option>
										<option value="label_8"<%= (contentKey.equals("label_8")) ? " selected" : "" %>>label_8</option>
										<option value="content_8"<%= (contentKey.equals("content_8")) ? " selected" : "" %>>content_8</option>
										<option value="label_9"<%= (contentKey.equals("label_9")) ? " selected" : "" %>>label_9</option>
										<option value="content_9"<%= (contentKey.equals("content_9")) ? " selected" : "" %>>content_9</option>
<%
}
else if (adminContentPage.equals(content_newsletters_page))
{
%>
										<option value="label_1"<%= (contentKey.equals("label_1")) ? " selected" : "" %>>label_1</option>
										<option value="content_1"<%= (contentKey.equals("content_1")) ? " selected" : "" %>>content_1</option>
										<option value="label_2"<%= (contentKey.equals("label_2")) ? " selected" : "" %>>label_2</option>
										<option value="content_2"<%= (contentKey.equals("content_2")) ? " selected" : "" %>>content_2</option>
										<option value="label_3"<%= (contentKey.equals("label_3")) ? " selected" : "" %>>label_3</option>
										<option value="content_3"<%= (contentKey.equals("content_3")) ? " selected" : "" %>>content_3</option>
										<option value="label_4"<%= (contentKey.equals("label_4")) ? " selected" : "" %>>label_4</option>
										<option value="content_4"<%= (contentKey.equals("content_4")) ? " selected" : "" %>>content_4</option>
										<option value="label_5"<%= (contentKey.equals("label_5")) ? " selected" : "" %>>label_5</option>
										<option value="content_5"<%= (contentKey.equals("content_5")) ? " selected" : "" %>>content_5</option>
										<option value="label_6"<%= (contentKey.equals("label_6")) ? " selected" : "" %>>label_6</option>
										<option value="content_6"<%= (contentKey.equals("content_6")) ? " selected" : "" %>>content_6</option>
										<option value="label_7"<%= (contentKey.equals("label_7")) ? " selected" : "" %>>label_7</option>
										<option value="content_7"<%= (contentKey.equals("content_7")) ? " selected" : "" %>>content_7</option>
										<option value="label_8"<%= (contentKey.equals("label_8")) ? " selected" : "" %>>label_8</option>
										<option value="content_8"<%= (contentKey.equals("content_8")) ? " selected" : "" %>>content_8</option>
										<option value="label_9"<%= (contentKey.equals("label_9")) ? " selected" : "" %>>label_9</option>
										<option value="content_9"<%= (contentKey.equals("content_9")) ? " selected" : "" %>>content_9</option>
<%
}
else if (adminContentPage.equals(content_forms_page))
{
%>
										<option value="label_1"<%= (contentKey.equals("label_1")) ? " selected" : "" %>>label_1</option>
										<option value="content_1"<%= (contentKey.equals("content_1")) ? " selected" : "" %>>content_1</option>
										<option value="label_2"<%= (contentKey.equals("label_2")) ? " selected" : "" %>>label_2</option>
										<option value="content_2"<%= (contentKey.equals("content_2")) ? " selected" : "" %>>content_2</option>
										<option value="label_3"<%= (contentKey.equals("label_3")) ? " selected" : "" %>>label_3</option>
										<option value="content_3"<%= (contentKey.equals("content_3")) ? " selected" : "" %>>content_3</option>
										<option value="label_4"<%= (contentKey.equals("label_4")) ? " selected" : "" %>>label_4</option>
										<option value="content_4"<%= (contentKey.equals("content_4")) ? " selected" : "" %>>content_4</option>
										<option value="label_5"<%= (contentKey.equals("label_5")) ? " selected" : "" %>>label_5</option>
										<option value="content_5"<%= (contentKey.equals("content_5")) ? " selected" : "" %>>content_5</option>
										<option value="label_6"<%= (contentKey.equals("label_6")) ? " selected" : "" %>>label_6</option>
										<option value="content_6"<%= (contentKey.equals("content_6")) ? " selected" : "" %>>content_6</option>
										<option value="label_7"<%= (contentKey.equals("label_7")) ? " selected" : "" %>>label_7</option>
										<option value="content_7"<%= (contentKey.equals("content_7")) ? " selected" : "" %>>content_7</option>
										<option value="label_8"<%= (contentKey.equals("label_8")) ? " selected" : "" %>>label_8</option>
										<option value="content_8"<%= (contentKey.equals("content_8")) ? " selected" : "" %>>content_8</option>
										<option value="label_9"<%= (contentKey.equals("label_9")) ? " selected" : "" %>>label_9</option>
										<option value="content_9"<%= (contentKey.equals("content_9")) ? " selected" : "" %>>content_9</option>
<%
}
%>
									</select>
								</div>
								<div class="end"></div>
							</div>

							<div class="adminItem">
								<textarea id="editor" name="editor" class="textarea"><%= adminContentPage.getNewsPropertyString(contentKey) %></textarea>
								<div class="end"></div>
							</div>

							<div class="adminItem">
								<div class="leftTM">&nbsp;</div>
								<div class="right">
									<input onclick="document.siteContentForm.valueInput.value = myEditor.cleanHTML(myEditor.getEditorHTML());" class="formbutton" type="submit" value="Add&nbsp;&#47;&nbsp;Update" alt="Add&nbsp;&#47;&nbsp;Update" style="margin-right: 4px;"/><input type="hidden" name="valueInput" value="" />
								</div>
								<div class="end"></div>
							</div>

						</struts-html:form>


					</div>

				<div class="end"></div>
			</div>

			<div class="content_TextBlock">
				<div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>
			</div>





	<div class="content_TextBlock">
	    <div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>
	</div>

    </div>


</div>


<script>

(function() {
    var Dom = YAHOO.util.Dom,
        Event = YAHOO.util.Event;

    var myConfig = {
        height: '300px',
        width: '500px',
        animate: true,
        dompath: true,
        focusAtStart: true
    };

    var state = 'off';
    YAHOO.log('Set state to off..', 'info', 'example');

    YAHOO.log('Create the Editor..', 'info', 'example');
    myEditor = new YAHOO.widget.Editor('editor', myConfig);
    myEditor.on('toolbarLoaded', function() {
        var codeConfig = {
            type: 'push', label: 'Edit HTML Code', value: 'editcode'
        };
        YAHOO.log('Create the (editcode) Button', 'info', 'example');
        this.toolbar.addButtonToGroup(codeConfig, 'insertitem');

        this.toolbar.on('editcodeClick', function() {
            var ta = this.get('element'),
                iframe = this.get('iframe').get('element');

            if (state == 'on') {
                state = 'off';
                this.toolbar.set('disabled', false);
                YAHOO.log('Show the Editor', 'info', 'example');
                YAHOO.log('Inject the HTML from the textarea into the editor', 'info', 'example');
                this.setEditorHTML(ta.value);
                if (!this.browser.ie) {
                    this._setDesignMode('on');
                }

                Dom.removeClass(iframe, 'editor-hidden');
                Dom.addClass(ta, 'editor-hidden');
                this.show();
                this._focusWindow();
            } else {
                state = 'on';
                YAHOO.log('Show the Code Editor', 'info', 'example');
                this.cleanHTML();
                YAHOO.log('Save the Editors HTML', 'info', 'example');
                Dom.addClass(iframe, 'editor-hidden');
                Dom.removeClass(ta, 'editor-hidden');
                this.toolbar.set('disabled', true);
                this.toolbar.getButtonByValue('editcode').set('disabled', false);
                this.toolbar.selectButton('editcode');
                this.dompath.innerHTML = 'Editing HTML Code';
                this.hide();
            }
            return false;
        }, this, true);

        this.on('cleanHTML', function(ev) {
            YAHOO.log('cleanHTML callback fired..', 'info', 'example');
            this.get('element').value = ev.html;
        }, this, true);

        this.on('afterRender', function() {
            var wrapper = this.get('editor_wrapper');
            wrapper.appendChild(this.get('element'));
            this.setStyle('width', '100%');
            this.setStyle('height', '100%');
            this.setStyle('visibility', '');
            this.setStyle('top', '');
            this.setStyle('left', '');
            this.setStyle('position', '');

            this.addClass('editor-hidden');
        }, this, true);
    }, myEditor, true);
    myEditor.render();

})();
</script>

</body>
</html>