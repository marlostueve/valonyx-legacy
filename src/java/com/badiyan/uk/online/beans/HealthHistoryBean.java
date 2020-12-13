/*
 * To change this template, choose Tools | Templates
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
HealthHistoryBean
	extends CUBean
	implements java.io.Serializable
{
    // CLASS VARIABLES

    protected static HashMap<Integer,HealthHistoryBean> hash = new HashMap<Integer,HealthHistoryBean>(11);

    // CLASS METHODS

    public static void
    delete(int _id)
		throws TorqueException
    {
		Criteria crit = new Criteria();
		crit.add(HealthHistoryPeer.HEALTH_HISTORY_ID, _id);
		HealthHistoryPeer.doDelete(crit);
    }

    public static HealthHistoryBean
    getHealthHistory(int _id)
		throws TorqueException, ObjectNotFoundException
    {
		Integer key = new Integer(_id);
		HealthHistoryBean history = (HealthHistoryBean)hash.get(key);
		if (history == null)
		{
			Criteria crit = new Criteria();
			crit.add(HealthHistoryPeer.HEALTH_HISTORY_ID, _id);
			List objList = HealthHistoryPeer.doSelect(crit);
			if (objList.size() == 0)
				throw new ObjectNotFoundException("Could not locate Health History with id: " + _id);

			history = HealthHistoryBean.getHealthHistory((HealthHistory)objList.get(0));
		}

		return history;
    }

    private static HealthHistoryBean
    getHealthHistory(HealthHistory _history)
		throws TorqueException
    {
		Integer key = new Integer(_history.getHealthHistoryId());
		HealthHistoryBean history = (HealthHistoryBean)hash.get(key);
		if (history == null)
		{
			history = new HealthHistoryBean(_history);
			hash.put(key, history);
		}

		return history;
    }

    public static HealthHistoryBean
    getHealthHistory(UKOnlinePersonBean _person, boolean _create)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		Criteria crit = new Criteria();
		crit.add(HealthHistoryPeer.PERSON_ID, _person.getId());
		List objList = HealthHistoryPeer.doSelect(crit);
		if (objList.size() == 1)
			return HealthHistoryBean.getHealthHistory((HealthHistory)objList.get(0));
		else if (objList.size() == 0)
		{
			if (_create)
			{
				HealthHistoryBean history = new HealthHistoryBean();
				history.setPerson(_person);
				return history;
			}
			else
				throw new ObjectNotFoundException("Could not locate Health History for " + _person.getLabel());
		}
		else
			throw new UniqueObjectNotFoundException("Could not locate unique Health History for " + _person.getLabel());
    }

    // SQL

    /*
     *        <table name="HEALTH_HISTORY" idMethod="native">
    <column name="HEALTH_HISTORY_ID" primaryKey="true" required="true" type="INTEGER" autoIncrement="true"/>
    <column name="PERSON_ID" required="true" type="INTEGER"/>
    <column name="HISTORY" type="LONGVARCHAR"/>
    <column name="CREATION_DATE" required="true" type="TIMESTAMP"/>
    <column name="MODIFICATION_DATE" required="false" type="TIMESTAMP"/>
    <column name="CREATE_PERSON_ID" required="false" type="INTEGER"/>
    <column name="MODIFY_PERSON_ID" required="false" type="INTEGER"/>

    <foreign-key foreignTable="PERSON">
	<reference local="PERSON_ID" foreign="PERSONID"/>
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

    private HealthHistory history;

    // CONSTRUCTORS

    public
    HealthHistoryBean()
    {
		history = new HealthHistory();
		isNew = true;
    }

    public
    HealthHistoryBean(HealthHistory _history)
    {
		history = _history;
		isNew = false;
    }

    // INSTANCE METHODS

    public int
    getId()
    {
		return history.getHealthHistoryId();
    }

    public String
    getLabel()
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		return this.getPerson().getLabel() + " health history";
    }

	UKOnlinePersonBean
	getPerson()
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		return (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(history.getPersonId());
	}

    public String
    getHistoryString()
    {
		String str = history.getHistory();
		if (str == null)
			return "";
		return str;
    }

    public String
    getValue()
    {
		return history.getHealthHistoryId() + "";
    }

    protected void
    insertObject()
		throws Exception
    {
		history.setCreationDate(new Date());
		history.save();
    }

    public void
    setCreateOrModifyPerson(UKOnlinePersonBean _person)
		throws TorqueException
    {
		if (this.isNew())
			history.setCreatePersonId(_person.getId());
		else
			history.setModifyPersonId(_person.getId());
    }

    public void
    setPerson(PersonBean _person)
		throws TorqueException
    {
		history.setPersonId(_person.getId());
    }

    public void
    setHistory(String _str)
    {
		if (_str.length() > 0)
			history.setHistory(_str);
    }

    protected void
    updateObject()
	throws ObjectAlreadyExistsException, IllegalValueException, Exception
    {
		history.setModificationDate(new Date());
		history.save();
    }
}