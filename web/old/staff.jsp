<%@ page contentType="text/html" import="java.util.*, java.text.*, java.io.*, com.badiyan.uk.beans.*, com.badiyan.torque.*, com.badiyan.uk.exceptions.*, com.badiyan.uk.online.beans.*" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="struts-bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="struts-html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="struts-logic" %>

<jsp:useBean id="loginBean" class="com.badiyan.uk.online.beans.UKOnlineLoginBean" scope="session" />


<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
    <head>
	<link rel="stylesheet" type="text/css" href="css/mystyle2.css" />
	<title>Welcome to Valeo</title>
    </head>
    <body>

	<div id="main">
	    
<%@ include file="channels\channel-header.jsp" %>
	    <div id="bodyline">
		<div id="todaywrapper">
		    <div id="today">
			<div id="serviceswhere">
			    <div id="services-wide">
				<h2><strong>Tanya Brown, Practitioner Asst (PA)</strong></h2>
				<p><img src="images/tanya brown.jpg" style="float: left;" alt="">I have been married to Stuart for 19 years. We have two children, Mackenzie 15 and Mitchell 13.  Over a year ago I was referred to Christine from a friend.  At the time I was seeing another naturopath in Burnsville, and I was so excited to know that there was one in Chanhassen.  The first time I met her I knew God had led me there.  There was something special about her, some reason I felt I clicked with her instantly ... perhaps her passion for taking a personal interest in my health and another big one ... her passion for God.  What a bonus for me when she prayed at the end of our visit!  It almost brought me to tears!</p>
				<p>I consider myself "work in progress."   I don't know if you can relate, but I am one who wants instant results.  I've come to find out; it actually doesn't work that way.  Christine explained to me that it was just not God's plan right now for healing the way I wanted  and in the timeframe I wanted it.  I'm on a journey...</p>
				<p>Nonetheless, I'm very excited to be a part of the Valeo family.  I've known since I first stepped foot in the office that someday I wanted to be here.  My family and I have come a long way in working with Christine and also with chiropractic care.  We are healthier because of it and seeing benefits every day.</p>
				<p>I encourage you to take advantage of the free seminars that are offered here.  The staff continually share a wealth of valuable information that could be very helpful for you and your family.  I know they will touch you like they have me!  I look forward to working with you and seeing you in the clinic!</p>
			    </div>
			    <div id="services-wide">
				<h2><strong>Christine Carlson, Front Desk</strong></h2>
				<p><img src="images/chris carlson.jpg" style="float: left;" alt="">I have been happily married to Duane for 21 years and have two wonderful daughters, Samantha (age 20) and Danielle (age 17).  I came to Valeo Health & Wellness Center after 8 years of being a stay-at-home & home-schooling mom.  It was and is my passion to be there for my family even though our girls are now young women.  Working part-time as a Chiropractic Assistant has allowed me to keep this passion, but also allowed God to form a new passion in me as well.  God has given me a passion for the health & well being of those people that walk through our doors every day.  I know I don't touch our practice members physically by adjustment or massage, or counsel them on nutrition.  But I do know that when the front desk is running efficiently and in an orderly manner, everyone else in the office can do their jobs to the best of their ability.  God has given me this passion for this job and I am so thankful to be working for Him in this capacity!</p>
				<p>Colossians 3:23 - Whatever you do, work at with all your heart, as working for the Lord, not for men.</p>
			    </div>
			    <div id="services-wide">
				<h2><strong>Kerri Champion, Office Manager</strong></h2>
				<p><img src="images/kerri champion.jpg" style="float: left;" alt="">I am a wife of 20 plus years to Steve and a mom of two great "kids", Amanda who is in college and Mark who is in high school. I received a business degree from the University of Minnesota and entered the business world. When the kids were born I stayed home most of the time and then worked part time at our church in children's ministry and then at our school with children who were diagnosed with Asperger's Syndrome. Now I have the pleasure of being on staff here at Valeo. These are exciting times here at the clinic and it is fun to be part of what God is doing here. As far as my family goes, we are a much healthier bunch since meeting the practitioners here at Valeo.  I wish I had been educated on the value of chiropractic at a much younger age, but it's never too late to start getting healthy again. Fortunately our kids are understanding and experiencing the benefits already. We have also learned a lot about what the body needs from us in the way of food. I thought we were doing pretty well in that area until both Amanda and Mark had tests run here and we found out otherwise. Like I said though, we are learning and getting healthier everyday. Can't ask for more than that! Come in and see for yourself. </p>
			    </div>
			    <div id="services-wide">
				<h2><strong>Kelsey McKeever, Front Desk</strong></h2>
				<p><img src="images/kelsey mckeever.jpg" style="float: left;" alt="">Hi my name is Kelsey, and I am the youngest member of the Valeo team.  I was born and raised in Sioux Falls, SD and just recently moved to the greater Minneapolis area.</p>
				<p>Speaking of recently, I just graduated from the University of Sioux Falls with a B.A. in Criminal Justice just this last May.  I am also happily engaged, as I was proposed to in July.  So I've graduated, become engaged, and moved to the big city, my next step was to find a job.</p>
				<p>I couldn't have imagined anything better than working at Valeo, the moment I walked in the doors to meet Dr. Aaron I knew that Valeo was a place that I wanted to work.  Chris and Gail were all smiles when I walked in the door, and I felt instantly welcomed.  The experience from being at Valeo is very difficult for me to put into words.  Simply put... I love working here, and when I'm not working I still love to be here.  Come on in and experience Valeo!</p>
			    </div>
			    <div id="services-wide">
				<h2><strong>Sue Monson, Front Desk</strong></h2>
				<p><img src="images/sue monson.jpg" style="float: left;" alt="">I am Sue Monson.  I grew up in Chanhassen and now live here with my husband, Chris and our four children, Christa, Creighton, Callie and Corey.  I attended Winona State University and spent many years in "Corporate America."</p>
				<p>With four children I took a leap of faith and quit my full-time job to be home with our children.  I am now a home-schooling mom, and working part-time at Valeo.</p>
				<p>Our family has enjoyed the benefits of Chiropractic care for many years with my brother-in-law being a Chiropractor.  He has been our primary source for healthcare and wellness for many years, unfortunately his office is an hour away.  I was excited to discover Valeo and have experienced great health improvements in the short time I have been working here.  I have learned so much and love the education I get every time I walk in the door of the office.  Come in and learn how you can improve your health and feel great!</p>
			    </div>
			    <div id="services-wide">
				<h2><strong>Jill Duzan, Front Desk</strong></h2>
				<p><img src="images/jill duzan.jpg" style="float: left;" alt="">My name is Jill and I live in Chaska with my husband Mike. We have four amazing children. Joe is 27 and an architectural drafter. He is married and lives in Seattle with his wife Lindsay. Erik is 22 and works in construction. Courtney, 19, sings for the Lord and is going to intern at a church in Alabama and Travis, 15, is home educated, creative, and great with the computer.</p>
				<p>I have been a stay at home mom most of my children's growing up years, home educating them which has been such a gift and most rewarding. I enjoy the lake, boating, reading, and learning. Our family enjoys hosting parties, prayer meetings, and having our home open to all.</p>
				<p>My family and I have been going to chiropractic care for eight years after a car accident, which was a blessing in disguise. Through prayer from my chiropractor, adjustments, and God's supernatural healing I am pain free and off all medications. I was also healed from a nut allergy. I have learned a great deal about health and wellness, and looking forward to learning more.</p>
				<p>God led me to Valeo recently to help serve this team. I am very excited to use the gifts and talents God has given me to advance His kingdom and be used to help others have the abundant life He wants to give us.</p>
				
			    </div>
			</div><!-- div#serviceswhere -->
		    </div><!-- div#today -->
		</div><!-- div#todaywrapper -->
<%@ include file="channels\channel-menu-new.jsp" %>
	    </div><!-- div#bodyline -->
	    <div id="footline"><img src="images/footer.gif" alt="footer">
	    </div><!-- div#footline -->
	</div><!-- div#main -->

    </body>
</html>