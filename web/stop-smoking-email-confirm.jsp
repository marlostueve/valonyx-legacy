<%@ page contentType="text/html" import="java.util.*, java.text.*, java.io.*, com.badiyan.uk.beans.*, com.badiyan.torque.*, com.badiyan.uk.exceptions.*, com.badiyan.uk.online.beans.*" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="struts-html" %>

<jsp:useBean id="loginBean" class="com.badiyan.uk.online.beans.UKOnlineLoginBean" scope="session" />

<%
NewsItemBean content_index_page = null;
try
{
	content_index_page = NewsItemBean.getNewsItemBean(1);
}
catch (ObjectNotFoundException x)
{
	content_index_page = new NewsItemBean();
	content_index_page.setName("index");
}
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>Stop Smoking Program Eden Prairie - Chanhassen</title>

<meta name="description" content="Valeo Wellness Center in Eden Prairie offers Athlete's Power Tonic, a herbal formula provides rapid recovery from strenuous physical stress. Our Eden Prairie, MN office is open Monday - Friday, starting at 7 a.m.">
<meta name="keywords" content="Stop Smoking, chiropractor, Valeo Wellness Center, natural health care, wellness center, chiropractic, massage, naturopathy,natural health solutions, back pain, lower back pain, headaches, alternative medicine, alternative health care, Eden Prairie, MN, Twin Cities, Edina, Chanhassen">

<link href="styles.css" rel="stylesheet" type="text/css" />
<script language="JavaScript" type="text/javascript" src="js/jquery.js"></script>
<script language="JavaScript" type="text/javascript" src="js/cycle.js"></script>
<script language="JavaScript" type="text/javascript" src="js/behaviors.js"></script>
<script language="JavaScript" type="text/javascript" src="js/google-analytics.js"></script>


 
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
 
<link type="text/css" rel="stylesheet" href="http://yui.yahooapis.com/3.3.0/build/cssfonts/fonts-min.css" /> 
<script type="text/javascript" src="http://yui.yahooapis.com/3.3.0/build/yui/yui-min.js"></script> 
 
 
<!--begin custom header content for this example--> 
<style id="yui3-style-overrides"> 
 
#main #example-canvas .yui3-tabview .yui3-tab-selected a {
	color:white;
}
 
</style> 
<!--end custom header content for this example--> 
 

<script type="text/javascript">

  var _gaq = _gaq || [];
  _gaq.push(['_setAccount', 'UA-7869165-5']);
  _gaq.push(['_trackPageview']);

  (function() {
    var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;
    ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
    var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);
  })();

</script>

</head>
<body class="yui3-skin-sam  yui-skin-sam">
<div class="container">
  <div id="header">
    <div id="logo"></div>
    <!-- end #logo -->
    <div class="imagecycle">
      <div id="imageslider">
        <div><img src="img/Image_Cycle/Image_1.gif" alt="Valeo Health &amp; Wellness Image 1" /></div>
        <div><img src="img/Image_Cycle/Image_2.gif" alt="Valeo Health &amp; Wellness Image 2" /></div>
        <div><img src="img/Image_Cycle/Image_3.gif" alt="Valeo Health &amp; Wellness Image 3" /></div>
      </div>
      <!-- end #imageslider -->
    </div>
    <!-- end .imagecycle -->
    <div class="clear"></div>
    <!-- end .clear -->
    <div id="tagline"></div>
    <!-- end #tagline -->
  </div>
  <!-- end #header -->
<%@ include file="channels\channel-nav.jsp" %>
  
    <div id="content">
     <h1>Thanks!</h1><h2>Your email has been sent.</h2><br /><br /><a href="stop-smoking.jsp">Back to previous page.</a>
		<div ><img src="images/stop-smoking.png" alt="Stop Smoking" style="float: left; padding: 10px;" />
      </div>
      <!-- end #video --><br />
<%

%>
    </div>
    <!-- end #content -->
    <!-- end #sidebar -->
    <div class="clear"> </div>
    <!-- end .clear -->
	
 


	<br /><br />
	
	
	
    <!-- end .boxesbottom -->
  
  <!-- end #floatwrapper -->
  <div class="clearfooter"> </div>
  <!-- end .clearfooter -->
</div>
<!-- end .container -->
<%@ include file="channels\channel-footer-new.jsp" %>
</body>
</html> 