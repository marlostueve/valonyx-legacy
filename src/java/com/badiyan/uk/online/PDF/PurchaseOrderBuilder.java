/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.badiyan.uk.online.PDF;


import java.io.*;
import java.util.*;

import com.badiyan.uk.online.beans.*;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
//import com.lowagie.text.pdf.codec.TiffImage;

import com.badiyan.torque.*;
import com.badiyan.uk.beans.*;

import java.awt.Color;

import java.math.*;
import com.badiyan.uk.exceptions.*;
import com.valeo.qb.data.VendorRet;

public class
PurchaseOrderBuilder
{
	private static float abs_pos_x = 40f;
	private static float abs_pos_y = 20f;

	private static float height = 595;
	private static float height_per_row = 12;


	/**
	 * Manages the creation of the printed plan's individual pages.
	 * @param args
	 */
	public static String
	generatePurchaseOrder(UKOnlineCompanyBean _company, PurchaseOrder _purchase_order)
		throws IllegalValueException
	{

		String realPath = CUBean.getProperty("cu.realPath");
		String resourcesFolder = CUBean.getProperty("cu.resourcesFolder");
		if (resourcesFolder == null)
			throw new IllegalValueException("Property not defined: cu.resourcesFolder");
		resourcesFolder = realPath + resourcesFolder;

		String file = "purchase-order-" + System.currentTimeMillis() + ".pdf";
		String pdf_file = resourcesFolder + "pdf/" + file;

		String pdf_file_source = resourcesFolder + "pdf/purchase-order.pdf";

		int rows_per_page = 30;


		try
		{
			// we create a reader for a certain document
            PdfReader reader = new PdfReader(pdf_file_source);
            // we retrieve the total number of pages
            int n = reader.getNumberOfPages();

			int total_items = 0;

			System.out.println("total_items >" + total_items);

			n = total_items / rows_per_page;
			if ((total_items % rows_per_page) > 0)
				n++;

			if (n == 0)
				n = 1;

            // we retrieve the size of the first page
            Rectangle psize = reader.getPageSize(1);

            float page_width = psize.width();
            float page_height = psize.height();

			System.out.println("page_width >" + page_width);
			System.out.println("page_height >" + page_height);

            // step 1: creation of a document-object
            Document document = new Document(new Rectangle(page_width, page_height));
            // step 2: we create a writer that listens to the document
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(pdf_file));
            // step 3: we open the document
            document.open();
            // step 4: we add content
            PdfContentByte cb = writer.getDirectContent();
            int i = 0;
            int p = 0;

			int row = 0;

			Iterator item_itr = _purchase_order.getItems().iterator();
			int ln = 1;

            while (i < n) {

				row = 0;

                document.newPage();
                p++;
                i++;
                PdfImportedPage page1 = writer.getImportedPage(reader, 1);
				cb.addTemplate(page1, 0, 0);

                BaseFont bf = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);

				PurchaseOrderBuilder.doText(_purchase_order.getPurchaseOrderNumber(_company) + "", cb, bf, 10, 514, 763);
				PurchaseOrderBuilder.doText(_purchase_order.getPurchaseOrderDateString(), cb, bf, 10, 514, 739);

				String total_str = _purchase_order.getTotalString();
				PurchaseOrderBuilder.doText(total_str, cb, bf, 10, getXForNumberStrX(total_str, 568), 223);
				PurchaseOrderBuilder.doText("0.00", cb, bf, 10, getXForNumberStrX("0.00", 568), 204);
				PurchaseOrderBuilder.doText(total_str, cb, bf, 10, getXForNumberStrX(total_str, 568), 185);

				VendorRet vendor = _purchase_order.getVendor();
				PurchaseOrderBuilder.doText(vendor.getLabel(), cb, bf, 10, 37, 700);
				try
				{
					AddressBean vendor_address = vendor.getAddress();
					PurchaseOrderBuilder.doText(vendor_address.getStreet1String(), cb, bf, 10, 37, 688);
					PurchaseOrderBuilder.doText(vendor_address.getCityString() + ", " + vendor_address.getStateString() + " " + vendor_address.getZipCodeString(), cb, bf, 10, 37, 676);
					PurchaseOrderBuilder.doText("PHONE " + vendor.getPhone(), cb, bf, 10, 37, 664);
					PurchaseOrderBuilder.doText("FAX " + vendor.getFax(), cb, bf, 10, 37, 652);
				}
				catch (ObjectNotFoundException x)
				{
				}


				PurchaseOrderBuilder.doText(_company.getLabel(), cb, bf, 10, 240, 700);
				try
				{
					AddressBean company_address = _company.getAddress(AddressBean.PRACTICE_ADDRESS_TYPE);
					PurchaseOrderBuilder.doText(company_address.getStreet1String(), cb, bf, 10, 240, 688);
					PurchaseOrderBuilder.doText(company_address.getCityString() + ", " + company_address.getStateString() + " " + company_address.getZipCodeString(), cb, bf, 10, 240, 676);
					PurchaseOrderBuilder.doText("ATTN:", cb, bf, 10, 240, 664);
					//PurchaseOrderBuilder.doText(, cb, bf, 10, 240, 652);
				}
				catch (ObjectNotFoundException x)
				{
					PurchaseOrderBuilder.doText("[ADDRESS NOT FOUND]", cb, bf, 10, 240, 688);
				}


				if (item_itr.hasNext())
				{
					for (; item_itr.hasNext() && (row < rows_per_page); ln++)
					{
						//String log_str = (String)item_itr.next();
						PurchaseOrderItemMapping item = (PurchaseOrderItemMapping)item_itr.next();
						
						CheckoutCodeBean checkout_code = CheckoutCodeBean.getCheckoutCode(item.getCheckoutCodeId());

						//PurchaseOrderBuilder.doText(log_str, cb, bf, 8, height, row, 60);
						PurchaseOrderBuilder.doText(ln + "", cb, bf, 10, 30, getHeightForRow(height, row));

						String qty_str = item.getQuantity().setScale(2, BigDecimal.ROUND_HALF_UP).toString();
						PurchaseOrderBuilder.doText(qty_str, cb, bf, 10, getXForNumberStrX(qty_str, 69), getHeightForRow(height, row));
						
						PurchaseOrderBuilder.doText(checkout_code.getItemNumberString(), cb, bf, 10, 100, getHeightForRow(height, row));

						int num_rows = 0;
						if (item.getProductWaitListDbId() > 0) {
							String waitPersonStr = "";
							try {
								ProductWaitList wait_list_item = ProductWaitList.getProductWaitListEntry(item.getProductWaitListDbId());
								waitPersonStr = " [" + wait_list_item.getWaitingPerson().getLabel() + "]";
							} catch (Exception x) {
								x.printStackTrace();
							}
							num_rows = PurchaseOrderBuilder.doParagraph(checkout_code.getLabel() + waitPersonStr, 180, getHeightForRow(height, row), 270, cb, bf, 8, height_per_row, 3);
						} else {
							num_rows = PurchaseOrderBuilder.doParagraph(checkout_code.getLabel(), 180, getHeightForRow(height, row), 270, cb, bf, 8, height_per_row, 3);
						}
						
						String rate_str = item.getRate().setScale(2, BigDecimal.ROUND_HALF_UP).toString();
						PurchaseOrderBuilder.doText(rate_str, cb, bf, 10, getXForNumberStrX(rate_str, 476), getHeightForRow(height, row));

						String amount_str = item.getAmount().setScale(2, BigDecimal.ROUND_HALF_UP).toString();
						PurchaseOrderBuilder.doText(amount_str, cb, bf, 10, getXForNumberStrX(amount_str, 568), getHeightForRow(height, row));

						row += num_rows;
					}
				}

				if (!(i < n)) // out of pages, but still more to do...
				{
					if (item_itr.hasNext())
						n++;
				}

                cb.beginText();
                cb.setFontAndSize(bf, 10);
                cb.showTextAligned(PdfContentByte.ALIGN_CENTER, "page " + p + " of " + n, 60, 28, 0);
                cb.endText();

            }
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
	doText(String _text, PdfContentByte _cb, BaseFont _bf, int _font_size, float _x, float _y)
	{
		_cb.beginText();
		_cb.setFontAndSize(_bf, _font_size);
		_cb.setColorFill(new Color(0x00, 0x00, 0x00));
		_cb.showTextAligned(PdfContentByte.ALIGN_LEFT, _text, _x, _y, 0f);
		_cb.endText();
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

	private static int
	doParagraph(String _str, int _x, float _y, int _width, PdfContentByte _over, BaseFont _bf, int _font_size, float _space_between_rows, int _max_rows)
	{
	    int num_chars = _width / 4;
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
			_over.showTextAligned(PdfContentByte.ALIGN_LEFT, string_to_print.trim(), _x , _y - (row * _space_between_rows), 0f);
			_over.endText();

			row++;
			if (row == _max_rows)
				return row;

			pos = pos + string_to_print.length();
	    }

		return row;
	}

	private static float
	getHeightForRow(float _height, int _row)
	{
		return _height - (_row * height_per_row);
	}

	private static float
	getXForNumberStrX(String _str, float _baseline)
	{
		return _baseline - (_str.substring(0, _str.indexOf('.')).length() * 5.5f);
	}

} // ClientAppointmentBuilder



