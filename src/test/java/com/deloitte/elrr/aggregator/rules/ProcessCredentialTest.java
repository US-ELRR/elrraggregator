package com.deloitte.elrr.aggregator.rules;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.deloitte.elrr.aggregator.util.TestFileUtils;
import com.deloitte.elrr.aggregator.utils.LangMapUtil;
import com.deloitte.elrr.entity.Credential;
import com.deloitte.elrr.entity.Email;
import com.deloitte.elrr.entity.Identity;
import com.deloitte.elrr.entity.Person;
import com.deloitte.elrr.entity.PersonalCredential;
import com.deloitte.elrr.jpa.svc.CredentialSvc;
import com.deloitte.elrr.jpa.svc.PersonSvc;
import com.deloitte.elrr.jpa.svc.PersonalCredentialSvc;
import com.yetanalytics.xapi.model.Statement;
import com.yetanalytics.xapi.util.Mapper;

import lombok.extern.slf4j.Slf4j;

@ExtendWith(MockitoExtension.class)
@Slf4j
class ProcessCredentialTest {

    @Mock
    private LangMapUtil langMapUtil;

    @Mock
    PersonSvc personService;

    @Mock
    private CredentialSvc credentialService;

    @Mock
    private PersonalCredentialSvc personalCredentialService;

    @InjectMocks
    ProcessCredential processCredential;

    @Test
    void test() {

        try {

            File testFile = TestFileUtils.getJsonTestFile("credential.json");

            Statement stmt = Mapper.getMapper().readValue(testFile, Statement.class);
            assertNotNull(stmt);

            Mockito.doReturn("Test Credential A").doReturn("Object representing Credential A level").when(langMapUtil)
                    .getLangMapValue(any());

            Email email = new Email();
            email.setId(UUID.randomUUID());
            email.setEmailAddressType("primary");
            email.setEmailAddress("mailto:testcredential@gmail.com");

            Person person = new Person();
            person.setId(UUID.randomUUID());
            person.setName("Test Credential");
            person.setEmailAddresses(new HashSet<Email>());
            person.getEmailAddresses().add(email);
            Mockito.doReturn(person).when(personService).save(person);

            UUID identityUUID = UUID.randomUUID();
            Identity identity = new Identity();
            identity.setId(identityUUID);
            identity.setMbox("mailto:testcredential@gmail.com");

            Credential credential = new Credential();
            credential.setId(UUID.randomUUID());
            credential.setIdentifier("http://example.edlm/credentials/credential-a");
            credential.setFrameworkTitle("Test Credential A");
            credential.setFrameworkDescription("Object representing Test Credential A level");
            Mockito.doReturn(credential).when(credentialService).save(any());

            PersonalCredential personalCredential = new PersonalCredential();
            personalCredential.setId(UUID.randomUUID());
            personalCredential.setHasRecord(true);

            LocalDateTime expires = LocalDateTime.parse("2025-12-05T15:30:00Z", DateTimeFormatter.ISO_DATE_TIME);
            personalCredential.setExpires(expires);

            personalCredential.setPerson(person);
            personalCredential.setCredential(credential);
            Mockito.doReturn(personalCredential).when(personalCredentialService).save(any());

            boolean fireRule = processCredential.fireRule(stmt);
            assertTrue(fireRule);

            Person personResult = processCredential.processRule(person, stmt);

            Set<PersonalCredential> personalCredentials = personResult.getCredentials();
            assertNotNull(personalCredentials);

            personalCredential = personalCredentials.stream().findFirst().orElse(null);
            assertNotNull(personalCredential);

            assertEquals(personalCredential.getCredential().getFrameworkTitle(), "Test Credential A");
            assertEquals(personalCredential.getCredential().getFrameworkDescription(),
                    "Object representing Credential A level");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
