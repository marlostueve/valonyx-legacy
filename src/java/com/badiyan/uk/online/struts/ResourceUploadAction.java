package com.badiyan.uk.online.struts;

import com.badiyan.torque.*;
import com.badiyan.uk.beans.*;
import com.badiyan.uk.exceptions.*;
//import com.badiyan.uk.forms.*;

import com.badiyan.uk.online.beans.*;

import java.text.ParseException;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.struts.action.*;
import org.apache.struts.upload.FormFile;

import org.textmining.text.extraction.*;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.analysis.standard.*;
import org.apache.lucene.search.*;

import java.io.*;

import org.pdfbox.searchengine.lucene.*;
import org.pdfbox.util.*;
import org.pdfbox.pdmodel.*;

/**
 * Implementation of <strong>Action</strong> that validates a course audience.
 *
 * @author Marlo Stueve
 */
public final class
ResourceUploadAction
    extends Action
{
    public ActionForward
    execute(ActionMapping _mapping, ActionForm _form, HttpServletRequest _request, HttpServletResponse _response)
	throws Exception
    {
	System.out.println("execute() invoked in ResourceUploadAction");

	ActionErrors errors = new ActionErrors();

	ResourceBean resourceBean = null;
	UKOnlineLoginBean loginBean = null;
	HttpSession session = _request.getSession(false);

	try
	{
	    // Check the session to see if there's a course in progress...

	    if (session != null)
	    {
		loginBean = (UKOnlineLoginBean)session.getAttribute("loginBean");
		if (loginBean == null)
		    return (_mapping.findForward("session_expired"));
		else
		{
		    try
		    {
			UKOnlinePersonBean person = (UKOnlinePersonBean)loginBean.getPerson();
			if (!person.hasRole(RoleBean.SYSTEM_ADMINISTRATOR_ROLE_NAME) && !person.hasRole(RoleBean.COMPANY_ADMINISTRATOR_ROLE_NAME))
			    return (_mapping.findForward("session_expired"));
		    }
		    catch (IllegalValueException x)
		    {
			return (_mapping.findForward("session_expired"));
		    }
		}

		resourceBean = (ResourceBean)session.getAttribute("adminResource");
	    }
	    else
		return (_mapping.findForward("session_expired"));

	    Boolean mediaType1 = (Boolean)PropertyUtils.getSimpleProperty(_form, "mediaType1");
	    Boolean mediaType2 = (Boolean)PropertyUtils.getSimpleProperty(_form, "mediaType2");
	    Boolean mediaType3 = (Boolean)PropertyUtils.getSimpleProperty(_form, "mediaType3");
	    Boolean mediaType4 = (Boolean)PropertyUtils.getSimpleProperty(_form, "mediaType4");
	    Boolean mediaType5 = (Boolean)PropertyUtils.getSimpleProperty(_form, "mediaType5");
	    Boolean mediaType7 = (Boolean)PropertyUtils.getSimpleProperty(_form, "mediaType7");

	    boolean m1 = false;
	    boolean m2 = false;
	    boolean m3 = false;
	    boolean m4 = false;
	    boolean m5 = false;
	    boolean m7 = false;

	    if (mediaType1 != null)
		m1 = mediaType1.booleanValue();
	    if (mediaType2 != null)
		m2 = mediaType2.booleanValue();
	    if (mediaType3 != null)
		m3 = mediaType3.booleanValue();
	    if (mediaType4 != null)
		m4 = mediaType4.booleanValue();
	    if (mediaType5 != null)
		m5 = mediaType5.booleanValue();
	    if (mediaType7 != null)
		m7 = mediaType7.booleanValue();

	    //if (m1 || m2 || m3 || m4 || m5 || m7)
	    //{
		ResourceUploadForm fileForm = (ResourceUploadForm)_form;
		FormFile uploadedFile = fileForm.getUploadInput();

		System.out.println("++++++++++++++ - " + uploadedFile.getFileName());

		InputStream uploadInStream = uploadedFile.getInputStream();
		String filename = uploadedFile.getFileName();
		int c = 0;
		c = uploadInStream.read();
		if (c != -1)
		{
		    String resourceFolder = CUBean.getProperty("cu.realPath") + File.separator + CUBean.getProperty("cu.resourcesFolder");
		    //String resourceFolder = CUBean.getProperty("cu.resourceFolder");
		    if (resourceFolder == null)
			throw new IllegalValueException("Property not defined: cu.resourceFolder");

		    FileOutputStream fOut = new FileOutputStream(resourceFolder + filename);
		    while (c != -1)
		    {
			fOut.write(c);
			c = uploadInStream.read();
		    }
		    fOut.flush();
		    fOut.close();

		    if (m1)
			resourceBean.setType(ResourceBean.WORD_RESOURCE_TYPE);
		    else if (m2)
			resourceBean.setType(ResourceBean.POWERPOINT_RESOURCE_TYPE);
		    else if (m3)
			resourceBean.setType(ResourceBean.ACROBAT_RESOURCE_TYPE);
		    else if (m4)
			resourceBean.setType(ResourceBean.FLASH_RESOURCE_TYPE);
		    else if (m5)
			resourceBean.setType(ResourceBean.VIDEO_RESOURCE_TYPE);
		    else if (m7)
			resourceBean.setType(ResourceBean.CD_RESOURCE_TYPE);

		    resourceBean.setURL(filename);



		    /*
		     *
		    // index word and pdf documents

		    if (m1) // WORD type
		    {
			FileInputStream in = new FileInputStream(resourceFolder + filename);
			WordExtractor extractor = new WordExtractor();

			String str = extractor.extractText(in);
			System.out.println(">");
			System.out.println("EXTRACTED >" + str);
			System.out.println(">");

			Document doc = new Document();

			Field id_field = new Field("id", resourceBean.getIdString(), Field.Store.YES, Field.Index.TOKENIZED);
			Field keyword_field = new Field("keyword", str, Field.Store.YES, Field.Index.TOKENIZED);


			doc.add(id_field);
			doc.add(keyword_field);
			CUBean.index(doc);

		    }

		    if (m3) // PDF type
		    {
			FileInputStream in = new FileInputStream(resourceFolder + filename);
			PDDocument pd_document = PDDocument.load(in);

			PDFTextStripper stripper = new PDFTextStripper();
			String str = stripper.getText(pd_document);

			System.out.println(">");
			System.out.println("EXTRACTED >" + str);
			System.out.println(">");

			Document doc = new Document();

			Field id_field = new Field("id", resourceBean.getIdString(), Field.Store.YES, Field.Index.TOKENIZED);
			Field keyword_field = new Field("keyword", str, Field.Store.YES, Field.Index.TOKENIZED);

			doc.add(id_field);
			doc.add(keyword_field);
			CUBean.index(doc);

		    }
		     */
		}
		else
		{
		    errors.add(ActionErrors.GLOBAL_MESSAGE, new ActionMessage("exception", "Cannot find specified file."));
		}
	    //}
	    //else
	    //{
		// no type selected
		//errors.add(ActionErrors.GLOBAL_MESSAGE, new ActionMessage("exception", "You must select a resource type for the upload."));
	    //}


	    // determine the type of the passed file



	    //resourceBean.setType(ResourceBean.);

			/*
			resourceBean.setURL(urlInput);

			resourceBean.setName(nameInput);
			if (displayAsNew.booleanValue())
				resourceBean.setDisplayAsNewDays(displayAsNewInput.shortValue());
			else
				resourceBean.setDisplayAsNewDays((short)0);
			resourceBean.setReleasedDate(releasedDateInput);
			resourceBean.setExpirationDate(expiresDateInput);
			resourceBean.setOwner(loginBean.getPerson());
			System.out.println("~~ABOUT TO SAVE - " + resourceBean.isNew());
			resourceBean.setType(ResourceBean.UNKNOWN_RESOURCE_TYPE);
			resourceBean.setActionType(ResourceBean.UNKNOWN_ACTION_TYPE);
			resourceBean.setContentType(ResourceBean.UNKNOWN_CONTENT_TYPE);
			resourceBean.setTextType(ResourceBean.UNKNOWN_TEXT_TYPE);
			resourceBean.save();
			System.out.println("~~SAVED - " + resourceBean.isNew());

			resourceBean.deleteAssociations();

			if (!manufacturerSelect.equals("0"))
				resourceBean.associate(ManufacturerBean.getManufacturer(manufacturerSelect));
			if (!categorySelect.equals("0"))
				resourceBean.associate(CategoryBean.getCategory(categorySelect));
			if (!userGroupSelect.equals("0"))
				resourceBean.associate(PersonGroupBean.getPersonGroup(userGroupSelect));
			//if (!subGroupSelect.equals("0"))
			//	faqSearchBean.setSub(PersonGroupBean.getPersonGroup(userGroupSelect));
			if (!regionSelect.equals("0"))
				resourceBean.associate(DepartmentBean.getDepartment(Integer.parseInt(regionSelect)));
			if (!locationSelect.equals("0"))
				resourceBean.associate(DepartmentBean.getDepartment(Integer.parseInt(locationSelect)));
			if (!jobTitleSelect.equals("0"))
				resourceBean.associate(jobTitleSelect);
			 */

	    resourceBean.save();

	}
	catch (IllegalValueException x)
	{
	    errors.add(ActionErrors.GLOBAL_MESSAGE, new ActionMessage("exception", x.getMessage()));
	}
	catch (Exception x)
	{
	    x.printStackTrace();
	    errors.add(ActionErrors.GLOBAL_MESSAGE, new ActionMessage("exception", x.getMessage()));
	}

	// Report any errors we have discovered back to the original form
	if (!errors.isEmpty())
	{
	    saveErrors(_request, errors);
	    return _mapping.getInputForward();
	}

	// Remove the obsolete form bean
	if (_mapping.getAttribute() != null)
	{
	    if ("request".equals(_mapping.getScope()))
		_request.removeAttribute(_mapping.getAttribute());
	    else
		session.removeAttribute(_mapping.getAttribute());
	}

	// Forward control to the specified success URI
	return (_mapping.findForward("success"));
    }

    public void
    index(Document _document)
    {
	String indexPath = CUBean.getProperty("cu.realPath") + File.separator + CUBean.getProperty("cu.resourcesFolder") + File.separator + "index";
	IndexWriter writer;

	try
	{
	    //first search to see if an index exists for this document already
	    IndexReader reader = IndexReader.open(indexPath);
	    Field deleteField = _document.getField("id");
	    System.out.println("deleteField >" + deleteField.stringValue());
	    Term deleteTerm = new Term("id", deleteField.stringValue());
	    System.out.println("freq >" + reader.docFreq(deleteTerm));
	    int numDeleted = reader.deleteDocuments(deleteTerm);
	    //int numDeleted = reader.delete(deleteTerm);
	    System.out.println("numDeleted >" + numDeleted);
	    System.out.println("max doc >" + reader.numDocs());

	    //System.out.println("max doc >" + reader.numDocs());

	    reader.close();

	    writer = new IndexWriter(indexPath, new StandardAnalyzer(), false);
	    writer.addDocument(_document);
	    writer.optimize();
	    writer.close();
	}
	catch (Exception x)
	{
	    System.out.println("Error >" + x.getMessage());
	    x.printStackTrace();
	}
    }
}