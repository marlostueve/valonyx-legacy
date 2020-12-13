
var focusedAppt = 0;

var default_practitioner = 0;

var previous_balance = 0;
var total_charges = 0;
var total_credits = 0;
var client_owes = 0;

var numCharges = 0;
var numCredits = 0;
var creditId = 0;
var chargeId = 0;

var is_sales_receipt = false;
var is_credit_memo = false;


var wTimer;
var wEid;
var wPid;
var wCmd;

var message_row;


function showPeople(xml_str)
{
	/*
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
	 */


	 document.getElementById(wPid).options.length = 0;
	 
	//document.forms['newApptForm'].clientSelect.options.length = 0;

	var index = 0;
	while (xml_str.getElementsByTagName("person")[index] != null)
	{
		key = xml_str.getElementsByTagName("person")[index].childNodes[0].childNodes[0].nodeValue;
		value = xml_str.getElementsByTagName("person")[index].childNodes[1].childNodes[0].nodeValue;
		eval("var personArray = " + value);

		/*
		if (show_ci_names)
			document.forms['clientInfoForm'].ci_clientSelect.options[index] = new Option(personArray["label"], key);
		else if (show_co_names)
			document.forms['checkoutForm'].co_clientSelect.options[index] = new Option(personArray["label"], key);
		else if (show_group_names)
			document.forms['newClientForm'].groupSelect.options[index] = new Option(personArray["label"], key);
		else if (show_referral_names)
			document.forms['newClientForm'].referralClientSelect.options[index] = new Option(personArray["label"], key);
		else
			document.forms['newApptForm'].clientSelect.options[index] = new Option(personArray["label"], key);
			*/
		   
		document.getElementById(wPid).options[index] = new Option(personArray["label"], key);
		   
		index++;
	}

	/*
	show_ci_names = false;
	show_co_names = false;
	show_group_names = false;
	show_referral_names = false;
	*/
}

function showCodes(xml_str)
{
	codeArray = new Array();

	//document.forms['checkoutForm'].codeSelect.options.length = 0;
	document.getElementById(wPid).options.length = 0;
	var index = 0;
	while (xml_str.getElementsByTagName("code")[index] != null)
	{
		key = xml_str.getElementsByTagName("code")[index].childNodes[0].childNodes[0].nodeValue;
		value = xml_str.getElementsByTagName("code")[index].childNodes[1].childNodes[0].nodeValue;
		eval("var codeArr = " + value);

		codeArray[key] = codeArr;

		//document.forms['checkoutForm'].codeSelect.options[index] = new Option(codeArr["desc"],key);
		document.getElementById(wPid).options[index] = new Option(codeArr["desc"],key);
		index++;
	}
}
    
function showCheckout(xml_str)
{
	//alert("showCheckout() invoked");

	//var key = xml_str.childNodes[0].childNodes[0].nodeValue;
	//var value = xml_str.childNodes[1].childNodes[0].nodeValue;
	
	clearCheckoutForm();
	
	var coForm = document.getElementById('checkoutForm');

	if (xml_str.getAttribute("p") != '0')
		default_practitioner = xml_str.getAttribute("p");

	var key = xml_str.getElementsByTagName("person")[0].childNodes[0].childNodes[0].nodeValue;
	var value = xml_str.getElementsByTagName("person")[0].childNodes[1].childNodes[0].nodeValue;

	eval("var arr = " + value);

	//alert(value);

	previous_balance = parseFloat(xml_str.getElementsByTagName("previous")[0].childNodes[0].nodeValue);
	client_owes = previous_balance;

	document.getElementById('previousBalance').firstChild.nodeValue = xml_str.getElementsByTagName("previous")[0].childNodes[0].nodeValue;
	document.getElementById('owes').firstChild.nodeValue = xml_str.getElementsByTagName("previous")[0].childNodes[0].nodeValue;

	var index = 1;
	if (xml_str.getElementsByTagName("person")[index] != null)
	{
		while (xml_str.getElementsByTagName("person")[index] != null)
		{
			g_key = xml_str.getElementsByTagName("person")[index].childNodes[0].childNodes[0].nodeValue;
			g_value = xml_str.getElementsByTagName("person")[index].childNodes[1].childNodes[0].nodeValue;
			eval("arx = " + g_value);

			coForm.co_clientSelect.options[index - 1] = new Option(arx["label"] + ' (' + arx["relate"] + ')',g_key);
			if (g_key == key)
				coForm.co_clientSelect.options[index - 1].selected = true;
			index++;
		}

		//document.getElementById('checkoutFormLabel').firstChild.nodeValue = arr["label"] + " (" + arr["fts"] + ")";

		document.getElementById('checkoutFormLabel').firstChild.nodeValue = arr["ltl"];
	}
	else
		document.getElementById('checkoutFormLabel').firstChild.nodeValue = arr["label"];

	//guc_selected = true;

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

			addRowToTable('tblCharges', arr["desc"], arr["code_id"], arr["code"], arr["amount"], arr["qty"], arr["tax"],arr["type"]);
			numCharges++;

			index++;
		}

		adjustTableWidth('tblCredits');
	}
	else
		addRowToTable('tblCharges', 'No Charges Entered');



	coForm.numPrevious.value=index;

	if (xml_str.getElementsByTagName("payment-plan-instance")[0] != null)
	{
		key = xml_str.getElementsByTagName("payment-plan-instance")[0].childNodes[0].childNodes[0].nodeValue;
		value = xml_str.getElementsByTagName("payment-plan-instance")[0].childNodes[1].childNodes[0].nodeValue;
		eval("arr = " + value);

		plan_id = arr["practice_area_id"];
	}
	else {
		plan_id = 0;
	}

	coForm.discount_perc.value = '';
	//coForm.shipping.value = '';
	coForm.memo.value = '';
	coForm.amount_1.value = '';
	coForm.amount_2.value = '';
	coForm.over_payment.checked = false;

	document.getElementById('charges').firstChild.nodeValue = '0.00';
	document.getElementById('credits').firstChild.nodeValue = '0.00';



	

	updateAmount();

	// YAHOO.example.container.checkoutDialog.show();
	
	
}



function showCheckoutNoAppt(p_oEvent) {

	clearCheckoutForm();

	//YAHOO.example.container.checkoutDialog.show();
	
	$(function () { $("#checkoutDialog").modal('show'); });
	
}

function clearCheckoutForm() {
	
	default_practitioner = '0';

	previous_balance = 0;
	total_charges = 0;
	total_credits = 0;
	client_owes = 0;
	numCharges = 0;
	numCredits = 0;
	creditId = 0;
	chargeId = 0;
	
	var coForm = document.getElementById('checkoutForm');

	//document.getElementById('discount').firstChild.nodeValue = '0.00';
	coForm.discount_perc.value = '';
	//document.forms['checkoutForm'].shipping.value = '';
	coForm.memo.value = '';
	coForm.amount_1.value = '';
	coForm.amount_2.value = '';
	coForm.over_payment.checked = false;

	coForm.h_subtotal.value = '0';
	coForm.h_tax.value = '0';
	coForm.h_total.value = '0';
	coForm.h_previousBalance.value = '0';
	coForm.h_charges.value = '0';
	coForm.h_credits.value = '0';
	coForm.h_owes.value = '0';
	coForm.h_reverse.value = '0';

	document.getElementById('subtotal').firstChild.nodeValue = '0.00';
	document.getElementById('tax').firstChild.nodeValue = '0.00';
	document.getElementById('total').firstChild.nodeValue = '0.00';
	document.getElementById('previousBalance').firstChild.nodeValue = '0.00';
	document.getElementById('owes').firstChild.nodeValue = '0.00';
	document.getElementById('charges').firstChild.nodeValue = '0.00';
	document.getElementById('credits').firstChild.nodeValue = '0.00';

	coForm.co_clientSelect.options.length = 0;
	document.getElementById('checkoutFormLabel').firstChild.nodeValue = '';
	
	//alert('tblCharges >' + document.getElementById('tblCharges'));

	var charges_element = document.getElementById('tblCharges');

	removeAllChildren(charges_element);
	removeAllChildren(document.getElementById('tblCredits'));
	//removeAllChildren(document.getElementById('tblPrevious'));
	
	
	if (!message_row) {
		addNoChargesMessageRow('No Charges Entered');
	}
	charges_element.appendChild(message_row);
	
	addMethodOfPaymentRow();

	//addRowToTable('tblCharges', 'No Charges Entered');
	//addRowToTableNoEdit('tblPrevious', 'No Open Orders Found');

	reverse_checked = false;

	/*
	coForm.exp_month.value = '';
	coForm.exp_year.value = '';
	coForm.zip_code.value = '';
	coForm.street_address.value = '';
	coForm.cvc_code.value = '';
	*/

	is_sales_receipt = false;
	is_credit_memo = false;
	
	// reset payment method back to default (credit card)
	coForm.method.value = 1;
	methodSelect();

	
}

function addRowToTable(tname, desc, id, code, amount, qty, tax, type)
{
	
}

function addRowToTableNoEdit(tname, desc, id, code, amount, qty, tax, type)
{
	
}

function addPreviousRow() {
	
	
	
}

function addNoChargesMessageRow(message) {
	
	message_row = document.createElement('tr');
	var message_cell_1 = document.createElement('td');
	message_cell_1.setAttribute('colspan','5');
	message_cell_1.setAttribute('style', 'padding: 5px; color: #333333; outline-color: #333333; text-shadow: none; text-align: left; font-size: larger;');
	var strong_1 = document.createElement('strong');
	strong_1.appendChild(document.createTextNode(message));
	message_cell_1.appendChild(strong_1);
	message_row.appendChild(message_cell_1);
}

function addMethodOfPaymentRow() {
	
	/*
	<tr>
		<td style="padding: 2px; color: #333333; outline-color: #333333; text-shadow: none; text-align: center;">
			<select name="method" style="padding: 0px; margin: 0px;  height:22px; width: 85px; ">
				<option value="1">Credit Card</option>
				<option value="4">Account</option>
				<option value="7">Bitcoin</option>
			</select>
		</td>
		<td style="padding: 2px; color: #333333; outline-color: #333333; text-shadow: none; text-align: right;">
			<input type="text" name="amount_1" onkeyup="amountTenderedChange();" style=" text-align: right; padding: 0px; margin: 0px;  height:22px; width: 55px; " value="14.50" />
		</td>
		<td style="padding: 2px; color: #333333; outline-color: #333333; text-shadow: none; text-align: right;">
			<input type="text" name="amount_2" style=" text-align: right; padding: 0px; margin: 0px;  height:22px; width: 55px; " value="14.50" />
		</td>
		<td style="padding: 2px; color: #333333; outline-color: #333333; text-shadow: none; text-align: center;">
			<a class="btn btn-mini btn-success"   >Accept</a>
		</td>
	</tr>
	 */
	
	var credits_element = document.getElementById('tblCredits');
	
	var mop_row = document.createElement('tr');
	var mop_cell_1 = document.createElement('td');
	mop_cell_1.setAttribute('style', 'padding: 2px; color: #333333; outline-color: #333333; text-shadow: none; text-align: center;');
	var mop_select = document.createElement('select');
	mop_select.setAttribute('name', 'method');
	mop_select.setAttribute('onchange', 'methodSelect();');
	mop_select.setAttribute('style', 'padding: 0px; margin: 0px;  height:22px; width: 85px; ');
	mop_cell_1.appendChild(mop_select);
	var mop_option_1 = document.createElement('option');
	mop_option_1.setAttribute('value', '1');
	mop_option_1.appendChild(document.createTextNode('Credit Card'));
	var mop_option_2 = document.createElement('option');
	mop_option_2.setAttribute('value', '4');
	mop_option_2.appendChild(document.createTextNode('Account'));
	var mop_option_3 = document.createElement('option');
	mop_option_3.setAttribute('value', '7');
	mop_option_3.appendChild(document.createTextNode('Bitcoin'));
	mop_select.appendChild(mop_option_1);
	mop_select.appendChild(mop_option_2);
	mop_select.appendChild(mop_option_3);
	
	var mop_cell_2 = document.createElement('td');
	mop_cell_2.setAttribute('style', 'padding: 2px; color: #333333; outline-color: #333333; text-shadow: none; text-align: right;');
	var amount_1_input = document.createElement('input');
	amount_1_input.setAttribute('type', 'text');
	amount_1_input.setAttribute('name', 'amount_1');
	amount_1_input.setAttribute('onkeyup', 'amountTenderedChange();');
	amount_1_input.setAttribute('style', ' text-align: right; padding: 0px; margin: 0px;  height:22px; width: 55px; ');
	amount_1_input.setAttribute('value', '0.00');
	mop_cell_2.appendChild(amount_1_input);
	
	var mop_cell_3 = document.createElement('td');
	mop_cell_3.setAttribute('style', 'padding: 2px; color: #333333; outline-color: #333333; text-shadow: none; text-align: right;');
	var amount_2_input = document.createElement('input');
	amount_2_input.setAttribute('type', 'text');
	amount_2_input.setAttribute('name', 'amount_2');
	amount_2_input.setAttribute('style', ' text-align: right; padding: 0px; margin: 0px;  height:22px; width: 55px; ');
	amount_2_input.setAttribute('value', '0.00');
	mop_cell_3.appendChild(amount_2_input);
	
	var mop_cell_4 = document.createElement('td');
	mop_cell_4.setAttribute('style', 'padding: 2px; color: #333333; outline-color: #333333; text-shadow: none; text-align: center;');
	var accept_anchor = document.createElement('a');
	accept_anchor.setAttribute('class','btn btn-mini btn-success');
	accept_anchor.setAttribute('onclick','acceptPayment();');
	accept_anchor.appendChild(document.createTextNode('Accept'));
	mop_cell_4.appendChild(accept_anchor);
	
	mop_row.appendChild(mop_cell_1);
	mop_row.appendChild(mop_cell_2);
	mop_row.appendChild(mop_cell_3);
	mop_row.appendChild(mop_cell_4);
	
	credits_element.appendChild(mop_row);
}

function acceptPayment()
{
	//alert('acceptPayment() invoked - ' + document.checkoutForm.method.value);

	// I should ensure that I have a client selected prior to accepting payment...
	
	var coForm = document.getElementById('checkoutForm');

	if (!coForm.co_clientSelect.value)
	{
		alert('Please select a client.');
		return;
	}

	if (!coForm.over_payment.value)
	{
		if (total_charges == 0)
		{
			alert("No charges entered.");
			return;
		}
	}

	amount_1 = coForm.amount_1.value;
	amount_2 = coForm.amount_2.value;
	amount_1_valid = amount_1 != '' && !isNaN(amount_1) && (parseFloat(amount_1) > 0);
	amount_2_valid = amount_2 != '' && !isNaN(amount_2) && (parseFloat(amount_2) > 0);
	if (!amount_1_valid && !amount_2_valid)
		alert('Please specify a valid amount.');
	else
	{
		var payment_method = coForm.method.value;
		if (payment_method) {
			switch (coForm.method.value)
			{
				case '1':
				{
					//alert('card_swiper_present >' + card_swiper_present);
					if (card_swiper_present) {
						credit_card_number = '';
						if (amount_1_valid)
						{
							coForm.charge_amount.value = parseFloat(amount_1).formatMoney(2, '.', ',');
							document.getElementById('charge_label').firstChild.nodeValue = 'Charge Amount';
						}
						else
						{
							coForm.charge_amount.value = parseFloat(amount_2).formatMoney(2, '.', ',');
							document.getElementById('charge_label').firstChild.nodeValue = 'Refund Amount';
						}
						coForm.credit_card_number.value = '';
						document.getElementById('charge_status').firstChild.nodeValue = 'Swipe Card Now';
						YAHOO.example.container.panel1.show();
						coForm.credit_card_number.focus();
					} else {
						addPaymentStuff('Charge To', 'Refund');
						addPaymentStuff('Credit Card', amount_1_valid ? amount_1 : '0', amount_2_valid ? amount_2 : '0');
					}

					break;

				}
				case '2':
				{
					if (record_check_num) {
						if (amount_1_valid)
						{
							coForm.check_amount.value = parseFloat(amount_1).formatMoney(2, '.', ',');
							//document.getElementById('charge_label').firstChild.nodeValue = 'Charge Amount';
						}
						else
						{
							coForm.check_amount.value = parseFloat(amount_2).formatMoney(2, '.', ',');
							//document.getElementById('charge_label').firstChild.nodeValue = 'Refund Amount';
						}

						coForm.check_number.value = '';
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
					//addPaymentStuff('Charge To', 'Payment');
					//addPaymentStuff('Account', amount_1_valid ? amount_1 : '0', amount_2_valid ? amount_2 : '0');
					
					addAcceptPaymentRow('Account', amount_1_valid ? amount_1 : '0', amount_2_valid ? amount_2 : '0');
					
					break;
				}
				case '5':
				{
					gift_certificate_payment = true;
					coForm.gift_card_number.value = '';
					document.getElementById('panel3_hd_txt').firstChild.nodeValue='Gift Certificate';
					document.getElementById('panel3_bd_txt').firstChild.nodeValue='Enter Gift Certificate Number';
					document.getElementById('panel3_lb_txt').firstChild.nodeValue='Gift Certificate Number';
					document.getElementById('panel3_ft_txt').firstChild.nodeValue='Enter Gift Certificate Number';
					YAHOO.example.container.panel3.show();

					coForm.gift_card_number.focus();
					//addPaymentStuff('Redeem', 'Sell');
					//addPaymentStuff('Gift Certificate', amount_1_valid ? amount_1 : '0', amount_2_valid ? amount_2 : '0');
					break;
				}
				case '6':
				{
					gift_card_payment = true;
					coForm.gift_card_number.value = '';
					document.getElementById('panel3_hd_txt').firstChild.nodeValue='Gift Card';
					document.getElementById('panel3_bd_txt').firstChild.nodeValue='Swipe Gift Card Now';
					document.getElementById('panel3_lb_txt').firstChild.nodeValue='Gift Card Number';
					document.getElementById('panel3_ft_txt').firstChild.nodeValue='Swipe Gift Card Now';
					YAHOO.example.container.panel3.show();
					coForm.gift_card_number.focus();
					//addPaymentStuff('Redeem', 'Sell');
					//addPaymentStuff('Gift Card', amount_1_valid ? amount_1 : '0', amount_2_valid ? amount_2 : '0');
					break;
				}
				case '7':
				{
					gift_card_payment = true;
					coForm.gift_card_number.value = '';
					document.getElementById('panel3_hd_txt').firstChild.nodeValue='Gift Card';
					document.getElementById('panel3_bd_txt').firstChild.nodeValue='Swipe Gift Card Now';
					document.getElementById('panel3_lb_txt').firstChild.nodeValue='Gift Card Number';
					document.getElementById('panel3_ft_txt').firstChild.nodeValue='Swipe Gift Card Now';
					YAHOO.example.container.panel3.show();
					coForm.gift_card_number.focus();
					//addPaymentStuff('Redeem', 'Sell');
					//addPaymentStuff('Gift Card', amount_1_valid ? amount_1 : '0', amount_2_valid ? amount_2 : '0');
					break;
				}
			}
		}

		
	}
}

function addAcceptPaymentRow(paymentMethod, amount1, amount2) {
	
	/*
			<tr>
				<td style="padding: 2px; color: #333333; outline-color: #333333; text-shadow: none; text-align: center;"><strong>Credit Card</strong></td>
				<td style="padding: 2px; color: #333333; outline-color: #333333; text-shadow: none; text-align: right; "><strong>40.76</strong></td>
				<td style="padding: 2px; color: #333333; outline-color: #333333; text-shadow: none; text-align: right;"><strong>0.00</strong></td>
				<td style="padding: 2px; color: #333333; outline-color: #333333; text-shadow: none; text-align: center;"><a class="btn btn-mini btn-danger"   >Delete</a></td>
			</tr>
	 */
	
	numCredits++;
	creditId++;
	
	var credits_element = document.getElementById('tblCredits');
	
	var payment_row = document.createElement('tr');
	payment_row.setAttribute('id', 'pr_' + creditId);
	
	var payment_cell_1 = document.createElement('td');
	payment_cell_1.setAttribute('style', 'padding: 2px; color: #333333; outline-color: #333333; text-shadow: none; text-align: center;');
	var strong_1 = document.createElement('strong');
	strong_1.appendChild(document.createTextNode(paymentMethod));
	payment_cell_1.appendChild(strong_1);
	var hiddenPaymentMethodInp = document.createElement('input');
	hiddenPaymentMethodInp.setAttribute('type', 'hidden');
	hiddenPaymentMethodInp.setAttribute('name', 'hiddenPaymentMethod' + creditId + 'tblCredits');
	hiddenPaymentMethodInp.setAttribute('value', paymentMethod);
	payment_cell_1.appendChild(hiddenPaymentMethodInp);
	
	var payment_cell_2 = document.createElement('td');
	payment_cell_2.setAttribute('style', 'padding: 2px; color: #333333; outline-color: #333333; text-shadow: none; text-align: right;');
	var strong_2 = document.createElement('strong');
	strong_2.appendChild(document.createTextNode(amount1));
	payment_cell_2.appendChild(strong_2);
	var hiddenAmount1Inp = document.createElement('input');
	hiddenAmount1Inp.setAttribute('type', 'hidden');
	hiddenAmount1Inp.setAttribute('name', 'hiddenAmount1' + creditId + 'tblCredits');
	hiddenAmount1Inp.setAttribute('value', amount1);
	payment_cell_2.appendChild(hiddenAmount1Inp);
	
	var payment_cell_3 = document.createElement('td');
	payment_cell_3.setAttribute('style', 'padding: 2px; color: #333333; outline-color: #333333; text-shadow: none; text-align: right;');
	var strong_3 = document.createElement('strong');
	strong_3.appendChild(document.createTextNode(amount2));
	payment_cell_3.appendChild(strong_3);
	var hiddenAmount2Inp = document.createElement('input');
	hiddenAmount2Inp.setAttribute('type', 'hidden');
	hiddenAmount2Inp.setAttribute('name', 'hiddenAmount2' + creditId + 'tblCredits');
	hiddenAmount2Inp.setAttribute('value', amount2);
	payment_cell_3.appendChild(hiddenAmount2Inp);
	
	var payment_cell_4 = document.createElement('td');
	payment_cell_4.setAttribute('style', 'padding: 2px; color: #333333; outline-color: #333333; text-shadow: none; text-align: center;');
	var delete_anchor = document.createElement('a');
	delete_anchor.setAttribute('class', 'btn btn-mini btn-danger');
	delete_anchor.setAttribute('onclick', "removeChildElement('tblCredits','pr_"  + creditId +  "');");
	delete_anchor.appendChild(document.createTextNode('Delete'));
	payment_cell_4.appendChild(delete_anchor);
	
	payment_row.appendChild(payment_cell_1);
	payment_row.appendChild(payment_cell_2);
	payment_row.appendChild(payment_cell_3);
	payment_row.appendChild(payment_cell_4);
	
	credits_element.appendChild(payment_row);
}

function removeCredit(cid) {
	removeChildElement('tblCharges', 'cr_' + cid);
	var coForm = document.getElementById('checkoutForm');
	eval('var row_qty_input_delete = coForm.qty' + cid + 'tblCharges;');
	row_qty_input_delete.parentNode.removeChild(row_qty_input_delete);
	var foundCharge = false;
	for (var index = 1; index <= chargeId; index++) {
		eval('var row_qty_input = coForm.qty' + index + 'tblCharges;');
		if (row_qty_input && row_qty_input.parentNode) {
			foundCharge = true;
		}
	}
	if (!foundCharge) {
		document.getElementById('tblCharges').appendChild(message_row);
	}
	updateAmount();
}

function methodSelect()
{
	var coForm = document.getElementById('checkoutForm');
	switch (coForm.method.value)
	{
		case '1':
		{
			document.getElementById('method_1').firstChild.nodeValue = 'Charge To';
			document.getElementById('method_2').firstChild.nodeValue = 'Refund';
			break;
		}
		case '2': case '3': case '7':
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
	
function addTrx(p_oEvent) {

	if (is_credit_memo) {
		alert('Can\'t add sale to return receipt.  Please ring sales separately from returns.');
		return;
	} else {
		is_sales_receipt = true;
	}
		
	addCode('tblCharges');
	eval("focusControl = document.getElementById('ch" + document.getElementById('tblCharges').tBodies[0].rows.length + "').focus();");
}
    
function addCode(tbl, isReturn) {
	
	//var index_value = document.checkoutForm.codeSelect.value;
	var index_value = document.getElementById('codeSelect').value;
	//alert("index_value >" + index_value);
	if (index_value)
	{
		if (numCharges == 0) {
			deleteAllRows(document.getElementById('tblCharges'));
		}
		numCharges++;
		
		var amount = codeArray[index_value]["amount"];

		/*
		if (plan_id > 0)
		{
			if (codeArray[index_value]["practice_area"] == plan_id)
			{
				amount = "PLAN";
			//alert('no charge for this checkout code');
			}
		}
		*/

		var existing_qty = false;
		//var table_obj = document.getElementById('tblCharges');
		if (codeArray[index_value]["type"] != 1 && (1 == 2)) // disable this and take it back to the drawing board...
		{
			for (var i = 0; tbl.tBodies[0].rows[i]; i++)
			{
				row_code_id = tbl.tBodies[0].rows[i].myRow.four.value;
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
			//addRowToTable(tbl, codeArray[index_value]["desc"], codeArray[index_value]["id"], codeArray[index_value]["code"], amount, (ret ? -1 : 1), codeArray[index_value]["tax"], codeArray[index_value]["type"]);
			
			addChargeRow(codeArray[index_value]["id"], (isReturn ? -1 : 1), codeArray[index_value]["desc"], amount, codeArray[index_value]["tax"], codeArray[index_value]["type"]);
		}
	}
	else
		alert('You must select a code to add.');
}

function addChargeRow(id, qty, desc, amount, tax, type) {
	
	/*
		<tr>
		  <td style="padding: 2px; color: #333333; outline-color: #333333; text-shadow: none; text-align: center;"><input type="text" name="qty_1" style=" text-align: right; padding: 0px; margin: 0px;  height:22px; width: 25px; " /></td>
		  <td style="padding: 2px; color: #333333; outline-color: #333333; text-shadow: none; "><strong>Catalyn (Chew) - 90T</strong></td>
		  <td style="padding: 2px; color: #333333; outline-color: #333333; text-shadow: none; text-align: right;"><input type="text" name="qty_1" style=" text-align: right; padding: 0px; margin: 0px;  height:22px; width: 55px; " value="14.50" /></td>
		  <td style="padding: 2px; color: #333333; outline-color: #333333; text-shadow: none;">
			  <select name="prac_1" style="padding: 0px; margin: 0px;  height:22px; width: 125px;  ">
				  <option value="11">Christine Stueve</option>
				  <option value="22">Michelle Guggenberger</option>
			  </select>
		  </td>
		  <td style="padding: 2px; color: #333333; outline-color: #333333; text-shadow: none; text-align: center;"><a class="btn btn-mini btn-danger"   >Delete</a></td>
		</tr>
	 */
	
	
	chargeId++;
	
	var charges_element = document.getElementById('tblCharges');
	
	if (message_row.parentNode == charges_element)
		charges_element.removeChild(message_row);
	
	var charge_row = document.createElement('tr');
	charge_row.setAttribute('id', 'cr_' + chargeId);
	
	var charge_cell_1 = document.createElement('td');
	charge_cell_1.setAttribute('style', 'padding: 2px; color: #333333; outline-color: #333333; text-shadow: none; text-align: center;');
	var qty_input = document.createElement('input');
	qty_input.setAttribute('type', 'text');
	qty_input.setAttribute('style', ' text-align: right; padding: 0px; margin: 0px;  height:22px; width: 25px; ');
	qty_input.setAttribute('name', 'qty' + chargeId + 'tblCharges');
	qty_input.setAttribute('value', qty);
	qty_input.setAttribute('onkeyup', 'updateAmount(1);');
	qty_input.setAttribute('onfocus', 'qty_input.select();');
	charge_cell_1.appendChild(qty_input);
	
	
	var charge_cell_2 = document.createElement('td');
	charge_cell_2.setAttribute('style', 'padding: 2px; color: #333333; outline-color: #333333; text-shadow: none; ');
	var strong_2 = document.createElement('strong');
	strong_2.appendChild(document.createTextNode(desc));
	charge_cell_2.appendChild(strong_2);
	
	
	var charge_cell_3 = document.createElement('td');
	charge_cell_3.setAttribute('style', 'padding: 2px; color: #333333; outline-color: #333333; text-shadow: none; text-align: right;');
	var txtInp = document.createElement('input');
	txtInp.setAttribute('type', 'text');
	txtInp.setAttribute('name', 'inputName' + chargeId + 'tblCharges');
	txtInp.setAttribute('style', ' text-align: right; padding: 0px; margin: 0px;  height:22px; width: 55px; ');
	txtInp.setAttribute('value', amount);
	txtInp.setAttribute('onkeyup', 'updateAmount(1);');
	txtInp.setAttribute('onfocus', 'txtInp.select();');
	charge_cell_3.appendChild(txtInp);
	var hiddenInp = document.createElement('input');
	hiddenInp.setAttribute('type', 'hidden');
	hiddenInp.setAttribute('name', 'hidden' + chargeId + 'tblCharges');
	hiddenInp.setAttribute('value', id);
	charge_cell_3.appendChild(hiddenInp);
	if (type == '7' || type == '8') {
		hiddenGiftCardNumber = document.createElement('input');
		hiddenGiftCardNumber.setAttribute('type', 'hidden');
		if (type == '7')
			hiddenGiftCardNumber.setAttribute('name', 'hiddenGiftCardNumber' + chargeId + 'tblCharges');
		else
			hiddenGiftCardNumber.setAttribute('name', 'hiddenGiftCertNumber' + chargeId + 'tblCharges');
		charge_cell_3.appendChild(hiddenGiftCardNumber);
	}
	var hiddenTaxInp = document.createElement('input');
	hiddenTaxInp.setAttribute('type', 'hidden');
	hiddenTaxInp.setAttribute('name', 'hiddenTax' + chargeId + 'tblCharges');
	hiddenTaxInp.setAttribute('value', tax);
	charge_cell_3.appendChild(hiddenTaxInp);
	
	
	var charge_cell_4 = document.createElement('td');
	charge_cell_4.setAttribute('style', 'padding: 2px; color: #333333; outline-color: #333333; text-shadow: none;');
	var sel1 = document.createElement('select');
	sel1.setAttribute('style', 'padding: 0px; margin: 0px;  height:22px; width: 125px;  ');
	sel1.setAttribute('name', 'sel' + chargeId + 'tblCharges');
	sel1.options[0] = new Option('none', 0);
	var x=1;
	for (i in practitionerArray) {
		sel1.options[x] = new Option(practitionerArray[i]["label"], i);
		if (i == default_practitioner)
			sel1.options[x].selected = true;
		x++;
	}
	charge_cell_4.appendChild(sel1);
	
	
	var charge_cell_5 = document.createElement('td');
	charge_cell_5.setAttribute('style', 'padding: 2px; color: #333333; outline-color: #333333; text-shadow: none; text-align: center;');
	var delete_anchor = document.createElement('a');
	delete_anchor.setAttribute('class', 'btn btn-mini btn-danger');
	delete_anchor.setAttribute('onclick', "removeCharge('"  + chargeId +  "');");
	delete_anchor.appendChild(document.createTextNode('Delete'));
	charge_cell_5.appendChild(delete_anchor);
	
	charge_row.appendChild(charge_cell_1);
	charge_row.appendChild(charge_cell_2);
	charge_row.appendChild(charge_cell_3);
	charge_row.appendChild(charge_cell_4);
	charge_row.appendChild(charge_cell_5);
	
	charges_element.appendChild(charge_row);
	
	updateAmount();
}

function removeCharge(cid) {
	removeChildElement('tblCharges', 'cr_' + cid);
	var coForm = document.getElementById('checkoutForm');
	eval('var row_qty_input_delete = coForm.qty' + cid + 'tblCharges;');
	row_qty_input_delete.parentNode.removeChild(row_qty_input_delete);
	var foundCharge = false;
	for (var index = 1; index <= chargeId; index++) {
		eval('var row_qty_input = coForm.qty' + index + 'tblCharges;');
		if (row_qty_input && row_qty_input.parentNode) {
			foundCharge = true;
		}
	}
	if (!foundCharge) {
		document.getElementById('tblCharges').appendChild(message_row);
	}
	updateAmount();
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
	
	var coForm = document.getElementById('checkoutForm');

	var discount_perc = parseFloat(coForm.discount_perc.value) ? parseFloat(coForm.discount_perc.value) : 0;
	if (discount_perc > 100)
		discount_perc = 100;
	
	var client_taxable = coForm.client_taxable.checked;

	//var shipping = parseFloat(coForm.shipping.value) ? parseFloat(coForm.shipping.value) : 0;
	
	for (var index = 1; index <= chargeId; index++) {
		
		eval('var row_qty_input = coForm.qty' + index + 'tblCharges;');
		
		if (row_qty_input && row_qty_input.parentNode) {
			
			eval('var row_amount_input = coForm.inputName' + index + 'tblCharges;');
			eval('var row_tax_input = coForm.hiddenTax' + index + 'tblCharges;');
			
			row_amount = row_amount_input.value;
			row_qty = row_qty_input.value;
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
			if (client_taxable) {
				
				
				table_1_tax += parseFloat(row_tax_input.value) * row_amount; // <-- incorrect...
			}
			//alert('table_1_tax >' + table_1_tax);
		
		}
	}

	table_1_subtotal = roundMoney(table_1_subtotal);
	table_1_tax = Math.round(table_1_tax) / 100;
	
	//alert(table_1_tax);
	
	
	/*
	table_obj = document.getElementById('tblPrevious');
	for (i = 0; table_obj.tBodies[0].rows[i]; i++)
	{
		if (table_obj.tBodies[0].rows[i].myRow)
		{
			if (table_obj.tBodies[0].rows[i].myRow.three.checked == true)
			{
				row_amount = table_obj.tBodies[0].rows[i].myRow.two.value;
				row_amount = parseFloat(row_amount) ? (parseFloat(row_amount) * 1) : 0;
				table_2_amount += row_amount;
			}
		}
	}
	table_2_amount = roundMoney(table_2_amount);
	*/
	
	
	
	
	for (var index = 1; index <= creditId; index++) {
		
		eval('var row_mop_input = coForm.hiddenPaymentMethod' + index + 'tblCredits;');
		
		if (row_mop_input) {
			
			alert('row_mop_input >' + row_mop_input.value);
			
			eval('var row_amount1_input = coForm.hiddenAmount1' + index + 'tblCredits;');
			eval('var row_amount2_input = coForm.hiddenAmount2' + index + 'tblCredits;');
			

			row_amount = row_amount1_input.value;
			row_amount_b = row_amount2_input.value;

			row_amount = parseFloat(row_amount) ? (parseFloat(row_amount) * 1) : 0;
			row_amount_b = parseFloat(row_amount_b) ? (parseFloat(row_amount_b) * 1) : 0;

			table_3_amount += (row_amount - row_amount_b);
			if (row_mop_input.value == 'Account')
				table_3_account_amount += (row_amount - row_amount_b);
			
		
		}
	}
	
	/*
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
	*/
	
	
	//table_3_amount = Math.round(table_3_amount * 100) / 100;
	table_3_amount = roundMoney(table_3_amount);
	//table_3_account_amount = Math.round(table_3_account_amount * 100) / 100;
	table_3_account_amount = roundMoney(table_3_account_amount);

	//total_charges = table_1_subtotal + table_2_amount + table_1_tax + shipping;
	total_charges = table_1_subtotal + table_2_amount + table_1_tax;  // I'm thinking that paying for previous stuff should not get added to current charges
	total_charges_current_only = table_1_subtotal + table_1_tax ;
	total_credits = table_3_amount;

	coForm.h_subtotal.value = (table_1_subtotal + table_2_amount);
	document.getElementById('subtotal').firstChild.nodeValue = (table_1_subtotal + table_2_amount).formatMoney(2, '.', ',');
	coForm.h_tax.value = table_1_tax;
	document.getElementById('tax').firstChild.nodeValue = table_1_tax.formatMoney(2, '.', ',');
	coForm.h_total.value = total_charges;
	document.getElementById('total').firstChild.nodeValue = total_charges.formatMoney(2, '.', ',');

	coForm.h_previousBalance.value = previous_balance;
	coForm.h_charges.value = total_charges;
	document.getElementById('charges').firstChild.nodeValue = total_charges.formatMoney(2, '.', ',');
	coForm.h_credits.value = total_credits;
	document.getElementById('credits').firstChild.nodeValue = total_credits.formatMoney(2, '.', ',');

	//client_owes = Math.round((previous_balance + total_charges - (total_credits - table_3_account_amount)) * 100) / 100;
	//client_owes = Math.round((previous_balance + table_1_subtotal + table_1_tax - (total_credits - table_3_account_amount)) * 100) / 100;
	//client_owes = roundMoney(previous_balance + table_1_subtotal + table_1_tax - (total_credits - table_3_account_amount));
	//client_owes = roundMoney(previous_balance + total_charges_current_only - (total_credits - table_3_account_amount));
	client_owes = roundMoney(previous_balance + total_charges - (total_credits - table_3_account_amount));
	coForm.h_owes.value = client_owes;
	document.getElementById('owes').firstChild.nodeValue = client_owes.formatMoney(2, '.', ',');

	if (coForm.over_payment.checked == false)
	{
		if ((total_charges - total_credits) > 0)
		{
			coForm.amount_1.value = (total_charges - total_credits).formatMoney(2, '.', ',');
			coForm.amount_2.value = '';
			if (!x)
				coForm.amount_1.select();
		}
		else if ((total_credits - total_charges) > 0)
		{
			coForm.amount_2.value = (Math.abs(total_charges - total_credits)).formatMoney(2, '.', ',');
			coForm.amount_1.value = '';
			if (!x)
				coForm.amount_2.select();
		}
		else
			coForm.amount_2.value = '';
	}
	else
		coForm.amount_2.value = '';
}

function roundMoney(amt)
{
	return Math.round(parseFloat((amt * 100).toFixed(2))) / 100;
}

Number.prototype.formatMoney = function(c, d, t){
var n = this, c = isNaN(c = Math.abs(c)) ? 2 : c, d = d == undefined ? "," : d, t = t == undefined ? "." : t, s = n < 0 ? "-" : "", i = parseInt(n = Math.abs(+n || 0).toFixed(c)) + "", j = (j = i.length) > 3 ? j % 3 : 0;
return s + (j ? i.substr(0, j) + t : "") + i.substr(j).replace(/(\d{3})(?=\d)/g, "$1" + t) + (c ? d + Math.abs(n - i).toFixed(c).slice(2) : "");
};

function waitOnKeyDown(eid, pid, cmd) {
	//alert('waitOnKeyDown invoked >' + cmd);
	clearTimeout(wTimer);
	wEid = eid;
	wPid = pid;
	wCmd = cmd;
	wTimer = setTimeout('waitComplete();', 500);
}

function waitComplete() {
	var keyword = document.getElementById(wEid).value;
	if (keyword.length > 1) {
		doAjax(wCmd, keyword.replace(/\'/g,'\\\''));
	}
}

