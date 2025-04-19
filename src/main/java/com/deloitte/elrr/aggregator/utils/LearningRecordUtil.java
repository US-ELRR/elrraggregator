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

  @Autowired private LearningRecordSvc learningRecordService;

  /**
   * @param Activity
   * @param Person
   * @param verb
   * @param Result
   * @param LearningResource
   * @return LearningRccord
   */
  public LearningRecord processLearningRecord(
      final Activity activity,
      final Person person,
      final Verb verb,
      final Result result,
      final LearningResource learningResource) {

    LearningRecord learningRecord = null;

    try {

      // Get LearningRecord
      learningRecord =
          learningRecordService.findByPersonIdAndLearningResourceId(
              person.getId(), learningResource.getId());

      // If LearningRecord doesn't exist
      if (learningRecord == null) {

        createLearningRecord(person, learningResource, verb, result);

        // If learningRecord already exists
      } else {

        updateLearningRecord(learningRecord, verb, result);
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
  private LearningRecord createLearningRecord(
      final Person person,
      final LearningResource learningResource,
      final Verb verb,
      final Result result) {

    log.info("Creating new learning record.");
    LearningRecord learningRecord = new LearningRecord();

    if (result != null) {
      learningRecord = setStatus(learningRecord, verb, result);
    } else {
      learningRecord.setRecordStatus(LearningStatus.ATTEMPTED);
    }

    learningRecord.setLearningResource(learningResource);
    learningRecord.setPerson(person);
    learningRecordService.save(learningRecord);

    log.info(
        "Learning record for "
            + person.getName()
            + " - "
            + learningResource.getTitle()
            + " created.");

    return learningRecord;
  }

  /**
   * @param person
   * @param learningRecord
   * @param learningResource
   * @param verb
   * @param result
   * @return LearningRecord
   */
  private LearningRecord updateLearningRecord(
      LearningRecord learningRecord, final Verb verb, final Result result) {

    log.info("Update learning record.");

    try {
      learningRecord = setStatus(learningRecord, verb, result);
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
   * @return learningRecord
   */
  private LearningRecord setStatus(
      LearningRecord learningRecord, final Verb verb, final Result result) {

    if (result != null) {

      Boolean success = result.getSuccess();
      Boolean completed = result.getCompletion();

      // status
      if (verb.getId() == VerbIdConstants.PASSED_VERB_ID) {
        learningRecord.setRecordStatus(LearningStatus.PASSED);
      } else if (verb.getId() == VerbIdConstants.FAILED_VERB_ID) {
        learningRecord.setRecordStatus(LearningStatus.FAILED);
      } else if (completed && success == null) {
        learningRecord.setRecordStatus(LearningStatus.COMPLETED);
      } else if (completed && success) {
        learningRecord.setRecordStatus(LearningStatus.PASSED);
      } else if (completed && !success) {
        learningRecord.setRecordStatus(LearningStatus.FAILED);
      } else {
        learningRecord.setRecordStatus(LearningStatus.ATTEMPTED);
      }

      // grade
      Score score = result.getScore();

      if (score != null) {
        if (score.getScaled() != null) {
          learningRecord.setAcademicGrade(score.getScaled().toString());
        }
      }

    } else {
      learningRecord.setRecordStatus(LearningStatus.ATTEMPTED);
    }

    return learningRecord;
  }
}
