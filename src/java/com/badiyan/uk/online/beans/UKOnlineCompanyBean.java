/**
 * Created on August 17, 2008, 8:38 PM
 *
 * @author  marlo
 * @version
 */

package com.badiyan.uk.online.beans;

import com.badiyan.uk.beans.*;
import com.badiyan.uk.exceptions.*;

import com.badiyan.torque.*;

import java.math.BigDecimal;
import java.util.*;

import org.apache.torque.*;
import org.apache.torque.om.NumberKey;
import org.apache.torque.util.Criteria;

public class
UKOnlineCompanyBean
    extends CompanyBean
    implements java.io.Serializable
{
    // CLASS VARIABLES
    
    public static final String DEPARTMENT_ADMINISTRATOR_ROLE_NAME = "Department Administrator";
    
    public static final String PRACTITIONER_ROLE_NAME = "Practitioner";
    public static final String CLIENT_ROLE_NAME = "Client";
    
    // CLASS METHODS
	
	public static UKOnlineCompanyBean
	getValonyxCompany() throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		return (UKOnlineCompanyBean)UKOnlineCompanyBean.getCompany(3);
	}

    public static void
    delete(int _id)
		throws TorqueException
    {
		Criteria delete_crit = new Criteria();
		delete_crit.add(CompanyPeer.COMPANYID, _id);
		delete_crit.add(CompanyPeer.COMPANYTYPE, "Vendor");
		List objList = CompanyPeer.doSelect(delete_crit);
		if (objList.size() == 1)
		{
			// delete any associated phone numbers

			Criteria crit = new Criteria();
			crit.add(PhonenumberCompanyPeer.COMPANY_ID, _id);
			PhonenumberCompanyPeer.doDelete(crit);

			// delete any associated addresses

			crit = new Criteria();
			crit.add(AddressPeer.COMPANYID, _id);
			AddressPeer.doDelete(crit);

			// delete any associated settings

			crit = new Criteria();
			crit.add(CompanySettingsPeer.COMPANY_ID, _id);
			CompanySettingsPeer.doDelete(crit);
			CompanySettingsPeer.doDelete(crit);
		}

		CompanyPeer.doDelete(delete_crit);
    }

	public static CompanyBean
	getCompany(int _id)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		/*
		if (_id == 0)
				throw new ObjectNotFoundException("Could not locate company with id: " + _id);
		 */

		// First look for the company in the Hashtable

		NumberKey key = new NumberKey(_id);
		Object company = (Object)idCompanies.get(key);
		if (company instanceof UKOnlineCompanyBean)
			return (UKOnlineCompanyBean)company;
		else
			company = null;

		//UKOnlineCompanyBean company = (UKOnlineCompanyBean)idCompanies.get(key);

		if (company == null)
		{
			// The named company was not located in the hashtable.  Search the db for it.

			Criteria crit = new Criteria();
			crit.add(CompanyPeer.COMPANYID, _id);
			List objList = CompanyPeer.doSelect(crit);
			if (objList.size() == 0)
				throw new ObjectNotFoundException("Could not locate company with id: " + _id);
			if (objList.size() > 1)
				throw new UniqueObjectNotFoundException("Multiple companies found for id: " + _id);

			company = (UKOnlineCompanyBean)UKOnlineCompanyBean.getCompany((Company)objList.get(0));
		}

		return (UKOnlineCompanyBean)company;
	}

	protected static CompanyBean
	getCompany(Company _company)
	{
		NumberKey key = new NumberKey(_company.getCompanyid());

		Object company = (Object)idCompanies.get(key);
		if (company instanceof UKOnlineCompanyBean)
			return (UKOnlineCompanyBean)company;
		else
			company = null;

		//UKOnlineCompanyBean company = (UKOnlineCompanyBean)idCompanies.get(key);

		if (company == null)
		{
			company = new UKOnlineCompanyBean(_company);
			idCompanies.put(key, company);
		}

		return (UKOnlineCompanyBean)company;
	}

	public static List
	getCompaniesActiveOnly()
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		Vector vec = new Vector();
		Criteria crit = new Criteria();
		crit.add(CompanyPeer.COMPANYTYPE, "Corporation");
		crit.add(CompanyPeer.ISACTIVE, (short)1);
		crit.addAscendingOrderByColumn(CompanyPeer.COMPANYNAME);
		List objList = CompanyPeer.doSelect(crit);
		Iterator itr = objList.iterator();
		while (itr.hasNext())
		{
			Company obj = (Company)itr.next();
			vec.addElement(UKOnlineCompanyBean.getCompany(obj));
		}
		return vec;
	}

	public static List
	getCompanies()
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		Vector vec = new Vector();
		Criteria crit = new Criteria();
		crit.add(CompanyPeer.COMPANYTYPE, "Corporation");
		crit.addAscendingOrderByColumn(CompanyPeer.COMPANYNAME);
		List objList = CompanyPeer.doSelect(crit);
		Iterator itr = objList.iterator();
		while (itr.hasNext())
		{
			Company obj = (Company)itr.next();
			vec.addElement(UKOnlineCompanyBean.getCompany(obj));
		}
		return vec;
	}

	public static List
	getCompanies(PersonBean _person)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		Vector vec = new Vector();

		Criteria crit = new Criteria();

		if (_person.hasRole(RoleBean.SYSTEM_ADMINISTRATOR_ROLE_NAME))
		{
		    return UKOnlineCompanyBean.getCompanies();
		}
		else
		{
		    Iterator itr = null;
		    if (_person.hasRole(RoleBean.COMPANY_ADMINISTRATOR_ROLE_NAME))
		    {
			    crit.addJoin(CompanyPeer.COMPANYID, CompanyAdministratorPeer.COMPANY_ID);
			    crit.addJoin(CompanyAdministratorPeer.PERSON_ID, PersonPeer.PERSONID);
			    crit.add(PersonPeer.PERSONID, _person.getId());
				crit.add(CompanyPeer.COMPANYTYPE, "Corporation");
			    crit.addAscendingOrderByColumn(CompanyPeer.COMPANYNAME);
			    itr = CompanyPeer.doSelect(crit).iterator();
			    while (itr.hasNext())
			    {
				    Company obj = (Company)itr.next();
				    vec.addElement(UKOnlineCompanyBean.getCompany(obj.getCompanyid()));
			    }
			    crit = new Criteria();
		    }

		    crit.addJoin(CompanyPeer.COMPANYID, DepartmentPeer.COMPANYID);
		    crit.addJoin(DepartmentPeer.DEPARTMENTID, DepartmentpersonPeer.DEPARTMENTID);
		    crit.add(DepartmentpersonPeer.PERSONID, _person.getId());
			crit.add(CompanyPeer.COMPANYTYPE, "Corporation");
		    itr = CompanyPeer.doSelect(crit).iterator();
		    while (itr.hasNext())
		    {
			    Company obj = (Company)itr.next();
			    UKOnlineCompanyBean company = (UKOnlineCompanyBean)UKOnlineCompanyBean.getCompany(obj.getCompanyid());
			    if (!vec.contains(company))
				    vec.addElement(company);
		    }
		}

		return vec;
	}

	public static CompanyBean
	getInstance()
		throws TorqueException, UniqueObjectNotFoundException, Exception
	{
		try
		{
			if (companySingleton != null)
			{
				if (!(companySingleton instanceof UKOnlineCompanyBean))
					companySingleton = null;
			}

			if (companySingleton == null)
			{
				// The CompanyBean singleton has not been initialized.  I need to query the database for it.

				Criteria crit = new Criteria();
				crit.add(CompanyPeer.COMPANYNAME, CUBean.getProperty("cu.companyName"));
				List objList = CompanyPeer.doSelect(crit);
				if (objList.size() == 0)
				{
					// The company specified in the web.xml configuration file was not found.  I need to create it.

					System.out.println("The company specified in the web.xml configuration file was not found.  Creating new company :" + CUBean.getProperty("cu.companyName"));
					companySingleton = new UKOnlineCompanyBean();
					companySingleton.setName(CUBean.getProperty("cu.companyName"));
					companySingleton.setType(CUBean.getProperty("cu.defaultCompanyType"));
					companySingleton.save();
					System.out.println(CUBean.getProperty("cu.companyName") + " created.");
				}
				else if (objList.size() > 1)
					throw new UniqueObjectNotFoundException("Could not find unique company object.");
				else
					companySingleton = new UKOnlineCompanyBean((Company)objList.get(0));
			}
		}
		catch (Exception x)
		{
			System.out.println(x.getMessage());
			x.printStackTrace();
			throw x;
		}

		return companySingleton;
	}

	/*
	public static Vector
	getVendors(UKOnlineCompanyBean _company)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		System.out.println("getVendors() invoked >" + _company.getLabel());
		Vector vec = new Vector();
		Criteria crit = new Criteria();
		crit.addJoin(CompanySettingsPeer.COMPANY_ID, CompanyPeer.COMPANYID);
		crit.add(CompanySettingsPeer.PARENT_ID, _company.getId());
		crit.add(CompanyPeer.COMPANYTYPE, "Vendor");
		crit.addAscendingOrderByColumn(CompanyPeer.COMPANYNAME);
		System.out.println("getVendors() crit >" + crit.toString());
		Iterator itr = CompanyPeer.doSelect(crit).iterator();
		while (itr.hasNext())
			vec.addElement(UKOnlineCompanyBean.getCompany((Company)itr.next()));

		return vec;
	}

	public static Vector
	getVendorsNewToQuickBooks(UKOnlineCompanyBean _company)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		System.out.println("getVendorsNewToQuickBooks() invoked");
		Vector vec = new Vector();
		Vector vendors = UKOnlineCompanyBean.getVendors(_company);
		System.out.println("vendors found >" + vendors.size());
		Iterator itr = vendors.iterator();
		while (itr.hasNext())
		{
			UKOnlineCompanyBean vendor = (UKOnlineCompanyBean)itr.next();
			Criteria crit = new Criteria();
			crit.add(VendorRetDbPeer.COMPANY_ID, vendor.getId());
			System.out.println("crit >" + crit.toString());
			List objList = VendorRetDbPeer.doSelect(crit);
			if (objList.size() == 0)
			{
				// no VendorRet for this vendor.
				vec.addElement(vendor);
			}
			else
				System.out.println("VendorRet found for >" + vendor.getLabel());
		}
		return vec;
	}
	 */
    
    // INSTANCE VARIABLES

	private QuickBooksSettings qbfs_settings = null;
	private SubscriptionInfo subscription_info = null;
	
	private Vector treatment_rooms = null;
    
    // CONSTRUCTORS
    
    public UKOnlineCompanyBean()
    {
		super();
    }

    public UKOnlineCompanyBean(Company _company)
    {
		super(_company);
    }
    
    // INSTANCE METHODS

	public String
	getAccountNumberString()
	{
		String str = super.company.getMerchantid();
		if (str == null)
			return "";
		return str;
	}

	public UKOnlinePersonBean
	getAdministrator()
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		Criteria crit = new Criteria();
		crit.add(CompanyAdministratorPeer.COMPANY_ID, this.getId());
		List objList = CompanyAdministratorPeer.doSelect(crit);
		if (objList.size() == 0)
			throw new ObjectNotFoundException("Administrator not found for " + this.getLabel());

		CompanyAdministrator map = (CompanyAdministrator)objList.get(0);
		return (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(map.getPersonId());
	}
	
	public int
	getEndHourOfDay()
		throws TorqueException
	{
		this.getSettings();
		return settings.getCloseHour();
		//return 20;
	}
	
	public int
	getEndMinute()
		throws TorqueException
	{
		this.getSettings();
		return settings.getCloseMinute();
		//return 0;
	}

	public QuickBooksSettings
	getQuickBooksSettings()
		throws TorqueException
	{
		if (qbfs_settings == null)
		{
			try
			{
				qbfs_settings = QuickBooksSettings.getSettings(this);
			}
			catch (ObjectNotFoundException x)
			{
				qbfs_settings = new QuickBooksSettings();
				qbfs_settings.setCompany(this);
			}
		}
		return qbfs_settings;
	}

	public SubscriptionInfo
	getSubscriptionInfo()
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		if (subscription_info == null)
		{
			try
			{
				subscription_info = SubscriptionInfo.getSubscriptionInfo(this);
			}
			catch (ObjectNotFoundException x)
			{
				subscription_info = new SubscriptionInfo();
				subscription_info.setCompany(this);
				subscription_info.setSubscriptionStartDate(new Date());
			}
		}
		return subscription_info;
	}
	
	public int
	getStartHourOfDay()
		throws TorqueException
	{
		this.getSettings();
		return settings.getOpenHour();
		//return 7;
	}
	
	public int
	getStartMinute()
		throws TorqueException
	{
		this.getSettings();
		return settings.getOpenMinute();
		//return 0;
	}
    
    protected void
    insertObject()
		throws ObjectAlreadyExistsException, IllegalValueException, Exception
    {
		System.out.println("insertObject() invoked in UKOnlinePersonBean");
		super.insertObject();
    }

	public boolean
	isActive()
	{
		return true;
	}
	
	public boolean
	isBeyondClosingTime(AppointmentBean _appointment)
		throws TorqueException
	{
		Calendar appointment_cal = Calendar.getInstance();
		appointment_cal.setTime(_appointment.getAppointmentDate());
		appointment_cal.add(Calendar.MINUTE, _appointment.getDuration()); // appointment end
		
		// is the appointment end beyond the end of day?
		
		int appointment_end_hour_of_day = appointment_cal.get(Calendar.HOUR_OF_DAY);
		int appointment_end_minute = appointment_cal.get(Calendar.MINUTE);
		
		if (appointment_end_hour_of_day < this.getEndHourOfDay())
			return false;
		if (appointment_end_hour_of_day == this.getEndHourOfDay())
		{
			if (appointment_end_minute <= this.getEndMinute())
				return false;
		}
		
		return true;
	}

	public boolean
	isPOSEnabled()
		throws TorqueException
	{
		QuickBooksSettings qb_settings = this.getQuickBooksSettings();
		return qb_settings.isPOSEnabled();
	}

	public void
	setAccountNumber(String _account_number)
	{
		super.company.setMerchantid(_account_number);
	}
    
    protected void
    updateObject()
    throws ObjectAlreadyExistsException, IllegalValueException, Exception
    {
		System.out.println("updateObject() invoked in UKOnlinePersonBean - " + super.company);
		super.updateObject();
    }
	
	public int getNumClientsForSubscriptionFeeCalculation() throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException {
		int num_clients = 0;
		Iterator itrx = this.getPeople().iterator();
		while (itrx.hasNext()) {
			PersonBean person_obj = (PersonBean)itrx.next();
			UKOnlinePersonBean person = (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(person_obj.getId());
			if (person.isActive()) {
				if (!(person.hasRole(UKOnlineRoleBean.PRACTITIONER_ROLE_NAME) || person.hasRole(UKOnlineRoleBean.CASHIER_ROLE_NAME)))
					num_clients++;
			}
		}
		return num_clients;
	}
	
	public BigDecimal
	getBaseMonthlySubscriptionFee() throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException {
		int num_clients = this.getNumClientsForSubscriptionFeeCalculation();
		BigDecimal fee = new BigDecimal(49.95f);
		if (num_clients > 1999)
			fee = new BigDecimal(199.95f);
		else if (num_clients > 499)
			fee = new BigDecimal(149.95f);
		else if (num_clients > 99)
			fee = new BigDecimal(99.95f);
		
		return fee;
	}
	
	public String
	getBaseMonthlySubscriptionFeeString() throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException {
		int num_clients = this.getNumClientsForSubscriptionFeeCalculation();
		String basic_subscription_fee = "$49.95/month";
		if (num_clients > 1999)
			basic_subscription_fee = "$199.95/month";
		else if (num_clients > 499)
			basic_subscription_fee = "$149.95/month";
		else if (num_clients > 99)
			basic_subscription_fee = "$99.95/month";
		
		return basic_subscription_fee;
	}
	
	public Vector
	getTreatmentRooms() {
		
		if (treatment_rooms == null) {
			try {
				treatment_rooms = TreatmentRoomBean.getTreatmentRooms(this);
			} catch (Exception x) {
				treatment_rooms = new Vector();
			}
		}
		return treatment_rooms;
	}
}