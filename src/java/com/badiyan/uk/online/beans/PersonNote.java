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
PersonNote
	extends CUBean
	implements java.io.Serializable
{
    // CLASS VARIABLES

    protected static HashMap<Integer,PersonNote> hash = new HashMap<Integer,PersonNote>(11);

    // CLASS METHODS

    public static void
    delete(int _id)
		throws TorqueException
    {
		Criteria crit = new Criteria();
		crit.add(PersonNoteDbPeer.PERSON_NOTE_DB_ID, _id);
		PersonNoteDbPeer.doDelete(crit);
    }

    public static PersonNote
    getPersonNote(int _id)
		throws TorqueException, ObjectNotFoundException
    {
		Integer key = new Integer(_id);
		PersonNote obj = (PersonNote)hash.get(key);
		if (obj == null)
		{
			Criteria crit = new Criteria();
			crit.add(PersonNoteDbPeer.PERSON_NOTE_DB_ID, _id);
			List objList = PersonNoteDbPeer.doSelect(crit);
			if (objList.size() == 0)
				throw new ObjectNotFoundException("Could not locate Referral Source with id: " + _id);

			obj = PersonNote.getPersonNote((PersonNoteDb)objList.get(0));
		}

		return obj;
    }

    private static PersonNote
    getPersonNote(PersonNoteDb _person_note)
		throws TorqueException
    {
		Integer key = new Integer(_person_note.getPersonNoteDbId());
		PersonNote obj = (PersonNote)hash.get(key);
		if (obj == null)
		{
			obj = new PersonNote(_person_note);
			hash.put(key, obj);
		}

		return obj;
    }

    public static Vector
    getPersonNotes(UKOnlinePersonBean _person)
		throws TorqueException
    {
		Vector vec = new Vector();

		Criteria crit = new Criteria();
		crit.add(PersonNoteDbPeer.PERSON_ID, _person.getId());
		Iterator obj_itr = PersonNoteDbPeer.doSelect(crit).iterator();
		while (obj_itr.hasNext()) {
			vec.addElement(PersonNote.getPersonNote((PersonNoteDb)obj_itr.next()));
		}

		return vec;
    }

    public static Vector
    getPersonNotesToShowOnCheckIn(UKOnlinePersonBean _person)
		throws TorqueException
    {
		Vector vec = new Vector();

		Criteria crit = new Criteria();
		crit.add(PersonNoteDbPeer.PERSON_ID, _person.getId());
		crit.add(PersonNoteDbPeer.ISALERT, (short)1);
		Iterator obj_itr = PersonNoteDbPeer.doSelect(crit).iterator();
		while (obj_itr.hasNext()) {
			vec.addElement(PersonNote.getPersonNote((PersonNoteDb)obj_itr.next()));
		}

		return vec;
    }

    // SQL

    /*
     * <table name="PERSON_NOTE_DB" idMethod="native">
    <column name="PERSON_NOTE_DB_ID" primaryKey="true" required="true" type="INTEGER" autoIncrement="true"/>
    <column name="PERSON_ID" required="true" type="INTEGER"/>
	<column name="NOTE_SUBJECT" required="true" type="VARCHAR" size="100"/>
    <column name="NOTE" type="LONGVARCHAR"/>
    <column name="ISALERT" required="true" type="SMALLINT"/>
    <column name="NOTE_DATE" required="true" type="DATE"/>
    <column name="EMAIL_DATE" required="false" type="DATE"/>

    <foreign-key foreignTable="PERSON">
		<reference local="PERSON_ID" foreign="PERSONID"/>
    </foreign-key>
</table>
     */

    // INSTANCE VARIABLES

    private PersonNoteDb person_note;

    // CONSTRUCTORS

    public
    PersonNote()
    {
		person_note = new PersonNoteDb();
		isNew = true;
    }

    public
    PersonNote(PersonNoteDb _person_note)
    {
		person_note = _person_note;
		isNew = false;
    }

    // INSTANCE METHODS

    public String
    getNoteDateString()
    {
		Date date = person_note.getNoteDate();
		if (date == null)
			return "";
		return CUBean.getUserDateString(date);
    }

    public int
    getId()
    {
		return person_note.getPersonNoteDbId();
    }

    public String
    getLabel()
    {
		if (this.hasBeenEmailed())
			return "*" + this.getNoteDateString() + " - " + this.getSubjectString();
		return this.getNoteDateString() + " - " + this.getSubjectString();
    }

	public String
	getNoteString()
	{
		String str = person_note.getNote();
		if (str == null)
			return "";
		return str;
	}

	public UKOnlinePersonBean
	getPerson()
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		return (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(person_note.getPersonId());
	}

	public String
	getSubjectString()
	{
		String str = person_note.getNoteSubject();
		if (str == null)
			return "";
		return str;
	}

    public String
    getValue()
    {
		return person_note.getPersonNoteDbId() + "";
    }

	public boolean
	hasBeenEmailed()
	{
		return person_note.getEmailDate() != null;
	}

	@Override
    protected void
    insertObject()
		throws Exception
    {
		person_note.setNoteDate(new Date());
		person_note.save();
    }

	public boolean
	showOnCheckIn() {
		return (person_note.getIsalert() == (short)1);
	}

    public void
    setEmailDate(Date _date)
    {
		person_note.setEmailDate(_date);
    }

    public void
    setNoteDate(Date _date)
    {
		person_note.setNoteDate(_date);
    }

    public void
    setNote(String _note)
		throws TorqueException
    {
		person_note.setNote(_note);
    }

    public void
    setPerson(UKOnlinePersonBean _person)
		throws TorqueException
    {
		person_note.setPersonId(_person.getId());
    }

	public void
	setShowOnCheckIn(boolean _show)
	{
		person_note.setIsalert(_show ? (short)1 : (short)0);
	}

    public void
    setSubject(String _subject)
		throws TorqueException
    {
		person_note.setNoteSubject(_subject);
    }

	@Override
    protected void
    updateObject()
		throws ObjectAlreadyExistsException, IllegalValueException, Exception
    {
		person_note.save();
    }
}