package com.deloitte.elrr.aggregator.test.consumer;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.deloitte.elrr.aggregator.consumer.ProcessPerson;
import com.deloitte.elrr.aggregator.rules.ProcessCompetency;
import com.deloitte.elrr.aggregator.test.util.TestFileUtils;
import com.deloitte.elrr.aggregator.utils.LangMapUtil;
import com.deloitte.elrr.entity.Person;
import com.deloitte.elrr.jpa.svc.CompetencySvc;
import com.deloitte.elrr.jpa.svc.EmailSvc;
import com.deloitte.elrr.jpa.svc.IdentitySvc;
import com.deloitte.elrr.jpa.svc.PersonSvc;
import com.deloitte.elrr.jpa.svc.PersonalCompetencySvc;
import com.yetanalytics.xapi.model.Statement;
import com.yetanalytics.xapi.util.Mapper;

import lombok.extern.slf4j.Slf4j;

@ExtendWith(MockitoExtension.class)
@Slf4j
class ProcessCompetencyTest {

  @Mock private EmailSvc emailSvc;

  @Mock private IdentitySvc identitySvc;

  @Mock private PersonSvc personSvc;

  @Mock private LangMapUtil langMapUtil;

  @Mock CompetencySvc competencySvc;

  @Mock PersonalCompetencySvc personalCompetencySvc;

  @InjectMocks ProcessPerson processPerson;

  @InjectMocks ProcessCompetency processCompetency;

  @Test
  void test() {

    Person person = null;

    try {

      File testFile = TestFileUtils.getJsonTestFile("competency.json");

      Statement stmt = Mapper.getMapper().readValue(testFile, Statement.class);
      assertTrue(stmt != null);

      person = processPerson.processPerson(stmt);
      assertTrue(person != null);

      boolean fireRule = processCompetency.fireRule(stmt);
      assertTrue(fireRule);

      if (fireRule) {
        Person personResult = processCompetency.processRule(person, stmt);
        Set competancies = new HashSet();
        competancies = personResult.getCompetencies();
        assertTrue(competancies != null && !competancies.isEmpty());
      }

    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
