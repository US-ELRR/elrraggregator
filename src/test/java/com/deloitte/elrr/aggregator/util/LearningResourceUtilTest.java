package com.deloitte.elrr.aggregator.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

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

    @Mock
    private LearningResourceSvc learningResourceSvc;

    @Spy
    private LangMapUtil langMapUtil;

    @InjectMocks
    private LearningResourceUtil learningResourceUtil;

    @Test
    void test() {

        try {

            File testFile = TestFileUtil.getJsonTestFile("completed.json");

            Statement stmt = Mapper.getMapper().readValue(testFile,
                    Statement.class);
            assertNotNull(stmt);

            Activity activity = (Activity) stmt.getObject();
            assertNotNull(activity);

            LearningResource learningResource = learningResourceUtil
                    .processLearningResource(activity);
            assertNotNull(learningResource);
            assertEquals(learningResource.getIri(),
                    "http://example.edlm/activities/activityTest");
            assertEquals(learningResource.getTitle(), "Activity 1");
            assertEquals(learningResource.getDescription(),
                    "Example Activity Test");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
