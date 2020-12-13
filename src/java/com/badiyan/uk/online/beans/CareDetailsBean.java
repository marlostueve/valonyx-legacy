/*
 * CareDetailsBean.java
 *
 * Created on May 17, 2008, 10:58 AM
 *
 */

package com.badiyan.uk.online.beans;

import com.badiyan.uk.beans.*;

import com.badiyan.torque.*;
import com.badiyan.uk.exceptions.*;

import java.beans.*;
import java.text.*;
import java.util.*;

import org.apache.torque.*;
import org.apache.torque.util.Criteria;

import com.badiyan.uk.tasks.*;

/**
 *
 * @author marlo
 */
public class
CareDetailsBean
	extends CUBean
	implements java.io.Serializable
{
    // CLASS VARIABLES
    
    protected static HashMap<Integer,CareDetailsBean> hash = new HashMap<Integer,CareDetailsBean>(11);

    // CLASS METHODS
	
    public static void
    delete(int _id)
		throws TorqueException
    {
		Criteria crit = new Criteria();
		crit.add(CareDetailsPeer.CARE_DETAILS_ID, _id);
		CareDetailsPeer.doDelete(crit);

		Integer key = new Integer(_id);
		hash.remove(key);
    }

    public static CareDetailsBean
    getCareDetails(int _id)
		throws TorqueException, ObjectNotFoundException
    {
		Integer key = new Integer(_id);
		CareDetailsBean obj = (CareDetailsBean)hash.get(key);
		if (obj == null)
		{
			Criteria crit = new Criteria();
			crit.add(CareDetailsPeer.CARE_DETAILS_ID, _id);
			List objList = CareDetailsPeer.doSelect(crit);
			if (objList.size() == 0)
				throw new ObjectNotFoundException("Could not locate care details item with id: " + _id);

			obj = CareDetailsBean.getCareDetails((CareDetails)objList.get(0));
		}

		return obj;
    }

    private static CareDetailsBean
    getCareDetails(CareDetails _obj)
		throws TorqueException
    {
		Integer key = new Integer(_obj.getCareDetailsId());
		CareDetailsBean obj = (CareDetailsBean)hash.get(key);
		if (obj == null)
		{
			obj = new CareDetailsBean(_obj);
			hash.put(key, obj);
		}

		return obj;
    }

    public static CareDetailsBean
    getCareDetails(UKOnlinePersonBean _person, PracticeAreaBean _practice_area)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		Criteria crit = new Criteria();
		crit.add(CareDetailsPeer.PERSON_ID, _person.getId());
		crit.add(CareDetailsPeer.PRACTICE_AREA_ID, _practice_area.getId());
		List objList = CareDetailsPeer.doSelect(crit);
		if (objList.size() == 0)
			throw new ObjectNotFoundException("Could not locate care details item.");
		else if (objList.size() > 1)
			throw new UniqueObjectNotFoundException("Could not locate unique care details item.");
		
		return CareDetailsBean.getCareDetails((CareDetails)objList.get(0));
    }

    public static Vector
    getCareDetails(UKOnlinePersonBean _person)
		throws TorqueException, ObjectNotFoundException
    {
		Vector vec = new Vector();
		
		Criteria crit = new Criteria();
		crit.add(CareDetailsPeer.PERSON_ID, _person.getId());
		crit.addAscendingOrderByColumn(CareDetailsPeer.PRACTICE_AREA_ID);
		Iterator itr = CareDetailsPeer.doSelect(crit).iterator();
		while (itr.hasNext())
			vec.addElement(CareDetailsBean.getCareDetails((CareDetails)itr.next()));
	
		return vec;
    }
    
    // SQL
    
    /*
     * <table name="CARE_DETAILS" idMethod="native">
    <column name="CARE_DETAILS_ID" primaryKey="true" required="true" type="INTEGER" autoIncrement="true"/>
    <column name="COMPANY_ID" required="true" type="INTEGER"/>
	
    <column name="PERSON_ID" required="true" type="INTEGER"/>
    <column name="PREFERRED_PRACTITIONER_ID" required="false" type="INTEGER" />
    <column name="PRACTICE_AREA_ID" required="false" type="INTEGER" />
	
    <column name="FREQUENCY_OF_CARE_VALUE" required="false" type="INTEGER" />
	<column name="FREQUENCY_OF_CARE_PERIOD" required="false" type="INTEGER" />
	<column name="FREQUENCY_OF_CARE_DAYS" required="false" type="INTEGER" />
    
    <column name="CREATION_DATE" required="true" type="DATE"/>
    <column name="MODIFICATION_DATE" required="false" type="DATE"/>
    <column name="CREATE_PERSON_ID" required="false" type="INTEGER"/>
    <column name="MODIFY_PERSON_ID" required="false" type="INTEGER"/>
    
    <foreign-key foreignTable="COMPANY">
		<reference local="COMPANY_ID" foreign="COMPANYID"/>
    </foreign-key>
    <foreign-key foreignTable="PERSON">
		<reference local="PERSON_ID" foreign="PERSONID"/>
    </foreign-key>
    <foreign-key foreignTable="PERSON">
		<reference local="PREFERRED_PRACTITIONER_ID" foreign="PERSONID"/>
    </foreign-key>
    <foreign-key foreignTable="PRACTICE_AREA">
		<reference local="PRACTICE_AREA_ID" foreign="PRACTICE_AREA_ID"/>
    </foreign-key>
    <foreign-key foreignTable="PERSON">
		<reference local="CREATE_PERSON_ID" foreign="PERSONID"/>
    </foreign-key>
    <foreign-key foreignTable="PERSON">
		<reference local="MODIFY_PERSON_ID" foreign="PERSONID"/>
    </foreign-key>
</table>
     */
	
    // INSTANCE VARIABLES
	
    private CareDetails details;
    
    // CONSTRUCTORS
    
    public
    CareDetailsBean()
    {
		details = new CareDetails();
		isNew = true;
    }
    
    public
    CareDetailsBean(CareDetails _obj)
    {
		details = _obj;
		isNew = false;
    }
    
    // INSTANCE METHODS
    
    public int
    getId()
    {
		return details.getCareDetailsId();
    }
	
	public int
	getFrequencyOfCareDays()
	{
		return details.getFrequencyOfCareDays();
	}
    
    public String
    getLabel()
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		String period_str = null;
		
		int period = details.getFrequencyOfCarePeriod();
		int recurrence = details.getFrequencyOfCarePeriodRecurrence();
		switch (period)
		{
			case 1: period_str = "Day"; break;
			case 2: period_str = "Week"; break;
			case 3: period_str = "Month"; break;
			case 4: period_str = "Year"; break;
			
		}

		if (this.isPRN())
			return this.getPracticeArea().getLabel() + " - PRN (" + this.getPreferredPractitioner().getLabel() + ")";
		else
		{
			if (recurrence > 1)
				return this.getPracticeArea().getLabel() + " - " + details.getFrequencyOfCareValue() + " time" + ((details.getFrequencyOfCareValue() > 1) ? "s" : "") + " every " + recurrence + " " + period_str + "s (" + this.getPreferredPractitioner().getLabel() + ")";
			else
				return this.getPracticeArea().getLabel() + " - " + details.getFrequencyOfCareValue() + " time" + ((details.getFrequencyOfCareValue() > 1) ? "s" : "") + " per " + period_str + " (" + this.getPreferredPractitioner().getLabel() + ")";
		}
    }
	
	public UKOnlinePersonBean
	getPerson()
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		return (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(details.getPersonId());
	}
	
	public PracticeAreaBean
	getPracticeArea()
		throws TorqueException, ObjectNotFoundException
	{
		return PracticeAreaBean.getPracticeArea(details.getPracticeAreaId());
	}
    
    public UKOnlinePersonBean
    getPreferredPractitioner()
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		return (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(details.getPreferredPractitionerId());
    }
    
    public String
    getValue()
    {
		return this.getId() + "";
    }
    
    protected void
    insertObject()
		throws Exception
    {
		details.setCreationDate(new Date());
		details.save();
    }

	public boolean
	isPRN()
	{
		return (details.getIsPrn() == (short)1);
	}
	
	public void
	setCompany(CompanyBean _company)
		throws TorqueException
	{
		details.setCompanyId(_company.getId());
	}
    
    public void
    setCreateOrModifyPerson(UKOnlinePersonBean _person)
		throws TorqueException
    {
		if (this.isNew())
			details.setCreatePersonId(_person.getId());
		else
			details.setModifyPersonId(_person.getId());
    }
	
	public void
	setFrequencyOfCarePeriod(int _period)
	{
		System.out.println("setFrequencyOfCarePeriod() invoked >" + _period);
		details.setFrequencyOfCarePeriod(_period);
		calculateDaysBetweenCare();
	}

	public void
	setFrequencyOfCarePeriodRecurrence(int _value)
	{
		System.out.println("setFrequencyOfCarePeriodRecurrence() invoked >" + _value);
		details.setFrequencyOfCarePeriodRecurrence(_value);
		calculateDaysBetweenCare();
	}
	
	public void
	setFrequencyOfCareValue(int _value)
	{
		System.out.println("setFrequencyOfCareValue() invoked >" + _value);
		details.setFrequencyOfCareValue(_value);
		calculateDaysBetweenCare();
	}

	public void
	setPRN(boolean _is_prn)
	{
		details.setIsPrn(_is_prn ? (short)1 : (short)0);
	}
	
	private void
	calculateDaysBetweenCare()
	{
		if ((details.getFrequencyOfCareValue() > 0) && (details.getFrequencyOfCarePeriod() > 0) && (details.getFrequencyOfCarePeriodRecurrence() > 0))
		{
			Calendar now = Calendar.getInstance();
			Calendar later = Calendar.getInstance();

			switch (details.getFrequencyOfCarePeriod())
			{
				case 1: later.add(Calendar.DATE, details.getFrequencyOfCarePeriodRecurrence()); break;
				case 2: later.add(Calendar.WEEK_OF_YEAR, details.getFrequencyOfCarePeriodRecurrence()); break;
				case 3: later.add(Calendar.MONTH, details.getFrequencyOfCarePeriodRecurrence()); break;
				case 4: later.add(Calendar.YEAR, details.getFrequencyOfCarePeriodRecurrence()); break;
			}

			Long days = new Long((later.getTime().getTime() - now.getTime().getTime()) / (86400000 * details.getFrequencyOfCareValue()));

			if (days.intValue() == 0)
				details.setFrequencyOfCareDays(1);
			else
				details.setFrequencyOfCareDays(days.intValue());
		}
	}
    
    public void
    setPerson(UKOnlinePersonBean _person)
		throws TorqueException
    {
		details.setPersonId(_person.getId());
    }
	
	public void
	setPracticeArea(PracticeAreaBean _practice_area)
		throws TorqueException
	{
		details.setPracticeAreaId(_practice_area.getId());
	}
    
    public void
    setPreferredPractioner(UKOnlinePersonBean _person)
		throws TorqueException
    {
		details.setPreferredPractitionerId(_person.getId());
    }
    
    protected void
    updateObject()
		throws ObjectAlreadyExistsException, IllegalValueException, Exception
    {
		details.setModificationDate(new Date());
		details.save();
    }
}