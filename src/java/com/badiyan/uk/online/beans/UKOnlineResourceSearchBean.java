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

import java.net.*;
import java.io.*;

public class
UKOnlineResourceSearchBean
    extends ResourceSearchBean
    implements java.io.Serializable
{
	// CLASS METHODS

	public static Vector
	getIndexFileNames()
	    throws MalformedURLException, IOException, ClassNotFoundException
	{
	    Vector vec = new Vector();

	    String servletLocation = "http://" + CUBean.getProperty("cu.host") + ":" + CUBean.getProperty("cu.port") + "/" + CUBean.getProperty("cu.clientPackageName") + "/DBSyncTabletUserApproveServlet.html";

	    URL myServlet = new URL(servletLocation);
	    URLConnection servletConnection = myServlet.openConnection();

	    servletConnection.setDoInput(true);
	    servletConnection.setDoOutput(true);
	    servletConnection.setUseCaches(false);
	    servletConnection.setRequestProperty("Content-type","application/x-java-serialized-object");

	    ObjectOutputStream obj_out = new ObjectOutputStream(servletConnection.getOutputStream());

	    String[] command_array = new String[2];
	    command_array[0] = "6";
	    command_array[1] = "dummy";

	    obj_out.writeObject(command_array);
	    obj_out.flush();

	    ObjectInputStream obj_in = new ObjectInputStream(servletConnection.getInputStream());
	    String index_files = (String)obj_in.readObject();

	    obj_in.close();
	    obj_out.close();

	    System.out.println("index_files_str >" + index_files);

	    StringTokenizer st = new StringTokenizer(index_files, "|");
	    while (st.hasMoreTokens())
	    {
		//System.out.println(st.nextToken());
		vec.addElement(st.nextToken());
	    }

	    return vec;
	}

	// INSTANCE VARIABLES

	private BigDecimal one_hundred = new BigDecimal(100);

	private Vector rel_vec = new Vector();
	private HashMap rel_hash = new HashMap(11);
	private Vector score_vec = new Vector();
	private int type = 1;

	// CONSTRUCTORS

	public UKOnlineResourceSearchBean()
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
				Resource obj = (Resource)itr.next();
				if (i >= startValue)
				    vec.addElement(ResourceBean.getResource(obj));
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
			    //System.out.println("getList() score >" + score.intValue());
			    try
			    {
				    //Integer resource_id_key = (Integer)rel_hash.get(score);
				    Vector resource_id_key_vec = (Vector)rel_hash.get(score);
				    Iterator resource_id_key_vec_itr = resource_id_key_vec.iterator();
				    while (resource_id_key_vec_itr.hasNext())
				    {
					Integer resource_id_key = (Integer)resource_id_key_vec_itr.next();
					ResourceBean score_resource = ResourceBean.getResource(resource_id_key.intValue());
					//System.out.println("score_resource >" + score_resource.getLabel());
					if (vec.contains(score_resource))
					{
					    score_sort_vec.addElement(score_resource);
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
	    return ((keyword != null) && (keyword.length() > 0));
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
		System.out.println("search() invoked in UKOnlineResourceSearchBean");

		list = null;

		Criteria crit = new Criteria();

		boolean criteriaEntered = false;

		if (company != null)
			crit.add(ResourcePeer.COMPANY_ID, company.getId());

		if (course != null)
		{
			crit.addJoin(CoursePeer.COURSEID, CourseresourcePeer.COURSEID);
			crit.addJoin(CourseresourcePeer.RESOURCEID, ResourcePeer.RESOURCE_ID);
			crit.add(CoursePeer.COURSEID, course.getId());
		}

		if (certification != null)
		{
			// the user want to search for a specific certification.  no need to pay attention to any other input...
			//Criteria crit = new Criteria();
			//crit.add(RequirementsetPeer.REQUIREMENTSETID, certification.getId());
			//list = CoursePeer.doSelect(crit);
			crit.addJoin(RequirementsetPeer.REQUIREMENTSETID, ResourcereqsetPeer.REQSETID);
			crit.addJoin(ResourcereqsetPeer.RESOURCEID, ResourcePeer.RESOURCE_ID);
			crit.add(RequirementsetPeer.REQUIREMENTSETID, certification.getId());
		}

		if (category != null)
			crit.add(ResourcePeer.CATEGORY_ID, category.getId());

		if (personGroup != null)
		{
			crit.addJoin(PersonGroupPeer.PERSON_GROUP_ID, AudiencePersonGroupPeer.PERSON_GROUP_ID);
			crit.addJoin(AudiencePersonGroupPeer.AUDIENCE_ID, ResourceAudiencePeer.AUDIENCE_ID);
			crit.addJoin(ResourceAudiencePeer.RESOURCE_ID, ResourcePeer.RESOURCE_ID);
			crit.add(PersonGroupPeer.PERSON_GROUP_ID, personGroup.getId());
		}

		if (department != null)
		{
			crit.addJoin(DepartmentPeer.DEPARTMENTID, AudienceDepartmentPeer.DEPARTMENT_ID);
			crit.addJoin(AudienceDepartmentPeer.AUDIENCE_ID, ResourceAudiencePeer.AUDIENCE_ID);
			crit.addJoin(ResourceAudiencePeer.RESOURCE_ID, ResourcePeer.RESOURCE_ID);
			crit.add(DepartmentPeer.DEPARTMENTID, department.getId());
		}

		if (jobTitle != null)
		{
			crit.addJoin(PersonTitlePeer.PERSON_TITLE_ID, AudiencePersonTitlePeer.PERSON_TITLE_ID);
			crit.addJoin(AudiencePersonTitlePeer.AUDIENCE_ID, ResourceAudiencePeer.AUDIENCE_ID);
			crit.addJoin(ResourceAudiencePeer.RESOURCE_ID, ResourcePeer.RESOURCE_ID);
			crit.add(PersonTitlePeer.PERSON_TITLE_ID, jobTitle.getId());
		}

		if (expirationDate != null)
			crit.add(ResourcePeer.EXPIRATIONDATE, expirationDate, Criteria.GREATER_THAN);



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

				String field_1 = "r_title";
				String field_2 = "r_description";
				String field_3 = "r_keyword";

				//QueryParser parser = new QueryParser(field, analyzer);

				QueryParser parser_1 = new QueryParser(field_1, analyzer);
				QueryParser parser_2 = new QueryParser(field_2, analyzer);
				QueryParser parser_3 = new QueryParser(field_3, analyzer);

				//Query query = parser.parse("(\"" + keyword + "\")");

				Query query_1 = parser_1.parse("(\"" + keyword + "\")");
				Query query_2 = parser_2.parse("(\"" + keyword + "\")");
				Query query_3 = parser_3.parse("(\"" + keyword + "\")");

				//System.out.println("Searching for: " + query.toString(field));

				int total_hits = 0;

				HashMap temp_hash = new HashMap(11);
				for (int x = 0; x < 3; x++)
				{
				    //Hits hits = searcher.search(query);
				    Hits hits = null;

				    if (x == 0)
					hits = searcher.search(query_1);
				    else if (x == 1)
					hits = searcher.search(query_2);
				    else
					hits = searcher.search(query_3);

				    total_hits += hits.length();

				    for (int i = 0; i < hits.length(); i++)
				    {
					Document doc = hits.doc(i);
					Field idField = doc.getField("r_id");
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
					    crit.add(ResourcePeer.RESOURCE_ID, id_key.intValue());
					else
					    crit.or(ResourcePeer.RESOURCE_ID, id_key.intValue());
					bool = false;
				    }
				}

				if (total_hits == 0)
				    crit.add(ResourcePeer.RESOURCE_ID, 0);
				else
				{
				    Iterator itr = temp_hash.keySet().iterator();
				    while (itr.hasNext())
				    {
					Integer id_key = (Integer)itr.next();
					Integer score = (Integer)temp_hash.get(id_key);

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
			//crit.add(ResourcePeer.RESOURCENAME, (Object)("%" + keyword + "%"), Criteria.LIKE);
		}

		Iterator itr = resourceTypes.iterator();
		for (int i = 0; itr.hasNext(); i++)
		{
			String type = (String)itr.next();
			if (i == 0)
				crit.add(ResourcePeer.TYPE, type);
			else
				crit.or(ResourcePeer.TYPE, type);
		}

		if (releasedDate != null)
		{
		    Calendar releasedDateCal = Calendar.getInstance();
		    releasedDateCal.setTime(releasedDate);

		    if (releasedDateCompareType == 1)
		    {
			    releasedDateCal.set(Calendar.HOUR_OF_DAY, 23);
			    releasedDateCal.set(Calendar.MINUTE, 59);
			    crit.add(ResourcePeer.EFFECTIVEDATE, releasedDateCal.getTime(), Criteria.LESS_THAN);
		    }
		    else if (releasedDateCompareType == 2)
		    {
			    releasedDateCal.set(Calendar.HOUR_OF_DAY, 0);
			    releasedDateCal.set(Calendar.MINUTE, 0);
			    crit.add(ResourcePeer.EFFECTIVEDATE, releasedDateCal.getTime(), Criteria.GREATER_EQUAL);
		    }

		    /*
			if (releasedDateCompareType == 1)
				crit.add(ResourcePeer.EFFECTIVEDATE, releasedDate, Criteria.LESS_THAN);
			else if (releasedDateCompareType == 2)
				crit.add(ResourcePeer.EFFECTIVEDATE, releasedDate, Criteria.GREATER_THAN);
		     */
		}

		if (onlyActive)
		{
			crit.add(ResourcePeer.ISACTIVE, (short)1);

			if ((releasedDate != null) && (releasedDateCompareType < 3))
			    crit.and(ResourcePeer.EFFECTIVEDATE, new Date(), Criteria.LESS_EQUAL);
			else
			    crit.add(ResourcePeer.EFFECTIVEDATE, new Date(), Criteria.LESS_EQUAL);

			crit.add(ResourcePeer.EXPIRATIONDATE, new Date(), Criteria.GREATER_THAN);
		}

		if (mostActive)
			crit.addDescendingOrderByColumn(ResourcePeer.HITS);

		crit.addAscendingOrderByColumn(sortBy);
		crit.setDistinct();

		System.out.println("crit >" + crit.toString());

		list = ResourcePeer.doSelect(crit);

		if (list != null)
			System.out.println("SIZER >" + list.size());

		searchPerformed = true;
	}
}