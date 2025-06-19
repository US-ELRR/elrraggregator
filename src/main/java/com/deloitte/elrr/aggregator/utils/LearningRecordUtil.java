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
     * @param activity
     * @param person
     * @param verb
     * @param result
     * @param learningResource
     * @return learningRccord
     * @throws RuntimeServiceException
     */
    public LearningRecord processLearningRecord(final Activity activity,
            final Person person, final Verb verb, final Result result,
            final LearningResource learningResource) {

        LearningRecord learningRecord = null;

        try {

            log.info("Process learning record.");

            // Get LearningRecord
            learningRecord = learningRecordService
                    .findByPersonIdAndLearningResourceId(person.getId(),
                            learningResource.getId());

            // If LearningRecord doesn't exist
            if (learningRecord == null) {

                learningRecord = createLearningRecord(person, learningResource,
                        verb, result);

                // If learningRecord already exists
            } else {

                learningRecord = updateLearningRecord(person, learningRecord,
                        verb, result);
            }

        } catch (RuntimeServiceException e) {
            throw e;
        }

        return learningRecord;
    }

    /**
     * @param person
     * @param learningResource
     * @param verb
     * @param result
     * @return learningRecord
     */
    private LearningRecord createLearningRecord(final Person person,
            final LearningResource learningResource, final Verb verb,
            final Result result) {

        log.info("Creating new learning record.");
        LearningRecord learningRecord = new LearningRecord();

        LearningStatus learningStatus = getStatus(verb, result);

        learningRecord.setLearningResource(learningResource);
        learningRecord.setPerson(person);
        learningRecord.setRecordStatus(learningStatus);

        if (result != null) {
            Score score = result.getScore();
            if (score != null) {
                if (score.getScaled() != null) {
                    learningRecord.setAcademicGrade(score.getScaled()
                            .toString());
                }
            }

        }

        learningRecordService.save(learningRecord);

        log.info("Learning Record for " + person.getName() + " - "
                + learningResource.getTitle() + " created.");

        return learningRecord;
    }

    /**
     * @param person
     * @param learningRecord
     * @param verb
     * @param result
     * @return learningRecord
     * @throws RuntimeServiceException
     */
    public LearningRecord updateLearningRecord(Person person,
            LearningRecord learningRecord, final Verb verb,
            final Result result) {

        log.info("Update learning record.");

        try {

            LearningStatus learningStatus = getStatus(verb, result);

            learningRecord.setRecordStatus(learningStatus);

            if (result != null) {
                Score score = result.getScore();
                if (score != null) {
                    if (score.getScaled() != null) {
                        learningRecord.setAcademicGrade(score.getScaled()
                                .toString());
                    }
                }

            }

            learningRecordService.update(learningRecord);

            log.info("Learning Record for " + person.getName() + " - "
                    + learningRecord.getLearningResource().getTitle()
                    + " updated.");

        } catch (RuntimeServiceException e) {
            throw e;
        }
        return learningRecord;
    }

    /**
     * @param verb
     * @param result
     * @return learningStatus
     */
    private LearningStatus getStatus(final Verb verb, final Result result) {

        log.info("Verb = " + verb.getId());

        if (verb.getId().toString().equalsIgnoreCase(
                VerbIdConstants.PASSED_VERB_ID.toString())) {

            return LearningStatus.PASSED;

        } else if (verb.getId().toString().equalsIgnoreCase(
                VerbIdConstants.FAILED_VERB_ID.toString())) {

            return LearningStatus.FAILED;

        } else if (verb.getId().toString().equalsIgnoreCase(
                VerbIdConstants.INITIALIZED_VERB_ID.toString())) {

            return LearningStatus.ATTEMPTED;

        } else if (verb.getId().toString().equalsIgnoreCase(
                VerbIdConstants.REGISTERED_VERB_ID.toString()) || verb.getId()
                        .toString().equalsIgnoreCase(
                                VerbIdConstants.SCHEDULED_VERB_ID.toString())) {

            return LearningStatus.REGISTERED;

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
