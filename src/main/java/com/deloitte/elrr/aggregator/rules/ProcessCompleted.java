package com.deloitte.elrr.aggregator.rules;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.deloitte.elrr.aggregator.utils.LangMapUtil;
import com.deloitte.elrr.elrraggregator.exception.AggregatorException;
import com.deloitte.elrr.entity.LearningRecord;
import com.deloitte.elrr.entity.LearningResource;
import com.deloitte.elrr.entity.Person;
import com.deloitte.elrr.entity.types.LearningStatus;
import com.deloitte.elrr.exception.RuntimeServiceException;
import com.deloitte.elrr.jpa.svc.LearningRecordSvc;
import com.deloitte.elrr.jpa.svc.LearningResourceSvc;
import com.yetanalytics.xapi.model.Activity;
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

  @Autowired private LangMapUtil langMapUtil;

  @Override
  public boolean fireRule(final Statement statement) {

    // Is Verb Id = completed and object = activity
    if (statement.getVerb().getId().equalsIgnoreCase(VerbIdConstants.COMPLETED_VERB_ID)
        && statement.getObject() instanceof Activity) {
      return true;
    } else {
      return false;
    }
  }

  @Override
  @Transactional
  public void processRule(final Person person, final Statement statement) {

    try {

      log.info("Process activity completed");

      // Get Activity
      Activity activity = (Activity) statement.getObject();

      // Process LearningResource
      LearningResource learningResource = processLearningResource(activity);

      // Process LearningRecord
      if (learningResource != null) {
        processLearningRecord(activity, person, statement.getResult(), learningResource);
      }

    } catch (AggregatorException
        | ClassCastException
        | NullPointerException
        | RuntimeServiceException e) {
      throw e;
    }
  }

  /**
   * @param activity
   * @return LearningResource
   */
  private LearningResource processLearningResource(final Activity activity) {

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
  private LearningResource createLearningResource(final Activity activity) {

    log.info("Creating new learning resource.");

    LearningResource learningResource = null;
    String nameLangCode = null;
    String descLangCode = null;

    String activityName = "";
    String activityDescription = "";

    LangMap nameLangMap = activity.getDefinition().getName();
    LangMap descLangMap = activity.getDefinition().getDescription();

    try {

      nameLangCode = langMapUtil.getLangMapValue(nameLangMap);
      activityName = activity.getDefinition().getName().get(nameLangCode);

      descLangCode = langMapUtil.getLangMapValue(descLangMap);
      activityDescription = activity.getDefinition().getDescription().get(descLangCode);

      learningResource = new LearningResource();
      learningResource.setIri(activity.getId());
      learningResource.setDescription(activityDescription);
      learningResource.setTitle(activityName);
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
  private LearningRecord processLearningRecord(
      final Activity activity,
      final Person person,
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

        createLearningRecord(person, learningResource, result);

        // If learningRecord already exists
      } else {

        updateLearningRecord(learningRecord, result);
      }

    } catch (RuntimeServiceException e) {
      throw e;
    }

    return learningRecord;
  }

  /**
   * @param Person
   * @param learningResource
   * @param Result
   * @return LearningRecord
   */
  private LearningRecord createLearningRecord(
      final Person person, final LearningResource learningResource, final Result result) {

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
  private LearningRecord updateLearningRecord(LearningRecord learningRecord, final Result result) {

    log.info("Update learning record.");

    try {
      learningRecord = setStatus(learningRecord, result);
      learningRecordService.update(learningRecord);
    } catch (RuntimeServiceException e) {
      throw e;
    }
    return learningRecord;
  }

  /**
   * @param learningRecord
   * @param result
   * @return learningRecord
   */
  private LearningRecord setStatus(LearningRecord learningRecord, final Result result) {

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
