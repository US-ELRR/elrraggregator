package com.deloitte.elrr.aggregator.rules;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.deloitte.elrr.aggregator.consumer.ProcessPerson;
import com.deloitte.elrr.aggregator.utils.ExtensionsUtil;
import com.deloitte.elrr.aggregator.utils.LearningRecordUtil;
import com.deloitte.elrr.aggregator.utils.LearningResourceUtil;
import com.deloitte.elrr.elrraggregator.exception.AggregatorException;
import com.deloitte.elrr.entity.Person;
import com.deloitte.elrr.jpa.svc.PersonSvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yetanalytics.xapi.model.AbstractActor;
import com.yetanalytics.xapi.model.Activity;
import com.yetanalytics.xapi.model.Statement;
import com.yetanalytics.xapi.util.Mapper;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ProcessAssigned implements Rule {

    @Autowired
    private ProcessPerson processPerson;

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
     * @return
     * @throws AggregatorException
     */
    @Override
    @Transactional
    public Person processRule(Person person, final Statement statement)
            throws AggregatorException {

        log.info("Process assigned");

        Map<URI, Object> extensionsMap = new HashMap<>();
        String requestStatement = null;
        Statement responseStatement = null;

        try {

            ObjectMapper mapper = Mapper.getMapper();

            // Get extensions
            extensionsMap = (Map<URI, Object>) extensionsUtil.getExtensions(
                    statement.getContext(), "Map");

            for (Map.Entry<URI, Object> entry : extensionsMap.entrySet()) {

                String json = mapper.writeValueAsString(entry.getValue());
                AbstractActor actor = mapper.readValue(json,
                        AbstractActor.class);

                log.info("Name " + actor.getName());

            }

        } catch (IOException e) {
            throw new AggregatorException("Error ", e);
        }

        return null;
    }
}
