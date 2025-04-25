package com.deloitte.elrr.aggregator.rules;

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
import com.deloitte.elrr.jpa.svc.PersonalCredentialSvc;
import com.yetanalytics.xapi.model.Activity;
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

		Credential credential = null;
		PersonalCredential personalCredential = null;

		try {

			log.info("Process credential.");

			// Get Activity
			Activity activity = (Activity) statement.getObject();

			// Process Credential
			credential = processCredential(activity);

			// Process PersonalCredential
			if (credential != null) {

				personalCredential = processPersonalCredential(activity, person, credential);

				if (person.getCredentials() == null) {
					person.setCredentials(new HashSet<PersonalCredential>());
				}

				person.getCredentials().add(personalCredential);
			}

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
			credential.setFrameworkTitle(activityName);
			credential.setFrameworkDescription(activityDescription);
			credentialService.save(credential);
			log.info("Credential " + activity.getId() + " created.");

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

			credential.setFrameworkTitle(activityName);
			credential.setFrameworkDescription(activityDescription);
			credentialService.update(credential);
			log.info("Credential " + activity.getId() + " updated.");

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
	 * @return PersonalCredential
	 */
	private PersonalCredential processPersonalCredential(final Activity activity, final Person person,
			final Credential credential) {

		// Get PersonalCredential
		PersonalCredential personalCredential = personalCredentialService.findByPersonIdAndCredentialId(person.getId(),
				credential.getId());

		// If PersonalCredential doesn't exist
		if (personalCredential == null) {

			createPersonalCredential(person, credential);

		} else {

			updatePersonalCredential(personalCredential, person, credential);
		}

		return personalCredential;
	}

	/**
	 * @param Person
	 * @param Credential
	 * @return PersonalCredential
	 */
	private PersonalCredential createPersonalCredential(final Person person, final Credential credential) {

		log.info("Creating new personal credential record.");
		PersonalCredential personalCredential = new PersonalCredential();

		personalCredential.setPerson(person);
		personalCredential.setCredential(credential);
		personalCredential.setHasRecord(true);
		personalCredentialService.save(personalCredential);

		log.info("Personal Credential for " + person.getName() + " - " + credential.getFrameworkTitle() + " created.");

		return personalCredential;
	}

	private PersonalCredential updatePersonalCredential(PersonalCredential personalCredential, final Person person,
			final Credential credential) {

		try {

			// TO DO
			personalCredentialService.update(personalCredential);

			log.info("Personal Credential for " + person.getName() + " - " + credential.getFrameworkTitle()
					+ " updated.");

		} catch (RuntimeServiceException e) {
			throw e;
		}

		return personalCredential;
	}
}
