<%@ page language="java" buffer="12kb" contentType="text/html" import="com.badiyan.uk.beans.*, com.badiyan.torque.*, java.util.*, java.text.*, java.math.*, org.apache.commons.beanutils.*, com.badiyan.uk.online.beans.*, com.valeo.qb.data.*, com.badiyan.uk.exceptions.*;" %>

<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="struts-bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="struts-html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="struts-logic" %>

<jsp:useBean id="adminCompany" class="com.badiyan.uk.online.beans.UKOnlineCompanyBean" scope="session" />
<jsp:useBean id="loginBean" class="com.badiyan.uk.online.beans.UKOnlineLoginBean" scope="session" />
<jsp:useBean id="adminPerson" class="com.badiyan.uk.online.beans.UKOnlinePersonBean" scope="session" />



<%
CompanySettingsBean settings = adminCompany.getSettings();

if (request.getParameter("id") != null)
{
	try
	{
		adminPerson = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(adminCompany, Integer.parseInt(request.getParameter("id")));
		session.setAttribute("adminPerson", adminPerson);
	}
	catch (Exception x)
	{
		x.printStackTrace();
	}
}

String search_str = (String)session.getAttribute("invSearchStr");
if (search_str == null)
	search_str = "";

String vendor_search_str = (String)session.getAttribute("vendorSearchStr");
if (vendor_search_str == null)
	vendor_search_str = "";



String searchLastName = (String)session.getAttribute("searchLastName");
if (searchLastName == null)
	searchLastName = "";

String searchFirstName = (String)session.getAttribute("searchFirstName");
if (searchFirstName == null)
	searchFirstName = "";

String searchFileNumber = (String)session.getAttribute("searchFileNumber");
if (searchFileNumber == null)
	searchFileNumber = "";



boolean is_prospect = false;
if (!adminPerson.isNew())
	is_prospect = adminPerson.getPersonType().equals(UKOnlinePersonBean.PROSPECT_PERSON_TYPE);

if (adminPerson.isNew())
	adminPerson.setFemale();

Vector codes = CheckoutCodeBean.getCheckoutCodes(adminCompany);
GroupUnderCareBean group_under_care = null;
Calendar now = Calendar.getInstance();
SimpleDateFormat date_format = new SimpleDateFormat("EEEE, MMMM d, yyyy");

if (!adminPerson.isNew()) {
	adminPerson.setPassword(adminPerson.getPasswordString());
	adminPerson.setConfirmPassword(adminPerson.getPasswordString());
}

%>

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
<head>
    <title>Valonyx</title>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
	<link rel="stylesheet" type="text/css" href="../css/schedule-client.css" />
	<link rel="stylesheet" type="text/css" href="../css/schedule-stylesheet.css" />
	

    <style type="text/css">
    /*
      margin and padding on body element
      can introduce errors in determining
      element position and are not recommended;
      we turn them off as a foundation for YUI
      CSS treatments.
    */
    body {
        margin:0;
        padding:0;
    }
    </style>

    <link rel="stylesheet" type="text/css" href="https://www.valonyx.com/yui/build/fonts/fonts-min.css" />
    <link rel="stylesheet" type="text/css" href="https://www.valonyx.com/yui/build/container/assets/skins/sam/container.css" />
    <link rel="stylesheet" type="text/css" href="https://www.valonyx.com/yui/build/resize/assets/skins/sam/resize.css" />
    <script type="text/javascript" src="https://www.valonyx.com/yui/build/utilities/utilities.js"></script>
    <script type="text/javascript" src="https://www.valonyx.com/yui/build/container/container.js"></script>
    <script type="text/javascript" src="https://www.valonyx.com/yui/build/resize/resize.js"></script>


	<link rel="stylesheet" type="text/css"href="https://www.valonyx.com/yui/build/reset-fonts-grids/reset-fonts-grids.css" />
	<link rel="stylesheet" type="text/css" href="https://www.valonyx.com/yui/build/assets/skins/sam/layout.css" />
	<script type="text/javascript" src="https://www.valonyx.com/yui/build/yahoo-dom-event/yahoo-dom-event.js"></script>
	<script type="text/javascript" src="https://www.valonyx.com/yui/build/dragdrop/dragdrop-min.js"></script>
	<script type="text/javascript" src="https://www.valonyx.com/yui/build/element/element-min.js"></script>
	<script type="text/javascript" src="https://www.valonyx.com/yui/build/animation/animation-min.js"></script>
	<script type="text/javascript" src="https://www.valonyx.com/yui/build/resize/resize-min.js"></script>
	<script type="text/javascript" src="https://www.valonyx.com/yui/build/layout/layout-min.js"></script>

	<link rel="stylesheet" type="text/css" href="https://www.valonyx.com/yui/build/button/assets/skins/sam/button.css" />
	<script type="text/javascript" src="https://www.valonyx.com/yui/build/button/button-min.js"></script>


    <style type="text/css">

    #resizablepanel .bd {
        overflow:auto;
        background-color:#fff;
        padding:10px;
    }

    #resizablepanel .ft {
        height:15px;
        padding:0;
    }

    #resizablepanel .yui-resize-handle-br {
        right:0;
        bottom:0;
        height: 8px;
        width: 8px;
        position:absolute;
    }

    /*
        The following CSS is added to prevent scrollbar bleedthrough on
        Gecko browsers (e.g. Firefox) on MacOS.
    */

    /*
        PLEASE NOTE: It is necessary to toggle the "overflow" property
        of the body element between "hidden" and "auto" in order to
        prevent the scrollbars from remaining visible after the the
        Resizable Panel is hidden.  For more information on this issue,
        read the comments in the "container-core.css" file.

        We use the #reziablepanel_c id based specifier, so that the rule
        is specific enough to over-ride the .bd overflow rule above.
    */

    #resizablepanel_c.hide-scrollbars .yui-resize .bd {
        overflow: hidden;
    }

    #resizablepanel_c.show-scrollbars .yui-resize .bd {
        overflow: auto;
    }

    /*
        PLEASE NOTE: It is necessary to set the "overflow" property of
        the underlay element to "visible" in order for the
        scrollbars on the body of a Resizable Panel instance to be
        visible.  By default the "overflow" property of the underlay
        element is set to "auto" when a Panel is made visible on
        Gecko for Mac OS X to prevent scrollbars from poking through
        it on that browser + platform combintation.  For more
        information on this issue, read the comments in the
        "container-core.css" file.
    */

    #resizablepanel_c.show-scrollbars .underlay {
        overflow: visible;
    }
    </style>


	<script type="text/javascript">

    var codeArray = new Array();

    var httpRequest;
    var xml_response;
	var panel;
	
	var scheduleArray = new Array();
    var idArray = new Array();
    var scheduleIndex = 0;
    var typeArray = new Array();
    var request_NextDay;

    var edit = 0;

    var show_group_names = false;
    var show_assign_names = false;
    var show_referral_names = false;
    var show_referral_names2 = false;

    var initial = true;

    var last_order = -1;
    var iteration = 0;
    var selected_order = 0;
	

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
				if (httpRequest.responseXML.getElementsByTagName("checkout").length > 0)
				{
					xml_response = httpRequest.responseXML.getElementsByTagName("checkout")[0];
					showCheckout(xml_response);
				}
				else if (httpRequest.responseXML.getElementsByTagName("status").length > 0)
				{
					var xml_response = httpRequest.responseXML.getElementsByTagName("status")[0];
					showPurchasedPlans(xml_response);
				}
				else if (httpRequest.responseXML.getElementsByTagName("clients").length > 0)
				{
					var xml_response = httpRequest.responseXML.getElementsByTagName("clients")[0];
					showPeople(xml_response);
				}
				else if (httpRequest.responseXML.getElementsByTagName("person").length > 0)
				{
					var xml_response = httpRequest.responseXML.getElementsByTagName("person")[0];
					showPerson(xml_response);
				}
				else if (httpRequest.responseXML.getElementsByTagName("payment-plan-instance").length > 0)
				{
					xml_response = httpRequest.responseXML.getElementsByTagName("payment-plan-instance")[0];
					showPaymentPlanInstance(xml_response);
				}
				else if (httpRequest.responseXML.getElementsByTagName("client-billing").length > 0)
				{
					var xml_response = httpRequest.responseXML.getElementsByTagName("client-billing")[0];
					showBilling(xml_response);
				}
				else if (httpRequest.responseXML.getElementsByTagName("bill").length > 0)
				{
					var xml_response = httpRequest.responseXML.getElementsByTagName("bill")[0];
					showBill(xml_response);
				}
				else if (httpRequest.responseXML.getElementsByTagName("billing-orders").length > 0)
				{
					var xml_response = httpRequest.responseXML.getElementsByTagName("billing-orders")[0];
					showBillingOrders(xml_response);
				}
				else if (httpRequest.responseXML.getElementsByTagName("nfn").length > 0)
				{
					xml_response = httpRequest.responseXML.getElementsByTagName("nfn")[0];
					document.forms[0].file_number.value = xml_response.getAttribute("num");
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
		if (show_referral_names)
			document.forms['newClientForm'].referralClientSelect.options.length = 0;
		else if (show_referral_names2)
			document.forms['clientProfileForm'].referralProfileSelect.options.length = 0;
		else if (show_assign_names)
			document.forms['assignGroupUnderCareForm'].groupSelect.options.length = 0;
		else if (show_group_names)
			document.forms['newClientForm'].groupSelect.options.length = 0;
		else
			document.getElementById('clientSelect').options.length = 0;

		var index = 0;
		if (xml_str.getElementsByTagName("person")[index] != null)
		{
			while (xml_str.getElementsByTagName("person")[index] != null)
			{
			key = xml_str.getElementsByTagName("person")[index].childNodes[0].childNodes[0].nodeValue;
			value = xml_str.getElementsByTagName("person")[index].childNodes[1].childNodes[0].nodeValue;
			eval("var personArray = " + value);

			if (show_referral_names)
				document.forms['newClientForm'].referralClientSelect.options[index] = new Option(personArray["label"],key);
			else if (show_referral_names2)
				document.forms['clientProfileForm'].referralProfileSelect.options[index] = new Option(personArray["label"],key);
			else if (show_assign_names)
				document.forms['assignGroupUnderCareForm'].groupSelect.options[index] = new Option(personArray["label"],key);
			else if (show_group_names)
				document.forms['newClientForm'].groupSelect.options[index] = new Option(personArray["label"],key);
			else
				document.getElementById('clientSelect').options[index] = new Option(personArray["label"],key);

			index++;
			}
		}
		else
		{
			if (show_referral_names)
			document.forms['newClientForm'].referralClientSelect.options[index] = new Option('No Clients Found',-1);
			else if (show_referral_names2)
			document.forms['clientProfileForm'].referralProfileSelect.options[index] = new Option('No Clients Found',-1);
			else if (show_assign_names)
			document.forms['assignGroupUnderCareForm'].groupSelect.options[index] = new Option('No Clients Found',-1);
			else if (show_group_names)
			document.forms['newClientForm'].groupSelect.options[index] = new Option('No Clients Found',-1);
			else
			document.getElementById('clientSelect').options[index] = new Option('No Clients Found',-1);
		}

		show_group_names = false;
		show_assign_names = false;
		show_referral_names = false;
		show_referral_names2 = false;

		if (initial)
		{
			//refresh();
			initial = false;
		}
    }

    function selectPerson()
    {
		//unselectPlan();
		processCommand('getPersonDetails', document.getElementById('clientSelect').options[document.getElementById('clientSelect').selectedIndex].value);
    }

    function showPerson(xml_str)
    {
		//var key = xml_str.childNodes[0].text;
		//var value = xml_str.childNodes[1].text;

		//.childNodes[0].nodeValue;

		var key = xml_str.childNodes[0].childNodes[0].nodeValue;
		var value = xml_str.childNodes[1].childNodes[0].nodeValue;

		eval("var personArray = " + value);

		document.getElementById('selectedPerson').firstChild.nodeValue = personArray["label"];

		if (personArray["pt"] == "<%= UKOnlinePersonBean.PROSPECT_PERSON_TYPE%>")
		{
			document.forms[0].c_prospect.checked = true;
			document.forms[0].hidden_filenumber.value=document.forms[0].file_number.value;
			document.forms[0].file_number.value='';
			document.forms[0].file_number.disabled=true;
		}
		else
		{
			document.forms[0].c_prospect.checked = false;
			document.forms[0].file_number.disabled=false;
			document.forms[0].file_number.value=document.forms[0].hidden_filenumber.value;
		}

		document.forms[0].person_id.value = personArray["id"];
		document.forms[0].file_number.value = personArray["file"];
		document.forms[0].prefix.value = personArray["prefix"];
		document.forms[0].firstname.value = personArray["fn"];
		document.forms[0].middle.value = personArray["mn"];
		document.forms[0].lastname.value = personArray["ln"];
		document.forms[0].suffix.value = personArray["suffix"];

		document.getElementById('n_appt').firstChild.nodeValue = personArray["n_appt"];
		document.getElementById('l_appt').firstChild.nodeValue = personArray["l_appt"];
		document.forms[0].pos_id.value = personArray["pos_id"];
		document.forms[0].qb_id.value = personArray["qb_id"];
		document.forms[0].address1.value = personArray["addr1"];
		document.forms[0].address2.value = personArray["addr2"];
		document.forms[0].city.value = personArray["city"];
		document.forms[0].state.value = personArray["state"];
		document.forms[0].zipcode.value = personArray["zip"];

		document.forms[0].ssn.value = personArray["ssn"];
		document.forms[0].password.value = personArray["password"];
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

		if (personArray["sel"] == "true")
		{
			document.getElementById('lastname').value = personArray["ln"];
			document.getElementById('clientSelect').options.length = 0;
			document.getElementById('clientSelect').options[0] = new Option(personArray["label"],key);
			document.getElementById('clientSelect').selectedIndex = 0;
		}

		document.forms[0].client_type.value = personArray["ct"];
		document.forms[0].file_type.value = personArray["ft"];

		document.forms[0].groupSelect.options.length = 0;
		var index = 0;
		if (xml_str.getElementsByTagName("person")[index] != null)
		{
			while (xml_str.getElementsByTagName("person")[index] != null)
			{
			key = xml_str.getElementsByTagName("person")[index].childNodes[0].childNodes[0].nodeValue;
			value = xml_str.getElementsByTagName("person")[index].childNodes[1].childNodes[0].nodeValue;
			eval("arr = " + value);
			document.forms[0].groupSelect.options[index] = new Option(arr["label"] + ' (' + arr["relate"] + ')',key);
			index++;
			}
		}
		else
			document.forms[0].groupSelect.options[index] = new Option('Group Not Found',-1);


		document.forms[0].marketingProfileSelect.value = personArray["mc"];

		document.forms[0].referralProfileSelect.options.length = 0;
		if (personArray["rc"] == "0")
			document.forms[0].referralProfileSelect.options[0] = new Option('-- SEARCH FOR A CLIENT --',0);
		else
		{
			document.forms[0].referralProfileSelect.options[0] = new Option(personArray["rcl"],personArray["rc"]);
			document.forms[0].referralProfileSelect.selectedIndex = 0;
		}

		document.assignGroupUnderCareForm.clientSelect.value = personArray["id"];

		//refresh();
    }

	function newClick2() {
            document.newClientForm.firstname.value='';
	    document.newClientForm.lastname.value='';
	    document.newClientForm.relationshipSelect.value='1';
	    document.newClientForm.clientFileTypeSelect.value='-1';
	    YAHOO.example.container.dialog2.show();
        }

    function showCodes(xml_str)
    {
		codeArray = new Array();

        //document.forms[0].codeSelect.options.length = 0;

		deleteAllRows(document.getElementById('inventory-table'));

        var index = 0;
        while (xml_str.getElementsByTagName("code")[index] != null)
        {
            key = xml_str.getElementsByTagName("code")[index].childNodes[0].childNodes[0].nodeValue;
            value = xml_str.getElementsByTagName("code")[index].childNodes[1].childNodes[0].nodeValue;
            eval("var codeArr = " + value);

			codeArray[key] = codeArr;

			//document.forms[0].codeSelect.options[index] = new Option(codeArr["desc"],key);

			addItemRow(key, codeArr["desc"], codeArr["type"], codeArr["pa"], codeArr["dept"], codeArr["com"]);

			index++;
        }
    }

    var last_code;
	var qty = 0;

	function selectCode(code)
	{
		if (code == last_code)
			qty += 1;
		else
			qty = 1;

		document.getElementById("qty").value = qty;
		document.getElementById("rate").value = codeArray[code]["cost"];

		last_code = code;
		calculateAmount();
	}

    Number.prototype.formatMoney = function(c, d, t){
	var n = this, c = isNaN(c = Math.abs(c)) ? 2 : c, d = d == undefined ? "," : d, t = t == undefined ? "." : t, s = n < 0 ? "-" : "", i = parseInt(n = Math.abs(+n || 0).toFixed(c)) + "", j = (j = i.length) > 3 ? j % 3 : 0;
	return s + (j ? i.substr(0, j) + t : "") + i.substr(j).replace(/(\d{3})(?=\d)/g, "$1" + t) + (c ? d + Math.abs(n - i).toFixed(c).slice(2) : "");
    };

	var last_target;
	function mouseUpHandler(e)
	{
		var event = e || window.event;
		var target = event.target || event.srcElement;
		if (target != last_target)
		{
			e.preventDefault();
			last_target = target;
		}
	}
	document.onmouseup = mouseUpHandler;

	</script>
	
<script>
YAHOO.namespace("example.container");

function init() {

        var layout = new YAHOO.widget.Layout({
                minWidth: 750,
                units: [
		{ position: 'top', height: 21, resize: false, body: 'top1' },
		{ position: 'left', header: 'Client Search', width: 213, resize: false, body: 'left1', gutter: '0px', collapse: true, close: false, scroll: true, animate: true, duration: .1 },
                { position: 'center', body: 'center1', scroll: true, minWidth: 400 }
            ]
        });
        layout.on('render', function() {
            layout.getUnitByPosition('left').on('close', function() {
                closeLeft();
            });
        });
        layout.render();

// Define various event handlers for Dialog
	var hsANG = function() {
	    this.submit();
	};
	var hcANG = function() {
	    YAHOO.example.container.dialogAssignNewGroup.hide();
	};
	var hsxANG = function(o) {
		if (o.responseText.length > 0)
		    alert(o.responseText);

		refresh();
	};
	var hfANG = function(o) {
		alert("Submission failed: " + o.status);
	};

	// Instantiate the Dialog
	YAHOO.example.container.dialogAssignNewGroup = new YAHOO.widget.Dialog("dialogAssignNewGroup",
	    { width : "300px",
	      fixedcenter : true,
	      visible : false,
	      constraintoviewport : true,
	      buttons : [ { text:"Cancel", handler:hcANG }, { text:"Submit", handler:hsANG, isDefault:true } ]
	     } );

	// Validate the entries in the form to require that both first and last name are entered
	YAHOO.example.container.dialogAssignNewGroup.validate = function() {
		var data = this.getData();
		if (data.clientSelect == 0)
		{
		    alert("Please select a client.");
		    return false;
		}
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
	YAHOO.example.container.dialogAssignNewGroup.callback = { success: hsxANG, failure: hfANG };

	// Render the Dialog
	YAHOO.example.container.dialogAssignNewGroup.render();



	// Define various event handlers for Dialog
	var handleSubmit2 = function() {
		this.submit();
	};
	var handleSuccess2 = function(o) {
		if (o.responseText.length > 0)
		    alert(o.responseText);
		else
		{
			//processCommand('getPeopleByLastName', '');
			processCommand('getPersonDetails', '-1');
		}
	};
	var handleFailure2 = function(o) {
		alert("Submission failed: " + o.status);
	};

	// Instantiate the Dialog
	YAHOO.example.container.dialog2 = new YAHOO.widget.Dialog("dialogNewClient",
                                { width : "450px",
								  fixedcenter : false,
								  visible : false,
								  lazyload : true,
								  x : 150,
								  y : 150,
                                  constraintoviewport : true,
                                  buttons : [ { text:"Submit", handler:handleSubmit2, isDefault:true } ]
                                 } );

	// Validate the entries in the form to require that both first and last name are entered
	YAHOO.example.container.dialog2.validate = function() {
		var data = this.getData();
		if (data.firstname == 0)
		{
		    alert("Please select a first name.");
		    return false;
		}
		if (data.lastname == 0)
		{
		    alert("Please select a last name.");
		    return false;
		}

		return true;
	};

	// Wire up the success and failure handlers
	YAHOO.example.container.dialog2.callback = { success: handleSuccess2, failure: handleFailure2 };

	// Render the Dialog
	YAHOO.example.container.dialog2.render();


	function saveClick(p_oEvent) {
            document.forms[0].submit();
        }

	function newClick(p_oEvent) {
            document.newClientForm.firstname.value='';
	    document.newClientForm.lastname.value='';
	    document.newClientForm.relationshipSelect.value='1';
	    document.newClientForm.clientFileTypeSelect.value='-1';
	    YAHOO.example.container.dialog2.show();
        }

	function assignClick(p_oEvent) {
            YAHOO.example.container.dialogAssignNewGroup.show();
        }

	function nextFileNumber(p_oEvent) {
            processCommand('nextFileNumber');
        }

	var oPushButton1 = new YAHOO.widget.Button("sButton1a", { onclick: { fn: saveClick } });
	var oPushButton2 = new YAHOO.widget.Button("sButton1b", { onclick: { fn: newClick } });
	var oPushButton3 = new YAHOO.widget.Button("sButton1c", { onclick: { fn: assignClick } });
	var oPushButton4 = new YAHOO.widget.Button("sButton1d", { onclick: { fn: newClick } });
	var oPushButton5 = new YAHOO.widget.Button("sButton1e", { onclick: { fn: nextFileNumber } });

	function deletePlan(p_oEvent) {
	    if (confirm('Delete plan?  Are you sure?'))
		processCommand('deletePlanInstance', document.getElementById('planSelect').value);
        }

	function savePlan(p_oEvent) {
	    //alert('Save functionality not complete...');
            //document.forms[0].submit();
	    document.clientFinancialForm.submit();
        }

	function newEntry(p_oEvent) {
	    //alert("new enrty");
	    //YAHOO.example.container.dialogNewTransaction.show();
	    processCommand('getCheckoutDetails', -1);
        }

	function editSelected(p_oEvent) {
	    if (selected_order > 0)
		processCommand('getCheckoutDetails', selected_order);
        }

	var oPushButton6 = new YAHOO.widget.Button("deletePlanButton", { onclick: { fn: deletePlan } });
	var oPushButton7 = new YAHOO.widget.Button("savePlanButton", { onclick: { fn: savePlan } });


	//var oPushButton8 = new YAHOO.widget.Button("newEntryButton", { onclick: { fn: newEntry } });
	//var oPushButton10 = new YAHOO.widget.Button("editSelectedButton", { onclick: { fn: editSelected } });

	function addTrx(p_oEvent) {

	    //alert("addTrx invoked");

            //alert (document.forms[2].typeSelect[document.forms[2].typeSelect.selectedIndex].innerHTML);

            if (document.checkoutForm.typeSelect[document.checkoutForm.typeSelect.selectedIndex].innerHTML == 'Payment')
            {
                addCode('tblCredits');
                //updateAmount('tblCredits');
                eval("focusControl = document.getElementById('cr" + document.getElementById('tblCredits').tBodies[0].rows.length + "').focus();");
            }
            else
            {
                addCode('tblCharges');
                eval("focusControl = document.getElementById('ch" + document.getElementById('tblCharges').tBodies[0].rows.length + "').focus();");
            }
        }

	var oPushButton9 = new YAHOO.widget.Button("addButton", { onclick: { fn: addTrx } });


	var tabView = new YAHOO.widget.TabView('demo');











	var handleFailure = function(o) {
		alert("Submission failed: " + o.status);
	};
	var handleSubmit3 = function() {
		this.submit();
	};
	var handleCancel3 = function() {
		YAHOO.example.container.checkoutDialog.hide();
	};
	YAHOO.example.container.checkoutDialog = new YAHOO.widget.Dialog("checkoutDialog",
                        { width : "775px",
                          fixedcenter : true,
                          visible : false,
                          constraintoviewport : true,
                          buttons : [ { text:"Cancel", handler:handleCancel3 },
                                                  { text:"Checkout", handler:handleSubmit3, isDefault:true } ]
                         } );

	// Validate the entries in the form to require that both first and last name are entered
	YAHOO.example.container.checkoutDialog.validate = function() {
            if (numCharges == 0)
            {
                alert('No Charges Entered.');
                return false;
            }

		return true;
	};

	var handleCheckoutSuccess = function(o) {
		if (o.responseText.length > 0)
		{
		    alert(o.responseText);
		    YAHOO.example.container.checkoutDialog.show();
		}
		else
		    refresh();

		//var response = o.responseText;
		//document.getElementById("resp").innerHTML = response;

		//document.forms[0].appointmentSelect.value = -1;
		//document.forms[0].typeSelect.selectedIndex = 0;
		//document.forms[0].duration.value = "";

	};

	// Wire up the success and failure handlers
	YAHOO.example.container.checkoutDialog.callback = { success: handleCheckoutSuccess, failure: handleFailure };

	// Render the Dialog
	YAHOO.example.container.checkoutDialog.render();













	var subnt = function() {
		this.submit();
	};
	var sucnt = function(o) {
		if (o.responseText.length > 0)
		    alert(o.responseText);

		/*
		document.forms[0].appointmentSelect.value = -1;
		document.forms[0].typeSelect.selectedIndex = 0;
		document.forms[0].duration.value = "";
		*/

	};

	// Instantiate the Dialog
	YAHOO.example.container.dialogNewTransaction = new YAHOO.widget.Dialog("dialogNewTransaction",
			{ width : "475px",
			  fixedcenter : true,
			  visible : false,
			  constraintoviewport : true,
			  buttons : [ { text:"Submit", handler:subnt, isDefault:true } ]
			 } );

	// Validate the entries in the form to require that both first and last name are entered
	YAHOO.example.container.dialogNewTransaction.validate = function() {
		var data = this.getData();
		/*
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
		*/

		return true;
	};

	// Wire up the success and failure handlers
	YAHOO.example.container.dialogNewTransaction.callback = { success: sucnt, failure: handleFailure };

	// Render the Dialog
	YAHOO.example.container.dialogNewTransaction.render();



}

YAHOO.util.Event.onDOMReady(init);


</script>

    <script type="text/javascript">
    YAHOO.util.Event.onDOMReady(

        function() {

            // Create a panel Instance, from the 'resizablepanel' DIV standard module markup
            panel = new YAHOO.widget.Panel("resizablepanel", {
                draggable: true,
                width: "600px",
                height: "550px",
                autofillheight: "body", // default value, specified here to highlight its use in the example
                constraintoviewport:true,
				visible:false
            });
            panel.render();

            // Create Resize instance, binding it to the 'resizablepanel' DIV
            var resize = new YAHOO.util.Resize("resizablepanel", {
                handles: ["br"],
                autoRatio: false,
                minWidth: 300,
                minHeight: 100,
                status: false
            });

            // Setup startResize handler, to constrain the resize width/height
            // if the constraintoviewport configuration property is enabled.
            resize.on("startResize", function(args) {

    		    if (this.cfg.getProperty("constraintoviewport")) {
                    var D = YAHOO.util.Dom;

                    var clientRegion = D.getClientRegion();
                    var elRegion = D.getRegion(this.element);

                    resize.set("maxWidth", clientRegion.right - elRegion.left - YAHOO.widget.Overlay.VIEWPORT_OFFSET);
                    resize.set("maxHeight", clientRegion.bottom - elRegion.top - YAHOO.widget.Overlay.VIEWPORT_OFFSET);
	            } else {
                    resize.set("maxWidth", null);
                    resize.set("maxHeight", null);
	        	}

            }, panel, true);

            // Setup resize handler to update the Panel's 'height' configuration property
            // whenever the size of the 'resizablepanel' DIV changes.

            // Setting the height configuration property will result in the
            // body of the Panel being resized to fill the new height (based on the
            // autofillheight property introduced in 2.6.0) and the iframe shim and
            // shadow being resized also if required (for IE6 and IE7 quirks mode).
            resize.on("resize", function(args) {
                var panelHeight = args.height;
                this.cfg.setProperty("height", panelHeight + "px");
            }, panel, true);
        }
    );
    </script>




<script type="text/javascript">

    YAHOO.example.init = function () {

        // "contentready" event handler for the "submitbuttonsfrommarkup" <fieldset>

        YAHOO.util.Event.onContentReady("submitbuttonsfrommarkup2", function () {

            // Create a Button using an existing <input> element as a data source

            var oSubmitButton3 = new YAHOO.widget.Button("submitbutton3", {onclick: {fn: showAll}});

        });

    } ();

</script>

<%

if (settings.isSetupComplete())
{
%>
<script type="text/javascript">

(function() {
    var Dom = YAHOO.util.Dom,
        Event = YAHOO.util.Event;

    Event.onDOMReady(function() {
        var layout = new YAHOO.widget.Layout({
                minWidth: 750,
                units: [
		{ position: 'top', height: 21, resize: false, body: 'top1' },
                { position: 'center', body: 'center1', scroll: true, minWidth: 400 }
            ]
        });
        layout.render();



			
			if (document.getElementById('lastname').value.length > 0) {processCommand('getPeopleByLastName', document.getElementById('lastname').value.replace(/\'/g,'\\\''));}
			else if (document.getElementById('firstname').value.length > 0) {processCommand('getPeopleByFirstName', document.getElementById('firstname').value);}
			else if (document.getElementById('filenumber').value.length > 0) {processCommand('getPeopleByFileNumber', document.getElementById('filenumber').value);}
			


    });

})();
</script>
<%
}
%>

<%@ include file="..\channels\channel-head-javascript.jsp" %>

	<title>Valonyx</title>


</head>
<body class="yui-skin-sam" onload="javascript:initErrors();<%= (search_str.equals("") && vendor_search_str.equals("")) ? "" : "processCommand('getCheckoutCodesByDescInv','" + search_str + "|" + vendor_search_str + "');" %>">

<%

if (settings.isSetupComplete())
{
%>
<div id="top1">
    <%@ include file="channels/channel-schedule-menu.jsp" %>
</div>
<div id="center1" style="background-color: white;">
<%
}
%>


    <div id="content">


	<div class="content_TextBlock">
	    <p class="headline"><%= adminCompany.getLabel() %> - Clients - <span id="selectedPerson"><%= adminPerson.isNew() ? "No Client Selected" : adminPerson.getLabel() %></span></p>
	    <div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>
	</div>

	<div class="content_Administration">

	    <%@ include file="channels\channel-client-menu.jsp" %>

	    <div class="main">
<!--
		<p class="headlineA"></p>

		<div class="horizontalRule"><img src="images/trspacer.gif" width="1" height="1" alt="" /></div>
-->
		<struts-html:form action="/admin/clientProfile" >

			<input type="hidden" name="delete_id" value="" />

			<div id="resizablepanel">
				<div class="hd">Purchase Order</div>
				<div class="bd">
					<div style="width: 100%;">
						<div id="cos_div">
						</div>
					</div>
				</div>
				<div class="ft"></div>
			</div>

			<table border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td valign="top"  style="width: 40px;" >


		    <div class="adminItem2">
			    <div class="leftTM">Next Appointment</div>
			    <div class="right">
				    <span id="n_appt" class="adminInput"><%= adminPerson.getNextAppointmentString() %></span>
			    </div>
			    <div class="end"></div>
		    </div>
		    <div class="adminItem2">
			    <div class="leftTM">Last Appointment</div>
			    <div class="right">
				    <span id="l_appt" class="adminInput"><%= adminPerson.getLastAppointmentString() %></span>
			    </div>
			    <div class="end"></div>
		    </div>
		    <div class="adminItem2">
			    <div class="leftTM"></div>
			    <div class="right"></div>
			    <div class="end"></div>
		    </div>
		    <div class="adminItem2">
			    <div class="leftTM">File Number</div>
			    <div class="right">
				    <input type="hidden" name="person_id" value="<%= adminPerson.getId() %>" />
					<input type="hidden" name="hidden_filenumber">
				    <input value="<%= adminPerson.getFileNumberString() %>" name="file_number" maxlength="100" class="adminInput" style="width: 76px;"<%= is_prospect ? " disabled" : ""  %> /><input type="checkbox" name="c_prospect"<%= is_prospect ? " checked" : ""  %> onclick="if (this.checked) { document.forms[0].hidden_filenumber.value=document.forms[0].file_number.value; document.forms[0].file_number.value=''; document.forms[0].file_number.disabled=true; } else {document.forms[0].file_number.disabled=false; document.forms[0].file_number.value=document.forms[0].hidden_filenumber.value;}" /><strong>Prospect</strong>
					<br /><input type="button" id="sButton1e" name="sButton1e" value="Get Next File Number">
			    </div>
			    <div class="end"></div>
		    </div>
		    <div class="adminItem2">
			    <div class="leftTM">Prefix</div>
			    <div class="right">
				    <input value="<%= adminPerson.getPrefixString() %>" name="prefix" maxlength="100" class="adminInput" style="width: 76px;" />
			    </div>
			    <div class="end"></div>
		    </div>
		    <div class="adminItem2">
			    <div class="leftTM">First Name</div>
			    <div class="right">
				    <input value="<%= adminPerson.getFirstNameString() %>" name="firstname" maxlength="100" class="adminInput" style="width: 136px;" />
				    <input value="<%= adminPerson.getMiddleInitialString() %>" name="middle" maxlength="1" class="adminInput" style="width: 26px;" />
			    </div>
			    <div class="end"></div>
		    </div>
		    <div class="adminItem2">
			    <div class="leftTM">Last Name</div>
			    <div class="right">
				    <input value="<%= adminPerson.getLastNameString() %>" name="lastname" maxlength="100" class="adminInput" style="width: 166px;" />
			    </div>
			    <div class="end"></div>
		    </div>
		    <div class="adminItem2">
			    <div class="leftTM">Suffix</div>
			    <div class="right">
				    <input value="<%= adminPerson.getSuffixString() %>" name="suffix" maxlength="100" class="adminInput" style="width: 76px;" />
			    </div>
			    <div class="end"></div>
		    </div>

		    <div class="adminItem2">
			    <div class="leftTM">Address 1</div>
			    <div class="right">
				    <input value="<%= adminPerson.getHomeAddress1String() %>" name="address1" maxlength="250" class="adminInput" style="width: 250px;" />
			    </div>
			    <div class="end"></div>
		    </div>
		    <div class="adminItem2">
			    <div class="leftTM">Address 2</div>
			    <div class="right">
				    <input value="<%= adminPerson.getHomeAddress2String() %>" name="address2" maxlength="250" class="adminInput" style="width: 250px;" />
			    </div>
			    <div class="end"></div>
		    </div>
		    <div class="adminItem2">
			    <div class="leftTM">City / State / Zip</div>
			    <div class="right">
				    <input value="<%= adminPerson.getHomeAddressCityString() %>" name="city" maxlength="100" class="adminInput" style="width: 136px;" />
				    <input value="<%= adminPerson.getHomeAddressStateString() %>" name="state" maxlength="2" class="adminInput" style="width: 30px;" />
				    <input value="<%= adminPerson.getHomeAddressZipString() %>" name="zipcode" maxlength="10" class="adminInput" style="width: 64px;" />
			    </div>
			    <div class="end"></div>
		    </div>
		    <div class="adminItem2">
			    <div class="leftTM">Client Type</div>
			    <div class="right">
				<select name="client_type" class="adminInput" style="width: 250px;">
<%
Iterator itr = PersonTitleBean.getPersonTitles(adminCompany).iterator();
if (itr.hasNext())
{
    while (itr.hasNext())
    {
	PersonTitleBean client_type = (PersonTitleBean)itr.next();
%>
				    <option value="<%= client_type.getValue() %>"<%= adminPerson.getTitleString().equals(client_type.getLabel()) ? " selected" : "" %>><%= client_type.getLabel() %></option>
<%
    }
}
else
{
%>
				    <option value="-1">-- NO CLIENT TYPES FOUND --</option>
<%
}
%>
%>
				</select>
			    </div>
			    <div class="end"></div>
		    </div>
		    <div class="adminItem2">
			    <div class="leftTM">File Type</div>
			    <div class="right">
				<select name="file_type" class="adminInput" style="width: 250px;">
<%
itr = GroupUnderCareMemberTypeBean.getMemberTypes(adminCompany).iterator();
if (itr.hasNext())
{
%>
				    <option value="-1">-- SELECT A FILE TYPE --</option>
<%
    while (itr.hasNext())
    {
		GroupUnderCareMemberTypeBean file_type = (GroupUnderCareMemberTypeBean)itr.next();
%>
				    <option value="<%= file_type.getValue() %>"<%= adminPerson.getGroupUnderCareMemberTypeValue().equals(file_type.getValue()) ? " selected" : "" %>><%= file_type.getLabel() %></option>
<%
    }
}
else
{
%>
				    <option value="-1">-- NO FILE TYPES FOUND --</option>
<%
}
%>
				</select>
			    </div>
			    <div class="end"></div>
		    </div>

		    <div class="adminItem2">
			    <div class="leftTM">Group Under Care</div>
			    <div class="right">
				    <select name="groupSelect" size="5" id="groupSelect" class="adminInput" style="width: 250px;">
<%
try
{
    group_under_care = adminPerson.getGroupUnderCare();
    Iterator group_under_care_members_itr = group_under_care.getMembers().iterator();
    if (group_under_care_members_itr.hasNext())
    {
	while (group_under_care_members_itr.hasNext())
	{
	    //UKOnlinePersonBean member = (UKOnlinePersonBean)group_under_care_members_itr.next();
	    GroupUnderCareMember member = (GroupUnderCareMember)group_under_care_members_itr.next();
	    UKOnlinePersonBean person_member = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(member.getPersonId());
%>
					    <option value="<%= person_member.getValue() %>"><%= person_member.getLabel() + " (" + GroupUnderCareBean.getRelationshipString(member.getRelationshipToPrimaryClient()) + ")" %></option>
<%
	}
    }
    else
    {
%>
					    <option value="-1">-- NO GROUP MEMBERS FOUND --</option>
<%
    }
}
catch (Exception x)
{
    x.printStackTrace();
%>
					    <option value="-1">-- GROUP NOT FOUND --</option>
<%
}
%>
				    </select>
			    </div>
			    <div class="end"></div>
		    </div>
		    <div class="adminItem2">
			    <div class="leftTM">&nbsp;</div>
			    <div class="right">
				    <input type="button" id="sButton1c" name="sButton1c" value="Assign to new Group Under Care">
			    </div>
			    <div class="end"></div>
		    </div>


				</td>
				<td><img src="../images/spacer.gif" width="5px;"></td>
				<td valign="top" style="width: 45%;" >

		    <div class="adminItem2">
			    <div class="leftTM">PoS ID</div>
			    <div class="right">
				    <input value="<%= adminPerson.getEmployeeNumberString() %>" name="pos_id" size="7" class="adminInput" style="width: 166px;" disabled />
			    </div>
			    <div class="end"></div>
		    </div>
		    <div class="adminItem2">
			    <div class="leftTM">QB ID</div>
			    <div class="right">
				    <input value="<%= adminPerson.getEmail2String() %>" name="qb_id" size="7" class="adminInput" style="width: 166px;" disabled />
			    </div>
			    <div class="end"></div>
		    </div>
		    <div class="adminItem2">
			    <div class="leftTM">Phone</div>
			    <div class="right">
				    <input value="<%= adminPerson.getHomePhoneNumberString() %>" name="phone" size="7" maxlength="14" class="adminInput" style="width: 120px;" />
			    </div>
			    <div class="end"></div>
		    </div>
		    <div class="adminItem2">
			    <div class="leftTM">Cell</div>
			    <div class="right">
				    <input value="<%= adminPerson.getCellPhoneNumberString() %>" name="cell" size="7" maxlength="14" class="adminInput" style="width: 120px;" />
			    </div>
			    <div class="end"></div>
		    </div>
		    <div class="adminItem2">
			    <div class="leftTM">Email</div>
			    <div class="right">
				    <input value="<%= adminPerson.getEmailString() %>" name="email" size="7" maxlength="250" class="adminInput" style="width: 225px;" />
			    </div>
			    <div class="end"></div>
		    </div>
		    <div class="adminItem2">
			    <div class="leftTM">Password</div>
			    <div class="right">
				    <input value="<%= adminPerson.getPasswordString() %>" name="password" size="11" maxlength="250" class="adminInput" style="width: 225px;" />
			    </div>
			    <div class="end"></div>
		    </div>
		    <div class="adminItem2">
			    <div class="leftTM">SSN</div>
			    <div class="right">
				    <input value="<%= adminPerson.getSSNString() %>" name="ssn" size="11" maxlength="250" class="adminInput" style="width: 100px;" />
			    </div>
			    <div class="end"></div>
		    </div>

		    <div class="adminItem2">
			    <div class="leftTM">Date of Birth</div>
			    <div class="right">
				    <input value="<%= adminPerson.getBirthDateString() %>" name="dob" size="7" maxlength="12" class="adminInput" style="width: 80px;" />
			    </div>
			    <div class="end"></div>
		    </div>
		    <div class="adminItem2">
			    <div class="leftTM"></div>
			    <div class="right">
				    <input type="radio" name="gender" value="Male"<%= adminPerson.isMale() ? " checked" : "" %> /><strong>Male</strong>
				    <input type="radio" name="gender" value="Female"<%= adminPerson.isFemale() ? " checked" : "" %> /><strong>Female</strong>
			    </div>
			    <div class="end"></div>
		    </div>
		    <div class="adminItem2">
			    <div class="leftTM"></div>
			    <div class="right">
				    <input type="checkbox" name="deceased"<%= adminPerson.isDeceased() ? " checked" : "" %> /><strong>Deceased</strong>
			    </div>
			    <div class="end"></div>
		    </div>
		    <div class="adminItem2">
			    <div class="leftTM"></div>
			    <div class="right">&nbsp;</div>
			    <div class="end"></div>
		    </div>
		    <div class="adminItem2">
			    <div class="leftTM">Referral From</div>
			    <div class="right">
			<table border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td><select style="width: 250px;" name="marketingProfileSelect" class="adminInput">
				<option value="0">-- SELECT A MARKETING CAMPAIGN --</option>
<%
int selected_marketing_plan_id = 0;
int selected_referred_by_client_id = 0;
String selected_referred_by_client_label = "-- SEARCH FOR A CLIENT --";
try
{
	ReferralSource referral_source = ReferralSource.getReferralSource(adminPerson);
	if (referral_source.isMarketingPlanReferral())
		selected_marketing_plan_id = referral_source.getMarketingPlan().getId();
	else if (referral_source.isClientReferral())
	{
		UKOnlinePersonBean selected_referred_by_client = referral_source.getReferredByClient();
		selected_referred_by_client_id = selected_referred_by_client.getId();
		selected_referred_by_client_label = selected_referred_by_client.getLabel();
	}
}
catch (ObjectNotFoundException x)
{
}
Iterator mp_itrx = MarketingPlan.getMarketingPlans(adminCompany, true).iterator();
while (mp_itrx.hasNext())
{
	MarketingPlan marketing_plan = (MarketingPlan)mp_itrx.next();
%>
				<option  value="<%= marketing_plan.getValue() %>"<%= (selected_marketing_plan_id == marketing_plan.getId()) ? " selected" : "" %>><%= marketing_plan.getLabel() %></option>
<%
}
%>
					</select></td>
				</tr>
			</table></div>
			    <div class="end"></div>
		    </div>
		    <div class="adminItem2">
			    <div class="leftTM">or</div>
			    <div class="right">
			<table border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td>
	<input id="lnnc3" type="textbox" onkeyup="if (document.getElementById('lnnc3').value.length > 0) {show_referral_names2 = true; processCommand('getPeopleByLastName', document.getElementById('lnnc3').value);}" name="lnnc3" />
					</td>
				</tr>
			</table></div>
			    <div class="end"></div>
		    </div>
		    <div class="adminItem2">
			    <div class="leftTM">Client Referral</div>
			    <div class="right">
			<table border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td><select multiple style="width: 250px;" name="referralProfileSelect" class="adminInput">
<%
if (selected_referred_by_client_id == 0)
{
%>
				<option value="0">-- SEARCH FOR A CLIENT --</option>
<%
}
else
{
%>
				<option value="<%= selected_referred_by_client_id %>" selected><%= selected_referred_by_client_label %></option>
<%
}
%>
					</select></td>
				</tr>
			</table></div>
			    <div class="end"></div>
		    </div>



		    <div class="adminItem2">
			    <div class="leftTM">&nbsp;</div>
			    <div class="right">&nbsp;</div>
			    <div class="end"></div>
		    </div>
		    <div class="adminItem2">
			    <div class="leftTM">&nbsp;</div>
			    <div class="right">
				    <input type="button" id="sButton1a" name="sButton1a" value="Save Client">
			    </div>
			    <div class="end"></div>
		    </div>


				</td>
			</tr>
			</table>






		</struts-html:form>

	    </div>

	    <div class="end"></div>
	</div>





	<div class="content_TextBlock">
	    <div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>
	</div>

    </div>


<%
if (settings.isSetupComplete())
{
%>
</div>
<%
}
%>

<%@ include file="channels/channel-new-client.jsp" %>

<div id="dialogAssignNewGroup">
<div class="hd"><h2><span id="apptFormLabel">Assign to new Group Under Care</span></h2></div>
<div class="bd" id="bd-r">
<form name="assignGroupUnderCareForm" method="POST" action="<%= CUBean.getProperty("cu.formSubmitPath") %>/assignGroupUnderCare.x"  >

	<input type="hidden" name="clientSelect" value="-1">

	<div class="clear"></div>

	<strong>Search Last Name:</strong>
	<div class="clear"></div>
	<input id="lnaguc" type="textbox" onkeyup="if (document.getElementById('lnaguc').value.length > 0) {show_assign_names = true; processCommand('getPeopleByLastName', document.getElementById('lnaguc').value);}" name="lnaguc" />

	<div class="clear"></div>

	<label for="groupSelect"><strong>Group:</strong></label><br />
	<select multiple name="groupSelect">
	    <option value="-1">-- SEARCH FOR A GROUP --</option>
	</select>
	<div class="clear"></div>


	<strong>Relationship:</strong><br />
	<select name="relationshipSelect">
	    <option value="<%= GroupUnderCareBean.RELATIONSHIP_SELF_TYPE %>">Self</option>
	    <option value="<%= GroupUnderCareBean.RELATIONSHIP_SPOUSE_TYPE %>">Spouse</option>
	    <option value="<%= GroupUnderCareBean.RELATIONSHIP_CHILD_TYPE %>">Child</option>
	    <option value="<%= GroupUnderCareBean.RELATIONSHIP_GUARDIAN_TYPE %>">Guardian</option>
	    <option value="<%= GroupUnderCareBean.RELATIONSHIP_PARENT_TYPE %>">Parent</option>
	    <option value="<%= GroupUnderCareBean.RELATIONSHIP_PARTNER_TYPE %>">Partner</option>
	    <option value="<%= GroupUnderCareBean.RELATIONSHIP_OTHER_TYPE %>">Other</option>
	</select>

	<div class="clear"></div>

</form>
</div>
</div>

</body>
</html>
