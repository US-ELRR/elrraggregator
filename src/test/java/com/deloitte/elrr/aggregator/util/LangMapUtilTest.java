package com.deloitte.elrr.aggregator.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.deloitte.elrr.aggregator.utils.LangMapUtil;
import com.yetanalytics.xapi.model.Activity;
import com.yetanalytics.xapi.model.Statement;
import com.yetanalytics.xapi.util.Mapper;

import lombok.extern.slf4j.Slf4j;

@ExtendWith(MockitoExtension.class)
@Slf4j
class LangMapUtilTest {

    @Test
    void testEnglish() {

        String activityName = "";
        String activityDescription = "";

        try {

            LangMapUtil langMapUtil = new LangMapUtil();

            File testFile = TestFileUtil.getJsonTestFile("completed.json");

            Statement stmt = Mapper.getMapper().readValue(testFile,
                    Statement.class);
            assertNotNull(stmt);

            Activity activity = (Activity) stmt.getObject();
            assertNotNull(activity);

            activityName = langMapUtil.getLangMapValue(activity.getDefinition()
                    .getName());
            activityDescription = langMapUtil.getLangMapValue(activity
                    .getDefinition().getDescription());
            assertEquals(activityName, "Activity 1");
            assertEquals(activityDescription, "Example Activity Test");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testFrench() {

        String activityName = "";
        String activityDescription = "";

        try {

            LangMapUtil langMapUtil = new LangMapUtil();

            File testFile = TestFileUtil.getJsonTestFile("completed_fr.json");

            Statement stmt = Mapper.getMapper().readValue(testFile,
                    Statement.class);
            assertNotNull(stmt);

            Activity activity = (Activity) stmt.getObject();
            assertNotNull(activity);

            activityName = langMapUtil.getLangMapValue(activity.getDefinition()
                    .getName());
            activityDescription = langMapUtil.getLangMapValue(activity
                    .getDefinition().getDescription());
            assertEquals(activityName, "Activité 1");
            assertEquals(activityDescription, "Exemple de test d'activité");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testSpanish() {

        String activityName = "";
        String activityDescription = "";

        try {

            LangMapUtil langMapUtil = new LangMapUtil();

            File testFile = TestFileUtil.getJsonTestFile("completed_es.json");

            Statement stmt = Mapper.getMapper().readValue(testFile,
                    Statement.class);
            assertNotNull(stmt);

            Activity activity = (Activity) stmt.getObject();
            assertNotNull(activity);

            activityName = langMapUtil.getLangMapValue(activity.getDefinition()
                    .getName());
            activityDescription = langMapUtil.getLangMapValue(activity
                    .getDefinition().getDescription());
            assertEquals(activityName, "actividad ejemplar 1");
            assertEquals(activityDescription, "Ejemplo de actividad");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testMulti() {

        String activityName = "";
        String activityDescription = "";

        try {

            LangMapUtil langMapUtil = new LangMapUtil();

            File testFile = TestFileUtil.getJsonTestFile(
                    "completed_multiple.json");

            Statement stmt = Mapper.getMapper().readValue(testFile,
                    Statement.class);
            assertNotNull(stmt);

            Activity activity = (Activity) stmt.getObject();
            assertNotNull(activity);

            activityName = langMapUtil.getLangMapValue(activity.getDefinition()
                    .getName());
            activityDescription = langMapUtil.getLangMapValue(activity
                    .getDefinition().getDescription());
            assertEquals(activityName, "Learning Module");
            assertEquals(activityDescription, "A module to learn about xAPI.");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
