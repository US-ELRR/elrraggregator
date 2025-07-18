package com.deloitte.elrr.aggregator.consumer;

import static org.assertj.core.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.deloitte.elrr.aggregator.util.TestFileUtil;
import com.deloitte.elrr.entity.Email;
import com.deloitte.elrr.entity.Identity;
import com.deloitte.elrr.entity.Person;
import com.deloitte.elrr.jpa.svc.EmailSvc;
import com.deloitte.elrr.jpa.svc.IdentitySvc;
import com.deloitte.elrr.jpa.svc.PersonSvc;
import com.yetanalytics.xapi.model.Statement;
import com.yetanalytics.xapi.util.Mapper;

import lombok.extern.slf4j.Slf4j;

@ExtendWith(MockitoExtension.class)
@Slf4j
class ProcessPersonTest {

    @Mock
    private EmailSvc emailSvc;

    @Mock
    private IdentitySvc identitySvc;

    @Mock
    private PersonSvc personSvc;

    @InjectMocks
    private ProcessPerson processPerson;

    @Test
    void test() {

        try {

            File testFile = TestFileUtil.getJsonTestFile("completed.json");

            Statement stmt = Mapper.getMapper().readValue(testFile,
                    Statement.class);
            assertNotNull(stmt);

            Person person = processPerson.processPerson(stmt);
            assertNotNull(person);
            assertEquals(person.getName(), "test");

            Set<Email> emails = person.getEmailAddresses();
            assertNotNull(emails);
            Email email = emails.stream().findFirst().orElse(null);
            assertEquals(email.getEmailAddressType(), "primary");

            Set<Identity> identities = person.getIdentities();
            assertNotNull(identities);
            Identity identity = identities.stream().findFirst().orElse(null);
            assertEquals(identity.getMbox(), "mailto:test@gmail.com");

        } catch (IOException e) {
            fail("Should not have thrown any exception");
        }
    }

    @Test
    void testHomepage() {

        try {

            File testFile = TestFileUtil.getJsonTestFile(
                    "completed_homepage.json");

            Statement stmt = Mapper.getMapper().readValue(testFile,
                    Statement.class);
            assertNotNull(stmt);

            Person person = processPerson.processPerson(stmt);
            assertNotNull(person);
            assertEquals(person.getName(), "Terry Bradshaw");

            Set<Identity> identities = person.getIdentities();
            assertNotNull(identities);
            Identity identity = identities.stream().findFirst().orElse(null);
            assertEquals(identity.getHomePage(), "http://example44.org");
            assertEquals(identity.getName(), "account_name");

        } catch (IOException e) {
            fail("Should not have thrown any exception");
        }
    }

    @Test
    void testSha() {

        try {

            File testFile = TestFileUtil.getJsonTestFile(
                    "completed_mboxsha.json");

            Statement stmt = Mapper.getMapper().readValue(testFile,
                    Statement.class);
            assertNotNull(stmt);

            Person person = processPerson.processPerson(stmt);
            assertNotNull(person);
            assertEquals(person.getName(), "Test Sha");

            Set<Identity> identities = person.getIdentities();
            assertNotNull(identities);
            Identity identity = identities.stream().findFirst().orElse(null);
            assertEquals(identity.getMboxSha1Sum(), "i-am-a-sha");

        } catch (IOException e) {
            fail("Should not have thrown any exception");
        }
    }

    @Test
    @SuppressWarnings("checkstyle:linelength")
    void testOpenId() {

        try {

            File testFile = TestFileUtil.getJsonTestFile(
                    "completed_openid.json");

            Statement stmt = Mapper.getMapper().readValue(testFile,
                    Statement.class);
            assertNotNull(stmt);

            Person person = processPerson.processPerson(stmt);
            assertNotNull(person);
            assertEquals(person.getName(), "Test OpenId");

            Set<Identity> identities = person.getIdentities();
            assertNotNull(identities);
            Identity identity = identities.stream().findFirst().orElse(null);
            assertEquals(identity.getOpenid(),
                    "http://test.openid.example.org/");

        } catch (IOException e) {
            fail("Should not have thrown any exception");
        }
    }

}
