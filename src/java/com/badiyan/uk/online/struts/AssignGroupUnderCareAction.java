/*
 * AssignGroupUnderCareAction.java
 *
 */

package com.badiyan.uk.online.struts;

import com.badiyan.torque.*;
import com.badiyan.uk.beans.*;
import com.badiyan.uk.exceptions.*;

import com.badiyan.uk.online.beans.*;

import java.text.*;
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
AssignGroupUnderCareAction
	extends Action
{
	public ActionForward
	execute(ActionMapping _mapping, ActionForm _form,
			HttpServletRequest _request, HttpServletResponse _response)
		throws Exception
	{
		System.out.println("execute() invoked in AssignGroupUnderCareAction");

		ActionErrors errors = new ActionErrors();
		String forwardString = "success";

		UKOnlineLoginBean loginBean = null;
		
		CompanyBean admin_company = null;
		
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
			}
			else
				return (_mapping.findForward("session_expired"));
			
			/*
			 * <form-bean       name="assignGroupUnderCareForm"
                     type="org.apache.struts.validator.DynaValidatorForm">
      <form-property name="clientSelect" type="java.lang.Integer"/>
      <form-property name="groupSelect" type="java.lang.Integer"/>
      <form-property name="relationshipSelect" type="java.lang.Integer"/>
    </form-bean>
			 */
			
			Integer clientSelect = (Integer)PropertyUtils.getSimpleProperty(_form, "clientSelect");
			Integer groupSelect = (Integer)PropertyUtils.getSimpleProperty(_form, "groupSelect");
			Integer relationshipSelect = (Integer)PropertyUtils.getSimpleProperty(_form, "relationshipSelect");
			
			System.out.println("clientSelect >" + clientSelect);
			System.out.println("groupSelect >" + groupSelect);
			System.out.println("relationshipSelect >" + relationshipSelect);
			
			Enumeration enumx = _request.getParameterNames();
			while (enumx.hasMoreElements())
			{
				String param_name = (String)enumx.nextElement();
				System.out.println(param_name + " >" + _request.getParameter(param_name));
			}
			
			if (clientSelect.intValue() == -1)
				throw new IllegalValueException("You must select a client to assign to a new group under care.");
			
			
			UKOnlinePersonBean person_to_move = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(clientSelect.intValue());
			GroupUnderCareBean current_group_under_care = null;
			try
			{
				current_group_under_care = person_to_move.getGroupUnderCare();
			}
			catch (ObjectNotFoundException x)
			{
			}
			
			UKOnlinePersonBean group_person = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(groupSelect.intValue());
			GroupUnderCareBean new_group_under_care = group_person.getGroupUnderCare();


			//GroupUnderCareBean group_under_care = GroupUnderCareBean.getGroupUnderCare(group_id.intValue());
			
			if ((current_group_under_care == null) || (current_group_under_care.getId() != new_group_under_care.getId()))
			{
				GroupUnderCareMember member = null;
				
				if (current_group_under_care != null)
				{
					Vector current_members = current_group_under_care.getMembers();
					for (int i = 0; i < current_members.size(); i++)
					{
						GroupUnderCareMember member_obj = (GroupUnderCareMember)current_members.elementAt(i);
						if (member_obj.getPersonId() == person_to_move.getId())
						{
							member = member_obj;
							current_members.removeElementAt(i);
							current_group_under_care.setGroupUnderCareMembers(current_members);
							current_group_under_care.setModifyPerson((UKOnlinePersonBean)loginBean.getPerson());
							current_group_under_care.save();
							current_group_under_care.invalidate();
							break;
						}
					}

					if (member == null)
						throw new IllegalValueException("Existing group membership not found...");
				}
				
				
				member = new GroupUnderCareMember();
				member.setPersonId(person_to_move.getId());
				//member.setGroupUnderCareMemberTypeId(clientFileTypeSelect.intValue());
				member.setRelationshipToPrimaryClient(relationshipSelect.shortValue());
				
				new_group_under_care.setModifyPerson((UKOnlinePersonBean)loginBean.getPerson());
				Vector members = new_group_under_care.getMembers();
				members.addElement(member);
				new_group_under_care.setGroupUnderCareMembers(members);
				
				new_group_under_care.save();
				new_group_under_care.invalidate();
				
				person_to_move.invalidateGroup();
				
			}
			else if (current_group_under_care != null)
			{
				//System.out.println("changing relationship stuff");

				if (current_group_under_care.getId() == new_group_under_care.getId())
				{
					// am I changing the relationship?

					//System.out.println("am I changing the relationship?");

					GroupUnderCareMember member = null;

					Vector current_members = current_group_under_care.getMembers();
					for (int i = 0; i < current_members.size(); i++)
					{
						GroupUnderCareMember member_obj = (GroupUnderCareMember)current_members.elementAt(i);
						if (member_obj.getPersonId() == person_to_move.getId())
						{
							member = member_obj;

							//System.out.println("member_obj.getRelationshipToPrimaryClient() >" + member_obj.getRelationshipToPrimaryClient());
							//System.out.println("relationshipSelect.shortValue() >" + relationshipSelect.shortValue());

							if (member_obj.getRelationshipToPrimaryClient() != relationshipSelect.shortValue())
							{
								member_obj.setRelationshipToPrimaryClient(relationshipSelect.shortValue());
								member_obj.save();

								current_group_under_care.setModifyPerson((UKOnlinePersonBean)loginBean.getPerson());
								current_group_under_care.save();
								current_group_under_care.invalidate();
							}
							break;
						}
					}

					if (member == null)
						throw new IllegalValueException("Existing group membership not found...");
				}
			}

		}
		catch (Exception x)
		{
			x.printStackTrace();
			//errors.add(ActionErrors.GLOBAL_MESSAGE, new ActionMessage("exception", x.getMessage()));
			session.setAttribute("error-message", x.getMessage());
			forwardString = "failure";
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
		return (_mapping.findForward(forwardString));
	}
}
