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
InventoryDepartment
	extends CUBean
	implements java.io.Serializable
{
    // CLASS VARIABLES

    protected static HashMap<Integer,InventoryDepartment> hash = new HashMap<Integer,InventoryDepartment>(11);

    // CLASS METHODS

    public static InventoryDepartment
    getInventoryDepartment(int _id)
		throws TorqueException, ObjectNotFoundException
    {
		Integer key = new Integer(_id);
		InventoryDepartment inventory_department = (InventoryDepartment)hash.get(key);
		if (inventory_department == null)
		{
			Criteria crit = new Criteria();
			crit.add(InventoryDepartmentDbPeer.INVENTORY_DEPARTMENT_DB_ID, _id);
			List objList = InventoryDepartmentDbPeer.doSelect(crit);
			if (objList.isEmpty())
				throw new ObjectNotFoundException("Could not locate Inventory Department with id: " + _id);

			inventory_department = InventoryDepartment.getInventoryDepartment((InventoryDepartmentDb)objList.get(0));
		}

		return inventory_department;
    }

    private static InventoryDepartment
    getInventoryDepartment(InventoryDepartmentDb _inventory_department)
		throws TorqueException
    {
		Integer key = new Integer(_inventory_department.getInventoryDepartmentDbId());
		InventoryDepartment inventory_department = (InventoryDepartment)hash.get(key);
		if (inventory_department == null)
		{
			inventory_department = new InventoryDepartment(_inventory_department);
			hash.put(key, inventory_department);
		}

		return inventory_department;
    }

    public static InventoryDepartment
    getInventoryDepartment(CompanyBean _company, String _desc)
		throws TorqueException, ObjectNotFoundException
    {
		Criteria crit = new Criteria();
		crit.add(InventoryDepartmentDbPeer.COMPANY_ID, _company.getId());
		crit.add(InventoryDepartmentDbPeer.DEPARTMENT, _desc);
		List list = InventoryDepartmentDbPeer.doSelect(crit);
		if (list.size() == 1) {
			InventoryDepartmentDb obj = (InventoryDepartmentDb)list.get(0);
			return InventoryDepartment.getInventoryDepartment(obj);
		} else {
			throw new ObjectNotFoundException("Unable to locate department " + _desc);
		}
    }

    public static Vector
    getInventoryDepartments(CompanyBean _company)
		throws TorqueException
    {
		Vector vec = new Vector();
		
		Criteria crit = new Criteria();
		crit.add(InventoryDepartmentDbPeer.COMPANY_ID, _company.getId());
		Iterator itr = InventoryDepartmentDbPeer.doSelect(crit).iterator();
		while (itr.hasNext())
		{
			InventoryDepartmentDb obj = (InventoryDepartmentDb)itr.next();
			vec.addElement(InventoryDepartment.getInventoryDepartment(obj));
		}
		
		return vec;
    }

    // SQL

    /*
     *        <table name="INVENTORY_DEPARTMENT_DB" idMethod="native">
				<column name="INVENTORY_DEPARTMENT_DB_ID" primaryKey="true" required="true" type="INTEGER" autoIncrement="true"/>
				<column name="COMPANY_ID" required="true" type="INTEGER"/>
				<column name="DEPARTMENT" required="true" type="VARCHAR" size="30"/>
				<column name="IS_COMMISSIONABLE" required="false" type="SMALLINT"/>

				<foreign-key foreignTable="COMPANY">
					<reference local="COMPANY_ID" foreign="COMPANYID"/>
				</foreign-key>
			</table>
     */

    // INSTANCE VARIABLES

    private InventoryDepartmentDb inventory_department;

    // CONSTRUCTORS

    public
    InventoryDepartment()
    {
		inventory_department = new InventoryDepartmentDb();
		isNew = true;
    }

    public
    InventoryDepartment(InventoryDepartmentDb _inventory_department)
    {
		inventory_department = _inventory_department;
		isNew = false;
    }

    // INSTANCE METHODS

	public CompanyBean
	getCompany()
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		return CompanyBean.getCompany(inventory_department.getCompanyId());
	}

	public void
	setCompany(CompanyBean _company)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		inventory_department.setCompanyId(_company.getId());
	}

    public int
    getId()
    {
		return inventory_department.getInventoryDepartmentDbId();
    }

    public String
    getLabel()
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		String str = inventory_department.getDepartment();
		if (str == null)
			return "";
		return str;
    }

    public String
    getValue()
    {
		return inventory_department.getInventoryDepartmentDbId() + "";
    }

	public void
	setName(String _str)
		throws TorqueException
	{
		inventory_department.setDepartment(_str);
	}

    protected void
    insertObject()
		throws Exception
    {
		inventory_department.save();
    }

    protected void
    updateObject()
		throws ObjectAlreadyExistsException, IllegalValueException, Exception
    {
		inventory_department.save();
    }
}