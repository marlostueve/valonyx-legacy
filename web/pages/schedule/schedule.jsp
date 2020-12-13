<%@ page import="com.badiyan.uk.beans.*, java.util.*, java.text.*, org.apache.commons.beanutils.*, com.badiyan.uk.online.beans.*" %>

<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>

<jsp:useBean id="adminCompany" class="com.badiyan.uk.online.beans.UKOnlineCompanyBean" scope="session" />
<jsp:useBean id="adminPracticeArea" class="com.badiyan.uk.online.beans.PracticeAreaBean" scope="session" />
<jsp:useBean id="loginBean" class="com.badiyan.uk.online.beans.UKOnlineLoginBean" scope="session" />

<%

boolean cash_drawer_present = false; // need to make this a setting...
boolean credit_card_swiper_present = false; // ditto
boolean record_check_number = false; // ditto


session.setAttribute("chrome", "true");

boolean pos_enabled = adminCompany.getQuickBooksSettings().isPOSEnabled();

boolean left_expanded = true;
boolean right_expanded = false;

try
{
    UKOnlinePersonBean adminPerson = (UKOnlinePersonBean)loginBean.getPerson();
    left_expanded = adminPerson.isLeftExpanded();
    right_expanded = adminPerson.isRightExpanded();

	if (!adminPerson.hasRole(UKOnlineRoleBean.CASHIER_ROLE_NAME) && !adminPerson.hasRole(UKOnlineRoleBean.SYSTEM_ADMINISTRATOR_ROLE_NAME) && !adminPerson.hasRole(UKOnlineRoleBean.COMPANY_ADMINISTRATOR_ROLE_NAME))
		pos_enabled = false;
}
catch (Exception exception1)
{
    RequestDispatcher rd = pageContext.getServletContext().getRequestDispatcher("/admin/schedule-login.jsp");
    request.setAttribute("session-expired", "true");
    rd.forward(request, response);
    return;
}


Calendar display_date = null;
if (session.getAttribute("display_date") == null) {
    display_date = Calendar.getInstance();
    session.setAttribute("display_date", display_date);
} else {
    display_date = (Calendar)session.getAttribute("display_date");
}
   
int start_hour_of_day = adminCompany.getStartHourOfDay();
int start_minute = adminCompany.getStartMinute();
int end_hour_of_day = adminCompany.getEndHourOfDay();
int end_minute = adminCompany.getEndMinute();

Vector practice_areas = PracticeAreaBean.getPracticeAreas(adminCompany);

Vector practitioners = null;
try
{
    if (request.getParameter("practice_area_id") != null)
    {
		if (request.getParameter("practice_area_id").equals("0"))
			adminPracticeArea = new PracticeAreaBean();
		else
		{
			adminPracticeArea = PracticeAreaBean.getPracticeArea(adminCompany, Integer.parseInt(request.getParameter("practice_area_id")));
			practitioners = adminPracticeArea.getPractitioners();
		}
		session.setAttribute("adminPracticeArea", adminPracticeArea);
    }
    else
    {
		if (!adminPracticeArea.isNew())
			practitioners = adminPracticeArea.getPractitioners();
    }
}
catch (Exception x)
{
    x.printStackTrace();
}
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





//SimpleDateFormat date_format = new SimpleDateFormat("EEEE, MMMM d, yyyy");
SimpleDateFormat date_format = new SimpleDateFormat("MM/d/yyyy");
SimpleDateFormat time_date_format = new SimpleDateFormat("h:mm a");

Calendar open_time = Calendar.getInstance();
open_time.set(Calendar.HOUR_OF_DAY, start_hour_of_day);
open_time.set(Calendar.MINUTE, start_minute);
Calendar closing_time = Calendar.getInstance();
closing_time.set(Calendar.HOUR_OF_DAY, end_hour_of_day);
closing_time.set(Calendar.MINUTE, end_minute);

String time_prefix = time_date_format.format(open_time.getTime());

Vector codes = CheckoutCodeBean.getCheckoutCodes(adminCompany);

%>

<!DOCTYPE html>
<html:html lang="true">
<head>
  <meta charset="utf-8">
  <!-- Always force latest IE rendering engine or request Chrome Frame -->
  <meta content="IE=edge,chrome=1" http-equiv="X-UA-Compatible">
  <title><%= adminCompany.getLabel() %> Schedule</title>

  <link href="../../stylesheets/application.css" media="screen" rel="stylesheet" type="text/css" />


  <!--[if lt IE 9]>
<script src="../../javascripts/html5shiv.js" type="text/javascript"></script><script src="../../javascripts/excanvas.js" type="text/javascript"></script><script src="../../javascripts/iefix.js" type="text/javascript"></script><link href="../../stylesheets/iefix.css" media="screen" rel="stylesheet" type="text/css" /><![endif]-->

  <meta name="viewport" content="width=device-width, maximum-scale=1, initial-scale=1, user-scalable=0">
  
  
</head>
<body>

<div id="modal" class="black-box modal hide fade">
  <div class="modal-header tab-header">
    <button type="button" class="close" data-dismiss="modal">&times;</button>
    <span>Some modal title</span>
  </div>
  <div class="modal-body separator">
    <h4>Text in a modal</h4>
    <p>Duis mollis, est non commodo luctus, nisi erat porttitor ligula, eget lacinia odio sem.</p>
  </div>
  <div class="modal-footer">
    <div class="inner-well">
      <a class="button mini rounded light-gray" data-dismiss="modal">Close</a>
      <a class="button mini rounded blue">Save changes</a>
    </div>
  </div>
</div>

<div id="modal-gallery" class="black-box modal modal-gallery hide fade">
  <div class="modal-header tab-header">
    <button type="button" class="close" data-dismiss="modal">&times;</button>
    <span class="modal-title"></span>
  </div>
  <div class="modal-body"><div class="modal-image"></div></div>
  <div class="modal-footer">
    <div class="pull-left">
      You can also change the images<br/> by scrolling the mouse wheel!
    </div>
    <div class="pull-right">
      <a class="button blue modal-next">Next <i class="icon-arrow-right icon-white"></i></a>
      <a class="button gray modal-prev"><i class="icon-arrow-left icon-white"></i> Previous</a>
      <a class="button green modal-play modal-slideshow" data-slideshow="5000"><i class="icon-play icon-white"></i> Slideshow</a>
      <a class="button black" target="_blank"><i class="icon-download"></i> Download</a>
    </div>
  </div>
</div>
<nav id="primary" class="main-nav">
  <ul>

    <li class="active">
      <a href="../schedule/schedule.jsp">
        <i class="icon-calendar"></i> Schedule
      </a>
    </li>

    <li class="">
      <a href="../user_interface/buttons_dropdowns.html">
          <i class="icon-group"></i> Clients
      </a>
    </li>

    <li class="">
      <a href="widgets.html">
          <i class="icon-pushpin"></i> End Of Day
      </a>
    </li>

    <li class="">
      <a href="../charts/charts.html">
          <i class="icon-bar-chart"></i> Reports
      </a>
    </li>

    <li class="">
      <a href="../charts/charts.html">
          <i class="icon-wrench"></i> Admin
      </a>
    </li>




<!--
    <li class="dropdown">
      <a class="dropdown-toggle" data-toggle="dropdown"><i class="icon-share-alt"></i>More <span class="caret"></span></a>

      <ul class="dropdown-menu">
        <li>
          <a href="../error_404.html">
              <i class="icon-warning-sign"></i> Error 404
          </a>
        </li>

        

        <li>
          <a href="../docs/docs.html">
              <i class="icon-book"></i> Documentation
          </a>
        </li>

        
        <li class="divider"></li>
        <li>
          <a href="../login/login.html">
              <i class="icon-off"></i> Log out (login page)
          </a>
        </li>
      </ul>
    </li>
-->

  </ul>
</nav>

<nav id="secondary" class="main-nav">

<!--
  <div class="profile-menu" style="background-color: #c3c3c3; height: 44px;" >
    <div class="pull-left" style="margin-top: 4px;">
      <img src="../../images/valonyx-logo.png" />
    </div>
  </div>
 
 
  <div class="profile-menu" style="height: 44px;" >
    <div class="pull-left" style="margin-top: 4px; margin-left: 8px;">
      
      <div class="title">
        Sano Wellness Center, LLC
      </div>
    </div>
  </div>
-->


		

  <div class="profile-menu">

    <div class="pull-left">
      <div class="avatar">
        <img src="../../images/marlo.png" />
      </div>
    </div>

    <div class="pull-left">
      <div class="title">
        Marlo
      </div>
      <div class="btn-group">
        <!-- <button class="button mini inset black"><i class="icon-search"></i></button> -->
        <button class="button mini inset black">Logout</button>
        <button class="button mini inset black dropdown-toggle" data-toggle="dropdown"><i class="icon-cog"></i></button>
        <ul class="dropdown-menu black-box-dropdown">
          <li><a href="#">Action</a></li>
          <li><a href="#">Another action</a></li>
          <li><a href="#">Something else here</a></li>
          <li class="divider"></li>
          <li><a href="#">Separated link</a></li>
        </ul>

      </div>
		
		
    </div>

    <div class="pull-right profile-menu-nav-collapse">
      <button class="button black"><i class="icon-reorder"></i></button>
    </div>

  </div>

  <ul class="secondary-nav-menu">
	  
	  <li class="">
		  <div style="margin-left:auto; margin-right: auto; margin-top: 5px; width: 75%; ">
			<input id="datetimepicker" type="text" class="fill-up" value="<%= date_format.format(display_date.getTime()) %>" style="font-size: larger; font-weight:  bolder;" onchange="javascript: doAjax('pickDay', this.value);">
		</div>
	  </li>
	  
	  <li class="">
		<a href="javascript: showNewAppointment();">
			<i class="icon-time"></i> New Appointment
		</a>
	  </li>
	  
	  <li class="">
		<a href="javascript: showNewClient();">
			<i class="icon-user"></i> New Client
		</a>
	  </li>

	  <li class="">
		<a href="javascript: showClientInfo();">
			<i class="icon-info-sign"></i> Client Info
		</a>
	  </li>

	  <li class="">
		<a href="javascript: showCheckoutNoAppt();">
			<i class="icon-shopping-cart"></i> Checkout
		</a>
	  </li>
	  

    <li class="dropdown">
      <a class="dropdown-toggle" data-toggle="dropdown"><i class="icon-user-md"></i> Practitioners <span class="caret"></span></a>

      <ul class="dropdown-menu">
        <li>
          <a href="../error_404.html">
              <i class="icon-warning-sign"></i> View All Practitioners
          </a>
        </li>
        <li>
          <a href="../docs/docs.html">
              <i class="icon-book"></i> Christine Stueve
          </a>
        </li>
        <li>
          <a href="../docs/docs.html">
              <i class="icon-book"></i> Michelle Guggenberger
          </a>
        </li>
      </ul>
    </li>
	  

    <li class="dropdown">
      <a class="dropdown-toggle" data-toggle="dropdown"><i class="icon-share-alt"></i> Practice Areas <span class="caret"></span></a>

      <ul class="dropdown-menu">
        <li>
          <a href="../error_404.html">
              <i class="icon-warning-sign"></i> Error 404
          </a>
        </li>

        

        <li>
          <a href="../docs/docs.html">
              <i class="icon-book"></i> Documentation
          </a>
        </li>

      </ul>
    </li>
	
	
	
	
	
	

	  
  </ul>
	
	
	
	

	
	

</nav>



<section id="main">
  <div class="top-nav">
  <div class="container-fluid">
	
	  
	  
	  
	  
    <div class="row-fluid search-button-bar-container">
      <div class="span12">
        <ul class="breadcrumb">
          <li><a href="#"><%= adminCompany.getLabel() %></a></li>
          <li class="active"><a href="#"><i class="icon-calendar"></i> Schedule</a></li>
		  <!--
          <li><a href="#">Nice</a></li>
          <li><a href="#">Breadcrumbs</a></li>
          <li class="active"><a href="#">Here</a></li>
		  -->
        </ul>
        <a class="search-button-trigger" href="#"><i class="icon-search"></i></a>
      </div>
    </div>
    <div class="row-fluid search-bar-nav">
      <div class="span12">
        <form>
          <input type="text" class="search" placeholder="Search...">
        </form>
      </div>
    </div>
 

	  
	  
	  
	  
  </div>
</div>

  <div class="container-fluid">
	  
	  
    <div id="mainContainer" class="row-fluid">
		
		
	  </div>
	  
	  

		  <div class="row-fluid">
			<div class="span12">
			  <div class="footer">
			
				  <img src="../../images/valonyx-logo.png" />
				<div>2013 &copy; Valonyx, LLC</div>
				<div>Carefully crafted by <a href="https://wrapbootstrap.com/user/andrei4002">andrei4002</a></div>
			  </div>
			</div>
		</div>
	  

	  
	  
  </div>
	
</section>

<script type="text/html" id="template-notification">
  <div class="notification animated fadeInLeftMiddle fast{{ item.itemClass }}">
    <div class="left">
      <div style="background-image: url({{ item.imagePath }})" class="{{ item.imageClass }}"></div>
    </div>
    <div class="right">
      <div class="inner">{{ item.text }}</div>
      <div class="time">{{ item.time }}</div>
    </div>

    <i class="icon-remove-sign hide"></i>
  </div>
</script>
<script type="text/html" id="template-notifications">
  <div class="container">
    <div class="row" id="notifications-wrapper">
      <div id="notifications" class="{{ bootstrapPositionClass }} notifications animated">
        <div id="dismiss-all" class="dismiss-all button blue">Dismiss all</div>
        <div id="content">
          <div id="notes"></div>
        </div>
      </div>
    </div>
  </div>
</script>




<script src="../../javascripts/application.js" type="text/javascript"></script>
<script src="../../javascripts/docs.js" type="text/javascript"></script>
<script src="../../javascripts/docs_charts.js" type="text/javascript"></script>
<script src="../../javascripts/documentation.js" type="text/javascript"></script>
<script src="../../javascripts/prettify.js" type="text/javascript"></script>

<link href="../../stylesheets/prettify.css" media="screen" rel="stylesheet" type="text/css" />
<script type="text/javascript">
    prettyPrint()
</script>

<%@ include file="channels/schedule-dynamic-javascript.jsp" %>

<script type='text/javascript' src='../src/_loader.js'></script>

<script src="scripts/schedule.js" type="text/javascript"></script>

<script type='text/javascript'>

	$(document).ready(function() {
		
		$forXm = $('#newApptForm');
		$forXm.submit(function(){
			
			var jqxhr = $.post($(this).attr('action'), $(this).serialize(), function(data) {
				
				//$(function () { $("#editAppt").modal('hide'); });
			
			 
			 if (data) {
				document.getElementById('alertModalText').firstChild.nodeValue = data;
				$(function () { $("#alertModal").modal('show'); });
			 } else {
				$(function () { $("#editAppt").modal('hide'); });
				
			 }
				
			  
			})
			.done(function(data) {
				
			})
			.fail(function(data) {  })
			.always(function(data) {  });
			
			
			/*
		   $.post($(this).attr('action'), $(this).serialize(), function(response){
			   
			   
			   alert(response);
			   
			   
		   },'json');
		   */
		   return false;
		});
		
		$forXc = $('#newClientForm');
		$forXc.submit(function(){
			
			var jqxhr = $.post($(this).attr('action'), $(this).serialize(), function(data) {
			 if (data) {
				document.getElementById('alertModalText').firstChild.nodeValue = data;
				$(function () { $("#alertModal").modal('show'); });
			 } else {
				$(function () { $("#newClient").modal('hide'); });	
			 }  
			})
			.done(function(data) {
				
			})
			.fail(function(data) {  })
			.always(function(data) {  });
			
		   return false;
		});

		refresh();
		
		//renderCalendar(9);
		//drawCalendar();
		
		//$('.timepicker').timepicker();

	});

	//var noShowArr = new Array();
	//var lateArr = new Array();
	//var checkedInArr = new Array();
<%

String calendarView = (String)session.getAttribute("calendarView");
if (calendarView == null) {
	session.setAttribute("calendarView", "resourceDay");
	calendarView = "resourceDay";
}

%>
	var selected_view = '<%= calendarView %>';

    var forceRefresh = true;
	var isNavigating = false;
	var disconnected = false;
	var block = false;
    var block_count = 0;
	var can_block = false;
	
	var mTimer;

    function refresh() {
		if (disconnected)
			document.getElementById('today_date').firstChild.nodeValue = 'Attempting to Reconnect...';
		if (!block) {
			//alert('r2');
			if (can_block)
				block = true;
			if (forceRefresh) {
				//processCommand('forceRefresh');
				doAjax('forceRefresh', selected_view);
			} else {
				//doAjax('forceRefresh', selected_view);
				doAjax('refresh', selected_view);
			}
		}
		else {
			block_count++;
			document.getElementById('today_date').firstChild.nodeValue = 'Blocked...(' + block_count + ')';
		}

		if (disconnected)
			mTimer = setTimeout('refresh();', 20000);
		else
			mTimer = setTimeout('refresh();', 5000);
    }

	
	
	var scheduledArr = new Array();
	var renderArrayIndex = 0;
	var renderArray = new Array();
	var renderArrayIndexMonth = 0;
	var renderArrayMonth = new Array();
	var renderArrayIndexWeek = 0;
	var renderArrayWeek = new Array();
	var offtimeArray = new Array();

	var prev_num_rendered = -1;

	var xml_response;
	
	var timeoutCleared = false;
	
	var nonClientIds = -1;

	function doAjax(command, parameter, arg1, arg2, arg3, arg4, arg5, arg6, arg7) {
		
		//alert('command >' + command);
		if (command != 'refresh' && command != 'forceRefresh') {
			clearTimeout(mTimer);
			timeoutCleared = true;
		}
		
		//alert('doAjax() invoked >' + command);
		
		//$("input").prop('disabled', true);

		// fire off the request to /form.php
		$.ajax({
			url: "<%= CUBean.getProperty("cu.webDomain") %>/ScheduleServletPlastique.html",
			type: "post",
			data: { command: command, parameter: parameter, arg1: arg1, arg2: arg2, arg3: arg3, arg4: arg4, arg5: arg5, arg6: arg6, arg7: arg7 },
			//data: serializedData,
			// callback handler that will be called on success
			success: function(response, textStatus, jqXHR){
				// log a message to the console
				//console.log("Hooray, it worked!");
				//alert("Hooray, it worked!");
				
				//alert('appt >' + jqXHR.responseText);
				//alert('appt length >' + jqXHR.responseXML.getElementsByTagName("appt").length);
				if ((jqXHR.responseXML.getElementsByTagName("refresh").length > 0) || (jqXHR.responseXML.getElementsByTagName("status").length > 0))
				{
					if (jqXHR.responseXML.getElementsByTagName("status").length > 0) {
						//renderArray = new Array(); // dump the array, I guess
						//alert('status');
					} else {
						//alert('refresh');
					}
				
				
					
					//alert('got appt data');
				
					var num_rendered = renderStatus(jqXHR.responseXML);
					//var num_rendered = 0;
					//alert(num_rendered);
				
					
					var dateElement;
					var refresh = false;
					if (jqXHR.responseXML.getElementsByTagName("refresh").length > 0) {
						refresh = true;
						xml_response = jqXHR.responseXML.getElementsByTagName("refresh")[0];
						if (xml_response.getElementsByTagName("date"))
							dateElement = xml_response.getElementsByTagName("date")[0];
					} else {
						xml_response = jqXHR.responseXML.getElementsByTagName("status")[0];
						if (xml_response.getElementsByTagName("date"))
							dateElement = xml_response.getElementsByTagName("date")[0];
					}
				
					var y;
					var m;
					var d;
					if (dateElement) {
					   
					   if (dateElement.getAttribute("y")) {
						   
						   y = dateElement.getAttribute("y");
						   m = dateElement.getAttribute("m");
						   d = dateElement.getAttribute("d");
						   
							var d_str = (parseInt(m) + 1) + '/' + d + '/' + y;
						
							if (date_str && (d_str != date_str)) {
								//alert('go to date for some reason.. >' + d_str + '  >' + date_str);
								$("#calendarX").fullCalendar('gotoDate', dateElement.getAttribute("y"), dateElement.getAttribute("m"), dateElement.getAttribute("d"));
							}
						
						    var month_str = '' + (parseInt(m) + 1);
							if (parseInt(m) < 9) {
								month_str = '0' + (parseInt(m) + 1);
							}
						    var date_str = '' + (parseInt(d));
							if (parseInt(d) < 10) {
								date_str = '0' + (parseInt(d));
							}
							d_str = month_str + '/' + date_str + '/' + y;
							if (d_str != document.getElementById('datetimepicker').value) {
								//alert('update dates');
								document.getElementById('datetimepicker').value = d_str;
							}
					   }
						
					}
					
					if (num_rendered != prev_num_rendered) {
						//alert('render cal');
						renderCalendar(num_rendered > 0 ? 9 : 12);
						drawCalendar(selected_view, y, m, d); // <-- this is resetting the calendar date...
						prev_num_rendered = num_rendered;
						//$("#calendarX").fullCalendar('addEventSource', renderArray);
					}
				
					//alert('start appt');
					//renderArrayIndex = 0;
					xml_response = xml_response.getElementsByTagName("schedule")[0];
					var index = 0;
					//alert(xml_response.getElementsByTagName("appt").length);
					while (xml_response.getElementsByTagName("appt")[index] != null) {
						
						var del = xml_response.getElementsByTagName("appt")[index].getAttribute("delete");

						var key = xml_response.getElementsByTagName("appt")[index].childNodes[0].childNodes[0].nodeValue;
						var value = xml_response.getElementsByTagName("appt")[index].childNodes[1].childNodes[0].nodeValue;

						//alert(key);
						//alert(value);
						
						//var practitioner_id = key.split("|")[1];
						//alert('practitioner_id >' + practitionerArray[key.split("|")[1]]["label"]);
						
						eval("var apptArray = " + value);
						
						if (apptArray["start"]) {
							//alert(apptArray["label"]);
							
							//typeArray[apptArray["type"]]["bg"];
							
							//alert(typeArray[apptArray["type"]]["bg"]);
							
							if (apptArray["cid"] == '0') {
								renderAppointment(key.split("|")[1], apptArray["id"], apptArray["cid"], apptArray["label"], "", apptArray["start"], apptArray["end"], false, "", apptArray["cm"], apptArray["state"], apptArray["type"], '#' + typeArray[apptArray["type"]]["bg"], apptArray["duration"], false, apptArray["new"]);
							} else {
								renderAppointment(key.split("|")[1], apptArray["id"], apptArray["cid"], apptArray["label"], "", apptArray["start"], apptArray["end"], false, "", apptArray["cm"], apptArray["state"], apptArray["type"], '#' + typeArray[apptArray["type"]]["bg"], apptArray["duration"], true, apptArray["new"]);
								renderAppointment('1', '1' + apptArray["id"], apptArray["cid"], apptArray["label"], "", apptArray["start"], apptArray["end"], false, "", apptArray["cm"], apptArray["state"], apptArray["type"], '#' + typeArray[apptArray["type"]]["bg"], apptArray["duration"], true, apptArray["new"]);
							}
							
						}
						
						index++;
					}
				
					renderAppointments();
					//alert('done appt');
				
					//renderStatus(jqXHR.responseXML);
					
					
					if (forceRefresh) {
						forceRefresh = false;
					}
					isNavigating = false;
					
				}
				else if (jqXHR.responseXML.getElementsByTagName("clients").length > 0)
				{
					//xml_response = httpRequest.responseXML.getElementsByTagName("clients")[0];
					//showPeople(xml_response);
					
					xml_response = jqXHR.responseXML.getElementsByTagName("clients")[0];
					showPeople(xml_response);
				}
				else if (jqXHR.responseXML.getElementsByTagName("codes").length > 0)
				{
					xml_response = jqXHR.responseXML.getElementsByTagName("codes")[0];
					showCodes(xml_response);
				}
				else if (jqXHR.responseXML.getElementsByTagName("checkout").length > 0)
				{
					alert('gots stuff');
					xml_response = jqXHR.responseXML.getElementsByTagName("checkout")[0];
					//alert(xml_response.getAttribute("p"));
					showCheckout(xml_response);
				}
				else if (jqXHR.responseXML.getElementsByTagName("pdr").length > 0)
				{
					isNavigating = true;
					var dateElement = jqXHR.responseXML.getElementsByTagName("pdr")[0];
					$("#calendarX").fullCalendar('gotoDate', dateElement.getAttribute("y"), dateElement.getAttribute("m"), dateElement.getAttribute("d"));
				}
				
			},
			// callback handler that will be called on error
			error: function(jqXHR, textStatus, errorThrown){
				// log the error to the console
				
				console.log(
					"The following error occured: "+
					textStatus, errorThrown
				);
			},
			// callback handler that will be called on completion
			// which means, either on success or error
			complete: function(){
				// enable the inputs
				//$("input").prop('disabled', false);
				
				if (timeoutCleared) {
					timeoutCleared = false;
					refresh();
				}
			}
		});

	}
	
	function renderAppointment(pid, id, cid, title, url, start, end, allDay, location, desc, state, type, color, duration, editable, isNew) {
	
		//alert('title >' + title + " >" + id + " >" + start + " >" + duration + " >" + type + " >" + <%= AppointmentTypeBean.OFFTIME_APPOINTMENT_TYPE_TYPE %>);
		
		var isEdit = false;
		
		if (id == 0) {
			id = nonClientIds;
			nonClientIds--;
			
			//isOfftime = true;
			var key = pid + title + start;
			if (offtimeArray[key]) {
				isEdit = true;
			} else {
				offtimeArray[key] = id;
			}
			
			//alert('isEdit >' + isEdit + '  key >' + key);
		}
	
		var event = {
		 resourceId  : pid,
		 id          : id,
		 cid         : cid,
		 title       : title,
		 start       : start,
		 end         : end,
		 allDay      : allDay,
		 state       : state,
		 description : desc,
		 type        : type,
		 backgroundColor: color,
		 borderColor: color,
		 duration    : duration,
		 editable    : editable
		};
	
	
		
		if (scheduledArr[id]) {
			isEdit = true;
		}
		scheduledArr[id] = event;
	
		if (isEdit) {
			// I need to find the corresponding entry in the array and edit/replace it...
			
			for (var i = 0; i < renderArray.length; i++) {
				var existingEvent = renderArray[i];
				if (event.id == existingEvent.id) {
					renderArray[i] = event;
					break;
				}
			}
			
			for (var i = 0; i < renderArrayMonth.length; i++) {
				var existingEvent = renderArrayMonth[i];
				if (event.id == existingEvent.id) {
					renderArrayMonth[i] = event;
					break;
				}
			}
			
			for (var i = 0; i < renderArrayWeek.length; i++) {
				var existingEvent = renderArrayWeek[i];
				if (event.id == existingEvent.id) {
					renderArrayWeek[i] = event;
					break;
				}
			}
			
		} else {
			renderArray[renderArrayIndex] = event;
			renderArrayIndex++;
			
			if (id > 0) {
				renderArrayMonth[renderArrayIndexMonth] = event;
				renderArrayIndexMonth++;
				
				renderArrayWeek[renderArrayIndexWeek] = event;
				renderArrayIndexWeek++;
			}
			
			if (isNew) {
				renderNotification("Good day sire!!", 'New appointment created for ' + title, 20);
			}
		}
		
		//var realArray = $.makeArray( renderArray );
		
		//renderArray = jQuery.map(realArray, function(e){
			//return (e.cid > 0) ? e : null;
			//return (e.cid > 0) ? e : null;
		  //});
		  //renderArrayIndex = renderArray.length;
	
	}

	function renderAppointments() {
		
		// I should only be re-fetching if there's an actual change in the source array
		
		//alert('renderAppointments() invoked >' + renderArray.length);
		
		
		$("#calendarX").fullCalendar('removeEventSource', renderArray);
		$("#calendarX").fullCalendar('removeEventSource', renderArrayMonth);
		$("#calendarX").fullCalendar('removeEventSource', renderArrayWeek);
		
		if (selected_view == 'resourceDay') {
			$("#calendarX").fullCalendar('addEventSource', renderArray);
		} else if (selected_view == 'agendaWeek') {
			$("#calendarX").fullCalendar('addEventSource', renderArrayWeek);
		} else {
			$("#calendarX").fullCalendar('addEventSource', renderArrayMonth);
		}
		
		$("#calendarX").fullCalendar( 'refetchEvents' );
		
	}

	function renderStatus(xml_str) {
		
		var container = document.getElementById('mainContainer');
		//deleteAllRows(container);
		
		//alert('deleted');
		
		/*
		<div id="statusDiv" class="span3">
		</div>
		 */
		
		var status_div = document.getElementById('statusDiv');
		if (status_div) {
			deleteAllRows(status_div);
		} else {
			status_div = document.createElement('div');
			status_div.setAttribute('id', 'statusDiv');
			status_div.setAttribute('class', 'span3');
			container.appendChild(status_div);
		}
			
		
		var numRendered = 0;
		
		/*
		var xml_response = xml_str.getElementsByTagName("schedule")[0];
		if (xml_response.getElementsByTagName("appt").length > 0) {
			
			var numScheduled = 0;
			var index = 0;
			while (xml_str.getElementsByTagName("appt")[index] != null)
			{
				var key = xml_str.getElementsByTagName("appt")[index].childNodes[0].childNodes[0].nodeValue;
				var value = xml_str.getElementsByTagName("appt")[index].childNodes[1].childNodes[0].nodeValue;
				eval("var apptArray = " + value);
				if (apptArray["cid"] != '0') {
					if (apptArray["state"] == '1')
						numScheduled++;
				}

				index++;
			}
			
			if (numScheduled > 0)
				status_div.appendChild(renderStatusSub('statusScheduled', numScheduled, xml_response));
			
			numRendered = numScheduled;
		}
		*/
				
		xml_response = xml_str.getElementsByTagName("schd")[0];
		if (xml_response && xml_response.getElementsByTagName("appt").length > 0) {
			status_div.appendChild(renderStatusSub('statusScheduled', xml_response.getElementsByTagName("appt").length, xml_response));
			numRendered += xml_response.getElementsByTagName("appt").length;
		}
				
		xml_response = xml_str.getElementsByTagName("late")[0];
		if (xml_response && xml_response.getElementsByTagName("appt").length > 0) {
			status_div.appendChild(renderStatusSub('statusLate', xml_response.getElementsByTagName("appt").length, xml_response));
			numRendered += xml_response.getElementsByTagName("appt").length;
		}
		
		xml_response = xml_str.getElementsByTagName("c-in")[0];
		if (xml_response && xml_response.getElementsByTagName("appt").length > 0) {
			status_div.appendChild(renderStatusSub('statusCheckedIn', xml_response.getElementsByTagName("appt").length, xml_response));
			numRendered += xml_response.getElementsByTagName("appt").length;
		}

		xml_response = xml_str.getElementsByTagName("no-show")[0];
		if (xml_response && xml_response.getElementsByTagName("appt").length > 0) {
			status_div.appendChild(renderStatusSub('statusNoShow', xml_response.getElementsByTagName("appt").length, xml_response));
			numRendered += xml_response.getElementsByTagName("appt").length;
		}
		
		return numRendered;
	}

	function renderStatusSub(id, num, xml_str) {
		
		var status_scheduled = document.createElement('div');
		status_scheduled.setAttribute('id', id);
		status_scheduled.setAttribute('class', 'black-box tex');
		
		var status_header = document.createElement('div');
		status_header.setAttribute('class', 'tab-header');
		
		var status_span = document.createElement('span');
		if (id == 'statusScheduled') {
			status_span.setAttribute('class', 'badge blue');
			status_header.appendChild(status_span);
			status_header.appendChild(document.createTextNode(' Scheduled'));
		} else if (id == 'statusLate') {
			status_span.setAttribute('class', 'badge yellow');
			status_header.appendChild(status_span);
			status_header.appendChild(document.createTextNode(' Late'));
		} else if (id == 'statusCheckedIn') {
			status_span.setAttribute('class', 'badge green');
			status_header.appendChild(status_span);
			status_header.appendChild(document.createTextNode(' Checked In'));
		} else if (id == 'statusNoShow') {
			status_span.setAttribute('class', 'badge red');
			status_header.appendChild(status_span);
			status_header.appendChild(document.createTextNode(' No Show'));
		}
		status_span.appendChild(document.createTextNode(num));
		
		status_scheduled.appendChild(status_header);
		
		var ul_element = document.createElement('ul');
		ul_element.setAttribute('class', 'recent-comments');
		
		var index = 0;
		//alert('late >' + xml_response.getAttribute("num"));
		while (xml_str.getElementsByTagName("appt")[index] != null)
		{
			var key = xml_str.getElementsByTagName("appt")[index].childNodes[0].childNodes[0].nodeValue;
			var value = xml_str.getElementsByTagName("appt")[index].childNodes[1].childNodes[0].nodeValue;
			//alert(key);
			//alert(value);

			//status_div.appendChild(renderStatusSub('statusLate'));
			eval("var apptArray = " + value);
			if (apptArray["cid"] != '0') {
				
				if (id == 'statusScheduled') {
					if (apptArray["state"] == '1') {
						ul_element.appendChild(renderStatusClient(key, apptArray, '../../images/avatar.png'));
					}
				} else {
					ul_element.appendChild(renderStatusClient(key, apptArray, '../../images/avatar.png'));
				}
			}
			index++;
		}
		
		status_scheduled.appendChild(status_header);
		status_scheduled.appendChild(ul_element);
		
		return status_scheduled;
	}

	function renderStatusClient(key, apptArray, avatar) {
		
		var li_element = document.createElement('li');
		li_element.setAttribute('class', 'separator');
		
		var avatar_div = document.createElement('div');
		avatar_div.setAttribute('class', 'avatar pull-left');
		var avatar_anchor = document.createElement('a');
		avatar_anchor.setAttribute('href', "javascript:alert('" + avatar + "');");
		var avatar_image = document.createElement('img');
		avatar_image.setAttribute('src', avatar);
		avatar_anchor.appendChild(avatar_image);
		avatar_div.appendChild(avatar_anchor);
		
		var post_div = document.createElement('div');
		post_div.setAttribute('class', 'article-post');
		
		
		var div_1 = document.createElement('div');
		div_1.setAttribute('class', 'user-info');
		
		var user_anchor = document.createElement('a');
		user_anchor.setAttribute('href', "");
		user_anchor.appendChild(document.createTextNode(apptArray["label"]));
		
		div_1.appendChild(document.createTextNode(' '));
		div_1.appendChild(user_anchor);
		
		
		
		var div_2 = document.createElement('div');
		div_2.setAttribute('class', 'user-content');
		//div_2.appendChild(document.createTextNode('11:30am Supplement Check with Christine'));
		div_2.appendChild(document.createTextNode(key.split("|")[0] + ' ' + typeArray[apptArray["type"]]["label"] + ' with ' + practitionerArray[key.split("|")[1]]["label"]));
		
		
		var button_group_div = document.createElement('div');
		button_group_div.setAttribute('class', 'btn-group');
		
		var button_1 = document.createElement('button');
		button_1.setAttribute('class', 'button black mini');
		button_1.setAttribute('onclick', "javascript:alert('do check-in stuff');");
		var i_1 = document.createElement('i');
		i_1.setAttribute('class', 'icon-ok');
		button_1.appendChild(i_1);
		button_1.appendChild(document.createTextNode(' Check-In '));
		
		var button_2 = document.createElement('button');
		button_2.setAttribute('class', 'button black mini');
		button_2.setAttribute('onclick', "javascript:alert('do remove stuff');");
		var i_2 = document.createElement('i');
		i_2.setAttribute('class', 'icon-remove');
		button_2.appendChild(i_2);
		button_2.appendChild(document.createTextNode(' '));
		
		button_group_div.appendChild(button_1);
		button_group_div.appendChild(button_2);
		
		post_div.appendChild(div_1);
		post_div.appendChild(div_2);
		post_div.appendChild(button_group_div);
		
		li_element.appendChild(avatar_div);
		li_element.appendChild(post_div);
		
		return li_element;
	}

	function renderCalendar(span) {
		var container = document.getElementById('mainContainer');
		deleteAllRows(container);
		
		//alert('deleted');
		
		/*
		<div id="calendarDiv" class="span9">
		  <div class="box padded">
			<div id='calendarX'></div>
		  </div>
		</div>
		 */
		
		var calendar_div = document.createElement('div');
		calendar_div.setAttribute('id', 'calendarDiv');
		calendar_div.setAttribute('class', 'span' + span);
		
		var div1 = document.createElement('div');
		div1.setAttribute('class', 'box padded');
		
		var div2 = document.createElement('div');
		div2.setAttribute('id', 'calendarX');
		
		div1.appendChild(div2);
		calendar_div.appendChild(div1);
		//container.appendChild(calendar_div);
		container.insertBefore(calendar_div, container.firstChild);
	}
	
	function deleteAllRows(div) {
		if (div) {
			var childArr = div.childNodes;
			
			for (i = (childArr.length - 1); i > -1; i--) {
				if (childArr[i].id) {
					//alert(childArr[i].id);
					if (childArr[i].id != 'statusDiv')
						div.removeChild(childArr[i]);
				}
			}
		}
	}
	
	function removeAllChildren(e) {
		if (e) {
			var childArr = e.childNodes;
			for (i = (childArr.length - 1); i > -1; i--) {
				e.removeChild(childArr[i]);
			}
		}
	}

	function removeChildElement(eid, cid) {
		document.getElementById(eid).removeChild(document.getElementById(cid));
	}

	function renderNotification(header, text, dismiss) {
		Notifications.push({
			imagePath: "../../images/cloud.png",
			text: "<h4><strong>" + header + "</strong><br />" + text + "</h4>",
			autoDismiss: dismiss
		  });
	}

</script>



				

<script language="JavaScript" type="text/JavaScript">
	<!--
		    
$(document).ready(function(){

/*
var event = {
 id          : '123',
 title       : 'New Event',
 url         : 'http://thearena.com/',
 start       : "Sun, 2 Dec 2012 13:00:00 CST",
 end         : "Sun, 2 Dec 2012 17:00:00 CST",
 allDay      : false,
 location    : 'The Arena',
 description : 'Big Event',
 backgroundColor: 'red',
 editable    : true
};
$("#calendarX").fullCalendar( 'renderEvent', event, true );
	*/

});

	//-->
</script>

<%@ include file="channels/channel-appointment.jsp" %>
<%@ include file="channels/channel-new-client.jsp" %>
<%@ include file="channels/channel-checkout.jsp" %>
<%@ include file="channels/channel-client-info.jsp" %>


<div id="alertModal" class="black-box modal hide fade">
  <div class="modal-header tab-header">
    <button type="button" class="close" data-dismiss="modal">&times;</button>
    <span>Oh Snap!  We have a problem...</span>
  </div>
  <div class="modal-body separator">
    <h4><span id="alertModalText">This is some error text</span></h4>
  </div>
  <div class="modal-footer">
    <div class="inner-well">
      <a class="btn btn-danger" data-dismiss="modal">D'oh!</a>
    </div>
  </div>
</div>

</body>
</html:html>
