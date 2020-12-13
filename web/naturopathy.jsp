<%@ page contentType="text/html" import="java.util.*, java.text.*, java.io.*, com.badiyan.uk.beans.*, com.badiyan.torque.*, com.badiyan.uk.exceptions.*, com.badiyan.uk.online.beans.*" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="struts-html" %>

<jsp:useBean id="loginBean" class="com.badiyan.uk.online.beans.UKOnlineLoginBean" scope="session" />

<%
NewsItemBean content_page = null;
try
{
	content_page = NewsItemBean.getNewsItemBean(9);
}
catch (ObjectNotFoundException x)
{
	content_page = new NewsItemBean();
}
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>Naturopathy Eden Prairie, MN</title>

<meta name="description" content="Naturopathy is a holistic health care that focuses on the whole person, rather than the problems afflicting his/her various organs and systems. Naturopathy recognizes and uses the fact that the body is a self-healing organism, working with the knowledge that if the right environment and opportunity for self-healing can be created; repair, recovery and good health will result.">
<meta name="keywords" content="Naturopathy, Valeo Wellness Center, natural health care, wellness center, chiropractic, massage, acupuncture, natural health solutions, back pain, lower back pain, headaches, alternative medicine, alternative health care, Eden Prairie, MN, Twin Cities, Edina, Chanhassen">
<meta name="Robots" content="index,follow">
<meta name="revisit-after" content="30 days">
<meta name="GOOGLEBOT" CONTENT="INDEX, FOLLOW">

<link href="styles.css" rel="stylesheet" type="text/css" />
<script language="JavaScript" type="text/javascript" src="js/jquery.js"></script>
<script language="JavaScript" type="text/javascript" src="js/cycle.js"></script>
<script language="JavaScript" type="text/javascript" src="js/behaviors.js"></script>

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
<body>
<div class="container2">
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
  <div id="floatwrapper">
<%
for (int i = 1; i <= 9; i++)
{
	String label = content_page.getNewsPropertyString("label_" + i);
	String value = content_page.getNewsPropertyString("content_" + i);
	if (label.length() > 0 && value.length() > 0)
	{
%>
    <div id="content2">
      <h1><%= label %></h1>
	  <p><%= value %></p>
    </div>
<%
	}
}
%>
    <!-- end #content2 -->
    <div class="clear"> </div>
    <!-- end .clear -->
  </div>
  <!-- end #floatwrapper -->
  <div class="clearfooter"> </div>
  <!-- end .clearfooter -->
</div>
<!-- end .container2 -->
<%@ include file="channels\channel-footer-new.jsp" %>
</body>
</html>