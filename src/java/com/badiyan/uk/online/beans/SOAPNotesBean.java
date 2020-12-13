/*
 * SOAPNotesBean.java
 *
 * Created on October 13, 2007, 5:01 PM
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
SOAPNotesBean
	extends CUBean
	implements java.io.Serializable
{
    // CLASS VARIABLES

    protected static HashMap<Integer,SOAPNotesBean> hash = new HashMap<Integer,SOAPNotesBean>(11);

    // CLASS METHODS
	
    public static void
    delete(int _id)
	throws TorqueException
    {
		Criteria crit = new Criteria();
		crit.add(SoapNotesPeer.SOAP_NOTES_ID, _id);
		SoapNotesPeer.doDelete(crit);
    }
    
    public static Vector
    getSOAPNotes(UKOnlinePersonBean _person)
		throws TorqueException
    {
		Vector vec = new Vector();

		Criteria crit = new Criteria();
		crit.add(SoapNotesPeer.PERSON_ID, _person.getId());
		crit.addDescendingOrderByColumn(SoapNotesPeer.ANALYSIS_DATE);
		Iterator itr = SoapNotesPeer.doSelect(crit).iterator();
		while (itr.hasNext())
			vec.addElement(SOAPNotesBean.getSOAPNotes((SoapNotes)itr.next()));

		return vec;
    }
    
    public static SOAPNotesBean
    getSOAPNotes(UKOnlinePersonBean _person, Date _analysisDate)
		throws TorqueException, ObjectNotFoundException
    {
		Calendar start_of_day = Calendar.getInstance();
		start_of_day.setTime(_analysisDate);
		start_of_day.set(Calendar.HOUR_OF_DAY, 0);
		start_of_day.set(Calendar.MINUTE, 0);
		start_of_day.set(Calendar.SECOND, 0);

		Calendar end_of_day = Calendar.getInstance();
		end_of_day.setTime(_analysisDate);
		end_of_day.set(Calendar.HOUR_OF_DAY, 23);
		end_of_day.set(Calendar.MINUTE, 59);
		end_of_day.set(Calendar.SECOND, 59);

		Criteria crit = new Criteria();
		crit.add(SoapNotesPeer.PERSON_ID, _person.getId());
		crit.add(SoapNotesPeer.ANALYSIS_DATE, start_of_day.getTime(), Criteria.GREATER_EQUAL);
		crit.and(SoapNotesPeer.ANALYSIS_DATE, end_of_day.getTime(), Criteria.LESS_EQUAL);
		crit.addDescendingOrderByColumn(SoapNotesPeer.ANALYSIS_DATE);
		
		//System.out.println("getSOAPNotes crit() >" + crit.toString());
		
		List list = SoapNotesPeer.doSelect(crit);
		if (list.size() == 1) {
			return SOAPNotesBean.getSOAPNotes((SoapNotes)list.get(0));
		}
		
		throw new ObjectNotFoundException("SOAP note not found for date for " + _person.getLabel());
    }

    public static SOAPNotesBean
    getSOAPNotes(int _id)
		throws TorqueException, ObjectNotFoundException
    {
		Integer key = new Integer(_id);
		SOAPNotesBean notes = (SOAPNotesBean)hash.get(key);
		if (notes == null)
		{
			Criteria crit = new Criteria();
			crit.add(SoapNotesPeer.SOAP_NOTES_ID, _id);
			List objList = SoapNotesPeer.doSelect(crit);
			if (objList.size() == 0)
			throw new ObjectNotFoundException("Could not locate SOAP Notes with id: " + _id);

			notes = SOAPNotesBean.getSOAPNotes((SoapNotes)objList.get(0));
		}

		return notes;
    }

    private static SOAPNotesBean
    getSOAPNotes(SoapNotes _notes)
		throws TorqueException
    {
		Integer key = new Integer(_notes.getSoapNotesId());
		SOAPNotesBean notes = (SOAPNotesBean)hash.get(key);
		if (notes == null)
		{
			notes = new SOAPNotesBean(_notes);
			hash.put(key, notes);
		}

		return notes;
    }
	
	public static void
	deleteStatement(int _id)
		throws TorqueException
	{
		Criteria crit = new Criteria();
		crit.add(SoapStatementPeer.SOAP_STATEMENT_ID, _id);
		SoapStatementPeer.doDelete(crit);
	}
	
	public static SoapStatement
	getStatement(int _id)
		throws TorqueException, ObjectNotFoundException
	{
		Criteria crit = new Criteria();
		crit.add(SoapStatementPeer.SOAP_STATEMENT_ID, _id);
		List obj_list = SoapStatementPeer.doSelect(crit);
		if (obj_list.size() == 1)
			return (SoapStatement)obj_list.get(0);
		
		throw new ObjectNotFoundException("SOAP Statement not found for id " + _id);
	}
	
	public static Vector
	getChildStatements(SoapStatement _statement)
		throws TorqueException
	{
		Vector vec = new Vector();
		Criteria crit = new Criteria();
		crit.add(SoapStatementPeer.PARENT_ID, _statement.getSoapStatementId());
		crit.addAscendingOrderByColumn(SoapStatementPeer.STATEMENT);
		Iterator obj_itr = SoapStatementPeer.doSelect(crit).iterator();
		while (obj_itr.hasNext())
		{
			SoapStatement statement = (SoapStatement)obj_itr.next();
			vec.addElement(statement);
		}
		
		return vec;
	}
	
	public static ArrayList<ArrayList>
	getStatements(CompanyBean _company, PracticeAreaBean _practice_area)
		throws TorqueException
	{
		ArrayList s_vec = new ArrayList();
		ArrayList o_vec = new ArrayList();
		ArrayList a_vec = new ArrayList();
		ArrayList p_vec = new ArrayList();
		Criteria crit = new Criteria();
		crit.add(SoapStatementPeer.COMPANY_ID, _company.getId());
		crit.add(SoapStatementPeer.PRACTICE_AREA_ID, _practice_area.getId());
		crit.add(SoapStatementPeer.PARENT_ID, 0);
		crit.addAscendingOrderByColumn(SoapStatementPeer.STATEMENT);
		Iterator obj_itr = SoapStatementPeer.doSelect(crit).iterator();
		while (obj_itr.hasNext()) {
			SoapStatement statement = (SoapStatement)obj_itr.next();
			if (statement.getType().equals("S")) {
				s_vec.add(statement);
			} else if (statement.getType().equals("O")) {
				o_vec.add(statement);
			} else if (statement.getType().equals("A")) {
				a_vec.add(statement);
			} else if (statement.getType().equals("P")) {
				p_vec.add(statement);
			}
		}
		
		ArrayList l = new ArrayList();
		l.add(s_vec);
		l.add(o_vec);
		l.add(a_vec);
		l.add(p_vec);
		
		return l;
	}
	
	public static Vector
	getStatements(CompanyBean _company, PracticeAreaBean _practice_area, String _type)
		throws TorqueException
	{
		Vector vec = new Vector();
		Criteria crit = new Criteria();
		crit.add(SoapStatementPeer.COMPANY_ID, _company.getId());
		crit.add(SoapStatementPeer.PRACTICE_AREA_ID, _practice_area.getId());
		crit.add(SoapStatementPeer.TYPE, _type);
		crit.add(SoapStatementPeer.PARENT_ID, 0);
		crit.addAscendingOrderByColumn(SoapStatementPeer.STATEMENT);
		Iterator obj_itr = SoapStatementPeer.doSelect(crit).iterator();
		while (obj_itr.hasNext())
		{
			SoapStatement statement = (SoapStatement)obj_itr.next();
			vec.addElement(statement);
		}
		
		return vec;
	}

	public static Vector
	getStatements(int _parent_id)
		throws TorqueException
	{
		Vector vec = new Vector();
		Criteria crit = new Criteria();
		//crit.add(SoapStatementPeer.COMPANY_ID, _company.getId());
		//crit.add(SoapStatementPeer.PRACTICE_AREA_ID, _practice_area.getId());
		//crit.add(SoapStatementPeer.TYPE, _type);
		crit.add(SoapStatementPeer.PARENT_ID, _parent_id);
		crit.addAscendingOrderByColumn(SoapStatementPeer.STATEMENT);
		Iterator obj_itr = SoapStatementPeer.doSelect(crit).iterator();
		while (obj_itr.hasNext())
		{
			SoapStatement statement = (SoapStatement)obj_itr.next();
			vec.addElement(statement);
		}

		return vec;
	}

	public static Vector
	getStatementParents(CompanyBean _company, int _child_id)
		throws TorqueException
	{
		Vector vec = new Vector();

		Criteria crit = new Criteria();
		crit.add(SoapStatementPeer.SOAP_STATEMENT_ID, _child_id);
		List obj_list = SoapStatementPeer.doSelect(crit);
		if (obj_list.size() == 1)
		{
			SoapStatement child = (SoapStatement)obj_list.get(0);

			crit = new Criteria();
			crit.add(SoapStatementPeer.SOAP_STATEMENT_ID, child.getParentId());
			obj_list = SoapStatementPeer.doSelect(crit);
			if (obj_list.size() == 1)
			{
				SoapStatement parent = (SoapStatement)obj_list.get(0);

				crit = new Criteria();
				crit.add(SoapStatementPeer.COMPANY_ID, _company.getId());
				crit.add(SoapStatementPeer.PRACTICE_AREA_ID, child.getPracticeAreaId());
				crit.add(SoapStatementPeer.TYPE, child.getType());
				crit.add(SoapStatementPeer.PARENT_ID, parent.getParentId());
				Iterator obj_itr = SoapStatementPeer.doSelect(crit).iterator();
				while (obj_itr.hasNext())
				{
					SoapStatement statement = (SoapStatement)obj_itr.next();
					vec.addElement(statement);
				}
			}
		}

		return vec;
	}
    
    // SQL
    
    /*
     *        <table name="SOAP_NOTES" idMethod="native">
	    <column name="SOAP_NOTES_ID" primaryKey="true" required="true" type="INTEGER" autoIncrement="true"/>
	    
	    <column name="PERSON_ID" required="true" type="INTEGER"/>

	    <column name="S_NOTES" type="LONGVARCHAR"/>
	    <column name="O_NOTES" type="LONGVARCHAR"/>
	    <column name="A_NOTES" type="LONGVARCHAR"/>
	    <column name="P_NOTES" type="LONGVARCHAR"/>
	    
	    <column name="ANALYSIS_DATE" required="true" type="TIMESTAMP"/>
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
	
    private SoapNotes notes;
    
    // CONSTRUCTORS
    
    public
    SOAPNotesBean()
    {
		notes = new SoapNotes();
		isNew = true;
    }
    
    public
    SOAPNotesBean(SoapNotes _notes)
    {
		notes = _notes;
		isNew = false;
    }
    
    // INSTANCE METHODS
    
    public String
    getAnalysisDateString()
    {
		Date date = notes.getAnalysisDate();
		if (date == null)
			return "";
		return CUBean.getUserDateString(date);
    }

	public Date
	getCreationDate()
	{
		return notes.getCreationDate();
	}

	public UKOnlinePersonBean
	getCreatePerson()
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		return (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(notes.getCreatePersonId());
	}

	public UKOnlinePersonBean
	getModifyPerson()
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		return (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(notes.getModifyPersonId());
	}
    
    public int
    getId()
    {
		return notes.getSoapNotesId();
    }
    
    public String
    getLabel()
    {
		//return this.getAnalysisDateString() + " - " + getSNoteString();
		
		String str = this.getONoteString();
		if (str.isEmpty()) {
			return this.getAnalysisDateString();
		} else if (str.length() > 30) {
			return this.getAnalysisDateString() + " - " + getONoteString().substring(0, 29); 
		}
		return this.getAnalysisDateString() + " - " + getONoteString();
    }

    public UKOnlinePersonBean
    getPerson()
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		return (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(notes.getPersonId());
    }

	public PracticeAreaBean
	getPracticeArea()
		throws TorqueException, ObjectNotFoundException
	{
		return PracticeAreaBean.getPracticeArea(notes.getPracticeAreaId());
	}
    
    public String
    getSNoteString() {
		String str = notes.getSNotes();
		if (str == null)
			return "";
		if (str.indexOf("~|~") > -1) {
			String[] arr = str.split("~|~");
			return arr[0];
		}
		return str;
    }
    
    public String
    getSNoteStatementHTMLString() {
		String str = notes.getSNotes();
		if (str == null)
			return "";
		if (str.indexOf("~|~") > -1) {
			String[] arr = str.split("~|~");
			return arr[2];
		}
		return "";
    }
    
    public String
    getONoteString() {
		String str = notes.getONotes();
		if (str == null)
			return "";
		if (str.indexOf("~|~") > -1) {
			String[] arr = str.split("~|~");
			return arr[0];
		}
		return str;
    }
    
    public String
    getONoteStatementHTMLString() {
		String str = notes.getONotes();
		if (str == null)
			return "";
		if (str.indexOf("~|~") > -1) {
			String[] arr = str.split("~|~");
			return arr[2];
		}
		return "";
    }
    
    public String
    getANoteString() {
		String str = notes.getANotes();
		if (str == null)
			return "";
		if (str.indexOf("~|~") > -1) {
			String[] arr = str.split("~|~");
			return arr[0];
		}
		return str;
    }
    
    public String
    getANoteStatementHTMLString() {
		String str = notes.getANotes();
		if (str == null)
			return "";
		if (str.indexOf("~|~") > -1) {
			String[] arr = str.split("~|~");
			return arr[2];
		}
		return "";
    }
    
    public String
    getPNoteString() {
		String str = notes.getPNotes();
		if (str == null)
			return "";
		if (str.indexOf("~|~") > -1) {
			String[] arr = str.split("~|~");
			return arr[0];
		}
		return str;
    }
    
    public String
    getPNoteStatementHTMLString() {
		String str = notes.getPNotes();
		if (str == null)
			return "";
		if (str.indexOf("~|~") > -1) {
			String[] arr = str.split("~|~");
			return arr[2];
		}
		return "";
    }
    
    public String
    getValue()
    {
		return notes.getSoapNotesId() + "";
    }
    
    protected void
    insertObject()
		throws Exception
    {
		notes.setCreationDate(new Date());
		notes.save();
    }
    
    public void
    setAnalysisDate(Date _date)
    {
		notes.setAnalysisDate(_date);
    }

    public void
    setCreateOrModifyPerson(UKOnlinePersonBean _person)
		throws TorqueException
    {
		if (this.isNew())
			notes.setCreatePersonId(_person.getId());
		else
			notes.setModifyPersonId(_person.getId());
    }

    public void
    setPerson(PersonBean _person)
		throws TorqueException
    {
		notes.setPersonId(_person.getId());
    }

	public void
	setPracticeArea(PracticeAreaBean _practice_area)
		throws TorqueException
	{
		notes.setPracticeAreaId(_practice_area.getId());
	}
    
    public void
    setSNote(String _str)
    {
		notes.setSNotes(_str);
    }
    
    public void
    setONote(String _str)
    {
		notes.setONotes(_str);
    }
    
    public void
    setANote(String _str)
    {
		notes.setANotes(_str);
    }
    
    public void
    setPNote(String _str)
    {
		notes.setPNotes(_str);
    }
    
    protected void
    updateObject()
	throws ObjectAlreadyExistsException, IllegalValueException, Exception
    {
		notes.setModificationDate(new Date());
		notes.save();
    }
	
	private String timeInStr = "";
	private String timeOutStr = "";

	public String getTimeInStr() {
		return timeInStr;
	}

	public void setTimeInStr(String timeInStr) {
		this.timeInStr = timeInStr;
	}

	public String getTimeOutStr() {
		return timeOutStr;
	}

	public void setTimeOutStr(String timeOutStr) {
		this.timeOutStr = timeOutStr;
	}
	
	private String timeInStrAlt = "";
	private String timeOutStrAlt = "";

	public String getTimeInAltStr() {
		return timeInStrAlt;
	}

	public void setTimeInAltStr(String timeInStr) {
		this.timeInStrAlt = timeInStr;
	}

	public String getTimeOutAltStr() {
		return timeOutStrAlt;
	}

	public void setTimeOutAltStr(String timeOutStr) {
		this.timeOutStrAlt = timeOutStr;
	}
	
	
}