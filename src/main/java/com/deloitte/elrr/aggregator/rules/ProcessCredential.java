package com.deloitte.elrr.aggregator.rules;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Map;

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
    PersonSvc personService;

    @Override
    public boolean fireRule(final Statement statement) {

        // Is Verb Id = achieved and object = activity
        Activity obj = (Activity) statement.getObject();
        String objType = obj.getDefinition().getType();

        // Is Verb Id = achieved, object = activity and object type = competency
        return (statement.getVerb().getId().equalsIgnoreCase(VerbIdConstants.ACHIEVED_VERB_ID)
                && statement.getObject() instanceof Activity && objType != null
                && objType.equalsIgnoreCase(ObjectTypeConstants.CREDENTIAL));

    }

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
            PersonalCredential personalCredential = processPersonalCredential(activity, person, credential, extensions);

        } catch (AggregatorException | ClassCastException | NullPointerException | RuntimeServiceException e) {
            throw e;
        }

        return person;
    }

    /**
     * @param statement
     * @return credential
     */
    private Credential processCredential(final Activity activity) {

        Credential credential = null;
        PersonalCredential personalCredential = null;

        try {

            // Get credential
            credential = credentialService.findByIdentifier(activity.getId());

            // If credential doesn't exist
            if (credential == null) {

                credential = createCredential(activity);

            } else {

                log.info("Credential " + activity.getId() + " exists.");
                credential = updateCredential(credential, activity);
            }

        } catch (AggregatorException | ClassCastException | NullPointerException e) {

            log.error("Error processing competency - " + e.getMessage());
            e.printStackTrace();
            throw e;
        }

        return credential;
    }

    /**
     * @param activity
     * @return credential
     */
    private Credential createCredential(final Activity activity) {

        log.info("Creating new credential.");

        Credential credential = null;
        String activityName = "";
        String activityDescription = "";

        try {

            activityName = langMapUtil.getLangMapValue(activity.getDefinition().getName());
            activityDescription = langMapUtil.getLangMapValue(activity.getDefinition().getDescription());

            credential = new Credential();
            credential.setIdentifier(activity.getId());
            credential.setRecordStatus(StatusConstants.COMPLETED);
            credential.setFrameworkTitle(activityName);
            credential.setFrameworkDescription(activityDescription);
            credentialService.save(credential);
            String[] strings = { "Credential", activity.getId(), "created." };
            log.info(String.join(" ", strings));

        } catch (AggregatorException | ClassCastException | NullPointerException e) {
            log.error(e.getMessage());
            e.printStackTrace();
            throw e;
        }

        return credential;
    }

    /**
     * @param activity
     * @return credential
     */
    private Credential updateCredential(Credential credential, final Activity activity) {

        log.info("Updating credential.");

        String activityName = "";
        String activityDescription = "";
        try {

            activityName = langMapUtil.getLangMapValue(activity.getDefinition().getName());
            activityDescription = langMapUtil.getLangMapValue(activity.getDefinition().getDescription());

            credential.setRecordStatus(StatusConstants.COMPLETED);
            credential.setFrameworkTitle(activityName);
            credential.setFrameworkDescription(activityDescription);
            credentialService.update(credential);
            String[] strings = { "Credential", activity.getId(), "updated." };
            log.info(String.join(" ", strings));

        } catch (AggregatorException | ClassCastException | NullPointerException e) {
            log.error(e.getMessage());
            e.printStackTrace();
            throw e;
        }

        return credential;
    }

    /**
     * @param Activity
     * @param Person
     * @param Credential
     * @param EXtensions
     * @return PersonalCredential
     */
    private PersonalCredential processPersonalCredential(final Activity activity, Person person,
            final Credential credential, Extensions extensions) {

        LocalDate expires = null;

        // Get PersonalCredential
        PersonalCredential personalCredential = personalCredentialService.findByPersonIdAndCredentialId(person.getId(),
                credential.getId());

        if (extensions != null) {

            Map extensionMap = extensions.getMap();
            String strExpires = (String) extensionMap.get(ExtensionsConstants.CONTEXT_EXTENSIONS);

            if (strExpires != null) {
                expires = LocalDate.parse(strExpires);
            }

        }

        // If PersonalCredential doesn't exist
        if (personalCredential == null) {

            personalCredential = createPersonalCredential(person, credential, expires);

            if (person.getCredentials() == null) {
                person.setCredentials(new HashSet<PersonalCredential>());
            }

            person.getCredentials().add(personalCredential);
            personService.save(person);

        } else {

            personalCredential = updatePersonalCredential(personalCredential, person, credential, expires);
        }

        return personalCredential;
    }

    /**
     * @param Person
     * @param Credential
     * @param expires
     * @return PersonalCredential
     */
    private PersonalCredential createPersonalCredential(final Person person, final Credential credential,
            final LocalDate expires) {

        log.info("Creating new personal credential record.");
        PersonalCredential personalCredential = new PersonalCredential();

        personalCredential.setPerson(person);
        personalCredential.setCredential(credential);
        personalCredential.setHasRecord(true);

        if (expires != null) {
            personalCredential.setExpires(expires);
        }

        personalCredentialService.save(personalCredential);

        String[] strings = { "Personal Credential", person.getName(), "-", credential.getFrameworkTitle(),
                " created." };
        log.info(String.join(" ", strings));

        return personalCredential;
    }

    /**
     * @param personalCredential
     * @param person
     * @param credential
     * @param expires
     * @return
     */
    private PersonalCredential updatePersonalCredential(PersonalCredential personalCredential, final Person person,
            final Credential credential, final LocalDate expires) {

        try {

            if (expires != null) {

                personalCredential.setExpires(expires);
                personalCredentialService.update(personalCredential);

                String[] strings = { "Personal Credential", person.getName(), "-", credential.getFrameworkTitle(),
                        " updated." };
                log.info(String.join(" ", strings));

            }

        } catch (RuntimeServiceException e) {
            throw e;
        }

        return personalCredential;
    }
}
