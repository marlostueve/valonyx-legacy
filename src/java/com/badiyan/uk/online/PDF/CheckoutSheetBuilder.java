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

import com.badiyan.torque.*;
import com.badiyan.uk.beans.*;

import java.awt.Color;

import java.math.*;
import com.badiyan.uk.exceptions.*;

import com.valeo.qbpos.data.*;

public class
CheckoutSheetBuilder
{

	private static float appt_start_y = 436;
	private static float appt_spacing = 16;


	/**
	 * Manages the creation of the printed plan's individual pages.
	 * @param args
	 */
	public static String
	generateCheckoutSheet(UKOnlineCompanyBean _company, UKOnlinePersonBean _person)
		throws IllegalValueException
	{
		Date report_date = new Date();

		String realPath = CUBean.getProperty("cu.realPath");
		String resourcesFolder = CUBean.getProperty("cu.resourcesFolder");
		if (resourcesFolder == null)
			throw new IllegalValueException("Property not defined: cu.resourcesFolder");
		resourcesFolder = realPath + resourcesFolder;

		//String pdf_file = "C:\\odd.pdf";
		String file = "client-checkout-sheet-" + System.currentTimeMillis() + ".pdf";
		String pdf_file = resourcesFolder + "pdf/" + file;

		String pdf_file_source = resourcesFolder + "pdf/universal-check-out-sheet.pdf";


		//Document document = new Document();
		try
		{
		    //PdfReader reader = new PdfReader(pdf_file_source);
		    //PdfStamper stamp = new PdfStamper(reader, new FileOutputStream(pdf_file));

		    // we create a reader for a certain document
		    PdfReader reader = new PdfReader(pdf_file_source);
		    int n = reader.getNumberOfPages();
		    // we create a stamper that will copy the document to a new file
		    PdfStamper stamp = new PdfStamper(reader, new FileOutputStream(pdf_file));
		    // adding some metadata
		    HashMap moreInfo = new HashMap();
		    moreInfo.put("Author", "Valeo");
		    stamp.setMoreInfo(moreInfo);
		    // adding content to each page
		    int i = 0;
		    PdfContentByte under;
		    PdfContentByte over;
		    //Image img = Image.getInstance("watermark.jpg");
		    BaseFont bf = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.EMBEDDED);
		    //img.setAbsolutePosition(200, 400);
		    while (i < n)
			{
				i++;
				// watermark under the existing page
				under = stamp.getUnderContent(i);
				//under.addImage(img);
				// text over the existing page
				over = stamp.getOverContent(i);

				over.beginText();
				over.setFontAndSize(bf, 18);
				over.setTextMatrix(30, 30);

				//over.showText("page " + i);
				over.setFontAndSize(bf, 16);
				over.showTextAligned(Element.ALIGN_LEFT, "", 200, 200, 0);
				over.endText();

				over.beginText();
				over.setFontAndSize(bf, 12);
				over.setColorFill(new Color(0x00, 0x00, 0x00));
				over.showTextAligned(PdfContentByte.ALIGN_LEFT, _person.getFileNumberString() + " - " + _person.getLabel(), 105, 746, 0f);
				over.endText();

				over.beginText();
				over.setFontAndSize(bf, 12);
				over.setColorFill(new Color(0x00, 0x00, 0x00));
				over.showTextAligned(PdfContentByte.ALIGN_LEFT, CUBean.getUserDateString(report_date), 121, 733, 0f);
				over.endText();

				String client_type = " - " + _person.getTitleString();
				if (client_type.equals(" - [DEFAULT]"))
					client_type = "";
				
				try
				{
					CustomerRet customer_ret_obj = CustomerRet.getCustomer(_company, _person.getEmployeeNumberString());

					over.beginText();
					over.setFontAndSize(bf, 12);
					over.setColorFill(new Color(0x00, 0x00, 0x00));
					over.showTextAligned(PdfContentByte.ALIGN_LEFT, customer_ret_obj.getPriceLevelNumber() + client_type, 148, 720, 0f);
					over.endText();
				}
				catch (ObjectNotFoundException x)
				{
					over.beginText();
					over.setFontAndSize(bf, 12);
					over.setColorFill(new Color(0x00, 0x00, 0x00));
					over.showTextAligned(PdfContentByte.ALIGN_LEFT, "[NOT FOUND] " + client_type, 148, 720, 0f);
					over.endText();
				}
		

				over.beginText();
				over.setFontAndSize(bf, 12);
				over.setColorFill(new Color(0x00, 0x00, 0x00));
				over.showTextAligned(PdfContentByte.ALIGN_LEFT, _person.getAddressesString(), 350, 746, 0f);
				over.endText();

				over.beginText();
				over.setFontAndSize(bf, 12);
				over.setColorFill(new Color(0x00, 0x00, 0x00));
				over.showTextAligned(PdfContentByte.ALIGN_LEFT, _person.getPhoneNumbersString(), 345, 733, 0f);
				over.endText();

				over.beginText();
				over.setFontAndSize(bf, 12);
				over.setColorFill(new Color(0x00, 0x00, 0x00));
				over.showTextAligned(PdfContentByte.ALIGN_LEFT, _person.getEmail(), 347, 720, 0f);
				over.endText();


				/*
				Iterator appt_itr = AppointmentBean.getAppointmentsForClient(_person, report_date).iterator();
				for (int appt_row = 0; appt_itr.hasNext() && (appt_row < 17); appt_row++)
				{
					AppointmentBean appt = (AppointmentBean)appt_itr.next();
					Date appt_date = appt.getAppointmentDate();

					over.beginText();
					over.setFontAndSize(bf, 12);
					over.setColorFill(new Color(0x00, 0x00, 0x00));
					over.showTextAligned(PdfContentByte.ALIGN_LEFT, CUBean.getUserDateString(appt_date), 120, appt_start_y - (appt_row * appt_spacing), 0f);
					over.endText();

					over.beginText();
					over.setFontAndSize(bf, 12);
					over.setColorFill(new Color(0x00, 0x00, 0x00));
					over.showTextAligned(PdfContentByte.ALIGN_LEFT, CUBean.getUserTimeString(appt_date), 219, appt_start_y - (appt_row * appt_spacing), 0f);
					over.endText();

					over.beginText();
					over.setFontAndSize(bf, 12);
					over.setColorFill(new Color(0x00, 0x00, 0x00));
					over.showTextAligned(PdfContentByte.ALIGN_LEFT, appt.getType().getLabel(), 344, appt_start_y - (appt_row * appt_spacing), 0f);
					over.endText();

					over.beginText();
					over.setFontAndSize(bf, 12);
					over.setColorFill(new Color(0x00, 0x00, 0x00));
					over.showTextAligned(PdfContentByte.ALIGN_LEFT, appt.getPractitioner().getLabel(), 498, appt_start_y - (appt_row * appt_spacing), 0f);
					over.endText();
				}
				 */






		    }
		    // adding an extra page
		    //stamp.insertPage(1, PageSize.A4);
		    over = stamp.getOverContent(1);
		    stamp.close();





		}
		catch (Throwable e)
		{
			e.printStackTrace();
		}

		return file;

	} // generateCheckoutSheet




} // CheckoutSheetBuilder



