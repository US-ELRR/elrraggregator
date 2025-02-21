package com.deloitte.elrr.elrrconsolidate.consumer;

import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.deloitte.elrr.InputSanatizer;
import com.deloitte.elrr.drools.DroolsProcessStatementService;
import com.deloitte.elrr.elrrconsolidate.dto.MessageVO;
import com.deloitte.elrr.elrrconsolidate.entity.ELRRAuditLog;
import com.deloitte.elrr.elrrconsolidate.jpa.service.ELRRAuditLogService;
import com.deloitte.elrr.elrrconsolidate.service.ECCService;
import com.deloitte.elrr.elrrconsolidate.service.HRService;
import com.deloitte.elrr.entity.Identity;
import com.deloitte.elrr.entity.LearningRecord;
import com.deloitte.elrr.entity.LearningResource;
import com.deloitte.elrr.entity.Person;
import com.deloitte.elrr.jpa.svc.IdentitySvc;
import com.deloitte.elrr.jpa.svc.LearningRecordSvc;
import com.deloitte.elrr.jpa.svc.LearningResourceSvc;
import com.deloitte.elrr.jpa.svc.PersonSvc;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yetanalytics.xapi.model.AbstractActor;
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

  @Autowired private HRService hrService;

  @Autowired private IdentitySvc identitySvc;

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

    try {

      MessageVO messageVo = mapper.readValue(payload, MessageVO.class);
      insertAuditLog(messageVo);
      Statement statement = messageVo.getStatement();

      String actorName = "";
      String actorEmail = "";
      String homePage = "";
      String name = "";
      String openId = "";
      String mboxSha1Sum = "";

      // Parse xAPI Statement

      // Actor
      AbstractActor actor = (AbstractActor) statement.getActor();
      ObjectType actorType = actor.getObjectType();

      // If Agent
      if (actorType == actorType.AGENT) {

        actorName = actor.getName();
        actorEmail = actor.getMbox();

        // If Activity
      } else if (actorType == actorType.ACTIVITY) {

        // If Group
      } else if (actorType == actorType.GROUP) {

        // If Statement
      } else if (actorType == actorType.STATEMENT_REF) {

        // If Sub Statement
      } else if (actorType == actorType.SUB_STATEMENT) {

      }

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

        // Authority
        AbstractActor authority = statement.getAuthority();
        openId = authority.getOpenid();
        ObjectType authorityType = authority.getObjectType();

        // If Agent
        if (authorityType == authorityType.AGENT) {

          homePage = authority.getAccount().getHomePage();
          name = authority.getAccount().getName();

          // If Activity
        } else if (authorityType == authorityType.ACTIVITY) {

          // If Group
        } else if (authorityType == authorityType.GROUP) {

          // If Statement
        } else if (authorityType == authorityType.STATEMENT_REF) {

          // If Sub Statement
        } else if (authorityType == authorityType.SUB_STATEMENT) {

        }

        // Use xAPI Statement values

        // Get person
        String ifi = Identity.createIfi(mboxSha1Sum, actorEmail, openId, homePage, name);

        // If person not found, create person
        if (ifi == null) {
          person = hrService.createPerson(actorName, actorEmail);
        } else {
          person = identity.getPerson();
        }

        UUID personId = person.getId();

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
        learningRecord = learningRecordService.getLearningRecord();

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
