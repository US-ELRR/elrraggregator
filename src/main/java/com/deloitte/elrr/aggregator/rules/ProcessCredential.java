package com.deloitte.elrr.aggregator.rules;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.deloitte.elrr.aggregator.utils.LangMapUtil;
import com.deloitte.elrr.elrraggregator.exception.AggregatorException;
import com.deloitte.elrr.entity.Credential;
import com.deloitte.elrr.entity.Person;
import com.deloitte.elrr.entity.PersonalCredential;
import com.deloitte.elrr.exception.RuntimeServiceException;
import com.deloitte.elrr.jpa.svc.CredentialSvc;
import com.deloitte.elrr.jpa.svc.PersonalCredentialSvc;
import com.yetanalytics.xapi.model.Activity;
import com.yetanalytics.xapi.model.LangMap;
import com.yetanalytics.xapi.model.Statement;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ProcessCredential implements Rule {

  @Autowired private CredentialSvc credentialService;

  @Autowired private PersonalCredentialSvc personalCredentialService;

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
        // If object type = credential
      } else if (objType.equalsIgnoreCase(ObjectTypeConstants.CREDENTIAL)) {
        fireRule = true;
      }
    }
    return fireRule;
  }

  @Override
  @Transactional
  public void processRule(Person person, Statement statement) {

    try {

      log.info("Process credential.");

      // Get Activity
      Activity activity = (Activity) statement.getObject();

      // Process Credential
      Credential credential = processCredential(activity);

      // Process PersonalCredential
      if (credential != null) {
        processPersonalCredential(activity, person, credential);
      }

    } catch (AggregatorException
        | ClassCastException
        | NullPointerException
        | RuntimeServiceException e) {
      throw e;
    }
  }

  /**
   * @param statement
   * @return credential
   */
  private Credential processCredential(Activity activity) {

    Credential credential = null;

    try {

      // Get credential
      credential = credentialService.findByIdentifier(activity.getId());

      // If credential doesn't exist
      if (credential == null) {

        credential = createCredential(activity);

      } else {

        log.info("Credential " + activity.getId() + " exists.");
        credential = updateCredential(credential, activity);
      }

    } catch (AggregatorException | ClassCastException | NullPointerException e) {

      log.error("Error processing competency - " + e.getMessage());
      e.printStackTrace();
      throw e;
    }

    return credential;
  }

  /**
   * @param activity
   * @return credential
   */
  private Credential createCredential(Activity activity) {

    log.info("Creating new credential.");

    Credential credential = null;
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

      credential = new Credential();
      credential.setIdentifier(activity.getId());
      credential.setFrameworkTitle(activityName);
      credential.setFrameworkDescription(activityDescription);
      credentialService.save(credential);
      log.info("Credential " + activity.getId() + " created.");

    } catch (AggregatorException | ClassCastException | NullPointerException e) {
      log.error(e.getMessage());
      e.printStackTrace();
      throw e;
    }

    return credential;
  }

  /**
   * @param activity
   * @return credential
   */
  private Credential updateCredential(Credential credential, Activity activity) {

    log.info("Updating credential.");

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

      credential.setFrameworkTitle(activityName);
      credential.setFrameworkDescription(activityDescription);
      credentialService.update(credential);
      log.info("Credential " + activity.getId() + " updated.");

    } catch (AggregatorException | ClassCastException | NullPointerException e) {
      log.error(e.getMessage());
      e.printStackTrace();
      throw e;
    }

    return credential;
  }

  /**
   * @param Activity
   * @param Person
   * @param Credential
   * @return PersonalCredential
   */
  private PersonalCredential processPersonalCredential(
      Activity activity, Person person, Credential credential) {

    // Get PersonalCredential
    PersonalCredential personalCredential =
        personalCredentialService.findByPersonIdAndCredentialId(person.getId(), credential.getId());

    // If PersonalCredential doesn't exist
    if (personalCredential == null) {

      createPersonalCredential(person, credential);

    } else {

      updatePersonalCredential(personalCredential, person, credential);
    }

    return personalCredential;
  }

  /**
   * @param Person
   * @param Credential
   * @return PersonalCredential
   */
  private PersonalCredential createPersonalCredential(Person person, Credential credential) {

    log.info("Creating new personal credential record.");
    PersonalCredential personalCredential = new PersonalCredential();

    personalCredential.setPerson(person);
    personalCredential.setCredential(credential);
    personalCredential.setHasRecord(true);
    personalCredentialService.save(personalCredential);

    log.info(
        "Personal Credential for "
            + person.getName()
            + " - "
            + credential.getFrameworkTitle()
            + " created.");

    return personalCredential;
  }

  private PersonalCredential updatePersonalCredential(
      PersonalCredential personalCredential, Person person, Credential credential) {

    try {

      // TO DO
      // personalCredentialService.update(personalCredential);

      log.info(
          "Personal Credential for "
              + person.getName()
              + " - "
              + credential.getFrameworkTitle()
              + " updated.");

    } catch (RuntimeServiceException e) {
      throw e;
    }

    return personalCredential;
  }
}
