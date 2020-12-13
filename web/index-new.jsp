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
<title>Welcome to Valeo : Eden Prairie Chiropractic & Natural Health</title>

<meta name="description" content="Welcome to Valeo Wellness Center, a natural health and wellness center and alternative health care office located in Eden Prairie, MN that specializes in chiropractic, massage, naturopathy, acupuncture, natural health care practices.">
<meta name="keywords" content="Valeo Wellness Center, natural health care, wellness center, chiropractic, massage, naturopathy, acupuncture, natural health solutions, back pain, lower back pain, headaches, alternative medicine, alternative health care, Eden Prairie, MN, Twin Cities, Edina, Chanhassen">
<meta name="Robots" content="index,follow">
<meta name="revisit-after" content="30 days">
<meta name="GOOGLEBOT" CONTENT="INDEX, FOLLOW">

<link href="styles.css" rel="stylesheet" type="text/css" />
<script language="JavaScript" type="text/javascript" src="js/jquery.js"></script>
<script language="JavaScript" type="text/javascript" src="js/cycle.js"></script>
<script language="JavaScript" type="text/javascript" src="js/behaviors.js"></script>
<script language="JavaScript" type="text/javascript" src="js/google-analytics.js"></script>

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
  <div id="floatwrapper">
    <div id="content">
     <h1>Welcome To Valeo</h1>
      <div id="video" style="width: 480px; height: 360px;">
        <%= content_index_page.getNewsPropertyString("channel_url") %>
      </div>
	  <br />
      <!-- end #video -->
      <!-- <p><%= content_index_page.getNewsPropertyString("welcome") %></p> -->
<%
NewsItemBean content_page = null;
try
{
	content_page = NewsItemBean.getNewsItemBean(4);
}
catch (ObjectNotFoundException x)
{
	content_page = new NewsItemBean();
	content_page.setName("testimonials");
}
Random r = new Random();
boolean done = false;
for (int i = 1; i <= 17 && !done; i++)
{
	int r_i = r.nextInt() % 16 + 1;
	String label = content_page.getNewsPropertyString("testimonial_label_" + r_i);
	String value = content_page.getNewsPropertyString("testimonial_content_" + r_i);
	if (label.length() > 0 && value.length() > 0)
	{
%>
      <p><%= value %></p>
      <p align="right"><%= label %></p>
<%
		done = true;
	}
}
%>
    </div>
    <!-- end #content -->
    <div id="sidebar">
<!--
    <h1>Newsletter Signup</h1>
      <p>Please enter your email address below...</p>
      <p align="center">
        <input name="newsletter" type="text" class="newsletterinput" />
      </p>
      <p align="center">
        <input type="image" src="img/buttonbg_newsletter.gif" name="submit" class="newsletterbutton" value="Send" />
      </p>
-->
      <h1>Wellness Programs</h1>
	  <h2>Stop Smoking Naturally!</h2>
	  
	  <a href="stop-smoking.jsp">
	  <div align="center">
	  <img src="images/stop-smoking.png" alt="Stop Smoking" />
	  <p>New Laser Therapy Will Help You <span style="color: red;" >Just Stop!</span>  Just 3 treatments over 7 days.</p>
	  </a>
	  </div>
	  
	  <h2>Athlete's Power Tonic - &nbsp;<span style="color: red;" >COMING SOON!</span></h2>
	  
	  <a href="#">
	  <div align="center">
	  <img src="images/athletic-recovery.png" alt="Athletic Recovery" />
	  <p>The safe alternative to synthetic anabolic steroids for both men and women!</p>
	  </a>
	  </div>
	  
	  <h2>Neuro-Metabolic Therapy (NMT)</h2>
	  
	  <a href="nmt.jsp">
	  <div align="center">
	  <img src="images/allergy-free.png" alt="Allergy Free" />
	  <p>Need Content</p>
	  </a>
	  </div>
	  
	  <!--
      <h2><%= content_index_page.getNewsPropertyString("news_article_title") %></h2>
      <p><%= content_index_page.getNewsPropertyString("news_article") %></p>
	  -->
	  
    </div>
    <!-- end #sidebar -->
    <div class="clear"> </div>
    <!-- end .clear -->
    <div class="boxesbottom">
    <div class="boxoutside">
    <div class="boxinside">
    <h2>Testimonials</h2>
    <ul>
    <li><a href="testimonials.jsp">I recently was able to have the BAX treatment at Valeo to help quit smoking...</a></li>
    <li><a href="testimonials.jsp">When I first came to Valeo, I had all but given up on regaining my vitality...</a></li>
    <li><a href="testimonials.jsp">I started coming to Valeo because my newborn baby would not stop crying...</a></li>
    <li><a href="testimonials.jsp">I just wanted to say a great big thanks to Dr. Rob for allowing me to go thru the Bax treatment at Valeo...</a></li>
    </ul>
    </div>
    <!-- end .boxinside -->
    </div>
    <!-- end .boxoutside -->
    <div class="boxoutside">
    <div class="boxinside">
    <h2><%= content_index_page.getNewsPropertyString("small_box_header_2") %></h2>
    <ul>
    <li><%= content_index_page.getNewsPropertyString("small_box_header_2_item_1") %></li>
    <li><%= content_index_page.getNewsPropertyString("small_box_header_2_item_2") %></li>
    <li><%= content_index_page.getNewsPropertyString("small_box_header_2_item_3") %></li>
    <li><%= content_index_page.getNewsPropertyString("small_box_header_2_item_4") %></li>
    <li><%= content_index_page.getNewsPropertyString("small_box_header_2_item_5") %></li>
    <!-- <li><%= content_index_page.getNewsPropertyString("small_box_header_2_item_6") %></li> -->
    </ul>
    </div>
    <!-- end .boxinside -->
    </div>
    <!-- end .boxoutside -->
    <div class="boxoutside">
    <div class="boxinside">
    <h2><%= content_index_page.getNewsPropertyString("small_box_header_3") %></h2>
	<%= content_index_page.getNewsPropertyString("small_box_header_3_item_1") %>
	<%= content_index_page.getNewsPropertyString("small_box_header_3_item_2") %>
	<%= content_index_page.getNewsPropertyString("small_box_header_3_item_3") %>
	<%= content_index_page.getNewsPropertyString("small_box_header_3_item_4") %>
	<%= content_index_page.getNewsPropertyString("small_box_header_3_item_5") %>
	<%= content_index_page.getNewsPropertyString("small_box_header_3_item_6") %>
	

    <!-- BEGIN: Constant Contact Stylish Email Newsletter Form -->
	
	<br /><br />
    
    <form name="ccoptin" action="http://visitor.constantcontact.com/d.jsp" target="_blank" method="post" style="margin-bottom: 3em;">
	<h2>Newsletter Signup</h2>
	<h3><span style="font-size: smaller;" >Please enter your email address:</span></h3><br />
      <div align="center">
		  
      
	<input type="text" name="ea" size="20" value="" class="newsletterinput" style="font-family:Verdana,Geneva,Arial,Helvetica,sans-serif; font-size:14px; border:1px solid #999999;" />
	<input type="submit" name="go" value="GO" class="submit"  style="font-family:Verdana,Arial,Helvetica,sans-serif; font-size:10px;" />
	<input type="hidden" name="m" value="1102288667365" />
	<input type="hidden" name="p" value="oi" />
      </div>
    </form>
    <!-- END: Constant Contact Stylish Email Newsletter Form -->
	
    </div>
    <!-- end .boxinside -->
    </div>
    <!-- end .boxoutside -->
    </div>
    <!-- end .boxesbottom -->
  </div>
  <!-- end #floatwrapper -->
  <div class="clearfooter"> </div>
  <!-- end .clearfooter -->
</div>
<!-- end .container -->
<%@ include file="channels\channel-footer-new.jsp" %>
</body>
</html> 