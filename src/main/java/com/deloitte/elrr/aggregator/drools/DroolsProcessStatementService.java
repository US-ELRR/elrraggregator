package com.deloitte.elrr.aggregator.drools;

import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.deloitte.elrr.elrraggregator.exception.AggregatorException;
import com.deloitte.elrr.entity.Person;
import com.deloitte.elrr.jpa.svc.CompetencySvc;
import com.deloitte.elrr.jpa.svc.CredentialSvc;
import com.deloitte.elrr.jpa.svc.LearningRecordSvc;
import com.deloitte.elrr.jpa.svc.LearningResourceSvc;
import com.deloitte.elrr.jpa.svc.PersonalCompetencySvc;
import com.deloitte.elrr.jpa.svc.PersonalCredentialSvc;
import com.yetanalytics.xapi.model.Statement;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DroolsProcessStatementService {

  @Autowired private KieContainer kieContainer;

  @Autowired private LearningResourceSvc learningResourceService;

  @Autowired private LearningRecordSvc learningRecordService;

  @Autowired private CompetencySvc competencyService;

  @Autowired private PersonalCompetencySvc personalCompetencyService;

  @Autowired private CredentialSvc credentialService;

  @Autowired private PersonalCredentialSvc personalCredentialService;

  public void processStatement(Person person, Statement statement, String verbId, String objType)
      throws AggregatorException {

    try {

      KieSession kieSession = kieContainer.newKieSession();
      kieSession.setGlobal("learningRecordService", learningRecordService);
      kieSession.setGlobal("learningResourceService", learningResourceService);
      kieSession.setGlobal("competencyService", competencyService);
      kieSession.setGlobal("personalCompetencyService", personalCompetencyService);
      kieSession.setGlobal("credentialService", credentialService);
      kieSession.setGlobal("personalCredentialService", personalCredentialService);
      kieSession.setGlobal("person", person);
      kieSession.setGlobal("statement", statement);
      kieSession.setGlobal("verbId", verbId);
      kieSession.setGlobal("objType", objType);
      // Insert facts into working memory
      kieSession.insert(person);
      kieSession.insert(statement);
      kieSession.insert(verbId);
      kieSession.fireAllRules();
      kieSession.dispose();

    } catch (RuntimeException e) {
      log.info("Exception while creating KieSession.");
      throw new AggregatorException("Exception while creating KieSession. " + e.getMessage());
    }
  }
}
