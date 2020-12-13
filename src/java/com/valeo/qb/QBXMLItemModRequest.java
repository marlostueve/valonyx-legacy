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
import com.badiyan.uk.online.beans.CheckoutCodeBean;
import com.generationjava.io.WritingException;
import com.valeo.qb.data.AccountRet;
import com.valeo.qb.data.ItemRet;
import com.valeo.qb.data.SalesTaxCodeRet;
import com.valeo.qb.data.VendorRet;
import com.valeo.qbpos.QBPOSXMLRequest;
import java.util.List;
import org.apache.torque.TorqueException;
import org.apache.torque.util.Criteria;

/**
 *
 * @author marlo
 */
public class QBXMLItemModRequest
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

			qb_request = QBXMLItemModRequest.getRequest((QbRequestDb)objList.get(0));
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
			qb_request = new QBXMLItemModRequest(_request);
			hash.put(key, qb_request);
		}

		return qb_request;
    }
	
	// INSTANCE VARIABLES

	private CheckoutCodeBean item;
	String rq_str = "ItemNonInventoryModRq";
	
	// CONSTRUCTORS

    public
    QBXMLItemModRequest()
    {
		qb_request = new QbRequestDb();
		isNew = true;
    }

    public
    QBXMLItemModRequest(QbRequestDb _request)
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

	@Override
	protected void assembleRequestBody() throws WritingException {

		/*
		 *
    <!-- ItemNonInventoryModRq contains 1 optional attribute: 'requestID' -->
    <ItemNonInventoryModRq requestID = "UUIDTYPE">          <!-- not in QBOE -->
      <ItemNonInventoryMod>
        <ListID>IDTYPE</ListID>
        <EditSequence>STRTYPE</EditSequence>                <!-- max length = 16 for QBD|QBCA|QBUK|QBAU -->
        <Name>STRTYPE</Name>                                <!-- opt, max length = 31 for QBD|QBCA|QBUK|QBAU -->
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
        <ForceUOMChange>BOOLTYPE</ForceUOMChange>           <!-- opt, v7.0 -->
        <IsTaxIncluded>BOOLTYPE</IsTaxIncluded>             <!-- opt, not in QBD, v6.0 -->
        <SalesTaxCodeRef>                                   <!-- opt -->
          <ListID>IDTYPE</ListID>                           <!-- opt -->
          <FullName>STRTYPE</FullName>                      <!-- opt, max length = 3 for QBD|QBCA|QBUK, max length = 6 for QBAU -->
        </SalesTaxCodeRef>
        <!-- BEGIN OR: You may optionally have SalesOrPurchaseMod OR SalesAndPurchaseMod -->
        <SalesOrPurchaseMod>
          <Desc>STRTYPE</Desc>                              <!-- opt, max length = 4095 for QBD|QBCA|QBUK|QBAU -->
          <!-- BEGIN OR: You may optionally have Price OR PricePercent -->
          <Price>PRICETYPE</Price>
          <!-- OR -->
          <PricePercent>PERCENTTYPE</PricePercent>
          <!-- END OR -->
          <AccountRef>                                      <!-- opt, v7.0 -->
            <ListID>IDTYPE</ListID>                         <!-- opt -->
            <FullName>STRTYPE</FullName>                    <!-- opt, max length = 159 for QBD|QBCA|QBUK|QBAU -->
          </AccountRef>
          <ApplyAccountRefToExistingTxns>BOOLTYPE</ApplyAccountRefToExistingTxns> <!-- opt, v7.0 -->
        </SalesOrPurchaseMod>
        <!-- OR -->
        <SalesAndPurchaseMod>
          <SalesDesc>STRTYPE</SalesDesc>                    <!-- opt, max length = 4095 for QBD|QBCA|QBUK|QBAU -->
          <SalesPrice>PRICETYPE</SalesPrice>                <!-- opt -->
          <IncomeAccountRef>                                <!-- opt, v7.0 -->
            <ListID>IDTYPE</ListID>                         <!-- opt -->
            <FullName>STRTYPE</FullName>                    <!-- opt, max length = 159 for QBD|QBCA|QBUK|QBAU -->
          </IncomeAccountRef>
          <ApplyIncomeAccountRefToExistingTxns>BOOLTYPE</ApplyIncomeAccountRefToExistingTxns> <!-- opt, v7.0 -->
          <PurchaseDesc>STRTYPE</PurchaseDesc>              <!-- opt, max length = 4095 for QBD|QBCA|QBUK|QBAU -->
          <PurchaseCost>PRICETYPE</PurchaseCost>            <!-- opt -->
          <PurchaseTaxCodeRef>                              <!-- opt, not in QBD, v6.0 -->
            <ListID>IDTYPE</ListID>                         <!-- opt -->
            <FullName>STRTYPE</FullName>                    <!-- opt, max length = 3 for QBCA|QBUK, max length = 6 for QBAU -->
          </PurchaseTaxCodeRef>
          <ExpenseAccountRef>                               <!-- opt, v7.0 -->
            <ListID>IDTYPE</ListID>                         <!-- opt -->
            <FullName>STRTYPE</FullName>                    <!-- opt, max length = 159 for QBD|QBCA|QBUK|QBAU -->
          </ExpenseAccountRef>
          <ApplyExpenseAccountRefToExistingTxns>BOOLTYPE</ApplyExpenseAccountRefToExistingTxns> <!-- opt, v7.0 -->
          <PrefVendorRef>                                   <!-- opt -->
            <ListID>IDTYPE</ListID>                         <!-- opt -->
            <FullName>STRTYPE</FullName>                    <!-- opt, max length = 41 for QBD|QBCA|QBUK|QBAU -->
          </PrefVendorRef>
        </SalesAndPurchaseMod>
        <!-- END OR -->
      </ItemNonInventoryMod>
      <IncludeRetElement>STRTYPE</IncludeRetElement>        <!-- opt, may rep, max length = 50 for QBD|QBCA|QBUK|QBAU, v4.0 -->
    </ItemNonInventoryModRq>
		 *
		 */

		
		
		/*
		 *
    <!-- ItemServiceModRq contains 1 optional attribute: 'requestID' -->
    <ItemServiceModRq requestID = "UUIDTYPE">
      <ItemServiceMod>
        <ListID>IDTYPE</ListID>
        <EditSequence>STRTYPE</EditSequence>                <!-- max length = 16 for QBD|QBCA|QBUK|QBAU, max length = 10 for QBOE -->
        <Name>STRTYPE</Name>                                <!-- opt, max length = 31 for QBD|QBCA|QBUK|QBAU -->
        <IsActive>BOOLTYPE</IsActive>                       <!-- opt, not in QBOE -->
        <ParentRef>                                         <!-- opt -->
          <ListID>IDTYPE</ListID>                           <!-- opt -->
          <FullName>STRTYPE</FullName>                      <!-- opt -->
        </ParentRef>
        <UnitOfMeasureSetRef>                               <!-- opt, not in QBOE, v7.0 -->
          <ListID>IDTYPE</ListID>                           <!-- opt -->
          <FullName>STRTYPE</FullName>                      <!-- opt, max length = 31 for QBD|QBCA|QBUK|QBAU -->
        </UnitOfMeasureSetRef>
        <ForceUOMChange>BOOLTYPE</ForceUOMChange>           <!-- opt, not in QBOE, v7.0 -->
        <IsTaxIncluded>BOOLTYPE</IsTaxIncluded>             <!-- opt, not in QBD|QBOE, v6.0 -->
        <SalesTaxCodeRef>                                   <!-- opt, not in QBOE -->
          <ListID>IDTYPE</ListID>                           <!-- opt -->
          <FullName>STRTYPE</FullName>                      <!-- opt, max length = 3 for QBD|QBCA|QBUK, max length = 6 for QBAU -->
        </SalesTaxCodeRef>
        <!-- BEGIN OR: You may optionally have SalesOrPurchaseMod OR SalesAndPurchaseMod -->
        <SalesOrPurchaseMod>
          <Desc>STRTYPE</Desc>                              <!-- opt, max length = 4095 for QBD|QBCA|QBUK|QBAU, max length = 4000 for QBOE -->
          <!-- BEGIN OR: You may optionally have Price OR PricePercent -->
          <Price>PRICETYPE</Price>
          <!-- OR -->
          <PricePercent>PERCENTTYPE</PricePercent>
          <!-- END OR -->
          <AccountRef>                                      <!-- opt, not in QBOE, v7.0 -->
            <ListID>IDTYPE</ListID>                         <!-- opt -->
            <FullName>STRTYPE</FullName>                    <!-- opt, max length = 159 for QBD|QBCA|QBUK|QBAU -->
          </AccountRef>
          <ApplyAccountRefToExistingTxns>BOOLTYPE</ApplyAccountRefToExistingTxns> <!-- opt, not in QBOE, v7.0 -->
        </SalesOrPurchaseMod>
        <!-- OR -->
        <SalesAndPurchaseMod>                               <!-- not in QBOE -->
          <SalesDesc>STRTYPE</SalesDesc>                    <!-- opt, max length = 4095 for QBD|QBCA|QBUK|QBAU -->
          <SalesPrice>PRICETYPE</SalesPrice>                <!-- opt -->
          <IncomeAccountRef>                                <!-- opt, v7.0 -->
            <ListID>IDTYPE</ListID>                         <!-- opt -->
            <FullName>STRTYPE</FullName>                    <!-- opt, max length = 159 for QBD|QBCA|QBUK|QBAU -->
          </IncomeAccountRef>
          <ApplyIncomeAccountRefToExistingTxns>BOOLTYPE</ApplyIncomeAccountRefToExistingTxns> <!-- opt, v7.0 -->
          <PurchaseDesc>STRTYPE</PurchaseDesc>              <!-- opt, max length = 4095 for QBD|QBCA|QBUK|QBAU -->
          <PurchaseCost>PRICETYPE</PurchaseCost>            <!-- opt -->
          <PurchaseTaxCodeRef>                              <!-- opt, not in QBD, v6.0 -->
            <ListID>IDTYPE</ListID>                         <!-- opt -->
            <FullName>STRTYPE</FullName>                    <!-- opt, max length = 3 for QBCA|QBUK, max length = 6 for QBAU -->
          </PurchaseTaxCodeRef>
          <ExpenseAccountRef>                               <!-- opt, v7.0 -->
            <ListID>IDTYPE</ListID>                         <!-- opt -->
            <FullName>STRTYPE</FullName>                    <!-- opt, max length = 159 for QBD|QBCA|QBUK|QBAU -->
          </ExpenseAccountRef>
          <ApplyExpenseAccountRefToExistingTxns>BOOLTYPE</ApplyExpenseAccountRefToExistingTxns> <!-- opt, v7.0 -->
          <PrefVendorRef>                                   <!-- opt -->
            <ListID>IDTYPE</ListID>                         <!-- opt -->
            <FullName>STRTYPE</FullName>                    <!-- opt, max length = 41 for QBD|QBCA|QBUK|QBAU -->
          </PrefVendorRef>
        </SalesAndPurchaseMod>
        <!-- END OR -->
      </ItemServiceMod>
      <IncludeRetElement>STRTYPE</IncludeRetElement>        <!-- opt, may rep, max length = 50 for QBD|QBCA|QBUK|QBAU, not in QBOE, v4.0 -->
    </ItemServiceModRq>
		 *
		 */
		
		String mod_str = "ItemNonInventoryMod";
		
		try
		{
			switch (item.getQBItemType())
			{
				case ItemRet.NON_INVENTORY_TYPE: rq_str = "ItemNonInventoryModRq"; mod_str = "ItemNonInventoryMod"; break;
				case ItemRet.INVENTORY_TYPE: rq_str = "ItemInventoryModRq"; mod_str = "ItemInventoryMod"; break;
				case ItemRet.SERVICE_TYPE: rq_str = "ItemServiceModRq"; mod_str = "ItemServiceMod"; break;
				case ItemRet.GROUP_TYPE: rq_str = "ItemGroupModRq"; mod_str = "ItemGroupMod"; break;
				case ItemRet.SALES_TAX_TYPE: rq_str = "ItemSalesTaxModRq"; mod_str = "ItemSalesTaxMod"; break;
				case ItemRet.SALES_TAX_GROUP_TYPE: rq_str = "ItemSalesTaxGroupModRq"; mod_str = "ItemSalesTaxGroupMod"; break;
				case ItemRet.DISCOUNT_TYPE: rq_str = "ItemDiscountModRq"; mod_str = "ItemDiscountMod"; break;
				case ItemRet.FIXED_ASSET_TYPE: rq_str = "ItemFixedAssetModRq"; mod_str = "ItemFixedAssetMod"; break;
				case ItemRet.INVENTORY_ASSEMBLY_TYPE: rq_str = "ItemInventoryAssemblyModRq"; mod_str = "ItemInventoryAssemblyMod"; break;
				case ItemRet.OTHER_CHARGE_TYPE: rq_str = "ItemOtherChargeModRq"; mod_str = "ItemOtherChargeMod"; break;
				case ItemRet.PAYMENT_TYPE: rq_str = "ItemPaymentModRq"; mod_str = "ItemPaymentMod"; break;
				case ItemRet.SUBTOTAL_TYPE: rq_str = "ItemSubtotalModRq"; mod_str = "ItemSubtotalMod"; break;
			}
		}
		catch (IllegalValueException x)
		{
			x.printStackTrace();
		}

		xmlwriter.writeEntity(rq_str);
		xmlwriter.writeAttribute("requestID", getRequestId());

		xmlwriter.writeEntity(mod_str);

		xmlwriter.writeEntity("ListID");
		xmlwriter.writeText(item.getListID());
		xmlwriter.endEntity();

		xmlwriter.writeEntity("EditSequence");
		xmlwriter.writeText(item.getEditSequence());
		xmlwriter.endEntity();

		xmlwriter.writeEntity("Name");
		xmlwriter.writeText(item.getLabel());
		xmlwriter.endEntity();

		xmlwriter.writeEntity("IsActive");
		xmlwriter.writeText(item.isActive() ? "1" : "0");
		xmlwriter.endEntity();
		
		if (mod_str.equals("ItemGroupMod") || mod_str.equals("ItemSalesTaxMod"))
		{
			/*
			 * <ItemDesc>STRTYPE</ItemDesc>
			 */
			
			xmlwriter.writeEntity("ItemDesc");
			xmlwriter.writeText(item.getLabel());
			xmlwriter.endEntity();
			
			if (mod_str.equals("ItemSalesTaxMod"))
			{
				CUBean.sendEmail("marlo@valeowc.com", "admin@valeowc.com", "TO DO: Complete ItemSalesTaxMod Processing", "Do Stuff...");
				
				/*
				xmlwriter.writeEntity("TaxRate");
				xmlwriter.writeText(item.getLabel());
				xmlwriter.endEntity();
				 */
			}
			
			if (mod_str.equals("ItemGroupMod"))
			{
				// do child items here...

				CUBean.sendEmail("marlo@valeowc.com", "admin@valeowc.com", "TO DO: Complete ItemGroupMod Child Processing", "Do Stuff...");
			}
			
		}
		else if (mod_str.equals("ItemSalesTaxGroupMod") || mod_str.equals("ItemDiscountMod") || mod_str.equals("ItemFixedAssetMod") || mod_str.equals("ItemInventoryAssemblyMod") || mod_str.equals("ItemOtherChargeMod") || mod_str.equals("ItemPaymentMod") || mod_str.equals("ItemSubtotalMod"))
		{
			CUBean.sendEmail("marlo@valeowc.com", "admin@valeowc.com", "TO DO: Complete " + mod_str + " Processing", "Do Stuff...");
		}
		else
		{
		
			try
			{
				SalesTaxCodeRet qbfs_sales_tax_code= SalesTaxCodeRet.getSalesTaxCode(item.getSalesTaxCodeId());

				xmlwriter.writeEntity("SalesTaxCodeRef");
				xmlwriter.writeEntity("ListID");
				xmlwriter.writeText(qbfs_sales_tax_code.getListID());
				xmlwriter.endEntity();
				xmlwriter.writeEntity("FullName");
				xmlwriter.writeText(qbfs_sales_tax_code.getName());
				xmlwriter.endEntity();
				xmlwriter.endEntity(); // </SalesTaxCodeRef>
			}
			catch (Exception x)
			{
				x.printStackTrace();
			}
			
			boolean is_aggregate = mod_str.equals("ItemNonInventoryMod") || mod_str.equals("ItemServiceMod");
			
			if (is_aggregate && !item.isReimbursable())
			{
				/*
				<SalesOrPurchaseMod>
				  <Desc>STRTYPE</Desc>                              <!-- opt, max length = 4095 for QBD|QBCA|QBUK|QBAU, max length = 4000 for QBOE -->
				  <!-- BEGIN OR: You may optionally have Price OR PricePercent -->
				  <Price>PRICETYPE</Price>
				  <!-- OR -->
				  <PricePercent>PERCENTTYPE</PricePercent>
				  <!-- END OR -->
				  <AccountRef>                                      <!-- opt, not in QBOE, v7.0 -->
					<ListID>IDTYPE</ListID>                         <!-- opt -->
					<FullName>STRTYPE</FullName>                    <!-- opt, max length = 159 for QBD|QBCA|QBUK|QBAU -->
				  </AccountRef>
				  <ApplyAccountRefToExistingTxns>BOOLTYPE</ApplyAccountRefToExistingTxns> <!-- opt, not in QBOE, v7.0 -->
				</SalesOrPurchaseMod>
				 */
				
				xmlwriter.writeEntity("SalesOrPurchaseMod");

				xmlwriter.writeEntity("Desc");
				xmlwriter.writeText(item.getSalesDescriptionString());
				xmlwriter.endEntity(); // </SalesDesc>

				xmlwriter.writeEntity("Price");
				xmlwriter.writeText(item.getAmountString());
				xmlwriter.endEntity(); // </Price>

				try
				{
					int income_account_id = item.getIncomeAccountId();
					if (income_account_id > 0)
					{
						AccountRet income_account = AccountRet.getAccount(income_account_id);

						xmlwriter.writeEntity("AccountRef");
						xmlwriter.writeEntity("ListID");
						xmlwriter.writeText(income_account.getListID());
						xmlwriter.endEntity();
						xmlwriter.endEntity(); // </AccountRef>
					}
				}
				catch (Exception x)
				{
					x.printStackTrace();
				}
				
				xmlwriter.endEntity(); // </SalesOrPurchaseMod>
			}
			else
			{
				
				/*
				<SalesAndPurchaseMod>
				  <SalesDesc>STRTYPE</SalesDesc>                    <!-- opt, max length = 4095 for QBD|QBCA|QBUK|QBAU -->
				  <SalesPrice>PRICETYPE</SalesPrice>                <!-- opt -->
				  <IncomeAccountRef>                                <!-- opt, v7.0 -->
					<ListID>IDTYPE</ListID>                         <!-- opt -->
					<FullName>STRTYPE</FullName>                    <!-- opt, max length = 159 for QBD|QBCA|QBUK|QBAU -->
				  </IncomeAccountRef>
				  <ApplyIncomeAccountRefToExistingTxns>BOOLTYPE</ApplyIncomeAccountRefToExistingTxns> <!-- opt, v7.0 -->
				  <PurchaseDesc>STRTYPE</PurchaseDesc>              <!-- opt, max length = 4095 for QBD|QBCA|QBUK|QBAU -->
				  <PurchaseCost>PRICETYPE</PurchaseCost>            <!-- opt -->
				  <PurchaseTaxCodeRef>                              <!-- opt, not in QBD, v6.0 -->
					<ListID>IDTYPE</ListID>                         <!-- opt -->
					<FullName>STRTYPE</FullName>                    <!-- opt, max length = 3 for QBCA|QBUK, max length = 6 for QBAU -->
				  </PurchaseTaxCodeRef>
				  <ExpenseAccountRef>                               <!-- opt, v7.0 -->
					<ListID>IDTYPE</ListID>                         <!-- opt -->
					<FullName>STRTYPE</FullName>                    <!-- opt, max length = 159 for QBD|QBCA|QBUK|QBAU -->
				  </ExpenseAccountRef>
				  <ApplyExpenseAccountRefToExistingTxns>BOOLTYPE</ApplyExpenseAccountRefToExistingTxns> <!-- opt, v7.0 -->
				  <PrefVendorRef>                                   <!-- opt -->
					<ListID>IDTYPE</ListID>                         <!-- opt -->
					<FullName>STRTYPE</FullName>                    <!-- opt, max length = 41 for QBD|QBCA|QBUK|QBAU -->
				  </PrefVendorRef>
				</SalesAndPurchaseMod>
				*/

				if (is_aggregate)
					xmlwriter.writeEntity("SalesAndPurchaseMod");

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

				if (is_aggregate)
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

				if (mod_str.equals("ItemInventoryMod"))
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

							/*  This isn't parsing for some reason...
							xmlwriter.writeEntity("ApplyCOGSAccountRefToExistingTxns");
							xmlwriter.writeText("1");
							xmlwriter.endEntity(); // </ApplyCOGSAccountRefToExistingTxns>
							 * 
							 */
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

				if (mod_str.equals("ItemInventoryMod"))
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


				if (is_aggregate)
					xmlwriter.endEntity(); // </SalesAndPurchaseMod>
		
			}
		}
		

		xmlwriter.endEntity(); // </ItemNonInventoryMod>

		xmlwriter.endEntity(); // </ItemNonInventoryModRq>
		
		
	}

	@Override
	public String getLabel() {
		return CUBean.getUserTimeString(super.creation_date) + " - Inventory Item Mod Request (" + item.getLabel() + ")";
	}

	@Override
	public String getRequestType() {
		return rq_str;
	}

}
