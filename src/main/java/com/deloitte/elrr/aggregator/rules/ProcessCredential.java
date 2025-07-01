package com.deloitte.elrr.aggregator.rules;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.deloitte.elrr.aggregator.utils.LangMapUtil;
import com.deloitte.elrr.elrraggregator.exception.AggregatorException;
import com.deloitte.elrr.entity.Credential;
import com.deloitte.elrr.entity.Person;
import com.deloitte.elrr.entity.PersonalCredential;
import com.deloitte.elrr.exception.RuntimeServiceException;
import com.deloitte.elrr.jpa.svc.CredentialSvc;
import com.deloitte.elrr.jpa.svc.PersonSvc;
import com.deloitte.elrr.jpa.svc.PersonalCredentialSvc;
import com.yetanalytics.xapi.model.Activity;
import com.yetanalytics.xapi.model.Context;
import com.yetanalytics.xapi.model.Extensions;
import com.yetanalytics.xapi.model.Statement;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ProcessCredential implements Rule {

    @Autowired
    private CredentialSvc credentialService;

    @Autowired
    private PersonalCredentialSvc personalCredentialService;

    @Autowired
    private LangMapUtil langMapUtil;

    @Autowired
    private PersonSvc personService;

    private static final String CREDENTIAL_MESSAGE = "Credential";

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
                VerbIdConstants.ACHIEVED_VERB_ID.toString())
                && objType
                        .equalsIgnoreCase(ObjectTypeConstants.CREDENTIAL));

    }

    /**
     * @param person
     * @param statement
     * @throws AggregatorException
     */
    @Override
    @Transactional
    public Person processRule(final Person person, final Statement statement) {

        Extensions extensions = null;

        try {

            log.info("Process credential.");

            // Get Activity
            Activity activity = (Activity) statement.getObject();

            // Get Extensions
            Context context = statement.getContext();

            if (context != null) {
                extensions = context.getExtensions();
            }

            // Process Credential
            Credential credential = processCredential(activity);

            // Process PersonalCredential
            processPersonalCredential(person, credential,
                    extensions);

        } catch (AggregatorException e) {
            throw e;
        }

        return person;
    }

    /**
     * @param activity
     * @return credential
     * @throws AggregatorException
     */
    private Credential processCredential(final Activity activity)
            throws AggregatorException {

        Credential credential = null;

        // Get credential
        credential = credentialService.findByIdentifier(activity.getId()
                .toString());

        // If credential doesn't exist
        if (credential == null) {

            credential = createCredential(activity);

        } else {

            log.info(CREDENTIAL_MESSAGE + " " + activity.getId() + " exists.");
            credential = updateCredential(credential, activity);
        }

        return credential;
    }

    /**
     * @param activity
     * @return credential
     * @throws AggregatorException
     */
    private Credential createCredential(final Activity activity) {

        log.info("Creating new credential.");

        Credential credential = null;
        String activityName = "";
        String activityDescription = "";

        try {

            activityName = langMapUtil.getLangMapValue(activity.getDefinition()
                    .getName());
            activityDescription = langMapUtil.getLangMapValue(activity
                    .getDefinition().getDescription());

            credential = new Credential();
            credential.setIdentifier(activity.getId().toString());
            credential.setFrameworkTitle(activityName);
            credential.setFrameworkDescription(activityDescription);
            credentialService.save(credential);
            log.info(CREDENTIAL_MESSAGE + " " + activity.getId() + " created.");

        } catch (AggregatorException e) {
            throw e;
        }

        return credential;
    }

    /**
     * @param credential
     * @param activity
     * @return credential
     * @throws AggregatorException
     */
    public Credential updateCredential(Credential credential,
            final Activity activity) {

        log.info("Updating credential.");

        String activityName = "";
        String activityDescription = "";
        try {

            activityName = langMapUtil.getLangMapValue(activity.getDefinition()
                    .getName());
            activityDescription = langMapUtil.getLangMapValue(activity
                    .getDefinition().getDescription());

            credential.setFrameworkTitle(activityName);
            credential.setFrameworkDescription(activityDescription);
            credentialService.update(credential);
            log.info(CREDENTIAL_MESSAGE + " " + activity.getId() + " updated.");

        } catch (AggregatorException e) {
            throw e;
        }

        return credential;
    }

    /**
     * @param person
     * @param credential
     * @param extensions
     * @return PersonalCredential
     */
    private PersonalCredential processPersonalCredential(
            Person person, final Credential credential,
            Extensions extensions) {

        LocalDateTime expires = null;
        PersonalCredential personalCredential = null;

        try {

            // Get PersonalCredential
            personalCredential = personalCredentialService
                    .findByPersonIdAndCredentialId(person.getId(), credential
                            .getId());

            if (extensions != null) {

                String strExpires = (String) extensions.get(
                        ExtensionsConstants.CONTEXT_EXTENSIONS);

                if (strExpires != null) {
                    expires = LocalDateTime.parse(strExpires,
                            DateTimeFormatter.ISO_DATE_TIME);
                }

            }

            // If PersonalCredential doesn't exist
            if (personalCredential == null) {

                personalCredential = createPersonalCredential(person,
                        credential, expires);

                if (person.getCredentials() == null) {
                    person.setCredentials(new HashSet<PersonalCredential>());
                }

                person.getCredentials().add(personalCredential);
                personService.save(person);

            } else {

                personalCredential = updatePersonalCredential(
                        personalCredential, person, expires);
            }

        } catch (DateTimeParseException e) {
            log.error("Error invalid expires date", e);
            throw new AggregatorException("Error invalid expires date.", e);
        }

        return personalCredential;
    }

    /**
     * @param person
     * @param credential
     * @param expires
     * @return personalCredential
     */
    private PersonalCredential createPersonalCredential(final Person person,
            final Credential credential, final LocalDateTime expires) {

        log.info("Creating new personal credential record.");
        PersonalCredential personalCredential = new PersonalCredential();

        personalCredential.setPerson(person);
        personalCredential.setCredential(credential);
        personalCredential.setHasRecord(true);

        if (expires != null) {
            personalCredential.setExpires(expires);
        }

        personalCredentialService.save(personalCredential);

        log.info("Personal Credential for " + person.getName() + " - "
                + personalCredential.getCredential().getFrameworkTitle()
                + " created.");

        return personalCredential;
    }

    /**
     * @param personalCredential
     * @param person
     * @param expires
     * @return PersonalCredential
     * @throws RuntimeServiceException
     */
    public PersonalCredential updatePersonalCredential(
            PersonalCredential personalCredential, final Person person,
            final LocalDateTime expires) {

        try {

            if (expires != null) {

                personalCredential.setExpires(expires);
                personalCredentialService.update(personalCredential);

                log.info("Personal Credential for " + person.getName() + " - "
                        + personalCredential.getCredential().getFrameworkTitle()
                        + " updated.");

            }

        } catch (RuntimeServiceException e) {
            throw e;
        }

        return personalCredential;
    }
}
