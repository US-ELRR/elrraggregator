package com.deloitte.elrr.aggregator.consumer;

import static org.assertj.core.api.Assertions.fail;
import static org.junit.Assert.assertNotNull;

import java.net.URISyntaxException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import com.deloitte.elrr.aggregator.rules.Rule;
import com.deloitte.elrr.elrraggregator.exception.AggregatorException;

import lombok.extern.slf4j.Slf4j;

@ExtendWith(MockitoExtension.class)
@Slf4j
class ELRRMessageListenerTest {

    @Mock
    private Rule processCompleted;

    @Mock
    private Rule processCompetency;

    @Mock
    private Rule processCredential;

    @Mock
    private ProcessPerson processPerson;

    @Mock
    private Rule processPassed;

    @Mock
    private Rule processFailed;

    @Mock
    private Rule processInitialized;

    @Mock
    private Rule processSatisfied;

    @Mock
    private Rule processRegistered;

    @Mock
    private Rule processScheduled;

    @Mock
    private Rule processAssigned;

    @Mock
    private Rule processWasAssigned;

    @Mock
    private Rule processRemoved;

    @Mock
    private KafkaTemplate<?, String> kafkaTemplate;

    @InjectMocks
    private ELRRMessageListener elrrMessageListener;

    @Test
    @SuppressWarnings("checkstyle:linelength")
    void test() {

        try {

            String content = "{\"statement\":{\"id\":\"d9f1328b-bcc2-4b9c-b954-03cb88a240c8\",\"actor\":{\"objectType\":\"Agent\",\"name\":\"Sophia Lewis\",\"mbox\":\"mailto:sophia.lewis@us.navy.mil\"},\"verb\":{\"id\":\"https://adlnet.gov/expapi/verbs/achieved\",\"display\":{\"en-us\":\"Achieved\"}},\"object\":{\"id\":\"https://w3id.org/xapi/credential/GIAC%20Security%20Essentials%20Certification%20%28GSEC%29\",\"definition\":{\"name\":{\"en-us\":\"GIAC Security Essentials Certification (GSEC)\"},\"type\":\"https://yetanalytics.com/deloitte-edlm/demo-profile/certificate\"}},\"authority\":{\"objectType\":\"Agent\",\"account\":{\"homePage\":\"http://example.org\",\"name\":\"0194609d-5948-87ff-b11a-0eec04f384c2\"}},\"timestamp\":\"2024-09-20T21:37:23.835Z\",\"stored\":\"2025-01-13T17:34:37.277Z\",\"version\":\"1.0.0\"}}";
            elrrMessageListener.listen(content);

        } catch (AggregatorException | URISyntaxException e) {
            fail("Should not have thrown any exception");
        }
    }

    @Test
    @SuppressWarnings("checkstyle:linelength")
    void testInvalid() {

        try {

            String content = "{\"statement\":{\"id\":\"הושלם28b-bcc2-4b9c-b954-03cb88a240c8\",\"actor\":{\"objectType\":\"Agent\",\"name\":\"Sophia Lewis\",\"mbox\":\"mailto:sophia.lewis@us.navy.mil\"},\"verb\":{\"id\":\"https://adlnet.gov/expapi/verbs/achieved\",\"display\":{\"en-us\":\"Achieved\"}},\"object\":{\"id\":\"https://w3id.org/xapi/credential/GIAC%20Security%20Essentials%20Certification%20%28GSEC%29\",\"definition\":{\"name\":{\"en-us\":\"GIAC Security Essentials Certification (GSEC)\"},\"type\":\"https://yetanalytics.com/deloitte-edlm/demo-profile/certificate\"}},\"authority\":{\"objectType\":\"Agent\",\"account\":{\"homePage\":\"http://example.org\",\"name\":\"0194609d-5948-87ff-b11a-0eec04f384c2\"}},\"timestamp\":\"2024-09-20T21:37:23.835Z\",\"stored\":\"2025-01-13T17:34:37.277Z\",\"version\":\"1.0.0\"}}";
            elrrMessageListener.listen(content);

        } catch (AggregatorException | URISyntaxException e) {
            assertNotNull(e);
        }
    }

}
