package com.deloitte.elrr.aggregator.consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.deloitte.elrr.InputSanatizer;
import com.deloitte.elrr.aggregator.drools.DroolsProcessStatementService;
import com.deloitte.elrr.aggregator.dto.MessageVO;
import com.deloitte.elrr.aggregator.rules.Rule;
import com.deloitte.elrr.elrraggregator.exception.ActivityNotFoundException;
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

  // Import ProcessCompleted implementation of Rule interface
  @Autowired private Rule processCompleted;

  @Autowired private ProcessPerson processPerson;

  @Autowired private DroolsProcessStatementService droolsProcessStatementService;

  @Autowired KafkaTemplate<?, String> kafkaTemplate;

  @Value("${kafka.dead.letter.topic}")
  private String deadLetterTopic;

  @Value("${lang.codes}")
  private String[] namLang = new String[10];

  /**
   * @param message
   */
  @KafkaListener(topics = "${kafka.topic}")
  public void listen(final String message) {

    log.info("\n\n ===> Received Messasge in group - group-id: " + message);

    try {

      if (InputSanatizer.isValidInput(message)) {
        processMessage(message);
        // processMessageFromRule(message);
      } else {
        log.error("Invalid message did not pass whitelist check - " + message);
        // Send to dead letter queue
        kafkaTemplate.send(deadLetterTopic, message);
      }

    } catch (Exception e) {
      // Send to dead letter queue
      kafkaTemplate.send(deadLetterTopic, message);
    }
  }

  /**
   * @param statement
   * @throws ActivityNotFoundException, PersonNotFoundException, JsonProcessingException, Exception
   */
  private void processMessage(final String payload)
      throws ActivityNotFoundException,
          PersonNotFoundException,
          JsonProcessingException,
          Exception {

    log.info("Process kafka message.");

    Statement statement = null;
    Person person = null;

    // Get Statement
    statement = getStatement(payload);

    // Process completed
    boolean fireRule = processCompleted.fireRule(statement);

    if (fireRule) {

      log.info("Process verb " + statement.getVerb().getId());

      // Process Person
      person = processPerson.processPerson(statement);

      // If person exists
      if (person != null) {
        processCompleted.processRule(person, statement);
      } else {
        log.error("Person not found.");
        throw new PersonNotFoundException("Person not found.");
      }

    } else {
      log.info("Verb " + statement.getVerb().getId() + " is not recognized.");
    }
  }

  /**
   * @param statement
   */
  private void processMessageFromRule(final String payload)
      throws ActivityNotFoundException,
          PersonNotFoundException,
          JsonProcessingException,
          Exception {

    log.info("Process kafka message with Drools.");

    Statement statement = null;
    Person person = null;

    // Get Statement
    statement = getStatement(payload);

    // Process completed
    boolean fireRule = processCompleted.fireRule(statement);

    if (fireRule) {

      log.info("Process verb " + statement.getVerb().getId());

      // Process Person
      person = processPerson.processPerson(statement);

      // If person exists
      if (person != null) {
        droolsProcessStatementService.processStatement(person, statement);
      } else {
        log.error("Person not found.");
        throw new PersonNotFoundException("Person not found.");
      }

    } else {
      log.info("Verb " + statement.getVerb().getId() + " is not recognized.");
    }
  }

  /**
   * @param payload
   * @return Statement
   */
  public Statement getStatement(String payload) throws JsonProcessingException {

    Statement statement = null;
    ObjectMapper mapper = Mapper.getMapper();
    MessageVO messageVo;

    try {

      messageVo = mapper.readValue(payload, MessageVO.class);
      statement = messageVo.getStatement();

    } catch (JsonProcessingException e) {

      log.error("Error getting statement - " + e.getMessage());
      e.printStackTrace();
    }

    return statement;
  }
}
