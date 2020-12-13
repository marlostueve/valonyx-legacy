<%@ page contentType="text/html" import="java.util.*, java.net.*, java.io.*, com.badiyan.uk.beans.*, com.badiyan.torque.*, com.badiyan.uk.exceptions.*, com.badiyan.uk.online.beans.*, com.badiyan.uk.tasks.*" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="struts-bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="struts-html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="struts-logic" %>

<jsp:useBean id="loginBean" class="com.badiyan.uk.online.beans.UKOnlineLoginBean" scope="session" />
<jsp:useBean id="courseSearch" class="com.badiyan.uk.online.beans.UKOnlineCourseSearchBean" scope="session"/>
<jsp:useBean id="resourceSearch" class="com.badiyan.uk.online.beans.UKOnlineResourceSearchBean" scope="session" />

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
			
			function installInit()
			{
			    //hideLayer('syncbutton');
			    SwapWorking(2);
			    document.getElementById('head_sync').innerHTML = 'Installing...';
			    install();
			}
			
			function install()
			{
			    if (window.ActiveXObject)
				httpRequest = new ActiveXObject("Microsoft.XMLHTTP");
			    else if (window.XMLHttpRequest)
				httpRequest = new XMLHttpRequest();

			    httpRequest.open("POST", 'http://localhost:<%= System.getProperty("cu.localPort") %>/<%= System.getProperty("cu.clientPackageName") %>/ProcessExternalContentServlet.html', true);
			    httpRequest.setRequestHeader('Content-Type','application/x-www-form-urlencoded');
			    httpRequest.onreadystatechange = function() {processInstall(); } ;
			    httpRequest.send('command=install');
			}
			
			function processInstall()
			{
			    if (httpRequest.readyState == 4)
			    {
				var killTimer = false;
				if(httpRequest.status == 200)
				{
				    if (httpRequest.responseXML.getElementsByTagName("status").length > 0)
				    {
					var xml_response = httpRequest.responseXML.getElementsByTagName("status")[0];
					eval("document.getElementById('activity_" + global_line_number++ + "').innerHTML = xml_response.childNodes[0].nodeValue");
				    }
				    else if (httpRequest.responseXML.getElementsByTagName("download").length > 0)
				    {
					var xml_response = httpRequest.responseXML.getElementsByTagName("download")[0];
					eval("document.getElementById('activity_" + global_line_number + "').innerHTML = xml_response.childNodes[0].nodeValue");
				    }
				    else if (httpRequest.responseXML.getElementsByTagName("complete").length > 0)
				    {
					var xml_response = httpRequest.responseXML.getElementsByTagName("complete")[0];
					eval("document.getElementById('activity_" + global_line_number++ + "').innerHTML = xml_response.childNodes[0].nodeValue");
					document.getElementById('head_sync').innerHTML = '<strong>Install Complete</strong>';
					SwapComplete(2);
					killTimer = true;
				    }
				    else if (httpRequest.responseXML.getElementsByTagName("error").length > 0)
				    {
					var xml_response = httpRequest.responseXML.getElementsByTagName("error")[0];
					eval("document.getElementById('activity_" + global_line_number++ + "').innerHTML = 'Error : ' + xml_response.childNodes[0].nodeValue");
					SwapError(2);
					killTimer = true;
				    }
				}
				else
				{
				    eval("document.getElementById('activity_" + global_line_number++ + "').innerHTML = 'Error : ' + httpRequest.status + ' : ' + httpRequest.statusText");
				    SwapError(2);
				    killTimer = true;
				}
				
				if (!killTimer)
				    mTimer = setTimeout('install();',250);
				else
				    document.location.href='index.jsp';
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

	<body onload="installInit();">

		<div id="main">

<%@ include file="channels\channel-menu.jsp" %>

			<div class="horizontalRule"><img src="images/trspacer.gif" width="1" height="1" alt="" /></div>

			<!-- *** BEGIN Content *** -->
			<div id="content">

				<div class="content_TextBlock">
					<p class="headline">Install Activities</p>
					
					<p>New activities have been found and are being installed. Please wait...</p>
					<p><span id="head_sync">Installing...</span></p>
					
					<div class="horizontalRule"><img src="images/trspacer.gif" width="1" height="1" alt="" /></div>
				</div>


				<div class="content_Synchronize">
					<div class="statusColumn">
						<div class="headline"><img src="images/gfxLoading-stopped.gif" width="32" height="32" title="" alt="" name="m2" />Install Progress</div>
						<div class="status"><span id="activity_1">&nbsp;</span></div>
						<div class="status"><span id="activity_2">&nbsp;</span></div>
						<div class="status"><span id="activity_3">&nbsp;</span></div>
						<div class="status"><span id="activity_4">&nbsp;</span></div>
						<div class="status"><span id="activity_5">&nbsp;</span></div>
						<div class="status"><span id="activity_6">&nbsp;</span></div>
						<div class="status"><span id="activity_7">&nbsp;</span></div>
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