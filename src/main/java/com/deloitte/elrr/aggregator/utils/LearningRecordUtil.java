package com.deloitte.elrr.aggregator.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.deloitte.elrr.aggregator.rules.VerbIdConstants;
import com.deloitte.elrr.entity.LearningRecord;
import com.deloitte.elrr.entity.LearningResource;
import com.deloitte.elrr.entity.Person;
import com.deloitte.elrr.entity.types.LearningStatus;
import com.deloitte.elrr.exception.RuntimeServiceException;
import com.deloitte.elrr.jpa.svc.LearningRecordSvc;
import com.yetanalytics.xapi.model.Activity;
import com.yetanalytics.xapi.model.Result;
import com.yetanalytics.xapi.model.Score;
import com.yetanalytics.xapi.model.Verb;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class LearningRecordUtil {

	@Autowired
	private LearningRecordSvc learningRecordService;

	/**
	 * @param Activity
	 * @param Person
	 * @param verb
	 * @param Result
	 * @param LearningResource
	 * @return LearningRccord
	 */
	public LearningRecord processLearningRecord(final Activity activity, final Person person, final Verb verb,
			final Result result, final LearningResource learningResource) {

		LearningRecord learningRecord = null;

		try {

			log.info("Process learning record.");

			// Get LearningRecord
			learningRecord = learningRecordService.findByPersonIdAndLearningResourceId(person.getId(),
					learningResource.getId());

			// If LearningRecord doesn't exist
			if (learningRecord == null) {

				learningRecord = createLearningRecord(person, learningResource, verb, result);

				// If learningRecord already exists
			} else {

				learningRecord = updateLearningRecord(learningRecord, verb, result);
			}

		} catch (RuntimeServiceException e) {
			throw e;
		}

		return learningRecord;
	}

	/**
	 * @param Person
	 * @param learningResource
	 * @param verb
	 * @param Result
	 * @return LearningRecord
	 */
	private LearningRecord createLearningRecord(final Person person, final LearningResource learningResource,
			final Verb verb, final Result result) {

		log.info("Creating new learning record.");
		LearningRecord learningRecord = new LearningRecord();

		LearningStatus learningStatus = getStatus(learningRecord, verb, result);

		learningRecord.setLearningResource(learningResource);
		learningRecord.setPerson(person);
		learningRecord.setRecordStatus(learningStatus);

		if (result != null) {
			Score score = result.getScore();
			if (score != null) {
				if (score.getScaled() != null) {
					learningRecord.setAcademicGrade(score.getScaled().toString());
				}
			}

		}

		learningRecordService.save(learningRecord);

		String[] strings = { "Learning record for", person.getName(), "-", learningResource.getTitle(), " created." };
		log.info(String.join(" ", strings));

		return learningRecord;
	}

	/**
	 * @param person
	 * @param learningRecord
	 * @param verb
	 * @param result
	 * @return LearningRecord
	 */
	private LearningRecord updateLearningRecord(LearningRecord learningRecord, final Verb verb, final Result result) {

		log.info("Update learning record.");

		try {

			LearningStatus learningStatus = getStatus(learningRecord, verb, result);

			learningRecord.setRecordStatus(learningStatus);

			if (result != null) {
				Score score = result.getScore();
				if (score != null) {
					if (score.getScaled() != null) {
						learningRecord.setAcademicGrade(score.getScaled().toString());
					}
				}

			}

			learningRecordService.update(learningRecord);

		} catch (RuntimeServiceException e) {
			throw e;
		}
		return learningRecord;
	}

	/**
	 * @param learningRecord
	 * @param verb
	 * @param result
	 * @return LearningStatus
	 */
	private LearningStatus getStatus(LearningRecord learningRecord, final Verb verb, final Result result) {

		String[] strings = { "Verb =", verb.getId() };
		log.info(String.join(" ", strings));

		if (verb.getId().equalsIgnoreCase(VerbIdConstants.PASSED_VERB_ID)) {

			return LearningStatus.PASSED;

		} else if (verb.getId().equalsIgnoreCase(VerbIdConstants.FAILED_VERB_ID)) {

			return LearningStatus.FAILED;

		} else if (verb.getId().equalsIgnoreCase(VerbIdConstants.INITIALIZED_VERB_ID)) {

			return LearningStatus.ATTEMPTED;
		}

		if (result == null) {
			return LearningStatus.ATTEMPTED;
		}

		Boolean success = result.getSuccess();
		Boolean completed = result.getCompletion();

		if (completed && success == null) {

			return LearningStatus.COMPLETED;

		} else if (completed && success) {

			return LearningStatus.PASSED;

		} else if (completed && !success) {

			return LearningStatus.FAILED;

		} else {

			return LearningStatus.ATTEMPTED;
		}

	}
}
