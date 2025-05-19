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
public class ProcessRegistered implements Rule {

    @Autowired
    private LearningResourceUtil learningResourceUtil;

    @Autowired
    private LearningRecordUtil learningRecordUtil;

    @Autowired
    private PersonSvc personService;

    /**
     * @param statement
     * @return boolean
     */
    @Override
    public boolean fireRule(final Statement statement) {

        // If not an activity
        if (!(statement.getObject() instanceof Activity)) {
            return false;
        }

        // Is Verb Id = registered
        return (statement.getVerb().getId().toString().equalsIgnoreCase(
                VerbIdConstants.REGISTERED_VERB_ID.toString()));

    }

    /**
     * @param person
     * @param statement
     * @throws AggregatorException
     */
    @Override
    @Transactional
    public Person processRule(final Person person, final Statement statement) {

        try {

            log.info("Process activity registered");

            // Get Activity
            Activity activity = (Activity) statement.getObject();

            // Process LearningResource
            LearningResource learningResource = learningResourceUtil
                    .processLearningResource(activity);

            // Process LearningRecord
            LearningRecord learningRecord = learningRecordUtil
                    .processLearningRecord(activity, person, statement
                            .getVerb(), statement.getResult(),
                            learningResource);

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
