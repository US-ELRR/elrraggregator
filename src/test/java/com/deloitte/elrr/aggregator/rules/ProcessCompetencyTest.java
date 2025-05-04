package com.deloitte.elrr.aggregator.rules;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.deloitte.elrr.aggregator.rules.ProcessCompetency;
import com.deloitte.elrr.aggregator.util.TestFileUtils;
import com.deloitte.elrr.aggregator.utils.LangMapUtil;
import com.deloitte.elrr.entity.Competency;
import com.deloitte.elrr.entity.Email;
import com.deloitte.elrr.entity.Identity;
import com.deloitte.elrr.entity.Person;
import com.deloitte.elrr.entity.PersonalCompetency;
import com.deloitte.elrr.jpa.svc.CompetencySvc;
import com.deloitte.elrr.jpa.svc.PersonSvc;
import com.deloitte.elrr.jpa.svc.PersonalCompetencySvc;
import com.yetanalytics.xapi.model.Statement;
import com.yetanalytics.xapi.util.Mapper;

import lombok.extern.slf4j.Slf4j;

@ExtendWith(MockitoExtension.class)
@Slf4j
class ProcessCompetencyTest {

	@Mock
	private LangMapUtil langMapUtil;

	@Mock
	PersonSvc personService;

	@Mock
	CompetencySvc competencyService;

	@Mock
	PersonalCompetencySvc personalCompetencyService;

	@InjectMocks
	ProcessCompetency processCompetency;

	@Test
	void test() {

		try {

			File testFile = TestFileUtils.getJsonTestFile("competency.json");

			Statement stmt = Mapper.getMapper().readValue(testFile, Statement.class);
			assertNotNull(stmt);

			Mockito.doReturn("Competency A").doReturn("Object representing Competency A level").when(langMapUtil)
					.getLangMapValue(any());

			Email email = new Email();
			email.setId(UUID.randomUUID());
			email.setEmailAddressType("primary");
			email.setEmailAddress("mailto:testcompetency@gmail.com");

			Person person = new Person();
			person.setId(UUID.randomUUID());
			person.setName("Test Competency");
			person.setEmailAddresses(new HashSet<Email>());
			person.getEmailAddresses().add(email);
			Mockito.doReturn(person).when(personService).save(person);

			Identity identity = new Identity();
			identity.setId(UUID.randomUUID());
			identity.setMbox("mailto:testcompetency@gmail.com");

			Competency competency = new Competency();
			competency.setId(UUID.randomUUID());
			competency.setIdentifier("http://example.edlm/competencies/testcompetency-a");
			competency.setRecordStatus("SUCCESS");
			competency.setFrameworkTitle("Competency A");
			competency.setFrameworkDescription("Object representing Competency A level");
			Mockito.doReturn(competency).when(competencyService).save(any());

			PersonalCompetency personalCompetency = new PersonalCompetency();
			personalCompetency.setId(UUID.randomUUID());
			personalCompetency.setHasRecord(true);
			personalCompetency.setExpires(LocalDate.parse("2026-03-07"));
			personalCompetency.setPerson(person);
			personalCompetency.setCompetency(competency);
			Mockito.doReturn(personalCompetency).when(personalCompetencyService).save(any());

			boolean fireRule = processCompetency.fireRule(stmt);
			assertTrue(fireRule);

			Person personResult = processCompetency.processRule(person, stmt);

			Set<PersonalCompetency> personalCompetencies = personResult.getCompetencies();
			assertNotNull(personalCompetencies);

			PersonalCompetency personalCompetencyResult = personalCompetencies.stream().findFirst().orElse(null);
			assertNotNull(personalCompetencyResult);

			assertEquals(personalCompetencyResult.getCompetency().getFrameworkTitle(), "Competency A");
			assertEquals(personalCompetencyResult.getCompetency().getFrameworkDescription(),
					"Object representing Competency A level");

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
