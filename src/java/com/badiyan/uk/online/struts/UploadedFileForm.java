
package com.badiyan.uk.online.struts;

import org.apache.struts.upload.FormFile;
import org.apache.struts.action.ActionForm;

public class UploadedFileForm extends ActionForm {
 FormFile uploadedFile;

 public void setUploadedFile(FormFile file) {
	 System.out.println("");
	 System.out.println("setUploadedFile() invoked");
	 System.out.println("");
 uploadedFile = file;
 } // setUploadedFile

 public FormFile getUploadedFile() {
	System.out.println("");
	 System.out.println("getUploadedFile() invoked");
	 System.out.println("");
 return uploadedFile;
 } // getUploadedFile
} // UploadedFileForm
