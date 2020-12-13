
package com.valonyx.tasks;

import com.valonyx.fitness.beans.Food;
import com.valonyx.fitness.servlets.FitnessServlet;

import java.util.*;


public class
USDAFoodListTask
    extends TimerTask
{
    // INSTANCE VARIABLES
	

    // CONSTRUCTORS

    public
    USDAFoodListTask()
    {
		System.out.println("USDAFoodListTask(CompanyBean) invoked >");
    }

    // INSTANCE METHODS

	@Override
    public void
    run()
    {
		System.out.println("run() invoked in USDAFoodListTask for >");
		
		try
		{
			
			long offset = Food.getStartOffset();
			offset++;
			boolean keep_doing_stuff = true;
			while (keep_doing_stuff) {
				keep_doing_stuff = FitnessServlet.procUSDAList(FitnessServlet.getJSON("https://api.nal.usda.gov/ndb/list?format=json&lt=f&sort=n&api_key=15IvtHBVZPtEWSgEdGisLrpYe79IdVU9uj7pM8oP&max=100&offset=" + offset, null, 5000, "GET"));
				offset += 100;
			}
			
		}
		catch (Exception x)
		{
			x.printStackTrace();
		}
    }
	
}