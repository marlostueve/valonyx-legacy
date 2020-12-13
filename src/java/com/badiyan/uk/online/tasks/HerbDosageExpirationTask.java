/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.badiyan.uk.online.tasks;

import com.badiyan.uk.online.beans.*;

import com.badiyan.torque.*;
import com.badiyan.uk.beans.*;

import java.util.*;

import org.apache.torque.*;
import org.apache.torque.util.Criteria;

public class
HerbDosageExpirationTask
	extends TimerTask
{
	// INSTANCE VARIABLES

	private UKOnlineCompanyBean company;

	// CONSTRUCTORS

	public
	HerbDosageExpirationTask(UKOnlineCompanyBean _company)
		throws TorqueException
	{
		company = _company;
	}

	// INSTANCE METHODS

	public void
	run()
	{
		System.out.println("RUN INVOKED IN HerbDosageExpirationTask");
		
		try
		{
			Iterator itr = HerbDosage.getHerbDosages(company, true).iterator();
			while (itr.hasNext()) {
				HerbDosage dosage = (HerbDosage)itr.next();
				short days_remaining = dosage.getDaysRemaining();
				System.out.println("DOSAGE >" + dosage.getDescription() + " days rem >" + days_remaining + "  email >" + dosage.isExpirationEmailSent());
				if (days_remaining < 1) {
					dosage.inActivate();
					
					if (!dosage.isExpirationEmailSent()) {
						UKOnlinePersonBean client = dosage.getClient();

						System.out.println("SENDING HERB EMAIL FOR >" + client.getLabel());
						CUBean.sendEmail("cstueve@sanowc.com", "marlo@valonyx.com", "Hey Babe, guess who has finished their herbs!", client.getLabel() + " has finished their " + dosage.getDescription() + " herbs.  Take action as appropriate.");
						
						dosage.setExpirationEmailSent(true);
					}
					
					dosage.save();
				}
			}


		}
		catch (Exception x)
		{
			x.printStackTrace();
		}
	}
}
