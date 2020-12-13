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
SOAPNoteReportBuilder
{
	private static float abs_pos_x = 40f;
	private static float abs_pos_y = 20f;

	private static float appt_start_y = 670;

	private static float height_per_row = 13f;

	private static float left_margin = 10f;
	private static float qty_margin = 160f;
	private static float amount_margin = 190f;


	private static float space_per_number = 7f;


	private static float height = 775f;
	private static float width = 500f;


	/**
	 * Manages the creation of the printed plan's individual pages.
	 * @param args
	 */
	public static String
	generateSOAPNote(UKOnlineCompanyBean _company, UKOnlinePersonBean _client, UKOnlinePersonBean _practitioner, Date _report_date, SOAPNotesBean _note)
		throws IllegalValueException
	{

		String realPath = CUBean.getProperty("cu.realPath");
		String resourcesFolder = CUBean.getProperty("cu.resourcesFolder");
		if (resourcesFolder == null)
			throw new IllegalValueException("Property not defined: cu.resourcesFolder");
		resourcesFolder = realPath + resourcesFolder;

		String file = "soap-report-" + System.currentTimeMillis() + ".pdf";
		String pdf_file = resourcesFolder + "pdf/" + file;


		int appointments_per_page = 53;

		try
		{

			// we create a reader for a certain document
            //PdfReader reader = new PdfReader(pdf_file_source);
            // we retrieve the total number of pages

			//Vector appointments = AppointmentBean.getAppointmentsForPractitioner(_practitioner, _report_date);
			//Iterator appt_itr = appointments.iterator();

			//n = appointments.size() / appointments_per_page;
			//if ((appointments.size() % appointments_per_page) > 0)
			//	n++;

            // we retrieve the size of the first page
            //Rectangle psize = reader.getPageSize(1);

            //float width = psize.width();
            //float height = psize.height();



            // step 1: creation of a document-object
            //Document document = new Document(new Rectangle(width, height));
            Document document = new Document(PageSize.A4);
            // step 2: we create a writer that listens to the document
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(pdf_file));
            // step 3: we open the document
            document.open();
            // step 4: we add content
            PdfContentByte cb = writer.getDirectContent();
            int i = 0;
            int p = 0;
            if (i < 1) {

                document.newPage();
                p++;
                i++;
                //PdfImportedPage page1 = writer.getImportedPage(reader, 1);
				//cb.addTemplate(page1, 0, 0);

                BaseFont bf = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);

				int row = 1;

				SOAPNoteReportBuilder.doText("Treatment Notes", cb, bf, 18, left_margin, row++);

				cb.moveTo(left_margin, getHeightForRow(height, row) - 2);
				cb.lineTo(width - left_margin, getHeightForRow(height, row) - 2);
				cb.stroke();


				row++;

				row++;

				SOAPNoteReportBuilder.doText("Date of Treatment: " + CUBean.getUserDateString(_report_date), cb, bf, 12, left_margin, row);
				String file_number_str = _client.getFileNumberString();
				if (file_number_str.equals(""))
					SOAPNoteReportBuilder.doText("Patient / Client: " + _client.getLabel(), cb, bf, 12, width / 2, row++);
				else
					SOAPNoteReportBuilder.doText("Patient / Client: " + file_number_str + " - " + _client.getLabel(), cb, bf, 12, width / 2, row++);

				SOAPNoteReportBuilder.doText("Report Date: " + CUBean.getUserDateString(new Date()), cb, bf, 12, left_margin, row++);


				cb.moveTo(left_margin, getHeightForRow(height, row) - 2);
				cb.lineTo(width - left_margin, getHeightForRow(height, row) - 2);
				cb.stroke();

				row++;
				row++;
				row++;


				SOAPNoteReportBuilder.doText("S", cb, bf, 18, left_margin, row);
				int rows_consumed = SOAPNoteReportBuilder.doParagraph(_note.getSNoteString(), 34, (int)getHeightForRow(height, row), 400, cb, bf, 12, (int)height_per_row, 8);
				row += rows_consumed + 1;

				SOAPNoteReportBuilder.doText("O", cb, bf, 18, left_margin, row);
				rows_consumed = SOAPNoteReportBuilder.doParagraph(_note.getONoteStatementHTMLString(), 34, (int)getHeightForRow(height, row), 400, cb, bf, 12, (int)height_per_row, 8);
				row += rows_consumed + 1;

				SOAPNoteReportBuilder.doText("A", cb, bf, 18, left_margin, row);
				rows_consumed = SOAPNoteReportBuilder.doParagraph(_note.getANoteString(), 34, (int)getHeightForRow(height, row), 400, cb, bf, 12, (int)height_per_row, 8);
				row += rows_consumed + 1;

				SOAPNoteReportBuilder.doText("P", cb, bf, 18, left_margin, row);
				rows_consumed = SOAPNoteReportBuilder.doParagraph(_note.getPNoteString(), 34, (int)getHeightForRow(height, row), 400, cb, bf, 12, (int)height_per_row, 8);
				row += rows_consumed + 1;

				cb.moveTo(left_margin, getHeightForRow(height, row) - 2);
				cb.lineTo(width - left_margin, getHeightForRow(height, row) - 2);
				cb.stroke();

				row++;
				row++;

				try
				{
					SOAPNoteReportBuilder.doText("Attending Practitioner: " + _note.getModifyPerson().getLabel(), cb, bf, 12, width / 2, row++);
				}
				catch (Exception x)
				{
					if (_practitioner == null) {
						SOAPNoteReportBuilder.doText("Attending Practitioner: [NOT FOUND]", cb, bf, 12, width / 2, row++);
					} else {
						SOAPNoteReportBuilder.doText("Attending Practitioner: " + _practitioner.getLabel(), cb, bf, 12, width / 2, row++);
					}
					
				}

				


				/*

				cb.beginText();
				cb.setFontAndSize(bf, 12);
				cb.setColorFill(new Color(0x00, 0x00, 0x00));
				cb.showTextAligned(PdfContentByte.ALIGN_LEFT, "Valeo Health & Wellness Center PLLC", left_margin, getHeightForRow(height, row++), 0f);
				cb.endText();

				cb.beginText();
				cb.setFontAndSize(bf, 8);
				cb.setColorFill(new Color(0x00, 0x00, 0x00));
				cb.showTextAligned(PdfContentByte.ALIGN_LEFT, CUBean.getUserDateString(_report_date) + " " + CUBean.getUserTimeString(_report_date), left_margin, getHeightForRow(height, row), 0f);
				cb.endText();

				cb.beginText();
				cb.setFontAndSize(bf, 8);
				cb.setColorFill(new Color(0x00, 0x00, 0x00));
				cb.showTextAligned(PdfContentByte.ALIGN_LEFT, "Sales Receipt #" + _adminOrder.getValue(), 115f, getHeightForRow(height, row++), 0f);
				cb.endText();

				row++;


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

				Iterator itr = _adminOrder.getOrdersVec().iterator();
				while (itr.hasNext())
				{
					row++;
					CheckoutOrderline orderline = (CheckoutOrderline)itr.next();
					CheckoutCodeBean code = CheckoutCodeBean.getCheckoutCode(orderline.getCheckoutCodeId());
					String description = code.getLabel();
					String qty = orderline.getQuantity().setScale(2, BigDecimal.ROUND_HALF_UP).toString();
					String amount = orderline.getPrice().setScale(2, BigDecimal.ROUND_HALF_UP).toString();


					int rows_consumed = SOAPNoteReportBuilder.doParagraph(description, (int)left_margin, (int)getHeightForRow(height, row), 140, cb, bf, 8, (int)height_per_row, 3);
					System.out.println("rows_consumed >" + rows_consumed);

					cb.beginText();
					cb.setFontAndSize(bf, 8);
					cb.setColorFill(new Color(0x00, 0x00, 0x00));
					cb.showTextAligned(PdfContentByte.ALIGN_LEFT, qty, qty_margin, getHeightForRow(height, row), 0f);
					cb.endText();
					cb.beginText();
					cb.setFontAndSize(bf, 8);
					cb.setColorFill(new Color(0x00, 0x00, 0x00));
					cb.showTextAligned(PdfContentByte.ALIGN_LEFT, amount, getXForNumberStr(amount), getHeightForRow(height, row), 0f);
					cb.endText();

					row += (rows_consumed - 1);
				}


				itr = _previous_orders.iterator();
				while (itr.hasNext())
				{
					row++;
					ValeoOrderBean previous_order = (ValeoOrderBean)itr.next();
					String description = "Payment on Account (Order #" + previous_order.getValue() + ")";
					int rows_consumed = SOAPNoteReportBuilder.doParagraph(description, (int)left_margin, (int)getHeightForRow(height, row), 140, cb, bf, 8, (int)height_per_row, 3);
					row += (rows_consumed - 1);
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

				// loop the tenders...

				itr = _tenders.iterator();
				while (itr.hasNext())
				{
					TenderRet tender = (TenderRet)itr.next();
					if (tender.getType().equals(TenderRet.ACCOUNT_TENDER_TYPE))
					{
						cb.beginText();
						cb.setFontAndSize(bf, 8);
						cb.setColorFill(new Color(0x00, 0x00, 0x00));
						cb.showTextAligned(PdfContentByte.ALIGN_LEFT, "Amount Placed on Account: " + tender.getTenderedAmountString(), left_margin, getHeightForRow(height, row++), 0f);
						cb.endText();
					}
					else if (tender.getType().equals(TenderRet.CASH_TENDER_TYPE))
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
					}
					else if (tender.getType().equals(TenderRet.CHECK_TENDER_TYPE))
					{
						cb.beginText();
						cb.setFontAndSize(bf, 8);
						cb.setColorFill(new Color(0x00, 0x00, 0x00));
						cb.showTextAligned(PdfContentByte.ALIGN_LEFT, "Check #" + tender.getCheckNumber() + ": " + tender.getTenderedAmountString(), left_margin, getHeightForRow(height, row++), 0f);
						cb.endText();
						if (!tender.getChangeAmountString().equals("0.00"))
						{
							cb.beginText();
							cb.setFontAndSize(bf, 8);
							cb.setColorFill(new Color(0x00, 0x00, 0x00));
							cb.showTextAligned(PdfContentByte.ALIGN_LEFT, "Change Given: " + tender.getChangeAmountString(), left_margin, getHeightForRow(height, row++), 0f);
							cb.endText();
						}
					}
					else if (tender.getType().equals(TenderRet.CREDIT_CARD_TENDER_TYPE))
					{
						if (!tender.getTenderedAmountString().equals("0.00"))
						{
							cb.beginText();
							cb.setFontAndSize(bf, 8);
							cb.setColorFill(new Color(0x00, 0x00, 0x00));
							cb.showTextAligned(PdfContentByte.ALIGN_LEFT, "Credit Card: " + tender.getTenderedAmountString(), left_margin, getHeightForRow(height, row++), 0f);
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


						int rows_consumed = SOAPNoteReportBuilder.doParagraph("I agree to pay the above amount according to the card issuer agreement (merchant agreement if credit vaucher).", 34, (int)getHeightForRow(height, row), 200, cb, bf, 8, (int)height_per_row, 3);
						System.out.println("rows_consumed >" + rows_consumed);

						row += (rows_consumed - 1);

					}

					row++;
					if (itr.hasNext())
						row++;
				}

				int rows_consumed = SOAPNoteReportBuilder.doParagraph("You will need to keep your receipts for insurance purposes.", 34, (int)getHeightForRow(height, row), 200, cb, bf, 8, (int)height_per_row, 3);
				System.out.println("rows_consumed >" + rows_consumed);
				 *
				 */

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

	private static void
	doText(String _text, PdfContentByte _over, BaseFont _bf, int _font_size, float _x, int _row)
	{
		_over.beginText();
		_over.setFontAndSize(_bf, _font_size);
		_over.setColorFill(new Color(0x00, 0x00, 0x00));
		_over.showTextAligned(PdfContentByte.ALIGN_LEFT, _text, _x, getHeightForRow(height, _row), 0f);
		_over.endText();
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



