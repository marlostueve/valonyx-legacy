<div id="dialogNewTransaction">
<div class="hd"><h2>New Transaction</h2></div>
<div class="bd" id="bd-r">
<form name="transactionForm" method="POST" action="<%= CUBean.getProperty("cu.formSubmitPath") %>/newTransaction.x"  >

	<input type="hidden" name="appointmentSelect" value="-1">
	<input type="hidden" name="statusSelect" value="-1">

	<div class="clear"></div>

        <label for="transactionDate"><strong>Transaction Date:</strong></label>
        <input name="transactionDate" onfocus="getDate('transactionForm','transactionDate');select();" value="" size="7" maxlength="100" class="inputbox" style="width: 64px; margin-right: 4px;" />
	<div class="clear"></div>

	<label for="transactionTypeSelect"><strong>Transaction Type:</strong></label>
        <select name="transactionTypeSelect" size="4" id="transactionTypeSelect" class="adminInput" style="width: 166px;" onchange="selectType();">
            <option value="<%= CheckoutCodeBean.PROCEDURE_TYPE%>" selected>Procedures</option>
            <option value="<%= CheckoutCodeBean.GROUP_TYPE%>">Group</option>
            <option value="<%= CheckoutCodeBean.INVENTORY_TYPE%>">Inventory</option>
            <option value="<%= CheckoutCodeBean.PAYMENT_TYPE%>">Payment</option>
        </select>
	<div class="clear"></div>

	<label for="paymentCodeSelect"><strong>Transaction Code:</strong></label>
	<select multiple name="paymentCodeSelect">
	    <option value="-1">-- SELECT A PAYMENT CODE --</option>
<%
codes_itr = codes.iterator();
while (codes_itr.hasNext())
{
    CheckoutCodeBean code = (CheckoutCodeBean)codes_itr.next();
%>
	    <option value="<%= code.getValue() %>"><%= code.getLabel() %></option>
<%
}
%>
	</select>
	<div class="clear"></div>

	<label for="amountInput"><strong>Amount:</strong></label><input id="amount" type="textbox" name="amountInput" />

	<div class="clear"></div>

</form>
</div>
</div>