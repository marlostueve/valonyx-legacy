/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.badiyan.uk.online.beans;

import com.badiyan.uk.beans.*;

import com.badiyan.torque.*;
import com.badiyan.uk.exceptions.*;
import com.valeo.qbpos.data.TenderRet;

import java.math.BigDecimal;
import java.util.*;

import org.apache.torque.*;
import org.apache.torque.util.Criteria;


/**
 *
 * @author marlo
 */
public class
UnitOfMeasure
	extends CUBean
	implements java.io.Serializable
{
    // CLASS VARIABLES

    protected static HashMap<Integer,UnitOfMeasure> hash = new HashMap<Integer,UnitOfMeasure>(11);

    // CLASS METHODS

    public static UnitOfMeasure
    getUnitOfMeasure(int _id)
		throws TorqueException, ObjectNotFoundException
    {
		Integer key = new Integer(_id);
		UnitOfMeasure unit_of_measure = (UnitOfMeasure)hash.get(key);
		if (unit_of_measure == null)
		{
			Criteria crit = new Criteria();
			crit.add(UnitOfMeasureDbPeer.UNIT_OF_MEASURE_DB_ID, _id);
			List objList = UnitOfMeasureDbPeer.doSelect(crit);
			if (objList.isEmpty())
				throw new ObjectNotFoundException("Could not locate Unit of Measure with id: " + _id);

			unit_of_measure = UnitOfMeasure.getUnitOfMeasure((UnitOfMeasureDb)objList.get(0));
		}

		return unit_of_measure;
    }

    private static UnitOfMeasure
    getUnitOfMeasure(UnitOfMeasureDb _unit_of_measure)
		throws TorqueException
    {
		Integer key = new Integer(_unit_of_measure.getUnitOfMeasureDbId());
		UnitOfMeasure unit_of_measure = (UnitOfMeasure)hash.get(key);
		if (unit_of_measure == null)
		{
			unit_of_measure = new UnitOfMeasure(_unit_of_measure);
			hash.put(key, unit_of_measure);
		}

		return unit_of_measure;
    }

    public static Vector
    getUnitsOfMeasure(CompanyBean _company)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		Vector vec = new Vector();
		Criteria crit = new Criteria();
		crit.add(UnitOfMeasureDbPeer.COMPANY_ID, _company.getId());
		Iterator itr = UnitOfMeasureDbPeer.doSelect(crit).iterator();
		while (itr.hasNext()) {
			UnitOfMeasureDb obj = (UnitOfMeasureDb)itr.next();
			vec.addElement(UnitOfMeasure.getUnitOfMeasure(obj));
		}
		return vec;
    }

    public static UnitOfMeasure
    maintainUnitOfMeasure(CompanyBean _company, String _unit_of_measure_str)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, Exception
    {
		Criteria crit = new Criteria();
		crit.add(UnitOfMeasureDbPeer.DESCRIPTION, _unit_of_measure_str);
		List objList = UnitOfMeasureDbPeer.doSelect(crit);
		if (objList.isEmpty()) {
			UnitOfMeasure obj = new UnitOfMeasure();
			obj.setCompany(_company);
			obj.setDescription(_unit_of_measure_str);
			obj.activate();
			obj.save();
			return obj;
		} else {
			return UnitOfMeasure.getUnitOfMeasure((UnitOfMeasureDb)objList.get(0));
		}
    }

    // SQL

    /*
<table name="UNIT_OF_MEASURE_DB" idMethod="native">
    <column name="UNIT_OF_MEASURE_DB_ID" primaryKey="true" required="true" type="INTEGER" autoIncrement="true"/>
    <column name="DESCRIPTION" required="true" type="VARCHAR" size="50"/>
	
    <column name="IS_ACTIVE" type="SMALLINT" default="1"/>
    <column name="COMPANY_ID" required="true" type="INTEGER"/>

    <foreign-key foreignTable="COMPANY">
		<reference local="COMPANY_ID" foreign="COMPANYID"/>
    </foreign-key>

</table>
     */

    // INSTANCE VARIABLES

    private UnitOfMeasureDb unit_of_measure;

    // CONSTRUCTORS

    public
    UnitOfMeasure()
    {
		unit_of_measure = new UnitOfMeasureDb();
		isNew = true;
    }

    public
    UnitOfMeasure(UnitOfMeasureDb _unit_of_measure)
    {
		unit_of_measure = _unit_of_measure;
		isNew = false;
    }

    // INSTANCE METHODS

	public CompanyBean
	getCompany()
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		return CompanyBean.getCompany(unit_of_measure.getCompanyId());
	}

	public void
	setCompany(CompanyBean _company)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		unit_of_measure.setCompanyId(_company.getId());
	}

	public void
	setDescription(String _desc)
	{
		unit_of_measure.setDescription(_desc);
	}

    public int
    getId()
    {
		return unit_of_measure.getUnitOfMeasureDbId();
    }

    public String
    getLabel()
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		
		return this.getCompany().getLabel() + " - " + (this.isActive() ? "Active" : "Inactive");
    }

    public String
    getValue()
    {
		return unit_of_measure.getUnitOfMeasureDbId() + "";
    }

    protected void
    insertObject()
		throws Exception
    {
		unit_of_measure.save();
    }
	
	public boolean
	isActive()
	{
		return (unit_of_measure.getIsActive() == (short)1);
	}

	public void
	activate()
		throws TorqueException
	{
		unit_of_measure.setIsActive((short)1);
	}

	public void
	inActivate()
		throws TorqueException
	{
		unit_of_measure.setIsActive((short)0);
	}

    protected void
    updateObject()
		throws ObjectAlreadyExistsException, IllegalValueException, Exception
    {
		unit_of_measure.save();
    }
}