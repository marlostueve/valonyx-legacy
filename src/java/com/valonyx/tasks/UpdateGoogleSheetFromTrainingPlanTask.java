/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.valonyx.tasks;

import com.badiyan.uk.beans.PersonBean;
import com.valonyx.fitness.beans.TrainingPlan;
import com.valonyx.fitness.servlets.FitnessServlet;
import java.util.*;


public class
UpdateGoogleSheetFromTrainingPlanTask
    extends TimerTask
{
    // INSTANCE VARIABLES
	
	private PersonBean logged_in_person;
	private TrainingPlan plan;
	private String sheet_id;
	
    // CONSTRUCTORS

    public
    UpdateGoogleSheetFromTrainingPlanTask(PersonBean _logged_in_person, TrainingPlan _plan, String _sheet_id) {
		System.out.println("UpdateGoogleSheetFromTrainingPlanTask() invoked >");
		logged_in_person = _logged_in_person;
		plan = _plan;
		sheet_id = _sheet_id;
    }

    // INSTANCE METHODS

	@Override
    public void
    run() {
		System.out.println("run() invoked in UpdateGoogleSheetFromTrainingPlanTask for >" + sheet_id);
		
		try {
			FitnessServlet.updateSheetFromTrainingPlan(logged_in_person, plan, sheet_id);
		} catch (Exception x) {
			x.printStackTrace();
		}
    }
	
}