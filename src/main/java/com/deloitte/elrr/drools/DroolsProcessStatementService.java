package com.deloitte.elrr.drools;

import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.deloitte.elrr.entity.LearningRecord;
import com.yetanalytics.xapi.model.Statement;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DroolsProcessStatementService {

  @Autowired private static KieContainer kieContainer;

  public static LearningRecord processStatement(Statement statement) {

    LearningRecord learningRecord = new LearningRecord();
    try {

      KieSession kieSession = kieContainer.newKieSession();
      kieSession.setGlobal("learningRecord", learningRecord);
      kieSession.setGlobal("statement", statement);
      kieSession.insert(statement);
      kieSession.fireAllRules();
      kieSession.dispose();

    } catch (RuntimeException e) {
      log.info("Exception while creating KieSession.");
      e.printStackTrace();
    }
    return learningRecord;
  }
}
