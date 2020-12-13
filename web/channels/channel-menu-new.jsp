		<div id="browse_block">
		    <h4><a href="valeo-home.jsp">Home</a></h4>
		    <h4><a href="why-valeo.jsp">Why Valeo?</a></h4>
		    <p>
			&nbsp;&nbsp;&nbsp;<a href="testimonials.jsp">Testimonials</a><br />
		    </p>
		    <h4>About Us</h4>
		    <p>
			&nbsp;&nbsp;&nbsp;<a href="meet-the-practitioners.jsp">Meet the Practitioners</a><br />
			&nbsp;&nbsp;&nbsp;<a href="staff.jsp">Meet the Staff</a><br />
			<!-- &nbsp;&nbsp;&nbsp;<a href="">Introduction to Valeo</a> -->
		    </p>
		    <h4>Services</h4>
		    <p>
			&nbsp;&nbsp;&nbsp;<a href="chiropractic.jsp">Chiropractic</a><br />
			&nbsp;&nbsp;&nbsp;<a href="naturopathy.jsp">Naturopathy</a><br />
			    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="scan.jsp">EDS Scanning</a><br />
			    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="nutrition.jsp">Nutrition</a><br />
			    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="detox.jsp">Detoxification</a><br />
			    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="hormone.jsp">Hormone Testing</a><br />
			    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="hair.jsp">Hair Analysis</a><br />
			    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="nrt.jsp">Neurological Muscle Testing</a><br />
			    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="essential-oils-info.jsp">Essential Oils</a><br />
			    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="homeopathy.jsp">Homeopathy</a><br />
			    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="herb.jsp">Herbology</a><br />
			    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="q2.jsp">Q2 Energy Spa</a><br />
			&nbsp;&nbsp;&nbsp;<a href="massage.jsp">Massage</a><br />
			&nbsp;&nbsp;&nbsp;<a href="cranio.jsp">CranioSacral Therapy</a><br />
			&nbsp;&nbsp;&nbsp;<a href="acupuncture.jsp">Acupuncture</a><br />
			&nbsp;&nbsp;&nbsp;<a href="electro-acuscope-therapy.jsp">Electro-Acuscope Therapy</a><br />
			&nbsp;&nbsp;&nbsp;<a href="lifestyle.jsp">Lifestyle Coaching</a><br />
			&nbsp;&nbsp;&nbsp;<a href="defense.jsp">Self Defense</a><br />
		    <!--
		    <h4><a href="products.jsp">Products</a></h4>
		    <p>
			&nbsp;&nbsp;&nbsp;<a href="whole-food.jsp">Whole Food Nutrition</a><br />
			&nbsp;&nbsp;&nbsp;<a href="homeopathics.jsp">Homeopathics</a><br />
			&nbsp;&nbsp;&nbsp;<a href="essential-oils.jsp">Essential Oils</a><br />
			&nbsp;&nbsp;&nbsp;<a href="skin.jsp">Skin Care</a><br />
			&nbsp;&nbsp;&nbsp;<a href="valeo.jsp">Valeo</a><br />
			&nbsp;&nbsp;&nbsp;<a href="chiro.jsp">Chiropractic</a>
		    </p>
		    -->
		    <h4><a href="seminars.jsp">Seminars & Events</a></h4>
		    <h4><a href="contact-us.jsp">Contact Us</a></h4>
		    <h4><a href="links.jsp">Links</a></h4>
		    <h4><a href="newsletter.jsp">Newsletters</a></h4>
		    <h4><a href="forms.jsp">Forms</a></h4>
		    <h4><a href="login.jsp">Log In</a></h4>
<%
try
{
    if (loginBean.getPerson().hasRole(RoleBean.COMPANY_ADMINISTRATOR_ROLE_NAME) ||
	loginBean.getPerson().hasRole(RoleBean.SYSTEM_ADMINISTRATOR_ROLE_NAME))
    {
%>
		    <h4><a href="admin/index.jsp">Administration</a></h4>
		    <h4><a href="admin/schedule.jsp">Daysheet</a></h4>
<%
    }
}
catch (Exception x)
{
    x.printStackTrace();
}
%>
		</div>