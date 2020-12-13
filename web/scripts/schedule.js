		
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
    var editB = 0;
    var last_key_ovr;
    
    var numCharges = 0;
    var numCredits = 0;
    
    var previous_balance = 0;
    var total_charges = 0;
    var total_credits = 0;
    var client_owes = 0;
    
    var mTimer;
    var mTimer2;
    var doCCRefresh = false;

    var amount_1;
    var amount_2;
    var amount_1_valid;
    var amount_2_valid;

    var forceRefresh = true;
    var focusControl;

    var block = false;
    var block_count = 0;
    
    var plan_id = 0;

    var refresh_black_hole = false;
    var cal_for_review = false;

    var myEditor;

    var client_check_in = false;

    var default_practitioner = 0;

    var selected_note = '';

	var iteration = 1;

	var hiddenGiftCardNumber;
	var gift_card_payment = false;
	var gift_certificate_payment = false;

	var reverse_checked = false;

	var credit_card_number;

	var is_sales_receipt = false;
	var is_credit_memo = false;
	
	var guc_selected = false;

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

	function disableEnterKey(e)
	{
		 var key;
		 if(window.event)
			  key = window.event.keyCode;     //IE
		 else
			  key = e.which;     //firefox
		 if(key == 13)
			  return false;
		 else
			  return true;
	}

    function refresh()
    {
		if (disconnected)
			document.getElementById('today_date').firstChild.nodeValue = 'Attempting to Reconnect...';
			if (!block)
			{
			//alert('r2');
			if (can_block)
			block = true;
				if (forceRefresh)
					processCommand('forceRefresh');
				else
					processCommand('refresh');
			}
		else
		{
			block_count++;
			document.getElementById('today_date').firstChild.nodeValue = 'Blocked...(' + block_count + ')';
		}

		if (disconnected)
			mTimer = setTimeout('refresh();',20000);
		else
			mTimer = setTimeout('refresh();',5000);
    }

    function refreshPractitioner()
    {
		if (disconnected)
			document.getElementById('today_date').firstChild.nodeValue = 'Attempting to Reconnect...';
			if (!block)
			{
			//alert('r2');
			if (can_block)
			block = true;
				if (forceRefresh)
					processCommand('forceRefresh','p');
				else
					processCommand('refresh','p');
			}
		else
		{
			block_count++;
			document.getElementById('today_date').firstChild.nodeValue = 'Blocked...(' + block_count + ')';
		}

		if (disconnected)
			mTimer = setTimeout('refreshPractitioner();',20000);
		else
			mTimer = setTimeout('refreshPractitioner();',5000);


		/*
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
		*/
    }

    function refreshCreditCardStatus()
    {
		if (doCCRefresh)
		{
			processCommand('acceptCreditCardStatus');
			mTimer2 = setTimeout('refreshCreditCardStatus();',1000);
		}
    }
    
    function showPeople(xml_str)
    {
        if (show_ci_names)
            document.forms['clientInfoForm'].ci_clientSelect.options.length = 0;
		else if (show_co_names)
			document.forms['checkoutForm'].co_clientSelect.options.length = 0;
        else if (show_group_names)
            document.forms['newClientForm'].groupSelect.options.length = 0;
        else if (show_referral_names)
            document.forms['newClientForm'].referralClientSelect.options.length = 0;
        else
            document.forms['newApptForm'].clientSelect.options.length = 0;
        
        var index = 0;
        while (xml_str.getElementsByTagName("person")[index] != null)
        {
            key = xml_str.getElementsByTagName("person")[index].childNodes[0].childNodes[0].nodeValue;
            value = xml_str.getElementsByTagName("person")[index].childNodes[1].childNodes[0].nodeValue;
            eval("var personArray = " + value);

			if (show_ci_names)
				document.forms['clientInfoForm'].ci_clientSelect.options[index] = new Option(personArray["label"],key);
			else if (show_co_names)
				document.forms['checkoutForm'].co_clientSelect.options[index] = new Option(personArray["label"],key);
            else if (show_group_names)
                document.forms['newClientForm'].groupSelect.options[index] = new Option(personArray["label"],key);
            else if (show_referral_names)
                document.forms['newClientForm'].referralClientSelect.options[index] = new Option(personArray["label"],key);
            else
                document.forms['newApptForm'].clientSelect.options[index] = new Option(personArray["label"],key);

            index++;
        }

		show_ci_names = false;
		show_co_names = false;
        show_group_names = false;
        show_referral_names = false;
    }

    function showCodes(xml_str, show)
    {
		codeArray = new Array();

        document.forms['checkoutForm'].codeSelect.options.length = 0;
        var index = 0;
        while (xml_str.getElementsByTagName("code")[index] != null)
        {
            key = xml_str.getElementsByTagName("code")[index].childNodes[0].childNodes[0].nodeValue;
            value = xml_str.getElementsByTagName("code")[index].childNodes[1].childNodes[0].nodeValue;
            eval("var codeArr = " + value);

	    
		
		


	    codeArray[key] = codeArr;

	    document.forms['checkoutForm'].codeSelect.options[index] = new Option(codeArr["desc"],key);
            index++;
        }
        
        if (show) {
            document.getElementById("codeSelect").selectedIndex = 0;
            //addTrx(); // not working???
            is_sales_receipt = true;
            addCode('tblCharges');
            eval("focusControl = document.getElementById('ch" + document.getElementById('tblCharges').tBodies[0].rows.length + "').focus();");
        }
    }

    function showStatements(xml_str)
    {
		stArray = new Array();

        document.clientInfoForm.ci_noteOptionSelect.options.length = 0;
	
        var index = 0;
		while (xml_str.getElementsByTagName("item")[index] != null)
		{
			key = xml_str.getElementsByTagName("item")[index].childNodes[0].childNodes[0].nodeValue;
			value = xml_str.getElementsByTagName("item")[index].childNodes[1].childNodes[0].nodeValue;
			eval("var stArr = " + value);

			//if (!stArray[key])
			//stArray[key] = new Array();

			stArray[key] = stArr;

			document.clientInfoForm.ci_noteOptionSelect.options[index + 1] = new Option(stArr["label"],key);
			index++;
		}

		document.clientInfoForm.ci_noteOptionSelect.options[0] = new Option('..',key);
    }
    
    function showClientInfo(cid)
    {
		//alert('showClientInfo >' + cid);
		if (cid)
			processCommand('getPersonStats', cid);
    }
    
	function showCheckout(xml_str)
	{
		//alert("showCheckout() invoked");

		//var key = xml_str.childNodes[0].childNodes[0].nodeValue;
		//var value = xml_str.childNodes[1].childNodes[0].nodeValue;

		if (xml_str.getAttribute("p") != '0')
			default_practitioner = xml_str.getAttribute("p");

		var key = xml_str.getElementsByTagName("person")[0].childNodes[0].childNodes[0].nodeValue;
		var value = xml_str.getElementsByTagName("person")[0].childNodes[1].childNodes[0].nodeValue;

		eval("var arr = " + value);

		//alert(value);

		previous_balance = parseFloat(xml_str.getElementsByTagName("previous")[0].childNodes[0].nodeValue);
		total_charges = 0;
		total_credits = 0;
		client_owes = previous_balance;

		document.getElementById('previousBalance').firstChild.nodeValue = xml_str.getElementsByTagName("previous")[0].childNodes[0].nodeValue;
		document.getElementById('owes').firstChild.nodeValue = xml_str.getElementsByTagName("previous")[0].childNodes[0].nodeValue;



		document.checkoutForm.co_clientSelect.options.length = 0;

		var index = 1;
		if (xml_str.getElementsByTagName("person")[index] != null)
		{
			while (xml_str.getElementsByTagName("person")[index] != null)
			{
				g_key = xml_str.getElementsByTagName("person")[index].childNodes[0].childNodes[0].nodeValue;
				g_value = xml_str.getElementsByTagName("person")[index].childNodes[1].childNodes[0].nodeValue;
				eval("arx = " + g_value);

				document.checkoutForm.co_clientSelect.options[index - 1] = new Option(arx["label"] + ' (' + arx["relate"] + ')',g_key);
				if (g_key == key)
					document.checkoutForm.co_clientSelect.options[index - 1].selected = true;
				index++;
			}

			//document.getElementById('checkoutFormLabel').firstChild.nodeValue = arr["label"] + " (" + arr["fts"] + ")";
			
			document.getElementById('checkoutFormLabel').firstChild.nodeValue = arr["ltl"];
		}
		else
			document.getElementById('checkoutFormLabel').firstChild.nodeValue = arr["label"];

		guc_selected = true;


		/*
		*element_obj = document.clientInfoForm.ci_clientSelect;
			element_obj.options.length = 0;
			index = 0;
			for (i = 0; xml_str.childNodes[7].childNodes[i]; i++)
			{
			var group_key = xml_str.childNodes[7].childNodes[i].childNodes[0].childNodes[0].nodeValue;
			var group_value = xml_str.childNodes[7].childNodes[i].childNodes[1].childNodes[0].nodeValue;

			eval("var groupArray = " + group_value);
			element_obj.options[index] = new Option(groupArray["label"] + ' (' + groupArray["relate"] + ')',group_key);
			if (group_key == key)
				document.clientInfoForm.ci_clientSelect.options[i].selected = true;
			index++;
			}
		*/



		deleteAllRows(document.getElementById('tblCharges'));
		deleteAllRows(document.getElementById('tblCredits'));
		deleteAllRows(document.getElementById('tblPrevious'));

		//addRowToTable('tblCredits', 'No Payments Entered');

		numCharges = 0;
		numCredits = 0;

		index = 0;
		var elements = xml_str.getElementsByTagName("order");
		if (elements[index] != null)
		{
			while (elements[index] != null)
			{
				key = elements[index].childNodes[0].childNodes[0].nodeValue;
				value = elements[index].childNodes[1].childNodes[0].nodeValue;
				eval("arr = " + value);

				//alert("valuex >" + value);

				//addRowToTableNoEdit('tblPrevious', arr["desc"], arr["date"], arr["id"], arr["code"], arr["amount"], arr["qty"]);
				
				addRowToTableNoEdit('tblPrevious', arr["label"], arr["date"], arr["id"], '', arr["open"] == 'true' ? arr["balance"] : arr["total"], arr["id"], arr["open"], arr["reversed"], arr["legacy"]);

				index++;
			}

			adjustTableWidth('tblPrevious');
		}
		else
			addRowToTableNoEdit('tblPrevious', 'No Open Orders Found');





		index = 0;
		var elements = xml_str.getElementsByTagName("credit");
		if (elements[index] != null)
		{
			while (elements[index] != null)
			{
				key = elements[index].childNodes[0].childNodes[0].nodeValue;
				value = elements[index].childNodes[1].childNodes[0].nodeValue;
				eval("arr = " + value);

				//alert("valuex >" + value);

				addPaymentStuff('Charge To', 'Refund');
				addPaymentStuff('Credit Card', arr["amount"], '0', key, 'true');

				index++;
			}

			adjustTableWidth('tblCredits');
		}




		iteration = 1;


		index = 0;
		var elements = xml_str.getElementsByTagName("orderline");
		if (elements[index] != null) {
			while (elements[index] != null) {
				
				// do add item
				
				//addCode('tblCharges');
				
				key = elements[index].childNodes[0].childNodes[0].nodeValue;
				value = elements[index].childNodes[1].childNodes[0].nodeValue;
				eval("arr = " + value);

				//alert("valuexI >" + value);

				//addPaymentStuff('Charge To', 'Refund');
				//addPaymentStuff('Credit Card', arr["amount"], '0', key, 'true');
				
				/*
					String value = "{\"amount\" : \"" + checkout_code.getAmount().setScale(2, BigDecimal.ROUND_HALF_UP).toString() + "\"," +
							" \"code\" : \"" + checkout_code.getCode() + "\"," +
							" \"desc\" : \"" + checkout_code.getDescription() + "\"," +
							" \"code_id\" : \"" + checkout_code.getId() + "\"," +
							" \"qty\" : \"" + _orderline.getQuantity() + "\" }";
				 */
				
				addRowToTable('tblCharges', arr["desc"], arr["code_id"], arr["code"], arr["amount"], arr["qty"], arr["tax"],arr["type"]);
				numCharges++;
				
				index++;
			}

			adjustTableWidth('tblCredits');
		}
		else
			addRowToTable('tblCharges', 'No Charges Entered');
		
			



		document.checkoutForm.numPrevious.value=index;

		if (xml_str.getElementsByTagName("payment-plan-instance")[0] != null)
		{
			key = xml_str.getElementsByTagName("payment-plan-instance")[0].childNodes[0].childNodes[0].nodeValue;
			value = xml_str.getElementsByTagName("payment-plan-instance")[0].childNodes[1].childNodes[0].nodeValue;
			eval("arr = " + value);
			/*
				document.getElementById('plan').firstChild.nodeValue = arr["label"];
				document.getElementById('planStart').firstChild.nodeValue = arr["start"];
				document.getElementById('prepaidVisits').firstChild.nodeValue = arr["prepaid_visits"];
				document.getElementById('visitsUsed').firstChild.nodeValue = arr["visits_used"];
				document.getElementById('visitsRemaining').firstChild.nodeValue = arr["visits_remaining"];
				document.getElementById('perVisitCharge').firstChild.nodeValue = arr["visit_charge"];
				document.getElementById('amountPaid').firstChild.nodeValue = arr["amount_paid"];
				document.getElementById('escrowAmount').firstChild.nodeValue = arr["escrow"];
				document.getElementById('dropPlanCharge').firstChild.nodeValue = arr["drop_plan_charge"];
				*/
			plan_id = arr["practice_area_id"];
		}
		else
		{
			/*
			document.getElementById('plan').firstChild.nodeValue = 'Payment Plan Not Found';
			document.getElementById('planStart').firstChild.nodeValue = '';
			document.getElementById('prepaidVisits').firstChild.nodeValue = '';
			document.getElementById('visitsUsed').firstChild.nodeValue = '';
			document.getElementById('visitsRemaining').firstChild.nodeValue = '';
			document.getElementById('perVisitCharge').firstChild.nodeValue = '';
			document.getElementById('amountPaid').firstChild.nodeValue = '';
			document.getElementById('escrowAmount').firstChild.nodeValue = '';
			document.getElementById('dropPlanCharge').firstChild.nodeValue = '';
			*/
			plan_id = 0;
		}

		document.forms['checkoutForm'].discount_perc.value = '';
		//document.forms['checkoutForm'].shipping.value = '';
		document.forms['checkoutForm'].memo.value = '';
		document.forms['checkoutForm'].amount_1.value = '';
		document.forms['checkoutForm'].amount_2.value = '';
		document.forms['checkoutForm'].over_payment.checked = false;
		
		document.getElementById('charges').firstChild.nodeValue = '0.00';
		document.getElementById('credits').firstChild.nodeValue = '0.00';

		//addPreviousCharges();
		//addInitialCharges();
		//selectType();

		// reset payment method back to default (credit card)
		document.forms['checkoutForm'].method.value = 1;
		methodSelect();

		reverse_checked = false;
		
		document.checkoutForm.exp_month.value = '';
		document.checkoutForm.exp_year.value = '';
		document.checkoutForm.zip_code.value = '';
		document.checkoutForm.street_address.value = '';
		document.checkoutForm.cvc_code.value = '';

		is_sales_receipt = false;
		is_credit_memo = false;
		
		updateAmount();

		YAHOO.example.container.checkoutDialog.show();
	}
        
    function showPerson(xml_str)
    {
		var key = xml_str.childNodes[0].childNodes[0].nodeValue;
		var value = xml_str.childNodes[1].childNodes[0].nodeValue;

		//alert(key);
		//alert(value);

		eval("var personArray = " + value);

		document.getElementById('clientInfoFormLabel').firstChild.nodeValue = personArray["label"];
		document.getElementById('clientInfoFormClient').innerHTML = personArray["file"] + ' - <a href=\"clients-new.jsp?id=' + personArray["id"] + '\">' + personArray["label"] + '</a>';
		document.clientInfoForm.cid.value = personArray["id"];
		document.getElementById('clientInfoFormNext').firstChild.nodeValue = personArray["next"];

		if (paLabel == '')
			processCommand('pastep', last_key_ovr);
		else
			processCommand('paste', last_key_ovr);

		if (personArray["next"] == 'Not Scheduled')
			document.getElementById('clientInfoFormNext').innerHTML = 'Not Scheduled';
		else
		{
			if (paLabel == '')
			document.getElementById('clientInfoFormNext').innerHTML = '<a href=\"#\" onclick=\"processCommand(\'gotoNextApptP\',' + key + ');\">' + personArray["next"] + '</a>';
			else
			document.getElementById('clientInfoFormNext').innerHTML = '<a href=\"#\" onclick=\"processCommand(\'gotoNextAppt\',' + key + ');\">' + personArray["next"] + '</a>';
		}

		if (personArray["last"] == 'Not Scheduled')
			document.getElementById('clientInfoFormLast').innerHTML = 'Not Scheduled';
		else
		{
			if (paLabel == '')
			document.getElementById('clientInfoFormLast').innerHTML = '<a href=\"#\" onclick=\"processCommand(\'gotoLastApptP\',' + key + ');\">' + personArray["last"] + '</a>';
			else
			document.getElementById('clientInfoFormLast').innerHTML = '<a href=\"#\" onclick=\"processCommand(\'gotoLastAppt\',' + key + ');\">' + personArray["last"] + '</a>';
		}

		document.getElementById('clientInfoFormBalance').firstChild.nodeValue = personArray["bal"];
		document.getElementById('clientInfoFormAddr').firstChild.nodeValue = personArray["addr"];
		document.getElementById('clientInfoFormPhone').firstChild.nodeValue = personArray["ph"];
		document.getElementById('clientInfoFormEmail').firstChild.nodeValue = personArray["email"];
		document.getElementById('clientInfoFormEmailAnchor').href = 'mailto:' + personArray["email"];

		document.clientInfoForm.ci_subject.value = '';
		//document.clientInfoForm.ci_alert.checked = false;
		document.forms[1].elements['ci_alert'].checked = false;
		if (myEditor)
			myEditor.setEditorHTML('');
		else
			document.getElementById('info-editor').value = '';

		document.clientInfoForm.editorInput.value = personArray["cm"].replace(/&quot;/g,"\"").replace(/_/g,"\n");
		
		/*
		if (personArray["status"] == '1')
			document.clientInfoForm.statusInput[0].checked = true;
		else if (personArray["status"] == '2')
			document.clientInfoForm.statusInput[1].checked = true;
		else if (personArray["status"] == '3')
			document.clientInfoForm.statusInput[2].checked = true;
		*/

		for (var i = 0; document.forms[1].elements['contactInput'][i]; i++)
			document.forms[1].elements['contactInput'][i].checked = false;
		document.forms[1].elements['otherInput'].value = '';
		document.forms[1].elements['todoInput'][0].checked = false;
		document.forms[1].elements['todoInput'][1].checked = false;
		document.forms[1].elements['callOnInput'].value = '';
		document.forms[1].elements['eList1'].value = '1';
		document.forms[1].elements['eList2'].value = '1';
		//document.forms[1].elements['paList'].value = '0';
		//document.forms[1].elements['ppList'].value = '0';
		document.forms[1].elements['prn'].checked = false;

		if (xml_str.childNodes[2].childNodes[0])
		{
			var element_obj = document.clientInfoForm.eventList;
			element_obj.options.length = 0;
			var index = 0;
			for (i = 0; xml_str.childNodes[2].childNodes[i]; i++)
			{
			var contact_key = xml_str.childNodes[2].childNodes[i].childNodes[0].childNodes[0].nodeValue;
			var contact_value = xml_str.childNodes[2].childNodes[i].childNodes[1].childNodes[0].nodeValue;

			eval("var contactArray = " + contact_value);
			element_obj.options[index] = new Option(contactArray["label"],contact_key);
			index++;
			}
		}
		else
			document.clientInfoForm.eventList.options.length = 0;

		/*
				if (xml_str.childNodes[3].childNodes[0])
				{
					element_obj = document.clientInfoForm.toDoList;
					element_obj.options.length = 0;
						index = 0;
					for (i = 0; xml_str.childNodes[3].childNodes[i]; i++)
					{
					var toDo_key = xml_str.childNodes[3].childNodes[i].childNodes[0].childNodes[0].nodeValue;
					var toDo_value = xml_str.childNodes[3].childNodes[i].childNodes[1].childNodes[0].nodeValue;

					eval("var toDoArray = " + toDo_value);
					element_obj.options[index] = new Option(toDoArray["label"],toDo_key);
							index++;
					}
				}
				else
					document.clientInfoForm.toDoList.options.length = 0;
				*/

		if (xml_str.childNodes[4].childNodes[0])
		{
			element_obj = document.clientInfoForm.foqList;
			element_obj.options.length = 0;
			index = 0;
			for (i = 0; xml_str.childNodes[4].childNodes[i]; i++)
			{
				var foq_key = xml_str.childNodes[4].childNodes[i].childNodes[0].childNodes[0].nodeValue;
				var foq_value = xml_str.childNodes[4].childNodes[i].childNodes[1].childNodes[0].nodeValue;

				eval("var foqArray = " + foq_value);
				element_obj.options[index] = new Option(foqArray["label"],foq_key);
				index++;
			}
		}
		else
			document.clientInfoForm.foqList.options.length = 0;

		if (xml_str.childNodes[5].childNodes[0])
		{
			element_obj = document.clientInfoForm.ci_notesSelect;
			element_obj.options.length = 0;
			index = 0;
			for (i = 0; xml_str.childNodes[5].childNodes[i]; i++)
			{
				var note_key = xml_str.childNodes[5].childNodes[i].childNodes[0].childNodes[0].nodeValue;
				var note_value = xml_str.childNodes[5].childNodes[i].childNodes[1].childNodes[0].nodeValue;
				//alert(note_value);

				eval("var noteArray = " + note_value);
				element_obj.options[index] = new Option(noteArray["label"].replace(/&quot;/g,"\""),note_key);
				index++;
			}
		}
		else
		{
			document.clientInfoForm.ci_notesSelect.options.length = 0;
			document.clientInfoForm.ci_notesSelect.options[0] = new Option('-- NO NOTES ENTERED --','-1');
		}

		if (xml_str.childNodes[6].childNodes[0])
		{
			element_obj = document.clientInfoForm.reviewList;
			element_obj.options.length = 0;
			index = 0;
			for (i = 0; xml_str.childNodes[6].childNodes[i]; i++)
			{
			var review_key = xml_str.childNodes[6].childNodes[i].childNodes[0].childNodes[0].nodeValue;
			var review_value = xml_str.childNodes[6].childNodes[i].childNodes[1].childNodes[0].nodeValue;

			//alert(review_value);

			eval("var reviewArray = " + review_value);
			element_obj.options[index] = new Option(reviewArray["label"].replace(/&quot;/g,"\""),review_key);
			index++;
			}
		}
		else {
			document.clientInfoForm.reviewList.options.length = 0;
		}

		document.forms[1].elements['reviewOnInput'].value = '';
		document.forms[1].elements['followUpNote'].value = '';
		for (var i = 0; document.forms[1].elements['fuInput'][i]; i++)
			document.forms[1].elements['fuInput'][i].checked = false;

		if (xml_str.childNodes[7].childNodes[0])
		{
			element_obj = document.clientInfoForm.ci_clientSelect;
			element_obj.options.length = 0;
			index = 0;
			for (i = 0; xml_str.childNodes[7].childNodes[i]; i++)
			{
			var group_key = xml_str.childNodes[7].childNodes[i].childNodes[0].childNodes[0].nodeValue;
			var group_value = xml_str.childNodes[7].childNodes[i].childNodes[1].childNodes[0].nodeValue;

			eval("var groupArray = " + group_value);
			element_obj.options[index] = new Option(groupArray["label"] + ' (' + groupArray["relate"] + ')',group_key);
			if (group_key == key)
				document.clientInfoForm.ci_clientSelect.options[i].selected = true;
			index++;
			}
		}

		element_obj = document.clientInfoForm.ci_soapSelect;
		if (xml_str.childNodes[8].childNodes[0])
		{
			element_obj.options.length = 0;
			index = 0;
			for (i = 0; xml_str.childNodes[8].childNodes[i]; i++)
			{

			var note_key = xml_str.childNodes[8].childNodes[i].childNodes[0].childNodes[0].nodeValue;
			var note_value = xml_str.childNodes[8].childNodes[i].childNodes[1].childNodes[0].nodeValue;

			//alert('note_key >' + note_key);
			//alert("note_value >" + note_value);

			eval('var soapArray = ' + note_value);
			
			//alert("soapArrayX >" + soapArray["label"].replace(/&quot;/g,"\""));
			
			element_obj.options[index] = new Option(soapArray["label"].replace(/&quot;/g,"\""),note_key);
			index++;
			}
		}
		else
		{
			element_obj.options.length = 0;
			element_obj.options[0] = new Option('-- NO NOTES ENTERED --','-1');
		}

		document.clientInfoForm.s_note.value = '';
		document.clientInfoForm.o_note.value = '';
		document.clientInfoForm.a_note.value = '';
		document.clientInfoForm.p_note.value = '';

		if (xml_str.childNodes[9].childNodes[0])
		{
			document.clientInfoForm.history.value = xml_str.childNodes[9].childNodes[0].nodeValue;
		}

		//document.clientInfoForm.history.value = personArray["h"];

		YAHOO.example.container.dialog2.show();

		if (refresh_black_hole)
		{
			refresh_black_hole = false;
			processCommand('blackHole',document.getElementById('bhPaFilter').value);
		}

		/*
					YAHOO.example.calendar.cal1.addRenderer(personArray["as"], YAHOO.example.calendar.cal1.renderCellStyleHighlight1);
					YAHOO.example.calendar.cal1.render();
			*/
    }

    function showNote(xml_str)
    {
		var key = xml_str.childNodes[0].childNodes[0].childNodes[0].nodeValue;
		var value = xml_str.childNodes[0].childNodes[1].childNodes[0].nodeValue;

		//alert(key);
		//alert(value);

		eval("var noteArray = " + value);

		document.clientInfoForm.ci_subject.value = noteArray["subject"].replace(/&quot;/g,"\"");
		eval("document.clientInfoForm.ci_alert.checked = " + noteArray["show"]);
		if (myEditor)
			myEditor.setEditorHTML(noteArray["note"].replace(/&quot;/g,"\"").replace(/_/g,"\n"));
		else
			document.getElementById('info-editor').value = noteArray["note"].replace(/&quot;/g,"\"").replace(/_/g,"\n");
    }

    function showSoap(xml_str)
    {
		var key = xml_str.childNodes[0].childNodes[0].childNodes[0].nodeValue;
		var value = xml_str.childNodes[0].childNodes[1].childNodes[0].nodeValue;

		//alert(key);
		//alert(value);

		eval("var soapArray = " + value);

		document.clientInfoForm.s_note.value = soapArray["s"].replace(/&quot;/g,"\"").replace(/_/g,"\n");
		document.clientInfoForm.o_note.value = soapArray["o"].replace(/&quot;/g,"\"").replace(/_/g,"\n");
		document.clientInfoForm.a_note.value = soapArray["a"].replace(/&quot;/g,"\"").replace(/_/g,"\n");
		document.clientInfoForm.p_note.value = soapArray["p"].replace(/&quot;/g,"\"").replace(/_/g,"\n");

		document.clientInfoForm.paSOAPList.value = soapArray["pa"];
    }

	function showFollowup(xml_str)
	{
		var key = xml_str.childNodes[0].childNodes[0].nodeValue;
		var value = xml_str.childNodes[1].childNodes[0].nodeValue;
		//alert(value);
		eval("var followupArray = " + value);
		document.forms[1].rrCList.value = followupArray["reason"];
		document.forms[1].rpList.value = followupArray["practitioner"];
		document.forms[1].reviewInput[0].checked = true;
		document.forms[1].reviewOnInput.value = followupArray["date"];
		document.forms[1].followUpNote.value = followupArray["note"].replace(/&quot;/g,"\"");
	}

    function showAlerts(xml_str)
    {
		//alert('showAlerts() invoked >' + xml_str);
		var index = 0;
		var html_str = '';
		var person_str = '';
		while (xml_str.getElementsByTagName("item")[index] != null)
		{
			key = xml_str.getElementsByTagName("item")[index].childNodes[0].childNodes[0].nodeValue;
			value = xml_str.getElementsByTagName("item")[index].childNodes[1].childNodes[0].nodeValue;

			//alert(key);
			//alert(value);

			eval("var noteArray = " + value);

			if (html_str == '')
			html_str = ("<h2>" + noteArray["subject"] + "</h2><br />" + noteArray["note"]);
			else
			html_str += ("<br /><hr /><br /><h2>" + noteArray["subject"] + "</h2><br />" + noteArray["note"]);

			if (person_str == '')
			person_str = noteArray["person"];

			index++;
		}

		document.getElementById('reportDialogFormLabel').firstChild.nodeValue = person_str + ' Alert';
		document.getElementById('cos_div').innerHTML = html_str;
		YAHOO.example.container.reportDialog.show();
    }

    function fetchNote(id)
    {
		if (id > 0)
			processCommand('showNote',id);
    }

    function fetchSoap(id)
    {
		if (id > 0)
			processCommand('showSoap',id);
    }

    function soapSelect(note)
    {
		//var pa_element = document.getElementById('paSOAPList');

		selected_note = note;
		processCommand('soapSelect',document.forms[1].elements['paSOAPList'].value + "|" + note);
    }
	
	var soap_lvl = 0;

    function statementSelect(sel)
    {
		//alert(sel.options[sel.selectedIndex].text);

		//document.clientInfoForm.s_note.value = soapArray["s"];
		//document.clientInfoForm.o_note.value = soapArray["o"];
		//document.clientInfoForm.a_note.value = soapArray["a"];
		//document.clientInfoForm.p_note.value = soapArray["p"];

		if (sel.options[sel.selectedIndex].text == '..') {
			processCommand('statementUpSelect',sel.value);
			soap_lvl--;
		}
		else
		{
			var element_obj;
			if (selected_note == 'S')
				element_obj = document.clientInfoForm.s_note;
			else if (selected_note == 'O')
				element_obj = document.clientInfoForm.o_note;
			else if (selected_note == 'A')
				element_obj = document.clientInfoForm.a_note;
			else if (selected_note == 'P')
				element_obj = document.clientInfoForm.p_note;
			
			var indent_str = '';
			for (var i = soap_lvl; i > 0; i--)
				indent_str += '  ';

			if (element_obj.value.length > 0)
				element_obj.value += '\n' + indent_str + sel.options[sel.selectedIndex].text;
			else
				element_obj.value = sel.options[sel.selectedIndex].text;

			element_obj.tabIndex = 1;
			element_obj.focus();

			processCommand('statementSelect',sel.value);
			soap_lvl++;
		}
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

    function showBlackHole(element_obj,xml_str)
    {
        var index = 0;
        var changed = false;

        if (element_obj.length != xml_str.getElementsByTagName("person").length)
            changed = true;
        else
        {
            while (element_obj.options[index])
            {
                value = xml_str.getElementsByTagName("person")[index].childNodes[1].childNodes[0].nodeValue;
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
            while (xml_str.getElementsByTagName("person")[index] != null)
            {
                key = xml_str.getElementsByTagName("person")[index].childNodes[0].childNodes[0].nodeValue;
                value = xml_str.getElementsByTagName("person")[index].childNodes[1].childNodes[0].nodeValue;
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
	var del = false;

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
	    del = xml_str.getElementsByTagName("appt")[index].getAttribute("delete");

	    //if (xml_str.getElementsByTagName("appt")[index].getAttribute("delete"))
		//del = true;

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
			
			if (del)
			    appointmentArray[apptDate][key].splice(i,1);
			else
			    eval("appointmentArray[apptDate][key][i] = " + value);  // replace current contents

			break;
		    }
		}
		if (!exists && !del)
		    eval("appointmentArray[apptDate][key][" + appointmentArray[apptDate][key].length + "] = " + value);

		//alert(appointmentArray["05/21/08"][key][0]["label"]);

		var appt_html = "&nbsp;";
		var appt_id;
		var cancelled_or_rescheduled_appt = false;
		if (!del)
		{
		    //alert('a1');
		    for (i = 0; i < appointmentArray[apptDate][key].length; i++)
		    {
			//alert('a');
			cancelled_or_rescheduled_appt = false;
			var appt_cm = appointmentArray[apptDate][key][i]["cm"].replace(/&quot;/g,"\"").replace(/_/g,"\n");
			if (appt_cm == 'nva')
			    hl_appt = true;

			//alert('b');
			var appt_label = appointmentArray[apptDate][key][i]["label"];
			var first_label = appointmentArray[apptDate][key][i]["n"];
			//if (first_label && first_label == 'true')
			//    alert(first_label);
			var appt_cid = appointmentArray[apptDate][key][i]["cid"];
			//alert('c');
			appt_id = parseInt(appointmentArray[apptDate][key][i]["id"]);
			//alert('c >' + apptArray["type"]);
			//alert('c >' + typeArray[apptArray["type"]]);
			var type = parseInt(typeArray[apptArray["type"]]["type"]);
			//alert('d');
			var appt_image = "default.gif";

			if (type == appt_meeting_type)
			    appt_image = "meeting.gif";
			else
			{
			    var appt_state = parseInt(appointmentArray[apptDate][key][i]["state"]);
			    if (appt_state > 1)
			    {
				if (appt_state == 2)
				{
				    appt_image = "reschedule.gif";
				    cancelled_or_rescheduled_appt = true;
				}
				else if (appt_state == 3)
				    appt_image = "late.gif";
				else if (appt_state == 4)
				    appt_image = "checked-in.gif";
				else if (appt_state == 5)
				    appt_image = "checked-out.gif";
				else if (appt_state == 6)
				{
				    appt_image = "cancel.gif";
				    cancelled_or_rescheduled_appt = true;
				}
				else if (appt_state == 7)
				    appt_image = "no-show.gif";
			    }
			}

			var ahs = "";
			if (appt_cid == '0') // offtime
			{
			    if (show_off_time && (appt_label != 'Practitioner Off-Time'))
			    {
					if (cancelled_or_rescheduled_appt)
						ahs = "<img src=\"../images/" + appt_image + "\" border=\"0\" width=\"14\" height=\"14\" alt=\"\" onclick=\"editOtx('" + key + "');\" /><a style=\"color: black;\" onmouseout=\"clientOut();\" onmouseover=\"clientOvr(" + appt_cid + "," + appt_id + ");\" onclick=\"editClient(" + appt_cid + ");\" title=\"" + appt_cm + "\" >" + appt_label + "</a>";
					else
						ahs = "<img src=\"../images/" + appt_image + "\" border=\"0\" width=\"14\" height=\"14\" alt=\"\" onclick=\"editOtx('" + key + "');\" /><a onmouseout=\"clientOut();\" onmouseover=\"clientOvr(" + appt_cid + "," + appt_id + ");\" onclick=\"editClient(" + appt_cid + ");\" title=\"" + appt_cm + "\" >" + appt_label + "</a>";
			    }
			}
			else
			{
			    if (show_appts)
			    {
					//alert(typeArray[apptArray["type"]]["label"]);

					if (cancelled_or_rescheduled_appt)
						ahs = "<img src=\"../images/" + appt_image + "\" border=\"0\" width=\"14\" height=\"14\" alt=\"\" onclick=\"editAppt(" + appt_cid + ");\" /><a style=\"color: black;\" onmouseout=\"clientOut();\" onmouseover=\"clientOvr(" + appt_cid + "," + appt_id + ");\" onclick=\"editClient(" + appt_cid + ");\" title=\"" + appt_cm + "\" href=\"javascript:showClientInfo(" + appt_cid + ");\">" + appt_label + "</a>";
					else
					{
						var bd = appointmentArray[apptDate][key][i]["bd"];
						if (first_label && (first_label == 'true'))
							ahs = "<img src=\"../images/" + appt_image + "\" border=\"0\" width=\"14\" height=\"14\" alt=\"\" title=\"" + typeArray[apptArray["type"]]["label"] + "\" onclick=\"editAppt(" + appt_cid + ");\" /><a onmouseout=\"clientOut();\" onmouseover=\"clientOvr(" + appt_cid + "," + appt_id + ");\" onclick=\"editClient(" + appt_cid + ");\" title=\"" + appt_cm + "\" href=\"javascript:showClientInfo(" + appt_cid + ");\">" + appt_label + "</a>&nbsp;" + ((appt_cm != '') ? "&nbsp;<img src=\"../images/info.gif\" border=\"0\" width=\"14\" height=\"14\" alt=\"\" title=\"" + appt_cm + "\"/>" : "") + (bd ? "&nbsp;<img src=\"../images/bd.gif\" border=\"0\" width=\"14\" height=\"14\" alt=\"\" title=\"Birthday " + bd + "\"/>" : "") + "<img src=\"../images/exclamation.gif\" border=\"0\" width=\"14\" height=\"14\" alt=\"\" title=\"1st " + typeArray[apptArray["type"]]["palabel"] + " Appointment\"/>";
						else
							ahs = "<img src=\"../images/" + appt_image + "\" border=\"0\" width=\"14\" height=\"14\" alt=\"\" title=\"" + typeArray[apptArray["type"]]["label"] + "\" onclick=\"editAppt(" + appt_cid + ");\" /><a onmouseout=\"clientOut();\" onmouseover=\"clientOvr(" + appt_cid + "," + appt_id + ");\" onclick=\"editClient(" + appt_cid + ");\" title=\"" + appt_cm + "\" href=\"javascript:showClientInfo(" + appt_cid + ");\">" + appt_label + "</a>" + ((appt_cm != '') ? "&nbsp;<img src=\"../images/info.gif\" border=\"0\" width=\"14\" height=\"14\" alt=\"\" title=\"" + appt_cm + "\"/>" : "") + (bd ? "&nbsp;<img src=\"../images/bd.gif\" border=\"0\" width=\"14\" height=\"14\" alt=\"\" title=\"Birthday " + bd + "\"/>" : "");
					}
			    }
			    // <img src=\"../images/tr.gif\" border=\"0\" style=\"float:right;\">
			}

			if (appt_html == "&nbsp;")
			    appt_html = ahs;
			else
			    appt_html += "<br>" + ahs;
		    }
		}

		var hide_border = ((appt_label != 'Practitioner Off-Time') && (!cancelled_or_rescheduled_appt) && (!del));

		var element_obj = document.getElementById(key);
		if (!element_obj)
		{
		    //alert("unable to display appointment >" + key);
		}
		else
		{
		    var bg_color = typeArray[apptArray["type"]]["bg"];

		    if ((appt_html != '') && !hl_appt)
			element_obj.innerHTML = appt_html;

		    if (hide_border)
			element_obj.style.borderColor = bg_color;
		    else
			element_obj.style.borderColor = 'CCCCCC';

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
			var year=adj_date.getYear();
			//Y2K bug fix
			if (year < 1000)
			    year += 1900;
			document.getElementById('appt_date').value = weekday[adj_date.getDay()] + ", " + monthname[adj_date.getMonth()] + " " + adj_date.getDate() + ", " + year;

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
		    {
			if (!cancelled_or_rescheduled_appt && !del)
			    element_obj.style.backgroundColor = bg_color;
			else
			{
			    //element_obj.style.backgroundColor = null;
			    //element_obj.style.backgroundColor = 'FFFFFF';
			    var parent_obj = element_obj.parentNode;
			    element_obj.style.backgroundColor = parent_obj.style.backgroundColor;
			}

			if (apptArray["f"])
			    focus_element = element_obj;
		    }

		    //element_obj.style.color = typeArray[apptArray["type"]]["txt"]; // doesn't have a side effect

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

			if (element_obj)
			{
			    if (hide_border)
				element_obj.style.borderColor = bg_color;
			    else
				element_obj.style.borderColor = 'CCCCCC';

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
			    {
				if (!cancelled_or_rescheduled_appt && !del)
				    element_obj.style.backgroundColor = bg_color;
				else
				{
				    //element_obj.style.backgroundColor = null;
				    var parent_obj = element_obj.parentNode;
				    element_obj.style.backgroundColor = parent_obj.style.backgroundColor;
				}
			    }
			}

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
	{
	    focus_element.tabIndex = 1;
	    focus_element.focus();
	}

	forceRefresh = false;

	if (refresh_black_hole && document.getElementById('bhPaFilter'))
	    processCommand('blackHole',document.getElementById('bhPaFilter').value);

	//alert('fin');
    }
    
    function clearSchedule()
    {
	//alert("clearSchedule() invoked");
        
        /*
        if (!empty_cell)
            alert("empty_cell >" + empty_cell);
	*/


	
        for (key in scheduleArray)
        {
	    //alert(scheduleArray[key]);
            var element_obj = document.getElementById(scheduleArray[key]);
	    if (element_obj)
	    {
		var parent_obj = element_obj.parentNode;
		element_obj.style.backgroundColor = parent_obj.style.backgroundColor;
		element_obj.style.borderColor = parent_obj.style.borderColor;

		element_obj.style.borderTopStyle = parent_obj.style.borderTopStyle;
		element_obj.style.borderBottomStyle = parent_obj.style.borderBottomStyle;
		element_obj.style.borderLeftStyle = parent_obj.style.borderLeftStyle;
		element_obj.style.borderRightStyle = parent_obj.style.borderRightStyle;
		element_obj.style.borderWidth = parent_obj.style.borderWidth;

		element_obj.innerHTML = "&nbsp;";
	    }
	    //var parent_obj = element_obj.parentNode;
	    //element_obj.style.backgroundColor = parent_obj.style.backgroundColor;


            //element_obj.style.borderColor = 'CCC';
            //element_obj.style.backgroundColor = 'FFFFFF';
            //element_obj.innerHTML = "<img src='../images/spacer.gif' width='12' height='12' alt='' />&nbsp;";
            
            //element_obj.className="normalRow";
            //element_obj.className='highlightRow';
          
            //var parent_obj = element_obj.parentNode;
            
            //var new_element = document.getElementById("7:00 AM|8").cloneNode(true);
            //var new_element = empty_cell.cloneNode(true);
            //new_element.id = scheduleArray[key];
            //parent_obj.insertBefore(new_element, element_obj);
            //parent_obj.removeChild(element_obj);
        }


	/*
        var element_obj = document.getElementById("schedule");
        var parent_obj = element_obj.parentNode;
        parent_obj.removeChild(element_obj);
	var new_element = empty_table.cloneNode(true);
        parent_obj.appendChild(new_element);
	*/
        
       
        appointmentArray = new Array();
        scheduleArray = new Array();
        scheduleIndex = 0;
        idArray = new Array();
    }
    
    function cClk(pId,tStr,dow)
    {
	//alert('cClk');
		//alert(document.getElementById("tableContainer").scrollTop);

        if (editC)
            return;

	var selected_appt_type;

	if (!pId)
	{
		tStr = last_key_ovr.substring(0, last_key_ovr.indexOf("|"));
		if (selected_date.indexOf(" - ") != -1)
			dow = last_key_ovr.substring(last_key_ovr.indexOf("|") + 1);
	}
        
        if (dow)
        {
            var weekday = new Array("Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday");
            var monthname = new Array("January","February","March","April","May","June","July","August","September","October","November","December");
            var adj_date = new Date(selected_date.substring(0, selected_date.indexOf(" - ")));
            adj_date.setDate(adj_date.getDate() + (dow - 1));
	    var year=adj_date.getYear();
	    //Y2K bug fix
	    if (year < 1000)
		    year += 1900;
            //alert(selected_date.substring(0, selected_date.indexOf(" - ")));
            document.getElementById('appt_date').value = weekday[adj_date.getDay()] + ", " + monthname[adj_date.getMonth()] + " " + adj_date.getDate() + ", " + year;
            document.forms[0].pSelect.value='1';
        }
        else
        {
            document.getElementById('appt_date').value = selected_date;
            document.forms[0].pSelect.value='0';
        }
        
        document.forms[0].hr.value=tStr.substring(0,tStr.indexOf(":"));
        document.forms[0].mn.value=tStr.substring(tStr.indexOf(":")+1,tStr.indexOf(" "));
        document.forms[0].ampm.value=tStr.substring(tStr.indexOf(" ")+1);
        
        document.forms[0].lastname.value = '';
        document.forms[0].firstname.value = '';
        document.forms[0].filenumber.value = '';
        
        if ((edit == 0) && (editOt == 0) && pId)
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
			if (!pId)
				pId = last_key_ovr.substring(last_key_ovr.indexOf("|") + 1);

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
				if (edit == 0)
					edit = editB;
                if (dow)
                    key = tStr + "|" + dow + "|" + edit;
                else
                    key = tStr + "|" + pId + "|" + edit;
            }

            if (idArray[key])
            {
                document.getElementById('apptFormLabel').firstChild.nodeValue = 'Edit Appointment';

                eval("var apptArray = " + idArray[key]);

                document.forms[0].appointmentSelect.value = parseInt(apptArray["id"]);
                document.forms[0].statusSelect.value = parseInt(apptArray["state"]);

                document.forms[0].clientSelect.options[0] = new Option(apptArray["label"],apptArray["cid"]);
                document.forms[0].clientSelect.selectedIndex = 0;

				selected_appt_type = apptArray["type"];
                document.forms[0].duration.value = apptArray["duration"];
                document.forms[0].comments.value = apptArray["cm"].replace(/&quot;/g,"\"").replace(/_/g,"\n");
                
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
		document.forms[0].appointmentSelect.value=-1;
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

	if (dow)
	    document.getElementById('practitionerSelect').value = practitionerArrayId;
	else
	    document.getElementById('practitionerSelect').value = pId;

	showApptTypes(document.getElementById('practitionerSelect').value);
	if (selected_appt_type)
	    document.forms[0].typeSelect.value = selected_appt_type;

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
		editB = cid;
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
    
    function remove(arg)
    {
	if (editA2 != 0)
	{
	    if (confirm("Delete?"))
	    {
		if (arg)
		    processCommand('deleteP', editA2);
		else
		    processCommand('delete', editA2);
	    }
	}
    }
    
    function appointmentReport(arg)
    {
		YAHOO.example.container.panel4.show();

	
		if (arg)
		{
			//processCommand('apptReport', arg);
			document.clientInfoForm.ar_arg.value = arg;
		}
		else
		{
			if (editA2 != 0)
			{
				//processCommand('apptReport', editA2);
				document.clientInfoForm.ar_arg.value = editA2;
			}
		}
    }

    function acceptAppointmentReport()
    {
	   YAHOO.example.container.panel4.hide();
	   processCommand('apptReport', document.clientInfoForm.ar_arg.value + "|" + document.clientInfoForm.ar_start_dt.value + "|" + document.clientInfoForm.ar_end_dt.value);
    }

    function checkoutSheet()
    {
        if (editA2 != 0)
            processCommand('checkOutSheet', editA2);
    }

    function lastReceipt()
    {
        if (editA2 != 0)
            processCommand('lastReceipt', editA2);
    }
    
	function addCode(tbl,ret)
	{
		var index_value = document.checkoutForm.codeSelect.value;
		if (index_value)
		{
			//if (tbl == 'tblCharges')
			//{
				if (numCharges == 0)
					deleteAllRows(document.getElementById('tblCharges'));
				numCharges++;
			//}
			//else
			//{
			//	if (numCredits == 0)
			//		deleteAllRows(document.getElementById('tblCredits'));
			//	numCredits++;
			//}

			//alert(index_value);
			//alert(codeArray[index_value]);
			//alert(codeArray[index_value]["practice_area"]);
			//alert(codeArray[index_value]["tax"]);

			var amount = codeArray[index_value]["amount"];

			//alert(plan_id);

			if (plan_id > 0)
			{
				if (codeArray[index_value]["practice_area"] == plan_id)
				{
					amount = "PLAN";
				//alert('no charge for this checkout code');
				}
			}

			// see if this code is already present

			//alert(codeArray[index_value]["type"]);

			var existing_qty = false;
			var table_obj = document.getElementById('tblCharges');
			if (codeArray[index_value]["type"] != 1 && (1 == 2)) // disable this and take it back to the drawing board...
			{
				for (var i = 0; table_obj.tBodies[0].rows[i]; i++)
				{
					row_code_id = table_obj.tBodies[0].rows[i].myRow.four.value;
					if (row_code_id == codeArray[index_value]["id"])
					{
						// if this is not a ret and the current qty is negative, assume corrective entry, I want to add another row

						var current_qty = eval("document.checkoutForm.qty" + (i + 1) + "tblCharges.value");
						//alert("current_qty >" + current_qty);

						if ((ret && (current_qty < 0)) || (!ret && (current_qty > 0)))
						{
							eval("document.checkoutForm.qty" + (i + 1) + "tblCharges.value = parseInt(document.checkoutForm.qty" + (i + 1) + "tblCharges.value) " + ((ret) ? '-' : '+') + " 1");
							updateAmount();
							existing_qty = true;
						}
					}
				}
			}
			if (!existing_qty) {
				addRowToTable(tbl, codeArray[index_value]["desc"], codeArray[index_value]["id"], codeArray[index_value]["code"], amount, (ret ? -1 : 1), codeArray[index_value]["tax"], codeArray[index_value]["type"]);
			}
		}
		else
			alert('You must select a code to add.');
	}
    
    function myRowObject(one, two, three, four, five)
    {
        this.one = one;
        this.two = two;
        this.three = three;
        this.four = four;
        this.five = five;
    }

	function addRowToTable(tname, desc, id, code, amount, qty, tax, type)
	{
		
		var tbl = document.getElementById(tname);
		
		//alert('addRowToTable invoked >' + tbl.tBodies[0].rows.length);

		
		var nextRow = tbl.tBodies[0].rows.length;
		//var iteration = nextRow + 1;

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
			tbl.tBodies[0].rows[0].cells[0].style.width = 516;
			return;
		}

		// cell 0 - text
		/*
		cell0 = row.insertCell(0);
		if (tname == 'tblCharges')
			cell0.setAttribute('id', 'ch' + iteration);
		else
			cell0.setAttribute('id', 'cr' + iteration);
		textNode = document.createTextNode(iteration);
		cell0.appendChild(textNode);
		*/

		if (!qty)
			qty = 1;

		// cell 0
		if (tname == 'tblCharges')
		{
			cell0 = row.insertCell(0);
			var qtyInp = document.createElement('input');
			qtyInp.setAttribute('type', 'text');
			qtyInp.setAttribute('name', 'qty' + iteration + tname);
			qtyInp.setAttribute('size', '2');
			qtyInp.setAttribute('style', 'text-align:right; width: 24px;');
			qtyInp.setAttribute('value', qty);
			qtyInp.onkeyup = function () {updateAmount(1)};
			qtyInp.onfocus = function () {qtyInp.select()};
			cell0.appendChild(qtyInp);
		}
		else
		{
			cell0 = row.insertCell(0);
			cell0.setAttribute('id', 'cr' + iteration);
			textNode = document.createTextNode(iteration);
			cell0.appendChild(textNode);
		}

		// cell 1 - text
		var cell1 = row.insertCell(1);
		//textNode = document.createTextNode(code + " " + desc);
		textNode = document.createTextNode(desc);
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
			txtInp.setAttribute('style', 'text-align:right; width: 54px;');
			txtInp.setAttribute('value', amount);
			txtInp.onkeyup = function () {updateAmount(1)};
			txtInp.onfocus = function () {txtInp.select()};
			cell2.appendChild(txtInp);
			hiddenInp.setAttribute('type', 'hidden');
			hiddenInp.setAttribute('name', 'hidden' + iteration + tname);
			hiddenInp.setAttribute('value', id);
			cell2.appendChild(hiddenInp);
			if (type == '7' || type == '8')
			{
				hiddenGiftCardNumber = document.createElement('input');
				hiddenGiftCardNumber.setAttribute('type', 'hidden');
				if (type == '7')
					hiddenGiftCardNumber.setAttribute('name', 'hiddenGiftCardNumber' + iteration + tname);
				else
					hiddenGiftCardNumber.setAttribute('name', 'hiddenGiftCertNumber' + iteration + tname);
				cell2.appendChild(hiddenGiftCardNumber);
			}
			

			if (tname == 'tblCharges')
			{
				// cell 3 - input select
				var cell3 = row.insertCell(3);
				var sel1 = document.createElement('select');
				sel1.setAttribute('style', 'width: 110px;');
				sel1.name = 'sel' + iteration + tname;
				sel1.options[0] = new Option('none', 0);
				var x=1;
				for (i in practitionerArray) {
					sel1.options[x] = new Option(practitionerArray[i]["label"], i);
					if (i == default_practitioner)
						sel1.options[x].selected = true;
					x++;
				}
				cell3.appendChild(sel1);
			}
		}

		// cell 4 - input button
		var cell4 = row.insertCell(4);
		var btnEl = document.createElement('input');
		btnEl.setAttribute('type', 'button');
		btnEl.setAttribute('value', 'Delete');
		btnEl.onclick = function () {
			deleteCurrentRow(this, tname)
			};
		cell4.appendChild(btnEl);

		if (tname == 'tblCharges')
		{
		// cell 5 - input check
		//var cell5 = row.insertCell(5);
		//var checkInp = document.createElement('input');
		//checkInp.setAttribute('type', 'checkbox');
		//checkInp.setAttribute('name', 'inputCheck' + iteration + tname);
		//checkInp.setAttribute('size', '8');
		//cell5.appendChild(checkInp);
		}

		// Pass in the elements you want to reference later
		// Store the myRow object in each row
		//row.myRow = new myRowObject(textNode, txtInp, cbEl, raEl);
		row.myRow = new myRowObject(textNode, txtInp, qtyInp, hiddenInp, tax);

		adjustTableWidth(tname);
		updateAmount();

		iteration++;

		if (type == '7' || type == '8')
		{
			if (type == '7')
			{
				document.getElementById('panel3_hd_txt').firstChild.nodeValue='Gift Card';
				document.getElementById('panel3_bd_txt').firstChild.nodeValue='Swipe Gift Card Now';
				document.getElementById('panel3_lb_txt').firstChild.nodeValue='Gift Card Number';
				document.getElementById('panel3_ft_txt').firstChild.nodeValue='Swipe Gift Card Now';
			}
			else
			{
				document.getElementById('panel3_hd_txt').firstChild.nodeValue='Gift Certificate';
				document.getElementById('panel3_bd_txt').firstChild.nodeValue='Enter Gift Certificate Number';
				document.getElementById('panel3_lb_txt').firstChild.nodeValue='Gift Certificate Number';
				document.getElementById('panel3_ft_txt').firstChild.nodeValue='Enter Gift Certificate Number';
			}

			document.checkoutForm.gift_card_number.value = '';
			YAHOO.example.container.panel3.show();
			document.checkoutForm.gift_card_number.focus();
		}
	}

    function adjustTableWidth(tname)
    {
		var tbl = document.getElementById(tname);
		if (tbl.tBodies[0].rows[0])
		{
			if (tname == 'tblCharges')
			{
				tbl.tBodies[0].rows[0].cells[0].style.width = 26;
				tbl.tBodies[0].rows[0].cells[1].style.width = 227;
				tbl.tBodies[0].rows[0].cells[2].style.width = 60;
				tbl.tBodies[0].rows[0].cells[3].style.width = 113;
				tbl.tBodies[0].rows[0].cells[4].style.width = 62;
			}
			else if (tname == 'tblCredits')
			{
				tbl.tBodies[0].rows[0].cells[0].style.width = 112;
				tbl.tBodies[0].rows[0].cells[1].style.width = 79;
				tbl.tBodies[0].rows[0].cells[2].style.width = 79;
				tbl.tBodies[0].rows[0].cells[3].style.width = 67;
			}
			else
			{
				tbl.tBodies[0].rows[0].cells[0].style.width = 39;
				tbl.tBodies[0].rows[0].cells[1].style.width = 60;
				tbl.tBodies[0].rows[0].cells[2].style.width = 242;
				tbl.tBodies[0].rows[0].cells[3].style.width = 68;
				tbl.tBodies[0].rows[0].cells[4].style.width = 37;
				
				//tbl.tBodies[0].rows[0].cells[5].style.width = 56;
				
				tbl.tBodies[0].rows[0].cells[5].style.width = 40;
			}
		}
    }
    
    function addRowToTableNoEdit(tname, desc, date, id, code, amount, qty, open, reversed, legacy)
    {
		var tbl = document.getElementById(tname);
		var nextRow = tbl.tBodies[0].rows.length;
		var iteration = nextRow + 1;

		num = nextRow;

		// add the row
		var row = tbl.tBodies[0].insertRow(num);
		var textNode;
		var cell0;

		// CONFIG: requires classes named classy0 and classy1
		row.className = 'classy' + (iteration % 2);

		if (desc == 'No Open Orders Found')
		{
			// cell 0 - text
			cell0 = row.insertCell(0);
			textNode = document.createTextNode(desc);
			cell0.style.textAlign = 'left';
			cell0.colSpan = '5';
			cell0.appendChild(textNode);
			tbl.tBodies[0].rows[0].cells[0].style.width = 518;
			return;
		}

		// CONFIG: This whole section can be configured

		// cell 0 - text
		cell0 = row.insertCell(0);
		textNode = document.createTextNode(qty);
		cell0.style.verticalAlign = 'top';
		cell0.appendChild(textNode);

		// cell 1 - text
		var cell1 = row.insertCell(1);
		textNode = document.createTextNode(date);
		cell1.style.verticalAlign = 'top';
		cell1.appendChild(textNode);

		// cell 2 - text
		var cell2 = row.insertCell(2);
		textNode = document.createTextNode(code + " " + desc);
		cell2.style.textAlign = 'left';
		cell2.appendChild(textNode);

		// cell 3 - text
		var cell3 = row.insertCell(3);
		textNode = document.createTextNode(amount);
		cell3.style.textAlign = 'right';
		cell3.style.verticalAlign = 'top';
		cell3.appendChild(textNode);
		var hiddenAmount = document.createElement('input');
		hiddenAmount.setAttribute('type', 'hidden');
		hiddenAmount.setAttribute('name', 'hiddenAmount' + iteration + tname);
		hiddenAmount.setAttribute('value', amount);
		cell3.appendChild(hiddenAmount);

		// cell 4 - input check
		var cell4 = row.insertCell(4);
		cell4.style.verticalAlign = 'top';
		var checkInp = document.createElement('input');
		checkInp.setAttribute('type', 'checkbox');
		checkInp.setAttribute('name', 'inputCheck' + iteration + tname);
		checkInp.setAttribute('size', '8');
		checkInp.setAttribute('title', 'Pay Account Balance For Order');
		checkInp.onclick = function () {payPreviousOrder(this, tname)};
		if (open == 'true')
		{
			cell4.appendChild(checkInp);
			var hiddenInp = document.createElement('input');
			hiddenInp.setAttribute('type', 'hidden');
			hiddenInp.setAttribute('name', 'hidden' + 'inputCheck' + iteration + tname);
			hiddenInp.setAttribute('value', id);
			cell4.appendChild(hiddenInp);
		}
		
		if (legacy == 'true')
		{
			var hiddenInp3 = document.createElement('input');
			hiddenInp3.setAttribute('type', 'hidden');
			hiddenInp3.setAttribute('name', 'hidden' + 'legacyInvoice' + iteration + tname);
			hiddenInp3.setAttribute('value', id);
			cell4.appendChild(hiddenInp3);
		}

		// cell 5 - input check
		var cell5 = row.insertCell(5);
		cell5.style.verticalAlign = 'top';
		var checkInp2 = document.createElement('input');
		checkInp2.setAttribute('type', 'checkbox');
		checkInp2.setAttribute('name', 'reverseCheck' + iteration + tname);
		checkInp2.setAttribute('size', '8');
		checkInp2.setAttribute('title', 'This Never Happened');
		checkInp2.onclick = function () {reverseOrder(this, tname)};
		if (reversed != 'true' && legacy != 'true')
		{
			cell5.appendChild(checkInp2);
			var hiddenInp2 = document.createElement('input');
			hiddenInp2.setAttribute('type', 'hidden');
			hiddenInp2.setAttribute('name', 'hidden' + 'reverseCheck' + iteration + tname);
			hiddenInp2.setAttribute('value', id);
			cell4.appendChild(hiddenInp2);
		}

		row.myRow = new myRowObject(textNode, hiddenAmount, checkInp, checkInp2);

		updateAmount();
    }
    
    function deleteCurrentRow(obj, tname)
    {
		var delRow = obj.parentNode.parentNode;
		var tbl = delRow.parentNode.parentNode;
		var rIndex = delRow.sectionRowIndex;
		var rowArray = new Array(delRow);
		deleteRows(rowArray);
		reorderRows(tbl, rIndex, tname);

		adjustTableWidth(tname);
		updateAmount();
    }

	function voidCharge(v1)
	{
		//alert(v1);
		processCommand('voidCharge',v1);
		deleteAllRows(document.getElementById('tblCredits'));
		updateAmount();
	}

    function payPreviousOrder(obj, tname)
    {
		//alert('clicky click >' + obj.checked);
		updateAmount();
    }

	function reverseOrder(obj, tname)
	{
		reverse_checked = true;
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



					//tbl.tBodies[0].rows[i].cells[0].innerHTML = count;
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
    
    function updateAmount(x)
	{
		var table_1_subtotal = 0;
		var table_1_tax = 0;
		var table_2_amount = 0;
		var table_3_amount = 0;
		var table_3_account_amount = 0;
		var row_amount = 0;
		var row_amount_b = 0;
		var row_qty = 0;

		var discount_perc = parseFloat(document.forms['checkoutForm'].discount_perc.value) ? parseFloat(document.forms['checkoutForm'].discount_perc.value) : 0;
		if (discount_perc > 100)
			discount_perc = 100;
		
		var client_taxable = document.forms['checkoutForm'].client_taxable.value == '1';

		//var shipping = parseFloat(document.forms['checkoutForm'].shipping.value) ? parseFloat(document.forms['checkoutForm'].shipping.value) : 0;

		var table_obj = document.getElementById('tblCharges');
		for (var i = 0; table_obj.tBodies[0].rows[i]; i++)
		{
			if (table_obj.tBodies[0].rows[i].myRow)
			{
				row_amount = table_obj.tBodies[0].rows[i].myRow.two.value;
				row_qty = table_obj.tBodies[0].rows[i].myRow.three.value;
				row_qty = parseInt(row_qty) ? parseInt(row_qty) : 0;

				if (is_sales_receipt && (row_qty < 0))
					alert('Can\'t add return to sales receipt.  Please ring returns separately from sales.');
				else if (is_credit_memo && (row_qty > 0))
					alert('Can\'t add sales to return receipt.  Please ring sales separately from returns.');

				row_amount = parseFloat(row_amount) ? (parseFloat(row_amount) * row_qty) : 0;

				row_amount = row_amount - (row_amount * discount_perc / 100);

				table_1_subtotal += row_amount;

				//alert('table_obj.tBodies[0].rows[i].myRow.five >' + table_obj.tBodies[0].rows[i].myRow.five);
				//table_1_tax += parseFloat(table_obj.tBodies[0].rows[i].myRow.five) * table_1_subtotal; // <-- incorrect...
				if (client_taxable)
					table_1_tax += parseFloat(table_obj.tBodies[0].rows[i].myRow.five) * row_amount; // <-- incorrect...
				//alert('table_1_tax >' + table_1_tax);
			}
		}
		//table_1_subtotal = Math.round(table_1_subtotal * 100) / 100;
		table_1_subtotal = roundMoney(table_1_subtotal);
		table_1_tax = Math.round(table_1_tax) / 100;

		table_obj = document.getElementById('tblPrevious');
		for (i = 0; table_obj.tBodies[0].rows[i]; i++)
		{
			if (table_obj.tBodies[0].rows[i].myRow)
			{
				//row_qty = table_obj.tBodies[0].rows[i].myRow.three.value;
				//if (row_qty == 'on')
				if (table_obj.tBodies[0].rows[i].myRow.three.checked == true)
				{
					row_amount = table_obj.tBodies[0].rows[i].myRow.two.value;
					row_amount = parseFloat(row_amount) ? (parseFloat(row_amount) * 1) : 0;
					table_2_amount += row_amount;
				}
			}
		}
		table_2_amount = roundMoney(table_2_amount);

		table_obj = document.getElementById('tblCredits');
		for (i = 0; table_obj.tBodies[0].rows[i]; i++)
		{
			if (table_obj.tBodies[0].rows[i].myRow)
			{
				//alert(table_obj.tBodies[0].rows[i].myRow.one);
				
				row_amount = table_obj.tBodies[0].rows[i].myRow.two;
				row_amount_b = table_obj.tBodies[0].rows[i].myRow.three;

				row_amount = parseFloat(row_amount) ? (parseFloat(row_amount) * 1) : 0;
				row_amount_b = parseFloat(row_amount_b) ? (parseFloat(row_amount_b) * 1) : 0;

				table_3_amount += (row_amount - row_amount_b);
				if (table_obj.tBodies[0].rows[i].myRow.one == 'Account')
					table_3_account_amount += (row_amount - row_amount_b);
			}
		}
		//table_3_amount = Math.round(table_3_amount * 100) / 100;
		table_3_amount = roundMoney(table_3_amount);
		//table_3_account_amount = Math.round(table_3_account_amount * 100) / 100;
		table_3_account_amount = roundMoney(table_3_account_amount);

		//total_charges = table_1_subtotal + table_2_amount + table_1_tax + shipping;
		total_charges = table_1_subtotal + table_2_amount + table_1_tax;  // I'm thinking that paying for previous stuff should not get added to current charges
		total_charges_current_only = table_1_subtotal + table_1_tax ;
		total_credits = table_3_amount;

		document.forms['checkoutForm'].h_subtotal.value = (table_1_subtotal + table_2_amount);
		document.getElementById('subtotal').firstChild.nodeValue = (table_1_subtotal + table_2_amount).formatMoney(2, '.', ',');
		document.forms['checkoutForm'].h_tax.value = table_1_tax;
		document.getElementById('tax').firstChild.nodeValue = table_1_tax.formatMoney(2, '.', ',');
		document.forms['checkoutForm'].h_total.value = total_charges;
		document.getElementById('total').firstChild.nodeValue = total_charges.formatMoney(2, '.', ',');

		document.forms['checkoutForm'].h_previousBalance.value = previous_balance;
		document.forms['checkoutForm'].h_charges.value = total_charges;
		document.getElementById('charges').firstChild.nodeValue = total_charges.formatMoney(2, '.', ',');
		document.forms['checkoutForm'].h_credits.value = total_credits;
		document.getElementById('credits').firstChild.nodeValue = total_credits.formatMoney(2, '.', ',');

		//client_owes = Math.round((previous_balance + total_charges - (total_credits - table_3_account_amount)) * 100) / 100;
		//client_owes = Math.round((previous_balance + table_1_subtotal + table_1_tax - (total_credits - table_3_account_amount)) * 100) / 100;
		//client_owes = roundMoney(previous_balance + table_1_subtotal + table_1_tax - (total_credits - table_3_account_amount));
		//client_owes = roundMoney(previous_balance + total_charges_current_only - (total_credits - table_3_account_amount));
		client_owes = roundMoney(previous_balance + total_charges - (total_credits - table_3_account_amount));
		document.forms['checkoutForm'].h_owes.value = client_owes;
		document.getElementById('owes').firstChild.nodeValue = client_owes.formatMoney(2, '.', ',');
		
		if (document.forms['checkoutForm'].over_payment.checked == false)
		{
			if ((total_charges - total_credits) > 0)
			{
				document.forms['checkoutForm'].amount_1.value = (total_charges - total_credits).formatMoney(2, '.', ',');
				document.forms['checkoutForm'].amount_2.value = '';
				if (!x)
					document.forms['checkoutForm'].amount_1.select();
			}
			else if ((total_credits - total_charges) > 0)
			{
				document.forms['checkoutForm'].amount_2.value = (Math.abs(total_charges - total_credits)).formatMoney(2, '.', ',');
				document.forms['checkoutForm'].amount_1.value = '';
				if (!x)
					document.forms['checkoutForm'].amount_2.select();
			}
			else
				document.forms['checkoutForm'].amount_2.value = '';
		}
		else
			document.forms['checkoutForm'].amount_2.value = '';
	}
	
	function roundMoney(amt)
	{
		return Math.round(parseFloat((amt * 100).toFixed(2))) / 100;
	}

    function methodSelect()
    {
		switch (document.checkoutForm.method.value)
		{
			case '1':
			{
				document.getElementById('method_1').firstChild.nodeValue = 'Charge To';
				document.getElementById('method_2').firstChild.nodeValue = 'Refund';
				break;
			}
			case '2': case '3':
			{
				document.getElementById('method_1').firstChild.nodeValue = 'Tendered';
				document.getElementById('method_2').firstChild.nodeValue = 'Change';
				break;
			}
			case '4':
			{
				document.getElementById('method_1').firstChild.nodeValue = 'Charge To';
				document.getElementById('method_2').firstChild.nodeValue = 'Payment';
				break;
			}
			case '5': case '6':
			{
				document.getElementById('method_1').firstChild.nodeValue = 'Redeem';
				document.getElementById('method_2').firstChild.nodeValue = 'Sell';
				break;
			}
		}
    }

    function acceptCreditCard()
    {
		//alert('acceptCreditCard() invoked - ' + document.checkoutForm.credit_card_number.value);
		
		if (!doCCRefresh)
		{
			//oPushButton22.disabled(true);
			
			if (document.checkoutForm.credit_card_number.value != credit_card_number)
			{
				credit_card_number = document.checkoutForm.credit_card_number.value;
				if (cardValid(document.checkoutForm.credit_card_number.value))
				{
					if (document.checkoutForm.co_clientSelect.value)
					{
						if (document.checkoutForm.credit_card_number.value == '')
							alert('Please scan a card or enter a card number.');
						else
						{
							amount_1 = document.checkoutForm.amount_1.value;
							amount_2 = document.checkoutForm.amount_2.value;
							amount_1_valid = amount_1 != '' && !isNaN(amount_1) && (parseFloat(amount_1) > 0);
							amount_2_valid = amount_2 != '' && !isNaN(amount_2) && (parseFloat(amount_2) > 0);
							
							var valid = false;
							if (amount_2_valid) {
								amount_2 = document.checkoutForm.charge_amount.value;
								amount_2_valid = amount_2 != '' && !isNaN(amount_2);
								valid = amount_2_valid;
							}
							else {
								amount_1 = document.checkoutForm.charge_amount.value;
								amount_1_valid = amount_1 != '' && !isNaN(amount_1);
								valid = amount_1_valid;
							}
							
							if (!valid)
								alert('Please specify a valid charge amount.');
							else
							{
								var accept_str = document.checkoutForm.co_clientSelect.value + "|" + document.checkoutForm.charge_amount.value + "|" + document.checkoutForm.credit_card_number.value + "|" + document.checkoutForm.exp_month.value + "|" + document.checkoutForm.exp_year.value + "|" + document.checkoutForm.zip_code.value + "|" + document.checkoutForm.street_address.value + "|" + document.checkoutForm.cvc_code.value + "|" + amount_2_valid;
								processCommand('acceptCreditCard',escape(accept_str));
							}
						}
					}
					else
					{
						alert('Please select a client.');
					}
				}
			}
		}
    }

	function cardValid(swipe_string)
	{
		if (getCardID(swipe_string) == -1)
		{
			var index_1 = swipe_string.indexOf(';');
			var index_2 = swipe_string.indexOf('?', index_1);

			if ((swipe_string.indexOf('%') == 0) && (index_1 > -1) && (index_2 > -1))
				return true;

			return false;
		}
		else
			return true;
	}


  /**
   * Get the Card type
   * returns the credit card type
   *      INVALID          = -1;
   *      VISA             = 0;
   *      MASTERCARD       = 1;
   *      DISCOVER	       = 2;
   *      AMERICAN_EXPRESS = 3;
   *      EN_ROUTE         = 4;
   *      DINERS_CLUB      = 5;
   */
	function getCardID(card_number) {

		var valid = -1;

		if (card_number && card_number.length > 3)
		{
			var digit1 = parseInt(card_number.substring(0,1));
			var digit2 = parseInt(card_number.substring(0,2));
			var digit3 = parseInt(card_number.substring(0,3));
			var digit4 = parseInt(card_number.substring(0,4));

			if (!isNaN(card_number)) {
				/* ----
				 ** VISA  prefix=4
				 ** ----  length=13 or 16  (can be 15 too!?! maybe)
				 */
				if (digit1 == 4)  {
					if (card_number.length == 13 || card_number.length == 16)
						valid = 0;
				}
				/* ----------
				 ** MASTERCARD  prefix= 51 ... 55
				 ** ----------  length= 16
				 */
				else if ((digit2 >= 51) && (digit2 <= 55)) {
					if (card_number.length == 16)
						valid = 1;
				}
				/* ----
				 ** DISCOVER card prefix = 60
				 ** --------      length = 16
				 */
				else if ((digit2 == 60) || (digit2 == 66)) {
					if (card_number.length == 16)
						valid = 2;
				}
				/* ----
				 ** AMEX  prefix=34 or 37
				 ** ----  length=15
				 */
				else if (digit2 == 34 || digit2 == 37 || digit4 == 2149) {
					if (card_number.length == 15)
						valid = 3;
				}
				/* -----
				 ** ENROU prefix=2014 or 2149
				 ** ----- length=15
				 */
				else if (digit4 == 1800 || digit4 == 2014) {
					if (card_number.length == 15)
						valid = 4;
				}
				/* -----
				 ** DCLUB prefix=300 ... 305 or 36 or 38
				 ** ----- length=14
				 */
				else if (digit2 == 36 ||
					(digit3 >= 300 && digit3 <= 305)) {
					if (card_number.length == 14)
						valid = 5;
				}
			}
		}

		return valid;

    }

    function acceptCheck()
    {
		if (document.checkoutForm.co_clientSelect.value)
		{
			if (document.checkoutForm.check_number.value == '')
				alert('Please enter a check number.');
			else
			{
				if (amount_1_valid)
				{
					amount_1 = document.checkoutForm.check_amount.value;
					amount_1_valid = amount_1 != '' && !isNaN(amount_1);
				}
				else
				{
					amount_2 = document.checkoutForm.check_amount.value;
					amount_2_valid = amount_2 != '' && !isNaN(amount_2);
				}
				
				if (!amount_1_valid && !amount_2_valid)
					alert('Please specify a valid check amount.');
				else
				{
					//processCommand('acceptCreditCard',document.checkoutForm.charge_amount.value + "|" + document.checkoutForm.credit_card_number.value);
					addPaymentStuff('Tendered', 'Change');
					addPaymentStuff('Check #' + document.checkoutForm.check_number.value, amount_1_valid ? amount_1 : '0', amount_2_valid ? amount_2 : '0');
					adjustTableWidth('tblCredits');
					YAHOO.example.container.panel2.hide();
				}
			}
		}
		else
		{
			alert('Please select a client.');
		}

		//alert(document.getElementById('charge_status').firstChild.nodeValue);
		//document.getElementById('charge_status').firstChild.nodeValue = 'Card Detected';
    }

    function acceptGiftCard()
    {
		acceptGiftCardValidate(false);
    }
	
	function acceptMemo()
	{
		YAHOO.example.container.panel5.hide();
	}

	function acceptGiftCardValidate(validate)
	{
		//alert('acceptCreditCard() invoked - ' + validate + ' - ' + document.checkoutForm.credit_card_number.value);

		if (document.checkoutForm.co_clientSelect.value)
		{
			if (document.checkoutForm.gift_card_number.value == '')
				alert('Please scan a card or enter a card number.');
			else
			{
				var cert_or_card_number = document.checkoutForm.gift_card_number.value;
				var is_valid = true;
				if (validate)
				{
					is_valid = giftCardValid(cert_or_card_number);
					if (is_valid)
					{
						var index_1 = cert_or_card_number.indexOf(';');
						var index_2 = cert_or_card_number.indexOf('?', index_1);
						cert_or_card_number = cert_or_card_number.substring(index_1 + 1,index_2);
					}
				}

				if (is_valid)
				{
					if (gift_card_payment || gift_certificate_payment)
					{
						addPaymentStuff('Redeem', 'Sell');
						addPaymentStuff(gift_card_payment ? 'Gift Card' : 'Gift Cert', amount_1_valid ? amount_1 : '0', amount_2_valid ? amount_2 : '0');
						adjustTableWidth('tblCredits');
					}
					else
						hiddenGiftCardNumber.setAttribute('value', cert_or_card_number);

					YAHOO.example.container.panel3.hide();
				}
			}
		}
		else
		{
			alert('Please select a client.');
			YAHOO.example.container.panel3.hide();
		}
	}

	function giftCardValid(swipe_string)
	{
		var index_1 = swipe_string.indexOf(';');
		var index_2 = swipe_string.indexOf('?', index_1);

		if ((index_1 > -1) && (index_2 > -1))
			return true;

		return false;
	}

	function acceptPayment()
	{
		console.log('accept payment invoked');
		
		//alert('acceptPayment() invoked - ' + document.checkoutForm.method.value);
		
		// I should ensure that I have a client selected prior to accepting payment...
		
		if (!document.checkoutForm.co_clientSelect.value)
		{
			alert('Please select a client.');
			return;
		}

		if (!document.forms['checkoutForm'].over_payment.value)
		{
			if (total_charges == 0)
			{
				alert("No charges entered.");
				return;
			}
		}

		amount_1 = document.checkoutForm.amount_1.value;
		amount_2 = document.checkoutForm.amount_2.value;
		amount_1_valid = amount_1 != '' && !isNaN(amount_1) && (parseFloat(amount_1) > 0);
		amount_2_valid = amount_2 != '' && !isNaN(amount_2) && (parseFloat(amount_2) > 0);
		if (!amount_1_valid && !amount_2_valid)
			alert('Please specify a valid amount.');
		else
		{
			var payment_method = document.checkoutForm.method.value;
			if (payment_method)
			switch (document.checkoutForm.method.value)
			{
				case '1':
				{
					console.log('card_swiper_present >' + card_swiper_present);
					if (card_swiper_present) {
						credit_card_number = '';
						if (amount_1_valid)
						{
							document.checkoutForm.charge_amount.value = parseFloat(amount_1).formatMoney(2, '.', ',');
							document.getElementById('charge_label').firstChild.nodeValue = 'Charge Amount';
						}
						else
						{
							document.checkoutForm.charge_amount.value = parseFloat(amount_2).formatMoney(2, '.', ',');
							document.getElementById('charge_label').firstChild.nodeValue = 'Refund Amount';
						}
						document.checkoutForm.credit_card_number.value = '';
						document.getElementById('charge_status').firstChild.nodeValue = 'Swipe Card Now';
						YAHOO.example.container.panel1.show();
						document.checkoutForm.credit_card_number.focus();
					} else {
						addPaymentStuff('Charge To', 'Refund');
						addPaymentStuff('Credit Card', amount_1_valid ? amount_1 : '0', amount_2_valid ? amount_2 : '0');
					}
					
					break;
					
				}
				case '7':
				{
					console.log('card_swiper_present >' + card_swiper_present);
					if (card_swiper_present) {
						credit_card_number = '';
						if (amount_1_valid)
						{
							document.checkoutForm.charge_amount.value = parseFloat(amount_1).formatMoney(2, '.', ',');
							document.getElementById('charge_label').firstChild.nodeValue = 'Charge Amount';
						}
						else
						{
							document.checkoutForm.charge_amount.value = parseFloat(amount_2).formatMoney(2, '.', ',');
							document.getElementById('charge_label').firstChild.nodeValue = 'Refund Amount';
						}
						document.checkoutForm.credit_card_number.value = '';
						document.getElementById('charge_status').firstChild.nodeValue = 'Swipe Card Now';
						YAHOO.example.container.panel1.show();
						document.checkoutForm.credit_card_number.focus();
					} else {
						addPaymentStuff('Charge To', 'Refund');
						addPaymentStuff('Visa', amount_1_valid ? amount_1 : '0', amount_2_valid ? amount_2 : '0');
					}
					
					break;
					
				}
				case '8':
				{
					console.log('card_swiper_present >' + card_swiper_present);
					if (card_swiper_present) {
						credit_card_number = '';
						if (amount_1_valid)
						{
							document.checkoutForm.charge_amount.value = parseFloat(amount_1).formatMoney(2, '.', ',');
							document.getElementById('charge_label').firstChild.nodeValue = 'Charge Amount';
						}
						else
						{
							document.checkoutForm.charge_amount.value = parseFloat(amount_2).formatMoney(2, '.', ',');
							document.getElementById('charge_label').firstChild.nodeValue = 'Refund Amount';
						}
						document.checkoutForm.credit_card_number.value = '';
						document.getElementById('charge_status').firstChild.nodeValue = 'Swipe Card Now';
						YAHOO.example.container.panel1.show();
						document.checkoutForm.credit_card_number.focus();
					} else {
						addPaymentStuff('Charge To', 'Refund');
						addPaymentStuff('Mastercard', amount_1_valid ? amount_1 : '0', amount_2_valid ? amount_2 : '0');
					}
					
					break;
					
				}
				case '9':
				{
					console.log('card_swiper_present >' + card_swiper_present);
					if (card_swiper_present) {
						credit_card_number = '';
						if (amount_1_valid)
						{
							document.checkoutForm.charge_amount.value = parseFloat(amount_1).formatMoney(2, '.', ',');
							document.getElementById('charge_label').firstChild.nodeValue = 'Charge Amount';
						}
						else
						{
							document.checkoutForm.charge_amount.value = parseFloat(amount_2).formatMoney(2, '.', ',');
							document.getElementById('charge_label').firstChild.nodeValue = 'Refund Amount';
						}
						document.checkoutForm.credit_card_number.value = '';
						document.getElementById('charge_status').firstChild.nodeValue = 'Swipe Card Now';
						YAHOO.example.container.panel1.show();
						document.checkoutForm.credit_card_number.focus();
					} else {
						addPaymentStuff('Charge To', 'Refund');
						addPaymentStuff('Discover', amount_1_valid ? amount_1 : '0', amount_2_valid ? amount_2 : '0');
					}
					
					break;
					
				}
				case '2':
				{
					if (record_check_num) {
						if (amount_1_valid)
						{
							document.checkoutForm.check_amount.value = parseFloat(amount_1).formatMoney(2, '.', ',');
							//document.getElementById('charge_label').firstChild.nodeValue = 'Charge Amount';
						}
						else
						{
							document.checkoutForm.check_amount.value = parseFloat(amount_2).formatMoney(2, '.', ',');
							//document.getElementById('charge_label').firstChild.nodeValue = 'Refund Amount';
						}

						document.checkoutForm.check_number.value = '';
						YAHOO.example.container.panel2.show();
					} else {addPaymentStuff('Amount Tendered', 'Change Amount');
						addPaymentStuff('Check', amount_1_valid ? amount_1 : '0', amount_2_valid ? amount_2 : '0');
					}
					break;
				}
				case '3':
				{
					addPaymentStuff('Tendered', 'Change');
					addPaymentStuff('Cash', amount_1_valid ? amount_1 : '0', amount_2_valid ? amount_2 : '0');
					break;
				}
				case '4':
				{
					addPaymentStuff('Charge To', 'Payment');
					addPaymentStuff('Account', amount_1_valid ? amount_1 : '0', amount_2_valid ? amount_2 : '0');
					break;
				}
				case '5':
				{
					gift_certificate_payment = true;
					document.checkoutForm.gift_card_number.value = '';
					document.getElementById('panel3_hd_txt').firstChild.nodeValue='Gift Certificate';
					document.getElementById('panel3_bd_txt').firstChild.nodeValue='Enter Gift Certificate Number';
					document.getElementById('panel3_lb_txt').firstChild.nodeValue='Gift Certificate Number';
					document.getElementById('panel3_ft_txt').firstChild.nodeValue='Enter Gift Certificate Number';
					YAHOO.example.container.panel3.show();

					document.checkoutForm.gift_card_number.focus();
					//addPaymentStuff('Redeem', 'Sell');
					//addPaymentStuff('Gift Certificate', amount_1_valid ? amount_1 : '0', amount_2_valid ? amount_2 : '0');
					break;
				}
				case '6':
				{
					gift_card_payment = true;
					document.checkoutForm.gift_card_number.value = '';
					document.getElementById('panel3_hd_txt').firstChild.nodeValue='Gift Card';
					document.getElementById('panel3_bd_txt').firstChild.nodeValue='Swipe Gift Card Now';
					document.getElementById('panel3_lb_txt').firstChild.nodeValue='Gift Card Number';
					document.getElementById('panel3_ft_txt').firstChild.nodeValue='Swipe Gift Card Now';
					YAHOO.example.container.panel3.show();
					document.checkoutForm.gift_card_number.focus();
					//addPaymentStuff('Redeem', 'Sell');
					//addPaymentStuff('Gift Card', amount_1_valid ? amount_1 : '0', amount_2_valid ? amount_2 : '0');
					break;
				}
			}

			adjustTableWidth('tblCredits');
		}
	}

    function addPaymentStuff(v1,v2,v3,v4,v5)
    {
		var tbl = document.getElementById('tblCredits');
		var nextRow = tbl.tBodies[0].rows.length;
		var iteration = nextRow + 1;
		var row = tbl.tBodies[0].insertRow(nextRow);

		var cell0, cell1, cell2, cell3;
		if (v3)
		{
			cell0 = document.createElement('td');
			cell1 = document.createElement('td');
			cell2 = document.createElement('td');
			cell3 = document.createElement('td');

			cell0.style.textAlign = 'right';
			cell0.style.fontWeight = 'bold';
			cell1.style.textAlign = 'right';
			cell2.style.textAlign = 'right';

			var textNode = document.createTextNode(v1);
			cell0.appendChild(textNode);
			//v2 = Math.round(v2 * 100) / 100;
			v2 = roundMoney(v2);
			//v3 = Math.round(v3 * 100) / 100;
			v3 = roundMoney(v3);
			cell1.appendChild(document.createTextNode(v2.formatMoney(2, '.', ',')));
			cell2.appendChild(document.createTextNode(v3.formatMoney(2, '.', ',')));

			//alert('previous row >' + tbl.tBodies[0].rows[tbl.tBodies[0].rows.length - 2].cells[0].firstChild);
			//var previous =

			if (v5)
			{
				var btnEl = document.createElement('input');
				btnEl.setAttribute('type', 'button');
				btnEl.setAttribute('value', 'Void');
				btnEl.onclick = function () {voidCharge(v4);};
				cell3.appendChild(btnEl);
			}
			else
			{
				var btnEl = document.createElement('input');
				btnEl.setAttribute('type', 'button');
				btnEl.setAttribute('value', 'Delete');
				btnEl.onclick = function () {deleteCurrentRow(tbl.tBodies[0].rows[tbl.tBodies[0].rows.length - 2].cells[0].firstChild, 'tblCredits');deleteCurrentRow(this, 'tblCredits');};
				cell3.appendChild(btnEl);
			}

			var method = v1;

			if (v1.indexOf('Check #') > -1)
			{
				var hiddenCheckNum = document.createElement('input');
				hiddenCheckNum.setAttribute('type', 'hidden');
				hiddenCheckNum.setAttribute('name', 'checkNum' + (iteration / 2) + 'tblCredits');
				hiddenCheckNum.setAttribute('value', v1.substring(7));
				cell3.appendChild(hiddenCheckNum);
				method = 'Check';
			}

			var hiddenMethod = document.createElement('input');
			hiddenMethod.setAttribute('type', 'hidden');
			hiddenMethod.setAttribute('name', 'method' + (iteration / 2) + 'tblCredits');
			hiddenMethod.setAttribute('value', method);
			cell3.appendChild(hiddenMethod);

			var hiddenTendered = document.createElement('input');
			hiddenTendered.setAttribute('type', 'hidden');
			hiddenTendered.setAttribute('name', 'tendered' + (iteration / 2) + 'tblCredits');
			hiddenTendered.setAttribute('value', v2);
			cell3.appendChild(hiddenTendered);

			if (gift_card_payment || gift_certificate_payment)
			{
				var hiddenGiftCard = document.createElement('input');
				hiddenGiftCard.setAttribute('type', 'hidden');
				if (gift_card_payment)
					hiddenGiftCard.setAttribute('name', 'hiddenGiftCardNumber' + (iteration / 2) + 'tblCredits');
				else
					hiddenGiftCard.setAttribute('name', 'hiddenGiftCertNumber' + (iteration / 2) + 'tblCredits');
				var cert_or_card_number = document.checkoutForm.gift_card_number.value;
				var index_1 = cert_or_card_number.indexOf(';');
				var index_2 = cert_or_card_number.indexOf('?', index_1);
				if ((index_1 > -1) && (index_2 > -1))
					hiddenGiftCard.setAttribute('value', cert_or_card_number.substring(index_1 + 1,index_2));
				else
					hiddenGiftCard.setAttribute('value', cert_or_card_number);
				cell3.appendChild(hiddenGiftCard);
				gift_card_payment = false;
				gift_certificate_payment = false;
			}

			var hiddenChange = document.createElement('input');
			hiddenChange.setAttribute('type', 'hidden');
			hiddenChange.setAttribute('name', 'change' + (iteration / 2) + 'tblCredits');
			hiddenChange.setAttribute('value', v3);
			cell3.appendChild(hiddenChange);

			if (v4)
			{
				var hiddenResponse = document.createElement('input');
				hiddenResponse.setAttribute('type', 'hidden');
				hiddenResponse.setAttribute('name', 'response' + (iteration / 2) + 'tblCredits');
				hiddenResponse.setAttribute('value', v4);
				cell3.appendChild(hiddenResponse);
			}

			//row.myRow = new myRowObject(textNode, v2, v3);
			row.myRow = new myRowObject(v1, v2, v3);
			updateAmount();
		}
		else
		{
			cell0 = document.createElement('th');
			cell1 = document.createElement('th');
			cell2 = document.createElement('th');
			cell3 = document.createElement('th');

			cell0.appendChild(document.createTextNode(''));
			cell1.appendChild(document.createTextNode(v1));
			cell2.appendChild(document.createTextNode(v2));
			cell3.appendChild(document.createTextNode(''));
		}

		row.appendChild(cell0);
		row.appendChild(cell1);
		row.appendChild(cell2);
		if (cell3)
			row.appendChild(cell3);
    }

    function amountTenderedChange()
    {
		if (!document.forms['checkoutForm'].over_payment.value)
		{
			if (parseFloat(document.forms['checkoutForm'].amount_1.value))
			{
				var value_2 = parseFloat(document.forms['checkoutForm'].amount_1.value) + total_credits - total_charges;
				if (value_2 > 0)
					document.forms['checkoutForm'].amount_2.value = value_2.formatMoney(2, '.', ',');
				else
					document.forms['checkoutForm'].amount_2.value = '';
			}
			else
				document.forms['checkoutForm'].amount_2.value = '';
		}
    }

    Number.prototype.formatMoney = function(c, d, t){
	var n = this, c = isNaN(c = Math.abs(c)) ? 2 : c, d = d == undefined ? "," : d, t = t == undefined ? "." : t, s = n < 0 ? "-" : "", i = parseInt(n = Math.abs(+n || 0).toFixed(c)) + "", j = (j = i.length) > 3 ? j % 3 : 0;
	return s + (j ? i.substr(0, j) + t : "") + i.substr(j).replace(/(\d{3})(?=\d)/g, "$1" + t) + (c ? d + Math.abs(n - i).toFixed(c).slice(2) : "");
    };
    
    
    
    
    
    
    
    
    
    
YAHOO.namespace("example.container");

function init() {
	
	// Define various event handlers for Dialog
	var handleSubmit = function() {
		refresh_black_hole = true;
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

		if (document.getElementById('apptFormLabel').firstChild.nodeValue == 'New Appointment')
		{
		    if (document.forms[0].pSelect.value == '0')
			processCommand('refresh');
		    else
			processCommand('refresh','p');
		}
		else
		{
		    if (document.forms[0].pSelect.value == '0')
			processCommand('forceRefresh');
		    else
			processCommand('forceRefresh','p');
		}
	};
	var handleFailure = function(o) {
		alert("Submission failed: " + o.status);
	};

	// Instantiate the Dialog
	YAHOO.example.container.dialog1 = new YAHOO.widget.Dialog("dialog1", 
                        {width : "485px",
                          fixedcenter : false,
                          visible : false,
                          lazyload : true,
                          x : 200,  y : 100,
                          constraintoviewport : true,
                          buttons : [ {text:"Cancel", handler:handleCancel}, {text:"Submit", handler:handleSubmit, isDefault:true} ]
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
	YAHOO.example.container.dialog1.callback = {success: handleSuccess, failure: handleFailure};
	
	// Render the Dialog
	YAHOO.example.container.dialog1.render();

	
	var handleSubmit2 = function() {
		if (myEditor)
			document.clientInfoForm.editorInput.value = myEditor.cleanHTML(myEditor.getEditorHTML());
		else
			document.clientInfoForm.editorInput.value = document.getElementById('info-editor').value;
		this.submit();
	};
	var handleCancel2 = function() {
		YAHOO.example.container.dialog2.hide();
	};
	YAHOO.example.container.dialog2 = new YAHOO.widget.Dialog("dialog2", 
                        {width : "620px",
                          fixedcenter : false,
                          visible : false,
                          lazyload : true,
                          x : 200,
						  y : 250,
                          constraintoviewport : true,
                          buttons : [ {text:"Close", handler:handleCancel2, isDefault:true} ]
                         } );
	
	// Validate the entries in the form to require that both first and last name are entered
	YAHOO.example.container.dialog2.validate = function() {
		return true;
	};

	// Wire up the success and failure handlers
	YAHOO.example.container.dialog2.callback = {success: handleSuccess, failure: handleFailure};
	
	// Render the Dialog
	YAHOO.example.container.dialog2.render();
	
	
	

	
	
	var handleCancel3 = function() {
		YAHOO.example.container.panel1.hide();
		YAHOO.example.container.panel2.hide();
		YAHOO.example.container.panel3.hide();
		YAHOO.example.container.panel5.hide();
		YAHOO.example.container.checkoutDialog.hide();
	};

	var handleMemo = function() {
		YAHOO.example.container.panel5.show();
		document.forms['checkoutForm'].memo.focus();
	};

	var handleReverse = function() {
		if (!reverse_checked)
			alert('Please select an order to reverse.');
		else
		{
			if (confirm('Reverse selected orders?'))
			{
				document.forms['checkoutForm'].h_reverse.value = '1';
				this.submit();
			}
		}
	};

	var handleSubmit3 = function() {
		this.submit();
	};

	var handleSuccess3 = function(o) {
		if (o.responseText.length > 0)
		{
		    alert(o.responseText);
			YAHOO.example.container.checkoutDialog.show();
		}
		else
		{
			if (document.forms['checkoutForm'].h_reverse.value == '0')
				processCommand('salesReceipt','xxx');
			else
				document.forms['checkoutForm'].h_reverse.value = '0';
		}
	};

	YAHOO.example.container.checkoutDialog = new YAHOO.widget.Dialog("checkoutDialog", 
                        {width : "800px",
                          fixedcenter : false,
                          visible : false,
                          lazyload : true,
                          x : 200, y : 175,
                          constraintoviewport : true,
                          buttons : [ {text:"Cancel", handler:handleCancel3}, {text:"Memo", handler:handleMemo}, {text:"Reverse", handler:handleReverse}, {text:"Checkout", handler:handleSubmit3, isDefault:true} ]
                         } );
	
	YAHOO.example.container.checkoutDialog.validate = function() {
            //if (numCharges == 0)
            //{
            //    alert('No Charges Entered.');
            //    return false;
            //}
            
		return true;
	};

	YAHOO.example.container.checkoutDialog.callback = {success: handleSuccess3, failure: handleFailure};
	
	YAHOO.example.container.checkoutDialog.render();









	var handleReportCancel = function() {
		YAHOO.example.container.reportDialog.hide();
	};
	var handleReportSuccess = function(o) {
	};
	YAHOO.example.container.reportDialog = new YAHOO.widget.Dialog("reportDialog",
                        {width : "800px",
                          fixedcenter : false,
                          visible : false,
                          lazyload : true,
                          x : 200,
						  y : 175,
                          constraintoviewport : true,
                          buttons : [ {text:"Close", handler:handleReportCancel, isDefault:true} ]
                         } );
	YAHOO.example.container.reportDialog.validate = function() {
		return true;
	};
	YAHOO.example.container.reportDialog.callback = {success: handleReportSuccess, failure: handleFailure};
	YAHOO.example.container.reportDialog.render();
	
	

	 
    YAHOO.util.Event.onContentReady("basicmenu", function () {
    
        /*
             Instantiate the menu.  The first argument passed to the 
             constructor is the id of the element in the DOM that represents 
             the menu; the second is an object literal representing a set of 
             configuration properties for the menu.
        */

        var oMenu = new YAHOO.widget.ContextMenu("basicmenu", {trigger: "sc_cont", lazyload: true, hidedelay: 250});
		//var oMenu = new YAHOO.widget.ContextMenu("basicmenu", { fixedcenter: true });
		
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


    if (paLabel == '')
        refreshPractitioner();
    else
        refresh();


}

YAHOO.util.Event.onDOMReady(init);

YAHOO.namespace("example.calendar");

YAHOO.example.calendar.init = function() {
	
	//var render_appt_str = "2/1/2008-2/7/2008, 2/12/2008-2/17/2008, 2/25/2008, 2/26/2008, 2/27/2008, 2/28/2008, 2/29/2008, 2/30/2008, 3/1/2008-3/7/2008, 3/12/2008-3/17/2008, 3/25/2008, 3/26/2008, 3/27/2008, 3/28/2008, 3/29/2008, 3/30/2008, 4/1/2008-4/7/2008, 4/12/2008-4/17/2008, 4/25/2008, 4/26/2008, 4/27/2008, 4/28/2008, 4/29/2008, 4/30/2008, 5/1/2008-5/7/2008, 5/12/2008-4/17/2008, 5/25/2008, 5/26/2008, 5/27/2008, 5/28/2008, 5/29/2008, 5/30/2008, 6/1/2008-6/7/2008, 6/12/2008-6/17/2008, 6/25/2008, 6/26/2008, 6/27/2008, 6/28/2008, 6/29/2008, 6/30/2008";

	YAHOO.example.calendar.cal1 = new YAHOO.widget.CalendarGroup("cal1","cal1Container", {pagedate:pageDateStr} );
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
	    if (cal_for_review)
	    {
		document.forms[1].reviewOnInput.value = month + "/" + day + "/" + year;
		document.forms[1].reviewInput[0].checked = true;
		cal_for_review = false;
	    }
	    else
	    {
		document.forms[1].callOnInput.value = month + "/" + day + "/" + year;
		document.forms[1].todoInput[0].checked = true;
	    }
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
	
	YAHOO.example.calendar.cal2 = new YAHOO.widget.Calendar("cal2","cal2Container", {title:"Choose a date:", close:true} );
	YAHOO.example.calendar.cal2.render();
	YAHOO.example.calendar.cal2.selectEvent.subscribe(selectHandler2, YAHOO.example.calendar.cal2, true);

	var tabView = new YAHOO.widget.TabView('demo');
	var tabView2 = new YAHOO.widget.TabView('demo2');
	var tabView3 = new YAHOO.widget.TabView('demo3');

	
	function onButtonClick(p_oEvent) {

	    if (this.get("id") == 'sButton1a')
	    {
		refresh_black_hole = true;
		var to_do_str = "";
		if (document.forms[1].elements['todoInput'][0].checked)
		    to_do_str = document.forms[1].elements['callOnInput'].value;
		else if (document.forms[1].elements['todoInput'][1].checked)
		    to_do_str = document.forms[1].elements['eList1'].value + '|' + document.forms[1].elements['eList2'].value;

		if (document.forms[1].elements['contactInput'][5].checked)
		    processCommand('addContactStatus',document.forms[1].elements['otherInput'].value + '~' + to_do_str + '~' + document.forms[1].elements['paCList'].value + '~' + document.forms[1].elements['contactAttempt'].value);
		else
		{
		    for (var i = 0; document.forms[1].elements['contactInput'][i]; i++)
		    {
				if (document.forms[1].elements['contactInput'][i].checked)
				{
					processCommand('addContactStatus',i + '~' + to_do_str + '~' + document.forms[1].elements['paCList'].value + '~' + document.forms[1].elements['contactAttempt'].value);
					break;
				}
		    }
		}
	    }
	    else if (this.get("id") == 'sButton1d')
	    {
		refresh_black_hole = true;
		processCommand('deleteContactStatus',document.forms[1].elements['eventList'].value);
	    }
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
			refresh_black_hole = true;
			var prn_check = 'off';
			if (document.forms[1].elements['prn'].checked)
				prn_check = 'on';
			processCommand('addCareDetails', document.forms[1].elements['paList'].value + '|' + document.forms[1].elements['ppList'].value + '|' + document.forms[1].elements['fList1'].value + '|' + document.forms[1].elements['fList2'].value + '|' + document.forms[1].elements['fList3'].value + '|' + prn_check);
	    }
	    else if (this.get("id") == 'sButton3d')
	    {
			refresh_black_hole = true;
			processCommand('deleteCareDetails',document.forms[1].elements['foqList'].value);
	    }
	    else if (this.get("id") == 'sButton3f')
	    {
			//myEditor.cleanHTML(myEditor.getEditorHTML())
			//alert('note id >' + document.forms[1].ci_notesSelect.value);
			//alert('note subject >' + document.forms[1].ci_subject.value);
			//alert('check >' + document.forms[1].ci_alert.value);
			//alert('stuff >' + myEditor.cleanHTML(myEditor.getEditorHTML()));
			
			if (document.forms[1].ci_subject.value == '')
				alert('Please enter a subject.');
			else {
				if (myEditor) {
					if (myEditor.cleanHTML(myEditor.getEditorHTML()) == '')
						alert('Please enter a note.');
					else {
						
						var show_note_check = '';
						if (document.forms[1].elements['ci_alert'].checked)
							show_note_check = 'on';
						
						processCommand('saveNote',document.forms[1].ci_notesSelect.value + ' |' + show_note_check + ' |' + document.forms[1].ci_subject.value + ' |' + escape(myEditor.cleanHTML(myEditor.getEditorHTML())));
						myEditor.setEditorHTML('');
					}
				}
				else {
					if (document.getElementById('info-editor').value == '')
						alert('Please enter a note.');
					else {
						
						var show_note_check = '';
						if (document.forms[1].elements['ci_alert'].checked)
							show_note_check = 'on';
						
						processCommand('saveNote',document.forms[1].ci_notesSelect.value + ' |' + show_note_check + ' |' + document.forms[1].ci_subject.value + ' |' + escape(document.getElementById('info-editor').value));
						document.getElementById('info-editor').value = '';
					}
				}
				
				document.clientInfoForm.ci_subject.value = '';
				document.clientInfoForm.ci_alert.checked = false;
			}
	    }
	    else if (this.get("id") == 'sButton3g')
		{
			//alert('functionality incomplete');
			if (confirm('Send Email??'))
			{
				if (myEditor)
					processCommand('emailNote',document.forms[1].ci_notesSelect.value + ' |' + document.forms[1].ci_alert.value + ' |' + document.forms[1].ci_subject.value + ' |' + escape(myEditor.cleanHTML(myEditor.getEditorHTML())));
				else
					processCommand('emailNote',document.forms[1].ci_notesSelect.value + ' |' + document.forms[1].ci_alert.value + ' |' + document.forms[1].ci_subject.value + ' |' + escape(document.getElementById('info-editor').value));
			}
		}
	    else if (this.get("id") == 'sButton3e')
		{
			if (document.forms[1].ci_notesSelect.value && (document.forms[1].ci_notesSelect.value > 0))
			{
				if (confirm('Delete Note?'))
					processCommand('deleteNote',document.forms[1].ci_notesSelect.value);
			}
			else
				alert('Please select a note to delete.');
		}
		else if (this.get("id") == 'sButton3h')
		{
		    if (document.clientInfoForm.statusInput[0].checked)
			processCommand('saveStatus','0');
		    else if (document.clientInfoForm.statusInput[1].checked)
			processCommand('saveStatus','1');
		    else if (document.clientInfoForm.statusInput[2].checked)
			processCommand('saveStatus','2');
		}
		else if (this.get("id") == 'sButton3i')
		{
		    if (document.forms[1].elements['rrCList'].value == '0')
			    alert('Select a Review Reason');
		    else
		    {
				var selected_index = -1;
				for (i = 0; document.forms[1].elements['fuInput'][i]; i++)
				{
					if (document.forms[1].elements['fuInput'][i].checked)
					{
						selected_index = i;
						break;
					}
				}

				if (document.forms[1].elements['reviewInput'][0].checked)
					processCommand('addClientReview', document.forms[1].reviewList.value + '|' + document.forms[1].elements['rrCList'].value + '|' + document.forms[1].elements['rpList'].value + '|' + document.forms[1].elements['reviewOnInput'].value + '|' + selected_index + '|' + escape(document.forms[1].elements['followUpNote'].value));
				else if (document.forms[1].elements['reviewInput'][1].checked)
					processCommand('addClientReview', document.forms[1].reviewList.value + '|' + document.forms[1].elements['rrCList'].value + '|' + document.forms[1].elements['rpList'].value + '|' + document.forms[1].elements['rList1'].value + '|' + document.forms[1].elements['rList2'].value + '|' + selected_index + '|' + escape(document.forms[1].elements['followUpNote'].value));
				else
					alert('Please choose a date to follow up on/in.');
		    }
		}
		else if (this.get("id") == 'sButton3j')
		{
		    if (document.forms[1].reviewList.value && (document.forms[1].reviewList.value > 0))
		    {
			if (confirm('Delete review item?'))
			    processCommand('deleteClientReview',document.forms[1].reviewList.value);
		    }
		    else
			alert('Please select a review item to delete.');
		}
		else if (this.get("id") == 'sButton3k')
		{
			if (document.forms[1].reviewList.value && (document.forms[1].reviewList.value > 0))
				processCommand('completeClientReview',document.forms[1].reviewList.value);
			else
				alert('Please select a review item to mark as reviewed.');
		}
		else if (this.get("id") == 'sButton3l')
		{
		    if (confirm("Delete?"))
			processCommand('deleteSoap',document.forms[1].ci_soapSelect.value);
		}
		else if (this.get("id") == 'sButton3m')
		{
		    if (document.forms[1].paSOAPList.value == 0)
			alert('Please select a practice area.');
		    else
		    {
			if (document.forms[1].s_note.value.length == 0)
			    alert('Please enter a note');
			else
			{
			    var s_str = escape(document.forms[1].s_note.value);
			    var o_str = escape(document.forms[1].o_note.value);
			    var a_str = escape(document.forms[1].a_note.value);
			    var p_str = escape(document.forms[1].p_note.value);

			    processCommand('saveSoap',document.forms[1].ci_soapSelect.value + ' |' + document.forms[1].paSOAPList.value + ' |' + s_str + ' |' + o_str + ' |' + a_str + ' |' + p_str);
			    
			    document.clientInfoForm.s_note.value = '';
			    document.clientInfoForm.o_note.value = '';
			    document.clientInfoForm.a_note.value = '';
			    document.clientInfoForm.p_note.value = '';
			}
		    }
		}
		else if (this.get("id") == 'sButton3n')
		{
		    processCommand('saveHistory',escape(document.forms[1].history.value));
		    //document.clientInfoForm.history.value = '';
		}
		else if (this.get("id") == 'sButton3r')
		{
		    processCommand('printSoap',document.forms[1].ci_soapSelect.value);
		}
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

		if (is_credit_memo)
		{
			alert('Can\'t add sale to return receipt.  Please ring sales separately from returns.');
			return;
		}
		else
			is_sales_receipt = true;
            
            //alert(document.checkoutForm.typeSelect[document.checkoutForm.typeSelect.selectedIndex].innerHTML);
            
            //if (document.checkoutForm.typeSelect[document.checkoutForm.typeSelect.selectedIndex].innerHTML == 'Payment')
            //{
            //    addCode('tblCredits');
            //    eval("focusControl = document.getElementById('cr" + document.getElementById('tblCredits').tBodies[0].rows.length + "').focus();");
            //}
            //else
            //{
                addCode('tblCharges');
                eval("focusControl = document.getElementById('ch" + document.getElementById('tblCharges').tBodies[0].rows.length + "').focus();");
            //}
        }
		
	function retTrx(p_oEvent) {

		if (is_sales_receipt)
		{
			alert('Can\'t add return to sales receipt.  Please ring returns separately from sales.');
			return;
		}
		else
			is_credit_memo = true;

            //alert(document.checkoutForm.typeSelect[document.checkoutForm.typeSelect.selectedIndex].innerHTML);

            //if (document.checkoutForm.typeSelect[document.checkoutForm.typeSelect.selectedIndex].innerHTML == 'Payment')
            //{
            //    addCode('tblCredits');
            //    eval("focusControl = document.getElementById('cr" + document.getElementById('tblCredits').tBodies[0].rows.length + "').focus();");
            //}
            //else
            //{
                addCode('tblCharges',true);
                eval("focusControl = document.getElementById('ch" + document.getElementById('tblCharges').tBodies[0].rows.length + "').focus();");
            //}
        }
	
	var oPushButton1 = new YAHOO.widget.Button("sButton1a", {onclick: {fn: onButtonClick}});
	var oPushButton2 = new YAHOO.widget.Button("sButton1d", {onclick: {fn: onButtonClick}});

	//var oPushButton3 = new YAHOO.widget.Button("sButton2a", { onclick: { fn: onButtonClick } });
	//var oPushButton4 = new YAHOO.widget.Button("sButton2d", { onclick: { fn: onButtonClick } });

	var oPushButton5 = new YAHOO.widget.Button("sButton3a", {onclick: {fn: onButtonClick}});
	var oPushButton6 = new YAHOO.widget.Button("sButton3d", {onclick: {fn: onButtonClick}});
	var oPushButton10 = new YAHOO.widget.Button("sButton3e", {onclick: {fn: onButtonClick}});
	var oPushButton11 = new YAHOO.widget.Button("sButton3f", {onclick: {fn: onButtonClick}});
	var oPushButton12 = new YAHOO.widget.Button("sButton3g", {onclick: {fn: onButtonClick}});
	//var oPushButton13 = new YAHOO.widget.Button("sButton3h", { onclick: { fn: onButtonClick } });
	var oPushButton14 = new YAHOO.widget.Button("sButton3i", {onclick: {fn: onButtonClick}});
	var oPushButton15 = new YAHOO.widget.Button("sButton3j", {onclick: {fn: onButtonClick}});
	var oPushButton16 = new YAHOO.widget.Button("sButton3k", {onclick: {fn: onButtonClick}});

	var oPushButton17 = new YAHOO.widget.Button("sButton3l", {onclick: {fn: onButtonClick}});
	var oPushButton24 = new YAHOO.widget.Button("sButton3r", {onclick: {fn: onButtonClick}});
	var oPushButton18 = new YAHOO.widget.Button("sButton3m", {onclick: {fn: onButtonClick}});
	var oPushButton19 = new YAHOO.widget.Button("sButton3n", {onclick: {fn: onButtonClick}});

	var oPushButton20 = new YAHOO.widget.Button("sButton3o", {onclick: {fn: acceptPayment}});
	var oPushButton21 = new YAHOO.widget.Button("sButton3p", {onclick: {fn: acceptCheck}});
	var oPushButton22 = new YAHOO.widget.Button("sButton3q", {onclick: {fn: acceptCreditCard}});
	var oPushButton26 = new YAHOO.widget.Button("sButton3t", {onclick: {fn: acceptGiftCard}});
	var oPushButton27 = new YAHOO.widget.Button("sButton3u", {onclick: {fn: acceptMemo}});
	
	var oPushButton7 = new YAHOO.widget.Button("chargeButton", {onclick: {fn: addCharge}});
	var oPushButton8 = new YAHOO.widget.Button("creditButton", {onclick: {fn: addCredit}});
	var oPushButton9 = new YAHOO.widget.Button("addButton", {onclick: {fn: addTrx}});
	//var oPushButton28 = new YAHOO.widget.Button("addDiscount", {onclick: {fn: addDisc}});
	var oPushButton23 = new YAHOO.widget.Button("returnButton", {onclick: {fn: retTrx}});

	var oPushButton25 = new YAHOO.widget.Button("sButton3s", {onclick: {fn: acceptAppointmentReport}});
	
	function newClient(p_oEvent) {
            document.newClientForm.firstname.value='';
	    document.newClientForm.lastname.value='';
	    document.newClientForm.filenumber.value='';
	    document.newClientForm.filenumber.disabled=false;
	    document.newClientForm.prospect.checked=false;
	    document.newClientForm.email.value='';
	    document.newClientForm.phone.value='';
	    document.newClientForm.cell.value='';
	    document.newClientForm.lnnc.value='';
	    document.newClientForm.lnnc2.value='';
	    document.newClientForm.relationshipSelect.value='1';
	    document.newClientForm.clientFileTypeSelect.value='-1';
	    document.newClientForm.marketingPlanSelect.value='0';
		document.newClientForm.groupSelect.options.length = 0;
		document.newClientForm.referralClientSelect.options.length = 0;
	    YAHOO.example.container.dialogNewClient.show();
		
		processCommand('nextFileNumber');

        }
	
		var newClientButton1 = new YAHOO.widget.Button("b1a", {onclick: {fn: newClient}});


		function showClientInfo(p_oEvent) {YAHOO.example.container.dialog2.show();}
		function showCheckout2(p_oEvent) {

		    default_practitioner = '0';

		    previous_balance = 0;
		    total_charges = 0;
		    total_credits = 0;
		    client_owes = 0;
		    numCharges = 0;
		    numCredits = 0;

		    //document.getElementById('discount').firstChild.nodeValue = '0.00';
		    document.forms['checkoutForm'].discount_perc.value = '';
			//document.forms['checkoutForm'].shipping.value = '';
			document.forms['checkoutForm'].memo.value = '';
			document.forms['checkoutForm'].amount_1.value = '';
			document.forms['checkoutForm'].amount_2.value = '';
			document.forms['checkoutForm'].over_payment.checked = false;

		    document.forms['checkoutForm'].h_subtotal.value = '0';
		    document.forms['checkoutForm'].h_tax.value = '0';
		    document.forms['checkoutForm'].h_total.value = '0';
		    document.forms['checkoutForm'].h_previousBalance.value = '0';
		    document.forms['checkoutForm'].h_charges.value = '0';
		    document.forms['checkoutForm'].h_credits.value = '0';
		    document.forms['checkoutForm'].h_owes.value = '0';
		    document.forms['checkoutForm'].h_reverse.value = '0';

		    document.getElementById('subtotal').firstChild.nodeValue = '0.00';
		    document.getElementById('tax').firstChild.nodeValue = '0.00';
		    document.getElementById('total').firstChild.nodeValue = '0.00';
		    document.getElementById('previousBalance').firstChild.nodeValue = '0.00';
		    document.getElementById('owes').firstChild.nodeValue = '0.00';
		    document.getElementById('charges').firstChild.nodeValue = '0.00';
		    document.getElementById('credits').firstChild.nodeValue = '0.00';

		    document.checkoutForm.co_clientSelect.options.length = 0;
		    document.getElementById('checkoutFormLabel').firstChild.nodeValue = '';

		    deleteAllRows(document.getElementById('tblCharges'));
		    deleteAllRows(document.getElementById('tblCredits'));
		    deleteAllRows(document.getElementById('tblPrevious'));

		    addRowToTable('tblCharges', 'No Charges Entered');
		    addRowToTableNoEdit('tblPrevious', 'No Open Orders Found');

			reverse_checked = false;
			
			document.checkoutForm.exp_month.value = '';
			document.checkoutForm.exp_year.value = '';
			document.checkoutForm.zip_code.value = '';
			document.checkoutForm.street_address.value = '';
			document.checkoutForm.cvc_code.value = '';

			is_sales_receipt = false;
			is_credit_memo = false;

		    YAHOO.example.container.checkoutDialog.show();
		}
		
		
		
		var showClientInfoButton = new YAHOO.widget.Button("ci_button", {onclick: {fn: showClientInfo}});
		var showCheckoutButton = new YAHOO.widget.Button("co_button", {onclick: {fn: showCheckout2}});

		var oMenuButton4;
        if (paLabel != '')
            oMenuButton4 = new YAHOO.widget.Button("menubutton2", {type: "menu", label: paLabel, name: "mymenubutton", menu: aMenuButton4Menu});
        else
		{
            oMenuButton4 = new YAHOO.widget.Button("pracRep", {type: "menu", label: "Appointment Report", name: "mymenubutton", menu: repDowMenu});
			var oMenuButton5 = new YAHOO.widget.Button("pracSel", {type: "menu", label: "Practitioner Select", name: "mymenubutton", menu: pracSelMenu});
		}
        
        var apptCalPop = new YAHOO.widget.Button("acp");
        YAHOO.example.calendar.acal2 = new YAHOO.widget.Calendar("acal2","cal3Container", {title:"Choose a date:", close:true} );
        YAHOO.example.calendar.acal2.render();
        YAHOO.example.calendar.acal2.hide();
		YAHOO.example.calendar.acal2.selectEvent.subscribe(selectHandler3, YAHOO.example.calendar.acal2, true);

        // Listener to show the 1-up Calendar when the button is clicked
        YAHOO.util.Event.addListener("acp", "click", YAHOO.example.calendar.acal2.show, YAHOO.example.calendar.acal2, true);
        
        
        var apptCalPop2 = new YAHOO.widget.Button("acp2");
        YAHOO.example.calendar.acal3 = new YAHOO.widget.Calendar("acal3","cal4Container", {title:"Choose a date:", close:true} );
        YAHOO.example.calendar.acal3.render();
        YAHOO.example.calendar.acal3.hide();
		YAHOO.example.calendar.acal3.selectEvent.subscribe(selectHandler4, YAHOO.example.calendar.acal3, true);

        // Listener to show the 1-up Calendar when the button is clicked
        YAHOO.util.Event.addListener("acp2", "click", YAHOO.example.calendar.acal3.show, YAHOO.example.calendar.acal3, true);
        
        var apptCalPop3 = new YAHOO.widget.Button("acp3", {onclick: {fn: nextAppt}});
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
        
        
        
        // Define various event handlers for Dialog
	var handleSubmitNewClient = function() {
		this.submit();
	};
	var handleSuccessNewClient = function(o) {
		if (o.responseText.length > 0)
		{
			YAHOO.example.container.dialogNewClient.show();
		    alert(o.responseText);
		}
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
                                {width : "450px",
								  fixedcenter : false,
								  visible : false,
								  lazyload : true,
								  x : 150,
								  y : 150,
                                  constraintoviewport : true,
                                  buttons : [ {text:"Submit", handler:handleSubmitNewClient, isDefault:true} ]
                                 } );
	
	// Validate the entries in the form to require that both first and last name are entered
	YAHOO.example.container.dialogNewClient.validate = function() {
		var data = this.getData();
		if (data.firstname == 0) {alert("Please select a first name.");return false;}
		if (data.lastname == 0) {alert("Please select a last name.");return false;}
		if ((data.phone == 0) && (data.cell == 0) && (data.email == 0)) {alert("Please select a home phone, cell phone, or email address.");return false;}
		return true;
	};

	// Wire up the success and failure handlers
	YAHOO.example.container.dialogNewClient.callback = {success: handleSuccessNewClient, failure: handleFailureNewClient};
	
	// Render the Dialog
	YAHOO.example.container.dialogNewClient.render();
        
	var Dom = YAHOO.util.Dom, Event = YAHOO.util.Event;
	if (is_chrome) {
		var myConfig = {height: '150px', width: '580px', dompath: true, focusAtStart: false};
		myEditor = new YAHOO.widget.SimpleEditor('info-editor', myConfig);
		myEditor.render();
	}
	
    // Instantiate a Panel from script
    /*
    YAHOO.example.container.panel2 = new YAHOO.widget.Panel("panel2", { width:"320px", visible:false, draggable:false, close:true } );
    YAHOO.example.container.panel2.setHeader("Panel #2 from Script &mdash; This Panel Isn't Draggable");
    YAHOO.example.container.panel2.setBody("This is a dynamically generated Panel.  <br /> more stuff<br /><input type=\"text\" name=\"stuff\"><br /><input type=\"submit\" name=\"submit payment\" value=\"submit payment\">");
    YAHOO.example.container.panel2.setFooter("End of Panel #2");
    YAHOO.example.container.panel2.render("payments");
    */
   
   //alert('got start');

    YAHOO.example.container.panel1 = new YAHOO.widget.Panel("panel1", {width:"320px", visible:false, constraintoviewport:true} );
    YAHOO.example.container.panel1.render();
    YAHOO.example.container.panel2 = new YAHOO.widget.Panel("panel2", {width:"320px", visible:false, constraintoviewport:true} );
    YAHOO.example.container.panel2.render();
    YAHOO.example.container.panel3 = new YAHOO.widget.Panel("panel3", {width:"320px", visible:false, constraintoviewport:true} );
    YAHOO.example.container.panel3.render();
    YAHOO.example.container.panel4 = new YAHOO.widget.Panel("panel4", {width:"320px", visible:false, constraintoviewport:true} );
    YAHOO.example.container.panel4.render();
    YAHOO.example.container.panel5 = new YAHOO.widget.Panel("panel5", {width:"320px", visible:false, constraintoviewport:true} );
    YAHOO.example.container.panel5.render();

	//alert('got end');

}



YAHOO.util.Event.onContentReady("left1", function () {
    YAHOO.util.Event.onDOMReady(YAHOO.example.calendar.init);
});

//YAHOO.util.Event.onDOMReady(YAHOO.example.calendar.init);
//YAHOO.util.Event.onContentReady("left1", YAHOO.example.calendar.init);




function openCashDrawer() {
	var applet = document.jZebra;
	if (applet != null) {

		applet.append("\x07");

		// Send to the printer
		applet.print();

		/*
		while (!applet.isDonePrinting()) {
		// Wait
		}
		var e = applet.getException();
		alert(e == null ? "Printed Successfully" : "Exception occured: " + e.getLocalizedMessage());
		*/
	}
	else {
		//alert("Applet not loaded!");
	}
}
