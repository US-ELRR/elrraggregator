package com.deloitte.elrr.aggregator.rules;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.deloitte.elrr.aggregator.consumer.VerbIdConstants;
import com.deloitte.elrr.entity.LearningRecord;
import com.deloitte.elrr.entity.LearningResource;
import com.deloitte.elrr.entity.Person;
import com.deloitte.elrr.entity.types.LearningStatus;
import com.deloitte.elrr.jpa.svc.LearningRecordSvc;
import com.deloitte.elrr.jpa.svc.LearningResourceSvc;
import com.yetanalytics.xapi.model.AbstractObject;
import com.yetanalytics.xapi.model.Activity;
import com.yetanalytics.xapi.model.ActivityDefinition;
import com.yetanalytics.xapi.model.LangMap;
import com.yetanalytics.xapi.model.Result;
import com.yetanalytics.xapi.model.Score;
import com.yetanalytics.xapi.model.Statement;
import com.yetanalytics.xapi.model.Verb;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ProcessCompleted implements Rule {

  @Autowired private LearningResourceSvc learningResourceService;

  @Autowired private LearningRecordSvc learningRecordService;

  @Value("${lang.codes}")
  private String[] namLang = new String[10];

  private static String updatedBy = "ELRR";

  @Override
  public boolean fireRule(Statement statement) {

    // Completed Verb Id
    String completedVerbId = VerbIdConstants.COMPLETED_VERB_ID;

    // Achieved Verb Id
    String achievedVerbId = VerbIdConstants.ACHIEVED_VERB_ID;

    // Get Verb
    Verb verb = getVerb(statement);

    // Is Verb Id completed or achieved
    if (verb.getId().equalsIgnoreCase(completedVerbId)) {
      return true;
    } else if (verb.getId().equalsIgnoreCase(achievedVerbId)) {
      return true;
    } else {
      return false;
    }
  }

  @Override
  public void processRule(Person person, Statement statement) throws Exception {

    try {

      // Get Object
      AbstractObject obj = getObject(statement);

      // Get Result
      Result result = getResult(statement);

      // If Activity
      if (obj instanceof Activity) {

        log.info("Process activity.");

        // Get Activity
        Activity activity = (Activity) obj;

        // Process LearningResource
        LearningResource learningResource = processLearningResource(activity);

        // Process LearningRecord
        if (learningResource != null) {
          LearningRecord learningRecord =
              processLearningRecord(activity, person, result, learningResource);
        }
      }

    } catch (Exception e) {
      log.error("Exception while processing message.");
      log.error(e.getMessage());
      e.printStackTrace();
    }
  }

  /**
   * @param statement
   * @return AbstractObject
   */
  public AbstractObject getObject(Statement statement) {
    AbstractObject obj = statement.getObject();
    return obj;
  }

  /**
   * @param statement
   * @return Verb
   */
  public Verb getVerb(Statement statement) {
    Verb verb = statement.getVerb();
    return verb;
  }

  /**
   * @param statement
   * @return Result
   */
  public Result getResult(Statement statement) {
    Result result = statement.getResult();
    return result;
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

      System.out.println("Learning resource " + learningResource.getTitle() + " exists.");

      // If LearningResource doesn't exist
    } else if (learningResource == null) {
      learningResource = createLearningResource(activity);
    }

    return learningResource;
  }

  /**
   * @param activity
   * @return LearningResource
   */
  public LearningResource createLearningResource(Activity activity) {

    log.info("Creating new learning resource.");

    // Activity Definition
    ActivityDefinition activityDefenition = activity.getDefinition();

    // Activity name
    String activityName = null;
    String nameLangCode = null;

    LangMap nameLangMap = activityDefenition.getName();

    // If Activity name
    if (nameLangMap != null) {
      nameLangCode = getLangCode(nameLangMap);
      activityName = activityDefenition.getName().get(nameLangCode);
    }

    // Activity Description
    String activityDescription = "";
    String langCode = null;

    LangMap descLangMap = activityDefenition.getDescription();
    LangMap namLangMap = activityDefenition.getName();

    // If activity description
    if (descLangMap != null) {

      langCode = getLangCode(descLangMap);
      activityDescription = activityDefenition.getDescription().get(langCode);

      // If activity name
    } else if (namLangMap != null) {

      langCode = getLangCode(nameLangMap);
      activityDescription = activityDefenition.getName().get(langCode);

    } else {

      activityDescription = "";
    }

    if (activityDescription == null) {
      activityDescription = "";
    }

    LearningResource learningResource = new LearningResource();
    learningResource.setIri(activity.getId());
    learningResource.setDescription(activityDescription);
    learningResource.setTitle(activityDescription);
    learningResource.setUpdatedBy(updatedBy);
    learningResourceService.save(learningResource);
    log.info("Learning resource " + learningResource.getTitle() + " created.");
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
        learningRecordService.findByPersonIdAndLearninResourceId(
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

    learningRecord.setLearningResource(learningResource);
    learningRecord.setPerson(person);
    learningRecord.setUpdatedBy(updatedBy);
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

    learningRecord.setUpdatedBy(updatedBy);
    learningRecordService.update(learningRecord);
    return learningRecord;
  }

  /**
   * @param map
   * @return langCode
   */
  private String getLangCode(LangMap map) {

    String langCode = null;

    Set<String> langCodes = map.getLanguageCodes();

    // Check for en-us then en
    if (langCodes.contains("en-us")) {

      langCode = "en-us";

    } else if (langCodes.contains("en")) {

      langCode = "en";

    } else {

      Iterator<String> langCodesIterator = langCodes.iterator();

      // Iterate and compare to lang.codes in .properties
      while (langCodesIterator.hasNext()) {

        String code = langCodesIterator.next();

        boolean found = Arrays.asList(namLang).contains(code);

        if (found) {
          langCode = code;
          break;
        }
      }
    }

    if (langCode == null || langCode.length() == 0) {
      String firstElement = langCodes.stream().findFirst().orElse(null);
      langCode = firstElement;
    }

    return langCode;
  }
}
