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
<title>Welcome to Valeo</title>
<link href="styles.css" rel="stylesheet" type="text/css" />
<script language="JavaScript" type="text/javascript" src="js/jquery.js"></script>
<script language="JavaScript" type="text/javascript" src="js/cycle.js"></script>
<script language="JavaScript" type="text/javascript" src="js/behaviors.js"></script>
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
      <p><%= content_index_page.getNewsPropertyString("welcome") %></p>
      <div id="video">
        <%= content_index_page.getNewsPropertyString("channel_url") %>
      </div>
      <!-- end #video -->
    </div>
    <!-- end #content -->
    <div id="sidebar">
    <h1>Newsletter Signup</h1>
      <p>Please enter your email address below...</p>
      <p align="center">
        <input name="newsletter" type="text" class="newsletterinput" />
      </p>
      <p align="center">
        <input type="image" src="img/buttonbg_newsletter.gif" name="submit" class="newsletterbutton" value="Send" />
      </p>
      <h1>Latest News</h1>
      <h2><%= content_index_page.getNewsPropertyString("news_article_title") %></h2>
      <p><%= content_index_page.getNewsPropertyString("news_article") %></p>
    </div>
    <!-- end #sidebar -->
    <div class="clear"> </div>
    <!-- end .clear -->
    <div class="boxesbottom">
    <div class="boxoutside">
    <div class="boxinside">
    <h2><%= content_index_page.getNewsPropertyString("small_box_header_1") %></h2>
    <ul>
    <li><%= content_index_page.getNewsPropertyString("small_box_header_1_item_1") %></li>
    <li><%= content_index_page.getNewsPropertyString("small_box_header_1_item_2") %></li>
    <li><%= content_index_page.getNewsPropertyString("small_box_header_1_item_3") %></li>
    <li><%= content_index_page.getNewsPropertyString("small_box_header_1_item_4") %></li>
    <li><%= content_index_page.getNewsPropertyString("small_box_header_1_item_5") %></li>
    <li><%= content_index_page.getNewsPropertyString("small_box_header_1_item_6") %></li>
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
    <li><%= content_index_page.getNewsPropertyString("small_box_header_2_item_6") %></li>
    </ul>
    </div>
    <!-- end .boxinside -->
    </div>
    <!-- end .boxoutside -->
    <div class="boxoutside">
    <div class="boxinside">
    <h2><%= content_index_page.getNewsPropertyString("small_box_header_3") %></h2>
    <ul>
    <li><%= content_index_page.getNewsPropertyString("small_box_header_3_item_1") %></li>
    <li><%= content_index_page.getNewsPropertyString("small_box_header_3_item_2") %></li>
    <li><%= content_index_page.getNewsPropertyString("small_box_header_3_item_3") %></li>
    <li><%= content_index_page.getNewsPropertyString("small_box_header_3_item_4") %></li>
    <li><%= content_index_page.getNewsPropertyString("small_box_header_3_item_5") %></li>
    <li><%= content_index_page.getNewsPropertyString("small_box_header_3_item_6") %></li>
    </ul>
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