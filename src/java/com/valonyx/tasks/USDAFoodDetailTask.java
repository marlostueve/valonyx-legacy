
package com.valonyx.tasks;

import com.valonyx.fitness.beans.Food;
import com.valonyx.fitness.servlets.FitnessServlet;

import java.util.*;


public class
USDAFoodDetailTask
    extends TimerTask
{
    // INSTANCE VARIABLES
	

    // CONSTRUCTORS

    public
    USDAFoodDetailTask()
    {
		System.out.println("USDAFoodDetailTask(CompanyBean) invoked >");
    }

    // INSTANCE METHODS

	@Override
    public void
    run()
    {
		System.out.println("run() invoked in USDAFoodDetailTask for >");
		
		try
		{
			
			// grab a bunch of foods that need nutrition data...

			Iterator itr = Food.getFoodsThatNeedUSDASyncing(3000).iterator();
			while (itr.hasNext()) {
				Food fud = (Food)itr.next();
				System.out.println("found food to sync >" + fud.getLabel());
				FitnessServlet.procUSDAItem(FitnessServlet.getJSON("https://api.nal.usda.gov/ndb/V2/reports?ndbno=" + fud.getNDbNo() + "&type=f&format=json&api_key=15IvtHBVZPtEWSgEdGisLrpYe79IdVU9uj7pM8oP", null, 5000, "GET"));
			}
			
		}
		catch (Exception x)
		{
			x.printStackTrace();
		}
    }
	
}