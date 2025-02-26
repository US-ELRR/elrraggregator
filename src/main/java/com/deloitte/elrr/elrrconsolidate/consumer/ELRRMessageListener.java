package com.deloitte.elrr.elrrconsolidate.consumer;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.deloitte.elrr.InputSanatizer;
import com.deloitte.elrr.drools.DroolsProcessStatementService;
import com.deloitte.elrr.elrrconsolidate.dto.MessageVO;
import com.deloitte.elrr.elrrconsolidate.service.ECCService;
import com.deloitte.elrr.entity.ELRRAuditLog;
import com.deloitte.elrr.entity.Email;
import com.deloitte.elrr.entity.Identity;
import com.deloitte.elrr.entity.LearningRecord;
import com.deloitte.elrr.entity.LearningResource;
import com.deloitte.elrr.entity.Person;
import com.deloitte.elrr.jpa.svc.ELRRAuditLogService;
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

  @Autowired private ELRRAuditLogService elrrAuditLogService;

  @Autowired private LearningResourceSvc learningResourceService;

  @Autowired private LearningRecordSvc learningRecordService;

  @Autowired private DroolsProcessStatementService droolsProcessStatementService;

  @Autowired private ECCService eccService;

  @Autowired private Identity identity;

  /**
   * @param message
   */
  @KafkaListener(topics = "${kafka.topic}")
  public void listen(final String message) {
    if (InputSanatizer.isValidInput(message)) {
      log.info("Received Messasge in group - group-id: " + message);
      processMessage(message);
      // Use Drools rule
      // LearnerChange learnerChange = processMessageFromRule(message);
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
      insertAuditLog(messageVo);
      Statement statement = messageVo.getStatement();

      // Parse xAPI Statement

      // Actor
      AbstractActor actor = (AbstractActor) statement.getActor();
      ObjectType actorType = actor.getObjectType();

      // Account
      Account account = actor.getAccount();

      // Verb
      String verbDisplay = "";

      Verb verb = statement.getVerb();

      if (verb != null) {
        verbDisplay = verb.getDisplay().get("en-us");
      }

      // Object type
      ObjectType objType = statement.getObjectType();

      // If Activity
      if (objType == objType.ACTIVITY) {

        Activity activity = (Activity) statement.getObject();

        // Id
        String id = "";
        id = activity.getId();

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

          person = identity.getPerson();

        } else {

          log.info("creating new identity");
          identity = new Identity();
          identity.setMboxSha1Sum(actor.getMbox_sha1sum());
          identity.setMbox(actor.getMbox());
          identity.setOpenid(actor.getOpenid());
          identity.setPerson(person);

          if (account != null) {
            identity.setHomePage(account.getHomePage());
            identity.setName(account.getName());
          }

          identityService.save(identity);

          log.info("creating new person");
          person = new Person();

          if (actor.getMbox() != null & actor.getMbox().length() > 0) {

            log.info("creating new email");
            email = new Email();
            email.setEmailAddress(actor.getMbox());
            email.setEmailAddressType("primary");
            emailService.save(email);
          }

          String[] tokens = actor.getName().split(" ");

          person.setFirstName(tokens[0]);

          // If first name only
          if (tokens.length == 1) {
            person.setMiddleName("");
            person.setLastName("");
          }

          // If first and last name
          if (tokens.length == 2) {
            person.setMiddleName("");
            person.setLastName(tokens[1]);
          }

          // If first, middle and last name
          if (tokens.length == 3) {
            person.setMiddleName(tokens[1]);
            person.setLastName(tokens[2]);
          }

          person.getEmailAddresses().add(email);
          personService.save(person);
        }

        // Get learningResource
        learningResource = eccService.getLearningResource(id);

        // If learningResource doesn't exist
        if (learningResource == null) {
          learningResource = new LearningResource();
          learningResource.setIri(id);
          learningResource.setDescription(activityDescription);
          learningResource.setNumber(activityName);
          learningResourceService.save(learningResource);
        }

        // Get LearningRecord
        // learningRecord = learningRecordService.getLearningRecord();

        // If learningRecord doesn't exist
        if (learningRecord == null) {
          learningRecord = new LearningRecord();
        }

        learningRecord.setLearningResource(learningResource);
        learningRecord.setPerson(person);
        learningRecordService.save(learningRecord);

        // If Agent
      } else if (objType == objType.AGENT) {

        // If Group
      } else if (objType == objType.GROUP) {

        // If Statement
      } else if (objType == objType.STATEMENT_REF) {

        // If Sub-Statement
      } else if (objType == objType.SUB_STATEMENT) {

      }

    } catch (JsonProcessingException e) {
      log.info("Exception while processing message.");
      e.printStackTrace();
    }
  }

  /**
   * @param statement
   * @return LearningRecord
   */
  private LearningRecord processMessageFromRule(final String payload) {
    ObjectMapper mapper = Mapper.getMapper();
    log.info("payload received " + payload);
    LearningRecord learningRecord = new LearningRecord();

    try {

      MessageVO messageVo = mapper.readValue(payload, MessageVO.class);
      insertAuditLog(messageVo);
      Statement statement = messageVo.getStatement();
      learningRecord = DroolsProcessStatementService.processStatement(statement);

    } catch (JsonProcessingException e) {
      log.info("Exception while inserting LearnerChange.");
      e.printStackTrace();
    }

    return learningRecord;
  }

  /**
   * @param messageVo
   */
  private void insertAuditLog(final MessageVO messageVo) {
    ELRRAuditLog auditLog = new ELRRAuditLog();
    auditLog.setSyncid(messageVo.getAuditRecord().getAuditId());
    elrrAuditLogService.save(auditLog);
  }
}
