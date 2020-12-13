/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.badiyan.uk.online.beans;

import com.badiyan.torque.*;
import com.badiyan.uk.exceptions.*;

import java.beans.*;
import java.util.*;
import java.math.BigDecimal;

import org.apache.torque.*;
import org.apache.torque.util.Criteria;

import com.badiyan.uk.tasks.*;

import com.badiyan.uk.conformance.scorm.rte.util.ScormConstants;

import com.badiyan.uk.beans.*;

/**
 *
 * @author  marlo
 * @version 
 */
public class
EvaluationProgress
	extends CUBean
	implements java.io.Serializable
{
	// CLASS VARIABLES
	
	protected static HashMap<Integer,EvaluationProgress> progress_hash = new HashMap<Integer,EvaluationProgress>(11);
	
	// CLASS METHODS
	
	public static EvaluationProgress
	getProgress(int _id)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{	
		Integer key = new Integer(_id);
		EvaluationProgress progress = (EvaluationProgress)progress_hash.get(key);
		if (progress == null) {
			Criteria crit = new Criteria();
			crit.add(EvaluationProgressDbPeer.EVALUATION_PROGRESS_DB_ID, _id);
			List objList = EvaluationProgressDbPeer.doSelect(crit);
			if (objList.size() == 0)
				throw new ObjectNotFoundException("Could not locate Evaluation Progress with id: " + _id);
		
			progress = EvaluationProgress.getProgress((EvaluationProgressDb)objList.get(0));
		}
		
		return progress;
	}
	
	private static EvaluationProgress
	getProgress(EvaluationProgressDb _progress)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		Integer key = new Integer(_progress.getEvaluationProgressDbId());
		EvaluationProgress progress = (EvaluationProgress)progress_hash.get(key);
		if (progress == null) {
			progress = new EvaluationProgress(_progress);
			progress_hash.put(key, progress);
		}
		
		return progress;
	}
	
	public static EvaluationProgress
	getProgress(UKOnlinePersonBean _client, AssessmentBean _assessment, String _session)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		Criteria crit = new Criteria();
		crit.add(EvaluationProgressDbPeer.CLIENT_ID, _client.getId());
		crit.add(EvaluationProgressDbPeer.ASSESSMENT_ID, _assessment.getId());
		crit.add(EvaluationProgressDbPeer.SESSION, _session);
		List objList = EvaluationProgressDbPeer.doSelect(crit);
		if (objList.size() == 1) {
			return EvaluationProgress.getProgress((EvaluationProgressDb)objList.get(0));
		} else {
			throw new ObjectNotFoundException("Could not locate Evaluation Progress");
		}
	}
	
	public static EvaluationProgress
	getProgress(UKOnlinePersonBean _client, AssessmentBean _assessment, Date _date)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		Calendar start_of_day = Calendar.getInstance();
		start_of_day.setTime(_date);
		start_of_day.set(Calendar.HOUR_OF_DAY, 0);
		start_of_day.set(Calendar.MINUTE, 0);
		start_of_day.set(Calendar.SECOND, 0);

		Calendar end_of_day = Calendar.getInstance();
		end_of_day.setTime(_date);
		end_of_day.set(Calendar.HOUR_OF_DAY, 23);
		end_of_day.set(Calendar.MINUTE, 59);
		end_of_day.set(Calendar.SECOND, 59);
		
		Criteria crit = new Criteria();
		crit.add(EvaluationProgressDbPeer.CLIENT_ID, _client.getId());
		crit.add(EvaluationProgressDbPeer.ASSESSMENT_ID, _assessment.getId());
		crit.add(EvaluationProgressDbPeer.PROGRESS_DATE, start_of_day.getTime(), Criteria.GREATER_EQUAL);
		crit.and(EvaluationProgressDbPeer.PROGRESS_DATE, end_of_day.getTime(), Criteria.LESS_EQUAL);
		List objList = EvaluationProgressDbPeer.doSelect(crit);
		if (objList.size() == 1) {
			return EvaluationProgress.getProgress((EvaluationProgressDb)objList.get(0));
		} else {
			throw new ObjectNotFoundException("Could not locate Evaluation Progress");
		}
	}
	
	public static Vector
	getProgress(UKOnlinePersonBean _client, AssessmentBean _assessment)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		Vector vec = new Vector();
		Criteria crit = new Criteria();
		crit.add(EvaluationProgressDbPeer.CLIENT_ID, _client.getId());
		crit.add(EvaluationProgressDbPeer.ASSESSMENT_ID, _assessment.getId());
		crit.addAscendingOrderByColumn(EvaluationProgressDbPeer.PROGRESS_DATE);
		Iterator objItr = EvaluationProgressDbPeer.doSelect(crit).iterator();
		while (objItr.hasNext()) {
			vec.addElement(EvaluationProgress.getProgress((EvaluationProgressDb)objItr.next()));
		}
		return vec;
	}
	
	public static boolean
	hasCompletedProgress(UKOnlinePersonBean _client, AssessmentBean _assessment)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		Vector vec = new Vector();
		Criteria crit = new Criteria();
		crit.add(EvaluationProgressDbPeer.CLIENT_ID, _client.getId());
		crit.add(EvaluationProgressDbPeer.ASSESSMENT_ID, _assessment.getId());
		//System.out.println("hasCompletedProgress crit >" + crit.toString());
		Iterator objItr = EvaluationProgressDbPeer.doSelect(crit).iterator();
		while (objItr.hasNext()) {
			EvaluationProgress progress = EvaluationProgress.getProgress((EvaluationProgressDb)objItr.next());
			if (progress.isCompleted())
				return true;
		}
		return false;
	}
	
    // INSTANCE VARIABLES
	
	private EvaluationProgressDb progress;
	
	// CONSTRUCTORS
	
	/*
	 * The default no-arg constructor.
	 */
	public
	EvaluationProgress()
	{
	    progress = new EvaluationProgressDb();
	    isNew = true;
	}
	
	public
	EvaluationProgress(EvaluationProgressDb _progress)
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		progress = _progress;
		isNew = false;
	}

	// INSTANCE METHODS
	
	public int
	getId()
	{
		return progress.getEvaluationProgressDbId();
	}
	
	public UKOnlinePersonBean
	getClient()
		throws TorqueException, ObjectNotFoundException, UniqueObjectNotFoundException
	{
		return (UKOnlinePersonBean)UKOnlinePersonBean.getPerson(progress.getClientId());
	}
	
	protected void
	insertObject()
		throws Exception
	{
		progress.save();
	}
	
	public void
	setEvaluation(AssessmentBean _assessment)
		throws TorqueException
	{
		progress.setAssessmentId(_assessment.getId());
	}
	
	public void
	setClient(UKOnlinePersonBean _person)
		throws TorqueException
	{
		progress.setClientId(_person.getId());
	}
	
	public void
	setCompany(UKOnlineCompanyBean _company)
		throws TorqueException
	{
		progress.setCompanyId(_company.getId());
	}
	
	public void
	setSession(String _session)
		throws IllegalValueException
	{
		progress.setSession(_session);
	}
	
	public void
	setProgressDate(Date _date)
	{
		progress.setProgressDate(_date);
	}
	
	public Date
	getProgressDate()
	{
		return progress.getProgressDate();
	}
	
	public void
	setCompleted()
	{
		progress.setIsCompleted((short)1);
	}
	
	public boolean
	isCompleted()
	{
		return (progress.getIsCompleted() == (short)1);
	}
	
	public String toSNote() throws TorqueException, ObjectNotFoundException {
		String str = "";
		Criteria crit = new Criteria();
		crit.addJoin(StemPeer.STEMID, EvaluationInteractionDbPeer.STEM_ID);
		crit.add(EvaluationInteractionDbPeer.EVALUATION_PROGRESS_DB_ID, this.getId());
		crit.addAscendingOrderByColumn(StemPeer.DISPLAYORDER);
		Iterator itr = EvaluationInteractionDbPeer.doSelect(crit).iterator();
		while (itr.hasNext()) {
			EvaluationInteractionDb obj = (EvaluationInteractionDb)itr.next();
			StemBean stem = StemBean.getStem(obj.getStemId());
			String interactionResponse = "";
			if (obj.getSelectedDistractor() > 0) {
				DistractorBean selectedDistractor = DistractorBean.getDistractor(obj.getSelectedDistractor());
				interactionResponse = selectedDistractor.getNameString();
			}
			if (obj.getFillInTheBlankResponse() != null) {
				if (!obj.getFillInTheBlankResponse().isEmpty()) {
					interactionResponse = obj.getFillInTheBlankResponse();
				}
			}
				
			if (str.isEmpty())
				str = (stem.getLabel() + ": " + interactionResponse);
			else
				str += ("<br />" + stem.getLabel() + ": " + interactionResponse);
		}
		System.out.println("S Note >" + str);
		return str;
	}
	
	public void
	recordInteraction(StemBean _stem, DistractorBean _selectedDistractor) throws TorqueException, Exception {
		
		EvaluationInteractionDb interactionObj = null;
				
		// is there already a record for this interaction???
		
		Criteria crit = new Criteria();
		crit.add(EvaluationInteractionDbPeer.EVALUATION_PROGRESS_DB_ID, this.getId());
		crit.add(EvaluationInteractionDbPeer.STEM_ID, _stem.getId());
		List objList = EvaluationInteractionDbPeer.doSelect(crit);
		if (objList.size() == 0) {
			// no interaction recorded yet
			interactionObj = new EvaluationInteractionDb();
			interactionObj.setEvaluationProgressDbId(this.getId());
			interactionObj.setStemId(_stem.getId());
		} else {
			interactionObj = (EvaluationInteractionDb)objList.get(0);
		}
		
		interactionObj.setSelectedDistractor(_selectedDistractor.getId());
		interactionObj.save();
	}
	
	public void
	recordInteraction(StemBean _stem, String _fitb_str) throws TorqueException, Exception {
		
		EvaluationInteractionDb interactionObj = null;
				
		// is there already a record for this interaction???
		
		Criteria crit = new Criteria();
		crit.add(EvaluationInteractionDbPeer.EVALUATION_PROGRESS_DB_ID, this.getId());
		crit.add(EvaluationInteractionDbPeer.STEM_ID, _stem.getId());
		List objList = EvaluationInteractionDbPeer.doSelect(crit);
		if (objList.size() == 0) {
			// no interaction recorded yet
			interactionObj = new EvaluationInteractionDb();
			interactionObj.setEvaluationProgressDbId(this.getId());
			interactionObj.setStemId(_stem.getId());
		} else {
			interactionObj = (EvaluationInteractionDb)objList.get(0);
		}
		
		interactionObj.setFillInTheBlankResponse(_fitb_str);
		interactionObj.save();
	}
	
	public boolean
	hasInteraction(StemBean _stem) throws TorqueException {
		
		Criteria crit = new Criteria();
		crit.add(EvaluationInteractionDbPeer.EVALUATION_PROGRESS_DB_ID, this.getId());
		crit.add(EvaluationInteractionDbPeer.STEM_ID, _stem.getId());
		List objList = EvaluationInteractionDbPeer.doSelect(crit);
		if (objList.size() == 0) {
			return false;
		}
		
		return true;
	}
	
	public EvaluationInteractionDb
	retrieveInteraction(StemBean _stem) throws TorqueException, ObjectNotFoundException {
		
		EvaluationInteractionDb interactionObj = null;
				
		// is there already a record for this interaction???
		
		Criteria crit = new Criteria();
		crit.add(EvaluationInteractionDbPeer.EVALUATION_PROGRESS_DB_ID, this.getId());
		crit.add(EvaluationInteractionDbPeer.STEM_ID, _stem.getId());
		List objList = EvaluationInteractionDbPeer.doSelect(crit);
		if (objList.size() == 0) {
			throw new ObjectNotFoundException("Interaction not found for stem: " + _stem.getId());
		} else {
			interactionObj = (EvaluationInteractionDb)objList.get(0);
		}
		
		return interactionObj;
	}
	
	protected void
	updateObject()
		throws ObjectAlreadyExistsException, IllegalValueException, Exception
	{
		progress.save();
	}
	
	public int
	getWellnessSurveyScore() throws TorqueException, ObjectNotFoundException {
		
		int score = 0;
		
		Criteria crit = new Criteria();
		crit.add(EvaluationInteractionDbPeer.EVALUATION_PROGRESS_DB_ID, this.getId());
		Iterator itr = EvaluationInteractionDbPeer.doSelect(crit).iterator();
		while (itr.hasNext()) {
			EvaluationInteractionDb obj = (EvaluationInteractionDb)itr.next();
			try {
				DistractorBean distractorObj = DistractorBean.getDistractor(obj.getSelectedDistractor());
				score += Integer.parseInt(distractorObj.getNameString());
			} catch (Exception x) {	}
		}
		
		return score;
	}
}