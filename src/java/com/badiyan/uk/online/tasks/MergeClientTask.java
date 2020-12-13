/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.badiyan.uk.online.tasks;


import com.badiyan.torque.*;
import com.badiyan.uk.beans.RoleBean;
import com.badiyan.uk.exceptions.IllegalValueException;
import com.badiyan.uk.online.beans.*;

import java.util.*;


import javax.servlet.http.HttpSession;
import org.apache.torque.util.Criteria;



public class
MergeClientTask
    extends TimerTask
{
    // INSTANCE VARIABLES

	private HttpSession session;
	//private StringBuffer status;
	
	private UKOnlinePersonBean source;
	private UKOnlinePersonBean destination;

    // CONSTRUCTORS

    public
    MergeClientTask(UKOnlinePersonBean _source, UKOnlinePersonBean _destination)
    {
		//status = (StringBuffer)session.getAttribute("update_quickbooks_status");
		
		source = _source;
		destination = _destination;
    }

    public
    MergeClientTask(HttpSession _session, UKOnlinePersonBean _source, UKOnlinePersonBean _destination)
    {
		session = _session;
		//status = (StringBuffer)session.getAttribute("update_quickbooks_status");
		
		source = _source;
		destination = _destination;
    }

    // INSTANCE METHODS

	@Override
    public void
    run()
    {
		System.out.println("run() invoked in MergeClientTask ");
		
		// the goal is to move anything attached to the source person and attach it to the destination person.  things already attached to the destination should be maintained.  this includes:
		// - SalesReceipts
		// - Invoices
		// - Address / Phone
		// 

		try
		{
			// find the QB_LIST_ID of the destination
			
			String source_QB_List_ID = source.getEmail2String();
			if (source_QB_List_ID.equals(""))
				source_QB_List_ID = null;
			String source_PoS_List_ID = source.getEmployeeNumberString();
			String destination_QB_List_ID = destination.getQBFSListID();
			
			if (destination_QB_List_ID == null || destination_QB_List_ID.equals(""))
				throw new IllegalValueException("invalid QB List ID for destination >" + destination_QB_List_ID);
			
			if (source.getId() == destination.getId())
				throw new IllegalValueException("Source client is identical to destination client.");
			
			/*
			if (destination_QB_List_ID == null && source_QB_List_ID != null)
			{
				System.out.println("Merging QB List ID");
				destination.setQBFSListID(source_QB_List_ID);
				
			}
			else if (source_QB_List_ID != null)
			{
				System.out.println("what should I do with the source CustomerRet object???");
			}
			 */
			
			
			/*
<table name="PRACTITIONER_INVENTORY_DEPARTMENT_COMMISSION_SETTINGS_DB">
    <column name="INVENTORY_DEPARTMENT_DB_ID" primaryKey="true" required="true" type="INTEGER"/>
    <column name="PRACTITIONER_ID" primaryKey="true" required="true" type="INTEGER"/>
    <column name="COMMISSION" required="true" scale="2" size="7" type="DECIMAL"/>

    <foreign-key foreignTable="INVENTORY_DEPARTMENT_DB">
		<reference local="INVENTORY_DEPARTMENT_DB_ID" foreign="INVENTORY_DEPARTMENT_DB_ID"/>
    </foreign-key>
    <foreign-key foreignTable="PERSON">
		<reference local="PRACTITIONER_ID" foreign="PERSONID"/>
    </foreign-key>
</table>
			*/
			
			int num_merged = 0;
			System.out.println("Merging >PRACTITIONER_INVENTORY_DEPARTMENT_COMMISSION_SETTINGS_DB");
			
			// merge this only if destination has none

			Criteria crit = new Criteria();
			crit.add(PractitionerInventoryDepartmentCommissionSettingsDbPeer.PRACTITIONER_ID, destination.getId());
			List obj_list = PractitionerInventoryDepartmentCommissionSettingsDbPeer.doSelect(crit);
			if (obj_list.size() == 0)
			{
				crit = new Criteria();
				crit.add(PractitionerInventoryDepartmentCommissionSettingsDbPeer.PRACTITIONER_ID, source.getId());
				Iterator obj_itr = PractitionerInventoryDepartmentCommissionSettingsDbPeer.doSelect(crit).iterator();
				while (obj_itr.hasNext())
				{
					PractitionerInventoryDepartmentCommissionSettingsDb existing_obj = (PractitionerInventoryDepartmentCommissionSettingsDb)obj_itr.next();
					
					// the person is part of the primary key.  I'll need to re-create then delete
					
					PractitionerInventoryDepartmentCommissionSettingsDb new_obj = new PractitionerInventoryDepartmentCommissionSettingsDb();
					new_obj.setCommission(existing_obj.getCommission());
					new_obj.setInventoryDepartmentDbId(existing_obj.getInventoryDepartmentDbId());
					new_obj.setPractitionerId(destination.getId());
					new_obj.save();
					
					PractitionerInventoryDepartmentCommissionSettingsDbPeer.doDelete(existing_obj);
					
					num_merged++;
				}
					
			}
			else
				System.out.println("Can't merge already exists...");
			
			System.out.println("NUM MERGED >" + num_merged);
			
			
			/*
<table name="CLIENT_REVIEW_REASON_DB" idMethod="native">
    <column name="CLIENT_REVIEW_REASON_DB_ID" primaryKey="true" required="true" type="INTEGER" autoIncrement="true"/>
    <column name="PERSON_ID" required="true" type="INTEGER"/>
    <column name="REVIEW_REASON_DB_ID" required="true" type="INTEGER"/>
    <column name="PRACTITIONER_ID" required="false" type="INTEGER"/>
    <column name="IS_REVIEWED" required="true" type="SMALLINT"/>
    <column name="REVIEW_DATE" required="true" type="DATE"/>
    <column name="NOTE" required="false" type="VARCHAR" size="250"/>

    <foreign-key foreignTable="PERSON">
	<reference local="PERSON_ID" foreign="PERSONID"/>
    </foreign-key>
    <foreign-key foreignTable="REVIEW_REASON_DB">
	<reference local="REVIEW_REASON_DB_ID" foreign="REVIEW_REASON_DB_ID"/>
    </foreign-key>
    <foreign-key foreignTable="PERSON">
	<reference local="PRACTITIONER_ID" foreign="PERSONID"/>
    </foreign-key>
</table>
			*/

			
			num_merged = 0;
			System.out.println("Merging >CLIENT_REVIEW_REASON_DB");
			
			crit = new Criteria();
			crit.add(ClientReviewReasonDbPeer.PERSON_ID, source.getId());
			Iterator obj_itr = ClientReviewReasonDbPeer.doSelect(crit).iterator();
			while (obj_itr.hasNext())
			{
				ClientReviewReasonDb existing_obj = (ClientReviewReasonDb)obj_itr.next();
				existing_obj.setPersonId(destination.getId());
				existing_obj.save();
					
				num_merged++;
			}
			
			crit = new Criteria();
			crit.add(ClientReviewReasonDbPeer.PRACTITIONER_ID, source.getId());
			obj_itr = ClientReviewReasonDbPeer.doSelect(crit).iterator();
			while (obj_itr.hasNext())
			{
				ClientReviewReasonDb existing_obj = (ClientReviewReasonDb)obj_itr.next();
				existing_obj.setPractitionerId(destination.getId());
				existing_obj.save();
					
				num_merged++;
			}
			
			System.out.println("NUM MERGED >" + num_merged);
			
			
			/*
<table name="PERSON_NOTE_DB" idMethod="native">
    <column name="PERSON_NOTE_DB_ID" primaryKey="true" required="true" type="INTEGER" autoIncrement="true"/>
    <column name="PERSON_ID" required="true" type="INTEGER"/>
    <column name="NOTE_SUBJECT" required="true" type="VARCHAR" size="100"/>
    <column name="NOTE" type="LONGVARCHAR"/>
    <column name="ISALERT" required="true" type="SMALLINT"/>
    <column name="NOTE_DATE" required="true" type="DATE"/>
    <column name="EMAIL_DATE" required="false" type="DATE"/>

    <foreign-key foreignTable="PERSON">
	<reference local="PERSON_ID" foreign="PERSONID"/>
    </foreign-key>
</table>
			*/

			
			num_merged = 0;
			System.out.println("Merging >PERSON_NOTE_DB");
			
			crit = new Criteria();
			crit.add(PersonNoteDbPeer.PERSON_ID, source.getId());
			obj_itr = PersonNoteDbPeer.doSelect(crit).iterator();
			while (obj_itr.hasNext())
			{
				PersonNoteDb existing_obj = (PersonNoteDb)obj_itr.next();
				existing_obj.setPersonId(destination.getId());
				existing_obj.save();
					
				num_merged++;
			}
			
			System.out.println("NUM MERGED >" + num_merged);
			
			
			/*
<table name="REFERRAL_SOURCE_DB" idMethod="native">
    <column name="REFERRAL_SOURCE_DB_ID" primaryKey="true" required="true" type="INTEGER" autoIncrement="true"/>
    <column name="PERSON_ID" required="true" type="INTEGER"/>
    <column name="REFERRED_BY_ID" required="false" type="INTEGER"/>
    <column name="MARKETING_PLAN_DB_ID" required="false" type="INTEGER"/>
    <column name="REFERRAL_DATE" required="true" type="DATE"/>

    <foreign-key foreignTable="PERSON">
	<reference local="PERSON_ID" foreign="PERSONID"/>
    </foreign-key>
    <foreign-key foreignTable="PERSON">
	<reference local="REFERRED_BY_ID" foreign="PERSONID"/>
    </foreign-key>
    <foreign-key foreignTable="MARKETING_PLAN_DB">
	<reference local="MARKETING_PLAN_DB_ID" foreign="MARKETING_PLAN_DB_ID"/>
    </foreign-key>
</table>
			*/

			
			num_merged = 0;
			
			crit = new Criteria();
			crit.add(ReferralSourceDbPeer.PERSON_ID, destination.getId());
			obj_list = ReferralSourceDbPeer.doSelect(crit);
			if (obj_list.size() == 0)
			{
				System.out.println("Merging >REFERRAL_SOURCE_DB");

				crit = new Criteria();
				crit.add(ReferralSourceDbPeer.PERSON_ID, source.getId());
				obj_itr = ReferralSourceDbPeer.doSelect(crit).iterator();
				while (obj_itr.hasNext())
				{
					ReferralSourceDb existing_obj = (ReferralSourceDb)obj_itr.next();
					existing_obj.setPersonId(destination.getId());
					existing_obj.save();

					num_merged++;
				}
			
			}
			
			crit = new Criteria();
			crit.add(ReferralSourceDbPeer.REFERRED_BY_ID, source.getId());
			obj_itr = ReferralSourceDbPeer.doSelect(crit).iterator();
			while (obj_itr.hasNext())
			{
				ReferralSourceDb existing_obj = (ReferralSourceDb)obj_itr.next();
				existing_obj.setReferredById(destination.getId());
				existing_obj.save();
					
				num_merged++;
			}
			
			System.out.println("NUM MERGED >" + num_merged);
			
			
			/*
<table name="RECEIVE_PAYMENT_RET_DB" idMethod="native">
    <column name="RECEIVE_PAYMENT_RET_DB_ID" primaryKey="true" required="true" type="INTEGER" autoIncrement="true"/>
    <column name="TXN_I_D" required="true" type="VARCHAR" size="20"/>
    <column name="COMPANY_ID" required="true" type="INTEGER"/>
    <column name="REQUEST_I_D" required="true" type="VARCHAR" size="40"/>

    <column name="TIME_CREATED" required="true" type="DATE"/>
    <column name="TIME_MODIFIED" required="true" type="DATE"/>
    <column name="TXN_NUMBER" required="false" type="INTEGER"/>
    <column name="CUSTOMER_LIST_I_D" required="true" type="VARCHAR" size="50"/>
    <column name="TXN_DATE" required="true" type="DATE"/>
    <column name="TOTAL_AMOUNT" required="true" scale="2" size="7" type="DECIMAL"/>
    <column name="MEMO" type="VARCHAR" size="250"/>

    <foreign-key foreignTable="COMPANY">
	<reference local="COMPANY_ID" foreign="COMPANYID"/>
    </foreign-key>
    <foreign-key foreignTable="PERSON">
	<reference local="CUSTOMER_LIST_I_D" foreign="EMAIL2"/>
    </foreign-key>
</table>
			*/


			if (source_QB_List_ID != null)
			{
				num_merged = 0;
				System.out.println("Merging >RECEIVE_PAYMENT_RET_DB");

				crit = new Criteria();
				crit.add(ReceivePaymentRetDbPeer.CUSTOMER_LIST_I_D, source_QB_List_ID);
				obj_itr = ReceivePaymentRetDbPeer.doSelect(crit).iterator();
				while (obj_itr.hasNext())
				{
					ReceivePaymentRetDb existing_obj = (ReceivePaymentRetDb)obj_itr.next();
					existing_obj.setCustomerListID(destination_QB_List_ID);
					existing_obj.save();

					num_merged++;
				}

				System.out.println("NUM MERGED >" + num_merged);
			}
			
			
			/*
<table name="INVOICE_RET_DB" idMethod="native">
    <column name="INVOICE_RET_DB_ID" primaryKey="true" required="true" type="INTEGER" autoIncrement="true"/>
    <column name="TXN_I_D" required="true" type="VARCHAR" size="20"/>
    <column name="COMPANY_ID" required="true" type="INTEGER"/>
    <column name="CASHIER_ID" required="true" type="INTEGER"/>
    <column name="PRACTITIONER_ID" required="false" type="INTEGER"/>
    <column name="REQUEST_I_D" required="true" type="VARCHAR" size="40"/>

    <column name="INVOICE_NUMBER" required="false" type="INTEGER"/>

    <column name="TIME_MODIFIED" required="true" type="DATE"/>
    <column name="CUSTOMER_LIST_I_D" required="true" type="VARCHAR" size="50"/>
    <column name="SUBTOTAL" required="true" scale="2" size="7" type="DECIMAL"/>
    <column name="SALES_TAX_TOTAL" required="true" scale="2" size="7" type="DECIMAL"/>
	<column name="BALANCE_REMAINING" required="true" scale="2" size="7" type="DECIMAL"/>
    <column name="TXN_DATE" required="true" type="DATE"/>

    <column name="TIME_CREATED" required="false" type="DATE"/>
	<column name="EDIT_SEQUENCE" required="false" type="VARCHAR" size="15"/>
    <column name="TXN_NUMBER" required="false" type="INTEGER"/>
    <column name="REF_NUMBER" required="false" type="INTEGER"/>
    <column name="IS_PENDING" required="false" type="VARCHAR" size="10"/>
    <column name="IS_FINANCE_CHARGE" required="false" type="VARCHAR" size="10"/>
    <column name="IS_PAID" required="false" type="VARCHAR" size="10"/>
    <column name="CHECK_NUMBER" required="false" type="INTEGER"/>

	<column name="DUE_DATE" required="false" type="DATE"/>
	<column name="P_O_NUMBER" required="false" type="VARCHAR" size="20"/>
	<column name="MEMO" required="false" type="VARCHAR" size="200"/>

    <foreign-key foreignTable="COMPANY">
		<reference local="COMPANY_ID" foreign="COMPANYID"/>
    </foreign-key>
    <foreign-key foreignTable="PERSON">
		<reference local="CASHIER_ID" foreign="PERSONID"/>
    </foreign-key>
    <foreign-key foreignTable="PERSON">
		<reference local="PRACTITIONER_ID" foreign="PERSONID"/>
    </foreign-key>
    <foreign-key foreignTable="PERSON">
		<reference local="CUSTOMER_LIST_I_D" foreign="EMAIL2"/>
    </foreign-key>
</table>
			*/


			if (source_QB_List_ID != null)
			{
				num_merged = 0;
				System.out.println("Merging >INVOICE_RET_DB");

				crit = new Criteria();
				crit.add(InvoiceRetDbPeer.CUSTOMER_LIST_I_D, source_QB_List_ID);
				obj_itr = InvoiceRetDbPeer.doSelect(crit).iterator();
				while (obj_itr.hasNext())
				{
					InvoiceRetDb existing_obj = (InvoiceRetDb)obj_itr.next();
					existing_obj.setCustomerListID(destination_QB_List_ID);
					existing_obj.save();

					num_merged++;
				}

				System.out.println("NUM MERGED >" + num_merged);
			}

			
			/*
<table name="SALES_RECEIPT_RET_DB" idMethod="native">
    <column name="SALES_RECEIPT_RET_DB_ID" primaryKey="true" required="true" type="INTEGER" autoIncrement="true"/>
    <column name="TXN_I_D" required="true" type="VARCHAR" size="20"/>
    <column name="COMPANY_ID" required="true" type="INTEGER"/>
    <column name="CASHIER_ID" required="true" type="INTEGER"/>
    <column name="PRACTITIONER_ID" required="false" type="INTEGER"/>
    <column name="REQUEST_I_D" required="true" type="VARCHAR" size="40"/>
	
    <column name="TIME_MODIFIED" required="true" type="DATE"/>
    <column name="CUSTOMER_LIST_I_D" required="true" type="VARCHAR" size="50"/>
    <column name="SUBTOTAL" required="true" scale="2" size="7" type="DECIMAL"/>
    <column name="TAX_AMOUNT" required="true" scale="2" size="7" type="DECIMAL"/>
    <column name="TOTAL" required="true" scale="2" size="7" type="DECIMAL"/>
    <column name="TXN_DATE" required="true" type="DATE"/>

    <column name="TENDER_TYPE" size="30" type="VARCHAR"/>

    <column name="SALES_RECEIPT_NUMBER" required="false" type="INTEGER"/>
    <column name="SALES_RECEIPT_TYPE" required="false" type="VARCHAR" size="10"/>
    <column name="HISTORY_DOC_STATUS" required="false" type="VARCHAR" size="10"/>


	<!-- the following are specific to QBFS -->

    <column name="TIME_CREATED" required="false" type="DATE"/>
	<column name="EDIT_SEQUENCE" required="false" type="VARCHAR" size="15"/>
    <column name="TXN_NUMBER" required="false" type="INTEGER"/>
    <column name="REF_NUMBER" required="false" type="INTEGER"/>
    <column name="IS_PENDING" required="false" type="VARCHAR" size="10"/>
    <column name="CHECK_NUMBER" required="false" type="INTEGER"/>

    
    <foreign-key foreignTable="COMPANY">
		<reference local="COMPANY_ID" foreign="COMPANYID"/>
    </foreign-key>
    <foreign-key foreignTable="PERSON">
		<reference local="CASHIER_ID" foreign="PERSONID"/>
    </foreign-key>
    <foreign-key foreignTable="PERSON">
		<reference local="PRACTITIONER_ID" foreign="PERSONID"/>
    </foreign-key>
    <foreign-key foreignTable="PERSON">
		<reference local="CUSTOMER_LIST_I_D" foreign="EMPLOYEEID"/>
    </foreign-key>
</table>
			*/


			if (source_QB_List_ID != null)
			{
				num_merged = 0;
				System.out.println("Merging >SALES_RECEIPT_RET_DB");

				crit = new Criteria();
				crit.add(SalesReceiptRetDbPeer.CUSTOMER_LIST_I_D, source_QB_List_ID);
				obj_itr = SalesReceiptRetDbPeer.doSelect(crit).iterator();
				while (obj_itr.hasNext())
				{
					SalesReceiptRetDb existing_obj = (SalesReceiptRetDb)obj_itr.next();
					existing_obj.setCustomerListID(destination_QB_List_ID);
					existing_obj.save();

					num_merged++;
				}

				System.out.println("NUM MERGED >" + num_merged);
			}

			if (source_PoS_List_ID != null && !source_PoS_List_ID.equals(""))
			{
				num_merged = 0;
				System.out.println("Merging >SALES_RECEIPT_RET_DB");

				crit = new Criteria();
				crit.add(SalesReceiptRetDbPeer.CUSTOMER_LIST_I_D, source_PoS_List_ID);
				obj_itr = SalesReceiptRetDbPeer.doSelect(crit).iterator();
				while (obj_itr.hasNext())
				{
					SalesReceiptRetDb existing_obj = (SalesReceiptRetDb)obj_itr.next();
					existing_obj.setCustomerListID(destination_QB_List_ID);
					existing_obj.save();

					num_merged++;
				}

				System.out.println("NUM MERGED >" + num_merged);
			}

			
			/*
<table name="SALES_RECEIPT_ITEM_RET_DB" idMethod="native">
    <column name="SALES_RECEIPT_ITEM_RET_DB_ID" primaryKey="true" required="true" type="INTEGER" autoIncrement="true"/>
    <column name="SALES_RECEIPT_RET_DB_ID" required="true" type="INTEGER"/>
    <column name="PRACTITIONER_ID" required="false" type="INTEGER"/>

    <column name="LIST_I_D" required="true" type="VARCHAR" size="20"/>
    <column name="ITEM_NUMBER" required="true" type="INTEGER"/>
    <column name="PRICE" required="true" scale="2" size="7" type="DECIMAL"/>
    <column name="QTY" required="true" scale="2" size="7" type="DECIMAL"/>
    <column name="TAX_AMOUNT" required="true" scale="2" size="7" type="DECIMAL"/>
    <column name="COMMISSION" required="false" scale="2" size="7" type="DECIMAL"/>
    <column name="DESC1" required="true" type="VARCHAR" size="200"/>

    <foreign-key foreignTable="SALES_RECEIPT_RET_DB">
		<reference local="SALES_RECEIPT_RET_DB_ID" foreign="SALES_RECEIPT_RET_DB_ID"/>
    </foreign-key>
    <foreign-key foreignTable="PERSON">
		<reference local="PRACTITIONER_ID" foreign="PERSONID"/>
    </foreign-key>
</table>
			*/


			num_merged = 0;
			System.out.println("Merging >SALES_RECEIPT_ITEM_RET_DB");
			
			crit = new Criteria();
			crit.add(SalesReceiptItemRetDbPeer.PRACTITIONER_ID, source.getId());
			obj_itr = SalesReceiptItemRetDbPeer.doSelect(crit).iterator();
			while (obj_itr.hasNext())
			{
				SalesReceiptItemRetDb existing_obj = (SalesReceiptItemRetDb)obj_itr.next();
				existing_obj.setPractitionerId(destination.getId());
				existing_obj.save();
					
				num_merged++;
			}
			
			System.out.println("NUM MERGED >" + num_merged);

			
			/*
<table name="CHARGE_RESPONSE_DB" idMethod="native">
    <column name="CHARGE_RESPONSE_DB_ID" primaryKey="true" required="true" type="INTEGER" autoIncrement="true"/>
    <column name="COMPANY_ID" required="true" type="INTEGER"/>
    <column name="CLIENT_ID" required="true" type="INTEGER"/>
    <column name="TENDER_RET_DB_ID" required="true" type="INTEGER"/>

    <column name="CHARGE_AMOUNT" required="true" scale="2" size="7" type="DECIMAL"/>
	<column name="CARD_TYPE" required="true" size="20" type="VARCHAR"/>
	<column name="EXP_M_M" required="false" size="2" type="VARCHAR"/>
	<column name="EXP_Y_Y" required="false" size="2" type="VARCHAR"/>
	<column name="ZIP_CODE" required="false" size="20" type="VARCHAR"/>
	<column name="STREET_ADDRESS" required="false" size="200" type="VARCHAR"/>
	<column name="CVC_CODE" required="false" size="10" type="VARCHAR"/>

    <column name="RESPONSE_TYPE" required="true" type="SMALLINT"/>
    <column name="AUTHORIZATION_CODE" required="true" size="15" type="VARCHAR"/>
    <column name="CLIENT_TRANS_ID" required="false" size="15" type="VARCHAR"/>
    <column name="CREDIT_CARD_TRANS_ID" required="false" size="30" type="VARCHAR"/>
    <column name="MERCHANT_ACCOUNT_NUMBER" required="false" size="30" type="VARCHAR"/>
    <column name="PAYMENT_GROUPING_CODE" required="false" size="5" type="VARCHAR"/>
    <column name="PAYMENT_STATUS" required="false" size="15" type="VARCHAR"/>
    <column name="TXN_AUTHORIZATION_TIME" required="false" type="DATE"/>
    <column name="TXN_AUTHORIZATION_STAMP" required="false" size="15" type="VARCHAR"/>
    <column name="CARD_SECURITY_CODE_MATCH" required="false" size="15" type="VARCHAR"/>
    <column name="AVS_STREET" required="false" size="15" type="VARCHAR"/>
    <column name="AVS_ZIP" required="false" size="15" type="VARCHAR"/>
    <column name="RECON_BATCH_ID" required="false" size="50" type="VARCHAR"/>

	<column name="RESPONSE_CODE" required="true" type="SMALLINT"/>
	<column name="ERROR_CODE" required="true" type="SMALLINT"/>

    <column name="RESPONSE_DATE" required="true" type="DATE"/>

    <foreign-key foreignTable="COMPANY">
		<reference local="COMPANY_ID" foreign="COMPANYID"/>
    </foreign-key>
    <foreign-key foreignTable="PERSON">
		<reference local="CLIENT_ID" foreign="PERSONID"/>
    </foreign-key>
    <foreign-key foreignTable="TENDER_RET_DB">
		<reference local="TENDER_RET_DB_ID" foreign="TENDER_RET_DB_ID"/>
    </foreign-key>
</table>
			*/


			num_merged = 0;
			System.out.println("Merging >CHARGE_RESPONSE_DB");
			
			crit = new Criteria();
			crit.add(ChargeResponseDbPeer.CLIENT_ID, source.getId());
			obj_itr = ChargeResponseDbPeer.doSelect(crit).iterator();
			while (obj_itr.hasNext())
			{
				ChargeResponseDb existing_obj = (ChargeResponseDb)obj_itr.next();
				existing_obj.setClientId(destination.getId());
				existing_obj.save();
					
				num_merged++;
			}
			
			System.out.println("NUM MERGED >" + num_merged);

			
			/*
<table name="PRINTED_RECEIPT_DB" idMethod="native">
    <column name="PRINTED_RECEIPT_DB_ID" primaryKey="true" required="true" type="INTEGER" autoIncrement="true"/>
    <column name="PERSON_ID" required="true" type="INTEGER"/>
    <column name="PRIMARY_ORDER_ID" required="true" type="INTEGER"/>

    <column name="SUBTOTAL" required="true" scale="2" size="7" type="DECIMAL"/>
    <column name="TAX" required="true" scale="2" size="7" type="DECIMAL"/>
    <column name="TOTAL" required="true" scale="2" size="7" type="DECIMAL"/>
    <column name="DISCOUNT" required="true" scale="2" size="7" type="DECIMAL"/>
    <column name="DISCOUNT_PERCENTAGE" required="true" scale="2" size="7" type="DECIMAL"/>
    <column name="SHIPPING" required="true" scale="2" size="7" type="DECIMAL"/>

    <foreign-key foreignTable="PERSON">
		<reference local="PERSON_ID" foreign="PERSONID"/>
    </foreign-key>
    <foreign-key foreignTable="PERSONORDER">
		<reference local="PRIMARY_ORDER_ID" foreign="ORDERID"/>
    </foreign-key>
</table>
			*/


			num_merged = 0;
			System.out.println("Merging >PRINTED_RECEIPT_DB");
			
			crit = new Criteria();
			crit.add(PrintedReceiptDbPeer.PERSON_ID, source.getId());
			obj_itr = PrintedReceiptDbPeer.doSelect(crit).iterator();
			while (obj_itr.hasNext())
			{
				PrintedReceiptDb existing_obj = (PrintedReceiptDb)obj_itr.next();
				existing_obj.setPersonId(destination.getId());
				existing_obj.save();
					
				num_merged++;
			}
			
			System.out.println("NUM MERGED >" + num_merged);

			
			/*
<table name="PRACTITIONER_SCHEDULE" idMethod="native">
    <column name="PRACTITIONER_SCHEDULE_ID" primaryKey="true" required="true" type="INTEGER" autoIncrement="true"/>
    <column name="PRACTITIONER_ID" required="true" type="INTEGER"/>
    <column name="COMPANY_ID" required="true" type="INTEGER"/>
	
    <column name="NAME" required="true" type="VARCHAR" size="50"/>
    <column name="IS_BASE_SCHEDULE" required="true" type="SMALLINT" />
    <column name="START_DATE" required="false" type="DATE"/>
    <column name="END_DATE" required="false" type="DATE"/>
    
    <column name="CREATION_DATE" required="true" type="DATE"/>
    <column name="MODIFICATION_DATE" required="false" type="DATE"/>
    <column name="CREATE_PERSON_ID" required="false" type="INTEGER"/>
    <column name="MODIFY_PERSON_ID" required="false" type="INTEGER"/>
    
    <foreign-key foreignTable="PERSON">
	<reference local="PRACTITIONER_ID" foreign="PERSONID"/>
    </foreign-key>
    <foreign-key foreignTable="COMPANY">
	<reference local="COMPANY_ID" foreign="COMPANYID"/>
    </foreign-key>
    <foreign-key foreignTable="PERSON">
	<reference local="CREATE_PERSON_ID" foreign="PERSONID"/>
    </foreign-key>
    <foreign-key foreignTable="PERSON">
	<reference local="MODIFY_PERSON_ID" foreign="PERSONID"/>
    </foreign-key>
</table>
			*/


			num_merged = 0;
			System.out.println("Merging >PRACTITIONER_SCHEDULE");
			
			crit = new Criteria();
			crit.add(PractitionerSchedulePeer.PRACTITIONER_ID, source.getId());
			obj_itr = PractitionerSchedulePeer.doSelect(crit).iterator();
			while (obj_itr.hasNext())
			{
				PractitionerSchedule existing_obj = (PractitionerSchedule)obj_itr.next();
				existing_obj.setPractitionerId(destination.getId());
				existing_obj.save();
					
				num_merged++;
			}
			
			System.out.println("NUM MERGED >" + num_merged);

			
			/*
<table name="CONTACT_STATUS" idMethod="native">
    <column name="CONTACT_STATUS_ID" primaryKey="true" required="true" type="INTEGER" autoIncrement="true"/>
    <column name="COMPANY_ID" required="true" type="INTEGER"/>
	
    <column name="PERSON_ID" required="true" type="INTEGER"/>
    <column name="ADMIN_PERSON_ID" required="false" type="INTEGER" />
	
    <column name="STATUS" required="true" type="VARCHAR" size="200"/>
    <column name="CONTACT_DATE" required="true" type="DATE"/>
    <column name="TO_DO_DATE" required="false" type="DATE"/>

    <column name="PRACTICE_AREA_ID" required="false" type="INTEGER" />
    <column name="CONTACT_ATTEMPT" required="false" type="INTEGER" />
    
    <foreign-key foreignTable="COMPANY">
	<reference local="COMPANY_ID" foreign="COMPANYID"/>
    </foreign-key>
    <foreign-key foreignTable="PERSON">
	<reference local="PERSON_ID" foreign="PERSONID"/>
    </foreign-key>
    <foreign-key foreignTable="PERSON">
	<reference local="ADMIN_PERSON_ID" foreign="PERSONID"/>
    </foreign-key>
    <foreign-key foreignTable="PRACTICE_AREA">
	<reference local="PRACTICE_AREA_ID" foreign="PRACTICE_AREA_ID"/>
    </foreign-key>
</table>
			*/


			num_merged = 0;
			System.out.println("Merging >CONTACT_STATUS");
			
			crit = new Criteria();
			crit.add(ContactStatusPeer.PERSON_ID, source.getId());
			obj_itr = ContactStatusPeer.doSelect(crit).iterator();
			while (obj_itr.hasNext())
			{
				ContactStatus existing_obj = (ContactStatus)obj_itr.next();
				existing_obj.setPersonId(destination.getId());
				existing_obj.save();
					
				num_merged++;
			}
			
			System.out.println("NUM MERGED >" + num_merged);


			num_merged = 0;
			System.out.println("Merging >CONTACT_STATUS");
			
			crit = new Criteria();
			crit.add(ContactStatusPeer.ADMIN_PERSON_ID, source.getId());
			obj_itr = ContactStatusPeer.doSelect(crit).iterator();
			while (obj_itr.hasNext())
			{
				ContactStatus existing_obj = (ContactStatus)obj_itr.next();
				existing_obj.setAdminPersonId(destination.getId());
				existing_obj.save();
					
				num_merged++;
			}
			
			System.out.println("NUM MERGED >" + num_merged);

			
			/*
<table name="TO_DO_ITEM" idMethod="native">
    <column name="TO_DO_ITEM_ID" primaryKey="true" required="true" type="INTEGER" autoIncrement="true"/>
    <column name="COMPANY_ID" required="true" type="INTEGER"/>
	
    <column name="PERSON_ID" required="false" type="INTEGER"/>
    <column name="ASSIGN_PERSON_ID" required="false" type="INTEGER" />
    <column name="COMPLETE_PERSON_ID" required="false" type="INTEGER" />
	
    <column name="TO_DO_TEXT" required="true" type="VARCHAR" size="50"/>
    <column name="TO_DO_DATE" required="true" type="DATE"/>
    <column name="COMPLETION_DATE" required="false" type="DATE"/>
    
    <foreign-key foreignTable="COMPANY">
	<reference local="COMPANY_ID" foreign="COMPANYID"/>
    </foreign-key>
    <foreign-key foreignTable="PERSON">
	<reference local="PERSON_ID" foreign="PERSONID"/>
    </foreign-key>
    <foreign-key foreignTable="PERSON">
	<reference local="ASSIGN_PERSON_ID" foreign="PERSONID"/>
    </foreign-key>
    <foreign-key foreignTable="PERSON">
	<reference local="COMPLETE_PERSON_ID" foreign="PERSONID"/>
    </foreign-key>
</table>
			*/


			num_merged = 0;
			System.out.println("Merging >TO_DO_ITEM");
			
			crit = new Criteria();
			crit.add(ToDoItemPeer.PERSON_ID, source.getId());
			obj_itr = ToDoItemPeer.doSelect(crit).iterator();
			while (obj_itr.hasNext())
			{
				ToDoItem existing_obj = (ToDoItem)obj_itr.next();
				existing_obj.setPersonId(destination.getId());
				existing_obj.save();
					
				num_merged++;
			}
			
			System.out.println("NUM MERGED >" + num_merged);


			num_merged = 0;
			System.out.println("Merging >TO_DO_ITEM");
			
			crit = new Criteria();
			crit.add(ToDoItemPeer.ASSIGN_PERSON_ID, source.getId());
			obj_itr = ToDoItemPeer.doSelect(crit).iterator();
			while (obj_itr.hasNext())
			{
				ToDoItem existing_obj = (ToDoItem)obj_itr.next();
				existing_obj.setAssignPersonId(destination.getId());
				existing_obj.save();
					
				num_merged++;
			}
			
			System.out.println("NUM MERGED >" + num_merged);


			num_merged = 0;
			System.out.println("Merging >TO_DO_ITEM");
			
			crit = new Criteria();
			crit.add(ToDoItemPeer.COMPLETE_PERSON_ID, source.getId());
			obj_itr = ToDoItemPeer.doSelect(crit).iterator();
			while (obj_itr.hasNext())
			{
				ToDoItem existing_obj = (ToDoItem)obj_itr.next();
				existing_obj.setCompletePersonId(destination.getId());
				existing_obj.save();
					
				num_merged++;
			}
			
			System.out.println("NUM MERGED >" + num_merged);

			
			/*
<table name="CARE_DETAILS" idMethod="native">
    <column name="CARE_DETAILS_ID" primaryKey="true" required="true" type="INTEGER" autoIncrement="true"/>
    <column name="COMPANY_ID" required="true" type="INTEGER"/>
	
    <column name="PERSON_ID" required="true" type="INTEGER"/>
    <column name="PREFERRED_PRACTITIONER_ID" required="false" type="INTEGER" />
    <column name="PRACTICE_AREA_ID" required="false" type="INTEGER" />
	
    <column name="FREQUENCY_OF_CARE_VALUE" required="false" type="INTEGER" />
    <column name="FREQUENCY_OF_CARE_PERIOD" required="false" type="INTEGER" />
    <column name="FREQUENCY_OF_CARE_PERIOD_RECURRENCE" required="false" type="INTEGER" />
    <column name="FREQUENCY_OF_CARE_DAYS" required="false" type="INTEGER" />

    <column name="IS_PRN" required="true" type="SMALLINT" />
    
    <column name="CREATION_DATE" required="true" type="DATE"/>
    <column name="MODIFICATION_DATE" required="false" type="DATE"/>
    <column name="CREATE_PERSON_ID" required="false" type="INTEGER"/>
    <column name="MODIFY_PERSON_ID" required="false" type="INTEGER"/>
    
    <foreign-key foreignTable="COMPANY">
	<reference local="COMPANY_ID" foreign="COMPANYID"/>
    </foreign-key>
    <foreign-key foreignTable="PERSON">
	<reference local="PERSON_ID" foreign="PERSONID"/>
    </foreign-key>
    <foreign-key foreignTable="PERSON">
	<reference local="PREFERRED_PRACTITIONER_ID" foreign="PERSONID"/>
    </foreign-key>
    <foreign-key foreignTable="PRACTICE_AREA">
	<reference local="PRACTICE_AREA_ID" foreign="PRACTICE_AREA_ID"/>
    </foreign-key>
    <foreign-key foreignTable="PERSON">
	<reference local="CREATE_PERSON_ID" foreign="PERSONID"/>
    </foreign-key>
    <foreign-key foreignTable="PERSON">
	<reference local="MODIFY_PERSON_ID" foreign="PERSONID"/>
    </foreign-key>
</table>
			*/


			num_merged = 0;
			System.out.println("Merging >CARE_DETAILS");
			
			crit = new Criteria();
			crit.add(CareDetailsPeer.PERSON_ID, source.getId());
			obj_itr = CareDetailsPeer.doSelect(crit).iterator();
			while (obj_itr.hasNext())
			{
				CareDetails existing_obj = (CareDetails)obj_itr.next();
				existing_obj.setPersonId(destination.getId());
				existing_obj.save();
					
				num_merged++;
			}
			
			System.out.println("NUM MERGED >" + num_merged);


			num_merged = 0;
			System.out.println("Merging >CARE_DETAILS");
			
			crit = new Criteria();
			crit.add(CareDetailsPeer.PREFERRED_PRACTITIONER_ID, source.getId());
			obj_itr = CareDetailsPeer.doSelect(crit).iterator();
			while (obj_itr.hasNext())
			{
				CareDetails existing_obj = (CareDetails)obj_itr.next();
				existing_obj.setPreferredPractitionerId(destination.getId());
				existing_obj.save();
					
				num_merged++;
			}
			
			System.out.println("NUM MERGED >" + num_merged);

			
			/*
<table name="BLACK_HOLE_MEMBER" idMethod="native">
    <column name="BLACK_HOLE_MEMBER_ID" primaryKey="true" required="true" type="INTEGER" autoIncrement="true"/>
	
    <column name="BLACK_HOLE_ID" required="true" type="INTEGER"/>
    <column name="PERSON_ID" required="true" type="INTEGER"/>
    <column name="PRACTICE_AREA_ID" required="false" type="INTEGER" />
	
    <column name="IS_CURRENT_MEMBER" required="true" type="SMALLINT"/>
    <column name="BLACK_HOLE_ADD_DATE" required="true" type="DATE"/>
    <column name="BLACK_HOLE_REMOVE_DATE" required="false" type="DATE"/>
    
    <foreign-key foreignTable="BLACK_HOLE">
		<reference local="BLACK_HOLE_ID" foreign="BLACK_HOLE_ID"/>
    </foreign-key>
    <foreign-key foreignTable="PERSON">
		<reference local="PERSON_ID" foreign="PERSONID"/>
    </foreign-key>
    <foreign-key foreignTable="PRACTICE_AREA">
		<reference local="PRACTICE_AREA_ID" foreign="PRACTICE_AREA_ID"/>
    </foreign-key>
</table>
			*/

/*
			num_merged = 0;
			System.out.println("Merging >BLACK_HOLE_MEMBER");
			
			crit = new Criteria();
			crit.add(BlackHoleMemberPeer.PERSON_ID, source.getId());
			obj_itr = BlackHoleMemberPeer.doSelect(crit).iterator();
			while (obj_itr.hasNext())
			{
				BlackHoleMember existing_obj = (BlackHoleMember)obj_itr.next();
				existing_obj.setPersonId(destination.getId());
				existing_obj.save();
					
				num_merged++;
			}
			
			System.out.println("NUM MERGED >" + num_merged);
*/
			
			/*
<table name="CHECKOUT_ORDERLINE" idMethod="native">
    <column name="CHECKOUT_ORDERLINE_ID" primaryKey="true" required="true" type="INTEGER" autoIncrement="true"/>
    <column name="QUANTITY" required="true" scale="2" size="7" type="DECIMAL"/>
    <column name="ORDERID" required="true" type="INTEGER"/>
    <column name="CHECKOUT_CODE_ID" required="true" type="INTEGER"/>
    <column name="PRICE" required="true" scale="2" size="7" type="DECIMAL"/>
    <column name="TAX" required="false" scale="2" size="7" type="DECIMAL"/>
    <column name="ACTUAL_AMOUNT" scale="2" size="7" type="DECIMAL"/>
    <column name="DISCOUNTCODE" size="50" type="VARCHAR"/>
    <column name="ORDERSTATUS" size="20" type="VARCHAR"/>
    <column name="PAYMENT_PLAN_INSTANCE_ID" required="false" type="INTEGER"/>
    <column name="APPOINTMENT_ID" required="false" type="INTEGER"/>
    <column name="PRACTITIONER_ID" required="false" type="INTEGER"/>
    <column name="COMMISSION" required="false" scale="2" size="7" type="DECIMAL"/>
	
    <foreign-key foreignTable="PERSONORDER">
	<reference local="ORDERID" foreign="ORDERID"/>
    </foreign-key>
    <foreign-key foreignTable="CHECKOUT_CODE">
	<reference local="CHECKOUT_CODE_ID" foreign="CHECKOUT_CODE_ID"/>
    </foreign-key>
    <foreign-key foreignTable="ORDERSTATUS">
	<reference local="ORDERSTATUS" foreign="ORDERSTATUS"/>
    </foreign-key>
    <foreign-key foreignTable="PAYMENT_PLAN_INSTANCE">
	<reference local="PAYMENT_PLAN_INSTANCE_ID" foreign="PAYMENT_PLAN_INSTANCE_ID"/>
    </foreign-key>
    <foreign-key foreignTable="APPOINTMENT">
	<reference local="APPOINTMENT_ID" foreign="APPOINTMENT_ID"/>
    </foreign-key>
    <foreign-key foreignTable="PERSON">
	<reference local="PRACTITIONER_ID" foreign="PERSONID"/>
    </foreign-key>
</table>
			*/


			num_merged = 0;
			System.out.println("Merging >CHECKOUT_ORDERLINE");
			
			crit = new Criteria();
			crit.add(CheckoutOrderlinePeer.PRACTITIONER_ID, source.getId());
			obj_itr = CheckoutOrderlinePeer.doSelect(crit).iterator();
			while (obj_itr.hasNext())
			{
				CheckoutOrderline existing_obj = (CheckoutOrderline)obj_itr.next();
				existing_obj.setPractitionerId(destination.getId());
				existing_obj.save();
					
				num_merged++;
			}
			
			System.out.println("NUM MERGED >" + num_merged);

			
			/*
<table name="PAYMENT_PLAN_INSTANCE" idMethod="native">
    <column name="PAYMENT_PLAN_INSTANCE_ID" primaryKey="true" required="true" type="INTEGER" autoIncrement="true"/>
    <column name="PAYMENT_PLAN_ID" required="true" type="INTEGER"/>
    <column name="PERSON_ID" required="true" type="INTEGER"/>
    <column name="VISITS_USED" required="true" type="INTEGER"/>
    <column name="VISITS_REMAINING" required="true" type="INTEGER"/>
    <column name="IS_PAID_FOR" required="true" type="SMALLINT"/>
    <column name="AMOUNT_CHARGED" required="true" scale="2" size="7" type="DECIMAL"/>
    <column name="AMOUNT_PAID" required="true" scale="2" size="7" type="DECIMAL"/>
    <column name="MONTHS_PAID" required="true" type="INTEGER"/>
    <column name="START_DATE" required="true" type="DATE"/>
    <column name="END_DATE" required="false" type="DATE"/>
    <column name="ISACTIVE" required="true" type="SMALLINT"/>
    <column name="ALLOW_GROUP_POOLING" required="true" type="SMALLINT"/>
    <column name="NOTE" required="false" type="VARCHAR" size="255"/>
    <column name="CREATION_DATE" required="true" type="DATE"/>
    <column name="MODIFICATION_DATE" required="false" type="DATE"/>
    <column name="CREATE_PERSON_ID" required="false" type="INTEGER"/>
    <column name="MODIFY_PERSON_ID" required="false" type="INTEGER"/>
	
    <foreign-key foreignTable="PAYMENT_PLAN">
	<reference local="PAYMENT_PLAN_ID" foreign="PAYMENT_PLAN_ID"/>
    </foreign-key>
    <foreign-key foreignTable="PERSON">
	<reference local="PERSON_ID" foreign="PERSONID"/>
    </foreign-key>
    <foreign-key foreignTable="PERSON">
	<reference local="CREATE_PERSON_ID" foreign="PERSONID"/>
    </foreign-key>
    <foreign-key foreignTable="PERSON">
	<reference local="MODIFY_PERSON_ID" foreign="PERSONID"/>
    </foreign-key>
</table>
			*/


			num_merged = 0;
			System.out.println("Merging >PAYMENT_PLAN_INSTANCE");
			
			crit = new Criteria();
			crit.add(PaymentPlanInstancePeer.PERSON_ID, source.getId());
			obj_itr = PaymentPlanInstancePeer.doSelect(crit).iterator();
			while (obj_itr.hasNext())
			{
				PaymentPlanInstance existing_obj = (PaymentPlanInstance)obj_itr.next();
				existing_obj.setPersonId(destination.getId());
				existing_obj.save();
					
				num_merged++;
			}
			
			System.out.println("NUM MERGED >" + num_merged);

			
			/*
<table name="GROUP_UNDER_CARE" idMethod="native">
    <column name="GROUP_UNDER_CARE_ID" required="true" primaryKey="true" type="INTEGER" autoIncrement="true"/>
    <column name="COMPANY_ID" required="true" type="INTEGER"/>
    <column name="NOTE" required="true" type="VARCHAR" size="255"/>
    <column name="PRIMARY_CLIENT_ID" required="true" type="INTEGER"/>
    <column name="FRP_ID" required="true" type="INTEGER"/>
    <column name="CREATION_DATE" required="true" type="DATE"/>
    <column name="MODIFICATION_DATE" required="false" type="DATE"/>
    <column name="CREATE_PERSON_ID" required="false" type="INTEGER"/>
    <column name="MODIFY_PERSON_ID" required="false" type="INTEGER"/>
	
    <foreign-key foreignTable="COMPANY">
	<reference local="COMPANY_ID" foreign="COMPANYID"/>
    </foreign-key>
    <foreign-key foreignTable="PERSON">
	<reference local="PRIMARY_CLIENT_ID" foreign="PERSONID"/>
    </foreign-key>
    <foreign-key foreignTable="PERSON">
	<reference local="FRP_ID" foreign="PERSONID"/>
    </foreign-key>
    <foreign-key foreignTable="PERSON">
	<reference local="CREATE_PERSON_ID" foreign="PERSONID"/>
    </foreign-key>
    <foreign-key foreignTable="PERSON">
	<reference local="MODIFY_PERSON_ID" foreign="PERSONID"/>
    </foreign-key>
</table>
			*/


			/*
			num_merged = 0;
			System.out.println("Merging >GROUP_UNDER_CARE");
			
			crit = new Criteria();
			crit.add(GroupUnderCarePeer.PRIMARY_CLIENT_ID, source.getId());
			obj_itr = GroupUnderCarePeer.doSelect(crit).iterator();
			while (obj_itr.hasNext())
			{
				GroupUnderCare existing_obj = (GroupUnderCare)obj_itr.next();
				existing_obj.setPrimaryClientId(destination.getId());
				existing_obj.save();
					
				num_merged++;
			}
			
			System.out.println("NUM MERGED >" + num_merged);


			num_merged = 0;
			System.out.println("Merging >GROUP_UNDER_CARE");
			
			crit = new Criteria();
			crit.add(GroupUnderCarePeer.PRIMARY_CLIENT_ID, source.getId());
			obj_itr = GroupUnderCarePeer.doSelect(crit).iterator();
			while (obj_itr.hasNext())
			{
				GroupUnderCare existing_obj = (GroupUnderCare)obj_itr.next();
				existing_obj.setPrimaryClientId(destination.getId());
				existing_obj.save();
					
				num_merged++;
			}
			
			System.out.println("NUM MERGED >" + num_merged);
			*/
			
			
			
			/*
<table name="GROUP_UNDER_CARE_MEMBER">
    <column name="GROUP_UNDER_CARE_ID" required="true" primaryKey="true" type="INTEGER"/>
    <column name="PERSON_ID" required="true" primaryKey="true" type="INTEGER"/>
    <column name="GROUP_UNDER_CARE_MEMBER_TYPE_ID" required="false" type="INTEGER"/>
    <column name="RELATIONSHIP_TO_PRIMARY_CLIENT" required="false" type="SMALLINT" />
	
    <foreign-key foreignTable="GROUP_UNDER_CARE">
	<reference local="GROUP_UNDER_CARE_ID" foreign="GROUP_UNDER_CARE_ID"/>
    </foreign-key>
    <foreign-key foreignTable="PERSON">
	<reference local="PERSON_ID" foreign="PERSONID"/>
    </foreign-key>
    <foreign-key foreignTable="GROUP_UNDER_CARE_MEMBER_TYPE">
	<reference local="GROUP_UNDER_CARE_MEMBER_TYPE_ID" foreign="GROUP_UNDER_CARE_MEMBER_TYPE_ID"/>
    </foreign-key>
</table>
			*/

			/*
			try
			{
				int destination_group_size = destination.getGroupUnderCare().getMembers().size();
				if (destination_group_size > 0)
				{
					num_merged = 0;
					System.out.println("Merging >GROUP_UNDER_CARE_MEMBER");

					crit = new Criteria();
					crit.add(GroupUnderCareMemberPeer.PERSON_ID, source.getId());
					obj_itr = GroupUnderCareMemberPeer.doSelect(crit).iterator();
					while (obj_itr.hasNext())
					{
						GroupUnderCareMember existing_obj = (GroupUnderCareMember)obj_itr.next();
						existing_obj.setPersonId(destination.getId());
						existing_obj.save();

						num_merged++;
					}

					System.out.println("NUM MERGED >" + num_merged);
				}
			}
			catch (Exception x)
			{
				x.printStackTrace();
			}
*/
			
			/*
<table name="APPOINTMENT" idMethod="native">
    <column name="APPOINTMENT_ID" primaryKey="true" required="true" type="INTEGER" autoIncrement="true"/>
    <column name="COMPANY_ID" required="true" type="INTEGER"/>
    <column name="CLIENT_ID" required="true" type="INTEGER"/>
    <column name="PRACTITIONER_ID" required="true" type="INTEGER"/>
    <column name="APPOINTMENT_TYPE_ID" required="false" type="INTEGER"/>
    <column name="PARENT_ID" required="false" type="INTEGER"/>
    <column name="APPOINTMENT_STATE" type="SMALLINT" default="0"/>
    <column name="APPOINTMENT_DATE" required="true" type="DATE"/>
    <column name="APPOINTMENT_END_DATE" required="true" type="DATE"/>
    <column name="DURATION_MINUTES" required="true" type="SMALLINT" />
	
    <column name="COMMENTS" required="false" type="VARCHAR" size="200"/>
	
    <column name="RECURRING" required="false" type="SMALLINT" default="0" />
    <column name="RECUR_VAL" required="false" type="SMALLINT" />
    <column name="RECUR_PERIOD" required="false" type="SMALLINT" />
    <column name="RECUR_PW" required="false" type="SMALLINT" />
    <column name="RECUR_PWS" required="false" type="SMALLINT" />
    <column name="STOP_AFTER_N" required="false" type="SMALLINT" />
    <column name="STOP_BY_DATE" required="false" type="DATE"/>

    <column name="FIRST_TIME" required="false" type="SMALLINT" default="0" />
    
    <column name="RESCHEDULE_DATE" required="false" type="DATE"/>
    <column name="LATE_DATE" required="false" type="DATE"/>
    <column name="CHECKED_IN_DATE" required="false" type="DATE"/>
    <column name="CHECKED_OUT_DATE" required="false" type="DATE"/>
    <column name="CANCELLED_DATE" required="false" type="DATE"/>
    <column name="NO_SHOW_DATE" required="false" type="DATE"/>

    <column name="PAYMENT_PLAN_INSTANCE_ID" required="false" type="INTEGER"/>
    
    <column name="CREATION_DATE" required="true" type="DATE"/>
    <column name="MODIFICATION_DATE" required="false" type="DATE"/>
    <column name="CREATE_PERSON_ID" required="false" type="INTEGER"/>
    <column name="MODIFY_PERSON_ID" required="false" type="INTEGER"/>
    
    <foreign-key foreignTable="COMPANY">
	<reference local="COMPANY_ID" foreign="COMPANYID"/>
    </foreign-key>
    <foreign-key foreignTable="PERSON">
	<reference local="CLIENT_ID" foreign="PERSONID"/>
    </foreign-key>
    <foreign-key foreignTable="PERSON">
	<reference local="PRACTITIONER_ID" foreign="PERSONID"/>
    </foreign-key>
    <foreign-key foreignTable="APPOINTMENT_TYPE">
	<reference local="APPOINTMENT_TYPE_ID" foreign="APPOINTMENT_TYPE_ID"/>
    </foreign-key>
    <foreign-key foreignTable="APPOINTMENT">
	<reference local="PARENT_ID" foreign="APPOINTMENT_ID"/>
    </foreign-key>
    <foreign-key foreignTable="PAYMENT_PLAN_INSTANCE">
	<reference local="PAYMENT_PLAN_INSTANCE_ID" foreign="PAYMENT_PLAN_INSTANCE_ID"/>
    </foreign-key>
    <foreign-key foreignTable="PERSON">
	<reference local="CREATE_PERSON_ID" foreign="PERSONID"/>
    </foreign-key>
    <foreign-key foreignTable="PERSON">
	<reference local="MODIFY_PERSON_ID" foreign="PERSONID"/>
    </foreign-key>
</table>
			*/


			num_merged = 0;
			System.out.println("Merging >APPOINTMENT");
			
			crit = new Criteria();
			crit.add(AppointmentPeer.CLIENT_ID, source.getId());
			obj_itr = AppointmentPeer.doSelect(crit).iterator();
			while (obj_itr.hasNext())
			{
				Appointment existing_obj = (Appointment)obj_itr.next();
				existing_obj.setClientId(destination.getId());
				existing_obj.save();
					
				num_merged++;
			}
			
			System.out.println("NUM MERGED >" + num_merged);


			num_merged = 0;
			System.out.println("Merging >APPOINTMENT");
			
			crit = new Criteria();
			crit.add(AppointmentPeer.PRACTITIONER_ID, source.getId());
			obj_itr = AppointmentPeer.doSelect(crit).iterator();
			while (obj_itr.hasNext())
			{
				Appointment existing_obj = (Appointment)obj_itr.next();
				existing_obj.setPractitionerId(destination.getId());
				existing_obj.save();
					
				num_merged++;
			}
			
			System.out.println("NUM MERGED >" + num_merged);

			
			/*
<table name="PRACTICE_AREA_PRACTITIONER">
    <column name="PRACTICE_AREA_ID" required="true" primaryKey="true" type="INTEGER"/>
    <column name="PRACTITIONER_ID" required="true" primaryKey="true" type="INTEGER"/>
	
    <foreign-key foreignTable="PRACTICE_AREA">
	<reference local="PRACTICE_AREA_ID" foreign="PRACTICE_AREA_ID"/>
    </foreign-key>
    <foreign-key foreignTable="PERSON">
	<reference local="PRACTITIONER_ID" foreign="PERSONID"/>
    </foreign-key>
</table>
			*/


			num_merged = 0;
			System.out.println("Merging >PRACTICE_AREA_PRACTITIONER");
			
			crit = new Criteria();
			crit.add(PracticeAreaPractitionerPeer.PRACTITIONER_ID, source.getId());
			obj_itr = PracticeAreaPractitionerPeer.doSelect(crit).iterator();
			while (obj_itr.hasNext())
			{
				PracticeAreaPractitioner existing_obj = (PracticeAreaPractitioner)obj_itr.next();
				existing_obj.setPractitionerId(destination.getId());
				existing_obj.save();
					
				num_merged++;
			}
			
			System.out.println("NUM MERGED >" + num_merged);

			
			/*
<table name="SOAP_NOTES" idMethod="native">
    <column name="SOAP_NOTES_ID" primaryKey="true" required="true" type="INTEGER" autoIncrement="true"/>
    <column name="PRACTICE_AREA_ID" required="true" type="INTEGER"/>
    <column name="PERSON_ID" required="true" type="INTEGER"/>
    <column name="S_NOTES" type="LONGVARCHAR"/>
    <column name="O_NOTES" type="LONGVARCHAR"/>
    <column name="A_NOTES" type="LONGVARCHAR"/>
    <column name="P_NOTES" type="LONGVARCHAR"/>
    <column name="ANALYSIS_DATE" required="true" type="TIMESTAMP"/>
    <column name="CREATION_DATE" required="true" type="TIMESTAMP"/>
    <column name="MODIFICATION_DATE" required="false" type="TIMESTAMP"/>
    <column name="CREATE_PERSON_ID" required="false" type="INTEGER"/>
    <column name="MODIFY_PERSON_ID" required="false" type="INTEGER"/>

    <foreign-key foreignTable="PRACTICE_AREA">
	<reference local="PRACTICE_AREA_ID" foreign="PRACTICE_AREA_ID"/>
    </foreign-key>
    <foreign-key foreignTable="PERSON">
	<reference local="PERSON_ID" foreign="PERSONID"/>
    </foreign-key>
    <foreign-key foreignTable="PERSON">
	<reference local="CREATE_PERSON_ID" foreign="PERSONID"/>
    </foreign-key>
    <foreign-key foreignTable="PERSON">
	<reference local="MODIFY_PERSON_ID" foreign="PERSONID"/>
    </foreign-key>
</table>
			*/


			num_merged = 0;
			System.out.println("Merging >SOAP_NOTES");
			
			crit = new Criteria();
			crit.add(SoapNotesPeer.PERSON_ID, source.getId());
			obj_itr = SoapNotesPeer.doSelect(crit).iterator();
			while (obj_itr.hasNext())
			{
				SoapNotes existing_obj = (SoapNotes)obj_itr.next();
				existing_obj.setPersonId(destination.getId());
				existing_obj.save();
					
				num_merged++;
			}
			
			System.out.println("NUM MERGED >" + num_merged);

			
			/*
<table name="HEALTH_HISTORY" idMethod="native">
    <column name="HEALTH_HISTORY_ID" primaryKey="true" required="true" type="INTEGER" autoIncrement="true"/>
    <column name="PERSON_ID" required="true" type="INTEGER"/>
    <column name="HISTORY" type="LONGVARCHAR"/>
    <column name="CREATION_DATE" required="true" type="TIMESTAMP"/>
    <column name="MODIFICATION_DATE" required="false" type="TIMESTAMP"/>
    <column name="CREATE_PERSON_ID" required="false" type="INTEGER"/>
    <column name="MODIFY_PERSON_ID" required="false" type="INTEGER"/>

    <foreign-key foreignTable="PERSON">
	<reference local="PERSON_ID" foreign="PERSONID"/>
    </foreign-key>
    <foreign-key foreignTable="PERSON">
	<reference local="CREATE_PERSON_ID" foreign="PERSONID"/>
    </foreign-key>
    <foreign-key foreignTable="PERSON">
	<reference local="MODIFY_PERSON_ID" foreign="PERSONID"/>
    </foreign-key>
</table>
			*/


			num_merged = 0;
			System.out.println("Merging >HEALTH_HISTORY");
			
			crit = new Criteria();
			crit.add(HealthHistoryPeer.PERSON_ID, source.getId());
			obj_itr = HealthHistoryPeer.doSelect(crit).iterator();
			while (obj_itr.hasNext())
			{
				HealthHistory existing_obj = (HealthHistory)obj_itr.next();
				existing_obj.setPersonId(destination.getId());
				existing_obj.save();
					
				num_merged++;
			}
			
			System.out.println("NUM MERGED >" + num_merged);

			
			/*
<table name="MINERAL_RATIO" idMethod="native">
    <column name="MINERAL_RATIOS_ID" primaryKey="true" required="true" type="INTEGER" autoIncrement="true"/>
    <column name="PERSON_ID" required="true" type="INTEGER"/>
    <column name="CA_MG" type="DECIMAL"/>
    <column name="CA_K" type="DECIMAL"/>
    <column name="NA_MG" type="DECIMAL"/>
    <column name="NA_K" type="DECIMAL"/>
    <column name="ZN_CU" type="DECIMAL"/>
    <column name="CA_P" type="DECIMAL"/>
    <column name="CA" type="DECIMAL"/>
    <column name="K" type="DECIMAL"/>
    <column name="CU" type="DECIMAL"/>
    <column name="HG" type="DECIMAL"/>
    <column name="OXIDATION_TYPE" size="100" type="VARCHAR"/>
    <column name="NOTES" type="LONGVARCHAR"/>
    <column name="ANALYSIS_DATE" required="true" type="TIMESTAMP"/>
    <column name="CREATION_DATE" required="true" type="TIMESTAMP"/>
    <column name="MODIFICATION_DATE" required="false" type="TIMESTAMP"/>
    <column name="CREATE_PERSON_ID" required="false" type="INTEGER"/>
    <column name="MODIFY_PERSON_ID" required="false" type="INTEGER"/>

    <foreign-key foreignTable="PERSON">
	<reference local="PERSON_ID" foreign="PERSONID"/>
    </foreign-key>
    <foreign-key foreignTable="PERSON">
	<reference local="CREATE_PERSON_ID" foreign="PERSONID"/>
    </foreign-key>
    <foreign-key foreignTable="PERSON">
	<reference local="MODIFY_PERSON_ID" foreign="PERSONID"/>
    </foreign-key>
</table>
			*/


			num_merged = 0;
			System.out.println("Merging >MINERAL_RATIO");
			
			crit = new Criteria();
			crit.add(MineralRatioPeer.PERSON_ID, source.getId());
			obj_itr = MineralRatioPeer.doSelect(crit).iterator();
			while (obj_itr.hasNext())
			{
				MineralRatio existing_obj = (MineralRatio)obj_itr.next();
				existing_obj.setPersonId(destination.getId());
				existing_obj.save();
					
				num_merged++;
			}
			
			System.out.println("NUM MERGED >" + num_merged);

			
			/*
<table name="MINERAL_RATIOS_CLIENT_DOCUMENT" idMethod="native">
    <column name="MINERAL_RATIOS_CLIENT_DOCUMENT_ID" primaryKey="true" required="true" type="INTEGER" autoIncrement="true"/>
    <column name="PERSON_ID" required="true" type="INTEGER"/>
    <column name="IS_TEMPLATE" type="SMALLINT" default="0"/>
    <column name="NOTES" type="LONGVARCHAR"/>
    <column name="REPORT_DATE" required="true" type="TIMESTAMP"/>
    <column name="INITIAL_RECOMMENDATIONS" type="LONGVARCHAR"/>
    <column name="SUPPLEMENTS_HERBAL_MIXTURES" type="LONGVARCHAR"/>
    <column name="DETOX_EXCERCISE_DIET_STRESS_MANAGEMENT" type="LONGVARCHAR"/>
    <column name="NEXT_APPOINTMENT" type="LONGVARCHAR"/>
    <column name="PHASE_I_DATE" required="true" type="TIMESTAMP"/>
    <column name="PHASE_I_I_DATE" required="true" type="TIMESTAMP"/>
    <column name="PHASE_I_I_I_DATE" required="true" type="TIMESTAMP"/>
    <column name="PHASE_I_V_DATE" required="true" type="TIMESTAMP"/>
    <column name="PHASE_V_DATE" required="true" type="TIMESTAMP"/>
    <column name="CREATION_DATE" required="true" type="TIMESTAMP"/>
    <column name="MODIFICATION_DATE" required="false" type="TIMESTAMP"/>
    <column name="CREATE_PERSON_ID" required="false" type="INTEGER"/>
    <column name="MODIFY_PERSON_ID" required="false" type="INTEGER"/>

    <foreign-key foreignTable="PERSON">
	<reference local="PERSON_ID" foreign="PERSONID"/>
    </foreign-key>
    <foreign-key foreignTable="PERSON">
	<reference local="CREATE_PERSON_ID" foreign="PERSONID"/>
    </foreign-key>
    <foreign-key foreignTable="PERSON">
	<reference local="MODIFY_PERSON_ID" foreign="PERSONID"/>
    </foreign-key>
</table>
			*/


			num_merged = 0;
			System.out.println("Merging >MINERAL_RATIOS_CLIENT_DOCUMENT");
			
			crit = new Criteria();
			crit.add(MineralRatiosClientDocumentPeer.PERSON_ID, source.getId());
			obj_itr = MineralRatiosClientDocumentPeer.doSelect(crit).iterator();
			while (obj_itr.hasNext())
			{
				MineralRatiosClientDocument existing_obj = (MineralRatiosClientDocument)obj_itr.next();
				existing_obj.setPersonId(destination.getId());
				existing_obj.save();
					
				num_merged++;
			}
			
			System.out.println("NUM MERGED >" + num_merged);

			
			/*
<table name="PERSON_ADDRESS">
    <column name="PERSON_ID" required="true" primaryKey="true" type="INTEGER"/>
    <column name="ADDRESS_ID" required="true" primaryKey="true" type="INTEGER"/>

    <foreign-key foreignTable="PERSON">
		<reference local="PERSON_ID" foreign="PERSONID"/>
    </foreign-key>
    <foreign-key foreignTable="ADDRESS">
		<reference local="ADDRESS_ID" foreign="ADDRESSID"/>
    </foreign-key>
</table>
			*/

			
			
			
			/*
<table name="PERSON_GROUP_MEMBER">
    <column name="PERSONID" required="true" primaryKey="true" type="INTEGER"/>
    <column name="PERSON_GROUP_ID" required="true" type="INTEGER"/>

    <foreign-key foreignTable="PERSON">
	<reference local="PERSONID" foreign="PERSONID"/>
    </foreign-key>
    <foreign-key foreignTable="PERSON_GROUP">
	<reference local="PERSON_GROUP_ID" foreign="PERSON_GROUP_ID"/>
    </foreign-key>
</table>
			*/

			
			
			
			/*
<table name="PERSONORDER" idMethod="native">
    <column name="ORDERID" required="true" primaryKey="true" type="INTEGER" autoIncrement="true" />
    <column name="COMPANY_ID" required="true" type="INTEGER"/>
    <column name="PERSONID" required="true" type="INTEGER"/>
    <column name="ORDERDATE" required="true" type="DATE"/>
    <column name="ORDERSTATUS" required="true" size="20" type="VARCHAR"/>
    <column name="CREDITCARDTYPE" size="25" type="VARCHAR"/>
    <column name="AUTHORIZATIONCODE" size="25" type="VARCHAR"/>
    <column name="BILLTOADDRESSID" type="INTEGER"/>
    <column name="SHIPTOADDRESSID" type="INTEGER"/>
    <column name="SHIPMETHOD" size="25" type="VARCHAR"/>
    <column name="SHIPTRACKCODE" size="25" type="VARCHAR"/>
    <column name="SHIPPRICE" scale="2" size="7" type="DECIMAL"/>
    <column name="TAXPRICE" scale="2" size="7" type="DECIMAL"/>
    <column name="SUBTOTAL" scale="2" size="7" type="DECIMAL"/>
    <column name="TOTALPRICE" scale="2" size="7" type="DECIMAL"/>
    <column name="BALANCE" scale="2" size="7" type="DECIMAL"/>
    <column name="DISCOUNT" scale="2" size="7" type="DECIMAL"/>
    <column name="PONUMBER" size="60" type="VARCHAR"/>
    <column name="PAYMENTMETHOD" size="30" type="VARCHAR"/>
    <column name="IS_PAID" required="false" type="SMALLINT"/>
    <column name="IS_RETURN" required="true" type="SMALLINT" default="0"/>

    <column name="MEMO" size="250" type="VARCHAR"/>
	    
    <column name="CREATION_DATE" required="true" type="DATE"/>
    <column name="MODIFICATION_DATE" required="false" type="DATE"/>
    <column name="CREATE_PERSON_ID" required="false" type="INTEGER"/>
    <column name="MODIFY_PERSON_ID" required="false" type="INTEGER"/>

    <column name="REQUEST_I_D" size="50" type="VARCHAR"/>
    <column name="IS_UPDATED" required="true" type="SMALLINT" default="0"/>

    <column name="WORKSTATION_NAME" required="false" size="100" type="VARCHAR"/>

    <foreign-key foreignTable="PERSON">
		<reference local="PERSONID" foreign="PERSONID"/>
    </foreign-key>
    <foreign-key foreignTable="CREDITCARDTYPE">
		<reference local="CREDITCARDTYPE" foreign="CREDITCARDTYPE"/>
    </foreign-key>
    <foreign-key foreignTable="ADDRESS">
		<reference local="BILLTOADDRESSID" foreign="ADDRESSID"/>
    </foreign-key>
    <foreign-key foreignTable="ADDRESS">
		<reference local="SHIPTOADDRESSID" foreign="ADDRESSID"/>
    </foreign-key>
    <foreign-key foreignTable="SHIPMETHOD">
		<reference local="SHIPMETHOD" foreign="SHIPMETHOD"/>
    </foreign-key>
    <foreign-key foreignTable="ORDERSTATUS">
		<reference local="ORDERSTATUS" foreign="ORDERSTATUS"/>
    </foreign-key>
    <foreign-key foreignTable="PAYMENTMETHOD">
		<reference local="PAYMENTMETHOD" foreign="PAYMENTMETHOD"/>
    </foreign-key>
	<foreign-key foreignTable="COMPANY">
		<reference local="COMPANY_ID" foreign="COMPANYID"/>
	</foreign-key>
	<foreign-key foreignTable="PERSON">
		<reference local="CREATE_PERSON_ID" foreign="PERSONID"/>
	</foreign-key>
	<foreign-key foreignTable="PERSON">
		<reference local="MODIFY_PERSON_ID" foreign="PERSONID"/>
	</foreign-key>
</table>
			*/


			num_merged = 0;
			System.out.println("Merging >PERSONORDER");
			
			crit = new Criteria();
			crit.add(PersonorderPeer.PERSONID, source.getId());
			obj_itr = PersonorderPeer.doSelect(crit).iterator();
			while (obj_itr.hasNext())
			{
				Personorder existing_obj = (Personorder)obj_itr.next();
				existing_obj.setPersonid(destination.getId());
				existing_obj.save();
					
				num_merged++;
			}
			
			System.out.println("NUM MERGED >" + num_merged);

			
			/*
<table name="PHONENUMBER">
    <column name="PHONETYPE" required="true" primaryKey="true" size="20" type="VARCHAR"/>
    <column name="PERSONID" required="true" primaryKey="true" type="INTEGER"/>
    <column name="PHONENUMBER" required="true" size="50" type="VARCHAR"/>

    <foreign-key foreignTable="PERSON">
	<reference local="PERSONID" foreign="PERSONID"/>
    </foreign-key>
    <foreign-key foreignTable="PHONETYPE">
	<reference local="PHONETYPE" foreign="PHONETYPE"/>
    </foreign-key>
</table>
			*/

			
			
			
			/*
<table name="PERSONROLE">
    <column name="PERSONID" required="true" primaryKey="true" type="INTEGER"/>
    <column name="ROLEID" required="true" primaryKey="true" type="INTEGER"/>

    <foreign-key foreignTable="PERSON">
	<reference local="PERSONID" foreign="PERSONID"/>
    </foreign-key>
    <foreign-key foreignTable="ROLE">
	<reference local="ROLEID" foreign="ROLEID"/>
    </foreign-key>
</table>
			*/


			num_merged = 0;
			System.out.println("Merging >PERSONROLE");
			
			crit = new Criteria();
			crit.add(PersonrolePeer.PERSONID, source.getId());
			obj_itr = PersonrolePeer.doSelect(crit).iterator();
			while (obj_itr.hasNext())
			{
				Personrole existing_obj = (Personrole)obj_itr.next();
				RoleBean role = RoleBean.getRole(existing_obj.getRoleid());
				if (!destination.hasRole(role))
				{
					destination.addRole(role);
					num_merged++;
				}
			}
			
			System.out.println("NUM MERGED >" + num_merged);

			
			
			
			/*
<table name="CUSTOMER_RET_DB" idMethod="native">
    <column name="CUSTOMER_RET_DB_ID" primaryKey="true" required="true" type="INTEGER" autoIncrement="true"/>
    <column name="COMPANY_ID" required="true" type="INTEGER"/>
    <column name="REQUEST_I_D" required="true" type="VARCHAR" size="40"/>

    <column name="LIST_I_D" required="false" type="VARCHAR" size="25"/>
    <column name="QB_LIST_I_D" required="false" type="VARCHAR" size="25"/>
    <column name="EDIT_SEQUENCE" required="false" type="VARCHAR" size="15"/>
    <column name="TIME_MODIFIED" required="true" type="DATE"/>
	
    <column name="ACCOUNT_BALANCE" required="true" scale="2" size="7" type="DECIMAL"/>
	
    <column name="FIRST_NAME" required="false" type="VARCHAR" size="30"/>
    <column name="FULL_NAME" required="true" type="VARCHAR" size="60"/>
    <column name="LAST_NAME" required="true" type="VARCHAR" size="40"/>
	
    <column name="LAST_SALE" required="false" type="DATE"/>
    <column name="PHONE" required="false" type="VARCHAR" size="21"/>
    <column name="PHONE2" required="false" type="VARCHAR" size="21"/>
    <column name="E_MAIL" required="false" type="VARCHAR" size="99"/>
    <column name="IS_ACTIVE" required="false" type="VARCHAR" size="5"/>

    <column name="PRICE_LEVEL_NUMBER" required="false" type="SMALLINT" />
    
    <foreign-key foreignTable="COMPANY">
	<reference local="COMPANY_ID" foreign="COMPANYID"/>
    </foreign-key>
</table>
			*/

			
			
			// merge this only if destination has none

			crit = new Criteria();
			crit.add(CustomerRetDbPeer.QB_LIST_I_D, destination_QB_List_ID);
			obj_list = CustomerRetDbPeer.doSelect(crit);
			if (obj_list.size() == 0)
			{
				// no customer ret found with the destination qb list id

				if (source_QB_List_ID != null)
				{
					num_merged = 0;
					System.out.println("Merging >CUSTOMER_RET_DB");

					crit = new Criteria();
					crit.add(CustomerRetDbPeer.QB_LIST_I_D, source_QB_List_ID);
					obj_itr = CustomerRetDbPeer.doSelect(crit).iterator();
					while (obj_itr.hasNext())
					{
						CustomerRetDb existing_obj = (CustomerRetDb)obj_itr.next();
						existing_obj.setQbListID(destination_QB_List_ID);
						existing_obj.save();

						num_merged++;
					}

					System.out.println("NUM MERGED >" + num_merged);
				}

				if (num_merged == 0 && source_PoS_List_ID != null && !source_PoS_List_ID.equals(""))
				{
					num_merged = 0;
					System.out.println("Merging >CUSTOMER_RET_DB");

					crit = new Criteria();
					crit.add(CustomerRetDbPeer.LIST_I_D, source_PoS_List_ID);
					obj_itr = CustomerRetDbPeer.doSelect(crit).iterator();
					while (obj_itr.hasNext())
					{
						CustomerRetDb existing_obj = (CustomerRetDb)obj_itr.next();
						existing_obj.setQbListID(destination_QB_List_ID);
						existing_obj.save();

						num_merged++;
					}

					System.out.println("NUM MERGED >" + num_merged);
				}
			}
			
			

			source.setIsDeleted(true);
			source.save();
			
			
			
			/*
xxx
			*/

			
			
			
			/*
xxx
			*/

			
			
			
			/*
xxx
			*/

			
			
			
			/*
xxx
			*/

			
			
			
			
		}
		catch (Exception x)
		{
			x.printStackTrace();
		}
    }

}
