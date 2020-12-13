<%
Calendar now = Calendar.getInstance();
SimpleDateFormat format = new SimpleDateFormat("EEEE MMMMM dd yyyy");

%>
			<h2>Today <em><%= format.format(now.getTime()) %></em></h2>