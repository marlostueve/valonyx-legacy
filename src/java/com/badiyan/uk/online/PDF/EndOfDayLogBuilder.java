/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.badiyan.uk.online.PDF;


import java.io.*;
import java.util.*;

import com.badiyan.uk.online.beans.*;

import com.lowagie.text.Image;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
//import com.lowagie.text.pdf.codec.TiffImage;

import com.badiyan.torque.*;
import com.badiyan.uk.beans.*;

import java.awt.Color;

import java.math.*;
import com.badiyan.uk.exceptions.*;

public class
EndOfDayLogBuilder
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
	generateEndOfDayLog(Vector _log)
		throws IllegalValueException
	{

		String realPath = CUBean.getProperty("cu.realPath");
		String resourcesFolder = CUBean.getProperty("cu.resourcesFolder");
		if (resourcesFolder == null)
			throw new IllegalValueException("Property not defined: cu.resourcesFolder");
		resourcesFolder = realPath + resourcesFolder;

		String file = "end-of-day-log-" + System.currentTimeMillis() + ".pdf";
		String pdf_file = resourcesFolder + "pdf/" + file;

		String pdf_file_source = resourcesFolder + "pdf/end-of-day-log.pdf";

		int rows_per_page = 53;


		try
		{
			// we create a reader for a certain document
            PdfReader reader = new PdfReader(pdf_file_source);
            // we retrieve the total number of pages
            int n = reader.getNumberOfPages();

			int total_items = _log.size();

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

			Iterator log_itr = _log.iterator();

            while (i < n) {

				row = 0;

                document.newPage();
                p++;
                i++;
                PdfImportedPage page1 = writer.getImportedPage(reader, 1);
				cb.addTemplate(page1, 0, 0);
				
				/*
				com.lowagie.text.Image image = com.lowagie.text.Image.getInstance(resourcesFolder + "signature.png");
				document.add(image);
				*/

                BaseFont bf = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);

				cb.beginText();
				cb.setFontAndSize(bf, 12);
				cb.setColorFill(new Color(0x00, 0x00, 0x00));
				cb.showTextAligned(PdfContentByte.ALIGN_LEFT, CUBean.getUserDateString(new Date(), "EEEE, MMMM d, yyyy"), 260, 700, 0f);
				cb.endText();

				
				if (log_itr.hasNext())
				{
					while (log_itr.hasNext() && (row < rows_per_page))
					{
						String log_str = (String)log_itr.next();

						//EndOfDayLogBuilder.doText(log_str, cb, bf, 8, height, row, 60);
						int num_rows = EndOfDayLogBuilder.doParagraph(log_str, 60, getHeightForRow(height, row), 500, cb, bf, 8, height_per_row, 3);
						row += num_rows;

						//row++;
					}
				}

				if (!(i < n)) // out of pages, but still more to do...
				{
					if (log_itr.hasNext())
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
	
	public static String
	generateEndOfDayLog()
		throws IllegalValueException
	{

		String realPath = CUBean.getProperty("cu.realPath");
		String resourcesFolder = CUBean.getProperty("cu.resourcesFolder");
		if (resourcesFolder == null)
			throw new IllegalValueException("Property not defined: cu.resourcesFolder");
		resourcesFolder = realPath + resourcesFolder;

		String file = "end-of-day-log-" + System.currentTimeMillis() + ".pdf";
		String pdf_file = resourcesFolder + "pdf/" + file;

		String pdf_file_source = resourcesFolder + "pdf/end-of-day-log.pdf";

		int rows_per_page = 53;


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

			//Iterator log_itr = _log.iterator();

            while (i < n) {

				row = 0;

                document.newPage();
                p++;
                i++;
                PdfImportedPage page1 = writer.getImportedPage(reader, 1);
				cb.addTemplate(page1, 0, 0);
				
				com.lowagie.text.Image image = com.lowagie.text.Image.getInstance("C:\\saved.png");
				document.add(image);

                BaseFont bf = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);

				cb.beginText();
				cb.setFontAndSize(bf, 12);
				cb.setColorFill(new Color(0x00, 0x00, 0x00));
				cb.showTextAligned(PdfContentByte.ALIGN_LEFT, CUBean.getUserDateString(new Date(), "EEEE, MMMM d, yyyy"), 260, 700, 0f);
				cb.endText();


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

} // ClientAppointmentBuilder



