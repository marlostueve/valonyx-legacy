/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.badiyan.uk.online.PDF;


import com.badiyan.torque.CheckoutOrderline;
import java.io.*;
import java.util.*;

import com.badiyan.uk.online.beans.*;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
//import com.lowagie.text.pdf.codec.TiffImage;

import com.badiyan.uk.beans.*;

import java.awt.Color;

import com.badiyan.uk.exceptions.*;
import com.valeo.qbpos.data.TenderRet;
import java.math.BigDecimal;
import java.text.NumberFormat;
import org.apache.torque.TorqueException;

public class
ZOutDrawerCountReportBuilder
{
	private static float height_per_row = 10f;
	private static float left_margin = 5f;

	/**
	 * Manages the creation of the printed plan's individual pages.
	 * @param args
	 */
	public static synchronized String
	generateReport(UKOnlineCompanyBean _company, UKOnlinePersonBean _cashier, CashOut _cash_out, String _workstation, boolean _is_primary)
		throws IllegalValueException
	{

		Calendar now = Calendar.getInstance();

		String realPath = CUBean.getProperty("cu.realPath");
		String resourcesFolder = CUBean.getProperty("cu.resourcesFolder");
		if (resourcesFolder == null)
			throw new IllegalValueException("Property not defined: cu.resourcesFolder");
		resourcesFolder = realPath + resourcesFolder;

		String file = "z-out-drawer-count-report-" + System.currentTimeMillis() + ".pdf";
		String pdf_file = resourcesFolder + "pdf/" + file;

		BigDecimal negative_one = new BigDecimal(-1);

		try
		{
			// get the orders within the specified date range to that I can add non-taxable sales and taxable sales in the three columns

			BigDecimal non_taxable_sales = CUBean.zero;
			BigDecimal non_taxable_returns = CUBean.zero;
			BigDecimal non_taxable_net = CUBean.zero;

			BigDecimal taxable_sales = CUBean.zero;
			BigDecimal taxable_returns = CUBean.zero;
			BigDecimal taxable_net = CUBean.zero;

			BigDecimal sales_tax = CUBean.zero;
			BigDecimal return_tax = CUBean.zero;
			BigDecimal tax_net = CUBean.zero;

			BigDecimal sales_discount = CUBean.zero;
			BigDecimal return_discount = CUBean.zero;

			Vector credit_card_orders = new Vector();
			Vector cash_orders = new Vector();
			Vector check_orders = new Vector();
			Vector account_orders = new Vector();
			Vector gift_card_orders = new Vector();
			Vector gift_certificate_orders = new Vector();

			Vector credit_card_tenders = new Vector();
			Vector cash_tenders = new Vector();
			Vector check_tenders = new Vector();
			Vector account_tenders = new Vector();
			Vector gift_card_tenders = new Vector();
			Vector gift_certificate_tenders = new Vector();
			Vector payment_on_account_tenders = new Vector();


			BigDecimal credit_card_activity = CUBean.zero; // do I need this??  should it be negative??
			BigDecimal credit_card_in = CUBean.zero;
			BigDecimal credit_card_out = CUBean.zero; // example?

			BigDecimal cash_activity = CUBean.zero; // do I need this??  should it be negative??
			BigDecimal cash_in = CUBean.zero;
			BigDecimal cash_out = CUBean.zero; // anything other than change???

			BigDecimal check_activity = CUBean.zero; // do I need this??  should it be negative??
			BigDecimal check_in = CUBean.zero;
			BigDecimal check_out = CUBean.zero; // example?

			BigDecimal account_activity = CUBean.zero; // display this as a negative value for the purposes of calculating what's available for deposit
			BigDecimal account_in = CUBean.zero; // the amount placed on account
			BigDecimal account_out = CUBean.zero; // example?  apparently this is the amount of payments on previous on-account orders

			BigDecimal gift_card_activity = CUBean.zero;
			BigDecimal gift_card_in = CUBean.zero;
			BigDecimal gift_card_out = CUBean.zero; // example?

			BigDecimal gift_certificate_activity = CUBean.zero;
			BigDecimal gift_certificate_in = CUBean.zero;
			BigDecimal gift_certificate_out = CUBean.zero; // example?


			BigDecimal net_deposits = CUBean.zero; // where does this come from.  prolly should be negative value for the purposes of this report
			BigDecimal payouts = CUBean.zero; // where does this come from.  prolly should be negative value for the purposes of this report

			int num_sales_receipts = 0;
			int num_return_receipts = 0;
			int num_deposit_receipts = 0;
			int num_reversed_receipts = 0;
			int num_payout_receipts = 0;

			Vector multiple_orders = new Vector();

			HashMap<ValeoOrderBean,Vector> order_to_tenders_hash = new HashMap<ValeoOrderBean,Vector>();
			Vector tenders_for_current_orders = new Vector();

			//HashMap<TenderRet,Vector> tender_to_orders_hash = new HashMap<TenderRet,Vector>();

			//Vector orders = ValeoOrderBean.getOrders(_company, _cash_out.getStartDate(), _cash_out.getEndDate());
			
			Vector cash_out_tenders = TenderRet.getTendersUnsubmitted(_company, _cash_out.getStartDate(), _cash_out.getEndDate());
			Vector consumed_tenders = new Vector();
			

			Vector orders;
			if (_is_primary)
				orders = _cash_out.getOrders();
			else
			{
				_cash_out.setOrders(ValeoOrderBean.getOrdersForCashOut(_company, _cash_out.getStartDate(), _cash_out.getEndDate()));
				_cash_out.save();

				orders = _cash_out.getOrders(_workstation);
			}

			System.out.println("num orders found >" + orders.size());

			Iterator orders_itr = orders.iterator();
			while (orders_itr.hasNext())
			{
				ValeoOrderBean order = (ValeoOrderBean)orders_itr.next();
					// make sure these are closed orders only!!!  I'm going to have to balance this against tenders for this order
					// on second thought, perhaps it's OK to show stuff as being on-account on this report.  need to track total sales, prolly

				// an order can have both taxable and non-taxable stuff on it.  loop through the items on the order to determine

				System.out.println("order.getStatus() >" + order.getStatus());

				if (order.getStatus().equals(ValeoOrderBean.SALES_RECEIPT_ORDER_STATUS))
					num_sales_receipts++;
				else if (order.getStatus().equals(ValeoOrderBean.RETURN_RECEIPT_ORDER_STATUS))
					num_return_receipts++;
				else if (order.getStatus().equals(ValeoOrderBean.DEPOSIT_RECEIPT_ORDER_STATUS))
					num_deposit_receipts++;
				else if (order.getStatus().equals(ValeoOrderBean.REVERSED_RECEIPT_ORDER_STATUS))
					num_reversed_receipts++;
				else if (order.getStatus().equals(ValeoOrderBean.PAYOUT_RECEIPT_ORDER_STATUS))
					num_payout_receipts++;

				Iterator orderline_itr = order.getOrdersVec().iterator();
				while (orderline_itr.hasNext())
				{
					CheckoutOrderline orderline = (CheckoutOrderline)orderline_itr.next();
					BigDecimal tax = orderline.getTax();
					BigDecimal total  = orderline.getPrice().multiply(orderline.getQuantity());

					//boolean is_return = false;

					if (order.isReturn())
					{
						if (tax.compareTo(CUBean.zero) != 0)
						{
							taxable_returns = taxable_returns.subtract(total);
							return_tax = return_tax.subtract(tax);
						}
						else
							non_taxable_returns = non_taxable_returns.subtract(total);

						return_discount = return_discount.add(order.getDiscountAmount());
					}
					else if (!order.isReversed())
					{
						// this is not a return
						// was there tax??

						if (tax.compareTo(CUBean.zero) != 0)
						{
							// there's tax, therefore taxable

							taxable_sales = taxable_sales.add(total);
							sales_tax = sales_tax.add(tax);
						}
						else
						{
							// not taxable

							non_taxable_sales = non_taxable_sales.add(total);
						}

						sales_discount = sales_discount.add(order.getDiscountAmount());
					}
				}

				if (!order.isReversed())
				{
					// get the tender(s) for this order

					Vector tenders = TenderRet.getTenders(order);
					if (tenders.size() > 1)
						multiple_orders.addElement(order);
					order_to_tenders_hash.put(order, tenders);
					Iterator tender_itr = tenders.iterator();
					while (tender_itr.hasNext())
					{
						TenderRet tender = (TenderRet)tender_itr.next();
						consumed_tenders.addElement(tender);

						if (!tenders_for_current_orders.contains(tender))
							tenders_for_current_orders.addElement(tender);

						if (tender.getType().equals(TenderRet.ACCOUNT_TENDER_TYPE))
						{
							if (!account_orders.contains(order))
								account_orders.addElement(order);
							if (!account_tenders.contains(tender))
							{
								account_tenders.addElement(tender);
								account_activity = account_activity.add(tender.getAmountBigDecimal().multiply(negative_one));
								account_in = account_in.add(tender.getAmountBigDecimal());
							}
						}
						else if (tender.getType().equals(TenderRet.CASH_TENDER_TYPE))
						{
							if (!cash_orders.contains(order))
								cash_orders.addElement(order);
							if (!cash_tenders.contains(tender))
							{
								cash_tenders.addElement(tender);
								cash_activity = cash_activity.add(tender.getAmountBigDecimal());
								cash_in = cash_in.add(tender.getTenderAmountBigDecimal());
								cash_out = cash_out.add(tender.getChangeAmountBigDecimal());
							}
						}
						else if (tender.getType().equals(TenderRet.CHECK_TENDER_TYPE))
						{
							if (!check_orders.contains(order))
								check_orders.addElement(order);
							if (!check_tenders.contains(tender))
							{
								check_tenders.addElement(tender);
								check_activity = check_activity.add(tender.getAmountBigDecimal());
								check_in = check_in.add(tender.getAmountBigDecimal());
							}
						}
						else if (tender.getType().equals(TenderRet.CREDIT_CARD_TENDER_TYPE))
						{
							if (!credit_card_orders.contains(order))
								credit_card_orders.addElement(order);
							if (!credit_card_tenders.contains(tender))
							{
								credit_card_tenders.addElement(tender);
								credit_card_activity = credit_card_activity.add(tender.getAmountBigDecimal());
								credit_card_in = credit_card_in.add(tender.getAmountBigDecimal());
							}
						}
						else if (tender.getType().equals(TenderRet.GIFT_CARD_TENDER_TYPE))
						{
							if (!gift_card_orders.contains(order))
								gift_card_orders.addElement(order);
							if (!gift_card_tenders.contains(tender))
							{
								gift_card_tenders.addElement(tender);
								gift_card_activity = gift_card_activity.add(tender.getAmountBigDecimal().multiply(negative_one));
								gift_card_in = gift_card_in.add(tender.getAmountBigDecimal());
							}
						}
						else if (tender.getType().equals(TenderRet.GIFT_CERTIFICATE_TENDER_TYPE))
						{
							if (!gift_certificate_orders.contains(order))
								gift_certificate_orders.addElement(order);
							if (!gift_certificate_tenders.contains(tender))
							{
								gift_certificate_tenders.addElement(tender);
								gift_certificate_activity = gift_certificate_activity.add(tender.getAmountBigDecimal().multiply(negative_one));
								gift_certificate_in = gift_certificate_in.add(tender.getAmountBigDecimal());
							}
						}
					}
				}
			}
			
			Iterator cash_out_tenders_itr = cash_out_tenders.iterator();
			while (cash_out_tenders_itr.hasNext())
			{
				TenderRet tender = (TenderRet)cash_out_tenders_itr.next();
				if (!consumed_tenders.contains(tender))
				{
					System.out.println("FOUND UNCONSUMED TENDER - not connected with a cash-out order");
					
					if (tender.getType().equals(TenderRet.ACCOUNT_TENDER_TYPE))
					{
						if (!account_tenders.contains(tender))
						{
							account_tenders.addElement(tender);
							account_activity = account_activity.add(tender.getAmountBigDecimal().multiply(negative_one));
							account_in = account_in.add(tender.getAmountBigDecimal());
						}
					}
					else if (tender.getType().equals(TenderRet.CASH_TENDER_TYPE))
					{
						if (!cash_tenders.contains(tender))
						{
							cash_tenders.addElement(tender);
							cash_activity = cash_activity.add(tender.getAmountBigDecimal());
							cash_in = cash_in.add(tender.getTenderAmountBigDecimal());
							cash_out = cash_out.add(tender.getChangeAmountBigDecimal());
						}
					}
					else if (tender.getType().equals(TenderRet.CHECK_TENDER_TYPE))
					{
						if (!check_tenders.contains(tender))
						{
							check_tenders.addElement(tender);
							check_activity = check_activity.add(tender.getAmountBigDecimal());
							check_in = check_in.add(tender.getAmountBigDecimal());
						}
					}
					else if (tender.getType().equals(TenderRet.CREDIT_CARD_TENDER_TYPE))
					{
						if (!credit_card_tenders.contains(tender))
						{
							credit_card_tenders.addElement(tender);
							credit_card_activity = credit_card_activity.add(tender.getAmountBigDecimal());
							credit_card_in = credit_card_in.add(tender.getAmountBigDecimal());
						}
					}
					else if (tender.getType().equals(TenderRet.GIFT_CARD_TENDER_TYPE))
					{
						if (!gift_card_tenders.contains(tender))
						{
							gift_card_tenders.addElement(tender);
							gift_card_activity = gift_card_activity.add(tender.getAmountBigDecimal().multiply(negative_one));
							gift_card_in = gift_card_in.add(tender.getAmountBigDecimal());
						}
					}
					else if (tender.getType().equals(TenderRet.GIFT_CERTIFICATE_TENDER_TYPE))
					{
						if (!gift_certificate_tenders.contains(tender))
						{
							gift_certificate_tenders.addElement(tender);
							gift_certificate_activity = gift_certificate_activity.add(tender.getAmountBigDecimal().multiply(negative_one));
							gift_certificate_in = gift_certificate_in.add(tender.getAmountBigDecimal());
						}
					}
				}
			}

			// payments would not be associated with an order for the current date range
			// this is for any other tender on the given date range that is not already being applied to a current order

			BigDecimal payments = CUBean.zero;

			Vector tenders_for_no_current_orders = new Vector();

			Vector tenders;
			if (_is_primary)
				tenders = TenderRet.getTendersUnsubmitted(_company, _cash_out.getStartDate(), _cash_out.getEndDate());
			else
				tenders = TenderRet.getTendersUnsubmitted(_company, _cash_out.getStartDate(), _cash_out.getEndDate(), _workstation);

			Iterator tender_itr = tenders.iterator();
			while (tender_itr.hasNext())
			{
				TenderRet tender = (TenderRet)tender_itr.next();

				if (!tenders_for_current_orders.contains(tender))
				{
					tenders_for_no_current_orders.addElement(tender);
					System.out.println("tenders_for_no_current_order >" + tender.getLabel());
				}
				
				//if (!tender.getType().equals(TenderRet.ACCOUNT_TENDER_TYPE))
				if (tender.isPaymentOnAccount())
				{
					payments = payments.add(tender.getAmountBigDecimal());
					payment_on_account_tenders.addElement(tender);
				}
			}

			non_taxable_net = non_taxable_sales.subtract(non_taxable_returns);
			taxable_net = taxable_sales.subtract(taxable_returns);
			tax_net = sales_tax.subtract(return_tax);

			float width = 234f;
            float height = 450f;
				// this is the base, minimum height.  I need to add rows for tips, checks, credit cards, account, cash, and possibly gift cert & gift cards
				// where do I get the tips from????
			float height_offset = 0f;
			if (credit_card_tenders.size() > 0)
				height_offset += (credit_card_tenders.size() + 3) * height_per_row; // add three to include a header, total, and an extra spacing row
			if (check_tenders.size() > 0)
				height_offset += (check_tenders.size() + 3) * height_per_row;
			if (cash_tenders.size() > 0)
				height_offset += (cash_tenders.size() + 3) * height_per_row;
			if (account_tenders.size() > 0)
				height_offset += (account_tenders.size() + 3) * height_per_row;
			if (payment_on_account_tenders.size() > 0)
				height_offset += (payment_on_account_tenders.size() + 3) * height_per_row;
			if (gift_card_tenders.size() > 0)
				height_offset += (gift_card_tenders.size() + 3) * height_per_row;
			if (gift_certificate_tenders.size() > 0)
				height_offset += (gift_certificate_tenders.size() + 3) * height_per_row;

			height += height_offset;

			float count_offset = 175f;

			height += count_offset;


            // step 1: creation of a document-object
            Document document = new Document(new Rectangle(width, height));
            // step 2: we create a writer that listens to the document
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(pdf_file));
            // step 3: we open the document
            document.open();
            // step 4: we add content
            PdfContentByte cb = writer.getDirectContent();
			document.newPage();

			BaseFont bf = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);

			int row = 1;

			ZOutDrawerCountReportBuilder.doText(CUBean.getUserDateString(now.getTime()), cb, bf, 8, height, row, left_margin);
			ZOutDrawerCountReportBuilder.doText(CUBean.getUserTimeString(now.getTime()), cb, bf, 8, height, row, 195f); row ++;

			ZOutDrawerCountReportBuilder.doText("Z-Out Drawer Count", cb, bf, 12, height, row, 60f); row++;
			ZOutDrawerCountReportBuilder.doText("Date " + CUBean.getUserDateString(_cash_out.getStartDate()) + " 12:00 AM to " + CUBean.getUserDateString(_cash_out.getEndDate()) + " 11:59 PM", cb, bf, 6, height, row, left_margin); row++;
			ZOutDrawerCountReportBuilder.doText("Sales Activity", cb, bf, 9, height, row, left_margin);
			ZOutDrawerCountReportBuilder.doText("Sales", cb, bf, 9, height, row, 80f);
			ZOutDrawerCountReportBuilder.doText("Returns", cb, bf, 9, height, row, 135f);
			ZOutDrawerCountReportBuilder.doText("Net", cb, bf, 9, height, row, 200f); row++;

			ZOutDrawerCountReportBuilder.doText("Non Txbl :", cb, bf, 8, height, row, left_margin);

			String str = ZOutDrawerCountReportBuilder.formatCurrency(non_taxable_sales);
			ZOutDrawerCountReportBuilder.doText(str, cb, bf, 8, height, row, ZOutDrawerCountReportBuilder.getXForNumberStr(str, 80f));
			str = ZOutDrawerCountReportBuilder.formatCurrency(non_taxable_returns);
			ZOutDrawerCountReportBuilder.doText(str, cb, bf, 8, height, row, ZOutDrawerCountReportBuilder.getXForNumberStr(str, 145f));
			str = ZOutDrawerCountReportBuilder.formatCurrency(non_taxable_net);
			ZOutDrawerCountReportBuilder.doText(str, cb, bf, 8, height, row, ZOutDrawerCountReportBuilder.getXForNumberStr(str, 200f)); row++;

			ZOutDrawerCountReportBuilder.doText("Txbl :", cb, bf, 8, height, row, left_margin);
			str = ZOutDrawerCountReportBuilder.formatCurrency(taxable_sales);
			ZOutDrawerCountReportBuilder.doText(str, cb, bf, 8, height, row, ZOutDrawerCountReportBuilder.getXForNumberStr(str, 80f));
			str = ZOutDrawerCountReportBuilder.formatCurrency(taxable_returns);
			ZOutDrawerCountReportBuilder.doText(str, cb, bf, 8, height, row, ZOutDrawerCountReportBuilder.getXForNumberStr(str, 145f));
			str = ZOutDrawerCountReportBuilder.formatCurrency(taxable_net);
			ZOutDrawerCountReportBuilder.doText(str, cb, bf, 8, height, row, ZOutDrawerCountReportBuilder.getXForNumberStr(str, 200f)); row++;

			ZOutDrawerCountReportBuilder.doText("Tax :", cb, bf, 8, height, row, left_margin);
			str = ZOutDrawerCountReportBuilder.formatCurrency(sales_tax);
			ZOutDrawerCountReportBuilder.doText(str, cb, bf, 8, height, row, ZOutDrawerCountReportBuilder.getXForNumberStr(str, 80f));
			str = ZOutDrawerCountReportBuilder.formatCurrency(return_tax);
			ZOutDrawerCountReportBuilder.doText(str, cb, bf, 8, height, row, ZOutDrawerCountReportBuilder.getXForNumberStr(str, 145f));
			str = ZOutDrawerCountReportBuilder.formatCurrency(tax_net);
			ZOutDrawerCountReportBuilder.doText(str, cb, bf, 8, height, row, ZOutDrawerCountReportBuilder.getXForNumberStr(str, 200f)); row++;

			ZOutDrawerCountReportBuilder.doText("Subtotal :", cb, bf, 8, height, row, left_margin);
			str = ZOutDrawerCountReportBuilder.formatCurrency(non_taxable_sales.add(taxable_sales.add(sales_tax)));
			ZOutDrawerCountReportBuilder.doText(str, cb, bf, 8, height, row, ZOutDrawerCountReportBuilder.getXForNumberStr(str, 80f));
			str = ZOutDrawerCountReportBuilder.formatCurrency(non_taxable_returns.add(taxable_returns.add(return_tax)));
			ZOutDrawerCountReportBuilder.doText(str, cb, bf, 8, height, row, ZOutDrawerCountReportBuilder.getXForNumberStr(str, 145f));
			str = ZOutDrawerCountReportBuilder.formatCurrency(non_taxable_net.add(taxable_net.add(tax_net)));
			ZOutDrawerCountReportBuilder.doText(str, cb, bf, 8, height, row, ZOutDrawerCountReportBuilder.getXForNumberStr(str, 200f)); row++;

			row++;

			account_out = payments;

			ZOutDrawerCountReportBuilder.doText("Payments :", cb, bf, 8, height, row, left_margin);
			str = ZOutDrawerCountReportBuilder.formatCurrency(payments);
			ZOutDrawerCountReportBuilder.doText(str, cb, bf, 8, height, row, ZOutDrawerCountReportBuilder.getXForNumberStr(str, 80f));
			ZOutDrawerCountReportBuilder.doText(str, cb, bf, 8, height, row, ZOutDrawerCountReportBuilder.getXForNumberStr(str, 200f)); row++;

			ZOutDrawerCountReportBuilder.doText("Tips :", cb, bf, 8, height, row, left_margin);
			str = ZOutDrawerCountReportBuilder.formatCurrency(CUBean.zero);
			ZOutDrawerCountReportBuilder.doText(str, cb, bf, 8, height, row, ZOutDrawerCountReportBuilder.getXForNumberStr(str, 80f));
			ZOutDrawerCountReportBuilder.doText(str, cb, bf, 8, height, row, ZOutDrawerCountReportBuilder.getXForNumberStr(str, 200f)); row++;

			ZOutDrawerCountReportBuilder.doText("Shipping :", cb, bf, 8, height, row, left_margin);
			str = ZOutDrawerCountReportBuilder.formatCurrency(CUBean.zero);
			ZOutDrawerCountReportBuilder.doText(str, cb, bf, 8, height, row, ZOutDrawerCountReportBuilder.getXForNumberStr(str, 80f));
			ZOutDrawerCountReportBuilder.doText(str, cb, bf, 8, height, row, ZOutDrawerCountReportBuilder.getXForNumberStr(str, 200f)); row++;


			// tips and shipping are not factored into this yet...
			ZOutDrawerCountReportBuilder.doText("Total Activity :", cb, bf, 8, height, row, left_margin);
			str = ZOutDrawerCountReportBuilder.formatCurrency(non_taxable_sales.add(taxable_sales.add(sales_tax.add(payments))));
			ZOutDrawerCountReportBuilder.doText(str, cb, bf, 8, height, row, ZOutDrawerCountReportBuilder.getXForNumberStr(str, 80f));
			str = ZOutDrawerCountReportBuilder.formatCurrency(non_taxable_returns.add(taxable_returns.add(return_tax)));
			ZOutDrawerCountReportBuilder.doText(str, cb, bf, 8, height, row, ZOutDrawerCountReportBuilder.getXForNumberStr(str, 145f));
			BigDecimal net_activity = non_taxable_net.add(taxable_net.add(tax_net.add(payments)));
			str = ZOutDrawerCountReportBuilder.formatCurrency(net_activity);
			ZOutDrawerCountReportBuilder.doText(str, cb, bf, 8, height, row, ZOutDrawerCountReportBuilder.getXForNumberStr(str, 200f)); row++;

			row++;

			ZOutDrawerCountReportBuilder.doText("Sales Adjustments", cb, bf, 9, height, row, left_margin); row++;

			ZOutDrawerCountReportBuilder.doText("Net Sales Activity :", cb, bf, 8, height, row, left_margin);
			ZOutDrawerCountReportBuilder.doText(str, cb, bf, 8, height, row, ZOutDrawerCountReportBuilder.getXForNumberStr(str, 200f)); row++;

			ZOutDrawerCountReportBuilder.doText("Charged to account :", cb, bf, 8, height, row, left_margin);
			str = ZOutDrawerCountReportBuilder.formatCurrency(account_activity);
			ZOutDrawerCountReportBuilder.doText("" + str + "", cb, bf, 8, height, row, ZOutDrawerCountReportBuilder.getXForNumberStr(str, 200f)); row++;

			ZOutDrawerCountReportBuilder.doText("Gift Certificates Used :", cb, bf, 8, height, row, left_margin);
			str = ZOutDrawerCountReportBuilder.formatCurrency(gift_certificate_activity);
			ZOutDrawerCountReportBuilder.doText(str, cb, bf, 8, height, row, ZOutDrawerCountReportBuilder.getXForNumberStr(str, 200f)); row++;

			ZOutDrawerCountReportBuilder.doText("Gift Cards Used :", cb, bf, 8, height, row, left_margin);
			str = ZOutDrawerCountReportBuilder.formatCurrency(gift_card_activity);
			ZOutDrawerCountReportBuilder.doText(str, cb, bf, 8, height, row, ZOutDrawerCountReportBuilder.getXForNumberStr(str, 200f)); row++;

			ZOutDrawerCountReportBuilder.doText("Net Deposits Used :", cb, bf, 8, height, row, left_margin); // where does this come from??? and, does it subtract from the total???
			str = ZOutDrawerCountReportBuilder.formatCurrency(net_deposits);
			ZOutDrawerCountReportBuilder.doText(str, cb, bf, 8, height, row, ZOutDrawerCountReportBuilder.getXForNumberStr(str, 200f)); row++;

			ZOutDrawerCountReportBuilder.doText("Payouts :", cb, bf, 8, height, row, left_margin); // where does this come from???
			str = ZOutDrawerCountReportBuilder.formatCurrency(payouts);
			ZOutDrawerCountReportBuilder.doText(str, cb, bf, 8, height, row, ZOutDrawerCountReportBuilder.getXForNumberStr(str, 200f)); row++;

			ZOutDrawerCountReportBuilder.doText("Total Available for Deposit :", cb, bf, 9, height, row, left_margin);
			BigDecimal total_available_for_deposit = net_activity.add(account_activity.add(gift_certificate_activity.add(gift_card_activity.add(net_deposits.add(payouts))))); // adding 'cause these should be negative values
			str = ZOutDrawerCountReportBuilder.formatCurrency(total_available_for_deposit);
			ZOutDrawerCountReportBuilder.doText(str, cb, bf, 8, height, row, ZOutDrawerCountReportBuilder.getXForNumberStr(str, 200f)); row++;

			row++;

			ZOutDrawerCountReportBuilder.doText("Discounts", cb, bf, 9, height, row, left_margin);

			ZOutDrawerCountReportBuilder.doText("Sales", cb, bf, 9, height, row, 80f);
			ZOutDrawerCountReportBuilder.doText("Returns", cb, bf, 9, height, row, 135f);
			ZOutDrawerCountReportBuilder.doText("Net", cb, bf, 9, height, row, 200f); row++;

			ZOutDrawerCountReportBuilder.doText("Discount :", cb, bf, 8, height, row, left_margin);
			str = ZOutDrawerCountReportBuilder.formatCurrency(sales_discount);
			ZOutDrawerCountReportBuilder.doText(str, cb, bf, 8, height, row, ZOutDrawerCountReportBuilder.getXForNumberStr(str, 80f));
			str = ZOutDrawerCountReportBuilder.formatCurrency(return_discount);
			ZOutDrawerCountReportBuilder.doText(str, cb, bf, 8, height, row, ZOutDrawerCountReportBuilder.getXForNumberStr(str, 145f));
			str = ZOutDrawerCountReportBuilder.formatCurrency(sales_discount.subtract(return_discount));
			ZOutDrawerCountReportBuilder.doText(str, cb, bf, 8, height, row, ZOutDrawerCountReportBuilder.getXForNumberStr(str, 200f)); row++;

			row++;


			ZOutDrawerCountReportBuilder.doText("Receipt Counts", cb, bf, 9, height, row, left_margin);

			ZOutDrawerCountReportBuilder.doText("Sales :", cb, bf, 8, height, row, 100f);
			str = num_sales_receipts + "";
			ZOutDrawerCountReportBuilder.doText(str, cb, bf, 8, height, row, ZOutDrawerCountReportBuilder.getXForNumberStr(str, 200f)); row++;

			ZOutDrawerCountReportBuilder.doText("Returns :", cb, bf, 8, height, row, 100f);
			str = num_return_receipts + "";
			ZOutDrawerCountReportBuilder.doText(num_return_receipts + "", cb, bf, 8, height, row, ZOutDrawerCountReportBuilder.getXForNumberStr(str, 200f)); row++;

			ZOutDrawerCountReportBuilder.doText("Deposits :", cb, bf, 8, height, row, 100f); // not sure where this comes from
			str = num_deposit_receipts + "";
			ZOutDrawerCountReportBuilder.doText(num_deposit_receipts + "", cb, bf, 8, height, row, ZOutDrawerCountReportBuilder.getXForNumberStr(str, 200f)); row++;

			ZOutDrawerCountReportBuilder.doText("Reversed :", cb, bf, 8, height, row, 100f); // what's the difference between a reverse and a return??? assuming reverse only possible on the same day or something...
			str = num_reversed_receipts + "";
			ZOutDrawerCountReportBuilder.doText(num_reversed_receipts + "", cb, bf, 8, height, row, ZOutDrawerCountReportBuilder.getXForNumberStr(str, 200f)); row++;

			ZOutDrawerCountReportBuilder.doText("Payouts :", cb, bf, 8, height, row, 100f); // not sure where this comes from
			str = num_payout_receipts + "";
			ZOutDrawerCountReportBuilder.doText(num_payout_receipts + "", cb, bf, 8, height, row, ZOutDrawerCountReportBuilder.getXForNumberStr(str, 200f)); row++;

			row++;

			ZOutDrawerCountReportBuilder.doText("Dollars", cb, bf, 9, height, row, left_margin);
			ZOutDrawerCountReportBuilder.doText("Paid In :", cb, bf, 8, height, row, 100f);
			str = ZOutDrawerCountReportBuilder.formatCurrency(cash_in);
			ZOutDrawerCountReportBuilder.doText(str, cb, bf, 8, height, row, ZOutDrawerCountReportBuilder.getXForNumberStr(str, 200f)); row++;
			ZOutDrawerCountReportBuilder.doText("Paid Out :", cb, bf, 8, height, row, 100f);
			str = ZOutDrawerCountReportBuilder.formatCurrency(cash_out);
			ZOutDrawerCountReportBuilder.doText(str, cb, bf, 8, height, row, ZOutDrawerCountReportBuilder.getXForNumberStr(str, 200f)); row++;

			/*
			ZOutDrawerCountReportBuilder.doText("Net :", cb, bf, 8, height, row, 100f);
			str = ZOutDrawerCountReportBuilder.formatCurrency(cash_in.subtract(cash_out));
			ZOutDrawerCountReportBuilder.doText(str, cb, bf, 8, height, row, ZOutDrawerCountReportBuilder.getXForNumberStr(str, 200f)); row++;
			 *
			 */

			// extra z-out stuff goes here



			ZOutDrawerCountReportBuilder.doText("Begin :", cb, bf, 8, height, row, 100f);
			str = ZOutDrawerCountReportBuilder.formatCurrency(_cash_out.getBeginAmount(_workstation));
			ZOutDrawerCountReportBuilder.doText(str, cb, bf, 8, height, row, ZOutDrawerCountReportBuilder.getXForNumberStr(str, 200f)); row++;
			ZOutDrawerCountReportBuilder.doText("Net :", cb, bf, 8, height, row, 100f);
			BigDecimal net = _cash_out.getBeginAmount(_workstation).add(cash_in.subtract(cash_out));
			str = ZOutDrawerCountReportBuilder.formatCurrency(net); // how is this calculated???
			ZOutDrawerCountReportBuilder.doText(str, cb, bf, 8, height, row, ZOutDrawerCountReportBuilder.getXForNumberStr(str, 200f)); row++;
			ZOutDrawerCountReportBuilder.doText("Count :", cb, bf, 8, height, row, 100f);
			str = ZOutDrawerCountReportBuilder.formatCurrency(_cash_out.getCountAmount(_workstation));
			ZOutDrawerCountReportBuilder.doText(str, cb, bf, 8, height, row, ZOutDrawerCountReportBuilder.getXForNumberStr(str, 200f)); row++;
			BigDecimal short_amount = _cash_out.getCountAmount(_workstation).subtract(net);
			ZOutDrawerCountReportBuilder.doText(short_amount.compareTo(CUBean.zero) == 1 ? "Over :" : "Short:", cb, bf, 8, height, row, 100f);
			str = ZOutDrawerCountReportBuilder.formatCurrency(short_amount); // how is this calculated???
			ZOutDrawerCountReportBuilder.doText(str, cb, bf, 8, height, row, ZOutDrawerCountReportBuilder.getXForNumberStr(str, 200f)); row++;
			ZOutDrawerCountReportBuilder.doText("Leave :", cb, bf, 8, height, row, 100f);
			str = ZOutDrawerCountReportBuilder.formatCurrency(_cash_out.getLeaveAmount(_workstation));
			ZOutDrawerCountReportBuilder.doText(str, cb, bf, 8, height, row, ZOutDrawerCountReportBuilder.getXForNumberStr(str, 200f)); row++;
			ZOutDrawerCountReportBuilder.doText("Deposit :", cb, bf, 8, height, row, 100f);
			str = ZOutDrawerCountReportBuilder.formatCurrency(_cash_out.getDepositAmount(_workstation));
			ZOutDrawerCountReportBuilder.doText(str, cb, bf, 8, height, row, ZOutDrawerCountReportBuilder.getXForNumberStr(str, 200f)); row++;


			BigDecimal count = null;
			BigDecimal one_hunded = new BigDecimal(100);
			BigDecimal fifty = new BigDecimal(50);
			BigDecimal twenty = new BigDecimal(20);
			BigDecimal ten = new BigDecimal(10);
			BigDecimal five = new BigDecimal(5);
			BigDecimal one = new BigDecimal(1);
			BigDecimal half_dollar = new BigDecimal(.50);
			BigDecimal quarter = new BigDecimal(.25);
			BigDecimal dime = new BigDecimal(.10);
			BigDecimal nickel = new BigDecimal(.05);
			BigDecimal penny = new BigDecimal(.01);

			ZOutDrawerCountReportBuilder.doText("100's", cb, bf, 9, height, row, left_margin);
			str = _cash_out.getCountHundreds(_workstation) + "";
			count = new BigDecimal(str);
			ZOutDrawerCountReportBuilder.doText(str, cb, bf, 8, height, row, ZOutDrawerCountReportBuilder.getXForNumberStr(str, 120f));
			str = ZOutDrawerCountReportBuilder.formatCurrency(one_hunded.multiply(count));
			ZOutDrawerCountReportBuilder.doText(str, cb, bf, 8, height, row, ZOutDrawerCountReportBuilder.getXForNumberStr(str, 200f)); row++;

			ZOutDrawerCountReportBuilder.doText("50's", cb, bf, 9, height, row, left_margin);
			str = _cash_out.getCountFifties(_workstation) + "";
			count = new BigDecimal(str);
			ZOutDrawerCountReportBuilder.doText(str, cb, bf, 8, height, row, ZOutDrawerCountReportBuilder.getXForNumberStr(str, 120f));
			str = ZOutDrawerCountReportBuilder.formatCurrency(fifty.multiply(count));
			ZOutDrawerCountReportBuilder.doText(str, cb, bf, 8, height, row, ZOutDrawerCountReportBuilder.getXForNumberStr(str, 200f)); row++;

			ZOutDrawerCountReportBuilder.doText("20's", cb, bf, 9, height, row, left_margin);
			str = _cash_out.getCountTwenties(_workstation) + "";
			count = new BigDecimal(str);
			ZOutDrawerCountReportBuilder.doText(str, cb, bf, 8, height, row, ZOutDrawerCountReportBuilder.getXForNumberStr(str, 120f));
			str = ZOutDrawerCountReportBuilder.formatCurrency(twenty.multiply(count));
			ZOutDrawerCountReportBuilder.doText(str, cb, bf, 8, height, row, ZOutDrawerCountReportBuilder.getXForNumberStr(str, 200f)); row++;

			ZOutDrawerCountReportBuilder.doText("10's", cb, bf, 9, height, row, left_margin);
			str = _cash_out.getCountTens(_workstation) + "";
			count = new BigDecimal(str);
			ZOutDrawerCountReportBuilder.doText(str, cb, bf, 8, height, row, ZOutDrawerCountReportBuilder.getXForNumberStr(str, 120f));
			str = ZOutDrawerCountReportBuilder.formatCurrency(ten.multiply(count));
			ZOutDrawerCountReportBuilder.doText(str, cb, bf, 8, height, row, ZOutDrawerCountReportBuilder.getXForNumberStr(str, 200f)); row++;

			ZOutDrawerCountReportBuilder.doText("5's", cb, bf, 9, height, row, left_margin);
			str = _cash_out.getCountFives(_workstation) + "";
			count = new BigDecimal(str);
			ZOutDrawerCountReportBuilder.doText(str, cb, bf, 8, height, row, ZOutDrawerCountReportBuilder.getXForNumberStr(str, 120f));
			str = ZOutDrawerCountReportBuilder.formatCurrency(five.multiply(count));
			ZOutDrawerCountReportBuilder.doText(str, cb, bf, 8, height, row, ZOutDrawerCountReportBuilder.getXForNumberStr(str, 200f)); row++;

			ZOutDrawerCountReportBuilder.doText("1's", cb, bf, 9, height, row, left_margin);
			str = _cash_out.getCountOnes(_workstation) + "";
			count = new BigDecimal(str);
			ZOutDrawerCountReportBuilder.doText(str, cb, bf, 8, height, row, ZOutDrawerCountReportBuilder.getXForNumberStr(str, 120f));
			str = ZOutDrawerCountReportBuilder.formatCurrency(one.multiply(count));
			ZOutDrawerCountReportBuilder.doText(str, cb, bf, 8, height, row, ZOutDrawerCountReportBuilder.getXForNumberStr(str, 200f)); row++;

			ZOutDrawerCountReportBuilder.doText(".50", cb, bf, 9, height, row, left_margin);
			str = _cash_out.getCountHalfDollars(_workstation) + "";
			count = new BigDecimal(str);
			ZOutDrawerCountReportBuilder.doText(str, cb, bf, 8, height, row, ZOutDrawerCountReportBuilder.getXForNumberStr(str, 120f));
			str = ZOutDrawerCountReportBuilder.formatCurrency(half_dollar.multiply(count));
			ZOutDrawerCountReportBuilder.doText(str, cb, bf, 8, height, row, ZOutDrawerCountReportBuilder.getXForNumberStr(str, 200f)); row++;

			ZOutDrawerCountReportBuilder.doText(".25", cb, bf, 9, height, row, left_margin);
			str = _cash_out.getCountQuarters(_workstation) + "";
			count = new BigDecimal(str);
			ZOutDrawerCountReportBuilder.doText(str, cb, bf, 8, height, row, ZOutDrawerCountReportBuilder.getXForNumberStr(str, 120f));
			str = ZOutDrawerCountReportBuilder.formatCurrency(quarter.multiply(count));
			ZOutDrawerCountReportBuilder.doText(str, cb, bf, 8, height, row, ZOutDrawerCountReportBuilder.getXForNumberStr(str, 200f)); row++;

			ZOutDrawerCountReportBuilder.doText(".10", cb, bf, 9, height, row, left_margin);
			str = _cash_out.getCountDimes(_workstation) + "";
			count = new BigDecimal(str);
			ZOutDrawerCountReportBuilder.doText(str, cb, bf, 8, height, row, ZOutDrawerCountReportBuilder.getXForNumberStr(str, 120f));
			str = ZOutDrawerCountReportBuilder.formatCurrency(dime.multiply(count));
			ZOutDrawerCountReportBuilder.doText(str, cb, bf, 8, height, row, ZOutDrawerCountReportBuilder.getXForNumberStr(str, 200f)); row++;

			ZOutDrawerCountReportBuilder.doText(".05", cb, bf, 9, height, row, left_margin);
			str = _cash_out.getCountNickels(_workstation) + "";
			count = new BigDecimal(str);
			ZOutDrawerCountReportBuilder.doText(str, cb, bf, 8, height, row, ZOutDrawerCountReportBuilder.getXForNumberStr(str, 120f));
			str = ZOutDrawerCountReportBuilder.formatCurrency(nickel.multiply(count));
			ZOutDrawerCountReportBuilder.doText(str, cb, bf, 8, height, row, ZOutDrawerCountReportBuilder.getXForNumberStr(str, 200f)); row++;

			ZOutDrawerCountReportBuilder.doText(".01", cb, bf, 9, height, row, left_margin);
			str = _cash_out.getCountPennies(_workstation) + "";
			count = new BigDecimal(str);
			ZOutDrawerCountReportBuilder.doText(str, cb, bf, 8, height, row, ZOutDrawerCountReportBuilder.getXForNumberStr(str, 120f));
			str = ZOutDrawerCountReportBuilder.formatCurrency(penny.multiply(count));
			ZOutDrawerCountReportBuilder.doText(str, cb, bf, 8, height, row, ZOutDrawerCountReportBuilder.getXForNumberStr(str, 200f)); row++;

			row++;



			ZOutDrawerCountReportBuilder.doText("Check", cb, bf, 9, height, row, left_margin);
			ZOutDrawerCountReportBuilder.doText("Paid In :", cb, bf, 8, height, row, 100f);
			str = ZOutDrawerCountReportBuilder.formatCurrency(check_in);
			ZOutDrawerCountReportBuilder.doText(str, cb, bf, 8, height, row, ZOutDrawerCountReportBuilder.getXForNumberStr(str, 200f)); row++;
			ZOutDrawerCountReportBuilder.doText("Paid Out :", cb, bf, 8, height, row, 100f);
			str = ZOutDrawerCountReportBuilder.formatCurrency(check_out);
			ZOutDrawerCountReportBuilder.doText(str, cb, bf, 8, height, row, ZOutDrawerCountReportBuilder.getXForNumberStr(str, 200f)); row++;
			ZOutDrawerCountReportBuilder.doText("Net :", cb, bf, 8, height, row, 100f);
			str = ZOutDrawerCountReportBuilder.formatCurrency(check_in.subtract(check_out));
			ZOutDrawerCountReportBuilder.doText(str, cb, bf, 8, height, row, ZOutDrawerCountReportBuilder.getXForNumberStr(str, 200f)); row++;

			ZOutDrawerCountReportBuilder.doText("All Credit/Debit Cards", cb, bf, 9, height, row, left_margin);
			ZOutDrawerCountReportBuilder.doText("Paid In :", cb, bf, 8, height, row, 100f);
			str = ZOutDrawerCountReportBuilder.formatCurrency(credit_card_in);
			ZOutDrawerCountReportBuilder.doText(str, cb, bf, 8, height, row, ZOutDrawerCountReportBuilder.getXForNumberStr(str, 200f)); row++;
			ZOutDrawerCountReportBuilder.doText("Paid Out :", cb, bf, 8, height, row, 100f);
			str = ZOutDrawerCountReportBuilder.formatCurrency(credit_card_out);
			ZOutDrawerCountReportBuilder.doText(str, cb, bf, 8, height, row, ZOutDrawerCountReportBuilder.getXForNumberStr(str, 200f)); row++;
			ZOutDrawerCountReportBuilder.doText("Net :", cb, bf, 8, height, row, 100f);
			str = ZOutDrawerCountReportBuilder.formatCurrency(credit_card_in.subtract(credit_card_out));
			ZOutDrawerCountReportBuilder.doText(str, cb, bf, 8, height, row, ZOutDrawerCountReportBuilder.getXForNumberStr(str, 200f)); row++;

			ZOutDrawerCountReportBuilder.doText("Account", cb, bf, 9, height, row, left_margin);
			ZOutDrawerCountReportBuilder.doText("Paid In :", cb, bf, 8, height, row, 100f);
			str = ZOutDrawerCountReportBuilder.formatCurrency(account_in);
			ZOutDrawerCountReportBuilder.doText(str, cb, bf, 8, height, row, ZOutDrawerCountReportBuilder.getXForNumberStr(str, 200f)); row++;
			ZOutDrawerCountReportBuilder.doText("Paid Out :", cb, bf, 8, height, row, 100f);
			str = ZOutDrawerCountReportBuilder.formatCurrency(account_out);
			ZOutDrawerCountReportBuilder.doText(str, cb, bf, 8, height, row, ZOutDrawerCountReportBuilder.getXForNumberStr(str, 200f)); row++;
			ZOutDrawerCountReportBuilder.doText("Net :", cb, bf, 8, height, row, 100f);
			str = ZOutDrawerCountReportBuilder.formatCurrency(account_in.subtract(account_out));
			ZOutDrawerCountReportBuilder.doText(str, cb, bf, 8, height, row, ZOutDrawerCountReportBuilder.getXForNumberStr(str, 200f)); row++;

			row++;

			BigDecimal listing_total = CUBean.zero;
			if (credit_card_tenders.size() > 0)
			{
				ZOutDrawerCountReportBuilder.doText("Credit Card Listing - " + credit_card_tenders.size(), cb, bf, 9, height, row, left_margin); row++;
				Iterator itr = credit_card_tenders.iterator();
				while (itr.hasNext())
				{
					TenderRet credit_card_tender = (TenderRet)itr.next();
					UKOnlinePersonBean client = ZOutDrawerCountReportBuilder.getClientForTender(credit_card_tender);
					ZOutDrawerCountReportBuilder.doText(((credit_card_tender.getOrders().size() > 1) ? "*" : "") + " " + client.getLabel(), cb, bf, 8, height, row, 75f);
					str = ZOutDrawerCountReportBuilder.formatCurrency(credit_card_tender.getAmountBigDecimal());
					ZOutDrawerCountReportBuilder.doText(str, cb, bf, 8, height, row, ZOutDrawerCountReportBuilder.getXForNumberStr(str, 200f)); row++;
					listing_total = listing_total.add(credit_card_tender.getAmountBigDecimal());
				}
				ZOutDrawerCountReportBuilder.doText("Credit Card Total ", cb, bf, 9, height, row, left_margin);
				str = ZOutDrawerCountReportBuilder.formatCurrency(listing_total);
				ZOutDrawerCountReportBuilder.doText(str, cb, bf, 8, height, row, ZOutDrawerCountReportBuilder.getXForNumberStr(str, 200f)); row++;
			}

			listing_total = CUBean.zero;
			if (check_tenders.size() > 0)
			{
				ZOutDrawerCountReportBuilder.doText("Check Listing - " + check_tenders.size(), cb, bf, 9, height, row, left_margin); row++;
				Iterator itr = check_tenders.iterator();
				while (itr.hasNext())
				{
					TenderRet check_tender = (TenderRet)itr.next();
					UKOnlinePersonBean client = ZOutDrawerCountReportBuilder.getClientForTender(check_tender);
					ZOutDrawerCountReportBuilder.doText(((check_tender.getOrders().size() > 1) ? "*" : "") + check_tender.getCheckNumber() + " " + client.getLabel(), cb, bf, 8, height, row, 75f);
					str = ZOutDrawerCountReportBuilder.formatCurrency(check_tender.getAmountBigDecimal());
					ZOutDrawerCountReportBuilder.doText(str, cb, bf, 8, height, row, ZOutDrawerCountReportBuilder.getXForNumberStr(str, 200f)); row++;
					listing_total = listing_total.add(check_tender.getAmountBigDecimal());
				}
				ZOutDrawerCountReportBuilder.doText("Check Total ", cb, bf, 9, height, row, left_margin);
				str = ZOutDrawerCountReportBuilder.formatCurrency(listing_total);
				ZOutDrawerCountReportBuilder.doText(str, cb, bf, 8, height, row, ZOutDrawerCountReportBuilder.getXForNumberStr(str, 200f)); row++;
			}

			listing_total = CUBean.zero;
			if (cash_tenders.size() > 0)
			{
				ZOutDrawerCountReportBuilder.doText("Cash Listing - " + cash_tenders.size(), cb, bf, 9, height, row, left_margin); row++;
				Iterator itr = cash_tenders.iterator();
				while (itr.hasNext())
				{
					TenderRet cash_tender = (TenderRet)itr.next();
					UKOnlinePersonBean client = ZOutDrawerCountReportBuilder.getClientForTender(cash_tender);
					ZOutDrawerCountReportBuilder.doText(((cash_tender.getOrders().size() > 1) ? "*" : "") + " " + client.getLabel(), cb, bf, 8, height, row, 75f);
					str = ZOutDrawerCountReportBuilder.formatCurrency(cash_tender.getChangeAmountBigDecimal());
					ZOutDrawerCountReportBuilder.doText(str, cb, bf, 8, height, row, ZOutDrawerCountReportBuilder.getXForNumberStr(str, 155f));
					str = ZOutDrawerCountReportBuilder.formatCurrency(cash_tender.getTenderAmountBigDecimal());
					ZOutDrawerCountReportBuilder.doText(str, cb, bf, 8, height, row, ZOutDrawerCountReportBuilder.getXForNumberStr(str, 200f)); row++;
					listing_total = listing_total.add(cash_tender.getTenderAmountBigDecimal().subtract(cash_tender.getChangeAmountBigDecimal()));
				}
				ZOutDrawerCountReportBuilder.doText("Cash Total ", cb, bf, 9, height, row, left_margin);
				str = ZOutDrawerCountReportBuilder.formatCurrency(listing_total);
				ZOutDrawerCountReportBuilder.doText(str, cb, bf, 8, height, row, ZOutDrawerCountReportBuilder.getXForNumberStr(str, 200f)); row++;
			}

			listing_total = CUBean.zero;
			if (account_tenders.size() > 0 || payment_on_account_tenders.size() > 0)
			{
				ZOutDrawerCountReportBuilder.doText("Account Listing - " + account_tenders.size(), cb, bf, 9, height, row, left_margin); row++;
				Iterator itr = account_tenders.iterator();
				while (itr.hasNext())
				{
					TenderRet account_tender = (TenderRet)itr.next();
					UKOnlinePersonBean client = ZOutDrawerCountReportBuilder.getClientForTender(account_tender);
					ZOutDrawerCountReportBuilder.doText(((account_tender.getOrders().size() > 1) ? "*" : "") + " " + client.getLabel(), cb, bf, 8, height, row, 75f);
					str = ZOutDrawerCountReportBuilder.formatCurrency(account_tender.getAmountBigDecimal());
					ZOutDrawerCountReportBuilder.doText(str, cb, bf, 8, height, row, ZOutDrawerCountReportBuilder.getXForNumberStr(str, 200f)); row++;
					listing_total = listing_total.add(account_tender.getAmountBigDecimal());
				}
				itr = payment_on_account_tenders.iterator();
				while (itr.hasNext())
				{
					TenderRet account_tender = (TenderRet)itr.next();
					UKOnlinePersonBean client = ZOutDrawerCountReportBuilder.getClientForTender(account_tender);
					ZOutDrawerCountReportBuilder.doText(((account_tender.getOrders().size() > 1) ? "*" : "") + " " + client.getLabel(), cb, bf, 8, height, row, 75f);
					BigDecimal payment_on_account_tender = account_tender.getAmountBigDecimal().multiply(negative_one);
					str = ZOutDrawerCountReportBuilder.formatCurrency(payment_on_account_tender);
					ZOutDrawerCountReportBuilder.doText(str, cb, bf, 8, height, row, ZOutDrawerCountReportBuilder.getXForNumberStr(str, 200f)); row++;
					listing_total = listing_total.add(payment_on_account_tender);
				}
				ZOutDrawerCountReportBuilder.doText("Account Total ", cb, bf, 9, height, row, left_margin);
				str = ZOutDrawerCountReportBuilder.formatCurrency(listing_total);
				ZOutDrawerCountReportBuilder.doText(str, cb, bf, 8, height, row, ZOutDrawerCountReportBuilder.getXForNumberStr(str, 200f)); row++;
			}

			listing_total = CUBean.zero;
			if (gift_card_tenders.size() > 0)
			{
				ZOutDrawerCountReportBuilder.doText("Gift Card Listing - " + gift_card_tenders.size(), cb, bf, 9, height, row, left_margin); row++;
				Iterator itr = gift_card_tenders.iterator();
				while (itr.hasNext())
				{
					TenderRet gift_card_tender = (TenderRet)itr.next();
					UKOnlinePersonBean client = ZOutDrawerCountReportBuilder.getClientForTender(gift_card_tender);
					ZOutDrawerCountReportBuilder.doText(((gift_card_tender.getOrders().size() > 1) ? "*" : "") + " " + client.getLabel(), cb, bf, 8, height, row, 75f);
					str = ZOutDrawerCountReportBuilder.formatCurrency(gift_card_tender.getAmountBigDecimal());
					ZOutDrawerCountReportBuilder.doText(str, cb, bf, 8, height, row, ZOutDrawerCountReportBuilder.getXForNumberStr(str, 200f)); row++;
					listing_total = listing_total.add(gift_card_tender.getAmountBigDecimal());
				}
				ZOutDrawerCountReportBuilder.doText("Gift Card Total ", cb, bf, 9, height, row, left_margin);
				str = ZOutDrawerCountReportBuilder.formatCurrency(listing_total);
				ZOutDrawerCountReportBuilder.doText(str, cb, bf, 8, height, row, ZOutDrawerCountReportBuilder.getXForNumberStr(str, 200f)); row++;
			}

			listing_total = CUBean.zero;
			if (gift_certificate_tenders.size() > 0)
			{
				ZOutDrawerCountReportBuilder.doText("Gift Certificate Listing - " + gift_certificate_tenders.size(), cb, bf, 9, height, row, left_margin); row++;
				Iterator itr = gift_certificate_tenders.iterator();
				while (itr.hasNext())
				{
					TenderRet gift_certificate_tender = (TenderRet)itr.next();
					UKOnlinePersonBean client = ZOutDrawerCountReportBuilder.getClientForTender(gift_certificate_tender);
					ZOutDrawerCountReportBuilder.doText(((gift_certificate_tender.getOrders().size() > 1) ? "*" : "") + " " + client.getLabel(), cb, bf, 8, height, row, 75f);
					str = ZOutDrawerCountReportBuilder.formatCurrency(gift_certificate_tender.getAmountBigDecimal());
					ZOutDrawerCountReportBuilder.doText(str, cb, bf, 8, height, row, ZOutDrawerCountReportBuilder.getXForNumberStr(str, 200f)); row++;
					listing_total = listing_total.add(gift_certificate_tender.getAmountBigDecimal());
				}
				ZOutDrawerCountReportBuilder.doText("Gift Certificate Total ", cb, bf, 9, height, row, left_margin);
				str = ZOutDrawerCountReportBuilder.formatCurrency(listing_total);
				ZOutDrawerCountReportBuilder.doText(str, cb, bf, 8, height, row, ZOutDrawerCountReportBuilder.getXForNumberStr(str, 200f)); row++;
			}


			/*
			String address_1_str = "470 West 78th Street #120";

			cb.beginText();
			cb.setFontAndSize(bf, 8);
			cb.setColorFill(new Color(0x00, 0x00, 0x00));
			cb.showTextAligned(PdfContentByte.ALIGN_LEFT, address_1_str, getCenterForString(width, address_1_str), getHeightForRow(height, row++), 0f);
			cb.endText();

			String address_2_str = "Chanhassen, MN 55317";

			cb.beginText();
			cb.setFontAndSize(bf, 8);
			cb.setColorFill(new Color(0x00, 0x00, 0x00));
			cb.showTextAligned(PdfContentByte.ALIGN_LEFT, address_2_str, getCenterForString(width, address_1_str), getHeightForRow(height, row++), 0f);
			cb.endText();
			 *
			 */



			//int rows_consumed = ZOutDrawerCountReportBuilder.doParagraph("You will need to keep your receipts for insurance purposes.", 34, (int)getHeightForRow(height, row), 200, cb, bf, 8, (int)height_per_row, 3);



            // step 5: we close the document
            document.close();


		}
		catch (Throwable e)
		{
			e.printStackTrace();
		}

		return file;

	} // generateClientAppointmentWorksheet

	private static UKOnlinePersonBean
	getClientForTender(TenderRet _tender)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException
	{
		UKOnlinePersonBean client_for_tender = _tender.getClient();
		if (client_for_tender != null)
			return client_for_tender;
		
		Vector orders_for_tender = _tender.getOrders();
		
		for (int x = 0 ; x < orders_for_tender.size(); x++)
		{
			ValeoOrderBean order_for_tender = (ValeoOrderBean)orders_for_tender.elementAt(x);
			UKOnlinePersonBean client = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(order_for_tender.getPersonId());
			if (client_for_tender == null)
				client_for_tender = client;
			else if (!client_for_tender.equals(client))
				throw new IllegalValueException("Multiple clients for tender.");
		}
		return client_for_tender;
	}


	 private static String formatCurrency(BigDecimal b)
	 {
		  NumberFormat n = NumberFormat.getCurrencyInstance(Locale.US);
		  double doublePayment = b.doubleValue();
		  String s;
		  if(doublePayment < 0)
		  {
			  s = n.format(doublePayment * -1);
			  s  = "(" + s.substring(1) + ")";
			  return s;
		  }
		  else
		 {
			  s = n.format(doublePayment);
			  return s.substring(1);
		  }

	 }

	private static float
	getHeightForRow(float _height, int _row)
	{
		float height_for_row = _height - (height_per_row * _row);
		return height_for_row;
	}

	private static float
	getCenterForString(float _width, String _str)
	{
		float x = (_width / 2) - (2f * _str.length());
		if (x > left_margin)
			return x;
		return left_margin;
	}

	private static float
	getXForNumberStrX(String _str, float _baseline)
	{
		return _baseline - (_str.substring(0, _str.indexOf('.')).length() * 4);
	}

	private static float
	getXForNumberStr(String _str, float _baseline)
	{
		int str_len = 0;
		if (_str.indexOf('.') > -1)
		{
			if (_str.charAt(0) == '(')
				str_len = _str.substring(1, _str.indexOf('.')).length();
			else
				str_len = _str.substring(0, _str.indexOf('.')).length();
			switch (str_len)
			{
				case 1: return _baseline; // ones
				case 2: return _baseline - 4; // tens
				case 3: return _baseline - 8; // hundreds
				case 5: return _baseline - 14; // thousands (including comma)
				case 6: return _baseline - 18; // ten thousand
				case 7: return _baseline - 22; // hundred thousand
				case 9: return _baseline - 28; // million (including comma)
				case 10: return _baseline - 32;
				case 11: return _baseline - 36;
				case 13: return _baseline - 42;
			}
		}
		else
		{
			str_len = _str.length();
			switch (str_len)
			{
				case 1: return _baseline; // ones
				case 2: return _baseline - 4; // tens
				case 3: return _baseline - 8; // hundreds
				case 4: return _baseline - 12; // thousands
				case 5: return _baseline - 16; // ten thousand
				case 6: return _baseline - 20; // hundred thousand
				case 7: return _baseline - 24;
				case 8: return _baseline - 32;
			}
		}

		return _baseline;
	}

	private static int
	doParagraph(String _str, int _x, int _y, int _width, PdfContentByte _over, BaseFont _bf, int _font_size, int _space_between_rows, int _max_rows)
	{
	    int num_chars = _width / 4;
	    int space_between_rows = _space_between_rows;
	    int row = 0;
	    int pos = 0;

	    boolean done = false;

	    while (!done)
	    {
			String string_to_print = "";

			if (num_chars < (_str.length() - pos))
			{
				string_to_print = _str.substring(pos, pos + num_chars);

				if (string_to_print.indexOf(' ') > -1)
				string_to_print = string_to_print.substring(0, string_to_print.lastIndexOf(' '));
			}
			else
			{
				string_to_print = _str.substring(pos);
				if (string_to_print.endsWith(", "))
				string_to_print = string_to_print.substring(0, string_to_print.length() - 2);
				done = true;
			}


			_over.beginText();
			_over.setFontAndSize(_bf, _font_size);
			_over.showTextAligned(PdfContentByte.ALIGN_LEFT, string_to_print.trim(), _x , _y - (row * space_between_rows), 0f);
			_over.endText();

			row++;
			if (row == _max_rows)
				return row;

			pos = pos + string_to_print.length();
	    }

		return row;
	}

	private static void
	doText(String _text, PdfContentByte _cb, BaseFont _bf, int _font_size, float _height, int _row, float _x)
	{
		_cb.beginText();
		_cb.setFontAndSize(_bf, _font_size);
		_cb.setColorFill(new Color(0x00, 0x00, 0x00));
		_cb.showTextAligned(PdfContentByte.ALIGN_LEFT, _text, _x, getHeightForRow(_height, _row), 0f);
		_cb.endText();
	}

} // ClientAppointmentBuilder



