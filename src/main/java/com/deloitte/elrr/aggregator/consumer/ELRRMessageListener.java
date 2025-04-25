package com.deloitte.elrr.aggregator.consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.deloitte.elrr.aggregator.InputSanitizer;
import com.deloitte.elrr.aggregator.dto.MessageVO;
import com.deloitte.elrr.aggregator.rules.ProcessCompetency;
import com.deloitte.elrr.aggregator.rules.ProcessCompleted;
import com.deloitte.elrr.aggregator.rules.ProcessCredential;
import com.deloitte.elrr.aggregator.rules.ProcessFailed;
import com.deloitte.elrr.aggregator.rules.ProcessInitialized;
import com.deloitte.elrr.aggregator.rules.ProcessPassed;
import com.deloitte.elrr.elrraggregator.exception.AggregatorException;
import com.deloitte.elrr.elrraggregator.exception.PersonNotFoundException;
import com.deloitte.elrr.entity.Person;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yetanalytics.xapi.model.Activity;
import com.yetanalytics.xapi.model.Statement;
import com.yetanalytics.xapi.util.Mapper;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ELRRMessageListener {

	@Autowired
	private ProcessCompleted processCompleted;

	@Autowired
	private ProcessCompetency processCompetency;

	@Autowired
	private ProcessCredential processCredential;

	@Autowired
	private ProcessPerson processPerson;

	@Autowired
	private ProcessPassed processPassed;

	@Autowired
	private ProcessFailed processFailed;

	@Autowired
	private ProcessInitialized processInitialized;

	@Autowired
	KafkaTemplate<?, String> kafkaTemplate;

	@Value("${kafka.dead.letter.topic}")
	private String deadLetterTopic;

	/**
	 * @param message
	 */
	@Transactional
	@KafkaListener(topics = "${kafka.topic}")
	public void listen(final String message) {

		log.info("\n\n ===============Received Messasge in group - group-id=============== \n" + message);

		try {

			if (InputSanitizer.isValidInput(message)) {
				processMessage(message);
			} else {
				log.error("Invalid message did not pass whitelist check - " + message);
				// Send to dead letter queue
				kafkaTemplate.send(deadLetterTopic, message);
			}

		} catch (AggregatorException e) {
			// Send to dead letter queue
			kafkaTemplate.send(deadLetterTopic, message);
			throw e;
		}
	}

	/**
	 * @param statement
	 * @throws AggregatorException
	 */
	@Transactional
	private void processMessage(final String payload) {

		log.info(" \n\n ===============Process Kafka message===============");

		Statement statement = null;
		Person person = null;

		try {

			// Get Statement
			statement = getStatement(payload);

			Activity obj = (Activity) statement.getObject();
			String objType = obj.getDefinition().getType();

			log.info("Process verb " + statement.getVerb().getId());

			// Process Person
			person = processPerson.processPerson(statement);

			// If completed
			if (processCompleted.fireRule(statement)) {

				// Process rule
				processCompleted.processRule(person, statement);

				// If achieved competency
			} else if (processCompetency.fireRule(statement)) {

				// Process rule
				processCompetency.processRule(person, statement);

				// If achieved credential
			} else if (processCredential.fireRule(statement)) {

				// Process rule
				processCredential.processRule(person, statement);

				// If passed
			} else if (processPassed.fireRule(statement)) {

				// Process rule
				processPassed.processRule(person, statement);

				// If failed
			} else if (processFailed.fireRule(statement)) {

				// Process rule
				processFailed.processRule(person, statement);

				// If initialized
			} else if (processInitialized.fireRule(statement)) {

				// Process rule
				processInitialized.processRule(person, statement);

			} else {

				log.info("Verb " + statement.getVerb().getId() + " Object Type " + objType + " is not recognized.");
			}

		} catch (AggregatorException | ClassCastException | PersonNotFoundException e) {
			log.error("Error processing Kafka message - " + e.getMessage());
			e.printStackTrace();
			throw e;

		} catch (JsonProcessingException e) {
			log.error("Error processing Kafka message - " + e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * @param payload
	 * @return Statement
	 */
	public Statement getStatement(final String payload) throws JsonProcessingException {

		Statement statement = null;
		ObjectMapper mapper = Mapper.getMapper();
		MessageVO messageVo;

		try {

			messageVo = mapper.readValue(payload, MessageVO.class);
			statement = messageVo.getStatement();

		} catch (JsonProcessingException e) {
			log.error("Error getting statement - " + e.getMessage());
			e.printStackTrace();
			throw e;
		}

		return statement;
	}
}
