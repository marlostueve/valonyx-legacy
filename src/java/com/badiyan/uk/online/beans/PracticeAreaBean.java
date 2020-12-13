/*
 * PracticeAreaBean.java
 *
 * Created on November 10, 2007, 1:00 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.badiyan.uk.online.beans;

import com.badiyan.uk.beans.*;

import com.badiyan.torque.*;
import com.badiyan.uk.exceptions.*;

import java.beans.*;
import java.util.*;

import org.apache.torque.*;
import org.apache.torque.util.Criteria;

import com.badiyan.uk.tasks.*;

/**
 *
 * @author marlo
 */
public class
PracticeAreaBean
	extends CUBean
	implements java.io.Serializable
{
    // CLASS VARIABLES

    protected static HashMap<Integer,PracticeAreaBean> hash = new HashMap<Integer,PracticeAreaBean>(11);

    // CLASS METHODS
	
    public static void
    delete(int _id)
		throws TorqueException, ObjectNotFoundException, IllegalValueException
    {
		Criteria crit = new Criteria();
		crit.add(PracticeAreaPeer.PARENT_ID, _id);
		List obj_list = PracticeAreaPeer.doSelect(crit);
		if (obj_list.size() > 0)
		{
			PracticeAreaBean failed_delete_candidate = PracticeAreaBean.getPracticeArea(_id);
			String delete_error_str = "Unable to delete " + failed_delete_candidate.getLabel() + ".  It is a parent for ";
			String child_str = "";
			Iterator obj_list_itr = obj_list.iterator();
			while (obj_list_itr.hasNext())
			{
				PracticeArea child_practice_area = (PracticeArea)obj_list_itr.next();
				if (child_str.length() == 0)
					child_str = child_practice_area.getPracticeAreaName();
				else if (obj_list_itr.hasNext())
					child_str += ", " + child_practice_area.getPracticeAreaName();
				else
					child_str += ", and " + child_practice_area.getPracticeAreaName();
			}
			
			throw new IllegalValueException(delete_error_str + child_str);
		}

		crit = new Criteria();
		crit.add(PracticeAreaPractitionerPeer.PRACTICE_AREA_ID, _id);
		PracticeAreaPractitionerPeer.doDelete(crit);

		crit = new Criteria();
		crit.add(PracticeAreaPeer.PRACTICE_AREA_ID, _id);
		PracticeAreaPeer.doDelete(crit);

		Integer key = new Integer(_id);
		hash.remove(key);
    }

    public static PracticeAreaBean
    getPracticeArea(int _id)
		throws TorqueException, ObjectNotFoundException
    {
		Integer key = new Integer(_id);
		PracticeAreaBean practice_area = (PracticeAreaBean)hash.get(key);
		if (practice_area == null)
		{
			Criteria crit = new Criteria();
			crit.add(PracticeAreaPeer.PRACTICE_AREA_ID, _id);
			List objList = PracticeAreaPeer.doSelect(crit);
			if (objList.size() == 0)
				throw new ObjectNotFoundException("Could not locate Practice Area with id: " + _id);

			practice_area = PracticeAreaBean.getPracticeArea((PracticeArea)objList.get(0));
		}

		return practice_area;
    }

    public static PracticeAreaBean
    getPracticeArea(CompanyBean _company, int _id)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		Integer key = new Integer(_id);
		PracticeAreaBean practice_area = (PracticeAreaBean)hash.get(key);
		if (practice_area == null)
		{
			Criteria crit = new Criteria();
			crit.add(PracticeAreaPeer.PRACTICE_AREA_ID, _id);
			crit.add(PracticeAreaPeer.COMPANY_ID, _company.getId());
			List objList = PracticeAreaPeer.doSelect(crit);
			if (objList.size() == 0)
			throw new ObjectNotFoundException("Could not locate Practice Area with id: " + _id);

			practice_area = PracticeAreaBean.getPracticeArea((PracticeArea)objList.get(0));
		}
		else
		{
			// ensure that the companies match
			
			if (!practice_area.getCompany().equals(_company))
				throw new ObjectNotFoundException("Could not locate Practice Area with id: " + _id);
		}

		return practice_area;
    }

    private static PracticeAreaBean
    getPracticeArea(PracticeArea _practice_area)
		throws TorqueException
    {
		Integer key = new Integer(_practice_area.getPracticeAreaId());
		PracticeAreaBean practice_area = (PracticeAreaBean)hash.get(key);
		if (practice_area == null)
		{
			practice_area = new PracticeAreaBean(_practice_area);
			hash.put(key, practice_area);
		}

		return practice_area;
    }

	public static Vector
	getPracticeAreas(UKOnlinePersonBean _practitioner)
		throws TorqueException, ObjectNotFoundException
	{
		Vector vec = new Vector();

		Criteria crit = new Criteria();
		crit.add(PracticeAreaPractitionerPeer.PRACTITIONER_ID, _practitioner.getId());
		Iterator itr = PracticeAreaPractitionerPeer.doSelect(crit).iterator();
		while (itr.hasNext())
		{
			PracticeAreaPractitioner obj = (PracticeAreaPractitioner)itr.next();
			vec.addElement(PracticeAreaBean.getPracticeArea(obj.getPracticeAreaId()));
		}

		return vec;
	}
    
    public static Vector
    getPracticeAreas(CompanyBean _company)
		throws TorqueException
    {
		Vector vec = new Vector();

		Criteria crit = new Criteria();
		crit.add(PracticeAreaPeer.COMPANY_ID, _company.getId());
		crit.addAscendingOrderByColumn(PracticeAreaPeer.PRACTICE_AREA_NAME);
		Iterator itr = PracticeAreaPeer.doSelect(crit).iterator();
		while (itr.hasNext())
			vec.addElement(PracticeAreaBean.getPracticeArea((PracticeArea)itr.next()));

		return vec;
    }
	
	private static Vector practitioners_all;
	
	public static Vector
	getAllPractitioners()
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		if (practitioners_all == null)
		{
			practitioners_all = new Vector();
			
			Criteria crit = new Criteria();
			//crit.addJoin(PersonPeer.PERSONID, PracticeAreaPractitionerPeer.PRACTITIONER_ID);
			//crit.add(PracticeAreaPractitionerPeer.PRACTICE_AREA_ID, this.getId());
			crit.add(PersonPeer.OKSENDEMAIL, 2, Criteria.GREATER_THAN);
			crit.addAscendingOrderByColumn(PersonPeer.OKSENDEMAIL);
			//crit.addDescendingOrderByColumn(PersonPeer.LASTNAME);
			//System.out.println("crit >" + crit.toString());
			//Iterator itr = PracticeAreaPractitionerPeer.doSelect(crit).iterator();
			Iterator itr = PersonPeer.doSelect(crit).iterator();
			while (itr.hasNext())
			{
				//PracticeAreaPractitioner obj = (PracticeAreaPractitioner)itr.next();
				Person obj = (Person)itr.next();
				practitioners_all.addElement((UKOnlinePersonBean)UKOnlinePersonBean.getPerson(obj.getPersonid()));
			}
		}
		
		return practitioners_all;
	}
    
    // SQL
    
    /*
     *        <table name="PRACTICE_AREA" idMethod="native">
	    <column name="PRACTICE_AREA_ID" primaryKey="true" required="true" type="INTEGER" autoIncrement="true"/>
	    
	    <column name="COMPANY_ID" required="true" type="INTEGER"/>
	    
	    <column name="PRACTICE_AREA_NAME" size="100" type="VARCHAR"/>
	    <column name="PARENT_ID" required="false" type="INTEGER"/>

            <foreign-key foreignTable="COMPANY">
                <reference local="COMPANY_ID" foreign="COMPANYID"/>
            </foreign-key>
	    <foreign-key foreignTable="PRACTICE_AREA">
		<reference local="PARENT_ID" foreign="PRACTICE_AREA_ID"/>
	    </foreign-key>
	</table>
     */
	
    // INSTANCE VARIABLES
	
    private PracticeArea practice_area;
	private Vector practitioners;
    
    // CONSTRUCTORS
    
    public
    PracticeAreaBean()
    {
		practice_area = new PracticeArea();
		isNew = true;
    }
    
    public
    PracticeAreaBean(PracticeArea _practice_area)
    {
		practice_area = _practice_area;
		isNew = false;
    }
    
    // INSTANCE METHODS
	
	public boolean
	contains(UKOnlinePersonBean _practitioner)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		this.getPractitioners();
		return this.practitioners.contains(_practitioner);
	}
	
	public boolean
	equals(PracticeAreaBean _practice_area)
	{
		if (_practice_area == null)
			return false;
		return (this.getId() == _practice_area.getId());
	}
    
    public CompanyBean
    getCompany()
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		return CompanyBean.getCompany(practice_area.getCompanyId());
    }
    
    public int
    getId()
    {
		return practice_area.getPracticeAreaId();
    }
    
    public String
    getLabel()
    {
		String str = practice_area.getPracticeAreaName();
		if (str == null)
			return "";
		return str;
    }
    
    public int
    getParentId()
    {
		return practice_area.getParentId();
    }
	
	public Vector
	getPractitioners()
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		if (practitioners == null)
		{
			practitioners = new Vector();
			
			Criteria crit = new Criteria();
			crit.addJoin(PersonPeer.PERSONID, PracticeAreaPractitionerPeer.PRACTITIONER_ID);
			crit.add(PracticeAreaPractitionerPeer.PRACTICE_AREA_ID, this.getId());
			crit.addAscendingOrderByColumn(PersonPeer.OKSENDEMAIL);
			//crit.addDescendingOrderByColumn(PersonPeer.LASTNAME);
			//System.out.println("crit >" + crit.toString());
			Iterator itr = PracticeAreaPractitionerPeer.doSelect(crit).iterator();
			while (itr.hasNext())
			{
				PracticeAreaPractitioner obj = (PracticeAreaPractitioner)itr.next();
				practitioners.addElement((UKOnlinePersonBean)UKOnlinePersonBean.getPerson(obj.getPractitionerId()));
			}
		}
		
		return practitioners;
	}
	
	public Vector
	getPractitionersDesc()
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		if (practitioners == null)
		{
			practitioners = new Vector();
			
			Criteria crit = new Criteria();
			crit.addJoin(PersonPeer.PERSONID, PracticeAreaPractitionerPeer.PRACTITIONER_ID);
			crit.add(PracticeAreaPractitionerPeer.PRACTICE_AREA_ID, this.getId());
			crit.addDescendingOrderByColumn(PersonPeer.LASTNAME);
			//System.out.println("crit >" + crit.toString());
			Iterator itr = PracticeAreaPractitionerPeer.doSelect(crit).iterator();
			while (itr.hasNext())
			{
				PracticeAreaPractitioner obj = (PracticeAreaPractitioner)itr.next();
				practitioners.addElement((UKOnlinePersonBean)UKOnlinePersonBean.getPerson(obj.getPractitionerId()));
			}
		}
		
		return practitioners;
	}
    
    public String
    getValue()
    {
		return practice_area.getPracticeAreaId() + "";
    }
    
    protected void
    insertObject()
		throws Exception
    {
		practice_area.save();
		this.savePractitioners();
    }
	
	private void
    savePractitioners()
		throws TorqueException, Exception
    {
		/*
		 *<table name="PRACTICE_AREA_PRACTITIONER">
    <column name="PRACTICE_AREA_ID" required="true" primaryKey="true" type="INTEGER"/>
    <column name="PERSON_ID" required="true" primaryKey="true" type="INTEGER"/>
	
    <foreign-key foreignTable="PRACTICE_AREA">
		<reference local="PRACTICE_AREA_ID" foreign="PRACTICE_AREA_ID"/>
    </foreign-key>
    <foreign-key foreignTable="PERSON">
		<reference local="PERSON_ID" foreign="PERSONID"/>
    </foreign-key>
</table>
		 */

		if (this.practitioners != null)
		{
			HashMap db_practitioners_hash = new HashMap(3);
			Criteria crit = new Criteria();
			crit.add(PracticeAreaPractitionerPeer.PRACTICE_AREA_ID, this.getId());
			System.out.println("crit >" + crit.toString());
			Iterator itr = PracticeAreaPractitionerPeer.doSelect(crit).iterator();
			while (itr.hasNext())
			{
				System.out.println("found existing ass...");
				PracticeAreaPractitioner value = (PracticeAreaPractitioner)itr.next();
				Integer key = new Integer(value.getPractitionerId());
				db_practitioners_hash.put(key, value);
			}

			System.out.println("num practitioners >" + this.practitioners.size());
			itr = this.practitioners.iterator();
			while (itr.hasNext())
			{
				UKOnlinePersonBean practitioner = (UKOnlinePersonBean)itr.next();
				Integer key = new Integer(practitioner.getId());
				PracticeAreaPractitioner obj = (PracticeAreaPractitioner)db_practitioners_hash.remove(key);
				if (obj == null)
				{
					// association does not exist in db.  need to create

					System.out.println("creating new ass for " + practitioner.getLabel());

					obj = new PracticeAreaPractitioner();
					obj.setPracticeAreaId(this.getId());
					obj.setPractitionerId(practitioner.getId());
					obj.save();
				}
			}

			itr = db_practitioners_hash.keySet().iterator();
			while (itr.hasNext())
			{
				Integer key = (Integer)itr.next();
				PracticeAreaPractitioner obj = (PracticeAreaPractitioner)db_practitioners_hash.get(key);
				PracticeAreaPractitionerPeer.doDelete(obj);
			}
		}
    }
    
    public void
    setCompany(CompanyBean _company)
		throws TorqueException
    {
		practice_area.setCompanyId(_company.getId());
    }
    
    public void
    setName(String _name)
    {
		practice_area.setPracticeAreaName(_name);
    }
    
    public void
    setParentId(int _id)
		throws TorqueException
    {
		practice_area.setParentId(_id);
    }
	
	public void
	setPractitioners(Vector _practitioners)
	{
		practitioners = _practitioners;
	}
    
    protected void
    updateObject()
		throws ObjectAlreadyExistsException, IllegalValueException, Exception
    {
		practice_area.save();
		this.savePractitioners();
    }
}