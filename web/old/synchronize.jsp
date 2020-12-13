<%@ page contentType="text/html" import="com.badiyan.uk.online.tasks.*, java.net.*, java.io.*, java.util.*, com.badiyan.uk.beans.*, com.badiyan.torque.*, com.badiyan.uk.exceptions.*, com.badiyan.uk.online.beans.*, com.badiyan.uk.tasks.*" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="struts-bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="struts-html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="struts-logic" %>

<jsp:useBean id="loginBean" class="com.badiyan.uk.online.beans.UKOnlineLoginBean" scope="session" />
<jsp:useBean id="requiredEnrollmentList" class="com.badiyan.uk.beans.ListerBean" scope="session"/>
<jsp:useBean id="recommendedList" class="com.badiyan.uk.beans.ListerBean" scope="session"/>

<%@ include file="channels/channel-session.jsp" %>
<%@ include file="channels/channel-permissions.jsp" %>
<%@ include file="channels/channel-permissions-tablet.jsp" %>

<%
UKOnlinePersonBean tablet_user = null;

String session_sync_string = (String)session.getAttribute("sync");
boolean sync_started = ((session_sync_string != null) && (session_sync_string.equals("true")));

String employee_id = "";

boolean is_connected = CUBean.isConnected();
boolean local_data_exists = SyncObjectBean.dataExists();
boolean is_tablet_user_approved = false;
boolean employee_id_exists = false;
boolean employee_id_available = false;
boolean update_user_from_server_success = false;

boolean pretty_bad_error = false;
String pretty_bad_error_message = "";
boolean really_bad_error = false;
String really_bad_error_message = "";

boolean need_to_confirm_name = false;
String name_to_confirm = "";

boolean name_confirmed = false;

try
{
    if (is_connected)
    {
	tablet_user = UKOnlinePersonBean.getTabletUser();
	System.out.println("tablet_user >" + tablet_user);
	employee_id = tablet_user.getUsername();
	is_tablet_user_approved = tablet_user.isServerApproved();
	if (!is_tablet_user_approved)
	{
	    employee_id_exists = UKOnlinePersonBean.doesEmployeeIDExist(employee_id);
	    employee_id_available = UKOnlinePersonBean.isEmployeeIDAvailable(employee_id);
	    
	    System.out.println("employee_id_exists >" + employee_id_exists);
	    System.out.println("employee_id_available >" + employee_id_available);
	    
	    if (employee_id_exists && employee_id_available)
	    {
		need_to_confirm_name = true;
		name_to_confirm = UKOnlinePersonBean.getFullName(employee_id);
	    }
	}
    }
}
catch (Exception x)
{
    pretty_bad_error = true;
    pretty_bad_error_message = x.getMessage();
    x.printStackTrace();
}

boolean initial_sync = true;
String initial_sync_string = (String)session.getAttribute("initial_sync");
if ((initial_sync_string != null) && (initial_sync_string.equals("false")))
    initial_sync = false;
if (is_tablet_user_approved)
    initial_sync = false;
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
		<script language="JavaScript" type="text/JavaScript">
		    <!--

			var httpRequest;
			var mTimer;
			var global_line_number = 1;
			
			function receiveSyncObjectsFromServerInit()
			{
			    hideLayer('syncbutton');
			    SwapWorking(2);
			    document.getElementById('head_sync').innerHTML = 'Synchronizing...';
			    receiveSyncObjectsFromServer();
			}
			
			function receiveSyncObjectsFromServerInitPersonal()
			{
			    SwapWorking(3);
			    global_line_number = 1;
			    receiveSyncObjectsFromServer();
			}
			
			function receiveSyncObjectsFromServer()
			{
			    if (window.ActiveXObject)
				httpRequest = new ActiveXObject("Microsoft.XMLHTTP");
			    else if (window.XMLHttpRequest)
				httpRequest = new XMLHttpRequest();

			    httpRequest.open("POST", 'http://localhost:<%= System.getProperty("cu.localPort") %>/<%= System.getProperty("cu.clientPackageName") %>/ProcessSyncObjectsServlet.html', true);
			    httpRequest.setRequestHeader('Content-Type','application/x-www-form-urlencoded');
			    httpRequest.onreadystatechange = function() {processReceiveSyncObjectsFromServer(); } ;
			    httpRequest.send('command=1');
			}
			
			function processReceiveSyncObjectsFromServer()
			{
			    if (httpRequest.readyState == 4)
			    {
				var killTimer = false;
				if(httpRequest.status == 200)
				{
				    if (httpRequest.responseXML.getElementsByTagName("status").length > 0)
				    {
					var xml_response = httpRequest.responseXML.getElementsByTagName("status")[0];
					eval("document.getElementById('global_" + global_line_number++ + "').innerHTML = xml_response.childNodes[0].nodeValue");
				    }
				    else if (httpRequest.responseXML.getElementsByTagName("complete").length > 0)
				    {
					var xml_response = httpRequest.responseXML.getElementsByTagName("complete")[0];
					eval("document.getElementById('global_" + global_line_number++ + "').innerHTML = xml_response.childNodes[0].nodeValue");
					SwapComplete(2);
					killTimer = true;
<%
if (initial_sync)
{
%>
					document.getElementById('head_sync').innerHTML = 'Initial Synchronize Complete.  Click <strong>Next</strong> to continue.';
					showLayer('syncbutton');
					document.getElementById('butsync').value='Next';
					document.getElementById('butsync').alt='Next';
					document.getElementById('butsync').onclick='';
					document.getElementById('hidden_status').value='Next';
					
<%
}
else
{
    // begin personal stuff
%>
					receiveSyncObjectsFromServerInitPersonal();
<%
}
%>
				    }
				    else if (httpRequest.responseXML.getElementsByTagName("error").length > 0)
				    {
					var xml_response = httpRequest.responseXML.getElementsByTagName("error")[0];
					eval("document.getElementById('global_" + global_line_number++ + "').innerHTML = 'Error : ' + xml_response.childNodes[0].nodeValue");
					SwapError(2);
					killTimer = true;
				    }
				    else if (httpRequest.responseXML.getElementsByTagName("personal_status").length > 0)
				    {
					var xml_response = httpRequest.responseXML.getElementsByTagName("personal_status")[0];
					eval("document.getElementById('personal_" + global_line_number++ + "').innerHTML = xml_response.childNodes[0].nodeValue");
				    }
				    else if (httpRequest.responseXML.getElementsByTagName("personal_complete").length > 0)
				    {
					var xml_response = httpRequest.responseXML.getElementsByTagName("personal_complete")[0];
					eval("document.getElementById('personal_" + global_line_number++ + "').innerHTML = xml_response.childNodes[0].nodeValue");
					document.getElementById('head_sync').innerHTML = '<strong>Synchronize Complete</strong>';
					SwapComplete(3);
					killTimer = true;
				    }
				    else if (httpRequest.responseXML.getElementsByTagName("personal_error").length > 0)
				    {
					var xml_response = httpRequest.responseXML.getElementsByTagName("personal_error")[0];
					eval("document.getElementById('personal_" + global_line_number++ + "').innerHTML = 'Error : ' + xml_response.childNodes[0].nodeValue");
					SwapError(3);
					killTimer = true;
				    }
				}
				else
				{
				    eval("document.getElementById('global_" + global_line_number++ + "').innerHTML = 'Error : ' + httpRequest.status + ' : ' + httpRequest.statusText");
				    SwapError(2);
				    killTimer = true;
				}
				
				if (!killTimer)
				    mTimer = setTimeout('receiveSyncObjectsFromServer();',250);
			    }
			}

			if (document.images) {
			    Rollimage = new Array();

			    Rollimage[0]= new Image(32,32);
			    Rollimage[0].src = "images/gfxLoading-error.gif";
			    Rollimage[1] = new Image(32,32);
			    Rollimage[1].src = "images/gfxLoading-complete.gif";
			    Rollimage[2] = new Image(32,32);
			    Rollimage[2].src = "images/gfxLoading-working.gif";
			}

			function SwapError(x){
			  if (document.images)
				eval("document.m" + x + ".src = Rollimage[0].src");
			  return true;
			}

			function SwapComplete(x){
			  if (document.images)
				eval("document.m" + x + ".src = Rollimage[1].src");
			  return true;
			}

			function SwapWorking(x){
			  if (document.images)
				eval("document.m" + x + ".src = Rollimage[2].src");
			  return true;
			}
			
			function hideLayer(whichLayer)
			{
			    document.getElementById(whichLayer).style.visibility = "hidden";
			}
			
			function showLayer(whichLayer)
			{
			    document.getElementById(whichLayer).style.visibility = "visible";
			}

		    //-->
		</script>
	</head>

	<body>

		<div id="main">

<%@ include file="channels\channel-menu.jsp" %>

			<div class="horizontalRule"><img src="images/trspacer.gif" width="1" height="1" alt="" /></div>

			<!-- *** BEGIN Content *** -->
			<div id="content">

				<div class="content_TextBlock">
					<p class="headline">Synchronize</p>
<%
System.out.println("is_connected >" + is_connected);
System.out.println("initial_sync >" + initial_sync);
System.out.println("sync_started >" + sync_started);
System.out.println("is_tablet_user_approved >" + is_tablet_user_approved);
System.out.println("employee_id_exists >" + employee_id_exists);
System.out.println("need_to_confirm_name >" + need_to_confirm_name);

if (is_connected)
{
    if (initial_sync)
    {
	if (sync_started)
	{
%>
					<p>As the synchronization proceeds, the actions taking place will be listed. Please wait until you see <strong>Synchronize Complete</strong> before proceeding with other tasks.</p>
					<struts-html:form action="/synchronize">
						<p><span id="head_sync">Synchronizing...</span></p>
						<div class="loginItem">
							<div class="left"></div>
							<div class="right"><input type="hidden" name="status" value="none"></div>
							<div class="end"></div>
						</div>
					</struts-html:form>
<%
	}
	else
	{
%>
					<p>As the synchronization proceeds, the actions taking place will be listed. Please wait until you see <strong>Synchronize Complete</strong> before proceeding with other tasks.</p>
					<struts-html:form action="/synchronize">
						<p><span id="head_sync">Please click <strong>Synchronize</strong> to begin.</span></p>
						<div class="loginItem" id="syncbutton">
							<div class="left"></div>
							<div class="right"><input class="formbutton" id="butsync" type="submit" value="Synchronize" alt="Synchronize" onclick="javascript:receiveSyncObjectsFromServerInit();return false;" /><input type="hidden" id="hidden_status" name="status" value="sync"></div>
							<div class="end"></div>
						</div>
					</struts-html:form>
<%
	}
    }
    else if (is_tablet_user_approved)
    {
	if (sync_started)
	{
%>
					<p>As the synchronization proceeds, the actions taking place will be listed. Please wait until you see <strong>Synchronize Complete</strong> before proceeding with other tasks.</p>
					<struts-html:form action="/synchronize">
						<p><span id="head_sync">Synchronizing...</span></p>
						<div class="loginItem">
							<div class="left"></div>
							<div class="right"><input type="hidden" name="status" value="sync"></div>
							<div class="end"></div>
						</div>
					</struts-html:form>
<%
	}
	else
	{
%>
					<p>As the synchronization proceeds, the actions taking place will be listed. Please wait until you see <strong>Synchronize Complete</strong> before proceeding with other tasks.</p>
					<struts-html:form action="/synchronize">
						<p><span id="head_sync">Please click <strong>Synchronize</strong> to begin.</span></p>
						<div class="loginItem" id="syncbutton">
							<div class="left"></div>
							<div class="right"><input class="formbutton" id="butsync" type="submit" value="Synchronize" alt="Synchronize" onclick="javascript:receiveSyncObjectsFromServerInit();return false;" /><input type="hidden" id="hidden_status" name="status" value="sync"></div>
							<div class="end"></div>
						</div>
					</struts-html:form>
<%
	}
    }
    else
    {
	if (!employee_id_exists)
	{
%>
					<p>Unable to find your Employee ID on the server.  Please <strong>verify</strong> your Employee ID below.  If this issue persists please contact the LMS administrator.</p>
					<p>&nbsp;</p>
					<div class="content_Login">
					    <struts-html:form action="/synchronize">
						<input id="dummy" name="dummy" type="hidden" />
						<div class="loginItem">
							<div class="left">EMPLOYEE ID</div>
							<div class="right"><input name="username" onfocus="select();" value="<%= employee_id %>" size="37" maxlength="50" class="inputbox" style="width: 206px;" /></div>
							<div class="end"></div>
						</div>
						<div class="loginItem">
							<div class="left"></div>
							<div class="right"><input class="formbutton" type="submit" value="Verify" alt="Verify" /><input type="hidden" name="status" value="verify"></div>
							<div class="end"></div>
						</div>
					    </struts-html:form>
					</div>
<%
	}
	else if (!employee_id_available)
	{
%>
					<p>The Employee ID that you specified is already in use by another user.  Please <strong>verify</strong> your Employee ID below.  If this issue persists please contact the LMS administrator.</p>
					<p>&nbsp;</p>
					<div class="content_Login">
					    <struts-html:form action="/synchronize">
						<input id="dummy" name="dummy" type="hidden" />
						<div class="loginItem">
							<div class="left">EMPLOYEE ID</div>
							<div class="right"><input name="username" onfocus="select();" value="<%= employee_id %>" size="37" maxlength="50" class="inputbox" style="width: 206px;" /></div>
							<div class="end"></div>
						</div>
						<div class="loginItem">
							<div class="left"></div>
							<div class="right"><input class="formbutton" type="submit" value="Verify" alt="Verify" /><input type="hidden" name="status" value="verify"></div>
							<div class="end"></div>
						</div>
					    </struts-html:form>
					</div>
<%
	}
	else if (need_to_confirm_name)
	{
%>
					<struts-html:form action="/synchronize">
					    <p>Please confirm that your name is:</p>
					    <p>&nbsp;</p>
					    <div class="content_Login">
						    <div class="loginItem">
							    <div class="left"><strong><%= name_to_confirm %></strong></div>
							    <div class="right"><input class="formbutton" type="submit" name="name_confirm" value="That's Me!" alt="That's Me!" /></div>
							    <div class="end"></div>
						    </div>
					    </div>
					    <p>If this is not you, please <strong>verify</strong> your Employee ID below.</p>
					    <p>&nbsp;</p>

					    <div class="content_Login">

						    <input id="dummy" name="dummy" type="hidden" />
						    <div class="loginItem">
							    <div class="left">EMPLOYEE ID</div>
							    <div class="right"><input name="username" onfocus="select();" value="<%= employee_id %>" size="37" maxlength="50" class="inputbox" style="width: 206px;" /></div>
							    <div class="end"></div>
						    </div>
						    <div class="loginItem">
							    <div class="left"></div>
							    <div class="right"><input class="formbutton" type="submit" value="Verify" alt="Verify" /><input type="hidden" name="status" value="verify"></div>
							    <div class="end"></div>
						    </div>
					    </div>
					</struts-html:form>
<%
	}
    }
}
else
{
%>
					<p>Unable to communicate with the server.  Please connect to the Internet and click <strong>Retry</strong>.</p>
					<struts-html:form action="/synchronize">
						<div class="loginItem">
							<div class="left"></div>
							<div class="right"><input class="formbutton" type="submit" value="Retry" alt="Retry" /><input type="hidden" name="status" value="retry"></div>
							<div class="end"></div>
						</div>
					</struts-html:form>
<%
}
%>
					<div class="horizontalRule"><img src="images/trspacer.gif" width="1" height="1" alt="" /></div>
				</div>


				<div class="content_Synchronize">
					<div class="statusColumn">
						<div class="headline"><img src="images/gfxLoading-stopped.gif" width="32" height="32" title="" alt="" name="m2" />Global LMS Data</div>
						<div class="status"><span id="global_1">&nbsp;</span></div>
						<div class="status"><span id="global_2">&nbsp;</span></div>
						<div class="status"><span id="global_3">&nbsp;</span></div>
						<div class="status"><span id="global_4">&nbsp;</span></div>
						<div class="status"><span id="global_5">&nbsp;</span></div>
						<div class="status"><span id="global_6">&nbsp;</span></div>
						<div class="status"><span id="global_7">&nbsp;</span></div>
					</div>
					<div class="statusColumn">
						<div class="headline"><img src="images/gfxLoading-stopped.gif" width="32" height="32" title="" alt="" name="m3" />Personal LMS Data</div>
						<div class="status"><span id="personal_1">&nbsp;</span></div>
						<div class="status"><span id="personal_2">&nbsp;</span></div>
						<div class="status"><span id="personal_3">&nbsp;</span></div>
						<div class="status"><span id="personal_4">&nbsp;</span></div>
						<div class="status"><span id="personal_5">&nbsp;</span></div>
						<div class="status"><span id="personal_6">&nbsp;</span></div>
						<div class="status"><span id="personal_7">&nbsp;</span></div>
						<div class="status"><span id="personal_8">&nbsp;</span></div>
					</div>
					<div class="end"></div>
				</div>




				<div class="content_TextBlock">
					<div class="horizontalRule"><img src="images/trspacer.gif" width="1" height="1" alt="" /></div>
				</div>

			</div>
			<!-- *** END Content *** -->

			<div class="horizontalRule"><img src="images/trspacer.gif" width="1" height="1" alt="" /></div>

<%@ include file="channels\channel-footer.jsp" %>

		</div>

	</body>

</html>


<%
session.removeAttribute("sync");
loginBean.enrollRequiredCourses();
requiredEnrollmentList.invalidateSearchResults();
recommendedList.invalidateSearchResults();
loginBean.getPerson().invalidateEnrollments();
%>