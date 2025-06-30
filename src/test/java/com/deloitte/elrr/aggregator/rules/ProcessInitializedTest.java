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
class ProcessInitializedTest {

    @Mock
    private PersonSvc personService;

    @Mock
    private LearningResourceUtil learningResourceUtil;

    @Mock
    private LearningRecordUtil learningRecordUtil;

    @InjectMocks
    private ProcessInitialized processInitialized;

    @Test
    void test() {

        try {

            File testFile = TestFileUtil.getJsonTestFile("initialized.json");

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
            email.setEmailAddress("mailto:test@gmail.com");

            Person person = new Person();
            person.setId(UUID.randomUUID());
            person.setName("test");
            person.setEmailAddresses(new HashSet<Email>());
            person.getEmailAddresses().add(email);

            UUID identityUUID = UUID.randomUUID();
            Identity identity = new Identity();
            identity.setId(identityUUID);
            identity.setMbox("mailto:test@gmail.com");

            LearningResource learningResource = new LearningResource();
            learningResource.setId(UUID.randomUUID());
            learningResource.setTitle("Example Activity 10");
            learningResource.setDescription("Example activity 10 description");
            Mockito.doReturn(learningResource).when(learningResourceUtil)
                    .processLearningResource(activity);

            LearningRecord learningRecord = new LearningRecord();
            learningRecord.setId(UUID.randomUUID());
            learningRecord.setRecordStatus(LearningStatus.COMPLETED);
            learningRecord.setPerson(person);
            learningRecord.setLearningResource(learningResource);
            Mockito.doReturn(learningRecord).when(learningRecordUtil)
                    .processLearningRecord(person, verb, result,
                            learningResource);

            boolean fireRule = processInitialized.fireRule(stmt);
            assertTrue(fireRule);

            Person personResult = processInitialized.processRule(person, stmt);
            assertEquals(personResult.getName(), "test");

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
                    "Example Activity 10");
            assertEquals(learningRecord.getLearningResource().getDescription(),
                    "Example activity 10 description");

        } catch (IOException e) {
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

            boolean fireRule = processInitialized.fireRule(stmt);
            assertFalse(fireRule);

        } catch (IOException e) {
            fail("Should not have thrown any exception");
        }

    }

}
