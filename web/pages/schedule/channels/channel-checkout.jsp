<div id="checkoutDialog" class="black-box modal hide fade" style="width: 770px;">
	<div class="modal-header tab-header">
		<button type="button" class="close" data-dismiss="modal">&times;</button>
		<span id="checkoutFormLabel">&nbsp;</span> Checkout
	</div>
	
	<form class="fill-up" id="checkoutForm"  name="checkoutForm" method="POST" action="<%= CUBean.getProperty("cu.formSubmitPath") %>/checkout.x"  >
		
		
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
	
	<div class="modal-body separator" style="max-height: 700px;" >

		<!--
	  <h4>Text in a modal</h4>
	  <p>Duis mollis, est non commodo luctus, nisi erat porttitor ligula, eget lacinia odio sem.</p>
		-->
		
<%
// 
%>

			<div class="row-fluid">
				<div class="span4">
					<!-- <div class="padded" style="padding-bottom: 0; padding-top: 0;"> -->
						
						<label for="lastname"><strong>Client Search:</strong></label>
						<div class="input">
							<input type="text" id="co_lastname" placeholder="Last / First / File Number"  onkeyup="waitOnKeyDown('co_lastname','co_clientSelect','getPeopleByKeyword');" />
						</div>
						<div class="input">
							<select size="5" id="co_clientSelect" name="co_clientSelect" onclick="doAjax('getCheckoutDetailsCID',this.value);" >
							<option value="-1">-- SEARCH FOR A CLIENT --</option>
							</select>
						</div>
						
						<label for="lnnc"><strong>Search Inventory:</strong></label>
						<div class="input">
							<input placeholder="Search inventory" id="co_desc" type="text" onkeyup="waitOnKeyDown('co_desc','codeSelect','getCheckoutCodesByDesc');" name="lnnc" />
						</div>
						<div class="input">
							<select size="7" multiple id="codeSelect" name="codeSelect">
							</select>
						</div>
						
						
						<div class="inner-well clearfix">
							<div class="pull-left">
							  <!-- <button class="btn" onclick="addTrx();">Add Item</button> -->
							  <a  class="btn btn-success"  onclick="addTrx();"  >Add Item</a>
							</div>

							<div class="pull-right">
							  <button class="btn">Return Item</button>
							</div>
						  </div>
						
					<!-- </div> -->
				</div>

				<div class="span8">
					
					


					<div class="row-fluid">
						<div class="span12">
							<div class="tabbable black-box" style="margin-bottom: 10px;">

								<div class="tab-header">
									Orders<span id="ordersFor">&nbsp;</span>
									<!--
									<span class="pull-right">
										<span class="options">
											<div class="btn-group">
												<a class="dropdown-toggle" data-toggle="dropdown"><i class="icon-cog"></i></a>
												<ul class="dropdown-menu black-box-dropdown dropdown-left">
													<li><a href="#">Action</a></li>
													<li><a href="#">Another action</a></li>
													<li><a href="#">Something else here</a></li>
													<li class="divider"></li>
													<li><a href="#">Separated link</a></li>
												</ul>
											</div>
										</span>
									</span>
									-->
								</div>

								<ul class="nav nav-tabs">
									<li class="active"><a href="#tab1" data-toggle="tab"><i class="icon-shopping-cart"></i> Current Order</a></li>
									<li class=""><a href="#tab2" data-toggle="tab"><i class="icon-comment"></i> Memo</a></li>
									<li class=""><a href="#tab3" data-toggle="tab"><i class="icon-folder-open"></i> Open & Recent Orders</a></li>
								</ul>
								<div class="tab-content" >

									<div class="tab-pane active" id="tab1" >
										<div class="separator" style="padding: 7px 7px 0px 7px; margin-bottom: 0px;">
											
											
											<div class="box" style="position:relative;">
												<!--
												<div class="tab-header" >
												  Current Charges:
												</div>
												-->
												<table class="table table-striped">
													<thead>
														<tr>
														  <th style="padding: 2px; color: #333333; outline-color: #333333; text-shadow: none; text-align: center;">Qty</th>
														  <th style="padding: 2px; color: #333333; outline-color: #333333; text-shadow: none;">Desc</th>
														  <th style="padding: 2px; color: #333333; outline-color: #333333; text-shadow: none; text-align: right;">Amount</th>
														  <th style="padding: 2px; color: #333333; outline-color: #333333; text-shadow: none;">Practitioner</th>
														  <th style="padding: 2px; color: #333333; outline-color: #333333; text-shadow: none;">&nbsp;</th>
														</tr>
													</thead>
													<tbody id="tblCharges">
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
														  <td style="padding: 2px; color: #333333; outline-color: #333333; text-shadow: none;"></td>
														</tr>
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
													</tbody>
													<tfoot>
														<tr>
														  <td colspan="5">
														  </td>
														</tr>
													</tfoot>
												</table>

											</div>
											
											

										</div>
									</div>

									
									<div class="tab-pane" id="tab2">
										<div class="separator" style="padding: 7px 7px 0px 7px; margin-bottom: 0px;">

											<div class="box" style="position:relative;">
												
												<table class="table table-striped">
													<thead>
														<tr>
														  <th style="padding: 2px; color: #333333; outline-color: #333333; text-shadow: none;">&nbsp;Memo for Current Order:</th>
														</tr>
													</thead>
													<tbody>
														<tr>
														  <td style="padding: 2px; color: #333333; outline-color: #333333; text-shadow: none;"><textarea name="memo" maxlength="250"  ></textarea></td>
														</tr>
													</tbody>
													<tfoot>
														<tr>
														  <td colspan="5">
														  </td>
														</tr>
													</tfoot>
												</table>
												
											</div>
											
										</div>
									</div>
									

									<div class="tab-pane" id="tab3">
										<div class="separator" style="padding: 7px 7px 0px 7px; margin-bottom: 0px;">

											<div class="box" style="position:relative;">
												<!--
												<div class="tab-header" >
												  Open & Recent Orders:
												</div>
												-->
												<table class="table table-striped">
													<thead>
														<tr style="border-bottom: none; font-weight: normal;">
														  <th style=" padding: 4px; color: #333333; outline-color: #333333; text-shadow: none;">#</th>
														  <th style="width: 190px; padding: 4px; color: #333333; outline-color: #333333; text-shadow: none;">Date</th>
														  <th style=" padding: 4px; color: #333333; outline-color: #333333; text-shadow: none;">Desc</th>
														  <th style=" padding: 4px; color: #333333; outline-color: #333333; text-shadow: none;">Balance</th>
														  <th style=" padding: 4px; color: #333333; outline-color: #333333; text-shadow: none;">&nbsp;</th>
														</tr>
													</thead>
													<tbody id="tblPrevious">
														<tr>
														  <td style=" padding: 4px; color: #333333; outline-color: #333333; text-shadow: none;">14555</td>
														  <td style=" padding: 4px; color: #333333; outline-color: #333333; text-shadow: none;">Internet Explorer 4.0</td>
														  <td style=" padding: 4px; color: #333333; outline-color: #333333; text-shadow: none;">Win 95+</td>
														  <td style=" padding: 4px; color: #333333; outline-color: #333333; text-shadow: none;" class="center">4</td>
														  <td style=" padding: 4px; color: #333333; outline-color: #333333; text-shadow: none;" class="center">4</td>
														</tr>
														<tr>
														  <td style=" padding: 4px; color: #333333; outline-color: #333333; text-shadow: none;">14555</td>
														  <td style=" padding: 4px; color: #333333; outline-color: #333333; text-shadow: none;">Internet Explorer 4.0</td>
														  <td style=" padding: 4px; color: #333333; outline-color: #333333; text-shadow: none;">Win 95+</td>
														  <td style=" padding: 4px; color: #333333; outline-color: #333333; text-shadow: none;" class="center">4</td>
														  <td style=" padding: 4px; color: #333333; outline-color: #333333; text-shadow: none;" class="center">4</td>
														</tr>
														<tr>
														  <td style=" padding: 4px; color: #333333; outline-color: #333333; text-shadow: none;">14555</td>
														  <td style=" padding: 4px; color: #333333; outline-color: #333333; text-shadow: none;">Internet Explorer 4.0</td>
														  <td style=" padding: 4px; color: #333333; outline-color: #333333; text-shadow: none;">Win 95+</td>
														  <td style=" padding: 4px; color: #333333; outline-color: #333333; text-shadow: none;" class="center">4</td>
														  <td style=" padding: 4px; color: #333333; outline-color: #333333; text-shadow: none;" class="center">4</td>
														</tr>
													</tbody>
													<tfoot>
														<tr>
														  <td colspan="5">
														  </td>
														</tr>
													</tfoot>
												</table>

											</div>
											
											
										</div>
										
									</div>
									
									
									
								</div>
							</div>
						</div>
					</div>
					

						
					
						

						<div class="row-fluid">
							<div class="span8">


							<div class="tabbable black-box" style="margin-bottom: 10px;">
								
								

								<div class="tab-header">
									Payments
									<!--
									<span class="pull-right">
										<span class="options">
											<div class="btn-group">
												<a class="dropdown-toggle" data-toggle="dropdown"><i class="icon-cog"></i></a>
												<ul class="dropdown-menu black-box-dropdown dropdown-left">
													<li><a href="#">Action</a></li>
													<li><a href="#">Another action</a></li>
													<li><a href="#">Something else here</a></li>
													<li class="divider"></li>
													<li><a href="#">Separated link</a></li>
												</ul>
											</div>
										</span>
									</span>
									-->
								</div>

								<div class="separator" style="padding: 7px 7px 0px 7px; margin-bottom: 0px;">

									<div class="box" style="position:relative;">

										<table class="table table-striped">
											<thead>
												<tr>
													<th style="padding: 2px; color: #333333; outline-color: #333333; text-shadow: none; text-align: center;">Method</th>
													<th style="padding: 2px; color: #333333; outline-color: #333333; text-shadow: none;"><span id="method_1">Charge To</span></th>
													<th style="padding: 2px; color: #333333; outline-color: #333333; text-shadow: none; text-align: right;"><span id="method_2">Refund</span></th>
													<th style="padding: 2px; color: #333333; outline-color: #333333; text-shadow: none;">&nbsp;</th>
												</tr>
											</thead>
											<tbody id="tblCredits">
												<tr>
													<td style="padding: 2px; color: #333333; outline-color: #333333; text-shadow: none; text-align: center;">
														<select name="prac_1" style="padding: 0px; margin: 0px;  height:22px; width: 85px; ">
															<option value="11">Account</option>
															<option value="22">Credit Card</option>
														</select>
													</td>
													<td style="padding: 2px; color: #333333; outline-color: #333333; text-shadow: none; text-align: right;"><input type="text" name="amount_1" onkeyup="amountTenderedChange();" style=" text-align: right; padding: 0px; margin: 0px;  height:22px; width: 55px; " value="14.50" /></td>
													<td style="padding: 2px; color: #333333; outline-color: #333333; text-shadow: none; text-align: right;"><input type="text" name="amount_2" style=" text-align: right; padding: 0px; margin: 0px;  height:22px; width: 55px; " value="14.50" /></td>
													<td style="padding: 2px; color: #333333; outline-color: #333333; text-shadow: none; text-align: center;"><a class="btn btn-mini btn-success"   >Accept</a></td>
												</tr>
												<tr>
													<td style="padding: 2px; color: #333333; outline-color: #333333; text-shadow: none; text-align: center;"><strong>Credit Card</strong></td>
													<td style="padding: 2px; color: #333333; outline-color: #333333; text-shadow: none; text-align: right; "><strong>40.76</strong></td>
													<td style="padding: 2px; color: #333333; outline-color: #333333; text-shadow: none; text-align: right;"><strong>0.00</strong></td>
													<td style="padding: 2px; color: #333333; outline-color: #333333; text-shadow: none; text-align: center;"><a class="btn btn-mini btn-danger"   >Delete</a></td>
												</tr>
											</tbody>
											<tfoot><tr><td colspan="5"></td></tr></tfoot>
										</table>

									</div>

								</div>
								
								<div class="separator" style="padding: 7px 7px 7px 7px; margin-bottom: 0px;">
									
									<div class="row-fluid">
										<div class="span5">
								
									
										<div class="inner-well clearfix">
											<div class="pull-left">
											  Taxable
											</div>

											<div class="pull-right">
											  <input rel="confirm-check" type="checkbox" id="LngBx" name="client_taxable" class="checky" onchange="updateAmount();" checked="checked"/>
											  <label for="LngBx" class="checky"><span></span></label>
											</div>
										  </div>
											
										</div>
										<div class="span7">

										<div class="inner-well clearfix">
											<div class="pull-left">
											  Payment on account
											</div>

											<div class="pull-right">
											  <input type="checkbox" id="LngBx2" name="over_payment" class="checky" />
											  <label for="LngBx2" class="checky"><span></span></label>
											</div>
										  </div>
									
										</div>
									</div>
									
								</div>
							</div>

							</div>
							<div class="span4">

								<table class="table table-striped table-bordered box">
									<tbody>
										<tr>
											<td style="padding: 2px; color: #333333; outline-color: #333333; text-shadow: none; text-align: right; ">Discount %:</td>
											<td style="padding: 2px; color: #333333; outline-color: #333333; text-shadow: none; text-align: right; "><input type="text" name="discount_perc" style=" text-align: right; padding: 0px; margin: 0px;  height:22px; width: 35px; " onkeyup="updateAmount(1);" /></td>
										</tr>
										<tr>
											<td style="padding: 2px; color: #333333; outline-color: #333333; text-shadow: none; text-align: right; ">SubTotal:</td>
											<td style="padding: 2px; color: #333333; outline-color: #333333; text-shadow: none; text-align: right; "><strong><span id="subtotal">0.00</span></strong></td>
										</tr>
										<tr>
											<td style="padding: 2px; color: #333333; outline-color: #333333; text-shadow: none; text-align: right; ">Tax:</td>
											<td style="padding: 2px; color: #333333; outline-color: #333333; text-shadow: none; text-align: right; "><strong><span id="tax">0.00</span></strong></td>
										</tr>
										<tr>
											<td style="padding: 2px; color: #333333; outline-color: #333333; text-shadow: none; text-align: right; color: red; "><strong>Total:</strong></td>
											<td style="padding: 2px; color: #333333; outline-color: #333333; text-shadow: none; text-align: right; font-size: larger; "><strong><span id="total">0.00</span></strong></td>
										</tr>
										<tr>
											<td style="padding: 2px; color: #333333; outline-color: #333333; text-shadow: none; text-align: right; ">Previous Balance:</td>
											<td style="padding: 2px; color: #333333; outline-color: #333333; text-shadow: none; text-align: right; "><strong><span id="previousBalance">0.00</span></strong></td>
										</tr>
										<tr>
											<td style="padding: 2px; color: #333333; outline-color: #333333; text-shadow: none; text-align: right; ">Current Charges:</td>
											<td style="padding: 2px; color: #333333; outline-color: #333333; text-shadow: none; text-align: right; "><strong><span id="charges">0.00</span></strong></td>
										</tr>
										<tr>
											<td style="padding: 2px; color: #333333; outline-color: #333333; text-shadow: none; text-align: right; ">Current Credits:</td>
											<td style="padding: 2px; color: #333333; outline-color: #333333; text-shadow: none; text-align: right; "><strong><span id="credits">0.00</span></strong></td>
										</tr>
										<tr>
											<td style="padding: 2px; color: #333333; outline-color: #333333; text-shadow: none; text-align: right; color: red; "><strong>Client Owes:</strong></td>
											<td style="padding: 2px; color: #333333; outline-color: #333333; text-shadow: none; text-align: right; font-size: larger; "><strong><span id="owes">0.00</span></strong></td>
										</tr>
									</tbody>
								</table>
							</div>
						</div>





						
						
						


					<!-- </div> -->
					
				</div>
			</div>
			
    
			<input type="hidden" id="appointmentSelect" name="appointmentSelect" value="-1">
			<input type="hidden" name="statusSelect" value="-1">
			<input type="hidden" name="pSelect" value="0">



	</div>
	
			
							
	<div class="modal-footer">
		<div class="inner-well">
			<a class="btn " data-dismiss="modal"  >Cancel</a>
			<button class="btn btn-primary">Checkout</button>
		</div>
	</div>
			
			
	</form>
	
 
</div>