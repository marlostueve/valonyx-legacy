package com.badiyan.uk.online.tasks;

import com.badiyan.torque.*;
import com.badiyan.uk.beans.*;

import java.util.*;

import org.apache.torque.util.Criteria;

public class
MakeCourseActiveTask
	extends TimerTask
{
	// INSTANCE VARIABLES

	private String message;

	// CONSTRUCTORS

	public
	MakeCourseActiveTask(String _message)
	{
		message = _message;
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
			crit.add(CoursePeer.BROADCASTDATE, new Date(), Criteria.LESS_EQUAL);
			List objList = CoursePeer.doSelect(crit);
			Iterator itr = objList.iterator();
			while (itr.hasNext())
			{
				Course courseObj = (Course)itr.next();
				CourseBean course = CourseBean.getCourse(courseObj);
				course.setStatus(CourseBean.DEFAULT_COURSE_STATUS);
				course.save();
			}

			/*
			Vector users = IntranetServlet.getUsersWithUnsubmittedTimeSheets();
			//System.out.println("NUM USERS >" + users.size());
			for (int i = 0; i < users.size(); i++)
			{
				User user = (User)users.elementAt(i);
				//System.out.println("Bad User >" + user.getFullName());
				IntranetServlet.sendEmail(user.getEmailString(), "intranet@badiyan.com", "Please Submit Your Time Sheet", message);
			}
			 */
		}
		catch (Exception x)
		{
			x.printStackTrace();
		}
	}
}
