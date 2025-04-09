package com.deloitte.elrr.aggregator.rules;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.deloitte.elrr.aggregator.consumer.ObjectTypeConstants;
import com.deloitte.elrr.aggregator.consumer.VerbIdConstants;
import com.deloitte.elrr.aggregator.utils.LangMapUtil;
import com.deloitte.elrr.elrraggregator.exception.AggregatorException;
import com.deloitte.elrr.entity.Competency;
import com.deloitte.elrr.entity.Person;
import com.deloitte.elrr.entity.PersonalCompetency;
import com.deloitte.elrr.jpa.svc.CompetencySvc;
import com.deloitte.elrr.jpa.svc.PersonalCompetencySvc;
import com.yetanalytics.xapi.model.Activity;
import com.yetanalytics.xapi.model.LangMap;
import com.yetanalytics.xapi.model.Statement;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ProcessCompetency implements Rule {

  @Autowired private CompetencySvc competencyService;

  @Autowired private PersonalCompetencySvc personalCompetencyService;

  @Autowired private LangMapUtil langMapUtil;

  @Override
  public boolean fireRule(Statement statement) {

    Boolean fireRule = false;

    // Is Verb Id = achieved and object = activity
    if (statement.getVerb().getId().equalsIgnoreCase(VerbIdConstants.ACHIEVED_VERB_ID)
        && statement.getObject() instanceof Activity) {

      Activity obj = (Activity) statement.getObject();
      String objType = obj.getDefinition().getType();

      // If no object type
      if (objType == null) {
        fireRule = false;
        // If object type = competency
      } else if (objType.equalsIgnoreCase(ObjectTypeConstants.COMPETENCY)) {
        fireRule = true;
      }
    }
    return fireRule;
  }

  @Override
  @Transactional
  public void processRule(Person person, Statement statement) {

    try {

      log.info("Process activity achieved.");

      // Get Activity
      Activity activity = (Activity) statement.getObject();

      // Process Competency
      Competency competency = processCompetency(activity);

      // Process PersonalCompetency
      if (competency != null) {
        processPersonalCompetency(activity, person, competency);
      }

    } catch (AggregatorException | ClassCastException | NullPointerException e) {
      throw e;
    }
  }

  /**
   * @param statement
   * @return competency
   */
  private Competency processCompetency(Activity activity) {

    Competency competency = null;

    try {

      // Get competency
      competency = competencyService.findByIdentifier(activity.getId());

      // If competency doesn't exist
      if (competency == null) {

        competency = createCompetency(activity);

      } else {

        log.info("Competency " + activity.getId() + " exists.");
        competency = updateCompetency(competency, activity);
      }

    } catch (AggregatorException | ClassCastException | NullPointerException e) {

      log.error("Error processing competency - " + e.getMessage());
      e.printStackTrace();
      throw e;
    }

    return competency;
  }

  /**
   * @param activity
   * @return competency
   */
  private Competency createCompetency(Activity activity) {

    log.info("Creating new competency.");

    Competency competency = null;
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

      competency = new Competency();
      competency.setIdentifier(activity.getId());
      competency.setFrameworkTitle(activityName);
      competency.setFrameworkDescription(activityDescription);
      competencyService.save(competency);
      log.info("Competency " + activity.getId() + " created.");

    } catch (AggregatorException | ClassCastException | NullPointerException e) {
      log.error(e.getMessage());
      e.printStackTrace();
      throw e;
    }

    return competency;
  }

  /**
   * @param activity
   * @return competency
   */
  private Competency updateCompetency(Competency competency, Activity activity) {

    log.info("Updating competency.");

    String nameLangCode = null;
    String descLangCode = null;

    String activityName = "";
    String activityDescription = "";

    LangMap nameLangMap = activity.getDefinition().getName();
    LangMap descLangMap = activity.getDefinition().getDescription();

    log.info("nameLangMap = " + nameLangMap.getLanguageCodes());

    try {

      nameLangCode = langMapUtil.getLangMapValue(nameLangMap);
      activityName = activity.getDefinition().getName().get(nameLangCode);

      descLangCode = langMapUtil.getLangMapValue(descLangMap);
      activityDescription = activity.getDefinition().getDescription().get(descLangCode);

      competency.setFrameworkTitle(activityName);
      competency.setFrameworkDescription(activityDescription);
      competencyService.update(competency);
      log.info("Competency " + activity.getId() + " updated.");

    } catch (AggregatorException | ClassCastException | NullPointerException e) {
      log.error(e.getMessage());
      e.printStackTrace();
      throw e;
    }

    return competency;
  }

  /**
   * @param Activity
   * @param Person
   * @param Competency
   * @return PersonalCompetency
   */
  private PersonalCompetency processPersonalCompetency(
      Activity activity, Person person, Competency competency) {

    // Get PersonalCompetency
    PersonalCompetency personalCompetency =
        personalCompetencyService.findByPersonIdAndCompetencyId(person.getId(), competency.getId());

    // If PersonalCompetancy doesn't exist
    if (personalCompetency == null) {

      createPersonalCompetency(person, competency);

    } else {

      updatePersonalCompetency(personalCompetency, person, competency);
    }

    return personalCompetency;
  }

  /**
   * @param Person
   * @param Competency
   * @return PersonalCompetency
   */
  private PersonalCompetency createPersonalCompetency(Person person, Competency competency) {

    log.info("Creating new personal competency record.");
    PersonalCompetency personalCompetency = new PersonalCompetency();

    personalCompetency.setPerson(person);
    personalCompetency.setCompetency(competency);
    personalCompetency.setHasRecord(true);
    personalCompetencyService.save(personalCompetency);

    log.info(
        "Personal Competency for "
            + person.getName()
            + " - "
            + competency.getFrameworkTitle()
            + " created.");

    return personalCompetency;
  }

  private PersonalCompetency updatePersonalCompetency(
      PersonalCompetency personalCompetency, Person person, Competency competency) {

    // TO DO
    // personalCompetencyService.update(personalCompetency);

    /*log.info(
    "Personal Competency for "
        + person.getName()
        + " - "
        + competency.getFrameworkTitle()
        + " updated.");*/

    return personalCompetency;
  }
}
