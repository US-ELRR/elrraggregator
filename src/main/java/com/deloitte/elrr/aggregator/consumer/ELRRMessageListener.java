package com.deloitte.elrr.aggregator.consumer;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.deloitte.elrr.InputSanatizer;
import com.deloitte.elrr.aggregator.drools.DroolsProcessStatementService;
import com.deloitte.elrr.aggregator.dto.MessageVO;
import com.deloitte.elrr.entity.Email;
import com.deloitte.elrr.entity.Identity;
import com.deloitte.elrr.entity.LearningRecord;
import com.deloitte.elrr.entity.LearningResource;
import com.deloitte.elrr.entity.Person;
import com.deloitte.elrr.entity.types.LearningStatus;
import com.deloitte.elrr.jpa.svc.EmailSvc;
import com.deloitte.elrr.jpa.svc.IdentitySvc;
import com.deloitte.elrr.jpa.svc.LearningRecordSvc;
import com.deloitte.elrr.jpa.svc.LearningResourceSvc;
import com.deloitte.elrr.jpa.svc.PersonSvc;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yetanalytics.xapi.model.AbstractActor;
import com.yetanalytics.xapi.model.Account;
import com.yetanalytics.xapi.model.Activity;
import com.yetanalytics.xapi.model.ActivityDefinition;
import com.yetanalytics.xapi.model.LangMap;
import com.yetanalytics.xapi.model.ObjectType;
import com.yetanalytics.xapi.model.Result;
import com.yetanalytics.xapi.model.Statement;
import com.yetanalytics.xapi.util.Mapper;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ELRRMessageListener {

  @Autowired private EmailSvc emailService;

  @Autowired private IdentitySvc identityService;

  @Autowired private PersonSvc personService;

  @Autowired private LearningResourceSvc learningResourceService;

  @Autowired private LearningRecordSvc learningRecordService;

  @Autowired private DroolsProcessStatementService droolsProcessStatementService;

  private static String updatedBy = "ELRR";

  /**
   * @param message
   */
  @KafkaListener(topics = "${kafka.topic}")
  public void listen(final String message) {

    if (InputSanatizer.isValidInput(message)) {
      log.info("Received Messasge in group - group-id: " + message);
      processMessage(message);
      // Use Drools rule
      // processMessageFromRule(message);
    } else {
      log.warn("Invalid message did not pass whitelist check - " + message);
    }
  }

  /**
   * @param statement
   */
  private void processMessage(final String payload) {

    ObjectMapper mapper = Mapper.getMapper();
    log.info("payload received " + payload);

    LearningRecord learningRecord = null;
    LearningResource learningResource = null;
    Person person = null;
    Email email = null;

    try {

      MessageVO messageVo = mapper.readValue(payload, MessageVO.class);
      Statement statement = messageVo.getStatement();

      // Parse xAPI Statement

      // Actor
      AbstractActor actor = (AbstractActor) statement.getActor();
      ObjectType actorType = actor.getObjectType();

      // Account
      Account account = actor.getAccount();

      // Result
      boolean completed = true;
      boolean success = true;
      Result result = statement.getResult();
      if (result != null) {
        completed = result.getCompletion();
        success = result.getSuccess();
      }

      // Object type
      String objType = statement.getObject().getObjectType().name();
      log.info("======> object type = " + objType);

      // If activity
      if (objType.equalsIgnoreCase("ACTIVITY")) {

        Activity activity = (Activity) statement.getObject();

        // Does person exist
        String ifi =
            Identity.createIfi(
                actor.getMbox_sha1sum(),
                actor.getMbox(),
                actor.getOpenid(),
                (account != null) ? account.getHomePage() : null,
                (account != null) ? account.getName() : null);

        Identity identity = identityService.getByIfi(ifi);

        // If person already exists
        if (identity != null) {

          log.info("person exists");
          person = identity.getPerson();

          // If person does not exist
        } else {

          // If email
          if (actor.getMbox() != null && actor.getMbox().length() > 0) {
            email = createEmail(actor);
          }

          person = createPerson(actor, email);
          identity = createIdentity(person, actor, account);
        }

        // Get learningResource
        learningResource = learningResourceService.findByIri(activity.getId());

        // If LearningResource doesn't exist
        if (learningResource == null) {
          learningResource = createLearningResource(activity);
        }

        // Get LearningRecord
        learningRecord =
            learningRecordService.findByPersonIdAndLearninResourceId(
                person.getId(), learningResource.getId());

        // If LearningRecord doesn't exist
        if (learningRecord == null) {
          learningRecord = createLearningRecord(person, learningResource, success, completed);

          // If learningRecord already exists
        } else {
          learningRecord = updateLearningRecord(person, learningRecord, learningResource);
        }

      } else if (objType.equalsIgnoreCase("AGENT")) {

      } else if (objType.equalsIgnoreCase("GROUP")) {

      } else if (objType.equalsIgnoreCase("STATEMENT_REF")) {

      } else if (objType.equalsIgnoreCase("SUB_STATEMENT")) {

      }

    } catch (JsonProcessingException e) {
      log.info("Exception while processing message.");
      e.printStackTrace();
    }
  }

  /**
   * @param statement
   */
  /*private void processMessageFromRule(final String payload) {
    ObjectMapper mapper = Mapper.getMapper();
    log.info("payload received " + payload);
    LearningRecord learningRecord = new LearningRecord();

    try {

      MessageVO messageVo = mapper.readValue(payload, MessageVO.class);
      Statement statement = messageVo.getStatement();
      learningRecord = DroolsProcessStatementService.processStatement(statement);

    } catch (JsonProcessingException e) {
      log.info("Exception while processing rule.");
      e.printStackTrace();
    }
  }*/

  /**
   * @param actor
   * @return Email
   */
  private Email createEmail(AbstractActor actor) {

    log.info("Creating new email.");
    Email email = new Email();
    email.setEmailAddress(actor.getMbox());
    email.setEmailAddressType("primary");
    email.setUpdatedBy(updatedBy);
    emailService.save(email);
    return email;
  }

  /**
   * @param actor
   * @param email
   * @return Person
   */
  private Person createPerson(AbstractActor actor, Email email) {
    log.info("Creating new person.");
    Person person = new Person();
    person.setName(actor.getName());
    person.setEmailAddresses(new HashSet<Email>()); // Populate person_email
    person.getEmailAddresses().add(email);
    person.setUpdatedBy(updatedBy);
    personService.save(person);
    return person;
  }

  /**
   * @param person
   * @param actor
   * @param account
   * @return Identity
   */
  private Identity createIdentity(Person person, AbstractActor actor, Account account) {
    log.info("Creating new identity.");
    Identity identity = new Identity();
    identity.setMboxSha1Sum(actor.getMbox_sha1sum());
    identity.setMbox(actor.getMbox());
    identity.setOpenid(actor.getOpenid());
    identity.setPerson(person);

    if (account != null) {
      identity.setHomePage(account.getHomePage());
      identity.setName(account.getName());
    }

    identity.setUpdatedBy(updatedBy);
    identityService.save(identity);
    return identity;
  }

  /**
   * @param activity
   * @return LearningResource
   */
  private LearningResource createLearningResource(Activity activity) {
    log.info("Creating new learning resource.");

    // Activity name
    String activityName = "";
    String nameLangCode = "";

    ActivityDefinition activityDefenition = activity.getDefinition();
    LangMap nameLangMap = activityDefenition.getName();

    if (nameLangMap != null) {
      Set<String> nameLangCodes = nameLangMap.getLanguageCodes();
      nameLangCode = nameLangCodes.iterator().next();
      activityName = activityDefenition.getName().get(nameLangCode);
    }

    // Activity Description
    String activityDescription = "";
    String langCode = "";

    LangMap descLangMap = activityDefenition.getDescription();

    if (descLangMap != null) {
      Set<String> descLangCodes = descLangMap.getLanguageCodes();
      langCode = descLangCodes.iterator().next();
      activityDescription = activityDefenition.getDescription().get(langCode);
    }

    LearningResource learningResource = new LearningResource();
    learningResource.setIri(activity.getId());
    learningResource.setDescription(activityDescription);
    learningResource.setNumber(activityName);
    learningResource.setTitle(activityDescription);
    learningResource.setUpdatedBy(updatedBy);
    learningResourceService.save(learningResource);
    return learningResource;
  }

  /**
   * @param person
   * @param learningResource
   * @param success
   * @param completed
   * @return LearningRecord
   */
  private LearningRecord createLearningRecord(
      Person person, LearningResource learningResource, boolean success, boolean completed) {
    log.info("Creating new learning record.");
    LearningRecord learningRecord = new LearningRecord();

    // status
    if (success && completed) {
      learningRecord.setRecordStatus(LearningStatus.COMPLETED);
    } else if (success && !completed) {
      learningRecord.setRecordStatus(LearningStatus.ATTEMPTED);
    } else {
      learningRecord.setRecordStatus(LearningStatus.FAILED);
    }

    learningRecord.setLearningResource(learningResource);
    learningRecord.setPerson(person);
    learningRecord.setUpdatedBy(updatedBy);
    learningRecordService.save(learningRecord);
    return learningRecord;
  }

  /**
   * @param person
   * @param learningRecord
   * @param learningResource
   * @return LearningRecord
   */
  private LearningRecord updateLearningRecord(
      Person person, LearningRecord learningRecord, LearningResource learningResource) {
    log.info("Update learning record.");
    learningRecord.setRecordStatus(LearningStatus.COMPLETED);
    learningRecord.setLearningResource(learningResource);
    learningRecord.setPerson(person);
    learningRecord.setUpdatedBy(updatedBy);
    learningRecordService.update(learningRecord);
    return learningRecord;
  }
}
