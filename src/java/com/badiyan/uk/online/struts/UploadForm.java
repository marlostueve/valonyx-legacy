package com.badiyan.uk.online.struts;

import javax.servlet.http.HttpServletRequest;
import org.apache.struts.action.*;
import org.apache.struts.upload.FormFile;

public class UploadForm extends ActionForm
{

    public UploadForm()
    {
    }

    public String getTheText()
    {
        return theText;
    }

    public void setTheText(String theText)
    {
        this.theText = theText;
    }

    public String getQueryParam()
    {
        return queryParam;
    }

    public void setQueryParam(String queryParam)
    {
        this.queryParam = queryParam;
    }

    public FormFile getTheFile()
    {
        return theFile;
    }

    public void setTheFile(FormFile theFile)
    {
        this.theFile = theFile;
    }

    public void setWriteFile(boolean writeFile)
    {
        this.writeFile = writeFile;
    }

    public boolean getWriteFile()
    {
        return writeFile;
    }

    public void setFilePath(String filePath)
    {
        this.filePath = filePath;
    }

    public String getFilePath()
    {
        return filePath;
    }

    public void reset()
    {
        writeFile = false;
    }

    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request)
    {
        ActionErrors errors = null;
        Boolean maxLengthExceeded = (Boolean)request.getAttribute("org.apache.struts.upload.MaxLengthExceeded");
        if(maxLengthExceeded != null && maxLengthExceeded.booleanValue())
        {
            errors = new ActionErrors();
            errors.add("org.apache.struts.webapp.upload.MaxLengthExceeded", new ActionMessage("maxLengthExceeded"));
        }
        return errors;
    }

    public static final String ERROR_PROPERTY_MAX_LENGTH_EXCEEDED = "org.apache.struts.webapp.upload.MaxLengthExceeded";
    protected String theText;
    protected String queryParam;
    protected boolean writeFile;
    protected FormFile theFile;
    protected String filePath;
}
