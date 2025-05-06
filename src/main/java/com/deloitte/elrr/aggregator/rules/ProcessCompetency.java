package com.deloitte.elrr.aggregator.rules;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.deloitte.elrr.aggregator.utils.LangMapUtil;
import com.deloitte.elrr.elrraggregator.exception.AggregatorException;
import com.deloitte.elrr.entity.Competency;
import com.deloitte.elrr.entity.Person;
import com.deloitte.elrr.entity.PersonalCompetency;
import com.deloitte.elrr.exception.RuntimeServiceException;
import com.deloitte.elrr.jpa.svc.CompetencySvc;
import com.deloitte.elrr.jpa.svc.PersonSvc;
import com.deloitte.elrr.jpa.svc.PersonalCompetencySvc;
import com.yetanalytics.xapi.model.Activity;
import com.yetanalytics.xapi.model.Context;
import com.yetanalytics.xapi.model.Extensions;
import com.yetanalytics.xapi.model.Statement;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ProcessCompetency implements Rule {

    @Autowired
    private CompetencySvc competencyService;

    @Autowired
    private PersonalCompetencySvc personalCompetencyService;

    @Autowired
    private LangMapUtil langMapUtil;

    @Autowired
    PersonSvc personService;

    @Override
    public boolean fireRule(final Statement statement) {

        Activity obj = (Activity) statement.getObject();
        String objType = obj.getDefinition().getType();

        // Is Verb Id = achieved, object = activity and object type != credential
        return (statement.getVerb().getId().equalsIgnoreCase(VerbIdConstants.ACHIEVED_VERB_ID)
                && statement.getObject() instanceof Activity
                && !ObjectTypeConstants.CREDENTIAL.equalsIgnoreCase(objType));
    }

    @Override
    @Transactional
    public Person processRule(final Person person, final Statement statement) {

        Extensions extensions = null;

        try {

            log.info("Process competency.");

            // Get Activity
            Activity activity = (Activity) statement.getObject();

            // Get Extensions
            Context context = statement.getContext();

            if (context != null) {
                extensions = context.getExtensions();
            }

            // Process Competency
            Competency competency = processCompetency(activity);

            // Process PersonalCompetency
            PersonalCompetency personalCompetency = processPersonalCompetency(activity, person, competency, extensions);

        } catch (AggregatorException | ClassCastException | NullPointerException | RuntimeServiceException e) {
            throw e;
        }

        return person;
    }

    /**
     * @param statement
     * @return competency
     */
    private Competency processCompetency(final Activity activity) {

        Competency competency = null;
        PersonalCompetency personalCompetency = null;

        try {

            // Get competency
            competency = competencyService.findByIdentifier(activity.getId());

            // If competency doesn't exist
            if (competency == null) {

                competency = createCompetency(activity);

            } else {

                String[] strings = { "Competency", activity.getId(), "exists." };
                log.info(String.join(" ", strings));
                competency = updateCompetency(competency, activity);

            }

        } catch (AggregatorException | ClassCastException | NullPointerException e) {

            String[] strings = { "Error processing competency -", e.getMessage() };
            log.info(String.join(" ", strings));
            e.printStackTrace();
            throw e;
        }

        return competency;
    }

    /**
     * @param activity
     * @return competency
     */
    private Competency createCompetency(final Activity activity) {

        log.info("Creating new competency.");

        Competency competency = null;
        String activityName = "";
        String activityDescription = "";

        try {

            activityName = langMapUtil.getLangMapValue(activity.getDefinition().getName());
            activityDescription = langMapUtil.getLangMapValue(activity.getDefinition().getDescription());

            competency = new Competency();
            competency.setIdentifier(activity.getId());
            competency.setRecordStatus(StatusConstants.COMPLETED);
            competency.setFrameworkTitle(activityName);
            competency.setFrameworkDescription(activityDescription);
            competencyService.save(competency);
            String[] strings = { "Competency", activity.getId(), "created." };
            log.info(String.join(" ", strings));

        } catch (AggregatorException | ClassCastException | NullPointerException e) {
            log.error(e.getMessage());
            e.printStackTrace();
            throw e;
        }

        return competency;
    }

    /**
     * @param activity
     * @return competency
     */
    private Competency updateCompetency(Competency competency, final Activity activity) {

        log.info("Updating competency.");

        String activityName = "";
        String activityDescription = "";

        try {

            activityName = langMapUtil.getLangMapValue(activity.getDefinition().getName());
            activityDescription = langMapUtil.getLangMapValue(activity.getDefinition().getDescription());

            competency.setRecordStatus(StatusConstants.COMPLETED);
            competency.setFrameworkTitle(activityName);
            competency.setFrameworkDescription(activityDescription);
            competencyService.update(competency);
            log.info("Competency " + activity.getId() + " updated.");

        } catch (AggregatorException | ClassCastException | NullPointerException | RuntimeServiceException e) {
            log.error(e.getMessage());
            e.printStackTrace();
            throw e;
        }

        return competency;
    }

    /**
     * @param Activity
     * @param Person
     * @param Competency
     * @param Extensions
     * @return PersonalCompetency
     */
    private PersonalCompetency processPersonalCompetency(final Activity activity, Person person,
            final Competency competency, final Extensions extensions) {

        LocalDate expires = null;

        // Get PersonalCompetency
        PersonalCompetency personalCompetency = personalCompetencyService.findByPersonIdAndCompetencyId(person.getId(),
                competency.getId());

        if (extensions != null) {

            Map extensionMap = extensions.getMap();
            String strExpires = (String) extensionMap.get(ExtensionsConstants.CONTEXT_EXTENSIONS);

            if (strExpires != null) {
                expires = LocalDate.parse(strExpires);
            }

        }

        // If PersonalCompetancy doesn't exist
        if (personalCompetency == null) {

            personalCompetency = createPersonalCompetency(person, competency, expires);

            if (person.getCompetencies() == null) {
                person.setCompetencies(new HashSet<PersonalCompetency>());
            }

            person.getCompetencies().add(personalCompetency);
            personService.save(person);

        } else {

            personalCompetency = updatePersonalCompetency(personalCompetency, person, competency, expires);
        }

        return personalCompetency;
    }

    /**
     * @param Person
     * @param Competency
     * @param expires
     * @return PersonalCompetency
     */
    private PersonalCompetency createPersonalCompetency(final Person person, final Competency competency,
            final LocalDate expires) {

        log.info("Creating new personal competency record.");
        PersonalCompetency personalCompetency = new PersonalCompetency();

        personalCompetency.setPerson(person);
        personalCompetency.setCompetency(competency);
        personalCompetency.setHasRecord(true);

        if (expires != null) {
            personalCompetency.setExpires(expires);
        }

        personalCompetencyService.save(personalCompetency);

        log.info("Personal Competency for " + person.getName() + " - " + competency.getFrameworkTitle() + " created.");

        return personalCompetency;
    }

    /**
     * @param personalCompetency
     * @param person
     * @param competency
     * @param expires
     * @return
     */
    private PersonalCompetency updatePersonalCompetency(PersonalCompetency personalCompetency, final Person person,
            final Competency competency, final LocalDate expires) {

        try {

            if (expires != null) {

                personalCompetency.setExpires(expires);
                personalCompetencyService.update(personalCompetency);

                String[] strings = { "Personal Credential", person.getName(), "-",
                        personalCompetency.getCompetency().getFrameworkTitle(), " updated." };
                log.info(String.join(" ", strings));

            }

        } catch (RuntimeServiceException e) {
            throw e;
        }

        return personalCompetency;
    }
}
