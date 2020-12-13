package com.badiyan.uk.online.tasks;

import com.badiyan.torque.*;
import com.badiyan.uk.beans.*;

import java.util.*;

import org.apache.torque.*;
import org.apache.torque.util.Criteria;

public class
CourseExpirationTask
	extends TimerTask
{
	// INSTANCE VARIABLES

	private String message;
	private Vector adminEmailStrings;

	// CONSTRUCTORS

	public
	CourseExpirationTask(String _message)
		throws TorqueException
	{
		message = _message;
		adminEmailStrings = new Vector();
		Criteria crit = new Criteria();
		crit.addJoin(RolePeer.ROLEID, PersonrolePeer.ROLEID);
		crit.addJoin(PersonrolePeer.PERSONID, PersonPeer.PERSONID);
		crit.add(RolePeer.NAME, RoleBean.SYSTEM_ADMINISTRATOR_ROLE_NAME);
		List objList = PersonPeer.doSelect(crit);
		Iterator itr = objList.iterator();
		while (itr.hasNext())
		{
			Person personObj = (Person)itr.next();
			adminEmailStrings.addElement(personObj.getEmail1());
			System.out.println("ADMIN EMAIL FOUND >" + personObj.getEmail1());
		}
	}

	// INSTANCE METHODS

	public void
	run()
	{
		try
		{
			//System.out.println(message);

			// look for any courses that need to be activated

			Criteria crit = new Criteria();
			crit.add(CoursePeer.COURSESTATUS, CourseBean.IN_DEVELOPMENT_COURSE_STATUS);
			crit.or(CoursePeer.COURSESTATUS, CourseBean.DEFAULT_COURSE_STATUS);
			crit.add(CoursePeer.OFFDATE, new Date(), Criteria.LESS_EQUAL);
			List objList = CoursePeer.doSelect(crit);
			System.out.println("objList.size() >" + objList.size());
			Iterator itr = objList.iterator();
			while (itr.hasNext())
			{
				Course courseObj = (Course)itr.next();
				CourseBean course = CourseBean.getCourse(courseObj);
				System.out.println("course name >" + course.getName());
				course.setStatus(CourseBean.INACTIVE_COURSE_STATUS);
				course.save();

				Iterator emailItr = adminEmailStrings.iterator();
				while (emailItr.hasNext())
				{
					//System.out.println("SENDING EMAIL TO >" + (String)emailItr.next());
					CUBean.sendEmail((String)emailItr.next(), "pls@patterson.com", CUBean.getProperty("cu.mail.subject"), course.getName() + " has expired.  Take action as appropriate.");
				}
			}

		}
		catch (Exception x)
		{
			x.printStackTrace();
		}
	}
}
