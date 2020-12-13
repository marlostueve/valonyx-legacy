/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.valeo.qb;

import com.badiyan.torque.QbRequestDb;
import com.badiyan.torque.QbRequestDbPeer;
import com.badiyan.uk.beans.*;
import com.badiyan.uk.exceptions.ObjectNotFoundException;
import com.badiyan.uk.online.beans.CheckoutCodeBean;
import com.badiyan.uk.online.beans.PurchaseOrder;
import com.generationjava.io.WritingException;
import com.valeo.qb.data.AccountRet;
import com.valeo.qb.data.ItemLineRet;
import com.valeo.qb.data.ItemReceiptRet;
import com.valeo.qbpos.QBPOSXMLRequest;
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import org.apache.torque.TorqueException;
import org.apache.torque.util.Criteria;

/**
 *
 * @author marlo
 */
public class QBXMLBillAddRequest
	extends QBXMLRequest {
	
    // CLASS METHODS

    public static QBPOSXMLRequest
    getRequest(int _id)
		throws TorqueException, ObjectNotFoundException
    {
		Integer key = new Integer(_id);
		//QB_REQUEST_DB
		QBPOSXMLRequest qb_request = (QBPOSXMLRequest)hash.get(key);
		if (qb_request == null)
		{
			Criteria crit = new Criteria();
			crit.add(QbRequestDbPeer.QB_REQUEST_DB_ID, _id);
			List objList = QbRequestDbPeer.doSelect(crit);
			if (objList.size() == 0)
				throw new ObjectNotFoundException("Could not locate QB Request with id: " + _id);

			qb_request = QBXMLBillAddRequest.getRequest((QbRequestDb)objList.get(0));
		}

		return qb_request;
    }

    private static QBPOSXMLRequest
    getRequest(QbRequestDb _request)
		throws TorqueException
    {
		Integer key = new Integer(_request.getQbRequestDbId());
		QBPOSXMLRequest qb_request = (QBPOSXMLRequest)hash.get(key);
		if (qb_request == null)
		{
			qb_request = new QBXMLBillAddRequest(_request);
			hash.put(key, qb_request);
		}

		return qb_request;
    }
	
	// INSTANCE VARIABLES

	private PurchaseOrder purchaseOrder;
	private Vector itemReceiptVec;
	
	// CONSTRUCTORS

    public
    QBXMLBillAddRequest()
    {
		qb_request = new QbRequestDb();
		isNew = true;
    }

    public
    QBXMLBillAddRequest(QbRequestDb _request)
    {
		qb_request = _request;
		isNew = false;
    }

	// INSTANCE METHODS

	public void
	setPurchaseOrder(PurchaseOrder _purchaseOrder) {
		purchaseOrder = _purchaseOrder;
	}

	public void
	setItemReceipts(Vector _vec) {
		itemReceiptVec = _vec;
	}

	@Override
	protected void assembleRequestBody() throws WritingException {

		/*
		 *
<?xml version="1.0" encoding="UTF-8"?>
<BillAddRq>
   <BillAdd defMacro="MACROTYPE">
      <!-- required -->
      <VendorRef>
         <!-- required -->
         <ListID>IDTYPE</ListID>
         <!-- optional -->
         <FullName>STRTYPE</FullName>
         <!-- optional -->
      </VendorRef>
      <VendorAddress>
         <!-- optional -->
         <Addr1>STRTYPE</Addr1>
         <!-- optional -->
         <Addr2>STRTYPE</Addr2>
         <!-- optional -->
         <Addr3>STRTYPE</Addr3>
         <!-- optional -->
         <Addr4>STRTYPE</Addr4>
         <!-- optional -->
         <Addr5>STRTYPE</Addr5>
         <!-- optional -->
         <City>STRTYPE</City>
         <!-- optional -->
         <State>STRTYPE</State>
         <!-- optional -->
         <PostalCode>STRTYPE</PostalCode>
         <!-- optional -->
         <Country>STRTYPE</Country>
         <!-- optional -->
         <Note>STRTYPE</Note>
         <!-- optional -->
      </VendorAddress>
      <APAccountRef>
         <!-- optional -->
         <ListID>IDTYPE</ListID>
         <!-- optional -->
         <FullName>STRTYPE</FullName>
         <!-- optional -->
      </APAccountRef>
      <TxnDate>DATETYPE</TxnDate>
      <!-- optional -->
      <DueDate>DATETYPE</DueDate>
      <!-- optional -->
      <RefNumber>STRTYPE</RefNumber>
      <!-- optional -->
      <TermsRef>
         <!-- optional -->
         <ListID>IDTYPE</ListID>
         <!-- optional -->
         <FullName>STRTYPE</FullName>
         <!-- optional -->
      </TermsRef>
      <Memo>STRTYPE</Memo>
      <!-- optional -->
      <ExchangeRate>FLOATTYPE</ExchangeRate>
      <!-- optional -->
      <ExternalGUID>GUIDTYPE</ExternalGUID>
      <!-- optional -->
      <LinkToTxnID>IDTYPE</LinkToTxnID>
      <!-- optional, may repeat -->
      <ExpenseLineAdd defMacro="MACROTYPE">
         <!-- optional, may repeat -->
         <AccountRef>
            <!-- optional -->
            <ListID>IDTYPE</ListID>
            <!-- optional -->
            <FullName>STRTYPE</FullName>
            <!-- optional -->
         </AccountRef>
         <Amount>AMTTYPE</Amount>
         <!-- optional -->
         <Memo>STRTYPE</Memo>
         <!-- optional -->
         <CustomerRef>
            <!-- optional -->
            <ListID>IDTYPE</ListID>
            <!-- optional -->
            <FullName>STRTYPE</FullName>
            <!-- optional -->
         </CustomerRef>
         <ClassRef>
            <!-- optional -->
            <ListID>IDTYPE</ListID>
            <!-- optional -->
            <FullName>STRTYPE</FullName>
            <!-- optional -->
         </ClassRef>
         <!-- BillableStatus may have one of the following values: Billable, NotBillable, HasBeenBilled -->
         <BillableStatus>ENUMTYPE</BillableStatus>
         <!-- optional -->
         <SalesRepRef>
            <!-- optional -->
            <ListID>IDTYPE</ListID>
            <!-- optional -->
            <FullName>STRTYPE</FullName>
            <!-- optional -->
         </SalesRepRef>
         <DataExt>
            <!-- optional, may repeat -->
            <OwnerID>GUIDTYPE</OwnerID>
            <!-- required -->
            <DataExtName>STRTYPE</DataExtName>
            <!-- required -->
            <DataExtValue>STRTYPE</DataExtValue>
            <!-- required -->
         </DataExt>
      </ExpenseLineAdd>
      <!-- BEGIN OR -->
      <ItemLineAdd>
         <!-- optional -->
         <ItemRef>
            <!-- optional -->
            <ListID>IDTYPE</ListID>
            <!-- optional -->
            <FullName>STRTYPE</FullName>
            <!-- optional -->
         </ItemRef>
         <InventorySiteRef>
            <!-- optional -->
            <ListID>IDTYPE</ListID>
            <!-- optional -->
            <FullName>STRTYPE</FullName>
            <!-- optional -->
         </InventorySiteRef>
         <InventorySiteLocationRef>
            <!-- optional -->
            <ListID>IDTYPE</ListID>
            <!-- optional -->
            <FullName>STRTYPE</FullName>
            <!-- optional -->
         </InventorySiteLocationRef>
         <!-- BEGIN OR -->
         <SerialNumber>STRTYPE</SerialNumber>
         <!-- optional -->
         <!-- OR -->
         <LotNumber>STRTYPE</LotNumber>
         <!-- optional -->
         <!-- END OR -->
         <Desc>STRTYPE</Desc>
         <!-- optional -->
         <Quantity>QUANTYPE</Quantity>
         <!-- optional -->
         <UnitOfMeasure>STRTYPE</UnitOfMeasure>
         <!-- optional -->
         <Cost>PRICETYPE</Cost>
         <!-- optional -->
         <Amount>AMTTYPE</Amount>
         <!-- optional -->
         <CustomerRef>
            <!-- optional -->
            <ListID>IDTYPE</ListID>
            <!-- optional -->
            <FullName>STRTYPE</FullName>
            <!-- optional -->
         </CustomerRef>
         <ClassRef>
            <!-- optional -->
            <ListID>IDTYPE</ListID>
            <!-- optional -->
            <FullName>STRTYPE</FullName>
            <!-- optional -->
         </ClassRef>
         <!-- BillableStatus may have one of the following values: Billable, NotBillable, HasBeenBilled -->
         <BillableStatus>ENUMTYPE</BillableStatus>
         <!-- optional -->
         <OverrideItemAccountRef>
            <!-- optional -->
            <ListID>IDTYPE</ListID>
            <!-- optional -->
            <FullName>STRTYPE</FullName>
            <!-- optional -->
         </OverrideItemAccountRef>
         <LinkToTxn>
            <!-- optional -->
            <TxnID>IDTYPE</TxnID>
            <!-- required -->
            <TxnLineID>IDTYPE</TxnLineID>
            <!-- required -->
         </LinkToTxn>
         <SalesRepRef>
            <!-- optional -->
            <ListID>IDTYPE</ListID>
            <!-- optional -->
            <FullName>STRTYPE</FullName>
            <!-- optional -->
         </SalesRepRef>
         <DataExt>
            <!-- optional, may repeat -->
            <OwnerID>GUIDTYPE</OwnerID>
            <!-- required -->
            <DataExtName>STRTYPE</DataExtName>
            <!-- required -->
            <DataExtValue>STRTYPE</DataExtValue>
            <!-- required -->
         </DataExt>
      </ItemLineAdd>
      <!-- OR -->
      <ItemGroupLineAdd>
         <!-- optional -->
         <ItemGroupRef>
            <!-- required -->
            <ListID>IDTYPE</ListID>
            <!-- optional -->
            <FullName>STRTYPE</FullName>
            <!-- optional -->
         </ItemGroupRef>
         <Quantity>QUANTYPE</Quantity>
         <!-- optional -->
         <UnitOfMeasure>STRTYPE</UnitOfMeasure>
         <!-- optional -->
         <InventorySiteRef>
            <!-- optional -->
            <ListID>IDTYPE</ListID>
            <!-- optional -->
            <FullName>STRTYPE</FullName>
            <!-- optional -->
         </InventorySiteRef>
         <InventorySiteLocationRef>
            <!-- optional -->
            <ListID>IDTYPE</ListID>
            <!-- optional -->
            <FullName>STRTYPE</FullName>
            <!-- optional -->
         </InventorySiteLocationRef>
         <DataExt>
            <!-- optional, may repeat -->
            <OwnerID>GUIDTYPE</OwnerID>
            <!-- required -->
            <DataExtName>STRTYPE</DataExtName>
            <!-- required -->
            <DataExtValue>STRTYPE</DataExtValue>
            <!-- required -->
         </DataExt>
      </ItemGroupLineAdd>
      <!-- END OR -->
   </BillAdd>
   <IncludeRetElement>STRTYPE</IncludeRetElement>
   <!-- optional, may repeat -->
</BillAddRq>
		 *
		 */

		try {

			xmlwriter.writeEntity("BillAddRq");
			xmlwriter.writeAttribute("requestID", getRequestId());

			xmlwriter.writeEntity("BillAdd");

			xmlwriter.writeEntity("VendorRef");
			xmlwriter.writeEntity("ListID");
			xmlwriter.writeText(purchaseOrder.getVendor().getListID());
			xmlwriter.endEntity();
			xmlwriter.endEntity(); // VendorRef
			
			/*
			      <APAccountRef>
         <!-- optional -->
         <ListID>IDTYPE</ListID>
         <!-- optional -->
         <FullName>STRTYPE</FullName>
         <!-- optional -->
      </APAccountRef>
				 -- */
			/*
			xmlwriter.writeEntity("APAccountRef");
			xmlwriter.writeEntity("ListID");
			xmlwriter.writeText(purchaseOrder.getVendor().getListID());
			xmlwriter.endEntity();
			xmlwriter.endEntity(); // APAccountRef
			*/
			
			/* apparently it defaults to this??
			<APAccountRef>
<ListID>80000030-1318646790</ListID>
<FullName>Accounts Payable</FullName>
</APAccountRef>
			*/

			xmlwriter.writeEntity("TxnDate");
			xmlwriter.writeText(dateTypeFormat.format(purchaseOrder.getPurchaseOrderDate()));
			xmlwriter.endEntity();

			xmlwriter.writeEntity("RefNumber");
			xmlwriter.writeText("" + purchaseOrder.getPurchaseOrderNumber(CompanyBean.getCompany(5)));
			xmlwriter.endEntity();
		
			this.setRefNumber(purchaseOrder.getValue());

			if (!purchaseOrder.getNoteString().equals("")) {
				xmlwriter.writeEntity("Memo");
				//xmlwriter.writeText(purchaseOrder.getNoteString());
				xmlwriter.writeText("PO #" + purchaseOrder.getPurchaseOrderNumber(purchaseOrder.getCompany()) + " Voucher #" + purchaseOrder.getValue() + ".  Posted on " + CUBean.getUserDateString(purchaseOrder.getPurchaseOrderDate()));
				xmlwriter.endEntity();
			}

			// add expenses like shipping and discount

			// for now track tax as shipping, which is lame, but what Valeo does...
			
			Vector allReceivedItems = new Vector();
			
			float total_tax = 0f;
			float total_shipping = 0f;
			float total_discount = 0f;
			
			if (itemReceiptVec != null) {
				
				Iterator itr = itemReceiptVec.iterator();
				while (itr.hasNext()) {
					ItemReceiptRet item_receipt = (ItemReceiptRet)itr.next();
					total_tax += item_receipt.getTaxAmount();
					total_shipping += item_receipt.getShippingAmount();
					total_discount += item_receipt.getDiscountAmount();
					allReceivedItems.addAll(item_receipt.getItemLines());
				}
			}

			/* grab from item receipt rather than purchase order, I guess
			BigDecimal shipping_and_tax_bd = purchaseOrder.getShipping().add(purchaseOrder.getTax());
			shipping_and_tax_bd = shipping_and_tax_bd.subtract(purchaseOrder.getDiscount());
			*/
			
			BigDecimal total_shipping_bd = new BigDecimal(total_shipping);
			BigDecimal total_tax_bd = new BigDecimal(total_tax);
			BigDecimal total_discount_bd = new BigDecimal(total_discount);
			
			BigDecimal shipping_and_tax_bd = total_shipping_bd.add(total_tax_bd).subtract(total_discount_bd);
			System.out.println("shipping_and_tax_bd nn >" + shipping_and_tax_bd);
			if (shipping_and_tax_bd.compareTo(BigDecimal.ZERO) != 0) {

				xmlwriter.writeEntity("ExpenseLineAdd");

				xmlwriter.writeEntity("AccountRef");
				xmlwriter.writeEntity("ListID");
				xmlwriter.writeText("80000044-1326768337"); // this needs to be changed to be dynamic, somehow... - Shipping &amp; Handling
				xmlwriter.endEntity();
				xmlwriter.endEntity();

				xmlwriter.writeEntity("Amount");
				xmlwriter.writeText(shipping_and_tax_bd.setScale(2, BigDecimal.ROUND_HALF_UP).toString());
				xmlwriter.endEntity();

				xmlwriter.endEntity(); // ExpenseLineAdd
			}
			

			/*
			float discount = this.itemReceipt.getDiscountAmount();
			if (discount > 0f) {
				xmlwriter.writeEntity("ExpenseLineAdd");

				xmlwriter.writeEntity("AccountRef");
				xmlwriter.writeEntity("ListID");
				xmlwriter.writeText("80000043-1326592878"); // this needs to be changed to be dynamic, somehow... Vendor Discount - CostOfGoodsSold account type?!?!
				xmlwriter.endEntity();
				xmlwriter.endEntity();

				BigDecimal discount_bd = new BigDecimal(discount);
				xmlwriter.writeEntity("Amount");
				xmlwriter.writeText(discount_bd.setScale(2, BigDecimal.ROUND_HALF_UP).toString());
				xmlwriter.endEntity();

				xmlwriter.endEntity(); // ExpenseLineAdd
			}
			*/

			//Iterator itr = this.itemReceipt.getItemLines().iterator();
			//Iterator itr = purchaseOrder.getItems().iterator();
			Iterator itr = allReceivedItems.iterator();
			while (itr.hasNext())
			{
				ItemLineRet item = (ItemLineRet)itr.next();
				//PurchaseOrderItemMapping obj = (PurchaseOrderItemMapping)itr.next();
				//CheckoutCodeBean checkout_code = CheckoutCodeBean.getCheckoutCode(obj.getCheckoutCodeId());
				CheckoutCodeBean checkout_code = item.getCheckoutCode();
				if (item.getQuantity().floatValue() > 0f)
				{
					xmlwriter.writeEntity("ItemLineAdd");

					xmlwriter.writeEntity("ItemRef");
					xmlwriter.writeEntity("ListID");
					xmlwriter.writeText(checkout_code.getQBListID());
					xmlwriter.endEntity();
					xmlwriter.endEntity(); // ItemRef

					xmlwriter.writeEntity("Quantity");
					xmlwriter.writeText(item.getQuantity().setScale(2, BigDecimal.ROUND_HALF_UP).toString());
					xmlwriter.endEntity();

					xmlwriter.writeEntity("Cost");
					xmlwriter.writeText(item.getCost().setScale(2, BigDecimal.ROUND_HALF_UP).toString());
					xmlwriter.endEntity();
					
					
					try
					{
						if (checkout_code.getCOGSAccountId() > 0)
						{
							AccountRet account_obj = AccountRet.getAccount(checkout_code.getCOGSAccountId());

						/*
						 * 
			  <OverrideItemAccountRef>                          <!-- opt, v2.0 -->
				<ListID>IDTYPE</ListID>                         <!-- opt -->
				<FullName>STRTYPE</FullName>                    <!-- opt, max length = 159 for QBD|QBCA|QBUK|QBAU -->
			  </OverrideItemAccountRef>
						 */


						/* removed 12/27/18 - thinking this needs to default to AccountsPayable account... 
							xmlwriter.writeEntity("OverrideItemAccountRef");
							xmlwriter.writeEntity("ListID");
							xmlwriter.writeText(account_obj.getListID());
							xmlwriter.endEntity();
							xmlwriter.writeEntity("FullName");
							xmlwriter.writeText(account_obj.getFullName());
							xmlwriter.endEntity();
							xmlwriter.endEntity(); // OverrideItemAccountRef
						*/

						}
					}
					catch (Exception x)
					{
						x.printStackTrace();
						CUBean.sendEmail("marlo@badiyan.com", "admin@valeowc.com", x.getMessage(), "Trouble with COGS account in QBXMLBillAddRequest");
					}

					xmlwriter.endEntity(); // ItemLineAdd
				}
			}

			xmlwriter.endEntity(); // </BillAdd>

			xmlwriter.endEntity(); // </BillAddRq>
		}
		catch (Exception x)
		{
			x.printStackTrace();
		}
	}

	@Override
	public String getLabel() {
		return CUBean.getUserTimeString(super.creation_date) + " - Item Receipt Add Request";
	}

	@Override
	public String getRequestType() {
		return "BillAddRq";
	}

}
