package com.badiyan.uk.online.struts;

import org.apache.struts.upload.FormFile;
import org.apache.struts.action.ActionForm;

public class
ResourceUploadForm
	extends ActionForm
{
	/*
	 *<form-property name="uploadInput" type="java.lang.String"/>
      <form-property name="urlInput" type="java.lang.String"/>
      <form-property name="mediaType1" type="java.lang.Boolean"/>
      <form-property name="mediaType2" type="java.lang.Boolean"/>
      <form-property name="mediaType3" type="java.lang.Boolean"/>
      <form-property name="mediaType4" type="java.lang.Boolean"/>
      <form-property name="mediaType5" type="java.lang.Boolean"/>
      <form-property name="mediaType6" type="java.lang.Boolean"/>
	 */

	FormFile uploadedFile;
	Boolean bool1;
	Boolean bool2;
	Boolean bool3;
	Boolean bool4;
	Boolean bool5;
	Boolean bool6;
	Boolean bool7;

	public FormFile
	getUploadInput()
	{
		System.out.println("");
		System.out.println("getUploadInput() invoked in ResourceUploadForm - " + uploadedFile);
		System.out.println("");
		return uploadedFile;
	}

	public FormFile
	getFile()
	{
		System.out.println("");
		System.out.println("getFile() invoked in ResourceUploadForm - " + uploadedFile);
		System.out.println("");
		return uploadedFile;
	}

	public Boolean
	getMediaType1()
	{
		return bool1;
	}

	public Boolean
	getMediaType2()
	{
		return bool2;
	}

	public Boolean
	getMediaType3()
	{
		return bool3;
	}

	public Boolean
	getMediaType4()
	{
		return bool4;
	}

	public Boolean
	getMediaType5()
	{
		return bool5;
	}

	public Boolean
	getMediaType6()
	{
		return bool6;
	}

	public Boolean
	getMediaType7()
	{
		return bool7;
	}

	public String
	getUrlInput()
	{
		return null;
	}

	public void
	setUploadInput(FormFile _file)
	{
		System.out.println("");
		System.out.println("setUploadInput() invoked in ResourceUploadForm");
		System.out.println("");
		uploadedFile = _file;
	}

	public void
	setFile(FormFile _file)
	{
		System.out.println("");
		System.out.println("setFile() invoked in ResourceUploadForm");
		System.out.println("");
		uploadedFile = _file;
	}

	public void
	setMediaType1(Boolean _bool)
	{
		System.out.println("");
		System.out.println("setMediaType1() invoked in ResourceUploadForm - " + _bool);
		System.out.println("");
		bool1 = _bool;
	}

	public void
	setMediaType2(Boolean _bool)
	{
		bool2 = _bool;
	}

	public void
	setMediaType3(Boolean _bool)
	{
		bool3 = _bool;
	}

	public void
	setMediaType4(Boolean _bool)
	{
		bool4 = _bool;
	}

	public void
	setMediaType5(Boolean _bool)
	{
		bool5 = _bool;
	}

	public void
	setMediaType6(Boolean _bool)
	{
		bool6 = _bool;
	}

	public void
	setMediaType7(Boolean _bool)
	{
		bool7 = _bool;
	}
}
