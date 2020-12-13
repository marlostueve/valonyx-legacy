<%@ page language="java" buffer="12kb" contentType="text/html" import="com.badiyan.uk.beans.*, java.util.*, java.text.*, org.apache.commons.beanutils.*, com.badiyan.uk.online.beans.*" %>

<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="struts-bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="struts-html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="struts-logic" %>

<jsp:useBean id="adminCompany" class="com.badiyan.uk.online.beans.UKOnlineCompanyBean" scope="session" />
<jsp:useBean id="loginBean" class="com.badiyan.uk.online.beans.UKOnlineLoginBean" scope="session" />
<jsp:useBean id="adminPerson" class="com.badiyan.uk.online.beans.UKOnlinePersonBean" scope="session" />

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
<title>Valonyx</title>

	<style type="text/css" media="all">
	
	body {
		font: small arial, helvetica, sans-serif;
	}
	
	#header ul {
		list-style: none;
		padding: 0;
		margin: 0;
	}
	
	#header li {
		display: inline;
		margin: 0 2px 0 0;
	}
	
	#header a {
		padding: 0 1em;
		text-decoration: none;
		color: #a80;
		background: #fe5;
	}
	
	#header a:hover {
		background: #fc0;
		color: #540;
	}
	
	#header #selected {
	}
	
	#header #selected a {
		padding-bottom: 2px;
		font-weight: bold;
		color: black;
		color: black;
		background: #fc0;
	}
	
	#content {
		border-top: 2px solid white;
		background: #fc0;
		padding: 1em;
	}
	
	#content p {
		margin: 0;
		padding: 1em;
		background: white;
	}
	
	h1 {
		font-size: 1.5em;
		color: #fc0;
	}
	 
#ygunav {background:#fc0; border-bottom:2px solid #ccc; padding:0 10px;font-size:78%;text-align:right;margin-bottom:6px;height:2.5em;line-height:2.5em;}
html>body #ygunav {overflow:hidden;}
#ygunav strong {font-family:verdana;}
#ygunav p {display:inline;margin:0;padding:0;}
#ygunav p em {float:left;text-align:left;font-style:normal; padding-top:.7em}
* html #ygunav p em {margin-top:1px;}
#ygunav p em i {visibility:hidden;}
#ygunav a {color:#000;}
#ygunav form {display:inline;margin:0 0 0 1em;}
#ygsp {width:8em;font-size:110%;padding:0;vertical-align:middle;}
#ygunav .ygbt {background:#dcdcdc;font:110% verdana;position:relative;top:1px;}
* html #ygunav .ygbt {top:4px;}
* html>body #ygunav .ygbt {line-height:0;top:-4px;}
#ygunav label {color:#666;font-family:tahoma;position:relative;top:1px;}

.adminInput {width:8em;font-size:110%;padding:0;vertical-align:middle;}

div.adminItem            { width: 292px;              margin: 2px 0px 3px  0px; }
div.adminItem div.left   { width: 100px; float: left; }
div.adminItem div.leftTM { width: 100px; float: left; margin-top: 2px; }
div.adminItem div.right  { width: 192px; float: left; }
div.adminItem div.end    { clear: left; }

div.adminItem div.left   { color: #4060af; font-weight: bold; }
div.adminItem div.leftTM { color: #4060af; font-weight: bold; }
div.adminItem div.right  { color: #000000;                    }
	
	</style>


<style type="text/css">
div.tableContainer {clear: both;border: 1px solid #963;	height: 485px;	overflow: auto;	width: 100%;}
div.tableContainer table {float: left;width: 100%}
\html div.tableContainer table/* */ {margin: 0 -16px 0 0}
thead.fixedHeader tr {position: relative;top: expression(document.getElementById("tableContainer").scrollTop);}
head:first-child+body thead[class].fixedHeader tr {display: block;}
thead.fixedHeader th {background: #C96;border-left: 1px solid #EB8;border-right: 1px solid #B74;border-top: 1px solid #EB8;font-weight: normal;	padding: 4px 3px;text-align: left; font-weight: bold;}
thead.fixedHeader a, thead.fixedHeader a:link, thead.fixedHeader a:visited {color: #FFF;display: block;text-decoration: none;width: 100%}
thead.fixedHeader a:hover {color: #FFF;	display: block;	text-decoration: underline;width: 100%}
head:first-child+body tbody[class].scrollContent {display: block;height: 262px;overflow: auto;width: 100%}
tbody.scrollContent td, tbody.scrollContent tr.normalRow td {background: #FFF;border-bottom: none;border-left: none;border-right: 1px solid #CCC;border-top: 1px solid #DDD;padding: 0px 1px 1px 1px; vertical-align: top; }
tbody.scrollContent tr.alternateRow td {background: #EEE;border-bottom: none;border-left: none;	border-right: 1px solid #CCC;border-top: 1px solid #DDD;padding: 0px 1px 1px 1px}
head:first-child+body thead[class].fixedHeader th {width: 200px}
head:first-child+body thead[class].fixedHeader th + th {width: 240px}
head:first-child+body thead[class].fixedHeader th + th + th {border-right: none;padding: 4px 4px 4px 3px;width: 316px}
head:first-child+body tbody[class].scrollContent td {width: 200px}
head:first-child+body tbody[class].scrollContent td + td {width: 240px}
head:first-child+body tbody[class].scrollContent td + td + td {border-right: none;padding: 2px 4px 2px 3px;width: 300px}
</style>



<link rel="stylesheet" type="text/css" href="../yui/build/fonts/fonts-min.css" />
<link rel="stylesheet" type="text/css" href="../yui/build/button/assets/skins/sam/button.css" />
<link rel="stylesheet" type="text/css" href="../yui/build/container/assets/skins/sam/container.css" />
<script type="text/javascript" src="../yui/build/utilities/utilities.js"></script>
<script type="text/javascript" src="../yui/build/button/button-beta.js"></script>
<script type="text/javascript" src="../yui/build/container/container.js"></script>




<script type="text/javascript" src="../../build/yahoo-dom-event/yahoo-dom-event.js"></script>
<script type="text/javascript" src="../../build/element/element-beta.js"></script>

<script type="text/javascript">
    
    var scheduleArray = new Array();
    var idArray = new Array();
    var scheduleIndex = 0;
    var typeArray = new Array();
    var httpRequest;
    var request_NextDay;
    
    var edit = 0;

<%
Iterator type_itr = AppointmentTypeBean.getAppointmentTypes(adminCompany).iterator();
while (type_itr.hasNext())
{
    AppointmentTypeBean appointmentType = (AppointmentTypeBean)type_itr.next();
%>
    typeArray["<%= appointmentType.getId() %>"] = {"bg" : "<%= appointmentType.getBGColorCodeString() %>", "txt" : "<%= appointmentType.getTextColorCodeString() %>", "duration" : "<%= appointmentType.getDurationString() %>" };
<%
}
%>

    function processCommand(command, parameter)
    {
		if (window.ActiveXObject)
			httpRequest = new ActiveXObject("Microsoft.XMLHTTP");
		else if (window.XMLHttpRequest)
			httpRequest = new XMLHttpRequest();

		httpRequest.open("POST", '<%= CUBean.getProperty("cu.webDomain") %>/ClientServlet.html', true);
		httpRequest.setRequestHeader('Content-Type','application/x-www-form-urlencoded');
		httpRequest.onreadystatechange = function() {processCommandEvent(); } ;
		eval("httpRequest.send('command=" + command + "&parameter=" + parameter + "')");

		return true;
    }

    function processCommandEvent()
    {
		if (httpRequest.readyState == 4)
		{
			if (httpRequest.status == 200)
			{
				if (httpRequest.responseXML.getElementsByTagName("status").length > 0)
				{
					var xml_response = httpRequest.responseXML.getElementsByTagName("status")[0];

					document.getElementById('today_date').firstChild.nodeValue = xml_response.getElementsByTagName("date")[0].childNodes[0].nodeValue;
					document.getElementById('new_appt_date').firstChild.nodeValue = xml_response.getElementsByTagName("date")[0].childNodes[0].nodeValue;
					//document.getElementById('newAppointmentDateInput').value = xml_response.getElementsByTagName("date")[0].childNodes[0].nodeValue;
					document.forms[0].newAppointmentDateInput.value = xml_response.getElementsByTagName("date")[0].childNodes[0].nodeValue;

					buildScheduleArray(xml_response);
				}
				else if (httpRequest.responseXML.getElementsByTagName("clients").length > 0)
				{
					//alert(httpRequest.responseXML.getElementsByTagName("clients").length);
					var xml_response = httpRequest.responseXML.getElementsByTagName("clients")[0];
					showPeople(xml_response);
				}
				else if (httpRequest.responseXML.getElementsByTagName("person").length > 0)
				{
					//alert(httpRequest.responseXML.getElementsByTagName("clients").length);
					var xml_response = httpRequest.responseXML.getElementsByTagName("person")[0];
					//showPeople(xml_response);
					
					showPerson(xml_response);
				}
				else if (httpRequest.responseXML.getElementsByTagName("error").length > 0)
				{
					var xml_response = httpRequest.responseXML.getElementsByTagName("error")[0];
					alert("Error : " + xml_response.childNodes[0].nodeValue);
				}
			}
			else
			{
				alert("Error : " + httpRequest.status + " : " + httpRequest.statusText);
			}
		}
    }
    
    function showPeople(xml_str)
    {
		document.getElementById('clientSelect').options.length = 0;
		var index = 0;
		var selectedValue = <%= adminPerson.getId() %>;
		while (xml_str.getElementsByTagName("person")[index] != null)
		{
			key = xml_str.getElementsByTagName("person")[index].childNodes[0].childNodes[0].nodeValue;
			value = xml_str.getElementsByTagName("person")[index].childNodes[1].childNodes[0].nodeValue;
			eval("var personArray = " + value);
			
			document.getElementById('clientSelect').options[index] = new Option(personArray["label"],key);
			if (key == selectedValue)
				document.getElementById('clientSelect').selectedIndex = index;

			index++;
		}
    }
    
    function showPerson(xml_str)
    {
		//document.getElementById('clientSelect').options.length = 0;
		
		 
		var key = xml_str.childNodes[0].childNodes[0].nodeValue;
		var value = xml_str.childNodes[1].childNodes[0].nodeValue;
		
		eval("var personArray = " + value);
		//alert(personArray["id"]);
		document.forms[0].person_id.value = personArray["id"];
		document.forms[0].prefix.value = personArray["prefix"];
		document.forms[0].firstname.value = personArray["fn"];
		document.forms[0].middle.value = personArray["mn"];
		document.forms[0].lastname.value = personArray["ln"];
		document.forms[0].suffix.value = personArray["suffix"];

		document.forms[0].address1.value = personArray["addr1"];
		document.forms[0].address2.value = personArray["addr2"];
		document.forms[0].city.value = personArray["city"];
		document.forms[0].state.value = personArray["state"];
		document.forms[0].zipcode.value = personArray["zip"];

		document.forms[0].ssn.value = personArray["ssn"];
		document.forms[0].phone.value = personArray["phone"];
		document.forms[0].cell.value = personArray["cell"];
		document.forms[0].email.value = personArray["email"];
		document.forms[0].dob.value = personArray["dob"];
		
		if (personArray["gender"] == "true")
			document.forms[0].gender[0].checked = true;
		else
			document.forms[0].gender[1].checked = true;

		if (personArray["deceased"] == "true")
			document.forms[0].deceased.checked = true;
		else
			document.forms[0].deceased.checked = false;
		//radioObj.checked = (radioObj.value == newValue.toString());
		
		/*
		var index = 0;
		while (xml_str.getElementsByTagName("person")[index] != null)
		{
			key = xml_str.getElementsByTagName("person")[index].childNodes[0].childNodes[0].nodeValue;
			value = xml_str.getElementsByTagName("person")[index].childNodes[1].childNodes[0].nodeValue;
			eval("var personArray = " + value);

			//document.getElementById('clientSelect').options[index] = new Option(personArray["label"],key);

			index++;
		}
		*/
    }

    function selectPerson()
    {
		//getPersonDetails
		
		//alert(document.getElementById('clientSelect').options[document.getElementById('clientSelect').selectedIndex].value);
		//document.getElementById('clientSelect').options.length = 0;
		
		processCommand('getPersonDetails', document.getElementById('clientSelect').options[document.getElementById('clientSelect').selectedIndex].value);
    }
        
    function selectPlanInstance()
    {
        processCommand('getPlanInstanceDetails', document.getElementById('planSelect').options[document.getElementById('planSelect').selectedIndex].value);
    }

</script>

<style>
#example {height:30em;}
label { display:block;float:left;width:45%;clear:left; }
.clear { clear:both; }
#resp { margin:10px;padding:5px;border:1px solid #ccc;background:#fff;}
#resp li { font-family:monospace }
</style>

<script>
YAHOO.namespace("example.container");

function init() {
	
	// Define various event handlers for Dialog
	var handleSubmit = function() {
		this.submit();
	};
	var handleSuccess = function(o) {
		if (o.responseText.length > 0)
		    alert(o.responseText);
		var response = o.responseText;
		document.getElementById("resp").innerHTML = response;
		
		document.forms[0].appointmentSelect.value = -1;
		document.forms[0].typeSelect.selectedIndex = 0;
		document.forms[0].duration.value = "";
	
	};
	var handleFailure = function(o) {
		alert("Submission failed: " + o.status);
	};

	// Instantiate the Dialog
	YAHOO.example.container.dialog1 = new YAHOO.widget.Dialog("dialog1", 
																{ width : "475px",
																  fixedcenter : true,
																  visible : false, 
																  constraintoviewport : true,
																  buttons : [ { text:"Submit", handler:handleSubmit, isDefault:true } ]
																 } );
	
	// Validate the entries in the form to require that both first and last name are entered
	YAHOO.example.container.dialog1.validate = function() {
		var data = this.getData();
		if (data.typeSelect == 0)
		{
		    alert("Please select an appointment type.");
		    return false;
		}
		if (data.duration == "")
		{
		    alert("Please specify a duration.");
		    return false;
		}
		
		return true;
	};

	// Wire up the success and failure handlers
	YAHOO.example.container.dialog1.callback = { success: handleSuccess, failure: handleFailure };
	
	// Render the Dialog
	YAHOO.example.container.dialog1.render();

}

YAHOO.util.Event.onDOMReady(init);

</script>

<!--there is no custom header content for this example-->


</head>

<%
String searchLastName = (String)session.getAttribute("searchLastName");
if (searchLastName == null)
	searchLastName = "";
%>

<body class="yui-skin-sam"<%= (searchLastName.length() > 0) ? " onload=\"processCommand('getPeopleByLastName', '" + searchLastName + "');\"" : "" %>>

<!-- <h1>Valeo Schedule</h1> -->

<%@ include file="channels/channel-schedule-menu.jsp" %>

<div id="content">

<!--BEGIN SOURCE CODE FOR EXAMPLE =============================== -->

    <table id="clients" border="0" cellpadding="0" cellspacing="0" width="97%">
        <tbody>
            <tr>
                <td width="30%" valign="top">
					<label for="lastname">Search Last Name:</label><input value="<%= searchLastName %>" class="adminInput" id="lastname" type="textbox" onkeyup="if (document.getElementById('lastname').value.length > 0) {processCommand('getPeopleByLastName', document.getElementById('lastname').value);}" name="lastname" />
					<div class="clear"></div>
					<label for="firstname">Search First Name:</label><input class="adminInput" type="textbox" name="firstname" />
					<div class="clear"></div>
					<label for="clientSelect">Client:</label>
					<div class="clear"></div>
					
					<select multiple name="clientSelect" size="25" id="clientSelect" class="adminInput" style="width: 75%" onchange="selectPerson();">
						<option value="-1">-- SEARCH FOR A CLIENT --</option>
					</select>
				</td>
				<td valign="top">
					 
<%@ include file="channels/channel-schedule-menu-clients.jsp" %>
					 <div id="content">
						 <table id="clients" border="0" cellpadding="0" cellspacing="0">
							  <struts-html:form action="/admin/clientFinancial" >
							  <tr>
								   <td align="left"  valign="top">
										<div class="adminItem">
											<div class="leftTM">Available Payment Plans</div>
											<div class="right">
<%
boolean plans_exist = false;
Iterator plans_itr = PaymentPlanBean.getPaymentPlans(adminCompany).iterator();
if (plans_itr.hasNext())
{
	plans_exist = true;
%>
												<select multiple name="purchasePlanSelect" size="5" id="purchasePlanSelect" class="adminInput" style="width: 75%">
<%
	while (plans_itr.hasNext())
	{
		PaymentPlanBean payment_plan = (PaymentPlanBean)plans_itr.next();
%>
													<option value="<%= payment_plan.getValue() %>"><%= payment_plan.getLabel() %></option>
<%
	}
%>
												</select>
<%
}
else
{
%>
												No payment plans found.  Contact your administrator to create the payment plans available in your practice.
<%
}
%>
											</div>
											<div class="end"></div>
										</div>
<%
if (plans_exist)
{
%>
										<div class="adminItem">
											<div class="leftTM"></div>
											<div class="right">
												<input type="submit" name="purchasePlan" value="Purchase Plan" class="ygbt">
											</div>
											<div class="end"></div>
										</div>
<%
}
%>
										<div class="adminItem">
											<div class="leftTM"></div>
											<div class="right"></div>
											<div class="end"></div>
										</div>
										<div class="adminItem">
											<div class="leftTM">Purchased Payment Plans</div>
											<div class="right">
												
<%
try
{
Iterator purchased_plan_instances_itr = PaymentPlanInstanceBean.getPaymentPlanInstances(adminPerson).iterator();
if (purchased_plan_instances_itr.hasNext())
{
%>
												<select multiple name="planSelect" size="5" id="planSelect" class="adminInput" style="width: 75%" onchange="selectPlanInstance();">
<%
	while (purchased_plan_instances_itr.hasNext())
	{
		PaymentPlanInstanceBean purchased_plan = (PaymentPlanInstanceBean)purchased_plan_instances_itr.next();
%>
													<option value="<%= purchased_plan.getValue() %>"><%= purchased_plan.getLabel() %></option>
<%
	}
%>
												</select>
<%
}
else
{
%>
												No Plans Purchased.
<%
}
}
catch (Exception x)
{
	x.printStackTrace();
}
%>
											</div>
											<div class="end"></div>
										</div>
										<div class="adminItem">
											<div class="leftTM"></div>
											<div class="right">
												<input type="submit" value="Delete Plan" class="ygbt">
											</div>
											<div class="end"></div>
										</div>
										<div class="adminItem">
											<div class="leftTM"></div>
											<div class="right"></div>
											<div class="end"></div>
										</div>
								</td>
								 <td valign="top">
										<div class="adminItem">
											<div class="leftTM"><span title="When checked, plan visits will be poooled among others in the group inder care.">Include Group Under Care Members</span></div>
											<div class="right">
												<input type="checkbox" name="includeGroup" />
											</div>
											<div class="end"></div>
										</div>
										<div class="adminItem">
											<div class="leftTM">Plan Start Date</div>
											<div class="right">
												<input value="" name="phone" size="7" maxlength="12" class="adminInput" style="width: 80px;" />
											</div>
											<div class="end"></div>
										</div>
										<div class="adminItem">
											<div class="leftTM">Prepaid Visits</div>
											<div class="right">
												<input value="24" name="phone" size="7" maxlength="3" class="adminInput" style="width: 32px;" disabled />
											</div>
											<div class="end"></div>
										</div>
										<div class="adminItem">
											<div class="leftTM">Visits Used</div>
											<div class="right">
												<input value="10" name="phone" size="7" maxlength="3" class="adminInput" style="width: 32px;" disabled />
											</div>
											<div class="end"></div>
										</div>
										<div class="adminItem">
											<div class="leftTM">Visits Remaining</div>
											<div class="right">
												<input value="14" name="phone" size="7" maxlength="3" class="adminInput" style="width: 32px;" />
											</div>
											<div class="end"></div>
										</div>
										<div class="adminItem">
											<div class="leftTM"></div>
											<div class="right"></div>
											<div class="end"></div>
										</div>
										<div class="adminItem">
											<div class="leftTM">Per Visit Charge</div>
											<div class="right">
												<input value="" name="phone" size="7" maxlength="7" class="adminInput" style="width: 64px;" />
											</div>
											<div class="end"></div>
										</div>
										<div class="adminItem">
											<div class="leftTM">Total Charge</div>
											<div class="right">
												<input value="" name="phone" size="7" maxlength="7" class="adminInput" style="width: 64px;" />
											</div>
											<div class="end"></div>
										</div>
										<div class="adminItem">
											<div class="leftTM">Escrow Amount</div>
											<div class="right">
												<input value="" name="phone" size="7" maxlength="7" class="adminInput" style="width: 64px;" />
											</div>
											<div class="end"></div>
										</div>
										<div class="adminItem">
											<div class="leftTM">Drop Plan Charge</div>
											<div class="right">
												<input value="" name="phone" size="7" maxlength="7" class="adminInput" style="width: 64px;" />
											</div>
											<div class="end"></div>
										</div>
								 </td>
							</tr>
							  <tr>
								   <td align="left"  valign="top">
									&nbsp;
								</td>
								 <td valign="top">

								 </td>
							</tr>
							  <tr>
								   <td align="left"  valign="top">

								</td>
								 <td valign="top">
							<div class="adminItem">
								<div class="leftTM">&nbsp;</div>
								<div class="right">
									<input type="submit" value="Save" class="ygbt">
								</div>
								<div class="end"></div>
							</div>
								 </td>
							</tr>
							  <tr>
								   <td align="left"  valign="top">
									&nbsp;
								</td>
								 <td valign="top">

								 </td>
							</tr>
							 </struts-html:form>
						 </table>
					  </div>
				</td>
            </tr>

        </tbody>
    </table>


</div>

<!--END SOURCE CODE FOR EXAMPLE =============================== -->

</body>

</html>
