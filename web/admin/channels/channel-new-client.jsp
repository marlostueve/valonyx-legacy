<div id="dialogNewClient">
<div class="hd"><h2><span id="apptFormLabel">New Client</span></h2></div>
<div class="bd" id="bd-r">
<form name="newClientForm" method="POST" action="<%= CUBean.getProperty("cu.formSubmitPath") %>/clientNew.x"  >
	
	<div class="clear"></div>
	<table border="0" cellpadding="0" cellspacing="0">
		<tr>
			<td><label for="firstname"><strong>First Name:</strong></label></td>
			<td><label for="lastname"><strong>Last Name:</strong></label></td>
		</tr>
		<tr>
			<td><input id="firstname" type="textbox" name="firstname" /></td>
			<td><input id="lastname" type="textbox" name="lastname" /></td>
		</tr>
		<tr>
			<td><label for="filenumber"><strong>File #:</strong></label></td>
			<td><label for="email"><strong>Email:</strong></label></td>
		</tr>
		<tr>
			<td><input id="filenumberx" type="textbox" name="filenumber" style="width: 70px;" />&nbsp;<input onclick="if (this.checked) {document.newClientForm.hidden_filenumber.value=document.newClientForm.filenumber.value; document.newClientForm.filenumber.value=''; document.newClientForm.filenumber.disabled=true; } else {document.newClientForm.filenumber.disabled=false; document.newClientForm.filenumber.value=document.newClientForm.hidden_filenumber.value;}" type="checkbox" name="prospect">Prospect</td>
			<td><input id="email" type="textbox" name="email" /><input type="hidden" name="hidden_filenumber"></td>
		</tr>
		<tr>
			<td><label for="phone"><strong>Home:</strong></label></td>
			<td><label for="cell"><strong>Cell:</strong></label></td>
		</tr>
		<tr>
			<td><input id="phone" type="textbox" name="phone" /></td>
			<td><input id="cell" type="textbox" name="cell" /></td>
		</tr>
	</table>
	
	<div class="clear"></div>

<table border="0" cellpadding="0" cellspacing="0">
	<tr>
		<td valign="top">
	<table border="0" cellpadding="0" cellspacing="0">
		<tr>
			<td>
	<input type="radio" name="gender" value="Female" checked /><strong>Female</strong>
				<input type="radio" name="gender" value="Male" /><strong>Male</strong>
			</td>
		</tr>
		<tr>
			<td>
	
	<input type="radio" name="group" value="New" checked /><strong>Add to new Group Under Care</strong>
	<div class="clear"></div>
	<input type="radio" name="group" value="Existing"  /><strong>Add to <span id="selectedGroup">Existing Group</span></strong>
	<input type="hidden" name="group_id">
	<div class="clear"></div>
			</td>
		</tr>
		<tr>
			<td>

	<input id="lnnc" type="textbox" onkeyup="if (document.getElementById('lnnc').value.length > 0) {show_group_names = true; processCommand('getPeopleByLastName', document.getElementById('lnnc').value);}" name="lnnc" />
	
	<div class="clear"></div>
	
	<label for="groupSelect"><strong>Group:</strong></label>
	<div class="clear"></div>
	<select multiple style="width: 200px;" name="groupSelect">
	    <option value="-1">-- SEARCH FOR A GROUP --</option>
	</select>

			</td>
		</tr>


		<tr>
			<td><strong>Relationship:</strong></td>
			<!-- <td><strong>Client File Type:</strong></td> -->
		</tr>
		<tr>
			<td><select style="width: 150px;" name="relationshipSelect">
	    <option value="<%= GroupUnderCareBean.RELATIONSHIP_SELF_TYPE %>">Self</option>
	    <option value="<%= GroupUnderCareBean.RELATIONSHIP_SPOUSE_TYPE %>">Spouse</option>
	    <option value="<%= GroupUnderCareBean.RELATIONSHIP_CHILD_TYPE %>">Child</option>
	    <option value="<%= GroupUnderCareBean.RELATIONSHIP_GUARDIAN_TYPE %>">Guardian</option>
	    <option value="<%= GroupUnderCareBean.RELATIONSHIP_PARENT_TYPE %>">Parent</option>
	    <option value="<%= GroupUnderCareBean.RELATIONSHIP_PARTNER_TYPE %>">Partner</option>
	    <option value="<%= GroupUnderCareBean.RELATIONSHIP_OTHER_TYPE %>">Other</option>
			</select><input type="hidden" name="clientFileTypeSelect"></td><!--
			<td><select name="clientFileTypeSelect">
<%
Iterator itr2 = GroupUnderCareMemberTypeBean.getMemberTypes(adminCompany).iterator();
if (itr2.hasNext())
{
%>
	    <option value="-1">-- SELECT A FILE TYPE --</option>
<%
    while (itr2.hasNext())
    {
	GroupUnderCareMemberTypeBean file_type = (GroupUnderCareMemberTypeBean)itr2.next();
%>
	    <option value="<%= file_type.getValue() %>"><%= file_type.getLabel() %></option>
<%
    }
}
else
{
%>
	    <option value="-1">-- No Client File Types Found --</option>
<%
}
%>
	</select></td> -->
		</tr>
	</table>
	</td>
		<td>
			&nbsp;&nbsp;
		</td>
		<td valign="top">
			<table border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td>
						<strong>Referral From:</strong>
					</td>
				</tr>
				<tr>
					<td><select style="width: 200px;" name="marketingPlanSelect">
				<option value="0">-- SELECT A MARKETING CAMPAIGN --</option>
<%
Iterator mp_itr = MarketingPlan.getMarketingPlans(adminCompany, true).iterator();
while (mp_itr.hasNext())
{
	MarketingPlan marketing_plan = (MarketingPlan)mp_itr.next();
%>
				<option value="<%= marketing_plan.getValue() %>"><%= marketing_plan.getLabel() %></option>
<%
}
%>
					</select></td>
				</tr>
				<tr>
					<td>
						<strong>or</strong>
					</td>
				</tr>
				<tr>
					<td>
	<input id="lnnc2" type="textbox" onkeyup="if (document.getElementById('lnnc2').value.length > 0) {show_referral_names = true; processCommand('getPeopleByLastName', document.getElementById('lnnc2').value);}" name="lnnc2" />
					</td>
				</tr>
				<tr>
					<td><select multiple style="width: 200px;" name="referralClientSelect">
				<option value="0">-- SEARCH FOR A CLIENT --</option>
					</select></td>
				</tr>
			</table>
		</td>
	</tr>
</table>
</form>
</div>
</div>