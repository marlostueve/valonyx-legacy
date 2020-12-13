<%@ page contentType="text/html" import="java.util.*, java.text.*, java.io.*, com.badiyan.uk.beans.*, com.badiyan.torque.*, com.badiyan.uk.exceptions.*, com.badiyan.uk.online.beans.*" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="struts-html" %>

<jsp:useBean id="loginBean" class="com.badiyan.uk.online.beans.UKOnlineLoginBean" scope="session" />

<%
NewsItemBean content_page = null;
try
{
	content_page = NewsItemBean.getNewsItemBean(2);
}
catch (ObjectNotFoundException x)
{
	content_page = new NewsItemBean();
	content_page.setName("practitioners");
}
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>Valeo Wellness Center : Meet the Practitioners</title>

<meta name="description" content="Meet the Valeo Wellness Center practitioners - Dr. Aaron Morland, Doctor of Chiropractic; Dr. Rob Lindsey, Doctor of Chiropractic; Becca Griffth, Health Care Practitioner; Leah Shirley, Certified Massage Therapist; Christine Stueve, Naturopath; Miranda McDonald, Licenses Acupuncturist; Jonnie Goodmanson, Certified Massage Therapist.">
<meta name="keywords" content="Valeo Wellness Center, natural health care, wellness center, chiropractic, massage, naturopathy, acupuncture, Dr. Aaron Morland, Doctor of Chiropractic, Dr. Rob Lindsey, Becca Griffth, Health Care Practitioner, Leah Shirley, Certified Massage Therapist, Christine Stueve, Naturopath, Miranda McDonald, Licenses Acupuncturist, Jonnie Goodmanson">
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
	String practitioner_name = content_page.getNewsPropertyString("practitioner_name_" + i);
	String practitioner_value = content_page.getNewsPropertyString("practitioner_content_" + i);
	if (practitioner_name.length() > 0 && practitioner_value.length() > 25)
	{

%>
    <div id="content2">
      <h1><%= practitioner_name %></h1>
	  <p><%= practitioner_value %></p>
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
