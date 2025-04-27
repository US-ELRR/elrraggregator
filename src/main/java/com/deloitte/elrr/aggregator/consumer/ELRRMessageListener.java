package com.deloitte.elrr.aggregator.consumer;

import java.util.Arrays;
import java.util.List;

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
import com.deloitte.elrr.aggregator.rules.Rule;
import com.deloitte.elrr.elrraggregator.exception.AggregatorException;
import com.deloitte.elrr.elrraggregator.exception.PersonNotFoundException;
import com.deloitte.elrr.entity.Person;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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

			// If valid message process otherwise send to dead letter queue
			if (InputSanitizer.isValidInput(message)) {
				processMessage(message);
			} else {
				log.error("Invalid message did not pass whitelist check - " + message);
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

			log.info("Process verb " + statement.getVerb().getId());

			// Process Person
			person = processPerson.processPerson(statement);

			List<Rule> classList = Arrays.asList(processCompetency, processCompleted, processCredential, processFailed,
					processInitialized, processPassed);

			for (Rule rule : classList) {

				log.info("Process verb " + statement.getVerb().getId());

				if (rule.fireRule(statement)) {

					try {

						// Process Rule
						rule.processRule(person, statement);

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
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
