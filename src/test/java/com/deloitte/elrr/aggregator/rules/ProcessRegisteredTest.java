package com.deloitte.elrr.aggregator.rules;

import static org.assertj.core.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.deloitte.elrr.aggregator.util.TestFileUtil;
import com.deloitte.elrr.aggregator.utils.LearningRecordUtil;
import com.deloitte.elrr.aggregator.utils.LearningResourceUtil;
import com.deloitte.elrr.entity.Email;
import com.deloitte.elrr.entity.Identity;
import com.deloitte.elrr.entity.LearningRecord;
import com.deloitte.elrr.entity.LearningResource;
import com.deloitte.elrr.entity.Person;
import com.deloitte.elrr.entity.types.LearningStatus;
import com.deloitte.elrr.jpa.svc.PersonSvc;
import com.yetanalytics.xapi.model.Activity;
import com.yetanalytics.xapi.model.Result;
import com.yetanalytics.xapi.model.Statement;
import com.yetanalytics.xapi.model.Verb;
import com.yetanalytics.xapi.util.Mapper;

import lombok.extern.slf4j.Slf4j;

@ExtendWith(MockitoExtension.class)
@Slf4j
class ProcessRegisteredTest {

    @Mock
    private PersonSvc personService;

    @Mock
    private LearningResourceUtil learningResourceUtil;

    @Mock
    private LearningRecordUtil learningRecordUtil;

    @InjectMocks
    private ProcessRegistered processRegistered;

    @Test
    void testWithObjectNameAndDescription() {

        try {

            File testFile = TestFileUtil.getJsonTestFile("registered.json");

            Statement stmt = Mapper.getMapper().readValue(testFile,
                    Statement.class);
            assertNotNull(stmt);

            Activity activity = (Activity) stmt.getObject();

            Verb verb = stmt.getVerb();
            assertNotNull(verb);

            Result result = stmt.getResult();

            Email email = new Email();
            email.setId(UUID.randomUUID());
            email.setEmailAddressType("primary");
            email.setEmailAddress("mailto:luke@jedi.com");

            Person person = new Person();
            person.setId(UUID.randomUUID());
            person.setName("Luke Skywalker");
            person.setEmailAddresses(new HashSet<Email>());
            person.getEmailAddresses().add(email);

            UUID identityUUID = UUID.randomUUID();
            Identity identity = new Identity();
            identity.setId(identityUUID);
            identity.setMbox("mailto:luke@jedi.com");

            LearningResource learningResource = new LearningResource();
            learningResource.setId(UUID.randomUUID());
            learningResource.setTitle("Example Registered Activity");
            learningResource.setDescription(
                    "Example Registered Activity Description");
            Mockito.doReturn(learningResource).when(learningResourceUtil)
                    .processLearningResource(activity);

            LearningRecord learningRecord = new LearningRecord();
            learningRecord.setId(UUID.randomUUID());
            learningRecord.setRecordStatus(LearningStatus.COMPLETED);
            learningRecord.setPerson(person);
            learningRecord.setLearningResource(learningResource);
            Mockito.doReturn(learningRecord).when(learningRecordUtil)
                    .processLearningRecord(person, verb, result,
                            learningResource, stmt.getTimestamp()
                                    .toLocalDate());

            boolean fireRule = processRegistered.fireRule(stmt);
            assertTrue(fireRule);

            Person personResult = processRegistered.processRule(person, stmt);
            assertEquals(personResult.getName(), "Luke Skywalker");

            Set<LearningRecord> learningRecords = personResult
                    .getLearningRecords();
            assertNotNull(learningRecords);
            learningRecord = learningRecords.stream().findFirst().orElse(null);

            assertNotNull(learningRecord);
            assertNotNull(learningRecord.getPerson());
            assertNotNull(learningRecord.getLearningResource());
            assertEquals(learningRecord.getRecordStatus(),
                    LearningStatus.COMPLETED);
            assertEquals(learningRecord.getLearningResource().getTitle(),
                    "Example Registered Activity");
            assertEquals(learningRecord.getLearningResource().getDescription(),
                    "Example Registered Activity Description");

        } catch (IOException e) {
            fail("Should not have thrown any exception");
        }
    }

    @Test
    void testWithObjectNameNoDescription() {

        try {

            File testFile = TestFileUtil.getJsonTestFile(
                    "registered_No_Object_Description.json");

            Statement stmt = Mapper.getMapper().readValue(testFile,
                    Statement.class);
            assertNotNull(stmt);

            Activity activity = (Activity) stmt.getObject();

            Verb verb = stmt.getVerb();
            assertNotNull(verb);

            Result result = stmt.getResult();

            Email email = new Email();
            email.setId(UUID.randomUUID());
            email.setEmailAddressType("primary");
            email.setEmailAddress("mailto:email37@example.com");

            Person person = new Person();
            person.setId(UUID.randomUUID());
            person.setName("Chief Warrant Officer 2 Kameron Koepp");
            person.setEmailAddresses(new HashSet<Email>());
            person.getEmailAddresses().add(email);

            UUID identityUUID = UUID.randomUUID();
            Identity identity = new Identity();
            identity.setId(identityUUID);
            identity.setMbox("mailto:email37@example.com");

            LearningResource learningResource = new LearningResource();
            learningResource.setId(UUID.randomUUID());
            learningResource.setTitle("USNCC-NAV-103-Naval Force Design");
            Mockito.doReturn(learningResource).when(learningResourceUtil)
                    .processLearningResource(activity);

            LearningRecord learningRecord = new LearningRecord();
            learningRecord.setId(UUID.randomUUID());
            learningRecord.setRecordStatus(LearningStatus.COMPLETED);
            learningRecord.setPerson(person);
            learningRecord.setLearningResource(learningResource);
            Mockito.doReturn(learningRecord).when(learningRecordUtil)
                    .processLearningRecord(person, verb, result,
                            learningResource, stmt.getTimestamp()
                                    .toLocalDate());

            boolean fireRule = processRegistered.fireRule(stmt);
            assertTrue(fireRule);

            Person personResult = processRegistered.processRule(person, stmt);
            assertEquals(personResult.getName(),
                    "Chief Warrant Officer 2 Kameron Koepp");

            Set<LearningRecord> learningRecords = personResult
                    .getLearningRecords();
            assertNotNull(learningRecords);
            learningRecord = learningRecords.stream().findFirst().orElse(null);

            assertNotNull(learningRecord);
            assertNotNull(learningRecord.getPerson());
            assertNotNull(learningRecord.getLearningResource());
            assertEquals(learningRecord.getRecordStatus(),
                    LearningStatus.COMPLETED);
            assertEquals(learningRecord.getLearningResource().getTitle(),
                    "USNCC-NAV-103-Naval Force Design");
            assertEquals(learningRecord.getLearningResource().getDescription(),
                    null);

        } catch (IOException e) {
            e.printStackTrace();
            fail("Should not have thrown any exception");
        }
    }

    @Test
    void testFireRule() {

        File testFile;

        try {

            testFile = TestFileUtil.getJsonTestFile("agent.json");

            Statement stmt = Mapper.getMapper().readValue(testFile,
                    Statement.class);
            assertNotNull(stmt);

            boolean fireRule = processRegistered.fireRule(stmt);
            assertFalse(fireRule);

        } catch (IOException e) {
            fail("Should not have thrown any exception");
        }

    }

}
