package com.deloitte.elrr.aggregator.drools;

import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.deloitte.elrr.elrraggregator.exception.AggregatorException;
import com.deloitte.elrr.entity.Person;
import com.deloitte.elrr.jpa.svc.EmailSvc;
import com.deloitte.elrr.jpa.svc.IdentitySvc;
import com.deloitte.elrr.jpa.svc.LearningRecordSvc;
import com.deloitte.elrr.jpa.svc.LearningResourceSvc;
import com.yetanalytics.xapi.model.Statement;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DroolsProcessStatementService {

  @Autowired private KieContainer kieContainer;

  @Autowired private EmailSvc emailService;

  @Autowired private IdentitySvc identityService;

  @Autowired private LearningResourceSvc learningResourceService;

  @Autowired private LearningRecordSvc learningRecordService;

  public void processStatement(Person person, Statement statement) throws AggregatorException {

    try {

      KieSession kieSession = kieContainer.newKieSession();
      kieSession.setGlobal("identityService", identityService);
      kieSession.setGlobal("emailService", emailService);
      kieSession.setGlobal("learningRecordService", learningRecordService);
      kieSession.setGlobal("learningResourceService", learningResourceService);
      kieSession.setGlobal("person", person);
      kieSession.setGlobal("statement", statement);
      // Insert facts into working memory
      kieSession.insert(person);
      kieSession.insert(statement);
      kieSession.fireAllRules();
      kieSession.dispose();

    } catch (RuntimeException e) {
      log.info("Exception while creating KieSession.");
      throw new AggregatorException("Exception while creating KieSession. " + e.getMessage());
    }
  }
}
