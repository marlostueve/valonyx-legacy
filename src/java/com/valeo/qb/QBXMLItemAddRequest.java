/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.valeo.qb;

import com.badiyan.torque.QbRequestDb;
import com.badiyan.torque.QbRequestDbPeer;
import com.badiyan.uk.beans.*;
import com.badiyan.uk.exceptions.IllegalValueException;
import com.badiyan.uk.exceptions.ObjectNotFoundException;
import com.badiyan.uk.online.beans.CheckoutCodeBean;
import com.generationjava.io.WritingException;
import com.valeo.qb.data.AccountRet;
import com.valeo.qb.data.ItemPaymentRet;
import com.valeo.qb.data.ItemRet;
import com.valeo.qb.data.ItemSalesTaxRet;
import com.valeo.qb.data.PaymentMethodRet;
import com.valeo.qb.data.SalesTaxCodeRet;
import com.valeo.qb.data.VendorRet;
import com.valeo.qbpos.QBPOSXMLRequest;
import java.math.RoundingMode;
import java.util.List;
import org.apache.torque.TorqueException;
import org.apache.torque.util.Criteria;

/**
 *
 * @author marlo
 */
public class QBXMLItemAddRequest
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

			qb_request = QBXMLItemAddRequest.getRequest((QbRequestDb)objList.get(0));
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
			qb_request = new QBXMLItemAddRequest(_request);
			hash.put(key, qb_request);
		}

		return qb_request;
    }
	
	// INSTANCE VARIABLES

	private CheckoutCodeBean item;
	private String rq_str = "ItemNonInventoryAddRq";
	
	private ItemPaymentRet item_payment;
	private ItemSalesTaxRet sales_tax;
	
	// CONSTRUCTORS

    public
    QBXMLItemAddRequest()
    {
		qb_request = new QbRequestDb();
		isNew = true;
    }

    public
    QBXMLItemAddRequest(QbRequestDb _request)
    {
		qb_request = _request;
		isNew = false;
    }

	// INSTANCE METHODS

	public void
	setCheckoutCode(CheckoutCodeBean _item)
	{
		item = _item;
	}

	public void
	setItemPaymentRet(ItemPaymentRet _item_payment)
	{
		item_payment = _item_payment;
	}

	public void
	setItemSalesTaxRet(ItemSalesTaxRet _sales_tax_for_add_request)
	{
		sales_tax = _sales_tax_for_add_request;
	}

	@Override
	protected void assembleRequestBody() throws WritingException {

		/*
		 *     <!-- ItemNonInventoryAddRq contains 1 optional attribute: 'requestID' -->
    <ItemNonInventoryAddRq requestID = "UUIDTYPE">          <!-- not in QBOE -->
      <ItemNonInventoryAdd>
        <Name>STRTYPE</Name>                                <!-- max length = 31 for QBD|QBCA|QBUK|QBAU -->
        <IsActive>BOOLTYPE</IsActive>                       <!-- opt -->
        <ParentRef>                                         <!-- opt -->
          <ListID>IDTYPE</ListID>                           <!-- opt -->
          <FullName>STRTYPE</FullName>                      <!-- opt -->
        </ParentRef>
        <ManufacturerPartNumber>STRTYPE</ManufacturerPartNumber> <!-- opt, max length = 31 for QBD|QBCA|QBUK|QBAU, v7.0 -->
        <UnitOfMeasureSetRef>                               <!-- opt, v7.0 -->
          <ListID>IDTYPE</ListID>                           <!-- opt -->
          <FullName>STRTYPE</FullName>                      <!-- opt, max length = 31 for QBD|QBCA|QBUK|QBAU -->
        </UnitOfMeasureSetRef>
        <IsTaxIncluded>BOOLTYPE</IsTaxIncluded>             <!-- opt, not in QBD, v6.0 -->
        <SalesTaxCodeRef>                                   <!-- opt -->
          <ListID>IDTYPE</ListID>                           <!-- opt -->
          <FullName>STRTYPE</FullName>                      <!-- opt, max length = 3 for QBD|QBCA|QBUK, max length = 6 for QBAU -->
        </SalesTaxCodeRef>
        <!-- BEGIN OR: You may have SalesOrPurchase OR SalesAndPurchase -->
        <SalesOrPurchase>
          <Desc>STRTYPE</Desc>                              <!-- opt, max length = 4095 for QBD|QBCA|QBUK|QBAU -->
          <!-- BEGIN OR: You may optionally have Price OR PricePercent -->
          <Price>PRICETYPE</Price>
          <!-- OR -->
          <PricePercent>PERCENTTYPE</PricePercent>
          <!-- END OR -->
          <AccountRef>                                      <!-- opt -->
            <ListID>IDTYPE</ListID>                         <!-- opt -->
            <FullName>STRTYPE</FullName>                    <!-- opt, max length = 159 for QBD|QBCA|QBUK|QBAU -->
          </AccountRef>
        </SalesOrPurchase>
        <!-- OR -->
        <SalesAndPurchase>
          <SalesDesc>STRTYPE</SalesDesc>                    <!-- opt, max length = 4095 for QBD|QBCA|QBUK|QBAU -->
          <SalesPrice>PRICETYPE</SalesPrice>                <!-- opt -->
          <IncomeAccountRef>                                <!-- opt -->
            <ListID>IDTYPE</ListID>                         <!-- opt -->
            <FullName>STRTYPE</FullName>                    <!-- opt, max length = 159 for QBD|QBCA|QBUK|QBAU -->
          </IncomeAccountRef>
          <PurchaseDesc>STRTYPE</PurchaseDesc>              <!-- opt, max length = 4095 for QBD|QBCA|QBUK|QBAU -->
          <PurchaseCost>PRICETYPE</PurchaseCost>            <!-- opt -->
          <PurchaseTaxCodeRef>                              <!-- opt, not in QBD, v6.0 -->
            <ListID>IDTYPE</ListID>                         <!-- opt -->
            <FullName>STRTYPE</FullName>                    <!-- opt, max length = 3 for QBCA|QBUK, max length = 6 for QBAU -->
          </PurchaseTaxCodeRef>
          <ExpenseAccountRef>                               <!-- opt -->
            <ListID>IDTYPE</ListID>                         <!-- opt -->
            <FullName>STRTYPE</FullName>                    <!-- opt, max length = 159 for QBD|QBCA|QBUK|QBAU -->
          </ExpenseAccountRef>
          <PrefVendorRef>                                   <!-- opt -->
            <ListID>IDTYPE</ListID>                         <!-- opt -->
            <FullName>STRTYPE</FullName>                    <!-- opt, max length = 41 for QBD|QBCA|QBUK|QBAU -->
          </PrefVendorRef>
        </SalesAndPurchase>
        <!-- END OR -->
        <ExternalGUID>GUIDTYPE</ExternalGUID>               <!-- opt, v8.0 -->
      </ItemNonInventoryAdd>
      <IncludeRetElement>STRTYPE</IncludeRetElement>        <!-- opt, may rep, max length = 50 for QBD|QBCA|QBUK|QBAU, v4.0 -->
    </ItemNonInventoryAddRq>
		 *
		 */

		
		
		/*
		 *         <!-- ItemServiceAddRq contains 1 optional attribute: 'requestID' -->
    <ItemServiceAddRq requestID = "UUIDTYPE">
      <ItemServiceAdd>
        <Name>STRTYPE</Name>                                <!-- max length = 31 for QBD|QBCA|QBUK|QBAU, max length = 100 for QBOE -->
        <IsActive>BOOLTYPE</IsActive>                       <!-- opt, not in QBOE -->
        <ParentRef>                                         <!-- opt -->
          <ListID>IDTYPE</ListID>                           <!-- opt -->
          <FullName>STRTYPE</FullName>                      <!-- opt -->
        </ParentRef>
        <UnitOfMeasureSetRef>                               <!-- opt, not in QBOE, v7.0 -->
          <ListID>IDTYPE</ListID>                           <!-- opt -->
          <FullName>STRTYPE</FullName>                      <!-- opt, max length = 31 for QBD|QBCA|QBUK|QBAU -->
        </UnitOfMeasureSetRef>
        <IsTaxIncluded>BOOLTYPE</IsTaxIncluded>             <!-- opt, not in QBD|QBOE, v6.0 -->
        <SalesTaxCodeRef>                                   <!-- opt, not in QBOE -->
          <ListID>IDTYPE</ListID>                           <!-- opt -->
          <FullName>STRTYPE</FullName>                      <!-- opt, max length = 3 for QBD|QBCA|QBUK, max length = 6 for QBAU -->
        </SalesTaxCodeRef>
        <!-- BEGIN OR: You may have SalesOrPurchase OR SalesAndPurchase -->
        <SalesOrPurchase>
          <Desc>STRTYPE</Desc>                              <!-- opt, max length = 4095 for QBD|QBCA|QBUK|QBAU, max length = 4000 for QBOE -->
          <!-- BEGIN OR: You may optionally have Price OR PricePercent -->
          <Price>PRICETYPE</Price>
          <!-- OR -->
          <PricePercent>PERCENTTYPE</PricePercent>
          <!-- END OR -->
          <AccountRef>                                      <!-- opt -->
            <ListID>IDTYPE</ListID>                         <!-- opt -->
            <FullName>STRTYPE</FullName>                    <!-- opt, max length = 159 for QBD|QBCA|QBUK|QBAU, max length = 1000 for QBOE -->
          </AccountRef>
        </SalesOrPurchase>
        <!-- OR -->
        <SalesAndPurchase>                                  <!-- not in QBOE -->
          <SalesDesc>STRTYPE</SalesDesc>                    <!-- opt, max length = 4095 for QBD|QBCA|QBUK|QBAU -->
          <SalesPrice>PRICETYPE</SalesPrice>                <!-- opt -->
          <IncomeAccountRef>                                <!-- opt -->
            <ListID>IDTYPE</ListID>                         <!-- opt -->
            <FullName>STRTYPE</FullName>                    <!-- opt, max length = 159 for QBD|QBCA|QBUK|QBAU -->
          </IncomeAccountRef>
          <PurchaseDesc>STRTYPE</PurchaseDesc>              <!-- opt, max length = 4095 for QBD|QBCA|QBUK|QBAU -->
          <PurchaseCost>PRICETYPE</PurchaseCost>            <!-- opt -->
          <PurchaseTaxCodeRef>                              <!-- opt, not in QBD, v6.0 -->
            <ListID>IDTYPE</ListID>                         <!-- opt -->
            <FullName>STRTYPE</FullName>                    <!-- opt, max length = 3 for QBCA|QBUK, max length = 6 for QBAU -->
          </PurchaseTaxCodeRef>
          <ExpenseAccountRef>                               <!-- opt -->
            <ListID>IDTYPE</ListID>                         <!-- opt -->
            <FullName>STRTYPE</FullName>                    <!-- opt, max length = 159 for QBD|QBCA|QBUK|QBAU -->
          </ExpenseAccountRef>
          <PrefVendorRef>                                   <!-- opt -->
            <ListID>IDTYPE</ListID>                         <!-- opt -->
            <FullName>STRTYPE</FullName>                    <!-- opt, max length = 41 for QBD|QBCA|QBUK|QBAU -->
          </PrefVendorRef>
        </SalesAndPurchase>
        <!-- END OR -->
        <ExternalGUID>GUIDTYPE</ExternalGUID>               <!-- opt, not in QBOE, v8.0 -->
      </ItemServiceAdd>
      <IncludeRetElement>STRTYPE</IncludeRetElement>        <!-- opt, may rep, max length = 50 for QBD|QBCA|QBUK|QBAU, not in QBOE, v4.0 -->
    </ItemServiceAddRq>
		 *
		 */
		
		String requestId = this.getRequestId();
		
		short itemType = (short)-1;
		try {
			if (item != null) {
				itemType = item.getQBItemType();
			}
		} catch (IllegalValueException x) {
			x.printStackTrace();
		}
		
		if (sales_tax != null)
		{
			/*
			 * 

    <!-- ItemSalesTaxAddRq contains 1 optional attribute: 'requestID' -->
    <ItemSalesTaxAddRq requestID = "UUIDTYPE">              <!-- not in QBOE -->
      <ItemSalesTaxAdd>
        <Name>STRTYPE</Name>                                <!-- max length = 31 for QBD|QBCA|QBUK|QBAU -->
        <IsActive>BOOLTYPE</IsActive>                       <!-- opt -->
        <ItemDesc>STRTYPE</ItemDesc>                        <!-- opt, max length = 4095 for QBD|QBCA|QBUK|QBAU -->
        <TaxRate>PERCENTTYPE</TaxRate>                      <!-- opt -->
        <TaxVendorRef>                                      <!-- opt -->
          <ListID>IDTYPE</ListID>                           <!-- opt -->
          <FullName>STRTYPE</FullName>                      <!-- opt, max length = 41 for QBD|QBCA|QBUK|QBAU -->
        </TaxVendorRef>
        <SalesTaxReturnLineRef>                             <!-- opt, not in QBD, v8.0 -->
          <ListID>IDTYPE</ListID>                           <!-- opt -->
          <FullName>STRTYPE</FullName>                      <!-- opt, max length = 41 for QBCA|QBUK|QBAU -->
        </SalesTaxReturnLineRef>
        <ExternalGUID>GUIDTYPE</ExternalGUID>               <!-- opt, v8.0 -->
      </ItemSalesTaxAdd>
      <IncludeRetElement>STRTYPE</IncludeRetElement>        <!-- opt, may rep, max length = 50 for QBD|QBCA|QBUK|QBAU, v4.0 -->
    </ItemSalesTaxAddRq>
			 * 
			 */
			
		
			try
			{
				sales_tax.setRequestID(requestId);
				sales_tax.save();
			}
			catch (Exception x)
			{
				x.printStackTrace();
			}
			
			xmlwriter.writeEntity("ItemSalesTaxAddRq");
			xmlwriter.writeAttribute("requestID", requestId);

			xmlwriter.writeEntity("ItemSalesTaxAdd");

			xmlwriter.writeEntity("Name");
			xmlwriter.writeText(sales_tax.getLabel());
			xmlwriter.endEntity();

			xmlwriter.writeEntity("IsActive");
			xmlwriter.writeText("1");
			xmlwriter.endEntity();

			xmlwriter.writeEntity("ItemDesc");
			xmlwriter.writeText(sales_tax.getItemDesc());
			xmlwriter.endEntity();

			xmlwriter.writeEntity("TaxRate");
			xmlwriter.writeText(sales_tax.getTaxRate().setScale(3, RoundingMode.HALF_UP).toString());
			xmlwriter.endEntity();

			try
			{
				VendorRet tax_vendor = VendorRet.getVendor(sales_tax.getTaxVendorRefDbId());
				
				xmlwriter.writeEntity("TaxVendorRef");
				xmlwriter.writeEntity("ListID");
				xmlwriter.writeText(tax_vendor.getListID());
				xmlwriter.endEntity();
				xmlwriter.endEntity();
			}
			catch (Exception x)
			{
				x.printStackTrace();
			}
			
			xmlwriter.endEntity(); // ItemSalesTaxAdd
			
			xmlwriter.endEntity(); // ItemSalesTaxAddRq
			
		}
		else if (item_payment != null)
		{
			/*
			 * 
    <!-- ItemPaymentAddRq contains 1 optional attribute: 'requestID' -->
    <ItemPaymentAddRq requestID = "UUIDTYPE">               <!-- not in QBOE -->
      <ItemPaymentAdd>
        <Name>STRTYPE</Name>                                <!-- max length = 31 for QBD|QBCA|QBUK|QBAU -->
        <IsActive>BOOLTYPE</IsActive>                       <!-- opt -->
        <ItemDesc>STRTYPE</ItemDesc>                        <!-- opt, max length = 4095 for QBD|QBCA|QBUK|QBAU -->
        <DepositToAccountRef>                               <!-- opt -->
          <ListID>IDTYPE</ListID>                           <!-- opt -->
          <FullName>STRTYPE</FullName>                      <!-- opt, max length = 159 for QBD|QBCA|QBUK|QBAU -->
        </DepositToAccountRef>
        <PaymentMethodRef>                                  <!-- opt -->
          <ListID>IDTYPE</ListID>                           <!-- opt -->
          <FullName>STRTYPE</FullName>                      <!-- opt, max length = 31 for QBD|QBCA|QBUK|QBAU -->
        </PaymentMethodRef>
        <ExternalGUID>GUIDTYPE</ExternalGUID>               <!-- opt, v8.0 -->
      </ItemPaymentAdd>
      <IncludeRetElement>STRTYPE</IncludeRetElement>        <!-- opt, may rep, max length = 50 for QBD|QBCA|QBUK|QBAU, v4.0 -->
    </ItemPaymentAddRq>
			 * 
			 */
		
			try
			{
				item_payment.setRequestID(requestId);
				item_payment.save();
			}
			catch (Exception x)
			{
				x.printStackTrace();
			}
			
			xmlwriter.writeEntity("ItemPaymentAddRq");
			xmlwriter.writeAttribute("requestID", requestId);

			xmlwriter.writeEntity("ItemPaymentAdd");

			xmlwriter.writeEntity("Name");
			xmlwriter.writeText(item_payment.getLabel());
			xmlwriter.endEntity();

			xmlwriter.writeEntity("IsActive");
			xmlwriter.writeText("1");
			xmlwriter.endEntity();

			xmlwriter.writeEntity("ItemDesc");
			xmlwriter.writeText(item_payment.getLabel());
			xmlwriter.endEntity();
			
			try
			{
				int deposit_to_account_id = item_payment.getDepositToAccountId();
				if (deposit_to_account_id > 0)
				{
					AccountRet deposit_to_account = AccountRet.getAccount(deposit_to_account_id);

					xmlwriter.writeEntity("DepositToAccountRef");
					xmlwriter.writeEntity("ListID");
					xmlwriter.writeText(deposit_to_account.getListID());
					xmlwriter.endEntity(); // </ListID>
					xmlwriter.endEntity(); // </IncomeAccountRef>
				}
			}
			catch (Exception x)
			{
				x.printStackTrace();
			}
			
			try
			{
				int payment_method_id = item_payment.getPaymentMethodId();
				if (payment_method_id > 0)
				{
					PaymentMethodRet payment_method_ref = PaymentMethodRet.getPaymentMethod(iteration);

					xmlwriter.writeEntity("PaymentMethodRef");
					xmlwriter.writeEntity("ListID");
					xmlwriter.writeText(payment_method_ref.getListID());
					xmlwriter.endEntity(); // </ListID>
					xmlwriter.endEntity(); // </IncomeAccountRef>
				}
			}
			catch (Exception x)
			{
				x.printStackTrace();
			}
			
			
			xmlwriter.endEntity(); // ItemPaymentAdd
			
			xmlwriter.endEntity(); // ItemPaymentAddRq
		}
		else if (itemType == ItemRet.SUBTOTAL_TYPE) {
			
			// handling this here rather than below because it's a simpler case.  too lazy to shoe horn it in below where it probably should be...  1/7/12
			
		
		/*
    <ItemSubtotalAddRq requestID = "UUIDTYPE">              <!-- not in QBOE -->
      <ItemSubtotalAdd>
        <Name>STRTYPE</Name>                                <!-- max length = 31 for QBD|QBCA|QBUK|QBAU -->
        <IsActive>BOOLTYPE</IsActive>                       <!-- opt -->
        <ItemDesc>STRTYPE</ItemDesc>                        <!-- opt, max length = 4095 for QBD|QBCA|QBUK|QBAU -->
        <ExternalGUID>GUIDTYPE</ExternalGUID>               <!-- opt, v8.0 -->
      </ItemSubtotalAdd>
      <IncludeRetElement>STRTYPE</IncludeRetElement>        <!-- opt, may rep, max length = 50 for QBD|QBCA|QBUK|QBAU, v4.0 -->
    </ItemSubtotalAddRq>
		 */
			
			System.out.println("Subtotal type found >");
		
			try
			{
				item_payment.setRequestID(requestId);
				item_payment.save();
			}
			catch (Exception x)
			{
				x.printStackTrace();
			}
			
			xmlwriter.writeEntity("ItemSubtotalAddRq");
			xmlwriter.writeAttribute("requestID", requestId);

			xmlwriter.writeEntity("ItemSubtotalAdd");

			xmlwriter.writeEntity("Name");
			xmlwriter.writeText(item.getLabel());
			xmlwriter.endEntity();

			xmlwriter.writeEntity("IsActive");
			xmlwriter.writeText("1");
			xmlwriter.endEntity();

			xmlwriter.writeEntity("ItemDesc");
			xmlwriter.writeText(item.getSalesDescriptionString());
			xmlwriter.endEntity();
			
			xmlwriter.endEntity(); // ItemSubtotalAdd
			
			xmlwriter.endEntity(); // ItemSubtotalAddRq
			
		}
		else
		{
		
			try
			{
				item.setRequestID(requestId);
				item.save();
			}
			catch (Exception x)
			{
				x.printStackTrace();
			}

			String add_str = "ItemNonInventoryAdd";

			try
			{
				System.out.println("item.getQBItemType() >" + item.getQBItemType());

				switch (item.getQBItemType())
				{
					case ItemRet.NON_INVENTORY_TYPE: rq_str = "ItemNonInventoryAddRq"; add_str = "ItemNonInventoryAdd"; break;
					case ItemRet.INVENTORY_TYPE: rq_str = "ItemInventoryAddRq"; add_str = "ItemInventoryAdd"; break;
					case ItemRet.SERVICE_TYPE: rq_str = "ItemServiceAddRq"; add_str = "ItemServiceAdd"; break;
					case ItemRet.GROUP_TYPE: rq_str = "ItemGroupAddRq"; add_str = "ItemGroupAdd"; break;
					case ItemRet.SALES_TAX_TYPE: rq_str = "ItemSalesTaxAddRq"; add_str = "ItemSalesTaxAdd"; break;
					case ItemRet.SALES_TAX_GROUP_TYPE: rq_str = "ItemSalesTaxGroupAddRq"; add_str = "ItemSalesTaxGroupAdd"; break;
					case ItemRet.DISCOUNT_TYPE: rq_str = "ItemDiscountAddRq"; add_str = "ItemDiscountAdd"; break;
					case ItemRet.FIXED_ASSET_TYPE: rq_str = "ItemFixedAssetAddRq"; add_str = "ItemFixedAssetAdd"; break;
					case ItemRet.INVENTORY_ASSEMBLY_TYPE: rq_str = "ItemInventoryAssemblyAddRq"; add_str = "ItemInventoryAssemblyAdd"; break;
					case ItemRet.OTHER_CHARGE_TYPE: rq_str = "ItemOtherChargeAddRq"; add_str = "ItemOtherChargeAdd"; break;
					case ItemRet.PAYMENT_TYPE: rq_str = "ItemPaymentAddRq"; add_str = "ItemPaymentAdd"; break;
					case ItemRet.SUBTOTAL_TYPE: rq_str = "ItemSubtotalAddRq"; add_str = "ItemSubtotalAdd"; break; // thise should be resolved above...
				}
			}
			catch (IllegalValueException x)
			{
				x.printStackTrace();
			}

			System.out.println("rq_str >" + rq_str);
			System.out.println("add_str >" + add_str);

			xmlwriter.writeEntity(rq_str);
			xmlwriter.writeAttribute("requestID", requestId);

			xmlwriter.writeEntity(add_str);

			xmlwriter.writeEntity("Name");
			xmlwriter.writeText(item.getLabel());
			xmlwriter.endEntity();

			xmlwriter.writeEntity("IsActive");
			xmlwriter.writeText("1");
			xmlwriter.endEntity();

			try
			{
				SalesTaxCodeRet sales_tax_code = SalesTaxCodeRet.getSalesTaxCode(item.getSalesTaxCodeId());

				xmlwriter.writeEntity("SalesTaxCodeRef");
				xmlwriter.writeEntity("ListID");
				xmlwriter.writeText(sales_tax_code.getListID());
				xmlwriter.endEntity(); // </ListID>
				xmlwriter.endEntity(); // </SalesTaxCodeRef>
			}
			catch (Exception x)
			{
				x.printStackTrace();
			}

			/*
			<SalesAndPurchase>
			  <SalesDesc>STRTYPE</SalesDesc>                    <!-- opt, max length = 4095 for QBD|QBCA|QBUK|QBAU -->
			  <SalesPrice>PRICETYPE</SalesPrice>                <!-- opt -->
			  <IncomeAccountRef>                                <!-- opt -->
				<ListID>IDTYPE</ListID>                         <!-- opt -->
				<FullName>STRTYPE</FullName>                    <!-- opt, max length = 159 for QBD|QBCA|QBUK|QBAU -->
			  </IncomeAccountRef>
			  <PurchaseDesc>STRTYPE</PurchaseDesc>              <!-- opt, max length = 4095 for QBD|QBCA|QBUK|QBAU -->
			  <PurchaseCost>PRICETYPE</PurchaseCost>            <!-- opt -->
			  <PurchaseTaxCodeRef>                              <!-- opt, not in QBD, v6.0 -->
				<ListID>IDTYPE</ListID>                         <!-- opt -->
				<FullName>STRTYPE</FullName>                    <!-- opt, max length = 3 for QBCA|QBUK, max length = 6 for QBAU -->
			  </PurchaseTaxCodeRef>
			  <ExpenseAccountRef>                               <!-- opt -->
				<ListID>IDTYPE</ListID>                         <!-- opt -->
				<FullName>STRTYPE</FullName>                    <!-- opt, max length = 159 for QBD|QBCA|QBUK|QBAU -->
			  </ExpenseAccountRef>
			  <PrefVendorRef>                                   <!-- opt -->
				<ListID>IDTYPE</ListID>                         <!-- opt -->
				<FullName>STRTYPE</FullName>                    <!-- opt, max length = 41 for QBD|QBCA|QBUK|QBAU -->
			  </PrefVendorRef>
			</SalesAndPurchase>
			*/

			if (add_str.equals("ItemNonInventoryAdd") || add_str.equals("ItemServiceAdd"))
				xmlwriter.writeEntity("SalesAndPurchase");

			xmlwriter.writeEntity("SalesDesc");
			xmlwriter.writeText(item.getSalesDescriptionString());
			xmlwriter.endEntity(); // </SalesDesc>

			xmlwriter.writeEntity("SalesPrice");
			xmlwriter.writeText(item.getAmountString());
			xmlwriter.endEntity(); // </SalesPrice>

			try
			{
				int income_account_id = item.getIncomeAccountId();
				if (income_account_id > 0)
				{
					AccountRet income_account = AccountRet.getAccount(income_account_id);

					xmlwriter.writeEntity("IncomeAccountRef");
					xmlwriter.writeEntity("ListID");
					xmlwriter.writeText(income_account.getListID());
					xmlwriter.endEntity(); // </ListID>
					xmlwriter.endEntity(); // </IncomeAccountRef>
				}
			}
			catch (Exception x)
			{
				x.printStackTrace();
			}

			xmlwriter.writeEntity("PurchaseDesc");
			xmlwriter.writeText(item.getLabel());
			xmlwriter.endEntity(); // </SalesDesc>

			xmlwriter.writeEntity("PurchaseCost");
			xmlwriter.writeText(item.getOrderCostString());
			xmlwriter.endEntity(); // </SalesPrice>

			if (add_str.equals("ItemNonInventoryAdd") || add_str.equals("ItemServiceAdd"))
			{
				try
				{
					int expense_account_id = item.getExpenseAccountId();
					if (expense_account_id > 0)
					{
						AccountRet expense_account = AccountRet.getAccount(expense_account_id);

						xmlwriter.writeEntity("ExpenseAccountRef");
						xmlwriter.writeEntity("ListID");
						xmlwriter.writeText(expense_account.getListID());
						xmlwriter.endEntity(); // </ListID>
						xmlwriter.endEntity(); // </IncomeAccountRef>
					}
				}
				catch (Exception x)
				{
					x.printStackTrace();
				}
			}

			if (add_str.equals("ItemInventoryAdd"))
			{
				try
				{
					int cogs_account_id = item.getCOGSAccountId();
					if (cogs_account_id > 0)
					{
						AccountRet cogs_account = AccountRet.getAccount(cogs_account_id);

						xmlwriter.writeEntity("COGSAccountRef");
						xmlwriter.writeEntity("ListID");
						xmlwriter.writeText(cogs_account.getListID());
						xmlwriter.endEntity(); // </ListID>
						xmlwriter.endEntity(); // </IncomeAccountRef>
					}
				}
				catch (Exception x)
				{
					x.printStackTrace();
				}
			}

			try
			{
				int vendor_id = item.getVendorId();
				if (vendor_id > 0)
				{
					VendorRet vendor = VendorRet.getVendor(vendor_id);

					if (vendor.getListID() != null) {
						xmlwriter.writeEntity("PrefVendorRef");
						xmlwriter.writeEntity("ListID");
						xmlwriter.writeText(vendor.getListID());
						xmlwriter.endEntity(); // </ListID>
						xmlwriter.endEntity(); // </PrefVendorRef>
					}
				}
			}
			catch (Exception x)
			{
				x.printStackTrace();
			}

			if (add_str.equals("ItemInventoryAdd"))
			{
				try
				{
					int asset_account_id = item.getAssetAccountId();
					if (asset_account_id > 0)
					{
						AccountRet asset_account = AccountRet.getAccount(asset_account_id);

						xmlwriter.writeEntity("AssetAccountRef");
						xmlwriter.writeEntity("ListID");
						xmlwriter.writeText(asset_account.getListID());
						xmlwriter.endEntity(); // </ListID>
						xmlwriter.endEntity(); // </AssetAccountRef>
					}
				}
				catch (Exception x)
				{
					x.printStackTrace();
				}
			}


			if (add_str.equals("ItemNonInventoryAdd") || add_str.equals("ItemServiceAdd"))
				xmlwriter.endEntity(); // </SalesAndPurchase>





			/*
			xmlwriter.writeEntity("FirstName");
			xmlwriter.writeText(vendor.getNameString());
			xmlwriter.endEntity();
			xmlwriter.writeEntity("LastName");
			xmlwriter.writeText(vendor.getNameString());
			xmlwriter.endEntity();
			 */



			xmlwriter.endEntity(); // </ItemNonInventoryAdd>

			xmlwriter.endEntity(); // </ItemNonInventoryAddRq>
		
		}
	}

	@Override
	public String getLabel() {
		if (item != null)
			return CUBean.getUserTimeString(super.creation_date) + " - Item Add Request (" + item.getLabel() + ")";
		else if (item_payment != null)
			return CUBean.getUserTimeString(super.creation_date) + " - Item Payment Add Request (" + item_payment.getLabel() + ")";
		else if (this.sales_tax != null)
			return CUBean.getUserTimeString(super.creation_date) + " - Item Sales Tax Add Request (" + sales_tax.getLabel() + ")";
		
		return "Error Generating Label";
	}

	@Override
	public String getRequestType() {
		return rq_str;
	}

}
