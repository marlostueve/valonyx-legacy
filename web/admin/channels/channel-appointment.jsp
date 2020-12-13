<div id="dialog1">
    <div class="hd"><h2><span id="apptFormLabel">&nbsp;</span></h2></div>
    <div class="bd" id="bd-r">
	<form name="newApptForm" method="POST" action="<%= CUBean.getProperty("cu.formSubmitPath") %>/newAppointment.x"  >
    
	    <input type="hidden" name="appointmentSelect" value="-1">
	    <input type="hidden" name="statusSelect" value="-1">
	    <input type="hidden" name="pSelect" value="0">

	    <label for="practitionerSelect"><strong>Practitioner:</strong></label>
	    <select id="practitionerSelect" name="practitionerSelect" onchange="if (document.forms['newApptForm'].typeSelect.value == 0) { showApptTypes(this.value); }">
		    <option value="0">-- SELECT A PRACTITIONER --</option>
<%
practitioner_itr = practitioners.iterator();
while (practitioner_itr.hasNext())
{
    UKOnlinePersonBean practitioner = (UKOnlinePersonBean)practitioner_itr.next();
%>
		    <option value="<%= practitioner.getValue() %>"><%= practitioner.getLabel() %></option>
<%
}
%>
	    </select>
	    <!-- <input type="hidden" name="practitionerSelect" value=""> -->

	    <div class="clear"></div>

	    <label for="typeSelect"><strong>Appointment Type:</strong></label>
	    <select name="typeSelect" onchange="document.forms['newApptForm'].duration.value = typeArray[document.forms['newApptForm'].typeSelect.value]['duration'];">
	    </select><button id="acp3" type="button">Next Appt</button>

	    <div class="clear"></div>

	    <label for="duration"><strong>Duration:</strong></label><input type="textbox" maxlength="3" name="duration" style="width: 50px;" />minutes

	    <div class="clear"></div>

	    <label for="appt_date"><strong>Date:</strong></label>
	    <input type="textbox" id="appt_date" name="appt_date" style="width: 210px;" />
	    
	    <button id="acp" type="button">...</button>
	    <div id="cal3Container"></div>

	    <div class="clear"></div>

	    <label for="hr"><strong>Time:</strong></label>
	    <select name="hr">
		<option value="1">1</option>
		<option value="2">2</option>
		<option value="3">3</option>
		<option value="4">4</option>
		<option value="5">5</option>
		<option value="6">6</option>
		<option value="7">7</option>
		<option value="8">8</option>
		<option value="9">9</option>
		<option value="10">10</option>
		<option value="11">11</option>
		<option value="12">12</option>
	    </select>
	    <select name="mn">
		<option value="00">00</option>
		<option value="05">05</option>
		<option value="10">10</option>
		<option value="15">15</option>
		<option value="20">20</option>
		<option value="25">25</option>
		<option value="30">30</option>
		<option value="35">35</option>
		<option value="40">40</option>
		<option value="45">45</option>
		<option value="50">50</option>
		<option value="55">55</option>
	    </select>
	    <select name="ampm">
		<option value="AM">AM</option>
		<option value="PM">PM</option>
	    </select>

	    <div class="clear"></div>

	    <label for="lastname"><strong>Search Last Name:</strong></label><input id="lastname" type="textbox" onkeyup="if (document.getElementById('lastname').value.length > 0) {processCommand('getPeopleByLastName', document.getElementById('lastname').value.replace(/\'/g,'\\\''));}" name="lastname" />
	    <label for="firstname"><strong>Search First / File Number:</strong></label><input id="firstname" type="textbox" onkeyup="if (document.getElementById('firstname').value.length > 0) {processCommand('getPeopleByFirstName', document.getElementById('firstname').value);}" name="firstname" style="width: 100px;" />&nbsp;/&nbsp;<input id="filenumber" type="textbox" onkeyup="if (document.getElementById('filenumber').value.length > 0) {processCommand('getPeopleByFileNumber', document.getElementById('filenumber').value);}" name="filenumber" style="width: 50px;" />

	    <div class="clear"></div>

	    <label for="clientSelect"><strong>Client:</strong></label>
	    <select size="4" name="clientSelect" multiple="true" style="width: 200px;">
		<option value="-1">-- SEARCH FOR A CLIENT --</option>
	    </select>

		<div class="clear"></div>
		<br />
		<input type="checkbox" id="include_christine" name="include_christine"><label for="include_christine"><strong>Include Christine:</strong></label> (check to include Christine in the appt.)<br /><br />
	   
		<div class="clear"></div>








		<div id="demo3" class="yui-navset">
            <ul class="yui-nav">
                <li class="selected"><a href="#tab1"><em>Comments</em></a></li>
                <li><a href="#tab2"><em>Recurrence</em></a></li>
            </ul>
            <div class="yui-content">
                <div id="tab1">
					<div class="clear"></div>
					<label for="comments"><strong>Comments:</strong></label>
					<textarea name="comments" rows="3" style="width: 200px;"></textarea>
					<div class="clear"></div>
                </div>
                <div id="tab2">
                    <label for="recurVal"><strong>Every:</strong></label><input type="textbox" maxlength="3" name="recurVal" style="width: 50px;" />
					<select name="recurPeriod">
					<option value="1">Hours</option>
					<option value="2">Days</option>
					<option value="3" selected>Weeks</option>
					<option value="4">Months</option>
					<option value="5">Years</option>
					</select>

					<div class="clear"></div>

					<label for="recurPW"><strong>Or:</strong></label>
					<select name="recurPW">
					<option value="0">-- SELECT --</option>
					<option value="1">First</option>
					<option value="2">Second</option>
					<option value="3">Third</option>
					<option value="4">Fourth</option>
					<option value="5">Last</option>
					</select>
					<select name="recurPWS">
					<option value="0">-- SELECT --</option>
					<option value="1">Sunday</option>
					<option value="2">Monday</option>
					<option value="3">Tuesday</option>
					<option value="4">Wednesday</option>
					<option value="5">Thursday</option>
					<option value="6">Friday</option>
					<option value="7">Saturday</option>
					</select>

					<div class="clear"></div>
					<label>&nbsp;</label><span>of the month</span>
					<div class="clear"></div>

					<label for="recurVal"><strong>Stop After:</strong></label>
					<input type="textbox" maxlength="3" name="stopAfterN" style="width: 50px;" />&nbsp;Occurrences&nbsp;

					<div class="clear"></div>
					<label for="stopAfterDate"><strong>or By:</strong></label>
					<input type="textbox" id="stopAfterDate" name="stopAfterDate" style="width: 210px;" />

					<button id="acp2" type="button">...</button>
					<div id="cal4Container"></div>
                </div>

            </div>
        </div>



		<!-- 
              <li class="yuimenuitem">
                <a class="yuimenuitemlabel" href="javascript:remove();">
                    Delete
                </a>
            </li>
            <li class="yuimenuitem">
                <a class="yuimenuitemlabel" href="javascript:appointmentReport();">
                    Appointment Report
                </a>
            </li>
            <li class="yuimenuitem">
                <a class="yuimenuitemlabel" href="javascript:checkoutSheet();">
                    Checkout Sheet
                </a>
            </li>
		-->
		

		<br />
		<a href="javascript: processCommand('checkin', document.newApptForm.appointmentSelect.value); YAHOO.example.container.dialog1.hide();">Check In</a>
		<!-- <a href="javascript: processCommand('checkin', document.newApptForm.appointmentSelect.value); YAHOO.example.container.dialog1.hide();">Check Out</button></a> -->
		<a href="javascript: processCommand('lastReceipt', document.newApptForm.appointmentSelect.value); YAHOO.example.container.dialog1.hide();">Last Receipt</a>
		<!-- <a href="javascript: processCommand('checkin', document.newApptForm.appointmentSelect.value); YAHOO.example.container.dialog1.hide();">Edit / Reschedule</button></a> -->
		<br />
		<a href="javascript: processCommand('cancel', document.newApptForm.appointmentSelect.value); YAHOO.example.container.dialog1.hide();">Cancel</a>
		<a href="javascript: processCommand('clear', document.newApptForm.appointmentSelect.value); YAHOO.example.container.dialog1.hide();">Clear Status</a>
		<a href="javascript: processCommand('cut', document.newApptForm.appointmentSelect.value); YAHOO.example.container.dialog1.hide();">Cut</a>
		<!-- <a href="javascript: processCommand('checkin', document.newApptForm.appointmentSelect.value); YAHOO.example.container.dialog1.hide();"><button>Paste</button></a> -->
		<a href="javascript: if (confirm('Delete?')) { processCommand('delete', document.newApptForm.appointmentSelect.value); } YAHOO.example.container.dialog1.hide();">Delete</a>
		<br />
		<a href="javascript: YAHOO.example.container.dialog1.hide(); appointmentReport();">Appointment Report</a>
		<a href="javascript: processCommand('checkOutSheet', document.newApptForm.appointmentSelect.value); YAHOO.example.container.dialog1.hide();">Checkout Sheet</a>










	 

	</form>
    </div>
</div>