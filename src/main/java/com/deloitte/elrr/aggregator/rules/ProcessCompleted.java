package com.deloitte.elrr.aggregator.rules;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.deloitte.elrr.aggregator.consumer.VerbIdConstants;
import com.deloitte.elrr.aggregator.utils.ActivityDescriptionValue;
import com.deloitte.elrr.elrraggregator.exception.AggregatorException;
import com.deloitte.elrr.entity.LearningRecord;
import com.deloitte.elrr.entity.LearningResource;
import com.deloitte.elrr.entity.Person;
import com.deloitte.elrr.entity.types.LearningStatus;
import com.deloitte.elrr.jpa.svc.LearningRecordSvc;
import com.deloitte.elrr.jpa.svc.LearningResourceSvc;
import com.yetanalytics.xapi.model.Activity;
import com.yetanalytics.xapi.model.ActivityDefinition;
import com.yetanalytics.xapi.model.LangMap;
import com.yetanalytics.xapi.model.Result;
import com.yetanalytics.xapi.model.Score;
import com.yetanalytics.xapi.model.Statement;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ProcessCompleted implements Rule {

  @Autowired private LearningResourceSvc learningResourceService;

  @Autowired private LearningRecordSvc learningRecordService;

  @Autowired private ActivityDescriptionValue activityDescriptionValue;

  @Value("${lang.codes}")
  ArrayList<String> languageCodes = new ArrayList<String>();

  @Override
  public boolean fireRule(Statement statement) {

    // Is Verb Id completed and object an activity
    if (statement.getVerb().getId().equalsIgnoreCase(VerbIdConstants.COMPLETED_VERB_ID)
        && statement.getObject() instanceof Activity) {
      return true;
    } else {
      return false;
    }
  }

  @Override
  @Transactional
  public void processRule(Person person, Statement statement) {

    try {

      log.info("Process activity.");

      // Get Activity
      Activity activity = (Activity) statement.getObject();

      // Process LearningResource
      LearningResource learningResource = processLearningResource(activity);

      // Process LearningRecord
      if (learningResource != null) {
        processLearningRecord(activity, person, statement.getResult(), learningResource);
      }

    } catch (AggregatorException | ClassCastException | NullPointerException e) {
      throw e;
    }
  }

  /**
   * @param activity
   * @return LearningResource
   */
  public LearningResource processLearningResource(Activity activity) {

    // Get learningResource
    LearningResource learningResource = learningResourceService.findByIri(activity.getId());

    // If LearningResource already exists
    if (learningResource != null) {

      log.info("Learning resource " + learningResource.getTitle() + " exists.");

    } else {

      try {

        learningResource = createLearningResource(activity);

      } catch (AggregatorException | ClassCastException | NullPointerException e) {

        log.error("Error processing learning resource - " + e.getMessage());
        e.printStackTrace();
        throw e;
      }
    }

    return learningResource;
  }

  /**
   * @param activity
   * @return LearningResource
   */
  public LearningResource createLearningResource(Activity activity) {

    log.info("Creating new learning resource.");

    LearningResource learningResource = null;

    // Activity Definition
    ActivityDefinition activityDefinition = activity.getDefinition();

    // Activity Description
    String activityDescription = "";

    LangMap nameLangMap = activityDefinition.getName();
    LangMap descLangMap = activityDefinition.getDescription();

    try {

      // If Activity name
      if (nameLangMap != null) {
        activityDescription =
            activityDescriptionValue.getActivityDescription(
                nameLangMap, activityDefinition, "name");
        // If activity description
      } else if (descLangMap != null) {
        activityDescription =
            activityDescriptionValue.getActivityDescription(
                descLangMap, activityDefinition, "desc");
      }

      learningResource = new LearningResource();
      learningResource.setIri(activity.getId());
      learningResource.setDescription(activityDescription);
      learningResource.setTitle(activityDescription);
      learningResourceService.save(learningResource);
      log.info("Learning resource " + learningResource.getTitle() + " created.");

    } catch (AggregatorException | ClassCastException | NullPointerException e) {
      log.error(e.getMessage());
      e.printStackTrace();
      throw e;
    }

    return learningResource;
  }

  /**
   * @param Activity
   * @param Person
   * @param Result
   * @param LearningResource
   * @return LearningRccord
   */
  public LearningRecord processLearningRecord(
      Activity activity, Person person, Result result, LearningResource learningResource) {

    // Get LearningRecord
    LearningRecord learningRecord =
        learningRecordService.findByPersonIdAndLearningResourceId(
            person.getId(), learningResource.getId());

    // If LearningRecord doesn't exist
    if (learningRecord == null) {
      learningRecord = createLearningRecord(person, learningResource, result);

      // If learningRecord already exists
    } else {
      learningRecord = updateLearningRecord(learningRecord, result);
    }

    return learningRecord;
  }

  /**
   * @param Person
   * @param learningResource
   * @param Result
   * @return LearningRecord
   */
  public LearningRecord createLearningRecord(
      Person person, LearningResource learningResource, Result result) {

    log.info("Creating new learning record.");
    LearningRecord learningRecord = new LearningRecord();

    if (result != null) {
      learningRecord = setStatus(learningRecord, result);
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
   * @param result
   * @return LearningRecord
   */
  public LearningRecord updateLearningRecord(LearningRecord learningRecord, Result result) {
    log.info("Update learning record.");
    learningRecord = setStatus(learningRecord, result);
    learningRecordService.update(learningRecord);
    return learningRecord;
  }

  /**
   * @param learningRecord
   * @param result
   * @return learningRecord
   */
  private LearningRecord setStatus(LearningRecord learningRecord, Result result) {

    if (result != null) {

      Boolean success = result.getSuccess();
      Boolean completed = result.getCompletion();

      // status
      if (completed && success == null) {
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
        learningRecord.setAcademicGrade(score.getRaw().toString());
      }

    } else {
      learningRecord.setRecordStatus(LearningStatus.ATTEMPTED);
    }

    return learningRecord;
  }
}
