/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.valeo.qb;

import com.badiyan.torque.PurchaseOrderItemMapping;
import com.badiyan.torque.QbRequestDb;
import com.badiyan.torque.QbRequestDbPeer;
import com.badiyan.uk.beans.*;
import com.badiyan.uk.exceptions.ObjectNotFoundException;
import com.badiyan.uk.online.beans.CheckoutCodeBean;
import com.badiyan.uk.online.beans.ProductWaitList;
import com.badiyan.uk.online.beans.PurchaseOrder;
import com.generationjava.io.WritingException;
import com.valeo.qb.data.AccountRet;
import com.valeo.qbpos.QBPOSXMLRequest;
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;
import org.apache.torque.TorqueException;
import org.apache.torque.util.Criteria;

/**
 *
 * @author marlo
 */
public class QBXMLPurchaseOrderAddRequest
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

			qb_request = QBXMLPurchaseOrderAddRequest.getRequest((QbRequestDb)objList.get(0));
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
			qb_request = new QBXMLPurchaseOrderAddRequest(_request);
			hash.put(key, qb_request);
		}

		return qb_request;
    }
	
	// INSTANCE VARIABLES

	private PurchaseOrder purchaseOrder;
	
	// CONSTRUCTORS

    public
    QBXMLPurchaseOrderAddRequest() {
		qb_request = new QbRequestDb();
		isNew = true;
    }

    public
    QBXMLPurchaseOrderAddRequest(QbRequestDb _request) {
		qb_request = _request;
		isNew = false;
    }

	// INSTANCE METHODS

	public void
	setPurchaseOrder(PurchaseOrder _purchaseOrder) {
		purchaseOrder = _purchaseOrder;
	}

	@Override
	protected void assembleRequestBody() throws WritingException {

		/*
		 *
	<?xml version="1.0" encoding="UTF-8"?>
	<PurchaseOrderAddRq>
	   <PurchaseOrderAdd defMacro="MACROTYPE">
		  <!-- required -->
		  <VendorRef>
			 <!-- optional -->
			 <ListID>IDTYPE</ListID>
			 <!-- optional -->
			 <FullName>STRTYPE</FullName>
			 <!-- optional -->
		  </VendorRef>
		  <ClassRef>
			 <!-- optional -->
			 <ListID>IDTYPE</ListID>
			 <!-- optional -->
			 <FullName>STRTYPE</FullName>
			 <!-- optional -->
		  </ClassRef>
		  <!-- BEGIN OR -->
		  <InventorySiteRef>
			 <!-- optional -->
			 <ListID>IDTYPE</ListID>
			 <!-- optional -->
			 <FullName>STRTYPE</FullName>
			 <!-- optional -->
		  </InventorySiteRef>
		  <!-- OR -->
		  <ShipToEntityRef>
			 <!-- optional -->
			 <ListID>IDTYPE</ListID>
			 <!-- optional -->
			 <FullName>STRTYPE</FullName>
			 <!-- optional -->
		  </ShipToEntityRef>
		  <!-- END OR -->
		  <TemplateRef>
			 <!-- optional -->
			 <ListID>IDTYPE</ListID>
			 <!-- optional -->
			 <FullName>STRTYPE</FullName>
			 <!-- optional -->
		  </TemplateRef>
		  <TxnDate>DATETYPE</TxnDate>
		  <!-- optional -->
		  <RefNumber>STRTYPE</RefNumber>
		  <!-- optional -->
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
		  <ShipAddress>
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
		  </ShipAddress>
		  <TermsRef>
			 <!-- optional -->
			 <ListID>IDTYPE</ListID>
			 <!-- optional -->
			 <FullName>STRTYPE</FullName>
			 <!-- optional -->
		  </TermsRef>
		  <DueDate>DATETYPE</DueDate>
		  <!-- optional -->
		  <ExpectedDate>DATETYPE</ExpectedDate>
		  <!-- optional -->
		  <ShipMethodRef>
			 <!-- optional -->
			 <ListID>IDTYPE</ListID>
			 <!-- optional -->
			 <FullName>STRTYPE</FullName>
			 <!-- optional -->
		  </ShipMethodRef>
		  <FOB>STRTYPE</FOB>
		  <!-- optional -->
		  <Memo>STRTYPE</Memo>
		  <!-- optional -->
		  <VendorMsg>STRTYPE</VendorMsg>
		  <!-- optional -->
		  <IsToBePrinted>BOOLTYPE</IsToBePrinted>
		  <!-- optional -->
		  <IsToBeEmailed>BOOLTYPE</IsToBeEmailed>
		  <!-- optional -->
		  <Other1>STRTYPE</Other1>
		  <!-- optional -->
		  <Other2>STRTYPE</Other2>
		  <!-- optional -->
		  <ExchangeRate>FLOATTYPE</ExchangeRate>
		  <!-- optional -->
		  <ExternalGUID>GUIDTYPE</ExternalGUID>
		  <!-- optional -->
		  <!-- BEGIN OR -->
		  <PurchaseOrderLineAdd defMacro="MACROTYPE">
			 <!-- optional -->
			 <ItemRef>
				<!-- optional -->
				<ListID>IDTYPE</ListID>
				<!-- optional -->
				<FullName>STRTYPE</FullName>
				<!-- optional -->
			 </ItemRef>
			 <ManufacturerPartNumber>STRTYPE</ManufacturerPartNumber>
			 <!-- optional -->
			 <Desc>STRTYPE</Desc>
			 <!-- optional -->
			 <Quantity>QUANTYPE</Quantity>
			 <!-- optional -->
			 <UnitOfMeasure>STRTYPE</UnitOfMeasure>
			 <!-- optional -->
			 <Rate>PRICETYPE</Rate>
			 <!-- optional -->
			 <ClassRef>
				<!-- optional -->
				<ListID>IDTYPE</ListID>
				<!-- optional -->
				<FullName>STRTYPE</FullName>
				<!-- optional -->
			 </ClassRef>
			 <Amount>AMTTYPE</Amount>
			 <!-- optional -->
			 <InventorySiteLocationRef>
				<!-- optional -->
				<ListID>IDTYPE</ListID>
				<!-- optional -->
				<FullName>STRTYPE</FullName>
				<!-- optional -->
			 </InventorySiteLocationRef>
			 <CustomerRef>
				<!-- optional -->
				<ListID>IDTYPE</ListID>
				<!-- optional -->
				<FullName>STRTYPE</FullName>
				<!-- optional -->
			 </CustomerRef>
			 <ServiceDate>DATETYPE</ServiceDate>
			 <!-- optional -->
			 <OverrideItemAccountRef>
				<!-- optional -->
				<ListID>IDTYPE</ListID>
				<!-- optional -->
				<FullName>STRTYPE</FullName>
				<!-- optional -->
			 </OverrideItemAccountRef>
			 <Other1>STRTYPE</Other1>
			 <!-- optional -->
			 <Other2>STRTYPE</Other2>
			 <!-- optional -->
			 <DataExt>
				<!-- optional, may repeat -->
				<OwnerID>GUIDTYPE</OwnerID>
				<!-- required -->
				<DataExtName>STRTYPE</DataExtName>
				<!-- required -->
				<DataExtValue>STRTYPE</DataExtValue>
				<!-- required -->
			 </DataExt>
		  </PurchaseOrderLineAdd>
		  <!-- OR -->
		  <PurchaseOrderLineGroupAdd>
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
		  </PurchaseOrderLineGroupAdd>
		  <!-- END OR -->
	   </PurchaseOrderAdd>
	   <IncludeRetElement>STRTYPE</IncludeRetElement>
	   <!-- optional, may repeat -->
	</PurchaseOrderAddRq>
		 *
		 */

		try
		{
			//PurchaseOrder purchaseOrder = this.itemReceipt.getPurchaseOrder();

			xmlwriter.writeEntity("PurchaseOrderAddRq");
			xmlwriter.writeAttribute("requestID", getRequestId());

			xmlwriter.writeEntity("PurchaseOrderAdd");

			xmlwriter.writeEntity("VendorRef");
			xmlwriter.writeEntity("ListID");
			xmlwriter.writeText(purchaseOrder.getVendor().getListID());
			xmlwriter.endEntity();
			xmlwriter.endEntity(); // VendorRef

			xmlwriter.writeEntity("TxnDate");
			xmlwriter.writeText(dateTypeFormat.format(purchaseOrder.getPurchaseOrderDate()));
			xmlwriter.endEntity();

			xmlwriter.writeEntity("RefNumber");
			xmlwriter.writeText("" + purchaseOrder.getPurchaseOrderNumber(CompanyBean.getCompany(5)));
			xmlwriter.endEntity();

			if (!purchaseOrder.getNoteString().equals("")) {
				xmlwriter.writeEntity("Memo");
				xmlwriter.writeText(purchaseOrder.getNoteString());
				xmlwriter.endEntity();
			}
		
			this.setRefNumber(purchaseOrder.getValue());

			/*
			if (!purchaseOrder.getNoteString().equals("")) {
				xmlwriter.writeEntity("Memo");
				//xmlwriter.writeText(purchaseOrder.getNoteString());
				xmlwriter.writeText("PO #" + purchaseOrder.getPurchaseOrderNumber(purchaseOrder.getCompany()) + " Voucher #" + this.itemReceipt.getValue() + ".  Posted on " + CUBean.getUserDateString(this.itemReceipt.getTimeCreated()));
				xmlwriter.endEntity();
			}
			*/

			// add expenses like shipping and discount

			// for now track tax as shipping, which is lame, but what Valeo does...

			/*
			float shipping_and_tax = this.itemReceipt.getShippingAmount() + this.itemReceipt.getTaxAmount();

			if (shipping_and_tax > 0f)
			{
				xmlwriter.writeEntity("ExpenseLineAdd");

				xmlwriter.writeEntity("AccountRef");
				xmlwriter.writeEntity("ListID");
				xmlwriter.writeText("80000044-1326768337"); // this needs to be changed to be dynamic, somehow... - Shipping &amp; Handling
				xmlwriter.endEntity();
				xmlwriter.endEntity();

				BigDecimal shipping_and_tax_bd = new BigDecimal(shipping_and_tax);
				xmlwriter.writeEntity("Amount");
				xmlwriter.writeText(shipping_and_tax_bd.setScale(2, BigDecimal.ROUND_HALF_UP).toString());
				xmlwriter.endEntity();

				xmlwriter.endEntity(); // ExpenseLineAdd
			}

			float discount = this.itemReceipt.getDiscountAmount();

			if (discount > 0f)
			{
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
			Iterator itr = purchaseOrder.getItems().iterator();
			while (itr.hasNext()) {
				//ItemLineRet item = (ItemLineRet)itr.next();
				PurchaseOrderItemMapping obj = (PurchaseOrderItemMapping)itr.next();
				CheckoutCodeBean checkout_code = CheckoutCodeBean.getCheckoutCode(obj.getCheckoutCodeId());
				if (obj.getQuantity().floatValue() > 0f) {
					
					xmlwriter.writeEntity("PurchaseOrderLineAdd");

					xmlwriter.writeEntity("ItemRef");
					xmlwriter.writeEntity("ListID");
					xmlwriter.writeText(checkout_code.getQBListID());
					xmlwriter.endEntity();
					xmlwriter.endEntity(); // ItemRef

					xmlwriter.writeEntity("Quantity");
					xmlwriter.writeText(obj.getQuantity().setScale(2, BigDecimal.ROUND_HALF_UP).toString());
					xmlwriter.endEntity();

					xmlwriter.writeEntity("Rate");
					xmlwriter.writeText(obj.getRate().setScale(2, BigDecimal.ROUND_HALF_UP).toString());
					xmlwriter.endEntity();
					
					if (obj.getProductWaitListDbId() > 0) {
						
						try {
							ProductWaitList wait_entry = ProductWaitList.getProductWaitListEntry(obj.getProductWaitListDbId());

							xmlwriter.writeEntity("CustomerRef");
							xmlwriter.writeEntity("ListID");
							xmlwriter.writeText(wait_entry.getWaitingPerson().getQBFSListID());
							xmlwriter.endEntity();
							xmlwriter.endEntity(); // CustomerRef
						} catch (Exception x) {
							x.printStackTrace();
							CUBean.sendEmail("marlo@badiyan.com", "admin@valeowc.com", x.getMessage(), "Trouble with ProductWaitList in QBXMLPurchaseOrderAddRequest");
						}
					}

					xmlwriter.endEntity(); // PurchaseOrderLineAdd
				}
			}

			xmlwriter.endEntity(); // </PurchaseOrderAdd>

			xmlwriter.endEntity(); // </PurchaseOrderAddRq>
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
		return "ItemReceiptAddRq";
	}

}
