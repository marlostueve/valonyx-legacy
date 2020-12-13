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
ClientReviewReason
	extends CUBean
	implements java.io.Serializable
{
    // CLASS VARIABLES

    protected static HashMap<Integer,ClientReviewReason> hash = new HashMap<Integer,ClientReviewReason>(11);

    // CLASS METHODS

    public static void
    delete(int _id)
		throws TorqueException
    {
		Criteria crit = new Criteria();
		crit.add(ClientReviewReasonDbPeer.CLIENT_REVIEW_REASON_DB_ID, _id);
		ClientReviewReasonDbPeer.doDelete(crit);
    }

    public static ClientReviewReason
    getClientReviewReason(int _id)
		throws TorqueException, ObjectNotFoundException
    {
		Integer key = new Integer(_id);
		ClientReviewReason obj = (ClientReviewReason)hash.get(key);
		if (obj == null)
		{
			Criteria crit = new Criteria();
			crit.add(ClientReviewReasonDbPeer.CLIENT_REVIEW_REASON_DB_ID, _id);
			List objList = ClientReviewReasonDbPeer.doSelect(crit);
			if (objList.size() == 0)
				throw new ObjectNotFoundException("Could not locate Client Review Reason with id: " + _id);

			obj = ClientReviewReason.getClientReviewReason((ClientReviewReasonDb)objList.get(0));
		}

		return obj;
    }

    private static ClientReviewReason
    getClientReviewReason(ClientReviewReasonDb _review_reason)
		throws TorqueException
    {
		Integer key = new Integer(_review_reason.getClientReviewReasonDbId());
		ClientReviewReason obj = (ClientReviewReason)hash.get(key);
		if (obj == null)
		{
			obj = new ClientReviewReason(_review_reason);
			hash.put(key, obj);
		}

		return obj;
    }

    public static Vector
    getClientReviewReasons(PersonBean _person)
		throws TorqueException
    {
		Vector vec = new Vector();

		Criteria crit = new Criteria();
		crit.add(ClientReviewReasonDbPeer.PERSON_ID, _person.getId());
		crit.addAscendingOrderByColumn(ClientReviewReasonDbPeer.REVIEW_DATE);
		Iterator obj_itr = ClientReviewReasonDbPeer.doSelect(crit).iterator();
		while (obj_itr.hasNext())
			vec.addElement(ClientReviewReason.getClientReviewReason((ClientReviewReasonDb)obj_itr.next()));

		return vec;
    }

    public static Vector
    getClientReviewReasons(CompanyBean _company, ReviewReason _reason)
		throws TorqueException
    {
		Vector vec = new Vector();

		Criteria crit = new Criteria();
		crit.addJoin(ClientReviewReasonDbPeer.REVIEW_REASON_DB_ID, ReviewReasonDbPeer.REVIEW_REASON_DB_ID);
		crit.add(ReviewReasonDbPeer.COMPANY_ID, _company.getId());
		crit.add(ReviewReasonDbPeer.REVIEW_REASON_DB_ID, _reason.getId());
		crit.add(ClientReviewReasonDbPeer.IS_REVIEWED, (short)0);
		crit.addAscendingOrderByColumn(ClientReviewReasonDbPeer.REVIEW_DATE);
		//System.out.println("getClientReviewReasons() crit >" + crit.toString());
		Iterator obj_itr = ClientReviewReasonDbPeer.doSelect(crit).iterator();
		while (obj_itr.hasNext())
			vec.addElement(ClientReviewReason.getClientReviewReason((ClientReviewReasonDb)obj_itr.next()));
		System.out.println("num reasons >" + vec.size());

		return vec;
    }

    public static Vector
    getClientReviewReasons(CompanyBean _company, ReviewReason _reason, UKOnlinePersonBean _practitioner)
		throws TorqueException
    {
		Vector vec = new Vector();

		Criteria crit = new Criteria();
		crit.addJoin(ClientReviewReasonDbPeer.REVIEW_REASON_DB_ID, ReviewReasonDbPeer.REVIEW_REASON_DB_ID);
		crit.add(ReviewReasonDbPeer.COMPANY_ID, _company.getId());
		crit.add(ReviewReasonDbPeer.REVIEW_REASON_DB_ID, _reason.getId());
		crit.add(ClientReviewReasonDbPeer.IS_REVIEWED, (short)0);
		crit.add(ClientReviewReasonDbPeer.PRACTITIONER_ID, _practitioner.getId());
		crit.addAscendingOrderByColumn(ClientReviewReasonDbPeer.REVIEW_DATE);
		Iterator obj_itr = ClientReviewReasonDbPeer.doSelect(crit).iterator();
		while (obj_itr.hasNext())
			vec.addElement(ClientReviewReason.getClientReviewReason((ClientReviewReasonDb)obj_itr.next()));

		return vec;
    }

    public static Vector
    getClientReviewReasonsForPractitioner(PersonBean _practioner)
		throws TorqueException
    {
		Vector vec = new Vector();

		Criteria crit = new Criteria();
		crit.add(ClientReviewReasonDbPeer.PRACTITIONER_ID, _practioner.getId());
		crit.addAscendingOrderByColumn(ClientReviewReasonDbPeer.REVIEW_DATE);
		Iterator obj_itr = ClientReviewReasonDbPeer.doSelect(crit).iterator();
		while (obj_itr.hasNext())
			vec.addElement(ClientReviewReason.getClientReviewReason((ClientReviewReasonDb)obj_itr.next()));

		return vec;
    }

    public static Vector
    getPastDueClientReviewReasonsForPractitioner(PersonBean _practioner)
		throws TorqueException
    {
		Vector vec = new Vector();

		// this is including items that are in the future...

		Criteria crit = new Criteria();
		crit.addJoin(ReviewReasonDbPeer.REVIEW_REASON_DB_ID, ClientReviewReasonDbPeer.REVIEW_REASON_DB_ID);
		crit.add(ClientReviewReasonDbPeer.PRACTITIONER_ID, _practioner.getId());
		crit.add(ClientReviewReasonDbPeer.IS_REVIEWED, (short)0);
		crit.add(ClientReviewReasonDbPeer.REVIEW_DATE, new Date(), Criteria.LESS_THAN);
		crit.addAscendingOrderByColumn(ReviewReasonDbPeer.NAME);
		crit.addAscendingOrderByColumn(ClientReviewReasonDbPeer.REVIEW_DATE);
		//System.out.println("getPastDueClientReviewReasonsForPractitioner crit >" + crit.toString());
		Iterator obj_itr = ClientReviewReasonDbPeer.doSelect(crit).iterator();
		while (obj_itr.hasNext())
			vec.addElement(ClientReviewReason.getClientReviewReason((ClientReviewReasonDb)obj_itr.next()));

		return vec;
    }

    public static Vector
    getClientReviewReasons(UKOnlineCompanyBean _company, Date _start_date, Date _end_date)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException, ObjectAlreadyExistsException, Exception
    {
		if (!_start_date.before(_end_date)) {
			throw new IllegalValueException("Start date must be before end date.");
		}

		Vector vec = new Vector();
		
		Criteria crit = new Criteria();
		crit.addJoin(ReviewReasonDbPeer.REVIEW_REASON_DB_ID, ClientReviewReasonDbPeer.REVIEW_REASON_DB_ID);
		crit.add(ReviewReasonDbPeer.COMPANY_ID, _company.getId());
		crit.add(ClientReviewReasonDbPeer.REVIEW_DATE, _start_date, Criteria.GREATER_EQUAL);
		crit.and(ClientReviewReasonDbPeer.REVIEW_DATE, _end_date, Criteria.LESS_EQUAL);
		crit.add(ClientReviewReasonDbPeer.IS_REVIEWED, (short)0);
		crit.addAscendingOrderByColumn(ClientReviewReasonDbPeer.REVIEW_DATE);
		
		System.out.println("getClientReviewReasons() crit >" + crit.toString());
		Iterator itr = ClientReviewReasonDbPeer.doSelect(crit).iterator();
		while (itr.hasNext()) {
			vec.addElement(ClientReviewReason.getClientReviewReason((ClientReviewReasonDb)itr.next()));
		}
		
		//"Dr. Stueve has requested that you contact Sano for your follow-up " ++ ""; 

		return vec;
    }

    public static Vector
    searchClientReviewReasons(CompanyBean _company, String _search_str, int _limit, boolean _show_all, boolean _past_due_only)
		throws TorqueException
    {
		Vector vec = new Vector();

		Criteria crit = new Criteria();
		crit.addJoin(ReviewReasonDbPeer.REVIEW_REASON_DB_ID, ClientReviewReasonDbPeer.REVIEW_REASON_DB_ID);
		crit.add(ReviewReasonDbPeer.COMPANY_ID, _company.getId());
		
		if ( (_search_str != null) && !_search_str.isEmpty() ) {
			
			crit.addJoin(ClientReviewReasonDbPeer.PERSON_ID, PersonPeer.PERSONID);
			//crit.addJoin(ClientReviewReasonDbPeer.PRACTITIONER_ID, PersonPeer.PERSONID); // not sure how this'll turn out
			
			String search_string = "%" + _search_str + "%";
			Criteria.Criterion crit_a = crit.getNewCriterion(ReviewReasonDbPeer.NAME, (Object)search_string, Criteria.LIKE);
			Criteria.Criterion crit_b = crit.getNewCriterion(PersonPeer.FIRSTNAME, (Object)search_string, Criteria.LIKE);
			Criteria.Criterion crit_c = crit.getNewCriterion(PersonPeer.LASTNAME, (Object)search_string, Criteria.LIKE);
			crit.add(crit_a.or(crit_b).or(crit_c));
		}
		
		if (!_show_all) {
			crit.add(ClientReviewReasonDbPeer.IS_REVIEWED, (short)0); // not returning all, return those not reviewed only
		}
		if (_past_due_only) {
			crit.add(ClientReviewReasonDbPeer.REVIEW_DATE, new Date(), Criteria.LESS_EQUAL);
		}
		if (_limit > 0) {
			crit.setLimit(_limit);
		}
		
		crit.addDescendingOrderByColumn(ClientReviewReasonDbPeer.REVIEW_DATE);
		
		System.out.println("crit >" + crit.toString());
		
		Iterator itr = ClientReviewReasonDbPeer.doSelect(crit).iterator();
		while (itr.hasNext()) {
			ClientReviewReason obj = ClientReviewReason.getClientReviewReason((ClientReviewReasonDb)itr.next());
			vec.addElement(obj);
		}

		return vec;
    }

    public static Vector
    searchClientReviewReasons(UKOnlinePersonBean _logged_in_person, String _search_str, int _limit, boolean _show_only_for_logged_in_person, boolean _show_already_reviewed_items, boolean _show_past_due_items)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		Vector vec = new Vector();

		Criteria crit = new Criteria();
		crit.addJoin(ReviewReasonDbPeer.REVIEW_REASON_DB_ID, ClientReviewReasonDbPeer.REVIEW_REASON_DB_ID);
		
		if (_show_only_for_logged_in_person) {
			crit.addJoin(ClientReviewReasonDbPeer.CLIENT_REVIEW_REASON_DB_ID, ClientReviewPractitionerMappingPeer.CLIENT_REVIEW_REASON_DB_ID);
			crit.add(ClientReviewPractitionerMappingPeer.PRACTITIONER_ID, _logged_in_person.getId());
		} else {
			String companyIdString = _logged_in_person.getCompanyIdString();
			if (companyIdString.equals("0")) {
				companyIdString = "5";
			}
			crit.add(ReviewReasonDbPeer.COMPANY_ID, Integer.parseInt(companyIdString));
		}
		
		
		if ( (_search_str != null) && !_search_str.isEmpty() ) {
			
			crit.addJoin(ClientReviewReasonDbPeer.PERSON_ID, PersonPeer.PERSONID);
			//crit.addJoin(ClientReviewReasonDbPeer.PRACTITIONER_ID, PersonPeer.PERSONID); // not sure how this'll turn out - it didn't
			
			String search_string = "%" + _search_str + "%";
			Criteria.Criterion crit_a = crit.getNewCriterion(ReviewReasonDbPeer.NAME, (Object)search_string, Criteria.LIKE);
			Criteria.Criterion crit_b = crit.getNewCriterion(PersonPeer.FIRSTNAME, (Object)search_string, Criteria.LIKE);
			Criteria.Criterion crit_c = crit.getNewCriterion(PersonPeer.LASTNAME, (Object)search_string, Criteria.LIKE);
			crit.add(crit_a.or(crit_b).or(crit_c));
		}
		
		
		crit.add(ClientReviewReasonDbPeer.IS_REVIEWED, _show_already_reviewed_items ? (short)1 : (short)0);
		
		if (_show_past_due_items) {
			crit.add(ClientReviewReasonDbPeer.REVIEW_DATE, new Date(), Criteria.LESS_EQUAL);
		} else {
			crit.add(ClientReviewReasonDbPeer.REVIEW_DATE, new Date(), Criteria.GREATER_THAN);
		}
		if (_limit > 0) {
			crit.setLimit(_limit);
		}
		
		crit.addDescendingOrderByColumn(ClientReviewReasonDbPeer.REVIEW_DATE);
		
		System.out.println("crit >" + crit.toString());
		
		Iterator itr = ClientReviewReasonDbPeer.doSelect(crit).iterator();
		while (itr.hasNext()) {
			ClientReviewReason obj = ClientReviewReason.getClientReviewReason((ClientReviewReasonDb)itr.next());
			vec.addElement(obj);
		}

		return vec;
    }

    // SQL

    /*
     * <table name="REVIEW_REASON_DB" idMethod="native">
		<column name="REVIEW_REASON_DB_ID" primaryKey="true" required="true" type="INTEGER" autoIncrement="true"/>
		<column name="NAME" required="true" type="VARCHAR" size="100"/>
		<column name="COMPANY_ID" required="true" type="INTEGER"/>

		<foreign-key foreignTable="COMPANY">
			<reference local="COMPANY_ID" foreign="COMPANYID"/>
		</foreign-key>
	</table>
     */

    // SQL

    /*
     * <table name="CLIENT_REVIEW_REASON_DB" idMethod="native">
    <column name="CLIENT_REVIEW_REASON_DB_ID" primaryKey="true" required="true" type="INTEGER" autoIncrement="true"/>
    <column name="PERSON_ID" required="true" type="INTEGER"/>
	<column name="REVIEW_REASON_DB_ID" required="true" type="INTEGER"/>
    <column name="PRACTITIONER_ID" required="false" type="INTEGER"/>
    <column name="IS_REVIEWED" required="true" type="SMALLINT"/>
    <column name="REVIEW_DATE" required="true" type="DATE"/>

    <foreign-key foreignTable="PERSON">
		<reference local="PERSON_ID" foreign="PERSONID"/>
    </foreign-key>
    <foreign-key foreignTable="REVIEW_REASON_DB">
		<reference local="REVIEW_REASON_DB_ID" foreign="REVIEW_REASON_DB_ID"/>
    </foreign-key>
    <foreign-key foreignTable="PERSON">
		<reference local="PRACTITIONER_ID" foreign="PERSONID"/>
    </foreign-key>
</table>
     */

    // INSTANCE VARIABLES

    private ClientReviewReasonDb review_reason;
	private Vector practitioners = null;

    // CONSTRUCTORS

    public
    ClientReviewReason()
    {
		review_reason = new ClientReviewReasonDb();
		isNew = true;
    }

    public
    ClientReviewReason(ClientReviewReasonDb _review_reason)
    {
		review_reason = _review_reason;
		isNew = false;
    }

    // INSTANCE METHODS

    public int
    getId()
    {
		return review_reason.getClientReviewReasonDbId();
    }

    public String
    getLabel()
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		String note = this.getNoteString();
		String practitionersStr = this.getPractitionersString();
		if (!note.equals(""))
			note = " - " + note;
		if (this.isReviewed()) {
			return this.getReviewReason().getLabel() + " - " + ((!practitionersStr.isEmpty()) ? (practitionersStr + " r") : " R") + "eview on " + this.getReviewDateString() + " [REVIEWED]" + note;
		} if (this.isPastDue()) {
			return this.getReviewReason().getLabel() + " - " + ((!practitionersStr.isEmpty()) ? (practitionersStr + " r") : " R") + "eview on " + this.getReviewDateString() + " [PAST DUE]" + note;
		}
		return this.getReviewReason().getLabel() + " - " + ((!practitionersStr.isEmpty()) ? (practitionersStr + " r") : " R") + "eview on/by " + this.getReviewDateString() + note;
    }

    public String
    getLabelWithoutReason()
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		String note = this.getNoteString();
		String practitionersStr = this.getPractitionersString();
		if (!note.equals(""))
			note = " - " + note;
		if (this.isReviewed()) {
			return this.getPerson().getLabel() + " - " + ((!practitionersStr.isEmpty()) ? (practitionersStr + " r") : " R") + "eview on " + this.getReviewDateString() + " [REVIEWED]" + note;
		} if (this.isPastDue()) {
			return this.getPerson().getLabel() + " - " + ((!practitionersStr.isEmpty()) ? (practitionersStr + " r") : " R") + "eview on " + this.getReviewDateString() + " [PAST DUE]" + note;
		}
		
		return this.getPerson().getLabel() + " - " + ((!practitionersStr.isEmpty()) ? (practitionersStr + " r") : " R") + "eview on " + this.getReviewDateString() + note;
    }
	
	public String getPractitionersString() throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException {
		StringBuilder b = new StringBuilder();
		boolean needs_comma = false;
		Iterator itr = this.getPractitioners().iterator();
		while (itr.hasNext()) {
			UKOnlinePersonBean practitioner = (UKOnlinePersonBean)itr.next();
			if (needs_comma) { b.append(", "); }
			b.append(practitioner.getFirstNameString());
			needs_comma = true;
		}
		return b.toString();
	}

	public String
	getNoteString()
	{
		String str = review_reason.getNote();
		return str == null ? "" : str;
	}

    public UKOnlinePersonBean
    getPerson()
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		return (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(review_reason.getPersonId());
    }

	/*
    public UKOnlinePersonBean
    getPractitioner()
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		return (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(review_reason.getPractitionerId());
    }
	*/

    public Date
    getReviewDate() {
		return review_reason.getReviewDate();
    }

    public String
    getReviewDateString()
    {
		Date date = review_reason.getReviewDate();
		if (date == null)
			return "";
		return CUBean.getUserDateString(date);
    }

	public ReviewReason
	getReviewReason()
		throws TorqueException, ObjectNotFoundException
	{
		return ReviewReason.getReviewReason(review_reason.getReviewReasonDbId());
	}

    public String
    getValue()
    {
		return review_reason.getClientReviewReasonDbId() + "";
    }

	@Override
    protected void
    insertObject()
		throws Exception
    {
		review_reason.setCreationDate(new Date());
		review_reason.save();
		
		this.savePractitioners();
    }

	public boolean
	isPastDue() {
		if (this.isReviewed()) {
			return false;
		}
		Date now = new Date();
		return (now.after(review_reason.getReviewDate()));
	}

	public boolean
	isReviewed()
	{
		return (review_reason.getIsReviewed() == (short)1);
	}

	public void
	setNote(String _note)
	{
		review_reason.setNote(_note);
	}

    public void
    setPerson(UKOnlinePersonBean _person)
		throws TorqueException
    {
		review_reason.setPersonId(_person.getId());
    }

	/*
    public void
    setPractitioner(UKOnlinePersonBean _person)
		throws TorqueException
    {
		review_reason.setPractitionerId(_person.getId());
    }
	*/
	
	/*
<table name="CLIENT_REVIEW_PRACTITIONER_MAPPING">
	<column name="CLIENT_REVIEW_REASON_DB_ID" primaryKey="true" required="true" type="INTEGER"/>
    <column name="PRACTITIONER_ID" primaryKey="true" required="true" type="INTEGER"/>

    <foreign-key foreignTable="CLIENT_REVIEW_REASON_DB">
		<reference local="CLIENT_REVIEW_REASON_DB_ID" foreign="CLIENT_REVIEW_REASON_DB_ID"/>
    </foreign-key>
	<foreign-key foreignTable="PERSON">
		<reference local="PRACTITIONER_ID" foreign="PERSONID"/>
	</foreign-key>
</table>
	*/

	public Vector
	getPractitioners() throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException {
		if (practitioners == null) {
			practitioners = new Vector();
			Criteria crit = new Criteria();
			crit.add(ClientReviewPractitionerMappingPeer.CLIENT_REVIEW_REASON_DB_ID, this.getId());
			//crit.addAscendingOrderByColumn(ClientReviewPractitionerMappingPeer.EXCERCISE_NUMBER);
			Iterator itr = ClientReviewPractitionerMappingPeer.doSelect(crit).iterator();
			while (itr.hasNext()) {
				ClientReviewPractitionerMapping obj = (ClientReviewPractitionerMapping)itr.next();
				practitioners.addElement(UKOnlinePersonBean.getPerson(obj.getPractitionerId()));
			}
		}
		return practitioners;
	}

	public void
	setPractitioners(Vector _practitioners) {
		practitioners = _practitioners;
	}

	private void
    savePractitioners() throws TorqueException, Exception {
		if (this.practitioners != null) {
			System.out.println("savePractitioners sizer >" + this.practitioners.size());

			HashMap<Integer,ClientReviewPractitionerMapping> db_pract_hash = new HashMap<Integer,ClientReviewPractitionerMapping>(3);
			Criteria crit = new Criteria();
			crit.add(ClientReviewPractitionerMappingPeer.CLIENT_REVIEW_REASON_DB_ID, this.getId());
			Iterator itr = ClientReviewPractitionerMappingPeer.doSelect(crit).iterator();
			while (itr.hasNext()) {
				System.out.println("found existing ass...");
				ClientReviewPractitionerMapping value = (ClientReviewPractitionerMapping)itr.next();
				Integer key = value.getPractitionerId();
				db_pract_hash.put(key, value);
			}

			System.out.println("num practs >" + this.practitioners.size());
			itr = this.practitioners.iterator();
			while (itr.hasNext()) {
				UKOnlinePersonBean practitioner = (UKOnlinePersonBean)itr.next();
				ClientReviewPractitionerMapping obj = (ClientReviewPractitionerMapping)db_pract_hash.remove(practitioner.getId());
				if (obj == null) { // mapping not already in database // 4/12/18 - or is it...
					/* // shrug
					org.apache.torque.TorqueException: java.sql.SQLException: Duplicate key or integrity constraint violation,  message from server: "Duplicate entry '491-4410' for key 'PRIMARY'"
	at org.apache.torque.util.BasePeer.throwTorqueException(BasePeer.java:103)
	at org.apache.torque.util.BasePeer.insertOrUpdateRecord(BasePeer.java:626)
	at org.apache.torque.util.BasePeer.doInsert(BasePeer.java:516)
	at com.badiyan.torque.BaseClientReviewPractitionerMappingPeer.doInsert(BaseClientReviewPractitionerMappingPeer.java:210)
	at com.badiyan.torque.BaseClientReviewPractitionerMappingPeer.doInsert(BaseClientReviewPractitionerMappingPeer.java:557)
	at com.badiyan.torque.BaseClientReviewPractitionerMapping.save(BaseClientReviewPractitionerMapping.java:451)
	at com.badiyan.torque.BaseClientReviewPractitionerMapping.save(BaseClientReviewPractitionerMapping.java:415)
	at com.badiyan.torque.BaseClientReviewPractitionerMapping.save(BaseClientReviewPractitionerMapping.java:395)
	at com.badiyan.uk.online.beans.ClientReviewReason.savePractitioners(ClientReviewReason.java:578)
	at com.badiyan.uk.online.beans.ClientReviewReason.insertObject(ClientReviewReason.java:478)
					*/
					obj = new ClientReviewPractitionerMapping();
					obj.setClientReviewReasonDbId(this.getId());
					obj.setPractitionerId(practitioner.getId());
					obj.save();
				}
			}

			itr = db_pract_hash.keySet().iterator();
			while (itr.hasNext()) {
				Integer key = (Integer)itr.next();
				ClientReviewPractitionerMapping obj = (ClientReviewPractitionerMapping)db_pract_hash.get(key);
				ClientReviewPractitionerMappingPeer.doDelete(obj);
			}
		}
    }

	public void
	setReviewed(boolean _reviewed)
	{
		review_reason.setIsReviewed(_reviewed ? (short)1 : (short)0);
	}

    public void
    setReviewDate(Date _date)
    {
		review_reason.setReviewDate(_date);
    }

	public void
	setReviewReason(ReviewReason _review_reason)
		throws TorqueException
	{
		review_reason.setReviewReasonDbId(_review_reason.getId());
	}

    public void
    setCreateOrModifyPerson(UKOnlinePersonBean _person)
		throws TorqueException
    {
		if (this.isNew()) {
			review_reason.setCreatePersonId(_person.getId());
		} else {
			review_reason.setModifyPersonId(_person.getId());
		}
    }
	
	public boolean
	emailClient() {
		return (review_reason.getEmailClient() == (short)1);
	}
	
	public void
	setEmailClient(boolean _b) {
		review_reason.setEmailClient(_b ? (short)1 : (short)0);
	}

	@Override
    protected void
    updateObject()
		throws ObjectAlreadyExistsException, IllegalValueException, Exception
    {
		review_reason.setModificationDate(new Date());
		review_reason.save();
		
		this.savePractitioners();
    }
	
	public CompanyBean
	getCompany() throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException {
		return this.getPerson().getDepartment().getCompany();
	}
	
	public String
	getPractitionerEmailString() throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException {
		
		AddressBean address = this.getCompany().getAddress(AddressBean.PRACTICE_ADDRESS_TYPE);
		String appt_date_str = CUBean.getUserDateString(this.getReviewDate(), "EEEE, MMMM d, yyyy");
		
		/*
		String review_str = "Dr. Stueve has requested that you contact Sano for your follow-up.";
		if (this.getReviewReason().getLabel().equals("O2")) {
			review_str = "This is a reminder to schedule your O2 therapy to expedite your healing.";
		} else if (this.getReviewReason().getLabel().equals("Sauna")) {
			review_str = "This is a reminder to schedule your sauna therapy to expedite your healing.";
		}
		*/
		
		String review_str = this.getPerson().getLabel() + " - " + this.getLabel();
		
		StringBuffer buf = new StringBuffer();
		buf.append("<div>");
		buf.append("  <table cellspacing=\"0\" cellpadding=\"0\" style=\"border:1px #acacac solid;width:615px\" align=\"center\">");
		buf.append("    <tbody><tr>");
		buf.append("      <td bgcolor=\"#11100e\" height=\"89\" valign=\"top\">");
		buf.append("        <table width=\"613\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" style=\"width:613px\">");
		buf.append("          <tbody><tr>");
		buf.append("            <td valign=\"middle\" height=\"89\" style=\"font-family:Arial,Helvetica,sans-serif;font-size:24px;color:#8e8d8d;padding-left:30px;overflow:hidden;word-wrap:break-word;width:350px\">" + this.getCompany().getLabel());
		buf.append("            <br><span style=\"font-size:20px\">Appointment Reminder</span></td>");
		if (this.getCompany().getId() == 5)
			buf.append("            <td valign=\"top\" width=\"233\" align=\"left\"><img src=\"http://3.bp.blogspot.com/-F3clsArp_q4/TshlXxkxJ_I/AAAAAAAAAAg/K_kSbulF0gw/s1600/Sano%2Blogo-small.jpg\"  /></td>");
		else
			buf.append("            <td valign=\"top\" width=\"233\" align=\"left\"></td>");
		buf.append("          </tr>");
		buf.append("        </tbody></table>");
		buf.append("      </td>");
		buf.append("    </tr>");
		buf.append("    <tr>");
		buf.append("      <td>");
		buf.append("        <table width=\"613\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
		buf.append("          <tbody><tr>");
		buf.append("                  <td style=\"font-family:Arial,Helvetica,sans-serif;font-size:14px;color:#6e6e6e;width:561px;overflow:auto;word-wrap:break-word;padding:14px 26px 14px 26px\">" + this.getCompany().getLabel());
		buf.append("                  <br>" + address.getStreet1String() + " <br>" + address.getStreet2String() );
		buf.append("                  <br>" + address.getCityString() + ", " + address.getStateString() + " " + address.getZipCodeString());
		buf.append("                  <br><a href=\"tel:952-681-2916\" value=\"+19526812916\" target=\"_blank\">952-681-2916</a></td>");
		buf.append("                </tr>");
		buf.append("              </tbody></table>");
		buf.append("            </td>");
		buf.append("          </tr>");
		buf.append("          <tr>");
		buf.append("            <td style=\"padding-left:26px\">");
		buf.append("              <table border=\"0\" cellspacing=\"0\" cellpadding=\"0\" style=\"border:1px solid #acacac;width:559px\">");
		buf.append("                <tbody><tr>");
		buf.append("                  <td bgcolor=\"#e1eeef\" style=\"padding:13px 20px 13px 20px;font-family:Arial,Helvetica,sans-serif;color:#317679;font-size:18px;font-weight:bold;line-height:24px;overflow:auto;word-wrap:break-word;width:240px\">");
		buf.append("                  Dear " + this.getPerson().getFirstNameString() + ",");
		buf.append("                  <br>" + review_str + "</td>");
		buf.append("                  <td style=\"color:#6e6e6e;font-family:Arial,Helvetica,sans-serif;font-size:14px;padding-left:51px;padding-right:10px;line-height:22px;overflow:auto;word-wrap:break-word;width:218px\">" +  this.getReviewReason().getLabel() );
		//buf.append("                  <br>" + this.getPractitioner().getLabel());
		buf.append("                  <br>" + appt_date_str);
		//buf.append("                  <br>" + CUBean.getUserTimeString(this.getReviewDate()));
		buf.append("                  <br></td>");
		buf.append("                </tr>");
		buf.append("              </tbody></table>");
		buf.append("            </td>");
		buf.append("          </tr>");
		buf.append("        <tr>");
		buf.append("            <td style=\"padding:12px 26px 16px 26px\">");
		buf.append("              <table width=\"561\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" style=\"width:561px\">");
		buf.append("                <tbody><tr>");
		buf.append("                  <td style=\"font-family:Arial,Helvetica,sans-serif;font-size:14px;color:#6e6e6e;width:561px;overflow:auto;word-wrap:break-word\">");
		buf.append("If you have any questions, please contact " + this.getCompany().getLabel() + " at <a href=\"tel:952-681-2916\" value=\"+19526812916\" target=\"_blank\">952-681-2916</a> . ");
		buf.append("                  <br>&nbsp;");
		buf.append("                </tr>");
		buf.append("              </tbody></table>");
		buf.append("            </td>");
		buf.append("          </tr>");
		buf.append("          <tr bgcolor=\"#f2f2f2\">");
		buf.append("            <td style=\"border-top:1px solid #acacac;padding:26px 0px 14px 26px\">");
		buf.append("              <table width=\"587\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" style=\"width:587px\">");
		buf.append("                <tbody><tr>");
		buf.append("                  <td style=\"font-family:Arial,Helvetica,sans-serif;font-size:12px;color:#6e6e6e;width:420px;overflow:auto;word-wrap:break-word\">This e-mail was sent on behalf of  ");
		buf.append("                  <a href=\"http://www.sanowc.com\" style=\"color:#317679!important\" target=\"_blank\">" + this.getCompany().getLabel() + "</a>");
		buf.append("                  <br>by <a href=\"http://www.valonyx.com\">valonyx.com</a>. This is an automated email; please do not reply. ");
		buf.append("                  <br>&nbsp;");
		buf.append("                  <br>");
		buf.append("                  ");
		buf.append("                  <br></td>");
		buf.append("                  <td width=\"167\" valign=\"bottom\">");
		buf.append("                    <a href=\"http://www.valonyx.com\"><img src=\"http://www.valonyx.com/images/valonyx-logo-grey-198.png\" /></a>");
		buf.append("                  </td>");
		buf.append("                </tr>");
		buf.append("              </tbody></table>");
		buf.append("            </td>");
		buf.append("          </tr>");
		buf.append("        </tbody></table>");
		buf.append("</div>");
		
		return buf.toString();
	}
	
	public String
	getEmailString() throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException {
		
		AddressBean address = this.getCompany().getAddress(AddressBean.PRACTICE_ADDRESS_TYPE);
		String appt_date_str = CUBean.getUserDateString(this.getReviewDate(), "EEEE, MMMM d, yyyy");
		
		String review_str = "Dr. Stueve has requested that you contact Sano for your follow-up.";
		if (this.getReviewReason().getLabel().equals("O2")) {
			review_str = "This is a reminder to schedule your O2 therapy to expedite your healing.";
		} else if (this.getReviewReason().getLabel().equals("Sauna")) {
			review_str = "This is a reminder to schedule your sauna therapy to expedite your healing.";
		}
		
		StringBuffer buf = new StringBuffer();
		buf.append("<div>");
		buf.append("  <table cellspacing=\"0\" cellpadding=\"0\" style=\"border:1px #acacac solid;width:615px\" align=\"center\">");
		buf.append("    <tbody><tr>");
		buf.append("      <td bgcolor=\"#11100e\" height=\"89\" valign=\"top\">");
		buf.append("        <table width=\"613\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" style=\"width:613px\">");
		buf.append("          <tbody><tr>");
		buf.append("            <td valign=\"middle\" height=\"89\" style=\"font-family:Arial,Helvetica,sans-serif;font-size:24px;color:#8e8d8d;padding-left:30px;overflow:hidden;word-wrap:break-word;width:350px\">" + this.getCompany().getLabel());
		buf.append("            <br><span style=\"font-size:20px\">Appointment Reminder</span></td>");
		if (this.getCompany().getId() == 5)
			buf.append("            <td valign=\"top\" width=\"233\" align=\"left\"><img src=\"http://3.bp.blogspot.com/-F3clsArp_q4/TshlXxkxJ_I/AAAAAAAAAAg/K_kSbulF0gw/s1600/Sano%2Blogo-small.jpg\"  /></td>");
		else
			buf.append("            <td valign=\"top\" width=\"233\" align=\"left\"></td>");
		buf.append("          </tr>");
		buf.append("        </tbody></table>");
		buf.append("      </td>");
		buf.append("    </tr>");
		buf.append("    <tr>");
		buf.append("      <td>");
		buf.append("        <table width=\"613\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
		buf.append("          <tbody><tr>");
		buf.append("                  <td style=\"font-family:Arial,Helvetica,sans-serif;font-size:14px;color:#6e6e6e;width:561px;overflow:auto;word-wrap:break-word;padding:14px 26px 14px 26px\">" + this.getCompany().getLabel());
		buf.append("                  <br>" + address.getStreet1String() + " <br>" + address.getStreet2String() );
		buf.append("                  <br>" + address.getCityString() + ", " + address.getStateString() + " " + address.getZipCodeString());
		buf.append("                  <br><a href=\"tel:952-681-2916\" value=\"+19526812916\" target=\"_blank\">952-681-2916</a></td>");
		buf.append("                </tr>");
		buf.append("              </tbody></table>");
		buf.append("            </td>");
		buf.append("          </tr>");
		buf.append("          <tr>");
		buf.append("            <td style=\"padding-left:26px\">");
		buf.append("              <table border=\"0\" cellspacing=\"0\" cellpadding=\"0\" style=\"border:1px solid #acacac;width:559px\">");
		buf.append("                <tbody><tr>");
		buf.append("                  <td bgcolor=\"#e1eeef\" style=\"padding:13px 20px 13px 20px;font-family:Arial,Helvetica,sans-serif;color:#317679;font-size:18px;font-weight:bold;line-height:24px;overflow:auto;word-wrap:break-word;width:240px\">");
		buf.append("                  Dear " + this.getPerson().getFirstNameString() + ",");
		buf.append("                  <br>" + review_str + "</td>");
		buf.append("                  <td style=\"color:#6e6e6e;font-family:Arial,Helvetica,sans-serif;font-size:14px;padding-left:51px;padding-right:10px;line-height:22px;overflow:auto;word-wrap:break-word;width:218px\">" +  this.getReviewReason().getLabel() );
		//buf.append("                  <br>" + this.getPractitioner().getLabel());
		buf.append("                  <br>" + appt_date_str);
		//buf.append("                  <br>" + CUBean.getUserTimeString(this.getReviewDate()));
		buf.append("                  <br></td>");
		buf.append("                </tr>");
		buf.append("              </tbody></table>");
		buf.append("            </td>");
		buf.append("          </tr>");
		buf.append("        <tr>");
		buf.append("            <td style=\"padding:12px 26px 16px 26px\">");
		buf.append("              <table width=\"561\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" style=\"width:561px\">");
		buf.append("                <tbody><tr>");
		buf.append("                  <td style=\"font-family:Arial,Helvetica,sans-serif;font-size:14px;color:#6e6e6e;width:561px;overflow:auto;word-wrap:break-word\">");
		buf.append("If you have any questions, please contact " + this.getCompany().getLabel() + " at <a href=\"tel:952-681-2916\" value=\"+19526812916\" target=\"_blank\">952-681-2916</a> . ");
		buf.append("                  <br>&nbsp;");
		buf.append("                </tr>");
		buf.append("              </tbody></table>");
		buf.append("            </td>");
		buf.append("          </tr>");
		buf.append("          <tr bgcolor=\"#f2f2f2\">");
		buf.append("            <td style=\"border-top:1px solid #acacac;padding:26px 0px 14px 26px\">");
		buf.append("              <table width=\"587\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" style=\"width:587px\">");
		buf.append("                <tbody><tr>");
		buf.append("                  <td style=\"font-family:Arial,Helvetica,sans-serif;font-size:12px;color:#6e6e6e;width:420px;overflow:auto;word-wrap:break-word\">This e-mail was sent on behalf of  ");
		buf.append("                  <a href=\"http://www.sanowc.com\" style=\"color:#317679!important\" target=\"_blank\">" + this.getCompany().getLabel() + "</a>");
		buf.append("                  <br>by <a href=\"http://www.valonyx.com\">valonyx.com</a>. This is an automated email; please do not reply. ");
		buf.append("                  <br>&nbsp;");
		buf.append("                  <br>");
		buf.append("                  ");
		buf.append("                  <br></td>");
		buf.append("                  <td width=\"167\" valign=\"bottom\">");
		buf.append("                    <a href=\"http://www.valonyx.com\"><img src=\"http://www.valonyx.com/images/valonyx-logo-grey-198.png\" /></a>");
		buf.append("                  </td>");
		buf.append("                </tr>");
		buf.append("              </tbody></table>");
		buf.append("            </td>");
		buf.append("          </tr>");
		buf.append("        </tbody></table>");
		buf.append("</div>");
		
		return buf.toString();
	}
}