package com.deloitte.elrr.aggregator.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.File;
import java.io.IOException;
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
                    .processLearningRecord(activity, person, verb, result,
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
            e.printStackTrace();
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
                    .processLearningRecord(activity, person, verb, result,
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
            e.printStackTrace();
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
                    .processLearningRecord(activity, person, verb, result,
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
            e.printStackTrace();
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
                    .processLearningRecord(activity, person, verb, result,
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
            e.printStackTrace();
        }
    }

}
