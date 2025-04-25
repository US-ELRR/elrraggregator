package com.deloitte.elrr.aggregator.rules;

import java.util.HashSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.deloitte.elrr.aggregator.utils.LearningRecordUtil;
import com.deloitte.elrr.aggregator.utils.LearningResourceUtil;
import com.deloitte.elrr.elrraggregator.exception.AggregatorException;
import com.deloitte.elrr.entity.LearningRecord;
import com.deloitte.elrr.entity.LearningResource;
import com.deloitte.elrr.entity.Person;
import com.deloitte.elrr.exception.RuntimeServiceException;
import com.yetanalytics.xapi.model.Activity;
import com.yetanalytics.xapi.model.Statement;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ProcessCompleted implements Rule {

	@Autowired
	LearningResourceUtil learningResourceUtil;

	@Autowired
	LearningRecordUtil learningRecordUtil;

	@Override
	public boolean fireRule(final Statement statement) {

		// Is Verb Id = completed and object = activity
		return (statement.getVerb().getId().equalsIgnoreCase(VerbIdConstants.COMPLETED_VERB_ID)
				&& statement.getObject() instanceof Activity);

	}

	@Override
	@Transactional
	public Person processRule(final Person person, final Statement statement) {

		LearningResource learningResource = null;
		LearningRecord learningRecord = null;

		try {

			log.info("Process activity completed");

			// Get Activity
			Activity activity = (Activity) statement.getObject();

			// Process LearningResource
			learningResource = learningResourceUtil.processLearningResource(activity);

			// Process LearningRecord
			if (learningResource != null) {

				// Process LearningRecord
				learningRecord = learningRecordUtil.processLearningRecord(activity, person, statement.getVerb(),
						statement.getResult(), learningResource);

				if (person.getLearningRecords() == null) {
					person.setLearningRecords(new HashSet<LearningRecord>());
				}

				person.getLearningRecords().add(learningRecord);
			}

		} catch (AggregatorException | ClassCastException | NullPointerException | RuntimeServiceException e) {
			throw e;
		}

		return person;
	}
}
