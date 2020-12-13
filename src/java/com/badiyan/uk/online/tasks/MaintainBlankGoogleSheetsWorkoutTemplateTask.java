/*
 * MaintainBlankGoogleSheetsWorkoutTemplateTask.java
 *
 * Created on December 16, 2018, 5:37 PM
 * 
 */
package com.badiyan.uk.online.tasks;

import com.badiyan.torque.*;
import com.badiyan.uk.beans.*;
import com.badiyan.uk.exceptions.*;
import com.badiyan.uk.tasks.*;

import com.badiyan.uk.online.beans.*;
import com.badiyan.uk.online.servlets.*;
import com.valonyx.fitness.servlets.FitnessServlet;

import java.net.*;
import java.io.*;
import java.util.*;

import org.apache.torque.*;
import org.apache.torque.util.Criteria;

public class
MaintainBlankGoogleSheetsWorkoutTemplateTask
    extends TimerTask
{
	// CLASS VARIABLES

	public static final int num_sheets_to_maintain = 5;

    // INSTANCE VARIABLES
	
	

    // CONSTRUCTORS

    public
    MaintainBlankGoogleSheetsWorkoutTemplateTask() {
    }

    // INSTANCE METHODS

    public void
    run() {
		
		System.out.println("run() invoked in MaintainBlankGoogleSheetsWorkoutTemplateTask");
		
		try {
			
			//UNUSED_GOOGLE_SHEET_DB
					
			int num_unused_sheets = UnusedGoogleSheetDbPeer.doSelect(new Criteria()).size();
			if (num_unused_sheets < num_sheets_to_maintain) {
				// create a sheet
				
				System.out.println("create sheet >" + num_unused_sheets);
				
				// https://script.google.com/macros/s/AKfycbxPmwAnQIEIxGwtWibHoQX5rM7HbCIT0LdlUS7_DNiohowgKVo/exec
				// https://script.google.com/macros/s/AKfycbz9gAgFlRMKTsm3UOwHdwogbhjtd6Z9XkGYUBYRWchLGwIHo7w/exec
				
				FitnessServlet.createBlankGoogleSheet();
				
			}
			
		}
		catch (Exception x)
		{
			x.printStackTrace();
		}
    }
}

