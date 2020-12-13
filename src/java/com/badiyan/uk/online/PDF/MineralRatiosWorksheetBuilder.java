/*
 * MineralRatiosWorksheetBuilder.java
 *
 * Created on July 22, 2007, 8:38 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.badiyan.uk.online.PDF;


import java.io.*;
import java.util.*;

import com.badiyan.uk.online.beans.*;
//import com.badiyan.uk.exceptions.ObjectNotFoundException;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
//import com.lowagie.text.pdf.codec.TiffImage;

import com.badiyan.torque.*;
import com.badiyan.uk.beans.*;

import java.awt.Color;

import java.util.*;
import java.math.*;
import com.badiyan.uk.exceptions.*;

public class
MineralRatiosWorksheetBuilder
{



	/**
	 * Manages the creation of the printed plan's individual pages.
	 * @param args
	 */
	public static String
	generateMineralRatiosWorksheet(MineralRatiosClientDocumentBean _document)
		throws IllegalValueException
	{
	    /*
		BigDecimal one_hundred = new BigDecimal(100);
		BigDecimal one = new BigDecimal(1);

		Vector pre_totals = new Vector();
		Vector post_totals = new Vector();
		Vector alt_totals = new Vector();
		Vector pre_correct = new Vector();
		Vector post_correct = new Vector();
		Vector alt_correct = new Vector();
		for (int i = 0; i < 77; i++)
		{
			BigDecimal a1 = new BigDecimal(0);
			BigDecimal a2 = new BigDecimal(0);
			BigDecimal a3 = new BigDecimal(0);
			BigDecimal a4 = new BigDecimal(0);
			BigDecimal a5 = new BigDecimal(0);
			BigDecimal a6 = new BigDecimal(0);
			pre_totals.addElement(a1);
			post_totals.addElement(a2);
			alt_totals.addElement(a3);
			pre_correct.addElement(a4);
			post_correct.addElement(a5);
			alt_correct.addElement(a6);
		}
	     */

		// Hair-analysis-ratio-worksheet-new-blank.pdf

		String realPath = CUBean.getProperty("cu.realPath");
		String resourcesFolder = CUBean.getProperty("cu.resourcesFolder");
		if (resourcesFolder == null)
			throw new IllegalValueException("Property not defined: cu.resourcesFolder");
		resourcesFolder = realPath + resourcesFolder;



		//String pdf_file = "C:\\odd.pdf";
		String file = "mineral-ratios-worksheet-" + System.currentTimeMillis() + ".pdf";
		String pdf_file = resourcesFolder + "pdf/" + file;

		String pdf_file_source = resourcesFolder + "pdf/mineral-ratios-worksheet-new-blank.pdf";


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
		    if (i < n) {
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
			over.setColorFill(new Color(0x00, 0x00, 0xFF));
			over.showTextAligned(PdfContentByte.ALIGN_LEFT, _document.getPerson().getLabel(), 106, 487, 0f);
			over.endText();

			over.beginText();
			over.setFontAndSize(bf, 8);
			over.showTextAligned(PdfContentByte.ALIGN_LEFT, _document.getReportDateString(), 70, 225, 0f);
			over.endText();

			over.beginText();
			over.setFontAndSize(bf, 8);
			over.showTextAligned(PdfContentByte.ALIGN_LEFT, _document.getInitialRecommendationsString(), 370, 225, 0f);
			over.endText();

			MineralRatiosWorksheetBuilder.doParagraph(_document.getSupplementsHerbalMixturesString(), 37, 195, 290, over, bf, 8);
			MineralRatiosWorksheetBuilder.doParagraph(_document.getDetoxExerciseDietStressManagementString(), 303, 195, 370, over, bf, 8);
			MineralRatiosWorksheetBuilder.doParagraph(_document.getNextAppointmentString(), 641, 195, 115, over, bf, 8);



			Iterator itrx = MineralRatioBean.getMineralRatios(_document.getPerson()).iterator();
			for (int xx = 0; ((xx < 4) && (itrx.hasNext())); xx++ )
			{
			    MineralRatioBean ratio_obj = (MineralRatioBean)itrx.next();

			    over.beginText();
			    over.setFontAndSize(bf, 12);
			    over.showTextAligned(PdfContentByte.ALIGN_LEFT, ratio_obj.getCaMgString(), 305 + (xx * 50), 430, 0f);
			    over.endText();

			    over.beginText();
			    over.setFontAndSize(bf, 12);
			    over.showTextAligned(PdfContentByte.ALIGN_LEFT, ratio_obj.getCaKString(), 305 + (xx * 50), 405, 0f);
			    over.endText();

			    over.beginText();
			    over.setFontAndSize(bf, 12);
			    over.showTextAligned(PdfContentByte.ALIGN_LEFT, ratio_obj.getCaPString(), 305 + (xx * 50), 380, 0f);
			    over.endText();

			    over.beginText();
			    over.setFontAndSize(bf, 12);
			    over.showTextAligned(PdfContentByte.ALIGN_LEFT, ratio_obj.getNaMgString(), 305 + (xx * 50), 355, 0f);
			    over.endText();

			    over.beginText();
			    over.setFontAndSize(bf, 12);
			    over.showTextAligned(PdfContentByte.ALIGN_LEFT, ratio_obj.getNaKString(), 305 + (xx * 50), 330, 0f);
			    over.endText();

			    over.beginText();
			    over.setFontAndSize(bf, 12);
			    over.showTextAligned(PdfContentByte.ALIGN_LEFT, ratio_obj.getZnCuString(), 305 + (xx * 50), 305, 0f);
			    over.endText();
			}


		    }

		    if (i < n) {
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
			over.setColorFill(new Color(0x00, 0x00, 0xFF));
			over.showTextAligned(PdfContentByte.ALIGN_LEFT, _document.getPerson().getLabel(), 106, 516, 0f);
			over.endText();


			over.beginText();
			over.setFontAndSize(bf, 8);
			over.showTextAligned(PdfContentByte.ALIGN_LEFT, _document.getPhaseIDateString(), 80, 434, 0f);
			over.endText();

			over.beginText();
			over.setFontAndSize(bf, 8);
			over.showTextAligned(PdfContentByte.ALIGN_LEFT, _document.getPhaseIIDateString(), 80, 342, 0f);
			over.endText();

			over.beginText();
			over.setFontAndSize(bf, 8);
			over.showTextAligned(PdfContentByte.ALIGN_LEFT, _document.getPhaseIIIDateString(), 80, 251, 0f);
			over.endText();

			over.beginText();
			over.setFontAndSize(bf, 8);
			over.showTextAligned(PdfContentByte.ALIGN_LEFT, _document.getPhaseIVDateString(), 80, 173, 0f);
			over.endText();

			//String ostr = null;

			MineralRatiosWorksheetBuilder.doParagraph(_document.get("supplements_header_phaseI"), 128, 445, 180, over, bf, 10);
			//ostr = _document.get("supplements_phaseI").replaceAll("\\|", ", ");
			MineralRatiosWorksheetBuilder.doParagraph(_document.get("supplements_phaseI").replaceAll("\\|", ", "), 128, 421, 180, over, bf, 8, 13, 5);

			MineralRatiosWorksheetBuilder.doParagraph(_document.get("supplements_header_phaseII"), 128, 354, 180, over, bf, 10);
			MineralRatiosWorksheetBuilder.doParagraph(_document.get("supplements_phaseII").replaceAll("\\|", ", "), 128, 329, 180, over, bf, 8, 13, 5);

			MineralRatiosWorksheetBuilder.doParagraph(_document.get("supplements_header_phaseIII"), 128, 260, 180, over, bf, 10);
			MineralRatiosWorksheetBuilder.doParagraph(_document.get("supplements_phaseIII").replaceAll("\\|", ", "), 128, 238, 180, over, bf, 8, 13, 4);

			MineralRatiosWorksheetBuilder.doParagraph(_document.get("supplements_header_phaseIV"), 128, 182, 180, over, bf, 10);
			MineralRatiosWorksheetBuilder.doParagraph(_document.get("supplements_phaseIV").replaceAll("\\|", ", "), 128, 160, 180, over, bf, 8, 13, 5);



			MineralRatiosWorksheetBuilder.doParagraph(_document.get("detox_header_phaseI"), 308, 445, 90, over, bf, 10);
			MineralRatiosWorksheetBuilder.doParagraph(_document.get("detox_phaseI").replaceAll("\\|", ", "), 308, 421, 90, over, bf, 8, 13, 5);

			MineralRatiosWorksheetBuilder.doParagraph(_document.get("detox_header_phaseII"), 308, 354, 90, over, bf, 10);
			MineralRatiosWorksheetBuilder.doParagraph(_document.get("detox_phaseII").replaceAll("\\|", ", "), 308, 329, 90, over, bf, 8, 13, 5);

			MineralRatiosWorksheetBuilder.doParagraph(_document.get("detox_header_phaseIII"), 308, 260, 90, over, bf, 10);
			MineralRatiosWorksheetBuilder.doParagraph(_document.get("detox_phaseIII").replaceAll("\\|", ", "), 308, 238, 90, over, bf, 8, 13, 4);

			MineralRatiosWorksheetBuilder.doParagraph(_document.get("detox_header_phaseIV"), 308, 182, 90, over, bf, 10);
			MineralRatiosWorksheetBuilder.doParagraph(_document.get("detox_phaseIV").replaceAll("\\|", ", "), 308, 160, 90, over, bf, 8, 13, 5);



			MineralRatiosWorksheetBuilder.doParagraph(_document.get("exercise_header_phaseI"), 398, 445, 95, over, bf, 10);
			MineralRatiosWorksheetBuilder.doParagraph(_document.get("exercise_phaseI").replaceAll("\\|", ", "), 398, 421, 95, over, bf, 8, 13, 5);

			MineralRatiosWorksheetBuilder.doParagraph(_document.get("exercise_header_phaseII"), 398, 354, 95, over, bf, 10);
			MineralRatiosWorksheetBuilder.doParagraph(_document.get("exercise_phaseII").replaceAll("\\|", ", "), 398, 329, 95, over, bf, 8, 13, 5);

			MineralRatiosWorksheetBuilder.doParagraph(_document.get("exercise_header_phaseIII"), 398, 260, 95, over, bf, 10);
			MineralRatiosWorksheetBuilder.doParagraph(_document.get("exercise_phaseIII").replaceAll("\\|", ", "), 398, 238, 95, over, bf, 8, 13, 4);

			MineralRatiosWorksheetBuilder.doParagraph(_document.get("exercise_header_phaseIV"), 398, 182, 95, over, bf, 10);
			MineralRatiosWorksheetBuilder.doParagraph(_document.get("exercise_phaseIV").replaceAll("\\|", ", "), 398, 160, 95, over, bf, 8, 13, 5);




			MineralRatiosWorksheetBuilder.doParagraph(_document.get("diet_header_phaseI"), 511, 445, 160, over, bf, 10);
			MineralRatiosWorksheetBuilder.doParagraph(_document.get("diet_phaseI").replaceAll("\\|", ", "), 511, 421, 160, over, bf, 8, 13, 5);

			MineralRatiosWorksheetBuilder.doParagraph(_document.get("diet_header_phaseII"), 511, 354, 160, over, bf, 10);
			MineralRatiosWorksheetBuilder.doParagraph(_document.get("diet_phaseII").replaceAll("\\|", ", "), 511, 329, 160, over, bf, 8, 13, 5);

			MineralRatiosWorksheetBuilder.doParagraph(_document.get("diet_header_phaseIII"), 511, 260, 160, over, bf, 10);
			MineralRatiosWorksheetBuilder.doParagraph(_document.get("diet_phaseIII").replaceAll("\\|", ", "), 511, 238, 160, over, bf, 8, 13, 4);

			MineralRatiosWorksheetBuilder.doParagraph(_document.get("diet_header_phaseIV"), 511, 182, 160, over, bf, 10);
			MineralRatiosWorksheetBuilder.doParagraph(_document.get("diet_phaseIV").replaceAll("\\|", ", "), 511, 160, 160, over, bf, 8, 13, 5);





			MineralRatiosWorksheetBuilder.doParagraph(_document.get("stress_header_phaseI"), 672, 445, 50, over, bf, 10);
			MineralRatiosWorksheetBuilder.doParagraph(_document.get("stress_phaseI").replaceAll("\\|", ", "), 672, 421, 70, over, bf, 8, 13, 5);

			MineralRatiosWorksheetBuilder.doParagraph(_document.get("stress_header_phaseII"), 672, 354, 50, over, bf, 10);
			MineralRatiosWorksheetBuilder.doParagraph(_document.get("stress_phaseII").replaceAll("\\|", ", "), 672, 329, 70, over, bf, 8, 13, 5);

			MineralRatiosWorksheetBuilder.doParagraph(_document.get("stress_header_phaseIII"), 672, 260, 50, over, bf, 10);
			MineralRatiosWorksheetBuilder.doParagraph(_document.get("stress_phaseIII").replaceAll("\\|", ", "), 672, 238, 70, over, bf, 8, 13, 4);

			MineralRatiosWorksheetBuilder.doParagraph(_document.get("stress_header_phaseIV"), 672, 182, 50, over, bf, 10);
			MineralRatiosWorksheetBuilder.doParagraph(_document.get("stress_phaseIV").replaceAll("\\|", ", "), 672, 160, 70, over, bf, 8, 13, 5);


			/*
			over.beginText();
			over.setFontAndSize(bf, 8);
			over.showTextAligned(PdfContentByte.ALIGN_LEFT, _document.getReportDateString(), 70, 225, 0f);
			over.endText();

			over.beginText();
			over.setFontAndSize(bf, 8);
			over.showTextAligned(PdfContentByte.ALIGN_LEFT, _document.getInitialRecommendationsString(), 370, 225, 0f);
			over.endText();
			 */

			/*
			MineralRatiosWorksheetBuilder.doParagraph(_document.getSupplementsHerbalMixturesString(), 37, 195, 290, over, bf);
			MineralRatiosWorksheetBuilder.doParagraph(_document.getDetoxExerciseDietStressManagementString(), 303, 195, 370, over, bf);
			MineralRatiosWorksheetBuilder.doParagraph(_document.getNextAppointmentString(), 641, 195, 115, over, bf);
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

	}

	private static void
	doParagraph(String _str, int _x, int _y, int _width, PdfContentByte _over, BaseFont _bf, int _font_size)
	{

	    int num_chars = _width / 4;
	    int space_between_rows = 11;
	    int row = 0;
	    int pos = 0;

	    boolean done = false;

	    while (!done)
	    {
		System.out.println("");
		System.out.println("pos >" + pos);

		String string_to_print = "";
		System.out.println("num_chars >" + num_chars);

		if (num_chars < (_str.length() - pos))
		{
		    string_to_print = _str.substring(pos, pos + num_chars);

		    System.out.println("string_to_print >" + string_to_print);
		    System.out.println("string_to_print.indexOf(' ') >" + string_to_print.indexOf(' '));
		    System.out.println("string_to_print.lastIndexOf(' ') >" + string_to_print.lastIndexOf(' '));

		    if (string_to_print.indexOf(' ') > -1)
			string_to_print = string_to_print.substring(0, string_to_print.lastIndexOf(' '));
		}
		else
		{
		    string_to_print = _str.substring(pos);
		    done = true;
		}



		_over.beginText();
		_over.setFontAndSize(_bf, _font_size);

		//System.out.println("_x - (row * space_between_rows) >" + (_x - (row * space_between_rows)));
		_over.showTextAligned(PdfContentByte.ALIGN_LEFT, string_to_print.trim(), _x , _y - (row * space_between_rows), 0f);
		_over.endText();

		row++;
		System.out.println("pos >" + pos);
		System.out.println("string_to_print.length() >" + string_to_print.length());
		pos = pos + string_to_print.length();
	    }
	}

	private static void
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
		    return;

		pos = pos + string_to_print.length();
	    }
	}

	/*
	private String
	stupidReplace(String _str)
	{
	    StringBuffer buf = new StringBuffer();
	    String[] result = _str.split("\\|");
	    for (int i = 0; i < result.length; i++)
	    {
		buf.append(result[i]);
	    }
	}
	 */


}



