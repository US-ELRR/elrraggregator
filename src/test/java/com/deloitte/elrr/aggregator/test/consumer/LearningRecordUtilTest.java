package com.deloitte.elrr.aggregator.test.consumer;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import com.deloitte.elrr.aggregator.consumer.ProcessPerson;
import com.deloitte.elrr.aggregator.test.util.TestFileUtils;
import com.deloitte.elrr.aggregator.utils.LangMapUtil;
import com.deloitte.elrr.aggregator.utils.LearningRecordUtil;
import com.deloitte.elrr.aggregator.utils.LearningResourceUtil;
import com.deloitte.elrr.entity.LearningRecord;
import com.deloitte.elrr.entity.LearningResource;
import com.deloitte.elrr.entity.Person;
import com.deloitte.elrr.jpa.svc.EmailSvc;
import com.deloitte.elrr.jpa.svc.IdentitySvc;
import com.deloitte.elrr.jpa.svc.LearningRecordSvc;
import com.deloitte.elrr.jpa.svc.LearningResourceSvc;
import com.deloitte.elrr.jpa.svc.PersonSvc;
import com.yetanalytics.xapi.model.Activity;
import com.yetanalytics.xapi.model.Result;
import com.yetanalytics.xapi.model.Statement;
import com.yetanalytics.xapi.model.Verb;
import com.yetanalytics.xapi.util.Mapper;

import lombok.extern.slf4j.Slf4j;

@ExtendWith(MockitoExtension.class)
@Slf4j
class LearningRecordUtilTest {

  @Mock private EmailSvc emailSvc;

  @Mock private IdentitySvc identitySvc;

  @Mock private PersonSvc personSvc;

  @Mock private LearningResourceSvc learningResourceSvc;

  @Mock private LearningRecordSvc learningRecordSvc;

  @Spy private LangMapUtil langMapUtil;

  @InjectMocks ProcessPerson processPerson;

  @InjectMocks private LearningResourceUtil learningResourceUtil;

  @InjectMocks private LearningRecordUtil learningRecordUtil;

  @Test
  void test() {

    Person person = null;
    try {

      File testFile = TestFileUtils.getJsonTestFile("completed.json");

      Statement stmt = Mapper.getMapper().readValue(testFile, Statement.class);
      assertTrue(stmt != null);

      Activity activity = (Activity) stmt.getObject();
      assertTrue(activity != null);

      Verb verb = stmt.getVerb();
      assertTrue(verb != null);

      Result result = stmt.getResult();
      assertTrue(result != null);

      person = processPerson.processPerson(stmt);
      assertTrue(person != null);
      log.info("Person id = " + person.getId());

      LearningResource learningResource = learningResourceUtil.processLearningResource(activity);
      assertTrue(learningResource != null);
      assertTrue(
          learningResource
              .getIri()
              .equalsIgnoreCase("http://example.edlm/activities/activityTest"));
      assertTrue(learningResource.getTitle().equalsIgnoreCase("Activity 1"));
      assertTrue(learningResource.getDescription().equalsIgnoreCase("Example Activity Test"));
      log.info("Learning Resource id = " + learningResource.getId());

      LearningRecord learningRecord =
          learningRecordUtil.processLearningRecord(
              activity, person, verb, result, learningResource);
      // assertTrue(learningRecord != null);

    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
