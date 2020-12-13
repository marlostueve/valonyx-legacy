/*
 * UKOnlineCourseSearchBean.java
 *
 * Created on February 23, 2007, 5:05 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.badiyan.uk.online.beans;

import com.badiyan.torque.*;
import com.badiyan.uk.beans.*;
import com.badiyan.uk.exceptions.*;

import java.io.*;
import java.math.*;
import java.util.*;

import org.apache.torque.*;
import org.apache.torque.util.Criteria;

import org.apache.lucene.search.*;
import org.apache.lucene.analysis.*;
import org.apache.lucene.queryParser.*;
import org.apache.lucene.document.*;
import org.apache.lucene.analysis.standard.StandardAnalyzer;

public class
UKOnlineCourseSearchBean
    extends CourseSearchBean
    implements java.io.Serializable
{
	// INSTANCE VARIABLES

	private BigDecimal one_hundred = new BigDecimal(100);

	private Vector rel_vec = new Vector();
	private HashMap rel_hash = new HashMap(11);
	private Vector score_vec = new Vector();
	private int type = 3;

	// CONSTRUCTORS

	public UKOnlineCourseSearchBean()
	{
		super();
	}

	// INSTANCE METHODS

	private void
	addRelScore(Integer _score)
	{
	    System.out.println("ADDING score >" + _score.intValue());

	    int insert_position = 0;

	    for (int i = 0; i < rel_vec.size(); i++)
	    {
		Integer current_score = (Integer)rel_vec.elementAt(i);

		System.out.println("_score.intValue() < current_score.intValue() >" + (_score.intValue() < current_score.intValue()));

		if (_score.intValue() < current_score.intValue())
		    insert_position = i + 1;
		else
		    break;
	    }
	    System.out.println("insert_position >" + insert_position);
	    rel_vec.insertElementAt(_score, insert_position);
	}

	public void
	clearSearchCriteria()
	{
		super.clearSearchCriteria();
		rel_vec = new Vector();
		rel_hash = new HashMap(11);
		score_vec = new Vector();
	}

	public Enumeration
	getList()
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException, IllegalValueException
	{
		Vector vec = new Vector();
		int endValue = currentPage * numToDisplayPerPage;
		int startValue = endValue - numToDisplayPerPage;
		if (list != null)
		{
			//searchPerformed = true;

			Iterator itr = list.iterator();

			int i = 0;
			for (; (i < endValue) && itr.hasNext(); i++)
			{
				Course obj = (Course)itr.next();
				if (i >= startValue)
				    vec.addElement(CourseBean.getCourse(obj));
			}
		}

		Vector already_processed_scores = new Vector();
		if (keyword == null)
		    return vec.elements();
		else
		{
		    Vector score_sort_vec = new Vector();
		    Iterator sort_itr = rel_vec.iterator();
		    while (sort_itr.hasNext())
		    {
			Integer score = (Integer)sort_itr.next();
			if (!already_processed_scores.contains(score))
			{
			    already_processed_scores.addElement(score);
			    try
			    {
				    //Integer course_id_key = (Integer)rel_hash.get(score);
				    Vector course_id_key_vec = (Vector)rel_hash.get(score);
				    Iterator course_id_key_vec_itr = course_id_key_vec.iterator();
				    while (course_id_key_vec_itr.hasNext())
				    {
					Integer course_id_key = (Integer)course_id_key_vec_itr.next();
					CourseBean score_course = CourseBean.getCourse(course_id_key.intValue());
					if (vec.contains(score_course))
					{
					    score_sort_vec.addElement(score_course);
					    score_vec.addElement(score);
					}
				    }
			    }
			    catch (Exception x)
			    {
				x.printStackTrace();
			    }
			}
		    }

		    return score_sort_vec.elements();
		}
	}

	public Vector
	getRelScores()
	{
	    Vector vec = new Vector();
	    if (score_vec != null && this.useKeywordSearch())
	    {
		//BigDecimal factor = this.getScoreFactor();
		Iterator itr = score_vec.iterator();
		while (itr.hasNext())
		{
		    Integer score = (Integer)itr.next();
		    //BigDecimal adj_score = score.divide(factor);
		    //vec.addElement(adj_score.intValue() + "");
		    vec.add(score.toString());
		}
	    }

	    return vec;
	}

	public boolean
	useKeywordSearch()
	{
	    return (keyword != null);
	}

	private BigDecimal
	getScoreFactor()
	{

	    BigDecimal largest_score = (BigDecimal)score_vec.elementAt(0);
	    return largest_score.divide(one_hundred);
	}

	public int
	getResultsType()
	{
	    return type;
	}

	public void
	setResultsType(int _type)
	{
	    type = _type;
	}

	public void
	search()
		throws TorqueException
	{
	    //System.out.println("search() invoked in CourseSearchBean");

	    //this.invalidateSearchResults();

	    Criteria crit = new Criteria();

	    if (status != null)
	    {
		    crit.add(CoursePeer.COURSESTATUS, status);
	    }

	    if (releasedDate != null)
	    {
		    Calendar releasedDateCal = Calendar.getInstance();
		    releasedDateCal.setTime(releasedDate);

		    if (releasedDateCompareType == 1)
		    {
			    releasedDateCal.set(Calendar.HOUR_OF_DAY, 23);
			    releasedDateCal.set(Calendar.MINUTE, 59);
			    crit.add(CoursePeer.RELEASEDDATE, releasedDateCal.getTime(), Criteria.LESS_THAN);
		    }
		    else if (releasedDateCompareType == 2)
		    {
			    releasedDateCal.set(Calendar.HOUR_OF_DAY, 0);
			    releasedDateCal.set(Calendar.MINUTE, 0);
			    crit.add(CoursePeer.RELEASEDDATE, releasedDateCal.getTime(), Criteria.GREATER_EQUAL);
		    }
	    }

	    if (expirationDate != null)
	    {
		    if (expirationDateCompareType == 1)
			    crit.add(CoursePeer.OFFDATE, expirationDate, Criteria.LESS_THAN);
		    else if (expirationDateCompareType == 2)
			    crit.add(CoursePeer.OFFDATE, expirationDate, Criteria.GREATER_THAN);
	    }

	    Iterator itr = resourceTypes.iterator();
	    for (int i = 0; itr.hasNext(); i++)
	    {
		    String type = (String)itr.next();
		    if (i == 0)
			    crit.add(CoursePeer.COURSETYPE, type);
		    else
			    crit.or(CoursePeer.COURSETYPE, type);
	    }

	    if (descriptionKeyword != null)
	    {
		    String searchString = "%" + descriptionKeyword + "%";
		    crit.add(CoursePeer.COURSECOMMENT, (Object)searchString, Criteria.LIKE);
	    }

	    if (course != null)
	    {
		    // the user wants to search for a specific course.  no need to pay attention to any other input...
		    crit.add(CoursePeer.COURSEID, course.getId());
		    list = CoursePeer.doSelect(crit);
		    return;
	    }

	    if ((keyword != null) && (keyword.length() > 0))
	    {
		boolean bool = true;
		try
		{
		    String indexPath = CUBean.getProperty("cu.realPath") + File.separator + CUBean.getProperty("cu.resourcesFolder") + File.separator + "index";
		    if (!CUBean.isMasterServer())
			indexPath = CUBean.getProperty("cu.tabletContentPath") + File.separator + "index";

		    Searcher searcher = new IndexSearcher(indexPath);
		    Analyzer analyzer = new StandardAnalyzer();

		    //String field = "keyword";

		    String field_1 = "c_title";
		    String field_2 = "c_comment";
		    String field_3 = "c_description";
		    String field_4 = "c_keyword";

		    //QueryParser parser = new QueryParser(field, analyzer);

		    QueryParser parser_1 = new QueryParser(field_1, analyzer);
		    QueryParser parser_2 = new QueryParser(field_2, analyzer);
		    QueryParser parser_3 = new QueryParser(field_3, analyzer);
		    QueryParser parser_4 = new QueryParser(field_4, analyzer);

		    //Query query = parser.parse("(\"" + keyword + "\")");

		    Query query_1 = parser_1.parse("(\"" + keyword + "\")");
		    Query query_2 = parser_2.parse("(\"" + keyword + "\")");
		    Query query_3 = parser_3.parse("(\"" + keyword + "\")");
		    Query query_4 = parser_4.parse("(\"" + keyword + "\")");

		    //System.out.println("Searching for: " + query.toString(field));

		    int total_hits = 0;

		    HashMap temp_hash = new HashMap(11);
		    for (int x = 0; x < 4; x++)
		    {
			//Hits hits = searcher.search(query);
			Hits hits = null;

			if (x == 0)
			    hits = searcher.search(query_1);
			else if (x == 1)
			    hits = searcher.search(query_2);
			else if (x == 2)
			    hits = searcher.search(query_3);
			else
			    hits = searcher.search(query_4);

			total_hits += hits.length();

			for (int i = 0; i < hits.length(); i++)
			{
			    Document doc = hits.doc(i);
			    Field idField = doc.getField("c_id");
			    Integer id_key = new Integer(idField.stringValue());

			    BigDecimal score_bd = new BigDecimal(hits.score(i) * 100.0f).setScale(0, BigDecimal.ROUND_HALF_UP);
			    Integer score = new Integer(score_bd.intValue());

			    System.out.println("FOUND SCORE >" + score.intValue());

			    // get any existing score for this id

			    Integer existing_score = (Integer)temp_hash.get(id_key);
			    if (existing_score == null)
				temp_hash.put(id_key, score);
			    else
			    {
				System.out.println("existing score found for " + id_key.intValue() + " >" + existing_score.intValue());
				Integer new_score = new Integer(score.intValue() + existing_score.intValue());
				temp_hash.put(id_key, new_score);
				System.out.println("mod score >" + new_score.intValue());
			    }

			    if (bool)
				crit.add(CoursePeer.COURSEID, id_key.intValue());
			    else
				crit.or(CoursePeer.COURSEID, id_key.intValue());
			    bool = false;
			}
		    }

		    if (total_hits == 0)
			crit.add(CoursePeer.COURSEID, 0);
		    else
		    {
			Iterator itrx = temp_hash.keySet().iterator();
			while (itrx.hasNext())
			{
			    Integer id_key = (Integer)itrx.next();
			    Integer score = (Integer)temp_hash.get(id_key);

			    //this.addRelScore(score);
			    //rel_hash.put(score, id_key);

			    this.addRelScore(score);
			    Vector id_key_vec = (Vector)rel_hash.get(score);
			    if (id_key_vec == null)
			    {
				id_key_vec = new Vector();
				rel_hash.put(score, id_key_vec);
			    }
			    //rel_hash.put(score, id_key);
			    id_key_vec.addElement(id_key);
			}
		    }


		}
		catch (Exception x)
		{
		    x.printStackTrace();
		}
	    }

	    if (category != null)
		crit.add(CoursePeer.CATEGORY_ID, category.getId());

	    if (personGroup != null)
	    {
		    //System.out.println("personGroup - " + personGroup);
		    crit.addJoin(CoursePeer.COURSEID, CourseaudiencePeer.COURSEID);
		    crit.addJoin(CourseaudiencePeer.AUDIENCEID, AudiencePersonGroupPeer.AUDIENCE_ID);
		    crit.addJoin(AudiencePersonGroupPeer.PERSON_GROUP_ID, PersonGroupPeer.PERSON_GROUP_ID);

		    crit.add(PersonGroupPeer.PERSON_GROUP_ID, personGroup.getId());
	    }

	    if (department != null)
	    {
		    crit.addJoin(DepartmentPeer.DEPARTMENTID, AudienceDepartmentPeer.DEPARTMENT_ID);
		    crit.addJoin(AudienceDepartmentPeer.AUDIENCE_ID, CourseaudiencePeer.AUDIENCEID);
		    crit.addJoin(CourseaudiencePeer.COURSEID, CoursePeer.COURSEID);
		    crit.add(DepartmentPeer.DEPARTMENTID, department.getId());
	    }
	    if (jobTitle != null)
	    {
		    crit.addJoin(PersonTitlePeer.PERSON_TITLE_ID, AudiencePersonTitlePeer.PERSON_TITLE_ID);
		    crit.addJoin(AudiencePersonTitlePeer.AUDIENCE_ID, CourseaudiencePeer.AUDIENCEID);
		    crit.addJoin(CourseaudiencePeer.COURSEID, CoursePeer.COURSEID);
		    crit.add(PersonTitlePeer.PERSON_TITLE_ID, jobTitle.getId());
	    }
	    if (company != null)
	    {
		    crit.add(CoursePeer.COMPANY_ID, company.getId());
	    }

	    crit.setDistinct();
	    crit.addAscendingOrderByColumn(CoursePeer.COURSENAME);

	    System.out.println("");
	    System.out.println("");
	    System.out.println("critxx >" + crit.toString());
	    System.out.println("");
	    System.out.println("");

	    Vector nameStrings = new Vector();
	    list = new Vector();
	    Iterator objItr = CoursePeer.doSelect(crit).iterator();
	    while (objItr.hasNext())
	    {
		    Course obj = (Course)objItr.next();
		    if (!nameStrings.contains(obj.getCoursename()))
		    {
			    nameStrings.addElement(obj.getCoursename());
			    list.add(obj);
		    }
	    }

	    //list = CoursePeer.doSelect(crit);

	    if (list != null)
		    System.out.println("SIZER >" + list.size());

	    searchPerformed = true;
	}
}