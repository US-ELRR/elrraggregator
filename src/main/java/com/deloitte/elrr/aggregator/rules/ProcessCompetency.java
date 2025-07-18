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
import com.deloitte.elrr.entity.Competency;
import com.deloitte.elrr.entity.Person;
import com.deloitte.elrr.entity.PersonalCompetency;
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
    private PersonSvc personService;

    private static final String COMPETENCY_MESSAGE = "Competency";

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
    public Person processRule(final Person person, final Statement statement) {

        Extensions extensions = null;

        log.info("Process competency.");

        // Get start date
        // Convert from ZonedDateTime to LocalDate
        LocalDateTime startDate = statement.getTimestamp().toLocalDateTime();

        // Get Activity
        Activity activity = (Activity) statement.getObject();

        LocalDateTime expires = null;

        // Get Extensions
        Context context = statement.getContext();

        if (context != null) {

            extensions = context.getExtensions();

            if (extensions != null) {

                String strExpires = (String) extensions.get(
                        ExtensionsConstants.CONTEXT_EXTENSIONS);

                if (strExpires != null) {

                    expires = LocalDateTime.parse(strExpires,
                            DateTimeFormatter.ISO_DATE_TIME);

                }

            }

        }

        // Process Competency
        Competency competency = processCompetency(activity, startDate, expires);

        // Process PersonalCompetency
        processPersonalCompetency(person, competency, expires);

        return person;
    }

    /**
     * @param activity
     * @param startDate
     * @param endDate
     * @return competency
     * @throws AggregatorException
     */
    private Competency processCompetency(final Activity activity,
            final LocalDateTime startDate, final LocalDateTime endDate)
            throws AggregatorException {

        Competency competency = null;

        // Get competency
        competency = competencyService.findByIdentifier(activity.getId()
                .toString());

        // If competency doesn't exist
        if (competency == null) {

            competency = createCompetency(activity, startDate, endDate);

        } else {

            log.info(COMPETENCY_MESSAGE + " " + activity.getId() + " exists.");
            competency = updateCompetency(competency, activity, endDate);

        }

        return competency;
    }

    /**
     * @param activity
     * @param startDate
     * @param endDate
     * @return competency
     * @throws AggregatorException
     */
    private Competency createCompetency(final Activity activity,
            final LocalDateTime startDate, final LocalDateTime endDate) {

        log.info("Creating new competency.");

        Competency competency = null;
        String activityName = "";
        String activityDescription = "";

        activityName = langMapUtil.getLangMapValue(activity.getDefinition()
                .getName());
        activityDescription = langMapUtil.getLangMapValue(activity
                .getDefinition().getDescription());

        competency = new Competency();
        competency.setIdentifier(activity.getId().toString());
        competency.setFrameworkTitle(activityName);
        competency.setFrameworkDescription(activityDescription);
        competency.setValidStartDate(startDate);
        competency.setValidEndDate(endDate);
        competencyService.save(competency);
        log.info(COMPETENCY_MESSAGE + " " + activity.getId() + " created.");

        return competency;
    }

    /**
     * @param competency
     * @param activity
     * @param endDate
     * @return competency
     * @throws AggregatorException
     */
    public Competency updateCompetency(Competency competency,
            final Activity activity, final LocalDateTime endDate) {

        log.info("Updating competency.");

        String activityName = "";
        String activityDescription = "";

        activityName = langMapUtil.getLangMapValue(activity.getDefinition()
                .getName());
        activityDescription = langMapUtil.getLangMapValue(activity
                .getDefinition().getDescription());

        competency.setFrameworkTitle(activityName);
        competency.setFrameworkDescription(activityDescription);
        competency.setValidEndDate(endDate);
        competencyService.update(competency);
        log.info(COMPETENCY_MESSAGE + " " + activity.getId() + " updated.");

        return competency;
    }

    /**
     * @param person
     * @param competency
     * @param expires
     * @return personalCompetency
     */
    private PersonalCompetency processPersonalCompetency(Person person,
            final Competency competency, final LocalDateTime expires) {

        PersonalCompetency personalCompetency = null;

        try {

            // Get PersonalCompetency
            personalCompetency = personalCompetencyService
                    .findByPersonIdAndCompetencyId(person.getId(), competency
                            .getId());

            // If PersonalCompetancy doesn't exist
            if (personalCompetency == null) {

                personalCompetency = createPersonalCompetency(person,
                        competency, expires);

                if (person.getCompetencies() == null) {
                    person.setCompetencies(new HashSet<PersonalCompetency>());
                }

                person.getCompetencies().add(personalCompetency);
                personService.save(person);

            } else {

                personalCompetency = updatePersonalCompetency(
                        personalCompetency, person, expires);
            }

        } catch (DateTimeParseException e) {
            log.error("Error invalid expires date.", e);
            throw new AggregatorException("Error invalid expires date.", e);

        }

        return personalCompetency;
    }

    /**
     * @param person
     * @param competency
     * @param expires
     * @return personalCompetency
     */
    private PersonalCompetency createPersonalCompetency(final Person person,
            final Competency competency, final LocalDateTime expires) {

        log.info("Creating new personal competency record.");
        PersonalCompetency personalCompetency = new PersonalCompetency();

        personalCompetency.setPerson(person);
        personalCompetency.setCompetency(competency);
        personalCompetency.setHasRecord(true);

        if (expires != null) {
            personalCompetency.setExpires(expires);
        }

        personalCompetencyService.save(personalCompetency);

        log.info("Personal Competency for " + person.getName() + " - "
                + personalCompetency.getCompetency().getFrameworkTitle()
                + " created.");

        return personalCompetency;
    }

    /**
     * @param personalCompetency
     * @param person
     * @param expires
     * @return personalCompetency
     */
    public PersonalCompetency updatePersonalCompetency(
            PersonalCompetency personalCompetency, final Person person,
            final LocalDateTime expires) {

        if (expires != null) {

            personalCompetency.setExpires(expires);
            personalCompetencyService.update(personalCompetency);

            log.info("Personal Competency for " + person.getName() + " - "
                    + personalCompetency.getCompetency().getFrameworkTitle()
                    + " updated.");

        }

        return personalCompetency;
    }
}
