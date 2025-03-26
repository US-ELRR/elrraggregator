package com.deloitte.elrr.aggregator.rules;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

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

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ProcessActivity {

  @Autowired private LearningResourceSvc learningResourceService;

  @Autowired private LearningRecordSvc learningRecordService;

  @Value("${lang.codes}")
  private String[] namLang = new String[10];

  private static String updatedBy = "ELRR";

  public void processActivity(Person person, Statement statement) throws Exception {

    try {

      // Get Object
      AbstractObject obj = getObject(statement);

      // Get Result
      Result result = getResult(statement);

      if (obj != null) {

        log.info("Process activity.");

        // Get Activity
        Activity activity = (Activity) obj;

        // If Person exists
        if (person != null) {

          // Process LearningResource
          LearningResource learningResource = processLearningResource(activity);

          // Process LearningRecord
          if (learningResource != null) {
            LearningRecord learningRecord =
                processLearningRecord(activity, person, result, learningResource);
          }
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
    String activityName = "";
    String nameLangCode = "";

    LangMap nameLangMap = activityDefenition.getName();

    // If Activity name
    if (nameLangMap != null) {
      Set<String> nameLangCodes = nameLangMap.getLanguageCodes();

      // Get namLangCode
      Iterator<String> nameLangCodesIterator = nameLangCodes.iterator();

      while (nameLangCodesIterator.hasNext()) {
        String code = nameLangCodesIterator.next();
        boolean found = Arrays.asList(namLang).contains(code);
        if (found) {
          nameLangCode = code;
          break;
        }
      }

      // If namLangCode not found
      if (nameLangCode.equalsIgnoreCase("")) {
        nameLangCode = "en-us";
      }

      activityName = activityDefenition.getName().get(nameLangCode);
    }

    // Activity Description
    String activityDescription = "";
    String langCode = "";

    LangMap descLangMap = activityDefenition.getDescription();
    LangMap namLangMap = activityDefenition.getName();

    // If activity description
    if (descLangMap != null) {
      Set<String> descLangCodes = descLangMap.getLanguageCodes();

      // Get namDescCode
      Iterator<String> descLangCodesIterator = descLangCodes.iterator();

      while (descLangCodesIterator.hasNext()) {
        String code = descLangCodesIterator.next();
        boolean found = Arrays.asList(namLang).contains(code);
        if (found) {
          langCode = code;
          break;
        }
      }

      // If langCode not found
      if (langCode.equalsIgnoreCase("")) {
        langCode = "en-us";
      }

      activityDescription = activityDefenition.getDescription().get(langCode);

      // If activity name
    } else if (namLangMap != null) {
      Set<String> namLangCodes = namLangMap.getLanguageCodes();

      // Get namDescCode
      Iterator<String> namLangCodesIterator = namLangCodes.iterator();

      while (namLangCodesIterator.hasNext()) {
        String code = namLangCodesIterator.next();
        boolean found = Arrays.asList(namLang).contains(code);
        if (found) {
          langCode = code;
          break;
        }
      }

      // If langCode not found
      if (langCode.equalsIgnoreCase("")) {
        langCode = "en-us";
      }

      activityDescription = activityDefenition.getName().get(langCode);

    } else {
      activityDescription = "";
    }

    LearningResource learningResource = new LearningResource();
    learningResource.setIri(activity.getId());
    learningResource.setDescription(activityDescription);

    if (activityDescription != null) {
      learningResource.setTitle(activityDescription);
    } else {
      learningResource.setTitle("");
    }

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
      learningRecord = updateLearningRecord(person, learningRecord, learningResource);
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
   * @return LearningRecord
   */
  public LearningRecord updateLearningRecord(
      Person person, LearningRecord learningRecord, LearningResource learningResource) {
    log.info("Update learning record.");
    learningRecord.setRecordStatus(LearningStatus.COMPLETED);
    learningRecord.setLearningResource(learningResource);
    learningRecord.setPerson(person);
    learningRecord.setUpdatedBy(updatedBy);
    learningRecordService.update(learningRecord);
    log.info(
        "Learning record for "
            + person.getName()
            + " - "
            + learningResource.getTitle()
            + " updated.");
    return learningRecord;
  }
}
