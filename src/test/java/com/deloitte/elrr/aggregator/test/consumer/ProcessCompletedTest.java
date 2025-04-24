package com.deloitte.elrr.aggregator.test.consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.deloitte.elrr.aggregator.rules.ProcessCompleted;
import com.deloitte.elrr.aggregator.test.util.TestFileUtils;
import com.deloitte.elrr.aggregator.utils.LangMapUtil;
import com.deloitte.elrr.aggregator.utils.LearningRecordUtil;
import com.deloitte.elrr.aggregator.utils.LearningResourceUtil;
import com.deloitte.elrr.entity.Email;
import com.deloitte.elrr.entity.Identity;
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
class ProcessCompletedTest {

  @Mock private EmailSvc emailSvc;

  @Mock private IdentitySvc identitySvc;

  @Mock private PersonSvc personSvc;

  @Mock private LangMapUtil langMapUtil;

  @Mock LearningResourceSvc learningResourceSvc;

  @Mock LearningRecordSvc learningRecordSvc;

  @Mock LearningResourceUtil learningResourceUtil;

  @Mock LearningRecordUtil learningRecordUtil;

  @InjectMocks ProcessCompleted processCompleted;

  @Test
  void test() {

    try {

      File testFile = TestFileUtils.getJsonTestFile("completed.json");

      Statement stmt = Mapper.getMapper().readValue(testFile, Statement.class);
      assertNotNull(stmt);

      Email email = new Email();
      email.setId(UUID.randomUUID());
      email.setEmailAddressType("primary");
      email.setEmailAddress("mailto:test@gmail.com");

      Person person = new Person();
      person.setId(UUID.randomUUID());
      person.setName("test");
      person.setEmailAddresses(new HashSet<Email>()); // Populate person_email
      person.getEmailAddresses().add(email);

      UUID identityUUID = UUID.randomUUID();
      Identity identity = new Identity();
      identity.setId(identityUUID);
      identity.setMbox("mailto:test@gmail.com");

      boolean fireRule = processCompleted.fireRule(stmt);
      assertTrue(fireRule);

      if (fireRule) {
        Person personResult = processCompleted.processRule(person, stmt);
        assertEquals(personResult.getName(), "test");
      }

    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
