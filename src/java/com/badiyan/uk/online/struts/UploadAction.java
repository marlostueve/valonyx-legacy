// Decompiled by DJ v2.9.9.61 Copyright 2000 Atanas Neshkov  Date: 10/24/2003 10:26:08 AM
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3)
// Source File Name:   UploadAction.java

package com.badiyan.uk.online.struts;

import java.io.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts.action.*;
import org.apache.struts.upload.FormFile;

//import com.badiyan.uk.forms.UploadForm;

// Referenced classes of package org.apache.struts.webapp.upload:
//            UploadForm

public class UploadAction extends Action
{

    public UploadAction()
    {
    }

    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
        throws Exception
    {
		System.out.println("");
		System.out.println("execute() invoked in UploadAction");
		System.out.println("");
        if(form instanceof UploadForm)
        {
            String encoding = request.getCharacterEncoding();
            if(encoding != null && encoding.equalsIgnoreCase("utf-8"))
                response.setContentType("text/html; charset=utf-8");
            UploadForm theForm = (com.badiyan.uk.online.struts.UploadForm)form;
            String text = theForm.getTheText();
            String queryValue = theForm.getQueryParam();
            FormFile file = theForm.getTheFile();
            String fileName = file.getFileName();
            String contentType = file.getContentType();
            boolean writeFile = theForm.getWriteFile();
            String size = file.getFileSize() + " bytes";
            String data = null;
            try
            {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                InputStream stream = file.getInputStream();
                if(!writeFile)
                {
                    if(file.getFileSize() < 0x3e8000)
                    {
                        byte buffer[] = new byte[8192];
                        for(int bytesRead = 0; (bytesRead = stream.read(buffer, 0, 8192)) != -1;)
                            baos.write(buffer, 0, bytesRead);

                        data = new String(baos.toByteArray());
                    } else
                    {
                        data = new String("The file is greater than 4MB,  and has not been written to stream. File Size: " + file.getFileSize() + " bytes. This is a" + " limitation of this particular web application, hard-coded" + " in org.apache.struts.webapp.upload.UploadAction");
                    }
                } else
                {
                    OutputStream bos = new FileOutputStream(theForm.getFilePath());
                    int bytesRead = 0;
                    byte buffer[] = new byte[8192];
                    while((bytesRead = stream.read(buffer, 0, 8192)) != -1)
                        bos.write(buffer, 0, bytesRead);
                    bos.close();
                    data = "The file has been written to \"" + theForm.getFilePath() + "\"";
                }
                stream.close();
            }
            catch(FileNotFoundException fnfe)
            {
                return null;
            }
            catch(IOException ioe)
            {
                return null;
            }
            request.setAttribute("text", text);
            request.setAttribute("queryValue", queryValue);
            request.setAttribute("fileName", fileName);
            request.setAttribute("contentType", contentType);
            request.setAttribute("size", size);
            request.setAttribute("data", data);
            file.destroy();
            return mapping.findForward("display");
        } else
        {
            return null;
        }
    }
}