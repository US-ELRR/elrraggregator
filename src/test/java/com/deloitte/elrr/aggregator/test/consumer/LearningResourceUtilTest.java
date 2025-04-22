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

import com.deloitte.elrr.aggregator.test.util.TestFileUtils;
import com.deloitte.elrr.aggregator.utils.LangMapUtil;
import com.deloitte.elrr.aggregator.utils.LearningResourceUtil;
import com.deloitte.elrr.entity.LearningResource;
import com.deloitte.elrr.jpa.svc.LearningResourceSvc;
import com.yetanalytics.xapi.model.Activity;
import com.yetanalytics.xapi.model.Statement;
import com.yetanalytics.xapi.util.Mapper;

import lombok.extern.slf4j.Slf4j;

@ExtendWith(MockitoExtension.class)
@Slf4j
class LearningResourceUtilTest {

  @Mock private LearningResourceSvc learningResourceSvc;

  @Spy private LangMapUtil langMapUtil;

  @InjectMocks private LearningResourceUtil learningResourceUtil;

  @Test
  void test() {

    try {

      File testFile = TestFileUtils.getJsonTestFile("completed.json");

      Statement stmt = Mapper.getMapper().readValue(testFile, Statement.class);
      assertTrue(stmt != null);

      Activity activity = (Activity) stmt.getObject();
      assertTrue(activity != null);

      LearningResource learningResource = learningResourceUtil.processLearningResource(activity);
      assertTrue(learningResource != null);
      assertTrue(
          learningResource
              .getIri()
              .equalsIgnoreCase("http://example.edlm/activities/activityTest"));
      assertTrue(learningResource.getTitle().equalsIgnoreCase("Activity 1"));
      assertTrue(learningResource.getDescription().equalsIgnoreCase("Example Activity Test"));

    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
