<%@page import="com.badiyan.uk.online.beans.TreatmentRoomBean"%>
<div id="editAppt" class="black-box modal hide fade">
	<div class="modal-header tab-header">
		<button type="button" class="close" data-dismiss="modal">&times;</button>
		<span id="editApptTitle">Edit Appointment</span>
	</div>
	
	
	<form class="fill-up" id="newApptForm" name="newApptForm" method="POST" action="<%= CUBean.getProperty("cu.formSubmitPath") %>/newAppointment.x"  >
	
	<div class="modal-body separator" >

		<!--
	  <h4>Text in a modal</h4>
	  <p>Duis mollis, est non commodo luctus, nisi erat porttitor ligula, eget lacinia odio sem.</p>
		-->
		
<%
// 
%>

			<div class="row-fluid">
				<div class="span6">
					<div class="padded" style="padding-bottom: 0; padding-top: 0;">

						<label for="practitionerSelect"><strong>Practitioner:</strong></label>
						<div class="input">
							<select name="practitionerSelect" id="practitionerSelect" class="fill-up select">
								<option value="0">-- SELECT PRACTITIONER --</option>
<%
practitioner_itr = practitioners.iterator();
while (practitioner_itr.hasNext())
{
    com.badiyan.uk.online.beans.UKOnlinePersonBean practitioner = (com.badiyan.uk.online.beans.UKOnlinePersonBean)practitioner_itr.next();
%>
								<option value="<%= practitioner.getId() %>"><%= practitioner.getLabel() %></option>
<%
}
%>
							</select>
						</div>

						<label for="roomSelect"><strong>Treatment Room:</strong></label>
						<div class="input">
							<select name="roomSelect" id="roomSelect" class="fill-up select">
								<option value="0">-- SELECT ROOM --</option>
<%

java.util.Iterator room_itr = adminCompany.getTreatmentRooms().iterator();
while (room_itr.hasNext()) {
    com.badiyan.uk.online.beans.TreatmentRoomBean treatment_room = (com.badiyan.uk.online.beans.TreatmentRoomBean)room_itr.next();
%>
								<option value="<%= treatment_room.getId() %>"><%= treatment_room.getLabel() %></option>
<%
}
%>
							</select>
						</div>

						<label for="datetimepickerModal"><strong>Date & Time:</strong></label>
						<div class="input">
							<input name="appt_date" id="datetimepickerModal" type="text" class="fill-up" value="<%= date_format.format(display_date.getTime()) %>">
						</div>
						<div class="input">
							<select id="hr" name="hr" style="width: 67px;">
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
							<select id="mn" name="mn" style="width: 67px;">
								<option value="0">00</option>
								<option value="5">05</option>
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
							<select id="ampm" name="ampm" style="width: 67px;">
								<option value="AM">AM</option>
								<option value="PM">PM</option>
							</select>
						</div>
						
						<label for="comments"><strong>Comments:</strong></label>
						
						<div class="input">
							<textarea id="comments" name="comments" rows="3" ></textarea>
						</div>
						
					</div>
				</div>

				<div class="span6">

					<div class="padded" style="padding-bottom: 0; padding-top: 0;">
						
						<label for="typeSelect"><strong>Appointment Type:</strong></label>
						<div class="input">
							<select name="typeSelect" id="typeSelect" class="fill-up select" onchange="document.getElementById('duration').value = typeArray[this.value]['duration'];">
								<option value="0">-- SELECT A TYPE --</option>
<%
type_itr = com.badiyan.uk.online.beans.AppointmentTypeBean.getAppointmentTypes(adminCompany).iterator();
while (type_itr.hasNext())
{
    com.badiyan.uk.online.beans.AppointmentTypeBean appointmentType = (com.badiyan.uk.online.beans.AppointmentTypeBean)type_itr.next();
    String palabel = "";
    if (appointmentType.getPracticeAreaId() > 0)
	palabel = appointmentType.getPracticeArea().getLabel();
%>
								<option value="<%= appointmentType.getId() %>"><%= appointmentType.getLabel() %></option>
<%
}
%>
							</select>
						</div>
						
						
						<div class="input">
							<input type="text" id="duration" name="duration" placeholder="minutes" style="width: 70px;" />&nbsp;<a href="javascript:alert('not done yet...');" class="button mini blue">Find Next Available...</a>
						</div>
						
						<label for="lastname"><strong>Client Search:</strong></label>
						<div class="input">
							<input type="text" id="lastname" placeholder="Last / First / File Number"  onkeyup="waitOnKeyDown('lastname','clientSelect','getPeopleByKeyword');" />
						</div>
						<div class="input">
							
							<select size="6" id="clientSelect" name="clientSelect" >
							<option value="-1">-- SEARCH FOR A CLIENT --</option>
							</select>
						</div>


					</div>
				</div>
			</div>
			
    
			<input type="hidden" id="appointmentSelect" name="appointmentSelect" value="-1">
			<input type="hidden" name="statusSelect" value="-1">
			<input type="hidden" name="pSelect" value="0">



	</div>
							
	<!--
							
	<div class="modal-footer">
		<div class="inner-well">
			<a class="button mini rounded light-gray" data-dismiss="modal">Cancel</a>
			<a href="javascript:void(0);" onclick="javascript:document.forms['newApptForm'].submit(); return false;" class="button mini rounded blue" >Save changes</a>
		</div>
	</div>
 
	-->
	
			
							
	<div class="modal-footer">
		<div class="inner-well">
			<a class="btn " data-dismiss="modal"  >Cancel</a>
			<button class="btn btn-primary">Save changes</button>
		</div>
	</div>
			
			
	</form>
	
 
</div>