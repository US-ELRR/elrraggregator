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
import com.deloitte.elrr.jpa.svc.PersonSvc;
import com.yetanalytics.xapi.model.Activity;
import com.yetanalytics.xapi.model.Statement;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ProcessFailed implements Rule {

    @Autowired
    LearningResourceUtil learningResourceUtil;

    @Autowired
    LearningRecordUtil learningRecordUtil;

    @Autowired
    PersonSvc personService;

    /**
     * @param Statement
     * @return boolean
     */
    @Override
    public boolean fireRule(final Statement statement) {

        // If not an activity
        if (!(statement.getObject() instanceof Activity)) {
            return false;
        }

        // Is Verb Id = failed
        return (statement.getVerb().getId().toString().equalsIgnoreCase(VerbIdConstants.FAILED_VERB_ID.toString()));

    }

    /**
     * @param Person
     * @param Statement
     * @throws AggregatorException
     */
    @Override
    @Transactional
    public Person processRule(final Person person, final Statement statement) {

        try {

            log.info("Process activity failed");

            // Get Activity
            Activity activity = (Activity) statement.getObject();

            // Process LearningResource
            LearningResource learningResource = learningResourceUtil.processLearningResource(activity);

            // Process LearningRecord
            LearningRecord learningRecord = learningRecordUtil.processLearningRecord(activity, person,
                    statement.getVerb(), statement.getResult(), learningResource);

            if (person.getLearningRecords() == null) {
                person.setLearningRecords(new HashSet<LearningRecord>());
            }

            person.getLearningRecords().add(learningRecord);
            personService.save(person);

        } catch (AggregatorException e) {
            throw e;
        }

        return person;
    }
}
