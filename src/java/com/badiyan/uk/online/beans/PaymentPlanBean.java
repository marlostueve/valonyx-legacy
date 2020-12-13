/*
 * PaymentPlanBean.java - 2/13/08
 */

package com.badiyan.uk.online.beans;

import com.badiyan.uk.beans.*;

import com.badiyan.torque.*;
import com.badiyan.uk.exceptions.*;

import java.beans.*;
import java.math.*;
import java.util.*;

import org.apache.torque.*;
import org.apache.torque.util.Criteria;

import com.badiyan.uk.tasks.*;

/**
 *
 * @author marlo
 */
public class
PaymentPlanBean
	extends CUBean
	implements java.io.Serializable
{
    // CLASS VARIABLES
    
	public static final short CASH_BILLING_TYPE = 1;
	public static final short THIRD_PARTY_INSURANCE_BILLING_TYPE = 2;
    public static final short DIRECT_INSURANCE_BILLING_TYPE = 3;

    protected static HashMap<Integer,PaymentPlanBean> hash = new HashMap<Integer,PaymentPlanBean>(11);

    // CLASS METHODS
	
	public static PaymentPlanBean
	getPaymentPlan(int _id)
		throws TorqueException, ObjectNotFoundException
	{
		Integer key = new Integer(_id);
		PaymentPlanBean payment_plan = (PaymentPlanBean)hash.get(key);
		if (payment_plan == null)
		{
			Criteria crit = new Criteria();
			crit.add(PaymentPlanPeer.PAYMENT_PLAN_ID, _id);
			List objList = PaymentPlanPeer.doSelect(crit);
			if (objList.size() == 0)
				throw new ObjectNotFoundException("Could not locate payment plan with id: " + _id);

			payment_plan = PaymentPlanBean.getPaymentPlan((PaymentPlan)objList.get(0));
		}

		return payment_plan;
	}

    private static PaymentPlanBean
    getPaymentPlan(PaymentPlan _payment_plan)
		throws TorqueException
    {
		Integer key = new Integer(_payment_plan.getPaymentPlanId());
		PaymentPlanBean plan = (PaymentPlanBean)hash.get(key);
		if (plan == null)
		{
			plan = new PaymentPlanBean(_payment_plan);
			hash.put(key, plan);
		}

		return plan;
    }
	
	public static PaymentPlanBean
	getPaymentPlan(CheckoutCodeBean _checkout_code)
		throws TorqueException, ObjectNotFoundException
	{
		Criteria crit = new Criteria();
		crit.add(PaymentPlanPeer.CHECKOUT_CODE_ID, _checkout_code.getId());
		List objList = PaymentPlanPeer.doSelect(crit);
		if (objList.size() == 0)
			throw new ObjectNotFoundException("Could not locate payment plan for checkout code: " + _checkout_code.getLabel());
		if (objList.size() > 1)
			throw new ObjectNotFoundException("Could not locate unique payment plan for checkout code: " + _checkout_code.getLabel());

		return PaymentPlanBean.getPaymentPlan((PaymentPlan)objList.get(0));
	}
    
    public static Vector
    getPaymentPlans(CompanyBean _company)
		throws TorqueException
    {
		Vector vec = new Vector();

		Criteria crit = new Criteria();
		crit.add(PaymentPlanPeer.COMPANY_ID, _company.getId());
		Iterator itr = PaymentPlanPeer.doSelect(crit).iterator();
		while (itr.hasNext())
			vec.addElement(PaymentPlanBean.getPaymentPlan((PaymentPlan)itr.next()));

		return vec;
    }
    
    // SQL
    
    /*
     * <table name="PAYMENT_PLAN" idMethod="native">
	    <column name="PAYMENT_PLAN_ID" primaryKey="true" required="true" type="INTEGER" autoIncrement="true"/>
		<column name="PLAN_NAME" required="true" type="VARCHAR" size="255"/>
		<column name="COMPANY_ID" required="true" type="INTEGER"/>
	    
	    <column name="PRIMARY_CLIENT_ID" required="true" type="INTEGER"/>
	    <column name="PRACTITIONER_ID" required="true" type="INTEGER"/>
		
		<!-- bill per visit, per day/week/month/year -->
		<!-- pool visits among group under care members? -->
		<!-- equal billing per visit?  or discounts for additional group under care members -->
		<!--  -->
		
	    <column name="VISITS" required="true" type="INTEGER"/>
	    <column name="PRICE" required="true" type="DECIMAL"/>
		<column name="MONTHS_TO_PAY" required="true" type="INTEGER"/>
		<column name="DEFAULT_ALLOW_GROUP_POOLING" required="true" type="SMALLINT"/>
		
		<column name="NOTE" required="true" type="VARCHAR" size="255"/>
	    
	    <column name="CREATION_DATE" required="true" type="DATE"/>
	    <column name="MODIFICATION_DATE" required="false" type="DATE"/>
	    <column name="CREATE_PERSON_ID" required="false" type="INTEGER"/>
	    <column name="MODIFY_PERSON_ID" required="false" type="INTEGER"/>

		<foreign-key foreignTable="COMPANY">
			<reference local="COMPANY_ID" foreign="COMPANYID"/>
		</foreign-key>
	    <foreign-key foreignTable="PERSON">
			<reference local="PRIMARY_CLIENT_ID" foreign="PERSONID"/>
	    </foreign-key>
	    <foreign-key foreignTable="PERSON">
			<reference local="PRACTITIONER_ID" foreign="PERSONID"/>
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
	
    private PaymentPlan payment_plan;
    
    // CONSTRUCTORS
    
    public
    PaymentPlanBean()
    {
		payment_plan = new PaymentPlan();
		isNew = true;
    }
    
    public
    PaymentPlanBean(PaymentPlan _payment_plan)
    {
		payment_plan = _payment_plan;
		isNew = false;
    }
    
    // INSTANCE METHODS
	
	public boolean
	areMonthlyPaymentsAllowed()
	{
		return (payment_plan.getDefaultAllowMonthlyPayments() == (short)1);
	}
    
    public boolean
    equals(Object _obj)
    {
		if (_obj == null)
			return false;
		if (_obj instanceof PaymentPlanBean)
			return (this.getId() == ((PaymentPlanBean)_obj).getId());
		else
			return false;
    }
	
	public CheckoutCodeBean
	getCheckoutCode()
		throws TorqueException, ObjectNotFoundException
	{
		return CheckoutCodeBean.getCheckoutCode(payment_plan.getCheckoutCodeId());
	}
    
    public CompanyBean
    getCompany()
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
    {
		return CompanyBean.getCompany(payment_plan.getCompanyId());
    }
	
	public BigDecimal
	getCost()
	{
		return payment_plan.getPrice();
	}

    public String
    getCostString()
    {
		return payment_plan.getPrice().setScale(2, BigDecimal.ROUND_HALF_UP).toString();
    }
    
    public int
    getId()
    {
		return payment_plan.getPaymentPlanId();
    }
    
    public String
    getLabel()
    {
		return payment_plan.getPlanName();
    }
	
	public int
	getMonthsToPay()
	{
		return payment_plan.getMonthsToPay();
	}
	
	public String
	getNote()
	{
		return payment_plan.getNote();
	}
	
	public PracticeAreaBean
	getPracticeArea()
		throws TorqueException, ObjectNotFoundException
	{
		return PracticeAreaBean.getPracticeArea(payment_plan.getPracticeAreaId());
	}
	
	public short
	getType()
	{
		return payment_plan.getType();
	}
    
    public String
    getValue()
    {
		return payment_plan.getPaymentPlanId() + "";
    }
	
	public int
	getVisits()
	{
		return payment_plan.getVisits();
	}
    
    protected void
    insertObject()
		throws Exception
    {
		payment_plan.setCreationDate(new Date());
		payment_plan.save();
    }
	
	public boolean
	isGroupPoolingAllowed()
	{
		return (payment_plan.getDefaultAllowGroupPooling() == (short)1);
	}
    
    public void
    setAllowGroupPooling(boolean _allow)
    {
		payment_plan.setDefaultAllowGroupPooling(_allow ? (short)1 : (short)0);
    }
    
    public void
    setAllowMonthlyPayments(boolean _allow)
    {
		payment_plan.setDefaultAllowMonthlyPayments(_allow ? (short)1 : (short)0);
    }
	
	public void
	setCheckoutCode(CheckoutCodeBean _checkout_code)
		throws TorqueException
	{
		payment_plan.setCheckoutCodeId(_checkout_code.getId());
	}
    
    public void
    setCompany(CompanyBean _company)
		throws TorqueException
    {
		payment_plan.setCompanyId(_company.getId());
    }
    
    public void
    setCost(BigDecimal _cost)
		throws TorqueException
    {
		payment_plan.setPrice(_cost);
    }
    
    public void
    setCreatePerson(UKOnlinePersonBean _person)
		throws TorqueException
    {
		payment_plan.setCreatePersonId(_person.getId());
    }
    
    public void
    setModifyPerson(UKOnlinePersonBean _person)
		throws TorqueException
    {
		payment_plan.setModifyPersonId(_person.getId());
    }
    
    public void
    setMonthsToPay(int _months_to_pay)
		throws TorqueException
    {
		payment_plan.setMonthsToPay(_months_to_pay);
    }
    
    public void
    setName(String _name)
    {
		payment_plan.setPlanName(_name);
    }
    
    public void
    setNote(String _note)
    {
		payment_plan.setNote(_note);
    }
	
	public void
	setPracticeArea(PracticeAreaBean _practice_area)
		throws TorqueException
	{
		payment_plan.setPracticeAreaId(_practice_area.getId());
	}
	
	public void
	setType(short _type)
	{
		payment_plan.setType(_type);
	}
	
	public void
	setVisits(int _visits)
	{
		payment_plan.setVisits(_visits);
	}
    
    protected void
    updateObject()
		throws ObjectAlreadyExistsException, IllegalValueException, Exception
    {
		payment_plan.setModificationDate(new Date());
		payment_plan.save();
    }
}
