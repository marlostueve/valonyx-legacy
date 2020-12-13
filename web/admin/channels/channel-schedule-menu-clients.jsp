<%
boolean client_profile = (request.getRequestURI().indexOf("clients.jsp") > -1);
boolean client_status = (request.getRequestURI().indexOf("client-status.jsp") > -1);
boolean client_financial = (request.getRequestURI().indexOf("client-financial.jsp") > -1);
boolean client_ledger = (request.getRequestURI().indexOf("client-ledger.jsp") > -1);
boolean client_billing = (request.getRequestURI().indexOf("client-billing.jsp") > -1);
%>

					<div id="header">
					<ul>
						<li<%= client_profile ? " id=\"selected\"" : "" %>><a href="clients.jsp">Profile</a></li><!-- these comments between li's solve a problem in IE that prevents spaces appearing between list items that appear on different lines in the source
						--><li<%= client_status ? " id=\"selected\"" : "" %>><a href="#">Status</a></li><!--
						--><li<%= client_financial ? " id=\"selected\"" : "" %>><a href="client-financial.jsp">Financial</a></li><!--
						--><li<%= client_ledger ? " id=\"selected\"" : "" %>><a href="client-ledger.jsp">Ledger</a></li><!--
						--><li<%= client_billing ? " id=\"selected\"" : "" %>><a href="client-billing.jsp">Billing</a></li>
					</ul>
					</div>