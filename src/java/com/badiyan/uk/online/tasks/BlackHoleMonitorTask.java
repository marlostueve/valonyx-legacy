/*
 * BlackHoleMonitorTask.java
 *
 * Created on May 17, 2008, 3:25 PM
 * 
 */

package com.badiyan.uk.online.tasks;

import com.badiyan.torque.*;
import com.badiyan.uk.beans.*;
import com.badiyan.uk.exceptions.*;
import com.badiyan.uk.tasks.*;

import com.badiyan.uk.online.beans.*;
import com.badiyan.uk.online.servlets.*;

import java.net.*;
import java.io.*;
import java.util.*;

import org.apache.torque.*;
import org.apache.torque.util.Criteria;

public class
BlackHoleMonitorTask
    extends TimerTask
{
	// CLASS VARIABLES

	public static final int num_days_dump_into_black_hole_if_no_foq_specified = 60;
	public static final int num_missed_appt_black_hole = 2;

    // INSTANCE VARIABLES
	
	private CompanyBean company;

    // CONSTRUCTORS

    public
    BlackHoleMonitorTask(CompanyBean _company)
    {
		company = _company;
    }

    // INSTANCE METHODS

    public void
    run()
    {
		System.out.println("run() invoked in BlackHoleMonitorTask");
		
		try
		{
			int num_analyzed = 0;
			int num_no_appt = 0;

			BlackHoleBean black_hole = BlackHoleBean.getInstance(company);
			Calendar now = Calendar.getInstance();
			boolean black_hole_modified = false;
			
			Iterator itr = company.getPeople().iterator();
			while (itr.hasNext())
			{

				PersonBean person_obj = (PersonBean)itr.next();
				UKOnlinePersonBean person = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(person_obj.getId());
				if ((person.getStatus() == UKOnlinePersonBean.ACTIVE_PERSON_STATUS) || (person.getStatus() == UKOnlinePersonBean.BLACK_HOLE_PERSON_STATUS))
				{
					num_analyzed++;
					black_hole_modified = person.updateBlackHoleStatus(black_hole);
				}
				
			}

			if (black_hole_modified)
				black_hole.save();

			//System.out.println("num_analyzed >" + num_analyzed);
			//System.out.println("num_no_appt >" + num_no_appt);
			
		}
		catch (Exception x)
		{
			x.printStackTrace();
		}
    }
}
