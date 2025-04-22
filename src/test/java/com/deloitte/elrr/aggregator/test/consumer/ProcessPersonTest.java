package com.deloitte.elrr.aggregator.test.consumer;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.deloitte.elrr.aggregator.consumer.ProcessPerson;
import com.deloitte.elrr.aggregator.test.util.TestFileUtils;
import com.deloitte.elrr.entity.Email;
import com.deloitte.elrr.entity.Identity;
import com.deloitte.elrr.entity.Person;
import com.deloitte.elrr.jpa.svc.EmailSvc;
import com.deloitte.elrr.jpa.svc.IdentitySvc;
import com.deloitte.elrr.jpa.svc.PersonSvc;
import com.yetanalytics.xapi.model.Statement;
import com.yetanalytics.xapi.util.Mapper;

import lombok.extern.slf4j.Slf4j;

@ExtendWith(MockitoExtension.class)
@Slf4j
class ProcessPersonTest {

  @Mock private EmailSvc emailSvc;

  @Mock private IdentitySvc identitySvc;

  @Mock private PersonSvc personSvc;

  @InjectMocks private ProcessPerson processPerson;

  @Test
  void test() {

    try {

      File testFile = TestFileUtils.getJsonTestFile("completed.json");

      Statement stmt = Mapper.getMapper().readValue(testFile, Statement.class);
      assertTrue(stmt != null);

      Email email = new Email();
      email.setId(UUID.randomUUID());
      email.setEmailAddressType("primary");
      email.setEmailAddress("mailto:test@gmail.com");
      Mockito.doReturn(email).when(emailSvc).save(any());

      Person person = new Person();
      person.setId(UUID.randomUUID());
      person.setName("test");
      person.setEmailAddresses(new HashSet<Email>()); // Populate person_email
      person.getEmailAddresses().add(email);
      Mockito.doReturn(person).when(personSvc).save(any());

      UUID identityUUID = UUID.randomUUID();
      Identity identity = new Identity();
      identity.setId(identityUUID);
      identity.setMbox("mailto:test@gmail.com");
      Mockito.doReturn(identity).when(identitySvc).getByIfi("mbox::mailto:test@gmail.com");

      person = processPerson.processPerson(stmt);
      assertTrue(person != null);
      assertTrue(person.getName().equalsIgnoreCase("test"));

    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
