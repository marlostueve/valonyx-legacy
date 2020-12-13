<%@ page language="java" buffer="12kb" contentType="text/html" import="com.badiyan.uk.beans.*, java.util.*, java.text.*, org.apache.commons.beanutils.*, com.badiyan.uk.online.beans.*" %>

<jsp:useBean id="adminCompany" class="com.badiyan.uk.online.beans.UKOnlineCompanyBean" scope="session" />
<jsp:useBean id="appointment" class="com.badiyan.uk.online.beans.AppointmentBean" scope="session" />
<jsp:useBean id="soapNote" class="com.badiyan.uk.online.beans.SOAPNotesBean" scope="session" />

<%


if (request.getParameter("id") != null) {
	appointment = AppointmentBean.getAppointment(Integer.parseInt(request.getParameter("id")));
	session.setAttribute("appointment", appointment);
	
	adminCompany = (UKOnlineCompanyBean)appointment.getCompany();
	System.out.println("COMPANY FOUND >" + adminCompany.getLabel());
	session.setAttribute("adminCompany", adminCompany);
}




Vector practice_areas = PracticeAreaBean.getPracticeAreas(adminCompany);
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

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<!--
iPad minimal web app HTML/CSS template (Responsive Web Design, no JS required)

@author Xavi Esteve
@website http://xaviesteve.com/2899/ipad-iphone-mobile-html-css-template-for-web-apps/
@version 1.0
@Last Updated: 31 January 2012
@license Public Domain (free + no need to attribute, I'd be glad if you send me a link to your creation)


Notes:
- Header position bug when scrolling: When you scroll down, the header may move to the middle of the screen. Fix it by removing the # from the URL.

-->
<title>Valonyx</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0"/> 
<link rel="apple-touch-icon" href="favicon-114.png" />
<meta name="apple-mobile-web-app-capable" content="yes" /><!-- hide top bar in mobile safari-->
<!--<meta name="apple-mobile-web-app-status-bar-style" content="black-translucent" /> translucent top bar -->
<!--<link rel="stylesheet" type="text/css" media="screen" href="style.css" />-->
<link rel="shortcut icon" href="/favicon.ico">
<style type="text/css">
/* Eric Meyer's Reset */html,body,div,span,applet,object,iframe,h1,h2,h3,h4,h5,h6,p,blockquote,pre,a,abbr,acronym,address,big,cite,code,del,dfn,em,img,ins,kbd,q,s,samp,small,strike,strong,sub,sup,tt,var,b,u,i,center,dl,dt,dd,ol,ul,li,fieldset,form,label,legend,table,caption,tbody,tfoot,thead,tr,th,td,article,aside,canvas,details,embed,figure,figcaption,footer,header,hgroup,menu,nav,output,ruby,section,summary,time,mark,audio,video{border:0;font-size:100%;font:inherit;vertical-align:baseline;margin:0;padding:0;}article,aside,details,figcaption,figure,footer,header,hgroup,menu,nav,section{display:block;}body{line-height:1;}ol,ul{list-style:none;}blockquote,q{quotes:none;}blockquote:before,blockquote:after,q:before,q:after{content:none;}table{border-collapse:collapse;border-spacing:0;}

/* Common */
strong,.strong {font-weight:bold;}
.center {text-align:center;}

/* Shared classes */
.header {background: #aeb2be; /* Old browsers */background: -moz-linear-gradient(top, #ffffff 0%, #aeb2be 100%); /* FF3.6+ */background: -webkit-gradient(linear, left top, left bottom, color-stop(0%,#ffffff), color-stop(100%,#aeb2be)); /* Chrome,Safari4+ */background: -webkit-linear-gradient(top, #ffffff 0%,#aeb2be 100%); /* Chrome10+,Safari5.1+ */background: -o-linear-gradient(top, #ffffff 0%,#aeb2be 100%); /* Opera 11.10+ */background: -ms-linear-gradient(top, #ffffff 0%,#aeb2be 100%); /* IE10+ */background: linear-gradient(top, #ffffff 0%,#aeb2be 100%); /* W3C */filter: progid:DXImageTransform.Microsoft.gradient( startColorstr='#ffffff', endColorstr='#aeb2be',GradientType=0 ); /* IE6-9 */
border-bottom:1px solid #000;height:43px;position:fixed;text-align:center;top:0;left:0;}

.header .title {color:#71787F;font-size:18px;font-weight:bold;margin-top:10px;text-shadow:0 1px 1px #fff;}

/* Structure */
html, #wrap {background:#d8dae0;font: 16px normal Helvetica,sans-serif;-webkit-user-select: none;}
			
	#main {background:#d8dae0;height:100%;padding:63px 20px 20px 320px;position:relative;vertical-align:top;}
		#main .header {padding-left:155px;width:100%;}
			#main .header .left,
			#main .header .right {background: #7A8091; /* Old browsers */background: -moz-linear-gradient(top, #999999 0%, #333333 100%); /* FF3.6+ */background: -webkit-gradient(linear, left top, left bottom, color-stop(0%,#999999), color-stop(100%,#333333)); /* Chrome,Safari4+ */background: -webkit-linear-gradient(top, #999999 0%,#333333 100%); /* Chrome10+,Safari5.1+ */background: -o-linear-gradient(top, #999999 0%,#333333 100%); /* Opera 11.10+ */background: -ms-linear-gradient(top, #999999 0%,#333333 100%); /* IE10+ */background: linear-gradient(top, #999999 0%,#333333 100%); /* W3C */filter: progid:DXImageTransform.Microsoft.gradient( startColorstr='#999999', endColorstr='#333333',GradientType=0 ); /* IE6-9 */
color: #fff;border-radius: 5px;border: 1px solid #6d6d6d;font-size: 12px;left: 310px;position: fixed;top: 9px;padding: 5px 8px;text-decoration:none;}
			#main .header .title {}
			#main .header .right {right: 10px;left: auto;}
		#main .content {}
			#main .content>:first-child {margin-top:0 !important;}
			#main .content .title {font-size:18px;font-weight:bold;margin:20px 0 10px;}
			#main .content .title2 {color:#4C536C;font-size:16px;font-weight:bold;margin:20px 0 10px;}
			#main .content .title3 {}
			#main .content .title4 {}
			#main .content .title5 {}
			#main .content>p {color:#4C536C;margin:10px 0;text-shadow:0 1px 1px #ccc;}
			#main .content p.note {color:#4C536C;font-size:12px;text-align:center;text-shadow:0 1px 1px #ccc;}
			
			/* Box white */
			#main .content .box-white {background:#fff;border:1px solid #B4B7BB;border-radius:10px;}
				#main .content .box-white p {color:#000;border-bottom:1px solid #B4B7BB;font-weight:bold;margin:0;padding:10px;}
					#main .content .box-white p:last-child {border-bottom:none;}
				#main .content .box-white p span {color:#4C556C;float:right;font-weight:normal;}
					#main .content .box-white p span.detail {color: #999;float: none;font-size:12px;margin-left:5px;}
					#main .content .box-white p span.arrow {color: #666;float: none;font-family: monospace;font-weight: bold;margin-left: 5px;text-shadow: 0 1px 1px #666;}
			
			/* Tables */
			#main table {margin:20px 0 10px;width:100%;}
				#main table thead th {color:#848B9A;font-size:90%;font-weight:normal;margin:20px 0 10px;padding-bottom:10px;text-align:left;}
					#main table thead th:first-child {color:#000;font-size:16px;font-weight:bold;}
				#main table tbody {background:#fff;border:1px solid #B4B7BB;border-radius:10px;/* not working */}
					#main table tbody tr {border-bottom:1px solid #B4B7BB;}
						#main table tbody tr:last-child {border-bottom:none;}
						#main table tbody tr td {color:#4C556C;padding:10px 0;}
							#main table tbody tr td:first-child {color:#000;padding-left:10px;}
							#main table tbody tr td:last-child {padding-right:10px;}
							
							/* Dirty fix attempt for tbody border-radius */
							#main table tbody {border-spacing: 0;}
								#main table tbody tr {border:1px solid #B4B7BB;border-radius:10px;}
								#main table tbody tr:first-child td:first-child {border-top-left-radius:10px;}
								#main table tbody tr:first-child td:last-child {border-top-right-radius:10px;}
								#main table tbody tr:last-child td:first-child {border-bottom-left-radius:10px;}
								#main table tbody tr:last-child td:last-child {border-bottom-right-radius:10px;}
								#main table tbody tr:last-child {border-bottom:1px solid #B4B7BB;}

				/* Links */
				a {color:#0085d5;text-decoration:none;-webkit-touch-callout: none;}
				#main .content .box-white p a,
				#main .content table a {display: block;padding: 10px;margin: -10px;}

				/* Forms and buttons */
				#main .content p label {width:15%;} /* Labels not currently clickable without scripting */
				#main .content p input[type=text],
				#main .content p input[type=password],
				#main .content p select {background:none;border:none;color:#4C556C;float:right;font-size:14px;margin-top: -1px;width:84%;}
				#main .content p select {margin-right:15px;}
				#main .content .button {color:#fff;cursor:pointer;border:1px solid #999;border-radius: 5px;font-size:16px;font-weight:bold;padding: 8px;width:100%;}
				#main .content .button.red {background: #D42E32; /* Old browsers */
background: -moz-linear-gradient(top, #d58e94 0%, #d42e32 50%, #be1012 51%, #90191b 100%); /* FF3.6+ */background: -webkit-gradient(linear, left top, left bottom, color-stop(0%,#d58e94), color-stop(50%,#d42e32), color-stop(51%,#be1012), color-stop(100%,#90191b)); /* Chrome,Safari4+ */background: -webkit-linear-gradient(top, #d58e94 0%,#d42e32 50%,#be1012 51%,#90191b 100%); /* Chrome10+,Safari5.1+ */background: -o-linear-gradient(top, #d58e94 0%,#d42e32 50%,#be1012 51%,#90191b 100%); /* Opera 11.10+ */background: -ms-linear-gradient(top, #d58e94 0%,#d42e32 50%,#be1012 51%,#90191b 100%); /* IE10+ */background: linear-gradient(top, #d58e94 0%,#d42e32 50%,#be1012 51%,#90191b 100%); /* W3C */filter: progid:DXImageTransform.Microsoft.gradient( startColorstr='#d58e94', endColorstr='#90191b',GradientType=0 ); /* IE6-9 */
border-color:#9A8185;}


	#sidebar {background:#BCBEC4;border-right:1px solid #000;height:100%;left:0;position:fixed;top:0;vertical-align:top;width:300px;z-index:1;}
		#sidebar .header {width:300px;}
			#sidebar .header .title {}
		#sidebar .content {padding: 43px 0 20px 0;}
			#sidebar .content .nav {}
				#sidebar .content .nav a {background:#D9DCE0;border-top:1px solid #E7EAED;border-bottom:1px solid #D0D3D7;color:#000;display:block;font-weight:900;height: 17px;padding: 12px 10px 16px 10px;text-decoration:none;}
					#sidebar .content .nav a.active {background: #0375EE;/* Old browsers */background: -moz-linear-gradient(top, #058CF5 0%, #015DE6 100%); /* FF3.6+ */background: -webkit-gradient(linear, left top, left bottom, color-stop(0%,#058CF5), color-stop(100%,#015DE6)); /* Chrome,Safari4+ */background: -webkit-linear-gradient(top, #058CF5 0%,#015DE6 100%); /* Chrome10+,Safari5.1+ */background: -o-linear-gradient(top, #058CF5 0%,#015DE6 100%); /* Opera 11.10+ */background: -ms-linear-gradient(top, #058CF5 0%,#015DE6 100%); /* IE10+ */background: linear-gradient(top, #058CF5 0%,#015DE6 100%); /* W3C */filter: progid:DXImageTransform.Microsoft.gradient( startColorstr='#058CF5', endColorstr='#015DE6',GradientType=0 ); /* IE6-9 */
					border-top:1px solid #015DE6;color:#fff;text-shadow:0 1px 1px #333;}
					#sidebar .content .nav a span {color:#4C556C;float:right;font-weight:normal;}
						#sidebar .content .nav a.active span {color:#fff;}
					#sidebar .content .nav a .ico {background:#999;border-radius:5px;display: inline-block;float: none;height: 28px;margin: -5px 10px 0 0;vertical-align: middle;width: 28px;}
					#sidebar .content .nav a .info {background: #E20000;border: 1px solid #C00;border-radius: 100%;box-shadow:0 1px 1px #999;color: white;font-size: 12px;display: block;padding: 1px 5px;}
			#sidebar .content p {color:#4C536C;font-size:14px;padding:10px;text-shadow:0 1px 1px #ccc;}

/* All portable */
@media only screen and (max-device-width: 1024px) {
	#sidebar {overflow:scroll;} /* Sidebar is only scrollable in portable devices, you can change that */
}
/* iPhone */
@media only screen and (max-width: 768px) {
	#sidebar {display:none;}
	#main {padding-left:20px;}
		#main .header {padding-left:0;}
			#main .header .left {left:10px;}
			
#main .content p label {}
				#main .content p input[type=text],
				#main .content p input[type=password],
				#main .content p select {width:60%;}
}

</style>
</head>
<%

SimpleDateFormat time_date_format = new SimpleDateFormat("hh:mm a");
SimpleDateFormat short_date_format = new SimpleDateFormat("M/d");
SimpleDateFormat longer_date_format = new SimpleDateFormat("EEEE MMM d");

Date appt_date = appointment.getAppointmentDate();
Calendar appt_end = Calendar.getInstance();
appt_end.setTime(appt_date);
appt_end.add(Calendar.MINUTE, appointment.getDuration());

String status_string = "";
if (appointment.isCancelled())
	status_string = "[CANCELLED] ";
else if (appointment.isRescheduled())
	status_string = "[RESCHEDULED] ";

String comments = appointment.getComments();
if (!comments.equals("") || !status_string.equals(""))
	comments = status_string + comments;




/*
System.out.println("soapNote.getPerson() >" + soapNote.getPerson());
if (soapNote.getPerson() != null)
	System.out.println("soapNote.getPerson().getId() >" + soapNote.getPerson().getLabel());
System.out.println("appointment.getClient().getId() >" + appointment.getClient().getLabel());
 */

if (appointment.hasClient()) {
	
	System.out.println("");
	System.out.println("");
	System.out.println("");
	System.out.println("do stuff");
	
	try {
		soapNote = SOAPNotesBean.getSOAPNotes(appointment.getClient(), new Date());
		System.out.println("found note >" + soapNote.getLabel());
		session.setAttribute("soapNote", soapNote);
	} catch (Exception x) {
		System.out.println("error >" + x.getMessage());
		x.printStackTrace();
		/*
		soapNote = new SOAPNotesBean();
		soapNote.setAnalysisDate(new Date());
		soapNote.setPerson(appointment.getClient());
		soapNote.setPracticeArea(appointment.getPracticeArea());
	 */
	}
	System.out.println("more stuff");
	
	if (soapNote.isNew() || (soapNote.getPerson() == null) || (appointment.getClient().getId() != soapNote.getPerson().getId())) {
		soapNote = new SOAPNotesBean();
		soapNote.setPerson(appointment.getClient());
		soapNote.setPracticeArea(appointment.getPracticeArea());
		session.setAttribute("soapNote", soapNote);
	}
}

%>
<body>

	<div id="wrap">
	
	
		<div id="main">
		
			<div class="header">
				
				<a class="left" href="schedule-mobile.jsp">Back</a>
				<h1 class="title"><%= appointment.getPractitioner().getLabel() %> - Appointment</h1>
				<!--<a class="right" href="?">Edit</a> -->
				
			</div><!--header-->
			
			<div class="content">
			
				<h2 class="title"><%= appointment.getLabel() %></h2>
				
				<p><%= appointment.getComments() %></p>
					
				<div class="box-white">
<%
if (appointment.hasClient()) {
%>
					<p>Client <span><a href="?"><strong><%= appointment.getClient().getLabel() %></strong></a></span></p>
<%
}
%>
					<p>Date <span><%= longer_date_format.format(appt_date) %></span></p>
					<p>Start Time <span><%= time_date_format.format(appt_date) %></span></p>
					<p>End Time <span><%= time_date_format.format(appt_end.getTime()) %></span></p>
					<p>Type <span><%= appointment.getType().getLabel() %></span></p>
					<p>Practitioner <span><%= appointment.getPractitioner().getLabel() %></span></p>
					
				</div><!--box-white-->
<%


					
if (soapNote.isNew()) {
	try {
		soapNote = SOAPNotesBean.getSOAPNotes(appointment.getClient(), new Date());
		session.setAttribute("soapNote", soapNote);
	} catch (Exception x) {
		/*
		soapNote = new SOAPNotesBean();
		soapNote.setAnalysisDate(new Date());
		soapNote.setPerson(appointment.getClient());
		soapNote.setPracticeArea(appointment.getPracticeArea());
	 */
	}
}

if (!soapNote.isNew()) {
%>
				<h2 class="title">Current SOAP Note (today)</h2>
				
				<h2 class="title2">Subjective</h2>
				<p><%= soapNote.getSNoteString().replaceAll("\n", "<br />") %></p>
				
				<h2 class="title2">Objective</h2>
				<p><%= soapNote.getONoteString().replaceAll("\n", "<br />") %></p>
				
				<h2 class="title2">Assessment</h2>
				<p><%= soapNote.getANoteString().replaceAll("\n", "<br />") %></p>
				
				<h2 class="title2">Plan</h2>
				<p><%= soapNote.getPNoteString().replaceAll("\n", "<br />") %></p>
<%
}

if (appointment.hasClient()) {
	Iterator oldNotes = SOAPNotesBean.getSOAPNotes(appointment.getClient()).iterator();
	if (oldNotes.hasNext()) {
		SOAPNotesBean lastNote = (SOAPNotesBean)oldNotes.next();
		
		if (lastNote.getId() == soapNote.getId()) {
			if (oldNotes.hasNext()) {
				lastNote = (SOAPNotesBean)oldNotes.next();
			}
		}
			
		if (lastNote.getId() != soapNote.getId()) {
%>
				<h2 class="title">Last SOAP Note (<%= lastNote.getAnalysisDateString() %>)</h2>
				
				<h2 class="title2">Subjective</h2>
				<p><%= lastNote.getSNoteString() %></p>
				
				<h2 class="title2">Objective</h2>
				<p><%= lastNote.getONoteString() %></p>
				
				<h2 class="title2">Assessment</h2>
				<p><%= lastNote.getANoteString() %></p>
				
				<h2 class="title2">Plan</h2>
				<p><%= lastNote.getPNoteString() %></p>
<%
		} else {
%>
				<h2 class="title">Last SOAP Note</h2>
				<h2 class="title2">None previous notes found</h2>
<%
		}
		
	} else {
%>
				<h2 class="title">Last SOAP Note</h2>
				<h2 class="title2">None found</h2>
<%
	}
}
%>
			

				
				
				<!--
				<h2 class="title">Heading first</h2>

				<div class="box-white">
					<p class="center">Centered text in a white box</p>
				</div>
				
				<h2 class="title2">Account details</h2>
				<p>Me got forms too:</p>
				
				<div class="box-white">
					<p><label for="field1">Apple ID</label><input id="field1" type="text" value="xavi@example.com" /></p>
					<p><label for="field2">Password</label><input id="field2" type="password" value="xavixavi" /></p>
					<p><label for="field3">Country</label><select id="field3"><option>Spain</option><option>United Kingdom</option></select></p>
				</div>
				
				<p><input type="submit" class="button red" value="Delete Account" /></p>
				-->
				
				
				
			
				<h2 class="title">Schedule Follow-up Appointment</h2>
					
				<div class="box-white">

<%


Iterator practitioner_itr = practitioners.iterator();
while (practitioner_itr.hasNext())
{
	UKOnlinePersonBean practitioner = (UKOnlinePersonBean)practitioner_itr.next();
%>
					<p><a href="schedule-mobile-appointment-followup.jsp?id=<%= practitioner.getValue() %>"><%= practitioner.getLabel() %><span><span class="arrow">&gt;</span></span></a></p>
<%
}


%>
				</div>
				
				
				
							
			</div><!--content-->
		
		</div><!--main-->
	
	
	
	
	
		<div id="sidebar">
			
			<div class="header">
				<p class="title">Valonyx Practitioner App</p>
			</div><!--header-->
			
			<div class="content">
				
				<ul class="nav">
					<li><a href="schedule-mobile-appointment.jsp" class="active"><span class="ico msg"></span>Appointment</a></li>
<%
if (appointment.hasClient()) {
%>
					<li><a href="schedule-mobile-appointment-soap.jsp?note=s"><span class="ico msg"></span>S Note</a></li>
					<li><a href="schedule-mobile-appointment-soap.jsp?note=o"><span class="ico msg"></span>O Note</a></li>
					<li><a href="schedule-mobile-appointment-soap.jsp?note=a"><span class="ico msg"></span>A Note</a></li>
					<li><a href="schedule-mobile-appointment-soap.jsp?note=p"><span class="ico msg"></span>P Note</a></li>
					<li><a href="schedule-mobile-appointment-pre-checkout.jsp"><span class="ico msg"></span>Pre-Checkout</a></li>
<%
}
%>
				</ul>
				
				
			</div><!--content-->
			
		</div><!--sidebar-->
	
		
		

	</div><!--wrap-->


<!--<script type="text/javascript" src="script.js">-->
</body>
</html>