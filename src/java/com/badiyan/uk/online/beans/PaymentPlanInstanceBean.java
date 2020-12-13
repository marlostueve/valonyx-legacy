/*
 * PaymentPlanInstanceBean.java - 2/13/08
 */

package com.badiyan.uk.online.beans;

import com.badiyan.uk.beans.*;

import com.badiyan.torque.*;
import com.badiyan.uk.exceptions.*;

import java.beans.*;
import java.util.*;
import java.math.*;

import org.apache.torque.*;
import org.apache.torque.util.Criteria;

import com.badiyan.uk.tasks.*;

/**
 *
 * @author marlo
 */
public class
PaymentPlanInstanceBean
	extends CUBean
	implements java.io.Serializable
{
    // CLASS VARIABLES

    protected static HashMap<Integer,PaymentPlanInstanceBean> hash = new HashMap<Integer,PaymentPlanInstanceBean>(11);

    // CLASS METHODS
	
    public static void
    delete(int _id)
		throws TorqueException
    {
		Criteria crit = new Criteria();
		crit.add(PaymentPlanInstancePeer.PAYMENT_PLAN_INSTANCE_ID, _id);
		PaymentPlanInstancePeer.doDelete(crit);

		Integer key = new Integer(_id);
		hash.remove(key);
    }
	
	public static PaymentPlanInstanceBean
	getPaymentPlanInstance(int _id)
		throws TorqueException, ObjectNotFoundException
	{
		Integer key = new Integer(_id);
		PaymentPlanInstanceBean plan_instance = (PaymentPlanInstanceBean)hash.get(key);
		if (plan_instance == null)
		{
			Criteria crit = new Criteria();
			crit.add(PaymentPlanInstancePeer.PAYMENT_PLAN_INSTANCE_ID, _id);
			List objList = PaymentPlanInstancePeer.doSelect(crit);
			if (objList.size() == 0)
				throw new ObjectNotFoundException("Could not locate plan instance with id: " + _id);

			plan_instance = PaymentPlanInstanceBean.getPaymentPlanInstance((PaymentPlanInstance)objList.get(0));
		}

		return plan_instance;
	}

    private static PaymentPlanInstanceBean
    getPaymentPlanInstance(PaymentPlanInstance _plan_instance)
		throws TorqueException
    {
		Integer key = new Integer(_plan_instance.getPaymentPlanInstanceId());
		PaymentPlanInstanceBean plan_instance = (PaymentPlanInstanceBean)hash.get(key);
		if (plan_instance == null)
		{
			plan_instance = new PaymentPlanInstanceBean(_plan_instance);
			hash.put(key, plan_instance);
		}

		return plan_instance;
    }

	public static PaymentPlanInstanceBean
	getPaymentPlanInstance(UKOnlinePersonBean _person, PracticeAreaBean _practice_area, Date _plan_date)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		Calendar start_of_day = Calendar.getInstance();
		start_of_day.setTime(_plan_date);
		start_of_day.set(Calendar.HOUR_OF_DAY, 0);
		start_of_day.set(Calendar.MINUTE, 0);
		start_of_day.set(Calendar.SECOND, 0);

		Calendar end_of_day = Calendar.getInstance();
		end_of_day.setTime(_plan_date);
		end_of_day.set(Calendar.HOUR_OF_DAY, 23);
		end_of_day.set(Calendar.MINUTE, 59);
		end_of_day.set(Calendar.SECOND, 59);

		Criteria crit = new Criteria();
		crit.addJoin(PaymentPlanPeer.PAYMENT_PLAN_ID, PaymentPlanInstancePeer.PAYMENT_PLAN_ID);
		crit.add(PaymentPlanInstancePeer.PERSON_ID, _person.getId());
		crit.add(PaymentPlanInstancePeer.START_DATE, start_of_day.getTime(), Criteria.GREATER_EQUAL);
		crit.and(PaymentPlanInstancePeer.START_DATE, end_of_day.getTime(), Criteria.LESS_EQUAL);
		crit.add(PaymentPlanPeer.PRACTICE_AREA_ID, _practice_area.getId());

		List obj_list = PaymentPlanInstancePeer.doSelect(crit);
		if (obj_list.size() == 1)
			return PaymentPlanInstanceBean.getPaymentPlanInstance((PaymentPlanInstance)obj_list.get(0));
		else if (obj_list.size() == 0)
			throw new ObjectNotFoundException("Payment Plan not found for " + _person.getLabel() + " on date " + CUBean.getUserDateString(_plan_date));
		throw new UniqueObjectNotFoundException("Unique Payment Plan not found for " + _person.getLabel() + " on date " + CUBean.getUserDateString(_plan_date));
	}
	
	public static Vector
	getPaymentPlanInstances(UKOnlinePersonBean _person)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		Vector vec = new Vector();
		Criteria crit = new Criteria();
		crit.add(PaymentPlanInstancePeer.PERSON_ID, _person.getId());
		crit.addAscendingOrderByColumn(PaymentPlanInstancePeer.CREATION_DATE);
		Iterator obj_itr = PaymentPlanInstancePeer.doSelect(crit).iterator();
		while (obj_itr.hasNext())
			vec.addElement(PaymentPlanInstanceBean.getPaymentPlanInstance((PaymentPlanInstance)obj_itr.next()));
		return vec;
	}
	
	public static Vector
	getPaymentPlanInstances(UKOnlinePersonBean _person, PracticeAreaBean _practice_area)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		Vector vec = new Vector();
		Criteria crit = new Criteria();
		crit.addJoin(PaymentPlanPeer.PAYMENT_PLAN_ID, PaymentPlanInstancePeer.PAYMENT_PLAN_ID);
		crit.add(PaymentPlanInstancePeer.PERSON_ID, _person.getId());
		crit.add(PaymentPlanInstancePeer.ISACTIVE, (short)1);
		crit.add(PaymentPlanInstancePeer.VISITS_REMAINING, (short)0, Criteria.GREATER_THAN);
		crit.add(PaymentPlanPeer.PRACTICE_AREA_ID, _practice_area.getId());
		crit.addAscendingOrderByColumn(PaymentPlanInstancePeer.CREATION_DATE);
		Iterator obj_itr = PaymentPlanInstancePeer.doSelect(crit).iterator();
		while (obj_itr.hasNext())
			vec.addElement(PaymentPlanInstanceBean.getPaymentPlanInstance((PaymentPlanInstance)obj_itr.next()));
		return vec;
	}
    
    // SQL
    
    /*
     * <table name="PAYMENT_PLAN_INSTANCE" idMethod="native">
    <column name="PAYMENT_PLAN_INSTANCE_ID" primaryKey="true" required="true" type="INTEGER" autoIncrement="true"/>
    <column name="PAYMENT_PLAN_ID" required="true" type="INTEGER"/>
    <column name="PERSON_ID" required="true" type="INTEGER"/>
    <column name="VISITS_USED" required="true" type="INTEGER"/>
    <column name="VISITS_REMAINING" required="true" type="INTEGER"/>
    <column name="IS_PAID_FOR" required="true" type="SMALLINT"/>
    <column name="AMOUNT_PAID" required="true" scale="2" size="7" type="DECIMAL"/>
    <column name="MONTHS_PAID" required="true" type="INTEGER"/>
    <column name="START_DATE" required="true" type="DATE"/>
    <column name="END_DATE" required="false" type="DATE"/>
    <column name="ISACTIVE" required="true" type="SMALLINT"/>
    <column name="ALLOW_GROUP_POOLING" required="true" type="SMALLINT"/>
    <column name="NOTE" required="false" type="VARCHAR" size="255"/>
    <column name="CREATION_DATE" required="true" type="DATE"/>
    <column name="MODIFICATION_DATE" required="false" type="DATE"/>
    <column name="CREATE_PERSON_ID" required="false" type="INTEGER"/>
    <column name="MODIFY_PERSON_ID" required="false" type="INTEGER"/>
    <foreign-key foreignTable="PAYMENT_PLAN">
	<reference local="PAYMENT_PLAN_ID" foreign="PAYMENT_PLAN_ID"/>
    </foreign-key>
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
	
    private PaymentPlanInstance plan_instance;
    
    // CONSTRUCTORS
    
    public
    PaymentPlanInstanceBean()
    {
		plan_instance = new PaymentPlanInstance();
		isNew = true;
    }
    
    public
    PaymentPlanInstanceBean(PaymentPlanInstance _plan_instance)
    {
		plan_instance = _plan_instance;
		isNew = false;
    }
    
    // INSTANCE METHODS
	
	public void
	activate()
	{
		plan_instance.setIsactive((short)1);
	}
    
    public boolean
    equals(Object _obj)
    {
		if (_obj == null)
			return false;
		if (_obj instanceof PaymentPlanInstanceBean)
			return (this.getId() == ((PaymentPlanInstanceBean)_obj).getId());
		else
			return false;
    }
	
	public BigDecimal
	getAmountCharged()
	{
		return plan_instance.getAmountCharged();
	}
	
	public String
	getAmountChargedString()
	{
		return plan_instance.getAmountCharged().toString();
	}
	
	public BigDecimal
	getAmountPaid()
	{
		return plan_instance.getAmountPaid();
	}
	
	public String
	getAmountPaidString()
	{
		return plan_instance.getAmountPaid().toString();
	}
	
	public BigDecimal
	getEscrowAmount()
		throws TorqueException, ObjectNotFoundException
	{
		// how to calculate escrow amount????
		// escrow = amount_paid - (number_of_visits_used * per_visit_charge)
		// or
		// escrow = amount_paid / (total_visits - number_of_visits_used)
		
		// or escrow = amount_paid - (amount_paid * visits_used / total_visits)
		
		
		BigDecimal zero = new BigDecimal(0);
		zero.setScale(2, BigDecimal.ROUND_HALF_UP);
		
		int visits_remaining = this.getPaymentPlan().getVisits() - this.getNumberOfVisitsUsed();
		if (visits_remaining < 1)
			return zero;
		
		BigDecimal amount_paid = this.getAmountPaid();
		
		if (this.getAmountPaid().compareTo(zero) != 1)
			return zero;
		
		
		//BigDecimal visits_remaining_bd = new BigDecimal(visits_remaining);
		
		//return this.getAmountPaid().divide(visits_remaining_bd, BigDecimal.ROUND_HALF_UP);
		
		
		BigDecimal total_visits = new BigDecimal(this.getPaymentPlan().getVisits());
		BigDecimal visits_used = new BigDecimal(this.getNumberOfVisitsUsed());
		
		BigDecimal value_of_visits_used = (amount_paid.multiply(visits_used)).divide(total_visits, BigDecimal.ROUND_HALF_UP);
		
		BigDecimal escrow_amount = amount_paid.subtract(value_of_visits_used);
		return escrow_amount.setScale(2, BigDecimal.ROUND_HALF_UP);
	}
	
	public String
	getEscrowAmountString()
		throws TorqueException, ObjectNotFoundException
	{
		return this.getEscrowAmount().setScale(2, BigDecimal.ROUND_HALF_UP).toString();
	}
	
	public String
	getDropChargeAmountString()
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		// how to calculate drop charge...  if a person drops from a plan, 
		
		try
		{
			BigDecimal zero = new BigDecimal(0);

			BigDecimal per_visit_charge = this.getPerVisitCharge();
			BigDecimal number_of_visits_used = new BigDecimal(this.getNumberOfVisitsUsed());

			BigDecimal amount_if_cash = number_of_visits_used.multiply(per_visit_charge);

			BigDecimal amount_paid = this.getAmountPaid();

			BigDecimal drop_charge = amount_if_cash.subtract(amount_paid);

			if (drop_charge.compareTo(zero) != 1)
				return "0.00";
			return drop_charge.toString();
		}
		catch (Exception x)
		{
			x.printStackTrace();
		}
		
		return "";
	}
    
    public int
    getId()
    {
		return plan_instance.getPaymentPlanInstanceId();
    }
    
    public String
    getLabel()
		throws TorqueException, ObjectNotFoundException
    {
		return this.getPaymentPlan().getLabel();
    }
	
	public String
	getNote()
	{
		return plan_instance.getNote();
	}
	
	public int
	getNumberOfVisitsRemaining()
	{
		return plan_instance.getVisitsRemaining();
	}
	
	public int
	getNumberOfVisitsUsed()
	{
		return plan_instance.getVisitsUsed();
	}
	
	public PaymentPlanBean
	getPaymentPlan()
		throws TorqueException, ObjectNotFoundException
	{
		return PaymentPlanBean.getPaymentPlan(plan_instance.getPaymentPlan().getPaymentPlanId());
	}
	
	public UKOnlinePersonBean
	getPerson()
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		return (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(plan_instance.getPersonId());
	}
	
	public BigDecimal
	getPerVisitCharge()
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		UKOnlinePersonBean person = this.getPerson();
		GroupUnderCareMemberTypeBean member_type = person.getGroupUnderCareMemberType();
		return member_type.getVisitCharge(this.getPaymentPlan().getPracticeArea());
	}
	
	public String
	getPerVisitChargeString()
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		try
		{
			UKOnlinePersonBean person = this.getPerson();
			GroupUnderCareMemberTypeBean member_type = person.getGroupUnderCareMemberType();
			return member_type.getVisitChargeString(this.getPaymentPlan().getPracticeArea());
		}
		catch (Exception x)
		{
			x.printStackTrace();
		}
		
		return "";
	}
	
	public String
	getStartDateString()
	{
		return CUBean.getUserDateString(plan_instance.getStartDate());
	}
    
    public String
    getValue()
    {
		return plan_instance.getPaymentPlanInstanceId() + "";
    }

	public boolean
	hasVisitsRemaining()
	{
		return (plan_instance.getVisitsRemaining() > 0);
	}
	
	public void
	inactivate()
	{
		plan_instance.setIsactive((short)0);
	}
    
    protected void
    insertObject()
		throws Exception
    {
		plan_instance.setCreationDate(new Date());
		plan_instance.save();
    }
	
	public boolean
	isActive()
	{
		return (plan_instance.getIsactive() == (short)1);
	}
	
	public boolean
	isGroupPoolingAllowed()
	{
		return (plan_instance.getAllowGroupPooling() == (short)1);
	}
	
	public boolean
	isPaidFor()
	{
		return (plan_instance.getIsPaidFor() == (short)1);
	}
	
	public void
	setAmountCharged(BigDecimal _amount)
	{
		plan_instance.setAmountCharged(_amount);
	}
	
	public void
	setAmountPaid(BigDecimal _amount)
	{
		plan_instance.setAmountPaid(_amount);
	}
    
    public void
    setCreatePerson(UKOnlinePersonBean _person)
		throws TorqueException
    {
		plan_instance.setCreatePersonId(_person.getId());
    }
	
	public void
	setEndDate(Date _end_date)
	{
		plan_instance.setEndDate(_end_date);
	}
	
	public void
	setGroupPoolingAllowed(boolean _allowed)
	{
		plan_instance.setAllowGroupPooling(_allowed ? (short)1 : (short)0);
	}
	
	public void
	setIsPaidFor(boolean _is_paid_for)
	{
		plan_instance.setIsPaidFor(_is_paid_for ? (short)1 : (short)0);
	}
	
	public void
	setPaymentPlan(PaymentPlanBean _payment_plan)
		throws TorqueException
	{
		plan_instance.setPaymentPlanId(_payment_plan.getId());
	}
    
    public void
    setModifyPerson(UKOnlinePersonBean _person)
		throws TorqueException
    {
		plan_instance.setModifyPersonId(_person.getId());
    }
	
	public void
	setMonthsPaid(int _months_paid)
	{
		plan_instance.setMonthsPaid(_months_paid);
	}
    
    public void
    setNote(String _note)
    {
		plan_instance.setNote(_note);
    }
	
	public void
	setNumberOfVisitsRemaining(int _visits_remaining)
	{
		plan_instance.setVisitsRemaining(_visits_remaining);
	}
	
	public void
	setNumberOfVisitsUsed(int _visits_used)
	{
		plan_instance.setVisitsUsed(_visits_used);
	}
	
	public void
	setPerson(UKOnlinePersonBean _person)
		throws TorqueException
	{
		plan_instance.setPersonId(_person.getId());
	}
	
	public void
	setStartDate(Date _start_date)
	{
		plan_instance.setStartDate(_start_date);
	}
    
    protected void
    updateObject()
		throws ObjectAlreadyExistsException, IllegalValueException, Exception
    {
		plan_instance.setModificationDate(new Date());
		plan_instance.save();
    }
	
	public void
	useVisit()
		throws TorqueException, ObjectNotFoundException, IllegalValueException
	{
		if (!this.hasVisitsRemaining())
			throw new IllegalValueException(this.getLabel() + " has only " + plan_instance.getVisitsRemaining() + " visits remaining.");

		plan_instance.setVisitsUsed(plan_instance.getVisitsUsed() + 1);
		plan_instance.setVisitsRemaining(plan_instance.getVisitsRemaining() - 1);
	}

	public void
	useVisits(short _visits_being_used)
		throws TorqueException, ObjectNotFoundException, IllegalValueException
	{
		if (plan_instance.getVisitsRemaining() < _visits_being_used)
			throw new IllegalValueException(this.getLabel() + " has only " + plan_instance.getVisitsRemaining() + " visits remaining.");

		plan_instance.setVisitsUsed(plan_instance.getVisitsUsed() + _visits_being_used);
		plan_instance.setVisitsRemaining(plan_instance.getVisitsRemaining() - _visits_being_used);
	}
}