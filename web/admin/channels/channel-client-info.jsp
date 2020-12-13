<div id="dialog2">
	<div class="hd"><h2><span id="clientInfoFormLabel">&nbsp;Client Info&nbsp;</span></h2></div>
<div class="bd" id="bd-r">
<form name="clientInfoForm" method="POST" action="<%= CUBean.getProperty("cu.formSubmitPath") %>/clientInfo.x"  >

	<input type="hidden" name="cid" value="-1">

	<div>

		<div id="panel4">
			<div class="hd">Appointment Report</div>
			<div class="bd">
				<table width="100%">
				<tr>
					<td><strong>Start Date:</strong></td>
					<td><input type="text" name="ar_start_dt" value="<%= now_str %>" style="width: 75px;" /></td>
				</tr>
				<tr>
					<td>&nbsp;</td>
					<td><input type="checkbox" onclick="if (this.checked) document.clientInfoForm.ar_start_dt.value=''; else document.clientInfoForm.ar_start_dt.value='<%= now_str %>';" />Include All Past Appointments</td>
				</tr>
				<tr>
					<td><strong>End Date:</strong></td>
					<td><input type="text" name="ar_end_dt" style="width: 75px;" /></td>
				</tr>
				<tr>
					<td>&nbsp;</td>
					<td><input type="checkbox" onclick="if (this.checked) document.clientInfoForm.ar_end_dt.value=''; else document.clientInfoForm.ar_end_dt.value='<%= now_str %>';" checked="checked" />Include All Future Appointments</td>
				</tr>
				<tr>
					<td colspan="2" align="right"><input type="hidden" name="ar_arg" /><input type="button" id="sButton3s" name="sButton3s" value="View Report" /></td>
				</tr>
				</table>
			</div>
		</div>
		
		<table border="0" cellpadding="0" cellspacing="0">
			<tr>
			    <td width="35%" valign="top">
				<strong>Search Last Name:</strong>
				<input style="width: 200px;" id="ci_lastname" type="textbox" onkeyup="if (document.getElementById('ci_lastname').value.length > 1) {show_ci_names = true; processCommand('getPeopleByLastName', document.getElementById('ci_lastname').value.replace(/\'/g,'\\\''));}" name="ci_lastname" />
				<strong>Search First / File Number:</strong>
				<input style="width: 120px;" id="ci_firstname" type="textbox" onkeyup="if (document.getElementById('ci_firstname').value.length > 1) {show_ci_names = true; processCommand('getPeopleByFirstName', document.getElementById('ci_firstname').value);}" name="ci_firstname" />&nbsp;/&nbsp;<input id="ci_filenumber" type="textbox" onkeyup="if (document.getElementById('ci_filenumber').value.length > 0) {show_ci_names = true; processCommand('getPeopleByFileNumber', document.getElementById('ci_filenumber').value);}" name="ci_filenumber" style="width: 68px;" />

				<div class="clear"></div>

				<select size="5" name="ci_clientSelect" multiple="false" style="width: 200px;" onclick="showClientInfo(this.value);">
				    <option value="-1">-- SEARCH FOR A CLIENT --</option>
				</select>
			    </td>
			    <td width="65%" valign="top">
				    <table border="0" cellpadding="0" cellspacing="0">
					    <tr><td align="right"><strong>Client:</strong>&nbsp;</td><td><span id="clientInfoFormClient">&nbsp;</span></td></tr>
					    <tr><td align="right"><strong>Next Appt:</strong>&nbsp;</td><td><span id="clientInfoFormNext">&nbsp;</span></td></tr>
					    <tr><td align="right"><strong>Last Appt:</strong>&nbsp;</td><td><span id="clientInfoFormLast">&nbsp;</span></td></tr>
					    <tr><td>&nbsp;</td><td><a href="javascript:appointmentReport('c' + document.clientInfoForm.cid.value);">Appointment Report</a></td></tr>
					    <tr><td align="right"><strong>Balance:</strong>&nbsp;</td><td><span id="clientInfoFormBalance">&nbsp;</span></td></tr>
					    <tr><td align="right"><strong>Address:</strong>&nbsp;</td><td><span id="clientInfoFormAddr">&nbsp;</span></td></tr>
					    <tr><td align="right"><strong>Phone:</strong>&nbsp;</td><td><span id="clientInfoFormPhone">&nbsp;</span></td></tr>
					    <tr><td align="right"><strong>Email:</strong>&nbsp;</td><td><a id="clientInfoFormEmailAnchor" href=""><span id="clientInfoFormEmail">&nbsp;</span></a></td></tr>
					    <!--
					    <tr><td colspan="2">&nbsp;</td></tr>
					    <tr>
						    <td colspan="2"><p>
		<input type="radio" name="statusInput" value="0" checked>Active
		<input type="radio" name="statusInput" value="1">Black Hole
		<input type="radio" name="statusInput" value="2">Inactive
		<input type="button" id="sButton3h" name="sButton3h" value="Save Status" disabled>
	    </p></td>
					    </tr>
					    -->
				    </table>
			    </td>


			</tr>
		</table>


	</div>
	<div id="cal2Container"></div>
        <div id="demo2" class="yui-navset">
            <ul class="yui-nav">
                <li class="selected"><a href="#tab1"><em>Notes</em></a></li>
                <li><a href="#tab2"><em>Care Details</em></a></li>
                <li><a href="#tab3"><em>Contact Status</em></a></li>
                <li><a href="#tab4"><em>Follow Up</em></a></li>
                <li><a href="#tab5"><em>SOAP Notes</em></a></li>
                <li><a href="#tab6"><em>Health History</em></a></li>
            </ul>
            <div class="yui-content">
                <div id="tab1">
		    <table border="0" cellpadding="0" cellspacing="0" >
			    <tr>
				<td valign="top">
				    <select size="3" name="ci_notesSelect" multiple="false" style="width: 200px;" onclick="fetchNote(this.value);">
					<option value="-1">-- NO NOTES ENTERED --</option>
				    </select>
				</td>
				<td>
				<strong>Note Subject:</strong>
				<input style="width: 250px;" id="ci_subject" type="textbox"  name="ci_subject" /><br />
				<input type="checkbox" name="ci_alert">Show note on client Check In<br />
				<input type="button" id="sButton3f" name="sButton3f" value="Save Note">
				<input type="button" id="sButton3g" name="sButton3g" value="Email Note">
				<input type="button" id="sButton3e" name="sButton3e" value="Delete Selected Note">
				</td>
			    </tr>
			    <tr>
				<td colspan="2">
				    <textarea name="info-editor" id="info-editor" rows="6" style="height: 150px; width: 580px;" ></textarea>
				    <input name="editorInput" type="hidden" >
				</td>
			    </tr>
		    </table>
                </div>
                <div id="tab2">
					<div>
						<strong>Practice Area</strong>:&nbsp;
						<select name="paList">

							<option value="0">-- SELECT A PRACTICE AREA --
<%
itr = practice_areas.iterator();
if (itr.hasNext())
{
    while (itr.hasNext())
    {
	PracticeAreaBean practice_area = (PracticeAreaBean)itr.next();
%>
							<option value="<%= practice_area.getValue() %>"><%= practice_area.getLabel() %>
<%
    }
}
%>
						</select><br />
						<strong>Preferred Practitioner</strong>:&nbsp;
						<select name="ppList">
							<option value="0">-- SELECT A PRACTITIONER --
<%
practitioner_itr = practitioners.iterator();
while (practitioner_itr.hasNext())
{
    UKOnlinePersonBean practitioner = (UKOnlinePersonBean)practitioner_itr.next();
%>
							<option value="<%= practitioner.getValue() %>"><%= practitioner.getLabel() %>
<%
}
%>
						</select><br />
						<strong>Frequency of Care</strong>:&nbsp;
						<select name="fList1" >
							<option value="1">1
							<option value="2">2
							<option value="3">3
							<option value="4">4
							<option value="5">5
							<option value="6">6
							<option value="7">7
							<option value="8">8
							<option value="9">9
						</select>
						<strong>time(s) every</strong>
						<select name="fList3" >
							<option value="1">1
							<option value="2">2
							<option value="3">3
							<option value="4">4
							<option value="5">5
							<option value="6">6
							<option value="7">7
							<option value="8">8
							<option value="9">9
						</select>
						<select name="fList2" >
							<option value="1">Day(s)
							<option value="2">Week(s)
							<option value="3">Month(s)
							<option value="4">Year(s)
						</select>
						&nbsp;<strong>OR</strong>
						&nbsp;<input onclick="if (this.checked) { } else { }" type="checkbox" name="prn"><strong>PRN</strong>
					</div>
					<div><input type="button" id="sButton3a" name="sButton3a" value="Add/Update Frequency of Care Details">
					<select name="foqList" size="5" style="width: 100%;">
					</select><input type="button" id="sButton3d" name="sButton3d" value="Delete Frequency of Care Details">
					</div>
                </div>
                <div id="tab3">
					<strong>Practice Area</strong>:&nbsp;
					<select name="paCList">
						<option value="0">-- CONTACT FOR ALL PRACTICE AREAS --
<%
itr = practice_areas.iterator();
if (itr.hasNext())
{
    while (itr.hasNext())
    {
	PracticeAreaBean practice_area = (PracticeAreaBean)itr.next();
%>
						<option value="<%= practice_area.getValue() %>"><%= practice_area.getLabel() %>
<%
    }
}
%>
					</select><br />
					<strong>Contact Attempt</strong>:&nbsp;
					<select name="contactAttempt">
						<option value="1">1st
						<option value="2">2nd
						<option value="3">3rd
						<option value="4">Send to Practitioner Contact List
					</select><br />
					<!--
					<strong>Contact Reason</strong>:&nbsp;
					<select name="contactFollowup">
						<option value="0">-- CONTACT FOR MISSED APPOINTMENT --
						<option value="1">Followup: Herbs: Mix - Christine Stueve review on 03/01/10 [PAST DUE]
						<option value="2">Followup: Herbs: Mix - Christine Stueve review on 04/15/10 [PAST DUE]
						<option value="3">Followup: Herbs: Review - Christine Stueve review on 04/20/10 [PAST DUE]
						<option value="4">Followup: Herbs: Mix - Christine Stueve review on 04/21/10 [PAST DUE]
					</select><br />
					-->
					<table border="0" cellpadding="2" cellspacing="2">
						<tr>
							<td>
								<!--
								<input type="radio" name="contactInput" value="0" onclick="document.forms[1].otherInput.disabled = true;">Scheduled New Appointment<br />
								<input type="radio" name="contactInput" value="1" disabled onclick="document.forms[1].otherInput.disabled = true;">Confirmed Next Appointment<br />
								-->
								<input type="radio" name="contactInput" value="0" onclick="document.forms[1].otherInput.disabled = true;">No Answer / Busy<br />
								<input type="radio" name="contactInput" value="1" onclick="document.forms[1].otherInput.disabled = true;">Left Voice Mail<br />
								<input type="radio" name="contactInput" value="2" onclick="document.forms[1].otherInput.disabled = true;">Left Message with Family Member<br />
								<input type="radio" name="contactInput" value="3" onclick="document.forms[1].otherInput.disabled = true;">Requested Call Back<br />
								<!--
								<input type="radio" name="contactInput" value="4" onclick="document.forms[1].otherInput.disabled = true;">Left without future Appointment<br />
								-->
							</td>
							<td>
								<input type="radio" name="contactInput" value="4" onclick="document.forms[1].otherInput.disabled = true;">Sent Email<br />
								<input type="radio" name="contactInput" value="5" onclick="document.forms[1].otherInput.disabled = !this.checked;">Other<br />
								<input type="text" name="otherInput" disabled>
							</td>
						</tr>
						<tr>
							<td>
								<input type="radio" name="todoInput" value="0">Contact On&nbsp;<input type="text" name="callOnInput" onkeyup="javascript:document.forms[1].todoInput[0].checked = true;"><input type="submit" name="calendar" value="..." onclick="YAHOO.example.calendar.cal2.show();return false;">
							</td>
							<td>
								<input type="radio" name="todoInput" value="1">Contact In
								<select name="eList1" onchange="javascript:document.forms[1].todoInput[1].checked = true;">
									<option value="1">1
									<option value="2">2
									<option value="3">3
									<option value="4">4
									<option value="5">5
									<option value="6">6
									<option value="15">15
									<option value="30">30
									<option value="45">45
								</select>
								<select name="eList2" onchange="javascript:document.forms[1].todoInput[1].checked = true;">
									<option value="1">Days
									<option value="2">Weeks
									<option value="3">Months
									<option value="4">Years
								</select>
							</td>
						</tr>
					</table>
					<div id="explore" style="width: 50%;">
					</div>
					<div id="stats" style="width: 50%;">
					</div>
					<div class="clear"></div>
					<div><input type="button" id="sButton1a" name="sButton1a" value="Add Contact Status">
						<select name="eventList" size="5" style="width: 100%;">
						</select><input type="button" id="sButton1d" name="sButton1d" value="Delete Contact Status">
					</div>
                </div>
                <div id="tab4">
					<strong>Follow Up Reason</strong>:&nbsp;
					<select name="rrCList">
						<option value="0">-- SELECT A REASON --
<%
itr = ReviewReason.getReviewReasons(adminCompany).iterator();
if (itr.hasNext())
{
	while (itr.hasNext())
	{
		ReviewReason review_reason = (ReviewReason)itr.next();
%>
						<option value="<%= review_reason.getValue() %>"><%= review_reason.getLabel() %>
<%
	}
}
%>
					</select><br />
					<strong>Follow Up Practitioner</strong>:&nbsp;
					<select name="rpList">
						<option value="0">-- SELECT A PRACTITIONER --
<%
practitioner_itr = practitioners.iterator();
while (practitioner_itr.hasNext())
{
	UKOnlinePersonBean practitioner = (UKOnlinePersonBean)practitioner_itr.next();
%>
						<option value="<%= practitioner.getValue() %>"><%= practitioner.getLabel() %>
<%
}
%>
					</select><br />
					<table border="0" cellpadding="2" cellspacing="2">
						<tr>
							<td>
								<input type="radio" name="fuInput" value="1">Left Voice Mail<br />
								<input type="radio" name="fuInput" value="3">Requested Call Back
							</td>
							<td>
								<input type="radio" name="fuInput" value="4">Sent Email
							</td>
						</tr>
						<tr>
							<td>
								<input type="radio" name="reviewInput" value="0">Follow Up On&nbsp;<input type="text" name="reviewOnInput" onkeyup="javascript:document.forms[1].todoInput[0].checked = true;"><input type="submit" name="calendar" value="..." onclick="cal_for_review = true;YAHOO.example.calendar.cal2.show();return false;">
							</td>
							<td>
								<input type="radio" name="reviewInput" value="1">Follow Up In
								<select name="rList1" onchange="javascript:document.forms[1].reviewInput[1].checked = true;">
									<option value="1">1
									<option value="2">2
									<option value="3">3
									<option value="4">4
									<option value="5">5
									<option value="6">6
									<option value="15">15
									<option value="30">30
									<option value="45">45
								</select>
								<select name="rList2" onchange="javascript:document.forms[1].reviewInput[1].checked = true;">
									<option value="1">Days
									<option value="2">Weeks
									<option value="3">Months
									<option value="4">Years
								</select>
							</td>
						</tr>
					</table>
					<table>
						<tr>
							<td style="vertical-align: text-top;"><strong>Note</strong>:&nbsp;</td>
							<td><textarea name="followUpNote" rows="2" style="width: 500px;"></textarea></td>
						</tr>
					</table>
					<div id="explore" style="width: 50%;">
					</div>
					<div id="stats" style="width: 50%;">
					</div>
					<div class="clear"></div>
					<div><input type="button" id="sButton3i" name="sButton3i" value="Add/Update Client Review">&nbsp;<input type="button" id="sButton3k" name="sButton3k" value="Mark As Reviewed">
						<select name="reviewList" size="5" onclick="processCommand('selectReviewItem',this.value);" style="width: 100%;">
						</select><input type="button" id="sButton3j" name="sButton3j" value="Delete Client Review">
					</div>
                </div>
                <div id="tab5">
		    <table border="0" cellpadding="0" cellspacing="0" >
			    <tr>
				<td valign="top">
				    <strong>Practice Area</strong>:&nbsp;
				    <select name="paSOAPList" style="width: 200px;">
<%
if (practice_areas.size() > 1) {
%>
					    <option value="0">-- SELECT A PRACTICE AREA --
<%
}
itr = practice_areas.iterator();
if (itr.hasNext())
{
while (itr.hasNext())
{
    PracticeAreaBean practice_area = (PracticeAreaBean)itr.next();
%>
					    <option value="<%= practice_area.getValue() %>"><%= practice_area.getLabel() %>
<%
}
}
%>
				    </select>
					<select size="6" name="ci_soapSelect" multiple="false" style="width: 200px;" onclick="fetchSoap(this.value);">
					<option value="-1">-- NO NOTES ENTERED --</option>
				    </select>
				    <select size="9" name="ci_noteOptionSelect" multiple="false" style="width: 200px;" onclick="statementSelect(this);">

				    </select>
				</td>
				<td>
				    <table>
					<tr>
					    <td><span style="font-size:large;">S</span></td>
					    <td><textarea name="s_note" rows="4" style="width: 350px;" onfocus="soapSelect('S');"></textarea></td>
					</tr>
					<tr>
					    <td><span style="font-size:large;">O</span></td>
					    <td><textarea name="o_note" rows="4" style="width: 350px;" onfocus="soapSelect('O');"></textarea></td>
					</tr>
					<tr>
					    <td><span style="font-size:large;">A</span></td>
					    <td><textarea name="a_note" rows="4" style="width: 350px;" onfocus="soapSelect('A');"></textarea></td>
					</tr>
					<tr>
					    <td><span style="font-size:large;">P</span></td>
					    <td><textarea name="p_note" rows="4" style="width: 350px;" onfocus="soapSelect('P');"></textarea></td>
					</tr>
				    </table>
				</td>
			    </tr>
			    <tr>
				<td colspan="2">
				    <input type="button" id="sButton3l" name="sButton3l" value="Delete Selected Note">
				    <input type="button" id="sButton3r" name="sButton3r" value="Print Note">
				    <input type="button" id="sButton3m" name="sButton3m" value="Save Note">
				</td>
			    </tr>
		    </table>
                </div>
                <div id="tab6">
		    <table border="0" cellpadding="0" cellspacing="0" >
			    <tr>
				<td>
				    <textarea name="history" rows="14" style="width: 575px;" ></textarea>
				</td>
			    </tr>
			    <tr>
				<td colspan="2">
				    <input type="button" id="sButton3n" name="sButton3n" value="Save History">
				</td>
			    </tr>
		    </table>
                </div>
            </div>
        </div>

</form>
</div>
</div>