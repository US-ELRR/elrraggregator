package com.deloitte.elrr.aggregator.rules;

import java.util.HashSet;

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

	public static final String COMPLETED = "COMPLETED";

	@Override
	public boolean fireRule(final Statement statement) {

		Activity obj = (Activity) statement.getObject();
		String objType = obj.getDefinition().getType();

		// Is Verb Id = achieved, object = activity and object type = competency
		return (statement.getVerb().getId().equalsIgnoreCase(VerbIdConstants.ACHIEVED_VERB_ID)
				&& statement.getObject() instanceof Activity && objType != null
				&& objType.equalsIgnoreCase(ObjectTypeConstants.COMPETENCY));

	}

	@Override
	@Transactional
	public Person processRule(final Person person, final Statement statement) {

		try {

			log.info("Process competency.");

			// Get Activity
			Activity activity = (Activity) statement.getObject();

			// Process Competency
			Competency competency = processCompetency(activity);

			// Process PersonalCompetency
			PersonalCompetency personalCompetency = processPersonalCompetency(activity, person, competency);

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

				log.info("Competency " + activity.getId() + " exists.");
				competency = updateCompetency(competency, activity);
			}

		} catch (AggregatorException | ClassCastException | NullPointerException e) {

			log.error("Error processing competency - " + e.getMessage());
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
			competency.setRecordStatus(COMPLETED);
			competency.setFrameworkTitle(activityName);
			competency.setFrameworkDescription(activityDescription);
			competencyService.save(competency);
			log.info("Competency " + activity.getId() + " created.");

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

			competency.setRecordStatus(COMPLETED);
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
	 * @return PersonalCompetency
	 */
	private PersonalCompetency processPersonalCompetency(final Activity activity, Person person,
			final Competency competency) {

		// Get PersonalCompetency
		PersonalCompetency personalCompetency = personalCompetencyService.findByPersonIdAndCompetencyId(person.getId(),
				competency.getId());

		// If PersonalCompetancy doesn't exist
		if (personalCompetency == null) {

			personalCompetency = createPersonalCompetency(person, competency);

			if (person.getCompetencies() == null) {
				person.setCompetencies(new HashSet<PersonalCompetency>());
			}

			person.getCompetencies().add(personalCompetency);
			personService.save(person);

		} else {

			personalCompetency = updatePersonalCompetency(personalCompetency, person, competency);
		}

		return personalCompetency;
	}

	/**
	 * @param Person
	 * @param Competency
	 * @return PersonalCompetency
	 */
	private PersonalCompetency createPersonalCompetency(final Person person, final Competency competency) {

		log.info("Creating new personal competency record.");
		PersonalCompetency personalCompetency = new PersonalCompetency();

		personalCompetency.setPerson(person);
		personalCompetency.setCompetency(competency);
		personalCompetency.setHasRecord(true);
		personalCompetencyService.save(personalCompetency);

		log.info("Personal Competency for " + person.getName() + " - " + competency.getFrameworkTitle() + " created.");

		return personalCompetency;
	}

	private PersonalCompetency updatePersonalCompetency(PersonalCompetency personalCompetency, final Person person,
			final Competency competency) {

		try {

			// TO DO
			// personalCompetencyService.update(personalCompetency);

			log.info("Personal Competency for " + person.getName() + " - " + competency.getFrameworkTitle()
					+ " updated.");

		} catch (RuntimeServiceException e) {
			throw e;
		}

		return personalCompetency;
	}
}
