<%@page import="com.badiyan.uk.online.beans.AppointmentBean"%>
<script type="text/javascript">

    var typeArray = new Array();
    var practitionerArray = new Array();
    var codeArray = new Array();
	
	var date_str = '';
    
<%
java.util.Iterator type_itr = com.badiyan.uk.online.beans.AppointmentTypeBean.getAppointmentTypes(adminCompany).iterator();
while (type_itr.hasNext())
{
    com.badiyan.uk.online.beans.AppointmentTypeBean appointmentType = (com.badiyan.uk.online.beans.AppointmentTypeBean)type_itr.next();
    String palabel = "";
    if (appointmentType.getPracticeAreaId() > 0)
	palabel = appointmentType.getPracticeArea().getLabel();
%>
    typeArray["<%= appointmentType.getId() %>"] = {"type" : "<%= appointmentType.getType() %>", "palabel" : "<%= palabel %>", "label" : "<%= appointmentType.getLabel() %>", "bg" : "<%= appointmentType.getBGColorCodeString() %>", "txt" : "<%= appointmentType.getTextColorCodeString() %>", "duration" : "<%= appointmentType.getDurationString() %>" };
<%
}
java.util.Iterator practitioner_itr = practitioners.iterator();
while (practitioner_itr.hasNext())
{
    com.badiyan.uk.online.beans.UKOnlinePersonBean practitioner = (com.badiyan.uk.online.beans.UKOnlinePersonBean)practitioner_itr.next();
%>
    practitionerArray["<%= practitioner.getId() %>"] = {"label" : "<%= practitioner.getLabel() %>", "pa_id" : "<%= practitioner.getPracticeAreasIdString() %>"};
<%
}
%>

	function showNewAppointment() {
		
		var now = new Date();
		
		document.getElementById('appointmentSelect').value = -1;
		document.getElementById('datetimepickerModal').value = (now.getMonth() + 1) + '/' + now.getDate() + '/' + now.getFullYear();

		var hrInDay = now.getHours();
		if (hrInDay > 11) {
			document.getElementById("ampm").value = 'PM';
			if (hrInDay > 12) {
				hrInDay -= 12;
			}
		} else {
			document.getElementById("ampm").value = 'AM';
		}
		document.getElementById("hr").value = hrInDay;
		document.getElementById("mn").value = now.getMinutes();

		document.getElementById('practitionerSelect').value = '0';
		document.getElementById('roomSelect').value = '0';

		document.getElementById('clientSelect').options.length = 0;
		document.getElementById('clientSelect').options[0] = new Option('-- SEARCH FOR A CLIENT --', '-1');

		document.getElementById('typeSelect').value = '0';
		document.getElementById('duration').value = '';
		document.getElementById('lastname').value = '';
		document.getElementById('comments').value = '';
		document.getElementById('editApptTitle').firstChild.nodeValue = 'New Appointment';
		$(function () { $("#editAppt").modal('show'); });
	}

	function showNewClient() {
		$(function () { $("#newClient").modal('show'); });
	}

	function showClientInfo() {
		$(function () { $("#clientInfo").modal('show'); });
	}

	function showCheckout() {
		
		/*
		$('#exampleDT').dataTable( {
			"sScrollY": "100px",
			
			
			"bPaginate": false,
			"bLengthChange": false,
			"bFilter": false,
			"bSort": false,
			"bInfo": false,
			"bAutoWidth": false
		} );
		*/
		
		/*
		$('#exampleDxY').dataTable( {
			"bPaginate": true,
			"bLengthChange": false,
			"bFilter": false,
			"bSort": false,
			"bInfo": false,
			"bAutoWidth": true
		} );
		*/
		
		/*
		$('#openAndRecentOrders').dataTable( {
			"bPaginate": false,
			"bLengthChange": false,
			"bFilter": false,
			"bSort": false,
			"bInfo": false,
			"bAutoWidth": false
		} );
		*/
		
		
		$(function () { $("#checkoutDialog").modal('show'); });
		
		/*
		var elemee = document.getElementById('coke');
		alert(elemee);
		elemee.setAttribute('style', 'line-height: 10px; height: 10px;');
	
		alert(elemee);
		
		deleteAllRows(elemee);
		*/
	   
	   
	   /*
	   var elemee = document.getElementById('weank');
	   
	   var tr_test = document.createElement('tr');
	   var td_test = document.createElement('td');
	   td_test.appendChild(document.createTextNode(' Scheduled'));
	   tr_test.appendChild(td_test);
	   elemee.appendChild(tr_test);
	   
	   removeAllChildren(elemee);
	   */
	}

	function drawCalendar(view, y, m, d) {
		
		/*
		var date = new Date();
		var d = date.getDate();
		var m = date.getMonth();
		var y = date.getFullYear();
		*/

		var calendarX = $('#calendarX').fullCalendar({
			aspectRatio: 1.1,
			height: 1100,
			header: {
				left: 'prev,next today resourceDay,agendaWeek,month',
				center: '',
				right: 'title'
			},
			titleFormat: {
				resourceDay: 'ddd, MMM d, yyyy',
				agendaWeek: "MMM d[ yyyy]{ '&#8212;'[ MMM] d yyyy}",
				month: 'MMMM yyyy' 
			},
			defaultView: view,
			minTime: <%= start_hour_of_day %>,
			maxTime: <%= end_minute > 0 ? (end_hour_of_day + 1) : end_hour_of_day %>,
			year: y,
			month: m,
			date: d,
			slotMinutes: 15,
			selectable: true,
			selectHelper: true,
			viewDisplay: function(view) {
				
				//alert(view.name);
				
				if (view.name != selected_view) {
					//alert(view.name);
					selected_view = view.name;
					
					// view is being switched.  I need to grab events from the server appropriate for the selected view
					
					// javascript: doAjax('pickDay', this.value);
					
					doAjax('pickDay', d_str, 'calendar control view change');
				}
				
				var d = $('#calendarX').fullCalendar('getDate');
				//alert("The current date of the calendar is " + d);
				//alert('year >' + d.getFullYear() + ', month >' + (d.getMonth() + 1) + ', date >' + d.getDate());
				var d_str = (d.getMonth() + 1) + '/' + d.getDate() + '/' + d.getFullYear();
				//alert(view.title + '   --- >date_str >' + date_str + ' vs. d_str >' + d_str);
				if (d_str != date_str) {
					
					//alert('pickDay >' + d_str);
					
					doAjax('pickDay', d_str, 'calendar control PickDay');
					
					date_str = d_str;
				}
				
			},
			select: function(start, end, allDay, event, resourceId) {
				//doAjax('newAppointment', start, allDay, resourceId);
				//alert('start >' + start.toUTCString());
				
				document.getElementById('appointmentSelect').value = -1;
				document.getElementById('datetimepickerModal').value = (start.getMonth() + 1) + '/' + start.getDate() + '/' + start.getFullYear();
				
				var hrInDay = start.getHours();
				if (hrInDay > 11) {
					document.getElementById("ampm").value = 'PM';
					if (hrInDay > 12) {
						hrInDay -= 12;
					}
				} else {
					document.getElementById("ampm").value = 'AM';
				}
				document.getElementById("hr").value = hrInDay;
				document.getElementById("mn").value = start.getMinutes();
				
				if (resourceId) {
					document.getElementById('practitionerSelect').value = resourceId;
				} else {
					document.getElementById('practitionerSelect').value = '0';
				}
				
				document.getElementById('roomSelect').value = '0';
				
				document.getElementById('clientSelect').options.length = 0;
				document.getElementById('clientSelect').options[0] = new Option('-- SEARCH FOR A CLIENT --', '-1');
			
				document.getElementById('typeSelect').value = '0';
				document.getElementById('duration').value = '';
				document.getElementById('lastname').value = '';
				document.getElementById('comments').value = '';
				document.getElementById('editApptTitle').firstChild.nodeValue = 'New Appointment';
				$(function () { $("#editAppt").modal('show'); });
				
				/*
				var title = prompt('Event Title:');
				if (title) {
					//console.log("@@ adding event " + title + ", start " + start + ", end " + end + ", allDay " + allDay + ", resource " + resourceId);
					
					calendarX.fullCalendar('renderEvent',
					{
						title: title,
						start: start,
						end: end,
						allDay: allDay,
						resourceId: resourceId
					},
					true // make the event "stick"
					);
				}
				calendarX.fullCalendar('unselect');
				*/
			},
			eventResize: function(event, dayDelta, minuteDelta) {
				//console.log("@@ resize event " + event.title + ", start " + event.start + ", end " + event.end + ", resource " + event.resourceId);
				alert("@@ resize event " + event.title + ", start " + event.start + ", end " + event.end + ", resource " + event.resourceId);
			},
			eventDrop: function(event, dayDelta, minuteDelta, allDay) {
				//console.log("@@ drag/drop event " + event.title + ", start " + event.start + ", end " + event.end + ", resource " + event.resourceId);
				alert("@@ drag/drop event " + event.title + ", start " + event.start + ", end " + event.end + ", resource " + event.resourceId + ", allDay " + allDay);
			},
			eventRender: function(event, element, calEvent) {
				if (event.cid == '0') {
					//element.find(".fc-event-title").replaceWith($("<span class=\"fc-event-title\"></span>").html("<img src=\"../../images/checked-out.gif\" />" + event.title));
				} else {
					if (event.state == <%= AppointmentBean.DEFAULT_APPOINTMENT_STATUS %>) {
						//element.find(".fc-event-title").replaceWith($("<span class=\"fc-event-title\"></span>").html("<img src=\"../../images/checked-out.gif\" />&nbsp;" + event.title));
					} else if (event.state == <%= AppointmentBean.CHECKED_OUT_APPOINTMENT_STATUS %>) {
						element.find(".fc-event-title").replaceWith($("<span class=\"fc-event-title\"></span>").html("<img src=\"../../images/checked-out.gif\" />&nbsp;" + event.title));
					} else if (event.state == <%= AppointmentBean.LATE_APPOINTMENT_STATUS %>) {
						element.find(".fc-event-title").replaceWith($("<span class=\"fc-event-title\"></span>").html("<img src=\"../../images/late.gif\" />&nbsp;" + event.title));
					} else if (event.state == <%= AppointmentBean.CHECKED_IN_APPOINTMENT_STATUS %>) {
						element.find(".fc-event-title").replaceWith($("<span class=\"fc-event-title\"></span>").html("<img src=\"../../images/checked-in.gif\" />&nbsp;" + event.title));
					} else if (event.state == <%= AppointmentBean.NO_SHOW_APPOINTMENT_STATUS %>) {
						element.find(".fc-event-title").replaceWith($("<span class=\"fc-event-title\"></span>").html("<img src=\"../../images/no-show.gif\" />&nbsp;" + event.title));
					}
				}
			},
			eventClick: function(calEvent, jsEvent, view) {
				//alert(calEvent.id);
				document.getElementById('editApptTitle').firstChild.nodeValue = 'Edit Appointment';
				
				// load up form field values
				
				document.getElementById('appointmentSelect').value = calEvent.id;
				
				document.getElementById('datetimepickerModal').value = (calEvent.start.getMonth() + 1) + '/' + calEvent.start.getDate() + '/' + calEvent.start.getFullYear();
				
				var hrInDay = calEvent.start.getHours();
				if (hrInDay > 11) {
					document.getElementById("ampm").value = 'PM';
					if (hrInDay > 12) {
						hrInDay -= 12;
					}
				} else {
					document.getElementById("ampm").value = 'AM';
				}
				document.getElementById("hr").value = hrInDay;
				document.getElementById("mn").value = calEvent.start.getMinutes();
				
			
				document.getElementById('typeSelect').value = calEvent.type;
				document.getElementById('duration').value = calEvent.duration;
				
				// empty clientSelect list and populate with client
				
				document.getElementById('clientSelect').options.length = 0;
				document.getElementById('clientSelect').options[0] = new Option(calEvent.title, calEvent.cid);
				document.getElementById('clientSelect').selectedIndex = 0;
				
				if (calEvent.resourceId) {
					document.getElementById('practitionerSelect').value = calEvent.resourceId;
				} else {
					document.getElementById('practitionerSelect').value = '0';
				}
				document.getElementById('roomSelect').value = '0';
				document.getElementById('comments').value = calEvent.description;
				
				$(function () { $("#editAppt").modal('show'); });
			},
			editable: true,
			resources: [
<%
practitioner_itr = practitioners.iterator();
while (practitioner_itr.hasNext())
{
    com.badiyan.uk.online.beans.UKOnlinePersonBean practitioner = (com.badiyan.uk.online.beans.UKOnlinePersonBean)practitioner_itr.next();
%>
				{
					name: '<%= practitioner.getLabel() %>',
					id:	'<%= practitioner.getId() %>'
				}<%= practitioner_itr.hasNext() ? "," : "" %>
<%
}
%>				,{
					name: 'Room 1',
					id:	'1'
				},

				{
					name: 'Room 2',
					id:	'2'
				},

				{
					name: 'Room 3',
					id:	'3'
				},

				{
					name: 'Room 4',
					id:	'4'
				}
			]
		});
		
	}
	
	
	
</script>