<%@ page language="java" pageEncoding="utf-8" contentType="text/html;charset=utf-8" import="java.util.*, com.badiyan.uk.beans.*, com.badiyan.uk.exceptions.*, com.badiyan.uk.online.beans.*" %>

<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="struts-bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="struts-html" %>

<jsp:useBean id="loginBean" class="com.badiyan.uk.online.beans.UKOnlineLoginBean" scope="session" />
<jsp:useBean id="userCompany" class="com.badiyan.uk.online.beans.UKOnlineCompanyBean" scope="session" />



<%

	String first = "";
	String last = "";
	String street = "";
	String city = "";
	String state = "";
	String zip = "";
	String email = "";
	Integer qty = -1;
	String ccnumber = "";
	String name_on_card = "";
	Integer exp_month = -1;
	Integer exp_year = -1;
	String ccv = "";
	String select = "";
	
	if (session.getAttribute("first") != null) {
		first = (String)session.getAttribute("first");
	}
	
	if (session.getAttribute("last") != null) {
		last = (String)session.getAttribute("last");
	}
	
	if (session.getAttribute("street") != null) {
		street = (String)session.getAttribute("street");
	}
	
	if (session.getAttribute("city") != null) {
		city = (String)session.getAttribute("city");
	}
	
	if (session.getAttribute("state") != null) {
		state = (String)session.getAttribute("state");
	}
	
	if (session.getAttribute("zip") != null) {
		zip = (String)session.getAttribute("zip");
	}
	
	if (session.getAttribute("email") != null) {
		email = (String)session.getAttribute("email");
	}
	
	if (session.getAttribute("qty") != null) {
		qty = (Integer)session.getAttribute("qty");
	}
	
	if (session.getAttribute("ccnumber") != null) {
		ccnumber = (String)session.getAttribute("ccnumber");
	}
	
	if (session.getAttribute("name_on_card") != null) {
		name_on_card = (String)session.getAttribute("name_on_card");
	}
	
	if (session.getAttribute("exp_month") != null) {
		exp_month = (Integer)session.getAttribute("exp_month");
	}
	
	if (session.getAttribute("exp_year") != null) {
		exp_year = (Integer)session.getAttribute("exp_year");
	}
	
	if (session.getAttribute("ccv") != null) {
		ccv = (String)session.getAttribute("ccv");
	}
	
	if (session.getAttribute("select") != null) {
		select = (String)session.getAttribute("select");
	}
%>


<%
userCompany = (UKOnlineCompanyBean)UKOnlineCompanyBean.getCompany(5);
session.setAttribute("userCompany", userCompany);

int selected_id = 1; // book = 1 // DVD = 2 // book + DVD = 3 // bracelet = 4
if (request.getParameter("id") != null) {
	selected_id = Integer.parseInt(request.getParameter("id"));
	if (selected_id > 4) {
		selected_id = 1;
	}
	
	/*
	selectedItemId = request.getParameter("id");
	session.setAttribute("selectedItemId", selectedItemId);
	*/
} else if (!select.isEmpty()) {
	selected_id = Integer.parseInt(select);
}




%>

<!DOCTYPE html>
<html lang="en">
  <head>
<%@ include file="channels\channel-head.jsp" %>

    <style type="text/css">
      body {
        background-color: #f5f5f5;
      }

      .form-signin {
        max-width: 400px;
        padding: 19px 29px 29px;
        margin: 0 auto 20px;
        background-color: #fff;
        border: 1px solid #e5e5e5;
        -webkit-border-radius: 5px;
           -moz-border-radius: 5px;
                border-radius: 5px;
        -webkit-box-shadow: 0 1px 2px rgba(0,0,0,.05);
           -moz-box-shadow: 0 1px 2px rgba(0,0,0,.05);
                box-shadow: 0 1px 2px rgba(0,0,0,.05);
      }
      .form-signin .form-signin-heading,
      .form-signin .checkbox {
        margin-bottom: 10px;
      }
      .form-signin input[type="text"],
      .form-signin input[type="password"] {
        font-size: 16px;
        height: auto;
        margin-bottom: 15px;
        padding: 7px 9px;
      }

    </style>
	
<script type="text/javascript">

<%
if (selected_id == 1 || selected_id == 2) {
%>
  function doPrice(qty) {
	  if (qty == '1') {
		document.getElementById('tax_id').value = '$1.09';
		document.getElementById('total_id').value = '$16.08';
	  } else if (qty == '2') {
		document.getElementById('tax_id').value = '$2.18';
		document.getElementById('total_id').value = '$32.16';
	  } else if (qty == '3') {
		document.getElementById('tax_id').value = '$3.27';
		document.getElementById('total_id').value = '$48.24';
	  } else if (qty == '4') {
		document.getElementById('tax_id').value = '$4.36';
		document.getElementById('total_id').value = '$64.32';
	  } else if (qty == '5') {
		document.getElementById('tax_id').value = '$5.45';
		document.getElementById('total_id').value = '$80.40';
	  } else if (qty == '6') {
		document.getElementById('tax_id').value = '$6.54';
		document.getElementById('total_id').value = '$96.48';
	  }

  }
<%
} else if (selected_id == 3) {
%>
  function doPrice(qty) {// 7.275%??
	  if (qty == '1') {
		document.getElementById('tax_id').value = '$1.82';
		document.getElementById('total_id').value = '$26.81';
	  } else if (qty == '2') {
		document.getElementById('tax_id').value = '$3.64';
		document.getElementById('total_id').value = '$53.62';
	  } else if (qty == '3') {
		document.getElementById('tax_id').value = '$5.45';
		document.getElementById('total_id').value = '$80.42';
	  } else if (qty == '4') {
		document.getElementById('tax_id').value = '$7.27';
		document.getElementById('total_id').value = '$107.23';
	  } else if (qty == '5') {
		document.getElementById('tax_id').value = '$9.09';
		document.getElementById('total_id').value = '$134.04';
	  } else if (qty == '6') {
		document.getElementById('tax_id').value = '$10.91';
		document.getElementById('total_id').value = '$160.85';
	  }

  }
<%
} else if (selected_id == 4) {
%>
  function doPrice(qty) {// 7.275%??
	  if (qty == '1') {
		document.getElementById('tax_id').value = '$0.07';
		document.getElementById('total_id').value = '$1.07';
	  } else if (qty == '2') {
		document.getElementById('tax_id').value = '$0.15';
		document.getElementById('total_id').value = '$2.15';
	  } else if (qty == '3') {
		document.getElementById('tax_id').value = '$0.22';
		document.getElementById('total_id').value = '$3.22';
	  } else if (qty == '4') {
		document.getElementById('tax_id').value = '$0.29';
		document.getElementById('total_id').value = '$4.29';
	  } else if (qty == '5') {
		document.getElementById('tax_id').value = '$0.36';
		document.getElementById('total_id').value = '$5.36';
	  } else if (qty == '6') {
		document.getElementById('tax_id').value = '$0.44';
		document.getElementById('total_id').value = '$6.44';
	  }

  }
<%
}
%>
</script>




  </head>
  <body onload="javascript: doPrice(<%= qty %>);">

<%@ include file="channels\channel-navbar.jsp" %>


    <div class="container">
		
		<div class="row">
			<div class="span4">
				
<%
	if (selected_id == 1) {
%>
				<h1>Pre-Order<br />Dust to Diamonds<br /> HERE</h1>
				<img src="images/book.png" alt="Dust to Diamonds" />
				<h1>Projected release<br />November 2013</h1>
<%
	} else if (selected_id == 2) {
%>
				<h1>Order<br />Christine's Self Defense DVD<br /> HERE</h1>
				<img src="../images/Christine-missions-163-small.jpg" alt="Self-Defense DVD" />
<%
	} else if (selected_id == 3) {
%>
				<h1>Pre-Order<br />Dust to Diamonds<br /> HERE</h1>
				<img src="images/book.png" alt="Dust to Diamonds" />
				<h1>Projected release<br />November 2013</h1>
<%
	} else if (selected_id == 4) {
%>
				<h1>Order your Sano "Fearless" bracelet HERE</h1>
				<img src="../images/bracelet.png" alt="Fearless Bracelet" />
				
<%
	}
%>

			</div>
        <div class="span8">
			
      <!-- Main hero unit for a primary marketing message or call to action -->
      <div class="hero-unit">

        <!--<h1>Dust to Diamonds</h1> -->

<%
	if (selected_id == 1) {
%>
		<p>Fill out the form below to pre-order Dr. Christine Stueve's latest book, Dust to Diamonds for 14.99.</p>
		<p>All of the proceeds from this book go to the Sano Wellness Foundation.</p>
<%
	} else if (selected_id == 2) {
%>
		<p>Fill out the form below to order Dr. Christine Stueve's self-defense DVD.</p>
		<p>Self defense for Women DVD equips, enables and empowers women of all ages to walk without fear. Using easy and effective techniques, we can choose not to be a target. Awareness, knowledge and a plan keep women safe.</p>
		<p>All of the proceeds from this book go to the Sano Wellness Foundation.</p>
<%
	} else if (selected_id == 3) {
%>
		<p>Fill out the form below to pre-order Dr. Christine Stueve's latest book, Dust to Diamonds.  Christine's self-defense DVD will be included in your pre-order.  Order them together to save.</p>
		<p>All of the proceeds from this book go to the Sano Wellness Foundation.</p>
<%
	} else if (selected_id == 4) {
%>
		<p>Fill out the form below to order your Sano "Fearless" bracelet.</p>
		<p>All of the proceeds from this book go to the Sano Wellness Foundation.</p>
<%
	}
%>


<%


/*

    
    <form-bean       name="dustToDiamondsOrderForm"
                     type="org.apache.struts.validator.DynaValidatorForm">
      <form-property name="first" type="java.lang.String"/>
      <form-property name="last" type="java.lang.String"/>
      <form-property name="street" type="java.lang.String"/>
      <form-property name="city" type="java.lang.String"/>
      <form-property name="state" type="java.lang.String"/>
      <form-property name="zip" type="java.lang.String"/>
      <form-property name="qty" type="java.lang.String"/>
      <form-property name="ccnumber" type="java.lang.String"/>
      <form-property name="name_on_card" type="java.lang.String"/>
      <form-property name="exp_month" type="java.lang.Integer"/>
      <form-property name="exp_year" type="java.lang.Integer"/>
      <form-property name="ccv" type="java.lang.String"/>
    </form-bean>
			  

*/

%>

<%

	String errorMessage = (String)session.getAttribute("d2d-message");
	if (errorMessage != null) {
%>
			<div class="alert alert-error">
				<button type="button" class="close" data-dismiss="alert">×</button>
				<h3>Oh Snap!!</h3>
				<strong><%= errorMessage %></strong>
			  
			</div>
				
				<br />
<%
		session.removeAttribute("d2d-message");
	}


%>


		<struts-html:form styleId="loginform" styleClass="form-signin" action="/client/dustToDiamonds" focus="first">
			  <h2 class="form-signin-heading">Name & Address</h2>
			  <input type="text" name="first" class="input-block-level" placeholder="First Name" style="width: 35%;" value="<%= first %>">
			  <input type="text" name="last" class="input-block-level" placeholder="Last Name" style="width: 55%;" value="<%= last %>">
			  <input type="text" name="street" class="input-block-level" placeholder="Street Address" value="<%= street %>">
			  <input type="text" name="city" class="input-block-level" placeholder="City" style="width: 35%;" value="<%= city %>">
			  <input type="text" name="state" class="input-block-level" placeholder="State" style="width: 35%;" value="<%= state %>">
			  <input type="text" name="zip" class="input-block-level" placeholder="Zip" style="width: 20%;" value="<%= zip %>">
			  <input type="text" name="email" class="input-block-level" placeholder="Email Address" value="<%= email %>">
			  <h2 class="form-signin-heading">Quantity & Total</h2>
			  Qty: <select name="qty" style="width: 35%;" onchange="javascript: doPrice(this.value);" >
				  <option value="1"<%= qty == 1 ? " selected" : "" %>>1</option>
				  <option value="2"<%= qty == 2 ? " selected" : "" %>>2</option>
				  <option value="3"<%= qty == 3 ? " selected" : "" %>>3</option>
				  <option value="4"<%= qty == 4 ? " selected" : "" %>>4</option>
				  <option value="5"<%= qty == 5 ? " selected" : "" %>>5</option>
				</select>&nbsp;$<%= (selected_id == 1 || selected_id == 2) ? "14.99" : (selected_id == 3) ? "24.99" : "1.00"  %> each<br />
				Tax: <input type="text" id="tax_id" name="tax" class="input-block-level" placeholder="Tax" value="$<%= (selected_id == 1 || selected_id == 2) ? "1.09" : (selected_id == 3) ? "1.82" : ".07"  %>" disabled="disabled" style="width: 35%;" >
				Total: <input type="text" id="total_id" name="total" class="input-block-level" placeholder="Total" value="$<%= (selected_id == 1 || selected_id == 2) ? "16.08" : (selected_id == 3) ? "26.81" : "1.07"  %>" disabled="disabled" style="width: 35%;" >
			  <h2 class="form-signin-heading">Credit Card</h2>
			  <input type="text" name="ccnumber" class="input-block-level" placeholder="Credit Card Number" value="<%= ccnumber %>">
			  <input type="text" name="name_on_card" class="input-block-level" placeholder="Name on Card" value="<%= name_on_card %>">
			  <select name="exp_month" style="width: 35%;">
				  <option value="<%= Calendar.JANUARY %>"<%= exp_month == Calendar.JANUARY ? " selected" : "" %>>January</option>
				  <option value="<%= Calendar.FEBRUARY %>"<%= exp_month == Calendar.FEBRUARY ? " selected" : "" %>>February</option>
				  <option value="<%= Calendar.MARCH %>"<%= exp_month == Calendar.MARCH ? " selected" : "" %>>March</option>
				  <option value="<%= Calendar.APRIL %>"<%= exp_month == Calendar.APRIL ? " selected" : "" %>>April</option>
				  <option value="<%= Calendar.MAY %>"<%= exp_month == Calendar.MAY ? " selected" : "" %>>May</option>
				  <option value="<%= Calendar.JUNE %>"<%= exp_month == Calendar.JUNE ? " selected" : "" %>>June</option>
				  <option value="<%= Calendar.JULY %>"<%= exp_month == Calendar.JULY ? " selected" : "" %>>July</option>
				  <option value="<%= Calendar.AUGUST %>"<%= exp_month == Calendar.AUGUST ? " selected" : "" %>>August</option>
				  <option value="<%= Calendar.SEPTEMBER %>"<%= exp_month == Calendar.SEPTEMBER ? " selected" : "" %>>September</option>
				  <option value="<%= Calendar.OCTOBER %>"<%= exp_month == Calendar.OCTOBER ? " selected" : "" %>>October</option>
				  <option value="<%= Calendar.NOVEMBER %>"<%= exp_month == Calendar.NOVEMBER ? " selected" : "" %>>November</option>
				  <option value="<%= Calendar.DECEMBER %>"<%= exp_month == Calendar.DECEMBER ? " selected" : "" %>>December</option>
				</select>
			  <select name="exp_year" style="width: 25%;">
				  <option value="2013"<%= (exp_year == 2013) ? " selected" : "" %>>2013</option>
				  <option value="2014"<%= (exp_year == 2014) ? " selected" : "" %>>2014</option>
				  <option value="2015"<%= (exp_year == 2015) ? " selected" : "" %>>2015</option>
				  <option value="2016"<%= (exp_year == 2016) ? " selected" : "" %>>2016</option>
				  <option value="2017"<%= (exp_year == 2017) ? " selected" : "" %>>2017</option>
				  <option value="2018"<%= (exp_year == 2018) ? " selected" : "" %>>2018</option>
				  <option value="2019"<%= (exp_year == 2019) ? " selected" : "" %>>2019</option>
				  <option value="2020"<%= (exp_year == 2020) ? " selected" : "" %>>2020</option>
				</select>
				<input type="text" name="ccv" class="input-block-level" placeholder="CVC Code" style="width: 25%;" value="<%= ccv %>">
				<input type="hidden" name="select" value="<%= selected_id %>">
			  <button class="btn btn-large btn-primary" type="submit">Submit Pre-Order &raquo;</button>
			  <!-- <a style="margin-left: 10px;" class="btn btn-large" href="forgot-password.jsp">I don't have my password</a> --?
		</struts-html:form>

		


      </div>

</div>
</div>

      <hr>

<%@ include file="channels\channel-footer.jsp" %>

    </div> <!-- /container -->

<%@ include file="channels\channel-foot-js.jsp" %>

  </body>
</html>
