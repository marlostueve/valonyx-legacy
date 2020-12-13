<div id="checkoutDialog">
    <div class="hd"><h2><span id="checkoutFormLabel">&nbsp;</span> Checkout</h2></div>
    <div class="bd" id="bd-r">
	<form name="checkoutForm" method="POST" action="<%= CUBean.getProperty("cu.formSubmitPath") %>/checkout.x"  >
    
	    <input type="hidden" name="cid" value="-1">
	    <input type="hidden" name="is_client" value="-1">

	    <table border="0" cellpadding="0" cellspacing="0" style="width: 100%;">
		<tr>
		    <td valign="top">
			<table border="0" cellpadding="0" cellspacing="0" >
			    <tr>
				<td>
				    <strong>Search Last Name:</strong>
				    <input style="width: 235px;" id="co_lastname" onfocus="select();" type="textbox" onkeyup="if (document.getElementById('co_lastname').value.length > 1) {show_co_names = true; guc_selected = false; processCommand('getPeopleByLastName', document.getElementById('co_lastname').value.replace(/\'/g,'\\\'') );}" name="co_lastname" /><br />
				    <strong>Search First / File Number:</strong>
				    <input style="width: 140px;" id="co_firstname" onfocus="select();" type="textbox" onkeyup="if (document.getElementById('co_firstname').value.length > 1) {show_co_names = true; guc_selected = false; processCommand('getPeopleByFirstName', document.getElementById('co_firstname').value);}" name="co_firstname" />&nbsp;/&nbsp;<input id="co_filenumber" type="textbox" onfocus="select();" onkeyup="if (document.getElementById('co_filenumber').value.length > 0) {show_co_names = true; guc_selected = false; processCommand('getPeopleByFileNumber', document.getElementById('co_filenumber').value);}" name="co_filenumber" style="width: 83px;" />

				    <div class="clear"></div>

				    <select size="5" name="co_clientSelect" multiple="false" style="width: 235px;" onclick="if (guc_selected) { processCommand('getCheckoutDetailsCID', this.value); YAHOO.example.container.panel1.hide(); YAHOO.example.container.panel2.hide(); } else { processCommand('getCheckoutDetailsCID', this.value); YAHOO.example.container.panel1.hide(); YAHOO.example.container.panel2.hide(); }">
					<option value="-1">-- SEARCH FOR A CLIENT --</option>
				    </select>
				    <br /><br />
				</td>
			    </tr>
			    <tr>
				<td>
				    <strong>Search Inventory:</strong><br />
				    <input style="width: 235px;" id="co_desc" onfocus="select();" type="textbox" onkeyup="if (document.getElementById('co_desc').value.length > 1) {processCommand('getCheckoutCodesByDesc', document.getElementById('co_desc').value);}" name="co_desc" /><br />
				    <strong>Items:</strong><br />
				    <select name="codeSelect" size="10" id="codeSelect" style="width: 235px;">
				    </select><br />
				    <input type="button" id="addButton" value="&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Add Item&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"/>
				    <input type="button" id="returnButton" value="&nbsp;&nbsp;&nbsp;Return Item&nbsp;&nbsp;&nbsp;"/>
				</td>
			    </tr>
			    <!--
			    <tr>
				<td>
				    <strong>Apply to Plan:</strong>&nbsp;<br />
				    <span id="plan">&nbsp;</span><br />
				    <strong>Plan Start Date:</strong>&nbsp;<span id="planStart">&nbsp;</span><br />
				    <strong>Prepaid Visits:</strong>&nbsp;<span id="prepaidVisits">&nbsp;</span><br />
				    <strong>Visits Used:</strong>&nbsp;<span id="visitsUsed">&nbsp;</span><br />
				    <strong>Visits Remaining:</strong>&nbsp;<span id="visitsRemaining">&nbsp;</span><br />
				    <strong>Per Visit Charge:</strong>&nbsp;<span id="perVisitCharge">&nbsp;</span><br />
				    <strong>Amount Paid:</strong>&nbsp;<span id="amountPaid">&nbsp;</span><br />
				    <strong>Escrow Amount:</strong>&nbsp;<span id="escrowAmount">&nbsp;</span><br />
				    <strong>Drop Plan Charge:</strong>&nbsp;<span id="dropPlanCharge">&nbsp;</span>
				    <div class="horizontalRule"><img src="images/trspacer.gif" width="1" height="1" alt="" /></div>
				</td>
			    </tr>
			    -->
			</table>
		    </td>
		    <td valign="top" align="center">
			<table border="0" cellpadding="0" cellspacing="0" style="width: 520px;">
			    <tr>
				<td align="left" valign="top" colspan="2">
				    <strong>Current Charges:</strong>
				    <div id="panel3">
					<div class="hd"><span id="panel3_hd_txt">Swipe Gift Card</span></div>
					<div class="bd">
					    <table width="100%">
						<tr><td colspan="2"><h2><span id="panel3_bd_txt">Swipe Gift Card Now</span></h2></td></tr>
						<tr>
						    <td><strong><span id="panel3_lb_txt">Gift Card Number:</span></strong></td>
							<td align="right"><input type="text" name="gift_card_number" onKeyPress="return disableEnterKey(event)" onkeyup="if (this.value.length > 13) acceptGiftCardValidate(true);" style="width: 120px; text-align: left;" /></td>
						</tr>
						<tr>
						    <td colspan="2" align="right"><input type="button" id="sButton3t" name="sButton3t" value="Accept" /></td>
						</tr>
					    </table>
					</div>
					<div class="ft"><span id="panel3_ft_txt">Swipe Gift Card Now</span></div>
				    </div>
				    <div id="panel5">
					<div class="hd">Receipt Memo</div>
					<div class="bd">
					    <table width="100%">
						<tr>
							<td><textarea name="memo" maxlength="250" style="height: 117px; width: 290px;" ></textarea></td>
						</tr>
						<tr>
						    <td align="right"><input type="button" id="sButton3u" name="sButton3u" value="OK" /></td>
						</tr>
					    </table>
					</div>
				    </div>
				    <div class="tableContainer" style="height: 147px; width: 520px;">
					<table id="tblCharges" border="0" cellpadding="0" cellspacing="0" class="scrollTable" >
					    <thead class="fixedHeader">
						<tr>
						    <th width="25px"><font color="white"><strong>Qty</strong></font></th>
						    <th width="226px"><font color="white"><strong>Description</strong></font></th>
						    <th width="59px"><font color="white"><strong>Amount</strong></font></th>
						    <th width="112px"><font color="white"><strong>Practitioner</strong></font></th>
						    <th width="78px">&nbsp;</th>
						</tr>
					    </thead>
					    <tbody class="scrollContent" style="height: 129px;">
					    </tbody>
					</table>
				    </div>
				</td>
			    </tr>
			    <tr>
				<td colspan="2">
				    <strong>Open & Recent Orders:</strong>
				    <div id="panel1">
					<div class="hd">Accept Credit Card</div>
					<div class="bd">
					    <table width="100%">
						<tr>
						    <td><strong><span id="charge_label">Charge Amount:</span></strong></td>
						    <td align="right"><input type="text" name="charge_amount" style="width: 75px; text-align: right;" /></td>
						</tr>
						<tr><td colspan="2"><strong>Status:</strong></td></tr>
						<tr><td colspan="2"><h2><span id="charge_status">Swipe Card Now</span></h2></td></tr>
						<tr>
						    <td><strong>Card Number:</strong></td>
							<td align="right"><input type="text" name="credit_card_number" onfocus="select();" onKeyPress="return disableEnterKey(event)" onkeyup="if (this.value.length > 23) acceptCreditCard();" style="width: 120px; text-align: left;" /></td>
						</tr>
						<tr>
						    <td><strong>Exp MM/YY:</strong></td>
							<td align="right"><input type="text" name="exp_month" onfocus="select();" maxlength="2" style="width: 30px; text-align: left;" />&nbsp;/&nbsp;<input type="text" onfocus="select();" name="exp_year" maxlength="2" style="width: 30px; text-align: left;" /></td>
						</tr>
						<tr>
						    <td><strong>Zip Code:</strong></td>
							<td align="right"><input type="text" name="zip_code" onfocus="select();" maxlength="10" style="width: 75px; text-align: left;" /></td>
						</tr>
						<tr>
						    <td><strong>Street Address:</strong></td>
							<td align="right"><input type="text" name="street_address" onfocus="select();" style="width: 150px; text-align: left;" /></td>
						</tr>
						<tr>
						    <td><strong>CVC Code:</strong></td>
							<td align="right"><input type="text" name="cvc_code" onfocus="select();" maxlength="4" style="width: 50px; text-align: left;" /></td>
						</tr>
						<tr>
						    <td colspan="2" align="right"><input type="button" id="sButton3q" name="sButton3q" value="Accept Credit Card" /></td>
						</tr>
					    </table>
					</div>
					<div class="ft">Swipe Card for a Better Rate</div>
				    </div>
				    <div class="tableContainer" style="height: 100px; width: 520px;">
					<table id="tblPrevious" border="0" cellpadding="0" cellspacing="0" class="scrollTable">
					    <thead class="fixedHeader">
						<tr>
						    <th width="40px"><font color="white"><strong>#</strong></font></th>
						    <th width="59px"><font color="white"><strong>Date</strong></font></th>
						    <th width="244px"><font color="white"><strong>Description</strong></font></th>
						    <th width="67px"><font color="white"><strong title="Balance or Order Total">Bal/Total</strong></font></th>
						    <th width="36px"><font color="white"><strong title="Pay for On Account Order">Pay</strong></font></th>
						    <th width="56px"><font color="white"><strong title="Reverse Order">Reverse</strong></font></th>
						</tr>
					    </thead>
					    <tbody class="scrollContent" style="height: 82px;">
					    </tbody>
					</table>
				    </div>
				    <input type="hidden" name="numPrevious" value="0">
				    <input type="hidden" name="isCheckout" value="1">
				    <input type="hidden" name="order_id" value="0">
				    <input type="hidden" name="h_subtotal" value="0">
				    <input type="hidden" name="h_tax" value="0">
				    <input type="hidden" name="h_total" value="0">
				    <input type="hidden" name="h_previousBalance" value="0">
				    <input type="hidden" name="h_charges" value="0">
				    <input type="hidden" name="h_credits" value="0">
				    <input type="hidden" name="h_owes" value="0">
				    <input type="hidden" name="h_reverse" value="0">
				</td>
			    </tr>
			    <tr>
				<td>
					<strong>Payments:</strong>&nbsp;&nbsp;<input type="checkbox" name="over_payment" onchange="updateAmount(1);" >Place overpayment on account
				    <div id="panel2">
					<div class="hd">Accept Check</div>
					<div class="bd">
					    <table width="100%">
						<tr>
						    <td><strong>Check Number:</strong></td>
						    <td><input type="text" name="check_number" style="width: 75px; text-align: right;" /></td>
						</tr>
						<tr>
						    <td><strong>Check Amount:</strong></td>
						    <td><input type="text" name="check_amount" style="width: 75px; text-align: right;" /></td>
						</tr>
						<tr>
						    <td colspan="2" align="right"><input type="button" id="sButton3p" name="sButton3p" value="Accept Check" /></td>
						</tr>
					    </table>
					</div>
				    </div>
				    <div class="tableContainer" style="height: 137px; width: 336px;">
					<table id="tblCredits" border="0" cellpadding="0" cellspacing="0" >
					    <thead class="fixedHeader">
						<tr>
						    <th width="109px"><font color="white"><strong>Method</strong></font></th>
						    <th width="77px"><font color="white"><strong><span id="method_1">Charge To</span></strong></font></th>
						    <th width="73px"><font color="white"><strong><span id="method_2">Refund</span></strong></font></th>
						    <th width="61px">&nbsp;</th>
						</tr>
						<tr>
						    <td width="109px">
							<select name="method" onchange="methodSelect();">
							    <option value="7">Visa</option>
							    <option value="8">Mastercard</option>
							    <option value="9">Discover</option>
							    <option value="2">Check</option>
							    <option value="3">Cash</option>
							    <option value="4">Account</option>
							    <!-- <option value="5">Gift Certificate</option> -->
							    <!-- <option value="6">Gift Card</option> -->
							</select>
						    </td>
						    <td width="79px" align="right"><input type="text" name="amount_1" style="width: 75px; text-align: right;" onfocus="select();" onkeyup="amountTenderedChange();" /></td>
						    <td width="79px" align="right"><input type="text" name="amount_2" style="width: 75px; text-align: right;" onfocus="select();" /></td>
						    <td><input type="button" id="sButton3o" name="sButton3o" value="Accept" /></td>
						</tr>
					    </thead>
					    <tbody class="scrollContent" style="height: 90px;">
					    </tbody>
					</table>
				    </div>
				</td>
				<td valign="bottom" align="right">
				    <div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>
				    <strong>Discount %:</strong>&nbsp;<input type="text" name="discount_perc" style="width: 35px; text-align: right;" onfocus="select();" onkeyup="updateAmount(1);" /><br />
				    <strong>Client Taxable:</strong>&nbsp;
							<select name="client_taxable" onchange="updateAmount(1);">
							    <option value="1">Yes</option>
							    <option value="2">No</option>
							</select><br />
				    <strong>SubTotal:</strong>&nbsp;<span id="subtotal">0.00</span><br />
				    <strong>Tax:</strong>&nbsp;<span id="tax">0.00</span><br />
				    <strong>Total:</strong>&nbsp;<span id="total">0.00</span><br />
				    <div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>
				    <strong>Previous Balance:</strong>&nbsp;<span id="previousBalance">0.00</span><br />
				    <strong>Current Charges:</strong>&nbsp;<span id="charges">0.00</span><br />
				    <strong>Current Credits:</strong>&nbsp;<span id="credits">0.00</span>
				    <div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>
				    <strong>Client Owes:</strong>&nbsp;<span id="owes">0.00</span>
				    <div class="horizontalRule"><img src="../images/trspacer.gif" width="1" height="1" alt="" /></div>
				</td>
			    </tr>
		    </table>
		    </td>
		    <td>
		    </td>
		</tr>
	    </table>

	</form>
    </div>
</div>