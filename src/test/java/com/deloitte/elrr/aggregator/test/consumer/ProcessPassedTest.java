package com.deloitte.elrr.aggregator.test.consumer;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.deloitte.elrr.aggregator.consumer.ProcessPerson;
import com.deloitte.elrr.aggregator.rules.ProcessPassed;
import com.deloitte.elrr.aggregator.test.util.TestFileUtils;
import com.deloitte.elrr.aggregator.utils.LangMapUtil;
import com.deloitte.elrr.aggregator.utils.LearningRecordUtil;
import com.deloitte.elrr.aggregator.utils.LearningResourceUtil;
import com.deloitte.elrr.entity.Person;
import com.deloitte.elrr.jpa.svc.EmailSvc;
import com.deloitte.elrr.jpa.svc.IdentitySvc;
import com.deloitte.elrr.jpa.svc.LearningRecordSvc;
import com.deloitte.elrr.jpa.svc.LearningResourceSvc;
import com.deloitte.elrr.jpa.svc.PersonSvc;
import com.yetanalytics.xapi.model.Statement;
import com.yetanalytics.xapi.util.Mapper;

import lombok.extern.slf4j.Slf4j;

@ExtendWith(MockitoExtension.class)
@Slf4j
class ProcessPassedTest {

  @Mock private EmailSvc emailSvc;

  @Mock private IdentitySvc identitySvc;

  @Mock private PersonSvc personSvc;

  @Mock private LangMapUtil langMapUtil;

  @Mock LearningResourceSvc learningResourceSvc;

  @Mock LearningRecordSvc learningRecordSvc;

  @Mock LearningResourceUtil learningResourceUtil;

  @Mock LearningRecordUtil learningRecordUtil;

  @InjectMocks ProcessPerson processPerson;

  @InjectMocks ProcessPassed processPassed;

  @Test
  void test() {

    Person person = null;

    try {

      File testFile = TestFileUtils.getJsonTestFile("passed.json");

      Statement stmt = Mapper.getMapper().readValue(testFile, Statement.class);
      assertTrue(stmt != null);

      person = processPerson.processPerson(stmt);
      assertTrue(person != null);

      processPassed.processRule(person, stmt);

    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
