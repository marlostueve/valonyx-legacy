<div id="newClient" class="black-box modal hide fade">
	<div class="modal-header tab-header">
		<button type="button" class="close" data-dismiss="modal">&times;</button>
		<span id="apptFormLabel">New Client</span>
	</div>
	
	<form class="fill-up" id="newClientForm" name="newClientForm" method="POST" action="<%= com.badiyan.uk.beans.CUBean.getProperty("cu.formSubmitPath") %>/clientNew.x"  >
	
	<div class="modal-body separator" >

			<div class="row-fluid">
				<div class="span6">
					<div class="padded" style="padding-bottom: 0; padding-top: 0;">

						<label for="firstname"><strong>First / Last Name:</strong></label>


						
						<div class="row-fluid">
							<div class="span5">
						
								<div class="input">
									<input type="text" id="firstname" name="firstname" placeholder="First Name" />
								</div>
						
							</div>
							<div class="span7">
												<div class="input">
							<input id="lastname" type="text" name="lastname" placeholder="Last Name" />
						</div>

						
							</div>
						</div>
						
						
						
						<div class="row-fluid">
							<div class="span7">
						
									<label for="filenumberx"><strong>File Number:</strong></label>
									<div class="input">
										<input id="filenumberx" type="text" name="filenumber" style="width: 90px;" />
									</div>
						
							</div>
							<div class="span5">
						
									<label for="rd1"><strong>Gender:</strong></label>
									<div class="input">
									  <input type="radio" name="gender" id="rd1" class="normal-radio" checked/>
									  <label for="rd1">Female</label>
									  <input type="radio" name="gender" id="rd2" class="normal-radio"/>
									  <label for="rd2">Male</label>
									</div>
						
							</div>
						</div>


						<label for="lnnc"><strong>Group Under Care:</strong></label>
						<div class="input">
							<input placeholder="Search for a group" id="lnnc" type="text" onkeyup="if (document.getElementById('lnnc').value.length > 0) {show_group_names = true; processCommand('getPeopleByLastName', document.getElementById('lnnc').value);}" name="lnnc" />
						</div>
						<div class="input">
							<select size="3" multiple id="groupSelect" name="groupSelect">
							</select>
						</div>

						<label for="relationshipSelect">group relationship</label>
						<div class="input">
							<select id="relationshipSelect" name="relationshipSelect" >
								<option value="<%= com.badiyan.uk.online.beans.GroupUnderCareBean.RELATIONSHIP_SELF_TYPE %>">Self</option>
								<option value="<%= com.badiyan.uk.online.beans.GroupUnderCareBean.RELATIONSHIP_SPOUSE_TYPE %>">Spouse</option>
								<option value="<%= com.badiyan.uk.online.beans.GroupUnderCareBean.RELATIONSHIP_CHILD_TYPE %>">Child</option>
								<option value="<%= com.badiyan.uk.online.beans.GroupUnderCareBean.RELATIONSHIP_GUARDIAN_TYPE %>">Guardian</option>
								<option value="<%= com.badiyan.uk.online.beans.GroupUnderCareBean.RELATIONSHIP_PARENT_TYPE %>">Parent</option>
								<option value="<%= com.badiyan.uk.online.beans.GroupUnderCareBean.RELATIONSHIP_PARTNER_TYPE %>">Partner</option>
								<option value="<%= com.badiyan.uk.online.beans.GroupUnderCareBean.RELATIONSHIP_OTHER_TYPE %>">Other</option>
							</select><input type="hidden" name="clientFileTypeSelect">
						</div>

						
					</div>
				</div>

				<div class="span6">

					<div class="padded" style="padding-bottom: 0; padding-top: 0;">
						
						<label for="email"><strong>Email:</strong></label>
						<div class="input">
							<input id="email" type="text" name="email" /><input type="hidden" name="hidden_filenumber">
						</div>

						<label for="phone"><strong>Phone Numbers:</strong></label>
						
						
						
						
						
						

						
						<div class="row-fluid">
							<div class="span6">
						
								<div class="input">
							<input id="phone" type="text" name="phone" placeholder="Home" />
						</div>
						
							</div>
							<div class="span6">
												<div class="input">
							<input id="cell" type="text" name="cell" placeholder="Cell" />
						</div>

						
							</div>
						</div>
						
						
						
						<label for="marketingPlanSelect"><strong>Referral From:</strong></label>
						<div class="input">
							<select id="marketingPlanSelect" name="marketingPlanSelect">
								<option value="0">-- PICK MARKETING CAMPAIGN --</option>
<%
java.util.Iterator mp_itr = com.badiyan.uk.online.beans.MarketingPlan.getMarketingPlans(adminCompany, true).iterator();
while (mp_itr.hasNext())
{
	com.badiyan.uk.online.beans.MarketingPlan marketing_plan = (com.badiyan.uk.online.beans.MarketingPlan)mp_itr.next();
%>
								<option value="<%= marketing_plan.getValue() %>"><%= marketing_plan.getLabel() %></option>
<%
}
%>
							</select>
						</div>
						
						<label for="lnnc2">or</label>
						<div class="input">
							<input type="text" id="lnnc2" placeholder="Search for a referral client"  onkeyup="waitOnKeyDown('lnnc2','referralClientSelect','getPeopleByKeyword');" />
						</div>
						<div class="input">
							<select size="3" multiple name="referralClientSelect" id="referralClientSelect">
							</select>
						</div>
						


					</div>
				</div>
			</div>
			
    



	</div>
	
			
							
	<div class="modal-footer">
		<div class="inner-well">
			<a class="btn " data-dismiss="modal"  >Cancel</a>
			<button class="btn btn-primary">Save changes</button>
		</div>
	</div>
			
			
	</form>
	
 
</div>