package com.deloitte.elrr.aggregator.rules;

import static org.assertj.core.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.deloitte.elrr.aggregator.consumer.ProcessPerson;
import com.deloitte.elrr.aggregator.util.TestFileUtil;
import com.deloitte.elrr.aggregator.utils.ExtensionsUtil;
import com.deloitte.elrr.aggregator.utils.LangMapUtil;
import com.deloitte.elrr.aggregator.utils.LearningResourceUtil;
import com.deloitte.elrr.elrraggregator.exception.AggregatorException;
import com.deloitte.elrr.entity.Goal;
import com.deloitte.elrr.entity.Person;
import com.deloitte.elrr.exception.RuntimeServiceException;
import com.deloitte.elrr.jpa.svc.CredentialSvc;
import com.deloitte.elrr.jpa.svc.GoalSvc;
import com.deloitte.elrr.jpa.svc.PersonSvc;
import com.deloitte.elrr.jpa.svc.PersonalCredentialSvc;
import com.yetanalytics.xapi.model.Activity;
import com.yetanalytics.xapi.model.Statement;
import com.yetanalytics.xapi.util.Mapper;

import lombok.extern.slf4j.Slf4j;

@ExtendWith(MockitoExtension.class)
@Slf4j
class ProcessWasAssignedTest {

    @Mock
    private LangMapUtil langMapUtil;

    @Mock
    private PersonSvc personService;

    @Mock
    private CredentialSvc credentialService;

    @Mock
    private ProcessPerson processPerson;

    @Mock
    private LearningResourceUtil learningResourceUtil;

    @Mock
    private ProcessCredential processCredential;

    @Mock
    private ProcessCompetency processCompetency;

    @Mock
    private PersonalCredentialSvc personalCredentialService;

    @Mock
    private ExtensionsUtil extensionsUtil;

    @Mock
    private GoalSvc goalService;

    @Mock
    private Person personMock;

    @InjectMocks
    private ProcessWasAssigned processWasAssigned;

    @Test
    void testAssignedCompetency() {

        try {

            File testFile = TestFileUtil.getJsonTestFile(
                    "was_assigned_by_learner_credential.json");

            Statement stmt = Mapper.getMapper().readValue(testFile,
                    Statement.class);
            assertNotNull(stmt);

            LocalDateTime startDate = stmt.getTimestamp().toLocalDateTime();

            // Get Activity
            Activity activity = (Activity) stmt.getObject();

            Mockito.doReturn("Competency AA").when(langMapUtil).getLangMapValue(
                    any());

            Person assignedPerson = new Person();
            assignedPerson.setId(UUID.randomUUID());
            assignedPerson.setName("Bill Curry");

            Goal goal = new Goal();
            goal.setId(UUID.randomUUID());
            goal.setName("Bill Curry Professional Development Goal 1");
            goal.setPerson(assignedPerson);

            boolean fireRule = processWasAssigned.fireRule(stmt);
            assertTrue(fireRule);

            Person personResult = processWasAssigned.processRule(assignedPerson,
                    stmt);
            assertNotNull(personResult);

            goal = processWasAssigned.updateGoal(goal, activity, null, null);
            assertNotNull(goal);

        } catch (AggregatorException | IOException | ClassCastException
                | NullPointerException | RuntimeServiceException
                | URISyntaxException e) {
            fail("Should not have thrown any exception");
        }
    }

    @Test
    void testWasAssignedCredential() {

        try {

            File testFile = TestFileUtil.getJsonTestFile(
                    "was_assigned_by_learner_credential.json");

            Statement stmt = Mapper.getMapper().readValue(testFile,
                    Statement.class);
            assertNotNull(stmt);

            Activity activity = (Activity) stmt.getObject();

            Mockito.doReturn("Credentail A").when(langMapUtil).getLangMapValue(
                    any());

            Person assignedPerson = new Person();
            assignedPerson.setId(UUID.randomUUID());
            assignedPerson.setName("Study O'Learner");

            Goal goal = new Goal();
            goal.setId(UUID.randomUUID());
            goal.setName("Study O'Learner Professional Development Goal 1");
            goal.setPerson(assignedPerson);

            boolean fireRule = processWasAssigned.fireRule(stmt);
            assertTrue(fireRule);

            Person personResult = processWasAssigned.processRule(assignedPerson,
                    stmt);
            assertNotNull(personResult);

            goal = processWasAssigned.updateGoal(goal, activity, null, null);
            assertNotNull(goal);

        } catch (AggregatorException | IOException | ClassCastException
                | NullPointerException | RuntimeServiceException
                | URISyntaxException e) {
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

            boolean fireRule = processWasAssigned.fireRule(stmt);
            assertFalse(fireRule);

        } catch (IOException e) {
            fail("Should not have thrown any exception");
        }
    }

}
