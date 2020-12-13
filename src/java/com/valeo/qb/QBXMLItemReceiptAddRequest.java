/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.valeo.qb;

import com.badiyan.torque.QbRequestDb;
import com.badiyan.torque.QbRequestDbPeer;
import com.badiyan.uk.beans.*;
import com.badiyan.uk.exceptions.IllegalValueException;
import com.badiyan.uk.exceptions.ObjectAlreadyExistsException;
import com.badiyan.uk.exceptions.ObjectNotFoundException;
import com.badiyan.uk.online.beans.PurchaseOrder;
import com.generationjava.io.WritingException;
import com.valeo.qb.data.AccountRet;
import com.valeo.qb.data.ItemLineRet;
import com.valeo.qb.data.ItemReceiptRet;
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
public class QBXMLItemReceiptAddRequest
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

			qb_request = QBXMLItemReceiptAddRequest.getRequest((QbRequestDb)objList.get(0));
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
			qb_request = new QBXMLItemReceiptAddRequest(_request);
			hash.put(key, qb_request);
		}

		return qb_request;
    }
	
	// INSTANCE VARIABLES

	private ItemReceiptRet itemReceipt;
	
	// CONSTRUCTORS

    public
    QBXMLItemReceiptAddRequest()
    {
		qb_request = new QbRequestDb();
		isNew = true;
    }

    public
    QBXMLItemReceiptAddRequest(QbRequestDb _request)
    {
		qb_request = _request;
		isNew = false;
    }

	// INSTANCE METHODS

	public void
	setItemReceipt(ItemReceiptRet _itemReceipt)
	{
		itemReceipt = _itemReceipt;
	}

	@Override
	protected void assembleRequestBody() throws WritingException {

		/*
		 *
	<ItemReceiptAddRq requestID = "UUIDTYPE">               <!-- not in QBOE, v4.0 -->
      <!-- ItemReceiptAdd contains 1 optional attribute: 'defMacro' -->
      <ItemReceiptAdd defMacro = "MACROTYPE">
        <VendorRef>
          <ListID>IDTYPE</ListID>                           <!-- opt -->
          <FullName>STRTYPE</FullName>                      <!-- opt, max length = 41 for QBD|QBCA|QBUK|QBAU -->
        </VendorRef>
        <APAccountRef>                                      <!-- opt -->
          <ListID>IDTYPE</ListID>                           <!-- opt -->
          <FullName>STRTYPE</FullName>                      <!-- opt, max length = 159 for QBD|QBCA|QBUK|QBAU -->
        </APAccountRef>
        <TxnDate>DATETYPE</TxnDate>                         <!-- opt -->
        <RefNumber>STRTYPE</RefNumber>                      <!-- opt, max length = 20 for QBD|QBCA|QBUK|QBAU -->
        <Memo>STRTYPE</Memo>                                <!-- opt, max length = 4095 for QBD|QBCA|QBUK|QBAU -->
        <IsTaxIncluded>BOOLTYPE</IsTaxIncluded>             <!-- opt, not in QBD, v8.0 -->
        <SalesTaxCodeRef>                                   <!-- opt, not in QBD, v8.0 -->
          <ListID>IDTYPE</ListID>                           <!-- opt -->
          <FullName>STRTYPE</FullName>                      <!-- opt, max length = 3 for QBCA|QBUK, max length = 6 for QBAU -->
        </SalesTaxCodeRef>
        <ExchangeRate>FLOATTYPE</ExchangeRate>              <!-- opt, v8.0 -->
        <LinkToTxnID>IDTYPE</LinkToTxnID>                   <!-- opt, may rep -->
        <!-- ExpenseLineAdd contains 1 optional attribute: 'defMacro' -->
        <ExpenseLineAdd defMacro = "MACROTYPE">             <!-- opt, may rep -->
          <AccountRef>                                      <!-- opt -->
            <ListID>IDTYPE</ListID>                         <!-- opt -->
            <FullName>STRTYPE</FullName>                    <!-- opt, max length = 159 for QBD|QBCA|QBUK|QBAU -->
          </AccountRef>
          <Amount>AMTTYPE</Amount>                          <!-- opt -->
          <TaxAmount>AMTTYPE</TaxAmount>                    <!-- opt, not in QBD|QBCA|QBUK, v6.1 -->
          <Memo>STRTYPE</Memo>                              <!-- opt, max length = 4095 for QBD|QBCA|QBUK|QBAU -->
          <CustomerRef>                                     <!-- opt -->
            <ListID>IDTYPE</ListID>                         <!-- opt -->
            <FullName>STRTYPE</FullName>                    <!-- opt, max length = 209 for QBD|QBCA|QBUK|QBAU -->
          </CustomerRef>
          <ClassRef>                                        <!-- opt -->
            <ListID>IDTYPE</ListID>                         <!-- opt -->
            <FullName>STRTYPE</FullName>                    <!-- opt, max length = 159 for QBD|QBCA|QBUK|QBAU -->
          </ClassRef>
          <SalesTaxCodeRef>                                 <!-- opt, not in QBD, v6.0 -->
            <ListID>IDTYPE</ListID>                         <!-- opt -->
            <FullName>STRTYPE</FullName>                    <!-- opt, max length = 3 for QBCA|QBUK, max length = 6 for QBAU -->
          </SalesTaxCodeRef>
          <!-- BillableStatus may have one of the following values: Billable, NotBillable, HasBeenBilled -->
          <BillableStatus>ENUMTYPE</BillableStatus>         <!-- opt, v2.0 -->
        </ExpenseLineAdd>
        <!-- BEGIN OR: You may have 0 or more ItemLineAdd OR ItemGroupLineAdd -->
        <ItemLineAdd>
          <ItemRef>                                         <!-- opt -->
            <ListID>IDTYPE</ListID>                         <!-- opt -->
            <FullName>STRTYPE</FullName>                    <!-- opt -->
          </ItemRef>
          <Desc>STRTYPE</Desc>                              <!-- opt, max length = 4095 for QBD|QBCA|QBUK|QBAU -->
          <Quantity>QUANTYPE</Quantity>                     <!-- opt -->
          <UnitOfMeasure>STRTYPE</UnitOfMeasure>            <!-- opt, max length = 31 for QBD|QBCA|QBUK|QBAU, v7.0 -->
          <Cost>PRICETYPE</Cost>                            <!-- opt -->
          <Amount>AMTTYPE</Amount>                          <!-- opt -->
          <TaxAmount>AMTTYPE</TaxAmount>                    <!-- opt, not in QBD|QBCA|QBUK, v6.1 -->
          <CustomerRef>                                     <!-- opt -->
            <ListID>IDTYPE</ListID>                         <!-- opt -->
            <FullName>STRTYPE</FullName>                    <!-- opt, max length = 209 for QBD|QBCA|QBUK|QBAU -->
          </CustomerRef>
          <ClassRef>                                        <!-- opt -->
            <ListID>IDTYPE</ListID>                         <!-- opt -->
            <FullName>STRTYPE</FullName>                    <!-- opt, max length = 159 for QBD|QBCA|QBUK|QBAU -->
          </ClassRef>
          <SalesTaxCodeRef>                                 <!-- opt, not in QBD, v6.0 -->
            <ListID>IDTYPE</ListID>                         <!-- opt -->
            <FullName>STRTYPE</FullName>                    <!-- opt, max length = 3 for QBCA|QBUK, max length = 6 for QBAU -->
          </SalesTaxCodeRef>
          <!-- BillableStatus may have one of the following values: Billable, NotBillable, HasBeenBilled -->
          <BillableStatus>ENUMTYPE</BillableStatus>         <!-- opt, v2.0 -->
          <OverrideItemAccountRef>                          <!-- opt, v2.0 -->
            <ListID>IDTYPE</ListID>                         <!-- opt -->
            <FullName>STRTYPE</FullName>                    <!-- opt, max length = 159 for QBD|QBCA|QBUK|QBAU -->
          </OverrideItemAccountRef>
          <LinkToTxn>                                       <!-- opt, v4.0 -->
            <TxnID>IDTYPE</TxnID>
            <TxnLineID>IDTYPE</TxnLineID>
          </LinkToTxn>
        </ItemLineAdd>
        <!-- OR -->
        <ItemGroupLineAdd>
          <ItemGroupRef>
            <ListID>IDTYPE</ListID>                         <!-- opt -->
            <FullName>STRTYPE</FullName>                    <!-- opt, max length = 31 for QBD|QBCA|QBUK|QBAU -->
          </ItemGroupRef>
          <Desc>STRTYPE</Desc>                              <!-- opt, not in QBD|QBCA|QBUK|QBAU -->
          <Quantity>QUANTYPE</Quantity>                     <!-- opt -->
          <UnitOfMeasure>STRTYPE</UnitOfMeasure>            <!-- opt, max length = 31 for QBD|QBCA|QBUK|QBAU, v7.0 -->
        </ItemGroupLineAdd>
        <!-- END OR -->
      </ItemReceiptAdd>
      <IncludeRetElement>STRTYPE</IncludeRetElement>        <!-- opt, may rep, max length = 50 for QBD|QBCA|QBUK|QBAU -->
    </ItemReceiptAddRq>
		 *
		 */

		try
		{
			PurchaseOrder purchaseOrder = this.itemReceipt.getPurchaseOrder();

			xmlwriter.writeEntity("ItemReceiptAddRq");
			xmlwriter.writeAttribute("requestID", getRequestId());

			xmlwriter.writeEntity("ItemReceiptAdd");

			xmlwriter.writeEntity("VendorRef");
			xmlwriter.writeEntity("ListID");
			xmlwriter.writeText(purchaseOrder.getVendor().getListID());
			xmlwriter.endEntity();
			xmlwriter.endEntity(); // VendorRef

			xmlwriter.writeEntity("TxnDate");
			xmlwriter.writeText(dateTypeFormat.format(purchaseOrder.getPurchaseOrderDate()));
			xmlwriter.endEntity();

			xmlwriter.writeEntity("RefNumber");
			xmlwriter.writeText(this.itemReceipt.getValue());
			xmlwriter.endEntity();
		
			this.setRefNumber(this.itemReceipt.getValue());

			if (!purchaseOrder.getNoteString().equals(""))
			{
				xmlwriter.writeEntity("Memo");
				//xmlwriter.writeText(purchaseOrder.getNoteString());
				xmlwriter.writeText("PO #" + purchaseOrder.getPurchaseOrderNumber(purchaseOrder.getCompany()) + " Voucher #" + this.itemReceipt.getValue() + ".  Posted on " + CUBean.getUserDateString(this.itemReceipt.getTimeCreated()));
				xmlwriter.endEntity();
			}

			// add expenses like shipping and discount

			// for now track tax as shipping, which is lame, but what Valeo does...

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

				BigDecimal discount_bd = new BigDecimal(discount * -1f); // appears this needs to be a negative number
				xmlwriter.writeEntity("Amount");
				xmlwriter.writeText(discount_bd.setScale(2, BigDecimal.ROUND_HALF_UP).toString());
				xmlwriter.endEntity();

				xmlwriter.endEntity(); // ExpenseLineAdd
			}

			Iterator itr = this.itemReceipt.getItemLines().iterator();
			while (itr.hasNext())
			{
				ItemLineRet item = (ItemLineRet)itr.next();
				if (item.getQuantity().floatValue() > 0f)
				{
					xmlwriter.writeEntity("ItemLineAdd");

					xmlwriter.writeEntity("ItemRef");
					xmlwriter.writeEntity("ListID");
					xmlwriter.writeText(item.getCheckoutCode().getQBListID());
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
						if (item.getCheckoutCode().getCOGSAccountId() > 0)
						{
							AccountRet account_obj = AccountRet.getAccount(item.getCheckoutCode().getCOGSAccountId());

						/*
						 * 
			  <OverrideItemAccountRef>                          <!-- opt, v2.0 -->
				<ListID>IDTYPE</ListID>                         <!-- opt -->
				<FullName>STRTYPE</FullName>                    <!-- opt, max length = 159 for QBD|QBCA|QBUK|QBAU -->
			  </OverrideItemAccountRef>
						 */


							xmlwriter.writeEntity("OverrideItemAccountRef");
							xmlwriter.writeEntity("ListID");
							xmlwriter.writeText(account_obj.getListID());
							xmlwriter.endEntity();
							xmlwriter.writeEntity("FullName");
							xmlwriter.writeText(account_obj.getFullName());
							xmlwriter.endEntity();
							xmlwriter.endEntity(); // OverrideItemAccountRef
						}
					}
					catch (Exception x)
					{
						x.printStackTrace();
						CUBean.sendEmail("marlo@badiyan.com", "admin@valeowc.com", x.getMessage(), "Trouble with COGS account in QBXMLItemReceiptAddRequest");
					}

					xmlwriter.endEntity(); // ItemLineAdd
				}
			}

			xmlwriter.endEntity(); // </ItemReceiptAdd>

			xmlwriter.endEntity(); // </ItemReceiptAddRq>
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
