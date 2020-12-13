/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.valonyx.tasks;

import com.valonyx.fitness.servlets.FitnessServlet;
import java.util.*;


public class
UpdateTrainingPlanFromGoogleSheetTask
    extends TimerTask
{
    // INSTANCE VARIABLES
	
	private String sheet_id;
	
    // CONSTRUCTORS

    public
    UpdateTrainingPlanFromGoogleSheetTask(String _sheet_id) {
		System.out.println("UpdateTrainingPlanFromGoogleSheetTask() invoked >");
		sheet_id = _sheet_id;
    }

    // INSTANCE METHODS

	@Override
    public void
    run() {
		System.out.println("run() invoked in UpdateTrainingPlanFromGoogleSheetTask for >" + sheet_id);
		
		try {
			FitnessServlet.updateTrainingPlanFromSpreadsheet(sheet_id);
		} catch (Exception x) {
			x.printStackTrace();
		}
    }
	
}