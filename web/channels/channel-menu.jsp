<%
boolean admin_page = (request.getRequestURI().indexOf("/admin/") > -1);

boolean index_page = (request.getRequestURI().indexOf("valeo-home.jsp") > -1);
boolean about_us_page = (request.getRequestURI().indexOf("about-us.jsp") > -1);
boolean services_page = (request.getRequestURI().indexOf("services.jsp") > -1);
boolean products_page = (request.getRequestURI().indexOf("products.jsp") > -1);
boolean contact_us_page = (request.getRequestURI().indexOf("contact-us.jsp") > -1);
boolean links_page = (request.getRequestURI().indexOf("links.jsp") > -1);

boolean newsletter_page = (request.getRequestURI().indexOf("newsletter.jsp") > -1);
boolean forms_page = (request.getRequestURI().indexOf("forms.jsp") > -1);

boolean staff_page = (request.getRequestURI().indexOf("staff.jsp") > -1);

boolean is_root_page = index_page || about_us_page || services_page || products_page || contact_us_page || links_page;
%>

	<struts-html:form action="/login">
	<div class="login">
<%
if (loginBean.isLoggedIn())
{
    UKOnlinePersonBean menu_person = (UKOnlinePersonBean)loginBean.getPerson();
%>
	    <span class="menu"><strong>Welcome <%= menu_person.getFullName() %></strong></span>
<%
    if (menu_person.hasRole(RoleBean.SYSTEM_ADMINISTRATOR_ROLE_NAME))
    {
%>
	    &nbsp;<a href="<%= admin_page ? "..\\index.jsp" : "admin\\index.jsp" %>"><span class="menu"><%= admin_page ? "home" : "admin" %></span></a>
<%
    }
%>
	    &nbsp;<!-- <a href="logout.jsp"><span class="menu">logout</span></a> -->
<%
}
else
{
%>
	    <span class="menu">Email</span>&nbsp;<input name="username" onfocus="select();" maxlength="150" class="inputbox" style="width: 146px;" />
	    <span class="menu">Password</span>&nbsp;<input name="password" onfocus="select();" maxlength="50" class="inputbox" style="width: 106px;" type="password" />
	    <struts-html:submit property="login" value="login" styleClass="menu" />
	    &nbsp;<!-- <a href="register.jsp"><span class="menu">register?</span></a> -->
	    &nbsp;<!-- <a href=""><span class="menu">forgot password?</span></a> -->
<%
}
%>
	</div>
	</struts-html:form>

<%
if (admin_page)
{
%>
<%
}
else
{
%>
<div id="menux">
	<div id="menux-1">
		<a href="meet-the-practitioners.jsp" onmouseover="hideMenux('4');hideMenux('5');" id="practitioners"><i>practitioners</i></a>
		<a href="staff.jsp" onmouseover="hideMenux('4');hideMenux('5');" id="staff"><i>staff</i></a>
		<a href="intro.jsp" onmouseover="hideMenux('4');hideMenux('5');" id="intro"><i>intro</i></a>
	</div>
	<div id="menux-2">
		<a href="chiropractic.jsp" onmouseover="hideMenux('4');hideMenux('5');" id="chiropractic"><i>chiropractic</i></a>
		<a href="naturopathy.jsp" onmouseover="showMenux('4');hideMenux('5');" id="naturopathy"><i>naturopathy</i></a>
		<a href="massage.jsp" onmouseover="hideMenux('4');hideMenux('5');" id="massage"><i>massage</i></a>
		<a href="cranio.jsp" onmouseover="hideMenux('4');hideMenux('5');" id="cranio"><i>cranio</i></a>
		<a href="lifestyle.jsp" onmouseover="hideMenux('4');hideMenux('5');" id="lifestyle"><i>lifestyle</i></a>
		<a href="defense.jsp" onmouseover="hideMenux('4');showMenux('5');" id="defense"><i>defense</i></a>
		<a href="seminars.jsp" onmouseover="hideMenux('4');hideMenux('5');" id="seminars"><i>seminars</i></a>
	</div>
	<div id="menux-3">
		<a href="whole-food.jsp" onmouseover="hideMenux('4');hideMenux('5');" id="food"><i>food</i></a>
		<a href="homeopathics.jsp" onmouseover="hideMenux('4');hideMenux('5');" id="homeopathics"><i>homeopathics</i></a>
		<a href="essential-oils.jsp" onmouseover="hideMenux('4');hideMenux('5');" id="oils"><i>oils</i></a>
		<a href="skin.jsp" onmouseover="hideMenux('4');hideMenux('5');" id="skin"><i>skin</i></a>
		<a href="valeo.jsp" onmouseover="hideMenux('4');hideMenux('5');" id="valeo"><i>valeo</i></a>
		<a href="chiro.jsp" onmouseover="hideMenux('4');hideMenux('5');"  id="chiro"><i>chiro</i></a>
	</div>
	<div id="menux-4">
		<a href="scan.jsp" id="scan"><i>scan</i></a>
		<a href="nutrition.jsp" id="nutrition"><i>nutrition</i></a>
		<a href="detox.jsp" id="detox"><i>detox</i></a>
		<a href="hormone.jsp" id="hormone"><i>hormone</i></a>
		<a href="hair.jsp" id="hair"><i>hair</i></a>
		<a href="nrt.jsp" id="nrt"><i>nrt</i></a>
		<a href="homeopathy.jsp" id="homeopathy"><i>homeopathy</i></a>
		<a href="herb.jsp" id="herb"><i>herb</i></a>
		<a href="q2.jsp" id="q2"><i>q2</i></a>
	</div>
	<div id="menux-5">
		<a href="schedule.jsp" id="schedule"><i>schedule</i></a>
		<a href="options.jsp" id="options"><i>options</i></a>
		<a href="stats.jsp" id="stats"><i>stats</i></a>
	</div>
</div>

	<div class="valeo-logo">
	  <img src="images/valeo-logo.png" alt="Valeo" />
	</div>

	<div class="vert-bar-grey">
	  <img src="images/vert-bar-grey.png" alt="" />
	</div>

	<div class="vert-bar-grey-2">
	  <img src="images/vert-bar-grey.png" width="3" height="394" alt="" />
	</div>

	<div class="exp-life-Gods-way">
	  <img src="images/exp-life-Gods-way.png" alt="Experience Life God's Way!" />
	</div>

<!--
	<div class="search-site">
	  <img src="images/search-site.png" alt="search site" />
	</div>

	<div class="search-button">
	  <img src="images/search-site-button.png" alt="" />
	</div>
-->

	<div class="newsletter-button">
	  <a href="newsletter.jsp" onmouseover="SwapOutX(7);" onmouseout="SwapBackX(7);" ><img src="images/newsletter-button.gif" name="m7" alt="Newsletter" border="0" /></a>
	</div>

	<div class="forms-button">
	  <a href="forms.jsp" onmouseover="SwapOutX(8);" onmouseout="SwapBackX(8);" ><img src="images/Forms_Up.gif" name="m8" alt="Newsletter" border="0" /></a>
	</div>
<%
if (is_root_page)
{
%>
	<div class="beach-woman">
	  <img src="images/beach-woman.png" alt="" />
	</div>
	
	<div class="menu-sep-x">
		<img src="images/column-purple.gif" alt="" />
	</div>
	
	<div class="footer">
		<img src="images/footer.gif" alt="0" />
	</div>
<%
    if (index_page)
    {
%>
	<div class="welcome-home">
		<img src="images/welcome-to-valeo-home.png" alt="0" />
	</div>
<%
    }
    else if (about_us_page)
    {
%>
	<div class="welcome-home">
		<img src="images/welcome-to-valeo-about-us.png" alt="0" />
	</div>
<%
    }
    else if (services_page)
    {
%>
	<div class="welcome-home">
		<img src="images/welcome-to-valeo-services.png" alt="0" />
	</div>
<%
    }
    else if (products_page)
    {
%>
	<div class="welcome-home">
		<img src="images/welcome-to-valeo-products.png" alt="0" />
	</div>
<%
    }
    else if (contact_us_page)
    {
%>
	<div class="welcome-home">
		<img src="images/welcome-to-valeo-contact.png" alt="0" />
	</div>
<%
    }
    else if (links_page)
    {
%>
	<div class="welcome-home">
		<img src="images/welcome-to-valeo-links.png" alt="0" />
	</div>
<%
    }
}
else if (newsletter_page || forms_page)
{
%>
	<div class="footer">
		<img src="images/footer.gif" alt="0" />
	</div>
<%
}
%>
	<div class="menu-homepage">
	  <a href="index.jsp" onmouseover="SwapOutX(1);hideMenux('1');hideMenux('2');hideMenux('3');hideMenux('4');hideMenux('5');" onmouseout="SwapBackX(1);" ><img src="images/homepage-normal.gif" name="m1" alt="Homepage" border="0" /></a>
	</div>

	<div class="about-us">
		<a href="about-us.jsp" onmouseover="SwapOutX(2);showMenux('1');hideMenux('2');hideMenux('3');hideMenux('4');hideMenux('5');" onmouseout="SwapBackX(2);" ><img src="images/about-us-normal.gif" name="m2" alt="About Us" border="0" /></a>
	</div>

	<div class="services-x">
		  <a href="services.jsp" onmouseover="SwapOutX(3);hideMenux('1');showMenux('2');hideMenux('3');hideMenux('4');hideMenux('5');" onmouseout="SwapBackX(3);" ><img src="images/services-normal.gif" name="m3" alt="Services" border="0" /></a>
	</div>

	<div class="products-x">
		<a href="products.jsp" onmouseover="SwapOutX(4);hideMenux('1');hideMenux('2');showMenux('3');hideMenux('4');hideMenux('5');" onmouseout="SwapBackX(4);" ><img src="images/products-normal.gif" name="m4" alt="Products" border="0" /></a>
	</div>

	<div class="contact-us">
		<a href="contact-us.jsp" onmouseover="SwapOutX(5);hideMenux('1');hideMenux('2');hideMenux('3');hideMenux('4');hideMenux('5');" onmouseout="SwapBackX(5);" ><img src="images/contact-us-normal.gif" name="m5" alt="Contact Us" border="0" /></a>
	</div>

	<div class="links">
		<a href="links.jsp" onmouseover="SwapOutX(6);hideMenux('1');hideMenux('2');hideMenux('3');hideMenux('4');hideMenux('5');" onmouseout="SwapBackX(6);" ><img src="images/links-normal.gif" name="m6" alt="Links" border="0" /></a>
	</div>



	<div class="latest-news">
		<img src="images/latest-news.png" alt="Latest News" />
	</div>

	<div class="latest-news-back">
		<img src="images/latest-news-back.gif" alt="" />
	</div>

<%
}
%>