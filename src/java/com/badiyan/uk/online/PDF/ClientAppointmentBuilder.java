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
ClientAppointmentBuilder
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
	generateClientAppointmentWorksheet(UKOnlineCompanyBean _company, UKOnlinePersonBean _person, Date _start_date, Date _end_date)
		throws IllegalValueException
	{

		String realPath = CUBean.getProperty("cu.realPath");
		String resourcesFolder = CUBean.getProperty("cu.resourcesFolder");
		if (resourcesFolder == null)
			throw new IllegalValueException("Property not defined: cu.resourcesFolder");
		resourcesFolder = realPath + resourcesFolder;

		String file = "practitioner-appointment-worksheet-" + System.currentTimeMillis() + ".pdf";
		String pdf_file = resourcesFolder + "pdf/" + file;

		String pdf_file_source = resourcesFolder + "pdf/client-appointments.pdf";

		if (_company.getId() == 1)
			pdf_file_source = resourcesFolder + "pdf/client-appointments-valeo.pdf";

		int rows_per_page = 53;


		try
		{
			// we create a reader for a certain document
            PdfReader reader = new PdfReader(pdf_file_source);
            // we retrieve the total number of pages
            int n = reader.getNumberOfPages();

			Vector appointments = AppointmentBean.getAppointmentsForClient(_person, _start_date, _end_date);
			Iterator appt_itr = appointments.iterator();

			int total_items = appointments.size();

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
				cb.showTextAligned(PdfContentByte.ALIGN_LEFT, _person.getLabel(), 260, 721, 0f);
				cb.endText();

				cb.beginText();
				cb.setFontAndSize(bf, 12);
				cb.setColorFill(new Color(0x00, 0x00, 0x00));
				cb.showTextAligned(PdfContentByte.ALIGN_LEFT, CUBean.getUserDateString(new Date(), "EEEE, MMMM d, yyyy"), 260, 700, 0f);
				cb.endText();

				if (appt_itr.hasNext())
				{
					while (appt_itr.hasNext() && (row < rows_per_page))
					{
						AppointmentBean appt = (AppointmentBean)appt_itr.next();
						Date appt_date = appt.getAppointmentDate();

						String status_string = "";
						if (appt.isCancelled())
							status_string = " [CANCELLED]";
						else if (appt.isRescheduled())
							status_string = " [RESCHEDULED]";

						String appt_date_str = AppointmentBean.long_date_format.format(appt_date);

						ClientAppointmentBuilder.doText(appt_date_str, cb, bf, 8, height, row, 60);
						ClientAppointmentBuilder.doText(CUBean.getUserTimeString(appt_date), cb, bf, 8, height, row, 184);
						ClientAppointmentBuilder.doText(appt.getType().getLabel() + status_string, cb, bf, 8, height, row, 255);
						ClientAppointmentBuilder.doText(appt.getPractitioner().getLabel(), cb, bf, 8, height, row, 400);

						row++;
					}
				}
				else
				{
					ClientAppointmentBuilder.doText("No Appointments Found", cb, bf, 8, height, row, 60);
				}


				if (!(i < n)) // out of pages, but still more to do...
				{
					if (appt_itr.hasNext())
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

} // ClientAppointmentBuilder



