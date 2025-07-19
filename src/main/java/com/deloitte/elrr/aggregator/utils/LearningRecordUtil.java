package com.deloitte.elrr.aggregator.utils;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.deloitte.elrr.aggregator.rules.VerbIdConstants;
import com.deloitte.elrr.entity.LearningRecord;
import com.deloitte.elrr.entity.LearningResource;
import com.deloitte.elrr.entity.Person;
import com.deloitte.elrr.entity.types.LearningStatus;
import com.deloitte.elrr.exception.RuntimeServiceException;
import com.deloitte.elrr.jpa.svc.LearningRecordSvc;
import com.yetanalytics.xapi.model.Result;
import com.yetanalytics.xapi.model.Verb;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class LearningRecordUtil {

    @Autowired
    private LearningRecordSvc learningRecordService;

    /**
     * @param person
     * @param verb
     * @param result
     * @param learningResource
     * @return learningRccord
     * @throws RuntimeServiceException
     */
    public LearningRecord processLearningRecord(final Person person,
            final Verb verb, final Result result,
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
     * For Registered.
     *
     * @param person
     * @param verb
     * @param result
     * @param learningResource
     * @param enrollmentDate
     * @return learningRccord
     * @throws RuntimeServiceException
     */
    @SuppressWarnings("checkstyle:linelength")
    public LearningRecord processLearningRecord(final Person person,
            final Verb verb, final Result result,
            final LearningResource learningResource,
            final LocalDateTime enrollmentDate) {

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
                        verb, result, enrollmentDate);

                // If learningRecord already exists
            } else {

                // If existing LearningRecord is COMPLETED
                if (learningRecord.getRecordStatus().equals(
                        LearningStatus.COMPLETED)) {

                    // If new registered enrollmentDate <= existing timestamp
                    if (enrollmentDate.isEqual(learningRecord.getInsertedDate())
                            || enrollmentDate.isBefore(learningRecord
                                    .getInsertedDate())) {
                        log.error(
                                "Error trying to re-register a completed activity. Learning Record for "
                                        + person.getName() + " - "
                                        + learningRecord.getLearningResource()
                                                .getTitle() + ".");
                        log.error("Original status = " + learningRecord
                                .getRecordStatus());
                        log.error("Original time stamp = " + learningRecord
                                .getInsertedDate());
                        log.error("New enrollment date = " + enrollmentDate);
                        return learningRecord;
                    }

                }

                learningRecord = updateLearningRecord(person, learningRecord,
                        verb, result, enrollmentDate);
            }

        } catch (RuntimeServiceException e) {
            throw e;
        }

        return learningRecord;
    }

    /**
     * For Extensions.
     *
     * @param person
     * @param verb
     * @param result
     * @param learningResource
     * @param extensionsMap
     * @return learningRccord
     * @throws URISyntaxException
     * @throws RuntimeServiceException
     */
    @SuppressWarnings("checkstyle:linelength")
    public LearningRecord processLearningRecord(final Person person,
            final Verb verb, final Result result,
            final LearningResource learningResource, final Map<URI,
                    Object> extensionsMap) throws URISyntaxException {

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
                        verb, result, extensionsMap);

                // If learningRecord already exists
            } else {

                learningRecord = updateLearningRecord(person, learningRecord,
                        verb, result, extensionsMap);
            }

        } catch (RuntimeServiceException | URISyntaxException e) {
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

        if (result != null && result.getScore() != null && result.getScore()
                .getScaled() != null) {
            learningRecord.setAcademicGrade(result.getScore().getScaled()
                    .toString());
        }

        learningRecordService.save(learningRecord);

        log.info("Learning Record for " + person.getName() + " - "
                + learningResource.getTitle() + " created.");

        return learningRecord;
    }

    /**
     * For Registered.
     *
     * @param person
     * @param learningResource
     * @param verb
     * @param result
     * @param enrollmentDate
     * @return learningRecord
     */
    private LearningRecord createLearningRecord(final Person person,
            final LearningResource learningResource, final Verb verb,
            final Result result, LocalDateTime enrollmentDate) {

        log.info("Creating new learning record.");
        LearningRecord learningRecord = new LearningRecord();

        LearningStatus learningStatus = getStatus(verb, result);

        learningRecord.setEnrollmentDate(enrollmentDate);
        learningRecord.setLearningResource(learningResource);
        learningRecord.setPerson(person);
        learningRecord.setRecordStatus(learningStatus);

        if (result != null && result.getScore() != null && result.getScore()
                .getScaled() != null) {
            learningRecord.setAcademicGrade(result.getScore().getScaled()
                    .toString());
        }

        learningRecordService.save(learningRecord);

        log.info("Learning Record for " + person.getName() + " - "
                + learningResource.getTitle() + " created.");

        return learningRecord;
    }

    /**
     * For Extensions.
     *
     * @param person
     * @param learningResource
     * @param verb
     * @param result
     * @param extensionsMap
     * @return learningRecord
     */
    private LearningRecord createLearningRecord(final Person person,
            final LearningResource learningResource, final Verb verb,
            final Result result, final Map<URI, Object> extensionsMap)
            throws URISyntaxException {

        log.info("Creating new learning record.");
        LearningRecord learningRecord = new LearningRecord();

        LearningStatus learningStatus = getStatus(verb, result);

        if (extensionsMap != null) {
            learningRecord.setExtensions(extensionsMap);
        }

        learningRecord.setLearningResource(learningResource);
        learningRecord.setPerson(person);
        learningRecord.setRecordStatus(learningStatus);

        if (result != null && result.getScore() != null && result.getScore()
                .getScaled() != null) {
            learningRecord.setAcademicGrade(result.getScore().getScaled()
                    .toString());
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

            if (result != null && result.getScore() != null && result.getScore()
                    .getScaled() != null) {
                learningRecord.setAcademicGrade(result.getScore().getScaled()
                        .toString());
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
     * For Registered.
     *
     * @param person
     * @param learningRecord
     * @param verb
     * @param result
     * @param enrollmentDate
     * @return learningRecord
     * @throws RuntimeServiceException
     */
    public LearningRecord updateLearningRecord(Person person,
            LearningRecord learningRecord, final Verb verb, final Result result,
            final LocalDateTime enrollmentDate) {

        log.info("Update learning record.");

        try {

            LearningStatus learningStatus = getStatus(verb, result);

            learningRecord.setRecordStatus(learningStatus);
            learningRecord.setEnrollmentDate(enrollmentDate);

            if (result != null && result.getScore() != null && result.getScore()
                    .getScaled() != null) {
                learningRecord.setAcademicGrade(result.getScore().getScaled()
                        .toString());
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
     * For Extensions.
     *
     * @param person
     * @param learningRecord
     * @param verb
     * @param result
     * @param extensionsMap
     * @return learningRecord
     * @throws RuntimeServiceException
     */
    public LearningRecord updateLearningRecord(Person person,
            LearningRecord learningRecord, final Verb verb, final Result result,
            final Map<URI, Object> extensionsMap) throws URISyntaxException {

        log.info("Update learning record.");

        try {

            LearningStatus learningStatus = getStatus(verb, result);

            if (extensionsMap != null) {
                learningRecord.setExtensions(extensionsMap);
            }

            learningRecord.setRecordStatus(learningStatus);

            if (result != null && result.getScore() != null && result.getScore()
                    .getScaled() != null) {
                learningRecord.setAcademicGrade(result.getScore().getScaled()
                        .toString());
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
