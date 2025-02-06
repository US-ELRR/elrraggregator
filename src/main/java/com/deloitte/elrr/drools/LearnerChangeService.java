package com.deloitte.elrr.drools;

import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.deloitte.elrr.elrrconsolidate.dto.LearnerChange;
import com.yetanalytics.xapi.model.Statement;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class LearnerChangeService {

  @Autowired private KieContainer kieContainer;

  public LearnerChange getLearnerChange(Statement statement) {

    LearnerChange learnerChange = new LearnerChange();
    try {

      KieSession kieSession = kieContainer.newKieSession();
      kieSession.setGlobal("learnerChange", learnerChange);
      kieSession.setGlobal("statement", statement);
      kieSession.insert(statement);
      kieSession.fireAllRules();
      kieSession.dispose();

    } catch (RuntimeException e) {
      log.info("Exception while creating KieSession.");
      e.printStackTrace();
    }
    return learnerChange;
  }
}
