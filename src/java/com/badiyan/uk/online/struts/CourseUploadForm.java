package com.badiyan.uk.online.struts;

import org.apache.struts.upload.FormFile;
import org.apache.struts.action.ActionForm;

public class
CourseUploadForm
	extends ActionForm
{
	FormFile uploadedFile;

	public FormFile
	getUploadInput()
	{
		return uploadedFile;
	}

	public FormFile
	getFile()
	{
		return uploadedFile;
	}

	public String
	getUrlInput()
	{
		return null;
	}

	public void
	setUploadInput(FormFile _file)
	{
		uploadedFile = _file;
	}

	public void
	setFile(FormFile _file)
	{
		uploadedFile = _file;
	}

}