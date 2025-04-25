package com.deloitte.elrr.aggregator.test.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import com.deloitte.elrr.aggregator.utils.LangMapUtil;
import com.deloitte.elrr.aggregator.utils.LearningRecordUtil;
import com.deloitte.elrr.entity.LearningRecord;
import com.deloitte.elrr.entity.LearningResource;
import com.deloitte.elrr.entity.Person;
import com.deloitte.elrr.entity.types.LearningStatus;
import com.deloitte.elrr.jpa.svc.LearningRecordSvc;
import com.yetanalytics.xapi.model.Activity;
import com.yetanalytics.xapi.model.Result;
import com.yetanalytics.xapi.model.Statement;
import com.yetanalytics.xapi.model.Verb;
import com.yetanalytics.xapi.util.Mapper;

import lombok.extern.slf4j.Slf4j;

@ExtendWith(MockitoExtension.class)
@Slf4j
class LearningRecordUtilTest {

	@Mock
	private LearningRecordSvc learningRecordSvc;

	@Spy
	private LangMapUtil langMapUtil;

	@InjectMocks
	private LearningRecordUtil learningRecordUtil;

	@Test
	void test() {

		try {

			File testFile = TestFileUtils.getJsonTestFile("completed.json");

			Statement stmt = Mapper.getMapper().readValue(testFile, Statement.class);
			assertNotNull(stmt);

			Activity activity = (Activity) stmt.getObject();
			assertNotNull(activity);

			Verb verb = stmt.getVerb();
			assertNotNull(verb);

			Result result = stmt.getResult();
			assertNotNull(result);

			Person person = new Person();
			person.setId(UUID.randomUUID());
			person.setName("Joe Williams");

			LearningResource learningResource = new LearningResource();
			learningResource.setId(UUID.randomUUID());
			learningResource.setTitle("title");
			learningResource.setDescription("description");

			LearningRecord learningRecord = learningRecordUtil.processLearningRecord(activity, person, verb, result,
					learningResource);
			assertNotNull(learningRecord);
			assertNotNull(learningRecord.getPerson());
			assertNotNull(learningRecord.getLearningResource());
			assertEquals(learningRecord.getRecordStatus(), LearningStatus.COMPLETED);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
