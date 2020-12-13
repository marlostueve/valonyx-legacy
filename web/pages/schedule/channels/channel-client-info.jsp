<div id="clientInfo" class="black-box modal hide fade"  >
	<div class="modal-header tab-header">
		<button type="button" class="close" data-dismiss="modal">&times;</button>
		<span id="checkoutFormLabel">&nbsp;</span> Client Info
	</div>
	
	<form class="fill-up" id="checkoutForm"  name="checkoutForm" method="POST" action="<%= CUBean.getProperty("cu.formSubmitPath") %>/checkout.x"  >
	
	<div class="modal-body separator" style="max-height: 500px;" >

		<!--
	  <h4>Text in a modal</h4>
	  <p>Duis mollis, est non commodo luctus, nisi erat porttitor ligula, eget lacinia odio sem.</p>
		-->
		
<%
// 
%>

			<div class="row-fluid">
				<div class="span5">
					<!-- <div class="padded" style="padding-bottom: 0; padding-top: 0;"> -->
						
						<label for="lastname"><strong>Client Search:</strong></label>
						<div class="input">
							<input type="text" id="co_lastname" placeholder="Last / First / File Number"  onkeyup="if (document.getElementById('lastname').value.length > 1) { waitOnKeyDown(); }" />
						</div>
						<div class="input">
							<select size="5" id="co_clientSelect" name="co_clientSelect" style="width: 205px;" >
							
							</select>
						</div>
						
						
				</div>
				<div class="span7">

						<div class="input">
								<table class="table table-striped table-bordered box">
									<tbody>
										<!--
										<tr>
											<td colspan="2" style="padding: 2px; color: #333333; outline-color: #333333; text-shadow: none; text-align: left; font-size: larger; "><strong>Marlo Stueve</strong></td>
										</tr>
										-->
										<tr>
											<td style="padding: 2px; color: #333333; outline-color: #333333; text-shadow: none; text-align: right; ">Next Appt:</td>
											<td style="padding: 2px; color: #333333; outline-color: #333333; text-shadow: none; text-align: right; "><strong>Wed, Mar 27 - 10:30 AM</strong></td>
											<td rowspan="2" style="padding: 2px; text-align: center; vertical-align:  middle;  "><a title="Appointment Report" class="btn btn-warning btn-mini"><i class="icon-print"></i> </a></td>
										</tr>
										<tr>
											<td style="padding: 2px; color: #333333; outline-color: #333333; text-shadow: none; text-align: right; ">Last Appt:</td>
											<td style="padding: 2px; color: #333333; outline-color: #333333; text-shadow: none; text-align: right; "><strong>Wed, Mar 27 - 10:30 AM</strong></td>
											
										</tr>
										<tr>
											<td style="padding: 2px; color: #333333; outline-color: #333333; text-shadow: none; text-align: right; ">Balance:</td>
											<td style="padding: 2px; color: #333333; outline-color: #333333; text-shadow: none; text-align: right; font-size: larger; "><strong>40.76</strong></td>
											<td style="padding: 2px; text-align: center;  "><a title="View Ledger" class="btn btn-info btn-mini"><i class="icon-book"></i> </a></td>
										</tr>
										<tr>
											<td style="padding: 2px; color: #333333; outline-color: #333333; text-shadow: none; text-align: right; ">Address:</td>
											<td colspan="2" style="padding: 2px; color: #333333; outline-color: #333333; text-shadow: none; text-align: right; "><strong>10015 Pioneer Trail (Personal)</strong></td>
										</tr>
										<tr>
											<td style="padding: 2px; color: #333333; outline-color: #333333; text-shadow: none; text-align: right; ">Phone:</td>
											<td colspan="2" style="padding: 2px; color: #333333; outline-color: #333333; text-shadow: none; text-align: right; "><strong>952-457-1596</strong></td>
										</tr>
										<tr>
											<td style="padding: 2px; color: #333333; outline-color: #333333; text-shadow: none; text-align: right; ">Email:</td>
											<td style="padding: 2px; color: #333333; outline-color: #333333; text-shadow: none; text-align: right; "><strong>marlo@badiyan.com</strong></td>
											<td style="padding: 2px; text-align: center; "><a title="View Client Details" class="btn btn-info btn-mini"><i class="icon-user"></i> </a></td>
										</tr>
									</tbody>
								</table>
						</div>


						
						
				</div>
			</div>
			<div class="row-fluid">

				<div class="span12">
					
					


					<div class="row-fluid">
						<div class="span12">
							<div class="tabbable black-box" style="margin-bottom: 10px;">


								<ul class="nav nav-tabs">
									<li class="active"><a href="#tab4" data-toggle="tab"><i class="icon-comment"></i> Comments</a></li>
									<li class=""><a href="#tab5" data-toggle="tab"><i class="icon-comments"></i> SOAP</a></li>
									<li class=""><a href="#tab6" data-toggle="tab"><i class="icon-cogs"></i> Care Details</a></li>
									<li class=""><a href="#tab7" data-toggle="tab"><i class="icon-phone"></i> Contact</a></li>
									<li class=""><a href="#tab8" data-toggle="tab"><i class="icon-bell"></i> Follow Up</a></li>
								</ul>
								<div class="tab-content" >

									<div class="tab-pane active" id="tab4" >
										<div class="separator" style="padding: 7px 7px 7px 7px; margin-bottom: 0px;">

											<div class="row-fluid">
												<div class="span5">

													<div class="row-fluid">
														<div class="span12">

															<div class="input">
																<select size="9" id="co_clientSelect" name="co_clientSelect" style="width: 190px;"  >
																</select>
															</div>
														</div>
													</div>
													<div class="row-fluid">
														<div class="span12">
															<a title="View Expanded" class="btn btn-info"><i class="icon-fullscreen"></i>  </a>
															<a title="Delete Selected Comment" class="btn btn-danger"><i class="icon-remove"></i> Delete</a>
														</div>
													</div>
												</div>
												<div class="span7">
													
													<div class="inner-well">

														<div class="row-fluid">
															<div class="span12">

																<div class="row-fluid">
																	<div class="span5">

																		<label for="co_lastname"><strong>Comment:</strong></label>
																	</div>
																	<div class="span7">

																		<div class="pull-left">
																			Show on check in:
																		</div>

																		<div class="pull-right">
																			<input rel="confirm-check" type="checkbox" id="showOnCheckIn" class="checky" checked="checked"/>
																			<label for="showOnCheckIn" class="checky"><span></span></label>
																		</div>
																	</div>
																</div>

																<div class="row-fluid">
																	<div class="span12">
																		<div class="input">
																			<input type="text" id="comment_subject" placeholder="Comment Subject"   />
																		</div>
																	</div>
																</div>

															</div>
														</div>

														<div class="row-fluid">
															<div class="span12">

																

																<textarea rows="3" name="memo" maxlength="250" placeholder="Place Comment Here"  ></textarea>
																

															</div>
														</div>
														<div class="row-fluid">

															<div class="span12">

																<a class="btn btn-warning"><i class="icon-envelope-alt"></i> Email</a>
																<a class="btn btn-success"><i class="icon-ok"></i> Save</a>

															</div>

														</div>
													
													</div>
													
												</div>
											</div>
										</div>
									</div>

									<div class="tab-pane" id="tab5">
										<div class="separator" style="padding: 7px 7px 7px 7px; margin-bottom: 0px;">

											<div class="row-fluid">
												<div class="span5">
													
													<div class="row-fluid">
														<div class="span12">
															
														
															<label for="selccc"><strong>Practice Area:</strong></label>
															<div class="input">
																<select id="selccc" name="selccc" style="width: 200px; ">
																	<option value="11">Account</option>
																	<option value="22">Credit Card</option>
																</select>
															</div>
															
															

															
															
														</div>
													</div>

													<div class="row-fluid">
														<div class="span12">
															<label for="co_clientSelectXX"><strong>Notes:</strong></label>
															<div class="input">
																<select size="4" id="co_clientSelectXX" name="co_clientSelect" style="width: 200px;"  >

																</select>
															</div>
														</div>
													</div>
													<div class="row-fluid">
														<div class="span12">
															<a title="View Expanded" class="btn btn-info"><i class="icon-fullscreen"></i>  </a>
															<a class="btn btn-danger"><i class="icon-remove"></i> Delete</a>

														</div>

													</div>

												</div>
												<div class="span7">
													
													<div class="tabbable black-box" style="margin-bottom: 10px;">


														<ul class="nav nav-tabs">
															<li class="active"><a href="#tab9" data-toggle="tab"> S Note</a></li>
															<li class=""><a href="#tab10" data-toggle="tab"> O Note</a></li>
															<li class=""><a href="#tab11" data-toggle="tab"> A Note</a></li>
															<li class=""><a href="#tab12" data-toggle="tab"> P Note</a></li>
														</ul>
														<div class="tab-content" >

															<div class="tab-pane active" id="tab9" >
																<div class="separator" style="padding: 7px 7px 7px 7px; margin-bottom: 0px;">

																	<div class="row-fluid">
																		<div class="span1">

																			<label for="s_note"><strong>S:</strong></label>

																		</div>
																		<div class="span11">

																			<div class="input">
																				<textarea rows="4" name="s_note" maxlength="250"  ></textarea>
																			</div>

																		</div>
																	</div>
																</div>
															</div>

															<div class="tab-pane" id="tab10" >
																<div class="separator" style="padding: 7px 7px 7px 7px; margin-bottom: 0px;">

																	<div class="row-fluid">
																		<div class="span1">

																			<label for="s_note"><strong>O:</strong></label>

																		</div>
																		<div class="span11">

																			<div class="input">
																				<textarea rows="4" name="s_note" maxlength="250"  ></textarea>
																			</div>

																		</div>
																	</div>
																</div>
															</div>

															<div class="tab-pane" id="tab11" >
																<div class="separator" style="padding: 7px 7px 7px 7px; margin-bottom: 0px;">

																	<div class="row-fluid">
																		<div class="span1">

																			<label for="s_note"><strong>A:</strong></label>

																		</div>
																		<div class="span11">

																			<div class="input">
																				<textarea rows="4" name="s_note" maxlength="250"  ></textarea>
																			</div>

																		</div>
																	</div>
																</div>
															</div>

															<div class="tab-pane" id="tab12" >
																<div class="separator" style="padding: 7px 7px 7px 7px; margin-bottom: 0px;">

																	<div class="row-fluid">
																		<div class="span1">

																			<label for="s_note"><strong>P:</strong></label>

																		</div>
																		<div class="span11">

																			<div class="input">
																				<textarea rows="4" name="s_note" maxlength="250"  ></textarea>
																			</div>

																		</div>
																	</div>
																</div>
															</div>
														</div>
													</div>



													<div class="row-fluid">

														<div class="span12">


															<a class="btn btn-warning"><i class="icon-print"></i> Print</a>
															<a class="btn btn-success"><i class="icon-ok"></i> Save</a>

														</div>

													</div>
												</div>
											</div>
										</div>
									</div>
									

									<div class="tab-pane" id="tab6">
										<div class="separator" style="padding: 7px 7px 7px 7px; margin-bottom: 0px;">

											<div class="row-fluid">
												<div class="span4">
													
													<div class="row-fluid">
														<div class="span12">
															
															<label for="selccc"><strong>Practice Area:</strong></label>
															<div class="input">
																<select id="selccc" name="selccc" style="width: 165px; ">
																	<option value="11">Naturopathy</option>
																	<option value="22">Chiropractic</option>
																</select>
															</div>
															
														</div>
													</div>
													<div class="row-fluid">
														<div class="span12">
															<a title="View Expanded" class="btn btn-info"><i class="icon-fullscreen"></i>  </a>

														</div>

													</div>

												</div>
												<div class="span8">
													
													<div class="inner-well">
													
														<div class="row-fluid">
															<div class="span12">

																<label for="selccc"><strong>Preferred Practitioner:</strong></label>
																<div class="input">
																	<select id="selccc" name="selccc" style="width: 200px; ">
																		<option value="11">Christine Stueve</option>
																		<option value="22">Michelle Guggenberger</option>
																	</select>
																</div>

															</div>
														</div>

														<div class="row-fluid">
															<div class="span12">

																<label for="selccc"><strong>Frequency of Care:</strong></label>
																<div class="input">
																	<select id="selccc" name="selccc" style="width: 50px; ">
																		<option value="11">1</option>
																		<option value="22">2</option>
																	</select>
																	<strong>time(s) every</strong>
																	<select id="selccc" name="selccc" style="width: 50px; ">
																		<option value="11">1</option>
																		<option value="22">2</option>
																	</select>
																	<select id="selccc" name="selccc" style="width: 90px; ">
																		<option value="11">Day(s)</option>
																		<option value="22">Week(s)</option>
																		<option value="22">Month(s)</option>
																		<option value="22">Year(s)</option>
																	</select>
																	<strong>OR</strong>
																	
																	
																</div>

															</div>
														</div>
																	
																	
															<div class="row-fluid">
																<div class="span12">

																	
																		<strong>PRN:&nbsp;</strong>
																		<input rel="confirm-check" type="checkbox" id="prn" class="checky" checked="checked"/>
																		<label for="prn" class="checky"><span></span></label>
																	
																</div>
															</div>

														<div class="row-fluid">

															<div class="span12">

																<a class="btn btn-danger"><i class="icon-remove"></i> Delete</a>
																<a class="btn btn-success"><i class="icon-ok"></i> Save</a>

															</div>

														</div>
													
													</div>


												</div>
											</div>
										</div>
										
									</div>
									
									<div class="tab-pane" id="tab7">
										<div class="separator" style="padding: 7px 7px 7px 7px; margin-bottom: 0px;">

											<div class="row-fluid">
												<div class="span7">
													
													<div class="row-fluid">
														<div class="span12">
															
															<label for="selccc"><strong>Practice Area:</strong></label>
															<div class="input">
																<select id="selccc" name="selccc" style="width: 225px; ">
																	<option value="11">Naturopathy</option>
																	<option value="22">Chiropractic</option>
																</select>
															</div>
															
														</div>
													</div>

													<div class="row-fluid">
														<div class="span12">
															<label for="co_clientSelectXX"><strong>Contact Attempts:</strong></label>
															<div class="input">
																<select size="4" id="co_clientSelectXX" name="co_clientSelect" style="width: 275px;"  >

																</select>
															</div>
														</div>
													</div>
													<div class="row-fluid">
													
														<div class="span12">
															
															
															<a title="View Expanded" class="btn btn-info"><i class="icon-fullscreen"></i>  </a>
															<a class="btn btn-danger" ><i class="icon-remove"></i> Delete</a>

														</div>

													</div>

												</div>
												<div class="span5">
													
													<div class="inner-well">
													
														<div class="row-fluid">
															<div class="span12">

																<label for="selccc"><strong>Contact Attempt:</strong></label>
																<div class="input">
																	<select id="selccc" name="selccc" style="width: 175px; ">
																		<option value="11">1st</option>
																		<option value="22">2nd</option>
																		<option value="22">3rd</option>
																		<option value="22">Send to Practitioner Call List</option>
																	</select>
																</div>

															</div>
														</div>
													
														<div class="row-fluid">
															<div class="span12">

																<label for="selccc"><strong>Contact Result:</strong></label>
																<div class="input">
																	<select id="selccc" name="selccc" style="width: 175px; ">
																		<option value="11">No Answer / Busy</option>
																		<option value="22">Left Voice Mail</option>
																		<option value="22">Left Message with Family Member</option>
																		<option value="22">Requested Call Back</option>
																		<option value="22">Send Email</option>
																		<option value="22">Other</option>
																	</select>
																	<input type="text" id="ci_other" name="ci_other" placeholder="Other Result" />
																</div>

															</div>
														</div>

																
														<div class="row-fluid">

															<div class="span12">
																
																	<a class="btn btn-success"><i class="icon-ok"></i> Save</a>

															</div>

														</div>	

													
													</div>


												</div>
											</div>
										</div>
									</div>
									
									<div class="tab-pane" id="tab8">
										<div class="separator" style="padding: 7px 7px 7px 7px; margin-bottom: 0px;">

											<div class="row-fluid">
												<div class="span7">
													
													<div class="row-fluid">
														<div class="span12">
															
															<label for="selccc"><strong>Practice Area:</strong></label>
															<div class="input">
																<select id="selccc" name="selccc" style="width: 225px; ">
																	<option value="11">Naturopathy</option>
																	<option value="22">Chiropractic</option>
																</select>
															</div>
															
														</div>
													</div>

													<div class="row-fluid">
														<div class="span12">
															<label for="co_clientSelectXX"><strong>Contact Attempts:</strong></label>
															<div class="input">
																<select size="4" id="co_clientSelectXX" name="co_clientSelect" style="width: 275px;"  >

																</select>
															</div>
														</div>
													</div>
													<div class="row-fluid">
													
														<div class="span12">
															
															
															<a title="View Expanded" class="btn btn-info"><i class="icon-fullscreen"></i>  </a>
															<a class="btn btn-danger" ><i class="icon-remove"></i> Delete</a>
																<a class="btn btn-success"><i class="icon-ok"></i> Save</a>

														</div>

													</div>

												</div>
												<div class="span5">
													
													<div class="inner-well">
													
														<div class="row-fluid">
															<div class="span12">

																<label for="selccc"><strong>Contact Attempt:</strong></label>
																<div class="input">
																	<select id="selccc" name="selccc" style="width: 175px; ">
																		<option value="11">1st</option>
																		<option value="22">2nd</option>
																		<option value="22">3rd</option>
																		<option value="22">Send to Practitioner Call List</option>
																	</select>
																</div>

															</div>
														</div>
													
														<div class="row-fluid">
															<div class="span12">

																<label for="selccc"><strong>Contact Result:</strong></label>
																<div class="input">
																	<select id="selccc" name="selccc" style="width: 175px; ">
																		<option value="11">No Answer / Busy</option>
																		<option value="22">Left Voice Mail</option>
																		<option value="22">Left Message with Family Member</option>
																		<option value="22">Requested Call Back</option>
																		<option value="22">Send Email</option>
																		<option value="22">Other</option>
																	</select>
																</div>

															</div>
														</div>

														<div class="row-fluid">
															<div class="span12">

																<label for="selccc"><strong>Contact In:</strong></label>
																<div class="input">
																	<select id="selccc" name="selccc" style="width: 65px; ">
																		<option value="11">1</option>
																		<option value="22">2</option>
																		<option value="22">3</option>
																		<option value="22">4</option>
																		<option value="22">5</option>
																		<option value="22">6</option>
																		<option value="22">15</option>
																		<option value="22">30</option>
																		<option value="22">45</option>
																	</select>
																	<select id="selccc" name="selccc" style="width: 90px; ">
																		<option value="11">Day(s)</option>
																		<option value="22">Week(s)</option>
																		<option value="22">Month(s)</option>
																		<option value="22">Year(s)</option>
																	</select>
																	
																	
																</div>

															</div>
														</div>
																	

													
													</div>


												</div>
											</div>
										</div>
									</div>
									
									
									
								</div>
							</div>
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
			<a class="btn " data-dismiss="modal"  >Close</a>
		</div>
	</div>
			
	</form>
	
 
</div>