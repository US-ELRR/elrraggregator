package com.deloitte.elrr.aggregator.rules;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.deloitte.elrr.aggregator.utils.ExtensionsUtil;
import com.deloitte.elrr.aggregator.utils.LearningRecordUtil;
import com.deloitte.elrr.aggregator.utils.LearningResourceUtil;
import com.deloitte.elrr.entity.LearningRecord;
import com.deloitte.elrr.entity.LearningResource;
import com.deloitte.elrr.entity.Person;
import com.deloitte.elrr.jpa.svc.PersonSvc;
import com.yetanalytics.xapi.model.Activity;
import com.yetanalytics.xapi.model.Context;
import com.yetanalytics.xapi.model.Statement;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ProcessAssigned implements Rule {

    @Autowired
    private LearningResourceUtil learningResourceUtil;

    @Autowired
    private LearningRecordUtil learningRecordUtil;

    @Autowired
    private ExtensionsUtil extensionsUtil;

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

        // Is Verb Id = assigned
        return (statement.getVerb().getId().toString().equalsIgnoreCase(
                VerbIdConstants.ASSIGNED_VERB_ID.toString()));
    }

    /**
     * @param person
     * @param statement
     * @return person
     * @throws URISyntaxException
     */
    @Override
    @Transactional
    public Person processRule(final Person person, final Statement statement)
            throws URISyntaxException {

        Map<URI, Object> extensionsMap = new HashMap<>();

        log.info("Process activity assigned");

        // Get Activity
        Activity activity = (Activity) statement.getObject();

        Context context = statement.getContext();

        // Get Extensions
        extensionsMap = extensionsUtil.getExtensions(context);

        // Process LearningResource
        LearningResource learningResource = learningResourceUtil
                .processLearningResource(activity);

        // Process LearningRecord
        LearningRecord learningRecord = learningRecordUtil
                .processLearningRecord(person, statement.getVerb(), statement
                        .getResult(), learningResource, extensionsMap);

        if (person.getLearningRecords() == null) {
            person.setLearningRecords(new HashSet<LearningRecord>());
        }

        person.getLearningRecords().add(learningRecord);
        personService.save(person);

        return person;
    }
}
