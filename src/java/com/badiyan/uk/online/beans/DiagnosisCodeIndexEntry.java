/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.badiyan.uk.online.beans;

import com.badiyan.uk.beans.*;

import com.badiyan.torque.*;
import com.badiyan.uk.exceptions.*;

import java.util.*;

import org.apache.torque.*;
import org.apache.torque.util.Criteria;


/**
 *
 * @author marlo
 */
public class
DiagnosisCodeIndexEntry
	extends CUBean
	implements java.io.Serializable
{
    // CLASS VARIABLES

    protected static HashMap<Integer,DiagnosisCodeIndexEntry> hash = new HashMap<Integer,DiagnosisCodeIndexEntry>(11);

    // CLASS METHODS

    public static void
    delete(int _id)
		throws TorqueException
    {
		Criteria crit = new Criteria();
		crit.add(DiagnosisCodeIndexDbPeer.DIAGNOSIS_CODE_INDEX_DB_ID, _id);
		DiagnosisCodeIndexDbPeer.doDelete(crit);
    }

    public static DiagnosisCodeIndexEntry
    getDiagnosisCodeIndexEntry(int _id)
		throws TorqueException, ObjectNotFoundException
    {
		Integer key = new Integer(_id);
		DiagnosisCodeIndexEntry obj = (DiagnosisCodeIndexEntry)hash.get(key);
		if (obj == null)
		{
			Criteria crit = new Criteria();
			crit.add(DiagnosisCodeIndexDbPeer.DIAGNOSIS_CODE_INDEX_DB_ID, _id);
			List objList = DiagnosisCodeIndexDbPeer.doSelect(crit);
			if (objList.size() == 0)
				throw new ObjectNotFoundException("Could not locate Diagnosis Code Index Entry with id: " + _id);

			obj = DiagnosisCodeIndexEntry.getDiagnosisCodeIndexEntry((DiagnosisCodeIndexDb)objList.get(0));
		}

		return obj;
    }

    private static DiagnosisCodeIndexEntry
    getDiagnosisCodeIndexEntry(DiagnosisCodeIndexDb _index_entry)
		throws TorqueException
    {
		Integer key = new Integer(_index_entry.getDiagnosisCodeIndexDbId());
		DiagnosisCodeIndexEntry obj = (DiagnosisCodeIndexEntry)hash.get(key);
		if (obj == null)
		{
			obj = new DiagnosisCodeIndexEntry(_index_entry);
			hash.put(key, obj);
		}

		return obj;
    }

    public static Vector
    getDiagnosisCodeIndices(DiagnosisCodeVersion _version, boolean _active)
		throws TorqueException
    {
		Vector vec = new Vector();

                /*
		Criteria crit = new Criteria();
		crit.add(DiagnosisCodeIndexDbPeer.DIAGNOSIS_CODE_VERSION_DB_ID, _version.getId());
		crit.add(DiagnosisCodeIndexDbPeer.IS_ACTIVE, _active ? (short)1 : (short)0);
		crit.addAscendingOrderByColumn(DiagnosisCodeIndexDbPeer.DESC);
		Iterator itr = DiagnosisCodeIndexDbPeer.doSelect(crit).iterator();
		while (itr.hasNext())
			vec.addElement(DiagnosisCodeIndexEntry.getDiagnosisCodeIndexEntry((DiagnosisCodeIndexDb)itr.next()));

                */
                
		return vec;
    }

    // SQL

    /*
	 * 
<table name="DIAGNOSIS_CODE_INDEX_DB" idMethod="native">
    <column name="DIAGNOSIS_CODE_INDEX_DB_ID" primaryKey="true" required="true" type="INTEGER" autoIncrement="true"/>
    <column name="DESC" required="true" type="VARCHAR" size="250"/>
    <column name="PARENT" required="false" type="INTEGER"/>
    <column name="DIAGNOSIS_CODE_DB_ID" required="false" type="INTEGER"/>
	<column name="IS_ACTIVE" type="SMALLINT" default="1"/>
	
	<column name="DIAGNOSIS_CODE_VERSION_DB_ID" required="true" type="INTEGER"/>

    <foreign-key foreignTable="DIAGNOSIS_CODE_INDEX_DB">
		<reference local="PARENT" foreign="DIAGNOSIS_CODE_INDEX_DB_ID"/>
    </foreign-key>

    <foreign-key foreignTable="DIAGNOSIS_CODE_DB">
		<reference local="DIAGNOSIS_CODE_DB_ID" foreign="DIAGNOSIS_CODE_DB_ID"/>
    </foreign-key>

    <foreign-key foreignTable="DIAGNOSIS_CODE_VERSION_DB">
		<reference local="DIAGNOSIS_CODE_VERSION_DB_ID" foreign="DIAGNOSIS_CODE_VERSION_DB_ID"/>
    </foreign-key>
	
</table>
	 * 
     */

    // INSTANCE VARIABLES

    private DiagnosisCodeIndexDb index_entry;
	private Vector members;

    // CONSTRUCTORS

    public
    DiagnosisCodeIndexEntry()
    {
		index_entry = new DiagnosisCodeIndexDb();
		isNew = true;
    }

    public
    DiagnosisCodeIndexEntry(DiagnosisCodeIndexDb _index_entry)
    {
		index_entry = _index_entry;
		isNew = false;
    }

    // INSTANCE METHODS

    public int
    getId()
    {
		return index_entry.getDiagnosisCodeIndexDbId();
    }

    public String
    getLabel()
    {
		return this.getDescriptionString();
    }

    public String
    getDescriptionString()
    {
        return null;
        
        /*
		String str = index_entry.getDesc();
		if (str == null)
			return "";
		return str;
                */
    }

    public String
    getValue()
    {
		return index_entry.getDiagnosisCodeIndexDbId() + "";
    }

	@Override
    protected void
    insertObject()
		throws Exception
    {
		index_entry.save();
    }

	public boolean
	isActive()
	{
		return (index_entry.getIsActive() == (short)1);
	}

    public DiagnosisCodeIndexEntry
    getParent()
		throws TorqueException, ObjectNotFoundException
    {
		return DiagnosisCodeIndexEntry.getDiagnosisCodeIndexEntry(index_entry.getParent());
    }

    public void
    setParent(DiagnosisCodeIndexEntry _entry)
		throws TorqueException
    {
		index_entry.setParent(_entry.getId());
    }

    public void
    setDescription(String _str)
    {
		//index_entry.setDesc(_str);
    }

    public void
    setIsActive(boolean _active)
    {
		index_entry.setIsActive(_active ? (short)1 : (short)0);
    }

    public DiagnosisCodeVersion
    getVersion()
		throws TorqueException, ObjectNotFoundException
    {
		return DiagnosisCodeVersion.getDiagnosisCodeVersion(index_entry.getDiagnosisCodeVersionDbId());
    }

    public void
    setVersion(DiagnosisCodeIndexEntry _entry)
		throws TorqueException
    {
		index_entry.setDiagnosisCodeVersionDbId(_entry.getId());
    }

	@Override
    protected void
    updateObject()
		throws ObjectAlreadyExistsException, IllegalValueException, Exception
    {
		index_entry.save();
    }
}