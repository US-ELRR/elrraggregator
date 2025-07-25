package com.deloitte.elrr.aggregator.rules;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.deloitte.elrr.aggregator.utils.CompetencyUtil;
import com.deloitte.elrr.aggregator.utils.ExtensionsUtil;
import com.deloitte.elrr.elrraggregator.exception.AggregatorException;
import com.deloitte.elrr.entity.Competency;
import com.deloitte.elrr.entity.Person;
import com.yetanalytics.xapi.model.Activity;
import com.yetanalytics.xapi.model.Statement;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ProcessCompetency implements Rule {

    @Autowired
    private CompetencyUtil competencyUtil;

    @Autowired
    private ExtensionsUtil extensionsUtil;

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

        String objType = null;

        Activity obj = (Activity) statement.getObject();

        if (obj.getDefinition().getType() != null) {
            objType = obj.getDefinition().getType().toString();
        }

        // Is Verb Id = achieved and object type != credential
        return (statement.getVerb().getId().toString().equalsIgnoreCase(
                VerbIdConstants.ACHIEVED_VERB_ID.toString())
                && !ObjectTypeConstants.CREDENTIAL.equalsIgnoreCase(objType));
    }

    /**
     * @param person
     * @param statement
     * @return person
     * @throws AggregatorException
     */
    @Override
    @Transactional
    public Person processRule(final Person person, final Statement statement)
            throws AggregatorException {

        log.info("Process competency.");

        // Get start date
        // Convert from ZonedDateTime to LocalDate
        LocalDateTime startDate = statement.getTimestamp().toLocalDateTime();

        // Get Activity
        Activity activity = (Activity) statement.getObject();

        // Get expires
        LocalDateTime expires = (LocalDateTime) extensionsUtil.getExtensions(
                statement.getContext(), "LocalDateTime");

        // Process Competency
        Competency competency = competencyUtil.processCompetency(activity,
                startDate, expires);

        // Process PersonalCompetency
        competencyUtil.processPersonalCompetency(person, competency, expires);

        return person;
    }

}
