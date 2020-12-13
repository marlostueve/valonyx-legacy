package com.badiyan.uk.online.tasks;

import com.badiyan.torque.*;
import com.badiyan.uk.beans.*;
import com.badiyan.uk.exceptions.*;

import java.util.*;

import org.apache.torque.*;
import org.apache.torque.util.Criteria;

public class
MaintainManufacturerPersonGroupsTask
	extends TimerTask
{
	// CONSTRUCTORS

	public
	MaintainManufacturerPersonGroupsTask()
	{
	}

	// INSTANCE METHODS

	public void
	run()
	{
		System.out.println("run() invoked in MaintainManufacturerPersonGroupsTask - " + this);
		try
		{
			/* strategy: loop through all active courses, get their audiences,
			 * get person group(s) for that audience, make sure there's an
			 * entry
			 */

			// first get all the Manufacturerpersongroup objects
			
			/*

			Hashtable manufacturerPersonGroupHash = new Hashtable();
			Vector keysNotToDelete = new Vector();

			Criteria crit = new Criteria();
			crit.add(ManufacturerpersongroupPeer.ASSIGNTYPE, PersonGroupBean.COURSE_PERSON_GROUP_ASSIGN_TYPE);
			Iterator objListItr = ManufacturerpersongroupPeer.doSelect(crit).iterator();
			while (objListItr.hasNext())
			{
				Manufacturerpersongroup obj = (Manufacturerpersongroup)objListItr.next();
				String key = obj.getManufacturername() + "-" + obj.getPersongroup();
				manufacturerPersonGroupHash.put(key, obj);
			}

			crit = new Criteria();
			crit.add(CoursePeer.COURSESTATUS, CourseBean.DEFAULT_COURSE_STATUS);
			Iterator courseListItr = CoursePeer.doSelect(crit).iterator();
			while (courseListItr.hasNext())
			{
				CourseBean course = CourseBean.getCourse((Course)courseListItr.next());
				try
				{
					ManufacturerBean manufacturer = course.getManufacturer();
					Iterator audienceItr = course.getAudiences().iterator();
					while (audienceItr.hasNext())
					{
						AudienceBean audience = (AudienceBean)audienceItr.next();
						Iterator itr = audience.getPersonGroups().iterator();
						while (itr.hasNext())
						{
							PersonGroupBean personGroup = (PersonGroupBean)itr.next();
							String key = manufacturer.getNameString() + "-" + personGroup.getNameString();

							if (!manufacturerPersonGroupHash.containsKey(key))
							{
								Manufacturerpersongroup obj = new Manufacturerpersongroup();
								obj.setManufacturername(manufacturer.getNameString());
								obj.setPersongroup(personGroup.getNameString());
								obj.setAssigntype(PersonGroupBean.COURSE_PERSON_GROUP_ASSIGN_TYPE);
								obj.save();
								manufacturerPersonGroupHash.put(key, obj);
							}
							keysNotToDelete.addElement(key);
						}
					}
				}
				catch (ObjectNotFoundException objNotFound)
				{
					System.out.println(objNotFound.getMessage());
				}
			}

			crit = new Criteria();
			crit.add(RequirementsetPeer.STATUS, CourseBean.DEFAULT_COURSE_STATUS);
			Iterator certListItr = RequirementsetPeer.doSelect(crit).iterator();
			while (certListItr.hasNext())
			{
				CertificationBean certification = CertificationBean.getCertification((Requirementset)certListItr.next());
				try
				{
					ManufacturerBean manufacturer = certification.getManufacturer();
					Iterator audienceItr = certification.getAudiences().iterator();
					while (audienceItr.hasNext())
					{
						AudienceBean audience = (AudienceBean)audienceItr.next();
						Iterator itr = audience.getPersonGroups().iterator();
						while (itr.hasNext())
						{
							PersonGroupBean personGroup = (PersonGroupBean)itr.next();
							String key = manufacturer.getNameString() + "-" + personGroup.getNameString();

							if (!manufacturerPersonGroupHash.containsKey(key))
							{
								Manufacturerpersongroup obj = new Manufacturerpersongroup();
								obj.setManufacturername(manufacturer.getNameString());
								obj.setPersongroup(personGroup.getNameString());
								obj.setAssigntype(PersonGroupBean.COURSE_PERSON_GROUP_ASSIGN_TYPE);
								obj.save();
								manufacturerPersonGroupHash.put(key, obj);
							}
							keysNotToDelete.addElement(key);
						}

					}
				}
				catch (ObjectNotFoundException objNotFound)
				{
					System.out.println(objNotFound.getMessage());
				}
			}

			Enumeration keys = manufacturerPersonGroupHash.keys();
			while (keys.hasMoreElements())
			{
				String key = (String)keys.nextElement();
				if (!keysNotToDelete.contains(key))
				{
					Manufacturerpersongroup obj = (Manufacturerpersongroup)manufacturerPersonGroupHash.get(key);
					crit = new Criteria();
					crit.add(ManufacturerpersongroupPeer.MANUFACTURERNAME, obj.getManufacturername());
					crit.add(ManufacturerpersongroupPeer.PERSONGROUP, obj.getPersongroup());
					crit.add(ManufacturerpersongroupPeer.ASSIGNTYPE, PersonGroupBean.COURSE_PERSON_GROUP_ASSIGN_TYPE);
					ManufacturerpersongroupPeer.doDelete(crit);
				}
			}


		}
		catch (Exception x)
		{
			x.printStackTrace();
		}

		try
		{
			Hashtable manufacturerPersonGroupHash = new Hashtable();
			Vector keysNotToDelete = new Vector();

			Criteria crit = new Criteria();
			crit.add(ManufacturerpersongroupPeer.ASSIGNTYPE, PersonGroupBean.FAQ_PERSON_GROUP_ASSIGN_TYPE);
			Iterator objListItr = ManufacturerpersongroupPeer.doSelect(crit).iterator();
			while (objListItr.hasNext())
			{
				Manufacturerpersongroup obj = (Manufacturerpersongroup)objListItr.next();
				String key = obj.getManufacturername() + "-" + obj.getPersongroup();
				manufacturerPersonGroupHash.put(key, obj);
			}

			// loop through all the (active) FAQs.  get the person groups for each FAQ.  then do same as above

			crit = new Criteria();
			crit.add(FaqPeer.EFFECTIVEDATE, new Date(), Criteria.LESS_EQUAL);
			crit.add(FaqPeer.EXPIRATIONDATE, new Date(), Criteria.GREATER_THAN);
			Iterator faqItr = FaqPeer.doSelect(crit).iterator();
			while (faqItr.hasNext())
			{
				FAQBean faq = FAQBean.getFAQ((Faq)faqItr.next());
				try
				{
					ManufacturerBean manufacturer = faq.getManufacturer();
					Iterator itr = faq.getPersonGroups().iterator();
					while (itr.hasNext())
					{
						PersonGroupBean personGroup = (PersonGroupBean)itr.next();
						String key = manufacturer.getNameString() + "-" + personGroup.getNameString();

						System.out.println(key + " - " + manufacturerPersonGroupHash.containsKey(key));

						if (!manufacturerPersonGroupHash.containsKey(key))
						{
							Manufacturerpersongroup obj = new Manufacturerpersongroup();
							obj.setManufacturername(manufacturer.getNameString());
							obj.setPersongroup(personGroup.getNameString());
							obj.setAssigntype(PersonGroupBean.FAQ_PERSON_GROUP_ASSIGN_TYPE);
							obj.save();
							manufacturerPersonGroupHash.put(key, obj);
						}
						keysNotToDelete.addElement(key);
					}
				}
				catch (ObjectNotFoundException objNotFound)
				{
					System.out.println(objNotFound.getMessage());
				}
			}

			Enumeration keys = manufacturerPersonGroupHash.keys();
			while (keys.hasMoreElements())
			{
				String key = (String)keys.nextElement();
				if (!keysNotToDelete.contains(key))
				{
					Manufacturerpersongroup obj = (Manufacturerpersongroup)manufacturerPersonGroupHash.get(key);
					crit = new Criteria();
					crit.add(ManufacturerpersongroupPeer.MANUFACTURERNAME, obj.getManufacturername());
					crit.add(ManufacturerpersongroupPeer.PERSONGROUP, obj.getPersongroup());
					crit.add(ManufacturerpersongroupPeer.ASSIGNTYPE, PersonGroupBean.FAQ_PERSON_GROUP_ASSIGN_TYPE);
					ManufacturerpersongroupPeer.doDelete(crit);
				}
			}





		}
		catch (Exception x)
		{
			x.printStackTrace();
		}

		try
		{
			Hashtable manufacturerPersonGroupHash = new Hashtable();
			Vector keysNotToDelete = new Vector();

			Criteria crit = new Criteria();
			crit.add(ManufacturerpersongroupPeer.ASSIGNTYPE, PersonGroupBean.RESOURCE_PERSON_GROUP_ASSIGN_TYPE);
			Iterator objListItr = ManufacturerpersongroupPeer.doSelect(crit).iterator();
			while (objListItr.hasNext())
			{
				Manufacturerpersongroup obj = (Manufacturerpersongroup)objListItr.next();
				String key = obj.getManufacturername() + "-" + obj.getPersongroup();
				manufacturerPersonGroupHash.put(key, obj);
			}

			// loop through all the (active) Resources.  get the person groups for each Resource.  then do same as above

			crit = new Criteria();
			crit.add(ResourcePeer.ISACTIVE, (short)1);
			crit.add(ResourcePeer.EFFECTIVEDATE, new Date(), Criteria.LESS_EQUAL);
			crit.add(ResourcePeer.EXPIRATIONDATE, new Date(), Criteria.GREATER_THAN);
			Iterator resourceItr = ResourcePeer.doSelect(crit).iterator();
			while (resourceItr.hasNext())
			{
				ResourceBean resource = ResourceBean.getResource((Resource)resourceItr.next());
				try
				{
					ManufacturerBean manufacturer = resource.getManufacturer();
					Iterator itr = resource.getPersonGroups().iterator();
					while (itr.hasNext())
					{
						PersonGroupBean personGroup = (PersonGroupBean)itr.next();
						String key = manufacturer.getNameString() + "-" + personGroup.getNameString();

						System.out.println(key + " - " + manufacturerPersonGroupHash.containsKey(key));

						if (!manufacturerPersonGroupHash.containsKey(key))
						{
							Manufacturerpersongroup obj = new Manufacturerpersongroup();
							obj.setManufacturername(manufacturer.getNameString());
							obj.setPersongroup(personGroup.getNameString());
							obj.setAssigntype(PersonGroupBean.RESOURCE_PERSON_GROUP_ASSIGN_TYPE);
							obj.save();
							manufacturerPersonGroupHash.put(key, obj);
						}
						keysNotToDelete.addElement(key);
					}
				}
				catch (ObjectNotFoundException objNotFound)
				{
					System.out.println(objNotFound.getMessage());
				}
			}

			Enumeration keys = manufacturerPersonGroupHash.keys();
			while (keys.hasMoreElements())
			{
				String key = (String)keys.nextElement();
				if (!keysNotToDelete.contains(key))
				{
					System.out.println("DELETE KEY >" + key);
					Manufacturerpersongroup obj = (Manufacturerpersongroup)manufacturerPersonGroupHash.get(key);
					crit = new Criteria();
					crit.add(ManufacturerpersongroupPeer.MANUFACTURERNAME, obj.getManufacturername());
					crit.add(ManufacturerpersongroupPeer.PERSONGROUP, obj.getPersongroup());
					crit.add(ManufacturerpersongroupPeer.ASSIGNTYPE, PersonGroupBean.RESOURCE_PERSON_GROUP_ASSIGN_TYPE);
					ManufacturerpersongroupPeer.doDelete(crit);
				}
			}
			 */
		}
		catch (Exception x)
		{
			x.printStackTrace();
		}

	}
}
