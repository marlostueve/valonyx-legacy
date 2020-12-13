<%
String header_image = "beach-woman.png";
if (request.getRequestURI().indexOf("index.jsp") > -1)
    header_image = "beach-woman-home.png";
else if (request.getRequestURI().indexOf("about-us.jsp") > -1)
    header_image = "beach-woman-about-us.png";
else if (request.getRequestURI().indexOf("services.jsp") > -1)
    header_image = "beach-woman-services.png";
else if (request.getRequestURI().indexOf("products.jsp") > -1)
    header_image = "beach-woman-products.png";
else if (request.getRequestURI().indexOf("contact-us.jsp") > -1)
    header_image = "beach-woman-contact.png";
else if (request.getRequestURI().indexOf("links.jsp") > -1)
    header_image = "beach-woman-links.png";
%>
	    <div id="headline">
		<div id="highlightedfeature" style="background-image: url(images/<%= header_image %>);">

		    <div class="welcome-home-text">
			    <p><strong>Upcoming Free Seminars @ Valeo</strong>:<br />
			    <a href="seminars.jsp">May 12, 2009 7:00 - 8:00 p.m.  Female Hormones Seminar</a> <br /><a href="seminars.jsp">May 19, 2009 7:00 - 8:00 p.m.   Vaccination Seminar</a> <br /><a href="seminars.jsp">&nbsp; </a> </p>
		    </div>

		</div>
		<div id="explore">
		    <!-- <h2>Experience Life God's Way!</h2> -->

		    <img src="images/whitespace3.gif" alt="">
		    <img src="images/valeo-logo.png" alt="Valeo">
		    <img src="images/exp-life-Gods-way.png" alt="Experience Life God's Way!">

		</div><!-- div#explore -->
	    </div><!-- div#headline -->