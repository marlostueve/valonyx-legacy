/*
 * PracticeAreasAction.java
 *
 * Created on November 10, 2007, 2:28 PM
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
 * Implementation of <strong>Action</strong> that validates a course audience.
 *
 * @author Marlo Stueve
 */
public final class
PracticeAreasAction
	extends Action
{
	public ActionForward
	execute(ActionMapping _mapping, ActionForm _form,
			HttpServletRequest _request, HttpServletResponse _response)
		throws Exception
	{
		System.out.println("execute() invoked in PracticeAreasAction");

		ActionErrors errors = new ActionErrors();

		UKOnlineLoginBean loginBean = null;
		
		CompanyBean admin_company = null;
		PracticeAreaBean practice_area = null;
		
		HttpSession session = _request.getSession(false);

		try
		{
			// Check the session to see if there's a course in progress...

			if (session != null)
			{
				loginBean = (UKOnlineLoginBean)session.getAttribute("loginBean");
				if (loginBean == null)
				{
					loginBean = new UKOnlineLoginBean();
					session.setAttribute("loginBean", loginBean);
				}
				
				admin_company = (CompanyBean)session.getAttribute("adminCompany");
				practice_area = (PracticeAreaBean)session.getAttribute("adminPracticeArea");
			}
			else
				return (_mapping.findForward("session_expired"));
			
			/*
			 *<form-bean       name="practiceAreasForm"
                     type="org.apache.struts.validator.DynaValidatorForm">
      <form-property name="nameInput" type="java.lang.String"/>
      <form-property name="parent" type="java.lang.Integer"/>
    </form-bean>
			 */

			if (_request.getParameter("delete_id").equals("0"))
			{
			    String nameInput = (String)PropertyUtils.getSimpleProperty(_form, "nameInput");
			    Integer parent = (Integer)PropertyUtils.getSimpleProperty(_form, "parent");
			    Integer[] practitionerSelect = (Integer[])PropertyUtils.getSimpleProperty(_form, "practitionerSelect");

			    System.out.println("nameInput >" + nameInput);
			    System.out.println("parent >" + parent);
			    System.out.println("practitionerSelect >" + practitionerSelect);
			    
			    practice_area.setName(nameInput);
			    practice_area.setCompany(admin_company);
			    if (parent.intValue() > 0)
					practice_area.setParentId(parent.intValue());
				
				Vector practitioners = new Vector();
				for (int i = 0; i < practitionerSelect.length; i++)
				{
					UKOnlinePersonBean practitioner = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(practitionerSelect[i].intValue());
					practitioners.addElement(practitioner);
				}
				practice_area.setPractitioners(practitioners);
				
			    practice_area.save();
			    
			    

			    session.removeAttribute("adminPracticeArea");
			}
			else
			{
			    PracticeAreaBean.delete(Integer.parseInt(_request.getParameter("delete_id")));
			}

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
