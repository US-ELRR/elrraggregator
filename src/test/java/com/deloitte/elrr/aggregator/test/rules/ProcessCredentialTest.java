package com.deloitte.elrr.aggregator.test.rules;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.deloitte.elrr.aggregator.rules.ProcessCredential;
import com.deloitte.elrr.aggregator.test.util.TestFileUtils;
import com.deloitte.elrr.aggregator.utils.LangMapUtil;
import com.deloitte.elrr.entity.Person;
import com.deloitte.elrr.jpa.svc.CredentialSvc;
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

			Person person = new Person();
			person.setId(UUID.randomUUID());
			person.setName("Joe Williams");

			boolean fireRule = processCredential.fireRule(stmt);
			assertTrue(fireRule);

			if (fireRule) {
				Person personResult = processCredential.processRule(person, stmt);
				Set credentials = new HashSet();
				credentials = personResult.getCredentials();
				assertNotNull(credentials);
				assertNotNull(personResult.getCredentials());
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
