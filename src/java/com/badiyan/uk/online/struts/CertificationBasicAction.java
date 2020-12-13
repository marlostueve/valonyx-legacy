package com.badiyan.uk.online.struts;

import com.badiyan.torque.*;
import com.badiyan.uk.beans.*;
import com.badiyan.uk.exceptions.*;

import com.badiyan.uk.online.beans.*;

import java.text.ParseException;
import java.util.Date;

import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.struts.action.*;

/**
 * Implementation of <strong>Action</strong> that validates a course.
 *
 * @author Marlo Stueve
 * @version $Revision: 1.2 $ $Date: 2009/07/17 16:23:52 $
 */
public final class
CertificationBasicAction
	extends Action
{
	public ActionForward
	execute(ActionMapping _mapping, ActionForm _form,
			HttpServletRequest _request, HttpServletResponse _response)
		throws Exception
	{
		System.out.println("execute() invoked in CertificationBasicAction");

		ActionErrors errors = new ActionErrors();

		CertificationBean certBean = null;
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
						if (!person.hasRole(RoleBean.SYSTEM_ADMINISTRATOR_ROLE_NAME))
							return (_mapping.findForward("session_expired"));
					}
					catch (IllegalValueException x)
					{
						return (_mapping.findForward("session_expired"));
					}
				}

				certBean = (CertificationBean)session.getAttribute("adminCert");
				if (certBean == null)
				{
					certBean = new CertificationBean();
					session.setAttribute("adminCert", certBean);
				}
			}
			else
				return (_mapping.findForward("session_expired"));

			Boolean createExternal = (Boolean)PropertyUtils.getSimpleProperty(_form, "createExternal");
			String titleInput = (String)PropertyUtils.getSimpleProperty(_form, "titleInput");
			String manufacturerSelect = (String)PropertyUtils.getSimpleProperty(_form, "manufacturerSelect");
			String categorySelect = (String)PropertyUtils.getSimpleProperty(_form, "categorySelect");
			String descriptionInput = (String)PropertyUtils.getSimpleProperty(_form, "descriptionInput");
			Integer durationHoursInput = (Integer)PropertyUtils.getSimpleProperty(_form, "durationHoursInput");
			Integer durationMinutesInput = (Integer)PropertyUtils.getSimpleProperty(_form, "durationMinutesInput");
			Integer creditsInput = (Integer)PropertyUtils.getSimpleProperty(_form, "creditsInput");
			Integer AICCSelect = (Integer)PropertyUtils.getSimpleProperty(_form, "AICCSelect");
			Integer rewardsSelect = (Integer)PropertyUtils.getSimpleProperty(_form, "rewardsSelect");
			String rewardDescInput = (String)PropertyUtils.getSimpleProperty(_form, "rewardDescInput");
			Integer surveySelect = (Integer)PropertyUtils.getSimpleProperty(_form, "surveySelect");
			String surveyURLInput = (String)PropertyUtils.getSimpleProperty(_form, "surveyURLInput");

			Integer statusSelect = (Integer)PropertyUtils.getSimpleProperty(_form, "statusSelect");
			Boolean scheduleActive = (Boolean)PropertyUtils.getSimpleProperty(_form, "scheduleActive");
			Boolean displayAsNew = (Boolean)PropertyUtils.getSimpleProperty(_form, "displayAsNew");
			Integer displayAsNewInput = (Integer)PropertyUtils.getSimpleProperty(_form, "displayAsNewInput");

			//Date createdDateInput = CUBean.getDateFromUserString((String)PropertyUtils.getSimpleProperty(_form, "createdDateInput"));


			if (manufacturerSelect.equals("0"))
				throw new IllegalValueException("Manufacturer is required.");
			if (categorySelect.equals("0"))
				throw new IllegalValueException("Category is required.");


			if (createExternal != null)
				certBean.setExternal(createExternal.booleanValue());
			else
				certBean.setExternal(false);
			certBean.setName(titleInput);
			certBean.setContact(loginBean.getPerson());
			certBean.setOwner(loginBean.getPerson());
			certBean.setDescription(descriptionInput);
			certBean.setDurationHours(durationHoursInput);
			certBean.setDurationMinutes(durationMinutesInput);
			certBean.setCreditsRequired(creditsInput.intValue());
			certBean.setAICCCompliant((AICCSelect.intValue() == 0) ? false : true);
			if (rewardsSelect.intValue() == 1)
				certBean.setComment(rewardDescInput);
			else
				certBean.setComment("");
			if (surveySelect.intValue() == 1)
				certBean.setSurveyURL(surveyURLInput);
			else
				certBean.setSurveyURL("");
			if (statusSelect.intValue() == 0)
				certBean.setStatus(CourseBean.IN_DEVELOPMENT_COURSE_STATUS);
			else if (statusSelect.intValue() == 1)
				certBean.setStatus(CourseBean.INACTIVE_COURSE_STATUS);
			else if (statusSelect.intValue() == 2)
				certBean.setStatus(CourseBean.DEFAULT_COURSE_STATUS);
			Date releasedDateInput = CUBean.getDateFromUserString((String)PropertyUtils.getSimpleProperty(_form, "releasedDateInput"));
			certBean.setReleasedDate(releasedDateInput);

			if (scheduleActive != null)
			{
				if (scheduleActive.booleanValue())
				{
					Date activeDateInput = CUBean.getDateFromUserString((String)PropertyUtils.getSimpleProperty(_form, "activeDateInput"));
					certBean.setBroadcastDate(activeDateInput);
				}
				else
					certBean.setBroadcastDate(releasedDateInput);
			}
			else
				certBean.setBroadcastDate(releasedDateInput);

			if (displayAsNew != null)
			{
				if (displayAsNew.booleanValue())
					certBean.setDisplayAsNewFor(displayAsNewInput.shortValue());
				else
					certBean.setDisplayAsNewFor((short)0);
			}
			else
				certBean.setDisplayAsNewFor((short)0);

			Date expiresDateInput = CUBean.getDateFromUserString((String)PropertyUtils.getSimpleProperty(_form, "expiresDateInput"));
			certBean.setOffDate(expiresDateInput);

			/*
			if (!categorySelect.equals("0"))
				certBean.setCategory(categorySelect);
			 */

			certBean.save();

			System.out.println("COURSE SAVED/CREATED >" + certBean.getNameString());
		}
        catch (IllegalValueException x)
        {
			errors.add(ActionErrors.GLOBAL_MESSAGE, new ActionMessage("exception", x.getMessage()));
		}
		/*
		catch (ParseException x)
		{
			errors.add(ActionErrors.GLOBAL_MESSAGE, new ActionMessage("exception", x.getMessage()));
		}
		 */
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