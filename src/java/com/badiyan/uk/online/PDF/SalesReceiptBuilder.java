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

public class
SalesReceiptBuilder
{
	private static float abs_pos_x = 40f;
	private static float abs_pos_y = 20f;

	private static float appt_start_y = 670;

	private static float height_per_row = 10f;

	private static float left_margin = 5f;
	private static float qty_margin = 160f;
	private static float amount_margin = 190f;


	private static float space_per_number = 7f;


	/**
	 * Manages the creation of the printed plan's individual pages.
	 * @param args
	 */
	public static String
	generateSalesReceipt(UKOnlineCompanyBean _company,
			UKOnlinePersonBean _cashier,
			UKOnlinePersonBean _client,
			ValeoOrderBean _adminOrder,
			Vector<ValeoOrderBean> _previous_orders,
			Vector<TenderRet> _tenders,
			BigDecimal _subtotal,
			BigDecimal _tax,
			BigDecimal _total,
			BigDecimal _total_discount_products,
			BigDecimal _total_discount_services,
			BigDecimal _discount_percentage_products,
			BigDecimal _discount_percentage_services,
			BigDecimal _shipping)
		throws IllegalValueException
	{
		String realPath = CUBean.getProperty("cu.realPath");
		String resourcesFolder = CUBean.getProperty("cu.resourcesFolder");
		if (resourcesFolder == null)
			throw new IllegalValueException("Property not defined: cu.resourcesFolder");
		resourcesFolder = realPath + resourcesFolder;

		String file = "sales-receipt-" + System.currentTimeMillis() + ".pdf";
		String pdf_file = resourcesFolder + "pdf/" + file;

		String pdf_file_source = resourcesFolder + "pdf/practitioner-appointments.pdf";

		if (_company.getId() == 1)
			pdf_file_source = resourcesFolder + "pdf/practitioner-appointments-valeo.pdf";

		int appointments_per_page = 53;

		try
		{

			// we create a reader for a certain document
            PdfReader reader = new PdfReader(pdf_file_source);
            // we retrieve the total number of pages
            int n = reader.getNumberOfPages();

			//Vector appointments = AppointmentBean.getAppointmentsForPractitioner(_practitioner, _report_date);
			//Iterator appt_itr = appointments.iterator();

			//n = appointments.size() / appointments_per_page;
			//if ((appointments.size() % appointments_per_page) > 0)
			//	n++;

            // we retrieve the size of the first page
            Rectangle psize = reader.getPageSize(1);

            float width = psize.width();
            float height = psize.height();

			System.out.println("width >" + width);
			System.out.println("height >" + height);

			width = 234f;
			height = 520f;

            // step 1: creation of a document-object
            Document document = new Document(new Rectangle(width, height));
            // step 2: we create a writer that listens to the document
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(pdf_file));
            // step 3: we open the document
            document.open();
            // step 4: we add content
            PdfContentByte cb = writer.getDirectContent();
            int i = 0;
            int p = 0;
            if (i < n) {

                document.newPage();
                p++;
                i++;
                //PdfImportedPage page1 = writer.getImportedPage(reader, 1);
				//cb.addTemplate(page1, 0, 0);

                BaseFont bf = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);

				int row = 3;
				
				Date order_date = _adminOrder.getOrderDate();
				if (order_date == null)
					order_date = new Date();

				cb.beginText();
				cb.setFontAndSize(bf, 8);
				cb.setColorFill(new Color(0x00, 0x00, 0x00));
				cb.showTextAligned(PdfContentByte.ALIGN_LEFT, CUBean.getUserDateString(order_date) + " " + CUBean.getUserTimeString(order_date), left_margin, getHeightForRow(height, row), 0f);
				cb.endText();

				boolean is_payment_on_account = false;
				if (_adminOrder.isNew())
				{
					cb.beginText();
					cb.setFontAndSize(bf, 8);
					cb.setColorFill(new Color(0x00, 0x00, 0x00));
					cb.showTextAligned(PdfContentByte.ALIGN_LEFT, "Payment on Account", 115f, getHeightForRow(height, row++), 0f);
					cb.endText();
					
					is_payment_on_account = true;
				}
				else
				{
					cb.beginText();
					cb.setFontAndSize(bf, 8);
					cb.setColorFill(new Color(0x00, 0x00, 0x00));
					cb.showTextAligned(PdfContentByte.ALIGN_LEFT, "Sales Receipt #" + _adminOrder.getValue(), 115f, getHeightForRow(height, row++), 0f);
					cb.endText();
				}

				row++;

				cb.beginText();
				cb.setFontAndSize(bf, 12);
				cb.setColorFill(new Color(0x00, 0x00, 0x00));
				cb.showTextAligned(PdfContentByte.ALIGN_LEFT, _company.getLabel(), left_margin, getHeightForRow(height, row++), 0f);
				cb.endText();

				String address_1_str = "";
				
				AddressBean practice_address;
				try
				{
					practice_address = _company.getAddress(AddressBean.PRACTICE_ADDRESS_TYPE);
				}
				catch (Exception x)
				{
					practice_address = new AddressBean();
					practice_address.setType(AddressBean.PRACTICE_ADDRESS_TYPE);
				}
				
				address_1_str = practice_address.getStreet1String();

				cb.beginText();
				cb.setFontAndSize(bf, 8);
				cb.setColorFill(new Color(0x00, 0x00, 0x00));
				cb.showTextAligned(PdfContentByte.ALIGN_LEFT, address_1_str, getCenterForString(width, address_1_str), getHeightForRow(height, row++), 0f);
				cb.endText();

				String address_2_str = practice_address.getCityString() + ", " + practice_address.getStateString() + " " + practice_address.getZipCodeString();

				cb.beginText();
				cb.setFontAndSize(bf, 8);
				cb.setColorFill(new Color(0x00, 0x00, 0x00));
				cb.showTextAligned(PdfContentByte.ALIGN_LEFT, address_2_str, getCenterForString(width, address_1_str), getHeightForRow(height, row++), 0f);
				cb.endText();

				cb.beginText();
				cb.setFontAndSize(bf, 10);
				cb.setColorFill(new Color(0x00, 0x00, 0x00));
				cb.showTextAligned(PdfContentByte.ALIGN_LEFT, "Bill To:", left_margin, getHeightForRow(height, row++), 0f);
				cb.endText();

				cb.beginText();
				cb.setFontAndSize(bf, 8);
				cb.setColorFill(new Color(0x00, 0x00, 0x00));
				cb.showTextAligned(PdfContentByte.ALIGN_LEFT, _client.getLabel(), left_margin, getHeightForRow(height, row++), 0f);
				cb.endText();

				try
				{
					AddressBean address = AddressBean.getAddress(_client, AddressBean.PERSON_ADDRESS_TYPE);

					cb.beginText();
					cb.setFontAndSize(bf, 8);
					cb.setColorFill(new Color(0x00, 0x00, 0x00));
					cb.showTextAligned(PdfContentByte.ALIGN_LEFT, address.getStreet1String(), left_margin, getHeightForRow(height, row++), 0f);
					cb.endText();

					cb.beginText();
					cb.setFontAndSize(bf, 8);
					cb.setColorFill(new Color(0x00, 0x00, 0x00));
					cb.showTextAligned(PdfContentByte.ALIGN_LEFT, address.getCityString() + ", " + address.getStateString() + " " + address.getZipCodeString(), left_margin, getHeightForRow(height, row++), 0f);
					cb.endText();
				}
				catch (ObjectNotFoundException x)
				{

				}
				
				String balance_str = _client.getBalanceString();

				/*
				try
				{
					if (is_payment_on_account)
					{
						BigDecimal balance_bd = _client.getBalance();
						BigDecimal total_tendered = BigDecimal.ZERO;
						Iterator itr = _tenders.iterator();
						while (itr.hasNext())
						{
							TenderRet tender = (TenderRet)itr.next();
							total_tendered = total_tendered.add(tender.getAmountBigDecimal());
						}
						balance_bd = balance_bd.subtract(total_tendered);
						balance_str = balance_bd.setScale(2, BigDecimal.ROUND_HALF_UP).toString();
					}
				}
				catch (Exception x)
				{
				}
				 */

				cb.beginText();
				cb.setFontAndSize(bf, 8);
				cb.setColorFill(new Color(0x00, 0x00, 0x00));
				cb.showTextAligned(PdfContentByte.ALIGN_LEFT, "Balance: " + balance_str, left_margin, getHeightForRow(height, row++), 0f);
				cb.endText();

				cb.beginText();
				cb.setFontAndSize(bf, 8);
				cb.setColorFill(new Color(0x00, 0x00, 0x00));
				cb.showTextAligned(PdfContentByte.ALIGN_LEFT, "Cashier: " + _cashier.getLabel(), left_margin, getHeightForRow(height, row++), 0f);
				cb.endText();

				row++;

				cb.beginText();
				cb.setFontAndSize(bf, 9);
				cb.setColorFill(new Color(0x00, 0x00, 0x00));
				cb.showTextAligned(PdfContentByte.ALIGN_LEFT, "Description", left_margin, getHeightForRow(height, row), 0f);
				cb.endText();
				cb.beginText();
				cb.setFontAndSize(bf, 9);
				cb.setColorFill(new Color(0x00, 0x00, 0x00));
				cb.showTextAligned(PdfContentByte.ALIGN_LEFT, "Qty", qty_margin, getHeightForRow(height, row), 0f);
				cb.endText();
				cb.beginText();
				cb.setFontAndSize(bf, 9);
				cb.setColorFill(new Color(0x00, 0x00, 0x00));
				cb.showTextAligned(PdfContentByte.ALIGN_LEFT, "Amount", amount_margin, getHeightForRow(height, row), 0f);
				cb.endText();

				cb.moveTo(left_margin, getHeightForRow(height, row) - 2);
				cb.lineTo(width - left_margin, getHeightForRow(height, row) - 2);
				cb.stroke();

				if (_adminOrder.isNew()) {
					Iterator tender_itr = _tenders.iterator();
					while (tender_itr.hasNext()) {
						row++;
						
						TenderRet tender_for_previous_order = (TenderRet)tender_itr.next();
						Iterator orders_for_tender_itr = tender_for_previous_order.getOrders().iterator();
						while (orders_for_tender_itr.hasNext())
						{
							ValeoOrderBean order_for_tender = (ValeoOrderBean)orders_for_tender_itr.next();
							String tender_amount_mapped_to_order_str = tender_for_previous_order.getAmountMappedToOrder(order_for_tender).toString();

							String description = tender_for_previous_order.getType() + " Payment on Account (Order #" + order_for_tender.getValue() + ")";
							int rows_consumed = SalesReceiptBuilder.doParagraph(description, (int)left_margin, (int)getHeightForRow(height, row), 140, cb, bf, 8, (int)height_per_row, 3);

							cb.beginText();
							cb.setFontAndSize(bf, 8);
							cb.setColorFill(new Color(0x00, 0x00, 0x00));
							cb.showTextAligned(PdfContentByte.ALIGN_LEFT, tender_amount_mapped_to_order_str, getXForNumberStr(tender_amount_mapped_to_order_str), getHeightForRow(height, row), 0f);
							cb.endText();

							row += (rows_consumed - 1);
						}
						
					}
					
					row--;
					
					//int rows_consumed = SalesReceiptBuilder.doParagraph("Payment on Account", (int)left_margin, (int)getHeightForRow(height, row), 140, cb, bf, 8, (int)height_per_row, 3);
					//row += (rows_consumed - 1);
				}
				else
				{
					Iterator itr = _adminOrder.getOrdersVec().iterator();
					while (itr.hasNext())
					{
						row++;
						CheckoutOrderline orderline = (CheckoutOrderline)itr.next();
						CheckoutCodeBean code = CheckoutCodeBean.getCheckoutCode(orderline.getCheckoutCodeId());
						String description = code.getLabel();
						String qty = orderline.getQuantity().setScale(2, BigDecimal.ROUND_HALF_UP).toString();
						//String amount = orderline.getPrice().setScale(2, BigDecimal.ROUND_HALF_UP).toString();
						String orderline_subtotal_str = orderline.getQuantity().multiply(orderline.getPrice()).setScale(2, BigDecimal.ROUND_HALF_UP).toString();

						/*
						cb.beginText();
						cb.setFontAndSize(bf, 8);
						cb.setColorFill(new Color(0x00, 0x00, 0x00));
						cb.showTextAligned(PdfContentByte.ALIGN_LEFT, description, left_margin, getHeightForRow(height, row), 0f);
						cb.endText();
						 */


						if (code.getType() == CheckoutCodeBean.GIFT_CARD || code.getType() == CheckoutCodeBean.GIFT_CERTIFICATE)
						{
							try
							{
								GiftCard card_or_cert = GiftCard.getGiftCard(_adminOrder);
								description += (" #" + card_or_cert.getCardNumberString());
							}
							catch (Exception x)
							{
								x.printStackTrace();
							}
						}

						int rows_consumed = SalesReceiptBuilder.doParagraph(description, (int)left_margin, (int)getHeightForRow(height, row), 140, cb, bf, 8, (int)height_per_row, 3);

						cb.beginText();
						cb.setFontAndSize(bf, 8);
						cb.setColorFill(new Color(0x00, 0x00, 0x00));
						cb.showTextAligned(PdfContentByte.ALIGN_LEFT, qty, qty_margin, getHeightForRow(height, row), 0f);
						cb.endText();
						cb.beginText();
						cb.setFontAndSize(bf, 8);
						cb.setColorFill(new Color(0x00, 0x00, 0x00));
						//cb.showTextAligned(PdfContentByte.ALIGN_LEFT, amount, getXForNumberStr(amount), getHeightForRow(height, row), 0f);
						cb.showTextAligned(PdfContentByte.ALIGN_LEFT, orderline_subtotal_str, getXForNumberStr(orderline_subtotal_str), getHeightForRow(height, row), 0f);
						cb.endText();

						row += (rows_consumed - 1);
					}
				}

				
				Iterator itr = _previous_orders.iterator();
				while (itr.hasNext())
				{
					row++;
					ValeoOrderBean previous_order = (ValeoOrderBean)itr.next();
					Iterator tender_itr = TenderRet.getTenders(_adminOrder).iterator();
					while (tender_itr.hasNext())
					{
						TenderRet tender_for_previous_order = (TenderRet)tender_itr.next();
						String tender_amount_mapped_to_order_str = tender_for_previous_order.getAmountMappedToOrder(previous_order).toString();
						
						String description = tender_for_previous_order.getType() + " Payment on Account (Order #" + previous_order.getValue() + ")";
						int rows_consumed = SalesReceiptBuilder.doParagraph(description, (int)left_margin, (int)getHeightForRow(height, row), 140, cb, bf, 8, (int)height_per_row, 3);
						
						cb.beginText();
						cb.setFontAndSize(bf, 8);
						cb.setColorFill(new Color(0x00, 0x00, 0x00));
						cb.showTextAligned(PdfContentByte.ALIGN_LEFT, tender_amount_mapped_to_order_str, getXForNumberStr(tender_amount_mapped_to_order_str), getHeightForRow(height, row), 0f);
						cb.endText();
						
						row += (rows_consumed - 1);
						
					}
					
					//String description = "Payment on Account (Order #" + previous_order.getValue() + ")";
					//int rows_consumed = SalesReceiptBuilder.doParagraph(description, (int)left_margin, (int)getHeightForRow(height, row), 140, cb, bf, 8, (int)height_per_row, 3);
					
				}


				cb.moveTo(left_margin, getHeightForRow(height, row) - 2);
				cb.lineTo(width - left_margin, getHeightForRow(height, row) - 2);
				cb.stroke();

				row++;

				cb.beginText();
				cb.setFontAndSize(bf, 8);
				cb.setColorFill(new Color(0x00, 0x00, 0x00));
				cb.showTextAligned(PdfContentByte.ALIGN_LEFT, "SubTotal:", 150, getHeightForRow(height, row), 0f);
				cb.endText();
				cb.beginText();
				cb.setFontAndSize(bf, 8);
				cb.setColorFill(new Color(0x00, 0x00, 0x00));
				cb.showTextAligned(PdfContentByte.ALIGN_LEFT, _subtotal.setScale(2, BigDecimal.ROUND_HALF_UP).toString(), getXForNumberStr(_subtotal.setScale(2, BigDecimal.ROUND_HALF_UP).toString()) , getHeightForRow(height, row), 0f);
				cb.endText();

				row++;

				if (_shipping.compareTo(BigDecimal.ZERO) == 1)
				{
					cb.beginText();
					cb.setFontAndSize(bf, 8);
					cb.setColorFill(new Color(0x00, 0x00, 0x00));
					cb.showTextAligned(PdfContentByte.ALIGN_LEFT, "Shipping:", 150, getHeightForRow(height, row), 0f);
					cb.endText();
					cb.beginText();
					cb.setFontAndSize(bf, 8);
					cb.setColorFill(new Color(0x00, 0x00, 0x00));
					cb.showTextAligned(PdfContentByte.ALIGN_LEFT, _shipping.setScale(2, BigDecimal.ROUND_HALF_UP).toString(), getXForNumberStr(_shipping.setScale(2, BigDecimal.ROUND_HALF_UP).toString()), getHeightForRow(height, row), 0f);
					cb.endText();

					row++;
				}
				
				cb.beginText();
				cb.setFontAndSize(bf, 8);
				cb.setColorFill(new Color(0x00, 0x00, 0x00));
				cb.showTextAligned(PdfContentByte.ALIGN_LEFT, "Tax:", 168, getHeightForRow(height, row), 0f);
				cb.endText();
				cb.beginText();
				cb.setFontAndSize(bf, 8);
				cb.setColorFill(new Color(0x00, 0x00, 0x00));
				cb.showTextAligned(PdfContentByte.ALIGN_LEFT, _tax.setScale(2, BigDecimal.ROUND_HALF_UP).toString(), getXForNumberStr(_tax.setScale(2, BigDecimal.ROUND_HALF_UP).toString()), getHeightForRow(height, row), 0f);
				cb.endText();

				row++;

				cb.beginText();
				cb.setFontAndSize(bf, 8);
				cb.setColorFill(new Color(0x00, 0x00, 0x00));
				cb.showTextAligned(PdfContentByte.ALIGN_LEFT, "Total:", 164, getHeightForRow(height, row), 0f);
				cb.endText();
				cb.beginText();
				cb.setFontAndSize(bf, 8);
				cb.setColorFill(new Color(0x00, 0x00, 0x00));
				cb.showTextAligned(PdfContentByte.ALIGN_LEFT, _total.setScale(2, BigDecimal.ROUND_HALF_UP).toString(), getXForNumberStr(_total.setScale(2, BigDecimal.ROUND_HALF_UP).toString()), getHeightForRow(height, row), 0f);
				cb.endText();

				row++;
				
				String memo = _adminOrder.getMemo();
				if (memo != null && memo.length() > 0)
				{
					row++;
					row += SalesReceiptBuilder.doParagraph(memo, 34, (int)getHeightForRow(height, row), 200, cb, bf, 8, (int)height_per_row, 3);
					row++;
				}

				// loop the tenders...

				itr = _tenders.iterator();
				while (itr.hasNext())
				{
					TenderRet tender = (TenderRet)itr.next();
					if (tender.getType().equals(TenderRet.CASH_TENDER_TYPE))
					{
						cb.beginText();
						cb.setFontAndSize(bf, 8);
						cb.setColorFill(new Color(0x00, 0x00, 0x00));
						cb.showTextAligned(PdfContentByte.ALIGN_LEFT, "Cash Tendered: " + tender.getTenderedAmountString(), left_margin, getHeightForRow(height, row++), 0f);
						cb.endText();
						cb.beginText();
						cb.setFontAndSize(bf, 8);
						cb.setColorFill(new Color(0x00, 0x00, 0x00));
						cb.showTextAligned(PdfContentByte.ALIGN_LEFT, " Change Given: " + tender.getChangeAmountString(), left_margin, getHeightForRow(height, row++), 0f);
						cb.endText();

						SalesReceiptBuilder.showDiscount(cb, bf, _total_discount_products, _total_discount_services, _discount_percentage_products, _discount_percentage_services, height, row++);
						//SalesReceiptBuilder.showShipping(cb, bf, _shipping, height, row++);
					}
					else if (tender.getType().equals(TenderRet.CHECK_TENDER_TYPE))
					{
						cb.beginText();
						cb.setFontAndSize(bf, 8);
						cb.setColorFill(new Color(0x00, 0x00, 0x00));
						if (tender.getCheckNumber() > 0)
							cb.showTextAligned(PdfContentByte.ALIGN_LEFT, "Check #" + tender.getCheckNumber() + ": " + tender.getTenderedAmountString(), left_margin, getHeightForRow(height, row++), 0f);
						else
							cb.showTextAligned(PdfContentByte.ALIGN_LEFT, "Check : " + tender.getTenderedAmountString(), left_margin, getHeightForRow(height, row++), 0f);
						cb.endText();
						if (!tender.getChangeAmountString().equals("0.00"))
						{
							cb.beginText();
							cb.setFontAndSize(bf, 8);
							cb.setColorFill(new Color(0x00, 0x00, 0x00));
							cb.showTextAligned(PdfContentByte.ALIGN_LEFT, "Change Given: " + tender.getChangeAmountString(), left_margin, getHeightForRow(height, row++), 0f);
							cb.endText();
						}

						SalesReceiptBuilder.showDiscount(cb, bf, _total_discount_products, _total_discount_services, _discount_percentage_products, _discount_percentage_services, height, row++);
						//SalesReceiptBuilder.showShipping(cb, bf, _shipping, height, row++);
					} else if (tender.getType().equals(TenderRet.CREDIT_CARD_TENDER_TYPE) ||
							tender.getType().equals(TenderRet.ACCOUNT_TENDER_TYPE) ||
							tender.getType().equals(TenderRet.NSIPM_TENDER_TYPE)) {
						
						String agreement_str = "I agree to pay the above amount according to the card issuer agreement (merchant agreement if credit voucher).";
						
						if (tender.getType().equals(TenderRet.ACCOUNT_TENDER_TYPE)) {
							agreement_str = "I agree to pay the above amount placed on account.";
							
							cb.beginText();
							cb.setFontAndSize(bf, 8);
							cb.setColorFill(new Color(0x00, 0x00, 0x00));
							cb.showTextAligned(PdfContentByte.ALIGN_LEFT, "Amount Placed on Account: " + tender.getTenderedAmountString(), left_margin, getHeightForRow(height, row++), 0f);
							cb.endText();
						} else if (tender.getType().equals(TenderRet.NSIPM_TENDER_TYPE)) {
							agreement_str = "NISPM non-profit will be billed the following amount.";
							
							cb.beginText();
							cb.setFontAndSize(bf, 8);
							cb.setColorFill(new Color(0x00, 0x00, 0x00));
							cb.showTextAligned(PdfContentByte.ALIGN_LEFT, "Amount billed to NSIPM: " + tender.getTenderedAmountString(), left_margin, getHeightForRow(height, row++), 0f);
							cb.endText();
						} else {
							if (!tender.getTenderedAmountString().equals("0.00"))
							{
								cb.beginText();
								cb.setFontAndSize(bf, 8);
								cb.setColorFill(new Color(0x00, 0x00, 0x00));
								cb.showTextAligned(PdfContentByte.ALIGN_LEFT, "Credit Card: " + tender.getTenderedAmountString() + "   " + tender.getCreditCardTypeString() + " " + tender.getMerchantAccountNumberString(), left_margin, getHeightForRow(height, row++), 0f);
								cb.endText();
							}
							if (!tender.getChangeAmountString().equals("0.00"))
							{
								cb.beginText();
								cb.setFontAndSize(bf, 8);
								cb.setColorFill(new Color(0x00, 0x00, 0x00));
								cb.showTextAligned(PdfContentByte.ALIGN_LEFT, "Credit Card Refund: " + tender.getChangeAmountString(), left_margin, getHeightForRow(height, row++), 0f);
								cb.endText();
							}
							cb.beginText();
							cb.setFontAndSize(bf, 8);
							cb.setColorFill(new Color(0x00, 0x00, 0x00));
							cb.showTextAligned(PdfContentByte.ALIGN_LEFT, "Auth #: " + tender.getAuthorizationCodeString(), left_margin, getHeightForRow(height, row++), 0f);
							cb.endText();
						}

						SalesReceiptBuilder.showDiscount(cb, bf, _total_discount_products, _total_discount_services, _discount_percentage_products, _discount_percentage_services, height, row++);
						//SalesReceiptBuilder.showShipping(cb, bf, _shipping, height, row++);

						row++;
						cb.beginText();
						cb.setFontAndSize(bf, 10);
						cb.setColorFill(new Color(0x00, 0x00, 0x00));
						cb.showTextAligned(PdfContentByte.ALIGN_LEFT, "Signature:", left_margin, getHeightForRow(height, row), 0f);
						cb.endText();

						cb.moveTo(60, getHeightForRow(height, row) - 2);
						cb.lineTo(width - left_margin, getHeightForRow(height, row) - 2);
						cb.stroke();
						
						row++;
						row++;

						int rows_consumed = SalesReceiptBuilder.doParagraph(agreement_str, 34, (int)getHeightForRow(height, row), 200, cb, bf, 8, (int)height_per_row, 3);
						row += (rows_consumed - 1);
					}
					else if (tender.getType().equals(TenderRet.GIFT_CARD_TENDER_TYPE) || tender.getType().equals(TenderRet.GIFT_CERTIFICATE_TENDER_TYPE))
					{
						String number_str = tender.getGiftCertCardNumber();
						
						GiftCard gift_obj = null;
						if (tender.getType().equals(TenderRet.GIFT_CERTIFICATE_TENDER_TYPE))
							gift_obj = GiftCard.getGiftCertificate(number_str);
						else
							gift_obj = GiftCard.getGiftCard(number_str);

						cb.beginText();
						cb.setFontAndSize(bf, 8);
						cb.setColorFill(new Color(0x00, 0x00, 0x00));
						if (tender.getType().equals(TenderRet.GIFT_CARD_TENDER_TYPE))
							cb.showTextAligned(PdfContentByte.ALIGN_LEFT, "Gift Card #" + number_str + ": " + tender.getTenderedAmountString(), left_margin, getHeightForRow(height, row++), 0f);
						else
							cb.showTextAligned(PdfContentByte.ALIGN_LEFT, "Gift Cert #" + number_str + ": " + tender.getTenderedAmountString(), left_margin, getHeightForRow(height, row++), 0f);
						cb.endText();

						cb.beginText();
						cb.setFontAndSize(bf, 8);
						cb.setColorFill(new Color(0x00, 0x00, 0x00));
						cb.showTextAligned(PdfContentByte.ALIGN_LEFT, "Remaining Balance: " + gift_obj.getBalanceString(), left_margin, getHeightForRow(height, row++), 0f);
						cb.endText();

						if (!tender.getChangeAmountString().equals("0.00"))
						{
							cb.beginText();
							cb.setFontAndSize(bf, 8);
							cb.setColorFill(new Color(0x00, 0x00, 0x00));
							cb.showTextAligned(PdfContentByte.ALIGN_LEFT, "Change Given: " + tender.getChangeAmountString(), left_margin, getHeightForRow(height, row++), 0f);
							cb.endText();
						}

						SalesReceiptBuilder.showDiscount(cb, bf, _total_discount_products, _total_discount_services, _discount_percentage_products, _discount_percentage_services, height, row++);
						//SalesReceiptBuilder.showShipping(cb, bf, _shipping, height, row++);
					}

					row++;
					if (itr.hasNext())
						row++;
				}

				int rows_consumed = SalesReceiptBuilder.doParagraph("You will need to keep your receipts for insurance purposes.", 34, (int)getHeightForRow(height, row), 200, cb, bf, 8, (int)height_per_row, 3);
				System.out.println("rows_consumed >" + rows_consumed);

            }
			
			PdfAction action = new PdfAction(PdfAction.PRINTDIALOG);
			writer.setOpenAction(action);
			
            // step 5: we close the document
            document.close();


		}
		catch (Throwable e)
		{
			e.printStackTrace();
		}

		return file;

	} // generateClientAppointmentWorksheet

	private static void
	showDiscount(PdfContentByte _cb,
			BaseFont _bf,
			BigDecimal _total_discount_products,
			BigDecimal _total_discount_services,
			BigDecimal _discount_percentage_products,
			BigDecimal _discount_percentage_services,
			float _height,
			int _row)
	{
		if (_total_discount_products.compareTo(BigDecimal.ZERO) == 1) {
			_cb.beginText();
			_cb.setFontAndSize(_bf, 8);
			_cb.setColorFill(new Color(0x00, 0x00, 0x00));
			_cb.showTextAligned(PdfContentByte.ALIGN_LEFT, " Products Discount: " + _total_discount_products.setScale(2, BigDecimal.ROUND_HALF_UP).toString() + " (" + _discount_percentage_products.toString() + "%)", left_margin, getHeightForRow(_height, _row++), 0f);
			_cb.endText();
		}
		
		if (_total_discount_services.compareTo(BigDecimal.ZERO) == 1) {
			_cb.beginText();
			_cb.setFontAndSize(_bf, 8);
			_cb.setColorFill(new Color(0x00, 0x00, 0x00));
			_cb.showTextAligned(PdfContentByte.ALIGN_LEFT, " Services Discount: " + _total_discount_services.setScale(2, BigDecimal.ROUND_HALF_UP).toString() + " (" + _discount_percentage_services.toString() + "%)", left_margin, getHeightForRow(_height, _row++), 0f);
			_cb.endText();
		}
	}

	/*
	private static void
	showShipping(PdfContentByte _cb, BaseFont _bf, BigDecimal _shipping, float _height, int _row)
	{
		if (_shipping.compareTo(BigDecimal.ZERO) == 1)
		{
			_cb.beginText();
			_cb.setFontAndSize(_bf, 8);
			_cb.setColorFill(new Color(0x00, 0x00, 0x00));
			_cb.showTextAligned(PdfContentByte.ALIGN_LEFT, " Shipping: " + _shipping.setScale(2, BigDecimal.ROUND_HALF_UP).toString(), left_margin, getHeightForRow(_height, _row++), 0f);
			_cb.endText();
		}
	}
	 */

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

	private static int
	getXForNumberStr(String _str)
	{
		switch (_str.substring(0, _str.indexOf('.')).length())
		{
			case 1: return 204;
			case 2: return 200;
			case 3: return 196;
			case 4: return 192;
			case 5: return 188;
			case 6: return 184;
		}

		return 180;
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

} // ClientAppointmentBuilder



