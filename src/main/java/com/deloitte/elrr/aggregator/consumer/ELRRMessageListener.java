package com.deloitte.elrr.aggregator.consumer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
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
import com.yetanalytics.xapi.model.AbstractObject;
import com.yetanalytics.xapi.model.Account;
import com.yetanalytics.xapi.model.Activity;
import com.yetanalytics.xapi.model.ActivityDefinition;
import com.yetanalytics.xapi.model.LangMap;
import com.yetanalytics.xapi.model.ObjectType;
import com.yetanalytics.xapi.model.Result;
import com.yetanalytics.xapi.model.Statement;
import com.yetanalytics.xapi.model.Verb;
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

  @Autowired KafkaTemplate<?, String> kafkaTemplate;

  private static String updatedBy = "ELRR";

  /**
   * @param message
   */
  @KafkaListener(topics = "${kafka.topic}")
  public void listen(final String message) {

    log.info("Received Messasge in group - group-id: " + message);

    try {
      if (InputSanatizer.isValidInput(message)) {
        processMessage(message);
        // processMessageFromRule(message);
      } else {
        log.warn("Invalid message did not pass whitelist check - " + message);
      }
    } catch (Exception e) {
      // Send to dead letter queue
      kafkaTemplate.send("${kafka.dead.letter.topic}", message);
    }
  }

  /**
   * @param statement
   */
  private void processMessage(final String payload) throws JsonProcessingException {

    log.info("Process kafka message.");

    Account account = null;
    Email email = null;

    try {

      // Get Statement
      Statement statement = getStatement(payload);

      // Get Actor
      AbstractActor actor = getActor(statement);

      // Get Account
      // Rule #1 = If actor != null get account.
      if (actor != null) {
        account = getAccount(actor);
      }

      // Get Verb
      Verb verb = getVerb(statement);

      // Get Object
      AbstractObject obj = getObject(statement);

      // Get Result
      Result result = getResult(statement);

      // Rule #2 = If object != null get object type
      if (obj != null) {

        // Object type
        ObjectType objType = obj.getObjectType();
        log.info("Object type = " + objType.name());

        // Rule # 3 = If object != null and object type = ACTIVITY process activity.
        if (objType.compareTo(objType.ACTIVITY) == 0) {

          // Process Activity
          ArrayList<Object> activityList = processActivity(actor, account, verb, obj, result);

          // Parse activityList
          Person person = (Person) activityList.get(0);
          Identity identity = (Identity) activityList.get(1);

          if (activityList.get(2) != null) {
            email = (Email) activityList.get(2);
          }

          LearningResource learningResource = (LearningResource) activityList.get(3);
          LearningRecord learningRecord = (LearningRecord) activityList.get(1);

        } else if (objType.compareTo(objType.AGENT) == 0) {

        } else if (objType.compareTo(objType.GROUP) == 0) {

        } else if (objType.compareTo(objType.STATEMENT_REF) == 0) {

        } else if (objType.compareTo(objType.SUB_STATEMENT) == 0) {

        }
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
   * @param payload
   * @return Statement
   */
  private Statement getStatement(String payload) throws JsonProcessingException {
    Statement statement = null;
    ObjectMapper mapper = Mapper.getMapper();
    MessageVO messageVo;
    try {
      messageVo = mapper.readValue(payload, MessageVO.class);
      statement = messageVo.getStatement();
    } catch (JsonProcessingException e) {
      log.info("Exception while getting statement.");
      e.printStackTrace();
    }
    return statement;
  }

  /**
   * @param statement
   * @return AbstractActor
   */
  private AbstractActor getActor(Statement statement) {
    AbstractActor actor = (AbstractActor) statement.getActor();
    return actor;
  }

  /**
   * @param statement
   * @return Account
   */
  private Account getAccount(AbstractActor actor) {
    Account account = actor.getAccount();
    return account;
  }

  /**
   * @param actor
   * @param account
   * @return ArrayList
   */
  private ArrayList<Object> getPerson(AbstractActor actor, Account account) {

    Person person = null;
    Email email = null;
    ArrayList<Object> personList = new ArrayList<Object>(2);

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
      log.info("Person exists.");
      person = identity.getPerson();
      email = null;
    }

    personList.add(person);
    personList.add(identity);
    personList.add(email);

    return personList;
  }

  /**
   * @param statement
   * @return Verb
   */
  private Verb getVerb(Statement statement) {
    Verb verb = statement.getVerb();
    return verb;
  }

  /**
   * @param statement
   * @return AbstractObject
   */
  private AbstractObject getObject(Statement statement) {
    AbstractObject obj = statement.getObject();
    return obj;
  }

  /**
   * @param statement
   * @return Result
   */
  private Result getResult(Statement statement) {
    Result result = statement.getResult();
    return result;
  }

  /**
   * @param actor
   * @param account
   * @param verb
   * @param obj
   * @param result
   */
  private ArrayList<Object> processActivity(
      AbstractActor actor, Account account, Verb verb, AbstractObject obj, Result result) {

    log.info("Process activity.");

    // Get activity
    Activity activity = (Activity) obj;

    // Process Person, Identity and Email
    ArrayList<Object> personList = processPerson(actor, account);
    Person person = (Person) personList.get(0);

    // Process LearningResource
    LearningResource learningResource = processLearningResource(activity);

    // Process LearningRecord
    LearningRecord learningRecord =
        processLearningRecord(activity, person, result, learningResource);

    ArrayList<Object> activityList = new ArrayList<Object>(6);
    activityList.add(activity);
    activityList.add((Person) personList.get(0));
    activityList.add((Identity) personList.get(1));

    // If Email
    if (personList.get(2) != null) {
      activityList.add((Email) personList.get(2));
    } else {
      activityList.add(null);
    }

    activityList.add(learningResource);
    activityList.add(learningRecord);

    return activityList;
  }

  /**
   * @param actor
   * @param account
   * @return ArrayList
   */
  private ArrayList<Object> processPerson(AbstractActor actor, Account account) {

    Person person = null;
    ArrayList<Object> personList = new ArrayList<Object>(3);

    // Get person
    personList = getPerson(actor, account);
    person = (Person) personList.get(0);

    // If person doesn't exist
    if (person == null) {
      personList = createNewPerson(actor, account);
    }

    return personList;
  }

  /**
   * @param actor
   * @param account
   * @return ArrayList
   */
  private ArrayList<Object> createNewPerson(AbstractActor actor, Account account) {

    Email email = null;

    // If email
    if (actor.getMbox() != null && actor.getMbox().length() > 0) {
      email = createEmail(actor);
    }

    Person person = createPerson(actor, email);

    Identity identity = createIdentity(person, actor, account);

    ArrayList<Object> personList = new ArrayList<Object>(3);
    personList.add(person);
    personList.add(identity);
    personList.add(email);

    return personList;
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
   * @param activity
   * @return LearningResource
   */
  private LearningResource processLearningResource(Activity activity) {

    // Get learningResource
    LearningResource learningResource = learningResourceService.findByIri(activity.getId());

    // If LearningResource doesn't exist
    if (learningResource == null) {
      learningResource = createLearningResource(activity);
    }

    return learningResource;
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
   * @param Activity
   * @param Person
   * @param Result
   * @param LearningResource
   * @return LearningRccord
   */
  private LearningRecord processLearningRecord(
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
  private LearningRecord createLearningRecord(
      Person person, LearningResource learningResource, Result result) {

    log.info("Creating new learning record.");
    LearningRecord learningRecord = new LearningRecord();

    if (result != null) {

      boolean success = result.getSuccess();
      boolean completed = result.getCompletion();

      // status
      if (completed && success) {
        learningRecord.setRecordStatus(LearningStatus.PASSED);
      } else if (completed && !success) {
        learningRecord.setRecordStatus(LearningStatus.FAILED);
      } else if (completed) {
        learningRecord.setRecordStatus(LearningStatus.COMPLETED);
      } else {
        learningRecord.setRecordStatus(LearningStatus.ATTEMPTED);
      }

    } else {
      learningRecord.setRecordStatus(LearningStatus.ATTEMPTED);
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
