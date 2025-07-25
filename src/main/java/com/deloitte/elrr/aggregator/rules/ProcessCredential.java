package com.deloitte.elrr.aggregator.rules;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.deloitte.elrr.aggregator.utils.CredentialUtil;
import com.deloitte.elrr.aggregator.utils.ExtensionsUtil;
import com.deloitte.elrr.elrraggregator.exception.AggregatorException;
import com.deloitte.elrr.entity.Credential;
import com.deloitte.elrr.entity.Person;
import com.yetanalytics.xapi.model.Activity;
import com.yetanalytics.xapi.model.Statement;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ProcessCredential implements Rule {

    @Autowired
    private CredentialUtil credentialUtil;

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

        // Is Verb Id = achieved and object type = competency
        return (statement.getVerb().getId().toString().equalsIgnoreCase(
                VerbIdConstants.ACHIEVED_VERB_ID.toString()) && objType
                        .equalsIgnoreCase(ObjectTypeConstants.CREDENTIAL));

    }

    /**
     * @param person
     * @param statement
     * @throws AggregatorException
     */
    @Override
    @Transactional
    public Person processRule(final Person person, final Statement statement)
            throws AggregatorException {

        log.info("Process credential.");

        // Get start date
        // Convert from ZonedDateTime to LocalDateTime
        LocalDateTime startDate = statement.getTimestamp().toLocalDateTime();

        // Get Activity
        Activity activity = (Activity) statement.getObject();

        // Get expires
        LocalDateTime expires = (LocalDateTime) extensionsUtil.getExtensions(
                statement.getContext(), "LocalDateTime");

        // Process Credential
        Credential credential = credentialUtil.processCredential(activity,
                startDate, expires);

        // Process PersonalCredential
        credentialUtil.processPersonalCredential(person, credential, expires);

        return person;
    }

}
