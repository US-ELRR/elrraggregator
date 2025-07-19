package com.deloitte.elrr.aggregator.rules;

import static org.assertj.core.api.Assertions.fail;
import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import com.deloitte.elrr.aggregator.util.TestFileUtil;
import com.deloitte.elrr.aggregator.utils.ExtensionsUtil;
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
class ProcessAssignedTest {

    @Mock
    private PersonSvc personService;

    @Mock
    private LearningResourceUtil learningResourceUtil;

    @Mock
    private LearningRecordUtil learningRecordUtil;

    @Spy
    private ExtensionsUtil extensionsUtil;

    @InjectMocks
    private ProcessAssigned processAssigned;

    @Test
    void test() {

        try {

            File testFile = TestFileUtil.getJsonTestFile("assigned.json");

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
            email.setEmailAddress("mailto:po@gmail.com");

            Person person = new Person();
            person.setId(UUID.randomUUID());
            person.setName("Robert Po");
            person.setEmailAddresses(new HashSet<Email>());
            person.getEmailAddresses().add(email);

            UUID identityUUID = UUID.randomUUID();
            Identity identity = new Identity();
            identity.setId(identityUUID);
            identity.setMbox("mailto:po@gmail.com");

            LearningResource learningResource = new LearningResource();
            learningResource.setId(UUID.randomUUID());
            learningResource.setDescription("Example to");
            Mockito.doReturn(learningResource).when(learningResourceUtil)
                    .processLearningResource(activity);

            LearningRecord learningRecord = new LearningRecord();
            learningRecord.setId(UUID.randomUUID());
            learningRecord.setRecordStatus(LearningStatus.ATTEMPTED);
            learningRecord.setPerson(person);
            learningRecord.setLearningResource(learningResource);
            Map<URI, Object> map = new HashMap();
            URI uri1 = new URI(
                    "https://yetanalytics.com/profiles/prepositions/concepts/context-extensions/to");
            map.put(uri1, "assign to Phil.");
            learningRecord.setExtensions(map);
            Mockito.doReturn(learningRecord).when(learningRecordUtil)
                    .processLearningRecord(person, verb, result,
                            learningResource, map);

            boolean fireRule = processAssigned.fireRule(stmt);
            assertTrue(fireRule);

            Person personResult = processAssigned.processRule(person, stmt);
            assertEquals(personResult.getName(), "Robert Po");

            Set<LearningRecord> learningRecords = personResult
                    .getLearningRecords();
            assertNotNull(learningRecords);

            learningRecord = learningRecords.stream().findFirst().orElse(null);
            assertNotNull(learningRecord);
            assertNotNull(learningRecord.getPerson());
            assertNotNull(learningRecord.getLearningResource());
            assertEquals(learningRecord.getRecordStatus(),
                    LearningStatus.ATTEMPTED);
            assertEquals(learningRecord.getLearningResource().getDescription(),
                    "Example to");

        } catch (IOException | URISyntaxException e) {
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

            boolean fireRule = processAssigned.fireRule(stmt);
            assertFalse(fireRule);

        } catch (IOException e) {
            fail("Should not have thrown any exception");
        }

    }

}
