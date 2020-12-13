/*
 * CourseWindowAction.java
 *
 * Created on March 5, 2007, 11:57 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.badiyan.uk.online.struts;

import com.badiyan.torque.*;
import com.badiyan.uk.beans.*;
import com.badiyan.uk.exceptions.*;

import com.badiyan.uk.online.beans.*;

import java.text.ParseException;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.struts.action.*;

/**
 * Implementation of <strong>Action</strong> that validates a course prereq.
 *
 * @author Marlo Stueve
 */
public final class
CourseWindowAction
    extends Action
{
    public ActionForward
    execute(ActionMapping _mapping, ActionForm _form, HttpServletRequest _request, HttpServletResponse _response)
	throws Exception
    {
	System.out.println("execute() invoked in CourseWindowAction");
	
	ActionErrors errors = new ActionErrors();
	
	CourseBean courseBean = null;
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
		
		courseBean = (CourseBean)session.getAttribute("adminCourse");
	    }
	    else
		return (_mapping.findForward("session_expired"));
	    
	    
	    /*
	         <form-bean       name="courseWindowForm"
                     type="org.apache.struts.validator.DynaValidatorForm">
      <form-property name="displayFullscreen" type="java.lang.Boolean"/>
      <form-property name="displayToolbar" type="java.lang.Boolean"/>
      <form-property name="displayLocation" type="java.lang.Boolean"/>
      <form-property name="displayStatusBar" type="java.lang.Boolean"/>
      <form-property name="displayMenuBar" type="java.lang.Boolean"/>
      <form-property name="displayScrollBars" type="java.lang.Boolean"/>
      <form-property name="isResizable" type="java.lang.Boolean"/>
      <form-property name="width" type="java.lang.Integer"/>
      <form-property name="height" type="java.lang.Integer"/>
      <form-property name="top" type="java.lang.Integer"/>
      <form-property name="left" type="java.lang.Integer"/>
    </form-bean>
	     */
	    
	    Boolean displayFullscreen = (Boolean)PropertyUtils.getSimpleProperty(_form, "displayFullscreen");
	    Boolean displayToolbar = (Boolean)PropertyUtils.getSimpleProperty(_form, "displayToolbar");
	    Boolean displayLocation = (Boolean)PropertyUtils.getSimpleProperty(_form, "displayLocation");
	    Boolean displayStatusBar = (Boolean)PropertyUtils.getSimpleProperty(_form, "displayStatusBar");
	    Boolean displayMenuBar = (Boolean)PropertyUtils.getSimpleProperty(_form, "displayMenuBar");
	    Boolean displayScrollBars = (Boolean)PropertyUtils.getSimpleProperty(_form, "displayScrollBars");
	    Boolean isResizable = (Boolean)PropertyUtils.getSimpleProperty(_form, "isResizable");
	    Integer width = (Integer)PropertyUtils.getSimpleProperty(_form, "width");
	    Integer height = (Integer)PropertyUtils.getSimpleProperty(_form, "height");
	    Integer top = (Integer)PropertyUtils.getSimpleProperty(_form, "top");
	    Integer left = (Integer)PropertyUtils.getSimpleProperty(_form, "left");
	    
	    System.out.println("displayFullscreen >" + displayFullscreen);
	    System.out.println("displayToolbar >" + displayToolbar);
	    System.out.println("displayLocation >" + displayLocation);
	    System.out.println("displayStatusBar >" + displayStatusBar);
	    System.out.println("displayMenuBar >" + displayMenuBar);
	    System.out.println("displayScrollBars >" + displayScrollBars);
	    System.out.println("isResizable >" + isResizable);
	    System.out.println("width >" + width);
	    System.out.println("height >" + height);
	    System.out.println("top >" + top);
	    System.out.println("left >" + left);
	    
	    
	    CourseWindowSettings settings = courseBean.getWindowSettings();
	    settings.setDisplayFullscreen((displayFullscreen == null) ? (short)0 : (short)1);
	    settings.setDisplayLocation((displayLocation == null) ? (short)0 : (short)1);
	    settings.setDisplayMenuBar((displayMenuBar == null) ? (short)0 : (short)1);
	    settings.setDisplayScrollBars((displayScrollBars == null) ? (short)0 : (short)1);
	    settings.setDisplayStatusBar((displayStatusBar == null) ? (short)0 : (short)1);
	    settings.setDisplayToolbar((displayToolbar == null) ? (short)0 : (short)1);
	    settings.setIsResizable((isResizable == null) ? (short)0 : (short)1);
	    if (height != null)
		settings.setWindowHeight(height.intValue());
	    if (width != null)
		settings.setWindowWidth(width.intValue());
	    if (left != null)
		settings.setWindowLeft(left.intValue());
	    if (top != null)
		settings.setWindowTop(top.intValue());
	    courseBean.saveWindowSettings();
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
}