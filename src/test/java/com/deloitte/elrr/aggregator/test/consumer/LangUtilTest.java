package com.deloitte.elrr.aggregator.test.consumer;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.deloitte.elrr.aggregator.test.util.TestFileUtils;
import com.deloitte.elrr.aggregator.utils.LangMapUtil;
import com.yetanalytics.xapi.model.Activity;
import com.yetanalytics.xapi.model.Statement;
import com.yetanalytics.xapi.util.Mapper;

import lombok.extern.slf4j.Slf4j;

@ExtendWith(MockitoExtension.class)
@Slf4j
class LangUtilTest {

  @Test
  void test() {

    String activityName = "";
    String activityDescription = "";

    try {

      LangMapUtil langMapUtil = new LangMapUtil();

      File testFile = TestFileUtils.getJsonTestFile("completed.json");

      Statement stmt = Mapper.getMapper().readValue(testFile, Statement.class);
      assertTrue(stmt != null);

      Activity activity = (Activity) stmt.getObject();
      assertTrue(activity != null);

      activityName = langMapUtil.getLangMapValue(activity.getDefinition().getName());
      activityDescription = langMapUtil.getLangMapValue(activity.getDefinition().getDescription());
      assertTrue(activityName.equalsIgnoreCase("Activity 1"));
      assertTrue(activityDescription.equalsIgnoreCase("Example Activity Test"));

    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
