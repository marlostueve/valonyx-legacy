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
DiagnosisCodeVersion
	extends CUBean
	implements java.io.Serializable
{
    // CLASS VARIABLES

    protected static HashMap<Integer,DiagnosisCodeVersion> hash = new HashMap<Integer,DiagnosisCodeVersion>(11);

    // CLASS METHODS

    public static DiagnosisCodeVersion
    getDiagnosisCodeVersion(int _id)
		throws TorqueException, ObjectNotFoundException
    {
		Integer key = new Integer(_id);
		DiagnosisCodeVersion version = (DiagnosisCodeVersion)hash.get(key);
		if (version == null)
		{
			Criteria crit = new Criteria();
			crit.add(DiagnosisCodeVersionDbPeer.DIAGNOSIS_CODE_VERSION_DB_ID, _id);
			List objList = DiagnosisCodeVersionDbPeer.doSelect(crit);
			if (objList.isEmpty())
				throw new ObjectNotFoundException("Could not locate Diagnosis Code Version with id: " + _id);

			version = DiagnosisCodeVersion.getDiagnosisCodeVersion((DiagnosisCodeVersionDb)objList.get(0));
		}

		return version;
    }

    private static DiagnosisCodeVersion
    getDiagnosisCodeVersion(DiagnosisCodeVersionDb _version)
		throws TorqueException
    {
		Integer key = new Integer(_version.getDiagnosisCodeVersionDbId());
		DiagnosisCodeVersion version = (DiagnosisCodeVersion)hash.get(key);
		if (version == null)
		{
			version = new DiagnosisCodeVersion(_version);
			hash.put(key, version);
		}

		return version;
    }
	
	public static DiagnosisCodeVersion
	getDiagnosisCodeVersion(String _versionName, boolean _create) throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		Criteria crit = new Criteria();
		crit.add(DiagnosisCodeVersionDbPeer.VERSION, _versionName);
		List objList = DiagnosisCodeVersionDbPeer.doSelect(crit);
		if (objList.size() == 1) {
			DiagnosisCodeVersionDb obj = (DiagnosisCodeVersionDb)objList.get(0);
			return DiagnosisCodeVersion.getDiagnosisCodeVersion(obj);
		} else if (objList.size() == 0) {
			if (_create) {
				DiagnosisCodeVersion version = new DiagnosisCodeVersion();
				version.setVersion(_versionName);
				version.activate();
				version.save();
				return version;
			} else {
				throw new ObjectNotFoundException("Diagnosis Code Version not found for " + _versionName);
			}
		} else {
			throw new UniqueObjectNotFoundException("Unique Diagnosis Code Version not found for " + _versionName);
		}
	}

    public static Vector
    getDiagnosisCodeVersions(boolean _active)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		Vector vec = new Vector();
		
		Criteria crit = new Criteria();
		crit.add(DiagnosisCodeVersionDbPeer.IS_ACTIVE, _active ? (short)1 : (short)0);
		crit.addAscendingOrderByColumn(DiagnosisCodeVersionDbPeer.VERSION);
		Iterator itr = DiagnosisCodeVersionDbPeer.doSelect(crit).iterator();
		while (itr.hasNext()) {
			DiagnosisCodeVersionDb obj = (DiagnosisCodeVersionDb)itr.next();
			DiagnosisCodeVersion version = DiagnosisCodeVersion.getDiagnosisCodeVersion(obj);
			vec.addElement(version);
		}
		
		return vec;
    }

    // SQL

    /*
     * 
<table name="DIAGNOSIS_CODE_VERSION_DB" idMethod="native">
    <column name="DIAGNOSIS_CODE_VERSION_DB_ID" primaryKey="true" required="true" type="INTEGER" autoIncrement="true"/>
	<column name="VERSION" required="true" type="VARCHAR" size="50"/>
	
    <column name="IS_ACTIVE" type="SMALLINT" default="1"/>

</table>
	 * 
	 * 
     */

    // INSTANCE VARIABLES

    private DiagnosisCodeVersionDb version;

    // CONSTRUCTORS

    public
    DiagnosisCodeVersion()
    {
		version = new DiagnosisCodeVersionDb();
		isNew = true;
    }

    public
    DiagnosisCodeVersion(DiagnosisCodeVersionDb _version)
    {
		version = _version;
		isNew = false;
    }

    // INSTANCE METHODS

    public String
    getVersion()
    {
		return version.getVersion();
    }

    public void
    setVersion(String _v)
    {
		version.setVersion(_v);
    }

    public int
    getId()
    {
		return version.getDiagnosisCodeVersionDbId();
    }

    public String
    getLabel()
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		return this.getVersion() + " - " + (this.isActive() ? "Active" : "Inactive");
    }

    public String
    getValue()
    {
		return version.getDiagnosisCodeVersionDbId() + "";
    }

    protected void
    insertObject()
		throws Exception
    {
		version.save();
    }
	
	public boolean
	isActive()
	{
		return (version.getIsActive() == (short)1);
	}

	public void
	activate()
		throws TorqueException
	{
		version.setIsActive((short)1);
	}

	public void
	inActivate()
		throws TorqueException
	{
		version.setIsActive((short)0);
	}

    protected void
    updateObject()
		throws ObjectAlreadyExistsException, IllegalValueException, Exception
    {
		version.save();
    }
}