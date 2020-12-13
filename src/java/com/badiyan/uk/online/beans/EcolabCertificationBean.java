/*
 * EcolabCertificationBean.java
 *
 * Created on February 17, 2007, 9:33 PM
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
EcolabCertificationBean
	extends CUBean
	implements java.io.Serializable
{
    // CLASS VARIABLES

    protected static HashMap<Integer,EcolabCertificationBean> hash = new HashMap<Integer,EcolabCertificationBean>(11);

    // CLASS METHODS
    
    public static Vector
    getActiveCertifications(UKOnlinePersonBean _person)
	throws TorqueException
    {
	Vector vec = new Vector();
	Date now = new Date();
	Criteria crit = new Criteria();
	crit.addJoin(PersonCertificationPeer.CERTIFICATION_ID, CertificationPeer.CERTIFICATION_ID);
	crit.add(CertificationPeer.ISACTIVE, (short)1);
	crit.add(PersonCertificationPeer.PERSON_ID, _person.getId());
	crit.add(PersonCertificationPeer.STARTDATE, now, Criteria.LESS_THAN);
	crit.add(PersonCertificationPeer.ENDDATE, now, Criteria.GREATER_THAN);
	Iterator itr = CertificationPeer.doSelect(crit).iterator();
	while (itr.hasNext())
	    vec.addElement(EcolabCertificationBean.getCertification((Certification)itr.next()));
	
	return vec;
    }
    
    public static Vector
    getActivePersonCertifications(UKOnlinePersonBean _person)
	throws TorqueException
    {
	Vector vec = new Vector();
	Date now = new Date();
	Criteria crit = new Criteria();
	crit.addJoin(PersonCertificationPeer.CERTIFICATION_ID, CertificationPeer.CERTIFICATION_ID);
	crit.add(CertificationPeer.ISACTIVE, (short)1);
	crit.add(PersonCertificationPeer.PERSON_ID, _person.getId());
	crit.add(PersonCertificationPeer.STARTDATE, now, Criteria.LESS_THAN);
	crit.add(PersonCertificationPeer.ENDDATE, now, Criteria.GREATER_THAN);
	Iterator itr = PersonCertificationPeer.doSelect(crit).iterator();
	while (itr.hasNext())
	    vec.addElement((PersonCertification)itr.next());
	
	return vec;
    }

    public static EcolabCertificationBean
    getCertification(int _id)
	throws TorqueException, ObjectNotFoundException
    {
	Integer key = new Integer(_id);
	EcolabCertificationBean certification = (EcolabCertificationBean)hash.get(key);
	if (certification == null)
	{
	    Criteria crit = new Criteria();
	    crit.add(CertificationPeer.CERTIFICATION_ID, _id);
	    List objList = CertificationPeer.doSelect(crit);
	    if (objList.size() == 0)
		throw new ObjectNotFoundException("Could not locate certification with id: " + _id);

	    certification = EcolabCertificationBean.getCertification((Certification)objList.get(0));
	}

	return certification;
    }

    private static EcolabCertificationBean
    getCertification(Certification _certification)
	throws TorqueException
    {
	Integer key = new Integer(_certification.getCertificationId());
	EcolabCertificationBean certification = (EcolabCertificationBean)hash.get(key);
	if (certification == null)
	{
	    certification = new EcolabCertificationBean(_certification);
	    hash.put(key, certification);
	}

	return certification;
    }
    
    public static Vector
    getCertifications()
	throws TorqueException
    {
	Vector vec = new Vector();
	Criteria crit = new Criteria();
	crit.addAscendingOrderByColumn(CertificationPeer.CERTIFICATION_NAME);
	Iterator itr = CertificationPeer.doSelect(crit).iterator();
	while (itr.hasNext())
	    vec.addElement(EcolabCertificationBean.getCertification((Certification)itr.next()));
	
	return vec;
    }
    
    // SQL
    
    /*
     *        <table name="CERTIFICATION" idMethod="native">
            <column name="CERTIFICATION_ID" primaryKey="true" required="true" type="INTEGER" autoIncrement="true"/>
            <column name="CERTIFICATION_NAME" required="true" size="200" type="VARCHAR"/>
	    <column name="ISACTIVE" required="true" type="SMALLINT" default="1"/>
        </table>
	
	<table name="PERSON_CERTIFICATION">
	    <column name="PERSON_ID" required="true" primaryKey="true" type="INTEGER" />
	    <column name="CERTIFICATION_ID" required="true" primaryKey="true" type="INTEGER" />
	    
	    <column name="STARTDATE" type="TIMESTAMP"/>
	    <column name="ENDDATE" type="TIMESTAMP"/>

	    <column name="ASSIGN_PERSON" required="true" type="INTEGER"/>

	    <foreign-key foreignTable="PERSON">
		<reference local="PERSON_ID" foreign="PERSONID"/>
	    </foreign-key>
	    <foreign-key foreignTable="CERTIFICATION">
		<reference local="CERTIFICATION_ID" foreign="CERTIFICATION_ID"/>
	    </foreign-key>
	    <foreign-key foreignTable="PERSON">
		<reference local="ASSIGN_PERSON" foreign="PERSONID"/>
	    </foreign-key>
	</table>
     */
	
    // INSTANCE VARIABLES
	
    private Certification certification;
    
    // CONSTRUCTORS
    
    public
    EcolabCertificationBean()
    {
	certification = new Certification();
	isNew = true;
    }
    
    public
    EcolabCertificationBean(Certification _certification)
    {
	certification = _certification;
	isNew = false;
    }
    
    // INSTANCE METHODS
    
    public void
    assign(UKOnlinePersonBean _person, Date _start_date, Date _end_date, UKOnlinePersonBean _assign_person)
	throws IllegalValueException, TorqueException, Exception
    {
	// already assigned?
	
	Criteria crit = new Criteria();
	crit.add(PersonCertificationPeer.CERTIFICATION_ID, this.getId());
	crit.add(PersonCertificationPeer.PERSON_ID, _person.getId());
	List obj_list = PersonCertificationPeer.doSelect(crit);
	if (obj_list.size() == 0)
	{
	    PersonCertification cert_map = new PersonCertification();
	    cert_map.setAssignPerson(_assign_person.getId());
	    cert_map.setStartdate(_start_date);
	    cert_map.setEnddate(_end_date);
	    cert_map.setPersonId(_person.getId());
	    cert_map.setCertificationId(this.getId());
	    cert_map.save();
	    
	    if (CUBean.isMasterServer())
		CUBean.storeDBSyncObject(new DBSyncMessage(-1, cert_map, DBSyncMessage.DB_INSERT, Calendar.getInstance(), "com.badiyan.torque.PersonCertificationPeer"), _person);
	}
	else if (obj_list.size() == 1)
	{
	    System.out.println("found ");
	    System.out.println("_start_date >" + _start_date);
	    System.out.println("_end_date >" + _end_date);
	    PersonCertification cert_map = (PersonCertification)obj_list.get(0);
	    cert_map.setAssignPerson(_assign_person.getId());
	    cert_map.setStartdate(_start_date);
	    cert_map.setEnddate(_end_date);
	    cert_map.save();
	    
	    if (CUBean.isMasterServer())
		CUBean.storeDBSyncObject(new DBSyncMessage(-1, cert_map, DBSyncMessage.DB_UPDATE, Calendar.getInstance(), "com.badiyan.torque.PersonCertificationPeer"), _person);
	}
	else
	    throw new IllegalValueException("Multiple certifications assigned to " + _person.getLabel());
	
	/*
	
	Vector active_certifications = EcolabCertificationBean.getActiveCertifications(_person);
	if (active_certifications.contains(this))
	{
	    //throw new IllegalValueException(_person.getLabel() + " has a current certification for " + this.getLabel());
	    
	    
	}
	else
	{
	    PersonCertification cert_map = new PersonCertification();
	    cert_map.setAssignPerson(_assign_person.getId());
	    cert_map.setStartdate(_start_date);
	    cert_map.setEnddate(_end_date);
	    cert_map.setPersonId(_person.getId());
	    cert_map.setCertificationId(this.getId());
	    cert_map.save();
	}
	 */
    }
    
    public UKOnlinePersonBean
    getContactPerson()
	throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
	return (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(certification.getContactPerson());
    }
    
    public String
    getDescriptionString()
    {
	String str = certification.getDescription();
	if (str == null)
	    return "";
	return str;
    }
    
    public int
    getId()
    {
	return certification.getCertificationId();
    }
    
    public String
    getLabel()
    {
	String name = certification.getCertificationName();
	if (name == null)
	    return "";
	return name;
    }
    
    public String
    getValue()
    {
	return certification.getCertificationId() + "";
    }
    
    protected void
    insertObject()
	throws Exception
    {
	certification.save();
	
	if (CUBean.isMasterServer())
	    CUBean.storeDBSyncObject(new DBSyncMessage(-1, certification, DBSyncMessage.DB_INSERT, Calendar.getInstance(), "com.badiyan.torque.CertificationPeer"));
    }
    
    public boolean
    isActive()
    {
	return (certification.getIsactive() == (short)1);
    }
    
    public void
    setContact(UKOnlinePersonBean _contact_person)
	throws TorqueException
    {
	certification.setContactPerson(_contact_person.getId());
    }
    
    public void
    setDescription(String _desc)
    {
	certification.setDescription(_desc);
    }
    
    public void
    setIsActive(boolean _active)
    {
	certification.setIsactive(_active ? (short)1 : (short)0);
    }
    
    public void
    setName(String _name)
    {
	certification.setCertificationName(_name);
    }
    
    protected void
    updateObject()
	throws ObjectAlreadyExistsException, IllegalValueException, Exception
    {
	certification.save();
	
	if (CUBean.isMasterServer())
	    CUBean.storeDBSyncObject(new DBSyncMessage(-1, certification, DBSyncMessage.DB_UPDATE, Calendar.getInstance(), "com.badiyan.torque.CertificationPeer"));
    }
}