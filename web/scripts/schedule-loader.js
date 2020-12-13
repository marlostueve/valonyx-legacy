		
    var appointmentArray = new Array();
    var scheduleArray = new Array();
    var idArray = new Array();
    var scheduleIndex = 0;

    var edit = 0;
    var editC = 0;
    var editC2 = 0;
    var editOt = 0;
    var editA = 0;
    var editA2 = 0;
    var last_key_ovr;
    
    var numCharges = 0;
    var numCredits = 0;
    
    var previous_balance = 0;
    var total_charges = 0;
    var total_credits = 0;
    var client_owes = 0;
    
    var mTimer;

    var forceRefresh = true;
    var focusControl;
    var block = false;
    
    var plan_id = 0;

    function refresh()
    {
        if (!block)
        {
            block = true;
            if (forceRefresh)
            {
                processCommand('forceRefresh');
                //forceRefresh = false;
            }
            else
                processCommand('refresh');
        }
        
        mTimer = setTimeout('refresh();',5000);
    }

    function refreshPractitioner()
    {
        if (!block)
        {
            block = true;
            if (forceRefresh)
            {
                processCommand('forceRefresh','p');
                //forceRefresh = false;
            }
            else
                processCommand('refresh','p');
        }

        mTimer = setTimeout('refreshPractitioner();',5000);
    }
    
    function showPeople(xml_str)
    {
        if (show_group_names)
            document.forms['newClientForm'].groupSelect.options.length = 0;
        else
            document.forms['newApptForm'].clientSelect.options.length = 0;
        
        var index = 0;
        while (xml_str.getElementsByTagName("person")[index] != null)
        {
            key = xml_str.getElementsByTagName("person")[index].childNodes[0].childNodes[0].nodeValue;
            value = xml_str.getElementsByTagName("person")[index].childNodes[1].childNodes[0].nodeValue;
            eval("var personArray = " + value);

            if (show_group_names)
                document.forms['newClientForm'].groupSelect.options[index] = new Option(personArray["label"],key);
            else
                document.forms['newApptForm'].clientSelect.options[index] = new Option(personArray["label"],key);

            index++;
        }
        
        show_group_names = false;
    }
    
    function showClientInfo(cid)
    {
	if (cid)
	    processCommand('getPersonStats', cid);
    }
    
    function showCheckout(xml_str)
    {
	//alert("showCheckout() invoked");
	
	//var key = xml_str.childNodes[0].childNodes[0].nodeValue;
        //var value = xml_str.childNodes[1].childNodes[0].nodeValue;
	
	var key = xml_str.getElementsByTagName("person")[0].childNodes[0].childNodes[0].nodeValue;
	var value = xml_str.getElementsByTagName("person")[0].childNodes[1].childNodes[0].nodeValue;
        
	eval("var arr = " + value);
	
	
        previous_balance = parseFloat(xml_str.getElementsByTagName("previous")[0].childNodes[0].nodeValue);
        total_charges = 0;
        total_credits = 0;
        client_owes = previous_balance;
        
        document.getElementById('previousBalance').firstChild.nodeValue = previous_balance;
        document.getElementById('owes').firstChild.nodeValue = previous_balance;
	
	document.forms[2].gucSelect.options.length = 0;
        var index = 1;
        if (xml_str.getElementsByTagName("person")[index] != null)
        {
	    while (xml_str.getElementsByTagName("person")[index] != null)
            {
                key = xml_str.getElementsByTagName("person")[index].childNodes[0].childNodes[0].nodeValue;
                value = xml_str.getElementsByTagName("person")[index].childNodes[1].childNodes[0].nodeValue;
                eval("arx = " + value);

                document.forms[2].gucSelect.options[index - 1] = new Option(arx["label"],key);

                index++;
            }
            
            document.getElementById('checkoutFormLabel').firstChild.nodeValue = arr["label"] + " (" + arr["fts"] + ")";
        }
        else
            document.getElementById('checkoutFormLabel').firstChild.nodeValue = arr["label"];
            
	
        deleteAllRows(document.getElementById('tblCharges'));
	deleteAllRows(document.getElementById('tblCredits'));
	deleteAllRows(document.getElementById('tblPrevious'));
	
	addRowToTable('tblCharges', 'No Charges Entered');
	addRowToTable('tblCredits', 'No Payments Entered');
	
	numCharges = 0;
	numCredits = 0;
	
	index = 0;
	if (xml_str.getElementsByTagName("orderline")[index] != null)
	{
	    while (xml_str.getElementsByTagName("orderline")[index] != null)
	    {
		key = xml_str.getElementsByTagName("orderline")[index].childNodes[0].childNodes[0].nodeValue;
		value = xml_str.getElementsByTagName("orderline")[index].childNodes[1].childNodes[0].nodeValue;
		eval("arr = " + value);

		//alert("value >" + value);

		addRowToTableNoEdit('tblPrevious', arr["desc"], arr["date"], arr["id"], arr["code"], arr["amount"]);

		//document.forms[2].gucSelect.options[index - 1] = new Option(arr["label"],key);

		index++;
	    }
	}
	else
	    addRowToTableNoEdit('tblPrevious', 'No Open Orders Found');
        
        document.forms[2].numPrevious.value=index;
        
        
        if (xml_str.getElementsByTagName("payment-plan-instance")[0] != null)
        {
	    key = xml_str.getElementsByTagName("payment-plan-instance")[0].childNodes[0].childNodes[0].nodeValue;
            value = xml_str.getElementsByTagName("payment-plan-instance")[0].childNodes[1].childNodes[0].nodeValue;
            eval("arr = " + value);
            
            document.getElementById('plan').firstChild.nodeValue = arr["label"];
            document.getElementById('planStart').firstChild.nodeValue = arr["start"];
            document.getElementById('prepaidVisits').firstChild.nodeValue = arr["prepaid_visits"];
            document.getElementById('visitsUsed').firstChild.nodeValue = arr["visits_used"];
            document.getElementById('visitsRemaining').firstChild.nodeValue = arr["visits_remaining"];
            document.getElementById('perVisitCharge').firstChild.nodeValue = arr["visit_charge"];
            document.getElementById('amountPaid').firstChild.nodeValue = arr["amount_paid"];
            document.getElementById('escrowAmount').firstChild.nodeValue = arr["escrow"];
            document.getElementById('dropPlanCharge').firstChild.nodeValue = arr["drop_plan_charge"];
            plan_id = arr["practice_area_id"];
        }
        else
        {
            document.getElementById('plan').firstChild.nodeValue = 'Payment Plan Not Found';
            document.getElementById('planStart').firstChild.nodeValue = '';
            document.getElementById('prepaidVisits').firstChild.nodeValue = '';
            document.getElementById('visitsUsed').firstChild.nodeValue = '';
            document.getElementById('visitsRemaining').firstChild.nodeValue = '';
            document.getElementById('perVisitCharge').firstChild.nodeValue = '';
            document.getElementById('amountPaid').firstChild.nodeValue = '';
            document.getElementById('escrowAmount').firstChild.nodeValue = '';
            document.getElementById('dropPlanCharge').firstChild.nodeValue = '';
            plan_id = 0;
        }
        
        document.getElementById('charges').firstChild.nodeValue = '0.00';
        document.getElementById('credits').firstChild.nodeValue = '0.00';
        
	//addPreviousCharges();
        //addInitialCharges();
	selectType();
	YAHOO.example.container.checkoutDialog.show();
    }
        
    function showPerson(xml_str)
    {
        var key = xml_str.childNodes[0].childNodes[0].nodeValue;
        var value = xml_str.childNodes[1].childNodes[0].nodeValue;

        eval("var personArray = " + value);
	
	/*
        document.getElementById('stats_label').firstChild.nodeValue = personArray["label"];
        document.getElementById('stats_label_01').firstChild.nodeValue = personArray["next"];
        document.getElementById('stats_label_02').firstChild.nodeValue = personArray["addr"];
	*/
	
	document.getElementById('clientInfoFormLabel').firstChild.nodeValue = personArray["label"];
	document.clientInfoForm.cid.value = personArray["id"];
	document.getElementById('clientInfoFormNext').firstChild.nodeValue = personArray["next"];
	document.getElementById('clientInfoFormLast').firstChild.nodeValue = personArray["last"];
	document.getElementById('clientInfoFormBalance').firstChild.nodeValue = personArray["bal"];
	document.getElementById('clientInfoFormAddr').firstChild.nodeValue = personArray["addr"];
	document.getElementById('clientInfoFormPhone').firstChild.nodeValue = personArray["ph"];
	document.getElementById('clientInfoFormEmail').firstChild.nodeValue = personArray["email"];
	
	if (personArray["status"] == '1')
	    document.clientInfoForm.statusInput[0].checked = true;
	else if (personArray["status"] == '2')
	    document.clientInfoForm.statusInput[1].checked = true;
	else if (personArray["status"] == '3')
	    document.clientInfoForm.statusInput[2].checked = true;
	
	for (var i = 0; document.forms[1].elements['contactInput'][i]; i++)
	    document.forms[1].elements['contactInput'][i].checked = false;
	document.forms[1].elements['otherInput'].value = '';
	document.forms[1].elements['todoInput'][0].checked = false;
	document.forms[1].elements['todoInput'][1].checked = false;
	document.forms[1].elements['callOnInput'].value = '';
	document.forms[1].elements['eList1'].value = '1';
	document.forms[1].elements['eList2'].value = '1';
	document.forms[1].elements['paList'].value = '0';
	document.forms[1].elements['ppList'].value = '0';
	
	//alert(personArray["status"]);
	
	if (xml_str.childNodes[2].childNodes[0])
	{
	    var element_obj = document.clientInfoForm.eventList;
	    element_obj.options.length = 0;
            var index = 0;
	    for (i = 0; xml_str.childNodes[2].childNodes[i]; i++)
	    {
		var contact_key = xml_str.childNodes[2].childNodes[i].childNodes[0].text;
		var contact_value = xml_str.childNodes[2].childNodes[i].childNodes[1].text;
		
		eval("var contactArray = " + contact_value);
		element_obj.options[index] = new Option(contactArray["label"],contact_key);
                index++;
	    }
	}
	else
	    document.clientInfoForm.eventList.options.length = 0;
	
	if (xml_str.childNodes[3].childNodes[0])
	{
	    element_obj = document.clientInfoForm.toDoList;
	    element_obj.options.length = 0;
            index = 0;
	    for (i = 0; xml_str.childNodes[3].childNodes[i]; i++)
	    {
		var toDo_key = xml_str.childNodes[3].childNodes[i].childNodes[0].text;
		var toDo_value = xml_str.childNodes[3].childNodes[i].childNodes[1].text;
		
		eval("var toDoArray = " + toDo_value);
		element_obj.options[index] = new Option(toDoArray["label"],toDo_key);
                index++;
	    }
	}
	else
	    document.clientInfoForm.toDoList.options.length = 0;
	
	if (xml_str.childNodes[4].childNodes[0])
	{
	    element_obj = document.clientInfoForm.foqList;
	    element_obj.options.length = 0;
            index = 0;
	    for (i = 0; xml_str.childNodes[4].childNodes[i]; i++)
	    {
		var foq_key = xml_str.childNodes[4].childNodes[i].childNodes[0].text;
		var foq_value = xml_str.childNodes[4].childNodes[i].childNodes[1].text;
		
		eval("var foqArray = " + foq_value);
		element_obj.options[index] = new Option(foqArray["label"],foq_key);
                index++;
	    }
	}
	else
	    document.clientInfoForm.foqList.options.length = 0;
	
	YAHOO.example.container.dialog2.show();

	/*
        YAHOO.example.calendar.cal1.addRenderer(personArray["as"], YAHOO.example.calendar.cal1.renderCellStyleHighlight1);
        YAHOO.example.calendar.cal1.render();
	*/
    }
    
    function showAppointments(element_obj,xml_str)
    {
        var index = 0;
        var changed = false;

        if (element_obj.length != xml_str.getElementsByTagName("appt").length)
            changed = true;
        else
        {
            while (element_obj.options[index])
            {
                value = xml_str.getElementsByTagName("appt")[index].childNodes[1].childNodes[0].nodeValue;
                eval("var apptArray = " + value);
                if (element_obj.options[index].value != apptArray["cid"])
                {
                    changed = true;
                    break;
                }

                index++;
            }
        }

        if (changed)
        {
            element_obj.options.length = 0;
            index = 0;
            while (xml_str.getElementsByTagName("appt")[index] != null)
            {
                key = xml_str.getElementsByTagName("appt")[index].childNodes[0].childNodes[0].nodeValue;
                value = xml_str.getElementsByTagName("appt")[index].childNodes[1].childNodes[0].nodeValue;
                eval("var apptArray = " + value);
                //element_obj.options[index] = new Option(apptArray["label"] + " (" + practitionerArray[key.split("|")[1]]["label"] + ")",apptArray["cid"]);
                element_obj.options[index] = new Option(apptArray["label"],apptArray["cid"]);
                index++;
            }
        }
    }
    
    function buildScheduleArray(xml_str, refresh)
    {
	//alert("buildScheduleArray invoked");
        var index = 0;
        var key;
        var value;
        var focus_element;
        
        if (xml_str.getElementsByTagName("appt")[index] == null)
        {
            //alert("buildScheduleArray invoked with null appt array - bailing");
            return;
        }
	
	if (!refresh)
        {
            //alert(refresh + " .. invoking clearSchedule() >" + xml_str.getElementsByTagName("appt")[index]);
	    clearSchedule();
        }
	
        while (xml_str.getElementsByTagName("appt")[index] != null)
        {
            key = xml_str.getElementsByTagName("appt")[index].childNodes[0].childNodes[0].nodeValue;
            value = xml_str.getElementsByTagName("appt")[index].childNodes[1].childNodes[0].nodeValue;

              //alert(key);
               //alert(value);

            var hl_appt = false;
            eval("var apptArray = " + value);
            
            if (apptArray)
            {
                var apptDate = apptArray["date"];

                if (!appointmentArray[apptDate])
                    appointmentArray[apptDate] = new Array();
                if (!appointmentArray[apptDate][key])
                    appointmentArray[apptDate][key] = new Array();

                var exists = false;
                for (var i = 0; i < appointmentArray[apptDate][key].length; i++)
                {
                    if (apptArray["id"] == appointmentArray[apptDate][key][i]["id"])
                    {
                        exists = true;
                        eval("appointmentArray[apptDate][key][i] = " + value);  // replace current contents
                        break;
                    }
                }
                if (!exists)
                    eval("appointmentArray[apptDate][key][" + appointmentArray[apptDate][key].length + "] = " + value);

                //alert(appointmentArray["05/21/08"][key][0]["label"]);

                var appt_html = "&nbsp;";
                var appt_id;
                for (i = 0; i < appointmentArray[apptDate][key].length; i++)
                {
                    var appt_cm = appointmentArray[apptDate][key][i]["cm"];
                    if (appt_cm == 'nva')
                        hl_appt = true;
                    var appt_label = appointmentArray[apptDate][key][i]["label"];
                    var appt_cid = appointmentArray[apptDate][key][i]["cid"];
                    appt_id = parseInt(appointmentArray[apptDate][key][i]["id"]);
                    var appt_image = "default.gif";
                    var appt_state = parseInt(appointmentArray[apptDate][key][i]["state"]);
                    if (appt_state > 1)
                    {
                        if (appt_state == 2)
                            appt_image = "reschedule.gif";
                        else if (appt_state == 3)
                            appt_image = "late.gif";
                        else if (appt_state == 4)
                            appt_image = "checked-in.gif";
                        else if (appt_state == 5)
                            appt_image = "checked-out.gif";
                        else if (appt_state == 6)
                            appt_image = "cancel.gif";
                        else if (appt_state == 7)
                            appt_image = "no-show.gif";
                    }
                    
                    //if (hl_appt)
                    //    appt_image = "spacer.gif";

                    var ahs = "";
                    if (appt_cid == '0') // offtime
                    {
                        if (show_off_time && (appt_label != 'Practitioner Off-Time'))
                            ahs = "<img src=\"../images/" + appt_image + "\" border=\"0\" width=\"14\" height=\"14\" alt=\"" + appt_label + "\" onclick=\"editOtx('" + key + "');\" /><a onmouseout=\"clientOut();\" onmouseover=\"clientOvr(" + appt_cid + "," + appt_id + ");\" onclick=\"editClient(" + appt_cid + ");\" title=\"" + appt_cm + "\" href=\"clients.jsp\">" + appt_label + "</a>";
                    }
                    else
                    {
                        if (show_appts)
                            ahs = "<img src=\"../images/" + appt_image + "\" border=\"0\" width=\"14\" height=\"14\" alt=\"" + appt_label + "\" onclick=\"editAppt(" + appt_cid + ");\" /><a onmouseout=\"clientOut();\" onmouseover=\"clientOvr(" + appt_cid + "," + appt_id + ");\" onclick=\"editClient(" + appt_cid + ");\" title=\"" + appt_cm + "\" href=\"clients.jsp\">" + appt_label + "</a>";
                    }
                    if (appt_html == "&nbsp;")
                        appt_html = ahs;
                    else
                        appt_html += "<br>" + ahs;

                }
                
                var hide_border = (appt_label != 'Practitioner Off-Time');

                var element_obj = document.getElementById(key);
                if (!element_obj)
                    alert("unable to display appointment >" + key);
                else
                {
                    if ((appt_html != '') && !hl_appt)
                        element_obj.innerHTML = appt_html;
                    
                    var bg_color = typeArray[apptArray["type"]]["bg"];

                    if (hide_border)
                        element_obj.style.borderColor = bg_color;
                    
                    //alert(key + "|" + appt_cid);
                    idArray[key + "|" + appt_cid] = value;

                    scheduleArray[scheduleIndex] = key;
                    scheduleIndex++;
                    var t_array = key.split("|")[0].split(" ");
                    var ampm = t_array[1];
                    var hour = parseInt(t_array[0].split(":")[0]);
                    var minute = parseInt(t_array[0].split(":")[1]);
                    var duration = parseInt(apptArray["duration"]);
                    
                    if (hl_appt)
                    {
                        var weekday = new Array("Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday");
                        var monthname = new Array("January","February","March","April","May","June","July","August","September","October","November","December");
                        var adj_date = new Date(apptDate);
                        adj_date.setYear(2000 + adj_date.getYear());
                        document.getElementById('appt_date').value = weekday[adj_date.getDay()] + ", " + monthname[adj_date.getMonth()] + " " + adj_date.getDate() + ", " + adj_date.getYear();
            
                        document.forms[0].hr.value = hour;
                        if (minute == '0')
                            document.forms[0].mn.value = '00';
                        else if (minute == '5')
                            document.forms[0].mn.value = '05';
                        else
                            document.forms[0].mn.value = minute;
                        document.forms[0].ampm.value = ampm;
                        
                        element_obj.style.borderTopStyle = 'dashed';
                        element_obj.style.borderLeftStyle = 'dotted';
                        element_obj.style.borderRightStyle = 'dotted';
                        element_obj.style.borderColor = 'yellow';
                        element_obj.style.borderWidth = 'thick';
                        element_obj.style.backgroundColor = 'white';
                        
                        focus_element = element_obj;
                    }
                    else
                        element_obj.style.backgroundColor = bg_color;
                    
                    element_obj.style.color = typeArray[apptArray["type"]]["txt"];
                    
                    duration -= 5;
                    while (duration > 0)
                    {
                        minute += 5;
                        if (minute == 60)
                        {
                            minute = 0;
                            hour++;
                            if (hour == 12)
                                ampm = "PM";
                            else if (hour == 13)
                                hour = 1;
                        }
                        duration -= 5;
                        var next_key;
                        if ((minute == 0) || (minute == 5))
                            next_key = hour + ":0" + minute + " " + ampm + "|" + key.split("|")[1];
                        else
                            next_key = hour + ":" + minute + " " + ampm + "|" + key.split("|")[1];

                        element_obj = document.getElementById(next_key);
                        
                        if (hide_border)
                            element_obj.style.borderColor = bg_color;
                        
                        if (hl_appt)
                        {
                            element_obj.style.backgroundColor = 'white';
                            element_obj.style.borderTopStyle = 'none';
                            element_obj.style.borderBottomStyle = 'none';
                            element_obj.style.borderLeftStyle = 'dotted';
                            element_obj.style.borderRightStyle = 'dotted';
                            element_obj.style.borderColor = 'yellow';
                            element_obj.style.borderWidth = 'thick';
                        }
                        else
                            element_obj.style.backgroundColor = bg_color;
                        
                        scheduleArray[scheduleIndex] = next_key;
                        scheduleIndex++;
                    }
                    
                    if (hl_appt)
                    {
                        element_obj.style.borderBottomStyle = 'dashed';
                        element_obj.style.borderLeftStyle = 'dotted';
                        
                        element_obj.focus();
                    }
                    
                }
	    }
            else
            {
                alert(key);
                alert(value);
            }
            
            index++;
        }
        
        if (focus_element)
            focus_element.focus();
        
        forceRefresh = false;
    }
    
    function clearSchedule()
    {
	//alert("clearSchedule() invoked");
        
        /*
        if (!empty_cell)
            alert("empty_cell >" + empty_cell);
	
        for (key in scheduleArray)
        {
            var element_obj = document.getElementById(scheduleArray[key]);
            //element_obj.style.borderColor = 'CCC';
            //element_obj.style.backgroundColor = 'FFFFFF';
            //element_obj.innerHTML = "<img src='../images/spacer.gif' width='12' height='12' alt='' />&nbsp;";
            
            //element_obj.className="normalRow";
            //element_obj.className='highlightRow';
          
            var parent_obj = element_obj.parentNode;
            
            //var new_element = document.getElementById("7:00 AM|8").cloneNode(true);
            var new_element = empty_cell.cloneNode(true);
            new_element.id = scheduleArray[key];
            parent_obj.insertBefore(new_element, element_obj);
            parent_obj.removeChild(element_obj);
        }

	
        */
       
        
        var element_obj = document.getElementById("schedule");
        var parent_obj = element_obj.parentNode;
        parent_obj.removeChild(element_obj);
        var new_element = empty_table.cloneNode(true);
        parent_obj.appendChild(new_element);
       
        appointmentArray = new Array();
        scheduleArray = new Array();
        scheduleIndex = 0;
        idArray = new Array();
    }
    
    function cClk(pId,tStr,dow)
    {
        if (editC)
            return;
    
        //var element_obj = document.getElementById(key);
        
        document.getElementById('practitionerSelect').value = pId;
        //document.getElementById('practitioner').firstChild.nodeValue = pLabel;
        
        
        if (dow)
        {
            var weekday = new Array("Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday");
            var monthname = new Array("January","February","March","April","May","June","July","August","September","October","November","December");
            var adj_date = new Date(selected_date.substring(0, selected_date.indexOf(" - ")));
            adj_date.setDate(adj_date.getDate() + (dow - 1));
            //alert(selected_date);
            document.getElementById('appt_date').value = weekday[adj_date.getDay()] + ", " + monthname[adj_date.getMonth()] + " " + adj_date.getDate() + ", " + adj_date.getYear();
            //document.getElementById('appt_date').value = 'frre';
            document.forms[0].pSelect.value='1';
        }
        else
        {
            document.getElementById('appt_date').value = selected_date;
            document.forms[0].pSelect.value='0';
        }
        
        //document.getElementById('newAppointmentTimeInput').value = tStr;
        //document.getElementById('time').firstChild.nodeValue = tStr;
        
        document.forms[0].hr.value=tStr.substring(0,tStr.indexOf(":"));
        document.forms[0].mn.value=tStr.substring(tStr.indexOf(":")+1,tStr.indexOf(" "));
        document.forms[0].ampm.value=tStr.substring(tStr.indexOf(" ")+1);
        
        document.forms[0].lastname.value = '';
        document.forms[0].firstname.value = '';
        
        if ((edit == 0) && (editOt == 0))
        {
            document.getElementById('apptFormLabel').firstChild.nodeValue='New Appointment';

            document.forms[0].appointmentSelect.value=-1;
            document.forms[0].statusSelect.value=-1;
            document.forms[0].clientSelect.options.length=0;
            document.forms[0].clientSelect.options[0] = new Option('-- SEARCH FOR A CLIENT --',-1);
            document.forms[0].comments.value = '';
            document.forms[0].recurVal.value = '';
            document.forms[0].recurPeriod.value = '3';
            document.forms[0].recurPW.value = '0';
            document.forms[0].recurPWS.value = '2';
            document.forms[0].stopAfterN.value = '';
            document.forms[0].stopAfterDate.value = '';
        }
        else
        {
            var key;
            if (editOt == 1)
            {
                if (dow)
                    key = tStr + "|" + dow + "|0";
                else
                    key = tStr + "|" + pId + "|0";
            }
            else
            {
                if (dow)
                    key = tStr + "|" + dow + "|" + edit;
                else
                    key = tStr + "|" + pId + "|" + edit;
            }
	    
	    //alert("idArray[key] >" + key);

            if (idArray[key])
            {
                document.getElementById('apptFormLabel').firstChild.nodeValue = 'Edit Appointment';

                eval("var apptArray = " + idArray[key]);

                document.forms[0].appointmentSelect.value = parseInt(apptArray["id"]);
                document.forms[0].statusSelect.value = parseInt(apptArray["state"]);

                document.forms[0].clientSelect.options[0] = new Option(apptArray["label"],apptArray["cid"]);
                document.forms[0].clientSelect.selectedIndex = 0;

                document.forms[0].typeSelect.value = apptArray["type"];
                document.forms[0].duration.value = apptArray["duration"];
                document.forms[0].comments.value = apptArray["cm"];
                
                if (apptArray["r"])
                {
                    if (apptArray["r1"] == '0')
                        document.forms[0].recurVal.value = '';
                    else
                        document.forms[0].recurVal.value = apptArray["r1"];
                    document.forms[0].recurPeriod.value = apptArray["r2"];
                    document.forms[0].recurPW.value = apptArray["r3"];
                    document.forms[0].recurPWS.value = apptArray["r4"];
                    if (apptArray["r5"] == '0')
                        document.forms[0].stopAfterN.value = '';
                    else
                        document.forms[0].stopAfterN.value = apptArray["r5"];
                    document.forms[0].stopAfterDate.value = apptArray["r6"];
                }
                else
                {
                    document.forms[0].recurVal.value = '';
                    document.forms[0].recurPeriod.value = '3';
                    document.forms[0].recurPW.value = '0';
                    document.forms[0].recurPWS.value = '0';
                    document.forms[0].stopAfterN.value = '';
                    document.forms[0].stopAfterDate.value = '';
                }
                
            }
            else
            {
                document.getElementById('apptFormLabel').firstChild.nodeValue = 'New Appointment';
                document.getElementById('appointmentSelect').value = '-1';
                document.forms[0].clientSelect.selectedIndex = 0;
                document.forms[0].typeSelect.selectedIndex = 0;
                document.forms[0].duration.value = '';
                document.forms[0].comments.value = '';
                document.forms[0].recurVal.value = '';
                document.forms[0].recurPeriod.value = '3';
                document.forms[0].recurPW.value = '0';
                document.forms[0].recurPWS.value = '0';
                document.forms[0].stopAfterN.value = '';
                document.forms[0].stopAfterDate.value = '';
            }

            edit = 0;
            editOt = 0;
        }
        
        YAHOO.example.container.dialog1.show();
    }
    
    //function cOut(key)
    //{
        //if (document.getElementById(key).style.backgroundColor == '#dddddd') { document.getElementById(key).style.backgroundColor = ''; }
        //editA = 0;
	//editC = 0;
    //}
    
    function cOvr(key)
    {
        last_key_ovr = key;
        //if (document.getElementById(key).style.backgroundColor == '') { document.getElementById(key).style.backgroundColor = '#dddddd'; }
    }
    
    function editAppt(cid)
    {
        edit = cid;
    }
    
    function editOtx(k)
    {
        editOt = 1;
    }
    
    function editClient(cid)
    {
        editC = cid;
    }
	
    function clientOut()
    {
        editA = 0;
        editC = 0;
    }

    function clientOvr(cid,id)
    {
        editA = id;
        editC = cid;
        //processCommand('getPersonStats', cid);
        //document.getElementById('today_date').firstChild.nodeValue = xml_response.getElementsByTagName("date")[0].childNodes[0].nodeValue;
    }
    
    function endsWith(str, s)
    {
        var reg = new RegExp(s + "$");
        return reg.test(str);
    }
        
    function changeState(state)
    {
        if (editA2 != 0)
            processCommand(state,editA2);
    }
    
    function checkout()
    {
        if (editA2 != 0)
	    processCommand('getCheckoutDetails', editA2);
    }
    
    function cut()
    {
        if (editA2 != 0)
            processCommand('cut', editA2);
    }
    
    function paste()
    {
        if (last_key_ovr)
        {
            if (paLabel == '')
                processCommand('pastep', last_key_ovr);
            else
                processCommand('paste', last_key_ovr);
        }
    }
    
    function remove()
    {
        if (editA2 != 0)
        {
            if (confirm("Delete?"))
                processCommand('delete', editA2);
        }
    }
    
    function addCode(tbl)
    {
        var index_value = document.forms[2].codeSelect.value;
        if (index_value)
        {
	    if (tbl == 'tblCharges')
	    {
		if (numCharges == 0)
		    deleteAllRows(document.getElementById('tblCharges'));
		numCharges++;
            }
	    else
	    {
		if (numCredits == 0)
		    deleteAllRows(document.getElementById('tblCredits'));
		numCredits++;
	    }
            
            var amount = codeArray[index_value]["amount"];
            if (codeArray[index_value]["practice_area"] == plan_id)
            {
                amount = "PLAN";
                //alert('no charge for this checkout code');
            }
	    
            addRowToTable(tbl, codeArray[index_value]["desc"], codeArray[index_value]["id"], codeArray[index_value]["code"], amount);
        }
        else
            alert('You must select a code to add.');
    }
    
    function myRowObject(one, two, three, four)
    {
        this.one = one;
        this.two = two;
        this.three = three;
        this.four = four;
    }

    function addRowToTable(tname, desc, id, code, amount)
    {
	var tbl = document.getElementById(tname);
	var nextRow = tbl.tBodies[0].rows.length;
	var iteration = nextRow + 1;
        
        /*
	if (num == null)
	    num = nextRow;
	else
	    iteration = num + 1;
        */
        num = nextRow;

	// add the row
	var row = tbl.tBodies[0].insertRow(num);
	
	//eval("focusControl = document.getElementById('" + index + "').focus();");

	// CONFIG: requires classes named classy0 and classy1
	row.className = 'classy' + (iteration % 2);

        var cell0;
        var textNode;
	if (!id)
	{
	    cell0 = row.insertCell(0);
	    textNode = document.createTextNode(desc);
	    cell0.style.textAlign = 'left';
	    if (tname == 'tblCharges')
		cell0.colSpan = '6';
	    else
		cell0.colSpan = '5';
	    cell0.appendChild(textNode);
	    return;
        }

	// cell 0 - text
	cell0 = row.insertCell(0);
	if (tname == 'tblCharges')
	    cell0.setAttribute('id', 'ch' + iteration);
	else
	    cell0.setAttribute('id', 'cr' + iteration);
	textNode = document.createTextNode(iteration);
	cell0.appendChild(textNode);

	// cell 1 - text
	var cell1 = row.insertCell(1);
	textNode = document.createTextNode(code + " " + desc);
	cell1.style.textAlign = 'left';
	cell1.appendChild(textNode);

	// cell 2 - input text
        var cell2 = row.insertCell(2);
        var hiddenInp = document.createElement('input');
        if (amount == 'PLAN')
        {
            textNode = document.createTextNode(amount);
            //cell1.style.textAlign = 'left';
            cell2.appendChild(textNode);
            hiddenInp.setAttribute('type', 'hidden');
            hiddenInp.setAttribute('name', 'inputName' + iteration + tname);
            hiddenInp.setAttribute('value', '0.00');
            cell2.appendChild(hiddenInp);
            hiddenInp = document.createElement('input');
            hiddenInp.setAttribute('type', 'hidden');
            hiddenInp.setAttribute('name', 'hidden' + iteration + tname);
            hiddenInp.setAttribute('value', id);
            cell2.appendChild(hiddenInp);
        }
        else
        {
            var txtInp = document.createElement('input');
            txtInp.setAttribute('type', 'text');
            txtInp.setAttribute('name', 'inputName' + iteration + tname);
            txtInp.setAttribute('size', '8');
            txtInp.setAttribute('value', amount);
            txtInp.onkeyup = function () {updateAmount(tname)};
            cell2.appendChild(txtInp);
            hiddenInp.setAttribute('type', 'hidden');
            hiddenInp.setAttribute('name', 'hidden' + iteration + tname);
            hiddenInp.setAttribute('value', id);
            cell2.appendChild(hiddenInp);
        }

	// cell 3 - input button
	var cell3 = row.insertCell(3);
	var btnEl = document.createElement('input');
	btnEl.setAttribute('type', 'button');
	btnEl.setAttribute('value', 'Delete');
	btnEl.onclick = function () {deleteCurrentRow(this, tname)};
	cell3.appendChild(btnEl);

	if (tname == 'tblCharges')
	{
	    // cell 4 - input check
	    var cell4 = row.insertCell(4);
	    var checkInp = document.createElement('input');
	    checkInp.setAttribute('type', 'checkbox');
	    checkInp.setAttribute('name', 'inputCheck' + iteration + tname);
	    checkInp.setAttribute('size', '8');
	    cell4.appendChild(checkInp);
	}
	
	

	// Pass in the elements you want to reference later
	// Store the myRow object in each row
	//row.myRow = new myRowObject(textNode, txtInp, cbEl, raEl);
	row.myRow = new myRowObject(textNode, txtInp);

	updateAmount(tname);
    }
    
    function addRowToTableNoEdit(tname, desc, date, id, code, amount)
    {
	var tbl = document.getElementById(tname);
	var nextRow = tbl.tBodies[0].rows.length;
	var iteration = nextRow + 1;
        
        /*
	if (num == null)
	    num = nextRow;
	else
	    iteration = num + 1;
            */
        
        num = nextRow;

	// add the row
	var row = tbl.tBodies[0].insertRow(num);

	// CONFIG: requires classes named classy0 and classy1
	row.className = 'classy' + (iteration % 2);
	
	if (desc == 'No Open Orders Found')
	{
	    // cell 0 - text
	    var cell0 = row.insertCell(0);
	    var textNode = document.createTextNode(desc);
	    cell0.style.textAlign = 'left';
	    cell0.colSpan = '5';
	    cell0.appendChild(textNode);
	    return;
        }

	// CONFIG: This whole section can be configured

	// cell 0 - text
	var cell0 = row.insertCell(0);
	var textNode = document.createTextNode(iteration);
	cell0.appendChild(textNode);

	// cell 1 - text
	var cell1 = row.insertCell(1);
	var textNode = document.createTextNode(date);
	cell1.appendChild(textNode);

	// cell 2 - text
	var cell2 = row.insertCell(2);
	var textNode = document.createTextNode(code + " " + desc);
	cell2.style.textAlign = 'left';
	cell2.appendChild(textNode);

	// cell 3 - text
	var cell3 = row.insertCell(3);
	var textNode = document.createTextNode(amount);
	cell3.appendChild(textNode);

	// cell 4 - input check
	var cell4 = row.insertCell(4);
	var checkInp = document.createElement('input');
	checkInp.setAttribute('type', 'checkbox');
	checkInp.setAttribute('name', 'inputCheck' + iteration + tname);
	checkInp.setAttribute('size', '8');
	cell4.appendChild(checkInp);
	var hiddenInp = document.createElement('input');
	hiddenInp.setAttribute('type', 'hidden');
	hiddenInp.setAttribute('name', 'hidden' + 'inputCheck' + iteration + tname);
	hiddenInp.setAttribute('value', id);
	cell4.appendChild(hiddenInp);
    }
    
    function deleteCurrentRow(obj, tname)
    {
	var delRow = obj.parentNode.parentNode;
	var tbl = delRow.parentNode.parentNode;
	var rIndex = delRow.sectionRowIndex;
	var rowArray = new Array(delRow);
	deleteRows(rowArray);
	reorderRows(tbl, rIndex, tname);
	updateAmount(tname);
    }

    function reorderRows(tbl, startingIndex, tname)
    {
	if (tbl.tBodies[0].rows[startingIndex]) {
	    var count = startingIndex + 1;
	    for (var i=startingIndex; i<tbl.tBodies[0].rows.length; i++) {

		// CONFIG: next line is affected by myRowObject settings
                
		//tbl.tBodies[0].rows[i].myRow.one.data = count; // text

		// CONFIG: next line is affected by myRowObject settings
		//tbl.tBodies[0].rows[i].myRow.two.name = 'inputName' + count + tname; // input text

		// CONFIG: next line is affected by myRowObject settings
		//var tempVal = tbl.tBodies[0].rows[i].myRow.two.value.split(' '); // for debug purposes
		//tbl.tBodies[0].rows[i].myRow.two.value = tempVal[0]; // for debug purposes

		// CONFIG: next line is affected by myRowObject settings
		//tbl.tBodies[0].rows[i].myRow.four.value = count; // input radio

		// CONFIG: requires class named classy0 and classy1
                //alert(tbl.tBodies[0].rows[i].cells[0].innerHTML);
                tbl.tBodies[0].rows[i].cells[0].innerHTML = count;
		tbl.tBodies[0].rows[i].className = 'classy' + (count % 2);

		count++;
	    }
	}
    }

    function deleteRows(rowObjArray)
    {
	for (var i=0; i<rowObjArray.length; i++) {
	    var rIndex = rowObjArray[i].sectionRowIndex;
	    rowObjArray[i].parentNode.deleteRow(rIndex);
	}
    }

    function deleteAllRows(tbl)
    {
        for (var i = tbl.tBodies[0].rows.length; i > 0; i--)
            tbl.tBodies[0].rows[i - 1].parentNode.deleteRow(i - 1);
    }
    
    function updateAmount(tname)
    {
        var table_amount = 0;
        var row_amount = 0;
        var table_obj = document.getElementById(tname);
        for (var i = 0; table_obj.tBodies[0].rows[i]; i++)
        {
            //alert(table_obj.tBodies[0].rows[i].myRow.two.value);
            row_amount = table_obj.tBodies[0].rows[i].myRow.two.value;
            table_amount += (parseFloat(row_amount) ? parseFloat(row_amount) : 0);
        }
        table_amount = Math.round(table_amount * 100) / 100;
        
        if (tname == 'tblCharges')
        {
            total_charges = table_amount;
            document.getElementById('charges').firstChild.nodeValue = total_charges;
        }
        else
        {
            total_credits = table_amount;
            document.getElementById('credits').firstChild.nodeValue = total_credits;
        }
        
        client_owes = Math.round((previous_balance + total_charges - total_credits) * 100) / 100;
        document.getElementById('owes').firstChild.nodeValue = client_owes;
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
YAHOO.example.loaderinit = function() {
	
	//Begin by creating a new Loader instance:
	var loader = new YAHOO.util.YUILoader();
	
	//configure Loader; we'll request TabView plus any
	//optional dependencies of TabView that aren't already on
	//the page:
        loader.require("layout");
        
	loader.require("container");
        loader.require("menu");
        loader.require("calendar");
        loader.require("tabview");
        loader.require("button");
	loader.loadOptional = true;
	
	//We can now look at the components list that Loader has
	//calculated; this is what Loader has determined it needs
	//to add to the page:
	//YAHOO.log("YUI components required: " + loader.sorted, "info", "example");
	
	//We'll specify files local to the current HTML page
	//so Loader does not load files from yui.yahooapis.com:
	loader.base = '../yui/build/';
	
	//When the loading is all complete, we want to initialize
	//our TabView process; we can set this here or pass this
	//in as an argument to the insert() method:
	//loader.onSuccess = YAHOO.example.tabviewinit;
        //loader.onSuccess = init;
        loader.onSuccess = initLayout;
	
	//We've created and configured our Loader instance;
	//now we tell it to insert the needed components on the
	//page:
	loader.insert();
	
};
YAHOO.util.Event.onDOMReady(YAHOO.example.loaderinit);
    
YAHOO.namespace("example.container");



function init() {
    
    
	
	// Define various event handlers for Dialog
	var handleSubmit = function() {
		this.submit();
	};
	var handleNone = function() {
		document.forms[0].statusSelect.value='<%= AppointmentBean.DEFAULT_APPOINTMENT_STATUS %>';
		this.submit();
	};
	var handleReschedule = function() {
		document.forms[0].statusSelect.value='<%= AppointmentBean.RESCHEDULE_APPOINTMENT_STATUS %>';
		this.submit();
	};
	var handleCheckIn = function() {
		document.forms[0].statusSelect.value='<%= AppointmentBean.CHECKED_IN_APPOINTMENT_STATUS %>';
		this.submit();
	};
	var handleCheckOut = function() {
		//document.forms[0].statusSelect.value='<%= AppointmentBean.CHECKED_OUT_APPOINTMENT_STATUS %>';
		//this.submit();
		//alert('hey >' + document.forms[0].clientSelect.value);
		document.location.href='checkout.jsp?id=' + document.forms[0].clientSelect.value + '&appt_id=' + document.forms[0].appointmentSelect.value;
	};
	var handleLate = function() {
		document.forms[0].statusSelect.value='<%= AppointmentBean.LATE_APPOINTMENT_STATUS %>';
		this.submit();
	};
	var handleCancel = function() {
		YAHOO.example.container.dialog1.hide();
	};
	var handleSuccess = function(o) {
		if (o.responseText.length > 0)
		    alert(o.responseText);
		
		document.forms[0].appointmentSelect.value = -1;
		document.forms[0].statusSelect.value = -1;
		document.forms[0].clientSelect.selectedIndex = -1;
		document.forms[0].typeSelect.selectedIndex = 0;
		document.forms[0].duration.value = "";
		
                if (document.forms[0].pSelect.value == '0')
                    processCommand('forceRefresh');
                else
                    processCommand('forceRefresh','p');
	};
	var handleFailure = function(o) {
		alert("Submission failed: " + o.status);
	};

	// Instantiate the Dialog
	YAHOO.example.container.dialog1 = new YAHOO.widget.Dialog("dialog1", 
                        { width : "475px",
                          fixedcenter : true,
                          visible : false,
                          lazyload : true,
                          constraintoviewport : true,
                          buttons : [ { text:"Cancel", handler:handleCancel },
                                                  { text:"Submit", handler:handleSubmit, isDefault:true } ]
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
		if (data.clientSelect == 0)
		{
                    if (typeArray[data.typeSelect]["type"] == appt_type_type)
                    {
                        alert("Please select a client.");
                        return false;
                    }
		}
		
		return true;
	};

	// Wire up the success and failure handlers
	YAHOO.example.container.dialog1.callback = { success: handleSuccess, failure: handleFailure };
	
	// Render the Dialog
	YAHOO.example.container.dialog1.render();

	
	var handleSubmit2 = function() {
		this.submit();
	};
	var handleCancel2 = function() {
		YAHOO.example.container.dialog2.hide();
	};
	YAHOO.example.container.dialog2 = new YAHOO.widget.Dialog("dialog2", 
                        { width : "575px",
                          fixedcenter : true,
                          visible : false,
                          lazyload : true,
                          constraintoviewport : true,
                          buttons : [ { text:"Cancel", handler:handleCancel2 },
                                                  { text:"Submit", handler:handleSubmit2, isDefault:true } ]
                         } );
	
	// Validate the entries in the form to require that both first and last name are entered
	YAHOO.example.container.dialog2.validate = function() {
		
		return true;
	};

	// Wire up the success and failure handlers
	YAHOO.example.container.dialog2.callback = { success: handleSuccess, failure: handleFailure };
	
	// Render the Dialog
	YAHOO.example.container.dialog2.render();
	
	
	
	
	
	
	
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
                          lazyload : true,
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

	// Wire up the success and failure handlers
	YAHOO.example.container.checkoutDialog.callback = { success: handleSuccess, failure: handleFailure };
	
	// Render the Dialog
	YAHOO.example.container.checkoutDialog.render();
	
	
	
	
	
	

	 
    YAHOO.util.Event.onContentReady("basicmenu", function () {
    
        /*
             Instantiate the menu.  The first argument passed to the 
             constructor is the id of the element in the DOM that represents 
             the menu; the second is an object literal representing a set of 
             configuration properties for the menu.
        */

        var oMenu = new YAHOO.widget.ContextMenu("basicmenu", { trigger: "tableContainer", lazyload: true, hidedelay: 250 });
		
		/*
		var oFieldContextMenu = new YAHOO.widget.ContextMenu(
										"fieldcontextmenu",
										{
											trigger: "clones",
											itemdata: oFieldContextMenuItemData,
											lazyload: true,
											effect: {
												effect: YAHOO.widget.ContainerEffect.FADE,
												duration: 0.1
											}
										}
									);
									*/

        oMenu.showEvent.subscribe(function () {
            //
            editA2 = editA;
            editA = 0;
	    editC2 = editC;
            editC = 0;
            this.focus();
        });

        /*
             Call the "render" method with no arguments since the markup for 
             this menu already exists in the DOM.
        */

        oMenu.render();

        YAHOO.util.Event.addListener("menutoggle", "click", oMenu.show, null, oMenu);

    });
    
    
    
        
        
        // Define various event handlers for Dialog
	var handleSubmitNewClient = function() {
		this.submit();
	};
	var handleSuccessNewClient = function(o) {
		if (o.responseText.length > 0)
		    alert(o.responseText);
		else
		{
			//processCommand('getPeopleByLastName', '');
			processCommand('getPersonDetails', '-1');
		}
	};
	var handleFailureNewClient = function(o) {
		alert("Submission failed: " + o.status);
	};
        
        // Instantiate the Dialog
	YAHOO.example.container.dialogNewClient = new YAHOO.widget.Dialog("dialogNewClient", 
                                { width : "275px",
                                  fixedcenter : true,
                                  visible : false,
                                  lazyload : true,
                                  constraintoviewport : true,
                                  buttons : [ { text:"Submit", handler:handleSubmitNewClient, isDefault:true } ]
                                 } );
	
	// Validate the entries in the form to require that both first and last name are entered
	YAHOO.example.container.dialogNewClient.validate = function() {
		var data = this.getData();
		if (data.firstname == 0) { alert("Please select a first name."); return false; }
		if (data.lastname == 0) { alert("Please select a last name."); return false; }
		return true;
	};

	// Wire up the success and failure handlers
	YAHOO.example.container.dialogNewClient.callback = { success: handleSuccessNewClient, failure: handleFailureNewClient };
	
	// Render the Dialog
	YAHOO.example.container.dialogNewClient.render();
        
        
      YAHOO.util.Event.onDOMReady(YAHOO.example.calendar.init);

}

//YAHOO.util.Event.onDOMReady(init);

YAHOO.namespace("example.calendar");

YAHOO.example.calendar.init = function() {
	
	//var render_appt_str = "2/1/2008-2/7/2008, 2/12/2008-2/17/2008, 2/25/2008, 2/26/2008, 2/27/2008, 2/28/2008, 2/29/2008, 2/30/2008, 3/1/2008-3/7/2008, 3/12/2008-3/17/2008, 3/25/2008, 3/26/2008, 3/27/2008, 3/28/2008, 3/29/2008, 3/30/2008, 4/1/2008-4/7/2008, 4/12/2008-4/17/2008, 4/25/2008, 4/26/2008, 4/27/2008, 4/28/2008, 4/29/2008, 4/30/2008, 5/1/2008-5/7/2008, 5/12/2008-4/17/2008, 5/25/2008, 5/26/2008, 5/27/2008, 5/28/2008, 5/29/2008, 5/30/2008, 6/1/2008-6/7/2008, 6/12/2008-6/17/2008, 6/25/2008, 6/26/2008, 6/27/2008, 6/28/2008, 6/29/2008, 6/30/2008";

	YAHOO.example.calendar.cal1 = new YAHOO.widget.CalendarGroup("cal1","cal1Container", { pagedate:pageDateStr } );
	//YAHOO.example.calendar.cal1 = new YAHOO.widget.Calendar("cal1","cal1Container");
	//YAHOO.example.calendar.cal1.addRenderer(render_appt_str, YAHOO.example.calendar.cal1.renderCellStyleHighlight1);
	/*
	YAHOO.example.calendar.cal1.addRenderer("2/29", YAHOO.example.calendar.cal1.renderBodyCellRestricted);
	YAHOO.example.calendar.cal1.addRenderer("2/1/2008-2/7/2008", YAHOO.example.calendar.cal1.renderCellStyleHighlight1);

	var myCustomRenderer = function(workingDate, cell) {
		cell.innerHTML = "X";
		YAHOO.util.Dom.addClass(cell, "sunday");
		return YAHOO.widget.Calendar.STOP_RENDER;
	}
	YAHOO.example.calendar.cal1.addWeekdayRenderer(1, myCustomRenderer);
	*/

	var selectHandler = function() {
		//alert(YAHOO.example.calendar.cal1.getSelectedDates());
		//processCommand('gotoDay', YAHOO.example.calendar.cal1.getSelectedDates());
                processGotoDay(YAHOO.example.calendar.cal1.getSelectedDates());
	};

	var selectHandler2 = function(type,args,obj) {
            var dates = args[0]; 
            var date = dates[0];
            var year = date[0], month = date[1], day = date[2];
            document.forms[1].callOnInput.value = month + "/" + day + "/" + year;
            document.forms[1].todoInput[0].checked = true;
            YAHOO.example.calendar.cal2.hide();
	};

	var selectHandler3 = function(type,args,obj) {
            var selected = args[0];
            var selDate = this.toDate(selected[0]);
            document.getElementById('appt_date').value = dateToLocaleString(selDate,YAHOO.example.calendar.acal2);
            YAHOO.example.calendar.acal2.hide();
	};

	var selectHandler4 = function(type,args,obj) {
            var selected = args[0];
            var selDate = this.toDate(selected[0]);
            document.getElementById('stopAfterDate').value = dateToLocaleString(selDate,YAHOO.example.calendar.acal2);
            YAHOO.example.calendar.acal3.hide();
	};
        
        function dateToLocaleString(dt, cal) {
            var wStr = cal.cfg.getProperty("WEEKDAYS_LONG")[dt.getDay()];
            var dStr = dt.getDate();
            var mStr = cal.cfg.getProperty("MONTHS_LONG")[dt.getMonth()];
            var yStr = dt.getFullYear();
            return (wStr + ", " + mStr + " " + dStr + ", " + yStr);
        }
	 
	YAHOO.example.calendar.cal1.selectEvent.subscribe(selectHandler, YAHOO.example.calendar.cal1, true);
	YAHOO.example.calendar.cal1.render();
	
	YAHOO.example.calendar.cal2 = new YAHOO.widget.Calendar("cal2","cal2Container", { title:"Choose a date:", close:true } );
	YAHOO.example.calendar.cal2.render();
	YAHOO.example.calendar.cal2.selectEvent.subscribe(selectHandler2, YAHOO.example.calendar.cal2, true);

	var tabView = new YAHOO.widget.TabView('demo');
	var tabView2 = new YAHOO.widget.TabView('demo2');
	
	function onButtonClick(p_oEvent) {
            //alert("You clicked button: " + this.get("id"));
	    if (this.get("id") == 'sButton1a')
	    {
		if (document.forms[1].elements['contactInput'][8].checked)
		    processCommand('addContactStatus',document.forms[1].elements['otherInput'].value);
		else
		{
		    for (var i = 0; document.forms[1].elements['contactInput'][i]; i++)
		    {
			if (document.forms[1].elements['contactInput'][i].checked)
			{
			    processCommand('addContactStatus',i);
			    break;
			}
		    }
		}
	    }
	    else if (this.get("id") == 'sButton1d')
		processCommand('deleteContactStatus',document.forms[1].elements['eventList'].value);
	    else if (this.get("id") == 'sButton2a')
	    {
		if (document.forms[1].elements['todoInput'][0].checked)
		    processCommand('addToDo', document.forms[1].elements['callOnInput'].value);
		else if (document.forms[1].elements['todoInput'][1].checked)
		    processCommand('addToDo', document.forms[1].elements['eList1'].value + '|' + document.forms[1].elements['eList2'].value);
            }
	    else if (this.get("id") == 'sButton2d')
		processCommand('deleteToDo',document.forms[1].elements['toDoList'].value);
	    else if (this.get("id") == 'sButton3a')
	    {
		processCommand('addCareDetails', document.forms[1].elements['paList'].value + '|' + document.forms[1].elements['ppList'].value + '|' + document.forms[1].elements['fList1'].value + '|' + document.forms[1].elements['fList2'].value);
            }
	    else if (this.get("id") == 'sButton3d')
		processCommand('deleteCareDetails',document.forms[1].elements['foqList'].value);
        }
	
	function addCharge(p_oEvent) {
            addCode('tblCharges');
	    eval("focusControl = document.getElementById('ch" + document.getElementById('tblCharges').tBodies[0].rows.length + "').focus();");
        }
	
	function addCredit(p_oEvent) {
            addCode('tblCredits');
	    eval("focusControl = document.getElementById('cr" + document.getElementById('tblCredits').tBodies[0].rows.length + "').focus();");
        }
	
	function addTrx(p_oEvent) {
            
            //alert (document.forms[2].typeSelect[document.forms[2].typeSelect.selectedIndex].innerHTML);
            
            if (document.forms[2].typeSelect[document.forms[2].typeSelect.selectedIndex].innerHTML == 'Payment')
            {
                addCode('tblCredits');
                eval("focusControl = document.getElementById('cr" + document.getElementById('tblCredits').tBodies[0].rows.length + "').focus();");
            }
            else
            {
                addCode('tblCharges');
                eval("focusControl = document.getElementById('ch" + document.getElementById('tblCharges').tBodies[0].rows.length + "').focus();");
            }
        }
	
	var oPushButton1 = new YAHOO.widget.Button("sButton1a", { onclick: { fn: onButtonClick } });
	var oPushButton2 = new YAHOO.widget.Button("sButton1d", { onclick: { fn: onButtonClick } });
	var oPushButton3 = new YAHOO.widget.Button("sButton2a", { onclick: { fn: onButtonClick } });
	var oPushButton4 = new YAHOO.widget.Button("sButton2d", { onclick: { fn: onButtonClick } });
	var oPushButton5 = new YAHOO.widget.Button("sButton3a", { onclick: { fn: onButtonClick } });
	var oPushButton6 = new YAHOO.widget.Button("sButton3d", { onclick: { fn: onButtonClick } });
	
	var oPushButton7 = new YAHOO.widget.Button("chargeButton", { onclick: { fn: addCharge } });
	var oPushButton8 = new YAHOO.widget.Button("creditButton", { onclick: { fn: addCredit } });
	var oPushButton9 = new YAHOO.widget.Button("addButton", { onclick: { fn: addTrx } });
	
	function newClient(p_oEvent) {
            document.newClientForm.firstname.value='';
	    document.newClientForm.lastname.value='';
	    document.newClientForm.relationshipSelect.value='1';
	    document.newClientForm.clientFileTypeSelect.value='-1';
	    YAHOO.example.container.dialogNewClient.show();
        }
	
	var newClientButton1 = new YAHOO.widget.Button("b1a", { onclick: { fn: newClient } });
        
        
        
        
        if (paLabel != '')
            var oMenuButton4 = new YAHOO.widget.Button("menubutton2", { type: "menu", label: paLabel, name: "mymenubutton", menu: aMenuButton4Menu });
        
        
        
        var apptCalPop = new YAHOO.widget.Button("acp");
        YAHOO.example.calendar.acal2 = new YAHOO.widget.Calendar("acal2","cal3Container", { title:"Choose a date:", close:true } );
        YAHOO.example.calendar.acal2.render();
        YAHOO.example.calendar.acal2.hide();
	YAHOO.example.calendar.acal2.selectEvent.subscribe(selectHandler3, YAHOO.example.calendar.acal2, true);

        // Listener to show the 1-up Calendar when the button is clicked
        YAHOO.util.Event.addListener("acp", "click", YAHOO.example.calendar.acal2.show, YAHOO.example.calendar.acal2, true);
        
        
        var apptCalPop2 = new YAHOO.widget.Button("acp2");
        YAHOO.example.calendar.acal3 = new YAHOO.widget.Calendar("acal3","cal4Container", { title:"Choose a date:", close:true } );
        YAHOO.example.calendar.acal3.render();
        YAHOO.example.calendar.acal3.hide();
	YAHOO.example.calendar.acal3.selectEvent.subscribe(selectHandler4, YAHOO.example.calendar.acal3, true);

        // Listener to show the 1-up Calendar when the button is clicked
        YAHOO.util.Event.addListener("acp2", "click", YAHOO.example.calendar.acal3.show, YAHOO.example.calendar.acal3, true);
        
        var apptCalPop3 = new YAHOO.widget.Button("acp3", { onclick: { fn: nextAppt } });
	function nextAppt(p_oEvent) {
            if (document.forms[0].typeSelect.value != '0')
            {
                block = true;
                if (paLabel == '')
                    processCommand('nextApptP', document.forms[0].practitionerSelect.value + '|' + document.forms[0].typeSelect.value + '|' + document.forms[0].duration.value + '|' + document.forms[0].appt_date.value + '-' + document.forms[0].hr.value + ':' + document.forms[0].mn.value + document.forms[0].ampm.value);
                else
                    processCommand('nextAppt', document.forms[0].practitionerSelect.value + '|' + document.forms[0].typeSelect.value + '|' + document.forms[0].duration.value + '|' + document.forms[0].appt_date.value + '-' + document.forms[0].hr.value + ':' + document.forms[0].mn.value + document.forms[0].ampm.value);
            }
        }
        
        
        
        initLayout();
        
        
        
    
        
}

function initLayout()
{
    if (paLabel == '')
        refreshPractitioner();
    else
        refresh();
    
    (function() {
    var Dom = YAHOO.util.Dom,
        Event = YAHOO.util.Event;

    Event.onDOMReady(function() {
        var layout = new YAHOO.widget.Layout({
                minWidth: 750,
                units: [
		{ position: 'top', height: 21, resize: false, body: 'top1' },
                //{ position: 'right', header: 'Today\'s Clients', width: 230, resize: true, gutter: '0px', collapse: true, scroll: true, body: 'right1', animate: true, duration: .1 },
		{ position: 'left', header: 'Stats & Calendar', width: 213, resize: false, body: 'left1', gutter: '0px', collapse: true, close: false, scroll: true, animate: true, duration: .1 },
                { position: 'center', body: 'center1', scroll: true, minWidth: 400 }
            ]
        });
        layout.on('render', function() {
            layout.getUnitByPosition('left').on('close', function() {
                closeLeft();
            });
        });
        layout.render();
	layout.getUnitByPosition('right').collapse();
	
    });


})();
}

//YAHOO.util.Event.onDOMReady(YAHOO.example.calendar.init);