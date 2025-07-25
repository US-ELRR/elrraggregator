package com.deloitte.elrr.aggregator.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.HashSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.deloitte.elrr.elrraggregator.exception.AggregatorException;
import com.deloitte.elrr.entity.Credential;
import com.deloitte.elrr.entity.Person;
import com.deloitte.elrr.entity.PersonalCredential;
import com.deloitte.elrr.jpa.svc.CredentialSvc;
import com.deloitte.elrr.jpa.svc.PersonSvc;
import com.deloitte.elrr.jpa.svc.PersonalCredentialSvc;
import com.yetanalytics.xapi.model.Activity;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CredentialUtil {

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
     * @param activity
     * @param startDate
     * @param endDate
     * @return credential
     * @throws AggregatorException
     */
    public Credential processCredential(final Activity activity,
            final LocalDateTime startDate, final LocalDateTime endDate)
            throws AggregatorException {

        Credential credential = null;

        // Get credential
        credential = credentialService.findByIdentifier(activity.getId()
                .toString());

        // If credential doesn't exist
        if (credential == null) {

            credential = createCredential(activity, startDate, endDate);

        } else {

            log.info(CREDENTIAL_MESSAGE + " " + activity.getId() + " exists.");
            credential = updateCredential(credential, activity, endDate);
        }

        return credential;
    }

    /**
     * @param activity
     * @param startDate
     * @param endDate
     * @return credential
     * @throws AggregatorException
     */
    public Credential createCredential(final Activity activity,
            final LocalDateTime startDate, final LocalDateTime endDate) {

        log.info("Creating new credential.");

        Credential credential = null;
        String activityName = "";
        String activityDescription = "";

        activityName = langMapUtil.getLangMapValue(activity.getDefinition()
                .getName());
        activityDescription = langMapUtil.getLangMapValue(activity
                .getDefinition().getDescription());

        credential = new Credential();
        credential.setIdentifier(activity.getId().toString());
        credential.setFrameworkTitle(activityName);
        credential.setFrameworkDescription(activityDescription);
        credential.setValidStartDate(startDate);
        credential.setValidEndDate(endDate);
        credentialService.save(credential);
        log.info(CREDENTIAL_MESSAGE + " " + activity.getId() + " created.");

        return credential;
    }

    /**
     * @param credential
     * @param activity
     * @param endDate
     * @return credential
     * @throws AggregatorException
     */
    public Credential updateCredential(Credential credential,
            final Activity activity, final LocalDateTime endDate) {

        log.info("Updating credential.");

        String activityName = "";
        String activityDescription = "";

        activityName = langMapUtil.getLangMapValue(activity.getDefinition()
                .getName());
        activityDescription = langMapUtil.getLangMapValue(activity
                .getDefinition().getDescription());

        credential.setFrameworkTitle(activityName);
        credential.setFrameworkDescription(activityDescription);
        credential.setValidEndDate(endDate);
        credentialService.update(credential);
        log.info(CREDENTIAL_MESSAGE + " " + activity.getId() + " updated.");

        return credential;
    }

    /**
     * @param person
     * @param credential
     * @param expires
     * @return PersonalCredential
     */
    public PersonalCredential processPersonalCredential(Person person,
            final Credential credential, final LocalDateTime expires) {

        PersonalCredential personalCredential = null;

        try {

            // Get PersonalCredential
            personalCredential = personalCredentialService
                    .findByPersonIdAndCredentialId(person.getId(), credential
                            .getId());

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
    public PersonalCredential createPersonalCredential(final Person person,
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
     */
    public PersonalCredential updatePersonalCredential(
            PersonalCredential personalCredential, final Person person,
            final LocalDateTime expires) {

        if (expires != null) {

            personalCredential.setExpires(expires);
            personalCredentialService.update(personalCredential);

            log.info("Personal Credential for " + person.getName() + " - "
                    + personalCredential.getCredential().getFrameworkTitle()
                    + " updated.");

        }

        return personalCredential;
    }

}
