package com.deloitte.elrr.aggregator.util;

import static org.assertj.core.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.deloitte.elrr.aggregator.utils.LangMapUtil;
import com.deloitte.elrr.aggregator.utils.LearningRecordUtil;
import com.deloitte.elrr.entity.LearningRecord;
import com.deloitte.elrr.entity.LearningResource;
import com.deloitte.elrr.entity.Person;
import com.deloitte.elrr.entity.types.LearningStatus;
import com.deloitte.elrr.jpa.svc.LearningRecordSvc;
import com.yetanalytics.xapi.model.Activity;
import com.yetanalytics.xapi.model.Result;
import com.yetanalytics.xapi.model.Statement;
import com.yetanalytics.xapi.model.Verb;
import com.yetanalytics.xapi.util.Mapper;

import lombok.extern.slf4j.Slf4j;

@ExtendWith(MockitoExtension.class)
@Slf4j
class LearningRecordUtilTest {

    @Mock
    private LearningRecordSvc learningRecordSvc;

    @Mock
    private LangMapUtil langMapUtil;

    @InjectMocks
    private LearningRecordUtil learningRecordUtil;

    @Test
    void testCompleted() {

        try {

            File testFile = TestFileUtil.getJsonTestFile("completed.json");

            Statement stmt = Mapper.getMapper().readValue(testFile,
                    Statement.class);
            assertNotNull(stmt);

            Activity activity = (Activity) stmt.getObject();
            assertNotNull(activity);

            Verb verb = stmt.getVerb();
            assertNotNull(verb);

            Result result = stmt.getResult();

            Person person = new Person();
            person.setId(UUID.randomUUID());
            person.setName("test");

            LearningResource learningResource = new LearningResource();
            learningResource.setId(UUID.randomUUID());
            learningResource.setTitle("Activity 1");
            learningResource.setDescription("Example Activity Test");

            LearningRecord learningRecord = learningRecordUtil
                    .processLearningRecord(person, verb, result,
                            learningResource);
            assertNotNull(learningRecord);
            assertNotNull(learningRecord.getPerson());
            assertNotNull(learningRecord.getLearningResource());
            assertEquals(learningRecord.getRecordStatus(),
                    LearningStatus.COMPLETED);
            assertEquals(learningRecord.getLearningResource().getTitle(),
                    "Activity 1");
            assertEquals(learningRecord.getLearningResource().getDescription(),
                    "Example Activity Test");

        } catch (IOException e) {
            fail("Should not have thrown any exception");
        }
    }

    @Test
    void testCompletedUpdate() {

        try {

            File testFile = TestFileUtil.getJsonTestFile("completed.json");

            Statement stmt = Mapper.getMapper().readValue(testFile,
                    Statement.class);
            assertNotNull(stmt);

            Activity activity = (Activity) stmt.getObject();
            assertNotNull(activity);

            Verb verb = stmt.getVerb();
            assertNotNull(verb);

            Result result = stmt.getResult();

            LocalDateTime enrollmentDate = stmt.getTimestamp()
                    .toLocalDateTime();

            Person person = new Person();
            person.setId(UUID.randomUUID());
            person.setName("test");

            LearningResource learningResource = new LearningResource();
            learningResource.setId(UUID.randomUUID());
            learningResource.setTitle("Activity 1");
            learningResource.setDescription("Example Activity Test");

            LearningRecord learningRecord = new LearningRecord();
            learningRecord.setId(UUID.randomUUID());
            learningRecord.setPerson(person);
            learningRecord.setLearningResource(learningResource);

            LearningRecord learningRecordResult = learningRecordUtil
                    .updateLearningRecord(person, learningRecord, verb, result,
                            enrollmentDate);
            assertNotNull(learningRecordResult);

        } catch (IOException e) {
            fail("Should not have thrown any exception");
        }
    }

    @Test
    void testPassed() {

        try {

            File testFile = TestFileUtil.getJsonTestFile("passed.json");

            Statement stmt = Mapper.getMapper().readValue(testFile,
                    Statement.class);
            assertNotNull(stmt);

            Activity activity = (Activity) stmt.getObject();
            assertNotNull(activity);

            Verb verb = stmt.getVerb();
            assertNotNull(verb);

            Result result = stmt.getResult();

            Person person = new Person();
            person.setId(UUID.randomUUID());
            person.setName("Tom Brady");

            LearningResource learningResource = new LearningResource();
            learningResource.setId(UUID.randomUUID());
            learningResource.setTitle("simple CBT 2 course");
            learningResource.setDescription(
                    "A fictitious example CBT 2 course.");

            LearningRecord learningRecord = learningRecordUtil
                    .processLearningRecord(person, verb, result,
                            learningResource);
            assertNotNull(learningRecord);
            assertNotNull(learningRecord.getPerson());
            assertNotNull(learningRecord.getLearningResource());
            assertEquals(learningRecord.getRecordStatus(),
                    LearningStatus.PASSED);
            assertEquals(learningRecord.getLearningResource().getTitle(),
                    "simple CBT 2 course");
            assertEquals(learningRecord.getLearningResource().getDescription(),
                    "A fictitious example CBT 2 course.");

        } catch (IOException e) {
            fail("Should not have thrown any exception");
        }
    }

    @Test
    void testPassedUpdate() {

        try {

            File testFile = TestFileUtil.getJsonTestFile("passed.json");

            Statement stmt = Mapper.getMapper().readValue(testFile,
                    Statement.class);
            assertNotNull(stmt);

            Activity activity = (Activity) stmt.getObject();
            assertNotNull(activity);

            Verb verb = stmt.getVerb();
            assertNotNull(verb);

            Result result = stmt.getResult();

            Person person = new Person();
            person.setId(UUID.randomUUID());
            person.setName("Tom Brady");

            LearningResource learningResource = new LearningResource();
            learningResource.setId(UUID.randomUUID());
            learningResource.setTitle("simple CBT 2 course");
            learningResource.setDescription(
                    "A fictitious example CBT 2 course.");

            LearningRecord learningRecord = new LearningRecord();
            learningRecord.setId(UUID.randomUUID());
            learningRecord.setPerson(person);
            learningRecord.setLearningResource(learningResource);

            learningRecord = learningRecordUtil.updateLearningRecord(person,
                    learningRecord, verb, result);
            assertNotNull(learningRecord);
            assertNotNull(learningRecord.getPerson());
            assertNotNull(learningRecord.getLearningResource());
            assertEquals(learningRecord.getRecordStatus(),
                    LearningStatus.PASSED);
            assertEquals(learningRecord.getLearningResource().getTitle(),
                    "simple CBT 2 course");
            assertEquals(learningRecord.getLearningResource().getDescription(),
                    "A fictitious example CBT 2 course.");
        } catch (IOException e) {
            fail("Should not have thrown any exception");
        }
    }

    @Test
    void testFailed() {

        try {

            File testFile = TestFileUtil.getJsonTestFile("failed.json");

            Statement stmt = Mapper.getMapper().readValue(testFile,
                    Statement.class);
            assertNotNull(stmt);

            Activity activity = (Activity) stmt.getObject();
            assertNotNull(activity);

            Verb verb = stmt.getVerb();
            assertNotNull(verb);

            Result result = stmt.getResult();

            Person person = new Person();
            person.setId(UUID.randomUUID());
            person.setName("Example Learner");

            LearningResource learningResource = new LearningResource();
            learningResource.setId(UUID.randomUUID());
            learningResource.setTitle("simple CBT course");
            learningResource.setDescription("A fictitious example CBT course.");

            LearningRecord learningRecord = learningRecordUtil
                    .processLearningRecord(person, verb, result,
                            learningResource);
            assertNotNull(learningRecord);
            assertNotNull(learningRecord.getPerson());
            assertNotNull(learningRecord.getLearningResource());
            assertEquals(learningRecord.getRecordStatus(),
                    LearningStatus.FAILED);
            assertEquals(learningRecord.getLearningResource().getTitle(),
                    "simple CBT course");
            assertEquals(learningRecord.getLearningResource().getDescription(),
                    "A fictitious example CBT course.");

        } catch (IOException e) {
            fail("Should not have thrown any exception");
        }
    }

    @Test
    void testInitialized() {

        try {

            File testFile = TestFileUtil.getJsonTestFile("initialized.json");

            Statement stmt = Mapper.getMapper().readValue(testFile,
                    Statement.class);
            assertNotNull(stmt);

            Activity activity = (Activity) stmt.getObject();
            assertNotNull(activity);

            Verb verb = stmt.getVerb();
            assertNotNull(verb);

            Result result = stmt.getResult();

            Person person = new Person();
            person.setId(UUID.randomUUID());
            person.setName("Robert Engle");

            LearningResource learningResource = new LearningResource();
            learningResource.setId(UUID.randomUUID());
            learningResource.setTitle("Example Activity 10");
            learningResource.setDescription("Example activity 10 description");

            LearningRecord learningRecord = learningRecordUtil
                    .processLearningRecord(person, verb, result,
                            learningResource);
            assertNotNull(learningRecord);
            assertNotNull(learningRecord.getPerson());
            assertNotNull(learningRecord.getLearningResource());
            assertEquals(learningRecord.getRecordStatus(),
                    LearningStatus.ATTEMPTED);
            assertEquals(learningRecord.getLearningResource().getTitle(),
                    "Example Activity 10");
            assertEquals(learningRecord.getLearningResource().getDescription(),
                    "Example activity 10 description");

        } catch (IOException e) {
            fail("Should not have thrown any exception");
        }
    }

    @Test
    void testRegistered() {

        try {

            File testFile = TestFileUtil.getJsonTestFile("registered.json");

            Statement stmt = Mapper.getMapper().readValue(testFile,
                    Statement.class);
            assertNotNull(stmt);

            Activity activity = (Activity) stmt.getObject();
            assertNotNull(activity);

            Verb verb = stmt.getVerb();
            assertNotNull(verb);

            Result result = stmt.getResult();

            LocalDateTime enrollmentDate = stmt.getTimestamp()
                    .toLocalDateTime();

            Person person = new Person();
            person.setId(UUID.randomUUID());
            person.setName("Luke Skywalker");

            LearningResource learningResource = new LearningResource();
            learningResource.setId(UUID.randomUUID());
            learningResource.setTitle("Example Registered Activity");
            learningResource.setDescription(
                    "Example Registered Activity Description");

            LearningRecord learningRecord = learningRecordUtil
                    .processLearningRecord(person, verb, result,
                            learningResource, enrollmentDate);
            assertNotNull(learningRecord);
            assertNotNull(learningRecord.getPerson());
            assertNotNull(learningRecord.getLearningResource());
            assertEquals(learningRecord.getRecordStatus(),
                    LearningStatus.REGISTERED);
            assertEquals(learningRecord.getLearningResource().getTitle(),
                    "Example Registered Activity");
            assertEquals(learningRecord.getLearningResource().getDescription(),
                    "Example Registered Activity Description");

        } catch (IOException e) {
            fail("Should not have thrown any exception");
        }
    }

    @Test
    void testRegisteredUpdate() {

        try {

            File testFile = TestFileUtil.getJsonTestFile("registered.json");

            Statement stmt = Mapper.getMapper().readValue(testFile,
                    Statement.class);
            assertNotNull(stmt);

            Activity activity = (Activity) stmt.getObject();
            assertNotNull(activity);

            Verb verb = stmt.getVerb();
            assertNotNull(verb);

            Result result = stmt.getResult();

            LocalDateTime enrollmentDate = stmt.getTimestamp()
                    .toLocalDateTime();

            Person person = new Person();
            person.setId(UUID.randomUUID());
            person.setName("Luke Skywalker");

            LearningResource learningResource = new LearningResource();
            learningResource.setId(UUID.randomUUID());
            learningResource.setTitle("Example Registered Activity");
            learningResource.setDescription(
                    "Example Registered Activity Description");

            LearningRecord learningRecord = new LearningRecord();
            learningRecord.setId(UUID.randomUUID());
            learningRecord.setPerson(person);
            learningRecord.setLearningResource(learningResource);

            learningRecord = learningRecordUtil.updateLearningRecord(person,
                    learningRecord, verb, result, enrollmentDate);
            assertNotNull(learningRecord);
            assertNotNull(learningRecord.getPerson());
            assertNotNull(learningRecord.getLearningResource());
            assertEquals(learningRecord.getRecordStatus(),
                    LearningStatus.REGISTERED);
            assertEquals(learningRecord.getLearningResource().getTitle(),
                    "Example Registered Activity");
            assertEquals(learningRecord.getLearningResource().getDescription(),
                    "Example Registered Activity Description");

        } catch (IOException e) {
            fail("Should not have thrown any exception");
        }
    }

    @Test
    void testAssigned() {

        try {

            File testFile = TestFileUtil.getJsonTestFile("assigned.json");

            Statement stmt = Mapper.getMapper().readValue(testFile,
                    Statement.class);
            assertNotNull(stmt);

            Activity activity = (Activity) stmt.getObject();
            assertNotNull(activity);

            Verb verb = stmt.getVerb();
            assertNotNull(verb);

            Result result = stmt.getResult();

            LocalDateTime enrollmentDate = stmt.getTimestamp()
                    .toLocalDateTime();

            Person person = new Person();
            person.setId(UUID.randomUUID());
            person.setName("Robert Po");

            LearningResource learningResource = new LearningResource();
            learningResource.setId(UUID.randomUUID());
            learningResource.setTitle("Extension to");
            learningResource.setDescription("Example to");

            Map<URI, Object> map = new HashMap<URI, Object>();
            URI uri1 = new URI(
                    "https://yetanalytics.com/profiles/prepositions/concepts/context-extensions/to");
            map.put(uri1, "assign to Phil.");

            LearningRecord learningRecord = learningRecordUtil
                    .processLearningRecord(person, verb, result,
                            learningResource, map);
            assertNotNull(learningRecord);
            assertNotNull(learningRecord.getPerson());
            assertNotNull(learningRecord.getLearningResource());
            assertEquals(learningRecord.getRecordStatus(),
                    LearningStatus.ATTEMPTED);
            assertEquals(learningRecord.getLearningResource().getTitle(),
                    "Extension to");
            assertEquals(learningRecord.getLearningResource().getDescription(),
                    "Example to");

        } catch (IOException | URISyntaxException e) {
            fail("Should not have thrown any exception");
        }
    }

    @Test
    void testAssignedUpdate() {

        try {

            File testFile = TestFileUtil.getJsonTestFile("assigned.json");

            Statement stmt = Mapper.getMapper().readValue(testFile,
                    Statement.class);
            assertNotNull(stmt);

            Activity activity = (Activity) stmt.getObject();
            assertNotNull(activity);

            Verb verb = stmt.getVerb();
            assertNotNull(verb);

            Result result = stmt.getResult();

            LocalDateTime enrollmentDate = stmt.getTimestamp()
                    .toLocalDateTime();

            Person person = new Person();
            person.setId(UUID.randomUUID());
            person.setName("Robert Po");

            LearningResource learningResource = new LearningResource();
            learningResource.setId(UUID.randomUUID());
            learningResource.setTitle("Extension to");
            learningResource.setDescription("Example to");

            Map<URI, Object> map = new HashMap<URI, Object>();
            URI uri1 = new URI(
                    "https://yetanalytics.com/profiles/prepositions/concepts/context-extensions/to");
            map.put(uri1, "assign to Phil.");

            LearningRecord learningRecord = new LearningRecord();
            learningRecord.setId(UUID.randomUUID());
            learningRecord.setPerson(person);
            learningRecord.setLearningResource(learningResource);

            learningRecord = learningRecordUtil.updateLearningRecord(person,
                    learningRecord, verb, result, map);
            assertNotNull(learningRecord);
            assertNotNull(learningRecord.getPerson());
            assertNotNull(learningRecord.getLearningResource());
            assertEquals(learningRecord.getRecordStatus(),
                    LearningStatus.ATTEMPTED);
            assertEquals(learningRecord.getLearningResource().getTitle(),
                    "Extension to");
            assertEquals(learningRecord.getLearningResource().getDescription(),
                    "Example to");

        } catch (IOException | URISyntaxException e) {
            fail("Should not have thrown any exception");
        }
    }

}
