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

public class
PractitionerAppointmentsBuilder
{
	private static float abs_pos_x = 40f;
	private static float abs_pos_y = 20f;
	
	private static float height = 670;
	private static float height_per_row = 12;


	/**
	 * Manages the creation of the printed plan's individual pages.
	 * @param args
	 */
	public static String
	generatePractitionerAppointmentWorksheet(UKOnlineCompanyBean _company, UKOnlinePersonBean _practitioner, Date _report_date)
		throws IllegalValueException
	{
		String realPath = CUBean.getProperty("cu.realPath");
		String resourcesFolder = CUBean.getProperty("cu.resourcesFolder");
		if (resourcesFolder == null)
			throw new IllegalValueException("Property not defined: cu.resourcesFolder");
		resourcesFolder = realPath + resourcesFolder;

		String file = "practitioner-appointment-worksheet-" + System.currentTimeMillis() + ".pdf";
		String pdf_file = resourcesFolder + "pdf/" + file;

		String pdf_file_source = resourcesFolder + "pdf/practitioner-appointments.pdf";

		if (_company.getId() == 1)
			pdf_file_source = resourcesFolder + "pdf/practitioner-appointments-valeo.pdf";

		int rows_per_page = 53;


		try
		{
			// we create a reader for a certain document
            PdfReader reader = new PdfReader(pdf_file_source);
            // we retrieve the total number of pages
            int n = reader.getNumberOfPages();
			
			Vector appointments = AppointmentBean.getAppointmentsForPractitioner(_practitioner, _report_date);
			Iterator appt_itr = appointments.iterator();

			HashMap<ReviewReason,Boolean> reason_header_printed = new HashMap<ReviewReason,Boolean>(3);
			//HashMap<ReviewReason,Vector> review_hash = new HashMap<ReviewReason,Vector>(3);
			
			
			//Vector reason_order = ReviewReason.getReviewReasons(_company);

			int rows_for_review_items = 0;
			Vector review_items = ClientReviewReason.getPastDueClientReviewReasonsForPractitioner(_practitioner);
			Iterator review_itr = review_items.iterator();
			while (review_itr.hasNext())
			{
				ClientReviewReason client_review_reason = (ClientReviewReason)review_itr.next();
				if (client_review_reason.getNoteString().equals(""))
					rows_for_review_items += 1;
				else
					rows_for_review_items += 2;
			}
			review_itr = review_items.iterator();

			/*
			while (review_itr.hasNext())
			{
				ClientReviewReason client_review_reason = (ClientReviewReason)appt_itr.next();
				ReviewReason review_reason = client_review_reason.getReviewReason();
				Vector vec = review_hash.get(review_reason);
				if (vec == null)
				{
					vec = new Vector();
					review_hash.put(review_reason, vec);
				}
				vec.addElement(client_review_reason);
			}
			 */

			int total_items = appointments.size() + rows_for_review_items;

			System.out.println("total_items >" + total_items);
			
			n = total_items / rows_per_page;
			if ((total_items % rows_per_page) > 0)
				n++;
			
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

            while (i < n) {

				row = 0;
				
                document.newPage();
                p++;
                i++;
                PdfImportedPage page1 = writer.getImportedPage(reader, 1);
				cb.addTemplate(page1, 0, 0);
				
                BaseFont bf = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
				
				cb.beginText();
				cb.setFontAndSize(bf, 12);
				cb.setColorFill(new Color(0x00, 0x00, 0x00));
				cb.showTextAligned(PdfContentByte.ALIGN_LEFT, _practitioner.getLabel(), 260, 721, 0f);
				cb.endText();
				
				cb.beginText();
				cb.setFontAndSize(bf, 12);
				cb.setColorFill(new Color(0x00, 0x00, 0x00));
				cb.showTextAligned(PdfContentByte.ALIGN_LEFT, CUBean.getUserDateString(_report_date, "EEEE, MMMM d, yyyy"), 260, 700, 0f);
				cb.endText();
				
				if (appt_itr.hasNext())
				{
					while (appt_itr.hasNext() && (row < rows_per_page))
					{
						AppointmentBean appt = (AppointmentBean)appt_itr.next();
						Date appt_date = appt.getAppointmentDate();

						PractitionerAppointmentsBuilder.doText(CUBean.getUserTimeString(appt_date), cb, bf, 8, height, row, 60);

						String status_string = "";
						if (appt.isCancelled())
							status_string = "[CANCELLED] ";
						else if (appt.isRescheduled())
							status_string = "[RESCHEDULED] ";

						PractitionerAppointmentsBuilder.doText(appt.getType().getLabel(), cb, bf, 8, height, row, 109);
						PractitionerAppointmentsBuilder.doText(appt.getLabel(), cb, bf, 8, height, row, 215);
						if (appt.hasClient())
							PractitionerAppointmentsBuilder.doText(appt.getClient().getPhoneNumbersString(), cb, bf, 8, height, row, 365);

						String comments = appt.getComments();
						if (!comments.equals("") || !status_string.equals(""))
						{
							row++;
							PractitionerAppointmentsBuilder.doText(status_string + comments, cb, bf, 8, height, row, 100);
						}

						row++;
					}
				}

				if (!appt_itr.hasNext() && review_itr.hasNext())
				{
					// The columns needed for appt list f/u section include (F/U type; client name; phone #; email; last practitioner seen; last date seen; action taken

					// action taken???

					while (review_itr.hasNext() && (row < rows_per_page))
					{
						ClientReviewReason client_review_reason = (ClientReviewReason)review_itr.next();
						UKOnlinePersonBean client = client_review_reason.getPerson();
						ReviewReason review_reason = client_review_reason.getReviewReason();

						Boolean printed = (Boolean)reason_header_printed.get(review_reason);
						if (printed == null)
						{
							row++;
							PractitionerAppointmentsBuilder.doText("Client Follow Up - " + review_reason.getLabel(), cb, bf, 12, height, row, 60);
							PractitionerAppointmentsBuilder.doText("Last Appt", cb, bf, 12, height, row, 465);
							reason_header_printed.put(review_reason, new Boolean(true));
							row++;
						}

						PractitionerAppointmentsBuilder.doText(client.getLabel(), cb, bf, 8, height, row, 60);
						PractitionerAppointmentsBuilder.doText(client.getPhoneNumbersString(), cb, bf, 8, height, row, 150);
						PractitionerAppointmentsBuilder.doText(client.getEmail1String(), cb, bf, 8, height, row, 365);

						AppointmentBean last_appointment = AppointmentBean.getLastAppointmentForClient(client);
						if (last_appointment == null)
							PractitionerAppointmentsBuilder.doText("No Previous Appt.", cb, bf, 8, height, row, 465);
						else
							PractitionerAppointmentsBuilder.doText(CUBean.getUserDateString(last_appointment.getAppointmentDate()) + " - " + last_appointment.getPractitioner().getLabel(), cb, bf, 8, height, row, 465);

						row++;

						String note = client_review_reason.getNoteString();
						if (!note.equals(""))
						{
							PractitionerAppointmentsBuilder.doText(note, cb, bf, 8, height, row, 60);
							int rows_consumed = PractitionerAppointmentsBuilder.doParagraph(note, 60, (int)getHeightForRow(height, row), 500, cb, bf, 8, (int)height_per_row, 4);
							//int rows_consumed = SalesReceiptBuilder.doParagraph("You will need to keep your receipts for insurance purposes.", 34, (int)getHeightForRow(height, row), 200, cb, bf, 8, (int)height_per_row, 3);
							row += rows_consumed;
						}
					}
				}

				if (!(i < n)) // out of pages, but still more to do...
				{
					if (appt_itr.hasNext() || review_itr.hasNext())
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
	doText(String _text, PdfContentByte _cb, BaseFont _bf, int _font_size, float _height, int _row, float _x)
	{
		_cb.beginText();
		_cb.setFontAndSize(_bf, _font_size);
		_cb.setColorFill(new Color(0x00, 0x00, 0x00));
		_cb.showTextAligned(PdfContentByte.ALIGN_LEFT, _text, _x, getHeightForRow(_height, _row), 0f);
		_cb.endText();
	}

	private static float
	getHeightForRow(float _height, int _row)
	{
		return _height - (_row * height_per_row);
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



