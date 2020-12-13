/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.valeo.qb.data;

import java.math.BigDecimal;

import java.util.*;

import com.badiyan.torque.*;
import com.badiyan.uk.exceptions.*;

import com.badiyan.uk.beans.*;
import com.badiyan.uk.online.beans.*;

import org.apache.torque.*;
import org.apache.torque.util.Criteria;

/**
 *
 * @author marlo
 */
public class
CreditMemoRet
	extends CUBean
	implements java.io.Serializable
{
	// CLASS VARIABLES

    protected static HashMap<Integer,CreditMemoRet> hash = new HashMap<Integer,CreditMemoRet>(11);
	private static HashMap<UKOnlineCompanyBean,Date> last_update_hash = new HashMap<UKOnlineCompanyBean,Date>(11);

	// CLASS METHODS

	public static Date
	getLastUpdateDate(UKOnlineCompanyBean _company)
		throws TorqueException
	{
		Date last_update = CreditMemoRet.last_update_hash.get(_company);
		if (last_update == null)
		{
			Criteria crit = new Criteria();
			crit.add(CreditMemoRetDbPeer.COMPANY_ID, _company.getId());
			crit.addDescendingOrderByColumn(CreditMemoRetDbPeer.TIME_MODIFIED);
			List objList = CreditMemoRetDbPeer.doSelect(crit);
			if (objList.size() > 0)
			{
				CreditMemoRetDb account_obj = (CreditMemoRetDb)objList.get(0);
				last_update = account_obj.getTimeModified();

				CreditMemoRet.last_update_hash.put(_company, last_update);
			}
		}

		return last_update;
	}

    public static CreditMemoRet
    getCreditMemo(int _id)
		throws TorqueException, ObjectNotFoundException
    {
		Integer key = new Integer(_id);
		CreditMemoRet obj = (CreditMemoRet)hash.get(key);
		if (obj == null)
		{
			Criteria crit = new Criteria();
			crit.add(CreditMemoRetDbPeer.CREDIT_MEMO_RET_DB_ID, _id);
			List objList = CreditMemoRetDbPeer.doSelect(crit);
			if (objList.size() == 0)
				throw new ObjectNotFoundException("Could not locate Credit Memo with id: " + _id);

			obj = CreditMemoRet.getCreditMemo((CreditMemoRetDb)objList.get(0));
		}

		return obj;
    }

    private static CreditMemoRet
    getCreditMemo(CreditMemoRetDb _obj)
		throws TorqueException
    {
		Integer key = new Integer(_obj.getCreditMemoRetDbId());
		CreditMemoRet obj = (CreditMemoRet)hash.get(key);
		if (obj == null)
		{
			obj = new CreditMemoRet(_obj);
			hash.put(key, obj);
		}

		return obj;
    }

    public static Vector
    getCreditMemos(CompanyBean _company)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		Vector vec = new Vector();
		Criteria crit = new Criteria();
		crit.add(CreditMemoRetDbPeer.COMPANY_ID, _company.getId());
		Iterator obj_itr = CreditMemoRetDbPeer.doSelect(crit).iterator();
		while (obj_itr.hasNext())
			vec.addElement(CreditMemoRet.getCreditMemo((CreditMemoRetDb)obj_itr.next()));

		return vec;
    }

	// INSTANCE VARIABLES

	private CreditMemoRetDb credit_memo;

	// CONSTRUCTORS

	public
	CreditMemoRet()
	{
		credit_memo = new CreditMemoRetDb();
		isNew = true;
	}

	public
	CreditMemoRet(CreditMemoRetDb _credit_memo)
	{
		credit_memo = _credit_memo;
		isNew = false;
	}

	// INSTANCE METHODS

	public float
	getTotalAmount()
	{
		return credit_memo.getTotalAmount().floatValue();
	}

	public UKOnlineCompanyBean
	getCompany()
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		return (UKOnlineCompanyBean)UKOnlineCompanyBean.getCompany(credit_memo.getCompanyId());
	}

	public String
	getMemoString()
	{
		String str = credit_memo.getMemo();
		if (str == null)
			return "";
		return str;
	}

	public String
	getEditSequence()
	{
		return credit_memo.getEditSequence();
	}

	public int
	getId()
	{
		return credit_memo.getCreditMemoRetDbId();
	}

	public String
	getLabel()
	{
		String str = credit_memo.getTxnID();
		if (str == null)
			return "";
		return str;
	}

	public Date
	getTimeCreated()
	{
		return credit_memo.getTimeCreated();
	}

	public Date
	getTimeModified()
	{
		return credit_memo.getTimeModified();
	}

	public String
	getValue()
	{
		return credit_memo.getCreditMemoRetDbId() + "";
	}

    protected void
    insertObject()
		throws Exception
    {
		// see if this credit memo conflicts with any others...

		Criteria crit = new Criteria();
		crit.add(CreditMemoRetDbPeer.COMPANY_ID, credit_memo.getCompanyId());
		crit.or(CreditMemoRetDbPeer.TXN_I_D, credit_memo.getTxnID());
		List obj_list = CreditMemoRetDbPeer.doSelect(crit);
		if (obj_list.size() > 0)
			throw new ObjectAlreadyExistsException("Matching Credit Memo exists.");

		// ensure that company id is set...

		if (credit_memo.getCompanyId() == 0)
			throw new IllegalValueException("Company not set for " + this.getLabel());

		credit_memo.save();

		this.updateLastUpdateTime();
    }

	public void
	setCompany(UKOnlineCompanyBean _company)
		throws TorqueException
	{
		credit_memo.setCompanyId(_company.getId());
	}

	public void
	setMemo(String _desc)
	{
		credit_memo.setMemo(_desc);
	}

	public void
	setEditSequence(String _edit_sequence)
	{
		credit_memo.setEditSequence(_edit_sequence);
	}

	public void
	setIsPending(boolean _pending)
	{
		credit_memo.setIsPending(_pending ? "true" : "false");
	}

	public void
	setTimeCreated(Date _timeCreated)
	{
		credit_memo.setTimeCreated(_timeCreated);
	}

	public void
	setTimeModified(Date _timeModified)
	{
		credit_memo.setTimeModified(_timeModified);
	}

	public void
	setTotalAmount(float _total)
	{
		credit_memo.setTotalAmount(new BigDecimal(_total));
	}

    protected void
    updateObject()
		throws ObjectAlreadyExistsException, IllegalValueException, Exception
    {
		credit_memo.save();

		this.updateLastUpdateTime();
    }

	private void
	updateLastUpdateTime()
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		Date timeModified = this.getTimeModified();
		UKOnlineCompanyBean company = (UKOnlineCompanyBean)UKOnlineCompanyBean.getCompany(credit_memo.getCompanyId());
		Date last_update = CreditMemoRet.last_update_hash.get(company);
		if ((last_update == null) || timeModified.after(last_update))
		{
			Calendar mod_time = Calendar.getInstance();
			mod_time.setTime(timeModified);
			last_update = mod_time.getTime();
			CreditMemoRet.last_update_hash.put(company, last_update);
		}
	}
}
