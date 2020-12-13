/*
 * ToDoItemBean.java
 *
 * Created on May 16, 2008, 9:53 AM
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
ToDoItemBean
	extends CUBean
	implements java.io.Serializable
{
    // CLASS VARIABLES
    
    protected static HashMap<Integer,ToDoItemBean> hash = new HashMap<Integer,ToDoItemBean>(11);

    // CLASS METHODS
	
    public static void
    delete(int _id)
		throws TorqueException
    {
		Criteria crit = new Criteria();
		crit.add(ToDoItemPeer.TO_DO_ITEM_ID, _id);
		ToDoItemPeer.doDelete(crit);

		Integer key = new Integer(_id);
		hash.remove(key);
    }

    public static ToDoItemBean
    getToDoItem(int _id)
		throws TorqueException, ObjectNotFoundException
    {
		Integer key = new Integer(_id);
		ToDoItemBean obj = (ToDoItemBean)hash.get(key);
		if (obj == null)
		{
			Criteria crit = new Criteria();
			crit.add(ToDoItemPeer.TO_DO_ITEM_ID, _id);
			List objList = ToDoItemPeer.doSelect(crit);
			if (objList.size() == 0)
				throw new ObjectNotFoundException("Could not locate \"To Do\" item with id: " + _id);

			obj = ToDoItemBean.getToDoItem((ToDoItem)objList.get(0));
		}

		return obj;
    }

    private static ToDoItemBean
    getToDoItem(ToDoItem _obj)
		throws TorqueException
    {
		Integer key = new Integer(_obj.getToDoItemId());
		ToDoItemBean obj = (ToDoItemBean)hash.get(key);
		if (obj == null)
		{
			obj = new ToDoItemBean(_obj);
			hash.put(key, obj);
		}

		return obj;
    }

    public static Vector
    getToDoItem(UKOnlinePersonBean _person)
		throws TorqueException, ObjectNotFoundException
    {
		Vector vec = new Vector();
		
		Criteria crit = new Criteria();
		crit.add(ToDoItemPeer.PERSON_ID, _person.getId());
		crit.addAscendingOrderByColumn(ToDoItemPeer.TO_DO_DATE);
		Iterator itr = ToDoItemPeer.doSelect(crit).iterator();
		while (itr.hasNext())
			vec.addElement(ToDoItemBean.getToDoItem((ToDoItem)itr.next()));
	
		return vec;
    }
    
    // SQL
    
    /*
     * <table name="CONTACT_STATUS" idMethod="native">
    <column name="CONTACT_STATUS_ID" primaryKey="true" required="true" type="INTEGER" autoIncrement="true"/>
    <column name="COMPANY_ID" required="true" type="INTEGER"/>
	
    <column name="PERSON_ID" required="true" type="INTEGER"/>
    <column name="ADMIN_PERSON_ID" required="false" type="INTEGER" />
	
    <column name="STATUS" required="true" type="VARCHAR" size="50"/>
    <column name="CONTACT_DATE" required="true" type="DATE"/>
    
    <foreign-key foreignTable="COMPANY">
		<reference local="COMPANY_ID" foreign="COMPANYID"/>
    </foreign-key>
    <foreign-key foreignTable="PERSON">
		<reference local="PERSON_ID" foreign="PERSONID"/>
    </foreign-key>
    <foreign-key foreignTable="PERSON">
		<reference local="ADMIN_PERSON_ID" foreign="PERSONID"/>
    </foreign-key>
</table>
     */
	
    // INSTANCE VARIABLES
	
    private ToDoItem toDo;
    
    // CONSTRUCTORS
    
    public
    ToDoItemBean()
    {
		toDo = new ToDoItem();
		isNew = true;
    }
    
    public
    ToDoItemBean(ToDoItem _obj)
    {
		toDo = _obj;
		isNew = false;
    }
    
    // INSTANCE METHODS
    
    public UKOnlinePersonBean
    getAssignPerson()
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		return (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(toDo.getAssignPersonId());
    }
	
	public Date
	getCompletionDate()
	{
		return toDo.getCompletionDate();
	}
    
    public UKOnlinePersonBean
    getCompletePerson()
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		return (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(toDo.getCompletePersonId());
    }
	
	public Date
	getToDoDate()
	{
		return toDo.getToDoDate();
	}
    
    public int
    getId()
    {
		return toDo.getToDoItemId();
    }
    
    public String
    getLabel()
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		return CUBean.getUserDateString(toDo.getToDoDate()) + " " + toDo.getToDoText();
    }
	
	public UKOnlinePersonBean
	getPerson()
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		return (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(toDo.getPersonId());
	}
	
	public String
	getToDoText()
	{
		return toDo.getToDoText();
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
		toDo.save();
    }
    
    public void
    setAssignPerson(UKOnlinePersonBean _person)
		throws TorqueException
    {
		toDo.setAssignPersonId(_person.getId());
    }
    
    public void
    setCompletePerson(UKOnlinePersonBean _person)
		throws TorqueException
    {
		toDo.setCompletePersonId(_person.getId());
    }
	
	public void
	setCompany(CompanyBean _company)
		throws TorqueException
	{
		toDo.setCompanyId(_company.getId());
	}
	
	public void
	setCompletionDate(Date _date)
	{
		toDo.setCompletionDate(_date);
	}
	
	public void
	setToDoDate(Date _date)
	{
		toDo.setToDoDate(_date);
	}
    
    public void
    setPerson(UKOnlinePersonBean _person)
		throws TorqueException
    {
		toDo.setPersonId(_person.getId());
    }
	
	public void
	setToDoText(String _text)
	{
		toDo.setToDoText(_text);
	}
    
    protected void
    updateObject()
		throws ObjectAlreadyExistsException, IllegalValueException, Exception
    {
		toDo.save();
    }
}