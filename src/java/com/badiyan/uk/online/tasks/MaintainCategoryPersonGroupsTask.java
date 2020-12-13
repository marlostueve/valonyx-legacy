package com.badiyan.uk.online.tasks;

import com.badiyan.torque.*;
import com.badiyan.uk.beans.*;
import com.badiyan.uk.exceptions.*;

import java.util.*;

import org.apache.torque.*;
import org.apache.torque.util.Criteria;

public class
MaintainCategoryPersonGroupsTask
	extends TimerTask
{
	// CONSTRUCTORS

	public
	MaintainCategoryPersonGroupsTask()
	{
	}

	// INSTANCE METHODS

	public void
	run()
	{
		System.out.println("run() invoked in MaintainCategoryPersonGroupsTask - " + this);
		
		try
		{
			/* strategy: loop through all active courses, get their audiences,
			 * get person group(s) for that audience, make sure there's an
			 * entry
			 */
			
			/*

			// first get all the Categorypersongroup objects

			Hashtable categoryPersonGroupHash = new Hashtable();
			Vector keysNotToDelete = new Vector();

			Criteria crit = new Criteria();
			crit.add(CategorypersongroupPeer.ASSIGNTYPE, PersonGroupBean.COURSE_PERSON_GROUP_ASSIGN_TYPE);
			Iterator objListItr = CategorypersongroupPeer.doSelect(crit).iterator();
			while (objListItr.hasNext())
			{
				Categorypersongroup obj = (Categorypersongroup)objListItr.next();
				String key = obj.getCategoryname() + "-" + obj.getPersongroup();
				categoryPersonGroupHash.put(key, obj);
				System.out.println("KEY FOUND >" + key);
			}

			crit = new Criteria();
			crit.add(CoursePeer.COURSESTATUS, CourseBean.DEFAULT_COURSE_STATUS);
			Iterator courseListItr = CoursePeer.doSelect(crit).iterator();
			while (courseListItr.hasNext())
			{
				CourseBean course = CourseBean.getCourse((Course)courseListItr.next());
				try
				{
					CategoryBean category = course.getCategory();
					Iterator audienceItr = course.getAudiences().iterator();
					while (audienceItr.hasNext())
					{
						AudienceBean audience = (AudienceBean)audienceItr.next();
						Iterator itr = audience.getPersonGroups().iterator();
						while (itr.hasNext())
						{
							PersonGroupBean personGroup = (PersonGroupBean)itr.next();
							String key = category.getNameString() + "-" + personGroup.getNameString();

							if (!categoryPersonGroupHash.containsKey(key))
							{
								Categorypersongroup obj = new Categorypersongroup();
								obj.setCategoryname(category.getNameString());
								obj.setPersongroup(personGroup.getNameString());
								obj.setAssigntype(PersonGroupBean.COURSE_PERSON_GROUP_ASSIGN_TYPE);
								obj.save();
								categoryPersonGroupHash.put(key, obj);
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
					CategoryBean category = certification.getCategory();
					Iterator audienceItr = certification.getAudiences().iterator();
					while (audienceItr.hasNext())
					{
						AudienceBean audience = (AudienceBean)audienceItr.next();
						Iterator itr = audience.getPersonGroups().iterator();
						while (itr.hasNext())
						{
							PersonGroupBean personGroup = (PersonGroupBean)itr.next();
							String key = category.getNameString() + "-" + personGroup.getNameString();

							System.out.println(key + " - " + categoryPersonGroupHash.containsKey(key));

							if (!categoryPersonGroupHash.containsKey(key))
							{
								Categorypersongroup obj = new Categorypersongroup();
								obj.setCategoryname(category.getNameString());
								obj.setPersongroup(personGroup.getNameString());
								obj.setAssigntype(PersonGroupBean.COURSE_PERSON_GROUP_ASSIGN_TYPE);
								obj.save();
								categoryPersonGroupHash.put(key, obj);
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

			Enumeration keys = categoryPersonGroupHash.keys();
			while (keys.hasMoreElements())
			{
				String key = (String)keys.nextElement();
				if (!keysNotToDelete.contains(key))
				{
					System.out.println("DELETE KEY >" + key);
					Categorypersongroup obj = (Categorypersongroup)categoryPersonGroupHash.get(key);
					crit = new Criteria();
					crit.add(CategorypersongroupPeer.CATEGORYNAME, obj.getCategoryname());
					crit.add(CategorypersongroupPeer.PERSONGROUP, obj.getPersongroup());
					crit.add(CategorypersongroupPeer.ASSIGNTYPE, PersonGroupBean.COURSE_PERSON_GROUP_ASSIGN_TYPE);
					CategorypersongroupPeer.doDelete(crit);
				}
			}


		}
		catch (Exception x)
		{
			x.printStackTrace();
		}

		try
		{
			Hashtable categoryPersonGroupHash = new Hashtable();
			Vector keysNotToDelete = new Vector();

			Criteria crit = new Criteria();
			crit.add(CategorypersongroupPeer.ASSIGNTYPE, PersonGroupBean.FAQ_PERSON_GROUP_ASSIGN_TYPE);
			Iterator objListItr = CategorypersongroupPeer.doSelect(crit).iterator();
			while (objListItr.hasNext())
			{
				Categorypersongroup obj = (Categorypersongroup)objListItr.next();
				String key = obj.getCategoryname() + "-" + obj.getPersongroup();
				categoryPersonGroupHash.put(key, obj);
				System.out.println("FAQ KEY FOUND >" + key);
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
					CategoryBean category = faq.getCategory();
					Iterator itr = faq.getPersonGroups().iterator();
					while (itr.hasNext())
					{
						PersonGroupBean personGroup = (PersonGroupBean)itr.next();
						String key = category.getNameString() + "-" + personGroup.getNameString();

						System.out.println(key + " - " + categoryPersonGroupHash.containsKey(key));

						if (!categoryPersonGroupHash.containsKey(key))
						{
							Categorypersongroup obj = new Categorypersongroup();
							obj.setCategoryname(category.getNameString());
							obj.setPersongroup(personGroup.getNameString());
							obj.setAssigntype(PersonGroupBean.FAQ_PERSON_GROUP_ASSIGN_TYPE);
							obj.save();
							categoryPersonGroupHash.put(key, obj);
						}
						keysNotToDelete.addElement(key);
					}
				}
				catch (ObjectNotFoundException objNotFound)
				{
					System.out.println(objNotFound.getMessage());
				}
			}

			Enumeration keys = categoryPersonGroupHash.keys();
			while (keys.hasMoreElements())
			{
				String key = (String)keys.nextElement();
				if (!keysNotToDelete.contains(key))
				{
					System.out.println("DELETE KEY >" + key);
					Categorypersongroup obj = (Categorypersongroup)categoryPersonGroupHash.get(key);
					crit = new Criteria();
					crit.add(CategorypersongroupPeer.CATEGORYNAME, obj.getCategoryname());
					crit.add(CategorypersongroupPeer.PERSONGROUP, obj.getPersongroup());
					crit.add(CategorypersongroupPeer.ASSIGNTYPE, PersonGroupBean.FAQ_PERSON_GROUP_ASSIGN_TYPE);
					CategorypersongroupPeer.doDelete(crit);
				}
			}


		}
		catch (Exception x)
		{
			x.printStackTrace();
		}

		try
		{
			Hashtable categoryPersonGroupHash = new Hashtable();
			Vector keysNotToDelete = new Vector();

			Criteria crit = new Criteria();
			crit.add(CategorypersongroupPeer.ASSIGNTYPE, PersonGroupBean.RESOURCE_PERSON_GROUP_ASSIGN_TYPE);
			Iterator objListItr = CategorypersongroupPeer.doSelect(crit).iterator();
			while (objListItr.hasNext())
			{
				Categorypersongroup obj = (Categorypersongroup)objListItr.next();
				String key = obj.getCategoryname() + "-" + obj.getPersongroup();
				categoryPersonGroupHash.put(key, obj);
				System.out.println("FAQ KEY FOUND >" + key);
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
					CategoryBean category = resource.getCategory();
					Iterator itr = resource.getPersonGroups().iterator();
					while (itr.hasNext())
					{
						PersonGroupBean personGroup = (PersonGroupBean)itr.next();
						String key = category.getNameString() + "-" + personGroup.getNameString();

						System.out.println(key + " - " + categoryPersonGroupHash.containsKey(key));

						if (!categoryPersonGroupHash.containsKey(key))
						{
							Categorypersongroup obj = new Categorypersongroup();
							obj.setCategoryname(category.getNameString());
							obj.setPersongroup(personGroup.getNameString());
							obj.setAssigntype(PersonGroupBean.RESOURCE_PERSON_GROUP_ASSIGN_TYPE);
							obj.save();
							categoryPersonGroupHash.put(key, obj);
						}
						keysNotToDelete.addElement(key);
					}
				}
				catch (ObjectNotFoundException objNotFound)
				{
					System.out.println(objNotFound.getMessage());
				}
			}

			Enumeration keys = categoryPersonGroupHash.keys();
			while (keys.hasMoreElements())
			{
				String key = (String)keys.nextElement();
				if (!keysNotToDelete.contains(key))
				{
					System.out.println("DELETE KEY >" + key);
					Categorypersongroup obj = (Categorypersongroup)categoryPersonGroupHash.get(key);
					crit = new Criteria();
					crit.add(CategorypersongroupPeer.CATEGORYNAME, obj.getCategoryname());
					crit.add(CategorypersongroupPeer.PERSONGROUP, obj.getPersongroup());
					crit.add(CategorypersongroupPeer.ASSIGNTYPE, PersonGroupBean.RESOURCE_PERSON_GROUP_ASSIGN_TYPE);
					CategorypersongroupPeer.doDelete(crit);
				}
			}
			 *
			 */
		}
		catch (Exception x)
		{
			x.printStackTrace();
		}

	}
}
