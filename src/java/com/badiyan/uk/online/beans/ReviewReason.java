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
ReviewReason
	extends CUBean
	implements java.io.Serializable
{
    // CLASS VARIABLES

    protected static HashMap<Integer,ReviewReason> hash = new HashMap<Integer,ReviewReason>(11);
    protected static HashMap<String,Vector> company_hash = new HashMap<String,Vector>(11);
	
	public static final short SERVICES_TYPE = 1;
	public static final short PRODUCT_TYPE = 2;
	public static final short TX_THERAPY_TYPE = 3;

    // CLASS METHODS

    public static void
    delete(int _id)
		throws TorqueException
    {
		Criteria crit = new Criteria();
		crit.add(ReviewReasonDbPeer.REVIEW_REASON_DB_ID, _id);
		ReviewReasonDbPeer.doDelete(crit);
    }

    public static ReviewReason
    getReviewReason(int _id)
		throws TorqueException, ObjectNotFoundException
    {
		Integer key = new Integer(_id);
		ReviewReason obj = (ReviewReason)hash.get(key);
		if (obj == null)
		{
			Criteria crit = new Criteria();
			crit.add(ReviewReasonDbPeer.REVIEW_REASON_DB_ID, _id);
			List objList = ReviewReasonDbPeer.doSelect(crit);
			if (objList.size() == 0)
				throw new ObjectNotFoundException("Could not locate Review Reason with id: " + _id);

			obj = ReviewReason.getReviewReason((ReviewReasonDb)objList.get(0));
		}

		return obj;
    }

    private static ReviewReason
    getReviewReason(ReviewReasonDb _review_reason)
		throws TorqueException
    {
		Integer key = new Integer(_review_reason.getReviewReasonDbId());
		ReviewReason obj = (ReviewReason)hash.get(key);
		if (obj == null)
		{
			obj = new ReviewReason(_review_reason);
			hash.put(key, obj);
		}

		return obj;
    }

    public static Vector
    getReviewReasons(CompanyBean _company)
		throws TorqueException
    {
		Vector vec = (Vector)company_hash.get(_company.getValue());
		if (vec == null) {
			vec = new Vector();
			company_hash.put(_company.getValue(), vec);

			Criteria crit = new Criteria();
			crit.add(ReviewReasonDbPeer.COMPANY_ID, _company.getId());
			crit.addAscendingOrderByColumn(ReviewReasonDbPeer.NAME);
			Iterator obj_itr = ReviewReasonDbPeer.doSelect(crit).iterator();
			while (obj_itr.hasNext()) {
				vec.addElement(ReviewReason.getReviewReason((ReviewReasonDb)obj_itr.next()));
			}
		}

		return vec;
    }

    public static Vector
    getReviewReasons(CompanyBean _company, short _type)
		throws TorqueException
    {
		String key = _company.getValue() + "-" + _type;
		Vector vec = (Vector)company_hash.get(key);
		if (vec == null) {
			vec = new Vector();
			company_hash.put(key, vec);

			Criteria crit = new Criteria();
			crit.add(ReviewReasonDbPeer.COMPANY_ID, _company.getId());
			crit.add(ReviewReasonDbPeer.TYPE, _type);
			crit.addAscendingOrderByColumn(ReviewReasonDbPeer.NAME);
			Iterator obj_itr = ReviewReasonDbPeer.doSelect(crit).iterator();
			while (obj_itr.hasNext()) {
				vec.addElement(ReviewReason.getReviewReason((ReviewReasonDb)obj_itr.next()));
			}
		}

		return vec;
    }
	
	public static ReviewReason
	maintainReason(CompanyBean _company, String _reason_str) throws TorqueException, IllegalValueException, ObjectAlreadyExistsException, Exception {
		ReviewReason reason_obj = null;
		Criteria crit = new Criteria();
		crit.add(ReviewReasonDbPeer.NAME, _reason_str);
		List objList = ReviewReasonDbPeer.doSelect(crit);
		if (objList.isEmpty()) {
			reason_obj = new ReviewReason();
			reason_obj.setCompany(_company);
			reason_obj.setName(_reason_str);
			reason_obj.save();
		} else {
			reason_obj = ReviewReason.getReviewReason((ReviewReasonDb)objList.get(0));
		}
		
		return reason_obj;
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

    // INSTANCE VARIABLES

    private ReviewReasonDb review_reason;

    // CONSTRUCTORS

    public
    ReviewReason()
    {
		review_reason = new ReviewReasonDb();
		isNew = true;
    }

    public
    ReviewReason(ReviewReasonDb _review_reason)
    {
		review_reason = _review_reason;
		isNew = false;
    }

    // INSTANCE METHODS

    public int
    getId()
    {
		return review_reason.getReviewReasonDbId();
    }

    public String
    getLabel()
    {
		String str = review_reason.getName();
		if (str == null)
			return "";
		return str;
    }

    public String
    getValue()
    {
		return review_reason.getReviewReasonDbId() + "";
    }

	@Override
    protected void
    insertObject()
		throws Exception
    {
		review_reason.save();
		ReviewReason.company_hash.clear();
    }

    public void
    setCompany(CompanyBean _company)
		throws TorqueException
    {
		review_reason.setCompanyId(_company.getId());
    }

    public void
    setName(String _name)
		throws TorqueException
    {
		review_reason.setName(_name);
    }
	
	public short
	getType() {
		return review_reason.getType();
	}
	
	public void
	setType(short _type) {
		review_reason.setType(_type);
	}

	@Override
    protected void
    updateObject()
		throws ObjectAlreadyExistsException, IllegalValueException, Exception
    {
		review_reason.save();
    }
}